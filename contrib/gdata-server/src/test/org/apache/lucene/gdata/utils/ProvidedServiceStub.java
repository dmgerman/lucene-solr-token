begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Templates
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|Feed
import|;
end_import

begin_class
DECL|class|ProvidedServiceStub
specifier|public
class|class
name|ProvidedServiceStub
implements|implements
name|ProvidedService
block|{
DECL|field|SERVICE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_NAME
init|=
literal|"service"
decl_stmt|;
DECL|field|indexSchema
specifier|private
name|IndexSchema
name|indexSchema
decl_stmt|;
DECL|method|ProvidedServiceStub
specifier|public
name|ProvidedServiceStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
DECL|method|getFeedType
specifier|public
name|Class
name|getFeedType
parameter_list|()
block|{
return|return
name|Feed
operator|.
name|class
return|;
block|}
DECL|method|getExtensionProfile
specifier|public
name|ExtensionProfile
name|getExtensionProfile
parameter_list|()
block|{
return|return
operator|new
name|ExtensionProfile
argument_list|()
return|;
block|}
DECL|method|getEntryType
specifier|public
name|Class
name|getEntryType
parameter_list|()
block|{
return|return
name|Entry
operator|.
name|class
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|SERVICE_NAME
return|;
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
DECL|method|setIndexSchema
specifier|public
name|void
name|setIndexSchema
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|indexSchema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|indexSchema
operator|.
name|setName
argument_list|(
name|SERVICE_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndexSchema
specifier|public
name|IndexSchema
name|getIndexSchema
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexSchema
return|;
block|}
DECL|method|getTransformTemplate
specifier|public
name|Templates
name|getTransformTemplate
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

