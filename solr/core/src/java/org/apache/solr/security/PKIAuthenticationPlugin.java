begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|HttpServletRequestWrapper
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
name|nio
operator|.
name|ByteBuffer
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
name|security
operator|.
name|InvalidKeyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|auth
operator|.
name|BasicUserPrincipal
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
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
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
name|impl
operator|.
name|HttpClientConfigurer
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
name|ExecutorUtil
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
name|SuppressForbidden
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
name|core
operator|.
name|CoreContainer
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
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrRequestHandler
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
name|request
operator|.
name|SolrRequestInfo
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
name|response
operator|.
name|SolrQueryResponse
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
name|CryptoKeys
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
DECL|class|PKIAuthenticationPlugin
specifier|public
class|class
name|PKIAuthenticationPlugin
extends|extends
name|AuthenticationPlugin
implements|implements
name|HttpClientInterceptorPlugin
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PKIAuthenticationPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|keyCache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PublicKey
argument_list|>
name|keyCache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|keyPair
specifier|private
name|CryptoKeys
operator|.
name|RSAKeyPair
name|keyPair
init|=
operator|new
name|CryptoKeys
operator|.
name|RSAKeyPair
argument_list|()
decl_stmt|;
DECL|field|cores
specifier|private
specifier|final
name|CoreContainer
name|cores
decl_stmt|;
DECL|field|maxValidity
specifier|private
name|int
name|maxValidity
init|=
literal|5000
decl_stmt|;
DECL|field|myNodeName
specifier|private
specifier|final
name|String
name|myNodeName
decl_stmt|;
DECL|field|interceptorRegistered
specifier|private
name|boolean
name|interceptorRegistered
init|=
literal|false
decl_stmt|;
DECL|method|setInterceptorRegistered
specifier|public
name|void
name|setInterceptorRegistered
parameter_list|()
block|{
name|this
operator|.
name|interceptorRegistered
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|isInterceptorRegistered
specifier|public
name|boolean
name|isInterceptorRegistered
parameter_list|()
block|{
return|return
name|interceptorRegistered
return|;
block|}
DECL|method|PKIAuthenticationPlugin
specifier|public
name|PKIAuthenticationPlugin
parameter_list|(
name|CoreContainer
name|cores
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|this
operator|.
name|cores
operator|=
name|cores
expr_stmt|;
name|myNodeName
operator|=
name|nodeName
expr_stmt|;
block|}
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
block|{   }
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Needs currentTimeMillis to compare against time in header"
argument_list|)
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
name|String
name|requestURI
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestURI
operator|.
name|endsWith
argument_list|(
name|PATH
argument_list|)
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
name|long
name|receivedTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|header
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getHeader
argument_list|(
name|HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No SolrAuth header present"
argument_list|)
expr_stmt|;
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
name|List
argument_list|<
name|String
argument_list|>
name|authInfo
init|=
name|StrUtils
operator|.
name|splitWS
argument_list|(
name|header
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|authInfo
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid SolrAuth Header"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|nodeName
init|=
name|authInfo
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|cipher
init|=
name|authInfo
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|decipher
init|=
name|decipherData
argument_list|(
name|nodeName
argument_list|,
name|cipher
argument_list|)
decl_stmt|;
if|if
condition|(
name|decipher
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|decipher
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pcs
init|=
name|StrUtils
operator|.
name|splitWS
argument_list|(
name|s
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|pcs
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|userName
init|=
name|pcs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|timeStr
init|=
name|pcs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|timeMillis
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|timeStr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|receivedTime
operator|-
name|timeMillis
operator|)
operator|>
name|maxValidity
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid key "
argument_list|)
expr_stmt|;
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
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid time "
operator|+
name|timeStr
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Principal
name|principal
init|=
literal|"$"
operator|.
name|equals
argument_list|(
name|userName
argument_list|)
condition|?
name|SU
else|:
operator|new
name|BasicUserPrincipal
argument_list|(
name|userName
argument_list|)
decl_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|getWrapper
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|request
argument_list|,
name|principal
argument_list|)
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|getWrapper
specifier|private
specifier|static
name|HttpServletRequestWrapper
name|getWrapper
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|Principal
name|principal
parameter_list|)
block|{
return|return
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|request
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
block|}
return|;
block|}
DECL|method|decipherData
specifier|private
name|byte
index|[]
name|decipherData
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|String
name|cipherBase64
parameter_list|)
block|{
name|boolean
name|freshKey
init|=
literal|false
decl_stmt|;
name|PublicKey
name|key
init|=
name|keyCache
operator|.
name|get
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|getRemotePublicKey
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|freshKey
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
return|return
name|CryptoKeys
operator|.
name|decryptRSA
argument_list|(
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|cipherBase64
argument_list|)
argument_list|,
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidKeyException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|freshKey
condition|)
block|{
name|key
operator|=
name|getRemotePublicKey
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|CryptoKeys
operator|.
name|decryptRSA
argument_list|(
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|cipherBase64
argument_list|)
argument_list|,
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error decrypting"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
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
literal|"Error decrypting"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getRemotePublicKey
name|PublicKey
name|getRemotePublicKey
parameter_list|(
name|String
name|nodename
parameter_list|)
block|{
name|String
name|url
init|=
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|nodename
argument_list|)
decl_stmt|;
try|try
block|{
name|HttpResponse
name|rsp
init|=
name|cores
operator|.
name|getUpdateShardHandler
argument_list|()
operator|.
name|getHttpClient
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|HttpGet
argument_list|(
name|url
operator|+
name|PATH
operator|+
literal|"?wt=json&omitHeader=true"
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|EntityUtils
operator|.
name|toByteArray
argument_list|(
name|rsp
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
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
name|bytes
argument_list|)
decl_stmt|;
name|String
name|key
init|=
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No key available from "
operator|+
name|url
operator|+
name|PATH
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|PublicKey
name|pubKey
init|=
name|CryptoKeys
operator|.
name|deserializeX509PublicKey
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|keyCache
operator|.
name|put
argument_list|(
name|nodename
argument_list|,
name|pubKey
argument_list|)
expr_stmt|;
return|return
name|pubKey
return|;
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
literal|"Exception trying to get public key from : "
operator|+
name|url
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|field|clientConfigurer
specifier|private
name|HttpHeaderClientConfigurer
name|clientConfigurer
init|=
operator|new
name|HttpHeaderClientConfigurer
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getClientConfigurer
specifier|public
name|HttpClientConfigurer
name|getClientConfigurer
parameter_list|()
block|{
return|return
name|clientConfigurer
return|;
block|}
DECL|method|getRequestHandler
specifier|public
name|SolrRequestHandler
name|getRequestHandler
parameter_list|()
block|{
return|return
operator|new
name|RequestHandlerBase
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"key"
argument_list|,
name|keyPair
operator|.
name|getPublicKeyStr
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Return the public key of this server"
return|;
block|}
block|}
return|;
block|}
DECL|method|needsAuthorization
specifier|public
name|boolean
name|needsAuthorization
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
if|if
condition|(
name|req
operator|.
name|getUserPrincipal
argument_list|()
operator|==
name|SU
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|class|HttpHeaderClientConfigurer
specifier|private
class|class
name|HttpHeaderClientConfigurer
extends|extends
name|HttpClientConfigurer
implements|implements
name|HttpRequestInterceptor
block|{
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|SolrParams
name|config
parameter_list|)
block|{
name|super
operator|.
name|configure
argument_list|(
name|httpClient
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|addRequestInterceptor
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|httpRequest
parameter_list|,
name|HttpContext
name|httpContext
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
if|if
condition|(
name|disabled
argument_list|()
condition|)
return|return;
name|setHeader
argument_list|(
name|httpRequest
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Needs currentTimeMillis to set current time in header"
argument_list|)
DECL|method|setHeader
name|void
name|setHeader
parameter_list|(
name|HttpRequest
name|httpRequest
parameter_list|)
block|{
name|SolrRequestInfo
name|reqInfo
init|=
name|getRequestInfo
argument_list|()
decl_stmt|;
name|String
name|usr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|reqInfo
operator|!=
literal|null
condition|)
block|{
name|Principal
name|principal
init|=
name|reqInfo
operator|.
name|getReq
argument_list|()
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
block|{
comment|//this had a request but not authenticated
comment|//so we don't not need to set a principal
return|return;
block|}
else|else
block|{
name|usr
operator|=
name|principal
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|isSolrThread
argument_list|()
condition|)
block|{
comment|//if this is not running inside a Solr threadpool (as in testcases)
comment|// then no need to add any header
return|return;
block|}
comment|//this request seems to be originated from Solr itself
name|usr
operator|=
literal|"$"
expr_stmt|;
comment|//special name to denote the user is the node itself
block|}
name|String
name|s
init|=
name|usr
operator|+
literal|" "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|byte
index|[]
name|payload
init|=
name|s
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|payloadCipher
init|=
name|keyPair
operator|.
name|encrypt
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|base64Cipher
init|=
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|payloadCipher
argument_list|)
decl_stmt|;
name|httpRequest
operator|.
name|setHeader
argument_list|(
name|HEADER
argument_list|,
name|myNodeName
operator|+
literal|" "
operator|+
name|base64Cipher
argument_list|)
expr_stmt|;
block|}
DECL|method|isSolrThread
name|boolean
name|isSolrThread
parameter_list|()
block|{
return|return
name|ExecutorUtil
operator|.
name|isSolrServerThread
argument_list|()
return|;
block|}
DECL|method|getRequestInfo
name|SolrRequestInfo
name|getRequestInfo
parameter_list|()
block|{
return|return
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
return|;
block|}
DECL|method|disabled
name|boolean
name|disabled
parameter_list|()
block|{
return|return
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
operator|==
literal|null
operator|||
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
operator|instanceof
name|HttpClientInterceptorPlugin
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
DECL|method|getPublicKey
specifier|public
name|String
name|getPublicKey
parameter_list|()
block|{
return|return
name|keyPair
operator|.
name|getPublicKeyStr
argument_list|()
return|;
block|}
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"SolrAuth"
decl_stmt|;
DECL|field|PATH
specifier|public
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|"/admin/info/key"
decl_stmt|;
DECL|field|NODE_IS_USER
specifier|public
specifier|static
specifier|final
name|String
name|NODE_IS_USER
init|=
literal|"$"
decl_stmt|;
comment|// special principal to denote the cluster member
DECL|field|SU
specifier|private
specifier|static
specifier|final
name|Principal
name|SU
init|=
operator|new
name|BasicUserPrincipal
argument_list|(
literal|"$"
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

