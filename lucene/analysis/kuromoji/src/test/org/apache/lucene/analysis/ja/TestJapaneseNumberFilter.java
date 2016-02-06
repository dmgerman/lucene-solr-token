begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
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
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestJapaneseNumberFilter
specifier|public
class|class
name|TestJapaneseNumberFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
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
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|JapaneseNumberFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
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
name|analyzer
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
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"æ¬æ¥åä¸äºåäºç¾åã®ã¯ã¤ã³ãè²·ã£ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ¬æ¥"
block|,
literal|"102500"
block|,
literal|"å"
block|,
literal|"ã®"
block|,
literal|"ã¯ã¤ã³"
block|,
literal|"ã"
block|,
literal|"è²·ã£"
block|,
literal|"ã"
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
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|13
block|,
literal|14
block|,
literal|16
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|13
block|,
literal|14
block|,
literal|16
block|,
literal|17
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"æ¨æ¥ã®ãå¯¿å¸ã¯ï¼ï¼ä¸åã§ããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ¨æ¥"
block|,
literal|"ã®"
block|,
literal|"ã"
block|,
literal|"å¯¿å¸"
block|,
literal|"ã¯"
block|,
literal|"100000"
block|,
literal|"å"
block|,
literal|"ã§ã"
block|,
literal|"ã"
block|,
literal|"ã"
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
literal|3
block|,
literal|4
block|,
literal|6
block|,
literal|7
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
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|6
block|,
literal|7
block|,
literal|10
block|,
literal|11
block|,
literal|13
block|,
literal|14
block|,
literal|15
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ã¢ãã£ãªã«ã®è³æ¬éã¯ï¼ï¼ï¼ä¸åã§ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã¢ãã£ãªã«"
block|,
literal|"ã®"
block|,
literal|"è³æ¬"
block|,
literal|"é"
block|,
literal|"ã¯"
block|,
literal|"6000000"
block|,
literal|"å"
block|,
literal|"ã§ã"
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
literal|6
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|14
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|6
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|14
block|,
literal|15
block|,
literal|17
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVariants
specifier|public
name|void
name|testVariants
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test variants of three
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
comment|// Test three variations with trailing zero
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"03"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"003"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼ï¼ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ããä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3"
block|}
argument_list|)
expr_stmt|;
comment|// Test thousand variants
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"1å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼ï¼ç¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
comment|// Strange, but supported
block|}
annotation|@
name|Test
DECL|method|testLargeVariants
specifier|public
name|void
name|testLargeVariants
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test large numbers
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸äºä¸å«ä¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"35789"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"å­ç¾äºä¸äºåä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"6025001"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"åå­ç¾ä¸äºåä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000006005001"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ååå­ç¾ä¸äºåä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"10000006005001"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸äº¬ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"10000000000000001"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"åäº¬å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"100000000000000010"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"åäº¬ååä¸åç¾åä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"100010001000100011111"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNegative
specifier|public
name|void
name|testNegative
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"-100ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-"
block|,
literal|"1000000"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMixed
specifier|public
name|void
name|testMixed
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test mixed numbers
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸å2ç¾ï¼åä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3223"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼ï¼äºä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3223"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNininsankyaku
specifier|public
name|void
name|testNininsankyaku
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Unstacked tokens
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"äº"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"äºäºº"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2"
block|,
literal|"äºº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"äºäººä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2"
block|,
literal|"äºº"
block|,
literal|"3"
block|}
argument_list|)
expr_stmt|;
comment|// Stacked tokens - emit tokens as they are
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"äºäººä¸è"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"äº"
block|,
literal|"äºäººä¸è"
block|,
literal|"äºº"
block|,
literal|"ä¸"
block|,
literal|"è"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFujiyaichinisanu
specifier|public
name|void
name|testFujiyaichinisanu
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Stacked tokens with a numeral partial
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸äºå®¶ä¸äºä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"ä¸äºå®¶"
block|,
literal|"äº"
block|,
literal|"å®¶"
block|,
literal|"123"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunny
specifier|public
name|void
name|testFunny
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test some oddities for inconsistent input
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"åå"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"20"
block|}
argument_list|)
expr_stmt|;
comment|// 100?
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ç¾ç¾ç¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"300"
block|}
argument_list|)
expr_stmt|;
comment|// 10,000?
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"åååå"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"4000"
block|}
argument_list|)
expr_stmt|;
comment|// 1,000,000,000,000?
block|}
annotation|@
name|Test
DECL|method|testKanjiArabic
specifier|public
name|void
name|testKanjiArabic
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test kanji numerals used as Arabic numbers (with head zero)
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãä¸äºä¸åäºå­ä¸å«ä¹ä¹å«ä¸å­äºåä¸äºä¸ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1234567899876543210"
block|}
argument_list|)
expr_stmt|;
comment|// I'm Bond, James "normalized" Bond...
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ããä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"7"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDoubleZero
specifier|public
name|void
name|testDoubleZero
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"0"
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
literal|2
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
annotation|@
name|Test
DECL|method|testName
specifier|public
name|void
name|testName
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test name that normalises to number
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ç°ä¸­äº¬ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ç°ä¸­"
block|,
literal|"10000000000000001"
block|}
argument_list|,
comment|// äº¬ä¸ is normalized to a number
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
argument_list|)
expr_stmt|;
comment|// An analyzer that marks äº¬ä¸ as a keyword
name|Analyzer
name|keywordMarkingAnalyzer
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
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"äº¬ä¸"
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|JapaneseNumberFilter
argument_list|(
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|tokenizer
argument_list|,
name|set
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordMarkingAnalyzer
argument_list|,
literal|"ç°ä¸­äº¬ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ç°ä¸­"
block|,
literal|"äº¬ä¸"
block|}
argument_list|,
comment|// äº¬ä¸ is not normalized
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
argument_list|)
expr_stmt|;
name|keywordMarkingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDecimal
specifier|public
name|void
name|testDecimal
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test Arabic numbers with punctuation, i.e. 3.2 thousands
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼ï¼ï¼ä¸ï¼ï¼ï¼ï¼ï¼ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"12345.67"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDecimalPunctuation
specifier|public
name|void
name|testDecimalPunctuation
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test Arabic numbers with punctuation, i.e. 3.2 thousands yen
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼ï¼ï¼åå"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"3200"
block|,
literal|"å"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThousandSeparator
specifier|public
name|void
name|testThousandSeparator
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"4,647"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"4647"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDecimalThousandSeparator
specifier|public
name|void
name|testDecimalThousandSeparator
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"4,647.0010"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"4647.001"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommaDecimalSeparator
specifier|public
name|void
name|testCommaDecimalSeparator
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"15,7"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"157"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrailingZeroStripping
specifier|public
name|void
name|testTrailingZeroStripping
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"1000.1000"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000.1"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"1000.0000"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1000"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|50
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandomSmallStrings
specifier|public
name|void
name|testRandomSmallStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|500
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|128
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFunnyIssue
specifier|public
name|void
name|testFunnyIssue
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
literal|"ãã\u302f\u3029\u3039\u3023\u3033\u302bB"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"This test is used during development when analyze normalizations in large amounts of text"
argument_list|)
annotation|@
name|Test
DECL|method|testLargeData
specifier|public
name|void
name|testLargeData
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|input
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"/tmp/test.txt"
argument_list|)
decl_stmt|;
name|Path
name|tokenizedOutput
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"/tmp/test.tok.txt"
argument_list|)
decl_stmt|;
name|Path
name|normalizedOutput
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"/tmp/test.norm.txt"
argument_list|)
decl_stmt|;
name|Analyzer
name|plainAnalyzer
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
name|tokenizer
init|=
operator|new
name|JapaneseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|analyze
argument_list|(
name|plainAnalyzer
argument_list|,
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|input
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|tokenizedOutput
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|analyze
argument_list|(
name|analyzer
argument_list|,
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|input
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|normalizedOutput
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|plainAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|analyze
specifier|public
name|void
name|analyze
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CharTermAttribute
name|termAttr
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|termAttr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

