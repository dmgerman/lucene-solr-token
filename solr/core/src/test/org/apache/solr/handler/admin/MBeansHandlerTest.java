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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStream
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
name|ContentStreamBase
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
name|Test
import|;
end_import

begin_class
DECL|class|MBeansHandlerTest
specifier|public
class|class
name|MBeansHandlerTest
extends|extends
name|SolrTestCaseJ4
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDiff
specifier|public
name|void
name|testDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/mbeans"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"xml"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|xml
argument_list|)
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/mbeans"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"xml"
argument_list|,
literal|"diff"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|xml
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|diff
init|=
name|SolrInfoMBeanHandler
operator|.
name|fromXML
argument_list|(
name|xml
argument_list|)
decl_stmt|;
comment|// The stats bean for SolrInfoMBeanHandler
name|NamedList
name|stats
init|=
operator|(
name|NamedList
operator|)
name|diff
operator|.
name|get
argument_list|(
literal|"ADMIN"
argument_list|)
operator|.
name|get
argument_list|(
literal|"/admin/mbeans"
argument_list|)
operator|.
name|get
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
comment|//System.out.println("stats:"+stats);
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Was: (?<was>[0-9]+), Now: (?<now>[0-9]+), Delta: (?<delta>[0-9]+)"
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|stats
operator|.
name|get
argument_list|(
literal|"ADMIN./admin/mbeans.requests"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Response did not match pattern: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|"delta"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|was
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|"was"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|now
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|"now"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|now
operator|-
name|was
argument_list|)
expr_stmt|;
name|xml
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/mbeans"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"key"
argument_list|,
literal|"org.apache.solr.handler.admin.CollectionsHandler"
argument_list|)
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|nl
init|=
name|SolrInfoMBeanHandler
operator|.
name|fromXML
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nl
operator|.
name|get
argument_list|(
literal|"ADMIN"
argument_list|)
operator|.
name|get
argument_list|(
literal|"org.apache.solr.handler.admin.CollectionsHandler"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testXMLDiffWithExternalEntity
specifier|public
name|void
name|testXMLDiffWithExternalEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|file
init|=
name|getFile
argument_list|(
literal|"mailing_lists.pdf"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<!DOCTYPE foo [<!ENTITY bar SYSTEM \""
operator|+
name|file
operator|+
literal|"\">]>\n"
operator|+
literal|"<response>\n"
operator|+
literal|"&bar;"
operator|+
literal|"<lst name=\"responseHeader\"><int name=\"status\">0</int><int name=\"QTime\">31</int></lst><lst name=\"solr-mbeans\"></lst>\n"
operator|+
literal|"</response>"
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|nl
init|=
name|SolrInfoMBeanHandler
operator|.
name|fromXML
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"external entity ignored properly"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

