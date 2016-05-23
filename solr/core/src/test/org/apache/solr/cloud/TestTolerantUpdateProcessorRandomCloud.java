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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|TestTolerantUpdateProcessorCloud
operator|.
name|addErr
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|TestTolerantUpdateProcessorCloud
operator|.
name|assertUpdateTolerantErrors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|TestTolerantUpdateProcessorCloud
operator|.
name|delIErr
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|TestTolerantUpdateProcessorCloud
operator|.
name|delQErr
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|TestTolerantUpdateProcessorCloud
operator|.
name|f
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|TestTolerantUpdateProcessorCloud
operator|.
name|update
import|;
end_import

begin_import
import|import static
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
name|CursorMarkParams
operator|.
name|CURSOR_MARK_PARAM
import|;
end_import

begin_import
import|import static
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
name|CursorMarkParams
operator|.
name|CURSOR_MARK_START
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

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
name|BitSet
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
name|Random
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
name|TestUtil
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
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
name|TestTolerantUpdateProcessorCloud
operator|.
name|ExpectedErr
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
name|SolrInputField
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
name|SolrParams
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

begin_comment
comment|/**  * Test of TolerantUpdateProcessor using a randomized MiniSolrCloud.  * Reuses some utility methods in {@link TestTolerantUpdateProcessorCloud}  *  *<p>  *<b>NOTE:</b> This test sets up a static instance of MiniSolrCloud with a single collection   * and several clients pointed at specific nodes. These are all re-used across multiple test methods,   * and assumes that the state of the cluster is healthy between tests.  *</p>  *  */
end_comment

