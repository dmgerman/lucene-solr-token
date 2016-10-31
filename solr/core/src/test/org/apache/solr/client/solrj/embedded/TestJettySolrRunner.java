begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
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
package|;
end_package

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
name|request
operator|.
name|CoreAdminRequest
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
name|Properties
import|;
end_import

begin_class
DECL|class|TestJettySolrRunner
specifier|public
class|class
name|TestJettySolrRunner
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testPassSolrHomeToRunner
specifier|public
name|void
name|testPassSolrHomeToRunner
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We set a non-standard coreRootDirectory, create a core, and check that it has been
comment|// built in the correct place
name|Path
name|solrHome
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|coresDir
init|=
name|createTempDir
argument_list|(
literal|"crazy_path_to_cores"
argument_list|)
decl_stmt|;
name|Path
name|configsets
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
decl_stmt|;
name|String
name|solrxml
init|=
literal|"<solr><str name=\"configSetBaseDir\">CONFIGSETS</str><str name=\"coreRootDirectory\">COREROOT</str></solr>"
operator|.
name|replace
argument_list|(
literal|"CONFIGSETS"
argument_list|,
name|configsets
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|replace
argument_list|(
literal|"COREROOT"
argument_list|,
name|coresDir
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|solrHome
operator|.
name|resolve
argument_list|(
literal|"solr.xml"
argument_list|)
argument_list|,
name|solrxml
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|JettyConfig
name|jettyConfig
init|=
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
decl_stmt|;
name|JettySolrRunner
name|runner
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|solrHome
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|,
name|jettyConfig
argument_list|)
decl_stmt|;
try|try
block|{
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|runner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
init|)
block|{
name|CoreAdminRequest
operator|.
name|Create
name|createReq
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|createReq
operator|.
name|setCoreName
argument_list|(
literal|"newcore"
argument_list|)
expr_stmt|;
name|createReq
operator|.
name|setConfigSet
argument_list|(
literal|"minimal"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|createReq
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|coresDir
operator|.
name|resolve
argument_list|(
literal|"newcore"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"core.properties"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|runner
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

