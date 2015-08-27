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
name|NamedList
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TimeOut
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

begin_class
DECL|class|TestRebalanceLeaders
specifier|public
class|class
name|TestRebalanceLeaders
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|COLLECTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"testcollection"
decl_stmt|;
DECL|method|TestRebalanceLeaders
specifier|public
name|TestRebalanceLeaders
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|sliceCount
operator|=
literal|4
expr_stmt|;
block|}
DECL|field|reps
name|int
name|reps
init|=
literal|10
decl_stmt|;
DECL|field|timeoutMs
name|int
name|timeoutMs
init|=
literal|60000
decl_stmt|;
DECL|field|initial
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Replica
argument_list|>
argument_list|>
name|initial
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|expected
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|expected
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
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
name|reps
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|9
argument_list|)
operator|+
literal|1
expr_stmt|;
comment|// make sure and do at least one.
try|try
init|(
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
init|)
block|{
comment|// Mix up a bunch of different combinations of shards and replicas in order to exercise boundary cases.
comment|// shards, replicationfactor, maxreplicaspernode
name|int
name|shards
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
if|if
condition|(
name|shards
operator|<
literal|2
condition|)
name|shards
operator|=
literal|2
expr_stmt|;
name|int
name|rFactor
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|rFactor
operator|<
literal|2
condition|)
name|rFactor
operator|=
literal|2
expr_stmt|;
name|createCollection
argument_list|(
literal|null
argument_list|,
name|COLLECTION_NAME
argument_list|,
name|shards
argument_list|,
name|rFactor
argument_list|,
name|shards
operator|*
name|rFactor
operator|+
literal|1
argument_list|,
name|client
argument_list|,
literal|null
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
block|}
name|waitForCollection
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|COLLECTION_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|listCollection
argument_list|()
expr_stmt|;
name|rebalanceLeaderTest
argument_list|()
expr_stmt|;
block|}
DECL|method|listCollection
specifier|private
name|void
name|listCollection
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|//CloudSolrServer client = createCloudClient(null);
try|try
block|{
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
name|LIST
operator|.
name|toString
argument_list|()
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
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|collections
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"control_collection was not found in list"
argument_list|,
name|collections
operator|.
name|contains
argument_list|(
literal|"control_collection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DEFAULT_COLLECTION
operator|+
literal|" was not found in list"
argument_list|,
name|collections
operator|.
name|contains
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|COLLECTION_NAME
operator|+
literal|" was not found in list"
argument_list|,
name|collections
operator|.
name|contains
argument_list|(
name|COLLECTION_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//remove collections
comment|//client.shutdown();
block|}
block|}
DECL|method|recordInitialState
name|void
name|recordInitialState
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
operator|.
name|getSlicesMap
argument_list|()
decl_stmt|;
comment|// Assemble a list of all the replicas for all the shards in a convenient way to look at them.
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
name|ent
range|:
name|slices
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|initial
operator|.
name|put
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getReplicas
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rebalanceLeaderTest
name|void
name|rebalanceLeaderTest
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|SolrServerException
throws|,
name|KeeperException
block|{
name|recordInitialState
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|reps
condition|;
operator|++
name|idx
control|)
block|{
name|issueCommands
argument_list|()
expr_stmt|;
name|checkConsistency
argument_list|()
expr_stmt|;
block|}
block|}
comment|// After we've called the rebalance command, we want to insure that:
comment|// 1> all replicas appear once and only once in the respective leader election queue
comment|// 2> All the replicas we _think_ are leaders are in the 0th position in the leader election queue.
comment|// 3> The node that ZooKeeper thinks is the leader is the one we think should be the leader.
DECL|method|checkConsistency
name|void
name|checkConsistency
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|TimeOut
name|timeout
init|=
operator|new
name|TimeOut
argument_list|(
name|timeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
if|if
condition|(
name|checkAppearOnce
argument_list|()
operator|&&
name|checkElectionZero
argument_list|()
operator|&&
name|checkZkLeadersAgree
argument_list|()
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Checking the rebalance leader command failed"
argument_list|)
expr_stmt|;
block|}
comment|// Do all the nodes appear exactly once in the leader election queue and vice-versa?
DECL|method|checkAppearOnce
name|Boolean
name|checkAppearOnce
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Replica
argument_list|>
argument_list|>
name|ent
range|:
name|initial
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|leaderQueue
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getChildren
argument_list|(
literal|"/collections/"
operator|+
name|COLLECTION_NAME
operator|+
literal|"/leader_elect/"
operator|+
name|ent
operator|.
name|getKey
argument_list|()
operator|+
literal|"/election"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaderQueue
operator|.
name|size
argument_list|()
operator|!=
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Check that each election node has a corresponding replica.
for|for
control|(
name|String
name|electionNode
range|:
name|leaderQueue
control|)
block|{
if|if
condition|(
name|checkReplicaName
argument_list|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNode
argument_list|)
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
return|return
literal|false
return|;
block|}
comment|// Check that each replica has an election node.
for|for
control|(
name|Replica
name|rep
range|:
name|ent
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|checkElectionNode
argument_list|(
name|rep
operator|.
name|getName
argument_list|()
argument_list|,
name|leaderQueue
argument_list|)
condition|)
block|{
continue|continue;
block|}
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// Check that the given name is in the leader election queue
DECL|method|checkElectionNode
name|Boolean
name|checkElectionNode
parameter_list|(
name|String
name|repName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|leaderQueue
parameter_list|)
block|{
for|for
control|(
name|String
name|electionNode
range|:
name|leaderQueue
control|)
block|{
if|if
condition|(
name|repName
operator|.
name|equals
argument_list|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNode
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// Check that the name passed in corresponds to a replica.
DECL|method|checkReplicaName
name|Boolean
name|checkReplicaName
parameter_list|(
name|String
name|toCheck
parameter_list|,
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
parameter_list|)
block|{
for|for
control|(
name|Replica
name|rep
range|:
name|replicas
control|)
block|{
if|if
condition|(
name|toCheck
operator|.
name|equals
argument_list|(
name|rep
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// Get the shard leader election from ZK and sort it. The node may not actually be there, so retry
DECL|method|getOverseerSort
name|List
argument_list|<
name|String
argument_list|>
name|getOverseerSort
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ret
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ret
operator|=
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
literal|"/collections/"
operator|+
name|COLLECTION_NAME
operator|+
literal|"/leader_elect/"
operator|+
name|key
operator|+
literal|"/election"
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|cloudClient
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
return|return
literal|null
return|;
block|}
comment|// Is every node we think is the leader in the zeroth position in the leader election queue?
DECL|method|checkElectionZero
name|Boolean
name|checkElectionZero
parameter_list|()
block|{
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
name|ent
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|leaderQueue
init|=
name|getOverseerSort
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaderQueue
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|String
name|electName
init|=
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|leaderQueue
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|coreName
init|=
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|electName
operator|.
name|equals
argument_list|(
name|coreName
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// Do who we _think_ should be the leader agree with the leader nodes?
DECL|method|checkZkLeadersAgree
name|Boolean
name|checkZkLeadersAgree
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
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
name|ent
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|path
init|=
literal|"/collections/"
operator|+
name|COLLECTION_NAME
operator|+
literal|"/leaders/"
operator|+
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|getZkData
argument_list|(
name|cloudClient
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|String
name|repCore
init|=
literal|null
decl_stmt|;
name|String
name|zkCore
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|zkCore
operator|=
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
expr_stmt|;
name|repCore
operator|=
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkCore
operator|.
name|equals
argument_list|(
name|repCore
argument_list|)
operator|==
literal|false
condition|)
block|{
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
DECL|method|getZkData
name|byte
index|[]
name|getZkData
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
name|stat
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
return|return
name|data
return|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
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
name|e1
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|KeeperException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
return|return
literal|null
return|;
block|}
comment|// It's OK not to check the return here since the subsequent tests will fail.
DECL|method|issueCommands
name|void
name|issueCommands
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|// Find a replica to make the preferredLeader. NOTE: may be one that's _already_ leader!
name|expected
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Replica
argument_list|>
argument_list|>
name|ent
range|:
name|initial
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Replica
name|rep
init|=
name|replicas
operator|.
name|get
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|%
name|replicas
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|rep
argument_list|)
expr_stmt|;
name|issuePreferred
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|rep
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|waitForAllPreferreds
argument_list|()
operator|==
literal|false
condition|)
block|{
name|fail
argument_list|(
literal|"Waited for timeout for preferredLeader assignments to be made and they werent."
argument_list|)
expr_stmt|;
block|}
comment|//fillExpectedWithCurrent();
comment|// Now rebalance the leaders
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
name|REBALANCELEADERS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Insure we get error returns when omitting required parameters
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"maxAtOnce"
argument_list|,
literal|"10"
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
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|issuePreferred
name|void
name|issuePreferred
parameter_list|(
name|String
name|slice
parameter_list|,
name|Replica
name|rep
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
throws|,
name|InterruptedException
block|{
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
name|ADDREPLICAPROP
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Insure we get error returns when omitting required parameters
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"shard"
argument_list|,
name|slice
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"replica"
argument_list|,
name|rep
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"property"
argument_list|,
literal|"preferredLeader"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"property.value"
argument_list|,
literal|"true"
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
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForAllPreferreds
name|boolean
name|waitForAllPreferreds
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|boolean
name|goAgain
init|=
literal|true
decl_stmt|;
name|TimeOut
name|timeout
init|=
operator|new
name|TimeOut
argument_list|(
name|timeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
name|goAgain
operator|=
literal|false
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
operator|.
name|getSlicesMap
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
name|ent
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Replica
name|me
init|=
name|slices
operator|.
name|get
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getReplica
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|me
operator|.
name|getBool
argument_list|(
literal|"property.preferredleader"
argument_list|,
literal|false
argument_list|)
operator|==
literal|false
condition|)
block|{
name|goAgain
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|goAgain
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
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
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

