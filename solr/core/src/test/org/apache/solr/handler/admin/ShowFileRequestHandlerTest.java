begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|ResponseParser
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
name|core
operator|.
name|SolrCore
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Extend SolrJettyTestBase because the SOLR-2535 bug only manifested itself when  * the {@link org.apache.solr.servlet.SolrDispatchFilter} is used, which isn't for embedded Solr use.  */
end_comment

begin_class
DECL|class|ShowFileRequestHandlerTest
specifier|public
class|class
name|ShowFileRequestHandlerTest
extends|extends
name|SolrJettyTestBase
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
name|createJetty
argument_list|(
name|legacyExampleCollection1SolrHome
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test404ViaHttp
specifier|public
name|void
name|test404ViaHttp
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrClient
name|client
init|=
name|getSolrClient
argument_list|()
decl_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"file"
argument_list|,
literal|"does-not-exist-404.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
try|try
block|{
name|QueryResponse
name|resp
init|=
name|request
operator|.
name|process
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"didn't get 404 exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|404
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|test404Locally
specifier|public
name|void
name|test404Locally
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we need to test that executing the handler directly does not
comment|// throw an exception, just sets the exception on the response.
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// bypass TestHarness since it will throw any exception found in the
comment|// response.
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/admin/file"
argument_list|)
argument_list|,
name|req
argument_list|(
literal|"file"
argument_list|,
literal|"does-not-exist-404.txt"
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"no exception in response"
argument_list|,
name|rsp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"wrong type of exception: "
operator|+
name|rsp
operator|.
name|getException
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|,
name|rsp
operator|.
name|getException
argument_list|()
operator|instanceof
name|SolrException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|404
argument_list|,
operator|(
operator|(
name|SolrException
operator|)
name|rsp
operator|.
name|getException
argument_list|()
operator|)
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertNull
argument_list|(
literal|"Should not have caught an exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDirList
specifier|public
name|void
name|testDirList
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrClient
name|client
init|=
name|getSolrClient
argument_list|()
decl_stmt|;
comment|//assertQ(req("qt", "/admin/file")); TODO file bug that SolrJettyTestBase extends SolrTestCaseJ4
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|request
operator|.
name|process
argument_list|(
name|client
argument_list|)
decl_stmt|;
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
name|assertTrue
argument_list|(
operator|(
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"files"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//some files
block|}
DECL|method|testGetRawFile
specifier|public
name|void
name|testGetRawFile
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrClient
name|client
init|=
name|getSolrClient
argument_list|()
decl_stmt|;
comment|//assertQ(req("qt", "/admin/file")); TODO file bug that SolrJettyTestBase extends SolrTestCaseJ4
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"file"
argument_list|,
literal|"managed-schema"
argument_list|)
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|readFile
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|request
operator|.
name|setResponseParser
argument_list|(
operator|new
name|ResponseParser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getWriterType
parameter_list|()
block|{
return|return
literal|"mock"
return|;
comment|//unfortunately this gets put onto params wt=mock but it apparently has no effect
block|}
annotation|@
name|Override
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|body
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|body
operator|.
name|read
argument_list|()
operator|>=
literal|0
condition|)
name|readFile
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO unimplemented"
argument_list|)
throw|;
comment|//TODO
block|}
block|}
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|//runs request
comment|//request.process(client); but we don't have a NamedList response
name|assertTrue
argument_list|(
name|readFile
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

