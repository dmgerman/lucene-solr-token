begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
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
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|util
operator|.
name|ClientUtils
import|;
end_import

begin_comment
comment|/**   * A utility class to hold the facet response.  It could use the NamedList container,   * but for JSTL, it is nice to have something that implements List so it can be iterated   *    * @since solr 1.3   */
end_comment

begin_class
DECL|class|FacetField
specifier|public
class|class
name|FacetField
implements|implements
name|Serializable
block|{
DECL|class|Count
specifier|public
specifier|static
class|class
name|Count
implements|implements
name|Serializable
block|{
DECL|field|_name
specifier|private
name|String
name|_name
init|=
literal|null
decl_stmt|;
DECL|field|_count
specifier|private
name|long
name|_count
init|=
literal|0
decl_stmt|;
comment|// hang onto the FacetField for breadcrumb creation convenience
DECL|field|_ff
specifier|private
name|FacetField
name|_ff
init|=
literal|null
decl_stmt|;
DECL|method|Count
specifier|public
name|Count
parameter_list|(
name|FacetField
name|ff
parameter_list|,
name|String
name|n
parameter_list|,
name|long
name|c
parameter_list|)
block|{
name|_name
operator|=
name|n
expr_stmt|;
name|_count
operator|=
name|c
expr_stmt|;
name|_ff
operator|=
name|ff
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|_name
return|;
block|}
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|_name
operator|=
name|n
expr_stmt|;
block|}
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|_count
return|;
block|}
DECL|method|setCount
specifier|public
name|void
name|setCount
parameter_list|(
name|long
name|c
parameter_list|)
block|{
name|_count
operator|=
name|c
expr_stmt|;
block|}
DECL|method|getFacetField
specifier|public
name|FacetField
name|getFacetField
parameter_list|()
block|{
return|return
name|_ff
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
name|_name
operator|+
literal|" ("
operator|+
name|_count
operator|+
literal|")"
return|;
block|}
DECL|method|getAsFilterQuery
specifier|public
name|String
name|getAsFilterQuery
parameter_list|()
block|{
if|if
condition|(
name|_ff
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"facet_queries"
argument_list|)
condition|)
block|{
return|return
name|_name
return|;
block|}
return|return
name|ClientUtils
operator|.
name|escapeQueryChars
argument_list|(
name|_ff
operator|.
name|_name
argument_list|)
operator|+
literal|":"
operator|+
name|ClientUtils
operator|.
name|escapeQueryChars
argument_list|(
name|_name
argument_list|)
return|;
block|}
block|}
DECL|field|_name
specifier|private
name|String
name|_name
init|=
literal|null
decl_stmt|;
DECL|field|_values
specifier|private
name|List
argument_list|<
name|Count
argument_list|>
name|_values
init|=
literal|null
decl_stmt|;
DECL|method|FacetField
specifier|public
name|FacetField
parameter_list|(
specifier|final
name|String
name|n
parameter_list|)
block|{
name|_name
operator|=
name|n
expr_stmt|;
block|}
comment|/**     * Insert at the end of the list     */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|cnt
parameter_list|)
block|{
if|if
condition|(
name|_values
operator|==
literal|null
condition|)
block|{
name|_values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|30
argument_list|)
expr_stmt|;
block|}
name|_values
operator|.
name|add
argument_list|(
operator|new
name|Count
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|cnt
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Insert at the beginning of the list.     */
DECL|method|insert
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|cnt
parameter_list|)
block|{
if|if
condition|(
name|_values
operator|==
literal|null
condition|)
block|{
name|_values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|30
argument_list|)
expr_stmt|;
block|}
name|_values
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|Count
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|cnt
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|_name
return|;
block|}
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|Count
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|_values
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|Count
operator|>
name|emptyList
argument_list|()
else|:
name|_values
return|;
block|}
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|_values
operator|==
literal|null
condition|?
literal|0
else|:
name|_values
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getLimitingFields
specifier|public
name|FacetField
name|getLimitingFields
parameter_list|(
name|long
name|max
parameter_list|)
block|{
name|FacetField
name|ff
init|=
operator|new
name|FacetField
argument_list|(
name|_name
argument_list|)
decl_stmt|;
if|if
condition|(
name|_values
operator|!=
literal|null
condition|)
block|{
name|ff
operator|.
name|_values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|_values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Count
name|c
range|:
name|_values
control|)
block|{
if|if
condition|(
name|c
operator|.
name|_count
operator|<
name|max
condition|)
block|{
comment|// !equal to
name|ff
operator|.
name|_values
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ff
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
name|_name
operator|+
literal|":"
operator|+
name|_values
return|;
block|}
block|}
end_class

end_unit

