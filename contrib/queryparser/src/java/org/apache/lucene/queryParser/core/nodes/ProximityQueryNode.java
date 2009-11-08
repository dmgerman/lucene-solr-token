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
comment|/**  * A {@link ProximityQueryNode} represents a query where the terms should meet  * specific distance conditions. (a b c) WITHIN [SENTENCE|PARAGRAPH|NUMBER]  * [INORDER] ("a" "b" "c") WITHIN [SENTENCE|PARAGRAPH|NUMBER] [INORDER]  *   * TODO: Add this to the future standard Lucene parser/processor/builder  */
end_comment

begin_class
DECL|class|ProximityQueryNode
specifier|public
class|class
name|ProximityQueryNode
extends|extends
name|BooleanQueryNode
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|9018220596680832916L
decl_stmt|;
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enum constant|PARAGRAPH
name|PARAGRAPH
block|{
annotation|@
name|Override
name|CharSequence
name|toQueryString
parameter_list|()
block|{
return|return
literal|"WITHIN PARAGRAPH"
return|;
block|}
block|}
block|,
DECL|enum constant|SENTENCE
name|SENTENCE
block|{
annotation|@
name|Override
name|CharSequence
name|toQueryString
parameter_list|()
block|{
return|return
literal|"WITHIN SENTENCE"
return|;
block|}
block|}
block|,
DECL|enum constant|NUMBER
name|NUMBER
block|{
annotation|@
name|Override
name|CharSequence
name|toQueryString
parameter_list|()
block|{
return|return
literal|"WITHIN"
return|;
block|}
block|}
block|;
DECL|method|toQueryString
specifier|abstract
name|CharSequence
name|toQueryString
parameter_list|()
function_decl|;
block|}
comment|// utility class
DECL|class|ProximityType
specifier|static
specifier|public
class|class
name|ProximityType
block|{
DECL|field|pDistance
name|int
name|pDistance
init|=
literal|0
decl_stmt|;
DECL|field|pType
name|Type
name|pType
init|=
literal|null
decl_stmt|;
DECL|method|ProximityType
specifier|public
name|ProximityType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ProximityType
specifier|public
name|ProximityType
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|distance
parameter_list|)
block|{
name|this
operator|.
name|pType
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|pDistance
operator|=
name|distance
expr_stmt|;
block|}
block|}
DECL|field|proximityType
specifier|private
name|Type
name|proximityType
init|=
name|Type
operator|.
name|SENTENCE
decl_stmt|;
DECL|field|distance
specifier|private
name|int
name|distance
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|inorder
specifier|private
name|boolean
name|inorder
init|=
literal|false
decl_stmt|;
DECL|field|field
specifier|private
name|CharSequence
name|field
init|=
literal|null
decl_stmt|;
comment|/**    * @param clauses    *          - QueryNode children    * @param field    *          - field name    * @param type    *          - type of proximity query    * @param distance    *          - positive integer that specifies the distance    * @param inorder    *          - true, if the tokens should be matched in the order of the    *          clauses    */
DECL|method|ProximityQueryNode
specifier|public
name|ProximityQueryNode
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|clauses
parameter_list|,
name|CharSequence
name|field
parameter_list|,
name|Type
name|type
parameter_list|,
name|int
name|distance
parameter_list|,
name|boolean
name|inorder
parameter_list|)
block|{
name|super
argument_list|(
name|clauses
argument_list|)
expr_stmt|;
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|proximityType
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|inorder
operator|=
name|inorder
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|NUMBER
condition|)
block|{
if|if
condition|(
name|distance
operator|<=
literal|0
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
name|PARAMETER_VALUE_NOT_SUPPORTED
argument_list|,
literal|"distance"
argument_list|,
name|distance
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|distance
operator|=
name|distance
expr_stmt|;
block|}
block|}
name|clearFields
argument_list|(
name|clauses
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param clauses    *          - QueryNode children    * @param field    *          - field name    * @param type    *          - type of proximity query    * @param inorder    *          - true, if the tokens should be matched in the order of the    *          clauses    */
DECL|method|ProximityQueryNode
specifier|public
name|ProximityQueryNode
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|clauses
parameter_list|,
name|CharSequence
name|field
parameter_list|,
name|Type
name|type
parameter_list|,
name|boolean
name|inorder
parameter_list|)
block|{
name|this
argument_list|(
name|clauses
argument_list|,
name|field
argument_list|,
name|type
argument_list|,
operator|-
literal|1
argument_list|,
name|inorder
argument_list|)
expr_stmt|;
block|}
DECL|method|clearFields
specifier|static
specifier|private
name|void
name|clearFields
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|nodes
parameter_list|,
name|CharSequence
name|field
parameter_list|)
block|{
if|if
condition|(
name|nodes
operator|==
literal|null
operator|||
name|nodes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
for|for
control|(
name|QueryNode
name|clause
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|clause
operator|instanceof
name|FieldQueryNode
condition|)
block|{
operator|(
operator|(
name|FieldQueryNode
operator|)
name|clause
operator|)
operator|.
name|toQueryStringIgnoreFields
operator|=
literal|true
expr_stmt|;
operator|(
operator|(
name|FieldQueryNode
operator|)
name|clause
operator|)
operator|.
name|setField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getProximityType
specifier|public
name|Type
name|getProximityType
parameter_list|()
block|{
return|return
name|this
operator|.
name|proximityType
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
name|String
name|distanceSTR
init|=
operator|(
operator|(
name|this
operator|.
name|distance
operator|==
operator|-
literal|1
operator|)
condition|?
operator|(
literal|""
operator|)
else|:
operator|(
literal|" distance='"
operator|+
name|this
operator|.
name|distance
operator|)
operator|+
literal|"'"
operator|)
decl_stmt|;
if|if
condition|(
name|getChildren
argument_list|()
operator|==
literal|null
operator|||
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|"<proximity field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' inorder='"
operator|+
name|this
operator|.
name|inorder
operator|+
literal|"' type='"
operator|+
name|this
operator|.
name|proximityType
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
operator|+
name|distanceSTR
operator|+
literal|"/>"
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<proximity field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' inorder='"
operator|+
name|this
operator|.
name|inorder
operator|+
literal|"' type='"
operator|+
name|this
operator|.
name|proximityType
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
operator|+
name|distanceSTR
operator|+
literal|">"
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|child
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n</proximity>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|String
name|withinSTR
init|=
name|this
operator|.
name|proximityType
operator|.
name|toQueryString
argument_list|()
operator|+
operator|(
operator|(
name|this
operator|.
name|distance
operator|==
operator|-
literal|1
operator|)
condition|?
operator|(
literal|""
operator|)
else|:
operator|(
literal|" "
operator|+
name|this
operator|.
name|distance
operator|)
operator|)
operator|+
operator|(
operator|(
name|this
operator|.
name|inorder
operator|)
condition|?
operator|(
literal|" INORDER"
operator|)
else|:
operator|(
literal|""
operator|)
operator|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|getChildren
argument_list|()
operator|==
literal|null
operator|||
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// no children case
block|}
else|else
block|{
name|String
name|filler
init|=
literal|""
decl_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|filler
argument_list|)
operator|.
name|append
argument_list|(
name|child
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
argument_list|)
expr_stmt|;
name|filler
operator|=
literal|" "
expr_stmt|;
block|}
block|}
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
literal|"( "
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|" ) "
operator|+
name|withinSTR
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|field
operator|+
literal|":(( "
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|" ) "
operator|+
name|withinSTR
operator|+
literal|")"
return|;
block|}
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
name|ProximityQueryNode
name|clone
init|=
operator|(
name|ProximityQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
name|clone
operator|.
name|proximityType
operator|=
name|this
operator|.
name|proximityType
expr_stmt|;
name|clone
operator|.
name|distance
operator|=
name|this
operator|.
name|distance
expr_stmt|;
name|clone
operator|.
name|field
operator|=
name|this
operator|.
name|field
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/**    * @return the distance    */
DECL|method|getDistance
specifier|public
name|int
name|getDistance
parameter_list|()
block|{
return|return
name|this
operator|.
name|distance
return|;
block|}
comment|/**    * returns null if the field was not specified in the query string    *     * @return the field    */
DECL|method|getField
specifier|public
name|CharSequence
name|getField
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
comment|/**    * returns null if the field was not specified in the query string    *     * @return the field    */
DECL|method|getFieldAsString
specifier|public
name|String
name|getFieldAsString
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|field
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|this
operator|.
name|field
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @param field    *          the field to set    */
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|CharSequence
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
comment|/**    * @return terms must be matched in the specified order    */
DECL|method|isInOrder
specifier|public
name|boolean
name|isInOrder
parameter_list|()
block|{
return|return
name|this
operator|.
name|inorder
return|;
block|}
block|}
end_class

end_unit

