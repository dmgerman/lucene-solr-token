begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|tasks
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|analysis
operator|.
name|TokenStream
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
name|highlight
operator|.
name|Highlighter
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
name|highlight
operator|.
name|TextFragment
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
name|highlight
operator|.
name|InvalidTokenOffsetsException
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
name|document
operator|.
name|Document
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
name|IndexReader
import|;
end_import

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
comment|/**  * Test Search task which counts number of searches.  */
end_comment

begin_class
DECL|class|CountingHighlighterTestTask
specifier|public
class|class
name|CountingHighlighterTestTask
extends|extends
name|SearchTravRetHighlightTask
block|{
DECL|field|numHighlightedResults
specifier|public
specifier|static
name|int
name|numHighlightedResults
init|=
literal|0
decl_stmt|;
DECL|field|numDocsRetrieved
specifier|public
specifier|static
name|int
name|numDocsRetrieved
init|=
literal|0
decl_stmt|;
DECL|method|CountingHighlighterTestTask
specifier|public
name|CountingHighlighterTestTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|retrieveDoc
specifier|protected
name|Document
name|retrieveDoc
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|document
init|=
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|document
operator|!=
literal|null
condition|)
block|{
name|numDocsRetrieved
operator|++
expr_stmt|;
block|}
return|return
name|document
return|;
block|}
DECL|method|doHighlight
specifier|protected
name|int
name|doHighlight
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
name|text
parameter_list|,
name|Highlighter
name|highlighter
parameter_list|,
name|boolean
name|mergeContiguous
parameter_list|,
name|int
name|maxFragments
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
name|TextFragment
index|[]
name|frag
init|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|ts
argument_list|,
name|text
argument_list|,
name|mergeContiguous
argument_list|,
name|maxFragments
argument_list|)
decl_stmt|;
name|numHighlightedResults
operator|+=
name|frag
operator|!=
literal|null
condition|?
name|frag
operator|.
name|length
else|:
literal|0
expr_stmt|;
return|return
name|frag
operator|!=
literal|null
condition|?
name|frag
operator|.
name|length
else|:
literal|0
return|;
block|}
block|}
end_class

end_unit

