begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|postingshighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TermStatistics
import|;
end_import

begin_comment
comment|/**   * Used for ranking passages.  *<p>  * Each passage is scored as a miniature document within the document.  * The final score is computed as {@link #norm} * {@link #weight} *&sum; {@link #tf}.  * The default implementation is BM25 * {@link #norm}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PassageScorer
specifier|public
class|class
name|PassageScorer
block|{
comment|// TODO: this formula completely made up. It might not provide relevant snippets!
comment|/** BM25 k1 parameter, controls term frequency normalization */
DECL|field|k1
specifier|public
specifier|static
specifier|final
name|float
name|k1
init|=
literal|1.2f
decl_stmt|;
comment|/** BM25 b parameter, controls length normalization. */
DECL|field|b
specifier|public
specifier|static
specifier|final
name|float
name|b
init|=
literal|0.75f
decl_stmt|;
comment|/**    * A pivot used for length normalization.    * The default value is the typical average english sentence length.    */
DECL|field|pivot
specifier|public
specifier|static
specifier|final
name|float
name|pivot
init|=
literal|87f
decl_stmt|;
comment|/**    * Computes term importance, given its collection-wide statistics.    *     * @param collectionStats statistics for the collection    * @param termStats statistics for the term    * @return term importance    */
DECL|method|weight
specifier|public
name|float
name|weight
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
parameter_list|)
block|{
name|long
name|numDocs
init|=
name|collectionStats
operator|.
name|maxDoc
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

