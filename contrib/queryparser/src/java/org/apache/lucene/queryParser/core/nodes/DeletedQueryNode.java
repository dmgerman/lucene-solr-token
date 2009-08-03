begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.nodes
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
name|nodes
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|parser
operator|.
name|EscapeQuerySyntax
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
name|RemoveDeletedQueryNodesProcessor
import|;
end_import

begin_comment
comment|/**  * A {@link DeletedQueryNode} represents a node that was deleted from the query  * node tree. It can be removed from the tree using the  * {@link RemoveDeletedQueryNodesProcessor} processor.  */
end_comment

begin_class
DECL|class|DeletedQueryNode
specifier|public
class|class
name|DeletedQueryNode
extends|extends
name|QueryNodeImpl
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|9151675506000425293L
decl_stmt|;
DECL|method|DeletedQueryNode
specifier|public
name|DeletedQueryNode
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
return|return
literal|"[DELETEDCHILD]"
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<deleted/>"
return|;
block|}
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|DeletedQueryNode
name|clone
init|=
operator|(
name|DeletedQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

