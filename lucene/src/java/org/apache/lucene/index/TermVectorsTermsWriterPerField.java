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
name|document
operator|.
name|Fieldable
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
name|store
operator|.
name|IndexOutput
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

begin_class
DECL|class|TermVectorsTermsWriterPerField
specifier|final
class|class
name|TermVectorsTermsWriterPerField
extends|extends
name|TermsHashConsumerPerField
block|{
DECL|field|perThread
specifier|final
name|TermVectorsTermsWriterPerThread
name|perThread
decl_stmt|;
DECL|field|termsHashPerField
specifier|final
name|TermsHashPerField
name|termsHashPerField
decl_stmt|;
DECL|field|termsWriter
specifier|final
name|TermVectorsTermsWriter
name|termsWriter
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|doVectors
name|boolean
name|doVectors
decl_stmt|;
DECL|field|doVectorPositions
name|boolean
name|doVectorPositions
decl_stmt|;
DECL|field|doVectorOffsets
name|boolean
name|doVectorOffsets
decl_stmt|;
DECL|field|maxNumPostings
name|int
name|maxNumPostings
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
init|=
literal|null
decl_stmt|;
DECL|method|TermVectorsTermsWriterPerField
specifier|public
name|TermVectorsTermsWriterPerField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|TermVectorsTermsWriterPerThread
name|perThread
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|termsHashPerField
operator|=
name|termsHashPerField
expr_stmt|;
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
name|this
operator|.
name|termsWriter
operator|=
name|perThread
operator|.
name|termsWriter
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|termsHashPerField
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|termsHashPerField
operator|.
name|fieldState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStreamCount
name|int
name|getStreamCount
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
annotation|@
name|Override
DECL|method|start
name|boolean
name|start
parameter_list|(
name|Fieldable
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|doVectors
operator|=
literal|false
expr_stmt|;
name|doVectorPositions
operator|=
literal|false
expr_stmt|;
name|doVectorOffsets
operator|=
literal|false
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Fieldable
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isIndexed
argument_list|()
operator|&&
name|field
operator|.
name|isTermVectorStored
argument_list|()
condition|)
block|{
name|doVectors
operator|=
literal|true
expr_stmt|;
name|doVectorPositions
operator||=
name|field
operator|.
name|isStorePositionWithTermVector
argument_list|()
expr_stmt|;
name|doVectorOffsets
operator||=
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doVectors
condition|)
block|{
if|if
condition|(
name|perThread
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
name|perThread
operator|.
name|doc
operator|=
name|termsWriter
operator|.
name|getPerDoc
argument_list|()
expr_stmt|;
name|perThread
operator|.
name|doc
operator|.
name|docID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
assert|assert
name|perThread
operator|.
name|doc
operator|.
name|numVectorFields
operator|==
literal|0
assert|;
assert|assert
literal|0
operator|==
name|perThread
operator|.
name|doc
operator|.
name|perDocTvf
operator|.
name|length
argument_list|()
assert|;
assert|assert
literal|0
operator|==
name|perThread
operator|.
name|doc
operator|.
name|perDocTvf
operator|.
name|getFilePointer
argument_list|()
assert|;
block|}
else|else
block|{
assert|assert
name|perThread
operator|.
name|doc
operator|.
name|docID
operator|==
name|docState
operator|.
name|docID
assert|;
if|if
condition|(
name|termsHashPerField
operator|.
name|numPostings
operator|!=
literal|0
condition|)
comment|// Only necessary if previous doc hit a
comment|// non-aborting exception while writing vectors in
comment|// this field:
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO: only if needed for performance
comment|//perThread.postingsCount = 0;
return|return
name|doVectors
return|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{}
comment|/** Called once per field per document if term vectors    *  are enabled, to write the vectors to    *  RAMOutputStream, which is then quickly flushed to    *  * the real term vectors files in the Directory. */
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.finish start"
argument_list|)
assert|;
specifier|final
name|int
name|numPostings
init|=
name|termsHashPerField
operator|.
name|numPostings
decl_stmt|;
specifier|final
name|BytesRef
name|flushTerm
init|=
name|perThread
operator|.
name|flushTerm
decl_stmt|;
assert|assert
name|numPostings
operator|>=
literal|0
assert|;
if|if
condition|(
operator|!
name|doVectors
operator|||
name|numPostings
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|numPostings
operator|>
name|maxNumPostings
condition|)
name|maxNumPostings
operator|=
name|numPostings
expr_stmt|;
specifier|final
name|IndexOutput
name|tvf
init|=
name|perThread
operator|.
name|doc
operator|.
name|perDocTvf
decl_stmt|;
comment|// This is called once, after inverting all occurrences
comment|// of a given field in the doc.  At this point we flush
comment|// our hash into the DocWriter.
assert|assert
name|fieldInfo
operator|.
name|storeTermVector
assert|;
assert|assert
name|perThread
operator|.
name|vectorFieldsInOrder
argument_list|(
name|fieldInfo
argument_list|)
assert|;
name|perThread
operator|.
name|doc
operator|.
name|addField
argument_list|(
name|termsHashPerField
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|TermVectorsPostingsArray
name|postings
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
comment|// TODO: we may want to make this sort in same order
comment|// as Codec's terms dict?
specifier|final
name|int
index|[]
name|termIDs
init|=
name|termsHashPerField
operator|.
name|sortPostings
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|numPostings
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|doVectorPositions
condition|)
name|bits
operator||=
name|TermVectorsReader
operator|.
name|STORE_POSITIONS_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|doVectorOffsets
condition|)
name|bits
operator||=
name|TermVectorsReader
operator|.
name|STORE_OFFSET_WITH_TERMVECTOR
expr_stmt|;
name|tvf
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|int
name|lastLen
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|lastBytes
init|=
literal|null
decl_stmt|;
name|int
name|lastStart
init|=
literal|0
decl_stmt|;
specifier|final
name|ByteSliceReader
name|reader
init|=
name|perThread
operator|.
name|vectorSliceReader
decl_stmt|;
specifier|final
name|ByteBlockPool
name|termBytePool
init|=
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|termBytePool
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
name|numPostings
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|termID
init|=
name|termIDs
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
decl_stmt|;
comment|// Get BytesRef
name|termBytePool
operator|.
name|setBytesRef
argument_list|(
name|flushTerm
argument_list|,
name|postings
operator|.
name|textStarts
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
comment|// Compute common byte prefix between last term and
comment|// this term
name|int
name|prefix
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|j
operator|>
literal|0
condition|)
block|{
while|while
condition|(
name|prefix
operator|<
name|lastLen
operator|&&
name|prefix
operator|<
name|flushTerm
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|lastBytes
index|[
name|lastStart
operator|+
name|prefix
index|]
operator|!=
name|flushTerm
operator|.
name|bytes
index|[
name|flushTerm
operator|.
name|offset
operator|+
name|prefix
index|]
condition|)
block|{
break|break;
block|}
name|prefix
operator|++
expr_stmt|;
block|}
block|}
name|lastLen
operator|=
name|flushTerm
operator|.
name|length
expr_stmt|;
name|lastBytes
operator|=
name|flushTerm
operator|.
name|bytes
expr_stmt|;
name|lastStart
operator|=
name|flushTerm
operator|.
name|offset
expr_stmt|;
specifier|final
name|int
name|suffix
init|=
name|flushTerm
operator|.
name|length
operator|-
name|prefix
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeBytes
argument_list|(
name|flushTerm
operator|.
name|bytes
argument_list|,
name|lastStart
operator|+
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|reader
argument_list|,
name|termID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|writeTo
argument_list|(
name|tvf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|reader
argument_list|,
name|termID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|writeTo
argument_list|(
name|tvf
argument_list|)
expr_stmt|;
block|}
block|}
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|reset
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|shrinkHash
name|void
name|shrinkHash
parameter_list|()
block|{
name|termsHashPerField
operator|.
name|shrinkHash
argument_list|(
name|maxNumPostings
argument_list|)
expr_stmt|;
name|maxNumPostings
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
name|void
name|start
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|offsetAttribute
operator|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offsetAttribute
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newTerm
name|void
name|newTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.newTerm start"
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|startOffset
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|endOffset
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
name|endOffset
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addTerm
name|void
name|addTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.addTerm start"
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|startOffset
operator|-
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|endOffset
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
name|endOffset
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|fieldState
operator|.
name|position
operator|-
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|skippingLongTerm
name|void
name|skippingLongTerm
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|createPostingsArray
name|ParallelPostingsArray
name|createPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|class|TermVectorsPostingsArray
specifier|static
specifier|final
class|class
name|TermVectorsPostingsArray
extends|extends
name|ParallelPostingsArray
block|{
DECL|method|TermVectorsPostingsArray
specifier|public
name|TermVectorsPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|freqs
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastOffsets
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastPositions
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|field|freqs
name|int
index|[]
name|freqs
decl_stmt|;
comment|// How many times this term occurred in the current doc
DECL|field|lastOffsets
name|int
index|[]
name|lastOffsets
decl_stmt|;
comment|// Last offset we saw
DECL|field|lastPositions
name|int
index|[]
name|lastPositions
decl_stmt|;
comment|// Last position where this term occurred
DECL|method|newInstance
name|ParallelPostingsArray
name|newInstance
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
name|void
name|copyTo
parameter_list|(
name|ParallelPostingsArray
name|toArray
parameter_list|,
name|int
name|numToCopy
parameter_list|)
block|{
assert|assert
name|toArray
operator|instanceof
name|TermVectorsPostingsArray
assert|;
name|TermVectorsPostingsArray
name|to
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|toArray
decl_stmt|;
name|super
operator|.
name|copyTo
argument_list|(
name|toArray
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|freqs
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|freqs
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|super
operator|.
name|bytesPerPosting
argument_list|()
operator|+
literal|3
operator|*
name|DocumentsWriter
operator|.
name|INT_NUM_BYTE
return|;
block|}
block|}
block|}
end_class

end_unit

