begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|Set
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
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
import|;
end_import

begin_comment
comment|/**   * Tests that the {@link BaseExplanationTestCase} helper code, as well as   * {@link CheckHits#checkNoMatchExplanations} are checking what they are suppose to.  */
end_comment

begin_class
DECL|class|TestBaseExplanationTestCase
specifier|public
class|class
name|TestBaseExplanationTestCase
extends|extends
name|BaseExplanationTestCase
block|{
DECL|method|testQueryNoMatchWhenExpected
specifier|public
name|void
name|testQueryNoMatchWhenExpected
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|AssertionFailedError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|qtest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"BOGUS"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
comment|/* none */
block|}
argument_list|)
expr_stmt|;
block|}
block|)
class|;
end_class

begin_function
unit|}   public
DECL|method|testQueryMatchWhenNotExpected
name|void
name|testQueryMatchWhenNotExpected
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|AssertionFailedError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|qtest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
operator|,
literal|1
comment|/*, 2, 3 */
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_function
unit|}    public
DECL|method|testIncorrectExplainScores
name|void
name|testIncorrectExplainScores
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sanity check what a real TermQuery matches
name|qtest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"zz"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
comment|// ensure when the Explanations are broken, we get an error about those matches
name|expectThrows
argument_list|(
name|AssertionFailedError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|qtest
argument_list|(
operator|new
name|BrokenExplainTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"zz"
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
operator|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_function
unit|}    public
DECL|method|testIncorrectExplainMatches
name|void
name|testIncorrectExplainMatches
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sanity check what a real TermQuery matches
name|qtest
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"zz"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
comment|// ensure when the Explanations are broken, we get an error about the non matches
name|expectThrows
argument_list|(
name|AssertionFailedError
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CheckHits
operator|.
name|checkNoMatchExplanations
argument_list|(
operator|new
name|BrokenExplainTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"zz"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|,
name|FIELD
argument_list|,
name|searcher
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
operator|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_class
unit|}       public
DECL|class|BrokenExplainTermQuery
specifier|static
specifier|final
class|class
name|BrokenExplainTermQuery
extends|extends
name|TermQuery
block|{
DECL|field|toggleExplainMatch
specifier|public
specifier|final
name|boolean
name|toggleExplainMatch
decl_stmt|;
DECL|field|breakExplainScores
specifier|public
specifier|final
name|boolean
name|breakExplainScores
decl_stmt|;
DECL|method|BrokenExplainTermQuery
specifier|public
name|BrokenExplainTermQuery
parameter_list|(
name|Term
name|t
parameter_list|,
name|boolean
name|toggleExplainMatch
parameter_list|,
name|boolean
name|breakExplainScores
parameter_list|)
block|{
name|super
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|this
operator|.
name|toggleExplainMatch
operator|=
name|toggleExplainMatch
expr_stmt|;
name|this
operator|.
name|breakExplainScores
operator|=
name|breakExplainScores
expr_stmt|;
block|}
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BrokenExplainWeight
argument_list|(
name|this
argument_list|,
name|super
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|BrokenExplainWeight
specifier|public
specifier|static
specifier|final
class|class
name|BrokenExplainWeight
extends|extends
name|Weight
block|{
DECL|field|in
specifier|final
name|Weight
name|in
decl_stmt|;
DECL|method|BrokenExplainWeight
specifier|public
name|BrokenExplainWeight
parameter_list|(
name|BrokenExplainTermQuery
name|q
parameter_list|,
name|Weight
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|BrokenExplainTermQuery
name|q
init|=
operator|(
name|BrokenExplainTermQuery
operator|)
name|this
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Explanation
name|result
init|=
name|in
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|isMatch
argument_list|()
condition|)
block|{
if|if
condition|(
name|q
operator|.
name|breakExplainScores
condition|)
block|{
name|result
operator|=
name|Explanation
operator|.
name|match
argument_list|(
operator|-
literal|1F
operator|*
name|result
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"Broken Explanation Score"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|q
operator|.
name|toggleExplainMatch
condition|)
block|{
name|result
operator|=
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Broken Explanation Matching"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|q
operator|.
name|toggleExplainMatch
condition|)
block|{
name|result
operator|=
name|Explanation
operator|.
name|match
argument_list|(
operator|-
literal|42.0F
argument_list|,
literal|"Broken Explanation Matching"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|in
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
end_class

unit|}
end_unit

