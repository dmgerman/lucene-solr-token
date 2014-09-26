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
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SolrException
operator|.
name|ErrorCode
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
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloudExitableDirectoryReaderTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_DOCS_PER_TYPE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS_PER_TYPE
init|=
literal|2000
decl_stmt|;
DECL|method|CloudExitableDirectoryReaderTest
specifier|public
name|CloudExitableDirectoryReaderTest
parameter_list|()
block|{
name|configString
operator|=
literal|"solrconfig-tlog.xml"
expr_stmt|;
name|schemaString
operator|=
literal|"schema.xml"
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
name|configString
return|;
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
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|indexDoc
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
name|indexDoc
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
name|indexDoc
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
name|commit
argument_list|()
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
name|assertFail
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
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|oneSecond
init|=
literal|1000L
decl_stmt|;
comment|// query rewriting for NUM_DOCS_PER_TYPE terms should take less time than this
name|Long
name|timeAllowed
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
name|oneSecond
argument_list|,
name|Long
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
name|assertFail
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
argument_list|)
argument_list|)
expr_stmt|;
name|timeAllowed
operator|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
name|oneSecond
argument_list|,
name|Long
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
name|timeAllowed
operator|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
comment|// negative timeAllowed should disable timeouts
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
DECL|method|assertFail
specifier|public
name|void
name|assertFail
parameter_list|(
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|timeoutMessage
init|=
literal|"Request took too long during query expansion. Terminating request."
decl_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
name|timeoutMessage
argument_list|)
expr_stmt|;
name|queryServer
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no exception matching expected: "
operator|+
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
operator|+
literal|": "
operator|+
name|timeoutMessage
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Exception "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|+
literal|" is not a SolrException:\n"
operator|+
name|prettyStackTrace
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SolrException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|.
name|getCause
argument_list|()
operator|)
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected error message substr not found: "
operator|+
name|timeoutMessage
operator|+
literal|"<!< "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|timeoutMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|unIgnoreException
argument_list|(
name|timeoutMessage
argument_list|)
expr_stmt|;
block|}
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
name|queryServer
argument_list|(
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
DECL|method|prettyStackTrace
specifier|public
name|String
name|prettyStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|elem
range|:
name|t
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"    at "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|elem
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

