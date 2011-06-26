begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|cache
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
name|index
operator|.
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|MultiFields
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
name|OrdTermState
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
name|TermState
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
name|Terms
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
name|TermsEnum
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
name|FieldCache
operator|.
name|DocTermsIndex
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
name|ArrayUtil
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
name|PagedBytes
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
name|GrowableWriter
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

begin_class
DECL|class|DocTermsIndexCreator
specifier|public
class|class
name|DocTermsIndexCreator
extends|extends
name|EntryCreatorWithOptions
argument_list|<
name|DocTermsIndex
argument_list|>
block|{
DECL|field|FASTER_BUT_MORE_RAM
specifier|public
specifier|static
specifier|final
name|int
name|FASTER_BUT_MORE_RAM
init|=
literal|2
decl_stmt|;
DECL|field|field
specifier|public
name|String
name|field
decl_stmt|;
DECL|method|DocTermsIndexCreator
specifier|public
name|DocTermsIndexCreator
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|FASTER_BUT_MORE_RAM
argument_list|)
expr_stmt|;
comment|// By default turn on FASTER_BUT_MORE_RAM
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field can not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|DocTermsIndexCreator
specifier|public
name|DocTermsIndexCreator
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|(
name|flags
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field can not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCacheKey
specifier|public
name|EntryKey
name|getCacheKey
parameter_list|()
block|{
return|return
operator|new
name|SimpleEntryKey
argument_list|(
name|DocTermsIndexCreator
operator|.
name|class
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTermsIndex
name|create
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|fasterButMoreRAM
init|=
name|hasOption
argument_list|(
name|FASTER_BUT_MORE_RAM
argument_list|)
decl_stmt|;
specifier|final
name|PagedBytes
name|bytes
init|=
operator|new
name|PagedBytes
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|int
name|startBytesBPV
decl_stmt|;
name|int
name|startTermsBPV
decl_stmt|;
name|int
name|startNumUniqueTerms
decl_stmt|;
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
name|termCountHardLimit
decl_stmt|;
if|if
condition|(
name|maxDoc
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|termCountHardLimit
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|termCountHardLimit
operator|=
name|maxDoc
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
comment|// Try for coarse estimate for number of bits; this
comment|// should be an underestimate most of the time, which
comment|// is fine -- GrowableWriter will reallocate as needed
name|long
name|numUniqueTerms
init|=
literal|0
decl_stmt|;
try|try
block|{
name|numUniqueTerms
operator|=
name|terms
operator|.
name|getUniqueTermCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
name|numUniqueTerms
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|numUniqueTerms
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|numUniqueTerms
operator|>
name|termCountHardLimit
condition|)
block|{
comment|// app is misusing the API (there is more than
comment|// one term per doc); in this case we make best
comment|// effort to load what we can (see LUCENE-2142)
name|numUniqueTerms
operator|=
name|termCountHardLimit
expr_stmt|;
block|}
name|startBytesBPV
operator|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|numUniqueTerms
operator|*
literal|4
argument_list|)
expr_stmt|;
name|startTermsBPV
operator|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|numUniqueTerms
argument_list|)
expr_stmt|;
name|startNumUniqueTerms
operator|=
operator|(
name|int
operator|)
name|numUniqueTerms
expr_stmt|;
block|}
else|else
block|{
name|startBytesBPV
operator|=
literal|1
expr_stmt|;
name|startTermsBPV
operator|=
literal|1
expr_stmt|;
name|startNumUniqueTerms
operator|=
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|startBytesBPV
operator|=
literal|1
expr_stmt|;
name|startTermsBPV
operator|=
literal|1
expr_stmt|;
name|startNumUniqueTerms
operator|=
literal|1
expr_stmt|;
block|}
name|GrowableWriter
name|termOrdToBytesOffset
init|=
operator|new
name|GrowableWriter
argument_list|(
name|startBytesBPV
argument_list|,
literal|1
operator|+
name|startNumUniqueTerms
argument_list|,
name|fasterButMoreRAM
argument_list|)
decl_stmt|;
specifier|final
name|GrowableWriter
name|docToTermOrd
init|=
operator|new
name|GrowableWriter
argument_list|(
name|startTermsBPV
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|fasterButMoreRAM
argument_list|)
decl_stmt|;
comment|// 0 is reserved for "unset"
name|bytes
operator|.
name|copyUsingLengthPrefix
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|termOrd
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|termOrd
operator|>=
name|termCountHardLimit
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|termOrd
operator|==
name|termOrdToBytesOffset
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// NOTE: this code only runs if the incoming
comment|// reader impl doesn't implement
comment|// getUniqueTermCount (which should be uncommon)
name|termOrdToBytesOffset
operator|=
name|termOrdToBytesOffset
operator|.
name|resize
argument_list|(
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|termOrd
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|termOrdToBytesOffset
operator|.
name|set
argument_list|(
name|termOrd
argument_list|,
name|bytes
operator|.
name|copyUsingLengthPrefix
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docs
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|docToTermOrd
operator|.
name|set
argument_list|(
name|docID
argument_list|,
name|termOrd
argument_list|)
expr_stmt|;
block|}
name|termOrd
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|termOrdToBytesOffset
operator|.
name|size
argument_list|()
operator|>
name|termOrd
condition|)
block|{
name|termOrdToBytesOffset
operator|=
name|termOrdToBytesOffset
operator|.
name|resize
argument_list|(
name|termOrd
argument_list|)
expr_stmt|;
block|}
block|}
comment|// maybe an int-only impl?
return|return
operator|new
name|DocTermsIndexImpl
argument_list|(
name|bytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
argument_list|,
name|termOrdToBytesOffset
operator|.
name|getMutable
argument_list|()
argument_list|,
name|docToTermOrd
operator|.
name|getMutable
argument_list|()
argument_list|,
name|termOrd
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|DocTermsIndex
name|validate
parameter_list|(
name|DocTermsIndex
name|entry
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO? nothing? perhaps subsequent call with FASTER_BUT_MORE_RAM?
return|return
name|entry
return|;
block|}
comment|//-----------------------------------------------------------------------------
comment|//-----------------------------------------------------------------------------
DECL|class|DocTermsIndexImpl
specifier|public
specifier|static
class|class
name|DocTermsIndexImpl
extends|extends
name|DocTermsIndex
block|{
DECL|field|bytes
specifier|private
specifier|final
name|PagedBytes
operator|.
name|Reader
name|bytes
decl_stmt|;
DECL|field|termOrdToBytesOffset
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|termOrdToBytesOffset
decl_stmt|;
DECL|field|docToTermOrd
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|docToTermOrd
decl_stmt|;
DECL|field|numOrd
specifier|private
specifier|final
name|int
name|numOrd
decl_stmt|;
DECL|method|DocTermsIndexImpl
specifier|public
name|DocTermsIndexImpl
parameter_list|(
name|PagedBytes
operator|.
name|Reader
name|bytes
parameter_list|,
name|PackedInts
operator|.
name|Reader
name|termOrdToBytesOffset
parameter_list|,
name|PackedInts
operator|.
name|Reader
name|docToTermOrd
parameter_list|,
name|int
name|numOrd
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|docToTermOrd
operator|=
name|docToTermOrd
expr_stmt|;
name|this
operator|.
name|termOrdToBytesOffset
operator|=
name|termOrdToBytesOffset
expr_stmt|;
name|this
operator|.
name|numOrd
operator|=
name|numOrd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocToOrd
specifier|public
name|PackedInts
operator|.
name|Reader
name|getDocToOrd
parameter_list|()
block|{
return|return
name|docToTermOrd
return|;
block|}
annotation|@
name|Override
DECL|method|numOrd
specifier|public
name|int
name|numOrd
parameter_list|()
block|{
return|return
name|numOrd
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|docToTermOrd
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|docToTermOrd
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|BytesRef
name|lookup
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
block|{
return|return
name|bytes
operator|.
name|fill
argument_list|(
name|ret
argument_list|,
name|termOrdToBytesOffset
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|()
block|{
return|return
name|this
operator|.
expr|new
name|DocTermsIndexEnum
argument_list|()
return|;
block|}
DECL|class|DocTermsIndexEnum
class|class
name|DocTermsIndexEnum
extends|extends
name|TermsEnum
block|{
DECL|field|currentOrd
name|int
name|currentOrd
decl_stmt|;
DECL|field|currentBlockNumber
name|int
name|currentBlockNumber
decl_stmt|;
DECL|field|end
name|int
name|end
decl_stmt|;
comment|// end position in the current block
DECL|field|blocks
specifier|final
name|byte
index|[]
index|[]
name|blocks
decl_stmt|;
DECL|field|blockEnds
specifier|final
name|int
index|[]
name|blockEnds
decl_stmt|;
DECL|field|term
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|DocTermsIndexEnum
specifier|public
name|DocTermsIndexEnum
parameter_list|()
block|{
name|currentOrd
operator|=
literal|0
expr_stmt|;
name|currentBlockNumber
operator|=
literal|0
expr_stmt|;
name|blocks
operator|=
name|bytes
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|blockEnds
operator|=
name|bytes
operator|.
name|getBlockEnds
argument_list|()
expr_stmt|;
name|currentBlockNumber
operator|=
name|bytes
operator|.
name|fillAndGetIndex
argument_list|(
name|term
argument_list|,
name|termOrdToBytesOffset
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|end
operator|=
name|blockEnds
index|[
name|currentBlockNumber
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
comment|/* ignored */
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|low
init|=
literal|1
decl_stmt|;
name|int
name|high
init|=
name|numOrd
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|seekExact
argument_list|(
name|mid
argument_list|)
expr_stmt|;
name|int
name|cmp
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
comment|// key found
block|}
if|if
condition|(
name|low
operator|==
name|numOrd
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
else|else
block|{
name|seekExact
argument_list|(
name|low
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|(
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<=
name|numOrd
operator|)
assert|;
comment|// TODO: if gap is small, could iterate from current position?  Or let user decide that?
name|currentBlockNumber
operator|=
name|bytes
operator|.
name|fillAndGetIndex
argument_list|(
name|term
argument_list|,
name|termOrdToBytesOffset
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|ord
argument_list|)
argument_list|)
expr_stmt|;
name|end
operator|=
name|blockEnds
index|[
name|currentBlockNumber
index|]
expr_stmt|;
name|currentOrd
operator|=
operator|(
name|int
operator|)
name|ord
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|start
init|=
name|term
operator|.
name|offset
operator|+
name|term
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|start
operator|>=
name|end
condition|)
block|{
comment|// switch byte blocks
if|if
condition|(
name|currentBlockNumber
operator|+
literal|1
operator|>=
name|blocks
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
name|currentBlockNumber
operator|++
expr_stmt|;
name|term
operator|.
name|bytes
operator|=
name|blocks
index|[
name|currentBlockNumber
index|]
expr_stmt|;
name|end
operator|=
name|blockEnds
index|[
name|currentBlockNumber
index|]
expr_stmt|;
name|start
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|end
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
comment|// special case of empty last array
block|}
name|currentOrd
operator|++
expr_stmt|;
name|byte
index|[]
name|block
init|=
name|term
operator|.
name|bytes
decl_stmt|;
if|if
condition|(
operator|(
name|block
index|[
name|start
index|]
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
name|term
operator|.
name|length
operator|=
name|block
index|[
name|start
index|]
expr_stmt|;
name|term
operator|.
name|offset
operator|=
name|start
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|term
operator|.
name|length
operator|=
operator|(
operator|(
operator|(
name|block
index|[
name|start
index|]
operator|&
literal|0x7f
operator|)
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block
index|[
literal|1
operator|+
name|start
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|term
operator|.
name|offset
operator|=
name|start
operator|+
literal|2
expr_stmt|;
block|}
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|currentOrd
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
literal|null
operator|&&
name|state
operator|instanceof
name|OrdTermState
assert|;
name|this
operator|.
name|seekExact
argument_list|(
operator|(
operator|(
name|OrdTermState
operator|)
name|state
operator|)
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
name|OrdTermState
name|state
init|=
operator|new
name|OrdTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|ord
operator|=
name|currentOrd
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

