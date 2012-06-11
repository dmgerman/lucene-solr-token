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
name|Closeable
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|PerDocProducerBase
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
name|values
operator|.
name|Bytes
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
name|values
operator|.
name|Floats
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
name|values
operator|.
name|Ints
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
name|index
operator|.
name|DocValues
operator|.
name|Type
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Lucene 4.0 PerDocProducer implementation that uses compound file.  *   * @see Lucene40DocValuesFormat  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene40DocValuesProducer
specifier|public
class|class
name|Lucene40DocValuesProducer
extends|extends
name|PerDocProducerBase
block|{
DECL|field|docValues
specifier|protected
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
decl_stmt|;
DECL|field|cfs
specifier|private
specifier|final
name|Directory
name|cfs
decl_stmt|;
comment|/**    * Creates a new {@link Lucene40DocValuesProducer} instance and loads all    * {@link DocValues} instances for this segment and codec.    */
DECL|method|Lucene40DocValuesProducer
specifier|public
name|Lucene40DocValuesProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|anyDocValuesFields
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|)
condition|)
block|{
name|cfs
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|state
operator|.
name|dir
argument_list|,
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
name|segmentSuffix
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|,
name|state
operator|.
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docValues
operator|=
name|load
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|cfs
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cfs
operator|=
literal|null
expr_stmt|;
name|docValues
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
annotation|@
name|Override
DECL|method|closeInternal
specifier|protected
name|void
name|closeInternal
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|closeables
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cfs
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
argument_list|(
name|closeables
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|cfs
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|closeables
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadDocValues
specifier|protected
name|DocValues
name|loadDocValues
parameter_list|(
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Type
name|type
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|VAR_INTS
case|:
return|return
name|Ints
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|type
argument_list|,
name|context
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
return|return
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
case|case
name|FLOAT_64
case|:
return|return
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unrecognized index values mode "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

