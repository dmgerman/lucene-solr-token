begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|io
operator|.
name|ByteArrayOutputStream
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/** Simple utility class providing static methods to  *  compress and decompress binary data for stored fields.  *  This class uses java.util.zip.Deflater and Inflater  *  classes to compress and decompress.  */
end_comment

begin_class
DECL|class|CompressionTools
specifier|public
class|class
name|CompressionTools
block|{
comment|// Export only static methods
DECL|method|CompressionTools
specifier|private
name|CompressionTools
parameter_list|()
block|{}
comment|/** Compresses the specified byte range using the    *  specified compressionLevel (constants are defined in    *  java.util.zip.Deflater). */
DECL|method|compress
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|compressionLevel
parameter_list|)
block|{
comment|/* Create an expandable byte array to hold the compressed data.      * You cannot use an array that's the same size as the orginal because      * there is no guarantee that the compressed data will be smaller than      * the uncompressed data. */
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|Deflater
name|compressor
init|=
operator|new
name|Deflater
argument_list|()
decl_stmt|;
try|try
block|{
name|compressor
operator|.
name|setLevel
argument_list|(
name|compressionLevel
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|setInput
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|// Compress the data
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|int
name|count
init|=
name|compressor
operator|.
name|deflate
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|compressor
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/** Compresses the specified byte range, with default BEST_COMPRESSION level */
DECL|method|compress
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|compress
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|Deflater
operator|.
name|BEST_COMPRESSION
argument_list|)
return|;
block|}
comment|/** Compresses all bytes in the array, with default BEST_COMPRESSION level */
DECL|method|compress
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
name|compress
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|,
name|Deflater
operator|.
name|BEST_COMPRESSION
argument_list|)
return|;
block|}
comment|/** Compresses the String value, with default BEST_COMPRESSION level */
DECL|method|compressString
specifier|public
specifier|static
name|byte
index|[]
name|compressString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|compressString
argument_list|(
name|value
argument_list|,
name|Deflater
operator|.
name|BEST_COMPRESSION
argument_list|)
return|;
block|}
comment|/** Compresses the String value using the specified    *  compressionLevel (constants are defined in    *  java.util.zip.Deflater). */
DECL|method|compressString
specifier|public
specifier|static
name|byte
index|[]
name|compressString
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|compressionLevel
parameter_list|)
block|{
name|BytesRef
name|result
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|compress
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|,
name|compressionLevel
argument_list|)
return|;
block|}
comment|/** Decompress the byte array previously returned by    *  compress */
DECL|method|decompress
specifier|public
specifier|static
name|byte
index|[]
name|decompress
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|DataFormatException
block|{
comment|// Create an expandable byte array to hold the decompressed data
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|value
operator|.
name|length
argument_list|)
decl_stmt|;
name|Inflater
name|decompressor
init|=
operator|new
name|Inflater
argument_list|()
decl_stmt|;
try|try
block|{
name|decompressor
operator|.
name|setInput
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|// Decompress the data
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|decompressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|int
name|count
init|=
name|decompressor
operator|.
name|inflate
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|decompressor
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/** Decompress the byte array previously returned by    *  compressString back into a String */
DECL|method|decompressString
specifier|public
specifier|static
name|String
name|decompressString
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|DataFormatException
block|{
name|UnicodeUtil
operator|.
name|UTF16Result
name|result
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|decompress
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|result
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
return|;
block|}
block|}
end_class

end_unit

