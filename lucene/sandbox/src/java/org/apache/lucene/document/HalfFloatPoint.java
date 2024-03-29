begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

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
name|Collection
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
name|index
operator|.
name|PointValues
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
name|PointInSetQuery
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
name|PointRangeQuery
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
name|Query
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

begin_comment
comment|/**  * An indexed {@code half-float} field for fast range filters. If you also  * need to store the value, you should add a separate {@link StoredField} instance.  * If you need doc values, you can store them in a {@link NumericDocValuesField}  * and use {@link #halfFloatToSortableShort} and  * {@link #sortableShortToHalfFloat} for encoding/decoding.  *<p>  * The API takes floats, but they will be encoded to half-floats before being  * indexed. In case the provided floats cannot be represented accurately as a  * half float, they will be rounded to the closest value that can be  * represented as a half float. In case of tie, values will be rounded to the  * value that has a zero as its least significant bit.  *<p>  * Finding all documents within an N-dimensional at search time is  * efficient.  Multiple values for the same field in one document  * is allowed.  *<p>  * This field defines static factory methods for creating common queries:  *<ul>  *<li>{@link #newExactQuery(String, float)} for matching an exact 1D point.  *<li>{@link #newSetQuery(String, float...)} for matching a set of 1D values.  *<li>{@link #newRangeQuery(String, float, float)} for matching a 1D range.  *<li>{@link #newRangeQuery(String, float[], float[])} for matching points/ranges in n-dimensional space.  *</ul>  * @see PointValues  */
end_comment

