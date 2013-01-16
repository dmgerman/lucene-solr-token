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
comment|/**  * A {@link ChunksIntEncoder} which encodes data in chunks of 8. Every group  * starts with a single byte (called indicator) which represents 8 - 1 bit  * flags, where the value:  *<ul>  *<li>1 means the encoded value is '1'  *<li>0 means the value is encoded using {@link VInt8IntEncoder}, and the  * encoded bytes follow the indicator.<br>  * Since value 0 is illegal, and 1 is encoded in the indicator, the actual value  * that is encoded is<code>value-2</code>, which saves some more bits.  *</ul>  * Encoding example:  *<ul>  *<li>Original values: 6, 16, 5, 9, 7, 1  *<li>After sorting: 1, 5, 6, 7, 9, 16  *<li>D-Gap computing: 1, 4, 1, 1, 2, 5 (so far - done by  * {@link DGapIntEncoder})  *<li>Encoding: 1,0,1,1,0,0,0,0 as the indicator, by 2 (4-2), 0 (2-2), 3 (5-2).  *<li>Binary encode:<u>0 | 0 | 0 | 0 | 1 | 1 | 0 | 1</u> 00000010 00000000  * 00000011 (indicator is<u>underlined</u>).<br>  *<b>NOTE:</b> the order of the values in the indicator is lsb&rArr; msb,  * which allows for more efficient decoding.  *</ul>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|EightFlagsIntEncoder
specifier|public
class|class
name|EightFlagsIntEncoder
extends|extends
name|ChunksIntEncoder
block|{
comment|/*    * Holds all combinations of<i>indicator</i> flags for fast encoding (saves    * time on bit manipulation at encode time)    */
DECL|field|ENCODE_TABLE
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|ENCODE_TABLE
init|=
operator|new
name|byte
index|[]
block|{
literal|0x1
block|,
literal|0x2
block|,
literal|0x4
block|,
literal|0x8
block|,
literal|0x10
block|,
literal|0x20
block|,
literal|0x40
block|,
operator|(
name|byte
operator|)
literal|0x80
block|}
decl_stmt|;
DECL|method|EightFlagsIntEncoder
specifier|public
name|EightFlagsIntEncoder
parameter_list|()
block|{
name|super
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
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
name|value
operator|==
literal|1
condition|)
block|{
name|indicator
operator||=
name|ENCODE_TABLE
index|[
name|ordinal
index|]
expr_stmt|;
block|}
else|else
block|{
name|encodeQueue
operator|.
name|ints
index|[
name|encodeQueue
operator|.
name|length
operator|++
index|]
operator|=
name|value
operator|-
literal|2
expr_stmt|;
block|}
operator|++
name|ordinal
expr_stmt|;
comment|// encode the chunk and the indicator
if|if
condition|(
name|ordinal
operator|==
literal|8
condition|)
block|{
name|encodeChunk
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
comment|// encode remaining values
if|if
condition|(
name|ordinal
operator|!=
literal|0
condition|)
block|{
name|encodeChunk
argument_list|(
name|buf
argument_list|)
expr_stmt|;
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
name|EightFlagsIntDecoder
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
literal|"EightFlags(VInt)"
return|;
block|}
block|}
end_class

end_unit

