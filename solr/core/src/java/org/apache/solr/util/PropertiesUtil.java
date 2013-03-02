begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Breaking out some utility methods into a separate class as part of SOLR-4196. These utils have nothing to do with  * the DOM (they came from DomUtils) and it's really confusing to see them in something labeled DOM  */
end_comment

begin_class
DECL|class|PropertiesUtil
specifier|public
class|class
name|PropertiesUtil
block|{
comment|/*   * This method borrowed from Ant's PropertyHelper.replaceProperties:   *   http://svn.apache.org/repos/asf/ant/core/trunk/src/main/org/apache/tools/ant/PropertyHelper.java   */
DECL|method|substituteProperty
specifier|public
specifier|static
name|String
name|substituteProperty
parameter_list|(
name|String
name|value
parameter_list|,
name|Properties
name|coreProperties
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|indexOf
argument_list|(
literal|'$'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|value
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|fragments
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|propertyRefs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|parsePropertyString
argument_list|(
name|value
argument_list|,
name|fragments
argument_list|,
name|propertyRefs
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|fragments
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|j
init|=
name|propertyRefs
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|fragment
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
name|String
name|propertyName
init|=
name|j
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|defaultValue
init|=
literal|null
decl_stmt|;
name|int
name|colon_index
init|=
name|propertyName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon_index
operator|>
operator|-
literal|1
condition|)
block|{
name|defaultValue
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
name|colon_index
operator|+
literal|1
argument_list|)
expr_stmt|;
name|propertyName
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon_index
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|coreProperties
operator|!=
literal|null
condition|)
block|{
name|fragment
operator|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
name|fragment
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"No system property or default value specified for "
operator|+
name|propertyName
operator|+
literal|" value:"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
name|fragment
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
comment|/*    * This method borrowed from Ant's PropertyHelper.parsePropertyStringDefault:    *   http://svn.apache.org/repos/asf/ant/core/trunk/src/main/org/apache/tools/ant/PropertyHelper.java    */
DECL|method|parsePropertyString
specifier|private
specifier|static
name|void
name|parsePropertyString
parameter_list|(
name|String
name|value
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fragments
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|propertyRefs
parameter_list|)
block|{
name|int
name|prev
init|=
literal|0
decl_stmt|;
name|int
name|pos
decl_stmt|;
comment|//search for the next instance of $ from the 'prev' position
while|while
condition|(
operator|(
name|pos
operator|=
name|value
operator|.
name|indexOf
argument_list|(
literal|"$"
argument_list|,
name|prev
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
comment|//if there was any text before this, add it as a fragment
comment|//TODO, this check could be modified to go if pos>prev;
comment|//seems like this current version could stick empty strings
comment|//into the list
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|fragments
operator|.
name|add
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|prev
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//if we are at the end of the string, we tack on a $
comment|//then move past it
if|if
condition|(
name|pos
operator|==
operator|(
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
condition|)
block|{
name|fragments
operator|.
name|add
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|prev
operator|=
name|pos
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|!=
literal|'{'
condition|)
block|{
comment|//peek ahead to see if the next char is a property or not
comment|//not a property: insert the char as a literal
comment|/*               fragments.addElement(value.substring(pos + 1, pos + 2));               prev = pos + 2;               */
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|==
literal|'$'
condition|)
block|{
comment|//backwards compatibility two $ map to one mode
name|fragments
operator|.
name|add
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|prev
operator|=
name|pos
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
comment|//new behaviour: $X maps to $X for all values of X!='$'
name|fragments
operator|.
name|add
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|prev
operator|=
name|pos
operator|+
literal|2
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//property found, extract its name or bail on a typo
name|int
name|endName
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|endName
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Syntax error in property: "
operator|+
name|value
argument_list|)
throw|;
block|}
name|String
name|propertyName
init|=
name|value
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|2
argument_list|,
name|endName
argument_list|)
decl_stmt|;
name|fragments
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|propertyRefs
operator|.
name|add
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|prev
operator|=
name|endName
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|//no more $ signs found
comment|//if there is any tail to the string, append it
if|if
condition|(
name|prev
operator|<
name|value
operator|.
name|length
argument_list|()
condition|)
block|{
name|fragments
operator|.
name|add
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|prev
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

