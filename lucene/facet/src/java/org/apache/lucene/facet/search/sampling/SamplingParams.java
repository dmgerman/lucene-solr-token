begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.sampling
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
operator|.
name|sampling
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Parameters for sampling, dictating whether sampling is to take place and how.   *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SamplingParams
specifier|public
class|class
name|SamplingParams
block|{
comment|/**    * Default factor by which more results are requested over the sample set.    * @see SamplingParams#getOversampleFactor()    */
DECL|field|DEFAULT_OVERSAMPLE_FACTOR
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_OVERSAMPLE_FACTOR
init|=
literal|2d
decl_stmt|;
comment|/**    * Default ratio between size of sample to original size of document set.    * @see Sampler#getSampleSet(org.apache.lucene.facet.search.ScoredDocIDs)    */
DECL|field|DEFAULT_SAMPLE_RATIO
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_SAMPLE_RATIO
init|=
literal|0.01
decl_stmt|;
comment|/**    * Default maximum size of sample.    * @see Sampler#getSampleSet(org.apache.lucene.facet.search.ScoredDocIDs)    */
DECL|field|DEFAULT_MAX_SAMPLE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_SAMPLE_SIZE
init|=
literal|10000
decl_stmt|;
comment|/**    * Default minimum size of sample.    * @see Sampler#getSampleSet(org.apache.lucene.facet.search.ScoredDocIDs)    */
DECL|field|DEFAULT_MIN_SAMPLE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_SAMPLE_SIZE
init|=
literal|100
decl_stmt|;
comment|/**    * Default sampling threshold, if number of results is less than this number - no sampling will take place    * @see SamplingParams#getSampleRatio()    */
DECL|field|DEFAULT_SAMPLING_THRESHOLD
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SAMPLING_THRESHOLD
init|=
literal|75000
decl_stmt|;
DECL|field|maxSampleSize
specifier|private
name|int
name|maxSampleSize
init|=
name|DEFAULT_MAX_SAMPLE_SIZE
decl_stmt|;
DECL|field|minSampleSize
specifier|private
name|int
name|minSampleSize
init|=
name|DEFAULT_MIN_SAMPLE_SIZE
decl_stmt|;
DECL|field|sampleRatio
specifier|private
name|double
name|sampleRatio
init|=
name|DEFAULT_SAMPLE_RATIO
decl_stmt|;
DECL|field|samplingThreshold
specifier|private
name|int
name|samplingThreshold
init|=
name|DEFAULT_SAMPLING_THRESHOLD
decl_stmt|;
DECL|field|oversampleFactor
specifier|private
name|double
name|oversampleFactor
init|=
name|DEFAULT_OVERSAMPLE_FACTOR
decl_stmt|;
comment|/**    * Return the maxSampleSize.    * In no case should the resulting sample size exceed this value.      * @see Sampler#getSampleSet(org.apache.lucene.facet.search.ScoredDocIDs)    */
DECL|method|getMaxSampleSize
specifier|public
specifier|final
name|int
name|getMaxSampleSize
parameter_list|()
block|{
return|return
name|maxSampleSize
return|;
block|}
comment|/**    * Return the minSampleSize.    * In no case should the resulting sample size be smaller than this value.      * @see Sampler#getSampleSet(org.apache.lucene.facet.search.ScoredDocIDs)    */
DECL|method|getMinSampleSize
specifier|public
specifier|final
name|int
name|getMinSampleSize
parameter_list|()
block|{
return|return
name|minSampleSize
return|;
block|}
comment|/**    * @return the sampleRatio    * @see Sampler#getSampleSet(org.apache.lucene.facet.search.ScoredDocIDs)    */
DECL|method|getSampleRatio
specifier|public
specifier|final
name|double
name|getSampleRatio
parameter_list|()
block|{
return|return
name|sampleRatio
return|;
block|}
comment|/**    * Return the samplingThreshold.    * Sampling would be performed only for document sets larger than this.      */
DECL|method|getSamplingThreshold
specifier|public
specifier|final
name|int
name|getSamplingThreshold
parameter_list|()
block|{
return|return
name|samplingThreshold
return|;
block|}
comment|/**    * @param maxSampleSize    *          the maxSampleSize to set    * @see #getMaxSampleSize()    */
DECL|method|setMaxSampleSize
specifier|public
name|void
name|setMaxSampleSize
parameter_list|(
name|int
name|maxSampleSize
parameter_list|)
block|{
name|this
operator|.
name|maxSampleSize
operator|=
name|maxSampleSize
expr_stmt|;
block|}
comment|/**    * @param minSampleSize    *          the minSampleSize to set    * @see #getMinSampleSize()    */
DECL|method|setMinSampleSize
specifier|public
name|void
name|setMinSampleSize
parameter_list|(
name|int
name|minSampleSize
parameter_list|)
block|{
name|this
operator|.
name|minSampleSize
operator|=
name|minSampleSize
expr_stmt|;
block|}
comment|/**    * @param sampleRatio    *          the sampleRatio to set    * @see #getSampleRatio()    */
DECL|method|setSampleRatio
specifier|public
name|void
name|setSampleRatio
parameter_list|(
name|double
name|sampleRatio
parameter_list|)
block|{
name|this
operator|.
name|sampleRatio
operator|=
name|sampleRatio
expr_stmt|;
block|}
comment|/**    * Set a sampling-threshold    * @see #getSamplingThreshold()    */
DECL|method|setSampingThreshold
specifier|public
name|void
name|setSampingThreshold
parameter_list|(
name|int
name|sampingThreshold
parameter_list|)
block|{
name|this
operator|.
name|samplingThreshold
operator|=
name|sampingThreshold
expr_stmt|;
block|}
comment|/**    * Check validity of sampling settings, making sure that    *<ul>    *<li><code>minSampleSize<= maxSampleSize<= samplingThreshold</code></li>    *<li><code>0< samplingRatio<= 1</code></li>    *</ul>     *     * @return true if valid, false otherwise    */
DECL|method|validate
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
return|return
name|samplingThreshold
operator|>=
name|maxSampleSize
operator|&&
name|maxSampleSize
operator|>=
name|minSampleSize
operator|&&
name|sampleRatio
operator|>
literal|0
operator|&&
name|sampleRatio
operator|<
literal|1
return|;
block|}
comment|/**    * Return the oversampleFactor. When sampling, we would collect that much more    * results, so that later, when selecting top out of these, chances are higher    * to get actual best results. Note that having this value larger than 1 only    * makes sense when using a SampleFixer which finds accurate results, such as    *<code>TakmiSampleFixer</code>. When this value is smaller than 1, it is    * ignored and no oversampling takes place.    */
DECL|method|getOversampleFactor
specifier|public
specifier|final
name|double
name|getOversampleFactor
parameter_list|()
block|{
return|return
name|oversampleFactor
return|;
block|}
comment|/**    * @param oversampleFactor the oversampleFactor to set    * @see #getOversampleFactor()    */
DECL|method|setOversampleFactor
specifier|public
name|void
name|setOversampleFactor
parameter_list|(
name|double
name|oversampleFactor
parameter_list|)
block|{
name|this
operator|.
name|oversampleFactor
operator|=
name|oversampleFactor
expr_stmt|;
block|}
block|}
end_class

end_unit

