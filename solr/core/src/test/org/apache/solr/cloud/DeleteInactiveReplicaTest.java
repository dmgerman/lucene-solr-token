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
name|embedded
operator|.
name|JettySolrRunner
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
name|impl
operator|.
name|CloudSolrServer
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
name|impl
operator|.
name|HttpSolrServer
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
name|DocCollection
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
name|Replica
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
name|MapSolrParams
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
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
name|ZkNodeProps
operator|.
name|makeMap
import|;
end_import

begin_class
DECL|class|DeleteInactiveReplicaTest
specifier|public
class|class
name|DeleteInactiveReplicaTest
extends|extends
name|DeleteReplicaTest
block|{
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteInactiveReplicaTest
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteInactiveReplicaTest
specifier|private
name|void
name|deleteInactiveReplicaTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|COLL_NAME
init|=
literal|"delDeadColl"
decl_stmt|;
name|CloudSolrServer
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|createColl
argument_list|(
name|COLL_NAME
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|DocCollection
name|testcoll
init|=
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLL_NAME
argument_list|)
decl_stmt|;
specifier|final
name|Slice
name|shard1
init|=
name|testcoll
operator|.
name|getSlices
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|shard1
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|Slice
operator|.
name|ACTIVE
argument_list|)
condition|)
name|fail
argument_list|(
literal|"shard is not active"
argument_list|)
expr_stmt|;
name|Replica
name|replica1
init|=
name|shard1
operator|.
name|getReplicas
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|boolean
name|stopped
init|=
literal|false
decl_stmt|;
name|JettySolrRunner
name|stoppedJetty
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|stoppedJetty
operator|=
name|jetty
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
name|stopped
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|stopped
condition|)
block|{
name|fail
argument_list|(
literal|"Could not find jetty for replica "
operator|+
name|replica1
operator|+
literal|"jettys: "
operator|+
name|sb
argument_list|)
expr_stmt|;
block|}
name|long
name|endAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|3000
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endAt
condition|)
block|{
name|testcoll
operator|=
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLL_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"active"
operator|.
name|equals
argument_list|(
name|testcoll
operator|.
name|getSlice
argument_list|(
name|shard1
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getReplica
argument_list|(
name|replica1
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|Slice
operator|.
name|STATE
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|success
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"removed_replicas {}/{} "
argument_list|,
name|shard1
operator|.
name|getName
argument_list|()
argument_list|,
name|replica1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|removeAndWaitForReplicaGone
argument_list|(
name|COLL_NAME
argument_list|,
name|client
argument_list|,
name|replica1
argument_list|,
name|shard1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|stoppedJetty
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"restarted jetty"
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/cores"
argument_list|,
literal|"action"
argument_list|,
literal|"status"
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resp
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
literal|"base_url"
argument_list|)
argument_list|)
operator|.
name|request
argument_list|(
operator|new
name|QueryRequest
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"The core is up and running again"
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

