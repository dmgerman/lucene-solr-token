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
name|Iterator
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
name|analysis
operator|.
name|MockAnalyzer
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
name|analysis
operator|.
name|MockTokenizer
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
name|FacetTestUtils
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|index
operator|.
name|params
operator|.
name|PerDimensionIndexingParams
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
name|FacetRequest
operator|.
name|ResultMode
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
name|TaxonomyWriter
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
name|DirectoryTaxonomyWriter
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
name|DocsEnum
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|MultiFields
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
name|DocIdSetIterator
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
name|util
operator|.
name|BytesRef
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
name|IOUtils
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
name|_TestUtil
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestMultipleCategoryLists
specifier|public
class|class
name|TestMultipleCategoryLists
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testDefault
specifier|public
name|void
name|testDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
index|[]
index|[]
name|dirs
init|=
name|getDirs
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dirs
index|[
literal|0
index|]
index|[
literal|0
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
comment|/**      * Configure with no custom counting lists      */
name|PerDimensionIndexingParams
name|iParams
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|()
decl_stmt|;
name|seedIndex
argument_list|(
name|iw
argument_list|,
name|tw
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|facetsCollector
init|=
name|performSearch
argument_list|(
name|iParams
argument_list|,
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facetsCollector
argument_list|)
expr_stmt|;
name|DocsEnum
name|td
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|ir
argument_list|,
literal|"$facets"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"$fulltree$"
argument_list|)
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|ir
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustom
specifier|public
name|void
name|testCustom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
index|[]
index|[]
name|dirs
init|=
name|getDirs
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dirs
index|[
literal|0
index|]
index|[
literal|0
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|PerDimensionIndexingParams
name|iParams
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|()
decl_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$author"
argument_list|,
literal|"Authors"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|iw
argument_list|,
name|tw
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|facetsCollector
init|=
name|performSearch
argument_list|(
name|iParams
argument_list|,
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facetsCollector
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$facets"
argument_list|,
literal|"$fulltree$"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$author"
argument_list|,
literal|"Authors"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoCustomsSameField
specifier|public
name|void
name|testTwoCustomsSameField
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
index|[]
index|[]
name|dirs
init|=
name|getDirs
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dirs
index|[
literal|0
index|]
index|[
literal|0
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|PerDimensionIndexingParams
name|iParams
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|()
decl_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Band"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$music"
argument_list|,
literal|"Bands"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Composer"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$music"
argument_list|,
literal|"Composers"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|iw
argument_list|,
name|tw
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|facetsCollector
init|=
name|performSearch
argument_list|(
name|iParams
argument_list|,
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facetsCollector
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$facets"
argument_list|,
literal|"$fulltree$"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$music"
argument_list|,
literal|"Bands"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$music"
argument_list|,
literal|"Composers"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPostingListExists
specifier|private
name|void
name|assertPostingListExists
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|,
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|IOException
block|{
name|DocsEnum
name|de
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|ir
argument_list|,
name|field
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|text
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|de
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDifferentFieldsAndText
specifier|public
name|void
name|testDifferentFieldsAndText
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
index|[]
index|[]
name|dirs
init|=
name|getDirs
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dirs
index|[
literal|0
index|]
index|[
literal|0
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|PerDimensionIndexingParams
name|iParams
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|()
decl_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Band"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$bands"
argument_list|,
literal|"Bands"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Composer"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$composers"
argument_list|,
literal|"Composers"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|iw
argument_list|,
name|tw
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|facetsCollector
init|=
name|performSearch
argument_list|(
name|iParams
argument_list|,
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facetsCollector
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$facets"
argument_list|,
literal|"$fulltree$"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$bands"
argument_list|,
literal|"Bands"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$composers"
argument_list|,
literal|"Composers"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSomeSameSomeDifferent
specifier|public
name|void
name|testSomeSameSomeDifferent
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
index|[]
index|[]
name|dirs
init|=
name|getDirs
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dirs
index|[
literal|0
index|]
index|[
literal|0
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|PerDimensionIndexingParams
name|iParams
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|()
decl_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Band"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$music"
argument_list|,
literal|"music"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Composer"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$music"
argument_list|,
literal|"music"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iParams
operator|.
name|addCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
operator|new
name|Term
argument_list|(
literal|"$literature"
argument_list|,
literal|"Authors"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|iw
argument_list|,
name|tw
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|facetsCollector
init|=
name|performSearch
argument_list|(
name|iParams
argument_list|,
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facetsCollector
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$music"
argument_list|,
literal|"music"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertPostingListExists
argument_list|(
literal|"$literature"
argument_list|,
literal|"Authors"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|getDirs
specifier|private
name|Directory
index|[]
index|[]
name|getDirs
parameter_list|()
block|{
return|return
name|FacetTestUtils
operator|.
name|createIndexTaxonomyDirs
argument_list|(
literal|1
argument_list|)
return|;
block|}
DECL|method|assertCorrectResults
specifier|private
name|void
name|assertCorrectResults
parameter_list|(
name|FacetsCollector
name|facetsCollector
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|facetsCollector
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|FacetResult
name|results
init|=
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FacetResultNode
name|resNode
init|=
name|results
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|?
extends|extends
name|FacetResultNode
argument_list|>
name|subResults
init|=
name|resNode
operator|.
name|getSubResults
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|FacetResultNode
argument_list|>
name|subIter
init|=
name|subResults
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|checkResult
argument_list|(
name|resNode
argument_list|,
literal|"Band"
argument_list|,
literal|5.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop"
argument_list|,
literal|4.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Punk"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|results
operator|=
name|res
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|resNode
operator|=
name|results
operator|.
name|getFacetResultNode
argument_list|()
expr_stmt|;
name|subResults
operator|=
name|resNode
operator|.
name|getSubResults
argument_list|()
expr_stmt|;
name|subIter
operator|=
name|subResults
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|checkResult
argument_list|(
name|resNode
argument_list|,
literal|"Band"
argument_list|,
literal|5.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop"
argument_list|,
literal|4.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/Dave Matthews Band"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/REM"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/U2"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Punk/The Ramones"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Punk"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/The Beatles"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|results
operator|=
name|res
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|resNode
operator|=
name|results
operator|.
name|getFacetResultNode
argument_list|()
expr_stmt|;
name|subResults
operator|=
name|resNode
operator|.
name|getSubResults
argument_list|()
expr_stmt|;
name|subIter
operator|=
name|subResults
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|checkResult
argument_list|(
name|resNode
argument_list|,
literal|"Author"
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Author/Kurt Vonnegut"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Author/Stephen King"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Author/Mark Twain"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|results
operator|=
name|res
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|resNode
operator|=
name|results
operator|.
name|getFacetResultNode
argument_list|()
expr_stmt|;
name|subResults
operator|=
name|resNode
operator|.
name|getSubResults
argument_list|()
expr_stmt|;
name|subIter
operator|=
name|subResults
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|checkResult
argument_list|(
name|resNode
argument_list|,
literal|"Band/Rock& Pop"
argument_list|,
literal|4.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/Dave Matthews Band"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/REM"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/U2"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|subIter
operator|.
name|next
argument_list|()
argument_list|,
literal|"Band/Rock& Pop/The Beatles"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
DECL|method|performSearch
specifier|private
name|FacetsCollector
name|performSearch
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|TaxonomyReader
name|tr
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
comment|// step 1: collect matching documents into a collector
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
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
comment|// Faceted search parameters indicate which facets are we interested in
name|FacetSearchParams
name|facetSearchParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|iParams
argument_list|)
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
literal|"Band"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|CountFacetRequest
name|bandDepth
init|=
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Band"
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|bandDepth
operator|.
name|setDepth
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// makes it easier to check the results in the test.
name|bandDepth
operator|.
name|setResultMode
argument_list|(
name|ResultMode
operator|.
name|GLOBAL_FLAT
argument_list|)
expr_stmt|;
name|facetSearchParams
operator|.
name|addFacetRequest
argument_list|(
name|bandDepth
argument_list|)
expr_stmt|;
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
literal|"Author"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// perform documents search and facets accumulation
name|FacetsCollector
name|facetsCollector
init|=
operator|new
name|FacetsCollector
argument_list|(
name|facetSearchParams
argument_list|,
name|ir
argument_list|,
name|tr
argument_list|)
decl_stmt|;
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
name|facetsCollector
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|facetsCollector
return|;
block|}
DECL|method|seedIndex
specifier|private
name|void
name|seedIndex
parameter_list|(
name|RandomIndexWriter
name|iw
parameter_list|,
name|TaxonomyWriter
name|tw
parameter_list|,
name|FacetIndexingParams
name|iParams
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Author"
argument_list|,
literal|"Mark Twain"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Author"
argument_list|,
literal|"Stephen King"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Author"
argument_list|,
literal|"Kurt Vonnegut"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"The Beatles"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Band"
argument_list|,
literal|"Punk"
argument_list|,
literal|"The Ramones"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"U2"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"REM"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"Dave Matthews Band"
argument_list|)
expr_stmt|;
name|FacetTestUtils
operator|.
name|add
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
literal|"Composer"
argument_list|,
literal|"Bach"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkResult
specifier|private
specifier|static
name|void
name|checkResult
parameter_list|(
name|FacetResultNode
name|sub
parameter_list|,
name|String
name|label
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Label of subresult "
operator|+
name|sub
operator|.
name|getLabel
argument_list|()
operator|+
literal|" was incorrect"
argument_list|,
name|label
argument_list|,
name|sub
operator|.
name|getLabel
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Value for "
operator|+
name|sub
operator|.
name|getLabel
argument_list|()
operator|+
literal|" subresult was incorrect"
argument_list|,
name|value
argument_list|,
name|sub
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

