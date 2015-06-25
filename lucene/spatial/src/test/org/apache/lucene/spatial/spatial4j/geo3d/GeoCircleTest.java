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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|GeoCircleTest
specifier|public
class|class
name|GeoCircleTest
block|{
annotation|@
name|Test
DECL|method|testCircleDistance
specifier|public
name|void
name|testCircleDistance
parameter_list|()
block|{
name|GeoCircle
name|c
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|c
operator|.
name|computeArcDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|c
operator|.
name|computeLinearDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|c
operator|.
name|computeNormalDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|c
operator|.
name|computeArcDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|c
operator|.
name|computeLinearDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|c
operator|.
name|computeNormalDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.05
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.05
argument_list|,
name|c
operator|.
name|computeArcDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.049995
argument_list|,
name|c
operator|.
name|computeLinearDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.049979
argument_list|,
name|c
operator|.
name|computeNormalDistance
argument_list|(
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCircleFullWorld
specifier|public
name|void
name|testCircleFullWorld
parameter_list|()
block|{
name|GeoCircle
name|c
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.55
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.45
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|Bounds
name|b
init|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCirclePointWithin
specifier|public
name|void
name|testCirclePointWithin
parameter_list|()
block|{
name|GeoCircle
name|c
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.55
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.45
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCircleBounds
specifier|public
name|void
name|testCircleBounds
parameter_list|()
block|{
name|GeoCircle
name|c
decl_stmt|;
name|Bounds
name|b
decl_stmt|;
comment|// Vertical circle cases
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.6
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.4
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.4
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.6
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
comment|// Horizontal circle cases
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
comment|// Now do a somewhat tilted plane, facing different directions.
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
literal|0.0
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
name|Math
operator|.
name|PI
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
comment|// Slightly tilted, PI/4 direction.
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|0.01
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.09
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.11
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|0.01
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.09
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.11
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
comment|// Now do a somewhat tilted plane.
name|c
operator|=
operator|new
name|GeoCircle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.01
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.11
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.09
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.6
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.4
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

