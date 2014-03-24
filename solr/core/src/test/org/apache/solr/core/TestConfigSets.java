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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
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
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|nullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|matchers
operator|.
name|StringContains
operator|.
name|containsString
import|;
end_import

begin_class
DECL|class|TestConfigSets
specifier|public
class|class
name|TestConfigSets
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Rule
DECL|field|testRule
specifier|public
name|TestRule
name|testRule
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|solrxml
specifier|public
specifier|static
name|String
name|solrxml
init|=
literal|"<solr><str name=\"configSetBaseDir\">${configsets:configsets}</str></solr>"
decl_stmt|;
DECL|method|setupContainer
specifier|public
name|CoreContainer
name|setupContainer
parameter_list|(
name|String
name|testName
parameter_list|,
name|String
name|configSetsBaseDir
parameter_list|)
block|{
name|File
name|testDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|testName
argument_list|)
decl_stmt|;
name|testDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"configsets"
argument_list|,
name|configSetsBaseDir
argument_list|)
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|testDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|CoreContainer
name|container
init|=
operator|new
name|CoreContainer
argument_list|(
name|loader
argument_list|,
name|ConfigSolr
operator|.
name|fromString
argument_list|(
name|loader
argument_list|,
name|solrxml
argument_list|)
argument_list|)
decl_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|container
return|;
block|}
annotation|@
name|Test
DECL|method|testConfigSetServiceFindsConfigSets
specifier|public
name|void
name|testConfigSetServiceFindsConfigSets
parameter_list|()
block|{
name|CoreContainer
name|container
init|=
literal|null
decl_stmt|;
try|try
block|{
name|container
operator|=
name|setupContainer
argument_list|(
literal|"findsConfigSets"
argument_list|,
name|getFile
argument_list|(
literal|"solr/configsets"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|testDirectory
init|=
name|container
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
decl_stmt|;
name|SolrCore
name|core1
init|=
name|container
operator|.
name|create
argument_list|(
literal|"core1"
argument_list|,
name|testDirectory
operator|+
literal|"/core1"
argument_list|,
literal|"configSet"
argument_list|,
literal|"configset-2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|core1
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"core1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|core1
operator|.
name|getDataDir
argument_list|()
argument_list|,
name|is
argument_list|(
name|testDirectory
operator|+
literal|"/core1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data"
operator|+
name|File
operator|.
name|separator
argument_list|)
argument_list|)
expr_stmt|;
name|core1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
name|container
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNonExistentConfigSetThrowsException
specifier|public
name|void
name|testNonExistentConfigSetThrowsException
parameter_list|()
block|{
name|CoreContainer
name|container
init|=
literal|null
decl_stmt|;
try|try
block|{
name|container
operator|=
name|setupContainer
argument_list|(
literal|"badConfigSet"
argument_list|,
name|getFile
argument_list|(
literal|"solr/configsets"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|testDirectory
init|=
name|container
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
decl_stmt|;
name|container
operator|.
name|create
argument_list|(
literal|"core1"
argument_list|,
name|testDirectory
operator|+
literal|"/core1"
argument_list|,
literal|"configSet"
argument_list|,
literal|"nonexistent"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected core creation to fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwable
name|wrappedException
init|=
name|getWrappedException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|wrappedException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"nonexistent"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
name|container
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testConfigSetOnCoreReload
specifier|public
name|void
name|testConfigSetOnCoreReload
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"core-reload"
argument_list|)
decl_stmt|;
name|testDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|configSetsDir
init|=
operator|new
name|File
argument_list|(
name|testDirectory
argument_list|,
literal|"configsets"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|getFile
argument_list|(
literal|"solr/configsets"
argument_list|)
argument_list|,
name|configSetsDir
argument_list|)
expr_stmt|;
name|String
name|csd
init|=
name|configSetsDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"configsets"
argument_list|,
name|csd
argument_list|)
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|testDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|CoreContainer
name|container
init|=
operator|new
name|CoreContainer
argument_list|(
name|loader
argument_list|,
name|ConfigSolr
operator|.
name|fromString
argument_list|(
name|loader
argument_list|,
name|solrxml
argument_list|)
argument_list|)
decl_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
comment|// We initially don't have a /get handler defined
name|SolrCore
name|core
init|=
name|container
operator|.
name|create
argument_list|(
literal|"core1"
argument_list|,
name|testDirectory
operator|+
literal|"/core"
argument_list|,
literal|"configSet"
argument_list|,
literal|"configset-2"
argument_list|)
decl_stmt|;
name|container
operator|.
name|register
argument_list|(
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"No /get handler should be defined in the initial configuration"
argument_list|,
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/get"
argument_list|)
argument_list|,
name|is
argument_list|(
name|nullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now copy in a config with a /get handler and reload
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-withgethandler.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|configSetsDir
argument_list|,
literal|"configset-2/conf"
argument_list|)
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|reload
argument_list|(
literal|"core1"
argument_list|)
expr_stmt|;
name|core
operator|=
name|container
operator|.
name|getCore
argument_list|(
literal|"core1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"A /get handler should be defined in the reloaded configuration"
argument_list|,
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/get"
argument_list|)
argument_list|,
name|is
argument_list|(
name|notNullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|container
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

