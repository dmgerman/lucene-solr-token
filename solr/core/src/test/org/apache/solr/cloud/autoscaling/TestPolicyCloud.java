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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|SolrClientDataProvider
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
name|cloud
operator|.
name|OverseerTaskProcessor
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
name|SolrCloudTestCase
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|rules
operator|.
name|ExpectedException
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
annotation|@
name|LuceneTestCase
operator|.
name|Slow
DECL|class|TestPolicyCloud
specifier|public
class|class
name|TestPolicyCloud
extends|extends
name|SolrCloudTestCase
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
annotation|@
name|org
operator|.
name|junit
operator|.
name|Rule
DECL|field|expectedException
specifier|public
name|ExpectedException
name|expectedException
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
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
literal|5
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
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|deleteAllCollections
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|setData
argument_list|(
name|ZkStateReader
operator|.
name|SOLR_AUTOSCALING_CONF_PATH
argument_list|,
literal|"{}"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateCollectionAddShardUsingPolicy
specifier|public
name|void
name|testCreateCollectionAddShardUsingPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|jetty
init|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|String
name|commands
init|=
literal|"{set-policy :{c1 : [{replica:1 , shard:'#EACH', port: '"
operator|+
name|port
operator|+
literal|"'}]}}"
decl_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|AutoScalingHandlerTest
operator|.
name|createAutoScalingRequest
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
name|commands
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|Utils
operator|.
name|getJson
argument_list|(
name|cluster
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|SOLR_AUTOSCALING_CONF_PATH
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"full json:"
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|json
argument_list|)
argument_list|,
literal|"#EACH"
argument_list|,
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|json
argument_list|,
literal|true
argument_list|,
literal|"/policies/c1[0]/shard"
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
literal|"policiesTest"
argument_list|,
literal|null
argument_list|,
literal|"s1,s2"
argument_list|,
literal|1
argument_list|)
operator|.
name|setPolicy
argument_list|(
literal|"c1"
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
name|coll
init|=
name|getCollectionState
argument_list|(
literal|"policiesTest"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"c1"
argument_list|,
name|coll
operator|.
name|getPolicyName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|coll
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|coll
operator|.
name|forEachReplica
argument_list|(
parameter_list|(
name|s
parameter_list|,
name|replica
parameter_list|)
lambda|->
name|assertEquals
argument_list|(
name|jetty
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createShard
argument_list|(
literal|"policiesTest"
argument_list|,
literal|"s3"
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
name|coll
operator|=
name|getCollectionState
argument_list|(
literal|"policiesTest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|coll
operator|.
name|getSlice
argument_list|(
literal|"s3"
argument_list|)
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|coll
operator|.
name|getSlice
argument_list|(
literal|"s3"
argument_list|)
operator|.
name|forEach
argument_list|(
name|replica
lambda|->
name|assertEquals
argument_list|(
name|jetty
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDataProvider
specifier|public
name|void
name|testDataProvider
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
literal|"policiesTest"
argument_list|,
literal|"conf"
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
name|DocCollection
name|rulesCollection
init|=
name|getCollectionState
argument_list|(
literal|"policiesTest"
argument_list|)
decl_stmt|;
name|SolrClientDataProvider
name|provider
init|=
operator|new
name|SolrClientDataProvider
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|val
init|=
name|provider
operator|.
name|getNodeValues
argument_list|(
name|rulesCollection
operator|.
name|getReplicas
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"freedisk"
argument_list|,
literal|"cores"
argument_list|,
literal|"heapUsage"
argument_list|,
literal|"sysLoadAvg"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"freedisk"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"heapUsage"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"sysLoadAvg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"cores"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"freedisk value is "
operator|+
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"freedisk"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|Double
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"freedisk"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"heapUsage value is "
operator|+
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"heapUsage"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|Double
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"heapUsage"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sysLoadAvg value is "
operator|+
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"sysLoadAvg"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|Double
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|val
operator|.
name|get
argument_list|(
literal|"sysLoadAvg"
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0d
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|overseerNode
init|=
name|OverseerTaskProcessor
operator|.
name|getLeaderNode
argument_list|(
name|cluster
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|CollectionAdminRequest
operator|.
name|addRole
argument_list|(
name|overseerNode
argument_list|,
literal|"overseer"
argument_list|)
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|Utils
operator|.
name|getJson
argument_list|(
name|cluster
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|ROLES
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|9
operator|&&
name|data
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"NO overseer node created"
argument_list|)
throw|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|val
operator|=
name|provider
operator|.
name|getNodeValues
argument_list|(
name|overseerNode
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"nodeRole"
argument_list|,
literal|"ip_1"
argument_list|,
literal|"ip_2"
argument_list|,
literal|"ip_3"
argument_list|,
literal|"ip_4"
argument_list|,
literal|"sysprop.java.version"
argument_list|,
literal|"sysprop.java.vendor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"overseer"
argument_list|,
name|val
operator|.
name|get
argument_list|(
literal|"nodeRole"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"ip_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"ip_2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"ip_3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"ip_4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"sysprop.java.version"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|val
operator|.
name|get
argument_list|(
literal|"sysprop.java.vendor"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

