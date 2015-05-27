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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
operator|.
name|makeMap
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CompletionService
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
name|ExecutorCompletionService
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
name|Future
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
name|SynchronousQueue
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
name|ThreadPoolExecutor
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
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
name|Nightly
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
name|SolrRequest
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
operator|.
name|Create
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
name|cloud
operator|.
name|hdfs
operator|.
name|HdfsTestUtil
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
name|ClusterStateUtil
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
name|MapSolrParams
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
name|solr
operator|.
name|util
operator|.
name|BadHdfsThreadsFilter
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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
import|;
end_import

begin_class
annotation|@
name|Nightly
annotation|@
name|Slow
annotation|@
name|SuppressSSL
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|BadHdfsThreadsFilter
operator|.
name|class
comment|// hdfs currently leaks thread(s)
block|}
argument_list|)
DECL|class|SharedFSAutoReplicaFailoverTest
specifier|public
class|class
name|SharedFSAutoReplicaFailoverTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|true
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|executor
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ExecutorUtil
operator|.
name|MDCAwareThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"testExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|completionService
name|CompletionService
argument_list|<
name|Object
argument_list|>
name|completionService
decl_stmt|;
DECL|field|pending
name|Set
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|pending
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|hdfsFailoverBeforeClass
specifier|public
specifier|static
name|void
name|hdfsFailoverBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|hdfsFailoverAfterClass
specifier|public
specifier|static
name|void
name|hdfsFailoverAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
name|useJettyDataDir
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|SharedFSAutoReplicaFailoverTest
specifier|public
name|SharedFSAutoReplicaFailoverTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|completionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|pending
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|4
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|testBasics
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// very slow tests, especially since jetty is started and stopped
comment|// serially
DECL|method|testBasics
specifier|private
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collection1
init|=
literal|"solrj_collection"
decl_stmt|;
name|Create
name|createCollectionRequest
init|=
operator|new
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collection1
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|2
argument_list|)
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
operator|.
name|setRouterField
argument_list|(
literal|"myOwnField"
argument_list|)
operator|.
name|setAutoAddReplicas
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collection1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|collection2
init|=
literal|"solrj_collection2"
decl_stmt|;
name|createCollectionRequest
operator|=
operator|new
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collection2
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|2
argument_list|)
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
operator|.
name|setRouterField
argument_list|(
literal|"myOwnField"
argument_list|)
operator|.
name|setAutoAddReplicas
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response2
init|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|getCommonCloudSolrClient
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response2
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response2
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collection2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|collection3
init|=
literal|"solrj_collection3"
decl_stmt|;
name|createCollectionRequest
operator|=
operator|new
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collection3
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|5
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|1
argument_list|)
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
operator|.
name|setRouterField
argument_list|(
literal|"myOwnField"
argument_list|)
operator|.
name|setAutoAddReplicas
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response3
init|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|getCommonCloudSolrClient
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response3
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response3
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collection3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLiveReplicas
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|120000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|ClusterStateUtil
operator|.
name|getLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ClusterStateUtil
operator|.
name|getLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection2
argument_list|)
operator|<
literal|4
argument_list|)
expr_stmt|;
comment|// collection3 has maxShardsPerNode=1, there are 4 standard jetties and one control jetty and 2 nodes stopped
name|ClusterStateUtil
operator|.
name|waitForLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection3
argument_list|,
literal|3
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// collection1 should still be at 4
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|ClusterStateUtil
operator|.
name|getLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|)
argument_list|)
expr_stmt|;
comment|// and collection2 less than 4
name|assertTrue
argument_list|(
name|ClusterStateUtil
operator|.
name|getLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection2
argument_list|)
operator|<
literal|4
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|controlJetty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all not live"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllReplicasNotLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|45000
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|jettys
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|controlJetty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLiveReplicas
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|120000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
name|assertSingleReplicationAndShardSize
argument_list|(
name|collection3
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|int
name|jettyIndex
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
name|jettyIndex
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
name|jettyIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLiveReplicas
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|60000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
name|assertSingleReplicationAndShardSize
argument_list|(
name|collection3
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|ClusterStateUtil
operator|.
name|waitForLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection3
argument_list|,
literal|5
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|//disable autoAddReplicas
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CLUSTERPROP
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|ZkStateReader
operator|.
name|AUTO_ADD_REPLICAS
argument_list|,
literal|"val"
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|SolrRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|int
name|currentCount
init|=
name|ClusterStateUtil
operator|.
name|getLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|)
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|//solr-no-core.xml has defined workLoopDelay=10s and waitAfterExpiration=10s
comment|//Hence waiting for 30 seconds to be on the safe side.
name|Thread
operator|.
name|sleep
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|//Ensures that autoAddReplicas has not kicked in.
name|assertTrue
argument_list|(
name|currentCount
operator|>
name|ClusterStateUtil
operator|.
name|getLiveAndActiveReplicaCount
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|)
argument_list|)
expr_stmt|;
comment|//enable autoAddReplicas
name|m
operator|=
name|makeMap
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CLUSTERPROP
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|ZkStateReader
operator|.
name|AUTO_ADD_REPLICAS
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|QueryRequest
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLiveReplicas
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|60000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSingleReplicationAndShardSize
specifier|private
name|void
name|assertSingleReplicationAndShardSize
parameter_list|(
name|String
name|collection
parameter_list|,
name|int
name|numSlices
parameter_list|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
decl_stmt|;
name|slices
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numSlices
argument_list|,
name|slices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|slice
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertSliceAndReplicaCount
specifier|private
name|void
name|assertSliceAndReplicaCount
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
decl_stmt|;
name|slices
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|slices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|slice
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

