begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.example.adaptive
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|example
operator|.
name|adaptive
package|;
end_package

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
name|IndexSearcher
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopScoreDocCollector
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
name|search
operator|.
name|MultiCollector
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
name|example
operator|.
name|ExampleUtils
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
name|example
operator|.
name|simple
operator|.
name|SimpleUtils
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
name|AdaptiveFacetsAccumulator
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
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyReader
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Search with facets through the {@link AdaptiveFacetsAccumulator}   *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|AdaptiveSearcher
specifier|public
class|class
name|AdaptiveSearcher
block|{
comment|/**    * Search with facets through the {@link AdaptiveFacetsAccumulator}     * @param indexDir Directory of the search index.    * @param taxoDir Directory of the taxonomy index.    * @throws Exception on error (no detailed exception handling here for sample simplicity    * @return facet results    */
DECL|method|searchWithFacets
specifier|public
specifier|static
name|List
argument_list|<
name|FacetResult
argument_list|>
name|searchWithFacets
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
comment|// faceted search is working in 2 steps:
comment|// 1. collect matching documents
comment|// 2. aggregate facets for collected documents and
comment|//    generate the requested faceted results from the aggregated facets
comment|// step 1: collect matching documents into a collector
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|SimpleUtils
operator|.
name|TEXT
argument_list|,
literal|"white"
argument_list|)
argument_list|)
decl_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"Query: "
operator|+
name|q
argument_list|)
expr_stmt|;
comment|// regular collector for scoring matched documents
name|TopScoreDocCollector
name|topDocsCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// docids collector for guiding facets accumulation (scoring disabled)
name|ScoredDocIdCollector
name|docIdsCollecor
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
comment|// Faceted search parameters indicate which facets are we interested in
name|FacetSearchParams
name|facetSearchParams
init|=
operator|new
name|FacetSearchParams
argument_list|()
decl_stmt|;
name|facetSearchParams
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
comment|// search, into both collectors. note: in case only facets accumulation
comment|// is required, the topDocCollector part can be totally discarded
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|topDocsCollector
argument_list|,
name|docIdsCollecor
argument_list|)
argument_list|)
expr_stmt|;
comment|// Obtain facets results and print them
name|AdaptiveFacetsAccumulator
name|accumulator
init|=
operator|new
name|AdaptiveFacetsAccumulator
argument_list|(
name|facetSearchParams
argument_list|,
name|indexReader
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|accumulator
operator|.
name|accumulate
argument_list|(
name|docIdsCollecor
operator|.
name|getScoredDocIDs
argument_list|()
argument_list|)
decl_stmt|;
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
name|ExampleUtils
operator|.
name|log
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
comment|// we're done, close the index reader and the taxonomy.
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

