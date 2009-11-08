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
name|messages
operator|.
name|QueryParserMessages
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
name|core
operator|.
name|util
operator|.
name|UnescapedCharSequence
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
name|AllowLeadingWildcardAttribute
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
name|WildcardQueryNode
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
name|parser
operator|.
name|EscapeQuerySyntaxImpl
import|;
end_import

begin_comment
comment|/**  * This processor verifies if the attribute  * {@link AllowLeadingWildcardAttribute} is defined in the  * {@link QueryConfigHandler}. If it is and leading wildcard is not allowed, it  * looks for every {@link WildcardQueryNode} contained in the query node tree  * and throws an exception if any of them has a leading wildcard ('*' or '?').<br/>  *   * @see AllowLeadingWildcardAttribute  */
end_comment

begin_class
DECL|class|AllowLeadingWildcardProcessor
specifier|public
class|class
name|AllowLeadingWildcardProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|AllowLeadingWildcardProcessor
specifier|public
name|AllowLeadingWildcardProcessor
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
if|if
condition|(
name|getQueryConfigHandler
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|AllowLeadingWildcardAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|AllowLeadingWildcardAttribute
name|alwAttr
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|AllowLeadingWildcardAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|alwAttr
operator|.
name|isAllowLeadingWildcard
argument_list|()
condition|)
block|{
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
name|WildcardQueryNode
condition|)
block|{
name|WildcardQueryNode
name|wildcardNode
init|=
operator|(
name|WildcardQueryNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|wildcardNode
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Validate if the wildcard was escaped
if|if
condition|(
name|UnescapedCharSequence
operator|.
name|wasEscaped
argument_list|(
name|wildcardNode
operator|.
name|getText
argument_list|()
argument_list|,
literal|0
argument_list|)
condition|)
return|return
name|node
return|;
switch|switch
condition|(
name|wildcardNode
operator|.
name|getText
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
condition|)
block|{
case|case
literal|'*'
case|:
case|case
literal|'?'
case|:
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|LEADING_WILDCARD_NOT_ALLOWED
argument_list|,
name|node
operator|.
name|toQueryString
argument_list|(
operator|new
name|EscapeQuerySyntaxImpl
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
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

