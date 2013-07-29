begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|facet
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link FacetRequest} for weighting facets by summing the scores of matching  * documents.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SumScoreFacetRequest
specifier|public
class|class
name|SumScoreFacetRequest
extends|extends
name|FacetRequest
block|{
comment|/** Create a score facet request for a given node in the taxonomy. */
DECL|method|SumScoreFacetRequest
specifier|public
name|SumScoreFacetRequest
parameter_list|(
name|CategoryPath
name|path
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFacetsAggregator
specifier|public
name|FacetsAggregator
name|createFacetsAggregator
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|)
block|{
return|return
operator|new
name|SumScoreFacetsAggregator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createAggregator
specifier|public
name|Aggregator
name|createAggregator
parameter_list|(
name|boolean
name|useComplements
parameter_list|,
name|FacetArrays
name|arrays
parameter_list|,
name|TaxonomyReader
name|taxonomy
parameter_list|)
block|{
assert|assert
operator|!
name|useComplements
operator|:
literal|"complements are not supported by this FacetRequest"
assert|;
return|return
operator|new
name|ScoringAggregator
argument_list|(
name|arrays
operator|.
name|getFloatArray
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueOf
specifier|public
name|double
name|getValueOf
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|arrays
operator|.
name|getFloatArray
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getFacetArraysSource
specifier|public
name|FacetArraysSource
name|getFacetArraysSource
parameter_list|()
block|{
return|return
name|FacetArraysSource
operator|.
name|FLOAT
return|;
block|}
block|}
end_class

end_unit

