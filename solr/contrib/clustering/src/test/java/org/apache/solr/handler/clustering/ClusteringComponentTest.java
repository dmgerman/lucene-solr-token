begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.clustering
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|NamedList
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|component
operator|.
name|QueryComponent
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|request
operator|.
name|SolrRequestHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|ClusteringComponentTest
specifier|public
class|class
name|ClusteringComponentTest
extends|extends
name|AbstractClusteringTest
block|{
annotation|@
name|Test
DECL|method|testComponent
specifier|public
name|void
name|testComponent
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SearchComponent
name|sc
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"clustering"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sc is null and it shouldn't be"
argument_list|,
name|sc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ClusteringComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ClusteringParams
operator|.
name|USE_SEARCH_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"standard"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|Object
name|clusters
init|=
name|values
operator|.
name|get
argument_list|(
literal|"clusters"
argument_list|)
decl_stmt|;
comment|//System.out.println("Clusters: " + clusters);
name|assertTrue
argument_list|(
literal|"clusters is null and it shouldn't be"
argument_list|,
name|clusters
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ClusteringComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ClusteringParams
operator|.
name|ENGINE_NAME
argument_list|,
literal|"mock"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ClusteringParams
operator|.
name|USE_COLLECTION
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|QueryComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"docClustering"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|clusters
operator|=
name|values
operator|.
name|get
argument_list|(
literal|"clusters"
argument_list|)
expr_stmt|;
comment|//System.out.println("Clusters: " + clusters);
name|assertTrue
argument_list|(
literal|"clusters is null and it shouldn't be"
argument_list|,
name|clusters
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

