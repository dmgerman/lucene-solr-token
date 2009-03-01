begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *<p>  * The default implementation of VariableResolver interface  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @see VariableResolver  * @since solr 1.3  */
end_comment

begin_class
DECL|class|VariableResolverImpl
specifier|public
class|class
name|VariableResolverImpl
extends|extends
name|VariableResolver
block|{
DECL|field|container
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|container
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Used for creating Evaluators    */
DECL|field|context
name|ContextImpl
name|context
decl_stmt|;
DECL|field|templateString
specifier|private
specifier|final
name|TemplateString
name|templateString
init|=
operator|new
name|TemplateString
argument_list|()
decl_stmt|;
DECL|method|VariableResolverImpl
specifier|public
name|VariableResolverImpl
parameter_list|()
block|{   }
comment|/**    * The current resolver instance    */
DECL|field|CURRENT_VARIABLE_RESOLVER
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|VariableResolverImpl
argument_list|>
name|CURRENT_VARIABLE_RESOLVER
init|=
operator|new
name|ThreadLocal
argument_list|<
name|VariableResolverImpl
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|addNamespace
specifier|public
name|VariableResolverImpl
name|addNamespace
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|parts
init|=
name|DOT_SPLIT
operator|.
name|split
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Map
name|ns
init|=
name|container
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
name|parts
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|ns
operator|.
name|put
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ns
operator|.
name|get
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|)
operator|==
literal|null
condition|)
block|{
name|ns
operator|.
name|put
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|=
operator|(
name|Map
operator|)
name|ns
operator|.
name|get
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ns
operator|.
name|get
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|)
operator|instanceof
name|Map
condition|)
block|{
name|ns
operator|=
operator|(
name|Map
operator|)
name|ns
operator|.
name|get
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ns
operator|.
name|put
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|=
operator|(
name|Map
operator|)
name|ns
operator|.
name|get
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|container
operator|.
name|putAll
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|removeNamespace
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|container
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|replaceTokens
specifier|public
name|String
name|replaceTokens
parameter_list|(
name|String
name|template
parameter_list|)
block|{
return|return
name|templateString
operator|.
name|replaceTokens
argument_list|(
name|template
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
return|return
name|container
return|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|parts
init|=
name|DOT_SPLIT
operator|.
name|split
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|CURRENT_VARIABLE_RESOLVER
operator|.
name|set
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|namespace
init|=
name|container
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|thePart
init|=
name|parts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|parts
operator|.
name|length
operator|-
literal|1
condition|)
block|{
return|return
name|namespace
operator|.
name|get
argument_list|(
name|thePart
argument_list|)
return|;
block|}
name|Object
name|temp
init|=
name|namespace
operator|.
name|get
argument_list|(
name|thePart
argument_list|)
decl_stmt|;
if|if
condition|(
name|temp
operator|==
literal|null
condition|)
block|{
return|return
name|namespace
operator|.
name|get
argument_list|(
name|mergeAll
argument_list|(
name|parts
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|temp
operator|instanceof
name|Map
condition|)
block|{
name|namespace
operator|=
operator|(
name|Map
operator|)
name|temp
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|CURRENT_VARIABLE_RESOLVER
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|mergeAll
specifier|private
name|String
name|mergeAll
parameter_list|(
name|String
index|[]
name|parts
parameter_list|,
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|==
name|parts
operator|.
name|length
operator|-
literal|1
condition|)
return|return
name|parts
index|[
name|parts
operator|.
name|length
operator|-
literal|1
index|]
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|parts
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|parts
index|[
name|j
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|<
name|parts
operator|.
name|length
operator|-
literal|1
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|DOT_SPLIT
specifier|static
specifier|final
name|Pattern
name|DOT_SPLIT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

