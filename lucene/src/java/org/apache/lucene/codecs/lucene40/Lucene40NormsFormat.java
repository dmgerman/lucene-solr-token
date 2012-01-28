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
name|Set
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
name|PerDocProducer
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
name|IndexReader
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
name|Directory
import|;
end_import

begin_comment
comment|/**  * Norms Format for the default codec.   * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene40NormsFormat
specifier|public
class|class
name|Lucene40NormsFormat
extends|extends
name|NormsFormat
block|{
DECL|field|NORMS_SEGMENT_SUFFIX
specifier|private
specifier|final
specifier|static
name|String
name|NORMS_SEGMENT_SUFFIX
init|=
literal|"nrm"
decl_stmt|;
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40NormsDocValuesConsumer
argument_list|(
name|state
argument_list|,
name|NORMS_SEGMENT_SUFFIX
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocProducer
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40NormsDocValuesProducer
argument_list|(
name|state
argument_list|,
name|NORMS_SEGMENT_SUFFIX
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
name|Lucene40NormsDocValuesConsumer
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
DECL|class|Lucene40NormsDocValuesProducer
specifier|public
specifier|static
class|class
name|Lucene40NormsDocValuesProducer
extends|extends
name|Lucene40DocValuesProducer
block|{
DECL|method|Lucene40NormsDocValuesProducer
specifier|public
name|Lucene40NormsDocValuesProducer
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
name|super
argument_list|(
name|state
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canLoad
specifier|protected
name|boolean
name|canLoad
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|normsPresent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getNormType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|anyDocValuesFields
specifier|protected
name|boolean
name|anyDocValuesFields
parameter_list|(
name|FieldInfos
name|infos
parameter_list|)
block|{
return|return
name|infos
operator|.
name|hasNorms
argument_list|()
return|;
block|}
block|}
DECL|class|Lucene40NormsDocValuesConsumer
specifier|public
specifier|static
class|class
name|Lucene40NormsDocValuesConsumer
extends|extends
name|Lucene40DocValuesConsumer
block|{
DECL|method|Lucene40NormsDocValuesConsumer
specifier|public
name|Lucene40NormsDocValuesConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|state
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesForMerge
specifier|protected
name|DocValues
name|getDocValuesForMerge
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|FieldInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|normValues
argument_list|(
name|info
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|canMerge
specifier|protected
name|boolean
name|canMerge
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|normsPresent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getNormType
argument_list|()
return|;
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
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
specifier|final
name|String
name|normsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|NORMS_SEGMENT_SUFFIX
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
name|segmentInfo
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|normsPresent
argument_list|()
condition|)
block|{
specifier|final
name|String
name|normsEntriesFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|NORMS_SEGMENT_SUFFIX
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|normsFileName
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|normsEntriesFileName
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

