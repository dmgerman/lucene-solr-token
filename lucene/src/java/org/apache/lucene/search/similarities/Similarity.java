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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|document
operator|.
name|DocValuesField
import|;
end_import

begin_comment
comment|// javadoc
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
name|IndexReader
import|;
end_import

begin_comment
comment|// javadoc
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
name|Terms
import|;
end_import

begin_comment
comment|// javadoc
end_comment

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
name|PhraseQuery
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
name|TermQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
import|;
end_import

begin_comment
comment|// javadoc
end_comment

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
comment|// javadoc
end_comment

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
comment|/**   * Similarity defines the components of Lucene scoring.  *<p>  * Expert: Scoring API.  *<p>  * This is a low-level API, you should only extend this API if you want to implement   * an information retrieval<i>model</i>.  If you are instead looking for a convenient way   * to alter Lucene's scoring, consider extending a higher-level implementation  * such as {@link TFIDFSimilarity}, which implements the vector space model with this API, or   * just tweaking the default implementation: {@link DefaultSimilarity}.  *<p>  * Similarity determines how Lucene weights terms, and Lucene interacts with  * this class at both<a href="#indextime">index-time</a> and   *<a href="#querytime">query-time</a>.  *<p>  *<a name="indextime"/>  * At indexing time, the indexer calls {@link #computeNorm(FieldInvertState)}, allowing  * the Similarity implementation to return a per-document byte for the field that will   * be later accessible via {@link IndexReader#normValues(String)}.  Lucene makes no assumption  * about what is in this byte, but it is most useful for encoding length normalization   * information.  *<p>  * Implementations should carefully consider how the normalization byte is encoded: while  * Lucene's classical {@link TFIDFSimilarity} encodes a combination of index-time boost  * and length normalization information with {@link SmallFloat}, this might not be suitable  * for all purposes.  *<p>  * Many formulas require the use of average document length, which can be computed via a   * combination of {@link Terms#getSumTotalTermFreq()} and {@link IndexReader#maxDoc()},  *<p>  * Because index-time boost is handled entirely at the application level anyway,  * an application can alternatively store the index-time boost separately using an   * {@link DocValuesField}, and access this at query-time with   * {@link IndexReader#docValues(String)}.  *<p>  * Finally, using index-time boosts (either via folding into the normalization byte or  * via DocValues), is an inefficient way to boost the scores of different fields if the  * boost will be the same for every document, instead the Similarity can simply take a constant  * boost parameter<i>C</i>, and the SimilarityProvider can return different instances with  * different boosts depending upon field name.  *<p>  *<a name="querytime"/>  * At query-time, Queries interact with the Similarity via these steps:  *<ol>  *<li>The {@link #computeStats(CollectionStatistics, float, TermStatistics...)} method is called a single time,  *       allowing the implementation to compute any statistics (such as IDF, average document length, etc)  *       across<i>the entire collection</i>. The {@link TermStatistics} passed in already contain  *       the raw statistics involved, so a Similarity can freely use any combination  *       of term statistics without causing any additional I/O. Lucene makes no assumption about what is   *       stored in the returned {@link Similarity.Stats} object.  *<li>The query normalization process occurs a single time: {@link Similarity.Stats#getValueForNormalization()}  *       is called for each query leaf node, {@link SimilarityProvider#queryNorm(float)} is called for the top-level  *       query, and finally {@link Similarity.Stats#normalize(float, float)} passes down the normalization value  *       and any top-level boosts (e.g. from enclosing {@link BooleanQuery}s).  *<li>For each segment in the index, the Query creates a {@link #exactDocScorer(Stats, String, IndexReader.AtomicReaderContext)}  *       (for queries with exact frequencies such as TermQuerys and exact PhraseQueries) or a   *       {@link #sloppyDocScorer(Stats, String, IndexReader.AtomicReaderContext)} (for queries with sloppy frequencies such as  *       SpanQuerys and sloppy PhraseQueries). The score() method is called for each matching document.  *</ol>  *<p>  *<a name="explaintime"/>  * When {@link IndexSearcher#explain(Query, int)} is called, queries consult the Similarity's DocScorer for an   * explanation of how it computed its score. The query passes in a the document id and an explanation of how the frequency  * was computed.  *  * @see org.apache.lucene.index.IndexWriterConfig#setSimilarityProvider(SimilarityProvider)  * @see IndexSearcher#setSimilarityProvider(SimilarityProvider)  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Similarity
specifier|public
specifier|abstract
class|class
name|Similarity
block|{
comment|/**    * Computes the normalization value for a field, given the accumulated    * state of term processing for this field (see {@link FieldInvertState}).    *     *<p>Implementations should calculate a byte value based on the field    * state and then return that value.    *    *<p>Matches in longer fields are less precise, so implementations of this    * method usually return smaller values when<code>state.getLength()</code> is large,    * and larger values when<code>state.getLength()</code> is small.    *     * @lucene.experimental    *     * @param state current processing state for this field    * @return the calculated byte norm    */
DECL|method|computeNorm
specifier|public
specifier|abstract
name|byte
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
function_decl|;
comment|/**    * Compute any collection-level stats (e.g. IDF, average document length, etc) needed for scoring a query.    */
DECL|method|computeStats
specifier|public
specifier|abstract
name|Stats
name|computeStats
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|float
name|queryBoost
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
function_decl|;
comment|/**    * returns a new {@link Similarity.ExactDocScorer}.    */
DECL|method|exactDocScorer
specifier|public
specifier|abstract
name|ExactDocScorer
name|exactDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * returns a new {@link Similarity.SloppyDocScorer}.    */
DECL|method|sloppyDocScorer
specifier|public
specifier|abstract
name|SloppyDocScorer
name|sloppyDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * API for scoring exact queries such as {@link TermQuery} and     * exact {@link PhraseQuery}.    *<p>    * Term frequencies are integers (the term or phrase's tf)    */
DECL|class|ExactDocScorer
specifier|public
specifier|static
specifier|abstract
class|class
name|ExactDocScorer
block|{
comment|/**      * Score a single document      * @param doc document id      * @param freq term frequency      * @return document's score      */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|freq
parameter_list|)
function_decl|;
comment|/**      * Explain the score for a single document      * @param doc document id      * @param freq Explanation of how the term frequency was computed      * @return document's score      */
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
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
name|score
argument_list|(
name|doc
argument_list|,
operator|(
name|int
operator|)
name|freq
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|"score(doc="
operator|+
name|doc
operator|+
literal|",freq="
operator|+
name|freq
operator|.
name|getValue
argument_list|()
operator|+
literal|"), with freq of:"
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|freq
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|/**    * API for scoring "sloppy" queries such as {@link SpanQuery} and     * sloppy {@link PhraseQuery}.    *<p>    * Term frequencies are floating point values.    */
DECL|class|SloppyDocScorer
specifier|public
specifier|static
specifier|abstract
class|class
name|SloppyDocScorer
block|{
comment|/**      * Score a single document      * @param doc document id      * @param freq sloppy term frequency      * @return document's score      */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
function_decl|;
comment|/** Computes the amount of a sloppy phrase match, based on an edit distance. */
DECL|method|computeSlopFactor
specifier|public
specifier|abstract
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
function_decl|;
comment|/** Calculate a scoring factor based on the data in the payload. */
DECL|method|computePayloadFactor
specifier|public
specifier|abstract
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
function_decl|;
comment|/**      * Explain the score for a single document      * @param doc document id      * @param freq Explanation of how the sloppy term frequency was computed      * @return document's score      */
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
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
name|score
argument_list|(
name|doc
argument_list|,
name|freq
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|"score(doc="
operator|+
name|doc
operator|+
literal|",freq="
operator|+
name|freq
operator|.
name|getValue
argument_list|()
operator|+
literal|"), with freq of:"
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|freq
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|/** Stores the statistics for the indexed collection. This abstract    * implementation is empty; descendants of {@code Similarity} should    * subclass {@code Stats} and define the statistics they require in the    * subclass. Examples include idf, average field length, etc.    */
DECL|class|Stats
specifier|public
specifier|static
specifier|abstract
class|class
name|Stats
block|{
comment|/** The value for normalization of contained query clauses (e.g. sum of squared weights).      *<p>      * NOTE: a Similarity implementation might not use any query normalization at all,      * its not required. However, if it wants to participate in query normalization,      * it can return a value here.      */
DECL|method|getValueForNormalization
specifier|public
specifier|abstract
name|float
name|getValueForNormalization
parameter_list|()
function_decl|;
comment|/** Assigns the query normalization factor and boost from parent queries to this.      *<p>      * NOTE: a Similarity implementation might not use this normalized value at all,      * its not required. However, its usually a good idea to at least incorporate       * the topLevelBoost (e.g. from an outer BooleanQuery) into its score.      */
DECL|method|normalize
specifier|public
specifier|abstract
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

