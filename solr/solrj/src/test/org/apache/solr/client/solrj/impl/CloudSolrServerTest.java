begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
name|impl
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Iterator
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|AbstractUpdateRequest
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|AbstractZkTestCase
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
name|util
operator|.
name|ExternalPaths
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
name|AfterClass
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * This test would be faster if we simulated the zk state instead.  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|CloudSolrServerTest
specifier|public
class|class
name|CloudSolrServerTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|SOLR_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
name|ExternalPaths
operator|.
name|SOURCE_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"src"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-files"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{        }
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
DECL|method|SOLR_HOME
specifier|public
specifier|static
name|String
name|SOLR_HOME
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
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
comment|// we expect this time of exception as shards go up and down...
comment|//ignoreException(".*");
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|CloudSolrServerTest
specifier|public
name|CloudSolrServerTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|4
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|SolrInputDocument
name|doc1
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
name|id
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello1"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello2"
argument_list|)
expr_stmt|;
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAction
argument_list|(
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Test single threaded routed updates for UpdateRequest
name|NamedList
name|response
init|=
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|CloudSolrServer
operator|.
name|RouteResponse
name|rr
init|=
operator|(
name|CloudSolrServer
operator|.
name|RouteResponse
operator|)
name|response
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|routes
init|=
name|rr
operator|.
name|getRoutes
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
argument_list|>
name|it
init|=
name|routes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|QueryRequest
name|queryRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|solrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|queryRequest
operator|.
name|process
argument_list|(
name|solrServer
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docList
init|=
name|queryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docList
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Test the deleteById routing for UpdateRequest
name|UpdateRequest
name|delRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|delRequest
operator|.
name|deleteById
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|delRequest
operator|.
name|deleteById
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|delRequest
operator|.
name|setAction
argument_list|(
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|delRequest
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|qParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|qParams
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryRequest
name|qRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|qParams
argument_list|)
decl_stmt|;
name|QueryResponse
name|qResponse
init|=
name|qRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
name|qResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docs
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// Test Multi-Threaded routed updates for UpdateRequest
name|CloudSolrServer
name|threadedClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|threadedClient
operator|=
operator|new
name|CloudSolrServer
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|threadedClient
operator|.
name|setParallelUpdates
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threadedClient
operator|.
name|setDefaultCollection
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|response
operator|=
name|threadedClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|rr
operator|=
operator|(
name|CloudSolrServer
operator|.
name|RouteResponse
operator|)
name|response
expr_stmt|;
name|routes
operator|=
name|rr
operator|.
name|getRoutes
argument_list|()
expr_stmt|;
name|it
operator|=
name|routes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|QueryRequest
name|queryRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|solrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|queryRequest
operator|.
name|process
argument_list|(
name|solrServer
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docList
init|=
name|queryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docList
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|threadedClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|getDoc
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

