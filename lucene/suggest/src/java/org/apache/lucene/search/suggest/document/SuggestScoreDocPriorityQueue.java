begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|suggest
operator|.
name|document
operator|.
name|TopSuggestDocs
operator|.
name|SuggestScoreDoc
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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * Bounded priority queue for {@link SuggestScoreDoc}s.  * Priority is based on {@link SuggestScoreDoc#score} and tie  * is broken by {@link SuggestScoreDoc#doc}  */
end_comment

begin_class
DECL|class|SuggestScoreDocPriorityQueue
specifier|final
class|class
name|SuggestScoreDocPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SuggestScoreDoc
argument_list|>
block|{
comment|/**    * Creates a new priority queue of the specified size.    */
DECL|method|SuggestScoreDocPriorityQueue
specifier|public
name|SuggestScoreDocPriorityQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SuggestScoreDoc
name|a
parameter_list|,
name|SuggestScoreDoc
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|score
operator|==
name|b
operator|.
name|score
condition|)
block|{
comment|// prefer smaller doc id, in case of a tie
return|return
name|a
operator|.
name|doc
operator|>
name|b
operator|.
name|doc
return|;
block|}
return|return
name|a
operator|.
name|score
operator|<
name|b
operator|.
name|score
return|;
block|}
comment|/**    * Returns the top N results in descending order.    */
DECL|method|getResults
specifier|public
name|SuggestScoreDoc
index|[]
name|getResults
parameter_list|()
block|{
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
name|SuggestScoreDoc
index|[]
name|res
init|=
operator|new
name|SuggestScoreDoc
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
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
block|{
name|res
index|[
name|i
index|]
operator|=
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

