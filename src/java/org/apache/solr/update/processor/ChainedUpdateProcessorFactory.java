begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

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
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryResponse
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
name|util
operator|.
name|plugin
operator|.
name|AbstractPluginLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * An UpdateRequestProcessorFactory that constructs a chain of UpdateRequestProcessor.  *   * This is the default implementation and can be configured via solrconfig.xml with:  *   *<updateRequestProcessor>  *<factory name="standard" class="solr.ChainedUpdateProcessorFactory">  *<chain class="PathToClass1" />  *<chain class="PathToClass2" />  *<chain class="solr.LogUpdateProcessorFactory">  *<int name="maxNumToLog">100</int>  *</chain>  *<chain class="solr.RunUpdateProcessorFactory" />  *</factory>  *</updateRequestProcessor>  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|ChainedUpdateProcessorFactory
specifier|public
class|class
name|ChainedUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
block|{
DECL|field|factory
specifier|protected
name|UpdateRequestProcessorFactory
index|[]
name|factory
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|,
specifier|final
name|Node
name|node
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|factories
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
comment|// Load and initialize the plugin chain
name|AbstractPluginLoader
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|loader
init|=
operator|new
name|AbstractPluginLoader
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
argument_list|(
literal|"processor chain"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|UpdateRequestProcessorFactory
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|plugin
operator|.
name|init
argument_list|(
name|core
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|UpdateRequestProcessorFactory
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|UpdateRequestProcessorFactory
name|plugin
parameter_list|)
throws|throws
name|Exception
block|{
name|factories
operator|.
name|add
argument_list|(
name|plugin
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|XPath
name|xpath
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newXPath
argument_list|()
decl_stmt|;
try|try
block|{
name|loader
operator|.
name|load
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"chain"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
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
name|SERVER_ERROR
argument_list|,
literal|"Error loading processor chain: "
operator|+
name|node
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
comment|// If not configured, make sure it has the default settings
if|if
condition|(
name|factories
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|factories
operator|.
name|add
argument_list|(
operator|new
name|RunUpdateProcessorFactory
argument_list|()
argument_list|)
expr_stmt|;
name|factories
operator|.
name|add
argument_list|(
operator|new
name|LogUpdateProcessorFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|factory
operator|=
name|factories
operator|.
name|toArray
argument_list|(
operator|new
name|UpdateRequestProcessorFactory
index|[
name|factories
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|UpdateRequestProcessor
name|processor
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|factory
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|processor
operator|=
name|factory
index|[
name|i
index|]
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|processor
argument_list|)
expr_stmt|;
block|}
return|return
name|processor
return|;
block|}
block|}
end_class

end_unit

