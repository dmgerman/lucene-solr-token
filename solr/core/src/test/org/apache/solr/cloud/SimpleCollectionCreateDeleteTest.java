begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|cloud
operator|.
name|ZkStateReader
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|SimpleCollectionCreateDeleteTest
specifier|public
class|class
name|SimpleCollectionCreateDeleteTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|SimpleCollectionCreateDeleteTest
specifier|public
name|SimpleCollectionCreateDeleteTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|1
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|1
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|overseerNode
init|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|notOverseerNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cloudJetty
range|:
name|cloudJettys
control|)
block|{
if|if
condition|(
operator|!
name|overseerNode
operator|.
name|equals
argument_list|(
name|cloudJetty
operator|.
name|nodeName
argument_list|)
condition|)
block|{
name|notOverseerNode
operator|=
name|cloudJetty
operator|.
name|nodeName
expr_stmt|;
break|break;
block|}
block|}
name|String
name|collectionName
init|=
literal|"SimpleCollectionCreateDeleteTest"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
operator|.
name|setCreateNodeSet
argument_list|(
name|overseerNode
argument_list|)
operator|.
name|setStateFormat
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
init|=
name|create
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
operator|.
name|getResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|get
argument_list|(
literal|"success"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|collectionName
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Delete
name|delete
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|delete
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|collectionName
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// create collection again on a node other than the overseer leader
name|create
operator|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
operator|.
name|setCreateNodeSet
argument_list|(
name|notOverseerNode
argument_list|)
operator|.
name|setStateFormat
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|request
operator|=
name|create
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
operator|.
name|getResponse
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Collection creation should not have failed"
argument_list|,
name|request
operator|.
name|get
argument_list|(
literal|"success"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

