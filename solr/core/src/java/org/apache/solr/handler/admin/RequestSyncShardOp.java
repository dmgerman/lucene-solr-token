begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|MatchAllDocsQuery
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
name|SyncStrategy
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
name|handler
operator|.
name|admin
operator|.
name|CoreAdminHandler
operator|.
name|CallInfo
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
name|search
operator|.
name|SolrIndexSearcher
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
name|RefCounted
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

begin_class
DECL|class|RequestSyncShardOp
class|class
name|RequestSyncShardOp
implements|implements
name|CoreAdminHandler
operator|.
name|CoreAdminOp
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
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|CallInfo
name|it
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SolrParams
name|params
init|=
name|it
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"I have been requested to sync up my shard"
argument_list|)
expr_stmt|;
name|ZkController
name|zkController
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getZkController
argument_list|()
decl_stmt|;
if|if
condition|(
name|zkController
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
literal|"Only valid for SolrCloud"
argument_list|)
throw|;
block|}
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
operator|+
literal|" is required"
argument_list|)
throw|;
block|}
name|SyncStrategy
name|syncStrategy
init|=
literal|null
decl_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
init|)
block|{
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|syncStrategy
operator|=
operator|new
name|SyncStrategy
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|<>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|zkController
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
name|cname
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|,
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|syncStrategy
operator|.
name|sync
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|,
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|isSuccess
argument_list|()
decl_stmt|;
comment|// solrcloud_debug
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
try|try
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searchHolder
init|=
name|core
operator|.
name|getNewestSearcher
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|searchHolder
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" synched "
operator|+
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searchHolder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Error in solrcloud_debug block"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|success
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
literal|"Sync Failed"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Could not find core to call sync:"
operator|+
name|cname
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// no recoveryStrat close for now
if|if
condition|(
name|syncStrategy
operator|!=
literal|null
condition|)
block|{
name|syncStrategy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
