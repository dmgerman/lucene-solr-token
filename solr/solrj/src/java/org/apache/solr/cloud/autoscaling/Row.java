begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.autoscaling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|autoscaling
package|;
end_package

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
name|Random
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
name|IteratorWriter
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
name|MapWriter
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
name|Pair
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
name|cloud
operator|.
name|autoscaling
operator|.
name|Policy
operator|.
name|ReplicaInfo
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
name|CoreAdminParams
operator|.
name|NODE
import|;
end_import

begin_class
DECL|class|Row
class|class
name|Row
implements|implements
name|MapWriter
block|{
DECL|field|node
specifier|public
specifier|final
name|String
name|node
decl_stmt|;
DECL|field|cells
specifier|final
name|Cell
index|[]
name|cells
decl_stmt|;
DECL|field|collectionVsShardVsReplicas
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
argument_list|>
name|collectionVsShardVsReplicas
decl_stmt|;
DECL|field|violations
name|List
argument_list|<
name|Clause
argument_list|>
name|violations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|anyValueMissing
name|boolean
name|anyValueMissing
init|=
literal|false
decl_stmt|;
DECL|method|Row
name|Row
parameter_list|(
name|String
name|node
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|params
parameter_list|,
name|ClusterDataProvider
name|dataProvider
parameter_list|)
block|{
name|collectionVsShardVsReplicas
operator|=
name|dataProvider
operator|.
name|getReplicaInfo
argument_list|(
name|node
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionVsShardVsReplicas
operator|==
literal|null
condition|)
name|collectionVsShardVsReplicas
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|cells
operator|=
operator|new
name|Cell
index|[
name|params
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vals
init|=
name|dataProvider
operator|.
name|getNodeValues
argument_list|(
name|node
argument_list|,
name|params
argument_list|)
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
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|params
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|cells
index|[
name|i
index|]
operator|=
operator|new
name|Cell
argument_list|(
name|i
argument_list|,
name|s
argument_list|,
name|vals
operator|.
name|get
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|NODE
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
name|cells
index|[
name|i
index|]
operator|.
name|val
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|cells
index|[
name|i
index|]
operator|.
name|val
operator|==
literal|null
condition|)
name|anyValueMissing
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|Row
name|Row
parameter_list|(
name|String
name|node
parameter_list|,
name|Cell
index|[]
name|cells
parameter_list|,
name|boolean
name|anyValueMissing
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
argument_list|>
name|collectionVsShardVsReplicas
parameter_list|,
name|List
argument_list|<
name|Clause
argument_list|>
name|violations
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|cells
operator|=
operator|new
name|Cell
index|[
name|cells
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|cells
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|cells
index|[
name|i
index|]
operator|=
name|cells
index|[
name|i
index|]
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|anyValueMissing
operator|=
name|anyValueMissing
expr_stmt|;
name|this
operator|.
name|collectionVsShardVsReplicas
operator|=
name|collectionVsShardVsReplicas
expr_stmt|;
name|this
operator|.
name|violations
operator|=
name|violations
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMap
specifier|public
name|void
name|writeMap
parameter_list|(
name|EntryWriter
name|ew
parameter_list|)
throws|throws
name|IOException
block|{
name|ew
operator|.
name|put
argument_list|(
name|node
argument_list|,
operator|(
name|IteratorWriter
operator|)
name|iw
lambda|->
block|{
name|iw
operator|.
name|add
argument_list|(
operator|(
name|MapWriter
operator|)
name|e
lambda|->
name|e
operator|.
name|put
argument_list|(
literal|"replicas"
argument_list|,
name|collectionVsShardVsReplicas
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Cell
name|cell
range|:
name|cells
control|)
name|iw
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|copy
name|Row
name|copy
parameter_list|()
block|{
return|return
operator|new
name|Row
argument_list|(
name|node
argument_list|,
name|cells
argument_list|,
name|anyValueMissing
argument_list|,
name|Utils
operator|.
name|getDeepCopy
argument_list|(
name|collectionVsShardVsReplicas
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|violations
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getVal
name|Object
name|getVal
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Cell
name|cell
range|:
name|cells
control|)
if|if
condition|(
name|cell
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|cell
operator|.
name|val
return|;
return|return
literal|null
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
name|node
return|;
block|}
comment|// this adds a replica to the replica info
DECL|method|addReplica
name|Row
name|addReplica
parameter_list|(
name|String
name|coll
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|Row
name|row
init|=
name|copy
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
name|c
init|=
name|row
operator|.
name|collectionVsShardVsReplicas
operator|.
name|get
argument_list|(
name|coll
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
name|row
operator|.
name|collectionVsShardVsReplicas
operator|.
name|put
argument_list|(
name|coll
argument_list|,
name|c
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
name|replicas
init|=
name|c
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicas
operator|==
literal|null
condition|)
name|c
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|replicas
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|replicas
operator|.
name|add
argument_list|(
operator|new
name|ReplicaInfo
argument_list|(
literal|""
operator|+
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|+
literal|1000
argument_list|,
name|coll
argument_list|,
name|shard
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Cell
name|cell
range|:
name|row
operator|.
name|cells
control|)
block|{
if|if
condition|(
name|cell
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"cores"
argument_list|)
condition|)
name|cell
operator|.
name|val
operator|=
operator|(
operator|(
name|Number
operator|)
name|cell
operator|.
name|val
operator|)
operator|.
name|intValue
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|row
return|;
block|}
DECL|method|removeReplica
name|Pair
argument_list|<
name|Row
argument_list|,
name|ReplicaInfo
argument_list|>
name|removeReplica
parameter_list|(
name|String
name|coll
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|Row
name|row
init|=
name|copy
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
name|c
init|=
name|row
operator|.
name|collectionVsShardVsReplicas
operator|.
name|get
argument_list|(
name|coll
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
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
name|s
init|=
name|c
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|Pair
argument_list|(
name|row
argument_list|,
name|s
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

