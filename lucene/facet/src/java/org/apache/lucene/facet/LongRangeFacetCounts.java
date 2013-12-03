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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LongFieldSource
import|;
end_import

begin_comment
comment|/** {@link Facets} implementation that computes counts for  *  dynamic long ranges from a provided {@link ValueSource},  *  using {@link FunctionValues#longVal}.  Use  *  this for dimensions that change in real-time (e.g. a  *  relative time based dimension like "Past day", "Past 2  *  days", etc.) or that change for each user (e.g. a  *  distance dimension like "< 1 km", "< 2 km", etc.).  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|LongRangeFacetCounts
specifier|public
class|class
name|LongRangeFacetCounts
extends|extends
name|RangeFacetCounts
block|{
comment|/** Create {@code LongRangeFacetCounts}, using {@link    *  LongFieldSource} from the specified field. */
DECL|method|LongRangeFacetCounts
specifier|public
name|LongRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|LongRange
modifier|...
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|new
name|LongFieldSource
argument_list|(
name|field
argument_list|)
argument_list|,
name|hits
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
block|}
comment|/** Create {@code RangeFacetCounts}, using the provided    *  {@link ValueSource}. */
DECL|method|LongRangeFacetCounts
specifier|public
name|LongRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|LongRange
modifier|...
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|field
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
name|count
argument_list|(
name|valueSource
argument_list|,
name|hits
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|count
specifier|private
name|void
name|count
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|,
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|LongRange
index|[]
name|ranges
init|=
operator|(
name|LongRange
index|[]
operator|)
name|this
operator|.
name|ranges
decl_stmt|;
comment|// Compute min& max over all ranges:
name|long
name|minIncl
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxIncl
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|LongRange
name|range
range|:
name|ranges
control|)
block|{
name|minIncl
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minIncl
argument_list|,
name|range
operator|.
name|minIncl
argument_list|)
expr_stmt|;
name|maxIncl
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxIncl
argument_list|,
name|range
operator|.
name|maxIncl
argument_list|)
expr_stmt|;
block|}
comment|// TODO: test if this is faster (in the past it was
comment|// faster to do MatchingDocs on the inside) ... see
comment|// patches on LUCENE-4965):
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|FunctionValues
name|fv
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|hits
operator|.
name|context
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|hits
operator|.
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
name|totCount
operator|+=
name|hits
operator|.
name|totalHits
expr_stmt|;
while|while
condition|(
name|doc
operator|<
name|length
operator|&&
operator|(
name|doc
operator|=
name|hits
operator|.
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Skip missing docs:
if|if
condition|(
name|fv
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|long
name|v
init|=
name|fv
operator|.
name|longVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
argument_list|<
name|minIncl
operator|||
name|v
argument_list|>
name|maxIncl
condition|)
block|{
name|doc
operator|++
expr_stmt|;
continue|continue;
block|}
comment|// TODO: if all ranges are non-overlapping, we
comment|// should instead do a bin-search up front
comment|// (really, a specialized case of the interval
comment|// tree)
comment|// TODO: use interval tree instead of linear search:
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ranges
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|ranges
index|[
name|j
index|]
operator|.
name|accept
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|counts
index|[
name|j
index|]
operator|++
expr_stmt|;
block|}
block|}
block|}
name|doc
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

