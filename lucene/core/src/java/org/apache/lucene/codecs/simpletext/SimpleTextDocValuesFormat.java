begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Plain-text DocValues format.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextDocValuesFormat
specifier|public
class|class
name|SimpleTextDocValuesFormat
extends|extends
name|DocValuesFormat
block|{
DECL|field|DOC_VALUES_SEG_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|DOC_VALUES_SEG_SUFFIX
init|=
literal|"dv"
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
name|SimpleTextPerDocConsumer
argument_list|(
name|state
argument_list|,
name|DOC_VALUES_SEG_SUFFIX
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
name|SimpleTextPerDocProducer
argument_list|(
name|state
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|,
name|DOC_VALUES_SEG_SUFFIX
argument_list|)
return|;
block|}
DECL|method|docValuesId
specifier|static
name|String
name|docValuesId
parameter_list|(
name|String
name|segmentsName
parameter_list|,
name|int
name|fieldId
parameter_list|)
block|{
return|return
name|segmentsName
operator|+
literal|"_"
operator|+
name|fieldId
return|;
block|}
block|}
end_class

end_unit

