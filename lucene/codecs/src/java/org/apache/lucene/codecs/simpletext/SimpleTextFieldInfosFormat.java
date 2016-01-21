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
name|util
operator|.
name|Collections
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
name|FieldInfosFormat
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
name|DocValuesType
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
name|FieldInfos
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
name|IndexOptions
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

begin_comment
comment|/**  * plaintext field infos format  *<p>  *<b>FOR RECREATIONAL USE ONLY</b>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextFieldInfosFormat
specifier|public
class|class
name|SimpleTextFieldInfosFormat
extends|extends
name|FieldInfosFormat
block|{
comment|/** Extension of field infos */
DECL|field|FIELD_INFOS_EXTENSION
specifier|static
specifier|final
name|String
name|FIELD_INFOS_EXTENSION
init|=
literal|"inf"
decl_stmt|;
DECL|field|NUMFIELDS
specifier|static
specifier|final
name|BytesRef
name|NUMFIELDS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"number of fields "
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|static
specifier|final
name|BytesRef
name|NAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  name "
argument_list|)
decl_stmt|;
DECL|field|NUMBER
specifier|static
specifier|final
name|BytesRef
name|NUMBER
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  number "
argument_list|)
decl_stmt|;
DECL|field|STORETV
specifier|static
specifier|final
name|BytesRef
name|STORETV
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term vectors "
argument_list|)
decl_stmt|;
DECL|field|STORETVPOS
specifier|static
specifier|final
name|BytesRef
name|STORETVPOS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term vector positions "
argument_list|)
decl_stmt|;
DECL|field|STORETVOFF
specifier|static
specifier|final
name|BytesRef
name|STORETVOFF
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term vector offsets "
argument_list|)
decl_stmt|;
DECL|field|PAYLOADS
specifier|static
specifier|final
name|BytesRef
name|PAYLOADS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  payloads "
argument_list|)
decl_stmt|;
DECL|field|NORMS
specifier|static
specifier|final
name|BytesRef
name|NORMS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  norms "
argument_list|)
decl_stmt|;
DECL|field|DOCVALUES
specifier|static
specifier|final
name|BytesRef
name|DOCVALUES
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  doc values "
argument_list|)
decl_stmt|;
DECL|field|DOCVALUES_GEN
specifier|static
specifier|final
name|BytesRef
name|DOCVALUES_GEN
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  doc values gen "
argument_list|)
decl_stmt|;
DECL|field|INDEXOPTIONS
specifier|static
specifier|final
name|BytesRef
name|INDEXOPTIONS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  index options "
argument_list|)
decl_stmt|;
DECL|field|NUM_ATTS
specifier|static
specifier|final
name|BytesRef
name|NUM_ATTS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  attributes "
argument_list|)
decl_stmt|;
DECL|field|ATT_KEY
specifier|static
specifier|final
name|BytesRef
name|ATT_KEY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    key "
argument_list|)
decl_stmt|;
DECL|field|ATT_VALUE
specifier|static
specifier|final
name|BytesRef
name|ATT_VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    value "
argument_list|)
decl_stmt|;
DECL|field|DIM_COUNT
specifier|static
specifier|final
name|BytesRef
name|DIM_COUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  dimensional count "
argument_list|)
decl_stmt|;
DECL|field|DIM_NUM_BYTES
specifier|static
specifier|final
name|BytesRef
name|DIM_NUM_BYTES
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  dimensional num bytes "
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|read
specifier|public
name|FieldInfos
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|iocontext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|FIELD_INFOS_EXTENSION
argument_list|)
decl_stmt|;
name|ChecksumIndexInput
name|input
init|=
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|iocontext
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
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
name|NUMFIELDS
argument_list|)
assert|;
specifier|final
name|int
name|size
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUMFIELDS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfo
name|infos
index|[]
init|=
operator|new
name|FieldInfo
index|[
name|size
index|]
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
name|size
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
name|NAME
argument_list|)
assert|;
name|String
name|name
init|=
name|readString
argument_list|(
name|NAME
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
name|NUMBER
argument_list|)
assert|;
name|int
name|fieldNumber
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUMBER
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
name|INDEXOPTIONS
argument_list|)
assert|;
name|String
name|s
init|=
name|readString
argument_list|(
name|INDEXOPTIONS
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
specifier|final
name|IndexOptions
name|indexOptions
init|=
name|IndexOptions
operator|.
name|valueOf
argument_list|(
name|s
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
name|STORETV
argument_list|)
assert|;
name|boolean
name|storeTermVector
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|STORETV
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
name|PAYLOADS
argument_list|)
assert|;
name|boolean
name|storePayloads
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|PAYLOADS
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
name|NORMS
argument_list|)
assert|;
name|boolean
name|omitNorms
init|=
operator|!
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|readString
argument_list|(
name|NORMS
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
name|DOCVALUES
argument_list|)
assert|;
name|String
name|dvType
init|=
name|readString
argument_list|(
name|DOCVALUES
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesType
name|docValuesType
init|=
name|docValuesType
argument_list|(
name|dvType
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
name|DOCVALUES_GEN
argument_list|)
assert|;
specifier|final
name|long
name|dvGen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|readString
argument_list|(
name|DOCVALUES_GEN
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
name|NUM_ATTS
argument_list|)
assert|;
name|int
name|numAtts
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|NUM_ATTS
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
name|atts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numAtts
condition|;
name|j
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
name|ATT_KEY
argument_list|)
assert|;
name|String
name|key
init|=
name|readString
argument_list|(
name|ATT_KEY
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
name|ATT_VALUE
argument_list|)
assert|;
name|String
name|value
init|=
name|readString
argument_list|(
name|ATT_VALUE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
decl_stmt|;
name|atts
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
name|DIM_COUNT
argument_list|)
assert|;
name|int
name|dimensionalCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|DIM_COUNT
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
name|DIM_NUM_BYTES
argument_list|)
assert|;
name|int
name|dimensionalNumBytes
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|DIM_NUM_BYTES
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
decl_stmt|;
name|infos
index|[
name|i
index|]
operator|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|,
name|dvGen
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|atts
argument_list|)
argument_list|,
name|dimensionalCount
argument_list|,
name|dimensionalNumBytes
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
name|infos
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|fieldInfos
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|docValuesType
specifier|public
name|DocValuesType
name|docValuesType
parameter_list|(
name|String
name|dvType
parameter_list|)
block|{
return|return
name|DocValuesType
operator|.
name|valueOf
argument_list|(
name|dvType
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
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|FieldInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|FIELD_INFOS_EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
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
name|write
argument_list|(
name|out
argument_list|,
name|NUMFIELDS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|infos
operator|.
name|size
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
name|out
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|fi
operator|.
name|name
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NUMBER
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|number
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|INDEXOPTIONS
argument_list|)
expr_stmt|;
name|IndexOptions
name|indexOptions
init|=
name|fi
operator|.
name|getIndexOptions
argument_list|()
decl_stmt|;
assert|assert
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
operator|||
operator|!
name|fi
operator|.
name|hasPayloads
argument_list|()
assert|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|indexOptions
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|STORETV
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|hasVectors
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|PAYLOADS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|hasPayloads
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NORMS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
operator|!
name|fi
operator|.
name|omitsNorms
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|DOCVALUES
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|getDocValuesType
argument_list|(
name|fi
operator|.
name|getDocValuesType
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|DOCVALUES_GEN
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|getDocValuesGen
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
name|out
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|atts
init|=
name|fi
operator|.
name|attributes
argument_list|()
decl_stmt|;
name|int
name|numAtts
init|=
name|atts
operator|==
literal|null
condition|?
literal|0
else|:
name|atts
operator|.
name|size
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|NUM_ATTS
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numAtts
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|numAtts
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
name|entry
range|:
name|atts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|ATT_KEY
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|entry
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|ATT_VALUE
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|entry
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
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|DIM_COUNT
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|getPointDimensionCount
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
name|out
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|DIM_NUM_BYTES
argument_list|)
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|fi
operator|.
name|getPointNumBytes
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
name|out
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|writeChecksum
argument_list|(
name|out
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
name|success
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
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
DECL|method|getDocValuesType
specifier|private
specifier|static
name|String
name|getDocValuesType
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
return|return
name|type
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

