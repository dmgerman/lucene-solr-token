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
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|net
operator|.
name|InetAddress
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|Constants
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
name|SolrTestCaseJ4
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
name|SolrClient
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
name|SolrRequest
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
name|embedded
operator|.
name|JettySolrRunner
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
name|HttpSolrClient
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
name|request
operator|.
name|CollectionAdminRequest
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
name|response
operator|.
name|CollectionAdminResponse
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
name|cloud
operator|.
name|ZkStateReader
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
name|ModifiableSolrParams
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
name|core
operator|.
name|CoreContainer
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
name|handler
operator|.
name|admin
operator|.
name|CollectionsHandler
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
name|response
operator|.
name|SolrQueryResponse
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
name|security
operator|.
name|HttpParamDelegationTokenPlugin
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
name|security
operator|.
name|KerberosPlugin
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
name|servlet
operator|.
name|SolrRequestParsers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|HttpParamDelegationTokenPlugin
operator|.
name|USER_PARAM
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
name|HttpParamDelegationTokenPlugin
operator|.
name|REMOTE_HOST_PARAM
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
name|HttpParamDelegationTokenPlugin
operator|.
name|REMOTE_ADDRESS_PARAM
import|;
end_import

begin_class
DECL|class|TestSolrCloudWithSecureImpersonation
specifier|public
class|class
name|TestSolrCloudWithSecureImpersonation
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|NUM_SERVERS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_SERVERS
init|=
literal|2
decl_stmt|;
DECL|field|miniCluster
specifier|private
specifier|static
name|MiniSolrCloudCluster
name|miniCluster
decl_stmt|;
DECL|field|solrClient
specifier|private
specifier|static
name|SolrClient
name|solrClient
decl_stmt|;
DECL|method|getUsersFirstGroup
specifier|private
specifier|static
name|String
name|getUsersFirstGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|Groups
name|hGroups
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|Groups
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|group
init|=
literal|"*"
decl_stmt|;
comment|// accept any group if a group can't be found
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|g
init|=
name|hGroups
operator|.
name|getGroups
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
operator|&&
name|g
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|group
operator|=
name|g
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// if user/group doesn't exist on test box
block|}
return|return
name|group
return|;
block|}
DECL|method|getImpersonatorSettings
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getImpersonatorSettings
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filterProps
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"noGroups.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"anyHostAnyUser.groups"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"anyHostAnyUser.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"wrongHost.hosts"
argument_list|,
literal|"1.1.1.1.1.1"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"wrongHost.groups"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"noHosts.groups"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"localHostAnyGroup.groups"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|InetAddress
name|loopback
init|=
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
decl_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"localHostAnyGroup.hosts"
argument_list|,
name|loopback
operator|.
name|getCanonicalHostName
argument_list|()
operator|+
literal|","
operator|+
name|loopback
operator|.
name|getHostName
argument_list|()
operator|+
literal|","
operator|+
name|loopback
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"anyHostUsersGroup.groups"
argument_list|,
name|getUsersFirstGroup
argument_list|()
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"anyHostUsersGroup.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"bogusGroup.groups"
argument_list|,
literal|"__some_bogus_group"
argument_list|)
expr_stmt|;
name|filterProps
operator|.
name|put
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_PREFIX
operator|+
literal|"bogusGroup.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
return|return
name|filterProps
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|startup
specifier|public
specifier|static
name|void
name|startup
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"Hadoop does not work on Windows"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|,
name|HttpParamDelegationTokenPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KerberosPlugin
operator|.
name|DELEGATION_TOKEN_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.cookie.domain"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|impSettings
init|=
name|getImpersonatorSettings
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|impSettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|SolrRequestParsers
operator|.
name|DEFAULT
operator|.
name|setAddRequestHeadersToContext
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|solrXml
init|=
name|MiniSolrCloudCluster
operator|.
name|DEFAULT_CLOUD_SOLR_XML
operator|.
name|replace
argument_list|(
literal|"</solr>"
argument_list|,
literal|"<str name=\"collectionsHandler\">"
operator|+
name|ImpersonatorCollectionsHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"</str>\n"
operator|+
literal|"</solr>"
argument_list|)
decl_stmt|;
name|miniCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|solrXml
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|runner
init|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|solrClient
operator|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|runner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify that impersonator info is preserved in the request    */
DECL|class|ImpersonatorCollectionsHandler
specifier|public
specifier|static
class|class
name|ImpersonatorCollectionsHandler
extends|extends
name|CollectionsHandler
block|{
DECL|field|called
specifier|public
specifier|static
name|AtomicBoolean
name|called
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|ImpersonatorCollectionsHandler
specifier|public
name|ImpersonatorCollectionsHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|ImpersonatorCollectionsHandler
specifier|public
name|ImpersonatorCollectionsHandler
parameter_list|(
specifier|final
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|super
argument_list|(
name|coreContainer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|called
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|String
name|doAs
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_DO_AS_HTTP_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|doAs
operator|!=
literal|null
condition|)
block|{
name|HttpServletRequest
name|httpRequest
init|=
operator|(
name|HttpServletRequest
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"httpRequest"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|httpRequest
argument_list|)
expr_stmt|;
name|String
name|user
init|=
operator|(
name|String
operator|)
name|httpRequest
operator|.
name|getAttribute
argument_list|(
name|USER_PARAM
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|httpRequest
operator|.
name|getAttribute
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_USER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Before
DECL|method|clearCalledIndicator
specifier|public
name|void
name|clearCalledIndicator
parameter_list|()
throws|throws
name|Exception
block|{
name|ImpersonatorCollectionsHandler
operator|.
name|called
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|miniCluster
operator|!=
literal|null
condition|)
block|{
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|miniCluster
operator|=
literal|null
expr_stmt|;
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|solrClient
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|KerberosPlugin
operator|.
name|DELEGATION_TOKEN_ENABLED
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.cookie.domain"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|impSettings
init|=
name|getImpersonatorSettings
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|impSettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|)
expr_stmt|;
name|SolrRequestParsers
operator|.
name|DEFAULT
operator|.
name|setAddRequestHeadersToContext
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|create1ShardCollection
specifier|private
name|void
name|create1ShardCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|config
parameter_list|,
name|MiniSolrCloudCluster
name|solrCluster
parameter_list|)
throws|throws
name|Exception
block|{
name|CollectionAdminResponse
name|response
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|msp
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|super
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|msp
operator|.
name|set
argument_list|(
name|USER_PARAM
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
return|return
name|msp
return|;
block|}
block|}
decl_stmt|;
name|create
operator|.
name|setConfigName
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|create
operator|.
name|setCollectionName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|create
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|create
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|create
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|response
operator|=
name|create
operator|.
name|process
argument_list|(
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|!=
literal|0
operator|||
name|response
operator|.
name|getErrorMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not create collection. Response"
operator|+
name|response
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ZkStateReader
name|zkStateReader
init|=
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|name
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|getProxyRequest
specifier|private
name|SolrRequest
name|getProxyRequest
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|doAs
parameter_list|)
block|{
return|return
name|getProxyRequest
argument_list|(
name|user
argument_list|,
name|doAs
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getProxyRequest
specifier|private
name|SolrRequest
name|getProxyRequest
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|doAs
parameter_list|,
name|String
name|remoteHost
parameter_list|)
block|{
return|return
name|getProxyRequest
argument_list|(
name|user
argument_list|,
name|doAs
argument_list|,
name|remoteHost
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getProxyRequest
specifier|private
name|SolrRequest
name|getProxyRequest
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|doAs
parameter_list|,
name|String
name|remoteHost
parameter_list|,
name|String
name|remoteAddress
parameter_list|)
block|{
return|return
operator|new
name|CollectionAdminRequest
operator|.
name|List
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|super
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|USER_PARAM
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|KerberosPlugin
operator|.
name|IMPERSONATOR_DO_AS_HTTP_PARAM
argument_list|,
name|doAs
argument_list|)
expr_stmt|;
if|if
condition|(
name|remoteHost
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
name|REMOTE_HOST_PARAM
argument_list|,
name|remoteHost
argument_list|)
expr_stmt|;
if|if
condition|(
name|remoteAddress
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
name|REMOTE_ADDRESS_PARAM
argument_list|,
name|remoteAddress
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
return|;
block|}
DECL|method|getExpectedGroupExMsg
specifier|private
name|String
name|getExpectedGroupExMsg
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|doAs
parameter_list|)
block|{
return|return
literal|"User: "
operator|+
name|user
operator|+
literal|" is not allowed to impersonate "
operator|+
name|doAs
return|;
block|}
DECL|method|getExpectedHostExMsg
specifier|private
name|String
name|getExpectedHostExMsg
parameter_list|(
name|String
name|user
parameter_list|)
block|{
return|return
literal|"Unauthorized connection for super-user: "
operator|+
name|user
return|;
block|}
annotation|@
name|Test
DECL|method|testProxyNoConfigGroups
specifier|public
name|void
name|testProxyNoConfigGroups
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"noGroups"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|getExpectedGroupExMsg
argument_list|(
literal|"noGroups"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyWrongHost
specifier|public
name|void
name|testProxyWrongHost
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"wrongHost"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|getExpectedHostExMsg
argument_list|(
literal|"wrongHost"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyNoConfigHosts
specifier|public
name|void
name|testProxyNoConfigHosts
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"noHosts"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
comment|// FixMe: this should return an exception about the host being invalid,
comment|// but a bug (HADOOP-11077) causes an NPE instead.
comment|//assertTrue(ex.getMessage().contains(getExpectedHostExMsg("noHosts")));
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyValidateAnyHostAnyUser
specifier|public
name|void
name|testProxyValidateAnyHostAnyUser
parameter_list|()
throws|throws
name|Exception
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"anyHostAnyUser"
argument_list|,
literal|"bar"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ImpersonatorCollectionsHandler
operator|.
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProxyInvalidProxyUser
specifier|public
name|void
name|testProxyInvalidProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// wrong direction, should fail
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"bar"
argument_list|,
literal|"anyHostAnyUser"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|getExpectedGroupExMsg
argument_list|(
literal|"bar"
argument_list|,
literal|"anyHostAnyUser"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyValidateHost
specifier|public
name|void
name|testProxyValidateHost
parameter_list|()
throws|throws
name|Exception
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"localHostAnyGroup"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ImpersonatorCollectionsHandler
operator|.
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProxyValidateGroup
specifier|public
name|void
name|testProxyValidateGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"anyHostUsersGroup"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ImpersonatorCollectionsHandler
operator|.
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProxyUnknownRemote
specifier|public
name|void
name|testProxyUnknownRemote
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Use a reserved ip address
name|String
name|nonProxyUserConfiguredIpAddress
init|=
literal|"255.255.255.255"
decl_stmt|;
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"localHostAnyGroup"
argument_list|,
literal|"bar"
argument_list|,
literal|"unknownhost.bar.foo"
argument_list|,
name|nonProxyUserConfiguredIpAddress
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|getExpectedHostExMsg
argument_list|(
literal|"localHostAnyGroup"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyInvalidRemote
specifier|public
name|void
name|testProxyInvalidRemote
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|invalidIpAddress
init|=
literal|"-127.-128"
decl_stmt|;
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"localHostAnyGroup"
argument_list|,
literal|"bar"
argument_list|,
literal|"[ff01::114]"
argument_list|,
name|invalidIpAddress
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|getExpectedHostExMsg
argument_list|(
literal|"localHostAnyGroup"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyInvalidGroup
specifier|public
name|void
name|testProxyInvalidGroup
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|"bogusGroup"
argument_list|,
literal|"bar"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|getExpectedGroupExMsg
argument_list|(
literal|"bogusGroup"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testProxyNullProxyUser
specifier|public
name|void
name|testProxyNullProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|getProxyRequest
argument_list|(
literal|""
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected RemoteSolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
comment|// this exception is specific to our implementation, don't check a specific message.
block|}
block|}
annotation|@
name|Test
DECL|method|testForwarding
specifier|public
name|void
name|testForwarding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"forwardingCollection"
decl_stmt|;
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|miniCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|create1ShardCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf1"
argument_list|,
name|miniCluster
argument_list|)
expr_stmt|;
comment|// try a command to each node, one of them must be forwarded
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|collectionName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|USER_PARAM
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

