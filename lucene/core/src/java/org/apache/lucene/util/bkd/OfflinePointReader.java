begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|ChecksumIndexInput
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
name|LongBitSet
import|;
end_import

begin_comment
comment|/** Reads points from disk in a fixed-with format, previously written with {@link OfflinePointWriter}.  *  * @lucene.internal */
end_comment

begin_class
DECL|class|OfflinePointReader
specifier|public
specifier|final
class|class
name|OfflinePointReader
extends|extends
name|PointReader
block|{
DECL|field|countLeft
name|long
name|countLeft
decl_stmt|;
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|packedValue
specifier|private
specifier|final
name|byte
index|[]
name|packedValue
decl_stmt|;
DECL|field|singleValuePerDoc
specifier|final
name|boolean
name|singleValuePerDoc
decl_stmt|;
DECL|field|bytesPerDoc
specifier|final
name|int
name|bytesPerDoc
decl_stmt|;
DECL|field|ord
specifier|private
name|long
name|ord
decl_stmt|;
DECL|field|docID
specifier|private
name|int
name|docID
decl_stmt|;
comment|// true if ords are written as long (8 bytes), else 4 bytes
DECL|field|longOrds
specifier|private
name|boolean
name|longOrds
decl_stmt|;
DECL|field|checked
specifier|private
name|boolean
name|checked
decl_stmt|;
comment|// File name we are reading
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|OfflinePointReader
specifier|public
name|OfflinePointReader
parameter_list|(
name|Directory
name|tempDir
parameter_list|,
name|String
name|tempFileName
parameter_list|,
name|int
name|packedBytesLength
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|longOrds
parameter_list|,
name|boolean
name|singleValuePerDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|singleValuePerDoc
operator|=
name|singleValuePerDoc
expr_stmt|;
name|int
name|bytesPerDoc
init|=
name|packedBytesLength
operator|+
name|Integer
operator|.
name|BYTES
decl_stmt|;
if|if
condition|(
name|singleValuePerDoc
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|longOrds
condition|)
block|{
name|bytesPerDoc
operator|+=
name|Long
operator|.
name|BYTES
expr_stmt|;
block|}
else|else
block|{
name|bytesPerDoc
operator|+=
name|Integer
operator|.
name|BYTES
expr_stmt|;
block|}
block|}
name|this
operator|.
name|bytesPerDoc
operator|=
name|bytesPerDoc
expr_stmt|;
if|if
condition|(
operator|(
name|start
operator|+
name|length
operator|)
operator|*
name|bytesPerDoc
operator|+
name|CodecUtil
operator|.
name|footerLength
argument_list|()
operator|>
name|tempDir
operator|.
name|fileLength
argument_list|(
name|tempFileName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"requested slice is beyond the length of this file: start="
operator|+
name|start
operator|+
literal|" length="
operator|+
name|length
operator|+
literal|" bytesPerDoc="
operator|+
name|bytesPerDoc
operator|+
literal|" fileLength="
operator|+
name|tempDir
operator|.
name|fileLength
argument_list|(
name|tempFileName
argument_list|)
operator|+
literal|" tempFileName="
operator|+
name|tempFileName
argument_list|)
throw|;
block|}
comment|// Best-effort checksumming:
if|if
condition|(
name|start
operator|==
literal|0
operator|&&
name|length
operator|*
name|bytesPerDoc
operator|==
name|tempDir
operator|.
name|fileLength
argument_list|(
name|tempFileName
argument_list|)
operator|-
name|CodecUtil
operator|.
name|footerLength
argument_list|()
condition|)
block|{
comment|// If we are going to read the entire file, e.g. because BKDWriter is now
comment|// partitioning it, we open with checksums:
name|in
operator|=
name|tempDir
operator|.
name|openChecksumInput
argument_list|(
name|tempFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Since we are going to seek somewhere in the middle of a possibly huge
comment|// file, and not read all bytes from there, don't use ChecksumIndexInput here.
comment|// This is typically fine, because this same file will later be read fully,
comment|// at another level of the BKDWriter recursion
name|in
operator|=
name|tempDir
operator|.
name|openInput
argument_list|(
name|tempFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|tempFileName
expr_stmt|;
name|long
name|seekFP
init|=
name|start
operator|*
name|bytesPerDoc
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|seekFP
argument_list|)
expr_stmt|;
name|countLeft
operator|=
name|length
expr_stmt|;
name|packedValue
operator|=
operator|new
name|byte
index|[
name|packedBytesLength
index|]
expr_stmt|;
name|this
operator|.
name|longOrds
operator|=
name|longOrds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|countLeft
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|countLeft
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|countLeft
operator|--
expr_stmt|;
block|}
try|try
block|{
name|in
operator|.
name|readBytes
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|,
name|packedValue
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|eofe
parameter_list|)
block|{
assert|assert
name|countLeft
operator|==
operator|-
literal|1
assert|;
return|return
literal|false
return|;
block|}
name|docID
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|singleValuePerDoc
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|longOrds
condition|)
block|{
name|ord
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|ord
operator|=
name|docID
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|packedValue
specifier|public
name|byte
index|[]
name|packedValue
parameter_list|()
block|{
return|return
name|packedValue
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
name|ord
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
name|docID
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
try|try
block|{
if|if
condition|(
name|countLeft
operator|==
literal|0
operator|&&
name|in
operator|instanceof
name|ChecksumIndexInput
operator|&&
name|checked
operator|==
literal|false
condition|)
block|{
comment|//System.out.println("NOW CHECK: " + name);
name|checked
operator|=
literal|true
expr_stmt|;
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
operator|(
name|ChecksumIndexInput
operator|)
name|in
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|markOrds
specifier|public
name|void
name|markOrds
parameter_list|(
name|long
name|count
parameter_list|,
name|LongBitSet
name|ordBitSet
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|countLeft
operator|<
name|count
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"only "
operator|+
name|countLeft
operator|+
literal|" points remain, but "
operator|+
name|count
operator|+
literal|" were requested"
argument_list|)
throw|;
block|}
name|long
name|fp
init|=
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
name|packedValue
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|singleValuePerDoc
operator|==
literal|false
condition|)
block|{
name|fp
operator|+=
name|Integer
operator|.
name|BYTES
expr_stmt|;
block|}
for|for
control|(
name|long
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
name|in
operator|.
name|seek
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
if|if
condition|(
name|longOrds
condition|)
block|{
name|ord
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
assert|assert
name|ordBitSet
operator|.
name|get
argument_list|(
name|ord
argument_list|)
operator|==
literal|false
operator|:
literal|"ord="
operator|+
name|ord
operator|+
literal|" i="
operator|+
name|i
operator|+
literal|" was seen twice from "
operator|+
name|this
assert|;
name|ordBitSet
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|fp
operator|+=
name|bytesPerDoc
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|split
specifier|public
name|long
name|split
parameter_list|(
name|long
name|count
parameter_list|,
name|LongBitSet
name|rightTree
parameter_list|,
name|PointWriter
name|left
parameter_list|,
name|PointWriter
name|right
parameter_list|,
name|boolean
name|doClearBits
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|left
operator|instanceof
name|OfflinePointWriter
operator|==
literal|false
operator|||
name|right
operator|instanceof
name|OfflinePointWriter
operator|==
literal|false
condition|)
block|{
return|return
name|super
operator|.
name|split
argument_list|(
name|count
argument_list|,
name|rightTree
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|doClearBits
argument_list|)
return|;
block|}
comment|// We specialize the offline -> offline split since the default impl
comment|// is somewhat wasteful otherwise (e.g. decoding docID when we don't
comment|// need to)
name|int
name|packedBytesLength
init|=
name|packedValue
operator|.
name|length
decl_stmt|;
name|int
name|bytesPerDoc
init|=
name|packedBytesLength
operator|+
name|Integer
operator|.
name|BYTES
decl_stmt|;
if|if
condition|(
name|singleValuePerDoc
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|longOrds
condition|)
block|{
name|bytesPerDoc
operator|+=
name|Long
operator|.
name|BYTES
expr_stmt|;
block|}
else|else
block|{
name|bytesPerDoc
operator|+=
name|Integer
operator|.
name|BYTES
expr_stmt|;
block|}
block|}
name|long
name|rightCount
init|=
literal|0
decl_stmt|;
name|IndexOutput
name|rightOut
init|=
operator|(
operator|(
name|OfflinePointWriter
operator|)
name|right
operator|)
operator|.
name|out
decl_stmt|;
name|IndexOutput
name|leftOut
init|=
operator|(
operator|(
name|OfflinePointWriter
operator|)
name|left
operator|)
operator|.
name|out
decl_stmt|;
assert|assert
name|count
operator|<=
name|countLeft
operator|:
literal|"count="
operator|+
name|count
operator|+
literal|" countLeft="
operator|+
name|countLeft
assert|;
name|countLeft
operator|-=
name|count
expr_stmt|;
name|long
name|countStart
init|=
name|count
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|bytesPerDoc
index|]
decl_stmt|;
while|while
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
if|if
condition|(
name|longOrds
condition|)
block|{
comment|// A long ord, after the docID:
name|ord
operator|=
name|readLong
argument_list|(
name|buffer
argument_list|,
name|packedBytesLength
operator|+
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|singleValuePerDoc
condition|)
block|{
comment|// docID is the ord:
name|ord
operator|=
name|readInt
argument_list|(
name|buffer
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// An int ord, after the docID:
name|ord
operator|=
name|readInt
argument_list|(
name|buffer
argument_list|,
name|packedBytesLength
operator|+
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rightTree
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
name|rightOut
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesPerDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|doClearBits
condition|)
block|{
name|rightTree
operator|.
name|clear
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
name|rightCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|leftOut
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesPerDoc
argument_list|)
expr_stmt|;
block|}
name|count
operator|--
expr_stmt|;
block|}
operator|(
operator|(
name|OfflinePointWriter
operator|)
name|right
operator|)
operator|.
name|count
operator|=
name|rightCount
expr_stmt|;
operator|(
operator|(
name|OfflinePointWriter
operator|)
name|left
operator|)
operator|.
name|count
operator|=
name|countStart
operator|-
name|rightCount
expr_stmt|;
return|return
name|rightCount
return|;
block|}
comment|// Poached from ByteArrayDataInput:
DECL|method|readLong
specifier|private
specifier|static
name|long
name|readLong
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
specifier|final
name|int
name|i1
init|=
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
specifier|final
name|int
name|i2
init|=
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|i1
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|i2
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
comment|// Poached from ByteArrayDataInput:
DECL|method|readInt
specifier|private
specifier|static
name|int
name|readInt
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
block|}
end_class

end_unit

