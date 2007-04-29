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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|*
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
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DataFormatException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Inflater
import|;
end_import

begin_comment
comment|/**  * Class responsible for access to stored document fields.  *<p/>  * It uses&lt;segment&gt;.fdt and&lt;segment&gt;.fdx; files.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|FieldsReader
specifier|final
class|class
name|FieldsReader
block|{
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
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
DECL|field|indexStream
specifier|private
specifier|final
name|IndexInput
name|indexStream
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
DECL|field|fieldsStreamTL
specifier|private
name|ThreadLocal
name|fieldsStreamTL
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|method|FieldsReader
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
name|segment
operator|+
literal|".fdt"
argument_list|)
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
name|d
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".fdx"
argument_list|)
expr_stmt|;
name|size
operator|=
call|(
name|int
call|)
argument_list|(
name|indexStream
operator|.
name|length
argument_list|()
operator|/
literal|8
argument_list|)
expr_stmt|;
block|}
comment|/**    * @throws AlreadyClosedException if this FieldsReader is closed    */
DECL|method|ensureOpen
specifier|protected
specifier|final
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
name|fieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|cloneableFieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|localFieldsStream
init|=
operator|(
name|IndexInput
operator|)
name|fieldsStreamTL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|localFieldsStream
operator|!=
literal|null
condition|)
block|{
name|localFieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|fieldsStreamTL
operator|.
name|set
argument_list|(
literal|null
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
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|doc
specifier|final
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|indexStream
operator|.
name|seek
argument_list|(
name|n
operator|*
literal|8L
argument_list|)
expr_stmt|;
name|long
name|position
init|=
name|indexStream
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFields
condition|;
name|i
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
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
name|FieldSelectorResult
name|acceptField
init|=
name|fieldSelector
operator|==
literal|null
condition|?
name|FieldSelectorResult
operator|.
name|LOAD
else|:
name|fieldSelector
operator|.
name|accept
argument_list|(
name|fi
operator|.
name|name
argument_list|)
decl_stmt|;
name|byte
name|bits
init|=
name|fieldsStream
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|compressed
init|=
operator|(
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_COMPRESSED
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|tokenize
init|=
operator|(
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_TOKENIZED
operator|)
operator|!=
literal|0
decl_stmt|;
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
comment|//TODO: Find an alternative approach here if this list continues to grow beyond the
comment|//list of 5 or 6 currently here.  See Lucene 762 for discussion
if|if
condition|(
name|acceptField
operator|.
name|equals
argument_list|(
name|FieldSelectorResult
operator|.
name|LOAD
argument_list|)
condition|)
block|{
name|addField
argument_list|(
name|doc
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|,
name|tokenize
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|acceptField
operator|.
name|equals
argument_list|(
name|FieldSelectorResult
operator|.
name|LOAD_FOR_MERGE
argument_list|)
condition|)
block|{
name|addFieldForMerge
argument_list|(
name|doc
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|,
name|tokenize
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|acceptField
operator|.
name|equals
argument_list|(
name|FieldSelectorResult
operator|.
name|LOAD_AND_BREAK
argument_list|)
condition|)
block|{
name|addField
argument_list|(
name|doc
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|,
name|tokenize
argument_list|)
expr_stmt|;
break|break;
comment|//Get out of this loop
block|}
elseif|else
if|if
condition|(
name|acceptField
operator|.
name|equals
argument_list|(
name|FieldSelectorResult
operator|.
name|LAZY_LOAD
argument_list|)
condition|)
block|{
name|addFieldLazy
argument_list|(
name|doc
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|,
name|tokenize
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|acceptField
operator|.
name|equals
argument_list|(
name|FieldSelectorResult
operator|.
name|SIZE
argument_list|)
condition|)
block|{
name|skipField
argument_list|(
name|binary
argument_list|,
name|compressed
argument_list|,
name|addFieldSize
argument_list|(
name|doc
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|acceptField
operator|.
name|equals
argument_list|(
name|FieldSelectorResult
operator|.
name|SIZE_AND_BREAK
argument_list|)
condition|)
block|{
name|addFieldSize
argument_list|(
name|doc
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|skipField
argument_list|(
name|binary
argument_list|,
name|compressed
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
comment|/**    * Skip the field.  We still have to read some of the information about the field, but can skip past the actual content.    * This will have the most payoff on large fields.    */
DECL|method|skipField
specifier|private
name|void
name|skipField
parameter_list|(
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|)
throws|throws
name|IOException
block|{
name|skipField
argument_list|(
name|binary
argument_list|,
name|compressed
argument_list|,
name|fieldsStream
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|skipField
specifier|private
name|void
name|skipField
parameter_list|(
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|,
name|int
name|toRead
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|binary
operator|||
name|compressed
condition|)
block|{
name|long
name|pointer
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|pointer
operator|+
name|toRead
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//We need to skip chars.  This will slow us down, but still better
name|fieldsStream
operator|.
name|skipChars
argument_list|(
name|toRead
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addFieldLazy
specifier|private
name|void
name|addFieldLazy
parameter_list|(
name|Document
name|doc
parameter_list|,
name|FieldInfo
name|fi
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|,
name|boolean
name|tokenize
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|binary
operator|==
literal|true
condition|)
block|{
name|int
name|toRead
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|long
name|pointer
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|compressed
condition|)
block|{
comment|//was: doc.add(new Fieldable(fi.name, uncompress(b), Fieldable.Store.COMPRESS));
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LazyField
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
argument_list|,
name|toRead
argument_list|,
name|pointer
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//was: doc.add(new Fieldable(fi.name, b, Fieldable.Store.YES));
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LazyField
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|toRead
argument_list|,
name|pointer
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Need to move the pointer ahead by toRead positions
name|fieldsStream
operator|.
name|seek
argument_list|(
name|pointer
operator|+
name|toRead
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Field
operator|.
name|Store
name|store
init|=
name|Field
operator|.
name|Store
operator|.
name|YES
decl_stmt|;
name|Field
operator|.
name|Index
name|index
init|=
name|getIndexType
argument_list|(
name|fi
argument_list|,
name|tokenize
argument_list|)
decl_stmt|;
name|Field
operator|.
name|TermVector
name|termVector
init|=
name|getTermVectorType
argument_list|(
name|fi
argument_list|)
decl_stmt|;
name|Fieldable
name|f
decl_stmt|;
if|if
condition|(
name|compressed
condition|)
block|{
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
expr_stmt|;
name|int
name|toRead
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|long
name|pointer
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|f
operator|=
operator|new
name|LazyField
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|store
argument_list|,
name|toRead
argument_list|,
name|pointer
argument_list|)
expr_stmt|;
comment|//skip over the part that we aren't loading
name|fieldsStream
operator|.
name|seek
argument_list|(
name|pointer
operator|+
name|toRead
argument_list|)
expr_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
name|fi
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|length
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|long
name|pointer
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|//Skip ahead of where we are by the length of what is stored
name|fieldsStream
operator|.
name|skipChars
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|LazyField
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|,
name|length
argument_list|,
name|pointer
argument_list|)
expr_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
name|fi
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|// in merge mode we don't uncompress the data of a compressed field
DECL|method|addFieldForMerge
specifier|private
name|void
name|addFieldForMerge
parameter_list|(
name|Document
name|doc
parameter_list|,
name|FieldInfo
name|fi
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|,
name|boolean
name|tokenize
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|data
decl_stmt|;
if|if
condition|(
name|binary
operator|||
name|compressed
condition|)
block|{
name|int
name|toRead
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|toRead
index|]
decl_stmt|;
name|fieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|data
operator|=
name|b
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
name|fieldsStream
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FieldForMerge
argument_list|(
name|data
argument_list|,
name|fi
argument_list|,
name|binary
argument_list|,
name|compressed
argument_list|,
name|tokenize
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|private
name|void
name|addField
parameter_list|(
name|Document
name|doc
parameter_list|,
name|FieldInfo
name|fi
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|,
name|boolean
name|tokenize
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
comment|//we have a binary stored field, and it may be compressed
if|if
condition|(
name|binary
condition|)
block|{
name|int
name|toRead
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|toRead
index|]
decl_stmt|;
name|fieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|compressed
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|uncompress
argument_list|(
name|b
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|b
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Field
operator|.
name|Store
name|store
init|=
name|Field
operator|.
name|Store
operator|.
name|YES
decl_stmt|;
name|Field
operator|.
name|Index
name|index
init|=
name|getIndexType
argument_list|(
name|fi
argument_list|,
name|tokenize
argument_list|)
decl_stmt|;
name|Field
operator|.
name|TermVector
name|termVector
init|=
name|getTermVectorType
argument_list|(
name|fi
argument_list|)
decl_stmt|;
name|Fieldable
name|f
decl_stmt|;
if|if
condition|(
name|compressed
condition|)
block|{
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
expr_stmt|;
name|int
name|toRead
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|toRead
index|]
decl_stmt|;
name|fieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
comment|// field name
operator|new
name|String
argument_list|(
name|uncompress
argument_list|(
name|b
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
comment|// uncompress the value and add as string
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
expr_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
name|fi
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
comment|// name
name|fieldsStream
operator|.
name|readString
argument_list|()
argument_list|,
comment|// read value
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
expr_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
name|fi
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add the size of field as a byte[] containing the 4 bytes of the integer byte size (high order byte first; char = 2 bytes)
comment|// Read just the size -- caller must skip the field content to continue reading fields
comment|// Return the size in bytes or chars, depending on field type
DECL|method|addFieldSize
specifier|private
name|int
name|addFieldSize
parameter_list|(
name|Document
name|doc
parameter_list|,
name|FieldInfo
name|fi
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|,
name|bytesize
init|=
name|binary
operator|||
name|compressed
condition|?
name|size
else|:
literal|2
operator|*
name|size
decl_stmt|;
name|byte
index|[]
name|sizebytes
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|sizebytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|bytesize
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|sizebytes
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|bytesize
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|sizebytes
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|bytesize
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|sizebytes
index|[
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|bytesize
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|sizebytes
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
DECL|method|getTermVectorType
specifier|private
name|Field
operator|.
name|TermVector
name|getTermVectorType
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
name|Field
operator|.
name|TermVector
name|termVector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|storeTermVector
condition|)
block|{
if|if
condition|(
name|fi
operator|.
name|storeOffsetWithTermVector
condition|)
block|{
if|if
condition|(
name|fi
operator|.
name|storePositionWithTermVector
condition|)
block|{
name|termVector
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
expr_stmt|;
block|}
else|else
block|{
name|termVector
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_OFFSETS
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storePositionWithTermVector
condition|)
block|{
name|termVector
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
expr_stmt|;
block|}
else|else
block|{
name|termVector
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|YES
expr_stmt|;
block|}
block|}
else|else
block|{
name|termVector
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|NO
expr_stmt|;
block|}
return|return
name|termVector
return|;
block|}
DECL|method|getIndexType
specifier|private
name|Field
operator|.
name|Index
name|getIndexType
parameter_list|(
name|FieldInfo
name|fi
parameter_list|,
name|boolean
name|tokenize
parameter_list|)
block|{
name|Field
operator|.
name|Index
name|index
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|tokenize
condition|)
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
expr_stmt|;
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|tokenize
condition|)
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
expr_stmt|;
else|else
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|NO
expr_stmt|;
return|return
name|index
return|;
block|}
comment|/**    * A Lazy implementation of Fieldable that differs loading of fields until asked for, instead of when the Document is    * loaded.    */
DECL|class|LazyField
specifier|private
class|class
name|LazyField
extends|extends
name|AbstractField
implements|implements
name|Fieldable
block|{
DECL|field|toRead
specifier|private
name|int
name|toRead
decl_stmt|;
DECL|field|pointer
specifier|private
name|long
name|pointer
decl_stmt|;
DECL|method|LazyField
specifier|public
name|LazyField
parameter_list|(
name|String
name|name
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|int
name|toRead
parameter_list|,
name|long
name|pointer
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
name|this
operator|.
name|toRead
operator|=
name|toRead
expr_stmt|;
name|this
operator|.
name|pointer
operator|=
name|pointer
expr_stmt|;
name|lazy
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|LazyField
specifier|public
name|LazyField
parameter_list|(
name|String
name|name
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|,
name|int
name|toRead
parameter_list|,
name|long
name|pointer
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
expr_stmt|;
name|this
operator|.
name|toRead
operator|=
name|toRead
expr_stmt|;
name|this
operator|.
name|pointer
operator|=
name|pointer
expr_stmt|;
name|lazy
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getFieldStream
specifier|private
name|IndexInput
name|getFieldStream
parameter_list|()
block|{
name|IndexInput
name|localFieldsStream
init|=
operator|(
name|IndexInput
operator|)
name|fieldsStreamTL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|localFieldsStream
operator|==
literal|null
condition|)
block|{
name|localFieldsStream
operator|=
operator|(
name|IndexInput
operator|)
name|cloneableFieldsStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|fieldsStreamTL
operator|.
name|set
argument_list|(
name|localFieldsStream
argument_list|)
expr_stmt|;
block|}
return|return
name|localFieldsStream
return|;
block|}
comment|/** The value of the field in Binary, or null.  If null, the Reader value,      * String value, or TokenStream value is used. Exactly one of stringValue(),       * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldsData
operator|==
literal|null
condition|)
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|toRead
index|]
decl_stmt|;
name|IndexInput
name|localFieldsStream
init|=
name|getFieldStream
argument_list|()
decl_stmt|;
comment|//Throw this IO Exception since IndexREader.document does so anyway, so probably not that big of a change for people
comment|//since they are already handling this exception when getting the document
try|try
block|{
name|localFieldsStream
operator|.
name|seek
argument_list|(
name|pointer
argument_list|)
expr_stmt|;
name|localFieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCompressed
operator|==
literal|true
condition|)
block|{
name|fieldsData
operator|=
name|uncompress
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldsData
operator|=
name|b
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FieldReaderException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|fieldsData
operator|instanceof
name|byte
index|[]
condition|?
operator|(
name|byte
index|[]
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/** The value of the field as a Reader, or null.  If null, the String value,      * binary value, or TokenStream value is used.  Exactly one of stringValue(),       * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fieldsData
operator|instanceof
name|Reader
condition|?
operator|(
name|Reader
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/** The value of the field as a TokesStream, or null.  If null, the Reader value,      * String value, or binary value is used. Exactly one of stringValue(),       * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fieldsData
operator|instanceof
name|TokenStream
condition|?
operator|(
name|TokenStream
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/** The value of the field as a String, or null.  If null, the Reader value,      * binary value, or TokenStream value is used.  Exactly one of stringValue(),       * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldsData
operator|==
literal|null
condition|)
block|{
name|IndexInput
name|localFieldsStream
init|=
name|getFieldStream
argument_list|()
decl_stmt|;
try|try
block|{
name|localFieldsStream
operator|.
name|seek
argument_list|(
name|pointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCompressed
condition|)
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|toRead
index|]
decl_stmt|;
name|localFieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|String
argument_list|(
name|uncompress
argument_list|(
name|b
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//read in chars b/c we already know the length we need to read
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|toRead
index|]
decl_stmt|;
name|localFieldsStream
operator|.
name|readChars
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FieldReaderException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|fieldsData
operator|instanceof
name|String
condition|?
operator|(
name|String
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
DECL|method|getPointer
specifier|public
name|long
name|getPointer
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|pointer
return|;
block|}
DECL|method|setPointer
specifier|public
name|void
name|setPointer
parameter_list|(
name|long
name|pointer
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|this
operator|.
name|pointer
operator|=
name|pointer
expr_stmt|;
block|}
DECL|method|getToRead
specifier|public
name|int
name|getToRead
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|toRead
return|;
block|}
DECL|method|setToRead
specifier|public
name|void
name|setToRead
parameter_list|(
name|int
name|toRead
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|this
operator|.
name|toRead
operator|=
name|toRead
expr_stmt|;
block|}
block|}
DECL|method|uncompress
specifier|private
specifier|final
name|byte
index|[]
name|uncompress
parameter_list|(
specifier|final
name|byte
index|[]
name|input
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|Inflater
name|decompressor
init|=
operator|new
name|Inflater
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|setInput
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|// Create an expandable byte array to hold the decompressed data
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|input
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Decompress the data
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|decompressor
operator|.
name|finished
argument_list|()
condition|)
block|{
try|try
block|{
name|int
name|count
init|=
name|decompressor
operator|.
name|inflate
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataFormatException
name|e
parameter_list|)
block|{
comment|// this will happen if the field is not compressed
name|CorruptIndexException
name|newException
init|=
operator|new
name|CorruptIndexException
argument_list|(
literal|"field data are in wrong format: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|newException
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|newException
throw|;
block|}
block|}
name|decompressor
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// Get the decompressed data
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|// Instances of this class hold field properties and data
comment|// for merge
DECL|class|FieldForMerge
specifier|final
specifier|static
class|class
name|FieldForMerge
extends|extends
name|AbstractField
block|{
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|this
operator|.
name|fieldsData
return|;
block|}
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
comment|// not needed for merge
return|return
literal|null
return|;
block|}
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
block|{
return|return
operator|(
name|byte
index|[]
operator|)
name|this
operator|.
name|fieldsData
return|;
block|}
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
comment|// not needed for merge
return|return
literal|null
return|;
block|}
DECL|method|FieldForMerge
specifier|public
name|FieldForMerge
parameter_list|(
name|Object
name|value
parameter_list|,
name|FieldInfo
name|fi
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|boolean
name|compressed
parameter_list|,
name|boolean
name|tokenize
parameter_list|)
block|{
name|this
operator|.
name|isStored
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
name|compressed
expr_stmt|;
name|this
operator|.
name|isBinary
operator|=
name|binary
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
name|tokenize
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|fi
operator|.
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
name|fi
operator|.
name|isIndexed
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|fi
operator|.
name|omitNorms
expr_stmt|;
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
name|fi
operator|.
name|storeOffsetWithTermVector
expr_stmt|;
name|this
operator|.
name|storePositionWithTermVector
operator|=
name|fi
operator|.
name|storePositionWithTermVector
expr_stmt|;
name|this
operator|.
name|storeTermVector
operator|=
name|fi
operator|.
name|storeTermVector
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

