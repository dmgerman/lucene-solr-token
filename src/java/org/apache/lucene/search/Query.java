begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/** The abstract base class for queries.<p>Instantiable subclasses are:<ul><li> {@link TermQuery}<li> {@link MultiTermQuery}<li> {@link BooleanQuery}<li> {@link WildcardQuery}<li> {@link PhraseQuery}<li> {@link PrefixQuery}<li> {@link PhrasePrefixQuery}<li> {@link FuzzyQuery}<li> {@link RangeQuery}<li> {@link org.apache.lucene.search.spans.SpanQuery}</ul><p>A parser for queries is contained in:<ul><li>{@link org.apache.lucene.queryParser.QueryParser QueryParser}</ul> */
end_comment

begin_class
DECL|class|Query
specifier|public
specifier|abstract
class|class
name|Query
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
implements|,
name|Cloneable
block|{
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|// query boost factor
comment|/** Sets the boost for this query clause to<code>b</code>.  Documents    * matching this clause will (in addition to the normal weightings) have    * their score multiplied by<code>b</code>.    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|b
parameter_list|)
block|{
name|boost
operator|=
name|b
expr_stmt|;
block|}
comment|/** Gets the boost for this clause.  Documents matching    * this clause will (in addition to the normal weightings) have their score    * multiplied by<code>b</code>.   The boost is 1.0 by default.    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Prints a query to a string, with<code>field</code> as the default field    * for terms.<p>The representation used is one that is readable by    * {@link org.apache.lucene.queryParser.QueryParser QueryParser}    * (although, if the query was created by the parser, the printed    * representation may not be exactly what was parsed).    */
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
comment|/** Prints a query to a string. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/** Expert: Constructs an appropriate Weight implementation for this query.    *    *<p>Only implemented by primitive queries, which re-write to themselves.    */
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Expert: Constructs an initializes a Weight for a top-level query. */
DECL|method|weight
specifier|public
name|Weight
name|weight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|Weight
name|weight
init|=
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|float
name|sum
init|=
name|weight
operator|.
name|sumOfSquaredWeights
argument_list|()
decl_stmt|;
name|float
name|norm
init|=
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
operator|.
name|queryNorm
argument_list|(
name|sum
argument_list|)
decl_stmt|;
name|weight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
return|return
name|weight
return|;
block|}
comment|/** Expert: called to re-write queries into primitive queries. */
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
comment|/** Expert: called when re-writing queries under MultiSearcher.    *    *<p>Only implemented by derived queries, with no    * {@link #createWeight(Searcher)} implementatation.    */
DECL|method|combine
specifier|public
name|Query
name|combine
parameter_list|(
name|Query
index|[]
name|queries
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Expert: merges the clauses of a set of BooleanQuery's into a single    * BooleanQuery.    *    *<p>A utility for use by {@link #combine(Query[])} implementations.    */
DECL|method|mergeBooleanQueries
specifier|public
specifier|static
name|Query
name|mergeBooleanQueries
parameter_list|(
name|Query
index|[]
name|queries
parameter_list|)
block|{
name|HashSet
name|allClauses
init|=
operator|new
name|HashSet
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
name|queries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
index|[]
name|clauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|queries
index|[
name|i
index|]
operator|)
operator|.
name|getClauses
argument_list|()
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
name|clauses
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|allClauses
operator|.
name|add
argument_list|(
name|clauses
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|BooleanQuery
name|result
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Iterator
name|i
init|=
name|allClauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|BooleanClause
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** Expert: Returns the Similarity implementation to be used for this query.    * Subclasses may override this method to specify their own Similarity    * implementation, perhaps one that delegates through that of the Searcher.    * By default the Searcher's Similarity implementation is returned.*/
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
return|return
name|searcher
operator|.
name|getSimilarity
argument_list|()
return|;
block|}
comment|/** Returns a clone of this query. */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|Query
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Clone not supported: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

