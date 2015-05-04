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
name|io
operator|.
name|IOException
import|;
end_import

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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
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
name|Rectangle
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
name|Shape
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
name|composite
operator|.
name|CompositeSpatialStrategy
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
name|prefix
operator|.
name|RandomSpatialOpStrategyTestCase
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
name|prefix
operator|.
name|RecursivePrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|query
operator|.
name|SpatialOperation
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
name|serialized
operator|.
name|SerializedDVStrategy
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
DECL|class|Geo3dRptTest
specifier|public
class|class
name|Geo3dRptTest
extends|extends
name|RandomSpatialOpStrategyTestCase
block|{
DECL|field|grid
specifier|private
name|SpatialPrefixTree
name|grid
decl_stmt|;
DECL|field|rptStrategy
specifier|private
name|RecursivePrefixTreeStrategy
name|rptStrategy
decl_stmt|;
block|{
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
block|}
DECL|method|setupGeohashGrid
specifier|private
name|void
name|setupGeohashGrid
parameter_list|()
block|{
name|this
operator|.
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//A fairly shallow grid
name|this
operator|.
name|rptStrategy
operator|=
name|newRPT
argument_list|()
expr_stmt|;
block|}
DECL|method|newRPT
specifier|protected
name|RecursivePrefixTreeStrategy
name|newRPT
parameter_list|()
block|{
specifier|final
name|RecursivePrefixTreeStrategy
name|rpt
init|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|this
operator|.
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_rpt"
argument_list|)
decl_stmt|;
name|rpt
operator|.
name|setDistErrPct
argument_list|(
literal|0.10
argument_list|)
expr_stmt|;
comment|//not too many cells
return|return
name|rpt
return|;
block|}
annotation|@
name|Override
DECL|method|needsDocValues
specifier|protected
name|boolean
name|needsDocValues
parameter_list|()
block|{
return|return
literal|true
return|;
comment|//due to SerializedDVStrategy
block|}
DECL|method|setupStrategy
specifier|private
name|void
name|setupStrategy
parameter_list|()
block|{
comment|//setup
name|setupGeohashGrid
argument_list|()
expr_stmt|;
name|SerializedDVStrategy
name|serializedDVStrategy
init|=
operator|new
name|SerializedDVStrategy
argument_list|(
name|ctx
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_sdv"
argument_list|)
decl_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|CompositeSpatialStrategy
argument_list|(
literal|"composite_"
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|rptStrategy
argument_list|,
name|serializedDVStrategy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailure1
specifier|public
name|void
name|testFailure1
parameter_list|()
throws|throws
name|IOException
block|{
name|setupStrategy
argument_list|()
expr_stmt|;
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
literal|18
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|27
operator|*
name|DEGREES_TO_RADIANS
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
operator|-
literal|57
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|146
operator|*
name|DEGREES_TO_RADIANS
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
literal|14
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|180
operator|*
name|DEGREES_TO_RADIANS
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
operator|-
literal|15
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|153
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Shape
name|triangle
init|=
operator|new
name|Geo3dShape
argument_list|(
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|points
argument_list|,
literal|0
argument_list|)
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|Rectangle
name|rect
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|49
argument_list|,
operator|-
literal|45
argument_list|,
literal|73
argument_list|,
literal|86
argument_list|)
decl_stmt|;
name|testOperation
argument_list|(
name|rect
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|triangle
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|10
argument_list|)
DECL|method|testOperations
specifier|public
name|void
name|testOperations
parameter_list|()
throws|throws
name|IOException
block|{
name|setupStrategy
argument_list|()
expr_stmt|;
name|testOperationRandomShapes
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|)
expr_stmt|;
block|}
DECL|method|makeTriangle
specifier|private
name|Shape
name|makeTriangle
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|x2
parameter_list|,
name|double
name|y2
parameter_list|,
name|double
name|x3
parameter_list|,
name|double
name|y3
parameter_list|)
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
name|geoPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|y1
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|x1
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|y2
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|x2
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|y3
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|x3
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|convexPointIndex
init|=
literal|0
decl_stmt|;
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
annotation|@
name|Override
DECL|method|randomIndexedShape
specifier|protected
name|Shape
name|randomIndexedShape
parameter_list|()
block|{
return|return
name|randomRectangle
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|randomQueryShape
specifier|protected
name|Shape
name|randomQueryShape
parameter_list|()
block|{
specifier|final
name|int
name|shapeType
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|shapeType
condition|)
block|{
case|case
literal|0
case|:
block|{
comment|// Polygons
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
case|case
literal|1
case|:
block|{
comment|// Circles
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
specifier|final
name|Point
name|point
init|=
name|randomPoint
argument_list|()
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
case|case
literal|2
case|:
block|{
comment|// Rectangles
while|while
condition|(
literal|true
condition|)
block|{
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
try|try
block|{
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
comment|//System.err.println("Trial rectangle shape: "+shape);
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
case|case
literal|3
case|:
block|{
comment|// Paths
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
name|randomPoint
argument_list|()
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
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected shape type"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

