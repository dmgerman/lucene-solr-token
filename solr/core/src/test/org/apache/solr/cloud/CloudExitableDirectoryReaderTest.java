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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|cloud
operator|.
name|DocCollection
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
name|response
operator|.
name|SolrQueryResponse
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
comment|/** * Distributed test for {@link org.apache.lucene.index.ExitableDirectoryReader}  */
end_comment

begin_class
DECL|class|CloudExitableDirectoryReaderTest
specifier|public
class|class
name|CloudExitableDirectoryReaderTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|NUM_DOCS_PER_TYPE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS_PER_TYPE
init|=
literal|20
decl_stmt|;
DECL|field|sleep
specifier|private
specifier|static
specifier|final
name|String
name|sleep
init|=
literal|"2"
decl_stmt|;
DECL|field|COLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION
init|=
literal|"exitable"
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
literal|2
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
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
literal|"exitable-directory"
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
name|COLLECTION
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|waitForState
argument_list|(
name|COLLECTION
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
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
name|indexDocs
argument_list|()
expr_stmt|;
name|doTimeoutTests
argument_list|()
expr_stmt|;
block|}
DECL|method|indexDocs
specifier|public
name|void
name|indexDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|counter
init|=
literal|1
decl_stmt|;
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
for|for
control|(
init|;
operator|(
name|counter
operator|%
name|NUM_DOCS_PER_TYPE
operator|)
operator|!=
literal|0
condition|;
name|counter
operator|++
control|)
name|req
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"a"
operator|+
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
for|for
control|(
init|;
operator|(
name|counter
operator|%
name|NUM_DOCS_PER_TYPE
operator|)
operator|!=
literal|0
condition|;
name|counter
operator|++
control|)
name|req
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"b"
operator|+
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
for|for
control|(
init|;
name|counter
operator|%
name|NUM_DOCS_PER_TYPE
operator|!=
literal|0
condition|;
name|counter
operator|++
control|)
name|req
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"dummy term doc"
operator|+
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|COLLECTION
argument_list|)
expr_stmt|;
block|}
DECL|method|doTimeoutTests
specifier|public
name|void
name|doTimeoutTests
parameter_list|()
throws|throws
name|Exception
block|{
name|assertPartialResults
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"name:a*"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1"
argument_list|,
literal|"sleep"
argument_list|,
name|sleep
argument_list|)
argument_list|)
expr_stmt|;
comment|/*     query rewriting for NUM_DOCS_PER_TYPE terms should take less      time than this. Keeping it at 5 because the delaying search component delays all requests      by at 1 second.      */
name|int
name|fiveSeconds
init|=
literal|5000
decl_stmt|;
name|Integer
name|timeAllowed
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|fiveSeconds
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertSuccess
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"name:a*"
argument_list|,
literal|"timeAllowed"
argument_list|,
name|timeAllowed
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertPartialResults
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"name:a*"
argument_list|,
literal|"timeAllowed"
argument_list|,
literal|"1"
argument_list|,
literal|"sleep"
argument_list|,
name|sleep
argument_list|)
argument_list|)
expr_stmt|;
name|timeAllowed
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|fiveSeconds
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|,
literal|"timeAllowed"
argument_list|,
name|timeAllowed
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// negative timeAllowed should disable timeouts
name|timeAllowed
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|,
literal|"timeAllowed"
argument_list|,
name|timeAllowed
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"name:b*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// no time limitation
block|}
comment|/**    * execute a request, verify that we get an expected error    */
DECL|method|assertPartialResults
specifier|public
name|void
name|assertPartialResults
parameter_list|(
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryResponse
name|rsp
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|COLLECTION
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_PARTIAL_RESULTS_KEY
operator|+
literal|" were expected"
argument_list|,
literal|true
argument_list|,
name|rsp
operator|.
name|getHeader
argument_list|()
operator|.
name|get
argument_list|(
name|SolrQueryResponse
operator|.
name|RESPONSE_HEADER_PARTIAL_RESULTS_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSuccess
specifier|public
name|void
name|assertSuccess
parameter_list|(
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryResponse
name|response
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|COLLECTION
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong #docs in response"
argument_list|,
name|NUM_DOCS_PER_TYPE
operator|-
literal|1
argument_list|,
name|response
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
end_class

end_unit

