begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ScoreDoc
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
name|TopDocs
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
name|suggest
operator|.
name|Lookup
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.lucene.search.TopDocs} wrapper with  * an additional CharSequence key per {@link org.apache.lucene.search.ScoreDoc}  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TopSuggestDocs
specifier|public
class|class
name|TopSuggestDocs
extends|extends
name|TopDocs
block|{
comment|/**    * Singleton for empty {@link TopSuggestDocs}    */
DECL|field|EMPTY
specifier|public
specifier|final
specifier|static
name|TopSuggestDocs
name|EMPTY
init|=
operator|new
name|TopSuggestDocs
argument_list|(
literal|0
argument_list|,
operator|new
name|SuggestScoreDoc
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**    * {@link org.apache.lucene.search.ScoreDoc} with an    * additional CharSequence key    */
DECL|class|SuggestScoreDoc
specifier|public
specifier|static
class|class
name|SuggestScoreDoc
extends|extends
name|ScoreDoc
implements|implements
name|Comparable
argument_list|<
name|SuggestScoreDoc
argument_list|>
block|{
comment|/**      * Matched completion key      */
DECL|field|key
specifier|public
specifier|final
name|CharSequence
name|key
decl_stmt|;
comment|/**      * Context for the completion      */
DECL|field|context
specifier|public
specifier|final
name|CharSequence
name|context
decl_stmt|;
comment|/**      * Creates a SuggestScoreDoc instance      *      * @param doc   document id (hit)      * @param key   matched completion      * @param score weight of the matched completion      */
DECL|method|SuggestScoreDoc
specifier|public
name|SuggestScoreDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|CharSequence
name|context
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
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|SuggestScoreDoc
name|o
parameter_list|)
block|{
return|return
name|Lookup
operator|.
name|CHARSEQUENCE_COMPARATOR
operator|.
name|compare
argument_list|(
name|key
argument_list|,
name|o
operator|.
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * {@link org.apache.lucene.search.TopDocs} wrapper with    * {@link TopSuggestDocs.SuggestScoreDoc}    * instead of {@link org.apache.lucene.search.ScoreDoc}    */
DECL|method|TopSuggestDocs
specifier|public
name|TopSuggestDocs
parameter_list|(
name|int
name|totalHits
parameter_list|,
name|SuggestScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|super
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns {@link TopSuggestDocs.SuggestScoreDoc}s    * for this instance    */
DECL|method|scoreLookupDocs
specifier|public
name|SuggestScoreDoc
index|[]
name|scoreLookupDocs
parameter_list|()
block|{
return|return
operator|(
name|SuggestScoreDoc
index|[]
operator|)
name|scoreDocs
return|;
block|}
comment|/**    * Returns a new TopSuggestDocs, containing topN results across    * the provided TopSuggestDocs, sorting by score. Each {@link TopSuggestDocs}    * instance must be sorted.    * Analogous to {@link org.apache.lucene.search.TopDocs#merge(int, org.apache.lucene.search.TopDocs[])}    * for {@link TopSuggestDocs}    *    * NOTE: assumes every<code>shardHit</code> is already sorted by score    */
DECL|method|merge
specifier|public
specifier|static
name|TopSuggestDocs
name|merge
parameter_list|(
name|int
name|topN
parameter_list|,
name|TopSuggestDocs
index|[]
name|shardHits
parameter_list|)
block|{
name|SuggestScoreDocPriorityQueue
name|priorityQueue
init|=
operator|new
name|SuggestScoreDocPriorityQueue
argument_list|(
name|topN
argument_list|)
decl_stmt|;
for|for
control|(
name|TopSuggestDocs
name|shardHit
range|:
name|shardHits
control|)
block|{
for|for
control|(
name|SuggestScoreDoc
name|scoreDoc
range|:
name|shardHit
operator|.
name|scoreLookupDocs
argument_list|()
control|)
block|{
if|if
condition|(
name|scoreDoc
operator|==
name|priorityQueue
operator|.
name|insertWithOverflow
argument_list|(
name|scoreDoc
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
name|SuggestScoreDoc
index|[]
name|topNResults
init|=
name|priorityQueue
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|topNResults
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|TopSuggestDocs
argument_list|(
name|topNResults
operator|.
name|length
argument_list|,
name|topNResults
argument_list|,
name|topNResults
index|[
literal|0
index|]
operator|.
name|score
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|TopSuggestDocs
operator|.
name|EMPTY
return|;
block|}
block|}
block|}
end_class

end_unit

