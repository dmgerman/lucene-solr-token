begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Scorer
operator|.
name|ChildScorer
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
name|Scorer
import|;
end_import

begin_comment
comment|/** Verifies in collect() that all child subScorers are on  *  the collected doc. */
end_comment

begin_class
DECL|class|AssertingSubDocsAtOnceCollector
specifier|public
class|class
name|AssertingSubDocsAtOnceCollector
extends|extends
name|Collector
block|{
comment|// TODO: allow wrapping another Collector
DECL|field|allScorers
name|List
argument_list|<
name|Scorer
argument_list|>
name|allScorers
decl_stmt|;
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|s
parameter_list|)
block|{
comment|// Gathers all scorers, including s and "under":
name|allScorers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Scorer
argument_list|>
argument_list|()
expr_stmt|;
name|allScorers
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|allScorers
operator|.
name|size
argument_list|()
condition|)
block|{
name|s
operator|=
name|allScorers
operator|.
name|get
argument_list|(
name|upto
operator|++
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildScorer
name|sub
range|:
name|s
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|allScorers
operator|.
name|add
argument_list|(
name|sub
operator|.
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
for|for
control|(
name|Scorer
name|s
range|:
name|allScorers
control|)
block|{
if|if
condition|(
name|docID
operator|!=
name|s
operator|.
name|docID
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"subScorer="
operator|+
name|s
operator|+
literal|" has docID="
operator|+
name|s
operator|.
name|docID
argument_list|()
operator|+
literal|" != collected docID="
operator|+
name|docID
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
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
end_class

end_unit

