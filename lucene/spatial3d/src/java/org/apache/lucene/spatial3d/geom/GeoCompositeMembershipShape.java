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
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * GeoComposite is a set of GeoMembershipShape's, treated as a unit.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoCompositeMembershipShape
specifier|public
class|class
name|GeoCompositeMembershipShape
implements|implements
name|GeoMembershipShape
block|{
comment|/** The list of shapes. */
DECL|field|shapes
specifier|protected
specifier|final
name|List
argument_list|<
name|GeoMembershipShape
argument_list|>
name|shapes
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoMembershipShape
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Constructor.    */
DECL|method|GeoCompositeMembershipShape
specifier|public
name|GeoCompositeMembershipShape
parameter_list|()
block|{   }
comment|/**    * Add a shape to the composite.    *@param shape is the shape to add.    */
DECL|method|addShape
specifier|public
name|void
name|addShape
parameter_list|(
specifier|final
name|GeoMembershipShape
name|shape
parameter_list|)
block|{
name|shapes
operator|.
name|add
argument_list|(
name|shape
argument_list|)
expr_stmt|;
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
for|for
control|(
name|GeoMembershipShape
name|shape
range|:
name|shapes
control|)
block|{
if|if
condition|(
name|shape
operator|.
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
literal|true
return|;
block|}
return|return
literal|false
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
name|shapes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEdgePoints
argument_list|()
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
for|for
control|(
name|GeoMembershipShape
name|shape
range|:
name|shapes
control|)
block|{
if|if
condition|(
name|shape
operator|.
name|intersects
argument_list|(
name|p
argument_list|,
name|notablePoints
argument_list|,
name|bounds
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
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
for|for
control|(
name|GeoMembershipShape
name|shape
range|:
name|shapes
control|)
block|{
name|shape
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
block|}
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
name|computeOutsideDistance
argument_list|(
name|distanceStyle
argument_list|,
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
name|double
name|distance
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
for|for
control|(
name|GeoMembershipShape
name|shape
range|:
name|shapes
control|)
block|{
specifier|final
name|double
name|normalDistance
init|=
name|shape
operator|.
name|computeOutsideDistance
argument_list|(
name|distanceStyle
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalDistance
operator|<
name|distance
condition|)
block|{
name|distance
operator|=
name|normalDistance
expr_stmt|;
block|}
block|}
return|return
name|distance
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
name|GeoCompositeMembershipShape
operator|)
condition|)
return|return
literal|false
return|;
name|GeoCompositeMembershipShape
name|other
init|=
operator|(
name|GeoCompositeMembershipShape
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
name|shapes
operator|.
name|equals
argument_list|(
name|other
operator|.
name|shapes
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
return|return
name|super
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|shapes
operator|.
name|hashCode
argument_list|()
return|;
comment|//TODO cache
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
literal|"GeoCompositeMembershipShape: {"
operator|+
name|shapes
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

