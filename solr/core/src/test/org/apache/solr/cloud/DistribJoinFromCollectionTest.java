begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LuceneTestCase
operator|.
name|Slow
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
name|JSONTestUtil
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
name|impl
operator|.
name|HttpSolrClient
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
name|request
operator|.
name|QueryRequest
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
name|UpdateRequest
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
name|SolrDocument
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
name|ClusterState
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
name|Replica
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
name|Slice
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
name|SolrZkClient
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
name|ZkCoreNodeProps
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
name|common
operator|.
name|params
operator|.
name|CollectionParams
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
name|ModifiableSolrParams
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
name|CoreContainer
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
name|servlet
operator|.
name|SolrDispatchFilter
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
name|Before
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
name|lang
operator|.
name|StringUtils
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
name|IOException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Tests using fromIndex that points to a collection in SolrCloud mode.  */
end_comment

begin_class
DECL|class|DistribJoinFromCollectionTest
specifier|public
class|class
name|DistribJoinFromCollectionTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|DistribJoinFromCollectionTest
specifier|public
name|DistribJoinFromCollectionTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
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
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{}
name|resetExceptionIgnores
argument_list|()
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
comment|// create a collection holding data for the "to" side of the JOIN
name|String
name|toColl
init|=
literal|"to_2x2"
decl_stmt|;
name|createCollection
argument_list|(
name|toColl
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ensureAllReplicasAreActive
argument_list|(
name|toColl
argument_list|,
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|ensureAllReplicasAreActive
argument_list|(
name|toColl
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|30
argument_list|)
expr_stmt|;
comment|// get the set of nodes where replicas for the "to" collection exist
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ClusterState
name|cs
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|cs
operator|.
name|getActiveSlices
argument_list|(
name|toColl
argument_list|)
control|)
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
name|nodeSet
operator|.
name|add
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeSet
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// deploy the "from" collection to all nodes where the "to" collection exists
name|String
name|fromColl
init|=
literal|"from_1x2"
decl_stmt|;
name|createCollection
argument_list|(
literal|null
argument_list|,
name|fromColl
argument_list|,
literal|1
argument_list|,
name|nodeSet
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|nodeSet
argument_list|,
literal|","
argument_list|)
argument_list|)
expr_stmt|;
name|ensureAllReplicasAreActive
argument_list|(
name|fromColl
argument_list|,
literal|"shard1"
argument_list|,
literal|1
argument_list|,
name|nodeSet
operator|.
name|size
argument_list|()
argument_list|,
literal|30
argument_list|)
expr_stmt|;
comment|// both to and from collections are up and active, index some docs ...
name|Integer
name|toDocId
init|=
name|indexDoc
argument_list|(
name|toColl
argument_list|,
literal|1001
argument_list|,
literal|"a"
argument_list|,
literal|null
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|indexDoc
argument_list|(
name|fromColl
argument_list|,
literal|2001
argument_list|,
literal|"a"
argument_list|,
literal|"c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// so the commits fire
comment|// verify the join with fromIndex works
name|String
name|joinQ
init|=
literal|"{!join from=join_s fromIndex="
operator|+
name|fromColl
operator|+
literal|" to=join_s}match_s:c"
decl_stmt|;
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|(
name|cloudClient
operator|.
name|request
argument_list|(
name|qr
argument_list|)
argument_list|,
name|cloudClient
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|hits
init|=
name|rsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 1 doc"
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|toDocId
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"get_s"
argument_list|)
argument_list|)
expr_stmt|;
comment|// create an alias for the fromIndex and then query through the alias
name|String
name|alias
init|=
name|fromColl
operator|+
literal|"Alias"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|CreateAlias
name|request
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|CreateAlias
argument_list|()
decl_stmt|;
name|request
operator|.
name|setAliasName
argument_list|(
name|alias
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAliasedCollections
argument_list|(
name|fromColl
argument_list|)
expr_stmt|;
name|request
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|joinQ
operator|=
literal|"{!join from=join_s fromIndex="
operator|+
name|alias
operator|+
literal|" to=join_s}match_s:c"
expr_stmt|;
name|qr
operator|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s"
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|QueryResponse
argument_list|(
name|cloudClient
operator|.
name|request
argument_list|(
name|qr
argument_list|)
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
name|hits
operator|=
name|rsp
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 1 doc"
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|=
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|toDocId
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"get_s"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify join doesn't work if no match in the "from" index
name|joinQ
operator|=
literal|"{!join from=join_s fromIndex="
operator|+
name|fromColl
operator|+
literal|" to=join_s}match_s:d"
expr_stmt|;
name|qr
operator|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s"
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|QueryResponse
argument_list|(
name|cloudClient
operator|.
name|request
argument_list|(
name|qr
argument_list|)
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
name|hits
operator|=
name|rsp
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected no hits"
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"DistribJoinFromCollectionTest logic complete ... deleting the "
operator|+
name|toColl
operator|+
literal|" and "
operator|+
name|fromColl
operator|+
literal|" collections"
argument_list|)
expr_stmt|;
comment|// try to clean up
for|for
control|(
name|String
name|c
range|:
operator|new
name|String
index|[]
block|{
name|toColl
block|,
name|fromColl
block|}
control|)
block|{
try|try
block|{
name|CollectionAdminRequest
operator|.
name|Delete
name|req
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCollectionName
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed due to: "
operator|+
name|e
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"DistribJoinFromCollectionTest succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
DECL|method|indexDoc
specifier|protected
name|Integer
name|indexDoc
parameter_list|(
name|String
name|collection
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|joinField
parameter_list|,
name|String
name|matchField
parameter_list|,
name|String
name|getField
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|setCommitWithin
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|up
operator|.
name|setParam
argument_list|(
literal|"collection"
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|Integer
name|docId
init|=
operator|new
name|Integer
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"join_s"
argument_list|,
name|joinField
argument_list|)
expr_stmt|;
if|if
condition|(
name|matchField
operator|!=
literal|null
condition|)
name|doc
operator|.
name|addField
argument_list|(
literal|"match_s"
argument_list|,
name|matchField
argument_list|)
expr_stmt|;
if|if
condition|(
name|getField
operator|!=
literal|null
condition|)
name|doc
operator|.
name|addField
argument_list|(
literal|"get_s"
argument_list|,
name|getField
argument_list|)
expr_stmt|;
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|up
argument_list|)
expr_stmt|;
return|return
name|docId
return|;
block|}
block|}
end_class

end_unit

