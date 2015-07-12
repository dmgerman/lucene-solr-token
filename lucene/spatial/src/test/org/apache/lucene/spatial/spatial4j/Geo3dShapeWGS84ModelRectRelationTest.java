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
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|PlanetModel
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

begin_class
DECL|class|Geo3dShapeWGS84ModelRectRelationTest
specifier|public
class|class
name|Geo3dShapeWGS84ModelRectRelationTest
extends|extends
name|Geo3dShapeRectRelationTestCase
block|{
DECL|method|Geo3dShapeWGS84ModelRectRelationTest
specifier|public
name|Geo3dShapeWGS84ModelRectRelationTest
parameter_list|()
block|{
name|super
argument_list|(
name|PlanetModel
operator|.
name|WGS84
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
block|{
specifier|final
name|GeoBBox
name|rect
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|planetModel
argument_list|,
literal|90
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|74
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|40
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|60
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
specifier|final
name|GeoPath
name|path
init|=
operator|new
name|GeoPath
argument_list|(
name|planetModel
argument_list|,
literal|4
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
name|path
operator|.
name|addPoint
argument_list|(
literal|84.4987594274
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|22.8345484402
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
expr_stmt|;
name|path
operator|.
name|done
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|GeoArea
operator|.
name|DISJOINT
operator|==
name|rect
operator|.
name|getRelationship
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// This is what the test failure claimed...
comment|//assertTrue(GeoArea.CONTAINS == rect.getRelationship(path));
comment|//final GeoBBox bbox = getBoundingBox(path);
comment|//assertFalse(GeoArea.DISJOINT == rect.getRelationship(bbox));
block|}
annotation|@
name|Test
DECL|method|testFailure2
specifier|public
name|void
name|testFailure2
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
name|planetModel
argument_list|,
operator|-
literal|74
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|90
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|0
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|26
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
specifier|final
name|GeoCircle
name|circle
init|=
operator|new
name|GeoCircle
argument_list|(
name|planetModel
argument_list|,
operator|-
literal|87.3647352103
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|52.3769709972
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|1
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|GeoArea
operator|.
name|DISJOINT
operator|==
name|rect
operator|.
name|getRelationship
argument_list|(
name|circle
argument_list|)
argument_list|)
expr_stmt|;
comment|// This is what the test failure claimed...
comment|//assertTrue(GeoArea.CONTAINS == rect.getRelationship(circle));
comment|//final GeoBBox bbox = getBoundingBox(circle);
comment|//assertFalse(GeoArea.DISJOINT == rect.getRelationship(bbox));
block|}
annotation|@
name|Test
DECL|method|testFailure3
specifier|public
name|void
name|testFailure3
parameter_list|()
block|{
comment|/*    [junit4]   1> S-R Rel: {}, Shape {}, Rectangle {}    lap# {} [CONTAINS, Geo3dShape{planetmodel=PlanetModel: {ab=1.0011188180710464, c=0.9977622539852008}, shape=GeoPath: {planetmodel=PlanetModel: {ab=1.0011188180710464, c=0.9977622539852008}, width=1.53588974175501(87.99999999999999),      points={[[X=0.12097657665150223, Y=-0.6754177666095532, Z=0.7265376136709238], [X=-0.3837892785614207, Y=0.4258049113530899, Z=0.8180007850434892]]}}},      Rect(minX=4.0,maxX=36.0,minY=16.0,maxY=16.0), 6981](no slf4j subst; sorry)    [junit4] FAILURE 0.59s | Geo3dWGS84ShapeRectRelationTest.testGeoPathRect<<<    [junit4]> Throwable #1: java.lang.AssertionError: Geo3dShape{planetmodel=PlanetModel: {ab=1.0011188180710464, c=0.9977622539852008}, shape=GeoPath: {planetmodel=PlanetModel: {ab=1.0011188180710464, c=0.9977622539852008}, width=1.53588974175501(87.99999999999999),      points={[[X=0.12097657665150223, Y=-0.6754177666095532, Z=0.7265376136709238], [X=-0.3837892785614207, Y=0.4258049113530899, Z=0.8180007850434892]]}}} intersect Pt(x=23.81626064835212,y=16.0)    [junit4]>  at __randomizedtesting.SeedInfo.seed([2595268DA3F13FEA:6CC30D8C83453E5D]:0)    [junit4]>  at org.apache.lucene.spatial.spatial4j.RandomizedShapeTestCase._assertIntersect(RandomizedShapeTestCase.java:168)    [junit4]>  at org.apache.lucene.spatial.spatial4j.RandomizedShapeTestCase.assertRelation(RandomizedShapeTestCase.java:153)    [junit4]>  at org.apache.lucene.spatial.spatial4j.RectIntersectionTestHelper.testRelateWithRectangle(RectIntersectionTestHelper.java:128)    [junit4]>  at org.apache.lucene.spatial.spatial4j.Geo3dWGS84ShapeRectRelationTest.testGeoPathRect(Geo3dWGS84ShapeRectRelationTest.java:265)   */
specifier|final
name|GeoBBox
name|rect
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|planetModel
argument_list|,
literal|16
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|16
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|4
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|36
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|pt
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|16
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|23.81626064835212
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
specifier|final
name|GeoPath
name|path
init|=
operator|new
name|GeoPath
argument_list|(
name|planetModel
argument_list|,
literal|88
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
decl_stmt|;
name|path
operator|.
name|addPoint
argument_list|(
literal|46.6369060853
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
operator|-
literal|79.8452213228
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
expr_stmt|;
name|path
operator|.
name|addPoint
argument_list|(
literal|54.9779334519
operator|*
name|RADIANS_PER_DEGREE
argument_list|,
literal|132.029177424
operator|*
name|RADIANS_PER_DEGREE
argument_list|)
expr_stmt|;
name|path
operator|.
name|done
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"rect="
operator|+
name|rect
argument_list|)
expr_stmt|;
comment|// Rectangle is within path (this is wrong; it's on the other side.  Should be OVERLAPS)
name|assertTrue
argument_list|(
name|GeoArea
operator|.
name|OVERLAPS
operator|==
name|rect
operator|.
name|getRelationship
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// Rectangle contains point
comment|//assertTrue(rect.isWithin(pt));
comment|// Path contains point (THIS FAILS)
comment|//assertTrue(path.isWithin(pt));
comment|// What happens: (1) The center point of the horizontal line is within the path, in fact within a radius of one of the endpoints.
comment|// (2) The point mentioned is NOT inside either SegmentEndpoint.
comment|// (3) The point mentioned is NOT inside the path segment, either.  (I think it should be...)
block|}
block|}
end_class

end_unit

