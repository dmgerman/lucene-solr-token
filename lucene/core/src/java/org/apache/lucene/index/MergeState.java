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
name|index
operator|.
name|PayloadProcessorProvider
operator|.
name|PayloadProcessor
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
name|PayloadProcessorProvider
operator|.
name|ReaderPayloadProcessor
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

begin_comment
comment|/** Holds common state used during segment merging  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|MergeState
specifier|public
class|class
name|MergeState
block|{
DECL|class|DocMap
specifier|public
specifier|static
specifier|abstract
class|class
name|DocMap
block|{
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|method|DocMap
specifier|protected
name|DocMap
parameter_list|(
name|Bits
name|liveDocs
parameter_list|)
block|{
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
block|}
DECL|method|build
specifier|public
specifier|static
name|DocMap
name|build
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
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
name|int
name|numDeletes
init|=
name|reader
operator|.
name|numDeletedDocs
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|maxDoc
operator|-
name|numDeletes
decl_stmt|;
assert|assert
name|reader
operator|.
name|getLiveDocs
argument_list|()
operator|!=
literal|null
operator|||
name|numDeletes
operator|==
literal|0
assert|;
if|if
condition|(
name|numDeletes
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|NoDelDocMap
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|numDeletes
operator|<
name|numDocs
condition|)
block|{
return|return
name|buildDelCountDocmap
argument_list|(
name|maxDoc
argument_list|,
name|numDeletes
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|buildDirectDocMap
argument_list|(
name|maxDoc
argument_list|,
name|numDocs
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
return|;
block|}
block|}
DECL|method|buildDelCountDocmap
specifier|static
name|DocMap
name|buildDelCountDocmap
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|int
name|numDeletes
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|PackedInts
operator|.
name|Mutable
name|numDeletesSoFar
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|maxDoc
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|numDeletes
argument_list|)
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
name|int
name|del
init|=
literal|0
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
name|del
expr_stmt|;
block|}
name|numDeletesSoFar
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|del
argument_list|)
expr_stmt|;
block|}
assert|assert
name|del
operator|==
name|numDeletes
operator|:
literal|"del="
operator|+
name|del
operator|+
literal|", numdeletes="
operator|+
name|numDeletes
assert|;
return|return
operator|new
name|DelCountDocMap
argument_list|(
name|liveDocs
argument_list|,
name|numDeletesSoFar
argument_list|)
return|;
block|}
DECL|method|buildDirectDocMap
specifier|static
name|DocMap
name|buildDirectDocMap
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|PackedInts
operator|.
name|Mutable
name|docIds
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|maxDoc
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
name|int
name|del
init|=
literal|0
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
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|docIds
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|i
operator|-
name|del
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|++
name|del
expr_stmt|;
block|}
block|}
assert|assert
name|numDocs
operator|+
name|del
operator|==
name|maxDoc
operator|:
literal|"maxDoc="
operator|+
name|maxDoc
operator|+
literal|", del="
operator|+
name|del
operator|+
literal|", numDocs="
operator|+
name|numDocs
assert|;
return|return
operator|new
name|DirectDocMap
argument_list|(
name|liveDocs
argument_list|,
name|docIds
argument_list|,
name|del
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
name|remap
argument_list|(
name|docId
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|remap
specifier|public
specifier|abstract
name|int
name|remap
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|maxDoc
specifier|public
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
DECL|method|numDocs
specifier|public
specifier|final
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|maxDoc
argument_list|()
operator|-
name|numDeletedDocs
argument_list|()
return|;
block|}
DECL|method|numDeletedDocs
specifier|public
specifier|abstract
name|int
name|numDeletedDocs
parameter_list|()
function_decl|;
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|numDeletedDocs
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
DECL|class|NoDelDocMap
specifier|private
specifier|static
class|class
name|NoDelDocMap
extends|extends
name|DocMap
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|NoDelDocMap
specifier|private
name|NoDelDocMap
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remap
specifier|public
name|int
name|remap
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|docId
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
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|numDeletedDocs
specifier|public
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|class|DirectDocMap
specifier|private
specifier|static
class|class
name|DirectDocMap
extends|extends
name|DocMap
block|{
DECL|field|docIds
specifier|private
specifier|final
name|PackedInts
operator|.
name|Mutable
name|docIds
decl_stmt|;
DECL|field|numDeletedDocs
specifier|private
specifier|final
name|int
name|numDeletedDocs
decl_stmt|;
DECL|method|DirectDocMap
specifier|private
name|DirectDocMap
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|PackedInts
operator|.
name|Mutable
name|docIds
parameter_list|,
name|int
name|numDeletedDocs
parameter_list|)
block|{
name|super
argument_list|(
name|liveDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|docIds
operator|=
name|docIds
expr_stmt|;
name|this
operator|.
name|numDeletedDocs
operator|=
name|numDeletedDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remap
specifier|public
name|int
name|remap
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|docIds
operator|.
name|get
argument_list|(
name|docId
argument_list|)
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
name|docIds
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numDeletedDocs
specifier|public
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
name|numDeletedDocs
return|;
block|}
block|}
DECL|class|DelCountDocMap
specifier|private
specifier|static
class|class
name|DelCountDocMap
extends|extends
name|DocMap
block|{
DECL|field|numDeletesSoFar
specifier|private
specifier|final
name|PackedInts
operator|.
name|Mutable
name|numDeletesSoFar
decl_stmt|;
DECL|method|DelCountDocMap
specifier|private
name|DelCountDocMap
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|PackedInts
operator|.
name|Mutable
name|numDeletesSoFar
parameter_list|)
block|{
name|super
argument_list|(
name|liveDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|numDeletesSoFar
operator|=
name|numDeletesSoFar
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remap
specifier|public
name|int
name|remap
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|docId
operator|-
operator|(
name|int
operator|)
name|numDeletesSoFar
operator|.
name|get
argument_list|(
name|docId
argument_list|)
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
name|numDeletesSoFar
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numDeletedDocs
specifier|public
name|int
name|numDeletedDocs
parameter_list|()
block|{
specifier|final
name|int
name|maxDoc
init|=
name|maxDoc
argument_list|()
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|numDeletesSoFar
operator|.
name|get
argument_list|(
name|maxDoc
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
DECL|field|segmentInfo
specifier|public
name|SegmentInfo
name|segmentInfo
decl_stmt|;
DECL|field|fieldInfos
specifier|public
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|readers
specifier|public
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|readers
decl_stmt|;
comment|// Readers being merged
DECL|field|docMaps
specifier|public
name|DocMap
index|[]
name|docMaps
decl_stmt|;
comment|// Maps docIDs around deletions
DECL|field|docBase
specifier|public
name|int
index|[]
name|docBase
decl_stmt|;
comment|// New docID base per reader
DECL|field|checkAbort
specifier|public
name|CheckAbort
name|checkAbort
decl_stmt|;
DECL|field|infoStream
specifier|public
name|InfoStream
name|infoStream
decl_stmt|;
comment|// Updated per field;
DECL|field|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
decl_stmt|;
comment|// Used to process payloads
comment|// TODO: this is a FactoryFactory here basically
comment|// and we could make a codec(wrapper) to do all of this privately so IW is uninvolved
DECL|field|payloadProcessorProvider
specifier|public
name|PayloadProcessorProvider
name|payloadProcessorProvider
decl_stmt|;
DECL|field|readerPayloadProcessor
specifier|public
name|ReaderPayloadProcessor
index|[]
name|readerPayloadProcessor
decl_stmt|;
DECL|field|currentReaderPayloadProcessor
specifier|public
name|ReaderPayloadProcessor
name|currentReaderPayloadProcessor
decl_stmt|;
DECL|field|currentPayloadProcessor
specifier|public
name|PayloadProcessor
index|[]
name|currentPayloadProcessor
decl_stmt|;
comment|// TODO: get rid of this? it tells you which segments are 'aligned' (e.g. for bulk merging)
comment|// but is this really so expensive to compute again in different components, versus once in SM?
DECL|field|matchingSegmentReaders
specifier|public
name|SegmentReader
index|[]
name|matchingSegmentReaders
decl_stmt|;
DECL|field|matchedCount
specifier|public
name|int
name|matchedCount
decl_stmt|;
DECL|class|CheckAbort
specifier|public
specifier|static
class|class
name|CheckAbort
block|{
DECL|field|workCount
specifier|private
name|double
name|workCount
decl_stmt|;
DECL|field|merge
specifier|private
specifier|final
name|MergePolicy
operator|.
name|OneMerge
name|merge
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|method|CheckAbort
specifier|public
name|CheckAbort
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|merge
operator|=
name|merge
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/**      * Records the fact that roughly units amount of work      * have been done since this method was last called.      * When adding time-consuming code into SegmentMerger,      * you should test different values for units to ensure      * that the time in between calls to merge.checkAborted      * is up to ~ 1 second.      */
DECL|method|work
specifier|public
name|void
name|work
parameter_list|(
name|double
name|units
parameter_list|)
throws|throws
name|MergePolicy
operator|.
name|MergeAbortedException
block|{
name|workCount
operator|+=
name|units
expr_stmt|;
if|if
condition|(
name|workCount
operator|>=
literal|10000.0
condition|)
block|{
name|merge
operator|.
name|checkAborted
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|workCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** If you use this: IW.close(false) cannot abort your merge!      * @lucene.internal */
DECL|field|NONE
specifier|static
specifier|final
name|MergeState
operator|.
name|CheckAbort
name|NONE
init|=
operator|new
name|MergeState
operator|.
name|CheckAbort
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|work
parameter_list|(
name|double
name|units
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
decl_stmt|;
block|}
block|}
end_class

end_unit

