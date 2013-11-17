begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/** Maps specified dims to provided Facets impls; else, uses  *  the default Facets impl. */
end_comment

begin_class
DECL|class|MultiFacets
specifier|public
class|class
name|MultiFacets
extends|extends
name|Facets
block|{
DECL|field|dimToFacets
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|dimToFacets
decl_stmt|;
DECL|field|defaultFacets
specifier|private
specifier|final
name|Facets
name|defaultFacets
decl_stmt|;
DECL|method|MultiFacets
specifier|public
name|MultiFacets
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|dimToFacets
parameter_list|,
name|Facets
name|defaultFacets
parameter_list|)
block|{
name|this
operator|.
name|dimToFacets
operator|=
name|dimToFacets
expr_stmt|;
name|this
operator|.
name|defaultFacets
operator|=
name|defaultFacets
expr_stmt|;
block|}
DECL|method|getTopChildren
specifier|public
name|SimpleFacetResult
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
throws|throws
name|IOException
block|{
name|Facets
name|facets
init|=
name|dimToFacets
operator|.
name|get
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|facets
operator|==
literal|null
condition|)
block|{
name|facets
operator|=
name|defaultFacets
expr_stmt|;
block|}
return|return
name|facets
operator|.
name|getTopChildren
argument_list|(
name|topN
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
return|;
block|}
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
name|Facets
name|facets
init|=
name|dimToFacets
operator|.
name|get
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|facets
operator|==
literal|null
condition|)
block|{
name|facets
operator|=
name|defaultFacets
expr_stmt|;
block|}
return|return
name|facets
operator|.
name|getSpecificValue
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|getAllDims
specifier|public
name|List
argument_list|<
name|SimpleFacetResult
argument_list|>
name|getAllDims
parameter_list|(
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit can/should we impl this?  ie, sparse
comment|// faceting after drill sideways
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

