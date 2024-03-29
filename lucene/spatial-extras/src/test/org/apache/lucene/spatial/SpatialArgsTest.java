begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
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
name|query
operator|.
name|SpatialArgs
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
DECL|class|SpatialArgsTest
specifier|public
class|class
name|SpatialArgsTest
block|{
annotation|@
name|Test
DECL|method|calcDistanceFromErrPct
specifier|public
name|void
name|calcDistanceFromErrPct
parameter_list|()
block|{
specifier|final
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
specifier|final
name|double
name|DEP
init|=
literal|0.5
decl_stmt|;
comment|//distErrPct
comment|//the result is the diagonal distance from the center to the closest corner,
comment|// times distErrPct
name|Shape
name|superwide
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
literal|180
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|//0 distErrPct means 0 distance always
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|superwide
argument_list|,
literal|0
argument_list|,
name|ctx
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|180
operator|*
name|DEP
argument_list|,
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|superwide
argument_list|,
name|DEP
argument_list|,
name|ctx
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Shape
name|supertall
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|90
argument_list|,
literal|90
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|90
operator|*
name|DEP
argument_list|,
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|supertall
argument_list|,
name|DEP
argument_list|,
name|ctx
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Shape
name|upperhalf
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
literal|180
argument_list|,
literal|0
argument_list|,
literal|90
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|45
operator|*
name|DEP
argument_list|,
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|upperhalf
argument_list|,
name|DEP
argument_list|,
name|ctx
argument_list|)
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|Shape
name|midCircle
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|45
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|60
operator|*
name|DEP
argument_list|,
name|SpatialArgs
operator|.
name|calcDistanceFromErrPct
argument_list|(
name|midCircle
argument_list|,
name|DEP
argument_list|,
name|ctx
argument_list|)
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

