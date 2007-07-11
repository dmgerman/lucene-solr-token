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
name|Token
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

begin_comment
comment|/**  * Splits words into subwords and performs optional transformations on subword groups.  * Words are split into subwords with the following rules:  *  - split on intra-word delimiters (by default, all non alpha-numeric characters).  *     - "Wi-Fi" -> "Wi", "Fi"  *  - split on case transitions  *     - "PowerShot" -> "Power", "Shot"  *  - split on letter-number transitions  *     - "SD500" -> "SD", "500"  *  - leading and trailing intra-word delimiters on each subword are ignored  *     - "//hello---there, 'dude'" -> "hello", "there", "dude"  *  - trailing "'s" are removed for each subword  *     - "O'Neil's" -> "O", "Neil"  *     - Note: this step isn't performed in a separate filter because of possible subword combinations.  *  * The<b>combinations</b> parameter affects how subwords are combined:  *  - combinations="0" causes no subword combinations.  *     - "PowerShot" -> 0:"Power", 1:"Shot"  (0 and 1 are the token positions)  *  - combinations="1" means that in addition to the subwords, maximum runs of non-numeric subwords are catenated and produced at the same position of the last subword in the run.  *     - "PowerShot" -> 0:"Power", 1:"Shot" 1:"PowerShot"  *     - "A's+B's&C's" -> 0:"A", 1:"B", 2:"C", 2:"ABC"  *     - "Super-Duper-XL500-42-AutoCoder!" -> 0:"Super", 1:"Duper", 2:"XL", 2:"SuperDuperXL", 3:"500" 4:"42", 5:"Auto", 6:"Coder", 6:"AutoCoder"  *  *  One use for WordDelimiterFilter is to help match words with different subword delimiters.  *  For example, if the source text contained "wi-fi" one may want "wifi" "WiFi" "wi-fi" "wi+fi"  *  queries to all match.  *  One way of doing so is to specify combinations="1" in the analyzer  *  used for indexing, and combinations="0" (the default) in the analyzer  *  used for querying.  Given that the current StandardTokenizer  *  immediately removes many intra-word delimiters, it is recommended that  *  this filter be used after a tokenizer that does not do this  *  (such as WhitespaceTokenizer).  *  *  @version $Id$  */
end_comment

