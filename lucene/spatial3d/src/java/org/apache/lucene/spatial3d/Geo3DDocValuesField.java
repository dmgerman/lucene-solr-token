begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
package|;
end_package

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
name|DocValuesType
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
name|FieldInfo
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
name|FieldDoc
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
name|SortField
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|geo
operator|.
name|Polygon
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
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPoint
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoDistanceShape
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoOutsideDistance
import|;
end_import

begin_comment
comment|/**   * An per-document 3D location field.  *<p>  * Sorting by distance is efficient. Multiple values for the same field in one document  * is allowed.   *<p>  * This field defines static factory methods for common operations:  *<ul>  *<li>TBD  *</ul>  *<p>  * If you also need query operations, you should add a separate {@link Geo3DPoint} instance.  *<p>  *<b>WARNING</b>: Values are indexed with some loss of precision from the  * original {@code double} values (4.190951585769653E-8 for the latitude component  * and 8.381903171539307E-8 for longitude).  * @see Geo3DPoint  */
end_comment

begin_class
DECL|class|Geo3DDocValuesField
specifier|public
class|class
name|Geo3DDocValuesField
extends|extends
name|Field
block|{
comment|// These are the multiplicative constants we need to use to arrive at values that fit in 21 bits.
comment|// The formula we use to go from double to encoded value is:  Math.floor((value - minimum) * factor + 0.5)
comment|// If we plug in maximum for value, we should get 0x1FFFFF.
comment|// So, 0x1FFFFF = Math.floor((maximum - minimum) * factor + 0.5)
comment|// We factor out the 0.5 and Math.floor by stating instead:
comment|// 0x1FFFFF = (maximum - minimum) * factor
comment|// So, factor = 0x1FFFFF / (maximum - minimum)
DECL|field|inverseMaximumValue
specifier|private
specifier|final
specifier|static
name|double
name|inverseMaximumValue
init|=
literal|1.0
operator|/
call|(
name|double
call|)
argument_list|(
literal|0x1FFFFF
argument_list|)
decl_stmt|;
DECL|field|inverseXFactor
specifier|private
specifier|final
specifier|static
name|double
name|inverseXFactor
init|=
operator|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumXValue
argument_list|()
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
operator|)
operator|*
name|inverseMaximumValue
decl_stmt|;
DECL|field|inverseYFactor
specifier|private
specifier|final
specifier|static
name|double
name|inverseYFactor
init|=
operator|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumYValue
argument_list|()
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
operator|)
operator|*
name|inverseMaximumValue
decl_stmt|;
DECL|field|inverseZFactor
specifier|private
specifier|final
specifier|static
name|double
name|inverseZFactor
init|=
operator|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumZValue
argument_list|()
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
operator|)
operator|*
name|inverseMaximumValue
decl_stmt|;
DECL|field|xFactor
specifier|private
specifier|final
specifier|static
name|double
name|xFactor
init|=
literal|1.0
operator|/
name|inverseXFactor
decl_stmt|;
DECL|field|yFactor
specifier|private
specifier|final
specifier|static
name|double
name|yFactor
init|=
literal|1.0
operator|/
name|inverseYFactor
decl_stmt|;
DECL|field|zFactor
specifier|private
specifier|final
specifier|static
name|double
name|zFactor
init|=
literal|1.0
operator|/
name|inverseZFactor
decl_stmt|;
comment|// Fudge factor for step adjustments.  This is here solely to handle inaccuracies in bounding boxes
comment|// that occur because of quantization.  For unknown reasons, the fudge factor needs to be
comment|// 10.0 rather than 1.0.  See LUCENE-7430.
DECL|field|STEP_FUDGE
specifier|private
specifier|final
specifier|static
name|double
name|STEP_FUDGE
init|=
literal|10.0
decl_stmt|;
comment|// These values are the delta between a value and the next value in each specific dimension
DECL|field|xStep
specifier|private
specifier|final
specifier|static
name|double
name|xStep
init|=
name|inverseXFactor
operator|*
name|STEP_FUDGE
decl_stmt|;
DECL|field|yStep
specifier|private
specifier|final
specifier|static
name|double
name|yStep
init|=
name|inverseYFactor
operator|*
name|STEP_FUDGE
decl_stmt|;
DECL|field|zStep
specifier|private
specifier|final
specifier|static
name|double
name|zStep
init|=
name|inverseZFactor
operator|*
name|STEP_FUDGE
decl_stmt|;
comment|/**    * Type for a Geo3DDocValuesField    *<p>    * Each value stores a 64-bit long where the three values (x, y, and z) are given    * 21 bits each.  Each 21-bit value represents the maximum extent in that dimension    * for the WGS84 planet model.    */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Creates a new Geo3DDocValuesField with the specified x, y, and z    * @param name field name    * @param point is the point.    * @throws IllegalArgumentException if the field name is null or the point is out of bounds    */
DECL|method|Geo3DDocValuesField
specifier|public
name|Geo3DDocValuesField
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|setLocationValue
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
comment|/**     * Creates a new Geo3DDocValuesField with the specified x, y, and z    * @param name field name    * @param x is the x value for the point.    * @param y is the y value for the point.    * @param z is the z value for the point.    * @throws IllegalArgumentException if the field name is null or x, y, or z are out of bounds    */
DECL|method|Geo3DDocValuesField
specifier|public
name|Geo3DDocValuesField
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|setLocationValue
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change the values of this field    * @param point is the point.    * @throws IllegalArgumentException if the point is out of bounds    */
DECL|method|setLocationValue
specifier|public
name|void
name|setLocationValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|encodePoint
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change the values of this field    * @param x is the x value for the point.    * @param y is the y value for the point.    * @param z is the z value for the point.    * @throws IllegalArgumentException if x, y, or z are out of bounds    */
DECL|method|setLocationValue
specifier|public
name|void
name|setLocationValue
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|encodePoint
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Encode a point.    * @param point is the point    * @return the encoded long    */
DECL|method|encodePoint
specifier|public
specifier|static
name|long
name|encodePoint
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|encodePoint
argument_list|(
name|point
operator|.
name|x
argument_list|,
name|point
operator|.
name|y
argument_list|,
name|point
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Encode a point.    * @param x is the x value    * @param y is the y value    * @param z is the z value    * @return the encoded long    */
DECL|method|encodePoint
specifier|public
specifier|static
name|long
name|encodePoint
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
name|int
name|XEncoded
init|=
name|encodeX
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|int
name|YEncoded
init|=
name|encodeY
argument_list|(
name|y
argument_list|)
decl_stmt|;
name|int
name|ZEncoded
init|=
name|encodeZ
argument_list|(
name|z
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|XEncoded
operator|&
literal|0x1FFFFF
argument_list|)
operator|)
operator|<<
literal|42
operator|)
operator||
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|YEncoded
operator|&
literal|0x1FFFFF
argument_list|)
operator|)
operator|<<
literal|21
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|ZEncoded
operator|&
literal|0x1FFFFF
argument_list|)
operator|)
return|;
block|}
comment|/** Decode GeoPoint value from long docvalues value.    * @param docValue is the doc values value.    * @return the GeoPoint.    */
DECL|method|decodePoint
specifier|public
specifier|static
name|GeoPoint
name|decodePoint
parameter_list|(
specifier|final
name|long
name|docValue
parameter_list|)
block|{
return|return
operator|new
name|GeoPoint
argument_list|(
name|decodeX
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|docValue
operator|>>
literal|42
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
argument_list|,
name|decodeY
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|docValue
operator|>>
literal|21
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
argument_list|,
name|decodeZ
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|docValue
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
argument_list|)
return|;
block|}
comment|/** Decode X value from long docvalues value.    * @param docValue is the doc values value.    * @return the x value.    */
DECL|method|decodeXValue
specifier|public
specifier|static
name|double
name|decodeXValue
parameter_list|(
specifier|final
name|long
name|docValue
parameter_list|)
block|{
return|return
name|decodeX
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|docValue
operator|>>
literal|42
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
return|;
block|}
comment|/** Decode Y value from long docvalues value.    * @param docValue is the doc values value.    * @return the y value.    */
DECL|method|decodeYValue
specifier|public
specifier|static
name|double
name|decodeYValue
parameter_list|(
specifier|final
name|long
name|docValue
parameter_list|)
block|{
return|return
name|decodeY
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|docValue
operator|>>
literal|21
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
return|;
block|}
comment|/** Decode Z value from long docvalues value.    * @param docValue is the doc values value.    * @return the z value.    */
DECL|method|decodeZValue
specifier|public
specifier|static
name|double
name|decodeZValue
parameter_list|(
specifier|final
name|long
name|docValue
parameter_list|)
block|{
return|return
name|decodeZ
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|docValue
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
return|;
block|}
comment|/** Round the provided X value down, by encoding it, decrementing it, and unencoding it.    * @param startValue is the starting value.    * @return the rounded value.    */
DECL|method|roundDownX
specifier|public
specifier|static
name|double
name|roundDownX
parameter_list|(
specifier|final
name|double
name|startValue
parameter_list|)
block|{
return|return
name|startValue
operator|-
name|xStep
return|;
block|}
comment|/** Round the provided X value up, by encoding it, incrementing it, and unencoding it.    * @param startValue is the starting value.    * @return the rounded value.    */
DECL|method|roundUpX
specifier|public
specifier|static
name|double
name|roundUpX
parameter_list|(
specifier|final
name|double
name|startValue
parameter_list|)
block|{
return|return
name|startValue
operator|+
name|xStep
return|;
block|}
comment|/** Round the provided Y value down, by encoding it, decrementing it, and unencoding it.    * @param startValue is the starting value.    * @return the rounded value.    */
DECL|method|roundDownY
specifier|public
specifier|static
name|double
name|roundDownY
parameter_list|(
specifier|final
name|double
name|startValue
parameter_list|)
block|{
return|return
name|startValue
operator|-
name|yStep
return|;
block|}
comment|/** Round the provided Y value up, by encoding it, incrementing it, and unencoding it.    * @param startValue is the starting value.    * @return the rounded value.    */
DECL|method|roundUpY
specifier|public
specifier|static
name|double
name|roundUpY
parameter_list|(
specifier|final
name|double
name|startValue
parameter_list|)
block|{
return|return
name|startValue
operator|+
name|yStep
return|;
block|}
comment|/** Round the provided Z value down, by encoding it, decrementing it, and unencoding it.    * @param startValue is the starting value.    * @return the rounded value.    */
DECL|method|roundDownZ
specifier|public
specifier|static
name|double
name|roundDownZ
parameter_list|(
specifier|final
name|double
name|startValue
parameter_list|)
block|{
return|return
name|startValue
operator|-
name|zStep
return|;
block|}
comment|/** Round the provided Z value up, by encoding it, incrementing it, and unencoding it.    * @param startValue is the starting value.    * @return the rounded value.    */
DECL|method|roundUpZ
specifier|public
specifier|static
name|double
name|roundUpZ
parameter_list|(
specifier|final
name|double
name|startValue
parameter_list|)
block|{
return|return
name|startValue
operator|+
name|zStep
return|;
block|}
comment|// For encoding/decoding, we generally want the following behavior:
comment|// (1) If you encode the maximum value or the minimum value, the resulting int fits in 21 bits.
comment|// (2) If you decode an encoded value, you get back the original value for both the minimum and maximum planet model values.
comment|// (3) Rounding occurs such that a small delta from the minimum and maximum planet model values still returns the same
comment|// values -- that is, these are in the center of the range of input values that should return the minimum or maximum when decoded
DECL|method|encodeX
specifier|private
specifier|static
name|int
name|encodeX
parameter_list|(
specifier|final
name|double
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|>
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumXValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"x value exceeds WGS84 maximum"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|x
operator|<
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"x value less than WGS84 minimum"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|x
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
operator|)
operator|*
name|xFactor
operator|+
literal|0.5
argument_list|)
return|;
block|}
DECL|method|decodeX
specifier|private
specifier|static
name|double
name|decodeX
parameter_list|(
specifier|final
name|int
name|x
parameter_list|)
block|{
return|return
name|x
operator|*
name|inverseXFactor
operator|+
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
return|;
block|}
DECL|method|encodeY
specifier|private
specifier|static
name|int
name|encodeY
parameter_list|(
specifier|final
name|double
name|y
parameter_list|)
block|{
if|if
condition|(
name|y
operator|>
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumYValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"y value exceeds WGS84 maximum"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|y
operator|<
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"y value less than WGS84 minimum"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|y
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
operator|)
operator|*
name|yFactor
operator|+
literal|0.5
argument_list|)
return|;
block|}
DECL|method|decodeY
specifier|private
specifier|static
name|double
name|decodeY
parameter_list|(
specifier|final
name|int
name|y
parameter_list|)
block|{
return|return
name|y
operator|*
name|inverseYFactor
operator|+
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
return|;
block|}
DECL|method|encodeZ
specifier|private
specifier|static
name|int
name|encodeZ
parameter_list|(
specifier|final
name|double
name|z
parameter_list|)
block|{
if|if
condition|(
name|z
operator|>
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumZValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"z value exceeds WGS84 maximum"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|z
operator|<
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"z value less than WGS84 minimum"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|z
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
operator|)
operator|*
name|zFactor
operator|+
literal|0.5
argument_list|)
return|;
block|}
DECL|method|decodeZ
specifier|private
specifier|static
name|double
name|decodeZ
parameter_list|(
specifier|final
name|int
name|z
parameter_list|)
block|{
return|return
name|z
operator|*
name|inverseZFactor
operator|+
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
return|;
block|}
comment|/** helper: checks a fieldinfo and throws exception if its definitely not a Geo3DDocValuesField */
DECL|method|checkCompatible
specifier|static
name|void
name|checkCompatible
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|// dv properties could be "unset", if you e.g. used only StoredField with this same name in the segment.
if|if
condition|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|TYPE
operator|.
name|docValuesType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" was indexed with docValuesType="
operator|+
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|+
literal|" but this type has docValuesType="
operator|+
name|TYPE
operator|.
name|docValuesType
argument_list|()
operator|+
literal|", is the field really a Geo3DDocValuesField?"
argument_list|)
throw|;
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
name|long
name|currentValue
init|=
operator|(
name|Long
operator|)
name|fieldsData
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeXValue
argument_list|(
name|currentValue
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeYValue
argument_list|(
name|currentValue
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeZValue
argument_list|(
name|currentValue
argument_list|)
argument_list|)
expr_stmt|;
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
comment|/**    * Creates a SortField for sorting by distance within a circle.    *<p>    * This sort orders documents by ascending distance from the location. The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance from the circle center is used.    *     * @param field field name. must not be null.    * @param latitude latitude at the center: must be within standard +/-90 coordinate bounds.    * @param longitude longitude at the center: must be within standard +/-180 coordinate bounds.    * @param maxRadiusMeters is the maximum radius in meters.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or circle has invalid coordinates.    */
DECL|method|newDistanceSort
specifier|public
specifier|static
name|SortField
name|newDistanceSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|double
name|maxRadiusMeters
parameter_list|)
block|{
specifier|final
name|GeoDistanceShape
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromDistance
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|maxRadiusMeters
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Creates a SortField for sorting by distance along a path.    *<p>    * This sort orders documents by ascending distance along the described path. The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance along the path is used.    *     * @param field field name. must not be null.    * @param pathLatitudes latitude values for points of the path: must be within standard +/-90 coordinate bounds.    * @param pathLongitudes longitude values for points of the path: must be within standard +/-180 coordinate bounds.    * @param pathWidthMeters width of the path in meters.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or path has invalid coordinates.    */
DECL|method|newPathSort
specifier|public
specifier|static
name|SortField
name|newPathSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
index|[]
name|pathLatitudes
parameter_list|,
specifier|final
name|double
index|[]
name|pathLongitudes
parameter_list|,
specifier|final
name|double
name|pathWidthMeters
parameter_list|)
block|{
specifier|final
name|GeoDistanceShape
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromPath
argument_list|(
name|pathLatitudes
argument_list|,
name|pathLongitudes
argument_list|,
name|pathWidthMeters
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|// Outside distances
comment|/**    * Creates a SortField for sorting by outside distance from a circle.    *<p>    * This sort orders documents by ascending outside distance from the circle.  Points within the circle have distance 0.0.    * The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance to the circle is used.    *     * @param field field name. must not be null.    * @param latitude latitude at the center: must be within standard +/-90 coordinate bounds.    * @param longitude longitude at the center: must be within standard +/-180 coordinate bounds.    * @param maxRadiusMeters is the maximum radius in meters.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or location has invalid coordinates.    */
DECL|method|newOutsideDistanceSort
specifier|public
specifier|static
name|SortField
name|newOutsideDistanceSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|double
name|maxRadiusMeters
parameter_list|)
block|{
specifier|final
name|GeoOutsideDistance
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromDistance
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|maxRadiusMeters
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointOutsideSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Creates a SortField for sorting by outside distance from a box.    *<p>    * This sort orders documents by ascending outside distance from the box.  Points within the box have distance 0.0.    * The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance to the box is used.    *     * @param field field name. must not be null.    * @param minLatitude latitude lower bound: must be within standard +/-90 coordinate bounds.    * @param maxLatitude latitude upper bound: must be within standard +/-90 coordinate bounds.    * @param minLongitude longitude lower bound: must be within standard +/-180 coordinate bounds.    * @param maxLongitude longitude upper bound: must be within standard +/-180 coordinate bounds.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or box has invalid coordinates.    */
DECL|method|newOutsideBoxSort
specifier|public
specifier|static
name|SortField
name|newOutsideBoxSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|minLatitude
parameter_list|,
specifier|final
name|double
name|maxLatitude
parameter_list|,
specifier|final
name|double
name|minLongitude
parameter_list|,
specifier|final
name|double
name|maxLongitude
parameter_list|)
block|{
specifier|final
name|GeoOutsideDistance
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromBox
argument_list|(
name|minLatitude
argument_list|,
name|maxLatitude
argument_list|,
name|minLongitude
argument_list|,
name|maxLongitude
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointOutsideSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Creates a SortField for sorting by outside distance from a polygon.    *<p>    * This sort orders documents by ascending outside distance from the polygon.  Points within the polygon have distance 0.0.    * The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance to the polygon is used.    *     * @param field field name. must not be null.    * @param polygons is the list of polygons to use to construct the query; must be at least one.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or polygon has invalid coordinates.    */
DECL|method|newOutsidePolygonSort
specifier|public
specifier|static
name|SortField
name|newOutsidePolygonSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
specifier|final
name|GeoOutsideDistance
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromPolygon
argument_list|(
name|polygons
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointOutsideSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Creates a SortField for sorting by outside distance from a large polygon.  This differs from the related newOutsideLargePolygonSort in that it    * does little or no legality checking and is optimized for very large numbers of polygon edges.    *<p>    * This sort orders documents by ascending outside distance from the polygon.  Points within the polygon have distance 0.0.    * The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance to the polygon is used.    *     * @param field field name. must not be null.    * @param polygons is the list of polygons to use to construct the query; must be at least one.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or polygon has invalid coordinates.    */
DECL|method|newOutsideLargePolygonSort
specifier|public
specifier|static
name|SortField
name|newOutsideLargePolygonSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
specifier|final
name|GeoOutsideDistance
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromLargePolygon
argument_list|(
name|polygons
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointOutsideSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Creates a SortField for sorting by outside distance from a path.    *<p>    * This sort orders documents by ascending outside distance from the described path. Points within the path    * are given the distance of 0.0.  The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance from the path is used.    *     * @param field field name. must not be null.    * @param pathLatitudes latitude values for points of the path: must be within standard +/-90 coordinate bounds.    * @param pathLongitudes longitude values for points of the path: must be within standard +/-180 coordinate bounds.    * @param pathWidthMeters width of the path in meters.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or path has invalid coordinates.    */
DECL|method|newOutsidePathSort
specifier|public
specifier|static
name|SortField
name|newOutsidePathSort
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
index|[]
name|pathLatitudes
parameter_list|,
specifier|final
name|double
index|[]
name|pathLongitudes
parameter_list|,
specifier|final
name|double
name|pathWidthMeters
parameter_list|)
block|{
specifier|final
name|GeoOutsideDistance
name|shape
init|=
name|Geo3DUtil
operator|.
name|fromPath
argument_list|(
name|pathLatitudes
argument_list|,
name|pathLongitudes
argument_list|,
name|pathWidthMeters
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3DPointOutsideSortField
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
block|}
end_class

end_unit

