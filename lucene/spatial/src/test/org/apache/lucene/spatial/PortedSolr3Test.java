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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Name
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
name|ParametersFactory
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
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContext
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
name|index
operator|.
name|IndexableField
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
name|search
operator|.
name|FilteredQuery
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|search
operator|.
name|Query
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
name|TermQueryPrefixTreeStrategy
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

begin_comment
comment|/**  * Based off of Solr 3's SpatialFilterTest.  */
end_comment

begin_class
DECL|class|PortedSolr3Test
specifier|public
class|class
name|PortedSolr3Test
extends|extends
name|StrategyTestCase
block|{
annotation|@
name|ParametersFactory
DECL|method|parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|ctorArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SimpleSpatialContext
operator|.
name|GEO_KM
decl_stmt|;
name|SpatialPrefixTree
name|grid
decl_stmt|;
name|SpatialStrategy
name|strategy
decl_stmt|;
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"recursive_geohash"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|,
literal|"recursive_geohash"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|grid
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"recursive_quad"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|,
literal|"recursive_quad"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"termquery_geohash"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|,
literal|"termquery_geohash"
argument_list|)
block|}
argument_list|)
expr_stmt|;
return|return
name|ctorArgs
return|;
block|}
comment|// this is a hack for clover!
DECL|class|Param
specifier|static
class|class
name|Param
block|{
DECL|field|strategy
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|field|description
name|String
name|description
decl_stmt|;
DECL|method|Param
name|Param
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
comment|//  private String fieldName;
DECL|method|PortedSolr3Test
specifier|public
name|PortedSolr3Test
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"strategy"
argument_list|)
name|Param
name|param
parameter_list|)
block|{
name|SpatialStrategy
name|strategy
init|=
name|param
operator|.
name|strategy
decl_stmt|;
name|this
operator|.
name|ctx
operator|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
DECL|method|setupDocs
specifier|private
name|void
name|setupDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|adoc
argument_list|(
literal|"1"
argument_list|,
literal|"32.7693246, -79.9289094"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"2"
argument_list|,
literal|"33.7693246, -80.9289094"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"3"
argument_list|,
literal|"-32.7693246, 50.9289094"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"4"
argument_list|,
literal|"-50.7693246, 60.9289094"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"5"
argument_list|,
literal|"0,0"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"6"
argument_list|,
literal|"0.1,0.1"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"7"
argument_list|,
literal|"-0.1,-0.1"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"8"
argument_list|,
literal|"0,179.9"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"9"
argument_list|,
literal|"0,-179.9"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"10"
argument_list|,
literal|"89.9,50"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"11"
argument_list|,
literal|"89.9,-130"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"12"
argument_list|,
literal|"-89.9,50"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"13"
argument_list|,
literal|"-89.9,-130"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntersections
specifier|public
name|void
name|testIntersections
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDocs
argument_list|()
expr_stmt|;
comment|//Try some edge cases
name|checkHitsCircle
argument_list|(
literal|"1,1"
argument_list|,
literal|175
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
literal|"0,179.8"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
literal|"89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
expr_stmt|;
comment|//this goes over the north pole
name|checkHitsCircle
argument_list|(
literal|"-89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|12
argument_list|,
literal|13
argument_list|)
expr_stmt|;
comment|//this goes over the south pole
comment|//try some normal cases
name|checkHitsCircle
argument_list|(
literal|"33.0,-80.0"
argument_list|,
literal|300
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//large distance
name|checkHitsCircle
argument_list|(
literal|"1,1"
argument_list|,
literal|5000
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|//Because we are generating a box based on the west/east longitudes and the south/north latitudes, which then
comment|//translates to a range query, which is slightly more inclusive.  Thus, even though 0.0 is 15.725 kms away,
comment|//it will be included, b/c of the box calculation.
name|checkHitsBBox
argument_list|(
literal|"0.1,0.1"
argument_list|,
literal|15
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|//try some more
name|deleteAll
argument_list|()
expr_stmt|;
name|adoc
argument_list|(
literal|"14"
argument_list|,
literal|"0,5"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"15"
argument_list|,
literal|"0,15"
argument_list|)
expr_stmt|;
comment|//3000KM from 0,0, see http://www.movable-type.co.uk/scripts/latlong.html
name|adoc
argument_list|(
literal|"16"
argument_list|,
literal|"18.71111,19.79750"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"17"
argument_list|,
literal|"44.043900,-95.436643"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|checkHitsCircle
argument_list|(
literal|"0,0"
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
literal|"0,0"
argument_list|,
literal|2000
argument_list|,
literal|2
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|checkHitsBBox
argument_list|(
literal|"0,0"
argument_list|,
literal|3000
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
literal|"0,0"
argument_list|,
literal|3001
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
literal|"0,0"
argument_list|,
literal|3000.1
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
comment|//really fine grained distance and reflects some of the vagaries of how we are calculating the box
name|checkHitsCircle
argument_list|(
literal|"43.517030,-96.789603"
argument_list|,
literal|109
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// falls outside of the real distance, but inside the bounding box
name|checkHitsCircle
argument_list|(
literal|"43.517030,-96.789603"
argument_list|,
literal|110
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkHitsBBox
argument_list|(
literal|"43.517030,-96.789603"
argument_list|,
literal|110
argument_list|,
literal|1
argument_list|,
literal|17
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test is similar to a Solr 3 spatial test.    */
annotation|@
name|Test
DECL|method|testDistanceOrder
specifier|public
name|void
name|testDistanceOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|adoc
argument_list|(
literal|"100"
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"101"
argument_list|,
literal|"4,-1"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|//query closer to #100
name|checkHitsOrdered
argument_list|(
literal|"Intersects(Circle(3,4 d=1000))"
argument_list|,
literal|"101"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|//query closer to #101
name|checkHitsOrdered
argument_list|(
literal|"Intersects(Circle(4,0 d=1000))"
argument_list|,
literal|"100"
argument_list|,
literal|"101"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHitsOrdered
specifier|private
name|void
name|checkHitsOrdered
parameter_list|(
name|String
name|spatialQ
parameter_list|,
name|String
modifier|...
name|ids
parameter_list|)
block|{
name|SpatialArgs
name|args
init|=
name|this
operator|.
name|argsParser
operator|.
name|parse
argument_list|(
name|spatialQ
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|SearchResults
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|String
index|[]
name|resultIds
init|=
operator|new
name|String
index|[
name|results
operator|.
name|numFound
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SearchResult
name|result
range|:
name|results
operator|.
name|results
control|)
block|{
name|resultIds
index|[
name|i
operator|++
index|]
operator|=
name|result
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
literal|"order matters"
argument_list|,
name|ids
argument_list|,
name|resultIds
argument_list|)
expr_stmt|;
block|}
comment|//---- these are similar to Solr test methods
DECL|method|adoc
specifier|private
name|void
name|adoc
parameter_list|(
name|String
name|idStr
parameter_list|,
name|String
name|shapeStr
parameter_list|)
throws|throws
name|IOException
block|{
name|Shape
name|shape
init|=
name|ctx
operator|.
name|readShape
argument_list|(
name|shapeStr
argument_list|)
decl_stmt|;
name|addDocument
argument_list|(
name|newDoc
argument_list|(
name|idStr
argument_list|,
name|shape
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
DECL|method|checkHitsCircle
specifier|private
name|void
name|checkHitsCircle
parameter_list|(
name|String
name|ptStr
parameter_list|,
name|double
name|dist
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
modifier|...
name|assertIds
parameter_list|)
block|{
name|_checkHits
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|ptStr
argument_list|,
name|dist
argument_list|,
name|assertNumFound
argument_list|,
name|assertIds
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHitsBBox
specifier|private
name|void
name|checkHitsBBox
parameter_list|(
name|String
name|ptStr
parameter_list|,
name|double
name|dist
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
modifier|...
name|assertIds
parameter_list|)
block|{
name|_checkHits
argument_list|(
name|SpatialOperation
operator|.
name|BBoxIntersects
argument_list|,
name|ptStr
argument_list|,
name|dist
argument_list|,
name|assertNumFound
argument_list|,
name|assertIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|_checkHits
specifier|private
name|void
name|_checkHits
parameter_list|(
name|SpatialOperation
name|op
parameter_list|,
name|String
name|ptStr
parameter_list|,
name|double
name|dist
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
modifier|...
name|assertIds
parameter_list|)
block|{
name|Point
name|pt
init|=
operator|(
name|Point
operator|)
name|ctx
operator|.
name|readShape
argument_list|(
name|ptStr
argument_list|)
decl_stmt|;
name|Shape
name|shape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|pt
argument_list|,
name|dist
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
argument_list|)
decl_stmt|;
comment|//args.setDistPrecision(0.025);
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|query
operator|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|strategy
operator|.
name|makeFilter
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SearchResults
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|shape
argument_list|,
name|assertNumFound
argument_list|,
name|results
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
name|resultIds
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
name|results
operator|.
name|results
control|)
block|{
name|resultIds
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
name|resultIds
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
block|}
end_class

end_unit

