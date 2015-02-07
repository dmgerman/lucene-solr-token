begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|BoostQueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldableNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|util
operator|.
name|StringUtils
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
import|;
end_import

begin_comment
comment|/**  * This processor iterates the query node tree looking for every  * {@link FieldableNode} that has {@link ConfigurationKeys#BOOST} in its  * config. If there is, the boost is applied to that {@link FieldableNode}.  *   * @see ConfigurationKeys#BOOST  * @see QueryConfigHandler  * @see FieldableNode  */
end_comment

begin_class
DECL|class|BoostQueryNodeProcessor
specifier|public
class|class
name|BoostQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|FieldableNode
operator|&&
operator|(
name|node
operator|.
name|getParent
argument_list|()
operator|==
literal|null
operator|||
operator|!
operator|(
name|node
operator|.
name|getParent
argument_list|()
operator|instanceof
name|FieldableNode
operator|)
operator|)
condition|)
block|{
name|FieldableNode
name|fieldNode
init|=
operator|(
name|FieldableNode
operator|)
name|node
decl_stmt|;
name|QueryConfigHandler
name|config
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|CharSequence
name|field
init|=
name|fieldNode
operator|.
name|getField
argument_list|()
decl_stmt|;
name|FieldConfig
name|fieldConfig
init|=
name|config
operator|.
name|getFieldConfig
argument_list|(
name|StringUtils
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldConfig
operator|!=
literal|null
condition|)
block|{
name|Float
name|boost
init|=
name|fieldConfig
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|BOOST
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|BoostQueryNode
argument_list|(
name|node
argument_list|,
name|boost
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

