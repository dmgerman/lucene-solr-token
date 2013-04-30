begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.range
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|range
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
name|facet
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|search
operator|.
name|FacetRequest
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
name|search
operator|.
name|FacetResult
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
name|search
operator|.
name|FacetResultNode
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
name|search
operator|.
name|FacetsAccumulator
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
name|search
operator|.
name|FacetsAggregator
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
name|search
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
name|CategoryPath
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|NumericDocValues
import|;
end_import

begin_comment
comment|/** Uses a {@link NumericDocValues} and accumulates  *  counts for provided ranges.  This is dynamic (does not  *  use the taxonomy index or anything from the index  *  except the NumericDocValuesField). */
end_comment

begin_class
DECL|class|RangeAccumulator
specifier|public
class|class
name|RangeAccumulator
extends|extends
name|FacetsAccumulator
block|{
DECL|class|RangeSet
specifier|static
class|class
name|RangeSet
block|{
DECL|field|ranges
specifier|final
name|Range
index|[]
name|ranges
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|RangeSet
specifier|public
name|RangeSet
parameter_list|(
name|Range
index|[]
name|ranges
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
block|}
DECL|field|requests
specifier|final
name|List
argument_list|<
name|RangeSet
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<
name|RangeSet
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|RangeAccumulator
specifier|public
name|RangeAccumulator
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|fsp
argument_list|,
name|reader
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|fr
operator|instanceof
name|RangeFacetRequest
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"only RangeFacetRequest is supported; got "
operator|+
name|fsp
operator|.
name|facetRequests
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|fr
operator|.
name|categoryPath
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"only flat (dimension only) CategoryPath is allowed"
argument_list|)
throw|;
block|}
name|RangeFacetRequest
argument_list|<
name|?
argument_list|>
name|rfr
init|=
operator|(
name|RangeFacetRequest
operator|)
name|fr
decl_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|RangeSet
argument_list|(
name|rfr
operator|.
name|ranges
argument_list|,
name|rfr
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAggregator
specifier|public
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|accumulate
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|accumulate
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: test if this is faster (in the past it was
comment|// faster to do MachingDocs on the inside) ... see
comment|// patches on LUCENE-4965):
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
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
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RangeSet
name|ranges
init|=
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|ranges
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|NumericDocValues
name|ndv
init|=
name|hits
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|ranges
operator|.
name|field
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
name|long
name|v
init|=
name|ndv
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
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
operator|.
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
name|doc
operator|++
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|FacetResultNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|(
name|ranges
operator|.
name|ranges
operator|.
name|length
argument_list|)
decl_stmt|;
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
name|ranges
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|RangeFacetResultNode
argument_list|(
name|ranges
operator|.
name|field
argument_list|,
name|ranges
operator|.
name|ranges
index|[
name|j
index|]
argument_list|,
name|counts
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FacetResultNode
name|rootNode
init|=
operator|new
name|FacetResultNode
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|label
operator|=
operator|new
name|CategoryPath
argument_list|(
name|ranges
operator|.
name|field
argument_list|)
expr_stmt|;
name|rootNode
operator|.
name|subResults
operator|=
name|nodes
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|FacetResult
argument_list|(
name|searchParams
operator|.
name|facetRequests
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|rootNode
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
annotation|@
name|Override
DECL|method|requiresDocScores
specifier|protected
name|boolean
name|requiresDocScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

