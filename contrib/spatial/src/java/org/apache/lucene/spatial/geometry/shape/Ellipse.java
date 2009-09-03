begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.geometry.shape
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geometry
operator|.
name|shape
package|;
end_package

begin_comment
comment|/**  * Ellipse shape. From C++ gl.  *  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_class
DECL|class|Ellipse
specifier|public
class|class
name|Ellipse
implements|implements
name|Geometry2D
block|{
DECL|field|center
specifier|private
name|Point2D
name|center
decl_stmt|;
comment|/**    * Half length of major axis    */
DECL|field|a
specifier|private
name|double
name|a
decl_stmt|;
comment|/**    * Half length of minor axis    */
DECL|field|b
specifier|private
name|double
name|b
decl_stmt|;
DECL|field|k1
DECL|field|k2
DECL|field|k3
specifier|private
name|double
name|k1
decl_stmt|,
name|k2
decl_stmt|,
name|k3
decl_stmt|;
comment|/**    * sin of rotation angle    */
DECL|field|s
specifier|private
name|double
name|s
decl_stmt|;
comment|/**    * cos of rotation angle    */
DECL|field|c
specifier|private
name|double
name|c
decl_stmt|;
DECL|method|Ellipse
specifier|public
name|Ellipse
parameter_list|()
block|{
name|center
operator|=
operator|new
name|Point2D
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|SQR
specifier|private
name|double
name|SQR
parameter_list|(
name|double
name|d
parameter_list|)
block|{
return|return
name|d
operator|*
name|d
return|;
block|}
comment|/**    * Constructor given bounding rectangle and a rotation.    */
DECL|method|Ellipse
specifier|public
name|Ellipse
parameter_list|(
name|Point2D
name|p1
parameter_list|,
name|Point2D
name|p2
parameter_list|,
name|double
name|angle
parameter_list|)
block|{
name|center
operator|=
operator|new
name|Point2D
argument_list|()
expr_stmt|;
comment|// Set the center
name|center
operator|.
name|x
argument_list|(
operator|(
name|p1
operator|.
name|x
argument_list|()
operator|+
name|p2
operator|.
name|x
argument_list|()
operator|)
operator|*
literal|0.5f
argument_list|)
expr_stmt|;
name|center
operator|.
name|y
argument_list|(
operator|(
name|p1
operator|.
name|y
argument_list|()
operator|+
name|p2
operator|.
name|y
argument_list|()
operator|)
operator|*
literal|0.5f
argument_list|)
expr_stmt|;
comment|// Find sin and cos of the angle
name|double
name|angleRad
init|=
name|Math
operator|.
name|toRadians
argument_list|(
name|angle
argument_list|)
decl_stmt|;
name|c
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|angleRad
argument_list|)
expr_stmt|;
name|s
operator|=
name|Math
operator|.
name|sin
argument_list|(
name|angleRad
argument_list|)
expr_stmt|;
comment|// Find the half lengths of the semi-major and semi-minor axes
name|double
name|dx
init|=
name|Math
operator|.
name|abs
argument_list|(
name|p2
operator|.
name|x
argument_list|()
operator|-
name|p1
operator|.
name|x
argument_list|()
argument_list|)
operator|*
literal|0.5
decl_stmt|;
name|double
name|dy
init|=
name|Math
operator|.
name|abs
argument_list|(
name|p2
operator|.
name|y
argument_list|()
operator|-
name|p1
operator|.
name|y
argument_list|()
argument_list|)
operator|*
literal|0.5
decl_stmt|;
if|if
condition|(
name|dx
operator|>=
name|dy
condition|)
block|{
name|a
operator|=
name|dx
expr_stmt|;
name|b
operator|=
name|dy
expr_stmt|;
block|}
else|else
block|{
name|a
operator|=
name|dy
expr_stmt|;
name|b
operator|=
name|dx
expr_stmt|;
block|}
comment|// Find k1, k2, k3 - define when a point x,y is on the ellipse
name|k1
operator|=
name|SQR
argument_list|(
name|c
operator|/
name|a
argument_list|)
operator|+
name|SQR
argument_list|(
name|s
operator|/
name|b
argument_list|)
expr_stmt|;
name|k2
operator|=
literal|2
operator|*
name|s
operator|*
name|c
operator|*
operator|(
operator|(
literal|1
operator|/
name|SQR
argument_list|(
name|a
argument_list|)
operator|)
operator|-
operator|(
literal|1
operator|/
name|SQR
argument_list|(
name|b
argument_list|)
operator|)
operator|)
expr_stmt|;
name|k3
operator|=
name|SQR
argument_list|(
name|s
operator|/
name|a
argument_list|)
operator|+
name|SQR
argument_list|(
name|c
operator|/
name|b
argument_list|)
expr_stmt|;
block|}
comment|/**    * Determines if a line segment intersects the ellipse and if so finds the    * point(s) of intersection.    *     * @param seg    *            Line segment to test for intersection    * @param pt0    *            OUT - intersection point (if it exists)    * @param pt1    *            OUT - second intersection point (if it exists)    *     * @return Returns the number of intersection points (0, 1, or 2).    */
DECL|method|intersect
specifier|public
name|int
name|intersect
parameter_list|(
name|LineSegment
name|seg
parameter_list|,
name|Point2D
name|pt0
parameter_list|,
name|Point2D
name|pt1
parameter_list|)
block|{
if|if
condition|(
name|pt0
operator|==
literal|null
condition|)
name|pt0
operator|=
operator|new
name|Point2D
argument_list|()
expr_stmt|;
if|if
condition|(
name|pt1
operator|==
literal|null
condition|)
name|pt1
operator|=
operator|new
name|Point2D
argument_list|()
expr_stmt|;
comment|// Solution is found by parameterizing the line segment and
comment|// substituting those values into the ellipse equation.
comment|// Results in a quadratic equation.
name|double
name|x1
init|=
name|center
operator|.
name|x
argument_list|()
decl_stmt|;
name|double
name|y1
init|=
name|center
operator|.
name|y
argument_list|()
decl_stmt|;
name|double
name|u1
init|=
name|seg
operator|.
name|A
operator|.
name|x
argument_list|()
decl_stmt|;
name|double
name|v1
init|=
name|seg
operator|.
name|A
operator|.
name|y
argument_list|()
decl_stmt|;
name|double
name|u2
init|=
name|seg
operator|.
name|B
operator|.
name|x
argument_list|()
decl_stmt|;
name|double
name|v2
init|=
name|seg
operator|.
name|B
operator|.
name|y
argument_list|()
decl_stmt|;
name|double
name|dx
init|=
name|u2
operator|-
name|u1
decl_stmt|;
name|double
name|dy
init|=
name|v2
operator|-
name|v1
decl_stmt|;
name|double
name|q0
init|=
name|k1
operator|*
name|SQR
argument_list|(
name|u1
operator|-
name|x1
argument_list|)
operator|+
name|k2
operator|*
operator|(
name|u1
operator|-
name|x1
operator|)
operator|*
operator|(
name|v1
operator|-
name|y1
operator|)
operator|+
name|k3
operator|*
name|SQR
argument_list|(
name|v1
operator|-
name|y1
argument_list|)
operator|-
literal|1
decl_stmt|;
name|double
name|q1
init|=
operator|(
literal|2
operator|*
name|k1
operator|*
name|dx
operator|*
operator|(
name|u1
operator|-
name|x1
operator|)
operator|)
operator|+
operator|(
name|k2
operator|*
name|dx
operator|*
operator|(
name|v1
operator|-
name|y1
operator|)
operator|)
operator|+
operator|(
name|k2
operator|*
name|dy
operator|*
operator|(
name|u1
operator|-
name|x1
operator|)
operator|)
operator|+
operator|(
literal|2
operator|*
name|k3
operator|*
name|dy
operator|*
operator|(
name|v1
operator|-
name|y1
operator|)
operator|)
decl_stmt|;
name|double
name|q2
init|=
operator|(
name|k1
operator|*
name|SQR
argument_list|(
name|dx
argument_list|)
operator|)
operator|+
operator|(
name|k2
operator|*
name|dx
operator|*
name|dy
operator|)
operator|+
operator|(
name|k3
operator|*
name|SQR
argument_list|(
name|dy
argument_list|)
operator|)
decl_stmt|;
comment|// Compare q1^2 to 4*q0*q2 to see how quadratic solves
name|double
name|d
init|=
name|SQR
argument_list|(
name|q1
argument_list|)
operator|-
operator|(
literal|4
operator|*
name|q0
operator|*
name|q2
operator|)
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
comment|// Roots are complex valued. Line containing the segment does
comment|// not intersect the ellipse
return|return
literal|0
return|;
block|}
if|if
condition|(
name|d
operator|==
literal|0
condition|)
block|{
comment|// One real-valued root - line is tangent to the ellipse
name|double
name|t
init|=
operator|-
name|q1
operator|/
operator|(
literal|2
operator|*
name|q2
operator|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
name|t
operator|&&
name|t
operator|<=
literal|1
condition|)
block|{
comment|// Intersection occurs along line segment
name|pt0
operator|.
name|x
argument_list|(
name|u1
operator|+
name|t
operator|*
name|dx
argument_list|)
expr_stmt|;
name|pt0
operator|.
name|y
argument_list|(
name|v1
operator|+
name|t
operator|*
name|dy
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
else|else
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// Two distinct real-valued roots. Solve for the roots and see if
comment|// they fall along the line segment
name|int
name|n
init|=
literal|0
decl_stmt|;
name|double
name|q
init|=
name|Math
operator|.
name|sqrt
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|double
name|t
init|=
operator|(
operator|-
name|q1
operator|-
name|q
operator|)
operator|/
operator|(
literal|2
operator|*
name|q2
operator|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
name|t
operator|&&
name|t
operator|<=
literal|1
condition|)
block|{
comment|// Intersection occurs along line segment
name|pt0
operator|.
name|x
argument_list|(
name|u1
operator|+
name|t
operator|*
name|dx
argument_list|)
expr_stmt|;
name|pt0
operator|.
name|y
argument_list|(
name|v1
operator|+
name|t
operator|*
name|dy
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
comment|// 2nd root
name|t
operator|=
operator|(
operator|-
name|q1
operator|+
name|q
operator|)
operator|/
operator|(
literal|2
operator|*
name|q2
operator|)
expr_stmt|;
if|if
condition|(
literal|0
operator|<=
name|t
operator|&&
name|t
operator|<=
literal|1
condition|)
block|{
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|pt0
operator|.
name|x
argument_list|(
name|u1
operator|+
name|t
operator|*
name|dx
argument_list|)
expr_stmt|;
name|pt0
operator|.
name|y
argument_list|(
name|v1
operator|+
name|t
operator|*
name|dy
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
else|else
block|{
name|pt1
operator|.
name|x
argument_list|(
name|u1
operator|+
name|t
operator|*
name|dx
argument_list|)
expr_stmt|;
name|pt1
operator|.
name|y
argument_list|(
name|v1
operator|+
name|t
operator|*
name|dy
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
block|}
DECL|method|intersect
specifier|public
name|IntersectCase
name|intersect
parameter_list|(
name|Rectangle
name|r
parameter_list|)
block|{
comment|// Test if all 4 corners of the rectangle are inside the ellipse
name|Point2D
name|ul
init|=
operator|new
name|Point2D
argument_list|(
name|r
operator|.
name|MinPt
argument_list|()
operator|.
name|x
argument_list|()
argument_list|,
name|r
operator|.
name|MaxPt
argument_list|()
operator|.
name|y
argument_list|()
argument_list|)
decl_stmt|;
name|Point2D
name|ur
init|=
operator|new
name|Point2D
argument_list|(
name|r
operator|.
name|MaxPt
argument_list|()
operator|.
name|x
argument_list|()
argument_list|,
name|r
operator|.
name|MaxPt
argument_list|()
operator|.
name|y
argument_list|()
argument_list|)
decl_stmt|;
name|Point2D
name|ll
init|=
operator|new
name|Point2D
argument_list|(
name|r
operator|.
name|MinPt
argument_list|()
operator|.
name|x
argument_list|()
argument_list|,
name|r
operator|.
name|MinPt
argument_list|()
operator|.
name|y
argument_list|()
argument_list|)
decl_stmt|;
name|Point2D
name|lr
init|=
operator|new
name|Point2D
argument_list|(
name|r
operator|.
name|MaxPt
argument_list|()
operator|.
name|x
argument_list|()
argument_list|,
name|r
operator|.
name|MinPt
argument_list|()
operator|.
name|y
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|ul
argument_list|)
operator|&&
name|contains
argument_list|(
name|ur
argument_list|)
operator|&&
name|contains
argument_list|(
name|ll
argument_list|)
operator|&&
name|contains
argument_list|(
name|lr
argument_list|)
condition|)
return|return
name|IntersectCase
operator|.
name|CONTAINS
return|;
comment|// Test if any of the rectangle edges intersect
name|Point2D
name|pt0
init|=
operator|new
name|Point2D
argument_list|()
decl_stmt|,
name|pt1
init|=
operator|new
name|Point2D
argument_list|()
decl_stmt|;
name|LineSegment
name|bottom
init|=
operator|new
name|LineSegment
argument_list|(
name|ll
argument_list|,
name|lr
argument_list|)
decl_stmt|;
if|if
condition|(
name|intersect
argument_list|(
name|bottom
argument_list|,
name|pt0
argument_list|,
name|pt1
argument_list|)
operator|>
literal|0
condition|)
return|return
name|IntersectCase
operator|.
name|INTERSECTS
return|;
name|LineSegment
name|top
init|=
operator|new
name|LineSegment
argument_list|(
name|ul
argument_list|,
name|ur
argument_list|)
decl_stmt|;
if|if
condition|(
name|intersect
argument_list|(
name|top
argument_list|,
name|pt0
argument_list|,
name|pt1
argument_list|)
operator|>
literal|0
condition|)
return|return
name|IntersectCase
operator|.
name|INTERSECTS
return|;
name|LineSegment
name|left
init|=
operator|new
name|LineSegment
argument_list|(
name|ll
argument_list|,
name|ul
argument_list|)
decl_stmt|;
if|if
condition|(
name|intersect
argument_list|(
name|left
argument_list|,
name|pt0
argument_list|,
name|pt1
argument_list|)
operator|>
literal|0
condition|)
return|return
name|IntersectCase
operator|.
name|INTERSECTS
return|;
name|LineSegment
name|right
init|=
operator|new
name|LineSegment
argument_list|(
name|lr
argument_list|,
name|ur
argument_list|)
decl_stmt|;
if|if
condition|(
name|intersect
argument_list|(
name|right
argument_list|,
name|pt0
argument_list|,
name|pt1
argument_list|)
operator|>
literal|0
condition|)
return|return
name|IntersectCase
operator|.
name|INTERSECTS
return|;
comment|// Ellipse does not intersect any edge : since the case for the ellipse
comment|// containing the rectangle was considered above then if the center
comment|// is inside the ellipse is fully inside and if center is outside
comment|// the ellipse is fully outside
return|return
operator|(
name|r
operator|.
name|contains
argument_list|(
name|center
argument_list|)
operator|)
condition|?
name|IntersectCase
operator|.
name|WITHIN
else|:
name|IntersectCase
operator|.
name|OUTSIDE
return|;
block|}
DECL|method|area
specifier|public
name|double
name|area
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|centroid
specifier|public
name|Point2D
name|centroid
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Point2D
name|pt
parameter_list|)
block|{
comment|// Plug in equation for ellipse, If evaluates to<= 0 then the
comment|// point is in or on the ellipse.
name|double
name|dx
init|=
name|pt
operator|.
name|x
argument_list|()
operator|-
name|center
operator|.
name|x
argument_list|()
decl_stmt|;
name|double
name|dy
init|=
name|pt
operator|.
name|y
argument_list|()
operator|-
name|center
operator|.
name|y
argument_list|()
decl_stmt|;
name|double
name|eq
init|=
operator|(
operator|(
operator|(
name|k1
operator|*
name|SQR
argument_list|(
name|dx
argument_list|)
operator|)
operator|+
operator|(
name|k2
operator|*
name|dx
operator|*
name|dy
operator|)
operator|+
operator|(
name|k3
operator|*
name|SQR
argument_list|(
name|dy
argument_list|)
operator|)
operator|-
literal|1
operator|)
operator|)
decl_stmt|;
return|return
name|eq
operator|<=
literal|0
return|;
block|}
DECL|method|translate
specifier|public
name|void
name|translate
parameter_list|(
name|Vector2D
name|v
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

