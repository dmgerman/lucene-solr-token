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
name|AttributeReflector
import|;
end_import

begin_comment
comment|/** Default implementation of {@link PositionLengthAttribute}. */
end_comment

begin_class
DECL|class|PositionLengthAttributeImpl
specifier|public
class|class
name|PositionLengthAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|PositionLengthAttribute
implements|,
name|Cloneable
block|{
DECL|field|positionLength
specifier|private
name|int
name|positionLength
init|=
literal|1
decl_stmt|;
comment|/** Initializes this attribute with position length of 1. */
DECL|method|PositionLengthAttributeImpl
specifier|public
name|PositionLengthAttributeImpl
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|setPositionLength
specifier|public
name|void
name|setPositionLength
parameter_list|(
name|int
name|positionLength
parameter_list|)
block|{
if|if
condition|(
name|positionLength
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Position length must be 1 or greater: got "
operator|+
name|positionLength
argument_list|)
throw|;
block|}
name|this
operator|.
name|positionLength
operator|=
name|positionLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPositionLength
specifier|public
name|int
name|getPositionLength
parameter_list|()
block|{
return|return
name|positionLength
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|positionLength
operator|=
literal|1
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
name|PositionLengthAttributeImpl
condition|)
block|{
name|PositionLengthAttributeImpl
name|_other
init|=
operator|(
name|PositionLengthAttributeImpl
operator|)
name|other
decl_stmt|;
return|return
name|positionLength
operator|==
name|_other
operator|.
name|positionLength
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
name|positionLength
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
name|PositionLengthAttribute
name|t
init|=
operator|(
name|PositionLengthAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setPositionLength
argument_list|(
name|positionLength
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|reflector
operator|.
name|reflect
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|,
literal|"positionLength"
argument_list|,
name|positionLength
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

