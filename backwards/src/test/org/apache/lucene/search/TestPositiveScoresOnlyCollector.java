begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestPositiveScoresOnlyCollector
specifier|public
class|class
name|TestPositiveScoresOnlyCollector
extends|extends
name|LuceneTestCase
block|{
DECL|class|SimpleScorer
specifier|private
specifier|static
specifier|final
class|class
name|SimpleScorer
extends|extends
name|Scorer
block|{
DECL|field|idx
specifier|private
name|int
name|idx
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SimpleScorer
specifier|public
name|SimpleScorer
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|score
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|idx
operator|==
name|scores
operator|.
name|length
condition|?
name|Float
operator|.
name|NaN
else|:
name|scores
index|[
name|idx
index|]
return|;
block|}
DECL|method|docID
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|idx
return|;
block|}
DECL|method|nextDoc
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|++
name|idx
operator|!=
name|scores
operator|.
name|length
condition|?
name|idx
else|:
name|NO_MORE_DOCS
return|;
block|}
DECL|method|advance
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|idx
operator|=
name|target
expr_stmt|;
return|return
name|idx
operator|<
name|scores
operator|.
name|length
condition|?
name|idx
else|:
name|NO_MORE_DOCS
return|;
block|}
block|}
comment|// The scores must have positive as well as negative values
DECL|field|scores
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[]
block|{
literal|0.7767749f
block|,
operator|-
literal|1.7839992f
block|,
literal|8.9925785f
block|,
literal|7.9608946f
block|,
operator|-
literal|0.07948637f
block|,
literal|2.6356435f
block|,
literal|7.4950366f
block|,
literal|7.1490803f
block|,
operator|-
literal|8.108544f
block|,
literal|4.961808f
block|,
literal|2.2423935f
block|,
operator|-
literal|7.285586f
block|,
literal|4.6699767f
block|}
decl_stmt|;
DECL|method|testNegativeScores
specifier|public
name|void
name|testNegativeScores
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The Top*Collectors previously filtered out documents with<= scores. This
comment|// behavior has changed. This test checks that if PositiveOnlyScoresFilter
comment|// wraps one of these collectors, documents with<= 0 scores are indeed
comment|// filtered.
name|int
name|numPositiveScores
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scores
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|scores
index|[
name|i
index|]
operator|>
literal|0
condition|)
block|{
operator|++
name|numPositiveScores
expr_stmt|;
block|}
block|}
name|Scorer
name|s
init|=
operator|new
name|SimpleScorer
argument_list|()
decl_stmt|;
name|TopDocsCollector
name|tdc
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|scores
operator|.
name|length
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Collector
name|c
init|=
operator|new
name|PositiveScoresOnlyCollector
argument_list|(
name|tdc
argument_list|)
decl_stmt|;
name|c
operator|.
name|setScorer
argument_list|(
name|s
argument_list|)
expr_stmt|;
while|while
condition|(
name|s
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|c
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|numPositiveScores
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"only positive scores should return: "
operator|+
name|sd
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|sd
index|[
name|i
index|]
operator|.
name|score
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

