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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|List
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Fieldable
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
name|document
operator|.
name|NumericField
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

begin_class
DECL|class|FieldsWriter
specifier|final
class|class
name|FieldsWriter
block|{
DECL|field|FIELD_IS_TOKENIZED
specifier|static
specifier|final
name|int
name|FIELD_IS_TOKENIZED
init|=
literal|1
operator|<<
literal|0
decl_stmt|;
DECL|field|FIELD_IS_BINARY
specifier|static
specifier|final
name|int
name|FIELD_IS_BINARY
init|=
literal|1
operator|<<
literal|1
decl_stmt|;
comment|// the old bit 1<< 2 was compressed, is now left out
DECL|field|_NUMERIC_BIT_SHIFT
specifier|private
specifier|static
specifier|final
name|int
name|_NUMERIC_BIT_SHIFT
init|=
literal|3
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_MASK
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_MASK
init|=
literal|0x07
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_INT
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_INT
init|=
literal|1
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_LONG
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_LONG
init|=
literal|2
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_FLOAT
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_FLOAT
init|=
literal|3
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_DOUBLE
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_DOUBLE
init|=
literal|4
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
comment|// currently unused: static final int FIELD_IS_NUMERIC_SHORT = 5<< _NUMERIC_BIT_SHIFT;
comment|// currently unused: static final int FIELD_IS_NUMERIC_BYTE = 6<< _NUMERIC_BIT_SHIFT;
comment|// the next possible bits are: 1<< 6; 1<< 7
comment|// Lucene 3.0: Removal of compressed fields
DECL|field|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
init|=
literal|2
decl_stmt|;
comment|// Lucene 3.2: NumericFields are stored in binary format
DECL|field|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
init|=
literal|3
decl_stmt|;
comment|// NOTE: if you introduce a new format, make it 1 higher
comment|// than the current one, and always change this if you
comment|// switch to a new format!
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
decl_stmt|;
comment|// when removing support for old versions, leave the last supported version here
DECL|field|FORMAT_MINIMUM
specifier|static
specifier|final
name|int
name|FORMAT_MINIMUM
init|=
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
decl_stmt|;
comment|// If null - we were supplied with streams, if notnull - we manage them ourselves
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
name|String
name|segment
decl_stmt|;
DECL|field|fieldsStream
specifier|private
name|IndexOutput
name|fieldsStream
decl_stmt|;
DECL|field|indexStream
specifier|private
name|IndexOutput
name|indexStream
decl_stmt|;
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fieldsStream
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|indexStream
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeInt
argument_list|(
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|indexStream
operator|.
name|writeInt
argument_list|(
name|FORMAT_CURRENT
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
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|IndexOutput
name|fdx
parameter_list|,
name|IndexOutput
name|fdt
parameter_list|)
block|{
name|directory
operator|=
literal|null
expr_stmt|;
name|segment
operator|=
literal|null
expr_stmt|;
name|fieldsStream
operator|=
name|fdt
expr_stmt|;
name|indexStream
operator|=
name|fdx
expr_stmt|;
block|}
DECL|method|setFieldsStream
name|void
name|setFieldsStream
parameter_list|(
name|IndexOutput
name|stream
parameter_list|)
block|{
name|this
operator|.
name|fieldsStream
operator|=
name|stream
expr_stmt|;
block|}
comment|// Writes the contents of buffer into the fields stream
comment|// and adds a new entry for this document into the index
comment|// stream.  This assumes the buffer was already written
comment|// in the correct fields format.
DECL|method|startDocument
name|void
name|startDocument
parameter_list|(
name|int
name|numStoredFields
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|numStoredFields
argument_list|)
expr_stmt|;
block|}
DECL|method|skipDocument
name|void
name|skipDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|close
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|false
argument_list|,
name|fieldsStream
argument_list|,
name|indexStream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fieldsStream
operator|=
name|indexStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{       }
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{       }
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{       }
block|}
block|}
DECL|method|writeField
specifier|final
name|void
name|writeField
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|Fieldable
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|fieldNumber
argument_list|)
expr_stmt|;
name|int
name|bits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
name|bits
operator||=
name|FIELD_IS_TOKENIZED
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
name|bits
operator||=
name|FIELD_IS_BINARY
expr_stmt|;
if|if
condition|(
name|field
operator|instanceof
name|NumericField
condition|)
block|{
switch|switch
condition|(
operator|(
operator|(
name|NumericField
operator|)
name|field
operator|)
operator|.
name|getDataType
argument_list|()
condition|)
block|{
case|case
name|INT
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_INT
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_LONG
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_FLOAT
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_DOUBLE
expr_stmt|;
break|break;
default|default:
assert|assert
literal|false
operator|:
literal|"Should never get here"
assert|;
block|}
block|}
name|fieldsStream
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bits
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
block|{
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
specifier|final
name|int
name|offset
decl_stmt|;
name|data
operator|=
name|field
operator|.
name|getBinaryValue
argument_list|()
expr_stmt|;
name|len
operator|=
name|field
operator|.
name|getBinaryLength
argument_list|()
expr_stmt|;
name|offset
operator|=
name|field
operator|.
name|getBinaryOffset
argument_list|()
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|instanceof
name|NumericField
condition|)
block|{
specifier|final
name|NumericField
name|nf
init|=
operator|(
name|NumericField
operator|)
name|field
decl_stmt|;
specifier|final
name|Number
name|n
init|=
name|nf
operator|.
name|getNumericValue
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|nf
operator|.
name|getDataType
argument_list|()
condition|)
block|{
case|case
name|INT
case|:
name|fieldsStream
operator|.
name|writeInt
argument_list|(
name|n
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|fieldsStream
operator|.
name|writeLong
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|fieldsStream
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|n
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|fieldsStream
operator|.
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|n
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
assert|assert
literal|false
operator|:
literal|"Should never get here"
assert|;
block|}
block|}
else|else
block|{
name|fieldsStream
operator|.
name|writeString
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Bulk write a contiguous series of documents.  The    *  lengths array is the length (in bytes) of each raw    *  document.  The stream IndexInput is the    *  fieldsStream from which we should bulk-copy all    *  bytes. */
DECL|method|addRawDocuments
specifier|final
name|void
name|addRawDocuments
parameter_list|(
name|IndexInput
name|stream
parameter_list|,
name|int
index|[]
name|lengths
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|position
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|position
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|position
operator|+=
name|lengths
index|[
name|i
index|]
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|copyBytes
argument_list|(
name|stream
argument_list|,
name|position
operator|-
name|start
argument_list|)
expr_stmt|;
assert|assert
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
operator|==
name|position
assert|;
block|}
DECL|method|addDocument
specifier|final
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|storedCount
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Fieldable
argument_list|>
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|storedCount
operator|++
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|storedCount
argument_list|)
expr_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|writeField
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

