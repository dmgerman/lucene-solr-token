begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
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
name|runtime
operator|.
name|resource
operator|.
name|loader
operator|.
name|ResourceLoader
import|;
end_import

begin_comment
comment|/**   * Velocity resource loader wrapper around Solr resource loader   */
end_comment

begin_class
DECL|class|SolrVelocityResourceLoader
specifier|public
class|class
name|SolrVelocityResourceLoader
extends|extends
name|ResourceLoader
block|{
DECL|field|loader
specifier|private
name|SolrResourceLoader
name|loader
decl_stmt|;
DECL|method|SolrVelocityResourceLoader
specifier|public
name|SolrVelocityResourceLoader
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|ExtendedProperties
name|extendedProperties
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getResourceStream
specifier|public
name|InputStream
name|getResourceStream
parameter_list|(
name|String
name|template_name
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
try|try
block|{
return|return
name|loader
operator|.
name|openResource
argument_list|(
literal|"velocity/"
operator|+
name|template_name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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

