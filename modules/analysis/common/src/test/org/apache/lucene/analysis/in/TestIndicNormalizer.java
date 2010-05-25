begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.in
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|in
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
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/**  * Test IndicNormalizer  */
end_comment

begin_class
DECL|class|TestIndicNormalizer
specifier|public
class|class
name|TestIndicNormalizer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Test some basic normalization    */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"à¤à¤¾à¥à¤à¤¾à¥"
argument_list|,
literal|"à¤à¤"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤¾à¥à¤à¤¾à¥"
argument_list|,
literal|"à¤à¤"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤¾à¥à¤à¤¾à¥"
argument_list|,
literal|"à¤à¤"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤¾à¥à¤à¤¾à¥"
argument_list|,
literal|"à¤à¤"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤¾à¤à¤¾"
argument_list|,
literal|"à¤à¤"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"à¤à¤¾à¥à¤°"
argument_list|,
literal|"à¤à¤°"
argument_list|)
expr_stmt|;
comment|// khanda-ta
name|check
argument_list|(
literal|"à¦¤à§â"
argument_list|,
literal|"à§"
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|Tokenizer
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
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|TokenFilter
name|tf
init|=
operator|new
name|IndicNormalizationFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
name|output
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

