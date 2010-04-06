begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*  * Some of this code came from the excellent Unicode  * conversion examples from:  *  *   http://www.unicode.org/Public/PROGRAMS/CVTUTF  *  * Full Copyright for that code follows: */
end_comment

begin_comment
comment|/*  * Copyright 2001-2004 Unicode, Inc.  *   * Disclaimer  *   * This source code is provided as is by Unicode, Inc. No claims are  * made as to fitness for any particular purpose. No warranties of any  * kind are expressed or implied. The recipient agrees to determine  * applicability of information provided. If this file has been  * purchased on magnetic or optical media from Unicode, Inc., the  * sole remedy for any claim will be exchange of defective media  * within 90 days of receipt.  *   * Limitations on Rights to Redistribute This Code  *   * Unicode, Inc. hereby grants the right to freely use the information  * supplied in this file in the creation of products supporting the  * Unicode Standard, and to make copies of this file in any form  * for internal or external distribution as long as this notice  * remains attached.  */
end_comment

begin_comment
comment|/**  * Class to encode java's UTF16 char[] into UTF8 byte[]  * without always allocating a new byte[] as  * String.getBytes("UTF-8") does.  *  *<p><b>WARNING</b>: This API is a new and experimental and  * may suddenly change.</p>  */
end_comment

