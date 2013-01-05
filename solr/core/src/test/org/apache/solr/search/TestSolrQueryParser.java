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
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
operator|.
name|ScoreAugmenter
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
DECL|class|TestSolrQueryParser
specifier|public
class|class
name|TestSolrQueryParser
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
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
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
name|Test
DECL|method|testPhrase
specifier|public
name|void
name|testPhrase
parameter_list|()
block|{
comment|// should generate a phrase of "now cow" and match only one doc
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:now-cow"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// should generate a query of (now OR cow) and match both docs
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text_np:now-cow"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocalParamsInQP
specifier|public
name|void
name|testLocalParamsInQP
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=text v=$qq} wsx"
argument_list|,
literal|"qq"
argument_list|,
literal|"now"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=text v=$qq} wsx"
argument_list|,
literal|"qq"
argument_list|,
literal|"nomatch"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=text}now wsx"
argument_list|,
literal|"qq"
argument_list|,
literal|"now"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=foo_s v='a \\' \" \\\\ {! ) } ( { z'} wsx"
argument_list|)
comment|// single quote escaping
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!term f=foo_s v=\"a ' \\\" \\\\ {! ) } ( { z\"} wsx"
argument_list|)
comment|// double quote escaping
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
comment|// double-join to test back-to-back local params
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"qaz {!join from=www_s to=eee_s}{!join from=qqq_s to=www_s}id:10"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/id=='12'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSolr4121
specifier|public
name|void
name|testSolr4121
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This query doesn't match anything, testing
comment|// to make sure that SOLR-4121 is not a problem.
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"eee_s:'balance'"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

