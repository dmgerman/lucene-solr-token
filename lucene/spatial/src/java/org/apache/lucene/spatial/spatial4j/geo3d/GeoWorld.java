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
comment|/**  * Bounding box including the entire world.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|GeoWorld
specifier|public
class|class
name|GeoWorld
extends|extends
name|GeoBBoxBase
block|{
DECL|field|originPoint
specifier|protected
specifier|final
specifier|static
name|GeoPoint
name|originPoint
init|=
operator|new
name|GeoPoint
argument_list|(
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
DECL|field|edgePoints
specifier|protected
specifier|final
specifier|static
name|GeoPoint
index|[]
name|edgePoints
init|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
decl_stmt|;
DECL|method|GeoWorld
specifier|public
name|GeoWorld
parameter_list|()
block|{   }
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
return|return
name|this
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
comment|// Totally arbitrary
return|return
name|originPoint
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
return|return
literal|true
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
literal|true
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
literal|false
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
name|noLongitudeBound
argument_list|()
operator|.
name|noTopLatitudeBound
argument_list|()
operator|.
name|noBottomLatitudeBound
argument_list|()
expr_stmt|;
return|return
name|bounds
return|;
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
if|if
condition|(
name|path
operator|.
name|getEdgePoints
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
comment|// Path is always within the world
return|return
name|WITHIN
return|;
return|return
name|OVERLAPS
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
name|GeoWorld
operator|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
return|return
literal|0
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
literal|"GeoWorld"
return|;
block|}
block|}
end_class

end_unit
