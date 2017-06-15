begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
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
name|params
operator|.
name|SpellingParams
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
name|junit
operator|.
name|AfterClass
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
DECL|class|SuggesterTest
specifier|public
class|class
name|SuggesterTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|/**    * Expected URI at which the given suggester will live.    */
DECL|field|requestUri
specifier|protected
name|String
name|requestUri
init|=
literal|"/suggest"
decl_stmt|;
comment|// TODO: fix this test to not require FSDirectory
DECL|field|savedFactory
specifier|static
name|String
name|savedFactory
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
name|savedFactory
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.DirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"org.apache.solr.core.MockFSDirectoryFactory"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-spellchecker.xml"
argument_list|,
literal|"schema-spellchecker.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
if|if
condition|(
name|savedFactory
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|savedFactory
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDocs
specifier|public
specifier|static
name|void
name|addDocs
parameter_list|()
block|{
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
literal|"acceptable accidentally accommodate acquire"
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
literal|"text"
argument_list|,
literal|"believe bellwether accommodate acquire"
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
literal|"text"
argument_list|,
literal|"cemetery changeable conscientious consensus acquire bellwether"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSuggestions
specifier|public
name|void
name|testSuggestions
parameter_list|()
throws|throws
name|Exception
block|{
name|addDocs
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// configured to do a rebuild on commit
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|requestUri
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[1][.='acquire']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[2][.='accommodate']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReload
specifier|public
name|void
name|testReload
parameter_list|()
throws|throws
name|Exception
block|{
name|addDocs
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|.
name|reload
argument_list|()
expr_stmt|;
comment|// wait until the new searcher is registered
name|waitForWarming
argument_list|()
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|requestUri
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[1][.='acquire']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[2][.='accommodate']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRebuild
specifier|public
name|void
name|testRebuild
parameter_list|()
throws|throws
name|Exception
block|{
name|addDocs
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|requestUri
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
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
literal|"text"
argument_list|,
literal|"actually"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|requestUri
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|)
expr_stmt|;
block|}
comment|// SOLR-2726
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|Suggester
name|suggester
init|=
operator|new
name|Suggester
argument_list|()
decl_stmt|;
name|NamedList
name|params
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"field"
argument_list|,
literal|"test_field"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"lookupImpl"
argument_list|,
literal|"org.apache.solr.spelling.suggest.tst.TSTLookupFactory"
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|init
argument_list|(
name|params
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|suggester
operator|.
name|getQueryAnalyzer
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

