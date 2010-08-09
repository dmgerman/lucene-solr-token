begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package

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
name|tier
operator|.
name|InvalidGeoException
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|DistanceUtilsTest
specifier|public
class|class
name|DistanceUtilsTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBoxCorner
specifier|public
name|void
name|testBoxCorner
parameter_list|()
throws|throws
name|Exception
block|{
name|double
index|[]
name|zero
init|=
operator|new
name|double
index|[]
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|zeroOne
init|=
operator|new
name|double
index|[]
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|double
index|[]
name|oneOne
init|=
operator|new
name|double
index|[]
block|{
literal|1
block|,
literal|1
block|}
decl_stmt|;
name|double
index|[]
name|pt1
init|=
operator|new
name|double
index|[]
block|{
literal|1.5
block|,
literal|110.3
block|}
decl_stmt|;
name|double
index|[]
name|result
init|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|zero
argument_list|,
literal|null
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|zero
argument_list|,
literal|null
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1.0
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1.0
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|oneOne
argument_list|,
literal|null
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|zeroOne
argument_list|,
literal|null
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|pt1
argument_list|,
literal|null
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.5
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|111.3
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|pt1
argument_list|,
literal|null
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.5
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|109.3
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testNormLatLon
specifier|public
name|void
name|testNormLatLon
parameter_list|()
throws|throws
name|Exception
block|{    }
DECL|method|testLatLonCorner
specifier|public
name|void
name|testLatLonCorner
parameter_list|()
throws|throws
name|Exception
block|{
name|double
index|[]
name|zero
init|=
operator|new
name|double
index|[]
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|zero45
init|=
operator|new
name|double
index|[]
block|{
literal|0
block|,
name|DistanceUtils
operator|.
name|DEG_45
block|}
decl_stmt|;
name|double
index|[]
name|result
decl_stmt|;
comment|// 	00Â°38â²09â³N, 000Â°38â²09â³E
comment|//Verify at http://www.movable-type.co.uk/scripts/latlong.html
name|result
operator|=
name|DistanceUtils
operator|.
name|latLonCorner
argument_list|(
name|zero
index|[
literal|0
index|]
argument_list|,
name|zero
index|[
literal|1
index|]
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.63583
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.63583
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|latLonCornerDegs
argument_list|(
name|zero
index|[
literal|0
index|]
argument_list|,
name|zero
index|[
literal|1
index|]
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
comment|// 	00Â°38â²09â³N, 000Â°38â²09â³E
name|assertEquals
argument_list|(
literal|0.63583
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.63583
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|latLonCornerDegs
argument_list|(
name|zero
index|[
literal|0
index|]
argument_list|,
name|zero
index|[
literal|1
index|]
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
comment|// 	00Â°38â²09â³N, 000Â°38â²09â³E
name|assertEquals
argument_list|(
operator|-
literal|0.63583
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.63583
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
comment|//test some edge cases
comment|//89Â°16â²02â³N, 060Â°12â²35â³E
name|result
operator|=
name|DistanceUtils
operator|.
name|latLonCornerDegs
argument_list|(
literal|89.0
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|89.26722
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60.20972
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|result
operator|=
name|DistanceUtils
operator|.
name|latLonCornerDegs
argument_list|(
literal|0
argument_list|,
operator|-
literal|179.0
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.63583
argument_list|,
name|result
index|[
literal|0
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|178.36417
argument_list|,
name|result
index|[
literal|1
index|]
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
block|}
DECL|method|testVectorDistance
specifier|public
name|void
name|testVectorDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|double
index|[]
name|zero
init|=
operator|new
name|double
index|[]
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|zeroOne
init|=
operator|new
name|double
index|[]
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|double
index|[]
name|oneZero
init|=
operator|new
name|double
index|[]
block|{
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|oneOne
init|=
operator|new
name|double
index|[]
block|{
literal|1
block|,
literal|1
block|}
decl_stmt|;
name|double
name|distance
decl_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|vectorDistance
argument_list|(
name|zero
argument_list|,
name|zeroOne
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|distance
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|vectorDistance
argument_list|(
name|zero
argument_list|,
name|oneZero
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|distance
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|vectorDistance
argument_list|(
name|zero
argument_list|,
name|oneOne
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
literal|2
argument_list|)
argument_list|,
name|distance
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|squaredEuclideanDistance
argument_list|(
name|zero
argument_list|,
name|oneOne
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|distance
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
block|}
DECL|method|testHaversine
specifier|public
name|void
name|testHaversine
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|distance
decl_stmt|;
comment|//compare to http://www.movable-type.co.uk/scripts/latlong.html
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|4.0
argument_list|,
name|Math
operator|.
name|PI
operator|/
literal|4.0
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6672.0
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|20
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|20
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3112
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|1
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|157.2
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
comment|//Try some around stuff
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|1
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|222.4
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
literal|89
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|89
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|179
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|222.4
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
literal|89
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|49
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|179
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4670
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|distance
operator|=
name|DistanceUtils
operator|.
name|haversine
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
operator|-
literal|179
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Math
operator|.
name|toRadians
argument_list|(
literal|179
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|222.4
argument_list|,
name|distance
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
block|}
DECL|method|testParse
specifier|public
name|void
name|testParse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|parse
decl_stmt|;
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
literal|"89.0,73.2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|parse
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"89.0"
argument_list|,
name|parse
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"73.2"
argument_list|,
name|parse
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
literal|"89.0,73.2,-92.3"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parse
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"89.0"
argument_list|,
name|parse
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"73.2"
argument_list|,
name|parse
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-92.3"
argument_list|,
name|parse
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
literal|"    89.0         ,   73.2  ,              -92.3   "
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parse
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"89.0"
argument_list|,
name|parse
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"73.2"
argument_list|,
name|parse
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-92.3"
argument_list|,
name|parse
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|String
index|[]
name|foo
init|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
name|parse
argument_list|,
literal|"89.0         ,   73.2 ,              -92.3"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|//should be same piece of memory
name|assertTrue
argument_list|(
name|foo
operator|==
name|parse
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parse
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"89.0"
argument_list|,
name|parse
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"73.2"
argument_list|,
name|parse
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-92.3"
argument_list|,
name|parse
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|//array should get automatically resized
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
operator|new
name|String
index|[
literal|1
index|]
argument_list|,
literal|"89.0         ,   73.2 ,              -92.3"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parse
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"89.0"
argument_list|,
name|parse
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"73.2"
argument_list|,
name|parse
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-92.3"
argument_list|,
name|parse
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
try|try
block|{
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
literal|"89.0         ,   "
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
block|{     }
try|try
block|{
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
literal|" , 89.0          "
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
block|{     }
try|try
block|{
name|parse
operator|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
block|{     }
name|double
index|[]
name|dbls
init|=
name|DistanceUtils
operator|.
name|parsePointDouble
argument_list|(
literal|null
argument_list|,
literal|"89.0         ,   73.2 ,              -92.3"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dbls
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|89.0
argument_list|,
name|dbls
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|73.2
argument_list|,
name|dbls
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|92.3
argument_list|,
name|dbls
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
try|try
block|{
name|dbls
operator|=
name|DistanceUtils
operator|.
name|parsePointDouble
argument_list|(
literal|null
argument_list|,
literal|"89.0         ,   foo ,              -92.3"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{     }
name|dbls
operator|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
literal|"89.0         ,   73.2    "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dbls
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|89.0
argument_list|,
name|dbls
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|73.2
argument_list|,
name|dbls
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|//test some bad lat/long pairs
try|try
block|{
name|dbls
operator|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
literal|"189.0         ,   73.2    "
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
block|{      }
try|try
block|{
name|dbls
operator|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
literal|"89.0         ,   273.2    "
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
block|{      }
block|}
block|}
end_class

end_unit

