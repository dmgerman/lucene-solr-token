begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|document
operator|.
name|FieldType
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
name|AtomicReader
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
name|DocsAndPositionsEnum
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|Fields
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
name|RandomIndexWriter
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
name|Terms
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
name|TermsEnum
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
name|BytesRef
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
name|TestUtil
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
name|AutomatonTestUtil
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
name|Automata
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
name|Operations
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
name|CharacterRunAutomaton
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

begin_class
DECL|class|TestMockAnalyzer
specifier|public
class|class
name|TestMockAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Test a configuration that behaves a lot like WhitespaceAnalyzer */
DECL|method|testWhitespace
specifier|public
name|void
name|testWhitespace
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"A bc defg hiJklmn opqrstuv wxy z "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"bc"
block|,
literal|"defg"
block|,
literal|"hijklmn"
block|,
literal|"opqrstuv"
block|,
literal|"wxy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aba cadaba shazam"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aba"
block|,
literal|"cadaba"
block|,
literal|"shazam"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"break on whitespace"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"break"
block|,
literal|"on"
block|,
literal|"whitespace"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like SimpleAnalyzer */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a-bc123 defg+hijklmn567opqrstuv78wxy_z "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"bc"
block|,
literal|"defg"
block|,
literal|"hijklmn"
block|,
literal|"opqrstuv"
block|,
literal|"wxy"
block|,
literal|"z"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aba4cadaba-Shazam"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aba"
block|,
literal|"cadaba"
block|,
literal|"shazam"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"break+on/Letters"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"break"
block|,
literal|"on"
block|,
literal|"letters"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like KeywordAnalyzer */
DECL|method|testKeyword
specifier|public
name|void
name|testKeyword
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"a-bc123 defg+hijklmn567opqrstuv78wxy_z "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a-bc123 defg+hijklmn567opqrstuv78wxy_z "
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aba4cadaba-Shazam"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aba4cadaba-Shazam"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"break+on/Nothing"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"break+on/Nothing"
block|}
argument_list|)
expr_stmt|;
comment|// currently though emits no tokens for empty string: maybe we can do it,
comment|// but we don't want to emit tokens infinitely...
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Test some regular expressions as tokenization patterns
comment|/** Test a configuration where each character is a term */
DECL|method|testSingleChar
specifier|public
name|void
name|testSingleChar
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|single
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"."
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|single
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foobar"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"f"
block|,
literal|"o"
block|,
literal|"o"
block|,
literal|"b"
block|,
literal|"a"
block|,
literal|"r"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|}
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration where two characters makes a term */
DECL|method|testTwoChars
specifier|public
name|void
name|testTwoChars
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|single
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|".."
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|single
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foobar"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fo"
block|,
literal|"ob"
block|,
literal|"ar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|}
argument_list|)
expr_stmt|;
comment|// make sure when last term is a "partial" match that end() is correct
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"bogus"
argument_list|,
literal|"fooba"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fo"
block|,
literal|"ob"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|,
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration where three characters makes a term */
DECL|method|testThreeChars
specifier|public
name|void
name|testThreeChars
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|single
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"..."
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|single
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foobar"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|}
argument_list|)
expr_stmt|;
comment|// make sure when last term is a "partial" match that end() is correct
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"bogus"
argument_list|,
literal|"fooba"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration where word starts with one uppercase */
DECL|method|testUppercase
specifier|public
name|void
name|testUppercase
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|single
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"[A-Z][a-z]*"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|single
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"FooBarBAZ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Foo"
block|,
literal|"Bar"
block|,
literal|"B"
block|,
literal|"A"
block|,
literal|"Z"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"aFooBar"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Foo"
block|,
literal|"Bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|}
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like StopAnalyzer */
DECL|method|testStop
specifier|public
name|void
name|testStop
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the quick brown a fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like KeepWordFilter */
DECL|method|testKeep
specifier|public
name|void
name|testKeep
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|keepWords
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Operations
operator|.
name|complement
argument_list|(
name|Operations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|keepWords
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick foo brown bar bar fox foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"bar"
block|,
literal|"foo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a configuration that behaves a lot like LengthFilter */
DECL|method|testLength
specifier|public
name|void
name|testLength
parameter_list|()
throws|throws
name|Exception
block|{
name|CharacterRunAutomaton
name|length5
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|".{5,}"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|,
name|length5
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ok toolong fine notfine"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ok"
block|,
literal|"fine"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test MockTokenizer encountering a too long token */
DECL|method|testTooLongToken
specifier|public
name|void
name|testTooLongToken
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|whitespace
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|t
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|,
literal|5
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
name|t
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|whitespace
operator|.
name|tokenStream
argument_list|(
literal|"bogus"
argument_list|,
literal|"test 123 toolong ok "
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|"123"
block|,
literal|"toolo"
block|,
literal|"ng"
block|,
literal|"ok"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|9
block|,
literal|14
block|,
literal|17
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|8
block|,
literal|14
block|,
literal|16
block|,
literal|19
block|}
argument_list|,
operator|new
name|Integer
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|whitespace
operator|.
name|tokenStream
argument_list|(
literal|"bogus"
argument_list|,
literal|"test 123 toolo"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|"123"
block|,
literal|"toolo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|8
block|,
literal|14
block|}
argument_list|,
operator|new
name|Integer
argument_list|(
literal|14
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLUCENE_3042
specifier|public
name|void
name|testLUCENE_3042
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testString
init|=
literal|"t"
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
name|testString
argument_list|)
init|)
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|// consume
block|}
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
name|testString
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through differently configured tokenizers */
DECL|method|testRandomRegexps
specifier|public
name|void
name|testRandomRegexps
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|30
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CharacterRunAutomaton
name|dfa
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomAutomaton
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|lowercase
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|t
init|=
operator|new
name|MockTokenizer
argument_list|(
name|dfa
argument_list|,
name|lowercase
argument_list|,
name|limit
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
name|t
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testForwardOffsets
specifier|public
name|void
name|testForwardOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomHtmlishString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|MockCharFilter
name|charfilter
init|=
operator|new
name|MockCharFilter
argument_list|(
name|reader
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"bogus"
argument_list|,
name|charfilter
argument_list|)
init|)
block|{
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
empty_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testWrapReader
specifier|public
name|void
name|testWrapReader
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-5153: test that wrapping an analyzer's reader is allowed
specifier|final
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|Analyzer
name|delegate
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|AnalyzerWrapper
argument_list|(
name|delegate
operator|.
name|getReuseStrategy
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Reader
name|wrapReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|MockCharFilter
argument_list|(
name|reader
argument_list|,
literal|7
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|delegate
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"abc"
argument_list|,
literal|"aabc"
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangeGaps
specifier|public
name|void
name|testChangeGaps
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-5324: check that it is possible to change the wrapper's gaps
specifier|final
name|int
name|positionGap
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offsetGap
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Analyzer
name|delegate
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Analyzer
name|a
init|=
operator|new
name|DelegatingAnalyzerWrapper
argument_list|(
name|delegate
operator|.
name|getReuseStrategy
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|delegate
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|positionGap
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|offsetGap
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|newDirectory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|a
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
specifier|final
name|TermsEnum
name|te
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DocsAndPositionsEnum
name|dpe
init|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dpe
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dpe
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dpe
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dpe
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|endOffset
init|=
name|dpe
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
operator|+
name|positionGap
argument_list|,
name|dpe
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
operator|+
name|endOffset
operator|+
name|offsetGap
argument_list|,
name|dpe
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|writer
operator|.
name|w
operator|.
name|getDirectory
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

