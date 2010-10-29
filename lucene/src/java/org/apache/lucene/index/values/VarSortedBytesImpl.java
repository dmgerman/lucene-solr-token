begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|values
operator|.
name|Bytes
operator|.
name|BytesBaseSortedSource
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
name|values
operator|.
name|Bytes
operator|.
name|BytesReaderBase
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
name|values
operator|.
name|Bytes
operator|.
name|BytesWriterBase
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
name|Directory
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
name|IndexInput
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
name|AttributeSource
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
name|RamUsageEstimator
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
operator|.
name|Allocator
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
operator|.
name|DirectAllocator
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

begin_comment
comment|// Stores variable-length byte[] by deref, ie when two docs
end_comment

begin_comment
comment|// have the same value, they store only 1 byte[] and both
end_comment

begin_comment
comment|// docs reference that single source
end_comment

begin_class
DECL|class|VarSortedBytesImpl
class|class
name|VarSortedBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"VarDerefBytes"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|class|Writer
specifier|static
class|class
name|Writer
extends|extends
name|BytesWriterBase
block|{
DECL|field|docToEntry
specifier|private
name|int
index|[]
name|docToEntry
decl_stmt|;
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|field|hash
specifier|private
specifier|final
name|BytesRefHash
name|hash
init|=
operator|new
name|BytesRefHash
argument_list|(
name|pool
argument_list|)
decl_stmt|;
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|comp
argument_list|,
operator|new
name|DirectAllocator
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|)
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|Allocator
name|allocator
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|ByteBlockPool
argument_list|(
name|allocator
argument_list|)
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|docToEntry
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
name|docToEntry
index|[
literal|0
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
comment|// default
if|if
condition|(
name|docID
operator|>=
name|docToEntry
operator|.
name|length
condition|)
block|{
name|int
index|[]
name|newArray
init|=
operator|new
name|int
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|docID
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|docToEntry
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|docToEntry
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|newArray
argument_list|,
name|docToEntry
operator|.
name|length
argument_list|,
name|newArray
operator|.
name|length
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|newArray
operator|.
name|length
operator|-
name|docToEntry
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|docToEntry
operator|=
name|newArray
expr_stmt|;
block|}
specifier|final
name|int
name|e
init|=
name|hash
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|docToEntry
index|[
name|docID
index|]
operator|=
name|e
operator|<
literal|0
condition|?
operator|(
operator|-
name|e
operator|)
operator|-
literal|1
else|:
name|e
expr_stmt|;
block|}
comment|// Important that we get docCount, in case there were
comment|// some last docs that we didn't see
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
return|return;
name|initIndexOut
argument_list|()
expr_stmt|;
name|initDataOut
argument_list|()
expr_stmt|;
name|int
index|[]
name|sortedEntries
init|=
name|hash
operator|.
name|sort
argument_list|(
name|comp
argument_list|)
decl_stmt|;
comment|// first dump bytes data, recording index& offset as
comment|// we go
name|long
name|offset
init|=
literal|0
decl_stmt|;
name|long
name|lastOffset
init|=
literal|0
decl_stmt|;
specifier|final
name|int
index|[]
name|index
init|=
operator|new
name|int
index|[
name|count
index|]
decl_stmt|;
specifier|final
name|long
index|[]
name|offsets
init|=
operator|new
name|long
index|[
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|e
init|=
name|sortedEntries
index|[
name|i
index|]
decl_stmt|;
name|offsets
index|[
name|i
index|]
operator|=
name|offset
expr_stmt|;
name|index
index|[
name|e
index|]
operator|=
literal|1
operator|+
name|i
expr_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|hash
operator|.
name|get
argument_list|(
name|e
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO: we could prefix code...
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|lastOffset
operator|=
name|offset
expr_stmt|;
name|offset
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
comment|// total bytes of data
name|idxOut
operator|.
name|writeLong
argument_list|(
name|offset
argument_list|)
expr_stmt|;
comment|// write index -- first doc -> 1+ord
comment|// TODO(simonw): allow not -1:
specifier|final
name|PackedInts
operator|.
name|Writer
name|indexWriter
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|count
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|docCount
operator|>
name|docToEntry
operator|.
name|length
condition|?
name|docToEntry
operator|.
name|length
else|:
name|docCount
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|e
init|=
name|docToEntry
index|[
name|i
index|]
decl_stmt|;
name|indexWriter
operator|.
name|add
argument_list|(
name|e
operator|==
operator|-
literal|1
condition|?
literal|0
else|:
name|index
index|[
name|e
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|limit
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|indexWriter
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|// next ord (0-based) -> offset
comment|// TODO(simonw): -- allow not -1:
name|PackedInts
operator|.
name|Writer
name|offsetWriter
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|count
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|lastOffset
argument_list|)
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|offsetWriter
operator|.
name|add
argument_list|(
name|offsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|offsetWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
name|super
operator|.
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
operator|-
name|docToEntry
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|BytesReaderBase
block|{
DECL|method|Reader
name|Reader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
operator|.
name|DocValues
operator|.
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|loadSorted
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loadSorted
specifier|public
name|SortedSource
name|loadSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Source
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|comp
argument_list|)
return|;
block|}
DECL|class|Source
specifier|private
specifier|static
class|class
name|Source
extends|extends
name|BytesBaseSortedSource
block|{
comment|// TODO: paged data
DECL|field|data
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
DECL|field|bytesRef
specifier|private
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|docToOrdIndex
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|docToOrdIndex
decl_stmt|;
DECL|field|ordToOffsetIndex
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|ordToOffsetIndex
decl_stmt|;
comment|// 0-based
DECL|field|totBytes
specifier|private
specifier|final
name|long
name|totBytes
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|field|lookupResult
specifier|private
specifier|final
name|LookupResult
name|lookupResult
init|=
operator|new
name|LookupResult
argument_list|()
decl_stmt|;
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|method|Source
specifier|public
name|Source
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|)
expr_stmt|;
name|totBytes
operator|=
name|idxIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|totBytes
index|]
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|totBytes
argument_list|)
expr_stmt|;
name|docToOrdIndex
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|ordToOffsetIndex
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|valueCount
operator|=
name|ordToOffsetIndex
operator|.
name|size
argument_list|()
expr_stmt|;
name|bytesRef
operator|.
name|bytes
operator|=
name|data
expr_stmt|;
comment|// default byte sort order
name|this
operator|.
name|comp
operator|=
name|comp
operator|==
literal|null
condition|?
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
else|:
name|comp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|ord
operator|==
literal|0
condition|?
name|defaultValue
else|:
name|deref
argument_list|(
operator|--
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|docToOrdIndex
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getByValue
specifier|public
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
return|return
name|binarySearch
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|valueCount
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// TODO(simonw): move ram usage to PackedInts?
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|data
operator|.
name|length
operator|+
operator|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|docToOrdIndex
operator|.
name|getBitsPerValue
argument_list|()
operator|*
name|docToOrdIndex
operator|.
name|getBitsPerValue
argument_list|()
operator|)
operator|+
operator|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|ordToOffsetIndex
operator|.
name|getBitsPerValue
argument_list|()
operator|*
name|ordToOffsetIndex
operator|.
name|getBitsPerValue
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
comment|// ord is 0-based
DECL|method|deref
specifier|private
name|BytesRef
name|deref
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
name|bytesRef
operator|.
name|offset
operator|=
operator|(
name|int
operator|)
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
name|ord
argument_list|)
expr_stmt|;
specifier|final
name|long
name|nextOffset
decl_stmt|;
if|if
condition|(
name|ord
operator|==
name|valueCount
operator|-
literal|1
condition|)
block|{
name|nextOffset
operator|=
name|totBytes
expr_stmt|;
block|}
else|else
block|{
name|nextOffset
operator|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
literal|1
operator|+
name|ord
argument_list|)
expr_stmt|;
block|}
name|bytesRef
operator|.
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|nextOffset
operator|-
name|bytesRef
operator|.
name|offset
argument_list|)
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
comment|// TODO: share w/ FixedSortedBytesValues?
DECL|method|binarySearch
specifier|private
name|LookupResult
name|binarySearch
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|deref
argument_list|(
name|mid
argument_list|)
expr_stmt|;
specifier|final
name|int
name|cmp
init|=
name|comp
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|lookupResult
operator|.
name|ord
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|lookupResult
operator|.
name|found
operator|=
literal|true
expr_stmt|;
return|return
name|lookupResult
return|;
block|}
block|}
assert|assert
name|comp
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
operator|!=
literal|0
assert|;
name|lookupResult
operator|.
name|ord
operator|=
name|low
expr_stmt|;
name|lookupResult
operator|.
name|found
operator|=
literal|false
expr_stmt|;
return|return
name|lookupResult
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VarSortedBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|)
return|;
block|}
DECL|class|VarSortedBytesEnum
specifier|private
specifier|static
class|class
name|VarSortedBytesEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|docToOrdIndex
specifier|private
name|PackedInts
operator|.
name|Reader
name|docToOrdIndex
decl_stmt|;
DECL|field|ordToOffsetIndex
specifier|private
name|PackedInts
operator|.
name|Reader
name|ordToOffsetIndex
decl_stmt|;
DECL|field|idxIn
specifier|private
name|IndexInput
name|idxIn
decl_stmt|;
DECL|field|datIn
specifier|private
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|bytesRef
specifier|private
specifier|final
name|BytesRef
name|bytesRef
decl_stmt|;
DECL|field|valueCount
specifier|private
name|int
name|valueCount
decl_stmt|;
DECL|field|totBytes
specifier|private
name|long
name|totBytes
decl_stmt|;
DECL|field|docCount
specifier|private
name|int
name|docCount
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fp
specifier|private
specifier|final
name|long
name|fp
decl_stmt|;
DECL|method|VarSortedBytesEnum
specifier|protected
name|VarSortedBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|Values
operator|.
name|BYTES_VAR_SORTED
argument_list|)
expr_stmt|;
name|bytesRef
operator|=
name|attr
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|totBytes
operator|=
name|idxIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// keep that in memory to prevent lots of disk seeks
name|docToOrdIndex
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|ordToOffsetIndex
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|valueCount
operator|=
name|ordToOffsetIndex
operator|.
name|size
argument_list|()
expr_stmt|;
name|docCount
operator|=
name|docToOrdIndex
operator|.
name|size
argument_list|()
expr_stmt|;
name|fp
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|idxIn
operator|=
name|idxIn
expr_stmt|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|idxIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|docCount
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
specifier|final
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|docToOrdIndex
operator|.
name|get
argument_list|(
name|target
argument_list|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|ord
operator|==
operator|-
literal|1
condition|)
block|{
name|bytesRef
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
specifier|final
name|long
name|offset
init|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
specifier|final
name|long
name|nextOffset
decl_stmt|;
if|if
condition|(
name|ord
operator|==
name|valueCount
operator|-
literal|1
condition|)
block|{
name|nextOffset
operator|=
name|totBytes
expr_stmt|;
block|}
else|else
block|{
name|nextOffset
operator|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
literal|1
operator|+
name|ord
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|nextOffset
operator|-
name|offset
argument_list|)
decl_stmt|;
name|datIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
name|offset
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRef
operator|.
name|bytes
operator|.
name|length
operator|<
name|length
condition|)
name|bytesRef
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
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
name|pos
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
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|BYTES_VAR_SORTED
return|;
block|}
block|}
block|}
end_class

end_unit

