begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ja.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|dict
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
name|BufferedInputStream
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
name|InputStream
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
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|InputStreamDataInput
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
name|CodecUtil
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Character category data.  */
end_comment

begin_class
DECL|class|CharacterDefinition
specifier|public
specifier|final
class|class
name|CharacterDefinition
block|{
DECL|field|FILENAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|FILENAME_SUFFIX
init|=
literal|".dat"
decl_stmt|;
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"kuromoji_cd"
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|int
name|VERSION
init|=
literal|1
decl_stmt|;
DECL|field|CLASS_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|CLASS_COUNT
init|=
name|CharacterClass
operator|.
name|values
argument_list|()
operator|.
name|length
decl_stmt|;
comment|// only used internally for lookup:
DECL|enum|CharacterClass
specifier|private
specifier|static
enum|enum
name|CharacterClass
block|{
DECL|enum constant|NGRAM
DECL|enum constant|DEFAULT
DECL|enum constant|SPACE
DECL|enum constant|SYMBOL
DECL|enum constant|NUMERIC
DECL|enum constant|ALPHA
DECL|enum constant|CYRILLIC
DECL|enum constant|GREEK
DECL|enum constant|HIRAGANA
DECL|enum constant|KATAKANA
DECL|enum constant|KANJI
DECL|enum constant|KANJINUMERIC
name|NGRAM
block|,
name|DEFAULT
block|,
name|SPACE
block|,
name|SYMBOL
block|,
name|NUMERIC
block|,
name|ALPHA
block|,
name|CYRILLIC
block|,
name|GREEK
block|,
name|HIRAGANA
block|,
name|KATAKANA
block|,
name|KANJI
block|,
name|KANJINUMERIC
block|;   }
DECL|field|characterCategoryMap
specifier|private
specifier|final
name|byte
index|[]
name|characterCategoryMap
init|=
operator|new
name|byte
index|[
literal|0x10000
index|]
decl_stmt|;
DECL|field|invokeMap
specifier|private
specifier|final
name|boolean
index|[]
name|invokeMap
init|=
operator|new
name|boolean
index|[
name|CLASS_COUNT
index|]
decl_stmt|;
DECL|field|groupMap
specifier|private
specifier|final
name|boolean
index|[]
name|groupMap
init|=
operator|new
name|boolean
index|[
name|CLASS_COUNT
index|]
decl_stmt|;
comment|// the classes:
DECL|field|NGRAM
specifier|public
specifier|static
specifier|final
name|byte
name|NGRAM
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|NGRAM
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|byte
name|DEFAULT
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|DEFAULT
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|SPACE
specifier|public
specifier|static
specifier|final
name|byte
name|SPACE
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|SPACE
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|SYMBOL
specifier|public
specifier|static
specifier|final
name|byte
name|SYMBOL
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|SYMBOL
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|NUMERIC
specifier|public
specifier|static
specifier|final
name|byte
name|NUMERIC
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|NUMERIC
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|ALPHA
specifier|public
specifier|static
specifier|final
name|byte
name|ALPHA
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|ALPHA
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|CYRILLIC
specifier|public
specifier|static
specifier|final
name|byte
name|CYRILLIC
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|CYRILLIC
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|GREEK
specifier|public
specifier|static
specifier|final
name|byte
name|GREEK
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|GREEK
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|HIRAGANA
specifier|public
specifier|static
specifier|final
name|byte
name|HIRAGANA
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|HIRAGANA
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|KATAKANA
specifier|public
specifier|static
specifier|final
name|byte
name|KATAKANA
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|KATAKANA
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|KANJI
specifier|public
specifier|static
specifier|final
name|byte
name|KANJI
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|KANJI
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|field|KANJINUMERIC
specifier|public
specifier|static
specifier|final
name|byte
name|KANJINUMERIC
init|=
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|KANJINUMERIC
operator|.
name|ordinal
argument_list|()
decl_stmt|;
DECL|method|CharacterDefinition
specifier|private
name|CharacterDefinition
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|priorE
init|=
literal|null
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|BinaryDictionary
operator|.
name|getClassResource
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|FILENAME_SUFFIX
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
specifier|final
name|DataInput
name|in
init|=
operator|new
name|InputStreamDataInput
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|HEADER
argument_list|,
name|VERSION
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|characterCategoryMap
argument_list|,
literal|0
argument_list|,
name|characterCategoryMap
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|CLASS_COUNT
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|byte
name|b
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|invokeMap
index|[
name|i
index|]
operator|=
operator|(
name|b
operator|&
literal|0x01
operator|)
operator|!=
literal|0
expr_stmt|;
name|groupMap
index|[
name|i
index|]
operator|=
operator|(
name|b
operator|&
literal|0x02
operator|)
operator|!=
literal|0
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|priorE
operator|=
name|ioe
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|priorE
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCharacterClass
specifier|public
name|byte
name|getCharacterClass
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|characterCategoryMap
index|[
name|c
index|]
return|;
block|}
DECL|method|isInvoke
specifier|public
name|boolean
name|isInvoke
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|invokeMap
index|[
name|characterCategoryMap
index|[
name|c
index|]
index|]
return|;
block|}
DECL|method|isGroup
specifier|public
name|boolean
name|isGroup
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|groupMap
index|[
name|characterCategoryMap
index|[
name|c
index|]
index|]
return|;
block|}
DECL|method|isKanji
specifier|public
name|boolean
name|isKanji
parameter_list|(
name|char
name|c
parameter_list|)
block|{
specifier|final
name|byte
name|characterClass
init|=
name|characterCategoryMap
index|[
name|c
index|]
decl_stmt|;
return|return
name|characterClass
operator|==
name|KANJI
operator|||
name|characterClass
operator|==
name|KANJINUMERIC
return|;
block|}
DECL|method|lookupCharacterClass
specifier|public
specifier|static
name|byte
name|lookupCharacterClass
parameter_list|(
name|String
name|characterClassName
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|CharacterClass
operator|.
name|valueOf
argument_list|(
name|characterClassName
argument_list|)
operator|.
name|ordinal
argument_list|()
return|;
block|}
DECL|method|getInstance
specifier|public
specifier|static
name|CharacterDefinition
name|getInstance
parameter_list|()
block|{
return|return
name|SingletonHolder
operator|.
name|INSTANCE
return|;
block|}
DECL|class|SingletonHolder
specifier|private
specifier|static
class|class
name|SingletonHolder
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|CharacterDefinition
name|INSTANCE
decl_stmt|;
static|static
block|{
try|try
block|{
name|INSTANCE
operator|=
operator|new
name|CharacterDefinition
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot load CharacterDefinition."
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

