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
name|packed
operator|.
name|GrowableWriter
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
comment|/**    * This format stores the document index on disk using 64-bits pointers to    * the start offsets of chunks in the fields data file.    *<p>    * This format has no memory overhead and requires at most 1 disk seek to    * locate a document in the fields data file. Use this format in    * memory-constrained environments.    */
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
comment|/**    * For every document in the segment, this format stores the offset of the    * compressed chunk that contains it in the fields data file.    *<p>    * This fields index format requires at most<code>8 * numDocs</code> bytes    * of memory. Locating a document in the fields data file requires no disk    * seek. Use this format when blocks are very likely to contain few    * documents (in particular when<code>chunkSize = 1</code>).    */
DECL|enum constant|MEMORY_DOC
name|MEMORY_DOC
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
name|ChunksFieldsIndexWriter
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
name|MemoryDocFieldsIndexReader
argument_list|(
name|in
argument_list|,
name|si
argument_list|)
return|;
block|}
block|}
block|,
comment|/**    * For every chunk of compressed documents, this format stores the first doc    * ID of the chunk as well as the start offset of the chunk.    *<p>    * This fields index format require at most    *<code>12 * numChunks</code> bytes of memory. Locating a document in the    * fields data file requires no disk seek. Use this format when chunks are    * likely to contain several documents.    */
DECL|enum constant|MEMORY_CHUNK
name|MEMORY_CHUNK
argument_list|(
literal|2
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
name|ChunksFieldsIndexWriter
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
DECL|class|ChunksFieldsIndexWriter
specifier|private
specifier|static
class|class
name|ChunksFieldsIndexWriter
extends|extends
name|Writer
block|{
DECL|field|numChunks
name|int
name|numChunks
decl_stmt|;
DECL|field|maxStartPointer
name|long
name|maxStartPointer
decl_stmt|;
DECL|field|docBaseDeltas
name|GrowableWriter
name|docBaseDeltas
decl_stmt|;
DECL|field|startPointerDeltas
name|GrowableWriter
name|startPointerDeltas
decl_stmt|;
DECL|method|ChunksFieldsIndexWriter
name|ChunksFieldsIndexWriter
parameter_list|(
name|IndexOutput
name|indexOutput
parameter_list|)
block|{
name|super
argument_list|(
name|indexOutput
argument_list|)
expr_stmt|;
name|numChunks
operator|=
literal|0
expr_stmt|;
name|maxStartPointer
operator|=
literal|0
expr_stmt|;
name|docBaseDeltas
operator|=
operator|new
name|GrowableWriter
argument_list|(
literal|2
argument_list|,
literal|128
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|startPointerDeltas
operator|=
operator|new
name|GrowableWriter
argument_list|(
literal|5
argument_list|,
literal|128
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
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
name|numChunks
operator|==
name|docBaseDeltas
operator|.
name|size
argument_list|()
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
name|numChunks
operator|+
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|docBaseDeltas
operator|=
name|docBaseDeltas
operator|.
name|resize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
name|startPointerDeltas
operator|=
name|startPointerDeltas
operator|.
name|resize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
name|docBaseDeltas
operator|.
name|set
argument_list|(
name|numChunks
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|startPointerDeltas
operator|.
name|set
argument_list|(
name|numChunks
argument_list|,
name|startPointer
operator|-
name|maxStartPointer
argument_list|)
expr_stmt|;
operator|++
name|numChunks
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
name|numChunks
operator|!=
name|docBaseDeltas
operator|.
name|size
argument_list|()
condition|)
block|{
name|docBaseDeltas
operator|=
name|docBaseDeltas
operator|.
name|resize
argument_list|(
name|numChunks
argument_list|)
expr_stmt|;
name|startPointerDeltas
operator|=
name|startPointerDeltas
operator|.
name|resize
argument_list|(
name|numChunks
argument_list|)
expr_stmt|;
block|}
name|fieldsIndexOut
operator|.
name|writeVInt
argument_list|(
name|numChunks
argument_list|)
expr_stmt|;
name|fieldsIndexOut
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxStartPointer
argument_list|)
argument_list|)
expr_stmt|;
name|docBaseDeltas
operator|.
name|save
argument_list|(
name|fieldsIndexOut
argument_list|)
expr_stmt|;
name|startPointerDeltas
operator|.
name|save
argument_list|(
name|fieldsIndexOut
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|fieldsIndexIn
operator|!=
literal|null
condition|)
block|{
name|fieldsIndexIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
DECL|class|MemoryDocFieldsIndexReader
specifier|private
specifier|static
class|class
name|MemoryDocFieldsIndexReader
extends|extends
name|Reader
block|{
DECL|field|startPointers
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|startPointers
decl_stmt|;
DECL|method|MemoryDocFieldsIndexReader
name|MemoryDocFieldsIndexReader
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
specifier|final
name|int
name|numChunks
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bitsPerStartPointer
init|=
name|fieldsIndexIn
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
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
specifier|final
name|PackedInts
operator|.
name|Reader
name|chunkDocs
init|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|fieldsIndexIn
argument_list|)
decl_stmt|;
if|if
condition|(
name|chunkDocs
operator|.
name|size
argument_list|()
operator|!=
name|numChunks
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Expected "
operator|+
name|numChunks
operator|+
literal|" chunks, but got "
operator|+
name|chunkDocs
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|startPointerDeltas
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|startPointerDeltas
operator|.
name|size
argument_list|()
operator|!=
name|numChunks
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Expected "
operator|+
name|numChunks
operator|+
literal|" chunks, but got "
operator|+
name|startPointerDeltas
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|PackedInts
operator|.
name|Mutable
name|startPointers
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|bitsPerStartPointer
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|int
name|docID
init|=
literal|0
decl_stmt|;
name|long
name|startPointer
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
name|numChunks
condition|;
operator|++
name|i
control|)
block|{
name|startPointer
operator|+=
name|startPointerDeltas
operator|.
name|next
argument_list|()
expr_stmt|;
specifier|final
name|int
name|chunkDocCount
init|=
operator|(
name|int
operator|)
name|chunkDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|chunkDocCount
condition|;
operator|++
name|j
control|)
block|{
name|startPointers
operator|.
name|set
argument_list|(
name|docID
operator|++
argument_list|,
name|startPointer
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docID
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
literal|"Expected "
operator|+
name|si
operator|.
name|getDocCount
argument_list|()
operator|+
literal|" docs, got "
operator|+
name|docID
argument_list|)
throw|;
block|}
name|this
operator|.
name|startPointers
operator|=
name|startPointers
expr_stmt|;
block|}
DECL|method|MemoryDocFieldsIndexReader
specifier|private
name|MemoryDocFieldsIndexReader
parameter_list|(
name|PackedInts
operator|.
name|Reader
name|startPointers
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|startPointers
operator|=
name|startPointers
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
return|return
name|startPointers
operator|.
name|get
argument_list|(
name|docID
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
name|MemoryDocFieldsIndexReader
argument_list|(
name|startPointers
argument_list|)
return|;
block|}
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
DECL|field|docBases
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|docBases
decl_stmt|;
DECL|field|startPointers
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|startPointers
decl_stmt|;
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
specifier|final
name|int
name|numChunks
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bitsPerStartPointer
init|=
name|fieldsIndexIn
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
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
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|docBaseDeltas
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|docBaseDeltas
operator|.
name|size
argument_list|()
operator|!=
name|numChunks
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Expected "
operator|+
name|numChunks
operator|+
literal|" chunks, but got "
operator|+
name|docBaseDeltas
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|PackedInts
operator|.
name|Mutable
name|docBases
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|numChunks
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|si
operator|.
name|getDocCount
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|int
name|docBase
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
name|numChunks
condition|;
operator|++
name|i
control|)
block|{
name|docBases
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
name|docBase
operator|+=
name|docBaseDeltas
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docBase
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
literal|"Expected "
operator|+
name|si
operator|.
name|getDocCount
argument_list|()
operator|+
literal|" docs, got "
operator|+
name|docBase
argument_list|)
throw|;
block|}
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|startPointerDeltas
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|startPointerDeltas
operator|.
name|size
argument_list|()
operator|!=
name|numChunks
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Expected "
operator|+
name|numChunks
operator|+
literal|" chunks, but got "
operator|+
name|startPointerDeltas
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|PackedInts
operator|.
name|Mutable
name|startPointers
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|numChunks
argument_list|,
name|bitsPerStartPointer
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|long
name|startPointer
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
name|numChunks
condition|;
operator|++
name|i
control|)
block|{
name|startPointer
operator|+=
name|startPointerDeltas
operator|.
name|next
argument_list|()
expr_stmt|;
name|startPointers
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|startPointer
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|docBases
operator|=
name|docBases
expr_stmt|;
name|this
operator|.
name|startPointers
operator|=
name|startPointers
expr_stmt|;
block|}
DECL|method|MemoryChunkFieldsIndexReader
specifier|private
name|MemoryChunkFieldsIndexReader
parameter_list|(
name|PackedInts
operator|.
name|Reader
name|docBases
parameter_list|,
name|PackedInts
operator|.
name|Reader
name|startPointers
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|docBases
operator|=
name|docBases
expr_stmt|;
name|this
operator|.
name|startPointers
operator|=
name|startPointers
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
block|{
assert|assert
name|docBases
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
name|int
name|lo
init|=
literal|0
operator|,
name|hi
operator|=
name|docBases
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
name|long
name|midValue
init|=
name|docBases
operator|.
name|get
argument_list|(
name|mid
argument_list|)
decl_stmt|;
if|if
condition|(
name|midValue
operator|==
name|docID
condition|)
block|{
return|return
name|startPointers
operator|.
name|get
argument_list|(
name|mid
argument_list|)
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
name|startPointers
operator|.
name|get
argument_list|(
name|hi
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
name|docBases
argument_list|,
name|startPointers
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_enum

end_unit

