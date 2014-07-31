begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.shingle
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|shingle
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
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|BaseTokenStreamTestCase
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
name|MockAnalyzer
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
name|MockTokenizer
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
name|TokenFilter
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
name|TokenStream
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
name|Tokenizer
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
name|core
operator|.
name|StopFilter
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|util
operator|.
name|CharArraySet
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
name|TextField
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
name|DirectoryReader
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
name|IndexReader
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
name|IndexWriterConfig
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
name|search
operator|.
name|*
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
name|Directory
import|;
end_import

begin_comment
comment|/**  * A test class for ShingleAnalyzerWrapper as regards queries and scoring.  */
end_comment

begin_class
DECL|class|ShingleAnalyzerWrapperTest
specifier|public
class|class
name|ShingleAnalyzerWrapperTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
comment|/**    * Set up a new index in RAM with three test phrases and the supplied Analyzer.    *    * @throws Exception if an error occurs with index writer or searcher    */
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
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
decl_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"please divide this sentence into shingles"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"just another test sentence"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"a sentence which contains no test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
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
name|shutdown
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
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
DECL|method|compareRanks
specifier|protected
name|void
name|compareRanks
parameter_list|(
name|ScoreDoc
index|[]
name|hits
parameter_list|,
name|int
index|[]
name|ranks
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|ranks
operator|.
name|length
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ranks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|ranks
index|[
name|i
index|]
argument_list|,
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * This shows how to construct a phrase query containing shingles.    */
DECL|method|testShingleAnalyzerWrapperPhraseQuery
specifier|public
name|void
name|testShingleAnalyzerWrapperPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|PhraseQuery
name|q
init|=
operator|new
name|PhraseQuery
argument_list|()
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
literal|"content"
argument_list|,
literal|"this sentence"
argument_list|)
init|)
block|{
name|int
name|j
init|=
operator|-
literal|1
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|j
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|String
name|termText
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
name|termText
argument_list|)
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|int
index|[]
name|ranks
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|}
decl_stmt|;
name|compareRanks
argument_list|(
name|hits
argument_list|,
name|ranks
argument_list|)
expr_stmt|;
block|}
comment|/*    * How to construct a boolean query with shingles. A query like this will    * implicitly score those documents higher that contain the words in the query    * in the right order and adjacent to each other.    */
DECL|method|testShingleAnalyzerWrapperBooleanQuery
specifier|public
name|void
name|testShingleAnalyzerWrapperBooleanQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
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
literal|"content"
argument_list|,
literal|"test sentence"
argument_list|)
init|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|String
name|termText
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
name|termText
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|int
index|[]
name|ranks
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|0
block|}
decl_stmt|;
name|compareRanks
argument_list|(
name|hits
argument_list|,
name|ranks
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"please divide"
block|,
literal|"divide"
block|,
literal|"divide into"
block|,
literal|"into"
block|,
literal|"into shingles"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|18
block|,
literal|18
block|,
literal|27
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"divide me up again"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"divide"
block|,
literal|"divide me"
block|,
literal|"me"
block|,
literal|"me up"
block|,
literal|"up"
block|,
literal|"up again"
block|,
literal|"again"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|10
block|,
literal|10
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|9
block|,
literal|9
block|,
literal|12
block|,
literal|12
block|,
literal|18
block|,
literal|18
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonDefaultMinShingleSize
specifier|public
name|void
name|testNonDefaultMinShingleSize
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide this sentence into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"please divide this"
block|,
literal|"please divide this sentence"
block|,
literal|"divide"
block|,
literal|"divide this sentence"
block|,
literal|"divide this sentence into"
block|,
literal|"this"
block|,
literal|"this sentence into"
block|,
literal|"this sentence into shingles"
block|,
literal|"sentence"
block|,
literal|"sentence into shingles"
block|,
literal|"into"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|,
literal|19
block|,
literal|28
block|,
literal|33
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|18
block|,
literal|27
block|,
literal|13
block|,
literal|27
block|,
literal|32
block|,
literal|18
block|,
literal|32
block|,
literal|41
block|,
literal|27
block|,
literal|41
block|,
literal|32
block|,
literal|41
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_TOKEN_SEPARATOR
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide this sentence into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please divide this"
block|,
literal|"please divide this sentence"
block|,
literal|"divide this sentence"
block|,
literal|"divide this sentence into"
block|,
literal|"this sentence into"
block|,
literal|"this sentence into shingles"
block|,
literal|"sentence into shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|18
block|,
literal|27
block|,
literal|27
block|,
literal|32
block|,
literal|32
block|,
literal|41
block|,
literal|41
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonDefaultMinAndSameMaxShingleSize
specifier|public
name|void
name|testNonDefaultMinAndSameMaxShingleSize
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide this sentence into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"please divide this"
block|,
literal|"divide"
block|,
literal|"divide this sentence"
block|,
literal|"this"
block|,
literal|"this sentence into"
block|,
literal|"sentence"
block|,
literal|"sentence into shingles"
block|,
literal|"into"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|,
literal|19
block|,
literal|28
block|,
literal|33
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|18
block|,
literal|13
block|,
literal|27
block|,
literal|18
block|,
literal|32
block|,
literal|27
block|,
literal|41
block|,
literal|32
block|,
literal|41
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_TOKEN_SEPARATOR
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide this sentence into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please divide this"
block|,
literal|"divide this sentence"
block|,
literal|"this sentence into"
block|,
literal|"sentence into shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|18
block|,
literal|27
block|,
literal|32
block|,
literal|41
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoTokenSeparator
specifier|public
name|void
name|testNoTokenSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|""
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"pleasedivide"
block|,
literal|"divide"
block|,
literal|"divideinto"
block|,
literal|"into"
block|,
literal|"intoshingles"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|18
block|,
literal|18
block|,
literal|27
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pleasedivide"
block|,
literal|"divideinto"
block|,
literal|"intoshingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|18
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullTokenSeparator
specifier|public
name|void
name|testNullTokenSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"pleasedivide"
block|,
literal|"divide"
block|,
literal|"divideinto"
block|,
literal|"into"
block|,
literal|"intoshingles"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|18
block|,
literal|18
block|,
literal|27
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pleasedivide"
block|,
literal|"divideinto"
block|,
literal|"intoshingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|18
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAltTokenSeparator
specifier|public
name|void
name|testAltTokenSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|"<SEP>"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"please<SEP>divide"
block|,
literal|"divide"
block|,
literal|"divide<SEP>into"
block|,
literal|"into"
block|,
literal|"into<SEP>shingles"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|14
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|18
block|,
literal|18
block|,
literal|27
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|"<SEP>"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please<SEP>divide"
block|,
literal|"divide<SEP>into"
block|,
literal|"into<SEP>shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|18
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAltFillerToken
specifier|public
name|void
name|testAltFillerToken
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|delegate
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
name|CharArraySet
name|stopSet
init|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"into"
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenFilter
name|filter
init|=
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|stopSet
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
name|delegate
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_TOKEN_SEPARATOR
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|"--"
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
block|,
literal|"please divide"
block|,
literal|"divide"
block|,
literal|"divide --"
block|,
literal|"-- shingles"
block|,
literal|"shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|19
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|19
block|,
literal|27
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
name|delegate
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_TOKEN_SEPARATOR
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please divide"
block|,
literal|"divide "
block|,
literal|" shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|19
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
name|delegate
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_TOKEN_SEPARATOR
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please divide into shingles"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please divide"
block|,
literal|"divide "
block|,
literal|" shingles"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|19
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOutputUnigramsIfNoShinglesSingleToken
specifier|public
name|void
name|testOutputUnigramsIfNoShinglesSingleToken
parameter_list|()
throws|throws
name|Exception
block|{
name|ShingleAnalyzerWrapper
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
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
literal|false
argument_list|)
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_FILLER_TOKEN
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"please"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"please"
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
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

