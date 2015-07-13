begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An object for accumulating bounds information.  * The bounds object is initially empty.  Bounding points  * are then applied by supplying (x,y,z) tuples.  It is also  * possible to indicate the following edge cases:  * (1) No longitude bound possible  * (2) No upper latitude bound possible  * (3) No lower latitude bound possible  * When any of these have been applied, further application of  * points cannot override that decision.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Bounds
specifier|public
class|class
name|Bounds
block|{
comment|/** Set to true if no longitude bounds can be stated */
DECL|field|noLongitudeBound
specifier|protected
name|boolean
name|noLongitudeBound
init|=
literal|false
decl_stmt|;
comment|/** Set to true if no top latitude bound can be stated */
DECL|field|noTopLatitudeBound
specifier|protected
name|boolean
name|noTopLatitudeBound
init|=
literal|false
decl_stmt|;
comment|/** Set to true if no bottom latitude bound can be stated */
DECL|field|noBottomLatitudeBound
specifier|protected
name|boolean
name|noBottomLatitudeBound
init|=
literal|false
decl_stmt|;
comment|/** If non-null, the minimum latitude bound */
DECL|field|minLatitude
specifier|protected
name|Double
name|minLatitude
init|=
literal|null
decl_stmt|;
comment|/** If non-null, the maximum latitude bound */
DECL|field|maxLatitude
specifier|protected
name|Double
name|maxLatitude
init|=
literal|null
decl_stmt|;
comment|// For longitude bounds, this class needs to worry about keeping track of the distinction
comment|// between left-side bounds and right-side bounds.  Points are always submitted in pairs
comment|// which have a maximum longitude separation of Math.PI.  It's therefore always possible
comment|// to determine which point represents a left bound, and which point represents a right
comment|// bound.
comment|//
comment|// The next problem is how to compare two of the same kind of bound, e.g. two left bounds.
comment|// We need to keep track of the leftmost longitude of the shape, but since this is a circle,
comment|// this is arbitrary.  What we could try to do instead would be to find a pair of (left,right) bounds such
comment|// that:
comment|// (1) all other bounds are within, and
comment|// (2) the left minus right distance is minimized
comment|// Unfortunately, there are still shapes that cannot be summarized in this way correctly.
comment|// For example. consider a spiral that entirely circles the globe; we might arbitrarily choose
comment|// lat/lon bounds that do not in fact circle the globe.
comment|//
comment|// One way to handle the longitude issue correctly is therefore to stipulate that we
comment|// walk the bounds of the shape in some kind of connected order.  Each point or circle is therefore
comment|// added in a sequence.  We also need an interior point to make sure we have the right
comment|// choice of longitude bounds.  But even with this, we still can't always choose whether the actual shape
comment|// goes right or left.
comment|//
comment|// We can make the specification truly general by submitting the following in order:
comment|// addSide(PlaneSide side, Membership... constraints)
comment|// ...
comment|// This is unambiguous, but I still can't see yet how this would help compute the bounds.  The plane
comment|// solution would in general seem to boil down to the same logic that relies on points along the path
comment|// to define the shape boundaries.  I guess the one thing that you do know for a bounded edge is that
comment|// the endpoints are actually connected.  But it is not clear whether relationship helps in any way.
comment|//
comment|// In any case, if we specify shapes by a sequence of planes, we should stipulate that multiple sequences
comment|// are allowed, provided they progressively tile an area of the sphere that is connected and sequential.
comment|// For example, paths do alternating rectangles and circles, in sequence.  Each sequence member is
comment|// described by a sequence of planes.  I think it would also be reasonable to insist that the first segment
comment|// of a shape overlap or adjoin the previous shape.
comment|//
comment|// Here's a way to think about it that might help: Traversing every edge should grow the longitude bounds
comment|// in the direction of the traversal.  So if the traversal is always known to be less than PI in total longitude
comment|// angle, then it is possible to use the endpoints to determine the unambiguous extension of the envelope.
comment|// For example, say you are currently at longitude -0.5.  The next point is at longitude PI-0.1.  You could say
comment|// that the difference in longitude going one way around would be beter than the distance the other way
comment|// around, and therefore the longitude envelope should be extended accordingly.  But in practice, when an
comment|// edge goes near a pole and may be inclined as well, the longer longitude change might be the right path, even
comment|// if the arc length is short.  So this too doesn't work.
comment|//
comment|// Given we have a hard time making an exact match, here's the current proposal.  The proposal is a
comment|// heuristic, based on the idea that most areas are small compared to the circumference of the globe.
comment|// We keep track of the last point we saw, and take each point as it arrives, and compute its longitude.
comment|// Then, we have a choice as to which way to expand the envelope: we can expand by going to the left or
comment|// to the right.  We choose the direction with the least longitude difference.  (If we aren't sure,
comment|// and can recognize that, we can set "unconstrained in longitude".)
comment|/** If non-null, the left longitude bound */
DECL|field|leftLongitude
specifier|protected
name|Double
name|leftLongitude
init|=
literal|null
decl_stmt|;
comment|/** If non-null, the right longitude bound */
DECL|field|rightLongitude
specifier|protected
name|Double
name|rightLongitude
init|=
literal|null
decl_stmt|;
comment|/** Construct an empty bounds object */
DECL|method|Bounds
specifier|public
name|Bounds
parameter_list|()
block|{   }
comment|/** Get maximum latitude, if any.    *@return maximum latitude or null.    */
DECL|method|getMaxLatitude
specifier|public
name|Double
name|getMaxLatitude
parameter_list|()
block|{
return|return
name|maxLatitude
return|;
block|}
comment|/** Get minimum latitude, if any.    *@return minimum latitude or null.    */
DECL|method|getMinLatitude
specifier|public
name|Double
name|getMinLatitude
parameter_list|()
block|{
return|return
name|minLatitude
return|;
block|}
comment|/** Get left longitude, if any.    *@return left longitude, or null.    */
DECL|method|getLeftLongitude
specifier|public
name|Double
name|getLeftLongitude
parameter_list|()
block|{
return|return
name|leftLongitude
return|;
block|}
comment|/** Get right longitude, if any.    *@return right longitude, or null.    */
DECL|method|getRightLongitude
specifier|public
name|Double
name|getRightLongitude
parameter_list|()
block|{
return|return
name|rightLongitude
return|;
block|}
comment|/** Check if there's no longitude bound.    *@return true if no longitude bound.    */
DECL|method|checkNoLongitudeBound
specifier|public
name|boolean
name|checkNoLongitudeBound
parameter_list|()
block|{
return|return
name|noLongitudeBound
return|;
block|}
comment|/** Check if there's no top latitude bound.    *@return true if no top latitude bound.    */
DECL|method|checkNoTopLatitudeBound
specifier|public
name|boolean
name|checkNoTopLatitudeBound
parameter_list|()
block|{
return|return
name|noTopLatitudeBound
return|;
block|}
comment|/** Check if there's no bottom latitude bound.    *@return true if no bottom latitude bound.    */
DECL|method|checkNoBottomLatitudeBound
specifier|public
name|boolean
name|checkNoBottomLatitudeBound
parameter_list|()
block|{
return|return
name|noBottomLatitudeBound
return|;
block|}
comment|/** Add a constraint representing a horizontal circle with a    * specified z value.    *@param z is the z value.    *@return the updated Bounds object.    */
DECL|method|addHorizontalCircle
specifier|public
name|Bounds
name|addHorizontalCircle
parameter_list|(
name|double
name|z
parameter_list|)
block|{
if|if
condition|(
operator|!
name|noTopLatitudeBound
operator|||
operator|!
name|noBottomLatitudeBound
condition|)
block|{
comment|// Compute a latitude value
name|double
name|latitude
init|=
name|Math
operator|.
name|asin
argument_list|(
name|z
argument_list|)
decl_stmt|;
name|addLatitudeBound
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Add a constraint representing a horizontal circle at    * a specific latitude.    *@param latitude is the latitude.    *@return the updated Bounds object.    */
DECL|method|addLatitudeZone
specifier|public
name|Bounds
name|addLatitudeZone
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
if|if
condition|(
operator|!
name|noTopLatitudeBound
operator|||
operator|!
name|noBottomLatitudeBound
condition|)
block|{
name|addLatitudeBound
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Add a constraint representing a longitude slice.    *@param newLeftLongitude is the left longitude value.    *@param newRightLongitude is the right longitude value.    *@return the updated Bounds object.    */
DECL|method|addLongitudeSlice
specifier|public
name|Bounds
name|addLongitudeSlice
parameter_list|(
name|double
name|newLeftLongitude
parameter_list|,
name|double
name|newRightLongitude
parameter_list|)
block|{
if|if
condition|(
operator|!
name|noLongitudeBound
condition|)
block|{
name|addLongitudeBound
argument_list|(
name|newLeftLongitude
argument_list|,
name|newRightLongitude
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Update latitude bound.    *@param latitude is the latitude.    */
DECL|method|addLatitudeBound
specifier|protected
name|void
name|addLatitudeBound
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
if|if
condition|(
operator|!
name|noTopLatitudeBound
operator|&&
operator|(
name|maxLatitude
operator|==
literal|null
operator|||
name|latitude
operator|>
name|maxLatitude
operator|)
condition|)
name|maxLatitude
operator|=
name|latitude
expr_stmt|;
if|if
condition|(
operator|!
name|noBottomLatitudeBound
operator|&&
operator|(
name|minLatitude
operator|==
literal|null
operator|||
name|latitude
operator|<
name|minLatitude
operator|)
condition|)
name|minLatitude
operator|=
name|latitude
expr_stmt|;
block|}
comment|/** Update longitude bound.    *@param newLeftLongitude is the left longitude.    *@param newRightLongitude is the right longitude.    */
DECL|method|addLongitudeBound
specifier|protected
name|void
name|addLongitudeBound
parameter_list|(
name|double
name|newLeftLongitude
parameter_list|,
name|double
name|newRightLongitude
parameter_list|)
block|{
if|if
condition|(
name|leftLongitude
operator|==
literal|null
operator|&&
name|rightLongitude
operator|==
literal|null
condition|)
block|{
name|leftLongitude
operator|=
name|newLeftLongitude
expr_stmt|;
name|rightLongitude
operator|=
name|newRightLongitude
expr_stmt|;
block|}
else|else
block|{
comment|// Map the current range to something monotonically increasing
name|double
name|currentLeftLongitude
init|=
name|leftLongitude
decl_stmt|;
name|double
name|currentRightLongitude
init|=
name|rightLongitude
decl_stmt|;
if|if
condition|(
name|currentRightLongitude
operator|<
name|currentLeftLongitude
condition|)
name|currentRightLongitude
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
name|double
name|adjustedLeftLongitude
init|=
name|newLeftLongitude
decl_stmt|;
name|double
name|adjustedRightLongitude
init|=
name|newRightLongitude
decl_stmt|;
if|if
condition|(
name|adjustedRightLongitude
operator|<
name|adjustedLeftLongitude
condition|)
name|adjustedRightLongitude
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
comment|// Compare to see what the relationship is
if|if
condition|(
name|currentLeftLongitude
operator|<=
name|adjustedLeftLongitude
operator|&&
name|currentRightLongitude
operator|>=
name|adjustedRightLongitude
condition|)
block|{
comment|// No adjustment needed.
block|}
elseif|else
if|if
condition|(
name|currentLeftLongitude
operator|>=
name|adjustedLeftLongitude
operator|&&
name|currentRightLongitude
operator|<=
name|adjustedRightLongitude
condition|)
block|{
comment|// New longitude entirely contains old one
name|leftLongitude
operator|=
name|newLeftLongitude
expr_stmt|;
name|rightLongitude
operator|=
name|newRightLongitude
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|currentLeftLongitude
operator|>
name|adjustedLeftLongitude
condition|)
block|{
comment|// New left longitude needed
name|leftLongitude
operator|=
name|newLeftLongitude
expr_stmt|;
block|}
if|if
condition|(
name|currentRightLongitude
operator|<
name|adjustedRightLongitude
condition|)
block|{
comment|// New right longitude needed
name|rightLongitude
operator|=
name|newRightLongitude
expr_stmt|;
block|}
block|}
block|}
name|double
name|testRightLongitude
init|=
name|rightLongitude
decl_stmt|;
if|if
condition|(
name|testRightLongitude
operator|<
name|leftLongitude
condition|)
name|testRightLongitude
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
comment|// If the bound exceeds 180 degrees, we know we could have screwed up.
if|if
condition|(
name|testRightLongitude
operator|-
name|leftLongitude
operator|>=
name|Math
operator|.
name|PI
condition|)
block|{
name|noLongitudeBound
operator|=
literal|true
expr_stmt|;
name|leftLongitude
operator|=
literal|null
expr_stmt|;
name|rightLongitude
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Update longitude bound.    *@param longitude is the new longitude value.    */
DECL|method|addLongitudeBound
specifier|protected
name|void
name|addLongitudeBound
parameter_list|(
name|double
name|longitude
parameter_list|)
block|{
comment|// If this point is within the current bounds, we're done; otherwise
comment|// expand one side or the other.
if|if
condition|(
name|leftLongitude
operator|==
literal|null
operator|&&
name|rightLongitude
operator|==
literal|null
condition|)
block|{
name|leftLongitude
operator|=
name|longitude
expr_stmt|;
name|rightLongitude
operator|=
name|longitude
expr_stmt|;
block|}
else|else
block|{
comment|// Compute whether we're to the right of the left value.  But the left value may be greater than
comment|// the right value.
name|double
name|currentLeftLongitude
init|=
name|leftLongitude
decl_stmt|;
name|double
name|currentRightLongitude
init|=
name|rightLongitude
decl_stmt|;
if|if
condition|(
name|currentRightLongitude
operator|<
name|currentLeftLongitude
condition|)
name|currentRightLongitude
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
comment|// We have a range to look at that's going in the right way.
comment|// Now, do the same trick with the computed longitude.
if|if
condition|(
name|longitude
operator|<
name|currentLeftLongitude
condition|)
name|longitude
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|longitude
argument_list|<
name|currentLeftLongitude
operator|||
name|longitude
argument_list|>
name|currentRightLongitude
condition|)
block|{
comment|// Outside of current bounds.  Consider carefully how we'll expand.
name|double
name|leftExtensionAmt
decl_stmt|;
name|double
name|rightExtensionAmt
decl_stmt|;
if|if
condition|(
name|longitude
operator|<
name|currentLeftLongitude
condition|)
block|{
name|leftExtensionAmt
operator|=
name|currentLeftLongitude
operator|-
name|longitude
expr_stmt|;
block|}
else|else
block|{
name|leftExtensionAmt
operator|=
name|currentLeftLongitude
operator|+
literal|2.0
operator|*
name|Math
operator|.
name|PI
operator|-
name|longitude
expr_stmt|;
block|}
if|if
condition|(
name|longitude
operator|>
name|currentRightLongitude
condition|)
block|{
name|rightExtensionAmt
operator|=
name|longitude
operator|-
name|currentRightLongitude
expr_stmt|;
block|}
else|else
block|{
name|rightExtensionAmt
operator|=
name|longitude
operator|+
literal|2.0
operator|*
name|Math
operator|.
name|PI
operator|-
name|currentRightLongitude
expr_stmt|;
block|}
if|if
condition|(
name|leftExtensionAmt
operator|<
name|rightExtensionAmt
condition|)
block|{
name|currentLeftLongitude
operator|=
name|leftLongitude
operator|-
name|leftExtensionAmt
expr_stmt|;
while|while
condition|(
name|currentLeftLongitude
operator|<=
operator|-
name|Math
operator|.
name|PI
condition|)
block|{
name|currentLeftLongitude
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
block|}
name|leftLongitude
operator|=
name|currentLeftLongitude
expr_stmt|;
block|}
else|else
block|{
name|currentRightLongitude
operator|=
name|rightLongitude
operator|+
name|rightExtensionAmt
expr_stmt|;
while|while
condition|(
name|currentRightLongitude
operator|>
name|Math
operator|.
name|PI
condition|)
block|{
name|currentRightLongitude
operator|-=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
block|}
name|rightLongitude
operator|=
name|currentRightLongitude
expr_stmt|;
block|}
block|}
block|}
name|double
name|testRightLongitude
init|=
name|rightLongitude
decl_stmt|;
if|if
condition|(
name|testRightLongitude
operator|<
name|leftLongitude
condition|)
name|testRightLongitude
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
if|if
condition|(
name|testRightLongitude
operator|-
name|leftLongitude
operator|>=
name|Math
operator|.
name|PI
condition|)
block|{
name|noLongitudeBound
operator|=
literal|true
expr_stmt|;
name|leftLongitude
operator|=
literal|null
expr_stmt|;
name|rightLongitude
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Add a single point.    *@param v is the point vector.    *@return the updated Bounds object.    */
DECL|method|addPoint
specifier|public
name|Bounds
name|addPoint
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|addPoint
argument_list|(
name|v
operator|.
name|x
argument_list|,
name|v
operator|.
name|y
argument_list|,
name|v
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Add a single point.    *@param x is the point x.    *@param y is the point y.    *@param z is the point z.    *@return the updated Bounds object.    */
DECL|method|addPoint
specifier|public
name|Bounds
name|addPoint
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
name|noLongitudeBound
condition|)
block|{
comment|// Get a longitude value
name|double
name|longitude
init|=
name|Math
operator|.
name|atan2
argument_list|(
name|y
argument_list|,
name|x
argument_list|)
decl_stmt|;
comment|//System.err.println(" add longitude bound at "+longitude * 180.0/Math.PI);
name|addLongitudeBound
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|noTopLatitudeBound
operator|||
operator|!
name|noBottomLatitudeBound
condition|)
block|{
comment|// Compute a latitude value
name|double
name|latitude
init|=
name|Math
operator|.
name|asin
argument_list|(
name|z
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|z
operator|*
name|z
operator|+
name|x
operator|*
name|x
operator|+
name|y
operator|*
name|y
argument_list|)
argument_list|)
decl_stmt|;
name|addLatitudeBound
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Add a single point.    *@param latitude is the point's latitude.    *@param longitude is the point's longitude.    *@return the updated Bounds object.    */
DECL|method|addPoint
specifier|public
name|Bounds
name|addPoint
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
if|if
condition|(
operator|!
name|noLongitudeBound
condition|)
block|{
comment|// Get a longitude value
name|addLongitudeBound
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|noTopLatitudeBound
operator|||
operator|!
name|noBottomLatitudeBound
condition|)
block|{
comment|// Compute a latitude value
name|addLatitudeBound
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Signal that there is no longitude bound.    *@return the updated Bounds object.    */
DECL|method|noLongitudeBound
specifier|public
name|Bounds
name|noLongitudeBound
parameter_list|()
block|{
name|noLongitudeBound
operator|=
literal|true
expr_stmt|;
name|leftLongitude
operator|=
literal|null
expr_stmt|;
name|rightLongitude
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Signal that there is no top latitude bound.    *@return the updated Bounds object.    */
DECL|method|noTopLatitudeBound
specifier|public
name|Bounds
name|noTopLatitudeBound
parameter_list|()
block|{
name|noTopLatitudeBound
operator|=
literal|true
expr_stmt|;
name|maxLatitude
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Signal that there is no bottom latitude bound.    *@return the updated Bounds object.    */
DECL|method|noBottomLatitudeBound
specifier|public
name|Bounds
name|noBottomLatitudeBound
parameter_list|()
block|{
name|noBottomLatitudeBound
operator|=
literal|true
expr_stmt|;
name|minLatitude
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

