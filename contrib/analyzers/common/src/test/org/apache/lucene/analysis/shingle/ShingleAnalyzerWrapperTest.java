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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LetterTokenizer
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
name|WhitespaceAnalyzer
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
name|WhitespaceTokenizer
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|queryParser
operator|.
name|QueryParser
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
name|BooleanClause
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
name|BooleanQuery
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
name|IndexSearcher
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
name|PhraseQuery
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
name|Query
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
name|ScoreDoc
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
name|TermQuery
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
name|Version
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
DECL|field|searcher
specifier|public
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/**    * Set up a new index in RAM with three test phrases and the supplied Analyzer.    *    * @param analyzer the analyzer to use    * @return an indexSearcher on the test index.    * @throws Exception if an error occurs with index writer or searcher    */
DECL|method|setUpSearcher
specifier|public
name|IndexSearcher
name|setUpSearcher
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
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
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
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
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|queryParsingTest
specifier|protected
name|ScoreDoc
index|[]
name|queryParsingTest
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|qs
parameter_list|)
throws|throws
name|Exception
block|{
name|searcher
operator|=
name|setUpSearcher
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"content"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qs
argument_list|)
decl_stmt|;
return|return
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
return|;
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
comment|/*    * Will not work on an index without unigrams, since QueryParser automatically    * tokenizes on whitespace.    */
DECL|method|testShingleAnalyzerWrapperQueryParsing
specifier|public
name|void
name|testShingleAnalyzerWrapperQueryParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|ScoreDoc
index|[]
name|hits
init|=
name|queryParsingTest
argument_list|(
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"test sentence"
argument_list|)
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
comment|/*    * This one fails with an exception.    */
DECL|method|testShingleAnalyzerWrapperPhraseQueryParsingFails
specifier|public
name|void
name|testShingleAnalyzerWrapperPhraseQueryParsingFails
parameter_list|()
throws|throws
name|Exception
block|{
name|ScoreDoc
index|[]
name|hits
init|=
name|queryParsingTest
argument_list|(
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"\"this sentence\""
argument_list|)
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
comment|/*    * This one works, actually.    */
DECL|method|testShingleAnalyzerWrapperPhraseQueryParsing
specifier|public
name|void
name|testShingleAnalyzerWrapperPhraseQueryParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|ScoreDoc
index|[]
name|hits
init|=
name|queryParsingTest
argument_list|(
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"\"test sentence\""
argument_list|)
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
comment|/*    * Same as above, is tokenized without using the analyzer.    */
DECL|method|testShingleAnalyzerWrapperRequiredQueryParsing
specifier|public
name|void
name|testShingleAnalyzerWrapperRequiredQueryParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|ScoreDoc
index|[]
name|hits
init|=
name|queryParsingTest
argument_list|(
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"+test +sentence"
argument_list|)
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
comment|/*    * This shows how to construct a phrase query containing shingles.    */
DECL|method|testShingleAnalyzerWrapperPhraseQuery
specifier|public
name|void
name|testShingleAnalyzerWrapperPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|searcher
operator|=
name|setUpSearcher
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"content"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"this sentence"
argument_list|)
argument_list|)
decl_stmt|;
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
name|TermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|term
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
name|Analyzer
name|analyzer
init|=
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|searcher
operator|=
name|setUpSearcher
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"content"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"test sentence"
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|term
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
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
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
name|assertAnalyzesToReuse
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
comment|/**    * subclass that acts just like whitespace analyzer for testing    */
DECL|class|ShingleWrapperSubclassAnalyzer
specifier|private
class|class
name|ShingleWrapperSubclassAnalyzer
extends|extends
name|ShingleAnalyzerWrapper
block|{
DECL|method|ShingleWrapperSubclassAnalyzer
specifier|public
name|ShingleWrapperSubclassAnalyzer
parameter_list|()
block|{
name|super
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
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
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
empty_stmt|;
DECL|method|testLUCENE1678BWComp
specifier|public
name|void
name|testLUCENE1678BWComp
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|ShingleWrapperSubclassAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"this is a test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
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
literal|8
block|,
literal|10
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|,
literal|9
block|,
literal|14
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * analyzer that does not support reuse    * it is LetterTokenizer on odd invocations, WhitespaceTokenizer on even.    */
DECL|class|NonreusableAnalyzer
specifier|private
class|class
name|NonreusableAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|invocationCount
name|int
name|invocationCount
init|=
literal|0
decl_stmt|;
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
operator|++
name|invocationCount
operator|%
literal|2
operator|==
literal|0
condition|)
return|return
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
return|;
else|else
return|return
operator|new
name|LetterTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
DECL|method|testWrappedAnalyzerDoesNotReuse
specifier|public
name|void
name|testWrappedAnalyzerDoesNotReuse
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
name|NonreusableAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"please divide into shingles."
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
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"please divide into shingles."
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
literal|"into shingles."
block|,
literal|"shingles."
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
literal|28
block|,
literal|28
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
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"please divide into shingles."
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
block|}
block|}
end_class

end_unit

