begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FileUtils
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
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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

begin_class
DECL|class|TestSolrDiscoveryProperties
specifier|public
class|class
name|TestSolrDiscoveryProperties
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|NEW_LINE
specifier|private
specifier|static
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|()
expr_stmt|;
block|}
DECL|field|solrHomeDirectory
specifier|private
specifier|final
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"org.apache.solr.core.TestSolrDiscoveryProperties"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrHome"
argument_list|)
decl_stmt|;
DECL|method|setMeUp
specifier|private
name|void
name|setMeUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addSolrPropertiesFile
specifier|private
name|void
name|addSolrPropertiesFile
parameter_list|(
name|String
modifier|...
name|extras
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|solrProps
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|SolrProperties
operator|.
name|SOLR_PROPERTIES_FILE
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|load
argument_list|(
operator|new
name|StringReader
argument_list|(
name|SOLR_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|extra
range|:
name|extras
control|)
block|{
name|String
index|[]
name|parts
init|=
name|extra
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|solrProps
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|.
name|store
argument_list|(
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addSolrXml
specifier|private
name|void
name|addSolrXml
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|SolrProperties
operator|.
name|SOLR_XML_FILE
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|tmpFile
argument_list|,
name|SOLR_XML
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeCorePropFile
specifier|private
name|Properties
name|makeCorePropFile
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isLazy
parameter_list|,
name|boolean
name|loadOnStartup
parameter_list|,
name|String
modifier|...
name|extraProps
parameter_list|)
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_SCHEMA
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_TRANSIENT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|isLazy
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_LOADONSTARTUP
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|loadOnStartup
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|,
literal|"${core.dataDir:stuffandnonsense}"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|extra
range|:
name|extraProps
control|)
block|{
name|String
index|[]
name|parts
init|=
name|extra
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
DECL|method|addCoreWithProps
specifier|private
name|void
name|addCoreWithProps
parameter_list|(
name|Properties
name|stockProps
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|stockProps
operator|.
name|getProperty
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|SolrProperties
operator|.
name|CORE_PROP_FILE
argument_list|)
decl_stmt|;
name|File
name|parent
init|=
name|propFile
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to mkdirs for "
operator|+
name|parent
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|parent
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|propFile
argument_list|)
decl_stmt|;
try|try
block|{
name|stockProps
operator|.
name|store
argument_list|(
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|addConfFiles
argument_list|(
operator|new
name|File
argument_list|(
name|parent
argument_list|,
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addConfFiles
specifier|private
name|void
name|addConfFiles
parameter_list|(
name|File
name|confDir
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|top
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to mkdirs for "
operator|+
name|confDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|confDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addConfigsForBackCompat
specifier|private
name|void
name|addConfigsForBackCompat
parameter_list|()
throws|throws
name|Exception
block|{
name|addConfFiles
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|CoreContainer
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|CoreContainer
operator|.
name|Initializer
name|init
init|=
operator|new
name|CoreContainer
operator|.
name|Initializer
argument_list|()
decl_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|init
operator|.
name|initialize
argument_list|()
decl_stmt|;
name|cores
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|cores
return|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Test the basic setup, create some dirs with core.properties files in them, but no solr.xml (a solr.properties
comment|// instead) and insure that we find all the cores and can load them.
annotation|@
name|Test
DECL|method|testPropertiesFile
specifier|public
name|void
name|testPropertiesFile
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
name|addSolrPropertiesFile
argument_list|()
expr_stmt|;
comment|// name, isLazy, loadOnStartup
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// I suspect what we're adding in here is a "configset" rather than a schema or solrconfig.
comment|//
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy1"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
try|try
block|{
name|Properties
name|props
init|=
name|cc
operator|.
name|containerProperties
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/admin/cores/props"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.adminPath"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/admin/cores/props"
argument_list|,
name|cc
operator|.
name|getAdminPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"defcore"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.defaultCoreName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"defcore"
argument_list|,
name|cc
operator|.
name|getDefaultCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"222.333.444.555"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"6000"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"port"
argument_list|)
argument_list|)
expr_stmt|;
comment|// getProperty actually looks at original props.
name|assertEquals
argument_list|(
literal|"/solrprop"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.hostContext"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"20"
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.zkClientTimeout"
argument_list|)
argument_list|)
expr_stmt|;
name|TestLazyCores
operator|.
name|checkInCores
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|)
expr_stmt|;
name|TestLazyCores
operator|.
name|checkNotInCores
argument_list|(
name|cc
argument_list|,
literal|"lazy1"
argument_list|,
literal|"core2"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|SolrCore
name|core1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core2
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core2"
argument_list|)
decl_stmt|;
name|SolrCore
name|lazy1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy1"
argument_list|)
decl_stmt|;
name|TestLazyCores
operator|.
name|checkInCores
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|,
literal|"core2"
argument_list|,
literal|"lazy1"
argument_list|)
expr_stmt|;
name|core1
operator|.
name|close
argument_list|()
expr_stmt|;
name|core2
operator|.
name|close
argument_list|()
expr_stmt|;
name|lazy1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Check that the various flavors of persistence work, including saving the state of a core when it's being swapped
comment|// out. Added a test in here to insure that files that have config variables are saved with the config vars not the
comment|// substitutions.
annotation|@
name|Test
DECL|method|testPersistTrue
specifier|public
name|void
name|testPersistTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
name|addSolrPropertiesFile
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.persistent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|Properties
name|special
init|=
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|special
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_INSTDIR
argument_list|,
literal|"${core1inst:anothersillypath}"
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|special
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy1"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy2"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy3"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"core1inst"
argument_list|,
literal|"core1"
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
name|SolrCore
name|coreC1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreC1
argument_list|,
literal|"addedPropC1=addedC1"
argument_list|,
literal|"addedPropC1B=foo"
argument_list|,
literal|"addedPropC1C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreC2
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core2"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreC2
argument_list|,
literal|"addedPropC2=addedC2"
argument_list|,
literal|"addedPropC2B=foo"
argument_list|,
literal|"addedPropC2C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreL1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy1"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreL1
argument_list|,
literal|"addedPropL1=addedL1"
argument_list|,
literal|"addedPropL1B=foo"
argument_list|,
literal|"addedPropL1C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreL2
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy2"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreL2
argument_list|,
literal|"addedPropL2=addedL2"
argument_list|,
literal|"addedPropL2B=foo"
argument_list|,
literal|"addedPropL2C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreL3
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy3"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreL3
argument_list|,
literal|"addedPropL3=addedL3"
argument_list|,
literal|"addedPropL3B=foo"
argument_list|,
literal|"addedPropL3C=bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|cc
operator|.
name|persist
argument_list|()
expr_stmt|;
comment|// Insure that one of the loaded cores was swapped out, with a cache size of 2 lazy1 should be gone.
name|TestLazyCores
operator|.
name|checkInCores
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|,
literal|"core2"
argument_list|,
literal|"lazy2"
argument_list|,
literal|"lazy3"
argument_list|)
expr_stmt|;
name|TestLazyCores
operator|.
name|checkNotInCores
argument_list|(
name|cc
argument_list|,
literal|"lazy1"
argument_list|)
expr_stmt|;
name|checkSolrProperties
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|File
name|xmlFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Solr.xml should NOT exist"
argument_list|,
name|xmlFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Properties
name|orig
init|=
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|orig
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_INSTDIR
argument_list|,
literal|"${core1inst:anothersillypath}"
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|orig
argument_list|,
literal|"addedPropC1=addedC1"
argument_list|,
literal|"addedPropC1B=foo"
argument_list|,
literal|"addedPropC1C=bar"
argument_list|)
expr_stmt|;
name|orig
operator|=
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|orig
argument_list|,
literal|"addedPropC2=addedC2"
argument_list|,
literal|"addedPropC2B=foo"
argument_list|,
literal|"addedPropC2C=bar"
argument_list|)
expr_stmt|;
comment|// This test insures that a core that was swapped out has its properties file persisted. Currently this happens
comment|// as the file is removed from the cache.
name|orig
operator|=
name|makeCorePropFile
argument_list|(
literal|"lazy1"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|orig
argument_list|,
literal|"addedPropL1=addedL1"
argument_list|,
literal|"addedPropL1B=foo"
argument_list|,
literal|"addedPropL1C=bar"
argument_list|)
expr_stmt|;
name|orig
operator|=
name|makeCorePropFile
argument_list|(
literal|"lazy2"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|orig
argument_list|,
literal|"addedPropL2=addedL2"
argument_list|,
literal|"addedPropL2B=foo"
argument_list|,
literal|"addedPropL2C=bar"
argument_list|)
expr_stmt|;
name|orig
operator|=
name|makeCorePropFile
argument_list|(
literal|"lazy3"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|orig
argument_list|,
literal|"addedPropL3=addedL3"
argument_list|,
literal|"addedPropL3B=foo"
argument_list|,
literal|"addedPropL3C=bar"
argument_list|)
expr_stmt|;
name|coreC1
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreC2
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreL1
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreL2
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreL3
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Make sure that, even if we do call persist, nothing's saved unless the flag is set in solr.properties.
annotation|@
name|Test
DECL|method|testPersistFalse
specifier|public
name|void
name|testPersistFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
name|addSolrPropertiesFile
argument_list|()
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy1"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreWithProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy2"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
name|SolrCore
name|coreC1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreC1
argument_list|,
literal|"addedPropC1=addedC1"
argument_list|,
literal|"addedPropC1B=foo"
argument_list|,
literal|"addedPropC1C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreC2
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"core2"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreC2
argument_list|,
literal|"addedPropC2=addedC2"
argument_list|,
literal|"addedPropC2B=foo"
argument_list|,
literal|"addedPropC2C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreL1
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy1"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreL1
argument_list|,
literal|"addedPropL1=addedL1"
argument_list|,
literal|"addedPropL1B=foo"
argument_list|,
literal|"addedPropL1C=bar"
argument_list|)
expr_stmt|;
name|SolrCore
name|coreL2
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"lazy2"
argument_list|)
decl_stmt|;
name|addCoreProps
argument_list|(
name|coreL2
argument_list|,
literal|"addedPropL2=addedL2"
argument_list|,
literal|"addedPropL2B=foo"
argument_list|,
literal|"addedPropL2C=bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|cc
operator|.
name|persist
argument_list|()
expr_stmt|;
name|checkSolrProperties
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"core2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy1"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkCoreProps
argument_list|(
name|makeCorePropFile
argument_list|(
literal|"lazy2"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|coreC1
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreC2
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreL1
operator|.
name|close
argument_list|()
expr_stmt|;
name|coreL2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addCoreProps
name|void
name|addCoreProps
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
modifier|...
name|propPairs
parameter_list|)
block|{
for|for
control|(
name|String
name|keyval
range|:
name|propPairs
control|)
block|{
name|String
index|[]
name|pair
init|=
name|keyval
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|putProperty
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|,
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Insure that the solr.properties is as it should be after persisting _and_, in some cases, different than
comment|// what's in memory
DECL|method|checkSolrProperties
name|void
name|checkSolrProperties
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|String
modifier|...
name|checkMemPairs
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|orig
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|orig
operator|.
name|load
argument_list|(
operator|new
name|StringReader
argument_list|(
name|SOLR_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|Properties
name|curr
init|=
name|cc
operator|.
name|getContainerProperties
argument_list|()
decl_stmt|;
name|Properties
name|persisted
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|SolrProperties
operator|.
name|SOLR_PROPERTIES_FILE
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|persisted
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Persisted and original should be the same size"
argument_list|,
name|orig
operator|.
name|size
argument_list|()
argument_list|,
name|persisted
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|orig
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"Values of original should match current"
argument_list|,
name|orig
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
argument_list|,
name|persisted
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Properties
name|specialProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|special
range|:
name|checkMemPairs
control|)
block|{
name|String
index|[]
name|pair
init|=
name|special
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|specialProps
operator|.
name|put
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|,
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
comment|// OK, current should match original except if the property is "special"
for|for
control|(
name|String
name|prop
range|:
name|curr
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|String
name|val
init|=
name|specialProps
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
comment|// Compare curr and val
name|assertEquals
argument_list|(
literal|"Modified property should be in current container properties"
argument_list|,
name|val
argument_list|,
name|curr
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Insure that the properties in the core passed in are exactly what's in the default core.properties below plus
comment|// whatever extra is passed in.
DECL|method|checkCoreProps
name|void
name|checkCoreProps
parameter_list|(
name|Properties
name|orig
parameter_list|,
name|String
modifier|...
name|extraProps
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Read the persisted file.
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|File
name|propParent
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|orig
operator|.
name|getProperty
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|propParent
argument_list|,
name|SolrProperties
operator|.
name|CORE_PROP_FILE
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|propSet
init|=
name|props
operator|.
name|stringPropertyNames
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Persisted properties should NOT contain extra properties"
argument_list|,
name|propSet
operator|.
name|size
argument_list|()
argument_list|,
name|orig
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|orig
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"Original and new properties should be equal for "
operator|+
name|prop
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
argument_list|,
name|orig
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|prop
range|:
name|extraProps
control|)
block|{
name|String
index|[]
name|pair
init|=
name|prop
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Modified parameters should not be present for "
operator|+
name|prop
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If there's a solr.xml AND a properties file, make sure that the xml file is loaded and the properties file
comment|// is ignored.
annotation|@
name|Test
DECL|method|testBackCompatXml
specifier|public
name|void
name|testBackCompatXml
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
name|addSolrPropertiesFile
argument_list|()
expr_stmt|;
name|addSolrXml
argument_list|()
expr_stmt|;
name|addConfigsForBackCompat
argument_list|()
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|init
argument_list|()
decl_stmt|;
try|try
block|{
name|Properties
name|props
init|=
name|cc
operator|.
name|getContainerProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/admin/cores"
argument_list|,
name|cc
operator|.
name|getAdminPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"collectionLazy2"
argument_list|,
name|cc
operator|.
name|getDefaultCoreName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Shouldn't get these in properties at this point
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.adminPath"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.defaultCoreName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"host"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"port"
argument_list|)
argument_list|)
expr_stmt|;
comment|// getProperty actually looks at original props.
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.hostContext"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"cores.zkClientTimeout"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|// For this test I want some of these to be different than what would be in solr.xml by default.
DECL|field|SOLR_PROPERTIES
specifier|private
specifier|final
specifier|static
name|String
name|SOLR_PROPERTIES
init|=
literal|"persistent=${persistent:false}"
operator|+
name|NEW_LINE
operator|+
literal|"cores.adminPath=/admin/cores/props"
operator|+
name|NEW_LINE
operator|+
literal|"cores.defaultCoreName=defcore"
operator|+
name|NEW_LINE
operator|+
literal|"host=222.333.444.555"
operator|+
name|NEW_LINE
operator|+
literal|"port=6000"
operator|+
name|NEW_LINE
operator|+
literal|"cores.hostContext=/solrprop"
operator|+
name|NEW_LINE
operator|+
literal|"cores.zkClientTimeout=20"
operator|+
name|NEW_LINE
operator|+
literal|"cores.transientCacheSize=2"
decl_stmt|;
comment|// For testing whether finding a solr.xml overrides looking at solr.properties
DECL|field|SOLR_XML
specifier|private
specifier|final
specifier|static
name|String
name|SOLR_XML
init|=
literal|"<solr persistent=\"false\"> "
operator|+
literal|"<cores adminPath=\"/admin/cores\" defaultCoreName=\"collectionLazy2\" transientCacheSize=\"4\">  "
operator|+
literal|"<core name=\"collection1\" instanceDir=\"collection1\" config=\"solrconfig-minimal.xml\" schema=\"schema-tiny.xml\" /> "
operator|+
literal|"</cores> "
operator|+
literal|"</solr>"
decl_stmt|;
block|}
end_class

end_unit

