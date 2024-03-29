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
name|Collection
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
name|params
operator|.
name|CoreAdminParams
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
name|params
operator|.
name|ShardParams
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
name|handler
operator|.
name|component
operator|.
name|ShardHandler
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
name|Assign
operator|.
name|getNodesForNewReplicas
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
name|OverseerCollectionMessageHandler
operator|.
name|COLL_CONF
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
name|OverseerCollectionMessageHandler
operator|.
name|SKIP_CREATE_REPLICA_IN_CLUSTER_STATE
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
name|ADDREPLICA
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

begin_class
DECL|class|AddReplicaCmd
specifier|public
class|class
name|AddReplicaCmd
implements|implements
name|OverseerCollectionMessageHandler
operator|.
name|Cmd
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
DECL|field|ocmh
specifier|private
specifier|final
name|OverseerCollectionMessageHandler
name|ocmh
decl_stmt|;
DECL|method|AddReplicaCmd
specifier|public
name|AddReplicaCmd
parameter_list|(
name|OverseerCollectionMessageHandler
name|ocmh
parameter_list|)
block|{
name|this
operator|.
name|ocmh
operator|=
name|ocmh
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call
specifier|public
name|void
name|call
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|,
name|NamedList
name|results
parameter_list|)
throws|throws
name|Exception
block|{
name|addReplica
argument_list|(
name|ocmh
operator|.
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|message
argument_list|,
name|results
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addReplica
name|ZkNodeProps
name|addReplica
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|,
name|NamedList
name|results
parameter_list|,
name|Runnable
name|onComplete
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"addReplica() : {}"
argument_list|,
name|Utils
operator|.
name|toJSONString
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|collection
init|=
name|message
operator|.
name|getStr
argument_list|(
name|COLLECTION_PROP
argument_list|)
decl_stmt|;
name|String
name|node
init|=
name|message
operator|.
name|getStr
argument_list|(
name|CoreAdminParams
operator|.
name|NODE
argument_list|)
decl_stmt|;
name|String
name|shard
init|=
name|message
operator|.
name|getStr
argument_list|(
name|SHARD_ID_PROP
argument_list|)
decl_stmt|;
name|String
name|coreName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|Replica
operator|.
name|Type
name|replicaType
init|=
name|Replica
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|message
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|REPLICA_TYPE
argument_list|,
name|Replica
operator|.
name|Type
operator|.
name|NRT
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|parallel
init|=
name|message
operator|.
name|getBool
argument_list|(
literal|"parallel"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|coreName
argument_list|)
condition|)
block|{
name|coreName
operator|=
name|message
operator|.
name|getStr
argument_list|(
name|CoreAdminParams
operator|.
name|PROPERTY_PREFIX
operator|+
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|asyncId
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ASYNC
argument_list|)
decl_stmt|;
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|coll
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
literal|"Collection: "
operator|+
name|collection
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|coll
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
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
literal|"Collection: "
operator|+
name|collection
operator|+
literal|" shard: "
operator|+
name|shard
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|ShardHandler
name|shardHandler
init|=
name|ocmh
operator|.
name|shardHandlerFactory
operator|.
name|getShardHandler
argument_list|()
decl_stmt|;
name|boolean
name|skipCreateReplicaInClusterState
init|=
name|message
operator|.
name|getBool
argument_list|(
name|SKIP_CREATE_REPLICA_IN_CLUSTER_STATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Kind of unnecessary, but it does put the logic of whether to override maxShardsPerNode in one place.
if|if
condition|(
operator|!
name|skipCreateReplicaInClusterState
condition|)
block|{
name|node
operator|=
name|getNodesForNewReplicas
argument_list|(
name|clusterState
argument_list|,
name|collection
argument_list|,
name|shard
argument_list|,
literal|1
argument_list|,
name|node
argument_list|,
name|ocmh
operator|.
name|overseer
operator|.
name|getZkController
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|nodeName
expr_stmt|;
comment|// TODO: use replica type in this logic too
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Node Identified {} for creating new replica"
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|node
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
literal|"Node: "
operator|+
name|node
operator|+
literal|" is not live"
argument_list|)
throw|;
block|}
if|if
condition|(
name|coreName
operator|==
literal|null
condition|)
block|{
name|coreName
operator|=
name|Assign
operator|.
name|buildCoreName
argument_list|(
name|coll
argument_list|,
name|shard
argument_list|,
name|replicaType
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|skipCreateReplicaInClusterState
condition|)
block|{
comment|//Validate that the core name is unique in that collection
for|for
control|(
name|Slice
name|slice
range|:
name|coll
operator|.
name|getSlices
argument_list|()
control|)
block|{
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
name|String
name|replicaCoreName
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|coreName
operator|.
name|equals
argument_list|(
name|replicaCoreName
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
literal|"Another replica with the same core name already exists"
operator|+
literal|" for this collection"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|ocmh
operator|.
name|zkStateReader
decl_stmt|;
if|if
condition|(
operator|!
name|Overseer
operator|.
name|isLegacy
argument_list|(
name|zkStateReader
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|skipCreateReplicaInClusterState
condition|)
block|{
name|ZkNodeProps
name|props
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|ADDREPLICA
operator|.
name|toLower
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
name|collection
argument_list|,
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
name|shard
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
name|coreName
argument_list|,
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|DOWN
operator|.
name|toString
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|node
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|,
name|node
argument_list|,
name|ZkStateReader
operator|.
name|REPLICA_TYPE
argument_list|,
name|replicaType
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|Overseer
operator|.
name|getStateUpdateQueue
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|props
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE_NODE_NAME
argument_list|,
name|ocmh
operator|.
name|waitToSeeReplicasInState
argument_list|(
name|collection
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|coreName
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|coreName
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|configName
init|=
name|zkStateReader
operator|.
name|readConfigName
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|String
name|routeKey
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|)
decl_stmt|;
name|String
name|dataDir
init|=
name|message
operator|.
name|getStr
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|)
decl_stmt|;
name|String
name|instanceDir
init|=
name|message
operator|.
name|getStr
argument_list|(
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|COLL_CONF
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|COLLECTION
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|REPLICA_TYPE
argument_list|,
name|replicaType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|SHARD
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|routeKey
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|coll
operator|.
name|getRouter
argument_list|()
operator|.
name|getSearchSlicesSingle
argument_list|(
name|routeKey
argument_list|,
literal|null
argument_list|,
name|coll
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|.
name|isEmpty
argument_list|()
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
literal|"No active shard serving _route_="
operator|+
name|routeKey
operator|+
literal|" found"
argument_list|)
throw|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|SHARD
argument_list|,
name|slices
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
literal|"Specify either 'shard' or _route_ param"
argument_list|)
throw|;
block|}
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|instanceDir
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
block|}
name|ocmh
operator|.
name|addPropertyParams
argument_list|(
name|message
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// For tracking async calls.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|requestMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ocmh
operator|.
name|sendShardRequest
argument_list|(
name|node
argument_list|,
name|params
argument_list|,
name|shardHandler
argument_list|,
name|asyncId
argument_list|,
name|requestMap
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fnode
init|=
name|node
decl_stmt|;
specifier|final
name|String
name|fcoreName
init|=
name|coreName
decl_stmt|;
name|Runnable
name|runnable
init|=
parameter_list|()
lambda|->
block|{
name|ocmh
operator|.
name|processResponses
argument_list|(
name|results
argument_list|,
name|shardHandler
argument_list|,
literal|true
argument_list|,
literal|"ADDREPLICA failed to create replica"
argument_list|,
name|asyncId
argument_list|,
name|requestMap
argument_list|)
expr_stmt|;
name|ocmh
operator|.
name|waitForCoreNodeName
argument_list|(
name|collection
argument_list|,
name|fnode
argument_list|,
name|fcoreName
argument_list|)
expr_stmt|;
if|if
condition|(
name|onComplete
operator|!=
literal|null
condition|)
name|onComplete
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|parallel
condition|)
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ocmh
operator|.
name|tpe
operator|.
name|submit
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
name|collection
argument_list|,
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
name|shard
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
name|coreName
argument_list|,
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
end_class

end_unit

