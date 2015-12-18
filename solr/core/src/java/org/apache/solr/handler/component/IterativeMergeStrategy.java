begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements. See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License. You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|concurrent
operator|.
name|Callable
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
name|Future
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
name|ExecutorService
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
name|Executors
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
name|ArrayList
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
name|util
operator|.
name|NamedThreadFactory
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
name|QueryRequest
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
name|QueryResponse
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
name|SolrDocumentList
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
name|util
operator|.
name|ExecutorUtil
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
name|SolrjNamedThreadFactory
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
name|http
operator|.
name|client
operator|.
name|HttpClient
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
DECL|class|IterativeMergeStrategy
specifier|public
specifier|abstract
class|class
name|IterativeMergeStrategy
implements|implements
name|MergeStrategy
block|{
DECL|field|executorService
specifier|protected
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|httpClient
specifier|protected
specifier|static
name|HttpClient
name|httpClient
decl_stmt|;
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
static|static
block|{
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
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|,
literal|32
argument_list|)
expr_stmt|;
name|httpClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
name|rb
operator|.
name|_responseDocs
operator|=
operator|new
name|SolrDocumentList
argument_list|()
expr_stmt|;
comment|// Null pointers will occur otherwise.
name|rb
operator|.
name|onePassDistributedQuery
operator|=
literal|true
expr_stmt|;
comment|// Turn off the second pass distributed.
name|executorService
operator|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"IterativeMergeStrategy"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|process
argument_list|(
name|rb
argument_list|,
name|sreq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergesIds
specifier|public
name|boolean
name|mergesIds
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|handlesMergeFields
specifier|public
name|boolean
name|handlesMergeFields
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|handleMergeFields
specifier|public
name|void
name|handleMergeFields
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{    }
DECL|class|CallBack
specifier|public
specifier|static
class|class
name|CallBack
implements|implements
name|Callable
argument_list|<
name|CallBack
argument_list|>
block|{
DECL|field|solrClient
specifier|private
name|HttpSolrClient
name|solrClient
decl_stmt|;
DECL|field|req
specifier|private
name|QueryRequest
name|req
decl_stmt|;
DECL|field|response
specifier|private
name|QueryResponse
name|response
decl_stmt|;
DECL|field|originalShardResponse
specifier|private
name|ShardResponse
name|originalShardResponse
decl_stmt|;
DECL|method|CallBack
specifier|public
name|CallBack
parameter_list|(
name|ShardResponse
name|originalShardResponse
parameter_list|,
name|QueryRequest
name|req
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"################ SHARD ADDRESSS ##############:"
operator|+
name|originalShardResponse
operator|.
name|getShardAddress
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"############ HTTP Client #############:"
operator|+
name|httpClient
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|schemes
init|=
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|getSchemeRegistry
argument_list|()
operator|.
name|getSchemeNames
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|scheme
range|:
name|schemes
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"############ Scheme #############:"
operator|+
name|scheme
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|solrClient
operator|=
operator|new
name|HttpSolrClient
argument_list|(
name|originalShardResponse
operator|.
name|getShardAddress
argument_list|()
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|originalShardResponse
operator|=
name|originalShardResponse
expr_stmt|;
name|req
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
DECL|method|getResponse
specifier|public
name|QueryResponse
name|getResponse
parameter_list|()
block|{
return|return
name|this
operator|.
name|response
return|;
block|}
DECL|method|getOriginalShardResponse
specifier|public
name|ShardResponse
name|getOriginalShardResponse
parameter_list|()
block|{
return|return
name|this
operator|.
name|originalShardResponse
return|;
block|}
DECL|method|call
specifier|public
name|CallBack
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|response
operator|=
name|req
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|method|callBack
specifier|public
name|List
argument_list|<
name|Future
argument_list|<
name|CallBack
argument_list|>
argument_list|>
name|callBack
parameter_list|(
name|List
argument_list|<
name|ShardResponse
argument_list|>
name|responses
parameter_list|,
name|QueryRequest
name|req
parameter_list|)
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|CallBack
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardResponse
name|response
range|:
name|responses
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|this
operator|.
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|CallBack
argument_list|(
name|response
argument_list|,
name|req
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|futures
return|;
block|}
DECL|method|callBack
specifier|public
name|Future
argument_list|<
name|CallBack
argument_list|>
name|callBack
parameter_list|(
name|ShardResponse
name|response
parameter_list|,
name|QueryRequest
name|req
parameter_list|)
block|{
return|return
name|this
operator|.
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|CallBack
argument_list|(
name|response
argument_list|,
name|req
argument_list|)
argument_list|)
return|;
block|}
DECL|method|process
specifier|protected
specifier|abstract
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

