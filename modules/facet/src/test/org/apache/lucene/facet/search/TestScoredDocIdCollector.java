begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Arrays
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
name|index
operator|.
name|Term
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
name|Query
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
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|FacetTestBase
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
name|search
operator|.
name|FacetsAccumulator
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
name|search
operator|.
name|ScoredDocIDs
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
name|search
operator|.
name|ScoredDocIDsIterator
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
name|search
operator|.
name|ScoredDocIdCollector
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
name|search
operator|.
name|StandardFacetsAccumulator
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
name|search
operator|.
name|params
operator|.
name|CountFacetRequest
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
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|search
operator|.
name|params
operator|.
name|ScoreFacetRequest
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
name|search
operator|.
name|results
operator|.
name|FacetResult
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
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|CategoryPath
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Test ScoredDocIdCollector. */
end_comment

begin_class
DECL|class|TestScoredDocIdCollector
specifier|public
class|class
name|TestScoredDocIdCollector
extends|extends
name|FacetTestBase
block|{
annotation|@
name|Override
annotation|@
name|Before
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
name|initIndex
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
name|closeAll
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConstantScore
specifier|public
name|void
name|testConstantScore
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test that constant score works well
name|assertTrue
argument_list|(
literal|"Would like to test this with deletions!"
argument_list|,
name|indexReader
operator|.
name|hasDeletions
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Would like to test this with deletions!"
argument_list|,
name|indexReader
operator|.
name|numDeletedDocs
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|CONTENT_FIELD
argument_list|,
literal|"white"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|q
argument_list|)
expr_stmt|;
block|}
name|float
name|constScore
init|=
literal|17.0f
decl_stmt|;
name|ScoredDocIdCollector
name|dCollector
init|=
name|ScoredDocIdCollector
operator|.
name|create
argument_list|(
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// scoring is disabled
name|dCollector
operator|.
name|setDefaultScore
argument_list|(
name|constScore
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|dCollector
argument_list|)
expr_stmt|;
comment|// verify by doc scores at the level of doc-id-iterator
name|ScoredDocIDs
name|scoredDocIDs
init|=
name|dCollector
operator|.
name|getScoredDocIDs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of matching documents!"
argument_list|,
literal|2
argument_list|,
name|scoredDocIDs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ScoredDocIDsIterator
name|docItr
init|=
name|scoredDocIDs
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|docItr
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong score for doc "
operator|+
name|docItr
operator|.
name|getDocID
argument_list|()
argument_list|,
name|constScore
argument_list|,
name|docItr
operator|.
name|getScore
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
comment|// verify by facet values
name|List
argument_list|<
name|FacetResult
argument_list|>
name|countRes
init|=
name|findFacets
argument_list|(
name|scoredDocIDs
argument_list|,
name|getFacetedSearchParams
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|scoreRes
init|=
name|findFacets
argument_list|(
name|scoredDocIDs
argument_list|,
name|sumScoreSearchParams
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facet count results!"
argument_list|,
literal|1
argument_list|,
name|countRes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facet score results!"
argument_list|,
literal|1
argument_list|,
name|scoreRes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|parentCountRes
init|=
name|countRes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|FacetResultNode
name|parentScoreRes
init|=
name|scoreRes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of top count aggregated categories!"
argument_list|,
literal|3
argument_list|,
name|parentCountRes
operator|.
name|getNumSubResults
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of top score aggregated categories!"
argument_list|,
literal|3
argument_list|,
name|parentScoreRes
operator|.
name|getNumSubResults
argument_list|()
argument_list|)
expr_stmt|;
comment|// rely on that facet value is computed as doc-score, and
comment|// accordingly compare values of the two top-category results.
name|FacetResultNode
index|[]
name|countResNodes
init|=
name|resultNodesAsArray
argument_list|(
name|parentCountRes
argument_list|)
decl_stmt|;
name|FacetResultNode
index|[]
name|scoreResNodes
init|=
name|resultNodesAsArray
argument_list|(
name|parentScoreRes
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
name|scoreResNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Ordinals differ!"
argument_list|,
name|countResNodes
index|[
name|i
index|]
operator|.
name|getOrdinal
argument_list|()
argument_list|,
name|scoreResNodes
index|[
name|i
index|]
operator|.
name|getOrdinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong scores!"
argument_list|,
name|constScore
operator|*
name|countResNodes
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
argument_list|,
name|scoreResNodes
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
comment|// compute facets with certain facet requests and docs
DECL|method|findFacets
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|findFacets
parameter_list|(
name|ScoredDocIDs
name|sDocids
parameter_list|,
name|FacetSearchParams
name|facetSearchParams
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetsAccumulator
name|fAccumulator
init|=
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|facetSearchParams
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|fAccumulator
operator|.
name|accumulate
argument_list|(
name|sDocids
argument_list|)
decl_stmt|;
comment|// Results are ready, printing them...
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetResult
name|facetResult
range|:
name|res
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Res "
operator|+
operator|(
name|i
operator|++
operator|)
operator|+
literal|": "
operator|+
name|facetResult
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Test
DECL|method|testOutOfOrderCollectionScoringEnabled
specifier|public
name|void
name|testOutOfOrderCollectionScoringEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
literal|"when scoring enabled, out-of-order collection should not be supported"
argument_list|,
name|ScoredDocIdCollector
operator|.
name|create
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOutOfOrderCollectionScoringDisabled
specifier|public
name|void
name|testOutOfOrderCollectionScoringDisabled
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This used to fail, because ScoredDocIdCollector.acceptDocsOutOfOrder
comment|// returned true, even when scoring was enabled.
specifier|final
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|2
block|}
decl_stmt|;
comment|// out of order on purpose
name|ScoredDocIdCollector
name|sdic
init|=
name|ScoredDocIdCollector
operator|.
name|create
argument_list|(
name|docs
operator|.
name|length
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"when scoring disabled, out-of-order collection should be supported"
argument_list|,
name|sdic
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sdic
operator|.
name|collect
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"expected 3 documents but got "
operator|+
name|sdic
operator|.
name|getScoredDocIDs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|,
name|sdic
operator|.
name|getScoredDocIDs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ScoredDocIDsIterator
name|iter
init|=
name|sdic
operator|.
name|getScoredDocIDs
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|docs
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|iter
operator|.
name|next
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"expected doc "
operator|+
name|docs
index|[
name|i
index|]
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
name|iter
operator|.
name|getDocID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* use a scoring aggregator */
DECL|method|sumScoreSearchParams
specifier|private
name|FacetSearchParams
name|sumScoreSearchParams
parameter_list|()
block|{
comment|// this will use default faceted indexing params, not altering anything about indexing
name|FacetSearchParams
name|res
init|=
name|super
operator|.
name|getFacetedSearchParams
argument_list|()
decl_stmt|;
name|res
operator|.
name|addFacetRequest
argument_list|(
operator|new
name|ScoreFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|getFacetedSearchParams
specifier|protected
name|FacetSearchParams
name|getFacetedSearchParams
parameter_list|()
block|{
name|FacetSearchParams
name|res
init|=
name|super
operator|.
name|getFacetedSearchParams
argument_list|()
decl_stmt|;
name|res
operator|.
name|addFacetRequest
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

