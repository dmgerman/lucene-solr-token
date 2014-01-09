begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Test the Persian Normalization Filter  *   */
end_comment

begin_class
DECL|class|TestPersianNormalizationFilter
specifier|public
class|class
name|TestPersianNormalizationFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testFarsiYeh
specifier|public
name|void
name|testFarsiYeh
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§Û"
argument_list|,
literal|"ÙØ§Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testYehBarree
specifier|public
name|void
name|testYehBarree
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§Û"
argument_list|,
literal|"ÙØ§Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeheh
specifier|public
name|void
name|testKeheh
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ú©Ø´Ø§ÙØ¯Ù"
argument_list|,
literal|"ÙØ´Ø§ÙØ¯Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHehYeh
specifier|public
name|void
name|testHehYeh
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØªØ§Ø¨Û"
argument_list|,
literal|"ÙØªØ§Ø¨Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHehHamzaAbove
specifier|public
name|void
name|testHehHamzaAbove
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØªØ§Ø¨ÙÙ"
argument_list|,
literal|"ÙØªØ§Ø¨Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHehGoal
specifier|public
name|void
name|testHehGoal
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø²Ø§Ø¯Û"
argument_list|,
literal|"Ø²Ø§Ø¯Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|MockTokenizer
name|tokenStream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|PersianNormalizationFilter
name|filter
init|=
operator|new
name|PersianNormalizationFilter
argument_list|(
name|tokenStream
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
name|expected
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
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
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|PersianNormalizationFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

