begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene45
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene45
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
name|Lucene40RWSegmentInfoFormat
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
name|Lucene41RWStoredFieldsFormat
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
name|Lucene42RWFieldInfosFormat
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
name|Lucene42RWNormsFormat
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
name|Lucene42RWTermVectorsFormat
import|;
end_import

begin_comment
comment|/**  * Read-write version of {@link Lucene45Codec} for testing.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|Lucene45RWCodec
specifier|public
specifier|final
class|class
name|Lucene45RWCodec
extends|extends
name|Lucene45Codec
block|{
DECL|field|fieldInfosFormat
specifier|private
specifier|static
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
init|=
operator|new
name|Lucene42RWFieldInfosFormat
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
name|fieldInfosFormat
return|;
block|}
DECL|field|docValues
specifier|private
specifier|static
specifier|final
name|DocValuesFormat
name|docValues
init|=
operator|new
name|Lucene45RWDocValuesFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
name|docValues
return|;
block|}
DECL|field|norms
specifier|private
specifier|static
specifier|final
name|NormsFormat
name|norms
init|=
operator|new
name|Lucene42RWNormsFormat
argument_list|()
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
name|norms
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
DECL|field|storedFields
specifier|private
specifier|static
specifier|final
name|StoredFieldsFormat
name|storedFields
init|=
operator|new
name|Lucene41RWStoredFieldsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|storedFields
return|;
block|}
DECL|field|vectorsFormat
specifier|private
specifier|final
name|TermVectorsFormat
name|vectorsFormat
init|=
operator|new
name|Lucene42RWTermVectorsFormat
argument_list|()
decl_stmt|;
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
block|}
end_class

end_unit

