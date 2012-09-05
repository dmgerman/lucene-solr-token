begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|io
operator|.
name|GeohashUtils
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|StoredField
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
name|document
operator|.
name|StringField
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
name|SpatialMatchConcern
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
name|StrategyTestCase
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
name|query
operator|.
name|SpatialArgs
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
name|junit
operator|.
name|Ignore
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_class
DECL|class|TestRecursivePrefixTreeStrategy
specifier|public
class|class
name|TestRecursivePrefixTreeStrategy
extends|extends
name|StrategyTestCase
block|{
DECL|field|maxLength
specifier|private
name|int
name|maxLength
decl_stmt|;
comment|//Tests should call this first.
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|int
name|maxLength
parameter_list|)
block|{
name|this
operator|.
name|maxLength
operator|=
name|maxLength
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
name|GeohashPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilterWithVariableScanLevel
specifier|public
name|void
name|testFilterWithVariableScanLevel
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_WORLD_CITIES_POINTS
argument_list|)
expr_stmt|;
comment|//execute queries for each prefix grid scan level
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|maxLength
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_Cities_IsWithin_BBox
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOneMeterPrecision
specifier|public
name|void
name|testOneMeterPrecision
parameter_list|()
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|GeohashPrefixTree
name|grid
init|=
call|(
name|GeohashPrefixTree
call|)
argument_list|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
argument_list|)
operator|.
name|getGrid
argument_list|()
decl_stmt|;
comment|//DWS: I know this to be true.  11 is needed for one meter
name|double
name|degrees
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
literal|0.001
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrecision
specifier|public
name|void
name|testPrecision
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|Point
name|iPt
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2.8028712999999925
argument_list|,
literal|48.3708044
argument_list|)
decl_stmt|;
comment|//lon, lat
name|addDocument
argument_list|(
name|newDoc
argument_list|(
literal|"iPt"
argument_list|,
name|iPt
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|Point
name|qPt
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2.4632387000000335
argument_list|,
literal|48.6003516
argument_list|)
decl_stmt|;
specifier|final
name|double
name|KM2DEG
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
literal|1
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
decl_stmt|;
specifier|final
name|double
name|DEG2KM
init|=
literal|1
operator|/
name|KM2DEG
decl_stmt|;
specifier|final
name|double
name|DIST
init|=
literal|35.75
decl_stmt|;
comment|//35.7499...
name|assertEquals
argument_list|(
name|DIST
argument_list|,
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|iPt
argument_list|,
name|qPt
argument_list|)
operator|*
name|DEG2KM
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
comment|//distPrec will affect the query shape precision. The indexed precision
comment|// was set to nearly zilch via init(GeohashPrefixTree.getMaxLevelsPossible());
specifier|final
name|double
name|distPrec
init|=
literal|0.025
decl_stmt|;
comment|//the suggested default, by the way
specifier|final
name|double
name|distMult
init|=
literal|1
operator|+
name|distPrec
decl_stmt|;
name|assertTrue
argument_list|(
literal|35.74
operator|*
name|distMult
operator|>=
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|35.74
operator|*
name|KM2DEG
argument_list|,
name|distPrec
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|30
operator|*
name|distMult
operator|<
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|30
operator|*
name|KM2DEG
argument_list|,
name|distPrec
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|33
operator|*
name|distMult
operator|<
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|33
operator|*
name|KM2DEG
argument_list|,
name|distPrec
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|34
operator|*
name|distMult
operator|<
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|34
operator|*
name|KM2DEG
argument_list|,
name|distPrec
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
comment|/* LUCENE-4351 ignore this test until I figure out why it failed (as reported by Jenkins) */
DECL|method|geohashRecursiveRandom
specifier|public
name|void
name|geohashRecursiveRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
literal|12
argument_list|)
expr_stmt|;
comment|//1. Iterate test with the cluster at some worldly point of interest
name|Point
index|[]
name|clusterCenters
init|=
operator|new
name|Point
index|[]
block|{
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
block|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|)
block|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
operator|-
literal|90
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Point
name|clusterCenter
range|:
name|clusterCenters
control|)
block|{
comment|//2. Iterate on size of cluster (a really small one and a large one)
name|String
name|hashCenter
init|=
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|clusterCenter
operator|.
name|getY
argument_list|()
argument_list|,
name|clusterCenter
operator|.
name|getX
argument_list|()
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
comment|//calculate the number of degrees in the smallest grid box size (use for both lat& lon)
name|String
name|smallBox
init|=
name|hashCenter
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hashCenter
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//chop off leaf precision
name|Rectangle
name|clusterDims
init|=
name|GeohashUtils
operator|.
name|decodeBoundary
argument_list|(
name|smallBox
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|double
name|smallRadius
init|=
name|Math
operator|.
name|max
argument_list|(
name|clusterDims
operator|.
name|getMaxX
argument_list|()
operator|-
name|clusterDims
operator|.
name|getMinX
argument_list|()
argument_list|,
name|clusterDims
operator|.
name|getMaxY
argument_list|()
operator|-
name|clusterDims
operator|.
name|getMinY
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|smallRadius
operator|<
literal|1
assert|;
name|double
name|largeRadius
init|=
literal|20d
decl_stmt|;
comment|//good large size; don't use>=45 for this test code to work
name|double
index|[]
name|radiusDegs
init|=
block|{
name|largeRadius
block|,
name|smallRadius
block|}
decl_stmt|;
for|for
control|(
name|double
name|radiusDeg
range|:
name|radiusDegs
control|)
block|{
comment|//3. Index random points in this cluster circle
name|deleteAll
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Point
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<
name|Point
argument_list|>
argument_list|()
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
comment|//Note that this will not result in randomly distributed points in the
comment|// circle, they will be concentrated towards the center a little. But
comment|// it's good enough.
name|Point
name|pt
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|pointOnBearing
argument_list|(
name|clusterCenter
argument_list|,
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
name|radiusDeg
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|*
literal|360
argument_list|,
name|ctx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|pt
operator|=
name|alignGeohash
argument_list|(
name|pt
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
name|pt
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|newDoc
argument_list|(
literal|""
operator|+
name|i
argument_list|,
name|pt
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
comment|//3. Use some query centers. Each is twice the cluster's radius away.
for|for
control|(
name|int
name|ri
init|=
literal|0
init|;
name|ri
operator|<
literal|4
condition|;
name|ri
operator|++
control|)
block|{
name|Point
name|queryCenter
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|pointOnBearing
argument_list|(
name|clusterCenter
argument_list|,
name|radiusDeg
operator|*
literal|2
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|*
literal|360
argument_list|,
name|ctx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|queryCenter
operator|=
name|alignGeohash
argument_list|(
name|queryCenter
argument_list|)
expr_stmt|;
comment|//4.1 Query a small box getting nothing
name|checkHits
argument_list|(
name|q
argument_list|(
name|queryCenter
argument_list|,
name|radiusDeg
operator|*
literal|0.99
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//4.2 Query a large box enclosing the cluster, getting everything
name|checkHits
argument_list|(
name|q
argument_list|(
name|queryCenter
argument_list|,
name|radiusDeg
operator|*
literal|3
operator|*
literal|1.01
argument_list|)
argument_list|,
name|points
operator|.
name|size
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//4.3 Query a medium box getting some (calculate the correct solution and verify)
name|double
name|queryDist
init|=
name|radiusDeg
operator|*
literal|2
decl_stmt|;
comment|//Find matching points.  Put into int[] of doc ids which is the same thing as the index into points list.
name|int
index|[]
name|ids
init|=
operator|new
name|int
index|[
name|points
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|ids_sz
init|=
literal|0
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
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Point
name|point
init|=
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|queryCenter
argument_list|,
name|point
argument_list|)
operator|<=
name|queryDist
condition|)
name|ids
index|[
name|ids_sz
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
name|ids
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|ids
argument_list|,
name|ids_sz
argument_list|)
expr_stmt|;
comment|//assert ids_sz> 0 (can't because randomness keeps us from being able to)
name|checkHits
argument_list|(
name|q
argument_list|(
name|queryCenter
argument_list|,
name|queryDist
argument_list|)
argument_list|,
name|ids
operator|.
name|length
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
comment|//for radiusDeg
block|}
comment|//for clusterCenter
block|}
comment|//randomTest()
comment|/** Query point-distance (in degrees) with zero error percent. */
DECL|method|q
specifier|private
name|SpatialArgs
name|q
parameter_list|(
name|Point
name|pt
parameter_list|,
name|double
name|distDEG
parameter_list|)
block|{
return|return
name|q
argument_list|(
name|pt
argument_list|,
name|distDEG
argument_list|,
literal|0.0
argument_list|)
return|;
block|}
DECL|method|q
specifier|private
name|SpatialArgs
name|q
parameter_list|(
name|Point
name|pt
parameter_list|,
name|double
name|distDEG
parameter_list|,
name|double
name|distPrec
parameter_list|)
block|{
name|Shape
name|shape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|pt
argument_list|,
name|distDEG
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
argument_list|)
decl_stmt|;
name|args
operator|.
name|setDistPrecision
argument_list|(
name|distPrec
argument_list|)
expr_stmt|;
return|return
name|args
return|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
index|[]
name|assertIds
parameter_list|)
block|{
name|SearchResults
name|got
init|=
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|args
argument_list|,
name|assertNumFound
argument_list|,
name|got
operator|.
name|numFound
argument_list|)
expr_stmt|;
if|if
condition|(
name|assertIds
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|gotIds
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SearchResult
name|result
range|:
name|got
operator|.
name|results
control|)
block|{
name|gotIds
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|result
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|assertId
range|:
name|assertIds
control|)
block|{
name|assertTrue
argument_list|(
literal|"has "
operator|+
name|assertId
argument_list|,
name|gotIds
operator|.
name|contains
argument_list|(
name|assertId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|newDoc
specifier|private
name|Document
name|newDoc
parameter_list|(
name|String
name|id
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeShape
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|ctx
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
comment|/** NGeohash round-trip for given precision. */
DECL|method|alignGeohash
specifier|private
name|Point
name|alignGeohash
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
return|return
name|GeohashUtils
operator|.
name|decode
argument_list|(
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|p
operator|.
name|getY
argument_list|()
argument_list|,
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|maxLength
argument_list|)
argument_list|,
name|ctx
argument_list|)
return|;
block|}
block|}
end_class

end_unit

