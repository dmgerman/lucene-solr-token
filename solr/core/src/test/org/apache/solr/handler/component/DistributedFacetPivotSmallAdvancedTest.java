begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|solr
operator|.
name|BaseDistributedSearchTestCase
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|FieldStatsInfo
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|PivotField
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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

begin_comment
comment|/**  * tests some edge cases of pivot faceting with stats  *  * NOTE: This test ignores the control collection (in single node mode, there is no   * need for the overrequesting, all the data is local -- so comparisons with it wouldn't   * be valid in some cases we are testing here)  */
end_comment

begin_class
DECL|class|DistributedFacetPivotSmallAdvancedTest
specifier|public
class|class
name|DistributedFacetPivotSmallAdvancedTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedFacetPivotSmallAdvancedTest
specifier|public
name|DistributedFacetPivotSmallAdvancedTest
parameter_list|()
block|{
name|this
operator|.
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|shardCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
specifier|final
name|SolrServer
name|shard0
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SolrServer
name|shard1
init|=
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// NOTE: we use the literal (4 character) string "null" as a company name
comment|// to help ensure there isn't any bugs where the literal string is treated as if it
comment|// were a true NULL value.
comment|// shard0
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|19
argument_list|,
literal|"place_t"
argument_list|,
literal|"cardiff dublin"
argument_list|,
literal|"company_t"
argument_list|,
literal|"microsoft polecat"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"15"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|20
argument_list|,
literal|"place_t"
argument_list|,
literal|"dublin"
argument_list|,
literal|"company_t"
argument_list|,
literal|"polecat microsoft null"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"19"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb"
argument_list|,
literal|"foo_i"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|21
argument_list|,
literal|"place_t"
argument_list|,
literal|"london la dublin"
argument_list|,
literal|"company_t"
argument_list|,
literal|"microsoft fujitsu null polecat"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"29"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb"
argument_list|,
literal|"foo_i"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|22
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow london cardiff"
argument_list|,
literal|"company_t"
argument_list|,
literal|"polecat null bbc"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"39"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb"
argument_list|,
literal|"foo_i"
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|23
argument_list|,
literal|"place_t"
argument_list|,
literal|"london"
argument_list|,
literal|"company_t"
argument_list|,
literal|""
argument_list|,
literal|"price_ti"
argument_list|,
literal|"29"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb"
argument_list|,
literal|"foo_i"
argument_list|,
literal|9
argument_list|)
argument_list|)
expr_stmt|;
comment|// shard1
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|24
argument_list|,
literal|"place_t"
argument_list|,
literal|"la"
argument_list|,
literal|"company_t"
argument_list|,
literal|""
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|21
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|25
argument_list|,
literal|"company_t"
argument_list|,
literal|"microsoft polecat null fujitsu null bbc"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"59"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|26
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow"
argument_list|,
literal|"company_t"
argument_list|,
literal|"null"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|23
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|27
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow cardiff dublin london la"
argument_list|,
literal|"company_t"
argument_list|,
literal|"null microsoft polecat bbc fujitsu"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|91
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|28
argument_list|,
literal|"place_t"
argument_list|,
literal|"cork"
argument_list|,
literal|"company_t"
argument_list|,
literal|"fujitsu rte"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|76
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|doTestDeepPivotStatsOnString
argument_list|()
expr_stmt|;
name|doTestTopStatsWithRefinement
argument_list|()
expr_stmt|;
block|}
comment|/**    * we need to ensure that stats never "overcount" the values from a single shard    * even if we hit that shard with a refinement request     */
DECL|method|doTestTopStatsWithRefinement
specifier|private
name|void
name|doTestTopStatsWithRefinement
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|coreParams
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
literal|"{!tag=s1}foo_i"
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|facetParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|coreParams
argument_list|)
decl_stmt|;
name|facetParams
operator|.
name|add
argument_list|(
name|params
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"{!stats=s1}place_t,company_t"
argument_list|)
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|facetForceRefineParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|facetParams
argument_list|)
decl_stmt|;
name|facetForceRefineParams
operator|.
name|add
argument_list|(
name|params
argument_list|(
name|FacetParams
operator|.
name|FACET_OVERREQUEST_COUNT
argument_list|,
literal|"0"
argument_list|,
name|FacetParams
operator|.
name|FACET_OVERREQUEST_RATIO
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ModifiableSolrParams
name|params
range|:
operator|new
name|ModifiableSolrParams
index|[]
block|{
name|coreParams
block|,
name|facetParams
block|,
name|facetForceRefineParams
block|}
control|)
block|{
comment|// for all three sets of these params, the "top level"
comment|// stats in the response of a distributed query should be the same
name|ModifiableSolrParams
name|q
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|FieldStatsInfo
name|fieldStatsInfo
init|=
name|rsp
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_i"
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
name|q
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|3.0
argument_list|,
name|fieldStatsInfo
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|91.0
argument_list|,
name|fieldStatsInfo
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|10
argument_list|,
operator|(
name|long
operator|)
name|fieldStatsInfo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|0
argument_list|,
operator|(
name|long
operator|)
name|fieldStatsInfo
operator|.
name|getMissing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|248.0
argument_list|,
name|fieldStatsInfo
operator|.
name|getSum
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|15294.0
argument_list|,
name|fieldStatsInfo
operator|.
name|getSumOfSquares
argument_list|()
argument_list|,
literal|0.1E
operator|-
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|24.8
argument_list|,
operator|(
name|double
operator|)
name|fieldStatsInfo
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.1E
operator|-
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|31.87405772027709
argument_list|,
name|fieldStatsInfo
operator|.
name|getStddev
argument_list|()
argument_list|,
literal|0.1E
operator|-
literal|7
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
literal|"facet"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// if this was a facet request, then the top pivot constraint and pivot
comment|// stats should match what we expect - regardless of wether refine
comment|// was used, or if the query was initially satisfied by the default overrequest
name|List
argument_list|<
name|PivotField
argument_list|>
name|placePivots
init|=
name|rsp
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"place_t,company_t"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|placePivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|dublinPivotField
init|=
name|placePivots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dublin"
argument_list|,
name|dublinPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dublinPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dublinPivotField
operator|.
name|getPivot
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|microsoftPivotField
init|=
name|dublinPivotField
operator|.
name|getPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"microsoft"
argument_list|,
name|microsoftPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|microsoftPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|FieldStatsInfo
name|dublinMicrosoftStatsInfo
init|=
name|microsoftPivotField
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_i"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3.0D
argument_list|,
name|dublinMicrosoftStatsInfo
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|91.0D
argument_list|,
name|dublinMicrosoftStatsInfo
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
operator|(
name|long
operator|)
name|dublinMicrosoftStatsInfo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|dublinMicrosoftStatsInfo
operator|.
name|getMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sanity check that the top pivot from each shard is diff, to prove to
comment|// ourselves that the above queries really must have involved refinement.
name|Object
name|s0pivValue
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|query
argument_list|(
name|facetParams
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"place_t,company_t"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|s1pivValue
init|=
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|query
argument_list|(
name|facetParams
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"place_t,company_t"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"both shards have same top constraint, test is invalid"
operator|+
literal|"(did someone change the test data?) ==> "
operator|+
name|s0pivValue
operator|+
literal|"=="
operator|+
name|s1pivValue
argument_list|,
name|s0pivValue
operator|.
name|equals
argument_list|(
name|s1pivValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestDeepPivotStatsOnString
specifier|private
name|void
name|doTestDeepPivotStatsOnString
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"{!stats=s1}place_t,company_t"
argument_list|,
literal|"stats.field"
argument_list|,
literal|"{!key=avg_price tag=s1}foo_s"
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PivotField
argument_list|>
name|placePivots
init|=
name|rsp
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"place_t,company_t"
argument_list|)
decl_stmt|;
name|PivotField
name|dublinPivotField
init|=
name|placePivots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dublin"
argument_list|,
name|dublinPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dublinPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|microsoftPivotField
init|=
name|dublinPivotField
operator|.
name|getPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"microsoft"
argument_list|,
name|microsoftPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|microsoftPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|FieldStatsInfo
name|dublinMicrosoftStatsInfo
init|=
name|microsoftPivotField
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"avg_price"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|dublinMicrosoftStatsInfo
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|dublinMicrosoftStatsInfo
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
operator|(
name|long
operator|)
name|dublinMicrosoftStatsInfo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|dublinMicrosoftStatsInfo
operator|.
name|getMissing
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|cardiffPivotField
init|=
name|placePivots
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"cardiff"
argument_list|,
name|cardiffPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cardiffPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|polecatPivotField
init|=
name|cardiffPivotField
operator|.
name|getPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"polecat"
argument_list|,
name|polecatPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|polecatPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|FieldStatsInfo
name|cardiffPolecatStatsInfo
init|=
name|polecatPivotField
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"avg_price"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|cardiffPolecatStatsInfo
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|cardiffPolecatStatsInfo
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
name|long
operator|)
name|cardiffPolecatStatsInfo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|cardiffPolecatStatsInfo
operator|.
name|getMissing
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|krakowPivotField
init|=
name|placePivots
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"krakow"
argument_list|,
name|krakowPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|krakowPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|PivotField
name|fujitsuPivotField
init|=
name|krakowPivotField
operator|.
name|getPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"fujitsu"
argument_list|,
name|fujitsuPivotField
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fujitsuPivotField
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|FieldStatsInfo
name|krakowFujitsuStatsInfo
init|=
name|fujitsuPivotField
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"avg_price"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|krakowFujitsuStatsInfo
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|krakowFujitsuStatsInfo
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
name|long
operator|)
name|krakowFujitsuStatsInfo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|long
operator|)
name|krakowFujitsuStatsInfo
operator|.
name|getMissing
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
