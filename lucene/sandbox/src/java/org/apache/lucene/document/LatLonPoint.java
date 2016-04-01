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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoUtils
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
name|NumericUtils
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
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|geo
operator|.
name|Polygon
import|;
end_import

begin_comment
comment|/**   * An indexed location field.  *<p>  * Finding all documents within a range at search time is  * efficient.  Multiple values for the same field in one document  * is allowed.   *<p>  * This field defines static factory methods for common operations:  *<ul>  *<li>{@link #newBoxQuery newBoxQuery()} for matching points within a bounding box.  *<li>{@link #newDistanceQuery newDistanceQuery()} for matching points within a specified distance.  *<li>{@link #newDistanceSort newDistanceSort()} for ordering documents by distance from a specified location.   *<li>{@link #newPolygonQuery newPolygonQuery()} for matching points within an arbitrary polygon.  *</ul>  *<p>  *<b>WARNING</b>: Values are indexed with some loss of precision, incurring up to 1E-7 error from the  * original {@code double} values.   * @see PointValues  */
end_comment

begin_comment
comment|// TODO ^^^ that is very sandy and hurts the API, usage, and tests tremendously, because what the user passes
end_comment

begin_comment
comment|// to the field is not actually what gets indexed. Float would be 1E-5 error vs 1E-7, but it might be
end_comment

begin_comment
comment|// a better tradeoff? then it would be completely transparent to the user and lucene would be "lossless".
end_comment

