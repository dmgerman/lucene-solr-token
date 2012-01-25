begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
name|embedded
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
name|util
operator|.
name|Random
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
name|HttpServlet
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrDispatchFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|component
operator|.
name|LifeCycle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Handler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|bio
operator|.
name|SocketConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|FilterHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|HashSessionIdManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|log
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|thread
operator|.
name|QueuedThreadPool
import|;
end_import

begin_comment
comment|/**  * Run solr using jetty  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|JettySolrRunner
specifier|public
class|class
name|JettySolrRunner
block|{
DECL|field|server
name|Server
name|server
decl_stmt|;
DECL|field|dispatchFilter
name|FilterHolder
name|dispatchFilter
decl_stmt|;
DECL|field|context
name|String
name|context
decl_stmt|;
DECL|field|solrConfigFilename
specifier|private
name|String
name|solrConfigFilename
decl_stmt|;
DECL|field|schemaFilename
specifier|private
name|String
name|schemaFilename
decl_stmt|;
DECL|field|waitOnSolr
specifier|private
name|boolean
name|waitOnSolr
init|=
literal|false
decl_stmt|;
DECL|field|lastPort
specifier|private
name|int
name|lastPort
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|shards
specifier|private
name|String
name|shards
decl_stmt|;
DECL|field|dataDir
specifier|private
name|String
name|dataDir
decl_stmt|;
DECL|field|startedBefore
specifier|private
specifier|volatile
name|boolean
name|startedBefore
init|=
literal|false
decl_stmt|;
DECL|field|solrHome
specifier|private
name|String
name|solrHome
decl_stmt|;
DECL|field|stopAtShutdown
specifier|private
name|boolean
name|stopAtShutdown
decl_stmt|;
DECL|method|JettySolrRunner
specifier|public
name|JettySolrRunner
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|context
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|init
argument_list|(
name|solrHome
argument_list|,
name|context
argument_list|,
name|port
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|JettySolrRunner
specifier|public
name|JettySolrRunner
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|context
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|solrConfigFilename
parameter_list|,
name|String
name|schemaFileName
parameter_list|)
block|{
name|this
operator|.
name|init
argument_list|(
name|solrHome
argument_list|,
name|context
argument_list|,
name|port
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrConfigFilename
operator|=
name|solrConfigFilename
expr_stmt|;
name|this
operator|.
name|schemaFilename
operator|=
name|schemaFileName
expr_stmt|;
block|}
DECL|method|JettySolrRunner
specifier|public
name|JettySolrRunner
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|context
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|solrConfigFilename
parameter_list|,
name|String
name|schemaFileName
parameter_list|,
name|boolean
name|stopAtShutdown
parameter_list|)
block|{
name|this
operator|.
name|init
argument_list|(
name|solrHome
argument_list|,
name|context
argument_list|,
name|port
argument_list|,
name|stopAtShutdown
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrConfigFilename
operator|=
name|solrConfigFilename
expr_stmt|;
name|this
operator|.
name|schemaFilename
operator|=
name|schemaFileName
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|context
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|stopAtShutdown
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|server
operator|=
operator|new
name|Server
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrHome
operator|=
name|solrHome
expr_stmt|;
name|this
operator|.
name|stopAtShutdown
operator|=
name|stopAtShutdown
expr_stmt|;
name|server
operator|.
name|setStopAtShutdown
argument_list|(
name|stopAtShutdown
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stopAtShutdown
condition|)
block|{
name|server
operator|.
name|setGracefulShutdown
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.testMode"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// SelectChannelConnector connector = new SelectChannelConnector();
comment|// Normal SocketConnector is what solr's example server uses by default
name|SocketConnector
name|connector
init|=
operator|new
name|SocketConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stopAtShutdown
condition|)
block|{
name|QueuedThreadPool
name|threadPool
init|=
operator|(
name|QueuedThreadPool
operator|)
name|connector
operator|.
name|getThreadPool
argument_list|()
decl_stmt|;
if|if
condition|(
name|threadPool
operator|!=
literal|null
condition|)
block|{
name|threadPool
operator|.
name|setMaxStopTimeMs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSessionIdManager
argument_list|(
operator|new
name|HashSessionIdManager
argument_list|(
operator|new
name|Random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|stopAtShutdown
condition|)
block|{
for|for
control|(
name|Connector
name|connector
range|:
name|server
operator|.
name|getConnectors
argument_list|()
control|)
block|{
if|if
condition|(
name|connector
operator|instanceof
name|SocketConnector
condition|)
block|{
name|QueuedThreadPool
name|threadPool
init|=
call|(
name|QueuedThreadPool
call|)
argument_list|(
operator|(
name|SocketConnector
operator|)
name|connector
argument_list|)
operator|.
name|getThreadPool
argument_list|()
decl_stmt|;
if|if
condition|(
name|threadPool
operator|!=
literal|null
condition|)
block|{
name|threadPool
operator|.
name|setMaxStopTimeMs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// Initialize the servlets
specifier|final
name|Context
name|root
init|=
operator|new
name|Context
argument_list|(
name|server
argument_list|,
name|context
argument_list|,
name|Context
operator|.
name|SESSIONS
argument_list|)
decl_stmt|;
name|server
operator|.
name|addLifeCycleListener
argument_list|(
operator|new
name|LifeCycle
operator|.
name|Listener
argument_list|()
block|{
specifier|public
name|void
name|lifeCycleStopping
parameter_list|(
name|LifeCycle
name|arg0
parameter_list|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|lifeCycleStopped
parameter_list|(
name|LifeCycle
name|arg0
parameter_list|)
block|{}
specifier|public
name|void
name|lifeCycleStarting
parameter_list|(
name|LifeCycle
name|arg0
parameter_list|)
block|{
synchronized|synchronized
init|(
name|JettySolrRunner
operator|.
name|this
init|)
block|{
name|waitOnSolr
operator|=
literal|true
expr_stmt|;
name|JettySolrRunner
operator|.
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|lifeCycleStarted
parameter_list|(
name|LifeCycle
name|arg0
parameter_list|)
block|{
name|lastPort
operator|=
name|getFirstConnectorPort
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|lastPort
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrConfigFilename
operator|!=
literal|null
condition|)
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrconfig"
argument_list|,
name|solrConfigFilename
argument_list|)
expr_stmt|;
if|if
condition|(
name|schemaFilename
operator|!=
literal|null
condition|)
name|System
operator|.
name|setProperty
argument_list|(
literal|"schema"
argument_list|,
name|schemaFilename
argument_list|)
expr_stmt|;
comment|//        SolrDispatchFilter filter = new SolrDispatchFilter();
comment|//        FilterHolder fh = new FilterHolder(filter);
name|dispatchFilter
operator|=
name|root
operator|.
name|addFilter
argument_list|(
name|SolrDispatchFilter
operator|.
name|class
argument_list|,
literal|"*"
argument_list|,
name|Handler
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrConfigFilename
operator|!=
literal|null
condition|)
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrconfig"
argument_list|)
expr_stmt|;
if|if
condition|(
name|schemaFilename
operator|!=
literal|null
condition|)
name|System
operator|.
name|clearProperty
argument_list|(
literal|"schema"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|lifeCycleFailure
parameter_list|(
name|LifeCycle
name|arg0
parameter_list|,
name|Throwable
name|arg1
parameter_list|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// for some reason, there must be a servlet for this to get applied
name|root
operator|.
name|addServlet
argument_list|(
name|Servlet404
operator|.
name|class
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
block|}
DECL|method|getDispatchFilter
specifier|public
name|FilterHolder
name|getDispatchFilter
parameter_list|()
block|{
return|return
name|dispatchFilter
return|;
block|}
DECL|method|isRunning
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|server
operator|.
name|isRunning
argument_list|()
return|;
block|}
DECL|method|isStopped
specifier|public
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|server
operator|.
name|isStopped
argument_list|()
return|;
block|}
comment|// ------------------------------------------------------------------------------------------------
comment|// ------------------------------------------------------------------------------------------------
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|boolean
name|waitForSolr
parameter_list|)
throws|throws
name|Exception
block|{
comment|// if started before, make a new server
if|if
condition|(
name|startedBefore
condition|)
block|{
name|waitOnSolr
operator|=
literal|false
expr_stmt|;
name|init
argument_list|(
name|solrHome
argument_list|,
name|context
argument_list|,
name|lastPort
argument_list|,
name|stopAtShutdown
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|startedBefore
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shards
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"shard"
argument_list|,
name|shards
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|server
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|JettySolrRunner
operator|.
name|this
init|)
block|{
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|waitOnSolr
condition|)
block|{
name|this
operator|.
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|cnt
operator|++
operator|==
literal|5
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Jetty/Solr unresponsive"
argument_list|)
throw|;
block|}
block|}
block|}
name|System
operator|.
name|clearProperty
argument_list|(
literal|"shard"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.data.dir"
argument_list|)
expr_stmt|;
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|server
operator|.
name|isStopped
argument_list|()
operator|&&
operator|!
name|server
operator|.
name|isStopping
argument_list|()
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the Local Port of the jetty Server.    *     * @exception RuntimeException if there is no Connector    */
DECL|method|getFirstConnectorPort
specifier|private
name|int
name|getFirstConnectorPort
parameter_list|()
block|{
name|Connector
index|[]
name|conns
init|=
name|server
operator|.
name|getConnectors
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|conns
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Jetty Server has no Connectors"
argument_list|)
throw|;
block|}
return|return
name|conns
index|[
literal|0
index|]
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
comment|/**    * Returns the Local Port of the jetty Server.    *     * @exception RuntimeException if there is no Connector    */
DECL|method|getLocalPort
specifier|public
name|int
name|getLocalPort
parameter_list|()
block|{
if|if
condition|(
name|lastPort
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You cannot get the port until this instance has started"
argument_list|)
throw|;
block|}
return|return
name|lastPort
return|;
block|}
comment|// --------------------------------------------------------------
comment|// --------------------------------------------------------------
comment|/**    * This is a stupid hack to give jetty something to attach to    */
DECL|class|Servlet404
specifier|public
specifier|static
class|class
name|Servlet404
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|service
specifier|public
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|res
operator|.
name|sendError
argument_list|(
literal|404
argument_list|,
literal|"Can not find: "
operator|+
name|req
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A main class that starts jetty+solr This is useful for debugging    */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
literal|"."
argument_list|,
literal|"/solr"
argument_list|,
literal|8983
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setShards
specifier|public
name|void
name|setShards
parameter_list|(
name|String
name|shardList
parameter_list|)
block|{
name|this
operator|.
name|shards
operator|=
name|shardList
expr_stmt|;
block|}
DECL|method|setDataDir
specifier|public
name|void
name|setDataDir
parameter_list|(
name|String
name|dataDir
parameter_list|)
block|{
name|this
operator|.
name|dataDir
operator|=
name|dataDir
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|NoLog
class|class
name|NoLog
implements|implements
name|Logger
block|{
DECL|field|debug
specifier|private
specifier|static
name|boolean
name|debug
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"DEBUG"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|NoLog
specifier|public
name|NoLog
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|NoLog
specifier|public
name|NoLog
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
operator|==
literal|null
condition|?
literal|""
else|:
name|name
expr_stmt|;
block|}
DECL|method|isDebugEnabled
specifier|public
name|boolean
name|isDebugEnabled
parameter_list|()
block|{
return|return
name|debug
return|;
block|}
DECL|method|setDebugEnabled
specifier|public
name|void
name|setDebugEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|debug
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|info
specifier|public
name|void
name|info
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{   }
DECL|method|debug
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{   }
DECL|method|debug
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{   }
DECL|method|warn
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{   }
DECL|method|warn
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{   }
DECL|method|getLogger
specifier|public
name|Logger
name|getLogger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|(
name|name
operator|==
literal|null
operator|&&
name|this
operator|.
name|name
operator|==
literal|null
operator|)
operator|||
operator|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|this
operator|.
name|name
argument_list|)
operator|)
condition|)
return|return
name|this
return|;
return|return
operator|new
name|NoLog
argument_list|(
name|name
argument_list|)
return|;
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
literal|"NOLOG["
operator|+
name|name
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

