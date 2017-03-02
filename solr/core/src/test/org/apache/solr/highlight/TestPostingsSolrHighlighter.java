begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|handler
operator|.
name|component
operator|.
name|HighlightComponent
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
name|schema
operator|.
name|IndexSchema
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

begin_comment
comment|/** simple tests for PostingsSolrHighlighter */
end_comment

begin_class
DECL|class|TestPostingsSolrHighlighter
specifier|public
class|class
name|TestPostingsSolrHighlighter
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
literal|"solrconfig-postingshighlight.xml"
argument_list|,
literal|"schema-postingshighlight.xml"
argument_list|)
expr_stmt|;
comment|// test our config is sane, just to be sure:
comment|// postingshighlighter should be used
name|SolrHighlighter
name|highlighter
init|=
name|HighlightComponent
operator|.
name|getHighlighter
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"wrong highlighter: "
operator|+
name|highlighter
operator|.
name|getClass
argument_list|()
argument_list|,
name|highlighter
operator|instanceof
name|PostingsSolrHighlighter
argument_list|)
expr_stmt|;
comment|// 'text' and 'text3' should have offsets, 'text2' should not
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"text"
argument_list|)
operator|.
name|storeOffsetsWithPositions
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"text3"
argument_list|)
operator|.
name|storeOffsetsWithPositions
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"text2"
argument_list|)
operator|.
name|storeOffsetsWithPositions
argument_list|()
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
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"document one"
argument_list|,
literal|"text2"
argument_list|,
literal|"document one"
argument_list|,
literal|"text3"
argument_list|,
literal|"crappy document"
argument_list|,
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"second document"
argument_list|,
literal|"text2"
argument_list|,
literal|"second document"
argument_list|,
literal|"text3"
argument_list|,
literal|"crappier document"
argument_list|,
literal|"id"
argument_list|,
literal|"102"
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
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"simplest test"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.method"
argument_list|,
literal|"postings"
argument_list|)
argument_list|,
comment|// test hl.method is happy too
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='<em>document</em> one'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second<em>document</em>'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPagination
specifier|public
name|void
name|testPagination
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"pagination test"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"start"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=1"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second<em>document</em>'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptySnippet
specifier|public
name|void
name|testEmptySnippet
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"null snippet test"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:one OR *:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='document<em>one</em>'"
argument_list|,
literal|"count(//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/*)=0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultSummary
specifier|public
name|void
name|testDefaultSummary
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"null snippet test"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:one OR *:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.defaultSummary"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='document<em>one</em>'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second document'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDifferentField
specifier|public
name|void
name|testDifferentField
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"highlighting text3"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text3:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
literal|"text3"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text3']/str='crappy<em>document</em>'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text3']/str='crappier<em>document</em>'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoFields
specifier|public
name|void
name|testTwoFields
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"highlighting text and text3"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document text3:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
literal|"text,text3"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='<em>document</em> one'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text3']/str='crappy<em>document</em>'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second<em>document</em>'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text3']/str='crappier<em>document</em>'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMisconfiguredField
specifier|public
name|void
name|testMisconfiguredField
parameter_list|()
block|{
name|ignoreException
argument_list|(
literal|"was indexed without offsets"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertQ
argument_list|(
literal|"should fail, has no offsets"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text2:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
literal|"text2"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
comment|// expected
block|}
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|testTags
specifier|public
name|void
name|testTags
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"different pre/post tags"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.tag.pre"
argument_list|,
literal|"["
argument_list|,
literal|"hl.tag.post"
argument_list|,
literal|"]"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='[document] one'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second [document]'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTagsPerField
specifier|public
name|void
name|testTagsPerField
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"highlighting text and text3"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document text3:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
literal|"text,text3"
argument_list|,
literal|"f.text3.hl.tag.pre"
argument_list|,
literal|"["
argument_list|,
literal|"f.text3.hl.tag.post"
argument_list|,
literal|"]"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='<em>document</em> one'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text3']/str='crappy [document]'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second<em>document</em>'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text3']/str='crappier [document]'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBreakIterator
specifier|public
name|void
name|testBreakIterator
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"different breakiterator"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.bs.type"
argument_list|,
literal|"WORD"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='<em>document</em>'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='<em>document</em>'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBreakIterator2
specifier|public
name|void
name|testBreakIterator2
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"Document one has a first sentence. Document two has a second sentence."
argument_list|,
literal|"id"
argument_list|,
literal|"103"
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
literal|"different breakiterator"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.bs.type"
argument_list|,
literal|"WHOLE"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='103']/arr[@name='text']/str='<em>Document</em> one has a first sentence.<em>Document</em> two has a second sentence.'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBreakIterator3
specifier|public
name|void
name|testBreakIterator3
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"This document contains # special characters, while the other document contains the same # special character."
argument_list|,
literal|"id"
argument_list|,
literal|"103"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"While the other document contains the same # special character."
argument_list|,
literal|"id"
argument_list|,
literal|"104"
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
literal|"different breakiterator"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.bs.type"
argument_list|,
literal|"SEPARATOR"
argument_list|,
literal|"hl.bs.separator"
argument_list|,
literal|"#"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='103']/arr[@name='text']/str='This<em>document</em> contains #'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"different breakiterator"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.bs.type"
argument_list|,
literal|"SEPARATOR"
argument_list|,
literal|"hl.bs.separator"
argument_list|,
literal|"#"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='104']/arr[@name='text']/str='While the other<em>document</em> contains the same #'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncoder
specifier|public
name|void
name|testEncoder
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"Document one has a first<i>sentence</i>."
argument_list|,
literal|"id"
argument_list|,
literal|"103"
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
literal|"html escaped"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:document"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.encoder"
argument_list|,
literal|"html"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='103']/arr[@name='text']/str='<em>Document</em>&#32;one&#32;has&#32;a&#32;first&#32;&lt;i&gt;sentence&lt;&#x2F;i&gt;&#46;'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWildcard
specifier|public
name|void
name|testWildcard
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"simplest test"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:doc*ment"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.highlightMultiTerm"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"count(//lst[@name='highlighting']/*)=2"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='101']/arr[@name='text']/str='<em>document</em> one'"
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='102']/arr[@name='text']/str='second<em>document</em>'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

