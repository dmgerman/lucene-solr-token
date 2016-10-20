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
comment|/**  * Bounding box wider than PI but limited on three sides (top lat,  * left lon, right lon).  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|GeoWideSouthRectangle
class|class
name|GeoWideSouthRectangle
extends|extends
name|GeoBaseBBox
block|{
comment|/** Top latitude of rect */
DECL|field|topLat
specifier|protected
specifier|final
name|double
name|topLat
decl_stmt|;
comment|/** Left longitude of rect */
DECL|field|leftLon
specifier|protected
specifier|final
name|double
name|leftLon
decl_stmt|;
comment|/** Right longitude of rect */
DECL|field|rightLon
specifier|protected
specifier|final
name|double
name|rightLon
decl_stmt|;
comment|/** Cosine of middle latitude */
DECL|field|cosMiddleLat
specifier|protected
specifier|final
name|double
name|cosMiddleLat
decl_stmt|;
comment|/** Upper left hand corner */
DECL|field|ULHC
specifier|protected
specifier|final
name|GeoPoint
name|ULHC
decl_stmt|;
comment|/** Upper right hand corner */
DECL|field|URHC
specifier|protected
specifier|final
name|GeoPoint
name|URHC
decl_stmt|;
comment|/** The top plane */
DECL|field|topPlane
specifier|protected
specifier|final
name|SidedPlane
name|topPlane
decl_stmt|;
comment|/** The left plane */
DECL|field|leftPlane
specifier|protected
specifier|final
name|SidedPlane
name|leftPlane
decl_stmt|;
comment|/** The right plane */
DECL|field|rightPlane
specifier|protected
specifier|final
name|SidedPlane
name|rightPlane
decl_stmt|;
comment|/** Notable points for top plane */
DECL|field|topPlanePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|topPlanePoints
decl_stmt|;
comment|/** Notable points for left plane */
DECL|field|leftPlanePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|leftPlanePoints
decl_stmt|;
comment|/** Notable points for right plane */
DECL|field|rightPlanePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|rightPlanePoints
decl_stmt|;
comment|/** Center point */
DECL|field|centerPoint
specifier|protected
specifier|final
name|GeoPoint
name|centerPoint
decl_stmt|;
comment|/** Left/right bounds */
DECL|field|eitherBound
specifier|protected
specifier|final
name|EitherBound
name|eitherBound
decl_stmt|;
comment|/** A point on the edge */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/**    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}.    * Horizontal angle must be greater than or equal to PI.    */
DECL|method|GeoWideSouthRectangle
specifier|public
name|GeoWideSouthRectangle
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|topLat
parameter_list|,
specifier|final
name|double
name|leftLon
parameter_list|,
name|double
name|rightLon
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
name|topLat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|topLat
operator|<
operator|-
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
literal|"Top latitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|leftLon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|leftLon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Left longitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|rightLon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|rightLon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Right longitude out of range"
argument_list|)
throw|;
name|double
name|extent
init|=
name|rightLon
operator|-
name|leftLon
decl_stmt|;
if|if
condition|(
name|extent
operator|<
literal|0.0
condition|)
block|{
name|extent
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
block|}
if|if
condition|(
name|extent
operator|<
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Width of rectangle too small"
argument_list|)
throw|;
name|this
operator|.
name|topLat
operator|=
name|topLat
expr_stmt|;
name|this
operator|.
name|leftLon
operator|=
name|leftLon
expr_stmt|;
name|this
operator|.
name|rightLon
operator|=
name|rightLon
expr_stmt|;
specifier|final
name|double
name|sinTopLat
init|=
name|Math
operator|.
name|sin
argument_list|(
name|topLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosTopLat
init|=
name|Math
operator|.
name|cos
argument_list|(
name|topLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinLeftLon
init|=
name|Math
operator|.
name|sin
argument_list|(
name|leftLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosLeftLon
init|=
name|Math
operator|.
name|cos
argument_list|(
name|leftLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinRightLon
init|=
name|Math
operator|.
name|sin
argument_list|(
name|rightLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosRightLon
init|=
name|Math
operator|.
name|cos
argument_list|(
name|rightLon
argument_list|)
decl_stmt|;
comment|// Now build the four points
name|this
operator|.
name|ULHC
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinTopLat
argument_list|,
name|sinLeftLon
argument_list|,
name|cosTopLat
argument_list|,
name|cosLeftLon
argument_list|,
name|topLat
argument_list|,
name|leftLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|URHC
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinTopLat
argument_list|,
name|sinRightLon
argument_list|,
name|cosTopLat
argument_list|,
name|cosRightLon
argument_list|,
name|topLat
argument_list|,
name|rightLon
argument_list|)
expr_stmt|;
specifier|final
name|double
name|middleLat
init|=
operator|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|)
operator|*
literal|0.5
decl_stmt|;
specifier|final
name|double
name|sinMiddleLat
init|=
name|Math
operator|.
name|sin
argument_list|(
name|middleLat
argument_list|)
decl_stmt|;
name|this
operator|.
name|cosMiddleLat
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|middleLat
argument_list|)
expr_stmt|;
comment|// Normalize
while|while
condition|(
name|leftLon
operator|>
name|rightLon
condition|)
block|{
name|rightLon
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
block|}
specifier|final
name|double
name|middleLon
init|=
operator|(
name|leftLon
operator|+
name|rightLon
operator|)
operator|*
literal|0.5
decl_stmt|;
specifier|final
name|double
name|sinMiddleLon
init|=
name|Math
operator|.
name|sin
argument_list|(
name|middleLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosMiddleLon
init|=
name|Math
operator|.
name|cos
argument_list|(
name|middleLon
argument_list|)
decl_stmt|;
name|this
operator|.
name|centerPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinMiddleLat
argument_list|,
name|sinMiddleLon
argument_list|,
name|cosMiddleLat
argument_list|,
name|cosMiddleLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|topPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|planetModel
argument_list|,
name|sinTopLat
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|cosLeftLon
argument_list|,
name|sinLeftLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|cosRightLon
argument_list|,
name|sinRightLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|topPlanePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|ULHC
block|,
name|URHC
block|}
expr_stmt|;
name|this
operator|.
name|leftPlanePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|ULHC
block|,
name|planetModel
operator|.
name|SOUTH_POLE
block|}
expr_stmt|;
name|this
operator|.
name|rightPlanePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|URHC
block|,
name|planetModel
operator|.
name|SOUTH_POLE
block|}
expr_stmt|;
name|this
operator|.
name|eitherBound
operator|=
operator|new
name|EitherBound
argument_list|()
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
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
specifier|final
name|double
name|newTopLat
init|=
name|topLat
operator|+
name|angle
decl_stmt|;
specifier|final
name|double
name|newBottomLat
init|=
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
decl_stmt|;
comment|// Figuring out when we escalate to a special case requires some prefiguring
name|double
name|currentLonSpan
init|=
name|rightLon
operator|-
name|leftLon
decl_stmt|;
if|if
condition|(
name|currentLonSpan
operator|<
literal|0.0
condition|)
name|currentLonSpan
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
name|double
name|newLeftLon
init|=
name|leftLon
operator|-
name|angle
decl_stmt|;
name|double
name|newRightLon
init|=
name|rightLon
operator|+
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
name|newTopLat
argument_list|,
name|newBottomLat
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
name|topPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
operator|(
name|leftPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|||
name|rightPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|)
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
comment|// Here we compute the distance from the middle point to one of the corners.  However, we need to be careful
comment|// to use the longest of three distances: the distance to a corner on the top; the distnace to a corner on the bottom, and
comment|// the distance to the right or left edge from the center.
specifier|final
name|double
name|centerAngle
init|=
operator|(
name|rightLon
operator|-
operator|(
name|rightLon
operator|+
name|leftLon
operator|)
operator|*
literal|0.5
operator|)
operator|*
name|cosMiddleLat
decl_stmt|;
specifier|final
name|double
name|topAngle
init|=
name|centerPoint
operator|.
name|arcDistance
argument_list|(
name|URHC
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|centerAngle
argument_list|,
name|topAngle
argument_list|)
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
name|centerPoint
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
comment|// Right and left bounds are essentially independent hemispheres; crossing into the wrong part of one
comment|// requires crossing into the right part of the other.  So intersection can ignore the left/right bounds.
return|return
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|topPlane
argument_list|,
name|notablePoints
argument_list|,
name|topPlanePoints
argument_list|,
name|bounds
argument_list|,
name|eitherBound
argument_list|)
operator|||
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|leftPlane
argument_list|,
name|notablePoints
argument_list|,
name|leftPlanePoints
argument_list|,
name|bounds
argument_list|,
name|topPlane
argument_list|)
operator|||
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|rightPlane
argument_list|,
name|notablePoints
argument_list|,
name|rightPlanePoints
argument_list|,
name|bounds
argument_list|,
name|topPlane
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
name|isWide
argument_list|()
operator|.
name|addHorizontalPlane
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|topPlane
argument_list|,
name|eitherBound
argument_list|)
operator|.
name|addVerticalPlane
argument_list|(
name|planetModel
argument_list|,
name|rightLon
argument_list|,
name|rightPlane
argument_list|,
name|topPlane
argument_list|)
operator|.
name|addVerticalPlane
argument_list|(
name|planetModel
argument_list|,
name|leftLon
argument_list|,
name|leftPlane
argument_list|,
name|topPlane
argument_list|)
operator|.
name|addIntersection
argument_list|(
name|planetModel
argument_list|,
name|leftPlane
argument_list|,
name|rightPlane
argument_list|,
name|topPlane
argument_list|)
operator|.
name|addPoint
argument_list|(
name|ULHC
argument_list|)
operator|.
name|addPoint
argument_list|(
name|URHC
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
comment|//System.err.println(this+" comparing to "+path);
specifier|final
name|int
name|insideRectangle
init|=
name|isShapeInsideBBox
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideRectangle
operator|==
name|SOME_INSIDE
condition|)
block|{
comment|//System.err.println(" some inside");
return|return
name|OVERLAPS
return|;
block|}
specifier|final
name|boolean
name|insideShape
init|=
name|path
operator|.
name|isWithin
argument_list|(
name|planetModel
operator|.
name|SOUTH_POLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
operator|&&
name|insideShape
condition|)
block|{
comment|//System.err.println(" both inside each other");
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|topPlane
argument_list|,
name|topPlanePoints
argument_list|,
name|eitherBound
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|leftPlane
argument_list|,
name|leftPlanePoints
argument_list|,
name|topPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|rightPlane
argument_list|,
name|rightPlanePoints
argument_list|,
name|topPlane
argument_list|)
condition|)
block|{
comment|//System.err.println(" edges intersect");
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
condition|)
block|{
comment|//System.err.println(" shape inside rectangle");
return|return
name|WITHIN
return|;
block|}
if|if
condition|(
name|insideShape
condition|)
block|{
comment|//System.err.println(" rectangle inside shape");
return|return
name|CONTAINS
return|;
block|}
comment|//System.err.println(" disjoint");
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
name|topDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|topPlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|eitherBound
argument_list|)
decl_stmt|;
comment|// Because the rectangle exceeds 180 degrees, it is safe to compute the horizontally
comment|// unbounded distance to both the left and the right and only take the minimum of the two.
specifier|final
name|double
name|leftDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|leftPlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|topPlane
argument_list|)
decl_stmt|;
specifier|final
name|double
name|rightDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|rightPlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|topPlane
argument_list|)
decl_stmt|;
specifier|final
name|double
name|ULHCDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|ULHC
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
name|URHCDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|URHC
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
name|Math
operator|.
name|min
argument_list|(
name|topDistance
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|leftDistance
argument_list|,
name|rightDistance
argument_list|)
argument_list|)
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|ULHCDistance
argument_list|,
name|URHCDistance
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
name|GeoWideSouthRectangle
operator|)
condition|)
return|return
literal|false
return|;
name|GeoWideSouthRectangle
name|other
init|=
operator|(
name|GeoWideSouthRectangle
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
name|other
operator|.
name|ULHC
operator|.
name|equals
argument_list|(
name|ULHC
argument_list|)
operator|&&
name|other
operator|.
name|URHC
operator|.
name|equals
argument_list|(
name|URHC
argument_list|)
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
name|ULHC
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|URHC
operator|.
name|hashCode
argument_list|()
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
literal|"GeoWideSouthRectangle: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", toplat="
operator|+
name|topLat
operator|+
literal|"("
operator|+
name|topLat
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), leftlon="
operator|+
name|leftLon
operator|+
literal|"("
operator|+
name|leftLon
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), rightlon="
operator|+
name|rightLon
operator|+
literal|"("
operator|+
name|rightLon
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
comment|/** Membership implementation representing width more than 180.    */
DECL|class|EitherBound
specifier|protected
class|class
name|EitherBound
implements|implements
name|Membership
block|{
comment|/** Constructor.      */
DECL|method|EitherBound
specifier|public
name|EitherBound
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|leftPlane
operator|.
name|isWithin
argument_list|(
name|v
argument_list|)
operator|||
name|rightPlane
operator|.
name|isWithin
argument_list|(
name|v
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
name|leftPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|||
name|rightPlane
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
block|}
block|}
end_class

end_unit

