begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|Closeable
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
name|index
operator|.
name|CorruptIndexException
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
name|SegmentInfo
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
comment|/**  * A format for the stored fields index file (.fdx).  *<p>  * These formats allow different memory/speed trade-offs to locate documents  * into the fields data file (.fdt).  * @lucene.experimental  */
end_comment

begin_enum
DECL|enum|CompressingStoredFieldsIndex
specifier|public
enum|enum
name|CompressingStoredFieldsIndex
block|{
comment|/**    * This format stores the document index on disk using 64-bits pointers to    * the start offsets of chunks in the fields data file.    *<p>    * This format has no memory overhead and requires at most 1 disk seek to    * locate a document in the fields data file. Use this fields index in    * memory-constrained environments.    */
DECL|enum constant|DISK_DOC
name|DISK_DOC
argument_list|(
literal|0
argument_list|)
block|{
annotation|@
name|Override
name|Writer
name|newWriter
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
block|{
return|return
operator|new
name|DiskDocFieldsIndexWriter
argument_list|(
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Reader
name|newReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DiskDocFieldsIndexReader
argument_list|(
name|in
argument_list|,
name|si
argument_list|)
return|;
block|}
block|}
block|,
comment|/**    * For every chunk of compressed documents, this index stores the first doc    * ID of the chunk as well as the start offset of the chunk.    *<p>    * This fields index uses a very compact in-memory representation (up to    *<code>12 * numChunks</code> bytes, but likely much less) and requires no    * disk seek to locate a document in the fields data file. Unless you are    * working with very little memory, you should use this instance.    */
DECL|enum constant|MEMORY_CHUNK
name|MEMORY_CHUNK
argument_list|(
literal|1
argument_list|)
block|{
annotation|@
name|Override
name|Writer
name|newWriter
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MemoryChunkFieldsIndexWriter
argument_list|(
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Reader
name|newReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MemoryChunkFieldsIndexReader
argument_list|(
name|in
argument_list|,
name|si
argument_list|)
return|;
block|}
block|}
block|;
comment|/**    * Retrieve a {@link CompressingStoredFieldsIndex} according to its    *<code>ID</code>.    */
DECL|method|byId
specifier|public
specifier|static
name|CompressingStoredFieldsIndex
name|byId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
for|for
control|(
name|CompressingStoredFieldsIndex
name|idx
range|:
name|CompressingStoredFieldsIndex
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|idx
operator|.
name|getId
argument_list|()
operator|==
name|id
condition|)
block|{
return|return
name|idx
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown id: "
operator|+
name|id
argument_list|)
throw|;
block|}
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|method|CompressingStoredFieldsIndex
specifier|private
name|CompressingStoredFieldsIndex
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Returns an ID for this compression mode. Should be unique across    * {@link CompressionMode}s as it is used for serialization and    * unserialization.    */
DECL|method|getId
specifier|public
specifier|final
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|newWriter
specifier|abstract
name|Writer
name|newWriter
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|newReader
specifier|abstract
name|Reader
name|newReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|class|Writer
specifier|static
specifier|abstract
class|class
name|Writer
implements|implements
name|Closeable
block|{
DECL|field|fieldsIndexOut
specifier|protected
specifier|final
name|IndexOutput
name|fieldsIndexOut
decl_stmt|;
DECL|method|Writer
name|Writer
parameter_list|(
name|IndexOutput
name|indexOutput
parameter_list|)
block|{
name|this
operator|.
name|fieldsIndexOut
operator|=
name|indexOutput
expr_stmt|;
block|}
comment|/** Write the index file for a chunk of<code>numDocs</code> docs starting      *  at offset<code>startPointer</code>. */
DECL|method|writeIndex
specifier|abstract
name|void
name|writeIndex
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|long
name|startPointer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Finish writing an index file of<code>numDocs</code> documents. */
DECL|method|finish
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
name|fieldsIndexOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|DiskDocFieldsIndexWriter
specifier|private
specifier|static
class|class
name|DiskDocFieldsIndexWriter
extends|extends
name|Writer
block|{
DECL|field|startOffset
specifier|final
name|long
name|startOffset
decl_stmt|;
DECL|method|DiskDocFieldsIndexWriter
name|DiskDocFieldsIndexWriter
parameter_list|(
name|IndexOutput
name|fieldsIndexOut
parameter_list|)
block|{
name|super
argument_list|(
name|fieldsIndexOut
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|fieldsIndexOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeIndex
name|void
name|writeIndex
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|long
name|startPointer
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|fieldsIndexOut
operator|.
name|writeLong
argument_list|(
name|startPointer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|(
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|startOffset
operator|+
operator|(
operator|(
name|long
operator|)
name|numDocs
operator|)
operator|*
literal|8
operator|!=
name|fieldsIndexOut
operator|.
name|getFilePointer
argument_list|()
condition|)
block|{
comment|// see Lucene40StoredFieldsWriter#finish
throw|throw
operator|new
name|RuntimeException
argument_list|(
operator|(
name|fieldsIndexOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|startOffset
operator|)
operator|/
literal|8
operator|+
literal|" fdx size mismatch: docCount is "
operator|+
name|numDocs
operator|+
literal|" but fdx file size is "
operator|+
name|fieldsIndexOut
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" file="
operator|+
name|fieldsIndexOut
operator|.
name|toString
argument_list|()
operator|+
literal|"; now aborting this merge to prevent index corruption"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|MemoryChunkFieldsIndexWriter
specifier|private
specifier|static
class|class
name|MemoryChunkFieldsIndexWriter
extends|extends
name|Writer
block|{
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
comment|// number of chunks to serialize at once
DECL|method|moveSignToLowOrderBit
specifier|static
name|long
name|moveSignToLowOrderBit
parameter_list|(
name|long
name|n
parameter_list|)
block|{
return|return
operator|(
name|n
operator|>>
literal|63
operator|)
operator|^
operator|(
name|n
operator|<<
literal|1
operator|)
return|;
block|}
DECL|field|totalDocs
name|int
name|totalDocs
decl_stmt|;
DECL|field|blockDocs
name|int
name|blockDocs
decl_stmt|;
DECL|field|blockChunks
name|int
name|blockChunks
decl_stmt|;
DECL|field|firstStartPointer
name|long
name|firstStartPointer
decl_stmt|;
DECL|field|maxStartPointer
name|long
name|maxStartPointer
decl_stmt|;
DECL|field|docBaseDeltas
specifier|final
name|int
index|[]
name|docBaseDeltas
decl_stmt|;
DECL|field|startPointerDeltas
specifier|final
name|long
index|[]
name|startPointerDeltas
decl_stmt|;
DECL|method|MemoryChunkFieldsIndexWriter
name|MemoryChunkFieldsIndexWriter
parameter_list|(
name|IndexOutput
name|indexOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|indexOutput
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|totalDocs
operator|=
literal|0
expr_stmt|;
name|docBaseDeltas
operator|=
operator|new
name|int
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|startPointerDeltas
operator|=
operator|new
name|long
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|blockChunks
operator|=
literal|0
expr_stmt|;
name|blockDocs
operator|=
literal|0
expr_stmt|;
name|firstStartPointer
operator|=
operator|-
literal|1
expr_stmt|;
comment|// means unset
block|}
DECL|method|writeBlock
specifier|private
name|void
name|writeBlock
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|blockChunks
operator|>
literal|0
assert|;
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|blockChunks
argument_list|)
expr_stmt|;
comment|// The trick here is that we only store the difference from the average start
comment|// pointer or doc base, this helps save bits per value.
comment|// And in order to prevent a few chunks that would be far from the average to
comment|// raise the number of bits per value for all of them, we only encode blocks
comment|// of 1024 chunks at once
comment|// See LUCENE-4512
comment|// doc bases
specifier|final
name|int
name|avgChunkDocs
decl_stmt|;
if|if
condition|(
name|blockChunks
operator|==
literal|1
condition|)
block|{
name|avgChunkDocs
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|avgChunkDocs
operator|=
name|Math
operator|.
name|round
argument_list|(
call|(
name|float
call|)
argument_list|(
name|blockDocs
operator|-
name|docBaseDeltas
index|[
name|blockChunks
operator|-
literal|1
index|]
argument_list|)
operator|/
operator|(
name|blockChunks
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|totalDocs
operator|-
name|blockDocs
argument_list|)
expr_stmt|;
comment|// docBase
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|avgChunkDocs
argument_list|)
expr_stmt|;
name|int
name|docBase
init|=
literal|0
decl_stmt|;
name|long
name|maxDelta
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
name|blockChunks
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|delta
init|=
name|docBase
operator|-
name|avgChunkDocs
operator|*
name|i
decl_stmt|;
name|maxDelta
operator||=
name|moveSignToLowOrderBit
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|docBase
operator|+=
name|docBaseDeltas
index|[
name|i
index|]
expr_stmt|;
block|}
specifier|final
name|int
name|bitsPerDocBase
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxDelta
argument_list|)
decl_stmt|;
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|bitsPerDocBase
argument_list|)
expr_stmt|;
name|PackedInts
operator|.
name|Writer
name|writer
init|=
name|PackedInts
operator|.
name|getWriterNoHeader
argument_list|(
name|fieldsIndexOut
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|blockChunks
argument_list|,
name|bitsPerDocBase
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|docBase
operator|=
literal|0
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
name|blockChunks
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|delta
init|=
name|docBase
operator|-
name|avgChunkDocs
operator|*
name|i
decl_stmt|;
assert|assert
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|moveSignToLowOrderBit
argument_list|(
name|delta
argument_list|)
argument_list|)
operator|<=
name|writer
operator|.
name|bitsPerValue
argument_list|()
assert|;
name|writer
operator|.
name|add
argument_list|(
name|moveSignToLowOrderBit
argument_list|(
name|delta
argument_list|)
argument_list|)
expr_stmt|;
name|docBase
operator|+=
name|docBaseDeltas
index|[
name|i
index|]
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|// start pointers
name|fieldsIndexOut
operator|.
name|writeVLong
argument_list|(
name|firstStartPointer
argument_list|)
expr_stmt|;
specifier|final
name|long
name|avgChunkSize
decl_stmt|;
if|if
condition|(
name|blockChunks
operator|==
literal|1
condition|)
block|{
name|avgChunkSize
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|avgChunkSize
operator|=
operator|(
name|maxStartPointer
operator|-
name|firstStartPointer
operator|)
operator|/
operator|(
name|blockChunks
operator|-
literal|1
operator|)
expr_stmt|;
block|}
name|fieldsIndexOut
operator|.
name|writeVLong
argument_list|(
name|avgChunkSize
argument_list|)
expr_stmt|;
name|long
name|startPointer
init|=
literal|0
decl_stmt|;
name|maxDelta
operator|=
literal|0
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
name|blockChunks
condition|;
operator|++
name|i
control|)
block|{
name|startPointer
operator|+=
name|startPointerDeltas
index|[
name|i
index|]
expr_stmt|;
specifier|final
name|long
name|delta
init|=
name|startPointer
operator|-
name|avgChunkSize
operator|*
name|i
decl_stmt|;
name|maxDelta
operator||=
name|moveSignToLowOrderBit
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|bitsPerStartPointer
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxDelta
argument_list|)
decl_stmt|;
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|bitsPerStartPointer
argument_list|)
expr_stmt|;
name|writer
operator|=
name|PackedInts
operator|.
name|getWriterNoHeader
argument_list|(
name|fieldsIndexOut
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|blockChunks
argument_list|,
name|bitsPerStartPointer
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|startPointer
operator|=
literal|0
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
name|blockChunks
condition|;
operator|++
name|i
control|)
block|{
name|startPointer
operator|+=
name|startPointerDeltas
index|[
name|i
index|]
expr_stmt|;
specifier|final
name|long
name|delta
init|=
name|startPointer
operator|-
name|avgChunkSize
operator|*
name|i
decl_stmt|;
assert|assert
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|moveSignToLowOrderBit
argument_list|(
name|delta
argument_list|)
argument_list|)
operator|<=
name|writer
operator|.
name|bitsPerValue
argument_list|()
assert|;
name|writer
operator|.
name|add
argument_list|(
name|moveSignToLowOrderBit
argument_list|(
name|delta
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeIndex
name|void
name|writeIndex
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|long
name|startPointer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|blockChunks
operator|==
name|BLOCK_SIZE
condition|)
block|{
name|writeBlock
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|firstStartPointer
operator|==
operator|-
literal|1
condition|)
block|{
name|firstStartPointer
operator|=
name|maxStartPointer
operator|=
name|startPointer
expr_stmt|;
block|}
assert|assert
name|firstStartPointer
operator|>
literal|0
operator|&&
name|startPointer
operator|>=
name|firstStartPointer
assert|;
name|docBaseDeltas
index|[
name|blockChunks
index|]
operator|=
name|numDocs
expr_stmt|;
name|startPointerDeltas
index|[
name|blockChunks
index|]
operator|=
name|startPointer
operator|-
name|maxStartPointer
expr_stmt|;
operator|++
name|blockChunks
expr_stmt|;
name|blockDocs
operator|+=
name|numDocs
expr_stmt|;
name|totalDocs
operator|+=
name|numDocs
expr_stmt|;
name|maxStartPointer
operator|=
name|startPointer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|(
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numDocs
operator|!=
name|totalDocs
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Expected "
operator|+
name|numDocs
operator|+
literal|" docs, but got "
operator|+
name|totalDocs
argument_list|)
throw|;
block|}
if|if
condition|(
name|blockChunks
operator|>
literal|0
condition|)
block|{
name|writeBlock
argument_list|()
expr_stmt|;
block|}
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// end marker
block|}
block|}
DECL|class|Reader
specifier|static
specifier|abstract
class|class
name|Reader
implements|implements
name|Cloneable
implements|,
name|Closeable
block|{
DECL|field|fieldsIndexIn
specifier|protected
specifier|final
name|IndexInput
name|fieldsIndexIn
decl_stmt|;
DECL|method|Reader
name|Reader
parameter_list|(
name|IndexInput
name|fieldsIndexIn
parameter_list|)
block|{
name|this
operator|.
name|fieldsIndexIn
operator|=
name|fieldsIndexIn
expr_stmt|;
block|}
comment|/** Get the start pointer of the compressed block that contains docID */
DECL|method|getStartPointer
specifier|abstract
name|long
name|getStartPointer
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fieldsIndexIn
argument_list|)
expr_stmt|;
block|}
DECL|method|clone
specifier|public
specifier|abstract
name|Reader
name|clone
parameter_list|()
function_decl|;
block|}
DECL|class|DiskDocFieldsIndexReader
specifier|private
specifier|static
class|class
name|DiskDocFieldsIndexReader
extends|extends
name|Reader
block|{
DECL|field|startPointer
specifier|final
name|long
name|startPointer
decl_stmt|;
DECL|method|DiskDocFieldsIndexReader
name|DiskDocFieldsIndexReader
parameter_list|(
name|IndexInput
name|fieldsIndexIn
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|CorruptIndexException
block|{
name|this
argument_list|(
name|fieldsIndexIn
argument_list|,
name|fieldsIndexIn
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|indexSize
init|=
name|fieldsIndexIn
operator|.
name|length
argument_list|()
operator|-
name|fieldsIndexIn
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|>>
literal|3
argument_list|)
decl_stmt|;
comment|// Verify two sources of "maxDoc" agree:
if|if
condition|(
name|numDocs
operator|!=
name|si
operator|.
name|getDocCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"doc counts differ for segment "
operator|+
name|si
operator|+
literal|": fieldsReader shows "
operator|+
name|numDocs
operator|+
literal|" but segmentInfo shows "
operator|+
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|DiskDocFieldsIndexReader
specifier|private
name|DiskDocFieldsIndexReader
parameter_list|(
name|IndexInput
name|fieldsIndexIn
parameter_list|,
name|long
name|startPointer
parameter_list|)
block|{
name|super
argument_list|(
name|fieldsIndexIn
argument_list|)
expr_stmt|;
name|this
operator|.
name|startPointer
operator|=
name|startPointer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStartPointer
name|long
name|getStartPointer
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsIndexIn
operator|.
name|seek
argument_list|(
name|startPointer
operator|+
name|docID
operator|*
literal|8L
argument_list|)
expr_stmt|;
return|return
name|fieldsIndexIn
operator|.
name|readLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Reader
name|clone
parameter_list|()
block|{
return|return
operator|new
name|DiskDocFieldsIndexReader
argument_list|(
name|fieldsIndexIn
operator|.
name|clone
argument_list|()
argument_list|,
name|startPointer
argument_list|)
return|;
block|}
block|}
DECL|class|MemoryChunkFieldsIndexReader
specifier|private
specifier|static
class|class
name|MemoryChunkFieldsIndexReader
extends|extends
name|Reader
block|{
DECL|method|moveLowOrderBitToSign
specifier|static
name|long
name|moveLowOrderBitToSign
parameter_list|(
name|long
name|n
parameter_list|)
block|{
return|return
operator|(
operator|(
name|n
operator|>>>
literal|1
operator|)
operator|^
operator|-
operator|(
name|n
operator|&
literal|1
operator|)
operator|)
return|;
block|}
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|docBases
specifier|private
specifier|final
name|int
index|[]
name|docBases
decl_stmt|;
DECL|field|startPointers
specifier|private
specifier|final
name|long
index|[]
name|startPointers
decl_stmt|;
DECL|field|avgChunkDocs
specifier|private
specifier|final
name|int
index|[]
name|avgChunkDocs
decl_stmt|;
DECL|field|avgChunkSizes
specifier|private
specifier|final
name|long
index|[]
name|avgChunkSizes
decl_stmt|;
DECL|field|docBasesDeltas
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|docBasesDeltas
decl_stmt|;
comment|// delta from the avg
DECL|field|startPointersDeltas
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|startPointersDeltas
decl_stmt|;
comment|// delta from the avg
DECL|method|MemoryChunkFieldsIndexReader
name|MemoryChunkFieldsIndexReader
parameter_list|(
name|IndexInput
name|fieldsIndexIn
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fieldsIndexIn
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|si
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|int
index|[]
name|docBases
init|=
operator|new
name|int
index|[
literal|16
index|]
decl_stmt|;
name|long
index|[]
name|startPointers
init|=
operator|new
name|long
index|[
literal|16
index|]
decl_stmt|;
name|int
index|[]
name|avgChunkDocs
init|=
operator|new
name|int
index|[
literal|16
index|]
decl_stmt|;
name|long
index|[]
name|avgChunkSizes
init|=
operator|new
name|long
index|[
literal|16
index|]
decl_stmt|;
name|PackedInts
operator|.
name|Reader
index|[]
name|docBasesDeltas
init|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
literal|16
index|]
decl_stmt|;
name|PackedInts
operator|.
name|Reader
index|[]
name|startPointersDeltas
init|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
literal|16
index|]
decl_stmt|;
specifier|final
name|int
name|packedIntsVersion
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|blockCount
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|numChunks
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numChunks
operator|==
literal|0
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|blockCount
operator|==
name|docBases
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|blockCount
operator|+
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|docBases
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBases
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|startPointers
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointers
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|avgChunkDocs
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkDocs
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|avgChunkSizes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkSizes
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|docBasesDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBasesDeltas
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|startPointersDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointersDeltas
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
block|}
comment|// doc bases
name|docBases
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|avgChunkDocs
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
specifier|final
name|int
name|bitsPerDocBase
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerDocBase
operator|>
literal|32
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted"
argument_list|)
throw|;
block|}
name|docBasesDeltas
index|[
name|blockCount
index|]
operator|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|numChunks
argument_list|,
name|bitsPerDocBase
argument_list|)
expr_stmt|;
comment|// start pointers
name|startPointers
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|avgChunkSizes
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVLong
argument_list|()
expr_stmt|;
specifier|final
name|int
name|bitsPerStartPointer
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerStartPointer
operator|>
literal|64
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted"
argument_list|)
throw|;
block|}
name|startPointersDeltas
index|[
name|blockCount
index|]
operator|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|numChunks
argument_list|,
name|bitsPerStartPointer
argument_list|)
expr_stmt|;
operator|++
name|blockCount
expr_stmt|;
block|}
name|this
operator|.
name|docBases
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBases
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|startPointers
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointers
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|avgChunkDocs
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkDocs
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|avgChunkSizes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkSizes
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|docBasesDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBasesDeltas
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|startPointersDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointersDeltas
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
block|}
DECL|method|MemoryChunkFieldsIndexReader
specifier|private
name|MemoryChunkFieldsIndexReader
parameter_list|(
name|MemoryChunkFieldsIndexReader
name|other
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|other
operator|.
name|maxDoc
expr_stmt|;
name|this
operator|.
name|docBases
operator|=
name|other
operator|.
name|docBases
expr_stmt|;
name|this
operator|.
name|startPointers
operator|=
name|other
operator|.
name|startPointers
expr_stmt|;
name|this
operator|.
name|avgChunkDocs
operator|=
name|other
operator|.
name|avgChunkDocs
expr_stmt|;
name|this
operator|.
name|avgChunkSizes
operator|=
name|other
operator|.
name|avgChunkSizes
expr_stmt|;
name|this
operator|.
name|docBasesDeltas
operator|=
name|other
operator|.
name|docBasesDeltas
expr_stmt|;
name|this
operator|.
name|startPointersDeltas
operator|=
name|other
operator|.
name|startPointersDeltas
expr_stmt|;
block|}
DECL|method|block
specifier|private
name|int
name|block
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
operator|,
name|hi
operator|=
name|docBases
operator|.
name|length
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|lo
operator|<=
name|hi
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|midValue
init|=
name|docBases
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|midValue
operator|==
name|docID
condition|)
block|{
return|return
name|mid
return|;
block|}
elseif|else
if|if
condition|(
name|midValue
operator|<
name|docID
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|hi
return|;
block|}
DECL|method|relativeDocBase
specifier|private
name|int
name|relativeDocBase
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|relativeChunk
parameter_list|)
block|{
specifier|final
name|int
name|expected
init|=
name|avgChunkDocs
index|[
name|block
index|]
operator|*
name|relativeChunk
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|moveLowOrderBitToSign
argument_list|(
name|docBasesDeltas
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|relativeChunk
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|expected
operator|+
operator|(
name|int
operator|)
name|delta
return|;
block|}
DECL|method|relativeStartPointer
specifier|private
name|long
name|relativeStartPointer
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|relativeChunk
parameter_list|)
block|{
specifier|final
name|long
name|expected
init|=
name|avgChunkSizes
index|[
name|block
index|]
operator|*
name|relativeChunk
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|moveLowOrderBitToSign
argument_list|(
name|startPointersDeltas
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|relativeChunk
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|expected
operator|+
name|delta
return|;
block|}
DECL|method|relativeChunk
specifier|private
name|int
name|relativeChunk
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|relativeDoc
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
operator|,
name|hi
operator|=
name|docBasesDeltas
index|[
name|block
index|]
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|lo
operator|<=
name|hi
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|midValue
init|=
name|relativeDocBase
argument_list|(
name|block
argument_list|,
name|mid
argument_list|)
decl_stmt|;
if|if
condition|(
name|midValue
operator|==
name|relativeDoc
condition|)
block|{
return|return
name|mid
return|;
block|}
elseif|else
if|if
condition|(
name|midValue
operator|<
name|relativeDoc
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|hi
return|;
block|}
annotation|@
name|Override
DECL|method|getStartPointer
name|long
name|getStartPointer
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docID out of range [0-"
operator|+
name|maxDoc
operator|+
literal|"]: "
operator|+
name|docID
argument_list|)
throw|;
block|}
specifier|final
name|int
name|block
init|=
name|block
argument_list|(
name|docID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|relativeChunk
init|=
name|relativeChunk
argument_list|(
name|block
argument_list|,
name|docID
operator|-
name|docBases
index|[
name|block
index|]
argument_list|)
decl_stmt|;
return|return
name|startPointers
index|[
name|block
index|]
operator|+
name|relativeStartPointer
argument_list|(
name|block
argument_list|,
name|relativeChunk
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Reader
name|clone
parameter_list|()
block|{
if|if
condition|(
name|fieldsIndexIn
operator|==
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|MemoryChunkFieldsIndexReader
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_enum

end_unit

