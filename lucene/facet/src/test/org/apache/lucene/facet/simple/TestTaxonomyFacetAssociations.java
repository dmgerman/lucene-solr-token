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

begin_comment
comment|/** Test for associations */
end_comment

begin_class
DECL|class|TestTaxonomyFacetAssociations
specifier|public
class|class
name|TestTaxonomyFacetAssociations
extends|extends
name|FacetTestCase
block|{
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|taxoDir
specifier|private
specifier|static
name|Directory
name|taxoDir
decl_stmt|;
DECL|field|taxoReader
specifier|private
specifier|static
name|TaxonomyReader
name|taxoReader
decl_stmt|;
DECL|field|aint
specifier|private
specifier|static
specifier|final
name|FacetLabel
name|aint
init|=
operator|new
name|FacetLabel
argument_list|(
literal|"int"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
DECL|field|bint
specifier|private
specifier|static
specifier|final
name|FacetLabel
name|bint
init|=
operator|new
name|FacetLabel
argument_list|(
literal|"int"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
DECL|field|afloat
specifier|private
specifier|static
specifier|final
name|FacetLabel
name|afloat
init|=
operator|new
name|FacetLabel
argument_list|(
literal|"float"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
DECL|field|bfloat
specifier|private
specifier|static
specifier|final
name|FacetLabel
name|bfloat
init|=
operator|new
name|FacetLabel
argument_list|(
literal|"float"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
specifier|final
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
comment|// preparations - index, taxonomy, content
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// Cannot mix ints& floats in the same indexed field:
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"int"
argument_list|,
literal|"$facets.int"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"int"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"float"
argument_list|,
literal|"$facets.float"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"float"
argument_list|,
literal|true
argument_list|)
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
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
name|taxoWriter
argument_list|,
name|config
argument_list|)
decl_stmt|;
comment|// index documents, 50% have only 'b' and all have 'a'
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|110
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
comment|// every 11th document is added empty, this used to cause the association
comment|// aggregators to go into an infinite loop
if|if
condition|(
name|i
operator|%
literal|11
operator|!=
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntAssociationFacetField
argument_list|(
literal|2
argument_list|,
literal|"int"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FloatAssociationFacetField
argument_list|(
literal|0.5f
argument_list|,
literal|"float"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
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
name|IntAssociationFacetField
argument_list|(
literal|3
argument_list|,
literal|"int"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FloatAssociationFacetField
argument_list|(
literal|0.2f
argument_list|,
literal|"float"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
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
name|taxoReader
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
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
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
operator|=
literal|null
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testIntSumAssociation
specifier|public
name|void
name|testIntSumAssociation
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFacetsCollector
name|fc
init|=
operator|new
name|SimpleFacetsCollector
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
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|TaxonomyFacetSumIntAssociations
argument_list|(
literal|"$facets.int"
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'a'!"
argument_list|,
literal|200
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"int"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'b'!"
argument_list|,
literal|150
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"int"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloatSumAssociation
specifier|public
name|void
name|testFloatSumAssociation
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFacetsCollector
name|fc
init|=
operator|new
name|SimpleFacetsCollector
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
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|TaxonomyFacetSumFloatAssociations
argument_list|(
literal|"$facets.float"
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'a'!"
argument_list|,
literal|50f
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"float"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'b'!"
argument_list|,
literal|10f
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"float"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
comment|/** Make sure we can test both int and float assocs in one    *  index, as long as we send each to a different field. */
DECL|method|testIntAndFloatAssocation
specifier|public
name|void
name|testIntAndFloatAssocation
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFacetsCollector
name|fc
init|=
operator|new
name|SimpleFacetsCollector
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
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|TaxonomyFacetSumFloatAssociations
argument_list|(
literal|"$facets.float"
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'a'!"
argument_list|,
literal|50f
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"float"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'b'!"
argument_list|,
literal|10f
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"float"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|facets
operator|=
operator|new
name|TaxonomyFacetSumIntAssociations
argument_list|(
literal|"$facets.int"
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'a'!"
argument_list|,
literal|200
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"int"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'b'!"
argument_list|,
literal|150
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"int"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrongIndexFieldName
specifier|public
name|void
name|testWrongIndexFieldName
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFacetsCollector
name|fc
init|=
operator|new
name|SimpleFacetsCollector
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
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|TaxonomyFacetSumFloatAssociations
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
try|try
block|{
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"float"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exc"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"float"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exc"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testMixedTypesInSameIndexField
specifier|public
name|void
name|testMixedTypesInSameIndexField
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
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
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
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
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
name|taxoWriter
argument_list|,
name|config
argument_list|)
decl_stmt|;
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
name|IntAssociationFacetField
argument_list|(
literal|14
argument_list|,
literal|"a"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FloatAssociationFacetField
argument_list|(
literal|55.0f
argument_list|,
literal|"b"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|exc
parameter_list|)
block|{
comment|// expected
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|taxoWriter
argument_list|,
name|dir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoHierarchy
specifier|public
name|void
name|testNoHierarchy
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
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
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setHierarchical
argument_list|(
literal|"a"
argument_list|,
literal|true
argument_list|)
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
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
name|taxoWriter
argument_list|,
name|config
argument_list|)
decl_stmt|;
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
name|IntAssociationFacetField
argument_list|(
literal|14
argument_list|,
literal|"a"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|exc
parameter_list|)
block|{
comment|// expected
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|taxoWriter
argument_list|,
name|dir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|testRequireDimCount
specifier|public
name|void
name|testRequireDimCount
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
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
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setRequireDimCount
argument_list|(
literal|"a"
argument_list|,
literal|true
argument_list|)
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
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
name|taxoWriter
argument_list|,
name|config
argument_list|)
decl_stmt|;
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
name|IntAssociationFacetField
argument_list|(
literal|14
argument_list|,
literal|"a"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|exc
parameter_list|)
block|{
comment|// expected
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|taxoWriter
argument_list|,
name|dir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

