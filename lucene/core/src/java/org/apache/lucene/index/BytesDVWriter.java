begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BinaryDocValuesConsumer
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/** Buffers up pending byte[] per doc, then flushes when  *  segment flushes. */
end_comment

begin_comment
comment|// nocommit name?
end_comment

begin_comment
comment|// nocommit make this a consumer in the chain?
end_comment

begin_class
DECL|class|BytesDVWriter
class|class
name|BytesDVWriter
block|{
comment|// nocommit more ram efficient?
DECL|field|pending
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|pending
init|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|int
name|bytesUsed
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
comment|// -2 means not set yet; -1 means length isn't fixed;
comment|// -otherwise it's the fixed length seen so far:
DECL|field|fixedLength
name|int
name|fixedLength
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
DECL|method|BytesDVWriter
specifier|public
name|BytesDVWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|iwBytesUsed
operator|=
name|iwBytesUsed
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
specifier|final
name|int
name|oldBytesUsed
init|=
name|bytesUsed
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// nocommit improve message
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null binaryValue not allowed (field="
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|mergeLength
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Fill in any holes:
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|<
name|docID
condition|)
block|{
name|pending
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|)
expr_stmt|;
name|bytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
expr_stmt|;
name|mergeLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|value
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|value
operator|.
name|bytes
argument_list|,
name|value
operator|.
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
comment|// estimate 25% overhead for ArrayList:
name|bytesUsed
operator|+=
call|(
name|int
call|)
argument_list|(
name|bytes
operator|.
name|length
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|*
literal|1.25
operator|)
argument_list|)
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
operator|-
name|oldBytesUsed
argument_list|)
expr_stmt|;
comment|//System.out.println("ADD: " + value);
block|}
DECL|method|mergeLength
specifier|private
name|void
name|mergeLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|fixedLength
operator|==
operator|-
literal|2
condition|)
block|{
name|fixedLength
operator|=
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fixedLength
operator|!=
name|length
condition|)
block|{
name|fixedLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|,
name|BinaryDocValuesConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|bufferedDocCount
init|=
name|pending
operator|.
name|size
argument_list|()
decl_stmt|;
name|BytesRef
name|value
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|bufferedDocCount
condition|;
name|docID
operator|++
control|)
block|{
name|value
operator|.
name|bytes
operator|=
name|pending
operator|.
name|get
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|value
operator|.
name|length
operator|=
name|value
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
name|consumer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|value
operator|.
name|length
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|docID
init|=
name|bufferedDocCount
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|consumer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|reset
argument_list|()
expr_stmt|;
comment|//System.out.println("FLUSH");
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|pending
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pending
operator|.
name|trimToSize
argument_list|()
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
literal|0
expr_stmt|;
name|fixedLength
operator|=
operator|-
literal|2
expr_stmt|;
name|maxLength
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

