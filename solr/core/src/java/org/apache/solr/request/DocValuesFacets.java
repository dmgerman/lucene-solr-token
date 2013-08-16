begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|AtomicReaderContext
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
operator|.
name|MultiSortedDocValues
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
operator|.
name|MultiSortedSetDocValues
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
operator|.
name|OrdinalMap
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
name|SingletonSortedSetDocValues
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
name|CharsRef
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
name|params
operator|.
name|FacetParams
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
name|util
operator|.
name|NamedList
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
name|FieldType
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
name|DocSet
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
name|SolrIndexSearcher
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
name|util
operator|.
name|LongPriorityQueue
import|;
end_import

begin_comment
comment|/**  * Computes term facets for docvalues field (single or multivalued).  *<p>  * This is basically a specialized case of the code in SimpleFacets.  * Instead of working on a top-level reader view (binary-search per docid),  * it collects per-segment, but maps ordinals to global ordinal space using  * MultiDocValues' OrdinalMap.  *<p>  * This means the ordinal map is created per-reopen: O(nterms), but this may  * perform better than PerSegmentSingleValuedFaceting which has to merge O(nterms)  * per query. Additionally it works for multi-valued fields.  */
end_comment

begin_class
DECL|class|DocValuesFacets
specifier|public
class|class
name|DocValuesFacets
block|{
DECL|method|DocValuesFacets
specifier|private
name|DocValuesFacets
parameter_list|()
block|{}
DECL|method|getCounts
specifier|public
specifier|static
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getCounts
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|mincount
parameter_list|,
name|boolean
name|missing
parameter_list|,
name|String
name|sort
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|schemaField
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
name|schemaField
operator|.
name|getType
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|SortedSetDocValues
name|si
decl_stmt|;
comment|// for term lookups only
name|OrdinalMap
name|ordinalMap
init|=
literal|null
decl_stmt|;
comment|// for mapping per-segment ords to global ones
if|if
condition|(
name|schemaField
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|si
operator|=
name|searcher
operator|.
name|getAtomicReader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|si
operator|instanceof
name|MultiSortedSetDocValues
condition|)
block|{
name|ordinalMap
operator|=
operator|(
operator|(
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
name|SortedDocValues
name|single
init|=
name|searcher
operator|.
name|getAtomicReader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|si
operator|=
name|single
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|SingletonSortedSetDocValues
argument_list|(
name|single
argument_list|)
expr_stmt|;
if|if
condition|(
name|single
operator|instanceof
name|MultiSortedDocValues
condition|)
block|{
name|ordinalMap
operator|=
operator|(
operator|(
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
operator|==
literal|null
condition|)
block|{
return|return
name|finalize
argument_list|(
name|res
argument_list|,
name|searcher
argument_list|,
name|schemaField
argument_list|,
name|docs
argument_list|,
operator|-
literal|1
argument_list|,
name|missing
argument_list|)
return|;
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
name|UnsupportedOperationException
argument_list|(
literal|"Currently this faceting method is limited to "
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|" unique terms"
argument_list|)
throw|;
block|}
specifier|final
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|prefixRef
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefixRef
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|prefix
operator|=
literal|null
expr_stmt|;
name|prefixRef
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|prefixRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
name|int
name|startTermIndex
decl_stmt|,
name|endTermIndex
decl_stmt|;
if|if
condition|(
name|prefix
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
operator|-
literal|1
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
specifier|final
name|int
name|nTerms
init|=
name|endTermIndex
operator|-
name|startTermIndex
decl_stmt|;
name|int
name|missingCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|CharsRef
name|charsRef
init|=
operator|new
name|CharsRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|nTerms
operator|>
literal|0
operator|&&
name|docs
operator|.
name|size
argument_list|()
operator|>=
name|mincount
condition|)
block|{
comment|// count collection array only needs to be as big as the number of terms we are
comment|// going to collect counts for.
specifier|final
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|nTerms
index|]
decl_stmt|;
name|Filter
name|filter
init|=
name|docs
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|subIndex
init|=
literal|0
init|;
name|subIndex
operator|<
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|subIndex
operator|++
control|)
block|{
name|AtomicReaderContext
name|leaf
init|=
name|leaves
operator|.
name|get
argument_list|(
name|subIndex
argument_list|)
decl_stmt|;
name|DocIdSet
name|dis
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|leaf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// solr docsets already exclude any deleted docs
name|DocIdSetIterator
name|disi
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dis
operator|!=
literal|null
condition|)
block|{
name|disi
operator|=
name|dis
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|disi
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|schemaField
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|SortedSetDocValues
name|sub
init|=
name|leaf
operator|.
name|reader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|sub
operator|=
name|SortedSetDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
name|accumMulti
argument_list|(
name|counts
argument_list|,
name|startTermIndex
argument_list|,
name|sub
argument_list|,
name|disi
argument_list|,
name|subIndex
argument_list|,
name|ordinalMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SortedDocValues
name|sub
init|=
name|leaf
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|sub
operator|=
name|SortedDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
name|accumSingle
argument_list|(
name|counts
argument_list|,
name|startTermIndex
argument_list|,
name|sub
argument_list|,
name|disi
argument_list|,
name|subIndex
argument_list|,
name|ordinalMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|startTermIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|missingCount
operator|=
name|counts
index|[
literal|0
index|]
expr_stmt|;
block|}
comment|// IDEA: we could also maintain a count of "other"... everything that fell outside
comment|// of the top 'N'
name|int
name|off
init|=
name|offset
decl_stmt|;
name|int
name|lim
init|=
name|limit
operator|>=
literal|0
condition|?
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|sort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT
argument_list|)
operator|||
name|sort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT_LEGACY
argument_list|)
condition|)
block|{
name|int
name|maxsize
init|=
name|limit
operator|>
literal|0
condition|?
name|offset
operator|+
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
decl_stmt|;
name|maxsize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|maxsize
argument_list|,
name|nTerms
argument_list|)
expr_stmt|;
name|LongPriorityQueue
name|queue
init|=
operator|new
name|LongPriorityQueue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|maxsize
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|maxsize
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
name|int
name|min
init|=
name|mincount
operator|-
literal|1
decl_stmt|;
comment|// the smallest value in the top 'N' values
for|for
control|(
name|int
name|i
init|=
operator|(
name|startTermIndex
operator|==
operator|-
literal|1
operator|)
condition|?
literal|1
else|:
literal|0
init|;
name|i
operator|<
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|counts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|>
name|min
condition|)
block|{
comment|// NOTE: we use c>min rather than c>=min as an optimization because we are going in
comment|// index order, so we already know that the keys are ordered.  This can be very
comment|// important if a lot of the counts are repeated (like zero counts would be).
comment|// smaller term numbers sort higher, so subtract the term number instead
name|long
name|pair
init|=
operator|(
operator|(
operator|(
name|long
operator|)
name|c
operator|)
operator|<<
literal|32
operator|)
operator|+
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|i
operator|)
decl_stmt|;
name|boolean
name|displaced
init|=
name|queue
operator|.
name|insert
argument_list|(
name|pair
argument_list|)
decl_stmt|;
if|if
condition|(
name|displaced
condition|)
name|min
operator|=
call|(
name|int
call|)
argument_list|(
name|queue
operator|.
name|top
argument_list|()
operator|>>>
literal|32
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if we are deep paging, we don't have to order the highest "offset" counts.
name|int
name|collectCount
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
operator|-
name|off
argument_list|)
decl_stmt|;
assert|assert
name|collectCount
operator|<=
name|lim
assert|;
comment|// the start and end indexes of our list "sorted" (starting with the highest value)
name|int
name|sortedIdxStart
init|=
name|queue
operator|.
name|size
argument_list|()
operator|-
operator|(
name|collectCount
operator|-
literal|1
operator|)
decl_stmt|;
name|int
name|sortedIdxEnd
init|=
name|queue
operator|.
name|size
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|long
index|[]
name|sorted
init|=
name|queue
operator|.
name|sort
argument_list|(
name|collectCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|sortedIdxStart
init|;
name|i
operator|<
name|sortedIdxEnd
condition|;
name|i
operator|++
control|)
block|{
name|long
name|pair
init|=
name|sorted
index|[
name|i
index|]
decl_stmt|;
name|int
name|c
init|=
call|(
name|int
call|)
argument_list|(
name|pair
operator|>>>
literal|32
argument_list|)
decl_stmt|;
name|int
name|tnum
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|-
operator|(
name|int
operator|)
name|pair
decl_stmt|;
name|si
operator|.
name|lookupOrd
argument_list|(
name|startTermIndex
operator|+
name|tnum
argument_list|,
name|br
argument_list|)
expr_stmt|;
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|br
argument_list|,
name|charsRef
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|charsRef
operator|.
name|toString
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// add results in index order
name|int
name|i
init|=
operator|(
name|startTermIndex
operator|==
operator|-
literal|1
operator|)
condition|?
literal|1
else|:
literal|0
decl_stmt|;
if|if
condition|(
name|mincount
operator|<=
literal|0
condition|)
block|{
comment|// if mincount<=0, then we won't discard any terms and we know exactly
comment|// where to start.
name|i
operator|+=
name|off
expr_stmt|;
name|off
operator|=
literal|0
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|counts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|<
name|mincount
operator|||
operator|--
name|off
operator|>=
literal|0
condition|)
continue|continue;
if|if
condition|(
operator|--
name|lim
operator|<
literal|0
condition|)
break|break;
name|si
operator|.
name|lookupOrd
argument_list|(
name|startTermIndex
operator|+
name|i
argument_list|,
name|br
argument_list|)
expr_stmt|;
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|br
argument_list|,
name|charsRef
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|charsRef
operator|.
name|toString
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|finalize
argument_list|(
name|res
argument_list|,
name|searcher
argument_list|,
name|schemaField
argument_list|,
name|docs
argument_list|,
name|missingCount
argument_list|,
name|missing
argument_list|)
return|;
block|}
comment|/** finalizes result: computes missing count if applicable */
DECL|method|finalize
specifier|static
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|finalize
parameter_list|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|res
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|SchemaField
name|schemaField
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|int
name|missingCount
parameter_list|,
name|boolean
name|missing
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|missing
condition|)
block|{
if|if
condition|(
name|missingCount
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|schemaField
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|missingCount
operator|=
name|SimpleFacets
operator|.
name|getFieldMissingCount
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// nocommit: support missing count (ord = -1) for single-valued here.
name|missingCount
operator|=
literal|0
expr_stmt|;
comment|// single-valued dv is implicitly 0
block|}
block|}
name|res
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|missingCount
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/** accumulates per-segment single-valued facet counts, mapping to global ordinal space */
comment|// specialized since the single-valued case is simpler: you don't have to deal with missing count, etc
DECL|method|accumSingle
specifier|static
name|void
name|accumSingle
parameter_list|(
name|int
name|counts
index|[]
parameter_list|,
name|int
name|startTermIndex
parameter_list|,
name|SortedDocValues
name|si
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|int
name|subIndex
parameter_list|,
name|OrdinalMap
name|map
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
name|int
name|term
init|=
name|si
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|term
operator|=
operator|(
name|int
operator|)
name|map
operator|.
name|getGlobalOrd
argument_list|(
name|subIndex
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|int
name|arrIdx
init|=
name|term
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
name|counts
operator|.
name|length
condition|)
name|counts
index|[
name|arrIdx
index|]
operator|++
expr_stmt|;
block|}
block|}
comment|/** accumulates per-segment multi-valued facet counts, mapping to global ordinal space */
DECL|method|accumMulti
specifier|static
name|void
name|accumMulti
parameter_list|(
name|int
name|counts
index|[]
parameter_list|,
name|int
name|startTermIndex
parameter_list|,
name|SortedSetDocValues
name|si
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|int
name|subIndex
parameter_list|,
name|OrdinalMap
name|map
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
name|si
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// strange do-while to collect the missing count (first ord is NO_MORE_ORDS)
name|int
name|term
init|=
operator|(
name|int
operator|)
name|si
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|startTermIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|counts
index|[
literal|0
index|]
operator|++
expr_stmt|;
comment|// missing count
block|}
continue|continue;
block|}
do|do
block|{
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|term
operator|=
operator|(
name|int
operator|)
name|map
operator|.
name|getGlobalOrd
argument_list|(
name|subIndex
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|int
name|arrIdx
init|=
name|term
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
name|counts
operator|.
name|length
condition|)
name|counts
index|[
name|arrIdx
index|]
operator|++
expr_stmt|;
block|}
do|while
condition|(
operator|(
name|term
operator|=
operator|(
name|int
operator|)
name|si
operator|.
name|nextOrd
argument_list|()
operator|)
operator|>=
literal|0
condition|)
do|;
block|}
block|}
block|}
end_class

end_unit

