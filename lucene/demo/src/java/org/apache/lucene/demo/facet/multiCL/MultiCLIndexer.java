begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet.multiCL
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
operator|.
name|multiCL
package|;
end_package

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
name|HashMap
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
name|demo
operator|.
name|facet
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
name|demo
operator|.
name|facet
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Sample indexer creates an index, and adds to it sample documents and facets   * with multiple CategoryLists specified for different facets, so there are different  * category lists for different facets.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiCLIndexer
specifier|public
class|class
name|MultiCLIndexer
block|{
comment|/** No instance */
DECL|method|MultiCLIndexer
specifier|private
name|MultiCLIndexer
parameter_list|()
block|{}
comment|/** Number of documents to index */
DECL|field|NUM_DOCS
specifier|public
specifier|static
name|int
name|NUM_DOCS
init|=
literal|100
decl_stmt|;
comment|/** Number of facets to add per document */
DECL|field|NUM_FACETS_PER_DOC
specifier|public
specifier|static
name|int
name|NUM_FACETS_PER_DOC
init|=
literal|10
decl_stmt|;
comment|/** Number of tokens in title */
DECL|field|TITLE_LENGTH
specifier|public
specifier|static
name|int
name|TITLE_LENGTH
init|=
literal|5
decl_stmt|;
comment|/** Number of tokens in text */
DECL|field|TEXT_LENGTH
specifier|public
specifier|static
name|int
name|TEXT_LENGTH
init|=
literal|100
decl_stmt|;
comment|// Lorum ipsum to use as content - this will be tokenized and used for document
comment|// titles/text.
DECL|field|words
specifier|static
name|String
name|words
init|=
literal|"Sed ut perspiciatis unde omnis iste natus error sit "
operator|+
literal|"voluptatem accusantium doloremque laudantium totam rem aperiam "
operator|+
literal|"eaque ipsa quae ab illo inventore veritatis et quasi architecto "
operator|+
literal|"beatae vitae dicta sunt explicabo Nemo enim ipsam voluptatem "
operator|+
literal|"quia voluptas sit aspernatur aut odit aut fugit sed quia consequuntur "
operator|+
literal|"magni dolores eos qui ratione voluptatem sequi nesciunt Neque porro "
operator|+
literal|"quisquam est qui dolorem ipsum quia dolor sit amet consectetur adipisci velit "
operator|+
literal|"sed quia non numquam eius modi tempora incidunt ut labore et dolore "
operator|+
literal|"magnam aliquam quaerat voluptatem Ut enim ad minima veniam "
operator|+
literal|"quis nostrum exercitationem ullam corporis suscipit laboriosam "
operator|+
literal|"nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure"
operator|+
literal|"reprehenderit qui in ea voluptate velit esse quam nihil molestiae "
operator|+
literal|"consequatur vel illum qui dolorem eum fugiat quo voluptas nulla pariatur"
decl_stmt|;
comment|/** PerDimensionIndexingParams for multiple category lists */
DECL|field|MULTI_IPARAMS
specifier|public
specifier|static
specifier|final
name|PerDimensionIndexingParams
name|MULTI_IPARAMS
decl_stmt|;
comment|// Initialize PerDimensionIndexingParams
static|static
block|{
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|CategoryListParams
argument_list|>
name|paramsMap
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryPath
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
name|CategoryPath
argument_list|(
literal|"0"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$Digits$Zero"
argument_list|)
argument_list|)
expr_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"1"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$Digits$One"
argument_list|)
argument_list|)
expr_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"2"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$Digits$Two"
argument_list|)
argument_list|)
expr_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"3"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$Digits$Three"
argument_list|)
argument_list|)
expr_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"4"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$Digits$Four"
argument_list|)
argument_list|)
expr_stmt|;
name|paramsMap
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"5"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$Digits$Five"
argument_list|)
argument_list|)
expr_stmt|;
name|MULTI_IPARAMS
operator|=
operator|new
name|PerDimensionIndexingParams
argument_list|(
name|paramsMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an index, and adds to it sample documents and facets.    *     * @param indexDir    *          Directory in which the index should be created.    * @param taxoDir    *          Directory in which the taxonomy index should be created.    * @throws Exception    *           on error (no detailed exception handling here for sample    *           simplicity    */
DECL|method|index
specifier|public
specifier|static
name|void
name|index
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
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|2003
argument_list|)
decl_stmt|;
name|String
index|[]
name|docTitles
init|=
operator|new
name|String
index|[
name|NUM_DOCS
index|]
decl_stmt|;
name|String
index|[]
name|docTexts
init|=
operator|new
name|String
index|[
name|NUM_DOCS
index|]
decl_stmt|;
name|CategoryPath
index|[]
index|[]
name|cPaths
init|=
operator|new
name|CategoryPath
index|[
name|NUM_DOCS
index|]
index|[
name|NUM_FACETS_PER_DOC
index|]
decl_stmt|;
name|String
index|[]
name|tokens
init|=
name|words
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|NUM_DOCS
condition|;
name|docNum
operator|++
control|)
block|{
name|String
name|title
init|=
literal|""
decl_stmt|;
name|String
name|text
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|TITLE_LENGTH
condition|;
name|j
operator|++
control|)
block|{
name|title
operator|=
name|title
operator|+
name|tokens
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|tokens
operator|.
name|length
argument_list|)
index|]
operator|+
literal|" "
expr_stmt|;
block|}
name|docTitles
index|[
name|docNum
index|]
operator|=
name|title
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|TEXT_LENGTH
condition|;
name|j
operator|++
control|)
block|{
name|text
operator|=
name|text
operator|+
name|tokens
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|tokens
operator|.
name|length
argument_list|)
index|]
operator|+
literal|" "
expr_stmt|;
block|}
name|docTexts
index|[
name|docNum
index|]
operator|=
name|text
expr_stmt|;
for|for
control|(
name|int
name|facetNum
init|=
literal|0
init|;
name|facetNum
operator|<
name|NUM_FACETS_PER_DOC
condition|;
name|facetNum
operator|++
control|)
block|{
name|cPaths
index|[
name|docNum
index|]
index|[
name|facetNum
index|]
operator|=
operator|new
name|CategoryPath
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|index
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|,
name|MULTI_IPARAMS
argument_list|,
name|docTitles
argument_list|,
name|docTexts
argument_list|,
name|cPaths
argument_list|)
expr_stmt|;
block|}
comment|/**    * More advanced method for specifying custom indexing params, doc texts,     * doc titles and category paths.    */
DECL|method|index
specifier|public
specifier|static
name|void
name|index
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|,
name|FacetIndexingParams
name|iParams
parameter_list|,
name|String
index|[]
name|docTitles
parameter_list|,
name|String
index|[]
name|docTexts
parameter_list|,
name|CategoryPath
index|[]
index|[]
name|cPaths
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create and open an index writer
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|ExampleUtils
operator|.
name|EXAMPLE_VER
argument_list|,
name|SimpleUtils
operator|.
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|DirectoryTaxonomyWriter
name|taxo
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|iw
argument_list|,
name|taxo
argument_list|,
name|iParams
argument_list|,
name|docTitles
argument_list|,
name|docTexts
argument_list|,
name|cPaths
argument_list|)
expr_stmt|;
block|}
comment|/**    * More advanced method for specifying custom indexing params, doc texts,     * doc titles and category paths.    *<p>    * Create an index, and adds to it sample documents and facets.    * @throws Exception    *             on error (no detailed exception handling here for sample    *             simplicity    */
DECL|method|index
specifier|public
specifier|static
name|void
name|index
parameter_list|(
name|IndexWriter
name|iw
parameter_list|,
name|DirectoryTaxonomyWriter
name|taxo
parameter_list|,
name|FacetIndexingParams
name|iParams
parameter_list|,
name|String
index|[]
name|docTitles
parameter_list|,
name|String
index|[]
name|docTexts
parameter_list|,
name|CategoryPath
index|[]
index|[]
name|cPaths
parameter_list|)
throws|throws
name|Exception
block|{
comment|// loop over sample documents
name|int
name|nDocsAdded
init|=
literal|0
decl_stmt|;
name|int
name|nFacetsAdded
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|SimpleUtils
operator|.
name|docTexts
operator|.
name|length
condition|;
name|docNum
operator|++
control|)
block|{
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|facetList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|cPaths
index|[
name|docNum
index|]
argument_list|)
decl_stmt|;
comment|// we do not alter indexing parameters!
comment|// FacetFields adds the categories to the document in addFields()
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxo
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
comment|// create a plain Lucene document and add some regular Lucene fields
comment|// to it
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|SimpleUtils
operator|.
name|TITLE
argument_list|,
name|docTitles
index|[
name|docNum
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|SimpleUtils
operator|.
name|TEXT
argument_list|,
name|docTexts
index|[
name|docNum
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// finally add the document to the index
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|facetList
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|nDocsAdded
operator|++
expr_stmt|;
name|nFacetsAdded
operator|+=
name|facetList
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// commit changes.
comment|// we commit changes to the taxonomy index prior to committing them to
comment|// the search index.
comment|// this is important, so that all facets referred to by documents in the
comment|// search index
comment|// will indeed exist in the taxonomy index.
name|taxo
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// close the taxonomy index and the index - all modifications are
comment|// now safely in the provided directories: indexDir and taxoDir.
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"Indexed "
operator|+
name|nDocsAdded
operator|+
literal|" documents with overall "
operator|+
name|nFacetsAdded
operator|+
literal|" facets."
argument_list|)
expr_stmt|;
block|}
comment|/** Driver for the example */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|index
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

