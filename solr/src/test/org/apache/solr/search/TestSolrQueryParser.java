begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
DECL|method|testReturnFields
specifier|public
name|void
name|testReturnFields
parameter_list|()
block|{
name|ReturnFields
name|rf
init|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
operator|instanceof
name|ScoreAugmenter
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsAllFields
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rf
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"_explain_"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsScore
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"_explain_"
argument_list|,
name|rf
operator|.
name|getTransformer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that we want wildcards
name|rf
operator|=
operator|new
name|ReturnFields
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,aaa*,*bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"aaaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"xxxbbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rf
operator|.
name|wantsField
argument_list|(
literal|"bb"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

