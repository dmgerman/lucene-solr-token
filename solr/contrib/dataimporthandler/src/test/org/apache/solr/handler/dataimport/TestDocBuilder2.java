begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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
name|Map
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
name|LocalSolrQueryRequest
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
name|Ignore
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for DocBuilder using the test harness  *</p>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestDocBuilder2
specifier|public
class|class
name|TestDocBuilder2
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
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
name|initCore
argument_list|(
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSingleEntity
specifier|public
name|void
name|testSingleEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|loadDataConfig
argument_list|(
literal|"single-entity-data-config.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor processAdd was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|processAddCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor processCommit was not callled"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|processCommitCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor finish was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSingleEntity_CaseInsensitive
specifier|public
name|void
name|testSingleEntity_CaseInsensitive
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desC"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigWithCaseInsensitiveFields
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Start event listener was not called"
argument_list|,
name|StartEventListener
operator|.
name|executed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"End event listener was not called"
argument_list|,
name|EndEventListener
operator|.
name|executed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor processAdd was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|processAddCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor finish was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorHandler
specifier|public
name|void
name|testErrorHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"FORCE_ERROR"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigWithErrorHandler
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Error event listener was not called"
argument_list|,
name|ErrorEventListener
operator|.
name|executed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ErrorEventListener
operator|.
name|lastException
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ForcedException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testDynamicFields
specifier|public
name|void
name|testDynamicFields
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigWithDynamicTransformer
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"dynamic_s:test"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testRequestParamsAsVariable
specifier|public
name|void
name|testRequestParamsAsVariable
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
literal|"desc"
argument_list|,
literal|"ApacheSolr"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from books where category='search'"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"debug"
argument_list|,
literal|"on"
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"category"
argument_list|,
literal|"search"
argument_list|,
literal|"dataConfig"
argument_list|,
name|requestParamAsVariable
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:ApacheSolr"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testRequestParamsAsFieldName
specifier|public
name|void
name|testRequestParamsAsFieldName
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"mypk"
argument_list|,
literal|"101"
argument_list|,
literal|"text"
argument_list|,
literal|"ApacheSolr"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"debug"
argument_list|,
literal|"on"
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"mypk"
argument_list|,
literal|"id"
argument_list|,
literal|"text"
argument_list|,
literal|"desc"
argument_list|,
literal|"dataConfig"
argument_list|,
name|dataConfigWithTemplatizedFieldNames
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testContext
specifier|public
name|void
name|testContext
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|loadDataConfig
argument_list|(
literal|"data-config-with-transformer.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSkipDoc
specifier|public
name|void
name|testSkipDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
name|DocBuilder
operator|.
name|SKIP_DOC
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigWithDynamicTransformer
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSkipRow
specifier|public
name|void
name|testSkipRow
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
name|DocBuilder
operator|.
name|SKIP_ROW
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigWithDynamicTransformer
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"name_s"
argument_list|,
literal|"abcd"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"3"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"name_s"
argument_list|,
literal|"xyz"
argument_list|,
name|DocBuilder
operator|.
name|SKIP_ROW
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"4"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigWithTwoEntities
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:3"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:4"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"name_s:abcd"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"name_s:xyz"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testStopTransform
specifier|public
name|void
name|testStopTransform
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
literal|"$stopTransform"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigForSkipTransform
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"name_s:xyz"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testDeleteDocs
specifier|public
name|void
name|testDeleteDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
name|DocBuilder
operator|.
name|DELETE_DOC_BY_ID
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigForSkipTransform
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:3"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor processDelete was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|processDeleteCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor finish was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|,
name|DocBuilder
operator|.
name|DELETE_DOC_BY_QUERY
argument_list|,
literal|"desc:one"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigForSkipTransform
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:3"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor processDelete was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|processDeleteCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor finish was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|rows
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
name|DocBuilder
operator|.
name|DELETE_DOC_BY_ID
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigForSkipTransform
argument_list|,
name|createMap
argument_list|(
literal|"clean"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:3"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor processDelete was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|processDeleteCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Update request processor finish was not called"
argument_list|,
name|TestUpdateRequestProcessor
operator|.
name|finishCalled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Fix Me. See SOLR-4103."
argument_list|)
DECL|method|testFileListEntityProcessor_lastIndexTime
specifier|public
name|void
name|testFileListEntityProcessor_lastIndexTime
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpdir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|"tmp"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|createMap
argument_list|(
literal|"baseDir"
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigFileList
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
comment|// Add a new file after a full index is done
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"t.xml"
argument_list|,
literal|"t.xml"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfigFileList
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// we should find only 1 because by default clean=true is passed
comment|// and this particular import should find only one file t.xml
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|class|MockTransformer
specifier|public
specifier|static
class|class
name|MockTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Context gave incorrect data source"
argument_list|,
name|context
operator|.
name|getDataSource
argument_list|(
literal|"mockDs"
argument_list|)
operator|instanceof
name|MockDataSource2
argument_list|)
expr_stmt|;
return|return
name|row
return|;
block|}
block|}
DECL|class|AddDynamicFieldTransformer
specifier|public
specifier|static
class|class
name|AddDynamicFieldTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
comment|// Add a dynamic field
name|row
operator|.
name|put
argument_list|(
literal|"dynamic_s"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
return|return
name|row
return|;
block|}
block|}
DECL|class|ForcedExceptionTransformer
specifier|public
specifier|static
class|class
name|ForcedExceptionTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"ForcedException"
argument_list|)
throw|;
block|}
block|}
DECL|class|MockDataSource2
specifier|public
specifier|static
class|class
name|MockDataSource2
extends|extends
name|MockDataSource
block|{    }
DECL|class|StartEventListener
specifier|public
specifier|static
class|class
name|StartEventListener
implements|implements
name|EventListener
block|{
DECL|field|executed
specifier|public
specifier|static
name|boolean
name|executed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|onEvent
specifier|public
name|void
name|onEvent
parameter_list|(
name|Context
name|ctx
parameter_list|)
block|{
name|executed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|class|EndEventListener
specifier|public
specifier|static
class|class
name|EndEventListener
implements|implements
name|EventListener
block|{
DECL|field|executed
specifier|public
specifier|static
name|boolean
name|executed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|onEvent
specifier|public
name|void
name|onEvent
parameter_list|(
name|Context
name|ctx
parameter_list|)
block|{
name|executed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|class|ErrorEventListener
specifier|public
specifier|static
class|class
name|ErrorEventListener
implements|implements
name|EventListener
block|{
DECL|field|executed
specifier|public
specifier|static
name|boolean
name|executed
init|=
literal|false
decl_stmt|;
DECL|field|lastException
specifier|public
specifier|static
name|Exception
name|lastException
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|onEvent
specifier|public
name|void
name|onEvent
parameter_list|(
name|Context
name|ctx
parameter_list|)
block|{
name|executed
operator|=
literal|true
expr_stmt|;
name|lastException
operator|=
operator|(
operator|(
name|ContextImpl
operator|)
name|ctx
operator|)
operator|.
name|lastException
expr_stmt|;
block|}
block|}
DECL|field|requestParamAsVariable
specifier|private
specifier|final
name|String
name|requestParamAsVariable
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<dataSource type=\"MockDataSource\" />\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from books where category='${dataimporter.request.category}'\">\n"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigWithDynamicTransformer
specifier|private
specifier|final
name|String
name|dataConfigWithDynamicTransformer
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from x\""
operator|+
literal|"                transformer=\"TestDocBuilder2$AddDynamicFieldTransformer\">\n"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigForSkipTransform
specifier|private
specifier|final
name|String
name|dataConfigForSkipTransform
init|=
literal|"<dataConfig><dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from x\""
operator|+
literal|"                transformer=\"TemplateTransformer\">\n"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"<field column=\"name_s\" template=\"xyz\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigWithTwoEntities
specifier|private
specifier|final
name|String
name|dataConfigWithTwoEntities
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from x\">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"<entity name=\"authors\" query=\"${books.id}\">"
operator|+
literal|"<field column=\"name_s\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigWithCaseInsensitiveFields
specifier|private
specifier|final
name|String
name|dataConfigWithCaseInsensitiveFields
init|=
literal|"<dataConfig><dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document onImportStart=\"TestDocBuilder2$StartEventListener\" onImportEnd=\"TestDocBuilder2$EndEventListener\">\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from x\">\n"
operator|+
literal|"<field column=\"ID\" />\n"
operator|+
literal|"<field column=\"Desc\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigWithErrorHandler
specifier|private
specifier|final
name|String
name|dataConfigWithErrorHandler
init|=
literal|"<dataConfig><dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document onError=\"TestDocBuilder2$ErrorEventListener\">\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from x\" transformer=\"TestDocBuilder2$ForcedExceptionTransformer\">\n"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<field column=\"FORCE_ERROR\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigWithTemplatizedFieldNames
specifier|private
specifier|final
name|String
name|dataConfigWithTemplatizedFieldNames
init|=
literal|"<dataConfig><dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"books\" query=\"select * from x\">\n"
operator|+
literal|"<field column=\"mypk\" name=\"${dih.request.mypk}\" />\n"
operator|+
literal|"<field column=\"text\" name=\"${dih.request.text}\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|dataConfigFileList
specifier|private
specifier|final
name|String
name|dataConfigFileList
init|=
literal|"<dataConfig>\n"
operator|+
literal|"\t<document>\n"
operator|+
literal|"\t\t<entity name=\"x\" processor=\"FileListEntityProcessor\" \n"
operator|+
literal|"\t\t\t\tfileName=\".*\" newerThan=\"${dih.last_index_time}\" \n"
operator|+
literal|"\t\t\t\tbaseDir=\"${dih.request.baseDir}\" transformer=\"TemplateTransformer\">\n"
operator|+
literal|"\t\t\t<field column=\"id\" template=\"${x.file}\" />\n"
operator|+
literal|"\t\t</entity>\n"
operator|+
literal|"\t</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
block|}
end_class

end_unit

