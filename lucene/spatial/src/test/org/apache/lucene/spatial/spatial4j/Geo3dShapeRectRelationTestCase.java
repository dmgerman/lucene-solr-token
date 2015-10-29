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
name|TestLog
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
name|Circle
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|RectIntersectionTestHelper
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
name|geo3d
operator|.
name|LatLonBounds
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
name|geo3d
operator|.
name|GeoStandardCircle
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
name|geo3d
operator|.
name|GeoShape
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
name|geo3d
operator|.
name|PlanetModel
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
DECL|class|Geo3dShapeRectRelationTestCase
specifier|public
specifier|abstract
class|class
name|Geo3dShapeRectRelationTestCase
extends|extends
name|RandomizedShapeTestCase
block|{
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
DECL|field|planetModel
specifier|protected
specifier|final
name|PlanetModel
name|planetModel
decl_stmt|;
DECL|method|Geo3dShapeRectRelationTestCase
specifier|public
name|Geo3dShapeRectRelationTestCase
parameter_list|(
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|super
argument_list|(
name|SpatialContext
operator|.
name|GEO
argument_list|)
expr_stmt|;
name|this
operator|.
name|planetModel
operator|=
name|planetModel
expr_stmt|;
block|}
DECL|method|getBoundingBox
specifier|protected
name|GeoBBox
name|getBoundingBox
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
name|LatLonBounds
name|bounds
init|=
operator|new
name|LatLonBounds
argument_list|()
decl_stmt|;
name|path
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
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
name|planetModel
argument_list|,
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
DECL|class|Geo3dRectIntersectionTestHelper
specifier|abstract
class|class
name|Geo3dRectIntersectionTestHelper
extends|extends
name|RectIntersectionTestHelper
argument_list|<
name|Geo3dShape
argument_list|>
block|{
DECL|method|Geo3dRectIntersectionTestHelper
specifier|public
name|Geo3dRectIntersectionTestHelper
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
comment|//20 times each -- should be plenty
DECL|method|getContainsMinimum
specifier|protected
name|int
name|getContainsMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
return|return
literal|20
return|;
block|}
DECL|method|getIntersectsMinimum
specifier|protected
name|int
name|getIntersectsMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
return|return
literal|20
return|;
block|}
comment|// producing "within" cases in Geo3D based on our random shapes doesn't happen often. It'd be nice to increase this.
DECL|method|getWithinMinimum
specifier|protected
name|int
name|getWithinMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
return|return
literal|2
return|;
block|}
DECL|method|getDisjointMinimum
specifier|protected
name|int
name|getDisjointMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
return|return
literal|20
return|;
block|}
DECL|method|getBoundingMinimum
specifier|protected
name|int
name|getBoundingMinimum
parameter_list|(
name|int
name|laps
parameter_list|)
block|{
return|return
literal|20
return|;
block|}
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-6867"
argument_list|)
annotation|@
name|Test
DECL|method|testGeoCircleRect
specifier|public
name|void
name|testGeoCircleRect
parameter_list|()
block|{
operator|new
name|Geo3dRectIntersectionTestHelper
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
name|int
name|circleRadius
init|=
literal|180
operator|-
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|180
argument_list|)
decl_stmt|;
comment|//no 0-radius
specifier|final
name|Point
name|point
init|=
name|nearP
decl_stmt|;
specifier|final
name|GeoShape
name|shape
init|=
operator|new
name|GeoStandardCircle
argument_list|(
name|planetModel
argument_list|,
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
name|planetModel
argument_list|,
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
name|GeoPoint
name|geoPoint
init|=
operator|(
operator|(
name|GeoStandardCircle
operator|)
name|shape
operator|.
name|shape
operator|)
operator|.
name|getCenter
argument_list|()
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
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-6867"
argument_list|)
annotation|@
name|Test
DECL|method|testGeoBBoxRect
specifier|public
name|void
name|testGeoBBoxRect
parameter_list|()
block|{
operator|new
name|Geo3dRectIntersectionTestHelper
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
name|planetModel
argument_list|,
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
name|planetModel
argument_list|,
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
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-6867"
argument_list|)
annotation|@
name|Test
DECL|method|testGeoPolygonRect
specifier|public
name|void
name|testGeoPolygonRect
parameter_list|()
block|{
operator|new
name|Geo3dRectIntersectionTestHelper
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
name|Circle
name|pointZone
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|centerPoint
argument_list|,
name|maxDistance
argument_list|)
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
name|randomPointIn
argument_list|(
name|pointZone
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|gPt
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
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
name|planetModel
argument_list|,
name|geoPoints
argument_list|,
name|convexPointIndex
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3dShape
argument_list|(
name|planetModel
argument_list|,
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
comment|// Long/thin so lets just find 1.
return|return
literal|1
return|;
block|}
block|}
operator|.
name|testRelateWithRectangle
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/LUCENE-6867"
argument_list|)
annotation|@
name|Test
DECL|method|testGeoPathRect
specifier|public
name|void
name|testGeoPathRect
parameter_list|()
block|{
operator|new
name|Geo3dRectIntersectionTestHelper
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
name|Circle
name|pointZone
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|centerPoint
argument_list|,
name|maxDistance
argument_list|)
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
name|planetModel
argument_list|,
name|width
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pointCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Point
name|nextPoint
init|=
name|randomPointIn
argument_list|(
name|pointZone
argument_list|)
decl_stmt|;
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
name|planetModel
argument_list|,
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
comment|// Long/thin so lets just find 1.
return|return
literal|1
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
name|getLongitude
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
argument_list|,
name|geoPoint
operator|.
name|getLongitude
argument_list|()
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

