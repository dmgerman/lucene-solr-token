begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|Collection
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
name|common
operator|.
name|StringUtils
import|;
end_import

begin_class
DECL|class|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
specifier|public
class|class
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
extends|extends
name|DefaultZkCredentialsProvider
block|{
DECL|field|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
init|=
literal|"zkDigestUsername"
decl_stmt|;
DECL|field|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
init|=
literal|"zkDigestPassword"
decl_stmt|;
DECL|field|zkDigestUsernameVMParamName
specifier|final
name|String
name|zkDigestUsernameVMParamName
decl_stmt|;
DECL|field|zkDigestPasswordVMParamName
specifier|final
name|String
name|zkDigestPasswordVMParamName
decl_stmt|;
DECL|method|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
specifier|public
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
specifier|public
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
parameter_list|(
name|String
name|zkDigestUsernameVMParamName
parameter_list|,
name|String
name|zkDigestPasswordVMParamName
parameter_list|)
block|{
name|this
operator|.
name|zkDigestUsernameVMParamName
operator|=
name|zkDigestUsernameVMParamName
expr_stmt|;
name|this
operator|.
name|zkDigestPasswordVMParamName
operator|=
name|zkDigestPasswordVMParamName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCredentials
specifier|protected
name|Collection
argument_list|<
name|ZkCredentials
argument_list|>
name|createCredentials
parameter_list|()
block|{
name|List
argument_list|<
name|ZkCredentials
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ZkCredentials
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|digestUsername
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|zkDigestUsernameVMParamName
argument_list|)
decl_stmt|;
name|String
name|digestPassword
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|zkDigestPasswordVMParamName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestUsername
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestPassword
argument_list|)
condition|)
block|{
try|try
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|ZkCredentials
argument_list|(
literal|"digest"
argument_list|,
operator|(
name|digestUsername
operator|+
literal|":"
operator|+
name|digestPassword
operator|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

