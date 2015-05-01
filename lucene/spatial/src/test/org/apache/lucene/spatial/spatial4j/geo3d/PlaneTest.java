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

begin_comment
comment|/** Test basic plane functionality. */
end_comment

begin_class
DECL|class|PlaneTest
specifier|public
class|class
name|PlaneTest
block|{
annotation|@
name|Test
DECL|method|testIdenticalPlanes
specifier|public
name|void
name|testIdenticalPlanes
parameter_list|()
block|{
specifier|final
name|GeoPoint
name|p
init|=
operator|new
name|GeoPoint
argument_list|(
literal|0.123
argument_list|,
operator|-
literal|0.456
argument_list|)
decl_stmt|;
specifier|final
name|Plane
name|plane1
init|=
operator|new
name|Plane
argument_list|(
name|p
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|Plane
name|plane2
init|=
operator|new
name|Plane
argument_list|(
name|p
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|plane1
operator|.
name|isNumericallyIdentical
argument_list|(
name|plane2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Plane
name|plane3
init|=
operator|new
name|Plane
argument_list|(
name|p
argument_list|,
literal|0.1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|plane1
operator|.
name|isNumericallyIdentical
argument_list|(
name|plane3
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Vector
name|v1
init|=
operator|new
name|Vector
argument_list|(
literal|0.1
argument_list|,
operator|-
literal|0.732
argument_list|,
literal|0.9
argument_list|)
decl_stmt|;
specifier|final
name|double
name|constant
init|=
literal|0.432
decl_stmt|;
specifier|final
name|Vector
name|v2
init|=
operator|new
name|Vector
argument_list|(
name|v1
operator|.
name|x
operator|*
name|constant
argument_list|,
name|v1
operator|.
name|y
operator|*
name|constant
argument_list|,
name|v1
operator|.
name|z
operator|*
name|constant
argument_list|)
decl_stmt|;
specifier|final
name|Plane
name|p1
init|=
operator|new
name|Plane
argument_list|(
name|v1
argument_list|,
literal|0.2
argument_list|)
decl_stmt|;
specifier|final
name|Plane
name|p2
init|=
operator|new
name|Plane
argument_list|(
name|v2
argument_list|,
literal|0.2
operator|*
name|constant
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p1
operator|.
name|isNumericallyIdentical
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInterpolation
specifier|public
name|void
name|testInterpolation
parameter_list|()
block|{
comment|// [X=0.35168818443386646, Y=-0.19637966197066342, Z=0.9152870857244183],
comment|// [X=0.5003343189532654, Y=0.522128543226148, Z=0.6906861469771293],
specifier|final
name|GeoPoint
name|start
init|=
operator|new
name|GeoPoint
argument_list|(
literal|0.35168818443386646
argument_list|,
operator|-
literal|0.19637966197066342
argument_list|,
literal|0.9152870857244183
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|end
init|=
operator|new
name|GeoPoint
argument_list|(
literal|0.5003343189532654
argument_list|,
literal|0.522128543226148
argument_list|,
literal|0.6906861469771293
argument_list|)
decl_stmt|;
comment|// [A=-0.6135342247741855, B=0.21504338363863665, C=0.28188192383666794, D=0.0, side=-1.0] internal? false;
specifier|final
name|Plane
name|p
init|=
operator|new
name|Plane
argument_list|(
operator|-
literal|0.6135342247741855
argument_list|,
literal|0.21504338363863665
argument_list|,
literal|0.28188192383666794
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|points
init|=
name|p
operator|.
name|interpolate
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
operator|new
name|double
index|[]
block|{
literal|0.25
block|,
literal|0.50
block|,
literal|0.75
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|GeoPoint
name|point
range|:
name|points
control|)
block|{
name|assertTrue
argument_list|(
name|p
operator|.
name|evaluateIsZero
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

