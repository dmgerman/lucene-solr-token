begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/**  * This class represents a degenerate point bounding box.  * It is not a simple GeoPoint because we must have the latitude and longitude.  *  * @lucene.internal  */
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
implements|,
name|GeoCircle
block|{
comment|/** Current planet model, since we don't extend BasePlanetObject */
DECL|field|planetModel
specifier|protected
specifier|final
name|PlanetModel
name|planetModel
decl_stmt|;
comment|/** Edge point is an area containing just this */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/** Constructor.    *@param planetModel is the planet model to use.    *@param lat is the latitude.    *@param lon is the longitude.    */
DECL|method|GeoDegeneratePoint
specifier|public
name|GeoDegeneratePoint
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
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
name|this
operator|.
name|planetModel
operator|=
name|planetModel
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
name|plane
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
comment|// If not on the plane, no intersection
if|if
condition|(
operator|!
name|plane
operator|.
name|evaluateIsZero
argument_list|(
name|this
argument_list|)
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
name|bounds
operator|.
name|addPoint
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeOutsideDistance
specifier|public
name|double
name|computeOutsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|this
argument_list|,
name|point
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeOutsideDistance
specifier|public
name|double
name|computeOutsideDistance
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
return|return
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|this
argument_list|,
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
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"GeoDegeneratePoint: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", lat="
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
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
return|return
name|this
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
block|{
comment|//System.err.println("Degenerate point "+this+" is WITHIN shape "+shape);
return|return
name|CONTAINS
return|;
block|}
comment|//System.err.println("Degenerate point "+this+" is NOT within shape "+shape);
return|return
name|DISJOINT
return|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
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
if|if
condition|(
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
literal|0.0
return|;
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
block|}
block|}
end_class

end_unit

