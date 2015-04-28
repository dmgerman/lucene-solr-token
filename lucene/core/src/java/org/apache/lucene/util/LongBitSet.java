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

begin_comment
comment|/**  * BitSet of fixed length (numBits), backed by accessible ({@link #getBits})  * long[], accessed with a long index. Use it only if you intend to store more  * than 2.1B bits, otherwise you should use {@link FixedBitSet}.  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|LongBitSet
specifier|public
specifier|final
class|class
name|LongBitSet
block|{
DECL|field|bits
specifier|private
specifier|final
name|long
index|[]
name|bits
decl_stmt|;
comment|// Array of longs holding the bits
DECL|field|numBits
specifier|private
specifier|final
name|long
name|numBits
decl_stmt|;
comment|// The number of bits in use
DECL|field|numWords
specifier|private
specifier|final
name|int
name|numWords
decl_stmt|;
comment|// The exact number of longs needed to hold numBits (<= bits.length)
comment|/**    * If the given {@link LongBitSet} is large enough to hold    * {@code numBits+1}, returns the given bits, otherwise returns a new    * {@link LongBitSet} which can hold the requested number of bits.    *<p>    *<b>NOTE:</b> the returned bitset reuses the underlying {@code long[]} of    * the given {@code bits} if possible. Also, calling {@link #length()} on the    * returned bits may return a value greater than {@code numBits}.    */
DECL|method|ensureCapacity
specifier|public
specifier|static
name|LongBitSet
name|ensureCapacity
parameter_list|(
name|LongBitSet
name|bits
parameter_list|,
name|long
name|numBits
parameter_list|)
block|{
if|if
condition|(
name|numBits
operator|<
name|bits
operator|.
name|numBits
condition|)
block|{
return|return
name|bits
return|;
block|}
else|else
block|{
comment|// Depends on the ghost bits being clear!
comment|// (Otherwise, they may become visible in the new instance)
name|int
name|numWords
init|=
name|bits2words
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|long
index|[]
name|arr
init|=
name|bits
operator|.
name|getBits
argument_list|()
decl_stmt|;
if|if
condition|(
name|numWords
operator|>=
name|arr
operator|.
name|length
condition|)
block|{
name|arr
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|arr
argument_list|,
name|numWords
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LongBitSet
argument_list|(
name|arr
argument_list|,
operator|(
name|long
operator|)
name|arr
operator|.
name|length
operator|<<
literal|6
argument_list|)
return|;
block|}
block|}
comment|/** returns the number of 64 bit words it would take to hold numBits */
DECL|method|bits2words
specifier|public
specifier|static
name|int
name|bits2words
parameter_list|(
name|long
name|numBits
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|numBits
operator|-
literal|1
operator|)
operator|>>
literal|6
argument_list|)
operator|+
literal|1
return|;
comment|// I.e.: get the word-offset of the last bit and add one (make sure to use>> so 0 returns 0!)
block|}
comment|/**    * Creates a new LongBitSet.    * The internally allocated long array will be exactly the size needed to accommodate the numBits specified.    * @param numBits the number of bits needed    */
DECL|method|LongBitSet
specifier|public
name|LongBitSet
parameter_list|(
name|long
name|numBits
parameter_list|)
block|{
name|this
operator|.
name|numBits
operator|=
name|numBits
expr_stmt|;
name|bits
operator|=
operator|new
name|long
index|[
name|bits2words
argument_list|(
name|numBits
argument_list|)
index|]
expr_stmt|;
name|numWords
operator|=
name|bits
operator|.
name|length
expr_stmt|;
block|}
comment|/**    * Creates a new LongBitSet using the provided long[] array as backing store.    * The storedBits array must be large enough to accommodate the numBits specified, but may be larger.    * In that case the 'extra' or 'ghost' bits must be clear (or they may provoke spurious side-effects)    * @param storedBits the array to use as backing store    * @param numBits the number of bits actually needed    */
DECL|method|LongBitSet
specifier|public
name|LongBitSet
parameter_list|(
name|long
index|[]
name|storedBits
parameter_list|,
name|long
name|numBits
parameter_list|)
block|{
name|this
operator|.
name|numWords
operator|=
name|bits2words
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
if|if
condition|(
name|numWords
operator|>
name|storedBits
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The given long array is too small  to hold "
operator|+
name|numBits
operator|+
literal|" bits"
argument_list|)
throw|;
block|}
name|this
operator|.
name|numBits
operator|=
name|numBits
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|storedBits
expr_stmt|;
assert|assert
name|verifyGhostBitsClear
argument_list|()
assert|;
block|}
comment|/**    * Checks if the bits past numBits are clear.    * Some methods rely on this implicit assumption: search for "Depends on the ghost bits being clear!"     * @return true if the bits past numBits are clear.    */
DECL|method|verifyGhostBitsClear
specifier|private
name|boolean
name|verifyGhostBitsClear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|numWords
init|;
name|i
operator|<
name|bits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bits
index|[
name|i
index|]
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
name|numBits
operator|&
literal|0x3f
operator|)
operator|==
literal|0
condition|)
return|return
literal|true
return|;
name|long
name|mask
init|=
operator|-
literal|1L
operator|<<
name|numBits
decl_stmt|;
return|return
operator|(
name|bits
index|[
name|numWords
operator|-
literal|1
index|]
operator|&
name|mask
operator|)
operator|==
literal|0
return|;
block|}
comment|/** Returns the number of bits stored in this bitset. */
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|numBits
return|;
block|}
comment|/** Expert. */
DECL|method|getBits
specifier|public
name|long
index|[]
name|getBits
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
comment|/** Returns number of set bits.  NOTE: this visits every    *  long in the backing bits array, and the result is not    *  internally cached!    */
DECL|method|cardinality
specifier|public
name|long
name|cardinality
parameter_list|()
block|{
comment|// Depends on the ghost bits being clear!
return|return
name|BitUtil
operator|.
name|pop_array
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|numWords
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
comment|// div 64
comment|// signed shift will keep a negative index and force an
comment|// array-index-out-of-bounds-exception, removing the need for an explicit check.
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|index
decl_stmt|;
return|return
operator|(
name|bits
index|[
name|i
index|]
operator|&
name|bitmask
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|" numBits="
operator|+
name|numBits
assert|;
name|int
name|wordNum
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
comment|// div 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|index
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator||=
name|bitmask
expr_stmt|;
block|}
DECL|method|getAndSet
specifier|public
name|boolean
name|getAndSet
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
name|int
name|wordNum
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
comment|// div 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|index
decl_stmt|;
name|boolean
name|val
init|=
operator|(
name|bits
index|[
name|wordNum
index|]
operator|&
name|bitmask
operator|)
operator|!=
literal|0
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator||=
name|bitmask
expr_stmt|;
return|return
name|val
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
name|int
name|wordNum
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|index
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator|&=
operator|~
name|bitmask
expr_stmt|;
block|}
DECL|method|getAndClear
specifier|public
name|boolean
name|getAndClear
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
name|int
name|wordNum
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
comment|// div 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|index
decl_stmt|;
name|boolean
name|val
init|=
operator|(
name|bits
index|[
name|wordNum
index|]
operator|&
name|bitmask
operator|)
operator|!=
literal|0
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator|&=
operator|~
name|bitmask
expr_stmt|;
return|return
name|val
return|;
block|}
comment|/** Returns the index of the first set bit starting at the index specified.    *  -1 is returned if there are no more set bits.    */
DECL|method|nextSetBit
specifier|public
name|long
name|nextSetBit
parameter_list|(
name|long
name|index
parameter_list|)
block|{
comment|// Depends on the ghost bits being clear!
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|long
name|word
init|=
name|bits
index|[
name|i
index|]
operator|>>
name|index
decl_stmt|;
comment|// skip all the bits to the right of index
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
name|index
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
while|while
condition|(
operator|++
name|i
operator|<
name|numWords
condition|)
block|{
name|word
operator|=
name|bits
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/** Returns the index of the last set bit before or on the index specified.    *  -1 is returned if there are no more set bits.    */
DECL|method|prevSetBit
specifier|public
name|long
name|prevSetBit
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|" numBits="
operator|+
name|numBits
assert|;
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
specifier|final
name|int
name|subIndex
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|&
literal|0x3f
argument_list|)
decl_stmt|;
comment|// index within the word
name|long
name|word
init|=
operator|(
name|bits
index|[
name|i
index|]
operator|<<
operator|(
literal|63
operator|-
name|subIndex
operator|)
operator|)
decl_stmt|;
comment|// skip all the bits to the left of index
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|subIndex
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|word
argument_list|)
return|;
comment|// See LUCENE-3197
block|}
while|while
condition|(
operator|--
name|i
operator|>=
literal|0
condition|)
block|{
name|word
operator|=
name|bits
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/** this = this OR other */
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|LongBitSet
name|other
parameter_list|)
block|{
assert|assert
name|other
operator|.
name|numWords
operator|<=
name|numWords
operator|:
literal|"numWords="
operator|+
name|numWords
operator|+
literal|", other.numWords="
operator|+
name|other
operator|.
name|numWords
assert|;
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|numWords
argument_list|,
name|other
operator|.
name|numWords
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|bits
index|[
name|pos
index|]
operator||=
name|other
operator|.
name|bits
index|[
name|pos
index|]
expr_stmt|;
block|}
block|}
comment|/** this = this XOR other */
DECL|method|xor
specifier|public
name|void
name|xor
parameter_list|(
name|LongBitSet
name|other
parameter_list|)
block|{
assert|assert
name|other
operator|.
name|numWords
operator|<=
name|numWords
operator|:
literal|"numWords="
operator|+
name|numWords
operator|+
literal|", other.numWords="
operator|+
name|other
operator|.
name|numWords
assert|;
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|numWords
argument_list|,
name|other
operator|.
name|numWords
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|bits
index|[
name|pos
index|]
operator|^=
name|other
operator|.
name|bits
index|[
name|pos
index|]
expr_stmt|;
block|}
block|}
comment|/** returns true if the sets have any elements in common */
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
name|LongBitSet
name|other
parameter_list|)
block|{
comment|// Depends on the ghost bits being clear!
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|numWords
argument_list|,
name|other
operator|.
name|numWords
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
operator|(
name|bits
index|[
name|pos
index|]
operator|&
name|other
operator|.
name|bits
index|[
name|pos
index|]
operator|)
operator|!=
literal|0
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** this = this AND other */
DECL|method|and
specifier|public
name|void
name|and
parameter_list|(
name|LongBitSet
name|other
parameter_list|)
block|{
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|numWords
argument_list|,
name|other
operator|.
name|numWords
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|bits
index|[
name|pos
index|]
operator|&=
name|other
operator|.
name|bits
index|[
name|pos
index|]
expr_stmt|;
block|}
if|if
condition|(
name|numWords
operator|>
name|other
operator|.
name|numWords
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
name|other
operator|.
name|numWords
argument_list|,
name|numWords
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** this = this AND NOT other */
DECL|method|andNot
specifier|public
name|void
name|andNot
parameter_list|(
name|LongBitSet
name|other
parameter_list|)
block|{
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|numWords
argument_list|,
name|other
operator|.
name|numWords
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|bits
index|[
name|pos
index|]
operator|&=
operator|~
name|other
operator|.
name|bits
index|[
name|pos
index|]
expr_stmt|;
block|}
block|}
comment|/**    * Scans the backing store to check if all bits are clear.    * The method is deliberately not called "isEmpty" to emphasize it is not low cost (as isEmpty usually is).    * @return true if all bits are clear.    */
DECL|method|scanIsEmpty
specifier|public
name|boolean
name|scanIsEmpty
parameter_list|()
block|{
comment|// This 'slow' implementation is still faster than any external one could be
comment|// (e.g.: (bitSet.length() == 0 || bitSet.nextSetBit(0) == -1))
comment|// especially for small BitSets
comment|// Depends on the ghost bits being clear!
specifier|final
name|int
name|count
init|=
name|numWords
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
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bits
index|[
name|i
index|]
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Flips a range of bits    *    * @param startIndex lower index    * @param endIndex one-past the last bit to flip    */
DECL|method|flip
specifier|public
name|void
name|flip
parameter_list|(
name|long
name|startIndex
parameter_list|,
name|long
name|endIndex
parameter_list|)
block|{
assert|assert
name|startIndex
operator|>=
literal|0
operator|&&
name|startIndex
operator|<
name|numBits
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
operator|&&
name|endIndex
operator|<=
name|numBits
assert|;
if|if
condition|(
name|endIndex
operator|<=
name|startIndex
condition|)
block|{
return|return;
block|}
name|int
name|startWord
init|=
call|(
name|int
call|)
argument_list|(
name|startIndex
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|int
name|endWord
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|endIndex
operator|-
literal|1
operator|)
operator|>>
literal|6
argument_list|)
decl_stmt|;
comment|/*** Grrr, java shifting uses only the lower 6 bits of the count so -1L>>>64 == -1      * for that reason, make sure not to use endmask if the bits to flip will      * be zero in the last word (redefine endWord to be the last changed...)     long startmask = -1L<< (startIndex& 0x3f);     // example: 11111...111000     long endmask = -1L>>> (64-(endIndex& 0x3f));   // example: 00111...111111     ***/
name|long
name|startmask
init|=
operator|-
literal|1L
operator|<<
name|startIndex
decl_stmt|;
name|long
name|endmask
init|=
operator|-
literal|1L
operator|>>>
operator|-
name|endIndex
decl_stmt|;
comment|// 64-(endIndex&0x3f) is the same as -endIndex since only the lowest 6 bits are used
if|if
condition|(
name|startWord
operator|==
name|endWord
condition|)
block|{
name|bits
index|[
name|startWord
index|]
operator|^=
operator|(
name|startmask
operator|&
name|endmask
operator|)
expr_stmt|;
return|return;
block|}
name|bits
index|[
name|startWord
index|]
operator|^=
name|startmask
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startWord
operator|+
literal|1
init|;
name|i
operator|<
name|endWord
condition|;
name|i
operator|++
control|)
block|{
name|bits
index|[
name|i
index|]
operator|=
operator|~
name|bits
index|[
name|i
index|]
expr_stmt|;
block|}
name|bits
index|[
name|endWord
index|]
operator|^=
name|endmask
expr_stmt|;
block|}
comment|/** Flip the bit at the provided index. */
DECL|method|flip
specifier|public
name|void
name|flip
parameter_list|(
name|long
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
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|" numBits="
operator|+
name|numBits
assert|;
name|int
name|wordNum
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
literal|6
argument_list|)
decl_stmt|;
comment|// div 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|index
decl_stmt|;
comment|// mod 64 is implicit
name|bits
index|[
name|wordNum
index|]
operator|^=
name|bitmask
expr_stmt|;
block|}
comment|/** Sets a range of bits    *    * @param startIndex lower index    * @param endIndex one-past the last bit to set    */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|long
name|startIndex
parameter_list|,
name|long
name|endIndex
parameter_list|)
block|{
assert|assert
name|startIndex
operator|>=
literal|0
operator|&&
name|startIndex
operator|<
name|numBits
operator|:
literal|"startIndex="
operator|+
name|startIndex
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
operator|&&
name|endIndex
operator|<=
name|numBits
operator|:
literal|"endIndex="
operator|+
name|endIndex
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
if|if
condition|(
name|endIndex
operator|<=
name|startIndex
condition|)
block|{
return|return;
block|}
name|int
name|startWord
init|=
call|(
name|int
call|)
argument_list|(
name|startIndex
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|int
name|endWord
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|endIndex
operator|-
literal|1
operator|)
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|long
name|startmask
init|=
operator|-
literal|1L
operator|<<
name|startIndex
decl_stmt|;
name|long
name|endmask
init|=
operator|-
literal|1L
operator|>>>
operator|-
name|endIndex
decl_stmt|;
comment|// 64-(endIndex&0x3f) is the same as -endIndex since only the lowest 6 bits are used
if|if
condition|(
name|startWord
operator|==
name|endWord
condition|)
block|{
name|bits
index|[
name|startWord
index|]
operator||=
operator|(
name|startmask
operator|&
name|endmask
operator|)
expr_stmt|;
return|return;
block|}
name|bits
index|[
name|startWord
index|]
operator||=
name|startmask
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
name|startWord
operator|+
literal|1
argument_list|,
name|endWord
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|bits
index|[
name|endWord
index|]
operator||=
name|endmask
expr_stmt|;
block|}
comment|/** Clears a range of bits.    *    * @param startIndex lower index    * @param endIndex one-past the last bit to clear    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|long
name|startIndex
parameter_list|,
name|long
name|endIndex
parameter_list|)
block|{
assert|assert
name|startIndex
operator|>=
literal|0
operator|&&
name|startIndex
operator|<
name|numBits
operator|:
literal|"startIndex="
operator|+
name|startIndex
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
operator|&&
name|endIndex
operator|<=
name|numBits
operator|:
literal|"endIndex="
operator|+
name|endIndex
operator|+
literal|", numBits="
operator|+
name|numBits
assert|;
if|if
condition|(
name|endIndex
operator|<=
name|startIndex
condition|)
block|{
return|return;
block|}
name|int
name|startWord
init|=
call|(
name|int
call|)
argument_list|(
name|startIndex
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|int
name|endWord
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|endIndex
operator|-
literal|1
operator|)
operator|>>
literal|6
argument_list|)
decl_stmt|;
name|long
name|startmask
init|=
operator|-
literal|1L
operator|<<
name|startIndex
decl_stmt|;
name|long
name|endmask
init|=
operator|-
literal|1L
operator|>>>
operator|-
name|endIndex
decl_stmt|;
comment|// 64-(endIndex&0x3f) is the same as -endIndex since only the lowest 6 bits are used
comment|// invert masks since we are clearing
name|startmask
operator|=
operator|~
name|startmask
expr_stmt|;
name|endmask
operator|=
operator|~
name|endmask
expr_stmt|;
if|if
condition|(
name|startWord
operator|==
name|endWord
condition|)
block|{
name|bits
index|[
name|startWord
index|]
operator|&=
operator|(
name|startmask
operator||
name|endmask
operator|)
expr_stmt|;
return|return;
block|}
name|bits
index|[
name|startWord
index|]
operator|&=
name|startmask
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
name|startWord
operator|+
literal|1
argument_list|,
name|endWord
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|bits
index|[
name|endWord
index|]
operator|&=
name|endmask
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|LongBitSet
name|clone
parameter_list|()
block|{
name|long
index|[]
name|bits
init|=
operator|new
name|long
index|[
name|this
operator|.
name|bits
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
argument_list|,
literal|0
argument_list|,
name|numWords
argument_list|)
expr_stmt|;
return|return
operator|new
name|LongBitSet
argument_list|(
name|bits
argument_list|,
name|numBits
argument_list|)
return|;
block|}
comment|/** returns true if both sets have the same bits set */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|LongBitSet
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|LongBitSet
name|other
init|=
operator|(
name|LongBitSet
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|numBits
operator|!=
name|other
operator|.
name|numBits
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Depends on the ghost bits being clear!
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|bits
argument_list|,
name|other
operator|.
name|bits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Depends on the ghost bits being clear!
name|long
name|h
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numWords
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
name|h
operator|^=
name|bits
index|[
name|i
index|]
expr_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|63
operator|)
expr_stmt|;
comment|// rotate left
block|}
comment|// fold leftmost bits into right and add a constant to prevent
comment|// empty sets from returning 0, which is too common.
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|h
operator|>>
literal|32
operator|)
operator|^
name|h
argument_list|)
operator|+
literal|0x98761234
return|;
block|}
block|}
end_class

end_unit

