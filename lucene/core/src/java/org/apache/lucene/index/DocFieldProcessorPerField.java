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
comment|/**  * Holds all per thread, per field state.  */
end_comment

begin_class
DECL|class|DocFieldProcessorPerField
specifier|final
class|class
name|DocFieldProcessorPerField
block|{
DECL|field|consumer
specifier|final
name|DocFieldConsumerPerField
name|consumer
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
comment|// nocommit after flush we should null these out?  then we
comment|// don't need reset() impl'd in each...
DECL|field|bytesDVWriter
name|BytesDVWriter
name|bytesDVWriter
decl_stmt|;
DECL|field|sortedBytesDVWriter
name|SortedBytesDVWriter
name|sortedBytesDVWriter
decl_stmt|;
DECL|field|numberDVWriter
name|NumberDVWriter
name|numberDVWriter
decl_stmt|;
DECL|field|next
name|DocFieldProcessorPerField
name|next
decl_stmt|;
DECL|field|lastGen
name|int
name|lastGen
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fieldCount
name|int
name|fieldCount
decl_stmt|;
DECL|field|fields
name|IndexableField
index|[]
name|fields
init|=
operator|new
name|IndexableField
index|[
literal|1
index|]
decl_stmt|;
DECL|method|DocFieldProcessorPerField
specifier|public
name|DocFieldProcessorPerField
parameter_list|(
specifier|final
name|DocFieldProcessor
name|docFieldProcessor
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|docFieldProcessor
operator|.
name|consumer
operator|.
name|addField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|docFieldProcessor
operator|.
name|bytesUsed
expr_stmt|;
block|}
comment|// nocommit make this generic chain through consumer?
DECL|method|addBytesDVField
specifier|public
name|void
name|addBytesDVField
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|bytesDVWriter
operator|==
literal|null
condition|)
block|{
name|bytesDVWriter
operator|=
operator|new
name|BytesDVWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
name|bytesDVWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// nocommit make this generic chain through consumer?
DECL|method|addSortedBytesDVField
specifier|public
name|void
name|addSortedBytesDVField
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|sortedBytesDVWriter
operator|==
literal|null
condition|)
block|{
name|sortedBytesDVWriter
operator|=
operator|new
name|SortedBytesDVWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
name|sortedBytesDVWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// nocommit make this generic chain through consumer?
DECL|method|addNumberDVField
specifier|public
name|void
name|addNumberDVField
parameter_list|(
name|int
name|docID
parameter_list|,
name|Number
name|value
parameter_list|)
block|{
if|if
condition|(
name|numberDVWriter
operator|==
literal|null
condition|)
block|{
name|numberDVWriter
operator|=
operator|new
name|NumberDVWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
name|numberDVWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// nocommit make this generic chain through consumer?
DECL|method|addFloatDVField
specifier|public
name|void
name|addFloatDVField
parameter_list|(
name|int
name|docID
parameter_list|,
name|Number
name|value
parameter_list|)
block|{
if|if
condition|(
name|numberDVWriter
operator|==
literal|null
condition|)
block|{
name|numberDVWriter
operator|=
operator|new
name|NumberDVWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
name|numberDVWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|value
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// nocommit make this generic chain through consumer?
DECL|method|addDoubleDVField
specifier|public
name|void
name|addDoubleDVField
parameter_list|(
name|int
name|docID
parameter_list|,
name|Number
name|value
parameter_list|)
block|{
if|if
condition|(
name|numberDVWriter
operator|==
literal|null
condition|)
block|{
name|numberDVWriter
operator|=
operator|new
name|NumberDVWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
name|numberDVWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|value
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|IndexableField
name|field
parameter_list|)
block|{
if|if
condition|(
name|fieldCount
operator|==
name|fields
operator|.
name|length
condition|)
block|{
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|fieldCount
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|newArray
init|=
operator|new
name|IndexableField
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fields
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|fieldCount
argument_list|)
expr_stmt|;
name|fields
operator|=
name|newArray
expr_stmt|;
block|}
name|fields
index|[
name|fieldCount
operator|++
index|]
operator|=
name|field
expr_stmt|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytesDVWriter
operator|!=
literal|null
condition|)
block|{
name|bytesDVWriter
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|numberDVWriter
operator|!=
literal|null
condition|)
block|{
name|numberDVWriter
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

