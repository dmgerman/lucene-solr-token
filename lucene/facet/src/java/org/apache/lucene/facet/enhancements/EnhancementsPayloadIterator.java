begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.enhancements
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
name|facet
operator|.
name|search
operator|.
name|PayloadIterator
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
name|Vint8
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
name|Vint8
operator|.
name|Position
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link PayloadIterator} for iterating over category posting lists generated  * using {@link EnhancementsCategoryTokenizer}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|EnhancementsPayloadIterator
specifier|public
class|class
name|EnhancementsPayloadIterator
extends|extends
name|PayloadIterator
block|{
DECL|field|EnhancedCategories
specifier|private
name|CategoryEnhancement
index|[]
name|EnhancedCategories
decl_stmt|;
DECL|field|nEnhancements
name|int
name|nEnhancements
decl_stmt|;
DECL|field|enhancementLength
specifier|private
name|int
index|[]
name|enhancementLength
decl_stmt|;
DECL|field|enhancementStart
specifier|private
name|int
index|[]
name|enhancementStart
decl_stmt|;
comment|/**    * Constructor.    *     * @param enhancementsList    *            A list of the {@link CategoryEnhancement}s from the indexing    *            params.    * @param indexReader    *            A reader of the index.    * @param term    *            The category term to iterate.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|EnhancementsPayloadIterator
specifier|public
name|EnhancementsPayloadIterator
parameter_list|(
name|List
argument_list|<
name|CategoryEnhancement
argument_list|>
name|enhancementsList
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|indexReader
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|EnhancedCategories
operator|=
name|enhancementsList
operator|.
name|toArray
argument_list|(
operator|new
name|CategoryEnhancement
index|[
name|enhancementsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|enhancementLength
operator|=
operator|new
name|int
index|[
name|EnhancedCategories
operator|.
name|length
index|]
expr_stmt|;
name|enhancementStart
operator|=
operator|new
name|int
index|[
name|EnhancedCategories
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setdoc
specifier|public
name|boolean
name|setdoc
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|setdoc
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// read header - number of enhancements and their lengths
name|Position
name|position
init|=
operator|new
name|Position
argument_list|(
name|data
operator|.
name|offset
argument_list|)
decl_stmt|;
name|nEnhancements
operator|=
name|Vint8
operator|.
name|decode
argument_list|(
name|data
operator|.
name|bytes
argument_list|,
name|position
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
name|nEnhancements
condition|;
name|i
operator|++
control|)
block|{
name|enhancementLength
index|[
name|i
index|]
operator|=
name|Vint8
operator|.
name|decode
argument_list|(
name|data
operator|.
name|bytes
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
comment|// set enhancements start points
name|enhancementStart
index|[
literal|0
index|]
operator|=
name|position
operator|.
name|pos
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nEnhancements
condition|;
name|i
operator|++
control|)
block|{
name|enhancementStart
index|[
name|i
index|]
operator|=
name|enhancementStart
index|[
name|i
operator|-
literal|1
index|]
operator|+
name|enhancementLength
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Get the data of the current category and document for a certain    * enhancement, or {@code null} if no such enhancement exists.    *     * @param enhancedCategory    *            The category enhancement to apply.    * @return the data of the current category and document for a certain    *         enhancement, or {@code null} if no such enhancement exists.    */
DECL|method|getCategoryData
specifier|public
name|Object
name|getCategoryData
parameter_list|(
name|CategoryEnhancement
name|enhancedCategory
parameter_list|)
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
name|nEnhancements
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|enhancedCategory
operator|.
name|equals
argument_list|(
name|EnhancedCategories
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
name|enhancedCategory
operator|.
name|extractCategoryTokenData
argument_list|(
name|data
operator|.
name|bytes
argument_list|,
name|enhancementStart
index|[
name|i
index|]
argument_list|,
name|enhancementLength
index|[
name|i
index|]
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

