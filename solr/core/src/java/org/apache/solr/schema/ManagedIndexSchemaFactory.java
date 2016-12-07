begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|cloud
operator|.
name|SolrZkClient
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
name|ZkCmdExecutor
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
name|util
operator|.
name|SystemIdResolver
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_comment
comment|/** Factory for ManagedIndexSchema */
end_comment

begin_class
DECL|class|ManagedIndexSchemaFactory
specifier|public
class|class
name|ManagedIndexSchemaFactory
extends|extends
name|IndexSchemaFactory
implements|implements
name|SolrCoreAware
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
DECL|field|UPGRADED_SCHEMA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|UPGRADED_SCHEMA_EXTENSION
init|=
literal|".bak"
decl_stmt|;
DECL|field|SCHEMA_DOT_XML
specifier|private
specifier|static
specifier|final
name|String
name|SCHEMA_DOT_XML
init|=
literal|"schema.xml"
decl_stmt|;
DECL|field|DEFAULT_MANAGED_SCHEMA_RESOURCE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MANAGED_SCHEMA_RESOURCE_NAME
init|=
literal|"managed-schema"
decl_stmt|;
DECL|field|MANAGED_SCHEMA_RESOURCE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|MANAGED_SCHEMA_RESOURCE_NAME
init|=
literal|"managedSchemaResourceName"
decl_stmt|;
DECL|field|isMutable
specifier|private
name|boolean
name|isMutable
init|=
literal|true
decl_stmt|;
DECL|field|managedSchemaResourceName
specifier|private
name|String
name|managedSchemaResourceName
init|=
name|DEFAULT_MANAGED_SCHEMA_RESOURCE_NAME
decl_stmt|;
DECL|method|getManagedSchemaResourceName
specifier|public
name|String
name|getManagedSchemaResourceName
parameter_list|()
block|{
return|return
name|managedSchemaResourceName
return|;
block|}
DECL|field|config
specifier|private
name|SolrConfig
name|config
decl_stmt|;
DECL|field|loader
specifier|private
name|SolrResourceLoader
name|loader
decl_stmt|;
DECL|method|getResourceLoader
specifier|public
name|SolrResourceLoader
name|getResourceLoader
parameter_list|()
block|{
return|return
name|loader
return|;
block|}
DECL|field|resourceName
specifier|private
name|String
name|resourceName
decl_stmt|;
DECL|field|schema
specifier|private
name|ManagedIndexSchema
name|schema
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|zkIndexSchemaReader
specifier|private
name|ZkIndexSchemaReader
name|zkIndexSchemaReader
decl_stmt|;
DECL|field|loadedResource
specifier|private
name|String
name|loadedResource
decl_stmt|;
DECL|field|shouldUpgrade
specifier|private
name|boolean
name|shouldUpgrade
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|isMutable
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"mutable"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
literal|"mutable"
argument_list|)
expr_stmt|;
name|managedSchemaResourceName
operator|=
name|params
operator|.
name|get
argument_list|(
name|MANAGED_SCHEMA_RESOURCE_NAME
argument_list|,
name|DEFAULT_MANAGED_SCHEMA_RESOURCE_NAME
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|MANAGED_SCHEMA_RESOURCE_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|SCHEMA_DOT_XML
operator|.
name|equals
argument_list|(
name|managedSchemaResourceName
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
name|MANAGED_SCHEMA_RESOURCE_NAME
operator|+
literal|" can't be '"
operator|+
name|SCHEMA_DOT_XML
operator|+
literal|"'"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|msg
init|=
literal|"Unexpected arg(s): "
operator|+
name|args
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
comment|/**    * First, try to locate the managed schema file named in the managedSchemaResourceName    * param. If the managed schema file exists and is accessible, it is used to instantiate    * an IndexSchema.    *    * If the managed schema file can't be found, the resource named by the resourceName    * parameter is used to instantiate an IndexSchema.    *    * Once the IndexSchema is instantiated, if the managed schema file does not exist,    * the instantiated IndexSchema is persisted to the managed schema file named in the    * managedSchemaResourceName param, in the directory given by     * {@link org.apache.solr.core.SolrResourceLoader#getConfigDir()}, or if configs are    * in ZooKeeper, under {@link org.apache.solr.cloud.ZkSolrResourceLoader#getConfigSetZkPath()}.    *    * After the managed schema file is persisted, the original schema file is    * renamed by appending the extension named in {@link #UPGRADED_SCHEMA_EXTENSION}.    */
annotation|@
name|Override
DECL|method|create
specifier|public
name|ManagedIndexSchema
name|create
parameter_list|(
name|String
name|resourceName
parameter_list|,
name|SolrConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|resourceName
operator|=
name|resourceName
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|config
operator|.
name|getResourceLoader
argument_list|()
expr_stmt|;
name|InputStream
name|schemaInputStream
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|resourceName
condition|)
block|{
name|resourceName
operator|=
name|IndexSchema
operator|.
name|DEFAULT_SCHEMA_FILE
expr_stmt|;
block|}
name|int
name|schemaZkVersion
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
operator|)
condition|)
block|{
name|schemaInputStream
operator|=
name|readSchemaLocally
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// ZooKeeper
specifier|final
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
decl_stmt|;
specifier|final
name|SolrZkClient
name|zkClient
init|=
name|zkLoader
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
specifier|final
name|String
name|managedSchemaPath
init|=
name|zkLoader
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|managedSchemaResourceName
decl_stmt|;
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Attempt to load the managed schema
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|managedSchemaPath
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|schemaZkVersion
operator|=
name|stat
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|schemaInputStream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|loadedResource
operator|=
name|managedSchemaResourceName
expr_stmt|;
name|warnIfNonManagedSchemaExists
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"The schema is configured as managed, but managed schema resource "
operator|+
name|managedSchemaResourceName
operator|+
literal|" not found - loading non-managed schema "
operator|+
name|resourceName
operator|+
literal|" instead"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Error attempting to access "
operator|+
name|managedSchemaPath
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|schemaInputStream
condition|)
block|{
comment|// The managed schema file could not be found - load the non-managed schema
try|try
block|{
name|schemaInputStream
operator|=
name|loader
operator|.
name|openSchema
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
name|loadedResource
operator|=
name|resourceName
expr_stmt|;
name|shouldUpgrade
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
comment|// Retry to load the managed schema, in case it was created since the first attempt
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|managedSchemaPath
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|schemaZkVersion
operator|=
name|stat
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|schemaInputStream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|loadedResource
operator|=
name|managedSchemaPath
expr_stmt|;
name|warnIfNonManagedSchemaExists
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
if|if
condition|(
name|e1
operator|instanceof
name|InterruptedException
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// Restore the interrupted status
block|}
specifier|final
name|String
name|msg
init|=
literal|"Error loading both non-managed schema '"
operator|+
name|resourceName
operator|+
literal|"' and managed schema '"
operator|+
name|managedSchemaResourceName
operator|+
literal|"'"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
name|InputSource
name|inputSource
init|=
operator|new
name|InputSource
argument_list|(
name|schemaInputStream
argument_list|)
decl_stmt|;
name|inputSource
operator|.
name|setSystemId
argument_list|(
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|loadedResource
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|schema
operator|=
operator|new
name|ManagedIndexSchema
argument_list|(
name|config
argument_list|,
name|loadedResource
argument_list|,
name|inputSource
argument_list|,
name|isMutable
argument_list|,
name|managedSchemaResourceName
argument_list|,
name|schemaZkVersion
argument_list|,
name|getSchemaUpdateLock
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Error instantiating ManagedIndexSchema"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shouldUpgrade
condition|)
block|{
comment|// Persist the managed schema if it doesn't already exist
name|upgradeToManagedSchema
argument_list|()
expr_stmt|;
block|}
return|return
name|schema
return|;
block|}
DECL|method|readSchemaLocally
specifier|private
name|InputStream
name|readSchemaLocally
parameter_list|()
block|{
name|InputStream
name|schemaInputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Attempt to load the managed schema
name|schemaInputStream
operator|=
name|loader
operator|.
name|openSchema
argument_list|(
name|managedSchemaResourceName
argument_list|)
expr_stmt|;
name|loadedResource
operator|=
name|managedSchemaResourceName
expr_stmt|;
name|warnIfNonManagedSchemaExists
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"The schema is configured as managed, but managed schema resource "
operator|+
name|managedSchemaResourceName
operator|+
literal|" not found - loading non-managed schema "
operator|+
name|resourceName
operator|+
literal|" instead"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|schemaInputStream
condition|)
block|{
comment|// The managed schema file could not be found - load the non-managed schema
try|try
block|{
name|schemaInputStream
operator|=
name|loader
operator|.
name|openSchema
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
name|loadedResource
operator|=
name|resourceName
expr_stmt|;
name|shouldUpgrade
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Error loading both non-managed schema '"
operator|+
name|resourceName
operator|+
literal|"' and managed schema '"
operator|+
name|managedSchemaResourceName
operator|+
literal|"'"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|schemaInputStream
return|;
block|}
comment|/**    * Return whether a non-managed schema exists, either in local storage or on ZooKeeper.     */
DECL|method|warnIfNonManagedSchemaExists
specifier|private
name|void
name|warnIfNonManagedSchemaExists
parameter_list|()
block|{
if|if
condition|(
operator|!
name|resourceName
operator|.
name|equals
argument_list|(
name|managedSchemaResourceName
argument_list|)
condition|)
block|{
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
name|config
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
decl_stmt|;
name|String
name|nonManagedSchemaPath
init|=
name|zkLoader
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|resourceName
decl_stmt|;
try|try
block|{
name|exists
operator|=
name|zkLoader
operator|.
name|getZkController
argument_list|()
operator|.
name|pathExists
argument_list|(
name|nonManagedSchemaPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// Restore the interrupted status
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// Log as warning and suppress the exception
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
comment|// log as warning and suppress the exception
name|log
operator|.
name|warn
argument_list|(
literal|"Error checking for the existence of the non-managed schema "
operator|+
name|resourceName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Config is not in ZooKeeper
name|InputStream
name|nonManagedSchemaInputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nonManagedSchemaInputStream
operator|=
name|loader
operator|.
name|openSchema
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|nonManagedSchemaInputStream
condition|)
block|{
name|exists
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// This is expected when the non-managed schema does not exist
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|nonManagedSchemaInputStream
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|exists
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"The schema has been upgraded to managed, but the non-managed schema "
operator|+
name|resourceName
operator|+
literal|" is still loadable.  PLEASE REMOVE THIS FILE."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Persist the managed schema and rename the non-managed schema     * by appending {@link #UPGRADED_SCHEMA_EXTENSION}.    *    * Failure to rename the non-managed schema will be logged as a warning,    * and no exception will be thrown.    */
DECL|method|upgradeToManagedSchema
specifier|private
name|void
name|upgradeToManagedSchema
parameter_list|()
block|{
name|SolrResourceLoader
name|loader
init|=
name|config
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|zkUgradeToManagedSchema
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Configs are not on ZooKeeper
name|schema
operator|.
name|persistManagedSchema
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Only create it - don't update it if it already exists
comment|// After successfully persisting the managed schema, rename the non-managed
comment|// schema file by appending UPGRADED_SCHEMA_EXTENSION to its name.
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|managedSchemaResourceName
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"On upgrading to managed schema, did not rename non-managed schema '"
operator|+
name|resourceName
operator|+
literal|"' because it's the same as the managed schema's name."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|File
name|nonManagedSchemaFile
init|=
name|locateConfigFile
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|nonManagedSchemaFile
condition|)
block|{
comment|// Don't throw an exception for failure to rename the non-managed schema
name|log
operator|.
name|warn
argument_list|(
literal|"On upgrading to managed schema, did not rename non-managed schema "
operator|+
name|resourceName
operator|+
literal|" because it's neither an absolute file "
operator|+
literal|"nor under SolrConfig.getConfigDir() or the current directory."
operator|+
literal|"  PLEASE REMOVE THIS FILE."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|upgradedSchemaFile
init|=
operator|new
name|File
argument_list|(
name|nonManagedSchemaFile
operator|+
name|UPGRADED_SCHEMA_EXTENSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|nonManagedSchemaFile
operator|.
name|renameTo
argument_list|(
name|upgradedSchemaFile
argument_list|)
condition|)
block|{
comment|// Set the resource name to the managed schema so that the CoreAdminHandler returns a findable filename
name|schema
operator|.
name|setResourceName
argument_list|(
name|managedSchemaResourceName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"After upgrading to managed schema, renamed the non-managed schema "
operator|+
name|nonManagedSchemaFile
operator|+
literal|" to "
operator|+
name|upgradedSchemaFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Don't throw an exception for failure to rename the non-managed schema
name|log
operator|.
name|warn
argument_list|(
literal|"Can't rename "
operator|+
name|nonManagedSchemaFile
operator|.
name|toString
argument_list|()
operator|+
literal|" to "
operator|+
name|upgradedSchemaFile
operator|.
name|toString
argument_list|()
operator|+
literal|" - PLEASE REMOVE THIS FILE."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Finds any resource by its name on the filesystem.  The classpath is not consulted.    *    * If the resource is not absolute, the resource is sought in $configDir and then in the current directory.    *    *@return the File for the named resource, or null if it can't be found    */
DECL|method|locateConfigFile
specifier|private
name|File
name|locateConfigFile
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|String
name|location
init|=
name|config
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|resourceLocation
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|equals
argument_list|(
name|resource
argument_list|)
operator|||
name|location
operator|.
name|startsWith
argument_list|(
literal|"classpath:"
argument_list|)
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|File
argument_list|(
name|location
argument_list|)
return|;
block|}
comment|/**    * Persist the managed schema to ZooKeeper and rename the non-managed schema     * by appending {@link #UPGRADED_SCHEMA_EXTENSION}.    *    * Failure to rename the non-managed schema will be logged as a warning,    * and no exception will be thrown.    */
DECL|method|zkUgradeToManagedSchema
specifier|private
name|void
name|zkUgradeToManagedSchema
parameter_list|()
block|{
name|schema
operator|.
name|persistManagedSchemaToZooKeeper
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Only create, don't update it if it already exists
comment|// After successfully persisting the managed schema, rename the non-managed
comment|// schema znode by appending UPGRADED_SCHEMA_EXTENSION to its name.
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|managedSchemaResourceName
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"On upgrading to managed schema, did not rename non-managed schema "
operator|+
name|resourceName
operator|+
literal|" because it's the same as the managed schema's name."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Rename the non-managed schema znode in ZooKeeper
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
decl_stmt|;
specifier|final
name|String
name|nonManagedSchemaPath
init|=
name|zkLoader
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|resourceName
decl_stmt|;
try|try
block|{
name|ZkController
name|zkController
init|=
name|zkLoader
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|ZkCmdExecutor
name|zkCmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|(
name|zkController
operator|.
name|getClientTimeout
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkController
operator|.
name|pathExists
argument_list|(
name|nonManagedSchemaPath
argument_list|)
condition|)
block|{
comment|// First, copy the non-managed schema znode content to the upgraded schema znode
name|byte
index|[]
name|bytes
init|=
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|nonManagedSchemaPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|String
name|upgradedSchemaPath
init|=
name|nonManagedSchemaPath
operator|+
name|UPGRADED_SCHEMA_EXTENSION
decl_stmt|;
name|zkCmdExecutor
operator|.
name|ensureExists
argument_list|(
name|upgradedSchemaPath
argument_list|,
name|zkController
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|setData
argument_list|(
name|upgradedSchemaPath
argument_list|,
name|bytes
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Then delete the non-managed schema znode
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|delete
argument_list|(
name|nonManagedSchemaPath
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Set the resource name to the managed schema so that the CoreAdminHandler returns a findable filename
name|schema
operator|.
name|setResourceName
argument_list|(
name|managedSchemaResourceName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"After upgrading to managed schema in ZooKeeper, renamed the non-managed schema "
operator|+
name|nonManagedSchemaPath
operator|+
literal|" to "
operator|+
name|upgradedSchemaPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"After upgrading to managed schema in ZooKeeper, the non-managed schema "
operator|+
name|nonManagedSchemaPath
operator|+
literal|" no longer exists."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// Restore the interrupted status
block|}
specifier|final
name|String
name|msg
init|=
literal|"Error persisting managed schema resource "
operator|+
name|managedSchemaResourceName
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// Log as warning and suppress the exception
block|}
block|}
block|}
DECL|field|schemaUpdateLock
specifier|private
name|Object
name|schemaUpdateLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|getSchemaUpdateLock
specifier|public
name|Object
name|getSchemaUpdateLock
parameter_list|()
block|{
return|return
name|schemaUpdateLock
return|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
if|if
condition|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|this
operator|.
name|zkIndexSchemaReader
operator|=
operator|new
name|ZkIndexSchemaReader
argument_list|(
name|this
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
decl_stmt|;
name|zkLoader
operator|.
name|setZkIndexSchemaReader
argument_list|(
name|this
operator|.
name|zkIndexSchemaReader
argument_list|)
expr_stmt|;
try|try
block|{
name|zkIndexSchemaReader
operator|.
name|refreshSchemaFromZk
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// update immediately if newer is available
name|core
operator|.
name|setLatestSchema
argument_list|(
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Error attempting to access "
operator|+
name|zkLoader
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|managedSchemaResourceName
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|zkIndexSchemaReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getSchema
specifier|public
name|ManagedIndexSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
DECL|method|setSchema
specifier|public
name|void
name|setSchema
parameter_list|(
name|ManagedIndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|core
operator|.
name|setLatestSchema
argument_list|(
name|schema
argument_list|)
expr_stmt|;
block|}
DECL|method|isMutable
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
name|isMutable
return|;
block|}
DECL|method|getConfig
specifier|public
name|SolrConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
block|}
end_class

end_unit

