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
comment|/**  * 3D rectangle, bounded on six sides by X,Y,Z limits, degenerate in X and Z.  * This figure, in fact, represents either zero, one, or two points, so the  * actual data stored is minimal.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|dXYdZSolid
class|class
name|dXYdZSolid
extends|extends
name|BaseXYZSolid
block|{
comment|/** The points in this figure on the planet surface; also doubles for edge points */
DECL|field|surfacePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|surfacePoints
decl_stmt|;
comment|/**    * Sole constructor    *    *@param planetModel is the planet model.    *@param X is the X value.    *@param minY is the minimum Y value.    *@param maxY is the maximum Y value.    *@param Z is the Z value.    */
DECL|method|dXYdZSolid
specifier|public
name|dXYdZSolid
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|X
parameter_list|,
specifier|final
name|double
name|minY
parameter_list|,
specifier|final
name|double
name|maxY
parameter_list|,
specifier|final
name|double
name|Z
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
name|maxY
operator|-
name|minY
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Y values in wrong order or identical"
argument_list|)
throw|;
comment|// Build the planes and intersect them.
specifier|final
name|Plane
name|xPlane
init|=
operator|new
name|Plane
argument_list|(
name|xUnitVector
argument_list|,
operator|-
name|X
argument_list|)
decl_stmt|;
specifier|final
name|Plane
name|zPlane
init|=
operator|new
name|Plane
argument_list|(
name|zUnitVector
argument_list|,
operator|-
name|Z
argument_list|)
decl_stmt|;
specifier|final
name|SidedPlane
name|minYPlane
init|=
operator|new
name|SidedPlane
argument_list|(
literal|0.0
argument_list|,
name|maxY
argument_list|,
literal|0.0
argument_list|,
name|yUnitVector
argument_list|,
operator|-
name|minY
argument_list|)
decl_stmt|;
specifier|final
name|SidedPlane
name|maxYPlane
init|=
operator|new
name|SidedPlane
argument_list|(
literal|0.0
argument_list|,
name|minY
argument_list|,
literal|0.0
argument_list|,
name|yUnitVector
argument_list|,
operator|-
name|maxY
argument_list|)
decl_stmt|;
name|surfacePoints
operator|=
name|xPlane
operator|.
name|findIntersections
argument_list|(
name|planetModel
argument_list|,
name|zPlane
argument_list|,
name|minYPlane
argument_list|,
name|maxYPlane
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|protected
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|surfacePoints
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
for|for
control|(
specifier|final
name|GeoPoint
name|p
range|:
name|surfacePoints
control|)
block|{
if|if
condition|(
name|p
operator|.
name|isIdentical
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
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
comment|//System.err.println(this+" getrelationship with "+path);
specifier|final
name|int
name|insideRectangle
init|=
name|isShapeInsideArea
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
comment|// Figure out if the entire XYZArea is contained by the shape.
specifier|final
name|int
name|insideShape
init|=
name|isAreaInsideShape
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideShape
operator|==
name|SOME_INSIDE
condition|)
block|{
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
operator|&&
name|insideShape
operator|==
name|ALL_INSIDE
condition|)
block|{
comment|//System.err.println(" inside of each other");
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
return|return
name|WITHIN
return|;
block|}
if|if
condition|(
name|insideShape
operator|==
name|ALL_INSIDE
condition|)
block|{
comment|//System.err.println(" shape contains rectangle");
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
name|dXYdZSolid
operator|)
condition|)
return|return
literal|false
return|;
name|dXYdZSolid
name|other
init|=
operator|(
name|dXYdZSolid
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|||
name|surfacePoints
operator|.
name|length
operator|!=
name|other
operator|.
name|surfacePoints
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|surfacePoints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|surfacePoints
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|other
operator|.
name|surfacePoints
index|[
name|i
index|]
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
for|for
control|(
specifier|final
name|GeoPoint
name|p
range|:
name|surfacePoints
control|)
block|{
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|p
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|GeoPoint
name|p
range|:
name|surfacePoints
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
literal|"dXYdZSolid: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", "
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

