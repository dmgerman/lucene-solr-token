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
comment|/**  * Distance computation styles, supporting various ways of computing  * distance to shapes.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|DistanceStyle
specifier|public
interface|interface
name|DistanceStyle
block|{
comment|// convenient access to built-in styles:
DECL|field|ARC
specifier|public
specifier|static
specifier|final
name|ArcDistance
name|ARC
init|=
name|ArcDistance
operator|.
name|INSTANCE
decl_stmt|;
DECL|field|LINEAR
specifier|public
specifier|static
specifier|final
name|LinearDistance
name|LINEAR
init|=
name|LinearDistance
operator|.
name|INSTANCE
decl_stmt|;
DECL|field|LINEAR_SQUARED
specifier|public
specifier|static
specifier|final
name|LinearSquaredDistance
name|LINEAR_SQUARED
init|=
name|LinearSquaredDistance
operator|.
name|INSTANCE
decl_stmt|;
DECL|field|NORMAL
specifier|public
specifier|static
specifier|final
name|NormalDistance
name|NORMAL
init|=
name|NormalDistance
operator|.
name|INSTANCE
decl_stmt|;
DECL|field|NORMAL_SQUARED
specifier|public
specifier|static
specifier|final
name|NormalSquaredDistance
name|NORMAL_SQUARED
init|=
name|NormalSquaredDistance
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Compute the distance from a point to another point.    * @param point1 Starting point    * @param point2 Final point    * @return the distance    */
DECL|method|computeDistance
specifier|public
specifier|default
name|double
name|computeDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point1
parameter_list|,
specifier|final
name|GeoPoint
name|point2
parameter_list|)
block|{
return|return
name|computeDistance
argument_list|(
name|point1
argument_list|,
name|point2
operator|.
name|x
argument_list|,
name|point2
operator|.
name|y
argument_list|,
name|point2
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Compute the distance from a point to another point.    * @param point1 Starting point    * @param x2 Final point x    * @param y2 Final point y    * @param z2 Final point z    * @return the distance    */
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point1
parameter_list|,
specifier|final
name|double
name|x2
parameter_list|,
specifier|final
name|double
name|y2
parameter_list|,
specifier|final
name|double
name|z2
parameter_list|)
function_decl|;
comment|/** Compute the distance from a plane to a point.    * @param planetModel The planet model    * @param plane The plane    * @param point The point    * @param bounds are the plane bounds    * @return the distance    */
DECL|method|computeDistance
specifier|public
specifier|default
name|double
name|computeDistance
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|Plane
name|plane
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|plane
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
argument_list|,
name|bounds
argument_list|)
return|;
block|}
comment|/** Compute the distance from a plane to a point.    * @param planetModel The planet model    * @param plane The plane    * @param x The point x    * @param y The point y    * @param z The point z    * @param bounds are the plane bounds    * @return the distance    */
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|Plane
name|plane
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
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

