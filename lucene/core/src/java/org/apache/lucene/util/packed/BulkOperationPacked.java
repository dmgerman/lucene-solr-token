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

begin_comment
comment|/**  * Non-specialized {@link BulkOperation} for {@link PackedInts.Format#PACKED}.  */
end_comment

begin_class
DECL|class|BulkOperationPacked
class|class
name|BulkOperationPacked
extends|extends
name|BulkOperation
block|{
DECL|field|bitsPerValue
specifier|private
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|longBlockCount
specifier|private
specifier|final
name|int
name|longBlockCount
decl_stmt|;
DECL|field|longValueCount
specifier|private
specifier|final
name|int
name|longValueCount
decl_stmt|;
DECL|field|byteBlockCount
specifier|private
specifier|final
name|int
name|byteBlockCount
decl_stmt|;
DECL|field|byteValueCount
specifier|private
specifier|final
name|int
name|byteValueCount
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|long
name|mask
decl_stmt|;
DECL|field|intMask
specifier|private
specifier|final
name|int
name|intMask
decl_stmt|;
DECL|method|BulkOperationPacked
specifier|public
name|BulkOperationPacked
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
assert|;
name|int
name|blocks
init|=
name|bitsPerValue
decl_stmt|;
while|while
condition|(
operator|(
name|blocks
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
name|blocks
operator|>>>=
literal|1
expr_stmt|;
block|}
name|this
operator|.
name|longBlockCount
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|longValueCount
operator|=
literal|64
operator|*
name|longBlockCount
operator|/
name|bitsPerValue
expr_stmt|;
name|int
name|byteBlockCount
init|=
literal|8
operator|*
name|longBlockCount
decl_stmt|;
name|int
name|byteValueCount
init|=
name|longValueCount
decl_stmt|;
while|while
condition|(
operator|(
name|byteBlockCount
operator|&
literal|1
operator|)
operator|==
literal|0
operator|&&
operator|(
name|byteValueCount
operator|&
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
name|byteBlockCount
operator|>>>=
literal|1
expr_stmt|;
name|byteValueCount
operator|>>>=
literal|1
expr_stmt|;
block|}
name|this
operator|.
name|byteBlockCount
operator|=
name|byteBlockCount
expr_stmt|;
name|this
operator|.
name|byteValueCount
operator|=
name|byteValueCount
expr_stmt|;
if|if
condition|(
name|bitsPerValue
operator|==
literal|64
condition|)
block|{
name|this
operator|.
name|mask
operator|=
operator|~
literal|0L
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mask
operator|=
operator|(
literal|1L
operator|<<
name|bitsPerValue
operator|)
operator|-
literal|1
expr_stmt|;
block|}
name|this
operator|.
name|intMask
operator|=
operator|(
name|int
operator|)
name|mask
expr_stmt|;
assert|assert
name|longValueCount
operator|*
name|bitsPerValue
operator|==
literal|64
operator|*
name|longBlockCount
assert|;
block|}
annotation|@
name|Override
DECL|method|longBlockCount
specifier|public
name|int
name|longBlockCount
parameter_list|()
block|{
return|return
name|longBlockCount
return|;
block|}
annotation|@
name|Override
DECL|method|longValueCount
specifier|public
name|int
name|longValueCount
parameter_list|()
block|{
return|return
name|longValueCount
return|;
block|}
annotation|@
name|Override
DECL|method|byteBlockCount
specifier|public
name|int
name|byteBlockCount
parameter_list|()
block|{
return|return
name|byteBlockCount
return|;
block|}
annotation|@
name|Override
DECL|method|byteValueCount
specifier|public
name|int
name|byteValueCount
parameter_list|()
block|{
return|return
name|byteValueCount
return|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|int
name|bitsLeft
init|=
literal|64
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
name|longValueCount
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|bitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
if|if
condition|(
name|bitsLeft
operator|<
literal|0
condition|)
block|{
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
operator|(
operator|(
literal|1L
operator|<<
operator|(
name|bitsPerValue
operator|+
name|bitsLeft
operator|)
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
operator|-
name|bitsLeft
operator|)
operator||
operator|(
name|blocks
index|[
name|blocksOffset
index|]
operator|>>>
operator|(
literal|64
operator|+
name|bitsLeft
operator|)
operator|)
expr_stmt|;
name|bitsLeft
operator|+=
literal|64
expr_stmt|;
block|}
else|else
block|{
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|blocks
index|[
name|blocksOffset
index|]
operator|>>>
name|bitsLeft
operator|)
operator|&
name|mask
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|long
name|nextValue
init|=
literal|0L
decl_stmt|;
name|int
name|bitsLeft
init|=
name|bitsPerValue
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
name|iterations
operator|*
name|byteBlockCount
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|bytes
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFFL
decl_stmt|;
if|if
condition|(
name|bitsLeft
operator|>
literal|8
condition|)
block|{
comment|// just buffer
name|bitsLeft
operator|-=
literal|8
expr_stmt|;
name|nextValue
operator||=
name|bytes
operator|<<
name|bitsLeft
expr_stmt|;
block|}
else|else
block|{
comment|// flush
name|int
name|bits
init|=
literal|8
operator|-
name|bitsLeft
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|nextValue
operator||
operator|(
name|bytes
operator|>>>
name|bits
operator|)
expr_stmt|;
while|while
condition|(
name|bits
operator|>=
name|bitsPerValue
condition|)
block|{
name|bits
operator|-=
name|bitsPerValue
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|bytes
operator|>>>
name|bits
operator|)
operator|&
name|mask
expr_stmt|;
block|}
comment|// then buffer
name|bitsLeft
operator|=
name|bitsPerValue
operator|-
name|bits
expr_stmt|;
name|nextValue
operator|=
operator|(
name|bytes
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
operator|<<
name|bitsLeft
expr_stmt|;
block|}
block|}
assert|assert
name|bitsLeft
operator|==
name|bitsPerValue
assert|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
if|if
condition|(
name|bitsPerValue
operator|>
literal|32
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot decode "
operator|+
name|bitsPerValue
operator|+
literal|"-bits values into an int[]"
argument_list|)
throw|;
block|}
name|int
name|bitsLeft
init|=
literal|64
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
name|longValueCount
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|bitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
if|if
condition|(
name|bitsLeft
operator|<
literal|0
condition|)
block|{
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
operator|(
operator|(
literal|1L
operator|<<
operator|(
name|bitsPerValue
operator|+
name|bitsLeft
operator|)
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
operator|-
name|bitsLeft
operator|)
operator||
operator|(
name|blocks
index|[
name|blocksOffset
index|]
operator|>>>
operator|(
literal|64
operator|+
name|bitsLeft
operator|)
operator|)
argument_list|)
expr_stmt|;
name|bitsLeft
operator|+=
literal|64
expr_stmt|;
block|}
else|else
block|{
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|blocks
index|[
name|blocksOffset
index|]
operator|>>>
name|bitsLeft
operator|)
operator|&
name|mask
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|int
name|nextValue
init|=
literal|0
decl_stmt|;
name|int
name|bitsLeft
init|=
name|bitsPerValue
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
name|iterations
operator|*
name|byteBlockCount
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|bytes
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
if|if
condition|(
name|bitsLeft
operator|>
literal|8
condition|)
block|{
comment|// just buffer
name|bitsLeft
operator|-=
literal|8
expr_stmt|;
name|nextValue
operator||=
name|bytes
operator|<<
name|bitsLeft
expr_stmt|;
block|}
else|else
block|{
comment|// flush
name|int
name|bits
init|=
literal|8
operator|-
name|bitsLeft
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|nextValue
operator||
operator|(
name|bytes
operator|>>>
name|bits
operator|)
expr_stmt|;
while|while
condition|(
name|bits
operator|>=
name|bitsPerValue
condition|)
block|{
name|bits
operator|-=
name|bitsPerValue
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|bytes
operator|>>>
name|bits
operator|)
operator|&
name|intMask
expr_stmt|;
block|}
comment|// then buffer
name|bitsLeft
operator|=
name|bitsPerValue
operator|-
name|bits
expr_stmt|;
name|nextValue
operator|=
operator|(
name|bytes
operator|&
operator|(
operator|(
literal|1
operator|<<
name|bits
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
name|bitsLeft
expr_stmt|;
block|}
block|}
assert|assert
name|bitsLeft
operator|==
name|bitsPerValue
assert|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|long
name|nextBlock
init|=
literal|0
decl_stmt|;
name|int
name|bitsLeft
init|=
literal|64
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
name|longValueCount
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|bitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
if|if
condition|(
name|bitsLeft
operator|>
literal|0
condition|)
block|{
name|nextBlock
operator||=
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
name|bitsLeft
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsLeft
operator|==
literal|0
condition|)
block|{
name|nextBlock
operator||=
name|values
index|[
name|valuesOffset
operator|++
index|]
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
name|nextBlock
expr_stmt|;
name|nextBlock
operator|=
literal|0
expr_stmt|;
name|bitsLeft
operator|=
literal|64
expr_stmt|;
block|}
else|else
block|{
comment|// bitsLeft< 0
name|nextBlock
operator||=
name|values
index|[
name|valuesOffset
index|]
operator|>>>
operator|-
name|bitsLeft
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
name|nextBlock
expr_stmt|;
name|nextBlock
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
operator|(
operator|(
literal|1L
operator|<<
operator|-
name|bitsLeft
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
operator|(
literal|64
operator|+
name|bitsLeft
operator|)
expr_stmt|;
name|bitsLeft
operator|+=
literal|64
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|long
name|nextBlock
init|=
literal|0
decl_stmt|;
name|int
name|bitsLeft
init|=
literal|64
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
name|longValueCount
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|bitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
if|if
condition|(
name|bitsLeft
operator|>
literal|0
condition|)
block|{
name|nextBlock
operator||=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xFFFFFFFFL
operator|)
operator|<<
name|bitsLeft
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsLeft
operator|==
literal|0
condition|)
block|{
name|nextBlock
operator||=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xFFFFFFFFL
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
name|nextBlock
expr_stmt|;
name|nextBlock
operator|=
literal|0
expr_stmt|;
name|bitsLeft
operator|=
literal|64
expr_stmt|;
block|}
else|else
block|{
comment|// bitsLeft< 0
name|nextBlock
operator||=
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xFFFFFFFFL
operator|)
operator|>>>
operator|-
name|bitsLeft
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
name|nextBlock
expr_stmt|;
name|nextBlock
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
operator|(
operator|(
literal|1L
operator|<<
operator|-
name|bitsLeft
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
operator|(
literal|64
operator|+
name|bitsLeft
operator|)
expr_stmt|;
name|bitsLeft
operator|+=
literal|64
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|int
name|nextBlock
init|=
literal|0
decl_stmt|;
name|int
name|bitsLeft
init|=
literal|8
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
name|byteValueCount
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|v
init|=
name|values
index|[
name|valuesOffset
operator|++
index|]
decl_stmt|;
assert|assert
name|bitsPerValue
operator|==
literal|64
operator|||
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|v
argument_list|)
operator|<=
name|bitsPerValue
assert|;
if|if
condition|(
name|bitsPerValue
operator|<
name|bitsLeft
condition|)
block|{
comment|// just buffer
name|nextBlock
operator||=
name|v
operator|<<
operator|(
name|bitsLeft
operator|-
name|bitsPerValue
operator|)
expr_stmt|;
name|bitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
block|}
else|else
block|{
comment|// flush as many blocks as possible
name|int
name|bits
init|=
name|bitsPerValue
operator|-
name|bitsLeft
decl_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|nextBlock
operator||
operator|(
name|v
operator|>>>
name|bits
operator|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|bits
operator|>=
literal|8
condition|)
block|{
name|bits
operator|-=
literal|8
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
name|bits
argument_list|)
expr_stmt|;
block|}
comment|// then buffer
name|bitsLeft
operator|=
literal|8
operator|-
name|bits
expr_stmt|;
name|nextBlock
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|v
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
operator|<<
name|bitsLeft
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|bitsLeft
operator|==
literal|8
assert|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|int
name|nextBlock
init|=
literal|0
decl_stmt|;
name|int
name|bitsLeft
init|=
literal|8
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
name|byteValueCount
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|v
init|=
name|values
index|[
name|valuesOffset
operator|++
index|]
decl_stmt|;
assert|assert
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|v
operator|&
literal|0xFFFFFFFFL
argument_list|)
operator|<=
name|bitsPerValue
assert|;
if|if
condition|(
name|bitsPerValue
operator|<
name|bitsLeft
condition|)
block|{
comment|// just buffer
name|nextBlock
operator||=
name|v
operator|<<
operator|(
name|bitsLeft
operator|-
name|bitsPerValue
operator|)
expr_stmt|;
name|bitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
block|}
else|else
block|{
comment|// flush as many blocks as possible
name|int
name|bits
init|=
name|bitsPerValue
operator|-
name|bitsLeft
decl_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|nextBlock
operator||
operator|(
name|v
operator|>>>
name|bits
operator|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|bits
operator|>=
literal|8
condition|)
block|{
name|bits
operator|-=
literal|8
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
name|bits
argument_list|)
expr_stmt|;
block|}
comment|// then buffer
name|bitsLeft
operator|=
literal|8
operator|-
name|bits
expr_stmt|;
name|nextBlock
operator|=
operator|(
name|v
operator|&
operator|(
operator|(
literal|1
operator|<<
name|bits
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
name|bitsLeft
expr_stmt|;
block|}
block|}
assert|assert
name|bitsLeft
operator|==
literal|8
assert|;
block|}
block|}
end_class

end_unit

