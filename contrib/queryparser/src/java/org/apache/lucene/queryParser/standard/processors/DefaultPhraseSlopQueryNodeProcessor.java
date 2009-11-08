begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|processors
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|queryParser
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
name|queryParser
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
name|queryParser
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|SlopQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|TokenizedPhraseQueryNode
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
name|queryParser
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|DefaultPhraseSlopAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|nodes
operator|.
name|MultiPhraseQueryNode
import|;
end_import

begin_comment
comment|/**  * This processor verifies if the attribute {@link DefaultPhraseSlopAttribute}  * is defined in the {@link QueryConfigHandler}. If it is, it looks for every  * {@link TokenizedPhraseQueryNode} and {@link MultiPhraseQueryNode} that does  * not have any {@link SlopQueryNode} applied to it and creates an  * {@link SlopQueryNode} and apply to it. The new {@link SlopQueryNode} has the  * same slop value defined in the attribute.<br/>  *   * @see SlopQueryNode  * @see DefaultPhraseSlopAttribute  */
end_comment

begin_class
DECL|class|DefaultPhraseSlopQueryNodeProcessor
specifier|public
class|class
name|DefaultPhraseSlopQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|field|processChildren
specifier|private
name|boolean
name|processChildren
init|=
literal|true
decl_stmt|;
DECL|field|defaultPhraseSlop
specifier|private
name|int
name|defaultPhraseSlop
decl_stmt|;
DECL|method|DefaultPhraseSlopQueryNodeProcessor
specifier|public
name|DefaultPhraseSlopQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|QueryNode
name|process
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|QueryConfigHandler
name|queryConfig
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryConfig
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|queryConfig
operator|.
name|hasAttribute
argument_list|(
name|DefaultPhraseSlopAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|this
operator|.
name|defaultPhraseSlop
operator|=
name|queryConfig
operator|.
name|getAttribute
argument_list|(
name|DefaultPhraseSlopAttribute
operator|.
name|class
argument_list|)
operator|.
name|getDefaultPhraseSlop
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
block|}
return|return
name|queryTree
return|;
block|}
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
name|TokenizedPhraseQueryNode
operator|||
name|node
operator|instanceof
name|MultiPhraseQueryNode
condition|)
block|{
return|return
operator|new
name|SlopQueryNode
argument_list|(
name|node
argument_list|,
name|this
operator|.
name|defaultPhraseSlop
argument_list|)
return|;
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
if|if
condition|(
name|node
operator|instanceof
name|SlopQueryNode
condition|)
block|{
name|this
operator|.
name|processChildren
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|processChildren
specifier|protected
name|void
name|processChildren
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|this
operator|.
name|processChildren
condition|)
block|{
name|super
operator|.
name|processChildren
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|processChildren
operator|=
literal|true
expr_stmt|;
block|}
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

