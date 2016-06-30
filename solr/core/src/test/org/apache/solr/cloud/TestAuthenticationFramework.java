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
name|io
operator|.
name|File
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
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequestInterceptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HttpContext
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
name|index
operator|.
name|TieredMergePolicy
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
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
name|SolrQuery
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
name|JettyConfig
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
name|impl
operator|.
name|HttpClientUtil
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
name|SolrHttpClientBuilder
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
name|response
operator|.
name|QueryResponse
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
name|response
operator|.
name|RequestStatusState
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
name|SolrInputDocument
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
name|core
operator|.
name|CoreDescriptor
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
name|index
operator|.
name|TieredMergePolicyFactory
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
name|security
operator|.
name|AuthenticationPlugin
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
name|security
operator|.
name|HttpClientBuilderPlugin
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
name|RevertDefaultThreadHandlerRule
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
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
name|TestRule
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import

begin_comment
comment|/**  * Test of the MiniSolrCloudCluster functionality with authentication enabled.  */
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Solr logs to JUL"
argument_list|)
DECL|class|TestAuthenticationFramework
specifier|public
class|class
name|TestAuthenticationFramework
extends|extends
name|LuceneTestCase
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
DECL|field|NUM_SERVERS
specifier|private
name|int
name|NUM_SERVERS
init|=
literal|5
decl_stmt|;
DECL|field|NUM_SHARDS
specifier|private
name|int
name|NUM_SHARDS
init|=
literal|2
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|private
name|int
name|REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
DECL|field|requestUsername
specifier|static
name|String
name|requestUsername
init|=
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
decl_stmt|;
DECL|field|requestPassword
specifier|static
name|String
name|requestPassword
init|=
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
decl_stmt|;
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|ClassRule
DECL|field|solrClassRules
specifier|public
specifier|static
name|TestRule
name|solrClassRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
operator|.
name|around
argument_list|(
operator|new
name|RevertDefaultThreadHandlerRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setupAuthenticationPlugin
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|setupAuthenticationPlugin
specifier|private
name|void
name|setupAuthenticationPlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|,
literal|"org.apache.solr.cloud.TestAuthenticationFramework$MockAuthenticationPlugin"
argument_list|)
expr_stmt|;
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
operator|=
literal|null
expr_stmt|;
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniSolrCloudCluster
name|miniCluster
init|=
name|createMiniSolrCloudCluster
argument_list|()
decl_stmt|;
comment|// Should pass
name|collectionCreateSearchDelete
argument_list|(
name|miniCluster
argument_list|)
expr_stmt|;
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
operator|=
literal|"solr"
expr_stmt|;
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
operator|=
literal|"s0lrRocks"
expr_stmt|;
comment|// Should fail with 401
try|try
block|{
name|collectionCreateSearchDelete
argument_list|(
name|miniCluster
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should've returned a 401 error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Error 401"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Should've returned a 401 error"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
operator|=
literal|null
expr_stmt|;
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
operator|=
literal|null
expr_stmt|;
block|}
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|createMiniSolrCloudCluster
specifier|private
name|MiniSolrCloudCluster
name|createMiniSolrCloudCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|JettyConfig
operator|.
name|Builder
name|jettyConfig
init|=
name|JettyConfig
operator|.
name|builder
argument_list|()
decl_stmt|;
name|jettyConfig
operator|.
name|waitForLoadingCoresToFinish
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|jettyConfig
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createCollection
specifier|private
name|void
name|createCollection
parameter_list|(
name|MiniSolrCloudCluster
name|miniCluster
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|asyncId
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
name|File
name|configDir
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
decl_stmt|;
name|miniCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
name|configName
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|persistIndex
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionProperties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
literal|"solr.tests.maxBufferedDocs"
argument_list|,
literal|"100000"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
literal|"solr.tests.ramBufferSizeMB"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|// use non-test classes so RandomizedRunner isn't necessary
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|SolrTestCaseJ4
operator|.
name|SYSTEM_PROPERTY_SOLR_TESTS_MERGEPOLICY
argument_list|,
name|TieredMergePolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|SolrTestCaseJ4
operator|.
name|SYSTEM_PROPERTY_SOLR_TESTS_USEMERGEPOLICY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|SolrTestCaseJ4
operator|.
name|SYSTEM_PROPERTY_SOLR_TESTS_USEMERGEPOLICYFACTORY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|SolrTestCaseJ4
operator|.
name|SYSTEM_PROPERTY_SOLR_TESTS_MERGEPOLICYFACTORY
argument_list|,
name|TieredMergePolicyFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|SolrTestCaseJ4
operator|.
name|SYSTEM_PROPERTY_SOLR_TESTS_USEMERGEPOLICYFACTORY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
name|SolrTestCaseJ4
operator|.
name|SYSTEM_PROPERTY_SOLR_TESTS_USEMERGEPOLICY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
literal|"solr.tests.mergeScheduler"
argument_list|,
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|putIfAbsent
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
operator|(
name|persistIndex
condition|?
literal|"solr.StandardDirectoryFactory"
else|:
literal|"solr.RAMDirectoryFactory"
operator|)
argument_list|)
expr_stmt|;
name|miniCluster
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|NUM_SHARDS
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|configName
argument_list|,
literal|null
argument_list|,
name|asyncId
argument_list|,
name|collectionProperties
argument_list|)
expr_stmt|;
block|}
DECL|method|collectionCreateSearchDelete
specifier|public
name|void
name|collectionCreateSearchDelete
parameter_list|(
name|MiniSolrCloudCluster
name|miniCluster
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"testcollection"
decl_stmt|;
specifier|final
name|CloudSolrClient
name|cloudSolrClient
init|=
name|miniCluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
init|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|assertTrue
argument_list|(
name|jetty
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create collection
name|log
operator|.
name|info
argument_list|(
literal|"#### Creating a collection"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|asyncId
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
literal|"asyncId("
operator|+
name|collectionName
operator|+
literal|".create)="
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|)
decl_stmt|;
name|createCollection
argument_list|(
name|miniCluster
argument_list|,
name|collectionName
argument_list|,
name|asyncId
argument_list|)
expr_stmt|;
if|if
condition|(
name|asyncId
operator|!=
literal|null
condition|)
block|{
specifier|final
name|RequestStatusState
name|state
init|=
name|AbstractFullDistribZkTestBase
operator|.
name|getRequestStateAfterCompletion
argument_list|(
name|asyncId
argument_list|,
literal|330
argument_list|,
name|cloudSolrClient
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"did not see async createCollection completion"
argument_list|,
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
name|ZkStateReader
name|zkStateReader
init|=
name|miniCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
comment|// modify/query collection
name|log
operator|.
name|info
argument_list|(
literal|"#### updating a querying collection"
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// delete the collection we created earlier
name|miniCluster
operator|.
name|deleteCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForCollectionToDisappear
argument_list|(
name|collectionName
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
comment|// create it again
name|String
name|asyncId2
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
literal|"asyncId("
operator|+
name|collectionName
operator|+
literal|".create)="
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|)
decl_stmt|;
name|createCollection
argument_list|(
name|miniCluster
argument_list|,
name|collectionName
argument_list|,
name|asyncId2
argument_list|)
expr_stmt|;
if|if
condition|(
name|asyncId2
operator|!=
literal|null
condition|)
block|{
specifier|final
name|RequestStatusState
name|state
init|=
name|AbstractFullDistribZkTestBase
operator|.
name|getRequestStateAfterCompletion
argument_list|(
name|asyncId2
argument_list|,
literal|330
argument_list|,
name|cloudSolrClient
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"did not see async createCollection completion"
argument_list|,
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
comment|// check that there's no left-over state
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cloudSolrClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cloudSolrClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MockAuthenticationPlugin
specifier|public
specifier|static
class|class
name|MockAuthenticationPlugin
extends|extends
name|AuthenticationPlugin
implements|implements
name|HttpClientBuilderPlugin
block|{
DECL|field|expectedUsername
specifier|public
specifier|static
name|String
name|expectedUsername
decl_stmt|;
DECL|field|expectedPassword
specifier|public
specifier|static
name|String
name|expectedPassword
decl_stmt|;
DECL|field|interceptor
specifier|private
name|HttpRequestInterceptor
name|interceptor
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|doAuthenticate
specifier|public
name|void
name|doAuthenticate
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|expectedUsername
operator|==
literal|null
condition|)
block|{
name|filterChain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
name|HttpServletRequest
name|httpRequest
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|String
name|username
init|=
name|httpRequest
operator|.
name|getHeader
argument_list|(
literal|"username"
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|httpRequest
operator|.
name|getHeader
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Username: "
operator|+
name|username
operator|+
literal|", password: "
operator|+
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
operator|.
name|equals
argument_list|(
name|username
argument_list|)
operator|&&
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
name|filterChain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
else|else
block|{
operator|(
operator|(
name|HttpServletResponse
operator|)
name|response
operator|)
operator|.
name|sendError
argument_list|(
literal|401
argument_list|,
literal|"Unauthorized request"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getHttpClientBuilder
specifier|public
name|SolrHttpClientBuilder
name|getHttpClientBuilder
parameter_list|(
name|SolrHttpClientBuilder
name|httpClientBuilder
parameter_list|)
block|{
name|interceptor
operator|=
operator|new
name|HttpRequestInterceptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|req
parameter_list|,
name|HttpContext
name|rsp
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
name|req
operator|.
name|addHeader
argument_list|(
literal|"username"
argument_list|,
name|requestUsername
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
literal|"password"
argument_list|,
name|requestPassword
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|HttpClientUtil
operator|.
name|addRequestInterceptor
argument_list|(
name|interceptor
argument_list|)
expr_stmt|;
return|return
name|httpClientBuilder
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|HttpClientUtil
operator|.
name|removeRequestInterceptor
argument_list|(
name|interceptor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

