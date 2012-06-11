begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.nodes
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
name|core
operator|.
name|nodes
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link OpaqueQueryNode} is used for specify values that are not supposed to  * be parsed by the parser. For example: and XPATH query in the middle of a  * query string a b @xpath:'/bookstore/book[1]/title' c d  */
end_comment

begin_class
DECL|class|OpaqueQueryNode
specifier|public
class|class
name|OpaqueQueryNode
extends|extends
name|QueryNodeImpl
block|{
DECL|field|schema
specifier|private
name|CharSequence
name|schema
init|=
literal|null
decl_stmt|;
DECL|field|value
specifier|private
name|CharSequence
name|value
init|=
literal|null
decl_stmt|;
comment|/**    * @param schema    *          - schema identifier    * @param value    *          - value that was not parsed    */
DECL|method|OpaqueQueryNode
specifier|public
name|OpaqueQueryNode
parameter_list|(
name|CharSequence
name|schema
parameter_list|,
name|CharSequence
name|value
parameter_list|)
block|{
name|this
operator|.
name|setLeaf
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<opaque schema='"
operator|+
name|this
operator|.
name|schema
operator|+
literal|"' value='"
operator|+
name|this
operator|.
name|value
operator|+
literal|"'/>"
return|;
block|}
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
block|{
return|return
literal|"@"
operator|+
name|this
operator|.
name|schema
operator|+
literal|":'"
operator|+
name|this
operator|.
name|value
operator|+
literal|"'"
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|OpaqueQueryNode
name|clone
init|=
operator|(
name|OpaqueQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
name|clone
operator|.
name|schema
operator|=
name|this
operator|.
name|schema
expr_stmt|;
name|clone
operator|.
name|value
operator|=
name|this
operator|.
name|value
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/**    * @return the schema    */
DECL|method|getSchema
specifier|public
name|CharSequence
name|getSchema
parameter_list|()
block|{
return|return
name|this
operator|.
name|schema
return|;
block|}
comment|/**    * @return the value    */
DECL|method|getValue
specifier|public
name|CharSequence
name|getValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
block|}
end_class

end_unit

