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
name|store
operator|.
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import

begin_class
DECL|class|AbstractTestCompressionMode
specifier|public
specifier|abstract
class|class
name|AbstractTestCompressionMode
extends|extends
name|LuceneTestCase
block|{
DECL|field|mode
name|CompressionMode
name|mode
decl_stmt|;
DECL|method|randomArray
specifier|static
name|byte
index|[]
name|randomArray
parameter_list|()
block|{
specifier|final
name|int
name|max
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|192
operator|*
literal|1024
argument_list|)
decl_stmt|;
return|return
name|randomArray
argument_list|(
name|length
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|randomArray
specifier|static
name|byte
index|[]
name|randomArray
parameter_list|(
name|int
name|length
parameter_list|,
name|int
name|max
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|arr
init|=
operator|new
name|byte
index|[
name|length
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
name|arr
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
DECL|method|compress
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|decompressed
parameter_list|)
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|mode
operator|.
name|newCompressor
argument_list|()
decl_stmt|;
return|return
name|compress
argument_list|(
name|compressor
argument_list|,
name|decompressed
argument_list|)
return|;
block|}
DECL|method|compress
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
name|Compressor
name|compressor
parameter_list|,
name|byte
index|[]
name|decompressed
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|compressed
init|=
operator|new
name|byte
index|[
name|decompressed
operator|.
name|length
operator|*
literal|2
operator|+
literal|16
index|]
decl_stmt|;
comment|// should be enough
name|ByteArrayDataOutput
name|out
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|compressed
argument_list|)
decl_stmt|;
name|compressor
operator|.
name|compress
argument_list|(
name|decompressed
argument_list|,
literal|0
argument_list|,
name|decompressed
operator|.
name|length
argument_list|,
name|out
argument_list|)
expr_stmt|;
specifier|final
name|int
name|compressedLen
init|=
name|out
operator|.
name|getPosition
argument_list|()
decl_stmt|;
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|compressed
argument_list|,
name|compressedLen
argument_list|)
return|;
block|}
DECL|method|decompress
name|byte
index|[]
name|decompress
parameter_list|(
name|byte
index|[]
name|compressed
parameter_list|,
name|int
name|originalLength
parameter_list|)
throws|throws
name|IOException
block|{
name|Decompressor
name|decompressor
init|=
name|mode
operator|.
name|newDecompressor
argument_list|()
decl_stmt|;
return|return
name|decompress
argument_list|(
name|decompressor
argument_list|,
name|compressed
argument_list|,
name|originalLength
argument_list|)
return|;
block|}
DECL|method|decompress
specifier|static
name|byte
index|[]
name|decompress
parameter_list|(
name|Decompressor
name|decompressor
parameter_list|,
name|byte
index|[]
name|compressed
parameter_list|,
name|int
name|originalLength
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
operator|new
name|ByteArrayDataInput
argument_list|(
name|compressed
argument_list|)
argument_list|,
name|originalLength
argument_list|,
literal|0
argument_list|,
name|originalLength
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|copyOfRange
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
name|offset
operator|+
name|bytes
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|decompress
name|byte
index|[]
name|decompress
parameter_list|(
name|byte
index|[]
name|compressed
parameter_list|,
name|int
name|originalLength
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|Decompressor
name|decompressor
init|=
name|mode
operator|.
name|newDecompressor
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
operator|new
name|ByteArrayDataInput
argument_list|(
name|compressed
argument_list|)
argument_list|,
name|originalLength
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|copyOfRange
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
name|offset
operator|+
name|bytes
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|copyCompressedData
specifier|static
name|byte
index|[]
name|copyCompressedData
parameter_list|(
name|Decompressor
name|decompressor
parameter_list|,
name|byte
index|[]
name|compressed
parameter_list|,
name|int
name|originalLength
parameter_list|)
throws|throws
name|IOException
block|{
name|GrowableByteArrayDataOutput
name|out
init|=
operator|new
name|GrowableByteArrayDataOutput
argument_list|(
name|compressed
operator|.
name|length
argument_list|)
decl_stmt|;
name|decompressor
operator|.
name|copyCompressedData
argument_list|(
operator|new
name|ByteArrayDataInput
argument_list|(
name|compressed
argument_list|)
argument_list|,
name|originalLength
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|out
operator|.
name|bytes
argument_list|,
name|out
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|copyCompressedData
name|byte
index|[]
name|copyCompressedData
parameter_list|(
name|byte
index|[]
name|compressed
parameter_list|,
name|int
name|originalLength
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|copyCompressedData
argument_list|(
name|mode
operator|.
name|newDecompressor
argument_list|()
argument_list|,
name|compressed
argument_list|,
name|originalLength
argument_list|)
return|;
block|}
DECL|method|testDecompress
specifier|public
name|void
name|testDecompress
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|decompressed
init|=
name|randomArray
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|compressed
init|=
name|compress
argument_list|(
name|decompressed
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|restored
init|=
name|decompress
argument_list|(
name|compressed
argument_list|,
name|decompressed
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|decompressed
argument_list|,
name|restored
argument_list|)
expr_stmt|;
block|}
DECL|method|testPartialDecompress
specifier|public
name|void
name|testPartialDecompress
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|10
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|byte
index|[]
name|decompressed
init|=
name|randomArray
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|compressed
init|=
name|compress
argument_list|(
name|decompressed
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
decl_stmt|,
name|length
decl_stmt|;
if|if
condition|(
name|decompressed
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|offset
operator|=
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|decompressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|decompressed
operator|.
name|length
operator|-
name|offset
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|restored
init|=
name|decompress
argument_list|(
name|compressed
argument_list|,
name|decompressed
operator|.
name|length
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|decompressed
argument_list|,
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|)
argument_list|,
name|restored
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCopyCompressedData
specifier|public
name|void
name|testCopyCompressedData
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|decompressed
init|=
name|randomArray
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|compressed
init|=
name|compress
argument_list|(
name|decompressed
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|compressed
argument_list|,
name|copyCompressedData
argument_list|(
name|compressed
argument_list|,
name|decompressed
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|(
name|byte
index|[]
name|decompressed
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|compressed
init|=
name|compress
argument_list|(
name|decompressed
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|restored
init|=
name|decompress
argument_list|(
name|compressed
argument_list|,
name|decompressed
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|decompressed
operator|.
name|length
argument_list|,
name|restored
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|compressed
argument_list|,
name|copyCompressedData
argument_list|(
name|compressed
argument_list|,
name|decompressed
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptySequence
specifier|public
name|void
name|testEmptySequence
parameter_list|()
throws|throws
name|IOException
block|{
name|test
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testShortSequence
specifier|public
name|void
name|testShortSequence
parameter_list|()
throws|throws
name|IOException
block|{
name|test
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncompressible
specifier|public
name|void
name|testIncompressible
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|decompressed
init|=
operator|new
name|byte
index|[
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|,
literal|256
argument_list|)
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
name|decompressed
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|decompressed
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
comment|// for LZ compression
DECL|method|testShortLiteralsAndMatchs
specifier|public
name|void
name|testShortLiteralsAndMatchs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// literals and matchs lengths<= 15
specifier|final
name|byte
index|[]
name|decompressed
init|=
literal|"1234562345673456745678910123"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongMatchs
specifier|public
name|void
name|testLongMatchs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// match length> 16
specifier|final
name|byte
index|[]
name|decompressed
init|=
operator|new
name|byte
index|[
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|300
argument_list|,
literal|1024
argument_list|)
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
name|decompressed
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|decompressed
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongLiterals
specifier|public
name|void
name|testLongLiterals
parameter_list|()
throws|throws
name|IOException
block|{
comment|// long literals (length> 16) which are not the last literals
specifier|final
name|byte
index|[]
name|decompressed
init|=
name|randomArray
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|400
argument_list|,
literal|1024
argument_list|)
argument_list|,
literal|256
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchRef
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchOff
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|decompressed
operator|.
name|length
operator|-
literal|40
argument_list|,
name|decompressed
operator|.
name|length
operator|-
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|matchLength
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|decompressed
argument_list|,
name|matchRef
argument_list|,
name|decompressed
argument_list|,
name|matchOff
argument_list|,
name|matchLength
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|decompressed
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

