begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|DocIdSetIterator
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
name|BitSetIterator
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
name|FixedBitSet
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
name|PackedLongValues
import|;
end_import

begin_comment
comment|/** Buffers up pending long per doc, then flushes when  *  segment flushes. */
end_comment

begin_class
DECL|class|NumericDocValuesWriter
class|class
name|NumericDocValuesWriter
extends|extends
name|DocValuesWriter
block|{
DECL|field|pending
specifier|private
name|PackedLongValues
operator|.
name|Builder
name|pending
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
DECL|field|docsWithField
specifier|private
name|FixedBitSet
name|docsWithField
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|NumericDocValuesWriter
specifier|public
name|NumericDocValuesWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|pending
operator|=
name|PackedLongValues
operator|.
name|deltaPackedBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|docsWithField
operator|=
operator|new
name|FixedBitSet
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|docsWithField
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
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
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
argument_list|)
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
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<=
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" appears more than once in this document (only one value is allowed per field)"
argument_list|)
throw|;
block|}
name|pending
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|docsWithField
operator|=
name|FixedBitSet
operator|.
name|ensureCapacity
argument_list|(
name|docsWithField
argument_list|,
name|docID
argument_list|)
expr_stmt|;
name|docsWithField
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|updateBytesUsed
argument_list|()
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
DECL|method|updateBytesUsed
specifier|private
name|void
name|updateBytesUsed
parameter_list|()
block|{
specifier|final
name|long
name|newBytesUsed
init|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|docsWithField
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|newBytesUsed
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|newBytesUsed
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
name|maxDoc
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|DocValuesConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PackedLongValues
name|values
init|=
name|pending
operator|.
name|build
argument_list|()
decl_stmt|;
name|dvConsumer
operator|.
name|addNumericField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|EmptyDocValuesProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
if|if
condition|(
name|fieldInfo
operator|!=
name|NumericDocValuesWriter
operator|.
name|this
operator|.
name|fieldInfo
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong fieldInfo"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BufferedNumericDocValues
argument_list|(
name|values
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// iterates over the values we have in ram
DECL|class|BufferedNumericDocValues
specifier|private
specifier|static
class|class
name|BufferedNumericDocValues
extends|extends
name|NumericDocValues
block|{
DECL|field|iter
specifier|final
name|PackedLongValues
operator|.
name|Iterator
name|iter
decl_stmt|;
DECL|field|docsWithField
specifier|final
name|DocIdSetIterator
name|docsWithField
decl_stmt|;
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|method|BufferedNumericDocValues
name|BufferedNumericDocValues
parameter_list|(
name|PackedLongValues
name|values
parameter_list|,
name|FixedBitSet
name|docsWithFields
parameter_list|)
block|{
name|this
operator|.
name|iter
operator|=
name|values
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|docsWithField
operator|=
operator|new
name|BitSetIterator
argument_list|(
name|docsWithFields
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|docsWithField
operator|.
name|docID
argument_list|()
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
name|int
name|docID
init|=
name|docsWithField
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|value
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|docID
return|;
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|docsWithField
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|longValue
specifier|public
name|long
name|longValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
block|}
end_class

end_unit

