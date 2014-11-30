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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
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
name|BytesRefBuilder
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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * plain text segments file format.  *<p>  *<b>FOR RECREATIONAL USE ONLY</b>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextSegmentInfoFormat
specifier|public
class|class
name|SimpleTextSegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
DECL|field|SI_VERSION
specifier|final
specifier|static
name|BytesRef
name|SI_VERSION
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    version "
argument_list|)
decl_stmt|;
DECL|field|SI_DOCCOUNT
specifier|final
specifier|static
name|BytesRef
name|SI_DOCCOUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    number of documents "
argument_list|)
decl_stmt|;
DECL|field|SI_USECOMPOUND
specifier|final
specifier|static
name|BytesRef
name|SI_USECOMPOUND
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    uses compound file "
argument_list|)
decl_stmt|;
DECL|field|SI_NUM_DIAG
specifier|final
specifier|static
name|BytesRef
name|SI_NUM_DIAG
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    diagnostics "
argument_list|)
decl_stmt|;
DECL|field|SI_DIAG_KEY
specifier|final
specifier|static
name|BytesRef
name|SI_DIAG_KEY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      key "
argument_list|)
decl_stmt|;
DECL|field|SI_DIAG_VALUE
specifier|final
specifier|static
name|BytesRef
name|SI_DIAG_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      value "
argument_list|)
decl_stmt|;
DECL|field|SI_NUM_FILES
specifier|final
specifier|static
name|BytesRef
name|SI_NUM_FILES
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    files "
argument_list|)
decl_stmt|;
DECL|field|SI_FILE
specifier|final
specifier|static
name|BytesRef
name|SI_FILE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      file "
argument_list|)
decl_stmt|;
DECL|field|SI_ID
specifier|final
specifier|static
name|BytesRef
name|SI_ID
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    id "
argument_list|)
decl_stmt|;
DECL|field|SI_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
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
parameter_list|,
name|byte
index|[]
name|segmentID
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|String
name|segFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
literal|""
argument_list|,
name|SimpleTextSegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
name|ChecksumIndexInput
name|input
init|=
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|segFileName
argument_list|,
name|context
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
operator|.
name|get
argument_list|()
argument_list|,
name|SI_VERSION
argument_list|)
assert|;
specifier|final
name|Version
name|version
decl_stmt|;
try|try
block|{
name|version
operator|=
name|Version
operator|.
name|parse
argument_list|(
name|readString
argument_list|(
name|SI_VERSION
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"unable to parse version string: "
operator|+
name|pe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|input
argument_list|,
name|pe
argument_list|)
throw|;
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
operator|.
name|get
argument_list|()
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
operator|.
name|get
argument_list|()
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
operator|.
name|get
argument_list|()
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
argument_list|<>
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
operator|.
name|get
argument_list|()
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
operator|.
name|get
argument_list|()
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
operator|.
name|get
argument_list|()
argument_list|,
name|SI_NUM_FILES
argument_list|)
assert|;
name|int
name|numFiles
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|SI_NUM_FILES
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
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
name|numFiles
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
operator|.
name|get
argument_list|()
argument_list|,
name|SI_FILE
argument_list|)
assert|;
name|String
name|fileName
init|=
name|readString
argument_list|(
name|SI_FILE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|fileName
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
operator|.
name|get
argument_list|()
argument_list|,
name|SI_ID
argument_list|)
assert|;
specifier|final
name|byte
index|[]
name|id
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|SI_ID
operator|.
name|length
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|segmentID
argument_list|,
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"file mismatch, expected: "
operator|+
name|StringHelper
operator|.
name|idToString
argument_list|(
name|segmentID
argument_list|)
operator|+
literal|", got: "
operator|+
name|StringHelper
operator|.
name|idToString
argument_list|(
name|id
argument_list|)
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|SimpleTextUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|SegmentInfo
name|info
init|=
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
name|isCompoundFile
argument_list|,
literal|null
argument_list|,
name|diagnostics
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|info
operator|.
name|setFiles
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|info
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
name|BytesRefBuilder
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
argument_list|()
argument_list|,
name|offset
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|offset
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|segFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SimpleTextSegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
name|si
operator|.
name|addFile
argument_list|(
name|segFileName
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|segFileName
argument_list|,
name|ioContext
argument_list|)
decl_stmt|;
try|try
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_VERSION
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|si
operator|.
name|getVersion
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DOCCOUNT
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_USECOMPOUND
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|si
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|int
name|numDiagnostics
init|=
name|diagnostics
operator|==
literal|null
condition|?
literal|0
else|:
name|diagnostics
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NUM_DIAG
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numDiagnostics
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDiagnostics
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagEntry
range|:
name|diagnostics
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DIAG_KEY
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|diagEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_DIAG_VALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|diagEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|si
operator|.
name|files
argument_list|()
decl_stmt|;
name|int
name|numFiles
init|=
name|files
operator|==
literal|null
condition|?
literal|0
else|:
name|files
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_NUM_FILES
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numFiles
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|numFiles
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|fileName
range|:
name|files
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_FILE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|fileName
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|SI_ID
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|si
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeChecksum
argument_list|(
name|output
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
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
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

