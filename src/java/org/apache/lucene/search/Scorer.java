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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/** Expert: Common scoring functionality for different types of queries.  *<br>A<code>Scorer</code> iterates over all documents matching a query,  * or provides an explanation of the score for a query for a given document.  *<br>Scores are computed using a given<code>Similarity</code> implementation.  */
end_comment

begin_class
DECL|class|Scorer
specifier|public
specifier|abstract
class|class
name|Scorer
block|{
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
comment|/** Constructs a Scorer.    * @param similarity The<code>Similarity</code> implementation used by this scorer.    */
DECL|method|Scorer
specifier|protected
name|Scorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Returns the Similarity implementation used by this scorer. */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
comment|/** Scores and collects all matching documents.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|next
argument_list|()
condition|)
block|{
name|hc
operator|.
name|collect
argument_list|(
name|doc
argument_list|()
argument_list|,
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Advances to the next document matching the query.    * @return true iff there is another document matching the query.    */
DECL|method|next
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current document number matching the query.    * Initially invalid, until {@link #next()} is called the first time.    */
DECL|method|doc
specifier|public
specifier|abstract
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #next()} is called the first time.    */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Skips to the first match beyond the current whose document number is    * greater than or equal to a given target.     * @param target The target document number.    * @return true iff there is such a match.    *<p>Behaves as if written:<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * Most implementations are considerably more efficient than that.    */
DECL|method|skipTo
specifier|public
specifier|abstract
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns an explanation of the score for a document.    *<br>When this method is used, the {@link #next()} method    * and the {@link #score(HitCollector)} method should not be used.    * @param doc The document number for the explanation.    */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

