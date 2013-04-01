begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
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
name|StringMockResourceLoader
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure the keyword marker filter factory is working.  */
end_comment

begin_class
DECL|class|TestKeywordMarkerFilterFactory
specifier|public
class|class
name|TestKeywordMarkerFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testKeywords
specifier|public
name|void
name|testKeywords
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
literal|"dogs cats"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"cats"
argument_list|)
argument_list|,
literal|"protected"
argument_list|,
literal|"protwords.txt"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"PorterStem"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
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
literal|"dog"
block|,
literal|"cats"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeywords2
specifier|public
name|void
name|testKeywords2
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
literal|"dogs cats"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
literal|"pattern"
argument_list|,
literal|"cats|Dogs"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"PorterStem"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
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
literal|"dog"
block|,
literal|"cats"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeywordsMixed
specifier|public
name|void
name|testKeywordsMixed
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
literal|"dogs cats birds"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"cats"
argument_list|)
argument_list|,
literal|"protected"
argument_list|,
literal|"protwords.txt"
argument_list|,
literal|"pattern"
argument_list|,
literal|"birds|Dogs"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"PorterStem"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
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
literal|"dog"
block|,
literal|"cats"
block|,
literal|"birds"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeywordsCaseInsensitive
specifier|public
name|void
name|testKeywordsCaseInsensitive
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
literal|"dogs cats Cats"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"cats"
argument_list|)
argument_list|,
literal|"protected"
argument_list|,
literal|"protwords.txt"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"PorterStem"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
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
literal|"dog"
block|,
literal|"cats"
block|,
literal|"Cats"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeywordsCaseInsensitive2
specifier|public
name|void
name|testKeywordsCaseInsensitive2
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
literal|"dogs cats Cats"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
literal|"pattern"
argument_list|,
literal|"Cats"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"PorterStem"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
empty_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dog"
block|,
literal|"cats"
block|,
literal|"Cats"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeywordsCaseInsensitiveMixed
specifier|public
name|void
name|testKeywordsCaseInsensitiveMixed
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
literal|"dogs cats Cats Birds birds"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"cats"
argument_list|)
argument_list|,
literal|"protected"
argument_list|,
literal|"protwords.txt"
argument_list|,
literal|"pattern"
argument_list|,
literal|"birds"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"PorterStem"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
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
literal|"dog"
block|,
literal|"cats"
block|,
literal|"Cats"
block|,
literal|"Birds"
block|,
literal|"birds"
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
name|tokenFilterFactory
argument_list|(
literal|"KeywordMarker"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
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

