begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.bg
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|bg
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
name|util
operator|.
name|CharArraySet
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
comment|/**  * Test the Bulgarian analyzer  */
end_comment

begin_class
DECL|class|TestBulgarianAnalyzer
specifier|public
class|class
name|TestBulgarianAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * This test fails with NPE when the stopwords file is missing in classpath    */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopwords
specifier|public
name|void
name|testStopwords
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐÐ°Ðº ÑÐµ ÐºÐ°Ð·Ð²Ð°Ñ?"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ°Ð·Ð²Ð°Ñ"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomStopwords
specifier|public
name|void
name|testCustomStopwords
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐÐ°Ðº ÑÐµ ÐºÐ°Ð·Ð²Ð°Ñ?"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ°Ðº"
block|,
literal|"ÑÐµ"
block|,
literal|"ÐºÐ°Ð·Ð²Ð°Ñ"
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
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"Ð´Ð¾ÐºÑÐ¼ÐµÐ½ÑÐ¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´Ð¾ÐºÑÐ¼ÐµÐ½Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"Ð´Ð¾ÐºÑÐ¼ÐµÐ½Ñ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´Ð¾ÐºÑÐ¼ÐµÐ½Ñ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test some examples from the paper    */
DECL|method|testBasicExamples
specifier|public
name|void
name|testBasicExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐµÐ½ÐµÑÐ³Ð¸Ð¹Ð½Ð¸ ÐºÑÐ¸Ð·Ð¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐµÐ½ÐµÑÐ³Ð¸Ð¹Ð½"
block|,
literal|"ÐºÑÐ¸Ð·"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐÑÐ¾Ð¼Ð½Ð°ÑÐ° ÐµÐ½ÐµÑÐ³Ð¸Ñ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð°ÑÐ¾Ð¼Ð½"
block|,
literal|"ÐµÐ½ÐµÑÐ³"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¼Ð¿ÑÑÑÐ¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¼Ð¿ÑÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¼Ð¿ÑÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¼Ð¿ÑÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´Ð¾Ð²Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithStemExclusionSet
specifier|public
name|void
name|testWithStemExclusionSet
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"ÑÑÑÐ¾ÐµÐ²Ðµ"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÑÑÐ¾ÐµÐ²ÐµÑÐµ ÑÑÑÐ¾ÐµÐ²Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑÐ¾Ð¹"
block|,
literal|"ÑÑÑÐ¾ÐµÐ²Ðµ"
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
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
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

