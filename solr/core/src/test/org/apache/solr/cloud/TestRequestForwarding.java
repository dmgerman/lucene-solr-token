begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|CollectionAdminResponse
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
name|ZkStateReader
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
annotation|@
name|SuppressSSL
DECL|class|TestRequestForwarding
specifier|public
class|class
name|TestRequestForwarding
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCluster
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|solrCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|3
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|uploadConfigSet
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1/conf"
argument_list|)
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|solrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiCollectionQuery
specifier|public
name|void
name|testMultiCollectionQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|createCollection
argument_list|(
literal|"collection1"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
comment|// Test against all nodes (two of them host the collection, one of them will
comment|// forward the query)
for|for
control|(
name|JettySolrRunner
name|jettySolrRunner
range|:
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|String
name|queryStrings
index|[]
init|=
block|{
literal|"q=cat%3Afootball%5E2"
block|,
comment|// URL encoded
literal|"q=cat:football^2"
comment|// No URL encoding, contains disallowed character ^
block|}
decl_stmt|;
for|for
control|(
name|String
name|q
range|:
name|queryStrings
control|)
block|{
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|jettySolrRunner
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/collection1/select?"
operator|+
name|q
argument_list|)
decl_stmt|;
name|url
operator|.
name|openStream
argument_list|()
expr_stmt|;
comment|// Shouldn't throw any errors
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Query '"
operator|+
name|q
operator|+
literal|"' failed, "
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|createCollection
specifier|private
name|void
name|createCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|CollectionAdminResponse
name|response
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|name
argument_list|,
name|config
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|create
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|response
operator|=
name|create
operator|.
name|process
argument_list|(
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|!=
literal|0
operator|||
name|response
operator|.
name|getErrorMessages
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not create collection. Response"
operator|+
name|response
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ZkStateReader
name|zkStateReader
init|=
name|solrCluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|name
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

