begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|Codec
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
name|LiveDocsFormat
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
name|NormsConsumer
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
name|Lucene40LiveDocsFormat
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
name|lucene41
operator|.
name|Lucene41StoredFieldsFormat
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
name|lucene42
operator|.
name|Lucene42TermVectorsFormat
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
name|lucene46
operator|.
name|Lucene46FieldInfosFormat
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
name|lucene46
operator|.
name|Lucene46SegmentInfoFormat
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
name|perfield
operator|.
name|PerFieldDocValuesFormat
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
name|perfield
operator|.
name|PerFieldPostingsFormat
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

begin_comment
comment|/**  * Implements the Lucene 4.9 index format  * @deprecated only for old 4.x segments  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene49Codec
specifier|public
class|class
name|Lucene49Codec
extends|extends
name|Codec
block|{
DECL|field|fieldsFormat
specifier|private
specifier|final
name|StoredFieldsFormat
name|fieldsFormat
init|=
operator|new
name|Lucene41StoredFieldsFormat
argument_list|()
decl_stmt|;
DECL|field|vectorsFormat
specifier|private
specifier|final
name|TermVectorsFormat
name|vectorsFormat
init|=
operator|new
name|Lucene42TermVectorsFormat
argument_list|()
decl_stmt|;
DECL|field|fieldInfosFormat
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
init|=
operator|new
name|Lucene46FieldInfosFormat
argument_list|()
decl_stmt|;
DECL|field|segmentInfosFormat
specifier|private
specifier|final
name|SegmentInfoFormat
name|segmentInfosFormat
init|=
operator|new
name|Lucene46SegmentInfoFormat
argument_list|()
decl_stmt|;
DECL|field|liveDocsFormat
specifier|private
specifier|final
name|LiveDocsFormat
name|liveDocsFormat
init|=
operator|new
name|Lucene40LiveDocsFormat
argument_list|()
decl_stmt|;
DECL|field|postingsFormat
specifier|private
specifier|final
name|PostingsFormat
name|postingsFormat
init|=
operator|new
name|PerFieldPostingsFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|Lucene49Codec
operator|.
name|this
operator|.
name|getPostingsFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|docValuesFormat
specifier|private
specifier|final
name|DocValuesFormat
name|docValuesFormat
init|=
operator|new
name|PerFieldDocValuesFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|Lucene49Codec
operator|.
name|this
operator|.
name|getDocValuesFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Lucene49Codec
specifier|public
name|Lucene49Codec
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene49"
argument_list|)
expr_stmt|;
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
name|fieldsFormat
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
name|vectorsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
specifier|final
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|postingsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|fieldInfosFormat
return|;
block|}
annotation|@
name|Override
DECL|method|segmentInfoFormat
specifier|public
name|SegmentInfoFormat
name|segmentInfoFormat
parameter_list|()
block|{
return|return
name|segmentInfosFormat
return|;
block|}
annotation|@
name|Override
DECL|method|liveDocsFormat
specifier|public
specifier|final
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
block|{
return|return
name|liveDocsFormat
return|;
block|}
comment|/** Returns the postings format that should be used for writing     *  new segments of<code>field</code>.    *      *  The default implementation always returns "Lucene41"    */
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
name|defaultFormat
return|;
block|}
comment|/** Returns the docvalues format that should be used for writing     *  new segments of<code>field</code>.    *      *  The default implementation always returns "Lucene49"    */
DECL|method|getDocValuesFormatForField
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|defaultDVFormat
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
specifier|final
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|docValuesFormat
return|;
block|}
DECL|field|defaultFormat
specifier|private
specifier|final
name|PostingsFormat
name|defaultFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Lucene41"
argument_list|)
decl_stmt|;
DECL|field|defaultDVFormat
specifier|private
specifier|final
name|DocValuesFormat
name|defaultDVFormat
init|=
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Lucene49"
argument_list|)
decl_stmt|;
DECL|field|normsFormat
specifier|private
specifier|final
name|NormsFormat
name|normsFormat
init|=
operator|new
name|Lucene49NormsFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|normsFormat
return|;
block|}
block|}
end_class

end_unit

