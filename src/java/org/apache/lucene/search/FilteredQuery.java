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
name|BitSet
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
comment|/**  * A query that applies a filter to the results of another query.  *  *<p>Note: the bits are retrieved from the filter each time this  * query is used in a search - use a CachingWrapperFilter to avoid  * regenerating the bits every time.  *  *<p>Created: Apr 20, 2004 8:58:29 AM  *  * @author  Tim Jones  * @since   1.4  * @version $Id$  * @see     CachingWrapperFilter  */
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
comment|/**    * Constructs a new query which applies a filter to the results of the original query.    * Filter.bits() will be called every time this query is used in a search.    * @param query  Query to be filtered, cannot be<code>null</code>.    * @param filter Filter to apply to query results, cannot be<code>null</code>.    */
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
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
specifier|final
name|Searcher
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
specifier|final
name|Similarity
name|similarity
init|=
name|query
operator|.
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|()
block|{
comment|// pass these methods through to enclosed query's weight
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|weight
operator|.
name|getValue
argument_list|()
return|;
block|}
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|sumOfSquaredWeights
argument_list|()
return|;
block|}
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|v
parameter_list|)
block|{
name|weight
operator|.
name|normalize
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
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
name|Filter
name|f
init|=
name|FilteredQuery
operator|.
name|this
operator|.
name|filter
decl_stmt|;
name|BitSet
name|matches
init|=
name|f
operator|.
name|bits
argument_list|(
name|ir
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
return|return
name|inner
return|;
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
comment|// return this query
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
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|bitset
init|=
name|filter
operator|.
name|bits
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
return|return
operator|new
name|Scorer
argument_list|(
name|similarity
argument_list|)
block|{
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
do|do
block|{
if|if
condition|(
operator|!
name|scorer
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
do|while
condition|(
operator|!
name|bitset
operator|.
name|get
argument_list|(
name|scorer
operator|.
name|doc
argument_list|()
argument_list|)
condition|)
do|;
comment|/* When skipTo() is allowed on scorer it should be used here              * in combination with bitset.nextSetBit(...)              * See the while loop in skipTo() below.              */
return|return
literal|true
return|;
block|}
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|doc
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|scorer
operator|.
name|skipTo
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
condition|(
operator|!
name|bitset
operator|.
name|get
argument_list|(
name|scorer
operator|.
name|doc
argument_list|()
argument_list|)
condition|)
block|{
name|int
name|nextFiltered
init|=
name|bitset
operator|.
name|nextSetBit
argument_list|(
name|scorer
operator|.
name|doc
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextFiltered
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|scorer
operator|.
name|skipTo
argument_list|(
name|nextFiltered
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
comment|// add an explanation about whether the document was filtered
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|exp
init|=
name|scorer
operator|.
name|explain
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitset
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
name|exp
operator|.
name|setDescription
argument_list|(
literal|"allowed by filter: "
operator|+
name|exp
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|exp
operator|.
name|setDescription
argument_list|(
literal|"removed by filter: "
operator|+
name|exp
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/** Rewrites the wrapped query. */
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
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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

