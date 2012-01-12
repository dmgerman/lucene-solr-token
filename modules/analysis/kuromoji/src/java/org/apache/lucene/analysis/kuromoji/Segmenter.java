begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
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
name|EnumMap
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
name|kuromoji
operator|.
name|dict
operator|.
name|*
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
name|kuromoji
operator|.
name|viterbi
operator|.
name|GraphvizFormatter
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
name|kuromoji
operator|.
name|viterbi
operator|.
name|Viterbi
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
name|kuromoji
operator|.
name|viterbi
operator|.
name|ViterbiNode
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
name|kuromoji
operator|.
name|viterbi
operator|.
name|ViterbiNode
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Tokenizer main class.  * Thread safe.  */
end_comment

begin_class
DECL|class|Segmenter
specifier|public
class|class
name|Segmenter
block|{
DECL|enum|Mode
specifier|public
specifier|static
enum|enum
name|Mode
block|{
DECL|enum constant|NORMAL
DECL|enum constant|SEARCH
DECL|enum constant|EXTENDED
name|NORMAL
block|,
name|SEARCH
block|,
name|EXTENDED
block|}
DECL|field|viterbi
specifier|private
specifier|final
name|Viterbi
name|viterbi
decl_stmt|;
DECL|field|dictionaryMap
specifier|private
specifier|final
name|EnumMap
argument_list|<
name|Type
argument_list|,
name|Dictionary
argument_list|>
name|dictionaryMap
init|=
operator|new
name|EnumMap
argument_list|<
name|Type
argument_list|,
name|Dictionary
argument_list|>
argument_list|(
name|Type
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|split
specifier|private
specifier|final
name|boolean
name|split
decl_stmt|;
DECL|method|Segmenter
specifier|public
name|Segmenter
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|Mode
operator|.
name|NORMAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Segmenter
specifier|public
name|Segmenter
parameter_list|(
name|UserDictionary
name|userDictionary
parameter_list|,
name|Mode
name|mode
parameter_list|)
block|{
name|this
argument_list|(
name|userDictionary
argument_list|,
name|mode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Segmenter
specifier|public
name|Segmenter
parameter_list|(
name|UserDictionary
name|userDictionary
parameter_list|)
block|{
name|this
argument_list|(
name|userDictionary
argument_list|,
name|Mode
operator|.
name|NORMAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Segmenter
specifier|public
name|Segmenter
parameter_list|(
name|Mode
name|mode
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|mode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Segmenter
specifier|public
name|Segmenter
parameter_list|(
name|UserDictionary
name|userDictionary
parameter_list|,
name|Mode
name|mode
parameter_list|,
name|boolean
name|split
parameter_list|)
block|{
specifier|final
name|TokenInfoDictionary
name|dict
init|=
name|TokenInfoDictionary
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|UnknownDictionary
name|unknownDict
init|=
name|UnknownDictionary
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|this
operator|.
name|viterbi
operator|=
operator|new
name|Viterbi
argument_list|(
name|dict
argument_list|,
name|unknownDict
argument_list|,
name|ConnectionCosts
operator|.
name|getInstance
argument_list|()
argument_list|,
name|userDictionary
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|this
operator|.
name|split
operator|=
name|split
expr_stmt|;
name|dictionaryMap
operator|.
name|put
argument_list|(
name|Type
operator|.
name|KNOWN
argument_list|,
name|dict
argument_list|)
expr_stmt|;
name|dictionaryMap
operator|.
name|put
argument_list|(
name|Type
operator|.
name|UNKNOWN
argument_list|,
name|unknownDict
argument_list|)
expr_stmt|;
name|dictionaryMap
operator|.
name|put
argument_list|(
name|Type
operator|.
name|USER
argument_list|,
name|userDictionary
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tokenize input text    * @param text    * @return list of Token    */
DECL|method|tokenize
specifier|public
name|List
argument_list|<
name|Token
argument_list|>
name|tokenize
parameter_list|(
name|String
name|text
parameter_list|)
block|{
if|if
condition|(
operator|!
name|split
condition|)
block|{
return|return
name|doTokenize
argument_list|(
literal|0
argument_list|,
name|text
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|splitPositions
init|=
name|getSplitPositions
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitPositions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|doTokenize
argument_list|(
literal|0
argument_list|,
name|text
argument_list|)
return|;
block|}
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|position
range|:
name|splitPositions
control|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|doTokenize
argument_list|(
name|offset
argument_list|,
name|text
operator|.
name|substring
argument_list|(
name|offset
argument_list|,
name|position
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|offset
operator|=
name|position
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|doTokenize
argument_list|(
name|offset
argument_list|,
name|text
operator|.
name|substring
argument_list|(
name|offset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Split input text at å¥èª­ç¹, which is ã and ã    * @param text    * @return list of split position    */
DECL|method|getSplitPositions
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|getSplitPositions
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|splitPositions
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
name|int
name|currentPosition
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|indexOfMaru
init|=
name|text
operator|.
name|indexOf
argument_list|(
literal|"ã"
argument_list|,
name|currentPosition
argument_list|)
decl_stmt|;
name|int
name|indexOfTen
init|=
name|text
operator|.
name|indexOf
argument_list|(
literal|"ã"
argument_list|,
name|currentPosition
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfMaru
operator|<
literal|0
operator|||
name|indexOfTen
operator|<
literal|0
condition|)
block|{
name|position
operator|=
name|Math
operator|.
name|max
argument_list|(
name|indexOfMaru
argument_list|,
name|indexOfTen
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
else|else
block|{
name|position
operator|=
name|Math
operator|.
name|min
argument_list|(
name|indexOfMaru
argument_list|,
name|indexOfTen
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|>=
literal|0
condition|)
block|{
name|splitPositions
operator|.
name|add
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|currentPosition
operator|=
name|position
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|splitPositions
return|;
block|}
DECL|method|doTokenize
specifier|private
name|List
argument_list|<
name|Token
argument_list|>
name|doTokenize
parameter_list|(
name|int
name|offset
parameter_list|,
name|String
name|sentence
parameter_list|)
block|{
name|char
name|text
index|[]
init|=
name|sentence
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
return|return
name|doTokenize
argument_list|(
name|offset
argument_list|,
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Tokenize input sentence.    * @param offset offset of sentence in original input text    * @param sentence sentence to tokenize    * @return list of Token    */
DECL|method|doTokenize
specifier|public
name|List
argument_list|<
name|Token
argument_list|>
name|doTokenize
parameter_list|(
name|int
name|offset
parameter_list|,
name|char
index|[]
name|sentence
parameter_list|,
name|int
name|sentenceOffset
parameter_list|,
name|int
name|sentenceLength
parameter_list|,
name|boolean
name|discardPunctuation
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|ViterbiNode
index|[]
index|[]
index|[]
name|lattice
decl_stmt|;
try|try
block|{
name|lattice
operator|=
name|viterbi
operator|.
name|build
argument_list|(
name|sentence
argument_list|,
name|sentenceOffset
argument_list|,
name|sentenceLength
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|impossible
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|impossible
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|ViterbiNode
argument_list|>
name|bestPath
init|=
name|viterbi
operator|.
name|search
argument_list|(
name|lattice
argument_list|)
decl_stmt|;
for|for
control|(
name|ViterbiNode
name|node
range|:
name|bestPath
control|)
block|{
name|int
name|wordId
init|=
name|node
operator|.
name|getWordId
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|KNOWN
operator|&&
name|wordId
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Do not include BOS/EOS
continue|continue;
block|}
elseif|else
if|if
condition|(
name|discardPunctuation
operator|&&
name|node
operator|.
name|getLength
argument_list|()
operator|>
literal|0
operator|&&
name|isPunctuation
argument_list|(
name|node
operator|.
name|getSurfaceForm
argument_list|()
index|[
name|node
operator|.
name|getOffset
argument_list|()
index|]
argument_list|)
condition|)
block|{
continue|continue;
comment|// Do not emit punctuation
block|}
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|wordId
argument_list|,
name|node
operator|.
name|getSurfaceForm
argument_list|()
argument_list|,
name|node
operator|.
name|getOffset
argument_list|()
argument_list|,
name|node
operator|.
name|getLength
argument_list|()
argument_list|,
name|node
operator|.
name|getType
argument_list|()
argument_list|,
name|offset
operator|+
name|node
operator|.
name|getStartIndex
argument_list|()
argument_list|,
name|dictionaryMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Pass different dictionary based on the type of node
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** returns a Graphviz String */
DECL|method|debugTokenize
specifier|public
name|String
name|debugTokenize
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|ViterbiNode
index|[]
index|[]
index|[]
name|lattice
decl_stmt|;
try|try
block|{
name|lattice
operator|=
name|this
operator|.
name|viterbi
operator|.
name|build
argument_list|(
name|text
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|impossible
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|impossible
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|ViterbiNode
argument_list|>
name|bestPath
init|=
name|this
operator|.
name|viterbi
operator|.
name|search
argument_list|(
name|lattice
argument_list|)
decl_stmt|;
return|return
operator|new
name|GraphvizFormatter
argument_list|(
name|ConnectionCosts
operator|.
name|getInstance
argument_list|()
argument_list|)
operator|.
name|format
argument_list|(
name|lattice
index|[
literal|0
index|]
argument_list|,
name|lattice
index|[
literal|1
index|]
argument_list|,
name|bestPath
argument_list|)
return|;
block|}
DECL|method|isPunctuation
specifier|static
specifier|final
name|boolean
name|isPunctuation
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
switch|switch
condition|(
name|Character
operator|.
name|getType
argument_list|(
name|ch
argument_list|)
condition|)
block|{
case|case
name|Character
operator|.
name|SPACE_SEPARATOR
case|:
case|case
name|Character
operator|.
name|LINE_SEPARATOR
case|:
case|case
name|Character
operator|.
name|PARAGRAPH_SEPARATOR
case|:
case|case
name|Character
operator|.
name|CONTROL
case|:
case|case
name|Character
operator|.
name|FORMAT
case|:
case|case
name|Character
operator|.
name|DASH_PUNCTUATION
case|:
case|case
name|Character
operator|.
name|START_PUNCTUATION
case|:
case|case
name|Character
operator|.
name|END_PUNCTUATION
case|:
case|case
name|Character
operator|.
name|CONNECTOR_PUNCTUATION
case|:
case|case
name|Character
operator|.
name|OTHER_PUNCTUATION
case|:
case|case
name|Character
operator|.
name|MATH_SYMBOL
case|:
case|case
name|Character
operator|.
name|CURRENCY_SYMBOL
case|:
case|case
name|Character
operator|.
name|MODIFIER_SYMBOL
case|:
case|case
name|Character
operator|.
name|OTHER_SYMBOL
case|:
case|case
name|Character
operator|.
name|INITIAL_QUOTE_PUNCTUATION
case|:
case|case
name|Character
operator|.
name|FINAL_QUOTE_PUNCTUATION
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

