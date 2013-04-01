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

begin_class
DECL|class|TestCapitalizationFilterFactory
specifier|public
class|class
name|TestCapitalizationFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testCapitalization
specifier|public
name|void
name|testCapitalization
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
literal|"kiTTEN"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
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
literal|"Kitten"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization2
specifier|public
name|void
name|testCapitalization2
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
literal|"and"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"And"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** first is forced, but it's not a keep word, either */
DECL|method|testCapitalization3
specifier|public
name|void
name|testCapitalization3
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
literal|"AnD"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"And"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization4
specifier|public
name|void
name|testCapitalization4
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
literal|"AnD"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"false"
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
literal|"And"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization5
specifier|public
name|void
name|testCapitalization5
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
literal|"big"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"Big"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization6
specifier|public
name|void
name|testCapitalization6
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
literal|"BIG"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"BIG"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization7
specifier|public
name|void
name|testCapitalization7
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
literal|"Hello thEre my Name is Ryan"
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
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"Hello there my name is ryan"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization8
specifier|public
name|void
name|testCapitalization8
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
literal|"Hello thEre my Name is Ryan"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"false"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"Hello"
block|,
literal|"There"
block|,
literal|"My"
block|,
literal|"Name"
block|,
literal|"Is"
block|,
literal|"Ryan"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization9
specifier|public
name|void
name|testCapitalization9
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
literal|"Hello thEre my Name is Ryan"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"false"
argument_list|,
literal|"minWordLength"
argument_list|,
literal|"3"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"Hello"
block|,
literal|"There"
block|,
literal|"my"
block|,
literal|"Name"
block|,
literal|"is"
block|,
literal|"Ryan"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization10
specifier|public
name|void
name|testCapitalization10
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
literal|"McKinley"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"false"
argument_list|,
literal|"minWordLength"
argument_list|,
literal|"3"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"Mckinley"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** using "McK" as okPrefix */
DECL|method|testCapitalization11
specifier|public
name|void
name|testCapitalization11
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
literal|"McKinley"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"false"
argument_list|,
literal|"minWordLength"
argument_list|,
literal|"3"
argument_list|,
literal|"okPrefix"
argument_list|,
literal|"McK"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"McKinley"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** test with numbers */
DECL|method|testCapitalization12
specifier|public
name|void
name|testCapitalization12
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
literal|"1st 2nd third"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"false"
argument_list|,
literal|"minWordLength"
argument_list|,
literal|"3"
argument_list|,
literal|"okPrefix"
argument_list|,
literal|"McK"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"false"
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
literal|"1st"
block|,
literal|"2nd"
block|,
literal|"Third"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCapitalization13
specifier|public
name|void
name|testCapitalization13
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
literal|"the The the"
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
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"and the it BIG"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"false"
argument_list|,
literal|"minWordLength"
argument_list|,
literal|"3"
argument_list|,
literal|"okPrefix"
argument_list|,
literal|"McK"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"The The the"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeepIgnoreCase
specifier|public
name|void
name|testKeepIgnoreCase
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
literal|"kiTTEN"
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
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"kitten"
argument_list|,
literal|"keepIgnoreCase"
argument_list|,
literal|"true"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"KiTTEN"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeepIgnoreCase2
specifier|public
name|void
name|testKeepIgnoreCase2
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
literal|"kiTTEN"
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
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"kitten"
argument_list|,
literal|"keepIgnoreCase"
argument_list|,
literal|"true"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"false"
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
literal|"kiTTEN"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeepIgnoreCase3
specifier|public
name|void
name|testKeepIgnoreCase3
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
literal|"kiTTEN"
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
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Capitalization"
argument_list|,
literal|"keepIgnoreCase"
argument_list|,
literal|"true"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"false"
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
literal|"Kitten"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CapitalizationFilterFactory's minWordLength option.    *     * This is very weird when combined with ONLY_FIRST_WORD!!!    */
DECL|method|testMinWordLength
specifier|public
name|void
name|testMinWordLength
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
literal|"helo testing"
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
literal|"Capitalization"
argument_list|,
literal|"onlyFirstWord"
argument_list|,
literal|"true"
argument_list|,
literal|"minWordLength"
argument_list|,
literal|"5"
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
literal|"helo"
block|,
literal|"Testing"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CapitalizationFilterFactory's maxWordCount option with only words of 1    * in each token (it should do nothing)    */
DECL|method|testMaxWordCount
specifier|public
name|void
name|testMaxWordCount
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
literal|"one two three four"
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
literal|"Capitalization"
argument_list|,
literal|"maxWordCount"
argument_list|,
literal|"2"
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
literal|"One"
block|,
literal|"Two"
block|,
literal|"Three"
block|,
literal|"Four"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CapitalizationFilterFactory's maxWordCount option when exceeded    */
DECL|method|testMaxWordCount2
specifier|public
name|void
name|testMaxWordCount2
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
literal|"one two three four"
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
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Capitalization"
argument_list|,
literal|"maxWordCount"
argument_list|,
literal|"2"
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
literal|"one two three four"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CapitalizationFilterFactory's maxTokenLength option when exceeded    *     * This is weird, it is not really a max, but inclusive (look at 'is')    */
DECL|method|testMaxTokenLength
specifier|public
name|void
name|testMaxTokenLength
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
literal|"this is a test"
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
literal|"Capitalization"
argument_list|,
literal|"maxTokenLength"
argument_list|,
literal|"2"
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
literal|"this"
block|,
literal|"is"
block|,
literal|"A"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CapitalizationFilterFactory's forceFirstLetter option    */
DECL|method|testForceFirstLetterWithKeep
specifier|public
name|void
name|testForceFirstLetterWithKeep
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
literal|"kitten"
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
literal|"Capitalization"
argument_list|,
literal|"keep"
argument_list|,
literal|"kitten"
argument_list|,
literal|"forceFirstLetter"
argument_list|,
literal|"true"
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
literal|"Kitten"
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
literal|"Capitalization"
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

