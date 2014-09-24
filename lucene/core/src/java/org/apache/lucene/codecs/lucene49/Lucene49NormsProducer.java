begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|NormsProducer
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
name|NumericDocValues
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
name|SegmentReadState
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
name|Accountable
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
name|Accountables
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
name|RamUsageEstimator
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
name|BlockPackedReader
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
name|lucene49
operator|.
name|Lucene49NormsFormat
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
name|lucene49
operator|.
name|Lucene49NormsFormat
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
name|lucene49
operator|.
name|Lucene49NormsConsumer
operator|.
name|CONST_COMPRESSED
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
name|lucene49
operator|.
name|Lucene49NormsConsumer
operator|.
name|DELTA_COMPRESSED
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
name|lucene49
operator|.
name|Lucene49NormsConsumer
operator|.
name|TABLE_COMPRESSED
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
name|lucene49
operator|.
name|Lucene49NormsConsumer
operator|.
name|UNCOMPRESSED
import|;
end_import

begin_comment
comment|/**  * Reader for {@link Lucene49NormsFormat}  */
end_comment

begin_class
DECL|class|Lucene49NormsProducer
class|class
name|Lucene49NormsProducer
extends|extends
name|NormsProducer
block|{
comment|// metadata maps (just file pointers and minimal stuff)
DECL|field|norms
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NormsEntry
argument_list|>
name|norms
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|IndexInput
name|data
decl_stmt|;
comment|// ram instances we have already loaded
DECL|field|instances
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NumericDocValues
argument_list|>
name|instances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|instancesInfo
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Accountable
argument_list|>
name|instancesInfo
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|final
name|AtomicLong
name|ramBytesUsed
decl_stmt|;
DECL|field|activeCount
specifier|private
specifier|final
name|AtomicInteger
name|activeCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|Lucene49NormsProducer
name|Lucene49NormsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|String
name|dataCodec
parameter_list|,
name|String
name|dataExtension
parameter_list|,
name|String
name|metaCodec
parameter_list|,
name|String
name|metaExtension
parameter_list|)
throws|throws
name|IOException
block|{
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|String
name|metaName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|metaExtension
argument_list|)
decl_stmt|;
name|ramBytesUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|version
init|=
operator|-
literal|1
decl_stmt|;
comment|// read in the entries from the metadata file.
try|try
init|(
name|ChecksumIndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|readFields
argument_list|(
name|in
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|in
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|dataName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|dataExtension
argument_list|)
decl_stmt|;
name|this
operator|.
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|dataName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|version2
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|version2
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Format versions mismatch: meta="
operator|+
name|version
operator|+
literal|",data="
operator|+
name|version2
argument_list|,
name|data
argument_list|)
throw|;
block|}
comment|// NOTE: data file is too costly to verify checksum against all the bytes on open,
comment|// but for now we at least verify proper structure of the checksum footer: which looks
comment|// for FOOTER_MAGIC + algorithmID. This is cheap and can detect some forms of corruption
comment|// such as file truncation.
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|data
argument_list|)
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
operator|.
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readFields
specifier|private
name|void
name|readFields
parameter_list|(
name|IndexInput
name|meta
parameter_list|,
name|FieldInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|fieldNumber
init|=
name|meta
operator|.
name|readVInt
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldNumber
operator|!=
operator|-
literal|1
condition|)
block|{
name|FieldInfo
name|info
init|=
name|infos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid field number: "
operator|+
name|fieldNumber
argument_list|,
name|meta
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|info
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid field: "
operator|+
name|info
operator|.
name|name
argument_list|,
name|meta
argument_list|)
throw|;
block|}
name|NormsEntry
name|entry
init|=
operator|new
name|NormsEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|format
operator|=
name|meta
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|entry
operator|.
name|format
condition|)
block|{
case|case
name|CONST_COMPRESSED
case|:
case|case
name|UNCOMPRESSED
case|:
case|case
name|TABLE_COMPRESSED
case|:
case|case
name|DELTA_COMPRESSED
case|:
break|break;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Unknown format: "
operator|+
name|entry
operator|.
name|format
argument_list|,
name|meta
argument_list|)
throw|;
block|}
name|norms
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNorms
specifier|public
specifier|synchronized
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|instance
init|=
name|instances
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
name|loadNorms
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|instances
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|instance
argument_list|)
expr_stmt|;
name|activeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
specifier|synchronized
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"field"
argument_list|,
name|instancesInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
DECL|method|loadNorms
specifier|private
name|NumericDocValues
name|loadNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NormsEntry
name|entry
init|=
name|norms
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|entry
operator|.
name|format
condition|)
block|{
case|case
name|CONST_COMPRESSED
case|:
name|instancesInfo
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"constant"
argument_list|,
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
literal|8
argument_list|)
expr_stmt|;
specifier|final
name|long
name|v
init|=
name|entry
operator|.
name|offset
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|v
return|;
block|}
block|}
return|;
case|case
name|UNCOMPRESSED
case|:
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
name|maxDoc
index|]
decl_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|instancesInfo
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"byte array"
argument_list|,
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|bytes
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
case|case
name|DELTA_COMPRESSED
case|:
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
name|int
name|packedIntsVersion
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|blockSize
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|BlockPackedReader
name|reader
init|=
operator|new
name|BlockPackedReader
argument_list|(
name|data
argument_list|,
name|packedIntsVersion
argument_list|,
name|blockSize
argument_list|,
name|maxDoc
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|reader
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
name|instancesInfo
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"delta compressed"
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
case|case
name|TABLE_COMPRESSED
case|:
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
name|int
name|packedVersion
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|256
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"TABLE_COMPRESSED cannot have more than 256 distinct values, got="
operator|+
name|size
argument_list|,
name|data
argument_list|)
throw|;
block|}
specifier|final
name|long
name|decode
index|[]
init|=
operator|new
name|long
index|[
name|size
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
name|decode
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|decode
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|formatID
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|ordsReader
init|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|data
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|byId
argument_list|(
name|formatID
argument_list|)
argument_list|,
name|packedVersion
argument_list|,
name|maxDoc
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|decode
argument_list|)
operator|+
name|ordsReader
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
name|instancesInfo
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"table compressed"
argument_list|,
name|ordsReader
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|decode
index|[
operator|(
name|int
operator|)
name|ordsReader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
index|]
return|;
block|}
block|}
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
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
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|NormsEntry
specifier|static
class|class
name|NormsEntry
block|{
DECL|field|format
name|byte
name|format
decl_stmt|;
DECL|field|offset
name|long
name|offset
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(fields="
operator|+
name|norms
operator|.
name|size
argument_list|()
operator|+
literal|",active="
operator|+
name|activeCount
operator|.
name|get
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

