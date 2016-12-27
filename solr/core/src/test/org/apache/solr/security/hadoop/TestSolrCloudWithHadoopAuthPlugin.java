begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.security.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
operator|.
name|hadoop
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|cloud
operator|.
name|AbstractDistribZkTestBase
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
name|KerberosTestServices
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
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
DECL|class|TestSolrCloudWithHadoopAuthPlugin
specifier|public
class|class
name|TestSolrCloudWithHadoopAuthPlugin
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|NUM_SERVERS
specifier|protected
specifier|static
specifier|final
name|int
name|NUM_SERVERS
init|=
literal|1
decl_stmt|;
DECL|field|NUM_SHARDS
specifier|protected
specifier|static
specifier|final
name|int
name|NUM_SHARDS
init|=
literal|1
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|protected
specifier|static
specifier|final
name|int
name|REPLICATION_FACTOR
init|=
literal|1
decl_stmt|;
DECL|field|kerberosTestServices
specifier|private
specifier|static
name|KerberosTestServices
name|kerberosTestServices
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"Hadoop does not work on Windows"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"FIXME: SOLR-8182: This test fails under Java 9"
argument_list|,
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA9
argument_list|)
expr_stmt|;
name|setupMiniKdc
argument_list|()
expr_stmt|;
name|configureCluster
argument_list|(
name|NUM_SERVERS
argument_list|)
comment|// nodes
operator|.
name|withSecurityJson
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"security"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"hadoop_kerberos_config.json"
argument_list|)
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf1"
argument_list|,
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"cloud-minimal"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownClass
specifier|public
specifier|static
name|void
name|tearDownClass
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
literal|"solr.kerberos.principal"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.keytab"
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
if|if
condition|(
name|kerberosTestServices
operator|!=
literal|null
condition|)
block|{
name|kerberosTestServices
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|kerberosTestServices
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setupMiniKdc
specifier|private
specifier|static
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
name|String
name|solrClientPrincipal
init|=
literal|"solr"
decl_stmt|;
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
name|kerberosTestServices
operator|=
name|KerberosTestServices
operator|.
name|builder
argument_list|()
operator|.
name|withKdc
argument_list|(
operator|new
name|File
argument_list|(
name|kdcDir
argument_list|)
argument_list|)
operator|.
name|withJaasConfiguration
argument_list|(
name|solrClientPrincipal
argument_list|,
name|keytabFile
argument_list|,
literal|"SolrClient"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|solrServerPrincipal
init|=
literal|"HTTP/127.0.0.1"
decl_stmt|;
name|kerberosTestServices
operator|.
name|start
argument_list|()
expr_stmt|;
name|kerberosTestServices
operator|.
name|getKdc
argument_list|()
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
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
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
name|CloudSolrClient
name|solrClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|String
name|collectionName
init|=
literal|"testkerberoscollection"
decl_stmt|;
comment|// create collection
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf1"
argument_list|,
name|NUM_SHARDS
argument_list|,
name|REPLICATION_FACTOR
argument_list|)
decl_stmt|;
name|create
operator|.
name|process
argument_list|(
name|solrClient
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
name|solrClient
operator|.
name|add
argument_list|(
name|collectionName
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|commit
argument_list|(
name|collectionName
argument_list|)
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
name|solrClient
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
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
name|CollectionAdminRequest
operator|.
name|Delete
name|deleteReq
init|=
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|deleteReq
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForCollectionToDisappear
argument_list|(
name|collectionName
argument_list|,
name|solrClient
operator|.
name|getZkStateReader
argument_list|()
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
end_class

end_unit
