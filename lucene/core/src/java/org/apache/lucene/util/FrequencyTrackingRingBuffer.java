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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A ring buffer that tracks the frequency of the integers that it contains.  * This is typically useful to track the hash codes of popular recently-used  * items.  *  * This data-structure requires 22 bytes per entry on average (between 16 and  * 28).  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|FrequencyTrackingRingBuffer
specifier|public
specifier|final
class|class
name|FrequencyTrackingRingBuffer
implements|implements
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|FrequencyTrackingRingBuffer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|int
index|[]
name|buffer
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
DECL|field|frequencies
specifier|private
specifier|final
name|IntBag
name|frequencies
decl_stmt|;
comment|/** Create a new ring buffer that will contain at most<code>maxSize</code> items.    *  This buffer will initially contain<code>maxSize</code> times the    *<code>sentinel</code> value. */
DECL|method|FrequencyTrackingRingBuffer
specifier|public
name|FrequencyTrackingRingBuffer
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|int
name|sentinel
parameter_list|)
block|{
if|if
condition|(
name|maxSize
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxSize must be at least 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
name|buffer
operator|=
operator|new
name|int
index|[
name|maxSize
index|]
expr_stmt|;
name|position
operator|=
literal|0
expr_stmt|;
name|frequencies
operator|=
operator|new
name|IntBag
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|sentinel
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxSize
condition|;
operator|++
name|i
control|)
block|{
name|frequencies
operator|.
name|add
argument_list|(
name|sentinel
argument_list|)
expr_stmt|;
block|}
assert|assert
name|frequencies
operator|.
name|frequency
argument_list|(
name|sentinel
argument_list|)
operator|==
name|maxSize
assert|;
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
name|BASE_RAM_BYTES_USED
operator|+
name|frequencies
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|/**    * Add a new item to this ring buffer, potentially removing the oldest    * entry from this buffer if it is already full.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|i
parameter_list|)
block|{
comment|// remove the previous value
specifier|final
name|int
name|removed
init|=
name|buffer
index|[
name|position
index|]
decl_stmt|;
specifier|final
name|boolean
name|removedFromBag
init|=
name|frequencies
operator|.
name|remove
argument_list|(
name|removed
argument_list|)
decl_stmt|;
assert|assert
name|removedFromBag
assert|;
comment|// add the new value
name|buffer
index|[
name|position
index|]
operator|=
name|i
expr_stmt|;
name|frequencies
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// increment the position
name|position
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|position
operator|==
name|maxSize
condition|)
block|{
name|position
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * Returns the frequency of the provided key in the ring buffer.    */
DECL|method|frequency
specifier|public
name|int
name|frequency
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|frequencies
operator|.
name|frequency
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|// pkg-private for testing
DECL|method|asFrequencyMap
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|asFrequencyMap
parameter_list|()
block|{
return|return
name|frequencies
operator|.
name|asMap
argument_list|()
return|;
block|}
comment|/**    * A bag of integers.    * Since in the context of the ring buffer the maximum size is known up-front    * there is no need to worry about resizing the underlying storage.    */
DECL|class|IntBag
specifier|private
specifier|static
class|class
name|IntBag
implements|implements
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|IntBag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|keys
specifier|private
specifier|final
name|int
index|[]
name|keys
decl_stmt|;
DECL|field|freqs
specifier|private
specifier|final
name|int
index|[]
name|freqs
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|int
name|mask
decl_stmt|;
DECL|method|IntBag
name|IntBag
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
comment|// load factor of 2/3
name|int
name|capacity
init|=
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
name|maxSize
operator|*
literal|3
operator|/
literal|2
argument_list|)
decl_stmt|;
comment|// round up to the next power of two
name|capacity
operator|=
name|Integer
operator|.
name|highestOneBit
argument_list|(
name|capacity
operator|-
literal|1
argument_list|)
operator|<<
literal|1
expr_stmt|;
assert|assert
name|capacity
operator|>
name|maxSize
assert|;
name|keys
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
name|freqs
operator|=
operator|new
name|int
index|[
name|capacity
index|]
expr_stmt|;
name|mask
operator|=
name|capacity
operator|-
literal|1
expr_stmt|;
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
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|keys
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|freqs
argument_list|)
return|;
block|}
comment|/** Return the frequency of the give key in the bag. */
DECL|method|frequency
name|int
name|frequency
parameter_list|(
name|int
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|slot
init|=
name|key
operator|&
name|mask
init|;
condition|;
name|slot
operator|=
operator|(
name|slot
operator|+
literal|1
operator|)
operator|&
name|mask
control|)
block|{
if|if
condition|(
name|keys
index|[
name|slot
index|]
operator|==
name|key
condition|)
block|{
return|return
name|freqs
index|[
name|slot
index|]
return|;
block|}
elseif|else
if|if
condition|(
name|freqs
index|[
name|slot
index|]
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
comment|/** Increment the frequency of the given key by 1 and return its new frequency. */
DECL|method|add
name|int
name|add
parameter_list|(
name|int
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|slot
init|=
name|key
operator|&
name|mask
init|;
condition|;
name|slot
operator|=
operator|(
name|slot
operator|+
literal|1
operator|)
operator|&
name|mask
control|)
block|{
if|if
condition|(
name|freqs
index|[
name|slot
index|]
operator|==
literal|0
condition|)
block|{
name|keys
index|[
name|slot
index|]
operator|=
name|key
expr_stmt|;
return|return
name|freqs
index|[
name|slot
index|]
operator|=
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|keys
index|[
name|slot
index|]
operator|==
name|key
condition|)
block|{
return|return
operator|++
name|freqs
index|[
name|slot
index|]
return|;
block|}
block|}
block|}
comment|/** Decrement the frequency of the given key by one, or do nothing if the      *  key is not present in the bag. Returns true iff the key was contained      *  in the bag. */
DECL|method|remove
name|boolean
name|remove
parameter_list|(
name|int
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|slot
init|=
name|key
operator|&
name|mask
init|;
condition|;
name|slot
operator|=
operator|(
name|slot
operator|+
literal|1
operator|)
operator|&
name|mask
control|)
block|{
if|if
condition|(
name|freqs
index|[
name|slot
index|]
operator|==
literal|0
condition|)
block|{
comment|// no such key in the bag
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|keys
index|[
name|slot
index|]
operator|==
name|key
condition|)
block|{
specifier|final
name|int
name|newFreq
init|=
operator|--
name|freqs
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|newFreq
operator|==
literal|0
condition|)
block|{
comment|// removed
name|relocateAdjacentKeys
argument_list|(
name|slot
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
DECL|method|relocateAdjacentKeys
specifier|private
name|void
name|relocateAdjacentKeys
parameter_list|(
name|int
name|freeSlot
parameter_list|)
block|{
for|for
control|(
name|int
name|slot
init|=
operator|(
name|freeSlot
operator|+
literal|1
operator|)
operator|&
name|mask
init|;
condition|;
name|slot
operator|=
operator|(
name|slot
operator|+
literal|1
operator|)
operator|&
name|mask
control|)
block|{
specifier|final
name|int
name|freq
init|=
name|freqs
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|freq
operator|==
literal|0
condition|)
block|{
comment|// end of the collision chain, we're done
break|break;
block|}
specifier|final
name|int
name|key
init|=
name|keys
index|[
name|slot
index|]
decl_stmt|;
comment|// the slot where<code>key</code> should be if there were no collisions
specifier|final
name|int
name|expectedSlot
init|=
name|key
operator|&
name|mask
decl_stmt|;
comment|// if the free slot is between the expected slot and the slot where the
comment|// key is, then we can relocate there
if|if
condition|(
name|between
argument_list|(
name|expectedSlot
argument_list|,
name|slot
argument_list|,
name|freeSlot
argument_list|)
condition|)
block|{
name|keys
index|[
name|freeSlot
index|]
operator|=
name|key
expr_stmt|;
name|freqs
index|[
name|freeSlot
index|]
operator|=
name|freq
expr_stmt|;
comment|// slot is the new free slot
name|freqs
index|[
name|slot
index|]
operator|=
literal|0
expr_stmt|;
name|freeSlot
operator|=
name|slot
expr_stmt|;
block|}
block|}
block|}
comment|/** Given a chain of occupied slots between<code>chainStart</code>      *  and<code>chainEnd</code>, return whether<code>slot</code> is      *  between the start and end of the chain. */
DECL|method|between
specifier|private
specifier|static
name|boolean
name|between
parameter_list|(
name|int
name|chainStart
parameter_list|,
name|int
name|chainEnd
parameter_list|,
name|int
name|slot
parameter_list|)
block|{
if|if
condition|(
name|chainStart
operator|<=
name|chainEnd
condition|)
block|{
return|return
name|chainStart
operator|<=
name|slot
operator|&&
name|slot
operator|<=
name|chainEnd
return|;
block|}
else|else
block|{
comment|// the chain is across the end of the array
return|return
name|slot
operator|>=
name|chainStart
operator|||
name|slot
operator|<=
name|chainEnd
return|;
block|}
block|}
DECL|method|asMap
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|asMap
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|keys
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|freqs
index|[
name|i
index|]
operator|>
literal|0
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|,
name|freqs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
block|}
block|}
end_class

end_unit

