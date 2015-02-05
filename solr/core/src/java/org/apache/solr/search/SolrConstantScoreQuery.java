begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|Set
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A query that wraps a filter and simply returns a constant score equal to the  * query boost for every document in the filter.   This Solr extension also supports  * weighting of a SolrFilter.  *  * Experimental and subject to change.  */
end_comment

begin_class
DECL|class|SolrConstantScoreQuery
specifier|public
class|class
name|SolrConstantScoreQuery
extends|extends
name|ConstantScoreQuery
implements|implements
name|ExtendedQuery
block|{
DECL|field|cache
name|boolean
name|cache
init|=
literal|true
decl_stmt|;
comment|// cache by default
DECL|field|cost
name|int
name|cost
decl_stmt|;
DECL|method|SolrConstantScoreQuery
specifier|public
name|SolrConstantScoreQuery
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|super
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the encapsulated filter */
annotation|@
name|Override
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
annotation|@
name|Override
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|boolean
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCache
specifier|public
name|boolean
name|getCache
parameter_list|()
block|{
return|return
name|cache
return|;
block|}
annotation|@
name|Override
DECL|method|setCacheSep
specifier|public
name|void
name|setCacheSep
parameter_list|(
name|boolean
name|cacheSep
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getCacheSep
specifier|public
name|boolean
name|getCacheSep
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setCost
specifier|public
name|void
name|setCost
parameter_list|(
name|int
name|cost
parameter_list|)
block|{
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
name|terms
parameter_list|)
block|{
comment|// OK to not add any terms when used for MultiSearcher,
comment|// but may not be OK for highlighting
block|}
DECL|class|ConstantWeight
specifier|protected
class|class
name|ConstantWeight
extends|extends
name|Weight
block|{
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
decl_stmt|;
DECL|field|context
specifier|private
name|Map
name|context
decl_stmt|;
DECL|method|ConstantWeight
specifier|public
name|ConstantWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|context
operator|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|instanceof
name|SolrFilter
condition|)
operator|(
operator|(
name|SolrFilter
operator|)
name|filter
operator|)
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|SolrConstantScoreQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|norm
operator|*
name|topLevelBoost
expr_stmt|;
name|queryWeight
operator|*=
name|this
operator|.
name|queryNorm
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScorer
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|queryWeight
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|ConstantScorer
name|cs
init|=
operator|new
name|ConstantScorer
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|queryWeight
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|exists
init|=
name|cs
operator|.
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
decl_stmt|;
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
name|result
operator|.
name|setDescription
argument_list|(
literal|"ConstantScoreQuery("
operator|+
name|filter
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|queryWeight
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|setDescription
argument_list|(
literal|"ConstantScoreQuery("
operator|+
name|filter
operator|+
literal|") doesn't match id "
operator|+
name|doc
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
DECL|class|ConstantScorer
specifier|protected
class|class
name|ConstantScorer
extends|extends
name|Scorer
block|{
DECL|field|docIdSetIterator
specifier|final
name|DocIdSetIterator
name|docIdSetIterator
decl_stmt|;
DECL|field|theScore
specifier|final
name|float
name|theScore
decl_stmt|;
DECL|field|acceptDocs
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|ConstantScorer
specifier|public
name|ConstantScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|ConstantWeight
name|w
parameter_list|,
name|float
name|theScore
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|theScore
operator|=
name|theScore
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|instanceof
name|SolrFilter
condition|?
operator|(
operator|(
name|SolrFilter
operator|)
name|filter
operator|)
operator|.
name|getDocIdSet
argument_list|(
name|w
operator|.
name|context
argument_list|,
name|context
argument_list|,
name|acceptDocs
argument_list|)
else|:
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
name|docIdSetIterator
operator|=
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|DocIdSetIterator
name|iter
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
block|{
name|docIdSetIterator
operator|=
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|docIdSetIterator
operator|=
name|iter
expr_stmt|;
block|}
block|}
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
return|return
name|docIdSetIterator
operator|.
name|nextDoc
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
return|return
name|docIdSetIterator
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|theScore
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
literal|1
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
return|return
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
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
return|return
name|docIdSetIterator
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|SolrConstantScoreQuery
operator|.
name|ConstantWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: remove this if ConstantScoreQuery.createWeight adds IOException
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|ExtendedQueryBase
operator|.
name|getOptionsString
argument_list|(
name|this
argument_list|)
operator|+
literal|"ConstantScore("
operator|+
name|filter
operator|.
name|toString
argument_list|()
operator|+
operator|(
name|getBoost
argument_list|()
operator|==
literal|1.0
condition|?
literal|")"
else|:
literal|"^"
operator|+
name|getBoost
argument_list|()
operator|)
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SolrConstantScoreQuery
operator|)
condition|)
return|return
literal|false
return|;
name|SolrConstantScoreQuery
name|other
init|=
operator|(
name|SolrConstantScoreQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|filter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|filter
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Simple add is OK since no existing filter hashcode has a float component.
return|return
name|filter
operator|.
name|hashCode
argument_list|()
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

