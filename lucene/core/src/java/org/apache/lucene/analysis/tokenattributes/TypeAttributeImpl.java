begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
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
name|util
operator|.
name|AttributeImpl
import|;
end_import

begin_comment
comment|/** Default implementation of {@link TypeAttribute}. */
end_comment

begin_class
DECL|class|TypeAttributeImpl
specifier|public
class|class
name|TypeAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|TypeAttribute
implements|,
name|Cloneable
block|{
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
comment|/** Initialize this attribute with {@link TypeAttribute#DEFAULT_TYPE} */
DECL|method|TypeAttributeImpl
specifier|public
name|TypeAttributeImpl
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_TYPE
argument_list|)
expr_stmt|;
block|}
comment|/** Initialize this attribute with<code>type</code> */
DECL|method|TypeAttributeImpl
specifier|public
name|TypeAttributeImpl
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|TypeAttributeImpl
condition|)
block|{
specifier|final
name|TypeAttributeImpl
name|o
init|=
operator|(
name|TypeAttributeImpl
operator|)
name|other
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|type
operator|==
literal|null
condition|?
name|o
operator|.
name|type
operator|==
literal|null
else|:
name|this
operator|.
name|type
operator|.
name|equals
argument_list|(
name|o
operator|.
name|type
argument_list|)
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|type
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|type
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|TypeAttribute
name|t
init|=
operator|(
name|TypeAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

