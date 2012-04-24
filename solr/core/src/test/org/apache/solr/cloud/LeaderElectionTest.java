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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|io
operator|.
name|IOException
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
name|Executors
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
name|ScheduledExecutorService
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
name|cloud
operator|.
name|OnReconnect
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
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
operator|.
name|NoNodeException
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
name|Test
import|;
end_import

begin_class
DECL|class|LeaderElectionTest
specifier|public
class|class
name|LeaderElectionTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|30000
decl_stmt|;
DECL|field|server
specifier|private
name|ZkTestServer
name|server
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|seqToThread
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Thread
argument_list|>
name|seqToThread
decl_stmt|;
DECL|field|stopStress
specifier|private
specifier|volatile
name|boolean
name|stopStress
init|=
literal|false
decl_stmt|;
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
name|createTempDir
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
block|{    }
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
name|dataDir
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
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|setTheTickTime
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
name|zkStateReader
operator|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|seqToThread
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Thread
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|ClientThread
class|class
name|ClientThread
extends|extends
name|Thread
block|{
DECL|field|zkClient
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|nodeNumber
specifier|private
name|int
name|nodeNumber
decl_stmt|;
DECL|field|seq
specifier|private
specifier|volatile
name|int
name|seq
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|stop
specifier|private
specifier|volatile
name|boolean
name|stop
decl_stmt|;
DECL|field|electionDone
specifier|private
specifier|volatile
name|boolean
name|electionDone
init|=
literal|false
decl_stmt|;
DECL|field|props
specifier|private
specifier|final
name|ZkNodeProps
name|props
decl_stmt|;
DECL|method|ClientThread
specifier|public
name|ClientThread
parameter_list|(
name|int
name|nodeNumber
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
literal|"Thread-"
operator|+
name|nodeNumber
argument_list|)
expr_stmt|;
name|props
operator|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nodeNumber
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|TIMEOUT
argument_list|,
operator|new
name|OnReconnect
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|command
parameter_list|()
block|{
try|try
block|{
name|setupOnConnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{           }
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeNumber
operator|=
name|nodeNumber
expr_stmt|;
block|}
DECL|method|setupOnConnect
specifier|private
name|void
name|setupOnConnect
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|KeeperException
throws|,
name|IOException
block|{
name|ZkStateReader
name|zkStateReader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|LeaderElector
name|elector
init|=
operator|new
name|LeaderElector
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|ShardLeaderElectionContextBase
name|context
init|=
operator|new
name|ShardLeaderElectionContextBase
argument_list|(
name|elector
argument_list|,
literal|"shard1"
argument_list|,
literal|"collection1"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nodeNumber
argument_list|)
argument_list|,
name|props
argument_list|,
name|zkStateReader
argument_list|)
decl_stmt|;
name|elector
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|seq
operator|=
name|elector
operator|.
name|joinElection
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|electionDone
operator|=
literal|true
expr_stmt|;
name|seqToThread
operator|.
name|put
argument_list|(
name|seq
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|setupOnConnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// e.printStackTrace();
block|}
while|while
condition|(
operator|!
name|stop
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|stop
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getSeq
specifier|public
name|int
name|getSeq
parameter_list|()
block|{
return|return
name|seq
return|;
block|}
DECL|method|getNodeNumber
specifier|public
name|int
name|getNodeNumber
parameter_list|()
block|{
return|return
name|nodeNumber
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|LeaderElector
name|elector
init|=
operator|new
name|LeaderElector
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|props
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
literal|"http://127.0.0.1/solr/"
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|ElectionContext
name|context
init|=
operator|new
name|ShardLeaderElectionContextBase
argument_list|(
name|elector
argument_list|,
literal|"shard2"
argument_list|,
literal|"collection1"
argument_list|,
literal|"dummynode1"
argument_list|,
name|props
argument_list|,
name|zkStateReader
argument_list|)
decl_stmt|;
name|elector
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|elector
operator|.
name|joinElection
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://127.0.0.1/solr/"
argument_list|,
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCancelElection
specifier|public
name|void
name|testCancelElection
parameter_list|()
throws|throws
name|Exception
block|{
name|LeaderElector
name|first
init|=
operator|new
name|LeaderElector
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|props
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
literal|"http://127.0.0.1/solr/"
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|ElectionContext
name|firstContext
init|=
operator|new
name|ShardLeaderElectionContextBase
argument_list|(
name|first
argument_list|,
literal|"slice1"
argument_list|,
literal|"collection2"
argument_list|,
literal|"dummynode1"
argument_list|,
name|props
argument_list|,
name|zkStateReader
argument_list|)
decl_stmt|;
name|first
operator|.
name|setup
argument_list|(
name|firstContext
argument_list|)
expr_stmt|;
name|first
operator|.
name|joinElection
argument_list|(
name|firstContext
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"original leader was not registered"
argument_list|,
literal|"http://127.0.0.1/solr/1/"
argument_list|,
name|getLeaderUrl
argument_list|(
literal|"collection2"
argument_list|,
literal|"slice1"
argument_list|)
argument_list|)
expr_stmt|;
name|LeaderElector
name|second
init|=
operator|new
name|LeaderElector
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|props
operator|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
literal|"http://127.0.0.1/solr/"
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|ElectionContext
name|context
init|=
operator|new
name|ShardLeaderElectionContextBase
argument_list|(
name|second
argument_list|,
literal|"slice1"
argument_list|,
literal|"collection2"
argument_list|,
literal|"dummynode1"
argument_list|,
name|props
argument_list|,
name|zkStateReader
argument_list|)
decl_stmt|;
name|second
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|second
operator|.
name|joinElection
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"original leader should have stayed leader"
argument_list|,
literal|"http://127.0.0.1/solr/1/"
argument_list|,
name|getLeaderUrl
argument_list|(
literal|"collection2"
argument_list|,
literal|"slice1"
argument_list|)
argument_list|)
expr_stmt|;
name|firstContext
operator|.
name|cancelElection
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new leader was not registered"
argument_list|,
literal|"http://127.0.0.1/solr/2/"
argument_list|,
name|getLeaderUrl
argument_list|(
literal|"collection2"
argument_list|,
literal|"slice1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getLeaderUrl
specifier|private
name|String
name|getLeaderUrl
parameter_list|(
specifier|final
name|String
name|collection
parameter_list|,
specifier|final
name|String
name|slice
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|int
name|iterCount
init|=
literal|60
decl_stmt|;
while|while
condition|(
name|iterCount
operator|--
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|getShardLeadersPath
argument_list|(
name|collection
argument_list|,
name|slice
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|leaderProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoNodeException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not get leader props"
argument_list|)
throw|;
block|}
annotation|@
name|Test
DECL|method|testElection
specifier|public
name|void
name|testElection
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ClientThread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|ClientThread
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
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|ClientThread
name|thread
init|=
operator|new
name|ClientThread
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
comment|//wait for election to complete
name|int
name|doneCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ClientThread
name|thread
range|:
name|threads
control|)
block|{
if|if
condition|(
name|thread
operator|.
name|electionDone
condition|)
block|{
name|doneCount
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doneCount
operator|==
literal|15
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|int
name|leaderThread
init|=
name|getLeaderThread
argument_list|()
decl_stmt|;
comment|// whoever the leader is, should be the n_0 seq
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|threads
operator|.
name|get
argument_list|(
name|leaderThread
argument_list|)
operator|.
name|seq
argument_list|)
expr_stmt|;
comment|// kill n_0, 1, 3 and 4
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|waitForLeader
argument_list|(
name|threads
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|leaderThread
operator|=
name|getLeaderThread
argument_list|()
expr_stmt|;
comment|// whoever the leader is, should be the n_1 seq
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|threads
operator|.
name|get
argument_list|(
name|leaderThread
argument_list|)
operator|.
name|seq
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// whoever the leader is, should be the n_2 seq
name|waitForLeader
argument_list|(
name|threads
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|leaderThread
operator|=
name|getLeaderThread
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|threads
operator|.
name|get
argument_list|(
name|leaderThread
argument_list|)
operator|.
name|seq
argument_list|)
expr_stmt|;
comment|// kill n_5, 2, 6, 7, and 8
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|ClientThread
operator|)
name|seqToThread
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|waitForLeader
argument_list|(
name|threads
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|leaderThread
operator|=
name|getLeaderThread
argument_list|()
expr_stmt|;
comment|// whoever the leader is, should be the n_9 seq
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|threads
operator|.
name|get
argument_list|(
name|leaderThread
argument_list|)
operator|.
name|seq
argument_list|)
expr_stmt|;
comment|// cleanup any threads still running
for|for
control|(
name|ClientThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|close
argument_list|()
expr_stmt|;
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|waitForLeader
specifier|private
name|void
name|waitForLeader
parameter_list|(
name|List
argument_list|<
name|ClientThread
argument_list|>
name|threads
parameter_list|,
name|int
name|seq
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|int
name|leaderThread
decl_stmt|;
name|int
name|tries
init|=
literal|0
decl_stmt|;
name|leaderThread
operator|=
name|getLeaderThread
argument_list|()
expr_stmt|;
while|while
condition|(
name|threads
operator|.
name|get
argument_list|(
name|leaderThread
argument_list|)
operator|.
name|seq
operator|<
name|seq
condition|)
block|{
name|leaderThread
operator|=
name|getLeaderThread
argument_list|()
expr_stmt|;
if|if
condition|(
name|tries
operator|++
operator|>
literal|50
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLeaderThread
specifier|private
name|int
name|getLeaderThread
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|String
name|leaderUrl
init|=
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|leaderUrl
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testStressElection
specifier|public
name|void
name|testStressElection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ScheduledExecutorService
name|scheduler
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ClientThread
argument_list|>
name|threads
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ClientThread
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// start with a leader
name|ClientThread
name|thread1
init|=
literal|null
decl_stmt|;
name|thread1
operator|=
operator|new
name|ClientThread
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread1
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
name|thread1
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Thread
name|scheduleThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|count
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|launchIn
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|ClientThread
name|thread
init|=
literal|null
decl_stmt|;
try|try
block|{
name|thread
operator|=
operator|new
name|ClientThread
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//
block|}
if|if
condition|(
name|thread
operator|!=
literal|null
condition|)
block|{
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|schedule
argument_list|(
name|thread
argument_list|,
name|launchIn
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|killThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stopStress
condition|)
block|{
try|try
block|{
name|int
name|j
decl_stmt|;
try|try
block|{
comment|// always 1 we won't kill...
name|j
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|threads
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
continue|continue;
block|}
try|try
block|{
name|threads
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                            }
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{            }
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|connLossThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stopStress
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|int
name|j
decl_stmt|;
name|j
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|threads
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|threads
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|pauseCnxn
argument_list|(
name|ZkTestServer
operator|.
name|TICK_TIME
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                        }
block|}
block|}
block|}
decl_stmt|;
name|scheduleThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|connLossThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|killThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|stopStress
operator|=
literal|true
expr_stmt|;
name|scheduleThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|connLossThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|killThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|scheduleThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|connLossThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|killThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|int
name|seq
init|=
name|threads
operator|.
name|get
argument_list|(
name|getLeaderThread
argument_list|()
argument_list|)
operator|.
name|getSeq
argument_list|()
decl_stmt|;
comment|// we have a leader we know, TODO: lets check some other things
comment|// cleanup any threads still running
for|for
control|(
name|ClientThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
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
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|printLayout
specifier|private
name|void
name|printLayout
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
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

