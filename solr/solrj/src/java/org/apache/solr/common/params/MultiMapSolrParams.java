begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
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
name|Iterator
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MultiMapSolrParams
specifier|public
class|class
name|MultiMapSolrParams
extends|extends
name|SolrParams
block|{
DECL|field|map
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
decl_stmt|;
DECL|method|addParam
specifier|public
specifier|static
name|void
name|addParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
block|{
name|arr
operator|=
operator|new
name|String
index|[]
block|{
name|val
block|}
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|newarr
init|=
operator|new
name|String
index|[
name|arr
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|newarr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
name|newarr
index|[
name|arr
operator|.
name|length
index|]
operator|=
name|val
expr_stmt|;
name|arr
operator|=
name|newarr
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|addParam
specifier|public
specifier|static
name|void
name|addParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|vals
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|vals
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
index|[]
name|newarr
init|=
operator|new
name|String
index|[
name|arr
operator|.
name|length
operator|+
name|vals
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|newarr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newarr
argument_list|,
name|arr
operator|.
name|length
argument_list|,
name|vals
operator|.
name|length
argument_list|)
expr_stmt|;
name|arr
operator|=
name|newarr
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|MultiMapSolrParams
specifier|public
name|MultiMapSolrParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|arr
operator|==
literal|null
condition|?
literal|null
else|:
name|arr
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParameterNamesIterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParameterNamesIterator
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|getMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|map
return|;
block|}
comment|/** Returns a MultiMap view of the SolrParams as efficiently as possible.  The returned map may or may not be a backing implementation. */
DECL|method|asMultiMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|asMultiMap
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
return|return
name|asMultiMap
argument_list|(
name|params
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Returns a MultiMap view of the SolrParams.  A new map will be created if newCopy==true */
DECL|method|asMultiMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|asMultiMap
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|boolean
name|newCopy
parameter_list|)
block|{
if|if
condition|(
name|params
operator|instanceof
name|MultiMapSolrParams
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
operator|(
operator|(
name|MultiMapSolrParams
operator|)
name|params
operator|)
operator|.
name|getMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|newCopy
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|map
argument_list|)
return|;
block|}
return|return
name|map
return|;
block|}
elseif|else
if|if
condition|(
name|params
operator|instanceof
name|ModifiableSolrParams
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
operator|(
operator|(
name|ModifiableSolrParams
operator|)
name|params
operator|)
operator|.
name|getMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|newCopy
condition|)
block|{
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|map
argument_list|)
return|;
block|}
return|return
name|map
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
block|}
end_class

end_unit

