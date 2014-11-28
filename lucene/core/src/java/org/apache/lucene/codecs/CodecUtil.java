begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|BufferedChecksumIndexInput
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
name|StringHelper
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
comment|/**    * Constant to identify the start of a codec footer.    */
DECL|field|FOOTER_MAGIC
specifier|public
specifier|final
specifier|static
name|int
name|FOOTER_MAGIC
init|=
operator|~
name|CODEC_MAGIC
decl_stmt|;
comment|/**    * Writes a codec header, which records both a string to    * identify the file and a version number. This header can    * be parsed and validated with     * {@link #checkHeader(DataInput, String, int, int) checkHeader()}.    *<p>    * CodecHeader --&gt; Magic,CodecName,Version    *<ul>    *<li>Magic --&gt; {@link DataOutput#writeInt Uint32}. This    *        identifies the start of the header. It is always {@value #CODEC_MAGIC}.    *<li>CodecName --&gt; {@link DataOutput#writeString String}. This    *        is a string to identify this file.    *<li>Version --&gt; {@link DataOutput#writeInt Uint32}. Records    *        the version of the file.    *</ul>    *<p>    * Note that the length of a codec header depends only upon the    * name of the codec, so this length can be computed at any time    * with {@link #headerLength(String)}.    *     * @param out Output stream    * @param codec String to identify this file. It should be simple ASCII,     *              less than 128 characters in length.    * @param version Version number    * @throws IOException If there is an I/O error writing to the underlying medium.    * @throws IllegalArgumentException If the codec name is not simple ASCII, or is more than 127 characters in length    */
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
comment|/**    * Writes a codec header for an index file, which records both a string to    * identify the format of the file, a version number, and data to identify    * the file instance (ID and auxiliary suffix such as generation).    *<p>    * This header can be parsed and validated with     * {@link #checkIndexHeader(DataInput, String, int, int, byte[], String) checkIndexHeader()}.    *<p>    * IndexHeader --&gt; CodecHeader,ObjectID,ObjectSuffix    *<ul>    *<li>CodecHeader   --&gt; {@link #writeHeader}    *<li>ObjectID     --&gt; {@link DataOutput#writeByte byte}<sup>16</sup>    *<li>ObjectSuffix --&gt; SuffixLength,SuffixBytes    *<li>SuffixLength  --&gt; {@link DataOutput#writeByte byte}    *<li>SuffixBytes   --&gt; {@link DataOutput#writeByte byte}<sup>SuffixLength</sup>    *</ul>    *<p>    * Note that the length of an index header depends only upon the    * name of the codec and suffix, so this length can be computed at any time    * with {@link #indexHeaderLength(String,String)}.    *     * @param out Output stream    * @param codec String to identify the format of this file. It should be simple ASCII,     *              less than 128 characters in length.    * @param id Unique identifier for this particular file instance.    * @param suffix auxiliary suffix information for the file. It should be simple ASCII,    *              less than 256 characters in length.    * @param version Version number    * @throws IOException If there is an I/O error writing to the underlying medium.    * @throws IllegalArgumentException If the codec name is not simple ASCII, or     *         is more than 127 characters in length, or if id is invalid,    *         or if the suffix is not simple ASCII, or more than 255 characters    *         in length.    */
DECL|method|writeIndexHeader
specifier|public
specifier|static
name|void
name|writeIndexHeader
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|version
parameter_list|,
name|byte
index|[]
name|id
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|id
operator|.
name|length
operator|!=
name|StringHelper
operator|.
name|ID_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid id: "
operator|+
name|StringHelper
operator|.
name|idToString
argument_list|(
name|id
argument_list|)
argument_list|)
throw|;
block|}
name|writeHeader
argument_list|(
name|out
argument_list|,
name|codec
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|)
expr_stmt|;
name|BytesRef
name|suffixBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|suffix
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffixBytes
operator|.
name|length
operator|!=
name|suffix
operator|.
name|length
argument_list|()
operator|||
name|suffixBytes
operator|.
name|length
operator|>=
literal|256
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec must be simple ASCII, less than 256 characters in length [got "
operator|+
name|suffix
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|suffixBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|suffixBytes
operator|.
name|bytes
argument_list|,
name|suffixBytes
operator|.
name|offset
argument_list|,
name|suffixBytes
operator|.
name|length
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
comment|/**    * Computes the length of an index header.    *     * @param codec Codec name.    * @return length of the entire index header.    * @see #writeIndexHeader(DataOutput, String, int, byte[], String)    */
DECL|method|indexHeaderLength
specifier|public
specifier|static
name|int
name|indexHeaderLength
parameter_list|(
name|String
name|codec
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
name|headerLength
argument_list|(
name|codec
argument_list|)
operator|+
name|StringHelper
operator|.
name|ID_LENGTH
operator|+
literal|1
operator|+
name|suffix
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Reads and validates a header previously written with     * {@link #writeHeader(DataOutput, String, int)}.    *<p>    * When reading a file, supply the expected<code>codec</code> and    * an expected version range (<code>minVersion to maxVersion</code>).    *     * @param in Input stream, positioned at the point where the    *        header was previously written. Typically this is located    *        at the beginning of the file.    * @param codec The expected codec name.    * @param minVersion The minimum supported expected version number.    * @param maxVersion The maximum supported expected version number.    * @return The actual version found, when a valid header is found     *         that matches<code>codec</code>, with an actual version     *         where {@code minVersion<= actual<= maxVersion}.    *         Otherwise an exception is thrown.    * @throws CorruptIndexException If the first four bytes are not    *         {@link #CODEC_MAGIC}, or if the actual codec found is    *         not<code>codec</code>.    * @throws IndexFormatTooOldException If the actual version is less     *         than<code>minVersion</code>.    * @throws IndexFormatTooNewException If the actual version is greater     *         than<code>maxVersion</code>.    * @throws IOException If there is an I/O error reading from the underlying medium.    * @see #writeHeader(DataOutput, String, int)    */
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
argument_list|,
name|in
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
argument_list|,
name|in
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
comment|/**    * Reads and validates a header previously written with     * {@link #writeIndexHeader(DataOutput, String, int, byte[], String)}.    *<p>    * When reading a file, supply the expected<code>codec</code>,    * expected version range (<code>minVersion to maxVersion</code>),    * and object ID and suffix.    *     * @param in Input stream, positioned at the point where the    *        header was previously written. Typically this is located    *        at the beginning of the file.    * @param codec The expected codec name.    * @param minVersion The minimum supported expected version number.    * @param maxVersion The maximum supported expected version number.    * @param expectedID The expected object identifier for this file.    * @param expectedSuffix The expected auxiliary suffix for this file.    * @return The actual version found, when a valid header is found     *         that matches<code>codec</code>, with an actual version     *         where {@code minVersion<= actual<= maxVersion},     *         and matching<code>expectedID</code> and<code>expectedSuffix</code>    *         Otherwise an exception is thrown.    * @throws CorruptIndexException If the first four bytes are not    *         {@link #CODEC_MAGIC}, or if the actual codec found is    *         not<code>codec</code>, or if the<code>expectedID</code>    *         or<code>expectedSuffix</code> do not match.    * @throws IndexFormatTooOldException If the actual version is less     *         than<code>minVersion</code>.    * @throws IndexFormatTooNewException If the actual version is greater     *         than<code>maxVersion</code>.    * @throws IOException If there is an I/O error reading from the underlying medium.    * @see #writeIndexHeader(DataOutput, String, int, byte[],String)    */
DECL|method|checkIndexHeader
specifier|public
specifier|static
name|int
name|checkIndexHeader
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
parameter_list|,
name|byte
index|[]
name|expectedID
parameter_list|,
name|String
name|expectedSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|version
init|=
name|checkHeader
argument_list|(
name|in
argument_list|,
name|codec
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
decl_stmt|;
name|checkIndexHeaderID
argument_list|(
name|in
argument_list|,
name|expectedID
argument_list|)
expr_stmt|;
name|checkIndexHeaderSuffix
argument_list|(
name|in
argument_list|,
name|expectedSuffix
argument_list|)
expr_stmt|;
return|return
name|version
return|;
block|}
comment|/** Expert: just reads and verifies the object ID of an index header */
DECL|method|checkIndexHeaderID
specifier|public
specifier|static
name|byte
index|[]
name|checkIndexHeaderID
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|byte
index|[]
name|expectedID
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|id
index|[]
init|=
operator|new
name|byte
index|[
name|StringHelper
operator|.
name|ID_LENGTH
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|id
argument_list|,
name|expectedID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"file mismatch, expected id="
operator|+
name|StringHelper
operator|.
name|idToString
argument_list|(
name|expectedID
argument_list|)
operator|+
literal|", got="
operator|+
name|StringHelper
operator|.
name|idToString
argument_list|(
name|id
argument_list|)
argument_list|,
name|in
argument_list|)
throw|;
block|}
return|return
name|id
return|;
block|}
comment|/** Expert: just reads and verifies the suffix of an index header */
DECL|method|checkIndexHeaderSuffix
specifier|public
specifier|static
name|String
name|checkIndexHeaderSuffix
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|String
name|expectedSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|suffixLength
init|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
name|byte
name|suffixBytes
index|[]
init|=
operator|new
name|byte
index|[
name|suffixLength
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|suffixBytes
argument_list|,
literal|0
argument_list|,
name|suffixBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|suffix
init|=
operator|new
name|String
argument_list|(
name|suffixBytes
argument_list|,
literal|0
argument_list|,
name|suffixBytes
operator|.
name|length
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|suffix
operator|.
name|equals
argument_list|(
name|expectedSuffix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"file mismatch, expected suffix="
operator|+
name|expectedSuffix
operator|+
literal|", got="
operator|+
name|suffix
argument_list|,
name|in
argument_list|)
throw|;
block|}
return|return
name|suffix
return|;
block|}
comment|/**    * Writes a codec footer, which records both a checksum    * algorithm ID and a checksum. This footer can    * be parsed and validated with     * {@link #checkFooter(ChecksumIndexInput) checkFooter()}.    *<p>    * CodecFooter --&gt; Magic,AlgorithmID,Checksum    *<ul>    *<li>Magic --&gt; {@link DataOutput#writeInt Uint32}. This    *        identifies the start of the footer. It is always {@value #FOOTER_MAGIC}.    *<li>AlgorithmID --&gt; {@link DataOutput#writeInt Uint32}. This    *        indicates the checksum algorithm used. Currently this is always 0,    *        for zlib-crc32.    *<li>Checksum --&gt; {@link DataOutput#writeLong Uint64}. The    *        actual checksum value for all previous bytes in the stream, including    *        the bytes from Magic and AlgorithmID.    *</ul>    *     * @param out Output stream    * @throws IOException If there is an I/O error writing to the underlying medium.    */
DECL|method|writeFooter
specifier|public
specifier|static
name|void
name|writeFooter
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|FOOTER_MAGIC
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|out
operator|.
name|getChecksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Computes the length of a codec footer.    *     * @return length of the entire codec footer.    * @see #writeFooter(IndexOutput)    */
DECL|method|footerLength
specifier|public
specifier|static
name|int
name|footerLength
parameter_list|()
block|{
return|return
literal|16
return|;
block|}
comment|/**     * Validates the codec footer previously written by {@link #writeFooter}.     * @return actual checksum value    * @throws IOException if the footer is invalid, if the checksum does not match,     *                     or if {@code in} is not properly positioned before the footer    *                     at the end of the stream.    */
DECL|method|checkFooter
specifier|public
specifier|static
name|long
name|checkFooter
parameter_list|(
name|ChecksumIndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|validateFooter
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|long
name|actualChecksum
init|=
name|in
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
name|long
name|expectedChecksum
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectedChecksum
operator|!=
name|actualChecksum
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"checksum failed (hardware problem?) : expected="
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|expectedChecksum
argument_list|)
operator|+
literal|" actual="
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|actualChecksum
argument_list|)
argument_list|,
name|in
argument_list|)
throw|;
block|}
return|return
name|actualChecksum
return|;
block|}
comment|/**     * Validates the codec footer previously written by {@link #writeFooter}, optionally    * passing an unexpected exception that has already occurred.    *<p>    * When a {@code priorException} is provided, this method will add a suppressed exception     * indicating whether the checksum for the stream passes, fails, or cannot be computed, and     * rethrow it. Otherwise it behaves the same as {@link #checkFooter(ChecksumIndexInput)}.    *<p>    * Example usage:    *<pre class="prettyprint">    * try (ChecksumIndexInput input = ...) {    *   Throwable priorE = null;    *   try {    *     // ... read a bunch of stuff ...     *   } catch (Throwable exception) {    *     priorE = exception;    *   } finally {    *     CodecUtil.checkFooter(input, priorE);    *   }    * }    *</pre>    */
DECL|method|checkFooter
specifier|public
specifier|static
name|void
name|checkFooter
parameter_list|(
name|ChecksumIndexInput
name|in
parameter_list|,
name|Throwable
name|priorException
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|priorException
operator|==
literal|null
condition|)
block|{
name|checkFooter
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|long
name|remaining
init|=
name|in
operator|.
name|length
argument_list|()
operator|-
name|in
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|<
name|footerLength
argument_list|()
condition|)
block|{
comment|// corruption caused us to read into the checksum footer already: we can't proceed
name|priorException
operator|.
name|addSuppressed
argument_list|(
operator|new
name|CorruptIndexException
argument_list|(
literal|"checksum status indeterminate: remaining="
operator|+
name|remaining
operator|+
literal|", please run checkindex for more details"
argument_list|,
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise, skip any unread bytes.
name|in
operator|.
name|skipBytes
argument_list|(
name|remaining
operator|-
name|footerLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// now check the footer
try|try
block|{
name|long
name|checksum
init|=
name|checkFooter
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|priorException
operator|.
name|addSuppressed
argument_list|(
operator|new
name|CorruptIndexException
argument_list|(
literal|"checksum passed ("
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|checksum
argument_list|)
operator|+
literal|"). possibly transient resource issue, or a Lucene or JVM bug"
argument_list|,
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptIndexException
name|t
parameter_list|)
block|{
name|priorException
operator|.
name|addSuppressed
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// catch-all for things that shouldn't go wrong (e.g. OOM during readInt) but could...
name|priorException
operator|.
name|addSuppressed
argument_list|(
operator|new
name|CorruptIndexException
argument_list|(
literal|"checksum status indeterminate: unexpected exception"
argument_list|,
name|in
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|priorException
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Returns (but does not validate) the checksum previously written by {@link #checkFooter}.    * @return actual checksum value    * @throws IOException if the footer is invalid    */
DECL|method|retrieveChecksum
specifier|public
specifier|static
name|long
name|retrieveChecksum
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|length
argument_list|()
operator|-
name|footerLength
argument_list|()
argument_list|)
expr_stmt|;
name|validateFooter
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|readLong
argument_list|()
return|;
block|}
DECL|method|validateFooter
specifier|private
specifier|static
name|void
name|validateFooter
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
name|in
operator|.
name|length
argument_list|()
operator|-
name|in
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|expected
init|=
name|footerLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|<
name|expected
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"misplaced codec footer (file truncated?): remaining="
operator|+
name|remaining
operator|+
literal|", expected="
operator|+
name|expected
argument_list|,
name|in
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|>
name|expected
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"misplaced codec footer (file extended?): remaining="
operator|+
name|remaining
operator|+
literal|", expected="
operator|+
name|expected
argument_list|,
name|in
argument_list|)
throw|;
block|}
specifier|final
name|int
name|magic
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|magic
operator|!=
name|FOOTER_MAGIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"codec footer mismatch: actual footer="
operator|+
name|magic
operator|+
literal|" vs expected footer="
operator|+
name|FOOTER_MAGIC
argument_list|,
name|in
argument_list|)
throw|;
block|}
specifier|final
name|int
name|algorithmID
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|algorithmID
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"codec footer mismatch: unknown algorithmID: "
operator|+
name|algorithmID
argument_list|,
name|in
argument_list|)
throw|;
block|}
block|}
comment|/**    * Checks that the stream is positioned at the end, and throws exception    * if it is not.     * @deprecated Use {@link #checkFooter} instead, this should only used for files without checksums     */
annotation|@
name|Deprecated
DECL|method|checkEOF
specifier|public
specifier|static
name|void
name|checkEOF
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|!=
name|in
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"did not read all bytes from file: read "
operator|+
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" vs size "
operator|+
name|in
operator|.
name|length
argument_list|()
argument_list|,
name|in
argument_list|)
throw|;
block|}
block|}
comment|/**     * Clones the provided input, reads all bytes from the file, and calls {@link #checkFooter}     *<p>    * Note that this method may be slow, as it must process the entire file.    * If you just need to extract the checksum value, call {@link #retrieveChecksum}.    */
DECL|method|checksumEntireFile
specifier|public
specifier|static
name|long
name|checksumEntireFile
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|clone
init|=
name|input
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ChecksumIndexInput
name|in
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
name|clone
argument_list|)
decl_stmt|;
assert|assert
name|in
operator|.
name|getFilePointer
argument_list|()
operator|==
literal|0
assert|;
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|length
argument_list|()
operator|-
name|footerLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|checkFooter
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

