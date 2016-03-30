begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|time
operator|.
name|Instant
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
name|params
operator|.
name|CommonParams
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
name|ShardParams
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
name|SolrCore
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
name|plugin
operator|.
name|SolrCoreAware
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
comment|/**  * Ping Request Handler for reporting SolrCore health to a Load Balancer.  *  *<p>  * This handler is designed to be used as the endpoint for an HTTP   * Load-Balancer to use when checking the "health" or "up status" of a   * Solr server.  *</p>  *   *<p>   * In its simplest form, the PingRequestHandler should be  * configured with some defaults indicating a request that should be  * executed.  If the request succeeds, then the PingRequestHandler  * will respond back with a simple "OK" status.  If the request fails,  * then the PingRequestHandler will respond back with the  * corresponding HTTP Error code.  Clients (such as load balancers)  * can be configured to poll the PingRequestHandler monitoring for  * these types of responses (or for a simple connection failure) to  * know if there is a problem with the Solr server.  *   * Note in case isShard=true, PingRequestHandler respond back with   * what the delegated handler returns (by default it's /select handler).  *</p>  *  *<pre class="prettyprint">  *&lt;requestHandler name="/admin/ping" class="solr.PingRequestHandler"&gt;  *&lt;lst name="invariants"&gt;  *&lt;str name="qt"&gt;/search&lt;/str&gt;&lt;!-- handler to delegate to --&gt;  *&lt;str name="q"&gt;some test query&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  *  *<p>  * A more advanced option available, is to configure the handler with a   * "healthcheckFile" which can be used to enable/disable the PingRequestHandler.  *</p>  *  *<pre class="prettyprint">  *&lt;requestHandler name="/admin/ping" class="solr.PingRequestHandler"&gt;  *&lt;!-- relative paths are resolved against the data dir --&gt;  *&lt;str name="healthcheckFile"&gt;server-enabled.txt&lt;/str&gt;  *&lt;lst name="invariants"&gt;  *&lt;str name="qt"&gt;/search&lt;/str&gt;&lt;!-- handler to delegate to --&gt;  *&lt;str name="q"&gt;some test query&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  *  *<ul>  *<li>If the health check file exists, the handler will execute the   *       delegated query and return status as described above.  *</li>  *<li>If the health check file does not exist, the handler will return   *       an HTTP error even if the server is working fine and the delegated   *       query would have succeeded  *</li>  *</ul>  *  *<p>   * This health check file feature can be used as a way to indicate  * to some Load Balancers that the server should be "removed from  * rotation" for maintenance, or upgrades, or whatever reason you may  * wish.    *</p>  *  *<p>   * The health check file may be created/deleted by any external  * system, or the PingRequestHandler itself can be used to  * create/delete the file by specifying an "action" param in a  * request:   *</p>  *  *<ul>  *<li><code>http://.../ping?action=enable</code>  *       - creates the health check file if it does not already exist  *</li>  *<li><code>http://.../ping?action=disable</code>  *       - deletes the health check file if it exists  *</li>  *<li><code>http://.../ping?action=status</code>  *       - returns a status code indicating if the healthcheck file exists   *       ("<code>enabled</code>") or not ("<code>disabled</code>")  *</li>  *</ul>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|PingRequestHandler
specifier|public
class|class
name|PingRequestHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
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
DECL|field|HEALTHCHECK_FILE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|HEALTHCHECK_FILE_PARAM
init|=
literal|"healthcheckFile"
decl_stmt|;
DECL|enum|ACTIONS
DECL|enum constant|STATUS
DECL|enum constant|ENABLE
DECL|enum constant|DISABLE
DECL|enum constant|PING
specifier|protected
enum|enum
name|ACTIONS
block|{
name|STATUS
block|,
name|ENABLE
block|,
name|DISABLE
block|,
name|PING
block|}
empty_stmt|;
DECL|field|healthFileName
specifier|private
name|String
name|healthFileName
init|=
literal|null
decl_stmt|;
DECL|field|healthcheck
specifier|private
name|File
name|healthcheck
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Object
name|tmp
init|=
name|args
operator|.
name|get
argument_list|(
name|HEALTHCHECK_FILE_PARAM
argument_list|)
decl_stmt|;
name|healthFileName
operator|=
operator|(
literal|null
operator|==
name|tmp
condition|?
literal|null
else|:
name|tmp
operator|.
name|toString
argument_list|()
operator|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|!=
name|healthFileName
condition|)
block|{
name|healthcheck
operator|=
operator|new
name|File
argument_list|(
name|healthFileName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|healthcheck
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|healthcheck
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|,
name|healthFileName
argument_list|)
expr_stmt|;
name|healthcheck
operator|=
name|healthcheck
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|healthcheck
operator|.
name|getParentFile
argument_list|()
operator|.
name|canWrite
argument_list|()
condition|)
block|{
comment|// this is not fatal, users may not care about enable/disable via
comment|// solr request, file might be touched/deleted by an external system
name|log
operator|.
name|warn
argument_list|(
literal|"Directory for configured healthcheck file is not writable by solr, PingRequestHandler will not be able to control enable/disable: {}"
argument_list|,
name|healthcheck
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns true if the healthcheck flag-file is enabled but does not exist,     * otherwise (no file configured, or file configured and exists)     * returns false.     */
DECL|method|isPingDisabled
specifier|public
name|boolean
name|isPingDisabled
parameter_list|()
block|{
return|return
operator|(
literal|null
operator|!=
name|healthcheck
operator|&&
operator|!
name|healthcheck
operator|.
name|exists
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
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
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
comment|// in this case, we want to default distrib to false so
comment|// we only ping the single node
name|Boolean
name|distrib
init|=
name|params
operator|.
name|getBool
argument_list|(
literal|"distrib"
argument_list|)
decl_stmt|;
if|if
condition|(
name|distrib
operator|==
literal|null
condition|)
block|{
name|ModifiableSolrParams
name|mparams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|mparams
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|mparams
argument_list|)
expr_stmt|;
block|}
name|String
name|actionParam
init|=
name|params
operator|.
name|get
argument_list|(
literal|"action"
argument_list|)
decl_stmt|;
name|ACTIONS
name|action
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|actionParam
operator|==
literal|null
condition|)
block|{
name|action
operator|=
name|ACTIONS
operator|.
name|PING
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|action
operator|=
name|ACTIONS
operator|.
name|valueOf
argument_list|(
name|actionParam
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown action: "
operator|+
name|actionParam
argument_list|)
throw|;
block|}
block|}
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|PING
case|:
if|if
condition|(
name|isPingDisabled
argument_list|()
condition|)
block|{
name|SolrException
name|e
init|=
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"Service disabled"
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|handlePing
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
case|case
name|ENABLE
case|:
name|handleEnable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|DISABLE
case|:
name|handleEnable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|STATUS
case|:
if|if
condition|(
name|healthcheck
operator|==
literal|null
condition|)
block|{
name|SolrException
name|e
init|=
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"healthcheck not configured"
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
name|isPingDisabled
argument_list|()
condition|?
literal|"disabled"
else|:
literal|"enabled"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handlePing
specifier|protected
name|void
name|handlePing
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
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// Get the RequestHandler
name|String
name|qt
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
comment|//optional; you get the default otherwise
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|qt
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown RequestHandler (qt): "
operator|+
name|qt
argument_list|)
throw|;
block|}
if|if
condition|(
name|handler
operator|instanceof
name|PingRequestHandler
condition|)
block|{
comment|// In case it's a query for shard, use default handler
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|wparams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|wparams
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|wparams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Cannot execute the PingRequestHandler recursively"
argument_list|)
throw|;
block|}
block|}
comment|// Execute the ping query and catch any possible exception
name|Throwable
name|ex
init|=
literal|null
decl_stmt|;
comment|// In case it's a query for shard, return the result from delegated handler for distributed query to merge result
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|false
argument_list|)
condition|)
block|{
try|try
block|{
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|ex
operator|=
name|rsp
operator|.
name|getException
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
block|}
comment|// Send an error or return
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
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
literal|"Ping query caused exception: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|SolrQueryResponse
name|pingrsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|req
argument_list|,
name|pingrsp
argument_list|)
expr_stmt|;
name|ex
operator|=
name|pingrsp
operator|.
name|getException
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|headers
init|=
name|rsp
operator|.
name|getResponseHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|headers
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|add
argument_list|(
literal|"zkConnected"
argument_list|,
name|pingrsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|get
argument_list|(
literal|"zkConnected"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
operator|=
name|e
expr_stmt|;
block|}
comment|// Send an error or an 'OK' message (response code will be 200)
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
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
literal|"Ping query caused exception: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"OK"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleEnable
specifier|protected
name|void
name|handleEnable
parameter_list|(
name|boolean
name|enable
parameter_list|)
throws|throws
name|SolrException
block|{
if|if
condition|(
name|healthcheck
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"No healthcheck file defined."
argument_list|)
throw|;
block|}
if|if
condition|(
name|enable
condition|)
block|{
try|try
block|{
comment|// write out when the file was created
name|FileUtils
operator|.
name|write
argument_list|(
name|healthcheck
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
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
literal|"Unable to write healthcheck flag file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|healthcheck
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Did not successfully delete healthcheck file: "
operator|+
name|healthcheck
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Reports application health to a load-balancer"
return|;
block|}
block|}
end_class

end_unit

