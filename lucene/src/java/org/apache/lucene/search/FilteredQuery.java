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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IndexReader
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
name|ToStringUtils
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

begin_comment
comment|/**  * A query that applies a filter to the results of another query.  *  *<p>Note: the bits are retrieved from the filter each time this  * query is used in a search - use a CachingWrapperFilter to avoid  * regenerating the bits every time.  *  *<p>Created: Apr 20, 2004 8:58:29 AM  *  * @since   1.4  * @see     CachingWrapperFilter  */
end_comment

begin_class
DECL|class|FilteredQuery
specifier|public
class|class
name|FilteredQuery
extends|extends
name|Query
block|{
DECL|field|query
name|Query
name|query
decl_stmt|;
DECL|field|filter
name|Filter
name|filter
decl_stmt|;
comment|/**    * Constructs a new query which applies a filter to the results of the original query.    * Filter.getDocIdSet() will be called every time this query is used in a search.    * @param query  Query to be filtered, cannot be<code>null</code>.    * @param filter Filter to apply to query results, cannot be<code>null</code>.    */
DECL|method|FilteredQuery
specifier|public
name|FilteredQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * Returns a Weight that applies the filter to the enclosed query's Weight.    * This is accomplished by overriding the Scorer returned by the Weight.    */
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
specifier|final
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
return|return
operator|new
name|Weight
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|getValueForNormalization
argument_list|()
operator|*
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|weight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|ir
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|inner
init|=
name|weight
operator|.
name|explain
argument_list|(
name|ir
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|Explanation
name|preBoost
init|=
name|inner
decl_stmt|;
name|inner
operator|=
operator|new
name|Explanation
argument_list|(
name|inner
operator|.
name|getValue
argument_list|()
operator|*
name|getBoost
argument_list|()
argument_list|,
literal|"product of:"
argument_list|)
expr_stmt|;
name|inner
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
name|inner
operator|.
name|addDetail
argument_list|(
name|preBoost
argument_list|)
expr_stmt|;
block|}
name|Filter
name|f
init|=
name|FilteredQuery
operator|.
name|this
operator|.
name|filter
decl_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|f
operator|.
name|getDocIdSet
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|docIdSetIterator
init|=
name|docIdSet
operator|==
literal|null
condition|?
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
else|:
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|docIdSetIterator
operator|==
literal|null
condition|)
block|{
name|docIdSetIterator
operator|=
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|i
argument_list|)
operator|==
name|i
condition|)
block|{
return|return
name|inner
return|;
block|}
else|else
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"failure to match filter: "
operator|+
name|f
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|inner
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|// return this query
annotation|@
name|Override
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|FilteredQuery
operator|.
name|this
return|;
block|}
comment|// return a filtering scorer
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we will advance() the subscorer
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|docIdSetIterator
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|docIdSetIterator
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Scorer
argument_list|(
name|this
argument_list|)
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|advanceToCommon
parameter_list|(
name|int
name|scorerDoc
parameter_list|,
name|int
name|disiDoc
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|scorerDoc
operator|!=
name|disiDoc
condition|)
block|{
if|if
condition|(
name|scorerDoc
operator|<
name|disiDoc
condition|)
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|disiDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|disiDoc
operator|=
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|scorerDoc
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
name|int
name|scorerDoc
decl_stmt|,
name|disiDoc
decl_stmt|;
return|return
name|doc
operator|=
operator|(
name|disiDoc
operator|=
name|docIdSetIterator
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
operator|&&
operator|(
name|scorerDoc
operator|=
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
operator|&&
name|advanceToCommon
argument_list|(
name|scorerDoc
argument_list|,
name|disiDoc
argument_list|)
operator|!=
name|NO_MORE_DOCS
condition|?
name|scorer
operator|.
name|docID
argument_list|()
else|:
name|NO_MORE_DOCS
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
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|disiDoc
decl_stmt|,
name|scorerDoc
decl_stmt|;
return|return
name|doc
operator|=
operator|(
name|disiDoc
operator|=
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|)
operator|!=
name|NO_MORE_DOCS
operator|&&
operator|(
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|disiDoc
argument_list|)
operator|)
operator|!=
name|NO_MORE_DOCS
operator|&&
name|advanceToCommon
argument_list|(
name|scorerDoc
argument_list|,
name|disiDoc
argument_list|)
operator|!=
name|NO_MORE_DOCS
condition|?
name|scorer
operator|.
name|docID
argument_list|()
else|:
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getBoost
argument_list|()
operator|*
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/** Rewrites the wrapped query. */
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
name|Query
name|rewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|query
condition|)
block|{
name|FilteredQuery
name|clone
init|=
operator|(
name|FilteredQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|query
operator|=
name|rewritten
expr_stmt|;
return|return
name|clone
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
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
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|getQuery
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
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
name|s
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"filtered("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")->"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
name|o
operator|instanceof
name|FilteredQuery
condition|)
block|{
name|FilteredQuery
name|fq
init|=
operator|(
name|FilteredQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|query
operator|.
name|equals
argument_list|(
name|fq
operator|.
name|query
argument_list|)
operator|&&
name|filter
operator|.
name|equals
argument_list|(
name|fq
operator|.
name|filter
argument_list|)
operator|&&
name|getBoost
argument_list|()
operator|==
name|fq
operator|.
name|getBoost
argument_list|()
operator|)
return|;
block|}
return|return
literal|false
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
return|return
name|query
operator|.
name|hashCode
argument_list|()
operator|^
name|filter
operator|.
name|hashCode
argument_list|()
operator|+
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

