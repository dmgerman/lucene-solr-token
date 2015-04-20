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
comment|/** This class represents a degenerate point bounding box. * It is not a simple GeoPoint because we must have the latitude and longitude. */
end_comment

begin_class
DECL|class|GeoDegeneratePoint
specifier|public
class|class
name|GeoDegeneratePoint
extends|extends
name|GeoPoint
implements|implements
name|GeoBBox
block|{
DECL|field|latitude
specifier|public
specifier|final
name|double
name|latitude
decl_stmt|;
DECL|field|longitude
specifier|public
specifier|final
name|double
name|longitude
decl_stmt|;
DECL|field|edgePoints
specifier|public
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
DECL|method|GeoDegeneratePoint
specifier|public
name|GeoDegeneratePoint
parameter_list|(
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|)
block|{
name|super
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
name|this
operator|.
name|latitude
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|longitude
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|this
block|}
expr_stmt|;
block|}
comment|/** Expand box by specified angle.      *@param angle is the angle amount to expand the GeoBBox by.      *@return a new GeoBBox.      */
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
name|latitude
operator|+
name|angle
decl_stmt|;
specifier|final
name|double
name|newBottomLat
init|=
name|latitude
operator|-
name|angle
decl_stmt|;
specifier|final
name|double
name|newLeftLon
init|=
name|longitude
operator|-
name|angle
decl_stmt|;
specifier|final
name|double
name|newRightLon
init|=
name|longitude
operator|+
name|angle
decl_stmt|;
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
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
comment|/** Return a sample point that is on the edge of the shape.      *@return an interior point.      */
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
comment|/** Assess whether a plane, within the provided bounds, intersects      * with the shape.      *@param plane is the plane to assess for intersection with the shape's edges or      *  bounding curves.      *@param bounds are a set of bounds that define an area that an      *  intersection must be within in order to qualify (provided by a GeoArea).      *@return true if there's such an intersection, false if not.      */
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|plane
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
if|if
condition|(
name|plane
operator|.
name|evaluate
argument_list|(
name|this
argument_list|)
operator|==
literal|0.0
condition|)
return|return
literal|false
return|;
for|for
control|(
name|Membership
name|m
range|:
name|bounds
control|)
block|{
if|if
condition|(
operator|!
name|m
operator|.
name|isWithin
argument_list|(
name|this
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Compute longitude/latitude bounds for the shape.     *@param bounds is the optional input bounds object.  If this is null,     * a bounds object will be created.  Otherwise, the input object will be modified.     *@return a Bounds object describing the shape's bounds.  If the bounds cannot     * be computed, then return a Bounds object with noLongitudeBound,     * noTopLatitudeBound, and noBottomLatitudeBound.     */
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
if|if
condition|(
name|bounds
operator|==
literal|null
condition|)
name|bounds
operator|=
operator|new
name|Bounds
argument_list|()
expr_stmt|;
name|bounds
operator|.
name|addPoint
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
expr_stmt|;
return|return
name|bounds
return|;
block|}
comment|/** Equals */
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
name|GeoDegeneratePoint
operator|)
condition|)
return|return
literal|false
return|;
name|GeoDegeneratePoint
name|other
init|=
operator|(
name|GeoDegeneratePoint
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|latitude
operator|==
name|latitude
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
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|result
operator|=
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
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
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
literal|"GeoDegeneratePoint: {lat="
operator|+
name|latitude
operator|+
literal|"("
operator|+
name|latitude
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), lon="
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
comment|/** Check if a point is within this shape.      *@param point is the point to check.      *@return true if the point is within this shape      */
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
return|return
name|isWithin
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
comment|/** Check if a point is within this shape.      *@param x is x coordinate of point to check.      *@param y is y coordinate of point to check.      *@param z is z coordinate of point to check.      *@return true if the point is within this shape      */
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
name|x
operator|==
name|this
operator|.
name|x
operator|&&
name|y
operator|==
name|this
operator|.
name|y
operator|&&
name|z
operator|==
name|this
operator|.
name|z
return|;
block|}
comment|/** Returns the radius of a circle into which the GeoSizeable area can      * be inscribed.      *@return the radius.      */
annotation|@
name|Override
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
block|{
return|return
literal|0.0
return|;
block|}
comment|/** Find the spatial relationship between a shape and the current geo area.      * Note: return value is how the GeoShape relates to the GeoArea, not the      * other way around. For example, if this GeoArea is entirely within the      * shape, then CONTAINS should be returned.  If the shape is entirely enclosed      * by this GeoArea, then WITHIN should be returned.      *@param shape is the shape to consider.      *@return the relationship, from the perspective of the shape.      */
annotation|@
name|Override
DECL|method|getRelationship
specifier|public
name|int
name|getRelationship
parameter_list|(
specifier|final
name|GeoShape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|.
name|isWithin
argument_list|(
name|this
argument_list|)
condition|)
return|return
name|CONTAINS
return|;
return|return
name|DISJOINT
return|;
block|}
block|}
end_class

end_unit

