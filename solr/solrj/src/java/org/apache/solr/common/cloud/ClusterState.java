begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
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
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
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
name|SolrException
operator|.
name|ErrorCode
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
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
comment|/**  * Immutable state of the cloud. Normally you can get the state by using  * {@link ZkStateReader#getClusterState()}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ClusterState
specifier|public
class|class
name|ClusterState
implements|implements
name|JSONWriter
operator|.
name|Writable
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
name|ClusterState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkClusterStateVersion
specifier|private
name|Integer
name|zkClusterStateVersion
decl_stmt|;
DECL|field|collectionStates
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collectionStates
decl_stmt|;
comment|// Map<collectionName, Map<sliceName,Slice>>
DECL|field|liveNodes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
decl_stmt|;
DECL|field|stateReader
specifier|private
specifier|final
name|ZkStateReader
name|stateReader
decl_stmt|;
comment|/**    * Use this constr when ClusterState is meant for publication.    *     * hashCode and equals will only depend on liveNodes and not clusterStateVersion.    */
annotation|@
name|Deprecated
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collectionStates
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|liveNodes
argument_list|,
name|collectionStates
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated prefer another constructor    */
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|Integer
name|zkClusterStateVersion
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collectionStates
parameter_list|)
block|{
name|this
argument_list|(
name|zkClusterStateVersion
argument_list|,
name|liveNodes
argument_list|,
name|collectionStates
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Use this constr when ClusterState is meant for consumption.    */
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|Integer
name|zkClusterStateVersion
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collectionStates
parameter_list|,
name|ZkStateReader
name|stateReader
parameter_list|)
block|{
assert|assert
name|stateReader
operator|!=
literal|null
assert|;
name|this
operator|.
name|zkClusterStateVersion
operator|=
name|zkClusterStateVersion
expr_stmt|;
name|this
operator|.
name|liveNodes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|liveNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|liveNodes
operator|.
name|addAll
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionStates
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|collectionStates
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionStates
operator|.
name|putAll
argument_list|(
name|collectionStates
argument_list|)
expr_stmt|;
name|this
operator|.
name|stateReader
operator|=
name|stateReader
expr_stmt|;
block|}
DECL|method|copyWith
specifier|public
name|ClusterState
name|copyWith
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|modified
parameter_list|)
block|{
name|ClusterState
name|result
init|=
operator|new
name|ClusterState
argument_list|(
name|zkClusterStateVersion
argument_list|,
name|liveNodes
argument_list|,
name|collectionStates
argument_list|,
name|stateReader
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|e
range|:
name|modified
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DocCollection
name|c
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|collectionStates
operator|.
name|remove
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|result
operator|.
name|collectionStates
operator|.
name|put
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Get the lead replica for specific collection, or null if one currently doesn't exist.    */
DECL|method|getLeader
specifier|public
name|Replica
name|getLeader
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|sliceName
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
literal|null
return|;
name|Slice
name|slice
init|=
name|coll
operator|.
name|getSlice
argument_list|(
name|sliceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|slice
operator|.
name|getLeader
argument_list|()
return|;
block|}
DECL|method|hasCollection
specifier|public
name|boolean
name|hasCollection
parameter_list|(
name|String
name|coll
parameter_list|)
block|{
if|if
condition|(
name|collectionStates
operator|.
name|containsKey
argument_list|(
name|coll
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
name|stateReader
operator|.
name|getAllCollections
argument_list|()
operator|.
name|contains
argument_list|(
name|coll
argument_list|)
return|;
block|}
comment|/**    * Get the named Slice for collection, or null if not found.    */
DECL|method|getSlice
specifier|public
name|Slice
name|getSlice
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|sliceName
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
literal|null
return|;
return|return
name|coll
operator|.
name|getSlice
argument_list|(
name|sliceName
argument_list|)
return|;
block|}
DECL|method|getSlicesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getSlicesMap
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
literal|null
return|;
return|return
name|coll
operator|.
name|getSlicesMap
argument_list|()
return|;
block|}
DECL|method|getActiveSlicesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getActiveSlicesMap
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
literal|null
return|;
return|return
name|coll
operator|.
name|getActiveSlicesMap
argument_list|()
return|;
block|}
DECL|method|getSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSlices
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
literal|null
return|;
return|return
name|coll
operator|.
name|getSlices
argument_list|()
return|;
block|}
DECL|method|getActiveSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getActiveSlices
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
literal|null
return|;
return|return
name|coll
operator|.
name|getActiveSlices
argument_list|()
return|;
block|}
comment|/**    * Get the {@code DocCollection} object if available. This method will    * never hit ZooKeeper and attempt to fetch collection from locally available    * state only.    *    * @param collection the name of the collection    * @return the {@link org.apache.solr.common.cloud.DocCollection} or null if not found    */
DECL|method|getCachedCollection
specifier|public
name|DocCollection
name|getCachedCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|c
init|=
name|collectionStates
operator|.
name|get
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
return|return
name|c
return|;
if|if
condition|(
operator|!
name|stateReader
operator|.
name|getAllCollections
argument_list|()
operator|.
name|contains
argument_list|(
name|collection
argument_list|)
condition|)
return|return
literal|null
return|;
return|return
name|stateReader
operator|.
name|getExternCollection
argument_list|(
name|collection
argument_list|,
literal|true
argument_list|)
return|;
comment|// return from cache
block|}
comment|/**    * Gets the replica from caches by the core name (assuming the slice is unknown) or null if replica is not found.    * If the slice is known, do not use this method.    * coreNodeName is the same as replicaName    */
DECL|method|getCachedReplica
specifier|public
name|Replica
name|getCachedReplica
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|coreNodeName
parameter_list|)
block|{
name|DocCollection
name|c
init|=
name|getCachedCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|Slice
name|slice
range|:
name|c
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|Replica
name|replica
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|coreNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
return|return
name|replica
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get the named DocCollection object, or throw an exception if it doesn't exist.    */
DECL|method|getCollection
specifier|public
name|DocCollection
name|getCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Could not find collection : "
operator|+
name|collection
argument_list|)
throw|;
return|return
name|coll
return|;
block|}
DECL|method|loadExtDocCollection
specifier|private
name|DocCollection
name|loadExtDocCollection
parameter_list|(
name|String
name|coll
parameter_list|)
block|{
return|return
name|stateReader
operator|.
name|getExternCollection
argument_list|(
name|coll
argument_list|)
return|;
block|}
DECL|method|getCollectionOrNull
specifier|public
name|DocCollection
name|getCollectionOrNull
parameter_list|(
name|String
name|coll
parameter_list|)
block|{
name|DocCollection
name|c
init|=
name|collectionStates
operator|.
name|get
argument_list|(
name|coll
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
return|return
name|c
return|;
if|if
condition|(
operator|!
name|stateReader
operator|.
name|getAllCollections
argument_list|()
operator|.
name|contains
argument_list|(
name|coll
argument_list|)
condition|)
return|return
literal|null
return|;
return|return
name|loadExtDocCollection
argument_list|(
name|coll
argument_list|)
return|;
block|}
comment|/**    * Get collection names.    */
DECL|method|getCollections
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getCollections
parameter_list|()
block|{
return|return
name|stateReader
operator|.
name|getAllCollections
argument_list|()
return|;
block|}
comment|/**    * @deprecated use #getAllCollections instead    * @return Map&lt;collectionName, Map&lt;sliceName,Slice&gt;&gt;    */
annotation|@
name|Deprecated
DECL|method|getCollectionStates
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|getCollectionStates
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|collectionStates
argument_list|)
return|;
block|}
comment|/**    * Get names of the currently live nodes.    */
DECL|method|getLiveNodes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getLiveNodes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|liveNodes
argument_list|)
return|;
block|}
DECL|method|getShardId
specifier|public
name|String
name|getShardId
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
return|return
name|getShardId
argument_list|(
literal|null
argument_list|,
name|nodeName
argument_list|,
name|coreName
argument_list|)
return|;
block|}
DECL|method|getShardId
specifier|public
name|String
name|getShardId
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
name|Collection
argument_list|<
name|DocCollection
argument_list|>
name|states
init|=
name|collectionStates
operator|.
name|values
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectionName
operator|!=
literal|null
condition|)
block|{
name|DocCollection
name|c
init|=
name|getCollectionOrNull
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
name|states
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DocCollection
name|coll
range|:
name|states
control|)
block|{
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
comment|// TODO: for really large clusters, we could 'index' on this
name|String
name|rnodeName
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
name|rcore
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
if|if
condition|(
name|nodeName
operator|.
name|equals
argument_list|(
name|rnodeName
argument_list|)
operator|&&
name|coreName
operator|.
name|equals
argument_list|(
name|rcore
argument_list|)
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
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Check if node is alive.     */
DECL|method|liveNodesContain
specifier|public
name|boolean
name|liveNodesContain
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|liveNodes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"live nodes:"
operator|+
name|liveNodes
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" collections:"
operator|+
name|collectionStates
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Create ClusterState by reading the current state from zookeeper.     */
DECL|method|load
specifier|public
specifier|static
name|ClusterState
name|load
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|ZkStateReader
name|stateReader
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|state
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|load
argument_list|(
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|state
argument_list|,
name|liveNodes
argument_list|,
name|stateReader
argument_list|)
return|;
block|}
comment|/**    * Create ClusterState from json string that is typically stored in zookeeper.    *     * Use {@link ClusterState#load(SolrZkClient, Set, ZkStateReader)} instead, unless you want to    * do something more when getting the data - such as get the stat, set watch, etc.    * @param version zk version of the clusterstate.json file (bytes)    * @param bytes clusterstate.json as a byte array    * @param liveNodes list of live nodes    * @return the ClusterState    */
DECL|method|load
specifier|public
specifier|static
name|ClusterState
name|load
parameter_list|(
name|Integer
name|version
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|ZkStateReader
name|stateReader
parameter_list|)
block|{
comment|// System.out.println("######## ClusterState.load:" + (bytes==null ? null : new String(bytes)));
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|ClusterState
argument_list|(
name|version
argument_list|,
name|liveNodes
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|DocCollection
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|stateReader
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stateMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ZkStateReader
operator|.
name|fromJSON
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collections
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|stateMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|stateMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|collectionName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|DocCollection
name|coll
init|=
name|collectionFromObjects
argument_list|(
name|collectionName
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|version
argument_list|)
decl_stmt|;
name|collections
operator|.
name|put
argument_list|(
name|collectionName
argument_list|,
name|coll
argument_list|)
expr_stmt|;
block|}
comment|// System.out.println("######## ClusterState.load result:" + collections);
return|return
operator|new
name|ClusterState
argument_list|(
name|version
argument_list|,
name|liveNodes
argument_list|,
name|collections
argument_list|,
name|stateReader
argument_list|)
return|;
block|}
comment|/**    * @deprecated use {@link #load(Integer, byte[], Set, ZkStateReader)}    */
annotation|@
name|Deprecated
DECL|method|load
specifier|public
specifier|static
name|ClusterState
name|load
parameter_list|(
name|Integer
name|version
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|)
block|{
return|return
name|load
argument_list|(
name|version
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|version
argument_list|,
name|bytes
argument_list|,
name|liveNodes
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|load
specifier|public
specifier|static
name|Aliases
name|load
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|Aliases
argument_list|()
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|aliasMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
operator|)
name|ZkStateReader
operator|.
name|fromJSON
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
return|return
operator|new
name|Aliases
argument_list|(
name|aliasMap
argument_list|)
return|;
block|}
DECL|method|collectionFromObjects
specifier|private
specifier|static
name|DocCollection
name|collectionFromObjects
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|objs
parameter_list|,
name|Integer
name|version
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sliceObjs
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|objs
operator|.
name|get
argument_list|(
name|DocCollection
operator|.
name|SHARDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|sliceObjs
operator|==
literal|null
condition|)
block|{
comment|// legacy format from 4.0... there was no separate "shards" level to contain the collection shards.
name|slices
operator|=
name|makeSlices
argument_list|(
name|objs
argument_list|)
expr_stmt|;
name|props
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|slices
operator|=
name|makeSlices
argument_list|(
name|sliceObjs
argument_list|)
expr_stmt|;
name|props
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|objs
argument_list|)
expr_stmt|;
name|objs
operator|.
name|remove
argument_list|(
name|DocCollection
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
block|}
name|Object
name|routerObj
init|=
name|props
operator|.
name|get
argument_list|(
name|DocCollection
operator|.
name|DOC_ROUTER
argument_list|)
decl_stmt|;
name|DocRouter
name|router
decl_stmt|;
if|if
condition|(
name|routerObj
operator|==
literal|null
condition|)
block|{
name|router
operator|=
name|DocRouter
operator|.
name|DEFAULT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|routerObj
operator|instanceof
name|String
condition|)
block|{
comment|// back compat with Solr4.4
name|router
operator|=
name|DocRouter
operator|.
name|getDocRouter
argument_list|(
operator|(
name|String
operator|)
name|routerObj
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
name|routerProps
init|=
operator|(
name|Map
operator|)
name|routerObj
decl_stmt|;
name|router
operator|=
name|DocRouter
operator|.
name|getDocRouter
argument_list|(
name|routerProps
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DocCollection
argument_list|(
name|name
argument_list|,
name|slices
argument_list|,
name|props
argument_list|,
name|router
argument_list|,
name|version
argument_list|)
return|;
block|}
DECL|method|makeSlices
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|makeSlices
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|genericSlices
parameter_list|)
block|{
if|if
condition|(
name|genericSlices
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|genericSlices
operator|.
name|size
argument_list|()
argument_list|)
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
name|entry
range|:
name|genericSlices
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Slice
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|(
name|Slice
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Map
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Slice
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|jsonWriter
parameter_list|)
block|{
name|jsonWriter
operator|.
name|write
argument_list|(
name|collectionStates
argument_list|)
expr_stmt|;
block|}
comment|/**    * The version of clusterstate.json in ZooKeeper.    *     * @return null if ClusterState was created for publication, not consumption    */
DECL|method|getZkClusterStateVersion
specifier|public
name|Integer
name|getZkClusterStateVersion
parameter_list|()
block|{
return|return
name|zkClusterStateVersion
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|zkClusterStateVersion
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|zkClusterStateVersion
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|liveNodes
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|liveNodes
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ClusterState
name|other
init|=
operator|(
name|ClusterState
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|zkClusterStateVersion
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|zkClusterStateVersion
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|zkClusterStateVersion
operator|.
name|equals
argument_list|(
name|other
operator|.
name|zkClusterStateVersion
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|liveNodes
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|liveNodes
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|liveNodes
operator|.
name|equals
argument_list|(
name|other
operator|.
name|liveNodes
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|hasExternalCollection
specifier|public
name|boolean
name|hasExternalCollection
parameter_list|(
name|String
name|coll
parameter_list|)
block|{
return|return
name|stateReader
operator|.
name|getAllCollections
argument_list|()
operator|.
name|contains
argument_list|(
name|coll
argument_list|)
operator|&&
operator|!
name|collectionStates
operator|.
name|containsKey
argument_list|(
name|coll
argument_list|)
return|;
block|}
DECL|method|getAllInternalCollections
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAllInternalCollections
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|collectionStates
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getStateReader
specifier|public
name|ZkStateReader
name|getStateReader
parameter_list|()
block|{
return|return
name|stateReader
return|;
block|}
comment|/**    * Internal API used only by ZkStateReader    */
DECL|method|setLiveNodes
name|void
name|setLiveNodes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|)
block|{
name|this
operator|.
name|liveNodes
operator|=
name|liveNodes
expr_stmt|;
block|}
DECL|method|getCommonCollection
specifier|public
name|DocCollection
name|getCommonCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|collectionStates
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

