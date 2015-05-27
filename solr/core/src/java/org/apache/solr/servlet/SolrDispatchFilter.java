begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package

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
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServletResponse
import|;
end_import

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
name|IOException
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
name|Properties
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
name|AtomicBoolean
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
name|AtomicReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|IOUtils
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
name|lang
operator|.
name|StringUtils
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
name|CloseableHttpClient
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
name|core
operator|.
name|NodeConfig
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
name|core
operator|.
name|SolrResourceLoader
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
name|SolrXmlConfig
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
name|security
operator|.
name|AuthenticationPlugin
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
comment|/**  * This filter looks at the incoming URL maps them to handlers defined in solrconfig.xml  *  * @since solr 1.2  */
end_comment

begin_class
DECL|class|SolrDispatchFilter
specifier|public
class|class
name|SolrDispatchFilter
extends|extends
name|BaseSolrFilter
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
name|SolrDispatchFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cores
specifier|protected
specifier|volatile
name|CoreContainer
name|cores
decl_stmt|;
DECL|field|abortErrorMessage
specifier|protected
name|String
name|abortErrorMessage
init|=
literal|null
decl_stmt|;
DECL|field|httpClient
specifier|protected
specifier|final
name|CloseableHttpClient
name|httpClient
init|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|excludePatterns
specifier|private
name|ArrayList
argument_list|<
name|Pattern
argument_list|>
name|excludePatterns
decl_stmt|;
comment|/**    * Enum to define action that needs to be processed.    * PASSTHROUGH: Pass through to Restlet via webapp.    * FORWARD: Forward rewritten URI (without path prefix and core/collection name) to Restlet    * RETURN: Returns the control, and no further specific processing is needed.    *  This is generally when an error is set and returned.    * RETRY:Retry the request. In cases when a core isn't found to work with, this is set.    */
DECL|enum|Action
enum|enum
name|Action
block|{
DECL|enum constant|PASSTHROUGH
DECL|enum constant|FORWARD
DECL|enum constant|RETURN
DECL|enum constant|RETRY
DECL|enum constant|ADMIN
DECL|enum constant|REMOTEQUERY
DECL|enum constant|PROCESS
name|PASSTHROUGH
block|,
name|FORWARD
block|,
name|RETURN
block|,
name|RETRY
block|,
name|ADMIN
block|,
name|REMOTEQUERY
block|,
name|PROCESS
block|}
DECL|method|SolrDispatchFilter
specifier|public
name|SolrDispatchFilter
parameter_list|()
block|{   }
DECL|field|PROPERTIES_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTIES_ATTRIBUTE
init|=
literal|"solr.properties"
decl_stmt|;
DECL|field|SOLRHOME_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|SOLRHOME_ATTRIBUTE
init|=
literal|"solr.solr.home"
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init()"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|exclude
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"excludePatterns"
argument_list|)
decl_stmt|;
if|if
condition|(
name|exclude
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|excludeArray
init|=
name|exclude
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|excludePatterns
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|element
range|:
name|excludeArray
control|)
block|{
name|excludePatterns
operator|.
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|element
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|Properties
name|extraProperties
init|=
operator|(
name|Properties
operator|)
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|PROPERTIES_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|extraProperties
operator|==
literal|null
condition|)
name|extraProperties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|String
name|solrHome
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|SOLRHOME_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrHome
operator|==
literal|null
condition|)
name|solrHome
operator|=
name|SolrResourceLoader
operator|.
name|locateSolrHome
argument_list|()
expr_stmt|;
name|this
operator|.
name|cores
operator|=
name|createCoreContainer
argument_list|(
name|solrHome
argument_list|,
name|extraProperties
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|HttpClientConfigurer
name|configurer
init|=
name|this
operator|.
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
operator|.
name|getDefaultConfigurer
argument_list|()
decl_stmt|;
if|if
condition|(
name|configurer
operator|!=
literal|null
condition|)
block|{
name|configurer
operator|.
name|configure
argument_list|(
operator|(
name|DefaultHttpClient
operator|)
name|httpClient
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"user.dir="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// catch this so our filter still works
name|log
operator|.
name|error
argument_list|(
literal|"Could not start Solr. Check solr/home property and the logs"
argument_list|)
expr_stmt|;
name|SolrCore
operator|.
name|log
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|t
throw|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init() done"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Override this to change CoreContainer initialization    * @return a CoreContainer to hold this server's cores    */
DECL|method|createCoreContainer
specifier|protected
name|CoreContainer
name|createCoreContainer
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|Properties
name|extraProperties
parameter_list|)
block|{
name|NodeConfig
name|nodeConfig
init|=
name|loadNodeConfig
argument_list|(
name|solrHome
argument_list|,
name|extraProperties
argument_list|)
decl_stmt|;
name|cores
operator|=
operator|new
name|CoreContainer
argument_list|(
name|nodeConfig
argument_list|,
name|extraProperties
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|cores
return|;
block|}
comment|/**    * Get the NodeConfig whether stored on disk, in ZooKeeper, etc.    * This may also be used by custom filters to load relevant configuration.    * @return the NodeConfig    */
DECL|method|loadNodeConfig
specifier|public
specifier|static
name|NodeConfig
name|loadNodeConfig
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|Properties
name|nodeProperties
parameter_list|)
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHome
argument_list|,
literal|null
argument_list|,
name|nodeProperties
argument_list|)
decl_stmt|;
name|String
name|solrxmlLocation
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|,
literal|"solrhome"
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrxmlLocation
operator|==
literal|null
operator|||
literal|"solrhome"
operator|.
name|equalsIgnoreCase
argument_list|(
name|solrxmlLocation
argument_list|)
condition|)
return|return
name|SolrXmlConfig
operator|.
name|fromSolrHome
argument_list|(
name|loader
argument_list|,
name|loader
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
return|;
if|if
condition|(
literal|"zookeeper"
operator|.
name|equalsIgnoreCase
argument_list|(
name|solrxmlLocation
argument_list|)
condition|)
block|{
name|String
name|zkHost
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkHost"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Trying to read solr.xml from "
operator|+
name|zkHost
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|zkHost
argument_list|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not load solr.xml from zookeeper: zkHost system property not set"
argument_list|)
throw|;
try|try
init|(
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
literal|30000
argument_list|)
init|)
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/solr.xml"
argument_list|,
literal|true
argument_list|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not load solr.xml from zookeeper: node not found"
argument_list|)
throw|;
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/solr.xml"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|SolrXmlConfig
operator|.
name|fromInputStream
argument_list|(
name|loader
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not load solr.xml from zookeeper"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Bad solr.solrxml.location set: "
operator|+
name|solrxmlLocation
operator|+
literal|" - should be 'solrhome' or 'zookeeper'"
argument_list|)
throw|;
block|}
DECL|method|getCores
specifier|public
name|CoreContainer
name|getCores
parameter_list|()
block|{
return|return
name|cores
return|;
block|}
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cores
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doFilter
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doFilter
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|,
name|boolean
name|retry
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
if|if
condition|(
operator|!
operator|(
name|request
operator|instanceof
name|HttpServletRequest
operator|)
condition|)
return|return;
name|AtomicReference
argument_list|<
name|ServletRequest
argument_list|>
name|wrappedRequest
init|=
operator|new
name|AtomicReference
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|authenticateRequest
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|wrappedRequest
argument_list|)
condition|)
block|{
comment|// the response and status code have already been sent
return|return;
block|}
if|if
condition|(
name|wrappedRequest
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|request
operator|=
name|wrappedRequest
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"User principal: "
operator|+
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getUserPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// No need to even create the HttpSolrCall object if this path is excluded.
if|if
condition|(
name|excludePatterns
operator|!=
literal|null
condition|)
block|{
name|String
name|servletPath
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getServletPath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|Pattern
name|p
range|:
name|excludePatterns
control|)
block|{
name|Matcher
name|matcher
init|=
name|p
operator|.
name|matcher
argument_list|(
name|servletPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|lookingAt
argument_list|()
condition|)
block|{
name|chain
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
block|}
name|HttpSolrCall
name|call
init|=
operator|new
name|HttpSolrCall
argument_list|(
name|this
argument_list|,
name|cores
argument_list|,
operator|(
name|HttpServletRequest
operator|)
name|request
argument_list|,
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|,
name|retry
argument_list|)
decl_stmt|;
try|try
block|{
name|Action
name|result
init|=
name|call
operator|.
name|call
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|result
condition|)
block|{
case|case
name|PASSTHROUGH
case|:
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
break|break;
case|case
name|RETRY
case|:
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|FORWARD
case|:
name|request
operator|.
name|getRequestDispatcher
argument_list|(
name|call
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|forward
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
finally|finally
block|{
name|call
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|authenticateRequest
specifier|private
name|boolean
name|authenticateRequest
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
specifier|final
name|AtomicReference
argument_list|<
name|ServletRequest
argument_list|>
name|wrappedRequest
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|AtomicBoolean
name|isAuthenticated
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|AuthenticationPlugin
name|authenticationPlugin
init|=
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
decl_stmt|;
if|if
condition|(
name|authenticationPlugin
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Request to authenticate: "
operator|+
name|request
operator|+
literal|", domain: "
operator|+
name|request
operator|.
name|getLocalName
argument_list|()
operator|+
literal|", port: "
operator|+
name|request
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
comment|// upon successful authentication, this should call the chain's next filter.
name|authenticationPlugin
operator|.
name|doAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
operator|new
name|FilterChain
argument_list|()
block|{
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|req
parameter_list|,
name|ServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|isAuthenticated
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|wrappedRequest
operator|.
name|set
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error during request authentication, "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// failed authentication?
if|if
condition|(
operator|!
name|isAuthenticated
operator|.
name|get
argument_list|()
condition|)
block|{
name|response
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

