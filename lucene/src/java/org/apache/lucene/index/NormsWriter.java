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
name|search
operator|.
name|Similarity
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

begin_comment
comment|// TODO FI: norms could actually be stored as doc store
end_comment

begin_comment
comment|/** Writes norms.  Each thread X field accumulates the norms  *  for the doc/fields it saw, then the flush method below  *  merges all of these together into a single _X.nrm file.  */
end_comment

begin_class
DECL|class|NormsWriter
specifier|final
class|class
name|NormsWriter
extends|extends
name|InvertedDocEndConsumer
block|{
DECL|field|defaultNorm
specifier|private
specifier|static
specifier|final
name|byte
name|defaultNorm
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
operator|.
name|encodeNormValue
argument_list|(
literal|1.0f
argument_list|)
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{}
comment|// We only write the _X.nrm file at flush
DECL|method|files
name|void
name|files
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|setFieldInfos
name|void
name|setFieldInfos
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
block|}
comment|/** Produce _X.nrm if any document had a field with norms    *  not disabled */
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocEndConsumerPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|normsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|NORMS_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|normsFileName
argument_list|)
expr_stmt|;
name|IndexOutput
name|normsOut
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|normsFileName
argument_list|)
decl_stmt|;
try|try
block|{
name|normsOut
operator|.
name|writeBytes
argument_list|(
name|SegmentMerger
operator|.
name|NORMS_HEADER
argument_list|,
literal|0
argument_list|,
name|SegmentMerger
operator|.
name|NORMS_HEADER
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numField
init|=
name|fieldInfos
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|normCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|fieldNumber
init|=
literal|0
init|;
name|fieldNumber
operator|<
name|numField
condition|;
name|fieldNumber
operator|++
control|)
block|{
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
name|NormsWriterPerField
name|toWrite
init|=
operator|(
name|NormsWriterPerField
operator|)
name|fieldsToFlush
operator|.
name|get
argument_list|(
name|fieldInfo
argument_list|)
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|toWrite
operator|!=
literal|null
operator|&&
name|toWrite
operator|.
name|upto
operator|>
literal|0
condition|)
block|{
name|normCount
operator|++
expr_stmt|;
name|int
name|docID
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|docID
operator|<
name|state
operator|.
name|numDocs
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|upto
operator|<
name|toWrite
operator|.
name|upto
operator|&&
name|toWrite
operator|.
name|docIDs
index|[
name|upto
index|]
operator|==
name|docID
condition|)
block|{
name|normsOut
operator|.
name|writeByte
argument_list|(
name|toWrite
operator|.
name|norms
index|[
name|upto
index|]
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
name|normsOut
operator|.
name|writeByte
argument_list|(
name|defaultNorm
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we should have consumed every norm
assert|assert
name|upto
operator|==
name|toWrite
operator|.
name|upto
assert|;
name|toWrite
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldInfo
operator|.
name|isIndexed
operator|&&
operator|!
name|fieldInfo
operator|.
name|omitNorms
condition|)
block|{
name|normCount
operator|++
expr_stmt|;
comment|// Fill entire field with default norm:
for|for
control|(
init|;
name|upto
operator|<
name|state
operator|.
name|numDocs
condition|;
name|upto
operator|++
control|)
name|normsOut
operator|.
name|writeByte
argument_list|(
name|defaultNorm
argument_list|)
expr_stmt|;
block|}
assert|assert
literal|4
operator|+
name|normCount
operator|*
name|state
operator|.
name|numDocs
operator|==
name|normsOut
operator|.
name|getFilePointer
argument_list|()
operator|:
literal|".nrm file size mismatch: expected="
operator|+
operator|(
literal|4
operator|+
name|normCount
operator|*
name|state
operator|.
name|numDocs
operator|)
operator|+
literal|" actual="
operator|+
name|normsOut
operator|.
name|getFilePointer
argument_list|()
assert|;
block|}
block|}
finally|finally
block|{
name|normsOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|addField
name|InvertedDocEndConsumerPerField
name|addField
parameter_list|(
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|NormsWriterPerField
argument_list|(
name|docInverterPerField
argument_list|,
name|fieldInfo
argument_list|)
return|;
block|}
block|}
end_class

end_unit

