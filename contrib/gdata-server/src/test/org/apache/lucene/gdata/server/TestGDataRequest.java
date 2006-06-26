begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|gdata
operator|.
name|server
operator|.
name|GDataRequest
operator|.
name|GDataRequestType
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
name|gdata
operator|.
name|server
operator|.
name|GDataRequest
operator|.
name|OutputFormat
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedService
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|RegistryException
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
name|gdata
operator|.
name|utils
operator|.
name|ProvidedServiceStub
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
name|gdata
operator|.
name|utils
operator|.
name|StorageStub
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|MockControl
import|;
end_import

begin_comment
comment|/**   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|TestGDataRequest
specifier|public
class|class
name|TestGDataRequest
extends|extends
name|TestCase
block|{
DECL|field|request
specifier|private
name|HttpServletRequest
name|request
decl_stmt|;
DECL|field|control
specifier|private
name|MockControl
name|control
decl_stmt|;
DECL|field|feedRequest
specifier|private
name|GDataRequest
name|feedRequest
decl_stmt|;
static|static
block|{
try|try
block|{
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|registerComponent
argument_list|(
name|StorageStub
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RegistryException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ProvidedService
name|configurator
init|=
operator|new
name|ProvidedServiceStub
argument_list|()
decl_stmt|;
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|registerService
argument_list|(
name|configurator
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|=
name|MockControl
operator|.
name|createControl
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
operator|(
name|HttpServletRequest
operator|)
name|this
operator|.
name|control
operator|.
name|getMock
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|=
operator|new
name|GDataRequest
argument_list|(
name|this
operator|.
name|request
argument_list|,
name|GDataRequestType
operator|.
name|GET
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testConstructor
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
try|try
block|{
operator|new
name|GDataRequest
argument_list|(
literal|null
argument_list|,
name|GDataRequestType
operator|.
name|GET
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalArgumentException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
try|try
block|{
operator|new
name|GDataRequest
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalArgumentException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
try|try
block|{
operator|new
name|GDataRequest
argument_list|(
name|this
operator|.
name|request
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalArgumentException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
DECL|method|testGetFeedId
specifier|public
name|void
name|testGetFeedId
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed/1/1"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"feedID"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getFeedId
argument_list|()
argument_list|,
literal|"feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmptyPathInfo
specifier|public
name|void
name|testEmptyPathInfo
parameter_list|()
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"FeedRequestException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataRequestException
name|e
parameter_list|)
block|{
comment|// expected
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"FeedRequestException expected"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetFeedIdWithoutEntry
specifier|public
name|void
name|testGetFeedIdWithoutEntry
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"feedID"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getFeedId
argument_list|()
argument_list|,
literal|"feed"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetEntyId
specifier|public
name|void
name|testGetEntyId
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed/1/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"entryid"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getEntryId
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"feedId"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getFeedId
argument_list|()
argument_list|,
literal|"feed"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"entryid"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getEntryVersion
argument_list|()
argument_list|,
literal|"15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testSetResponseFormatAtom
specifier|public
name|void
name|testSetResponseFormatAtom
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|"atom"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ResponseFromat Atom"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|,
name|OutputFormat
operator|.
name|ATOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testSetResponseFormatRSS
specifier|public
name|void
name|testSetResponseFormatRSS
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|"rss"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ResponseFromat RSS"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|,
name|OutputFormat
operator|.
name|RSS
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testSetResponseFormatKeepAtom
specifier|public
name|void
name|testSetResponseFormatKeepAtom
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|"fooBar"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ResponseFromat Atom"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|,
name|OutputFormat
operator|.
name|ATOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testSetResponseFormatNull
specifier|public
name|void
name|testSetResponseFormatNull
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ResponseFromat Atom"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|,
name|OutputFormat
operator|.
name|ATOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetItemsPerPage
specifier|public
name|void
name|testGetItemsPerPage
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default value 25"
argument_list|,
literal|25
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getItemsPerPage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|"24"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"24 results"
argument_list|,
literal|24
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getItemsPerPage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|"-1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"25 results"
argument_list|,
literal|25
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getItemsPerPage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|"helloworld"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"25 results"
argument_list|,
literal|25
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getItemsPerPage
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetSelfId
specifier|public
name|void
name|testGetSelfId
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|String
name|host
init|=
literal|"www.apache.org"
decl_stmt|;
name|String
name|feedAndEntryID
init|=
literal|"/feed/entryid"
decl_stmt|;
name|String
name|queryString
init|=
literal|"max-results=25"
decl_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Host"
argument_list|)
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|,
literal|"/host/feed/entryId/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed/entryId/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|"25"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|String
name|selfID
init|=
literal|"http://"
operator|+
name|host
operator|+
literal|"/host/feed/entryId/15?"
operator|+
name|queryString
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Self ID"
argument_list|,
name|selfID
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getSelfId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|queryString
operator|=
literal|"alt=rss&max-results=25"
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Host"
argument_list|)
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|,
literal|"/host/feed/entryId/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed/entryId/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|"25"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|selfID
operator|=
literal|"http://"
operator|+
name|host
operator|+
literal|"/host/feed/entryId/15?"
operator|+
name|queryString
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Self ID"
argument_list|,
name|selfID
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getSelfId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|queryString
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Host"
argument_list|)
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|,
literal|"/host/feed/entryId/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed/entryId/15"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|selfID
operator|=
literal|"http://"
operator|+
name|host
operator|+
literal|"/host/feed/entryId/15"
operator|+
literal|"?max-results=25"
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Self ID"
argument_list|,
name|selfID
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getSelfId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetQueryString
specifier|public
name|void
name|testGetQueryString
parameter_list|()
block|{
name|String
name|maxResults
init|=
literal|"max-results=25"
decl_stmt|;
name|String
name|queryString
init|=
literal|"?"
operator|+
name|maxResults
decl_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|"25"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|queryString
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// test no result defined
name|queryString
operator|=
literal|"?alt=rss"
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|queryString
operator|+
literal|"&"
operator|+
name|maxResults
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//         test no result defined&& query == null
name|queryString
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"max-results"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|maxResults
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testIsFeedRequest
specifier|public
name|void
name|testIsFeedRequest
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|String
name|host
init|=
literal|"www.apache.org"
decl_stmt|;
name|String
name|feedAndEntryID
init|=
literal|"/feed"
decl_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Host"
argument_list|)
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|,
literal|"/host/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
literal|"/feed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|feedRequest
operator|.
name|isFeedRequested
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|this
operator|.
name|feedRequest
operator|.
name|isEntryRequested
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
name|host
operator|=
literal|"www.apache.org"
expr_stmt|;
name|feedAndEntryID
operator|=
literal|"/feed/1"
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Host"
argument_list|)
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|,
literal|"/host/feed/1"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|,
name|feedAndEntryID
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
literal|"alt"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|this
operator|.
name|feedRequest
operator|.
name|isFeedRequested
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|this
operator|.
name|feedRequest
operator|.
name|isEntryRequested
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|testgetAuthToken
specifier|public
name|void
name|testgetAuthToken
parameter_list|()
block|{
name|this
operator|.
name|control
operator|.
name|expectAndDefaultReturn
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Authentication"
argument_list|)
argument_list|,
literal|"GoogleLogin auth=bla"
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|replay
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bla"
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getAuthToken
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|control
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetNextId
specifier|public
name|void
name|testGetNextId
parameter_list|()
throws|throws
name|GDataRequestException
block|{
comment|//        String host = "www.apache.org";
comment|//        String feedAndEntryID = "/feed/entryid";
comment|//        String queryString = "?max-results=25";
comment|//        String startIndex = "&start-index=26";
comment|//        this.control.expectAndDefaultReturn(this.request.getHeader("Host"),host);
comment|//        this.control.expectAndDefaultReturn(this.request.getRequestURI(),"/feed/");
comment|//        this.control.expectAndDefaultReturn(this.request.getPathInfo(),"/feed/");
comment|//        this.control.expectAndReturn(this.request.getParameter("max-results"),"25",2);
comment|//        this.control.expectAndReturn(this.request.getParameter("start-index"),null);
comment|//        this.control.expectAndDefaultReturn(this.request.getParameter("alt"),
comment|//                null);
comment|//        this.control.expectAndDefaultReturn(this.request.getQueryString(),
comment|//                queryString);
comment|//        this.control.replay();
comment|//        this.feedRequest.initializeRequest();
comment|//        String nextID = "http://"+host+"/feed/"+queryString+startIndex;
comment|//
comment|//        assertEquals("Next ID",nextID,this.feedRequest.getNextId());
comment|//        this.control.reset();
comment|//        queryString = "?alt=rss&max-results=25";
comment|//
comment|//        this.control.expectAndDefaultReturn(this.request.getHeader("Host"),host);
comment|//        this.control.expectAndDefaultReturn(this.request.getRequestURI(),"/feed/");
comment|//        this.control.expectAndDefaultReturn(this.request.getPathInfo(),"/feed/");
comment|//        this.control.expectAndReturn(this.request.getParameter("max-results"),"25",2);
comment|//        this.control.expectAndReturn(this.request.getParameter("start-index"),"26",2);
comment|//        this.control.expectAndDefaultReturn(this.request.getParameter("alt"),
comment|//                null);
comment|//        this.control.expectAndDefaultReturn(this.request.getQueryString(),
comment|//                queryString+startIndex);
comment|//        Enumeration e =
comment|//        this.control.expectAndDefaultReturn(this.request.getParameterNames(),)
comment|//
comment|//
comment|//        this.control.replay();
comment|//        this.feedRequest.initializeRequest();
comment|//        startIndex = "&start-index=51";
comment|//        nextID = "http://"+host+"/feed"+queryString+startIndex;
comment|//
comment|//        assertEquals("Next ID 51",nextID,this.feedRequest.getNextId());
comment|//        this.control.reset();
comment|//
comment|//        queryString = "";
comment|//        this.control.expectAndDefaultReturn(this.request.getHeader("Host"),host);
comment|//        this.control.expectAndDefaultReturn(this.request.getPathInfo(),"/feed/entryId/15");
comment|//        this.control.expectAndDefaultReturn(this.request.getParameter("max-results"),null);
comment|//        this.control.expectAndDefaultReturn(this.request.getParameter("alt"),
comment|//                null);
comment|//        this.control.expectAndDefaultReturn(this.request.getQueryString(),
comment|//                null);
comment|//        this.control.replay();
comment|//        this.feedRequest.initializeRequest();
comment|//        String selfID = "http://"+host+"/feed"+"?max-results=25";
comment|//
comment|//        assertEquals("Self ID",selfID,this.feedRequest.getSelfId());
comment|//        this.control.reset();
block|}
block|}
end_class

end_unit

