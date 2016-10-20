begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextLatitude
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextLongitude
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoTestUtil
operator|.
name|nextPolygon
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
name|index
operator|.
name|PointValues
operator|.
name|Relation
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/** Test Polygon2D impl */
end_comment

begin_class
DECL|class|TestPolygon2D
specifier|public
class|class
name|TestPolygon2D
extends|extends
name|LuceneTestCase
block|{
comment|/** Three boxes, an island inside a hole inside a shape */
DECL|method|testMultiPolygon
specifier|public
name|void
name|testMultiPolygon
parameter_list|()
block|{
name|Polygon
name|hole
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|10
block|,
operator|-
literal|10
block|,
literal|10
block|,
literal|10
block|,
operator|-
literal|10
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|10
block|,
literal|10
block|,
literal|10
block|,
operator|-
literal|10
block|,
operator|-
literal|10
block|}
argument_list|)
decl_stmt|;
name|Polygon
name|outer
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|50
block|,
operator|-
literal|50
block|,
literal|50
block|,
literal|50
block|,
operator|-
literal|50
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|50
block|,
literal|50
block|,
literal|50
block|,
operator|-
literal|50
block|,
operator|-
literal|50
block|}
argument_list|,
name|hole
argument_list|)
decl_stmt|;
name|Polygon
name|island
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|5
block|,
operator|-
literal|5
block|,
literal|5
block|,
literal|5
block|,
operator|-
literal|5
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|5
block|,
literal|5
block|,
literal|5
block|,
operator|-
literal|5
block|,
operator|-
literal|5
block|}
argument_list|)
decl_stmt|;
name|Polygon2D
name|polygon
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|outer
argument_list|,
name|island
argument_list|)
decl_stmt|;
comment|// contains(point)
name|assertTrue
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
operator|-
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the island
name|assertFalse
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
operator|-
literal|6
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the hole
name|assertTrue
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
operator|-
literal|25
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the mainland
name|assertFalse
argument_list|(
name|polygon
operator|.
name|contains
argument_list|(
operator|-
literal|51
argument_list|,
literal|51
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the ocean
comment|// relate(box): this can conservatively return CELL_CROSSES_QUERY
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_INSIDE_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
operator|-
literal|2
argument_list|,
literal|2
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the island
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
literal|6
argument_list|,
literal|7
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the hole
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_INSIDE_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
literal|24
argument_list|,
literal|25
argument_list|,
literal|24
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
comment|// on the mainland
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
literal|51
argument_list|,
literal|52
argument_list|,
literal|51
argument_list|,
literal|52
argument_list|)
argument_list|)
expr_stmt|;
comment|// in the ocean
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_CROSSES_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
operator|-
literal|60
argument_list|,
literal|60
argument_list|,
operator|-
literal|60
argument_list|,
literal|60
argument_list|)
argument_list|)
expr_stmt|;
comment|// enclosing us completely
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_CROSSES_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
literal|49
argument_list|,
literal|51
argument_list|,
literal|49
argument_list|,
literal|51
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the mainland
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_CROSSES_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
literal|9
argument_list|,
literal|11
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the hole
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_CROSSES_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// overlapping the island
block|}
DECL|method|testPacMan
specifier|public
name|void
name|testPacMan
parameter_list|()
throws|throws
name|Exception
block|{
comment|// pacman
name|double
index|[]
name|px
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|10
block|,
literal|0
block|,
operator|-
literal|8
block|,
operator|-
literal|10
block|,
operator|-
literal|8
block|,
literal|0
block|,
literal|10
block|,
literal|10
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|py
init|=
block|{
literal|0
block|,
literal|5
block|,
literal|9
block|,
literal|10
block|,
literal|9
block|,
literal|0
block|,
operator|-
literal|9
block|,
operator|-
literal|10
block|,
operator|-
literal|9
block|,
operator|-
literal|5
block|,
literal|0
block|}
decl_stmt|;
comment|// candidate crosses cell
name|double
name|xMin
init|=
literal|2
decl_stmt|;
comment|//-5;
name|double
name|xMax
init|=
literal|11
decl_stmt|;
comment|//0.000001;
name|double
name|yMin
init|=
operator|-
literal|1
decl_stmt|;
comment|//0;
name|double
name|yMax
init|=
literal|1
decl_stmt|;
comment|//5;
comment|// test cell crossing poly
name|Polygon2D
name|polygon
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
operator|new
name|Polygon
argument_list|(
name|py
argument_list|,
name|px
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Relation
operator|.
name|CELL_CROSSES_QUERY
argument_list|,
name|polygon
operator|.
name|relate
argument_list|(
name|yMin
argument_list|,
name|yMax
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoundingBox
specifier|public
name|void
name|testBoundingBox
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon2D
name|polygon
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|nextPolygon
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|double
name|latitude
init|=
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|longitude
init|=
name|nextLongitude
argument_list|()
decl_stmt|;
comment|// if the point is within poly, then it should be in our bounding box
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|latitude
operator|>=
name|polygon
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|polygon
operator|.
name|maxLat
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|longitude
operator|>=
name|polygon
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|polygon
operator|.
name|maxLon
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// targets the bounding box directly
DECL|method|testBoundingBoxEdgeCases
specifier|public
name|void
name|testBoundingBoxEdgeCases
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|Polygon2D
name|impl
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// if the point is within poly, then it should be in our bounding box
if|if
condition|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|latitude
operator|>=
name|polygon
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|polygon
operator|.
name|maxLat
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|longitude
operator|>=
name|polygon
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|polygon
operator|.
name|maxLon
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** If polygon.contains(box) returns true, then any point in that box should return true as well */
DECL|method|testContainsRandom
specifier|public
name|void
name|testContainsRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|50
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|Polygon2D
name|impl
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextBoxNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
comment|// allowed to conservatively return false
if|if
condition|(
name|impl
operator|.
name|relate
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
name|Relation
operator|.
name|CELL_INSIDE_QUERY
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|500
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|rectangle
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertTrue
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertTrue
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** If polygon.contains(box) returns true, then any point in that box should return true as well */
comment|// different from testContainsRandom in that its not a purely random test. we iterate the vertices of the polygon
comment|// and generate boxes near each one of those to try to be more efficient.
DECL|method|testContainsEdgeCases
specifier|public
name|void
name|testContainsEdgeCases
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|Polygon2D
name|impl
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextBoxNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
comment|// allowed to conservatively return false
if|if
condition|(
name|impl
operator|.
name|relate
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
name|Relation
operator|.
name|CELL_INSIDE_QUERY
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|rectangle
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertTrue
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|20
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertTrue
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** If polygon.intersects(box) returns false, then any point in that box should return false as well */
DECL|method|testIntersectRandom
specifier|public
name|void
name|testIntersectRandom
parameter_list|()
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|Polygon2D
name|impl
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextBoxNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
comment|// allowed to conservatively return true.
if|if
condition|(
name|impl
operator|.
name|relate
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|1000
condition|;
name|k
operator|++
control|)
block|{
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|rectangle
argument_list|)
decl_stmt|;
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertFalse
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertFalse
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** If polygon.intersects(box) returns false, then any point in that box should return false as well */
comment|// different from testIntersectsRandom in that its not a purely random test. we iterate the vertices of the polygon
comment|// and generate boxes near each one of those to try to be more efficient.
DECL|method|testIntersectEdgeCases
specifier|public
name|void
name|testIntersectEdgeCases
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
name|Polygon2D
name|impl
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|Rectangle
name|rectangle
init|=
name|GeoTestUtil
operator|.
name|nextBoxNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
comment|// allowed to conservatively return false.
if|if
condition|(
name|impl
operator|.
name|relate
argument_list|(
name|rectangle
operator|.
name|minLat
argument_list|,
name|rectangle
operator|.
name|maxLat
argument_list|,
name|rectangle
operator|.
name|minLon
argument_list|,
name|rectangle
operator|.
name|maxLon
argument_list|)
operator|==
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|rectangle
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertFalse
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|50
condition|;
name|k
operator|++
control|)
block|{
comment|// this tests in our range but sometimes outside! so we have to double-check its really in other box
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
comment|// check for sure its in our box
if|if
condition|(
name|latitude
operator|>=
name|rectangle
operator|.
name|minLat
operator|&&
name|latitude
operator|<=
name|rectangle
operator|.
name|maxLat
operator|&&
name|longitude
operator|>=
name|rectangle
operator|.
name|minLon
operator|&&
name|longitude
operator|<=
name|rectangle
operator|.
name|maxLon
condition|)
block|{
name|assertFalse
argument_list|(
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** Tests edge case behavior with respect to insideness */
DECL|method|testEdgeInsideness
specifier|public
name|void
name|testEdgeInsideness
parameter_list|()
block|{
name|Polygon2D
name|poly
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|2
block|,
operator|-
literal|2
block|,
literal|2
block|,
literal|2
block|,
operator|-
literal|2
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|2
block|,
literal|2
block|,
literal|2
block|,
operator|-
literal|2
block|,
operator|-
literal|2
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|2
argument_list|,
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// bottom left corner: true
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// bottom right corner: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|2
argument_list|,
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// top left corner: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// top right corner: false
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|2
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// bottom side: true
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// bottom side: true
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// bottom side: true
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|2
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// top side: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// top side: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// top side: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// right side: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// right side: false
name|assertFalse
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// right side: false
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// left side: true
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|0
argument_list|,
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// left side: true
name|assertTrue
argument_list|(
name|poly
operator|.
name|contains
argument_list|(
literal|1
argument_list|,
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// left side: true
block|}
comment|/** Tests current impl against original algorithm */
DECL|method|testContainsAgainstOriginal
specifier|public
name|void
name|testContainsAgainstOriginal
parameter_list|()
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|nextPolygon
argument_list|()
decl_stmt|;
comment|// currently we don't generate these, but this test does not want holes.
while|while
condition|(
name|polygon
operator|.
name|getHoles
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|polygon
operator|=
name|nextPolygon
argument_list|()
expr_stmt|;
block|}
name|Polygon2D
name|impl
init|=
name|Polygon2D
operator|.
name|create
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
comment|// random lat/lons against polygon
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|double
name|point
index|[]
init|=
name|GeoTestUtil
operator|.
name|nextPointNear
argument_list|(
name|polygon
argument_list|)
decl_stmt|;
name|double
name|latitude
init|=
name|point
index|[
literal|0
index|]
decl_stmt|;
name|double
name|longitude
init|=
name|point
index|[
literal|1
index|]
decl_stmt|;
name|boolean
name|expected
init|=
name|GeoTestUtil
operator|.
name|containsSlowly
argument_list|(
name|polygon
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|impl
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

