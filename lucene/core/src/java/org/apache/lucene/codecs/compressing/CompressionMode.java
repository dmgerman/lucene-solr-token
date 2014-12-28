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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DataFormatException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Deflater
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Inflater
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
name|store
operator|.
name|DataInput
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A compression mode. Tells how much effort should be spent on compression and  * decompression of stored fields.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompressionMode
specifier|public
specifier|abstract
class|class
name|CompressionMode
block|{
comment|/**    * A compression mode that trades compression ratio for speed. Although the    * compression ratio might remain high, compression and decompression are    * very fast. Use this mode with indices that have a high update rate but    * should be able to load documents from disk quickly.    */
DECL|field|FAST
specifier|public
specifier|static
specifier|final
name|CompressionMode
name|FAST
init|=
operator|new
name|CompressionMode
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Compressor
name|newCompressor
parameter_list|()
block|{
return|return
operator|new
name|LZ4FastCompressor
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Decompressor
name|newDecompressor
parameter_list|()
block|{
return|return
name|LZ4_DECOMPRESSOR
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FAST"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A compression mode that trades speed for compression ratio. Although    * compression and decompression might be slow, this compression mode should    * provide a good compression ratio. This mode might be interesting if/when    * your index size is much bigger than your OS cache.    */
DECL|field|HIGH_COMPRESSION
specifier|public
specifier|static
specifier|final
name|CompressionMode
name|HIGH_COMPRESSION
init|=
operator|new
name|CompressionMode
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Compressor
name|newCompressor
parameter_list|()
block|{
comment|// 3 is the highest level that doesn't have lazy match evaluation
return|return
operator|new
name|DeflateCompressor
argument_list|(
literal|3
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Decompressor
name|newDecompressor
parameter_list|()
block|{
return|return
operator|new
name|DeflateDecompressor
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HIGH_COMPRESSION"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * This compression mode is similar to {@link #FAST} but it spends more time    * compressing in order to improve the compression ratio. This compression    * mode is best used with indices that have a low update rate but should be    * able to load documents from disk quickly.    */
DECL|field|FAST_DECOMPRESSION
specifier|public
specifier|static
specifier|final
name|CompressionMode
name|FAST_DECOMPRESSION
init|=
operator|new
name|CompressionMode
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Compressor
name|newCompressor
parameter_list|()
block|{
return|return
operator|new
name|LZ4HighCompressor
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Decompressor
name|newDecompressor
parameter_list|()
block|{
return|return
name|LZ4_DECOMPRESSOR
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FAST_DECOMPRESSION"
return|;
block|}
block|}
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|CompressionMode
specifier|protected
name|CompressionMode
parameter_list|()
block|{}
comment|/**    * Create a new {@link Compressor} instance.    */
DECL|method|newCompressor
specifier|public
specifier|abstract
name|Compressor
name|newCompressor
parameter_list|()
function_decl|;
comment|/**    * Create a new {@link Decompressor} instance.    */
DECL|method|newDecompressor
specifier|public
specifier|abstract
name|Decompressor
name|newDecompressor
parameter_list|()
function_decl|;
DECL|field|LZ4_DECOMPRESSOR
specifier|private
specifier|static
specifier|final
name|Decompressor
name|LZ4_DECOMPRESSOR
init|=
operator|new
name|Decompressor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|decompress
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|originalLength
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|offset
operator|+
name|length
operator|<=
name|originalLength
assert|;
comment|// add 7 padding bytes, this is not necessary but can help decompression run faster
if|if
condition|(
name|bytes
operator|.
name|bytes
operator|.
name|length
operator|<
name|originalLength
operator|+
literal|7
condition|)
block|{
name|bytes
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|originalLength
operator|+
literal|7
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
specifier|final
name|int
name|decompressedLength
init|=
name|LZ4
operator|.
name|decompress
argument_list|(
name|in
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|bytes
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|decompressedLength
operator|>
name|originalLength
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted: lengths mismatch: "
operator|+
name|decompressedLength
operator|+
literal|"> "
operator|+
name|originalLength
argument_list|,
name|in
argument_list|)
throw|;
block|}
name|bytes
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Decompressor
name|clone
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
decl_stmt|;
DECL|class|LZ4FastCompressor
specifier|private
specifier|static
specifier|final
class|class
name|LZ4FastCompressor
extends|extends
name|Compressor
block|{
DECL|field|ht
specifier|private
specifier|final
name|LZ4
operator|.
name|HashTable
name|ht
decl_stmt|;
DECL|method|LZ4FastCompressor
name|LZ4FastCompressor
parameter_list|()
block|{
name|ht
operator|=
operator|new
name|LZ4
operator|.
name|HashTable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compress
specifier|public
name|void
name|compress
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|LZ4
operator|.
name|compress
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|out
argument_list|,
name|ht
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LZ4HighCompressor
specifier|private
specifier|static
specifier|final
class|class
name|LZ4HighCompressor
extends|extends
name|Compressor
block|{
DECL|field|ht
specifier|private
specifier|final
name|LZ4
operator|.
name|HCHashTable
name|ht
decl_stmt|;
DECL|method|LZ4HighCompressor
name|LZ4HighCompressor
parameter_list|()
block|{
name|ht
operator|=
operator|new
name|LZ4
operator|.
name|HCHashTable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compress
specifier|public
name|void
name|compress
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|LZ4
operator|.
name|compressHC
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|out
argument_list|,
name|ht
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DeflateDecompressor
specifier|private
specifier|static
specifier|final
class|class
name|DeflateDecompressor
extends|extends
name|Decompressor
block|{
DECL|field|decompressor
specifier|final
name|Inflater
name|decompressor
decl_stmt|;
DECL|field|compressed
name|byte
index|[]
name|compressed
decl_stmt|;
DECL|method|DeflateDecompressor
name|DeflateDecompressor
parameter_list|()
block|{
name|decompressor
operator|=
operator|new
name|Inflater
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|compressed
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decompress
specifier|public
name|void
name|decompress
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|originalLength
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|offset
operator|+
name|length
operator|<=
name|originalLength
assert|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
name|bytes
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|compressedLength
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|// pad with extra "dummy byte": see javadocs for using Inflater(true)
comment|// we do it for compliance, but it's unnecessary for years in zlib.
specifier|final
name|int
name|paddedLength
init|=
name|compressedLength
operator|+
literal|1
decl_stmt|;
name|compressed
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|compressed
argument_list|,
name|paddedLength
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|compressed
argument_list|,
literal|0
argument_list|,
name|compressedLength
argument_list|)
expr_stmt|;
name|compressed
index|[
name|compressedLength
index|]
operator|=
literal|0
expr_stmt|;
comment|// explicitly set dummy byte to 0
name|decompressor
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// extra "dummy byte"
name|decompressor
operator|.
name|setInput
argument_list|(
name|compressed
argument_list|,
literal|0
argument_list|,
name|paddedLength
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|offset
operator|=
name|bytes
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|bytes
operator|.
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|originalLength
argument_list|)
expr_stmt|;
try|try
block|{
name|bytes
operator|.
name|length
operator|=
name|decompressor
operator|.
name|inflate
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|originalLength
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|decompressor
operator|.
name|finished
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid decoder state: needsInput="
operator|+
name|decompressor
operator|.
name|needsInput
argument_list|()
operator|+
literal|", needsDict="
operator|+
name|decompressor
operator|.
name|needsDictionary
argument_list|()
argument_list|,
name|in
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|originalLength
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Lengths mismatch: "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" != "
operator|+
name|originalLength
argument_list|,
name|in
argument_list|)
throw|;
block|}
name|bytes
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Decompressor
name|clone
parameter_list|()
block|{
return|return
operator|new
name|DeflateDecompressor
argument_list|()
return|;
block|}
block|}
DECL|class|DeflateCompressor
specifier|private
specifier|static
class|class
name|DeflateCompressor
extends|extends
name|Compressor
block|{
DECL|field|compressor
specifier|final
name|Deflater
name|compressor
decl_stmt|;
DECL|field|compressed
name|byte
index|[]
name|compressed
decl_stmt|;
DECL|method|DeflateCompressor
name|DeflateCompressor
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|compressor
operator|=
operator|new
name|Deflater
argument_list|(
name|level
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|compressed
operator|=
operator|new
name|byte
index|[
literal|64
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compress
specifier|public
name|void
name|compress
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|compressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|finish
argument_list|()
expr_stmt|;
if|if
condition|(
name|compressor
operator|.
name|needsInput
argument_list|()
condition|)
block|{
comment|// no output
assert|assert
name|len
operator|==
literal|0
operator|:
name|len
assert|;
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|totalCount
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
name|count
init|=
name|compressor
operator|.
name|deflate
argument_list|(
name|compressed
argument_list|,
name|totalCount
argument_list|,
name|compressed
operator|.
name|length
operator|-
name|totalCount
argument_list|)
decl_stmt|;
name|totalCount
operator|+=
name|count
expr_stmt|;
assert|assert
name|totalCount
operator|<=
name|compressed
operator|.
name|length
assert|;
if|if
condition|(
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
break|break;
block|}
else|else
block|{
name|compressed
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|totalCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|compressed
argument_list|,
name|totalCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

