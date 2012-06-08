begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|analysis
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
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
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
name|PayloadAttribute
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

begin_class
DECL|class|TestAnalyzers
specifier|public
class|class
name|TestAnalyzers
extends|extends
name|BaseTokenStreamTestCase
block|{
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
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo bar FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo      bar .  FOO<> BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo.bar.FOO.BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"U.S.A."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"u"
block|,
literal|"s"
block|,
literal|"a"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C++"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"B2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"b"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\"QUOTED\" word"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quoted"
block|,
literal|"word"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNull
specifier|public
name|void
name|testNull
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo bar FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"FOO"
block|,
literal|"BAR"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo      bar .  FOO<> BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"."
block|,
literal|"FOO"
block|,
literal|"<>"
block|,
literal|"BAR"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo.bar.FOO.BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo.bar.FOO.BAR"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"U.S.A."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"U.S.A."
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C++"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"C++"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"B2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"B2B"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2B"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\"QUOTED\" word"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\"QUOTED\""
block|,
literal|"word"
block|}
argument_list|)
expr_stmt|;
block|}
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
name|StopAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo bar FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo a bar such FOO THESE BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyPayload
name|void
name|verifyPayload
parameter_list|(
name|TokenStream
name|ts
parameter_list|)
throws|throws
name|IOException
block|{
name|PayloadAttribute
name|payloadAtt
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|b
init|=
literal|1
init|;
condition|;
name|b
operator|++
control|)
block|{
name|boolean
name|hasNext
init|=
name|ts
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
break|break;
comment|// System.out.println("id="+System.identityHashCode(nextToken) + " " + t);
comment|// System.out.println("payload=" + (int)nextToken.getPayload().toByteArray()[0]);
name|assertEquals
argument_list|(
name|b
argument_list|,
name|payloadAtt
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Make sure old style next() calls result in a new copy of payloads
DECL|method|testPayloadCopy
specifier|public
name|void
name|testPayloadCopy
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|s
init|=
literal|"how now brown cow"
decl_stmt|;
name|TokenStream
name|ts
decl_stmt|;
name|ts
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|PayloadSetter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|verifyPayload
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|PayloadSetter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|verifyPayload
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1150: Just a compile time test, to ensure the
comment|// StandardAnalyzer constants remain publicly accessible
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|_testStandardConstants
specifier|public
name|void
name|_testStandardConstants
parameter_list|()
block|{
name|int
name|x
init|=
name|StandardTokenizer
operator|.
name|ALPHANUM
decl_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|APOSTROPHE
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|ACRONYM
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|COMPANY
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|EMAIL
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|HOST
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|NUM
expr_stmt|;
name|x
operator|=
name|StandardTokenizer
operator|.
name|CJ
expr_stmt|;
name|String
index|[]
name|y
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
decl_stmt|;
block|}
DECL|class|LowerCaseWhitespaceAnalyzer
specifier|private
specifier|static
class|class
name|LowerCaseWhitespaceAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|LowerCaseFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Test that LowercaseFilter handles entire unicode range correctly    */
DECL|method|testLowerCaseFilter
specifier|public
name|void
name|testLowerCaseFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|LowerCaseWhitespaceAnalyzer
argument_list|()
decl_stmt|;
comment|// BMP
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AbaCaDabA"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abacadaba"
block|}
argument_list|)
expr_stmt|;
comment|// supplementary
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\ud801\udc16\ud801\udc16\ud801\udc16\ud801\udc16"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\ud801\udc3e\ud801\udc3e\ud801\udc3e\ud801\udc3e"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AbaCa\ud801\udc16DabA"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abaca\ud801\udc3edaba"
block|}
argument_list|)
expr_stmt|;
comment|// unpaired lead surrogate
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AbaC\uD801AdaBa"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abac\uD801adaba"
block|}
argument_list|)
expr_stmt|;
comment|// unpaired trail surrogate
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"AbaC\uDC16AdaBa"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abac\uDC16adaba"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that LowercaseFilter handles the lowercasing correctly if the term    * buffer has a trailing surrogate character leftover and the current term in    * the buffer ends with a corresponding leading surrogate.    */
DECL|method|testLowerCaseFilterLowSurrogateLeftover
specifier|public
name|void
name|testLowerCaseFilterLowSurrogateLeftover
parameter_list|()
throws|throws
name|IOException
block|{
comment|// test if the limit of the termbuffer is correctly used with supplementary
comment|// chars
name|WhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"BogustermBogusterm\udc16"
argument_list|)
argument_list|)
decl_stmt|;
name|LowerCaseFilter
name|filter
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"bogustermbogusterm\udc16"
block|}
argument_list|)
expr_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
name|highSurEndingUpper
init|=
literal|"BogustermBoguster\ud801"
decl_stmt|;
name|String
name|highSurEndingLower
init|=
literal|"bogustermboguster\ud801"
decl_stmt|;
name|tokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|highSurEndingUpper
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
name|highSurEndingLower
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|hasAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|char
index|[]
name|termBuffer
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|highSurEndingLower
operator|.
name|length
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|'\ud801'
argument_list|,
name|termBuffer
index|[
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'\udc3e'
argument_list|,
name|termBuffer
index|[
name|length
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testLowerCaseTokenizer
specifier|public
name|void
name|testLowerCaseTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tokenizer"
block|,
literal|"\ud801\udc44test"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWhitespaceTokenizer
specifier|public
name|void
name|testWhitespaceTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|WhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Tokenizer"
block|,
literal|"\ud801\udc1ctest"
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
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|StopAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random large strings through the analyzer */
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
operator|new
name|StopAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|PayloadSetter
specifier|final
class|class
name|PayloadSetter
extends|extends
name|TokenFilter
block|{
DECL|field|payloadAtt
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|method|PayloadSetter
specifier|public
name|PayloadSetter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|data
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|p
name|BytesRef
name|p
init|=
operator|new
name|BytesRef
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|hasNext
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
return|return
literal|false
return|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// reuse the payload / byte[]
name|data
index|[
literal|0
index|]
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

