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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link QueryNode} is a interface implemented by all nodes on a QueryNode  * tree.  */
end_comment

begin_interface
DECL|interface|QueryNode
specifier|public
interface|interface
name|QueryNode
extends|extends
name|Serializable
block|{
comment|/** convert to a query string understood by the query parser */
comment|// TODO: this interface might be changed in the future
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
function_decl|;
comment|/** for printing */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
function_decl|;
comment|/** get Children nodes */
DECL|method|getChildren
specifier|public
name|List
argument_list|<
name|QueryNode
argument_list|>
name|getChildren
parameter_list|()
function_decl|;
comment|/** verify if a node is a Leaf node */
DECL|method|isLeaf
specifier|public
name|boolean
name|isLeaf
parameter_list|()
function_decl|;
comment|/** verify if a node contains a tag */
DECL|method|containsTag
specifier|public
name|boolean
name|containsTag
parameter_list|(
name|CharSequence
name|tagName
parameter_list|)
function_decl|;
comment|/**    * @param tagName    * @return of stored on under that tag name    */
DECL|method|getTag
specifier|public
name|Object
name|getTag
parameter_list|(
name|CharSequence
name|tagName
parameter_list|)
function_decl|;
DECL|method|getParent
specifier|public
name|QueryNode
name|getParent
parameter_list|()
function_decl|;
comment|/**    * Recursive clone the QueryNode tree The tags are not copied to the new tree    * when you call the cloneTree() method    *     * @return the cloned tree    * @throws CloneNotSupportedException    */
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
function_decl|;
comment|// Below are the methods that can change state of a QueryNode
comment|// Write Operations (not Thread Safe)
comment|// add a new child to a non Leaf node
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|QueryNode
name|child
parameter_list|)
function_decl|;
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
function_decl|;
comment|// reset the children of a node
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
function_decl|;
comment|/**    * Associate the specified value with the specified tagName. If the tagName    * already exists, the old value is replaced. The tagName and value cannot be    * null. tagName will be converted to lowercase.    *     * @param tagName    * @param value    */
DECL|method|setTag
specifier|public
name|void
name|setTag
parameter_list|(
name|CharSequence
name|tagName
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**    * Unset a tag. tagName will be converted to lowercase.    *     * @param tagName    */
DECL|method|unsetTag
specifier|public
name|void
name|unsetTag
parameter_list|(
name|CharSequence
name|tagName
parameter_list|)
function_decl|;
DECL|method|getTags
specifier|public
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|Object
argument_list|>
name|getTags
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

