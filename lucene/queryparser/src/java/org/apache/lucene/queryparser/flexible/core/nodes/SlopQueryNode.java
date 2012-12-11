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
name|messages
operator|.
name|MessageImpl
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
name|QueryNodeError
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
name|messages
operator|.
name|QueryParserMessages
import|;
end_import

begin_comment
comment|/**  * A {@link SlopQueryNode} represents phrase query with a slop.  *   * From Lucene FAQ: Is there a way to use a proximity operator (like near or  * within) with Lucene? There is a variable called slop that allows you to  * perform NEAR/WITHIN-like queries. By default, slop is set to 0 so that only  * exact phrases will match. When using TextParser you can use this syntax to  * specify the slop: "doug cutting"~2 will find documents that contain  * "doug cutting" as well as ones that contain "cutting doug".  */
end_comment

begin_class
DECL|class|SlopQueryNode
specifier|public
class|class
name|SlopQueryNode
extends|extends
name|QueryNodeImpl
implements|implements
name|FieldableNode
block|{
DECL|field|value
specifier|private
name|int
name|value
init|=
literal|0
decl_stmt|;
comment|/**    * @param query    *          - QueryNode Tree with the phrase    * @param value    *          - slop value    */
DECL|method|SlopQueryNode
specifier|public
name|SlopQueryNode
parameter_list|(
name|QueryNode
name|query
parameter_list|,
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryNodeError
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|NODE_ACTION_NOT_SUPPORTED
argument_list|,
literal|"query"
argument_list|,
literal|"null"
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|allocate
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|getChild
specifier|public
name|QueryNode
name|getChild
parameter_list|()
block|{
return|return
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getValue
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|getValueString
specifier|private
name|CharSequence
name|getValueString
parameter_list|()
block|{
name|Float
name|f
init|=
name|Float
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
name|f
operator|.
name|longValue
argument_list|()
condition|)
return|return
literal|""
operator|+
name|f
operator|.
name|longValue
argument_list|()
return|;
else|else
return|return
literal|""
operator|+
name|f
return|;
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
literal|"<slop value='"
operator|+
name|getValueString
argument_list|()
operator|+
literal|"'>"
operator|+
literal|"\n"
operator|+
name|getChild
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"\n</slop>"
return|;
block|}
annotation|@
name|Override
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
block|{
if|if
condition|(
name|getChild
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|""
return|;
return|return
name|getChild
argument_list|()
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
operator|+
literal|"~"
operator|+
name|getValueString
argument_list|()
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
name|SlopQueryNode
name|clone
init|=
operator|(
name|SlopQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
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
annotation|@
name|Override
DECL|method|getField
specifier|public
name|CharSequence
name|getField
parameter_list|()
block|{
name|QueryNode
name|child
init|=
name|getChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|instanceof
name|FieldableNode
condition|)
block|{
return|return
operator|(
operator|(
name|FieldableNode
operator|)
name|child
operator|)
operator|.
name|getField
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|CharSequence
name|fieldName
parameter_list|)
block|{
name|QueryNode
name|child
init|=
name|getChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|instanceof
name|FieldableNode
condition|)
block|{
operator|(
operator|(
name|FieldableNode
operator|)
name|child
operator|)
operator|.
name|setField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

