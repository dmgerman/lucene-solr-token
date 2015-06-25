begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Circular area with a center and radius.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoCircle
specifier|public
class|class
name|GeoCircle
extends|extends
name|GeoBaseExtendedShape
implements|implements
name|GeoDistanceShape
implements|,
name|GeoSizeable
block|{
DECL|field|center
specifier|public
specifier|final
name|GeoPoint
name|center
decl_stmt|;
DECL|field|cutoffAngle
specifier|public
specifier|final
name|double
name|cutoffAngle
decl_stmt|;
DECL|field|circlePlane
specifier|public
specifier|final
name|SidedPlane
name|circlePlane
decl_stmt|;
DECL|field|edgePoints
specifier|public
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
DECL|field|circlePoints
specifier|public
specifier|static
specifier|final
name|GeoPoint
index|[]
name|circlePoints
init|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
decl_stmt|;
DECL|method|GeoCircle
specifier|public
name|GeoCircle
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|cutoffAngle
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
if|if
condition|(
name|lat
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|lat
argument_list|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Latitude out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|lon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|lon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|cutoffAngle
operator|<=
literal|0.0
operator|||
name|cutoffAngle
operator|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cutoff angle out of bounds"
argument_list|)
throw|;
name|this
operator|.
name|center
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
comment|// In an ellipsoidal world, cutoff distances make no sense, unfortunately.  Only membership
comment|// can be used to make in/out determination.
name|this
operator|.
name|cutoffAngle
operator|=
name|cutoffAngle
expr_stmt|;
comment|// Compute two points on the circle, with the right angle from the center.  We'll use these
comment|// to obtain the perpendicular plane to the circle.
name|double
name|upperLat
init|=
name|lat
operator|+
name|cutoffAngle
decl_stmt|;
name|double
name|upperLon
init|=
name|lon
decl_stmt|;
if|if
condition|(
name|upperLat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
block|{
name|upperLon
operator|+=
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|upperLon
operator|>
name|Math
operator|.
name|PI
condition|)
name|upperLon
operator|-=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
name|upperLat
operator|=
name|Math
operator|.
name|PI
operator|-
name|upperLat
expr_stmt|;
block|}
name|double
name|lowerLat
init|=
name|lat
operator|-
name|cutoffAngle
decl_stmt|;
name|double
name|lowerLon
init|=
name|lon
decl_stmt|;
if|if
condition|(
name|lowerLat
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
block|{
name|lowerLon
operator|+=
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|lowerLon
operator|>
name|Math
operator|.
name|PI
condition|)
name|lowerLon
operator|-=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
name|lowerLat
operator|=
operator|-
name|Math
operator|.
name|PI
operator|-
name|lowerLat
expr_stmt|;
block|}
specifier|final
name|GeoPoint
name|upperPoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|upperLat
argument_list|,
name|upperLon
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|lowerPoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|lowerLat
argument_list|,
name|lowerLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|cutoffAngle
operator|-
name|Math
operator|.
name|PI
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
comment|// Circle is the whole world
name|this
operator|.
name|circlePlane
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// Construct normal plane
specifier|final
name|Plane
name|normalPlane
init|=
operator|new
name|Plane
argument_list|(
name|upperPoint
argument_list|,
name|center
argument_list|)
decl_stmt|;
comment|// Construct a sided plane that goes through the two points and whose normal is in the normalPlane.
name|this
operator|.
name|circlePlane
operator|=
name|SidedPlane
operator|.
name|constructNormalizedPerpendicularSidedPlane
argument_list|(
name|center
argument_list|,
name|normalPlane
argument_list|,
name|upperPoint
argument_list|,
name|lowerPoint
argument_list|)
expr_stmt|;
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Couldn't construct circle plane.  Cutoff angle = "
operator|+
name|cutoffAngle
operator|+
literal|"; upperPoint = "
operator|+
name|upperPoint
operator|+
literal|"; lowerPoint = "
operator|+
name|lowerPoint
argument_list|)
throw|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|upperPoint
block|}
expr_stmt|;
block|}
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
name|cutoffAngle
return|;
block|}
comment|/**    * Returns the center of a circle into which the area will be inscribed.    *    * @return the center.    */
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
return|return
name|center
return|;
block|}
comment|/**    * Compute an estimate of "distance" to the GeoPoint.    * A return value of Double.MAX_VALUE should be returned for    * points outside of the shape.    */
annotation|@
name|Override
DECL|method|computeNormalDistance
specifier|public
name|double
name|computeNormalDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|normalDistance
argument_list|(
name|point
argument_list|)
return|;
block|}
comment|/**    * Compute an estimate of "distance" to the GeoPoint.    * A return value of Double.MAX_VALUE should be returned for    * points outside of the shape.    */
annotation|@
name|Override
DECL|method|computeNormalDistance
specifier|public
name|double
name|computeNormalDistance
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
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|normalDistance
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/**    * Compute a squared estimate of the "distance" to the    * GeoPoint.  Double.MAX_VALUE indicates a point outside of the    * shape.    */
annotation|@
name|Override
DECL|method|computeSquaredNormalDistance
specifier|public
name|double
name|computeSquaredNormalDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|normalDistanceSquared
argument_list|(
name|point
argument_list|)
return|;
block|}
comment|/**    * Compute a squared estimate of the "distance" to the    * GeoPoint.  Double.MAX_VALUE indicates a point outside of the    * shape.    */
annotation|@
name|Override
DECL|method|computeSquaredNormalDistance
specifier|public
name|double
name|computeSquaredNormalDistance
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
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|normalDistanceSquared
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/**    * Compute a linear distance to the vector.    * return Double.MAX_VALUE for points outside the shape.    */
annotation|@
name|Override
DECL|method|computeLinearDistance
specifier|public
name|double
name|computeLinearDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|linearDistance
argument_list|(
name|point
argument_list|)
return|;
block|}
comment|/**    * Compute a linear distance to the vector.    * return Double.MAX_VALUE for points outside the shape.    */
annotation|@
name|Override
DECL|method|computeLinearDistance
specifier|public
name|double
name|computeLinearDistance
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
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|linearDistance
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/**    * Compute a squared linear distance to the vector.    */
annotation|@
name|Override
DECL|method|computeSquaredLinearDistance
specifier|public
name|double
name|computeSquaredLinearDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|linearDistanceSquared
argument_list|(
name|point
argument_list|)
return|;
block|}
comment|/**    * Compute a squared linear distance to the vector.    */
annotation|@
name|Override
DECL|method|computeSquaredLinearDistance
specifier|public
name|double
name|computeSquaredLinearDistance
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
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|linearDistanceSquared
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/**    * Compute a true, accurate, great-circle distance.    * Double.MAX_VALUE indicates a point is outside of the shape.    */
annotation|@
name|Override
DECL|method|computeArcDistance
specifier|public
name|double
name|computeArcDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|this
operator|.
name|center
operator|.
name|arcDistance
argument_list|(
name|point
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
name|Vector
name|point
parameter_list|)
block|{
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Fastest way of determining membership
return|return
name|circlePlane
operator|.
name|isWithin
argument_list|(
name|point
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
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Fastest way of determining membership
return|return
name|circlePlane
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
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|circlePlane
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|p
argument_list|,
name|notablePoints
argument_list|,
name|circlePoints
argument_list|,
name|bounds
argument_list|)
return|;
block|}
comment|/**    * Compute longitude/latitude bounds for the shape.    *    * @param bounds is the optional input bounds object.  If this is null,    *               a bounds object will be created.  Otherwise, the input object will be modified.    * @return a Bounds object describing the shape's bounds.  If the bounds cannot    * be computed, then return a Bounds object with noLongitudeBound,    * noTopLatitudeBound, and noBottomLatitudeBound.    */
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|Bounds
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|bounds
operator|=
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
comment|// Entire world
name|bounds
operator|.
name|noTopLatitudeBound
argument_list|()
operator|.
name|noBottomLatitudeBound
argument_list|()
operator|.
name|noLongitudeBound
argument_list|()
expr_stmt|;
return|return
name|bounds
return|;
block|}
name|bounds
operator|.
name|addPoint
argument_list|(
name|center
argument_list|)
expr_stmt|;
name|circlePlane
operator|.
name|recordBounds
argument_list|(
name|planetModel
argument_list|,
name|bounds
argument_list|)
expr_stmt|;
return|return
name|bounds
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
name|GeoCircle
operator|)
condition|)
return|return
literal|false
return|;
name|GeoCircle
name|other
init|=
operator|(
name|GeoCircle
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
name|center
operator|.
name|equals
argument_list|(
name|center
argument_list|)
operator|&&
name|other
operator|.
name|cutoffAngle
operator|==
name|cutoffAngle
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
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|center
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|long
name|temp
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|cutoffAngle
argument_list|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
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
literal|"GeoCircle: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", center="
operator|+
name|center
operator|+
literal|", radius="
operator|+
name|cutoffAngle
operator|+
literal|"("
operator|+
name|cutoffAngle
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

