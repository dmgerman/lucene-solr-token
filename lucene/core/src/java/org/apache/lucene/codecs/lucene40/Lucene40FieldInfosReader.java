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
name|Collections
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
name|CodecUtil
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
name|FieldInfosReader
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
name|FieldInfo
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Lucene 4.0 FieldInfos reader.  *   * @lucene.experimental  * @see Lucene40FieldInfosFormat  * @deprecated Only for reading old 4.0 and 4.1 segments  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40FieldInfosReader
specifier|public
class|class
name|Lucene40FieldInfosReader
extends|extends
name|FieldInfosReader
block|{
comment|/** Sole constructor. */
DECL|method|Lucene40FieldInfosReader
specifier|public
name|Lucene40FieldInfosReader
parameter_list|()
block|{   }
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
name|String
name|segmentName
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
name|segmentName
argument_list|,
literal|""
argument_list|,
name|Lucene40FieldInfosFormat
operator|.
name|FIELD_INFOS_EXTENSION
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
name|iocontext
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|Lucene40FieldInfosFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene40FieldInfosFormat
operator|.
name|FORMAT_START
argument_list|,
name|Lucene40FieldInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//read in the size
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
name|fieldNumber
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|byte
name|bits
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isIndexed
init|=
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|IS_INDEXED
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storeTermVector
init|=
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|STORE_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|omitNorms
init|=
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|OMIT_NORMS
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storePayloads
init|=
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|STORE_PAYLOADS
operator|)
operator|!=
literal|0
decl_stmt|;
specifier|final
name|IndexOptions
name|indexOptions
decl_stmt|;
if|if
condition|(
operator|!
name|isIndexed
condition|)
block|{
name|indexOptions
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|OMIT_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene40FieldInfosFormat
operator|.
name|STORE_OFFSETS_IN_POSTINGS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
expr_stmt|;
block|}
else|else
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
block|}
comment|// LUCENE-3027: past indices were able to write
comment|// storePayloads=true when omitTFAP is also true,
comment|// which is invalid.  We correct that, here:
if|if
condition|(
name|isIndexed
operator|&&
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|<
literal|0
condition|)
block|{
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
comment|// DV Types are packed in one byte
name|byte
name|val
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
specifier|final
name|LegacyDocValuesType
name|oldValuesType
init|=
name|getDocValuesType
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|val
operator|&
literal|0x0F
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|LegacyDocValuesType
name|oldNormsType
init|=
name|getDocValuesType
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|val
operator|>>>
literal|4
operator|)
operator|&
literal|0x0F
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|input
operator|.
name|readStringStringMap
argument_list|()
decl_stmt|;
empty_stmt|;
if|if
condition|(
name|oldValuesType
operator|.
name|mapping
operator|!=
literal|null
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|LEGACY_DV_TYPE_KEY
argument_list|,
name|oldValuesType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oldNormsType
operator|.
name|mapping
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|oldNormsType
operator|.
name|mapping
operator|!=
name|DocValuesType
operator|.
name|NUMERIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid norm type: "
operator|+
name|oldNormsType
argument_list|)
throw|;
block|}
name|attributes
operator|.
name|put
argument_list|(
name|LEGACY_NORM_TYPE_KEY
argument_list|,
name|oldNormsType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|isIndexed
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
name|oldValuesType
operator|.
name|mapping
argument_list|,
name|oldNormsType
operator|.
name|mapping
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|attributes
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|!=
name|input
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"did not read all bytes from file \""
operator|+
name|fileName
operator|+
literal|"\": read "
operator|+
name|input
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" vs size "
operator|+
name|input
operator|.
name|length
argument_list|()
operator|+
literal|" (resource: "
operator|+
name|input
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
DECL|field|LEGACY_DV_TYPE_KEY
specifier|static
specifier|final
name|String
name|LEGACY_DV_TYPE_KEY
init|=
name|Lucene40FieldInfosReader
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".dvtype"
decl_stmt|;
DECL|field|LEGACY_NORM_TYPE_KEY
specifier|static
specifier|final
name|String
name|LEGACY_NORM_TYPE_KEY
init|=
name|Lucene40FieldInfosReader
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".normtype"
decl_stmt|;
comment|// mapping of 4.0 types -> 4.2 types
DECL|enum|LegacyDocValuesType
specifier|static
enum|enum
name|LegacyDocValuesType
block|{
DECL|enum constant|NONE
name|NONE
argument_list|(
literal|null
argument_list|)
block|,
DECL|enum constant|VAR_INTS
name|VAR_INTS
parameter_list|(
name|DocValuesType
operator|.
name|NUMERIC
parameter_list|)
operator|,
DECL|enum constant|FLOAT_32
constructor|FLOAT_32(DocValuesType.NUMERIC
block|)
enum|,
DECL|enum constant|FLOAT_64
name|FLOAT_64
parameter_list|(
name|DocValuesType
operator|.
name|NUMERIC
parameter_list|)
operator|,
DECL|enum constant|BYTES_FIXED_STRAIGHT
constructor|BYTES_FIXED_STRAIGHT(DocValuesType.BINARY
block|)
operator|,
DECL|enum constant|BYTES_FIXED_DEREF
name|BYTES_FIXED_DEREF
argument_list|(
name|DocValuesType
operator|.
name|BINARY
argument_list|)
operator|,
DECL|enum constant|BYTES_VAR_STRAIGHT
name|BYTES_VAR_STRAIGHT
argument_list|(
name|DocValuesType
operator|.
name|BINARY
argument_list|)
operator|,
DECL|enum constant|BYTES_VAR_DEREF
name|BYTES_VAR_DEREF
argument_list|(
name|DocValuesType
operator|.
name|BINARY
argument_list|)
operator|,
DECL|enum constant|FIXED_INTS_16
name|FIXED_INTS_16
argument_list|(
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
operator|,
DECL|enum constant|FIXED_INTS_32
name|FIXED_INTS_32
argument_list|(
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
operator|,
DECL|enum constant|FIXED_INTS_64
name|FIXED_INTS_64
argument_list|(
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
operator|,
DECL|enum constant|FIXED_INTS_8
name|FIXED_INTS_8
argument_list|(
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
operator|,
DECL|enum constant|BYTES_FIXED_SORTED
name|BYTES_FIXED_SORTED
argument_list|(
name|DocValuesType
operator|.
name|SORTED
argument_list|)
operator|,
DECL|enum constant|BYTES_VAR_SORTED
name|BYTES_VAR_SORTED
argument_list|(
name|DocValuesType
operator|.
name|SORTED
argument_list|)
expr_stmt|;
end_class

begin_decl_stmt
DECL|field|mapping
specifier|final
name|DocValuesType
name|mapping
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|LegacyDocValuesType
name|LegacyDocValuesType
argument_list|(
name|DocValuesType
name|mapping
argument_list|)
block|{
name|this
operator|.
name|mapping
operator|=
name|mapping
block|;     }
end_expr_stmt

begin_comment
unit|}
comment|// decodes a 4.0 type
end_comment

begin_function
DECL|method|getDocValuesType
unit|private
specifier|static
name|LegacyDocValuesType
name|getDocValuesType
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
return|return
name|LegacyDocValuesType
operator|.
name|values
argument_list|()
index|[
name|b
index|]
return|;
block|}
end_function

unit|}
end_unit

