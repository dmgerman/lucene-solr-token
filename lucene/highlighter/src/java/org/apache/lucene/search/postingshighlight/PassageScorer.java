begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**   * Ranks passages found by {@link PostingsHighlighter}.  *<p>  * Each passage is scored as a miniature document within the document.  * The final score is computed as {@link #norm} *&sum; ({@link #weight} * {@link #tf}).  * The default implementation is {@link #norm} * BM25.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PassageScorer
specifier|public
class|class
name|PassageScorer
block|{
comment|// TODO: this formula is completely made up. It might not provide relevant snippets!
comment|/** BM25 k1 parameter, controls term frequency normalization */
DECL|field|k1
specifier|final
name|float
name|k1
decl_stmt|;
comment|/** BM25 b parameter, controls length normalization. */
DECL|field|b
specifier|final
name|float
name|b
decl_stmt|;
comment|/** A pivot used for length normalization. */
DECL|field|pivot
specifier|final
name|float
name|pivot
decl_stmt|;
comment|/**    * Creates PassageScorer with these default values:    *<ul>    *<li>{@code k1 = 1.2},    *<li>{@code b = 0.75}.    *<li>{@code pivot = 87}    *</ul>    */
DECL|method|PassageScorer
specifier|public
name|PassageScorer
parameter_list|()
block|{
comment|// 1.2 and 0.75 are well-known bm25 defaults (but maybe not the best here) ?
comment|// 87 is typical average english sentence length.
name|this
argument_list|(
literal|1.2f
argument_list|,
literal|0.75f
argument_list|,
literal|87f
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates PassageScorer with specified scoring parameters    * @param k1 Controls non-linear term frequency normalization (saturation).    * @param b Controls to what degree passage length normalizes tf values.    * @param pivot Pivot value for length normalization (some rough idea of average sentence length in characters).    */
DECL|method|PassageScorer
specifier|public
name|PassageScorer
parameter_list|(
name|float
name|k1
parameter_list|,
name|float
name|b
parameter_list|,
name|float
name|pivot
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
name|this
operator|.
name|pivot
operator|=
name|pivot
expr_stmt|;
block|}
comment|/**    * Computes term importance, given its in-document statistics.    *     * @param contentLength length of document in characters    * @param totalTermFreq number of time term occurs in document    * @return term importance    */
DECL|method|weight
specifier|public
name|float
name|weight
parameter_list|(
name|int
name|contentLength
parameter_list|,
name|int
name|totalTermFreq
parameter_list|)
block|{
comment|// approximate #docs from content length
name|float
name|numDocs
init|=
literal|1
operator|+
name|contentLength
operator|/
name|pivot
decl_stmt|;
comment|// numDocs not numDocs - docFreq (ala DFR), since we approximate numDocs
return|return
operator|(
name|k1
operator|+
literal|1
operator|)
operator|*
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
operator|+
literal|0.5D
operator|)
operator|/
operator|(
name|totalTermFreq
operator|+
literal|0.5D
operator|)
argument_list|)
return|;
block|}
comment|/**    * Computes term weight, given the frequency within the passage    * and the passage's length.    *     * @param freq number of occurrences of within this passage    * @param passageLen length of the passage in characters.    * @return term weight    */
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|int
name|freq
parameter_list|,
name|int
name|passageLen
parameter_list|)
block|{
name|float
name|norm
init|=
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
operator|(
name|passageLen
operator|/
name|pivot
operator|)
operator|)
decl_stmt|;
return|return
name|freq
operator|/
operator|(
name|freq
operator|+
name|norm
operator|)
return|;
block|}
comment|/**    * Normalize a passage according to its position in the document.    *<p>    * Typically passages towards the beginning of the document are     * more useful for summarizing the contents.    *<p>    * The default implementation is<code>1 + 1/log(pivot + passageStart)</code>    * @param passageStart start offset of the passage    * @return a boost value multiplied into the passage's core.    */
DECL|method|norm
specifier|public
name|float
name|norm
parameter_list|(
name|int
name|passageStart
parameter_list|)
block|{
return|return
literal|1
operator|+
literal|1
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
name|pivot
operator|+
name|passageStart
argument_list|)
return|;
block|}
block|}
end_class

end_unit

