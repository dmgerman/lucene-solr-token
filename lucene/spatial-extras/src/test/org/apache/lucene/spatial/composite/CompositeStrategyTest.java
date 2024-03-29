begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.composite
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|composite
package|;
end_package

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
name|QuadPrefixTree
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
name|junit
operator|.
name|Test
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
name|context
operator|.
name|SpatialContextFactory
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
name|Point
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
name|Rectangle
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
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|impl
operator|.
name|RectangleImpl
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomBoolean
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomDouble
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomIntBetween
import|;
end_import

begin_class
DECL|class|CompositeStrategyTest
specifier|public
class|class
name|CompositeStrategyTest
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
DECL|method|setupQuadGrid
specifier|private
name|void
name|setupQuadGrid
parameter_list|(
name|int
name|maxLevels
parameter_list|)
block|{
comment|//non-geospatial makes this test a little easier (in gridSnap), and using boundary values 2^X raises
comment|// the prospect of edge conditions we want to test, plus makes for simpler numbers (no decimals).
name|SpatialContextFactory
name|factory
init|=
operator|new
name|SpatialContextFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|geo
operator|=
literal|false
expr_stmt|;
name|factory
operator|.
name|worldBounds
operator|=
operator|new
name|RectangleImpl
argument_list|(
literal|0
argument_list|,
literal|256
argument_list|,
operator|-
literal|128
argument_list|,
literal|128
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|factory
operator|.
name|newSpatialContext
argument_list|()
expr_stmt|;
comment|//A fairly shallow grid
if|if
condition|(
name|maxLevels
operator|==
operator|-
literal|1
condition|)
name|maxLevels
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|8
argument_list|)
expr_stmt|;
comment|//max 64k cells (4^8), also 256*256
name|this
operator|.
name|grid
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
name|this
operator|.
name|rptStrategy
operator|=
name|newRPT
argument_list|()
expr_stmt|;
block|}
DECL|method|setupGeohashGrid
specifier|private
name|void
name|setupGeohashGrid
parameter_list|(
name|int
name|maxLevels
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
comment|//A fairly shallow grid
if|if
condition|(
name|maxLevels
operator|==
operator|-
literal|1
condition|)
name|maxLevels
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|//max 16k cells (32^3)
name|this
operator|.
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
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
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|20
argument_list|)
DECL|method|testOperations
specifier|public
name|void
name|testOperations
parameter_list|()
throws|throws
name|IOException
block|{
comment|//setup
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|setupQuadGrid
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setupGeohashGrid
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
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
comment|//Do it!
for|for
control|(
name|SpatialOperation
name|pred
range|:
name|SpatialOperation
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|pred
operator|==
name|SpatialOperation
operator|.
name|BBoxIntersects
operator|||
name|pred
operator|==
name|SpatialOperation
operator|.
name|BBoxWithin
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|pred
operator|==
name|SpatialOperation
operator|.
name|IsDisjointTo
condition|)
block|{
comment|//TODO
continue|continue;
block|}
name|testOperationRandomShapes
argument_list|(
name|pred
argument_list|)
expr_stmt|;
name|deleteAll
argument_list|()
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|randomShape
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
return|return
name|randomShape
argument_list|()
return|;
block|}
DECL|method|randomShape
specifier|private
name|Shape
name|randomShape
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|randomCircle
argument_list|()
else|:
name|randomRectangle
argument_list|()
return|;
block|}
comment|//TODO move up
DECL|method|randomCircle
specifier|private
name|Shape
name|randomCircle
parameter_list|()
block|{
specifier|final
name|Point
name|point
init|=
name|randomPoint
argument_list|()
decl_stmt|;
comment|//TODO pick using gaussian
name|double
name|radius
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
name|radius
operator|=
name|randomDouble
argument_list|()
operator|*
literal|100
expr_stmt|;
block|}
else|else
block|{
comment|//find distance to closest edge
specifier|final
name|Rectangle
name|worldBounds
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
name|double
name|maxRad
init|=
name|point
operator|.
name|getX
argument_list|()
operator|-
name|worldBounds
operator|.
name|getMinX
argument_list|()
decl_stmt|;
name|maxRad
operator|=
name|Math
operator|.
name|min
argument_list|(
name|maxRad
argument_list|,
name|worldBounds
operator|.
name|getMaxX
argument_list|()
operator|-
name|point
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
name|maxRad
operator|=
name|Math
operator|.
name|min
argument_list|(
name|maxRad
argument_list|,
name|point
operator|.
name|getY
argument_list|()
operator|-
name|worldBounds
operator|.
name|getMinY
argument_list|()
argument_list|)
expr_stmt|;
name|maxRad
operator|=
name|Math
operator|.
name|min
argument_list|(
name|maxRad
argument_list|,
name|worldBounds
operator|.
name|getMaxY
argument_list|()
operator|-
name|point
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
name|radius
operator|=
name|randomDouble
argument_list|()
operator|*
name|maxRad
expr_stmt|;
block|}
return|return
name|ctx
operator|.
name|makeCircle
argument_list|(
name|point
argument_list|,
name|radius
argument_list|)
return|;
block|}
block|}
end_class

end_unit

