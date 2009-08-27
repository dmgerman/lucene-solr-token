begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
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
name|memory
operator|.
name|MemoryIndex
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
name|spans
operator|.
name|SpanQuery
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
name|StringHelper
import|;
end_import

begin_comment
comment|/**  * {@link Scorer} implementation which scores text fragments by the number of  * unique query terms found. This class converts appropriate {@link Query}s to  * {@link SpanQuery}s and attempts to score only those terms that participated in  * generating the 'hit' on the document.  */
end_comment

begin_class
DECL|class|QueryScorer
specifier|public
class|class
name|QueryScorer
implements|implements
name|Scorer
block|{
DECL|field|totalScore
specifier|private
name|float
name|totalScore
decl_stmt|;
DECL|field|foundTerms
specifier|private
name|Set
name|foundTerms
decl_stmt|;
DECL|field|fieldWeightedSpanTerms
specifier|private
name|Map
name|fieldWeightedSpanTerms
decl_stmt|;
DECL|field|maxTermWeight
specifier|private
name|float
name|maxTermWeight
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|defaultField
specifier|private
name|String
name|defaultField
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|expandMultiTermQuery
specifier|private
name|boolean
name|expandMultiTermQuery
init|=
literal|true
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|skipInitExtractor
specifier|private
name|boolean
name|skipInitExtractor
decl_stmt|;
comment|/**    * @param query Query to use for highlighting    */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|init
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param query Query to use for highlighting    * @param field Field to highlight - pass null to ignore fields    */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|init
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param query Query to use for highlighting    * @param field Field to highlight - pass null to ignore fields    * @param reader {@link IndexReader} to use for quasi tf/idf scoring    */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|init
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
name|reader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param query to use for highlighting    * @param reader {@link IndexReader} to use for quasi tf/idf scoring    * @param field to highlight - pass null to ignore fields    * @param defaultField    */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|this
operator|.
name|defaultField
operator|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|defaultField
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
name|reader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param defaultField - The default field for queries with the field name unspecified    */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|this
operator|.
name|defaultField
operator|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|defaultField
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param weightedTerms an array of pre-created {@link WeightedSpanTerm}s    */
DECL|method|QueryScorer
specifier|public
name|QueryScorer
parameter_list|(
name|WeightedSpanTerm
index|[]
name|weightedTerms
parameter_list|)
block|{
name|this
operator|.
name|fieldWeightedSpanTerms
operator|=
operator|new
name|HashMap
argument_list|(
name|weightedTerms
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|weightedTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|WeightedSpanTerm
name|existingTerm
init|=
operator|(
name|WeightedSpanTerm
operator|)
name|fieldWeightedSpanTerms
operator|.
name|get
argument_list|(
name|weightedTerms
index|[
name|i
index|]
operator|.
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|existingTerm
operator|==
literal|null
operator|)
operator|||
operator|(
name|existingTerm
operator|.
name|weight
operator|<
name|weightedTerms
index|[
name|i
index|]
operator|.
name|weight
operator|)
condition|)
block|{
comment|// if a term is defined more than once, always use the highest
comment|// scoring weight
name|fieldWeightedSpanTerms
operator|.
name|put
argument_list|(
name|weightedTerms
index|[
name|i
index|]
operator|.
name|term
argument_list|,
name|weightedTerms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|maxTermWeight
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxTermWeight
argument_list|,
name|weightedTerms
index|[
name|i
index|]
operator|.
name|getWeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|skipInitExtractor
operator|=
literal|true
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.search.highlight.Scorer#getFragmentScore()    */
DECL|method|getFragmentScore
specifier|public
name|float
name|getFragmentScore
parameter_list|()
block|{
return|return
name|totalScore
return|;
block|}
comment|/**    *    * @return The highest weighted term (useful for passing to    *         GradientFormatter to set top end of coloring scale).    */
DECL|method|getMaxTermWeight
specifier|public
name|float
name|getMaxTermWeight
parameter_list|()
block|{
return|return
name|maxTermWeight
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.search.highlight.Scorer#getTokenScore(org.apache.lucene.analysis.Token,    *      int)    */
DECL|method|getTokenScore
specifier|public
name|float
name|getTokenScore
parameter_list|()
block|{
name|position
operator|+=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|String
name|termText
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
name|WeightedSpanTerm
name|weightedSpanTerm
decl_stmt|;
if|if
condition|(
operator|(
name|weightedSpanTerm
operator|=
operator|(
name|WeightedSpanTerm
operator|)
name|fieldWeightedSpanTerms
operator|.
name|get
argument_list|(
name|termText
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|weightedSpanTerm
operator|.
name|positionSensitive
operator|&&
operator|!
name|weightedSpanTerm
operator|.
name|checkPosition
argument_list|(
name|position
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|float
name|score
init|=
name|weightedSpanTerm
operator|.
name|getWeight
argument_list|()
decl_stmt|;
comment|// found a query term - is it unique in this doc?
if|if
condition|(
operator|!
name|foundTerms
operator|.
name|contains
argument_list|(
name|termText
argument_list|)
condition|)
block|{
name|totalScore
operator|+=
name|score
expr_stmt|;
name|foundTerms
operator|.
name|add
argument_list|(
name|termText
argument_list|)
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.Scorer#init(org.apache.lucene.analysis.TokenStream)    */
DECL|method|init
specifier|public
name|TokenStream
name|init
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
throws|throws
name|IOException
block|{
name|position
operator|=
operator|-
literal|1
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|skipInitExtractor
condition|)
block|{
if|if
condition|(
name|fieldWeightedSpanTerms
operator|!=
literal|null
condition|)
block|{
name|fieldWeightedSpanTerms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|initExtractor
argument_list|(
name|tokenStream
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Retrieve the {@link WeightedSpanTerm} for the specified token. Useful for passing    * Span information to a {@link Fragmenter}.    *    * @param token to get {@link WeightedSpanTerm} for    * @return WeightedSpanTerm for token    */
DECL|method|getWeightedSpanTerm
specifier|public
name|WeightedSpanTerm
name|getWeightedSpanTerm
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
operator|(
name|WeightedSpanTerm
operator|)
name|fieldWeightedSpanTerms
operator|.
name|get
argument_list|(
name|token
argument_list|)
return|;
block|}
comment|/**    */
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|field
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|expandMultiTermQuery
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|expandMultiTermQuery
operator|=
name|expandMultiTermQuery
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|initExtractor
specifier|private
name|TokenStream
name|initExtractor
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
throws|throws
name|IOException
block|{
name|WeightedSpanTermExtractor
name|qse
init|=
name|defaultField
operator|==
literal|null
condition|?
operator|new
name|WeightedSpanTermExtractor
argument_list|()
else|:
operator|new
name|WeightedSpanTermExtractor
argument_list|(
name|defaultField
argument_list|)
decl_stmt|;
name|qse
operator|.
name|setExpandMultiTermQuery
argument_list|(
name|expandMultiTermQuery
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|fieldWeightedSpanTerms
operator|=
name|qse
operator|.
name|getWeightedSpanTerms
argument_list|(
name|query
argument_list|,
name|tokenStream
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|fieldWeightedSpanTerms
operator|=
name|qse
operator|.
name|getWeightedSpanTermsWithScores
argument_list|(
name|query
argument_list|,
name|tokenStream
argument_list|,
name|field
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|qse
operator|.
name|isCachedTokenStream
argument_list|()
condition|)
block|{
return|return
name|qse
operator|.
name|getTokenStream
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.search.highlight.Scorer#startFragment(org.apache.lucene.search.highlight.TextFragment)    */
DECL|method|startFragment
specifier|public
name|void
name|startFragment
parameter_list|(
name|TextFragment
name|newFragment
parameter_list|)
block|{
name|foundTerms
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|totalScore
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * @return true if multi-term queries should be expanded    */
DECL|method|isExpandMultiTermQuery
specifier|public
name|boolean
name|isExpandMultiTermQuery
parameter_list|()
block|{
return|return
name|expandMultiTermQuery
return|;
block|}
comment|/**    * Controls whether or not multi-term queries are expanded    * against a {@link MemoryIndex} {@link IndexReader}.    *     * @param expandMultiTermQuery true if multi-term queries should be expanded    */
DECL|method|setExpandMultiTermQuery
specifier|public
name|void
name|setExpandMultiTermQuery
parameter_list|(
name|boolean
name|expandMultiTermQuery
parameter_list|)
block|{
name|this
operator|.
name|expandMultiTermQuery
operator|=
name|expandMultiTermQuery
expr_stmt|;
block|}
block|}
end_class

end_unit

