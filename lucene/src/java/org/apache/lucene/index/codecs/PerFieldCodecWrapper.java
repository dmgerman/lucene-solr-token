begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldsEnum
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
name|TermsEnum
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
name|Terms
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
name|FieldInfos
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
name|SegmentInfo
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
name|codecs
operator|.
name|docvalues
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
name|index
operator|.
name|values
operator|.
name|DocValues
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

begin_comment
comment|/** Simple Codec that dispatches field-specific codecs.  *  You must ensure every field you index has a Codec, or  *  the defaultCodec is non null.  Also, the separate  *  codecs cannot conflict on file names.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PerFieldCodecWrapper
specifier|public
class|class
name|PerFieldCodecWrapper
extends|extends
name|Codec
block|{
DECL|field|fields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Codec
argument_list|>
name|fields
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|String
argument_list|,
name|Codec
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|defaultCodec
specifier|private
specifier|final
name|Codec
name|defaultCodec
decl_stmt|;
DECL|method|PerFieldCodecWrapper
specifier|public
name|PerFieldCodecWrapper
parameter_list|(
name|Codec
name|defaultCodec
parameter_list|)
block|{
name|name
operator|=
literal|"PerField"
expr_stmt|;
name|this
operator|.
name|defaultCodec
operator|=
name|defaultCodec
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|field
parameter_list|,
name|Codec
name|codec
parameter_list|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Codec
name|codec
init|=
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
return|return
name|codec
return|;
block|}
else|else
block|{
return|return
name|defaultCodec
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|FieldsWriter
specifier|private
class|class
name|FieldsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|state
specifier|private
specifier|final
name|SegmentWriteState
name|state
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|Map
argument_list|<
name|Codec
argument_list|,
name|FieldsConsumer
argument_list|>
name|codecs
init|=
operator|new
name|HashMap
argument_list|<
name|Codec
argument_list|,
name|FieldsConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fieldsSeen
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsSeen
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldsWriter
specifier|public
name|FieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsSeen
operator|.
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|)
expr_stmt|;
name|Codec
name|codec
init|=
name|getCodec
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
name|FieldsConsumer
name|fields
init|=
name|codecs
operator|.
name|get
argument_list|(
name|codec
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|codec
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|codecs
operator|.
name|put
argument_list|(
name|codec
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
operator|.
name|addField
argument_list|(
name|field
argument_list|)
return|;
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
name|Iterator
argument_list|<
name|FieldsConsumer
argument_list|>
name|it
init|=
name|codecs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|it
operator|.
name|next
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// keep first IOException we hit but keep
comment|// closing the rest
if|if
condition|(
name|err
operator|==
literal|null
condition|)
block|{
name|err
operator|=
name|ioe
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
name|err
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsSeen
operator|.
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|)
expr_stmt|;
name|Codec
name|codec
init|=
name|getCodec
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
name|FieldsConsumer
name|fields
init|=
name|codecs
operator|.
name|get
argument_list|(
name|codec
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|codec
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|codecs
operator|.
name|put
argument_list|(
name|codec
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
operator|.
name|addValuesField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
DECL|class|FieldsReader
specifier|private
class|class
name|FieldsReader
extends|extends
name|FieldsProducer
block|{
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|Map
argument_list|<
name|Codec
argument_list|,
name|FieldsProducer
argument_list|>
name|codecs
init|=
operator|new
name|HashMap
argument_list|<
name|Codec
argument_list|,
name|FieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldsReader
specifier|public
name|FieldsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|fieldCount
init|=
name|fieldInfos
operator|.
name|size
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
name|Codec
name|codec
init|=
name|getCodec
argument_list|(
name|fi
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|codecs
operator|.
name|containsKey
argument_list|(
name|codec
argument_list|)
condition|)
block|{
name|codecs
operator|.
name|put
argument_list|(
name|codec
argument_list|,
name|codec
operator|.
name|fieldsProducer
argument_list|(
operator|new
name|SegmentReadState
argument_list|(
name|dir
argument_list|,
name|si
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
name|indexDivisor
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|FieldsIterator
specifier|private
specifier|final
class|class
name|FieldsIterator
extends|extends
name|FieldsEnum
block|{
DECL|field|it
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
decl_stmt|;
DECL|field|current
specifier|private
name|String
name|current
decl_stmt|;
DECL|method|FieldsIterator
specifier|public
name|FieldsIterator
parameter_list|()
block|{
name|it
operator|=
name|fields
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|codecs
operator|.
name|get
argument_list|(
name|getCodec
argument_list|(
name|current
argument_list|)
argument_list|)
operator|.
name|terms
argument_list|(
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
return|return
name|terms
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|codecs
operator|.
name|get
argument_list|(
name|getCodec
argument_list|(
name|current
argument_list|)
argument_list|)
operator|.
name|docValues
argument_list|(
name|current
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Codec
name|codec
init|=
name|getCodec
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldsProducer
name|fields
init|=
name|codecs
operator|.
name|get
argument_list|(
name|codec
argument_list|)
decl_stmt|;
assert|assert
name|fields
operator|!=
literal|null
assert|;
return|return
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
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
name|Iterator
argument_list|<
name|FieldsProducer
argument_list|>
name|it
init|=
name|codecs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|it
operator|.
name|next
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// keep first IOException we hit but keep
comment|// closing the rest
if|if
condition|(
name|err
operator|==
literal|null
condition|)
block|{
name|err
operator|=
name|ioe
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
name|err
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadTermsIndex
specifier|public
name|void
name|loadTermsIndex
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|FieldsProducer
argument_list|>
name|it
init|=
name|codecs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
operator|.
name|loadTermsIndex
argument_list|(
name|indexDivisor
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Codec
name|codec
init|=
name|getCodec
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldsProducer
name|fields
init|=
name|codecs
operator|.
name|get
argument_list|(
name|codec
argument_list|)
decl_stmt|;
assert|assert
name|fields
operator|!=
literal|null
assert|;
return|return
name|fields
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|Codec
argument_list|>
name|it
init|=
name|fields
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Codec
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|Codec
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Codec
name|codec
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|seen
operator|.
name|contains
argument_list|(
name|codec
argument_list|)
condition|)
block|{
name|seen
operator|.
name|add
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|codec
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Codec
argument_list|>
name|it
init|=
name|fields
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Codec
name|codec
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|codec
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

