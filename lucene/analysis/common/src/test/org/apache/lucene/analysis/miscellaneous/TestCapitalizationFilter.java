begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
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
name|CapitalizationFilter
operator|.
name|*
import|;
end_import

begin_comment
comment|/** Tests {@link CapitalizationFilter} */
end_comment

begin_class
DECL|class|TestCapitalizationFilter
specifier|public
class|class
name|TestCapitalizationFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testCapitalization
specifier|public
name|void
name|testCapitalization
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|keep
init|=
operator|new
name|CharArraySet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"and"
argument_list|,
literal|"the"
argument_list|,
literal|"it"
argument_list|,
literal|"BIG"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertCapitalizesTo
argument_list|(
literal|"kiTTEN"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Kitten"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
literal|"and"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"And"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
literal|"AnD"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"And"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
comment|//first is not forced, but it's not a keep word, either
name|assertCapitalizesTo
argument_list|(
literal|"AnD"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"And"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
literal|"big"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Big"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
literal|"BIG"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BIG"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|assertCapitalizesToKeyword
argument_list|(
literal|"Hello thEre my Name is Ryan"
argument_list|,
literal|"Hello there my name is ryan"
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
comment|// now each token
name|assertCapitalizesTo
argument_list|(
literal|"Hello thEre my Name is Ryan"
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
argument_list|,
literal|false
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
comment|// now only the long words
name|assertCapitalizesTo
argument_list|(
literal|"Hello thEre my Name is Ryan"
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
argument_list|,
literal|false
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
comment|// without prefix
name|assertCapitalizesTo
argument_list|(
literal|"McKinley"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Mckinley"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
comment|// Now try some prefixes
name|List
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|okPrefix
operator|.
name|add
argument_list|(
literal|"McK"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
literal|"McKinley"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"McKinley"
block|}
argument_list|,
literal|true
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
name|okPrefix
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
comment|// now try some stuff with numbers
name|assertCapitalizesTo
argument_list|(
literal|"1st 2nd third"
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
argument_list|,
literal|false
argument_list|,
name|keep
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
name|assertCapitalizesToKeyword
argument_list|(
literal|"the The the"
argument_list|,
literal|"The The the"
argument_list|,
literal|false
argument_list|,
name|keep
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCapitalizesTo
specifier|static
name|void
name|assertCapitalizesTo
parameter_list|(
name|Tokenizer
name|tokenizer
parameter_list|,
name|String
name|expected
index|[]
parameter_list|,
name|boolean
name|onlyFirstWord
parameter_list|,
name|CharArraySet
name|keep
parameter_list|,
name|boolean
name|forceFirstLetter
parameter_list|,
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
parameter_list|,
name|int
name|minWordLength
parameter_list|,
name|int
name|maxWordCount
parameter_list|,
name|int
name|maxTokenLength
parameter_list|)
throws|throws
name|IOException
block|{
name|CapitalizationFilter
name|filter
init|=
operator|new
name|CapitalizationFilter
argument_list|(
name|tokenizer
argument_list|,
name|onlyFirstWord
argument_list|,
name|keep
argument_list|,
name|forceFirstLetter
argument_list|,
name|okPrefix
argument_list|,
name|minWordLength
argument_list|,
name|maxWordCount
argument_list|,
name|maxTokenLength
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCapitalizesTo
specifier|static
name|void
name|assertCapitalizesTo
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|expected
index|[]
parameter_list|,
name|boolean
name|onlyFirstWord
parameter_list|,
name|CharArraySet
name|keep
parameter_list|,
name|boolean
name|forceFirstLetter
parameter_list|,
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
parameter_list|,
name|int
name|minWordLength
parameter_list|,
name|int
name|maxWordCount
parameter_list|,
name|int
name|maxTokenLength
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MockTokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
name|tokenizer
argument_list|,
name|expected
argument_list|,
name|onlyFirstWord
argument_list|,
name|keep
argument_list|,
name|forceFirstLetter
argument_list|,
name|okPrefix
argument_list|,
name|minWordLength
argument_list|,
name|maxWordCount
argument_list|,
name|maxTokenLength
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCapitalizesToKeyword
specifier|static
name|void
name|assertCapitalizesToKeyword
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|expected
parameter_list|,
name|boolean
name|onlyFirstWord
parameter_list|,
name|CharArraySet
name|keep
parameter_list|,
name|boolean
name|forceFirstLetter
parameter_list|,
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
parameter_list|,
name|int
name|minWordLength
parameter_list|,
name|int
name|maxWordCount
parameter_list|,
name|int
name|maxTokenLength
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MockTokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|assertCapitalizesTo
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|expected
block|}
argument_list|,
name|onlyFirstWord
argument_list|,
name|keep
argument_list|,
name|forceFirstLetter
argument_list|,
name|okPrefix
argument_list|,
name|minWordLength
argument_list|,
name|maxWordCount
argument_list|,
name|maxTokenLength
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomString
specifier|public
name|void
name|testRandomString
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
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|CapitalizationFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
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
name|CapitalizationFilter
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
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * checking the validity of constructor arguments    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|CapitalizationFilter
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
literal|"accept only valid arguments"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testIllegalArguments1
specifier|public
name|void
name|testIllegalArguments1
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|CapitalizationFilter
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
literal|"accept only valid arguments"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
operator|-
literal|10
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testIllegalArguments2
specifier|public
name|void
name|testIllegalArguments2
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|CapitalizationFilter
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
literal|"accept only valid arguments"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
operator|-
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

