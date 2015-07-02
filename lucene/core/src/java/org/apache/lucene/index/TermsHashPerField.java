begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|ByteBlockPool
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
name|IntBlockPool
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
operator|.
name|BytesStartArray
import|;
end_import

begin_class
DECL|class|TermsHashPerField
specifier|abstract
class|class
name|TermsHashPerField
implements|implements
name|Comparable
argument_list|<
name|TermsHashPerField
argument_list|>
block|{
DECL|field|HASH_INIT_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|HASH_INIT_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|termsHash
specifier|final
name|TermsHash
name|termsHash
decl_stmt|;
DECL|field|nextPerField
specifier|final
name|TermsHashPerField
name|nextPerField
decl_stmt|;
DECL|field|docState
specifier|protected
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|protected
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|termAtt
name|TermToBytesRefAttribute
name|termAtt
decl_stmt|;
comment|// Copied from our perThread
DECL|field|intPool
specifier|final
name|IntBlockPool
name|intPool
decl_stmt|;
DECL|field|bytePool
specifier|final
name|ByteBlockPool
name|bytePool
decl_stmt|;
DECL|field|termBytePool
specifier|final
name|ByteBlockPool
name|termBytePool
decl_stmt|;
DECL|field|streamCount
specifier|final
name|int
name|streamCount
decl_stmt|;
DECL|field|numPostingInt
specifier|final
name|int
name|numPostingInt
decl_stmt|;
DECL|field|fieldInfo
specifier|protected
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|bytesHash
specifier|final
name|BytesRefHash
name|bytesHash
decl_stmt|;
DECL|field|postingsArray
name|ParallelPostingsArray
name|postingsArray
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
comment|/** streamCount: how many streams this field stores per term.    * E.g. doc(+freq) is 1 stream, prox+offset is a second. */
DECL|method|TermsHashPerField
specifier|public
name|TermsHashPerField
parameter_list|(
name|int
name|streamCount
parameter_list|,
name|FieldInvertState
name|fieldState
parameter_list|,
name|TermsHash
name|termsHash
parameter_list|,
name|TermsHashPerField
name|nextPerField
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|intPool
operator|=
name|termsHash
operator|.
name|intPool
expr_stmt|;
name|bytePool
operator|=
name|termsHash
operator|.
name|bytePool
expr_stmt|;
name|termBytePool
operator|=
name|termsHash
operator|.
name|termBytePool
expr_stmt|;
name|docState
operator|=
name|termsHash
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|termsHash
operator|=
name|termsHash
expr_stmt|;
name|bytesUsed
operator|=
name|termsHash
operator|.
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|fieldState
operator|=
name|fieldState
expr_stmt|;
name|this
operator|.
name|streamCount
operator|=
name|streamCount
expr_stmt|;
name|numPostingInt
operator|=
literal|2
operator|*
name|streamCount
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|nextPerField
operator|=
name|nextPerField
expr_stmt|;
name|PostingsBytesStartArray
name|byteStarts
init|=
operator|new
name|PostingsBytesStartArray
argument_list|(
name|this
argument_list|,
name|bytesUsed
argument_list|)
decl_stmt|;
name|bytesHash
operator|=
operator|new
name|BytesRefHash
argument_list|(
name|termBytePool
argument_list|,
name|HASH_INIT_SIZE
argument_list|,
name|byteStarts
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|bytesHash
operator|.
name|clear
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
block|{
name|nextPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initReader
specifier|public
name|void
name|initReader
parameter_list|(
name|ByteSliceReader
name|reader
parameter_list|,
name|int
name|termID
parameter_list|,
name|int
name|stream
parameter_list|)
block|{
assert|assert
name|stream
operator|<
name|streamCount
assert|;
name|int
name|intStart
init|=
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|ints
init|=
name|intPool
operator|.
name|buffers
index|[
name|intStart
operator|>>
name|IntBlockPool
operator|.
name|INT_BLOCK_SHIFT
index|]
decl_stmt|;
specifier|final
name|int
name|upto
init|=
name|intStart
operator|&
name|IntBlockPool
operator|.
name|INT_BLOCK_MASK
decl_stmt|;
name|reader
operator|.
name|init
argument_list|(
name|bytePool
argument_list|,
name|postingsArray
operator|.
name|byteStarts
index|[
name|termID
index|]
operator|+
name|stream
operator|*
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|,
name|ints
index|[
name|upto
operator|+
name|stream
index|]
argument_list|)
expr_stmt|;
block|}
DECL|field|sortedTermIDs
name|int
index|[]
name|sortedTermIDs
decl_stmt|;
comment|/** Collapse the hash table and sort in-place; also sets    * this.sortedTermIDs to the results */
DECL|method|sortPostings
specifier|public
name|int
index|[]
name|sortPostings
parameter_list|()
block|{
name|sortedTermIDs
operator|=
name|bytesHash
operator|.
name|sort
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sortedTermIDs
return|;
block|}
DECL|field|doNextCall
specifier|private
name|boolean
name|doNextCall
decl_stmt|;
comment|// Secondary entry point (for 2nd& subsequent TermsHash),
comment|// because token text has already been "interned" into
comment|// textStart, so we hash by textStart.  term vectors use
comment|// this API.
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|textStart
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|termID
init|=
name|bytesHash
operator|.
name|addByPoolOffset
argument_list|(
name|textStart
argument_list|)
decl_stmt|;
if|if
condition|(
name|termID
operator|>=
literal|0
condition|)
block|{
comment|// New posting
comment|// First time we are seeing this token since we last
comment|// flushed the hash.
comment|// Init stream slices
if|if
condition|(
name|numPostingInt
operator|+
name|intPool
operator|.
name|intUpto
operator|>
name|IntBlockPool
operator|.
name|INT_BLOCK_SIZE
condition|)
block|{
name|intPool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
operator|-
name|bytePool
operator|.
name|byteUpto
operator|<
name|numPostingInt
operator|*
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
condition|)
block|{
name|bytePool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
name|intUptos
operator|=
name|intPool
operator|.
name|buffer
expr_stmt|;
name|intUptoStart
operator|=
name|intPool
operator|.
name|intUpto
expr_stmt|;
name|intPool
operator|.
name|intUpto
operator|+=
name|streamCount
expr_stmt|;
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
operator|=
name|intUptoStart
operator|+
name|intPool
operator|.
name|intOffset
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
name|streamCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|upto
init|=
name|bytePool
operator|.
name|newSlice
argument_list|(
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|)
decl_stmt|;
name|intUptos
index|[
name|intUptoStart
operator|+
name|i
index|]
operator|=
name|upto
operator|+
name|bytePool
operator|.
name|byteOffset
expr_stmt|;
block|}
name|postingsArray
operator|.
name|byteStarts
index|[
name|termID
index|]
operator|=
name|intUptos
index|[
name|intUptoStart
index|]
expr_stmt|;
name|newTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termID
operator|=
operator|(
operator|-
name|termID
operator|)
operator|-
literal|1
expr_stmt|;
name|int
name|intStart
init|=
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
decl_stmt|;
name|intUptos
operator|=
name|intPool
operator|.
name|buffers
index|[
name|intStart
operator|>>
name|IntBlockPool
operator|.
name|INT_BLOCK_SHIFT
index|]
expr_stmt|;
name|intUptoStart
operator|=
name|intStart
operator|&
name|IntBlockPool
operator|.
name|INT_BLOCK_MASK
expr_stmt|;
name|addTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Called once per inverted token.  This is the primary    *  entry point (for first TermsHash); postings use this    *  API. */
DECL|method|add
name|void
name|add
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We are first in the chain so we must "intern" the
comment|// term text into textStart address
comment|// Get the text& hash of this term.
name|int
name|termID
init|=
name|bytesHash
operator|.
name|add
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("add term=" + termBytesRef.utf8ToString() + " doc=" + docState.docID + " termID=" + termID);
if|if
condition|(
name|termID
operator|>=
literal|0
condition|)
block|{
comment|// New posting
name|bytesHash
operator|.
name|byteStart
argument_list|(
name|termID
argument_list|)
expr_stmt|;
comment|// Init stream slices
if|if
condition|(
name|numPostingInt
operator|+
name|intPool
operator|.
name|intUpto
operator|>
name|IntBlockPool
operator|.
name|INT_BLOCK_SIZE
condition|)
block|{
name|intPool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
operator|-
name|bytePool
operator|.
name|byteUpto
operator|<
name|numPostingInt
operator|*
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
condition|)
block|{
name|bytePool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
name|intUptos
operator|=
name|intPool
operator|.
name|buffer
expr_stmt|;
name|intUptoStart
operator|=
name|intPool
operator|.
name|intUpto
expr_stmt|;
name|intPool
operator|.
name|intUpto
operator|+=
name|streamCount
expr_stmt|;
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
operator|=
name|intUptoStart
operator|+
name|intPool
operator|.
name|intOffset
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
name|streamCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|upto
init|=
name|bytePool
operator|.
name|newSlice
argument_list|(
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|)
decl_stmt|;
name|intUptos
index|[
name|intUptoStart
operator|+
name|i
index|]
operator|=
name|upto
operator|+
name|bytePool
operator|.
name|byteOffset
expr_stmt|;
block|}
name|postingsArray
operator|.
name|byteStarts
index|[
name|termID
index|]
operator|=
name|intUptos
index|[
name|intUptoStart
index|]
expr_stmt|;
name|newTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termID
operator|=
operator|(
operator|-
name|termID
operator|)
operator|-
literal|1
expr_stmt|;
name|int
name|intStart
init|=
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
decl_stmt|;
name|intUptos
operator|=
name|intPool
operator|.
name|buffers
index|[
name|intStart
operator|>>
name|IntBlockPool
operator|.
name|INT_BLOCK_SHIFT
index|]
expr_stmt|;
name|intUptoStart
operator|=
name|intStart
operator|&
name|IntBlockPool
operator|.
name|INT_BLOCK_MASK
expr_stmt|;
name|addTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doNextCall
condition|)
block|{
name|nextPerField
operator|.
name|add
argument_list|(
name|postingsArray
operator|.
name|textStarts
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|intUptos
name|int
index|[]
name|intUptos
decl_stmt|;
DECL|field|intUptoStart
name|int
name|intUptoStart
decl_stmt|;
DECL|method|writeByte
name|void
name|writeByte
parameter_list|(
name|int
name|stream
parameter_list|,
name|byte
name|b
parameter_list|)
block|{
name|int
name|upto
init|=
name|intUptos
index|[
name|intUptoStart
operator|+
name|stream
index|]
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bytePool
operator|.
name|buffers
index|[
name|upto
operator|>>
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SHIFT
index|]
decl_stmt|;
assert|assert
name|bytes
operator|!=
literal|null
assert|;
name|int
name|offset
init|=
name|upto
operator|&
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_MASK
decl_stmt|;
if|if
condition|(
name|bytes
index|[
name|offset
index|]
operator|!=
literal|0
condition|)
block|{
comment|// End of slice; allocate a new one
name|offset
operator|=
name|bytePool
operator|.
name|allocSlice
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|bytes
operator|=
name|bytePool
operator|.
name|buffer
expr_stmt|;
name|intUptos
index|[
name|intUptoStart
operator|+
name|stream
index|]
operator|=
name|offset
operator|+
name|bytePool
operator|.
name|byteOffset
expr_stmt|;
block|}
name|bytes
index|[
name|offset
index|]
operator|=
name|b
expr_stmt|;
operator|(
name|intUptos
index|[
name|intUptoStart
operator|+
name|stream
index|]
operator|)
operator|++
expr_stmt|;
block|}
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|int
name|stream
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// TODO: optimize
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|len
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|writeByte
argument_list|(
name|stream
argument_list|,
name|b
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|writeVInt
name|void
name|writeVInt
parameter_list|(
name|int
name|stream
parameter_list|,
name|int
name|i
parameter_list|)
block|{
assert|assert
name|stream
operator|<
name|streamCount
assert|;
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7F
operator|)
operator|!=
literal|0
condition|)
block|{
name|writeByte
argument_list|(
name|stream
argument_list|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7f
operator|)
operator||
literal|0x80
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|writeByte
argument_list|(
name|stream
argument_list|,
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
DECL|class|PostingsBytesStartArray
specifier|private
specifier|static
specifier|final
class|class
name|PostingsBytesStartArray
extends|extends
name|BytesStartArray
block|{
DECL|field|perField
specifier|private
specifier|final
name|TermsHashPerField
name|perField
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|method|PostingsBytesStartArray
specifier|private
name|PostingsBytesStartArray
parameter_list|(
name|TermsHashPerField
name|perField
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|perField
operator|=
name|perField
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|int
index|[]
name|init
parameter_list|()
block|{
if|if
condition|(
name|perField
operator|.
name|postingsArray
operator|==
literal|null
condition|)
block|{
name|perField
operator|.
name|postingsArray
operator|=
name|perField
operator|.
name|createPostingsArray
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|perField
operator|.
name|newPostingsArray
argument_list|()
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|perField
operator|.
name|postingsArray
operator|.
name|size
operator|*
name|perField
operator|.
name|postingsArray
operator|.
name|bytesPerPosting
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|perField
operator|.
name|postingsArray
operator|.
name|textStarts
return|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|int
index|[]
name|grow
parameter_list|()
block|{
name|ParallelPostingsArray
name|postingsArray
init|=
name|perField
operator|.
name|postingsArray
decl_stmt|;
specifier|final
name|int
name|oldSize
init|=
name|perField
operator|.
name|postingsArray
operator|.
name|size
decl_stmt|;
name|postingsArray
operator|=
name|perField
operator|.
name|postingsArray
operator|=
name|postingsArray
operator|.
name|grow
argument_list|()
expr_stmt|;
name|perField
operator|.
name|newPostingsArray
argument_list|()
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|postingsArray
operator|.
name|bytesPerPosting
argument_list|()
operator|*
operator|(
name|postingsArray
operator|.
name|size
operator|-
name|oldSize
operator|)
operator|)
argument_list|)
expr_stmt|;
return|return
name|postingsArray
operator|.
name|textStarts
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|int
index|[]
name|clear
parameter_list|()
block|{
if|if
condition|(
name|perField
operator|.
name|postingsArray
operator|!=
literal|null
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|perField
operator|.
name|postingsArray
operator|.
name|size
operator|*
name|perField
operator|.
name|postingsArray
operator|.
name|bytesPerPosting
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|perField
operator|.
name|postingsArray
operator|=
literal|null
expr_stmt|;
name|perField
operator|.
name|newPostingsArray
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|bytesUsed
specifier|public
name|Counter
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TermsHashPerField
name|other
parameter_list|)
block|{
return|return
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
comment|/** Finish adding all instances of this field to the    *  current document. */
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
block|{
name|nextPerField
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Start adding a new field instance; first is true if    *  this is the first time this field name was seen in the    *  document. */
DECL|method|start
name|boolean
name|start
parameter_list|(
name|IndexableField
name|field
parameter_list|,
name|boolean
name|first
parameter_list|)
block|{
name|termAtt
operator|=
name|fieldState
operator|.
name|termAttribute
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
block|{
name|doNextCall
operator|=
name|nextPerField
operator|.
name|start
argument_list|(
name|field
argument_list|,
name|first
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Called when a term is seen for the first time. */
DECL|method|newTerm
specifier|abstract
name|void
name|newTerm
parameter_list|(
name|int
name|termID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when a previously seen term is seen again. */
DECL|method|addTerm
specifier|abstract
name|void
name|addTerm
parameter_list|(
name|int
name|termID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when the postings array is initialized or    *  resized. */
DECL|method|newPostingsArray
specifier|abstract
name|void
name|newPostingsArray
parameter_list|()
function_decl|;
comment|/** Creates a new postings array of the specified size. */
DECL|method|createPostingsArray
specifier|abstract
name|ParallelPostingsArray
name|createPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
block|}
end_class

end_unit

