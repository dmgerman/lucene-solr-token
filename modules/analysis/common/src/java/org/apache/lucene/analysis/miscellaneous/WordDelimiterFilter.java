begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|TypeAttribute
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
name|util
operator|.
name|ArrayUtil
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
name|RamUsageEstimator
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

begin_comment
comment|/**  * Splits words into subwords and performs optional transformations on subword groups.  * Words are split into subwords with the following rules:  *  - split on intra-word delimiters (by default, all non alpha-numeric characters).  *     - "Wi-Fi" -> "Wi", "Fi"  *  - split on case transitions  *     - "PowerShot" -> "Power", "Shot"  *  - split on letter-number transitions  *     - "SD500" -> "SD", "500"  *  - leading and trailing intra-word delimiters on each subword are ignored  *     - "//hello---there, 'dude'" -> "hello", "there", "dude"  *  - trailing "'s" are removed for each subword  *     - "O'Neil's" -> "O", "Neil"  *     - Note: this step isn't performed in a separate filter because of possible subword combinations.  *  * The<b>combinations</b> parameter affects how subwords are combined:  *  - combinations="0" causes no subword combinations.  *     - "PowerShot" -> 0:"Power", 1:"Shot"  (0 and 1 are the token positions)  *  - combinations="1" means that in addition to the subwords, maximum runs of non-numeric subwords are catenated and produced at the same position of the last subword in the run.  *     - "PowerShot" -> 0:"Power", 1:"Shot" 1:"PowerShot"  *     - "A's+B's&C's" -> 0:"A", 1:"B", 2:"C", 2:"ABC"  *     - "Super-Duper-XL500-42-AutoCoder!" -> 0:"Super", 1:"Duper", 2:"XL", 2:"SuperDuperXL", 3:"500" 4:"42", 5:"Auto", 6:"Coder", 6:"AutoCoder"  *  *  One use for WordDelimiterFilter is to help match words with different subword delimiters.  *  For example, if the source text contained "wi-fi" one may want "wifi" "WiFi" "wi-fi" "wi+fi" queries to all match.  *  One way of doing so is to specify combinations="1" in the analyzer used for indexing, and combinations="0" (the default)  *  in the analyzer used for querying.  Given that the current StandardTokenizer immediately removes many intra-word  *  delimiters, it is recommended that this filter be used after a tokenizer that does not do this (such as WhitespaceTokenizer).  *  */
end_comment

