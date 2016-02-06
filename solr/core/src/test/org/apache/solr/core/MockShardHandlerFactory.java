begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|ShardRequest
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
name|ShardResponse
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

begin_comment
comment|/** a fake shardhandler factory that does nothing. */
end_comment

begin_class
DECL|class|MockShardHandlerFactory
specifier|public
class|class
name|MockShardHandlerFactory
extends|extends
name|ShardHandlerFactory
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|args
name|NamedList
name|args
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|args
operator|=
name|info
operator|.
name|initArgs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|()
block|{
return|return
operator|new
name|ShardHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|prepDistributed
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|submit
parameter_list|(
name|ShardRequest
name|sreq
parameter_list|,
name|String
name|shard
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|preferredHostAddress
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|ShardResponse
name|takeCompletedIncludingErrors
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ShardResponse
name|takeCompletedOrError
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cancelAll
parameter_list|()
block|{}
annotation|@
name|Override
specifier|public
name|ShardHandlerFactory
name|getShardHandlerFactory
parameter_list|()
block|{
return|return
name|MockShardHandlerFactory
operator|.
name|this
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
block|}
end_class

end_unit

