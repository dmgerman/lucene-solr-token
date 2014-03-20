begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package

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
name|document
operator|.
name|StringField
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
name|facet
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|taxonomy
operator|.
name|FastTaxonomyFacetCounts
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|RandomIndexWriter
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
name|MultiCollector
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestRandomSamplingFacetsCollector
specifier|public
class|class
name|TestRandomSamplingFacetsCollector
extends|extends
name|FacetTestCase
block|{
DECL|method|testRandomSampling
specifier|public
name|void
name|testRandomSampling
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"EvenOdd"
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
condition|?
literal|"even"
else|:
literal|"odd"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"iMod10"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|%
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
comment|// Test empty results
name|RandomSamplingFacetsCollector
name|collectRandomZeroResults
init|=
operator|new
name|RandomSamplingFacetsCollector
argument_list|(
name|numDocs
operator|/
literal|10
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
comment|// There should be no divisions by zero
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"EvenOdd"
argument_list|,
literal|"NeverMatches"
argument_list|)
argument_list|)
argument_list|,
name|collectRandomZeroResults
argument_list|)
expr_stmt|;
comment|// There should be no divisions by zero and no null result
name|assertNotNull
argument_list|(
name|collectRandomZeroResults
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// There should be no results at all
for|for
control|(
name|MatchingDocs
name|doc
range|:
name|collectRandomZeroResults
operator|.
name|getMatchingDocs
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
comment|// Now start searching and retrieve results.
comment|// Use a query to select half of the documents.
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"EvenOdd"
argument_list|,
literal|"even"
argument_list|)
argument_list|)
decl_stmt|;
comment|// there will be 5 facet values (0, 2, 4, 6 and 8), as only the even (i %
comment|// 10) are hits.
comment|// there is a REAL small chance that one of the 5 values will be missed when
comment|// sampling.
comment|// but is that 0.8 (chance not to take a value) ^ 2000 * 5 (any can be
comment|// missing) ~ 10^-193
comment|// so that is probably not going to happen.
name|int
name|maxNumChildren
init|=
literal|5
decl_stmt|;
name|RandomSamplingFacetsCollector
name|random100Percent
init|=
operator|new
name|RandomSamplingFacetsCollector
argument_list|(
name|numDocs
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
comment|// no sampling
name|RandomSamplingFacetsCollector
name|random10Percent
init|=
operator|new
name|RandomSamplingFacetsCollector
argument_list|(
name|numDocs
operator|/
literal|10
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
comment|// 10 % of total docs, 20% of the hits
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|fc
argument_list|,
name|random100Percent
argument_list|,
name|random10Percent
argument_list|)
argument_list|)
expr_stmt|;
name|FastTaxonomyFacetCounts
name|random10FacetCounts
init|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|random10Percent
argument_list|)
decl_stmt|;
name|FastTaxonomyFacetCounts
name|random100FacetCounts
init|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|random100Percent
argument_list|)
decl_stmt|;
name|FastTaxonomyFacetCounts
name|exactFacetCounts
init|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|FacetResult
name|random10Result
init|=
name|random10Percent
operator|.
name|amortizeFacetCounts
argument_list|(
name|random10FacetCounts
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"iMod10"
argument_list|)
argument_list|,
name|config
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|FacetResult
name|random100Result
init|=
name|random100FacetCounts
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"iMod10"
argument_list|)
decl_stmt|;
name|FacetResult
name|exactResult
init|=
name|exactFacetCounts
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"iMod10"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|random100Result
argument_list|,
name|exactResult
argument_list|)
expr_stmt|;
comment|// we should have five children, but there is a small chance we have less.
comment|// (see above).
name|assertTrue
argument_list|(
name|random10Result
operator|.
name|childCount
operator|<=
name|maxNumChildren
argument_list|)
expr_stmt|;
comment|// there should be one child at least.
name|assertTrue
argument_list|(
name|random10Result
operator|.
name|childCount
operator|>=
literal|1
argument_list|)
expr_stmt|;
comment|// now calculate some statistics to determine if the sampled result is 'ok'.
comment|// because random sampling is used, the results will vary each time.
name|int
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LabelAndValue
name|lav
range|:
name|random10Result
operator|.
name|labelValues
control|)
block|{
name|sum
operator|+=
name|lav
operator|.
name|value
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|float
name|mu
init|=
operator|(
name|float
operator|)
name|sum
operator|/
operator|(
name|float
operator|)
name|maxNumChildren
decl_stmt|;
name|float
name|variance
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LabelAndValue
name|lav
range|:
name|random10Result
operator|.
name|labelValues
control|)
block|{
name|variance
operator|+=
name|Math
operator|.
name|pow
argument_list|(
operator|(
name|mu
operator|-
name|lav
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
name|variance
operator|=
name|variance
operator|/
name|maxNumChildren
expr_stmt|;
name|float
name|sigma
init|=
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|variance
argument_list|)
decl_stmt|;
comment|// we query only half the documents and have 5 categories. The average
comment|// number of docs in a category will thus be the total divided by 5*2
name|float
name|targetMu
init|=
name|numDocs
operator|/
operator|(
literal|5.0f
operator|*
literal|2.0f
operator|)
decl_stmt|;
comment|// the average should be in the range and the standard deviation should not
comment|// be too great
name|assertTrue
argument_list|(
name|sigma
operator|<
literal|200
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|targetMu
operator|-
literal|3
operator|*
name|sigma
operator|<
name|mu
operator|&&
name|mu
operator|<
name|targetMu
operator|+
literal|3
operator|*
name|sigma
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|taxoReader
argument_list|,
name|dir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

