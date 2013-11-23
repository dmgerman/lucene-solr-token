begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|spelling
operator|.
name|suggest
operator|.
name|SuggesterParams
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
DECL|class|SuggestComponentTest
specifier|public
class|class
name|SuggestComponentTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|rh
specifier|static
name|String
name|rh
init|=
literal|"/suggest"
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
name|initCore
argument_list|(
literal|"solrconfig-suggestercomponent.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
comment|// id, cat, price, weight
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"cat"
argument_list|,
literal|"This is a title"
argument_list|,
literal|"price"
argument_list|,
literal|"5"
argument_list|,
literal|"weight"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"cat"
argument_list|,
literal|"This is another title"
argument_list|,
literal|"price"
argument_list|,
literal|"10"
argument_list|,
literal|"weight"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"cat"
argument_list|,
literal|"Yet another"
argument_list|,
literal|"price"
argument_list|,
literal|"15"
argument_list|,
literal|"weight"
argument_list|,
literal|"10"
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
literal|"cat"
argument_list|,
literal|"Yet another title"
argument_list|,
literal|"price"
argument_list|,
literal|"20"
argument_list|,
literal|"weight"
argument_list|,
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"cat"
argument_list|,
literal|"suggestions for suggest"
argument_list|,
literal|"price"
argument_list|,
literal|"25"
argument_list|,
literal|"weight"
argument_list|,
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"cat"
argument_list|,
literal|"Red fox"
argument_list|,
literal|"price"
argument_list|,
literal|"30"
argument_list|,
literal|"weight"
argument_list|,
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"cat"
argument_list|,
literal|"Rad fox"
argument_list|,
literal|"price"
argument_list|,
literal|"35"
argument_list|,
literal|"weight"
argument_list|,
literal|"30"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"cat"
argument_list|,
literal|"example data"
argument_list|,
literal|"price"
argument_list|,
literal|"40"
argument_list|,
literal|"weight"
argument_list|,
literal|"30"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"cat"
argument_list|,
literal|"example inputdata"
argument_list|,
literal|"price"
argument_list|,
literal|"45"
argument_list|,
literal|"weight"
argument_list|,
literal|"30"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"cat"
argument_list|,
literal|"blah in blah"
argument_list|,
literal|"price"
argument_list|,
literal|"50"
argument_list|,
literal|"weight"
argument_list|,
literal|"40"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"cat"
argument_list|,
literal|"another blah in blah"
argument_list|,
literal|"price"
argument_list|,
literal|"55"
argument_list|,
literal|"weight"
argument_list|,
literal|"40"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|commit
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
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
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|optimize
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|commit
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDocumentBased
specifier|public
name|void
name|testDocumentBased
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"suggest_fuzzy_doc_dict"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD
argument_list|,
literal|"true"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_Q
argument_list|,
literal|"exampel"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][1]/str[@name='term'][.='example inputdata']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][1]/long[@name='weight'][.='45']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][2]/str[@name='term'][.='example data']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][2]/long[@name='weight'][.='40']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"suggest_fuzzy_doc_dict"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD
argument_list|,
literal|"true"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_Q
argument_list|,
literal|"Rad"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='Rad']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='Rad']/lst[@name='suggestion'][1]/str[@name='term'][.='Rad fox']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='Rad']/lst[@name='suggestion'][1]/long[@name='weight'][.='35']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='Rad']/lst[@name='suggestion'][2]/str[@name='term'][.='Red fox']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='Rad']/lst[@name='suggestion'][2]/long[@name='weight'][.='30']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExpressionBased
specifier|public
name|void
name|testExpressionBased
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"suggest_fuzzy_doc_expr_dict"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD
argument_list|,
literal|"true"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_Q
argument_list|,
literal|"exampel"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][1]/str[@name='term'][.='example inputdata']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][1]/long[@name='weight'][.='120']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][2]/str[@name='term'][.='example data']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='exampel']/lst[@name='suggestion'][2]/long[@name='weight'][.='110']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileBased
specifier|public
name|void
name|testFileBased
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"suggest_fuzzy_file_based"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD
argument_list|,
literal|"true"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_Q
argument_list|,
literal|"chn"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='chn']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='chn']/lst[@name='suggestion'][1]/str[@name='term'][.='chance']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='chn']/lst[@name='suggestion'][1]/long[@name='weight'][.='1']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='chn']/lst[@name='suggestion'][2]/str[@name='term'][.='change']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='suggestions']/lst[@name='chn']/lst[@name='suggestion'][2]/long[@name='weight'][.='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

