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
name|store
operator|.
name|IndexInput
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
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|SimpleTextSegmentInfosFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
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
name|success
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|SegmentInfo
argument_list|(
name|directory
argument_list|,
name|version
argument_list|,
name|segmentName
argument_list|,
name|docCount
argument_list|,
operator|-
literal|1
argument_list|,
name|segmentName
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|isCompoundFile
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|diagnostics
argument_list|)
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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

