begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Overseer
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
name|stats
operator|.
name|TimerContext
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_comment
comment|/**  * ZkStateWriter is responsible for writing updates to the cluster state stored in ZooKeeper for  * both stateFormat=1 collection (stored in shared /clusterstate.json in ZK) and stateFormat=2 collections  * each of which get their own individual state.json in ZK.  *  * Updates to the cluster state are specified using the  * {@link #enqueueUpdate(ClusterState, ZkWriteCommand, ZkWriteCallback)} method. The class buffers updates  * to reduce the number of writes to ZK. The buffered updates are flushed during<code>enqueueUpdate</code>  * automatically if necessary. The {@link #writePendingUpdates()} can be used to force flush any pending updates.  *  * If either {@link #enqueueUpdate(ClusterState, ZkWriteCommand, ZkWriteCallback)} or {@link #writePendingUpdates()}  * throws a {@link org.apache.zookeeper.KeeperException.BadVersionException} then the internal buffered state of the  * class is suspect and the current instance of the class should be discarded and a new instance should be created  * and used for any future updates.  */
end_comment

begin_class
DECL|class|ZkStateWriter
specifier|public
class|class
name|ZkStateWriter
block|{
DECL|field|MAX_FLUSH_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|MAX_FLUSH_INTERVAL
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|Overseer
operator|.
name|STATE_UPDATE_DELAY
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
comment|/**    * Represents a no-op {@link ZkWriteCommand} which will result in no modification to cluster state    */
DECL|field|NO_OP
specifier|public
specifier|static
name|ZkWriteCommand
name|NO_OP
init|=
name|ZkWriteCommand
operator|.
name|noop
argument_list|()
decl_stmt|;
DECL|field|reader
specifier|protected
specifier|final
name|ZkStateReader
name|reader
decl_stmt|;
DECL|field|stats
specifier|protected
specifier|final
name|Overseer
operator|.
name|Stats
name|stats
decl_stmt|;
DECL|field|updates
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|updates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|clusterState
specifier|protected
name|ClusterState
name|clusterState
init|=
literal|null
decl_stmt|;
DECL|field|isClusterStateModified
specifier|protected
name|boolean
name|isClusterStateModified
init|=
literal|false
decl_stmt|;
DECL|field|lastUpdatedTime
specifier|protected
name|long
name|lastUpdatedTime
init|=
literal|0
decl_stmt|;
comment|// state information which helps us batch writes
DECL|field|lastStateFormat
specifier|protected
name|int
name|lastStateFormat
init|=
operator|-
literal|1
decl_stmt|;
comment|// sentinel value
DECL|field|lastCollectionName
specifier|protected
name|String
name|lastCollectionName
init|=
literal|null
decl_stmt|;
comment|/**    * Set to true if we ever get a BadVersionException so that we can disallow future operations    * with this instance    */
DECL|field|invalidState
specifier|protected
name|boolean
name|invalidState
init|=
literal|false
decl_stmt|;
DECL|method|ZkStateWriter
specifier|public
name|ZkStateWriter
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|Overseer
operator|.
name|Stats
name|stats
parameter_list|)
block|{
assert|assert
name|zkStateReader
operator|!=
literal|null
assert|;
name|this
operator|.
name|reader
operator|=
name|zkStateReader
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|clusterState
operator|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
block|}
comment|/**    * Applies the given {@link ZkWriteCommand} on the<code>prevState</code>. The modified    * {@link ClusterState} is returned and it is expected that the caller will use the returned    * cluster state for the subsequent invocation of this method.    *<p>    * The modified state may be buffered or flushed to ZooKeeper depending on the internal buffering    * logic of this class. The {@link #hasPendingUpdates()} method may be used to determine if the    * last enqueue operation resulted in buffered state. The method {@link #writePendingUpdates()} can    * be used to force an immediate flush of pending cluster state changes.    *    * @param prevState the cluster state information on which the given<code>cmd</code> is applied    * @param cmd       the {@link ZkWriteCommand} which specifies the change to be applied to cluster state    * @param callback  a {@link org.apache.solr.cloud.overseer.ZkStateWriter.ZkWriteCallback} object to be used    *                  for any callbacks    * @return modified cluster state created after applying<code>cmd</code> to<code>prevState</code>. If    *<code>cmd</code> is a no-op ({@link #NO_OP}) then the<code>prevState</code> is returned unmodified.    * @throws IllegalStateException if the current instance is no longer usable. The current instance must be    *                               discarded.    * @throws Exception             on an error in ZK operations or callback. If a flush to ZooKeeper results    *                               in a {@link org.apache.zookeeper.KeeperException.BadVersionException} this instance becomes unusable and    *                               must be discarded    */
DECL|method|enqueueUpdate
specifier|public
name|ClusterState
name|enqueueUpdate
parameter_list|(
name|ClusterState
name|prevState
parameter_list|,
name|ZkWriteCommand
name|cmd
parameter_list|,
name|ZkWriteCallback
name|callback
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|Exception
block|{
if|if
condition|(
name|invalidState
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ZkStateWriter has seen a tragic error, this instance can no longer be used"
argument_list|)
throw|;
block|}
if|if
condition|(
name|cmd
operator|==
name|NO_OP
condition|)
return|return
name|prevState
return|;
if|if
condition|(
name|maybeFlushBefore
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
comment|// we must update the prev state to the new one
name|prevState
operator|=
name|clusterState
operator|=
name|writePendingUpdates
argument_list|()
expr_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|onWrite
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|onEnqueue
argument_list|()
expr_stmt|;
block|}
comment|/*     We need to know if the collection has moved from stateFormat=1 to stateFormat=2 (as a result of MIGRATECLUSTERSTATE)      */
name|DocCollection
name|previousCollection
init|=
name|prevState
operator|.
name|getCollectionOrNull
argument_list|(
name|cmd
operator|.
name|name
argument_list|)
decl_stmt|;
name|boolean
name|wasPreviouslyStateFormat1
init|=
name|previousCollection
operator|!=
literal|null
operator|&&
name|previousCollection
operator|.
name|getStateFormat
argument_list|()
operator|==
literal|1
decl_stmt|;
name|boolean
name|isCurrentlyStateFormat1
init|=
name|cmd
operator|.
name|collection
operator|!=
literal|null
operator|&&
name|cmd
operator|.
name|collection
operator|.
name|getStateFormat
argument_list|()
operator|==
literal|1
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|collection
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|wasPreviouslyStateFormat1
condition|)
block|{
name|isClusterStateModified
operator|=
literal|true
expr_stmt|;
block|}
name|clusterState
operator|=
name|prevState
operator|.
name|copyWith
argument_list|(
name|cmd
operator|.
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updates
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|isCurrentlyStateFormat1
condition|)
block|{
name|updates
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|name
argument_list|,
name|cmd
operator|.
name|collection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isCurrentlyStateFormat1
operator|||
name|wasPreviouslyStateFormat1
condition|)
block|{
name|isClusterStateModified
operator|=
literal|true
expr_stmt|;
block|}
name|clusterState
operator|=
name|prevState
operator|.
name|copyWith
argument_list|(
name|cmd
operator|.
name|name
argument_list|,
name|cmd
operator|.
name|collection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maybeFlushAfter
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|ClusterState
name|state
init|=
name|writePendingUpdates
argument_list|()
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|onWrite
argument_list|()
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
return|return
name|clusterState
return|;
block|}
comment|/**    * Logic to decide a flush before processing a ZkWriteCommand    *    * @param cmd the ZkWriteCommand instance    * @return true if a flush is required, false otherwise    */
DECL|method|maybeFlushBefore
specifier|protected
name|boolean
name|maybeFlushBefore
parameter_list|(
name|ZkWriteCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|lastUpdatedTime
operator|==
literal|0
condition|)
block|{
comment|// first update, make sure we go through
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cmd
operator|.
name|collection
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cmd
operator|.
name|collection
operator|.
name|getStateFormat
argument_list|()
operator|!=
name|lastStateFormat
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|cmd
operator|.
name|collection
operator|.
name|getStateFormat
argument_list|()
operator|>
literal|1
operator|&&
operator|!
name|cmd
operator|.
name|name
operator|.
name|equals
argument_list|(
name|lastCollectionName
argument_list|)
return|;
block|}
comment|/**    * Logic to decide a flush after processing a ZkWriteCommand    *    * @param cmd the ZkWriteCommand instance    * @return true if a flush to ZK is required, false otherwise    */
DECL|method|maybeFlushAfter
specifier|protected
name|boolean
name|maybeFlushAfter
parameter_list|(
name|ZkWriteCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|.
name|collection
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|lastCollectionName
operator|=
name|cmd
operator|.
name|name
expr_stmt|;
name|lastStateFormat
operator|=
name|cmd
operator|.
name|collection
operator|.
name|getStateFormat
argument_list|()
expr_stmt|;
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|lastUpdatedTime
operator|>
name|MAX_FLUSH_INTERVAL
return|;
block|}
DECL|method|hasPendingUpdates
specifier|public
name|boolean
name|hasPendingUpdates
parameter_list|()
block|{
return|return
operator|!
name|updates
operator|.
name|isEmpty
argument_list|()
operator|||
name|isClusterStateModified
return|;
block|}
comment|/**    * Writes all pending updates to ZooKeeper and returns the modified cluster state    *    * @return the modified cluster state    * @throws IllegalStateException if the current instance is no longer usable and must be discarded    * @throws KeeperException       if any ZooKeeper operation results in an error    * @throws InterruptedException  if the current thread is interrupted    */
DECL|method|writePendingUpdates
specifier|public
name|ClusterState
name|writePendingUpdates
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|invalidState
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ZkStateWriter has seen a tragic error, this instance can no longer be used"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|hasPendingUpdates
argument_list|()
condition|)
return|return
name|clusterState
return|;
name|TimerContext
name|timerContext
init|=
name|stats
operator|.
name|time
argument_list|(
literal|"update_state"
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|updates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|entry
range|:
name|updates
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
name|String
name|path
init|=
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|DocCollection
name|c
init|=
name|entry
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
comment|// let's clean up the collections path for this collection
name|log
operator|.
name|info
argument_list|(
literal|"going to delete_collection {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|reader
operator|.
name|getZkClient
argument_list|()
operator|.
name|clean
argument_list|(
literal|"/collections/"
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|getStateFormat
argument_list|()
operator|>
literal|1
condition|)
block|{
name|byte
index|[]
name|data
init|=
name|Utils
operator|.
name|toJSON
argument_list|(
name|singletonMap
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|c
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|getZkClient
argument_list|()
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"going to update_collection {} version: {}"
argument_list|,
name|path
argument_list|,
name|c
operator|.
name|getZNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Stat
name|stat
init|=
name|reader
operator|.
name|getZkClient
argument_list|()
operator|.
name|setData
argument_list|(
name|path
argument_list|,
name|data
argument_list|,
name|c
operator|.
name|getZNodeVersion
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DocCollection
name|newCollection
init|=
operator|new
name|DocCollection
argument_list|(
name|name
argument_list|,
name|c
operator|.
name|getSlicesMap
argument_list|()
argument_list|,
name|c
operator|.
name|getProperties
argument_list|()
argument_list|,
name|c
operator|.
name|getRouter
argument_list|()
argument_list|,
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|clusterState
operator|=
name|clusterState
operator|.
name|copyWith
argument_list|(
name|name
argument_list|,
name|newCollection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"going to create_collection {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|reader
operator|.
name|getZkClient
argument_list|()
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DocCollection
name|newCollection
init|=
operator|new
name|DocCollection
argument_list|(
name|name
argument_list|,
name|c
operator|.
name|getSlicesMap
argument_list|()
argument_list|,
name|c
operator|.
name|getProperties
argument_list|()
argument_list|,
name|c
operator|.
name|getRouter
argument_list|()
argument_list|,
literal|0
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|clusterState
operator|=
name|clusterState
operator|.
name|copyWith
argument_list|(
name|name
argument_list|,
name|newCollection
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|getStateFormat
argument_list|()
operator|==
literal|1
condition|)
block|{
name|isClusterStateModified
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|updates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isClusterStateModified
condition|)
block|{
assert|assert
name|clusterState
operator|.
name|getZkClusterStateVersion
argument_list|()
operator|>=
literal|0
assert|;
name|byte
index|[]
name|data
init|=
name|Utils
operator|.
name|toJSON
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
name|Stat
name|stat
init|=
name|reader
operator|.
name|getZkClient
argument_list|()
operator|.
name|setData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|,
name|data
argument_list|,
name|clusterState
operator|.
name|getZkClusterStateVersion
argument_list|()
argument_list|,
literal|true
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
name|clusterState
operator|.
name|getCollectionsMap
argument_list|()
decl_stmt|;
comment|// use the reader's live nodes because our cluster state's live nodes may be stale
name|clusterState
operator|=
operator|new
name|ClusterState
argument_list|(
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
argument_list|,
name|collections
argument_list|)
expr_stmt|;
name|isClusterStateModified
operator|=
literal|false
expr_stmt|;
block|}
name|lastUpdatedTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|bve
parameter_list|)
block|{
comment|// this is a tragic error, we must disallow usage of this instance
name|invalidState
operator|=
literal|true
expr_stmt|;
throw|throw
name|bve
throw|;
block|}
finally|finally
block|{
name|timerContext
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|stats
operator|.
name|success
argument_list|(
literal|"update_state"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|error
argument_list|(
literal|"update_state"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|clusterState
return|;
block|}
comment|/**    * @return time returned by System.nanoTime at which the main cluster state was last written to ZK or 0 if    * never    */
DECL|method|getLastUpdatedTime
specifier|public
name|long
name|getLastUpdatedTime
parameter_list|()
block|{
return|return
name|lastUpdatedTime
return|;
block|}
comment|/**    * @return the most up-to-date cluster state until the last enqueueUpdate operation    */
DECL|method|getClusterState
specifier|public
name|ClusterState
name|getClusterState
parameter_list|()
block|{
return|return
name|clusterState
return|;
block|}
DECL|interface|ZkWriteCallback
specifier|public
interface|interface
name|ZkWriteCallback
block|{
comment|/**      * Called by ZkStateWriter if a ZkWriteCommand is queued      */
DECL|method|onEnqueue
specifier|public
name|void
name|onEnqueue
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Called by ZkStateWriter if state is flushed to ZK      */
DECL|method|onWrite
specifier|public
name|void
name|onWrite
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

