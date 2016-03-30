begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|SolrDocument
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|SolrQueryRequest
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
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_comment
comment|/**  *  * @since solr 4.0  */
end_comment

begin_class
DECL|class|ValueAugmenterFactory
specifier|public
class|class
name|ValueAugmenterFactory
extends|extends
name|TransformerFactory
block|{
DECL|field|value
specifier|protected
name|Object
name|value
init|=
literal|null
decl_stmt|;
DECL|field|defaultValue
specifier|protected
name|Object
name|defaultValue
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|value
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|defaultValue
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"defaultValue"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getObjectFrom
specifier|public
specifier|static
name|Object
name|getObjectFrom
parameter_list|(
name|String
name|val
parameter_list|,
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
literal|"int"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
if|if
condition|(
literal|"double"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
if|if
condition|(
literal|"float"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
if|if
condition|(
literal|"date"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
return|return
name|DateMathParser
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to parse "
operator|+
name|type
operator|+
literal|"="
operator|+
name|val
argument_list|,
name|ex
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
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|Object
name|val
init|=
name|value
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|String
name|v
init|=
name|params
operator|.
name|get
argument_list|(
literal|"v"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|val
operator|=
name|defaultValue
expr_stmt|;
block|}
else|else
block|{
name|val
operator|=
name|getObjectFrom
argument_list|(
name|v
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ValueAugmenter is missing a value -- should be defined in solrconfig or inline"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|ValueAugmenter
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|ValueAugmenter
class|class
name|ValueAugmenter
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|final
name|Object
name|value
decl_stmt|;
DECL|method|ValueAugmenter
specifier|public
name|ValueAugmenter
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

