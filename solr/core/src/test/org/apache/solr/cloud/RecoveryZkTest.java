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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|Slow
DECL|class|RecoveryZkTest
specifier|public
class|class
name|RecoveryZkTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
comment|//private static final String DISTRIB_UPDATE_CHAIN = "distrib-update-chain";
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RecoveryZkTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|indexThread
specifier|private
name|StopableIndexingThread
name|indexThread
decl_stmt|;
DECL|field|indexThread2
specifier|private
name|StopableIndexingThread
name|indexThread2
decl_stmt|;
DECL|method|RecoveryZkTest
specifier|public
name|RecoveryZkTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|shardCount
operator|=
literal|2
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
comment|// start a couple indexing threads
name|indexThread
operator|=
operator|new
name|StopableIndexingThread
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|indexThread2
operator|=
operator|new
name|StopableIndexingThread
argument_list|(
literal|10000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexThread2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// give some time to index...
name|Thread
operator|.
name|sleep
argument_list|(
name|atLeast
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
comment|// bring shard replica down
name|JettySolrRunner
name|replica
init|=
name|chaosMonkey
operator|.
name|stopShard
argument_list|(
literal|"shard1"
argument_list|,
literal|1
argument_list|)
operator|.
name|jetty
decl_stmt|;
comment|// wait a moment - lets allow some docs to be indexed so replication time is non 0
name|Thread
operator|.
name|sleep
argument_list|(
name|atLeast
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
comment|// bring shard replica up
name|replica
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// make sure replication can start
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// stop indexing threads
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// test that leader and replica have same doc count
name|checkShardConsistency
argument_list|(
literal|"shard1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|long
name|client1Docs
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|long
name|client2Docs
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|client1Docs
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|client1Docs
argument_list|,
name|client2Docs
argument_list|)
expr_stmt|;
comment|// won't always pass yet...
comment|//query("q", "*:*", "sort", "id desc");
block|}
DECL|method|indexDoc
specifier|protected
name|void
name|indexDoc
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|controlClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// UpdateRequest ureq = new UpdateRequest();
comment|// ureq.add(doc);
comment|// ureq.setParam("update.chain", DISTRIB_UPDATE_CHAIN);
comment|// ureq.process(cloudClient);
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
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
comment|// make sure threads have been stopped...
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|// skip the randoms - they can deadlock...
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
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

