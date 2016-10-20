begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldInvertState
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
name|NumericDocValues
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
name|CollectionStatistics
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
name|Explanation
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
name|TermStatistics
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
name|SmallFloat
import|;
end_import

begin_comment
comment|/**  * A subclass of {@code Similarity} that provides a simplified API for its  * descendants. Subclasses are only required to implement the {@link #score}  * and {@link #toString()} methods. Implementing  * {@link #explain(List, BasicStats, int, float, float)} is optional,  * inasmuch as SimilarityBase already provides a basic explanation of the score  * and the term frequency. However, implementers of a subclass are encouraged to  * include as much detail about the scoring method as possible.  *<p>  * Note: multi-word queries such as phrase queries are scored in a different way  * than Lucene's default ranking algorithm: whereas it "fakes" an IDF value for  * the phrase as a whole (since it does not know it), this class instead scores  * phrases as a summation of the individual term scores.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimilarityBase
specifier|public
specifier|abstract
class|class
name|SimilarityBase
extends|extends
name|Similarity
block|{
comment|/** For {@link #log2(double)}. Precomputed for efficiency reasons. */
DECL|field|LOG_2
specifier|private
specifier|static
specifier|final
name|double
name|LOG_2
init|=
name|Math
operator|.
name|log
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|/**     * True if overlap tokens (tokens with a position of increment of zero) are    * discounted from the document's length.    */
DECL|field|discountOverlaps
specifier|protected
name|boolean
name|discountOverlaps
init|=
literal|true
decl_stmt|;
comment|/**    * Sole constructor. (For invocation by subclass     * constructors, typically implicit.)    */
DECL|method|SimilarityBase
specifier|public
name|SimilarityBase
parameter_list|()
block|{}
comment|/** Determines whether overlap tokens (Tokens with    *  0 position increment) are ignored when computing    *  norm.  By default this is true, meaning overlap    *  tokens do not count when computing norms.    *    *  @lucene.experimental    *    *  @see #computeNorm    */
DECL|method|setDiscountOverlaps
specifier|public
name|void
name|setDiscountOverlaps
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|discountOverlaps
operator|=
name|v
expr_stmt|;
block|}
comment|/**    * Returns true if overlap tokens are discounted from the document's length.     * @see #setDiscountOverlaps     */
DECL|method|getDiscountOverlaps
specifier|public
name|boolean
name|getDiscountOverlaps
parameter_list|()
block|{
return|return
name|discountOverlaps
return|;
block|}
annotation|@
name|Override
DECL|method|computeWeight
specifier|public
specifier|final
name|SimWeight
name|computeWeight
parameter_list|(
name|float
name|boost
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
name|BasicStats
name|stats
index|[]
init|=
operator|new
name|BasicStats
index|[
name|termStats
operator|.
name|length
index|]
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
name|termStats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stats
index|[
name|i
index|]
operator|=
name|newStats
argument_list|(
name|collectionStats
operator|.
name|field
argument_list|()
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|fillBasicStats
argument_list|(
name|stats
index|[
name|i
index|]
argument_list|,
name|collectionStats
argument_list|,
name|termStats
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
operator|.
name|length
operator|==
literal|1
condition|?
name|stats
index|[
literal|0
index|]
else|:
operator|new
name|MultiSimilarity
operator|.
name|MultiStats
argument_list|(
name|stats
argument_list|)
return|;
block|}
comment|/** Factory method to return a custom stats object */
DECL|method|newStats
specifier|protected
name|BasicStats
name|newStats
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
operator|new
name|BasicStats
argument_list|(
name|field
argument_list|,
name|boost
argument_list|)
return|;
block|}
comment|/** Fills all member fields defined in {@code BasicStats} in {@code stats}.     *  Subclasses can override this method to fill additional stats. */
DECL|method|fillBasicStats
specifier|protected
name|void
name|fillBasicStats
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
parameter_list|)
block|{
comment|// #positions(field) must be>= #positions(term)
assert|assert
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
operator|==
operator|-
literal|1
operator|||
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
operator|>=
name|termStats
operator|.
name|totalTermFreq
argument_list|()
assert|;
name|long
name|numberOfDocuments
init|=
name|collectionStats
operator|.
name|docCount
argument_list|()
operator|==
operator|-
literal|1
condition|?
name|collectionStats
operator|.
name|maxDoc
argument_list|()
else|:
name|collectionStats
operator|.
name|docCount
argument_list|()
decl_stmt|;
name|long
name|docFreq
init|=
name|termStats
operator|.
name|docFreq
argument_list|()
decl_stmt|;
name|long
name|totalTermFreq
init|=
name|termStats
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
comment|// codec does not supply totalTermFreq: substitute docFreq
if|if
condition|(
name|totalTermFreq
operator|==
operator|-
literal|1
condition|)
block|{
name|totalTermFreq
operator|=
name|docFreq
expr_stmt|;
block|}
specifier|final
name|long
name|numberOfFieldTokens
decl_stmt|;
specifier|final
name|float
name|avgFieldLength
decl_stmt|;
name|long
name|sumTotalTermFreq
init|=
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|sumTotalTermFreq
operator|<=
literal|0
condition|)
block|{
comment|// field does not exist;
comment|// We have to provide something if codec doesnt supply these measures,
comment|// or if someone omitted frequencies for the field... negative values cause
comment|// NaN/Inf for some scorers.
name|numberOfFieldTokens
operator|=
name|docFreq
expr_stmt|;
name|avgFieldLength
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|numberOfFieldTokens
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|avgFieldLength
operator|=
operator|(
name|float
operator|)
name|numberOfFieldTokens
operator|/
name|numberOfDocuments
expr_stmt|;
block|}
comment|// TODO: add sumDocFreq for field (numberOfFieldPostings)
name|stats
operator|.
name|setNumberOfDocuments
argument_list|(
name|numberOfDocuments
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumberOfFieldTokens
argument_list|(
name|numberOfFieldTokens
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setAvgFieldLength
argument_list|(
name|avgFieldLength
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setDocFreq
argument_list|(
name|docFreq
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setTotalTermFreq
argument_list|(
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
comment|/**    * Scores the document {@code doc}.    *<p>Subclasses must apply their scoring formula in this class.</p>    * @param stats the corpus level statistics.    * @param freq the term frequency.    * @param docLen the document length.    * @return the score.    */
DECL|method|score
specifier|protected
specifier|abstract
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
function_decl|;
comment|/**    * Subclasses should implement this method to explain the score. {@code expl}    * already contains the score, the name of the class and the doc id, as well    * as the term frequency and its explanation; subclasses can add additional    * clauses to explain details of their scoring formulae.    *<p>The default implementation does nothing.</p>    *     * @param subExpls the list of details of the explanation to extend    * @param stats the corpus level statistics.    * @param doc the document id.    * @param freq the term frequency.    * @param docLen the document length.    */
DECL|method|explain
specifier|protected
name|void
name|explain
parameter_list|(
name|List
argument_list|<
name|Explanation
argument_list|>
name|subExpls
parameter_list|,
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{}
comment|/**    * Explains the score. The implementation here provides a basic explanation    * in the format<em>score(name-of-similarity, doc=doc-id,    * freq=term-frequency), computed from:</em>, and    * attaches the score (computed via the {@link #score(BasicStats, float, float)}    * method) and the explanation for the term frequency. Subclasses content with    * this format may add additional details in    * {@link #explain(List, BasicStats, int, float, float)}.    *      * @param stats the corpus level statistics.    * @param doc the document id.    * @param freq the term frequency and its explanation.    * @param docLen the document length.    * @return the explanation.    */
DECL|method|explain
specifier|protected
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|explain
argument_list|(
name|subs
argument_list|,
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
operator|.
name|getValue
argument_list|()
argument_list|,
name|docLen
argument_list|)
expr_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|(
name|stats
argument_list|,
name|freq
operator|.
name|getValue
argument_list|()
argument_list|,
name|docLen
argument_list|)
argument_list|,
literal|"score("
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", doc="
operator|+
name|doc
operator|+
literal|", freq="
operator|+
name|freq
operator|.
name|getValue
argument_list|()
operator|+
literal|"), computed from:"
argument_list|,
name|subs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|SimScorer
name|simScorer
parameter_list|(
name|SimWeight
name|stats
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stats
operator|instanceof
name|MultiSimilarity
operator|.
name|MultiStats
condition|)
block|{
comment|// a multi term query (e.g. phrase). return the summation,
comment|// scoring almost as if it were boolean query
name|SimWeight
name|subStats
index|[]
init|=
operator|(
operator|(
name|MultiSimilarity
operator|.
name|MultiStats
operator|)
name|stats
operator|)
operator|.
name|subStats
decl_stmt|;
name|SimScorer
name|subScorers
index|[]
init|=
operator|new
name|SimScorer
index|[
name|subStats
operator|.
name|length
index|]
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
name|subScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BasicStats
name|basicstats
init|=
operator|(
name|BasicStats
operator|)
name|subStats
index|[
name|i
index|]
decl_stmt|;
name|subScorers
index|[
name|i
index|]
operator|=
operator|new
name|BasicSimScorer
argument_list|(
name|basicstats
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|basicstats
operator|.
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiSimilarity
operator|.
name|MultiSimScorer
argument_list|(
name|subScorers
argument_list|)
return|;
block|}
else|else
block|{
name|BasicStats
name|basicstats
init|=
operator|(
name|BasicStats
operator|)
name|stats
decl_stmt|;
return|return
operator|new
name|BasicSimScorer
argument_list|(
name|basicstats
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|basicstats
operator|.
name|field
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Subclasses must override this method to return the name of the Similarity    * and preferably the values of parameters (if any) as well.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
comment|// ------------------------------ Norm handling ------------------------------
comment|/** Norm to document length map. */
DECL|field|NORM_TABLE
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|NORM_TABLE
init|=
operator|new
name|float
index|[
literal|256
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|float
name|floatNorm
init|=
name|SmallFloat
operator|.
name|byte315ToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
decl_stmt|;
name|NORM_TABLE
index|[
name|i
index|]
operator|=
literal|1.0f
operator|/
operator|(
name|floatNorm
operator|*
name|floatNorm
operator|)
expr_stmt|;
block|}
name|NORM_TABLE
index|[
literal|0
index|]
operator|=
literal|1.0f
operator|/
name|NORM_TABLE
index|[
literal|255
index|]
expr_stmt|;
comment|// otherwise inf
block|}
comment|/** Encodes the document length in the same way as {@link TFIDFSimilarity}. */
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
specifier|final
name|float
name|numTerms
decl_stmt|;
if|if
condition|(
name|discountOverlaps
condition|)
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
expr_stmt|;
else|else
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
expr_stmt|;
return|return
name|encodeNormValue
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
argument_list|,
name|numTerms
argument_list|)
return|;
block|}
comment|/** Decodes a normalization factor (document length) stored in an index.    * @see #encodeNormValue(float,float)    */
DECL|method|decodeNormValue
specifier|protected
name|float
name|decodeNormValue
parameter_list|(
name|byte
name|norm
parameter_list|)
block|{
return|return
name|NORM_TABLE
index|[
name|norm
operator|&
literal|0xFF
index|]
return|;
comment|//& 0xFF maps negative bytes to positive above 127
block|}
comment|/** Encodes the length to a byte via SmallFloat. */
DECL|method|encodeNormValue
specifier|protected
name|byte
name|encodeNormValue
parameter_list|(
name|float
name|boost
parameter_list|,
name|float
name|length
parameter_list|)
block|{
return|return
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
operator|(
name|boost
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|length
argument_list|)
operator|)
argument_list|)
return|;
block|}
comment|// ----------------------------- Static methods ------------------------------
comment|/** Returns the base two logarithm of {@code x}. */
DECL|method|log2
specifier|public
specifier|static
name|double
name|log2
parameter_list|(
name|double
name|x
parameter_list|)
block|{
comment|// Put this to a 'util' class if we need more of these.
return|return
name|Math
operator|.
name|log
argument_list|(
name|x
argument_list|)
operator|/
name|LOG_2
return|;
block|}
comment|// --------------------------------- Classes ---------------------------------
comment|/** Delegates the {@link #score(int, float)} and    * {@link #explain(int, Explanation)} methods to    * {@link SimilarityBase#score(BasicStats, float, float)} and    * {@link SimilarityBase#explain(BasicStats, int, Explanation, float)},    * respectively.    */
DECL|class|BasicSimScorer
specifier|private
class|class
name|BasicSimScorer
extends|extends
name|SimScorer
block|{
DECL|field|stats
specifier|private
specifier|final
name|BasicStats
name|stats
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|NumericDocValues
name|norms
decl_stmt|;
DECL|method|BasicSimScorer
name|BasicSimScorer
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|NumericDocValues
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
block|}
DECL|method|getNormValue
specifier|private
name|float
name|getNormValue
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
return|return
literal|1F
return|;
block|}
if|if
condition|(
name|norms
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|decodeNormValue
argument_list|(
operator|(
name|byte
operator|)
name|norms
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|decodeNormValue
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We have to supply something in case norms are omitted
return|return
name|SimilarityBase
operator|.
name|this
operator|.
name|score
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|getNormValue
argument_list|(
name|doc
argument_list|)
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
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SimilarityBase
operator|.
name|this
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
argument_list|,
name|getNormValue
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeSlopFactor
specifier|public
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
operator|/
operator|(
name|distance
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|computePayloadFactor
specifier|public
name|float
name|computePayloadFactor
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
block|}
block|}
end_class

end_unit

