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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|CollectionAdminRequest
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
name|CoreStatus
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
name|Test
import|;
end_import

begin_class
DECL|class|DeleteReplicaTest
specifier|public
class|class
name|DeleteReplicaTest
extends|extends
name|SolrCloudTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|4
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
argument_list|,
name|configset
argument_list|(
literal|"cloud-minimal"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteLiveReplicaTest
specifier|public
name|void
name|deleteLiveReplicaTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"delLiveColl"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|DocCollection
name|state
init|=
name|getCollectionState
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Slice
name|shard
init|=
name|getRandomShard
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
name|getRandomReplica
argument_list|(
name|shard
argument_list|,
parameter_list|(
name|r
parameter_list|)
lambda|->
name|r
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|CoreStatus
name|coreStatus
init|=
name|getCoreStatus
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|Path
name|dataDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|coreStatus
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CollectionAdminRequest
operator|.
name|deleteReplica
argument_list|(
name|collectionName
argument_list|,
name|shard
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setOnlyIfDown
argument_list|(
literal|true
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected error message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"state is 'active'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Data directory for "
operator|+
name|replica
operator|.
name|getName
argument_list|()
operator|+
literal|" should not have been deleted"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteReplica
argument_list|(
name|collectionName
argument_list|,
name|shard
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected replica "
operator|+
name|replica
operator|.
name|getName
argument_list|()
operator|+
literal|" to have been removed"
argument_list|,
name|collectionName
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
name|Slice
name|testShard
init|=
name|c
operator|.
name|getSlice
argument_list|(
name|shard
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|testShard
operator|.
name|getReplica
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Data directory for "
operator|+
name|replica
operator|.
name|getName
argument_list|()
operator|+
literal|" should have been removed"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|dataDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteReplicaAndVerifyDirectoryCleanup
specifier|public
name|void
name|deleteReplicaAndVerifyDirectoryCleanup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"deletereplica_test"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|Replica
name|leader
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|collectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
comment|//Confirm that the instance and data directory exist
name|CoreStatus
name|coreStatus
init|=
name|getCoreStatus
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Instance directory doesn't exist"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|coreStatus
operator|.
name|getInstanceDirectory
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"DataDirectory doesn't exist"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|coreStatus
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteReplica
argument_list|(
name|collectionName
argument_list|,
literal|"shard1"
argument_list|,
name|leader
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|Replica
name|newLeader
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|collectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|leader
operator|.
name|equals
argument_list|(
name|newLeader
argument_list|)
argument_list|)
expr_stmt|;
comment|//Confirm that the instance and data directory were deleted by default
name|assertFalse
argument_list|(
literal|"Instance directory still exists"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|coreStatus
operator|.
name|getInstanceDirectory
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"DataDirectory still exists"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|coreStatus
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteReplicaByCount
specifier|public
name|void
name|deleteReplicaByCount
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"deleteByCount"
decl_stmt|;
name|pickRandom
argument_list|(
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected a single shard with three replicas"
argument_list|,
name|collectionName
argument_list|,
name|clusterShape
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteReplicasFromShard
argument_list|(
name|collectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|2
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected a single shard with a single replica"
argument_list|,
name|collectionName
argument_list|,
name|clusterShape
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|CollectionAdminRequest
operator|.
name|deleteReplicasFromShard
argument_list|(
name|collectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected Exception, Can't delete the last replica by count"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// expected
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There is only one replica available"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DocCollection
name|docCollection
init|=
name|getCollectionState
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
comment|// We know that since leaders are preserved, PULL replicas should not be left alone in the shard
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|docCollection
operator|.
name|getSlice
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|getReplicas
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|Replica
operator|.
name|Type
operator|.
name|PULL
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteReplicaByCountForAllShards
specifier|public
name|void
name|deleteReplicaByCountForAllShards
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"deleteByCountNew"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected two shards with two replicas each"
argument_list|,
name|collectionName
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteReplicasFromAllShards
argument_list|(
name|collectionName
argument_list|,
literal|1
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected two shards with one replica each"
argument_list|,
name|collectionName
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