begin_class
DECL|class|UnicodeUtil
specifier|final
specifier|public
class|class
name|UnicodeUtil
block|{
DECL|field|UNI_SUR_HIGH_START
specifier|public
specifier|static
specifier|final
name|int
name|UNI_SUR_HIGH_START
init|=
literal|0xD800
decl_stmt|;
DECL|field|UNI_SUR_HIGH_END
specifier|public
specifier|static
specifier|final
name|int
name|UNI_SUR_HIGH_END
init|=
literal|0xDBFF
decl_stmt|;
DECL|field|UNI_SUR_LOW_START
specifier|public
specifier|static
specifier|final
name|int
name|UNI_SUR_LOW_START
init|=
literal|0xDC00
decl_stmt|;
DECL|field|UNI_SUR_LOW_END
specifier|public
specifier|static
specifier|final
name|int
name|UNI_SUR_LOW_END
init|=
literal|0xDFFF
decl_stmt|;
DECL|field|UNI_REPLACEMENT_CHAR
specifier|public
specifier|static
specifier|final
name|int
name|UNI_REPLACEMENT_CHAR
init|=
literal|0xFFFD
decl_stmt|;
DECL|field|UNI_MAX_BMP
specifier|private
specifier|static
specifier|final
name|long
name|UNI_MAX_BMP
init|=
literal|0x0000FFFF
decl_stmt|;
DECL|field|HALF_BASE
specifier|private
specifier|static
specifier|final
name|int
name|HALF_BASE
init|=
literal|0x0010000
decl_stmt|;
DECL|field|HALF_SHIFT
specifier|private
specifier|static
specifier|final
name|long
name|HALF_SHIFT
init|=
literal|10
decl_stmt|;
DECL|field|HALF_MASK
specifier|private
specifier|static
specifier|final
name|long
name|HALF_MASK
init|=
literal|0x3FFL
decl_stmt|;
DECL|class|UTF8Result
specifier|public
specifier|static
specifier|final
class|class
name|UTF8Result
block|{
DECL|field|result
specifier|public
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
if|if
condition|(
name|result
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|byte
index|[]
name|newArray
init|=
operator|new
name|byte
index|[
call|(
name|int
call|)
argument_list|(
literal|1.5
operator|*
name|newLength
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|newArray
expr_stmt|;
block|}
name|length
operator|=
name|newLength
expr_stmt|;
block|}
block|}
DECL|class|UTF16Result
specifier|public
specifier|static
specifier|final
class|class
name|UTF16Result
block|{
DECL|field|result
specifier|public
name|char
index|[]
name|result
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
DECL|field|offsets
specifier|public
name|int
index|[]
name|offsets
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
if|if
condition|(
name|result
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|char
index|[]
name|newArray
init|=
operator|new
name|char
index|[
call|(
name|int
call|)
argument_list|(
literal|1.5
operator|*
name|newLength
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|newArray
expr_stmt|;
block|}
name|length
operator|=
name|newLength
expr_stmt|;
block|}
DECL|method|copyText
specifier|public
name|void
name|copyText
parameter_list|(
name|UTF16Result
name|other
parameter_list|)
block|{
name|setLength
argument_list|(
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|// stubs for tests only
DECL|method|UTF16toUTF8
specifier|public
specifier|static
name|void
name|UTF16toUTF8
parameter_list|(
name|char
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{}
DECL|method|UTF16toUTF8
specifier|public
specifier|static
name|void
name|UTF16toUTF8
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{}
comment|/** Encode characters from a char[] source, starting at    *  offset and stopping when the character 0xffff is seen.    *  Returns the number of bytes written to bytesOut. */
DECL|method|UTF16toUTF8
specifier|public
specifier|static
name|void
name|UTF16toUTF8
parameter_list|(
specifier|final
name|char
index|[]
name|source
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
name|UTF8Result
name|result
parameter_list|)
block|{
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
name|offset
decl_stmt|;
name|byte
index|[]
name|out
init|=
name|result
operator|.
name|result
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|source
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|upto
operator|+
literal|4
operator|>
name|out
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|newOut
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|out
operator|.
name|length
index|]
decl_stmt|;
assert|assert
name|newOut
operator|.
name|length
operator|>=
name|upto
operator|+
literal|4
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|newOut
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|result
operator|.
name|result
operator|=
name|out
operator|=
name|newOut
expr_stmt|;
block|}
if|if
condition|(
name|code
operator|<
literal|0x80
condition|)
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|code
expr_stmt|;
elseif|else
if|if
condition|(
name|code
operator|<
literal|0x800
condition|)
block|{
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
argument_list|<
literal|0xD800
operator|||
name|code
argument_list|>
literal|0xDFFF
condition|)
block|{
if|if
condition|(
name|code
operator|==
literal|0xffff
condition|)
comment|// END
break|break;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
name|code
operator|>>
literal|12
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// surrogate pair
comment|// confirm valid high surrogate
if|if
condition|(
name|code
operator|<
literal|0xDC00
operator|&&
name|source
index|[
name|i
index|]
operator|!=
literal|0xffff
condition|)
block|{
name|int
name|utf32
init|=
operator|(
name|int
operator|)
name|source
index|[
name|i
index|]
decl_stmt|;
comment|// confirm valid low surrogate and write pair
if|if
condition|(
name|utf32
operator|>=
literal|0xDC00
operator|&&
name|utf32
operator|<=
literal|0xDFFF
condition|)
block|{
name|utf32
operator|=
operator|(
operator|(
name|code
operator|-
literal|0xD7C0
operator|)
operator|<<
literal|10
operator|)
operator|+
operator|(
name|utf32
operator|&
literal|0x3FF
operator|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xF0
operator||
operator|(
name|utf32
operator|>>
literal|18
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|12
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|utf32
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// replace unpaired surrogate or out-of-order low surrogate
comment|// with substitution character
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xEF
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBF
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBD
expr_stmt|;
block|}
block|}
comment|//assert matches(source, offset, i-offset-1, out, upto);
name|result
operator|.
name|length
operator|=
name|upto
expr_stmt|;
block|}
comment|/** Encode characters from a char[] source, starting at    *  offset for length chars.  Returns the number of bytes    *  written to bytesOut. */
DECL|method|UTF16toUTF8
specifier|public
specifier|static
name|void
name|UTF16toUTF8
parameter_list|(
specifier|final
name|char
index|[]
name|source
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
name|UTF8Result
name|result
parameter_list|)
block|{
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
name|offset
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
name|byte
index|[]
name|out
init|=
name|result
operator|.
name|result
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|end
condition|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|source
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|upto
operator|+
literal|4
operator|>
name|out
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|newOut
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|out
operator|.
name|length
index|]
decl_stmt|;
assert|assert
name|newOut
operator|.
name|length
operator|>=
name|upto
operator|+
literal|4
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|newOut
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|result
operator|.
name|result
operator|=
name|out
operator|=
name|newOut
expr_stmt|;
block|}
if|if
condition|(
name|code
operator|<
literal|0x80
condition|)
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|code
expr_stmt|;
elseif|else
if|if
condition|(
name|code
operator|<
literal|0x800
condition|)
block|{
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
argument_list|<
literal|0xD800
operator|||
name|code
argument_list|>
literal|0xDFFF
condition|)
block|{
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
name|code
operator|>>
literal|12
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// surrogate pair
comment|// confirm valid high surrogate
if|if
condition|(
name|code
operator|<
literal|0xDC00
operator|&&
name|i
operator|<
name|end
operator|&&
name|source
index|[
name|i
index|]
operator|!=
literal|0xffff
condition|)
block|{
name|int
name|utf32
init|=
operator|(
name|int
operator|)
name|source
index|[
name|i
index|]
decl_stmt|;
comment|// confirm valid low surrogate and write pair
if|if
condition|(
name|utf32
operator|>=
literal|0xDC00
operator|&&
name|utf32
operator|<=
literal|0xDFFF
condition|)
block|{
name|utf32
operator|=
operator|(
operator|(
name|code
operator|-
literal|0xD7C0
operator|)
operator|<<
literal|10
operator|)
operator|+
operator|(
name|utf32
operator|&
literal|0x3FF
operator|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xF0
operator||
operator|(
name|utf32
operator|>>
literal|18
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|12
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|utf32
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// replace unpaired surrogate or out-of-order low surrogate
comment|// with substitution character
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xEF
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBF
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBD
expr_stmt|;
block|}
block|}
comment|//assert matches(source, offset, length, out, upto);
name|result
operator|.
name|length
operator|=
name|upto
expr_stmt|;
block|}
comment|/** Encode characters from this String, starting at offset    *  for length characters.  Returns the number of bytes    *  written to bytesOut. */
DECL|method|UTF16toUTF8
specifier|public
specifier|static
name|void
name|UTF16toUTF8
parameter_list|(
specifier|final
name|CharSequence
name|s
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
name|UTF8Result
name|result
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
name|byte
index|[]
name|out
init|=
name|result
operator|.
name|result
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|upto
operator|+
literal|4
operator|>
name|out
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|newOut
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|out
operator|.
name|length
index|]
decl_stmt|;
assert|assert
name|newOut
operator|.
name|length
operator|>=
name|upto
operator|+
literal|4
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|newOut
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|result
operator|.
name|result
operator|=
name|out
operator|=
name|newOut
expr_stmt|;
block|}
if|if
condition|(
name|code
operator|<
literal|0x80
condition|)
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|code
expr_stmt|;
elseif|else
if|if
condition|(
name|code
operator|<
literal|0x800
condition|)
block|{
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
argument_list|<
literal|0xD800
operator|||
name|code
argument_list|>
literal|0xDFFF
condition|)
block|{
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
name|code
operator|>>
literal|12
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// surrogate pair
comment|// confirm valid high surrogate
if|if
condition|(
name|code
operator|<
literal|0xDC00
operator|&&
operator|(
name|i
operator|<
name|end
operator|-
literal|1
operator|)
condition|)
block|{
name|int
name|utf32
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// confirm valid low surrogate and write pair
if|if
condition|(
name|utf32
operator|>=
literal|0xDC00
operator|&&
name|utf32
operator|<=
literal|0xDFFF
condition|)
block|{
name|utf32
operator|=
operator|(
operator|(
name|code
operator|-
literal|0xD7C0
operator|)
operator|<<
literal|10
operator|)
operator|+
operator|(
name|utf32
operator|&
literal|0x3FF
operator|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xF0
operator||
operator|(
name|utf32
operator|>>
literal|18
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|12
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|utf32
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// replace unpaired surrogate or out-of-order low surrogate
comment|// with substitution character
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xEF
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBF
expr_stmt|;
name|out
index|[
name|upto
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBD
expr_stmt|;
block|}
block|}
comment|//assert matches(s, offset, length, out, upto);
name|result
operator|.
name|length
operator|=
name|upto
expr_stmt|;
block|}
comment|/** Convert UTF8 bytes into UTF16 characters.  If offset    *  is non-zero, conversion starts at that starting point    *  in utf8, re-using the results from the previous call    *  up until offset. */
DECL|method|UTF8toUTF16
specifier|public
specifier|static
name|void
name|UTF8toUTF16
parameter_list|(
specifier|final
name|byte
index|[]
name|utf8
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|UTF16Result
name|result
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
name|char
index|[]
name|out
init|=
name|result
operator|.
name|result
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|offsets
operator|.
name|length
operator|<=
name|end
condition|)
block|{
name|int
index|[]
name|newOffsets
init|=
operator|new
name|int
index|[
literal|2
operator|*
name|end
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
operator|.
name|offsets
argument_list|,
literal|0
argument_list|,
name|newOffsets
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|offsets
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|offsets
operator|=
name|newOffsets
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|offsets
init|=
name|result
operator|.
name|offsets
decl_stmt|;
comment|// If incremental decoding fell in the middle of a
comment|// single unicode character, rollback to its start:
name|int
name|upto
init|=
name|offset
decl_stmt|;
while|while
condition|(
name|offsets
index|[
name|upto
index|]
operator|==
operator|-
literal|1
condition|)
name|upto
operator|--
expr_stmt|;
name|int
name|outUpto
init|=
name|offsets
index|[
name|upto
index|]
decl_stmt|;
comment|// Pre-allocate for worst case 1-for-1
if|if
condition|(
name|outUpto
operator|+
name|length
operator|>=
name|out
operator|.
name|length
condition|)
block|{
name|char
index|[]
name|newOut
init|=
operator|new
name|char
index|[
literal|2
operator|*
operator|(
name|outUpto
operator|+
name|length
operator|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|newOut
argument_list|,
literal|0
argument_list|,
name|outUpto
argument_list|)
expr_stmt|;
name|result
operator|.
name|result
operator|=
name|out
operator|=
name|newOut
expr_stmt|;
block|}
while|while
condition|(
name|upto
operator|<
name|end
condition|)
block|{
specifier|final
name|int
name|b
init|=
name|utf8
index|[
name|upto
index|]
operator|&
literal|0xff
decl_stmt|;
specifier|final
name|int
name|ch
decl_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
name|outUpto
expr_stmt|;
if|if
condition|(
name|b
operator|<
literal|0xc0
condition|)
block|{
assert|assert
name|b
operator|<
literal|0x80
assert|;
name|ch
operator|=
name|b
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|<
literal|0xe0
condition|)
block|{
name|ch
operator|=
operator|(
operator|(
name|b
operator|&
literal|0x1f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|utf8
index|[
name|upto
index|]
operator|&
literal|0x3f
operator|)
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|<
literal|0xf0
condition|)
block|{
name|ch
operator|=
operator|(
operator|(
name|b
operator|&
literal|0xf
operator|)
operator|<<
literal|12
operator|)
operator|+
operator|(
operator|(
name|utf8
index|[
name|upto
index|]
operator|&
literal|0x3f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|utf8
index|[
name|upto
operator|+
literal|1
index|]
operator|&
literal|0x3f
operator|)
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|b
operator|<
literal|0xf8
assert|;
name|ch
operator|=
operator|(
operator|(
name|b
operator|&
literal|0x7
operator|)
operator|<<
literal|18
operator|)
operator|+
operator|(
operator|(
name|utf8
index|[
name|upto
index|]
operator|&
literal|0x3f
operator|)
operator|<<
literal|12
operator|)
operator|+
operator|(
operator|(
name|utf8
index|[
name|upto
operator|+
literal|1
index|]
operator|&
literal|0x3f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|utf8
index|[
name|upto
operator|+
literal|2
index|]
operator|&
literal|0x3f
operator|)
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|ch
operator|<=
name|UNI_MAX_BMP
condition|)
block|{
comment|// target is a character<= 0xFFFF
name|out
index|[
name|outUpto
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|ch
expr_stmt|;
block|}
else|else
block|{
comment|// target is a character in range 0xFFFF - 0x10FFFF
specifier|final
name|int
name|chHalf
init|=
name|ch
operator|-
name|HALF_BASE
decl_stmt|;
name|out
index|[
name|outUpto
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|chHalf
operator|>>
name|HALF_SHIFT
operator|)
operator|+
name|UNI_SUR_HIGH_START
argument_list|)
expr_stmt|;
name|out
index|[
name|outUpto
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|chHalf
operator|&
name|HALF_MASK
operator|)
operator|+
name|UNI_SUR_LOW_START
argument_list|)
expr_stmt|;
block|}
block|}
name|offsets
index|[
name|upto
index|]
operator|=
name|outUpto
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|outUpto
expr_stmt|;
block|}
comment|// Only called from assert
comment|/*   private static boolean matches(char[] source, int offset, int length, byte[] result, int upto) {     try {       String s1 = new String(source, offset, length);       String s2 = new String(result, 0, upto, "UTF-8");       if (!s1.equals(s2)) {         //System.out.println("DIFF: s1 len=" + s1.length());         //for(int i=0;i<s1.length();i++)         //  System.out.println("    " + i + ": " + (int) s1.charAt(i));         //System.out.println("s2 len=" + s2.length());         //for(int i=0;i<s2.length();i++)         //  System.out.println("    " + i + ": " + (int) s2.charAt(i));          // If the input string was invalid, then the         // difference is OK         if (!validUTF16String(s1))           return true;          return false;       }       return s1.equals(s2);     } catch (UnsupportedEncodingException uee) {       return false;     }   }    // Only called from assert   private static boolean matches(String source, int offset, int length, byte[] result, int upto) {     try {       String s1 = source.substring(offset, offset+length);       String s2 = new String(result, 0, upto, "UTF-8");       if (!s1.equals(s2)) {         // Allow a difference if s1 is not valid UTF-16          //System.out.println("DIFF: s1 len=" + s1.length());         //for(int i=0;i<s1.length();i++)         //  System.out.println("    " + i + ": " + (int) s1.charAt(i));         //System.out.println("  s2 len=" + s2.length());         //for(int i=0;i<s2.length();i++)         //  System.out.println("    " + i + ": " + (int) s2.charAt(i));          // If the input string was invalid, then the         // difference is OK         if (!validUTF16String(s1))           return true;          return false;       }       return s1.equals(s2);     } catch (UnsupportedEncodingException uee) {       return false;     }   }    public static final boolean validUTF16String(String s) {     final int size = s.length();     for(int i=0;i<size;i++) {       char ch = s.charAt(i);       if (ch>= UNI_SUR_HIGH_START&& ch<= UNI_SUR_HIGH_END) {         if (i< size-1) {           i++;           char nextCH = s.charAt(i);           if (nextCH>= UNI_SUR_LOW_START&& nextCH<= UNI_SUR_LOW_END) {             // Valid surrogate pair           } else             // Unmatched high surrogate             return false;         } else           // Unmatched high surrogate           return false;       } else if (ch>= UNI_SUR_LOW_START&& ch<= UNI_SUR_LOW_END)         // Unmatched low surrogate         return false;     }      return true;   }    public static final boolean validUTF16String(char[] s, int size) {     for(int i=0;i<size;i++) {       char ch = s[i];       if (ch>= UNI_SUR_HIGH_START&& ch<= UNI_SUR_HIGH_END) {         if (i< size-1) {           i++;           char nextCH = s[i];           if (nextCH>= UNI_SUR_LOW_START&& nextCH<= UNI_SUR_LOW_END) {             // Valid surrogate pair           } else             return false;         } else           return false;       } else if (ch>= UNI_SUR_LOW_START&& ch<= UNI_SUR_LOW_END)         // Unmatched low surrogate         return false;     }      return true;   }   */
block|}
end_class

end_unit

