begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link CharTokenizer} subclasses  */
end_comment

begin_class
DECL|class|TestCharTokenizers
specifier|public
class|class
name|TestCharTokenizers
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/*    * test to read surrogate pairs without loosing the pairing     * if the surrogate pair is at the border of the internal IO buffer    */
DECL|method|testReadSupplementaryChars
specifier|public
name|void
name|testReadSupplementaryChars
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Random
name|newRandom
init|=
name|newRandom
argument_list|()
decl_stmt|;
comment|// create random input
name|int
name|num
init|=
literal|1024
operator|+
name|newRandom
operator|.
name|nextInt
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"\ud801\udc1cabc"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|10
operator|)
operator|==
literal|0
condition|)
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
comment|// internal buffer size is 1024 make sure we have a surrogate pair right at the border
name|builder
operator|.
name|insert
argument_list|(
literal|1023
argument_list|,
literal|"\ud801\udc1c"
argument_list|)
expr_stmt|;
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * test to extend the buffer TermAttribute buffer internally. If the internal    * alg that extends the size of the char array only extends by 1 char and the    * next char to be filled in is a supplementary codepoint (using 2 chars) an    * index out of bound exception is triggered.    */
DECL|method|testExtendCharBuffer
specifier|public
name|void
name|testExtendCharBuffer
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|40
condition|;
name|i
operator|++
control|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1
operator|+
name|i
condition|;
name|j
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"\ud801\udc1cabc"
argument_list|)
expr_stmt|;
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * tests the max word length of 255 - tokenizer will split at the 255 char no matter what happens    */
DECL|method|testMaxWordLength
specifier|public
name|void
name|testMaxWordLength
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|255
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
block|}
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|+
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
block|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * tests the max word length of 255 with a surrogate pair at position 255    */
DECL|method|testMaxWordLengthWithSupplementary
specifier|public
name|void
name|testMaxWordLengthWithSupplementary
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|254
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"\ud801\udc1c"
argument_list|)
expr_stmt|;
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|+
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
block|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLowerCaseTokenizer
specifier|public
name|void
name|testLowerCaseTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tokenizer"
block|,
literal|"\ud801\udc44test"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLowerCaseTokenizerBWCompat
specifier|public
name|void
name|testLowerCaseTokenizerBWCompat
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|LowerCaseTokenizer
name|tokenizer
init|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tokenizer"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWhitespaceTokenizer
specifier|public
name|void
name|testWhitespaceTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|WhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Tokenizer"
block|,
literal|"\ud801\udc1ctest"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWhitespaceTokenizerBWCompat
specifier|public
name|void
name|testWhitespaceTokenizerBWCompat
parameter_list|()
throws|throws
name|IOException
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Tokenizer \ud801\udc1ctest"
argument_list|)
decl_stmt|;
name|WhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Tokenizer"
block|,
literal|"\ud801\udc1ctest"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsTokenCharCharInSubclass
specifier|public
name|void
name|testIsTokenCharCharInSubclass
parameter_list|()
block|{
operator|new
name|TestingCharTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TestingCharTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"version 3.1 is not permitted if char based method is implemented"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testNormalizeCharInSubclass
specifier|public
name|void
name|testNormalizeCharInSubclass
parameter_list|()
block|{
operator|new
name|TestingCharTokenizerNormalize
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TestingCharTokenizerNormalize
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"version 3.1 is not permitted if char based method is implemented"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testNormalizeAndIsTokenCharCharInSubclass
specifier|public
name|void
name|testNormalizeAndIsTokenCharCharInSubclass
parameter_list|()
block|{
operator|new
name|TestingCharTokenizerNormalizeIsTokenChar
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TestingCharTokenizerNormalizeIsTokenChar
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"version 3.1 is not permitted if char based method is implemented"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|class|TestingCharTokenizer
specifier|static
class|class
name|TestingCharTokenizer
extends|extends
name|CharTokenizer
block|{
DECL|method|TestingCharTokenizer
specifier|public
name|TestingCharTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
DECL|class|TestingCharTokenizerNormalize
specifier|static
class|class
name|TestingCharTokenizerNormalize
extends|extends
name|CharTokenizer
block|{
DECL|method|TestingCharTokenizerNormalize
specifier|public
name|TestingCharTokenizerNormalize
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|protected
name|char
name|normalize
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|c
return|;
block|}
block|}
DECL|class|TestingCharTokenizerNormalizeIsTokenChar
specifier|static
class|class
name|TestingCharTokenizerNormalizeIsTokenChar
extends|extends
name|CharTokenizer
block|{
DECL|method|TestingCharTokenizerNormalizeIsTokenChar
specifier|public
name|TestingCharTokenizerNormalizeIsTokenChar
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|protected
name|char
name|normalize
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

