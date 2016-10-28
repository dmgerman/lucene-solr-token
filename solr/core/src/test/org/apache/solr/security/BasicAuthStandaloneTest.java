begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
package|;
end_package

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
name|Charset
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
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
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
name|client
operator|.
name|HttpClient
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
name|client
operator|.
name|methods
operator|.
name|HttpPost
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
name|entity
operator|.
name|ByteArrayEntity
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
name|message
operator|.
name|AbstractHttpMessage
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
name|message
operator|.
name|BasicHeader
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|GenericSolrRequest
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
name|ModifiableSolrParams
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
name|Base64
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
name|ContentStreamBase
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
name|handler
operator|.
name|admin
operator|.
name|SecurityConfHandler
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
name|handler
operator|.
name|admin
operator|.
name|SecurityConfHandlerLocalForTesting
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
name|AbstractSolrTestCase
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|security
operator|.
name|BasicAuthIntegrationTest
operator|.
name|NOT_NULL_PREDICATE
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
name|security
operator|.
name|BasicAuthIntegrationTest
operator|.
name|STD_CONF
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
name|security
operator|.
name|BasicAuthIntegrationTest
operator|.
name|verifySecurityStatus
import|;
end_import

begin_class
DECL|class|BasicAuthStandaloneTest
specifier|public
class|class
name|BasicAuthStandaloneTest
extends|extends
name|AbstractSolrTestCase
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
DECL|field|ROOT_DIR
specifier|private
name|Path
name|ROOT_DIR
init|=
name|Paths
operator|.
name|get
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|CONF_DIR
specifier|private
name|Path
name|CONF_DIR
init|=
name|ROOT_DIR
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"configset-2"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
DECL|field|securityConfHandler
name|SecurityConfHandlerLocalForTesting
name|securityConfHandler
decl_stmt|;
DECL|field|instance
name|SolrInstance
name|instance
init|=
literal|null
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
annotation|@
name|Before
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|instance
operator|=
operator|new
name|SolrInstance
argument_list|(
literal|"inst"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|jetty
operator|=
name|createJetty
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|securityConfHandler
operator|=
operator|new
name|SecurityConfHandlerLocalForTesting
argument_list|(
name|jetty
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicAuth
specifier|public
name|void
name|testBasicAuth
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|authcPrefix
init|=
literal|"/admin/authentication"
decl_stmt|;
name|HttpClient
name|cl
init|=
literal|null
decl_stmt|;
name|HttpSolrClient
name|httpSolrClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cl
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|String
name|baseUrl
init|=
name|buildUrl
argument_list|(
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
literal|"/solr"
argument_list|)
decl_stmt|;
name|httpSolrClient
operator|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"/errorMessages"
argument_list|,
literal|null
argument_list|,
literal|20
argument_list|)
expr_stmt|;
comment|// Write security.json locally. Should cause security to be initialized
name|securityConfHandler
operator|.
name|persistConf
argument_list|(
operator|new
name|SecurityConfHandler
operator|.
name|SecurityConfig
argument_list|()
operator|.
name|setData
argument_list|(
name|Utils
operator|.
name|fromJSONString
argument_list|(
name|STD_CONF
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"\""
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|securityConfHandler
operator|.
name|securityConfEdited
argument_list|()
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication/class"
argument_list|,
literal|"solr.BasicAuthPlugin"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|String
name|command
init|=
literal|"{\n"
operator|+
literal|"'set-user': {'harry':'HarryIsCool'}\n"
operator|+
literal|"}"
decl_stmt|;
name|GenericSolrRequest
name|genericReq
init|=
operator|new
name|GenericSolrRequest
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
name|authcPrefix
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|genericReq
operator|.
name|setContentStreams
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|ByteArrayStream
argument_list|(
name|command
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|HttpSolrClient
name|finalHttpSolrClient
init|=
name|httpSolrClient
decl_stmt|;
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|exp
init|=
name|expectThrows
argument_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|finalHttpSolrClient
operator|.
name|request
argument_list|(
name|genericReq
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|401
argument_list|,
name|exp
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|=
literal|"{\n"
operator|+
literal|"'set-user': {'harry':'HarryIsUberCool'}\n"
operator|+
literal|"}"
expr_stmt|;
name|HttpPost
name|httpPost
init|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
name|authcPrefix
argument_list|)
decl_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
literal|"solr"
argument_list|,
literal|"SolrRocks"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|command
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json; charset=UTF-8"
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication.enabled"
argument_list|,
literal|"true"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|HttpResponse
name|r
init|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
decl_stmt|;
name|int
name|statusCode
init|=
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|Utils
operator|.
name|consumeFully
argument_list|(
name|r
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"proper_cred sent, but access denied"
argument_list|,
literal|200
argument_list|,
name|statusCode
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication/credentials/harry"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
comment|// Read file from SOLR_HOME and verify that it contains our new user
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|securityConfHandler
operator|.
name|getSecurityConfig
argument_list|(
literal|false
argument_list|)
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"harry"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cl
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|close
argument_list|(
name|cl
argument_list|)
expr_stmt|;
name|httpSolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|setBasicAuthHeader
specifier|public
specifier|static
name|void
name|setBasicAuthHeader
parameter_list|(
name|AbstractHttpMessage
name|httpMsg
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pwd
parameter_list|)
block|{
name|String
name|userPass
init|=
name|user
operator|+
literal|":"
operator|+
name|pwd
decl_stmt|;
name|String
name|encoded
init|=
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|userPass
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|httpMsg
operator|.
name|setHeader
argument_list|(
operator|new
name|BasicHeader
argument_list|(
literal|"Authorization"
argument_list|,
literal|"Basic "
operator|+
name|encoded
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Added Basic Auth security Header {}"
argument_list|,
name|encoded
argument_list|)
expr_stmt|;
block|}
DECL|method|createJetty
specifier|private
name|JettySolrRunner
name|createJetty
parameter_list|(
name|SolrInstance
name|instance
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|nodeProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|nodeProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|instance
operator|.
name|getDataDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|nodeProperties
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|class|SolrInstance
specifier|private
class|class
name|SolrInstance
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|port
name|Integer
name|port
decl_stmt|;
DECL|field|homeDir
name|Path
name|homeDir
decl_stmt|;
DECL|field|confDir
name|Path
name|confDir
decl_stmt|;
DECL|field|dataDir
name|Path
name|dataDir
decl_stmt|;
comment|/**      * if masterPort is null, this instance is a master -- otherwise this instance is a slave, and assumes the master is      * on localhost at the specified port.      */
DECL|method|SolrInstance
specifier|public
name|SolrInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|Integer
name|port
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
name|port
operator|=
name|port
expr_stmt|;
block|}
DECL|method|getHomeDir
specifier|public
name|Path
name|getHomeDir
parameter_list|()
block|{
return|return
name|homeDir
return|;
block|}
DECL|method|getSchemaFile
specifier|public
name|Path
name|getSchemaFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|.
name|resolve
argument_list|(
literal|"schema.xml"
argument_list|)
return|;
block|}
DECL|method|getConfDir
specifier|public
name|Path
name|getConfDir
parameter_list|()
block|{
return|return
name|confDir
return|;
block|}
DECL|method|getDataDir
specifier|public
name|Path
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|Path
name|getSolrConfigFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|.
name|resolve
argument_list|(
literal|"solrconfig.xml"
argument_list|)
return|;
block|}
DECL|method|getSolrXmlFile
specifier|public
name|Path
name|getSolrXmlFile
parameter_list|()
block|{
return|return
name|ROOT_DIR
operator|.
name|resolve
argument_list|(
literal|"solr.xml"
argument_list|)
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|homeDir
operator|=
name|createTempDir
argument_list|(
name|name
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
expr_stmt|;
name|dataDir
operator|=
name|homeDir
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
name|homeDir
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|confDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|getSolrXmlFile
argument_list|()
argument_list|,
name|homeDir
operator|.
name|resolve
argument_list|(
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|,
name|confDir
operator|.
name|resolve
argument_list|(
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|,
name|confDir
operator|.
name|resolve
argument_list|(
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|homeDir
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"core.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

