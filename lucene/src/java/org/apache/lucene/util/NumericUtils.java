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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|NumericTokenStream
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericField
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|NumericRangeQuery
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|NumericRangeFilter
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_comment
comment|// TODO: Remove the commented out methods before release!
end_comment

begin_comment
comment|/**  * This is a helper class to generate prefix-encoded representations for numerical values  * and supplies converters to represent float/double values as sortable integers/longs.  *  *<p>To quickly execute range queries in Apache Lucene, a range is divided recursively  * into multiple intervals for searching: The center of the range is searched only with  * the lowest possible precision in the trie, while the boundaries are matched  * more exactly. This reduces the number of terms dramatically.  *  *<p>This class generates terms to achieve this: First the numerical integer values need to  * be converted to bytes. For that integer values (32 bit or 64 bit) are made unsigned  * and the bits are converted to ASCII chars with each 7 bit. The resulting byte[] is  * sortable like the original integer value (even using UTF-8 sort order). Each value is also  * prefixed (in the first char) by the<code>shift</code> value (number of bits removed) used  * during encoding.  *  *<p>To also index floating point numbers, this class supplies two methods to convert them  * to integer values by changing their bit layout: {@link #doubleToSortableLong},  * {@link #floatToSortableInt}. You will have no precision loss by  * converting floating point numbers to integers and back (only that the integer form  * is not usable). Other data types like dates can easily converted to longs or ints (e.g.  * date to long: {@link java.util.Date#getTime}).  *  *<p>For easy usage, the trie algorithm is implemented for indexing inside  * {@link NumericTokenStream} that can index<code>int</code>,<code>long</code>,  *<code>float</code>, and<code>double</code>. For querying,  * {@link NumericRangeQuery} and {@link NumericRangeFilter} implement the query part  * for the same data types.  *  *<p>This class can also be used, to generate lexicographically sortable (according to  * {@link BytesRef#getUTF8SortedAsUTF16Comparator()}) representations of numeric data  * types for other usages (e.g. sorting).  *  * @lucene.internal  * @since 2.9, API changed non backwards-compliant in 3.1  */
end_comment

