begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|NumericDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DiversifiedTopDocsCollector
operator|.
name|ScoreDocKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * A {@link TopDocsCollector} that controls diversity in results by ensuring no  * more than maxHitsPerKey results from a common source are collected in the  * final results.  *   * An example application might be a product search in a marketplace where no  * more than 3 results per retailer are permitted in search results.  *   *<p>  * To compare behaviour with other forms of collector, a useful analogy might be  * the problem of making a compilation album of 1967's top hit records:  *<ol>  *<li>A vanilla query's results might look like a "Best of the Beatles" album -  * high quality but not much diversity</li>  *<li>A GroupingSearch would produce the equivalent of "The 10 top-selling  * artists of 1967 - some killer and quite a lot of filler"</li>  *<li>A "diversified" query would be the top 20 hit records of that year - with  * a max of 3 Beatles hits in order to maintain diversity</li>  *</ol>  * This collector improves on the "GroupingSearch" type queries by  *<ul>  *<li>Working in one pass over the data</li>  *<li>Not requiring the client to guess how many groups are required</li>  *<li>Removing low-scoring "filler" which sits at the end of each group's hits</li>  *</ul>  *   * This is an abstract class and subclasses have to provide a source of keys for  * documents which is then used to help identify duplicate sources.  *   * @lucene.experimental  *   */
end_comment

