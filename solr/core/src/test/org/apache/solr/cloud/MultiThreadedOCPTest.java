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
name|java
operator|.
name|util
operator|.
name|Random
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
name|CollectionAdminRequest
operator|.
name|SplitShard
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|RequestStatusState
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
name|Utils
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|Overseer
operator|.
name|QUEUE_OPERATION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|OverseerTaskProcessor
operator|.
name|MAX_PARALLEL_TASKS
import|;
end_import

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
name|params
operator|.
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|MOCK_COLL_TASK
import|;
end_import

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
name|params
operator|.
name|CommonAdminParams
operator|.
name|ASYNC
import|;
end_import

begin_comment
comment|/**  * Tests the Multi threaded Collections API.  */
end_comment

begin_class
DECL|class|MultiThreadedOCPTest
specifier|public
class|class
name|MultiThreadedOCPTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|REQUEST_STATUS_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|REQUEST_STATUS_TIMEOUT
init|=
literal|5
operator|*
literal|60
decl_stmt|;
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
DECL|field|NUM_COLLECTIONS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_COLLECTIONS
init|=
literal|4
decl_stmt|;
DECL|method|MultiThreadedOCPTest
specifier|public
name|MultiThreadedOCPTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
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
name|testParallelCollectionAPICalls
argument_list|()
expr_stmt|;
name|testTaskExclusivity
argument_list|()
expr_stmt|;
name|testDeduplicationOfSubmittedTasks
argument_list|()
expr_stmt|;
name|testLongAndShortRunningParallelApiCalls
argument_list|()
expr_stmt|;
name|testFillWorkQueue
argument_list|()
expr_stmt|;
block|}
DECL|method|testFillWorkQueue
specifier|private
name|void
name|testFillWorkQueue
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|DistributedQueue
name|distributedQueue
init|=
operator|new
name|DistributedQueue
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
literal|"/overseer/collection-queue-work"
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
comment|//fill the work queue with blocked tasks by adding more than the no:of parallel tasks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MAX_PARALLEL_TASKS
operator|+
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|distributedQueue
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
literal|"A_COLL"
argument_list|,
name|QUEUE_OPERATION
argument_list|,
name|MOCK_COLL_TASK
operator|.
name|toLower
argument_list|()
argument_list|,
name|ASYNC
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"sleep"
argument_list|,
operator|(
name|i
operator|==
literal|0
condition|?
literal|"1000"
else|:
literal|"1"
operator|)
comment|//first task waits for 1 second, and thus blocking
comment|// all other tasks. Subsequent tasks only wait for 1ms
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"MOCK task added {}"
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|//wait and post the next message
comment|//this is not going to be blocked because it operates on another collection
name|distributedQueue
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
literal|"B_COLL"
argument_list|,
name|QUEUE_OPERATION
argument_list|,
name|MOCK_COLL_TASK
operator|.
name|toLower
argument_list|()
argument_list|,
name|ASYNC
argument_list|,
literal|"200"
argument_list|,
literal|"sleep"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|acoll
init|=
literal|null
decl_stmt|,
name|bcoll
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bcoll
operator|==
literal|null
condition|)
block|{
name|CollectionAdminResponse
name|statusResponse
init|=
name|getStatusResponse
argument_list|(
literal|"200"
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|bcoll
operator|=
operator|(
name|Long
operator|)
name|statusResponse
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"MOCK_FINISHED"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acoll
operator|==
literal|null
condition|)
block|{
name|CollectionAdminResponse
name|statusResponse
init|=
name|getStatusResponse
argument_list|(
literal|"2"
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|acoll
operator|=
operator|(
name|Long
operator|)
name|statusResponse
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"MOCK_FINISHED"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acoll
operator|!=
literal|null
operator|&&
name|bcoll
operator|!=
literal|null
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|acoll
operator|!=
literal|null
operator|&&
name|bcoll
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acoll
operator|>
name|bcoll
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParallelCollectionAPICalls
specifier|private
name|void
name|testParallelCollectionAPICalls
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_COLLECTIONS
condition|;
name|i
operator|++
control|)
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"ocptest"
operator|+
name|i
argument_list|,
literal|"conf1"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
operator|.
name|processAsync
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
name|boolean
name|pass
init|=
literal|false
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|numRunningTasks
init|=
literal|0
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
name|NUM_COLLECTIONS
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|getRequestState
argument_list|(
name|i
operator|+
literal|""
argument_list|,
name|client
argument_list|)
operator|==
name|RequestStatusState
operator|.
name|RUNNING
condition|)
block|{
name|numRunningTasks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|numRunningTasks
operator|>
literal|1
condition|)
block|{
name|pass
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|counter
operator|++
operator|>
literal|100
condition|)
block|{
break|break;
block|}
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
name|assertTrue
argument_list|(
literal|"More than one tasks were supposed to be running in parallel but they weren't."
argument_list|,
name|pass
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_COLLECTIONS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RequestStatusState
name|state
init|=
name|getRequestStateAfterCompletion
argument_list|(
name|i
operator|+
literal|""
argument_list|,
name|REQUEST_STATUS_TIMEOUT
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Task "
operator|+
name|i
operator|+
literal|" did not complete, final state: "
operator|+
name|state
argument_list|,
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testTaskExclusivity
specifier|private
name|void
name|testTaskExclusivity
parameter_list|()
throws|throws
name|Exception
throws|,
name|SolrServerException
block|{
name|DistributedQueue
name|distributedQueue
init|=
operator|new
name|DistributedQueue
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
literal|"/overseer/collection-queue-work"
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|Create
name|createCollectionRequest
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"ocptest_shardsplit"
argument_list|,
literal|"conf1"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|createCollectionRequest
operator|.
name|processAsync
argument_list|(
literal|"1000"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|distributedQueue
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
literal|"ocptest_shardsplit"
argument_list|,
name|QUEUE_OPERATION
argument_list|,
name|MOCK_COLL_TASK
operator|.
name|toLower
argument_list|()
argument_list|,
name|ASYNC
argument_list|,
literal|"1001"
argument_list|,
literal|"sleep"
argument_list|,
literal|"100"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|distributedQueue
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
literal|"ocptest_shardsplit"
argument_list|,
name|QUEUE_OPERATION
argument_list|,
name|MOCK_COLL_TASK
operator|.
name|toLower
argument_list|()
argument_list|,
name|ASYNC
argument_list|,
literal|"1002"
argument_list|,
literal|"sleep"
argument_list|,
literal|"100"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|iterations
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|runningTasks
init|=
literal|0
decl_stmt|;
name|int
name|completedTasks
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1001
init|;
name|i
operator|<=
literal|1002
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RequestStatusState
name|state
init|=
name|getRequestState
argument_list|(
name|i
argument_list|,
name|client
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
name|RequestStatusState
operator|.
name|RUNNING
condition|)
block|{
name|runningTasks
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|RequestStatusState
operator|.
name|COMPLETED
condition|)
block|{
name|completedTasks
operator|++
expr_stmt|;
block|}
name|assertNotSame
argument_list|(
literal|"We have a failed SPLITSHARD task"
argument_list|,
name|RequestStatusState
operator|.
name|FAILED
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
comment|// TODO: REQUESTSTATUS might come back with more than 1 running tasks over multiple calls.
comment|// The only way to fix this is to support checking of multiple requestids in a single REQUESTSTATUS task.
name|assertTrue
argument_list|(
literal|"Mutual exclusion failed. Found more than one task running for the same collection"
argument_list|,
name|runningTasks
operator|<
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|completedTasks
operator|==
literal|2
operator|||
name|iterations
operator|++
operator|>
name|REQUEST_STATUS_TIMEOUT
condition|)
break|break;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
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
return|return;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|1001
init|;
name|i
operator|<=
literal|1002
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RequestStatusState
name|state
init|=
name|getRequestStateAfterCompletion
argument_list|(
name|i
operator|+
literal|""
argument_list|,
name|REQUEST_STATUS_TIMEOUT
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Task "
operator|+
name|i
operator|+
literal|" did not complete, final state: "
operator|+
name|state
argument_list|,
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testDeduplicationOfSubmittedTasks
specifier|private
name|void
name|testDeduplicationOfSubmittedTasks
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"ocptest_shardsplit2"
argument_list|,
literal|"conf1"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
operator|.
name|processAsync
argument_list|(
literal|"3000"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|SplitShard
name|splitShardRequest
init|=
name|CollectionAdminRequest
operator|.
name|splitShard
argument_list|(
literal|"ocptest_shardsplit2"
argument_list|)
operator|.
name|setShardName
argument_list|(
name|SHARD1
argument_list|)
decl_stmt|;
name|splitShardRequest
operator|.
name|processAsync
argument_list|(
literal|"3001"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|=
name|CollectionAdminRequest
operator|.
name|splitShard
argument_list|(
literal|"ocptest_shardsplit2"
argument_list|)
operator|.
name|setShardName
argument_list|(
name|SHARD2
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|.
name|processAsync
argument_list|(
literal|"3002"
argument_list|,
name|client
argument_list|)
expr_stmt|;
comment|// Now submit another task with the same id. At this time, hopefully the previous 3002 should still be in the queue.
name|expectThrows
argument_list|(
name|SolrServerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|splitShard
argument_list|(
literal|"ocptest_shardsplit2"
argument_list|)
operator|.
name|setShardName
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|processAsync
argument_list|(
literal|"3002"
argument_list|,
name|client
argument_list|)
expr_stmt|;
comment|// more helpful assertion failure
name|fail
argument_list|(
literal|"Duplicate request was supposed to exist but wasn't found. De-duplication of submitted task failed."
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3001
init|;
name|i
operator|<=
literal|3002
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RequestStatusState
name|state
init|=
name|getRequestStateAfterCompletion
argument_list|(
name|i
operator|+
literal|""
argument_list|,
name|REQUEST_STATUS_TIMEOUT
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Task "
operator|+
name|i
operator|+
literal|" did not complete, final state: "
operator|+
name|state
argument_list|,
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testLongAndShortRunningParallelApiCalls
specifier|private
name|void
name|testLongAndShortRunningParallelApiCalls
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|SolrServerException
block|{
name|Thread
name|indexThread
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
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|max
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|200
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|101
init|;
name|id
operator|<
name|max
condition|;
name|id
operator|++
control|)
block|{
try|try
block|{
name|doAddDoc
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|)
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
literal|"Exception while adding docs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|SplitShard
name|splitShardRequest
init|=
name|CollectionAdminRequest
operator|.
name|splitShard
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|setShardName
argument_list|(
name|SHARD1
argument_list|)
decl_stmt|;
name|splitShardRequest
operator|.
name|processAsync
argument_list|(
literal|"2000"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|RequestStatusState
name|state
init|=
name|getRequestState
argument_list|(
literal|"2000"
argument_list|,
name|client
argument_list|)
decl_stmt|;
while|while
condition|(
name|state
operator|==
name|RequestStatusState
operator|.
name|SUBMITTED
condition|)
block|{
name|state
operator|=
name|getRequestState
argument_list|(
literal|"2000"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertSame
argument_list|(
literal|"SplitShard task [2000] was supposed to be in [running] but isn't. It is ["
operator|+
name|state
operator|+
literal|"]"
argument_list|,
name|RequestStatusState
operator|.
name|RUNNING
argument_list|,
name|state
argument_list|)
expr_stmt|;
comment|// CLUSTERSTATE is always mutually exclusive, it should return with a response before the split completes
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CLUSTERSTATUS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|SolrRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|state
operator|=
name|getRequestState
argument_list|(
literal|"2000"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"After invoking OVERSEERSTATUS, SplitShard task [2000] was still supposed to be in [running] but "
operator|+
literal|"isn't. It is ["
operator|+
name|state
operator|+
literal|"]"
argument_list|,
name|RequestStatusState
operator|.
name|RUNNING
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|indexThread
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
name|log
operator|.
name|warn
argument_list|(
literal|"Indexing thread interrupted."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doAddDoc
name|void
name|doAddDoc
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|index
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// todo - target diff servers and use cloud clients as well as non-cloud clients
block|}
block|}
end_class

end_unit

