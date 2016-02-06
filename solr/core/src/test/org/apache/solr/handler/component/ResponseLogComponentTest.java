begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

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
name|request
operator|.
name|SolrQueryRequest
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

begin_class
DECL|class|ResponseLogComponentTest
specifier|public
class|class
name|ResponseLogComponentTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-response-log-component.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"subject"
argument_list|,
literal|"aa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"two"
argument_list|,
literal|"subject"
argument_list|,
literal|"aa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"subject"
argument_list|,
literal|"aa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToLogIds
specifier|public
name|void
name|testToLogIds
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|handler
init|=
literal|"withlog"
decl_stmt|;
name|req
operator|=
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"withlog"
argument_list|,
literal|"q"
argument_list|,
literal|"aa"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,subject"
argument_list|,
literal|"responseLog"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|qr
init|=
name|h
operator|.
name|queryAndResponse
argument_list|(
name|handler
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|entries
init|=
name|qr
operator|.
name|getToLog
argument_list|()
decl_stmt|;
name|String
name|responseLog
init|=
operator|(
name|String
operator|)
name|entries
operator|.
name|get
argument_list|(
literal|"responseLog"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|responseLog
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|responseLog
operator|.
name|matches
argument_list|(
literal|"\\w+,\\w+"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testToLogScores
specifier|public
name|void
name|testToLogScores
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|handler
init|=
literal|"withlog"
decl_stmt|;
name|req
operator|=
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"withlog"
argument_list|,
literal|"q"
argument_list|,
literal|"aa"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,subject,score"
argument_list|,
literal|"responseLog"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|qr
init|=
name|h
operator|.
name|queryAndResponse
argument_list|(
name|handler
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|entries
init|=
name|qr
operator|.
name|getToLog
argument_list|()
decl_stmt|;
name|String
name|responseLog
init|=
operator|(
name|String
operator|)
name|entries
operator|.
name|get
argument_list|(
literal|"responseLog"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|responseLog
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|responseLog
operator|.
name|matches
argument_list|(
literal|"\\w+:\\d+\\.\\d+,\\w+:\\d+\\.\\d+"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDisabling
specifier|public
name|void
name|testDisabling
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|handler
init|=
literal|"withlog"
decl_stmt|;
name|req
operator|=
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"withlog"
argument_list|,
literal|"q"
argument_list|,
literal|"aa"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,subject"
argument_list|,
literal|"responseLog"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|qr
init|=
name|h
operator|.
name|queryAndResponse
argument_list|(
name|handler
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|entries
init|=
name|qr
operator|.
name|getToLog
argument_list|()
decl_stmt|;
name|String
name|responseLog
init|=
operator|(
name|String
operator|)
name|entries
operator|.
name|get
argument_list|(
literal|"responseLog"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|responseLog
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

