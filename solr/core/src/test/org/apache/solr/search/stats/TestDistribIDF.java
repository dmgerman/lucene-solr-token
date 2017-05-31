begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|TestUtil
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|response
operator|.
name|CollectionAdminResponse
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
name|cloud
operator|.
name|AbstractDistribZkTestBase
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
name|cloud
operator|.
name|MiniSolrCloudCluster
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
name|SolrInputDocument
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
name|cloud
operator|.
name|CompositeIdRouter
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
name|cloud
operator|.
name|ImplicitDocRouter
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
name|cloud
operator|.
name|ZkStateReader
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestDistribIDF
specifier|public
class|class
name|TestDistribIDF
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|solrCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCluster
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|ExactStatsCache
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|LRUStatsCache
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|solrCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|3
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|uploadConfigSet
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1/conf"
argument_list|)
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|uploadConfigSet
argument_list|(
name|configset
argument_list|(
literal|"configset-2"
argument_list|)
argument_list|,
literal|"conf2"
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
name|solrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.statsCache"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleQuery
specifier|public
name|void
name|testSimpleQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|//3 shards. 3rd shard won't have any data.
name|createCollection
argument_list|(
literal|"onecollection"
argument_list|,
literal|"conf1"
argument_list|,
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"onecollection_local"
argument_list|,
literal|"conf2"
argument_list|,
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
literal|"football"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"onecollection"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"onecollection_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
literal|"football"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"onecollection"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"onecollection_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|int
name|nDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|100
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|3
operator|+
name|i
argument_list|)
expr_stmt|;
name|String
name|cat
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cat
operator|.
name|equals
argument_list|(
literal|"football"
argument_list|)
condition|)
block|{
comment|//Making sure no other document has the query term in it.
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|cat
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
comment|//Put most documents in shard b so that 'football' becomes 'rare' in shard b
name|doc
operator|.
name|addField
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|addField
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
block|}
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"onecollection"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"onecollection_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|"onecollection"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|"onecollection_local"
argument_list|)
expr_stmt|;
comment|//Test against all nodes
for|for
control|(
name|JettySolrRunner
name|jettySolrRunner
range|:
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
try|try
init|(
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
name|SolrClient
name|solrClient_local
init|=
name|getHttpSolrClient
argument_list|(
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
init|)
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"cat:football"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setFields
argument_list|(
literal|"*,score"
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|solrClient
operator|.
name|query
argument_list|(
literal|"onecollection"
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score1
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Doc1 score="
operator|+
name|score1
operator|+
literal|" Doc2 score="
operator|+
name|score2
argument_list|,
literal|0
argument_list|,
name|Float
operator|.
name|compare
argument_list|(
name|score1
argument_list|,
name|score2
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"cat:football"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setShowDebugInfo
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
literal|"*,score"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|solrClient_local
operator|.
name|query
argument_list|(
literal|"onecollection_local"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|score1_local
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|float
name|score2_local
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Doc1 score="
operator|+
name|score1_local
operator|+
literal|" Doc2 score="
operator|+
name|score2_local
argument_list|,
literal|1
argument_list|,
name|Float
operator|.
name|compare
argument_list|(
name|score1_local
argument_list|,
name|score2_local
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testMultiCollectionQuery
specifier|public
name|void
name|testMultiCollectionQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// collection1 and collection2 are collections which have distributed idf enabled
comment|// collection1_local and collection2_local don't have distributed idf available
comment|// Only one doc has cat:football in each collection
comment|// When doing queries across collections we want to test that the query takes into account
comment|// distributed idf for the collection=collection1,collection2 query.
comment|// The way we verify is that score should be the same when querying across collection1 and collection2
comment|// But should be different when querying across collection1_local and collection2_local
comment|// since the idf is calculated per shard
name|createCollection
argument_list|(
literal|"collection1"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"collection1_local"
argument_list|,
literal|"conf2"
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"collection2"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"collection2_local"
argument_list|,
literal|"conf2"
argument_list|)
expr_stmt|;
name|addDocsRandomly
argument_list|()
expr_stmt|;
comment|//Test against all nodes
for|for
control|(
name|JettySolrRunner
name|jettySolrRunner
range|:
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
try|try
init|(
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
name|SolrClient
name|solrClient_local
init|=
name|getHttpSolrClient
argument_list|(
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
init|)
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"cat:football"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setFields
argument_list|(
literal|"*,score"
argument_list|)
operator|.
name|add
argument_list|(
literal|"collection"
argument_list|,
literal|"collection1,collection2"
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|solrClient
operator|.
name|query
argument_list|(
literal|"collection1"
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|score1
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|float
name|score2
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Doc1 score="
operator|+
name|score1
operator|+
literal|" Doc2 score="
operator|+
name|score2
argument_list|,
literal|0
argument_list|,
name|Float
operator|.
name|compare
argument_list|(
name|score1
argument_list|,
name|score2
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"cat:football"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
literal|"*,score"
argument_list|)
operator|.
name|add
argument_list|(
literal|"collection"
argument_list|,
literal|"collection1_local,collection2_local"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|solrClient_local
operator|.
name|query
argument_list|(
literal|"collection1_local"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|score1_local
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|float
name|score2_local
init|=
operator|(
name|float
operator|)
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Doc1 score="
operator|+
name|score1_local
operator|+
literal|" Doc2 score="
operator|+
name|score2_local
argument_list|,
literal|1
argument_list|,
name|Float
operator|.
name|compare
argument_list|(
name|score1_local
argument_list|,
name|score2_local
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|createCollection
specifier|private
name|void
name|createCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|createCollection
argument_list|(
name|name
argument_list|,
name|config
argument_list|,
name|CompositeIdRouter
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|createCollection
specifier|private
name|void
name|createCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|config
parameter_list|,
name|String
name|router
parameter_list|)
throws|throws
name|Exception
block|{
name|CollectionAdminResponse
name|response
decl_stmt|;
if|if
condition|(
name|router
operator|.
name|equals
argument_list|(
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
name|name
argument_list|,
name|config
argument_list|,
literal|"a,b,c"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|create
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|response
operator|=
name|create
operator|.
name|process
argument_list|(
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|name
argument_list|,
name|config
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|create
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|response
operator|=
name|create
operator|.
name|process
argument_list|(
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|!=
literal|0
operator|||
name|response
operator|.
name|getErrorMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not create collection. Response"
operator|+
name|response
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ZkStateReader
name|zkStateReader
init|=
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|name
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocsRandomly
specifier|private
name|void
name|addDocsRandomly
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
literal|"football"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection1"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection1_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
literal|"football"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection2"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection2_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|int
name|nDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|collection1Count
init|=
literal|1
decl_stmt|;
name|int
name|collection2Count
init|=
literal|1
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|3
operator|+
name|i
argument_list|)
expr_stmt|;
name|String
name|cat
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cat
operator|.
name|equals
argument_list|(
literal|"football"
argument_list|)
condition|)
block|{
comment|//Making sure no other document has the query term in it.
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|cat
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
comment|//Put most documents in collection2* so that 'football' becomes 'rare' in collection2*
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection1"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection1_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|collection1Count
operator|++
expr_stmt|;
block|}
else|else
block|{
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection2"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
literal|"collection2_local"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|collection2Count
operator|++
expr_stmt|;
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"numDocs={}. collection1Count={} collection2Count={}"
argument_list|,
name|nDocs
argument_list|,
name|collection1Count
argument_list|,
name|collection2Count
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|"collection2"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|"collection1_local"
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|"collection2_local"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

