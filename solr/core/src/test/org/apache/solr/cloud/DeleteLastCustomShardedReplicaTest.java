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
name|MapSolrParams
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
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|NUM_SLICES
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
name|OverseerCollectionProcessor
operator|.
name|SHARDS_PROP
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
name|ZkNodeProps
operator|.
name|makeMap
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
name|DELETEREPLICA
import|;
end_import

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"SOLR-6347"
argument_list|)
DECL|class|DeleteLastCustomShardedReplicaTest
specifier|public
class|class
name|DeleteLastCustomShardedReplicaTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|client
specifier|private
name|CloudSolrClient
name|client
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeThisClass2
specifier|public
specifier|static
name|void
name|beforeThisClass2
parameter_list|()
throws|throws
name|Exception
block|{    }
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
operator|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|DeleteLastCustomShardedReplicaTest
specifier|public
name|DeleteLastCustomShardedReplicaTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|checkCreatedVsState
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|replicationFactor
init|=
literal|1
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
literal|5
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
literal|"router.name"
argument_list|,
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|,
name|ZkStateReader
operator|.
name|REPLICATION_FACTOR
argument_list|,
name|replicationFactor
argument_list|,
name|ZkStateReader
operator|.
name|MAX_SHARDS_PER_NODE
argument_list|,
name|maxShardsPerNode
argument_list|,
name|NUM_SLICES
argument_list|,
literal|1
argument_list|,
name|SHARDS_PROP
argument_list|,
literal|"a,b"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|collectionInfos
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|collectionName
init|=
literal|"customcollreplicadeletion"
decl_stmt|;
name|createCollection
argument_list|(
name|collectionInfos
argument_list|,
name|collectionName
argument_list|,
name|props
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DocCollection
name|testcoll
init|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
name|testcoll
operator|.
name|getSlice
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getReplicas
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|removeAndWaitForLastReplicaGone
argument_list|(
name|collectionName
argument_list|,
name|replica
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
block|}
DECL|method|removeAndWaitForLastReplicaGone
specifier|protected
name|void
name|removeAndWaitForLastReplicaGone
parameter_list|(
name|String
name|COLL_NAME
parameter_list|,
name|Replica
name|replica
parameter_list|,
name|String
name|shard
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
name|COLL_NAME
argument_list|,
literal|"action"
argument_list|,
name|DELETEREPLICA
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"shard"
argument_list|,
name|shard
argument_list|,
literal|"replica"
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
decl_stmt|;
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
name|this
operator|.
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|long
name|endAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|3000
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|DocCollection
name|testcoll
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endAt
condition|)
block|{
name|testcoll
operator|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLL_NAME
argument_list|)
expr_stmt|;
comment|// In case of a custom sharded collection, the last replica deletion would also lead to
comment|// the deletion of the slice.
name|success
operator|=
name|testcoll
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
operator|==
literal|null
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"replica cleaned up {}/{} core {}"
argument_list|,
name|shard
operator|+
literal|"/"
operator|+
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"current state {}"
argument_list|,
name|testcoll
argument_list|)
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Replica not cleaned up"
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

