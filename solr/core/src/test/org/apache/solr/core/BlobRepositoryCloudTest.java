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
name|ByteBuffer
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
name|Path
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
name|Set
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
name|SolrServerException
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
name|impl
operator|.
name|CloudSolrClient
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
name|QueryResponse
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
name|cloud
operator|.
name|ZkTestServer
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
name|SolrDocumentList
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
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|TestBlobHandler
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
name|server
operator|.
name|DataNode
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
name|server
operator|.
name|DataTree
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
name|server
operator|.
name|ZKDatabase
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|BlobRepositoryCloudTest
specifier|public
class|class
name|BlobRepositoryCloudTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|TEST_PATH
specifier|public
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
name|getFile
argument_list|(
literal|"solr/configsets"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|1
argument_list|)
comment|// only sharing *within* a node
operator|.
name|addConfig
argument_list|(
literal|"configname"
argument_list|,
name|TEST_PATH
operator|.
name|resolve
argument_list|(
literal|"resource-sharing"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
comment|//    Thread.sleep(2000);
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|createCollection
argument_list|(
literal|".system"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// test component will fail if it cant' find a blob with this data by this name
name|TestBlobHandler
operator|.
name|postData
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|findLiveNodeURI
argument_list|()
argument_list|,
literal|"testResource"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
literal|"foo,bar\nbaz,bam"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//    Thread.sleep(2000);
comment|// if these don't load we probably failed to post the blob above
name|cluster
operator|.
name|createCollection
argument_list|(
literal|"col1"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"configname"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|createCollection
argument_list|(
literal|"col2"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"configname"
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|//    Thread.sleep(2000);
name|SolrInputDocument
name|document
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"text"
argument_list|,
literal|"col1"
argument_list|)
expr_stmt|;
name|CloudSolrClient
name|solrClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|solrClient
operator|.
name|add
argument_list|(
literal|"col1"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|commit
argument_list|(
literal|"col1"
argument_list|)
expr_stmt|;
name|document
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"text"
argument_list|,
literal|"col2"
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|add
argument_list|(
literal|"col2"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|commit
argument_list|(
literal|"col2"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
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
comment|// This test relies on the installation of ResourceSharingTestComponent which has 2 useful properties:
comment|// 1. it will fail to initialize if it doesn't find a 2 line CSV like foo,bar\nbaz,bam thus validating
comment|//    that we are properly pulling data from the blob store
comment|// 2. It replaces any q for a query request to /select with "text:<name>" where<name> is the name
comment|//    of the last collection to run a query. It does this by caching a shared resource of type
comment|//    ResourceSharingTestComponent.TestObject, and the following sequence is proof that either
comment|//    collection can tell if it was (or was not) the last collection to issue a query by
comment|//    consulting the shared object
name|assertLastQueryNotToCollection
argument_list|(
literal|"col1"
argument_list|)
expr_stmt|;
name|assertLastQueryNotToCollection
argument_list|(
literal|"col2"
argument_list|)
expr_stmt|;
name|assertLastQueryNotToCollection
argument_list|(
literal|"col1"
argument_list|)
expr_stmt|;
name|assertLastQueryToCollection
argument_list|(
literal|"col1"
argument_list|)
expr_stmt|;
name|assertLastQueryNotToCollection
argument_list|(
literal|"col2"
argument_list|)
expr_stmt|;
name|assertLastQueryToCollection
argument_list|(
literal|"col2"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: move this up to parent class?
DECL|method|findLiveNodeURI
specifier|private
specifier|static
name|String
name|findLiveNodeURI
parameter_list|()
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
return|return
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|".system"
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertLastQueryToCollection
specifier|private
name|void
name|assertLastQueryToCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrDocuments
argument_list|(
name|collection
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertLastQueryNotToCollection
specifier|private
name|void
name|assertLastQueryNotToCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getSolrDocuments
argument_list|(
name|collection
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSolrDocuments
specifier|private
name|SolrDocumentList
name|getSolrDocuments
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|QueryResponse
name|resp1
init|=
name|client
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|query
argument_list|)
decl_stmt|;
return|return
name|resp1
operator|.
name|getResults
argument_list|()
return|;
block|}
block|}
end_class

end_unit
