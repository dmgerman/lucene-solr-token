begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|Comparator
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
name|codecs
operator|.
name|lucene40
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|BytesSortedSourceBase
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
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|DerefBytesWriterBase
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
name|DocValues
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
name|SortedBytesMergeUtils
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
name|DocValues
operator|.
name|SortedSource
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
name|DocValues
operator|.
name|Type
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
name|SortedBytesMergeUtils
operator|.
name|IndexOutputBytesRefConsumer
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
name|SortedBytesMergeUtils
operator|.
name|MergeContext
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
name|SortedBytesMergeUtils
operator|.
name|SortedSourceSlice
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
name|MergeState
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
name|IOContext
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
name|IOUtils
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
DECL|class|FixedSortedBytesImpl
class|class
name|FixedSortedBytesImpl
block|{
DECL|field|CODEC_NAME_IDX
specifier|static
specifier|final
name|String
name|CODEC_NAME_IDX
init|=
literal|"FixedSortedBytesIdx"
decl_stmt|;
DECL|field|CODEC_NAME_DAT
specifier|static
specifier|final
name|String
name|CODEC_NAME_DAT
init|=
literal|"FixedSortedBytesDat"
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
specifier|final
class|class
name|Writer
extends|extends
name|DerefBytesWriterBase
block|{
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
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
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|float
name|acceptableOverheadRatio
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
name|CODEC_NAME_IDX
argument_list|,
name|CODEC_NAME_DAT
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|,
name|Type
operator|.
name|BYTES_FIXED_SORTED
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|DocValues
index|[]
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|MergeContext
name|ctx
init|=
name|SortedBytesMergeUtils
operator|.
name|init
argument_list|(
name|Type
operator|.
name|BYTES_FIXED_SORTED
argument_list|,
name|docValues
argument_list|,
name|comp
argument_list|,
name|mergeState
operator|.
name|mergedDocCount
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SortedSourceSlice
argument_list|>
name|slices
init|=
name|SortedBytesMergeUtils
operator|.
name|buildSlices
argument_list|(
name|mergeState
operator|.
name|docBase
argument_list|,
name|mergeState
operator|.
name|docMaps
argument_list|,
name|docValues
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|ctx
operator|.
name|sizePerValues
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxOrd
init|=
name|SortedBytesMergeUtils
operator|.
name|mergeRecords
argument_list|(
name|ctx
argument_list|,
operator|new
name|IndexOutputBytesRefConsumer
argument_list|(
name|datOut
argument_list|)
argument_list|,
name|slices
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
name|idxOut
operator|.
name|writeInt
argument_list|(
name|maxOrd
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|ordsWriter
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|ctx
operator|.
name|docToEntry
operator|.
name|length
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxOrd
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|SortedSourceSlice
name|slice
range|:
name|slices
control|)
block|{
name|slice
operator|.
name|writeOrds
argument_list|(
name|ordsWriter
argument_list|)
expr_stmt|;
block|}
name|ordsWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|releaseResources
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|getIndexOut
argument_list|()
argument_list|,
name|getDataOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|getIndexOut
argument_list|()
argument_list|,
name|getDataOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Important that we get docCount, in case there were
comment|// some last docs that we didn't see
annotation|@
name|Override
DECL|method|finishInternal
specifier|public
name|void
name|finishInternal
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|fillDefault
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
specifier|final
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|address
init|=
operator|new
name|int
index|[
name|count
index|]
decl_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
block|{
specifier|final
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
comment|// first dump bytes data, recording address as we go
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|(
name|size
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
specifier|final
name|int
name|e
init|=
name|sortedEntries
index|[
name|i
index|]
decl_stmt|;
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
name|spare
argument_list|)
decl_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|size
assert|;
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
name|address
index|[
name|e
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
name|idxOut
operator|.
name|writeInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|writeIndex
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|count
argument_list|,
name|address
argument_list|,
name|docToEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Reader
specifier|static
specifier|final
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
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|method|Reader
specifier|public
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
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|Type
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
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
name|CODEC_NAME_IDX
argument_list|,
name|CODEC_NAME_DAT
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|valueCount
operator|=
name|idxIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
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
return|return
operator|new
name|FixedSortedSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|,
name|valueCount
argument_list|,
name|comparator
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DirectFixedSortedSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|,
name|valueCount
argument_list|,
name|comparator
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
DECL|class|FixedSortedSource
specifier|static
specifier|final
class|class
name|FixedSortedSource
extends|extends
name|BytesSortedSourceBase
block|{
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|FixedSortedSource
name|FixedSortedSource
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
argument_list|,
name|comp
argument_list|,
name|size
operator|*
name|numValues
argument_list|,
name|Type
operator|.
name|BYTES_FIXED_SORTED
argument_list|,
literal|false
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
name|valueCount
operator|=
name|numValues
expr_stmt|;
name|closeIndexInput
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
operator|(
name|ord
operator|*
name|size
operator|)
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
DECL|class|DirectFixedSortedSource
specifier|static
specifier|final
class|class
name|DirectFixedSortedSource
extends|extends
name|SortedSource
block|{
DECL|field|docToOrdIndex
specifier|final
name|PackedInts
operator|.
name|Reader
name|docToOrdIndex
decl_stmt|;
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|basePointer
specifier|private
specifier|final
name|long
name|basePointer
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
DECL|method|DirectFixedSortedSource
name|DirectFixedSortedSource
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
name|valueCount
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|Type
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|type
argument_list|,
name|comp
argument_list|)
expr_stmt|;
name|docToOrdIndex
operator|=
name|PackedInts
operator|.
name|getDirectReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|basePointer
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
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
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
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
DECL|method|hasPackedDocToOrd
specifier|public
name|boolean
name|hasPackedDocToOrd
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getDocToOrd
specifier|public
name|PackedInts
operator|.
name|Reader
name|getDocToOrd
parameter_list|()
block|{
return|return
name|docToOrdIndex
return|;
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
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
try|try
block|{
name|datIn
operator|.
name|seek
argument_list|(
name|basePointer
operator|+
name|size
operator|*
name|ord
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|grow
argument_list|(
name|size
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
name|size
argument_list|)
expr_stmt|;
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
return|return
name|bytesRef
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to getByOrd"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
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
block|}
block|}
end_class

end_unit

