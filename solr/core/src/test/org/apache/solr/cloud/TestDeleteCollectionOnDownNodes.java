begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|common
operator|.
name|cloud
operator|.
name|Slice
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestDeleteCollectionOnDownNodes
specifier|public
class|class
name|TestDeleteCollectionOnDownNodes
extends|extends
name|SolrCloudTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|4
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
argument_list|,
name|configset
argument_list|(
literal|"cloud-minimal"
argument_list|)
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf2"
argument_list|,
name|configset
argument_list|(
literal|"cloud-minimal"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteCollectionWithDownNodes
specifier|public
name|void
name|deleteCollectionWithDownNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"halfdeletedcollection2"
argument_list|,
literal|"conf"
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|3
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// stop a couple nodes
name|cluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|cluster
operator|.
name|getRandomJetty
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// wait for leaders to settle out
name|waitForState
argument_list|(
literal|"Timed out waiting for leader elections"
argument_list|,
literal|"halfdeletedcollection2"
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|c
control|)
block|{
if|if
condition|(
name|slice
operator|.
name|getLeader
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|slice
operator|.
name|getLeader
argument_list|()
operator|.
name|isActive
argument_list|(
name|n
argument_list|)
operator|==
literal|false
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
argument_list|)
expr_stmt|;
comment|// delete the collection
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
literal|"halfdeletedcollection2"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Timed out waiting for collection to be deleted"
argument_list|,
literal|"halfdeletedcollection2"
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|c
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Still found collection that should be gone"
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|hasCollection
argument_list|(
literal|"halfdeletedcollection2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

