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
name|IOException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Token
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
name|TypeAttribute
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
name|TypeAttributeImpl
import|;
end_import

begin_class
DECL|class|ShingleFilterTest
specifier|public
class|class
name|ShingleFilterTest
extends|extends
name|TestCase
block|{
DECL|class|TestTokenStream
specifier|public
class|class
name|TestTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|index
specifier|protected
name|int
name|index
init|=
literal|0
decl_stmt|;
DECL|field|testToken
specifier|protected
name|Token
index|[]
name|testToken
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|method|TestTokenStream
specifier|public
name|TestTokenStream
parameter_list|(
name|Token
index|[]
name|testToken
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|testToken
operator|=
name|testToken
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|posIncrAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|index
operator|<
name|testToken
operator|.
name|length
condition|)
block|{
name|Token
name|t
init|=
name|testToken
index|[
name|index
operator|++
index|]
decl_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|t
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|t
operator|.
name|startOffset
argument_list|()
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|t
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|TypeAttributeImpl
operator|.
name|DEFAULT_TYPE
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ShingleFilterTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|TEST_TOKEN
specifier|public
specifier|static
specifier|final
name|Token
index|[]
name|TEST_TOKEN
init|=
operator|new
name|Token
index|[]
block|{
name|createToken
argument_list|(
literal|"please"
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide"
argument_list|,
literal|7
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"this"
argument_list|,
literal|14
argument_list|,
literal|18
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence"
argument_list|,
literal|19
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"into"
argument_list|,
literal|28
argument_list|,
literal|32
argument_list|)
block|,
name|createToken
argument_list|(
literal|"shingles"
argument_list|,
literal|33
argument_list|,
literal|39
argument_list|)
block|,   }
decl_stmt|;
DECL|field|testTokenWithHoles
specifier|public
specifier|static
name|Token
index|[]
name|testTokenWithHoles
decl_stmt|;
DECL|field|BI_GRAM_TOKENS
specifier|public
specifier|static
specifier|final
name|Token
index|[]
name|BI_GRAM_TOKENS
init|=
operator|new
name|Token
index|[]
block|{
name|createToken
argument_list|(
literal|"please"
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|createToken
argument_list|(
literal|"please divide"
argument_list|,
literal|0
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide"
argument_list|,
literal|7
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide this"
argument_list|,
literal|7
argument_list|,
literal|18
argument_list|)
block|,
name|createToken
argument_list|(
literal|"this"
argument_list|,
literal|14
argument_list|,
literal|18
argument_list|)
block|,
name|createToken
argument_list|(
literal|"this sentence"
argument_list|,
literal|14
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence"
argument_list|,
literal|19
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence into"
argument_list|,
literal|19
argument_list|,
literal|32
argument_list|)
block|,
name|createToken
argument_list|(
literal|"into"
argument_list|,
literal|28
argument_list|,
literal|32
argument_list|)
block|,
name|createToken
argument_list|(
literal|"into shingles"
argument_list|,
literal|28
argument_list|,
literal|39
argument_list|)
block|,
name|createToken
argument_list|(
literal|"shingles"
argument_list|,
literal|33
argument_list|,
literal|39
argument_list|)
block|,   }
decl_stmt|;
DECL|field|BI_GRAM_POSITION_INCREMENTS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|BI_GRAM_POSITION_INCREMENTS
init|=
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
literal|0
block|,
literal|1
block|}
decl_stmt|;
DECL|field|BI_GRAM_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|BI_GRAM_TYPES
init|=
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|}
decl_stmt|;
DECL|field|BI_GRAM_TOKENS_WITH_HOLES
specifier|public
specifier|static
specifier|final
name|Token
index|[]
name|BI_GRAM_TOKENS_WITH_HOLES
init|=
operator|new
name|Token
index|[]
block|{
name|createToken
argument_list|(
literal|"please"
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|createToken
argument_list|(
literal|"please divide"
argument_list|,
literal|0
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide"
argument_list|,
literal|7
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide _"
argument_list|,
literal|7
argument_list|,
literal|19
argument_list|)
block|,
name|createToken
argument_list|(
literal|"_"
argument_list|,
literal|19
argument_list|,
literal|19
argument_list|)
block|,
name|createToken
argument_list|(
literal|"_ sentence"
argument_list|,
literal|19
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence"
argument_list|,
literal|19
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence _"
argument_list|,
literal|19
argument_list|,
literal|33
argument_list|)
block|,
name|createToken
argument_list|(
literal|"_"
argument_list|,
literal|33
argument_list|,
literal|33
argument_list|)
block|,
name|createToken
argument_list|(
literal|"_ shingles"
argument_list|,
literal|33
argument_list|,
literal|39
argument_list|)
block|,
name|createToken
argument_list|(
literal|"shingles"
argument_list|,
literal|33
argument_list|,
literal|39
argument_list|)
block|,   }
decl_stmt|;
DECL|field|BI_GRAM_POSITION_INCREMENTS_WITH_HOLES
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|BI_GRAM_POSITION_INCREMENTS_WITH_HOLES
init|=
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
literal|0
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TRI_GRAM_TOKENS
specifier|public
specifier|static
specifier|final
name|Token
index|[]
name|TRI_GRAM_TOKENS
init|=
operator|new
name|Token
index|[]
block|{
name|createToken
argument_list|(
literal|"please"
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|createToken
argument_list|(
literal|"please divide"
argument_list|,
literal|0
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"please divide this"
argument_list|,
literal|0
argument_list|,
literal|18
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide"
argument_list|,
literal|7
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide this"
argument_list|,
literal|7
argument_list|,
literal|18
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide this sentence"
argument_list|,
literal|7
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"this"
argument_list|,
literal|14
argument_list|,
literal|18
argument_list|)
block|,
name|createToken
argument_list|(
literal|"this sentence"
argument_list|,
literal|14
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"this sentence into"
argument_list|,
literal|14
argument_list|,
literal|32
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence"
argument_list|,
literal|19
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence into"
argument_list|,
literal|19
argument_list|,
literal|32
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence into shingles"
argument_list|,
literal|19
argument_list|,
literal|39
argument_list|)
block|,
name|createToken
argument_list|(
literal|"into"
argument_list|,
literal|28
argument_list|,
literal|32
argument_list|)
block|,
name|createToken
argument_list|(
literal|"into shingles"
argument_list|,
literal|28
argument_list|,
literal|39
argument_list|)
block|,
name|createToken
argument_list|(
literal|"shingles"
argument_list|,
literal|33
argument_list|,
literal|39
argument_list|)
block|}
decl_stmt|;
DECL|field|TRI_GRAM_POSITION_INCREMENTS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|TRI_GRAM_POSITION_INCREMENTS
init|=
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
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TRI_GRAM_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TRI_GRAM_TYPES
init|=
operator|new
name|String
index|[]
block|{
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"shingle"
block|,
literal|"word"
block|,
literal|"shingle"
block|,
literal|"word"
block|}
decl_stmt|;
DECL|method|setUp
specifier|protected
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
name|testTokenWithHoles
operator|=
operator|new
name|Token
index|[]
block|{
name|createToken
argument_list|(
literal|"please"
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|createToken
argument_list|(
literal|"divide"
argument_list|,
literal|7
argument_list|,
literal|13
argument_list|)
block|,
name|createToken
argument_list|(
literal|"sentence"
argument_list|,
literal|19
argument_list|,
literal|27
argument_list|)
block|,
name|createToken
argument_list|(
literal|"shingles"
argument_list|,
literal|33
argument_list|,
literal|39
argument_list|)
block|,     }
expr_stmt|;
name|testTokenWithHoles
index|[
literal|2
index|]
operator|.
name|setPositionIncrement
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|testTokenWithHoles
index|[
literal|3
index|]
operator|.
name|setPositionIncrement
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/*    * Class under test for void ShingleFilter(TokenStream, int)    */
DECL|method|testBiGramFilter
specifier|public
name|void
name|testBiGramFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|shingleFilterTest
argument_list|(
literal|2
argument_list|,
name|TEST_TOKEN
argument_list|,
name|BI_GRAM_TOKENS
argument_list|,
name|BI_GRAM_POSITION_INCREMENTS
argument_list|,
name|BI_GRAM_TYPES
argument_list|)
expr_stmt|;
block|}
DECL|method|testBiGramFilterWithHoles
specifier|public
name|void
name|testBiGramFilterWithHoles
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|shingleFilterTest
argument_list|(
literal|2
argument_list|,
name|testTokenWithHoles
argument_list|,
name|BI_GRAM_TOKENS_WITH_HOLES
argument_list|,
name|BI_GRAM_POSITION_INCREMENTS
argument_list|,
name|BI_GRAM_TYPES
argument_list|)
expr_stmt|;
block|}
DECL|method|testTriGramFilter
specifier|public
name|void
name|testTriGramFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|shingleFilterTest
argument_list|(
literal|3
argument_list|,
name|TEST_TOKEN
argument_list|,
name|TRI_GRAM_TOKENS
argument_list|,
name|TRI_GRAM_POSITION_INCREMENTS
argument_list|,
name|TRI_GRAM_TYPES
argument_list|)
expr_stmt|;
block|}
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|wsTokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"please divide this sentence"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|ShingleFilter
argument_list|(
name|wsTokenizer
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(please,0,6)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(please divide,0,13,type=shingle,posIncr=0)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wsTokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"please divide this sentence"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(please,0,6)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|shingleFilterTest
specifier|protected
name|void
name|shingleFilterTest
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|Token
index|[]
name|tokensToShingle
parameter_list|,
name|Token
index|[]
name|tokensToCompare
parameter_list|,
name|int
index|[]
name|positionIncrements
parameter_list|,
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|filter
init|=
operator|new
name|ShingleFilter
argument_list|(
operator|new
name|TestTokenStream
argument_list|(
name|tokensToShingle
argument_list|)
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|filter
operator|.
name|addAttribute
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
name|filter
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
operator|(
name|PositionIncrementAttribute
operator|)
name|filter
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
operator|(
name|TypeAttribute
operator|)
name|filter
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|filter
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
name|String
name|goldText
init|=
name|tokensToCompare
index|[
name|i
index|]
operator|.
name|term
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong termText"
argument_list|,
name|goldText
argument_list|,
name|termText
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong startOffset for token \""
operator|+
name|termText
operator|+
literal|"\""
argument_list|,
name|tokensToCompare
index|[
name|i
index|]
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong endOffset for token \""
operator|+
name|termText
operator|+
literal|"\""
argument_list|,
name|tokensToCompare
index|[
name|i
index|]
operator|.
name|endOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong positionIncrement for token \""
operator|+
name|termText
operator|+
literal|"\""
argument_list|,
name|positionIncrements
index|[
name|i
index|]
argument_list|,
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong type for token \""
operator|+
name|termText
operator|+
literal|"\""
argument_list|,
name|types
index|[
name|i
index|]
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
DECL|method|createToken
specifier|private
specifier|static
name|Token
name|createToken
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|start
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|token
operator|.
name|setTermBuffer
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

