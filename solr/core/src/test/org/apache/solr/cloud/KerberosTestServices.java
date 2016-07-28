begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|minikdc
operator|.
name|MiniKdc
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
name|impl
operator|.
name|Krb5HttpClientBuilder
import|;
end_import

begin_class
DECL|class|KerberosTestServices
specifier|public
class|class
name|KerberosTestServices
block|{
DECL|field|kdc
specifier|private
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|jaasConfiguration
specifier|private
name|JaasConfiguration
name|jaasConfiguration
decl_stmt|;
DECL|field|savedConfig
specifier|private
name|Configuration
name|savedConfig
decl_stmt|;
DECL|field|savedLocale
specifier|private
name|Locale
name|savedLocale
decl_stmt|;
DECL|method|KerberosTestServices
specifier|private
name|KerberosTestServices
parameter_list|(
name|MiniKdc
name|kdc
parameter_list|,
name|JaasConfiguration
name|jaasConfiguration
parameter_list|,
name|Configuration
name|savedConfig
parameter_list|,
name|Locale
name|savedLocale
parameter_list|)
block|{
name|this
operator|.
name|kdc
operator|=
name|kdc
expr_stmt|;
name|this
operator|.
name|jaasConfiguration
operator|=
name|jaasConfiguration
expr_stmt|;
name|this
operator|.
name|savedConfig
operator|=
name|savedConfig
expr_stmt|;
name|this
operator|.
name|savedLocale
operator|=
name|savedLocale
expr_stmt|;
block|}
DECL|method|getKdc
specifier|public
name|MiniKdc
name|getKdc
parameter_list|()
block|{
return|return
name|kdc
return|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|jaasConfiguration
argument_list|)
expr_stmt|;
name|Krb5HttpClientBuilder
operator|.
name|regenerateJaasConfiguration
argument_list|()
expr_stmt|;
if|if
condition|(
name|brokenLanguagesWithMiniKdc
operator|.
name|contains
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
argument_list|)
condition|)
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|savedConfig
argument_list|)
expr_stmt|;
name|Krb5HttpClientBuilder
operator|.
name|regenerateJaasConfiguration
argument_list|()
expr_stmt|;
name|Locale
operator|.
name|setDefault
argument_list|(
name|savedLocale
argument_list|)
expr_stmt|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Returns a MiniKdc that can be used for creating kerberos principals    * and keytabs.  Caller is responsible for starting/stopping the kdc.    */
DECL|method|getKdc
specifier|private
specifier|static
name|MiniKdc
name|getKdc
parameter_list|(
name|File
name|workDir
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|conf
init|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
decl_stmt|;
return|return
operator|new
name|MiniKdc
argument_list|(
name|conf
argument_list|,
name|workDir
argument_list|)
return|;
block|}
comment|/**    * Programmatic version of a jaas.conf file suitable for connecting    * to a SASL-configured zookeeper.    */
DECL|class|JaasConfiguration
specifier|private
specifier|static
class|class
name|JaasConfiguration
extends|extends
name|Configuration
block|{
DECL|field|clientEntry
specifier|private
specifier|static
name|AppConfigurationEntry
index|[]
name|clientEntry
decl_stmt|;
DECL|field|serverEntry
specifier|private
specifier|static
name|AppConfigurationEntry
index|[]
name|serverEntry
decl_stmt|;
DECL|field|clientAppName
DECL|field|serverAppName
specifier|private
name|String
name|clientAppName
init|=
literal|"Client"
decl_stmt|,
name|serverAppName
init|=
literal|"Server"
decl_stmt|;
comment|/**      * Add an entry to the jaas configuration with the passed in name,      * principal, and keytab. The other necessary options will be set for you.      *      * @param clientPrincipal The principal of the client      * @param clientKeytab The location of the keytab with the clientPrincipal      * @param serverPrincipal The principal of the server      * @param serverKeytab The location of the keytab with the serverPrincipal      */
DECL|method|JaasConfiguration
specifier|public
name|JaasConfiguration
parameter_list|(
name|String
name|clientPrincipal
parameter_list|,
name|File
name|clientKeytab
parameter_list|,
name|String
name|serverPrincipal
parameter_list|,
name|File
name|serverKeytab
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clientOptions
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|clientPrincipal
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"keyTab"
argument_list|,
name|clientKeytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"useKeyTab"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"storeKey"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"useTicketCache"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"refreshKrb5Config"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|jaasProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.jaas.debug"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jaasProp
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|jaasProp
argument_list|)
condition|)
block|{
name|clientOptions
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|clientEntry
operator|=
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|clientOptions
argument_list|)
block|}
expr_stmt|;
if|if
condition|(
name|serverPrincipal
operator|!=
literal|null
operator|&&
name|serverKeytab
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|serverOptions
init|=
operator|new
name|HashMap
argument_list|(
name|clientOptions
argument_list|)
decl_stmt|;
name|serverOptions
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|serverPrincipal
argument_list|)
expr_stmt|;
name|serverOptions
operator|.
name|put
argument_list|(
literal|"keytab"
argument_list|,
name|serverKeytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|serverEntry
operator|=
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|serverOptions
argument_list|)
block|}
expr_stmt|;
block|}
block|}
comment|/**      * Add an entry to the jaas configuration with the passed in principal and keytab,      * along with the app name.      *      * @param principal The principal      * @param keytab The keytab containing credentials for the principal      * @param appName The app name of the configuration      */
DECL|method|JaasConfiguration
specifier|public
name|JaasConfiguration
parameter_list|(
name|String
name|principal
parameter_list|,
name|File
name|keytab
parameter_list|,
name|String
name|appName
parameter_list|)
block|{
name|this
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|clientAppName
operator|=
name|appName
expr_stmt|;
name|serverAppName
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAppConfigurationEntry
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|clientAppName
argument_list|)
condition|)
block|{
return|return
name|clientEntry
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|serverAppName
argument_list|)
condition|)
block|{
return|return
name|serverEntry
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getKrb5LoginModuleName
specifier|private
name|String
name|getKrb5LoginModuleName
parameter_list|()
block|{
name|String
name|krb5LoginModuleName
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM"
argument_list|)
condition|)
block|{
name|krb5LoginModuleName
operator|=
literal|"com.ibm.security.auth.module.Krb5LoginModule"
expr_stmt|;
block|}
else|else
block|{
name|krb5LoginModuleName
operator|=
literal|"com.sun.security.auth.module.Krb5LoginModule"
expr_stmt|;
block|}
return|return
name|krb5LoginModuleName
return|;
block|}
block|}
comment|/**    *  These Locales don't generate dates that are compatibile with Hadoop MiniKdc.    */
DECL|field|brokenLanguagesWithMiniKdc
specifier|private
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|brokenLanguagesWithMiniKdc
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"th"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|,
operator|new
name|Locale
argument_list|(
literal|"ja"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|,
operator|new
name|Locale
argument_list|(
literal|"hi"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|kdcWorkDir
specifier|private
name|File
name|kdcWorkDir
decl_stmt|;
DECL|field|clientPrincipal
specifier|private
name|String
name|clientPrincipal
decl_stmt|;
DECL|field|clientKeytab
specifier|private
name|File
name|clientKeytab
decl_stmt|;
DECL|field|serverPrincipal
specifier|private
name|String
name|serverPrincipal
decl_stmt|;
DECL|field|serverKeytab
specifier|private
name|File
name|serverKeytab
decl_stmt|;
DECL|field|appName
specifier|private
name|String
name|appName
decl_stmt|;
DECL|field|savedLocale
specifier|private
name|Locale
name|savedLocale
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|savedLocale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
DECL|method|withKdc
specifier|public
name|Builder
name|withKdc
parameter_list|(
name|File
name|kdcWorkDir
parameter_list|)
block|{
name|this
operator|.
name|kdcWorkDir
operator|=
name|kdcWorkDir
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withJaasConfiguration
specifier|public
name|Builder
name|withJaasConfiguration
parameter_list|(
name|String
name|clientPrincipal
parameter_list|,
name|File
name|clientKeytab
parameter_list|,
name|String
name|serverPrincipal
parameter_list|,
name|File
name|serverKeytab
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clientPrincipal
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clientKeytab
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientPrincipal
operator|=
name|clientPrincipal
expr_stmt|;
name|this
operator|.
name|clientKeytab
operator|=
name|clientKeytab
expr_stmt|;
name|this
operator|.
name|serverPrincipal
operator|=
name|serverPrincipal
expr_stmt|;
name|this
operator|.
name|serverKeytab
operator|=
name|serverKeytab
expr_stmt|;
name|this
operator|.
name|appName
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withJaasConfiguration
specifier|public
name|Builder
name|withJaasConfiguration
parameter_list|(
name|String
name|principal
parameter_list|,
name|File
name|keytab
parameter_list|,
name|String
name|appName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|keytab
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientPrincipal
operator|=
name|principal
expr_stmt|;
name|this
operator|.
name|clientKeytab
operator|=
name|keytab
expr_stmt|;
name|this
operator|.
name|serverPrincipal
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|serverKeytab
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|appName
operator|=
name|appName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|KerberosTestServices
name|build
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MiniKdc
name|kdc
init|=
name|kdcWorkDir
operator|!=
literal|null
condition|?
name|getKdc
argument_list|(
name|kdcWorkDir
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|Configuration
name|oldConfig
init|=
name|clientPrincipal
operator|!=
literal|null
condition|?
name|Configuration
operator|.
name|getConfiguration
argument_list|()
else|:
literal|null
decl_stmt|;
name|JaasConfiguration
name|jaasConfiguration
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|clientPrincipal
operator|!=
literal|null
condition|)
block|{
name|jaasConfiguration
operator|=
operator|(
name|appName
operator|==
literal|null
operator|)
condition|?
operator|new
name|JaasConfiguration
argument_list|(
name|clientPrincipal
argument_list|,
name|clientKeytab
argument_list|,
name|serverPrincipal
argument_list|,
name|serverKeytab
argument_list|)
else|:
operator|new
name|JaasConfiguration
argument_list|(
name|clientPrincipal
argument_list|,
name|clientKeytab
argument_list|,
name|appName
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|KerberosTestServices
argument_list|(
name|kdc
argument_list|,
name|jaasConfiguration
argument_list|,
name|oldConfig
argument_list|,
name|savedLocale
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

