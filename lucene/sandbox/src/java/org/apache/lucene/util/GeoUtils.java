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
name|ArrayList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|TO_DEGREES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|TO_RADIANS
import|;
end_import

begin_comment
comment|/**  * Basic reusable geo-spatial utility methods  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoUtils
specifier|public
specifier|final
class|class
name|GeoUtils
block|{
DECL|field|BITS
specifier|public
specifier|static
specifier|final
name|short
name|BITS
init|=
literal|31
decl_stmt|;
DECL|field|LON_SCALE
specifier|private
specifier|static
specifier|final
name|double
name|LON_SCALE
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|360.0D
decl_stmt|;
DECL|field|LAT_SCALE
specifier|private
specifier|static
specifier|final
name|double
name|LAT_SCALE
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|180.0D
decl_stmt|;
DECL|field|TOLERANCE
specifier|public
specifier|static
specifier|final
name|double
name|TOLERANCE
init|=
literal|1E
operator|-
literal|6
decl_stmt|;
comment|/** Minimum longitude value. */
DECL|field|MIN_LON_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LON_INCL
init|=
operator|-
literal|180.0D
decl_stmt|;
comment|/** Maximum longitude value. */
DECL|field|MAX_LON_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LON_INCL
init|=
literal|180.0D
decl_stmt|;
comment|/** Minimum latitude value. */
DECL|field|MIN_LAT_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LAT_INCL
init|=
operator|-
literal|90.0D
decl_stmt|;
comment|/** Maximum latitude value. */
DECL|field|MAX_LAT_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LAT_INCL
init|=
literal|90.0D
decl_stmt|;
comment|// No instance:
DECL|method|GeoUtils
specifier|private
name|GeoUtils
parameter_list|()
block|{   }
DECL|method|mortonHash
specifier|public
specifier|static
specifier|final
name|Long
name|mortonHash
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|)
block|{
return|return
name|BitUtil
operator|.
name|interleave
argument_list|(
name|scaleLon
argument_list|(
name|lon
argument_list|)
argument_list|,
name|scaleLat
argument_list|(
name|lat
argument_list|)
argument_list|)
return|;
block|}
DECL|method|mortonUnhashLon
specifier|public
specifier|static
specifier|final
name|double
name|mortonUnhashLon
parameter_list|(
specifier|final
name|long
name|hash
parameter_list|)
block|{
return|return
name|unscaleLon
argument_list|(
name|BitUtil
operator|.
name|deinterleave
argument_list|(
name|hash
argument_list|)
argument_list|)
return|;
block|}
DECL|method|mortonUnhashLat
specifier|public
specifier|static
specifier|final
name|double
name|mortonUnhashLat
parameter_list|(
specifier|final
name|long
name|hash
parameter_list|)
block|{
return|return
name|unscaleLat
argument_list|(
name|BitUtil
operator|.
name|deinterleave
argument_list|(
name|hash
operator|>>>
literal|1
argument_list|)
argument_list|)
return|;
block|}
DECL|method|scaleLon
specifier|private
specifier|static
specifier|final
name|long
name|scaleLon
parameter_list|(
specifier|final
name|double
name|val
parameter_list|)
block|{
return|return
call|(
name|long
call|)
argument_list|(
operator|(
name|val
operator|-
name|MIN_LON_INCL
operator|)
operator|*
name|LON_SCALE
argument_list|)
return|;
block|}
DECL|method|scaleLat
specifier|private
specifier|static
specifier|final
name|long
name|scaleLat
parameter_list|(
specifier|final
name|double
name|val
parameter_list|)
block|{
return|return
call|(
name|long
call|)
argument_list|(
operator|(
name|val
operator|-
name|MIN_LAT_INCL
operator|)
operator|*
name|LAT_SCALE
argument_list|)
return|;
block|}
DECL|method|unscaleLon
specifier|private
specifier|static
specifier|final
name|double
name|unscaleLon
parameter_list|(
specifier|final
name|long
name|val
parameter_list|)
block|{
return|return
operator|(
name|val
operator|/
name|LON_SCALE
operator|)
operator|+
name|MIN_LON_INCL
return|;
block|}
DECL|method|unscaleLat
specifier|private
specifier|static
specifier|final
name|double
name|unscaleLat
parameter_list|(
specifier|final
name|long
name|val
parameter_list|)
block|{
return|return
operator|(
name|val
operator|/
name|LAT_SCALE
operator|)
operator|+
name|MIN_LAT_INCL
return|;
block|}
comment|/**    * Compare two position values within a {@link org.apache.lucene.util.GeoUtils#TOLERANCE} factor    */
DECL|method|compare
specifier|public
specifier|static
name|double
name|compare
parameter_list|(
specifier|final
name|double
name|v1
parameter_list|,
specifier|final
name|double
name|v2
parameter_list|)
block|{
specifier|final
name|double
name|delta
init|=
name|v1
operator|-
name|v2
decl_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|delta
argument_list|)
operator|<=
name|TOLERANCE
condition|?
literal|0
else|:
name|delta
return|;
block|}
comment|/**    * Puts longitude in range of -180 to +180.    */
DECL|method|normalizeLon
specifier|public
specifier|static
name|double
name|normalizeLon
parameter_list|(
name|double
name|lon_deg
parameter_list|)
block|{
if|if
condition|(
name|lon_deg
operator|>=
operator|-
literal|180
operator|&&
name|lon_deg
operator|<=
literal|180
condition|)
block|{
return|return
name|lon_deg
return|;
comment|//common case, and avoids slight double precision shifting
block|}
name|double
name|off
init|=
operator|(
name|lon_deg
operator|+
literal|180
operator|)
operator|%
literal|360
decl_stmt|;
if|if
condition|(
name|off
operator|<
literal|0
condition|)
block|{
return|return
literal|180
operator|+
name|off
return|;
block|}
elseif|else
if|if
condition|(
name|off
operator|==
literal|0
operator|&&
name|lon_deg
operator|>
literal|0
condition|)
block|{
return|return
literal|180
return|;
block|}
else|else
block|{
return|return
operator|-
literal|180
operator|+
name|off
return|;
block|}
block|}
comment|/**    * Puts latitude in range of -90 to 90.    */
DECL|method|normalizeLat
specifier|public
specifier|static
name|double
name|normalizeLat
parameter_list|(
name|double
name|lat_deg
parameter_list|)
block|{
if|if
condition|(
name|lat_deg
operator|>=
operator|-
literal|90
operator|&&
name|lat_deg
operator|<=
literal|90
condition|)
block|{
return|return
name|lat_deg
return|;
comment|//common case, and avoids slight double precision shifting
block|}
name|double
name|off
init|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|lat_deg
operator|+
literal|90
operator|)
operator|%
literal|360
argument_list|)
decl_stmt|;
return|return
operator|(
name|off
operator|<=
literal|180
condition|?
name|off
else|:
literal|360
operator|-
name|off
operator|)
operator|-
literal|90
return|;
block|}
DECL|method|geoTermToString
specifier|public
specifier|static
name|String
name|geoTermToString
parameter_list|(
name|long
name|term
parameter_list|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|(
literal|64
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numberOfLeadingZeros
init|=
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|term
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
name|numberOfLeadingZeros
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|term
operator|!=
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toBinaryString
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Converts a given circle (defined as a point/radius) to an approximated line-segment polygon    *    * @param lon longitudinal center of circle (in degrees)    * @param lat latitudinal center of circle (in degrees)    * @param radiusMeters distance radius of circle (in meters)    * @return a list of lon/lat points representing the circle    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|circleToPoly
specifier|public
specifier|static
name|ArrayList
argument_list|<
name|double
index|[]
argument_list|>
name|circleToPoly
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|radiusMeters
parameter_list|)
block|{
name|double
name|angle
decl_stmt|;
comment|// a little under-sampling (to limit the number of polygonal points): using archimedes estimation of pi
specifier|final
name|int
name|sides
init|=
literal|25
decl_stmt|;
name|ArrayList
argument_list|<
name|double
index|[]
argument_list|>
name|geometry
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|double
index|[]
name|lons
init|=
operator|new
name|double
index|[
name|sides
index|]
decl_stmt|;
name|double
index|[]
name|lats
init|=
operator|new
name|double
index|[
name|sides
index|]
decl_stmt|;
name|double
index|[]
name|pt
init|=
operator|new
name|double
index|[
literal|2
index|]
decl_stmt|;
specifier|final
name|int
name|sidesLen
init|=
name|sides
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
name|sidesLen
condition|;
operator|++
name|i
control|)
block|{
name|angle
operator|=
operator|(
name|i
operator|*
literal|360
operator|/
name|sides
operator|)
expr_stmt|;
name|pt
operator|=
name|GeoProjectionUtils
operator|.
name|pointFromLonLatBearingGreatCircle
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
name|angle
argument_list|,
name|radiusMeters
argument_list|,
name|pt
argument_list|)
expr_stmt|;
name|lons
index|[
name|i
index|]
operator|=
name|pt
index|[
literal|0
index|]
expr_stmt|;
name|lats
index|[
name|i
index|]
operator|=
name|pt
index|[
literal|1
index|]
expr_stmt|;
block|}
comment|// close the poly
name|lons
index|[
name|sidesLen
index|]
operator|=
name|lons
index|[
literal|0
index|]
expr_stmt|;
name|lats
index|[
name|sidesLen
index|]
operator|=
name|lats
index|[
literal|0
index|]
expr_stmt|;
name|geometry
operator|.
name|add
argument_list|(
name|lons
argument_list|)
expr_stmt|;
name|geometry
operator|.
name|add
argument_list|(
name|lats
argument_list|)
expr_stmt|;
return|return
name|geometry
return|;
block|}
comment|/**    * Compute Bounding Box for a circle using WGS-84 parameters    */
DECL|method|circleToBBox
specifier|public
specifier|static
name|GeoRect
name|circleToBBox
parameter_list|(
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|radiusMeters
parameter_list|)
block|{
specifier|final
name|double
name|radLat
init|=
name|TO_RADIANS
operator|*
name|centerLat
decl_stmt|;
specifier|final
name|double
name|radLon
init|=
name|TO_RADIANS
operator|*
name|centerLon
decl_stmt|;
name|double
name|radDistance
init|=
name|radiusMeters
operator|/
name|GeoProjectionUtils
operator|.
name|SEMIMAJOR_AXIS
decl_stmt|;
name|double
name|minLat
init|=
name|radLat
operator|-
name|radDistance
decl_stmt|;
name|double
name|maxLat
init|=
name|radLat
operator|+
name|radDistance
decl_stmt|;
name|double
name|minLon
decl_stmt|;
name|double
name|maxLon
decl_stmt|;
if|if
condition|(
name|minLat
operator|>
name|GeoProjectionUtils
operator|.
name|MIN_LAT_RADIANS
operator|&&
name|maxLat
operator|<
name|GeoProjectionUtils
operator|.
name|MAX_LAT_RADIANS
condition|)
block|{
name|double
name|deltaLon
init|=
name|SloppyMath
operator|.
name|asin
argument_list|(
name|SloppyMath
operator|.
name|sin
argument_list|(
name|radDistance
argument_list|)
operator|/
name|SloppyMath
operator|.
name|cos
argument_list|(
name|radLat
argument_list|)
argument_list|)
decl_stmt|;
name|minLon
operator|=
name|radLon
operator|-
name|deltaLon
expr_stmt|;
if|if
condition|(
name|minLon
operator|<
name|GeoProjectionUtils
operator|.
name|MIN_LON_RADIANS
condition|)
block|{
name|minLon
operator|+=
literal|2d
operator|*
name|StrictMath
operator|.
name|PI
expr_stmt|;
block|}
name|maxLon
operator|=
name|radLon
operator|+
name|deltaLon
expr_stmt|;
if|if
condition|(
name|maxLon
operator|>
name|GeoProjectionUtils
operator|.
name|MAX_LON_RADIANS
condition|)
block|{
name|maxLon
operator|-=
literal|2d
operator|*
name|StrictMath
operator|.
name|PI
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// a pole is within the distance
name|minLat
operator|=
name|StrictMath
operator|.
name|max
argument_list|(
name|minLat
argument_list|,
name|GeoProjectionUtils
operator|.
name|MIN_LAT_RADIANS
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|StrictMath
operator|.
name|min
argument_list|(
name|maxLat
argument_list|,
name|GeoProjectionUtils
operator|.
name|MAX_LAT_RADIANS
argument_list|)
expr_stmt|;
name|minLon
operator|=
name|GeoProjectionUtils
operator|.
name|MIN_LON_RADIANS
expr_stmt|;
name|maxLon
operator|=
name|GeoProjectionUtils
operator|.
name|MAX_LON_RADIANS
expr_stmt|;
block|}
return|return
operator|new
name|GeoRect
argument_list|(
name|TO_DEGREES
operator|*
name|minLon
argument_list|,
name|TO_DEGREES
operator|*
name|maxLon
argument_list|,
name|TO_DEGREES
operator|*
name|minLat
argument_list|,
name|TO_DEGREES
operator|*
name|maxLat
argument_list|)
return|;
block|}
comment|/**    * Compute Bounding Box for a polygon using WGS-84 parameters    */
DECL|method|polyToBBox
specifier|public
specifier|static
name|GeoRect
name|polyToBBox
parameter_list|(
name|double
index|[]
name|polyLons
parameter_list|,
name|double
index|[]
name|polyLats
parameter_list|)
block|{
if|if
condition|(
name|polyLons
operator|.
name|length
operator|!=
name|polyLats
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLons and polyLats must be equal length"
argument_list|)
throw|;
block|}
name|double
name|minLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|minLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid polyLons["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|polyLons
index|[
name|i
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid polyLats["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|polyLats
index|[
name|i
index|]
argument_list|)
throw|;
block|}
name|minLon
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
name|minLat
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|minLat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GeoRect
argument_list|(
name|GeoUtils
operator|.
name|unscaleLon
argument_list|(
name|GeoUtils
operator|.
name|scaleLon
argument_list|(
name|minLon
argument_list|)
argument_list|)
argument_list|,
name|GeoUtils
operator|.
name|unscaleLon
argument_list|(
name|GeoUtils
operator|.
name|scaleLon
argument_list|(
name|maxLon
argument_list|)
argument_list|)
argument_list|,
name|GeoUtils
operator|.
name|unscaleLat
argument_list|(
name|GeoUtils
operator|.
name|scaleLat
argument_list|(
name|minLat
argument_list|)
argument_list|)
argument_list|,
name|GeoUtils
operator|.
name|unscaleLat
argument_list|(
name|GeoUtils
operator|.
name|scaleLat
argument_list|(
name|maxLat
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * validates latitude value is within standard +/-90 coordinate bounds    */
DECL|method|isValidLat
specifier|public
specifier|static
name|boolean
name|isValidLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
return|return
name|Double
operator|.
name|isNaN
argument_list|(
name|lat
argument_list|)
operator|==
literal|false
operator|&&
name|lat
operator|>=
name|MIN_LAT_INCL
operator|&&
name|lat
operator|<=
name|MAX_LAT_INCL
return|;
block|}
comment|/**    * validates longitude value is within standard +/-180 coordinate bounds    */
DECL|method|isValidLon
specifier|public
specifier|static
name|boolean
name|isValidLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
return|return
name|Double
operator|.
name|isNaN
argument_list|(
name|lon
argument_list|)
operator|==
literal|false
operator|&&
name|lon
operator|>=
name|MIN_LON_INCL
operator|&&
name|lon
operator|<=
name|MAX_LON_INCL
return|;
block|}
block|}
end_class

end_unit

