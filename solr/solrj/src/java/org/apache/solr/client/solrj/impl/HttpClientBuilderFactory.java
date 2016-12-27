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
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Factory interface for configuring {@linkplain SolrHttpClientBuilder}. This  * relies on the internal HttpClient implementation and is subject to  * change.  *  * @lucene.experimental  **/
end_comment

begin_interface
DECL|interface|HttpClientBuilderFactory
specifier|public
interface|interface
name|HttpClientBuilderFactory
extends|extends
name|Closeable
block|{
comment|/**    * This method configures the {@linkplain SolrHttpClientBuilder} by overriding the    * configuration of passed SolrHttpClientBuilder or as a new instance.    *    * @param builder The instance of the {@linkplain SolrHttpClientBuilder} which should    *                by configured (optional).    * @return the {@linkplain SolrHttpClientBuilder}    */
DECL|method|getHttpClientBuilder
specifier|public
name|SolrHttpClientBuilder
name|getHttpClientBuilder
parameter_list|(
name|Optional
argument_list|<
name|SolrHttpClientBuilder
argument_list|>
name|builder
parameter_list|)
function_decl|;
block|}
end_interface

end_unit