begin_class
DECL|class|DiversifiedTopDocsCollector
specifier|public
specifier|abstract
class|class
name|DiversifiedTopDocsCollector
extends|extends
name|TopDocsCollector
argument_list|<
name|ScoreDocKey
argument_list|>
block|{
DECL|field|spare
name|ScoreDocKey
name|spare
decl_stmt|;
DECL|field|globalQueue
specifier|private
name|ScoreDocKeyQueue
name|globalQueue
decl_stmt|;
DECL|field|numHits
specifier|private
name|int
name|numHits
decl_stmt|;
DECL|field|perKeyQueues
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|ScoreDocKeyQueue
argument_list|>
name|perKeyQueues
decl_stmt|;
DECL|field|maxNumPerKey
specifier|protected
name|int
name|maxNumPerKey
decl_stmt|;
DECL|field|sparePerKeyQueues
specifier|private
name|Stack
argument_list|<
name|ScoreDocKeyQueue
argument_list|>
name|sparePerKeyQueues
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|DiversifiedTopDocsCollector
specifier|public
name|DiversifiedTopDocsCollector
parameter_list|(
name|int
name|numHits
parameter_list|,
name|int
name|maxHitsPerKey
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|ScoreDocKeyQueue
argument_list|(
name|numHits
argument_list|)
argument_list|)
expr_stmt|;
comment|// Need to access pq.lessThan() which is protected so have to cast here...
name|this
operator|.
name|globalQueue
operator|=
operator|(
name|ScoreDocKeyQueue
operator|)
name|pq
expr_stmt|;
name|perKeyQueues
operator|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|ScoreDocKeyQueue
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|numHits
operator|=
name|numHits
expr_stmt|;
name|this
operator|.
name|maxNumPerKey
operator|=
name|maxHitsPerKey
expr_stmt|;
block|}
comment|/**    * Get a source of values used for grouping keys    */
DECL|method|getKeys
specifier|protected
specifier|abstract
name|NumericDocValues
name|getKeys
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|newTopDocs
specifier|protected
name|TopDocs
name|newTopDocs
parameter_list|(
name|ScoreDoc
index|[]
name|results
parameter_list|,
name|int
name|start
parameter_list|)
block|{
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY_TOPDOCS
return|;
block|}
comment|// We need to compute maxScore in order to set it in TopDocs. If start == 0,
comment|// it means the largest element is already in results, use its score as
comment|// maxScore. Otherwise pop everything else, until the largest element is
comment|// extracted and use its score as maxScore.
name|float
name|maxScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
if|if
condition|(
name|start
operator|==
literal|0
condition|)
block|{
name|maxScore
operator|=
name|results
index|[
literal|0
index|]
operator|.
name|score
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
name|globalQueue
operator|.
name|size
argument_list|()
init|;
name|i
operator|>
literal|1
condition|;
name|i
operator|--
control|)
block|{
name|globalQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
name|maxScore
operator|=
name|globalQueue
operator|.
name|pop
argument_list|()
operator|.
name|score
expr_stmt|;
block|}
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|results
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
DECL|method|insert
specifier|protected
name|ScoreDocKey
name|insert
parameter_list|(
name|ScoreDocKey
name|addition
parameter_list|,
name|int
name|docBase
parameter_list|,
name|NumericDocValues
name|keys
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|globalQueue
operator|.
name|size
argument_list|()
operator|>=
name|numHits
operator|)
operator|&&
operator|(
name|globalQueue
operator|.
name|lessThan
argument_list|(
name|addition
argument_list|,
name|globalQueue
operator|.
name|top
argument_list|()
argument_list|)
operator|)
condition|)
block|{
comment|// Queue is full and proposed addition is not a globally
comment|// competitive score
return|return
name|addition
return|;
block|}
comment|// The addition stands a chance of being entered - check the
comment|// key-specific restrictions.
comment|// We delay fetching the key until we are certain the score is globally
comment|// competitive. We need to adjust the ScoreDoc's global doc value to be
comment|// a leaf reader value when looking up keys
name|int
name|leafDocID
init|=
name|addition
operator|.
name|doc
operator|-
name|docBase
decl_stmt|;
name|long
name|value
decl_stmt|;
if|if
condition|(
name|keys
operator|.
name|advanceExact
argument_list|(
name|leafDocID
argument_list|)
condition|)
block|{
name|value
operator|=
name|keys
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|0
expr_stmt|;
block|}
name|addition
operator|.
name|key
operator|=
name|value
expr_stmt|;
comment|// For this to work the choice of key class needs to implement
comment|// hashcode and equals.
name|ScoreDocKeyQueue
name|thisKeyQ
init|=
name|perKeyQueues
operator|.
name|get
argument_list|(
name|addition
operator|.
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisKeyQ
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|sparePerKeyQueues
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|thisKeyQ
operator|=
operator|new
name|ScoreDocKeyQueue
argument_list|(
name|maxNumPerKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|thisKeyQ
operator|=
name|sparePerKeyQueues
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
name|perKeyQueues
operator|.
name|put
argument_list|(
name|addition
operator|.
name|key
argument_list|,
name|thisKeyQ
argument_list|)
expr_stmt|;
block|}
name|ScoreDocKey
name|perKeyOverflow
init|=
name|thisKeyQ
operator|.
name|insertWithOverflow
argument_list|(
name|addition
argument_list|)
decl_stmt|;
if|if
condition|(
name|perKeyOverflow
operator|==
name|addition
condition|)
block|{
comment|// This key group has reached capacity and our proposed addition
comment|// was not competitive in the group - do not insert into the
comment|// main PQ or the key will be overly-populated in final results.
return|return
name|addition
return|;
block|}
if|if
condition|(
name|perKeyOverflow
operator|==
literal|null
condition|)
block|{
comment|// This proposed addition is also locally competitive within the
comment|// key group - make a global entry and return
name|ScoreDocKey
name|globalOverflow
init|=
name|globalQueue
operator|.
name|insertWithOverflow
argument_list|(
name|addition
argument_list|)
decl_stmt|;
name|perKeyGroupRemove
argument_list|(
name|globalOverflow
argument_list|)
expr_stmt|;
return|return
name|globalOverflow
return|;
block|}
comment|// For the given key, we have reached max capacity but the new addition
comment|// is better than a prior entry that still exists in the global results
comment|// - request the weaker-scoring entry to be removed from the global
comment|// queue.
name|globalQueue
operator|.
name|remove
argument_list|(
name|perKeyOverflow
argument_list|)
expr_stmt|;
comment|// Add the locally-competitive addition into the globally queue
name|globalQueue
operator|.
name|add
argument_list|(
name|addition
argument_list|)
expr_stmt|;
return|return
name|perKeyOverflow
return|;
block|}
DECL|method|perKeyGroupRemove
specifier|private
name|void
name|perKeyGroupRemove
parameter_list|(
name|ScoreDocKey
name|globalOverflow
parameter_list|)
block|{
if|if
condition|(
name|globalOverflow
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ScoreDocKeyQueue
name|q
init|=
name|perKeyQueues
operator|.
name|get
argument_list|(
name|globalOverflow
operator|.
name|key
argument_list|)
decl_stmt|;
name|ScoreDocKey
name|perKeyLowest
init|=
name|q
operator|.
name|pop
argument_list|()
decl_stmt|;
comment|// The least globally-competitive item should also always be the least
comment|// key-local item
assert|assert
operator|(
name|globalOverflow
operator|==
name|perKeyLowest
operator|)
assert|;
if|if
condition|(
name|q
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|perKeyQueues
operator|.
name|remove
argument_list|(
name|globalOverflow
operator|.
name|key
argument_list|)
expr_stmt|;
name|sparePerKeyQueues
operator|.
name|push
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|base
init|=
name|context
operator|.
name|docBase
decl_stmt|;
specifier|final
name|NumericDocValues
name|keySource
init|=
name|getKeys
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|LeafCollector
argument_list|()
block|{
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
comment|// This collector cannot handle NaN
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
name|totalHits
operator|++
expr_stmt|;
name|doc
operator|+=
name|base
expr_stmt|;
if|if
condition|(
name|spare
operator|==
literal|null
condition|)
block|{
name|spare
operator|=
operator|new
name|ScoreDocKey
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|spare
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|spare
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
name|spare
operator|=
name|insert
argument_list|(
name|spare
argument_list|,
name|base
argument_list|,
name|keySource
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|class|ScoreDocKeyQueue
specifier|static
class|class
name|ScoreDocKeyQueue
extends|extends
name|PriorityQueue
argument_list|<
name|ScoreDocKey
argument_list|>
block|{
DECL|method|ScoreDocKeyQueue
name|ScoreDocKeyQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|ScoreDocKey
name|hitA
parameter_list|,
name|ScoreDocKey
name|hitB
parameter_list|)
block|{
if|if
condition|(
name|hitA
operator|.
name|score
operator|==
name|hitB
operator|.
name|score
condition|)
return|return
name|hitA
operator|.
name|doc
operator|>
name|hitB
operator|.
name|doc
return|;
else|else
return|return
name|hitA
operator|.
name|score
operator|<
name|hitB
operator|.
name|score
return|;
block|}
block|}
comment|//
comment|/**    * An extension to ScoreDoc that includes a key used for grouping purposes    */
DECL|class|ScoreDocKey
specifier|static
specifier|public
class|class
name|ScoreDocKey
extends|extends
name|ScoreDoc
block|{
DECL|field|key
name|Long
name|key
decl_stmt|;
DECL|method|ScoreDocKey
specifier|protected
name|ScoreDocKey
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|super
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
DECL|method|getKey
specifier|public
name|Long
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"key:"
operator|+
name|key
operator|+
literal|" doc="
operator|+
name|doc
operator|+
literal|" s="
operator|+
name|score
return|;
block|}
block|}
block|}
end_class

end_unit

