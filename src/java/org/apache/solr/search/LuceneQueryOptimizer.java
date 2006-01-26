begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/* Copyright (c) 2003 The Nutch Organization.  All rights reserved.   */
end_comment

begin_comment
comment|/* Use subject to the conditions in http://www.nutch.org/LICENSE.txt. */
end_comment

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
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** Utility which converts certain query clauses into {@link QueryFilter}s and  * caches these.  Only required {@link TermQuery}s whose boost is zero and  * whose term occurs in at least a certain fraction of documents are converted  * to cached filters.  This accellerates query constraints like language,  * document format, etc., which do not affect ranking but might otherwise slow  * search considerably. */
end_comment

begin_comment
comment|// Taken from Nutch and modified - YCS
end_comment

begin_class
DECL|class|LuceneQueryOptimizer
class|class
name|LuceneQueryOptimizer
block|{
DECL|field|cache
specifier|private
name|LinkedHashMap
name|cache
decl_stmt|;
comment|// an LRU cache of QueryFilter
DECL|field|threshold
specifier|private
name|float
name|threshold
decl_stmt|;
comment|/** Construct an optimizer that caches and uses filters for required {@link    * TermQuery}s whose boost is zero.    * @param cacheSize the number of QueryFilters to cache    * @param threshold the fraction of documents which must contain term    */
DECL|method|LuceneQueryOptimizer
specifier|public
name|LuceneQueryOptimizer
parameter_list|(
specifier|final
name|int
name|cacheSize
parameter_list|,
name|float
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
operator|new
name|LinkedHashMap
argument_list|(
name|cacheSize
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
block|{
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|cacheSize
return|;
comment|// limit size of cache
block|}
block|}
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
block|}
DECL|method|optimize
specifier|public
name|TopDocs
name|optimize
parameter_list|(
name|BooleanQuery
name|original
parameter_list|,
name|Searcher
name|searcher
parameter_list|,
name|int
name|numHits
parameter_list|,
name|Query
index|[]
name|queryOut
parameter_list|,
name|Filter
index|[]
name|filterOut
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BooleanQuery
name|filterQuery
init|=
literal|null
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|original
operator|.
name|getClauses
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|c
init|=
name|clauses
index|[
name|i
index|]
decl_stmt|;
comment|/*** System.out.println("required="+c.required); System.out.println("boost="+c.query.getBoost()); System.out.println("isTermQuery="+(c.query instanceof TermQuery)); if (c.query instanceof TermQuery) {  System.out.println("term="+((TermQuery)c.query).getTerm());  System.out.println("docFreq="+searcher.docFreq(((TermQuery)c.query).getTerm())); } ***/
if|if
condition|(
name|c
operator|.
name|required
comment|// required
operator|&&
name|c
operator|.
name|query
operator|.
name|getBoost
argument_list|()
operator|==
literal|0.0f
comment|// boost is zero
operator|&&
name|c
operator|.
name|query
operator|instanceof
name|TermQuery
comment|// TermQuery
operator|&&
operator|(
name|searcher
operator|.
name|docFreq
argument_list|(
operator|(
operator|(
name|TermQuery
operator|)
name|c
operator|.
name|query
operator|)
operator|.
name|getTerm
argument_list|()
argument_list|)
operator|/
operator|(
name|float
operator|)
name|searcher
operator|.
name|maxDoc
argument_list|()
operator|)
operator|>=
name|threshold
condition|)
block|{
comment|// check threshold
if|if
condition|(
name|filterQuery
operator|==
literal|null
condition|)
name|filterQuery
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|filterQuery
operator|.
name|add
argument_list|(
name|c
operator|.
name|query
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// filter it
comment|//System.out.println("WooHoo... qualified to be hoisted to a filter!");
block|}
else|else
block|{
name|query
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// query it
block|}
block|}
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|filterQuery
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// check cache
name|filter
operator|=
operator|(
name|Filter
operator|)
name|cache
operator|.
name|get
argument_list|(
name|filterQuery
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
comment|// miss
name|filter
operator|=
operator|new
name|QueryFilter
argument_list|(
name|filterQuery
argument_list|)
expr_stmt|;
comment|// construct new entry
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|filterQuery
argument_list|,
name|filter
argument_list|)
expr_stmt|;
comment|// cache it
block|}
block|}
block|}
comment|// YCS: added code to pass out optimized query and filter
comment|// so they can be used with Hits
if|if
condition|(
name|queryOut
operator|!=
literal|null
operator|&&
name|filterOut
operator|!=
literal|null
condition|)
block|{
name|queryOut
index|[
literal|0
index|]
operator|=
name|query
expr_stmt|;
name|filterOut
index|[
literal|0
index|]
operator|=
name|filter
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|numHits
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

