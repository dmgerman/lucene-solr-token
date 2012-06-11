begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.streaming
package|package
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
name|streaming
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|facet
operator|.
name|index
operator|.
name|CategoryContainerTestBase
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
name|CategoryAttributesIterable
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
name|DefaultFacetIndexingParams
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
name|streaming
operator|.
name|CategoryAttributesStream
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
name|streaming
operator|.
name|CategoryTokenizer
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
name|DirectoryTaxonomyWriter
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|CategoryTokenizerTest
specifier|public
class|class
name|CategoryTokenizerTest
extends|extends
name|CategoryContainerTestBase
block|{
comment|/**    * Verifies that a {@link CategoryTokenizer} adds the correct    * {@link CharTermAttribute}s to a {@link CategoryAttributesStream}.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testTokensDefaultParams
specifier|public
name|void
name|testTokensDefaultParams
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|DefaultFacetIndexingParams
name|indexingParams
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
decl_stmt|;
name|CategoryTokenizer
name|tokenizer
init|=
operator|new
name|CategoryTokenizer
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|indexingParams
argument_list|)
decl_stmt|;
comment|// count the number of tokens
name|Set
argument_list|<
name|String
argument_list|>
name|categoryTerms
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|initialCatgeories
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|categoryTerms
operator|.
name|add
argument_list|(
name|initialCatgeories
index|[
name|i
index|]
operator|.
name|toString
argument_list|(
name|indexingParams
operator|.
name|getFacetDelimChar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|nTokens
decl_stmt|;
for|for
control|(
name|nTokens
operator|=
literal|0
init|;
name|tokenizer
operator|.
name|incrementToken
argument_list|()
condition|;
name|nTokens
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|categoryTerms
operator|.
name|remove
argument_list|(
name|tokenizer
operator|.
name|termAttribute
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Unexpected term: "
operator|+
name|tokenizer
operator|.
name|termAttribute
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"all category terms should have been found"
argument_list|,
name|categoryTerms
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// should be 6 - all categories and parents
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|3
argument_list|,
name|nTokens
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies that {@link CategoryTokenizer} elongates the buffer in    * {@link CharTermAttribute} for long categories.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testLongCategoryPath
specifier|public
name|void
name|testLongCategoryPath
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|longCategory
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
name|longCategory
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"three"
argument_list|,
literal|"four"
argument_list|,
literal|"five"
argument_list|,
literal|"six"
argument_list|,
literal|"seven"
argument_list|)
argument_list|)
expr_stmt|;
name|DefaultFacetIndexingParams
name|indexingParams
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
decl_stmt|;
name|CategoryTokenizer
name|tokenizer
init|=
operator|new
name|CategoryTokenizer
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
operator|new
name|CategoryAttributesIterable
argument_list|(
name|longCategory
argument_list|)
argument_list|)
argument_list|,
name|indexingParams
argument_list|)
decl_stmt|;
comment|// count the number of tokens
name|String
name|categoryTerm
init|=
name|longCategory
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|(
name|indexingParams
operator|.
name|getFacetDelimChar
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Missing token"
argument_list|,
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|categoryTerm
operator|.
name|equals
argument_list|(
name|tokenizer
operator|.
name|termAttribute
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Unexpected term: "
operator|+
name|tokenizer
operator|.
name|termAttribute
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"Unexpected token"
argument_list|,
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

