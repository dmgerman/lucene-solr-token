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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
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
name|List
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

begin_class
DECL|class|RollingRestartTest
specifier|public
class|class
name|RollingRestartTest
extends|extends
name|AbstractFullDistribZkTestBase
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
DECL|field|MAX_WAIT_TIME
specifier|private
specifier|static
specifier|final
name|long
name|MAX_WAIT_TIME
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|300
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|method|RollingRestartTest
specifier|public
name|RollingRestartTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|fixShardCount
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|16
else|:
literal|2
argument_list|)
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
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|restartWithRolesTest
argument_list|()
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|restartWithRolesTest
specifier|public
name|void
name|restartWithRolesTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|leader
init|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|leader
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Current overseer leader = {}"
argument_list|,
name|leader
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|int
name|numDesignateOverseers
init|=
name|TEST_NIGHTLY
condition|?
literal|16
else|:
literal|2
decl_stmt|;
name|numDesignateOverseers
operator|=
name|Math
operator|.
name|max
argument_list|(
name|getShardCount
argument_list|()
argument_list|,
name|numDesignateOverseers
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|designates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|designateJettys
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numDesignateOverseers
condition|;
name|i
operator|++
control|)
block|{
name|int
name|n
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|getShardCount
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|cloudJettys
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|.
name|nodeName
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Chose {} as overseer designate"
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|addRole
argument_list|(
name|nodeName
argument_list|,
literal|"overseer"
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|designates
operator|.
name|add
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|designateJettys
operator|.
name|add
argument_list|(
name|cloudJettys
operator|.
name|get
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|waitUntilOverseerDesignateIsLeader
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|designates
argument_list|,
name|MAX_WAIT_TIME
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|boolean
name|sawLiveDesignate
init|=
literal|false
decl_stmt|;
name|int
name|numRestarts
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|12
else|:
literal|2
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
name|numRestarts
condition|;
name|i
operator|++
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Rolling restart #{}"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cloudJetty
range|:
name|designateJettys
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Restarting {}"
argument_list|,
name|cloudJetty
argument_list|)
expr_stmt|;
name|chaosMonkey
operator|.
name|stopJetty
argument_list|(
name|cloudJetty
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateLiveNodes
argument_list|()
expr_stmt|;
name|boolean
name|liveDesignates
init|=
name|CollectionUtils
operator|.
name|intersection
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
argument_list|,
name|designates
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|liveDesignates
condition|)
block|{
name|sawLiveDesignate
operator|=
literal|true
expr_stmt|;
name|boolean
name|success
init|=
name|waitUntilOverseerDesignateIsLeader
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|designates
argument_list|,
name|MAX_WAIT_TIME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|leader
operator|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|leader
operator|==
literal|null
condition|)
name|log
operator|.
name|error
argument_list|(
literal|"NOOVERSEER election queue is :"
operator|+
name|OverseerCollectionConfigSetProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
literal|"/overseer_elect/election"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No overseer designate as leader found after restart #"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|": "
operator|+
name|leader
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Unable to restart (#"
operator|+
name|i
operator|+
literal|"): "
operator|+
name|cloudJetty
argument_list|,
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|cloudJetty
operator|.
name|jetty
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|waitUntilOverseerDesignateIsLeader
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|designates
argument_list|,
name|MAX_WAIT_TIME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|leader
operator|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|leader
operator|==
literal|null
condition|)
name|log
operator|.
name|error
argument_list|(
literal|"NOOVERSEER election queue is :"
operator|+
name|OverseerCollectionConfigSetProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
literal|"/overseer_elect/election"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No overseer leader found after restart #"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|": "
operator|+
name|leader
argument_list|)
expr_stmt|;
block|}
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateLiveNodes
argument_list|()
expr_stmt|;
name|sawLiveDesignate
operator|=
name|CollectionUtils
operator|.
name|intersection
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
argument_list|,
name|designates
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Test may not be working if we never saw a live designate"
argument_list|,
name|sawLiveDesignate
argument_list|)
expr_stmt|;
name|leader
operator|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|leader
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Current overseer leader (after restart) = {}"
argument_list|,
name|leader
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
DECL|method|waitUntilOverseerDesignateIsLeader
specifier|static
name|boolean
name|waitUntilOverseerDesignateIsLeader
parameter_list|(
name|SolrZkClient
name|testZkClient
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|overseerDesignates
parameter_list|,
name|long
name|timeoutInNanos
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|maxTimeout
init|=
name|now
operator|+
name|timeoutInNanos
decl_stmt|;
comment|// the maximum amount of time we're willing to wait to see the designate as leader
name|long
name|timeout
init|=
name|now
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
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
name|int
name|stableCheckTimeout
init|=
literal|2000
decl_stmt|;
name|String
name|oldleader
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
operator|&&
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|maxTimeout
condition|)
block|{
name|String
name|newLeader
init|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|testZkClient
argument_list|)
decl_stmt|;
if|if
condition|(
name|newLeader
operator|!=
literal|null
operator|&&
operator|!
name|newLeader
operator|.
name|equals
argument_list|(
name|oldleader
argument_list|)
condition|)
block|{
comment|// the leaders have changed, let's move the timeout further
name|timeout
operator|=
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
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"oldLeader={} newLeader={} - Advancing timeout to: {}"
argument_list|,
name|oldleader
argument_list|,
name|newLeader
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|oldleader
operator|=
name|newLeader
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|overseerDesignates
operator|.
name|contains
argument_list|(
name|newLeader
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|stableCheckTimeout
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
if|if
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|maxTimeout
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Max wait time exceeded"
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

