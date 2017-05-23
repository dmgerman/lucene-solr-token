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
name|EnumSet
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
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

begin_comment
comment|/**  * A Slice contains immutable information about a logical shard (all replicas that share the same shard id).  */
end_comment

begin_class
DECL|class|Slice
specifier|public
class|class
name|Slice
extends|extends
name|ZkNodeProps
implements|implements
name|Iterable
argument_list|<
name|Replica
argument_list|>
block|{
comment|/** Loads multiple slices into a Map from a generic Map that probably came from deserialized JSON. */
DECL|method|loadAllFromMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|loadAllFromMap
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
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Replica
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|replicas
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** The slice's state. */
DECL|enum|State
specifier|public
enum|enum
name|State
block|{
comment|/** The normal/default state of a shard. */
DECL|enum constant|ACTIVE
name|ACTIVE
block|,
comment|/**      * A shard is put in that state after it has been successfully split. See      *<a href="https://cwiki.apache.org/confluence/display/solr/Collections+API#CollectionsAPI-api3">      * the reference guide</a> for more details.      */
DECL|enum constant|INACTIVE
name|INACTIVE
block|,
comment|/**      * When a shard is split, the new sub-shards are put in that state while the      * split operation is in progress. It's also used when the shard is undergoing data restoration.      * A shard in this state still receives      * update requests from the parent shard leader, however does not participate      * in distributed search.      */
DECL|enum constant|CONSTRUCTION
name|CONSTRUCTION
block|,
comment|/**      * Sub-shards of a split shard are put in that state, when they need to      * create replicas in order to meet the collection's replication factor. A      * shard in that state still receives update requests from the parent shard      * leader, however does not participate in distributed search.      */
DECL|enum constant|RECOVERY
name|RECOVERY
block|,
comment|/**      * Sub-shards of a split shard are put in that state when the split is deemed failed      * by the overseer even though all replicas are active because either the leader node is      * no longer live or has a different ephemeral owner (zk session id). Such conditions can potentially      * lead to data loss. See SOLR-9438 for details. A shard in that state will neither receive      * update requests from the parent shard leader, nor participate in distributed search.      */
DECL|enum constant|RECOVERY_FAILED
name|RECOVERY_FAILED
block|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
comment|/** Converts the state string to a State instance. */
DECL|method|getState
specifier|public
specifier|static
name|State
name|getState
parameter_list|(
name|String
name|stateStr
parameter_list|)
block|{
return|return
name|State
operator|.
name|valueOf
argument_list|(
name|stateStr
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|REPLICAS
specifier|public
specifier|static
specifier|final
name|String
name|REPLICAS
init|=
literal|"replicas"
decl_stmt|;
DECL|field|RANGE
specifier|public
specifier|static
specifier|final
name|String
name|RANGE
init|=
literal|"range"
decl_stmt|;
DECL|field|LEADER
specifier|public
specifier|static
specifier|final
name|String
name|LEADER
init|=
literal|"leader"
decl_stmt|;
comment|// FUTURE: do we want to record the leader as a slice property in the JSON (as opposed to isLeader as a replica property?)
DECL|field|PARENT
specifier|public
specifier|static
specifier|final
name|String
name|PARENT
init|=
literal|"parent"
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|range
specifier|private
specifier|final
name|DocRouter
operator|.
name|Range
name|range
decl_stmt|;
DECL|field|replicationFactor
specifier|private
specifier|final
name|Integer
name|replicationFactor
decl_stmt|;
comment|// FUTURE: optional per-slice override of the collection replicationFactor
DECL|field|replicas
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|replicas
decl_stmt|;
DECL|field|leader
specifier|private
specifier|final
name|Replica
name|leader
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|State
name|state
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|String
name|parent
decl_stmt|;
DECL|field|routingRules
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingRule
argument_list|>
name|routingRules
decl_stmt|;
comment|/**    * @param name  The name of the slice    * @param replicas The replicas of the slice.  This is used directly and a copy is not made.  If null, replicas will be constructed from props.    * @param props  The properties of the slice - a shallow copy will always be made.    */
DECL|method|Slice
specifier|public
name|Slice
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|replicas
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|)
block|{
name|super
argument_list|(
name|props
operator|==
literal|null
condition|?
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|2
argument_list|)
else|:
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|props
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|Object
name|rangeObj
init|=
name|propMap
operator|.
name|get
argument_list|(
name|RANGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|propMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|state
operator|=
name|State
operator|.
name|getState
argument_list|(
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|state
operator|=
name|State
operator|.
name|ACTIVE
expr_stmt|;
comment|//Default to ACTIVE
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|state
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocRouter
operator|.
name|Range
name|tmpRange
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rangeObj
operator|instanceof
name|DocRouter
operator|.
name|Range
condition|)
block|{
name|tmpRange
operator|=
operator|(
name|DocRouter
operator|.
name|Range
operator|)
name|rangeObj
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rangeObj
operator|!=
literal|null
condition|)
block|{
comment|// Doesn't support custom implementations of Range, but currently not needed.
name|tmpRange
operator|=
name|DocRouter
operator|.
name|DEFAULT
operator|.
name|fromString
argument_list|(
name|rangeObj
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|range
operator|=
name|tmpRange
expr_stmt|;
comment|/** debugging.  this isn't an error condition for custom sharding.     if (range == null) {       System.out.println("###### NO RANGE for " + name + " props=" + props);     }     **/
if|if
condition|(
name|propMap
operator|.
name|containsKey
argument_list|(
name|PARENT
argument_list|)
operator|&&
name|propMap
operator|.
name|get
argument_list|(
name|PARENT
argument_list|)
operator|!=
literal|null
condition|)
name|this
operator|.
name|parent
operator|=
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|PARENT
argument_list|)
expr_stmt|;
else|else
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|replicationFactor
operator|=
literal|null
expr_stmt|;
comment|// future
comment|// add the replicas *after* the other properties (for aesthetics, so it's easy to find slice properties in the JSON output)
name|this
operator|.
name|replicas
operator|=
name|replicas
operator|!=
literal|null
condition|?
name|replicas
else|:
name|makeReplicas
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|REPLICAS
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|REPLICAS
argument_list|,
name|this
operator|.
name|replicas
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rules
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propMap
operator|.
name|get
argument_list|(
literal|"routingRules"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rules
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|routingRules
operator|=
operator|new
name|HashMap
argument_list|<>
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
name|Object
argument_list|>
name|entry
range|:
name|rules
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|o
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|RoutingRule
name|rule
init|=
operator|new
name|RoutingRule
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|routingRules
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|rule
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|routingRules
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|RoutingRule
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|this
operator|.
name|routingRules
operator|=
literal|null
expr_stmt|;
block|}
name|leader
operator|=
name|findLeader
argument_list|()
expr_stmt|;
block|}
DECL|method|makeReplicas
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|makeReplicas
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|genericReplicas
parameter_list|)
block|{
if|if
condition|(
name|genericReplicas
operator|==
literal|null
condition|)
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|genericReplicas
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
name|genericReplicas
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
name|Replica
name|r
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Replica
condition|)
block|{
name|r
operator|=
operator|(
name|Replica
operator|)
name|val
expr_stmt|;
block|}
else|else
block|{
name|r
operator|=
operator|new
name|Replica
argument_list|(
name|name
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
expr_stmt|;
block|}
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|findLeader
specifier|private
name|Replica
name|findLeader
parameter_list|()
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|getStr
argument_list|(
name|LEADER
argument_list|)
operator|!=
literal|null
condition|)
block|{
assert|assert
name|replica
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|TLOG
operator|||
name|replica
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NRT
operator|:
literal|"Pull replica should not become leader!"
assert|;
return|return
name|replica
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Return slice name (shard id).    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Gets the list of all replicas for this slice.    */
DECL|method|getReplicas
specifier|public
name|Collection
argument_list|<
name|Replica
argument_list|>
name|getReplicas
parameter_list|()
block|{
return|return
name|replicas
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Gets all replicas that match a predicate    */
DECL|method|getReplicas
specifier|public
name|List
argument_list|<
name|Replica
argument_list|>
name|getReplicas
parameter_list|(
name|Predicate
argument_list|<
name|Replica
argument_list|>
name|pred
parameter_list|)
block|{
return|return
name|replicas
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|pred
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Gets the list of replicas that have a type present in s    */
DECL|method|getReplicas
specifier|public
name|List
argument_list|<
name|Replica
argument_list|>
name|getReplicas
parameter_list|(
name|EnumSet
argument_list|<
name|Replica
operator|.
name|Type
argument_list|>
name|s
parameter_list|)
block|{
return|return
name|this
operator|.
name|getReplicas
argument_list|(
name|r
lambda|->
name|s
operator|.
name|contains
argument_list|(
name|r
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the map of coreNodeName to replicas for this slice.    */
DECL|method|getReplicasMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|getReplicasMap
parameter_list|()
block|{
return|return
name|replicas
return|;
block|}
DECL|method|getReplicasCopy
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|getReplicasCopy
parameter_list|()
block|{
return|return
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|replicas
argument_list|)
return|;
block|}
DECL|method|getLeader
specifier|public
name|Replica
name|getLeader
parameter_list|()
block|{
return|return
name|leader
return|;
block|}
DECL|method|getReplica
specifier|public
name|Replica
name|getReplica
parameter_list|(
name|String
name|replicaName
parameter_list|)
block|{
return|return
name|replicas
operator|.
name|get
argument_list|(
name|replicaName
argument_list|)
return|;
block|}
DECL|method|getRange
specifier|public
name|DocRouter
operator|.
name|Range
name|getRange
parameter_list|()
block|{
return|return
name|range
return|;
block|}
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getParent
specifier|public
name|String
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|getRoutingRules
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingRule
argument_list|>
name|getRoutingRules
parameter_list|()
block|{
return|return
name|routingRules
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
return|return
name|name
operator|+
literal|':'
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|propMap
argument_list|)
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
name|propMap
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