begin_class
DECL|class|NumericUtils
specifier|public
specifier|final
class|class
name|NumericUtils
block|{
DECL|method|NumericUtils
specifier|private
name|NumericUtils
parameter_list|()
block|{}
comment|// no instance!
comment|/**    * The default precision step used by {@link NumericField}, {@link NumericTokenStream},    * {@link NumericRangeQuery}, and {@link NumericRangeFilter} as default    */
DECL|field|PRECISION_STEP_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP_DEFAULT
init|=
literal|4
decl_stmt|;
comment|/**    * Longs are stored at lower precision by shifting off lower bits. The shift count is    * stored as<code>SHIFT_START_LONG+shift</code> in the first byte    */
DECL|field|SHIFT_START_LONG
specifier|public
specifier|static
specifier|final
name|byte
name|SHIFT_START_LONG
init|=
literal|0x20
decl_stmt|;
comment|/**    * The maximum term length (used for<code>byte[]</code> buffer size)    * for encoding<code>long</code> values.    * @see #longToPrefixCoded(long,int,BytesRef)    */
DECL|field|BUF_SIZE_LONG
specifier|public
specifier|static
specifier|final
name|int
name|BUF_SIZE_LONG
init|=
literal|63
operator|/
literal|7
operator|+
literal|2
decl_stmt|;
comment|/**    * Integers are stored at lower precision by shifting off lower bits. The shift count is    * stored as<code>SHIFT_START_INT+shift</code> in the first byte    */
DECL|field|SHIFT_START_INT
specifier|public
specifier|static
specifier|final
name|byte
name|SHIFT_START_INT
init|=
literal|0x60
decl_stmt|;
comment|/**    * The maximum term length (used for<code>byte[]</code> buffer size)    * for encoding<code>int</code> values.    * @see #intToPrefixCoded(int,int,BytesRef)    */
DECL|field|BUF_SIZE_INT
specifier|public
specifier|static
specifier|final
name|int
name|BUF_SIZE_INT
init|=
literal|31
operator|/
literal|7
operator|+
literal|2
decl_stmt|;
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link NumericTokenStream}.    * @param val the numeric value    * @param shift how many bits to strip from the right    * @param bytes will contain the encoded value    * @return the hash code for indexing (TermsHash)    */
DECL|method|longToPrefixCoded
specifier|public
specifier|static
name|int
name|longToPrefixCoded
parameter_list|(
specifier|final
name|long
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|shift
operator|>
literal|63
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shift value, must be 0..63"
argument_list|)
throw|;
if|if
condition|(
name|bytes
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|NumericUtils
operator|.
name|BUF_SIZE_LONG
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|bytes
operator|.
name|length
operator|<
name|NumericUtils
operator|.
name|BUF_SIZE_LONG
condition|)
block|{
name|bytes
operator|.
name|grow
argument_list|(
name|NumericUtils
operator|.
name|BUF_SIZE_LONG
argument_list|)
expr_stmt|;
block|}
name|int
name|hash
decl_stmt|,
name|nChars
init|=
operator|(
literal|63
operator|-
name|shift
operator|)
operator|/
literal|7
operator|+
literal|1
decl_stmt|;
name|bytes
operator|.
name|length
operator|=
name|nChars
operator|+
literal|1
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|hash
operator|=
operator|(
name|SHIFT_START_LONG
operator|+
name|shift
operator|)
argument_list|)
expr_stmt|;
name|long
name|sortableBits
init|=
name|val
operator|^
literal|0x8000000000000000L
decl_stmt|;
name|sortableBits
operator|>>>=
name|shift
expr_stmt|;
while|while
condition|(
name|nChars
operator|>
literal|0
condition|)
block|{
comment|// Store 7 bits per byte for compatibility
comment|// with UTF-8 encoding of terms
name|bytes
operator|.
name|bytes
index|[
name|nChars
operator|--
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|sortableBits
operator|&
literal|0x7f
argument_list|)
expr_stmt|;
name|sortableBits
operator|>>>=
literal|7
expr_stmt|;
block|}
comment|// calculate hash
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|bytes
operator|.
name|bytes
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link LongRangeBuilder}.    * @param val the numeric value    * @param shift how many bits to strip from the right    * @deprecated This method is no longer needed!    *   @Deprecated   public static String longToPrefixCoded(final long val, final int shift) {     final BytesRef buffer = new BytesRef(BUF_SIZE_LONG);     longToPrefixCoded(val, shift, buffer);     return buffer.utf8ToString();   }*/
comment|/**    * This is a convenience method, that returns prefix coded bits of a long without    * reducing the precision. It can be used to store the full precision value as a    * stored field in index.    *<p>To decode, use {@link #prefixCodedToLong}.    * @deprecated This method is no longer needed!    *   @Deprecated   public static String longToPrefixCoded(final long val) {     return longToPrefixCoded(val, 0);   }*/
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link NumericTokenStream}.    * @param val the numeric value    * @param shift how many bits to strip from the right    * @param bytes will contain the encoded value    * @return the hash code for indexing (TermsHash)    */
DECL|method|intToPrefixCoded
specifier|public
specifier|static
name|int
name|intToPrefixCoded
parameter_list|(
specifier|final
name|int
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|shift
operator|>
literal|31
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shift value, must be 0..31"
argument_list|)
throw|;
if|if
condition|(
name|bytes
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|NumericUtils
operator|.
name|BUF_SIZE_INT
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|bytes
operator|.
name|length
operator|<
name|NumericUtils
operator|.
name|BUF_SIZE_INT
condition|)
block|{
name|bytes
operator|.
name|grow
argument_list|(
name|NumericUtils
operator|.
name|BUF_SIZE_INT
argument_list|)
expr_stmt|;
block|}
name|int
name|hash
decl_stmt|,
name|nChars
init|=
operator|(
literal|31
operator|-
name|shift
operator|)
operator|/
literal|7
operator|+
literal|1
decl_stmt|;
name|bytes
operator|.
name|length
operator|=
name|nChars
operator|+
literal|1
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|hash
operator|=
operator|(
name|SHIFT_START_INT
operator|+
name|shift
operator|)
argument_list|)
expr_stmt|;
name|int
name|sortableBits
init|=
name|val
operator|^
literal|0x80000000
decl_stmt|;
name|sortableBits
operator|>>>=
name|shift
expr_stmt|;
while|while
condition|(
name|nChars
operator|>
literal|0
condition|)
block|{
comment|// Store 7 bits per byte for compatibility
comment|// with UTF-8 encoding of terms
name|bytes
operator|.
name|bytes
index|[
name|nChars
operator|--
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|sortableBits
operator|&
literal|0x7f
argument_list|)
expr_stmt|;
name|sortableBits
operator|>>>=
literal|7
expr_stmt|;
block|}
comment|// calculate hash
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|bytes
operator|.
name|bytes
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link IntRangeBuilder}.    * @param val the numeric value    * @param shift how many bits to strip from the right    * @deprecated This method is no longer needed!    *   @Deprecated   public static String intToPrefixCoded(final int val, final int shift) {     final BytesRef buffer = new BytesRef(BUF_SIZE_INT);     intToPrefixCoded(val, shift, buffer);     return buffer.utf8ToString();   }*/
comment|/**    * This is a convenience method, that returns prefix coded bits of an int without    * reducing the precision. It can be used to store the full precision value as a    * stored field in index.    *<p>To decode, use {@link #prefixCodedToInt}.    * @deprecated This method is no longer needed!    *   @Deprecated   public static String intToPrefixCoded(final int val) {     return intToPrefixCoded(val, 0);   }*/
comment|/**    * Returns a long from prefixCoded characters.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode e.g. a stored field.    * @throws NumberFormatException if the supplied string is    * not correctly prefix encoded.    * @see #longToPrefixCoded(long)    * @deprecated This method is no longer needed!    *   @Deprecated   public static long prefixCodedToLong(final String prefixCoded) {     return prefixCodedToLong(new BytesRef(prefixCoded));   }*/
comment|/**    * Returns the shift value from a prefix encoded {@code long}.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    */
DECL|method|getPrefixCodedLongShift
specifier|public
specifier|static
name|int
name|getPrefixCodedLongShift
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|bytes
index|[
name|val
operator|.
name|offset
index|]
operator|-
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|63
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid shift value in prefixCoded bytes (is encoded value really an INT?)"
argument_list|)
throw|;
return|return
name|shift
return|;
block|}
comment|/**    * Returns the shift value from a prefix encoded {@code int}.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    */
DECL|method|getPrefixCodedIntShift
specifier|public
specifier|static
name|int
name|getPrefixCodedIntShift
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|bytes
index|[
name|val
operator|.
name|offset
index|]
operator|-
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|31
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid shift value in prefixCoded bytes (is encoded value really an INT?)"
argument_list|)
throw|;
return|return
name|shift
return|;
block|}
comment|/**    * Returns a long from prefixCoded bytes.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode a term's value.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    * @see #longToPrefixCoded(long,int,BytesRef)    */
DECL|method|prefixCodedToLong
specifier|public
specifier|static
name|long
name|prefixCodedToLong
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
name|long
name|sortableBits
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|val
operator|.
name|offset
operator|+
literal|1
init|,
name|limit
init|=
name|val
operator|.
name|offset
operator|+
name|val
operator|.
name|length
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|sortableBits
operator|<<=
literal|7
expr_stmt|;
specifier|final
name|byte
name|b
init|=
name|val
operator|.
name|bytes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid prefixCoded numerical value representation (byte "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
operator|+
literal|" at position "
operator|+
operator|(
name|i
operator|-
name|val
operator|.
name|offset
operator|)
operator|+
literal|" is invalid)"
argument_list|)
throw|;
block|}
name|sortableBits
operator||=
name|b
expr_stmt|;
block|}
return|return
operator|(
name|sortableBits
operator|<<
name|getPrefixCodedLongShift
argument_list|(
name|val
argument_list|)
operator|)
operator|^
literal|0x8000000000000000L
return|;
block|}
comment|/**    * Returns an int from prefixCoded characters.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode a term's value.    * @throws NumberFormatException if the supplied string is    * not correctly prefix encoded.    * @see #intToPrefixCoded(int)    * @deprecated This method is no longer needed!    *   @Deprecated   public static int prefixCodedToInt(final String prefixCoded) {     return prefixCodedToInt(new BytesRef(prefixCoded));   }*/
comment|/**    * Returns an int from prefixCoded bytes.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode a term's value.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    * @see #intToPrefixCoded(int,int,BytesRef)    */
DECL|method|prefixCodedToInt
specifier|public
specifier|static
name|int
name|prefixCodedToInt
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
name|int
name|sortableBits
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|val
operator|.
name|offset
operator|+
literal|1
init|,
name|limit
init|=
name|val
operator|.
name|offset
operator|+
name|val
operator|.
name|length
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|sortableBits
operator|<<=
literal|7
expr_stmt|;
specifier|final
name|byte
name|b
init|=
name|val
operator|.
name|bytes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid prefixCoded numerical value representation (byte "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
operator|+
literal|" at position "
operator|+
operator|(
name|i
operator|-
name|val
operator|.
name|offset
operator|)
operator|+
literal|" is invalid)"
argument_list|)
throw|;
block|}
name|sortableBits
operator||=
name|b
expr_stmt|;
block|}
return|return
operator|(
name|sortableBits
operator|<<
name|getPrefixCodedIntShift
argument_list|(
name|val
argument_list|)
operator|)
operator|^
literal|0x80000000
return|;
block|}
comment|/**    * Converts a<code>double</code> value to a sortable signed<code>long</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;double format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as long.    * By this the precision is not reduced, but the value can easily used as a long.    * @see #sortableLongToDouble    */
DECL|method|doubleToSortableLong
specifier|public
specifier|static
name|long
name|doubleToSortableLong
parameter_list|(
name|double
name|val
parameter_list|)
block|{
name|long
name|f
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|<
literal|0
condition|)
name|f
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * Convenience method: this just returns:    *   longToPrefixCoded(doubleToSortableLong(val))    * @deprecated This method is no longer needed!    *   @Deprecated   public static String doubleToPrefixCoded(double val) {     return longToPrefixCoded(doubleToSortableLong(val));   }*/
comment|/**    * Converts a sortable<code>long</code> back to a<code>double</code>.    * @see #doubleToSortableLong    */
DECL|method|sortableLongToDouble
specifier|public
specifier|static
name|double
name|sortableLongToDouble
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|<
literal|0
condition|)
name|val
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/**    * Convenience method: this just returns:    *    sortableLongToDouble(prefixCodedToLong(val))    * @deprecated This method is no longer needed!    *   @Deprecated   public static double prefixCodedToDouble(String val) {     return sortableLongToDouble(prefixCodedToLong(val));   }*/
comment|/**    * Converts a<code>float</code> value to a sortable signed<code>int</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;float format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as int.    * By this the precision is not reduced, but the value can easily used as an int.    * @see #sortableIntToFloat    */
DECL|method|floatToSortableInt
specifier|public
specifier|static
name|int
name|floatToSortableInt
parameter_list|(
name|float
name|val
parameter_list|)
block|{
name|int
name|f
init|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|<
literal|0
condition|)
name|f
operator|^=
literal|0x7fffffff
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * Convenience method: this just returns:    *   intToPrefixCoded(floatToSortableInt(val))    * @deprecated This method is no longer needed!    *   @Deprecated   public static String floatToPrefixCoded(float val) {     return intToPrefixCoded(floatToSortableInt(val));   }*/
comment|/**    * Converts a sortable<code>int</code> back to a<code>float</code>.    * @see #floatToSortableInt    */
DECL|method|sortableIntToFloat
specifier|public
specifier|static
name|float
name|sortableIntToFloat
parameter_list|(
name|int
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|<
literal|0
condition|)
name|val
operator|^=
literal|0x7fffffff
expr_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/**    * Convenience method: this just returns:    *    sortableIntToFloat(prefixCodedToInt(val))    * @deprecated This method is no longer needed!    *   @Deprecated   public static float prefixCodedToFloat(String val) {     return sortableIntToFloat(prefixCodedToInt(val));   }*/
comment|/**    * Splits a long range recursively.    * You may implement a builder that adds clauses to a    * {@link org.apache.lucene.search.BooleanQuery} for each call to its    * {@link LongRangeBuilder#addRange(BytesRef,BytesRef)}    * method.    *<p>This method is used by {@link NumericRangeQuery}.    */
DECL|method|splitLongRange
specifier|public
specifier|static
name|void
name|splitLongRange
parameter_list|(
specifier|final
name|LongRangeBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|long
name|minBound
parameter_list|,
specifier|final
name|long
name|maxBound
parameter_list|)
block|{
name|splitRange
argument_list|(
name|builder
argument_list|,
literal|64
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
block|}
comment|/**    * Splits an int range recursively.    * You may implement a builder that adds clauses to a    * {@link org.apache.lucene.search.BooleanQuery} for each call to its    * {@link IntRangeBuilder#addRange(BytesRef,BytesRef)}    * method.    *<p>This method is used by {@link NumericRangeQuery}.    */
DECL|method|splitIntRange
specifier|public
specifier|static
name|void
name|splitIntRange
parameter_list|(
specifier|final
name|IntRangeBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|int
name|minBound
parameter_list|,
specifier|final
name|int
name|maxBound
parameter_list|)
block|{
name|splitRange
argument_list|(
name|builder
argument_list|,
literal|32
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
block|}
comment|/** This helper does the splitting for both 32 and 64 bit. */
DECL|method|splitRange
specifier|private
specifier|static
name|void
name|splitRange
parameter_list|(
specifier|final
name|Object
name|builder
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|long
name|minBound
parameter_list|,
name|long
name|maxBound
parameter_list|)
block|{
if|if
condition|(
name|precisionStep
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep must be>=1"
argument_list|)
throw|;
if|if
condition|(
name|minBound
operator|>
name|maxBound
condition|)
return|return;
for|for
control|(
name|int
name|shift
init|=
literal|0
init|;
condition|;
name|shift
operator|+=
name|precisionStep
control|)
block|{
comment|// calculate new bounds for inner precision
specifier|final
name|long
name|diff
init|=
literal|1L
operator|<<
operator|(
name|shift
operator|+
name|precisionStep
operator|)
decl_stmt|,
name|mask
init|=
operator|(
operator|(
literal|1L
operator|<<
name|precisionStep
operator|)
operator|-
literal|1L
operator|)
operator|<<
name|shift
decl_stmt|;
specifier|final
name|boolean
name|hasLower
init|=
operator|(
name|minBound
operator|&
name|mask
operator|)
operator|!=
literal|0L
decl_stmt|,
name|hasUpper
init|=
operator|(
name|maxBound
operator|&
name|mask
operator|)
operator|!=
name|mask
decl_stmt|;
specifier|final
name|long
name|nextMinBound
init|=
operator|(
name|hasLower
condition|?
operator|(
name|minBound
operator|+
name|diff
operator|)
else|:
name|minBound
operator|)
operator|&
operator|~
name|mask
decl_stmt|,
name|nextMaxBound
init|=
operator|(
name|hasUpper
condition|?
operator|(
name|maxBound
operator|-
name|diff
operator|)
else|:
name|maxBound
operator|)
operator|&
operator|~
name|mask
decl_stmt|;
if|if
condition|(
name|shift
operator|+
name|precisionStep
operator|>=
name|valSize
operator|||
name|nextMinBound
operator|>
name|nextMaxBound
condition|)
block|{
comment|// We are in the lowest precision or the next precision is not available.
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
comment|// exit the split recursion loop
break|break;
block|}
if|if
condition|(
name|hasLower
condition|)
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|minBound
argument_list|,
name|minBound
operator||
name|mask
argument_list|,
name|shift
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasUpper
condition|)
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|maxBound
operator|&
operator|~
name|mask
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
comment|// recurse to next precision
name|minBound
operator|=
name|nextMinBound
expr_stmt|;
name|maxBound
operator|=
name|nextMaxBound
expr_stmt|;
block|}
block|}
comment|/** Helper that delegates to correct range builder */
DECL|method|addRange
specifier|private
specifier|static
name|void
name|addRange
parameter_list|(
specifier|final
name|Object
name|builder
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
name|long
name|minBound
parameter_list|,
name|long
name|maxBound
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
comment|// for the max bound set all lower bits (that were shifted away):
comment|// this is important for testing or other usages of the splitted range
comment|// (e.g. to reconstruct the full range). The prefixEncoding will remove
comment|// the bits anyway, so they do not hurt!
name|maxBound
operator||=
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
expr_stmt|;
comment|// delegate to correct range builder
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
operator|(
operator|(
name|LongRangeBuilder
operator|)
name|builder
operator|)
operator|.
name|addRange
argument_list|(
name|minBound
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
operator|(
operator|(
name|IntRangeBuilder
operator|)
name|builder
operator|)
operator|.
name|addRange
argument_list|(
operator|(
name|int
operator|)
name|minBound
argument_list|,
operator|(
name|int
operator|)
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Should not happen!
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Callback for {@link #splitLongRange}.    * You need to overwrite only one of the methods.    * @lucene.internal    * @since 2.9, API changed non backwards-compliant in 3.1    */
DECL|class|LongRangeBuilder
specifier|public
specifier|static
specifier|abstract
class|class
name|LongRangeBuilder
block|{
comment|/**      * Overwrite this method, if you like to receive the already prefix encoded range bounds.      * You can directly build classical (inclusive) range queries from them.      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
name|BytesRef
name|minPrefixCoded
parameter_list|,
name|BytesRef
name|maxPrefixCoded
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Overwrite this method, if you like to receive the raw long range bounds.      * You can use this for e.g. debugging purposes (print out range bounds).      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
specifier|final
name|long
name|min
parameter_list|,
specifier|final
name|long
name|max
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
specifier|final
name|BytesRef
name|minBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|BUF_SIZE_LONG
argument_list|)
decl_stmt|,
name|maxBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|BUF_SIZE_LONG
argument_list|)
decl_stmt|;
name|longToPrefixCoded
argument_list|(
name|min
argument_list|,
name|shift
argument_list|,
name|minBytes
argument_list|)
expr_stmt|;
name|longToPrefixCoded
argument_list|(
name|max
argument_list|,
name|shift
argument_list|,
name|maxBytes
argument_list|)
expr_stmt|;
name|addRange
argument_list|(
name|minBytes
argument_list|,
name|maxBytes
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Callback for {@link #splitIntRange}.    * You need to overwrite only one of the methods.    * @lucene.internal    * @since 2.9, API changed non backwards-compliant in 3.1    */
DECL|class|IntRangeBuilder
specifier|public
specifier|static
specifier|abstract
class|class
name|IntRangeBuilder
block|{
comment|/**      * Overwrite this method, if you like to receive the already prefix encoded range bounds.      * You can directly build classical range (inclusive) queries from them.      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
name|BytesRef
name|minPrefixCoded
parameter_list|,
name|BytesRef
name|maxPrefixCoded
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Overwrite this method, if you like to receive the raw int range bounds.      * You can use this for e.g. debugging purposes (print out range bounds).      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
specifier|final
name|int
name|min
parameter_list|,
specifier|final
name|int
name|max
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
specifier|final
name|BytesRef
name|minBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|BUF_SIZE_INT
argument_list|)
decl_stmt|,
name|maxBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|BUF_SIZE_INT
argument_list|)
decl_stmt|;
name|intToPrefixCoded
argument_list|(
name|min
argument_list|,
name|shift
argument_list|,
name|minBytes
argument_list|)
expr_stmt|;
name|intToPrefixCoded
argument_list|(
name|max
argument_list|,
name|shift
argument_list|,
name|maxBytes
argument_list|)
expr_stmt|;
name|addRange
argument_list|(
name|minBytes
argument_list|,
name|maxBytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

