begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
package|;
end_package

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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_class
DECL|class|TestCJKBigramFilter
specifier|public
class|class
name|TestCJKBigramFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
DECL|field|unibiAnalyzer
name|Analyzer
name|analyzer
decl_stmt|,
name|unibiAnalyzer
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
name|t
init|=
operator|new
name|StandardTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|CJKBigramFilter
argument_list|(
name|t
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|unibiAnalyzer
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
name|t
init|=
operator|new
name|StandardTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|CJKBigramFilter
argument_list|(
name|t
argument_list|,
literal|0xff
argument_list|,
literal|true
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
name|IOUtils
operator|.
name|close
argument_list|(
name|analyzer
argument_list|,
name|unibiAnalyzer
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testHuge
specifier|public
name|void
name|testHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHanOnly
specifier|public
name|void
name|testHanOnly
parameter_list|()
throws|throws
name|Exception
block|{
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
name|StandardTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|CJKBigramFilter
argument_list|(
name|t
argument_list|,
name|CJKBigramFilter
operator|.
name|HAN
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤"
block|,
literal|"ã"
block|,
literal|"ã®"
block|,
literal|"å­¦ç"
block|,
literal|"ã"
block|,
literal|"è©¦é¨"
block|,
literal|"ã«"
block|,
literal|"è½"
block|,
literal|"ã¡"
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
literal|1
block|,
literal|2
block|,
literal|3
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
literal|11
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
literal|11
block|,
literal|12
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<SINGLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<SINGLE>"
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
literal|1
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
literal|1
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAllScripts
specifier|public
name|void
name|testAllScripts
parameter_list|()
throws|throws
name|Exception
block|{
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
name|StandardTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|CJKBigramFilter
argument_list|(
name|t
argument_list|,
literal|0xff
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testUnigramsAndBigramsAllScripts
specifier|public
name|void
name|testUnigramsAndBigramsAllScripts
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|unibiAnalyzer
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
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
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|7
block|,
literal|8
block|,
literal|8
block|,
literal|9
block|,
literal|9
block|,
literal|10
block|,
literal|10
block|,
literal|11
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
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|7
block|,
literal|8
block|,
literal|8
block|,
literal|9
block|,
literal|9
block|,
literal|10
block|,
literal|10
block|,
literal|11
block|,
literal|11
block|,
literal|12
block|,
literal|12
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
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
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
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
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnigramsAndBigramsHanOnly
specifier|public
name|void
name|testUnigramsAndBigramsHanOnly
parameter_list|()
throws|throws
name|Exception
block|{
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
name|StandardTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|CJKBigramFilter
argument_list|(
name|t
argument_list|,
name|CJKBigramFilter
operator|.
name|HAN
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤"
block|,
literal|"ã"
block|,
literal|"ã®"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"ã"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"ã«"
block|,
literal|"è½"
block|,
literal|"ã¡"
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
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
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
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<SINGLE>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<HIRAGANA>"
block|,
literal|"<SINGLE>"
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
literal|1
block|,
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
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
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
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testUnigramsAndBigramsHuge
specifier|public
name|void
name|testUnigramsAndBigramsHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|unibiAnalyzer
argument_list|,
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
operator|+
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|,
literal|"ãå¤"
block|,
literal|"å¤"
block|,
literal|"å¤ã"
block|,
literal|"ã"
block|,
literal|"ãã®"
block|,
literal|"ã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"çã"
block|,
literal|"ã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡"
block|,
literal|"ã¡ã"
block|,
literal|"ã"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomUnibiStrings
specifier|public
name|void
name|testRandomUnibiStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|unibiAnalyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomUnibiHugeStrings
specifier|public
name|void
name|testRandomUnibiHugeStrings
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
name|unibiAnalyzer
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

