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
name|MergeState
operator|.
name|DocMap
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
name|search
operator|.
name|SortField
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
name|LongValues
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
name|PriorityQueue
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|MultiSorter
specifier|final
class|class
name|MultiSorter
block|{
comment|/** Does a merge sort of the leaves of the incoming reader, returning {@link DocMap} to map each leaf's    *  documents into the merged segment.  The documents for each incoming leaf reader must already be sorted by the same sort!    *  Returns null if the merge sort is not needed (segments are already in index sort order).    **/
DECL|method|sort
specifier|static
name|MergeState
operator|.
name|DocMap
index|[]
name|sort
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: optimize if only 1 reader is incoming, though that's a rare case
name|SortField
name|fields
index|[]
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|ComparableProvider
index|[]
index|[]
name|comparables
init|=
operator|new
name|ComparableProvider
index|[
name|fields
operator|.
name|length
index|]
index|[]
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparables
index|[
name|i
index|]
operator|=
name|getComparableProviders
argument_list|(
name|readers
argument_list|,
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|leafCount
init|=
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
name|PriorityQueue
argument_list|<
name|LeafAndDocID
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|LeafAndDocID
argument_list|>
argument_list|(
name|leafCount
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|LeafAndDocID
name|a
parameter_list|,
name|LeafAndDocID
name|b
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
name|comparables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|cmp
init|=
name|a
operator|.
name|values
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|values
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
operator|<
literal|0
return|;
block|}
block|}
comment|// tie-break by docID natural order:
if|if
condition|(
name|a
operator|.
name|readerIndex
operator|!=
name|b
operator|.
name|readerIndex
condition|)
block|{
return|return
name|a
operator|.
name|readerIndex
operator|<
name|b
operator|.
name|readerIndex
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|docID
operator|<
name|b
operator|.
name|docID
return|;
block|}
block|}
block|}
decl_stmt|;
name|PackedLongValues
operator|.
name|Builder
index|[]
name|builders
init|=
operator|new
name|PackedLongValues
operator|.
name|Builder
index|[
name|leafCount
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
name|leafCount
condition|;
name|i
operator|++
control|)
block|{
name|CodecReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LeafAndDocID
name|leaf
init|=
operator|new
name|LeafAndDocID
argument_list|(
name|i
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|comparables
operator|.
name|length
argument_list|)
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
name|comparables
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|leaf
operator|.
name|values
index|[
name|j
index|]
operator|=
name|comparables
index|[
name|j
index|]
index|[
name|i
index|]
operator|.
name|getComparable
argument_list|(
name|leaf
operator|.
name|docID
argument_list|)
expr_stmt|;
assert|assert
name|leaf
operator|.
name|values
index|[
name|j
index|]
operator|!=
literal|null
assert|;
block|}
name|queue
operator|.
name|add
argument_list|(
name|leaf
argument_list|)
expr_stmt|;
name|builders
index|[
name|i
index|]
operator|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
block|}
comment|// merge sort:
name|int
name|mappedDocID
init|=
literal|0
decl_stmt|;
name|int
name|lastReaderIndex
init|=
literal|0
decl_stmt|;
name|boolean
name|isSorted
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|LeafAndDocID
name|top
init|=
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastReaderIndex
operator|>
name|top
operator|.
name|readerIndex
condition|)
block|{
comment|// merge sort is needed
name|isSorted
operator|=
literal|false
expr_stmt|;
block|}
name|lastReaderIndex
operator|=
name|top
operator|.
name|readerIndex
expr_stmt|;
name|builders
index|[
name|top
operator|.
name|readerIndex
index|]
operator|.
name|add
argument_list|(
name|mappedDocID
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|liveDocs
operator|==
literal|null
operator|||
name|top
operator|.
name|liveDocs
operator|.
name|get
argument_list|(
name|top
operator|.
name|docID
argument_list|)
condition|)
block|{
name|mappedDocID
operator|++
expr_stmt|;
block|}
name|top
operator|.
name|docID
operator|++
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|docID
operator|<
name|top
operator|.
name|maxDoc
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|comparables
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|top
operator|.
name|values
index|[
name|j
index|]
operator|=
name|comparables
index|[
name|j
index|]
index|[
name|top
operator|.
name|readerIndex
index|]
operator|.
name|getComparable
argument_list|(
name|top
operator|.
name|docID
argument_list|)
expr_stmt|;
assert|assert
name|top
operator|.
name|values
index|[
name|j
index|]
operator|!=
literal|null
assert|;
block|}
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isSorted
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MergeState
operator|.
name|DocMap
index|[]
name|docMaps
init|=
operator|new
name|MergeState
operator|.
name|DocMap
index|[
name|leafCount
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
name|leafCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|PackedLongValues
name|remapped
init|=
name|builders
index|[
name|i
index|]
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|docMaps
index|[
name|i
index|]
operator|=
operator|new
name|MergeState
operator|.
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
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
name|docID
argument_list|)
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|remapped
operator|.
name|get
argument_list|(
name|docID
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
block|}
expr_stmt|;
block|}
return|return
name|docMaps
return|;
block|}
DECL|class|LeafAndDocID
specifier|private
specifier|static
class|class
name|LeafAndDocID
block|{
DECL|field|readerIndex
specifier|final
name|int
name|readerIndex
decl_stmt|;
DECL|field|liveDocs
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|values
specifier|final
name|Comparable
index|[]
name|values
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|method|LeafAndDocID
specifier|public
name|LeafAndDocID
parameter_list|(
name|int
name|readerIndex
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|int
name|numComparables
parameter_list|)
block|{
name|this
operator|.
name|readerIndex
operator|=
name|readerIndex
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|Comparable
index|[
name|numComparables
index|]
expr_stmt|;
block|}
block|}
comment|/** Returns an object for this docID whose .compareTo represents the requested {@link SortField} sort order. */
DECL|interface|ComparableProvider
specifier|private
interface|interface
name|ComparableProvider
block|{
DECL|method|getComparable
specifier|public
name|Comparable
name|getComparable
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/** Returns {@code ComparableProvider}s for the provided readers to represent the requested {@link SortField} sort order. */
DECL|method|getComparableProviders
specifier|private
specifier|static
name|ComparableProvider
index|[]
name|getComparableProviders
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|,
name|SortField
name|sortField
parameter_list|)
throws|throws
name|IOException
block|{
name|ComparableProvider
index|[]
name|providers
init|=
operator|new
name|ComparableProvider
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|int
name|reverseMul
init|=
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
decl_stmt|;
specifier|final
name|SortField
operator|.
name|Type
name|sortType
init|=
name|Sorter
operator|.
name|getSortFieldType
argument_list|(
name|sortField
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|sortType
condition|)
block|{
case|case
name|STRING
case|:
block|{
comment|// this uses the efficient segment-local ordinal map:
specifier|final
name|SortedDocValues
index|[]
name|values
init|=
operator|new
name|SortedDocValues
index|[
name|readers
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SortedDocValues
name|sorted
init|=
name|Sorter
operator|.
name|getOrWrapSorted
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|sortField
argument_list|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|sorted
expr_stmt|;
block|}
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordinalMap
init|=
name|MultiDocValues
operator|.
name|OrdinalMap
operator|.
name|build
argument_list|(
literal|null
argument_list|,
name|values
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|int
name|missingOrd
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|==
name|SortField
operator|.
name|STRING_LAST
condition|)
block|{
name|missingOrd
operator|=
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
name|Integer
operator|.
name|MIN_VALUE
else|:
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|missingOrd
operator|=
name|sortField
operator|.
name|getReverse
argument_list|()
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|SortedDocValues
name|readerValues
init|=
name|values
index|[
name|readerIndex
index|]
decl_stmt|;
specifier|final
name|LongValues
name|globalOrds
init|=
name|ordinalMap
operator|.
name|getGlobalOrds
argument_list|(
name|readerIndex
argument_list|)
decl_stmt|;
name|providers
index|[
name|readerIndex
index|]
operator|=
operator|new
name|ComparableProvider
argument_list|()
block|{
comment|// used only by assert:
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|docsInOrder
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"docs must be sent in order, but lastDocID="
operator|+
name|lastDocID
operator|+
literal|" vs docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparable
name|getComparable
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docsInOrder
argument_list|(
name|docID
argument_list|)
assert|;
name|int
name|readerDocID
init|=
name|readerValues
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerDocID
operator|<
name|docID
condition|)
block|{
name|readerDocID
operator|=
name|readerValues
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readerDocID
operator|==
name|docID
condition|)
block|{
comment|// translate segment's ord to global ord space:
return|return
name|reverseMul
operator|*
operator|(
name|int
operator|)
name|globalOrds
operator|.
name|get
argument_list|(
name|readerValues
operator|.
name|ordValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|missingOrd
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
break|break;
case|case
name|LONG
case|:
block|{
specifier|final
name|Long
name|missingValue
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|=
operator|(
name|Long
operator|)
name|sortField
operator|.
name|getMissingValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingValue
operator|=
literal|0L
expr_stmt|;
block|}
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|NumericDocValues
name|values
init|=
name|Sorter
operator|.
name|getOrWrapNumeric
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
argument_list|,
name|sortField
argument_list|)
decl_stmt|;
name|providers
index|[
name|readerIndex
index|]
operator|=
operator|new
name|ComparableProvider
argument_list|()
block|{
comment|// used only by assert:
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|docsInOrder
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"docs must be sent in order, but lastDocID="
operator|+
name|lastDocID
operator|+
literal|" vs docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparable
name|getComparable
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docsInOrder
argument_list|(
name|docID
argument_list|)
assert|;
name|int
name|readerDocID
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerDocID
operator|<
name|docID
condition|)
block|{
name|readerDocID
operator|=
name|values
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readerDocID
operator|==
name|docID
condition|)
block|{
return|return
name|reverseMul
operator|*
name|values
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|reverseMul
operator|*
name|missingValue
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
break|break;
case|case
name|INT
case|:
block|{
specifier|final
name|Integer
name|missingValue
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|=
operator|(
name|Integer
operator|)
name|sortField
operator|.
name|getMissingValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingValue
operator|=
literal|0
expr_stmt|;
block|}
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|NumericDocValues
name|values
init|=
name|Sorter
operator|.
name|getOrWrapNumeric
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
argument_list|,
name|sortField
argument_list|)
decl_stmt|;
name|providers
index|[
name|readerIndex
index|]
operator|=
operator|new
name|ComparableProvider
argument_list|()
block|{
comment|// used only by assert:
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|docsInOrder
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"docs must be sent in order, but lastDocID="
operator|+
name|lastDocID
operator|+
literal|" vs docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparable
name|getComparable
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docsInOrder
argument_list|(
name|docID
argument_list|)
assert|;
name|int
name|readerDocID
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerDocID
operator|<
name|docID
condition|)
block|{
name|readerDocID
operator|=
name|values
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readerDocID
operator|==
name|docID
condition|)
block|{
return|return
name|reverseMul
operator|*
operator|(
name|int
operator|)
name|values
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|reverseMul
operator|*
name|missingValue
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
break|break;
case|case
name|DOUBLE
case|:
block|{
specifier|final
name|Double
name|missingValue
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|=
operator|(
name|Double
operator|)
name|sortField
operator|.
name|getMissingValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingValue
operator|=
literal|0.0
expr_stmt|;
block|}
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|NumericDocValues
name|values
init|=
name|Sorter
operator|.
name|getOrWrapNumeric
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
argument_list|,
name|sortField
argument_list|)
decl_stmt|;
name|providers
index|[
name|readerIndex
index|]
operator|=
operator|new
name|ComparableProvider
argument_list|()
block|{
comment|// used only by assert:
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|docsInOrder
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"docs must be sent in order, but lastDocID="
operator|+
name|lastDocID
operator|+
literal|" vs docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparable
name|getComparable
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docsInOrder
argument_list|(
name|docID
argument_list|)
assert|;
name|int
name|readerDocID
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerDocID
operator|<
name|docID
condition|)
block|{
name|readerDocID
operator|=
name|values
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readerDocID
operator|==
name|docID
condition|)
block|{
return|return
name|reverseMul
operator|*
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|values
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|reverseMul
operator|*
name|missingValue
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
break|break;
case|case
name|FLOAT
case|:
block|{
specifier|final
name|Float
name|missingValue
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getMissingValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|missingValue
operator|=
operator|(
name|Float
operator|)
name|sortField
operator|.
name|getMissingValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|missingValue
operator|=
literal|0.0f
expr_stmt|;
block|}
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIndex
operator|++
control|)
block|{
specifier|final
name|NumericDocValues
name|values
init|=
name|Sorter
operator|.
name|getOrWrapNumeric
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
argument_list|,
name|sortField
argument_list|)
decl_stmt|;
name|providers
index|[
name|readerIndex
index|]
operator|=
operator|new
name|ComparableProvider
argument_list|()
block|{
comment|// used only by assert:
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|docsInOrder
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"docs must be sent in order, but lastDocID="
operator|+
name|lastDocID
operator|+
literal|" vs docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparable
name|getComparable
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docsInOrder
argument_list|(
name|docID
argument_list|)
assert|;
name|int
name|readerDocID
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerDocID
operator|<
name|docID
condition|)
block|{
name|readerDocID
operator|=
name|values
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readerDocID
operator|==
name|docID
condition|)
block|{
return|return
name|reverseMul
operator|*
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|values
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|reverseMul
operator|*
name|missingValue
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unhandled SortField.getType()="
operator|+
name|sortField
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|providers
return|;
block|}
block|}
end_class

end_unit

