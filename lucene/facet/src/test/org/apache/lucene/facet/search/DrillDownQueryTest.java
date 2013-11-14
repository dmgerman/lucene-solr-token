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
name|Random
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
name|document
operator|.
name|Field
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
name|TextField
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
name|FacetTestCase
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
name|FacetFields
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|QueryUtils
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
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_class
DECL|class|DrillDownQueryTest
specifier|public
class|class
name|DrillDownQueryTest
extends|extends
name|FacetTestCase
block|{
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|taxo
specifier|private
specifier|static
name|DirectoryTaxonomyReader
name|taxo
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|taxoDir
specifier|private
specifier|static
name|Directory
name|taxoDir
decl_stmt|;
DECL|field|defaultParams
specifier|private
name|FacetIndexingParams
name|defaultParams
decl_stmt|;
DECL|field|nonDefaultParams
specifier|private
name|PerDimensionIndexingParams
name|nonDefaultParams
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClassDrillDownQueryTest
specifier|public
specifier|static
name|void
name|afterClassDrillDownQueryTest
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|taxo
argument_list|,
name|dir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|taxo
operator|=
literal|null
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|taxoDir
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClassDrillDownQueryTest
specifier|public
specifier|static
name|void
name|beforeClassDrillDownQueryTest
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|r
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|taxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|ArrayList
argument_list|<
name|FacetLabel
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetLabel
argument_list|>
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
comment|// 50
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
comment|// 33
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|4
operator|==
literal|0
condition|)
block|{
comment|// 25
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a/1"
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a/2"
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|%
literal|5
operator|==
literal|0
condition|)
block|{
comment|// 20
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|taxo
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|DrillDownQueryTest
specifier|public
name|DrillDownQueryTest
parameter_list|()
block|{
name|Map
argument_list|<
name|FacetLabel
argument_list|,
name|CategoryListParams
argument_list|>
name|paramsMap
init|=
operator|new
name|HashMap
argument_list|<
name|FacetLabel
argument_list|,
name|CategoryListParams
argument_list|>
argument_list|()
decl_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|randomCategoryListParams
argument_list|(
literal|"testing_facets_a"
argument_list|)
argument_list|)
expr_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|randomCategoryListParams
argument_list|(
literal|"testing_facets_b"
argument_list|)
argument_list|)
expr_stmt|;
name|nonDefaultParams
operator|=
operator|new
name|PerDimensionIndexingParams
argument_list|(
name|paramsMap
argument_list|)
expr_stmt|;
name|defaultParams
operator|=
operator|new
name|FacetIndexingParams
argument_list|(
name|randomCategoryListParams
argument_list|(
name|CategoryListParams
operator|.
name|DEFAULT_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultField
specifier|public
name|void
name|testDefaultField
parameter_list|()
block|{
name|String
name|defaultField
init|=
name|CategoryListParams
operator|.
name|DEFAULT_FIELD
decl_stmt|;
name|Term
name|termA
init|=
name|DrillDownQuery
operator|.
name|term
argument_list|(
name|defaultParams
argument_list|,
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Term
argument_list|(
name|defaultField
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|termA
argument_list|)
expr_stmt|;
name|Term
name|termB
init|=
name|DrillDownQuery
operator|.
name|term
argument_list|(
name|defaultParams
argument_list|,
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Term
argument_list|(
name|defaultField
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|termB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAndOrs
specifier|public
name|void
name|testAndOrs
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// test (a/1 OR a/2) AND b
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a/1"
argument_list|,
literal|'/'
argument_list|)
argument_list|,
operator|new
name|FacetLabel
argument_list|(
literal|"a/2"
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQuery
specifier|public
name|void
name|testQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Making sure the query yields 25 documents with the facet "a"
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|25
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Making sure the query yields 5 documents with the facet "b" and the
comment|// previous (facet "a") query as a base query
name|DrillDownQuery
name|q2
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Making sure that a query of both facet "a" and facet "b" yields 5 results
name|DrillDownQuery
name|q3
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|)
decl_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Check that content:foo (which yields 50% results) and facet/b (which yields 20%)
comment|// would gather together 10 results (10%..)
name|Query
name|fooQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|DrillDownQuery
name|q4
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
name|fooQuery
argument_list|)
decl_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q4
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryImplicitDefaultParams
specifier|public
name|void
name|testQueryImplicitDefaultParams
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Create the base query to start with
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Making sure the query yields 5 documents with the facet "b" and the
comment|// previous (facet "a") query as a base query
name|DrillDownQuery
name|q2
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Check that content:foo (which yields 50% results) and facet/b (which yields 20%)
comment|// would gather together 10 results (10%..)
name|Query
name|fooQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|DrillDownQuery
name|q4
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
name|fooQuery
argument_list|)
decl_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q4
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testScoring
specifier|public
name|void
name|testScoring
parameter_list|()
throws|throws
name|IOException
block|{
comment|// verify that drill-down queries do not modify scores
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// fetch all available docs to this query
for|for
control|(
name|ScoreDoc
name|sd
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|scores
index|[
name|sd
operator|.
name|doc
index|]
operator|=
name|sd
operator|.
name|score
expr_stmt|;
block|}
comment|// create a drill-down query with category "a", scores should not change
name|DrillDownQuery
name|q2
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// fetch all available docs to this query
for|for
control|(
name|ScoreDoc
name|sd
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|assertEquals
argument_list|(
literal|"score of doc="
operator|+
name|sd
operator|.
name|doc
operator|+
literal|" modified"
argument_list|,
name|scores
index|[
name|sd
operator|.
name|doc
index|]
argument_list|,
name|sd
operator|.
name|score
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScoringNoBaseQuery
specifier|public
name|void
name|testScoringNoBaseQuery
parameter_list|()
throws|throws
name|IOException
block|{
comment|// verify that drill-down queries (with no base query) returns 0.0 score
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// fetch all available docs to this query
for|for
control|(
name|ScoreDoc
name|sd
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|assertEquals
argument_list|(
literal|0f
argument_list|,
name|sd
operator|.
name|score
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTermNonDefault
specifier|public
name|void
name|testTermNonDefault
parameter_list|()
block|{
name|Term
name|termA
init|=
name|DrillDownQuery
operator|.
name|term
argument_list|(
name|nonDefaultParams
argument_list|,
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Term
argument_list|(
literal|"testing_facets_a"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|termA
argument_list|)
expr_stmt|;
name|Term
name|termB
init|=
name|DrillDownQuery
operator|.
name|term
argument_list|(
name|nonDefaultParams
argument_list|,
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Term
argument_list|(
literal|"testing_facets_b"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|termB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClone
specifier|public
name|void
name|testClone
parameter_list|()
throws|throws
name|Exception
block|{
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|DrillDownQuery
name|clone
init|=
name|q
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"query wasn't cloned: source="
operator|+
name|q
operator|+
literal|" clone="
operator|+
name|clone
argument_list|,
name|q
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|clone
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testNoBaseNorDrillDown
specifier|public
name|void
name|testNoBaseNorDrillDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|)
decl_stmt|;
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoDrillDown
specifier|public
name|void
name|testNoDrillDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|base
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|defaultParams
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|Query
name|rewrite
init|=
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|base
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