begin_class
DECL|class|TestTolerantUpdateProcessorRandomCloud
specifier|public
class|class
name|TestTolerantUpdateProcessorRandomCloud
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|COLLECTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"test_col"
decl_stmt|;
comment|/** A basic client for operations at the cloud level, default collection will be set */
DECL|field|CLOUD_CLIENT
specifier|private
specifier|static
name|CloudSolrClient
name|CLOUD_CLIENT
decl_stmt|;
comment|/** one HttpSolrClient for each server */
DECL|field|NODE_CLIENTS
specifier|private
specifier|static
name|List
argument_list|<
name|HttpSolrClient
argument_list|>
name|NODE_CLIENTS
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createMiniSolrCloudCluster
specifier|private
specifier|static
name|void
name|createMiniSolrCloudCluster
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
specifier|final
name|File
name|configDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numShards
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
name|TEST_NIGHTLY
condition|?
literal|5
else|:
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|int
name|repFactor
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
name|TEST_NIGHTLY
condition|?
literal|5
else|:
literal|3
argument_list|)
decl_stmt|;
comment|// at least one server won't have any replicas
specifier|final
name|int
name|numServers
init|=
literal|1
operator|+
operator|(
name|numShards
operator|*
name|repFactor
operator|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Configuring cluster: servers={}, shards={}, repfactor={}"
argument_list|,
name|numServers
argument_list|,
name|numShards
argument_list|,
name|repFactor
argument_list|)
expr_stmt|;
name|configureCluster
argument_list|(
name|numServers
argument_list|)
operator|.
name|addConfig
argument_list|(
name|configName
argument_list|,
name|configDir
operator|.
name|toPath
argument_list|()
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|TestTolerantUpdateProcessorCloud
operator|.
name|assertSpinLoopAllJettyAreRunning
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionProperties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"config"
argument_list|,
literal|"solrconfig-distrib-update-processor-chains.xml"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"schema"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
comment|// string id
name|assertNotNull
argument_list|(
name|cluster
operator|.
name|createCollection
argument_list|(
name|COLLECTION_NAME
argument_list|,
name|numShards
argument_list|,
name|repFactor
argument_list|,
name|configName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|collectionProperties
argument_list|)
argument_list|)
expr_stmt|;
name|CLOUD_CLIENT
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
expr_stmt|;
name|CLOUD_CLIENT
operator|.
name|setDefaultCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|NODE_CLIENTS
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|HttpSolrClient
name|client
range|:
name|NODE_CLIENTS
control|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|NODE_CLIENTS
operator|=
operator|new
name|ArrayList
argument_list|<
name|HttpSolrClient
argument_list|>
argument_list|(
name|numServers
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|URL
name|jettyURL
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|NODE_CLIENTS
operator|.
name|add
argument_list|(
name|getHttpSolrClient
argument_list|(
name|jettyURL
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|COLLECTION_NAME
operator|+
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numServers
argument_list|,
name|NODE_CLIENTS
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|CLOUD_CLIENT
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|COLLECTION_NAME
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|deleteAllDocs
specifier|private
name|void
name|deleteAllDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|update
argument_list|(
name|params
argument_list|(
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|process
argument_list|(
name|CLOUD_CLIENT
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"index should be empty"
argument_list|,
literal|0L
argument_list|,
name|countDocs
argument_list|(
name|CLOUD_CLIENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|NODE_CLIENTS
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|HttpSolrClient
name|client
range|:
name|NODE_CLIENTS
control|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|NODE_CLIENTS
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|CLOUD_CLIENT
operator|!=
literal|null
condition|)
block|{
name|CLOUD_CLIENT
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|CLOUD_CLIENT
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testRandomUpdates
specifier|public
name|void
name|testRandomUpdates
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|maxDocId
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|expectedDocIds
init|=
operator|new
name|BitSet
argument_list|(
name|maxDocId
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"BEGIN ITER #{}"
argument_list|,
name|i
argument_list|)
expr_stmt|;
specifier|final
name|UpdateRequest
name|req
init|=
name|update
argument_list|(
name|params
argument_list|(
literal|"maxErrors"
argument_list|,
literal|"-1"
argument_list|,
literal|"update.chain"
argument_list|,
literal|"tolerant-chain-max-errors-10"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numCmds
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ExpectedErr
argument_list|>
name|expectedErrors
init|=
operator|new
name|ArrayList
argument_list|<
name|ExpectedErr
argument_list|>
argument_list|(
name|numCmds
argument_list|)
decl_stmt|;
name|int
name|expectedErrorsCount
init|=
literal|0
decl_stmt|;
comment|// it's ambigious/confusing which order mixed DELQ + ADD  (or ADD and DELI for the same ID)
comment|// in the same request wll be processed by various clients, so we keep things simple
comment|// and ensure that no single doc Id is affected by more then one command in the same request
specifier|final
name|BitSet
name|docsAffectedThisRequest
init|=
operator|new
name|BitSet
argument_list|(
name|maxDocId
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|cmdIter
init|=
literal|0
init|;
name|cmdIter
operator|<
name|numCmds
condition|;
name|cmdIter
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|maxDocId
operator|/
literal|2
operator|)
operator|<
name|docsAffectedThisRequest
operator|.
name|cardinality
argument_list|()
condition|)
block|{
comment|// we're already mucking with more then half the docs in the index
break|break;
block|}
specifier|final
name|boolean
name|causeError
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|causeError
condition|)
block|{
name|expectedErrorsCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// add a doc
name|String
name|id
init|=
literal|null
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|causeError
operator|&&
operator|(
literal|0
operator|==
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|21
argument_list|)
operator|)
condition|)
block|{
name|doc
operator|=
name|doc
argument_list|(
name|f
argument_list|(
literal|"foo_s"
argument_list|,
literal|"no unique key"
argument_list|)
argument_list|)
expr_stmt|;
name|expectedErrors
operator|.
name|add
argument_list|(
name|addErr
argument_list|(
literal|"(unknown)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|id_i
init|=
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|docsAffectedThisRequest
argument_list|,
name|maxDocId
argument_list|)
decl_stmt|;
name|docsAffectedThisRequest
operator|.
name|set
argument_list|(
name|id_i
argument_list|)
expr_stmt|;
name|id
operator|=
literal|"id_"
operator|+
name|id_i
expr_stmt|;
if|if
condition|(
name|causeError
condition|)
block|{
name|expectedErrors
operator|.
name|add
argument_list|(
name|addErr
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectedDocIds
operator|.
name|set
argument_list|(
name|id_i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|val
init|=
name|causeError
condition|?
literal|"bogus_val"
else|:
operator|(
literal|""
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|42
argument_list|,
literal|666
argument_list|)
operator|)
decl_stmt|;
name|doc
operator|=
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"id_i"
argument_list|,
name|id_i
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"foo_i"
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"ADD: {} = {}"
argument_list|,
name|id
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// delete something
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// delete by id
specifier|final
name|int
name|id_i
init|=
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|docsAffectedThisRequest
argument_list|,
name|maxDocId
argument_list|)
decl_stmt|;
specifier|final
name|String
name|id
init|=
literal|"id_"
operator|+
name|id_i
decl_stmt|;
specifier|final
name|boolean
name|docExists
init|=
name|expectedDocIds
operator|.
name|get
argument_list|(
name|id_i
argument_list|)
decl_stmt|;
name|docsAffectedThisRequest
operator|.
name|set
argument_list|(
name|id_i
argument_list|)
expr_stmt|;
name|long
name|versionConstraint
init|=
name|docExists
condition|?
literal|1
else|:
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|causeError
condition|)
block|{
name|versionConstraint
operator|=
operator|-
literal|1
operator|*
name|versionConstraint
expr_stmt|;
name|expectedErrors
operator|.
name|add
argument_list|(
name|delIErr
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if doc exists it will legitimately be deleted
name|expectedDocIds
operator|.
name|clear
argument_list|(
name|id_i
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|deleteById
argument_list|(
name|id
argument_list|,
name|versionConstraint
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"DEL: {} = {}"
argument_list|,
name|id
argument_list|,
name|causeError
condition|?
literal|"ERR"
else|:
literal|"OK"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// delete by query
specifier|final
name|String
name|q
decl_stmt|;
if|if
condition|(
name|causeError
condition|)
block|{
comment|// even though our DBQ is gibberish that's going to fail, record a docId as affected
comment|// so that we don't generate the same random DBQ and get redundent errors
comment|// (problematic because of how DUP forwarded DBQs have to have their errors deduped by TUP)
specifier|final
name|int
name|id_i
init|=
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|docsAffectedThisRequest
argument_list|,
name|maxDocId
argument_list|)
decl_stmt|;
name|docsAffectedThisRequest
operator|.
name|set
argument_list|(
name|id_i
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"foo_i:["
operator|+
name|id_i
operator|+
literal|" TO ....giberish"
expr_stmt|;
name|expectedErrors
operator|.
name|add
argument_list|(
name|delQErr
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// ensure our DBQ is only over a range of docs not already affected
comment|// by any other cmds in this request
specifier|final
name|int
name|rangeAxis
init|=
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|docsAffectedThisRequest
argument_list|,
name|maxDocId
argument_list|)
decl_stmt|;
specifier|final
name|int
name|loBound
init|=
name|docsAffectedThisRequest
operator|.
name|previousSetBit
argument_list|(
name|rangeAxis
argument_list|)
decl_stmt|;
specifier|final
name|int
name|hiBound
init|=
name|docsAffectedThisRequest
operator|.
name|nextSetBit
argument_list|(
name|rangeAxis
argument_list|)
decl_stmt|;
specifier|final
name|int
name|lo
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|loBound
operator|+
literal|1
argument_list|,
name|rangeAxis
argument_list|)
decl_stmt|;
specifier|final
name|int
name|hi
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|rangeAxis
argument_list|,
comment|// bound might be negative if no set bits above axis
operator|(
name|hiBound
operator|<
literal|0
operator|)
condition|?
name|maxDocId
else|:
name|hiBound
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|lo
operator|!=
name|hi
condition|)
block|{
assert|assert
name|lo
operator|<
name|hi
operator|:
literal|"lo="
operator|+
name|lo
operator|+
literal|" hi="
operator|+
name|hi
assert|;
comment|// NOTE: clear& set are exclusive of hi, so we use "}" in range query accordingly
name|q
operator|=
literal|"id_i:["
operator|+
name|lo
operator|+
literal|" TO "
operator|+
name|hi
operator|+
literal|"}"
expr_stmt|;
name|expectedDocIds
operator|.
name|clear
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
name|docsAffectedThisRequest
operator|.
name|set
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// edge case: special case DBQ of one doc
assert|assert
operator|(
name|lo
operator|==
name|rangeAxis
operator|&&
name|hi
operator|==
name|rangeAxis
operator|)
operator|:
literal|"lo="
operator|+
name|lo
operator|+
literal|" axis="
operator|+
name|rangeAxis
operator|+
literal|" hi="
operator|+
name|hi
assert|;
name|q
operator|=
literal|"id_i:["
operator|+
name|lo
operator|+
literal|" TO "
operator|+
name|lo
operator|+
literal|"]"
expr_stmt|;
comment|// have to be inclusive of both ends
name|expectedDocIds
operator|.
name|clear
argument_list|(
name|lo
argument_list|)
expr_stmt|;
name|docsAffectedThisRequest
operator|.
name|set
argument_list|(
name|lo
argument_list|)
expr_stmt|;
block|}
block|}
name|req
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"DEL: {}"
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|"expected error count sanity check: "
operator|+
name|req
operator|.
name|toString
argument_list|()
argument_list|,
name|expectedErrorsCount
argument_list|,
name|expectedErrors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SolrClient
name|client
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|CLOUD_CLIENT
else|:
name|NODE_CLIENTS
operator|.
name|get
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|NODE_CLIENTS
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|UpdateResponse
name|rsp
init|=
name|req
operator|.
name|process
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|assertUpdateTolerantErrors
argument_list|(
name|client
operator|.
name|toString
argument_list|()
operator|+
literal|" => "
operator|+
name|expectedErrors
operator|.
name|toString
argument_list|()
argument_list|,
name|rsp
argument_list|,
name|expectedErrors
operator|.
name|toArray
argument_list|(
operator|new
name|ExpectedErr
index|[
name|expectedErrors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"END ITER #{}, expecting #docs: {}"
argument_list|,
name|i
argument_list|,
name|expectedDocIds
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"post update commit failed?"
argument_list|,
literal|0
argument_list|,
name|CLOUD_CLIENT
operator|.
name|commit
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|expectedDocIds
operator|.
name|cardinality
argument_list|()
operator|==
name|countDocs
argument_list|(
name|CLOUD_CLIENT
argument_list|)
condition|)
block|{
break|break;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"sleeping to give searchers a chance to re-open #"
operator|+
name|j
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
comment|// check the index contents against our expectations
specifier|final
name|BitSet
name|actualDocIds
init|=
name|allDocs
argument_list|(
name|CLOUD_CLIENT
argument_list|,
name|maxDocId
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedDocIds
operator|.
name|cardinality
argument_list|()
operator|!=
name|actualDocIds
operator|.
name|cardinality
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"cardinality mismatch: expected {} BUT actual {}"
argument_list|,
name|expectedDocIds
operator|.
name|cardinality
argument_list|()
argument_list|,
name|actualDocIds
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BitSet
name|x
init|=
operator|(
name|BitSet
operator|)
name|actualDocIds
operator|.
name|clone
argument_list|()
decl_stmt|;
name|x
operator|.
name|xor
argument_list|(
name|expectedDocIds
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|b
init|=
name|x
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
literal|0
operator|<=
name|b
condition|;
name|b
operator|=
name|x
operator|.
name|nextSetBit
argument_list|(
name|b
operator|+
literal|1
argument_list|)
control|)
block|{
specifier|final
name|boolean
name|expectedBit
init|=
name|expectedDocIds
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|actualBit
init|=
name|actualDocIds
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"bit #"
operator|+
name|b
operator|+
literal|" mismatch: expected {} BUT actual {}"
argument_list|,
name|expectedBit
argument_list|,
name|actualBit
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|x
operator|.
name|cardinality
argument_list|()
operator|+
literal|" mismatched bits"
argument_list|,
name|expectedDocIds
operator|.
name|cardinality
argument_list|()
argument_list|,
name|actualDocIds
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** sanity check that randomUnsetBit works as expected     * @see #randomUnsetBit    */
DECL|method|testSanityRandomUnsetBit
specifier|public
name|void
name|testSanityRandomUnsetBit
parameter_list|()
block|{
specifier|final
name|int
name|max
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|max
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
literal|"how is bitset already full? iter="
operator|+
name|i
operator|+
literal|" card="
operator|+
name|bits
operator|.
name|cardinality
argument_list|()
operator|+
literal|"/max="
operator|+
name|max
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
operator|==
name|max
operator|+
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|nextBit
init|=
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|bits
argument_list|,
name|max
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"nextBit shouldn't be negative yet: "
operator|+
name|nextBit
argument_list|,
literal|0
operator|<=
name|nextBit
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"nextBit can't exceed max: "
operator|+
name|nextBit
argument_list|,
name|nextBit
operator|<=
name|max
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expect unset: "
operator|+
name|nextBit
argument_list|,
name|bits
operator|.
name|get
argument_list|(
name|nextBit
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|nextBit
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"why isn't bitset full?"
argument_list|,
name|max
operator|+
literal|1
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|firstClearBit
init|=
name|bits
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"why is there a clear bit? = "
operator|+
name|firstClearBit
argument_list|,
name|max
operator|<
name|firstClearBit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"why is a bit set above max?"
argument_list|,
operator|-
literal|1
argument_list|,
name|bits
operator|.
name|nextSetBit
argument_list|(
name|max
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong nextBit at end of all iters"
argument_list|,
operator|-
literal|1
argument_list|,
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|bits
argument_list|,
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong nextBit at redundant end of all iters"
argument_list|,
operator|-
literal|1
argument_list|,
name|randomUnsetBit
argument_list|(
name|random
argument_list|()
argument_list|,
name|bits
argument_list|,
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doc
specifier|public
specifier|static
name|SolrInputDocument
name|doc
parameter_list|(
name|SolrInputField
modifier|...
name|fields
parameter_list|)
block|{
comment|// SolrTestCaseJ4 has same method name, prevents static import from working
return|return
name|TestTolerantUpdateProcessorCloud
operator|.
name|doc
argument_list|(
name|fields
argument_list|)
return|;
block|}
comment|/**    * Given a BitSet, returns a random bit that is currently false, or -1 if all bits are true.    * NOTE: this method is not fair.    */
DECL|method|randomUnsetBit
specifier|public
specifier|static
specifier|final
name|int
name|randomUnsetBit
parameter_list|(
name|Random
name|r
parameter_list|,
name|BitSet
name|bits
parameter_list|,
specifier|final
name|int
name|max
parameter_list|)
block|{
comment|// NOTE: don't forget, BitSet will grow automatically if not careful
if|if
condition|(
name|bits
operator|.
name|cardinality
argument_list|()
operator|==
name|max
operator|+
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|final
name|int
name|candidate
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|candidate
argument_list|)
condition|)
block|{
specifier|final
name|int
name|lo
init|=
name|bits
operator|.
name|previousClearBit
argument_list|(
name|candidate
argument_list|)
decl_stmt|;
specifier|final
name|int
name|hi
init|=
name|bits
operator|.
name|nextClearBit
argument_list|(
name|candidate
argument_list|)
decl_stmt|;
if|if
condition|(
name|lo
operator|<
literal|0
operator|&&
name|max
operator|<
name|hi
condition|)
block|{
name|fail
argument_list|(
literal|"how the hell did we not short circut out? card="
operator|+
name|bits
operator|.
name|cardinality
argument_list|()
operator|+
literal|"/size="
operator|+
name|bits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lo
operator|<
literal|0
condition|)
block|{
return|return
name|hi
return|;
block|}
elseif|else
if|if
condition|(
name|max
operator|<
name|hi
condition|)
block|{
return|return
name|lo
return|;
block|}
comment|// else...
return|return
operator|(
operator|(
name|candidate
operator|-
name|lo
operator|)
operator|<
operator|(
name|hi
operator|-
name|candidate
operator|)
operator|)
condition|?
name|lo
else|:
name|hi
return|;
block|}
return|return
name|candidate
return|;
block|}
comment|/** returns the numFound from a *:* query */
DECL|method|countDocs
specifier|public
specifier|static
specifier|final
name|long
name|countDocs
parameter_list|(
name|SolrClient
name|c
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|c
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
return|;
block|}
comment|/** uses a Cursor to iterate over every doc in the index, recording the 'id_i' value in a BitSet */
DECL|method|allDocs
specifier|private
specifier|static
specifier|final
name|BitSet
name|allDocs
parameter_list|(
specifier|final
name|SolrClient
name|c
parameter_list|,
specifier|final
name|int
name|maxDocIdExpected
parameter_list|)
throws|throws
name|Exception
block|{
name|BitSet
name|docs
init|=
operator|new
name|BitSet
argument_list|(
name|maxDocIdExpected
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|cursorMark
init|=
name|CURSOR_MARK_START
decl_stmt|;
name|int
name|docsOnThisPage
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
while|while
condition|(
literal|0
operator|<
name|docsOnThisPage
condition|)
block|{
specifier|final
name|SolrParams
name|p
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"100"
argument_list|,
comment|// note: not numeric, but we don't actual care about the order
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
name|CURSOR_MARK_PARAM
argument_list|,
name|cursorMark
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|c
operator|.
name|query
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|cursorMark
operator|=
name|rsp
operator|.
name|getNextCursorMark
argument_list|()
expr_stmt|;
name|docsOnThisPage
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|docsOnThisPage
operator|++
expr_stmt|;
name|int
name|id_i
init|=
operator|(
operator|(
name|Integer
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id_i"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"found id_i bigger then expected "
operator|+
name|maxDocIdExpected
operator|+
literal|": "
operator|+
name|id_i
argument_list|,
name|id_i
operator|<=
name|maxDocIdExpected
argument_list|)
expr_stmt|;
name|docs
operator|.
name|set
argument_list|(
name|id_i
argument_list|)
expr_stmt|;
block|}
name|cursorMark
operator|=
name|rsp
operator|.
name|getNextCursorMark
argument_list|()
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
block|}
end_class

end_unit

