begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|embedded
operator|.
name|JettySolrRunner
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
name|request
operator|.
name|UpdateRequest
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
name|ShardParams
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
name|util
operator|.
name|StrUtils
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
DECL|class|TestDistribDocBasedVersion
specifier|public
class|class
name|TestDistribDocBasedVersion
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|bucket1
name|String
name|bucket1
init|=
literal|"shard1"
decl_stmt|;
comment|// shard1: top bits:10  80000000:ffffffff
DECL|field|bucket2
name|String
name|bucket2
init|=
literal|"shard2"
decl_stmt|;
comment|// shard2: top bits:00  00000000:7fffffff
DECL|field|vfield
specifier|private
specifier|static
name|String
name|vfield
init|=
literal|"my_version_l"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeShardHashingTest
specifier|public
specifier|static
name|void
name|beforeShardHashingTest
parameter_list|()
throws|throws
name|Exception
block|{
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-externalversionconstraint.xml"
return|;
block|}
DECL|method|TestDistribDocBasedVersion
specifier|public
name|TestDistribDocBasedVersion
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|super
operator|.
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|super
operator|.
name|shardCount
operator|=
literal|4
expr_stmt|;
name|super
operator|.
name|fixShardCount
operator|=
literal|true
expr_stmt|;
comment|// we only want to test with exactly 2 slices.
comment|/***      hash of a is 3c2569b2 high bits=0 shard=shard3      hash of b is 95de7e03 high bits=2 shard=shard1      hash of c is e132d65f high bits=3 shard=shard2      hash of d is 27191473 high bits=0 shard=shard3      hash of e is 656c4367 high bits=1 shard=shard4      hash of f is 2b64883b high bits=0 shard=shard3      hash of g is f18ae416 high bits=3 shard=shard2      hash of h is d482b2d3 high bits=3 shard=shard2      hash of i is 811a702b high bits=2 shard=shard1      hash of j is ca745a39 high bits=3 shard=shard2      hash of k is cfbda5d1 high bits=3 shard=shard2      hash of l is 1d5d6a2c high bits=0 shard=shard3      hash of m is 5ae4385c high bits=1 shard=shard4      hash of n is c651d8ac high bits=3 shard=shard2      hash of o is 68348473 high bits=1 shard=shard4      hash of p is 986fdf9a high bits=2 shard=shard1      hash of q is ff8209e8 high bits=3 shard=shard2      hash of r is 5c9373f1 high bits=1 shard=shard4      hash of s is ff4acaf1 high bits=3 shard=shard2      hash of t is ca87df4d high bits=3 shard=shard2      hash of u is 62203ae0 high bits=1 shard=shard4      hash of v is bdafcc55 high bits=2 shard=shard1      hash of w is ff439d1f high bits=3 shard=shard2      hash of x is 3e9a9b1b high bits=0 shard=shard3      hash of y is 477d9216 high bits=1 shard=shard4      hash of z is c1f69a17 high bits=3 shard=shard2      ***/
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
name|boolean
name|testFinished
init|=
literal|false
decl_stmt|;
try|try
block|{
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
comment|// todo: do I have to do this here?
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|doTestDocVersions
argument_list|()
expr_stmt|;
name|doTestHardFail
argument_list|()
expr_stmt|;
name|testFinished
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testFinished
condition|)
block|{
name|printLayoutOnTearDown
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|doTestHardFail
specifier|private
name|void
name|doTestHardFail
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doTestHardFail"
argument_list|)
expr_stmt|;
comment|// use a leader so we test both forwarding and non-forwarding logic
name|ss
operator|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|bucket1
argument_list|)
operator|.
name|client
operator|.
name|solrClient
expr_stmt|;
comment|// ss = cloudClient;   CloudSolrServer doesn't currently support propagating error codes
name|doTestHardFail
argument_list|(
literal|"p!doc1"
argument_list|)
expr_stmt|;
name|doTestHardFail
argument_list|(
literal|"q!doc1"
argument_list|)
expr_stmt|;
name|doTestHardFail
argument_list|(
literal|"r!doc1"
argument_list|)
expr_stmt|;
name|doTestHardFail
argument_list|(
literal|"x!doc1"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestHardFail
specifier|private
name|void
name|doTestHardFail
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|vdelete
argument_list|(
name|id
argument_list|,
literal|5
argument_list|,
literal|"update.chain"
argument_list|,
literal|"external-version-failhard"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
name|id
argument_list|,
literal|10
argument_list|,
literal|"update.chain"
argument_list|,
literal|"external-version-failhard"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
name|id
argument_list|,
literal|15
argument_list|,
literal|"update.chain"
argument_list|,
literal|"external-version-failhard"
argument_list|)
expr_stmt|;
name|vaddFail
argument_list|(
name|id
argument_list|,
literal|11
argument_list|,
literal|409
argument_list|,
literal|"update.chain"
argument_list|,
literal|"external-version-failhard"
argument_list|)
expr_stmt|;
name|vdeleteFail
argument_list|(
name|id
argument_list|,
literal|11
argument_list|,
literal|409
argument_list|,
literal|"update.chain"
argument_list|,
literal|"external-version-failhard"
argument_list|)
expr_stmt|;
name|vdelete
argument_list|(
name|id
argument_list|,
literal|20
argument_list|,
literal|"update.chain"
argument_list|,
literal|"external-version-failhard"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestDocVersions
specifier|private
name|void
name|doTestDocVersions
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doTestDocVersions"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ss
operator|=
name|cloudClient
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"c!doc2"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"d!doc3"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"e!doc4"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4"
argument_list|,
literal|"10,11,10,11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc1"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"c!doc2"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"d!doc3"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"e!doc4"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4"
argument_list|,
literal|"10,11,10,11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc1"
argument_list|,
literal|24
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"c!doc2"
argument_list|,
literal|23
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"d!doc3"
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"e!doc4"
argument_list|,
literal|21
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4"
argument_list|,
literal|"24,23,22,21"
argument_list|)
expr_stmt|;
name|vdelete
argument_list|(
literal|"b!doc1"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4"
argument_list|,
literal|"24,23,22,21"
argument_list|)
expr_stmt|;
name|vdelete
argument_list|(
literal|"b!doc1"
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1,c!doc2,d!doc3,e!doc4"
argument_list|,
literal|"30,23,22,21"
argument_list|)
expr_stmt|;
comment|// try delete before add
name|vdelete
argument_list|(
literal|"b!doc123"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc123"
argument_list|,
literal|99
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc123"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|// now add greater
name|vadd
argument_list|(
literal|"b!doc123"
argument_list|,
literal|101
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc123"
argument_list|,
literal|"101"
argument_list|)
expr_stmt|;
comment|//
comment|// now test with a non-smart client
comment|//
comment|// use a leader so we test both forwarding and non-forwarding logic
name|ss
operator|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|bucket1
argument_list|)
operator|.
name|client
operator|.
name|solrClient
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc5"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"c!doc6"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"d!doc7"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"e!doc8"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc5,c!doc6,d!doc7,e!doc8"
argument_list|,
literal|"10,11,10,11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc5"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"c!doc6"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"d!doc7"
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"e!doc8"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc5,c!doc6,d!doc7,e!doc8"
argument_list|,
literal|"10,11,10,11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc5"
argument_list|,
literal|24
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"c!doc6"
argument_list|,
literal|23
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"d!doc7"
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"e!doc8"
argument_list|,
literal|21
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc5,c!doc6,d!doc7,e!doc8"
argument_list|,
literal|"24,23,22,21"
argument_list|)
expr_stmt|;
name|vdelete
argument_list|(
literal|"b!doc5"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc5,c!doc6,d!doc7,e!doc8"
argument_list|,
literal|"24,23,22,21"
argument_list|)
expr_stmt|;
name|vdelete
argument_list|(
literal|"b!doc5"
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc5,c!doc6,d!doc7,e!doc8"
argument_list|,
literal|"30,23,22,21"
argument_list|)
expr_stmt|;
comment|// try delete before add
name|vdelete
argument_list|(
literal|"b!doc1234"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"b!doc1234"
argument_list|,
literal|99
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1234"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|// now add greater
name|vadd
argument_list|(
literal|"b!doc1234"
argument_list|,
literal|101
argument_list|)
expr_stmt|;
name|doRTG
argument_list|(
literal|"b!doc1234"
argument_list|,
literal|"101"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// check liveness for all docs
name|doQuery
argument_list|(
literal|"b!doc123,101,c!doc2,23,d!doc3,22,e!doc4,21,b!doc1234,101,c!doc6,23,d!doc7,22,e!doc8,21"
argument_list|,
literal|"q"
argument_list|,
literal|"live_b:true"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc1,30,b!doc5,30"
argument_list|,
literal|"q"
argument_list|,
literal|"live_b:false"
argument_list|)
expr_stmt|;
comment|// delete by query should just work like normal
name|doDBQ
argument_list|(
literal|"id:b!doc1 OR id:e*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc123,101,c!doc2,23,d!doc3,22,b!doc1234,101,c!doc6,23,d!doc7,22"
argument_list|,
literal|"q"
argument_list|,
literal|"live_b:true"
argument_list|)
expr_stmt|;
name|doQuery
argument_list|(
literal|"b!doc5,30"
argument_list|,
literal|"q"
argument_list|,
literal|"live_b:false"
argument_list|)
expr_stmt|;
block|}
DECL|field|ss
name|SolrServer
name|ss
decl_stmt|;
DECL|method|vdelete
name|void
name|vdelete
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"del_version"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|req
operator|.
name|setParam
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// req.process(cloudClient);
block|}
DECL|method|vadd
name|void
name|vadd
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|vfield
argument_list|,
name|version
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|req
operator|.
name|setParam
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|vaddFail
name|void
name|vaddFail
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|int
name|errCode
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|vadd
argument_list|(
name|id
argument_list|,
name|version
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
name|errCode
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
DECL|method|vdeleteFail
name|void
name|vdeleteFail
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|int
name|errCode
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|vdelete
argument_list|(
name|id
argument_list|,
name|version
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
name|errCode
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
DECL|method|doQuery
name|void
name|doQuery
parameter_list|(
name|String
name|expectedDocs
parameter_list|,
name|String
modifier|...
name|queryParams
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strs
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|expectedDocs
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedIds
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
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
name|strs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|id
init|=
name|strs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|vS
init|=
name|strs
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Long
name|v
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|vS
argument_list|)
decl_stmt|;
name|expectedIds
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|QueryResponse
name|rsp
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
name|queryParams
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obtainedIds
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|obtainedIds
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|vfield
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedIds
argument_list|,
name|obtainedIds
argument_list|)
expr_stmt|;
block|}
DECL|method|doRTG
name|void
name|doRTG
parameter_list|(
name|String
name|ids
parameter_list|,
name|String
name|versions
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedIds
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|strs
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|ids
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|verS
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|versions
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
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
name|strs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|expectedIds
operator|.
name|put
argument_list|(
name|strs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|verS
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|ids
argument_list|)
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|ids
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obtainedIds
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|obtainedIds
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|vfield
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedIds
argument_list|,
name|obtainedIds
argument_list|)
expr_stmt|;
block|}
DECL|method|doRTG
name|void
name|doRTG
parameter_list|(
name|String
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|ss
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|ids
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedIds
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|ids
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|ids
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|obtainedIds
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|obtainedIds
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedIds
argument_list|,
name|obtainedIds
argument_list|)
expr_stmt|;
block|}
comment|// TODO: refactor some of this stuff into the SolrJ client... it should be easier to use
DECL|method|doDBQ
name|void
name|doDBQ
parameter_list|(
name|String
name|q
parameter_list|,
name|String
modifier|...
name|reqParams
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|(
name|reqParams
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

