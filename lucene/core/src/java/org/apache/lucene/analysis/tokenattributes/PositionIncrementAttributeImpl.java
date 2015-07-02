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
comment|/** Default implementation of {@link PositionIncrementAttribute}. */
end_comment

begin_class
DECL|class|PositionIncrementAttributeImpl
specifier|public
class|class
name|PositionIncrementAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|PositionIncrementAttribute
implements|,
name|Cloneable
block|{
DECL|field|positionIncrement
specifier|private
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
comment|/** Initialize this attribute with position increment of 1 */
DECL|method|PositionIncrementAttributeImpl
specifier|public
name|PositionIncrementAttributeImpl
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|setPositionIncrement
specifier|public
name|void
name|setPositionIncrement
parameter_list|(
name|int
name|positionIncrement
parameter_list|)
block|{
if|if
condition|(
name|positionIncrement
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Increment must be zero or greater: got "
operator|+
name|positionIncrement
argument_list|)
throw|;
block|}
name|this
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrement
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
name|positionIncrement
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
name|positionIncrement
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
name|PositionIncrementAttributeImpl
condition|)
block|{
name|PositionIncrementAttributeImpl
name|_other
init|=
operator|(
name|PositionIncrementAttributeImpl
operator|)
name|other
decl_stmt|;
return|return
name|positionIncrement
operator|==
name|_other
operator|.
name|positionIncrement
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
name|positionIncrement
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
name|PositionIncrementAttribute
name|t
init|=
operator|(
name|PositionIncrementAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|positionIncrement
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
name|PositionIncrementAttribute
operator|.
name|class
argument_list|,
literal|"positionIncrement"
argument_list|,
name|positionIncrement
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

