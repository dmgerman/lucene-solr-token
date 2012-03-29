begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|concurrent
operator|.
name|*
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
name|CoreConnectionPNames
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
name|LBHttpSolrServer
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
name|core
operator|.
name|PluginInfo
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
name|DefaultSolrThreadFactory
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
name|plugin
operator|.
name|PluginInfoInitialized
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
DECL|class|HttpShardHandlerFactory
specifier|public
class|class
name|HttpShardHandlerFactory
extends|extends
name|ShardHandlerFactory
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HttpShardHandlerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// We want an executor that doesn't take up any resources if
comment|// it's not used, so it could be created statically for
comment|// the distributed search component if desired.
comment|//
comment|// Consider CallerRuns policy and a lower max threads to throttle
comment|// requests at some point (or should we simply return failure?)
DECL|field|commExecutor
name|ThreadPoolExecutor
name|commExecutor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
comment|// terminate idle threads after 5 sec
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
comment|// directly hand off tasks
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"httpShardExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|client
name|HttpClient
name|client
decl_stmt|;
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|loadbalancer
name|LBHttpSolrServer
name|loadbalancer
decl_stmt|;
DECL|field|soTimeout
name|int
name|soTimeout
init|=
literal|0
decl_stmt|;
comment|//current default values
DECL|field|connectionTimeout
name|int
name|connectionTimeout
init|=
literal|0
decl_stmt|;
comment|//current default values
DECL|field|maxConnectionsPerHost
name|int
name|maxConnectionsPerHost
init|=
literal|20
decl_stmt|;
DECL|field|corePoolSize
name|int
name|corePoolSize
init|=
literal|0
decl_stmt|;
DECL|field|maximumPoolSize
name|int
name|maximumPoolSize
init|=
literal|10
decl_stmt|;
DECL|field|keepAliveTime
name|int
name|keepAliveTime
init|=
literal|5
decl_stmt|;
DECL|field|queueSize
name|int
name|queueSize
init|=
literal|1
decl_stmt|;
DECL|field|accessPolicy
name|boolean
name|accessPolicy
init|=
literal|true
decl_stmt|;
DECL|field|scheme
specifier|public
name|String
name|scheme
init|=
literal|"http://"
decl_stmt|;
comment|//current default values
DECL|field|mgr
specifier|private
name|ThreadSafeClientConnManager
name|mgr
decl_stmt|;
comment|// socket timeout measured in ms, closes a socket if read
comment|// takes longer than x ms to complete. throws
comment|// java.net.SocketTimeoutException: Read timed out exception
DECL|field|INIT_SO_TIMEOUT
specifier|static
specifier|final
name|String
name|INIT_SO_TIMEOUT
init|=
literal|"socketTimeout"
decl_stmt|;
comment|// connection timeout measures in ms, closes a socket if connection
comment|// cannot be established within x ms. with a
comment|// java.net.SocketTimeoutException: Connection timed out
DECL|field|INIT_CONNECTION_TIMEOUT
specifier|static
specifier|final
name|String
name|INIT_CONNECTION_TIMEOUT
init|=
literal|"connTimeout"
decl_stmt|;
comment|// URL scheme to be used in distributed search.
DECL|field|INIT_URL_SCHEME
specifier|static
specifier|final
name|String
name|INIT_URL_SCHEME
init|=
literal|"urlScheme"
decl_stmt|;
comment|// Maximum connections allowed per host
DECL|field|INIT_MAX_CONNECTION_PER_HOST
specifier|static
specifier|final
name|String
name|INIT_MAX_CONNECTION_PER_HOST
init|=
literal|"maxConnectionsPerHost"
decl_stmt|;
comment|// The core size of the threadpool servicing requests
DECL|field|INIT_CORE_POOL_SIZE
specifier|static
specifier|final
name|String
name|INIT_CORE_POOL_SIZE
init|=
literal|"corePoolSize"
decl_stmt|;
comment|// The maximum size of the threadpool servicing requests
DECL|field|INIT_MAX_POOL_SIZE
specifier|static
specifier|final
name|String
name|INIT_MAX_POOL_SIZE
init|=
literal|"maximumPoolSize"
decl_stmt|;
comment|// The amount of time idle threads persist for in the queue, before being killed
DECL|field|MAX_THREAD_IDLE_TIME
specifier|static
specifier|final
name|String
name|MAX_THREAD_IDLE_TIME
init|=
literal|"maxThreadIdleTime"
decl_stmt|;
comment|// If the threadpool uses a backing queue, what is its maximum size (-1) to use direct handoff
DECL|field|INIT_SIZE_OF_QUEUE
specifier|static
specifier|final
name|String
name|INIT_SIZE_OF_QUEUE
init|=
literal|"sizeOfQueue"
decl_stmt|;
comment|// Configure if the threadpool favours fairness over throughput
DECL|field|INIT_FAIRNESS_POLICY
specifier|static
specifier|final
name|String
name|INIT_FAIRNESS_POLICY
init|=
literal|"fairnessPolicy"
decl_stmt|;
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|()
block|{
return|return
name|getShardHandler
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|)
block|{
return|return
operator|new
name|HttpShardHandler
argument_list|(
name|this
argument_list|,
name|httpClient
argument_list|)
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|NamedList
name|args
init|=
name|info
operator|.
name|initArgs
decl_stmt|;
name|this
operator|.
name|soTimeout
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_SO_TIMEOUT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_URL_SCHEME
argument_list|,
literal|"http://"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
operator|(
name|this
operator|.
name|scheme
operator|.
name|endsWith
argument_list|(
literal|"://"
argument_list|)
operator|)
condition|?
name|this
operator|.
name|scheme
else|:
name|this
operator|.
name|scheme
operator|+
literal|"://"
expr_stmt|;
name|this
operator|.
name|connectionTimeout
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_CONNECTION_TIMEOUT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxConnectionsPerHost
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_MAX_CONNECTION_PER_HOST
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|this
operator|.
name|corePoolSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_CORE_POOL_SIZE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumPoolSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_MAX_POOL_SIZE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepAliveTime
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|MAX_THREAD_IDLE_TIME
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_SIZE_OF_QUEUE
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|accessPolicy
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_FAIRNESS_POLICY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|blockingQueue
init|=
operator|(
name|this
operator|.
name|queueSize
operator|==
operator|-
literal|1
operator|)
condition|?
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|accessPolicy
argument_list|)
else|:
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|queueSize
argument_list|,
name|this
operator|.
name|accessPolicy
argument_list|)
decl_stmt|;
name|this
operator|.
name|commExecutor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|this
operator|.
name|corePoolSize
argument_list|,
name|this
operator|.
name|maximumPoolSize
argument_list|,
name|this
operator|.
name|keepAliveTime
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|blockingQueue
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"httpShardExecutor"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|=
operator|new
name|ThreadSafeClientConnManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|setDefaultMaxPerRoute
argument_list|(
literal|256
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|setMaxTotal
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|DefaultHttpClient
name|client
init|=
operator|new
name|DefaultHttpClient
argument_list|(
name|mgr
argument_list|)
decl_stmt|;
name|client
operator|.
name|getParams
argument_list|()
operator|.
name|setIntParameter
argument_list|(
name|CoreConnectionPNames
operator|.
name|CONNECTION_TIMEOUT
argument_list|,
name|connectionTimeout
argument_list|)
expr_stmt|;
name|client
operator|.
name|getParams
argument_list|()
operator|.
name|setIntParameter
argument_list|(
name|CoreConnectionPNames
operator|.
name|SO_TIMEOUT
argument_list|,
name|soTimeout
argument_list|)
expr_stmt|;
comment|// mgr.getParams().setStaleCheckingEnabled(false);
comment|// prevent retries  (note: this didn't work when set on mgr.. needed to be set on client)
name|DefaultHttpRequestRetryHandler
name|retryhandler
init|=
operator|new
name|DefaultHttpRequestRetryHandler
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|client
operator|.
name|setHttpRequestRetryHandler
argument_list|(
name|retryhandler
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
try|try
block|{
name|loadbalancer
operator|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// should be impossible since we're not passing any URLs here
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getParameter
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getParameter
parameter_list|(
name|NamedList
name|initArgs
parameter_list|,
name|String
name|configKey
parameter_list|,
name|T
name|defaultValue
parameter_list|)
block|{
name|T
name|toReturn
init|=
name|defaultValue
decl_stmt|;
if|if
condition|(
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|T
name|temp
init|=
operator|(
name|T
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
decl_stmt|;
name|toReturn
operator|=
operator|(
name|temp
operator|!=
literal|null
operator|)
condition|?
name|temp
else|:
name|defaultValue
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Setting {} to: {}"
argument_list|,
name|configKey
argument_list|,
name|soTimeout
argument_list|)
expr_stmt|;
return|return
name|toReturn
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
try|try
block|{
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|loadbalancer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|commExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

