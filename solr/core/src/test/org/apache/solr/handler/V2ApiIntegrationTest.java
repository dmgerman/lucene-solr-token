begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
operator|.
name|JettySolrRunner
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
name|CollectionAdminRequest
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
name|SolrCloudTestCase
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
name|Utils
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
name|TestSolrConfigHandler
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
name|RESTfulServerProvider
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
name|RestTestHarness
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
DECL|class|V2ApiIntegrationTest
specifier|public
class|class
name|V2ApiIntegrationTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|restTestHarnesses
specifier|private
name|List
argument_list|<
name|RestTestHarness
argument_list|>
name|restTestHarnesses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|COLL_NAME
specifier|private
specifier|static
name|String
name|COLL_NAME
init|=
literal|"collection1"
decl_stmt|;
DECL|method|setupHarnesses
specifier|private
name|void
name|setupHarnesses
parameter_list|()
block|{
for|for
control|(
specifier|final
name|JettySolrRunner
name|jettySolrRunner
range|:
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|RestTestHarness
name|harness
init|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|ServerProvider
argument_list|(
name|jettySolrRunner
argument_list|)
argument_list|)
decl_stmt|;
name|restTestHarnesses
operator|.
name|add
argument_list|(
name|harness
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ServerProvider
specifier|static
class|class
name|ServerProvider
implements|implements
name|RESTfulServerProvider
block|{
DECL|field|jettySolrRunner
specifier|final
name|JettySolrRunner
name|jettySolrRunner
decl_stmt|;
DECL|field|baseurl
name|String
name|baseurl
decl_stmt|;
DECL|method|ServerProvider
name|ServerProvider
parameter_list|(
name|JettySolrRunner
name|jettySolrRunner
parameter_list|)
block|{
name|this
operator|.
name|jettySolrRunner
operator|=
name|jettySolrRunner
expr_stmt|;
name|baseurl
operator|=
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|COLL_NAME
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBaseURL
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
name|baseurl
return|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|createCluster
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|configureCluster
argument_list|(
literal|2
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf1"
argument_list|,
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"cloud-managed"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|COLL_NAME
argument_list|,
literal|"conf1"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setupHarnesses
argument_list|()
expr_stmt|;
name|testApis
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|RestTestHarness
name|r
range|:
name|restTestHarnesses
control|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testApis
specifier|private
name|void
name|testApis
parameter_list|()
throws|throws
name|Exception
block|{
name|RestTestHarness
name|restHarness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ServerProvider
name|serverProvider
init|=
operator|(
name|ServerProvider
operator|)
name|restHarness
operator|.
name|getServerProvider
argument_list|()
decl_stmt|;
name|serverProvider
operator|.
name|baseurl
operator|=
name|serverProvider
operator|.
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"/v2/c/"
operator|+
name|COLL_NAME
expr_stmt|;
name|Map
name|result
init|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/get/_introspect"
argument_list|,
name|restHarness
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/c/collection1/get"
argument_list|,
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|result
argument_list|,
literal|true
argument_list|,
literal|"/spec[0]/url/paths[0]"
argument_list|)
argument_list|)
expr_stmt|;
name|serverProvider
operator|.
name|baseurl
operator|=
name|serverProvider
operator|.
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"/v2/collections/"
operator|+
name|COLL_NAME
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/get/_introspect"
argument_list|,
name|restHarness
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/collections/collection1/get"
argument_list|,
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|result
argument_list|,
literal|true
argument_list|,
literal|"/spec[0]/url/paths[0]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

