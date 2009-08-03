begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.processors
package|package
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
name|Iterator
import|;
end_import

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
name|DeletedQueryNode
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
name|MatchNoDocsQueryNode
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

begin_comment
comment|/**  * A {@link QueryNodeProcessorPipeline} class removes every instance of  * {@link DeletedQueryNode} from a query node tree. If the resulting root node  * is a {@link DeletedQueryNode}, {@link MatchNoDocsQueryNode} is returned.  *   */
end_comment

begin_class
DECL|class|RemoveDeletedQueryNodesProcessor
specifier|public
class|class
name|RemoveDeletedQueryNodesProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|RemoveDeletedQueryNodesProcessor
specifier|public
name|RemoveDeletedQueryNodesProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
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
name|queryTree
operator|=
name|super
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryTree
operator|instanceof
name|DeletedQueryNode
operator|&&
operator|!
operator|(
name|queryTree
operator|instanceof
name|MatchNoDocsQueryNode
operator|)
condition|)
block|{
return|return
operator|new
name|MatchNoDocsQueryNode
argument_list|()
return|;
block|}
return|return
name|queryTree
return|;
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
operator|!
name|node
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
name|boolean
name|removeBoolean
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
operator|||
name|children
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|removeBoolean
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|removeBoolean
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|QueryNode
argument_list|>
name|it
init|=
name|children
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|it
operator|.
name|next
argument_list|()
operator|instanceof
name|DeletedQueryNode
operator|)
condition|)
block|{
name|removeBoolean
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|removeBoolean
condition|)
block|{
return|return
operator|new
name|DeletedQueryNode
argument_list|()
return|;
block|}
block|}
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|instanceof
name|DeletedQueryNode
condition|)
block|{
name|children
operator|.
name|remove
argument_list|(
name|i
operator|--
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|children
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
block|}
end_class

end_unit

