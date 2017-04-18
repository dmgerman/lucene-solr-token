begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import

begin_class
DECL|class|VarianceAgg
specifier|public
class|class
name|VarianceAgg
extends|extends
name|SimpleAggValueSource
block|{
DECL|method|VarianceAgg
specifier|public
name|VarianceAgg
parameter_list|(
name|ValueSource
name|vs
parameter_list|)
block|{
name|super
argument_list|(
literal|"variance"
argument_list|,
name|vs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSlotAcc
specifier|public
name|SlotAcc
name|createSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VarianceSlotAcc
argument_list|(
name|getArg
argument_list|()
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFacetMerger
specifier|public
name|FacetMerger
name|createFacetMerger
parameter_list|(
name|Object
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|Merger
argument_list|()
return|;
block|}
DECL|class|Merger
specifier|private
specifier|static
class|class
name|Merger
extends|extends
name|FacetDoubleMerger
block|{
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|sumSq
name|double
name|sumSq
decl_stmt|;
DECL|field|sum
name|double
name|sum
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|,
name|Context
name|mcontext1
parameter_list|)
block|{
name|List
argument_list|<
name|Number
argument_list|>
name|numberList
init|=
operator|(
name|List
argument_list|<
name|Number
argument_list|>
operator|)
name|facetResult
decl_stmt|;
name|this
operator|.
name|count
operator|+=
name|numberList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|sumSq
operator|+=
name|numberList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|sum
operator|+=
name|numberList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergedResult
specifier|public
name|Object
name|getMergedResult
parameter_list|()
block|{
return|return
name|this
operator|.
name|getDouble
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDouble
specifier|protected
name|double
name|getDouble
parameter_list|()
block|{
name|double
name|val
init|=
name|count
operator|==
literal|0
condition|?
literal|0.0d
else|:
operator|(
name|sumSq
operator|/
name|count
operator|)
operator|-
name|Math
operator|.
name|pow
argument_list|(
name|sum
operator|/
name|count
argument_list|,
literal|2
argument_list|)
decl_stmt|;
return|return
name|val
return|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

