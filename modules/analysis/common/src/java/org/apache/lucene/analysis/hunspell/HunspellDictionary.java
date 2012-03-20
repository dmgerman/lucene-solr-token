begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|CharArrayMap
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * In-memory structure for the dictionary (.dic) and affix (.aff)  * data of a hunspell dictionary.  */
end_comment

begin_class
DECL|class|HunspellDictionary
specifier|public
class|class
name|HunspellDictionary
block|{
DECL|field|NOFLAGS
specifier|static
specifier|final
name|HunspellWord
name|NOFLAGS
init|=
operator|new
name|HunspellWord
argument_list|()
decl_stmt|;
DECL|field|PREFIX_KEY
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_KEY
init|=
literal|"PFX"
decl_stmt|;
DECL|field|SUFFIX_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SUFFIX_KEY
init|=
literal|"SFX"
decl_stmt|;
DECL|field|FLAG_KEY
specifier|private
specifier|static
specifier|final
name|String
name|FLAG_KEY
init|=
literal|"FLAG"
decl_stmt|;
DECL|field|NUM_FLAG_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|NUM_FLAG_TYPE
init|=
literal|"num"
decl_stmt|;
DECL|field|UTF8_FLAG_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|UTF8_FLAG_TYPE
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|LONG_FLAG_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|LONG_FLAG_TYPE
init|=
literal|"long"
decl_stmt|;
DECL|field|PREFIX_CONDITION_REGEX_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_CONDITION_REGEX_PATTERN
init|=
literal|"%s.*"
decl_stmt|;
DECL|field|SUFFIX_CONDITION_REGEX_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|SUFFIX_CONDITION_REGEX_PATTERN
init|=
literal|".*%s"
decl_stmt|;
DECL|field|IGNORE_CASE_DEFAULT
specifier|private
specifier|static
specifier|final
name|boolean
name|IGNORE_CASE_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|words
specifier|private
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellWord
argument_list|>
argument_list|>
name|words
decl_stmt|;
DECL|field|prefixes
specifier|private
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|>
name|prefixes
decl_stmt|;
DECL|field|suffixes
specifier|private
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|>
name|suffixes
decl_stmt|;
DECL|field|flagParsingStrategy
specifier|private
name|FlagParsingStrategy
name|flagParsingStrategy
init|=
operator|new
name|SimpleFlagParsingStrategy
argument_list|()
decl_stmt|;
comment|// Default flag parsing strategy
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
init|=
name|IGNORE_CASE_DEFAULT
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
comment|/**    * Creates a new HunspellDictionary containing the information read from the provided InputStreams to hunspell affix    * and dictionary files    *    * @param affix InputStream for reading the hunspell affix file    * @param dictionary InputStream for reading the hunspell dictionary file    * @param version Lucene Version    * @throws IOException Can be thrown while reading from the InputStreams    * @throws ParseException Can be thrown if the content of the files does not meet expected formats    */
DECL|method|HunspellDictionary
specifier|public
name|HunspellDictionary
parameter_list|(
name|InputStream
name|affix
parameter_list|,
name|InputStream
name|dictionary
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|this
argument_list|(
name|affix
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|version
argument_list|,
name|IGNORE_CASE_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new HunspellDictionary containing the information read from the provided InputStreams to hunspell affix    * and dictionary files    *    * @param affix InputStream for reading the hunspell affix file    * @param dictionary InputStream for reading the hunspell dictionary file    * @param version Lucene Version    * @param ignoreCase If true, dictionary matching will be case insensitive    * @throws IOException Can be thrown while reading from the InputStreams    * @throws ParseException Can be thrown if the content of the files does not meet expected formats    */
DECL|method|HunspellDictionary
specifier|public
name|HunspellDictionary
parameter_list|(
name|InputStream
name|affix
parameter_list|,
name|InputStream
name|dictionary
parameter_list|,
name|Version
name|version
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|this
argument_list|(
name|affix
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictionary
argument_list|)
argument_list|,
name|version
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new HunspellDictionary containing the information read from the provided InputStreams to hunspell affix    * and dictionary files    *    * @param affix InputStream for reading the hunspell affix file    * @param dictionaries InputStreams for reading the hunspell dictionary file    * @param version Lucene Version    * @param ignoreCase If true, dictionary matching will be case insensitive    * @throws IOException Can be thrown while reading from the InputStreams    * @throws ParseException Can be thrown if the content of the files does not meet expected formats    */
DECL|method|HunspellDictionary
specifier|public
name|HunspellDictionary
parameter_list|(
name|InputStream
name|affix
parameter_list|,
name|List
argument_list|<
name|InputStream
argument_list|>
name|dictionaries
parameter_list|,
name|Version
name|version
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|String
name|encoding
init|=
name|getDictionaryEncoding
argument_list|(
name|affix
argument_list|)
decl_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|getJavaEncoding
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
name|readAffixFile
argument_list|(
name|affix
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
name|words
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellWord
argument_list|>
argument_list|>
argument_list|(
name|version
argument_list|,
literal|65535
comment|/* guess */
argument_list|,
name|this
operator|.
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|InputStream
name|dictionary
range|:
name|dictionaries
control|)
block|{
name|readDictionaryFile
argument_list|(
name|dictionary
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Looks up HunspellWords that match the String created from the given char array, offset and length    *    * @param word Char array to generate the String from    * @param offset Offset in the char array that the String starts at    * @param length Length from the offset that the String is    * @return List of HunspellWords that match the generated String, or {@code null} if none are found    */
DECL|method|lookupWord
specifier|public
name|List
argument_list|<
name|HunspellWord
argument_list|>
name|lookupWord
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|words
operator|.
name|get
argument_list|(
name|word
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Looks up HunspellAffix prefixes that have an append that matches the String created from the given char array, offset and length    *    * @param word Char array to generate the String from    * @param offset Offset in the char array that the String starts at    * @param length Length from the offset that the String is    * @return List of HunspellAffix prefixes with an append that matches the String, or {@code null} if none are found    */
DECL|method|lookupPrefix
specifier|public
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|lookupPrefix
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|prefixes
operator|.
name|get
argument_list|(
name|word
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Looks up HunspellAffix suffixes that have an append that matches the String created from the given char array, offset and length    *    * @param word Char array to generate the String from    * @param offset Offset in the char array that the String starts at    * @param length Length from the offset that the String is    * @return List of HunspellAffix suffixes with an append that matches the String, or {@code null} if none are found    */
DECL|method|lookupSuffix
specifier|public
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|lookupSuffix
parameter_list|(
name|char
name|word
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|suffixes
operator|.
name|get
argument_list|(
name|word
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Reads the affix file through the provided InputStream, building up the prefix and suffix maps    *    * @param affixStream InputStream to read the content of the affix file from    * @param decoder CharsetDecoder to decode the content of the file    * @throws IOException Can be thrown while reading from the InputStream    */
DECL|method|readAffixFile
specifier|private
name|void
name|readAffixFile
parameter_list|(
name|InputStream
name|affixStream
parameter_list|,
name|CharsetDecoder
name|decoder
parameter_list|)
throws|throws
name|IOException
block|{
name|prefixes
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|>
argument_list|(
name|version
argument_list|,
literal|8
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|suffixes
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|>
argument_list|(
name|version
argument_list|,
literal|8
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|affixStream
argument_list|,
name|decoder
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|PREFIX_KEY
argument_list|)
condition|)
block|{
name|parseAffix
argument_list|(
name|prefixes
argument_list|,
name|line
argument_list|,
name|reader
argument_list|,
name|PREFIX_CONDITION_REGEX_PATTERN
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|SUFFIX_KEY
argument_list|)
condition|)
block|{
name|parseAffix
argument_list|(
name|suffixes
argument_list|,
name|line
argument_list|,
name|reader
argument_list|,
name|SUFFIX_CONDITION_REGEX_PATTERN
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|FLAG_KEY
argument_list|)
condition|)
block|{
comment|// Assume that the FLAG line comes before any prefix or suffixes
comment|// Store the strategy so it can be used when parsing the dic file
name|flagParsingStrategy
operator|=
name|getFlagParsingStrategy
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Parses a specific affix rule putting the result into the provided affix map    *     * @param affixes Map where the result of the parsing will be put    * @param header Header line of the affix rule    * @param reader BufferedReader to read the content of the rule from    * @param conditionPattern {@link String#format(String, Object...)} pattern to be used to generate the condition regex    *                         pattern    * @throws IOException Can be thrown while reading the rule    */
DECL|method|parseAffix
specifier|private
name|void
name|parseAffix
parameter_list|(
name|CharArrayMap
argument_list|<
name|List
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|>
name|affixes
parameter_list|,
name|String
name|header
parameter_list|,
name|BufferedReader
name|reader
parameter_list|,
name|String
name|conditionPattern
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|args
index|[]
init|=
name|header
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|boolean
name|crossProduct
init|=
name|args
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"Y"
argument_list|)
decl_stmt|;
name|int
name|numLines
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
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
name|numLines
condition|;
name|i
operator|++
control|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|String
name|ruleArgs
index|[]
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|HunspellAffix
name|affix
init|=
operator|new
name|HunspellAffix
argument_list|()
decl_stmt|;
name|affix
operator|.
name|setFlag
argument_list|(
name|flagParsingStrategy
operator|.
name|parseFlag
argument_list|(
name|ruleArgs
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|affix
operator|.
name|setStrip
argument_list|(
name|ruleArgs
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
condition|?
literal|""
else|:
name|ruleArgs
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|String
name|affixArg
init|=
name|ruleArgs
index|[
literal|3
index|]
decl_stmt|;
name|int
name|flagSep
init|=
name|affixArg
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|flagSep
operator|!=
operator|-
literal|1
condition|)
block|{
name|char
name|appendFlags
index|[]
init|=
name|flagParsingStrategy
operator|.
name|parseFlags
argument_list|(
name|affixArg
operator|.
name|substring
argument_list|(
name|flagSep
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|appendFlags
argument_list|)
expr_stmt|;
name|affix
operator|.
name|setAppendFlags
argument_list|(
name|appendFlags
argument_list|)
expr_stmt|;
name|affix
operator|.
name|setAppend
argument_list|(
name|affixArg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|flagSep
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|affix
operator|.
name|setAppend
argument_list|(
name|affixArg
argument_list|)
expr_stmt|;
block|}
name|String
name|condition
init|=
name|ruleArgs
index|[
literal|4
index|]
decl_stmt|;
name|affix
operator|.
name|setCondition
argument_list|(
name|condition
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|conditionPattern
argument_list|,
name|condition
argument_list|)
argument_list|)
expr_stmt|;
name|affix
operator|.
name|setCrossProduct
argument_list|(
name|crossProduct
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|HunspellAffix
argument_list|>
name|list
init|=
name|affixes
operator|.
name|get
argument_list|(
name|affix
operator|.
name|getAppend
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|HunspellAffix
argument_list|>
argument_list|()
expr_stmt|;
name|affixes
operator|.
name|put
argument_list|(
name|affix
operator|.
name|getAppend
argument_list|()
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|affix
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Parses the encoding specified in the affix file readable through the provided InputStream    *    * @param affix InputStream for reading the affix file    * @return Encoding specified in the affix file    * @throws IOException Can be thrown while reading from the InputStream    * @throws ParseException Thrown if the first non-empty non-comment line read from the file does not adhere to the format {@code SET<encoding>}    */
DECL|method|getDictionaryEncoding
specifier|private
name|String
name|getDictionaryEncoding
parameter_list|(
name|InputStream
name|affix
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
specifier|final
name|StringBuilder
name|encoding
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|encoding
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|ch
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|affix
operator|.
name|read
argument_list|()
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'\n'
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|ch
operator|!=
literal|'\r'
condition|)
block|{
name|encoding
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|encoding
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|encoding
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'#'
operator|||
comment|// this test only at the end as ineffective but would allow lines only containing spaces:
name|encoding
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|ch
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unexpected end of affix file."
argument_list|,
literal|0
argument_list|)
throw|;
block|}
continue|continue;
block|}
if|if
condition|(
literal|"SET "
operator|.
name|equals
argument_list|(
name|encoding
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
condition|)
block|{
comment|// cleanup the encoding string, too (whitespace)
return|return
name|encoding
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"The first non-comment line in the affix file must "
operator|+
literal|"be a 'SET charset', was: '"
operator|+
name|encoding
operator|+
literal|"'"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
block|}
comment|/**    * Retrieves the CharsetDecoder for the given encoding.  Note, This isn't perfect as I think ISCII-DEVANAGARI and    * MICROSOFT-CP1251 etc are allowed...    *    * @param encoding Encoding to retrieve the CharsetDecoder for    * @return CharSetDecoder for the given encoding    */
DECL|method|getJavaEncoding
specifier|private
name|CharsetDecoder
name|getJavaEncoding
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
name|Charset
name|charset
init|=
name|Charset
operator|.
name|forName
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
return|return
name|charset
operator|.
name|newDecoder
argument_list|()
return|;
block|}
comment|/**    * Determines the appropriate {@link FlagParsingStrategy} based on the FLAG definition line taken from the affix file    *    * @param flagLine Line containing the flag information    * @return FlagParsingStrategy that handles parsing flags in the way specified in the FLAG definition    */
DECL|method|getFlagParsingStrategy
specifier|private
name|FlagParsingStrategy
name|getFlagParsingStrategy
parameter_list|(
name|String
name|flagLine
parameter_list|)
block|{
name|String
name|flagType
init|=
name|flagLine
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|NUM_FLAG_TYPE
operator|.
name|equals
argument_list|(
name|flagType
argument_list|)
condition|)
block|{
return|return
operator|new
name|NumFlagParsingStrategy
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|UTF8_FLAG_TYPE
operator|.
name|equals
argument_list|(
name|flagType
argument_list|)
condition|)
block|{
return|return
operator|new
name|SimpleFlagParsingStrategy
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|LONG_FLAG_TYPE
operator|.
name|equals
argument_list|(
name|flagType
argument_list|)
condition|)
block|{
return|return
operator|new
name|DoubleASCIIFlagParsingStrategy
argument_list|()
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown flag type: "
operator|+
name|flagType
argument_list|)
throw|;
block|}
comment|/**    * Reads the dictionary file through the provided InputStream, building up the words map    *    * @param dictionary InputStream to read the dictionary file through    * @param decoder CharsetDecoder used to decode the contents of the file    * @throws IOException Can be thrown while reading from the file    */
DECL|method|readDictionaryFile
specifier|private
name|void
name|readDictionaryFile
parameter_list|(
name|InputStream
name|dictionary
parameter_list|,
name|CharsetDecoder
name|decoder
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|dictionary
argument_list|,
name|decoder
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: don't create millions of strings.
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
comment|// first line is number of entries
name|int
name|numEntries
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
argument_list|)
decl_stmt|;
comment|// TODO: the flags themselves can be double-chars (long) or also numeric
comment|// either way the trick is to encode them as char... but they must be parsed differently
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|entry
decl_stmt|;
name|HunspellWord
name|wordForm
decl_stmt|;
name|int
name|flagSep
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|flagSep
operator|==
operator|-
literal|1
condition|)
block|{
name|wordForm
operator|=
name|NOFLAGS
expr_stmt|;
name|entry
operator|=
name|line
expr_stmt|;
block|}
else|else
block|{
comment|// note, there can be comments (morph description) after a flag.
comment|// we should really look for any whitespace
name|int
name|end
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'\t'
argument_list|,
name|flagSep
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|==
operator|-
literal|1
condition|)
name|end
operator|=
name|line
operator|.
name|length
argument_list|()
expr_stmt|;
name|wordForm
operator|=
operator|new
name|HunspellWord
argument_list|(
name|flagParsingStrategy
operator|.
name|parseFlags
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|flagSep
operator|+
literal|1
argument_list|,
name|end
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|wordForm
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|flagSep
argument_list|)
expr_stmt|;
if|if
condition|(
name|ignoreCase
condition|)
block|{
name|entry
operator|=
name|entry
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|HunspellWord
argument_list|>
name|entries
init|=
name|words
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
condition|)
block|{
name|entries
operator|=
operator|new
name|ArrayList
argument_list|<
name|HunspellWord
argument_list|>
argument_list|()
expr_stmt|;
name|words
operator|.
name|put
argument_list|(
name|entry
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
name|entries
operator|.
name|add
argument_list|(
name|wordForm
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getVersion
specifier|public
name|Version
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * Abstraction of the process of parsing flags taken from the affix and dic files    */
DECL|class|FlagParsingStrategy
specifier|private
specifier|static
specifier|abstract
class|class
name|FlagParsingStrategy
block|{
comment|/**      * Parses the given String into a single flag      *      * @param rawFlag String to parse into a flag      * @return Parsed flag      */
DECL|method|parseFlag
name|char
name|parseFlag
parameter_list|(
name|String
name|rawFlag
parameter_list|)
block|{
return|return
name|parseFlags
argument_list|(
name|rawFlag
argument_list|)
index|[
literal|0
index|]
return|;
block|}
comment|/**      * Parses the given String into multiple flags      *      * @param rawFlags String to parse into flags      * @return Parsed flags      */
DECL|method|parseFlags
specifier|abstract
name|char
index|[]
name|parseFlags
parameter_list|(
name|String
name|rawFlags
parameter_list|)
function_decl|;
block|}
comment|/**    * Simple implementation of {@link FlagParsingStrategy} that treats the chars in each String as a individual flags.    * Can be used with both the ASCII and UTF-8 flag types.    */
DECL|class|SimpleFlagParsingStrategy
specifier|private
specifier|static
class|class
name|SimpleFlagParsingStrategy
extends|extends
name|FlagParsingStrategy
block|{
comment|/**      * {@inheritDoc}      */
DECL|method|parseFlags
specifier|public
name|char
index|[]
name|parseFlags
parameter_list|(
name|String
name|rawFlags
parameter_list|)
block|{
return|return
name|rawFlags
operator|.
name|toCharArray
argument_list|()
return|;
block|}
block|}
comment|/**    * Implementation of {@link FlagParsingStrategy} that assumes each flag is encoded in its numerical form.  In the case    * of multiple flags, each number is separated by a comma.    */
DECL|class|NumFlagParsingStrategy
specifier|private
specifier|static
class|class
name|NumFlagParsingStrategy
extends|extends
name|FlagParsingStrategy
block|{
comment|/**      * {@inheritDoc}      */
DECL|method|parseFlags
specifier|public
name|char
index|[]
name|parseFlags
parameter_list|(
name|String
name|rawFlags
parameter_list|)
block|{
name|String
index|[]
name|rawFlagParts
init|=
name|rawFlags
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|char
index|[]
name|flags
init|=
operator|new
name|char
index|[
name|rawFlagParts
operator|.
name|length
index|]
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
name|rawFlagParts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// note, removing the trailing X/leading I for nepali... what is the rule here?!
name|flags
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|rawFlagParts
index|[
name|i
index|]
operator|.
name|replaceAll
argument_list|(
literal|"[^0-9]"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|flags
return|;
block|}
block|}
comment|/**    * Implementation of {@link FlagParsingStrategy} that assumes each flag is encoded as two ASCII characters whose codes    * must be combined into a single character.    *    * TODO (rmuir) test    */
DECL|class|DoubleASCIIFlagParsingStrategy
specifier|private
specifier|static
class|class
name|DoubleASCIIFlagParsingStrategy
extends|extends
name|FlagParsingStrategy
block|{
comment|/**      * {@inheritDoc}      */
DECL|method|parseFlags
specifier|public
name|char
index|[]
name|parseFlags
parameter_list|(
name|String
name|rawFlags
parameter_list|)
block|{
if|if
condition|(
name|rawFlags
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|char
index|[
literal|0
index|]
return|;
block|}
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
name|rawFlags
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|char
name|cookedFlag
init|=
call|(
name|char
call|)
argument_list|(
operator|(
name|int
operator|)
name|rawFlags
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|+
operator|(
name|int
operator|)
name|rawFlags
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|cookedFlag
argument_list|)
expr_stmt|;
block|}
name|char
name|flags
index|[]
init|=
operator|new
name|char
index|[
name|builder
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|builder
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|builder
operator|.
name|length
argument_list|()
argument_list|,
name|flags
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|flags
return|;
block|}
block|}
DECL|method|isIgnoreCase
specifier|public
name|boolean
name|isIgnoreCase
parameter_list|()
block|{
return|return
name|ignoreCase
return|;
block|}
block|}
end_class

end_unit

