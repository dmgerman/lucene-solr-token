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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|cloud
operator|.
name|overseer
operator|.
name|OverseerAction
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
name|SolrZkClient
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
name|params
operator|.
name|CollectionParams
operator|.
name|CollectionAction
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
name|TimeOut
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
name|IOException
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
name|concurrent
operator|.
name|TimeUnit
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
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
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
name|OverseerCollectionConfigSetProcessor
operator|.
name|getSortedOverseerNodeNames
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
name|OverseerCollectionMessageHandler
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
name|common
operator|.
name|util
operator|.
name|Utils
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|MAX_SHARDS_PER_NODE
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
name|REPLICATION_FACTOR
import|;
end_import

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"SOLR-5776"
argument_list|)
DECL|class|OverseerRolesTest
specifier|public
class|class
name|OverseerRolesTest
extends|extends
name|AbstractFullDistribZkTestBase
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
DECL|method|OverseerRolesTest
specifier|public
name|OverseerRolesTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|fixShardCount
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|6
else|:
literal|2
argument_list|)
expr_stmt|;
block|}
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
try|try
init|(
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
init|)
block|{
name|testQuitCommand
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|testOverseerRole
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQuitCommand
specifier|private
name|void
name|testQuitCommand
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"testOverseerQuit"
decl_stmt|;
name|createCollection
argument_list|(
name|collectionName
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
name|SolrZkClient
name|zk
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|data
operator|=
name|zk
operator|.
name|getData
argument_list|(
literal|"/overseer_elect/leader"
argument_list|,
literal|null
argument_list|,
operator|new
name|Stat
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|String
name|s
init|=
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|String
name|leader
init|=
name|LeaderElector
operator|.
name|getNodeName
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Overseer
operator|.
name|getStateUpdateQueue
argument_list|(
name|zk
argument_list|)
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerAction
operator|.
name|QUIT
operator|.
name|toLower
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|TimeOut
name|timeout
init|=
operator|new
name|TimeOut
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|String
name|newLeader
init|=
literal|null
decl_stmt|;
for|for
control|(
init|;
operator|!
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|;
control|)
block|{
name|newLeader
operator|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|zk
argument_list|)
expr_stmt|;
if|if
condition|(
name|newLeader
operator|!=
literal|null
operator|&&
operator|!
name|newLeader
operator|.
name|equals
argument_list|(
name|leader
argument_list|)
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertNotSame
argument_list|(
literal|"Leader not changed yet"
argument_list|,
name|newLeader
argument_list|,
name|leader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The old leader should have rejoined election "
argument_list|,
name|OverseerCollectionConfigSetProcessor
operator|.
name|getSortedOverseerNodeNames
argument_list|(
name|zk
argument_list|)
operator|.
name|contains
argument_list|(
name|leader
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOverseerRole
specifier|private
name|void
name|testOverseerRole
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"testOverseerCol"
decl_stmt|;
name|createCollection
argument_list|(
name|collectionName
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
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getSortedOverseerNodeNames
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"All nodes {}"
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|String
name|currentLeader
init|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Current leader {} "
argument_list|,
name|currentLeader
argument_list|)
expr_stmt|;
name|l
operator|.
name|remove
argument_list|(
name|currentLeader
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|overseerDesignate
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"overseerDesignate {}"
argument_list|,
name|overseerDesignate
argument_list|)
expr_stmt|;
name|setOverseerRole
argument_list|(
name|client
argument_list|,
name|CollectionAction
operator|.
name|ADDROLE
argument_list|,
name|overseerDesignate
argument_list|)
expr_stmt|;
name|TimeOut
name|timeout
init|=
operator|new
name|TimeOut
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|boolean
name|leaderchanged
init|=
literal|false
decl_stmt|;
for|for
control|(
init|;
operator|!
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|overseerDesignate
operator|.
name|equals
argument_list|(
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"overseer designate is the new overseer"
argument_list|)
expr_stmt|;
name|leaderchanged
operator|=
literal|true
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
literal|"could not set the new overseer . expected "
operator|+
name|overseerDesignate
operator|+
literal|" current order : "
operator|+
name|getSortedOverseerNodeNames
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
operator|+
literal|" ldr :"
operator|+
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|,
name|leaderchanged
argument_list|)
expr_stmt|;
comment|//add another node as overseer
name|l
operator|.
name|remove
argument_list|(
name|overseerDesignate
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|anotherOverseer
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding another overseer designate {}"
argument_list|,
name|anotherOverseer
argument_list|)
expr_stmt|;
name|setOverseerRole
argument_list|(
name|client
argument_list|,
name|CollectionAction
operator|.
name|ADDROLE
argument_list|,
name|anotherOverseer
argument_list|)
expr_stmt|;
name|String
name|currentOverseer
init|=
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Current Overseer {}"
argument_list|,
name|currentOverseer
argument_list|)
expr_stmt|;
name|String
name|hostPort
init|=
name|currentOverseer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|currentOverseer
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//
comment|//
name|log
operator|.
name|info
argument_list|(
literal|"hostPort : {}"
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|leaderJetty
init|=
literal|null
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|String
name|s
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"jetTy {}"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|" , "
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|contains
argument_list|(
name|hostPort
argument_list|)
condition|)
block|{
name|leaderJetty
operator|=
name|jetty
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"Could not find a jetty2 kill"
argument_list|,
name|leaderJetty
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"leader node {}"
argument_list|,
name|leaderJetty
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"current election Queue"
argument_list|,
name|OverseerCollectionConfigSetProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
literal|"/overseer_elect/election"
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|leaderJetty
argument_list|)
expr_stmt|;
name|timeout
operator|=
operator|new
name|TimeOut
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|leaderchanged
operator|=
literal|false
expr_stmt|;
for|for
control|(
init|;
operator|!
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|;
control|)
block|{
name|currentOverseer
operator|=
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|anotherOverseer
operator|.
name|equals
argument_list|(
name|currentOverseer
argument_list|)
condition|)
block|{
name|leaderchanged
operator|=
literal|true
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
literal|"New overseer designate has not become the overseer, expected : "
operator|+
name|anotherOverseer
operator|+
literal|"actual : "
operator|+
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|,
name|leaderchanged
argument_list|)
expr_stmt|;
block|}
DECL|method|setOverseerRole
specifier|private
name|void
name|setOverseerRole
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|,
name|CollectionAction
name|action
parameter_list|,
name|String
name|overseerDesignate
parameter_list|)
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Adding overseer designate {} "
argument_list|,
name|overseerDesignate
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"action"
argument_list|,
name|action
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
argument_list|,
literal|"role"
argument_list|,
literal|"overseer"
argument_list|,
literal|"node"
argument_list|,
name|overseerDesignate
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
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|createCollection
specifier|protected
name|void
name|createCollection
parameter_list|(
name|String
name|COLL_NAME
parameter_list|,
name|CloudSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|replicationFactor
init|=
literal|2
decl_stmt|;
name|int
name|numShards
init|=
literal|4
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
operator|(
operator|(
operator|(
operator|(
name|numShards
operator|+
literal|1
operator|)
operator|*
name|replicationFactor
operator|)
operator|/
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|)
operator|+
literal|1
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|makeMap
argument_list|(
name|REPLICATION_FACTOR
argument_list|,
name|replicationFactor
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|,
name|maxShardsPerNode
argument_list|,
name|NUM_SLICES
argument_list|,
name|numShards
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
name|createCollection
argument_list|(
name|collectionInfos
argument_list|,
name|COLL_NAME
argument_list|,
name|props
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