begin_class
DECL|class|WordDelimiterFilter
specifier|public
specifier|final
class|class
name|WordDelimiterFilter
extends|extends
name|TokenFilter
block|{
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
comment|/**    * Causes parts of words to be generated:    *<p/>    * "PowerShot" => "Power" "Shot"    */
DECL|field|GENERATE_WORD_PARTS
specifier|public
specifier|static
specifier|final
name|int
name|GENERATE_WORD_PARTS
init|=
literal|1
decl_stmt|;
comment|/**    * Causes number subwords to be generated:    *<p/>    * "500-42" => "500" "42"    */
DECL|field|GENERATE_NUMBER_PARTS
specifier|public
specifier|static
specifier|final
name|int
name|GENERATE_NUMBER_PARTS
init|=
literal|2
decl_stmt|;
comment|/**    * Causes maximum runs of word parts to be catenated:    *<p/>    * "wi-fi" => "wifi"    */
DECL|field|CATENATE_WORDS
specifier|public
specifier|static
specifier|final
name|int
name|CATENATE_WORDS
init|=
literal|4
decl_stmt|;
comment|/**    * Causes maximum runs of word parts to be catenated:    *<p/>    * "wi-fi" => "wifi"    */
DECL|field|CATENATE_NUMBERS
specifier|public
specifier|static
specifier|final
name|int
name|CATENATE_NUMBERS
init|=
literal|8
decl_stmt|;
comment|/**    * Causes all subword parts to be catenated:    *<p/>    * "wi-fi-4000" => "wifi4000"    */
DECL|field|CATENATE_ALL
specifier|public
specifier|static
specifier|final
name|int
name|CATENATE_ALL
init|=
literal|16
decl_stmt|;
comment|/**    * Causes original words are preserved and added to the subword list (Defaults to false)    *<p/>    * "500-42" => "500" "42" "500-42"    */
DECL|field|PRESERVE_ORIGINAL
specifier|public
specifier|static
specifier|final
name|int
name|PRESERVE_ORIGINAL
init|=
literal|32
decl_stmt|;
comment|/**    * If not set, causes case changes to be ignored (subwords will only be generated    * given SUBWORD_DELIM tokens)    */
DECL|field|SPLIT_ON_CASE_CHANGE
specifier|public
specifier|static
specifier|final
name|int
name|SPLIT_ON_CASE_CHANGE
init|=
literal|64
decl_stmt|;
comment|/**    * If not set, causes numeric changes to be ignored (subwords will only be generated    * given SUBWORD_DELIM tokens).    */
DECL|field|SPLIT_ON_NUMERICS
specifier|public
specifier|static
specifier|final
name|int
name|SPLIT_ON_NUMERICS
init|=
literal|128
decl_stmt|;
comment|/**    * Causes trailing "'s" to be removed for each subword    *<p/>    * "O'Neil's" => "O", "Neil"    */
DECL|field|STEM_ENGLISH_POSSESSIVE
specifier|public
specifier|static
specifier|final
name|int
name|STEM_ENGLISH_POSSESSIVE
init|=
literal|256
decl_stmt|;
comment|/**    * If not null is the set of tokens to protect from being delimited    *    */
DECL|field|protWords
specifier|final
name|CharArraySet
name|protWords
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|int
name|flags
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAttribute
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAttribute
specifier|private
specifier|final
name|TypeAttribute
name|typeAttribute
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// used for iterating word delimiter breaks
DECL|field|iterator
specifier|private
specifier|final
name|WordDelimiterIterator
name|iterator
decl_stmt|;
comment|// used for concatenating runs of similar typed subwords (word,number)
DECL|field|concat
specifier|private
specifier|final
name|WordDelimiterConcatenation
name|concat
init|=
operator|new
name|WordDelimiterConcatenation
argument_list|()
decl_stmt|;
comment|// number of subwords last output by concat.
DECL|field|lastConcatCount
specifier|private
name|int
name|lastConcatCount
init|=
literal|0
decl_stmt|;
comment|// used for catenate all
DECL|field|concatAll
specifier|private
specifier|final
name|WordDelimiterConcatenation
name|concatAll
init|=
operator|new
name|WordDelimiterConcatenation
argument_list|()
decl_stmt|;
comment|// used for accumulating position increment gaps
DECL|field|accumPosInc
specifier|private
name|int
name|accumPosInc
init|=
literal|0
decl_stmt|;
DECL|field|savedBuffer
specifier|private
name|char
name|savedBuffer
index|[]
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
DECL|field|savedStartOffset
specifier|private
name|int
name|savedStartOffset
decl_stmt|;
DECL|field|savedEndOffset
specifier|private
name|int
name|savedEndOffset
decl_stmt|;
DECL|field|savedType
specifier|private
name|String
name|savedType
decl_stmt|;
DECL|field|hasSavedState
specifier|private
name|boolean
name|hasSavedState
init|=
literal|false
decl_stmt|;
comment|// if length by start + end offsets doesn't match the term text then assume
comment|// this is a synonym and don't adjust the offsets.
DECL|field|hasIllegalOffsets
specifier|private
name|boolean
name|hasIllegalOffsets
init|=
literal|false
decl_stmt|;
comment|// for a run of the same subword type within a word, have we output anything?
DECL|field|hasOutputToken
specifier|private
name|boolean
name|hasOutputToken
init|=
literal|false
decl_stmt|;
comment|// when preserve original is on, have we output any token following it?
comment|// this token must have posInc=0!
DECL|field|hasOutputFollowingOriginal
specifier|private
name|boolean
name|hasOutputFollowingOriginal
init|=
literal|false
decl_stmt|;
comment|/**    * Creates a new WordDelimiterFilter    *    * @param in TokenStream to be filtered    * @param charTypeTable table containing character types    * @param configurationFlags Flags configuring the filter    * @param protWords If not null is the set of tokens to protect from being delimited    */
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
name|configurationFlags
parameter_list|,
name|CharArraySet
name|protWords
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|configurationFlags
expr_stmt|;
name|this
operator|.
name|protWords
operator|=
name|protWords
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
operator|new
name|WordDelimiterIterator
argument_list|(
name|charTypeTable
argument_list|,
name|has
argument_list|(
name|SPLIT_ON_CASE_CHANGE
argument_list|)
argument_list|,
name|has
argument_list|(
name|SPLIT_ON_NUMERICS
argument_list|)
argument_list|,
name|has
argument_list|(
name|STEM_ENGLISH_POSSESSIVE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new WordDelimiterFilter using {@link WordDelimiterIterator#DEFAULT_WORD_DELIM_TABLE}    * as its charTypeTable    *    * @param in TokenStream to be filtered    * @param configurationFlags Flags configuring the filter    * @param protWords If not null is the set of tokens to protect from being delimited    */
DECL|method|WordDelimiterFilter
specifier|public
name|WordDelimiterFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|configurationFlags
parameter_list|,
name|CharArraySet
name|protWords
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|configurationFlags
argument_list|,
name|protWords
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|hasSavedState
condition|)
block|{
comment|// process a new input word
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|termLength
init|=
name|termAttribute
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|termBuffer
init|=
name|termAttribute
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|accumPosInc
operator|+=
name|posIncAttribute
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|iterator
operator|.
name|setText
argument_list|(
name|termBuffer
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// word of no delimiters, or protected word: just return it
if|if
condition|(
operator|(
name|iterator
operator|.
name|current
operator|==
literal|0
operator|&&
name|iterator
operator|.
name|end
operator|==
name|termLength
operator|)
operator|||
operator|(
name|protWords
operator|!=
literal|null
operator|&&
name|protWords
operator|.
name|contains
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
operator|)
condition|)
block|{
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|accumPosInc
argument_list|)
expr_stmt|;
name|accumPosInc
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// word of simply delimiters
if|if
condition|(
name|iterator
operator|.
name|end
operator|==
name|WordDelimiterIterator
operator|.
name|DONE
operator|&&
operator|!
name|has
argument_list|(
name|PRESERVE_ORIGINAL
argument_list|)
condition|)
block|{
comment|// if the posInc is 1, simply ignore it in the accumulation
if|if
condition|(
name|posIncAttribute
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
condition|)
block|{
name|accumPosInc
operator|--
expr_stmt|;
block|}
continue|continue;
block|}
name|saveState
argument_list|()
expr_stmt|;
name|hasOutputToken
operator|=
literal|false
expr_stmt|;
name|hasOutputFollowingOriginal
operator|=
operator|!
name|has
argument_list|(
name|PRESERVE_ORIGINAL
argument_list|)
expr_stmt|;
name|lastConcatCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|has
argument_list|(
name|PRESERVE_ORIGINAL
argument_list|)
condition|)
block|{
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|accumPosInc
argument_list|)
expr_stmt|;
name|accumPosInc
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|// at the end of the string, output any concatenations
if|if
condition|(
name|iterator
operator|.
name|end
operator|==
name|WordDelimiterIterator
operator|.
name|DONE
condition|)
block|{
if|if
condition|(
operator|!
name|concat
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|flushConcatenation
argument_list|(
name|concat
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
operator|!
name|concatAll
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// only if we haven't output this same combo above!
if|if
condition|(
name|concatAll
operator|.
name|subwordCount
operator|>
name|lastConcatCount
condition|)
block|{
name|concatAll
operator|.
name|writeAndClear
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|concatAll
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// no saved concatenations, on to the next input word
name|hasSavedState
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
comment|// word surrounded by delimiters: always output
if|if
condition|(
name|iterator
operator|.
name|isSingleWord
argument_list|()
condition|)
block|{
name|generatePart
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|int
name|wordType
init|=
name|iterator
operator|.
name|type
argument_list|()
decl_stmt|;
comment|// do we already have queued up incompatible concatenations?
if|if
condition|(
operator|!
name|concat
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|concat
operator|.
name|type
operator|&
name|wordType
operator|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|flushConcatenation
argument_list|(
name|concat
argument_list|)
condition|)
block|{
name|hasOutputToken
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
name|hasOutputToken
operator|=
literal|false
expr_stmt|;
block|}
comment|// add subwords depending upon options
if|if
condition|(
name|shouldConcatenate
argument_list|(
name|wordType
argument_list|)
condition|)
block|{
if|if
condition|(
name|concat
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|concat
operator|.
name|type
operator|=
name|wordType
expr_stmt|;
block|}
name|concatenate
argument_list|(
name|concat
argument_list|)
expr_stmt|;
block|}
comment|// add all subwords (catenateAll)
if|if
condition|(
name|has
argument_list|(
name|CATENATE_ALL
argument_list|)
condition|)
block|{
name|concatenate
argument_list|(
name|concatAll
argument_list|)
expr_stmt|;
block|}
comment|// if we should output the word or number part
if|if
condition|(
name|shouldGenerateParts
argument_list|(
name|wordType
argument_list|)
condition|)
block|{
name|generatePart
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|hasSavedState
operator|=
literal|false
expr_stmt|;
name|concat
operator|.
name|clear
argument_list|()
expr_stmt|;
name|concatAll
operator|.
name|clear
argument_list|()
expr_stmt|;
name|accumPosInc
operator|=
literal|0
expr_stmt|;
block|}
comment|// ================================================= Helper Methods ================================================
comment|/**    * Saves the existing attribute states    */
DECL|method|saveState
specifier|private
name|void
name|saveState
parameter_list|()
block|{
comment|// otherwise, we have delimiters, save state
name|savedStartOffset
operator|=
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|savedEndOffset
operator|=
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
expr_stmt|;
comment|// if length by start + end offsets doesn't match the term text then assume this is a synonym and don't adjust the offsets.
name|hasIllegalOffsets
operator|=
operator|(
name|savedEndOffset
operator|-
name|savedStartOffset
operator|!=
name|termAttribute
operator|.
name|length
argument_list|()
operator|)
expr_stmt|;
name|savedType
operator|=
name|typeAttribute
operator|.
name|type
argument_list|()
expr_stmt|;
if|if
condition|(
name|savedBuffer
operator|.
name|length
operator|<
name|termAttribute
operator|.
name|length
argument_list|()
condition|)
block|{
name|savedBuffer
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|termAttribute
operator|.
name|length
argument_list|()
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|termAttribute
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|savedBuffer
argument_list|,
literal|0
argument_list|,
name|termAttribute
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|text
operator|=
name|savedBuffer
expr_stmt|;
name|hasSavedState
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Flushes the given WordDelimiterConcatenation by either writing its concat and then clearing, or just clearing.    *    * @param concatenation WordDelimiterConcatenation that will be flushed    * @return {@code true} if the concatenation was written before it was cleared, {@code} false otherwise    */
DECL|method|flushConcatenation
specifier|private
name|boolean
name|flushConcatenation
parameter_list|(
name|WordDelimiterConcatenation
name|concatenation
parameter_list|)
block|{
name|lastConcatCount
operator|=
name|concatenation
operator|.
name|subwordCount
expr_stmt|;
if|if
condition|(
name|concatenation
operator|.
name|subwordCount
operator|!=
literal|1
operator|||
operator|!
name|shouldGenerateParts
argument_list|(
name|concatenation
operator|.
name|type
argument_list|)
condition|)
block|{
name|concatenation
operator|.
name|writeAndClear
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|concatenation
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/**    * Determines whether to concatenate a word or number if the current word is the given type    *    * @param wordType Type of the current word used to determine if it should be concatenated    * @return {@code true} if concatenation should occur, {@code false} otherwise    */
DECL|method|shouldConcatenate
specifier|private
name|boolean
name|shouldConcatenate
parameter_list|(
name|int
name|wordType
parameter_list|)
block|{
return|return
operator|(
name|has
argument_list|(
name|CATENATE_WORDS
argument_list|)
operator|&&
name|isAlpha
argument_list|(
name|wordType
argument_list|)
operator|)
operator|||
operator|(
name|has
argument_list|(
name|CATENATE_NUMBERS
argument_list|)
operator|&&
name|isDigit
argument_list|(
name|wordType
argument_list|)
operator|)
return|;
block|}
comment|/**    * Determines whether a word/number part should be generated for a word of the given type    *    * @param wordType Type of the word used to determine if a word/number part should be generated    * @return {@code true} if a word/number part should be generated, {@code false} otherwise    */
DECL|method|shouldGenerateParts
specifier|private
name|boolean
name|shouldGenerateParts
parameter_list|(
name|int
name|wordType
parameter_list|)
block|{
return|return
operator|(
name|has
argument_list|(
name|GENERATE_WORD_PARTS
argument_list|)
operator|&&
name|isAlpha
argument_list|(
name|wordType
argument_list|)
operator|)
operator|||
operator|(
name|has
argument_list|(
name|GENERATE_NUMBER_PARTS
argument_list|)
operator|&&
name|isDigit
argument_list|(
name|wordType
argument_list|)
operator|)
return|;
block|}
comment|/**    * Concatenates the saved buffer to the given WordDelimiterConcatenation    *    * @param concatenation WordDelimiterConcatenation to concatenate the buffer to    */
DECL|method|concatenate
specifier|private
name|void
name|concatenate
parameter_list|(
name|WordDelimiterConcatenation
name|concatenation
parameter_list|)
block|{
if|if
condition|(
name|concatenation
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|concatenation
operator|.
name|startOffset
operator|=
name|savedStartOffset
operator|+
name|iterator
operator|.
name|current
expr_stmt|;
block|}
name|concatenation
operator|.
name|append
argument_list|(
name|savedBuffer
argument_list|,
name|iterator
operator|.
name|current
argument_list|,
name|iterator
operator|.
name|end
operator|-
name|iterator
operator|.
name|current
argument_list|)
expr_stmt|;
name|concatenation
operator|.
name|endOffset
operator|=
name|savedStartOffset
operator|+
name|iterator
operator|.
name|end
expr_stmt|;
block|}
comment|/**    * Generates a word/number part, updating the appropriate attributes    *    * @param isSingleWord {@code true} if the generation is occurring from a single word, {@code false} otherwise    */
DECL|method|generatePart
specifier|private
name|void
name|generatePart
parameter_list|(
name|boolean
name|isSingleWord
parameter_list|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|copyBuffer
argument_list|(
name|savedBuffer
argument_list|,
name|iterator
operator|.
name|current
argument_list|,
name|iterator
operator|.
name|end
operator|-
name|iterator
operator|.
name|current
argument_list|)
expr_stmt|;
name|int
name|startOffSet
init|=
operator|(
name|isSingleWord
operator|||
operator|!
name|hasIllegalOffsets
operator|)
condition|?
name|savedStartOffset
operator|+
name|iterator
operator|.
name|current
else|:
name|savedStartOffset
decl_stmt|;
name|int
name|endOffSet
init|=
operator|(
name|hasIllegalOffsets
operator|)
condition|?
name|savedEndOffset
else|:
name|savedStartOffset
operator|+
name|iterator
operator|.
name|end
decl_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|startOffSet
argument_list|,
name|endOffSet
argument_list|)
expr_stmt|;
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|position
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|typeAttribute
operator|.
name|setType
argument_list|(
name|savedType
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the position increment gap for a subword or concatenation    *    * @param inject true if this token wants to be injected    * @return position increment gap    */
DECL|method|position
specifier|private
name|int
name|position
parameter_list|(
name|boolean
name|inject
parameter_list|)
block|{
name|int
name|posInc
init|=
name|accumPosInc
decl_stmt|;
if|if
condition|(
name|hasOutputToken
condition|)
block|{
name|accumPosInc
operator|=
literal|0
expr_stmt|;
return|return
name|inject
condition|?
literal|0
else|:
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|posInc
argument_list|)
return|;
block|}
name|hasOutputToken
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|hasOutputFollowingOriginal
condition|)
block|{
comment|// the first token following the original is 0 regardless
name|hasOutputFollowingOriginal
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|// clear the accumulated position increment
name|accumPosInc
operator|=
literal|0
expr_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|posInc
argument_list|)
return|;
block|}
comment|/**    * Checks if the given word type includes {@link #ALPHA}    *    * @param type Word type to check    * @return {@code true} if the type contains ALPHA, {@code false} otherwise    */
DECL|method|isAlpha
specifier|static
name|boolean
name|isAlpha
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
operator|(
name|type
operator|&
name|ALPHA
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Checks if the given word type includes {@link #DIGIT}    *    * @param type Word type to check    * @return {@code true} if the type contains DIGIT, {@code false} otherwise    */
DECL|method|isDigit
specifier|static
name|boolean
name|isDigit
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
operator|(
name|type
operator|&
name|DIGIT
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Checks if the given word type includes {@link #SUBWORD_DELIM}    *    * @param type Word type to check    * @return {@code true} if the type contains SUBWORD_DELIM, {@code false} otherwise    */
DECL|method|isSubwordDelim
specifier|static
name|boolean
name|isSubwordDelim
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
operator|(
name|type
operator|&
name|SUBWORD_DELIM
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Checks if the given word type includes {@link #UPPER}    *    * @param type Word type to check    * @return {@code true} if the type contains UPPER, {@code false} otherwise    */
DECL|method|isUpper
specifier|static
name|boolean
name|isUpper
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
operator|(
name|type
operator|&
name|UPPER
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * Determines whether the given flag is set    *    * @param flag Flag to see if set    * @return {@code} true if flag is set    */
DECL|method|has
specifier|private
name|boolean
name|has
parameter_list|(
name|int
name|flag
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|&
name|flag
operator|)
operator|!=
literal|0
return|;
block|}
comment|// ================================================= Inner Classes =================================================
comment|/**    * A WDF concatenated 'run'    */
DECL|class|WordDelimiterConcatenation
specifier|final
class|class
name|WordDelimiterConcatenation
block|{
DECL|field|buffer
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|field|type
name|int
name|type
decl_stmt|;
DECL|field|subwordCount
name|int
name|subwordCount
decl_stmt|;
comment|/**      * Appends the given text of the given length, to the concetenation at the given offset      *      * @param text Text to append      * @param offset Offset in the concetenation to add the text      * @param length Length of the text to append      */
DECL|method|append
name|void
name|append
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|subwordCount
operator|++
expr_stmt|;
block|}
comment|/**      * Writes the concatenation to the attributes      */
DECL|method|write
name|void
name|write
parameter_list|()
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|termAttribute
operator|.
name|length
argument_list|()
operator|<
name|buffer
operator|.
name|length
argument_list|()
condition|)
block|{
name|termAttribute
operator|.
name|resizeBuffer
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|char
name|termbuffer
index|[]
init|=
name|termAttribute
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|,
name|termbuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termAttribute
operator|.
name|setLength
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasIllegalOffsets
condition|)
block|{
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|savedStartOffset
argument_list|,
name|savedEndOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|position
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|typeAttribute
operator|.
name|setType
argument_list|(
name|savedType
argument_list|)
expr_stmt|;
name|accumPosInc
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Determines if the concatenation is empty      *      * @return {@code true} if the concatenation is empty, {@code false} otherwise      */
DECL|method|isEmpty
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|length
argument_list|()
operator|==
literal|0
return|;
block|}
comment|/**      * Clears the concatenation and resets its state      */
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|endOffset
operator|=
name|type
operator|=
name|subwordCount
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Convenience method for the common scenario of having to write the concetenation and then clearing its state      */
DECL|method|writeAndClear
name|void
name|writeAndClear
parameter_list|()
block|{
name|write
argument_list|()
expr_stmt|;
name|clear
argument_list|()
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

