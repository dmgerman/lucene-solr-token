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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|NoHttpResponseException
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
name|embedded
operator|.
name|JettySolrRunner
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
name|HttpSolrClient
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
name|util
operator|.
name|RTimer
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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|List
import|;
end_import

begin_class
DECL|class|LeaderInitiatedRecoveryOnCommitTest
specifier|public
class|class
name|LeaderInitiatedRecoveryOnCommitTest
extends|extends
name|BasicDistributedZkTest
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
DECL|field|sleepMsBeforeHealPartition
specifier|private
specifier|static
specifier|final
name|long
name|sleepMsBeforeHealPartition
init|=
literal|2000L
decl_stmt|;
DECL|field|onlyLeaderIndexes
specifier|private
specifier|final
name|boolean
name|onlyLeaderIndexes
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
DECL|method|LeaderInitiatedRecoveryOnCommitTest
specifier|public
name|LeaderInitiatedRecoveryOnCommitTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|fixShardCount
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|useTlogReplicas
specifier|protected
name|boolean
name|useTlogReplicas
parameter_list|()
block|{
return|return
name|onlyLeaderIndexes
return|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|oneShardTest
argument_list|()
expr_stmt|;
name|multiShardTest
argument_list|()
expr_stmt|;
block|}
DECL|method|multiShardTest
specifier|private
name|void
name|multiShardTest
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Running multiShardTest"
argument_list|)
expr_stmt|;
comment|// create a collection that has 2 shard and 2 replicas
name|String
name|testCollectionName
init|=
literal|"c8n_2x2_commits"
decl_stmt|;
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 1 replicas for collection "
operator|+
name|testCollectionName
operator|+
literal|" but found "
operator|+
name|notLeaders
operator|.
name|size
argument_list|()
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|notLeaders
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"All replicas active for "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// let's put the leader in its own partition, no replicas can contact it now
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating partition to leader at "
operator|+
name|leader
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
name|SocketProxy
name|leaderProxy
init|=
name|getProxyForReplica
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|leaderProxy
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// let's find the leader of shard2 and ask him to commit
name|Replica
name|shard2Leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard2"
argument_list|)
decl_stmt|;
name|sendCommitWithRetry
argument_list|(
name|shard2Leader
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|forceUpdateCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// get the latest state
name|leader
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Leader was not active"
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
argument_list|,
name|leader
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Healing partitioned replica at "
operator|+
name|leader
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
name|leaderProxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
comment|// try to clean up
name|attemptCollectionDelete
argument_list|(
name|cloudClient
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"multiShardTest completed OK"
argument_list|)
expr_stmt|;
block|}
DECL|method|oneShardTest
specifier|private
name|void
name|oneShardTest
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Running oneShardTest"
argument_list|)
expr_stmt|;
comment|// create a collection that has 1 shard and 3 replicas
name|String
name|testCollectionName
init|=
literal|"c8n_1x3_commits"
decl_stmt|;
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 2 replicas for collection "
operator|+
name|testCollectionName
operator|+
literal|" but found "
operator|+
name|notLeaders
operator|.
name|size
argument_list|()
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|notLeaders
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"All replicas active for "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// let's put the leader in its own partition, no replicas can contact it now
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating partition to leader at "
operator|+
name|leader
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
name|SocketProxy
name|leaderProxy
init|=
name|getProxyForReplica
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|leaderProxy
operator|.
name|close
argument_list|()
expr_stmt|;
name|Replica
name|replica
init|=
name|notLeaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|sendCommitWithRetry
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|forceUpdateCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// get the latest state
name|leader
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Leader was not active"
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
argument_list|,
name|leader
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Healing partitioned replica at "
operator|+
name|leader
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
name|leaderProxy
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsBeforeHealPartition
argument_list|)
expr_stmt|;
comment|// try to clean up
name|attemptCollectionDelete
argument_list|(
name|cloudClient
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"oneShardTest completed OK"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Overrides the parent implementation to install a SocketProxy in-front of the Jetty server.    */
annotation|@
name|Override
DECL|method|createJetty
specifier|public
name|JettySolrRunner
name|createJetty
parameter_list|(
name|File
name|solrHome
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|shardList
parameter_list|,
name|String
name|solrConfigOverride
parameter_list|,
name|String
name|schemaOverride
parameter_list|,
name|Replica
operator|.
name|Type
name|replicaType
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createProxiedJetty
argument_list|(
name|solrHome
argument_list|,
name|dataDir
argument_list|,
name|shardList
argument_list|,
name|solrConfigOverride
argument_list|,
name|schemaOverride
argument_list|,
name|replicaType
argument_list|)
return|;
block|}
DECL|method|sendCommitWithRetry
specifier|protected
name|void
name|sendCommitWithRetry
parameter_list|(
name|Replica
name|replica
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|replicaCoreUrl
init|=
name|replica
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending commit request to: "
operator|+
name|replicaCoreUrl
argument_list|)
expr_stmt|;
specifier|final
name|RTimer
name|timer
init|=
operator|new
name|RTimer
argument_list|()
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|replicaCoreUrl
argument_list|)
init|)
block|{
try|try
block|{
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sent commit request to {} OK, took {}ms"
argument_list|,
name|replicaCoreUrl
argument_list|,
name|timer
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|Throwable
name|rootCause
init|=
name|SolrException
operator|.
name|getRootCause
argument_list|(
name|exc
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootCause
operator|instanceof
name|NoHttpResponseException
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No HTTP response from sending commit request to "
operator|+
name|replicaCoreUrl
operator|+
literal|"; will re-try after waiting 3 seconds"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Second attempt at sending commit to "
operator|+
name|replicaCoreUrl
operator|+
literal|" succeeded."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|exc
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

