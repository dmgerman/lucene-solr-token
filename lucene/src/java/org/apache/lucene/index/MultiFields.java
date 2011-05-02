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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|values
operator|.
name|DocValues
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
name|values
operator|.
name|MultiDocValues
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
name|values
operator|.
name|Type
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
name|values
operator|.
name|MultiDocValues
operator|.
name|DocValuesIndex
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ReaderUtil
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
name|ReaderUtil
operator|.
name|Gather
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|Bits
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

begin_comment
comment|/**  * Exposes flex API, merged from flex API of sub-segments.  * This is useful when you're interacting with an {@link  * IndexReader} implementation that consists of sequential  * sub-readers (eg DirectoryReader or {@link  * MultiReader}).  *  *<p><b>NOTE</b>: for multi readers, you'll get better  * performance by gathering the sub readers using {@link  * ReaderUtil#gatherSubReaders} and then operate per-reader,  * instead of using this class.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiFields
specifier|public
specifier|final
class|class
name|MultiFields
extends|extends
name|Fields
block|{
DECL|field|subs
specifier|private
specifier|final
name|Fields
index|[]
name|subs
decl_stmt|;
DECL|field|subSlices
specifier|private
specifier|final
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
decl_stmt|;
DECL|field|terms
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
name|terms
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Returns a single {@link Fields} instance for this    *  reader, merging fields/terms/docs/positions on the    *  fly.  This method will not return null.    *    *<p><b>NOTE</b>: this is a slow way to access postings.    *  It's better to get the sub-readers (using {@link    *  Gather}) and iterate through them    *  yourself. */
DECL|method|getFields
specifier|public
specifier|static
name|Fields
name|getFields
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
index|[]
name|subs
init|=
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|subs
operator|==
literal|null
condition|)
block|{
comment|// already an atomic reader
return|return
name|r
operator|.
name|fields
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// no fields
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|getFields
argument_list|(
name|subs
index|[
literal|0
index|]
argument_list|)
return|;
block|}
else|else
block|{
name|Fields
name|currentFields
init|=
name|r
operator|.
name|retrieveFields
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentFields
operator|==
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Fields
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Fields
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
operator|new
name|ReaderUtil
operator|.
name|Gather
argument_list|(
name|r
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|f
init|=
name|r
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|slices
operator|.
name|add
argument_list|(
operator|new
name|ReaderUtil
operator|.
name|Slice
argument_list|(
name|base
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|fields
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|currentFields
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentFields
operator|=
operator|new
name|MultiFields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
name|Fields
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices
operator|.
name|toArray
argument_list|(
name|ReaderUtil
operator|.
name|Slice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|storeFields
argument_list|(
name|currentFields
argument_list|)
expr_stmt|;
block|}
return|return
name|currentFields
return|;
block|}
block|}
DECL|class|MultiReaderBits
specifier|private
specifier|static
class|class
name|MultiReaderBits
implements|implements
name|Bits
block|{
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|readers
specifier|private
specifier|final
name|IndexReader
index|[]
name|readers
decl_stmt|;
DECL|field|delDocs
specifier|private
specifier|final
name|Bits
index|[]
name|delDocs
decl_stmt|;
DECL|method|MultiReaderBits
specifier|public
name|MultiReaderBits
parameter_list|(
name|int
index|[]
name|starts
parameter_list|,
name|IndexReader
index|[]
name|readers
parameter_list|)
block|{
assert|assert
name|readers
operator|.
name|length
operator|==
name|starts
operator|.
name|length
operator|-
literal|1
assert|;
name|this
operator|.
name|starts
operator|=
name|starts
expr_stmt|;
name|this
operator|.
name|readers
operator|=
name|readers
expr_stmt|;
name|delDocs
operator|=
operator|new
name|Bits
index|[
name|readers
operator|.
name|length
index|]
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|delDocs
index|[
name|i
index|]
operator|=
name|readers
index|[
name|i
index|]
operator|.
name|getDeletedDocs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|sub
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|starts
argument_list|)
decl_stmt|;
name|Bits
name|dels
init|=
name|delDocs
index|[
name|sub
index|]
decl_stmt|;
if|if
condition|(
name|dels
operator|==
literal|null
condition|)
block|{
comment|// NOTE: this is not sync'd but multiple threads can
comment|// come through here; I think this is OK -- worst
comment|// case is more than 1 thread ends up filling in the
comment|// sub Bits
name|dels
operator|=
name|readers
index|[
name|sub
index|]
operator|.
name|getDeletedDocs
argument_list|()
expr_stmt|;
if|if
condition|(
name|dels
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|delDocs
index|[
name|sub
index|]
operator|=
name|dels
expr_stmt|;
block|}
block|}
return|return
name|dels
operator|.
name|get
argument_list|(
name|doc
operator|-
name|starts
index|[
name|sub
index|]
argument_list|)
return|;
block|}
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|starts
index|[
name|starts
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
block|}
DECL|method|getDeletedDocs
specifier|public
specifier|static
name|Bits
name|getDeletedDocs
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|Bits
name|result
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|IndexReader
argument_list|>
name|readers
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexReader
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|starts
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|maxDoc
init|=
operator|new
name|ReaderUtil
operator|.
name|Gather
argument_list|(
name|r
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
comment|// record all delDocs, even if they are null
name|readers
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|starts
operator|.
name|add
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|run
argument_list|()
decl_stmt|;
name|starts
operator|.
name|add
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
assert|assert
name|readers
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
if|if
condition|(
name|readers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// Only one actual sub reader -- optimize this case
name|result
operator|=
name|readers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDeletedDocs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
index|[]
name|startsArray
init|=
operator|new
name|int
index|[
name|starts
operator|.
name|size
argument_list|()
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
name|startsArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|startsArray
index|[
name|i
index|]
operator|=
name|starts
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|MultiReaderBits
argument_list|(
name|startsArray
argument_list|,
name|readers
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**  This method may return null if the field does not exist.*/
DECL|method|getTerms
specifier|public
specifier|static
name|Terms
name|getTerms
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|getFields
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
comment|/** Returns {@link DocsEnum} for the specified field&    *  term.  This may return null if the term does not    *  exist. */
DECL|method|getTermDocsEnum
specifier|public
specifier|static
name|DocsEnum
name|getTermDocsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|!=
literal|null
assert|;
specifier|final
name|Terms
name|terms
init|=
name|getTerms
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
return|return
name|terms
operator|.
name|docs
argument_list|(
name|skipDocs
argument_list|,
name|term
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Returns {@link DocsAndPositionsEnum} for the specified    *  field& term.  This may return null if the term does    *  not exist or positions were not indexed. */
DECL|method|getTermPositionsEnum
specifier|public
specifier|static
name|DocsAndPositionsEnum
name|getTermPositionsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|!=
literal|null
assert|;
specifier|final
name|Terms
name|terms
init|=
name|getTerms
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
return|return
name|terms
operator|.
name|docsAndPositions
argument_list|(
name|skipDocs
argument_list|,
name|term
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|MultiFields
specifier|public
name|MultiFields
parameter_list|(
name|Fields
index|[]
name|subs
parameter_list|,
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
parameter_list|)
block|{
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|FieldsEnum
argument_list|>
name|fieldsEnums
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldsEnum
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
name|fieldsSlices
init|=
operator|new
name|ArrayList
argument_list|<
name|ReaderUtil
operator|.
name|Slice
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fieldsEnums
operator|.
name|add
argument_list|(
name|subs
index|[
name|i
index|]
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|fieldsSlices
operator|.
name|add
argument_list|(
name|subSlices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldsEnums
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|FieldsEnum
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
return|return
operator|new
name|MultiFieldsEnum
argument_list|(
name|fieldsEnums
operator|.
name|toArray
argument_list|(
name|FieldsEnum
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|fieldsSlices
operator|.
name|toArray
argument_list|(
name|ReaderUtil
operator|.
name|Slice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|result
init|=
name|terms
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
name|result
return|;
comment|// Lazy init: first time this field is requested, we
comment|// create& add to terms:
specifier|final
name|List
argument_list|<
name|Terms
argument_list|>
name|subs2
init|=
operator|new
name|ArrayList
argument_list|<
name|Terms
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
name|slices2
init|=
operator|new
name|ArrayList
argument_list|<
name|ReaderUtil
operator|.
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
comment|// Gather all sub-readers that share this field
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|subs
index|[
name|i
index|]
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|subs2
operator|.
name|add
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|slices2
operator|.
name|add
argument_list|(
name|subSlices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subs2
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
operator|=
literal|null
expr_stmt|;
comment|// don't cache this case with an unbounded cache, since the number of fields that don't exist
comment|// is unbounded.
block|}
else|else
block|{
name|result
operator|=
operator|new
name|MultiTerms
argument_list|(
name|subs2
operator|.
name|toArray
argument_list|(
name|Terms
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
name|slices2
operator|.
name|toArray
argument_list|(
name|ReaderUtil
operator|.
name|Slice
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

