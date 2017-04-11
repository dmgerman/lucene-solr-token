begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|collect
operator|.
name|ImmutableList
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
name|params
operator|.
name|CommonParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
operator|.
name|NamedListEntry
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
name|CloudConfig
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
name|core
operator|.
name|CoreDescriptor
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
name|CorePropertiesLocator
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
name|CoresLocator
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
name|NodeConfig
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
name|PluginInfo
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
name|SolrConfig
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
name|SolrCore
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
name|solr
operator|.
name|core
operator|.
name|SolrXmlConfig
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
name|UpdateRequestHandler
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
name|metrics
operator|.
name|reporters
operator|.
name|SolrJmxReporter
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
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrRequestHandler
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
name|SolrRequestInfo
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
name|BinaryQueryResponseWriter
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
name|QueryResponseWriter
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
name|schema
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
name|solr
operator|.
name|schema
operator|.
name|IndexSchemaFactory
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
name|DirectSolrConnection
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
name|update
operator|.
name|UpdateShardHandlerConfig
import|;
end_import

begin_comment
comment|/**  * This class provides a simple harness that may be useful when  * writing testcases.  *  *<p>  * This class lives in the tests-framework source tree (and not in the test source  * tree), so that it will be included with even the most minimal solr  * distribution, in order to encourage plugin writers to create unit   * tests for their plugins.  *  *  */
end_comment

