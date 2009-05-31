begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
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
name|Similarity
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
name|DefaultSimilarity
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
name|TermQuery
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
name|DisjunctionMaxQuery
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
name|BooleanQuery
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
name|BooleanClause
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
name|BooleanClause
operator|.
name|Occur
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Test of the SweetSpotSimilarity  */
end_comment

begin_class
DECL|class|SweetSpotSimilarityTest
specifier|public
class|class
name|SweetSpotSimilarityTest
extends|extends
name|TestCase
block|{
DECL|method|testSweetSpotLengthNorm
specifier|public
name|void
name|testSweetSpotLengthNorm
parameter_list|()
block|{
name|SweetSpotSimilarity
name|ss
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
name|Similarity
name|d
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
name|Similarity
name|s
init|=
name|ss
decl_stmt|;
comment|// base case, should degrade
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"base case: i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// make a sweet spot
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"3,10: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"3,10: 10<x : i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
operator|-
literal|9
argument_list|)
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// seperate sweet spot for certain fields
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|"bar"
argument_list|,
literal|8
argument_list|,
literal|13
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|"yak"
argument_list|,
literal|6
argument_list|,
literal|9
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"f: 3,10: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"f: 3,10: 10<x : i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
operator|-
literal|9
argument_list|)
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<=
literal|13
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"f: 8,13: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"bar"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
literal|9
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"f: 6,9: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"yak"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|13
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"f: 8,13: 13<x : i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
operator|-
literal|12
argument_list|)
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"bar"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|9
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"f: 6,9: 9<x : i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|lengthNorm
argument_list|(
literal|"foo"
argument_list|,
name|i
operator|-
literal|8
argument_list|)
argument_list|,
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"yak"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// steepness
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|"a"
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|"b"
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|,
literal|0.1f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|9
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"s: i="
operator|+
name|i
operator|+
literal|" : a="
operator|+
name|ss
operator|.
name|lengthNorm
argument_list|(
literal|"a"
argument_list|,
name|i
argument_list|)
operator|+
literal|"< b="
operator|+
name|ss
operator|.
name|lengthNorm
argument_list|(
literal|"b"
argument_list|,
name|i
argument_list|)
argument_list|,
name|ss
operator|.
name|lengthNorm
argument_list|(
literal|"a"
argument_list|,
name|i
argument_list|)
operator|<
name|s
operator|.
name|lengthNorm
argument_list|(
literal|"b"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSweetSpotTf
specifier|public
name|void
name|testSweetSpotTf
parameter_list|()
block|{
name|SweetSpotSimilarity
name|ss
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|Similarity
name|d
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
name|Similarity
name|s
init|=
name|ss
decl_stmt|;
comment|// tf equal
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|0.0f
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf: i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// tf higher
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|1.0f
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"tf: i="
operator|+
name|i
operator|+
literal|" : d="
operator|+
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|+
literal|"< s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// tf flat
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|1.0f
argument_list|,
literal|6.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf flat1: i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|2.0f
argument_list|,
literal|6.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf flat2: i="
operator|+
name|i
argument_list|,
literal|2.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|+
literal|"< d="
operator|+
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// stupidity
name|assertEquals
argument_list|(
literal|"tf zero"
argument_list|,
literal|0.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyperbolicSweetSpot
specifier|public
name|void
name|testHyperbolicSweetSpot
parameter_list|()
block|{
name|SweetSpotSimilarity
name|ss
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
block|{
specifier|public
name|float
name|tf
parameter_list|(
name|int
name|freq
parameter_list|)
block|{
return|return
name|hyperbolicTf
argument_list|(
name|freq
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ss
operator|.
name|setHyperbolicTfFactors
argument_list|(
literal|3.3f
argument_list|,
literal|7.7f
argument_list|,
name|Math
operator|.
name|E
argument_list|,
literal|5.0f
argument_list|)
expr_stmt|;
name|Similarity
name|s
init|=
name|ss
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"MIN tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|3.3f
operator|<=
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MAX tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<=
literal|7.7f
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"MID tf"
argument_list|,
literal|3.3f
operator|+
operator|(
literal|7.7f
operator|-
literal|3.3f
operator|)
operator|/
literal|2.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
comment|// stupidity
name|assertEquals
argument_list|(
literal|"tf zero"
argument_list|,
literal|0.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

