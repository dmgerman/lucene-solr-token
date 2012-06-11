begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|IndexFormatTooNewException
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
name|IndexFormatTooOldException
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

begin_comment
comment|/**  * Utility class for reading and writing versioned headers.  *<p>  * Writing codec headers is useful to ensure that a file is in   * the format you think it is.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CodecUtil
specifier|public
specifier|final
class|class
name|CodecUtil
block|{
DECL|method|CodecUtil
specifier|private
name|CodecUtil
parameter_list|()
block|{}
comment|// no instance
comment|/**    * Constant to identify the start of a codec header.    */
DECL|field|CODEC_MAGIC
specifier|public
specifier|final
specifier|static
name|int
name|CODEC_MAGIC
init|=
literal|0x3fd76c17
decl_stmt|;
comment|/**    * Writes a codec header, which records both a string to    * identify the file and a version number. This header can    * be parsed and validated with     * {@link #checkHeader(DataInput, String, int, int) checkHeader()}.    *<p>    * CodecHeader --&gt; Magic,CodecName,Version    *<ul>    *<li>Magic --&gt; {@link DataOutput#writeInt Uint32}. This    *        identifies the start of the header. It is always {@value #CODEC_MAGIC}.    *<li>CodecName --&gt; {@link DataOutput#writeString String}. This    *        is a string to identify this file.    *<li>Version --&gt; {@link DataOutput#writeInt Uint32}. Records    *        the version of the file.    *</ul>    *<p>    * Note that the length of a codec header depends only upon the    * name of the codec, so this length can be computed at any time    * with {@link #headerLength(String)}.    *     * @param out Output stream    * @param codec String to identify this file. It should be simple ASCII,     *              less than 128 characters in length.    * @param version Version number    * @throws IOException If there is an I/O error writing to the underlying medium.    */
DECL|method|writeHeader
specifier|public
specifier|static
name|void
name|writeHeader
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
name|codec
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|codec
operator|.
name|length
argument_list|()
operator|||
name|bytes
operator|.
name|length
operator|>=
literal|128
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec must be simple ASCII, less than 128 characters in length [got "
operator|+
name|codec
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|CODEC_MAGIC
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
comment|/**    * Computes the length of a codec header.    *     * @param codec Codec name.    * @return length of the entire codec header.    * @see #writeHeader(DataOutput, String, int)    */
DECL|method|headerLength
specifier|public
specifier|static
name|int
name|headerLength
parameter_list|(
name|String
name|codec
parameter_list|)
block|{
return|return
literal|9
operator|+
name|codec
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Reads and validates a header previously written with     * {@link #writeHeader(DataOutput, String, int)}.    *<p>    * When reading a file, supply the expected<code>codec</code> and    * an expected version range (<code>minVersion to maxVersion</code>).    *     * @param in Input stream, positioned at the point where the    *        header was previously written. Typically this is located    *        at the beginning of the file.    * @param codec The expected codec name.    * @param minVersion The minimum supported expected version number.    * @param maxVersion The maximum supported expected version number.    * @return The actual version found, when a valid header is found     *         that matches<code>codec</code>, with an actual version     *         where<code>minVersion<= actual<= maxVersion</code>.    *         Otherwise an exception is thrown.    * @throws CorruptIndexException If the first four bytes are not    *         {@link #CODEC_MAGIC}, or if the actual codec found is    *         not<code>codec</code>.    * @throws IndexFormatTooOldException If the actual version is less     *         than<code>minVersion</code>.    * @throws IndexFormatTooNewException If the actual version is greater     *         than<code>maxVersion</code>.    * @throws IOException If there is an I/O error reading from the underlying medium.    * @see #writeHeader(DataOutput, String, int)    */
DECL|method|checkHeader
specifier|public
specifier|static
name|int
name|checkHeader
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Safety to guard against reading a bogus string:
specifier|final
name|int
name|actualHeader
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|actualHeader
operator|!=
name|CODEC_MAGIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"codec header mismatch: actual header="
operator|+
name|actualHeader
operator|+
literal|" vs expected header="
operator|+
name|CODEC_MAGIC
operator|+
literal|" (resource: "
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|checkHeaderNoMagic
argument_list|(
name|in
argument_list|,
name|codec
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
return|;
block|}
comment|/** Like {@link    *  #checkHeader(DataInput,String,int,int)} except this    *  version assumes the first int has already been read    *  and validated from the input. */
DECL|method|checkHeaderNoMagic
specifier|public
specifier|static
name|int
name|checkHeaderNoMagic
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|actualCodec
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|actualCodec
operator|.
name|equals
argument_list|(
name|codec
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"codec mismatch: actual codec="
operator|+
name|actualCodec
operator|+
literal|" vs expected codec="
operator|+
name|codec
operator|+
literal|" (resource: "
operator|+
name|in
operator|+
literal|")"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|actualVersion
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|actualVersion
operator|<
name|minVersion
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|in
argument_list|,
name|actualVersion
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
throw|;
block|}
if|if
condition|(
name|actualVersion
operator|>
name|maxVersion
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|in
argument_list|,
name|actualVersion
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
throw|;
block|}
return|return
name|actualVersion
return|;
block|}
block|}
end_class

end_unit

