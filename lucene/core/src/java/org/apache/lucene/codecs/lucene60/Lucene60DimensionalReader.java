begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene60
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene60
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|List
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
name|DimensionalReader
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
name|bkd
operator|.
name|BKDReader
import|;
end_import

begin_comment
comment|/** Reads dimensional values previously written with {@link Lucene60DimensionalWriter} */
end_comment

begin_class
DECL|class|Lucene60DimensionalReader
specifier|public
class|class
name|Lucene60DimensionalReader
extends|extends
name|DimensionalReader
implements|implements
name|Closeable
block|{
DECL|field|dataIn
specifier|final
name|IndexInput
name|dataIn
decl_stmt|;
DECL|field|readState
specifier|final
name|SegmentReadState
name|readState
decl_stmt|;
DECL|field|readers
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BKDReader
argument_list|>
name|readers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Sole constructor */
DECL|method|Lucene60DimensionalReader
specifier|public
name|Lucene60DimensionalReader
parameter_list|(
name|SegmentReadState
name|readState
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|readState
operator|=
name|readState
expr_stmt|;
name|String
name|dataFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|,
name|Lucene60DimensionalFormat
operator|.
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
name|dataIn
operator|=
name|readState
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|dataFileName
argument_list|,
name|readState
operator|.
name|context
argument_list|)
expr_stmt|;
name|String
name|indexFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|,
name|Lucene60DimensionalFormat
operator|.
name|INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
comment|// Read index file
try|try
init|(
name|ChecksumIndexInput
name|indexIn
init|=
name|readState
operator|.
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|indexFileName
argument_list|,
name|readState
operator|.
name|context
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|indexIn
argument_list|,
name|Lucene60DimensionalFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene60DimensionalFormat
operator|.
name|INDEX_VERSION_START
argument_list|,
name|Lucene60DimensionalFormat
operator|.
name|INDEX_VERSION_START
argument_list|,
name|readState
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|indexIn
operator|.
name|readVInt
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|fieldNumber
init|=
name|indexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|long
name|fp
init|=
name|indexIn
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|dataIn
operator|.
name|seek
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|BKDReader
name|reader
init|=
operator|new
name|BKDReader
argument_list|(
name|dataIn
argument_list|)
decl_stmt|;
name|readers
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|reader
argument_list|)
expr_stmt|;
comment|//reader.verify(readState.segmentInfo.maxDoc());
block|}
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|indexIn
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
name|success
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|field
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldInfo
name|fieldInfo
init|=
name|readState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|field
operator|+
literal|"\" is unrecognized"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|getDimensionCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|field
operator|+
literal|"\" did not index dimensional values"
argument_list|)
throw|;
block|}
name|BKDReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
assert|assert
name|reader
operator|!=
literal|null
assert|;
name|reader
operator|.
name|intersect
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|sizeInBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BKDReader
name|reader
range|:
name|readers
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|reader
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|sizeInBytes
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|BKDReader
argument_list|>
name|ent
range|:
name|readers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
name|readState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|name
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
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
name|dataIn
argument_list|)
expr_stmt|;
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
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Free up heap:
name|readers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

