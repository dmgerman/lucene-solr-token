begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.quality
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * Results of quality benchmark run for a single query or for a set of queries.  */
end_comment

begin_class
DECL|class|QualityStats
specifier|public
class|class
name|QualityStats
block|{
comment|/** Number of points for which precision is computed. */
DECL|field|MAX_POINTS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_POINTS
init|=
literal|20
decl_stmt|;
DECL|field|maxGoodPoints
specifier|private
name|double
name|maxGoodPoints
decl_stmt|;
DECL|field|recall
specifier|private
name|double
name|recall
decl_stmt|;
DECL|field|pAt
specifier|private
name|double
name|pAt
index|[]
decl_stmt|;
DECL|field|pReleventSum
specifier|private
name|double
name|pReleventSum
init|=
literal|0
decl_stmt|;
DECL|field|numPoints
specifier|private
name|double
name|numPoints
init|=
literal|0
decl_stmt|;
DECL|field|numGoodPoints
specifier|private
name|double
name|numGoodPoints
init|=
literal|0
decl_stmt|;
DECL|field|mrr
specifier|private
name|double
name|mrr
init|=
literal|0
decl_stmt|;
DECL|field|searchTime
specifier|private
name|long
name|searchTime
decl_stmt|;
DECL|field|docNamesExtractTime
specifier|private
name|long
name|docNamesExtractTime
decl_stmt|;
comment|/**    * A certain rank in which a relevant doc was found.    */
DECL|class|RecallPoint
specifier|public
specifier|static
class|class
name|RecallPoint
block|{
DECL|field|rank
specifier|private
name|int
name|rank
decl_stmt|;
DECL|field|recall
specifier|private
name|double
name|recall
decl_stmt|;
DECL|method|RecallPoint
specifier|private
name|RecallPoint
parameter_list|(
name|int
name|rank
parameter_list|,
name|double
name|recall
parameter_list|)
block|{
name|this
operator|.
name|rank
operator|=
name|rank
expr_stmt|;
name|this
operator|.
name|recall
operator|=
name|recall
expr_stmt|;
block|}
comment|/** Returns the rank: where on the list of returned docs this relevant doc appeared. */
DECL|method|getRank
specifier|public
name|int
name|getRank
parameter_list|()
block|{
return|return
name|rank
return|;
block|}
comment|/** Returns the recall: how many relevant docs were returned up to this point, inclusive. */
DECL|method|getRecall
specifier|public
name|double
name|getRecall
parameter_list|()
block|{
return|return
name|recall
return|;
block|}
block|}
DECL|field|recallPoints
specifier|private
name|ArrayList
argument_list|<
name|RecallPoint
argument_list|>
name|recallPoints
decl_stmt|;
comment|/**    * Construct a QualityStats object with anticipated maximal number of relevant hits.     * @param maxGoodPoints maximal possible relevant hits.    */
DECL|method|QualityStats
specifier|public
name|QualityStats
parameter_list|(
name|double
name|maxGoodPoints
parameter_list|,
name|long
name|searchTime
parameter_list|)
block|{
name|this
operator|.
name|maxGoodPoints
operator|=
name|maxGoodPoints
expr_stmt|;
name|this
operator|.
name|searchTime
operator|=
name|searchTime
expr_stmt|;
name|this
operator|.
name|recallPoints
operator|=
operator|new
name|ArrayList
argument_list|<
name|RecallPoint
argument_list|>
argument_list|()
expr_stmt|;
name|pAt
operator|=
operator|new
name|double
index|[
name|MAX_POINTS
operator|+
literal|1
index|]
expr_stmt|;
comment|// pAt[0] unused.
block|}
comment|/**    * Add a (possibly relevant) doc.    * @param n rank of the added doc (its ordinal position within the query results).    * @param isRelevant true if the added doc is relevant, false otherwise.    */
DECL|method|addResult
specifier|public
name|void
name|addResult
parameter_list|(
name|int
name|n
parameter_list|,
name|boolean
name|isRelevant
parameter_list|,
name|long
name|docNameExtractTime
parameter_list|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|numPoints
operator|+
literal|1
operator|-
name|n
argument_list|)
operator|>
literal|1E
operator|-
literal|6
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point "
operator|+
name|n
operator|+
literal|" illegal after "
operator|+
name|numPoints
operator|+
literal|" points!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isRelevant
condition|)
block|{
name|numGoodPoints
operator|+=
literal|1
expr_stmt|;
name|recallPoints
operator|.
name|add
argument_list|(
operator|new
name|RecallPoint
argument_list|(
name|n
argument_list|,
name|numGoodPoints
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|recallPoints
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|n
operator|<=
literal|5
condition|)
block|{
comment|// first point, but only within 5 top scores.
name|mrr
operator|=
literal|1.0
operator|/
name|n
expr_stmt|;
block|}
block|}
name|numPoints
operator|=
name|n
expr_stmt|;
name|double
name|p
init|=
name|numGoodPoints
operator|/
name|numPoints
decl_stmt|;
if|if
condition|(
name|isRelevant
condition|)
block|{
name|pReleventSum
operator|+=
name|p
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|<
name|pAt
operator|.
name|length
condition|)
block|{
name|pAt
index|[
name|n
index|]
operator|=
name|p
expr_stmt|;
block|}
name|recall
operator|=
name|maxGoodPoints
operator|<=
literal|0
condition|?
name|p
else|:
name|numGoodPoints
operator|/
name|maxGoodPoints
expr_stmt|;
name|docNamesExtractTime
operator|+=
name|docNameExtractTime
expr_stmt|;
block|}
comment|/**    * Return the precision at rank n:    * |{relevant hits within first<code>n</code> hits}| /<code>n</code>.    * @param n requested precision point, must be at least 1 and at most {@link #MAX_POINTS}.     */
DECL|method|getPrecisionAt
specifier|public
name|double
name|getPrecisionAt
parameter_list|(
name|int
name|n
parameter_list|)
block|{
if|if
condition|(
name|n
argument_list|<
literal|1
operator|||
name|n
argument_list|>
name|MAX_POINTS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"n="
operator|+
name|n
operator|+
literal|" - but it must be in [1,"
operator|+
name|MAX_POINTS
operator|+
literal|"] range!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|n
operator|>
name|numPoints
condition|)
block|{
return|return
operator|(
name|numPoints
operator|*
name|pAt
index|[
operator|(
name|int
operator|)
name|numPoints
index|]
operator|)
operator|/
name|n
return|;
block|}
return|return
name|pAt
index|[
name|n
index|]
return|;
block|}
comment|/**    * Return the average precision at recall points.    */
DECL|method|getAvp
specifier|public
name|double
name|getAvp
parameter_list|()
block|{
return|return
name|maxGoodPoints
operator|==
literal|0
condition|?
literal|0
else|:
name|pReleventSum
operator|/
name|maxGoodPoints
return|;
block|}
comment|/**    * Return the recall: |{relevant hits found}| / |{relevant hits existing}|.    */
DECL|method|getRecall
specifier|public
name|double
name|getRecall
parameter_list|()
block|{
return|return
name|recall
return|;
block|}
comment|/**    * Log information on this QualityStats object.    * @param logger Logger.    * @param prefix prefix before each log line.    */
DECL|method|log
specifier|public
name|void
name|log
parameter_list|(
name|String
name|title
parameter_list|,
name|int
name|paddLines
parameter_list|,
name|PrintWriter
name|logger
parameter_list|,
name|String
name|prefix
parameter_list|)
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
name|paddLines
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|title
operator|!=
literal|null
operator|&&
name|title
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|logger
operator|.
name|println
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
name|prefix
operator|=
name|prefix
operator|==
literal|null
condition|?
literal|""
else|:
name|prefix
expr_stmt|;
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|nf
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setGroupingUsed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|M
init|=
literal|19
decl_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Search Seconds: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
operator|(
name|double
operator|)
name|searchTime
operator|/
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"DocName Seconds: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
operator|(
name|double
operator|)
name|docNamesExtractTime
operator|/
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Num Points: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|numPoints
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Num Good Points: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|numGoodPoints
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Max Good Points: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|maxGoodPoints
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Average Precision: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|getAvp
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"MRR: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|getMRR
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Recall: "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|getRecall
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
operator|(
name|int
operator|)
name|numPoints
operator|&&
name|i
operator|<
name|pAt
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|println
argument_list|(
name|prefix
operator|+
name|format
argument_list|(
literal|"Precision At "
operator|+
name|i
operator|+
literal|": "
argument_list|,
name|M
argument_list|)
operator|+
name|fracFormat
argument_list|(
name|nf
operator|.
name|format
argument_list|(
name|getPrecisionAt
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paddLines
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|padd
specifier|private
specifier|static
name|String
name|padd
init|=
literal|"                                    "
decl_stmt|;
DECL|method|format
specifier|private
name|String
name|format
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|minLen
parameter_list|)
block|{
name|s
operator|=
operator|(
name|s
operator|==
literal|null
condition|?
literal|""
else|:
name|s
operator|)
expr_stmt|;
name|int
name|n
init|=
name|Math
operator|.
name|max
argument_list|(
name|minLen
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|s
operator|+
name|padd
operator|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
DECL|method|fracFormat
specifier|private
name|String
name|fracFormat
parameter_list|(
name|String
name|frac
parameter_list|)
block|{
name|int
name|k
init|=
name|frac
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|padd
operator|+
name|frac
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|Math
operator|.
name|max
argument_list|(
name|k
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|s1
operator|=
name|s1
operator|.
name|substring
argument_list|(
name|s1
operator|.
name|length
argument_list|()
operator|-
name|n
argument_list|)
expr_stmt|;
return|return
name|s1
operator|+
name|frac
operator|.
name|substring
argument_list|(
name|k
argument_list|)
return|;
block|}
comment|/**    * Create a QualityStats object that is the average of the input QualityStats objects.     * @param stats array of input stats to be averaged.    * @return an average over the input stats.    */
DECL|method|average
specifier|public
specifier|static
name|QualityStats
name|average
parameter_list|(
name|QualityStats
index|[]
name|stats
parameter_list|)
block|{
name|QualityStats
name|avg
init|=
operator|new
name|QualityStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// weired, no stats to average!
return|return
name|avg
return|;
block|}
name|int
name|m
init|=
literal|0
decl_stmt|;
comment|// queries with positive judgements
comment|// aggregate
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|avg
operator|.
name|searchTime
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|searchTime
expr_stmt|;
name|avg
operator|.
name|docNamesExtractTime
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|docNamesExtractTime
expr_stmt|;
if|if
condition|(
name|stats
index|[
name|i
index|]
operator|.
name|maxGoodPoints
operator|>
literal|0
condition|)
block|{
name|m
operator|++
expr_stmt|;
name|avg
operator|.
name|numGoodPoints
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|numGoodPoints
expr_stmt|;
name|avg
operator|.
name|numPoints
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|numPoints
expr_stmt|;
name|avg
operator|.
name|pReleventSum
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|getAvp
argument_list|()
expr_stmt|;
name|avg
operator|.
name|recall
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|recall
expr_stmt|;
name|avg
operator|.
name|mrr
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|getMRR
argument_list|()
expr_stmt|;
name|avg
operator|.
name|maxGoodPoints
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|maxGoodPoints
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|avg
operator|.
name|pAt
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|avg
operator|.
name|pAt
index|[
name|j
index|]
operator|+=
name|stats
index|[
name|i
index|]
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|m
operator|>
literal|0
operator|:
literal|"Fishy: no \"good\" queries!"
assert|;
comment|// take average: times go by all queries, other measures go by "good" queries only.
name|avg
operator|.
name|searchTime
operator|/=
name|stats
operator|.
name|length
expr_stmt|;
name|avg
operator|.
name|docNamesExtractTime
operator|/=
name|stats
operator|.
name|length
expr_stmt|;
name|avg
operator|.
name|numGoodPoints
operator|/=
name|m
expr_stmt|;
name|avg
operator|.
name|numPoints
operator|/=
name|m
expr_stmt|;
name|avg
operator|.
name|recall
operator|/=
name|m
expr_stmt|;
name|avg
operator|.
name|mrr
operator|/=
name|m
expr_stmt|;
name|avg
operator|.
name|maxGoodPoints
operator|/=
name|m
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|avg
operator|.
name|pAt
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|avg
operator|.
name|pAt
index|[
name|j
index|]
operator|/=
name|m
expr_stmt|;
block|}
name|avg
operator|.
name|pReleventSum
operator|/=
name|m
expr_stmt|;
comment|// this is actually avgp now
name|avg
operator|.
name|pReleventSum
operator|*=
name|avg
operator|.
name|maxGoodPoints
expr_stmt|;
comment|// so that getAvgP() would be correct
return|return
name|avg
return|;
block|}
comment|/**    * Returns the time it took to extract doc names for judging the measured query, in milliseconds.    */
DECL|method|getDocNamesExtractTime
specifier|public
name|long
name|getDocNamesExtractTime
parameter_list|()
block|{
return|return
name|docNamesExtractTime
return|;
block|}
comment|/**    * Returns the maximal number of good points.    * This is the number of relevant docs known by the judge for the measured query.    */
DECL|method|getMaxGoodPoints
specifier|public
name|double
name|getMaxGoodPoints
parameter_list|()
block|{
return|return
name|maxGoodPoints
return|;
block|}
comment|/**    * Returns the number of good points (only relevant points).    */
DECL|method|getNumGoodPoints
specifier|public
name|double
name|getNumGoodPoints
parameter_list|()
block|{
return|return
name|numGoodPoints
return|;
block|}
comment|/**    * Returns the number of points (both relevant and irrelevant points).    */
DECL|method|getNumPoints
specifier|public
name|double
name|getNumPoints
parameter_list|()
block|{
return|return
name|numPoints
return|;
block|}
comment|/**    * Returns the recallPoints.    */
DECL|method|getRecallPoints
specifier|public
name|RecallPoint
index|[]
name|getRecallPoints
parameter_list|()
block|{
return|return
name|recallPoints
operator|.
name|toArray
argument_list|(
operator|new
name|RecallPoint
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Returns the Mean reciprocal rank over the queries or RR for a single query.    *<p>    * Reciprocal rank is defined as<code>1/r</code> where<code>r</code> is the     * rank of the first correct result, or<code>0</code> if there are no correct     * results within the top 5 results.     *<p>    * This follows the definition in     *<a href="http://www.cnlp.org/publications/02cnlptrec10.pdf">     * Question Answering - CNLP at the TREC-10 Question Answering Track</a>.    */
DECL|method|getMRR
specifier|public
name|double
name|getMRR
parameter_list|()
block|{
return|return
name|mrr
return|;
block|}
comment|/**    * Returns the search time in milliseconds for the measured query.    */
DECL|method|getSearchTime
specifier|public
name|long
name|getSearchTime
parameter_list|()
block|{
return|return
name|searchTime
return|;
block|}
block|}
end_class

end_unit

