begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|FacetParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|//Unlike TestSolr4Spatial, not parametrized / not generic.
end_comment

begin_class
DECL|class|TestSolr4Spatial2
specifier|public
class|class
name|TestSolr4Spatial2
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-spatial.xml"
argument_list|,
literal|"schema-spatial.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBBox
specifier|public
name|void
name|testBBox
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"bbox"
else|:
literal|"bboxD_dynamic"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|//nothing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldName
argument_list|,
literal|"ENVELOPE(-10, 20, 15, 10)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|fieldName
argument_list|,
literal|"ENVELOPE(22, 22, 10, 10)"
argument_list|)
argument_list|)
expr_stmt|;
comment|//pt
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=overlapRatio "
operator|+
literal|"queryTargetProportion=0.25}"
operator|+
literal|"Intersects(ENVELOPE(10,25,12,10))"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='2'"
argument_list|,
literal|"/response/docs/[0]/score==0.75]"
argument_list|,
literal|"/response/docs/[1]/id=='1'"
argument_list|,
literal|"/response/docs/[1]/score==0.26666668]"
argument_list|,
literal|"/response/docs/[2]/id=='0'"
argument_list|,
literal|"/response/docs/[2]/score==0.0"
argument_list|,
literal|"/response/docs/[1]/"
operator|+
name|fieldName
operator|+
literal|"=='ENVELOPE(-10, 20, 15, 10)'"
comment|//stored value
argument_list|)
expr_stmt|;
comment|//minSideLength with point query
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=overlapRatio "
operator|+
literal|"queryTargetProportion=0.5 minSideLength=1}"
operator|+
literal|"Intersects(ENVELOPE(0,0,12,12))"
argument_list|,
comment|//pt
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='1'"
argument_list|,
literal|"/response/docs/[0]/score==0.50333333]"
comment|//just over 0.5
argument_list|)
expr_stmt|;
comment|//area2D
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=area2D}"
operator|+
literal|"Intersects(ENVELOPE(0,0,12,12))"
argument_list|,
comment|//pt
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='1'"
argument_list|,
literal|"/response/docs/[0]/score=="
operator|+
operator|(
literal|30f
operator|*
literal|5f
operator|)
operator|+
literal|"]"
comment|//150
argument_list|)
expr_stmt|;
comment|//area (not 2D)
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=area}"
operator|+
literal|"Intersects(ENVELOPE(0,0,12,12))"
argument_list|,
comment|//pt
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='1'"
argument_list|,
literal|"/response/docs/[0]/score=="
operator|+
literal|146.39793f
operator|+
literal|"]"
comment|//a bit less than 150
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadScoreParam
specifier|public
name|void
name|testBadScoreParam
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"bbox"
decl_stmt|;
name|assertQEx
argument_list|(
literal|"expect friendly error message"
argument_list|,
literal|"area2D"
argument_list|,
name|req
argument_list|(
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=bogus}Intersects(ENVELOPE(0,0,12,12))"
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRptWithGeometryField
specifier|public
name|void
name|testRptWithGeometryField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"srptgeom"
decl_stmt|;
comment|//note: fails with "srpt_geohash" because it's not as precise
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|fieldName
argument_list|,
literal|"ENVELOPE(-10, 20, 15, 10)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldName
argument_list|,
literal|"BUFFER(POINT(-10 15), 5)"
argument_list|)
argument_list|)
expr_stmt|;
comment|//circle at top-left corner
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
comment|// one segment.
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Search to the edge but not quite touching the indexed envelope of id=0.  It requires geom validation to
comment|//  eliminate id=0.  id=1 is found and doesn't require validation.  cache=false means no query cache.
specifier|final
name|SolrQueryRequest
name|sameReq
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!cache=false field f="
operator|+
name|fieldName
operator|+
literal|"}Intersects(ENVELOPE(-20, -10.0001, 30, 15.0001))"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
decl_stmt|;
name|assertJQ
argument_list|(
name|sameReq
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
comment|// The tricky thing is verifying the cache works correctly...
name|SolrCache
name|cache
init|=
operator|(
name|SolrCache
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"perSegSpatialFieldCache_srptgeom"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|cache
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"cumulative_inserts"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|cache
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"cumulative_hits"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Repeat the query earlier
name|assertJQ
argument_list|(
name|sameReq
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|cache
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"cumulative_hits"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1 segment"
argument_list|,
literal|1
argument_list|,
name|getSearcher
argument_list|()
operator|.
name|getRawReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get key of first leaf reader -- this one contains the match for sure.
name|Object
name|leafKey1
init|=
name|getFirstLeafReaderKey
argument_list|()
decl_stmt|;
comment|// add new segment
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// sometimes merges (to one seg), sometimes won't
comment|// can still find the same document
name|assertJQ
argument_list|(
name|sameReq
argument_list|,
literal|"/response/numFound==1"
argument_list|,
literal|"/response/docs/[0]/id=='1'"
argument_list|)
expr_stmt|;
comment|// When there are new segments, we accumulate another hit. This tests the cache was not blown away on commit.
comment|// Checking equality for the first reader's cache key indicates wether the cache should still be valid.
name|Object
name|leafKey2
init|=
name|getFirstLeafReaderKey
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|leafKey1
operator|.
name|equals
argument_list|(
name|leafKey2
argument_list|)
condition|?
literal|"2"
else|:
literal|"1"
argument_list|,
name|cache
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"cumulative_hits"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try to see if heatmaps work:
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
name|FacetParams
operator|.
name|FACET_HEATMAP
argument_list|,
name|fieldName
argument_list|,
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|)
argument_list|,
literal|"/facet_counts/facet_heatmaps/"
operator|+
name|fieldName
operator|+
literal|"/minX==-180.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|getSearcher
specifier|protected
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
comment|// neat trick; needn't deal with the hassle RefCounted
return|return
operator|(
name|SolrIndexSearcher
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
return|;
block|}
DECL|method|getFirstLeafReaderKey
specifier|protected
name|Object
name|getFirstLeafReaderKey
parameter_list|()
block|{
return|return
name|getSearcher
argument_list|()
operator|.
name|getRawReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Test
comment|// SOLR-8541
DECL|method|testConstantScoreQueryWithFilterPartOnly
specifier|public
name|void
name|testConstantScoreQueryWithFilterPartOnly
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|doc1
init|=
block|{
literal|"id"
block|,
literal|"1"
block|,
literal|"srptgeom"
block|,
literal|"56.9485,24.0980"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc1
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"{!geofilt sfield=\"srptgeom\" pt=\"56.9484,24.0981\" d=100}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"srptgeom"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

