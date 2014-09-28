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
name|DocValuesFormat
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
name|FieldInfosFormat
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
name|FieldInfosWriter
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
name|NormsFormat
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
name|PostingsFormat
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
name|SegmentInfoFormat
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
name|StoredFieldsFormat
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
name|TermVectorsFormat
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Read-write version of 4.0 codec for testing  * @deprecated for test purposes only  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40RWCodec
specifier|public
specifier|final
class|class
name|Lucene40RWCodec
extends|extends
name|Lucene40Codec
block|{
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfos
init|=
operator|new
name|Lucene40FieldInfosFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FieldInfosWriter
name|getFieldInfosWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40FieldInfosWriter
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|DocValuesFormat
name|docValues
init|=
operator|new
name|Lucene40RWDocValuesFormat
argument_list|()
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|NormsFormat
name|norms
init|=
operator|new
name|Lucene40RWNormsFormat
argument_list|()
decl_stmt|;
DECL|field|stored
specifier|private
specifier|final
name|StoredFieldsFormat
name|stored
init|=
operator|new
name|Lucene40RWStoredFieldsFormat
argument_list|()
decl_stmt|;
DECL|field|vectors
specifier|private
specifier|final
name|TermVectorsFormat
name|vectors
init|=
operator|new
name|Lucene40RWTermVectorsFormat
argument_list|()
decl_stmt|;
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
operator|new
name|Lucene40RWPostingsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|norms
return|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|stored
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|vectors
return|;
block|}
annotation|@
name|Override
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|postings
return|;
block|}
DECL|field|segmentInfos
specifier|private
specifier|static
specifier|final
name|SegmentInfoFormat
name|segmentInfos
init|=
operator|new
name|Lucene40RWSegmentInfoFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|segmentInfoFormat
specifier|public
name|SegmentInfoFormat
name|segmentInfoFormat
parameter_list|()
block|{
return|return
name|segmentInfos
return|;
block|}
block|}
end_class

end_unit

