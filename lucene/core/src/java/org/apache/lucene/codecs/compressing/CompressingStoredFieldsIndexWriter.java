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
name|codecs
operator|.
name|Codec
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
name|CodecUtil
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
name|DataOutput
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * Efficient index format for block-based {@link Codec}s.  *<p> This writer generates a file which can be loaded into memory using  * memory-efficient data structures to quickly locate the block that contains  * any document.  *<p>In order to have a compact in-memory representation, for every block of  * 1024 chunks, this index computes the average number of bytes per  * chunk and for every chunk, only stores the difference between<ul>  *<li>${chunk number} * ${average length of a chunk}</li>  *<li>and the actual start offset of the chunk</li></ul></p>  *<p>Data is written as follows:</p>  *<ul>  *<li>PackedIntsVersion,&lt;Block&gt;<sup>BlockCount</sup>, BlocksEndMarker</li>  *<li>PackedIntsVersion --&gt; {@link PackedInts#VERSION_CURRENT} as a {@link DataOutput#writeVInt VInt}</li>  *<li>BlocksEndMarker --&gt;<tt>0</tt> as a {@link DataOutput#writeVInt VInt}, this marks the end of blocks since blocks are not allowed to start with<tt>0</tt></li>  *<li>Block --&gt; BlockChunks,&lt;DocBases&gt;,&lt;StartPointers&gt;</li>  *<li>BlockChunks --&gt; a {@link DataOutput#writeVInt VInt} which is the number of chunks encoded in the block</li>  *<li>DocBases --&gt; DocBase, AvgChunkDocs, BitsPerDocBaseDelta, DocBaseDeltas</li>  *<li>DocBase --&gt; first document ID of the block of chunks, as a {@link DataOutput#writeVInt VInt}</li>  *<li>AvgChunkDocs --&gt; average number of documents in a single chunk, as a {@link DataOutput#writeVInt VInt}</li>  *<li>BitsPerDocBaseDelta --&gt; number of bits required to represent a delta from the average using<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">ZigZag encoding</a></li>  *<li>DocBaseDeltas --&gt; {@link PackedInts packed} array of BlockChunks elements of BitsPerDocBaseDelta bits each, representing the deltas from the average doc base using<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">ZigZag encoding</a>.</li>  *<li>StartPointers --&gt; StartPointerBase, AvgChunkSize, BitsPerStartPointerDelta, StartPointerDeltas</li>  *<li>StartPointerBase --&gt; the first start pointer of the block, as a {@link DataOutput#writeVLong VLong}</li>  *<li>AvgChunkSize --&gt; the average size of a chunk of compressed documents, as a {@link DataOutput#writeVLong VLong}</li>  *<li>BitsPerStartPointerDelta --&gt; number of bits required to represent a delta from the average using<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">ZigZag encoding</a></li>  *<li>StartPointerDeltas --&gt; {@link PackedInts packed} array of BlockChunks elements of BitsPerStartPointerDelta bits each, representing the deltas from the average start pointer using<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">ZigZag encoding</a></li>  *</ul>  *<p>Notes</p>  *<ul>  *<li>For any block, the doc base of the n-th chunk can be restored with  *<code>DocBase + AvgChunkDocs * n + DocBaseDeltas[n]</code>.</li>  *<li>For any block, the start pointer of the n-th chunk can be restored with  *<code>StartPointerBase + AvgChunkSize * n + StartPointerDeltas[n]</code>.</li>  *<li>Once data is loaded into memory, you can lookup the start pointer of any  * document by performing two binary searches: a first one based on the values  * of DocBase in order to find the right block, and then inside the block based  * on DocBaseDeltas (by reconstructing the doc bases for every chunk).</li>  *</ul>  * @lucene.internal  */
end_comment

begin_class
DECL|class|CompressingStoredFieldsIndexWriter
specifier|public
specifier|final
class|class
name|CompressingStoredFieldsIndexWriter
implements|implements
name|Closeable
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
DECL|field|fieldsIndexOut
specifier|final
name|IndexOutput
name|fieldsIndexOut
decl_stmt|;
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
DECL|method|CompressingStoredFieldsIndexWriter
name|CompressingStoredFieldsIndexWriter
parameter_list|(
name|IndexOutput
name|indexOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldsIndexOut
operator|=
name|indexOutput
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
end_class

end_unit

