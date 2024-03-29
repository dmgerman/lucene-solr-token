begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package

begin_comment
comment|/**  * Degenerate longitude slice.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|GeoDegenerateLongitudeSlice
class|class
name|GeoDegenerateLongitudeSlice
extends|extends
name|GeoBaseBBox
block|{
comment|/** The longitude of the slice */
DECL|field|longitude
specifier|protected
specifier|final
name|double
name|longitude
decl_stmt|;
comment|/** The bounding plane for the slice (through both poles, perpendicular to the slice) */
DECL|field|boundingPlane
specifier|protected
specifier|final
name|SidedPlane
name|boundingPlane
decl_stmt|;
comment|/** The plane of the slice */
DECL|field|plane
specifier|protected
specifier|final
name|Plane
name|plane
decl_stmt|;
comment|/** A point on the slice */
DECL|field|interiorPoint
specifier|protected
specifier|final
name|GeoPoint
name|interiorPoint
decl_stmt|;
comment|/** An array consisting of the one point chosen on the slice */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/** Notable points for the slice (north and south poles) */
DECL|field|planePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|planePoints
decl_stmt|;
comment|/**    * Accepts only values in the following ranges: lon: {@code -PI -> PI}    */
DECL|method|GeoDegenerateLongitudeSlice
specifier|public
name|GeoDegenerateLongitudeSlice
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
comment|// Argument checking
if|if
condition|(
name|longitude
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|longitude
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of range"
argument_list|)
throw|;
name|this
operator|.
name|longitude
operator|=
name|longitude
expr_stmt|;
specifier|final
name|double
name|sinLongitude
init|=
name|Math
operator|.
name|sin
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosLongitude
init|=
name|Math
operator|.
name|cos
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
name|this
operator|.
name|plane
operator|=
operator|new
name|Plane
argument_list|(
name|cosLongitude
argument_list|,
name|sinLongitude
argument_list|)
expr_stmt|;
comment|// We need a bounding plane too, which is perpendicular to the longitude plane and sided so that the point (0.0, longitude) is inside.
name|this
operator|.
name|interiorPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|0.0
argument_list|,
name|sinLongitude
argument_list|,
literal|1.0
argument_list|,
name|cosLongitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|boundingPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|interiorPoint
argument_list|,
operator|-
name|sinLongitude
argument_list|,
name|cosLongitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|interiorPoint
block|}
expr_stmt|;
name|this
operator|.
name|planePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|planetModel
operator|.
name|NORTH_POLE
block|,
name|planetModel
operator|.
name|SOUTH_POLE
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expand
specifier|public
name|GeoBBox
name|expand
parameter_list|(
specifier|final
name|double
name|angle
parameter_list|)
block|{
comment|// Figuring out when we escalate to a special case requires some prefiguring
name|double
name|newLeftLon
init|=
name|longitude
operator|-
name|angle
decl_stmt|;
name|double
name|newRightLon
init|=
name|longitude
operator|+
name|angle
decl_stmt|;
name|double
name|currentLonSpan
init|=
literal|2.0
operator|*
name|angle
decl_stmt|;
if|if
condition|(
name|currentLonSpan
operator|+
literal|2.0
operator|*
name|angle
operator|>=
name|Math
operator|.
name|PI
operator|*
literal|2.0
condition|)
block|{
name|newLeftLon
operator|=
operator|-
name|Math
operator|.
name|PI
expr_stmt|;
name|newRightLon
operator|=
name|Math
operator|.
name|PI
expr_stmt|;
block|}
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|planetModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
name|newLeftLon
argument_list|,
name|newRightLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
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
return|return
name|plane
operator|.
name|evaluateIsZero
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|boundingPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
block|{
return|return
name|Math
operator|.
name|PI
operator|*
literal|0.5
return|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
return|return
name|interiorPoint
return|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|public
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|edgePoints
return|;
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|p
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|notablePoints
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|plane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
argument_list|,
name|boundingPlane
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
name|bounds
operator|.
name|addVerticalPlane
argument_list|(
name|planetModel
argument_list|,
name|longitude
argument_list|,
name|plane
argument_list|,
name|boundingPlane
argument_list|)
operator|.
name|addPoint
argument_list|(
name|planetModel
operator|.
name|NORTH_POLE
argument_list|)
operator|.
name|addPoint
argument_list|(
name|planetModel
operator|.
name|SOUTH_POLE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRelationship
specifier|public
name|int
name|getRelationship
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
comment|// Look for intersections.
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|plane
argument_list|,
name|planePoints
argument_list|,
name|boundingPlane
argument_list|)
condition|)
return|return
name|OVERLAPS
return|;
if|if
condition|(
name|path
operator|.
name|isWithin
argument_list|(
name|interiorPoint
argument_list|)
condition|)
return|return
name|CONTAINS
return|;
return|return
name|DISJOINT
return|;
block|}
annotation|@
name|Override
DECL|method|outsideDistance
specifier|protected
name|double
name|outsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
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
specifier|final
name|double
name|distance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|plane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|boundingPlane
argument_list|)
decl_stmt|;
specifier|final
name|double
name|northDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
operator|.
name|NORTH_POLE
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
specifier|final
name|double
name|southDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
operator|.
name|SOUTH_POLE
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|distance
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|northDistance
argument_list|,
name|southDistance
argument_list|)
argument_list|)
return|;
block|}
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoDegenerateLongitudeSlice
operator|)
condition|)
return|return
literal|false
return|;
name|GeoDegenerateLongitudeSlice
name|other
init|=
operator|(
name|GeoDegenerateLongitudeSlice
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
name|other
operator|.
name|longitude
operator|==
name|longitude
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|*
literal|31
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
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
literal|"GeoDegenerateLongitudeSlice: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", longitude="
operator|+
name|longitude
operator|+
literal|"("
operator|+
name|longitude
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|")}"
return|;
block|}
block|}
end_class

end_unit

