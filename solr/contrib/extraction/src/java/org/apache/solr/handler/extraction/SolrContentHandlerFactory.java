begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|schema
operator|.
name|IndexSchema
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

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|SolrContentHandlerFactory
specifier|public
class|class
name|SolrContentHandlerFactory
block|{
DECL|field|dateFormats
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
decl_stmt|;
DECL|method|SolrContentHandlerFactory
specifier|public
name|SolrContentHandlerFactory
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
parameter_list|)
block|{
name|this
operator|.
name|dateFormats
operator|=
name|dateFormats
expr_stmt|;
block|}
DECL|method|createSolrContentHandler
specifier|public
name|SolrContentHandler
name|createSolrContentHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
return|return
operator|new
name|SolrContentHandler
argument_list|(
name|metadata
argument_list|,
name|params
argument_list|,
name|schema
argument_list|,
name|dateFormats
argument_list|)
return|;
block|}
block|}
end_class

end_unit

