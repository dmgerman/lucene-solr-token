begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.enhancements.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|association
package|;
end_package

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
name|facet
operator|.
name|enhancements
operator|.
name|EnhancementsDocumentBuilder
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
name|enhancements
operator|.
name|params
operator|.
name|DefaultEnhancementsIndexingParams
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
name|enhancements
operator|.
name|params
operator|.
name|EnhancementsIndexingParams
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
name|CategoryContainer
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
name|attributes
operator|.
name|CategoryAttributeImpl
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
name|attributes
operator|.
name|CategoryProperty
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|CustomAssociationPropertyTest
specifier|public
class|class
name|CustomAssociationPropertyTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testCustomProperty
specifier|public
name|void
name|testCustomProperty
parameter_list|()
throws|throws
name|Exception
block|{
class|class
name|CustomProperty
extends|extends
name|AssociationIntProperty
block|{
specifier|public
name|CustomProperty
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|CategoryProperty
name|other
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
specifier|final
name|int
name|NUM_CATEGORIES
init|=
literal|10
decl_stmt|;
name|EnhancementsIndexingParams
name|iParams
init|=
operator|new
name|DefaultEnhancementsIndexingParams
argument_list|(
operator|new
name|AssociationEnhancement
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|iDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|tDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|iDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
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
name|DirectoryTaxonomyWriter
name|taxoW
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|tDir
argument_list|)
decl_stmt|;
name|CategoryContainer
name|cc
init|=
operator|new
name|CategoryContainer
argument_list|()
decl_stmt|;
name|EnhancementsDocumentBuilder
name|builder
init|=
operator|new
name|EnhancementsDocumentBuilder
argument_list|(
name|taxoW
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_CATEGORIES
condition|;
name|i
operator|++
control|)
block|{
name|CategoryAttributeImpl
name|ca
init|=
operator|new
name|CategoryAttributeImpl
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ca
operator|.
name|addProperty
argument_list|(
operator|new
name|CustomProperty
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|cc
operator|.
name|addCategory
argument_list|(
name|ca
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setCategories
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|taxoW
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|tDir
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|iParams
operator|.
name|getCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"0"
argument_list|)
argument_list|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
decl_stmt|;
name|AssociationsPayloadIterator
name|api
init|=
operator|new
name|AssociationsPayloadIterator
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|api
operator|.
name|setNextDoc
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|flag
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_CATEGORIES
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|taxo
operator|.
name|getOrdinal
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|flag
operator|=
literal|true
expr_stmt|;
name|long
name|association
init|=
name|api
operator|.
name|getAssociation
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Association expected for ordinal "
operator|+
name|ordinal
operator|+
literal|" but none was found"
argument_list|,
name|association
operator|<=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong association value for category '"
operator|+
name|i
operator|+
literal|"'"
argument_list|,
name|i
argument_list|,
operator|(
name|int
operator|)
name|association
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"No categories found for doc #0"
argument_list|,
name|flag
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
name|iDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|tDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

