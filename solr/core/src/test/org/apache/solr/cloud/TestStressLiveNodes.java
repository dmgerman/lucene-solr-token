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
name|List
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
name|Callable
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
name|ExecutorService
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
name|cloud
operator|.
name|SolrCloudTestCase
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
name|util
operator|.
name|ExecutorUtil
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
name|CloudConfig
operator|.
name|CloudConfigBuilder
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
name|util
operator|.
name|DefaultSolrThreadFactory
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
name|CreateMode
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
name|BeforeClass
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
comment|/**  * Stress test LiveNodes watching.  *  * Does bursts of adds to live_nodes using parallel threads to and verifies that after each   * burst a ZkStateReader detects the correct set.  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|TestStressLiveNodes
specifier|public
class|class
name|TestStressLiveNodes
extends|extends
name|SolrCloudTestCase
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
comment|/** A basic cloud client, we'll be testing the behavior of it's ZkStateReader */
DECL|field|CLOUD_CLIENT
specifier|private
specifier|static
name|CloudSolrClient
name|CLOUD_CLIENT
decl_stmt|;
comment|/** The addr of the zk server used in this test */
DECL|field|ZK_SERVER_ADDR
specifier|private
specifier|static
name|String
name|ZK_SERVER_ADDR
decl_stmt|;
comment|/* how many seconds we're willing to wait for our executor tasks to finish before failing the test */
DECL|field|WAIT_TIME
specifier|private
specifier|final
specifier|static
name|int
name|WAIT_TIME
init|=
name|TEST_NIGHTLY
condition|?
literal|60
else|:
literal|30
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createMiniSolrCloudCluster
specifier|private
specifier|static
name|void
name|createMiniSolrCloudCluster
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we only need 1 node, and we don't care about any configs or collections
comment|// we're going to fake all the live_nodes changes we want to fake.
name|configureCluster
argument_list|(
literal|1
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
comment|// give all nodes a chance to come alive
name|TestTolerantUpdateProcessorCloud
operator|.
name|assertSpinLoopAllJettyAreRunning
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|CLOUD_CLIENT
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
expr_stmt|;
name|CLOUD_CLIENT
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// force connection even though we aren't sending any requests
name|ZK_SERVER_ADDR
operator|=
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
expr_stmt|;
block|}
DECL|method|newSolrZkClient
specifier|private
specifier|static
name|SolrZkClient
name|newSolrZkClient
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|ZK_SERVER_ADDR
argument_list|)
expr_stmt|;
comment|// WTF is CloudConfigBuilder.DEFAULT_ZK_CLIENT_TIMEOUT private?
return|return
operator|new
name|SolrZkClient
argument_list|(
name|ZK_SERVER_ADDR
argument_list|,
literal|15000
argument_list|)
return|;
block|}
comment|/** returns the true set of live nodes (currently in zk) as a sorted list */
DECL|method|getTrueLiveNodesFromZk
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getTrueLiveNodesFromZk
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|client
init|=
name|newSolrZkClient
argument_list|()
decl_stmt|;
try|try
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|client
operator|.
name|getChildren
argument_list|(
name|ZkStateReader
operator|.
name|LIVE_NODES_ZKNODE
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**     * returns the cached set of live nodes (according to the ZkStateReader in our CloudSolrClient)     * as a sorted list.     * This is done in a sleep+retry loop until the result matches the expectedCount, or a few iters have passed    * (this way we aren't testing how fast the watchers complete, just that they got the correct result)    */
DECL|method|getCachedLiveNodesFromLocalState
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getCachedLiveNodesFromLocalState
parameter_list|(
specifier|final
name|int
name|expectedCount
parameter_list|)
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|result
init|=
literal|null
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|CLOUD_CLIENT
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedCount
operator|!=
name|result
operator|.
name|size
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"sleeping #{} to give watchers a chance to finish: {} != {}"
argument_list|,
name|i
argument_list|,
name|expectedCount
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|expectedCount
operator|!=
name|result
operator|.
name|size
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"gave up waiting for live nodes to match expected size: {} != {}"
argument_list|,
name|expectedCount
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|testStress
specifier|public
name|void
name|testStress
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do many iters, so we have "bursts" of adding nodes that we then check
specifier|final
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|numIters
condition|;
name|iter
operator|++
control|)
block|{
comment|// sanity check that ZK says there is in fact 1 live node
name|List
argument_list|<
name|String
argument_list|>
name|actualLiveNodes
init|=
name|getTrueLiveNodesFromZk
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"iter"
operator|+
name|iter
operator|+
literal|": "
operator|+
name|actualLiveNodes
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
name|actualLiveNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// only here do we forcibly update the cached live nodes so we don't have to wait for it to catch up
comment|// with all the ephemeral nodes that vanished after the last iteration
name|CLOUD_CLIENT
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateLiveNodes
argument_list|()
expr_stmt|;
comment|// sanity check that our Cloud Client's local state knows about the 1 (real) live node in our cluster
name|List
argument_list|<
name|String
argument_list|>
name|cachedLiveNodes
init|=
name|getCachedLiveNodesFromLocalState
argument_list|(
name|actualLiveNodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"iter"
operator|+
name|iter
operator|+
literal|" "
operator|+
name|actualLiveNodes
operator|.
name|size
argument_list|()
operator|+
literal|" != "
operator|+
name|cachedLiveNodes
operator|.
name|size
argument_list|()
argument_list|,
name|actualLiveNodes
argument_list|,
name|cachedLiveNodes
argument_list|)
expr_stmt|;
comment|// start spining up some threads to add some live_node children in parallel
comment|// we don't need a lot of threads or nodes (we don't want to swamp the CPUs
comment|// just bursts of conccurent adds) but we do want to randomize it a bit so we increase the
comment|// odds of concurrent watchers firing regardless of the num CPUs or load on the machine running
comment|// the test (but we deliberately don't look at availableProcessors() since we want randomization
comment|// consistency across all machines for a given seed)
specifier|final
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|// use same num for all thrashers, to increase likely hood of them all competing
comment|// (diff random number would mean heavy concurency only for ~ the first N=lowest num requetss)
comment|//
comment|// this does not need to be a large number -- in fact, the higher it is, the more
comment|// likely we are to see a mistake in early watcher triggers get "corrected" by a later one
comment|// and overlook a possible bug
specifier|final
name|int
name|numNodesPerThrasher
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"preparing parallel adds to live nodes: iter={}, numThreads={} numNodesPerThread={}"
argument_list|,
name|iter
argument_list|,
name|numThreads
argument_list|,
name|numNodesPerThrasher
argument_list|)
expr_stmt|;
comment|// NOTE: using ephemeral nodes
comment|// so we can't close any of these thrashers until we are done with our assertions
specifier|final
name|List
argument_list|<
name|LiveNodeTrasher
argument_list|>
name|thrashers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numThreads
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|thrashers
operator|.
name|add
argument_list|(
operator|new
name|LiveNodeTrasher
argument_list|(
literal|"T"
operator|+
name|iter
operator|+
literal|"_"
operator|+
name|i
argument_list|,
name|numNodesPerThrasher
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|ExecutorService
name|executorService
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareFixedThreadPool
argument_list|(
name|thrashers
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"test_live_nodes_thrasher_iter"
operator|+
name|iter
argument_list|)
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|invokeAll
argument_list|(
name|thrashers
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|WAIT_TIME
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
for|for
control|(
name|LiveNodeTrasher
name|thrasher
range|:
name|thrashers
control|)
block|{
name|thrasher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"iter"
operator|+
name|iter
operator|+
literal|": thrashers didn't finish even after explicitly stopping"
argument_list|,
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|WAIT_TIME
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// sanity check the *real* live_nodes entries from ZK match what the thrashers added
name|int
name|totalAdded
init|=
literal|1
decl_stmt|;
comment|// 1 real live node when we started
for|for
control|(
name|LiveNodeTrasher
name|thrasher
range|:
name|thrashers
control|)
block|{
name|totalAdded
operator|+=
name|thrasher
operator|.
name|getNumAdded
argument_list|()
expr_stmt|;
block|}
name|actualLiveNodes
operator|=
name|getTrueLiveNodesFromZk
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"iter"
operator|+
name|iter
argument_list|,
name|totalAdded
argument_list|,
name|actualLiveNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify our local client knows the correct set of live nodes
name|cachedLiveNodes
operator|=
name|getCachedLiveNodesFromLocalState
argument_list|(
name|actualLiveNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"iter"
operator|+
name|iter
operator|+
literal|" "
operator|+
name|actualLiveNodes
operator|.
name|size
argument_list|()
operator|+
literal|" != "
operator|+
name|cachedLiveNodes
operator|.
name|size
argument_list|()
argument_list|,
name|actualLiveNodes
argument_list|,
name|cachedLiveNodes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|LiveNodeTrasher
name|thrasher
range|:
name|thrashers
control|)
block|{
comment|// shutdown our zk connection, freeing our ephemeral nodes
name|thrasher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** NOTE: has internal counter which is not thread safe, only call() in one thread at a time */
DECL|class|LiveNodeTrasher
specifier|public
specifier|static
specifier|final
class|class
name|LiveNodeTrasher
implements|implements
name|Callable
argument_list|<
name|Integer
argument_list|>
block|{
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|numNodesToAdd
specifier|private
specifier|final
name|int
name|numNodesToAdd
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|SolrZkClient
name|client
decl_stmt|;
DECL|field|running
specifier|private
name|boolean
name|running
init|=
literal|false
decl_stmt|;
empty_stmt|;
DECL|field|numAdded
specifier|private
name|int
name|numAdded
init|=
literal|0
decl_stmt|;
comment|/** ID should ideally be unique amonst any other instances */
DECL|method|LiveNodeTrasher
specifier|public
name|LiveNodeTrasher
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|numNodesToAdd
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|numNodesToAdd
operator|=
name|numNodesToAdd
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|newSolrZkClient
argument_list|()
expr_stmt|;
block|}
comment|/** returns the number of nodes actually added w/o error */
DECL|method|call
specifier|public
name|Integer
name|call
parameter_list|()
block|{
name|running
operator|=
literal|true
expr_stmt|;
comment|// NOTE: test includes 'running'
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|running
operator|&&
name|i
operator|<
name|numNodesToAdd
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|nodePath
init|=
name|ZkStateReader
operator|.
name|LIVE_NODES_ZKNODE
operator|+
literal|"/thrasher-"
operator|+
name|id
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
try|try
block|{
name|client
operator|.
name|makePath
argument_list|(
name|nodePath
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|numAdded
operator|++
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
literal|"failed to create: "
operator|+
name|nodePath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|numAdded
return|;
block|}
DECL|method|getNumAdded
specifier|public
name|int
name|getNumAdded
parameter_list|()
block|{
return|return
name|numAdded
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

