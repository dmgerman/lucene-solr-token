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
name|SolrQuery
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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
name|SolrResourceLoader
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
DECL|class|TestEmbeddedSolrServerConstructors
specifier|public
class|class
name|TestEmbeddedSolrServerConstructors
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testPathConstructor
specifier|public
name|void
name|testPathConstructor
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|EmbeddedSolrServer
name|server
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|path
argument_list|,
literal|"collection1"
argument_list|)
init|)
block|{      }
block|}
annotation|@
name|Test
DECL|method|testNodeConfigConstructor
specifier|public
name|void
name|testNodeConfigConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|NodeConfig
name|config
init|=
operator|new
name|NodeConfig
operator|.
name|NodeConfigBuilder
argument_list|(
literal|"testnode"
argument_list|,
name|loader
argument_list|)
operator|.
name|setConfigSetBaseDirectory
argument_list|(
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
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
init|(
name|EmbeddedSolrServer
name|server
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|config
argument_list|,
literal|"newcore"
argument_list|)
init|)
block|{
name|CoreAdminRequest
operator|.
name|Create
name|createRequest
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|createRequest
operator|.
name|setCoreName
argument_list|(
literal|"newcore"
argument_list|)
expr_stmt|;
name|createRequest
operator|.
name|setConfigSet
argument_list|(
literal|"minimal"
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createRequest
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"articleid"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
literal|"newcore"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|server
operator|.
name|query
argument_list|(
literal|"newcore"
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

