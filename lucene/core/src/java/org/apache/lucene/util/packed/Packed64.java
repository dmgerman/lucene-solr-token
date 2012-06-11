begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Space optimized random access capable array of values with a fixed number of  * bits. For 32 bits/value and less, performance on 32 bit machines is not  * optimal. Consider using {@link Packed32} for such a setup.  *</p><p>  * The implementation strives to avoid conditionals and expensive operations,  * sacrificing code clarity to achieve better performance.  */
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
DECL|field|ENTRY_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|ENTRY_SIZE
init|=
name|BLOCK_SIZE
operator|+
literal|1
decl_stmt|;
DECL|field|FAC_BITPOS
specifier|static
specifier|final
name|int
name|FAC_BITPOS
init|=
literal|3
decl_stmt|;
comment|/*    * In order to make an efficient value-getter, conditionals should be    * avoided. A value can be positioned inside of a block, requiring shifting    * left or right or it can span two blocks, requiring a left-shift on the    * first block and a right-shift on the right block.    *</p><p>    * By always shifting the first block both left and right, we get exactly    * the right bits. By always shifting the second block right and applying    * a mask, we get the right bits there. After that, we | the two bitsets.   */
DECL|field|SHIFTS
specifier|static
specifier|final
name|int
index|[]
index|[]
name|SHIFTS
init|=
operator|new
name|int
index|[
name|ENTRY_SIZE
index|]
index|[
name|ENTRY_SIZE
operator|*
name|FAC_BITPOS
index|]
decl_stmt|;
DECL|field|MASKS
specifier|static
specifier|final
name|long
index|[]
index|[]
name|MASKS
init|=
operator|new
name|long
index|[
name|ENTRY_SIZE
index|]
index|[
name|ENTRY_SIZE
index|]
decl_stmt|;
static|static
block|{
comment|// Generate shifts
for|for
control|(
name|int
name|elementBits
init|=
literal|1
init|;
name|elementBits
operator|<=
name|BLOCK_SIZE
condition|;
name|elementBits
operator|++
control|)
block|{
for|for
control|(
name|int
name|bitPos
init|=
literal|0
init|;
name|bitPos
operator|<
name|BLOCK_SIZE
condition|;
name|bitPos
operator|++
control|)
block|{
name|int
index|[]
name|currentShifts
init|=
name|SHIFTS
index|[
name|elementBits
index|]
decl_stmt|;
name|int
name|base
init|=
name|bitPos
operator|*
name|FAC_BITPOS
decl_stmt|;
name|currentShifts
index|[
name|base
index|]
operator|=
name|bitPos
expr_stmt|;
name|currentShifts
index|[
name|base
operator|+
literal|1
index|]
operator|=
name|BLOCK_SIZE
operator|-
name|elementBits
expr_stmt|;
if|if
condition|(
name|bitPos
operator|<=
name|BLOCK_SIZE
operator|-
name|elementBits
condition|)
block|{
comment|// Single block
name|currentShifts
index|[
name|base
operator|+
literal|2
index|]
operator|=
literal|0
expr_stmt|;
name|MASKS
index|[
name|elementBits
index|]
index|[
name|bitPos
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// Two blocks
name|int
name|rBits
init|=
name|elementBits
operator|-
operator|(
name|BLOCK_SIZE
operator|-
name|bitPos
operator|)
decl_stmt|;
name|currentShifts
index|[
name|base
operator|+
literal|2
index|]
operator|=
name|BLOCK_SIZE
operator|-
name|rBits
expr_stmt|;
name|MASKS
index|[
name|elementBits
index|]
index|[
name|bitPos
index|]
operator|=
operator|~
operator|(
operator|~
literal|0L
operator|<<
name|rBits
operator|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/*    * The setter requires more masking than the getter.   */
DECL|field|WRITE_MASKS
specifier|private
specifier|static
specifier|final
name|long
index|[]
index|[]
name|WRITE_MASKS
init|=
operator|new
name|long
index|[
name|ENTRY_SIZE
index|]
index|[
name|ENTRY_SIZE
operator|*
name|FAC_BITPOS
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|elementBits
init|=
literal|1
init|;
name|elementBits
operator|<=
name|BLOCK_SIZE
condition|;
name|elementBits
operator|++
control|)
block|{
name|long
name|elementPosMask
init|=
operator|~
operator|(
operator|~
literal|0L
operator|<<
name|elementBits
operator|)
decl_stmt|;
name|int
index|[]
name|currentShifts
init|=
name|SHIFTS
index|[
name|elementBits
index|]
decl_stmt|;
name|long
index|[]
name|currentMasks
init|=
name|WRITE_MASKS
index|[
name|elementBits
index|]
decl_stmt|;
for|for
control|(
name|int
name|bitPos
init|=
literal|0
init|;
name|bitPos
operator|<
name|BLOCK_SIZE
condition|;
name|bitPos
operator|++
control|)
block|{
name|int
name|base
init|=
name|bitPos
operator|*
name|FAC_BITPOS
decl_stmt|;
name|currentMasks
index|[
name|base
index|]
operator|=
operator|~
operator|(
operator|(
name|elementPosMask
operator|<<
name|currentShifts
index|[
name|base
operator|+
literal|1
index|]
operator|)
operator|>>>
name|currentShifts
index|[
name|base
index|]
operator|)
expr_stmt|;
if|if
condition|(
name|bitPos
operator|<=
name|BLOCK_SIZE
operator|-
name|elementBits
condition|)
block|{
comment|// Second block not used
name|currentMasks
index|[
name|base
operator|+
literal|1
index|]
operator|=
operator|~
literal|0
expr_stmt|;
comment|// Keep all bits
name|currentMasks
index|[
name|base
operator|+
literal|2
index|]
operator|=
literal|0
expr_stmt|;
comment|// Or with 0
block|}
else|else
block|{
name|currentMasks
index|[
name|base
operator|+
literal|1
index|]
operator|=
operator|~
operator|(
name|elementPosMask
operator|<<
name|currentShifts
index|[
name|base
operator|+
literal|2
index|]
operator|)
expr_stmt|;
name|currentMasks
index|[
name|base
operator|+
literal|2
index|]
operator|=
name|currentShifts
index|[
name|base
operator|+
literal|2
index|]
operator|==
literal|0
condition|?
literal|0
else|:
operator|~
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|pgcd
specifier|private
specifier|static
name|int
name|pgcd
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
name|pgcd
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
name|pgcd
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
comment|/* The bits */
DECL|field|blocks
specifier|private
specifier|final
name|long
index|[]
name|blocks
decl_stmt|;
comment|// Cached calculations
DECL|field|maxPos
specifier|private
name|int
name|maxPos
decl_stmt|;
comment|// blocks.length * BLOCK_SIZE / elementBits - 1
DECL|field|shifts
specifier|private
name|int
index|[]
name|shifts
decl_stmt|;
comment|// The shifts for the current elementBits
DECL|field|readMasks
specifier|private
name|long
index|[]
name|readMasks
decl_stmt|;
DECL|field|writeMasks
specifier|private
name|long
index|[]
name|writeMasks
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
comment|// TODO: Test for edge-cases (2^31 values, 63 bitsPerValue)
comment|// +2 due to the avoid-conditionals-trick. The last entry is always 0
name|this
argument_list|(
operator|new
name|long
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|long
operator|)
name|valueCount
operator|*
name|bitsPerValue
operator|/
name|BLOCK_SIZE
operator|+
literal|2
argument_list|)
index|]
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an array backed by the given blocks.    *</p><p>    * Note: The blocks are used directly, so changes to the given block will    * affect the Packed32-structure.    * @param blocks   used as the internal backing array. Not that the last    *                 element cannot be addressed directly.    * @param valueCount the number of values.    * @param bitsPerValue the number of bits available for any given value.    */
DECL|method|Packed64
specifier|public
name|Packed64
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
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
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|updateCached
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates an array with content retrieved from the given DataInput.    * @param in       a DataInput, positioned at the start of Packed64-content.    * @param valueCount  the number of elements.    * @param bitsPerValue the number of bits available for any given value.    * @throws java.io.IOException if the values for the backing array could not    *                             be retrieved.    */
DECL|method|Packed64
specifier|public
name|Packed64
parameter_list|(
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
name|int
name|size
init|=
name|size
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|blocks
operator|=
operator|new
name|long
index|[
name|size
operator|+
literal|1
index|]
expr_stmt|;
comment|// +1 due to non-conditional tricks
comment|// TODO: find a faster way to bulk-read longs...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
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
name|updateCached
argument_list|()
expr_stmt|;
block|}
DECL|method|size
specifier|private
specifier|static
name|int
name|size
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
specifier|final
name|long
name|totBitCount
init|=
operator|(
name|long
operator|)
name|valueCount
operator|*
name|bitsPerValue
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|totBitCount
operator|/
literal|64
operator|+
operator|(
operator|(
name|totBitCount
operator|%
literal|64
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
argument_list|)
return|;
block|}
DECL|method|updateCached
specifier|private
name|void
name|updateCached
parameter_list|()
block|{
name|readMasks
operator|=
name|MASKS
index|[
name|bitsPerValue
index|]
expr_stmt|;
name|shifts
operator|=
name|SHIFTS
index|[
name|bitsPerValue
index|]
expr_stmt|;
name|writeMasks
operator|=
name|WRITE_MASKS
index|[
name|bitsPerValue
index|]
expr_stmt|;
name|maxPos
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
operator|(
name|long
operator|)
name|blocks
operator|.
name|length
operator|)
operator|*
name|BLOCK_SIZE
operator|/
name|bitsPerValue
operator|)
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param index the position of the value.    * @return the value at the given index.    */
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
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|size
argument_list|()
assert|;
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
specifier|final
name|int
name|bitPos
init|=
call|(
name|int
call|)
argument_list|(
name|majorBitPos
operator|&
name|MOD_MASK
argument_list|)
decl_stmt|;
comment|// % BLOCK_SIZE);
specifier|final
name|int
name|base
init|=
name|bitPos
operator|*
name|FAC_BITPOS
decl_stmt|;
assert|assert
name|elementPos
operator|<
name|blocks
operator|.
name|length
operator|:
literal|"elementPos: "
operator|+
name|elementPos
operator|+
literal|"; blocks.len: "
operator|+
name|blocks
operator|.
name|length
assert|;
return|return
operator|(
operator|(
name|blocks
index|[
name|elementPos
index|]
operator|<<
name|shifts
index|[
name|base
index|]
operator|)
operator|>>>
name|shifts
index|[
name|base
operator|+
literal|1
index|]
operator|)
operator||
operator|(
operator|(
name|blocks
index|[
name|elementPos
operator|+
literal|1
index|]
operator|>>>
name|shifts
index|[
name|base
operator|+
literal|2
index|]
operator|)
operator|&
name|readMasks
index|[
name|bitPos
index|]
operator|)
return|;
block|}
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
specifier|final
name|int
name|bitPos
init|=
call|(
name|int
call|)
argument_list|(
name|majorBitPos
operator|&
name|MOD_MASK
argument_list|)
decl_stmt|;
comment|// % BLOCK_SIZE);
specifier|final
name|int
name|base
init|=
name|bitPos
operator|*
name|FAC_BITPOS
decl_stmt|;
name|blocks
index|[
name|elementPos
index|]
operator|=
operator|(
name|blocks
index|[
name|elementPos
index|]
operator|&
name|writeMasks
index|[
name|base
index|]
operator|)
operator||
operator|(
name|value
operator|<<
name|shifts
index|[
name|base
operator|+
literal|1
index|]
operator|>>>
name|shifts
index|[
name|base
index|]
operator|)
expr_stmt|;
name|blocks
index|[
name|elementPos
operator|+
literal|1
index|]
operator|=
operator|(
name|blocks
index|[
name|elementPos
operator|+
literal|1
index|]
operator|&
name|writeMasks
index|[
name|base
operator|+
literal|1
index|]
operator|)
operator||
operator|(
operator|(
name|value
operator|<<
name|shifts
index|[
name|base
operator|+
literal|2
index|]
operator|)
operator|&
name|writeMasks
index|[
name|base
operator|+
literal|2
index|]
operator|)
expr_stmt|;
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
literal|", size="
operator|+
name|size
argument_list|()
operator|+
literal|", maxPos="
operator|+
name|maxPos
operator|+
literal|", elements.length="
operator|+
name|blocks
operator|.
name|length
operator|+
literal|")"
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
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
name|bitsRequired
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
name|pgcd
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

