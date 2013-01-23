begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|DocValuesConsumer
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
name|lucene40
operator|.
name|Lucene40FieldInfosReader
operator|.
name|LegacyDocValuesType
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
name|SegmentWriteState
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
name|CompoundFileDirectory
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_class
DECL|class|Lucene40DocValuesWriter
class|class
name|Lucene40DocValuesWriter
extends|extends
name|DocValuesConsumer
block|{
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|SegmentWriteState
name|state
decl_stmt|;
DECL|field|legacyKey
specifier|private
specifier|final
name|String
name|legacyKey
decl_stmt|;
comment|// note: intentionally ignores seg suffix
comment|// String filename = IndexFileNames.segmentFileName(state.segmentInfo.name, "dv", IndexFileNames.COMPOUND_FILE_EXTENSION);
DECL|method|Lucene40DocValuesWriter
name|Lucene40DocValuesWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|legacyKey
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|legacyKey
operator|=
name|legacyKey
expr_stmt|;
name|this
operator|.
name|dir
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|filename
argument_list|,
name|state
operator|.
name|context
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNumericField
specifier|public
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: examine the values: and simulate all the possibilities.
comment|// e.g. if all values fit in a byte, write a fixed_8 etc.
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|VAR_INTS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fileName
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexOutput
name|data
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|addVarIntsField
argument_list|(
name|data
argument_list|,
name|values
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
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addBinaryField
specifier|public
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
literal|false
assert|;
block|}
annotation|@
name|Override
DECL|method|addSortedField
specifier|public
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
literal|false
assert|;
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addVarIntsField
specifier|private
name|void
name|addVarIntsField
parameter_list|(
name|IndexOutput
name|output
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|long
name|v
init|=
name|n
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
block|{
comment|// writes longs
name|output
operator|.
name|writeByte
argument_list|(
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_FIXED_64
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|output
operator|.
name|writeLong
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// writes packed ints
name|output
operator|.
name|writeByte
argument_list|(
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_PACKED
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
literal|0
operator|-
name|minValue
argument_list|)
expr_stmt|;
comment|// default value (representation of 0)
name|PackedInts
operator|.
name|Writer
name|writer
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|output
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
operator|-
name|minValue
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

