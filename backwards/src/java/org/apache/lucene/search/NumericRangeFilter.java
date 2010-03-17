begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|NumericTokenStream
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|NumericField
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|NumericUtils
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_comment
comment|/**  * A {@link Filter} that only accepts numeric values within  * a specified range. To use this, you must first index the  * numeric values using {@link NumericField} (expert: {@link  * NumericTokenStream}).  *  *<p>You create a new NumericRangeFilter with the static  * factory methods, eg:  *  *<pre>  * Filter f = NumericRangeFilter.newFloatRange("weight", 0.3f, 0.10f, true, true);  *</pre>  *  * accepts all documents whose float valued "weight" field  * ranges from 0.3 to 0.10, inclusive.  * See {@link NumericRangeQuery} for details on how Lucene  * indexes and searches numeric valued fields.  *  *<p><font color="red"><b>NOTE:</b> This API is experimental and  * might change in incompatible ways in the next  * release.</font>  *  * @since 2.9  **/
end_comment

begin_class
DECL|class|NumericRangeFilter
specifier|public
specifier|final
class|class
name|NumericRangeFilter
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|MultiTermQueryWrapperFilter
argument_list|<
name|NumericRangeQuery
argument_list|<
name|T
argument_list|>
argument_list|>
block|{
DECL|method|NumericRangeFilter
specifier|private
name|NumericRangeFilter
parameter_list|(
specifier|final
name|NumericRangeQuery
argument_list|<
name|T
argument_list|>
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that filters a<code>long</code>    * range using the given<a href="NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newLongRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Long
argument_list|>
name|newLongRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Long
name|min
parameter_list|,
name|Long
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Long
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that queries a<code>long</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newLongRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Long
argument_list|>
name|newLongRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Long
name|min
parameter_list|,
name|Long
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Long
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that filters a<code>int</code>    * range using the given<a href="NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newIntRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Integer
argument_list|>
name|newIntRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Integer
name|min
parameter_list|,
name|Integer
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that queries a<code>int</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newIntRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Integer
argument_list|>
name|newIntRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Integer
name|min
parameter_list|,
name|Integer
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that filters a<code>double</code>    * range using the given<a href="NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newDoubleRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Double
argument_list|>
name|newDoubleRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Double
name|min
parameter_list|,
name|Double
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Double
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that queries a<code>double</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newDoubleRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Double
argument_list|>
name|newDoubleRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Double
name|min
parameter_list|,
name|Double
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Double
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that filters a<code>float</code>    * range using the given<a href="NumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>.    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newFloatRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Float
argument_list|>
name|newFloatRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|Float
name|min
parameter_list|,
name|Float
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Float
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newFloatRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a<code>NumericRangeFilter</code>, that queries a<code>float</code>    * range using the default<code>precisionStep</code> {@link NumericUtils#PRECISION_STEP_DEFAULT} (4).    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the min or max value to<code>null</code>. By setting inclusive to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    */
DECL|method|newFloatRange
specifier|public
specifier|static
name|NumericRangeFilter
argument_list|<
name|Float
argument_list|>
name|newFloatRange
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|Float
name|min
parameter_list|,
name|Float
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
operator|new
name|NumericRangeFilter
argument_list|<
name|Float
argument_list|>
argument_list|(
name|NumericRangeQuery
operator|.
name|newFloatRange
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns the field name for this filter */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|query
operator|.
name|getField
argument_list|()
return|;
block|}
comment|/** Returns<code>true</code> if the lower endpoint is inclusive */
DECL|method|includesMin
specifier|public
name|boolean
name|includesMin
parameter_list|()
block|{
return|return
name|query
operator|.
name|includesMin
argument_list|()
return|;
block|}
comment|/** Returns<code>true</code> if the upper endpoint is inclusive */
DECL|method|includesMax
specifier|public
name|boolean
name|includesMax
parameter_list|()
block|{
return|return
name|query
operator|.
name|includesMax
argument_list|()
return|;
block|}
comment|/** Returns the lower value of this range filter */
DECL|method|getMin
specifier|public
name|T
name|getMin
parameter_list|()
block|{
return|return
name|query
operator|.
name|getMin
argument_list|()
return|;
block|}
comment|/** Returns the upper value of this range filter */
DECL|method|getMax
specifier|public
name|T
name|getMax
parameter_list|()
block|{
return|return
name|query
operator|.
name|getMax
argument_list|()
return|;
block|}
block|}
end_class

end_unit

