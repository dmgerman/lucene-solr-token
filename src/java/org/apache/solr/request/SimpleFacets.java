begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Term
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
name|TermEnum
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
name|TermDocs
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
name|queryParser
operator|.
name|ParseException
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
name|*
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
name|core
operator|.
name|SolrCore
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
name|core
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
name|core
operator|.
name|SolrConfig
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
name|request
operator|.
name|SolrParams
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
name|IndexSchema
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
name|schema
operator|.
name|BoolField
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
name|*
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
name|util
operator|.
name|BoundedTreeSet
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
name|SimpleOrderedMap
import|;
end_import

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
name|Comparator
import|;
end_import

begin_comment
comment|/**  * A class that generates simple Facet information for a request.  *  * More advanced facet implementations may compose or subclass this class   * to leverage any of it's functionality.  */
end_comment

begin_class
DECL|class|SimpleFacets
specifier|public
class|class
name|SimpleFacets
block|{
comment|/** The main set of documents all facet counts should be relative to */
DECL|field|docs
specifier|protected
name|DocSet
name|docs
decl_stmt|;
comment|/** Configuration params behavior should be driven by */
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
comment|/** Searcher to use for all calculations */
DECL|field|searcher
specifier|protected
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|method|SimpleFacets
specifier|public
name|SimpleFacets
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|/**    * Looks at various Params to determing if any simple Facet Constraint count    * computations are desired.    *    * @see #getFacetQueryCounts    * @see #getFacetFieldCounts    * @see SolrParams#FACET    * @return a NamedList of Facet Count info or null    */
DECL|method|getFacetCounts
specifier|public
name|NamedList
name|getFacetCounts
parameter_list|()
block|{
comment|// if someone called this method, benefit of the doubt: assume true
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|params
operator|.
name|FACET
argument_list|,
literal|true
argument_list|)
condition|)
return|return
literal|null
return|;
name|NamedList
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
try|try
block|{
name|res
operator|.
name|add
argument_list|(
literal|"facet_queries"
argument_list|,
name|getFacetQueryCounts
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"facet_fields"
argument_list|,
name|getFacetFieldCounts
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|logOnce
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception during facet counts"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"exception"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Returns a list of facet counts for each of the facet queries     * specified in the params    *    * @see SolrParams#FACET_QUERY    */
DECL|method|getFacetQueryCounts
specifier|public
name|NamedList
name|getFacetQueryCounts
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|NamedList
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
comment|/* Ignore SolrParams.DF - could have init param facet.query assuming      * the schema default with query param DF intented to only affect Q.      * If user doesn't want schema default for facet.query, they should be      * explicit.      */
name|SolrQueryParser
name|qp
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getSolrQueryParser
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
index|[]
name|facetQs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|SolrParams
operator|.
name|FACET_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|facetQs
operator|&&
literal|0
operator|!=
name|facetQs
operator|.
name|length
condition|)
block|{
for|for
control|(
name|String
name|q
range|:
name|facetQs
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|searcher
operator|.
name|numDocs
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
name|q
argument_list|)
argument_list|,
name|docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
DECL|method|getTermCounts
specifier|public
name|NamedList
name|getTermCounts
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_OFFSET
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|limit
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_LIMIT
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Integer
name|mincount
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_MINCOUNT
argument_list|)
decl_stmt|;
if|if
condition|(
name|mincount
operator|==
literal|null
condition|)
block|{
name|Boolean
name|zeros
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_ZEROS
argument_list|)
decl_stmt|;
comment|// mincount = (zeros!=null&& zeros) ? 0 : 1;
name|mincount
operator|=
operator|(
name|zeros
operator|!=
literal|null
operator|&&
operator|!
name|zeros
operator|)
condition|?
literal|1
else|:
literal|0
expr_stmt|;
comment|// current default is to include zeros.
block|}
name|boolean
name|missing
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_MISSING
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// default to sorting if there is a limit.
name|boolean
name|sort
init|=
name|params
operator|.
name|getFieldBool
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_SORT
argument_list|,
name|limit
operator|>
literal|0
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|params
operator|.
name|FACET_PREFIX
argument_list|)
decl_stmt|;
name|NamedList
name|counts
decl_stmt|;
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
operator|||
name|ft
operator|.
name|isTokenized
argument_list|()
operator|||
name|ft
operator|instanceof
name|BoolField
condition|)
block|{
comment|// Always use filters for booleans... we know the number of values is very small.
name|counts
operator|=
name|getFacetTermEnumCounts
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|field
argument_list|,
name|offset
argument_list|,
name|limit
argument_list|,
name|mincount
argument_list|,
name|missing
argument_list|,
name|sort
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: future logic could use filters instead of the fieldcache if
comment|// the number of terms in the field is small enough.
name|counts
operator|=
name|getFieldCacheCounts
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|field
argument_list|,
name|offset
argument_list|,
name|limit
argument_list|,
name|mincount
argument_list|,
name|missing
argument_list|,
name|sort
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
return|return
name|counts
return|;
block|}
comment|/**    * Returns a list of value constraints and the associated facet counts     * for each facet field specified in the params.    *    * @see SolrParams#FACET_FIELD    * @see #getFieldMissingCount    * @see #getFacetTermEnumCounts    */
DECL|method|getFacetFieldCounts
specifier|public
name|NamedList
name|getFacetFieldCounts
parameter_list|()
throws|throws
name|IOException
block|{
name|NamedList
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|String
index|[]
name|facetFs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|SolrParams
operator|.
name|FACET_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|facetFs
condition|)
block|{
for|for
control|(
name|String
name|f
range|:
name|facetFs
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|getTermCounts
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**    * Returns a count of the documents in the set which do not have any     * terms for for the specified field.    *    * @see SolrParams#FACET_MISSING    */
DECL|method|getFieldMissingCount
specifier|public
specifier|static
name|int
name|getFieldMissingCount
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|DocSet
name|hasVal
init|=
name|searcher
operator|.
name|getDocSet
argument_list|(
operator|new
name|ConstantScoreRangeQuery
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|docs
operator|.
name|andNotSize
argument_list|(
name|hasVal
argument_list|)
return|;
block|}
comment|// first element of the fieldcache is null, so we need this comparator.
DECL|field|nullStrComparator
specifier|private
specifier|static
specifier|final
name|Comparator
name|nullStrComparator
init|=
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
return|return
operator|(
name|o2
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
operator|-
literal|1
return|;
elseif|else
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
return|return
literal|1
return|;
return|return
operator|(
operator|(
name|String
operator|)
name|o1
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
name|String
operator|)
name|o2
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Use the Lucene FieldCache to get counts for each unique field value in<code>docs</code>.    * The field must have at most one indexed token per document.    */
DECL|method|getFieldCacheCounts
specifier|public
specifier|static
name|NamedList
name|getFieldCacheCounts
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
name|boolean
name|sort
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: If the number of terms is high compared to docs.size(), and zeros==false,
comment|//  we should use an alternate strategy to avoid
comment|//  1) creating another huge int[] for the counts
comment|//  2) looping over that huge int[] looking for the rare non-zeros.
comment|//
comment|// Yet another variation: if docs.size() is small and termvectors are stored,
comment|// then use them instead of the FieldCache.
comment|//
comment|// TODO: this function is too big and could use some refactoring, but
comment|// we also need a facet cache, and refactoring of SimpleFacets instead of
comment|// trying to pass all the various params around.
name|FieldType
name|ft
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|FieldCache
operator|.
name|StringIndex
name|si
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStringIndex
argument_list|(
name|searcher
operator|.
name|getReader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|terms
init|=
name|si
operator|.
name|lookup
decl_stmt|;
specifier|final
name|int
index|[]
name|termNum
init|=
name|si
operator|.
name|order
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|prefix
operator|=
literal|null
expr_stmt|;
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
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|terms
argument_list|,
name|prefix
argument_list|,
name|nullStrComparator
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
comment|// find the end term.  \uffff isn't a legal unicode char, but only compareTo
comment|// is used, so it should be fine, and is guaranteed to be bigger than legal chars.
name|endTermIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|terms
argument_list|,
name|prefix
operator|+
literal|"\uffff\uffff\uffff\uffff"
argument_list|,
name|nullStrComparator
argument_list|)
expr_stmt|;
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
literal|1
expr_stmt|;
name|endTermIndex
operator|=
name|terms
operator|.
name|length
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
if|if
condition|(
name|nTerms
operator|>
literal|0
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
name|DocIterator
name|iter
init|=
name|docs
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|term
init|=
name|termNum
index|[
name|iter
operator|.
name|nextDoc
argument_list|()
index|]
decl_stmt|;
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
name|nTerms
condition|)
name|counts
index|[
name|arrIdx
index|]
operator|++
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
specifier|final
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|(
name|maxsize
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
name|queue
operator|.
name|add
argument_list|(
operator|new
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|terms
index|[
name|startTermIndex
operator|+
name|i
index|]
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>=
name|maxsize
condition|)
name|min
operator|=
name|queue
operator|.
name|last
argument_list|()
operator|.
name|val
expr_stmt|;
block|}
block|}
comment|// now select the right page from the results
for|for
control|(
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|p
range|:
name|queue
control|)
block|{
if|if
condition|(
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
name|res
operator|.
name|add
argument_list|(
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|p
operator|.
name|key
argument_list|)
argument_list|,
name|p
operator|.
name|val
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
operator|=
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
name|res
operator|.
name|add
argument_list|(
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|terms
index|[
name|startTermIndex
operator|+
name|i
index|]
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|missing
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|getFieldMissingCount
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Returns a list of terms in the specified field along with the     * corresponding count of documents in the set that match that constraint.    * This method uses the FilterCache to get the intersection count between<code>docs</code>    * and the DocSet for each term in the filter.    *    * @see SolrParams#FACET_LIMIT    * @see SolrParams#FACET_ZEROS    * @see SolrParams#FACET_MISSING    */
DECL|method|getFacetTermEnumCounts
specifier|public
name|NamedList
name|getFacetTermEnumCounts
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|String
name|field
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
name|boolean
name|sort
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* :TODO: potential optimization...     * cache the Terms with the highest docFreq and try them first     * don't enum if we get our max from them     */
comment|// Minimum term docFreq in order to use the filterCache for that term.
name|int
name|minDfFilterCache
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|SolrParams
operator|.
name|FACET_ENUM_CACHE_MINDF
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxsize
init|=
name|limit
operator|>=
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
specifier|final
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|queue
init|=
name|sort
condition|?
operator|new
name|BoundedTreeSet
argument_list|<
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|(
name|maxsize
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|NamedList
name|res
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|int
name|min
init|=
name|mincount
operator|-
literal|1
decl_stmt|;
comment|// the smallest value in the top 'N' values
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
name|String
name|startTerm
init|=
name|prefix
operator|==
literal|null
condition|?
literal|""
else|:
name|ft
operator|.
name|toInternal
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|TermEnum
name|te
init|=
name|r
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|startTerm
argument_list|)
argument_list|)
decl_stmt|;
name|TermDocs
name|td
init|=
name|r
operator|.
name|termDocs
argument_list|()
decl_stmt|;
do|do
block|{
name|Term
name|t
init|=
name|te
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|t
operator|||
operator|!
name|t
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
break|break;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
operator|!
name|t
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
break|break;
name|int
name|df
init|=
name|te
operator|.
name|docFreq
argument_list|()
decl_stmt|;
comment|// If we are sorting, we can use df>min (rather than>=) since we
comment|// are going in index order.  For certain term distributions this can
comment|// make a large difference (for example, many terms with df=1).
if|if
condition|(
name|df
operator|>
literal|0
operator|&&
name|df
operator|>
name|min
condition|)
block|{
name|int
name|c
decl_stmt|;
if|if
condition|(
name|df
operator|>=
name|minDfFilterCache
condition|)
block|{
comment|// use the filter cache
name|c
operator|=
name|searcher
operator|.
name|numDocs
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// iterate over TermDocs to calculate the intersection
name|td
operator|.
name|seek
argument_list|(
name|te
argument_list|)
expr_stmt|;
name|c
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|td
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|docs
operator|.
name|exists
argument_list|(
name|td
operator|.
name|doc
argument_list|()
argument_list|)
condition|)
name|c
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sort
condition|)
block|{
if|if
condition|(
name|c
operator|>
name|min
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
operator|new
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>=
name|maxsize
condition|)
name|min
operator|=
name|queue
operator|.
name|last
argument_list|()
operator|.
name|val
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|c
operator|>=
name|mincount
operator|&&
operator|--
name|off
operator|<
literal|0
condition|)
block|{
if|if
condition|(
operator|--
name|lim
operator|<
literal|0
condition|)
break|break;
name|res
operator|.
name|add
argument_list|(
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
condition|)
do|;
if|if
condition|(
name|sort
condition|)
block|{
for|for
control|(
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|p
range|:
name|queue
control|)
block|{
if|if
condition|(
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
name|res
operator|.
name|add
argument_list|(
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|p
operator|.
name|key
argument_list|)
argument_list|,
name|p
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|missing
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|getFieldMissingCount
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|te
operator|.
name|close
argument_list|()
expr_stmt|;
name|td
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * A simple key=>val pair whose natural order is such that     *<b>higher</b> vals come before lower vals.    * In case of tie vals, then<b>lower</b> keys come before higher keys.    */
DECL|class|CountPair
specifier|public
specifier|static
class|class
name|CountPair
parameter_list|<
name|K
extends|extends
name|Comparable
parameter_list|<
name|?
super|super
name|K
parameter_list|>
parameter_list|,
name|V
extends|extends
name|Comparable
parameter_list|<
name|?
super|super
name|V
parameter_list|>
parameter_list|>
implements|implements
name|Comparable
argument_list|<
name|CountPair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
DECL|method|CountPair
specifier|public
name|CountPair
parameter_list|(
name|K
name|k
parameter_list|,
name|V
name|v
parameter_list|)
block|{
name|key
operator|=
name|k
expr_stmt|;
name|val
operator|=
name|v
expr_stmt|;
block|}
DECL|field|key
specifier|public
name|K
name|key
decl_stmt|;
DECL|field|val
specifier|public
name|V
name|val
decl_stmt|;
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|key
operator|.
name|hashCode
argument_list|()
operator|^
name|val
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|CountPair
operator|)
operator|&&
operator|(
literal|0
operator|==
name|this
operator|.
name|compareTo
argument_list|(
operator|(
name|CountPair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
name|o
argument_list|)
operator|)
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|CountPair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|o
parameter_list|)
block|{
name|int
name|vc
init|=
name|o
operator|.
name|val
operator|.
name|compareTo
argument_list|(
name|val
argument_list|)
decl_stmt|;
return|return
operator|(
literal|0
operator|!=
name|vc
condition|?
name|vc
else|:
name|key
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|key
argument_list|)
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

