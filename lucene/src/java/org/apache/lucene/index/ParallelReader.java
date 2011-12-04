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
name|index
operator|.
name|codecs
operator|.
name|PerDocValues
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
name|IndexDocValues
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
name|MapBackedSet
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
name|*
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

begin_comment
comment|/** An IndexReader which reads multiple, parallel indexes.  Each index added  * must have the same number of documents, but typically each contains  * different fields.  Each document contains the union of the fields of all  * documents with the same document number.  When searching, matches for a  * query term are from the first index added that has the field.  *  *<p>This is useful, e.g., with collections that have large fields which  * change rarely and small fields that change more frequently.  The smaller  * fields may be re-indexed in a new index and both indexes may be searched  * together.  *  *<p><strong>Warning:</strong> It is up to you to make sure all indexes  * are created and modified the same way. For example, if you add  * documents to one index, you need to add the same documents in the  * same order to the other indexes.<em>Failure to do so will result in  * undefined behavior</em>.  */
end_comment

begin_class
DECL|class|ParallelReader
specifier|public
class|class
name|ParallelReader
extends|extends
name|IndexReader
block|{
DECL|field|readers
specifier|private
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
DECL|field|decrefOnClose
specifier|private
name|List
argument_list|<
name|Boolean
argument_list|>
name|decrefOnClose
init|=
operator|new
name|ArrayList
argument_list|<
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|// remember which subreaders to decRef on close
DECL|field|incRefReaders
name|boolean
name|incRefReaders
init|=
literal|false
decl_stmt|;
DECL|field|fieldToReader
specifier|private
name|SortedMap
argument_list|<
name|String
argument_list|,
name|IndexReader
argument_list|>
name|fieldToReader
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|IndexReader
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|readerToFields
specifier|private
name|Map
argument_list|<
name|IndexReader
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|readerToFields
init|=
operator|new
name|HashMap
argument_list|<
name|IndexReader
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|storedFieldReaders
specifier|private
name|List
argument_list|<
name|IndexReader
argument_list|>
name|storedFieldReaders
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexReader
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|normsCache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|normsCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|topLevelReaderContext
specifier|private
specifier|final
name|ReaderContext
name|topLevelReaderContext
init|=
operator|new
name|AtomicReaderContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|hasDeletions
specifier|private
name|boolean
name|hasDeletions
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|ParallelFields
name|fields
init|=
operator|new
name|ParallelFields
argument_list|()
decl_stmt|;
DECL|field|perDocs
specifier|private
specifier|final
name|ParallelPerDocs
name|perDocs
init|=
operator|new
name|ParallelPerDocs
argument_list|()
decl_stmt|;
comment|/** Construct a ParallelReader.    *<p>Note that all subreaders are closed if this ParallelReader is closed.</p>   */
DECL|method|ParallelReader
specifier|public
name|ParallelReader
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a ParallelReader.    * @param closeSubReaders indicates whether the subreaders should be closed   * when this ParallelReader is closed   */
DECL|method|ParallelReader
specifier|public
name|ParallelReader
parameter_list|(
name|boolean
name|closeSubReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|incRefReaders
operator|=
operator|!
name|closeSubReaders
expr_stmt|;
name|readerFinishedListeners
operator|=
operator|new
name|MapBackedSet
argument_list|<
name|ReaderFinishedListener
argument_list|>
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|ReaderFinishedListener
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ParallelReader("
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|IndexReader
argument_list|>
name|iter
init|=
name|readers
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Add an IndexReader.   * @throws IOException if there is a low-level IO error   */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Add an IndexReader whose stored fields will not be returned.  This can   * accelerate search when stored fields are only needed from a subset of   * the IndexReaders.   *   * @throws IllegalArgumentException if not all indexes contain the same number   *     of documents   * @throws IllegalArgumentException if not all indexes have the same value   *     of {@link IndexReader#maxDoc()}   * @throws IOException if there is a low-level IO error   */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|ignoreStoredFields
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|readers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|reader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|reader
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|!=
name|maxDoc
condition|)
comment|// check compatibility
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All readers must have same maxDoc: "
operator|+
name|maxDoc
operator|+
literal|"!="
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|!=
name|numDocs
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All readers must have same numDocs: "
operator|+
name|numDocs
operator|+
literal|"!="
operator|+
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
throw|;
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|readerToFields
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|fields
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|field
range|:
name|fields
control|)
block|{
comment|// update fieldToReader map
if|if
condition|(
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
condition|)
block|{
name|fieldToReader
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
operator|.
name|terms
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|perDocs
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ignoreStoredFields
condition|)
name|storedFieldReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// add to storedFieldReaders
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|incRefReaders
condition|)
block|{
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
name|decrefOnClose
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|incRefReaders
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|normsCache
init|)
block|{
name|normsCache
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// TODO: don't need to clear this for all fields really?
block|}
block|}
DECL|class|ParallelFieldsEnum
specifier|private
class|class
name|ParallelFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|currentField
name|String
name|currentField
decl_stmt|;
DECL|field|keys
name|Iterator
argument_list|<
name|String
argument_list|>
name|keys
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|Fields
name|fields
decl_stmt|;
DECL|method|ParallelFieldsEnum
name|ParallelFieldsEnum
parameter_list|(
name|Fields
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|keys
operator|=
name|fieldToReader
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentField
operator|=
name|keys
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentField
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|currentField
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|terms
argument_list|(
name|currentField
argument_list|)
return|;
block|}
block|}
comment|// Single instance of this, per ParallelReader instance
DECL|class|ParallelFields
specifier|private
class|class
name|ParallelFields
extends|extends
name|Fields
block|{
DECL|field|fields
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|terms
argument_list|)
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
return|return
operator|new
name|ParallelFieldsEnum
argument_list|(
name|this
argument_list|)
return|;
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
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueFieldCount
specifier|public
name|int
name|getUniqueFieldCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|readers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|synchronized
name|Object
name|clone
parameter_list|()
block|{
comment|// doReopen calls ensureOpen
try|try
block|{
return|return
name|doReopen
argument_list|(
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Tries to reopen the subreaders.    *<br>    * If one or more subreaders could be re-opened (i. e. subReader.reopen()     * returned a new instance != subReader), then a new ParallelReader instance     * is returned, otherwise null is returned.    *<p>    * A re-opened instance might share one or more subreaders with the old     * instance. Index modification operations result in undefined behavior    * when performed before the old instance is closed.    * (see {@link IndexReader#openIfChanged}).    *<p>    * If subreaders are shared, then the reference count of those    * readers is increased to ensure that the subreaders remain open    * until the last referring reader is closed.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error     */
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|synchronized
name|IndexReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
comment|// doReopen calls ensureOpen
return|return
name|doReopen
argument_list|(
literal|false
argument_list|)
return|;
block|}
DECL|method|doReopen
specifier|protected
name|IndexReader
name|doReopen
parameter_list|(
name|boolean
name|doClone
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|boolean
name|reopened
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|IndexReader
argument_list|>
name|newReaders
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexReader
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|IndexReader
name|oldReader
range|:
name|readers
control|)
block|{
name|IndexReader
name|newReader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|doClone
condition|)
block|{
name|newReader
operator|=
operator|(
name|IndexReader
operator|)
name|oldReader
operator|.
name|clone
argument_list|()
expr_stmt|;
name|reopened
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|newReader
operator|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|)
expr_stmt|;
if|if
condition|(
name|newReader
operator|!=
literal|null
condition|)
block|{
name|reopened
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|newReader
operator|=
name|oldReader
expr_stmt|;
block|}
block|}
name|newReaders
operator|.
name|add
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
block|}
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
operator|&&
name|reopened
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|newReaders
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|r
init|=
name|newReaders
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
try|try
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// keep going - we want to clean up as much as possible
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|reopened
condition|)
block|{
name|List
argument_list|<
name|Boolean
argument_list|>
name|newDecrefOnClose
init|=
operator|new
name|ArrayList
argument_list|<
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: maybe add a special reopen-ctor for norm-copying?
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|oldReader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|IndexReader
name|newReader
init|=
name|newReaders
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReader
operator|==
name|oldReader
condition|)
block|{
name|newDecrefOnClose
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|newReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// this is a new subreader instance, so on close() we don't
comment|// decRef but close it
name|newDecrefOnClose
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
name|pr
operator|.
name|add
argument_list|(
name|newReader
argument_list|,
operator|!
name|storedFieldReaders
operator|.
name|contains
argument_list|(
name|oldReader
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pr
operator|.
name|decrefOnClose
operator|=
name|newDecrefOnClose
expr_stmt|;
name|pr
operator|.
name|incRefReaders
operator|=
name|incRefReaders
expr_stmt|;
return|return
name|pr
return|;
block|}
else|else
block|{
comment|// No subreader was refreshed
return|return
literal|null
return|;
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
comment|// Don't call ensureOpen() here (it could affect performance)
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
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|maxDoc
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|hasDeletions
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|IndexReader
name|reader
range|:
name|storedFieldReaders
control|)
block|{
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
comment|// get all vectors
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|ParallelFields
name|fields
init|=
operator|new
name|ParallelFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexReader
argument_list|>
name|ent
range|:
name|fieldToReader
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Terms
name|vector
init|=
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getTermVector
argument_list|(
name|docID
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|vector
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|false
else|:
name|reader
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|norms
specifier|public
specifier|synchronized
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|byte
index|[]
name|bytes
init|=
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
return|return
name|bytes
return|;
if|if
condition|(
operator|!
name|hasNorms
argument_list|(
name|field
argument_list|)
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|normsCache
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
comment|// cached omitNorms, not missing key
return|return
literal|null
return|;
name|bytes
operator|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|normsCache
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|fieldToReader
operator|.
name|get
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|0
else|:
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|0
else|:
name|reader
operator|.
name|docFreq
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
return|;
block|}
comment|/**    * Checks recursively if all subreaders are up to date.     */
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|IndexReader
name|reader
range|:
name|readers
control|)
block|{
if|if
condition|(
operator|!
name|reader
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subreaders are up to date
return|return
literal|true
return|;
block|}
comment|/** Not implemented.    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ParallelReader does not support this method."
argument_list|)
throw|;
block|}
comment|// for testing
DECL|method|getSubReaders
name|IndexReader
index|[]
name|getSubReaders
parameter_list|()
block|{
return|return
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
return|;
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
for|for
control|(
specifier|final
name|IndexReader
name|reader
range|:
name|readers
control|)
name|reader
operator|.
name|commit
argument_list|(
name|commitUserData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
specifier|synchronized
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|decrefOnClose
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
specifier|final
name|IndexReader
name|reader
range|:
name|readers
control|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|topLevelReaderContext
return|;
block|}
annotation|@
name|Override
DECL|method|addReaderFinishedListener
specifier|public
name|void
name|addReaderFinishedListener
parameter_list|(
name|ReaderFinishedListener
name|listener
parameter_list|)
block|{
name|super
operator|.
name|addReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexReader
name|reader
range|:
name|readers
control|)
block|{
name|reader
operator|.
name|addReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeReaderFinishedListener
specifier|public
name|void
name|removeReaderFinishedListener
parameter_list|(
name|ReaderFinishedListener
name|listener
parameter_list|)
block|{
name|super
operator|.
name|removeReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexReader
name|reader
range|:
name|readers
control|)
block|{
name|reader
operator|.
name|removeReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|perDocValues
specifier|public
name|PerDocValues
name|perDocValues
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|perDocs
return|;
block|}
comment|// Single instance of this, per ParallelReader instance
DECL|class|ParallelPerDocs
specifier|private
specifier|static
specifier|final
class|class
name|ParallelPerDocs
extends|extends
name|PerDocValues
block|{
DECL|field|fields
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|IndexDocValues
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|IndexDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|addField
name|void
name|addField
parameter_list|(
name|String
name|field
parameter_list|,
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|PerDocValues
name|perDocs
init|=
name|MultiPerDocValues
operator|.
name|getPerDocs
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|perDocs
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|perDocs
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|IndexDocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|()
block|{
return|return
name|fields
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

