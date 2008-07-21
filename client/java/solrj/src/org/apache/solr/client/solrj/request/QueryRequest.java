begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Collection
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
name|SolrServer
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
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|QueryRequest
specifier|public
class|class
name|QueryRequest
extends|extends
name|SolrRequest
block|{
DECL|field|query
specifier|private
name|SolrParams
name|query
decl_stmt|;
DECL|method|QueryRequest
specifier|public
name|QueryRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryRequest
specifier|public
name|QueryRequest
parameter_list|(
name|SolrParams
name|q
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|query
operator|=
name|q
expr_stmt|;
block|}
DECL|method|QueryRequest
specifier|public
name|QueryRequest
parameter_list|(
name|SolrParams
name|q
parameter_list|,
name|METHOD
name|method
parameter_list|)
block|{
name|super
argument_list|(
name|method
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|query
operator|=
name|q
expr_stmt|;
block|}
comment|/**    * Use the params 'QT' parameter if it exists    */
annotation|@
name|Override
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|String
name|qt
init|=
name|query
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
if|if
condition|(
name|qt
operator|==
literal|null
condition|)
block|{
name|qt
operator|=
name|super
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|qt
operator|!=
literal|null
operator|&&
name|qt
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|qt
return|;
block|}
return|return
literal|"/select"
return|;
block|}
comment|//---------------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------------
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
block|{
return|return
literal|null
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
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|QueryResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
block|{
try|try
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|QueryResponse
name|res
init|=
operator|new
name|QueryResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|,
name|server
argument_list|)
decl_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"Error executing query"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

