begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Tokenizer
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

begin_comment
comment|/**  * Tests for {@link TestKuromojiReadingFormFilter}  */
end_comment

begin_class
DECL|class|TestKuromojiReadingFormFilter
specifier|public
class|class
name|TestKuromojiReadingFormFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|katakanaAnalyzer
specifier|private
name|Analyzer
name|katakanaAnalyzer
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
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KuromojiTokenizer
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|KuromojiTokenizer
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
name|KuromojiReadingFormFilter
argument_list|(
name|tokenizer
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|romajiAnalyzer
specifier|private
name|Analyzer
name|romajiAnalyzer
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
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KuromojiTokenizer
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|KuromojiTokenizer
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
name|KuromojiReadingFormFilter
argument_list|(
name|tokenizer
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|testKatakanaReadings
specifier|public
name|void
name|testKatakanaReadings
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|katakanaAnalyzer
argument_list|,
literal|"ä»å¤ã¯ã­ãã¼ãåçã¨è©±ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã³ã³ã¤"
block|,
literal|"ã"
block|,
literal|"ã­ãã¼ã"
block|,
literal|"ã»ã³ã»ã¤"
block|,
literal|"ã"
block|,
literal|"ããã·"
block|,
literal|"ã¿"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRomajiReadings
specifier|public
name|void
name|testRomajiReadings
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|romajiAnalyzer
argument_list|,
literal|"ä»å¤ã¯ã­ãã¼ãåçã¨è©±ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kon'ya"
block|,
literal|"ha"
block|,
literal|"robato"
block|,
literal|"sensei"
block|,
literal|"to"
block|,
literal|"hanashi"
block|,
literal|"ta"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomData
specifier|public
name|void
name|testRandomData
parameter_list|()
throws|throws
name|IOException
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|katakanaAnalyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|romajiAnalyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

