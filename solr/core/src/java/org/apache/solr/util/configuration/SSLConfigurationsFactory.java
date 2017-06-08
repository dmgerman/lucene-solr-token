begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.configuration
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|configuration
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
DECL|class|SSLConfigurationsFactory
specifier|public
class|class
name|SSLConfigurationsFactory
block|{
DECL|field|currentConfigurations
specifier|static
specifier|private
name|SSLConfigurations
name|currentConfigurations
decl_stmt|;
comment|/**    * Creates if necessary and returns singleton object of Configurations. Can be used for    * static accessor of application-wide instance.    * @return Configurations object    */
DECL|method|current
specifier|static
specifier|public
name|SSLConfigurations
name|current
parameter_list|()
block|{
if|if
condition|(
name|currentConfigurations
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|SSLConfigurationsFactory
operator|.
name|class
init|)
block|{
if|if
condition|(
name|currentConfigurations
operator|==
literal|null
condition|)
block|{
name|currentConfigurations
operator|=
name|getInstance
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|currentConfigurations
return|;
block|}
DECL|method|getInstance
specifier|private
specifier|static
name|SSLConfigurations
name|getInstance
parameter_list|()
block|{
return|return
operator|new
name|SSLConfigurations
argument_list|(
name|System
operator|.
name|getenv
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setCurrent
specifier|static
specifier|public
specifier|synchronized
name|void
name|setCurrent
parameter_list|(
name|SSLConfigurations
name|configurations
parameter_list|)
block|{
name|currentConfigurations
operator|=
name|configurations
expr_stmt|;
block|}
block|}
end_class

end_unit
