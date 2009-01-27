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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/** A {@link HitCollector} implementation that collects the top-scoring  * documents, returning them as a {@link TopDocs}.  This is used by {@link  * IndexSearcher} to implement {@link TopDocs}-based search.  *  *<p>This may be extended, overriding the collect method to, e.g.,  * conditionally invoke<code>super()</code> in order to filter which  * documents are collected.  *  * @deprecated Please use {@link TopScoreDocCollector}  * instead, which has better performance.  **/
end_comment

begin_class
DECL|class|TopDocCollector
specifier|public
class|class
name|TopDocCollector
extends|extends
name|HitCollector
block|{
DECL|field|reusableSD
specifier|private
name|ScoreDoc
name|reusableSD
decl_stmt|;
comment|/** The total number of hits the collector encountered. */
DECL|field|totalHits
specifier|protected
name|int
name|totalHits
decl_stmt|;
comment|/** The priority queue which holds the top-scoring documents. */
DECL|field|hq
specifier|protected
name|PriorityQueue
name|hq
decl_stmt|;
comment|/** Construct to collect a given number of hits.    * @param numHits the maximum number of hits to collect    */
DECL|method|TopDocCollector
specifier|public
name|TopDocCollector
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|HitQueue
argument_list|(
name|numHits
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated use TopDocCollector(hq) instead. numHits is not used by this    * constructor. It will be removed in a future release.    */
DECL|method|TopDocCollector
name|TopDocCollector
parameter_list|(
name|int
name|numHits
parameter_list|,
name|PriorityQueue
name|hq
parameter_list|)
block|{
name|this
operator|.
name|hq
operator|=
name|hq
expr_stmt|;
block|}
comment|/** Constructor to collect the top-scoring documents by using the given PQ.    * @param hq the PQ to use by this instance.    */
DECL|method|TopDocCollector
specifier|protected
name|TopDocCollector
parameter_list|(
name|PriorityQueue
name|hq
parameter_list|)
block|{
name|this
operator|.
name|hq
operator|=
name|hq
expr_stmt|;
block|}
comment|// javadoc inherited
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|score
operator|>
literal|0.0f
condition|)
block|{
name|totalHits
operator|++
expr_stmt|;
if|if
condition|(
name|reusableSD
operator|==
literal|null
condition|)
block|{
name|reusableSD
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|score
operator|>=
name|reusableSD
operator|.
name|score
condition|)
block|{
comment|// reusableSD holds the last "rejected" entry, so, if
comment|// this new score is not better than that, there's no
comment|// need to try inserting it
name|reusableSD
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|reusableSD
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
name|reusableSD
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|insertWithOverflow
argument_list|(
name|reusableSD
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** The total number of documents that matched this query. */
DECL|method|getTotalHits
specifier|public
name|int
name|getTotalHits
parameter_list|()
block|{
return|return
name|totalHits
return|;
block|}
comment|/** The top-scoring hits. */
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|()
block|{
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
name|float
name|maxScore
init|=
operator|(
name|totalHits
operator|==
literal|0
operator|)
condition|?
name|Float
operator|.
name|NEGATIVE_INFINITY
else|:
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
decl_stmt|;
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
block|}
end_class

end_unit

