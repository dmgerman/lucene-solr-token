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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|AlreadyClosedException
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
name|CloseableThreadLocal
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
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  * Class responsible for access to stored document fields.  *<p/>  * It uses&lt;segment&gt;.fdt and&lt;segment&gt;.fdx; files.  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|FieldsReader
specifier|public
specifier|final
class|class
name|FieldsReader
implements|implements
name|Cloneable
implements|,
name|Closeable
block|{
DECL|field|FORMAT_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|FORMAT_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsStreamTL
specifier|private
name|CloseableThreadLocal
argument_list|<
name|IndexInput
argument_list|>
name|fieldsStreamTL
init|=
operator|new
name|CloseableThreadLocal
argument_list|<
name|IndexInput
argument_list|>
argument_list|()
decl_stmt|;
comment|// The main fieldStream, used only for cloning.
DECL|field|cloneableFieldsStream
specifier|private
specifier|final
name|IndexInput
name|cloneableFieldsStream
decl_stmt|;
comment|// This is a clone of cloneableFieldsStream used for reading documents.
comment|// It should not be cloned outside of a synchronized context.
DECL|field|fieldsStream
specifier|private
specifier|final
name|IndexInput
name|fieldsStream
decl_stmt|;
DECL|field|cloneableIndexStream
specifier|private
specifier|final
name|IndexInput
name|cloneableIndexStream
decl_stmt|;
DECL|field|indexStream
specifier|private
specifier|final
name|IndexInput
name|indexStream
decl_stmt|;
DECL|field|numTotalDocs
specifier|private
name|int
name|numTotalDocs
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|int
name|format
decl_stmt|;
comment|// The docID offset where our docs begin in the index
comment|// file.  This will be 0 if we have our own private file.
DECL|field|docStoreOffset
specifier|private
name|int
name|docStoreOffset
decl_stmt|;
DECL|field|isOriginal
specifier|private
name|boolean
name|isOriginal
init|=
literal|false
decl_stmt|;
comment|/** Returns a cloned FieldsReader that shares open    *  IndexInputs with the original one.  It is the caller's    *  job not to close the original FieldsReader until all    *  clones are called (eg, currently SegmentReader manages    *  this logic). */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|FieldsReader
argument_list|(
name|fieldInfos
argument_list|,
name|numTotalDocs
argument_list|,
name|size
argument_list|,
name|format
argument_list|,
name|docStoreOffset
argument_list|,
name|cloneableFieldsStream
argument_list|,
name|cloneableIndexStream
argument_list|)
return|;
block|}
comment|/** Verifies that the code version which wrote the segment is supported. */
DECL|method|checkCodeVersion
specifier|public
specifier|static
name|void
name|checkCodeVersion
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|indexStreamFN
init|=
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
decl_stmt|;
name|IndexInput
name|idxStream
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|indexStreamFN
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|format
init|=
name|idxStream
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|<
name|FieldsWriter
operator|.
name|FORMAT_MINIMUM
condition|)
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|indexStreamFN
argument_list|,
name|format
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_MINIMUM
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
if|if
condition|(
name|format
operator|>
name|FieldsWriter
operator|.
name|FORMAT_CURRENT
condition|)
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|indexStreamFN
argument_list|,
name|format
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_MINIMUM
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
block|}
finally|finally
block|{
name|idxStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Used only by clone
DECL|method|FieldsReader
specifier|private
name|FieldsReader
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|numTotalDocs
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|format
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|IndexInput
name|cloneableFieldsStream
parameter_list|,
name|IndexInput
name|cloneableIndexStream
parameter_list|)
block|{
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|numTotalDocs
operator|=
name|numTotalDocs
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|cloneableFieldsStream
operator|=
name|cloneableFieldsStream
expr_stmt|;
name|this
operator|.
name|cloneableIndexStream
operator|=
name|cloneableIndexStream
expr_stmt|;
name|fieldsStream
operator|=
operator|(
name|IndexInput
operator|)
name|cloneableFieldsStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|indexStream
operator|=
operator|(
name|IndexInput
operator|)
name|cloneableIndexStream
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
DECL|method|FieldsReader
specifier|public
name|FieldsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|d
argument_list|,
name|segment
argument_list|,
name|fn
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldsReader
specifier|public
name|FieldsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|isOriginal
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|cloneableFieldsStream
operator|=
name|d
operator|.
name|openInput
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
specifier|final
name|String
name|indexStreamFN
init|=
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
decl_stmt|;
name|cloneableIndexStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|indexStreamFN
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|format
operator|=
name|cloneableIndexStream
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|format
operator|<
name|FieldsWriter
operator|.
name|FORMAT_MINIMUM
condition|)
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|indexStreamFN
argument_list|,
name|format
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_MINIMUM
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
if|if
condition|(
name|format
operator|>
name|FieldsWriter
operator|.
name|FORMAT_CURRENT
condition|)
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|indexStreamFN
argument_list|,
name|format
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_MINIMUM
argument_list|,
name|FieldsWriter
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
name|fieldsStream
operator|=
operator|(
name|IndexInput
operator|)
name|cloneableFieldsStream
operator|.
name|clone
argument_list|()
expr_stmt|;
specifier|final
name|long
name|indexSize
init|=
name|cloneableIndexStream
operator|.
name|length
argument_list|()
operator|-
name|FORMAT_SIZE
decl_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We read only a slice out of this shared fields file
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
comment|// Verify the file is long enough to hold all of our
comment|// docs
assert|assert
operator|(
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|/
literal|8
argument_list|)
operator|)
operator|>=
name|size
operator|+
name|this
operator|.
name|docStoreOffset
operator|:
literal|"indexSize="
operator|+
name|indexSize
operator|+
literal|" size="
operator|+
name|size
operator|+
literal|" docStoreOffset="
operator|+
name|docStoreOffset
assert|;
block|}
else|else
block|{
name|this
operator|.
name|docStoreOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|size
operator|=
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|>>
literal|3
argument_list|)
expr_stmt|;
block|}
name|indexStream
operator|=
operator|(
name|IndexInput
operator|)
name|cloneableIndexStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|numTotalDocs
operator|=
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|>>
literal|3
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
comment|// With lock-less commits, it's entirely possible (and
comment|// fine) to hit a FileNotFound exception above. In
comment|// this case, we want to explicitly close any subset
comment|// of things that were opened so that we don't have to
comment|// wait for a GC to do so.
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this FieldsReader is closed    */
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this FieldsReader is closed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Closes the underlying {@link org.apache.lucene.store.IndexInput} streams, including any ones associated with a    * lazy implementation of a Field.  This means that the Fields values will not be accessible.    *    * @throws IOException    */
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
if|if
condition|(
name|isOriginal
condition|)
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
argument_list|,
name|fieldsStreamTL
argument_list|,
name|cloneableFieldsStream
argument_list|,
name|cloneableIndexStream
argument_list|)
expr_stmt|;
block|}
else|else
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
argument_list|,
name|fieldsStreamTL
argument_list|)
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|seekIndex
specifier|private
name|void
name|seekIndex
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|seek
argument_list|(
name|FORMAT_SIZE
operator|+
operator|(
name|docID
operator|+
name|docStoreOffset
operator|)
operator|*
literal|8L
argument_list|)
expr_stmt|;
block|}
DECL|method|visitDocument
specifier|public
specifier|final
name|void
name|visitDocument
parameter_list|(
name|int
name|n
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|seekIndex
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|indexStream
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numFields
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fieldIDX
init|=
literal|0
init|;
name|fieldIDX
operator|<
name|numFields
condition|;
name|fieldIDX
operator|++
control|)
block|{
name|int
name|fieldNumber
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
name|int
name|bits
init|=
name|fieldsStream
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
assert|assert
name|bits
operator|<=
operator|(
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_MASK
operator||
name|FieldsWriter
operator|.
name|FIELD_IS_BINARY
operator|)
operator|:
literal|"bits="
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
assert|;
specifier|final
name|boolean
name|binary
init|=
operator|(
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_BINARY
operator|)
operator|!=
literal|0
decl_stmt|;
specifier|final
name|int
name|numeric
init|=
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_MASK
decl_stmt|;
specifier|final
name|boolean
name|doStop
decl_stmt|;
if|if
condition|(
name|binary
condition|)
block|{
specifier|final
name|int
name|numBytes
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|doStop
operator|=
name|visitor
operator|.
name|binaryField
argument_list|(
name|fieldInfo
argument_list|,
name|fieldsStream
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numeric
operator|!=
literal|0
condition|)
block|{
switch|switch
condition|(
name|numeric
condition|)
block|{
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_INT
case|:
name|doStop
operator|=
name|visitor
operator|.
name|intField
argument_list|(
name|fieldInfo
argument_list|,
name|fieldsStream
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_LONG
case|:
name|doStop
operator|=
name|visitor
operator|.
name|longField
argument_list|(
name|fieldInfo
argument_list|,
name|fieldsStream
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_FLOAT
case|:
name|doStop
operator|=
name|visitor
operator|.
name|floatField
argument_list|(
name|fieldInfo
argument_list|,
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|fieldsStream
operator|.
name|readInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_DOUBLE
case|:
name|doStop
operator|=
name|visitor
operator|.
name|doubleField
argument_list|(
name|fieldInfo
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|fieldsStream
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|FieldReaderException
argument_list|(
literal|"Invalid numeric type: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|numeric
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// Text:
specifier|final
name|int
name|numUTF8Bytes
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|doStop
operator|=
name|visitor
operator|.
name|stringField
argument_list|(
name|fieldInfo
argument_list|,
name|fieldsStream
argument_list|,
name|numUTF8Bytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doStop
condition|)
block|{
return|return;
block|}
block|}
block|}
comment|/** Returns the length in bytes of each raw document in a    *  contiguous range of length numDocs starting with    *  startDocID.  Returns the IndexInput (the fieldStream),    *  already seeked to the starting point for startDocID.*/
DECL|method|rawDocs
specifier|public
specifier|final
name|IndexInput
name|rawDocs
parameter_list|(
name|int
index|[]
name|lengths
parameter_list|,
name|int
name|startDocID
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|seekIndex
argument_list|(
name|startDocID
argument_list|)
expr_stmt|;
name|long
name|startOffset
init|=
name|indexStream
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|lastOffset
init|=
name|startOffset
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|numDocs
condition|)
block|{
specifier|final
name|long
name|offset
decl_stmt|;
specifier|final
name|int
name|docID
init|=
name|docStoreOffset
operator|+
name|startDocID
operator|+
name|count
operator|+
literal|1
decl_stmt|;
assert|assert
name|docID
operator|<=
name|numTotalDocs
assert|;
if|if
condition|(
name|docID
operator|<
name|numTotalDocs
condition|)
name|offset
operator|=
name|indexStream
operator|.
name|readLong
argument_list|()
expr_stmt|;
else|else
name|offset
operator|=
name|fieldsStream
operator|.
name|length
argument_list|()
expr_stmt|;
name|lengths
index|[
name|count
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|-
name|lastOffset
argument_list|)
expr_stmt|;
name|lastOffset
operator|=
name|offset
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|seek
argument_list|(
name|startOffset
argument_list|)
expr_stmt|;
return|return
name|fieldsStream
return|;
block|}
comment|/**    * Skip the field.  We still have to read some of the information about the field, but can skip past the actual content.    * This will have the most payoff on large fields.    */
DECL|method|skipField
specifier|private
name|void
name|skipField
parameter_list|(
name|int
name|numeric
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBytes
decl_stmt|;
switch|switch
condition|(
name|numeric
condition|)
block|{
case|case
literal|0
case|:
name|numBytes
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
break|break;
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_INT
case|:
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_FLOAT
case|:
name|numBytes
operator|=
literal|4
expr_stmt|;
break|break;
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_LONG
case|:
case|case
name|FieldsWriter
operator|.
name|FIELD_IS_NUMERIC_DOUBLE
case|:
name|numBytes
operator|=
literal|8
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|FieldReaderException
argument_list|(
literal|"Invalid numeric type: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|numeric
argument_list|)
argument_list|)
throw|;
block|}
name|skipFieldBytes
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
block|}
DECL|method|skipFieldBytes
specifier|private
name|void
name|skipFieldBytes
parameter_list|(
name|int
name|toRead
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|seek
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
operator|+
name|toRead
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

