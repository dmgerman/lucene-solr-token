begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
operator|.
name|RequestContinuesRecorderAuthenticationHandler
operator|.
name|REQUEST_CONTINUES_ATTR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
operator|.
name|HadoopAuthFilter
operator|.
name|DELEGATION_TOKEN_ZK_CLIENT
import|;
end_import

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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponseWrapper
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
name|iterators
operator|.
name|IteratorEnumeration
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
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
name|HttpClientBuilderFactory
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
name|SolrHttpClientBuilder
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
name|cloud
operator|.
name|ZkController
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
name|util
operator|.
name|SuppressForbidden
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
name|CoreContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This class implements a generic plugin which can use authentication schemes exposed by the  * Hadoop framework. This plugin supports following features  * - integration with authentication mehcanisms (e.g. kerberos)  * - Delegation token support  * - Proxy users (or secure impersonation) support  *  * This plugin enables defining configuration parameters required by the undelying Hadoop authentication  * mechanism. These configuration parameters can either be specified as a Java system property or the default  * value can be specified as part of the plugin configuration.  *  * The proxy users are configured by specifying relevant Hadoop configuration parameters. Please note that  * the delegation token support must be enabled for using the proxy users support.  *  * For Solr internal communication, this plugin enables configuring {@linkplain HttpClientBuilderFactory}  * implementation (e.g. based on kerberos).  */
end_comment

