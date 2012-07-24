begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|AbstractAnalysisFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|AnalysisPluginLoader
specifier|public
specifier|abstract
class|class
name|AnalysisPluginLoader
parameter_list|<
name|S
extends|extends
name|AbstractAnalysisFactory
parameter_list|>
extends|extends
name|AbstractPluginLoader
argument_list|<
name|S
argument_list|>
block|{
DECL|method|AnalysisPluginLoader
specifier|public
name|AnalysisPluginLoader
parameter_list|(
name|String
name|type
parameter_list|,
name|Class
argument_list|<
name|S
argument_list|>
name|pluginClassType
parameter_list|,
name|boolean
name|preRegister
parameter_list|,
name|boolean
name|requireName
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|pluginClassType
argument_list|,
name|preRegister
argument_list|,
name|requireName
argument_list|)
expr_stmt|;
block|}
DECL|method|AnalysisPluginLoader
specifier|public
name|AnalysisPluginLoader
parameter_list|(
name|String
name|type
parameter_list|,
name|Class
argument_list|<
name|S
argument_list|>
name|pluginClassType
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|pluginClassType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|S
name|create
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|className
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|S
name|instance
init|=
literal|null
decl_stmt|;
name|Matcher
name|m
init|=
name|legacyPattern
operator|.
name|matcher
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
try|try
block|{
name|instance
operator|=
name|createSPI
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// ok
block|}
block|}
if|if
condition|(
name|instance
operator|!=
literal|null
condition|)
block|{
comment|// necessary because SolrResourceLoader manages its own list of 'awaiting ResourceLoaderAware'
name|className
operator|=
name|instance
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|super
operator|.
name|create
argument_list|(
name|loader
argument_list|,
name|name
argument_list|,
name|className
argument_list|,
name|node
argument_list|)
return|;
block|}
DECL|field|legacyPattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|legacyPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"((org\\.apache\\.solr\\.analysis\\.)|(solr\\.))([\\p{L}_$][\\p{L}\\p{N}_$]+?)(TokenFilter|Filter|Tokenizer|CharFilter)Factory"
argument_list|)
decl_stmt|;
DECL|method|createSPI
specifier|protected
specifier|abstract
name|S
name|createSPI
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit

