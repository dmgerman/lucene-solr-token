begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|LowerCaseFilter
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
name|OffsetAttribute
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestSynonymTokenFilter
specifier|public
class|class
name|TestSynonymTokenFilter
extends|extends
name|TestCase
block|{
DECL|field|dataDir
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|testFile
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"org/apache/lucene/index/memory/testSynonyms.txt"
argument_list|)
decl_stmt|;
DECL|method|testSynonyms
specifier|public
name|void
name|testSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|testFile
argument_list|)
argument_list|)
decl_stmt|;
comment|/* all expansions */
name|Analyzer
name|analyzer
init|=
operator|new
name|SynonymWhitespaceAnalyzer
argument_list|(
name|map
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"Lost in the woods"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lost"
block|,
literal|"in"
block|,
literal|"the"
block|,
literal|"woods"
block|,
literal|"forest"
block|,
literal|"wood"
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
literal|12
block|,
literal|12
block|,
literal|12
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
literal|11
block|,
literal|17
block|,
literal|17
block|,
literal|17
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
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSynonymsLimitedAmount
specifier|public
name|void
name|testSynonymsLimitedAmount
parameter_list|()
throws|throws
name|Exception
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|testFile
argument_list|)
argument_list|)
decl_stmt|;
comment|/* limit to one synonym expansion */
name|Analyzer
name|analyzer
init|=
operator|new
name|SynonymWhitespaceAnalyzer
argument_list|(
name|map
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"Lost in the woods"
argument_list|,
comment|/* wood comes before forest due to           * the input file, not lexicographic order          */
operator|new
name|String
index|[]
block|{
literal|"lost"
block|,
literal|"in"
block|,
literal|"the"
block|,
literal|"woods"
block|,
literal|"wood"
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
literal|12
block|,
literal|12
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
literal|11
block|,
literal|17
block|,
literal|17
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
block|,
literal|0
block|}
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
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|testFile
argument_list|)
argument_list|)
decl_stmt|;
comment|/* limit to one synonym expansion */
name|Analyzer
name|analyzer
init|=
operator|new
name|SynonymWhitespaceAnalyzer
argument_list|(
name|map
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"Lost in the woods"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lost"
block|,
literal|"in"
block|,
literal|"the"
block|,
literal|"woods"
block|,
literal|"wood"
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
literal|12
block|,
literal|12
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
literal|11
block|,
literal|17
block|,
literal|17
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
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"My wolfish dog went to the forest"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"my"
block|,
literal|"wolfish"
block|,
literal|"ravenous"
block|,
literal|"dog"
block|,
literal|"went"
block|,
literal|"to"
block|,
literal|"the"
block|,
literal|"forest"
block|,
literal|"woods"
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
literal|3
block|,
literal|11
block|,
literal|15
block|,
literal|20
block|,
literal|23
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
literal|2
block|,
literal|10
block|,
literal|10
block|,
literal|14
block|,
literal|19
block|,
literal|22
block|,
literal|26
block|,
literal|33
block|,
literal|33
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
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|SynonymWhitespaceAnalyzer
specifier|private
class|class
name|SynonymWhitespaceAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|synonyms
specifier|private
name|SynonymMap
name|synonyms
decl_stmt|;
DECL|field|maxSynonyms
specifier|private
name|int
name|maxSynonyms
decl_stmt|;
DECL|method|SynonymWhitespaceAnalyzer
specifier|public
name|SynonymWhitespaceAnalyzer
parameter_list|(
name|SynonymMap
name|synonyms
parameter_list|,
name|int
name|maxSynonyms
parameter_list|)
block|{
name|this
operator|.
name|synonyms
operator|=
name|synonyms
expr_stmt|;
name|this
operator|.
name|maxSynonyms
operator|=
name|maxSynonyms
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
name|TokenStream
name|ts
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ts
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|SynonymTokenFilter
argument_list|(
name|ts
argument_list|,
name|synonyms
argument_list|,
name|maxSynonyms
argument_list|)
expr_stmt|;
return|return
name|ts
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|source
name|Tokenizer
name|source
decl_stmt|;
DECL|field|result
name|TokenStream
name|result
decl_stmt|;
block|}
empty_stmt|;
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SavedStreams
name|streams
init|=
operator|(
name|SavedStreams
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
name|streams
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|streams
operator|.
name|source
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|streams
operator|.
name|source
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|SynonymTokenFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|,
name|synonyms
argument_list|,
name|maxSynonyms
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|source
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// reset the SynonymTokenFilter
block|}
return|return
name|streams
operator|.
name|result
return|;
block|}
block|}
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|int
name|posIncs
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
operator|(
name|OffsetAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
operator|(
name|PositionIncrementAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
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
name|output
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|termAtt
operator|.
name|term
argument_list|()
argument_list|,
name|output
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|startOffsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|,
name|endOffsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|,
name|posIncs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|int
name|posIncs
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|reusableTokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
operator|(
name|OffsetAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
operator|(
name|PositionIncrementAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
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
name|output
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|termAtt
operator|.
name|term
argument_list|()
argument_list|,
name|output
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|startOffsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|,
name|endOffsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|,
name|posIncs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

