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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|Properties
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
name|annotations
operator|.
name|ThreadLeakFilters
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|minikdc
operator|.
name|MiniKdc
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
name|Constants
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
name|Krb5HttpClientConfigurer
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
name|util
operator|.
name|BadZookeeperThreadsFilter
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
name|BeforeClass
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

begin_comment
comment|/**  * Test 5 nodes Solr cluster with Kerberos plugin enabled.  * This test is Ignored right now as Mini KDC has a known bug that  * doesn't allow us to run multiple nodes on the same host.  * https://issues.apache.org/jira/browse/HADOOP-9893  */
end_comment

begin_class
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|BadZookeeperThreadsFilter
operator|.
name|class
comment|// Zookeeper login leaks TGT renewal threads
block|}
argument_list|)
annotation|@
name|LuceneTestCase
operator|.
name|Slow
annotation|@
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Solr logs to JUL"
argument_list|)
DECL|class|TestSolrCloudWithKerberosAlt
specifier|public
class|class
name|TestSolrCloudWithKerberosAlt
extends|extends
name|LuceneTestCase
block|{
DECL|field|originalConfig
specifier|private
specifier|final
name|Configuration
name|originalConfig
init|=
name|Configuration
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
DECL|field|NUM_SERVERS
specifier|protected
specifier|final
name|int
name|NUM_SERVERS
decl_stmt|;
DECL|field|NUM_SHARDS
specifier|protected
specifier|final
name|int
name|NUM_SHARDS
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|protected
specifier|final
name|int
name|REPLICATION_FACTOR
decl_stmt|;
DECL|method|TestSolrCloudWithKerberosAlt
specifier|public
name|TestSolrCloudWithKerberosAlt
parameter_list|()
block|{
name|NUM_SERVERS
operator|=
literal|1
expr_stmt|;
name|NUM_SHARDS
operator|=
literal|1
expr_stmt|;
name|REPLICATION_FACTOR
operator|=
literal|1
expr_stmt|;
block|}
DECL|field|kdc
specifier|private
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|savedLocale
specifier|private
name|Locale
name|savedLocale
decl_stmt|;
comment|// in case locale is broken and we need to fill in a working locale
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
name|BeforeClass
DECL|method|betterNotBeJava9
specifier|public
specifier|static
name|void
name|betterNotBeJava9
parameter_list|()
block|{
name|assumeFalse
argument_list|(
literal|"FIXME: SOLR-8182: This test fails under Java 9"
argument_list|,
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|savedLocale
operator|=
name|KerberosTestUtil
operator|.
name|overrideLocaleIfNotSpportedByMiniKdc
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|setupMiniKdc
argument_list|()
expr_stmt|;
name|HttpClientUtil
operator|.
name|setConfigurer
argument_list|(
operator|new
name|Krb5HttpClientConfigurer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupMiniKdc
specifier|private
name|void
name|setupMiniKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.jaas.debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|kdcDir
init|=
name|createTempDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"minikdc"
decl_stmt|;
name|kdc
operator|=
name|KerberosTestUtil
operator|.
name|getKdc
argument_list|(
operator|new
name|File
argument_list|(
name|kdcDir
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|keytabFile
init|=
operator|new
name|File
argument_list|(
name|kdcDir
argument_list|,
literal|"keytabs"
argument_list|)
decl_stmt|;
name|String
name|solrServerPrincipal
init|=
literal|"HTTP/127.0.0.1"
decl_stmt|;
name|String
name|solrClientPrincipal
init|=
literal|"solr"
decl_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|solrServerPrincipal
argument_list|,
name|solrClientPrincipal
argument_list|)
expr_stmt|;
name|String
name|jaas
init|=
literal|"SolrClient {\n"
operator|+
literal|" com.sun.security.auth.module.Krb5LoginModule required\n"
operator|+
literal|" useKeyTab=true\n"
operator|+
literal|" keyTab=\""
operator|+
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\"\n"
operator|+
literal|" storeKey=true\n"
operator|+
literal|" useTicketCache=false\n"
operator|+
literal|" doNotPrompt=true\n"
operator|+
literal|" debug=true\n"
operator|+
literal|" principal=\""
operator|+
name|solrClientPrincipal
operator|+
literal|"\";\n"
operator|+
literal|"};"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|KerberosTestUtil
operator|.
name|JaasConfiguration
argument_list|(
name|solrClientPrincipal
argument_list|,
name|keytabFile
argument_list|,
literal|"SolrClient"
argument_list|)
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|jaasFilePath
init|=
name|kdcDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"jaas-client.conf"
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|jaasFilePath
argument_list|)
argument_list|,
name|jaas
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|,
name|jaasFilePath
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.jaas.appname"
argument_list|,
literal|"SolrClient"
argument_list|)
expr_stmt|;
comment|// Get this app name from the jaas file
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.cookie.domain"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.principal"
argument_list|,
name|solrServerPrincipal
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.keytab"
argument_list|,
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|,
literal|"org.apache.solr.security.KerberosPlugin"
argument_list|)
expr_stmt|;
comment|// Extracts 127.0.0.1 from HTTP/127.0.0.1@EXAMPLE.COM
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.name.rules"
argument_list|,
literal|"RULE:[1:$1@$0](.*EXAMPLE.COM)s/@.*//"
operator|+
literal|"\nRULE:[2:$2@$0](.*EXAMPLE.COM)s/@.*//"
operator|+
literal|"\nDEFAULT"
argument_list|)
expr_stmt|;
comment|// more debugging, if needed
comment|/*System.setProperty("sun.security.jgss.debug", "true");     System.setProperty("sun.security.krb5.debug", "true");     System.setProperty("sun.security.jgss.debug", "true");     System.setProperty("java.security.debug", "logincontext,policy,scl,gssloginconfig");*/
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
name|testCollectionCreateSearchDelete
argument_list|()
expr_stmt|;
comment|// sometimes run a second test e.g. to test collection create-delete-create scenario
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
name|testCollectionCreateSearchDelete
argument_list|()
expr_stmt|;
block|}
DECL|method|testCollectionCreateSearchDelete
specifier|protected
name|void
name|testCollectionCreateSearchDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpClientUtil
operator|.
name|setConfigurer
argument_list|(
operator|new
name|Krb5HttpClientConfigurer
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|collectionName
init|=
literal|"testkerberoscollection"
decl_stmt|;
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr-no-core.xml"
argument_list|)
decl_stmt|;
name|MiniSolrCloudCluster
name|miniCluster
init|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
literal|null
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|,
name|solrXml
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CloudSolrClient
name|cloudSolrClient
init|=
name|miniCluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|cloudSolrClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
try|try
block|{
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
name|CollectionAdminRequest
operator|.
name|Create
name|createRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|createRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setNumShards
argument_list|(
name|NUM_SHARDS
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setReplicationFactor
argument_list|(
name|REPLICATION_FACTOR
argument_list|)
expr_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"solr.tests.maxBufferedDocs"
argument_list|,
literal|"100000"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"solr.tests.ramBufferSizeMB"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|// use non-test classes so RandomizedRunner isn't necessary
name|properties
operator|.
name|put
argument_list|(
literal|"solr.tests.mergePolicy"
argument_list|,
literal|"org.apache.lucene.index.TieredMergePolicy"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"solr.tests.mergeScheduler"
argument_list|,
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.RAMDirectoryFactory"
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|process
argument_list|(
name|cloudSolrClient
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|45000
argument_list|,
literal|null
argument_list|)
init|;
name|ZkStateReader
name|zkStateReader
operator|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
init|)
block|{
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
name|CollectionAdminRequest
operator|.
name|Delete
name|deleteRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|deleteRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|deleteRequest
operator|.
name|process
argument_list|(
name|cloudSolrClient
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
block|}
block|}
finally|finally
block|{
name|cloudSolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
literal|"java.security.auth.login.config"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"cookie.domain"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"kerberos.principal"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"kerberos.keytab"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.name.rules"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.jaas.debug"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|this
operator|.
name|originalConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
block|{
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|Locale
operator|.
name|setDefault
argument_list|(
name|savedLocale
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

