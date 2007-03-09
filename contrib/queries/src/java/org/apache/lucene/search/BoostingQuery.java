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
name|DefaultSimilarity
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
name|Searcher
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
name|Similarity
import|;
end_import

begin_comment
comment|/**  * The BoostingQuery class can be used to effectively demote results that match a given query.   * Unlike the "NOT" clause, this still selects documents that contain undesirable terms,   * but reduces their overall score:  *  *     Query balancedQuery = new BoostingQuery(positiveQuery, negativeQuery, 0.01f);  * In this scenario the positiveQuery contains the mandatory, desirable criteria which is used to   * select all matching documents, and the negativeQuery contains the undesirable elements which   * are simply used to lessen the scores. Documents that match the negativeQuery have their score   * multiplied by the supplied "boost" parameter, so this should be less than 1 to achieve a   * demoting effect  *   * This code was originally made available here: [WWW] http://marc.theaimsgroup.com/?l=lucene-user&m=108058407130459&w=2  * and is documented here: http://wiki.apache.org/lucene-java/CommunityContributions  */
end_comment

begin_class
DECL|class|BoostingQuery
specifier|public
class|class
name|BoostingQuery
extends|extends
name|Query
block|{
DECL|field|boost
specifier|private
name|float
name|boost
decl_stmt|;
comment|// the amount to boost by
DECL|field|match
specifier|private
name|Query
name|match
decl_stmt|;
comment|// query to match
DECL|field|context
specifier|private
name|Query
name|context
decl_stmt|;
comment|// boost when matches too
DECL|method|BoostingQuery
specifier|public
name|BoostingQuery
parameter_list|(
name|Query
name|match
parameter_list|,
name|Query
name|context
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
name|this
operator|.
name|context
operator|=
operator|(
name|Query
operator|)
name|context
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// clone before boost
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|context
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
comment|// ignore context-only matches
block|}
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
name|BooleanQuery
name|result
init|=
operator|new
name|BooleanQuery
argument_list|()
block|{
specifier|public
name|Similarity
name|getSimilarity
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
return|return
operator|new
name|DefaultSimilarity
argument_list|()
block|{
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|max
parameter_list|)
block|{
switch|switch
condition|(
name|overlap
condition|)
block|{
case|case
literal|1
case|:
comment|// matched only one clause
return|return
literal|1.0f
return|;
comment|// use the score as-is
case|case
literal|2
case|:
comment|// matched both clauses
return|return
name|boost
return|;
comment|// multiply by boost
default|default:
return|return
literal|0.0f
return|;
block|}
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|match
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|"/"
operator|+
name|context
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class

end_unit

