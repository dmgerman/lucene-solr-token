begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|KeywordAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|Term
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
name|store
operator|.
name|RAMDirectory
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
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|automaton
operator|.
name|Automaton
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
name|util
operator|.
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_comment
comment|/**  * Test the automaton query for several unicode corner cases,  * specifically enumerating strings/indexes containing supplementary characters,  * and the differences between UTF-8/UTF-32 and UTF-16 binary sort order.  */
end_comment

begin_class
DECL|class|TestAutomatonQueryUnicode
specifier|public
class|class
name|TestAutomatonQueryUnicode
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|FN
specifier|private
specifier|final
name|String
name|FN
init|=
literal|"field"
decl_stmt|;
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
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|titleField
init|=
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
literal|"some title"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
name|FN
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|Field
name|footerField
init|=
operator|new
name|Field
argument_list|(
literal|"footer"
argument_list|,
literal|"a footer"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|titleField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|footerField
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"\uD866\uDF05abcdef"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"\uD866\uDF06ghijkl"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// this sorts before the previous two in UTF-8/UTF-32, but after in UTF-16!!!
name|field
operator|.
name|setValue
argument_list|(
literal|"\uFB94mnopqr"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"\uFB95stuvwx"
argument_list|)
expr_stmt|;
comment|// this one too.
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"a\uFFFCbc"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"a\uFFFDbc"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"a\uFFFEbc"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"a\uFB94bc"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"bacadaba"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"\uFFFD"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"\uFFFD\uD866\uDF05"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
literal|"\uFFFD\uFFFD"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|newTerm
specifier|private
name|Term
name|newTerm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FN
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|automatonQueryNrHits
specifier|private
name|int
name|automatonQueryNrHits
parameter_list|(
name|AutomatonQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|5
argument_list|)
operator|.
name|totalHits
return|;
block|}
DECL|method|assertAutomatonHits
specifier|private
name|void
name|assertAutomatonHits
parameter_list|(
name|int
name|expected
parameter_list|,
name|Automaton
name|automaton
parameter_list|)
throws|throws
name|IOException
block|{
name|AutomatonQuery
name|query
init|=
operator|new
name|AutomatonQuery
argument_list|(
name|newTerm
argument_list|(
literal|"bogus"
argument_list|)
argument_list|,
name|automaton
argument_list|)
decl_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|automatonQueryNrHits
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that AutomatonQuery interacts with lucene's sort order correctly.    *     * This expression matches something either starting with the arabic    * presentation forms block, or a supplementary character.    */
DECL|method|testSortOrder
specifier|public
name|void
name|testSortOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"((\uD866\uDF05)|\uFB94).*"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|2
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that AutomatonQuery properly seeks to supplementary characters.    * Transitions are modeled as UTF-16 code units, so without special handling    * by default it will try to seek to a lead surrogate with some DFAs    */
DECL|method|testSeekSurrogate
specifier|public
name|void
name|testSeekSurrogate
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"\uD866[a\uDF05\uFB93][a-z]{0,5}[fl]"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/**    * Try seeking to an ending lead surrogate.    */
DECL|method|testSeekSurrogate2
specifier|public
name|void
name|testSeekSurrogate2
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"\uD866(\uDF06ghijkl)?"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/**    * Try seeking to an starting trail surrogate.    */
DECL|method|testSeekSurrogate3
specifier|public
name|void
name|testSeekSurrogate3
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"[\uDF06\uFB94]mnopqr"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/**    * Try seeking to an medial/final trail surrogate.    */
DECL|method|testSeekSurrogate4
specifier|public
name|void
name|testSeekSurrogate4
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"a[\uDF06\uFB94]bc"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure the 'constant suffix' does not contain a leading trail surrogate.    */
DECL|method|testSurrogateSuffix
specifier|public
name|void
name|testSurrogateSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|".*[\uD865\uD866]\uDF06ghijkl"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/**    * Try when the constant suffix is only a leading trail surrogate.    * instead this must use an empty suffix.    */
DECL|method|testSurrogateSuffix2
specifier|public
name|void
name|testSurrogateSuffix2
parameter_list|()
throws|throws
name|IOException
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|".*\uDF05"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertAutomatonHits
argument_list|(
literal|1
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

