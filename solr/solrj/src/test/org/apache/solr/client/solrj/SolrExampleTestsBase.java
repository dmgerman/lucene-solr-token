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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|solr
operator|.
name|SolrJettyTestBase
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
name|request
operator|.
name|AbstractUpdateRequest
operator|.
name|ACTION
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
name|util
operator|.
name|ClientUtils
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
name|params
operator|.
name|CommonParams
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
DECL|class|SolrExampleTestsBase
specifier|abstract
specifier|public
class|class
name|SolrExampleTestsBase
extends|extends
name|SolrJettyTestBase
block|{
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
name|SolrExampleTestsBase
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * query the example    */
annotation|@
name|Test
DECL|method|testCommitWithinOnAdd
specifier|public
name|void
name|testCommitWithinOnAdd
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure it is empty...
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now try a timed commit...
name|SolrInputDocument
name|doc3
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"id3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"doc3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|add
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|up
operator|.
name|setCommitWithin
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// a smaller commitWithin caused failures on the
comment|// following assert
name|up
operator|.
name|process
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|server
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: not a great way to test this - timing is easily out
comment|// of whack due to parallel tests and various computer specs/load
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// wait 1 sec
comment|// now check that it comes out...
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id3"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// wait and try again for slower/busier machines
comment|// and/or parallel test effects.
if|if
condition|(
name|cnt
operator|++
operator|==
literal|10
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// wait 2 seconds...
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now test the new convenience parameter on the add() for commitWithin
name|SolrInputDocument
name|doc4
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"id4"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"doc4"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc4
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// wait 1 sec
comment|// now check that it comes out...
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id4"
argument_list|)
argument_list|)
expr_stmt|;
name|cnt
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// wait and try again for slower/busier machines
comment|// and/or parallel test effects.
if|if
condition|(
name|cnt
operator|++
operator|==
literal|10
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// wait 2 seconds...
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitWithinOnDelete
specifier|public
name|void
name|testCommitWithinOnDelete
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure it is empty...
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now add one document...
name|SolrInputDocument
name|doc3
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"id3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"doc3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|addField
argument_list|(
literal|"price"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// now check that it comes out...
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id3"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// now test commitWithin on a delete
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
literal|1000
argument_list|)
expr_stmt|;
name|up
operator|.
name|deleteById
argument_list|(
literal|"id3"
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|server
argument_list|)
expr_stmt|;
comment|// the document should still be there
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id3"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// check if the doc has been deleted every 250 ms for 30 seconds
name|long
name|timeout
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|30000
decl_stmt|;
do|do
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
comment|// wait 250 ms
name|rsp
operator|=
name|server
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:id3"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
block|}
do|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|timeout
condition|)
do|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"commitWithin failed to commit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddDelete
specifier|public
name|void
name|testAddDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|// Empty the database...
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|SolrInputDocument
index|[]
name|doc
init|=
operator|new
name|SolrInputDocument
index|[
literal|3
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|doc
index|[
name|i
index|]
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
index|[
name|i
index|]
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
name|i
operator|+
literal|"& 222"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
name|String
name|id
init|=
operator|(
name|String
operator|)
name|doc
index|[
literal|0
index|]
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getFirstValue
argument_list|()
decl_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// make sure it got in there
name|server
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got out
comment|// add it back
name|server
operator|.
name|add
argument_list|(
name|doc
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// make sure it got in
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"id:\""
operator|+
name|ClientUtils
operator|.
name|escapeQueryChars
argument_list|(
name|id
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got out
comment|// Add two documents
for|for
control|(
name|SolrInputDocument
name|d
range|:
name|doc
control|)
block|{
name|server
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// should be able to handle multiple delete commands in a single go
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrInputDocument
name|d
range|:
name|doc
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|deleteById
argument_list|(
name|ids
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got out
block|}
annotation|@
name|Test
DECL|method|testStreamingRequest
specifier|public
name|void
name|testStreamingRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|// Empty the database...
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// Add some docs to the index
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
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
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"cat"
argument_list|,
literal|"foocat"
argument_list|)
expr_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|setAction
argument_list|(
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
expr_stmt|;
comment|// Make sure it ran OK
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
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id,score,_docid_"
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
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
comment|// Now make sure each document gets output
specifier|final
name|AtomicInteger
name|cnt
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|server
operator|.
name|queryAndStreamResponse
argument_list|(
name|query
argument_list|,
operator|new
name|StreamingResponseCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|streamDocListInfo
parameter_list|(
name|long
name|numFound
parameter_list|,
name|long
name|start
parameter_list|,
name|Float
name|maxScore
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|streamSolrDocument
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
block|{
name|cnt
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// Make sure the transformer works for streaming
name|Float
name|score
init|=
operator|(
name|Float
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"should have score"
argument_list|,
operator|new
name|Float
argument_list|(
literal|1.0
argument_list|)
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|cnt
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNumFound
specifier|protected
name|void
name|assertNumFound
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|QueryResponse
name|rsp
init|=
name|getSolrServer
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|num
operator|!=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"expected: "
operator|+
name|num
operator|+
literal|" but had: "
operator|+
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|+
literal|" :: "
operator|+
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

