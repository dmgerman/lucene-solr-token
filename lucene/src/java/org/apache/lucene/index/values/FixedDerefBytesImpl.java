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
name|BytesBaseSource
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
name|PagedBytes
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
name|BytesRefHash
operator|.
name|TrackingDirectBytesStartArray
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
comment|// Stores fixed-length byte[] by deref, ie when two docs
end_comment

begin_comment
comment|// have the same value, they store only 1 byte[]
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FixedDerefBytesImpl
class|class
name|FixedDerefBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"FixedDerefBytes"
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
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docToID
specifier|private
name|int
index|[]
name|docToID
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
argument_list|,
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
operator|new
name|TrackingDirectBytesStartArray
argument_list|(
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
name|bytesUsed
argument_list|)
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
name|AtomicLong
name|bytesUsed
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
operator|new
name|DirectAllocator
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|)
argument_list|,
name|bytesUsed
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
literal|true
argument_list|,
literal|true
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
name|docToID
operator|=
operator|new
name|int
index|[
literal|1
index|]
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
comment|// TODO BytesRefHash
comment|// uses bytes too!
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
comment|// default value - skip it
return|return;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|size
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expected bytes size="
operator|+
name|size
operator|+
literal|" but got "
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
name|int
name|ord
init|=
name|hash
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
comment|// new added entry
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
block|}
else|else
block|{
name|ord
operator|=
operator|(
operator|-
name|ord
operator|)
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|docID
operator|>=
name|docToID
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|size
init|=
name|docToID
operator|.
name|length
decl_stmt|;
name|docToID
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docToID
argument_list|,
literal|1
operator|+
name|docID
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|docToID
operator|.
name|length
operator|-
name|size
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
name|docToID
index|[
name|docID
index|]
operator|=
literal|1
operator|+
name|ord
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
try|try
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|count
init|=
literal|1
operator|+
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|idxOut
operator|.
name|writeInt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// write index
specifier|final
name|PackedInts
operator|.
name|Writer
name|w
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
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|docCount
operator|>
name|docToID
operator|.
name|length
condition|?
name|docToID
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
name|w
operator|.
name|add
argument_list|(
name|docToID
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// fill up remaining doc with zeros
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
name|w
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|hash
operator|.
name|close
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
name|docToID
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|docToID
operator|=
literal|null
expr_stmt|;
block|}
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
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
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
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|index
init|=
name|cloneIndex
argument_list|()
decl_stmt|;
return|return
operator|new
name|Source
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|index
argument_list|,
name|size
argument_list|,
name|index
operator|.
name|readInt
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Source
specifier|private
specifier|static
class|class
name|Source
extends|extends
name|BytesBaseSource
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|index
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|numValues
specifier|private
specifier|final
name|int
name|numValues
decl_stmt|;
DECL|method|Source
specifier|protected
name|Source
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|numValues
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|size
operator|*
name|numValues
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|numValues
operator|=
name|numValues
expr_stmt|;
name|index
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
specifier|final
name|int
name|id
init|=
operator|(
name|int
operator|)
name|index
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
operator|(
operator|(
name|id
operator|-
literal|1
operator|)
operator|*
name|size
operator|)
argument_list|,
name|size
argument_list|)
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
name|numValues
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|Type
operator|.
name|BYTES_FIXED_DEREF
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|protected
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|index
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|DocValuesEnum
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
name|DerefBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|)
return|;
block|}
DECL|class|DerefBytesEnum
specifier|static
class|class
name|DerefBytesEnum
extends|extends
name|DocValuesEnum
block|{
DECL|field|datIn
specifier|protected
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|idx
specifier|private
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|idx
decl_stmt|;
DECL|field|fp
specifier|protected
specifier|final
name|long
name|fp
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|DerefBytesEnum
specifier|public
name|DerefBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|source
argument_list|,
name|datIn
argument_list|,
name|idxIn
argument_list|,
name|size
argument_list|,
name|Type
operator|.
name|BYTES_FIXED_DEREF
argument_list|)
expr_stmt|;
block|}
DECL|method|DerefBytesEnum
specifier|protected
name|DerefBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|int
name|size
parameter_list|,
name|Type
name|enumType
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|enumType
argument_list|)
expr_stmt|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|idxIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read valueCount
name|idx
operator|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|fp
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|bytesRef
operator|.
name|grow
argument_list|(
name|this
operator|.
name|size
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|this
operator|.
name|size
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|valueCount
operator|=
name|idx
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
DECL|method|copyReferences
specifier|protected
name|void
name|copyReferences
parameter_list|(
name|DocValuesEnum
name|valuesEnum
parameter_list|)
block|{
name|bytesRef
operator|=
name|valuesEnum
operator|.
name|bytesRef
expr_stmt|;
if|if
condition|(
name|bytesRef
operator|.
name|bytes
operator|.
name|length
operator|<
name|size
condition|)
block|{
name|bytesRef
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|bytesRef
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
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
operator|<
name|valueCount
condition|)
block|{
name|long
name|address
decl_stmt|;
while|while
condition|(
operator|(
name|address
operator|=
name|idx
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|++
name|target
operator|>=
name|valueCount
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
name|pos
operator|=
name|idx
operator|.
name|ord
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|address
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
return|return
name|pos
return|;
block|}
return|return
name|pos
operator|=
name|NO_MORE_DOCS
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
if|if
condition|(
name|pos
operator|>=
name|valueCount
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|idx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|fill
specifier|protected
name|void
name|fill
parameter_list|(
name|long
name|address
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
name|datIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
operator|(
operator|(
name|address
operator|-
literal|1
operator|)
operator|*
name|size
operator|)
argument_list|)
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
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
name|pos
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|Type
operator|.
name|BYTES_FIXED_DEREF
return|;
block|}
block|}
block|}
end_class

end_unit

