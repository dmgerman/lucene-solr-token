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
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|ArrayList
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
name|Objects
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|HttpGet
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
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|NamedList
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
name|StrUtils
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
name|SolrCLI
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_class
DECL|class|BasicAuthIntegrationTest
specifier|public
class|class
name|BasicAuthIntegrationTest
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
DECL|field|COLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION
init|=
literal|"authCollection"
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
literal|3
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
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|COLLECTION
argument_list|,
literal|"conf"
argument_list|,
literal|3
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
name|String
name|authzPrefix
init|=
literal|"/admin/authorization"
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
decl_stmt|;
name|HttpClient
name|cl
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
name|JettySolrRunner
name|randomJetty
init|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|baseUrl
init|=
name|randomJetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|zkClient
argument_list|()
operator|.
name|setData
argument_list|(
literal|"/security.json"
argument_list|,
name|STD_CONF
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"\""
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|true
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
literal|"authentication/class"
argument_list|,
literal|"solr.BasicAuthPlugin"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|randomJetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|randomJetty
operator|.
name|start
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|baseUrl
operator|=
name|randomJetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
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
name|cluster
operator|.
name|getSolrClient
argument_list|()
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
name|baseUrl
operator|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
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
literal|"authentication/credentials/harry"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|command
operator|=
literal|"{\n"
operator|+
literal|"'set-user-role': {'harry':'admin'}\n"
operator|+
literal|"}"
expr_stmt|;
name|executeCommand
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
name|cl
argument_list|,
name|command
argument_list|,
literal|"solr"
argument_list|,
literal|"SolrRocks"
argument_list|)
expr_stmt|;
name|baseUrl
operator|=
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/user-role/harry"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|executeCommand
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
name|cl
argument_list|,
name|Utils
operator|.
name|toJSONString
argument_list|(
name|singletonMap
argument_list|(
literal|"set-permission"
argument_list|,
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
literal|"x"
argument_list|,
literal|"path"
argument_list|,
literal|"/update/*"
argument_list|,
literal|"role"
argument_list|,
literal|"dev"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/permissions[1]/collection"
argument_list|,
literal|"x"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|executeCommand
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
name|cl
argument_list|,
name|Utils
operator|.
name|toJSONString
argument_list|(
name|singletonMap
argument_list|(
literal|"set-permission"
argument_list|,
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"name"
argument_list|,
literal|"collection-admin-edit"
argument_list|,
literal|"role"
argument_list|,
literal|"admin"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/permissions[2]/name"
argument_list|,
literal|"collection-admin-edit"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Reload
name|reload
init|=
name|CollectionAdminRequest
operator|.
name|reloadCollection
argument_list|(
name|COLLECTION
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
init|)
block|{
try|try
block|{
name|rsp
operator|=
name|solrClient
operator|.
name|request
argument_list|(
name|reload
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{          }
name|reload
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
try|try
block|{
name|rsp
operator|=
name|solrClient
operator|.
name|request
argument_list|(
name|reload
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{          }
block|}
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|CollectionAdminRequest
operator|.
name|reloadCollection
argument_list|(
name|COLLECTION
argument_list|)
operator|.
name|setBasicAuthCredentials
argument_list|(
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|CollectionAdminRequest
operator|.
name|reloadCollection
argument_list|(
name|COLLECTION
argument_list|)
operator|.
name|setBasicAuthCredentials
argument_list|(
literal|"harry"
argument_list|,
literal|"Cool12345"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This should not succeed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{        }
name|executeCommand
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
name|cl
argument_list|,
literal|"{set-permission : { name : update , role : admin}}"
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
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
literal|"4"
argument_list|)
expr_stmt|;
name|UpdateRequest
name|update
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|update
operator|.
name|setBasicAuthCredentials
argument_list|(
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|update
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|update
operator|.
name|setCommitWithin
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|update
argument_list|,
name|COLLECTION
argument_list|)
expr_stmt|;
name|executeCommand
argument_list|(
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
name|cl
argument_list|,
literal|"{set-property : { blockUnknown: true}}"
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
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
literal|"authentication/blockUnknown"
argument_list|,
literal|"true"
argument_list|,
literal|20
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
literal|"/admin/info/key?wt=json"
argument_list|,
literal|"key"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|String
index|[]
name|toolArgs
init|=
operator|new
name|String
index|[]
block|{
literal|"status"
block|,
literal|"-solr"
block|,
name|baseUrl
block|}
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|stdoutSim
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|,
literal|true
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|SolrCLI
operator|.
name|StatusTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|StatusTool
argument_list|(
name|stdoutSim
argument_list|)
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"basicauth"
argument_list|,
literal|"harry:HarryIsUberCool"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|runTool
argument_list|(
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|toolArgs
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|obj
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|.
name|containsKey
argument_list|(
literal|"version"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|.
name|containsKey
argument_list|(
literal|"startTime"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|.
name|containsKey
argument_list|(
literal|"uptime"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|obj
operator|.
name|containsKey
argument_list|(
literal|"memory"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"RunExampleTool failed due to: "
operator|+
name|e
operator|+
literal|"; stdout from tool prior to failure: "
operator|+
name|baos
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|executeCommand
argument_list|(
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
name|cl
argument_list|,
literal|"{set-property : { blockUnknown: false}}"
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
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
block|}
block|}
block|}
DECL|method|executeCommand
specifier|public
specifier|static
name|void
name|executeCommand
parameter_list|(
name|String
name|url
parameter_list|,
name|HttpClient
name|cl
parameter_list|,
name|String
name|payload
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpPost
name|httpPost
decl_stmt|;
name|HttpResponse
name|r
decl_stmt|;
name|httpPost
operator|=
operator|new
name|HttpPost
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
name|user
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|payload
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
name|r
operator|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
DECL|method|verifySecurityStatus
specifier|public
specifier|static
name|void
name|verifySecurityStatus
parameter_list|(
name|HttpClient
name|cl
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|objPath
parameter_list|,
name|Object
name|expected
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|url
argument_list|,
name|objPath
argument_list|,
name|expected
argument_list|,
name|count
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySecurityStatus
specifier|public
specifier|static
name|void
name|verifySecurityStatus
parameter_list|(
name|HttpClient
name|cl
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|objPath
parameter_list|,
name|Object
name|expected
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pwd
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|String
name|s
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|objPath
argument_list|,
literal|'/'
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|HttpGet
name|get
init|=
operator|new
name|HttpGet
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
name|setBasicAuthHeader
argument_list|(
name|get
argument_list|,
name|user
argument_list|,
name|pwd
argument_list|)
expr_stmt|;
name|HttpResponse
name|rsp
init|=
name|cl
operator|.
name|execute
argument_list|(
name|get
argument_list|)
decl_stmt|;
name|s
operator|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|rsp
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
literal|null
decl_stmt|;
try|try
block|{
name|m
operator|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSONString
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Invalid json "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
name|Utils
operator|.
name|consumeFully
argument_list|(
name|rsp
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|actual
init|=
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|hierarchy
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|instanceof
name|Predicate
condition|)
block|{
name|Predicate
name|predicate
init|=
operator|(
name|Predicate
operator|)
name|expected
decl_stmt|;
if|if
condition|(
name|predicate
operator|.
name|test
argument_list|(
name|actual
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|actual
operator|==
literal|null
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|actual
argument_list|)
argument_list|,
name|expected
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"No match for "
operator|+
name|objPath
operator|+
literal|" = "
operator|+
name|expected
operator|+
literal|", full response = "
operator|+
name|s
argument_list|,
name|success
argument_list|)
expr_stmt|;
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
DECL|method|getRandomReplica
specifier|public
specifier|static
name|Replica
name|getRandomReplica
parameter_list|(
name|DocCollection
name|coll
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Replica
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|coll
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|,
name|random
argument_list|)
expr_stmt|;
return|return
name|l
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|field|NOT_NULL_PREDICATE
specifier|protected
specifier|static
specifier|final
name|Predicate
name|NOT_NULL_PREDICATE
init|=
name|o
lambda|->
name|o
operator|!=
literal|null
decl_stmt|;
comment|//the password is 'SolrRocks'
comment|//this could be generated everytime. But , then we will not know if there is any regression
DECL|field|STD_CONF
specifier|protected
specifier|static
specifier|final
name|String
name|STD_CONF
init|=
literal|"{\n"
operator|+
literal|"  'authentication':{\n"
operator|+
literal|"    'class':'solr.BasicAuthPlugin',\n"
operator|+
literal|"    'credentials':{'solr':'orwp2Ghgj39lmnrZOTm7Qtre1VqHFDfwAEzr0ApbN3Y= Ju5osoAqOX8iafhWpPP01E5P+sg8tK8tHON7rCYZRRw='}},\n"
operator|+
literal|"  'authorization':{\n"
operator|+
literal|"    'class':'solr.RuleBasedAuthorizationPlugin',\n"
operator|+
literal|"    'user-role':{'solr':'admin'},\n"
operator|+
literal|"    'permissions':[{'name':'security-edit','role':'admin'}]}}"
decl_stmt|;
block|}
end_class

end_unit

