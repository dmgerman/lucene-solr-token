begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReader
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
name|IndexWriter
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
name|MergePolicy
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
name|MergeState
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
name|MultiReader
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
name|SegmentInfoPerCommit
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
name|SegmentInfos
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
name|SlowCompositeReaderWrapper
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
name|packed
operator|.
name|MonotonicAppendingLongBuffer
import|;
end_import

begin_comment
comment|/** A {@link MergePolicy} that reorders documents according to a {@link Sorter}  *  before merging them. As a consequence, all segments resulting from a merge  *  will be sorted while segments resulting from a flush will be in the order  *  in which documents have been added.  *<p>Never use this {@link MergePolicy} if you rely on  *  {@link IndexWriter#addDocuments(Iterable, org.apache.lucene.analysis.Analyzer)}  *  to have sequentially-assigned doc IDs, this policy will scatter doc IDs.  *  @lucene.experimental */
end_comment

begin_class
DECL|class|SortingMergePolicy
specifier|public
specifier|final
class|class
name|SortingMergePolicy
extends|extends
name|MergePolicy
block|{
DECL|class|SortingOneMerge
class|class
name|SortingOneMerge
extends|extends
name|OneMerge
block|{
DECL|field|unsortedReaders
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|unsortedReaders
decl_stmt|;
DECL|field|docMap
name|Sorter
operator|.
name|DocMap
name|docMap
decl_stmt|;
DECL|field|sortedView
name|AtomicReader
name|sortedView
decl_stmt|;
DECL|method|SortingOneMerge
name|SortingOneMerge
parameter_list|(
name|List
argument_list|<
name|SegmentInfoPerCommit
argument_list|>
name|segments
parameter_list|)
block|{
name|super
argument_list|(
name|segments
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeReaders
specifier|public
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|getMergeReaders
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|unsortedReaders
operator|==
literal|null
condition|)
block|{
name|unsortedReaders
operator|=
name|super
operator|.
name|getMergeReaders
argument_list|()
expr_stmt|;
specifier|final
name|AtomicReader
name|atomicView
decl_stmt|;
if|if
condition|(
name|unsortedReaders
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|atomicView
operator|=
name|unsortedReaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|IndexReader
name|multiReader
init|=
operator|new
name|MultiReader
argument_list|(
name|unsortedReaders
operator|.
name|toArray
argument_list|(
operator|new
name|AtomicReader
index|[
name|unsortedReaders
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|atomicView
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|multiReader
argument_list|)
expr_stmt|;
block|}
name|docMap
operator|=
name|sorter
operator|.
name|sort
argument_list|(
name|atomicView
argument_list|)
expr_stmt|;
name|sortedView
operator|=
name|SortingAtomicReader
operator|.
name|wrap
argument_list|(
name|atomicView
argument_list|,
name|docMap
argument_list|)
expr_stmt|;
block|}
comment|// a null doc map means that the readers are already sorted
return|return
name|docMap
operator|==
literal|null
condition|?
name|unsortedReaders
else|:
name|Collections
operator|.
name|singletonList
argument_list|(
name|sortedView
argument_list|)
return|;
block|}
DECL|method|getDeletes
specifier|private
name|MonotonicAppendingLongBuffer
name|getDeletes
parameter_list|(
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|readers
parameter_list|)
block|{
name|MonotonicAppendingLongBuffer
name|deletes
init|=
operator|new
name|MonotonicAppendingLongBuffer
argument_list|()
decl_stmt|;
name|int
name|deleteCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|readers
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
operator|++
name|deleteCount
expr_stmt|;
block|}
else|else
block|{
name|deletes
operator|.
name|add
argument_list|(
name|deleteCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|deletes
return|;
block|}
annotation|@
name|Override
DECL|method|getDocMap
specifier|public
name|MergePolicy
operator|.
name|DocMap
name|getDocMap
parameter_list|(
specifier|final
name|MergeState
name|mergeState
parameter_list|)
block|{
if|if
condition|(
name|unsortedReaders
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
if|if
condition|(
name|docMap
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|getDocMap
argument_list|(
name|mergeState
argument_list|)
return|;
block|}
assert|assert
name|mergeState
operator|.
name|docMaps
operator|.
name|length
operator|==
literal|1
assert|;
comment|// we returned a singleton reader
specifier|final
name|MonotonicAppendingLongBuffer
name|deletes
init|=
name|getDeletes
argument_list|(
name|unsortedReaders
argument_list|)
decl_stmt|;
return|return
operator|new
name|MergePolicy
operator|.
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|map
parameter_list|(
name|int
name|old
parameter_list|)
block|{
specifier|final
name|int
name|oldWithDeletes
init|=
name|old
operator|+
operator|(
name|int
operator|)
name|deletes
operator|.
name|get
argument_list|(
name|old
argument_list|)
decl_stmt|;
specifier|final
name|int
name|newWithDeletes
init|=
name|docMap
operator|.
name|oldToNew
argument_list|(
name|oldWithDeletes
argument_list|)
decl_stmt|;
return|return
name|mergeState
operator|.
name|docMaps
index|[
literal|0
index|]
operator|.
name|get
argument_list|(
name|newWithDeletes
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|SortingMergeSpecification
class|class
name|SortingMergeSpecification
extends|extends
name|MergeSpecification
block|{
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|OneMerge
name|merge
parameter_list|)
block|{
name|super
operator|.
name|add
argument_list|(
operator|new
name|SortingOneMerge
argument_list|(
name|merge
operator|.
name|segments
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|segString
specifier|public
name|String
name|segString
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
return|return
literal|"SortingMergeSpec("
operator|+
name|super
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
operator|+
literal|", sorter="
operator|+
name|sorter
operator|+
literal|")"
return|;
block|}
block|}
DECL|method|sortedMergeSpecification
specifier|private
name|MergeSpecification
name|sortedMergeSpecification
parameter_list|(
name|MergeSpecification
name|specification
parameter_list|)
block|{
if|if
condition|(
name|specification
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MergeSpecification
name|sortingSpec
init|=
operator|new
name|SortingMergeSpecification
argument_list|()
decl_stmt|;
for|for
control|(
name|OneMerge
name|merge
range|:
name|specification
operator|.
name|merges
control|)
block|{
name|sortingSpec
operator|.
name|add
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
return|return
name|sortingSpec
return|;
block|}
DECL|field|in
specifier|final
name|MergePolicy
name|in
decl_stmt|;
DECL|field|sorter
specifier|final
name|Sorter
name|sorter
decl_stmt|;
comment|/** Create a new {@link MergePolicy} that sorts documents with<code>sorter</code>. */
DECL|method|SortingMergePolicy
specifier|public
name|SortingMergePolicy
parameter_list|(
name|MergePolicy
name|in
parameter_list|,
name|Sorter
name|sorter
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|sorter
operator|=
name|sorter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|MergeTrigger
name|mergeTrigger
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sortedMergeSpecification
argument_list|(
name|in
operator|.
name|findMerges
argument_list|(
name|mergeTrigger
argument_list|,
name|segmentInfos
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedMerges
specifier|public
name|MergeSpecification
name|findForcedMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Map
argument_list|<
name|SegmentInfoPerCommit
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sortedMergeSpecification
argument_list|(
name|in
operator|.
name|findForcedMerges
argument_list|(
name|segmentInfos
argument_list|,
name|maxSegmentCount
argument_list|,
name|segmentsToMerge
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedDeletesMerges
specifier|public
name|MergeSpecification
name|findForcedDeletesMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sortedMergeSpecification
argument_list|(
name|in
operator|.
name|findForcedDeletesMerges
argument_list|(
name|segmentInfos
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MergePolicy
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SortingMergePolicy
argument_list|(
name|in
operator|.
name|clone
argument_list|()
argument_list|,
name|sorter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|useCompoundFile
specifier|public
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|SegmentInfoPerCommit
name|newSegment
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|useCompoundFile
argument_list|(
name|segments
argument_list|,
name|newSegment
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setIndexWriter
specifier|public
name|void
name|setIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|in
operator|.
name|setIndexWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SortingMergePolicy("
operator|+
name|in
operator|+
literal|", sorter="
operator|+
name|sorter
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

