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
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|concurrent
operator|.
name|TimeUnit
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
name|LuceneTestCase
operator|.
name|Slow
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
name|JSONTestUtil
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
operator|.
name|SuppressSSL
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
name|HttpSolrServer
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
name|ZkCoreNodeProps
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
name|After
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
comment|/**  * Simulates HTTP partitions between a leader and replica but the replica does  * not lose its ZooKeeper connection.  */
end_comment

begin_class
annotation|@
name|Slow
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|HttpPartitionTest
specifier|public
class|class
name|HttpPartitionTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HttpPartitionTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// To prevent the test assertions firing too fast before cluster state
comment|// recognizes (and propagates) partitions
DECL|field|sleepMsBeforeHealPartition
specifier|private
specifier|static
specifier|final
name|long
name|sleepMsBeforeHealPartition
init|=
literal|2000L
decl_stmt|;
DECL|field|maxWaitSecsToSeeAllActive
specifier|private
specifier|static
specifier|final
name|int
name|maxWaitSecsToSeeAllActive
init|=
literal|30
decl_stmt|;
DECL|field|proxies
specifier|private
name|Map
argument_list|<
name|URI
argument_list|,
name|SocketProxy
argument_list|>
name|proxies
init|=
operator|new
name|HashMap
argument_list|<
name|URI
argument_list|,
name|SocketProxy
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|HttpPartitionTest
specifier|public
name|HttpPartitionTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Before
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{}
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
comment|// close socket proxies after super.tearDown
if|if
condition|(
operator|!
name|proxies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|SocketProxy
name|proxy
range|:
name|proxies
operator|.
name|values
argument_list|()
control|)
block|{
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Overrides the parent implementation so that we can configure a socket proxy    * to sit infront of each Jetty server, which gives us the ability to simulate    * network partitions without having to fuss with IPTables (which is not very    * cross platform friendly).    */
annotation|@
name|Override
DECL|method|createJetty
specifier|public
name|JettySolrRunner
name|createJetty
parameter_list|(
name|File
name|solrHome
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|shardList
parameter_list|,
name|String
name|solrConfigOverride
parameter_list|,
name|String
name|schemaOverride
parameter_list|)
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|solrHome
operator|.
name|getPath
argument_list|()
argument_list|,
name|context
argument_list|,
literal|0
argument_list|,
name|solrConfigOverride
argument_list|,
name|schemaOverride
argument_list|,
literal|false
argument_list|,
name|getExtraServlets
argument_list|()
argument_list|,
name|sslConfig
argument_list|,
name|getExtraRequestFilters
argument_list|()
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|setShards
argument_list|(
name|shardList
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|dataDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// setup to proxy Http requests to this server unless it is the control
comment|// server
name|int
name|proxyPort
init|=
name|getNextAvailablePort
argument_list|()
decl_stmt|;
name|jetty
operator|.
name|setProxyPort
argument_list|(
name|proxyPort
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// create a socket proxy for the jetty server ...
name|SocketProxy
name|proxy
init|=
operator|new
name|SocketProxy
argument_list|(
name|proxyPort
argument_list|,
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|proxies
operator|.
name|put
argument_list|(
name|proxy
operator|.
name|getUrl
argument_list|()
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|method|getNextAvailablePort
specifier|protected
name|int
name|getNextAvailablePort
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
try|try
init|(
name|ServerSocket
name|s
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
init|)
block|{
name|port
operator|=
name|s
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
return|return
name|port
return|;
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
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// test a 1x2 collection
name|testRf2
argument_list|()
expr_stmt|;
comment|// now do similar for a 1x3 collection while taking 2 replicas on-and-off
comment|// each time
name|testRf3
argument_list|()
expr_stmt|;
comment|// kill a leader and make sure recovery occurs as expected
name|testRf3WithLeaderFailover
argument_list|()
expr_stmt|;
block|}
DECL|method|testRf2
specifier|protected
name|void
name|testRf2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a collection that has 1 shard but 2 replicas
name|String
name|testCollectionName
init|=
literal|"c8n_1x2"
decl_stmt|;
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Replica
name|notLeader
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// ok, now introduce a network partition between the leader and the replica
name|SocketProxy
name|proxy
init|=
name|getProxyForReplica
argument_list|(
name|notLeader
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// indexing during a partition
name|sendDoc
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// Have the partition last at least 1 sec
comment|// While this gives the impression that recovery is timing related, this is
comment|// really only
comment|// to give time for the state to be written to ZK before the test completes.
comment|// In other words,
comment|// without a brief pause, the test finishes so quickly that it doesn't give
comment|// time for the recovery process to kick-in
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
decl_stmt|;
name|sendDoc
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// sent 3 docs in so far, verify they are on the leader and replica
name|assertDocsExistInAllReplicas
argument_list|(
name|notLeaders
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// now up the stakes and do more docs
name|int
name|numDocs
init|=
literal|1000
decl_stmt|;
name|boolean
name|hasPartition
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|numDocs
condition|;
name|d
operator|++
control|)
block|{
comment|// create / restore partition every 100 docs
if|if
condition|(
name|d
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|hasPartition
condition|)
block|{
name|proxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|hasPartition
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|d
operator|>=
literal|100
condition|)
block|{
name|proxy
operator|.
name|close
argument_list|()
expr_stmt|;
name|hasPartition
operator|=
literal|true
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|sendDoc
argument_list|(
name|d
operator|+
literal|4
argument_list|)
expr_stmt|;
comment|// 4 is offset as we've already indexed 1-3
block|}
comment|// restore connectivity if lost
if|if
condition|(
name|hasPartition
condition|)
block|{
name|proxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
block|}
name|notLeaders
operator|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
expr_stmt|;
comment|// verify all docs received
name|assertDocsExistInAllReplicas
argument_list|(
name|notLeaders
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|+
literal|3
argument_list|)
expr_stmt|;
comment|// try to clean up
try|try
block|{
name|CollectionAdminRequest
name|req
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRf3
specifier|protected
name|void
name|testRf3
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a collection that has 1 shard but 2 replicas
name|String
name|testCollectionName
init|=
literal|"c8n_1x3"
decl_stmt|;
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 2 replicas for collection "
operator|+
name|testCollectionName
operator|+
literal|" but found "
operator|+
name|notLeaders
operator|.
name|size
argument_list|()
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|notLeaders
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// ok, now introduce a network partition between the leader and the replica
name|SocketProxy
name|proxy0
init|=
name|getProxyForReplica
argument_list|(
name|notLeaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|proxy0
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// indexing during a partition
name|sendDoc
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|proxy0
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|SocketProxy
name|proxy1
init|=
name|getProxyForReplica
argument_list|(
name|notLeaders
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|proxy1
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendDoc
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|proxy1
operator|.
name|reopen
argument_list|()
expr_stmt|;
comment|// sent 4 docs in so far, verify they are on the leader and replica
name|notLeaders
operator|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertDocsExistInAllReplicas
argument_list|(
name|notLeaders
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|// try to clean up
try|try
block|{
name|CollectionAdminRequest
name|req
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRf3WithLeaderFailover
specifier|protected
name|void
name|testRf3WithLeaderFailover
parameter_list|()
throws|throws
name|Exception
block|{
comment|// now let's create a partition in one of the replicas and outright
comment|// kill the leader ... see what happens
comment|// create a collection that has 1 shard but 3 replicas
name|String
name|testCollectionName
init|=
literal|"c8n_1x3_lf"
decl_stmt|;
comment|// _lf is leader fails
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 2 replicas for collection "
operator|+
name|testCollectionName
operator|+
literal|" but found "
operator|+
name|notLeaders
operator|.
name|size
argument_list|()
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|notLeaders
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// ok, now introduce a network partition between the leader and the replica
name|SocketProxy
name|proxy0
init|=
literal|null
decl_stmt|;
name|proxy0
operator|=
name|getProxyForReplica
argument_list|(
name|notLeaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|proxy0
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// indexing during a partition
name|sendDoc
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|proxy0
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|SocketProxy
name|proxy1
init|=
name|getProxyForReplica
argument_list|(
name|notLeaders
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|proxy1
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendDoc
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|proxy1
operator|.
name|reopen
argument_list|()
expr_stmt|;
comment|// sent 4 docs in so far, verify they are on the leader and replica
name|notLeaders
operator|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertDocsExistInAllReplicas
argument_list|(
name|notLeaders
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|String
name|leaderNode
init|=
name|leader
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Could not find leader for shard1 of "
operator|+
name|testCollectionName
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|leader
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|leaderJetty
init|=
name|getJettyOnPort
argument_list|(
name|getReplicaPort
argument_list|(
name|leader
argument_list|)
argument_list|)
decl_stmt|;
comment|// since maxShardsPerNode is 1, we're safe to kill the leader
name|notLeaders
operator|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
expr_stmt|;
name|proxy0
operator|=
name|getProxyForReplica
argument_list|(
name|notLeaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|proxy0
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// indexing during a partition
comment|// doc should be on leader and 1 replica
name|sendDoc
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|String
name|shouldNotBeNewLeaderNode
init|=
name|notLeaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
comment|// kill the leader
name|leaderJetty
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|leaderJetty
operator|.
name|isRunning
argument_list|()
condition|)
name|fail
argument_list|(
literal|"Failed to stop the leader on "
operator|+
name|leaderNode
argument_list|)
expr_stmt|;
name|SocketProxy
name|oldLeaderProxy
init|=
name|getProxyForReplica
argument_list|(
name|leader
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldLeaderProxy
operator|!=
literal|null
condition|)
block|{
name|oldLeaderProxy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No SocketProxy found for old leader node "
operator|+
name|leaderNode
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
comment|// give chance for new leader to be elected.
name|Replica
name|newLeader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|60000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No new leader was elected after 60 seconds; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|newLeader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected node "
operator|+
name|shouldNotBeNewLeaderNode
operator|+
literal|" to NOT be the new leader b/c it was out-of-sync with the old leader! ClusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
operator|!
name|shouldNotBeNewLeaderNode
operator|.
name|equals
argument_list|(
name|newLeader
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|proxy0
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|long
name|timeout
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|activeReps
init|=
name|getActiveOrRecoveringReplicas
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|activeReps
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Replica
argument_list|>
name|activeReps
init|=
name|getActiveOrRecoveringReplicas
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 2 of 3 replicas to be active but only found "
operator|+
name|activeReps
operator|.
name|size
argument_list|()
operator|+
literal|"; "
operator|+
name|activeReps
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|activeReps
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|assertDocsExistInAllReplicas
argument_list|(
name|activeReps
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
DECL|method|getActiveOrRecoveringReplicas
specifier|protected
name|List
argument_list|<
name|Replica
argument_list|>
name|getActiveOrRecoveringReplicas
parameter_list|(
name|String
name|testCollectionName
parameter_list|,
name|String
name|shardId
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|activeReplicas
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
argument_list|()
decl_stmt|;
name|ZkStateReader
name|zkr
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|cs
init|=
name|zkr
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|cs
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|shard
range|:
name|cs
operator|.
name|getActiveSlices
argument_list|(
name|testCollectionName
argument_list|)
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|shardId
argument_list|)
condition|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|shard
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|replicaState
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|ZkStateReader
operator|.
name|ACTIVE
operator|.
name|equals
argument_list|(
name|replicaState
argument_list|)
operator|||
name|ZkStateReader
operator|.
name|RECOVERING
operator|.
name|equals
argument_list|(
name|replicaState
argument_list|)
condition|)
block|{
name|activeReplicas
operator|.
name|put
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
operator|new
name|ArrayList
argument_list|<
name|Replica
argument_list|>
argument_list|()
decl_stmt|;
name|replicas
operator|.
name|addAll
argument_list|(
name|activeReplicas
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|replicas
return|;
block|}
DECL|method|getProxyForReplica
specifier|protected
name|SocketProxy
name|getProxyForReplica
parameter_list|(
name|Replica
name|replica
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|replicaBaseUrl
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|replicaBaseUrl
argument_list|)
expr_stmt|;
name|URL
name|baseUrl
init|=
operator|new
name|URL
argument_list|(
name|replicaBaseUrl
argument_list|)
decl_stmt|;
name|SocketProxy
name|proxy
init|=
name|proxies
operator|.
name|get
argument_list|(
name|baseUrl
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxy
operator|==
literal|null
operator|&&
operator|!
name|baseUrl
operator|.
name|toExternalForm
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|baseUrl
operator|=
operator|new
name|URL
argument_list|(
name|baseUrl
operator|.
name|toExternalForm
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|proxies
operator|.
name|get
argument_list|(
name|baseUrl
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"No proxy found for "
operator|+
name|baseUrl
operator|+
literal|"!"
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
return|return
name|proxy
return|;
block|}
DECL|method|assertDocsExistInAllReplicas
specifier|protected
name|void
name|assertDocsExistInAllReplicas
parameter_list|(
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
parameter_list|,
name|String
name|testCollectionName
parameter_list|,
name|int
name|firstDocId
parameter_list|,
name|int
name|lastDocId
parameter_list|)
throws|throws
name|Exception
block|{
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|leaderSolr
init|=
name|getHttpSolrServer
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HttpSolrServer
argument_list|>
name|replicas
init|=
operator|new
name|ArrayList
argument_list|<
name|HttpSolrServer
argument_list|>
argument_list|(
name|notLeaders
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Replica
name|r
range|:
name|notLeaders
control|)
block|{
name|replicas
operator|.
name|add
argument_list|(
name|getHttpSolrServer
argument_list|(
name|r
argument_list|,
name|testCollectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|int
name|d
init|=
name|firstDocId
init|;
name|d
operator|<=
name|lastDocId
condition|;
name|d
operator|++
control|)
block|{
name|String
name|docId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertDocExists
argument_list|(
name|leaderSolr
argument_list|,
name|testCollectionName
argument_list|,
name|docId
argument_list|)
expr_stmt|;
for|for
control|(
name|HttpSolrServer
name|replicaSolr
range|:
name|replicas
control|)
block|{
name|assertDocExists
argument_list|(
name|replicaSolr
argument_list|,
name|testCollectionName
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|leaderSolr
operator|!=
literal|null
condition|)
block|{
name|leaderSolr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|HttpSolrServer
name|replicaSolr
range|:
name|replicas
control|)
block|{
name|replicaSolr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getHttpSolrServer
specifier|protected
name|HttpSolrServer
name|getHttpSolrServer
parameter_list|(
name|Replica
name|replica
parameter_list|,
name|String
name|coll
parameter_list|)
throws|throws
name|Exception
block|{
name|ZkCoreNodeProps
name|zkProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|zkProps
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"/"
operator|+
name|coll
decl_stmt|;
return|return
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
return|;
block|}
DECL|method|sendDoc
specifier|protected
name|void
name|sendDoc
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|Exception
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
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|docId
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**    * Query the real-time get handler for a specific doc by ID to verify it    * exists in the provided server.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|assertDocExists
specifier|protected
name|void
name|assertDocExists
parameter_list|(
name|HttpSolrServer
name|solr
parameter_list|,
name|String
name|coll
parameter_list|,
name|String
name|docId
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
name|docId
argument_list|)
argument_list|)
decl_stmt|;
name|NamedList
name|rsp
init|=
name|solr
operator|.
name|request
argument_list|(
name|qr
argument_list|)
decl_stmt|;
name|String
name|match
init|=
name|JSONTestUtil
operator|.
name|matchObj
argument_list|(
literal|"/id"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
argument_list|,
operator|new
name|Integer
argument_list|(
name|docId
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Doc with id="
operator|+
name|docId
operator|+
literal|" not found in "
operator|+
name|solr
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|" due to: "
operator|+
name|match
argument_list|,
name|match
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|getJettyOnPort
specifier|protected
name|JettySolrRunner
name|getJettyOnPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|JettySolrRunner
name|theJetty
init|=
literal|null
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
if|if
condition|(
name|port
operator|==
name|jetty
operator|.
name|getLocalPort
argument_list|()
condition|)
block|{
name|theJetty
operator|=
name|jetty
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|theJetty
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|controlJetty
operator|.
name|getLocalPort
argument_list|()
operator|==
name|port
condition|)
block|{
name|theJetty
operator|=
name|controlJetty
expr_stmt|;
block|}
block|}
if|if
condition|(
name|theJetty
operator|==
literal|null
condition|)
name|fail
argument_list|(
literal|"Not able to find JettySolrRunner for port: "
operator|+
name|port
argument_list|)
expr_stmt|;
return|return
name|theJetty
return|;
block|}
DECL|method|getReplicaPort
specifier|protected
name|int
name|getReplicaPort
parameter_list|(
name|Replica
name|replica
parameter_list|)
block|{
name|String
name|replicaNode
init|=
name|replica
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|String
name|tmp
init|=
name|replicaNode
operator|.
name|substring
argument_list|(
name|replicaNode
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
name|tmp
operator|=
name|tmp
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|tmp
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmp
argument_list|)
return|;
block|}
block|}
end_class

end_unit

