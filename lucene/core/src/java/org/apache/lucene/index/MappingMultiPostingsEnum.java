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
name|ArrayList
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
comment|/**  * Exposes flex API, merged from flex API of sub-segments,  * remapping docIDs (this is used for segment merging).  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MappingMultiPostingsEnum
specifier|final
class|class
name|MappingMultiPostingsEnum
extends|extends
name|PostingsEnum
block|{
DECL|field|multiDocsAndPositionsEnum
name|MultiPostingsEnum
name|multiDocsAndPositionsEnum
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|docIDMerger
specifier|final
name|DocIDMerger
argument_list|<
name|MappingPostingsSub
argument_list|>
name|docIDMerger
decl_stmt|;
DECL|field|current
specifier|private
name|MappingPostingsSub
name|current
decl_stmt|;
DECL|field|allSubs
specifier|private
specifier|final
name|MappingPostingsSub
index|[]
name|allSubs
decl_stmt|;
DECL|field|subs
specifier|private
specifier|final
name|List
argument_list|<
name|MappingPostingsSub
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|MappingPostingsSub
specifier|private
specifier|static
class|class
name|MappingPostingsSub
extends|extends
name|DocIDMerger
operator|.
name|Sub
block|{
DECL|field|postings
specifier|public
name|PostingsEnum
name|postings
decl_stmt|;
DECL|method|MappingPostingsSub
specifier|public
name|MappingPostingsSub
parameter_list|(
name|MergeState
operator|.
name|DocMap
name|docMap
parameter_list|)
block|{
name|super
argument_list|(
name|docMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
try|try
block|{
return|return
name|postings
operator|.
name|nextDoc
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Sole constructor. */
DECL|method|MappingMultiPostingsEnum
specifier|public
name|MappingMultiPostingsEnum
parameter_list|(
name|String
name|field
parameter_list|,
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|allSubs
operator|=
operator|new
name|MappingPostingsSub
index|[
name|mergeState
operator|.
name|fieldsProducers
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
name|allSubs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|allSubs
index|[
name|i
index|]
operator|=
operator|new
name|MappingPostingsSub
argument_list|(
name|mergeState
operator|.
name|docMaps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|docIDMerger
operator|=
operator|new
name|DocIDMerger
argument_list|<
name|MappingPostingsSub
argument_list|>
argument_list|(
name|subs
argument_list|,
name|allSubs
operator|.
name|length
argument_list|,
name|mergeState
operator|.
name|needsIndexSort
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
name|MappingMultiPostingsEnum
name|reset
parameter_list|(
name|MultiPostingsEnum
name|postingsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|multiDocsAndPositionsEnum
operator|=
name|postingsEnum
expr_stmt|;
name|MultiPostingsEnum
operator|.
name|EnumWithSlice
index|[]
name|subsArray
init|=
name|postingsEnum
operator|.
name|getSubs
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|postingsEnum
operator|.
name|getNumSubs
argument_list|()
decl_stmt|;
name|subs
operator|.
name|clear
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|MappingPostingsSub
name|sub
init|=
name|allSubs
index|[
name|subsArray
index|[
name|i
index|]
operator|.
name|slice
operator|.
name|readerIndex
index|]
decl_stmt|;
name|sub
operator|.
name|postings
operator|=
name|subsArray
index|[
name|i
index|]
operator|.
name|postingsEnum
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|docIDMerger
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|postings
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|current
operator|.
name|mappedDocID
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|current
operator|=
name|docIDMerger
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
return|return
name|current
operator|.
name|mappedDocID
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|pos
init|=
name|current
operator|.
name|postings
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"position="
operator|+
name|pos
operator|+
literal|" is negative, field=\""
operator|+
name|field
operator|+
literal|" doc="
operator|+
name|current
operator|.
name|mappedDocID
argument_list|,
name|current
operator|.
name|postings
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|>
name|IndexWriter
operator|.
name|MAX_POSITION
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"position="
operator|+
name|pos
operator|+
literal|" is too large (> IndexWriter.MAX_POSITION="
operator|+
name|IndexWriter
operator|.
name|MAX_POSITION
operator|+
literal|"), field=\""
operator|+
name|field
operator|+
literal|"\" doc="
operator|+
name|current
operator|.
name|mappedDocID
argument_list|,
name|current
operator|.
name|postings
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|postings
operator|.
name|startOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|postings
operator|.
name|endOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|postings
operator|.
name|getPayload
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MappingPostingsSub
name|sub
range|:
name|subs
control|)
block|{
name|cost
operator|+=
name|sub
operator|.
name|postings
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
block|}
end_class

end_unit

