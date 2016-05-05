begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
DECL|class|ClusterStateUtil
specifier|public
class|class
name|ClusterStateUtil
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
DECL|field|TIMEOUT_POLL_MS
specifier|private
specifier|static
specifier|final
name|int
name|TIMEOUT_POLL_MS
init|=
literal|1000
decl_stmt|;
comment|/**    * Wait to see *all* cores live and active.    *     * @param zkStateReader    *          to use for ClusterState    * @param timeoutInMs    *          how long to wait before giving up    * @return false if timed out    */
DECL|method|waitForAllActiveAndLiveReplicas
specifier|public
specifier|static
name|boolean
name|waitForAllActiveAndLiveReplicas
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|int
name|timeoutInMs
parameter_list|)
block|{
return|return
name|waitForAllActiveAndLiveReplicas
argument_list|(
name|zkStateReader
argument_list|,
literal|null
argument_list|,
name|timeoutInMs
argument_list|)
return|;
block|}
comment|/**    * Wait to see *all* cores live and active.    *     * @param zkStateReader    *          to use for ClusterState    * @param collection to look at    * @param timeoutInMs    *          how long to wait before giving up    * @return false if timed out    */
DECL|method|waitForAllActiveAndLiveReplicas
specifier|public
specifier|static
name|boolean
name|waitForAllActiveAndLiveReplicas
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collection
parameter_list|,
name|int
name|timeoutInMs
parameter_list|)
block|{
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
name|timeoutInMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collections
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collections
operator|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|collection
argument_list|,
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collections
operator|=
name|clusterState
operator|.
name|getCollectionsMap
argument_list|()
expr_stmt|;
block|}
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
name|collections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DocCollection
name|docCollection
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|docCollection
operator|.
name|getSlices
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
comment|// only look at active shards
if|if
condition|(
name|slice
operator|.
name|getState
argument_list|()
operator|==
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
condition|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
comment|// on a live node?
specifier|final
name|boolean
name|live
init|=
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isActive
init|=
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
decl_stmt|;
if|if
condition|(
operator|!
name|live
operator|||
operator|!
name|isActive
condition|)
block|{
comment|// fail
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|TIMEOUT_POLL_MS
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Interrupted"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|success
return|;
block|}
comment|/**    * Wait to see an entry in the ClusterState with a specific coreNodeName and    * baseUrl.    *     * @param zkStateReader    *          to use for ClusterState    * @param collection    *          to look in    * @param coreNodeName    *          to wait for    * @param baseUrl    *          to wait for    * @param timeoutInMs    *          how long to wait before giving up    * @return false if timed out    */
DECL|method|waitToSeeLiveReplica
specifier|public
specifier|static
name|boolean
name|waitToSeeLiveReplica
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|coreNodeName
parameter_list|,
name|String
name|baseUrl
parameter_list|,
name|int
name|timeoutInMs
parameter_list|)
block|{
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
name|timeoutInMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|log
operator|.
name|debug
argument_list|(
literal|"waiting to see replica just created live collection={} replica={} baseUrl={}"
argument_list|,
name|collection
argument_list|,
name|coreNodeName
argument_list|,
name|baseUrl
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|!=
literal|null
condition|)
block|{
name|DocCollection
name|docCollection
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|docCollection
operator|.
name|getSlices
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
comment|// only look at active shards
if|if
condition|(
name|slice
operator|.
name|getState
argument_list|()
operator|==
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
condition|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
comment|// on a live node?
name|boolean
name|live
init|=
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|rcoreNodeName
init|=
name|replica
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|rbaseUrl
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|live
operator|&&
name|coreNodeName
operator|.
name|equals
argument_list|(
name|rcoreNodeName
argument_list|)
operator|&&
name|baseUrl
operator|.
name|equals
argument_list|(
name|rbaseUrl
argument_list|)
condition|)
block|{
comment|// found it
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|TIMEOUT_POLL_MS
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Interrupted"
argument_list|)
throw|;
block|}
block|}
block|}
name|log
operator|.
name|error
argument_list|(
literal|"Timed out waiting to see replica just created in cluster state. Continuing..."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|waitForAllReplicasNotLive
specifier|public
specifier|static
name|boolean
name|waitForAllReplicasNotLive
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|int
name|timeoutInMs
parameter_list|)
block|{
return|return
name|waitForAllReplicasNotLive
argument_list|(
name|zkStateReader
argument_list|,
literal|null
argument_list|,
name|timeoutInMs
argument_list|)
return|;
block|}
DECL|method|waitForAllReplicasNotLive
specifier|public
specifier|static
name|boolean
name|waitForAllReplicasNotLive
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collection
parameter_list|,
name|int
name|timeoutInMs
parameter_list|)
block|{
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
name|timeoutInMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collections
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collections
operator|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|collection
argument_list|,
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collections
operator|=
name|clusterState
operator|.
name|getCollectionsMap
argument_list|()
expr_stmt|;
block|}
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
name|collections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DocCollection
name|docCollection
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|docCollection
operator|.
name|getSlices
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
comment|// only look at active shards
if|if
condition|(
name|slice
operator|.
name|getState
argument_list|()
operator|==
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
condition|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
comment|// on a live node?
name|boolean
name|live
init|=
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|live
condition|)
block|{
comment|// fail
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|TIMEOUT_POLL_MS
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Interrupted"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|success
return|;
block|}
DECL|method|getLiveAndActiveReplicaCount
specifier|public
specifier|static
name|int
name|getLiveAndActiveReplicaCount
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
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
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|int
name|liveAndActive
init|=
literal|0
decl_stmt|;
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
name|boolean
name|live
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|active
init|=
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
decl_stmt|;
if|if
condition|(
name|live
operator|&&
name|active
condition|)
block|{
name|liveAndActive
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|liveAndActive
return|;
block|}
DECL|method|waitForLiveAndActiveReplicaCount
specifier|public
specifier|static
name|boolean
name|waitForLiveAndActiveReplicaCount
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collection
parameter_list|,
name|int
name|replicaCount
parameter_list|,
name|int
name|timeoutInMs
parameter_list|)
block|{
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
name|timeoutInMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|success
operator|=
name|getLiveAndActiveReplicaCount
argument_list|(
name|zkStateReader
argument_list|,
name|collection
argument_list|)
operator|==
name|replicaCount
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|TIMEOUT_POLL_MS
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Interrupted"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|success
return|;
block|}
DECL|method|isAutoAddReplicas
specifier|public
specifier|static
name|boolean
name|isAutoAddReplicas
parameter_list|(
name|ZkStateReader
name|reader
parameter_list|,
name|String
name|collection
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|reader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|!=
literal|null
condition|)
block|{
name|DocCollection
name|docCollection
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
name|docCollection
operator|!=
literal|null
condition|)
block|{
return|return
name|docCollection
operator|.
name|getAutoAddReplicas
argument_list|()
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

