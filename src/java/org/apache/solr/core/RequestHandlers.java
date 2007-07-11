begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
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
name|DOMUtil
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
name|SimpleOrderedMap
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
name|StandardRequestHandler
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
name|util
operator|.
name|plugin
operator|.
name|AbstractPluginLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|RequestHandlers
specifier|final
class|class
name|RequestHandlers
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RequestHandlers
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_HANDLER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HANDLER_NAME
init|=
literal|"standard"
decl_stmt|;
comment|// Use a synchronized map - since the handlers can be changed at runtime,
comment|// the map implementation should be thread safe
DECL|field|handlers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|handlers
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Trim the trailing '/' if its there.    *     * we want:    *  /update/csv    *  /update/csv/    * to map to the same handler     *     */
DECL|method|normalize
specifier|private
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|p
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
return|return
name|p
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
return|return
name|p
return|;
block|}
comment|/**    * @return the RequestHandler registered at the given name     */
DECL|method|get
specifier|public
name|SolrRequestHandler
name|get
parameter_list|(
name|String
name|handlerName
parameter_list|)
block|{
return|return
name|handlers
operator|.
name|get
argument_list|(
name|normalize
argument_list|(
name|handlerName
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Handlers must be initialized before calling this function.  As soon as this is    * called, the handler can immediately accept requests.    *     * This call is thread safe.    *     * @return the previous handler at the given path or null    */
DECL|method|register
specifier|public
name|SolrRequestHandler
name|register
parameter_list|(
name|String
name|handlerName
parameter_list|,
name|SolrRequestHandler
name|handler
parameter_list|)
block|{
name|String
name|norm
init|=
name|normalize
argument_list|(
name|handlerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
return|return
name|handlers
operator|.
name|remove
argument_list|(
name|norm
argument_list|)
return|;
block|}
name|SolrRequestHandler
name|old
init|=
name|handlers
operator|.
name|put
argument_list|(
name|norm
argument_list|,
name|handler
argument_list|)
decl_stmt|;
if|if
condition|(
name|handlerName
operator|!=
literal|null
operator|&&
name|handlerName
operator|!=
literal|""
condition|)
block|{
if|if
condition|(
name|handler
operator|instanceof
name|SolrInfoMBean
condition|)
block|{
name|SolrInfoRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|put
argument_list|(
name|handlerName
argument_list|,
operator|(
name|SolrInfoMBean
operator|)
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|old
return|;
block|}
comment|/**    * Returns an unmodifiable Map containing the registered handlers    */
DECL|method|getRequestHandlers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|getRequestHandlers
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|handlers
argument_list|)
return|;
block|}
comment|/**    * Read solrconfig.xml and register the appropriate handlers    *     * This function should<b>only</b> be called from the SolrCore constructor.  It is    * not intended as a public API.    *     * While the normal runtime registration contract is that handlers MUST be initialized     * before they are registered, this function does not do that exactly.    *     * This function registers all handlers first and then calls init() for each one.      *     * This is OK because this function is only called at startup and there is no chance that    * a handler could be asked to handle a request before it is initialized.    *     * The advantage to this approach is that handlers can know what path they are registered    * to and what other handlers are available at startup.    *     * Handlers will be registered and initialized in the order they appear in solrconfig.xml    */
DECL|method|initHandlersFromConfig
name|void
name|initHandlersFromConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
specifier|final
name|RequestHandlers
name|handlers
init|=
name|this
decl_stmt|;
name|AbstractPluginLoader
argument_list|<
name|SolrRequestHandler
argument_list|>
name|loader
init|=
operator|new
name|AbstractPluginLoader
argument_list|<
name|SolrRequestHandler
argument_list|>
argument_list|(
literal|"[solrconfig.xml] requestHandler"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|SolrRequestHandler
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|className
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|startup
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"startup"
argument_list|)
decl_stmt|;
if|if
condition|(
name|startup
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"lazy"
operator|.
name|equals
argument_list|(
name|startup
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"adding lazy requestHandler: "
operator|+
name|className
argument_list|)
expr_stmt|;
name|NamedList
name|args
init|=
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
operator|new
name|LazyRequestHandlerWrapper
argument_list|(
name|className
argument_list|,
name|args
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unknown startup value: '"
operator|+
name|startup
operator|+
literal|"' for: "
operator|+
name|className
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|className
argument_list|,
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|SolrRequestHandler
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrRequestHandler
name|plugin
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|handlers
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|plugin
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|SolrRequestHandler
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|plugin
operator|.
name|init
argument_list|(
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|config
operator|.
name|evaluate
argument_list|(
literal|"requestHandler"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
comment|// Load the handlers and get the default one
name|SolrRequestHandler
name|defaultHandler
init|=
name|loader
operator|.
name|load
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultHandler
operator|==
literal|null
condition|)
block|{
name|defaultHandler
operator|=
name|get
argument_list|(
name|RequestHandlers
operator|.
name|DEFAULT_HANDLER_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultHandler
operator|==
literal|null
condition|)
block|{
name|defaultHandler
operator|=
operator|new
name|StandardRequestHandler
argument_list|()
expr_stmt|;
name|register
argument_list|(
name|RequestHandlers
operator|.
name|DEFAULT_HANDLER_NAME
argument_list|,
name|defaultHandler
argument_list|)
expr_stmt|;
block|}
block|}
name|register
argument_list|(
literal|null
argument_list|,
name|defaultHandler
argument_list|)
expr_stmt|;
name|register
argument_list|(
literal|""
argument_list|,
name|defaultHandler
argument_list|)
expr_stmt|;
block|}
comment|/**    * The<code>LazyRequestHandlerWrapper</core> wraps any {@link SolrRequestHandler}.      * Rather then instanciate and initalize the handler on startup, this wrapper waits    * until it is actually called.  This should only be used for handlers that are    * unlikely to be used in the normal lifecycle.    *     * You can enable lazy loading in solrconfig.xml using:    *     *<pre>    *&lt;requestHandler name="..." class="..." startup="lazy"&gt;    *    ...    *&lt;/requestHandler&gt;    *</pre>    *     * This is a private class - if there is a real need for it to be public, it could    * move    *     * @version $Id$    * @since solr 1.2    */
DECL|class|LazyRequestHandlerWrapper
specifier|private
specifier|static
specifier|final
class|class
name|LazyRequestHandlerWrapper
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
block|{
DECL|field|_className
specifier|private
name|String
name|_className
decl_stmt|;
DECL|field|_args
specifier|private
name|NamedList
name|_args
decl_stmt|;
DECL|field|_handler
specifier|private
name|SolrRequestHandler
name|_handler
decl_stmt|;
DECL|method|LazyRequestHandlerWrapper
specifier|public
name|LazyRequestHandlerWrapper
parameter_list|(
name|String
name|className
parameter_list|,
name|NamedList
name|args
parameter_list|)
block|{
name|_className
operator|=
name|className
expr_stmt|;
name|_args
operator|=
name|args
expr_stmt|;
name|_handler
operator|=
literal|null
expr_stmt|;
comment|// don't initialize
block|}
comment|/**      * In normal use, this function will not be called      */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|// do nothing
block|}
comment|/**      * Wait for the first request before initializing the wrapped handler       */
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|getWrappedHandler
argument_list|()
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|getWrappedHandler
specifier|public
specifier|synchronized
name|SolrRequestHandler
name|getWrappedHandler
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|Config
operator|.
name|findClass
argument_list|(
name|_className
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
name|_handler
operator|=
operator|(
name|SolrRequestHandler
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|_handler
operator|.
name|init
argument_list|(
name|_args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
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
literal|"lazy loading error"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
return|return
name|_handler
return|;
block|}
DECL|method|getHandlerClass
specifier|public
name|String
name|getHandlerClass
parameter_list|()
block|{
return|return
name|_className
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Lazy["
operator|+
name|_className
operator|+
literal|"]"
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|==
literal|null
condition|)
block|{
return|return
name|getName
argument_list|()
return|;
block|}
return|return
name|_handler
operator|.
name|getDescription
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
name|String
name|rev
init|=
literal|"$Revision$"
decl_stmt|;
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
name|rev
operator|+=
literal|" :: "
operator|+
name|_handler
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
return|return
name|rev
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
name|String
name|rev
init|=
literal|"$Id$"
decl_stmt|;
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
name|rev
operator|+=
literal|" :: "
operator|+
name|_handler
operator|.
name|getSourceId
argument_list|()
expr_stmt|;
block|}
return|return
name|rev
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
name|String
name|rev
init|=
literal|"$URL$"
decl_stmt|;
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
name|rev
operator|+=
literal|"\n"
operator|+
name|_handler
operator|.
name|getSource
argument_list|()
expr_stmt|;
block|}
return|return
name|rev
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|_handler
operator|.
name|getDocs
argument_list|()
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
return|return
name|_handler
operator|.
name|getStatistics
argument_list|()
return|;
block|}
name|NamedList
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"note"
argument_list|,
literal|"not initialized yet"
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
block|}
end_class

end_unit

