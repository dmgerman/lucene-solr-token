begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  *   *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrRequest
specifier|public
specifier|abstract
class|class
name|SolrRequest
implements|implements
name|Serializable
block|{
DECL|enum|METHOD
specifier|public
enum|enum
name|METHOD
block|{
DECL|enum constant|GET
name|GET
block|,
DECL|enum constant|POST
name|POST
block|}
empty_stmt|;
DECL|field|method
specifier|private
name|METHOD
name|method
init|=
name|METHOD
operator|.
name|GET
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
init|=
literal|null
decl_stmt|;
DECL|field|responseParser
specifier|private
name|ResponseParser
name|responseParser
decl_stmt|;
DECL|field|callback
specifier|private
name|StreamingResponseCallback
name|callback
decl_stmt|;
comment|//---------------------------------------------------------
comment|//---------------------------------------------------------
DECL|method|SolrRequest
specifier|public
name|SolrRequest
parameter_list|(
name|METHOD
name|m
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|m
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|//---------------------------------------------------------
comment|//---------------------------------------------------------
DECL|method|getMethod
specifier|public
name|METHOD
name|getMethod
parameter_list|()
block|{
return|return
name|method
return|;
block|}
DECL|method|setMethod
specifier|public
name|void
name|setMethod
parameter_list|(
name|METHOD
name|method
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|setPath
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/**    *    * @return The {@link org.apache.solr.client.solrj.ResponseParser}    */
DECL|method|getResponseParser
specifier|public
name|ResponseParser
name|getResponseParser
parameter_list|()
block|{
return|return
name|responseParser
return|;
block|}
comment|/**    * Optionally specify how the Response should be parsed.  Not all server implementations require a ResponseParser    * to be specified.    * @param responseParser The {@link org.apache.solr.client.solrj.ResponseParser}    */
DECL|method|setResponseParser
specifier|public
name|void
name|setResponseParser
parameter_list|(
name|ResponseParser
name|responseParser
parameter_list|)
block|{
name|this
operator|.
name|responseParser
operator|=
name|responseParser
expr_stmt|;
block|}
DECL|method|getStreamingResponseCallback
specifier|public
name|StreamingResponseCallback
name|getStreamingResponseCallback
parameter_list|()
block|{
return|return
name|callback
return|;
block|}
DECL|method|setStreamingResponseCallback
specifier|public
name|void
name|setStreamingResponseCallback
parameter_list|(
name|StreamingResponseCallback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
DECL|method|getParams
specifier|public
specifier|abstract
name|SolrParams
name|getParams
parameter_list|()
function_decl|;
DECL|method|getContentStreams
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|process
specifier|public
specifier|abstract
name|SolrResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
function_decl|;
block|}
end_class

end_unit

