begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
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
name|io
operator|.
name|Serializable
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
name|ArrayList
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
name|request
operator|.
name|SolrPing
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
name|UpdateRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SolrPingResponse
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
name|UpdateResponse
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
operator|.
name|METHOD
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
name|beans
operator|.
name|DocumentObjectBinder
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
name|SolrInputDocument
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

begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrServer
specifier|public
specifier|abstract
class|class
name|SolrServer
implements|implements
name|Serializable
block|{
DECL|field|binder
specifier|private
name|DocumentObjectBinder
name|binder
decl_stmt|;
DECL|method|add
specifier|public
name|UpdateResponse
name|add
parameter_list|(
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|addBeans
specifier|public
name|UpdateResponse
name|addBeans
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|beans
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|DocumentObjectBinder
name|binder
init|=
name|this
operator|.
name|getBinder
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
name|beans
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|bean
range|:
name|beans
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|binder
operator|.
name|toSolrInputDocument
argument_list|(
name|bean
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|add
argument_list|(
name|docs
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|UpdateResponse
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|addBean
specifier|public
name|UpdateResponse
name|addBean
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
return|return
name|add
argument_list|(
name|getBinder
argument_list|()
operator|.
name|toSolrInputDocument
argument_list|(
name|obj
argument_list|)
argument_list|)
return|;
block|}
comment|/** waitFlush=true and waitSearcher=true to be inline with the defaults for plain HTTP access    * @throws IOException     */
DECL|method|commit
specifier|public
name|UpdateResponse
name|commit
parameter_list|( )
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** waitFlush=true and waitSearcher=true to be inline with the defaults for plain HTTP access    * @throws IOException     */
DECL|method|optimize
specifier|public
name|UpdateResponse
name|optimize
parameter_list|( )
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|optimize
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|commit
specifier|public
name|UpdateResponse
name|commit
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|setAction
argument_list|(
name|UpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|optimize
specifier|public
name|UpdateResponse
name|optimize
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|optimize
argument_list|(
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
literal|1
argument_list|)
return|;
block|}
DECL|method|optimize
specifier|public
name|UpdateResponse
name|optimize
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|int
name|maxSegments
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|setAction
argument_list|(
name|UpdateRequest
operator|.
name|ACTION
operator|.
name|OPTIMIZE
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|maxSegments
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|rollback
specifier|public
name|UpdateResponse
name|rollback
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|rollback
argument_list|()
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|deleteById
specifier|public
name|UpdateResponse
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|deleteByQuery
specifier|public
name|UpdateResponse
name|deleteByQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|deleteByQuery
argument_list|(
name|query
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|ping
specifier|public
name|SolrPingResponse
name|ping
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|SolrPing
argument_list|()
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|query
specifier|public
name|QueryResponse
name|query
parameter_list|(
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
block|{
return|return
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|query
specifier|public
name|QueryResponse
name|query
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|METHOD
name|method
parameter_list|)
throws|throws
name|SolrServerException
block|{
return|return
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|,
name|method
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * SolrServer implementations need to implement a how a request is actually processed    */
DECL|method|request
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
specifier|final
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
function_decl|;
DECL|method|getBinder
specifier|public
name|DocumentObjectBinder
name|getBinder
parameter_list|()
block|{
if|if
condition|(
name|binder
operator|==
literal|null
condition|)
block|{
name|binder
operator|=
operator|new
name|DocumentObjectBinder
argument_list|()
expr_stmt|;
block|}
return|return
name|binder
return|;
block|}
block|}
end_class

end_unit

