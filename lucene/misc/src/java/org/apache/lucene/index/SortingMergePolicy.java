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
name|ArrayList
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
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_comment
comment|// javadocs
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
name|LeafReader
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
name|MergeTrigger
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
name|SegmentCommitInfo
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
name|SegmentInfo
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
name|SegmentReader
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
name|search
operator|.
name|Sort
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
name|InfoStream
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
name|PackedInts
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
name|PackedLongValues
import|;
end_import

begin_comment
comment|/** A {@link MergePolicy} that reorders documents according to a {@link Sort}  *  before merging them. As a consequence, all segments resulting from a merge  *  will be sorted while segments resulting from a flush will be in the order  *  in which documents have been added.  *<p><b>NOTE</b>: Never use this policy if you rely on  *  {@link IndexWriter#addDocuments(Iterable) IndexWriter.addDocuments}  *  to have sequentially-assigned doc IDs, this policy will scatter doc IDs.  *<p><b>NOTE</b>: This policy should only be used with idempotent {@code Sort}s   *  so that the order of segments is predictable. For example, using   *  {@link Sort#INDEXORDER} in reverse (which is not idempotent) will make   *  the order of documents in a segment depend on the number of times the segment   *  has been merged.  *  @lucene.experimental */
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
comment|/**    * Put in the {@link SegmentInfo#getDiagnostics() diagnostics} to denote that    * this segment is sorted.    */
DECL|field|SORTER_ID_PROP
specifier|public
specifier|static
specifier|final
name|String
name|SORTER_ID_PROP
init|=
literal|"sorter"
decl_stmt|;
DECL|class|SortingOneMerge
class|class
name|SortingOneMerge
extends|extends
name|OneMerge
block|{
DECL|field|unsortedReaders
name|List
argument_list|<
name|CodecReader
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
name|LeafReader
name|sortedView
decl_stmt|;
DECL|field|infoStream
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
DECL|method|SortingOneMerge
name|SortingOneMerge
parameter_list|(
name|List
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|segments
parameter_list|,
name|InfoStream
name|infoStream
parameter_list|)
block|{
name|super
argument_list|(
name|segments
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeReaders
specifier|public
name|List
argument_list|<
name|CodecReader
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
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SMP"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"SMP"
argument_list|,
literal|"sorting "
operator|+
name|unsortedReaders
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafReader
name|leaf
range|:
name|unsortedReaders
control|)
block|{
name|String
name|sortDescription
init|=
name|getSortDescription
argument_list|(
name|leaf
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortDescription
operator|==
literal|null
condition|)
block|{
name|sortDescription
operator|=
literal|"not sorted"
expr_stmt|;
block|}
name|infoStream
operator|.
name|message
argument_list|(
literal|"SMP"
argument_list|,
literal|"seg="
operator|+
name|leaf
operator|+
literal|" "
operator|+
name|sortDescription
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wrap readers, to be optimal for merge;
name|List
argument_list|<
name|LeafReader
argument_list|>
name|wrapped
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|unsortedReaders
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LeafReader
name|leaf
range|:
name|unsortedReaders
control|)
block|{
if|if
condition|(
name|leaf
operator|instanceof
name|SegmentReader
condition|)
block|{
name|leaf
operator|=
operator|new
name|MergeReaderWrapper
argument_list|(
operator|(
name|SegmentReader
operator|)
name|leaf
argument_list|)
expr_stmt|;
block|}
name|wrapped
operator|.
name|add
argument_list|(
name|leaf
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LeafReader
name|atomicView
decl_stmt|;
if|if
condition|(
name|wrapped
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|atomicView
operator|=
name|wrapped
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
name|CompositeReader
name|multiReader
init|=
operator|new
name|MultiReader
argument_list|(
name|wrapped
operator|.
name|toArray
argument_list|(
operator|new
name|LeafReader
index|[
name|wrapped
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|atomicView
operator|=
operator|new
name|SlowCompositeReaderWrapper
argument_list|(
name|multiReader
argument_list|,
literal|true
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
name|SortingLeafReader
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
if|if
condition|(
name|docMap
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SMP"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"SMP"
argument_list|,
literal|"readers already sorted, omitting sort"
argument_list|)
expr_stmt|;
block|}
return|return
name|unsortedReaders
return|;
block|}
else|else
block|{
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SMP"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"SMP"
argument_list|,
literal|"sorting readers by "
operator|+
name|sort
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
name|sortedView
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setMergeInfo
specifier|public
name|void
name|setMergeInfo
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|info
operator|.
name|info
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|diagnostics
operator|.
name|put
argument_list|(
name|SORTER_ID_PROP
argument_list|,
name|sorter
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setMergeInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|getDeletes
specifier|private
name|PackedLongValues
name|getDeletes
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|)
block|{
name|PackedLongValues
operator|.
name|Builder
name|deletes
init|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|int
name|deleteCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReader
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
operator|.
name|build
argument_list|()
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
name|PackedLongValues
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
DECL|field|infoStream
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
DECL|method|SortingMergeSpecification
name|SortingMergeSpecification
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
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
argument_list|,
name|infoStream
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
comment|/** Returns {@code true} if the given {@code reader} is sorted by the    *  {@code sort} given. Typically the given {@code sort} would be the    *  {@link SortingMergePolicy#getSort()} order of a {@link SortingMergePolicy}. */
DECL|method|isSorted
specifier|public
specifier|static
name|boolean
name|isSorted
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|Sort
name|sort
parameter_list|)
block|{
name|String
name|description
init|=
name|getSortDescription
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|description
operator|!=
literal|null
operator|&&
name|description
operator|.
name|equals
argument_list|(
name|sort
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getSortDescription
specifier|private
specifier|static
name|String
name|getSortDescription
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|SegmentReader
condition|)
block|{
specifier|final
name|SegmentReader
name|segReader
init|=
operator|(
name|SegmentReader
operator|)
name|reader
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|segReader
operator|.
name|getSegmentInfo
argument_list|()
operator|.
name|info
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
if|if
condition|(
name|diagnostics
operator|!=
literal|null
condition|)
block|{
return|return
name|diagnostics
operator|.
name|get
argument_list|(
name|SORTER_ID_PROP
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|sortedMergeSpecification
specifier|private
name|MergeSpecification
name|sortedMergeSpecification
parameter_list|(
name|MergeSpecification
name|specification
parameter_list|,
name|InfoStream
name|infoStream
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
argument_list|(
name|infoStream
argument_list|)
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
DECL|field|sort
specifier|final
name|Sort
name|sort
decl_stmt|;
comment|/** Create a new {@code MergePolicy} that sorts documents with the given {@code sort}. */
DECL|method|SortingMergePolicy
specifier|public
name|SortingMergePolicy
parameter_list|(
name|MergePolicy
name|in
parameter_list|,
name|Sort
name|sort
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
operator|new
name|Sorter
argument_list|(
name|sort
argument_list|)
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
block|}
comment|/** Return the {@link Sort} order that is used to sort segments when merging. */
DECL|method|getSort
specifier|public
name|Sort
name|getSort
parameter_list|()
block|{
return|return
name|sort
return|;
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
parameter_list|,
name|IndexWriter
name|writer
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
argument_list|,
name|writer
argument_list|)
argument_list|,
name|writer
operator|.
name|infoStream
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
name|SegmentCommitInfo
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|,
name|IndexWriter
name|writer
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
argument_list|,
name|writer
argument_list|)
argument_list|,
name|writer
operator|.
name|infoStream
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
parameter_list|,
name|IndexWriter
name|writer
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
argument_list|,
name|writer
argument_list|)
argument_list|,
name|writer
operator|.
name|infoStream
argument_list|)
return|;
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
name|SegmentCommitInfo
name|newSegment
parameter_list|,
name|IndexWriter
name|writer
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
argument_list|,
name|writer
argument_list|)
return|;
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

