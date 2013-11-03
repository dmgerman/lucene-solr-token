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
name|Random
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
operator|.
name|CloudJettyRunner
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
name|solr
operator|.
name|core
operator|.
name|CoreContainer
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
name|SolrCore
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
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|FilterHolder
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
comment|/**  * The monkey can stop random or specific jetties used with SolrCloud.  *   * It can also run in a background thread and start and stop jetties  * randomly.  * TODO: expire multiple sessions / connectionloss at once  * TODO: kill multiple jetties at once  * TODO: ? add random headhunter mode that always kills the leader  * TODO: chaosmonkey should be able to do cluster stop/start tests  */
end_comment

begin_class
DECL|class|ChaosMonkey
specifier|public
class|class
name|ChaosMonkey
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
name|ChaosMonkey
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONLOSS_PERCENT
specifier|private
specifier|static
specifier|final
name|int
name|CONLOSS_PERCENT
init|=
literal|10
decl_stmt|;
comment|// 0 - 10 = 0 - 100%
DECL|field|EXPIRE_PERCENT
specifier|private
specifier|static
specifier|final
name|int
name|EXPIRE_PERCENT
init|=
literal|10
decl_stmt|;
comment|// 0 - 10 = 0 - 100%
DECL|field|shardToJetty
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
argument_list|>
name|shardToJetty
decl_stmt|;
DECL|field|MONKEY_ENABLED
specifier|private
specifier|static
specifier|final
name|Boolean
name|MONKEY_ENABLED
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.enabled"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|CONN_LOSS
specifier|private
specifier|static
specifier|final
name|Boolean
name|CONN_LOSS
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.connloss"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|EXP
specifier|private
specifier|static
specifier|final
name|Boolean
name|EXP
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.exp"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|private
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
decl_stmt|;
DECL|field|stop
specifier|private
specifier|volatile
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
DECL|field|stops
specifier|private
name|AtomicInteger
name|stops
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|starts
specifier|private
name|AtomicInteger
name|starts
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|expires
specifier|private
name|AtomicInteger
name|expires
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|connloss
specifier|private
name|AtomicInteger
name|connloss
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|shardToClient
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|SolrServer
argument_list|>
argument_list|>
name|shardToClient
decl_stmt|;
DECL|field|expireSessions
specifier|private
name|boolean
name|expireSessions
decl_stmt|;
DECL|field|causeConnectionLoss
specifier|private
name|boolean
name|causeConnectionLoss
decl_stmt|;
DECL|field|aggressivelyKillLeaders
specifier|private
name|boolean
name|aggressivelyKillLeaders
decl_stmt|;
DECL|field|shardToLeaderJetty
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|CloudJettyRunner
argument_list|>
name|shardToLeaderJetty
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|monkeyThread
specifier|private
name|Thread
name|monkeyThread
decl_stmt|;
DECL|method|ChaosMonkey
specifier|public
name|ChaosMonkey
parameter_list|(
name|ZkTestServer
name|zkServer
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
argument_list|>
name|shardToJetty
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CloudJettyRunner
argument_list|>
name|shardToLeaderJetty
parameter_list|)
block|{
name|this
operator|.
name|shardToJetty
operator|=
name|shardToJetty
expr_stmt|;
name|this
operator|.
name|shardToLeaderJetty
operator|=
name|shardToLeaderJetty
expr_stmt|;
name|this
operator|.
name|zkServer
operator|=
name|zkServer
expr_stmt|;
name|this
operator|.
name|zkStateReader
operator|=
name|zkStateReader
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
if|if
condition|(
operator|!
name|MONKEY_ENABLED
condition|)
block|{
name|monkeyLog
argument_list|(
literal|"The Monkey is Disabled and will not run"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Random
name|random
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
name|EXP
operator|!=
literal|null
condition|)
block|{
name|expireSessions
operator|=
name|EXP
expr_stmt|;
block|}
else|else
block|{
name|expireSessions
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|CONN_LOSS
operator|!=
literal|null
condition|)
block|{
name|causeConnectionLoss
operator|=
name|CONN_LOSS
expr_stmt|;
block|}
else|else
block|{
name|causeConnectionLoss
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
name|monkeyLog
argument_list|(
literal|"init - expire sessions:"
operator|+
name|expireSessions
operator|+
literal|" cause connection loss:"
operator|+
name|causeConnectionLoss
argument_list|)
expr_stmt|;
block|}
comment|// TODO: expire all clients at once?
DECL|method|expireSession
specifier|public
name|void
name|expireSession
parameter_list|(
specifier|final
name|JettySolrRunner
name|jetty
parameter_list|)
block|{
name|monkeyLog
argument_list|(
literal|"expire session for "
operator|+
name|jetty
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|" !"
argument_list|)
expr_stmt|;
name|SolrDispatchFilter
name|solrDispatchFilter
init|=
operator|(
name|SolrDispatchFilter
operator|)
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|solrDispatchFilter
operator|!=
literal|null
condition|)
block|{
name|CoreContainer
name|cores
init|=
name|solrDispatchFilter
operator|.
name|getCores
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|causeConnectionLoss
argument_list|(
name|jetty
argument_list|,
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getClientTimeout
argument_list|()
operator|+
literal|200
argument_list|)
expr_stmt|;
block|}
block|}
comment|//    Thread thread = new Thread() {
comment|//      {
comment|//        setDaemon(true);
comment|//      }
comment|//      public void run() {
comment|//        SolrDispatchFilter solrDispatchFilter = (SolrDispatchFilter) jetty.getDispatchFilter().getFilter();
comment|//        if (solrDispatchFilter != null) {
comment|//          CoreContainer cores = solrDispatchFilter.getCores();
comment|//          if (cores != null) {
comment|//            try {
comment|//              Thread.sleep(ZkTestServer.TICK_TIME * 2 + 800);
comment|//            } catch (InterruptedException e) {
comment|//              // we act as only connection loss
comment|//              return;
comment|//            }
comment|//            long sessionId = cores.getZkController().getZkClient().getSolrZooKeeper().getSessionId();
comment|//            zkServer.expire(sessionId);
comment|//          }
comment|//        }
comment|//      }
comment|//    };
comment|//    thread.start();
block|}
DECL|method|expireRandomSession
specifier|public
name|void
name|expireRandomSession
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|String
name|sliceName
init|=
name|getRandomSlice
argument_list|()
decl_stmt|;
name|CloudJettyRunner
name|jetty
init|=
name|getRandomJetty
argument_list|(
name|sliceName
argument_list|,
name|aggressivelyKillLeaders
argument_list|)
decl_stmt|;
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
block|{
name|expireSession
argument_list|(
name|jetty
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|expires
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|randomConnectionLoss
specifier|public
name|void
name|randomConnectionLoss
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|monkeyLog
argument_list|(
literal|"cause connection loss!"
argument_list|)
expr_stmt|;
name|String
name|sliceName
init|=
name|getRandomSlice
argument_list|()
decl_stmt|;
name|CloudJettyRunner
name|jetty
init|=
name|getRandomJetty
argument_list|(
name|sliceName
argument_list|,
name|aggressivelyKillLeaders
argument_list|)
decl_stmt|;
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
block|{
name|causeConnectionLoss
argument_list|(
name|jetty
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|connloss
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|causeConnectionLoss
specifier|public
specifier|static
name|void
name|causeConnectionLoss
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|)
block|{
name|causeConnectionLoss
argument_list|(
name|jetty
argument_list|,
name|ZkTestServer
operator|.
name|TICK_TIME
operator|*
literal|2
operator|+
literal|200
argument_list|)
expr_stmt|;
block|}
DECL|method|causeConnectionLoss
specifier|public
specifier|static
name|void
name|causeConnectionLoss
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|,
name|int
name|pauseTime
parameter_list|)
block|{
name|SolrDispatchFilter
name|solrDispatchFilter
init|=
operator|(
name|SolrDispatchFilter
operator|)
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|solrDispatchFilter
operator|!=
literal|null
condition|)
block|{
name|CoreContainer
name|cores
init|=
name|solrDispatchFilter
operator|.
name|getCores
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|SolrZkClient
name|zkClient
init|=
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
comment|// must be at least double tick time...
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|pauseCnxn
argument_list|(
name|pauseTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|stopShard
specifier|public
name|CloudJettyRunner
name|stopShard
parameter_list|(
name|String
name|slice
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|Exception
block|{
name|CloudJettyRunner
name|cjetty
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|stopJetty
argument_list|(
name|cjetty
argument_list|)
expr_stmt|;
return|return
name|cjetty
return|;
block|}
DECL|method|stopJetty
specifier|public
name|void
name|stopJetty
parameter_list|(
name|CloudJettyRunner
name|cjetty
parameter_list|)
throws|throws
name|Exception
block|{
name|stop
argument_list|(
name|cjetty
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|stops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|killJetty
specifier|public
name|void
name|killJetty
parameter_list|(
name|CloudJettyRunner
name|cjetty
parameter_list|)
throws|throws
name|Exception
block|{
name|kill
argument_list|(
name|cjetty
argument_list|)
expr_stmt|;
name|stops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|stopJetty
specifier|public
name|void
name|stopJetty
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|)
throws|throws
name|Exception
block|{
name|stops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|stopJettySolrRunner
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
DECL|method|stopJettySolrRunner
specifier|private
specifier|static
name|void
name|stopJettySolrRunner
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
operator|(
name|jetty
operator|!=
literal|null
operator|)
assert|;
name|monkeyLog
argument_list|(
literal|"stop shard! "
operator|+
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
comment|// get a clean shutdown so that no dirs are left open...
name|FilterHolder
name|fh
init|=
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|fh
operator|!=
literal|null
condition|)
block|{
name|SolrDispatchFilter
name|sdf
init|=
operator|(
name|SolrDispatchFilter
operator|)
name|fh
operator|.
name|getFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|sdf
operator|!=
literal|null
condition|)
block|{
name|sdf
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|jetty
operator|.
name|isStopped
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"could not stop jetty"
argument_list|)
throw|;
block|}
block|}
DECL|method|kill
specifier|public
specifier|static
name|void
name|kill
parameter_list|(
name|CloudJettyRunner
name|cjetty
parameter_list|)
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|jetty
init|=
name|cjetty
operator|.
name|jetty
decl_stmt|;
name|monkeyLog
argument_list|(
literal|"kill shard! "
operator|+
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|stop
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|jetty
operator|.
name|isStopped
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"could not kill jetty"
argument_list|)
throw|;
block|}
block|}
DECL|method|stopShard
specifier|public
name|void
name|stopShard
parameter_list|(
name|String
name|slice
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|jetty
range|:
name|jetties
control|)
block|{
name|stopJetty
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stopShardExcept
specifier|public
name|void
name|stopShardExcept
parameter_list|(
name|String
name|slice
parameter_list|,
name|String
name|shardName
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|jetty
range|:
name|jetties
control|)
block|{
if|if
condition|(
operator|!
name|jetty
operator|.
name|nodeName
operator|.
name|equals
argument_list|(
name|shardName
argument_list|)
condition|)
block|{
name|stopJetty
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getShard
specifier|public
name|JettySolrRunner
name|getShard
parameter_list|(
name|String
name|slice
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|jetty
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|jetty
decl_stmt|;
return|return
name|jetty
return|;
block|}
DECL|method|stopRandomShard
specifier|public
name|CloudJettyRunner
name|stopRandomShard
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sliceName
init|=
name|getRandomSlice
argument_list|()
decl_stmt|;
return|return
name|stopRandomShard
argument_list|(
name|sliceName
argument_list|)
return|;
block|}
DECL|method|stopRandomShard
specifier|public
name|CloudJettyRunner
name|stopRandomShard
parameter_list|(
name|String
name|slice
parameter_list|)
throws|throws
name|Exception
block|{
name|CloudJettyRunner
name|cjetty
init|=
name|getRandomJetty
argument_list|(
name|slice
argument_list|,
name|aggressivelyKillLeaders
argument_list|)
decl_stmt|;
if|if
condition|(
name|cjetty
operator|!=
literal|null
condition|)
block|{
name|stopJetty
argument_list|(
name|cjetty
argument_list|)
expr_stmt|;
block|}
return|return
name|cjetty
return|;
block|}
DECL|method|killRandomShard
specifier|public
name|CloudJettyRunner
name|killRandomShard
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add all the shards to a list
name|String
name|sliceName
init|=
name|getRandomSlice
argument_list|()
decl_stmt|;
return|return
name|killRandomShard
argument_list|(
name|sliceName
argument_list|)
return|;
block|}
DECL|method|getRandomSlice
specifier|private
name|String
name|getRandomSlice
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sliceKeyList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|slices
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|sliceKeyList
operator|.
name|addAll
argument_list|(
name|slices
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|sliceName
init|=
name|sliceKeyList
operator|.
name|get
argument_list|(
name|LuceneTestCase
operator|.
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sliceKeyList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|sliceName
return|;
block|}
DECL|method|killRandomShard
specifier|public
name|CloudJettyRunner
name|killRandomShard
parameter_list|(
name|String
name|slice
parameter_list|)
throws|throws
name|Exception
block|{
name|CloudJettyRunner
name|cjetty
init|=
name|getRandomJetty
argument_list|(
name|slice
argument_list|,
name|aggressivelyKillLeaders
argument_list|)
decl_stmt|;
if|if
condition|(
name|cjetty
operator|!=
literal|null
condition|)
block|{
name|killJetty
argument_list|(
name|cjetty
argument_list|)
expr_stmt|;
block|}
return|return
name|cjetty
return|;
block|}
DECL|method|getRandomJetty
specifier|public
name|CloudJettyRunner
name|getRandomJetty
parameter_list|(
name|String
name|slice
parameter_list|,
name|boolean
name|aggressivelyKillLeaders
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|int
name|numRunning
init|=
literal|0
decl_stmt|;
name|int
name|numRecovering
init|=
literal|0
decl_stmt|;
name|int
name|numActive
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cloudJetty
range|:
name|shardToJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
control|)
block|{
name|boolean
name|running
init|=
literal|true
decl_stmt|;
comment|// get latest cloud state
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Slice
name|theShards
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
operator|.
name|get
argument_list|(
name|slice
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|props
init|=
name|theShards
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|get
argument_list|(
name|cloudJetty
operator|.
name|coreNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"shard name "
operator|+
name|cloudJetty
operator|.
name|coreNodeName
operator|+
literal|" not found in "
operator|+
name|theShards
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|state
init|=
name|props
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|props
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cloudJetty
operator|.
name|jetty
operator|.
name|isRunning
argument_list|()
operator|||
operator|!
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
operator|||
operator|!
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|cloudJetty
operator|.
name|jetty
operator|.
name|isRunning
argument_list|()
operator|&&
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|RECOVERING
argument_list|)
operator|&&
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|numRecovering
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|cloudJetty
operator|.
name|jetty
operator|.
name|isRunning
argument_list|()
operator|&&
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
operator|&&
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|numActive
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|running
condition|)
block|{
name|numRunning
operator|++
expr_stmt|;
block|}
block|}
comment|// TODO: stale state makes this a tough call
if|if
condition|(
name|numActive
operator|<
literal|2
condition|)
block|{
comment|// we cannot kill anyone
name|monkeyLog
argument_list|(
literal|"only one active node in shard - monkey cannot kill :("
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Random
name|random
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
decl_stmt|;
name|int
name|chance
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|CloudJettyRunner
name|cjetty
decl_stmt|;
if|if
condition|(
name|chance
operator|<=
literal|5
operator|&&
name|aggressivelyKillLeaders
condition|)
block|{
comment|// if killLeader, really aggressively go after leaders
name|cjetty
operator|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// get random shard
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|slice
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|jetties
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|cjetty
operator|=
name|jetties
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|leader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|leader
operator|=
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
name|collection
argument_list|,
name|slice
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not get leader"
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|FilterHolder
name|fh
init|=
name|cjetty
operator|.
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|fh
operator|==
literal|null
condition|)
block|{
name|monkeyLog
argument_list|(
literal|"selected jetty not running correctly - skip"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|SolrDispatchFilter
name|df
init|=
operator|(
operator|(
name|SolrDispatchFilter
operator|)
name|fh
operator|.
name|getFilter
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|df
operator|==
literal|null
condition|)
block|{
name|monkeyLog
argument_list|(
literal|"selected jetty not running correctly - skip"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|CoreContainer
name|cores
init|=
name|df
operator|.
name|getCores
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
operator|==
literal|null
condition|)
block|{
name|monkeyLog
argument_list|(
literal|"selected jetty not running correctly - skip"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|leader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|monkeyLog
argument_list|(
literal|"selected jetty not running correctly - skip"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// cluster state can be stale - also go by our 'near real-time' is leader prop
name|boolean
name|rtIsLeader
decl_stmt|;
try|try
block|{
name|rtIsLeader
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|isLeader
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|boolean
name|isLeader
init|=
name|leader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
operator|.
name|equals
argument_list|(
name|jetties
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|nodeName
argument_list|)
operator|||
name|rtIsLeader
decl_stmt|;
if|if
condition|(
operator|!
name|aggressivelyKillLeaders
operator|&&
name|isLeader
condition|)
block|{
comment|// we don't kill leaders...
name|monkeyLog
argument_list|(
literal|"abort! I don't kill leaders"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|cjetty
operator|.
name|jetty
operator|.
name|getLocalPort
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|// we can't kill the dead
name|monkeyLog
argument_list|(
literal|"abort! This guy is already dead"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|//System.out.println("num active:" + numActive + " for " + slice + " sac:" + jetty.getLocalPort());
name|monkeyLog
argument_list|(
literal|"chose a victim! "
operator|+
name|cjetty
operator|.
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cjetty
return|;
block|}
DECL|method|getRandomClient
specifier|public
name|SolrServer
name|getRandomClient
parameter_list|(
name|String
name|slice
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|// get latest cloud state
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// get random shard
name|List
argument_list|<
name|SolrServer
argument_list|>
name|clients
init|=
name|shardToClient
operator|.
name|get
argument_list|(
name|slice
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|SolrServer
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|client
return|;
block|}
comment|// synchronously starts and stops shards randomly, unless there is only one
comment|// active shard up for a slice or if there is one active and others recovering
DECL|method|startTheMonkey
specifier|public
name|void
name|startTheMonkey
parameter_list|(
name|boolean
name|killLeaders
parameter_list|,
specifier|final
name|int
name|roundPauseUpperLimit
parameter_list|)
block|{
if|if
condition|(
operator|!
name|MONKEY_ENABLED
condition|)
block|{
name|monkeyLog
argument_list|(
literal|"The Monkey is disabled and will not start"
argument_list|)
expr_stmt|;
return|return;
block|}
name|monkeyLog
argument_list|(
literal|"starting"
argument_list|)
expr_stmt|;
name|this
operator|.
name|aggressivelyKillLeaders
operator|=
name|killLeaders
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|// TODO: when kill leaders is on, lets kill a higher percentage of leaders
name|stop
operator|=
literal|false
expr_stmt|;
name|monkeyThread
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|private
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|deadPool
init|=
operator|new
name|ArrayList
argument_list|<
name|CloudJettyRunner
argument_list|>
argument_list|()
decl_stmt|;
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
name|stop
condition|)
block|{
try|try
block|{
name|Random
name|random
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|roundPauseUpperLimit
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|deadPool
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|index
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|deadPool
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|JettySolrRunner
name|jetty
init|=
name|deadPool
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|jetty
decl_stmt|;
if|if
condition|(
operator|!
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|jetty
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|//System.out.println("started on port:" + jetty.getLocalPort());
name|deadPool
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|starts
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
name|int
name|rnd
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|expireSessions
operator|&&
name|rnd
operator|<
name|EXPIRE_PERCENT
condition|)
block|{
name|expireRandomSession
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|causeConnectionLoss
operator|&&
name|rnd
operator|<
name|CONLOSS_PERCENT
condition|)
block|{
name|randomConnectionLoss
argument_list|()
expr_stmt|;
block|}
name|CloudJettyRunner
name|cjetty
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|cjetty
operator|=
name|stopRandomShard
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cjetty
operator|=
name|killRandomShard
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cjetty
operator|==
literal|null
condition|)
block|{
comment|// we cannot kill
block|}
else|else
block|{
name|deadPool
operator|.
name|add
argument_list|(
name|cjetty
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|monkeyLog
argument_list|(
literal|"finished"
argument_list|)
expr_stmt|;
name|monkeyLog
argument_list|(
literal|"I ran for "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|/
literal|1000.0f
operator|+
literal|"sec. I stopped "
operator|+
name|stops
operator|+
literal|" and I started "
operator|+
name|starts
operator|+
literal|". I also expired "
operator|+
name|expires
operator|.
name|get
argument_list|()
operator|+
literal|" and caused "
operator|+
name|connloss
operator|+
literal|" connection losses"
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|monkeyThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|monkeyLog
specifier|public
specifier|static
name|void
name|monkeyLog
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"monkey: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|stopTheMonkey
specifier|public
name|void
name|stopTheMonkey
parameter_list|()
block|{
name|stop
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|monkeyThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getStarts
specifier|public
name|int
name|getStarts
parameter_list|()
block|{
return|return
name|starts
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|stop
specifier|public
specifier|static
name|void
name|stop
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|)
throws|throws
name|Exception
block|{
name|stopJettySolrRunner
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
DECL|method|start
specifier|public
specifier|static
name|boolean
name|start
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
try|try
block|{
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
try|try
block|{
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e3
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not get the port to start jetty again"
argument_list|,
name|e3
argument_list|)
expr_stmt|;
comment|// we coud not get the port
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

