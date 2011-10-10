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
name|BytesSourceBase
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
name|index
operator|.
name|values
operator|.
name|DirectSource
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
name|IndexDocValues
operator|.
name|Source
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
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
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
name|PagedBytes
import|;
end_import

begin_comment
comment|// Simplest storage: stores fixed length byte[] per
end_comment

begin_comment
comment|// document, with no dedup and no sorting.
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FixedStraightBytesImpl
class|class
name|FixedStraightBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"FixedStraightBytes"
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
DECL|class|FixedBytesWriterBase
specifier|static
specifier|abstract
class|class
name|FixedBytesWriterBase
extends|extends
name|BytesWriterBase
block|{
DECL|field|lastDocID
specifier|protected
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
comment|// start at -1 if the first added value is> 0
DECL|field|size
specifier|protected
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|byteBlockSize
specifier|private
specifier|final
name|int
name|byteBlockSize
init|=
name|BYTE_BLOCK_SIZE
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|method|FixedBytesWriterBase
specifier|protected
name|FixedBytesWriterBase
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|codecName
parameter_list|,
name|int
name|version
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
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
name|codecName
argument_list|,
name|version
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|DirectTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
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
assert|assert
name|lastDocID
operator|<
name|docID
assert|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|>
name|BYTE_BLOCK_SIZE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bytes arrays> "
operator|+
name|Short
operator|.
name|MAX_VALUE
operator|+
literal|" are not supported"
argument_list|)
throw|;
block|}
name|size
operator|=
name|bytes
operator|.
name|length
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
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|docID
condition|)
block|{
name|advancePool
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|copy
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
DECL|method|advancePool
specifier|private
specifier|final
name|void
name|advancePool
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|long
name|numBytes
init|=
operator|(
name|docID
operator|-
operator|(
name|lastDocID
operator|+
literal|1
operator|)
operator|)
operator|*
name|size
decl_stmt|;
while|while
condition|(
name|numBytes
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|numBytes
operator|+
name|pool
operator|.
name|byteUpto
operator|<
name|byteBlockSize
condition|)
block|{
name|pool
operator|.
name|byteUpto
operator|+=
name|numBytes
expr_stmt|;
name|numBytes
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|numBytes
operator|-=
name|byteBlockSize
operator|-
name|pool
operator|.
name|byteUpto
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|numBytes
operator|==
literal|0
assert|;
block|}
DECL|method|set
specifier|protected
name|void
name|set
parameter_list|(
name|BytesRef
name|ref
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
assert|assert
name|BYTE_BLOCK_SIZE
operator|%
name|size
operator|==
literal|0
operator|:
literal|"BYTE_BLOCK_SIZE ("
operator|+
name|BYTE_BLOCK_SIZE
operator|+
literal|") must be a multiple of the size: "
operator|+
name|size
assert|;
name|ref
operator|.
name|offset
operator|=
name|docId
operator|*
name|size
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|pool
operator|.
name|deref
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
DECL|method|resetPool
specifier|protected
name|void
name|resetPool
parameter_list|()
block|{
name|pool
operator|.
name|dropBuffersAndReset
argument_list|()
expr_stmt|;
block|}
DECL|method|writeData
specifier|protected
name|void
name|writeData
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|pool
operator|.
name|writePool
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeZeros
specifier|protected
name|void
name|writeZeros
parameter_list|(
name|int
name|num
parameter_list|,
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|zeros
init|=
operator|new
name|byte
index|[
name|size
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|zeros
argument_list|,
name|zeros
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Writer
specifier|static
class|class
name|Writer
extends|extends
name|FixedBytesWriterBase
block|{
DECL|field|hasMerged
specifier|private
name|boolean
name|hasMerged
decl_stmt|;
DECL|field|datOut
specifier|private
name|IndexOutput
name|datOut
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
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
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
name|bytesUsed
argument_list|,
name|context
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
name|String
name|codecName
parameter_list|,
name|int
name|version
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
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
name|codecName
argument_list|,
name|version
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|SingleSubMergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|datOut
operator|=
name|getOrCreateDataOut
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|hasMerged
operator|&&
name|size
operator|!=
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
if|if
condition|(
name|state
operator|.
name|liveDocs
operator|==
literal|null
operator|&&
name|tryBulkMerge
argument_list|(
name|state
operator|.
name|reader
argument_list|)
condition|)
block|{
name|FixedStraightReader
name|reader
init|=
operator|(
name|FixedStraightReader
operator|)
name|state
operator|.
name|reader
decl_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|reader
operator|.
name|maxDoc
decl_stmt|;
if|if
condition|(
name|maxDocs
operator|==
literal|0
condition|)
block|{
return|return;
block|}
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
name|reader
operator|.
name|size
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
name|size
operator|!=
name|reader
operator|.
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
name|reader
operator|.
name|size
argument_list|)
throw|;
block|}
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|state
operator|.
name|docBase
condition|)
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|state
operator|.
name|docBase
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|state
operator|.
name|docBase
operator|-
literal|1
expr_stmt|;
block|}
comment|// TODO should we add a transfer to API to each reader?
specifier|final
name|IndexInput
name|cloneData
init|=
name|reader
operator|.
name|cloneData
argument_list|()
decl_stmt|;
try|try
block|{
name|datOut
operator|.
name|copyBytes
argument_list|(
name|cloneData
argument_list|,
name|size
operator|*
name|maxDocs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|cloneData
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|+=
name|maxDocs
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|merge
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
name|hasMerged
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|tryBulkMerge
specifier|protected
name|boolean
name|tryBulkMerge
parameter_list|(
name|IndexDocValues
name|docValues
parameter_list|)
block|{
return|return
name|docValues
operator|instanceof
name|FixedStraightReader
return|;
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|sourceDoc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|lastDocID
operator|<
name|docID
assert|;
name|setMergeBytes
argument_list|(
name|sourceDoc
argument_list|)
expr_stmt|;
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
name|bytesRef
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
assert|assert
name|size
operator|==
name|bytesRef
operator|.
name|length
operator|:
literal|"size: "
operator|+
name|size
operator|+
literal|" ref: "
operator|+
name|bytesRef
operator|.
name|length
assert|;
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|docID
condition|)
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
name|bytesRef
operator|.
name|offset
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
DECL|method|setMergeBytes
specifier|protected
name|void
name|setMergeBytes
parameter_list|(
name|int
name|sourceDoc
parameter_list|)
block|{
name|currentMergeSource
operator|.
name|getBytes
argument_list|(
name|sourceDoc
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
comment|// Fills up to but not including this docID
DECL|method|fill
specifier|private
name|void
name|fill
parameter_list|(
name|IndexOutput
name|datOut
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|size
operator|>=
literal|0
assert|;
name|writeZeros
argument_list|(
operator|(
name|docID
operator|-
operator|(
name|lastDocID
operator|+
literal|1
operator|)
operator|)
argument_list|,
name|datOut
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
name|docCount
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
if|if
condition|(
operator|!
name|hasMerged
condition|)
block|{
comment|// indexing path - no disk IO until here
assert|assert
name|datOut
operator|==
literal|null
assert|;
name|datOut
operator|=
name|getOrCreateDataOut
argument_list|()
expr_stmt|;
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
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|writeData
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|docCount
condition|)
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// merge path - datOut should be initialized
assert|assert
name|datOut
operator|!=
literal|null
assert|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no data added
name|datOut
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|resetPool
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
name|datOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|FixedStraightReader
specifier|public
specifier|static
class|class
name|FixedStraightReader
extends|extends
name|BytesReaderBase
block|{
DECL|field|size
specifier|protected
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|FixedStraightReader
name|FixedStraightReader
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
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|,
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|)
expr_stmt|;
block|}
DECL|method|FixedStraightReader
specifier|protected
name|FixedStraightReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|version
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|ValueType
name|type
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
name|codec
argument_list|,
name|version
argument_list|,
literal|false
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
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
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
name|size
operator|==
literal|1
condition|?
operator|new
name|SingleByteSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
else|:
operator|new
name|FixedStraightSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|size
argument_list|,
name|maxDoc
argument_list|,
name|type
argument_list|)
return|;
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
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|DirectFixedStraightSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|size
argument_list|,
name|type
argument_list|()
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
comment|// specialized version for single bytes
DECL|class|SingleByteSource
specifier|private
specifier|static
specifier|final
class|class
name|SingleByteSource
extends|extends
name|Source
block|{
DECL|field|data
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
DECL|method|SingleByteSource
specifier|public
name|SingleByteSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|)
expr_stmt|;
try|try
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|maxDoc
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
name|data
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|datIn
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|data
return|;
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
name|bytesRef
operator|.
name|length
operator|=
literal|1
expr_stmt|;
name|bytesRef
operator|.
name|bytes
operator|=
name|data
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
name|docID
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
block|}
DECL|class|FixedStraightSource
specifier|private
specifier|final
specifier|static
class|class
name|FixedStraightSource
extends|extends
name|BytesSourceBase
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|FixedStraightSource
specifier|public
name|FixedStraightSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|ValueType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
literal|null
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|size
operator|*
name|maxDoc
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
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
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
name|docID
operator|*
name|size
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
DECL|class|DirectFixedStraightSource
specifier|public
specifier|final
specifier|static
class|class
name|DirectFixedStraightSource
extends|extends
name|DirectSource
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|DirectFixedStraightSource
name|DirectFixedStraightSource
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|size
parameter_list|,
name|ValueType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|protected
name|int
name|position
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|.
name|seek
argument_list|(
name|baseOffset
operator|+
name|size
operator|*
name|docID
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
block|}
block|}
end_class

end_unit

