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
name|SegmentWriteState
import|;
end_import

begin_comment
comment|/**  * Read-write version of 4.0 norms format for testing  * @deprecated for test purposes only  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40RWNormsFormat
specifier|public
specifier|final
class|class
name|Lucene40RWNormsFormat
extends|extends
name|Lucene40NormsFormat
block|{
annotation|@
name|Override
DECL|method|normsConsumer
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
name|String
name|filename
init|=
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
literal|"nrm"
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|Lucene40DocValuesWriter
name|impl
init|=
operator|new
name|Lucene40DocValuesWriter
argument_list|(
name|state
argument_list|,
name|filename
argument_list|,
name|Lucene40FieldInfosReader
operator|.
name|LEGACY_NORM_TYPE_KEY
argument_list|)
decl_stmt|;
return|return
operator|new
name|NormsConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|addNormsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|addNumericField
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|impl
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

