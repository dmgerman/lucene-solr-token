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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FilteredTermsEnum
import|;
end_import

begin_comment
comment|// javadocs
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
name|SingleTermsEnum
import|;
end_import

begin_comment
comment|// javadocs
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
name|util
operator|.
name|AttributeSource
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
name|TermContext
import|;
end_import

begin_comment
comment|/**  * An abstract {@link Query} that matches documents  * containing a subset of terms provided by a {@link  * FilteredTermsEnum} enumeration.  *  *<p>This query cannot be used directly; you must subclass  * it and define {@link #getTermsEnum(Terms,AttributeSource)} to provide a {@link  * FilteredTermsEnum} that iterates through the terms to be  * matched.  *  *<p><b>NOTE</b>: if {@link #setRewriteMethod} is either  * {@link #CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE} or {@link  * #SCORING_BOOLEAN_QUERY_REWRITE}, you may encounter a  * {@link BooleanQuery.TooManyClauses} exception during  * searching, which happens when the number of terms to be  * searched exceeds {@link  * BooleanQuery#getMaxClauseCount()}.  Setting {@link  * #setRewriteMethod} to {@link #CONSTANT_SCORE_FILTER_REWRITE}  * prevents this.  *  *<p>The recommended rewrite method is {@link  * #CONSTANT_SCORE_AUTO_REWRITE_DEFAULT}: it doesn't spend CPU  * computing unhelpful scores, and it tries to pick the most  * performant rewrite method given the query. If you  * need scoring (like {@link FuzzyQuery}, use  * {@link TopTermsScoringBooleanQueryRewrite} which uses  * a priority queue to only collect competitive terms  * and not hit this limitation.  *  * Note that org.apache.lucene.queryparser.classic.QueryParser produces  * MultiTermQueries using {@link  * #CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} by default.  */
end_comment

