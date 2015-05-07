begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|Fields
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
name|LeafReader
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
name|PostingsEnum
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
name|PrefixCodedTerms
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
name|PrefixCodedTerms
operator|.
name|TermIterator
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|ConstantScoreWeight
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
name|IndexSearcher
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
name|Query
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
name|Scorer
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
name|Weight
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
name|Accountable
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
name|BitDocIdSet
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
name|RamUsageEstimator
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

begin_comment
comment|/**  * Specialization for a disjunction over many terms that behaves like a  * {@link ConstantScoreQuery} over a {@link BooleanQuery} containing only  * {@link org.apache.lucene.search.BooleanClause.Occur#SHOULD} clauses.  *<p>For instance in the following example, both @{code q1} and {@code q2}  * would yield the same scores:  *<pre class="prettyprint">  * Query q1 = new TermsQuery(new Term("field", "foo"), new Term("field", "bar"));  *   * BooleanQuery bq = new BooleanQuery();  * bq.add(new TermQuery(new Term("field", "foo")), Occur.SHOULD);  * bq.add(new TermQuery(new Term("field", "bar")), Occur.SHOULD);  * Query q2 = new ConstantScoreQuery(bq);  *</pre>  *<p>This query creates a bit set and sets bits that match any of the  * wrapped terms. While this might help performance when there are many terms,  * it would be slower than a {@link BooleanQuery} when there are few terms to  * match.  *<p>NOTE: This query produces scores that are equal to its boost  */
end_comment

begin_class
DECL|class|TermsQuery
specifier|public
class|class
name|TermsQuery
extends|extends
name|Query
implements|implements
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|TermsQuery
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|termData
specifier|private
specifier|final
name|PrefixCodedTerms
name|termData
decl_stmt|;
DECL|field|termDataHashCode
specifier|private
specifier|final
name|int
name|termDataHashCode
decl_stmt|;
comment|// cached hashcode of termData
DECL|method|toTermArray
specifier|private
specifier|static
name|Term
index|[]
name|toTermArray
parameter_list|(
name|String
name|field
parameter_list|,
name|List
argument_list|<
name|BytesRef
argument_list|>
name|termBytes
parameter_list|)
block|{
name|Term
index|[]
name|array
init|=
operator|new
name|Term
index|[
name|termBytes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|t
range|:
name|termBytes
control|)
block|{
name|array
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given list. The list    * can contain duplicate terms and multiple fields.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
specifier|final
name|List
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|Term
index|[]
name|sortedTerms
init|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|sortedTerms
argument_list|)
expr_stmt|;
name|PrefixCodedTerms
operator|.
name|Builder
name|builder
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|Term
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|sortedTerms
control|)
block|{
if|if
condition|(
name|term
operator|.
name|equals
argument_list|(
name|previous
argument_list|)
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|term
expr_stmt|;
block|}
name|termData
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|termDataHashCode
operator|=
name|termData
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given {@link BytesRef} list for    * a single field.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|toTermArray
argument_list|(
name|field
argument_list|,
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given {@link BytesRef} array for    * a single field.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|BytesRef
modifier|...
name|terms
parameter_list|)
block|{
comment|// this ctor prevents unnecessary Term creations
name|this
argument_list|(
name|field
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsQuery} from the given array. The array can    * contain duplicate terms and multiple fields.    */
DECL|method|TermsQuery
specifier|public
name|TermsQuery
parameter_list|(
specifier|final
name|Term
modifier|...
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsQuery
name|that
init|=
operator|(
name|TermsQuery
operator|)
name|obj
decl_stmt|;
comment|// termData might be heavy to compare so check the hash code first
return|return
name|termDataHashCode
operator|==
name|that
operator|.
name|termDataHashCode
operator|&&
name|termData
operator|.
name|equals
argument_list|(
name|that
operator|.
name|termData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|termDataHashCode
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|TermIterator
name|iterator
init|=
name|termData
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|iterator
operator|.
name|field
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
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
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
name|termData
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
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
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
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
comment|// no-op
comment|// This query is for abuse cases when the number of terms is too high to
comment|// run efficiently as a BooleanQuery. So likewise we hide its terms in
comment|// order to protect highlighters
block|}
annotation|@
name|Override
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
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
name|String
name|lastField
init|=
literal|null
decl_stmt|;
name|Terms
name|terms
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|PostingsEnum
name|docs
init|=
literal|null
decl_stmt|;
name|TermIterator
name|iterator
init|=
name|termData
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|iterator
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
control|)
block|{
name|String
name|field
init|=
name|iterator
operator|.
name|field
argument_list|()
decl_stmt|;
comment|// comparing references is fine here
if|if
condition|(
name|field
operator|!=
name|lastField
condition|)
block|{
name|terms
operator|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termsEnum
operator|!=
literal|null
operator|&&
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
name|BitDocIdSet
name|result
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
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
name|disi
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
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
name|score
return|;
block|}
annotation|@
name|Override
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
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|disi
operator|.
name|docID
argument_list|()
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
return|return
name|disi
operator|.
name|nextDoc
argument_list|()
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
return|return
name|disi
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

