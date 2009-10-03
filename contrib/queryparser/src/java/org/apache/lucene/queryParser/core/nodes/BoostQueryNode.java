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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link BoostQueryNode} boosts the QueryNode tree which is under this node.  * So, it must only and always have one child.  *   * The boost value may vary from 0.0 to 1.0.  *   */
end_comment

begin_class
DECL|class|BoostQueryNode
specifier|public
class|class
name|BoostQueryNode
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
literal|3929082630855807593L
decl_stmt|;
DECL|field|value
specifier|private
name|float
name|value
init|=
literal|0
decl_stmt|;
comment|/**    * Constructs a boost node    *     * @param query    *          the query to be boosted    * @param value    *          the boost value, it may vary from 0.0 to 1.0    *     * @throws QueryNodeException    */
DECL|method|BoostQueryNode
specifier|public
name|BoostQueryNode
parameter_list|(
name|QueryNode
name|query
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|QueryNodeException
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
comment|/**    * Returns the single child which this node boosts.    *     * @return the single child which this node boosts    */
DECL|method|getChild
specifier|public
name|QueryNode
name|getChild
parameter_list|()
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|getChildren
argument_list|()
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
return|return
literal|null
return|;
block|}
return|return
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Returns the boost value. It may vary from 0.0 to 1.0.    *     * @return the boost value    */
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
comment|/**    * Returns the boost value parsed to a string.    *     * @return the parsed value    */
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<boost value='"
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
literal|"\n</boost>"
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
literal|"^"
operator|+
name|getValueString
argument_list|()
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
name|BoostQueryNode
name|clone
init|=
operator|(
name|BoostQueryNode
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
block|}
end_class

end_unit

