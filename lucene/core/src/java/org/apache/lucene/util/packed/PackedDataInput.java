begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|store
operator|.
name|DataInput
import|;
end_import

begin_comment
comment|/**  * A {@link DataInput} wrapper to read unaligned, variable-length packed  * integers. This API is much slower than the {@link PackedInts} fixed-length  * API but can be convenient to save space.  * @see PackedDataOutput  * @lucene.internal  */
end_comment

begin_class
DECL|class|PackedDataInput
specifier|public
specifier|final
class|class
name|PackedDataInput
block|{
DECL|field|in
specifier|final
name|DataInput
name|in
decl_stmt|;
DECL|field|current
name|long
name|current
decl_stmt|;
DECL|field|remainingBits
name|int
name|remainingBits
decl_stmt|;
comment|/**    * Create a new instance that wraps<code>in</code>.    */
DECL|method|PackedDataInput
specifier|public
name|PackedDataInput
parameter_list|(
name|DataInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|skipToNextByte
argument_list|()
expr_stmt|;
block|}
comment|/**    * Read the next long using exactly<code>bitsPerValue</code> bits.    */
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
name|bitsPerValue
assert|;
name|long
name|r
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bitsPerValue
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|remainingBits
operator|==
literal|0
condition|)
block|{
name|current
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
expr_stmt|;
name|remainingBits
operator|=
literal|8
expr_stmt|;
block|}
specifier|final
name|int
name|bits
init|=
name|Math
operator|.
name|min
argument_list|(
name|bitsPerValue
argument_list|,
name|remainingBits
argument_list|)
decl_stmt|;
name|r
operator|=
operator|(
name|r
operator|<<
name|bits
operator|)
operator||
operator|(
operator|(
name|current
operator|>>>
operator|(
name|remainingBits
operator|-
name|bits
operator|)
operator|)
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|bits
operator|)
operator|-
literal|1
operator|)
operator|)
expr_stmt|;
name|bitsPerValue
operator|-=
name|bits
expr_stmt|;
name|remainingBits
operator|-=
name|bits
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
comment|/**    * If there are pending bits (at most 7), they will be ignored and the next    * value will be read starting at the next byte.    */
DECL|method|skipToNextByte
specifier|public
name|void
name|skipToNextByte
parameter_list|()
block|{
name|remainingBits
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

