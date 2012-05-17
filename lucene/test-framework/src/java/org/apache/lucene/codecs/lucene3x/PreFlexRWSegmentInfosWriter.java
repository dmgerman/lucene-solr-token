begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|SegmentInfosWriter
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
name|ChecksumIndexOutput
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
name|FlushInfo
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
name|store
operator|.
name|IndexOutput
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * PreFlex implementation of {@link SegmentInfosWriter}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PreFlexRWSegmentInfosWriter
class|class
name|PreFlexRWSegmentInfosWriter
extends|extends
name|SegmentInfosWriter
block|{
annotation|@
name|Override
DECL|method|writeInfos
specifier|public
name|IndexOutput
name|writeInfos
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentFileName
parameter_list|,
name|String
name|codecID
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
name|IndexOutput
name|out
init|=
name|createOutput
argument_list|(
name|dir
argument_list|,
name|segmentFileName
argument_list|,
operator|new
name|IOContext
argument_list|(
operator|new
name|FlushInfo
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|,
name|infos
operator|.
name|totalDocCount
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|SegmentInfos
operator|.
name|FORMAT_3_1
argument_list|)
expr_stmt|;
comment|// write FORMAT
comment|// we don't write a codec - this is 3.x
name|out
operator|.
name|writeLong
argument_list|(
name|infos
operator|.
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|infos
operator|.
name|counter
argument_list|)
expr_stmt|;
comment|// write counter
name|out
operator|.
name|writeInt
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// write infos
for|for
control|(
name|SegmentInfo
name|si
range|:
name|infos
control|)
block|{
name|writeInfo
argument_list|(
name|out
argument_list|,
name|si
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeStringStringMap
argument_list|(
name|infos
operator|.
name|getUserData
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|out
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Save a single segment's info. */
DECL|method|writeInfo
specifier|private
name|void
name|writeInfo
parameter_list|(
name|IndexOutput
name|output
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we are about to write this SI in 3.x format, dropping all codec information, etc.
comment|// so it had better be a 3.x segment or you will get very confusing errors later.
assert|assert
name|si
operator|.
name|getCodec
argument_list|()
operator|instanceof
name|Lucene3xCodec
operator|:
literal|"broken test, trying to mix preflex with other codecs"
assert|;
assert|assert
name|si
operator|.
name|getDelCount
argument_list|()
operator|<=
name|si
operator|.
name|docCount
operator|:
literal|"delCount="
operator|+
name|si
operator|.
name|getDelCount
argument_list|()
operator|+
literal|" docCount="
operator|+
name|si
operator|.
name|docCount
operator|+
literal|" segment="
operator|+
name|si
operator|.
name|name
assert|;
comment|// Write the Lucene version that created this segment, since 3.1
name|output
operator|.
name|writeString
argument_list|(
name|si
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|si
operator|.
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|si
operator|.
name|getDelGen
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|si
operator|.
name|getDocStoreOffset
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|output
operator|.
name|writeString
argument_list|(
name|si
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|si
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|?
literal|1
else|:
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// pre-4.0 indexes write a byte if there is a single norms file
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|normGen
init|=
name|si
operator|.
name|getNormGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|normGen
operator|==
literal|null
condition|)
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|SegmentInfo
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|normGen
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|normGen
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|output
operator|.
name|writeLong
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|?
name|SegmentInfo
operator|.
name|YES
else|:
name|SegmentInfo
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|getDelCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// hasProx:
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|si
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
comment|// hasVectors:
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|createOutput
specifier|protected
name|IndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentFileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|plainOut
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|segmentFileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|ChecksumIndexOutput
name|out
init|=
operator|new
name|ChecksumIndexOutput
argument_list|(
name|plainOut
argument_list|)
decl_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|(
name|IndexOutput
name|segmentOutput
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|ChecksumIndexOutput
operator|)
name|segmentOutput
operator|)
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishCommit
specifier|public
name|void
name|finishCommit
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|ChecksumIndexOutput
operator|)
name|out
operator|)
operator|.
name|finishCommit
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

