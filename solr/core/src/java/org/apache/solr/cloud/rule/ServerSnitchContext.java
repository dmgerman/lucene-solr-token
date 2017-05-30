begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
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
name|util
operator|.
name|Map
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
name|BinaryResponseParser
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
name|HttpSolrClient
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
name|GenericSolrRequest
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
name|SimpleSolrResponse
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
name|rule
operator|.
name|RemoteCallback
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
name|rule
operator|.
name|SnitchContext
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
name|Utils
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
name|update
operator|.
name|UpdateShardHandler
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
name|ACTION
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
name|CoreAdminAction
operator|.
name|INVOKE
import|;
end_import

begin_class
DECL|class|ServerSnitchContext
specifier|public
class|class
name|ServerSnitchContext
extends|extends
name|SnitchContext
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
DECL|field|coreContainer
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|method|ServerSnitchContext
specifier|public
name|ServerSnitchContext
parameter_list|(
name|SnitchInfo
name|perSnitch
parameter_list|,
name|String
name|node
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|session
parameter_list|,
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|super
argument_list|(
name|perSnitch
argument_list|,
name|node
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
block|}
DECL|method|getZkJson
specifier|public
name|Map
name|getZkJson
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
try|try
block|{
name|byte
index|[]
name|data
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
operator|new
name|Stat
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|data
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to read from ZK path : "
operator|+
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|invokeRemote
specifier|public
name|void
name|invokeRemote
parameter_list|(
name|String
name|node
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|klas
parameter_list|,
name|RemoteCallback
name|callback
parameter_list|)
block|{
if|if
condition|(
name|callback
operator|==
literal|null
condition|)
name|callback
operator|=
name|this
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"class"
argument_list|,
name|klas
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ACTION
argument_list|,
name|INVOKE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//todo batch all requests to the same server
try|try
block|{
name|SimpleSolrResponse
name|rsp
init|=
name|invoke
argument_list|(
name|node
argument_list|,
name|CommonParams
operator|.
name|CORES_HANDLER_PATH
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|returnedVal
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|rsp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
name|klas
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
comment|//        log this
block|}
else|else
block|{
name|callback
operator|.
name|remoteCallback
argument_list|(
name|ServerSnitchContext
operator|.
name|this
argument_list|,
name|returnedVal
argument_list|)
expr_stmt|;
block|}
name|callback
operator|.
name|remoteCallback
argument_list|(
name|this
argument_list|,
name|returnedVal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to invoke snitch counterpart"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
DECL|method|invoke
specifier|public
name|SimpleSolrResponse
name|invoke
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|String
name|path
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|String
name|url
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|solrNode
argument_list|)
decl_stmt|;
name|UpdateShardHandler
name|shardHandler
init|=
name|coreContainer
operator|.
name|getUpdateShardHandler
argument_list|()
decl_stmt|;
name|GenericSolrRequest
name|request
init|=
operator|new
name|GenericSolrRequest
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|GET
argument_list|,
name|path
argument_list|,
name|params
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|url
argument_list|)
operator|.
name|withHttpClient
argument_list|(
name|shardHandler
operator|.
name|getHttpClient
argument_list|()
argument_list|)
operator|.
name|withResponseParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|request
operator|.
name|response
operator|.
name|nl
operator|=
name|rsp
expr_stmt|;
return|return
name|request
operator|.
name|response
return|;
block|}
block|}
block|}
end_class

end_unit

