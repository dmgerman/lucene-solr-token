begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
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
operator|.
name|smart
package|;
end_package

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
name|KeywordTokenizer
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
name|miscellaneous
operator|.
name|ASCIIFoldingFilter
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

begin_class
DECL|class|TestSmartChineseAnalyzer
specifier|public
class|class
name|TestSmartChineseAnalyzer
extends|extends
name|BaseTokenStreamTestCase
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
comment|// set stop-words from the outer world - must yield same behavior
name|ca
operator|=
operator|new
name|SmartChineseAnalyzer
argument_list|(
name|SmartChineseAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|)
expr_stmt|;
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
index|[]
name|analyzers
init|=
operator|new
name|Analyzer
index|[]
block|{
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|false
argument_list|)
block|,
comment|/* doesn't load stopwords */
operator|new
name|SmartChineseAnalyzer
argument_list|(
literal|null
argument_list|)
comment|/* sets stopwords to empty set */
block|}
decl_stmt|;
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
for|for
control|(
name|Analyzer
name|analyzer
range|:
name|analyzers
control|)
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Check that position increments after stopwords are correct,    * when stopfilter is configured with enablePositionIncrements    */
DECL|method|testChineseStopWords2
specifier|public
name|void
name|testChineseStopWords2
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
literal|"Title:San"
decl_stmt|;
comment|// : is a stopword
name|String
name|result
index|[]
init|=
block|{
literal|"titl"
block|,
literal|"san"
block|}
decl_stmt|;
name|int
name|startOffsets
index|[]
init|=
block|{
literal|0
block|,
literal|6
block|}
decl_stmt|;
name|int
name|endOffsets
index|[]
init|=
block|{
literal|5
block|,
literal|9
block|}
decl_stmt|;
name|int
name|posIncr
index|[]
init|=
block|{
literal|1
block|,
literal|2
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|ca
argument_list|,
name|sentence
argument_list|,
name|result
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
name|posIncr
argument_list|)
expr_stmt|;
block|}
DECL|method|testChineseAnalyzer
specifier|public
name|void
name|testChineseAnalyzer
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
literal|true
argument_list|)
decl_stmt|;
name|String
name|sentence
init|=
literal|"æè´­ä¹°äºéå·åæè£ã"
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
name|assertAnalyzesTo
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
name|assertAnalyzesTo
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
comment|// LUCENE-3026
DECL|method|testLargeDocument
specifier|public
name|void
name|testLargeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"æè´­ä¹°äºéå·åæè£ã"
argument_list|)
expr_stmt|;
block|}
name|Analyzer
name|analyzer
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|()
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
literal|""
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
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
block|{       }
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-3026
DECL|method|testLargeSentence
specifier|public
name|void
name|testLargeSentence
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"æè´­ä¹°äºéå·åæè£"
argument_list|)
expr_stmt|;
block|}
name|Analyzer
name|analyzer
init|=
operator|new
name|SmartChineseAnalyzer
argument_list|()
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
literal|""
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
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
block|{       }
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
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
name|SmartChineseAnalyzer
argument_list|()
argument_list|,
literal|1000
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
name|SmartChineseAnalyzer
argument_list|()
argument_list|,
literal|100
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

