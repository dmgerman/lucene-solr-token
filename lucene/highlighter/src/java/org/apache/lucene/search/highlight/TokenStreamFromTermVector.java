begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|PayloadAttribute
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
name|index
operator|.
name|DocsAndPositionsEnum
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * TokenStream created from a term vector field. The term vector requires positions and/or offsets (either). If you  * want payloads add PayloadAttributeImpl (as you would normally) but don't assume the attribute is already added just  * because you know the term vector has payloads.  This TokenStream supports an efficient {@link #reset()}, so there's  * no need to wrap with a caching impl.  *<p />  * The implementation will create an array of tokens indexed by token position.  As long as there aren't massive jumps  * in positions, this is fine.  And it assumes there aren't large numbers of tokens at the same position, since it adds  * them to a linked-list per position in O(N^2) complexity.  When there aren't positions in the term vector, it divides  * the startOffset by 8 to use as a temporary substitute. In that case, tokens with the same startOffset will occupy  * the same final position; otherwise tokens become adjacent.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|TokenStreamFromTermVector
specifier|public
specifier|final
class|class
name|TokenStreamFromTermVector
extends|extends
name|TokenStream
block|{
comment|//TODO add a maxStartOffset filter, which highlighters will find handy
DECL|field|vector
specifier|private
specifier|final
name|Terms
name|vector
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
decl_stmt|;
DECL|field|positionIncrementAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|positionIncrementAttribute
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
comment|//maybe null
DECL|field|payloadAttribute
specifier|private
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
comment|//maybe null
DECL|field|firstToken
specifier|private
name|TokenLL
name|firstToken
init|=
literal|null
decl_stmt|;
comment|// the head of a linked-list
DECL|field|incrementToken
specifier|private
name|TokenLL
name|incrementToken
init|=
literal|null
decl_stmt|;
comment|/**    * Constructor.    *     * @param vector Terms that contains the data for    *        creating the TokenStream. Must have positions and/or offsets.    */
DECL|method|TokenStreamFromTermVector
specifier|public
name|TokenStreamFromTermVector
parameter_list|(
name|Terms
name|vector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|vector
operator|.
name|hasPositions
argument_list|()
operator|&&
operator|!
name|vector
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The term vector needs positions and/or offsets."
argument_list|)
throw|;
block|}
assert|assert
name|vector
operator|.
name|hasFreqs
argument_list|()
assert|;
name|this
operator|.
name|vector
operator|=
name|vector
expr_stmt|;
name|termAttribute
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermVectorTerms
specifier|public
name|Terms
name|getTermVectorTerms
parameter_list|()
block|{
return|return
name|vector
return|;
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
if|if
condition|(
name|firstToken
operator|==
literal|null
condition|)
block|{
comment|//just the first time
name|init
argument_list|()
expr_stmt|;
block|}
name|incrementToken
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|//We initialize in reset() because we can see which attributes the consumer wants, particularly payloads
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|vector
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
name|offsetAttribute
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vector
operator|.
name|hasPayloads
argument_list|()
operator|&&
name|hasAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|payloadAttribute
operator|=
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// Step 1: iterate termsEnum and create a token, placing into an array of tokens by position
name|TokenLL
index|[]
name|positionedTokens
init|=
name|initTokensArray
argument_list|()
decl_stmt|;
name|int
name|lastPosition
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|termBytesRef
decl_stmt|;
name|DocsAndPositionsEnum
name|dpEnum
init|=
literal|null
decl_stmt|;
comment|//int sumFreq = 0;
while|while
condition|(
operator|(
name|termBytesRef
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|//Grab the term (in same way as BytesRef.utf8ToString() but we don't want a String obj)
comment|// note: if term vectors supported seek by ord then we might just keep an int and seek by ord on-demand
specifier|final
name|char
index|[]
name|termChars
init|=
operator|new
name|char
index|[
name|termBytesRef
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|termCharsLen
init|=
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|termBytesRef
argument_list|,
name|termChars
argument_list|)
decl_stmt|;
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|)
expr_stmt|;
assert|assert
name|dpEnum
operator|!=
literal|null
assert|;
comment|// presumably checked by TokenSources.hasPositions earlier
name|dpEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
specifier|final
name|int
name|freq
init|=
name|dpEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
comment|//sumFreq += freq;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|int
name|pos
init|=
name|dpEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|TokenLL
name|token
init|=
operator|new
name|TokenLL
argument_list|()
decl_stmt|;
name|token
operator|.
name|termChars
operator|=
name|termChars
expr_stmt|;
name|token
operator|.
name|termCharsLen
operator|=
name|termCharsLen
expr_stmt|;
if|if
condition|(
name|offsetAttribute
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|startOffset
operator|=
name|dpEnum
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|token
operator|.
name|endOffset
operator|=
name|dpEnum
operator|.
name|endOffset
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
name|pos
operator|=
name|token
operator|.
name|startOffset
operator|>>
literal|3
expr_stmt|;
comment|//divide by 8
block|}
block|}
if|if
condition|(
name|payloadAttribute
operator|!=
literal|null
condition|)
block|{
comment|// Must make a deep copy of the returned payload,
comment|// since D&PEnum API is allowed to re-use on every
comment|// call:
specifier|final
name|BytesRef
name|payload
init|=
name|dpEnum
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|payload
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|payload
argument_list|)
expr_stmt|;
comment|//TODO share a ByteBlockPool& re-use BytesRef
block|}
block|}
comment|//Add token to an array indexed by position
if|if
condition|(
name|positionedTokens
operator|.
name|length
operator|<=
name|pos
condition|)
block|{
comment|//grow, but not 2x since we think our original length estimate is close
name|TokenLL
index|[]
name|newPositionedTokens
init|=
operator|new
name|TokenLL
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|pos
operator|+
literal|1
operator|)
operator|*
literal|1.5f
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|positionedTokens
argument_list|,
literal|0
argument_list|,
name|newPositionedTokens
argument_list|,
literal|0
argument_list|,
name|lastPosition
operator|+
literal|1
argument_list|)
expr_stmt|;
name|positionedTokens
operator|=
name|newPositionedTokens
expr_stmt|;
block|}
name|positionedTokens
index|[
name|pos
index|]
operator|=
name|token
operator|.
name|insertIntoSortedLinkedList
argument_list|(
name|positionedTokens
index|[
name|pos
index|]
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|Math
operator|.
name|max
argument_list|(
name|lastPosition
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
comment|//    System.out.println(String.format(
comment|//        "SumFreq: %5d Size: %4d SumFreq/size: %3.3f MaxPos: %4d MaxPos/SumFreq: %3.3f WastePct: %3.3f",
comment|//        sumFreq, vector.size(), (sumFreq / (float)vector.size()), lastPosition, ((float)lastPosition)/sumFreq,
comment|//        (originalPositionEstimate/(lastPosition + 1.0f))));
comment|// Step 2:  Link all Tokens into a linked-list and set position increments as we go
name|int
name|prevTokenPos
init|=
operator|-
literal|1
decl_stmt|;
name|TokenLL
name|prevToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<=
name|lastPosition
condition|;
name|pos
operator|++
control|)
block|{
name|TokenLL
name|token
init|=
name|positionedTokens
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|//link
if|if
condition|(
name|prevToken
operator|!=
literal|null
condition|)
block|{
assert|assert
name|prevToken
operator|.
name|next
operator|==
literal|null
assert|;
name|prevToken
operator|.
name|next
operator|=
name|token
expr_stmt|;
comment|//concatenate linked-list
block|}
else|else
block|{
assert|assert
name|firstToken
operator|==
literal|null
assert|;
name|firstToken
operator|=
name|token
expr_stmt|;
block|}
comment|//set increments
if|if
condition|(
name|vector
operator|.
name|hasPositions
argument_list|()
condition|)
block|{
name|token
operator|.
name|positionIncrement
operator|=
name|pos
operator|-
name|prevTokenPos
expr_stmt|;
while|while
condition|(
name|token
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
name|token
operator|=
name|token
operator|.
name|next
expr_stmt|;
name|token
operator|.
name|positionIncrement
operator|=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
name|token
operator|.
name|positionIncrement
operator|=
literal|1
expr_stmt|;
while|while
condition|(
name|token
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
name|prevToken
operator|=
name|token
expr_stmt|;
name|token
operator|=
name|token
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|prevToken
operator|.
name|startOffset
operator|==
name|token
operator|.
name|startOffset
condition|)
block|{
name|token
operator|.
name|positionIncrement
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|token
operator|.
name|positionIncrement
operator|=
literal|1
expr_stmt|;
block|}
block|}
block|}
name|prevTokenPos
operator|=
name|pos
expr_stmt|;
name|prevToken
operator|=
name|token
expr_stmt|;
block|}
block|}
DECL|method|initTokensArray
specifier|private
name|TokenLL
index|[]
name|initTokensArray
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Estimate the number of position slots we need. We use some estimation factors taken from Wikipedia
comment|//  that reduce the likelihood of needing to expand the array.
name|int
name|sumTotalTermFreq
init|=
operator|(
name|int
operator|)
name|vector
operator|.
name|getSumTotalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|sumTotalTermFreq
operator|==
operator|-
literal|1
condition|)
block|{
comment|//unfortunately term vectors seem to not have this stat
name|int
name|size
init|=
operator|(
name|int
operator|)
name|vector
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
comment|//doesn't happen with term vectors, it seems, but pick a default any way
name|size
operator|=
literal|128
expr_stmt|;
block|}
name|sumTotalTermFreq
operator|=
call|(
name|int
call|)
argument_list|(
name|size
operator|*
literal|2.4
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|originalPositionEstimate
init|=
call|(
name|int
call|)
argument_list|(
name|sumTotalTermFreq
operator|*
literal|1.5
argument_list|)
decl_stmt|;
comment|//less than 1 in 10 docs exceed this
return|return
operator|new
name|TokenLL
index|[
name|originalPositionEstimate
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|incrementToken
operator|==
literal|null
condition|)
block|{
name|incrementToken
operator|=
name|firstToken
expr_stmt|;
if|if
condition|(
name|incrementToken
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|incrementToken
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
name|incrementToken
operator|=
name|incrementToken
operator|.
name|next
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|copyBuffer
argument_list|(
name|incrementToken
operator|.
name|termChars
argument_list|,
literal|0
argument_list|,
name|incrementToken
operator|.
name|termCharsLen
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|incrementToken
operator|.
name|positionIncrement
argument_list|)
expr_stmt|;
if|if
condition|(
name|offsetAttribute
operator|!=
literal|null
condition|)
block|{
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|incrementToken
operator|.
name|startOffset
argument_list|,
name|incrementToken
operator|.
name|endOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payloadAttribute
operator|!=
literal|null
condition|)
block|{
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
name|incrementToken
operator|.
name|payload
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|class|TokenLL
specifier|private
specifier|static
class|class
name|TokenLL
block|{
DECL|field|termChars
name|char
index|[]
name|termChars
decl_stmt|;
DECL|field|termCharsLen
name|int
name|termCharsLen
decl_stmt|;
DECL|field|positionIncrement
name|int
name|positionIncrement
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|field|payload
name|BytesRef
name|payload
decl_stmt|;
DECL|field|next
name|TokenLL
name|next
decl_stmt|;
comment|/** Given the head of a linked-list (possibly null) this inserts the token at the correct      * spot to maintain the desired order, and returns the head (which could be this token if it's the smallest).      * O(N^2) complexity but N should be a handful at most.      */
DECL|method|insertIntoSortedLinkedList
name|TokenLL
name|insertIntoSortedLinkedList
parameter_list|(
specifier|final
name|TokenLL
name|head
parameter_list|)
block|{
assert|assert
name|next
operator|==
literal|null
assert|;
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|compareOffsets
argument_list|(
name|head
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|this
operator|.
name|next
operator|=
name|head
expr_stmt|;
return|return
name|this
return|;
block|}
name|TokenLL
name|prev
init|=
name|head
decl_stmt|;
while|while
condition|(
name|prev
operator|.
name|next
operator|!=
literal|null
operator|&&
name|this
operator|.
name|compareOffsets
argument_list|(
name|prev
operator|.
name|next
argument_list|)
operator|>
literal|0
condition|)
block|{
name|prev
operator|=
name|prev
operator|.
name|next
expr_stmt|;
block|}
name|this
operator|.
name|next
operator|=
name|prev
operator|.
name|next
expr_stmt|;
name|prev
operator|.
name|next
operator|=
name|this
expr_stmt|;
return|return
name|head
return|;
block|}
comment|/** by startOffset then endOffset */
DECL|method|compareOffsets
name|int
name|compareOffsets
parameter_list|(
name|TokenLL
name|tokenB
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Integer
operator|.
name|compare
argument_list|(
name|this
operator|.
name|startOffset
argument_list|,
name|tokenB
operator|.
name|startOffset
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|Integer
operator|.
name|compare
argument_list|(
name|this
operator|.
name|endOffset
argument_list|,
name|tokenB
operator|.
name|endOffset
argument_list|)
expr_stmt|;
block|}
return|return
name|cmp
return|;
block|}
block|}
block|}
end_class

end_unit
