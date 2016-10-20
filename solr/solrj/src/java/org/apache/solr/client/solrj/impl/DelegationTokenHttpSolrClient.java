begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
name|impl
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpRequestBase
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
name|ResponseParser
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

begin_class
DECL|class|DelegationTokenHttpSolrClient
specifier|public
class|class
name|DelegationTokenHttpSolrClient
extends|extends
name|HttpSolrClient
block|{
DECL|field|DELEGATION_TOKEN_PARAM
specifier|public
specifier|final
specifier|static
name|String
name|DELEGATION_TOKEN_PARAM
init|=
literal|"delegation"
decl_stmt|;
DECL|field|delegationToken
specifier|private
specifier|final
name|String
name|delegationToken
decl_stmt|;
DECL|method|DelegationTokenHttpSolrClient
specifier|public
name|DelegationTokenHttpSolrClient
parameter_list|(
name|String
name|baseURL
parameter_list|,
name|HttpClient
name|client
parameter_list|,
name|ResponseParser
name|parser
parameter_list|,
name|boolean
name|allowCompression
parameter_list|,
name|String
name|delegationToken
parameter_list|)
block|{
name|super
argument_list|(
name|baseURL
argument_list|,
name|client
argument_list|,
name|parser
argument_list|,
name|allowCompression
argument_list|)
expr_stmt|;
if|if
condition|(
name|delegationToken
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Delegation token cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|delegationToken
operator|=
name|delegationToken
expr_stmt|;
name|setQueryParams
argument_list|(
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DELEGATION_TOKEN_PARAM
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|invariantParams
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|invariantParams
operator|.
name|set
argument_list|(
name|DELEGATION_TOKEN_PARAM
argument_list|,
name|delegationToken
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createMethod
specifier|protected
name|HttpRequestBase
name|createMethod
parameter_list|(
specifier|final
name|SolrRequest
name|request
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|SolrParams
name|params
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getParams
argument_list|(
name|DELEGATION_TOKEN_PARAM
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|DELEGATION_TOKEN_PARAM
operator|+
literal|" parameter not supported"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|createMethod
argument_list|(
name|request
argument_list|,
name|collection
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setQueryParams
specifier|public
name|void
name|setQueryParams
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
parameter_list|)
block|{
name|queryParams
operator|=
name|queryParams
operator|==
literal|null
condition|?
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DELEGATION_TOKEN_PARAM
argument_list|)
argument_list|)
argument_list|)
else|:
name|queryParams
expr_stmt|;
if|if
condition|(
operator|!
name|queryParams
operator|.
name|contains
argument_list|(
name|DELEGATION_TOKEN_PARAM
argument_list|)
condition|)
block|{
name|queryParams
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|queryParams
argument_list|)
expr_stmt|;
name|queryParams
operator|.
name|add
argument_list|(
name|DELEGATION_TOKEN_PARAM
argument_list|)
expr_stmt|;
name|queryParams
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|queryParams
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setQueryParams
argument_list|(
name|queryParams
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
