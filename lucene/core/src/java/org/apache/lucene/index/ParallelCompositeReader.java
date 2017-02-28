begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IdentityHashMap
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
name|Set
import|;
end_import

begin_comment
comment|/** An {@link CompositeReader} which reads multiple, parallel indexes.  Each  * index added must have the same number of documents, and exactly the same  * number of leaves (with equal {@code maxDoc}), but typically each contains  * different fields. Deletions are taken from the first reader. Each document  * contains the union of the fields of all documents with the same document  * number.  When searching, matches for a query term are from the first index  * added that has the field.  *  *<p>This is useful, e.g., with collections that have large fields which  * change rarely and small fields that change more frequently.  The smaller  * fields may be re-indexed in a new index and both indexes may be searched  * together.  *   *<p><strong>Warning:</strong> It is up to you to make sure all indexes  * are created and modified the same way. For example, if you add  * documents to one index, you need to add the same documents in the  * same order to the other indexes.<em>Failure to do so will result in  * undefined behavior</em>.  * A good strategy to create suitable indexes with {@link IndexWriter} is to use  * {@link LogDocMergePolicy}, as this one does not reorder documents  * during merging (like {@code TieredMergePolicy}) and triggers merges  * by number of documents per segment. If you use different {@link MergePolicy}s  * it might happen that the segment structure of your index is no longer predictable.  */
end_comment

