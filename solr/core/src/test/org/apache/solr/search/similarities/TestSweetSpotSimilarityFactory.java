begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
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
name|misc
operator|.
name|SweetSpotSimilarity
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
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Tests {@link SweetSpotSimilarityFactory}  */
end_comment

begin_class
DECL|class|TestSweetSpotSimilarityFactory
specifier|public
class|class
name|TestSweetSpotSimilarityFactory
extends|extends
name|BaseSimilarityTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-sweetspot.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|computeNorm
specifier|private
specifier|static
name|float
name|computeNorm
parameter_list|(
name|Similarity
name|sim
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
name|newDirectory
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
literal|"foo"
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
literal|"foo"
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
comment|/** default parameters */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|SweetSpotSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// SSS tf w/defaults should behave just like DS
name|ClassicSimilarity
name|d
init|=
operator|new
name|ClassicSimilarity
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
operator|<=
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
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|// default norm sanity check
name|assertEquals
argument_list|(
literal|"norm 1"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 4"
argument_list|,
literal|0.50F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 16"
argument_list|,
literal|0.25F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|16
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|/** baseline with parameters */
DECL|method|testBaselineParameters
specifier|public
name|void
name|testBaselineParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|SweetSpotSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_baseline"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|ClassicSimilarity
name|d
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
comment|// constant up to 6
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
literal|"tf i="
operator|+
name|i
argument_list|,
literal|1.5F
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|// less then default sim above 6
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
name|sim
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
name|sim
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
comment|// norms: plateau from 3-5
name|assertEquals
argument_list|(
literal|"norm 1 == 7"
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|1
argument_list|)
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|7
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 2 == 6"
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|1
argument_list|)
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|7
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 3"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 4"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 5"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 6 too high: "
operator|+
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|6
argument_list|)
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|6
argument_list|)
operator|<
literal|1.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 7 higher then norm 6"
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|7
argument_list|)
operator|<
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 20"
argument_list|,
literal|0.25F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|20
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|/** hyperbolic with parameters */
DECL|method|testHyperbolicParameters
specifier|public
name|void
name|testHyperbolicParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|SweetSpotSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_hyperbolic"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
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
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|3.3F
operator|<=
name|sim
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
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<=
literal|7.7F
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"MID tf"
argument_list|,
literal|3.3F
operator|+
operator|(
literal|7.7F
operator|-
literal|3.3F
operator|)
operator|/
literal|2.0F
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|0.00001F
argument_list|)
expr_stmt|;
comment|// norms: plateau from 1-5, shallow slope
name|assertEquals
argument_list|(
literal|"norm 1"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 2"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 3"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 4"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 5"
argument_list|,
literal|1.00F
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 6 too high: "
operator|+
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|6
argument_list|)
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|6
argument_list|)
operator|<
literal|1.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 7 higher then norm 6"
argument_list|,
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|7
argument_list|)
operator|<
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 20 not high enough: "
operator|+
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|20
argument_list|)
argument_list|,
literal|0.25F
operator|<
name|computeNorm
argument_list|(
name|sim
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

