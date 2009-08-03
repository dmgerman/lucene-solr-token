begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.original.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
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
name|original
operator|.
name|nodes
operator|.
name|MultiPhraseQueryNode
import|;
end_import

begin_comment
comment|/**  * This processor removes invalid {@link SlopQueryNode} objects in the query  * node tree. A {@link SlopQueryNode} is invalid if its child is neither a  * {@link TokenizedPhraseQueryNode} nor a {@link MultiPhraseQueryNode}.<br/>  *   * @see SlopQueryNode  */
end_comment

begin_class
DECL|class|PhraseSlopQueryNodeProcessor
specifier|public
class|class
name|PhraseSlopQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|PhraseSlopQueryNodeProcessor
specifier|public
name|PhraseSlopQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
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
name|SlopQueryNode
condition|)
block|{
name|SlopQueryNode
name|phraseSlopNode
init|=
operator|(
name|SlopQueryNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|phraseSlopNode
operator|.
name|getChild
argument_list|()
operator|instanceof
name|TokenizedPhraseQueryNode
operator|)
operator|&&
operator|!
operator|(
name|phraseSlopNode
operator|.
name|getChild
argument_list|()
operator|instanceof
name|MultiPhraseQueryNode
operator|)
condition|)
block|{
return|return
name|phraseSlopNode
operator|.
name|getChild
argument_list|()
return|;
block|}
block|}
return|return
name|node
return|;
block|}
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

