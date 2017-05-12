begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SolrDocument
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
name|SolrDocumentList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Test for QueryComponent's distributed querying  *  * @see org.apache.solr.handler.component.QueryComponent  */
end_comment

begin_class
DECL|class|DistributedExpandComponentTest
specifier|public
class|class
name|DistributedExpandComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedExpandComponentTest
specifier|public
name|DistributedExpandComponentTest
parameter_list|()
block|{
name|stress
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUpBeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-collapseqparser.xml"
argument_list|,
literal|"schema11.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|3
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|group
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"group_s"
else|:
literal|"group_s_dv"
operator|)
decl_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|0
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group1"
argument_list|,
literal|"test_i"
argument_list|,
literal|"5"
argument_list|,
literal|"test_l"
argument_list|,
literal|"10"
argument_list|,
literal|"test_f"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|0
argument_list|,
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group1"
argument_list|,
literal|"test_i"
argument_list|,
literal|"50"
argument_list|,
literal|"test_l"
argument_list|,
literal|"100"
argument_list|,
literal|"test_f"
argument_list|,
literal|"200"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group2"
argument_list|,
literal|"test_i"
argument_list|,
literal|"4"
argument_list|,
literal|"test_l"
argument_list|,
literal|"10"
argument_list|,
literal|"test_f"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group2"
argument_list|,
literal|"test_i"
argument_list|,
literal|"10"
argument_list|,
literal|"test_l"
argument_list|,
literal|"100"
argument_list|,
literal|"test_f"
argument_list|,
literal|"200"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|0
argument_list|,
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group1"
argument_list|,
literal|"test_i"
argument_list|,
literal|"1"
argument_list|,
literal|"test_l"
argument_list|,
literal|"100000"
argument_list|,
literal|"test_f"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group2"
argument_list|,
literal|"test_i"
argument_list|,
literal|"2"
argument_list|,
literal|"test_l"
argument_list|,
literal|"100000"
argument_list|,
literal|"test_f"
argument_list|,
literal|"200"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group3"
argument_list|,
literal|"test_i"
argument_list|,
literal|"1000"
argument_list|,
literal|"test_l"
argument_list|,
literal|"1005"
argument_list|,
literal|"test_f"
argument_list|,
literal|"3000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group3"
argument_list|,
literal|"test_i"
argument_list|,
literal|"1500"
argument_list|,
literal|"test_l"
argument_list|,
literal|"1001"
argument_list|,
literal|"test_f"
argument_list|,
literal|"3200"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group3"
argument_list|,
literal|"test_i"
argument_list|,
literal|"1300"
argument_list|,
literal|"test_l"
argument_list|,
literal|"1002"
argument_list|,
literal|"test_f"
argument_list|,
literal|"3300"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group4"
argument_list|,
literal|"test_i"
argument_list|,
literal|"15"
argument_list|,
literal|"test_l"
argument_list|,
literal|"10"
argument_list|,
literal|"test_f"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group4"
argument_list|,
literal|"test_i"
argument_list|,
literal|"16"
argument_list|,
literal|"test_l"
argument_list|,
literal|"9"
argument_list|,
literal|"test_f"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"14"
argument_list|,
literal|"term_s"
argument_list|,
literal|"YYYY"
argument_list|,
name|group
argument_list|,
literal|"group4"
argument_list|,
literal|"test_i"
argument_list|,
literal|"1"
argument_list|,
literal|"test_l"
argument_list|,
literal|"20"
argument_list|,
literal|"test_f"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"explain"
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
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards.qt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
name|SKIP
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
name|handle
operator|.
name|put
argument_list|(
literal|"_version_"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"expanded"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|,
literal|"expand"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|,
literal|"expand"
argument_list|,
literal|"true"
argument_list|,
literal|"expand.sort"
argument_list|,
literal|"test_l desc"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|,
literal|"expand"
argument_list|,
literal|"true"
argument_list|,
literal|"expand.sort"
argument_list|,
literal|"test_l desc"
argument_list|,
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|//Test no expand results
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"test_i:5"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|,
literal|"expand"
argument_list|,
literal|"true"
argument_list|,
literal|"expand.sort"
argument_list|,
literal|"test_l desc"
argument_list|,
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|//Test zero results
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"test_i:5434343"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|,
literal|"expand"
argument_list|,
literal|"true"
argument_list|,
literal|"expand.sort"
argument_list|,
literal|"test_l desc"
argument_list|,
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|//Test page 2
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"start"
argument_list|,
literal|"1"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|,
literal|"expand"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|//First basic test case.
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
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocumentList
argument_list|>
name|results
init|=
name|rsp
operator|.
name|getExpandedResults
argument_list|()
decl_stmt|;
name|assertExpandGroups
argument_list|(
name|results
argument_list|,
literal|"group1"
argument_list|,
literal|"group2"
argument_list|,
literal|"group3"
argument_list|,
literal|"group4"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group1"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"1.0"
argument_list|,
literal|"7.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group2"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"5.0"
argument_list|,
literal|"8.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group3"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"11.0"
argument_list|,
literal|"9.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group4"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"12.0"
argument_list|,
literal|"14.0"
argument_list|)
expr_stmt|;
comment|//Test expand.sort
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_l desc"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|results
operator|=
name|rsp
operator|.
name|getExpandedResults
argument_list|()
expr_stmt|;
name|assertExpandGroups
argument_list|(
name|results
argument_list|,
literal|"group1"
argument_list|,
literal|"group2"
argument_list|,
literal|"group3"
argument_list|,
literal|"group4"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group1"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"7.0"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group2"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"8.0"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group3"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"9.0"
argument_list|,
literal|"11.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group4"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"14.0"
argument_list|,
literal|"12.0"
argument_list|)
expr_stmt|;
comment|//Test expand.rows
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_l desc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|results
operator|=
name|rsp
operator|.
name|getExpandedResults
argument_list|()
expr_stmt|;
name|assertExpandGroups
argument_list|(
name|results
argument_list|,
literal|"group1"
argument_list|,
literal|"group2"
argument_list|,
literal|"group3"
argument_list|,
literal|"group4"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group1"
argument_list|,
literal|1
argument_list|,
name|results
argument_list|,
literal|"7.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group2"
argument_list|,
literal|1
argument_list|,
name|results
argument_list|,
literal|"8.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group3"
argument_list|,
literal|1
argument_list|,
name|results
argument_list|,
literal|"9.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group4"
argument_list|,
literal|1
argument_list|,
name|results
argument_list|,
literal|"14.0"
argument_list|)
expr_stmt|;
comment|//Test key-only fl
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|results
operator|=
name|rsp
operator|.
name|getExpandedResults
argument_list|()
expr_stmt|;
name|assertExpandGroups
argument_list|(
name|results
argument_list|,
literal|"group1"
argument_list|,
literal|"group2"
argument_list|,
literal|"group3"
argument_list|,
literal|"group4"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group1"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"1.0"
argument_list|,
literal|"7.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group2"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"5.0"
argument_list|,
literal|"8.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group3"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"11.0"
argument_list|,
literal|"9.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group4"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"12.0"
argument_list|,
literal|"14.0"
argument_list|)
expr_stmt|;
comment|//Test distrib.singlePass true
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_i)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|results
operator|=
name|rsp
operator|.
name|getExpandedResults
argument_list|()
expr_stmt|;
name|assertExpandGroups
argument_list|(
name|results
argument_list|,
literal|"group1"
argument_list|,
literal|"group2"
argument_list|,
literal|"group3"
argument_list|,
literal|"group4"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group1"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"1.0"
argument_list|,
literal|"7.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group2"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"5.0"
argument_list|,
literal|"8.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group3"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"11.0"
argument_list|,
literal|"9.0"
argument_list|)
expr_stmt|;
name|assertExpandGroupCountAndOrder
argument_list|(
literal|"group4"
argument_list|,
literal|2
argument_list|,
name|results
argument_list|,
literal|"12.0"
argument_list|,
literal|"14.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExpandGroups
specifier|private
name|void
name|assertExpandGroups
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocumentList
argument_list|>
name|expandedResults
parameter_list|,
name|String
modifier|...
name|groups
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|expandedResults
operator|.
name|containsKey
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Expanded Group Not Found:"
operator|+
name|groups
index|[
name|i
index|]
operator|+
literal|", Found:"
operator|+
name|exportGroups
argument_list|(
name|expandedResults
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|exportGroups
specifier|private
name|String
name|exportGroups
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocumentList
argument_list|>
name|groups
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|groups
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|group
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|group
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|assertExpandGroupCountAndOrder
specifier|private
name|void
name|assertExpandGroupCountAndOrder
parameter_list|(
name|String
name|group
parameter_list|,
name|int
name|count
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocumentList
argument_list|>
name|expandedResults
parameter_list|,
name|String
modifier|...
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrDocumentList
name|results
init|=
name|expandedResults
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Group Not Found:"
operator|+
name|group
argument_list|)
throw|;
block|}
if|if
condition|(
name|results
operator|.
name|size
argument_list|()
operator|!=
name|count
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Expected Count "
operator|+
name|results
operator|.
name|size
argument_list|()
operator|+
literal|" Not Found:"
operator|+
name|count
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|docs
index|[
name|i
index|]
decl_stmt|;
name|SolrDocument
name|doc
init|=
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Id not in results or out of order:"
operator|+
name|id
operator|+
literal|"!="
operator|+
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

