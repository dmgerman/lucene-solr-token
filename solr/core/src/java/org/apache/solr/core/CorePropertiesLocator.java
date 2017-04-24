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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|FileVisitOption
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
name|FileVisitResult
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
name|SimpleFileVisitor
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
name|attribute
operator|.
name|BasicFileAttributes
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|Lists
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
comment|/**  * Persists CoreDescriptors as properties files  */
end_comment

begin_class
DECL|class|CorePropertiesLocator
specifier|public
class|class
name|CorePropertiesLocator
implements|implements
name|CoresLocator
block|{
DECL|field|PROPERTIES_FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTIES_FILENAME
init|=
literal|"core.properties"
decl_stmt|;
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|rootDirectory
specifier|private
specifier|final
name|Path
name|rootDirectory
decl_stmt|;
DECL|method|CorePropertiesLocator
specifier|public
name|CorePropertiesLocator
parameter_list|(
name|Path
name|coreDiscoveryRoot
parameter_list|)
block|{
name|this
operator|.
name|rootDirectory
operator|=
name|coreDiscoveryRoot
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Config-defined core root directory: {}"
argument_list|,
name|this
operator|.
name|rootDirectory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
for|for
control|(
name|CoreDescriptor
name|cd
range|:
name|coreDescriptors
control|)
block|{
name|Path
name|propertiesFile
init|=
name|this
operator|.
name|rootDirectory
operator|.
name|resolve
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|propertiesFile
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
name|BAD_REQUEST
argument_list|,
literal|"Could not create a new core in "
operator|+
name|cd
operator|.
name|getInstanceDir
argument_list|()
operator|+
literal|"as another core is already defined there"
argument_list|)
throw|;
name|writePropertiesFile
argument_list|(
name|cd
argument_list|,
name|propertiesFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO, this isn't atomic!  If we crash in the middle of a rename, we
comment|// could end up with two cores with identical names, in which case one of
comment|// them won't start up.  Are we happy with this?
annotation|@
name|Override
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
for|for
control|(
name|CoreDescriptor
name|cd
range|:
name|coreDescriptors
control|)
block|{
name|Path
name|propFile
init|=
name|this
operator|.
name|rootDirectory
operator|.
name|resolve
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
name|writePropertiesFile
argument_list|(
name|cd
argument_list|,
name|propFile
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writePropertiesFile
specifier|private
name|void
name|writePropertiesFile
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|,
name|Path
name|propfile
parameter_list|)
block|{
name|Properties
name|p
init|=
name|buildCoreProperties
argument_list|(
name|cd
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|propfile
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|Writer
name|os
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|propfile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|p
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"Written by CorePropertiesLocator"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Couldn't persist core properties to {}: {}"
argument_list|,
name|propfile
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
block|{
if|if
condition|(
name|coreDescriptors
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|CoreDescriptor
name|cd
range|:
name|coreDescriptors
control|)
block|{
if|if
condition|(
name|cd
operator|==
literal|null
condition|)
continue|continue;
name|Path
name|propfile
init|=
name|this
operator|.
name|rootDirectory
operator|.
name|resolve
argument_list|(
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|propfile
argument_list|)
expr_stmt|;
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
literal|"Couldn't delete core properties file {}: {}"
argument_list|,
name|propfile
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|rename
specifier|public
name|void
name|rename
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|oldCD
parameter_list|,
name|CoreDescriptor
name|newCD
parameter_list|)
block|{
name|persist
argument_list|(
name|cc
argument_list|,
name|newCD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|swap
specifier|public
name|void
name|swap
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd1
parameter_list|,
name|CoreDescriptor
name|cd2
parameter_list|)
block|{
name|persist
argument_list|(
name|cc
argument_list|,
name|cd1
argument_list|,
name|cd2
argument_list|)
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
specifier|final
name|CoreContainer
name|cc
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Looking for core definitions underneath {}"
argument_list|,
name|rootDirectory
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|cds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|FileVisitOption
argument_list|>
name|options
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|options
operator|.
name|add
argument_list|(
name|FileVisitOption
operator|.
name|FOLLOW_LINKS
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDepth
init|=
literal|256
decl_stmt|;
name|Files
operator|.
name|walkFileTree
argument_list|(
name|this
operator|.
name|rootDirectory
argument_list|,
name|options
argument_list|,
name|maxDepth
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|PROPERTIES_FILENAME
argument_list|)
condition|)
block|{
name|CoreDescriptor
name|cd
init|=
name|buildCoreDescriptor
argument_list|(
name|file
argument_list|,
name|cc
argument_list|)
decl_stmt|;
if|if
condition|(
name|cd
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Found core {} in {}"
argument_list|,
name|cd
operator|.
name|getName
argument_list|()
argument_list|,
name|cd
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
expr_stmt|;
name|cds
operator|.
name|add
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
return|return
name|FileVisitResult
operator|.
name|SKIP_SIBLINGS
return|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFileFailed
parameter_list|(
name|Path
name|file
parameter_list|,
name|IOException
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if we get an error on the root, then fail the whole thing
comment|// otherwise, log a warning and continue to try and load other cores
if|if
condition|(
name|file
operator|.
name|equals
argument_list|(
name|rootDirectory
argument_list|)
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error reading core root directory {}: {}"
argument_list|,
name|file
argument_list|,
name|exc
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
literal|"Error reading core root directory"
argument_list|)
throw|;
block|}
name|logger
operator|.
name|warn
argument_list|(
literal|"Error visiting {}: {}"
argument_list|,
name|file
argument_list|,
name|exc
argument_list|)
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
literal|"Couldn't walk file tree under "
operator|+
name|this
operator|.
name|rootDirectory
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Found {} core definitions underneath {}"
argument_list|,
name|cds
operator|.
name|size
argument_list|()
argument_list|,
name|rootDirectory
argument_list|)
expr_stmt|;
if|if
condition|(
name|cds
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Cores are: {}"
argument_list|,
name|cds
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|CoreDescriptor
operator|::
name|getName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cds
return|;
block|}
DECL|method|buildCoreDescriptor
specifier|protected
name|CoreDescriptor
name|buildCoreDescriptor
parameter_list|(
name|Path
name|propertiesFile
parameter_list|,
name|CoreContainer
name|cc
parameter_list|)
block|{
name|Path
name|instanceDir
init|=
name|propertiesFile
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|Properties
name|coreProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|fis
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|propertiesFile
argument_list|)
init|)
block|{
name|coreProperties
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|createName
argument_list|(
name|coreProperties
argument_list|,
name|instanceDir
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|coreProperties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|propMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CoreDescriptor
name|ret
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|name
argument_list|,
name|instanceDir
argument_list|,
name|propMap
argument_list|,
name|cc
operator|.
name|getContainerProperties
argument_list|()
argument_list|,
name|cc
operator|.
name|isZooKeeperAware
argument_list|()
argument_list|)
decl_stmt|;
name|ret
operator|.
name|loadExtraProperties
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Couldn't load core descriptor from {}:{}"
argument_list|,
name|propertiesFile
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|createName
specifier|protected
specifier|static
name|String
name|createName
parameter_list|(
name|Properties
name|p
parameter_list|,
name|Path
name|instanceDir
parameter_list|)
block|{
return|return
name|p
operator|.
name|getProperty
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
name|instanceDir
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|buildCoreProperties
specifier|protected
name|Properties
name|buildCoreProperties
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|putAll
argument_list|(
name|cd
operator|.
name|getPersistableStandardProperties
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|putAll
argument_list|(
name|cd
operator|.
name|getPersistableUserProperties
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

