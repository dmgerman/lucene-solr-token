begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SolrResponse
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
name|SolrServerException
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
name|HttpSolrServer
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
name|CoreAdminRequest
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
name|CoreAdminRequest
operator|.
name|RequestSyncShard
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
name|DistributedQueue
operator|.
name|QueueEvent
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
name|Overseer
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
name|OverseerCollectionProcessor
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
name|ClusterState
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
name|ImplicitDocRouter
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
name|ZkCoreNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
operator|.
name|CollectionAction
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
name|zookeeper
operator|.
name|KeeperException
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|Overseer
operator|.
name|QUEUE_OPERATION
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|COLL_CONF
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|CREATESHARD
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|CREATE_NODE_SET
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|MAX_SHARDS_PER_NODE
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|NUM_SLICES
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|REPLICATION_FACTOR
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|ROUTER
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|SHARDS_PROP
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
name|DocRouter
operator|.
name|ROUTE_FIELD
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
name|ZkStateReader
operator|.
name|COLLECTION_PROP
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
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
import|;
end_import

begin_class
DECL|class|CollectionsHandler
specifier|public
class|class
name|CollectionsHandler
extends|extends
name|RequestHandlerBase
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
name|CollectionsHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|coreContainer
specifier|protected
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|method|CollectionsHandler
specifier|public
name|CollectionsHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// Unlike most request handlers, CoreContainer initialization
comment|// should happen in the constructor...
name|this
operator|.
name|coreContainer
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Overloaded ctor to inject CoreContainer into the handler.    *    * @param coreContainer Core Container of the solr webapp installed.    */
DECL|method|CollectionsHandler
specifier|public
name|CollectionsHandler
parameter_list|(
specifier|final
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|final
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{    }
comment|/**    * The instance of CoreContainer this handler handles. This should be the CoreContainer instance that created this    * handler.    *    * @return a CoreContainer instance    */
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|coreContainer
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
comment|// Make sure the cores is enabled
name|CoreContainer
name|cores
init|=
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
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
literal|"Core container instance missing"
argument_list|)
throw|;
block|}
comment|// Pick the action
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|CollectionAction
name|action
init|=
literal|null
decl_stmt|;
name|String
name|a
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|action
operator|=
name|CollectionAction
operator|.
name|get
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown action: "
operator|+
name|a
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|CREATE
case|:
block|{
name|this
operator|.
name|handleCreateAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|DELETE
case|:
block|{
name|this
operator|.
name|handleDeleteAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|RELOAD
case|:
block|{
name|this
operator|.
name|handleReloadAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|SYNCSHARD
case|:
block|{
name|this
operator|.
name|handleSyncShardAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|CREATEALIAS
case|:
block|{
name|this
operator|.
name|handleCreateAliasAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|DELETEALIAS
case|:
block|{
name|this
operator|.
name|handleDeleteAliasAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|SPLITSHARD
case|:
block|{
name|this
operator|.
name|handleSplitShardAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|DELETESHARD
case|:
block|{
name|this
operator|.
name|handleDeleteShardAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|CREATESHARD
case|:
block|{
name|this
operator|.
name|handleCreateShard
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown action: "
operator|+
name|action
argument_list|)
throw|;
block|}
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|field|DEFAULT_ZK_TIMEOUT
specifier|public
specifier|static
name|long
name|DEFAULT_ZK_TIMEOUT
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|method|handleResponse
specifier|private
name|void
name|handleResponse
parameter_list|(
name|String
name|operation
parameter_list|,
name|ZkNodeProps
name|m
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|handleResponse
argument_list|(
name|operation
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|,
name|DEFAULT_ZK_TIMEOUT
argument_list|)
expr_stmt|;
block|}
DECL|method|handleResponse
specifier|private
name|void
name|handleResponse
parameter_list|(
name|String
name|operation
parameter_list|,
name|ZkNodeProps
name|m
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|QueueEvent
name|event
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerCollectionQueue
argument_list|()
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getBytes
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SolrResponse
name|response
init|=
name|SolrResponse
operator|.
name|deserialize
argument_list|(
name|event
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|exp
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"exception"
argument_list|)
decl_stmt|;
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|Integer
name|code
init|=
operator|(
name|Integer
operator|)
name|exp
operator|.
name|get
argument_list|(
literal|"rspCode"
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|code
operator|!=
literal|null
operator|&&
name|code
operator|!=
operator|-
literal|1
condition|?
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|code
argument_list|)
else|:
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
operator|(
name|String
operator|)
name|exp
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
operator|>=
name|timeout
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|operation
operator|+
literal|" the collection time out:"
operator|+
name|timeout
operator|/
literal|1000
operator|+
literal|"s"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|operation
operator|+
literal|" the collection error [Watcher fired on path: "
operator|+
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|.
name|getPath
argument_list|()
operator|+
literal|" state: "
operator|+
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|.
name|getState
argument_list|()
operator|+
literal|" type "
operator|+
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|.
name|getType
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|operation
operator|+
literal|" the collection unkown case"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|handleReloadAction
specifier|private
name|void
name|handleReloadAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Reloading Collection : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|RELOADCOLLECTION
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|RELOADCOLLECTION
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|handleSyncShardAction
specifier|private
name|void
name|handleSyncShardAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|SolrServerException
throws|,
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Syncing shard : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|collection
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|)
decl_stmt|;
name|String
name|shard
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"shard"
argument_list|)
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|ZkNodeProps
name|leaderProps
init|=
name|clusterState
operator|.
name|getLeader
argument_list|(
name|collection
argument_list|,
name|shard
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|nodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|leaderProps
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|RequestSyncShard
name|reqSyncShard
init|=
operator|new
name|CoreAdminRequest
operator|.
name|RequestSyncShard
argument_list|()
decl_stmt|;
name|reqSyncShard
operator|.
name|setCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|reqSyncShard
operator|.
name|setShard
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|reqSyncShard
operator|.
name|setCoreName
argument_list|(
name|nodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|reqSyncShard
argument_list|)
expr_stmt|;
block|}
DECL|method|handleCreateAliasAction
specifier|private
name|void
name|handleCreateAliasAction
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
name|log
operator|.
name|info
argument_list|(
literal|"Create alias action : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|collections
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|CREATEALIAS
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|,
literal|"collections"
argument_list|,
name|collections
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|CREATEALIAS
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|handleDeleteAliasAction
specifier|private
name|void
name|handleDeleteAliasAction
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
name|log
operator|.
name|info
argument_list|(
literal|"Delete alias action : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|DELETEALIAS
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|CREATEALIAS
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|handleDeleteAction
specifier|private
name|void
name|handleDeleteAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting Collection : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|DELETECOLLECTION
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|DELETECOLLECTION
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
comment|// very simple currently, you can pass a template collection, and the new collection is created on
comment|// every node the template collection is on
comment|// there is a lot more to add - you should also be able to create with an explicit server list
comment|// we might also want to think about error handling (add the request to a zk queue and involve overseer?)
comment|// as well as specific replicas= options
DECL|method|handleCreateAction
specifier|private
name|void
name|handleCreateAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating Collection : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Collection name is required to create a new collection"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Collection name is required to create a new collection"
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|CREATECOLLECTION
argument_list|)
expr_stmt|;
name|copyIfNotNull
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
name|props
argument_list|,
literal|"name"
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|COLL_CONF
argument_list|,
name|NUM_SLICES
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|,
name|CREATE_NODE_SET
argument_list|,
name|ROUTER
argument_list|,
name|SHARDS_PROP
argument_list|,
name|ROUTE_FIELD
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|CREATECOLLECTION
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|handleCreateShard
specifier|private
name|void
name|handleCreateShard
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Create shard: "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|check
argument_list|(
name|COLLECTION_PROP
argument_list|,
name|SHARD_ID_PROP
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ImplicitDocRouter
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|clusterState
operator|.
name|getCollection
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|COLLECTION_PROP
argument_list|)
argument_list|)
operator|.
name|getStr
argument_list|(
name|ROUTER
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"shards can be added only to 'implicit' collections"
argument_list|)
throw|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|OverseerCollectionProcessor
operator|.
name|asMap
argument_list|(
name|QUEUE_OPERATION
argument_list|,
name|CREATESHARD
argument_list|)
decl_stmt|;
name|copyIfNotNull
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
name|map
argument_list|,
name|COLLECTION_PROP
argument_list|,
name|SHARD_ID_PROP
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|CREATE_NODE_SET
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|CREATESHARD
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|copyIfNotNull
specifier|private
specifier|static
name|void
name|copyIfNotNull
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|String
modifier|...
name|keys
parameter_list|)
block|{
if|if
condition|(
name|keys
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|String
name|v
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|props
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handleDeleteShardAction
specifier|private
name|void
name|handleDeleteShardAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting Shard : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|)
decl_stmt|;
name|String
name|shard
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"shard"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"collection"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|DELETESHARD
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|DELETESHARD
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|handleSplitShardAction
specifier|private
name|void
name|handleSplitShardAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Splitting shard : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|)
decl_stmt|;
comment|// TODO : add support for multiple shards
name|String
name|shard
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"shard"
argument_list|)
decl_stmt|;
comment|// TODO : add support for shard range
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|SPLITSHARD
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"collection"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|SPLITSHARD
argument_list|,
name|m
argument_list|,
name|rsp
argument_list|,
name|DEFAULT_ZK_TIMEOUT
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|params
specifier|public
specifier|static
name|ModifiableSolrParams
name|params
parameter_list|(
name|String
modifier|...
name|params
parameter_list|)
block|{
name|ModifiableSolrParams
name|msp
init|=
operator|new
name|ModifiableSolrParams
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|msp
operator|.
name|add
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|msp
return|;
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
literal|"Manage SolrCloud Collections"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL: https://svn.apache.org/repos/asf/lucene/dev/trunk/solr/core/src/java/org/apache/solr/handler/admin/CollectionHandler.java $"
return|;
block|}
block|}
end_class

end_unit

