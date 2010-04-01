begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|TestCapitalizationFilter
specifier|public
class|class
name|TestCapitalizationFilter
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testCapitalization
specifier|public
name|void
name|testCapitalization
parameter_list|()
throws|throws
name|Exception
block|{
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|KEEP
argument_list|,
literal|"and the it BIG"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|ONLY_FIRST_WORD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|char
index|[]
name|termBuffer
decl_stmt|;
name|termBuffer
operator|=
literal|"kiTTEN"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Kitten"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|true
expr_stmt|;
name|termBuffer
operator|=
literal|"and"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"And"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|//first is forced
name|termBuffer
operator|=
literal|"AnD"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"And"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|//first is forced, but it's not a keep word, either
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|false
expr_stmt|;
name|termBuffer
operator|=
literal|"AnD"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"And"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|//first is not forced, but it's not a keep word, either
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|true
expr_stmt|;
name|termBuffer
operator|=
literal|"big"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Big"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|termBuffer
operator|=
literal|"BIG"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BIG"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"Hello thEre my Name is Ryan"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
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
comment|// now each token
name|factory
operator|.
name|onlyFirstWord
operator|=
literal|false
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"Hello thEre my Name is Ryan"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
comment|// now only the long words
name|factory
operator|.
name|minWordLength
operator|=
literal|3
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"Hello thEre my Name is Ryan"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
comment|// without prefix
name|tokenizer
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"McKinley"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
comment|// Now try some prefixes
name|factory
operator|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"okPrefix"
argument_list|,
literal|"McK"
argument_list|)
expr_stmt|;
comment|// all words
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"McKinley"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
comment|// now try some stuff with numbers
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|false
expr_stmt|;
name|factory
operator|.
name|onlyFirstWord
operator|=
literal|false
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"1st 2nd third"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|true
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"the The the"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|KEEP
argument_list|,
literal|"kitten"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|KEEP_IGNORE_CASE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|ONLY_FIRST_WORD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|char
index|[]
name|termBuffer
decl_stmt|;
name|termBuffer
operator|=
literal|"kiTTEN"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|true
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"KiTTEN"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|forceFirstLetter
operator|=
literal|false
expr_stmt|;
name|termBuffer
operator|=
literal|"kiTTEN"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kiTTEN"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|keep
operator|=
literal|null
expr_stmt|;
name|termBuffer
operator|=
literal|"kiTTEN"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Kitten"
argument_list|,
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|ONLY_FIRST_WORD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|MIN_WORD_LENGTH
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"helo testing"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|MAX_WORD_COUNT
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"one two three four"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|MAX_WORD_COUNT
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"one two three four"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|MAX_TOKEN_LENGTH
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
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
DECL|method|testForceFirstLetter
specifier|public
name|void
name|testForceFirstLetter
parameter_list|()
throws|throws
name|Exception
block|{
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|KEEP
argument_list|,
literal|"kitten"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CapitalizationFilterFactory
operator|.
name|FORCE_FIRST_LETTER
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|CapitalizationFilterFactory
name|factory
init|=
operator|new
name|CapitalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"kitten"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
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
block|}
end_class

end_unit

