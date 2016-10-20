begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|PostingsEnum
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
name|BytesRefArray
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
name|Counter
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
comment|/**  * TokenStream created from a term vector field. The term vector requires positions and/or offsets (either). If you  * want payloads add PayloadAttributeImpl (as you would normally) but don't assume the attribute is already added just  * because you know the term vector has payloads, since the first call to incrementToken() will observe if you asked  * for them and if not then won't get them.  This TokenStream supports an efficient {@link #reset()}, so there's  * no need to wrap with a caching impl.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|TokenStreamFromTermVector
specifier|final
class|class
name|TokenStreamFromTermVector
extends|extends
name|TokenStream
block|{
comment|// note: differs from similar class in the standard highlighter. This one is optimized for sparse cases.
comment|/**    * content length divided by distinct positions; an average of dense text.    */
DECL|field|AVG_CHARS_PER_POSITION
specifier|private
specifier|static
specifier|final
name|double
name|AVG_CHARS_PER_POSITION
init|=
literal|6
decl_stmt|;
DECL|field|INSERTION_SORT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|INSERTION_SORT_THRESHOLD
init|=
literal|16
decl_stmt|;
DECL|field|vector
specifier|private
specifier|final
name|Terms
name|vector
decl_stmt|;
DECL|field|filteredDocId
specifier|private
specifier|final
name|int
name|filteredDocId
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
DECL|field|offsetLength
specifier|private
specifier|final
name|int
name|offsetLength
decl_stmt|;
DECL|field|loadFactor
specifier|private
specifier|final
name|float
name|loadFactor
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
DECL|field|termCharsBuilder
specifier|private
name|CharsRefBuilder
name|termCharsBuilder
decl_stmt|;
comment|//term data here
DECL|field|payloadsBytesRefArray
specifier|private
name|BytesRefArray
name|payloadsBytesRefArray
decl_stmt|;
comment|//only used when payloadAttribute is non-null
DECL|field|spareBytesRefBuilder
specifier|private
name|BytesRefBuilder
name|spareBytesRefBuilder
decl_stmt|;
comment|//only used when payloadAttribute is non-null
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
DECL|field|initialized
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
comment|//lazy
DECL|method|TokenStreamFromTermVector
specifier|public
name|TokenStreamFromTermVector
parameter_list|(
name|Terms
name|vector
parameter_list|,
name|int
name|offsetLength
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|vector
argument_list|,
literal|0
argument_list|,
name|offsetLength
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    *    * @param vector        Terms that contains the data for    *                      creating the TokenStream. Must have positions and/or offsets.    * @param filteredDocId The docID we will process.    * @param offsetLength  Supply the character length of the text being uninverted, or a lower value if you don't want    *                      to invert text beyond an offset (in so doing this will act as a filter).  If you don't    *                      know the length, pass -1.  In conjunction with {@code loadFactor}, it's used to    *                      determine how many buckets to create during uninversion.    *                      It's also used to filter out tokens with a start offset exceeding this value.    * @param loadFactor    The percent of tokens from the original terms (by position count) that are    *                      expected to be inverted.  If they are filtered (e.g.    *                      {@link org.apache.lucene.index.FilterLeafReader.FilterTerms})    *                      then consider using less than 1.0 to avoid wasting space.    *                      1.0 means all, 1/64th would suggest 1/64th of all tokens coming from vector.    */
DECL|method|TokenStreamFromTermVector
name|TokenStreamFromTermVector
parameter_list|(
name|Terms
name|vector
parameter_list|,
name|int
name|filteredDocId
parameter_list|,
name|int
name|offsetLength
parameter_list|,
name|float
name|loadFactor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|filteredDocId
operator|=
name|filteredDocId
expr_stmt|;
name|this
operator|.
name|offsetLength
operator|=
name|offsetLength
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|?
operator|-
literal|1
else|:
name|offsetLength
expr_stmt|;
if|if
condition|(
name|loadFactor
operator|<=
literal|0f
operator|||
name|loadFactor
operator|>
literal|1f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"loadFactor should be> 0 and<= 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|loadFactor
operator|=
name|loadFactor
expr_stmt|;
assert|assert
operator|!
name|hasAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
operator|:
literal|"AttributeFactory shouldn't have payloads *yet*"
assert|;
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
comment|//We delay initialization because we can see which attributes the consumer wants, particularly payloads
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|initialized
assert|;
name|int
name|dpEnumFlags
init|=
literal|0
decl_stmt|;
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
name|dpEnumFlags
operator||=
name|PostingsEnum
operator|.
name|OFFSETS
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
name|payloadsBytesRefArray
operator|=
operator|new
name|BytesRefArray
argument_list|(
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
expr_stmt|;
name|spareBytesRefBuilder
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
name|dpEnumFlags
operator||=
name|PostingsEnum
operator|.
name|PAYLOADS
expr_stmt|;
block|}
comment|// We put term data here
name|termCharsBuilder
operator|=
operator|new
name|CharsRefBuilder
argument_list|()
expr_stmt|;
name|termCharsBuilder
operator|.
name|grow
argument_list|(
name|initTotalTermCharLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// Step 1: iterate termsEnum and create a token, placing into a bucketed array (given a load factor)
specifier|final
name|TokenLL
index|[]
name|tokenBuckets
init|=
name|initTokenBucketsArray
argument_list|()
decl_stmt|;
specifier|final
name|double
name|OFFSET_TO_BUCKET_IDX
init|=
name|loadFactor
operator|/
name|AVG_CHARS_PER_POSITION
decl_stmt|;
specifier|final
name|double
name|POSITION_TO_BUCKET_IDX
init|=
name|loadFactor
decl_stmt|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|termBytesRef
decl_stmt|;
name|PostingsEnum
name|dpEnum
init|=
literal|null
decl_stmt|;
specifier|final
name|CharsRefBuilder
name|tempCharsRefBuilder
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
comment|//only for UTF8->UTF16 call
name|TERM_LOOP
label|:
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
name|tempCharsRefBuilder
operator|.
name|grow
argument_list|(
name|termBytesRef
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|tempCharsRefBuilder
operator|.
name|chars
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|termCharsOff
init|=
name|termCharsBuilder
operator|.
name|length
argument_list|()
decl_stmt|;
name|termCharsBuilder
operator|.
name|append
argument_list|(
name|tempCharsRefBuilder
operator|.
name|chars
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termCharsLen
argument_list|)
expr_stmt|;
name|dpEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|dpEnum
argument_list|,
name|dpEnumFlags
argument_list|)
expr_stmt|;
assert|assert
name|dpEnum
operator|!=
literal|null
assert|;
comment|// presumably checked by TokenSources.hasPositions earlier
name|int
name|currentDocId
init|=
name|dpEnum
operator|.
name|advance
argument_list|(
name|filteredDocId
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentDocId
operator|!=
name|filteredDocId
condition|)
block|{
continue|continue;
comment|//Not expected
block|}
specifier|final
name|int
name|freq
init|=
name|dpEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
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
name|TokenLL
name|token
init|=
operator|new
name|TokenLL
argument_list|()
decl_stmt|;
name|token
operator|.
name|position
operator|=
name|dpEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
comment|// can be -1 if not in the TV
name|token
operator|.
name|termCharsOff
operator|=
name|termCharsOff
expr_stmt|;
name|token
operator|.
name|termCharsLen
operator|=
operator|(
name|short
operator|)
name|Math
operator|.
name|min
argument_list|(
name|termCharsLen
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
comment|// copy offset (if it's there) and compute bucketIdx
name|int
name|bucketIdx
decl_stmt|;
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
if|if
condition|(
name|offsetLength
operator|>=
literal|0
operator|&&
name|token
operator|.
name|startOffset
operator|>
name|offsetLength
condition|)
block|{
continue|continue
name|TERM_LOOP
continue|;
comment|//filter this token out; exceeds threshold
block|}
name|token
operator|.
name|endOffsetInc
operator|=
operator|(
name|short
operator|)
name|Math
operator|.
name|min
argument_list|(
name|dpEnum
operator|.
name|endOffset
argument_list|()
operator|-
name|token
operator|.
name|startOffset
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|bucketIdx
operator|=
call|(
name|int
call|)
argument_list|(
name|token
operator|.
name|startOffset
operator|*
name|OFFSET_TO_BUCKET_IDX
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bucketIdx
operator|=
call|(
name|int
call|)
argument_list|(
name|token
operator|.
name|position
operator|*
name|POSITION_TO_BUCKET_IDX
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bucketIdx
operator|>=
name|tokenBuckets
operator|.
name|length
condition|)
block|{
name|bucketIdx
operator|=
name|tokenBuckets
operator|.
name|length
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|payloadAttribute
operator|!=
literal|null
condition|)
block|{
specifier|final
name|BytesRef
name|payload
init|=
name|dpEnum
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|token
operator|.
name|payloadIndex
operator|=
name|payload
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|payloadsBytesRefArray
operator|.
name|append
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
comment|//Add token to the head of the bucket linked list
name|token
operator|.
name|next
operator|=
name|tokenBuckets
index|[
name|bucketIdx
index|]
expr_stmt|;
name|tokenBuckets
index|[
name|bucketIdx
index|]
operator|=
name|token
expr_stmt|;
block|}
block|}
comment|// Step 2:  Link all Tokens into a linked-list and sort all tokens at the same position
name|firstToken
operator|=
name|initLinkAndSortTokens
argument_list|(
name|tokenBuckets
argument_list|)
expr_stmt|;
comment|// If the term vector didn't have positions, synthesize them
if|if
condition|(
operator|!
name|vector
operator|.
name|hasPositions
argument_list|()
operator|&&
name|firstToken
operator|!=
literal|null
condition|)
block|{
name|TokenLL
name|prevToken
init|=
name|firstToken
decl_stmt|;
name|prevToken
operator|.
name|position
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|TokenLL
name|token
init|=
name|prevToken
operator|.
name|next
init|;
name|token
operator|!=
literal|null
condition|;
name|prevToken
operator|=
name|token
operator|,
name|token
operator|=
name|token
operator|.
name|next
control|)
block|{
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
name|position
operator|=
name|prevToken
operator|.
name|position
expr_stmt|;
block|}
else|else
block|{
name|token
operator|.
name|position
operator|=
name|prevToken
operator|.
name|position
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|initLinkAndSortTokens
specifier|private
specifier|static
name|TokenLL
name|initLinkAndSortTokens
parameter_list|(
name|TokenLL
index|[]
name|tokenBuckets
parameter_list|)
block|{
name|TokenLL
name|firstToken
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|TokenLL
argument_list|>
name|scratchTokenArray
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// declare here for re-use.  TODO use native array
name|TokenLL
name|prevToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|TokenLL
name|tokenHead
range|:
name|tokenBuckets
control|)
block|{
if|if
condition|(
name|tokenHead
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|//sort tokens at this position and link them; return the first
name|TokenLL
name|tokenTail
decl_stmt|;
comment|// just one token
if|if
condition|(
name|tokenHead
operator|.
name|next
operator|==
literal|null
condition|)
block|{
name|tokenTail
operator|=
name|tokenHead
expr_stmt|;
block|}
else|else
block|{
comment|// add the linked list to a temporary array
for|for
control|(
name|TokenLL
name|cur
init|=
name|tokenHead
init|;
name|cur
operator|!=
literal|null
condition|;
name|cur
operator|=
name|cur
operator|.
name|next
control|)
block|{
name|scratchTokenArray
operator|.
name|add
argument_list|(
name|cur
argument_list|)
expr_stmt|;
block|}
comment|// sort; and set tokenHead& tokenTail
if|if
condition|(
name|scratchTokenArray
operator|.
name|size
argument_list|()
operator|<
name|INSERTION_SORT_THRESHOLD
condition|)
block|{
comment|// insertion sort by creating a linked list (leave scratchTokenArray alone)
name|tokenHead
operator|=
name|tokenTail
operator|=
name|scratchTokenArray
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|tokenHead
operator|.
name|next
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|scratchTokenArray
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TokenLL
name|insertToken
init|=
name|scratchTokenArray
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|insertToken
operator|.
name|compareTo
argument_list|(
name|tokenHead
argument_list|)
operator|<=
literal|0
condition|)
block|{
comment|// takes the place of tokenHead
name|insertToken
operator|.
name|next
operator|=
name|tokenHead
expr_stmt|;
name|tokenHead
operator|=
name|insertToken
expr_stmt|;
block|}
else|else
block|{
comment|// goes somewhere after tokenHead
for|for
control|(
name|TokenLL
name|prev
init|=
name|tokenHead
init|;
literal|true
condition|;
name|prev
operator|=
name|prev
operator|.
name|next
control|)
block|{
if|if
condition|(
name|prev
operator|.
name|next
operator|==
literal|null
operator|||
name|insertToken
operator|.
name|compareTo
argument_list|(
name|prev
operator|.
name|next
argument_list|)
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|prev
operator|.
name|next
operator|==
literal|null
condition|)
block|{
name|tokenTail
operator|=
name|insertToken
expr_stmt|;
block|}
name|insertToken
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
name|insertToken
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|scratchTokenArray
argument_list|)
expr_stmt|;
comment|// take back out and create a linked list
name|TokenLL
name|prev
init|=
name|tokenHead
operator|=
name|scratchTokenArray
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|scratchTokenArray
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|prev
operator|.
name|next
operator|=
name|scratchTokenArray
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|prev
operator|=
name|prev
operator|.
name|next
expr_stmt|;
block|}
name|tokenTail
operator|=
name|prev
expr_stmt|;
name|tokenTail
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
name|scratchTokenArray
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//too bad ArrayList nulls it out; we don't actually need that
block|}
comment|//link to previous
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
name|tokenHead
expr_stmt|;
comment|//concatenate linked-list
assert|assert
name|prevToken
operator|.
name|compareTo
argument_list|(
name|tokenHead
argument_list|)
operator|<
literal|0
operator|:
literal|"wrong offset / position ordering expectations"
assert|;
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
name|tokenHead
expr_stmt|;
block|}
name|prevToken
operator|=
name|tokenTail
expr_stmt|;
block|}
return|return
name|firstToken
return|;
block|}
DECL|method|initTotalTermCharLen
specifier|private
name|int
name|initTotalTermCharLen
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|guessNumTerms
decl_stmt|;
if|if
condition|(
name|vector
operator|.
name|size
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|guessNumTerms
operator|=
operator|(
name|int
operator|)
name|vector
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|offsetLength
operator|!=
operator|-
literal|1
condition|)
block|{
name|guessNumTerms
operator|=
call|(
name|int
call|)
argument_list|(
name|offsetLength
operator|*
literal|0.33
argument_list|)
expr_stmt|;
comment|//guess 1/3rd
block|}
else|else
block|{
return|return
literal|128
return|;
block|}
return|return
name|Math
operator|.
name|max
argument_list|(
literal|64
argument_list|,
call|(
name|int
call|)
argument_list|(
name|guessNumTerms
operator|*
name|loadFactor
operator|*
literal|7.0
argument_list|)
argument_list|)
return|;
comment|//7 is over-estimate of average term len
block|}
DECL|method|initTokenBucketsArray
specifier|private
name|TokenLL
index|[]
name|initTokenBucketsArray
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Estimate the number of non-empty positions (number of tokens, excluding same-position synonyms).
name|int
name|positionsEstimate
decl_stmt|;
if|if
condition|(
name|offsetLength
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no clue what the char length is.
comment|// Estimate the number of position slots we need from term stats based on Wikipedia.
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
name|positionsEstimate
operator|=
call|(
name|int
call|)
argument_list|(
name|sumTotalTermFreq
operator|*
literal|1.5
argument_list|)
expr_stmt|;
comment|//less than 1 in 10 docs exceed this
block|}
else|else
block|{
comment|// guess number of token positions by this factor.
name|positionsEstimate
operator|=
call|(
name|int
call|)
argument_list|(
name|offsetLength
operator|/
name|AVG_CHARS_PER_POSITION
argument_list|)
expr_stmt|;
block|}
comment|// apply the load factor.
return|return
operator|new
name|TokenLL
index|[
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
call|(
name|int
call|)
argument_list|(
name|positionsEstimate
operator|*
name|loadFactor
argument_list|)
argument_list|)
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
throws|throws
name|IOException
block|{
name|int
name|posInc
decl_stmt|;
if|if
condition|(
name|incrementToken
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|init
argument_list|()
expr_stmt|;
assert|assert
name|initialized
assert|;
block|}
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
name|posInc
operator|=
name|incrementToken
operator|.
name|position
operator|+
literal|1
expr_stmt|;
comment|//first token normally has pos 0; add 1 to get posInc
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
name|int
name|lastPosition
init|=
name|incrementToken
operator|.
name|position
decl_stmt|;
name|incrementToken
operator|=
name|incrementToken
operator|.
name|next
expr_stmt|;
name|posInc
operator|=
name|incrementToken
operator|.
name|position
operator|-
name|lastPosition
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
name|termCharsBuilder
operator|.
name|chars
argument_list|()
argument_list|,
name|incrementToken
operator|.
name|termCharsOff
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
name|posInc
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
name|startOffset
operator|+
name|incrementToken
operator|.
name|endOffsetInc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payloadAttribute
operator|!=
literal|null
operator|&&
name|incrementToken
operator|.
name|payloadIndex
operator|>=
literal|0
condition|)
block|{
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
name|payloadsBytesRefArray
operator|.
name|get
argument_list|(
name|spareBytesRefBuilder
argument_list|,
name|incrementToken
operator|.
name|payloadIndex
argument_list|)
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
implements|implements
name|Comparable
argument_list|<
name|TokenLL
argument_list|>
block|{
comment|// This class should weigh 32 bytes, including object header
DECL|field|termCharsOff
name|int
name|termCharsOff
decl_stmt|;
comment|// see termCharsBuilder
DECL|field|termCharsLen
name|short
name|termCharsLen
decl_stmt|;
DECL|field|position
name|int
name|position
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffsetInc
name|short
name|endOffsetInc
decl_stmt|;
comment|// add to startOffset to get endOffset
DECL|field|payloadIndex
name|int
name|payloadIndex
decl_stmt|;
DECL|field|next
name|TokenLL
name|next
decl_stmt|;
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
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
name|position
argument_list|,
name|tokenB
operator|.
name|position
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
name|startOffset
argument_list|,
name|tokenB
operator|.
name|startOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
name|cmp
operator|=
name|Short
operator|.
name|compare
argument_list|(
name|this
operator|.
name|endOffsetInc
argument_list|,
name|tokenB
operator|.
name|endOffsetInc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|cmp
return|;
block|}
block|}
block|}
end_class

end_unit
