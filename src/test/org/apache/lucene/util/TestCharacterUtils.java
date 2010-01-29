begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|util
operator|.
name|CharacterUtils
operator|.
name|CharacterBuffer
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

begin_comment
comment|/**  * TestCase for the {@link CharacterUtils} class.  */
end_comment

begin_class
DECL|class|TestCharacterUtils
specifier|public
class|class
name|TestCharacterUtils
block|{
annotation|@
name|Test
DECL|method|testCodePointAtCharArrayInt
specifier|public
name|void
name|testCodePointAtCharArrayInt
parameter_list|()
block|{
name|CharacterUtils
name|java4
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|char
index|[]
name|cpAt3
init|=
literal|"Abc\ud801\udc1c"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|highSurrogateAt3
init|=
literal|"Abc\ud801"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'A'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|java4
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"array index out of bounds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{     }
name|CharacterUtils
name|java5
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'A'
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Character
operator|.
name|toCodePoint
argument_list|(
literal|'\ud801'
argument_list|,
literal|'\udc1c'
argument_list|)
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|java5
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"array index out of bounds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testCodePointAtCharSequenceInt
specifier|public
name|void
name|testCodePointAtCharSequenceInt
parameter_list|()
block|{
name|CharacterUtils
name|java4
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|String
name|cpAt3
init|=
literal|"Abc\ud801\udc1c"
decl_stmt|;
name|String
name|highSurrogateAt3
init|=
literal|"Abc\ud801"
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'A'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|java4
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"string index out of bounds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|e
parameter_list|)
block|{     }
name|CharacterUtils
name|java5
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'A'
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Character
operator|.
name|toCodePoint
argument_list|(
literal|'\ud801'
argument_list|,
literal|'\udc1c'
argument_list|)
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|java5
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"string index out of bounds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testCodePointAtCharArrayIntInt
specifier|public
name|void
name|testCodePointAtCharArrayIntInt
parameter_list|()
block|{
name|CharacterUtils
name|java4
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|char
index|[]
name|cpAt3
init|=
literal|"Abc\ud801\udc1c"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|highSurrogateAt3
init|=
literal|"Abc\ud801"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'A'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java4
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|CharacterUtils
name|java5
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'A'
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Character
operator|.
name|toCodePoint
argument_list|(
literal|'\ud801'
argument_list|,
literal|'\udc1c'
argument_list|)
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|cpAt3
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|int
operator|)
literal|'\ud801'
argument_list|,
name|java5
operator|.
name|codePointAt
argument_list|(
name|highSurrogateAt3
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNewCharacterBuffer
specifier|public
name|void
name|testNewCharacterBuffer
parameter_list|()
block|{
name|CharacterBuffer
name|newCharacterBuffer
init|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|newCharacterBuffer
operator|.
name|getBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newCharacterBuffer
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newCharacterBuffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|newCharacterBuffer
operator|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|newCharacterBuffer
operator|.
name|getBuffer
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newCharacterBuffer
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newCharacterBuffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|newCharacterBuffer
operator|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"length must be>= 2"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testFillNoHighSurrogate
specifier|public
name|void
name|testFillNoHighSurrogate
parameter_list|()
throws|throws
name|IOException
block|{
name|Version
index|[]
name|versions
init|=
operator|new
name|Version
index|[]
block|{
name|Version
operator|.
name|LUCENE_30
block|,
name|Version
operator|.
name|LUCENE_31
block|}
decl_stmt|;
for|for
control|(
name|Version
name|version
range|:
name|versions
control|)
block|{
name|CharacterUtils
name|instance
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"helloworld"
argument_list|)
decl_stmt|;
name|CharacterBuffer
name|buffer
init|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hellow"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"orld"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFillJava15
specifier|public
name|void
name|testFillJava15
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|input
init|=
literal|"1234\ud801\udc1c789123\ud801\ud801\udc1c\ud801"
decl_stmt|;
name|CharacterUtils
name|instance
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|CharacterBuffer
name|buffer
init|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1234"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\ud801\udc1c789"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123\ud801"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\ud801\udc1c"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\ud801"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFillJava14
specifier|public
name|void
name|testFillJava14
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|input
init|=
literal|"1234\ud801\udc1c789123\ud801\ud801\udc1c\ud801"
decl_stmt|;
name|CharacterUtils
name|instance
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|CharacterBuffer
name|buffer
init|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1234\ud801"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\udc1c7891"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"23\ud801\ud801\udc1c\ud801"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|instance
operator|.
name|fill
argument_list|(
name|buffer
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

