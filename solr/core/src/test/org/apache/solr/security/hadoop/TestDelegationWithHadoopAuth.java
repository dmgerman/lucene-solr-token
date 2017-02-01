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
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|PseudoAuthenticator
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
name|util
operator|.
name|Time
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
name|HttpStatus
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
name|SolrClient
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
name|impl
operator|.
name|LBHttpSolrClient
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
name|DelegationTokenRequest
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
name|DelegationTokenResponse
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
name|SolrException
operator|.
name|ErrorCode
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

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|TestDelegationWithHadoopAuth
specifier|public
class|class
name|TestDelegationWithHadoopAuth
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
literal|2
decl_stmt|;
DECL|field|USER_1
specifier|protected
specifier|static
specifier|final
name|String
name|USER_1
init|=
literal|"foo"
decl_stmt|;
DECL|field|USER_2
specifier|protected
specifier|static
specifier|final
name|String
name|USER_2
init|=
literal|"bar"
decl_stmt|;
DECL|field|primarySolrClient
DECL|field|secondarySolrClient
specifier|private
specifier|static
name|HttpSolrClient
name|primarySolrClient
decl_stmt|,
name|secondarySolrClient
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
literal|"hadoop_simple_auth_with_delegation.json"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|JettySolrRunner
name|runnerPrimary
init|=
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|primarySolrClient
operator|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|runnerPrimary
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|JettySolrRunner
name|runnerSecondary
init|=
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|secondarySolrClient
operator|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|runnerSecondary
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
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
if|if
condition|(
name|primarySolrClient
operator|!=
literal|null
condition|)
block|{
name|primarySolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|primarySolrClient
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|secondarySolrClient
operator|!=
literal|null
condition|)
block|{
name|secondarySolrClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|secondarySolrClient
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getDelegationToken
specifier|private
name|String
name|getDelegationToken
parameter_list|(
specifier|final
name|String
name|renewer
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
name|HttpSolrClient
name|solrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|DelegationTokenRequest
operator|.
name|Get
name|get
init|=
operator|new
name|DelegationTokenRequest
operator|.
name|Get
argument_list|(
name|renewer
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|super
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
argument_list|,
name|user
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
decl_stmt|;
name|DelegationTokenResponse
operator|.
name|Get
name|getResponse
init|=
name|get
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
decl_stmt|;
return|return
name|getResponse
operator|.
name|getDelegationToken
argument_list|()
return|;
block|}
DECL|method|renewDelegationToken
specifier|private
name|long
name|renewDelegationToken
parameter_list|(
specifier|final
name|String
name|token
parameter_list|,
specifier|final
name|int
name|expectedStatusCode
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|DelegationTokenRequest
operator|.
name|Renew
name|renew
init|=
operator|new
name|DelegationTokenRequest
operator|.
name|Renew
argument_list|(
name|token
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|super
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
argument_list|,
name|user
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getQueryParams
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
init|=
name|super
operator|.
name|getQueryParams
argument_list|()
decl_stmt|;
name|queryParams
operator|.
name|add
argument_list|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
argument_list|)
expr_stmt|;
return|return
name|queryParams
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|DelegationTokenResponse
operator|.
name|Renew
name|renewResponse
init|=
name|renew
operator|.
name|process
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|expectedStatusCode
argument_list|)
expr_stmt|;
return|return
name|renewResponse
operator|.
name|getExpirationTime
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedStatusCode
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|cancelDelegationToken
specifier|private
name|void
name|cancelDelegationToken
parameter_list|(
name|String
name|token
parameter_list|,
name|int
name|expectedStatusCode
parameter_list|,
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|DelegationTokenRequest
operator|.
name|Cancel
name|cancel
init|=
operator|new
name|DelegationTokenRequest
operator|.
name|Cancel
argument_list|(
name|token
argument_list|)
decl_stmt|;
try|try
block|{
name|cancel
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|expectedStatusCode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedStatusCode
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSolrRequest
specifier|private
name|void
name|doSolrRequest
parameter_list|(
name|String
name|token
parameter_list|,
name|int
name|expectedStatusCode
parameter_list|,
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|doSolrRequest
argument_list|(
name|token
argument_list|,
name|expectedStatusCode
argument_list|,
name|client
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|doSolrRequest
specifier|private
name|void
name|doSolrRequest
parameter_list|(
name|String
name|token
parameter_list|,
name|int
name|expectedStatusCode
parameter_list|,
name|HttpSolrClient
name|client
parameter_list|,
name|int
name|trials
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|lastStatusCode
init|=
literal|0
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
name|trials
condition|;
operator|++
name|i
control|)
block|{
name|lastStatusCode
operator|=
name|getStatusCode
argument_list|(
name|token
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastStatusCode
operator|==
name|expectedStatusCode
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Did not receieve excepted status code"
argument_list|,
name|expectedStatusCode
argument_list|,
name|lastStatusCode
argument_list|)
expr_stmt|;
block|}
DECL|method|getAdminRequest
specifier|private
name|SolrRequest
name|getAdminRequest
parameter_list|(
specifier|final
name|SolrParams
name|params
parameter_list|)
block|{
return|return
operator|new
name|CollectionAdminRequest
operator|.
name|List
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|p
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|super
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
name|params
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
return|;
block|}
DECL|method|getStatusCode
specifier|private
name|int
name|getStatusCode
parameter_list|(
name|String
name|token
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|op
parameter_list|,
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrClient
name|delegationTokenClient
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
name|delegationTokenClient
operator|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withKerberosDelegationToken
argument_list|(
name|token
argument_list|)
operator|.
name|withResponseParser
argument_list|(
name|client
operator|.
name|getParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
else|else
name|delegationTokenClient
operator|=
operator|new
name|CloudSolrClient
operator|.
name|Builder
argument_list|()
operator|.
name|withZkHost
argument_list|(
operator|(
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
operator|)
argument_list|)
operator|.
name|withLBHttpSolrClientBuilder
argument_list|(
operator|new
name|LBHttpSolrClient
operator|.
name|Builder
argument_list|()
operator|.
name|withResponseParser
argument_list|(
name|client
operator|.
name|getParser
argument_list|()
argument_list|)
operator|.
name|withHttpSolrClientBuilder
argument_list|(
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|()
operator|.
name|withKerberosDelegationToken
argument_list|(
name|token
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|ModifiableSolrParams
name|p
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
name|p
operator|.
name|set
argument_list|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
argument_list|,
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
name|p
operator|.
name|set
argument_list|(
literal|"op"
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|SolrRequest
name|req
init|=
name|getAdminRequest
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
operator|||
name|op
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
name|queryParams
operator|.
name|add
argument_list|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
name|queryParams
operator|.
name|add
argument_list|(
literal|"op"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setQueryParams
argument_list|(
name|queryParams
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|delegationTokenClient
operator|.
name|request
argument_list|(
name|req
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|HttpStatus
operator|.
name|SC_OK
return|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|re
parameter_list|)
block|{
return|return
name|re
operator|.
name|code
argument_list|()
return|;
block|}
block|}
finally|finally
block|{
name|delegationTokenClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doSolrRequest
specifier|private
name|void
name|doSolrRequest
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|SolrRequest
name|request
parameter_list|,
name|int
name|expectedStatusCode
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|expectedStatusCode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedStatusCode
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyTokenValid
specifier|private
name|void
name|verifyTokenValid
parameter_list|(
name|String
name|token
parameter_list|)
throws|throws
name|Exception
block|{
comment|// pass with token
name|doSolrRequest
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
comment|// fail without token
name|doSolrRequest
argument_list|(
literal|null
argument_list|,
name|ErrorCode
operator|.
name|UNAUTHORIZED
operator|.
name|code
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
comment|// pass with token on other server
name|doSolrRequest
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
comment|// fail without token on other server
name|doSolrRequest
argument_list|(
literal|null
argument_list|,
name|ErrorCode
operator|.
name|UNAUTHORIZED
operator|.
name|code
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test basic Delegation Token get/verify    */
annotation|@
name|Test
DECL|method|testDelegationTokenVerify
specifier|public
name|void
name|testDelegationTokenVerify
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Get token
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|verifyTokenValid
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTokenCancelled
specifier|private
name|void
name|verifyTokenCancelled
parameter_list|(
name|String
name|token
parameter_list|,
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
comment|// fail with token on both servers.  If cancelToOtherURL is true,
comment|// the request went to other url, so FORBIDDEN should be returned immediately.
comment|// The cancelled token may take awhile to propogate to the standard url (via ZK).
comment|// This is of course the opposite if cancelToOtherURL is false.
name|doSolrRequest
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|,
name|client
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// fail without token on both servers
name|doSolrRequest
argument_list|(
literal|null
argument_list|,
name|ErrorCode
operator|.
name|UNAUTHORIZED
operator|.
name|code
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|doSolrRequest
argument_list|(
literal|null
argument_list|,
name|ErrorCode
operator|.
name|UNAUTHORIZED
operator|.
name|code
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenCancel
specifier|public
name|void
name|testDelegationTokenCancel
parameter_list|()
throws|throws
name|Exception
block|{
block|{
comment|// Get token
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// cancel token, note don't need to be authenticated to cancel (no user specified)
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|verifyTokenCancelled
argument_list|(
name|token
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
block|}
block|{
comment|// cancel token on different server from where we got it
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
name|verifyTokenCancelled
argument_list|(
name|token
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/HADOOP-14044"
argument_list|)
DECL|method|testDelegationTokenCancelFail
specifier|public
name|void
name|testDelegationTokenCancelFail
parameter_list|()
throws|throws
name|Exception
block|{
comment|// cancel a bogus token
name|cancelDelegationToken
argument_list|(
literal|"BOGUS"
argument_list|,
name|ErrorCode
operator|.
name|NOT_FOUND
operator|.
name|code
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
block|{
comment|// cancel twice, first on same server
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|NOT_FOUND
operator|.
name|code
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|NOT_FOUND
operator|.
name|code
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
block|}
block|{
comment|// cancel twice, first on other server
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|NOT_FOUND
operator|.
name|code
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|NOT_FOUND
operator|.
name|code
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyDelegationTokenRenew
specifier|private
name|void
name|verifyDelegationTokenRenew
parameter_list|(
name|String
name|renewer
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|Exception
block|{
block|{
comment|// renew on same server
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
name|renewer
argument_list|,
name|user
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|renewDelegationToken
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|user
argument_list|,
name|primarySolrClient
argument_list|)
operator|>
name|now
argument_list|)
expr_stmt|;
name|verifyTokenValid
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|{
comment|// renew on different server
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
name|renewer
argument_list|,
name|user
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|renewDelegationToken
argument_list|(
name|token
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|user
argument_list|,
name|secondarySolrClient
argument_list|)
operator|>
name|now
argument_list|)
expr_stmt|;
name|verifyTokenValid
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenRenew
specifier|public
name|void
name|testDelegationTokenRenew
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test with specifying renewer
name|verifyDelegationTokenRenew
argument_list|(
name|USER_1
argument_list|,
name|USER_1
argument_list|)
expr_stmt|;
comment|// test without specifying renewer
name|verifyDelegationTokenRenew
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenRenewFail
specifier|public
name|void
name|testDelegationTokenRenewFail
parameter_list|()
throws|throws
name|Exception
block|{
comment|// don't set renewer and try to renew as an a different user
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|renewDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|,
name|USER_2
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|renewDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|,
name|USER_2
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
comment|// set renewer and try to renew as different user
name|token
operator|=
name|getDelegationToken
argument_list|(
literal|"renewUser"
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|renewDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|,
literal|"notRenewUser"
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|renewDelegationToken
argument_list|(
name|token
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|,
literal|"notRenewUser"
argument_list|,
name|secondarySolrClient
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that a non-delegation-token "op" http param is handled correctly    */
annotation|@
name|Test
DECL|method|testDelegationOtherOp
specifier|public
name|void
name|testDelegationOtherOp
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|getStatusCode
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
literal|"someSolrOperation"
argument_list|,
name|primarySolrClient
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testZNodePaths
specifier|public
name|void
name|testZNodePaths
parameter_list|()
throws|throws
name|Exception
block|{
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/security/zkdtsm"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/security/token"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test HttpSolrServer's delegation token support    */
annotation|@
name|Test
DECL|method|testDelegationTokenSolrClient
specifier|public
name|void
name|testDelegationTokenSolrClient
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Get token
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|USER_1
argument_list|,
name|primarySolrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|SolrRequest
name|request
init|=
name|getAdminRequest
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
comment|// test without token
name|HttpSolrClient
name|ss
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|primarySolrClient
operator|.
name|getBaseURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withResponseParser
argument_list|(
name|primarySolrClient
operator|.
name|getParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|doSolrRequest
argument_list|(
name|ss
argument_list|,
name|request
argument_list|,
name|ErrorCode
operator|.
name|UNAUTHORIZED
operator|.
name|code
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|ss
operator|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|primarySolrClient
operator|.
name|getBaseURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withDelegationToken
argument_list|(
name|token
argument_list|)
operator|.
name|withResponseParser
argument_list|(
name|primarySolrClient
operator|.
name|getParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
comment|// test with token via property
name|doSolrRequest
argument_list|(
name|ss
argument_list|,
name|request
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
comment|// test with param -- should throw an exception
name|ModifiableSolrParams
name|tokenParam
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|tokenParam
operator|.
name|set
argument_list|(
literal|"delegation"
argument_list|,
literal|"invalidToken"
argument_list|)
expr_stmt|;
try|try
block|{
name|doSolrRequest
argument_list|(
name|ss
argument_list|,
name|getAdminRequest
argument_list|(
name|tokenParam
argument_list|)
argument_list|,
name|ErrorCode
operator|.
name|FORBIDDEN
operator|.
name|code
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{}
block|}
finally|finally
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

