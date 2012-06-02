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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
comment|/**  * This class is similar to {@link Packed64} except that it trades space for  * speed by ensuring that a single block needs to be read/written in order to  * read/write a value.  */
end_comment

begin_class
DECL|class|Packed64SingleBlock
specifier|abstract
class|class
name|Packed64SingleBlock
extends|extends
name|PackedInts
operator|.
name|MutableImpl
block|{
DECL|field|SUPPORTED_BITS_PER_VALUE
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|SUPPORTED_BITS_PER_VALUE
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|9
block|,
literal|10
block|,
literal|12
block|,
literal|21
block|}
decl_stmt|;
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
literal|22
index|]
index|[]
decl_stmt|;
DECL|field|SHIFTS
specifier|private
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
literal|22
index|]
index|[]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|bpv
range|:
name|SUPPORTED_BITS_PER_VALUE
control|)
block|{
name|initMasks
argument_list|(
name|bpv
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initMasks
specifier|protected
specifier|static
name|void
name|initMasks
parameter_list|(
name|int
name|bpv
parameter_list|)
block|{
name|int
name|valuesPerBlock
init|=
name|Long
operator|.
name|SIZE
operator|/
name|bpv
decl_stmt|;
name|long
index|[]
name|writeMasks
init|=
operator|new
name|long
index|[
name|valuesPerBlock
index|]
decl_stmt|;
name|int
index|[]
name|shifts
init|=
operator|new
name|int
index|[
name|valuesPerBlock
index|]
decl_stmt|;
name|long
name|bits
init|=
operator|(
literal|1L
operator|<<
name|bpv
operator|)
operator|-
literal|1
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
name|valuesPerBlock
condition|;
operator|++
name|i
control|)
block|{
name|shifts
index|[
name|i
index|]
operator|=
name|bpv
operator|*
name|i
expr_stmt|;
name|writeMasks
index|[
name|i
index|]
operator|=
operator|~
operator|(
name|bits
operator|<<
name|shifts
index|[
name|i
index|]
operator|)
expr_stmt|;
block|}
name|WRITE_MASKS
index|[
name|bpv
index|]
operator|=
name|writeMasks
expr_stmt|;
name|SHIFTS
index|[
name|bpv
index|]
operator|=
name|shifts
expr_stmt|;
block|}
DECL|method|create
specifier|public
specifier|static
name|Packed64SingleBlock
name|create
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
switch|switch
condition|(
name|bitsPerValue
condition|)
block|{
case|case
literal|1
case|:
return|return
operator|new
name|Packed64SingleBlock1
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|2
case|:
return|return
operator|new
name|Packed64SingleBlock2
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|3
case|:
return|return
operator|new
name|Packed64SingleBlock3
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|4
case|:
return|return
operator|new
name|Packed64SingleBlock4
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|5
case|:
return|return
operator|new
name|Packed64SingleBlock5
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|6
case|:
return|return
operator|new
name|Packed64SingleBlock6
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|7
case|:
return|return
operator|new
name|Packed64SingleBlock7
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|9
case|:
return|return
operator|new
name|Packed64SingleBlock9
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|10
case|:
return|return
operator|new
name|Packed64SingleBlock10
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|12
case|:
return|return
operator|new
name|Packed64SingleBlock12
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|21
case|:
return|return
operator|new
name|Packed64SingleBlock21
argument_list|(
name|valueCount
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported bitsPerValue: "
operator|+
name|bitsPerValue
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|public
specifier|static
name|Packed64SingleBlock
name|create
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
name|Packed64SingleBlock
name|reader
init|=
name|create
argument_list|(
name|valueCount
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
name|reader
operator|.
name|blocks
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|reader
operator|.
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
return|return
name|reader
return|;
block|}
DECL|method|isSupported
specifier|public
specifier|static
name|boolean
name|isSupported
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|SUPPORTED_BITS_PER_VALUE
argument_list|,
name|bitsPerValue
argument_list|)
operator|>=
literal|0
return|;
block|}
DECL|method|overheadPerValue
specifier|public
specifier|static
name|float
name|overheadPerValue
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
name|int
name|valuesPerBlock
init|=
literal|64
operator|/
name|bitsPerValue
decl_stmt|;
name|int
name|overhead
init|=
literal|64
operator|%
name|bitsPerValue
decl_stmt|;
return|return
operator|(
name|float
operator|)
name|overhead
operator|/
name|valuesPerBlock
return|;
block|}
DECL|field|blocks
specifier|protected
specifier|final
name|long
index|[]
name|blocks
decl_stmt|;
DECL|field|valuesPerBlock
specifier|protected
specifier|final
name|int
name|valuesPerBlock
decl_stmt|;
DECL|field|shifts
specifier|protected
specifier|final
name|int
index|[]
name|shifts
decl_stmt|;
DECL|field|writeMasks
specifier|protected
specifier|final
name|long
index|[]
name|writeMasks
decl_stmt|;
DECL|field|readMask
specifier|protected
specifier|final
name|long
name|readMask
decl_stmt|;
DECL|method|Packed64SingleBlock
name|Packed64SingleBlock
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
name|valuesPerBlock
operator|=
name|Long
operator|.
name|SIZE
operator|/
name|bitsPerValue
expr_stmt|;
name|blocks
operator|=
operator|new
name|long
index|[
name|requiredCapacity
argument_list|(
name|valueCount
argument_list|,
name|valuesPerBlock
argument_list|)
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
name|readMask
operator|=
operator|~
name|writeMasks
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|requiredCapacity
specifier|private
specifier|static
name|int
name|requiredCapacity
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|valuesPerBlock
parameter_list|)
block|{
return|return
name|valueCount
operator|/
name|valuesPerBlock
operator|+
operator|(
name|valueCount
operator|%
name|valuesPerBlock
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
name|valuesPerBlock
return|;
block|}
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
name|valuesPerBlock
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|blockOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|b
init|=
name|offsetInBlock
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
operator|(
name|blocks
index|[
name|o
index|]
operator|>>
name|shifts
index|[
name|b
index|]
operator|)
operator|&
name|readMask
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
comment|// go to the next block boundary
specifier|final
name|int
name|offsetInBlock
init|=
name|offsetInBlock
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|offsetInBlock
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offsetInBlock
init|;
name|i
argument_list|<
name|valuesPerBlock
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
name|offsetInBlock
argument_list|(
name|index
argument_list|)
operator|==
literal|0
assert|;
specifier|final
name|int
name|startBlock
init|=
name|blockOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endBlock
init|=
name|blockOffset
argument_list|(
name|index
operator|+
name|len
argument_list|)
decl_stmt|;
specifier|final
name|int
name|diff
init|=
operator|(
name|endBlock
operator|-
name|startBlock
operator|)
operator|*
name|valuesPerBlock
decl_stmt|;
name|index
operator|+=
name|diff
expr_stmt|;
name|len
operator|-=
name|diff
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valuesPerBlock
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
operator|(
name|blocks
index|[
name|block
index|]
operator|>>
name|shifts
index|[
name|i
index|]
operator|)
operator|&
name|readMask
expr_stmt|;
block|}
block|}
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
comment|// no progress so far => already at a block boundary but no full block to
comment|// get
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
name|int
name|index
parameter_list|,
name|long
name|value
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|blockOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|b
init|=
name|offsetInBlock
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|blocks
index|[
name|o
index|]
operator|=
operator|(
name|blocks
index|[
name|o
index|]
operator|&
name|writeMasks
index|[
name|b
index|]
operator|)
operator||
operator|(
name|value
operator|<<
name|shifts
index|[
name|b
index|]
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
comment|// go to the next block boundary
specifier|final
name|int
name|offsetInBlock
init|=
name|offsetInBlock
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|offsetInBlock
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offsetInBlock
init|;
name|i
argument_list|<
name|valuesPerBlock
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
name|offsetInBlock
argument_list|(
name|index
argument_list|)
operator|==
literal|0
assert|;
specifier|final
name|int
name|startBlock
init|=
name|blockOffset
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endBlock
init|=
name|blockOffset
argument_list|(
name|index
operator|+
name|len
argument_list|)
decl_stmt|;
specifier|final
name|int
name|diff
init|=
operator|(
name|endBlock
operator|-
name|startBlock
operator|)
operator|*
name|valuesPerBlock
decl_stmt|;
name|index
operator|+=
name|diff
expr_stmt|;
name|len
operator|-=
name|diff
expr_stmt|;
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
name|long
name|next
init|=
literal|0L
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
name|valuesPerBlock
condition|;
operator|++
name|i
control|)
block|{
name|next
operator||=
operator|(
name|arr
index|[
name|off
operator|++
index|]
operator|<<
name|shifts
index|[
name|i
index|]
operator|)
expr_stmt|;
block|}
name|blocks
index|[
name|block
index|]
operator|=
name|next
expr_stmt|;
block|}
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
comment|// no progress so far => already at a block boundary but no full block to
comment|// set
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
name|fromIndex
operator|>=
literal|0
assert|;
assert|assert
name|fromIndex
operator|<=
name|toIndex
assert|;
assert|assert
operator|(
name|val
operator|&
name|readMask
operator|)
operator|==
name|val
assert|;
if|if
condition|(
name|toIndex
operator|-
name|fromIndex
operator|<=
name|valuesPerBlock
operator|<<
literal|1
condition|)
block|{
comment|// there needs to be at least one full block to set for the block
comment|// approach to be worth trying
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
comment|// set values naively until the next block start
name|int
name|fromOffsetInBlock
init|=
name|offsetInBlock
argument_list|(
name|fromIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|fromOffsetInBlock
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|fromOffsetInBlock
init|;
name|i
operator|<
name|valuesPerBlock
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
assert|assert
name|offsetInBlock
argument_list|(
name|fromIndex
argument_list|)
operator|==
literal|0
assert|;
block|}
comment|// bulk set of the inner blocks
specifier|final
name|int
name|fromBlock
init|=
name|blockOffset
argument_list|(
name|fromIndex
argument_list|)
decl_stmt|;
specifier|final
name|int
name|toBlock
init|=
name|blockOffset
argument_list|(
name|toIndex
argument_list|)
decl_stmt|;
assert|assert
name|fromBlock
operator|*
name|valuesPerBlock
operator|==
name|fromIndex
assert|;
name|long
name|blockValue
init|=
literal|0L
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
name|valuesPerBlock
condition|;
operator|++
name|i
control|)
block|{
name|blockValue
operator|=
name|blockValue
operator||
operator|(
name|val
operator|<<
name|shifts
index|[
name|i
index|]
operator|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|fill
argument_list|(
name|blocks
argument_list|,
name|fromBlock
argument_list|,
name|toBlock
argument_list|,
name|blockValue
argument_list|)
expr_stmt|;
comment|// fill the gap
for|for
control|(
name|int
name|i
init|=
name|valuesPerBlock
operator|*
name|toBlock
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(bitsPerValue="
operator|+
name|bitsPerValue
operator|+
literal|", size="
operator|+
name|size
argument_list|()
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
comment|// Specialisations that allow the JVM to optimize computation of the block
comment|// offset as well as the offset in block
DECL|class|Packed64SingleBlock21
specifier|static
specifier|final
class|class
name|Packed64SingleBlock21
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock21
name|Packed64SingleBlock21
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|21
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|3
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|3
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|3
return|;
block|}
block|}
DECL|class|Packed64SingleBlock12
specifier|static
specifier|final
class|class
name|Packed64SingleBlock12
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock12
name|Packed64SingleBlock12
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|12
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|5
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|5
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|5
return|;
block|}
block|}
DECL|class|Packed64SingleBlock10
specifier|static
specifier|final
class|class
name|Packed64SingleBlock10
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock10
name|Packed64SingleBlock10
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|10
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|6
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|6
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|6
return|;
block|}
block|}
DECL|class|Packed64SingleBlock9
specifier|static
specifier|final
class|class
name|Packed64SingleBlock9
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock9
name|Packed64SingleBlock9
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|9
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|7
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|7
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|7
return|;
block|}
block|}
DECL|class|Packed64SingleBlock7
specifier|static
specifier|final
class|class
name|Packed64SingleBlock7
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock7
name|Packed64SingleBlock7
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|7
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|9
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|9
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|9
return|;
block|}
block|}
DECL|class|Packed64SingleBlock6
specifier|static
specifier|final
class|class
name|Packed64SingleBlock6
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock6
name|Packed64SingleBlock6
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|6
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|10
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|10
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|10
return|;
block|}
block|}
DECL|class|Packed64SingleBlock5
specifier|static
specifier|final
class|class
name|Packed64SingleBlock5
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock5
name|Packed64SingleBlock5
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|5
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|12
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|12
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|12
return|;
block|}
block|}
DECL|class|Packed64SingleBlock4
specifier|static
specifier|final
class|class
name|Packed64SingleBlock4
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock4
name|Packed64SingleBlock4
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|4
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|16
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|>>
literal|4
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|&
literal|15
return|;
block|}
block|}
DECL|class|Packed64SingleBlock3
specifier|static
specifier|final
class|class
name|Packed64SingleBlock3
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock3
name|Packed64SingleBlock3
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|3
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|21
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|/
literal|21
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|%
literal|21
return|;
block|}
block|}
DECL|class|Packed64SingleBlock2
specifier|static
specifier|final
class|class
name|Packed64SingleBlock2
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock2
name|Packed64SingleBlock2
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|2
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|32
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|>>
literal|5
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|&
literal|31
return|;
block|}
block|}
DECL|class|Packed64SingleBlock1
specifier|static
specifier|final
class|class
name|Packed64SingleBlock1
extends|extends
name|Packed64SingleBlock
block|{
DECL|method|Packed64SingleBlock1
name|Packed64SingleBlock1
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|1
argument_list|)
expr_stmt|;
assert|assert
name|valuesPerBlock
operator|==
literal|64
assert|;
block|}
annotation|@
name|Override
DECL|method|blockOffset
specifier|protected
name|int
name|blockOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|>>
literal|6
return|;
block|}
annotation|@
name|Override
DECL|method|offsetInBlock
specifier|protected
name|int
name|offsetInBlock
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|offset
operator|&
literal|63
return|;
block|}
block|}
block|}
end_class

end_unit

