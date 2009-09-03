begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.geometry
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geometry
package|;
end_package

begin_comment
comment|/**  * Abstract base lat-lng class which can manipulate fixed point or floating  * point based coordinates. Instances are immutable.  *   * @see FloatLatLng  *  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_class
DECL|class|LatLng
specifier|public
specifier|abstract
class|class
name|LatLng
block|{
DECL|method|isNormalized
specifier|public
specifier|abstract
name|boolean
name|isNormalized
parameter_list|()
function_decl|;
DECL|method|isFixedPoint
specifier|public
specifier|abstract
name|boolean
name|isFixedPoint
parameter_list|()
function_decl|;
DECL|method|normalize
specifier|public
specifier|abstract
name|LatLng
name|normalize
parameter_list|()
function_decl|;
DECL|method|getFixedLat
specifier|public
specifier|abstract
name|int
name|getFixedLat
parameter_list|()
function_decl|;
DECL|method|getFixedLng
specifier|public
specifier|abstract
name|int
name|getFixedLng
parameter_list|()
function_decl|;
DECL|method|getLat
specifier|public
specifier|abstract
name|double
name|getLat
parameter_list|()
function_decl|;
DECL|method|getLng
specifier|public
specifier|abstract
name|double
name|getLng
parameter_list|()
function_decl|;
DECL|method|copy
specifier|public
specifier|abstract
name|LatLng
name|copy
parameter_list|()
function_decl|;
DECL|method|toFixed
specifier|public
specifier|abstract
name|FixedLatLng
name|toFixed
parameter_list|()
function_decl|;
DECL|method|toFloat
specifier|public
specifier|abstract
name|FloatLatLng
name|toFloat
parameter_list|()
function_decl|;
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|LatLng
name|other
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|LatLng
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|equals
argument_list|(
operator|(
name|LatLng
operator|)
name|other
argument_list|)
return|;
block|}
comment|/**    * Convert the lat/lng into the cartesian coordinate plane such that all    * world coordinates are represented in the first quadrant.    * The x dimension corresponds to latitude and y corresponds to longitude.    * The translation starts with the normalized latlng and adds 180 to the latitude and     * 90 to the longitude (subject to fixed point scaling).    */
DECL|method|toCartesian
specifier|public
name|CartesianPoint
name|toCartesian
parameter_list|()
block|{
name|LatLng
name|ll
init|=
name|normalize
argument_list|()
decl_stmt|;
name|int
name|lat
init|=
name|ll
operator|.
name|getFixedLat
argument_list|()
decl_stmt|;
name|int
name|lng
init|=
name|ll
operator|.
name|getFixedLng
argument_list|()
decl_stmt|;
return|return
operator|new
name|CartesianPoint
argument_list|(
name|lng
operator|+
literal|180
operator|*
name|FixedLatLng
operator|.
name|SCALE_FACTOR_INT
argument_list|,
name|lat
operator|+
literal|90
operator|*
name|FixedLatLng
operator|.
name|SCALE_FACTOR_INT
argument_list|)
return|;
block|}
comment|/**    * The inverse of toCartesian().  Always returns a FixedLatLng.    * @param pt    */
DECL|method|fromCartesian
specifier|public
specifier|static
name|LatLng
name|fromCartesian
parameter_list|(
name|CartesianPoint
name|pt
parameter_list|)
block|{
name|int
name|lat
init|=
name|pt
operator|.
name|getY
argument_list|()
operator|-
literal|90
operator|*
name|FixedLatLng
operator|.
name|SCALE_FACTOR_INT
decl_stmt|;
name|int
name|lng
init|=
name|pt
operator|.
name|getX
argument_list|()
operator|-
literal|180
operator|*
name|FixedLatLng
operator|.
name|SCALE_FACTOR_INT
decl_stmt|;
return|return
operator|new
name|FixedLatLng
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|)
return|;
block|}
comment|/**    * Calculates the distance between two lat/lng's in miles.    * Imported from mq java client.    *     * @param ll2    *            Second lat,lng position to calculate distance to.    *     * @return Returns the distance in miles.    */
DECL|method|arcDistance
specifier|public
name|double
name|arcDistance
parameter_list|(
name|LatLng
name|ll2
parameter_list|)
block|{
return|return
name|arcDistance
argument_list|(
name|ll2
argument_list|,
name|DistanceUnits
operator|.
name|MILES
argument_list|)
return|;
block|}
comment|/**    * Calculates the distance between two lat/lng's in miles or meters.    * Imported from mq java client.  Variable references changed to match.    *     * @param ll2    *            Second lat,lng position to calculate distance to.    * @param lUnits    *            Units to calculate distace, defaults to miles    *     * @return Returns the distance in meters or miles.    */
DECL|method|arcDistance
specifier|public
name|double
name|arcDistance
parameter_list|(
name|LatLng
name|ll2
parameter_list|,
name|DistanceUnits
name|lUnits
parameter_list|)
block|{
name|LatLng
name|ll1
init|=
name|normalize
argument_list|()
decl_stmt|;
name|ll2
operator|=
name|ll2
operator|.
name|normalize
argument_list|()
expr_stmt|;
name|double
name|lat1
init|=
name|ll1
operator|.
name|getLat
argument_list|()
decl_stmt|,
name|lng1
init|=
name|ll1
operator|.
name|getLng
argument_list|()
decl_stmt|;
name|double
name|lat2
init|=
name|ll2
operator|.
name|getLat
argument_list|()
decl_stmt|,
name|lng2
init|=
name|ll2
operator|.
name|getLng
argument_list|()
decl_stmt|;
comment|// Check for same position
if|if
condition|(
name|lat1
operator|==
name|lat2
operator|&&
name|lng1
operator|==
name|lng2
condition|)
return|return
literal|0.0
return|;
comment|// Get the m_dLongitude diffeernce. Don't need to worry about
comment|// crossing 180 since cos(x) = cos(-x)
name|double
name|dLon
init|=
name|lng2
operator|-
name|lng1
decl_stmt|;
name|double
name|a
init|=
name|radians
argument_list|(
literal|90.0
operator|-
name|lat1
argument_list|)
decl_stmt|;
name|double
name|c
init|=
name|radians
argument_list|(
literal|90.0
operator|-
name|lat2
argument_list|)
decl_stmt|;
name|double
name|cosB
init|=
operator|(
name|Math
operator|.
name|cos
argument_list|(
name|a
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|c
argument_list|)
operator|)
operator|+
operator|(
name|Math
operator|.
name|sin
argument_list|(
name|a
argument_list|)
operator|*
name|Math
operator|.
name|sin
argument_list|(
name|c
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|radians
argument_list|(
name|dLon
argument_list|)
argument_list|)
operator|)
decl_stmt|;
name|double
name|radius
init|=
operator|(
name|lUnits
operator|==
name|DistanceUnits
operator|.
name|MILES
operator|)
condition|?
literal|3963.205
comment|/* MILERADIUSOFEARTH */
else|:
literal|6378.160187
comment|/* KMRADIUSOFEARTH */
decl_stmt|;
comment|// Find angle subtended (with some bounds checking) in radians and
comment|// multiply by earth radius to find the arc distance
if|if
condition|(
name|cosB
operator|<
operator|-
literal|1.0
condition|)
return|return
literal|3.14159265358979323846
comment|/* PI */
operator|*
name|radius
return|;
elseif|else
if|if
condition|(
name|cosB
operator|>=
literal|1.0
condition|)
return|return
literal|0
return|;
else|else
return|return
name|Math
operator|.
name|acos
argument_list|(
name|cosB
argument_list|)
operator|*
name|radius
return|;
block|}
DECL|method|radians
specifier|private
name|double
name|radians
parameter_list|(
name|double
name|a
parameter_list|)
block|{
return|return
name|a
operator|*
literal|0.01745329251994
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
literal|"["
operator|+
name|getLat
argument_list|()
operator|+
literal|","
operator|+
name|getLng
argument_list|()
operator|+
literal|"]"
return|;
block|}
comment|/**    * Calculate the midpoint between this point an another.  Respects fixed vs floating point    * @param other    */
DECL|method|calculateMidpoint
specifier|public
specifier|abstract
name|LatLng
name|calculateMidpoint
parameter_list|(
name|LatLng
name|other
parameter_list|)
function_decl|;
block|}
end_class

end_unit

