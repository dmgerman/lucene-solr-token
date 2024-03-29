begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
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
name|Field
operator|.
name|Store
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
name|DirectoryReader
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
name|Explanation
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
name|IndexSearcher
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
name|similarities
operator|.
name|ClassicSimilarity
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
name|similarities
operator|.
name|PerFieldSimilarityWrapper
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
name|similarities
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
name|similarities
operator|.
name|TFIDFSimilarity
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|LuceneTestCase
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
name|LuceneTestCase
block|{
DECL|method|computeNorm
specifier|private
specifier|static
name|float
name|computeNorm
parameter_list|(
name|Similarity
name|sim
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|value
init|=
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
literal|"a"
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|" "
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|newTextField
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
name|Explanation
name|expl
init|=
name|searcher
operator|.
name|explain
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|Explanation
name|norm
init|=
name|findExplanation
argument_list|(
name|expl
argument_list|,
literal|"fieldNorm"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|norm
argument_list|)
expr_stmt|;
return|return
name|norm
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|findExplanation
specifier|private
specifier|static
name|Explanation
name|findExplanation
parameter_list|(
name|Explanation
name|expl
parameter_list|,
name|String
name|text
parameter_list|)
block|{
if|if
condition|(
name|expl
operator|.
name|getDescription
argument_list|()
operator|.
name|startsWith
argument_list|(
name|text
argument_list|)
condition|)
block|{
return|return
name|expl
return|;
block|}
else|else
block|{
for|for
control|(
name|Explanation
name|sub
range|:
name|expl
operator|.
name|getDetails
argument_list|()
control|)
block|{
name|Explanation
name|match
init|=
name|findExplanation
argument_list|(
name|sub
argument_list|,
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
return|return
name|match
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|testSweetSpotComputeNorm
specifier|public
name|void
name|testSweetSpotComputeNorm
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Similarity
name|d
init|=
operator|new
name|ClassicSimilarity
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
name|computeNorm
argument_list|(
name|d
argument_list|,
literal|"bogus"
argument_list|,
name|i
argument_list|)
argument_list|,
name|computeNorm
argument_list|(
name|s
argument_list|,
literal|"bogus"
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
argument_list|,
literal|true
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
name|computeNorm
argument_list|(
name|ss
argument_list|,
literal|"bogus"
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
specifier|final
name|float
name|normD
init|=
name|computeNorm
argument_list|(
name|d
argument_list|,
literal|"bogus"
argument_list|,
name|i
operator|-
literal|9
argument_list|)
decl_stmt|;
specifier|final
name|float
name|normS
init|=
name|computeNorm
argument_list|(
name|s
argument_list|,
literal|"bogus"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"3,10: 10<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
block|}
comment|// separate sweet spot for certain fields
specifier|final
name|SweetSpotSimilarity
name|ssBar
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssBar
operator|.
name|setLengthNormFactors
argument_list|(
literal|8
argument_list|,
literal|13
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SweetSpotSimilarity
name|ssYak
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssYak
operator|.
name|setLengthNormFactors
argument_list|(
literal|6
argument_list|,
literal|9
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SweetSpotSimilarity
name|ssA
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssA
operator|.
name|setLengthNormFactors
argument_list|(
literal|5
argument_list|,
literal|8
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SweetSpotSimilarity
name|ssB
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssB
operator|.
name|setLengthNormFactors
argument_list|(
literal|5
argument_list|,
literal|8
argument_list|,
literal|0.1f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Similarity
name|sp
init|=
operator|new
name|PerFieldSimilarityWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"bar"
argument_list|)
condition|)
return|return
name|ssBar
return|;
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"yak"
argument_list|)
condition|)
return|return
name|ssYak
return|;
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"a"
argument_list|)
condition|)
return|return
name|ssA
return|;
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"b"
argument_list|)
condition|)
return|return
name|ssB
return|;
else|else
return|return
name|ss
return|;
block|}
block|}
decl_stmt|;
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
name|computeNorm
argument_list|(
name|sp
argument_list|,
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
specifier|final
name|float
name|normD
init|=
name|computeNorm
argument_list|(
name|d
argument_list|,
literal|"foo"
argument_list|,
name|i
operator|-
literal|9
argument_list|)
decl_stmt|;
specifier|final
name|float
name|normS
init|=
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"foo"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f: 3,10: 10<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.01f
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
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"bar"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.01f
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
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"yak"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0.01f
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
specifier|final
name|float
name|normD
init|=
name|computeNorm
argument_list|(
name|d
argument_list|,
literal|"bar"
argument_list|,
name|i
operator|-
literal|12
argument_list|)
decl_stmt|;
specifier|final
name|float
name|normS
init|=
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"bar"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f: 8,13: 13<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.01f
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
specifier|final
name|float
name|normD
init|=
name|computeNorm
argument_list|(
name|d
argument_list|,
literal|"yak"
argument_list|,
name|i
operator|-
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|float
name|normS
init|=
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"yak"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f: 6,9: 9<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
block|}
comment|// steepness
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
specifier|final
name|float
name|normSS
init|=
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"a"
argument_list|,
name|i
argument_list|)
decl_stmt|;
specifier|final
name|float
name|normS
init|=
name|computeNorm
argument_list|(
name|sp
argument_list|,
literal|"b"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"s: i="
operator|+
name|i
operator|+
literal|" : a="
operator|+
name|normSS
operator|+
literal|"< b="
operator|+
name|normS
argument_list|,
name|normSS
operator|<
name|normS
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
name|TFIDFSimilarity
name|d
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
name|TFIDFSimilarity
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
annotation|@
name|Override
specifier|public
name|float
name|tf
parameter_list|(
name|float
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
name|TFIDFSimilarity
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

