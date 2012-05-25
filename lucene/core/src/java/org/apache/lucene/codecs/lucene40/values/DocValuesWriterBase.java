begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40.values
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
name|Comparator
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
name|PerDocConsumer
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
name|Writer
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
name|PerDocWriteState
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

begin_comment
comment|// javadoc
end_comment

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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * Abstract base class for PerDocConsumer implementations  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesWriterBase
specifier|public
specifier|abstract
class|class
name|DocValuesWriterBase
extends|extends
name|PerDocConsumer
block|{
DECL|field|segmentName
specifier|protected
specifier|final
name|String
name|segmentName
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|field|context
specifier|protected
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|field|acceptableOverheadRatio
specifier|private
specifier|final
name|float
name|acceptableOverheadRatio
decl_stmt|;
comment|/**    * Filename extension for index files    */
DECL|field|INDEX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_EXTENSION
init|=
literal|"idx"
decl_stmt|;
comment|/**    * Filename extension for data files.    */
DECL|field|DATA_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dat"
decl_stmt|;
comment|/**    * @param state The state to initiate a {@link PerDocConsumer} instance    */
DECL|method|DocValuesWriterBase
specifier|protected
name|DocValuesWriterBase
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|state
argument_list|,
name|PackedInts
operator|.
name|FAST
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param state The state to initiate a {@link PerDocConsumer} instance    * @param acceptableOverheadRatio    *          how to trade space for speed. This option is only applicable for    *          docvalues of type {@link Type#BYTES_FIXED_SORTED} and    *          {@link Type#BYTES_VAR_SORTED}.    * @see PackedInts#getReader(org.apache.lucene.store.DataInput)    */
DECL|method|DocValuesWriterBase
specifier|protected
name|DocValuesWriterBase
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|this
operator|.
name|segmentName
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|state
operator|.
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|state
operator|.
name|context
expr_stmt|;
name|this
operator|.
name|acceptableOverheadRatio
operator|=
name|acceptableOverheadRatio
expr_stmt|;
block|}
DECL|method|getDirectory
specifier|protected
specifier|abstract
name|Directory
name|getDirectory
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|Type
name|valueType
parameter_list|,
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Writer
operator|.
name|create
argument_list|(
name|valueType
argument_list|,
name|PerDocProducerBase
operator|.
name|docValuesId
argument_list|(
name|segmentName
argument_list|,
name|field
operator|.
name|number
argument_list|)
argument_list|,
name|getDirectory
argument_list|()
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
block|}
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

