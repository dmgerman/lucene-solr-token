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
name|InfoStream
import|;
end_import

begin_comment
comment|/**  * Encapsulates all necessary state to initiate a {@link PerDocConsumer} and  * create all necessary files in order to consume and merge per-document values.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|PerDocWriteState
specifier|public
class|class
name|PerDocWriteState
block|{
DECL|field|infoStream
specifier|public
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
DECL|field|directory
specifier|public
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segmentName
specifier|public
specifier|final
name|String
name|segmentName
decl_stmt|;
DECL|field|bytesUsed
specifier|public
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|field|segmentSuffix
specifier|public
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
DECL|field|context
specifier|public
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|method|PerDocWriteState
specifier|public
name|PerDocWriteState
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentName
operator|=
name|segmentName
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|PerDocWriteState
specifier|public
name|PerDocWriteState
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
name|infoStream
operator|=
name|state
operator|.
name|infoStream
expr_stmt|;
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
name|segmentName
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|segmentSuffix
operator|=
name|state
operator|.
name|segmentSuffix
expr_stmt|;
name|bytesUsed
operator|=
name|Counter
operator|.
name|newCounter
argument_list|()
expr_stmt|;
name|context
operator|=
name|state
operator|.
name|context
expr_stmt|;
block|}
DECL|method|PerDocWriteState
specifier|public
name|PerDocWriteState
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|state
operator|.
name|infoStream
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
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
name|segmentSuffix
operator|=
name|segmentSuffix
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
block|}
block|}
end_class

end_unit

