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
name|document
operator|.
name|DoubleDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
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
name|FloatDocValuesField
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|FacetLabel
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
name|DoubleFieldSource
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
name|FloatFieldSource
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/** {@link Facets} implementation that computes counts for  *  dynamic double ranges from a provided {@link  *  ValueSource}, using {@link FunctionValues#doubleVal}.  Use  *  this for dimensions that change in real-time (e.g. a  *  relative time based dimension like "Past day", "Past 2  *  days", etc.) or that change for each user (e.g. a  *  distance dimension like "< 1 km", "< 2 km", etc.).  *  *<p> If you had indexed your field using {@link  *  FloatDocValuesField} then pass {@link FloatFieldSource}  *  as the {@link ValueSource}; if you used {@link  *  DoubleDocValuesField} then pass {@link  *  DoubleFieldSource} (this is the default used when you  *  pass just a the field name).  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|DoubleRangeFacetCounts
specifier|public
class|class
name|DoubleRangeFacetCounts
extends|extends
name|Facets
block|{
DECL|field|ranges
specifier|private
specifier|final
name|DoubleRange
index|[]
name|ranges
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|totCount
specifier|private
name|int
name|totCount
decl_stmt|;
comment|/** Create {@code RangeFacetCounts}, using {@link    *  DoubleFieldSource} from the specified field. */
DECL|method|DoubleRangeFacetCounts
specifier|public
name|DoubleRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|DoubleRange
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
name|DoubleFieldSource
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
DECL|method|DoubleRangeFacetCounts
specifier|public
name|DoubleRangeFacetCounts
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
name|DoubleRange
modifier|...
name|ranges
parameter_list|)
throws|throws
name|IOException
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
name|counts
operator|=
operator|new
name|int
index|[
name|ranges
operator|.
name|length
index|]
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
name|double
name|v
init|=
name|fv
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
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
comment|// nocommit all args are ... unused ... this doesn't "fit"
comment|// very well:
annotation|@
name|Override
DECL|method|getTopChildren
specifier|public
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
if|if
condition|(
name|dim
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid dim \""
operator|+
name|dim
operator|+
literal|"\"; should be \""
operator|+
name|field
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|LabelAndValue
index|[]
name|labelValues
init|=
operator|new
name|LabelAndValue
index|[
name|counts
operator|.
name|length
index|]
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
name|counts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// nocommit can we add the range into this?
name|labelValues
index|[
name|i
index|]
operator|=
operator|new
name|LabelAndValue
argument_list|(
name|ranges
index|[
name|i
index|]
operator|.
name|label
argument_list|,
name|counts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FacetResult
argument_list|(
name|totCount
argument_list|,
name|labelValues
argument_list|,
name|labelValues
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSpecificValue
specifier|public
name|Number
name|getSpecificValue
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit we could impl this?
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getAllDims
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getAllDims
parameter_list|(
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|getTopChildren
argument_list|(
name|topN
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

