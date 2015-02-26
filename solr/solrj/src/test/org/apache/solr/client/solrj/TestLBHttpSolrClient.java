begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|IOUtils
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
name|lucene
operator|.
name|util
operator|.
name|QuickPatchThreadsFilter
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
name|SolrIgnoredThreadsFilter
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
name|embedded
operator|.
name|JettyConfig
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
name|HttpClientUtil
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
name|impl
operator|.
name|LBHttpSolrClient
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
name|SolrResponseBase
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
name|file
operator|.
name|Files
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Test for LBHttpSolrClient  *  * @since solr 1.4  */
end_comment

begin_class
annotation|@
name|Slow
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|SolrIgnoredThreadsFilter
operator|.
name|class
block|,
name|QuickPatchThreadsFilter
operator|.
name|class
block|}
argument_list|)
DECL|class|TestLBHttpSolrClient
specifier|public
class|class
name|TestLBHttpSolrClient
extends|extends
name|SolrTestCaseJ4
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
name|TestLBHttpSolrClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|solr
name|SolrInstance
index|[]
name|solr
init|=
operator|new
name|SolrInstance
index|[
literal|3
index|]
decl_stmt|;
DECL|field|httpClient
name|CloseableHttpClient
name|httpClient
decl_stmt|;
comment|// TODO: fix this test to not require FSDirectory
DECL|field|savedFactory
specifier|static
name|String
name|savedFactory
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|savedFactory
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.DirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"org.apache.solr.core.MockFSDirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
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
block|{
if|if
condition|(
name|savedFactory
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|savedFactory
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|)
expr_stmt|;
block|}
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
name|httpClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|HttpClientUtil
operator|.
name|setConnectionTimeout
argument_list|(
name|httpClient
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|solr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|solr
index|[
name|i
index|]
operator|=
operator|new
name|SolrInstance
argument_list|(
literal|"solr/collection1"
operator|+
name|i
argument_list|,
name|createTempDir
argument_list|(
literal|"instance-"
operator|+
name|i
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|solr
index|[
name|i
index|]
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|solr
index|[
name|i
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
name|addDocs
argument_list|(
name|solr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|SolrInstance
name|solrInstance
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
name|solrInstance
operator|.
name|name
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|SolrResponseBase
name|resp
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|solrInstance
operator|.
name|getUrl
argument_list|()
argument_list|,
name|httpClient
argument_list|)
init|)
block|{
name|resp
operator|=
name|client
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|resp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|=
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|resp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|SolrInstance
name|aSolr
range|:
name|solr
control|)
block|{
if|if
condition|(
name|aSolr
operator|!=
literal|null
condition|)
block|{
name|aSolr
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
name|httpClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|solr
operator|.
name|length
index|]
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
name|solr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|s
index|[
name|i
index|]
operator|=
name|solr
index|[
name|i
index|]
operator|.
name|getUrl
argument_list|()
expr_stmt|;
block|}
name|LBHttpSolrClient
name|client
init|=
operator|new
name|LBHttpSolrClient
argument_list|(
name|httpClient
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|client
operator|.
name|setAliveCheckInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|QueryResponse
name|resp
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|s
control|)
block|{
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Kill a server and test again
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|s
control|)
block|{
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"solr1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Start the killed server once again
name|solr
index|[
literal|1
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
comment|// Wait for the alive check to complete
name|Thread
operator|.
name|sleep
argument_list|(
literal|1200
argument_list|)
expr_stmt|;
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|s
control|)
block|{
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoServers
specifier|public
name|void
name|testTwoServers
parameter_list|()
throws|throws
name|Exception
block|{
name|LBHttpSolrClient
name|client
init|=
operator|new
name|LBHttpSolrClient
argument_list|(
name|httpClient
argument_list|,
name|solr
index|[
literal|0
index|]
operator|.
name|getUrl
argument_list|()
argument_list|,
name|solr
index|[
literal|1
index|]
operator|.
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|setAliveCheckInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|resp
init|=
literal|null
decl_stmt|;
name|solr
index|[
literal|0
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|0
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr/collection11"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|name
operator|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr/collection11"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
name|solr
index|[
literal|0
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1200
argument_list|)
expr_stmt|;
try|try
block|{
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
comment|// try again after a pause in case the error is lack of time to start server
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr/collection10"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|testReliability
specifier|public
name|void
name|testReliability
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|solr
operator|.
name|length
index|]
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
name|solr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|s
index|[
name|i
index|]
operator|=
name|solr
index|[
name|i
index|]
operator|.
name|getUrl
argument_list|()
expr_stmt|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|,
literal|250
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|,
literal|250
argument_list|)
expr_stmt|;
name|CloseableHttpClient
name|myHttpClient
init|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
name|params
argument_list|)
decl_stmt|;
try|try
block|{
name|LBHttpSolrClient
name|client
init|=
operator|new
name|LBHttpSolrClient
argument_list|(
name|myHttpClient
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|client
operator|.
name|setAliveCheckInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// Kill a server and test again
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
comment|// query the servers
for|for
control|(
name|String
name|value
range|:
name|s
control|)
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Start the killed server once again
name|solr
index|[
literal|1
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
comment|// Wait for the alive check to complete
name|waitForServer
argument_list|(
literal|30000
argument_list|,
name|client
argument_list|,
literal|3
argument_list|,
literal|"solr1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|myHttpClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// wait maximum ms for serverName to come back up
DECL|method|waitForServer
specifier|private
name|void
name|waitForServer
parameter_list|(
name|int
name|maximum
parameter_list|,
name|LBHttpSolrClient
name|client
parameter_list|,
name|int
name|nServers
parameter_list|,
name|String
name|serverName
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|maximum
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
condition|)
block|{
name|QueryResponse
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|name
init|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|serverName
argument_list|)
condition|)
return|return;
block|}
block|}
DECL|class|SolrInstance
specifier|private
class|class
name|SolrInstance
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|homeDir
name|File
name|homeDir
decl_stmt|;
DECL|field|dataDir
name|File
name|dataDir
decl_stmt|;
DECL|field|confDir
name|File
name|confDir
decl_stmt|;
DECL|field|port
name|int
name|port
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|method|SolrInstance
specifier|public
name|SolrInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|File
name|homeDir
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|homeDir
operator|=
name|homeDir
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
literal|"/collection1"
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
literal|"/collection1"
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
block|}
DECL|method|getHomeDir
specifier|public
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|homeDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getUrl
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|buildUrl
argument_list|(
name|port
argument_list|,
literal|"/solr/collection1"
argument_list|)
return|;
block|}
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"solrj/solr/collection1/conf/schema-replication1.xml"
return|;
block|}
DECL|method|getConfDir
specifier|public
name|String
name|getConfDir
parameter_list|()
block|{
return|return
name|confDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrj/solr/collection1/conf/solrconfig-slave1.xml"
return|;
block|}
DECL|method|getSolrXmlFile
specifier|public
name|String
name|getSolrXmlFile
parameter_list|()
block|{
return|return
literal|"solrj/solr/solr.xml"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|getSolrXmlFile
argument_list|()
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|homeDir
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1/core.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|homeDir
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|startJetty
specifier|public
name|void
name|startJetty
parameter_list|()
throws|throws
name|Exception
block|{
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|getHomeDir
argument_list|()
argument_list|,
literal|"bad_solrconfig.xml"
argument_list|,
literal|null
argument_list|,
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|setContext
argument_list|(
literal|"/solr"
argument_list|)
operator|.
name|stopAtShutdown
argument_list|(
literal|true
argument_list|)
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
operator|.
name|withSSLConfig
argument_list|(
name|sslConfig
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|newPort
init|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|!=
literal|0
operator|&&
name|newPort
operator|!=
name|port
condition|)
block|{
name|fail
argument_list|(
literal|"TESTING FAILURE: could not grab requested port."
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|port
operator|=
name|newPort
expr_stmt|;
comment|//      System.out.println("waiting.........");
comment|//      Thread.sleep(5000);
block|}
block|}
block|}
end_class

end_unit

