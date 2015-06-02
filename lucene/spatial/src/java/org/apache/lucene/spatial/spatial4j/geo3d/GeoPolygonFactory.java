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
name|BitSet
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
comment|/**  * Class which constructs a GeoMembershipShape representing an arbitrary polygon.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPolygonFactory
specifier|public
class|class
name|GeoPolygonFactory
block|{
DECL|method|GeoPolygonFactory
specifier|private
name|GeoPolygonFactory
parameter_list|()
block|{   }
comment|/**    * Create a GeoMembershipShape of the right kind given the specified bounds.    *    * @param pointList        is a list of the GeoPoints to build an arbitrary polygon out of.    * @param convexPointIndex is the index of a single convex point whose conformation with    *                         its neighbors determines inside/outside for the entire polygon.    * @return a GeoMembershipShape corresponding to what was specified.    */
DECL|method|makeGeoPolygon
specifier|public
specifier|static
name|GeoMembershipShape
name|makeGeoPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|,
specifier|final
name|int
name|convexPointIndex
parameter_list|)
block|{
comment|// The basic operation uses a set of points, two points determining one particular edge, and a sided plane
comment|// describing membership.
return|return
name|buildPolygonShape
argument_list|(
name|planetModel
argument_list|,
name|pointList
argument_list|,
name|convexPointIndex
argument_list|,
name|getLegalIndex
argument_list|(
name|convexPointIndex
operator|+
literal|1
argument_list|,
name|pointList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SidedPlane
argument_list|(
name|pointList
operator|.
name|get
argument_list|(
name|getLegalIndex
argument_list|(
name|convexPointIndex
operator|-
literal|1
argument_list|,
name|pointList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|pointList
operator|.
name|get
argument_list|(
name|convexPointIndex
argument_list|)
argument_list|,
name|pointList
operator|.
name|get
argument_list|(
name|getLegalIndex
argument_list|(
name|convexPointIndex
operator|+
literal|1
argument_list|,
name|pointList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|buildPolygonShape
specifier|public
specifier|static
name|GeoMembershipShape
name|buildPolygonShape
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointsList
parameter_list|,
specifier|final
name|int
name|startPointIndex
parameter_list|,
specifier|final
name|int
name|endPointIndex
parameter_list|,
specifier|final
name|SidedPlane
name|startingEdge
parameter_list|,
specifier|final
name|boolean
name|isInternalEdge
parameter_list|)
block|{
comment|// Algorithm as follows:
comment|// Start with sided edge.  Go through all points in some order.  For each new point, determine if the point is within all edges considered so far.
comment|// If not, put it into a list of points for recursion.  If it is within, add new edge and keep going.
comment|// Once we detect a point that is within, if there are points put aside for recursion, then call recursively.
comment|// Current composite.  This is what we'll actually be returning.
specifier|final
name|GeoCompositeMembershipShape
name|rval
init|=
operator|new
name|GeoCompositeMembershipShape
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|recursionList
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|currentList
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|BitSet
name|internalEdgeList
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SidedPlane
argument_list|>
name|currentPlanes
init|=
operator|new
name|ArrayList
argument_list|<
name|SidedPlane
argument_list|>
argument_list|()
decl_stmt|;
comment|// Initialize the current list and current planes
name|currentList
operator|.
name|add
argument_list|(
name|pointsList
operator|.
name|get
argument_list|(
name|startPointIndex
argument_list|)
argument_list|)
expr_stmt|;
name|currentList
operator|.
name|add
argument_list|(
name|pointsList
operator|.
name|get
argument_list|(
name|endPointIndex
argument_list|)
argument_list|)
expr_stmt|;
name|internalEdgeList
operator|.
name|set
argument_list|(
name|currentPlanes
operator|.
name|size
argument_list|()
argument_list|,
name|isInternalEdge
argument_list|)
expr_stmt|;
name|currentPlanes
operator|.
name|add
argument_list|(
name|startingEdge
argument_list|)
expr_stmt|;
comment|// Now, scan all remaining points, in order.  We'll use an index and just add to it.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pointsList
operator|.
name|size
argument_list|()
operator|-
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|GeoPoint
name|newPoint
init|=
name|pointsList
operator|.
name|get
argument_list|(
name|getLegalIndex
argument_list|(
name|i
operator|+
name|endPointIndex
operator|+
literal|1
argument_list|,
name|pointsList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isWithin
argument_list|(
name|newPoint
argument_list|,
name|currentPlanes
argument_list|)
condition|)
block|{
comment|// Construct a sided plane based on the last two points, and the previous point
name|SidedPlane
name|newBoundary
init|=
operator|new
name|SidedPlane
argument_list|(
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|,
name|newPoint
argument_list|,
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// Construct a sided plane based on the return trip
name|SidedPlane
name|returnBoundary
init|=
operator|new
name|SidedPlane
argument_list|(
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|currentList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|newPoint
argument_list|)
decl_stmt|;
comment|// Verify that none of the points beyond the new point in the list are inside the polygon we'd
comment|// be creating if we stopped making the current polygon right now.
name|boolean
name|pointInside
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|pointsList
operator|.
name|size
argument_list|()
operator|-
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|GeoPoint
name|checkPoint
init|=
name|pointsList
operator|.
name|get
argument_list|(
name|getLegalIndex
argument_list|(
name|j
operator|+
name|endPointIndex
operator|+
literal|1
argument_list|,
name|pointsList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|isInside
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|isInside
operator|&&
operator|!
name|newBoundary
operator|.
name|isWithin
argument_list|(
name|checkPoint
argument_list|)
condition|)
name|isInside
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|isInside
operator|&&
operator|!
name|returnBoundary
operator|.
name|isWithin
argument_list|(
name|checkPoint
argument_list|)
condition|)
name|isInside
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|isInside
condition|)
block|{
for|for
control|(
name|SidedPlane
name|plane
range|:
name|currentPlanes
control|)
block|{
if|if
condition|(
operator|!
name|plane
operator|.
name|isWithin
argument_list|(
name|checkPoint
argument_list|)
condition|)
block|{
name|isInside
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|isInside
condition|)
block|{
name|pointInside
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|pointInside
condition|)
block|{
comment|// Any excluded points?
name|boolean
name|isInternalBoundary
init|=
name|recursionList
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|isInternalBoundary
condition|)
block|{
comment|// Handle exclusion
name|recursionList
operator|.
name|add
argument_list|(
name|newPoint
argument_list|)
expr_stmt|;
name|recursionList
operator|.
name|add
argument_list|(
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|recursionList
operator|.
name|size
argument_list|()
operator|==
name|pointsList
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// We are trying to recurse with a list the same size as the one we started with.
comment|// Clearly, the polygon cannot be constructed
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon is illegal; cannot be decomposed into convex parts"
argument_list|)
throw|;
block|}
comment|// We want the other side for the recursion
name|SidedPlane
name|otherSideNewBoundary
init|=
operator|new
name|SidedPlane
argument_list|(
name|newBoundary
argument_list|)
decl_stmt|;
name|rval
operator|.
name|addShape
argument_list|(
name|buildPolygonShape
argument_list|(
name|planetModel
argument_list|,
name|recursionList
argument_list|,
name|recursionList
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|,
name|recursionList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|otherSideNewBoundary
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|recursionList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|currentList
operator|.
name|add
argument_list|(
name|newPoint
argument_list|)
expr_stmt|;
name|internalEdgeList
operator|.
name|set
argument_list|(
name|currentPlanes
operator|.
name|size
argument_list|()
argument_list|,
name|isInternalBoundary
argument_list|)
expr_stmt|;
name|currentPlanes
operator|.
name|add
argument_list|(
name|newBoundary
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|recursionList
operator|.
name|add
argument_list|(
name|newPoint
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|recursionList
operator|.
name|add
argument_list|(
name|newPoint
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|returnEdgeInternalBoundary
init|=
name|recursionList
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|returnEdgeInternalBoundary
condition|)
block|{
comment|// The last step back to the start point had a recursion, so take care of that before we complete our work
name|recursionList
operator|.
name|add
argument_list|(
name|currentList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|recursionList
operator|.
name|add
argument_list|(
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|recursionList
operator|.
name|size
argument_list|()
operator|==
name|pointsList
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// We are trying to recurse with a list the same size as the one we started with.
comment|// Clearly, the polygon cannot be constructed
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon is illegal; cannot be decomposed into convex parts"
argument_list|)
throw|;
block|}
comment|// Construct a sided plane based on these two points, and the previous point
name|SidedPlane
name|newBoundary
init|=
operator|new
name|SidedPlane
argument_list|(
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|,
name|currentList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|currentList
operator|.
name|get
argument_list|(
name|currentList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// We want the other side for the recursion
name|SidedPlane
name|otherSideNewBoundary
init|=
operator|new
name|SidedPlane
argument_list|(
name|newBoundary
argument_list|)
decl_stmt|;
name|rval
operator|.
name|addShape
argument_list|(
name|buildPolygonShape
argument_list|(
name|planetModel
argument_list|,
name|recursionList
argument_list|,
name|recursionList
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|,
name|recursionList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|otherSideNewBoundary
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|recursionList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Now, add in the current shape.
name|rval
operator|.
name|addShape
argument_list|(
operator|new
name|GeoConvexPolygon
argument_list|(
name|planetModel
argument_list|,
name|currentList
argument_list|,
name|internalEdgeList
argument_list|,
name|returnEdgeInternalBoundary
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("Done creating polygon");
return|return
name|rval
return|;
block|}
DECL|method|isWithin
specifier|protected
specifier|static
name|boolean
name|isWithin
parameter_list|(
name|GeoPoint
name|newPoint
parameter_list|,
name|List
argument_list|<
name|SidedPlane
argument_list|>
name|currentPlanes
parameter_list|)
block|{
for|for
control|(
name|SidedPlane
name|p
range|:
name|currentPlanes
control|)
block|{
if|if
condition|(
operator|!
name|p
operator|.
name|isWithin
argument_list|(
name|newPoint
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
DECL|method|getLegalIndex
specifier|protected
specifier|static
name|int
name|getLegalIndex
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|size
parameter_list|)
block|{
while|while
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|index
operator|+=
name|size
expr_stmt|;
block|}
while|while
condition|(
name|index
operator|>=
name|size
condition|)
block|{
name|index
operator|-=
name|size
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
block|}
end_class

end_unit

