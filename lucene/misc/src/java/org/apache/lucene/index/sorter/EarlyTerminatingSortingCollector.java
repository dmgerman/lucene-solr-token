begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|IndexWriter
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
name|LeafCollector
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
name|Collector
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
name|FilterLeafCollector
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
name|FilterCollector
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
name|Sort
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
name|TopDocsCollector
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
name|TotalHitCountCollector
import|;
end_import

begin_comment
comment|/**  * A {@link Collector} that early terminates collection of documents on a  * per-segment basis, if the segment was sorted according to the given  * {@link Sort}.  *  *<p>  *<b>NOTE:</b> the {@code Collector} detects sorted segments according to  * {@link SortingMergePolicy}, so it's best used in conjunction with it. Also,  * it collects up to a specified {@code numDocsToCollect} from each segment,  * and therefore is mostly suitable for use in conjunction with collectors such as  * {@link TopDocsCollector}, and not e.g. {@link TotalHitCountCollector}.  *<p>  *<b>NOTE</b>: If you wrap a {@code TopDocsCollector} that sorts in the same  * order as the index order, the returned {@link TopDocsCollector#topDocs() TopDocs}  * will be correct. However the total of {@link TopDocsCollector#getTotalHits()  * hit count} will be underestimated since not all matching documents will have  * been collected.  *<p>  *<b>NOTE</b>: This {@code Collector} uses {@link Sort#toString()} to detect  * whether a segment was sorted with the same {@code Sort}. This has  * two implications:  *<ul>  *<li>if a custom comparator is not implemented correctly and returns  * different identifiers for equivalent instances, this collector will not  * detect sorted segments,</li>  *<li>if you suddenly change the {@link IndexWriter}'s  * {@code SortingMergePolicy} to sort according to another criterion and if both  * the old and the new {@code Sort}s have the same identifier, this  * {@code Collector} will incorrectly detect sorted segments.</li>  *</ul>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|EarlyTerminatingSortingCollector
specifier|public
class|class
name|EarlyTerminatingSortingCollector
extends|extends
name|FilterCollector
block|{
comment|/** Sort used to sort the search results */
DECL|field|sort
specifier|protected
specifier|final
name|Sort
name|sort
decl_stmt|;
comment|/** Number of documents to collect in each segment */
DECL|field|numDocsToCollect
specifier|protected
specifier|final
name|int
name|numDocsToCollect
decl_stmt|;
DECL|field|numCollected
specifier|private
name|int
name|numCollected
decl_stmt|;
comment|/**    * Create a new {@link EarlyTerminatingSortingCollector} instance.    *    * @param in    *          the collector to wrap    * @param sort    *          the sort you are sorting the search results on    * @param numDocsToCollect    *          the number of documents to collect on each segment. When wrapping    *          a {@link TopDocsCollector}, this number should be the number of    *          hits.    */
DECL|method|EarlyTerminatingSortingCollector
specifier|public
name|EarlyTerminatingSortingCollector
parameter_list|(
name|Collector
name|in
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|numDocsToCollect
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDocsToCollect
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"numDocsToCollect must always be> 0, got "
operator|+
name|numDocsToCollect
argument_list|)
throw|;
block|}
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|numDocsToCollect
operator|=
name|numDocsToCollect
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|SortingMergePolicy
operator|.
name|isSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|sort
argument_list|)
condition|)
block|{
comment|// segment is sorted, can early-terminate
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
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
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|numCollected
operator|>=
name|numDocsToCollect
condition|)
block|{
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

