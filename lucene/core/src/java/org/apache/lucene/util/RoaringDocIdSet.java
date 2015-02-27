begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|Arrays
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
name|DocIdSet
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
name|DocIdSetIterator
import|;
end_import

begin_comment
comment|/**  * {@link DocIdSet} implementation inspired from http://roaringbitmap.org/  *  * The space is divided into blocks of 2^16 bits and each block is encoded  * independently. In each block, if less than 2^12 bits are set, then  * documents are simply stored in a short[]. If more than 2^16-2^12 bits are  * set, then the inverse of the set is encoded in a simple short[]. Otherwise  * a {@link FixedBitSet} is used.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|RoaringDocIdSet
specifier|public
class|class
name|RoaringDocIdSet
extends|extends
name|DocIdSet
block|{
comment|// Number of documents in a block
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1
operator|<<
literal|16
decl_stmt|;
comment|// The maximum length for an array, beyond that point we switch to a bitset
DECL|field|MAX_ARRAY_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ARRAY_LENGTH
init|=
literal|1
operator|<<
literal|12
decl_stmt|;
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|RoaringDocIdSet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** A builder of {@link RoaringDocIdSet}s. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|sets
specifier|private
specifier|final
name|DocIdSet
index|[]
name|sets
decl_stmt|;
DECL|field|cardinality
specifier|private
name|int
name|cardinality
decl_stmt|;
DECL|field|lastDocId
specifier|private
name|int
name|lastDocId
decl_stmt|;
DECL|field|currentBlock
specifier|private
name|int
name|currentBlock
decl_stmt|;
DECL|field|currentBlockCardinality
specifier|private
name|int
name|currentBlockCardinality
decl_stmt|;
comment|// We start by filling the buffer and when it's full we copy the content of
comment|// the buffer to the FixedBitSet and put further documents in that bitset
DECL|field|buffer
specifier|private
specifier|final
name|short
index|[]
name|buffer
decl_stmt|;
DECL|field|denseBuffer
specifier|private
name|FixedBitSet
name|denseBuffer
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|sets
operator|=
operator|new
name|DocIdSet
index|[
operator|(
name|maxDoc
operator|+
operator|(
literal|1
operator|<<
literal|16
operator|)
operator|-
literal|1
operator|)
operator|>>>
literal|16
index|]
expr_stmt|;
name|lastDocId
operator|=
operator|-
literal|1
expr_stmt|;
name|currentBlock
operator|=
operator|-
literal|1
expr_stmt|;
name|buffer
operator|=
operator|new
name|short
index|[
name|MAX_ARRAY_LENGTH
index|]
expr_stmt|;
block|}
DECL|method|flush
specifier|private
name|void
name|flush
parameter_list|()
block|{
assert|assert
name|currentBlockCardinality
operator|<=
name|BLOCK_SIZE
assert|;
if|if
condition|(
name|currentBlockCardinality
operator|<=
name|MAX_ARRAY_LENGTH
condition|)
block|{
comment|// Use sparse encoding
assert|assert
name|denseBuffer
operator|==
literal|null
assert|;
if|if
condition|(
name|currentBlockCardinality
operator|>
literal|0
condition|)
block|{
name|sets
index|[
name|currentBlock
index|]
operator|=
operator|new
name|ShortArrayDocIdSet
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buffer
argument_list|,
name|currentBlockCardinality
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|denseBuffer
operator|!=
literal|null
assert|;
assert|assert
name|denseBuffer
operator|.
name|cardinality
argument_list|()
operator|==
name|currentBlockCardinality
assert|;
if|if
condition|(
name|denseBuffer
operator|.
name|length
argument_list|()
operator|==
name|BLOCK_SIZE
operator|&&
name|BLOCK_SIZE
operator|-
name|currentBlockCardinality
operator|<
name|MAX_ARRAY_LENGTH
condition|)
block|{
comment|// Doc ids are very dense, inverse the encoding
specifier|final
name|short
index|[]
name|excludedDocs
init|=
operator|new
name|short
index|[
name|BLOCK_SIZE
operator|-
name|currentBlockCardinality
index|]
decl_stmt|;
name|denseBuffer
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|denseBuffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|excludedDoc
init|=
operator|-
literal|1
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
name|excludedDocs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|excludedDoc
operator|=
name|denseBuffer
operator|.
name|nextSetBit
argument_list|(
name|excludedDoc
operator|+
literal|1
argument_list|)
expr_stmt|;
assert|assert
name|excludedDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
name|excludedDocs
index|[
name|i
index|]
operator|=
operator|(
name|short
operator|)
name|excludedDoc
expr_stmt|;
block|}
assert|assert
name|excludedDoc
operator|+
literal|1
operator|==
name|denseBuffer
operator|.
name|length
argument_list|()
operator|||
name|denseBuffer
operator|.
name|nextSetBit
argument_list|(
name|excludedDoc
operator|+
literal|1
argument_list|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
name|sets
index|[
name|currentBlock
index|]
operator|=
operator|new
name|NotDocIdSet
argument_list|(
name|BLOCK_SIZE
argument_list|,
operator|new
name|ShortArrayDocIdSet
argument_list|(
name|excludedDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Neither sparse nor super dense, use a fixed bit set
name|sets
index|[
name|currentBlock
index|]
operator|=
operator|new
name|BitDocIdSet
argument_list|(
name|denseBuffer
argument_list|,
name|currentBlockCardinality
argument_list|)
expr_stmt|;
block|}
name|denseBuffer
operator|=
literal|null
expr_stmt|;
block|}
name|cardinality
operator|+=
name|currentBlockCardinality
expr_stmt|;
name|denseBuffer
operator|=
literal|null
expr_stmt|;
name|currentBlockCardinality
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Add a new doc-id to this builder.      * NOTE: doc ids must be added in order.      */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
if|if
condition|(
name|docId
operator|<=
name|lastDocId
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Doc ids must be added in-order, got "
operator|+
name|docId
operator|+
literal|" which is<= lastDocID="
operator|+
name|lastDocId
argument_list|)
throw|;
block|}
specifier|final
name|int
name|block
init|=
name|docId
operator|>>>
literal|16
decl_stmt|;
if|if
condition|(
name|block
operator|!=
name|currentBlock
condition|)
block|{
comment|// we went to a different block, let's flush what we buffered and start from fresh
name|flush
argument_list|()
expr_stmt|;
name|currentBlock
operator|=
name|block
expr_stmt|;
block|}
if|if
condition|(
name|currentBlockCardinality
operator|<
name|MAX_ARRAY_LENGTH
condition|)
block|{
name|buffer
index|[
name|currentBlockCardinality
index|]
operator|=
operator|(
name|short
operator|)
name|docId
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|denseBuffer
operator|==
literal|null
condition|)
block|{
comment|// the buffer is full, let's move to a fixed bit set
specifier|final
name|int
name|numBits
init|=
name|Math
operator|.
name|min
argument_list|(
literal|1
operator|<<
literal|16
argument_list|,
name|maxDoc
operator|-
operator|(
name|block
operator|<<
literal|16
operator|)
argument_list|)
decl_stmt|;
name|denseBuffer
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
for|for
control|(
name|short
name|doc
range|:
name|buffer
control|)
block|{
name|denseBuffer
operator|.
name|set
argument_list|(
name|doc
operator|&
literal|0xFFFF
argument_list|)
expr_stmt|;
block|}
block|}
name|denseBuffer
operator|.
name|set
argument_list|(
name|docId
operator|&
literal|0xFFFF
argument_list|)
expr_stmt|;
block|}
name|lastDocId
operator|=
name|docId
expr_stmt|;
name|currentBlockCardinality
operator|+=
literal|1
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add the content of the provided {@link DocIdSetIterator}. */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|DocIdSetIterator
name|disi
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|doc
init|=
name|disi
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Build an instance. */
DECL|method|build
specifier|public
name|RoaringDocIdSet
name|build
parameter_list|()
block|{
name|flush
argument_list|()
expr_stmt|;
return|return
operator|new
name|RoaringDocIdSet
argument_list|(
name|sets
argument_list|,
name|cardinality
argument_list|)
return|;
block|}
block|}
comment|/**    * {@link DocIdSet} implementation that can store documents up to 2^16-1 in a short[].    */
DECL|class|ShortArrayDocIdSet
specifier|private
specifier|static
class|class
name|ShortArrayDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|ShortArrayDocIdSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|docIDs
specifier|private
specifier|final
name|short
index|[]
name|docIDs
decl_stmt|;
DECL|method|ShortArrayDocIdSet
specifier|private
name|ShortArrayDocIdSet
parameter_list|(
name|short
index|[]
name|docIDs
parameter_list|)
block|{
name|this
operator|.
name|docIDs
operator|=
name|docIDs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|docIDs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
comment|// this is the index of the current document in the array
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|docId
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|docIDs
index|[
name|i
index|]
operator|&
literal|0xFFFF
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|docIDs
operator|.
name|length
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|doc
operator|=
name|docId
argument_list|(
name|i
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|docIDs
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// binary search
name|int
name|lo
init|=
name|i
operator|+
literal|1
decl_stmt|;
name|int
name|hi
init|=
name|docIDs
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|lo
operator|<=
name|hi
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|midDoc
init|=
name|docId
argument_list|(
name|mid
argument_list|)
decl_stmt|;
if|if
condition|(
name|midDoc
operator|<
name|target
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lo
operator|==
name|docIDs
operator|.
name|length
condition|)
block|{
name|i
operator|=
name|docIDs
operator|.
name|length
expr_stmt|;
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|i
operator|=
name|lo
expr_stmt|;
return|return
name|doc
operator|=
name|docId
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
block|}
DECL|field|docIdSets
specifier|private
specifier|final
name|DocIdSet
index|[]
name|docIdSets
decl_stmt|;
DECL|field|cardinality
specifier|private
specifier|final
name|int
name|cardinality
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|final
name|long
name|ramBytesUsed
decl_stmt|;
DECL|method|RoaringDocIdSet
specifier|private
name|RoaringDocIdSet
parameter_list|(
name|DocIdSet
index|[]
name|docIdSets
parameter_list|,
name|int
name|cardinality
parameter_list|)
block|{
name|this
operator|.
name|docIdSets
operator|=
name|docIdSets
expr_stmt|;
name|long
name|ramBytesUsed
init|=
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|docIdSets
argument_list|)
decl_stmt|;
for|for
control|(
name|DocIdSet
name|set
range|:
name|this
operator|.
name|docIdSets
control|)
block|{
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|set
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|ramBytesUsed
operator|=
name|ramBytesUsed
expr_stmt|;
name|this
operator|.
name|cardinality
operator|=
name|cardinality
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cardinality
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
name|Iterator
argument_list|()
return|;
block|}
DECL|class|Iterator
specifier|private
class|class
name|Iterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|block
name|int
name|block
decl_stmt|;
DECL|field|sub
name|DocIdSetIterator
name|sub
init|=
literal|null
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|method|Iterator
name|Iterator
parameter_list|()
throws|throws
name|IOException
block|{
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|block
operator|=
operator|-
literal|1
expr_stmt|;
name|sub
operator|=
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
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
specifier|final
name|int
name|subNext
init|=
name|sub
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|subNext
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|firstDocFromNextBlock
argument_list|()
return|;
block|}
return|return
name|doc
operator|=
operator|(
name|block
operator|<<
literal|16
operator|)
operator||
name|subNext
return|;
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
throws|throws
name|IOException
block|{
specifier|final
name|int
name|targetBlock
init|=
name|target
operator|>>>
literal|16
decl_stmt|;
if|if
condition|(
name|targetBlock
operator|!=
name|block
condition|)
block|{
name|block
operator|=
name|targetBlock
expr_stmt|;
if|if
condition|(
name|block
operator|>=
name|docIdSets
operator|.
name|length
condition|)
block|{
name|sub
operator|=
literal|null
expr_stmt|;
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|docIdSets
index|[
name|block
index|]
operator|==
literal|null
condition|)
block|{
return|return
name|firstDocFromNextBlock
argument_list|()
return|;
block|}
name|sub
operator|=
name|docIdSets
index|[
name|block
index|]
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|subNext
init|=
name|sub
operator|.
name|advance
argument_list|(
name|target
operator|&
literal|0xFFFF
argument_list|)
decl_stmt|;
if|if
condition|(
name|subNext
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|firstDocFromNextBlock
argument_list|()
return|;
block|}
return|return
name|doc
operator|=
operator|(
name|block
operator|<<
literal|16
operator|)
operator||
name|subNext
return|;
block|}
DECL|method|firstDocFromNextBlock
specifier|private
name|int
name|firstDocFromNextBlock
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|block
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|block
operator|>=
name|docIdSets
operator|.
name|length
condition|)
block|{
name|sub
operator|=
literal|null
expr_stmt|;
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
elseif|else
if|if
condition|(
name|docIdSets
index|[
name|block
index|]
operator|!=
literal|null
condition|)
block|{
name|sub
operator|=
name|docIdSets
index|[
name|block
index|]
operator|.
name|iterator
argument_list|()
expr_stmt|;
specifier|final
name|int
name|subNext
init|=
name|sub
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
assert|assert
name|subNext
operator|!=
name|NO_MORE_DOCS
assert|;
return|return
name|doc
operator|=
operator|(
name|block
operator|<<
literal|16
operator|)
operator||
name|subNext
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cardinality
return|;
block|}
block|}
comment|/** Return the exact number of documents that are contained in this set. */
DECL|method|cardinality
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
return|return
name|cardinality
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
literal|"RoaringDocIdSet(cardinality="
operator|+
name|cardinality
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

