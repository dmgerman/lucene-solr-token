begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|resource
operator|.
name|loader
operator|.
name|ResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|resource
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|exception
operator|.
name|ResourceNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|ExtendedProperties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringBufferInputStream
import|;
end_import

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

begin_class
DECL|class|SolrParamResourceLoader
specifier|public
class|class
name|SolrParamResourceLoader
extends|extends
name|ResourceLoader
block|{
DECL|field|templates
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|templates
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|method|SolrParamResourceLoader
specifier|public
name|SolrParamResourceLoader
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO: Consider using content streams, but need a template name associated with each stream
comment|// for now, a custom param convention of template.<name>=<template body> is a nice example
comment|// of per-request overrides of templates
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
name|params
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|names
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|names
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"template."
argument_list|)
condition|)
block|{
name|templates
operator|.
name|put
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|9
argument_list|)
operator|+
literal|".vm"
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|ExtendedProperties
name|extendedProperties
parameter_list|)
block|{   }
DECL|method|getResourceStream
specifier|public
name|InputStream
name|getResourceStream
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
name|String
name|template
init|=
name|templates
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
name|template
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|StringBufferInputStream
argument_list|(
name|template
argument_list|)
return|;
block|}
DECL|method|isSourceModified
specifier|public
name|boolean
name|isSourceModified
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|getLastModified
specifier|public
name|long
name|getLastModified
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

