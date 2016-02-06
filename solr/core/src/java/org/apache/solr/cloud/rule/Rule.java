begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Objects
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
name|util
operator|.
name|StrUtils
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
operator|.
name|ImplicitSnitch
operator|.
name|CORES
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
name|rule
operator|.
name|Rule
operator|.
name|MatchStatus
operator|.
name|CANNOT_ASSIGN_FAIL
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
name|rule
operator|.
name|Rule
operator|.
name|MatchStatus
operator|.
name|NODE_CAN_BE_ASSIGNED
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
name|rule
operator|.
name|Rule
operator|.
name|MatchStatus
operator|.
name|NOT_APPLICABLE
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
name|rule
operator|.
name|Rule
operator|.
name|Operand
operator|.
name|EQUAL
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
name|rule
operator|.
name|Rule
operator|.
name|Operand
operator|.
name|GREATER_THAN
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
name|rule
operator|.
name|Rule
operator|.
name|Operand
operator|.
name|LESS_THAN
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
name|rule
operator|.
name|Rule
operator|.
name|Operand
operator|.
name|NOT_EQUAL
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
name|REPLICA_PROP
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

begin_class
DECL|class|Rule
specifier|public
class|class
name|Rule
block|{
DECL|field|WILD_CARD
specifier|public
specifier|static
specifier|final
name|String
name|WILD_CARD
init|=
literal|"*"
decl_stmt|;
DECL|field|WILD_WILD_CARD
specifier|public
specifier|static
specifier|final
name|String
name|WILD_WILD_CARD
init|=
literal|"**"
decl_stmt|;
DECL|field|SHARD_DEFAULT
specifier|static
specifier|final
name|Condition
name|SHARD_DEFAULT
init|=
operator|new
name|Rule
operator|.
name|Condition
argument_list|(
name|SHARD_ID_PROP
argument_list|,
name|WILD_WILD_CARD
argument_list|)
decl_stmt|;
DECL|field|REPLICA_DEFAULT
specifier|static
specifier|final
name|Condition
name|REPLICA_DEFAULT
init|=
operator|new
name|Rule
operator|.
name|Condition
argument_list|(
name|REPLICA_PROP
argument_list|,
name|WILD_CARD
argument_list|)
decl_stmt|;
DECL|field|shard
name|Condition
name|shard
decl_stmt|;
DECL|field|replica
name|Condition
name|replica
decl_stmt|;
DECL|field|tag
name|Condition
name|tag
decl_stmt|;
DECL|method|Rule
specifier|public
name|Rule
parameter_list|(
name|Map
name|m
parameter_list|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|Condition
name|condition
init|=
operator|new
name|Condition
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|condition
operator|.
name|name
operator|.
name|equals
argument_list|(
name|SHARD_ID_PROP
argument_list|)
condition|)
name|shard
operator|=
name|condition
expr_stmt|;
elseif|else
if|if
condition|(
name|condition
operator|.
name|name
operator|.
name|equals
argument_list|(
name|REPLICA_PROP
argument_list|)
condition|)
name|replica
operator|=
name|condition
expr_stmt|;
else|else
block|{
if|if
condition|(
name|tag
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"There can be only one and only one tag other than 'shard' and 'replica' in rule "
operator|+
name|m
argument_list|)
throw|;
block|}
name|tag
operator|=
name|condition
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
name|shard
operator|=
name|SHARD_DEFAULT
expr_stmt|;
if|if
condition|(
name|replica
operator|==
literal|null
condition|)
name|replica
operator|=
name|REPLICA_DEFAULT
expr_stmt|;
if|if
condition|(
name|tag
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"There should be a tag other than 'shard' and 'replica'"
argument_list|)
throw|;
if|if
condition|(
name|replica
operator|.
name|isWildCard
argument_list|()
operator|&&
name|tag
operator|.
name|isWildCard
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Both replica and tag cannot be wild cards"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseObj
specifier|static
name|Object
name|parseObj
parameter_list|(
name|Object
name|o
parameter_list|,
name|Class
name|typ
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
name|o
return|;
if|if
condition|(
name|typ
operator|==
name|String
operator|.
name|class
condition|)
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
return|;
if|if
condition|(
name|typ
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
return|;
block|}
return|return
name|o
return|;
block|}
DECL|method|parseRule
specifier|public
specifier|static
name|Map
name|parseRule
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|s
operator|=
name|s
operator|.
name|trim
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keyVals
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|s
argument_list|,
literal|','
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|kv
range|:
name|keyVals
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|keyVal
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|kv
argument_list|,
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyVal
operator|.
name|size
argument_list|()
operator|!=
literal|2
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
literal|"Invalid rule. should have only key and val in : "
operator|+
name|kv
argument_list|)
throw|;
block|}
if|if
condition|(
name|keyVal
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|keyVal
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
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
literal|"Invalid rule. should have key and val in : "
operator|+
name|kv
argument_list|)
throw|;
block|}
name|result
operator|.
name|put
argument_list|(
name|keyVal
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|,
name|keyVal
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
name|Map
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
name|SHARD_DEFAULT
condition|)
name|map
operator|.
name|put
argument_list|(
name|shard
operator|.
name|name
argument_list|,
name|shard
operator|.
name|operand
operator|.
name|toStr
argument_list|(
name|shard
operator|.
name|val
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|replica
operator|!=
name|REPLICA_DEFAULT
condition|)
name|map
operator|.
name|put
argument_list|(
name|replica
operator|.
name|name
argument_list|,
name|replica
operator|.
name|operand
operator|.
name|toStr
argument_list|(
name|replica
operator|.
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|tag
operator|.
name|name
argument_list|,
name|tag
operator|.
name|operand
operator|.
name|toStr
argument_list|(
name|tag
operator|.
name|val
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Utils
operator|.
name|toJSONString
argument_list|(
name|map
argument_list|)
return|;
block|}
comment|/**    * Check if it is possible to assign this node as a replica of the given shard    * without violating this rule    *    * @param testNode       The node in question    * @param shardVsNodeSet Set of nodes for every shard     * @param nodeVsTags     The pre-fetched tags for all the nodes    * @param shardName      The shard to which this node should be attempted    * @return MatchStatus    */
DECL|method|tryAssignNodeToShard
name|MatchStatus
name|tryAssignNodeToShard
parameter_list|(
name|String
name|testNode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|shardVsNodeSet
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|nodeVsTags
parameter_list|,
name|String
name|shardName
parameter_list|,
name|Phase
name|phase
parameter_list|)
block|{
if|if
condition|(
name|tag
operator|.
name|isWildCard
argument_list|()
condition|)
block|{
comment|//this is ensuring uniqueness across a certain tag
comment|//eg: rack:r168
if|if
condition|(
operator|!
name|shard
operator|.
name|isWildCard
argument_list|()
operator|&&
name|shardName
operator|.
name|equals
argument_list|(
name|shard
operator|.
name|val
argument_list|)
condition|)
return|return
name|NOT_APPLICABLE
return|;
name|Object
name|tagValueForThisNode
init|=
name|nodeVsTags
operator|.
name|get
argument_list|(
name|testNode
argument_list|)
operator|.
name|get
argument_list|(
name|tag
operator|.
name|name
argument_list|)
decl_stmt|;
name|int
name|v
init|=
name|getNumberOfNodesWithSameTagVal
argument_list|(
name|shard
argument_list|,
name|nodeVsTags
argument_list|,
name|shardVsNodeSet
argument_list|,
name|shardName
argument_list|,
operator|new
name|Condition
argument_list|(
name|tag
operator|.
name|name
argument_list|,
name|tagValueForThisNode
argument_list|,
name|EQUAL
argument_list|)
argument_list|,
name|phase
argument_list|)
decl_stmt|;
if|if
condition|(
name|phase
operator|==
name|Phase
operator|.
name|ASSIGN
operator|||
name|phase
operator|==
name|Phase
operator|.
name|FUZZY_ASSIGN
condition|)
name|v
operator|++
expr_stmt|;
comment|//v++ because including this node , it becomes v+1 during ASSIGN
return|return
name|replica
operator|.
name|canMatch
argument_list|(
name|v
argument_list|,
name|phase
argument_list|)
condition|?
name|NODE_CAN_BE_ASSIGNED
else|:
name|CANNOT_ASSIGN_FAIL
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|shard
operator|.
name|isWildCard
argument_list|()
operator|&&
operator|!
name|shardName
operator|.
name|equals
argument_list|(
name|shard
operator|.
name|val
argument_list|)
condition|)
return|return
name|NOT_APPLICABLE
return|;
if|if
condition|(
name|replica
operator|.
name|isWildCard
argument_list|()
condition|)
block|{
comment|//this means for each replica, the value must match
comment|//shard match is already tested
if|if
condition|(
name|tag
operator|.
name|canMatch
argument_list|(
name|nodeVsTags
operator|.
name|get
argument_list|(
name|testNode
argument_list|)
operator|.
name|get
argument_list|(
name|tag
operator|.
name|name
argument_list|)
argument_list|,
name|phase
argument_list|)
condition|)
return|return
name|NODE_CAN_BE_ASSIGNED
return|;
else|else
return|return
name|CANNOT_ASSIGN_FAIL
return|;
block|}
else|else
block|{
name|int
name|v
init|=
name|getNumberOfNodesWithSameTagVal
argument_list|(
name|shard
argument_list|,
name|nodeVsTags
argument_list|,
name|shardVsNodeSet
argument_list|,
name|shardName
argument_list|,
name|tag
argument_list|,
name|phase
argument_list|)
decl_stmt|;
return|return
name|replica
operator|.
name|canMatch
argument_list|(
name|v
argument_list|,
name|phase
argument_list|)
condition|?
name|NODE_CAN_BE_ASSIGNED
else|:
name|CANNOT_ASSIGN_FAIL
return|;
block|}
block|}
block|}
DECL|method|getNumberOfNodesWithSameTagVal
specifier|private
name|int
name|getNumberOfNodesWithSameTagVal
parameter_list|(
name|Condition
name|shardCondition
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|nodeVsTags
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|shardVsNodeSet
parameter_list|,
name|String
name|shardName
parameter_list|,
name|Condition
name|tagCondition
parameter_list|,
name|Phase
name|phase
parameter_list|)
block|{
name|int
name|countMatchingThisTagValue
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|entry
range|:
name|shardVsNodeSet
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//check if this shard is relevant. either it is a ANY Wild card (**)
comment|// or this shard is same as the shard in question
if|if
condition|(
name|shardCondition
operator|.
name|val
operator|.
name|equals
argument_list|(
name|WILD_WILD_CARD
argument_list|)
operator|||
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|shardName
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodesInThisShard
init|=
name|shardVsNodeSet
operator|.
name|get
argument_list|(
name|shardCondition
operator|.
name|val
operator|.
name|equals
argument_list|(
name|WILD_WILD_CARD
argument_list|)
condition|?
name|entry
operator|.
name|getKey
argument_list|()
else|:
name|shardName
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodesInThisShard
operator|!=
literal|null
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
name|Integer
argument_list|>
name|aNode
range|:
name|nodesInThisShard
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|obj
init|=
name|nodeVsTags
operator|.
name|get
argument_list|(
name|aNode
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
name|tag
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|tagCondition
operator|.
name|canMatch
argument_list|(
name|obj
argument_list|,
name|phase
argument_list|)
condition|)
name|countMatchingThisTagValue
operator|+=
name|aNode
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|countMatchingThisTagValue
return|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|n1
parameter_list|,
name|String
name|n2
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|nodeVsTags
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|currentState
parameter_list|)
block|{
return|return
name|tag
operator|.
name|compare
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|,
name|nodeVsTags
argument_list|)
return|;
block|}
DECL|method|isFuzzy
specifier|public
name|boolean
name|isFuzzy
parameter_list|()
block|{
return|return
name|shard
operator|.
name|fuzzy
operator|||
name|replica
operator|.
name|fuzzy
operator|||
name|tag
operator|.
name|fuzzy
return|;
block|}
DECL|enum|Operand
specifier|public
enum|enum
name|Operand
block|{
DECL|enum constant|EQUAL
name|EQUAL
argument_list|(
literal|""
argument_list|)
block|,
DECL|enum constant|NOT_EQUAL
name|NOT_EQUAL
argument_list|(
literal|"!"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|canMatch
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
operator|!
name|super
operator|.
name|canMatch
argument_list|(
name|ruleVal
argument_list|,
name|testVal
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|GREATER_THAN
name|GREATER_THAN
argument_list|(
literal|">"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|match
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|checkNumeric
argument_list|(
name|super
operator|.
name|match
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canMatch
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
name|compareNum
argument_list|(
name|ruleVal
argument_list|,
name|testVal
argument_list|)
operator|==
literal|1
return|;
block|}
block|}
block|,
DECL|enum constant|LESS_THAN
name|LESS_THAN
argument_list|(
literal|"<"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|n1Val
parameter_list|,
name|Object
name|n2Val
parameter_list|)
block|{
return|return
name|GREATER_THAN
operator|.
name|compare
argument_list|(
name|n1Val
argument_list|,
name|n2Val
argument_list|)
operator|*
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canMatch
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
name|compareNum
argument_list|(
name|ruleVal
argument_list|,
name|testVal
argument_list|)
operator|==
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|match
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|checkNumeric
argument_list|(
name|super
operator|.
name|match
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
block|;
DECL|field|operand
specifier|public
specifier|final
name|String
name|operand
decl_stmt|;
DECL|method|Operand
name|Operand
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|this
operator|.
name|operand
operator|=
name|val
expr_stmt|;
block|}
DECL|method|toStr
specifier|public
name|String
name|toStr
parameter_list|(
name|Object
name|expectedVal
parameter_list|)
block|{
return|return
name|operand
operator|+
name|expectedVal
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|checkNumeric
name|Object
name|checkNumeric
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"for operand "
operator|+
name|operand
operator|+
literal|" the value must be numeric"
argument_list|)
throw|;
block|}
block|}
DECL|method|match
specifier|public
name|Object
name|match
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|operand
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|val
return|;
return|return
name|val
operator|.
name|startsWith
argument_list|(
name|operand
argument_list|)
condition|?
name|val
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|canMatch
specifier|public
name|boolean
name|canMatch
parameter_list|(
name|Object
name|ruleVal
parameter_list|,
name|Object
name|testVal
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ruleVal
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|testVal
argument_list|)
argument_list|)
return|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|n1Val
parameter_list|,
name|Object
name|n2Val
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|compareNum
specifier|public
name|int
name|compareNum
parameter_list|(
name|Object
name|n1Val
parameter_list|,
name|Object
name|n2Val
parameter_list|)
block|{
name|Integer
name|n1
init|=
operator|(
name|Integer
operator|)
name|parseObj
argument_list|(
name|n1Val
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Integer
name|n2
init|=
operator|(
name|Integer
operator|)
name|parseObj
argument_list|(
name|n2Val
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|n1
operator|>
name|n2
condition|?
operator|-
literal|1
else|:
name|Objects
operator|.
name|equals
argument_list|(
name|n1
argument_list|,
name|n2
argument_list|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
DECL|enum|MatchStatus
enum|enum
name|MatchStatus
block|{
DECL|enum constant|NODE_CAN_BE_ASSIGNED
name|NODE_CAN_BE_ASSIGNED
block|,
DECL|enum constant|CANNOT_ASSIGN_GO_AHEAD
name|CANNOT_ASSIGN_GO_AHEAD
block|,
DECL|enum constant|NOT_APPLICABLE
name|NOT_APPLICABLE
block|,
DECL|enum constant|CANNOT_ASSIGN_FAIL
name|CANNOT_ASSIGN_FAIL
block|}
DECL|enum|Phase
enum|enum
name|Phase
block|{
DECL|enum constant|ASSIGN
DECL|enum constant|VERIFY
DECL|enum constant|FUZZY_ASSIGN
DECL|enum constant|FUZZY_VERIFY
name|ASSIGN
block|,
name|VERIFY
block|,
name|FUZZY_ASSIGN
block|,
name|FUZZY_VERIFY
block|}
DECL|class|Condition
specifier|public
specifier|static
class|class
name|Condition
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|val
specifier|final
name|Object
name|val
decl_stmt|;
DECL|field|operand
specifier|public
specifier|final
name|Operand
name|operand
decl_stmt|;
DECL|field|fuzzy
specifier|final
name|boolean
name|fuzzy
decl_stmt|;
DECL|method|Condition
name|Condition
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|,
name|Operand
name|op
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|operand
operator|=
name|op
expr_stmt|;
name|fuzzy
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|Condition
name|Condition
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
name|Object
name|expectedVal
decl_stmt|;
name|boolean
name|fuzzy
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"value of  a tag cannot be null for key "
operator|+
name|key
argument_list|)
throw|;
try|try
block|{
name|this
operator|.
name|name
operator|=
name|key
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
name|value
init|=
name|val
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|endsWith
argument_list|(
literal|"~"
argument_list|)
condition|)
block|{
name|fuzzy
operator|=
literal|true
expr_stmt|;
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|expectedVal
operator|=
name|NOT_EQUAL
operator|.
name|match
argument_list|(
name|value
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|operand
operator|=
name|NOT_EQUAL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|expectedVal
operator|=
name|GREATER_THAN
operator|.
name|match
argument_list|(
name|value
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|operand
operator|=
name|GREATER_THAN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|expectedVal
operator|=
name|LESS_THAN
operator|.
name|match
argument_list|(
name|value
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|operand
operator|=
name|LESS_THAN
expr_stmt|;
block|}
else|else
block|{
name|operand
operator|=
name|EQUAL
expr_stmt|;
name|expectedVal
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|REPLICA_PROP
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|WILD_CARD
operator|.
name|equals
argument_list|(
name|expectedVal
argument_list|)
condition|)
block|{
try|try
block|{
name|expectedVal
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|expectedVal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The replica tag value can only be '*' or an integer"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid condition : "
operator|+
name|key
operator|+
literal|":"
operator|+
name|val
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|val
operator|=
name|expectedVal
expr_stmt|;
name|this
operator|.
name|fuzzy
operator|=
name|fuzzy
expr_stmt|;
block|}
DECL|method|isWildCard
specifier|public
name|boolean
name|isWildCard
parameter_list|()
block|{
return|return
name|val
operator|.
name|equals
argument_list|(
name|WILD_CARD
argument_list|)
operator|||
name|val
operator|.
name|equals
argument_list|(
name|WILD_WILD_CARD
argument_list|)
return|;
block|}
DECL|method|canMatch
name|boolean
name|canMatch
parameter_list|(
name|Object
name|testVal
parameter_list|,
name|Phase
name|phase
parameter_list|)
block|{
if|if
condition|(
name|phase
operator|==
name|Phase
operator|.
name|FUZZY_ASSIGN
operator|||
name|phase
operator|==
name|Phase
operator|.
name|FUZZY_VERIFY
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|phase
operator|==
name|Phase
operator|.
name|ASSIGN
condition|)
block|{
if|if
condition|(
operator|(
name|name
operator|.
name|equals
argument_list|(
name|REPLICA_PROP
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
name|CORES
argument_list|)
operator|)
operator|&&
operator|(
name|operand
operator|==
name|GREATER_THAN
operator|||
name|operand
operator|==
name|NOT_EQUAL
operator|)
condition|)
block|{
comment|//the no:of replicas or cores will increase towards the end
comment|//so this should only be checked in the Phase.
comment|//process
return|return
literal|true
return|;
block|}
block|}
return|return
name|operand
operator|.
name|canMatch
argument_list|(
name|val
argument_list|,
name|testVal
argument_list|)
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
name|obj
operator|instanceof
name|Condition
condition|)
block|{
name|Condition
name|that
init|=
operator|(
name|Condition
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
name|that
operator|.
name|name
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|operand
argument_list|,
name|that
operator|.
name|operand
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|val
argument_list|,
name|that
operator|.
name|val
argument_list|)
return|;
block|}
return|return
literal|false
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
literal|":"
operator|+
name|operand
operator|.
name|toStr
argument_list|(
name|val
argument_list|)
operator|+
operator|(
name|fuzzy
condition|?
literal|"~"
else|:
literal|""
operator|)
return|;
block|}
DECL|method|getInt
specifier|public
name|Integer
name|getInt
parameter_list|()
block|{
return|return
operator|(
name|Integer
operator|)
name|val
return|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|n1
parameter_list|,
name|String
name|n2
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|nodeVsTags
parameter_list|)
block|{
return|return
name|isWildCard
argument_list|()
condition|?
literal|0
else|:
name|operand
operator|.
name|compare
argument_list|(
name|nodeVsTags
operator|.
name|get
argument_list|(
name|n1
argument_list|)
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|,
name|nodeVsTags
operator|.
name|get
argument_list|(
name|n2
argument_list|)
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

