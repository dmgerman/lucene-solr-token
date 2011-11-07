begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|values
operator|.
name|FixedStraightBytesImpl
operator|.
name|FixedBytesWriterBase
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
name|values
operator|.
name|IndexDocValues
operator|.
name|Source
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
name|values
operator|.
name|IndexDocValuesArray
operator|.
name|LongValues
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
name|util
operator|.
name|Counter
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

begin_comment
comment|/**  * Stores integers using {@link PackedInts}  *   * @lucene.experimental  * */
end_comment

begin_class
DECL|class|PackedIntValues
class|class
name|PackedIntValues
block|{
DECL|field|CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"PackedInts"
decl_stmt|;
DECL|field|PACKED
specifier|private
specifier|static
specifier|final
name|byte
name|PACKED
init|=
literal|0x00
decl_stmt|;
DECL|field|FIXED_64
specifier|private
specifier|static
specifier|final
name|byte
name|FIXED_64
init|=
literal|0x01
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|class|PackedIntsWriter
specifier|static
class|class
name|PackedIntsWriter
extends|extends
name|FixedBytesWriterBase
block|{
DECL|field|minValue
specifier|private
name|long
name|minValue
decl_stmt|;
DECL|field|maxValue
specifier|private
name|long
name|maxValue
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
decl_stmt|;
DECL|field|lastDocId
specifier|private
name|int
name|lastDocId
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|PackedIntsWriter
specifier|protected
name|PackedIntsWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|bytesRef
operator|=
operator|new
name|BytesRef
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|lastDocId
operator|<
name|docID
assert|;
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|started
operator|=
literal|true
expr_stmt|;
name|minValue
operator|=
name|maxValue
operator|=
name|v
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|v
operator|<
name|minValue
condition|)
block|{
name|minValue
operator|=
name|v
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|>
name|maxValue
condition|)
block|{
name|maxValue
operator|=
name|v
expr_stmt|;
block|}
block|}
name|lastDocId
operator|=
name|docID
expr_stmt|;
name|bytesRef
operator|.
name|copy
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|docID
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|IndexOutput
name|dataOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|minValue
operator|=
name|maxValue
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
comment|// if we exceed the range of positive longs we must switch to fixed
comment|// ints
if|if
condition|(
name|delta
operator|<=
operator|(
name|maxValue
operator|>=
literal|0
operator|&&
name|minValue
operator|<=
literal|0
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
operator|)
operator|&&
name|delta
operator|>=
literal|0
condition|)
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
name|PACKED
argument_list|)
expr_stmt|;
name|writePackedInts
argument_list|(
name|dataOut
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
return|return;
comment|// done
block|}
else|else
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
name|FIXED_64
argument_list|)
expr_stmt|;
block|}
name|writeData
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|writeZeros
argument_list|(
name|docCount
operator|-
operator|(
name|lastDocID
operator|+
literal|1
operator|)
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|resetPool
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|sourceDoc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
name|add
argument_list|(
name|docID
argument_list|,
name|currentMergeSource
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writePackedInts
specifier|private
name|void
name|writePackedInts
parameter_list|(
name|IndexOutput
name|datOut
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|datOut
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
comment|// write a default value to recognize docs without a value for that
comment|// field
specifier|final
name|long
name|defaultValue
init|=
name|maxValue
operator|>=
literal|0
operator|&&
name|minValue
operator|<=
literal|0
condition|?
literal|0
operator|-
name|minValue
else|:
operator|++
name|maxValue
operator|-
name|minValue
decl_stmt|;
name|datOut
operator|.
name|writeLong
argument_list|(
name|defaultValue
argument_list|)
expr_stmt|;
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|datOut
argument_list|,
name|docCount
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxValue
operator|-
name|minValue
argument_list|)
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
name|lastDocID
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|set
argument_list|(
name|bytesRef
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|long
name|asLong
init|=
name|bytesRef
operator|.
name|asLong
argument_list|()
decl_stmt|;
name|w
operator|.
name|add
argument_list|(
name|asLong
operator|==
literal|0
condition|?
name|defaultValue
else|:
name|asLong
operator|-
name|minValue
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|lastDocID
operator|+
literal|1
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|defaultValue
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|PerDocFieldValues
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|docValues
operator|.
name|getInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Opens all necessary files, but does not read any data in until you call    * {@link #load}.    */
DECL|class|PackedIntsReader
specifier|static
class|class
name|PackedIntsReader
extends|extends
name|IndexDocValues
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|byte
name|type
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|LongValues
name|values
decl_stmt|;
DECL|method|PackedIntsReader
specifier|protected
name|PackedIntsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|datIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
name|Bytes
operator|.
name|DV_SEGMENT_SUFFIX
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|datIn
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
name|type
operator|=
name|datIn
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|values
operator|=
name|type
operator|==
name|FIXED_64
condition|?
operator|new
name|LongValues
argument_list|()
else|:
literal|null
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
name|datIn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Loads the actual values. You may call this more than once, eg if you      * already previously loaded but then discarded the Source.      */
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|Source
name|source
decl_stmt|;
name|IndexInput
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
name|input
operator|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|source
operator|=
operator|new
name|PackedIntsSource
argument_list|(
name|input
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
name|values
operator|.
name|newFromInput
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|source
return|;
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
name|input
argument_list|,
name|datIn
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|VAR_INTS
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|values
operator|!=
literal|null
condition|?
operator|new
name|FixedStraightBytesImpl
operator|.
name|DirectFixedStraightSource
argument_list|(
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
argument_list|,
literal|8
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_64
argument_list|)
else|:
operator|new
name|PackedIntsSource
argument_list|(
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
DECL|class|PackedIntsSource
specifier|static
class|class
name|PackedIntsSource
extends|extends
name|Source
block|{
DECL|field|minValue
specifier|private
specifier|final
name|long
name|minValue
decl_stmt|;
DECL|field|defaultValue
specifier|private
specifier|final
name|long
name|defaultValue
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|values
decl_stmt|;
DECL|method|PackedIntsSource
specifier|public
name|PackedIntsSource
parameter_list|(
name|IndexInput
name|dataIn
parameter_list|,
name|boolean
name|direct
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ValueType
operator|.
name|VAR_INTS
argument_list|)
expr_stmt|;
name|minValue
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|defaultValue
operator|=
name|dataIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|values
operator|=
name|direct
condition|?
name|PackedInts
operator|.
name|getDirectReader
argument_list|(
name|dataIn
argument_list|)
else|:
name|PackedInts
operator|.
name|getReader
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
name|ref
operator|.
name|grow
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|ref
operator|.
name|copy
argument_list|(
name|getInt
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|// TODO -- can we somehow avoid 2X method calls
comment|// on each get? must push minValue down, and make
comment|// PackedInts implement Ints.Source
assert|assert
name|docID
operator|>=
literal|0
assert|;
specifier|final
name|long
name|value
init|=
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|value
operator|==
name|defaultValue
condition|?
literal|0
else|:
name|minValue
operator|+
name|value
return|;
block|}
block|}
block|}
end_class

end_unit