begin_class
DECL|class|MultiTermQuery
specifier|public
specifier|abstract
class|class
name|MultiTermQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|rewriteMethod
specifier|protected
name|RewriteMethod
name|rewriteMethod
init|=
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
decl_stmt|;
comment|/** Abstract class that defines how the query is rewritten. */
DECL|class|RewriteMethod
specifier|public
specifier|static
specifier|abstract
class|class
name|RewriteMethod
block|{
DECL|method|rewrite
specifier|public
specifier|abstract
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the {@link MultiTermQuery}s {@link TermsEnum}      * @see MultiTermQuery#getTermsEnum(Terms, AttributeSource)      */
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|MultiTermQuery
name|query
parameter_list|,
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|,
name|atts
argument_list|)
return|;
comment|// allow RewriteMethod subclasses to pull a TermsEnum from the MTQ
block|}
block|}
comment|/** A rewrite method that first creates a private Filter,    *  by visiting each term in sequence and marking all docs    *  for that term.  Matching documents are assigned a    *  constant score equal to the query's boost.    *     *<p> This method is faster than the BooleanQuery    *  rewrite methods when the number of matched terms or    *  matched documents is non-trivial. Also, it will never    *  hit an errant {@link BooleanQuery.TooManyClauses}    *  exception.    *    *  @see #setRewriteMethod */
DECL|field|CONSTANT_SCORE_FILTER_REWRITE
specifier|public
specifier|static
specifier|final
name|RewriteMethod
name|CONSTANT_SCORE_FILTER_REWRITE
init|=
operator|new
name|RewriteMethod
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
block|{
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|MultiTermQueryWrapperFilter
argument_list|<
name|MultiTermQuery
argument_list|>
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
comment|/** A rewrite method that first translates each term into    *  {@link BooleanClause.Occur#SHOULD} clause in a    *  BooleanQuery, and keeps the scores as computed by the    *  query.  Note that typically such scores are    *  meaningless to the user, and require non-trivial CPU    *  to compute, so it's almost always better to use {@link    *  #CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} instead.    *    *<p><b>NOTE</b>: This rewrite method will hit {@link    *  BooleanQuery.TooManyClauses} if the number of terms    *  exceeds {@link BooleanQuery#getMaxClauseCount}.    *    *  @see #setRewriteMethod */
DECL|field|SCORING_BOOLEAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|SCORING_BOOLEAN_QUERY_REWRITE
init|=
name|ScoringRewrite
operator|.
name|SCORING_BOOLEAN_QUERY_REWRITE
decl_stmt|;
comment|/** Like {@link #SCORING_BOOLEAN_QUERY_REWRITE} except    *  scores are not computed.  Instead, each matching    *  document receives a constant score equal to the    *  query's boost.    *     *<p><b>NOTE</b>: This rewrite method will hit {@link    *  BooleanQuery.TooManyClauses} if the number of terms    *  exceeds {@link BooleanQuery#getMaxClauseCount}.    *    *  @see #setRewriteMethod */
DECL|field|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
init|=
name|ScoringRewrite
operator|.
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
decl_stmt|;
comment|/**    * A rewrite method that first translates each term into    * {@link BooleanClause.Occur#SHOULD} clause in a BooleanQuery, and keeps the    * scores as computed by the query.    *     *<p>    * This rewrite method only uses the top scoring terms so it will not overflow    * the boolean max clause count. It is the default rewrite method for    * {@link FuzzyQuery}.    *     * @see #setRewriteMethod    */
DECL|class|TopTermsScoringBooleanQueryRewrite
specifier|public
specifier|static
specifier|final
class|class
name|TopTermsScoringBooleanQueryRewrite
extends|extends
name|TopTermsRewrite
argument_list|<
name|BooleanQuery
argument_list|>
block|{
comment|/**       * Create a TopTermsScoringBooleanQueryRewrite for       * at most<code>size</code> terms.      *<p>      * NOTE: if {@link BooleanQuery#getMaxClauseCount} is smaller than       *<code>size</code>, then it will be used instead.       */
DECL|method|TopTermsScoringBooleanQueryRewrite
specifier|public
name|TopTermsScoringBooleanQueryRewrite
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxSize
specifier|protected
name|int
name|getMaxSize
parameter_list|()
block|{
return|return
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTopLevelQuery
specifier|protected
name|BooleanQuery
name|getTopLevelQuery
parameter_list|()
block|{
return|return
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addClause
specifier|protected
name|void
name|addClause
parameter_list|(
name|BooleanQuery
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docCount
parameter_list|,
name|float
name|boost
parameter_list|,
name|TermContext
name|states
parameter_list|)
block|{
specifier|final
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|,
name|states
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|topLevel
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A rewrite method that first translates each term into    * {@link BooleanClause.Occur#SHOULD} clause in a BooleanQuery, but the scores    * are only computed as the boost.    *<p>    * This rewrite method only uses the top scoring terms so it will not overflow    * the boolean max clause count.    *     * @see #setRewriteMethod    */
DECL|class|TopTermsBoostOnlyBooleanQueryRewrite
specifier|public
specifier|static
specifier|final
class|class
name|TopTermsBoostOnlyBooleanQueryRewrite
extends|extends
name|TopTermsRewrite
argument_list|<
name|BooleanQuery
argument_list|>
block|{
comment|/**       * Create a TopTermsBoostOnlyBooleanQueryRewrite for       * at most<code>size</code> terms.      *<p>      * NOTE: if {@link BooleanQuery#getMaxClauseCount} is smaller than       *<code>size</code>, then it will be used instead.       */
DECL|method|TopTermsBoostOnlyBooleanQueryRewrite
specifier|public
name|TopTermsBoostOnlyBooleanQueryRewrite
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxSize
specifier|protected
name|int
name|getMaxSize
parameter_list|()
block|{
return|return
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTopLevelQuery
specifier|protected
name|BooleanQuery
name|getTopLevelQuery
parameter_list|()
block|{
return|return
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addClause
specifier|protected
name|void
name|addClause
parameter_list|(
name|BooleanQuery
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docFreq
parameter_list|,
name|float
name|boost
parameter_list|,
name|TermContext
name|states
parameter_list|)
block|{
specifier|final
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|,
name|states
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|topLevel
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** A rewrite method that tries to pick the best    *  constant-score rewrite method based on term and    *  document counts from the query.  If both the number of    *  terms and documents is small enough, then {@link    *  #CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE} is used.    *  Otherwise, {@link #CONSTANT_SCORE_FILTER_REWRITE} is    *  used.    */
DECL|class|ConstantScoreAutoRewrite
specifier|public
specifier|static
class|class
name|ConstantScoreAutoRewrite
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ConstantScoreAutoRewrite
block|{}
comment|/** Read-only default instance of {@link    *  ConstantScoreAutoRewrite}, with {@link    *  ConstantScoreAutoRewrite#setTermCountCutoff} set to    *  {@link    *  ConstantScoreAutoRewrite#DEFAULT_TERM_COUNT_CUTOFF}    *  and {@link    *  ConstantScoreAutoRewrite#setDocCountPercent} set to    *  {@link    *  ConstantScoreAutoRewrite#DEFAULT_DOC_COUNT_PERCENT}.    *  Note that you cannot alter the configuration of this    *  instance; you'll need to create a private instance    *  instead. */
DECL|field|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|CONSTANT_SCORE_AUTO_REWRITE_DEFAULT
init|=
operator|new
name|ConstantScoreAutoRewrite
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setTermCountCutoff
parameter_list|(
name|int
name|count
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Please create a private instance"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocCountPercent
parameter_list|(
name|double
name|percent
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Please create a private instance"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
comment|/**    * Constructs a query matching terms that cannot be represented with a single    * Term.    */
DECL|method|MultiTermQuery
specifier|public
name|MultiTermQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
assert|assert
name|field
operator|!=
literal|null
assert|;
block|}
comment|/** Returns the field name for this query */
DECL|method|getField
specifier|public
specifier|final
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Construct the enumeration to be used, expanding the    *  pattern term.  This method should only be called if    *  the field exists (ie, implementations can assume the    *  field does exist).  This method should not return null    *  (should instead return {@link TermsEnum#EMPTY} if no    *  terms match).  The TermsEnum must already be    *  positioned to the first matching term.    * The given {@link AttributeSource} is passed by the {@link RewriteMethod} to    * provide attributes, the rewrite method uses to inform about e.g. maximum competitive boosts.    * This is currently only used by {@link TopTermsRewrite}    */
DECL|method|getTermsEnum
specifier|protected
specifier|abstract
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Convenience method, if no attributes are needed:    * This simply passes empty attributes and is equal to:    *<code>getTermsEnum(terms, new AttributeSource())</code>    */
DECL|method|getTermsEnum
specifier|protected
specifier|final
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTermsEnum
argument_list|(
name|terms
argument_list|,
operator|new
name|AttributeSource
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * To rewrite to a simpler form, instead return a simpler    * enum from {@link #getTermsEnum(Terms, AttributeSource)}.  For example,    * to rewrite to a single term, return a {@link SingleTermsEnum}    */
annotation|@
name|Override
DECL|method|rewrite
specifier|public
specifier|final
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
name|rewriteMethod
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**    * @see #setRewriteMethod    */
DECL|method|getRewriteMethod
specifier|public
name|RewriteMethod
name|getRewriteMethod
parameter_list|()
block|{
return|return
name|rewriteMethod
return|;
block|}
comment|/**    * Sets the rewrite method to be used when executing the    * query.  You can use one of the four core methods, or    * implement your own subclass of {@link RewriteMethod}. */
DECL|method|setRewriteMethod
specifier|public
name|void
name|setRewriteMethod
parameter_list|(
name|RewriteMethod
name|method
parameter_list|)
block|{
name|rewriteMethod
operator|=
name|method
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|rewriteMethod
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MultiTermQuery
name|other
init|=
operator|(
name|MultiTermQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|getBoost
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|rewriteMethod
operator|.
name|equals
argument_list|(
name|other
operator|.
name|rewriteMethod
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
name|other
operator|.
name|field
operator|==
literal|null
condition|?
name|field
operator|==
literal|null
else|:
name|other
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

