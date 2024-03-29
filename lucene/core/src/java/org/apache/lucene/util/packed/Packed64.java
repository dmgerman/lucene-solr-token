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
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * Space optimized random access capable array of values with a fixed number of  * bits/value. Values are packed contiguously.  *<p>  * The implementation strives to perform as fast as possible under the  * constraint of contiguous bits, by avoiding expensive operations. This comes  * at the cost of code clarity.  *<p>  * Technical details: This implementation is a refinement of a non-branching  * version. The non-branching get and set methods meant that 2 or 4 atomics in  * the underlying array were always accessed, even for the cases where only  * 1 or 2 were needed. Even with caching, this had a detrimental effect on  * performance.  * Related to this issue, the old implementation used lookup tables for shifts  * and masks, which also proved to be a bit slower than calculating the shifts  * and masks on the fly.  * See https://issues.apache.org/jira/browse/LUCENE-4062 for details.  *  */
end_comment

begin_class
DECL|class|Packed64
class|class
name|Packed64
extends|extends
name|PackedInts
operator|.
name|MutableImpl
block|{
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|64
decl_stmt|;
comment|// 32 = int, 64 = long
DECL|field|BLOCK_BITS
specifier|static
specifier|final
name|int
name|BLOCK_BITS
init|=
literal|6
decl_stmt|;
comment|// The #bits representing BLOCK_SIZE
DECL|field|MOD_MASK
specifier|static
specifier|final
name|int
name|MOD_MASK
init|=
name|BLOCK_SIZE
operator|-
literal|1
decl_stmt|;
comment|// x % BLOCK_SIZE
comment|/**    * Values are stores contiguously in the blocks array.    */
DECL|field|blocks
specifier|private
specifier|final
name|long
index|[]
name|blocks
decl_stmt|;
comment|/**    * A right-aligned mask of width BitsPerValue used by {@link #get(int)}.    */
DECL|field|maskRight
specifier|private
specifier|final
name|long
name|maskRight
decl_stmt|;
comment|/**    * Optimization: Saves one lookup in {@link #get(int)}.    */
DECL|field|bpvMinusBlockSize
specifier|private
specifier|final
name|int
name|bpvMinusBlockSize
decl_stmt|;
comment|/**    * Creates an array with the internal structures adjusted for the given    * limits and initialized to 0.    * @param valueCount   the number of elements.    * @param bitsPerValue the number of bits available for any given value.    */
DECL|method|Packed64
specifier|public
name|Packed64
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Format
name|format
init|=
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
decl_stmt|;
specifier|final
name|int
name|longCount
init|=
name|format
operator|.
name|longCount
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|this
operator|.
name|blocks
operator|=
operator|new
name|long
index|[
name|longCount
index|]
expr_stmt|;
name|maskRight
operator|=
operator|~
literal|0L
operator|<<
operator|(
name|BLOCK_SIZE
operator|-
name|bitsPerValue
operator|)
operator|>>>
operator|(
name|BLOCK_SIZE
operator|-
name|bitsPerValue
operator|)
expr_stmt|;
name|bpvMinusBlockSize
operator|=
name|bitsPerValue
operator|-
name|BLOCK_SIZE
expr_stmt|;
block|}
comment|/**    * Creates an array with content retrieved from the given DataInput.    * @param in       a DataInput, positioned at the start of Packed64-content.    * @param valueCount  the number of elements.    * @param bitsPerValue the number of bits available for any given value.    * @throws java.io.IOException if the values for the backing array could not    *                             be retrieved.    */
DECL|method|Packed64
specifier|public
name|Packed64
parameter_list|(
name|int
name|packedIntsVersion
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Format
name|format
init|=
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
decl_stmt|;
specifier|final
name|long
name|byteCount
init|=
name|format
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
comment|// to know how much to read
specifier|final
name|int
name|longCount
init|=
name|format
operator|.
name|longCount
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
comment|// to size the array
name|blocks
operator|=
operator|new
name|long
index|[
name|longCount
index|]
expr_stmt|;
comment|// read as many longs as we can
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|byteCount
operator|/
literal|8
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|remaining
init|=
call|(
name|int
call|)
argument_list|(
name|byteCount
operator|%
literal|8
argument_list|)
decl_stmt|;
if|if
condition|(
name|remaining
operator|!=
literal|0
condition|)
block|{
comment|// read the last bytes
name|long
name|lastLong
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|remaining
condition|;
operator|++
name|i
control|)
block|{
name|lastLong
operator||=
operator|(
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFFL
operator|)
operator|<<
operator|(
literal|56
operator|-
name|i
operator|*
literal|8
operator|)
expr_stmt|;
block|}
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|lastLong
expr_stmt|;
block|}
name|maskRight
operator|=
operator|~
literal|0L
operator|<<
operator|(
name|BLOCK_SIZE
operator|-
name|bitsPerValue
operator|)
operator|>>>
operator|(
name|BLOCK_SIZE
operator|-
name|bitsPerValue
operator|)
expr_stmt|;
name|bpvMinusBlockSize
operator|=
name|bitsPerValue
operator|-
name|BLOCK_SIZE
expr_stmt|;
block|}
comment|/**    * @param index the position of the value.    * @return the value at the given index.    */
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
comment|// The abstract index in a bit stream
specifier|final
name|long
name|majorBitPos
init|=
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
decl_stmt|;
comment|// The index in the backing long-array
specifier|final
name|int
name|elementPos
init|=
call|(
name|int
call|)
argument_list|(
name|majorBitPos
operator|>>>
name|BLOCK_BITS
argument_list|)
decl_stmt|;
comment|// The number of value-bits in the second long
specifier|final
name|long
name|endBits
init|=
operator|(
name|majorBitPos
operator|&
name|MOD_MASK
operator|)
operator|+
name|bpvMinusBlockSize
decl_stmt|;
if|if
condition|(
name|endBits
operator|<=
literal|0
condition|)
block|{
comment|// Single block
return|return
operator|(
name|blocks
index|[
name|elementPos
index|]
operator|>>>
operator|-
name|endBits
operator|)
operator|&
name|maskRight
return|;
block|}
comment|// Two blocks
return|return
operator|(
operator|(
name|blocks
index|[
name|elementPos
index|]
operator|<<
name|endBits
operator|)
operator||
operator|(
name|blocks
index|[
name|elementPos
operator|+
literal|1
index|]
operator|>>>
operator|(
name|BLOCK_SIZE
operator|-
name|endBits
operator|)
operator|)
operator|)
operator|&
name|maskRight
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
operator|:
literal|"len must be> 0 (got "
operator|+
name|len
operator|+
literal|")"
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|valueCount
operator|-
name|index
argument_list|)
expr_stmt|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|originalIndex
init|=
name|index
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Decoder
name|decoder
init|=
name|BulkOperation
operator|.
name|of
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
comment|// go to the next block where the value does not span across two blocks
specifier|final
name|int
name|offsetInBlocks
init|=
name|index
operator|%
name|decoder
operator|.
name|longValueCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|offsetInBlocks
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offsetInBlocks
init|;
name|i
argument_list|<
name|decoder
operator|.
name|longValueCount
operator|(
operator|)
operator|&&
name|len
argument_list|>
literal|0
condition|;
operator|++
name|i
control|)
block|{
name|arr
index|[
name|off
operator|++
index|]
operator|=
name|get
argument_list|(
name|index
operator|++
argument_list|)
expr_stmt|;
operator|--
name|len
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
name|index
operator|-
name|originalIndex
return|;
block|}
block|}
comment|// bulk get
assert|assert
name|index
operator|%
name|decoder
operator|.
name|longValueCount
argument_list|()
operator|==
literal|0
assert|;
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
operator|)
operator|>>>
name|BLOCK_BITS
argument_list|)
decl_stmt|;
assert|assert
operator|(
operator|(
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
operator|)
operator|&
name|MOD_MASK
operator|)
operator|==
literal|0
assert|;
specifier|final
name|int
name|iterations
init|=
name|len
operator|/
name|decoder
operator|.
name|longValueCount
argument_list|()
decl_stmt|;
name|decoder
operator|.
name|decode
argument_list|(
name|blocks
argument_list|,
name|blockIndex
argument_list|,
name|arr
argument_list|,
name|off
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
specifier|final
name|int
name|gotValues
init|=
name|iterations
operator|*
name|decoder
operator|.
name|longValueCount
argument_list|()
decl_stmt|;
name|index
operator|+=
name|gotValues
expr_stmt|;
name|len
operator|-=
name|gotValues
expr_stmt|;
assert|assert
name|len
operator|>=
literal|0
assert|;
if|if
condition|(
name|index
operator|>
name|originalIndex
condition|)
block|{
comment|// stay at the block boundary
return|return
name|index
operator|-
name|originalIndex
return|;
block|}
else|else
block|{
comment|// no progress so far => already at a block boundary but no full block to get
assert|assert
name|index
operator|==
name|originalIndex
assert|;
return|return
name|super
operator|.
name|get
argument_list|(
name|index
argument_list|,
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
specifier|final
name|int
name|index
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
comment|// The abstract index in a contiguous bit stream
specifier|final
name|long
name|majorBitPos
init|=
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
decl_stmt|;
comment|// The index in the backing long-array
specifier|final
name|int
name|elementPos
init|=
call|(
name|int
call|)
argument_list|(
name|majorBitPos
operator|>>>
name|BLOCK_BITS
argument_list|)
decl_stmt|;
comment|// / BLOCK_SIZE
comment|// The number of value-bits in the second long
specifier|final
name|long
name|endBits
init|=
operator|(
name|majorBitPos
operator|&
name|MOD_MASK
operator|)
operator|+
name|bpvMinusBlockSize
decl_stmt|;
if|if
condition|(
name|endBits
operator|<=
literal|0
condition|)
block|{
comment|// Single block
name|blocks
index|[
name|elementPos
index|]
operator|=
name|blocks
index|[
name|elementPos
index|]
operator|&
operator|~
operator|(
name|maskRight
operator|<<
operator|-
name|endBits
operator|)
operator||
operator|(
name|value
operator|<<
operator|-
name|endBits
operator|)
expr_stmt|;
return|return;
block|}
comment|// Two blocks
name|blocks
index|[
name|elementPos
index|]
operator|=
name|blocks
index|[
name|elementPos
index|]
operator|&
operator|~
operator|(
name|maskRight
operator|>>>
name|endBits
operator|)
operator||
operator|(
name|value
operator|>>>
name|endBits
operator|)
expr_stmt|;
name|blocks
index|[
name|elementPos
operator|+
literal|1
index|]
operator|=
name|blocks
index|[
name|elementPos
operator|+
literal|1
index|]
operator|&
operator|(
operator|~
literal|0L
operator|>>>
name|endBits
operator|)
operator||
operator|(
name|value
operator|<<
operator|(
name|BLOCK_SIZE
operator|-
name|endBits
operator|)
operator|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|int
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
operator|:
literal|"len must be> 0 (got "
operator|+
name|len
operator|+
literal|")"
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|valueCount
operator|-
name|index
argument_list|)
expr_stmt|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|originalIndex
init|=
name|index
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Encoder
name|encoder
init|=
name|BulkOperation
operator|.
name|of
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
comment|// go to the next block where the value does not span across two blocks
specifier|final
name|int
name|offsetInBlocks
init|=
name|index
operator|%
name|encoder
operator|.
name|longValueCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|offsetInBlocks
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offsetInBlocks
init|;
name|i
argument_list|<
name|encoder
operator|.
name|longValueCount
operator|(
operator|)
operator|&&
name|len
argument_list|>
literal|0
condition|;
operator|++
name|i
control|)
block|{
name|set
argument_list|(
name|index
operator|++
argument_list|,
name|arr
index|[
name|off
operator|++
index|]
argument_list|)
expr_stmt|;
operator|--
name|len
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
name|index
operator|-
name|originalIndex
return|;
block|}
block|}
comment|// bulk set
assert|assert
name|index
operator|%
name|encoder
operator|.
name|longValueCount
argument_list|()
operator|==
literal|0
assert|;
name|int
name|blockIndex
init|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
operator|)
operator|>>>
name|BLOCK_BITS
argument_list|)
decl_stmt|;
assert|assert
operator|(
operator|(
operator|(
name|long
operator|)
name|index
operator|*
name|bitsPerValue
operator|)
operator|&
name|MOD_MASK
operator|)
operator|==
literal|0
assert|;
specifier|final
name|int
name|iterations
init|=
name|len
operator|/
name|encoder
operator|.
name|longValueCount
argument_list|()
decl_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|blocks
argument_list|,
name|blockIndex
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
specifier|final
name|int
name|setValues
init|=
name|iterations
operator|*
name|encoder
operator|.
name|longValueCount
argument_list|()
decl_stmt|;
name|index
operator|+=
name|setValues
expr_stmt|;
name|len
operator|-=
name|setValues
expr_stmt|;
assert|assert
name|len
operator|>=
literal|0
assert|;
if|if
condition|(
name|index
operator|>
name|originalIndex
condition|)
block|{
comment|// stay at the block boundary
return|return
name|index
operator|-
name|originalIndex
return|;
block|}
else|else
block|{
comment|// no progress so far => already at a block boundary but no full block to get
assert|assert
name|index
operator|==
name|originalIndex
assert|;
return|return
name|super
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
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
literal|"Packed64(bitsPerValue="
operator|+
name|bitsPerValue
operator|+
literal|",size="
operator|+
name|size
argument_list|()
operator|+
literal|",blocks="
operator|+
name|blocks
operator|.
name|length
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
literal|3
operator|*
name|Integer
operator|.
name|BYTES
comment|// bpvMinusBlockSize,valueCount,bitsPerValue
operator|+
name|Long
operator|.
name|BYTES
comment|// maskRight
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
comment|// blocks ref
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|blocks
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|public
name|void
name|fill
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|long
name|val
parameter_list|)
block|{
assert|assert
name|PackedInts
operator|.
name|unsignedBitsRequired
argument_list|(
name|val
argument_list|)
operator|<=
name|getBitsPerValue
argument_list|()
assert|;
assert|assert
name|fromIndex
operator|<=
name|toIndex
assert|;
comment|// minimum number of values that use an exact number of full blocks
specifier|final
name|int
name|nAlignedValues
init|=
literal|64
operator|/
name|gcd
argument_list|(
literal|64
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
specifier|final
name|int
name|span
init|=
name|toIndex
operator|-
name|fromIndex
decl_stmt|;
if|if
condition|(
name|span
operator|<=
literal|3
operator|*
name|nAlignedValues
condition|)
block|{
comment|// there needs be at least 2 * nAlignedValues aligned values for the
comment|// block approach to be worth trying
name|super
operator|.
name|fill
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// fill the first values naively until the next block start
specifier|final
name|int
name|fromIndexModNAlignedValues
init|=
name|fromIndex
operator|%
name|nAlignedValues
decl_stmt|;
if|if
condition|(
name|fromIndexModNAlignedValues
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|fromIndexModNAlignedValues
init|;
name|i
operator|<
name|nAlignedValues
condition|;
operator|++
name|i
control|)
block|{
name|set
argument_list|(
name|fromIndex
operator|++
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|fromIndex
operator|%
name|nAlignedValues
operator|==
literal|0
assert|;
comment|// compute the long[] blocks for nAlignedValues consecutive values and
comment|// use them to set as many values as possible without applying any mask
comment|// or shift
specifier|final
name|int
name|nAlignedBlocks
init|=
operator|(
name|nAlignedValues
operator|*
name|bitsPerValue
operator|)
operator|>>
literal|6
decl_stmt|;
specifier|final
name|long
index|[]
name|nAlignedValuesBlocks
decl_stmt|;
block|{
name|Packed64
name|values
init|=
operator|new
name|Packed64
argument_list|(
name|nAlignedValues
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nAlignedValues
condition|;
operator|++
name|i
control|)
block|{
name|values
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|nAlignedValuesBlocks
operator|=
name|values
operator|.
name|blocks
expr_stmt|;
assert|assert
name|nAlignedBlocks
operator|<=
name|nAlignedValuesBlocks
operator|.
name|length
assert|;
block|}
specifier|final
name|int
name|startBlock
init|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|long
operator|)
name|fromIndex
operator|*
name|bitsPerValue
operator|)
operator|>>>
literal|6
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endBlock
init|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|long
operator|)
name|toIndex
operator|*
name|bitsPerValue
operator|)
operator|>>>
literal|6
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|block
init|=
name|startBlock
init|;
name|block
operator|<
name|endBlock
condition|;
operator|++
name|block
control|)
block|{
specifier|final
name|long
name|blockValue
init|=
name|nAlignedValuesBlocks
index|[
name|block
operator|%
name|nAlignedBlocks
index|]
decl_stmt|;
name|blocks
index|[
name|block
index|]
operator|=
name|blockValue
expr_stmt|;
block|}
comment|// fill the gap
for|for
control|(
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|long
operator|)
name|endBlock
operator|<<
literal|6
operator|)
operator|/
name|bitsPerValue
argument_list|)
init|;
name|i
operator|<
name|toIndex
condition|;
operator|++
name|i
control|)
block|{
name|set
argument_list|(
name|i
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|gcd
specifier|private
specifier|static
name|int
name|gcd
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|<
name|b
condition|)
block|{
return|return
name|gcd
argument_list|(
name|b
argument_list|,
name|a
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
name|a
return|;
block|}
else|else
block|{
return|return
name|gcd
argument_list|(
name|b
argument_list|,
name|a
operator|%
name|b
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|blocks
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

