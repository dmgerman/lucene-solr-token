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
name|Replica
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
name|List
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Tests leader-initiated recovery scenarios after a leader node fails  * and one of the replicas is out-of-sync.  */
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
DECL|class|LeaderFailoverAfterPartitionTest
specifier|public
class|class
name|LeaderFailoverAfterPartitionTest
extends|extends
name|HttpPartitionTest
block|{
DECL|method|LeaderFailoverAfterPartitionTest
specifier|public
name|LeaderFailoverAfterPartitionTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
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
comment|// kill a leader and make sure recovery occurs as expected
name|testRf3WithLeaderFailover
argument_list|()
expr_stmt|;
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
argument_list|(
name|testCollectionName
argument_list|)
argument_list|,
name|notLeaders
operator|.
name|size
argument_list|()
operator|==
literal|2
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
argument_list|(
name|testCollectionName
argument_list|)
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
name|assertDocExists
argument_list|(
name|getHttpSolrServer
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
argument_list|,
name|testCollectionName
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|assertDocExists
argument_list|(
name|getHttpSolrServer
argument_list|(
name|notLeaders
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|testCollectionName
argument_list|)
argument_list|,
name|testCollectionName
argument_list|,
literal|"5"
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
comment|//chaosMonkey.expireSession(leaderJetty);
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
argument_list|(
name|testCollectionName
argument_list|)
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
argument_list|(
name|testCollectionName
argument_list|)
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
operator|>=
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
name|participatingReplicas
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
name|participatingReplicas
operator|.
name|size
argument_list|()
operator|+
literal|"; "
operator|+
name|participatingReplicas
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|(
name|testCollectionName
argument_list|)
argument_list|,
name|participatingReplicas
operator|.
name|size
argument_list|()
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|sendDoc
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|replicasToCheck
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|stillUp
range|:
name|participatingReplicas
control|)
name|replicasToCheck
operator|.
name|add
argument_list|(
name|stillUp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|waitToSeeReplicasActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
name|replicasToCheck
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertDocsExistInAllReplicas
argument_list|(
name|participatingReplicas
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|6
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
block|}
end_class

end_unit