begin_class
DECL|class|TestHarness
specifier|public
class|class
name|TestHarness
extends|extends
name|BaseTestHarness
block|{
DECL|field|coreName
specifier|public
name|String
name|coreName
decl_stmt|;
DECL|field|container
specifier|protected
specifier|volatile
name|CoreContainer
name|container
decl_stmt|;
DECL|field|updater
specifier|public
name|UpdateRequestHandler
name|updater
decl_stmt|;
comment|/**    * Creates a SolrConfig object for the specified coreName assuming it     * follows the basic conventions of being a relative path in the solrHome     * dir. (ie:<code>${solrHome}/${coreName}/conf/${confFile}</code>    */
DECL|method|createConfig
specifier|public
specifier|static
name|SolrConfig
name|createConfig
parameter_list|(
name|Path
name|solrHome
parameter_list|,
name|String
name|coreName
parameter_list|,
name|String
name|confFile
parameter_list|)
block|{
comment|// set some system properties for use by tests
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
try|try
block|{
return|return
operator|new
name|SolrConfig
argument_list|(
name|solrHome
operator|.
name|resolve
argument_list|(
name|coreName
argument_list|)
argument_list|,
name|confFile
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|xany
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|xany
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates a SolrConfig object for the default test core using {@link #createConfig(Path,String,String)}    */
DECL|method|createConfig
specifier|public
specifier|static
name|SolrConfig
name|createConfig
parameter_list|(
name|Path
name|solrHome
parameter_list|,
name|String
name|confFile
parameter_list|)
block|{
return|return
name|createConfig
argument_list|(
name|solrHome
argument_list|,
name|SolrTestCaseJ4
operator|.
name|DEFAULT_TEST_CORENAME
argument_list|,
name|confFile
argument_list|)
return|;
block|}
comment|/**    * @param coreName to initialize    * @param dataDirectory path for index data, will not be cleaned up    * @param solrConfig solronfig instance    * @param schemaFile schema filename    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|dataDirectory
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|schemaFile
parameter_list|)
block|{
name|this
argument_list|(
name|coreName
argument_list|,
name|dataDirectory
argument_list|,
name|solrConfig
argument_list|,
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|schemaFile
argument_list|,
name|solrConfig
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * @param dataDirectory path for index data, will not be cleaned up     * @param solrConfig solronfig instance     * @param schemaFile schema filename     */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|schemaFile
parameter_list|)
block|{
name|this
argument_list|(
name|dataDirectory
argument_list|,
name|solrConfig
argument_list|,
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|schemaFile
argument_list|,
name|solrConfig
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * @param dataDirectory path for index data, will not be cleaned up     * @param solrConfig solrconfig instance     * @param indexSchema schema instance     */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|dataDirectory
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|IndexSchema
name|indexSchema
parameter_list|)
block|{
name|this
argument_list|(
name|SolrTestCaseJ4
operator|.
name|DEFAULT_TEST_CORENAME
argument_list|,
name|dataDirectory
argument_list|,
name|solrConfig
argument_list|,
name|indexSchema
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param coreName to initialize    * @param dataDir path for index data, will not be cleaned up    * @param solrConfig solrconfig resource name    * @param indexSchema schema resource name    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|solrConfig
parameter_list|,
name|String
name|indexSchema
parameter_list|)
block|{
name|this
argument_list|(
name|buildTestNodeConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|SolrResourceLoader
operator|.
name|locateSolrHome
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|TestCoresLocator
argument_list|(
name|coreName
argument_list|,
name|dataDir
argument_list|,
name|solrConfig
argument_list|,
name|indexSchema
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
operator|(
name|coreName
operator|==
literal|null
operator|)
condition|?
name|SolrTestCaseJ4
operator|.
name|DEFAULT_TEST_CORENAME
else|:
name|coreName
expr_stmt|;
block|}
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|IndexSchema
name|indexSchema
parameter_list|)
block|{
name|this
argument_list|(
name|coreName
argument_list|,
name|dataDir
argument_list|,
name|solrConfig
operator|.
name|getResourceName
argument_list|()
argument_list|,
name|indexSchema
operator|.
name|getResourceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a TestHarness using a specific solr home directory and solr xml    * @param solrHome the solr home directory    * @param solrXml the text of a solrxml    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|Path
name|solrHome
parameter_list|,
name|String
name|solrXml
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHome
argument_list|)
argument_list|,
name|solrXml
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a TestHarness using a specific solr resource loader and solr xml    * @param loader the SolrResourceLoader to use    * @param solrXml the text of a solrxml    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|solrXml
parameter_list|)
block|{
name|this
argument_list|(
name|SolrXmlConfig
operator|.
name|fromString
argument_list|(
name|loader
argument_list|,
name|solrXml
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|NodeConfig
name|nodeConfig
parameter_list|)
block|{
name|this
argument_list|(
name|nodeConfig
argument_list|,
operator|new
name|CorePropertiesLocator
argument_list|(
name|nodeConfig
operator|.
name|getCoreRootDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a TestHarness using a specific config    * @param config the ConfigSolr to use    */
DECL|method|TestHarness
specifier|public
name|TestHarness
parameter_list|(
name|NodeConfig
name|config
parameter_list|,
name|CoresLocator
name|coresLocator
parameter_list|)
block|{
name|container
operator|=
operator|new
name|CoreContainer
argument_list|(
name|config
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|,
name|coresLocator
argument_list|)
expr_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
name|updater
operator|=
operator|new
name|UpdateRequestHandler
argument_list|()
expr_stmt|;
name|updater
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|buildTestNodeConfig
specifier|public
specifier|static
name|NodeConfig
name|buildTestNodeConfig
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|CloudConfig
name|cloudConfig
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"host"
argument_list|)
argument_list|,
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"hostPort"
argument_list|,
literal|8983
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"hostContext"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|setZkClientTimeout
argument_list|(
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"zkClientTimeout"
argument_list|,
literal|30000
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkHost"
argument_list|)
operator|==
literal|null
condition|)
name|cloudConfig
operator|=
literal|null
expr_stmt|;
name|UpdateShardHandlerConfig
name|updateShardHandlerConfig
init|=
operator|new
name|UpdateShardHandlerConfig
argument_list|(
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_MAXUPDATECONNECTIONS
argument_list|,
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_MAXUPDATECONNECTIONSPERHOST
argument_list|,
literal|30000
argument_list|,
literal|30000
argument_list|,
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_METRICNAMESTRATEGY
argument_list|)
decl_stmt|;
comment|// universal default metric reporter
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
literal|"class"
argument_list|,
name|SolrJmxReporter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PluginInfo
name|defaultPlugin
init|=
operator|new
name|PluginInfo
argument_list|(
literal|"reporter"
argument_list|,
name|attributes
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeConfig
operator|.
name|NodeConfigBuilder
argument_list|(
literal|"testNode"
argument_list|,
name|loader
argument_list|)
operator|.
name|setUseSchemaCache
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"shareSchema"
argument_list|)
argument_list|)
operator|.
name|setCloudConfig
argument_list|(
name|cloudConfig
argument_list|)
operator|.
name|setUpdateShardHandlerConfig
argument_list|(
name|updateShardHandlerConfig
argument_list|)
operator|.
name|setMetricReporterPlugins
argument_list|(
operator|new
name|PluginInfo
index|[]
block|{
name|defaultPlugin
block|}
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|class|TestCoresLocator
specifier|public
specifier|static
class|class
name|TestCoresLocator
extends|extends
name|ReadOnlyCoresLocator
block|{
DECL|field|coreName
specifier|final
name|String
name|coreName
decl_stmt|;
DECL|field|dataDir
specifier|final
name|String
name|dataDir
decl_stmt|;
DECL|field|solrConfig
specifier|final
name|String
name|solrConfig
decl_stmt|;
DECL|field|schema
specifier|final
name|String
name|schema
decl_stmt|;
DECL|method|TestCoresLocator
specifier|public
name|TestCoresLocator
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|solrConfig
parameter_list|,
name|String
name|schema
parameter_list|)
block|{
name|this
operator|.
name|coreName
operator|=
name|coreName
operator|==
literal|null
condition|?
name|SolrTestCaseJ4
operator|.
name|DEFAULT_TEST_CORENAME
else|:
name|coreName
expr_stmt|;
name|this
operator|.
name|dataDir
operator|=
name|dataDir
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|solrConfig
operator|=
name|solrConfig
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|discover
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|discover
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
name|coreName
argument_list|,
name|cc
operator|.
name|getCoreRootDirectory
argument_list|()
operator|.
name|resolve
argument_list|(
name|coreName
argument_list|)
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|,
name|dataDir
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|,
name|solrConfig
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_SCHEMA
argument_list|,
name|schema
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_COLLECTION
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"collection"
argument_list|,
literal|"collection1"
argument_list|)
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_SHARD
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"shard"
argument_list|,
literal|"shard1"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|container
return|;
block|}
comment|/** Gets a core that does not have its refcount incremented (i.e. there is no need to    * close when done).  This is not MT safe in conjunction with reloads!    */
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
comment|// get the core& decrease its refcount:
comment|// the container holds the core for the harness lifetime
name|SolrCore
name|core
init|=
name|container
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|core
return|;
block|}
comment|/** Gets the core with its reference count incremented.    * You must call core.close() when done!    */
DECL|method|getCoreInc
specifier|public
name|SolrCore
name|getCoreInc
parameter_list|()
block|{
return|return
name|container
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
return|;
block|}
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|reload
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Processes an "update" (add, commit or optimize) and    * returns the response as a String.    *    * @param xml The XML of the update    * @return The XML response to the update    */
DECL|method|update
specifier|public
name|String
name|update
parameter_list|(
name|String
name|xml
parameter_list|)
block|{
try|try
init|(
name|SolrCore
name|core
init|=
name|getCoreInc
argument_list|()
init|)
block|{
name|DirectSolrConnection
name|connection
init|=
operator|new
name|DirectSolrConnection
argument_list|(
name|core
argument_list|)
decl_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/update"
argument_list|)
decl_stmt|;
comment|// prefer the handler mapped to /update, but use our generic backup handler
comment|// if that lookup fails
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|handler
operator|=
name|updater
expr_stmt|;
block|}
return|return
name|connection
operator|.
name|request
argument_list|(
name|handler
argument_list|,
literal|null
argument_list|,
name|xml
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|SolrException
operator|)
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
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
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validates a "query" response against an array of XPath test strings    *    * @param req the Query to process    * @return null if all good, otherwise the first test that fails.    * @exception Exception any exception in the response.    * @exception IOException if there is a problem writing the XML    * @see LocalSolrQueryRequest    */
DECL|method|validateQuery
specifier|public
name|String
name|validateQuery
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|res
init|=
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|validateXPath
argument_list|(
name|res
argument_list|,
name|tests
argument_list|)
return|;
block|}
comment|/**    * Processes a "query" using a user constructed SolrQueryRequest    *    * @param req the Query to process, will be closed.    * @return The XML response to the query    * @exception Exception any exception in the response.    * @exception IOException if there is a problem writing the XML    * @see LocalSolrQueryRequest    */
DECL|method|query
specifier|public
name|String
name|query
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|query
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
argument_list|,
name|req
argument_list|)
return|;
block|}
comment|/**    * Processes a "query" using a user constructed SolrQueryRequest, and closes the request at the end.    *    * @param handler the name of the request handler to process the request    * @param req the Query to process, will be closed.    * @return The XML response to the query    * @exception Exception any exception in the response.    * @exception IOException if there is a problem writing the XML    * @see LocalSolrQueryRequest    */
DECL|method|query
specifier|public
name|String
name|query
parameter_list|(
name|String
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|responseWriter
operator|instanceof
name|BinaryQueryResponseWriter
condition|)
block|{
name|ByteArrayOutputStream
name|byteArrayOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|32000
argument_list|)
decl_stmt|;
name|BinaryQueryResponseWriter
name|writer
init|=
operator|(
name|BinaryQueryResponseWriter
operator|)
name|responseWriter
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|byteArrayOutputStream
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|byteArrayOutputStream
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
else|else
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|(
literal|32000
argument_list|)
decl_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|sw
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|SolrRequestInfo
operator|.
name|clearRequestInfo
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** It is the users responsibility to close the request object when done with it.    * This method does not set/clear SolrRequestInfo */
DECL|method|queryAndResponse
specifier|public
name|SolrQueryResponse
name|queryAndResponse
parameter_list|(
name|String
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|SolrCore
name|core
init|=
name|getCoreInc
argument_list|()
init|)
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
return|return
name|rsp
return|;
block|}
block|}
comment|/**    * Shuts down and frees any resources    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SolrCore
name|c
range|:
name|container
operator|.
name|getCores
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getOpenCount
argument_list|()
operator|>
literal|1
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SolrCore.getOpenCount()=="
operator|+
name|c
operator|.
name|getOpenCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|container
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getRequestFactory
specifier|public
name|LocalRequestFactory
name|getRequestFactory
parameter_list|(
name|String
name|qtype
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|LocalRequestFactory
name|f
init|=
operator|new
name|LocalRequestFactory
argument_list|()
decl_stmt|;
name|f
operator|.
name|qtype
operator|=
name|qtype
expr_stmt|;
name|f
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|f
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * 0 and Even numbered args are keys, Odd numbered args are values.    */
DECL|method|getRequestFactory
specifier|public
name|LocalRequestFactory
name|getRequestFactory
parameter_list|(
name|String
name|qtype
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
name|LocalRequestFactory
name|f
init|=
name|getRequestFactory
argument_list|(
name|qtype
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|f
operator|.
name|args
operator|.
name|put
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
DECL|method|getRequestFactory
specifier|public
name|LocalRequestFactory
name|getRequestFactory
parameter_list|(
name|String
name|qtype
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|LocalRequestFactory
name|f
init|=
name|getRequestFactory
argument_list|(
name|qtype
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|f
operator|.
name|args
operator|.
name|putAll
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * A Factory that generates LocalSolrQueryRequest objects using a    * specified set of default options.    */
DECL|class|LocalRequestFactory
specifier|public
class|class
name|LocalRequestFactory
block|{
DECL|field|qtype
specifier|public
name|String
name|qtype
init|=
literal|null
decl_stmt|;
DECL|field|start
specifier|public
name|int
name|start
init|=
literal|0
decl_stmt|;
DECL|field|limit
specifier|public
name|int
name|limit
init|=
literal|1000
decl_stmt|;
DECL|field|args
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|LocalRequestFactory
specifier|public
name|LocalRequestFactory
parameter_list|()
block|{     }
comment|/**      * Creates a LocalSolrQueryRequest based on variable args; for      * historical reasons, this method has some peculiar behavior:      *<ul>      *<li>If there is a single arg, then it is treated as the "q"      *       param, and the LocalSolrQueryRequest consists of that query      *       string along with "qt", "start", and "rows" params (based      *       on the qtype, start, and limit properties of this factory)      *       along with any other default "args" set on this factory.      *</li>      *<li>If there are multiple args, then there must be an even number      *       of them, and each pair of args is used as a key=value param in      *       the LocalSolrQueryRequest.<b>NOTE: In this usage, the "qtype",      *       "start", "limit", and "args" properties of this factory are      *       ignored.</b>      *</li>      *</ul>      *      * TODO: this isn't really safe in the presense of core reloads!      * Perhaps the best we could do is increment the core reference count      * and decrement it in the request close() method?      */
DECL|method|makeRequest
specifier|public
name|LocalSolrQueryRequest
name|makeRequest
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
block|{
if|if
condition|(
name|q
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|TestHarness
operator|.
name|this
operator|.
name|getCore
argument_list|()
argument_list|,
name|q
index|[
literal|0
index|]
argument_list|,
name|qtype
argument_list|,
name|start
argument_list|,
name|limit
argument_list|,
name|args
argument_list|)
return|;
block|}
if|if
condition|(
name|q
operator|.
name|length
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The length of the string array (query arguments) needs to be even"
argument_list|)
throw|;
block|}
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
index|[]
name|entries
init|=
operator|new
name|NamedListEntry
index|[
name|q
operator|.
name|length
operator|/
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|q
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|entries
index|[
name|i
operator|/
literal|2
index|]
operator|=
operator|new
name|NamedListEntry
argument_list|<>
argument_list|(
name|q
index|[
name|i
index|]
argument_list|,
name|q
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|(
name|entries
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|get
argument_list|(
literal|"wt"
argument_list|)
operator|==
literal|null
condition|)
name|nl
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
return|return
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|TestHarness
operator|.
name|this
operator|.
name|getCore
argument_list|()
argument_list|,
name|nl
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

