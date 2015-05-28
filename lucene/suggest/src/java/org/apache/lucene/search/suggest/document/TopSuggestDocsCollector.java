begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|search
operator|.
name|CollectionTerminatedException
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
name|SimpleCollector
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|TopSuggestDocs
operator|.
name|SuggestScoreDoc
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.lucene.search.Collector} that collects completion and  * score, along with document id  *<p>  * Non scoring collector that collect completions in order of their  * pre-computed scores.  *<p>  * NOTE: One document can be collected multiple times if a document  * is matched for multiple unique completions for a given query  *<p>  * Subclasses should only override  * {@link TopSuggestDocsCollector#collect(int, CharSequence, CharSequence, float)}.  *<p>  * NOTE: {@link #setScorer(org.apache.lucene.search.Scorer)} and  * {@link #collect(int)} is not used  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TopSuggestDocsCollector
specifier|public
class|class
name|TopSuggestDocsCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|priorityQueue
specifier|private
specifier|final
name|SuggestScoreDocPriorityQueue
name|priorityQueue
decl_stmt|;
DECL|field|num
specifier|private
specifier|final
name|int
name|num
decl_stmt|;
comment|/**    * Document base offset for the current Leaf    */
DECL|field|docBase
specifier|protected
name|int
name|docBase
decl_stmt|;
comment|/**    * Sole constructor    *    * Collects at most<code>num</code> completions    * with corresponding document and weight    */
DECL|method|TopSuggestDocsCollector
specifier|public
name|TopSuggestDocsCollector
parameter_list|(
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|num
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'num' must be> 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|num
operator|=
name|num
expr_stmt|;
name|this
operator|.
name|priorityQueue
operator|=
operator|new
name|SuggestScoreDocPriorityQueue
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of results to be collected    */
DECL|method|getCountToCollect
specifier|public
name|int
name|getCountToCollect
parameter_list|()
block|{
return|return
name|num
return|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
comment|/**    * Called for every matched completion,    * similar to {@link org.apache.lucene.search.LeafCollector#collect(int)}    * but for completions.    *    * NOTE: collection at the leaf level is guaranteed to be in    * descending order of score    */
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|docID
parameter_list|,
name|CharSequence
name|key
parameter_list|,
name|CharSequence
name|context
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
name|SuggestScoreDoc
name|current
init|=
operator|new
name|SuggestScoreDoc
argument_list|(
name|docBase
operator|+
name|docID
argument_list|,
name|key
argument_list|,
name|context
argument_list|,
name|score
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|==
name|priorityQueue
operator|.
name|insertWithOverflow
argument_list|(
name|current
argument_list|)
condition|)
block|{
comment|// if the current SuggestScoreDoc has overflown from pq,
comment|// we can assume all of the successive collections from
comment|// this leaf will be overflown as well
comment|// TODO: reuse the overflow instance?
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Returns at most<code>num</code> Top scoring {@link org.apache.lucene.search.suggest.document.TopSuggestDocs}s    */
DECL|method|get
specifier|public
name|TopSuggestDocs
name|get
parameter_list|()
throws|throws
name|IOException
block|{
name|SuggestScoreDoc
index|[]
name|suggestScoreDocs
init|=
name|priorityQueue
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|suggestScoreDocs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|TopSuggestDocs
argument_list|(
name|suggestScoreDocs
operator|.
name|length
argument_list|,
name|suggestScoreDocs
argument_list|,
name|suggestScoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|TopSuggestDocs
operator|.
name|EMPTY
return|;
block|}
block|}
comment|/**    * Ignored    */
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// {@link #collect(int, CharSequence, CharSequence, long)} is used
comment|// instead
block|}
comment|/**    * Ignored    */
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

