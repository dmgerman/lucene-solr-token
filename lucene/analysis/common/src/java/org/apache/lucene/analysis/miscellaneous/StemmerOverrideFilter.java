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
name|util
operator|.
name|ArrayList
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
name|KeywordAttribute
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
name|BytesRef
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
name|BytesRefBuilder
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
name|BytesRefHash
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
name|CharsRefBuilder
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
name|IntsRefBuilder
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
name|UnicodeUtil
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
name|fst
operator|.
name|ByteSequenceOutputs
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|FST
operator|.
name|Arc
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
name|fst
operator|.
name|FST
operator|.
name|BytesReader
import|;
end_import

begin_comment
comment|/**  * Provides the ability to override any {@link KeywordAttribute} aware stemmer  * with custom dictionary-based stemming.  */
end_comment

begin_class
DECL|class|StemmerOverrideFilter
specifier|public
specifier|final
class|class
name|StemmerOverrideFilter
extends|extends
name|TokenFilter
block|{
DECL|field|stemmerOverrideMap
specifier|private
specifier|final
name|StemmerOverrideMap
name|stemmerOverrideMap
decl_stmt|;
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
DECL|field|keywordAtt
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAtt
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|fstReader
specifier|private
specifier|final
name|BytesReader
name|fstReader
decl_stmt|;
DECL|field|scratchArc
specifier|private
specifier|final
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|scratchArc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|spare
specifier|private
name|char
index|[]
name|spare
init|=
operator|new
name|char
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Create a new StemmerOverrideFilter, performing dictionary-based stemming    * with the provided<code>dictionary</code>.    *<p>    * Any dictionary-stemmed terms will be marked with {@link KeywordAttribute}    * so that they will not be stemmed with stemmers down the chain.    *</p>    */
DECL|method|StemmerOverrideFilter
specifier|public
name|StemmerOverrideFilter
parameter_list|(
specifier|final
name|TokenStream
name|input
parameter_list|,
specifier|final
name|StemmerOverrideMap
name|stemmerOverrideMap
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemmerOverrideMap
operator|=
name|stemmerOverrideMap
expr_stmt|;
name|fstReader
operator|=
name|stemmerOverrideMap
operator|.
name|getBytesReader
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|fstReader
operator|==
literal|null
condition|)
block|{
comment|// No overrides
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|keywordAtt
operator|.
name|isKeyword
argument_list|()
condition|)
block|{
comment|// don't muck with already-keyworded terms
specifier|final
name|BytesRef
name|stem
init|=
name|stemmerOverrideMap
operator|.
name|get
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|,
name|scratchArc
argument_list|,
name|fstReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|stem
operator|!=
literal|null
condition|)
block|{
name|spare
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|stem
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|stem
argument_list|,
name|spare
argument_list|)
decl_stmt|;
if|if
condition|(
name|spare
operator|!=
name|termAtt
operator|.
name|buffer
argument_list|()
condition|)
block|{
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|spare
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
name|keywordAtt
operator|.
name|setKeyword
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * A read-only 4-byte FST backed map that allows fast case-insensitive key    * value lookups for {@link StemmerOverrideFilter}    */
comment|// TODO maybe we can generalize this and reuse this map somehow?
DECL|class|StemmerOverrideMap
specifier|public
specifier|final
specifier|static
class|class
name|StemmerOverrideMap
block|{
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
comment|/**      * Creates a new {@link StemmerOverrideMap}       * @param fst the fst to lookup the overrides      * @param ignoreCase if the keys case should be ingored      */
DECL|method|StemmerOverrideMap
specifier|public
name|StemmerOverrideMap
parameter_list|(
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
block|}
comment|/**      * Returns a {@link BytesReader} to pass to the {@link #get(char[], int, FST.Arc, FST.BytesReader)} method.      */
DECL|method|getBytesReader
specifier|public
name|BytesReader
name|getBytesReader
parameter_list|()
block|{
if|if
condition|(
name|fst
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|fst
operator|.
name|getBytesReader
argument_list|()
return|;
block|}
block|}
comment|/**      * Returns the value mapped to the given key or<code>null</code> if the key is not in the FST dictionary.      */
DECL|method|get
specifier|public
name|BytesRef
name|get
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|bufferLen
parameter_list|,
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|scratchArc
parameter_list|,
name|BytesReader
name|fstReader
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|pendingOutput
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|BytesRef
name|matchOutput
init|=
literal|null
decl_stmt|;
name|int
name|bufUpto
init|=
literal|0
decl_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|scratchArc
argument_list|)
expr_stmt|;
while|while
condition|(
name|bufUpto
operator|<
name|bufferLen
condition|)
block|{
specifier|final
name|int
name|codePoint
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|buffer
argument_list|,
name|bufUpto
argument_list|,
name|bufferLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|ignoreCase
condition|?
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codePoint
argument_list|)
else|:
name|codePoint
argument_list|,
name|scratchArc
argument_list|,
name|scratchArc
argument_list|,
name|fstReader
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|pendingOutput
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|pendingOutput
argument_list|,
name|scratchArc
operator|.
name|output
argument_list|)
expr_stmt|;
name|bufUpto
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePoint
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scratchArc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|matchOutput
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|pendingOutput
argument_list|,
name|scratchArc
operator|.
name|nextFinalOutput
argument_list|)
expr_stmt|;
block|}
return|return
name|matchOutput
return|;
block|}
block|}
comment|/**    * This builder builds an {@link FST} for the {@link StemmerOverrideFilter}    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|hash
specifier|private
specifier|final
name|BytesRefHash
name|hash
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|outputValues
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|CharSequence
argument_list|>
name|outputValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|charsSpare
specifier|private
specifier|final
name|CharsRefBuilder
name|charsSpare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
comment|/**      * Creates a new {@link Builder} with ignoreCase set to<code>false</code>       */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new {@link Builder}      * @param ignoreCase if the input case should be ignored.      */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
block|}
comment|/**      * Adds an input string and its stemmer override output to this builder.      *       * @param input the input char sequence       * @param output the stemmer override output char sequence      * @return<code>false</code> iff the input has already been added to this builder otherwise<code>true</code>.      */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|CharSequence
name|input
parameter_list|,
name|CharSequence
name|output
parameter_list|)
block|{
specifier|final
name|int
name|length
init|=
name|input
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|ignoreCase
condition|)
block|{
comment|// convert on the fly to lowercase
name|charsSpare
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
specifier|final
name|char
index|[]
name|buffer
init|=
name|charsSpare
operator|.
name|chars
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
name|length
condition|;
control|)
block|{
name|i
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|Character
operator|.
name|codePointAt
argument_list|(
name|input
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|,
name|buffer
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|spare
operator|.
name|copyChars
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|spare
operator|.
name|copyChars
argument_list|(
name|input
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hash
operator|.
name|add
argument_list|(
name|spare
operator|.
name|get
argument_list|()
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|outputValues
operator|.
name|add
argument_list|(
name|output
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns an {@link StemmerOverrideMap} to be used with the {@link StemmerOverrideFilter}      * @return an {@link StemmerOverrideMap} to be used with the {@link StemmerOverrideFilter}      * @throws IOException if an {@link IOException} occurs;      */
DECL|method|build
specifier|public
name|StemmerOverrideMap
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteSequenceOutputs
name|outputs
init|=
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|Builder
argument_list|<
name|BytesRef
argument_list|>
name|builder
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|sort
init|=
name|hash
operator|.
name|sort
argument_list|()
decl_stmt|;
name|IntsRefBuilder
name|intsSpare
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|sort
index|[
name|i
index|]
decl_stmt|;
name|BytesRef
name|bytesRef
init|=
name|hash
operator|.
name|get
argument_list|(
name|id
argument_list|,
name|spare
argument_list|)
decl_stmt|;
name|intsSpare
operator|.
name|copyUTF8Bytes
argument_list|(
name|bytesRef
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|intsSpare
operator|.
name|get
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|outputValues
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StemmerOverrideMap
argument_list|(
name|builder
operator|.
name|finish
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

