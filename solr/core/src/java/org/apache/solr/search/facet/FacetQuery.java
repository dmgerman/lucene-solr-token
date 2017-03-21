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
name|HashMap
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_class
DECL|class|FacetQuery
specifier|public
class|class
name|FacetQuery
extends|extends
name|FacetRequest
block|{
comment|// query string or query?
DECL|field|q
name|Query
name|q
decl_stmt|;
annotation|@
name|Override
DECL|method|createFacetProcessor
specifier|public
name|FacetProcessor
name|createFacetProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|)
block|{
return|return
operator|new
name|FacetQueryProcessor
argument_list|(
name|fcontext
argument_list|,
name|this
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
name|FacetQueryMerger
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFacetDescription
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFacetDescription
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|descr
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|descr
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
name|q
argument_list|)
expr_stmt|;
return|return
name|descr
return|;
block|}
block|}
end_class

begin_class
DECL|class|FacetQueryProcessor
class|class
name|FacetQueryProcessor
extends|extends
name|FacetProcessor
argument_list|<
name|FacetQuery
argument_list|>
block|{
DECL|method|FacetQueryProcessor
name|FacetQueryProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetQuery
name|freq
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|process
argument_list|()
expr_stmt|;
if|if
condition|(
name|fcontext
operator|.
name|facetInfo
operator|!=
literal|null
condition|)
block|{
comment|// FIXME - what needs to be done here?
block|}
name|response
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|fillBucket
argument_list|(
name|response
argument_list|,
name|freq
operator|.
name|q
argument_list|,
literal|null
argument_list|,
operator|(
name|fcontext
operator|.
name|flags
operator|&
name|FacetContext
operator|.
name|SKIP_FACET
operator|)
operator|!=
literal|0
argument_list|,
name|fcontext
operator|.
name|facetInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

