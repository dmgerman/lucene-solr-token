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
comment|/**  * Generic shape.  This describes methods that help GeoAreas figure out  * how they interact with a shape, for the purposes of coming up with a  * set of geo hash values.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|GeoShape
specifier|public
interface|interface
name|GeoShape
extends|extends
name|Membership
block|{
comment|/**    * Return a sample point that is on the outside edge/boundary of the shape.    *    * @return samples of all edge points from distinct edge sections.  Typically one point    * is returned, but zero or two are also possible.    */
DECL|method|getEdgePoints
specifier|public
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
function_decl|;
comment|/**    * Assess whether a plane, within the provided bounds, intersects    * with the shape.  Note well that this method is allowed to return "true"    * if there are internal edges of a composite shape which intersect the plane.    * Doing this can cause getRelationship() for most GeoBBox shapes to return    * OVERLAPS rather than the more correct CONTAINS, but that cannot be    * helped for some complex shapes that are built out of overlapping parts.    *    * @param plane         is the plane to assess for intersection with the shape's edges or    *                      bounding curves.    * @param notablePoints represents the intersections of the plane with the supplied    *                      bounds.  These are used to disambiguate when two planes are identical and it needs    *                      to be determined whether any points exist that fulfill all the bounds.    * @param bounds        are a set of bounds that define an area that an    *                      intersection must be within in order to qualify (provided by a GeoArea).    * @return true if there's such an intersection, false if not.    */
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
function_decl|;
comment|/**    * Compute bounds for the shape.    *    * @param bounds is the input bounds object.    *             The input object will be modified.    */
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
specifier|final
name|Bounds
name|bounds
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

