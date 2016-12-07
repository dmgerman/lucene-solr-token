begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
name|Arrays
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
name|DocValues
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
name|LeafReaderContext
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
name|MultiDocValues
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uninverting
operator|.
name|FieldCacheImpl
import|;
end_import

begin_comment
comment|/**  * Grabs values from {@link DocValues}.  */
end_comment

begin_class
DECL|class|FacetFieldProcessorByArrayDV
class|class
name|FacetFieldProcessorByArrayDV
extends|extends
name|FacetFieldProcessorByArray
block|{
DECL|field|unwrap_singleValued_multiDv
specifier|static
name|boolean
name|unwrap_singleValued_multiDv
init|=
literal|true
decl_stmt|;
comment|// only set to false for test coverage
DECL|field|multiValuedField
name|boolean
name|multiValuedField
decl_stmt|;
DECL|field|si
name|SortedSetDocValues
name|si
decl_stmt|;
comment|// only used for term lookups (for both single and multi-valued)
DECL|field|ordinalMap
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordinalMap
init|=
literal|null
decl_stmt|;
comment|// maps per-segment ords to global ords
DECL|method|FacetFieldProcessorByArrayDV
name|FacetFieldProcessorByArrayDV
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetField
name|freq
parameter_list|,
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|,
name|sf
argument_list|)
expr_stmt|;
name|multiValuedField
operator|=
name|sf
operator|.
name|multiValued
argument_list|()
operator|||
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|multiValuedFieldCache
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findStartAndEndOrds
specifier|protected
name|void
name|findStartAndEndOrds
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|multiValuedField
condition|)
block|{
name|si
operator|=
name|FieldUtil
operator|.
name|getSortedSetDocValues
argument_list|(
name|fcontext
operator|.
name|qcontext
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|si
operator|instanceof
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
condition|)
block|{
name|ordinalMap
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
operator|)
name|si
operator|)
operator|.
name|mapping
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// multi-valued view
name|SortedDocValues
name|single
init|=
name|FieldUtil
operator|.
name|getSortedDocValues
argument_list|(
name|fcontext
operator|.
name|qcontext
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|si
operator|=
name|DocValues
operator|.
name|singleton
argument_list|(
name|single
argument_list|)
expr_stmt|;
if|if
condition|(
name|single
operator|instanceof
name|MultiDocValues
operator|.
name|MultiSortedDocValues
condition|)
block|{
name|ordinalMap
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedDocValues
operator|)
name|single
operator|)
operator|.
name|mapping
expr_stmt|;
block|}
block|}
if|if
condition|(
name|si
operator|.
name|getValueCount
argument_list|()
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Field has too many unique values. field="
operator|+
name|sf
operator|+
literal|" nterms= "
operator|+
name|si
operator|.
name|getValueCount
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|prefixRef
operator|!=
literal|null
condition|)
block|{
name|startTermIndex
operator|=
operator|(
name|int
operator|)
name|si
operator|.
name|lookupTerm
argument_list|(
name|prefixRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|startTermIndex
operator|<
literal|0
condition|)
name|startTermIndex
operator|=
operator|-
name|startTermIndex
operator|-
literal|1
expr_stmt|;
name|prefixRef
operator|.
name|append
argument_list|(
name|UnicodeUtil
operator|.
name|BIG_TERM
argument_list|)
expr_stmt|;
name|endTermIndex
operator|=
operator|(
name|int
operator|)
name|si
operator|.
name|lookupTerm
argument_list|(
name|prefixRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|endTermIndex
operator|<
literal|0
assert|;
name|endTermIndex
operator|=
operator|-
name|endTermIndex
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|startTermIndex
operator|=
literal|0
expr_stmt|;
name|endTermIndex
operator|=
operator|(
name|int
operator|)
name|si
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
name|nTerms
operator|=
name|endTermIndex
operator|-
name|startTermIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collectDocs
specifier|protected
name|void
name|collectDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|domainSize
init|=
name|fcontext
operator|.
name|base
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|nTerms
operator|<=
literal|0
operator|||
name|domainSize
operator|<
name|effectiveMincount
condition|)
block|{
comment|// TODO: what about allBuckets? missing bucket?
return|return;
block|}
comment|// TODO: refactor some of this logic into a base class
name|boolean
name|countOnly
init|=
name|collectAcc
operator|==
literal|null
operator|&&
name|allBucketsAcc
operator|==
literal|null
decl_stmt|;
name|boolean
name|fullRange
init|=
name|startTermIndex
operator|==
literal|0
operator|&&
name|endTermIndex
operator|==
name|si
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
comment|// Are we expecting many hits per bucket?
comment|// FUTURE: pro-rate for nTerms?
comment|// FUTURE: better take into account number of values in multi-valued fields.  This info is available for indexed fields.
comment|// FUTURE: take into account that bigger ord maps are more expensive than smaller ones
comment|// One test: 5M doc index, faceting on a single-valued field with almost 1M unique values, crossover point where global counting was slower
comment|// than per-segment counting was a domain of 658k docs.  At that point, top 10 buckets had 6-7 matches each.
comment|// this was for heap docvalues produced by UninvertingReader
comment|// Since these values were randomly distributed, lets round our domain multiplier up to account for less random real world data.
name|long
name|domainMultiplier
init|=
name|multiValuedField
condition|?
literal|4L
else|:
literal|2L
decl_stmt|;
name|boolean
name|manyHitsPerBucket
init|=
name|domainSize
operator|*
name|domainMultiplier
operator|>
operator|(
name|si
operator|.
name|getValueCount
argument_list|()
operator|+
literal|3
operator|)
decl_stmt|;
comment|// +3 to increase test coverage with small tests
comment|// If we're only calculating counts, we're not prefixing, and we expect to collect many documents per unique value,
comment|// then collect per-segment before mapping to global ords at the end.  This will save redundant seg->global ord mappings.
comment|// FUTURE: there are probably some other non "countOnly" cases where we can use this as well (i.e. those where
comment|// the docid is not used)
name|boolean
name|canDoPerSeg
init|=
name|countOnly
operator|&&
name|fullRange
decl_stmt|;
name|boolean
name|accumSeg
init|=
name|manyHitsPerBucket
operator|&&
name|canDoPerSeg
decl_stmt|;
if|if
condition|(
name|freq
operator|.
name|perSeg
operator|!=
literal|null
condition|)
name|accumSeg
operator|=
name|canDoPerSeg
operator|&&
name|freq
operator|.
name|perSeg
expr_stmt|;
comment|// internal - override perSeg heuristic
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|fcontext
operator|.
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|fcontext
operator|.
name|base
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|subIdx
init|=
literal|0
init|;
name|subIdx
operator|<
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|subIdx
operator|++
control|)
block|{
name|LeafReaderContext
name|subCtx
init|=
name|leaves
operator|.
name|get
argument_list|(
name|subIdx
argument_list|)
decl_stmt|;
name|setNextReaderFirstPhase
argument_list|(
name|subCtx
argument_list|)
expr_stmt|;
name|DocIdSet
name|dis
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|subCtx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// solr docsets already exclude any deleted docs
name|DocIdSetIterator
name|disi
init|=
name|dis
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|SortedDocValues
name|singleDv
init|=
literal|null
decl_stmt|;
name|SortedSetDocValues
name|multiDv
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|multiValuedField
condition|)
block|{
comment|// TODO: get sub from multi?
name|multiDv
operator|=
name|subCtx
operator|.
name|reader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|multiDv
operator|==
literal|null
condition|)
block|{
name|multiDv
operator|=
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
expr_stmt|;
block|}
comment|// some codecs may optimize SortedSet storage for single-valued fields
comment|// this will be null if this is not a wrapped single valued docvalues.
if|if
condition|(
name|unwrap_singleValued_multiDv
condition|)
block|{
name|singleDv
operator|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|multiDv
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|singleDv
operator|=
name|subCtx
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|singleDv
operator|==
literal|null
condition|)
block|{
name|singleDv
operator|=
name|DocValues
operator|.
name|emptySorted
argument_list|()
expr_stmt|;
block|}
block|}
name|LongValues
name|toGlobal
init|=
name|ordinalMap
operator|==
literal|null
condition|?
literal|null
else|:
name|ordinalMap
operator|.
name|getGlobalOrds
argument_list|(
name|subIdx
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleDv
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|accumSeg
condition|)
block|{
name|collectPerSeg
argument_list|(
name|singleDv
argument_list|,
name|disi
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|canDoPerSeg
operator|&&
name|toGlobal
operator|!=
literal|null
condition|)
block|{
name|collectCounts
argument_list|(
name|singleDv
argument_list|,
name|disi
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectDocs
argument_list|(
name|singleDv
argument_list|,
name|disi
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|accumSeg
condition|)
block|{
name|collectPerSeg
argument_list|(
name|multiDv
argument_list|,
name|disi
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|canDoPerSeg
operator|&&
name|toGlobal
operator|!=
literal|null
condition|)
block|{
name|collectCounts
argument_list|(
name|multiDv
argument_list|,
name|disi
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectDocs
argument_list|(
name|multiDv
argument_list|,
name|disi
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|reuse
operator|=
literal|null
expr_stmt|;
comment|// better GC
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|protected
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|si
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
DECL|method|collectPerSeg
specifier|private
name|void
name|collectPerSeg
parameter_list|(
name|SortedDocValues
name|singleDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|segMax
init|=
name|singleDv
operator|.
name|getValueCount
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|int
index|[]
name|counts
init|=
name|getCountArr
argument_list|(
name|segMax
argument_list|)
decl_stmt|;
comment|/** alternate trial implementations      // ord      // FieldUtil.visitOrds(singleDv, disi,  (doc,ord)->{counts[ord+1]++;} );      FieldUtil.OrdValues ordValues = FieldUtil.getOrdValues(singleDv, disi);     while (ordValues.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {       counts[ ordValues.getOrd() + 1]++;     }      **/
comment|// calculate segment-local counts
name|int
name|doc
decl_stmt|;
if|if
condition|(
name|singleDv
operator|instanceof
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
condition|)
block|{
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
name|fc
init|=
operator|(
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
operator|)
name|singleDv
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|counts
index|[
name|fc
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
operator|+
literal|1
index|]
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|singleDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|counts
index|[
name|singleDv
operator|.
name|ordValue
argument_list|()
operator|+
literal|1
index|]
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|// convert segment-local counts to global counts
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|segMax
condition|;
name|i
operator|++
control|)
block|{
name|int
name|segCount
init|=
name|counts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|segCount
operator|>
literal|0
condition|)
block|{
name|int
name|slot
init|=
name|toGlobal
operator|==
literal|null
condition|?
operator|(
name|i
operator|-
literal|1
operator|)
else|:
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|slot
argument_list|,
name|segCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|collectPerSeg
specifier|private
name|void
name|collectPerSeg
parameter_list|(
name|SortedSetDocValues
name|multiDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|segMax
init|=
operator|(
name|int
operator|)
name|multiDv
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|counts
init|=
name|getCountArr
argument_list|(
name|segMax
argument_list|)
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|multiDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|segOrd
init|=
operator|(
name|int
operator|)
name|multiDv
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|segOrd
operator|<
literal|0
condition|)
break|break;
name|counts
index|[
name|segOrd
index|]
operator|++
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segMax
condition|;
name|i
operator|++
control|)
block|{
name|int
name|segCount
init|=
name|counts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|segCount
operator|>
literal|0
condition|)
block|{
name|int
name|slot
init|=
name|toGlobal
operator|==
literal|null
condition|?
operator|(
name|i
operator|)
else|:
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|slot
argument_list|,
name|segCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|reuse
specifier|private
name|int
index|[]
name|reuse
decl_stmt|;
DECL|method|getCountArr
specifier|private
name|int
index|[]
name|getCountArr
parameter_list|(
name|int
name|maxNeeded
parameter_list|)
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
condition|)
block|{
comment|// make the count array large enough for any segment
comment|// FUTURE: (optionally) directly use the array of the CountAcc for an optimized index..
name|reuse
operator|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|si
operator|.
name|getValueCount
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|reuse
argument_list|,
literal|0
argument_list|,
name|maxNeeded
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|reuse
return|;
block|}
DECL|method|collectDocs
specifier|private
name|void
name|collectDocs
parameter_list|(
name|SortedDocValues
name|singleDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|singleDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|int
name|segOrd
init|=
name|singleDv
operator|.
name|ordValue
argument_list|()
decl_stmt|;
name|collect
argument_list|(
name|doc
argument_list|,
name|segOrd
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|collectCounts
specifier|private
name|void
name|collectCounts
parameter_list|(
name|SortedDocValues
name|singleDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
if|if
condition|(
name|singleDv
operator|instanceof
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
condition|)
block|{
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
name|fc
init|=
operator|(
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
operator|)
name|singleDv
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|segOrd
init|=
name|fc
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|segOrd
operator|<
literal|0
condition|)
continue|continue;
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|segOrd
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|ord
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|singleDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|int
name|segOrd
init|=
name|singleDv
operator|.
name|ordValue
argument_list|()
decl_stmt|;
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|segOrd
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|ord
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|collectDocs
specifier|private
name|void
name|collectDocs
parameter_list|(
name|SortedSetDocValues
name|multiDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|multiDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|segOrd
init|=
operator|(
name|int
operator|)
name|multiDv
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|segOrd
operator|<
literal|0
condition|)
break|break;
name|collect
argument_list|(
name|doc
argument_list|,
name|segOrd
argument_list|,
name|toGlobal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|collectCounts
specifier|private
name|void
name|collectCounts
parameter_list|(
name|SortedSetDocValues
name|multiDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|multiDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|segOrd
init|=
operator|(
name|int
operator|)
name|multiDv
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|segOrd
operator|<
literal|0
condition|)
break|break;
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|segOrd
argument_list|)
decl_stmt|;
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|ord
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|collect
specifier|private
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|segOrd
parameter_list|,
name|LongValues
name|toGlobal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
operator|(
name|toGlobal
operator|!=
literal|null
operator|&&
name|segOrd
operator|>=
literal|0
operator|)
condition|?
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|segOrd
argument_list|)
else|:
name|segOrd
decl_stmt|;
name|int
name|arrIdx
init|=
name|ord
operator|-
name|startTermIndex
decl_stmt|;
if|if
condition|(
name|arrIdx
operator|>=
literal|0
operator|&&
name|arrIdx
operator|<
name|nTerms
condition|)
block|{
name|countAcc
operator|.
name|incrementCount
argument_list|(
name|arrIdx
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|arrIdx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allBucketsAcc
operator|!=
literal|null
condition|)
block|{
name|allBucketsAcc
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|arrIdx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