begin_class
DECL|class|ParallelCompositeReader
specifier|public
class|class
name|ParallelCompositeReader
extends|extends
name|BaseCompositeReader
argument_list|<
name|LeafReader
argument_list|>
block|{
DECL|field|closeSubReaders
specifier|private
specifier|final
name|boolean
name|closeSubReaders
decl_stmt|;
DECL|field|completeReaderSet
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexReader
argument_list|>
name|completeReaderSet
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|IndexReader
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cacheHelper
specifier|private
specifier|final
name|CacheHelper
name|cacheHelper
decl_stmt|;
comment|/** Create a ParallelCompositeReader based on the provided    *  readers; auto-closes the given readers on {@link #close()}. */
DECL|method|ParallelCompositeReader
specifier|public
name|ParallelCompositeReader
parameter_list|(
name|CompositeReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
comment|/** Create a ParallelCompositeReader based on the provided    *  readers. */
DECL|method|ParallelCompositeReader
specifier|public
name|ParallelCompositeReader
parameter_list|(
name|boolean
name|closeSubReaders
parameter_list|,
name|CompositeReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|closeSubReaders
argument_list|,
name|readers
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: create a ParallelCompositeReader based on the provided    *  readers and storedFieldReaders; when a document is    *  loaded, only storedFieldsReaders will be used. */
DECL|method|ParallelCompositeReader
specifier|public
name|ParallelCompositeReader
parameter_list|(
name|boolean
name|closeSubReaders
parameter_list|,
name|CompositeReader
index|[]
name|readers
parameter_list|,
name|CompositeReader
index|[]
name|storedFieldReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|prepareLeafReaders
argument_list|(
name|readers
argument_list|,
name|storedFieldReaders
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|closeSubReaders
operator|=
name|closeSubReaders
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|completeReaderSet
argument_list|,
name|readers
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|completeReaderSet
argument_list|,
name|storedFieldReaders
argument_list|)
expr_stmt|;
comment|// update ref-counts (like MultiReader):
if|if
condition|(
operator|!
name|closeSubReaders
condition|)
block|{
for|for
control|(
specifier|final
name|IndexReader
name|reader
range|:
name|completeReaderSet
control|)
block|{
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
block|}
comment|// finally add our own synthetic readers, so we close or decRef them, too (it does not matter what we do)
name|completeReaderSet
operator|.
name|addAll
argument_list|(
name|getSequentialSubReaders
argument_list|()
argument_list|)
expr_stmt|;
comment|// ParallelReader instances can be short-lived, which would make caching trappy
comment|// so we do not cache on them, unless they wrap a single reader in which
comment|// case we delegate
if|if
condition|(
name|readers
operator|.
name|length
operator|==
literal|1
operator|&&
name|storedFieldReaders
operator|.
name|length
operator|==
literal|1
operator|&&
name|readers
index|[
literal|0
index|]
operator|==
name|storedFieldReaders
index|[
literal|0
index|]
condition|)
block|{
name|cacheHelper
operator|=
name|readers
index|[
literal|0
index|]
operator|.
name|getReaderCacheHelper
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cacheHelper
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|prepareLeafReaders
specifier|private
specifier|static
name|LeafReader
index|[]
name|prepareLeafReaders
parameter_list|(
name|CompositeReader
index|[]
name|readers
parameter_list|,
name|CompositeReader
index|[]
name|storedFieldsReaders
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|readers
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|storedFieldsReaders
operator|.
name|length
operator|>
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"There must be at least one main reader if storedFieldsReaders are used."
argument_list|)
throw|;
return|return
operator|new
name|LeafReader
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|LeafReaderContext
argument_list|>
name|firstLeaves
init|=
name|readers
index|[
literal|0
index|]
operator|.
name|leaves
argument_list|()
decl_stmt|;
comment|// check compatibility:
specifier|final
name|int
name|maxDoc
init|=
name|readers
index|[
literal|0
index|]
operator|.
name|maxDoc
argument_list|()
decl_stmt|,
name|noLeaves
init|=
name|firstLeaves
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|leafMaxDoc
init|=
operator|new
name|int
index|[
name|noLeaves
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
name|noLeaves
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|LeafReader
name|r
init|=
name|firstLeaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|leafMaxDoc
index|[
name|i
index|]
operator|=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
name|validate
argument_list|(
name|readers
argument_list|,
name|maxDoc
argument_list|,
name|leafMaxDoc
argument_list|)
expr_stmt|;
name|validate
argument_list|(
name|storedFieldsReaders
argument_list|,
name|maxDoc
argument_list|,
name|leafMaxDoc
argument_list|)
expr_stmt|;
comment|// flatten structure of each Composite to just LeafReader[]
comment|// and combine parallel structure with ParallelLeafReaders:
specifier|final
name|LeafReader
index|[]
name|wrappedLeaves
init|=
operator|new
name|LeafReader
index|[
name|noLeaves
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
name|wrappedLeaves
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|LeafReader
index|[]
name|subs
init|=
operator|new
name|LeafReader
index|[
name|readers
operator|.
name|length
index|]
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
name|readers
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|subs
index|[
name|j
index|]
operator|=
name|readers
index|[
name|j
index|]
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
expr_stmt|;
block|}
specifier|final
name|LeafReader
index|[]
name|storedSubs
init|=
operator|new
name|LeafReader
index|[
name|storedFieldsReaders
operator|.
name|length
index|]
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
name|storedFieldsReaders
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|storedSubs
index|[
name|j
index|]
operator|=
name|storedFieldsReaders
index|[
name|j
index|]
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
expr_stmt|;
block|}
comment|// We pass true for closeSubs and we prevent touching of subreaders in doClose():
comment|// By this the synthetic throw-away readers used here are completely invisible to ref-counting
name|wrappedLeaves
index|[
name|i
index|]
operator|=
operator|new
name|ParallelLeafReader
argument_list|(
literal|true
argument_list|,
name|subs
argument_list|,
name|storedSubs
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
block|{}
block|}
expr_stmt|;
block|}
return|return
name|wrappedLeaves
return|;
block|}
block|}
DECL|method|validate
specifier|private
specifier|static
name|void
name|validate
parameter_list|(
name|CompositeReader
index|[]
name|readers
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|int
index|[]
name|leafMaxDoc
parameter_list|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CompositeReader
name|reader
init|=
name|readers
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|LeafReaderContext
argument_list|>
name|subs
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|!=
name|maxDoc
condition|)
block|{
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
block|}
specifier|final
name|int
name|noSubs
init|=
name|subs
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|noSubs
operator|!=
name|leafMaxDoc
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All readers must have same number of leaf readers"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|subIDX
init|=
literal|0
init|;
name|subIDX
operator|<
name|noSubs
condition|;
name|subIDX
operator|++
control|)
block|{
specifier|final
name|LeafReader
name|r
init|=
name|subs
operator|.
name|get
argument_list|(
name|subIDX
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|maxDoc
argument_list|()
operator|!=
name|leafMaxDoc
index|[
name|subIDX
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All leaf readers must have same corresponding subReader maxDoc"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|cacheHelper
return|;
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
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|IndexReader
name|reader
range|:
name|completeReaderSet
control|)
block|{
try|try
block|{
if|if
condition|(
name|closeSubReaders
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
name|ioe
operator|=
name|e
expr_stmt|;
block|}
block|}
comment|// throw the first exception
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
throw|throw
name|ioe
throw|;
block|}
block|}
end_class

end_unit

