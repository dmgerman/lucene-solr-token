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
name|search
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
name|results
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
name|results
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
name|sampling
operator|.
name|RandomSampler
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
name|sampling
operator|.
name|Sampler
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
name|sampling
operator|.
name|SamplingAccumulator
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * {@link FacetsAccumulator} whose behavior regarding complements, sampling,  * etc. is not set up front but rather is determined at accumulation time  * according to the statistics of the accumulated set of documents and the  * index.  *<p>  * Note: Sampling accumulation (Accumulation over a sampled-set of the results),  * does not guarantee accurate values for  * {@link FacetResult#getNumValidDescendants()}&  * {@link FacetResultNode#getResidue()}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|AdaptiveFacetsAccumulator
specifier|public
specifier|final
class|class
name|AdaptiveFacetsAccumulator
extends|extends
name|StandardFacetsAccumulator
block|{
DECL|field|sampler
specifier|private
name|Sampler
name|sampler
init|=
operator|new
name|RandomSampler
argument_list|()
decl_stmt|;
comment|/**    * Create an {@link AdaptiveFacetsAccumulator}     * @see StandardFacetsAccumulator#StandardFacetsAccumulator(FacetSearchParams, IndexReader, TaxonomyReader)    */
DECL|method|AdaptiveFacetsAccumulator
specifier|public
name|AdaptiveFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|searchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
name|super
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an {@link AdaptiveFacetsAccumulator}    *     * @see StandardFacetsAccumulator#StandardFacetsAccumulator(FacetSearchParams,    *      IndexReader, TaxonomyReader, FacetArrays)    */
DECL|method|AdaptiveFacetsAccumulator
specifier|public
name|AdaptiveFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|searchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|super
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the sampler.    * @param sampler sampler to set    */
DECL|method|setSampler
specifier|public
name|void
name|setSampler
parameter_list|(
name|Sampler
name|sampler
parameter_list|)
block|{
name|this
operator|.
name|sampler
operator|=
name|sampler
expr_stmt|;
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
name|ScoredDocIDs
name|docids
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetsAccumulator
name|delegee
init|=
name|appropriateFacetCountingAccumulator
argument_list|(
name|docids
argument_list|)
decl_stmt|;
if|if
condition|(
name|delegee
operator|==
name|this
condition|)
block|{
return|return
name|super
operator|.
name|accumulate
argument_list|(
name|docids
argument_list|)
return|;
block|}
return|return
name|delegee
operator|.
name|accumulate
argument_list|(
name|docids
argument_list|)
return|;
block|}
comment|/**    * Compute the appropriate facet accumulator to use.    * If no special/clever adaptation is possible/needed return this (self).    */
DECL|method|appropriateFacetCountingAccumulator
specifier|private
name|FacetsAccumulator
name|appropriateFacetCountingAccumulator
parameter_list|(
name|ScoredDocIDs
name|docids
parameter_list|)
block|{
comment|// Verify that searchPareams permit sampling/complement/etc... otherwise do default
if|if
condition|(
operator|!
name|mayComplement
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
comment|// Now we're sure we can use the sampling methods as we're in a counting only mode
comment|// Verify that sampling is enabled and required ... otherwise do default
if|if
condition|(
name|sampler
operator|==
literal|null
operator|||
operator|!
name|sampler
operator|.
name|shouldSample
argument_list|(
name|docids
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
name|SamplingAccumulator
name|samplingAccumulator
init|=
operator|new
name|SamplingAccumulator
argument_list|(
name|sampler
argument_list|,
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|)
decl_stmt|;
name|samplingAccumulator
operator|.
name|setComplementThreshold
argument_list|(
name|getComplementThreshold
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|samplingAccumulator
return|;
block|}
comment|/**    * @return the sampler in effect    */
DECL|method|getSampler
specifier|public
specifier|final
name|Sampler
name|getSampler
parameter_list|()
block|{
return|return
name|sampler
return|;
block|}
block|}
end_class

end_unit