begin_class
DECL|class|LatLonPoint
specifier|public
class|class
name|LatLonPoint
extends|extends
name|Field
block|{
DECL|field|currentValue
specifier|private
name|long
name|currentValue
decl_stmt|;
comment|/**    * Type for an indexed LatLonPoint    *<p>    * Each point stores two dimensions with 4 bytes per dimension.    */
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
name|setDimensions
argument_list|(
literal|2
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
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
comment|/**    * Change the values of this field    * @param latitude latitude value: must be within standard +/-90 coordinate bounds.    * @param longitude longitude value: must be within standard +/-180 coordinate bounds.    * @throws IllegalArgumentException if latitude or longitude are out of bounds    */
DECL|method|setLocationValue
specifier|public
name|void
name|setLocationValue
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|int
name|latitudeEncoded
init|=
name|encodeLatitude
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
name|int
name|longitudeEncoded
init|=
name|encodeLongitude
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|latitudeEncoded
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|longitudeEncoded
argument_list|,
name|bytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|currentValue
operator|=
operator|(
operator|(
operator|(
name|long
operator|)
name|latitudeEncoded
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|longitudeEncoded
operator|&
literal|0xFFFFFFFFL
operator|)
expr_stmt|;
block|}
comment|/**     * Creates a new LatLonPoint with the specified latitude and longitude    * @param name field name    * @param latitude latitude value: must be within standard +/-90 coordinate bounds.    * @param longitude longitude value: must be within standard +/-180 coordinate bounds.    * @throws IllegalArgumentException if the field name is null or latitude or longitude are out of bounds    */
DECL|method|LatLonPoint
specifier|public
name|LatLonPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
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
name|latitude
argument_list|,
name|longitude
argument_list|)
expr_stmt|;
block|}
DECL|field|BITS
specifier|private
specifier|static
specifier|final
name|int
name|BITS
init|=
literal|32
decl_stmt|;
DECL|field|LONGITUDE_MUL
specifier|private
specifier|static
specifier|final
name|double
name|LONGITUDE_MUL
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|360.0D
decl_stmt|;
DECL|field|LONGITUDE_DECODE
specifier|private
specifier|static
specifier|final
name|double
name|LONGITUDE_DECODE
init|=
literal|1
operator|/
name|LONGITUDE_MUL
decl_stmt|;
DECL|field|LATITUDE_MUL
specifier|private
specifier|static
specifier|final
name|double
name|LATITUDE_MUL
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|180.0D
decl_stmt|;
DECL|field|LATITUDE_DECODE
specifier|private
specifier|static
specifier|final
name|double
name|LATITUDE_DECODE
init|=
literal|1
operator|/
name|LATITUDE_MUL
decl_stmt|;
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
name|result
operator|.
name|append
argument_list|(
name|decodeLatitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|currentValue
operator|>>
literal|32
argument_list|)
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
name|decodeLongitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|currentValue
operator|&
literal|0xFFFFFFFF
argument_list|)
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
comment|/**    * Returns a 64-bit long, where the upper 32 bits are the encoded latitude,    * and the lower 32 bits are the encoded longitude.    * @see #decodeLatitude(int)    * @see #decodeLongitude(int)    */
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
return|return
name|currentValue
return|;
block|}
comment|// public helper methods (e.g. for queries)
comment|/**     * Quantizes double (64 bit) latitude into 32 bits (rounding down: in the direction of -90)    * @param latitude latitude value: must be within standard +/-90 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if latitude is out of bounds    */
DECL|method|encodeLatitude
specifier|public
specifier|static
name|int
name|encodeLatitude
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|latitude
operator|==
literal|90.0D
condition|)
block|{
name|latitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|latitude
operator|/
name|LATITUDE_DECODE
argument_list|)
return|;
block|}
comment|/**     * Quantizes double (64 bit) latitude into 32 bits (rounding up: in the direction of +90)    * @param latitude latitude value: must be within standard +/-90 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if latitude is out of bounds    */
DECL|method|encodeLatitudeCeil
specifier|public
specifier|static
name|int
name|encodeLatitudeCeil
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|latitude
operator|==
literal|90.0D
condition|)
block|{
name|latitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|latitude
operator|/
name|LATITUDE_DECODE
argument_list|)
return|;
block|}
comment|/**     * Quantizes double (64 bit) longitude into 32 bits (rounding down: in the direction of -180)    * @param longitude longitude value: must be within standard +/-180 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if longitude is out of bounds    */
DECL|method|encodeLongitude
specifier|public
specifier|static
name|int
name|encodeLongitude
parameter_list|(
name|double
name|longitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|longitude
operator|==
literal|180.0D
condition|)
block|{
name|longitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|longitude
operator|/
name|LONGITUDE_DECODE
argument_list|)
return|;
block|}
comment|/**     * Quantizes double (64 bit) longitude into 32 bits (rounding up: in the direction of +180)    * @param longitude longitude value: must be within standard +/-180 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if longitude is out of bounds    */
DECL|method|encodeLongitudeCeil
specifier|public
specifier|static
name|int
name|encodeLongitudeCeil
parameter_list|(
name|double
name|longitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|longitude
operator|==
literal|180.0D
condition|)
block|{
name|longitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|longitude
operator|/
name|LONGITUDE_DECODE
argument_list|)
return|;
block|}
comment|/**     * Turns quantized value from {@link #encodeLatitude} back into a double.     * @param encoded encoded value: 32-bit quantized value.    * @return decoded latitude value.    */
DECL|method|decodeLatitude
specifier|public
specifier|static
name|double
name|decodeLatitude
parameter_list|(
name|int
name|encoded
parameter_list|)
block|{
name|double
name|result
init|=
name|encoded
operator|*
name|LATITUDE_DECODE
decl_stmt|;
assert|assert
name|result
operator|>=
name|GeoUtils
operator|.
name|MIN_LAT_INCL
operator|&&
name|result
operator|<=
name|GeoUtils
operator|.
name|MAX_LAT_INCL
assert|;
return|return
name|result
return|;
block|}
comment|/**     * Turns quantized value from byte array back into a double.     * @param src byte array containing 4 bytes to decode at {@code offset}    * @param offset offset into {@code src} to decode from.    * @return decoded latitude value.    */
DECL|method|decodeLatitude
specifier|public
specifier|static
name|double
name|decodeLatitude
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|decodeLatitude
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|src
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Turns quantized value from {@link #encodeLongitude} back into a double.     * @param encoded encoded value: 32-bit quantized value.    * @return decoded longitude value.    */
DECL|method|decodeLongitude
specifier|public
specifier|static
name|double
name|decodeLongitude
parameter_list|(
name|int
name|encoded
parameter_list|)
block|{
name|double
name|result
init|=
name|encoded
operator|*
name|LONGITUDE_DECODE
decl_stmt|;
assert|assert
name|result
operator|>=
name|GeoUtils
operator|.
name|MIN_LON_INCL
operator|&&
name|result
operator|<=
name|GeoUtils
operator|.
name|MAX_LON_INCL
assert|;
return|return
name|result
return|;
block|}
comment|/**     * Turns quantized value from byte array back into a double.     * @param src byte array containing 4 bytes to decode at {@code offset}    * @param offset offset into {@code src} to decode from.    * @return decoded longitude value.    */
DECL|method|decodeLongitude
specifier|public
specifier|static
name|double
name|decodeLongitude
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|decodeLongitude
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|src
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/** sugar encodes a single point as a byte array */
DECL|method|encode
specifier|private
specifier|static
name|byte
index|[]
name|encode
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLatitude
argument_list|(
name|latitude
argument_list|)
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLongitude
argument_list|(
name|longitude
argument_list|)
argument_list|,
name|bytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/** sugar encodes a single point as a byte array, rounding values up */
DECL|method|encodeCeil
specifier|private
specifier|static
name|byte
index|[]
name|encodeCeil
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLatitudeCeil
argument_list|(
name|latitude
argument_list|)
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|encodeLongitudeCeil
argument_list|(
name|longitude
argument_list|)
argument_list|,
name|bytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/** helper: checks a fieldinfo and throws exception if its definitely not a LatLonPoint */
DECL|method|checkCompatible
specifier|static
name|void
name|checkCompatible
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|// point/dv properties could be "unset", if you e.g. used only StoredField with this same name in the segment.
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|!=
literal|0
operator|&&
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|!=
name|TYPE
operator|.
name|pointDimensionCount
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
literal|"\" was indexed with numDims="
operator|+
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|+
literal|" but this point type has numDims="
operator|+
name|TYPE
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|", is the field really a LatLonPoint?"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|!=
literal|0
operator|&&
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|!=
name|TYPE
operator|.
name|pointNumBytes
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
literal|"\" was indexed with bytesPerDim="
operator|+
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|+
literal|" but this point type has bytesPerDim="
operator|+
name|TYPE
operator|.
name|pointNumBytes
argument_list|()
operator|+
literal|", is the field really a LatLonPoint?"
argument_list|)
throw|;
block|}
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
literal|" but this point type has docValuesType="
operator|+
name|TYPE
operator|.
name|docValuesType
argument_list|()
operator|+
literal|", is the field really a LatLonPoint?"
argument_list|)
throw|;
block|}
block|}
comment|// static methods for generating queries
comment|/**    * Create a query for matching a bounding box.    *<p>    * The box may cross over the dateline.    * @param field field name. must not be null.    * @param minLatitude latitude lower bound: must be within standard +/-90 coordinate bounds.    * @param maxLatitude latitude upper bound: must be within standard +/-90 coordinate bounds.    * @param minLongitude longitude lower bound: must be within standard +/-180 coordinate bounds.    * @param maxLongitude longitude upper bound: must be within standard +/-180 coordinate bounds.    * @return query matching points within this box    * @throws IllegalArgumentException if {@code field} is null, or the box has invalid coordinates.    */
DECL|method|newBoxQuery
specifier|public
specifier|static
name|Query
name|newBoxQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|minLatitude
parameter_list|,
name|double
name|maxLatitude
parameter_list|,
name|double
name|minLongitude
parameter_list|,
name|double
name|maxLongitude
parameter_list|)
block|{
name|byte
index|[]
name|lower
init|=
name|encodeCeil
argument_list|(
name|minLatitude
argument_list|,
name|minLongitude
argument_list|)
decl_stmt|;
name|byte
index|[]
name|upper
init|=
name|encode
argument_list|(
name|maxLatitude
argument_list|,
name|maxLongitude
argument_list|)
decl_stmt|;
comment|// Crosses date line: we just rewrite into OR of two bboxes, with longitude as an open range:
if|if
condition|(
name|maxLongitude
operator|<
name|minLongitude
condition|)
block|{
comment|// Disable coord here because a multi-valued doc could match both rects and get unfairly boosted:
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// E.g.: maxLon = -179, minLon = 179
name|byte
index|[]
name|leftOpen
init|=
name|lower
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// leave longitude open
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|leftOpen
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|Query
name|left
init|=
name|newBoxInternal
argument_list|(
name|field
argument_list|,
name|leftOpen
argument_list|,
name|upper
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|left
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|rightOpen
init|=
name|upper
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// leave longitude open
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|rightOpen
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|Query
name|right
init|=
name|newBoxInternal
argument_list|(
name|field
argument_list|,
name|lower
argument_list|,
name|rightOpen
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|right
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|newBoxInternal
argument_list|(
name|field
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
return|;
block|}
block|}
DECL|method|newBoxInternal
specifier|private
specifier|static
name|Query
name|newBoxInternal
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|min
parameter_list|,
name|byte
index|[]
name|max
parameter_list|)
block|{
return|return
operator|new
name|PointRangeQuery
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|2
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
if|if
condition|(
name|dimension
operator|==
literal|0
condition|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|decodeLatitude
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dimension
operator|==
literal|1
condition|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|decodeLongitude
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Create a query for matching points within the specified distance of the supplied location.    * @param field field name. must not be null.    * @param latitude latitude at the center: must be within standard +/-90 coordinate bounds.    * @param longitude longitude at the center: must be within standard +/-180 coordinate bounds.    * @param radiusMeters maximum distance from the center in meters: must be non-negative and finite.    * @return query matching points within this distance    * @throws IllegalArgumentException if {@code field} is null, location has invalid coordinates, or radius is invalid.    */
DECL|method|newDistanceQuery
specifier|public
specifier|static
name|Query
name|newDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
return|return
operator|new
name|LatLonPointDistanceQuery
argument_list|(
name|field
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radiusMeters
argument_list|)
return|;
block|}
comment|/**     * Create a query for matching a polygon.    *<p>    * The supplied {@code polygon} must be clockwise or counter-clockwise.    * @param field field name. must not be null.    * @param polygons array of polygons. must not be null or empty    * @return query matching points within this polygon    * @throws IllegalArgumentException if {@code field} is null, {@code polygons} is null or empty    */
DECL|method|newPolygonQuery
specifier|public
specifier|static
name|Query
name|newPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
return|return
operator|new
name|LatLonPointInPolygonQuery
argument_list|(
name|field
argument_list|,
name|polygons
argument_list|)
return|;
block|}
comment|/**    * Creates a SortField for sorting by distance from a location.    *<p>    * This sort orders documents by ascending distance from the location. The value returned in {@link FieldDoc} for    * the hits contains a Double instance with the distance in meters.    *<p>    * If a document is missing the field, then by default it is treated as having {@link Double#POSITIVE_INFINITY} distance    * (missing values sort last).    *<p>    * If a document contains multiple values for the field, the<i>closest</i> distance to the location is used.    *     * @param field field name. must not be null.    * @param latitude latitude at the center: must be within standard +/-90 coordinate bounds.    * @param longitude longitude at the center: must be within standard +/-180 coordinate bounds.    * @return SortField ordering documents by distance    * @throws IllegalArgumentException if {@code field} is null or location has invalid coordinates.    */
DECL|method|newDistanceSort
specifier|public
specifier|static
name|SortField
name|newDistanceSort
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
return|return
operator|new
name|LatLonPointSortField
argument_list|(
name|field
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|)
return|;
block|}
block|}
end_class

end_unit

