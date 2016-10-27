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
name|File
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCorePropertiesReload
specifier|public
class|class
name|TestCorePropertiesReload
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrHomeDirectory
specifier|private
specifier|final
name|File
name|solrHomeDirectory
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
DECL|method|setMeUp
specifier|public
name|void
name|setMeUp
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"Before reload"
argument_list|)
expr_stmt|;
name|writeProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPropertiesReload
specifier|public
name|void
name|testPropertiesReload
parameter_list|()
throws|throws
name|Exception
block|{
name|setMeUp
argument_list|()
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|CoreDescriptor
name|coreDescriptor
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|String
name|testProp
init|=
name|coreDescriptor
operator|.
name|getCoreProperty
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testProp
operator|.
name|equals
argument_list|(
literal|"Before reload"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Re-write the properties file
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"After reload"
argument_list|)
expr_stmt|;
name|writeProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
name|core
operator|=
name|h
operator|.
name|getCore
argument_list|()
expr_stmt|;
name|coreDescriptor
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
expr_stmt|;
name|testProp
operator|=
name|coreDescriptor
operator|.
name|getCoreProperty
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testProp
operator|.
name|equals
argument_list|(
literal|"After reload"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeProperties
specifier|private
name|void
name|writeProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|Exception
block|{
name|Writer
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"collection1"
argument_list|)
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|out
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrcore.properties"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|store
argument_list|(
name|out
argument_list|,
literal|"Reload Test"
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
block|}
end_class

end_unit

