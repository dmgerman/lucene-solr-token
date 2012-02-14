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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|CloudState
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
name|ZkNodeProps
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
name|AfterClass
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

begin_class
DECL|class|AbstractDistributedZkTestCase
specifier|public
specifier|abstract
class|class
name|AbstractDistributedZkTestCase
extends|extends
name|BaseDistributedSearchTestCase
block|{
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
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|createTempDir
argument_list|()
expr_stmt|;
name|ignoreException
argument_list|(
literal|"java.nio.channels.ClosedChannelException"
argument_list|)
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
literal|"zkHost"
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
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"remove.version.field"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.StandardDirectoryFactory"
argument_list|)
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"collection"
argument_list|,
literal|"control_collection"
argument_list|)
expr_stmt|;
name|controlJetty
operator|=
name|createJetty
argument_list|(
name|testDir
argument_list|,
name|testDir
operator|+
literal|"/control/data"
argument_list|,
literal|"control_shard"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
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
name|JettySolrRunner
name|j
init|=
name|createJetty
argument_list|(
name|testDir
argument_list|,
name|testDir
operator|+
literal|"/jetty"
operator|+
name|i
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
literal|"localhost:"
argument_list|)
operator|.
name|append
argument_list|(
name|j
operator|.
name|getLocalPort
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|context
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
name|KeeperException
throws|,
name|InterruptedException
block|{
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
name|zkStateReader
argument_list|,
name|verbose
argument_list|,
literal|false
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
name|KeeperException
throws|,
name|InterruptedException
block|{
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
name|updateCloudState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CloudState
name|cloudState
init|=
name|zkStateReader
operator|.
name|getCloudState
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
name|cloudState
operator|.
name|getSlices
argument_list|(
name|collection
argument_list|)
decl_stmt|;
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
name|ZkNodeProps
argument_list|>
name|shards
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getShards
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
name|ZkNodeProps
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
literal|"rstate:"
operator|+
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|+
literal|" live:"
operator|+
name|cloudState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
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
name|get
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
operator|)
operator|&&
name|cloudState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|get
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
literal|15
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
name|failOnTimeout
condition|)
block|{
name|fail
argument_list|(
literal|"There are still nodes recoverying"
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"gave up waiting for recovery to finish.."
argument_list|)
expr_stmt|;
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
literal|2000
argument_list|)
expr_stmt|;
block|}
name|cnt
operator|++
expr_stmt|;
block|}
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
name|updateCloudState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CloudState
name|cloudState
init|=
name|zkStateReader
operator|.
name|getCloudState
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
name|cloudState
operator|.
name|getSlices
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
name|ZkNodeProps
argument_list|>
name|shards
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getShards
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
name|ZkNodeProps
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
name|get
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
literal|"Not all shards are ACTIVE"
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
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
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
literal|"enable.update.log"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"remove.version.field"
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
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
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
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
block|{   }
block|}
end_class

end_unit

