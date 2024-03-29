begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.accumulator.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|accumulator
operator|.
name|facet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|statistics
operator|.
name|StatsCollector
import|;
end_import

begin_comment
comment|/**  * An Accumulator that manages a certain range of a given range facet.  */
end_comment

begin_class
DECL|class|RangeFacetAccumulator
specifier|public
class|class
name|RangeFacetAccumulator
extends|extends
name|QueryFacetAccumulator
block|{
DECL|method|RangeFacetAccumulator
specifier|public
name|RangeFacetAccumulator
parameter_list|(
name|FacetValueAccumulator
name|parent
parameter_list|,
name|String
name|facetName
parameter_list|,
name|String
name|facetValue
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|facetName
argument_list|,
name|facetValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tell the FacetingAccumulator to collect the doc with the     * given rangeFacet and range.    */
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|parent
operator|.
name|collectRange
argument_list|(
name|doc
argument_list|,
name|facetName
argument_list|,
name|facetValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the readers of the rangeFacet {@link StatsCollector}s in FacetingAccumulator    */
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|parent
operator|.
name|setRangeStatsCollectorReaders
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

