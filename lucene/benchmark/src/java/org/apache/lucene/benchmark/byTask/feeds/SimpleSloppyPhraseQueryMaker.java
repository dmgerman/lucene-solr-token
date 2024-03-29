begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

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
name|StringTokenizer
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
name|Term
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

begin_comment
comment|/**  * Create sloppy phrase queries for performance test, in an index created using simple doc maker.  */
end_comment

begin_class
DECL|class|SimpleSloppyPhraseQueryMaker
specifier|public
class|class
name|SimpleSloppyPhraseQueryMaker
extends|extends
name|SimpleQueryMaker
block|{
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.feeds.SimpleQueryMaker#prepareQueries()    */
annotation|@
name|Override
DECL|method|prepareQueries
specifier|protected
name|Query
index|[]
name|prepareQueries
parameter_list|()
throws|throws
name|Exception
block|{
comment|// extract some 100 words from doc text to an array
name|String
name|words
index|[]
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|w
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|SingleDocSource
operator|.
name|DOC_TEXT
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
operator|&&
name|w
operator|.
name|size
argument_list|()
operator|<
literal|100
condition|)
block|{
name|w
operator|.
name|add
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|words
operator|=
name|w
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// create queries (that would find stuff) with varying slops
name|ArrayList
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|8
condition|;
name|slop
operator|++
control|)
block|{
for|for
control|(
name|int
name|qlen
init|=
literal|2
init|;
name|qlen
operator|<
literal|6
condition|;
name|qlen
operator|++
control|)
block|{
for|for
control|(
name|int
name|wd
init|=
literal|0
init|;
name|wd
operator|<
name|words
operator|.
name|length
operator|-
name|qlen
operator|-
name|slop
condition|;
name|wd
operator|++
control|)
block|{
comment|// ordered
name|int
name|remainedSlop
init|=
name|slop
decl_stmt|;
name|int
name|wind
init|=
name|wd
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qlen
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|words
index|[
name|wind
operator|++
index|]
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|remainedSlop
operator|>
literal|0
condition|)
block|{
name|remainedSlop
operator|--
expr_stmt|;
name|wind
operator|++
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|PhraseQuery
name|q
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
comment|// reversed
name|remainedSlop
operator|=
name|slop
expr_stmt|;
name|wind
operator|=
name|wd
operator|+
name|qlen
operator|+
name|remainedSlop
operator|-
literal|1
expr_stmt|;
name|builder
operator|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qlen
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|,
name|words
index|[
name|wind
operator|--
index|]
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|remainedSlop
operator|>
literal|0
condition|)
block|{
name|remainedSlop
operator|--
expr_stmt|;
name|wind
operator|--
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|setSlop
argument_list|(
name|slop
operator|+
literal|2
operator|*
name|qlen
argument_list|)
expr_stmt|;
name|q
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|queries
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