begin_class
DECL|class|GenericHadoopAuthPlugin
specifier|public
class|class
name|GenericHadoopAuthPlugin
extends|extends
name|AuthenticationPlugin
implements|implements
name|HttpClientBuilderPlugin
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * A property specifying the type of authentication scheme to be configured.    */
DECL|field|HADOOP_AUTH_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP_AUTH_TYPE
init|=
literal|"type"
decl_stmt|;
comment|/**    * A property specifies the value of the prefix to be used to define Java system property    * for configuring the authentication mechanism. The name of the Java system property is    * defined by appending the configuration parmeter namne to this prefix value e.g. if prefix    * is 'solr' then the Java system property 'solr.kerberos.principal' defines the value of    * configuration parameter 'kerberos.principal'.    */
DECL|field|SYSPROP_PREFIX_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|SYSPROP_PREFIX_PROPERTY
init|=
literal|"sysPropPrefix"
decl_stmt|;
comment|/**    * A property specifying the configuration parameters required by the authentication scheme    * defined by {@linkplain #HADOOP_AUTH_TYPE} property.    */
DECL|field|AUTH_CONFIG_NAMES_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|AUTH_CONFIG_NAMES_PROPERTY
init|=
literal|"authConfigs"
decl_stmt|;
comment|/**    * A property specifying the {@linkplain HttpClientBuilderFactory} used for the Solr internal    * communication.    */
DECL|field|HTTPCLIENT_BUILDER_FACTORY
specifier|private
specifier|static
specifier|final
name|String
name|HTTPCLIENT_BUILDER_FACTORY
init|=
literal|"clientBuilderFactory"
decl_stmt|;
comment|/**    * A property specifying the default values for the configuration parameters specified by the    * {@linkplain #AUTH_CONFIG_NAMES_PROPERTY} property. The default values are specified as a    * collection of key-value pairs (i.e. property-name : default_value).    */
DECL|field|DEFAULT_AUTH_CONFIGS_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_AUTH_CONFIGS_PROPERTY
init|=
literal|"defaultConfigs"
decl_stmt|;
comment|/**    * A property which enable (or disable) the delegation tokens functionality.    */
DECL|field|DELEGATION_TOKEN_ENABLED_PROPERTY
specifier|private
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_ENABLED_PROPERTY
init|=
literal|"enableDelegationToken"
decl_stmt|;
comment|/**    * A property which enables initialization of kerberos before connecting to Zookeeper.    */
DECL|field|INIT_KERBEROS_ZK
specifier|private
specifier|static
specifier|final
name|String
name|INIT_KERBEROS_ZK
init|=
literal|"initKerberosZk"
decl_stmt|;
comment|/**    * A property which configures proxy users for the underlying Hadoop authentication mechanism.    * This configuration is expressed as a collection of key-value pairs  (i.e. property-name : value).    */
DECL|field|PROXY_USER_CONFIGS
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_USER_CONFIGS
init|=
literal|"proxyUserConfigs"
decl_stmt|;
DECL|field|authFilter
specifier|private
name|AuthenticationFilter
name|authFilter
decl_stmt|;
DECL|field|factory
specifier|private
name|HttpClientBuilderFactory
name|factory
init|=
literal|null
decl_stmt|;
DECL|field|coreContainer
specifier|private
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|method|GenericHadoopAuthPlugin
specifier|public
name|GenericHadoopAuthPlugin
parameter_list|(
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{
try|try
block|{
name|String
name|delegationTokenEnabled
init|=
operator|(
name|String
operator|)
name|pluginConfig
operator|.
name|getOrDefault
argument_list|(
name|DELEGATION_TOKEN_ENABLED_PROPERTY
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|authFilter
operator|=
operator|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|delegationTokenEnabled
argument_list|)
operator|)
condition|?
operator|new
name|HadoopAuthFilter
argument_list|()
else|:
operator|new
name|AuthenticationFilter
argument_list|()
expr_stmt|;
comment|// Initialize kerberos before initializing curator instance.
name|boolean
name|initKerberosZk
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
operator|(
name|String
operator|)
name|pluginConfig
operator|.
name|getOrDefault
argument_list|(
name|INIT_KERBEROS_ZK
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|initKerberosZk
condition|)
block|{
operator|(
operator|new
name|Krb5HttpClientBuilder
argument_list|()
operator|)
operator|.
name|getBuilder
argument_list|()
expr_stmt|;
block|}
name|FilterConfig
name|conf
init|=
name|getInitFilterConfig
argument_list|(
name|pluginConfig
argument_list|)
decl_stmt|;
name|authFilter
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|httpClientBuilderFactory
init|=
operator|(
name|String
operator|)
name|pluginConfig
operator|.
name|get
argument_list|(
name|HTTPCLIENT_BUILDER_FACTORY
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpClientBuilderFactory
operator|!=
literal|null
condition|)
block|{
name|Class
name|c
init|=
name|Class
operator|.
name|forName
argument_list|(
name|httpClientBuilderFactory
argument_list|)
decl_stmt|;
name|factory
operator|=
operator|(
name|HttpClientBuilderFactory
operator|)
name|c
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ServletException
decl||
name|ClassNotFoundException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error initializing kerberos authentication plugin: "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getInitFilterConfig
specifier|protected
name|FilterConfig
name|getInitFilterConfig
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|type
init|=
operator|(
name|String
operator|)
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|pluginConfig
operator|.
name|get
argument_list|(
name|HADOOP_AUTH_TYPE
argument_list|)
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|HADOOP_AUTH_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|String
name|sysPropPrefix
init|=
operator|(
name|String
operator|)
name|pluginConfig
operator|.
name|getOrDefault
argument_list|(
name|SYSPROP_PREFIX_PROPERTY
argument_list|,
literal|"solr."
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|authConfigNames
init|=
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|pluginConfig
operator|.
name|getOrDefault
argument_list|(
name|AUTH_CONFIG_NAMES_PROPERTY
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|authConfigDefaults
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|pluginConfig
operator|.
name|getOrDefault
argument_list|(
name|DEFAULT_AUTH_CONFIGS_PROPERTY
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|proxyUserConfigs
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|pluginConfig
operator|.
name|getOrDefault
argument_list|(
name|PROXY_USER_CONFIGS
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|configName
range|:
name|authConfigNames
control|)
block|{
name|String
name|systemProperty
init|=
name|sysPropPrefix
operator|+
name|configName
decl_stmt|;
name|String
name|defaultConfigVal
init|=
name|authConfigDefaults
operator|.
name|get
argument_list|(
name|configName
argument_list|)
decl_stmt|;
name|String
name|configVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|systemProperty
argument_list|,
name|defaultConfigVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|configVal
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|configName
argument_list|,
name|configVal
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Configure proxy user settings.
name|params
operator|.
name|putAll
argument_list|(
name|proxyUserConfigs
argument_list|)
expr_stmt|;
specifier|final
name|ServletContext
name|servletContext
init|=
operator|new
name|AttributeOnlyServletContext
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Params: "
operator|+
name|params
argument_list|)
expr_stmt|;
name|ZkController
name|controller
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
decl_stmt|;
if|if
condition|(
name|controller
operator|!=
literal|null
condition|)
block|{
name|servletContext
operator|.
name|setAttribute
argument_list|(
name|DELEGATION_TOKEN_ZK_CLIENT
argument_list|,
name|controller
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FilterConfig
name|conf
init|=
operator|new
name|FilterConfig
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
return|return
name|servletContext
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
return|return
operator|new
name|IteratorEnumeration
argument_list|(
name|params
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|param
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFilterName
parameter_list|()
block|{
return|return
literal|"HadoopAuthFilter"
return|;
block|}
block|}
decl_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|doAuthenticate
specifier|public
name|boolean
name|doAuthenticate
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|HttpServletResponse
name|frsp
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
decl_stmt|;
comment|// Workaround until HADOOP-13346 is fixed.
name|HttpServletResponse
name|rspCloseShield
init|=
operator|new
name|HttpServletResponseWrapper
argument_list|(
name|frsp
argument_list|)
block|{
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Hadoop DelegationTokenAuthenticationFilter uses response writer, this"
operator|+
literal|"is providing a CloseShield on top of that"
argument_list|)
annotation|@
name|Override
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriterWrapper
argument_list|(
name|frsp
operator|.
name|getWriter
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{}
empty_stmt|;
block|}
decl_stmt|;
return|return
name|pw
return|;
block|}
block|}
decl_stmt|;
name|authFilter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|rspCloseShield
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
if|if
condition|(
name|authFilter
operator|instanceof
name|HadoopAuthFilter
condition|)
block|{
comment|// delegation token mgmt.
name|String
name|requestContinuesAttr
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
name|REQUEST_CONTINUES_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|requestContinuesAttr
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not find "
operator|+
name|REQUEST_CONTINUES_ATTR
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|requestContinuesAttr
argument_list|)
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpClientBuilder
specifier|public
name|SolrHttpClientBuilder
name|getHttpClientBuilder
parameter_list|(
name|SolrHttpClientBuilder
name|builder
parameter_list|)
block|{
return|return
operator|(
name|factory
operator|!=
literal|null
operator|)
condition|?
name|factory
operator|.
name|getHttpClientBuilder
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|builder
argument_list|)
argument_list|)
else|:
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|authFilter
operator|!=
literal|null
condition|)
block|{
name|authFilter
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

