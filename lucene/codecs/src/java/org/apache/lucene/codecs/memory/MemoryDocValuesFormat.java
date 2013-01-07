begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
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
name|SimpleDVConsumer
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
name|SimpleDVProducer
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
name|SimpleDocValuesFormat
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
name|simpletext
operator|.
name|SimpleTextSimpleDocValuesFormat
operator|.
name|SimpleTextDocValuesReader
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
name|simpletext
operator|.
name|SimpleTextSimpleDocValuesFormat
operator|.
name|SimpleTextDocValuesWriter
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
name|BinaryDocValues
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
name|index
operator|.
name|SortedDocValues
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/** Indexes doc values to disk and loads them in RAM at  *  search time. */
end_comment

begin_class
DECL|class|MemoryDocValuesFormat
specifier|public
class|class
name|MemoryDocValuesFormat
extends|extends
name|SimpleDocValuesFormat
block|{
DECL|method|MemoryDocValuesFormat
specifier|public
name|MemoryDocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Memory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|SimpleDVConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit use a more efficient format ;):
return|return
operator|new
name|SimpleTextDocValuesWriter
argument_list|(
name|state
argument_list|,
literal|"dat"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|SimpleDVProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SimpleDVProducer
name|producer
init|=
operator|new
name|SimpleTextDocValuesReader
argument_list|(
name|state
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleDVProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|valuesIn
init|=
name|producer
operator|.
name|getNumeric
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|valuesIn
operator|.
name|size
argument_list|()
decl_stmt|;
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
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|long
name|v
init|=
name|valuesIn
operator|.
name|get
argument_list|(
name|docID
argument_list|)
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
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
specifier|final
name|int
name|bitsRequired
init|=
name|delta
operator|<
literal|0
condition|?
literal|64
else|:
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Mutable
name|values
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|maxDoc
argument_list|,
name|bitsRequired
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
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
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|values
operator|.
name|set
argument_list|(
name|docID
argument_list|,
name|valuesIn
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|-
name|minValue
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|finalMinValue
init|=
name|minValue
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
name|finalMinValue
operator|+
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|valuesIn
init|=
name|producer
operator|.
name|getBinary
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|valuesIn
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxLength
init|=
name|valuesIn
operator|.
name|maxLength
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|fixedLength
init|=
name|valuesIn
operator|.
name|isFixedLength
argument_list|()
decl_stmt|;
comment|// nocommit more ram efficient
specifier|final
name|byte
index|[]
index|[]
name|values
init|=
operator|new
name|byte
index|[
name|maxDoc
index|]
index|[]
decl_stmt|;
name|BytesRef
name|scratch
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
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|valuesIn
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|values
index|[
name|docID
index|]
operator|=
operator|new
name|byte
index|[
name|scratch
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
argument_list|,
name|values
index|[
name|docID
index|]
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|bytes
operator|=
name|values
index|[
name|docID
index|]
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|result
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
name|fixedLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
name|maxLength
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|valuesIn
init|=
name|producer
operator|.
name|getSorted
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|valuesIn
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxLength
init|=
name|valuesIn
operator|.
name|maxLength
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|fixedLength
init|=
name|valuesIn
operator|.
name|isFixedLength
argument_list|()
decl_stmt|;
specifier|final
name|int
name|valueCount
init|=
name|valuesIn
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
comment|// nocommit used packed ints and so on
specifier|final
name|byte
index|[]
index|[]
name|values
init|=
operator|new
name|byte
index|[
name|valueCount
index|]
index|[]
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|values
operator|.
name|length
condition|;
name|ord
operator|++
control|)
block|{
name|valuesIn
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|values
index|[
name|ord
index|]
operator|=
operator|new
name|byte
index|[
name|scratch
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
argument_list|,
name|values
index|[
name|ord
index|]
argument_list|,
literal|0
argument_list|,
name|scratch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|docToOrd
init|=
operator|new
name|int
index|[
name|maxDoc
index|]
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
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|docToOrd
index|[
name|docID
index|]
operator|=
name|valuesIn
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|docToOrd
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|bytes
operator|=
name|values
index|[
name|ord
index|]
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|result
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
name|fixedLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
name|maxLength
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|SimpleDVProducer
name|clone
parameter_list|()
block|{
comment|// We are already thread-safe:
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

