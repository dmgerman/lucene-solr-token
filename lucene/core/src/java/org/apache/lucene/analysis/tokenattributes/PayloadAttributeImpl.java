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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * The payload of a Token. See also {@link Payload}.  */
end_comment

begin_class
DECL|class|PayloadAttributeImpl
specifier|public
class|class
name|PayloadAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|PayloadAttribute
implements|,
name|Cloneable
block|{
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
comment|/**    * Initialize this attribute with no payload.    */
DECL|method|PayloadAttributeImpl
specifier|public
name|PayloadAttributeImpl
parameter_list|()
block|{}
comment|/**    * Initialize this attribute with the given payload.     */
DECL|method|PayloadAttributeImpl
specifier|public
name|PayloadAttributeImpl
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
comment|/**    * Returns this Token's payload.    */
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
return|return
name|this
operator|.
name|payload
return|;
block|}
comment|/**     * Sets this Token's payload.    */
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
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
name|payload
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|PayloadAttributeImpl
name|clone
parameter_list|()
block|{
name|PayloadAttributeImpl
name|clone
init|=
operator|(
name|PayloadAttributeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|payload
operator|=
name|payload
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
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
name|PayloadAttribute
condition|)
block|{
name|PayloadAttributeImpl
name|o
init|=
operator|(
name|PayloadAttributeImpl
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|o
operator|.
name|payload
operator|==
literal|null
operator|||
name|payload
operator|==
literal|null
condition|)
block|{
return|return
name|o
operator|.
name|payload
operator|==
literal|null
operator|&&
name|payload
operator|==
literal|null
return|;
block|}
return|return
name|o
operator|.
name|payload
operator|.
name|equals
argument_list|(
name|payload
argument_list|)
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
name|payload
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|payload
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
name|PayloadAttribute
name|t
init|=
operator|(
name|PayloadAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setPayload
argument_list|(
operator|(
name|payload
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|payload
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

