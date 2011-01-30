begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
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
name|commons
operator|.
name|httpclient
operator|.
name|MultiThreadedHttpConnectionManager
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
name|httpclient
operator|.
name|DefaultHttpMethodRetryHandler
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
name|httpclient
operator|.
name|params
operator|.
name|HttpMethodParams
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
name|*
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
name|SolrException
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
name|ref
operator|.
name|WeakReference
import|;
end_import

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
name|net
operator|.
name|URL
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * LBHttpSolrServer or "LoadBalanced HttpSolrServer" is a load balancing wrapper to CommonsHttpSolrServer. This is useful when you  * have multiple SolrServers and the requests need to be Load Balanced among them. This should<b>NOT</b> be used for  * indexing. Also see the<a href="http://wiki.apache.org/solr/LBHttpSolrServer">wiki</a> page.  *<p/>  * It offers automatic failover when a server goes down and it detects when the server comes back up.  *<p/>  * Load balancing is done using a simple round-robin on the list of servers.  *<p/>  * If a request to a server fails by an IOException due to a connection timeout or read timeout then the host is taken  * off the list of live servers and moved to a 'dead server list' and the request is resent to the next live server.  * This process is continued till it tries all the live servers. If atleast one server is alive, the request succeeds,  * and if not it fails.  *<blockquote><pre>  * SolrServer lbHttpSolrServer = new LBHttpSolrServer("http://host1:8080/solr/","http://host2:8080/solr","http://host2:8080/solr");  * //or if you wish to pass the HttpClient do as follows  * httpClient httpClient =  new HttpClient();  * SolrServer lbHttpSolrServer = new LBHttpSolrServer(httpClient,"http://host1:8080/solr/","http://host2:8080/solr","http://host2:8080/solr");  *</pre></blockquote>  * This detects if a dead server comes alive automatically. The check is done in fixed intervals in a dedicated thread.  * This interval can be set using {@link #setAliveCheckInterval} , the default is set to one minute.  *<p/>  *<b>When to use this?</b><br/> This can be used as a software load balancer when you do not wish to setup an external  * load balancer. Alternatives to this code are to use  * a dedicated hardware load balancer or using Apache httpd with mod_proxy_balancer as a load balancer. See<a  * href="http://en.wikipedia.org/wiki/Load_balancing_(computing)">Load balancing on Wikipedia</a>  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|LBHttpSolrServer
specifier|public
class|class
name|LBHttpSolrServer
extends|extends
name|SolrServer
block|{
comment|// keys to the maps are currently of the form "http://localhost:8983/solr"
comment|// which should be equivalent to CommonsHttpSolrServer.getBaseURL()
DECL|field|aliveServers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ServerWrapper
argument_list|>
name|aliveServers
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|ServerWrapper
argument_list|>
argument_list|()
decl_stmt|;
comment|// access to aliveServers should be synchronized on itself
DECL|field|zombieServers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ServerWrapper
argument_list|>
name|zombieServers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|ServerWrapper
argument_list|>
argument_list|()
decl_stmt|;
comment|// changes to aliveServers are reflected in this array, no need to synchronize
DECL|field|aliveServerList
specifier|private
specifier|volatile
name|ServerWrapper
index|[]
name|aliveServerList
init|=
operator|new
name|ServerWrapper
index|[
literal|0
index|]
decl_stmt|;
DECL|field|aliveCheckExecutor
specifier|private
name|ScheduledExecutorService
name|aliveCheckExecutor
decl_stmt|;
DECL|field|httpClient
specifier|private
name|HttpClient
name|httpClient
decl_stmt|;
DECL|field|counter
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|solrQuery
specifier|private
specifier|static
specifier|final
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
DECL|field|binaryParser
specifier|private
specifier|static
specifier|final
name|BinaryResponseParser
name|binaryParser
init|=
operator|new
name|BinaryResponseParser
argument_list|()
decl_stmt|;
static|static
block|{
name|solrQuery
operator|.
name|setRows
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|class|ServerWrapper
specifier|private
specifier|static
class|class
name|ServerWrapper
block|{
DECL|field|solrServer
specifier|final
name|CommonsHttpSolrServer
name|solrServer
decl_stmt|;
DECL|field|lastUsed
name|long
name|lastUsed
decl_stmt|;
comment|// last time used for a real request
DECL|field|lastChecked
name|long
name|lastChecked
decl_stmt|;
comment|// last time checked for liveness
comment|// "standard" servers are used by default.  They normally live in the alive list
comment|// and move to the zombie list when unavailable.  When they become available again,
comment|// they move back to the alive list.
DECL|field|standard
name|boolean
name|standard
init|=
literal|true
decl_stmt|;
DECL|field|failedPings
name|int
name|failedPings
init|=
literal|0
decl_stmt|;
DECL|method|ServerWrapper
specifier|public
name|ServerWrapper
parameter_list|(
name|CommonsHttpSolrServer
name|solrServer
parameter_list|)
block|{
name|this
operator|.
name|solrServer
operator|=
name|solrServer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|solrServer
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|solrServer
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|getKey
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|ServerWrapper
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ServerWrapper
operator|)
name|obj
operator|)
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|Req
specifier|public
specifier|static
class|class
name|Req
block|{
DECL|field|request
specifier|protected
name|SolrRequest
name|request
decl_stmt|;
DECL|field|servers
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|servers
decl_stmt|;
DECL|field|numDeadServersToTry
specifier|protected
name|int
name|numDeadServersToTry
decl_stmt|;
DECL|method|Req
specifier|public
name|Req
parameter_list|(
name|SolrRequest
name|request
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|servers
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|servers
operator|=
name|servers
expr_stmt|;
name|this
operator|.
name|numDeadServersToTry
operator|=
name|servers
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
DECL|method|getRequest
specifier|public
name|SolrRequest
name|getRequest
parameter_list|()
block|{
return|return
name|request
return|;
block|}
DECL|method|getServers
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getServers
parameter_list|()
block|{
return|return
name|servers
return|;
block|}
comment|/** @return the number of dead servers to try if there are no live servers left */
DECL|method|getNumDeadServersToTry
specifier|public
name|int
name|getNumDeadServersToTry
parameter_list|()
block|{
return|return
name|numDeadServersToTry
return|;
block|}
comment|/** @return The number of dead servers to try if there are no live servers left.      * Defaults to the number of servers in this request. */
DECL|method|setNumDeadServersToTry
specifier|public
name|void
name|setNumDeadServersToTry
parameter_list|(
name|int
name|numDeadServersToTry
parameter_list|)
block|{
name|this
operator|.
name|numDeadServersToTry
operator|=
name|numDeadServersToTry
expr_stmt|;
block|}
block|}
DECL|class|Rsp
specifier|public
specifier|static
class|class
name|Rsp
block|{
DECL|field|server
specifier|protected
name|String
name|server
decl_stmt|;
DECL|field|rsp
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
decl_stmt|;
comment|/** The response from the server */
DECL|method|getResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getResponse
parameter_list|()
block|{
return|return
name|rsp
return|;
block|}
comment|/** The server that returned the response */
DECL|method|getServer
specifier|public
name|String
name|getServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
block|}
DECL|method|LBHttpSolrServer
specifier|public
name|LBHttpSolrServer
parameter_list|(
name|String
modifier|...
name|solrServerUrls
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
argument_list|(
operator|new
name|HttpClient
argument_list|(
operator|new
name|MultiThreadedHttpConnectionManager
argument_list|()
argument_list|)
argument_list|,
name|solrServerUrls
argument_list|)
expr_stmt|;
name|DefaultHttpMethodRetryHandler
name|retryhandler
init|=
operator|new
name|DefaultHttpMethodRetryHandler
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|httpClient
operator|.
name|getParams
argument_list|()
operator|.
name|setParameter
argument_list|(
name|HttpMethodParams
operator|.
name|RETRY_HANDLER
argument_list|,
name|retryhandler
argument_list|)
expr_stmt|;
block|}
comment|/** The provided httpClient should use a multi-threaded connection manager */
DECL|method|LBHttpSolrServer
specifier|public
name|LBHttpSolrServer
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|String
modifier|...
name|solrServerUrl
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
argument_list|(
name|httpClient
argument_list|,
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|,
name|solrServerUrl
argument_list|)
expr_stmt|;
block|}
comment|/** The provided httpClient should use a multi-threaded connection manager */
DECL|method|LBHttpSolrServer
specifier|public
name|LBHttpSolrServer
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|ResponseParser
name|parser
parameter_list|,
name|String
modifier|...
name|solrServerUrl
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
operator|.
name|httpClient
operator|=
name|httpClient
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|solrServerUrl
control|)
block|{
name|ServerWrapper
name|wrapper
init|=
operator|new
name|ServerWrapper
argument_list|(
name|makeServer
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
name|aliveServers
operator|.
name|put
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
block|}
name|updateAliveList
argument_list|()
expr_stmt|;
block|}
DECL|method|normalize
specifier|public
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|server
parameter_list|)
block|{
if|if
condition|(
name|server
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|server
operator|=
name|server
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|server
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
DECL|method|makeServer
specifier|protected
name|CommonsHttpSolrServer
name|makeServer
parameter_list|(
name|String
name|server
parameter_list|)
throws|throws
name|MalformedURLException
block|{
return|return
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|server
argument_list|,
name|httpClient
argument_list|,
name|binaryParser
argument_list|)
return|;
block|}
comment|/**    * Tries to query a live server from the list provided in Req. Servers in the dead pool are skipped.    * If a request fails due to an IOException, the server is moved to the dead pool for a certain period of    * time, or until a test request on that server succeeds.    *    * Servers are queried in the exact order given (except servers currently in the dead pool are skipped).    * If no live servers from the provided list remain to be tried, a number of previously skipped dead servers will be tried.    * Req.getNumDeadServersToTry() controls how many dead servers will be tried.    *    * If no live servers are found a SolrServerException is thrown.    *    * @param req contains both the request as well as the list of servers to query    *    * @return the result of the request    *    * @throws SolrServerException    * @throws IOException    */
DECL|method|request
specifier|public
name|Rsp
name|request
parameter_list|(
name|Req
name|req
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|Rsp
name|rsp
init|=
operator|new
name|Rsp
argument_list|()
decl_stmt|;
name|Exception
name|ex
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|ServerWrapper
argument_list|>
name|skipped
init|=
operator|new
name|ArrayList
argument_list|<
name|ServerWrapper
argument_list|>
argument_list|(
name|req
operator|.
name|getNumDeadServersToTry
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|serverStr
range|:
name|req
operator|.
name|getServers
argument_list|()
control|)
block|{
name|serverStr
operator|=
name|normalize
argument_list|(
name|serverStr
argument_list|)
expr_stmt|;
comment|// if the server is currently a zombie, just skip to the next one
name|ServerWrapper
name|wrapper
init|=
name|zombieServers
operator|.
name|get
argument_list|(
name|serverStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapper
operator|!=
literal|null
condition|)
block|{
comment|// System.out.println("ZOMBIE SERVER QUERIED: " + serverStr);
if|if
condition|(
name|skipped
operator|.
name|size
argument_list|()
operator|<
name|req
operator|.
name|getNumDeadServersToTry
argument_list|()
condition|)
name|skipped
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|rsp
operator|.
name|server
operator|=
name|serverStr
expr_stmt|;
name|CommonsHttpSolrServer
name|server
init|=
name|makeServer
argument_list|(
name|serverStr
argument_list|)
decl_stmt|;
try|try
block|{
name|rsp
operator|.
name|rsp
operator|=
name|server
operator|.
name|request
argument_list|(
name|req
operator|.
name|getRequest
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rsp
return|;
comment|// SUCCESS
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// Server is alive but the request was malformed or invalid
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getRootCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
name|wrapper
operator|=
operator|new
name|ServerWrapper
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|lastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|wrapper
operator|.
name|standard
operator|=
literal|false
expr_stmt|;
name|zombieServers
operator|.
name|put
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
name|startAliveCheckExecutor
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// try the servers we previously skipped
for|for
control|(
name|ServerWrapper
name|wrapper
range|:
name|skipped
control|)
block|{
try|try
block|{
name|rsp
operator|.
name|rsp
operator|=
name|wrapper
operator|.
name|solrServer
operator|.
name|request
argument_list|(
name|req
operator|.
name|getRequest
argument_list|()
argument_list|)
expr_stmt|;
name|zombieServers
operator|.
name|remove
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rsp
return|;
comment|// SUCCESS
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// Server is alive but the request was malformed or invalid
name|zombieServers
operator|.
name|remove
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getRootCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
comment|// already a zombie, no need to re-add
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ex
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"No live SolrServers available to handle this request"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"No live SolrServers available to handle this request"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|updateAliveList
specifier|private
name|void
name|updateAliveList
parameter_list|()
block|{
synchronized|synchronized
init|(
name|aliveServers
init|)
block|{
name|aliveServerList
operator|=
name|aliveServers
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|ServerWrapper
index|[
name|aliveServers
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeFromAlive
specifier|private
name|ServerWrapper
name|removeFromAlive
parameter_list|(
name|String
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|aliveServers
init|)
block|{
name|ServerWrapper
name|wrapper
init|=
name|aliveServers
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapper
operator|!=
literal|null
condition|)
name|updateAliveList
argument_list|()
expr_stmt|;
return|return
name|wrapper
return|;
block|}
block|}
DECL|method|addToAlive
specifier|private
name|void
name|addToAlive
parameter_list|(
name|ServerWrapper
name|wrapper
parameter_list|)
block|{
synchronized|synchronized
init|(
name|aliveServers
init|)
block|{
name|ServerWrapper
name|prev
init|=
name|aliveServers
operator|.
name|put
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|,
name|wrapper
argument_list|)
decl_stmt|;
comment|// TODO: warn if there was a previous entry?
name|updateAliveList
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addSolrServer
specifier|public
name|void
name|addSolrServer
parameter_list|(
name|String
name|server
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|CommonsHttpSolrServer
name|solrServer
init|=
name|makeServer
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|addToAlive
argument_list|(
operator|new
name|ServerWrapper
argument_list|(
name|solrServer
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|removeSolrServer
specifier|public
name|String
name|removeSolrServer
parameter_list|(
name|String
name|server
parameter_list|)
block|{
try|try
block|{
name|server
operator|=
operator|new
name|URL
argument_list|(
name|server
argument_list|)
operator|.
name|toExternalForm
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|server
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|server
operator|=
name|server
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|server
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// there is a small race condition here - if the server is in the process of being moved between
comment|// lists, we could fail to remove it.
name|removeFromAlive
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|zombieServers
operator|.
name|remove
argument_list|(
name|server
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|setConnectionTimeout
specifier|public
name|void
name|setConnectionTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|httpClient
operator|.
name|getHttpConnectionManager
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|setConnectionTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * set connectionManagerTimeout on the HttpClient.*    */
DECL|method|setConnectionManagerTimeout
specifier|public
name|void
name|setConnectionManagerTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|httpClient
operator|.
name|getParams
argument_list|()
operator|.
name|setConnectionManagerTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * set soTimeout (read timeout) on the underlying HttpConnectionManager. This is desirable for queries, but probably    * not for indexing.    */
DECL|method|setSoTimeout
specifier|public
name|void
name|setSoTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|httpClient
operator|.
name|getParams
argument_list|()
operator|.
name|setSoTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tries to query a live server. A SolrServerException is thrown if all servers are dead.    * If the request failed due to IOException then the live server is moved to dead pool and the request is    * retried on another live server.  After live servers are exhausted, any servers previously marked as dead    * will be tried before failing the request.    *    * @param request the SolrRequest.    *    * @return response    *    * @throws SolrServerException    * @throws IOException    */
annotation|@
name|Override
DECL|method|request
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
specifier|final
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|Exception
name|ex
init|=
literal|null
decl_stmt|;
name|ServerWrapper
index|[]
name|serverList
init|=
name|aliveServerList
decl_stmt|;
name|int
name|maxTries
init|=
name|serverList
operator|.
name|length
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ServerWrapper
argument_list|>
name|justFailed
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|attempts
init|=
literal|0
init|;
name|attempts
operator|<
name|maxTries
condition|;
name|attempts
operator|++
control|)
block|{
name|int
name|count
init|=
name|counter
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|ServerWrapper
name|wrapper
init|=
name|serverList
index|[
name|count
operator|%
name|serverList
operator|.
name|length
index|]
decl_stmt|;
name|wrapper
operator|.
name|lastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|wrapper
operator|.
name|solrServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// Server is alive but the request was malformed or invalid
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getRootCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
name|moveAliveToDead
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|justFailed
operator|==
literal|null
condition|)
name|justFailed
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ServerWrapper
argument_list|>
argument_list|()
expr_stmt|;
name|justFailed
operator|.
name|put
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// try other standard servers that we didn't try just now
for|for
control|(
name|ServerWrapper
name|wrapper
range|:
name|zombieServers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|wrapper
operator|.
name|standard
operator|==
literal|false
operator|||
name|justFailed
operator|!=
literal|null
operator|&&
name|justFailed
operator|.
name|containsKey
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
continue|continue;
try|try
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|wrapper
operator|.
name|solrServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// remove from zombie list *before* adding to alive to avoid a race that could lose a server
name|zombieServers
operator|.
name|remove
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|addToAlive
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
return|return
name|rsp
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// Server is alive but the request was malformed or invalid
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getRootCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
comment|// still dead
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|ex
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"No live SolrServers available to handle this request"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"No live SolrServers available to handle this request"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Takes up one dead server and check for aliveness. The check is done in a roundrobin. Each server is checked for    * aliveness once in 'x' millis where x is decided by the setAliveCheckinterval() or it is defaulted to 1 minute    *    * @param zombieServer a server in the dead pool    */
DECL|method|checkAZombieServer
specifier|private
name|void
name|checkAZombieServer
parameter_list|(
name|ServerWrapper
name|zombieServer
parameter_list|)
block|{
name|long
name|currTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|zombieServer
operator|.
name|lastChecked
operator|=
name|currTime
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|zombieServer
operator|.
name|solrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getStatus
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// server has come back up.
comment|// make sure to remove from zombies before adding to alive to avoid a race condition
comment|// where another thread could mark it down, move it back to zombie, and then we delete
comment|// from zombie and lose it forever.
name|ServerWrapper
name|wrapper
init|=
name|zombieServers
operator|.
name|remove
argument_list|(
name|zombieServer
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrapper
operator|!=
literal|null
condition|)
block|{
name|wrapper
operator|.
name|failedPings
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|wrapper
operator|.
name|standard
condition|)
block|{
name|addToAlive
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// something else already moved the server from zombie to alive
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//Expected. The server is still down.
name|zombieServer
operator|.
name|failedPings
operator|++
expr_stmt|;
comment|// If the server doesn't belong in the standard set belonging to this load balancer
comment|// then simply drop it after a certain number of failed pings.
if|if
condition|(
operator|!
name|zombieServer
operator|.
name|standard
operator|&&
name|zombieServer
operator|.
name|failedPings
operator|>=
name|NONSTANDARD_PING_LIMIT
condition|)
block|{
name|zombieServers
operator|.
name|remove
argument_list|(
name|zombieServer
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|moveAliveToDead
specifier|private
name|void
name|moveAliveToDead
parameter_list|(
name|ServerWrapper
name|wrapper
parameter_list|)
block|{
name|wrapper
operator|=
name|removeFromAlive
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrapper
operator|==
literal|null
condition|)
return|return;
comment|// another thread already detected the failure and removed it
name|zombieServers
operator|.
name|put
argument_list|(
name|wrapper
operator|.
name|getKey
argument_list|()
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
name|startAliveCheckExecutor
argument_list|()
expr_stmt|;
block|}
DECL|field|interval
specifier|private
name|int
name|interval
init|=
name|CHECK_INTERVAL
decl_stmt|;
comment|/**    * LBHttpSolrServer keeps pinging the dead servers at fixed interval to find if it is alive. Use this to set that    * interval    *    * @param interval time in milliseconds    */
DECL|method|setAliveCheckInterval
specifier|public
name|void
name|setAliveCheckInterval
parameter_list|(
name|int
name|interval
parameter_list|)
block|{
if|if
condition|(
name|interval
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Alive check interval must be "
operator|+
literal|"positive, specified value = "
operator|+
name|interval
argument_list|)
throw|;
block|}
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
DECL|method|startAliveCheckExecutor
specifier|private
name|void
name|startAliveCheckExecutor
parameter_list|()
block|{
comment|// double-checked locking, but it's OK because we don't *do* anything with aliveCheckExecutor
comment|// if it's not null.
if|if
condition|(
name|aliveCheckExecutor
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|aliveCheckExecutor
operator|==
literal|null
condition|)
block|{
name|aliveCheckExecutor
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|aliveCheckExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|getAliveCheckRunner
argument_list|(
operator|new
name|WeakReference
argument_list|<
name|LBHttpSolrServer
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
argument_list|,
name|this
operator|.
name|interval
argument_list|,
name|this
operator|.
name|interval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getAliveCheckRunner
specifier|private
specifier|static
name|Runnable
name|getAliveCheckRunner
parameter_list|(
specifier|final
name|WeakReference
argument_list|<
name|LBHttpSolrServer
argument_list|>
name|lbRef
parameter_list|)
block|{
return|return
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LBHttpSolrServer
name|lb
init|=
name|lbRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|lb
operator|!=
literal|null
operator|&&
name|lb
operator|.
name|zombieServers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ServerWrapper
name|zombieServer
range|:
name|lb
operator|.
name|zombieServers
operator|.
name|values
argument_list|()
control|)
block|{
name|lb
operator|.
name|checkAZombieServer
argument_list|(
name|zombieServer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
DECL|method|getHttpClient
specifier|public
name|HttpClient
name|getHttpClient
parameter_list|()
block|{
return|return
name|httpClient
return|;
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
name|this
operator|.
name|aliveCheckExecutor
operator|!=
literal|null
condition|)
name|this
operator|.
name|aliveCheckExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
comment|// defaults
DECL|field|CHECK_INTERVAL
specifier|private
specifier|static
specifier|final
name|int
name|CHECK_INTERVAL
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
comment|//1 minute between checks
DECL|field|NONSTANDARD_PING_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|NONSTANDARD_PING_LIMIT
init|=
literal|5
decl_stmt|;
comment|// number of times we'll ping dead servers not in the server list
block|}
end_class

end_unit

