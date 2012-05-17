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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SegmentInfosReader
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
name|SegmentInfos
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
name|ChecksumIndexInput
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

begin_comment
comment|/**  * Lucene 4.0 implementation of {@link SegmentInfosReader}.  *   * @see Lucene40SegmentInfosFormat  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene40SegmentInfosReader
specifier|public
class|class
name|Lucene40SegmentInfosReader
extends|extends
name|SegmentInfosReader
block|{
annotation|@
name|Override
DECL|method|read
specifier|public
name|void
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentsFileName
parameter_list|,
name|ChecksumIndexInput
name|input
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|infos
operator|.
name|version
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read version
name|infos
operator|.
name|counter
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read counter
specifier|final
name|int
name|format
init|=
name|infos
operator|.
name|getFormat
argument_list|()
decl_stmt|;
assert|assert
name|format
operator|<=
name|SegmentInfos
operator|.
name|FORMAT_4_0
assert|;
for|for
control|(
name|int
name|i
init|=
name|input
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// read segmentInfos
name|SegmentInfo
name|si
init|=
name|readSegmentInfo
argument_list|(
name|directory
argument_list|,
name|format
argument_list|,
name|input
argument_list|)
decl_stmt|;
assert|assert
name|si
operator|.
name|getVersion
argument_list|()
operator|!=
literal|null
assert|;
name|infos
operator|.
name|add
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
name|infos
operator|.
name|userData
operator|=
name|input
operator|.
name|readStringStringMap
argument_list|()
expr_stmt|;
block|}
DECL|method|readSegmentInfo
specifier|public
name|SegmentInfo
name|readSegmentInfo
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|format
parameter_list|,
name|ChecksumIndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|version
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|delGen
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
comment|// this is still written in 4.0 if we open a 3.x and upgrade the SI
specifier|final
name|int
name|docStoreOffset
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|String
name|docStoreSegment
decl_stmt|;
specifier|final
name|boolean
name|docStoreIsCompoundFile
decl_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|docStoreSegment
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|SegmentInfo
operator|.
name|YES
expr_stmt|;
block|}
else|else
block|{
name|docStoreSegment
operator|=
name|name
expr_stmt|;
name|docStoreIsCompoundFile
operator|=
literal|false
expr_stmt|;
block|}
specifier|final
name|int
name|numNormGen
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
decl_stmt|;
if|if
condition|(
name|numNormGen
operator|==
name|SegmentInfo
operator|.
name|NO
condition|)
block|{
name|normGen
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|normGen
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numNormGen
condition|;
name|j
operator|++
control|)
block|{
name|normGen
operator|.
name|put
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|,
name|input
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|isCompoundFile
init|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|SegmentInfo
operator|.
name|YES
decl_stmt|;
specifier|final
name|int
name|delCount
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
assert|assert
name|delCount
operator|<=
name|docCount
assert|;
specifier|final
name|Codec
name|codec
init|=
name|Codec
operator|.
name|forName
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|input
operator|.
name|readStringStringMap
argument_list|()
decl_stmt|;
return|return
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|version
argument_list|,
name|name
argument_list|,
name|docCount
argument_list|,
name|delGen
argument_list|,
name|docStoreOffset
argument_list|,
name|docStoreSegment
argument_list|,
name|docStoreIsCompoundFile
argument_list|,
name|normGen
argument_list|,
name|isCompoundFile
argument_list|,
name|delCount
argument_list|,
name|codec
argument_list|,
name|diagnostics
argument_list|)
return|;
block|}
block|}
end_class

end_unit

