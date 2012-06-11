begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|InflaterInputStream
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
name|Header
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
name|HeaderElement
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
name|HttpEntity
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
name|HttpResponseInterceptor
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
name|AuthScope
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
name|UsernamePasswordCredentials
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
name|params
operator|.
name|ClientParamBean
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
name|HttpEntityWrapper
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
name|impl
operator|.
name|client
operator|.
name|DefaultHttpRequestRetryHandler
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
name|conn
operator|.
name|tsccm
operator|.
name|ThreadSafeClientConnManager
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
name|params
operator|.
name|HttpConnectionParams
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

begin_comment
comment|/**  * Utility class for creating/configuring httpclient instances.   */
end_comment

begin_class
DECL|class|HttpClientUtil
specifier|public
class|class
name|HttpClientUtil
block|{
comment|// socket timeout measured in ms, closes a socket if read
comment|// takes longer than x ms to complete. throws
comment|// java.net.SocketTimeoutException: Read timed out exception
DECL|field|PROP_SO_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|PROP_SO_TIMEOUT
init|=
literal|"socketTimeout"
decl_stmt|;
comment|// connection timeout measures in ms, closes a socket if connection
comment|// cannot be established within x ms. with a
comment|// java.net.SocketTimeoutException: Connection timed out
DECL|field|PROP_CONNECTION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|PROP_CONNECTION_TIMEOUT
init|=
literal|"connTimeout"
decl_stmt|;
comment|// Maximum connections allowed per host
DECL|field|PROP_MAX_CONNECTIONS_PER_HOST
specifier|public
specifier|static
specifier|final
name|String
name|PROP_MAX_CONNECTIONS_PER_HOST
init|=
literal|"maxConnectionsPerHost"
decl_stmt|;
comment|// Maximum total connections allowed
DECL|field|PROP_MAX_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|String
name|PROP_MAX_CONNECTIONS
init|=
literal|"maxConnections"
decl_stmt|;
comment|// Retry http requests on error
DECL|field|PROP_USE_RETRY
specifier|public
specifier|static
specifier|final
name|String
name|PROP_USE_RETRY
init|=
literal|"retry"
decl_stmt|;
comment|// Allow compression (deflate,gzip) if server supports it
DECL|field|PROP_ALLOW_COMPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|PROP_ALLOW_COMPRESSION
init|=
literal|"allowCompression"
decl_stmt|;
comment|// Follow redirects
DECL|field|PROP_FOLLOW_REDIRECTS
specifier|public
specifier|static
specifier|final
name|String
name|PROP_FOLLOW_REDIRECTS
init|=
literal|"followRedirects"
decl_stmt|;
comment|// Basic auth username
DECL|field|PROP_BASIC_AUTH_USER
specifier|public
specifier|static
specifier|final
name|String
name|PROP_BASIC_AUTH_USER
init|=
literal|"httpBasicAuthUser"
decl_stmt|;
comment|// Basic auth password
DECL|field|PROP_BASIC_AUTH_PASS
specifier|public
specifier|static
specifier|final
name|String
name|PROP_BASIC_AUTH_PASS
init|=
literal|"httpBasicAuthPassword"
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HttpClientUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NO_RETRY
specifier|static
specifier|final
name|DefaultHttpRequestRetryHandler
name|NO_RETRY
init|=
operator|new
name|DefaultHttpRequestRetryHandler
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|method|HttpClientUtil
specifier|private
name|HttpClientUtil
parameter_list|()
block|{}
comment|/**    * Creates new http client by using the provided configuration.    *     * @param params    *          http client configuration, if null a client with default    *          configuration (no additional configuration) is created that uses    *          ThreadSafeClientConnManager.    */
DECL|method|createClient
specifier|public
specifier|static
name|HttpClient
name|createClient
parameter_list|(
specifier|final
name|SolrParams
name|params
parameter_list|)
block|{
specifier|final
name|ModifiableSolrParams
name|config
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Creating new http client, config:"
operator|+
name|config
argument_list|)
expr_stmt|;
specifier|final
name|ThreadSafeClientConnManager
name|mgr
init|=
operator|new
name|ThreadSafeClientConnManager
argument_list|()
decl_stmt|;
specifier|final
name|DefaultHttpClient
name|httpClient
init|=
operator|new
name|DefaultHttpClient
argument_list|(
name|mgr
argument_list|)
decl_stmt|;
name|configureClient
argument_list|(
name|httpClient
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
name|httpClient
return|;
block|}
comment|/**    * Configures {@link DefaultHttpClient}, only sets parameters if they are    * present in config.    */
DECL|method|configureClient
specifier|public
specifier|static
name|void
name|configureClient
parameter_list|(
specifier|final
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|SolrParams
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MAX_CONNECTIONS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setMaxConnections
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|PROP_MAX_CONNECTIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setMaxConnectionsPerHost
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_CONNECTION_TIMEOUT
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setConnectionTimeout
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|PROP_CONNECTION_TIMEOUT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_SO_TIMEOUT
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setSoTimeout
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getInt
argument_list|(
name|PROP_SO_TIMEOUT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_USE_RETRY
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setUseRetry
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getBool
argument_list|(
name|PROP_USE_RETRY
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_FOLLOW_REDIRECTS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setFollowRedirects
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getBool
argument_list|(
name|PROP_FOLLOW_REDIRECTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|basicAuthUser
init|=
name|config
operator|.
name|get
argument_list|(
name|PROP_BASIC_AUTH_USER
argument_list|)
decl_stmt|;
specifier|final
name|String
name|basicAuthPass
init|=
name|config
operator|.
name|get
argument_list|(
name|PROP_BASIC_AUTH_PASS
argument_list|)
decl_stmt|;
name|setBasicAuth
argument_list|(
name|httpClient
argument_list|,
name|basicAuthUser
argument_list|,
name|basicAuthPass
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
name|PROP_ALLOW_COMPRESSION
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|setAllowCompression
argument_list|(
name|httpClient
argument_list|,
name|config
operator|.
name|getBool
argument_list|(
name|PROP_ALLOW_COMPRESSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Control HTTP payload compression.    *     * @param allowCompression    *          true will enable compression (needs support from server), false    *          will disable compression.    */
DECL|method|setAllowCompression
specifier|public
specifier|static
name|void
name|setAllowCompression
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|boolean
name|allowCompression
parameter_list|)
block|{
name|httpClient
operator|.
name|removeRequestInterceptorByClass
argument_list|(
name|UseCompressionRequestInterceptor
operator|.
name|class
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|removeResponseInterceptorByClass
argument_list|(
name|UseCompressionResponseInterceptor
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowCompression
condition|)
block|{
name|httpClient
operator|.
name|addRequestInterceptor
argument_list|(
operator|new
name|UseCompressionRequestInterceptor
argument_list|()
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|addResponseInterceptor
argument_list|(
operator|new
name|UseCompressionResponseInterceptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set http basic auth information. If basicAuthUser or basicAuthPass is null    * the basic auth configuration is cleared. Currently this is not preemtive    * authentication. So it is not currently possible to do a post request while    * using this setting.    */
DECL|method|setBasicAuth
specifier|public
specifier|static
name|void
name|setBasicAuth
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|String
name|basicAuthUser
parameter_list|,
name|String
name|basicAuthPass
parameter_list|)
block|{
if|if
condition|(
name|basicAuthUser
operator|!=
literal|null
operator|&&
name|basicAuthPass
operator|!=
literal|null
condition|)
block|{
name|httpClient
operator|.
name|getCredentialsProvider
argument_list|()
operator|.
name|setCredentials
argument_list|(
name|AuthScope
operator|.
name|ANY
argument_list|,
operator|new
name|UsernamePasswordCredentials
argument_list|(
name|basicAuthUser
argument_list|,
name|basicAuthPass
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpClient
operator|.
name|getCredentialsProvider
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Set max connections allowed per host. This call will only work when    * {@link ThreadSafeClientConnManager} is used.    */
DECL|method|setMaxConnectionsPerHost
specifier|public
specifier|static
name|void
name|setMaxConnectionsPerHost
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
operator|instanceof
name|ThreadSafeClientConnManager
condition|)
block|{
name|ThreadSafeClientConnManager
name|mgr
init|=
operator|(
name|ThreadSafeClientConnManager
operator|)
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|setDefaultMaxPerRoute
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set max total connections allowed. This call will only work when    * {@link ThreadSafeClientConnManager} is used.    */
DECL|method|setMaxConnections
specifier|public
specifier|static
name|void
name|setMaxConnections
parameter_list|(
specifier|final
name|HttpClient
name|httpClient
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
operator|instanceof
name|ThreadSafeClientConnManager
condition|)
block|{
name|ThreadSafeClientConnManager
name|mgr
init|=
operator|(
name|ThreadSafeClientConnManager
operator|)
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|setMaxTotal
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Defines the socket timeout (SO_TIMEOUT) in milliseconds. A timeout value of    * zero is interpreted as an infinite timeout.    *     * @param timeout timeout in milliseconds    */
DECL|method|setSoTimeout
specifier|public
specifier|static
name|void
name|setSoTimeout
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|int
name|timeout
parameter_list|)
block|{
name|HttpConnectionParams
operator|.
name|setSoTimeout
argument_list|(
name|httpClient
operator|.
name|getParams
argument_list|()
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Control retry handler     * @param useRetry when false the client will not try to retry failed requests.    */
DECL|method|setUseRetry
specifier|public
specifier|static
name|void
name|setUseRetry
parameter_list|(
specifier|final
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|boolean
name|useRetry
parameter_list|)
block|{
if|if
condition|(
operator|!
name|useRetry
condition|)
block|{
name|httpClient
operator|.
name|setHttpRequestRetryHandler
argument_list|(
name|NO_RETRY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpClient
operator|.
name|setHttpRequestRetryHandler
argument_list|(
operator|new
name|DefaultHttpRequestRetryHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set connection timeout. A timeout value of zero is interpreted as an    * infinite timeout.    *     * @param timeout    *          connection Timeout in milliseconds    */
DECL|method|setConnectionTimeout
specifier|public
specifier|static
name|void
name|setConnectionTimeout
parameter_list|(
specifier|final
name|HttpClient
name|httpClient
parameter_list|,
name|int
name|timeout
parameter_list|)
block|{
name|HttpConnectionParams
operator|.
name|setConnectionTimeout
argument_list|(
name|httpClient
operator|.
name|getParams
argument_list|()
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set follow redirects.    *    * @param followRedirects  When true the client will follow redirects.    */
DECL|method|setFollowRedirects
specifier|public
specifier|static
name|void
name|setFollowRedirects
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|boolean
name|followRedirects
parameter_list|)
block|{
operator|new
name|ClientParamBean
argument_list|(
name|httpClient
operator|.
name|getParams
argument_list|()
argument_list|)
operator|.
name|setHandleRedirects
argument_list|(
name|followRedirects
argument_list|)
expr_stmt|;
block|}
DECL|class|UseCompressionRequestInterceptor
specifier|private
specifier|static
class|class
name|UseCompressionRequestInterceptor
implements|implements
name|HttpRequestInterceptor
block|{
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpContext
name|context
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|request
operator|.
name|containsHeader
argument_list|(
literal|"Accept-Encoding"
argument_list|)
condition|)
block|{
name|request
operator|.
name|addHeader
argument_list|(
literal|"Accept-Encoding"
argument_list|,
literal|"gzip, deflate"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|UseCompressionResponseInterceptor
specifier|private
specifier|static
class|class
name|UseCompressionResponseInterceptor
implements|implements
name|HttpResponseInterceptor
block|{
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|,
specifier|final
name|HttpContext
name|context
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Header
name|ceheader
init|=
name|entity
operator|.
name|getContentEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|ceheader
operator|!=
literal|null
condition|)
block|{
name|HeaderElement
index|[]
name|codecs
init|=
name|ceheader
operator|.
name|getElements
argument_list|()
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
name|codecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|codecs
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"gzip"
argument_list|)
condition|)
block|{
name|response
operator|.
name|setEntity
argument_list|(
operator|new
name|GzipDecompressingEntity
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|codecs
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"deflate"
argument_list|)
condition|)
block|{
name|response
operator|.
name|setEntity
argument_list|(
operator|new
name|DeflateDecompressingEntity
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
block|}
DECL|class|GzipDecompressingEntity
specifier|private
specifier|static
class|class
name|GzipDecompressingEntity
extends|extends
name|HttpEntityWrapper
block|{
DECL|method|GzipDecompressingEntity
specifier|public
name|GzipDecompressingEntity
parameter_list|(
specifier|final
name|HttpEntity
name|entity
parameter_list|)
block|{
name|super
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
DECL|method|getContent
specifier|public
name|InputStream
name|getContent
parameter_list|()
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
return|return
operator|new
name|GZIPInputStream
argument_list|(
name|wrappedEntity
operator|.
name|getContent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getContentLength
specifier|public
name|long
name|getContentLength
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|class|DeflateDecompressingEntity
specifier|private
specifier|static
class|class
name|DeflateDecompressingEntity
extends|extends
name|GzipDecompressingEntity
block|{
DECL|method|DeflateDecompressingEntity
specifier|public
name|DeflateDecompressingEntity
parameter_list|(
specifier|final
name|HttpEntity
name|entity
parameter_list|)
block|{
name|super
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
DECL|method|getContent
specifier|public
name|InputStream
name|getContent
parameter_list|()
throws|throws
name|IOException
throws|,
name|IllegalStateException
block|{
return|return
operator|new
name|InflaterInputStream
argument_list|(
name|wrappedEntity
operator|.
name|getContent
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

