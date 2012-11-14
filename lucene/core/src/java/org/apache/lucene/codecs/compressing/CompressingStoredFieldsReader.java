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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|BYTE_ARR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|CODEC_NAME_DAT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|CODEC_NAME_IDX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|HEADER_LENGTH_DAT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|HEADER_LENGTH_IDX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|NUMERIC_DOUBLE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|NUMERIC_FLOAT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|NUMERIC_INT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|NUMERIC_LONG
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|TYPE_BITS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|TYPE_MASK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|VERSION_CURRENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
operator|.
name|CompressingStoredFieldsWriter
operator|.
name|VERSION_START
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40StoredFieldsWriter
operator|.
name|FIELDS_EXTENSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40StoredFieldsWriter
operator|.
name|FIELDS_INDEX_EXTENSION
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
name|codecs
operator|.
name|StoredFieldsReader
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
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|index
operator|.
name|StoredFieldVisitor
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
name|AlreadyClosedException
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

begin_class
DECL|class|CompressingStoredFieldsReader
specifier|final
class|class
name|CompressingStoredFieldsReader
extends|extends
name|StoredFieldsReader
block|{
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|indexReader
specifier|private
specifier|final
name|CompressingStoredFieldsIndexReader
name|indexReader
decl_stmt|;
DECL|field|fieldsStream
specifier|private
specifier|final
name|IndexInput
name|fieldsStream
decl_stmt|;
DECL|field|packedIntsVersion
specifier|private
specifier|final
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|compressionMode
specifier|private
specifier|final
name|CompressionMode
name|compressionMode
decl_stmt|;
DECL|field|decompressor
specifier|private
specifier|final
name|Decompressor
name|decompressor
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|// used by clone
DECL|method|CompressingStoredFieldsReader
specifier|private
name|CompressingStoredFieldsReader
parameter_list|(
name|CompressingStoredFieldsReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|fieldInfos
operator|=
name|reader
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|fieldsStream
operator|=
name|reader
operator|.
name|fieldsStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexReader
operator|=
name|reader
operator|.
name|indexReader
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|packedIntsVersion
operator|=
name|reader
operator|.
name|packedIntsVersion
expr_stmt|;
name|this
operator|.
name|compressionMode
operator|=
name|reader
operator|.
name|compressionMode
expr_stmt|;
name|this
operator|.
name|decompressor
operator|=
name|reader
operator|.
name|decompressor
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|reader
operator|.
name|numDocs
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|reader
operator|.
name|bytes
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|CompressingStoredFieldsReader
specifier|public
name|CompressingStoredFieldsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|segment
init|=
name|si
operator|.
name|name
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|numDocs
operator|=
name|si
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|IndexInput
name|indexStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fieldsStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
specifier|final
name|String
name|indexStreamFN
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|indexStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|indexStreamFN
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|indexStream
argument_list|,
name|CODEC_NAME_IDX
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|fieldsStream
argument_list|,
name|CODEC_NAME_DAT
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
assert|assert
name|HEADER_LENGTH_DAT
operator|==
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
assert|;
assert|assert
name|HEADER_LENGTH_IDX
operator|==
name|indexStream
operator|.
name|getFilePointer
argument_list|()
assert|;
name|indexReader
operator|=
operator|new
name|CompressingStoredFieldsIndexReader
argument_list|(
name|indexStream
argument_list|,
name|si
argument_list|)
expr_stmt|;
name|indexStream
operator|=
literal|null
expr_stmt|;
name|packedIntsVersion
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
specifier|final
name|int
name|compressionModeId
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|compressionMode
operator|=
name|CompressionMode
operator|.
name|byId
argument_list|(
name|compressionModeId
argument_list|)
expr_stmt|;
name|decompressor
operator|=
name|compressionMode
operator|.
name|newDecompressor
argument_list|()
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
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
name|this
argument_list|,
name|indexStream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this FieldsReader is closed    */
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this FieldsReader is closed"
argument_list|)
throw|;
block|}
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
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fieldsStream
argument_list|,
name|indexReader
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|readField
specifier|private
specifier|static
name|void
name|readField
parameter_list|(
name|ByteArrayDataInput
name|in
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|,
name|FieldInfo
name|info
parameter_list|,
name|int
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|bits
operator|&
name|TYPE_MASK
condition|)
block|{
case|case
name|BYTE_ARR
case|:
name|int
name|length
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|binaryField
argument_list|(
name|info
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING
case|:
name|length
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|stringField
argument_list|(
name|info
argument_list|,
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_INT
case|:
name|visitor
operator|.
name|intField
argument_list|(
name|info
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_FLOAT
case|:
name|visitor
operator|.
name|floatField
argument_list|(
name|info
argument_list|,
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_LONG
case|:
name|visitor
operator|.
name|longField
argument_list|(
name|info
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_DOUBLE
case|:
name|visitor
operator|.
name|doubleField
argument_list|(
name|info
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknown type flag: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|skipField
specifier|private
specifier|static
name|void
name|skipField
parameter_list|(
name|ByteArrayDataInput
name|in
parameter_list|,
name|int
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|bits
operator|&
name|TYPE_MASK
condition|)
block|{
case|case
name|BYTE_ARR
case|:
case|case
name|STRING
case|:
specifier|final
name|int
name|length
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|in
operator|.
name|skipBytes
argument_list|(
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_INT
case|:
case|case
name|NUMERIC_FLOAT
case|:
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
break|break;
case|case
name|NUMERIC_LONG
case|:
case|case
name|NUMERIC_DOUBLE
case|:
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknown type flag: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|visitDocument
specifier|public
name|void
name|visitDocument
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|seek
argument_list|(
name|indexReader
operator|.
name|getStartPointer
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|docBase
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|chunkDocs
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
argument_list|<
name|docBase
operator|||
name|docID
operator|>=
name|docBase
operator|+
name|chunkDocs
operator|||
name|docBase
operator|+
name|chunkDocs
argument_list|>
name|numDocs
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted: docID="
operator|+
name|docID
operator|+
literal|", docBase="
operator|+
name|docBase
operator|+
literal|", chunkDocs="
operator|+
name|chunkDocs
operator|+
literal|", numDocs="
operator|+
name|numDocs
argument_list|)
throw|;
block|}
specifier|final
name|int
name|numStoredFields
decl_stmt|,
name|offset
decl_stmt|,
name|length
decl_stmt|,
name|totalLength
decl_stmt|;
if|if
condition|(
name|chunkDocs
operator|==
literal|1
condition|)
block|{
name|numStoredFields
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|length
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|totalLength
operator|=
name|length
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|bitsPerStoredFields
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerStoredFields
operator|==
literal|0
condition|)
block|{
name|numStoredFields
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerStoredFields
operator|>
literal|31
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bitsPerStoredFields="
operator|+
name|bitsPerStoredFields
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|long
name|filePointer
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
init|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|fieldsStream
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerStoredFields
argument_list|)
decl_stmt|;
name|numStoredFields
operator|=
call|(
name|int
call|)
argument_list|(
name|reader
operator|.
name|get
argument_list|(
name|docID
operator|-
name|docBase
argument_list|)
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|filePointer
operator|+
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerStoredFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|bitsPerLength
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerLength
operator|==
literal|0
condition|)
block|{
name|length
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|offset
operator|=
operator|(
name|docID
operator|-
name|docBase
operator|)
operator|*
name|length
expr_stmt|;
name|totalLength
operator|=
name|chunkDocs
operator|*
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerStoredFields
operator|>
literal|31
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bitsPerLength="
operator|+
name|bitsPerLength
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|it
init|=
name|PackedInts
operator|.
name|getReaderIteratorNoHeader
argument_list|(
name|fieldsStream
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerLength
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|off
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
name|docID
operator|-
name|docBase
condition|;
operator|++
name|i
control|)
block|{
name|off
operator|+=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|offset
operator|=
name|off
expr_stmt|;
name|length
operator|=
operator|(
name|int
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|off
operator|+=
name|length
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|docID
operator|-
name|docBase
operator|+
literal|1
init|;
name|i
operator|<
name|chunkDocs
condition|;
operator|++
name|i
control|)
block|{
name|off
operator|+=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|totalLength
operator|=
name|off
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|length
operator|==
literal|0
operator|)
operator|!=
operator|(
name|numStoredFields
operator|==
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"length="
operator|+
name|length
operator|+
literal|", numStoredFields="
operator|+
name|numStoredFields
argument_list|)
throw|;
block|}
if|if
condition|(
name|numStoredFields
operator|==
literal|0
condition|)
block|{
comment|// nothing to do
return|return;
block|}
name|decompressor
operator|.
name|decompress
argument_list|(
name|fieldsStream
argument_list|,
name|totalLength
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|length
assert|;
specifier|final
name|ByteArrayDataInput
name|documentInput
init|=
operator|new
name|ByteArrayDataInput
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
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|fieldIDX
init|=
literal|0
init|;
name|fieldIDX
operator|<
name|numStoredFields
condition|;
name|fieldIDX
operator|++
control|)
block|{
specifier|final
name|long
name|infoAndBits
init|=
name|documentInput
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldNumber
init|=
call|(
name|int
call|)
argument_list|(
name|infoAndBits
operator|>>>
name|TYPE_BITS
argument_list|)
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bits
init|=
call|(
name|int
call|)
argument_list|(
name|infoAndBits
operator|&
name|TYPE_MASK
argument_list|)
decl_stmt|;
assert|assert
name|bits
operator|<=
name|NUMERIC_DOUBLE
operator|:
literal|"bits="
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
assert|;
switch|switch
condition|(
name|visitor
operator|.
name|needsField
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
name|readField
argument_list|(
name|documentInput
argument_list|,
name|visitor
argument_list|,
name|fieldInfo
argument_list|,
name|bits
argument_list|)
expr_stmt|;
assert|assert
name|documentInput
operator|.
name|getPosition
argument_list|()
operator|<=
name|bytes
operator|.
name|offset
operator|+
name|bytes
operator|.
name|length
operator|:
name|documentInput
operator|.
name|getPosition
argument_list|()
operator|+
literal|" "
operator|+
name|bytes
operator|.
name|offset
operator|+
name|bytes
operator|.
name|length
assert|;
break|break;
case|case
name|NO
case|:
name|skipField
argument_list|(
name|documentInput
argument_list|,
name|bits
argument_list|)
expr_stmt|;
assert|assert
name|documentInput
operator|.
name|getPosition
argument_list|()
operator|<=
name|bytes
operator|.
name|offset
operator|+
name|bytes
operator|.
name|length
operator|:
name|documentInput
operator|.
name|getPosition
argument_list|()
operator|+
literal|" "
operator|+
name|bytes
operator|.
name|offset
operator|+
name|bytes
operator|.
name|length
assert|;
break|break;
case|case
name|STOP
case|:
return|return;
block|}
block|}
assert|assert
name|documentInput
operator|.
name|getPosition
argument_list|()
operator|==
name|bytes
operator|.
name|offset
operator|+
name|bytes
operator|.
name|length
operator|:
name|documentInput
operator|.
name|getPosition
argument_list|()
operator|+
literal|" "
operator|+
name|bytes
operator|.
name|offset
operator|+
literal|" "
operator|+
name|bytes
operator|.
name|length
assert|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|StoredFieldsReader
name|clone
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|CompressingStoredFieldsReader
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|getCompressionMode
name|CompressionMode
name|getCompressionMode
parameter_list|()
block|{
return|return
name|compressionMode
return|;
block|}
DECL|method|chunkIterator
name|ChunkIterator
name|chunkIterator
parameter_list|(
name|int
name|startDocID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|indexReader
operator|.
name|getStartPointer
argument_list|(
name|startDocID
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ChunkIterator
argument_list|()
return|;
block|}
DECL|class|ChunkIterator
specifier|final
class|class
name|ChunkIterator
block|{
DECL|field|bytes
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|docBase
name|int
name|docBase
decl_stmt|;
DECL|field|chunkDocs
name|int
name|chunkDocs
decl_stmt|;
DECL|field|numStoredFields
name|int
index|[]
name|numStoredFields
decl_stmt|;
DECL|field|lengths
name|int
index|[]
name|lengths
decl_stmt|;
DECL|method|ChunkIterator
specifier|private
name|ChunkIterator
parameter_list|()
block|{
name|this
operator|.
name|docBase
operator|=
operator|-
literal|1
expr_stmt|;
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|numStoredFields
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
name|lengths
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
block|}
comment|/**      * Return the decompressed size of the chunk      */
DECL|method|chunkSize
name|int
name|chunkSize
parameter_list|()
block|{
name|int
name|sum
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
name|chunkDocs
condition|;
operator|++
name|i
control|)
block|{
name|sum
operator|+=
name|lengths
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
comment|/**      * Go to the chunk containing the provided doc ID.      */
DECL|method|next
name|void
name|next
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|doc
operator|>=
name|docBase
operator|+
name|chunkDocs
operator|:
name|doc
operator|+
literal|" "
operator|+
name|docBase
operator|+
literal|" "
operator|+
name|chunkDocs
assert|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|indexReader
operator|.
name|getStartPointer
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|docBase
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|chunkDocs
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|docBase
argument_list|<
name|this
operator|.
name|docBase
operator|+
name|this
operator|.
name|chunkDocs
operator|||
name|docBase
operator|+
name|chunkDocs
argument_list|>
name|numDocs
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted: current docBase="
operator|+
name|this
operator|.
name|docBase
operator|+
literal|", current numDocs="
operator|+
name|this
operator|.
name|chunkDocs
operator|+
literal|", new docBase="
operator|+
name|docBase
operator|+
literal|", new numDocs="
operator|+
name|chunkDocs
argument_list|)
throw|;
block|}
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
name|this
operator|.
name|chunkDocs
operator|=
name|chunkDocs
expr_stmt|;
if|if
condition|(
name|chunkDocs
operator|>
name|numStoredFields
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|newLength
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|chunkDocs
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|numStoredFields
operator|=
operator|new
name|int
index|[
name|newLength
index|]
expr_stmt|;
name|lengths
operator|=
operator|new
name|int
index|[
name|newLength
index|]
expr_stmt|;
block|}
if|if
condition|(
name|chunkDocs
operator|==
literal|1
condition|)
block|{
name|numStoredFields
index|[
literal|0
index|]
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|lengths
index|[
literal|0
index|]
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|bitsPerStoredFields
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerStoredFields
operator|==
literal|0
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|numStoredFields
argument_list|,
literal|0
argument_list|,
name|chunkDocs
argument_list|,
name|fieldsStream
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerStoredFields
operator|>
literal|31
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bitsPerStoredFields="
operator|+
name|bitsPerStoredFields
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|it
init|=
name|PackedInts
operator|.
name|getReaderIteratorNoHeader
argument_list|(
name|fieldsStream
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerStoredFields
argument_list|,
literal|1
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
name|chunkDocs
condition|;
operator|++
name|i
control|)
block|{
name|numStoredFields
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|bitsPerLength
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerLength
operator|==
literal|0
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lengths
argument_list|,
literal|0
argument_list|,
name|chunkDocs
argument_list|,
name|fieldsStream
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerLength
operator|>
literal|31
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bitsPerLength="
operator|+
name|bitsPerLength
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|it
init|=
name|PackedInts
operator|.
name|getReaderIteratorNoHeader
argument_list|(
name|fieldsStream
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerLength
argument_list|,
literal|1
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
name|chunkDocs
condition|;
operator|++
name|i
control|)
block|{
name|lengths
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Decompress the chunk.      */
DECL|method|decompress
name|void
name|decompress
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decompress data
specifier|final
name|int
name|chunkSize
init|=
name|chunkSize
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
name|fieldsStream
argument_list|,
name|chunkSize
argument_list|,
literal|0
argument_list|,
name|chunkSize
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|chunkSize
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted: expected chunk size = "
operator|+
name|chunkSize
argument_list|()
operator|+
literal|", got "
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
comment|/**      * Copy compressed data.      */
DECL|method|copyCompressedData
name|void
name|copyCompressedData
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|chunkSize
init|=
name|chunkSize
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|copyCompressedData
argument_list|(
name|fieldsStream
argument_list|,
name|chunkSize
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

