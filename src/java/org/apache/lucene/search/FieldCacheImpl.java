begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
operator|.
name|IndexReader
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
name|Term
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
name|TermDocs
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
name|TermEnum
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
name|util
operator|.
name|Locale
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
name|WeakHashMap
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

begin_comment
comment|/**  * Expert: The default cache implementation, storing all values in memory.  * A WeakHashMap is used for storage.  *  *<p>Created: May 19, 2004 4:40:36 PM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  */
end_comment

begin_class
DECL|class|FieldCacheImpl
class|class
name|FieldCacheImpl
implements|implements
name|FieldCache
block|{
comment|/** Expert: Internal cache. */
DECL|class|Cache
specifier|abstract
specifier|static
class|class
name|Cache
block|{
DECL|field|readerCache
specifier|private
specifier|final
name|Map
name|readerCache
init|=
operator|new
name|WeakHashMap
argument_list|()
decl_stmt|;
DECL|method|createValue
specifier|protected
specifier|abstract
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|innerCache
decl_stmt|;
name|Object
name|value
decl_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|innerCache
operator|=
operator|(
name|Map
operator|)
name|readerCache
operator|.
name|get
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerCache
operator|==
literal|null
condition|)
block|{
name|innerCache
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|readerCache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|innerCache
argument_list|)
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|innerCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|CreationPlaceholder
argument_list|()
expr_stmt|;
name|innerCache
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
if|if
condition|(
name|value
operator|instanceof
name|CreationPlaceholder
condition|)
block|{
synchronized|synchronized
init|(
name|value
init|)
block|{
name|CreationPlaceholder
name|progress
init|=
operator|(
name|CreationPlaceholder
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|progress
operator|.
name|value
operator|==
literal|null
condition|)
block|{
name|progress
operator|.
name|value
operator|=
name|createValue
argument_list|(
name|reader
argument_list|,
name|key
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|innerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|progress
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|progress
operator|.
name|value
return|;
block|}
block|}
return|return
name|value
return|;
block|}
block|}
DECL|class|CreationPlaceholder
specifier|static
specifier|final
class|class
name|CreationPlaceholder
block|{
DECL|field|value
name|Object
name|value
decl_stmt|;
block|}
comment|/** Expert: Every composite-key in the internal cache is of this type. */
DECL|class|Entry
specifier|static
class|class
name|Entry
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
comment|// which Fieldable
DECL|field|type
specifier|final
name|int
name|type
decl_stmt|;
comment|// which SortField type
DECL|field|custom
specifier|final
name|Object
name|custom
decl_stmt|;
comment|// which custom comparator
DECL|field|locale
specifier|final
name|Locale
name|locale
decl_stmt|;
comment|// the locale we're sorting (if string)
comment|/** Creates one of these objects. */
DECL|method|Entry
name|Entry
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|custom
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
block|}
comment|/** Creates one of these objects for a custom comparator. */
DECL|method|Entry
name|Entry
parameter_list|(
name|String
name|field
parameter_list|,
name|Object
name|custom
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|SortField
operator|.
name|CUSTOM
expr_stmt|;
name|this
operator|.
name|custom
operator|=
name|custom
expr_stmt|;
name|this
operator|.
name|locale
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Two of these are equal iff they reference the same field and type. */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Entry
condition|)
block|{
name|Entry
name|other
init|=
operator|(
name|Entry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|field
operator|==
name|field
operator|&&
name|other
operator|.
name|type
operator|==
name|type
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|locale
operator|==
literal|null
condition|?
name|locale
operator|==
literal|null
else|:
name|other
operator|.
name|locale
operator|.
name|equals
argument_list|(
name|locale
argument_list|)
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|custom
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|custom
operator|==
literal|null
condition|)
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|custom
operator|.
name|equals
argument_list|(
name|custom
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Composes a hashcode based on the field and type. */
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
operator|^
name|type
operator|^
operator|(
name|custom
operator|==
literal|null
condition|?
literal|0
else|:
name|custom
operator|.
name|hashCode
argument_list|()
operator|)
operator|^
operator|(
name|locale
operator|==
literal|null
condition|?
literal|0
else|:
name|locale
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
block|}
DECL|field|BYTE_PARSER
specifier|private
specifier|static
specifier|final
name|ByteParser
name|BYTE_PARSER
init|=
operator|new
name|ByteParser
argument_list|()
block|{
specifier|public
name|byte
name|parseByte
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|SHORT_PARSER
specifier|private
specifier|static
specifier|final
name|ShortParser
name|SHORT_PARSER
init|=
operator|new
name|ShortParser
argument_list|()
block|{
specifier|public
name|short
name|parseShort
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Short
operator|.
name|parseShort
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|INT_PARSER
specifier|private
specifier|static
specifier|final
name|IntParser
name|INT_PARSER
init|=
operator|new
name|IntParser
argument_list|()
block|{
specifier|public
name|int
name|parseInt
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|FLOAT_PARSER
specifier|private
specifier|static
specifier|final
name|FloatParser
name|FLOAT_PARSER
init|=
operator|new
name|FloatParser
argument_list|()
block|{
specifier|public
name|float
name|parseFloat
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBytes
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|BYTE_PARSER
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ByteParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|byte
index|[]
operator|)
name|bytesCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|field|bytesCache
name|Cache
name|bytesCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|ByteParser
name|parser
init|=
operator|(
name|ByteParser
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|byte
index|[]
name|retArray
init|=
operator|new
name|byte
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|byte
name|termval
init|=
name|parser
operator|.
name|parseByte
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getShorts
specifier|public
name|short
index|[]
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getShorts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|SHORT_PARSER
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getShorts
specifier|public
name|short
index|[]
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ShortParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|short
index|[]
operator|)
name|shortsCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|field|shortsCache
name|Cache
name|shortsCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|ShortParser
name|parser
init|=
operator|(
name|ShortParser
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|short
index|[]
name|retArray
init|=
operator|new
name|short
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|short
name|termval
init|=
name|parser
operator|.
name|parseShort
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|INT_PARSER
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|IntParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|int
index|[]
operator|)
name|intsCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|field|intsCache
name|Cache
name|intsCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|IntParser
name|parser
init|=
operator|(
name|IntParser
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|int
index|[]
name|retArray
init|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|int
name|termval
init|=
name|parser
operator|.
name|parseInt
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|FLOAT_PARSER
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|FloatParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|float
index|[]
operator|)
name|floatsCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
return|;
block|}
DECL|field|floatsCache
name|Cache
name|floatsCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|FloatParser
name|parser
init|=
operator|(
name|FloatParser
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|float
index|[]
name|retArray
init|=
operator|new
name|float
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|float
name|termval
init|=
name|parser
operator|.
name|parseFloat
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getStrings
specifier|public
name|String
index|[]
name|getStrings
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|String
index|[]
operator|)
name|stringsCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|field|stringsCache
name|Cache
name|stringsCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|fieldKey
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
operator|(
operator|(
name|String
operator|)
name|fieldKey
operator|)
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|retArray
init|=
operator|new
name|String
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|String
name|termval
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getStringIndex
specifier|public
name|StringIndex
name|getStringIndex
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|StringIndex
operator|)
name|stringsIndexCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|field|stringsIndexCache
name|Cache
name|stringsIndexCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|fieldKey
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
operator|(
operator|(
name|String
operator|)
name|fieldKey
operator|)
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|retArray
init|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|String
index|[]
name|mterms
init|=
operator|new
name|String
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|t
init|=
literal|0
decl_stmt|;
comment|// current term number
comment|// an entry for documents that have no terms in this field
comment|// should a document with no terms be at top or bottom?
comment|// this puts them at the top - if it is changed, FieldDocSortedHitQueue
comment|// needs to change as well.
name|mterms
index|[
name|t
operator|++
index|]
operator|=
literal|null
expr_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
comment|// store term text
comment|// we expect that there is at most one term per document
if|if
condition|(
name|t
operator|>=
name|mterms
operator|.
name|length
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"there are more terms than "
operator|+
literal|"documents in field \""
operator|+
name|field
operator|+
literal|"\", but it's impossible to sort on "
operator|+
literal|"tokenized fields"
argument_list|)
throw|;
name|mterms
index|[
name|t
index|]
operator|=
name|term
operator|.
name|text
argument_list|()
expr_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|t
expr_stmt|;
block|}
name|t
operator|++
expr_stmt|;
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|==
literal|0
condition|)
block|{
comment|// if there are no terms, make the term array
comment|// have a single null entry
name|mterms
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|<
name|mterms
operator|.
name|length
condition|)
block|{
comment|// if there are less terms than documents,
comment|// trim off the dead array space
name|String
index|[]
name|terms
init|=
operator|new
name|String
index|[
name|t
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mterms
argument_list|,
literal|0
argument_list|,
name|terms
argument_list|,
literal|0
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|mterms
operator|=
name|terms
expr_stmt|;
block|}
name|StringIndex
name|value
init|=
operator|new
name|StringIndex
argument_list|(
name|retArray
argument_list|,
name|mterms
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
block|}
decl_stmt|;
comment|/** The pattern used to detect integer values in a field */
comment|/** removed for java 1.3 compatibility    protected static final Pattern pIntegers = Pattern.compile ("[0-9\\-]+");    **/
comment|/** The pattern used to detect float values in a field */
comment|/**    * removed for java 1.3 compatibility    * protected static final Object pFloats = Pattern.compile ("[0-9+\\-\\.eEfFdD]+");    */
comment|// inherit javadocs
DECL|method|getAuto
specifier|public
name|Object
name|getAuto
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|autoCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|field|autoCache
name|Cache
name|autoCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|fieldKey
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
operator|(
operator|(
name|String
operator|)
name|fieldKey
operator|)
operator|.
name|intern
argument_list|()
decl_stmt|;
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no terms in field "
operator|+
name|field
operator|+
literal|" - cannot determine sort type"
argument_list|)
throw|;
block|}
name|Object
name|ret
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|String
name|termtext
init|=
name|term
operator|.
name|text
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|/**            * Java 1.4 level code:             if (pIntegers.matcher(termtext).matches())            return IntegerSortedHitQueue.comparator (reader, enumerator, field);             else if (pFloats.matcher(termtext).matches())            return FloatSortedHitQueue.comparator (reader, enumerator, field);            */
comment|// Java 1.3 level code:
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe1
parameter_list|)
block|{
try|try
block|{
name|Float
operator|.
name|parseFloat
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe2
parameter_list|)
block|{
name|ret
operator|=
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" does not appear to be indexed"
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
comment|// inherit javadocs
DECL|method|getCustom
specifier|public
name|Comparable
index|[]
name|getCustom
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|SortComparator
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|Comparable
index|[]
operator|)
name|customCache
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|comparator
argument_list|)
argument_list|)
return|;
block|}
DECL|field|customCache
name|Cache
name|customCache
init|=
operator|new
name|Cache
argument_list|()
block|{
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Object
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entryKey
decl_stmt|;
name|String
name|field
init|=
name|entry
operator|.
name|field
decl_stmt|;
name|SortComparator
name|comparator
init|=
operator|(
name|SortComparator
operator|)
name|entry
operator|.
name|custom
decl_stmt|;
specifier|final
name|Comparable
index|[]
name|retArray
init|=
operator|new
name|Comparable
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
operator|||
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
break|break;
name|Comparable
name|termval
init|=
name|comparator
operator|.
name|getComparable
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|retArray
index|[
name|termDocs
operator|.
name|doc
argument_list|()
index|]
operator|=
name|termval
expr_stmt|;
block|}
block|}
do|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|retArray
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

