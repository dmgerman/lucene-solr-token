begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.nodes
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
name|nodes
operator|.
name|FieldQueryNode
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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link WildcardQueryNode} represents wildcard query This does not apply to  * phrases. Examples: a*b*c Fl?w? m?ke*g  */
end_comment

begin_class
DECL|class|WildcardQueryNode
specifier|public
class|class
name|WildcardQueryNode
extends|extends
name|FieldQueryNode
block|{
comment|/**    * @param field    *          - field name    * @param text    *          - value that contains one or more wild card characters (? or *)    * @param begin    *          - position in the query string    * @param end    *          - position in the query string    */
DECL|method|WildcardQueryNode
specifier|public
name|WildcardQueryNode
parameter_list|(
name|CharSequence
name|field
parameter_list|,
name|CharSequence
name|text
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
DECL|method|WildcardQueryNode
specifier|public
name|WildcardQueryNode
parameter_list|(
name|FieldQueryNode
name|fqn
parameter_list|)
block|{
name|this
argument_list|(
name|fqn
operator|.
name|getField
argument_list|()
argument_list|,
name|fqn
operator|.
name|getText
argument_list|()
argument_list|,
name|fqn
operator|.
name|getBegin
argument_list|()
argument_list|,
name|fqn
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
if|if
condition|(
name|isDefaultField
argument_list|(
name|this
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|text
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|field
operator|+
literal|":"
operator|+
name|this
operator|.
name|text
return|;
block|}
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
literal|"<wildcard field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' term='"
operator|+
name|this
operator|.
name|text
operator|+
literal|"'/>"
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|WildcardQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|WildcardQueryNode
name|clone
init|=
operator|(
name|WildcardQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
comment|// nothing to do here
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

