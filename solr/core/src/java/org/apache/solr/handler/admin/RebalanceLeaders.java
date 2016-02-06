begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|COLLECTION_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|CORE_NODE_NAME_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|ELECTION_NODE_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|LEADER_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|MAX_AT_ONCE_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|MAX_WAIT_SECONDS_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|REJOIN_AT_HEAD_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
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
name|REBALANCELEADERS
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Locale
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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|LeaderElector
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
name|OverseerTaskProcessor
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
name|overseer
operator|.
name|SliceMutator
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
name|SolrException
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
name|DocCollection
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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

begin_class
DECL|class|RebalanceLeaders
class|class
name|RebalanceLeaders
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
DECL|field|req
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|final
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|collectionsHandler
specifier|final
name|CollectionsHandler
name|collectionsHandler
decl_stmt|;
DECL|field|coreContainer
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|method|RebalanceLeaders
name|RebalanceLeaders
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|CollectionsHandler
name|collectionsHandler
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
name|this
operator|.
name|collectionsHandler
operator|=
name|collectionsHandler
expr_stmt|;
name|coreContainer
operator|=
name|collectionsHandler
operator|.
name|getCoreContainer
argument_list|()
expr_stmt|;
block|}
DECL|method|execute
name|void
name|execute
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|check
argument_list|(
name|COLLECTION_PROP
argument_list|)
expr_stmt|;
name|String
name|collectionName
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|COLLECTION_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"The "
operator|+
name|COLLECTION_PROP
operator|+
literal|" is required for the Rebalance Leaders command."
argument_list|)
argument_list|)
throw|;
block|}
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|dc
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|dc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Collection '"
operator|+
name|collectionName
operator|+
literal|"' does not exist, no action taken."
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|currentRequests
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|max
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
name|MAX_AT_ONCE_PROP
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|max
operator|<=
literal|0
condition|)
name|max
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|int
name|maxWaitSecs
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
name|MAX_WAIT_SECONDS_PROP
argument_list|,
literal|60
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|results
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|keepGoing
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|dc
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|ensurePreferredIsLeader
argument_list|(
name|results
argument_list|,
name|slice
argument_list|,
name|currentRequests
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentRequests
operator|.
name|size
argument_list|()
operator|==
name|max
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Queued "
operator|+
name|max
operator|+
literal|" leader reassignments, waiting for some to complete."
argument_list|)
expr_stmt|;
name|keepGoing
operator|=
name|waitForLeaderChange
argument_list|(
name|currentRequests
argument_list|,
name|maxWaitSecs
argument_list|,
literal|false
argument_list|,
name|results
argument_list|)
expr_stmt|;
if|if
condition|(
name|keepGoing
operator|==
literal|false
condition|)
block|{
break|break;
comment|// If we've waited longer than specified, don't continue to wait!
block|}
block|}
block|}
if|if
condition|(
name|keepGoing
operator|==
literal|true
condition|)
block|{
name|keepGoing
operator|=
name|waitForLeaderChange
argument_list|(
name|currentRequests
argument_list|,
name|maxWaitSecs
argument_list|,
literal|true
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keepGoing
operator|==
literal|true
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"All leader reassignments completed."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exceeded specified timeout of ."
operator|+
name|maxWaitSecs
operator|+
literal|"' all leaders may not have been reassigned"
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|addAll
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
DECL|method|ensurePreferredIsLeader
specifier|private
name|void
name|ensurePreferredIsLeader
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|results
parameter_list|,
name|Slice
name|slice
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|currentRequests
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|inactivePreferreds
init|=
literal|"inactivePreferreds"
decl_stmt|;
specifier|final
name|String
name|alreadyLeaders
init|=
literal|"alreadyLeaders"
decl_stmt|;
name|String
name|collectionName
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|COLLECTION_PROP
argument_list|)
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
comment|// Tell the replica to become the leader if we're the preferred leader AND active AND not the leader already
if|if
condition|(
name|replica
operator|.
name|getBool
argument_list|(
name|SliceMutator
operator|.
name|PREFERRED_LEADER_PROP
argument_list|,
literal|false
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
comment|// OK, we are the preferred leader, are we the actual leader?
if|if
condition|(
name|replica
operator|.
name|getBool
argument_list|(
name|LEADER_PROP
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|//We're a preferred leader, but we're _also_ the leader, don't need to do anything.
name|NamedList
argument_list|<
name|Object
argument_list|>
name|noops
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|results
operator|.
name|get
argument_list|(
name|alreadyLeaders
argument_list|)
decl_stmt|;
if|if
condition|(
name|noops
operator|==
literal|null
condition|)
block|{
name|noops
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|alreadyLeaders
argument_list|,
name|noops
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
literal|"Already leader"
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"shard"
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"nodeName"
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|noops
operator|.
name|add
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|res
argument_list|)
expr_stmt|;
return|return;
comment|// already the leader, do nothing.
block|}
comment|// We're the preferred leader, but someone else is leader. Only become leader if we're active.
if|if
condition|(
name|replica
operator|.
name|getState
argument_list|()
operator|!=
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|inactives
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|results
operator|.
name|get
argument_list|(
name|inactivePreferreds
argument_list|)
decl_stmt|;
if|if
condition|(
name|inactives
operator|==
literal|null
condition|)
block|{
name|inactives
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|inactivePreferreds
argument_list|,
name|inactives
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"skipped"
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
literal|"Node is a referredLeader, but it's inactive. Skipping"
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"shard"
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"nodeName"
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|inactives
operator|.
name|add
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|res
argument_list|)
expr_stmt|;
return|return;
comment|// Don't try to become the leader if we're not active!
block|}
comment|// Replica is the preferred leader but not the actual leader, do something about that.
comment|// "Something" is
comment|// 1> if the preferred leader isn't first in line, tell it to re-queue itself.
comment|// 2> tell the actual leader to re-queue itself.
name|ZkStateReader
name|zkStateReader
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|electionNodes
init|=
name|OverseerTaskProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|getShardLeadersElectPath
argument_list|(
name|collectionName
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|electionNodes
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
comment|// if there's only one node in the queue, should already be leader and we shouldn't be here anyway.
name|log
operator|.
name|info
argument_list|(
literal|"Rebalancing leaders and slice "
operator|+
name|slice
operator|.
name|getName
argument_list|()
operator|+
literal|" has less than two elements in the leader "
operator|+
literal|"election queue, but replica "
operator|+
name|replica
operator|.
name|getName
argument_list|()
operator|+
literal|" doesn't think it's the leader."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Ok, the sorting for election nodes is a bit strange. If the sequence numbers are the same, then the whole
comment|// string is used, but that sorts nodes with the same sequence number by their session IDs from ZK.
comment|// While this is determinate, it's not quite what we need, so re-queue nodes that aren't us and are
comment|// watching the leader node..
name|String
name|firstWatcher
init|=
name|electionNodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|firstWatcher
argument_list|)
operator|.
name|equals
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|makeReplicaFirstWatcher
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
name|String
name|coreName
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|rejoinElection
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|electionNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|coreName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|waitForNodeChange
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|electionNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return;
comment|// Done with this slice, skip the rest of the replicas.
block|}
block|}
comment|// Put the replica in at the head of the queue and send all nodes with the same sequence number to the back of the list
DECL|method|makeReplicaFirstWatcher
name|void
name|makeReplicaFirstWatcher
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|Slice
name|slice
parameter_list|,
name|Replica
name|replica
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|electionNodes
init|=
name|OverseerTaskProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|getShardLeadersElectPath
argument_list|(
name|collectionName
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// First, queue up the preferred leader at the head of the queue.
name|int
name|newSeq
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|String
name|electionNode
range|:
name|electionNodes
control|)
block|{
if|if
condition|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNode
argument_list|)
operator|.
name|equals
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|coreName
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNode
argument_list|)
argument_list|)
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|rejoinElection
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|electionNode
argument_list|,
name|coreName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|newSeq
operator|=
name|waitForNodeChange
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|electionNode
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|newSeq
operator|==
operator|-
literal|1
condition|)
block|{
return|return;
comment|// let's not continue if we didn't get what we expect. Possibly we're offline etc..
block|}
comment|// Now find other nodes that have the same sequence number as this node and re-queue them at the end of the queue.
name|electionNodes
operator|=
name|OverseerTaskProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|getShardLeadersElectPath
argument_list|(
name|collectionName
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|thisNode
range|:
name|electionNodes
control|)
block|{
if|if
condition|(
name|LeaderElector
operator|.
name|getSeq
argument_list|(
name|thisNode
argument_list|)
operator|>
name|newSeq
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|thisNode
argument_list|)
operator|.
name|equals
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|LeaderElector
operator|.
name|getSeq
argument_list|(
name|thisNode
argument_list|)
operator|==
name|newSeq
condition|)
block|{
name|String
name|coreName
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|thisNode
argument_list|)
argument_list|)
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|rejoinElection
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|thisNode
argument_list|,
name|coreName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|waitForNodeChange
argument_list|(
name|collectionName
argument_list|,
name|slice
argument_list|,
name|thisNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|waitForNodeChange
name|int
name|waitForNodeChange
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|Slice
name|slice
parameter_list|,
name|String
name|electionNode
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|String
name|nodeName
init|=
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNode
argument_list|)
decl_stmt|;
name|int
name|oldSeq
init|=
name|LeaderElector
operator|.
name|getSeq
argument_list|(
name|electionNode
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|600
condition|;
operator|++
name|idx
control|)
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|electionNodes
init|=
name|OverseerTaskProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|getShardLeadersElectPath
argument_list|(
name|collectionName
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|testNode
range|:
name|electionNodes
control|)
block|{
if|if
condition|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|testNode
argument_list|)
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
operator|&&
name|oldSeq
operator|!=
name|LeaderElector
operator|.
name|getSeq
argument_list|(
name|testNode
argument_list|)
condition|)
block|{
return|return
name|LeaderElector
operator|.
name|getSeq
argument_list|(
name|testNode
argument_list|)
return|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|rejoinElection
specifier|private
name|void
name|rejoinElection
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|Slice
name|slice
parameter_list|,
name|String
name|electionNode
parameter_list|,
name|String
name|core
parameter_list|,
name|boolean
name|rejoinAtHead
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Replica
name|replica
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|electionNode
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|COLLECTION_PROP
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|SHARD_ID_PROP
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|QUEUE_OPERATION
argument_list|,
name|REBALANCELEADERS
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CORE_NAME_PROP
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CORE_NODE_NAME_PROP
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|replica
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|REJOIN_AT_HEAD_PROP
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|rejoinAtHead
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get ourselves to be first in line.
name|propMap
operator|.
name|put
argument_list|(
name|ELECTION_NODE_PROP
argument_list|,
name|electionNode
argument_list|)
expr_stmt|;
name|String
name|asyncId
init|=
name|REBALANCELEADERS
operator|.
name|toLower
argument_list|()
operator|+
literal|"_"
operator|+
name|core
operator|+
literal|"_"
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|ASYNC
argument_list|,
name|asyncId
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|propMap
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rspIgnore
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
comment|// I'm constructing my own response
name|collectionsHandler
operator|.
name|handleResponse
argument_list|(
name|REBALANCELEADERS
operator|.
name|toLower
argument_list|()
argument_list|,
name|m
argument_list|,
name|rspIgnore
argument_list|)
expr_stmt|;
comment|// Want to construct my own response here.
block|}
comment|// currentAsyncIds - map of request IDs and reporting data (value)
comment|// maxWaitSecs - How long are we going to wait? Defaults to 30 seconds.
comment|// waitForAll - if true, do not return until all assignments have been made.
comment|// results - a place to stash results for reporting back to the user.
comment|//
DECL|method|waitForLeaderChange
specifier|private
name|boolean
name|waitForLeaderChange
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|currentAsyncIds
parameter_list|,
specifier|final
name|int
name|maxWaitSecs
parameter_list|,
name|Boolean
name|waitForAll
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|results
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|currentAsyncIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|true
return|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|maxWaitSecs
operator|*
literal|10
condition|;
operator|++
name|idx
control|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iter
init|=
name|currentAsyncIds
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|boolean
name|foundChange
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pair
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|asyncId
init|=
name|pair
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerFailureMap
argument_list|()
operator|.
name|contains
argument_list|(
name|asyncId
argument_list|)
condition|)
block|{
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerFailureMap
argument_list|()
operator|.
name|remove
argument_list|(
name|asyncId
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|fails
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|results
operator|.
name|get
argument_list|(
literal|"failures"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fails
operator|==
literal|null
condition|)
block|{
name|fails
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
literal|"failures"
argument_list|,
name|fails
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"failed"
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
literal|"Failed to assign '"
operator|+
name|pair
operator|.
name|getValue
argument_list|()
operator|+
literal|"' to be leader"
argument_list|)
expr_stmt|;
name|fails
operator|.
name|add
argument_list|(
name|asyncId
operator|.
name|substring
argument_list|(
name|REBALANCELEADERS
operator|.
name|toLower
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|foundChange
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerCompletedMap
argument_list|()
operator|.
name|contains
argument_list|(
name|asyncId
argument_list|)
condition|)
block|{
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerCompletedMap
argument_list|()
operator|.
name|remove
argument_list|(
name|asyncId
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|successes
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|results
operator|.
name|get
argument_list|(
literal|"successes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|successes
operator|==
literal|null
condition|)
block|{
name|successes
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
literal|"successes"
argument_list|,
name|successes
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
literal|"Assigned '"
operator|+
name|pair
operator|.
name|getValue
argument_list|()
operator|+
literal|"' to be leader"
argument_list|)
expr_stmt|;
name|successes
operator|.
name|add
argument_list|(
name|asyncId
operator|.
name|substring
argument_list|(
name|REBALANCELEADERS
operator|.
name|toLower
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|foundChange
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// We're done if we're processing a few at a time or all requests are processed.
if|if
condition|(
operator|(
name|foundChange
operator|&&
name|waitForAll
operator|==
literal|false
operator|)
operator|||
name|currentAsyncIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|//TODO: Is there a better thing to do than sleep here?
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

