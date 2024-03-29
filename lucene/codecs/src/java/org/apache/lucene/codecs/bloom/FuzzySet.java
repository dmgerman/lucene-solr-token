begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|bloom
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
name|search
operator|.
name|DocIdSetIterator
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
name|store
operator|.
name|DataOutput
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
name|Accountable
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
name|FixedBitSet
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
comment|/**  *<p>  * A class used to represent a set of many, potentially large, values (e.g. many  * long strings such as URLs), using a significantly smaller amount of memory.  *</p>  *<p>  * The set is "lossy" in that it cannot definitively state that is does contain  * a value but it<em>can</em> definitively say if a value is<em>not</em> in  * the set. It can therefore be used as a Bloom Filter.  *</p>   * Another application of the set is that it can be used to perform fuzzy counting because  * it can estimate reasonably accurately how many unique values are contained in the set.   *<p>This class is NOT threadsafe.</p>  *<p>  * Internally a Bitset is used to record values and once a client has finished recording  * a stream of values the {@link #downsize(float)} method can be used to create a suitably smaller set that  * is sized appropriately for the number of values recorded and desired saturation levels.   *   *</p>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FuzzySet
specifier|public
class|class
name|FuzzySet
implements|implements
name|Accountable
block|{
DECL|field|VERSION_SPI
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_SPI
init|=
literal|1
decl_stmt|;
comment|// HashFunction used to be loaded through a SPI
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
name|VERSION_SPI
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
literal|2
decl_stmt|;
DECL|method|hashFunctionForVersion
specifier|public
specifier|static
name|HashFunction
name|hashFunctionForVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
if|if
condition|(
name|version
operator|<
name|VERSION_START
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Version "
operator|+
name|version
operator|+
literal|" is too old, expected at least "
operator|+
name|VERSION_START
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|version
operator|>
name|VERSION_CURRENT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Version "
operator|+
name|version
operator|+
literal|" is too new, expected at most "
operator|+
name|VERSION_CURRENT
argument_list|)
throw|;
block|}
return|return
name|MurmurHash2
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * Result from {@link FuzzySet#contains(BytesRef)}:    * can never return definitively YES (always MAYBE),     * but can sometimes definitely return NO.    */
DECL|enum|ContainsResult
specifier|public
enum|enum
name|ContainsResult
block|{
DECL|enum constant|MAYBE
DECL|enum constant|NO
name|MAYBE
block|,
name|NO
block|}
empty_stmt|;
DECL|field|hashFunction
specifier|private
name|HashFunction
name|hashFunction
decl_stmt|;
DECL|field|filter
specifier|private
name|FixedBitSet
name|filter
decl_stmt|;
DECL|field|bloomSize
specifier|private
name|int
name|bloomSize
decl_stmt|;
comment|//The sizes of BitSet used are all numbers that, when expressed in binary form,
comment|//are all ones. This is to enable fast downsizing from one bitset to another
comment|//by simply ANDing each set index in one bitset with the size of the target bitset
comment|// - this provides a fast modulo of the number. Values previously accumulated in
comment|// a large bitset and then mapped to a smaller set can be looked up using a single
comment|// AND operation of the query term's hash rather than needing to perform a 2-step
comment|// translation of the query term that mirrors the stored content's reprojections.
DECL|field|usableBitSetSizes
specifier|static
specifier|final
name|int
name|usableBitSetSizes
index|[]
decl_stmt|;
static|static
block|{
name|usableBitSetSizes
operator|=
operator|new
name|int
index|[
literal|30
index|]
expr_stmt|;
name|int
name|mask
init|=
literal|1
decl_stmt|;
name|int
name|size
init|=
name|mask
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
name|usableBitSetSizes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|size
operator|=
operator|(
name|size
operator|<<
literal|1
operator|)
operator||
name|mask
expr_stmt|;
name|usableBitSetSizes
index|[
name|i
index|]
operator|=
name|size
expr_stmt|;
block|}
block|}
comment|/**    * Rounds down required maxNumberOfBits to the nearest number that is made up    * of all ones as a binary number.      * Use this method where controlling memory use is paramount.    */
DECL|method|getNearestSetSize
specifier|public
specifier|static
name|int
name|getNearestSetSize
parameter_list|(
name|int
name|maxNumberOfBits
parameter_list|)
block|{
name|int
name|result
init|=
name|usableBitSetSizes
index|[
literal|0
index|]
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
name|usableBitSetSizes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|usableBitSetSizes
index|[
name|i
index|]
operator|<=
name|maxNumberOfBits
condition|)
block|{
name|result
operator|=
name|usableBitSetSizes
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Use this method to choose a set size where accuracy (low content saturation) is more important    * than deciding how much memory to throw at the problem.    * @param desiredSaturation A number between 0 and 1 expressing the % of bits set once all values have been recorded    * @return The size of the set nearest to the required size    */
DECL|method|getNearestSetSize
specifier|public
specifier|static
name|int
name|getNearestSetSize
parameter_list|(
name|int
name|maxNumberOfValuesExpected
parameter_list|,
name|float
name|desiredSaturation
parameter_list|)
block|{
comment|// Iterate around the various scales of bitset from smallest to largest looking for the first that
comment|// satisfies value volumes at the chosen saturation level
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|usableBitSetSizes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numSetBitsAtDesiredSaturation
init|=
call|(
name|int
call|)
argument_list|(
name|usableBitSetSizes
index|[
name|i
index|]
operator|*
name|desiredSaturation
argument_list|)
decl_stmt|;
name|int
name|estimatedNumUniqueValues
init|=
name|getEstimatedNumberUniqueValuesAllowingForCollisions
argument_list|(
name|usableBitSetSizes
index|[
name|i
index|]
argument_list|,
name|numSetBitsAtDesiredSaturation
argument_list|)
decl_stmt|;
if|if
condition|(
name|estimatedNumUniqueValues
operator|>
name|maxNumberOfValuesExpected
condition|)
block|{
return|return
name|usableBitSetSizes
index|[
name|i
index|]
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|createSetBasedOnMaxMemory
specifier|public
specifier|static
name|FuzzySet
name|createSetBasedOnMaxMemory
parameter_list|(
name|int
name|maxNumBytes
parameter_list|)
block|{
name|int
name|setSize
init|=
name|getNearestSetSize
argument_list|(
name|maxNumBytes
argument_list|)
decl_stmt|;
return|return
operator|new
name|FuzzySet
argument_list|(
operator|new
name|FixedBitSet
argument_list|(
name|setSize
operator|+
literal|1
argument_list|)
argument_list|,
name|setSize
argument_list|,
name|hashFunctionForVersion
argument_list|(
name|VERSION_CURRENT
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createSetBasedOnQuality
specifier|public
specifier|static
name|FuzzySet
name|createSetBasedOnQuality
parameter_list|(
name|int
name|maxNumUniqueValues
parameter_list|,
name|float
name|desiredMaxSaturation
parameter_list|)
block|{
name|int
name|setSize
init|=
name|getNearestSetSize
argument_list|(
name|maxNumUniqueValues
argument_list|,
name|desiredMaxSaturation
argument_list|)
decl_stmt|;
return|return
operator|new
name|FuzzySet
argument_list|(
operator|new
name|FixedBitSet
argument_list|(
name|setSize
operator|+
literal|1
argument_list|)
argument_list|,
name|setSize
argument_list|,
name|hashFunctionForVersion
argument_list|(
name|VERSION_CURRENT
argument_list|)
argument_list|)
return|;
block|}
DECL|method|FuzzySet
specifier|private
name|FuzzySet
parameter_list|(
name|FixedBitSet
name|filter
parameter_list|,
name|int
name|bloomSize
parameter_list|,
name|HashFunction
name|hashFunction
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|bloomSize
operator|=
name|bloomSize
expr_stmt|;
name|this
operator|.
name|hashFunction
operator|=
name|hashFunction
expr_stmt|;
block|}
comment|/**    * The main method required for a Bloom filter which, given a value determines set membership.    * Unlike a conventional set, the fuzzy set returns NO or MAYBE rather than true or false.    * @return NO or MAYBE    */
DECL|method|contains
specifier|public
name|ContainsResult
name|contains
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|int
name|hash
init|=
name|hashFunction
operator|.
name|hash
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|hash
operator|<
literal|0
condition|)
block|{
name|hash
operator|=
name|hash
operator|*
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|mayContainValue
argument_list|(
name|hash
argument_list|)
return|;
block|}
comment|/**    * Serializes the data set to file using the following format:    *<ul>    *<li>FuzzySet --&gt;FuzzySetVersion,HashFunctionName,BloomSize,    * NumBitSetWords,BitSetWord<sup>NumBitSetWords</sup></li>     *<li>HashFunctionName --&gt; {@link DataOutput#writeString(String) String} The    * name of a ServiceProvider registered {@link HashFunction}</li>    *<li>FuzzySetVersion --&gt; {@link DataOutput#writeInt Uint32} The version number of the {@link FuzzySet} class</li>    *<li>BloomSize --&gt; {@link DataOutput#writeInt Uint32} The modulo value used    * to project hashes into the field's Bitset</li>    *<li>NumBitSetWords --&gt; {@link DataOutput#writeInt Uint32} The number of    * longs (as returned from {@link FixedBitSet#getBits})</li>    *<li>BitSetWord --&gt; {@link DataOutput#writeLong Long} A long from the array    * returned by {@link FixedBitSet#getBits}</li>    *</ul>    * @param out Data output stream    * @throws IOException If there is a low-level I/O error    */
DECL|method|serialize
specifier|public
name|void
name|serialize
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|bloomSize
argument_list|)
expr_stmt|;
name|long
index|[]
name|bits
init|=
name|filter
operator|.
name|getBits
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|bits
operator|.
name|length
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
name|bits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Can't used VLong encoding because cant cope with negative numbers
comment|// output by FixedBitSet
name|out
operator|.
name|writeLong
argument_list|(
name|bits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deserialize
specifier|public
specifier|static
name|FuzzySet
name|deserialize
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|version
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|==
name|VERSION_SPI
condition|)
block|{
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
specifier|final
name|HashFunction
name|hashFunction
init|=
name|hashFunctionForVersion
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|int
name|bloomSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|int
name|numLongs
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
name|numLongs
index|]
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
name|numLongs
condition|;
name|i
operator|++
control|)
block|{
name|longs
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
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|longs
argument_list|,
name|bloomSize
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|FuzzySet
argument_list|(
name|bits
argument_list|,
name|bloomSize
argument_list|,
name|hashFunction
argument_list|)
return|;
block|}
DECL|method|mayContainValue
specifier|private
name|ContainsResult
name|mayContainValue
parameter_list|(
name|int
name|positiveHash
parameter_list|)
block|{
assert|assert
name|positiveHash
operator|>=
literal|0
assert|;
comment|// Bloom sizes are always base 2 and so can be ANDed for a fast modulo
name|int
name|pos
init|=
name|positiveHash
operator|&
name|bloomSize
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|get
argument_list|(
name|pos
argument_list|)
condition|)
block|{
comment|// This term may be recorded in this index (but could be a collision)
return|return
name|ContainsResult
operator|.
name|MAYBE
return|;
block|}
comment|// definitely NOT in this segment
return|return
name|ContainsResult
operator|.
name|NO
return|;
block|}
comment|/**    * Records a value in the set. The referenced bytes are hashed and then modulo n'd where n is the    * chosen size of the internal bitset.    * @param value the key value to be hashed    * @throws IOException If there is a low-level I/O error    */
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|hash
init|=
name|hashFunction
operator|.
name|hash
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|hash
operator|<
literal|0
condition|)
block|{
name|hash
operator|=
name|hash
operator|*
operator|-
literal|1
expr_stmt|;
block|}
comment|// Bitmasking using bloomSize is effectively a modulo operation.
name|int
name|bloomPos
init|=
name|hash
operator|&
name|bloomSize
decl_stmt|;
name|filter
operator|.
name|set
argument_list|(
name|bloomPos
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @param targetMaxSaturation A number between 0 and 1 describing the % of bits that would ideally be set in the     * result. Lower values have better accuracy but require more space.    * @return a smaller FuzzySet or null if the current set is already over-saturated    */
DECL|method|downsize
specifier|public
name|FuzzySet
name|downsize
parameter_list|(
name|float
name|targetMaxSaturation
parameter_list|)
block|{
name|int
name|numBitsSet
init|=
name|filter
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|FixedBitSet
name|rightSizedBitSet
init|=
name|filter
decl_stmt|;
name|int
name|rightSizedBitSetSize
init|=
name|bloomSize
decl_stmt|;
comment|//Hopefully find a smaller size bitset into which we can project accumulated values while maintaining desired saturation level
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|usableBitSetSizes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|candidateBitsetSize
init|=
name|usableBitSetSizes
index|[
name|i
index|]
decl_stmt|;
name|float
name|candidateSaturation
init|=
operator|(
name|float
operator|)
name|numBitsSet
operator|/
operator|(
name|float
operator|)
name|candidateBitsetSize
decl_stmt|;
if|if
condition|(
name|candidateSaturation
operator|<=
name|targetMaxSaturation
condition|)
block|{
name|rightSizedBitSetSize
operator|=
name|candidateBitsetSize
expr_stmt|;
break|break;
block|}
block|}
comment|// Re-project the numbers to a smaller space if necessary
if|if
condition|(
name|rightSizedBitSetSize
operator|<
name|bloomSize
condition|)
block|{
comment|// Reset the choice of bitset to the smaller version
name|rightSizedBitSet
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|rightSizedBitSetSize
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Map across the bits from the large set to the smaller one
name|int
name|bitIndex
init|=
literal|0
decl_stmt|;
do|do
block|{
name|bitIndex
operator|=
name|filter
operator|.
name|nextSetBit
argument_list|(
name|bitIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|bitIndex
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|// Project the larger number into a smaller one effectively
comment|// modulo-ing by using the target bitset size as a mask
name|int
name|downSizedBitIndex
init|=
name|bitIndex
operator|&
name|rightSizedBitSetSize
decl_stmt|;
name|rightSizedBitSet
operator|.
name|set
argument_list|(
name|downSizedBitIndex
argument_list|)
expr_stmt|;
name|bitIndex
operator|++
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|(
name|bitIndex
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|bitIndex
operator|<=
name|bloomSize
operator|)
condition|)
do|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|FuzzySet
argument_list|(
name|rightSizedBitSet
argument_list|,
name|rightSizedBitSetSize
argument_list|,
name|hashFunction
argument_list|)
return|;
block|}
DECL|method|getEstimatedUniqueValues
specifier|public
name|int
name|getEstimatedUniqueValues
parameter_list|()
block|{
return|return
name|getEstimatedNumberUniqueValuesAllowingForCollisions
argument_list|(
name|bloomSize
argument_list|,
name|filter
operator|.
name|cardinality
argument_list|()
argument_list|)
return|;
block|}
comment|// Given a set size and a the number of set bits, produces an estimate of the number of unique values recorded
DECL|method|getEstimatedNumberUniqueValuesAllowingForCollisions
specifier|public
specifier|static
name|int
name|getEstimatedNumberUniqueValuesAllowingForCollisions
parameter_list|(
name|int
name|setSize
parameter_list|,
name|int
name|numRecordedBits
parameter_list|)
block|{
name|double
name|setSizeAsDouble
init|=
name|setSize
decl_stmt|;
name|double
name|numRecordedBitsAsDouble
init|=
name|numRecordedBits
decl_stmt|;
name|double
name|saturation
init|=
name|numRecordedBitsAsDouble
operator|/
name|setSizeAsDouble
decl_stmt|;
name|double
name|logInverseSaturation
init|=
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|-
name|saturation
argument_list|)
operator|*
operator|-
literal|1
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|setSizeAsDouble
operator|*
name|logInverseSaturation
argument_list|)
return|;
block|}
DECL|method|getSaturation
specifier|public
name|float
name|getSaturation
parameter_list|()
block|{
name|int
name|numBitsSet
init|=
name|filter
operator|.
name|cardinality
argument_list|()
decl_stmt|;
return|return
operator|(
name|float
operator|)
name|numBitsSet
operator|/
operator|(
name|float
operator|)
name|bloomSize
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
name|sizeOf
argument_list|(
name|filter
operator|.
name|getBits
argument_list|()
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
literal|"(hash="
operator|+
name|hashFunction
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

