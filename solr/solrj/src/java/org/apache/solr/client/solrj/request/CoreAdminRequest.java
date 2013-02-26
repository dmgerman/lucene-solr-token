begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
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
name|Collection
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
name|Arrays
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
name|SolrRequest
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
name|SolrServer
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
name|response
operator|.
name|CoreAdminResponse
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
name|params
operator|.
name|CoreAdminParams
operator|.
name|CoreAdminAction
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

begin_comment
comment|/**  * This class is experimental and subject to change.  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|CoreAdminRequest
specifier|public
class|class
name|CoreAdminRequest
extends|extends
name|SolrRequest
block|{
DECL|field|core
specifier|protected
name|String
name|core
init|=
literal|null
decl_stmt|;
DECL|field|other
specifier|protected
name|String
name|other
init|=
literal|null
decl_stmt|;
DECL|field|isIndexInfoNeeded
specifier|protected
name|boolean
name|isIndexInfoNeeded
init|=
literal|true
decl_stmt|;
DECL|field|action
specifier|protected
name|CoreAdminParams
operator|.
name|CoreAdminAction
name|action
init|=
literal|null
decl_stmt|;
comment|//a create core request
DECL|class|Create
specifier|public
specifier|static
class|class
name|Create
extends|extends
name|CoreAdminRequest
block|{
DECL|field|instanceDir
specifier|protected
name|String
name|instanceDir
decl_stmt|;
DECL|field|configName
specifier|protected
name|String
name|configName
init|=
literal|null
decl_stmt|;
DECL|field|schemaName
specifier|protected
name|String
name|schemaName
init|=
literal|null
decl_stmt|;
DECL|field|dataDir
specifier|protected
name|String
name|dataDir
init|=
literal|null
decl_stmt|;
DECL|field|ulogDir
specifier|protected
name|String
name|ulogDir
init|=
literal|null
decl_stmt|;
DECL|field|collection
specifier|protected
name|String
name|collection
decl_stmt|;
DECL|field|numShards
specifier|private
name|Integer
name|numShards
decl_stmt|;
DECL|field|shardId
specifier|private
name|String
name|shardId
decl_stmt|;
DECL|field|roles
specifier|private
name|String
name|roles
decl_stmt|;
DECL|field|coreNodeName
specifier|private
name|String
name|coreNodeName
decl_stmt|;
DECL|method|Create
specifier|public
name|Create
parameter_list|()
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|CREATE
expr_stmt|;
block|}
DECL|method|setInstanceDir
specifier|public
name|void
name|setInstanceDir
parameter_list|(
name|String
name|instanceDir
parameter_list|)
block|{
name|this
operator|.
name|instanceDir
operator|=
name|instanceDir
expr_stmt|;
block|}
DECL|method|setSchemaName
specifier|public
name|void
name|setSchemaName
parameter_list|(
name|String
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schemaName
operator|=
name|schema
expr_stmt|;
block|}
DECL|method|setConfigName
specifier|public
name|void
name|setConfigName
parameter_list|(
name|String
name|config
parameter_list|)
block|{
name|this
operator|.
name|configName
operator|=
name|config
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
DECL|method|setUlogDir
specifier|public
name|void
name|setUlogDir
parameter_list|(
name|String
name|ulogDir
parameter_list|)
block|{
name|this
operator|.
name|ulogDir
operator|=
name|ulogDir
expr_stmt|;
block|}
DECL|method|setCollection
specifier|public
name|void
name|setCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
DECL|method|setNumShards
specifier|public
name|void
name|setNumShards
parameter_list|(
name|int
name|numShards
parameter_list|)
block|{
name|this
operator|.
name|numShards
operator|=
name|numShards
expr_stmt|;
block|}
DECL|method|setShardId
specifier|public
name|void
name|setShardId
parameter_list|(
name|String
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
DECL|method|setRoles
specifier|public
name|void
name|setRoles
parameter_list|(
name|String
name|roles
parameter_list|)
block|{
name|this
operator|.
name|roles
operator|=
name|roles
expr_stmt|;
block|}
DECL|method|setCoreNodeName
specifier|public
name|void
name|setCoreNodeName
parameter_list|(
name|String
name|coreNodeName
parameter_list|)
block|{
name|this
operator|.
name|coreNodeName
operator|=
name|coreNodeName
expr_stmt|;
block|}
DECL|method|getInstanceDir
specifier|public
name|String
name|getInstanceDir
parameter_list|()
block|{
return|return
name|instanceDir
return|;
block|}
DECL|method|getSchemaName
specifier|public
name|String
name|getSchemaName
parameter_list|()
block|{
return|return
name|schemaName
return|;
block|}
DECL|method|getConfigName
specifier|public
name|String
name|getConfigName
parameter_list|()
block|{
return|return
name|configName
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
return|;
block|}
DECL|method|getUlogDir
specifier|public
name|String
name|getUlogDir
parameter_list|()
block|{
return|return
name|ulogDir
return|;
block|}
DECL|method|getCollection
specifier|public
name|String
name|getCollection
parameter_list|()
block|{
return|return
name|collection
return|;
block|}
DECL|method|getShardId
specifier|public
name|String
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|getRoles
specifier|public
name|String
name|getRoles
parameter_list|()
block|{
return|return
name|roles
return|;
block|}
DECL|method|getCoreNodeName
specifier|public
name|String
name|getCoreNodeName
parameter_list|()
block|{
return|return
name|coreNodeName
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|equals
argument_list|(
name|CoreAdminAction
operator|.
name|CREATE
argument_list|)
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|configName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
name|configName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|schemaName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
name|schemaName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ulogDir
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ULOG_DIR
argument_list|,
name|ulogDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|COLLECTION
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numShards
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
name|numShards
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardId
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|SHARD
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|roles
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ROLES
argument_list|,
name|roles
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|coreNodeName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE_NODE_NAME
argument_list|,
name|coreNodeName
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
block|}
DECL|class|WaitForState
specifier|public
specifier|static
class|class
name|WaitForState
extends|extends
name|CoreAdminRequest
block|{
DECL|field|nodeName
specifier|protected
name|String
name|nodeName
decl_stmt|;
DECL|field|coreNodeName
specifier|protected
name|String
name|coreNodeName
decl_stmt|;
DECL|field|state
specifier|protected
name|String
name|state
decl_stmt|;
DECL|field|checkLive
specifier|protected
name|Boolean
name|checkLive
decl_stmt|;
DECL|field|onlyIfLeader
specifier|protected
name|Boolean
name|onlyIfLeader
decl_stmt|;
DECL|method|WaitForState
specifier|public
name|WaitForState
parameter_list|()
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|PREPRECOVERY
expr_stmt|;
block|}
DECL|method|setNodeName
specifier|public
name|void
name|setNodeName
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
block|}
DECL|method|getNodeName
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
DECL|method|getCoreNodeName
specifier|public
name|String
name|getCoreNodeName
parameter_list|()
block|{
return|return
name|coreNodeName
return|;
block|}
DECL|method|setCoreNodeName
specifier|public
name|void
name|setCoreNodeName
parameter_list|(
name|String
name|coreNodeName
parameter_list|)
block|{
name|this
operator|.
name|coreNodeName
operator|=
name|coreNodeName
expr_stmt|;
block|}
DECL|method|getState
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState
specifier|public
name|void
name|setState
parameter_list|(
name|String
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getCheckLive
specifier|public
name|Boolean
name|getCheckLive
parameter_list|()
block|{
return|return
name|checkLive
return|;
block|}
DECL|method|setCheckLive
specifier|public
name|void
name|setCheckLive
parameter_list|(
name|Boolean
name|checkLive
parameter_list|)
block|{
name|this
operator|.
name|checkLive
operator|=
name|checkLive
expr_stmt|;
block|}
DECL|method|isOnlyIfLeader
specifier|public
name|boolean
name|isOnlyIfLeader
parameter_list|()
block|{
return|return
name|onlyIfLeader
return|;
block|}
DECL|method|setOnlyIfLeader
specifier|public
name|void
name|setOnlyIfLeader
parameter_list|(
name|boolean
name|onlyIfLeader
parameter_list|)
block|{
name|this
operator|.
name|onlyIfLeader
operator|=
name|onlyIfLeader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"nodeName"
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|coreNodeName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"coreNodeName"
argument_list|,
name|coreNodeName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"state"
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkLive
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"checkLive"
argument_list|,
name|checkLive
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|onlyIfLeader
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"onlyIfLeader"
argument_list|,
name|onlyIfLeader
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
block|}
DECL|class|RequestRecovery
specifier|public
specifier|static
class|class
name|RequestRecovery
extends|extends
name|CoreAdminRequest
block|{
DECL|method|RequestRecovery
specifier|public
name|RequestRecovery
parameter_list|()
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|REQUESTRECOVERY
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
DECL|class|RequestSyncShard
specifier|public
specifier|static
class|class
name|RequestSyncShard
extends|extends
name|CoreAdminRequest
block|{
DECL|field|shard
specifier|private
name|String
name|shard
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
decl_stmt|;
DECL|method|RequestSyncShard
specifier|public
name|RequestSyncShard
parameter_list|()
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|REQUESTSYNCSHARD
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"shard"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|method|getShard
specifier|public
name|String
name|getShard
parameter_list|()
block|{
return|return
name|shard
return|;
block|}
DECL|method|setShard
specifier|public
name|void
name|setShard
parameter_list|(
name|String
name|shard
parameter_list|)
block|{
name|this
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
block|}
DECL|method|getCollection
specifier|public
name|String
name|getCollection
parameter_list|()
block|{
return|return
name|collection
return|;
block|}
DECL|method|setCollection
specifier|public
name|void
name|setCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
block|}
block|}
comment|//a persist core request
DECL|class|Persist
specifier|public
specifier|static
class|class
name|Persist
extends|extends
name|CoreAdminRequest
block|{
DECL|field|fileName
specifier|protected
name|String
name|fileName
init|=
literal|null
decl_stmt|;
DECL|method|Persist
specifier|public
name|Persist
parameter_list|()
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|PERSIST
expr_stmt|;
block|}
DECL|method|setFileName
specifier|public
name|void
name|setFileName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|fileName
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getFileName
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|FILE
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
block|}
DECL|class|MergeIndexes
specifier|public
specifier|static
class|class
name|MergeIndexes
extends|extends
name|CoreAdminRequest
block|{
DECL|field|indexDirs
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|indexDirs
decl_stmt|;
DECL|field|srcCores
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|srcCores
decl_stmt|;
DECL|method|MergeIndexes
specifier|public
name|MergeIndexes
parameter_list|()
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|MERGEINDEXES
expr_stmt|;
block|}
DECL|method|setIndexDirs
specifier|public
name|void
name|setIndexDirs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|indexDirs
parameter_list|)
block|{
name|this
operator|.
name|indexDirs
operator|=
name|indexDirs
expr_stmt|;
block|}
DECL|method|getIndexDirs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getIndexDirs
parameter_list|()
block|{
return|return
name|indexDirs
return|;
block|}
DECL|method|getSrcCores
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSrcCores
parameter_list|()
block|{
return|return
name|srcCores
return|;
block|}
DECL|method|setSrcCores
specifier|public
name|void
name|setSrcCores
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|srcCores
parameter_list|)
block|{
name|this
operator|.
name|srcCores
operator|=
name|srcCores
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexDirs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|indexDir
range|:
name|indexDirs
control|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|srcCores
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|srcCore
range|:
name|srcCores
control|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|SRC_CORE
argument_list|,
name|srcCore
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|params
return|;
block|}
block|}
DECL|class|Unload
specifier|public
specifier|static
class|class
name|Unload
extends|extends
name|CoreAdminRequest
block|{
DECL|field|deleteIndex
specifier|protected
name|boolean
name|deleteIndex
decl_stmt|;
DECL|method|Unload
specifier|public
name|Unload
parameter_list|(
name|boolean
name|deleteIndex
parameter_list|)
block|{
name|action
operator|=
name|CoreAdminAction
operator|.
name|UNLOAD
expr_stmt|;
name|this
operator|.
name|deleteIndex
operator|=
name|deleteIndex
expr_stmt|;
block|}
DECL|method|isDeleteIndex
specifier|public
name|boolean
name|isDeleteIndex
parameter_list|()
block|{
return|return
name|deleteIndex
return|;
block|}
DECL|method|setDeleteIndex
specifier|public
name|void
name|setDeleteIndex
parameter_list|(
name|boolean
name|deleteIndex
parameter_list|)
block|{
name|this
operator|.
name|deleteIndex
operator|=
name|deleteIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|super
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_INDEX
argument_list|,
name|deleteIndex
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
DECL|method|CoreAdminRequest
specifier|public
name|CoreAdminRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|"/admin/cores"
argument_list|)
expr_stmt|;
block|}
DECL|method|CoreAdminRequest
specifier|public
name|CoreAdminRequest
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|setCoreName
specifier|public
specifier|final
name|void
name|setCoreName
parameter_list|(
name|String
name|coreName
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|coreName
expr_stmt|;
block|}
DECL|method|setOtherCoreName
specifier|public
specifier|final
name|void
name|setOtherCoreName
parameter_list|(
name|String
name|otherCoreName
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|otherCoreName
expr_stmt|;
block|}
DECL|method|setIndexInfoNeeded
specifier|public
specifier|final
name|void
name|setIndexInfoNeeded
parameter_list|(
name|boolean
name|isIndexInfoNeeded
parameter_list|)
block|{
name|this
operator|.
name|isIndexInfoNeeded
operator|=
name|isIndexInfoNeeded
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
DECL|method|setAction
specifier|public
name|void
name|setAction
parameter_list|(
name|CoreAdminAction
name|action
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|INDEX_INFO
argument_list|,
operator|(
name|isIndexInfoNeeded
condition|?
literal|"true"
else|:
literal|"false"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|CoreAdminResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|CoreAdminResponse
name|res
init|=
operator|new
name|CoreAdminResponse
argument_list|()
decl_stmt|;
name|res
operator|.
name|setResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
DECL|method|reloadCore
specifier|public
specifier|static
name|CoreAdminResponse
name|reloadCore
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CoreAdminRequest
name|req
init|=
operator|new
name|CoreAdminRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|req
operator|.
name|setAction
argument_list|(
name|CoreAdminAction
operator|.
name|RELOAD
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|unloadCore
specifier|public
specifier|static
name|CoreAdminResponse
name|unloadCore
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|unloadCore
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|server
argument_list|)
return|;
block|}
DECL|method|unloadCore
specifier|public
specifier|static
name|CoreAdminResponse
name|unloadCore
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|deleteIndex
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|Unload
name|req
init|=
operator|new
name|Unload
argument_list|(
name|deleteIndex
argument_list|)
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|renameCore
specifier|public
specifier|static
name|CoreAdminResponse
name|renameCore
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|newName
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CoreAdminRequest
name|req
init|=
operator|new
name|CoreAdminRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
name|req
operator|.
name|setOtherCoreName
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|req
operator|.
name|setAction
argument_list|(
name|CoreAdminAction
operator|.
name|RENAME
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|getStatus
specifier|public
specifier|static
name|CoreAdminResponse
name|getStatus
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CoreAdminRequest
name|req
init|=
operator|new
name|CoreAdminRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|req
operator|.
name|setAction
argument_list|(
name|CoreAdminAction
operator|.
name|STATUS
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|createCore
specifier|public
specifier|static
name|CoreAdminResponse
name|createCore
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|CoreAdminRequest
operator|.
name|createCore
argument_list|(
name|name
argument_list|,
name|instanceDir
argument_list|,
name|server
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createCore
specifier|public
specifier|static
name|CoreAdminResponse
name|createCore
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|,
name|SolrServer
name|server
parameter_list|,
name|String
name|configFile
parameter_list|,
name|String
name|schemaFile
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|createCore
argument_list|(
name|name
argument_list|,
name|instanceDir
argument_list|,
name|server
argument_list|,
name|configFile
argument_list|,
name|schemaFile
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createCore
specifier|public
specifier|static
name|CoreAdminResponse
name|createCore
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|,
name|SolrServer
name|server
parameter_list|,
name|String
name|configFile
parameter_list|,
name|String
name|schemaFile
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|tlogDir
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CoreAdminRequest
operator|.
name|Create
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|req
operator|.
name|setInstanceDir
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|req
operator|.
name|setDataDir
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tlogDir
operator|!=
literal|null
condition|)
block|{
name|req
operator|.
name|setUlogDir
argument_list|(
name|tlogDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|configFile
operator|!=
literal|null
condition|)
block|{
name|req
operator|.
name|setConfigName
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|schemaFile
operator|!=
literal|null
condition|)
block|{
name|req
operator|.
name|setSchemaName
argument_list|(
name|schemaFile
argument_list|)
expr_stmt|;
block|}
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|persist
specifier|public
specifier|static
name|CoreAdminResponse
name|persist
parameter_list|(
name|String
name|fileName
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CoreAdminRequest
operator|.
name|Persist
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Persist
argument_list|()
decl_stmt|;
name|req
operator|.
name|setFileName
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|mergeIndexes
specifier|public
specifier|static
name|CoreAdminResponse
name|mergeIndexes
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|indexDirs
parameter_list|,
name|String
index|[]
name|srcCores
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CoreAdminRequest
operator|.
name|MergeIndexes
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|MergeIndexes
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|req
operator|.
name|setIndexDirs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|indexDirs
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setSrcCores
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|srcCores
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
block|}
end_class

end_unit

