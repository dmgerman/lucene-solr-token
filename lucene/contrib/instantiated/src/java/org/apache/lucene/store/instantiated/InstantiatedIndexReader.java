begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|FieldSelector
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
name|index
operator|.
name|IndexReader
operator|.
name|ReaderContext
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
name|util
operator|.
name|BitVector
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * An InstantiatedIndexReader is not a snapshot in time, it is completely in  * sync with the latest commit to the store!  *<p>  * Consider using InstantiatedIndex as if it was immutable.  */
end_comment

begin_class
DECL|class|InstantiatedIndexReader
specifier|public
class|class
name|InstantiatedIndexReader
extends|extends
name|IndexReader
block|{
DECL|field|index
specifier|private
specifier|final
name|InstantiatedIndex
name|index
decl_stmt|;
DECL|field|context
specifier|private
name|ReaderContext
name|context
init|=
operator|new
name|AtomicReaderContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|method|InstantiatedIndexReader
specifier|public
name|InstantiatedIndexReader
parameter_list|(
name|InstantiatedIndex
name|index
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|/**    * @return always true.    */
annotation|@
name|Override
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * An InstantiatedIndexReader is not a snapshot in time, it is completely in    * sync with the latest commit to the store!    *     * @return output from {@link InstantiatedIndex#getVersion()} in associated instantiated index.    */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|index
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * An InstantiatedIndexReader is always current!    *     * Check whether this IndexReader is still using the current (i.e., most    * recently committed) version of the index. If a writer has committed any    * changes to the index since this reader was opened, this will return    *<code>false</code>, in which case you must open a new IndexReader in    * order to see the changes. See the description of the<a    * href="IndexWriter.html#autoCommit"><code>autoCommit</code></a> flag    * which controls when the {@link IndexWriter} actually commits changes to the    * index.    *     * @return always true    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @throws UnsupportedOperationException unless overridden in subclass    */
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
DECL|method|getIndex
specifier|public
name|InstantiatedIndex
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
annotation|@
name|Override
DECL|method|getDeletedDocs
specifier|public
name|Bits
name|getDeletedDocs
parameter_list|()
block|{
return|return
operator|new
name|Bits
argument_list|()
block|{
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
operator|(
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|!=
literal|null
operator|&&
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|)
operator|||
operator|(
name|uncommittedDeletedDocuments
operator|!=
literal|null
operator|&&
name|uncommittedDeletedDocuments
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|)
return|;
block|}
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|field|uncommittedDeletedDocuments
specifier|private
name|BitVector
name|uncommittedDeletedDocuments
decl_stmt|;
DECL|field|uncommittedNormsByFieldNameAndDocumentNumber
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|NormUpdate
argument_list|>
argument_list|>
name|uncommittedNormsByFieldNameAndDocumentNumber
init|=
literal|null
decl_stmt|;
DECL|class|NormUpdate
specifier|private
class|class
name|NormUpdate
block|{
DECL|field|doc
specifier|private
name|int
name|doc
decl_stmt|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|method|NormUpdate
specifier|public
name|NormUpdate
parameter_list|(
name|int
name|doc
parameter_list|,
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// todo i suppose this value could be cached, but array#length and bitvector#count is fast.
name|int
name|numDocs
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|uncommittedDeletedDocuments
operator|!=
literal|null
condition|)
block|{
name|numDocs
operator|-=
name|uncommittedDeletedDocuments
operator|.
name|count
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|numDocs
operator|-=
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|count
argument_list|()
expr_stmt|;
block|}
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|!=
literal|null
operator|||
name|uncommittedDeletedDocuments
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
comment|// dont delete if already deleted
if|if
condition|(
operator|(
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|!=
literal|null
operator|&&
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|get
argument_list|(
name|docNum
argument_list|)
operator|)
operator|||
operator|(
name|uncommittedDeletedDocuments
operator|!=
literal|null
operator|&&
name|uncommittedDeletedDocuments
operator|.
name|get
argument_list|(
name|docNum
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|uncommittedDeletedDocuments
operator|==
literal|null
condition|)
block|{
name|uncommittedDeletedDocuments
operator|=
operator|new
name|BitVector
argument_list|(
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|uncommittedDeletedDocuments
operator|.
name|set
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|IOException
block|{
comment|// todo: read/write lock
name|uncommittedDeletedDocuments
operator|=
literal|null
expr_stmt|;
comment|// todo: read/write unlock
block|}
annotation|@
name|Override
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitUserData
parameter_list|)
throws|throws
name|IOException
block|{
comment|// todo: read/write lock
comment|// 1. update norms
if|if
condition|(
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|!=
literal|null
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
name|List
argument_list|<
name|NormUpdate
argument_list|>
argument_list|>
name|e
range|:
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|byte
index|[]
name|norms
init|=
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|NormUpdate
name|normUpdate
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|norms
index|[
name|normUpdate
operator|.
name|doc
index|]
operator|=
name|normUpdate
operator|.
name|value
expr_stmt|;
block|}
block|}
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|=
literal|null
expr_stmt|;
block|}
comment|// 2. remove deleted documents
if|if
condition|(
name|uncommittedDeletedDocuments
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|==
literal|null
condition|)
block|{
name|index
operator|.
name|setDeletedDocuments
argument_list|(
name|uncommittedDeletedDocuments
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|uncommittedDeletedDocuments
operator|.
name|size
argument_list|()
condition|;
name|d
operator|++
control|)
block|{
if|if
condition|(
name|uncommittedDeletedDocuments
operator|.
name|get
argument_list|(
name|d
argument_list|)
condition|)
block|{
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|set
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|uncommittedDeletedDocuments
operator|=
literal|null
expr_stmt|;
block|}
comment|// todo unlock read/writelock
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|// ignored
comment|// todo perhaps release all associated instances?
block|}
annotation|@
name|Override
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|(
name|FieldOption
name|fieldOption
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldSetting
name|fi
range|:
name|index
operator|.
name|getFieldSettings
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fi
operator|.
name|indexed
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storePayloads
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|STORES_PAYLOADS
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|indexed
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|indexed
operator|&&
name|fi
operator|.
name|storeTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_NO_TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storeTermVector
operator|==
literal|true
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|==
literal|false
operator|&&
name|fi
operator|.
name|storeOffsetWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|indexed
operator|&&
name|fi
operator|.
name|storeTermVector
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_WITH_TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storePositionWithTermVector
operator|&&
name|fi
operator|.
name|storeOffsetWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storeOffsetWithTermVector
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_OFFSET
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|fi
operator|.
name|storeOffsetWithTermVector
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|)
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION_OFFSET
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fieldSet
return|;
block|}
comment|/**    * Return the {@link org.apache.lucene.document.Document} at the<code>n</code><sup>th</sup>    * position.<p>    *<b>Warning!</b>    * The resulting document is the actual stored document instance    * and not a deserialized clone as retuned by an IndexReader    * over a {@link org.apache.lucene.store.Directory}.    * I.e., if you need to touch the document, clone it first!    *<p>    * This can also be seen as a feature for live changes of stored values,    * but be careful! Adding a field with an name unknown to the index    * or to a field with previously no stored values will make    * {@link org.apache.lucene.store.instantiated.InstantiatedIndexReader#getFieldNames(org.apache.lucene.index.IndexReader.FieldOption)}    * out of sync, causing problems for instance when merging the    * instantiated index to another index.<p>    * This implementation ignores the field selector! All stored fields are always returned!    *<p>    *    * @param n document number    * @param fieldSelector ignored    * @return The stored fields of the {@link org.apache.lucene.document.Document} at the nth position    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    *     * @see org.apache.lucene.document.Fieldable    * @see org.apache.lucene.document.FieldSelector    * @see org.apache.lucene.document.SetBasedFieldSelector    * @see org.apache.lucene.document.LoadFirstFieldSelector    */
annotation|@
name|Override
DECL|method|document
specifier|public
name|Document
name|document
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
return|return
name|document
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/**    * Returns the stored fields of the<code>n</code><sup>th</sup>    *<code>Document</code> in this index.    *<p>    *<b>Warning!</b>    * The resulting document is the actual stored document instance    * and not a deserialized clone as retuned by an IndexReader    * over a {@link org.apache.lucene.store.Directory}.    * I.e., if you need to touch the document, clone it first!    *<p>    * This can also be seen as a feature for live changes of stored values,    * but be careful! Adding a field with an name unknown to the index    * or to a field with previously no stored values will make    * {@link org.apache.lucene.store.instantiated.InstantiatedIndexReader#getFieldNames(org.apache.lucene.index.IndexReader.FieldOption)}    * out of sync, causing problems for instance when merging the    * instantiated index to another index.    *    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
annotation|@
name|Override
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|n
index|]
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/**    * never ever touch these values. it is the true values, unless norms have    * been touched.    */
annotation|@
name|Override
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|norms
init|=
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
comment|// todo a static final zero length attribute?
block|}
if|if
condition|(
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|!=
literal|null
condition|)
block|{
name|norms
operator|=
name|norms
operator|.
name|clone
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NormUpdate
argument_list|>
name|updated
init|=
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|updated
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NormUpdate
name|normUpdate
range|:
name|updated
control|)
block|{
name|norms
index|[
name|normUpdate
operator|.
name|doc
index|]
operator|=
name|normUpdate
operator|.
name|value
expr_stmt|;
block|}
block|}
block|}
return|return
name|norms
return|;
block|}
annotation|@
name|Override
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|==
literal|null
condition|)
block|{
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|NormUpdate
argument_list|>
argument_list|>
argument_list|(
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NormUpdate
argument_list|>
name|list
init|=
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|NormUpdate
argument_list|>
argument_list|()
expr_stmt|;
name|uncommittedNormsByFieldNameAndDocumentNumber
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|NormUpdate
argument_list|(
name|doc
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedTerm
name|term
init|=
name|getIndex
argument_list|()
operator|.
name|findTerm
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
block|{
if|if
condition|(
name|getIndex
argument_list|()
operator|.
name|getOrderedTerms
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Fields
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
block|{
specifier|final
name|InstantiatedTerm
index|[]
name|orderedTerms
init|=
name|getIndex
argument_list|()
operator|.
name|getOrderedTerms
argument_list|()
decl_stmt|;
return|return
operator|new
name|FieldsEnum
argument_list|()
block|{
name|int
name|upto
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|currentField
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
do|do
block|{
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|>=
name|orderedTerms
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
do|while
condition|(
name|orderedTerms
index|[
name|upto
index|]
operator|.
name|field
argument_list|()
operator|==
name|currentField
condition|)
do|;
name|currentField
operator|=
name|orderedTerms
index|[
name|upto
index|]
operator|.
name|field
argument_list|()
expr_stmt|;
return|return
name|currentField
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermsEnum
name|terms
parameter_list|()
block|{
return|return
operator|new
name|InstantiatedTermsEnum
argument_list|(
name|orderedTerms
argument_list|,
name|upto
argument_list|,
name|currentField
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Terms
name|terms
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
specifier|final
name|InstantiatedTerm
index|[]
name|orderedTerms
init|=
name|getIndex
argument_list|()
operator|.
name|getOrderedTerms
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|orderedTerms
argument_list|,
operator|new
name|Term
argument_list|(
name|field
argument_list|)
argument_list|,
name|InstantiatedTerm
operator|.
name|termComparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|i
operator|=
operator|-
name|i
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>=
name|orderedTerms
operator|.
name|length
operator|||
name|orderedTerms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
comment|// field does not exist
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|startLoc
init|=
name|i
decl_stmt|;
comment|// TODO: heavy to do this here; would be better to
comment|// do it up front& cache
name|long
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|upto
init|=
name|i
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|orderedTerms
operator|.
name|length
operator|&&
name|orderedTerms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|sum
operator|+=
name|orderedTerms
index|[
name|i
index|]
operator|.
name|getTotalTermFreq
argument_list|()
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
specifier|final
name|long
name|sumTotalTermFreq
init|=
name|sum
decl_stmt|;
return|return
operator|new
name|Terms
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|InstantiatedTermsEnum
argument_list|(
name|orderedTerms
argument_list|,
name|startLoc
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
name|sumTotalTermFreq
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
name|ReaderContext
name|getTopReaderContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
annotation|@
name|Override
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|docNumber
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TermFreqVector
index|[]
name|ret
init|=
operator|new
name|TermFreqVector
index|[
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
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
name|ret
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
operator|new
name|InstantiatedTermPositionVector
argument_list|(
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|==
literal|null
operator|||
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
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
operator|new
name|InstantiatedTermPositionVector
argument_list|(
name|doc
argument_list|,
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|!=
literal|null
operator|&&
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
name|tv
init|=
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|mapper
operator|.
name|setExpectations
argument_list|(
name|field
argument_list|,
name|tv
operator|.
name|size
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|InstantiatedTermDocumentInformation
name|tdi
range|:
name|tv
control|)
block|{
name|mapper
operator|.
name|map
argument_list|(
name|tdi
operator|.
name|getTerm
argument_list|()
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
operator|.
name|length
argument_list|,
name|tdi
operator|.
name|getTermOffsets
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|>
name|e
range|:
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|mapper
operator|.
name|setExpectations
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|InstantiatedTermDocumentInformation
name|tdi
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|mapper
operator|.
name|map
argument_list|(
name|tdi
operator|.
name|getTerm
argument_list|()
operator|.
name|getTerm
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
operator|.
name|length
argument_list|,
name|tdi
operator|.
name|getTermOffsets
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

