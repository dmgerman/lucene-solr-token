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
name|SegmentInfoReader
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
name|SegmentInfoWriter
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

begin_comment
comment|/**  * Lucene 4.0 Segment info format.  * @deprecated Only for reading old 4.0-4.5 segments  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40SegmentInfoFormat
specifier|public
class|class
name|Lucene40SegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|SegmentInfoReader
name|reader
init|=
operator|new
name|Lucene40SegmentInfoReader
argument_list|()
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Lucene40SegmentInfoFormat
specifier|public
name|Lucene40SegmentInfoFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getSegmentInfoReader
specifier|public
specifier|final
name|SegmentInfoReader
name|getSegmentInfoReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentInfoWriter
specifier|public
name|SegmentInfoWriter
name|getSegmentInfoWriter
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
comment|/** File extension used to store {@link SegmentInfo}. */
DECL|field|SI_EXTENSION
specifier|static
specifier|final
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene40SegmentInfo"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
block|}
end_class

end_unit

