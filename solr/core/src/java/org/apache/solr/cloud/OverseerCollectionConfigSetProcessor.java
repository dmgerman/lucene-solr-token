begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|handler
operator|.
name|component
operator|.
name|ShardHandler
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
name|component
operator|.
name|ShardHandlerFactory
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
name|OverseerConfigSetMessageHandler
operator|.
name|CONFIGSETS_ACTION_PREFIX
import|;
end_import

begin_comment
comment|/**  * An {@link OverseerTaskProcessor} that handles:  * 1) collection-related Overseer messages  * 2) configset-related Overseer messages  */
end_comment

begin_class
DECL|class|OverseerCollectionConfigSetProcessor
specifier|public
class|class
name|OverseerCollectionConfigSetProcessor
extends|extends
name|OverseerTaskProcessor
block|{
DECL|method|OverseerCollectionConfigSetProcessor
specifier|public
name|OverseerCollectionConfigSetProcessor
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|myId
parameter_list|,
specifier|final
name|ShardHandler
name|shardHandler
parameter_list|,
name|String
name|adminPath
parameter_list|,
name|Overseer
operator|.
name|Stats
name|stats
parameter_list|,
name|Overseer
name|overseer
parameter_list|,
name|OverseerNodePrioritizer
name|overseerNodePrioritizer
parameter_list|)
block|{
name|this
argument_list|(
name|zkStateReader
argument_list|,
name|myId
argument_list|,
name|shardHandler
operator|.
name|getShardHandlerFactory
argument_list|()
argument_list|,
name|adminPath
argument_list|,
name|stats
argument_list|,
name|overseer
argument_list|,
name|overseerNodePrioritizer
argument_list|,
name|Overseer
operator|.
name|getCollectionQueue
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|stats
argument_list|)
argument_list|,
name|Overseer
operator|.
name|getRunningMap
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|,
name|Overseer
operator|.
name|getCompletedMap
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|,
name|Overseer
operator|.
name|getFailureMap
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|OverseerCollectionConfigSetProcessor
specifier|protected
name|OverseerCollectionConfigSetProcessor
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|myId
parameter_list|,
specifier|final
name|ShardHandlerFactory
name|shardHandlerFactory
parameter_list|,
name|String
name|adminPath
parameter_list|,
name|Overseer
operator|.
name|Stats
name|stats
parameter_list|,
name|Overseer
name|overseer
parameter_list|,
name|OverseerNodePrioritizer
name|overseerNodePrioritizer
parameter_list|,
name|OverseerTaskQueue
name|workQueue
parameter_list|,
name|DistributedMap
name|runningMap
parameter_list|,
name|DistributedMap
name|completedMap
parameter_list|,
name|DistributedMap
name|failureMap
parameter_list|)
block|{
name|super
argument_list|(
name|zkStateReader
argument_list|,
name|myId
argument_list|,
name|stats
argument_list|,
name|getOverseerMessageHandlerSelector
argument_list|(
name|zkStateReader
argument_list|,
name|myId
argument_list|,
name|shardHandlerFactory
argument_list|,
name|adminPath
argument_list|,
name|stats
argument_list|,
name|overseer
argument_list|,
name|overseerNodePrioritizer
argument_list|)
argument_list|,
name|overseerNodePrioritizer
argument_list|,
name|workQueue
argument_list|,
name|runningMap
argument_list|,
name|completedMap
argument_list|,
name|failureMap
argument_list|)
expr_stmt|;
block|}
DECL|method|getOverseerMessageHandlerSelector
specifier|private
specifier|static
name|OverseerMessageHandlerSelector
name|getOverseerMessageHandlerSelector
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|myId
parameter_list|,
specifier|final
name|ShardHandlerFactory
name|shardHandlerFactory
parameter_list|,
name|String
name|adminPath
parameter_list|,
name|Overseer
operator|.
name|Stats
name|stats
parameter_list|,
name|Overseer
name|overseer
parameter_list|,
name|OverseerNodePrioritizer
name|overseerNodePrioritizer
parameter_list|)
block|{
specifier|final
name|OverseerCollectionMessageHandler
name|collMessageHandler
init|=
operator|new
name|OverseerCollectionMessageHandler
argument_list|(
name|zkStateReader
argument_list|,
name|myId
argument_list|,
name|shardHandlerFactory
argument_list|,
name|adminPath
argument_list|,
name|stats
argument_list|,
name|overseer
argument_list|,
name|overseerNodePrioritizer
argument_list|)
decl_stmt|;
specifier|final
name|OverseerConfigSetMessageHandler
name|configMessageHandler
init|=
operator|new
name|OverseerConfigSetMessageHandler
argument_list|(
name|zkStateReader
argument_list|)
decl_stmt|;
return|return
operator|new
name|OverseerMessageHandlerSelector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|collMessageHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OverseerMessageHandler
name|selectOverseerMessageHandler
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|operation
init|=
name|message
operator|.
name|getStr
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|operation
operator|!=
literal|null
operator|&&
name|operation
operator|.
name|startsWith
argument_list|(
name|CONFIGSETS_ACTION_PREFIX
argument_list|)
condition|)
block|{
return|return
name|configMessageHandler
return|;
block|}
return|return
name|collMessageHandler
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

