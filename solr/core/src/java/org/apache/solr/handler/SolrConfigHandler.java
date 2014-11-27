begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|text
operator|.
name|MessageFormat
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
name|Arrays
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
name|HashSet
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
name|Set
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
name|ZkController
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
name|ZkSolrResourceLoader
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
name|cloud
operator|.
name|ZkNodeProps
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
name|CollectionParams
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
name|CoreAdminParams
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
name|MapSolrParams
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
name|ContentStream
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
name|core
operator|.
name|ConfigOverlay
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
name|core
operator|.
name|SolrConfig
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|SchemaManager
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
name|CommandOperation
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
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
name|text
operator|.
name|MessageFormat
operator|.
name|format
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
name|singletonList
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

begin_import
import|import static
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
name|ZkNodeProps
operator|.
name|makeMap
import|;
end_import

begin_import
import|import static
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
name|CoreAdminParams
operator|.
name|NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|ConfigOverlay
operator|.
name|NOT_EDITABLE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|PluginInfo
operator|.
name|DEFAULTS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
operator|.
name|CLASS_NAME
import|;
end_import

begin_class
DECL|class|SolrConfigHandler
specifier|public
class|class
name|SolrConfigHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrConfigHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|configEditing_disabled
specifier|public
specifier|static
specifier|final
name|boolean
name|configEditing_disabled
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"disable.configEdit"
argument_list|)
decl_stmt|;
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
name|setWt
argument_list|(
name|req
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|String
name|httpMethod
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"httpMethod"
argument_list|)
decl_stmt|;
name|Command
name|command
init|=
operator|new
name|Command
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|httpMethod
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"POST"
operator|.
name|equals
argument_list|(
name|httpMethod
argument_list|)
condition|)
block|{
if|if
condition|(
name|configEditing_disabled
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|" solrconfig editing is not enabled"
argument_list|)
throw|;
name|command
operator|.
name|handlePOST
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|command
operator|.
name|handleGET
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|instanceof
name|ZkSolrResourceLoader
operator|)
condition|)
return|return;
specifier|final
name|ZkSolrResourceLoader
name|zkSolrResourceLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|zkSolrResourceLoader
operator|!=
literal|null
condition|)
name|zkSolrResourceLoader
operator|.
name|getZkController
argument_list|()
operator|.
name|registerConfListenerForCore
argument_list|(
name|zkSolrResourceLoader
operator|.
name|getConfigSetZkPath
argument_list|()
argument_list|,
name|core
argument_list|,
name|getListener
argument_list|(
name|core
argument_list|,
name|zkSolrResourceLoader
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getListener
specifier|private
specifier|static
name|Runnable
name|getListener
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|ZkSolrResourceLoader
name|zkSolrResourceLoader
parameter_list|)
block|{
specifier|final
name|String
name|coreName
init|=
name|core
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|CoreContainer
name|cc
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
specifier|final
name|String
name|overlayPath
init|=
operator|(
name|zkSolrResourceLoader
operator|)
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|ConfigOverlay
operator|.
name|RESOURCE_NAME
decl_stmt|;
specifier|final
name|String
name|solrConfigPath
init|=
operator|(
name|zkSolrResourceLoader
operator|)
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"config update listener called for core {}"
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|cc
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|int
name|solrConfigversion
decl_stmt|,
name|overlayVersion
decl_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|cc
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
init|)
block|{
if|if
condition|(
name|core
operator|.
name|isClosed
argument_list|()
condition|)
return|return;
name|solrConfigversion
operator|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getOverlay
argument_list|()
operator|.
name|getZnodeVersion
argument_list|()
expr_stmt|;
name|overlayVersion
operator|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getZnodeVersion
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|checkStale
argument_list|(
name|zkClient
argument_list|,
name|overlayPath
argument_list|,
name|solrConfigversion
argument_list|)
operator|||
name|checkStale
argument_list|(
name|zkClient
argument_list|,
name|solrConfigPath
argument_list|,
name|overlayVersion
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"core reload {}"
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|checkStale
specifier|private
specifier|static
name|boolean
name|checkStale
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|zkPath
parameter_list|,
name|int
name|currentVersion
parameter_list|)
block|{
try|try
block|{
name|Stat
name|stat
init|=
name|zkClient
operator|.
name|exists
argument_list|(
name|zkPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|currentVersion
operator|>
operator|-
literal|1
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|stat
operator|.
name|getVersion
argument_list|()
operator|>
name|currentVersion
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|zkPath
operator|+
literal|" is stale will need an update from {} to {}"
argument_list|,
name|currentVersion
argument_list|,
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|nne
parameter_list|)
block|{
comment|//no problem
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"error refreshing solrconfig "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|class|Command
specifier|private
specifier|static
class|class
name|Command
block|{
DECL|field|req
specifier|private
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|resp
specifier|private
specifier|final
name|SolrQueryResponse
name|resp
decl_stmt|;
DECL|field|method
specifier|private
specifier|final
name|String
name|method
decl_stmt|;
DECL|method|Command
specifier|private
name|Command
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|resp
parameter_list|,
name|String
name|httpMethod
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|resp
operator|=
name|resp
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|httpMethod
expr_stmt|;
block|}
DECL|method|handleGET
specifier|private
name|void
name|handleGET
parameter_list|()
block|{
name|String
name|path
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
name|path
operator|=
literal|"/config"
expr_stmt|;
if|if
condition|(
literal|"/config/overlay"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|resp
operator|.
name|add
argument_list|(
literal|"overlay"
argument_list|,
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getOverlay
argument_list|()
operator|.
name|toMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
name|parts
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|resp
operator|.
name|add
argument_list|(
literal|"solrConfig"
argument_list|,
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|toMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|toMap
argument_list|()
decl_stmt|;
name|resp
operator|.
name|add
argument_list|(
literal|"solrConfig"
argument_list|,
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handlePOST
specifier|private
name|void
name|handlePOST
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
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
literal|"missing content stream"
argument_list|)
throw|;
block|}
name|ArrayList
argument_list|<
name|CommandOperation
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContentStream
name|stream
range|:
name|streams
control|)
name|ops
operator|.
name|addAll
argument_list|(
name|CommandOperation
operator|.
name|parse
argument_list|(
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|>
name|errList
init|=
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|ops
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resp
operator|.
name|add
argument_list|(
name|CommandOperation
operator|.
name|ERR_MSGS
argument_list|,
name|errList
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|ArrayList
argument_list|<
name|CommandOperation
argument_list|>
name|opsCopy
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ops
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ConfigOverlay
name|overlay
init|=
name|SolrConfig
operator|.
name|getConfigOverlay
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CommandOperation
name|op
range|:
name|ops
control|)
name|opsCopy
operator|.
name|add
argument_list|(
name|op
operator|.
name|getCopy
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|handleCommands
argument_list|(
name|opsCopy
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
comment|//succeeded . so no need to go over the loop again
block|}
catch|catch
parameter_list|(
name|ZkController
operator|.
name|ResourceModifiedInZkException
name|e
parameter_list|)
block|{
comment|//retry
name|log
operator|.
name|info
argument_list|(
literal|"Race condition, the node is modified in ZK by someone else "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|resp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|resp
operator|.
name|add
argument_list|(
name|CommandOperation
operator|.
name|ERR_MSGS
argument_list|,
name|singletonList
argument_list|(
name|SchemaManager
operator|.
name|getErrorStr
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleCommands
specifier|private
name|void
name|handleCommands
parameter_list|(
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|ops
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|CommandOperation
name|op
range|:
name|ops
control|)
block|{
switch|switch
condition|(
name|op
operator|.
name|name
condition|)
block|{
case|case
name|SET_PROPERTY
case|:
name|overlay
operator|=
name|applySetProp
argument_list|(
name|op
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
case|case
name|UNSET_PROPERTY
case|:
name|overlay
operator|=
name|applyUnset
argument_list|(
name|op
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
case|case
name|SET_USER_PROPERTY
case|:
name|overlay
operator|=
name|applySetUserProp
argument_list|(
name|op
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
case|case
name|UNSET_USER_PROPERTY
case|:
name|overlay
operator|=
name|applyUnsetUserProp
argument_list|(
name|op
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
case|case
name|UPDATE_REQHANDLER
case|:
case|case
name|CREATE_REQHANDLER
case|:
name|overlay
operator|=
name|applyRequestHandler
argument_list|(
name|op
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE_REQHANDLER
case|:
name|overlay
operator|=
name|applyDeleteHandler
argument_list|(
name|op
argument_list|,
name|overlay
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|List
name|errs
init|=
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|ops
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resp
operator|.
name|add
argument_list|(
name|CommandOperation
operator|.
name|ERR_MSGS
argument_list|,
name|errs
argument_list|)
expr_stmt|;
return|return;
block|}
name|SolrResourceLoader
name|loader
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|ZkController
operator|.
name|persistConfigResourceToZooKeeper
argument_list|(
name|loader
argument_list|,
name|overlay
operator|.
name|getZnodeVersion
argument_list|()
argument_list|,
name|ConfigOverlay
operator|.
name|RESOURCE_NAME
argument_list|,
name|overlay
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SolrResourceLoader
operator|.
name|persistConfLocally
argument_list|(
name|loader
argument_list|,
name|ConfigOverlay
operator|.
name|RESOURCE_NAME
argument_list|,
name|overlay
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|applyDeleteHandler
specifier|private
name|ConfigOverlay
name|applyDeleteHandler
parameter_list|(
name|CommandOperation
name|op
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
block|{
name|String
name|name
init|=
name|op
operator|.
name|getStr
argument_list|(
name|CommandOperation
operator|.
name|ROOT_OBJ
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
name|overlay
return|;
if|if
condition|(
name|overlay
operator|.
name|getReqHandlers
argument_list|()
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|overlay
operator|.
name|deleteHandler
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
name|op
operator|.
name|addError
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"NO such requestHandler ''{0}'' "
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|overlay
return|;
block|}
block|}
DECL|method|applyRequestHandler
specifier|private
name|ConfigOverlay
name|applyRequestHandler
parameter_list|(
name|CommandOperation
name|op
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
block|{
name|String
name|name
init|=
name|op
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
name|op
operator|.
name|getStr
argument_list|(
name|CLASS_NAME
argument_list|)
expr_stmt|;
name|op
operator|.
name|getMap
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|op
operator|.
name|getMap
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|op
operator|.
name|getMap
argument_list|(
name|PluginInfo
operator|.
name|APPENDS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
name|overlay
return|;
if|if
condition|(
name|CREATE_REQHANDLER
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|overlay
operator|.
name|getReqHandlers
argument_list|()
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|" ''{0}'' already exists . Do an ''{1}'' , if you want to change it "
argument_list|,
name|name
argument_list|,
name|UPDATE_REQHANDLER
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|overlay
return|;
block|}
else|else
block|{
return|return
name|overlay
operator|.
name|addReqHandler
argument_list|(
name|op
operator|.
name|getDataMap
argument_list|()
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|UPDATE_REQHANDLER
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|overlay
operator|.
name|getReqHandlers
argument_list|()
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|" ''{0}'' does not exist . Do an ''{1}'' , if you want to create it "
argument_list|,
name|name
argument_list|,
name|CREATE_REQHANDLER
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|overlay
return|;
block|}
else|else
block|{
return|return
name|overlay
operator|.
name|addReqHandler
argument_list|(
name|op
operator|.
name|getDataMap
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|overlay
return|;
block|}
DECL|method|applySetUserProp
specifier|private
name|ConfigOverlay
name|applySetUserProp
parameter_list|(
name|CommandOperation
name|op
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|op
operator|.
name|getDataMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
name|overlay
return|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|val
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|overlay
operator|=
name|overlay
operator|.
name|setUserProperty
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|overlay
return|;
block|}
DECL|method|applyUnsetUserProp
specifier|private
name|ConfigOverlay
name|applyUnsetUserProp
parameter_list|(
name|CommandOperation
name|op
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|name
init|=
name|op
operator|.
name|getStrs
argument_list|(
name|CommandOperation
operator|.
name|ROOT_OBJ
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
name|overlay
return|;
for|for
control|(
name|String
name|o
range|:
name|name
control|)
block|{
if|if
condition|(
operator|!
name|overlay
operator|.
name|getUserProps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|format
argument_list|(
literal|"No such property ''{0}''"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|overlay
operator|=
name|overlay
operator|.
name|unsetUserProperty
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|overlay
return|;
block|}
DECL|method|applyUnset
specifier|private
name|ConfigOverlay
name|applyUnset
parameter_list|(
name|CommandOperation
name|op
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|name
init|=
name|op
operator|.
name|getStrs
argument_list|(
name|CommandOperation
operator|.
name|ROOT_OBJ
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
name|overlay
return|;
for|for
control|(
name|String
name|o
range|:
name|name
control|)
block|{
if|if
condition|(
operator|!
name|ConfigOverlay
operator|.
name|isEditableProp
argument_list|(
name|o
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|format
argument_list|(
name|NOT_EDITABLE
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|overlay
operator|=
name|overlay
operator|.
name|unsetProperty
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|overlay
return|;
block|}
DECL|method|applySetProp
specifier|private
name|ConfigOverlay
name|applySetProp
parameter_list|(
name|CommandOperation
name|op
parameter_list|,
name|ConfigOverlay
name|overlay
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|op
operator|.
name|getDataMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
name|overlay
return|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|val
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ConfigOverlay
operator|.
name|isEditableProp
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|format
argument_list|(
name|NOT_EDITABLE
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|overlay
operator|=
name|overlay
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|overlay
return|;
block|}
block|}
DECL|method|setWt
specifier|static
name|void
name|setWt
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|wt
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
operator|!=
literal|null
condition|)
return|return;
comment|//wt is set by user
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
name|wt
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|params
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubHandler
specifier|public
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|subPaths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
return|return
name|this
return|;
return|return
literal|null
return|;
block|}
DECL|field|subPaths
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|subPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"/overlay"
argument_list|,
literal|"/query"
argument_list|,
literal|"/jmx"
argument_list|,
literal|"/requestDispatcher"
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
for|for
control|(
name|SolrConfig
operator|.
name|SolrPluginInfo
name|solrPluginInfo
range|:
name|SolrConfig
operator|.
name|plugins
control|)
name|subPaths
operator|.
name|add
argument_list|(
literal|"/"
operator|+
name|solrPluginInfo
operator|.
name|tag
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Edit solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|OTHER
return|;
block|}
DECL|field|SET_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|SET_PROPERTY
init|=
literal|"set-property"
decl_stmt|;
DECL|field|UNSET_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|UNSET_PROPERTY
init|=
literal|"unset-property"
decl_stmt|;
DECL|field|SET_USER_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|SET_USER_PROPERTY
init|=
literal|"set-user-property"
decl_stmt|;
DECL|field|UNSET_USER_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|UNSET_USER_PROPERTY
init|=
literal|"unset-user-property"
decl_stmt|;
DECL|field|CREATE_REQHANDLER
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_REQHANDLER
init|=
literal|"create-requesthandler"
decl_stmt|;
DECL|field|DELETE_REQHANDLER
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_REQHANDLER
init|=
literal|"delete-requesthandler"
decl_stmt|;
DECL|field|UPDATE_REQHANDLER
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_REQHANDLER
init|=
literal|"update-requesthandler"
decl_stmt|;
block|}
end_class

end_unit

