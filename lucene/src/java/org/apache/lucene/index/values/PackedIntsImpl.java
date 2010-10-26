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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|AttributeSource
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
name|LongsRef
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
name|PackedInts
import|;
end_import

begin_comment
comment|/** Stores ints packed with fixed-bit precision. */
end_comment

begin_class
DECL|class|PackedIntsImpl
class|class
name|PackedIntsImpl
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
DECL|class|IntsWriter
specifier|static
class|class
name|IntsWriter
extends|extends
name|Writer
block|{
comment|// nocommit - can we bulkcopy this on a merge?
DECL|field|intsRef
specifier|private
name|LongsRef
name|intsRef
decl_stmt|;
DECL|field|docToValue
specifier|private
name|long
index|[]
name|docToValue
decl_stmt|;
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
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|maxDocID
specifier|private
name|int
name|maxDocID
decl_stmt|;
DECL|field|minDocID
specifier|private
name|int
name|minDocID
decl_stmt|;
DECL|method|IntsWriter
specifier|protected
name|IntsWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|docToValue
operator|=
operator|new
name|long
index|[
literal|1
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|synchronized
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
name|v
expr_stmt|;
name|minDocID
operator|=
name|maxDocID
operator|=
name|docID
expr_stmt|;
name|started
operator|=
literal|true
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
if|if
condition|(
name|docID
operator|<
name|minDocID
condition|)
block|{
name|minDocID
operator|=
name|docID
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docID
operator|>
name|maxDocID
condition|)
block|{
name|maxDocID
operator|=
name|docID
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docID
operator|>=
name|docToValue
operator|.
name|length
condition|)
block|{
name|docToValue
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docToValue
argument_list|,
literal|1
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|docToValue
index|[
name|docID
index|]
operator|=
name|v
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
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
if|if
condition|(
operator|!
name|started
condition|)
return|return;
specifier|final
name|IndexOutput
name|datOut
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|datOut
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|// nocommit -- long can't work right since it's signed
name|datOut
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
comment|// write a default value to recognize docs without a value for that field
specifier|final
name|long
name|defaultValue
init|=
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
specifier|final
name|int
name|limit
init|=
name|maxDocID
operator|+
literal|1
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
name|minDocID
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
for|for
control|(
name|int
name|i
init|=
name|minDocID
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|docToValue
index|[
name|i
index|]
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
name|limit
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
name|datOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|docToValue
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|intsRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextAttribute
specifier|protected
name|void
name|setNextAttribute
parameter_list|(
name|ValuesAttribute
name|attr
parameter_list|)
block|{
name|intsRef
operator|=
name|attr
operator|.
name|ints
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
name|ValuesAttribute
name|attr
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LongsRef
name|ref
decl_stmt|;
if|if
condition|(
operator|(
name|ref
operator|=
name|attr
operator|.
name|ints
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Opens all necessary files, but does not read any data in until you call    * {@link #load}.    */
DECL|class|IntsReader
specifier|static
class|class
name|IntsReader
extends|extends
name|DocValues
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|method|IntsReader
specifier|protected
name|IntsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
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
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
operator|new
name|IntsSource
argument_list|(
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
DECL|class|IntsSource
specifier|private
specifier|static
class|class
name|IntsSource
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
DECL|method|IntsSource
specifier|public
name|IntsSource
parameter_list|(
name|IndexInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|dataIn
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
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
DECL|method|ints
specifier|public
name|long
name|ints
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|// nocommit -- can we somehow avoid 2X method calls
comment|// on each get? must push minValue down, and make
comment|// PackedInts implement Ints.Source
specifier|final
name|long
name|val
init|=
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// docs not having a value for that field must return a default value
return|return
name|val
operator|==
name|defaultValue
condition|?
literal|0
else|:
name|minValue
operator|+
name|val
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// TODO(simonw): move that to PackedInts?
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|values
operator|.
name|getBitsPerValue
argument_list|()
operator|*
name|values
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IntsEnumImpl
argument_list|(
name|source
argument_list|,
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|PACKED_INTS
return|;
block|}
block|}
DECL|class|IntsEnumImpl
specifier|private
specifier|static
specifier|final
class|class
name|IntsEnumImpl
extends|extends
name|ValuesEnum
block|{
DECL|field|ints
specifier|private
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|ints
decl_stmt|;
DECL|field|minValue
specifier|private
name|long
name|minValue
decl_stmt|;
DECL|field|dataIn
specifier|private
specifier|final
name|IndexInput
name|dataIn
decl_stmt|;
DECL|field|defaultValue
specifier|private
specifier|final
name|long
name|defaultValue
decl_stmt|;
DECL|field|ref
specifier|private
name|LongsRef
name|ref
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|IntsEnumImpl
specifier|private
name|IntsEnumImpl
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|Values
operator|.
name|PACKED_INTS
argument_list|)
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|attr
operator|.
name|ints
argument_list|()
expr_stmt|;
name|this
operator|.
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|dataIn
operator|=
name|dataIn
expr_stmt|;
name|dataIn
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
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
name|this
operator|.
name|ints
operator|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|ints
operator|.
name|size
argument_list|()
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
name|ints
operator|.
name|close
argument_list|()
expr_stmt|;
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
specifier|final
name|long
name|val
init|=
name|ints
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|ref
operator|.
name|ints
index|[
literal|0
index|]
operator|=
name|val
operator|==
name|defaultValue
condition|?
literal|0
else|:
name|minValue
operator|+
name|val
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
comment|// can we skip this?
return|return
name|pos
operator|=
name|target
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
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

