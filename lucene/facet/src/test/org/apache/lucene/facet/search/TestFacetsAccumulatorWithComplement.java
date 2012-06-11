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
name|MultiReader
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
name|ParallelAtomicReader
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
name|SlowCompositeReaderWrapper
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
name|MatchAllDocsQuery
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
name|junit
operator|.
name|After
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Test that complementsworks as expected.  * We place this test under *.facet.search rather than *.search  * because the test actually does faceted search.  */
end_comment

begin_class
DECL|class|TestFacetsAccumulatorWithComplement
specifier|public
class|class
name|TestFacetsAccumulatorWithComplement
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
annotation|@
name|After
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
comment|/**    * Test that complements does not cause a failure when using a parallel reader    */
annotation|@
name|Test
DECL|method|testComplementsWithParallerReader
specifier|public
name|void
name|testComplementsWithParallerReader
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|origReader
init|=
name|indexReader
decl_stmt|;
name|ParallelAtomicReader
name|pr
init|=
operator|new
name|ParallelAtomicReader
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|origReader
argument_list|)
argument_list|)
decl_stmt|;
name|indexReader
operator|=
name|pr
expr_stmt|;
try|try
block|{
name|doTestComplements
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|=
name|origReader
expr_stmt|;
block|}
block|}
comment|/**    * Test that complements works with MultiReader    */
annotation|@
name|Test
DECL|method|testComplementsWithMultiReader
specifier|public
name|void
name|testComplementsWithMultiReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|IndexReader
name|origReader
init|=
name|indexReader
decl_stmt|;
name|indexReader
operator|=
operator|new
name|MultiReader
argument_list|(
name|origReader
argument_list|)
expr_stmt|;
try|try
block|{
name|doTestComplements
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|=
name|origReader
expr_stmt|;
block|}
block|}
comment|/**    * Test that score is indeed constant when using a constant score    */
annotation|@
name|Test
DECL|method|testComplements
specifier|public
name|void
name|testComplements
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestComplements
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestComplements
specifier|private
name|void
name|doTestComplements
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
comment|//new TermQuery(new Term(TEXT,"white"));
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
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|dCollector
argument_list|)
expr_stmt|;
comment|// verify by facet values
name|List
argument_list|<
name|FacetResult
argument_list|>
name|countResWithComplement
init|=
name|findFacets
argument_list|(
name|dCollector
operator|.
name|getScoredDocIDs
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|countResNoComplement
init|=
name|findFacets
argument_list|(
name|dCollector
operator|.
name|getScoredDocIDs
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facet count results with complement!"
argument_list|,
literal|1
argument_list|,
name|countResWithComplement
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facet count results no complement!"
argument_list|,
literal|1
argument_list|,
name|countResNoComplement
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|parentResWithComp
init|=
name|countResWithComplement
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
name|parentResNoComp
init|=
name|countResWithComplement
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
literal|"Wrong number of top count aggregated categories with complement!"
argument_list|,
literal|3
argument_list|,
name|parentResWithComp
operator|.
name|getNumSubResults
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of top count aggregated categories no complement!"
argument_list|,
literal|3
argument_list|,
name|parentResNoComp
operator|.
name|getNumSubResults
argument_list|()
argument_list|)
expr_stmt|;
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
comment|/** compute facets with certain facet requests and docs */
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
name|boolean
name|withComplement
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
name|getFacetedSearchParams
argument_list|()
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
name|fAccumulator
operator|.
name|setComplementThreshold
argument_list|(
name|withComplement
condition|?
name|FacetsAccumulator
operator|.
name|FORCE_COMPLEMENT
else|:
name|FacetsAccumulator
operator|.
name|DISABLE_COMPLEMENT
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|withComplement
argument_list|,
operator|(
operator|(
name|StandardFacetsAccumulator
operator|)
name|fAccumulator
operator|)
operator|.
name|isUsingComplements
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

