begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An {@link IntEncoder} which implements variable length encoding. A number is  * encoded as follows:  *<ul>  *<li>If it is less than 127 and non-negative, i.e. uses only 7 bits, it is  * encoded as a single byte: 0bbbbbbb.  *<li>If it occupies more than 7 bits, it is represented as a series of bytes,  * each byte carrying 7 bits. All but the last byte have the MSB set, the last  * one has it unset.  *</ul>  * Example:  *<ol>  *<li>n = 117 = 01110101: This has less than 8 significant bits, therefore is  * encoded as 01110101 = 0x75.  *<li>n = 100000 = (binary) 11000011010100000. This has 17 significant bits,  * thus needs three Vint8 bytes. Pad it to a multiple of 7 bits, then split it  * into chunks of 7 and add an MSB, 0 for the last byte, 1 for the others:  * 1|0000110 1|0001101 0|0100000 = 0x86 0x8D 0x20.  *</ol>  *<b>NOTE:</b> although this encoder is not limited to values&ge; 0, it is not  * recommended for use with negative values, as their encoding will result in 5  * bytes written to the output stream, rather than 4. For such values, either  * use {@link SimpleIntEncoder} or write your own version of variable length  * encoding, which can better handle negative values.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|VInt8IntEncoder
specifier|public
specifier|final
class|class
name|VInt8IntEncoder
extends|extends
name|IntEncoder
block|{
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|IntsRef
name|values
parameter_list|,
name|BytesRef
name|buf
parameter_list|)
block|{
name|buf
operator|.
name|offset
operator|=
name|buf
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|int
name|maxBytesNeeded
init|=
literal|5
operator|*
name|values
operator|.
name|length
decl_stmt|;
comment|// at most 5 bytes per VInt
if|if
condition|(
name|buf
operator|.
name|bytes
operator|.
name|length
operator|<
name|maxBytesNeeded
condition|)
block|{
name|buf
operator|.
name|grow
argument_list|(
name|maxBytesNeeded
argument_list|)
expr_stmt|;
block|}
name|int
name|upto
init|=
name|values
operator|.
name|offset
operator|+
name|values
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|values
operator|.
name|offset
init|;
name|i
operator|<
name|upto
condition|;
name|i
operator|++
control|)
block|{
comment|// it is better if the encoding is inlined like so, and not e.g.
comment|// in a utility method
name|int
name|value
init|=
name|values
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x7F
operator|)
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
name|buf
operator|.
name|length
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x3FFF
operator|)
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
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
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|buf
operator|.
name|length
operator|+=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x1FFFFF
operator|)
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
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
name|value
operator|&
literal|0x1FC000
operator|)
operator|>>
literal|14
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|1
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
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|buf
operator|.
name|length
operator|+=
literal|3
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0xFFFFFFF
operator|)
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
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
name|value
operator|&
literal|0xFE00000
operator|)
operator|>>
literal|21
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|1
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
name|value
operator|&
literal|0x1FC000
operator|)
operator|>>
literal|14
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|2
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
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|buf
operator|.
name|length
operator|+=
literal|4
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
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
name|value
operator|&
literal|0xF0000000
operator|)
operator|>>
literal|28
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|1
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
name|value
operator|&
literal|0xFE00000
operator|)
operator|>>
literal|21
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|2
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
name|value
operator|&
literal|0x1FC000
operator|)
operator|>>
literal|14
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|3
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
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|bytes
index|[
name|buf
operator|.
name|length
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|buf
operator|.
name|length
operator|+=
literal|5
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createMatchingDecoder
specifier|public
name|IntDecoder
name|createMatchingDecoder
parameter_list|()
block|{
return|return
operator|new
name|VInt8IntDecoder
argument_list|()
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
literal|"VInt8"
return|;
block|}
block|}
end_class

end_unit

