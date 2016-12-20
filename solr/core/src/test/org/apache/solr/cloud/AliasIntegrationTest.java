begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SolrQuery
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
name|impl
operator|.
name|CloudSolrClient
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
name|impl
operator|.
name|HttpSolrClient
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
name|CollectionAdminRequest
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
name|SolrException
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

begin_class
DECL|class|AliasIntegrationTest
specifier|public
class|class
name|AliasIntegrationTest
extends|extends
name|SolrCloudTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|2
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
argument_list|,
name|configset
argument_list|(
literal|"cloud-minimal"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"collection1"
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"collection2"
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected collection1 to be created with 2 shards and 1 replica"
argument_list|,
literal|"collection1"
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected collection2 to be created with 1 shard and 1 replica"
argument_list|,
literal|"collection2"
argument_list|,
name|clusterShape
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"a_t"
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"a_t"
argument_list|,
literal|"humpty dumpy3 sat on a walls"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"a_t"
argument_list|,
literal|"humpty dumpy2 sat on a walled"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"a_t"
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"a_t"
argument_list|,
literal|"humpty dumpy3 sat on a walls"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
literal|"collection2"
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection1"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// search for alias
name|QueryResponse
name|res
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
literal|"testalias"
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// search for alias with random non cloud client
name|JettySolrRunner
name|jetty
init|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/testalias"
argument_list|)
init|)
block|{
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create alias, collection2 first because it's not on every node
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2,collection1"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// search with new cloud client
try|try
init|(
name|CloudSolrClient
name|cloudSolrClient
init|=
name|getCloudSolrClient
argument_list|(
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
init|)
block|{
name|cloudSolrClient
operator|.
name|setParallelUpdates
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// Try with setDefaultCollection
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|setDefaultCollection
argument_list|(
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// search for alias with random non cloud client
name|jetty
operator|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/testalias"
argument_list|)
init|)
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// now without collections param
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// update alias
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// search for alias
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// set alias to two collections
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection1,collection2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// try a std client
comment|// search 1 and 2, but have no collections param
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/testalias"
argument_list|)
init|)
block|{
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// a second alias
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias2"
argument_list|,
literal|"collection2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/testalias"
argument_list|)
init|)
block|{
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"a_t"
argument_list|,
literal|"humpty dumpy4 sat on a walls"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2,collection1"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteAlias
argument_list|(
literal|"testalias"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteAlias
argument_list|(
literal|"testalias2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected exception message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Collection not found: testalias"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testErrorChecks
specifier|public
name|void
name|testErrorChecks
parameter_list|()
throws|throws
name|Exception
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"testErrorChecks-collection"
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected testErrorChecks-collection to be created with 2 shards and 1 replica"
argument_list|,
literal|"testErrorChecks-collection"
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
comment|// Invalid Alias name
name|SolrException
name|e
init|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"test:alias"
argument_list|,
literal|"testErrorChecks-collection"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Target collection doesn't exists
name|e
operator|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"doesnotexist"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Can't create collection alias for collections='doesnotexist', 'doesnotexist' is not an existing collection or alias"
argument_list|)
argument_list|)
expr_stmt|;
comment|// One of the target collections doesn't exist
name|e
operator|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"testErrorChecks-collection,doesnotexist"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Can't create collection alias for collections='testErrorChecks-collection,doesnotexist', 'doesnotexist' is not an existing collection or alias"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Valid
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"testErrorChecks-collection"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias2"
argument_list|,
literal|"testalias"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// Alias + invalid
name|e
operator|=
name|expectThrows
argument_list|(
name|SolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
literal|"testalias3"
argument_list|,
literal|"testalias2,doesnotexist"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|unIgnoreException
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteAlias
argument_list|(
literal|"testalias"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteAlias
argument_list|(
literal|"testalias2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
literal|"testErrorChecks-collection"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

