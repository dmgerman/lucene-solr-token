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
name|Collections
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
operator|.
name|Store
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
name|StringField
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
name|facet
operator|.
name|util
operator|.
name|AssertingCategoryListIterator
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
name|DirectoryReader
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
name|IndexWriter
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
name|NoMergePolicy
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
name|Test
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestStandardFacetsAccumulator
specifier|public
class|class
name|TestStandardFacetsAccumulator
extends|extends
name|FacetTestCase
block|{
DECL|method|indexTwoDocs
specifier|private
name|void
name|indexTwoDocs
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|FacetFields
name|facetFields
parameter_list|,
name|boolean
name|withContent
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|withContent
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|facetFields
operator|!=
literal|null
condition|)
block|{
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"A"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSegmentsWithoutCategoriesOrResults
specifier|public
name|void
name|testSegmentsWithoutCategoriesOrResults
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests the accumulator when there are segments with no results
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
expr_stmt|;
comment|// prevent merges
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|fip
init|=
operator|new
name|FacetIndexingParams
argument_list|(
operator|new
name|CategoryListParams
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CategoryListIterator
name|createCategoryListIterator
parameter_list|(
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingCategoryListIterator
argument_list|(
name|super
operator|.
name|createCategoryListIterator
argument_list|(
name|partition
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxoWriter
argument_list|,
name|fip
argument_list|)
decl_stmt|;
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
name|facetFields
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 1st segment, no content, with categories
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 2nd segment, with content, no categories
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
name|facetFields
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 3rd segment ok
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 4th segment, no content, or categories
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 5th segment, with content, no categories
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
name|facetFields
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 6th segment, with content, with categories
name|indexTwoDocs
argument_list|(
name|indexWriter
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 7th segment, with content, no categories
name|IOUtils
operator|.
name|close
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
name|DirectoryReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
comment|// search for "f:a", only segments 1 and 3 should match results
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|FacetRequest
name|countNoComplements
init|=
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"A"
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|fip
argument_list|,
name|countNoComplements
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"received too many facet results"
argument_list|,
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|frn
init|=
name|results
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
literal|"wrong number of children"
argument_list|,
literal|2
argument_list|,
name|frn
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|node
range|:
name|frn
operator|.
name|subResults
control|)
block|{
name|assertEquals
argument_list|(
literal|"wrong weight for child "
operator|+
name|node
operator|.
name|label
argument_list|,
literal|2
argument_list|,
operator|(
name|int
operator|)
name|node
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

