begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*  * Forked from https://github.com/codahale/metrics  */
end_comment

begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|sqrt
import|;
end_import

begin_comment
comment|/**  * A metric which calculates the distribution of a value.  *  * @see<a href="http://www.johndcook.com/standard_deviation.html">Accurately computing running  *      variance</a>  */
end_comment

begin_class
DECL|class|Histogram
specifier|public
class|class
name|Histogram
block|{
DECL|field|DEFAULT_SAMPLE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SAMPLE_SIZE
init|=
literal|1028
decl_stmt|;
DECL|field|DEFAULT_ALPHA
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_ALPHA
init|=
literal|0.015
decl_stmt|;
comment|/**    * The type of sampling the histogram should be performing.    */
DECL|enum|SampleType
enum|enum
name|SampleType
block|{
comment|/**      * Uses a uniform sample of 1028 elements, which offers a 99.9% confidence level with a 5%      * margin of error assuming a normal distribution.      */
DECL|enum constant|UNIFORM
name|UNIFORM
block|{
annotation|@
name|Override
specifier|public
name|Sample
name|newSample
parameter_list|()
block|{
return|return
operator|new
name|UniformSample
argument_list|(
name|DEFAULT_SAMPLE_SIZE
argument_list|)
return|;
block|}
block|}
block|,
comment|/**      * Uses an exponentially decaying sample of 1028 elements, which offers a 99.9% confidence      * level with a 5% margin of error assuming a normal distribution, and an alpha factor of      * 0.015, which heavily biases the sample to the past 5 minutes of measurements.      */
DECL|enum constant|BIASED
name|BIASED
block|{
annotation|@
name|Override
specifier|public
name|Sample
name|newSample
parameter_list|()
block|{
return|return
operator|new
name|ExponentiallyDecayingSample
argument_list|(
name|DEFAULT_SAMPLE_SIZE
argument_list|,
name|DEFAULT_ALPHA
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|newSample
specifier|public
specifier|abstract
name|Sample
name|newSample
parameter_list|()
function_decl|;
block|}
DECL|field|sample
specifier|private
specifier|final
name|Sample
name|sample
decl_stmt|;
DECL|field|min
specifier|private
specifier|final
name|AtomicLong
name|min
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|max
specifier|private
specifier|final
name|AtomicLong
name|max
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|sum
specifier|private
specifier|final
name|AtomicLong
name|sum
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// These are for the Welford algorithm for calculating running variance
comment|// without floating-point doom.
DECL|field|variance
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|double
index|[]
argument_list|>
name|variance
init|=
operator|new
name|AtomicReference
argument_list|<
name|double
index|[]
argument_list|>
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|1
block|,
literal|0
block|}
argument_list|)
decl_stmt|;
comment|// M, S
DECL|field|count
specifier|private
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@link Histogram} with the given sample type.    *    * @param type the type of sample to use    */
DECL|method|Histogram
name|Histogram
parameter_list|(
name|SampleType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|type
operator|.
name|newSample
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link Histogram} with the given sample.    *    * @param sample the sample to create a histogram from    */
DECL|method|Histogram
name|Histogram
parameter_list|(
name|Sample
name|sample
parameter_list|)
block|{
name|this
operator|.
name|sample
operator|=
name|sample
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Clears all recorded values.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|sample
operator|.
name|clear
argument_list|()
expr_stmt|;
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|max
operator|.
name|set
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|min
operator|.
name|set
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|sum
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|variance
operator|.
name|set
argument_list|(
operator|new
name|double
index|[]
block|{
operator|-
literal|1
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a recorded value.    *    * @param value the length of the value    */
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|update
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a recorded value.    *    * @param value the length of the value    */
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|sample
operator|.
name|update
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|setMax
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|setMin
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sum
operator|.
name|getAndAdd
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|updateVariance
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of values recorded.    *    * @return the number of values recorded    */
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
operator|.
name|get
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)    * @see com.yammer.metrics.core.Summarizable#max()    */
DECL|method|getMax
specifier|public
name|double
name|getMax
parameter_list|()
block|{
if|if
condition|(
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|max
operator|.
name|get
argument_list|()
return|;
block|}
return|return
literal|0.0
return|;
block|}
comment|/* (non-Javadoc)    * @see com.yammer.metrics.core.Summarizable#min()    */
DECL|method|getMin
specifier|public
name|double
name|getMin
parameter_list|()
block|{
if|if
condition|(
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|min
operator|.
name|get
argument_list|()
return|;
block|}
return|return
literal|0.0
return|;
block|}
comment|/* (non-Javadoc)    * @see com.yammer.metrics.core.Summarizable#mean()    */
DECL|method|getMean
specifier|public
name|double
name|getMean
parameter_list|()
block|{
if|if
condition|(
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|sum
operator|.
name|get
argument_list|()
operator|/
operator|(
name|double
operator|)
name|getCount
argument_list|()
return|;
block|}
return|return
literal|0.0
return|;
block|}
comment|/* (non-Javadoc)    * @see com.yammer.metrics.core.Summarizable#stdDev()    */
DECL|method|getStdDev
specifier|public
name|double
name|getStdDev
parameter_list|()
block|{
if|if
condition|(
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|sqrt
argument_list|(
name|getVariance
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|0.0
return|;
block|}
comment|/* (non-Javadoc)    * @see com.yammer.metrics.core.Summarizable#sum()    */
DECL|method|getSum
specifier|public
name|double
name|getSum
parameter_list|()
block|{
return|return
operator|(
name|double
operator|)
name|sum
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getSnapshot
specifier|public
name|Snapshot
name|getSnapshot
parameter_list|()
block|{
return|return
name|sample
operator|.
name|getSnapshot
argument_list|()
return|;
block|}
DECL|method|getVariance
specifier|private
name|double
name|getVariance
parameter_list|()
block|{
if|if
condition|(
name|getCount
argument_list|()
operator|<=
literal|1
condition|)
block|{
return|return
literal|0.0
return|;
block|}
return|return
name|variance
operator|.
name|get
argument_list|()
index|[
literal|1
index|]
operator|/
operator|(
name|getCount
argument_list|()
operator|-
literal|1
operator|)
return|;
block|}
DECL|method|setMax
specifier|private
name|void
name|setMax
parameter_list|(
name|long
name|potentialMax
parameter_list|)
block|{
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
specifier|final
name|long
name|currentMax
init|=
name|max
operator|.
name|get
argument_list|()
decl_stmt|;
name|done
operator|=
name|currentMax
operator|>=
name|potentialMax
operator|||
name|max
operator|.
name|compareAndSet
argument_list|(
name|currentMax
argument_list|,
name|potentialMax
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setMin
specifier|private
name|void
name|setMin
parameter_list|(
name|long
name|potentialMin
parameter_list|)
block|{
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
specifier|final
name|long
name|currentMin
init|=
name|min
operator|.
name|get
argument_list|()
decl_stmt|;
name|done
operator|=
name|currentMin
operator|<=
name|potentialMin
operator|||
name|min
operator|.
name|compareAndSet
argument_list|(
name|currentMin
argument_list|,
name|potentialMin
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateVariance
specifier|private
name|void
name|updateVariance
parameter_list|(
name|long
name|value
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|double
index|[]
name|oldValues
init|=
name|variance
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|double
index|[]
name|newValues
init|=
operator|new
name|double
index|[
literal|2
index|]
decl_stmt|;
if|if
condition|(
name|oldValues
index|[
literal|0
index|]
operator|==
operator|-
literal|1
condition|)
block|{
name|newValues
index|[
literal|0
index|]
operator|=
name|value
expr_stmt|;
name|newValues
index|[
literal|1
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|double
name|oldM
init|=
name|oldValues
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|double
name|oldS
init|=
name|oldValues
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|double
name|newM
init|=
name|oldM
operator|+
operator|(
operator|(
name|value
operator|-
name|oldM
operator|)
operator|/
name|getCount
argument_list|()
operator|)
decl_stmt|;
specifier|final
name|double
name|newS
init|=
name|oldS
operator|+
operator|(
operator|(
name|value
operator|-
name|oldM
operator|)
operator|*
operator|(
name|value
operator|-
name|newM
operator|)
operator|)
decl_stmt|;
name|newValues
index|[
literal|0
index|]
operator|=
name|newM
expr_stmt|;
name|newValues
index|[
literal|1
index|]
operator|=
name|newS
expr_stmt|;
block|}
if|if
condition|(
name|variance
operator|.
name|compareAndSet
argument_list|(
name|oldValues
argument_list|,
name|newValues
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
end_class

end_unit