begin_class
DECL|class|WordDelimiterFilter
specifier|final
class|class
name|WordDelimiterFilter
extends|extends
name|TokenFilter
block|{
DECL|field|charTypeTable
specifier|private
specifier|final
name|byte
index|[]
name|charTypeTable
decl_stmt|;
DECL|field|LOWER
specifier|public
specifier|static
specifier|final
name|int
name|LOWER
init|=
literal|0x01
decl_stmt|;
DECL|field|UPPER
specifier|public
specifier|static
specifier|final
name|int
name|UPPER
init|=
literal|0x02
decl_stmt|;
DECL|field|DIGIT
specifier|public
specifier|static
specifier|final
name|int
name|DIGIT
init|=
literal|0x04
decl_stmt|;
DECL|field|SUBWORD_DELIM
specifier|public
specifier|static
specifier|final
name|int
name|SUBWORD_DELIM
init|=
literal|0x08
decl_stmt|;
comment|// combinations: for testing, not for setting bits
DECL|field|ALPHA
specifier|public
specifier|static
specifier|final
name|int
name|ALPHA
init|=
literal|0x03
decl_stmt|;
DECL|field|ALPHANUM
specifier|public
specifier|static
specifier|final
name|int
name|ALPHANUM
init|=
literal|0x07
decl_stmt|;
comment|// TODO: should there be a WORD_DELIM category for
comment|// chars that only separate words (no catenation of subwords
comment|// will be done if separated by these chars?)
comment|// "," would be an obvious candidate...
DECL|field|defaultWordDelimTable
specifier|static
name|byte
index|[]
name|defaultWordDelimTable
decl_stmt|;
static|static
block|{
name|byte
index|[]
name|tab
init|=
operator|new
name|byte
index|[
literal|256
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
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|code
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isLowerCase
argument_list|(
name|i
argument_list|)
condition|)
name|code
operator||=
name|LOWER
expr_stmt|;
elseif|else
if|if
condition|(
name|Character
operator|.
name|isUpperCase
argument_list|(
name|i
argument_list|)
condition|)
name|code
operator||=
name|UPPER
expr_stmt|;
elseif|else
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|i
argument_list|)
condition|)
name|code
operator||=
name|DIGIT
expr_stmt|;
if|if
condition|(
name|code
operator|==
literal|0
condition|)
name|code
operator|=
name|SUBWORD_DELIM
expr_stmt|;
name|tab
index|[
name|i
index|]
operator|=
name|code
expr_stmt|;
block|}
name|defaultWordDelimTable
operator|=
name|tab
expr_stmt|;
block|}
comment|/**    * If 1, causes parts of words to be generated:    *<p/>    * "PowerShot" => "Power" "Shot"    */
DECL|field|generateWordParts
specifier|final
name|int
name|generateWordParts
decl_stmt|;
comment|/**    * If 1, causes number subwords to be generated:    *<p/>    * "500-42" => "500" "42"    */
DECL|field|generateNumberParts
specifier|final
name|int
name|generateNumberParts
decl_stmt|;
comment|/**    * If 1, causes maximum runs of word parts to be catenated:    *<p/>    * "wi-fi" => "wifi"    */
DECL|field|catenateWords
specifier|final
name|int
name|catenateWords
decl_stmt|;
comment|/**    * If 1, causes maximum runs of number parts to be catenated:    *<p/>    * "500-42" => "50042"    */
DECL|field|catenateNumbers
specifier|final
name|int
name|catenateNumbers
decl_stmt|;
comment|/**    * If 1, causes all subword parts to be catenated:    *<p/>    * "wi-fi-4000" => "wifi4000"    */
DECL|field|catenateAll
specifier|final
name|int
name|catenateAll
decl_stmt|;
comment|/**    * If 0, causes case changes to be ignored (subwords will only be generated    * given SUBWORD_DELIM tokens). (Defaults to 1)    */
DECL|field|splitOnCaseChange
specifier|final
name|int
name|splitOnCaseChange
decl_stmt|;
comment|/**    *    * @param in Token stream to be filtered.    * @param charTypeTable    * @param generateWordParts If 1, causes parts of words to be generated: "PowerShot" => "Power" "Shot"    * @param generateNumberParts If 1, causes number subwords to be generated: "500-42" => "500" "42"    * @param catenateWords  1, causes maximum runs of word parts to be catenated: "wi-fi" => "wifi"    * @param catenateNumbers If 1, causes maximum runs of number parts to be catenated: "500-42" => "50042"    * @param catenateAll If 1, causes all subword parts to be catenated: "wi-fi-4000" => "wifi4000"    * @param splitOnCaseChange 1, causes "PowerShot" to be two tokens; ("Power-Shot" remains two parts regards)    */
DECL|method|WordDelimiterFilter
specifier|public
name|WordDelimiterFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|byte
index|[]
name|charTypeTable
parameter_list|,
name|int
name|generateWordParts
parameter_list|,
name|int
name|generateNumberParts
parameter_list|,
name|int
name|catenateWords
parameter_list|,
name|int
name|catenateNumbers
parameter_list|,
name|int
name|catenateAll
parameter_list|,
name|int
name|splitOnCaseChange
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|generateWordParts
operator|=
name|generateWordParts
expr_stmt|;
name|this
operator|.
name|generateNumberParts
operator|=
name|generateNumberParts
expr_stmt|;
name|this
operator|.
name|catenateWords
operator|=
name|catenateWords
expr_stmt|;
name|this
operator|.
name|catenateNumbers
operator|=
name|catenateNumbers
expr_stmt|;
name|this
operator|.
name|catenateAll
operator|=
name|catenateAll
expr_stmt|;
name|this
operator|.
name|splitOnCaseChange
operator|=
name|splitOnCaseChange
expr_stmt|;
name|this
operator|.
name|charTypeTable
operator|=
name|charTypeTable
expr_stmt|;
block|}
comment|/**    * @param in Token stream to be filtered.    * @param generateWordParts If 1, causes parts of words to be generated: "PowerShot", "Power-Shot" => "Power" "Shot"    * @param generateNumberParts If 1, causes number subwords to be generated: "500-42" => "500" "42"    * @param catenateWords  1, causes maximum runs of word parts to be catenated: "wi-fi" => "wifi"    * @param catenateNumbers If 1, causes maximum runs of number parts to be catenated: "500-42" => "50042"    * @param catenateAll If 1, causes all subword parts to be catenated: "wi-fi-4000" => "wifi4000"    * @param splitOnCaseChange 1, causes "PowerShot" to be two tokens; ("Power-Shot" remains two parts regards)    */
DECL|method|WordDelimiterFilter
specifier|public
name|WordDelimiterFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|generateWordParts
parameter_list|,
name|int
name|generateNumberParts
parameter_list|,
name|int
name|catenateWords
parameter_list|,
name|int
name|catenateNumbers
parameter_list|,
name|int
name|catenateAll
parameter_list|,
name|int
name|splitOnCaseChange
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|defaultWordDelimTable
argument_list|,
name|generateWordParts
argument_list|,
name|generateNumberParts
argument_list|,
name|catenateWords
argument_list|,
name|catenateNumbers
argument_list|,
name|catenateAll
argument_list|,
name|splitOnCaseChange
argument_list|)
expr_stmt|;
block|}
comment|/** Compatibility constructor */
annotation|@
name|Deprecated
DECL|method|WordDelimiterFilter
specifier|public
name|WordDelimiterFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|byte
index|[]
name|charTypeTable
parameter_list|,
name|int
name|generateWordParts
parameter_list|,
name|int
name|generateNumberParts
parameter_list|,
name|int
name|catenateWords
parameter_list|,
name|int
name|catenateNumbers
parameter_list|,
name|int
name|catenateAll
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|charTypeTable
argument_list|,
name|generateWordParts
argument_list|,
name|generateNumberParts
argument_list|,
name|catenateWords
argument_list|,
name|catenateNumbers
argument_list|,
name|catenateAll
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Compatibility constructor */
annotation|@
name|Deprecated
DECL|method|WordDelimiterFilter
specifier|public
name|WordDelimiterFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|generateWordParts
parameter_list|,
name|int
name|generateNumberParts
parameter_list|,
name|int
name|catenateWords
parameter_list|,
name|int
name|catenateNumbers
parameter_list|,
name|int
name|catenateAll
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|defaultWordDelimTable
argument_list|,
name|generateWordParts
argument_list|,
name|generateNumberParts
argument_list|,
name|catenateWords
argument_list|,
name|catenateNumbers
argument_list|,
name|catenateAll
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|charType
name|int
name|charType
parameter_list|(
name|int
name|ch
parameter_list|)
block|{
if|if
condition|(
name|ch
operator|<
name|charTypeTable
operator|.
name|length
condition|)
block|{
return|return
name|charTypeTable
index|[
name|ch
index|]
return|;
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isLowerCase
argument_list|(
name|ch
argument_list|)
condition|)
block|{
return|return
name|LOWER
return|;
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|ch
argument_list|)
condition|)
block|{
return|return
name|UPPER
return|;
block|}
else|else
block|{
return|return
name|SUBWORD_DELIM
return|;
block|}
block|}
comment|// use the type of the first char as the type
comment|// of the token.
DECL|method|tokType
specifier|private
name|int
name|tokType
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
return|return
name|charType
argument_list|(
name|t
operator|.
name|termText
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|// There isn't really an efficient queue class, so we will
comment|// just use an array for now.
DECL|field|queue
specifier|private
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|queue
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|field|queuePos
specifier|private
name|int
name|queuePos
init|=
literal|0
decl_stmt|;
comment|// temporary working queue
DECL|field|tlist
specifier|private
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|tlist
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
DECL|method|newTok
specifier|private
name|Token
name|newTok
parameter_list|(
name|Token
name|orig
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|Token
argument_list|(
name|orig
operator|.
name|termText
argument_list|()
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|,
name|orig
operator|.
name|startOffset
argument_list|()
operator|+
name|start
argument_list|,
name|orig
operator|.
name|startOffset
argument_list|()
operator|+
name|end
argument_list|,
name|orig
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// check the queue first
if|if
condition|(
name|queuePos
operator|<
name|queue
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|queue
operator|.
name|get
argument_list|(
name|queuePos
operator|++
argument_list|)
return|;
block|}
comment|// reset the queue if it had been previously used
if|if
condition|(
name|queuePos
operator|!=
literal|0
condition|)
block|{
name|queuePos
operator|=
literal|0
expr_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// optimize for the common case: assume there will be
comment|// no subwords (just a simple word)
comment|//
comment|// Would it actually be faster to check for the common form
comment|// of isLetter() isLower()*, and then backtrack if it doesn't match?
name|int
name|origPosIncrement
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|s
init|=
name|t
operator|.
name|termText
argument_list|()
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|==
literal|0
condition|)
continue|continue;
name|origPosIncrement
operator|=
name|t
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
comment|// Avoid calling charType more than once for each char (basically
comment|// avoid any backtracking).
comment|// makes code slightly more difficult, but faster.
name|int
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|charType
argument_list|(
name|ch
argument_list|)
decl_stmt|;
name|int
name|numWords
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|end
condition|)
block|{
comment|// first eat delimiters at the start of this subword
while|while
condition|(
operator|(
name|type
operator|&
name|SUBWORD_DELIM
operator|)
operator|!=
literal|0
operator|&&
operator|++
name|start
operator|<
name|end
condition|)
block|{
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|type
operator|=
name|charType
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|int
name|pos
init|=
name|start
decl_stmt|;
comment|// save the type of the first char of the subword
comment|// as a way to tell what type of subword token this is (number, word, etc)
name|int
name|firstType
init|=
name|type
decl_stmt|;
name|int
name|lastType
init|=
name|type
decl_stmt|;
comment|// type of the previously read char
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
if|if
condition|(
name|type
operator|!=
name|lastType
condition|)
block|{
comment|// check and remove "'s" from the end of a token.
comment|// the pattern to check for is
comment|//   ALPHA "'" ("s"|"S") (SUBWORD_DELIM | END)
if|if
condition|(
operator|(
name|lastType
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'\''
operator|&&
name|pos
operator|+
literal|1
operator|<
name|end
operator|&&
operator|(
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|==
literal|'s'
operator|||
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|==
literal|'S'
operator|)
condition|)
block|{
name|int
name|subWordEnd
init|=
name|pos
decl_stmt|;
if|if
condition|(
name|pos
operator|+
literal|2
operator|>=
name|end
condition|)
block|{
comment|// end of string detected after "'s"
name|pos
operator|+=
literal|2
expr_stmt|;
block|}
else|else
block|{
comment|// make sure that a delimiter follows "'s"
name|int
name|ch2
init|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
decl_stmt|;
name|int
name|type2
init|=
name|charType
argument_list|(
name|ch2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|type2
operator|&
name|SUBWORD_DELIM
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// if delimiter, move position pointer
comment|// to it (skipping over "'s"
name|ch
operator|=
name|ch2
expr_stmt|;
name|type
operator|=
name|type2
expr_stmt|;
name|pos
operator|+=
literal|2
expr_stmt|;
block|}
block|}
name|queue
operator|.
name|add
argument_list|(
name|newTok
argument_list|(
name|t
argument_list|,
name|start
argument_list|,
name|subWordEnd
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|firstType
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
condition|)
name|numWords
operator|++
expr_stmt|;
break|break;
block|}
block|}
comment|// For case changes, only split on a transition from
comment|// lower to upper case, not vice-versa.
comment|// That will correctly handle the
comment|// case of a word starting with a capital (won't split).
comment|// It will also handle pluralization of
comment|// an uppercase word such as FOOs (won't split).
if|if
condition|(
name|splitOnCaseChange
operator|==
literal|0
operator|&&
operator|(
name|lastType
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
operator|&&
operator|(
name|type
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// ALPHA->ALPHA: always ignore if case isn't considered.
block|}
elseif|else
if|if
condition|(
operator|(
name|lastType
operator|&
name|UPPER
operator|)
operator|!=
literal|0
operator|&&
operator|(
name|type
operator|&
name|LOWER
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// UPPER->LOWER: Don't split
block|}
else|else
block|{
comment|// NOTE: this code currently assumes that only one flag
comment|// is set for each character now, so we don't have
comment|// to explicitly check for all the classes of transitions
comment|// listed below.
comment|// LOWER->UPPER
comment|// ALPHA->NUMERIC
comment|// NUMERIC->ALPHA
comment|// *->DELIMITER
name|queue
operator|.
name|add
argument_list|(
name|newTok
argument_list|(
name|t
argument_list|,
name|start
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|firstType
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
condition|)
name|numWords
operator|++
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|++
name|pos
operator|>=
name|end
condition|)
block|{
if|if
condition|(
name|start
operator|==
literal|0
condition|)
block|{
comment|// the subword is the whole original token, so
comment|// return it unchanged.
return|return
name|t
return|;
block|}
name|Token
name|newtok
init|=
name|newTok
argument_list|(
name|t
argument_list|,
name|start
argument_list|,
name|pos
argument_list|)
decl_stmt|;
comment|// optimization... if this is the only token,
comment|// return it immediately.
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|newtok
operator|.
name|setPositionIncrement
argument_list|(
name|origPosIncrement
argument_list|)
expr_stmt|;
return|return
name|newtok
return|;
block|}
name|queue
operator|.
name|add
argument_list|(
name|newtok
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|firstType
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
condition|)
name|numWords
operator|++
expr_stmt|;
break|break;
block|}
name|lastType
operator|=
name|type
expr_stmt|;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|type
operator|=
name|charType
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
comment|// start of the next subword is the current position
name|start
operator|=
name|pos
expr_stmt|;
block|}
comment|// System.out.println("##########TOKEN=" + s + " ######### WORD DELIMITER QUEUE=" + str(queue));
specifier|final
name|int
name|numtok
init|=
name|queue
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// We reached the end of the current token.
comment|// If the queue is empty, we should continue by reading
comment|// the next token
if|if
condition|(
name|numtok
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
comment|// if number of tokens is 1, always return the single tok
if|if
condition|(
name|numtok
operator|==
literal|1
condition|)
block|{
break|break;
block|}
specifier|final
name|int
name|numNumbers
init|=
name|numtok
operator|-
name|numWords
decl_stmt|;
comment|// check conditions under which the current token
comment|// queue may be used as-is (no catenations needed)
if|if
condition|(
name|catenateAll
operator|==
literal|0
comment|// no "everything" to catenate
operator|&&
operator|(
name|catenateWords
operator|==
literal|0
operator|||
name|numWords
operator|<=
literal|1
operator|)
comment|// no words to catenate
operator|&&
operator|(
name|catenateNumbers
operator|==
literal|0
operator|||
name|numNumbers
operator|<=
literal|1
operator|)
comment|// no numbers to catenate
operator|&&
operator|(
name|generateWordParts
operator|!=
literal|0
operator|||
name|numWords
operator|==
literal|0
operator|)
comment|// word generation is on
operator|&&
operator|(
name|generateNumberParts
operator|!=
literal|0
operator|||
name|numNumbers
operator|==
literal|0
operator|)
condition|)
comment|// number generation is on
block|{
break|break;
block|}
comment|// swap queue and the temporary working list, then clear the
comment|// queue in preparation for adding all combinations back to it.
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|tmp
init|=
name|tlist
decl_stmt|;
name|tlist
operator|=
name|queue
expr_stmt|;
name|queue
operator|=
name|tmp
expr_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|numWords
operator|==
literal|0
condition|)
block|{
comment|// all numbers
name|addCombos
argument_list|(
name|tlist
argument_list|,
literal|0
argument_list|,
name|numtok
argument_list|,
name|generateNumberParts
operator|!=
literal|0
argument_list|,
name|catenateNumbers
operator|!=
literal|0
operator|||
name|catenateAll
operator|!=
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
break|break;
else|else
continue|continue;
block|}
elseif|else
if|if
condition|(
name|numNumbers
operator|==
literal|0
condition|)
block|{
comment|// all words
name|addCombos
argument_list|(
name|tlist
argument_list|,
literal|0
argument_list|,
name|numtok
argument_list|,
name|generateWordParts
operator|!=
literal|0
argument_list|,
name|catenateWords
operator|!=
literal|0
operator|||
name|catenateAll
operator|!=
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
break|break;
else|else
continue|continue;
block|}
elseif|else
if|if
condition|(
name|generateNumberParts
operator|==
literal|0
operator|&&
name|generateWordParts
operator|==
literal|0
operator|&&
name|catenateNumbers
operator|==
literal|0
operator|&&
name|catenateWords
operator|==
literal|0
condition|)
block|{
comment|// catenate all *only*
comment|// OPT:could be optimized to add to current queue...
name|addCombos
argument_list|(
name|tlist
argument_list|,
literal|0
argument_list|,
name|numtok
argument_list|,
literal|false
argument_list|,
name|catenateAll
operator|!=
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
break|break;
else|else
continue|continue;
block|}
comment|//
comment|// Find all adjacent tokens of the same type.
comment|//
name|Token
name|tok
init|=
name|tlist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|isWord
init|=
operator|(
name|tokType
argument_list|(
name|tok
argument_list|)
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|wasWord
init|=
name|isWord
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
name|numtok
condition|;
control|)
block|{
name|int
name|j
decl_stmt|;
for|for
control|(
name|j
operator|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|numtok
condition|;
name|j
operator|++
control|)
block|{
name|wasWord
operator|=
name|isWord
expr_stmt|;
name|tok
operator|=
name|tlist
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|isWord
operator|=
operator|(
name|tokType
argument_list|(
name|tok
argument_list|)
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
expr_stmt|;
if|if
condition|(
name|isWord
operator|!=
name|wasWord
condition|)
break|break;
block|}
if|if
condition|(
name|wasWord
condition|)
block|{
name|addCombos
argument_list|(
name|tlist
argument_list|,
name|i
argument_list|,
name|j
argument_list|,
name|generateWordParts
operator|!=
literal|0
argument_list|,
name|catenateWords
operator|!=
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addCombos
argument_list|(
name|tlist
argument_list|,
name|i
argument_list|,
name|j
argument_list|,
name|generateNumberParts
operator|!=
literal|0
argument_list|,
name|catenateNumbers
operator|!=
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
name|j
expr_stmt|;
block|}
comment|// take care catenating all subwords
if|if
condition|(
name|catenateAll
operator|!=
literal|0
condition|)
block|{
name|addCombos
argument_list|(
name|tlist
argument_list|,
literal|0
argument_list|,
name|numtok
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: in certain cases, queue may be empty (for instance, if catenate
comment|// and generate are both set to false).  Only exit the loop if the queue
comment|// is not empty.
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
break|break;
block|}
comment|// System.out.println("##########AFTER COMBINATIONS:"+ str(queue));
name|queuePos
operator|=
literal|1
expr_stmt|;
name|Token
name|tok
init|=
name|queue
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|origPosIncrement
argument_list|)
expr_stmt|;
return|return
name|tok
return|;
block|}
comment|// index "a","b","c" as  pos0="a", pos1="b", pos2="c", pos2="abc"
DECL|method|addCombos
specifier|private
name|void
name|addCombos
parameter_list|(
name|List
argument_list|<
name|Token
argument_list|>
name|lst
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|generateSubwords
parameter_list|,
name|boolean
name|catenateSubwords
parameter_list|,
name|int
name|posOffset
parameter_list|)
block|{
if|if
condition|(
name|end
operator|-
name|start
operator|==
literal|1
condition|)
block|{
comment|// always generate a word alone, even if generateSubwords=0 because
comment|// the catenation of all the subwords *is* the subword.
name|queue
operator|.
name|add
argument_list|(
name|lst
operator|.
name|get
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|StringBuilder
name|sb
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|catenateSubwords
condition|)
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|Token
name|firstTok
init|=
literal|null
decl_stmt|;
name|Token
name|tok
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|tok
operator|=
name|lst
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|catenateSubwords
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|start
condition|)
name|firstTok
operator|=
name|tok
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|tok
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|generateSubwords
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|catenateSubwords
condition|)
block|{
name|Token
name|concatTok
init|=
operator|new
name|Token
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|firstTok
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok
operator|.
name|endOffset
argument_list|()
argument_list|,
name|firstTok
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
comment|// if we indexed some other tokens, then overlap concatTok with the last.
comment|// Otherwise, use the value passed in as the position offset.
name|concatTok
operator|.
name|setPositionIncrement
argument_list|(
name|generateSubwords
operator|==
literal|true
condition|?
literal|0
else|:
name|posOffset
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|concatTok
argument_list|)
expr_stmt|;
block|}
block|}
comment|// questions:
comment|// negative numbers?  -42 indexed as just 42?
comment|// dollar sign?  $42
comment|// percent sign?  33%
comment|// downsides:  if source text is "powershot" then a query of "PowerShot" won't match!
block|}
end_class

end_unit

