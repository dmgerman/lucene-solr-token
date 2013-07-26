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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * This is a simple wrapper to SolrParams that will throw a 400  * exception if you ask for a parameter that does not exist.  Fields  * specified with  *   * In short, any value you for from a<code>RequiredSolrParams</code>   * will return a valid non-null value or throw a 400 exception.    * (If you pass in<code>null</code> as the default value, you can   * get a null return value)  *   *  * @since solr 1.2  */
end_comment

begin_class
DECL|class|RequiredSolrParams
specifier|public
class|class
name|RequiredSolrParams
extends|extends
name|SolrParams
block|{
DECL|field|params
specifier|protected
specifier|final
name|SolrParams
name|params
decl_stmt|;
DECL|method|RequiredSolrParams
specifier|public
name|RequiredSolrParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|/** get the param from params, fail if not found **/
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
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
name|BAD_REQUEST
argument_list|,
literal|"Missing required parameter: "
operator|+
name|param
argument_list|)
throw|;
block|}
return|return
name|val
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldParam
specifier|public
name|String
name|getFieldParam
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|param
parameter_list|)
block|{
specifier|final
name|String
name|fpname
init|=
name|fpname
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|params
operator|.
name|get
argument_list|(
name|fpname
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|val
condition|)
block|{
comment|// don't call this.get, we want a specified exception message
name|val
operator|=
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|val
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
name|BAD_REQUEST
argument_list|,
literal|"Missing required parameter: "
operator|+
name|fpname
operator|+
literal|" (or default: "
operator|+
name|param
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
return|return
name|val
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldParams
specifier|public
name|String
index|[]
name|getFieldParams
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|param
parameter_list|)
block|{
specifier|final
name|String
name|fpname
init|=
name|fpname
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
name|String
index|[]
name|val
init|=
name|params
operator|.
name|getParams
argument_list|(
name|fpname
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|val
condition|)
block|{
comment|// don't call this.getParams, we want a specified exception message
name|val
operator|=
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|val
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
name|BAD_REQUEST
argument_list|,
literal|"Missing required parameter: "
operator|+
name|fpname
operator|+
literal|" (or default: "
operator|+
name|param
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
return|return
name|val
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
name|param
parameter_list|)
block|{
name|String
index|[]
name|vals
init|=
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
operator|||
name|vals
operator|.
name|length
operator|==
literal|0
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
name|BAD_REQUEST
argument_list|,
literal|"Missing required parameter: "
operator|+
name|param
argument_list|)
throw|;
block|}
return|return
name|vals
return|;
block|}
comment|/** returns an Iterator over the parameter names */
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
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
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
literal|"{required("
operator|+
name|params
operator|+
literal|")}"
return|;
block|}
comment|//----------------------------------------------------------
comment|// Functions with a default value - pass directly to the
comment|// wrapped SolrParams (they won't return null - unless its the default)
comment|//----------------------------------------------------------
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|,
name|String
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|String
name|param
parameter_list|,
name|int
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getInt
argument_list|(
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|(
name|String
name|param
parameter_list|,
name|float
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFloat
argument_list|(
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|String
name|param
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getBool
argument_list|(
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInt
specifier|public
name|int
name|getFieldInt
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|int
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldBool
specifier|public
name|boolean
name|getFieldBool
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldFloat
specifier|public
name|float
name|getFieldFloat
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|float
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldFloat
argument_list|(
name|field
argument_list|,
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldParam
specifier|public
name|String
name|getFieldParam
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|def
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
block|}
end_class

end_unit

