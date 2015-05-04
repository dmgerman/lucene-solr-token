begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|distance
operator|.
name|DistanceUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
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
operator|.
name|Bounds
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoArea
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoBBox
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoBBoxFactory
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoCircle
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoPath
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoPoint
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoPolygonFactory
import|;
end_import

begin_import
import|import
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
operator|.
name|GeoShape
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|distance
operator|.
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
import|;
end_import

begin_class
DECL|class|Geo3dShapeRectRelationTest
specifier|public
class|class
name|Geo3dShapeRectRelationTest
extends|extends
name|RandomizedShapeTest
block|{
annotation|@
name|Rule
DECL|field|testLog
specifier|public
specifier|final
name|TestLog
name|testLog
init|=
name|TestLog
operator|.
name|instance
decl_stmt|;
DECL|method|random
specifier|static
name|Random
name|random
parameter_list|()
block|{
return|return
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRandom
argument_list|()
return|;
block|}
block|{
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
block|}
DECL|field|RADIANS_PER_DEGREE
specifier|protected
specifier|final
specifier|static
name|double
name|RADIANS_PER_DEGREE
init|=
name|Math
operator|.
name|PI
operator|/
literal|180.0
decl_stmt|;
annotation|@
name|Test
DECL|method|testFailure1
specifier|public
name|void
name|testFailure1
parameter_list|()
block|{
specifier|final
name|GeoBBox
name|rect
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
literal|88
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|30
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|30
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|62
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|66.2465299717
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|29.1786158537
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|43.684447915
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|46.2210986329
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|30.4579218227
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|14.5238410082
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoShape
name|path
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|points
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
literal|34.2730264413182
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|82.75500168892472
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
comment|// Apparently the rectangle thinks the polygon is completely within it... "shape inside rectangle"
name|assertTrue
argument_list|(
name|GeoArea
operator|.
name|WITHIN
operator|==
name|rect
operator|.
name|getRelationship
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// Point is within path? Apparently not...
name|assertFalse
argument_list|(
name|path
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
comment|// If it is within the path, it must be within the rectangle, and similarly visa versa
name|assertFalse
argument_list|(
name|rect
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getBoundingBox
specifier|protected
specifier|static
name|GeoBBox
name|getBoundingBox
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
name|Bounds
name|bounds
init|=
name|path
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|double
name|leftLon
decl_stmt|;
name|double
name|rightLon
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|checkNoLongitudeBound
argument_list|()
condition|)
block|{
name|leftLon
operator|=
operator|-
name|Math
operator|.
name|PI
expr_stmt|;
name|rightLon
operator|=
name|Math
operator|.
name|PI
expr_stmt|;
block|}
else|else
block|{
name|leftLon
operator|=
name|bounds
operator|.
name|getLeftLongitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|rightLon
operator|=
name|bounds
operator|.
name|getRightLongitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|double
name|minLat
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
condition|)
block|{
name|minLat
operator|=
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
expr_stmt|;
block|}
else|else
block|{
name|minLat
operator|=
name|bounds
operator|.
name|getMinLatitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|double
name|maxLat
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|checkNoTopLatitudeBound
argument_list|()
condition|)
block|{
name|maxLat
operator|=
name|Math
operator|.
name|PI
operator|*
literal|0.5
expr_stmt|;
block|}
else|else
block|{
name|maxLat
operator|=
name|bounds
operator|.
name|getMaxLatitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|maxLat
argument_list|,
name|minLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testGeoCircleRect
specifier|public
name|void
name|testGeoCircleRect
parameter_list|()
block|{
operator|new
name|RectIntersectionTestHelper
argument_list|<
name|Geo3dShape
argument_list|>
argument_list|(
name|ctx
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Geo3dShape
name|generateRandomShape
parameter_list|(
name|Point
name|nearP
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|circleRadius
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|179
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|//no 0-radius
specifier|final
name|Point
name|point
init|=
name|nearP
decl_stmt|;
try|try
block|{
specifier|final
name|GeoShape
name|shape
init|=
operator|new
name|GeoCircle
argument_list|(
name|point
operator|.
name|getY
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|point
operator|.
name|getX
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|circleRadius
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3dShape
argument_list|(
name|shape
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// This is what happens when we create a shape that is invalid.  Although it is conceivable that there are cases where
comment|// the exception is thrown incorrectly, we aren't going to be able to do that in this random test.
continue|continue;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Point
name|randomPointInEmptyShape
parameter_list|(
name|Geo3dShape
name|shape
parameter_list|)
block|{
name|GeoPoint
name|geoPoint
init|=
operator|(
operator|(
name|GeoCircle
operator|)
name|shape
operator|.
name|shape
operator|)
operator|.
name|center
decl_stmt|;
return|return
name|geoPointToSpatial4jPoint
argument_list|(
name|geoPoint
argument_list|)
return|;
block|}
block|}
operator|.
name|testRelateWithRectangle
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeoBBoxRect
specifier|public
name|void
name|testGeoBBoxRect
parameter_list|()
block|{
operator|new
name|RectIntersectionTestHelper
argument_list|<
name|Geo3dShape
argument_list|>
argument_list|(
name|ctx
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|isRandomShapeRectangular
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Geo3dShape
name|generateRandomShape
parameter_list|(
name|Point
name|nearP
parameter_list|)
block|{
comment|// (ignoring nearP)
name|Point
name|ulhcPoint
init|=
name|randomPoint
argument_list|()
decl_stmt|;
name|Point
name|lrhcPoint
init|=
name|randomPoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|ulhcPoint
operator|.
name|getY
argument_list|()
operator|<
name|lrhcPoint
operator|.
name|getY
argument_list|()
condition|)
block|{
comment|//swap
name|Point
name|temp
init|=
name|ulhcPoint
decl_stmt|;
name|ulhcPoint
operator|=
name|lrhcPoint
expr_stmt|;
name|lrhcPoint
operator|=
name|temp
expr_stmt|;
block|}
specifier|final
name|GeoShape
name|shape
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|ulhcPoint
operator|.
name|getY
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|lrhcPoint
operator|.
name|getY
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|ulhcPoint
operator|.
name|getX
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|lrhcPoint
operator|.
name|getX
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3dShape
argument_list|(
name|shape
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Point
name|randomPointInEmptyShape
parameter_list|(
name|Geo3dShape
name|shape
parameter_list|)
block|{
return|return
name|shape
operator|.
name|getBoundingBox
argument_list|()
operator|.
name|getCenter
argument_list|()
return|;
block|}
block|}
operator|.
name|testRelateWithRectangle
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeoPolygonRect
specifier|public
name|void
name|testGeoPolygonRect
parameter_list|()
block|{
operator|new
name|RectIntersectionTestHelper
argument_list|<
name|Geo3dShape
argument_list|>
argument_list|(
name|ctx
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Geo3dShape
name|generateRandomShape
parameter_list|(
name|Point
name|nearP
parameter_list|)
block|{
specifier|final
name|Point
name|centerPoint
init|=
name|randomPoint
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDistance
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|160
argument_list|)
operator|+
literal|20
decl_stmt|;
specifier|final
name|int
name|vertexCount
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|3
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|geoPoints
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|geoPoints
operator|.
name|size
argument_list|()
operator|<
name|vertexCount
condition|)
block|{
specifier|final
name|Point
name|point
init|=
name|randomPoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|point
argument_list|,
name|centerPoint
argument_list|)
operator|>
name|maxDistance
condition|)
continue|continue;
specifier|final
name|GeoPoint
name|gPt
init|=
operator|new
name|GeoPoint
argument_list|(
name|point
operator|.
name|getY
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|point
operator|.
name|getX
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
decl_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
name|gPt
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|convexPointIndex
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|vertexCount
argument_list|)
decl_stmt|;
comment|//If we get this wrong, hopefully we get IllegalArgumentException
try|try
block|{
specifier|final
name|GeoShape
name|shape
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|geoPoints
argument_list|,
name|convexPointIndex
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3dShape
argument_list|(
name|shape
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// This is what happens when we create a shape that is invalid.  Although it is conceivable that there are cases where
comment|// the exception is thrown incorrectly, we aren't going to be able to do that in this random test.
continue|continue;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Point
name|randomPointInEmptyShape
parameter_list|(
name|Geo3dShape
name|shape
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unexpected; need to finish test code"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|getWithinMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
comment|// Long/thin so only 10% of the usual figure
return|return
name|laps
operator|/
literal|10000
return|;
block|}
block|}
operator|.
name|testRelateWithRectangle
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeoPathRect
specifier|public
name|void
name|testGeoPathRect
parameter_list|()
block|{
operator|new
name|RectIntersectionTestHelper
argument_list|<
name|Geo3dShape
argument_list|>
argument_list|(
name|ctx
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Geo3dShape
name|generateRandomShape
parameter_list|(
name|Point
name|nearP
parameter_list|)
block|{
specifier|final
name|Point
name|centerPoint
init|=
name|randomPoint
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDistance
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|160
argument_list|)
operator|+
literal|20
decl_stmt|;
specifier|final
name|int
name|pointCount
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|+
literal|1
decl_stmt|;
specifier|final
name|double
name|width
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|89
argument_list|)
operator|+
literal|1
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
specifier|final
name|GeoPath
name|path
init|=
operator|new
name|GeoPath
argument_list|(
name|width
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|pointCount
condition|)
block|{
specifier|final
name|Point
name|nextPoint
init|=
name|randomPoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|nextPoint
argument_list|,
name|centerPoint
argument_list|)
operator|>
name|maxDistance
condition|)
continue|continue;
name|path
operator|.
name|addPoint
argument_list|(
name|nextPoint
operator|.
name|getY
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|nextPoint
operator|.
name|getX
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|path
operator|.
name|done
argument_list|()
expr_stmt|;
return|return
operator|new
name|Geo3dShape
argument_list|(
name|path
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// This is what happens when we create a shape that is invalid.  Although it is conceivable that there are cases where
comment|// the exception is thrown incorrectly, we aren't going to be able to do that in this random test.
continue|continue;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Point
name|randomPointInEmptyShape
parameter_list|(
name|Geo3dShape
name|shape
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unexpected; need to finish test code"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|getWithinMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
comment|// Long/thin so only 10% of the usual figure
return|return
name|laps
operator|/
literal|10000
return|;
block|}
block|}
operator|.
name|testRelateWithRectangle
argument_list|()
expr_stmt|;
block|}
DECL|method|geoPointToSpatial4jPoint
specifier|private
name|Point
name|geoPointToSpatial4jPoint
parameter_list|(
name|GeoPoint
name|geoPoint
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|makePoint
argument_list|(
name|geoPoint
operator|.
name|x
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
argument_list|,
name|geoPoint
operator|.
name|y
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
argument_list|)
return|;
block|}
block|}
end_class

end_unit

