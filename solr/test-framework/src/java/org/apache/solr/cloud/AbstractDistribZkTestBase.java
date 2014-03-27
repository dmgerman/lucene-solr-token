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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|core
operator|.
name|Diagnostics
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
name|core
operator|.
name|MockDirectoryFactory
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
name|servlet
operator|.
name|SolrDispatchFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|AbstractDistribZkTestBase
specifier|public
specifier|abstract
class|class
name|AbstractDistribZkTestBase
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|REMOVE_VERSION_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|REMOVE_VERSION_FIELD
init|=
literal|"remove.version.field"
decl_stmt|;
DECL|field|ENABLE_UPDATE_LOG
specifier|private
specifier|static
specifier|final
name|String
name|ENABLE_UPDATE_LOG
init|=
literal|"enable.update.log"
decl_stmt|;
DECL|field|ZK_HOST
specifier|private
specifier|static
specifier|final
name|String
name|ZK_HOST
init|=
literal|"zkHost"
decl_stmt|;
DECL|field|ZOOKEEPER_FORCE_SYNC
specifier|private
specifier|static
specifier|final
name|String
name|ZOOKEEPER_FORCE_SYNC
init|=
literal|"zookeeper.forceSync"
decl_stmt|;
DECL|field|DEFAULT_COLLECTION
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_COLLECTION
init|=
literal|"collection1"
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|homeCount
specifier|private
name|AtomicInteger
name|homeCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeThisClass
specifier|public
specifier|static
name|void
name|beforeThisClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Only For Manual Testing: this will force an fs based dir factory
comment|//useFactory(null);
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
name|String
name|zkDir
init|=
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZK_HOST
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_UPDATE_LOG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|REMOVE_VERSION_FIELD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZOOKEEPER_FORCE_SYNC
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|MockDirectoryFactory
operator|.
name|SOLR_TESTS_ALLOW_READING_FILES_STILL_OPEN_FOR_WRITE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|schema
init|=
name|getSchemaFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
name|schema
operator|=
literal|"schema.xml"
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|getCloudSolrConfig
argument_list|()
argument_list|,
name|schema
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
block|}
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-tlog.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|createServers
specifier|protected
name|void
name|createServers
parameter_list|(
name|int
name|numShards
parameter_list|)
throws|throws
name|Exception
block|{
comment|// give everyone there own solrhome
name|File
name|controlHome
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"control"
operator|+
name|homeCount
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
argument_list|,
name|controlHome
argument_list|)
expr_stmt|;
name|setupJettySolrHome
argument_list|(
name|controlHome
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"collection"
argument_list|,
literal|"control_collection"
argument_list|)
expr_stmt|;
name|String
name|numShardsS
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|controlJetty
operator|=
name|createJetty
argument_list|(
name|controlHome
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// let the shardId default to shard1
name|System
operator|.
name|clearProperty
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
if|if
condition|(
name|numShardsS
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
name|numShardsS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|)
expr_stmt|;
block|}
name|controlClient
operator|=
name|createNewSolrServer
argument_list|(
name|controlJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numShards
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
comment|// give everyone there own solrhome
name|File
name|jettyHome
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"jetty"
operator|+
name|homeCount
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|setupJettySolrHome
argument_list|(
name|jettyHome
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|j
init|=
name|createJetty
argument_list|(
name|jettyHome
argument_list|,
literal|null
argument_list|,
literal|"shard"
operator|+
operator|(
name|i
operator|+
literal|2
operator|)
argument_list|)
decl_stmt|;
name|jettys
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|clients
operator|.
name|add
argument_list|(
name|createNewSolrServer
argument_list|(
name|j
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|buildUrl
argument_list|(
name|j
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shards
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// now wait till we see the leader for each shard
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numShards
condition|;
name|i
operator|++
control|)
block|{
name|ZkStateReader
name|zkStateReader
init|=
operator|(
operator|(
name|SolrDispatchFilter
operator|)
name|jettys
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
operator|)
operator|.
name|getCores
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard"
operator|+
operator|(
name|i
operator|+
literal|2
operator|)
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitForRecoveriesToFinish
specifier|protected
name|void
name|waitForRecoveriesToFinish
parameter_list|(
name|String
name|collection
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|boolean
name|verbose
parameter_list|)
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
name|zkStateReader
argument_list|,
name|verbose
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForRecoveriesToFinish
specifier|protected
name|void
name|waitForRecoveriesToFinish
parameter_list|(
name|String
name|collection
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|boolean
name|failOnTimeout
parameter_list|)
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
name|zkStateReader
argument_list|,
name|verbose
argument_list|,
name|failOnTimeout
argument_list|,
literal|330
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForRecoveriesToFinish
specifier|protected
name|void
name|waitForRecoveriesToFinish
parameter_list|(
name|String
name|collection
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|boolean
name|failOnTimeout
parameter_list|,
name|int
name|timeoutSeconds
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Wait for recoveries to finish - collection: "
operator|+
name|collection
operator|+
literal|" failOnTimeout:"
operator|+
name|failOnTimeout
operator|+
literal|" timeout (sec):"
operator|+
name|timeoutSeconds
argument_list|)
expr_stmt|;
name|boolean
name|cont
init|=
literal|true
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|cont
condition|)
block|{
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|boolean
name|sawLiveRecovering
init|=
literal|false
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
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Could not find collection:"
operator|+
name|collection
argument_list|,
name|slices
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|entry
range|:
name|slices
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shards
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shard
range|:
name|shards
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"replica:"
operator|+
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" rstate:"
operator|+
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|+
literal|" live:"
operator|+
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|shard
operator|.
name|getValue
argument_list|()
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
operator|(
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|RECOVERING
argument_list|)
operator|||
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|SYNC
argument_list|)
operator|||
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|DOWN
argument_list|)
operator|)
operator|&&
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|sawLiveRecovering
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|sawLiveRecovering
operator|||
name|cnt
operator|==
name|timeoutSeconds
condition|)
block|{
if|if
condition|(
operator|!
name|sawLiveRecovering
condition|)
block|{
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"no one is recoverying"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Gave up waiting for recovery to finish.."
argument_list|)
expr_stmt|;
if|if
condition|(
name|failOnTimeout
condition|)
block|{
name|Diagnostics
operator|.
name|logThreadDumps
argument_list|(
literal|"Gave up waiting for recovery to finish.  THREAD DUMP:"
argument_list|)
expr_stmt|;
name|printLayout
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"There are still nodes recoverying - waited for "
operator|+
name|timeoutSeconds
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
comment|// won't get here
return|return;
block|}
block|}
name|cont
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|cnt
operator|++
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Recoveries finished - collection: "
operator|+
name|collection
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAllActive
specifier|protected
name|void
name|assertAllActive
parameter_list|(
name|String
name|collection
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
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
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot find collection:"
operator|+
name|collection
argument_list|)
throw|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|entry
range|:
name|slices
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shards
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shard
range|:
name|shards
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|state
init|=
name|shard
operator|.
name|getValue
argument_list|()
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
operator|!
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Not all shards are ACTIVE - found a shard that is: "
operator|+
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
if|if
condition|(
name|DEBUG
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|clearProperty
argument_list|(
name|ZK_HOST
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_UPDATE_LOG
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|REMOVE_VERSION_FIELD
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
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
name|System
operator|.
name|clearProperty
argument_list|(
name|ZOOKEEPER_FORCE_SYNC
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|MockDirectoryFactory
operator|.
name|SOLR_TESTS_ALLOW_READING_FILES_STILL_OPEN_FOR_WRITE
argument_list|)
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|printLayout
specifier|protected
name|void
name|printLayout
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