begin_class
DECL|class|HalfFloatPoint
specifier|public
specifier|final
class|class
name|HalfFloatPoint
extends|extends
name|Field
block|{
comment|/** The number of bytes used to represent a half-float value. */
DECL|field|BYTES
specifier|public
specifier|static
specifier|final
name|int
name|BYTES
init|=
literal|2
decl_stmt|;
comment|/**    * Return the first half float which is immediately greater than {@code v}.    * If the argument is {@link Float#NaN} then the return value is    * {@link Float#NaN}. If the argument is {@link Float#POSITIVE_INFINITY}    * then the return value is {@link Float#POSITIVE_INFINITY}.    */
DECL|method|nextUp
specifier|public
specifier|static
name|float
name|nextUp
parameter_list|(
name|float
name|v
parameter_list|)
block|{
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|v
argument_list|)
operator|||
name|v
operator|==
name|Float
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
return|return
name|v
return|;
block|}
name|short
name|s
init|=
name|halfFloatToSortableShort
argument_list|(
name|v
argument_list|)
decl_stmt|;
comment|// if the float does not represent a half float accurately then just
comment|// converting back might give us the value we are looking for
name|float
name|r
init|=
name|sortableShortToHalfFloat
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|<=
name|v
condition|)
block|{
name|r
operator|=
name|sortableShortToHalfFloat
argument_list|(
call|(
name|short
call|)
argument_list|(
name|s
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
comment|/**    * Return the first half float which is immediately smaller than {@code v}.    * If the argument is {@link Float#NaN} then the return value is    * {@link Float#NaN}. If the argument is {@link Float#NEGATIVE_INFINITY}    * then the return value is {@link Float#NEGATIVE_INFINITY}.    */
DECL|method|nextDown
specifier|public
specifier|static
name|float
name|nextDown
parameter_list|(
name|float
name|v
parameter_list|)
block|{
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|v
argument_list|)
operator|||
name|v
operator|==
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
return|return
name|v
return|;
block|}
name|short
name|s
init|=
name|halfFloatToSortableShort
argument_list|(
name|v
argument_list|)
decl_stmt|;
comment|// if the float does not represent a half float accurately then just
comment|// converting back might give us the value we are looking for
name|float
name|r
init|=
name|sortableShortToHalfFloat
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|>=
name|v
condition|)
block|{
name|r
operator|=
name|sortableShortToHalfFloat
argument_list|(
call|(
name|short
call|)
argument_list|(
name|s
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
comment|/** Convert a half-float to a short value that maintains ordering. */
DECL|method|halfFloatToSortableShort
specifier|public
specifier|static
name|short
name|halfFloatToSortableShort
parameter_list|(
name|float
name|v
parameter_list|)
block|{
return|return
name|sortableShortBits
argument_list|(
name|halfFloatToShortBits
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
comment|/** Convert short bits to a half-float value that maintains ordering. */
DECL|method|sortableShortToHalfFloat
specifier|public
specifier|static
name|float
name|sortableShortToHalfFloat
parameter_list|(
name|short
name|bits
parameter_list|)
block|{
return|return
name|shortBitsToHalfFloat
argument_list|(
name|sortableShortBits
argument_list|(
name|bits
argument_list|)
argument_list|)
return|;
block|}
DECL|method|sortableShortBits
specifier|private
specifier|static
name|short
name|sortableShortBits
parameter_list|(
name|short
name|s
parameter_list|)
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|s
operator|^
operator|(
name|s
operator|>>
literal|15
operator|)
operator|&
literal|0x7fff
argument_list|)
return|;
block|}
DECL|method|halfFloatToShortBits
specifier|static
name|short
name|halfFloatToShortBits
parameter_list|(
name|float
name|v
parameter_list|)
block|{
name|int
name|floatBits
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|int
name|sign
init|=
name|floatBits
operator|>>>
literal|31
decl_stmt|;
name|int
name|exp
init|=
operator|(
name|floatBits
operator|>>>
literal|23
operator|)
operator|&
literal|0xff
decl_stmt|;
name|int
name|mantissa
init|=
name|floatBits
operator|&
literal|0x7fffff
decl_stmt|;
if|if
condition|(
name|exp
operator|==
literal|0xff
condition|)
block|{
comment|// preserve NaN and Infinity
name|exp
operator|=
literal|0x1f
expr_stmt|;
name|mantissa
operator|>>>=
operator|(
literal|23
operator|-
literal|10
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exp
operator|==
literal|0x00
condition|)
block|{
comment|// denormal float rounded to zero since even the largest denormal float
comment|// cannot be represented as a half float
name|mantissa
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|exp
operator|=
name|exp
operator|-
literal|127
operator|+
literal|15
expr_stmt|;
if|if
condition|(
name|exp
operator|>=
literal|0x1f
condition|)
block|{
comment|// too large, make it infinity
name|exp
operator|=
literal|0x1f
expr_stmt|;
name|mantissa
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|exp
operator|<=
literal|0
condition|)
block|{
comment|// we need to convert to a denormal representation
name|int
name|shift
init|=
literal|23
operator|-
literal|10
operator|-
name|exp
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|shift
operator|>=
literal|32
condition|)
block|{
comment|// need a special case since shifts are mod 32...
name|exp
operator|=
literal|0
expr_stmt|;
name|mantissa
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// add the implicit bit
name|mantissa
operator||=
literal|0x800000
expr_stmt|;
name|mantissa
operator|=
name|roundShift
argument_list|(
name|mantissa
argument_list|,
name|shift
argument_list|)
expr_stmt|;
name|exp
operator|=
name|mantissa
operator|>>>
literal|10
expr_stmt|;
name|mantissa
operator|&=
literal|0x3ff
expr_stmt|;
block|}
block|}
else|else
block|{
name|mantissa
operator|=
name|roundShift
argument_list|(
operator|(
name|exp
operator|<<
literal|23
operator|)
operator||
name|mantissa
argument_list|,
literal|23
operator|-
literal|10
argument_list|)
expr_stmt|;
name|exp
operator|=
name|mantissa
operator|>>>
literal|10
expr_stmt|;
name|mantissa
operator|&=
literal|0x3ff
expr_stmt|;
block|}
block|}
return|return
call|(
name|short
call|)
argument_list|(
operator|(
name|sign
operator|<<
literal|15
operator|)
operator||
operator|(
name|exp
operator|<<
literal|10
operator|)
operator||
name|mantissa
argument_list|)
return|;
block|}
comment|// divide by 2^shift and round to the closest int
comment|// round to even in case of tie
DECL|method|roundShift
specifier|static
name|int
name|roundShift
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|shift
parameter_list|)
block|{
assert|assert
name|shift
operator|>
literal|0
assert|;
name|i
operator|+=
literal|1
operator|<<
operator|(
name|shift
operator|-
literal|1
operator|)
expr_stmt|;
comment|// add 2^(shift-1) so that we round rather than truncate
name|i
operator|-=
operator|(
name|i
operator|>>>
name|shift
operator|)
operator|&
literal|1
expr_stmt|;
comment|// and subtract the shift-th bit so that we round to even in case of tie
return|return
name|i
operator|>>>
name|shift
return|;
block|}
DECL|method|shortBitsToHalfFloat
specifier|static
name|float
name|shortBitsToHalfFloat
parameter_list|(
name|short
name|s
parameter_list|)
block|{
name|int
name|sign
init|=
name|s
operator|>>>
literal|15
decl_stmt|;
name|int
name|exp
init|=
operator|(
name|s
operator|>>>
literal|10
operator|)
operator|&
literal|0x1f
decl_stmt|;
name|int
name|mantissa
init|=
name|s
operator|&
literal|0x3ff
decl_stmt|;
if|if
condition|(
name|exp
operator|==
literal|0x1f
condition|)
block|{
comment|// NaN or infinities
name|exp
operator|=
literal|0xff
expr_stmt|;
name|mantissa
operator|<<=
operator|(
literal|23
operator|-
literal|10
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mantissa
operator|==
literal|0
operator|&&
name|exp
operator|==
literal|0
condition|)
block|{
comment|// zero
block|}
else|else
block|{
if|if
condition|(
name|exp
operator|==
literal|0
condition|)
block|{
comment|// denormal half float becomes a normal float
name|int
name|shift
init|=
name|Integer
operator|.
name|numberOfLeadingZeros
argument_list|(
name|mantissa
argument_list|)
operator|-
operator|(
literal|32
operator|-
literal|11
operator|)
decl_stmt|;
name|mantissa
operator|=
operator|(
name|mantissa
operator|<<
name|shift
operator|)
operator|&
literal|0x3ff
expr_stmt|;
comment|// clear the implicit bit
name|exp
operator|=
name|exp
operator|-
name|shift
operator|+
literal|1
expr_stmt|;
block|}
name|exp
operator|=
name|exp
operator|+
literal|127
operator|-
literal|15
expr_stmt|;
name|mantissa
operator|<<=
operator|(
literal|23
operator|-
literal|10
operator|)
expr_stmt|;
block|}
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|sign
operator|<<
literal|31
operator|)
operator||
operator|(
name|exp
operator|<<
literal|23
operator|)
operator||
name|mantissa
argument_list|)
return|;
block|}
DECL|method|shortToSortableBytes
specifier|static
name|void
name|shortToSortableBytes
parameter_list|(
name|short
name|value
parameter_list|,
name|byte
index|[]
name|result
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
comment|// Flip the sign bit, so negative shorts sort before positive shorts correctly:
name|value
operator|^=
literal|0x8000
expr_stmt|;
name|result
index|[
name|offset
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|result
index|[
name|offset
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
block|}
DECL|method|sortableBytesToShort
specifier|static
name|short
name|sortableBytesToShort
parameter_list|(
name|byte
index|[]
name|encoded
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|short
name|x
init|=
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
name|encoded
index|[
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|encoded
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
argument_list|)
decl_stmt|;
comment|// Re-flip the sign bit to restore the original value:
return|return
call|(
name|short
call|)
argument_list|(
name|x
operator|^
literal|0x8000
argument_list|)
return|;
block|}
DECL|method|getType
specifier|private
specifier|static
name|FieldType
name|getType
parameter_list|(
name|int
name|numDims
parameter_list|)
block|{
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|type
operator|.
name|setDimensions
argument_list|(
name|numDims
argument_list|,
name|BYTES
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|setFloatValue
specifier|public
name|void
name|setFloatValue
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|setFloatValues
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Change the values of this field */
DECL|method|setFloatValues
specifier|public
name|void
name|setFloatValues
parameter_list|(
name|float
modifier|...
name|point
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|!=
name|point
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this field (name="
operator|+
name|name
operator|+
literal|") uses "
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" dimensions; cannot change to (incoming) "
operator|+
name|point
operator|.
name|length
operator|+
literal|" dimensions"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|pack
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBytesValue
specifier|public
name|void
name|setBytesValue
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from float to BytesRef"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
if|if
condition|(
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this field (name="
operator|+
name|name
operator|+
literal|") uses "
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" dimensions; cannot convert to a single numeric value"
argument_list|)
throw|;
block|}
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|BYTES
assert|;
return|return
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
return|;
block|}
DECL|method|pack
specifier|private
specifier|static
name|BytesRef
name|pack
parameter_list|(
name|float
modifier|...
name|point
parameter_list|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point must not be 0 dimensions"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|packed
init|=
operator|new
name|byte
index|[
name|point
operator|.
name|length
operator|*
name|BYTES
index|]
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|point
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|encodeDimension
argument_list|(
name|point
index|[
name|dim
index|]
argument_list|,
name|packed
argument_list|,
name|dim
operator|*
name|BYTES
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|packed
argument_list|)
return|;
block|}
comment|/** Creates a new FloatPoint, indexing the    *  provided N-dimensional float point.    *    *  @param name field name    *  @param point float[] value    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|HalfFloatPoint
specifier|public
name|HalfFloatPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|float
modifier|...
name|point
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pack
argument_list|(
name|point
argument_list|)
argument_list|,
name|getType
argument_list|(
name|point
operator|.
name|length
argument_list|)
argument_list|)
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
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|type
operator|.
name|pointDimensionCount
argument_list|()
condition|;
name|dim
operator|++
control|)
block|{
if|if
condition|(
name|dim
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
name|dim
operator|*
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// public helper methods (e.g. for queries)
comment|/** Encode single float dimension */
DECL|method|encodeDimension
specifier|public
specifier|static
name|void
name|encodeDimension
parameter_list|(
name|float
name|value
parameter_list|,
name|byte
name|dest
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|shortToSortableBytes
argument_list|(
name|halfFloatToSortableShort
argument_list|(
name|value
argument_list|)
argument_list|,
name|dest
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
comment|/** Decode single float dimension */
DECL|method|decodeDimension
specifier|public
specifier|static
name|float
name|decodeDimension
parameter_list|(
name|byte
name|value
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|sortableShortToHalfFloat
argument_list|(
name|sortableBytesToShort
argument_list|(
name|value
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|// static methods for generating queries
comment|/**    * Create a query for matching an exact half-float value. It will be rounded    * to the closest half-float if {@code value} cannot be represented accurately    * as a half-float.    *<p>    * This is for simple one-dimension points, for multidimensional points use    * {@link #newRangeQuery(String, float[], float[])} instead.    *    * @param field field name. must not be {@code null}.    * @param value half-float value    * @throws IllegalArgumentException if {@code field} is null.    * @return a query matching documents with this exact value    */
DECL|method|newExactQuery
specifier|public
specifier|static
name|Query
name|newExactQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|value
parameter_list|)
block|{
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * Create a range query for half-float values. Bounds will be rounded to the    * closest half-float if they cannot be represented accurately as a    * half-float.    *<p>    * This is for simple one-dimension ranges, for multidimensional ranges use    * {@link #newRangeQuery(String, float[], float[])} instead.    *<p>    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting {@code lowerValue = Float.NEGATIVE_INFINITY} or {@code upperValue = Float.POSITIVE_INFINITY}.    *<p> Ranges are inclusive. For exclusive ranges, pass {@code nextUp(lowerValue)}    * or {@code nextDown(upperValue)}.    *<p>    * Range comparisons are consistent with {@link Float#compareTo(Float)}.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range (inclusive).    * @param upperValue upper portion of the range (inclusive).    * @throws IllegalArgumentException if {@code field} is null.    * @return a query matching documents within this range.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|lowerValue
parameter_list|,
name|float
name|upperValue
parameter_list|)
block|{
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|float
index|[]
block|{
name|lowerValue
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
name|upperValue
block|}
argument_list|)
return|;
block|}
comment|/**    * Create a range query for n-dimensional half-float values. Bounds will be    * rounded to the closest half-float if they cannot be represented accurately    * as a half-float.    *<p>    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting {@code lowerValue[i] = Float.NEGATIVE_INFINITY} or {@code upperValue[i] = Float.POSITIVE_INFINITY}.    *<p> Ranges are inclusive. For exclusive ranges, pass {@code nextUp(lowerValue[i])}    * or {@code nextDown(upperValue[i])}.    *<p>    * Range comparisons are consistent with {@link Float#compareTo(Float)}.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range (inclusive). must not be {@code null}.    * @param upperValue upper portion of the range (inclusive). must not be {@code null}.    * @throws IllegalArgumentException if {@code field} is null, if {@code lowerValue} is null, if {@code upperValue} is null,    *                                  or if {@code lowerValue.length != upperValue.length}    * @return a query matching documents within this range.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|float
index|[]
name|lowerValue
parameter_list|,
name|float
index|[]
name|upperValue
parameter_list|)
block|{
name|PointRangeQuery
operator|.
name|checkArgs
argument_list|(
name|field
argument_list|,
name|lowerValue
argument_list|,
name|upperValue
argument_list|)
expr_stmt|;
return|return
operator|new
name|PointRangeQuery
argument_list|(
name|field
argument_list|,
name|pack
argument_list|(
name|lowerValue
argument_list|)
operator|.
name|bytes
argument_list|,
name|pack
argument_list|(
name|upperValue
argument_list|)
operator|.
name|bytes
argument_list|,
name|lowerValue
operator|.
name|length
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|int
name|dimension
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|decodeDimension
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Create a query matching any of the specified 1D values.    * This is the points equivalent of {@code TermsQuery}.    * Values will be rounded to the closest half-float if they    * cannot be represented accurately as a half-float.    *    * @param field field name. must not be {@code null}.    * @param values all values to match    */
DECL|method|newSetQuery
specifier|public
specifier|static
name|Query
name|newSetQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|float
modifier|...
name|values
parameter_list|)
block|{
comment|// Don't unexpectedly change the user's incoming values array:
name|float
index|[]
name|sortedValues
init|=
name|values
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sortedValues
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|encoded
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
name|BYTES
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|PointInSetQuery
argument_list|(
name|field
argument_list|,
literal|1
argument_list|,
name|BYTES
argument_list|,
operator|new
name|PointInSetQuery
operator|.
name|Stream
argument_list|()
block|{
name|int
name|upto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|==
name|sortedValues
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|encodeDimension
argument_list|(
name|sortedValues
index|[
name|upto
index|]
argument_list|,
name|encoded
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
return|return
name|encoded
return|;
block|}
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|.
name|length
operator|==
name|BYTES
assert|;
return|return
name|Float
operator|.
name|toString
argument_list|(
name|decodeDimension
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Create a query matching any of the specified 1D values.  This is the points equivalent of {@code TermsQuery}.    *    * @param field field name. must not be {@code null}.    * @param values all values to match    */
DECL|method|newSetQuery
specifier|public
specifier|static
name|Query
name|newSetQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Collection
argument_list|<
name|Float
argument_list|>
name|values
parameter_list|)
block|{
name|Float
index|[]
name|boxed
init|=
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Float
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|float
index|[]
name|unboxed
init|=
operator|new
name|float
index|[
name|boxed
operator|.
name|length
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
name|boxed
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|unboxed
index|[
name|i
index|]
operator|=
name|boxed
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|newSetQuery
argument_list|(
name|field
argument_list|,
name|unboxed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

