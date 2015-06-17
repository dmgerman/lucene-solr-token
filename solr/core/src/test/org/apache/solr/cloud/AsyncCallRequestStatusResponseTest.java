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
name|response
operator|.
name|CollectionAdminResponse
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
DECL|class|AsyncCallRequestStatusResponseTest
specifier|public
class|class
name|AsyncCallRequestStatusResponseTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
annotation|@
name|Test
DECL|method|testAsyncCallStatusResponse
specifier|public
name|void
name|testAsyncCallStatusResponse
parameter_list|()
throws|throws
name|Exception
block|{
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
decl_stmt|;
name|create
operator|.
name|setCollectionName
argument_list|(
literal|"asynccall"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|create
operator|.
name|setAsyncId
argument_list|(
literal|"1000"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|create
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|waitForCollection
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|"asynccall"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1000"
argument_list|,
literal|30
argument_list|,
name|cloudClient
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|equals
argument_list|(
literal|"completed"
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|RequestStatus
name|requestStatus
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|RequestStatus
argument_list|()
decl_stmt|;
name|requestStatus
operator|.
name|setRequestId
argument_list|(
literal|"1000"
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|rsp
init|=
name|requestStatus
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|NamedList
name|r
init|=
name|rsp
operator|.
name|getResponse
argument_list|()
decl_stmt|;
comment|// Check that there's more response than the hardcoded status and states
name|assertEquals
argument_list|(
literal|"Assertion Failure"
operator|+
name|r
operator|.
name|toString
argument_list|()
argument_list|,
literal|5
argument_list|,
name|r
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

