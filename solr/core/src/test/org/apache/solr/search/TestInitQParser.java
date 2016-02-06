begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|junit
operator|.
name|Before
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
comment|/**  * Checking QParser plugin initialization, failing with NPE during Solr startup.  * Ensures that query is working by registered in solrconfig.xml "fail" query parser.  */
end_comment

begin_class
DECL|class|TestInitQParser
specifier|public
class|class
name|TestInitQParser
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|createIndex
specifier|private
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
name|String
name|v
decl_stmt|;
name|v
operator|=
literal|"how now brown cow"
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
name|v
argument_list|,
literal|"text_np"
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|v
operator|=
literal|"now cow"
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
name|v
argument_list|,
literal|"text_np"
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"a ' \" \\ {! ) } ( { z"
argument_list|)
argument_list|)
expr_stmt|;
comment|// A value filled with special chars
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"qqq_s"
argument_list|,
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"www_s"
argument_list|,
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"eee_s"
argument_list|,
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"eee_s"
argument_list|,
literal|"'balance'"
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
name|Override
annotation|@
name|Before
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig-query-parser-init.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryParserInit
specifier|public
name|void
name|testQueryParserInit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// should query using registered fail (defType=fail) QParser and match only one doc
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"defType"
argument_list|,
literal|"fail"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

