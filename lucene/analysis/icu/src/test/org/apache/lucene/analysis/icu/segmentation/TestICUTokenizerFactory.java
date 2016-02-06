begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
package|;
end_package

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|util
operator|.
name|ClasspathResourceLoader
import|;
end_import

begin_comment
comment|/** basic tests for {@link ICUTokenizerFactory} **/
end_comment

begin_class
DECL|class|TestICUTokenizerFactory
specifier|public
class|class
name|TestICUTokenizerFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testMixedText
specifier|public
name|void
name|testMixedText
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ  This is a test àºàº§à»àº²àºàº­àº"
argument_list|)
decl_stmt|;
name|ICUTokenizerFactory
name|factory
init|=
operator|new
name|ICUTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|,
literal|"This"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|,
literal|"àºàº§à»àº²"
block|,
literal|"àºàº­àº"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenizeLatinOnWhitespaceOnly
specifier|public
name|void
name|testTokenizeLatinOnWhitespaceOnly
parameter_list|()
throws|throws
name|Exception
block|{
comment|// â U+201C LEFT DOUBLE QUOTATION MARK; â U+201D RIGHT DOUBLE QUOTATION MARK
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"  Don't,break.at?/(punct)!  \u201Cnice\u201D\r\n\r\n85_At:all; `really\" +2=3$5,&813 !@#%$^)(*@#$   "
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|ICUTokenizerFactory
operator|.
name|RULEFILES
argument_list|,
literal|"Latn:Latin-break-only-on-whitespace.rbbi"
argument_list|)
expr_stmt|;
name|ICUTokenizerFactory
name|factory
init|=
operator|new
name|ICUTokenizerFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Don't,break.at?/(punct)!"
block|,
literal|"\u201Cnice\u201D"
block|,
literal|"85_At:all;"
block|,
literal|"`really\""
block|,
literal|"+2=3$5,&813"
block|,
literal|"!@#%$^)(*@#$"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<NUM>"
block|,
literal|"<OTHER>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenizeLatinDontBreakOnHyphens
specifier|public
name|void
name|testTokenizeLatinDontBreakOnHyphens
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"One-two punch.  Brang-, not brung-it.  This one--not that one--is the right one, -ish."
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|ICUTokenizerFactory
operator|.
name|RULEFILES
argument_list|,
literal|"Latn:Latin-dont-break-on-hyphens.rbbi"
argument_list|)
expr_stmt|;
name|ICUTokenizerFactory
name|factory
init|=
operator|new
name|ICUTokenizerFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"One-two"
block|,
literal|"punch"
block|,
literal|"Brang"
block|,
literal|"not"
block|,
literal|"brung-it"
block|,
literal|"This"
block|,
literal|"one"
block|,
literal|"not"
block|,
literal|"that"
block|,
literal|"one"
block|,
literal|"is"
block|,
literal|"the"
block|,
literal|"right"
block|,
literal|"one"
block|,
literal|"ish"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Specify more than one script/rule file pair.    * Override default DefaultICUTokenizerConfig Thai script tokenization.    * Use the same rule file for both scripts.    */
DECL|method|testKeywordTokenizeCyrillicAndThai
specifier|public
name|void
name|testKeywordTokenizeCyrillicAndThai
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Some English.  ÐÐµÐ¼Ð½Ð¾Ð³Ð¾ ÑÑÑÑÐºÐ¸Ð¹.  à¸à¹à¸­à¸à¸§à¸²à¸¡à¸ à¸²à¸©à¸²à¹à¸à¸¢à¹à¸¥à¹à¸ à¹ à¸à¹à¸­à¸¢ à¹  More English."
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|ICUTokenizerFactory
operator|.
name|RULEFILES
argument_list|,
literal|"Cyrl:KeywordTokenizer.rbbi,Thai:KeywordTokenizer.rbbi"
argument_list|)
expr_stmt|;
name|ICUTokenizerFactory
name|factory
init|=
operator|new
name|ICUTokenizerFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Some"
block|,
literal|"English"
block|,
literal|"ÐÐµÐ¼Ð½Ð¾Ð³Ð¾ ÑÑÑÑÐºÐ¸Ð¹.  "
block|,
literal|"à¸à¹à¸­à¸à¸§à¸²à¸¡à¸ à¸²à¸©à¸²à¹à¸à¸¢à¹à¸¥à¹à¸ à¹ à¸à¹à¸­à¸¢ à¹  "
block|,
literal|"More"
block|,
literal|"English"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|ICUTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

