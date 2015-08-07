begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud.overseer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|overseer
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
name|Collection
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
name|LinkedHashMap
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|OverseerCollectionMessageHandler
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
name|DocRouter
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
name|ImplicitDocRouter
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
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_class
DECL|class|ClusterStateMutator
specifier|public
class|class
name|ClusterStateMutator
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
name|ClusterStateMutator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkStateReader
specifier|protected
specifier|final
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|method|ClusterStateMutator
specifier|public
name|ClusterStateMutator
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|)
block|{
name|this
operator|.
name|zkStateReader
operator|=
name|zkStateReader
expr_stmt|;
block|}
DECL|method|createCollection
specifier|public
name|ZkWriteCommand
name|createCollection
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|cName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"building a new cName: "
operator|+
name|cName
argument_list|)
expr_stmt|;
if|if
condition|(
name|clusterState
operator|.
name|hasCollection
argument_list|(
name|cName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Collection {} already exists. exit"
argument_list|,
name|cName
argument_list|)
expr_stmt|;
return|return
name|ZkStateWriter
operator|.
name|NO_OP
return|;
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|shards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|ImplicitDocRouter
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|message
operator|.
name|getStr
argument_list|(
literal|"router.name"
argument_list|,
name|DocRouter
operator|.
name|DEFAULT_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|getShardNames
argument_list|(
name|shards
argument_list|,
name|message
operator|.
name|getStr
argument_list|(
literal|"shards"
argument_list|,
name|DocRouter
operator|.
name|DEFAULT_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|numShards
init|=
name|message
operator|.
name|getInt
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|numShards
operator|<
literal|1
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"numShards is a required parameter for 'compositeId' router"
argument_list|)
throw|;
name|getShardNames
argument_list|(
name|numShards
argument_list|,
name|shards
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|routerSpec
init|=
name|DocRouter
operator|.
name|getRouterSpec
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|String
name|routerName
init|=
name|routerSpec
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
operator|==
literal|null
condition|?
name|DocRouter
operator|.
name|DEFAULT_NAME
else|:
operator|(
name|String
operator|)
name|routerSpec
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
name|DocRouter
name|router
init|=
name|DocRouter
operator|.
name|getDocRouter
argument_list|(
name|routerName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
name|router
operator|.
name|partitionRange
argument_list|(
name|shards
operator|.
name|size
argument_list|()
argument_list|,
name|router
operator|.
name|fullRange
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|newSlices
init|=
operator|new
name|LinkedHashMap
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
name|shards
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|sliceName
init|=
name|shards
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sliceProps
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|sliceProps
operator|.
name|put
argument_list|(
name|Slice
operator|.
name|RANGE
argument_list|,
name|ranges
operator|==
literal|null
condition|?
literal|null
else|:
name|ranges
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|newSlices
operator|.
name|put
argument_list|(
name|sliceName
argument_list|,
operator|new
name|Slice
argument_list|(
name|sliceName
argument_list|,
literal|null
argument_list|,
name|sliceProps
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collectionProps
init|=
operator|new
name|HashMap
argument_list|<>
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
name|Object
argument_list|>
name|e
range|:
name|OverseerCollectionMessageHandler
operator|.
name|COLL_PROPS
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|message
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
name|OverseerCollectionMessageHandler
operator|.
name|COLL_PROPS
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|collectionProps
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|collectionProps
operator|.
name|put
argument_list|(
name|DocCollection
operator|.
name|DOC_ROUTER
argument_list|,
name|routerSpec
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getStr
argument_list|(
literal|"fromApi"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|collectionProps
operator|.
name|put
argument_list|(
literal|"autoCreated"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|String
name|znode
init|=
name|message
operator|.
name|getInt
argument_list|(
name|DocCollection
operator|.
name|STATE_FORMAT
argument_list|,
literal|1
argument_list|)
operator|==
literal|1
condition|?
literal|null
else|:
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
name|cName
argument_list|)
decl_stmt|;
name|DocCollection
name|newCollection
init|=
operator|new
name|DocCollection
argument_list|(
name|cName
argument_list|,
name|newSlices
argument_list|,
name|collectionProps
argument_list|,
name|router
argument_list|,
operator|-
literal|1
argument_list|,
name|znode
argument_list|)
decl_stmt|;
return|return
operator|new
name|ZkWriteCommand
argument_list|(
name|cName
argument_list|,
name|newCollection
argument_list|)
return|;
block|}
DECL|method|deleteCollection
specifier|public
name|ZkWriteCommand
name|deleteCollection
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
specifier|final
name|String
name|collection
init|=
name|message
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|CollectionMutator
operator|.
name|checkKeyExistence
argument_list|(
name|message
argument_list|,
name|NAME
argument_list|)
condition|)
return|return
name|ZkStateWriter
operator|.
name|NO_OP
return|;
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollectionOrNull
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
return|return
name|ZkStateWriter
operator|.
name|NO_OP
return|;
return|return
operator|new
name|ZkWriteCommand
argument_list|(
name|coll
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newState
specifier|public
specifier|static
name|ClusterState
name|newState
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|String
name|name
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
name|ClusterState
name|newClusterState
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|newClusterState
operator|=
name|state
operator|.
name|copyWith
argument_list|(
name|name
argument_list|,
operator|(
name|DocCollection
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newClusterState
operator|=
name|state
operator|.
name|copyWith
argument_list|(
name|name
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|newClusterState
return|;
block|}
DECL|method|getShardNames
specifier|public
specifier|static
name|void
name|getShardNames
parameter_list|(
name|Integer
name|numShards
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|shardNames
parameter_list|)
block|{
if|if
condition|(
name|numShards
operator|==
literal|null
condition|)
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
literal|"numShards"
operator|+
literal|" is a required param"
argument_list|)
throw|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numShards
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|sliceName
init|=
literal|"shard"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
decl_stmt|;
name|shardNames
operator|.
name|add
argument_list|(
name|sliceName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getShardNames
specifier|public
specifier|static
name|void
name|getShardNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|shardNames
parameter_list|,
name|String
name|shards
parameter_list|)
block|{
if|if
condition|(
name|shards
operator|==
literal|null
condition|)
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
literal|"shards"
operator|+
literal|" is a required param"
argument_list|)
throw|;
for|for
control|(
name|String
name|s
range|:
name|shards
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
continue|continue;
name|shardNames
operator|.
name|add
argument_list|(
name|s
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardNames
operator|.
name|isEmpty
argument_list|()
condition|)
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
literal|"shards"
operator|+
literal|" is a required param"
argument_list|)
throw|;
block|}
comment|/*        * Return an already assigned id or null if not assigned        */
DECL|method|getAssignedId
specifier|public
specifier|static
name|String
name|getAssignedId
parameter_list|(
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|String
name|nodeName
parameter_list|,
specifier|final
name|ZkNodeProps
name|coreState
parameter_list|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|state
operator|.
name|getSlices
argument_list|(
name|coreState
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
if|if
condition|(
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|get
argument_list|(
name|nodeName
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|slice
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getAssignedCoreNodeName
specifier|public
specifier|static
name|String
name|getAssignedCoreNodeName
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|state
operator|.
name|getSlices
argument_list|(
name|message
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|slices
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
name|nodeName
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
decl_stmt|;
name|String
name|core
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|String
name|msgNodeName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
decl_stmt|;
name|String
name|msgCore
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|equals
argument_list|(
name|msgNodeName
argument_list|)
operator|&&
name|core
operator|.
name|equals
argument_list|(
name|msgCore
argument_list|)
condition|)
block|{
return|return
name|replica
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|migrateStateFormat
specifier|public
name|ZkWriteCommand
name|migrateStateFormat
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
specifier|final
name|String
name|collection
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|CollectionMutator
operator|.
name|checkKeyExistence
argument_list|(
name|message
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|)
condition|)
return|return
name|ZkStateWriter
operator|.
name|NO_OP
return|;
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollectionOrNull
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
operator|||
name|coll
operator|.
name|getStateFormat
argument_list|()
operator|==
literal|2
condition|)
return|return
name|ZkStateWriter
operator|.
name|NO_OP
return|;
return|return
operator|new
name|ZkWriteCommand
argument_list|(
name|coll
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|DocCollection
argument_list|(
name|coll
operator|.
name|getName
argument_list|()
argument_list|,
name|coll
operator|.
name|getSlicesMap
argument_list|()
argument_list|,
name|coll
operator|.
name|getProperties
argument_list|()
argument_list|,
name|coll
operator|.
name|getRouter
argument_list|()
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
name|collection
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

