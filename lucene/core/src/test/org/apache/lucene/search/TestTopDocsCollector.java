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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|index
operator|.
name|RandomIndexWriter
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
name|store
operator|.
name|Directory
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
DECL|class|TestTopDocsCollector
specifier|public
class|class
name|TestTopDocsCollector
extends|extends
name|LuceneTestCase
block|{
DECL|class|MyTopsDocCollector
specifier|private
specifier|static
specifier|final
class|class
name|MyTopsDocCollector
extends|extends
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
block|{
DECL|field|idx
specifier|private
name|int
name|idx
init|=
literal|0
decl_stmt|;
DECL|field|base
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
DECL|method|MyTopsDocCollector
specifier|public
name|MyTopsDocCollector
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|HitQueue
argument_list|(
name|size
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
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
name|pq
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
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
name|maxScore
operator|=
name|pq
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
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
operator|++
name|totalHits
expr_stmt|;
name|pq
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|doc
operator|+
name|base
argument_list|,
name|scores
index|[
name|idx
operator|++
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
comment|// Don't do anything. Assign scores in random
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// Scores array to be used by MyTopDocsCollector. If it is changed, MAX_SCORE
comment|// must also change.
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
literal|1.7839992f
block|,
literal|8.9925785f
block|,
literal|7.9608946f
block|,
literal|0.07948637f
block|,
literal|2.6356435f
block|,
literal|7.4950366f
block|,
literal|7.1490803f
block|,
literal|8.108544f
block|,
literal|4.961808f
block|,
literal|2.2423935f
block|,
literal|7.285586f
block|,
literal|4.6699767f
block|,
literal|2.9655676f
block|,
literal|6.953706f
block|,
literal|5.383931f
block|,
literal|6.9916306f
block|,
literal|8.365894f
block|,
literal|7.888485f
block|,
literal|8.723962f
block|,
literal|3.1796896f
block|,
literal|0.39971232f
block|,
literal|1.3077754f
block|,
literal|6.8489285f
block|,
literal|9.17561f
block|,
literal|5.060466f
block|,
literal|7.9793315f
block|,
literal|8.601509f
block|,
literal|4.1858315f
block|,
literal|0.28146625f
block|}
decl_stmt|;
DECL|field|MAX_SCORE
specifier|private
specifier|static
specifier|final
name|float
name|MAX_SCORE
init|=
literal|9.17561f
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|method|doSearch
specifier|private
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|doSearch
parameter_list|(
name|int
name|numResults
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
operator|new
name|MyTopsDocCollector
argument_list|(
name|numResults
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
return|return
name|tdc
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// populate an index with 30 documents, this should be enough for the test.
comment|// The documents have no content - the test uses MatchAllDocsQuery().
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testInvalidArguments
specifier|public
name|void
name|testInvalidArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numResults
init|=
literal|5
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
name|numResults
argument_list|)
decl_stmt|;
comment|// start< 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// start> pq.size()
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
name|numResults
operator|+
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// start == pq.size()
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
name|numResults
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// howMany< 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// howMany == 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testZeroResults
specifier|public
name|void
name|testZeroResults
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
operator|new
name|MyTopsDocCollector
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstResultsPage
specifier|public
name|void
name|testFirstResultsPage
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testSecondResultsPages
specifier|public
name|void
name|testSecondResultsPages
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
literal|15
argument_list|)
decl_stmt|;
comment|// ask for more results than are available
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// ask for 5 results (exactly what there should be
name|tdc
operator|=
name|doSearch
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|10
argument_list|,
literal|5
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// ask for less results than there are
name|tdc
operator|=
name|doSearch
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|10
argument_list|,
literal|4
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetAllResults
specifier|public
name|void
name|testGetAllResults
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetResultsFromStart
specifier|public
name|void
name|testGetResultsFromStart
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
literal|15
argument_list|)
decl_stmt|;
comment|// should bring all results
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|tdc
operator|=
name|doSearch
argument_list|(
literal|15
argument_list|)
expr_stmt|;
comment|// get the last 5 only.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxScore
specifier|public
name|void
name|testMaxScore
parameter_list|()
throws|throws
name|Exception
block|{
comment|// ask for all results
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|MAX_SCORE
argument_list|,
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
comment|// ask for 5 last results
name|tdc
operator|=
name|doSearch
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|td
operator|=
name|tdc
operator|.
name|topDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_SCORE
argument_list|,
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
comment|// This does not test the PQ's correctness, but whether topDocs()
comment|// implementations return the results in decreasing score order.
DECL|method|testResultsOrder
specifier|public
name|void
name|testResultsOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|doSearch
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|MAX_SCORE
argument_list|,
name|sd
index|[
literal|0
index|]
operator|.
name|score
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
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
name|sd
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|score
operator|>=
name|sd
index|[
name|i
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

