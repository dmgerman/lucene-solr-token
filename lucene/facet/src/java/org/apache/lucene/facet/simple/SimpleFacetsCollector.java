begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|facet
operator|.
name|taxonomy
operator|.
name|FacetLabel
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|AtomicReaderContext
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
name|IndexReader
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
name|Collector
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
name|Scorer
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
name|ArrayUtil
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
name|FixedBitSet
import|;
end_import

begin_comment
comment|// nocommit javadocs
end_comment

begin_class
DECL|class|SimpleFacetsCollector
specifier|public
specifier|final
class|class
name|SimpleFacetsCollector
extends|extends
name|Collector
block|{
DECL|field|context
specifier|private
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|bits
specifier|private
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|totalHits
specifier|private
name|int
name|totalHits
decl_stmt|;
DECL|field|scores
specifier|private
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|keepScores
specifier|private
specifier|final
name|boolean
name|keepScores
decl_stmt|;
DECL|field|matchingDocs
specifier|private
specifier|final
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
init|=
operator|new
name|ArrayList
argument_list|<
name|MatchingDocs
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Holds the documents that were matched in the {@link AtomicReaderContext}.    * If scores were required, then {@code scores} is not null.    */
DECL|class|MatchingDocs
specifier|public
specifier|final
specifier|static
class|class
name|MatchingDocs
block|{
DECL|field|context
specifier|public
specifier|final
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|bits
specifier|public
specifier|final
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|scores
specifier|public
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|totalHits
specifier|public
specifier|final
name|int
name|totalHits
decl_stmt|;
DECL|method|MatchingDocs
specifier|public
name|MatchingDocs
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|FixedBitSet
name|bits
parameter_list|,
name|int
name|totalHits
parameter_list|,
name|float
index|[]
name|scores
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
block|}
block|}
DECL|method|SimpleFacetsCollector
specifier|public
name|SimpleFacetsCollector
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleFacetsCollector
specifier|public
name|SimpleFacetsCollector
parameter_list|(
name|boolean
name|keepScores
parameter_list|)
block|{
name|this
operator|.
name|keepScores
operator|=
name|keepScores
expr_stmt|;
block|}
DECL|method|getKeepScores
specifier|public
name|boolean
name|getKeepScores
parameter_list|()
block|{
return|return
name|keepScores
return|;
block|}
comment|/**    * Returns the documents matched by the query, one {@link MatchingDocs} per    * visited segment.    */
DECL|method|getMatchingDocs
specifier|public
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|getMatchingDocs
parameter_list|()
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
literal|null
expr_stmt|;
name|scores
operator|=
literal|null
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|matchingDocs
return|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
specifier|final
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
comment|// nocommit why not true?
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|keepScores
condition|)
block|{
if|if
condition|(
name|totalHits
operator|>=
name|scores
operator|.
name|length
condition|)
block|{
name|float
index|[]
name|newScores
init|=
operator|new
name|float
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|totalHits
operator|+
literal|1
argument_list|,
literal|4
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scores
argument_list|,
literal|0
argument_list|,
name|newScores
argument_list|,
literal|0
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
name|scores
operator|=
name|newScores
expr_stmt|;
block|}
name|scores
index|[
name|totalHits
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
name|totalHits
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
specifier|final
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
DECL|method|setNextReader
specifier|public
specifier|final
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|totalHits
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|keepScores
condition|)
block|{
name|scores
operator|=
operator|new
name|float
index|[
literal|64
index|]
expr_stmt|;
comment|// some initial size
block|}
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
end_class

end_unit

