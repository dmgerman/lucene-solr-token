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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_class
DECL|class|TestExtendedDismaxParser
specifier|public
class|class
name|TestExtendedDismaxParser
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema12.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
comment|// public String getCoreName() { return "collection1"; }
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|// test the edismax query parser based on the dismax parser
DECL|method|testFocusQueryParser
specifier|public
name|void
name|testFocusQueryParser
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"Tool"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"Obnoxious"
argument_list|,
literal|"name"
argument_list|,
literal|"Zapp Brannigan"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"title"
argument_list|,
literal|"Democratic Order op Planets"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"Tool"
argument_list|,
literal|"name"
argument_list|,
literal|"The Zapper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"45"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"Chauvinist"
argument_list|,
literal|"title"
argument_list|,
literal|"25 star General"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"46"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"Obnoxious"
argument_list|,
literal|"subject"
argument_list|,
literal|"Defeated the pacifists op the Gandhi nebula"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"47"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"Pig"
argument_list|,
literal|"text"
argument_list|,
literal|"line up and fly directly at the enemy death cannons, clogging them with wreckage!"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"48"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"this has gigabyte potential"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"49"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"start the big apple end"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"-100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"50"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"start new big city end"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|allq
init|=
literal|"id:[42 TO 50]"
decl_stmt|;
name|String
name|allr
init|=
literal|"*[count(//doc)=9]"
decl_stmt|;
name|String
name|oner
init|=
literal|"*[count(//doc)=1]"
decl_stmt|;
name|String
name|twor
init|=
literal|"*[count(//doc)=2]"
decl_stmt|;
name|String
name|nor
init|=
literal|"*[count(//doc)=0]"
decl_stmt|;
name|assertQ
argument_list|(
literal|"standard request handler returns all matches"
argument_list|,
name|req
argument_list|(
name|allq
argument_list|)
argument_list|,
name|allr
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"edismax query parser returns all matches"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
name|allq
argument_list|,
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
argument_list|,
name|allr
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"trait_ss"
argument_list|,
literal|"q"
argument_list|,
literal|"Tool"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
comment|// test that field types that aren't applicable don't cause an exception to be thrown
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"trait_ss foo_i foo_f foo_dt foo_l foo_d foo_b"
argument_list|,
literal|"q"
argument_list|,
literal|"Tool"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
comment|// test that numeric field types can be queried
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"q"
argument_list|,
literal|"foo_i:100"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
comment|// test that numeric field types can be queried
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"q"
argument_list|,
literal|"foo_i:-100"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
comment|// test that numeric field types can be queried  via qf
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"text_sw foo_i"
argument_list|,
literal|"q"
argument_list|,
literal|"100"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"qf defaults to defaultSearchField"
argument_list|,
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"q"
argument_list|,
literal|"op"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"op"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"Order op"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"Order AND op"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"Order and op"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"+Order op"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"Order OR op"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"Order or op"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
name|allr
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"star OR (-star)"
argument_list|)
argument_list|,
name|allr
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"name title subject text"
argument_list|,
literal|"q"
argument_list|,
literal|"id:42 OR (-id:42)"
argument_list|)
argument_list|,
name|allr
argument_list|)
expr_stmt|;
comment|// test that basic synonyms work
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"q"
argument_list|,
literal|"GB"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
comment|// test for stopword removal in main query part
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"q"
argument_list|,
literal|"the big"
argument_list|)
argument_list|,
name|twor
argument_list|)
expr_stmt|;
comment|// test for stopwords not removed
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|,
literal|"qf"
argument_list|,
literal|"text_sw"
argument_list|,
literal|"stopwords"
argument_list|,
literal|"false"
argument_list|,
literal|"q"
argument_list|,
literal|"the big"
argument_list|)
argument_list|,
name|oner
argument_list|)
expr_stmt|;
comment|/** stopword removal in conjunction with multi-word synonyms at query time      * break this test.      // multi-word synonyms      // remove id:50 which contans the false match           assertQ(req("defType", "edismax", "qf", "text_t", "indent","true", "debugQuery","true",            "q","-id:50 nyc"), oner     );     **/
comment|/*** these fail because multi-word synonyms are being used at query time     // this will incorrectly match "new big city"     assertQ(req("defType", "edismax", "qf", "id title",            "q","nyc"), oner     );      // this will incorrectly match "new big city"     assertQ(req("defType", "edismax", "qf", "title",            "q","the big apple"), nor     );     ***/
block|}
block|}
end_class

end_unit

