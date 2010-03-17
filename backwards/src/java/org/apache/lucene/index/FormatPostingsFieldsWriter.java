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

begin_class
DECL|class|FormatPostingsFieldsWriter
specifier|final
class|class
name|FormatPostingsFieldsWriter
extends|extends
name|FormatPostingsFieldsConsumer
block|{
DECL|field|dir
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|segment
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|termsOut
specifier|final
name|TermInfosWriter
name|termsOut
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|termsWriter
specifier|final
name|FormatPostingsTermsWriter
name|termsWriter
decl_stmt|;
DECL|field|skipListWriter
specifier|final
name|DefaultSkipListWriter
name|skipListWriter
decl_stmt|;
DECL|field|totalNumDocs
specifier|final
name|int
name|totalNumDocs
decl_stmt|;
DECL|method|FormatPostingsFieldsWriter
specifier|public
name|FormatPostingsFieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|dir
operator|=
name|state
operator|.
name|directory
expr_stmt|;
name|segment
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|totalNumDocs
operator|=
name|state
operator|.
name|numDocs
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|termsOut
operator|=
operator|new
name|TermInfosWriter
argument_list|(
name|dir
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|,
name|state
operator|.
name|termIndexInterval
argument_list|)
expr_stmt|;
comment|// TODO: this is a nasty abstraction violation (that we
comment|// peek down to find freqOut/proxOut) -- we need a
comment|// better abstraction here whereby these child consumers
comment|// can provide skip data or not
name|skipListWriter
operator|=
operator|new
name|DefaultSkipListWriter
argument_list|(
name|termsOut
operator|.
name|skipInterval
argument_list|,
name|termsOut
operator|.
name|maxSkipLevels
argument_list|,
name|totalNumDocs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|termsWriter
operator|=
operator|new
name|FormatPostingsTermsWriter
argument_list|(
name|state
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|/** Add a new field */
annotation|@
name|Override
DECL|method|addField
name|FormatPostingsTermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
name|termsWriter
operator|.
name|setField
argument_list|(
name|field
argument_list|)
expr_stmt|;
return|return
name|termsWriter
return|;
block|}
comment|/** Called when we are done adding everything. */
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|termsOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|termsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

