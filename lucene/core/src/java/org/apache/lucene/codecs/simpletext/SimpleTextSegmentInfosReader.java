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
name|CorruptIndexException
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
name|DataInput
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
name|BytesRef
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
name|StringHelper
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextSegmentInfosWriter
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * reads plaintext segments files  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextSegmentInfosReader
specifier|public
class|class
name|SimpleTextSegmentInfosReader
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
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|VERSION
argument_list|)
assert|;
name|infos
operator|.
name|version
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|readString
argument_list|(
name|VERSION
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|COUNTER
argument_list|)
assert|;
name|infos
operator|.
name|counter
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|COUNTER
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NUM_USERDATA
argument_list|)
assert|;
name|int
name|numUserData
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUM_USERDATA
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|infos
operator|.
name|userData
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numUserData
condition|;
name|i
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|USERDATA_KEY
argument_list|)
assert|;
name|String
name|key
init|=
name|readString
argument_list|(
name|USERDATA_KEY
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|USERDATA_VALUE
argument_list|)
assert|;
name|String
name|value
init|=
name|readString
argument_list|(
name|USERDATA_VALUE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|infos
operator|.
name|userData
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|NUM_SEGMENTS
argument_list|)
assert|;
name|int
name|numSegments
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUM_SEGMENTS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
name|infos
operator|.
name|add
argument_list|(
name|readSegmentInfo
argument_list|(
name|directory
argument_list|,
name|input
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readSegmentInfo
specifier|public
name|SegmentInfo
name|readSegmentInfo
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|DataInput
name|input
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_NAME
argument_list|)
assert|;
specifier|final
name|String
name|name
init|=
name|readString
argument_list|(
name|SI_NAME
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_CODEC
argument_list|)
assert|;
specifier|final
name|Codec
name|codec
init|=
name|Codec
operator|.
name|forName
argument_list|(
name|readString
argument_list|(
name|SI_CODEC
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_VERSION
argument_list|)
assert|;
specifier|final
name|String
name|version
init|=
name|readString
argument_list|(
name|SI_VERSION
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DOCCOUNT
argument_list|)
assert|;
specifier|final
name|int
name|docCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_DOCCOUNT
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DELCOUNT
argument_list|)
assert|;
specifier|final
name|int
name|delCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_DELCOUNT
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_HASPROX
argument_list|)
assert|;
specifier|final
name|boolean
name|hasProx
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|SI_HASPROX
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_HASVECTORS
argument_list|)
assert|;
specifier|final
name|boolean
name|hasVectors
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|SI_HASVECTORS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_USECOMPOUND
argument_list|)
assert|;
specifier|final
name|boolean
name|isCompoundFile
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|SI_USECOMPOUND
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DSOFFSET
argument_list|)
assert|;
specifier|final
name|int
name|dsOffset
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_DSOFFSET
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DSSEGMENT
argument_list|)
assert|;
specifier|final
name|String
name|dsSegment
init|=
name|readString
argument_list|(
name|SI_DSSEGMENT
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DSCOMPOUND
argument_list|)
assert|;
specifier|final
name|boolean
name|dsCompoundFile
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|SI_DSCOMPOUND
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DELGEN
argument_list|)
assert|;
specifier|final
name|long
name|delGen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|readString
argument_list|(
name|SI_DELGEN
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_NUM_NORMGEN
argument_list|)
assert|;
specifier|final
name|int
name|numNormGen
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_NUM_NORMGEN
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
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
literal|0
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numNormGen
condition|;
name|i
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_NORMGEN_KEY
argument_list|)
assert|;
name|int
name|key
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_NORMGEN_KEY
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_NORMGEN_VALUE
argument_list|)
assert|;
name|long
name|value
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|readString
argument_list|(
name|SI_NORMGEN_VALUE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|normGen
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_NUM_DIAG
argument_list|)
assert|;
name|int
name|numDiag
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_NUM_DIAG
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDiag
condition|;
name|i
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DIAG_KEY
argument_list|)
assert|;
name|String
name|key
init|=
name|readString
argument_list|(
name|SI_DIAG_KEY
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|SI_DIAG_VALUE
argument_list|)
assert|;
name|String
name|value
init|=
name|readString
argument_list|(
name|SI_DIAG_VALUE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|diagnostics
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SegmentInfo
argument_list|(
name|directory
argument_list|,
name|version
argument_list|,
name|name
argument_list|,
name|docCount
argument_list|,
name|delGen
argument_list|,
name|dsOffset
argument_list|,
name|dsSegment
argument_list|,
name|dsCompoundFile
argument_list|,
name|normGen
argument_list|,
name|isCompoundFile
argument_list|,
name|delCount
argument_list|,
name|hasProx
argument_list|,
name|codec
argument_list|,
name|diagnostics
argument_list|,
name|hasVectors
argument_list|)
return|;
block|}
DECL|method|readString
specifier|private
name|String
name|readString
parameter_list|(
name|int
name|offset
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|scratch
operator|.
name|length
operator|-
name|offset
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
return|;
block|}
block|}
end_class

end_unit

