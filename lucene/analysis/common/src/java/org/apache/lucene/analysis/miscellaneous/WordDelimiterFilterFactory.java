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
name|util
operator|.
name|ResourceLoader
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
name|ResourceLoaderAware
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
name|TokenFilterFactory
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
name|List
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
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|WordDelimiterFilter
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Factory for {@link WordDelimiterFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_wd" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.WordDelimiterFilterFactory" protected="protectedword.txt"  *             preserveOriginal="0" splitOnNumerics="1" splitOnCaseChange="1"  *             catenateWords="0" catenateNumbers="0" catenateAll="0"  *             generateWordParts="1" generateNumberParts="1" stemEnglishPossessive="1"  *             types="wdfftypes.txt" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment

begin_class
DECL|class|WordDelimiterFilterFactory
specifier|public
class|class
name|WordDelimiterFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|PROTECTED_TOKENS
specifier|public
specifier|static
specifier|final
name|String
name|PROTECTED_TOKENS
init|=
literal|"protected"
decl_stmt|;
DECL|field|TYPES
specifier|public
specifier|static
specifier|final
name|String
name|TYPES
init|=
literal|"types"
decl_stmt|;
DECL|field|wordFiles
specifier|private
specifier|final
name|String
name|wordFiles
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|String
name|types
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|int
name|flags
decl_stmt|;
DECL|field|typeTable
name|byte
index|[]
name|typeTable
init|=
literal|null
decl_stmt|;
DECL|field|protectedWords
specifier|private
name|CharArraySet
name|protectedWords
init|=
literal|null
decl_stmt|;
comment|/** Creates a new WordDelimiterFilterFactory */
DECL|method|WordDelimiterFilterFactory
specifier|public
name|WordDelimiterFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"generateWordParts"
argument_list|,
literal|1
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|GENERATE_WORD_PARTS
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"generateNumberParts"
argument_list|,
literal|1
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|GENERATE_NUMBER_PARTS
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"catenateWords"
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|CATENATE_WORDS
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"catenateNumbers"
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|CATENATE_NUMBERS
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"catenateAll"
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|CATENATE_ALL
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"splitOnCaseChange"
argument_list|,
literal|1
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|SPLIT_ON_CASE_CHANGE
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"splitOnNumerics"
argument_list|,
literal|1
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|SPLIT_ON_NUMERICS
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"preserveOriginal"
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|PRESERVE_ORIGINAL
expr_stmt|;
block|}
if|if
condition|(
name|getInt
argument_list|(
name|args
argument_list|,
literal|"stemEnglishPossessive"
argument_list|,
literal|1
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|flags
operator||=
name|STEM_ENGLISH_POSSESSIVE
expr_stmt|;
block|}
name|wordFiles
operator|=
name|get
argument_list|(
name|args
argument_list|,
name|PROTECTED_TOKENS
argument_list|)
expr_stmt|;
name|types
operator|=
name|get
argument_list|(
name|args
argument_list|,
name|TYPES
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|wordFiles
operator|!=
literal|null
condition|)
block|{
name|protectedWords
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|wordFiles
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|types
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|getLines
argument_list|(
name|loader
argument_list|,
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|wlist
operator|.
name|addAll
argument_list|(
name|lines
argument_list|)
expr_stmt|;
block|}
name|typeTable
operator|=
name|parseTypes
argument_list|(
name|wlist
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|WordDelimiterFilter
argument_list|(
name|input
argument_list|,
name|typeTable
operator|==
literal|null
condition|?
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
else|:
name|typeTable
argument_list|,
name|flags
argument_list|,
name|protectedWords
argument_list|)
return|;
block|}
comment|// source => type
DECL|field|typePattern
specifier|private
specifier|static
name|Pattern
name|typePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(.*)\\s*=>\\s*(.*)\\s*$"
argument_list|)
decl_stmt|;
comment|// parses a list of MappingCharFilter style rules into a custom byte[] type table
DECL|method|parseTypes
specifier|private
name|byte
index|[]
name|parseTypes
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|rules
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Character
argument_list|,
name|Byte
argument_list|>
name|typeMap
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|rule
range|:
name|rules
control|)
block|{
name|Matcher
name|m
init|=
name|typePattern
operator|.
name|matcher
argument_list|(
name|rule
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"]"
argument_list|)
throw|;
name|String
name|lhs
init|=
name|parseString
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|Byte
name|rhs
init|=
name|parseType
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lhs
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"]. Only a single character is allowed."
argument_list|)
throw|;
if|if
condition|(
name|rhs
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"]. Illegal type."
argument_list|)
throw|;
name|typeMap
operator|.
name|put
argument_list|(
name|lhs
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|rhs
argument_list|)
expr_stmt|;
block|}
comment|// ensure the table is always at least as big as DEFAULT_WORD_DELIM_TABLE for performance
name|byte
name|types
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|typeMap
operator|.
name|lastKey
argument_list|()
operator|+
literal|1
argument_list|,
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
operator|.
name|length
argument_list|)
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|types
index|[
name|i
index|]
operator|=
name|WordDelimiterIterator
operator|.
name|getType
argument_list|(
name|i
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Character
argument_list|,
name|Byte
argument_list|>
name|mapping
range|:
name|typeMap
operator|.
name|entrySet
argument_list|()
control|)
name|types
index|[
name|mapping
operator|.
name|getKey
argument_list|()
index|]
operator|=
name|mapping
operator|.
name|getValue
argument_list|()
expr_stmt|;
return|return
name|types
return|;
block|}
DECL|method|parseType
specifier|private
name|Byte
name|parseType
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"LOWER"
argument_list|)
condition|)
return|return
name|LOWER
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"UPPER"
argument_list|)
condition|)
return|return
name|UPPER
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"ALPHA"
argument_list|)
condition|)
return|return
name|ALPHA
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"DIGIT"
argument_list|)
condition|)
return|return
name|DIGIT
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"ALPHANUM"
argument_list|)
condition|)
return|return
name|ALPHANUM
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"SUBWORD_DELIM"
argument_list|)
condition|)
return|return
name|SUBWORD_DELIM
return|;
else|else
return|return
literal|null
return|;
block|}
DECL|field|out
name|char
index|[]
name|out
init|=
operator|new
name|char
index|[
literal|256
index|]
decl_stmt|;
DECL|method|parseString
specifier|private
name|String
name|parseString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|readPos
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|writePos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readPos
operator|<
name|len
condition|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|readPos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|readPos
operator|>=
name|len
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid escaped char in ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|readPos
operator|++
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
name|c
operator|=
literal|'\\'
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|c
operator|=
literal|'\n'
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|c
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|c
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|c
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|c
operator|=
literal|'\f'
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
if|if
condition|(
name|readPos
operator|+
literal|3
operator|>=
name|len
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid escaped char in ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
name|c
operator|=
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|readPos
argument_list|,
name|readPos
operator|+
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|readPos
operator|+=
literal|4
expr_stmt|;
break|break;
block|}
block|}
name|out
index|[
name|writePos
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|writePos
argument_list|)
return|;
block|}
block|}
end_class

end_unit

