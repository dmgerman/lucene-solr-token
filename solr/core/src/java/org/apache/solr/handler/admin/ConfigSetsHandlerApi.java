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
name|List
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
name|CollectionApiMapping
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
name|CollectionApiMapping
operator|.
name|ConfigSetMeta
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
name|ConfigSetsHandler
operator|.
name|ConfigSetOperation
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
import|import static
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
name|ConfigSetsHandler
operator|.
name|ConfigSetOperation
operator|.
name|CREATE_OP
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
name|handler
operator|.
name|admin
operator|.
name|ConfigSetsHandler
operator|.
name|ConfigSetOperation
operator|.
name|DELETE_OP
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
name|handler
operator|.
name|admin
operator|.
name|ConfigSetsHandler
operator|.
name|ConfigSetOperation
operator|.
name|LIST_OP
import|;
end_import

begin_class
DECL|class|ConfigSetsHandlerApi
specifier|public
class|class
name|ConfigSetsHandlerApi
extends|extends
name|BaseHandlerApiSupport
block|{
DECL|field|configSetHandler
specifier|final
name|ConfigSetsHandler
name|configSetHandler
decl_stmt|;
DECL|method|ConfigSetsHandlerApi
specifier|public
name|ConfigSetsHandlerApi
parameter_list|(
name|ConfigSetsHandler
name|configSetHandler
parameter_list|)
block|{
name|this
operator|.
name|configSetHandler
operator|=
name|configSetHandler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommands
specifier|protected
name|List
argument_list|<
name|ApiCommand
argument_list|>
name|getCommands
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|Cmd
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEndPoints
specifier|protected
name|List
argument_list|<
name|CollectionApiMapping
operator|.
name|V2EndPoint
argument_list|>
name|getEndPoints
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|CollectionApiMapping
operator|.
name|ConfigSetEndPoint
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
DECL|enum|Cmd
enum|enum
name|Cmd
implements|implements
name|ApiCommand
block|{
DECL|enum constant|LIST
name|LIST
parameter_list|(
name|ConfigSetMeta
operator|.
name|LIST
parameter_list|,
name|LIST_OP
parameter_list|)
operator|,
DECL|enum constant|CREATE
constructor|CREATE(ConfigSetMeta.CREATE
operator|,
constructor|CREATE_OP
block|)
enum|,
DECL|enum constant|DEL
name|DEL
parameter_list|(
name|ConfigSetMeta
operator|.
name|DEL
parameter_list|,
name|DELETE_OP
parameter_list|)
constructor_decl|;
DECL|field|meta
specifier|public
name|ConfigSetMeta
name|meta
decl_stmt|;
DECL|field|op
specifier|private
specifier|final
name|ConfigSetOperation
name|op
decl_stmt|;
DECL|method|Cmd
name|Cmd
parameter_list|(
name|ConfigSetMeta
name|meta
parameter_list|,
name|ConfigSetOperation
name|op
parameter_list|)
block|{
name|this
operator|.
name|meta
operator|=
name|meta
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|meta
specifier|public
name|CollectionApiMapping
operator|.
name|CommandMeta
name|meta
parameter_list|()
block|{
return|return
name|meta
return|;
block|}
annotation|@
name|Override
DECL|method|invoke
specifier|public
name|void
name|invoke
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|BaseHandlerApiSupport
name|apiHandler
parameter_list|)
throws|throws
name|Exception
block|{
operator|(
operator|(
name|ConfigSetsHandlerApi
operator|)
name|apiHandler
operator|)
operator|.
name|configSetHandler
operator|.
name|invokeAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|op
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
block|}
end_class

unit|}
end_unit

