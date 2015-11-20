begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|CloudConfigSetService
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
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
comment|/**  * Service class used by the CoreContainer to load ConfigSets for use in SolrCore  * creation.  */
end_comment

begin_class
DECL|class|ConfigSetService
specifier|public
specifier|abstract
class|class
name|ConfigSetService
block|{
DECL|method|createConfigSetService
specifier|public
specifier|static
name|ConfigSetService
name|createConfigSetService
parameter_list|(
name|NodeConfig
name|nodeConfig
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|ZkController
name|zkController
parameter_list|)
block|{
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
return|return
operator|new
name|CloudConfigSetService
argument_list|(
name|loader
argument_list|,
name|zkController
argument_list|)
return|;
if|if
condition|(
name|nodeConfig
operator|.
name|hasSchemaCache
argument_list|()
condition|)
return|return
operator|new
name|SchemaCaching
argument_list|(
name|loader
argument_list|,
name|nodeConfig
operator|.
name|getConfigSetBaseDirectory
argument_list|()
argument_list|)
return|;
return|return
operator|new
name|Default
argument_list|(
name|loader
argument_list|,
name|nodeConfig
operator|.
name|getConfigSetBaseDirectory
argument_list|()
argument_list|)
return|;
block|}
DECL|field|parentLoader
specifier|protected
specifier|final
name|SolrResourceLoader
name|parentLoader
decl_stmt|;
comment|/**    * Create a new ConfigSetService    * @param loader the CoreContainer's resource loader    */
DECL|method|ConfigSetService
specifier|public
name|ConfigSetService
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|parentLoader
operator|=
name|loader
expr_stmt|;
block|}
comment|/**    * Load the ConfigSet for a core    * @param dcore the core's CoreDescriptor    * @return a ConfigSet    */
DECL|method|getConfig
specifier|public
specifier|final
name|ConfigSet
name|getConfig
parameter_list|(
name|CoreDescriptor
name|dcore
parameter_list|)
block|{
name|SolrResourceLoader
name|coreLoader
init|=
name|createCoreResourceLoader
argument_list|(
name|dcore
argument_list|)
decl_stmt|;
try|try
block|{
name|SolrConfig
name|solrConfig
init|=
name|createSolrConfig
argument_list|(
name|dcore
argument_list|,
name|coreLoader
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|createIndexSchema
argument_list|(
name|dcore
argument_list|,
name|solrConfig
argument_list|)
decl_stmt|;
name|NamedList
name|properties
init|=
name|createConfigSetProperties
argument_list|(
name|dcore
argument_list|,
name|coreLoader
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConfigSet
argument_list|(
name|configName
argument_list|(
name|dcore
argument_list|)
argument_list|,
name|solrConfig
argument_list|,
name|schema
argument_list|,
name|properties
argument_list|)
return|;
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
name|SERVER_ERROR
argument_list|,
literal|"Could not load conf for core "
operator|+
name|dcore
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a SolrConfig object for a core    * @param cd the core's CoreDescriptor    * @param loader the core's resource loader    * @return a SolrConfig object    */
DECL|method|createSolrConfig
specifier|protected
name|SolrConfig
name|createSolrConfig
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
return|return
name|SolrConfig
operator|.
name|readFromResourceLoader
argument_list|(
name|loader
argument_list|,
name|cd
operator|.
name|getConfigName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create an IndexSchema object for a core    * @param cd the core's CoreDescriptor    * @param solrConfig the core's SolrConfig    * @return an IndexSchema    */
DECL|method|createIndexSchema
specifier|protected
name|IndexSchema
name|createIndexSchema
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|)
block|{
return|return
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|cd
operator|.
name|getSchemaName
argument_list|()
argument_list|,
name|solrConfig
argument_list|)
return|;
block|}
comment|/**    * Return the ConfigSet properties    * @param cd the core's CoreDescriptor    * @param loader the core's resource loader    * @return the ConfigSet properties    */
DECL|method|createConfigSetProperties
specifier|protected
name|NamedList
name|createConfigSetProperties
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
return|return
name|ConfigSetProperties
operator|.
name|readFromResourceLoader
argument_list|(
name|loader
argument_list|,
name|cd
operator|.
name|getConfigSetPropertiesName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a SolrResourceLoader for a core    * @param cd the core's CoreDescriptor    * @return a SolrResourceLoader    */
DECL|method|createCoreResourceLoader
specifier|protected
specifier|abstract
name|SolrResourceLoader
name|createCoreResourceLoader
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
function_decl|;
comment|/**    * Return a name for the ConfigSet for a core    * @param cd the core's CoreDescriptor    * @return a name for the core's ConfigSet    */
DECL|method|configName
specifier|public
specifier|abstract
name|String
name|configName
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
function_decl|;
comment|/**    * The default ConfigSetService.    *    * Loads a ConfigSet defined by the core's configSet property,    * looking for a directory named for the configSet property value underneath    * a base directory.  If no configSet property is set, loads the ConfigSet    * instead from the core's instance directory.    */
DECL|class|Default
specifier|public
specifier|static
class|class
name|Default
extends|extends
name|ConfigSetService
block|{
DECL|field|configSetBase
specifier|private
specifier|final
name|Path
name|configSetBase
decl_stmt|;
comment|/**      * Create a new ConfigSetService.Default      * @param loader the CoreContainer's resource loader      * @param configSetBase the base directory under which to look for config set directories      */
DECL|method|Default
specifier|public
name|Default
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|Path
name|configSetBase
parameter_list|)
block|{
name|super
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|this
operator|.
name|configSetBase
operator|=
name|configSetBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCoreResourceLoader
specifier|public
name|SolrResourceLoader
name|createCoreResourceLoader
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|Path
name|instanceDir
init|=
name|locateInstanceDir
argument_list|(
name|cd
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrResourceLoader
argument_list|(
name|instanceDir
argument_list|,
name|parentLoader
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|cd
operator|.
name|getSubstitutableProperties
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|configName
specifier|public
name|String
name|configName
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
return|return
operator|(
name|cd
operator|.
name|getConfigSet
argument_list|()
operator|==
literal|null
condition|?
literal|"instancedir "
else|:
literal|"configset "
operator|)
operator|+
name|locateInstanceDir
argument_list|(
name|cd
argument_list|)
return|;
block|}
DECL|method|locateInstanceDir
specifier|protected
name|Path
name|locateInstanceDir
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|String
name|configSet
init|=
name|cd
operator|.
name|getConfigSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|configSet
operator|==
literal|null
condition|)
return|return
name|Paths
operator|.
name|get
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
return|;
name|Path
name|configSetDirectory
init|=
name|configSetBase
operator|.
name|resolve
argument_list|(
name|configSet
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|configSetDirectory
argument_list|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not load configuration from directory "
operator|+
name|configSetDirectory
argument_list|)
throw|;
return|return
name|configSetDirectory
return|;
block|}
block|}
comment|/**    * A ConfigSetService that shares schema objects between cores    */
DECL|class|SchemaCaching
specifier|public
specifier|static
class|class
name|SchemaCaching
extends|extends
name|Default
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SchemaCaching
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|schemaCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|IndexSchema
argument_list|>
name|schemaCache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|SchemaCaching
specifier|public
name|SchemaCaching
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|Path
name|configSetBase
parameter_list|)
block|{
name|super
argument_list|(
name|loader
argument_list|,
name|configSetBase
argument_list|)
expr_stmt|;
block|}
DECL|field|cacheKeyFormatter
specifier|public
specifier|static
specifier|final
name|DateTimeFormatter
name|cacheKeyFormatter
init|=
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
literal|"yyyyMMddHHmmss"
argument_list|)
decl_stmt|;
DECL|method|cacheName
specifier|public
specifier|static
name|String
name|cacheName
parameter_list|(
name|Path
name|schemaFile
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|lastModified
init|=
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|schemaFile
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s:%s"
argument_list|,
name|schemaFile
operator|.
name|toString
argument_list|()
argument_list|,
name|cacheKeyFormatter
operator|.
name|print
argument_list|(
name|lastModified
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createIndexSchema
specifier|public
name|IndexSchema
name|createIndexSchema
parameter_list|(
specifier|final
name|CoreDescriptor
name|cd
parameter_list|,
specifier|final
name|SolrConfig
name|solrConfig
parameter_list|)
block|{
specifier|final
name|String
name|resourceNameToBeUsed
init|=
name|IndexSchemaFactory
operator|.
name|getResourceNameToBeUsed
argument_list|(
name|cd
operator|.
name|getSchemaName
argument_list|()
argument_list|,
name|solrConfig
argument_list|)
decl_stmt|;
name|Path
name|schemaFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|solrConfig
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|resourceNameToBeUsed
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|schemaFile
argument_list|)
condition|)
block|{
try|try
block|{
name|String
name|cachedName
init|=
name|cacheName
argument_list|(
name|schemaFile
argument_list|)
decl_stmt|;
return|return
name|schemaCache
operator|.
name|get
argument_list|(
name|cachedName
argument_list|,
operator|new
name|Callable
argument_list|<
name|IndexSchema
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexSchema
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Creating new index schema for core {}"
argument_list|,
name|cd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|cd
operator|.
name|getSchemaName
argument_list|()
argument_list|,
name|solrConfig
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
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
name|SERVER_ERROR
argument_list|,
literal|"Error creating index schema for core "
operator|+
name|cd
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Couldn't get last modified time for schema file {}: {}"
argument_list|,
name|schemaFile
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Will not use schema cache"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|cd
operator|.
name|getSchemaName
argument_list|()
argument_list|,
name|solrConfig
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

