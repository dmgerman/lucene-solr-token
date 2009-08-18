begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_class
DECL|class|TestSmartChineseAnalyzer
specifier|public
class|class
name|TestSmartChineseAnalyzer
extends|extends
name|TestCase
block|{
DECL|method|testChineseStopWordsDefault
specifier|public
name|void
name|testChineseStopWordsDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|ca
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|()
decl_stmt|;
comment|/* will load stopwords */
name|String
name|sentence
init|=
literal|"æè´­ä¹°äºéå·åæè£ã"
decl_stmt|;
name|String
name|result
index|[]
init|=
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|ca
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/*    * This test is the same as the above, except with two phrases.    * This tests to ensure the SentenceTokenizer->WordTokenFilter chain works correctly.    */
DECL|method|testChineseStopWordsDefaultTwoPhrases
specifier|public
name|void
name|testChineseStopWordsDefaultTwoPhrases
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|ca
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|()
decl_stmt|;
comment|/* will load stopwords */
name|String
name|sentence
init|=
literal|"æè´­ä¹°äºéå·åæè£ã æè´­ä¹°äºéå·åæè£ã"
decl_stmt|;
name|String
name|result
index|[]
init|=
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|,
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|ca
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/*    * This test is the same as the above, except using an ideographic space as a separator.    * This tests to ensure the stopwords are working correctly.    */
DECL|method|testChineseStopWordsDefaultTwoPhrasesIdeoSpace
specifier|public
name|void
name|testChineseStopWordsDefaultTwoPhrasesIdeoSpace
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|ca
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|()
decl_stmt|;
comment|/* will load stopwords */
name|String
name|sentence
init|=
literal|"æè´­ä¹°äºéå·åæè£ãæè´­ä¹°äºéå·åæè£ã"
decl_stmt|;
name|String
name|result
index|[]
init|=
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|,
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|ca
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/*    * Punctuation is handled in a strange way if you disable stopwords    * In this example the IDEOGRAPHIC FULL STOP is converted into a comma.    * if you don't supply (true) to the constructor, or use a different stopwords list,    * then punctuation is indexed.    */
DECL|method|testChineseStopWordsOff
specifier|public
name|void
name|testChineseStopWordsOff
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|ca
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/* doesnt load stopwords */
name|String
name|sentence
init|=
literal|"æè´­ä¹°äºéå·åæè£ã"
decl_stmt|;
name|String
name|result
index|[]
init|=
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|,
literal|","
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|ca
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testChineseAnalyzer
specifier|public
name|void
name|testChineseAnalyzer
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|nt
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|Analyzer
name|ca
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Reader
name|sentence
init|=
operator|new
name|StringReader
argument_list|(
literal|"æè´­ä¹°äºéå·åæè£ã"
argument_list|)
decl_stmt|;
name|String
index|[]
name|result
init|=
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
decl_stmt|;
name|TokenStream
name|ts
init|=
name|ca
operator|.
name|tokenStream
argument_list|(
literal|"sentence"
argument_list|,
name|sentence
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|nt
operator|=
name|ts
operator|.
name|next
argument_list|(
name|nt
argument_list|)
expr_stmt|;
while|while
condition|(
name|nt
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|result
index|[
name|i
index|]
argument_list|,
name|nt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|nt
operator|=
name|ts
operator|.
name|next
argument_list|(
name|nt
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*    * English words are lowercased and porter-stemmed.    */
DECL|method|testMixedLatinChinese
specifier|public
name|void
name|testMixedLatinChinese
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"æè´­ä¹° Tests äºéå·åæè£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"test"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * Numerics are parsed as their own tokens    */
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"æè´­ä¹° Tests äºéå·åæè£1234"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"test"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|,
literal|"1234"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * Full width alphas and numerics are folded to half-width    */
DECL|method|testFullWidth
specifier|public
name|void
name|testFullWidth
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"æè´­ä¹° ï¼´ï½ï½ï½ï½ äºéå·åæè£ï¼ï¼ï¼ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"test"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|,
literal|"1234"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * Presentation form delimiters are removed    */
DECL|method|testDelimiters
specifier|public
name|void
name|testDelimiters
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"æè´­ä¹°ï¸± Tests äºéå·åæè£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"test"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * Text from writing systems other than Chinese and Latin are parsed as individual characters.    * (regardless of Unicode category)    */
DECL|method|testNonChinese
specifier|public
name|void
name|testNonChinese
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"æè´­ä¹° Ø±ÙØ¨Ø±ØªTests äºéå·åæè£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"Ø±"
block|,
literal|"Ù"
block|,
literal|"Ø¨"
block|,
literal|"Ø±"
block|,
literal|"Øª"
block|,
literal|"test"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test what the analyzer does with out-of-vocabulary words.    * In this case the name is Yousaf Raza Gillani.    * Currently it is being analyzed into single characters...    */
DECL|method|testOOV
specifier|public
name|void
name|testOOV
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"ä¼ç´ ç¦Â·ææÂ·åæå°¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¼"
block|,
literal|"ç´ "
block|,
literal|"ç¦"
block|,
literal|"æ"
block|,
literal|"æ"
block|,
literal|"å"
block|,
literal|"æ"
block|,
literal|"å°¼"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"ä¼ç´ ç¦ææåæå°¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¼"
block|,
literal|"ç´ "
block|,
literal|"ç¦"
block|,
literal|"æ"
block|,
literal|"æ"
block|,
literal|"å"
block|,
literal|"æ"
block|,
literal|"å°¼"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"æè´­ä¹°äºéå·åæè£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
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
literal|3
block|,
literal|4
block|,
literal|6
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|4
block|,
literal|6
block|,
literal|7
block|,
literal|9
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
name|Analyzer
name|a
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"æè´­ä¹° Tests äºéå·åæè£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"test"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
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
literal|4
block|,
literal|10
block|,
literal|11
block|,
literal|13
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|9
block|,
literal|11
block|,
literal|13
block|,
literal|14
block|,
literal|16
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"æè´­ä¹°äºéå·åæè£ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"è´­ä¹°"
block|,
literal|"äº"
block|,
literal|"éå·"
block|,
literal|"å"
block|,
literal|"æè£"
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
literal|3
block|,
literal|4
block|,
literal|6
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|4
block|,
literal|6
block|,
literal|7
block|,
literal|9
block|}
argument_list|)
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
name|String
name|types
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
name|TypeAttribute
name|typeAtt
init|=
operator|(
name|TypeAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
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
if|if
condition|(
name|startOffsets
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|endOffsets
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
name|assertEquals
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|,
name|types
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
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|)
expr_stmt|;
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
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param args    * @throws IOException    */
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
throws|throws
name|IOException
block|{
operator|new
name|TestSmartChineseAnalyzer
argument_list|()
operator|.
name|sampleMethod
argument_list|()
expr_stmt|;
block|}
comment|/**    * @throws UnsupportedEncodingException    * @throws FileNotFoundException    * @throws IOException    */
DECL|method|sampleMethod
specifier|private
name|void
name|sampleMethod
parameter_list|()
throws|throws
name|UnsupportedEncodingException
throws|,
name|FileNotFoundException
throws|,
name|IOException
block|{
name|Token
name|nt
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|Analyzer
name|ca
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Reader
name|sentence
init|=
operator|new
name|StringReader
argument_list|(
literal|"æä»å°å°±ä¸ç±èªä¸»å°è®¤ä¸ºèªå·±é¿å¤§ä»¥åä¸å®å¾æä¸ºä¸ä¸ªè±¡æç¶äº²ä¸æ ·çç»å®¶, å¯è½æ¯ç¶æ¯æ½ç§»é»åçå½±åãå¶å®ææ ¹æ¬ä¸ç¥éä½ä¸ºç»å®¶æå³çä»ä¹ï¼ææ¯å¦åæ¬¢ï¼æéè¦çæ¯å¦éåæï¼ææ¯å¦æè¿ä¸ªæåãå¶å®äººå°ä¸­å¹´çæè¿æ¯ä¸ç¡®å®ææåæ¬¢ä»ä¹ï¼ææ³åçæ¯ä»ä¹ï¼æç¸ä¿¡å¾å¤äººåæä¸æ ·æåæ ·çç¦æ¼ãæ¯ç«ä¸æ¯æ¯ä¸ªäººé½è½æä¸ºä½æéçå®èªåï¼ç§å­¦å®¶åå¤§ææãç¥éèªå·±éååä»ä¹ï¼åæ¬¢åä»ä¹ï¼è½åå¥½ä»ä¹å¶å®æ¯ä¸ªéå¸¸å°é¾çé®é¢ã"
operator|+
literal|"å¹¸è¿çæ¯ï¼ææ³æçå­©å­ä¸ä¼ä¸ºè¿ä¸ªå¤ªè¿ç¦æ¼ãéè¿èå¤§ï¼ææ¢æ¢åç°ç¾å½é«ä¸­çä¸ä¸ªéè¦åè½å°±æ¯å¸®å©å­¦çåæä»ä»¬çä¸é¿åå´è¶£ï¼ä»èå¸®å©ä»ä»¬éæ©å¤§å­¦çä¸ä¸åæªæ¥çèä¸ãæè§å¾å¸®å©ä¸ä¸ªæªæå½¢çå­©å­æ¾å°å¥¹æªæ¥æé¿çæ¹åæ¯ä¸ªéå¸¸éè¦çè¿ç¨ã"
operator|+
literal|"ç¾å½é«ä¸­é½æä¸é¨çèä¸é¡¾é®ï¼éè¿æ¥è§¦ä¸åçè¯¾ç¨ï¼ååç§å¿çï¼ä¸ªæ§ï¼å´è¶£å¾å¤æ¹é¢çé®ç­æ¥å¸®å©æ¯ä¸ªå­¦çæ¾å°ææå´è¶£çä¸ä¸ãè¿æ ·çæè²ä¸è¬æ¯è¦å°é«å¹´çº§æå¼å§ï¼ å¯èå¤§å ä¸ºä»å¹´ä¸è®¡ç®æºçè¯¾ç¨å°±æ¯ç ç©¶ä¸ä¸ªèä¸èµ°åçè½¯ä»¶é¡¹ç®ï¼æä»¥å¥¹æååäºè¿äºèè¯åé¢è¯ãçæ¥ä»¥åè¿æ ·çæè²ä¼æ¢æ¢ç±çµèæ¥æµè¯äºãèå¤§å¸¦åå®¶äºä¸äºè¯å·ï¼ææåºä¸äºç»å¤§å®¶ççãè¿é¨è¯¾å¥¹è±äº2ä¸ªå¤ææåå®ï¼è¿éåªæ¯å¾å°çä¸é¨åã"
operator|+
literal|"å¨æµè¯éæè¿æ ·çä¸äºé®é¢ï¼"
operator|+
literal|"ä½ æ¯ä¸ªåæ¬¢å¨æçäººåï¼ ä½ åæ¬¢ä¿®ä¸è¥¿åï¼ä½ åæ¬¢ä½è²è¿å¨åï¼ä½ åæ¬¢å¨å®¤å¤å·¥ä½åï¼ä½ æ¯ä¸ªåæ¬¢æèçäººåï¼ä½ åæ¬¢æ°å­¦åç§å­¦è¯¾åï¼ä½ åæ¬¢ä¸ä¸ªäººå·¥ä½åï¼ä½ å¯¹èªå·±çæºåèªä¿¡åï¼ä½ çåé è½åå¾å¼ºåï¼ä½ åæ¬¢èºæ¯ï¼é³ä¹åæå§åï¼  ä½ åæ¬¢èªç±èªå¨çå·¥ä½ç¯å¢åï¼ä½ åæ¬¢å°è¯æ°çä¸è¥¿åï¼ ä½ åæ¬¢å¸®å©å«äººåï¼ä½ åæ¬¢æå«äººåï¼ä½ åæ¬¢åæºå¨åå·¥å·æäº¤éåï¼ä½ åæ¬¢å½é¢å¯¼åï¼ä½ åæ¬¢ç»ç»æ´»å¨åï¼ä½ ä»ä¹åæ°å­æäº¤éåï¼"
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|ca
operator|.
name|tokenStream
argument_list|(
literal|"sentence"
argument_list|,
name|sentence
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"start: "
operator|+
operator|(
operator|new
name|Date
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|long
name|before
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|nt
operator|=
name|ts
operator|.
name|next
argument_list|(
name|nt
argument_list|)
expr_stmt|;
while|while
condition|(
name|nt
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|nt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|nt
operator|=
name|ts
operator|.
name|next
argument_list|(
name|nt
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"time: "
operator|+
operator|(
name|now
operator|-
name|before
operator|)
operator|/
literal|1000.0
operator|+
literal|" s"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

