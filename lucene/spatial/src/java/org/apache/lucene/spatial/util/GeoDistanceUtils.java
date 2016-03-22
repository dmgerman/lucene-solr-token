begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
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
name|util
operator|.
name|SloppyMath
import|;
end_import

begin_comment
comment|/**  * Reusable geo-spatial distance utility methods.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoDistanceUtils
specifier|public
class|class
name|GeoDistanceUtils
block|{
comment|// No instance:
DECL|method|GeoDistanceUtils
specifier|private
name|GeoDistanceUtils
parameter_list|()
block|{   }
comment|/**    * Compute the inverse haversine to determine distance in degrees longitude for provided distance in meters    * @param lat latitude to compute delta degrees lon    * @param distance distance in meters to convert to degrees lon    * @return Sloppy distance in degrees longitude for provided distance in meters    */
DECL|method|distanceToDegreesLon
specifier|public
specifier|static
name|double
name|distanceToDegreesLon
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|distance
parameter_list|)
block|{
comment|// convert latitude to radians
name|lat
operator|=
name|StrictMath
operator|.
name|toRadians
argument_list|(
name|lat
argument_list|)
expr_stmt|;
comment|// get the diameter at the latitude
specifier|final
name|double
name|diameter
init|=
literal|2
operator|*
name|GeoUtils
operator|.
name|SEMIMAJOR_AXIS
decl_stmt|;
comment|// compute inverse haversine
name|double
name|a
init|=
name|StrictMath
operator|.
name|sin
argument_list|(
name|distance
operator|/
name|diameter
argument_list|)
decl_stmt|;
name|double
name|h
init|=
name|StrictMath
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|h
operator|*=
name|h
expr_stmt|;
name|double
name|cLat
init|=
name|StrictMath
operator|.
name|cos
argument_list|(
name|lat
argument_list|)
decl_stmt|;
return|return
name|StrictMath
operator|.
name|toDegrees
argument_list|(
name|StrictMath
operator|.
name|acos
argument_list|(
literal|1
operator|-
operator|(
operator|(
literal|2d
operator|*
name|h
operator|)
operator|/
operator|(
name|cLat
operator|*
name|cLat
operator|)
operator|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns the maximum distance/radius (in meters) from the point 'center' before overlapping */
DECL|method|maxRadialDistanceMeters
specifier|public
specifier|static
name|double
name|maxRadialDistanceMeters
parameter_list|(
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|centerLat
argument_list|)
operator|==
name|GeoUtils
operator|.
name|MAX_LAT_INCL
condition|)
block|{
return|return
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
literal|0
argument_list|,
name|centerLon
argument_list|)
return|;
block|}
return|return
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
operator|(
name|GeoUtils
operator|.
name|MAX_LON_INCL
operator|+
name|centerLon
operator|)
operator|%
literal|360
argument_list|)
return|;
block|}
block|}
end_class

end_unit

