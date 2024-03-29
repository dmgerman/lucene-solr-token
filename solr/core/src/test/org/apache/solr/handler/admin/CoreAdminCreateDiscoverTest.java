begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|IOException
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
name|util
operator|.
name|Properties
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
name|FileUtils
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
name|CoreAdminParams
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
name|response
operator|.
name|SolrQueryResponse
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

begin_class
DECL|class|CoreAdminCreateDiscoverTest
specifier|public
class|class
name|CoreAdminCreateDiscoverTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrHomeDirectory
specifier|private
specifier|static
name|File
name|solrHomeDirectory
init|=
literal|null
decl_stmt|;
DECL|field|admin
specifier|private
specifier|static
name|CoreAdminHandler
name|admin
init|=
literal|null
decl_stmt|;
DECL|field|coreNormal
specifier|private
specifier|static
name|String
name|coreNormal
init|=
literal|"normal"
decl_stmt|;
DECL|field|coreSysProps
specifier|private
specifier|static
name|String
name|coreSysProps
init|=
literal|"sys_props"
decl_stmt|;
DECL|field|coreDuplicate
specifier|private
specifier|static
name|String
name|coreDuplicate
init|=
literal|"duplicate"
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
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// I require FS-based indexes for this test.
name|solrHomeDirectory
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|setupNoCoreTest
argument_list|(
name|solrHomeDirectory
operator|.
name|toPath
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|admin
operator|=
operator|new
name|CoreAdminHandler
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|admin
operator|=
literal|null
expr_stmt|;
comment|// Release it or the test harness complains.
name|solrHomeDirectory
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setupCore
specifier|private
specifier|static
name|void
name|setupCore
parameter_list|(
name|String
name|coreName
parameter_list|,
name|boolean
name|blivet
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|instDir
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|coreName
argument_list|)
decl_stmt|;
name|File
name|subHome
init|=
operator|new
name|File
argument_list|(
name|instDir
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to make subdirectory "
argument_list|,
name|subHome
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Be sure we pick up sysvars when we create this
name|String
name|srcDir
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|srcDir
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"schema_ren.xml"
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
name|srcDir
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"solrconfig_ren.xml"
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
name|srcDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateSavesSysProps
specifier|public
name|void
name|testCreateSavesSysProps
parameter_list|()
throws|throws
name|Exception
block|{
name|setupCore
argument_list|(
name|coreSysProps
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create a new core (using CoreAdminHandler) w/ properties
comment|// Just to be sure it's NOT written to the core.properties file
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|coreSysProps
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"INSTDIR_TEST"
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"CONFIG_TEST"
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"SCHEMA_TEST"
argument_list|,
literal|"schema_ren.xml"
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"data_diff"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"DATA_TEST"
argument_list|,
literal|"data_diff"
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|coreSysProps
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
literal|"${INSTDIR_TEST}"
argument_list|,
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
literal|"${CONFIG_TEST}"
argument_list|,
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
literal|"${SCHEMA_TEST}"
argument_list|,
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
literal|"${DATA_TEST}"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception on create"
argument_list|,
name|resp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify props are in persisted file
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|coreSysProps
operator|+
literal|"/"
operator|+
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
argument_list|,
name|coreSysProps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|)
argument_list|,
literal|"${CONFIG_TEST}"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|)
argument_list|,
literal|"${SCHEMA_TEST}"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|)
argument_list|,
literal|"${DATA_TEST}"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|props
operator|.
name|size
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|//checkOnlyKnown(propFile);
comment|// Now assert that certain values are properly dereferenced in the process of creating the core, see
comment|// SOLR-4982. Really, we should be able to just verify that the index files exist.
comment|// Should NOT be a datadir named ${DATA_TEST} (literal).
name|File
name|badDir
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"${DATA_TEST}"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Should have substituted the sys var, found file "
operator|+
name|badDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|badDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// For the other 3 vars, we couldn't get past creating the core if dereferencing didn't work correctly.
comment|// Should have segments in the directory pointed to by the ${DATA_TEST}.
name|File
name|test
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have found index dir at "
operator|+
name|test
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|test
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCannotCreateTwoCoresWithSameInstanceDir
specifier|public
name|void
name|testCannotCreateTwoCoresWithSameInstanceDir
parameter_list|()
throws|throws
name|Exception
block|{
name|setupCore
argument_list|(
name|coreDuplicate
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|coreDuplicate
argument_list|)
decl_stmt|;
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
comment|// Create one core
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|coreDuplicate
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
literal|"schema_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception on create"
argument_list|,
name|resp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
comment|// Try to create another core with a different name, but the same instance dir
name|SolrQueryResponse
name|resp2
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
try|try
block|{
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
literal|"different_name_core"
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
literal|"schema_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|resp2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating two cores with a shared instance dir should throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"already defined there"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInstanceDirAsPropertyParam
specifier|public
name|void
name|testInstanceDirAsPropertyParam
parameter_list|()
throws|throws
name|Exception
block|{
name|setupCore
argument_list|(
literal|"testInstanceDirAsPropertyParam-XYZ"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// make sure workDir is different even if core name is used as instanceDir
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"testInstanceDirAsPropertyParam-XYZ"
argument_list|)
decl_stmt|;
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
comment|// Create one core
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
literal|"testInstanceDirAsPropertyParam"
argument_list|,
literal|"property.instanceDir"
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
literal|"schema_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception on create"
argument_list|,
name|resp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|STATUS
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
literal|"testInstanceDirAsPropertyParam"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|NamedList
name|status
init|=
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|NamedList
name|coreProps
init|=
operator|(
name|NamedList
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"testInstanceDirAsPropertyParam"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|String
name|instanceDir
init|=
operator|(
name|String
operator|)
name|coreProps
operator|.
name|get
argument_list|(
literal|"instanceDir"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Instance dir does not match param given in property.instanceDir syntax"
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateSavesRegProps
specifier|public
name|void
name|testCreateSavesRegProps
parameter_list|()
throws|throws
name|Exception
block|{
name|setupCore
argument_list|(
name|coreNormal
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create a new core (using CoreAdminHandler) w/ properties
comment|// Just to be sure it's NOT written to the core.properties file
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|coreNormal
argument_list|)
decl_stmt|;
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|coreNormal
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
literal|"schema_ren.xml"
argument_list|,
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception on create"
argument_list|,
name|resp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify props are in persisted file
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
name|coreNormal
operator|+
literal|"/"
operator|+
name|CorePropertiesLocator
operator|.
name|PROPERTIES_FILENAME
argument_list|)
decl_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
argument_list|,
name|coreNormal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|)
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|)
argument_list|,
literal|"schema_ren.xml"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected value preserved in properties file "
operator|+
name|propFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|)
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|props
operator|.
name|size
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|//checkOnlyKnown(propFile);
comment|// For the other 3 vars, we couldn't get past creating the core if dereferencing didn't work correctly.
comment|// Should have segments in the directory pointed to by the ${DATA_TEST}.
name|File
name|test
init|=
operator|new
name|File
argument_list|(
name|data
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have found index dir at "
operator|+
name|test
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|test
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

