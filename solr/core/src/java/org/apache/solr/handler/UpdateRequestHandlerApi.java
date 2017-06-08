begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|api
operator|.
name|Api
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

begin_class
DECL|class|UpdateRequestHandlerApi
specifier|public
class|class
name|UpdateRequestHandlerApi
extends|extends
name|UpdateRequestHandler
block|{
annotation|@
name|Override
DECL|method|getApis
specifier|public
name|Collection
argument_list|<
name|Api
argument_list|>
name|getApis
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|getApiImpl
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getApiImpl
specifier|private
name|Api
name|getApiImpl
parameter_list|()
block|{
return|return
operator|new
name|Api
argument_list|(
name|Utils
operator|.
name|getSpec
argument_list|(
literal|"core.Update"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|String
name|path
init|=
name|req
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|target
init|=
name|mapping
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
literal|"path"
argument_list|,
name|target
argument_list|)
expr_stmt|;
try|try
block|{
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
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
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|registerV1
specifier|public
name|Boolean
name|registerV1
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
annotation|@
name|Override
DECL|method|registerV2
specifier|public
name|Boolean
name|registerV2
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
DECL|field|mapping
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"/update"
argument_list|,
name|DOC_PATH
argument_list|)
decl|.
name|put
argument_list|(
name|JSON_PATH
argument_list|,
name|DOC_PATH
argument_list|)
decl|.
name|put
argument_list|(
literal|"/update/json/commands"
argument_list|,
name|JSON_PATH
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

