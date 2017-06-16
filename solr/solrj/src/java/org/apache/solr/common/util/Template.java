begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

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
name|Collections
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
name|function
operator|.
name|Function
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
name|Matcher
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

begin_class
DECL|class|Template
specifier|public
class|class
name|Template
block|{
DECL|field|template
specifier|public
specifier|final
name|String
name|template
decl_stmt|;
DECL|field|DOLLAR_BRACES_PLACEHOLDER_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|DOLLAR_BRACES_PLACEHOLDER_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[$][{](.*?)[}]"
argument_list|)
decl_stmt|;
DECL|field|BRACES_PLACEHOLDER_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|BRACES_PLACEHOLDER_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[{](.*?)[}]"
argument_list|)
decl_stmt|;
DECL|method|Template
specifier|public
name|Template
parameter_list|(
name|String
name|template
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|template
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|variable
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|startIndexes
operator|.
name|add
argument_list|(
name|m
operator|.
name|start
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|endOffsets
operator|.
name|add
argument_list|(
name|m
operator|.
name|end
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|variables
operator|.
name|add
argument_list|(
name|variable
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|variables
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
DECL|method|apply
specifier|public
name|String
name|apply
parameter_list|(
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|valueSupplier
parameter_list|)
block|{
if|if
condition|(
name|startIndexes
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|template
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startIndexes
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|replacement
init|=
name|valueSupplier
operator|.
name|apply
argument_list|(
name|variables
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sb
operator|.
name|replace
argument_list|(
name|startIndexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|endOffsets
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|replacement
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
else|else
block|{
return|return
name|template
return|;
block|}
block|}
DECL|field|startIndexes
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|startIndexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|endOffsets
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|endOffsets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|variables
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|variables
decl_stmt|;
block|}
end_class

end_unit

