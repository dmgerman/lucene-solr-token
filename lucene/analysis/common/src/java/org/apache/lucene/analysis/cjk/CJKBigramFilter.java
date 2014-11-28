begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
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
name|IOException
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
name|standard
operator|.
name|StandardTokenizer
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
name|PositionLengthAttribute
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
name|util
operator|.
name|ArrayUtil
import|;
end_import

begin_comment
comment|/**  * Forms bigrams of CJK terms that are generated from StandardTokenizer  * or ICUTokenizer.  *<p>  * CJK types are set by these tokenizers, but you can also use   * {@link #CJKBigramFilter(TokenStream, int)} to explicitly control which  * of the CJK scripts are turned into bigrams.  *<p>  * By default, when a CJK character has no adjacent characters to form  * a bigram, it is output in unigram form. If you want to always output  * both unigrams and bigrams, set the<code>outputUnigrams</code>  * flag in {@link CJKBigramFilter#CJKBigramFilter(TokenStream, int, boolean)}.  * This can be used for a combined unigram+bigram approach.  *<p>  * In all cases, all non-CJK input is passed thru unmodified.  */
end_comment

begin_class
DECL|class|CJKBigramFilter
specifier|public
specifier|final
class|class
name|CJKBigramFilter
extends|extends
name|TokenFilter
block|{
comment|// configuration
comment|/** bigram flag for Han Ideographs */
DECL|field|HAN
specifier|public
specifier|static
specifier|final
name|int
name|HAN
init|=
literal|1
decl_stmt|;
comment|/** bigram flag for Hiragana */
DECL|field|HIRAGANA
specifier|public
specifier|static
specifier|final
name|int
name|HIRAGANA
init|=
literal|2
decl_stmt|;
comment|/** bigram flag for Katakana */
DECL|field|KATAKANA
specifier|public
specifier|static
specifier|final
name|int
name|KATAKANA
init|=
literal|4
decl_stmt|;
comment|/** bigram flag for Hangul */
DECL|field|HANGUL
specifier|public
specifier|static
specifier|final
name|int
name|HANGUL
init|=
literal|8
decl_stmt|;
comment|/** when we emit a bigram, its then marked as this type */
DECL|field|DOUBLE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DOUBLE_TYPE
init|=
literal|"<DOUBLE>"
decl_stmt|;
comment|/** when we emit a unigram, its then marked as this type */
DECL|field|SINGLE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|SINGLE_TYPE
init|=
literal|"<SINGLE>"
decl_stmt|;
comment|// the types from standardtokenizer
DECL|field|HAN_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|HAN_TYPE
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|IDEOGRAPHIC
index|]
decl_stmt|;
DECL|field|HIRAGANA_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|HIRAGANA_TYPE
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|HIRAGANA
index|]
decl_stmt|;
DECL|field|KATAKANA_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|KATAKANA_TYPE
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|KATAKANA
index|]
decl_stmt|;
DECL|field|HANGUL_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|HANGUL_TYPE
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizer
operator|.
name|HANGUL
index|]
decl_stmt|;
comment|// sentinel value for ignoring a script
DECL|field|NO
specifier|private
specifier|static
specifier|final
name|Object
name|NO
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// these are set to either their type or NO if we want to pass them thru
DECL|field|doHan
specifier|private
specifier|final
name|Object
name|doHan
decl_stmt|;
DECL|field|doHiragana
specifier|private
specifier|final
name|Object
name|doHiragana
decl_stmt|;
DECL|field|doKatakana
specifier|private
specifier|final
name|Object
name|doKatakana
decl_stmt|;
DECL|field|doHangul
specifier|private
specifier|final
name|Object
name|doHangul
decl_stmt|;
comment|// true if we should output unigram tokens always
DECL|field|outputUnigrams
specifier|private
specifier|final
name|boolean
name|outputUnigrams
decl_stmt|;
DECL|field|ngramState
specifier|private
name|boolean
name|ngramState
decl_stmt|;
comment|// false = output unigram, true = output bigram
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posLengthAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLengthAtt
init|=
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// buffers containing codepoint and offsets in parallel
DECL|field|buffer
name|int
name|buffer
index|[]
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
index|[]
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
index|[]
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
comment|// length of valid buffer
DECL|field|bufferLen
name|int
name|bufferLen
decl_stmt|;
comment|// current buffer index
DECL|field|index
name|int
name|index
decl_stmt|;
comment|// the last end offset, to determine if we should bigram across tokens
DECL|field|lastEndOffset
name|int
name|lastEndOffset
decl_stmt|;
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
decl_stmt|;
comment|/**     * Calls {@link CJKBigramFilter#CJKBigramFilter(TokenStream, int)    *       CJKBigramFilter(in, HAN | HIRAGANA | KATAKANA | HANGUL)}    */
DECL|method|CJKBigramFilter
specifier|public
name|CJKBigramFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|HAN
operator||
name|HIRAGANA
operator||
name|KATAKANA
operator||
name|HANGUL
argument_list|)
expr_stmt|;
block|}
comment|/**     * Calls {@link CJKBigramFilter#CJKBigramFilter(TokenStream, int, boolean)    *       CJKBigramFilter(in, flags, false)}    */
DECL|method|CJKBigramFilter
specifier|public
name|CJKBigramFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|flags
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new CJKBigramFilter, specifying which writing systems should be bigrammed,    * and whether or not unigrams should also be output.    * @param flags OR'ed set from {@link CJKBigramFilter#HAN}, {@link CJKBigramFilter#HIRAGANA},     *        {@link CJKBigramFilter#KATAKANA}, {@link CJKBigramFilter#HANGUL}    * @param outputUnigrams true if unigrams for the selected writing systems should also be output.    *        when this is false, this is only done when there are no adjacent characters to form    *        a bigram.    */
DECL|method|CJKBigramFilter
specifier|public
name|CJKBigramFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|flags
parameter_list|,
name|boolean
name|outputUnigrams
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|doHan
operator|=
operator|(
name|flags
operator|&
name|HAN
operator|)
operator|==
literal|0
condition|?
name|NO
else|:
name|HAN_TYPE
expr_stmt|;
name|doHiragana
operator|=
operator|(
name|flags
operator|&
name|HIRAGANA
operator|)
operator|==
literal|0
condition|?
name|NO
else|:
name|HIRAGANA_TYPE
expr_stmt|;
name|doKatakana
operator|=
operator|(
name|flags
operator|&
name|KATAKANA
operator|)
operator|==
literal|0
condition|?
name|NO
else|:
name|KATAKANA_TYPE
expr_stmt|;
name|doHangul
operator|=
operator|(
name|flags
operator|&
name|HANGUL
operator|)
operator|==
literal|0
condition|?
name|NO
else|:
name|HANGUL_TYPE
expr_stmt|;
name|this
operator|.
name|outputUnigrams
operator|=
name|outputUnigrams
expr_stmt|;
block|}
comment|/*    * much of this complexity revolves around handling the special case of a     * "lone cjk character" where cjktokenizer would output a unigram. this     * is also the only time we ever have to captureState.    */
annotation|@
name|Override
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
name|hasBufferedBigram
argument_list|()
condition|)
block|{
comment|// case 1: we have multiple remaining codepoints buffered,
comment|// so we can emit a bigram here.
if|if
condition|(
name|outputUnigrams
condition|)
block|{
comment|// when also outputting unigrams, we output the unigram first,
comment|// then rewind back to revisit the bigram.
comment|// so an input of ABC is A + (rewind)AB + B + (rewind)BC + C
comment|// the logic in hasBufferedUnigram ensures we output the C,
comment|// even though it did actually have adjacent CJK characters.
if|if
condition|(
name|ngramState
condition|)
block|{
name|flushBigram
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|flushUnigram
argument_list|()
expr_stmt|;
name|index
operator|--
expr_stmt|;
block|}
name|ngramState
operator|=
operator|!
name|ngramState
expr_stmt|;
block|}
else|else
block|{
name|flushBigram
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|doNext
argument_list|()
condition|)
block|{
comment|// case 2: look at the token type. should we form any n-grams?
name|String
name|type
init|=
name|typeAtt
operator|.
name|type
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|doHan
operator|||
name|type
operator|==
name|doHiragana
operator|||
name|type
operator|==
name|doKatakana
operator|||
name|type
operator|==
name|doHangul
condition|)
block|{
comment|// acceptable CJK type: we form n-grams from these.
comment|// as long as the offsets are aligned, we just add these to our current buffer.
comment|// otherwise, we clear the buffer and start over.
if|if
condition|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
operator|!=
name|lastEndOffset
condition|)
block|{
comment|// unaligned, clear queue
if|if
condition|(
name|hasBufferedUnigram
argument_list|()
condition|)
block|{
comment|// we have a buffered unigram, and we peeked ahead to see if we could form
comment|// a bigram, but we can't, because the offsets are unaligned. capture the state
comment|// of this peeked data to be revisited next time thru the loop, and dump our unigram.
name|loneState
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|flushUnigram
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|index
operator|=
literal|0
expr_stmt|;
name|bufferLen
operator|=
literal|0
expr_stmt|;
block|}
name|refill
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// not a CJK type: we just return these as-is.
if|if
condition|(
name|hasBufferedUnigram
argument_list|()
condition|)
block|{
comment|// we have a buffered unigram, and we peeked ahead to see if we could form
comment|// a bigram, but we can't, because its not a CJK type. capture the state
comment|// of this peeked data to be revisited next time thru the loop, and dump our unigram.
name|loneState
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|flushUnigram
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
comment|// case 3: we have only zero or 1 codepoints buffered,
comment|// so not enough to form a bigram. But, we also have no
comment|// more input. So if we have a buffered codepoint, emit
comment|// a unigram, otherwise, its end of stream.
if|if
condition|(
name|hasBufferedUnigram
argument_list|()
condition|)
block|{
name|flushUnigram
argument_list|()
expr_stmt|;
comment|// flush our remaining unigram
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
DECL|field|loneState
specifier|private
name|State
name|loneState
decl_stmt|;
comment|// rarely used: only for "lone cjk characters", where we emit unigrams
comment|/**     * looks at next input token, returning false is none is available     */
DECL|method|doNext
specifier|private
name|boolean
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|loneState
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|loneState
argument_list|)
expr_stmt|;
name|loneState
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|exhausted
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|exhausted
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * refills buffers with new data from the current token.    */
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|()
block|{
comment|// compact buffers to keep them smallish if they become large
comment|// just a safety check, but technically we only need the last codepoint
if|if
condition|(
name|bufferLen
operator|>
literal|64
condition|)
block|{
name|int
name|last
init|=
name|bufferLen
operator|-
literal|1
decl_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
name|buffer
index|[
name|last
index|]
expr_stmt|;
name|startOffset
index|[
literal|0
index|]
operator|=
name|startOffset
index|[
name|last
index|]
expr_stmt|;
name|endOffset
index|[
literal|0
index|]
operator|=
name|endOffset
index|[
name|last
index|]
expr_stmt|;
name|bufferLen
operator|=
literal|1
expr_stmt|;
name|index
operator|-=
name|last
expr_stmt|;
block|}
name|char
name|termBuffer
index|[]
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|int
name|newSize
init|=
name|bufferLen
operator|+
name|len
decl_stmt|;
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|startOffset
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|endOffset
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|endOffset
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|lastEndOffset
operator|=
name|end
expr_stmt|;
if|if
condition|(
name|end
operator|-
name|start
operator|!=
name|len
condition|)
block|{
comment|// crazy offsets (modified by synonym or charfilter): just preserve
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|cp
operator|=
name|buffer
index|[
name|bufferLen
index|]
operator|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|termBuffer
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|startOffset
index|[
name|bufferLen
index|]
operator|=
name|start
expr_stmt|;
name|endOffset
index|[
name|bufferLen
index|]
operator|=
name|end
expr_stmt|;
name|bufferLen
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// normal offsets
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|,
name|cpLen
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|+=
name|cpLen
control|)
block|{
name|cp
operator|=
name|buffer
index|[
name|bufferLen
index|]
operator|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|termBuffer
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|cpLen
operator|=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|startOffset
index|[
name|bufferLen
index|]
operator|=
name|start
expr_stmt|;
name|start
operator|=
name|endOffset
index|[
name|bufferLen
index|]
operator|=
name|start
operator|+
name|cpLen
expr_stmt|;
name|bufferLen
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|/**     * Flushes a bigram token to output from our buffer     * This is the normal case, e.g. ABC -&gt; AB BC    */
DECL|method|flushBigram
specifier|private
name|void
name|flushBigram
parameter_list|()
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|char
name|termBuffer
index|[]
init|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|// maximum bigram length in code units (2 supplementaries)
name|int
name|len1
init|=
name|Character
operator|.
name|toChars
argument_list|(
name|buffer
index|[
name|index
index|]
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|len2
init|=
name|len1
operator|+
name|Character
operator|.
name|toChars
argument_list|(
name|buffer
index|[
name|index
operator|+
literal|1
index|]
argument_list|,
name|termBuffer
argument_list|,
name|len1
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|len2
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|startOffset
index|[
name|index
index|]
argument_list|,
name|endOffset
index|[
name|index
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|DOUBLE_TYPE
argument_list|)
expr_stmt|;
comment|// when outputting unigrams, all bigrams are synonyms that span two unigrams
if|if
condition|(
name|outputUnigrams
condition|)
block|{
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|posLengthAtt
operator|.
name|setPositionLength
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|index
operator|++
expr_stmt|;
block|}
comment|/**     * Flushes a unigram token to output from our buffer.    * This happens when we encounter isolated CJK characters, either the whole    * CJK string is a single character, or we encounter a CJK character surrounded     * by space, punctuation, english, etc, but not beside any other CJK.    */
DECL|method|flushUnigram
specifier|private
name|void
name|flushUnigram
parameter_list|()
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|char
name|termBuffer
index|[]
init|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// maximum unigram length (2 surrogates)
name|int
name|len
init|=
name|Character
operator|.
name|toChars
argument_list|(
name|buffer
index|[
name|index
index|]
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|startOffset
index|[
name|index
index|]
argument_list|,
name|endOffset
index|[
name|index
index|]
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|SINGLE_TYPE
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
comment|/**    * True if we have multiple codepoints sitting in our buffer    */
DECL|method|hasBufferedBigram
specifier|private
name|boolean
name|hasBufferedBigram
parameter_list|()
block|{
return|return
name|bufferLen
operator|-
name|index
operator|>
literal|1
return|;
block|}
comment|/**    * True if we have a single codepoint sitting in our buffer, where its future    * (whether it is emitted as unigram or forms a bigram) depends upon not-yet-seen    * inputs.    */
DECL|method|hasBufferedUnigram
specifier|private
name|boolean
name|hasBufferedUnigram
parameter_list|()
block|{
if|if
condition|(
name|outputUnigrams
condition|)
block|{
comment|// when outputting unigrams always
return|return
name|bufferLen
operator|-
name|index
operator|==
literal|1
return|;
block|}
else|else
block|{
comment|// otherwise its only when we have a lone CJK character
return|return
name|bufferLen
operator|==
literal|1
operator|&&
name|index
operator|==
literal|0
return|;
block|}
block|}
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
name|bufferLen
operator|=
literal|0
expr_stmt|;
name|index
operator|=
literal|0
expr_stmt|;
name|lastEndOffset
operator|=
literal|0
expr_stmt|;
name|loneState
operator|=
literal|null
expr_stmt|;
name|exhausted
operator|=
literal|false
expr_stmt|;
name|ngramState
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

