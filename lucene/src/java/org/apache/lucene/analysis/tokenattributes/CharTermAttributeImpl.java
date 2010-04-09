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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
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
name|ArrayUtil
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
name|RamUsageEstimator
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * The term text of a Token.  */
end_comment

begin_class
DECL|class|CharTermAttributeImpl
specifier|public
class|class
name|CharTermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|CharTermAttribute
implements|,
name|TermAttribute
implements|,
name|TermToBytesRefAttribute
implements|,
name|Cloneable
implements|,
name|Serializable
block|{
DECL|field|MIN_BUFFER_SIZE
specifier|private
specifier|static
name|int
name|MIN_BUFFER_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|termBuffer
specifier|private
name|char
index|[]
name|termBuffer
init|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
decl_stmt|;
DECL|field|termLength
specifier|private
name|int
name|termLength
init|=
literal|0
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|term
specifier|public
name|String
name|term
parameter_list|()
block|{
comment|// don't delegate to toString() here!
return|return
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
return|;
block|}
DECL|method|copyBuffer
specifier|public
name|void
name|copyBuffer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|growTermBuffer
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|copyBuffer
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|String
name|buffer
parameter_list|)
block|{
name|int
name|length
init|=
name|buffer
operator|.
name|length
argument_list|()
decl_stmt|;
name|growTermBuffer
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|length
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|String
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
assert|assert
name|offset
operator|<=
name|buffer
operator|.
name|length
argument_list|()
assert|;
assert|assert
name|offset
operator|+
name|length
operator|<=
name|buffer
operator|.
name|length
argument_list|()
assert|;
name|growTermBuffer
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|getChars
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
DECL|method|buffer
specifier|public
name|char
index|[]
name|buffer
parameter_list|()
block|{
return|return
name|termBuffer
return|;
block|}
annotation|@
name|Deprecated
DECL|method|termBuffer
specifier|public
name|char
index|[]
name|termBuffer
parameter_list|()
block|{
return|return
name|termBuffer
return|;
block|}
DECL|method|resizeBuffer
specifier|public
name|char
index|[]
name|resizeBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
if|if
condition|(
name|termBuffer
operator|.
name|length
operator|<
name|newSize
condition|)
block|{
comment|// Not big enough; create a new array with slight
comment|// over allocation and preserve content
specifier|final
name|char
index|[]
name|newCharBuffer
init|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|newSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|newCharBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|termBuffer
operator|=
name|newCharBuffer
expr_stmt|;
block|}
return|return
name|termBuffer
return|;
block|}
annotation|@
name|Deprecated
DECL|method|resizeTermBuffer
specifier|public
name|char
index|[]
name|resizeTermBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
return|return
name|resizeBuffer
argument_list|(
name|newSize
argument_list|)
return|;
block|}
DECL|method|growTermBuffer
specifier|private
name|void
name|growTermBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
if|if
condition|(
name|termBuffer
operator|.
name|length
operator|<
name|newSize
condition|)
block|{
comment|// Not big enough; create a new array with slight
comment|// over allocation:
name|termBuffer
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|newSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
expr_stmt|;
block|}
block|}
annotation|@
name|Deprecated
DECL|method|termLength
specifier|public
name|int
name|termLength
parameter_list|()
block|{
return|return
name|termLength
return|;
block|}
DECL|method|setLength
specifier|public
name|CharTermAttribute
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|>
name|termBuffer
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length "
operator|+
name|length
operator|+
literal|" exceeds the size of the termBuffer ("
operator|+
name|termBuffer
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
name|termLength
operator|=
name|length
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setEmpty
specifier|public
name|CharTermAttribute
name|setEmpty
parameter_list|()
block|{
name|termLength
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Deprecated
DECL|method|setTermLength
specifier|public
name|void
name|setTermLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
comment|// *** TermToBytesRefAttribute interface ***
DECL|method|toBytesRef
specifier|public
name|int
name|toBytesRef
parameter_list|(
name|BytesRef
name|target
parameter_list|)
block|{
comment|// TODO: Maybe require that bytes is already initialized? TermsHashPerField ensures this.
if|if
condition|(
name|target
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
name|target
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|termLength
operator|*
literal|4
index|]
expr_stmt|;
block|}
return|return
name|UnicodeUtil
operator|.
name|UTF16toUTF8WithHash
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|,
name|target
argument_list|)
return|;
block|}
comment|// *** CharSequence interface ***
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|termLength
return|;
block|}
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>=
name|termLength
condition|)
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
return|return
name|termBuffer
index|[
name|index
index|]
return|;
block|}
DECL|method|subSequence
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|end
parameter_list|)
block|{
if|if
condition|(
name|start
operator|>
name|termLength
operator|||
name|end
operator|>
name|termLength
condition|)
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
return|return
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
comment|// *** Appendable interface ***
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|)
block|{
return|return
name|append
argument_list|(
name|csq
argument_list|,
literal|0
argument_list|,
name|csq
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|CharSequence
name|csq
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|resizeBuffer
argument_list|(
name|termLength
operator|+
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|csq
operator|instanceof
name|String
condition|)
block|{
operator|(
operator|(
name|String
operator|)
name|csq
operator|)
operator|.
name|getChars
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|termBuffer
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|csq
operator|instanceof
name|StringBuilder
condition|)
block|{
operator|(
operator|(
name|StringBuilder
operator|)
name|csq
operator|)
operator|.
name|getChars
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|termBuffer
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|csq
operator|instanceof
name|StringBuffer
condition|)
block|{
operator|(
operator|(
name|StringBuffer
operator|)
name|csq
operator|)
operator|.
name|getChars
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|termBuffer
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|csq
operator|instanceof
name|CharBuffer
operator|&&
operator|(
operator|(
name|CharBuffer
operator|)
name|csq
operator|)
operator|.
name|hasArray
argument_list|()
condition|)
block|{
specifier|final
name|CharBuffer
name|cb
init|=
operator|(
name|CharBuffer
operator|)
name|csq
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|cb
operator|.
name|array
argument_list|()
argument_list|,
name|cb
operator|.
name|arrayOffset
argument_list|()
operator|+
name|cb
operator|.
name|position
argument_list|()
operator|+
name|start
argument_list|,
name|termBuffer
argument_list|,
name|termLength
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|start
operator|<
name|end
condition|)
name|termBuffer
index|[
name|termLength
operator|++
index|]
operator|=
name|csq
operator|.
name|charAt
argument_list|(
name|start
operator|++
argument_list|)
expr_stmt|;
comment|// no fall-through here, as termLength is updated!
return|return
name|this
return|;
block|}
name|termLength
operator|+=
name|end
operator|-
name|start
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|append
specifier|public
name|CharTermAttribute
name|append
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|resizeBuffer
argument_list|(
name|termLength
operator|+
literal|1
argument_list|)
index|[
name|termLength
operator|++
index|]
operator|=
name|c
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// *** AttributeImpl ***
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
name|termLength
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|ArrayUtil
operator|.
name|hashCode
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
return|return
name|code
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
name|termLength
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|CharTermAttributeImpl
name|t
init|=
operator|(
name|CharTermAttributeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Do a deep clone
name|t
operator|.
name|termBuffer
operator|=
name|termBuffer
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|t
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
name|CharTermAttributeImpl
condition|)
block|{
specifier|final
name|CharTermAttributeImpl
name|o
init|=
operator|(
operator|(
name|CharTermAttributeImpl
operator|)
name|other
operator|)
decl_stmt|;
if|if
condition|(
name|termLength
operator|!=
name|o
operator|.
name|termLength
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termLength
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termBuffer
index|[
name|i
index|]
operator|!=
name|o
operator|.
name|termBuffer
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
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
name|CharTermAttribute
condition|)
block|{
name|CharTermAttribute
name|t
init|=
operator|(
name|CharTermAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|copyBuffer
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TermAttribute
name|t
init|=
operator|(
name|TermAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

