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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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

begin_comment
comment|/** This is a DocFieldConsumer that inverts each field,  *  separately, from a Document, and accepts a  *  InvertedTermsConsumer to process those terms. */
end_comment

begin_class
DECL|class|DocInverter
specifier|final
class|class
name|DocInverter
extends|extends
name|DocFieldConsumer
block|{
DECL|field|consumer
specifier|final
name|InvertedDocConsumer
name|consumer
decl_stmt|;
DECL|field|endConsumer
specifier|final
name|InvertedDocEndConsumer
name|endConsumer
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
init|=
operator|new
name|FieldInvertState
argument_list|()
decl_stmt|;
DECL|field|singleToken
specifier|final
name|SingleTokenAttributeSource
name|singleToken
init|=
operator|new
name|SingleTokenAttributeSource
argument_list|()
decl_stmt|;
DECL|class|SingleTokenAttributeSource
specifier|static
class|class
name|SingleTokenAttributeSource
extends|extends
name|AttributeSource
block|{
DECL|field|termAttribute
specifier|final
name|CharTermAttribute
name|termAttribute
decl_stmt|;
DECL|field|offsetAttribute
specifier|final
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
DECL|method|SingleTokenAttributeSource
specifier|private
name|SingleTokenAttributeSource
parameter_list|()
block|{
name|termAttribute
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|String
name|stringValue
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Used to read a string value for a field
DECL|field|stringReader
specifier|final
name|ReusableStringReader
name|stringReader
init|=
operator|new
name|ReusableStringReader
argument_list|()
decl_stmt|;
DECL|method|DocInverter
specifier|public
name|DocInverter
parameter_list|(
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
parameter_list|,
name|InvertedDocConsumer
name|consumer
parameter_list|,
name|InvertedDocEndConsumer
name|endConsumer
parameter_list|)
block|{
name|this
operator|.
name|docState
operator|=
name|docState
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|endConsumer
operator|=
name|endConsumer
expr_stmt|;
block|}
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
name|super
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|endConsumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|DocFieldConsumerPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocConsumerPerField
argument_list|>
name|childFieldsToFlush
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocConsumerPerField
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocEndConsumerPerField
argument_list|>
name|endChildFieldsToFlush
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocEndConsumerPerField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|DocFieldConsumerPerField
argument_list|>
name|fieldToFlush
range|:
name|fieldsToFlush
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DocInverterPerField
name|perField
init|=
operator|(
name|DocInverterPerField
operator|)
name|fieldToFlush
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|childFieldsToFlush
operator|.
name|put
argument_list|(
name|fieldToFlush
operator|.
name|getKey
argument_list|()
argument_list|,
name|perField
operator|.
name|consumer
argument_list|)
expr_stmt|;
name|endChildFieldsToFlush
operator|.
name|put
argument_list|(
name|fieldToFlush
operator|.
name|getKey
argument_list|()
argument_list|,
name|perField
operator|.
name|endConsumer
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|flush
argument_list|(
name|childFieldsToFlush
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|endConsumer
operator|.
name|flush
argument_list|(
name|endChildFieldsToFlush
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
specifier|public
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: allow endConsumer.finishDocument to also return
comment|// a DocWriter
name|endConsumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|endConsumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
return|return
name|consumer
operator|.
name|freeRAM
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|DocFieldConsumerPerField
name|addField
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
return|return
operator|new
name|DocInverterPerField
argument_list|(
name|this
argument_list|,
name|fi
argument_list|)
return|;
block|}
block|}
end_class

end_unit

