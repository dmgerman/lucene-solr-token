begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.comp
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import

begin_comment
comment|/**  *  Wraps multiple Comparators to provide sub-sorting.  **/
end_comment

begin_class
DECL|class|MultipleFieldComparator
specifier|public
class|class
name|MultipleFieldComparator
implements|implements
name|StreamComparator
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|comps
specifier|private
name|StreamComparator
index|[]
name|comps
decl_stmt|;
DECL|method|MultipleFieldComparator
specifier|public
name|MultipleFieldComparator
parameter_list|(
name|StreamComparator
modifier|...
name|comps
parameter_list|)
block|{
name|this
operator|.
name|comps
operator|=
name|comps
expr_stmt|;
block|}
DECL|method|getComps
specifier|public
name|StreamComparator
index|[]
name|getComps
parameter_list|()
block|{
return|return
name|comps
return|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|t1
parameter_list|,
name|Tuple
name|t2
parameter_list|)
block|{
for|for
control|(
name|StreamComparator
name|comp
range|:
name|comps
control|)
block|{
name|int
name|i
init|=
name|comp
operator|.
name|compare
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamComparator
name|comp
range|:
name|comps
control|)
block|{
if|if
condition|(
name|comp
operator|instanceof
name|Expressible
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|comp
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This MultiComp contains a non-expressible comparator - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|StreamExpressionValue
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isDerivedFrom
specifier|public
name|boolean
name|isDerivedFrom
parameter_list|(
name|StreamComparator
name|base
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|base
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|base
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
name|MultipleFieldComparator
name|baseComp
init|=
operator|(
name|MultipleFieldComparator
operator|)
name|base
decl_stmt|;
if|if
condition|(
name|baseComp
operator|.
name|comps
operator|.
name|length
operator|>=
name|comps
operator|.
name|length
condition|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|comps
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
if|if
condition|(
operator|!
name|comps
index|[
name|idx
index|]
operator|.
name|isDerivedFrom
argument_list|(
name|baseComp
operator|.
name|comps
index|[
name|idx
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|copyAliased
specifier|public
name|MultipleFieldComparator
name|copyAliased
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aliases
parameter_list|)
block|{
name|StreamComparator
index|[]
name|aliasedComps
init|=
operator|new
name|StreamComparator
index|[
name|comps
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|comps
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|aliasedComps
index|[
name|idx
index|]
operator|=
name|comps
index|[
name|idx
index|]
operator|.
name|copyAliased
argument_list|(
name|aliases
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultipleFieldComparator
argument_list|(
name|aliasedComps
argument_list|)
return|;
block|}
block|}
end_class

end_unit

