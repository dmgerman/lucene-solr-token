begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
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
name|Arrays
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|search
operator|.
name|DocIdSetIterator
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
name|BitSetIterator
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
operator|.
name|DirectBytesStartArray
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
name|FixedBitSet
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
name|packed
operator|.
name|PackedInts
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
name|packed
operator|.
name|PackedLongValues
import|;
end_import

begin_comment
comment|/** Buffers up pending byte[]s per doc, deref and sorting via  *  int ord, then flushes when segment flushes. */
end_comment

begin_class
DECL|class|SortedSetDocValuesWriter
class|class
name|SortedSetDocValuesWriter
extends|extends
name|DocValuesWriter
block|{
DECL|field|hash
specifier|final
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|pending
specifier|private
name|PackedLongValues
operator|.
name|Builder
name|pending
decl_stmt|;
comment|// stream of all termIDs
DECL|field|pendingCounts
specifier|private
name|PackedLongValues
operator|.
name|Builder
name|pendingCounts
decl_stmt|;
comment|// termIDs per doc
DECL|field|docsWithField
specifier|private
name|FixedBitSet
name|docsWithField
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
comment|// this only tracks differences in 'pending' and 'pendingCounts'
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|currentDoc
specifier|private
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentValues
specifier|private
name|int
name|currentValues
index|[]
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
DECL|field|currentUpto
specifier|private
name|int
name|currentUpto
decl_stmt|;
DECL|field|maxCount
specifier|private
name|int
name|maxCount
decl_stmt|;
DECL|method|SortedSetDocValuesWriter
specifier|public
name|SortedSetDocValuesWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|iwBytesUsed
operator|=
name|iwBytesUsed
expr_stmt|;
name|hash
operator|=
operator|new
name|BytesRefHash
argument_list|(
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
argument_list|(
name|iwBytesUsed
argument_list|)
argument_list|)
argument_list|,
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
operator|new
name|DirectBytesStartArray
argument_list|(
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
name|iwBytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|pending
operator|=
name|PackedLongValues
operator|.
name|packedBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|pendingCounts
operator|=
name|PackedLongValues
operator|.
name|deltaPackedBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|docsWithField
operator|=
operator|new
name|FixedBitSet
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|pendingCounts
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
name|currentDoc
assert|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\": null value not allowed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|.
name|length
operator|>
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" is too large, must be<= "
operator|+
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|docID
operator|!=
name|currentDoc
condition|)
block|{
name|finishCurrentDoc
argument_list|()
expr_stmt|;
name|currentDoc
operator|=
name|docID
expr_stmt|;
block|}
name|addOneValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|updateBytesUsed
argument_list|()
expr_stmt|;
block|}
comment|// finalize currentDoc: this deduplicates the current term ids
DECL|method|finishCurrentDoc
specifier|private
name|void
name|finishCurrentDoc
parameter_list|()
block|{
if|if
condition|(
name|currentDoc
operator|==
operator|-
literal|1
condition|)
block|{
return|return;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|currentValues
argument_list|,
literal|0
argument_list|,
name|currentUpto
argument_list|)
expr_stmt|;
name|int
name|lastValue
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|count
init|=
literal|0
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
name|currentUpto
condition|;
name|i
operator|++
control|)
block|{
name|int
name|termID
init|=
name|currentValues
index|[
name|i
index|]
decl_stmt|;
comment|// if it's not a duplicate
if|if
condition|(
name|termID
operator|!=
name|lastValue
condition|)
block|{
name|pending
operator|.
name|add
argument_list|(
name|termID
argument_list|)
expr_stmt|;
comment|// record the term id
name|count
operator|++
expr_stmt|;
block|}
name|lastValue
operator|=
name|termID
expr_stmt|;
block|}
comment|// record the number of unique term ids for this doc
name|pendingCounts
operator|.
name|add
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|maxCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|currentUpto
operator|=
literal|0
expr_stmt|;
name|docsWithField
operator|=
name|FixedBitSet
operator|.
name|ensureCapacity
argument_list|(
name|docsWithField
argument_list|,
name|currentDoc
argument_list|)
expr_stmt|;
name|docsWithField
operator|.
name|set
argument_list|(
name|currentDoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|finishCurrentDoc
argument_list|()
expr_stmt|;
block|}
DECL|method|addOneValue
specifier|private
name|void
name|addOneValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|int
name|termID
init|=
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|termID
operator|<
literal|0
condition|)
block|{
name|termID
operator|=
operator|-
name|termID
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// reserve additional space for each unique value:
comment|// 1. when indexing, when hash is 50% full, rehash() suddenly needs 2*size ints.
comment|//    TODO: can this same OOM happen in THPF?
comment|// 2. when flushing, we need 1 int per value (slot in the ordMap).
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentUpto
operator|==
name|currentValues
operator|.
name|length
condition|)
block|{
name|currentValues
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|currentValues
argument_list|,
name|currentValues
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|currentValues
operator|.
name|length
operator|-
name|currentUpto
operator|)
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
name|currentValues
index|[
name|currentUpto
index|]
operator|=
name|termID
expr_stmt|;
name|currentUpto
operator|++
expr_stmt|;
block|}
DECL|method|updateBytesUsed
specifier|private
name|void
name|updateBytesUsed
parameter_list|()
block|{
specifier|final
name|long
name|newBytesUsed
init|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|pendingCounts
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|newBytesUsed
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|newBytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|DocValuesConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|valueCount
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|PackedLongValues
name|ords
init|=
name|pending
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PackedLongValues
name|ordCounts
init|=
name|pendingCounts
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|sortedValues
init|=
name|hash
operator|.
name|sort
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|ordMap
init|=
operator|new
name|int
index|[
name|valueCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|valueCount
condition|;
name|ord
operator|++
control|)
block|{
name|ordMap
index|[
name|sortedValues
index|[
name|ord
index|]
index|]
operator|=
name|ord
expr_stmt|;
block|}
name|dvConsumer
operator|.
name|addSortedSetField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|EmptyDocValuesProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|fieldInfoIn
parameter_list|)
block|{
if|if
condition|(
name|fieldInfoIn
operator|!=
name|fieldInfo
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong fieldInfo"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BufferedSortedSetDocValues
argument_list|(
name|sortedValues
argument_list|,
name|ordMap
argument_list|,
name|hash
argument_list|,
name|ords
argument_list|,
name|ordCounts
argument_list|,
name|maxCount
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|BufferedSortedSetDocValues
specifier|private
specifier|static
class|class
name|BufferedSortedSetDocValues
extends|extends
name|SortedSetDocValues
block|{
DECL|field|sortedValues
specifier|final
name|int
index|[]
name|sortedValues
decl_stmt|;
DECL|field|ordMap
specifier|final
name|int
index|[]
name|ordMap
decl_stmt|;
DECL|field|hash
specifier|final
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|ordsIter
specifier|final
name|PackedLongValues
operator|.
name|Iterator
name|ordsIter
decl_stmt|;
DECL|field|ordCountsIter
specifier|final
name|PackedLongValues
operator|.
name|Iterator
name|ordCountsIter
decl_stmt|;
DECL|field|docsWithField
specifier|final
name|DocIdSetIterator
name|docsWithField
decl_stmt|;
DECL|field|currentDoc
specifier|final
name|int
name|currentDoc
index|[]
decl_stmt|;
DECL|field|ordCount
specifier|private
name|int
name|ordCount
decl_stmt|;
DECL|field|ordUpto
specifier|private
name|int
name|ordUpto
decl_stmt|;
DECL|method|BufferedSortedSetDocValues
specifier|public
name|BufferedSortedSetDocValues
parameter_list|(
name|int
index|[]
name|sortedValues
parameter_list|,
name|int
index|[]
name|ordMap
parameter_list|,
name|BytesRefHash
name|hash
parameter_list|,
name|PackedLongValues
name|ords
parameter_list|,
name|PackedLongValues
name|ordCounts
parameter_list|,
name|int
name|maxCount
parameter_list|,
name|FixedBitSet
name|docsWithField
parameter_list|)
block|{
name|this
operator|.
name|currentDoc
operator|=
operator|new
name|int
index|[
name|maxCount
index|]
expr_stmt|;
name|this
operator|.
name|sortedValues
operator|=
name|sortedValues
expr_stmt|;
name|this
operator|.
name|ordMap
operator|=
name|ordMap
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
name|this
operator|.
name|ordsIter
operator|=
name|ords
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|ordCountsIter
operator|=
name|ordCounts
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|docsWithField
operator|=
operator|new
name|BitSetIterator
argument_list|(
name|docsWithField
argument_list|,
name|ordCounts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docsWithField
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|docID
init|=
name|docsWithField
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|ordCount
operator|=
operator|(
name|int
operator|)
name|ordCountsIter
operator|.
name|next
argument_list|()
expr_stmt|;
assert|assert
name|ordCount
operator|>
literal|0
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordCount
condition|;
name|i
operator|++
control|)
block|{
name|currentDoc
index|[
name|i
index|]
operator|=
name|ordMap
index|[
name|Math
operator|.
name|toIntExact
argument_list|(
name|ordsIter
operator|.
name|next
argument_list|()
argument_list|)
index|]
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|currentDoc
argument_list|,
literal|0
argument_list|,
name|ordCount
argument_list|)
expr_stmt|;
name|ordUpto
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
if|if
condition|(
name|ordUpto
operator|==
name|ordCount
condition|)
block|{
return|return
name|NO_MORE_ORDS
return|;
block|}
else|else
block|{
return|return
name|currentDoc
index|[
name|ordUpto
operator|++
index|]
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|docsWithField
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|ordMap
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|ordMap
operator|.
name|length
operator|:
literal|"ord="
operator|+
name|ord
operator|+
literal|" is out of bounds 0 .. "
operator|+
operator|(
name|ordMap
operator|.
name|length
operator|-
literal|1
operator|)
assert|;
name|hash
operator|.
name|get
argument_list|(
name|sortedValues
index|[
name|Math
operator|.
name|toIntExact
argument_list|(
name|ord
argument_list|)
index|]
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
block|}
block|}
end_class

end_unit

