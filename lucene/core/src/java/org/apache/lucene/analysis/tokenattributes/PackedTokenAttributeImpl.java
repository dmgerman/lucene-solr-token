begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/** Default implementation of the common attributes used by Lucene:<ul>  *<li>{@link CharTermAttribute}  *<li>{@link TypeAttribute}  *<li>{@link PositionIncrementAttribute}  *<li>{@link PositionLengthAttribute}  *<li>{@link OffsetAttribute}  *<li>{@link TermFrequencyAttribute}  *</ul>*/
end_comment

begin_class
DECL|class|PackedTokenAttributeImpl
specifier|public
class|class
name|PackedTokenAttributeImpl
extends|extends
name|CharTermAttributeImpl
implements|implements
name|TypeAttribute
implements|,
name|PositionIncrementAttribute
implements|,
name|PositionLengthAttribute
implements|,
name|OffsetAttribute
implements|,
name|TermFrequencyAttribute
block|{
DECL|field|startOffset
DECL|field|endOffset
specifier|private
name|int
name|startOffset
decl_stmt|,
name|endOffset
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
init|=
name|DEFAULT_TYPE
decl_stmt|;
DECL|field|positionIncrement
specifier|private
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
DECL|field|positionLength
specifier|private
name|int
name|positionLength
init|=
literal|1
decl_stmt|;
DECL|field|termFrequency
specifier|private
name|int
name|termFrequency
init|=
literal|1
decl_stmt|;
comment|/** Constructs the attribute implementation. */
DECL|method|PackedTokenAttributeImpl
specifier|public
name|PackedTokenAttributeImpl
parameter_list|()
block|{   }
comment|/**    * {@inheritDoc}    * @see PositionIncrementAttribute    */
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
literal|"Increment must be zero or greater: "
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
comment|/**    * {@inheritDoc}    * @see PositionIncrementAttribute    */
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
comment|/**    * {@inheritDoc}    * @see PositionLengthAttribute    */
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
comment|/**    * {@inheritDoc}    * @see PositionLengthAttribute    */
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
comment|/**    * {@inheritDoc}    * @see OffsetAttribute    */
annotation|@
name|Override
DECL|method|startOffset
specifier|public
specifier|final
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/**    * {@inheritDoc}    * @see OffsetAttribute    */
annotation|@
name|Override
DECL|method|endOffset
specifier|public
specifier|final
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/**    * {@inheritDoc}    * @see OffsetAttribute    */
annotation|@
name|Override
DECL|method|setOffset
specifier|public
name|void
name|setOffset
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
if|if
condition|(
name|startOffset
operator|<
literal|0
operator|||
name|endOffset
operator|<
name|startOffset
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"startOffset must be non-negative, and endOffset must be>= startOffset; got "
operator|+
literal|"startOffset="
operator|+
name|startOffset
operator|+
literal|",endOffset="
operator|+
name|endOffset
argument_list|)
throw|;
block|}
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    * @see TypeAttribute    */
annotation|@
name|Override
DECL|method|type
specifier|public
specifier|final
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * {@inheritDoc}    * @see TypeAttribute    */
annotation|@
name|Override
DECL|method|setType
specifier|public
specifier|final
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
DECL|method|setTermFrequency
specifier|public
specifier|final
name|void
name|setTermFrequency
parameter_list|(
name|int
name|termFrequency
parameter_list|)
block|{
if|if
condition|(
name|termFrequency
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Term frequency must be 1 or greater; got "
operator|+
name|termFrequency
argument_list|)
throw|;
block|}
name|this
operator|.
name|termFrequency
operator|=
name|termFrequency
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermFrequency
specifier|public
specifier|final
name|int
name|getTermFrequency
parameter_list|()
block|{
return|return
name|termFrequency
return|;
block|}
comment|/** Resets the attributes    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|positionIncrement
operator|=
name|positionLength
operator|=
literal|1
expr_stmt|;
name|termFrequency
operator|=
literal|1
expr_stmt|;
name|startOffset
operator|=
name|endOffset
operator|=
literal|0
expr_stmt|;
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
block|}
comment|/** Resets the attributes at end    */
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// super.end already calls this.clear, so we only set values that are different from clear:
name|positionIncrement
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|PackedTokenAttributeImpl
name|clone
parameter_list|()
block|{
return|return
operator|(
name|PackedTokenAttributeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|PackedTokenAttributeImpl
condition|)
block|{
specifier|final
name|PackedTokenAttributeImpl
name|other
init|=
operator|(
name|PackedTokenAttributeImpl
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|startOffset
operator|==
name|other
operator|.
name|startOffset
operator|&&
name|endOffset
operator|==
name|other
operator|.
name|endOffset
operator|&&
name|positionIncrement
operator|==
name|other
operator|.
name|positionIncrement
operator|&&
name|positionLength
operator|==
name|other
operator|.
name|positionLength
operator|&&
operator|(
name|type
operator|==
literal|null
condition|?
name|other
operator|.
name|type
operator|==
literal|null
else|:
name|type
operator|.
name|equals
argument_list|(
name|other
operator|.
name|type
argument_list|)
operator|)
operator|&&
name|termFrequency
operator|==
name|other
operator|.
name|termFrequency
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|)
return|;
block|}
else|else
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
name|int
name|code
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|startOffset
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|endOffset
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|positionIncrement
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|positionLength
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|type
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|termFrequency
expr_stmt|;
empty_stmt|;
return|return
name|code
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
if|if
condition|(
name|target
operator|instanceof
name|PackedTokenAttributeImpl
condition|)
block|{
specifier|final
name|PackedTokenAttributeImpl
name|to
init|=
operator|(
name|PackedTokenAttributeImpl
operator|)
name|target
decl_stmt|;
name|to
operator|.
name|copyBuffer
argument_list|(
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|to
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
name|to
operator|.
name|positionLength
operator|=
name|positionLength
expr_stmt|;
name|to
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|to
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|to
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|to
operator|.
name|termFrequency
operator|=
name|termFrequency
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|copyTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
operator|(
operator|(
name|OffsetAttribute
operator|)
name|target
operator|)
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PositionIncrementAttribute
operator|)
name|target
operator|)
operator|.
name|setPositionIncrement
argument_list|(
name|positionIncrement
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PositionLengthAttribute
operator|)
name|target
operator|)
operator|.
name|setPositionLength
argument_list|(
name|positionLength
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TypeAttribute
operator|)
name|target
operator|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TermFrequencyAttribute
operator|)
name|target
operator|)
operator|.
name|setTermFrequency
argument_list|(
name|termFrequency
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|reflectWith
argument_list|(
name|reflector
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|,
literal|"startOffset"
argument_list|,
name|startOffset
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|,
literal|"endOffset"
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
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
name|reflector
operator|.
name|reflect
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|,
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|TermFrequencyAttribute
operator|.
name|class
argument_list|,
literal|"termFrequency"
argument_list|,
name|termFrequency
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

