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
name|Arrays
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|SolrClient
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
name|NoOpResponseParser
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
name|DelegationTokenResponse
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
name|ContentStream
import|;
end_import

begin_comment
comment|/**  * Class for making Solr delegation token requests.  *  * @since Solr 6.2  */
end_comment

begin_class
DECL|class|DelegationTokenRequest
specifier|public
specifier|abstract
class|class
name|DelegationTokenRequest
parameter_list|<
name|Q
extends|extends
name|DelegationTokenRequest
parameter_list|<
name|Q
parameter_list|,
name|R
parameter_list|>
parameter_list|,
name|R
extends|extends
name|DelegationTokenResponse
parameter_list|>
extends|extends
name|SolrRequest
argument_list|<
name|R
argument_list|>
block|{
DECL|field|OP_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|OP_KEY
init|=
literal|"op"
decl_stmt|;
DECL|field|TOKEN_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|TOKEN_KEY
init|=
literal|"token"
decl_stmt|;
DECL|method|DelegationTokenRequest
specifier|public
name|DelegationTokenRequest
parameter_list|(
name|METHOD
name|m
parameter_list|)
block|{
comment|// path doesn't really matter -- the filter will respond to any path.
comment|// setting the path to admin/collections lets us pass through CloudSolrServer
comment|// without having to specify a collection (that may not even exist yet).
name|super
argument_list|(
name|m
argument_list|,
literal|"/admin/collections"
argument_list|)
expr_stmt|;
block|}
DECL|method|getThis
specifier|protected
specifier|abstract
name|Q
name|getThis
parameter_list|()
function_decl|;
comment|/**    * {@inheritDoc}    */
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
DECL|method|createResponse
specifier|protected
specifier|abstract
name|R
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
function_decl|;
DECL|class|Get
specifier|public
specifier|static
class|class
name|Get
extends|extends
name|DelegationTokenRequest
argument_list|<
name|Get
argument_list|,
name|DelegationTokenResponse
operator|.
name|Get
argument_list|>
block|{
DECL|field|renewer
specifier|protected
name|String
name|renewer
decl_stmt|;
DECL|method|Get
specifier|public
name|Get
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Get
specifier|public
name|Get
parameter_list|(
name|String
name|renewer
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|)
expr_stmt|;
name|this
operator|.
name|renewer
operator|=
name|renewer
expr_stmt|;
name|setResponseParser
argument_list|(
operator|new
name|DelegationTokenResponse
operator|.
name|JsonMapResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|setQueryParams
argument_list|(
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|OP_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getThis
specifier|protected
name|Get
name|getThis
parameter_list|()
block|{
return|return
name|this
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
name|OP_KEY
argument_list|,
literal|"GETDELEGATIONTOKEN"
argument_list|)
expr_stmt|;
if|if
condition|(
name|renewer
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
literal|"renewer"
argument_list|,
name|renewer
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|public
name|DelegationTokenResponse
operator|.
name|Get
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|DelegationTokenResponse
operator|.
name|Get
argument_list|()
return|;
block|}
block|}
DECL|class|Renew
specifier|public
specifier|static
class|class
name|Renew
extends|extends
name|DelegationTokenRequest
argument_list|<
name|Renew
argument_list|,
name|DelegationTokenResponse
operator|.
name|Renew
argument_list|>
block|{
DECL|field|token
specifier|protected
name|String
name|token
decl_stmt|;
annotation|@
name|Override
DECL|method|getThis
specifier|protected
name|Renew
name|getThis
parameter_list|()
block|{
return|return
name|this
return|;
block|}
DECL|method|Renew
specifier|public
name|Renew
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|PUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|setResponseParser
argument_list|(
operator|new
name|DelegationTokenResponse
operator|.
name|JsonMapResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|setQueryParams
argument_list|(
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|OP_KEY
argument_list|,
name|TOKEN_KEY
argument_list|)
argument_list|)
argument_list|)
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
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|OP_KEY
argument_list|,
literal|"RENEWDELEGATIONTOKEN"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|TOKEN_KEY
argument_list|,
name|token
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|public
name|DelegationTokenResponse
operator|.
name|Renew
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|DelegationTokenResponse
operator|.
name|Renew
argument_list|()
return|;
block|}
block|}
DECL|class|Cancel
specifier|public
specifier|static
class|class
name|Cancel
extends|extends
name|DelegationTokenRequest
argument_list|<
name|Cancel
argument_list|,
name|DelegationTokenResponse
operator|.
name|Cancel
argument_list|>
block|{
DECL|field|token
specifier|protected
name|String
name|token
decl_stmt|;
DECL|method|Cancel
specifier|public
name|Cancel
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|PUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|setResponseParser
argument_list|(
operator|new
name|NoOpResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|setQueryParams
argument_list|(
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|OP_KEY
argument_list|,
name|TOKEN_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getThis
specifier|protected
name|Cancel
name|getThis
parameter_list|()
block|{
return|return
name|this
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
name|OP_KEY
argument_list|,
literal|"CANCELDELEGATIONTOKEN"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|TOKEN_KEY
argument_list|,
name|token
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|public
name|DelegationTokenResponse
operator|.
name|Cancel
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|DelegationTokenResponse
operator|.
name|Cancel
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
