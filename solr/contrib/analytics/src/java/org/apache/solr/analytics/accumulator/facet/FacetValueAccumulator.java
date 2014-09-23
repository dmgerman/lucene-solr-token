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

begin_comment
comment|/**  * Interface that describes the methods needed for an Accumulator to be able to handle   * fieldFacets, rangeFacets and queryFacets.  */
end_comment

begin_interface
DECL|interface|FacetValueAccumulator
specifier|public
interface|interface
name|FacetValueAccumulator
block|{
DECL|method|collectField
name|void
name|collectField
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|facetName
parameter_list|,
name|String
name|facetValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|collectQuery
name|void
name|collectQuery
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|facetName
parameter_list|,
name|String
name|facetValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|collectRange
name|void
name|collectRange
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|facetName
parameter_list|,
name|String
name|facetValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setQueryStatsCollectorReaders
name|void
name|setQueryStatsCollectorReaders
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setRangeStatsCollectorReaders
name|void
name|setRangeStatsCollectorReaders
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

