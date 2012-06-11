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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|SolrIndexSearcher
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
name|RefCounted
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
DECL|class|TestQuerySenderNoQuery
specifier|public
class|class
name|TestQuerySenderNoQuery
extends|extends
name|SolrTestCaseJ4
block|{
comment|// number of instances configured in the solrconfig.xml
DECL|field|EXPECTED_MOCK_LISTENER_INSTANCES
specifier|private
specifier|static
specifier|final
name|int
name|EXPECTED_MOCK_LISTENER_INSTANCES
init|=
literal|4
decl_stmt|;
DECL|field|preInitMockListenerCount
specifier|private
specifier|static
name|int
name|preInitMockListenerCount
init|=
literal|0
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// record current value prior to core initialization
comment|// so we can verify the correct number of instances later
comment|// NOTE: this won't work properly if concurrent tests run
comment|// in the same VM
name|preInitMockListenerCount
operator|=
name|MockEventListener
operator|.
name|getCreateCount
argument_list|()
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-querysender-noquery.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testListenerCreationCounts
specifier|public
name|void
name|testListenerCreationCounts
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of listeners created"
argument_list|,
name|EXPECTED_MOCK_LISTENER_INSTANCES
argument_list|,
name|MockEventListener
operator|.
name|getCreateCount
argument_list|()
operator|-
name|preInitMockListenerCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequestHandlerRegistry
specifier|public
name|void
name|testRequestHandlerRegistry
parameter_list|()
block|{
comment|// property values defined in build.xml
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|core
operator|.
name|firstSearcherListeners
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|core
operator|.
name|newSearcherListeners
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Determine that when the query lists are commented out of both new and
comment|// first searchers in the config, we don't throw an NPE
annotation|@
name|Test
DECL|method|testSearcherEvents
specifier|public
name|void
name|testSearcherEvents
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrEventListener
name|newSearcherListener
init|=
name|core
operator|.
name|newSearcherListeners
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not an instance of QuerySenderListener"
argument_list|,
name|newSearcherListener
operator|instanceof
name|QuerySenderListener
argument_list|)
expr_stmt|;
name|QuerySenderListener
name|qsl
init|=
operator|(
name|QuerySenderListener
operator|)
name|newSearcherListener
decl_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|currentSearcherRef
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|currentSearcher
init|=
name|currentSearcherRef
operator|.
name|get
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|dummy
init|=
literal|null
decl_stmt|;
name|qsl
operator|.
name|newSearcher
argument_list|(
name|currentSearcher
argument_list|,
name|dummy
argument_list|)
expr_stmt|;
comment|//test first Searcher (since param is null)
name|MockQuerySenderListenerReqHandler
name|mock
init|=
operator|(
name|MockQuerySenderListenerReqHandler
operator|)
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"mock"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Mock is null"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Req (firstsearcher) is not null"
argument_list|,
name|mock
operator|.
name|req
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|newSearcher
init|=
operator|new
name|SolrIndexSearcher
argument_list|(
name|core
argument_list|,
name|core
operator|.
name|getNewIndexDir
argument_list|()
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
argument_list|,
literal|"testQuerySenderNoQuery"
argument_list|,
literal|false
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|)
decl_stmt|;
name|qsl
operator|.
name|newSearcher
argument_list|(
name|newSearcher
argument_list|,
name|currentSearcher
argument_list|)
expr_stmt|;
comment|// get newSearcher.
name|assertNull
argument_list|(
literal|"Req (newsearcher) is not null"
argument_list|,
name|mock
operator|.
name|req
argument_list|)
expr_stmt|;
name|newSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|currentSearcherRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

