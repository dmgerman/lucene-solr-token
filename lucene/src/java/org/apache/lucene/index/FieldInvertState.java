begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|util
operator|.
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * This class tracks the number and position / offset parameters of terms  * being added to the index. The information collected in this class is  * also used to calculate the normalization factor for a field.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|FieldInvertState
specifier|public
specifier|final
class|class
name|FieldInvertState
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|position
name|int
name|position
decl_stmt|;
DECL|field|length
name|int
name|length
decl_stmt|;
DECL|field|numOverlap
name|int
name|numOverlap
decl_stmt|;
DECL|field|offset
name|int
name|offset
decl_stmt|;
DECL|field|maxTermFrequency
name|int
name|maxTermFrequency
decl_stmt|;
DECL|field|uniqueTermCount
name|int
name|uniqueTermCount
decl_stmt|;
DECL|field|boost
name|float
name|boost
decl_stmt|;
DECL|field|attributeSource
name|AttributeSource
name|attributeSource
decl_stmt|;
DECL|method|FieldInvertState
specifier|public
name|FieldInvertState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|FieldInvertState
specifier|public
name|FieldInvertState
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|numOverlap
parameter_list|,
name|int
name|offset
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|numOverlap
operator|=
name|numOverlap
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/**    * Re-initialize the state, using this boost value.    * @param docBoost boost value to use.    */
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|position
operator|=
literal|0
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|numOverlap
operator|=
literal|0
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|maxTermFrequency
operator|=
literal|0
expr_stmt|;
name|uniqueTermCount
operator|=
literal|0
expr_stmt|;
name|boost
operator|=
literal|1.0f
expr_stmt|;
name|attributeSource
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Get the last processed term position.    * @return the position    */
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
comment|/**    * Get total number of terms in this field.    * @return the length    */
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Get the number of terms with<code>positionIncrement == 0</code>.    * @return the numOverlap    */
DECL|method|getNumOverlap
specifier|public
name|int
name|getNumOverlap
parameter_list|()
block|{
return|return
name|numOverlap
return|;
block|}
DECL|method|setNumOverlap
specifier|public
name|void
name|setNumOverlap
parameter_list|(
name|int
name|numOverlap
parameter_list|)
block|{
name|this
operator|.
name|numOverlap
operator|=
name|numOverlap
expr_stmt|;
block|}
comment|/**    * Get end offset of the last processed term.    * @return the offset    */
DECL|method|getOffset
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**    * Get boost value. This is the cumulative product of    * document boost and field boost for all field instances    * sharing the same field name.    * @return the boost    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/**    * Get the maximum term-frequency encountered for any term in the field.  A    * field containing "the quick brown fox jumps over the lazy dog" would have    * a value of 2, because "the" appears twice.    */
DECL|method|getMaxTermFrequency
specifier|public
name|int
name|getMaxTermFrequency
parameter_list|()
block|{
return|return
name|maxTermFrequency
return|;
block|}
comment|/**    * Return the number of unique terms encountered in this field.    */
DECL|method|getUniqueTermCount
specifier|public
name|int
name|getUniqueTermCount
parameter_list|()
block|{
return|return
name|uniqueTermCount
return|;
block|}
DECL|method|getAttributeSource
specifier|public
name|AttributeSource
name|getAttributeSource
parameter_list|()
block|{
return|return
name|attributeSource
return|;
block|}
comment|/**    * Return the field's name    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

