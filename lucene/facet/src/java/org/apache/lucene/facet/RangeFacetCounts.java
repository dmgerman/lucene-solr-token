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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LongFieldSource
import|;
end_import

begin_comment
comment|/** Base class for range faceting.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|RangeFacetCounts
specifier|abstract
class|class
name|RangeFacetCounts
extends|extends
name|Facets
block|{
DECL|field|ranges
specifier|protected
specifier|final
name|Range
index|[]
name|ranges
decl_stmt|;
DECL|field|counts
specifier|protected
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|totCount
specifier|protected
name|int
name|totCount
decl_stmt|;
comment|/** Create {@code RangeFacetCounts}, using {@link    *  LongFieldSource} from the specified field. */
DECL|method|RangeFacetCounts
specifier|protected
name|RangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|Range
index|[]
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|ranges
operator|=
name|ranges
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
block|}
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
if|if
condition|(
name|path
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path.length should be 0"
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
name|dim
argument_list|,
name|path
argument_list|,
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
comment|// TODO: should we impl this?
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

