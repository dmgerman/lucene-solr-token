begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * BM25 Similarity. Introduced in Stephen E. Robertson, Steve Walker,  * Susan Jones, Micheline Hancock-Beaulieu, and Mike Gatford. Okapi at TREC-3.  * In Proceedings of the Third Text Retrieval Conference (TREC 1994).  * Gaithersburg, USA, November 1994.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BM25Similarity
specifier|public
class|class
name|BM25Similarity
extends|extends
name|Similarity
block|{
DECL|field|k1
specifier|private
specifier|final
name|float
name|k1
decl_stmt|;
DECL|field|b
specifier|private
specifier|final
name|float
name|b
decl_stmt|;
comment|// TODO: should we add a delta like sifaka.cs.uiuc.edu/~ylv2/pub/sigir11-bm25l.pdf ?
comment|/**    * BM25 with the supplied parameter values.    * @param k1 Controls non-linear term frequency normalization (saturation).    * @param b Controls to what degree document length normalizes tf values.    */
DECL|method|BM25Similarity
specifier|public
name|BM25Similarity
parameter_list|(
name|float
name|k1
parameter_list|,
name|float
name|b
parameter_list|)
block|{
name|this
operator|.
name|k1
operator|=
name|k1
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
comment|/** BM25 with these default values:    *<ul>    *<li>{@code k1 = 1.2},    *<li>{@code b = 0.75}.</li>    *</ul>    */
DECL|method|BM25Similarity
specifier|public
name|BM25Similarity
parameter_list|()
block|{
name|this
operator|.
name|k1
operator|=
literal|1.2f
expr_stmt|;
name|this
operator|.
name|b
operator|=
literal|0.75f
expr_stmt|;
block|}
comment|/** Implemented as<code>log(1 + (numDocs - docFreq + 0.5)/(docFreq + 0.5))</code>. */
DECL|method|idf
specifier|protected
name|float
name|idf
parameter_list|(
name|long
name|docFreq
parameter_list|,
name|long
name|numDocs
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|+
operator|(
name|numDocs
operator|-
name|docFreq
operator|+
literal|0.5D
operator|)
operator|/
operator|(
name|docFreq
operator|+
literal|0.5D
operator|)
argument_list|)
return|;
block|}
comment|/** Implemented as<code>1 / (distance + 1)</code>. */
DECL|method|sloppyFreq
specifier|protected
name|float
name|sloppyFreq
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
comment|/** The default implementation returns<code>1</code> */
DECL|method|scorePayload
specifier|protected
name|float
name|scorePayload
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
literal|1
return|;
block|}
comment|/** The default implementation computes the average as<code>sumTotalTermFreq / maxDoc</code>,    * or returns<code>1</code> if the index does not store sumTotalTermFreq:    * any field that omits frequency information). */
DECL|method|avgFieldLength
specifier|protected
name|float
name|avgFieldLength
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|)
block|{
specifier|final
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
return|return
literal|1f
return|;
comment|// field does not exist, or stat is unsupported
block|}
else|else
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|sumTotalTermFreq
operator|/
operator|(
name|double
operator|)
name|collectionStats
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** The default implementation encodes<code>boost / sqrt(length)</code>    * with {@link SmallFloat#floatToByte315(float)}.  This is compatible with     * Lucene's default implementation.  If you change this, then you should     * change {@link #decodeNormValue(byte)} to match. */
DECL|method|encodeNormValue
specifier|protected
name|byte
name|encodeNormValue
parameter_list|(
name|float
name|boost
parameter_list|,
name|int
name|fieldLength
parameter_list|)
block|{
return|return
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|boost
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|fieldLength
argument_list|)
argument_list|)
return|;
block|}
comment|/** The default implementation returns<code>1 / f<sup>2</sup></code>    * where<code>f</code> is {@link SmallFloat#byte315ToFloat(byte)}. */
DECL|method|decodeNormValue
specifier|protected
name|float
name|decodeNormValue
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
return|return
name|NORM_TABLE
index|[
name|b
operator|&
literal|0xFF
index|]
return|;
block|}
comment|/**     * True if overlap tokens (tokens with a position of increment of zero) are    * discounted from the document's length.    */
DECL|field|discountOverlaps
specifier|protected
name|boolean
name|discountOverlaps
init|=
literal|true
decl_stmt|;
comment|/** Sets whether overlap tokens (Tokens with 0 position increment) are     *  ignored when computing norm.  By default this is true, meaning overlap    *  tokens do not count when computing norms. */
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
comment|/** Cache of decoded bytes. */
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
literal|0
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
name|f
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
name|f
operator|*
name|f
operator|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
specifier|final
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
specifier|final
name|int
name|numTerms
init|=
name|discountOverlaps
condition|?
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
else|:
name|state
operator|.
name|getLength
argument_list|()
decl_stmt|;
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
comment|/**    * Computes a score factor for a simple term and returns an explanation    * for that score factor.    *     *<p>    * The default implementation uses:    *     *<pre class="prettyprint">    * idf(docFreq, searcher.maxDoc());    *</pre>    *     * Note that {@link CollectionStatistics#maxDoc()} is used instead of    * {@link org.apache.lucene.index.IndexReader#numDocs() IndexReader#numDocs()} because also     * {@link TermStatistics#docFreq()} is used, and when the latter     * is inaccurate, so is {@link CollectionStatistics#maxDoc()}, and in the same direction.    * In addition, {@link CollectionStatistics#maxDoc()} is more efficient to compute    *       * @param collectionStats collection-level statistics    * @param termStats term-level statistics for the term    * @return an Explain object that includes both an idf score factor               and an explanation for the term.    */
DECL|method|idfExplain
specifier|public
name|Explanation
name|idfExplain
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
parameter_list|)
block|{
specifier|final
name|long
name|df
init|=
name|termStats
operator|.
name|docFreq
argument_list|()
decl_stmt|;
specifier|final
name|long
name|max
init|=
name|collectionStats
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|float
name|idf
init|=
name|idf
argument_list|(
name|df
argument_list|,
name|max
argument_list|)
decl_stmt|;
return|return
operator|new
name|Explanation
argument_list|(
name|idf
argument_list|,
literal|"idf(docFreq="
operator|+
name|df
operator|+
literal|", maxDocs="
operator|+
name|max
operator|+
literal|")"
argument_list|)
return|;
block|}
comment|/**    * Computes a score factor for a phrase.    *     *<p>    * The default implementation sums the idf factor for    * each term in the phrase.    *     * @param collectionStats collection-level statistics    * @param termStats term-level statistics for the terms in the phrase    * @return an Explain object that includes both an idf     *         score factor for the phrase and an explanation     *         for each term.    */
DECL|method|idfExplain
specifier|public
name|Explanation
name|idfExplain
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
index|[]
parameter_list|)
block|{
specifier|final
name|long
name|max
init|=
name|collectionStats
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|float
name|idf
init|=
literal|0.0f
decl_stmt|;
specifier|final
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|exp
operator|.
name|setDescription
argument_list|(
literal|"idf(), sum of:"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|TermStatistics
name|stat
range|:
name|termStats
control|)
block|{
specifier|final
name|long
name|df
init|=
name|stat
operator|.
name|docFreq
argument_list|()
decl_stmt|;
specifier|final
name|float
name|termIdf
init|=
name|idf
argument_list|(
name|df
argument_list|,
name|max
argument_list|)
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|termIdf
argument_list|,
literal|"idf(docFreq="
operator|+
name|df
operator|+
literal|", maxDocs="
operator|+
name|max
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
name|idf
operator|+=
name|termIdf
expr_stmt|;
block|}
name|exp
operator|.
name|setValue
argument_list|(
name|idf
argument_list|)
expr_stmt|;
return|return
name|exp
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
name|queryBoost
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
name|Explanation
name|idf
init|=
name|termStats
operator|.
name|length
operator|==
literal|1
condition|?
name|idfExplain
argument_list|(
name|collectionStats
argument_list|,
name|termStats
index|[
literal|0
index|]
argument_list|)
else|:
name|idfExplain
argument_list|(
name|collectionStats
argument_list|,
name|termStats
argument_list|)
decl_stmt|;
name|float
name|avgdl
init|=
name|avgFieldLength
argument_list|(
name|collectionStats
argument_list|)
decl_stmt|;
comment|// compute freq-independent part of bm25 equation across all norm values
name|float
name|cache
index|[]
init|=
operator|new
name|float
index|[
literal|256
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
name|cache
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cache
index|[
name|i
index|]
operator|=
name|k1
operator|*
operator|(
operator|(
literal|1
operator|-
name|b
operator|)
operator|+
name|b
operator|*
name|decodeNormValue
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
operator|/
name|avgdl
operator|)
expr_stmt|;
block|}
return|return
operator|new
name|BM25Stats
argument_list|(
name|collectionStats
operator|.
name|field
argument_list|()
argument_list|,
name|idf
argument_list|,
name|queryBoost
argument_list|,
name|avgdl
argument_list|,
name|cache
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|exactSimScorer
specifier|public
specifier|final
name|ExactSimScorer
name|exactSimScorer
parameter_list|(
name|SimWeight
name|stats
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BM25Stats
name|bm25stats
init|=
operator|(
name|BM25Stats
operator|)
name|stats
decl_stmt|;
specifier|final
name|NumericDocValues
name|norms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|bm25stats
operator|.
name|field
argument_list|)
decl_stmt|;
return|return
name|norms
operator|==
literal|null
condition|?
operator|new
name|ExactBM25DocScorerNoNorms
argument_list|(
name|bm25stats
argument_list|)
else|:
operator|new
name|ExactBM25DocScorer
argument_list|(
name|bm25stats
argument_list|,
name|norms
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sloppySimScorer
specifier|public
specifier|final
name|SloppySimScorer
name|sloppySimScorer
parameter_list|(
name|SimWeight
name|stats
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BM25Stats
name|bm25stats
init|=
operator|(
name|BM25Stats
operator|)
name|stats
decl_stmt|;
return|return
operator|new
name|SloppyBM25DocScorer
argument_list|(
name|bm25stats
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|bm25stats
operator|.
name|field
argument_list|)
argument_list|)
return|;
block|}
DECL|class|ExactBM25DocScorer
specifier|private
class|class
name|ExactBM25DocScorer
extends|extends
name|ExactSimScorer
block|{
DECL|field|stats
specifier|private
specifier|final
name|BM25Stats
name|stats
decl_stmt|;
DECL|field|weightValue
specifier|private
specifier|final
name|float
name|weightValue
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|NumericDocValues
name|norms
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|float
index|[]
name|cache
decl_stmt|;
DECL|method|ExactBM25DocScorer
name|ExactBM25DocScorer
parameter_list|(
name|BM25Stats
name|stats
parameter_list|,
name|NumericDocValues
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|norms
operator|!=
literal|null
assert|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|weightValue
operator|=
name|stats
operator|.
name|weight
operator|*
operator|(
name|k1
operator|+
literal|1
operator|)
expr_stmt|;
comment|// boost * idf * (k1 + 1)
name|this
operator|.
name|cache
operator|=
name|stats
operator|.
name|cache
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
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
name|int
name|freq
parameter_list|)
block|{
return|return
name|weightValue
operator|*
name|freq
operator|/
operator|(
name|freq
operator|+
name|cache
index|[
operator|(
name|byte
operator|)
name|norms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|&
literal|0xFF
index|]
operator|)
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
block|{
return|return
name|explainScore
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|,
name|stats
argument_list|,
name|norms
argument_list|)
return|;
block|}
block|}
comment|/** there are no norms, we act as if b=0 */
DECL|class|ExactBM25DocScorerNoNorms
specifier|private
class|class
name|ExactBM25DocScorerNoNorms
extends|extends
name|ExactSimScorer
block|{
DECL|field|stats
specifier|private
specifier|final
name|BM25Stats
name|stats
decl_stmt|;
DECL|field|weightValue
specifier|private
specifier|final
name|float
name|weightValue
decl_stmt|;
DECL|field|SCORE_CACHE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SCORE_CACHE_SIZE
init|=
literal|32
decl_stmt|;
DECL|field|scoreCache
specifier|private
name|float
index|[]
name|scoreCache
init|=
operator|new
name|float
index|[
name|SCORE_CACHE_SIZE
index|]
decl_stmt|;
DECL|method|ExactBM25DocScorerNoNorms
name|ExactBM25DocScorerNoNorms
parameter_list|(
name|BM25Stats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|weightValue
operator|=
name|stats
operator|.
name|weight
operator|*
operator|(
name|k1
operator|+
literal|1
operator|)
expr_stmt|;
comment|// boost * idf * (k1 + 1)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SCORE_CACHE_SIZE
condition|;
name|i
operator|++
control|)
name|scoreCache
index|[
name|i
index|]
operator|=
name|weightValue
operator|*
name|i
operator|/
operator|(
name|i
operator|+
name|k1
operator|)
expr_stmt|;
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
name|int
name|freq
parameter_list|)
block|{
comment|// TODO: maybe score cache is more trouble than its worth?
return|return
name|freq
operator|<
name|SCORE_CACHE_SIZE
comment|// check cache
condition|?
name|scoreCache
index|[
name|freq
index|]
comment|// cache hit
else|:
name|weightValue
operator|*
name|freq
operator|/
operator|(
name|freq
operator|+
name|k1
operator|)
return|;
comment|// cache miss
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
block|{
return|return
name|explainScore
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|,
name|stats
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|class|SloppyBM25DocScorer
specifier|private
class|class
name|SloppyBM25DocScorer
extends|extends
name|SloppySimScorer
block|{
DECL|field|stats
specifier|private
specifier|final
name|BM25Stats
name|stats
decl_stmt|;
DECL|field|weightValue
specifier|private
specifier|final
name|float
name|weightValue
decl_stmt|;
comment|// boost * idf * (k1 + 1)
DECL|field|norms
specifier|private
specifier|final
name|NumericDocValues
name|norms
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|float
index|[]
name|cache
decl_stmt|;
DECL|method|SloppyBM25DocScorer
name|SloppyBM25DocScorer
parameter_list|(
name|BM25Stats
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
name|weightValue
operator|=
name|stats
operator|.
name|weight
operator|*
operator|(
name|k1
operator|+
literal|1
operator|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|stats
operator|.
name|cache
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
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
block|{
comment|// if there are no norms, we act as if b=0
name|float
name|norm
init|=
name|norms
operator|==
literal|null
condition|?
name|k1
else|:
name|cache
index|[
operator|(
name|byte
operator|)
name|norms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|&
literal|0xFF
index|]
decl_stmt|;
return|return
name|weightValue
operator|*
name|freq
operator|/
operator|(
name|freq
operator|+
name|norm
operator|)
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
block|{
return|return
name|explainScore
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|,
name|stats
argument_list|,
name|norms
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
name|sloppyFreq
argument_list|(
name|distance
argument_list|)
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
name|scorePayload
argument_list|(
name|doc
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|payload
argument_list|)
return|;
block|}
block|}
comment|/** Collection statistics for the BM25 model. */
DECL|class|BM25Stats
specifier|private
specifier|static
class|class
name|BM25Stats
extends|extends
name|SimWeight
block|{
comment|/** BM25's idf */
DECL|field|idf
specifier|private
specifier|final
name|Explanation
name|idf
decl_stmt|;
comment|/** The average document length. */
DECL|field|avgdl
specifier|private
specifier|final
name|float
name|avgdl
decl_stmt|;
comment|/** query's inner boost */
DECL|field|queryBoost
specifier|private
specifier|final
name|float
name|queryBoost
decl_stmt|;
comment|/** query's outer boost (only for explain) */
DECL|field|topLevelBoost
specifier|private
name|float
name|topLevelBoost
decl_stmt|;
comment|/** weight (idf * boost) */
DECL|field|weight
specifier|private
name|float
name|weight
decl_stmt|;
comment|/** field name, for pulling norms */
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
comment|/** precomputed norm[256] with k1 * ((1 - b) + b * dl / avgdl) */
DECL|field|cache
specifier|private
specifier|final
name|float
name|cache
index|[]
decl_stmt|;
DECL|method|BM25Stats
name|BM25Stats
parameter_list|(
name|String
name|field
parameter_list|,
name|Explanation
name|idf
parameter_list|,
name|float
name|queryBoost
parameter_list|,
name|float
name|avgdl
parameter_list|,
name|float
name|cache
index|[]
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|idf
operator|=
name|idf
expr_stmt|;
name|this
operator|.
name|queryBoost
operator|=
name|queryBoost
expr_stmt|;
name|this
operator|.
name|avgdl
operator|=
name|avgdl
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
comment|// we return a TF-IDF like normalization to be nice, but we don't actually normalize ourselves.
specifier|final
name|float
name|queryWeight
init|=
name|idf
operator|.
name|getValue
argument_list|()
operator|*
name|queryBoost
decl_stmt|;
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
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
comment|// we don't normalize with queryNorm at all, we just capture the top-level boost
name|this
operator|.
name|topLevelBoost
operator|=
name|topLevelBoost
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|idf
operator|.
name|getValue
argument_list|()
operator|*
name|queryBoost
operator|*
name|topLevelBoost
expr_stmt|;
block|}
block|}
DECL|method|explainScore
specifier|private
name|Explanation
name|explainScore
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|,
name|BM25Stats
name|stats
parameter_list|,
name|NumericDocValues
name|norms
parameter_list|)
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"score(doc="
operator|+
name|doc
operator|+
literal|",freq="
operator|+
name|freq
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|boostExpl
init|=
operator|new
name|Explanation
argument_list|(
name|stats
operator|.
name|queryBoost
operator|*
name|stats
operator|.
name|topLevelBoost
argument_list|,
literal|"boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|boostExpl
operator|.
name|getValue
argument_list|()
operator|!=
literal|1.0f
condition|)
name|result
operator|.
name|addDetail
argument_list|(
name|boostExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|stats
operator|.
name|idf
argument_list|)
expr_stmt|;
name|Explanation
name|tfNormExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|tfNormExpl
operator|.
name|setDescription
argument_list|(
literal|"tfNorm, computed from:"
argument_list|)
expr_stmt|;
name|tfNormExpl
operator|.
name|addDetail
argument_list|(
name|freq
argument_list|)
expr_stmt|;
name|tfNormExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|k1
argument_list|,
literal|"parameter k1"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
name|tfNormExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
literal|0
argument_list|,
literal|"parameter b (norms omitted for field)"
argument_list|)
argument_list|)
expr_stmt|;
name|tfNormExpl
operator|.
name|setValue
argument_list|(
operator|(
name|freq
operator|.
name|getValue
argument_list|()
operator|*
operator|(
name|k1
operator|+
literal|1
operator|)
operator|)
operator|/
operator|(
name|freq
operator|.
name|getValue
argument_list|()
operator|+
name|k1
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|float
name|doclen
init|=
name|decodeNormValue
argument_list|(
operator|(
name|byte
operator|)
name|norms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|tfNormExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|b
argument_list|,
literal|"parameter b"
argument_list|)
argument_list|)
expr_stmt|;
name|tfNormExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|stats
operator|.
name|avgdl
argument_list|,
literal|"avgFieldLength"
argument_list|)
argument_list|)
expr_stmt|;
name|tfNormExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|doclen
argument_list|,
literal|"fieldLength"
argument_list|)
argument_list|)
expr_stmt|;
name|tfNormExpl
operator|.
name|setValue
argument_list|(
operator|(
name|freq
operator|.
name|getValue
argument_list|()
operator|*
operator|(
name|k1
operator|+
literal|1
operator|)
operator|)
operator|/
operator|(
name|freq
operator|.
name|getValue
argument_list|()
operator|+
name|k1
operator|*
operator|(
literal|1
operator|-
name|b
operator|+
name|b
operator|*
name|doclen
operator|/
name|stats
operator|.
name|avgdl
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|addDetail
argument_list|(
name|tfNormExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|boostExpl
operator|.
name|getValue
argument_list|()
operator|*
name|stats
operator|.
name|idf
operator|.
name|getValue
argument_list|()
operator|*
name|tfNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"BM25(k1="
operator|+
name|k1
operator|+
literal|",b="
operator|+
name|b
operator|+
literal|")"
return|;
block|}
comment|/**     * Returns the<code>k1</code> parameter    * @see #BM25Similarity(float, float)     */
DECL|method|getK1
specifier|public
name|float
name|getK1
parameter_list|()
block|{
return|return
name|k1
return|;
block|}
comment|/**    * Returns the<code>b</code> parameter     * @see #BM25Similarity(float, float)     */
DECL|method|getB
specifier|public
name|float
name|getB
parameter_list|()
block|{
return|return
name|b
return|;
block|}
block|}
end_class

end_unit

