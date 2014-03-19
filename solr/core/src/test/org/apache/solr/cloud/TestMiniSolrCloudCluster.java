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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|List
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
name|CloudSolrServer
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
name|QueryRequest
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
name|ClusterState
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
name|Replica
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
name|Slice
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
name|SolrZkClient
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
name|CollectionParams
operator|.
name|CollectionAction
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
name|CoreAdminParams
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
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_comment
comment|/**  * Test of the MiniSolrCloudCluster functionality.  This doesn't derive from  * LuceneTestCase, as the MiniSolrCloudCluster is designed to be used outside of the  * lucene test hierarchy.  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestMiniSolrCloudCluster
specifier|public
class|class
name|TestMiniSolrCloudCluster
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MiniSolrCloudCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_SERVERS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_SERVERS
init|=
literal|5
decl_stmt|;
DECL|field|NUM_SHARDS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_SHARDS
init|=
literal|2
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|private
specifier|static
specifier|final
name|int
name|REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
DECL|field|miniCluster
specifier|private
specifier|static
name|MiniSolrCloudCluster
name|miniCluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startup
specifier|public
specifier|static
name|void
name|startup
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testHome
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
decl_stmt|;
name|miniCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
literal|null
argument_list|,
operator|new
name|File
argument_list|(
name|testHome
argument_list|,
literal|"solr-no-core.xml"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|miniCluster
operator|!=
literal|null
condition|)
block|{
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|miniCluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
init|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|assertTrue
argument_list|(
name|jetty
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// shut down a server
name|JettySolrRunner
name|stoppedServer
init|=
name|miniCluster
operator|.
name|stopJettySolrRunner
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stoppedServer
operator|.
name|isStopped
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
operator|-
literal|1
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// create a server
name|JettySolrRunner
name|startedServer
init|=
name|miniCluster
operator|.
name|startJettySolrRunner
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|startedServer
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CloudSolrServer
name|cloudSolrServer
init|=
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cloudSolrServer
operator|=
operator|new
name|CloudSolrServer
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cloudSolrServer
operator|.
name|connect
argument_list|()
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|45000
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// create collection
name|String
name|collectionName
init|=
literal|"testSolrCloudCollection"
decl_stmt|;
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.mergePolicy"
argument_list|,
literal|"org.apache.lucene.index.TieredMergePolicy"
argument_list|)
expr_stmt|;
name|uploadConfigToZk
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
name|cloudSolrServer
argument_list|,
name|collectionName
argument_list|,
name|NUM_SHARDS
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|configName
argument_list|)
expr_stmt|;
comment|// modify/query collection
name|cloudSolrServer
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
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
literal|"1"
argument_list|)
expr_stmt|;
name|cloudSolrServer
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cloudSolrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|cloudSolrServer
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove a server not hosting any replicas
name|ZkStateReader
name|zkStateReader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|JettySolrRunner
argument_list|>
name|jettyMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|JettySolrRunner
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
operator|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|getProtocol
argument_list|()
operator|+
literal|"://"
operator|)
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|jettyMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|jetty
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getSlices
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
comment|// track the servers not host repliacs
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|jettyMap
operator|.
name|remove
argument_list|(
name|slice
operator|.
name|getLeader
argument_list|()
operator|.
name|getNodeName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"_solr"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|jettyMap
operator|.
name|remove
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"_solr"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Expected to find a node without a replica"
argument_list|,
name|jettyMap
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jettyToStop
init|=
name|jettyMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|jettys
operator|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
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
name|jettys
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|jettys
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|jettyToStop
argument_list|)
condition|)
block|{
name|miniCluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
operator|-
literal|1
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cloudSolrServer
operator|!=
literal|null
condition|)
block|{
name|cloudSolrServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|uploadConfigToZk
specifier|protected
name|void
name|uploadConfigToZk
parameter_list|(
name|String
name|configDir
parameter_list|,
name|String
name|configName
parameter_list|)
throws|throws
name|Exception
block|{
comment|// override settings in the solrconfig include
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.maxBufferedDocs"
argument_list|,
literal|"100000"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.maxIndexingThreads"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.ramBufferSizeMB"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|// use non-test classes so RandomizedRunner isn't necessary
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.mergeScheduler"
argument_list|,
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.RAMDirectoryFactory"
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|45000
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"solrconfig.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"schema.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"currency.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"currency.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"mapping-ISOLatin1Accent.txt"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"mapping-ISOLatin1Accent.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"old_synonyms.txt"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"old_synonyms.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"open-exchange-rates.json"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"open-exchange-rates.json"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"protwords.txt"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"protwords.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"stopwords.txt"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"stopwords.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|uploadConfigFileToZk
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
literal|"synonyms.txt"
argument_list|,
operator|new
name|File
argument_list|(
name|configDir
argument_list|,
literal|"synonyms.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|uploadConfigFileToZk
specifier|protected
name|void
name|uploadConfigFileToZk
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|configName
parameter_list|,
name|String
name|nameInZk
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|configName
operator|+
literal|"/"
operator|+
name|nameInZk
argument_list|,
name|file
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createCollection
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|createCollection
parameter_list|(
name|CloudSolrServer
name|server
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|numShards
parameter_list|,
name|int
name|replicationFactor
parameter_list|,
name|String
name|configName
parameter_list|)
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|modParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|modParams
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CollectionAction
operator|.
name|CREATE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|modParams
operator|.
name|set
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|modParams
operator|.
name|set
argument_list|(
literal|"numShards"
argument_list|,
name|numShards
argument_list|)
expr_stmt|;
name|modParams
operator|.
name|set
argument_list|(
literal|"replicationFactor"
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
name|modParams
operator|.
name|set
argument_list|(
literal|"collection.configName"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|modParams
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
return|return
name|server
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
end_class

end_unit

