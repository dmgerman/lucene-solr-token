begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|java
operator|.
name|util
operator|.
name|Properties
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
name|SolrTestCaseJ4
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
name|SolrRequest
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
name|CoreContainer
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
name|api
operator|.
name|Api
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
name|api
operator|.
name|ApiBag
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
name|util
operator|.
name|Utils
operator|.
name|fromJSONString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestCoreAdminApis
specifier|public
class|class
name|TestCoreAdminApis
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testCalls
specifier|public
name|void
name|testCalls
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
name|calls
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|CoreContainer
name|mockCC
init|=
name|getCoreContainerMock
argument_list|(
name|calls
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|CoreAdminHandler
name|coreAdminHandler
init|=
operator|new
name|CoreAdminHandler
argument_list|(
name|mockCC
argument_list|)
decl_stmt|;
name|ApiBag
name|apiBag
init|=
operator|new
name|ApiBag
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Api
name|api
range|:
name|coreAdminHandler
operator|.
name|getApis
argument_list|()
control|)
block|{
name|apiBag
operator|.
name|register
argument_list|(
name|api
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
expr_stmt|;
block|}
name|TestCollectionAPIs
operator|.
name|makeCall
argument_list|(
name|apiBag
argument_list|,
literal|"/cores"
argument_list|,
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
literal|"{create:{name: hello, instanceDir : someDir, schema: 'schema.xml'}}"
argument_list|,
name|mockCC
argument_list|)
expr_stmt|;
name|Object
index|[]
name|params
init|=
name|calls
operator|.
name|get
argument_list|(
literal|"create"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fromJSONString
argument_list|(
literal|"{schema : schema.xml}"
argument_list|)
argument_list|,
name|params
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|TestCollectionAPIs
operator|.
name|makeCall
argument_list|(
name|apiBag
argument_list|,
literal|"/cores/core1"
argument_list|,
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
literal|"{swap:{with: core2}}"
argument_list|,
name|mockCC
argument_list|)
expr_stmt|;
name|params
operator|=
name|calls
operator|.
name|get
argument_list|(
literal|"swap"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core1"
argument_list|,
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core2"
argument_list|,
name|params
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|TestCollectionAPIs
operator|.
name|makeCall
argument_list|(
name|apiBag
argument_list|,
literal|"/cores/core1"
argument_list|,
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
literal|"{rename:{to: core2}}"
argument_list|,
name|mockCC
argument_list|)
expr_stmt|;
name|params
operator|=
name|calls
operator|.
name|get
argument_list|(
literal|"swap"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core1"
argument_list|,
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core2"
argument_list|,
name|params
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|TestCollectionAPIs
operator|.
name|makeCall
argument_list|(
name|apiBag
argument_list|,
literal|"/cores/core1"
argument_list|,
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
literal|"{unload:{deleteIndex : true}}"
argument_list|,
name|mockCC
argument_list|)
expr_stmt|;
name|params
operator|=
name|calls
operator|.
name|get
argument_list|(
literal|"unload"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core1"
argument_list|,
name|params
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|params
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreContainerMock
specifier|public
specifier|static
name|CoreContainer
name|getCoreContainerMock
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
index|[]
argument_list|>
name|in
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|out
parameter_list|)
block|{
name|CoreContainer
name|mockCC
init|=
name|mock
argument_list|(
name|CoreContainer
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCC
operator|.
name|create
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocationOnMock
lambda|->
block|{
name|in
operator|.
name|put
argument_list|(
literal|"create"
argument_list|,
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
name|invocationOnMock
lambda|->
block|{
name|in
operator|.
name|put
argument_list|(
literal|"swap"
argument_list|,
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|mockCC
argument_list|)
operator|.
name|swap
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
name|invocationOnMock
lambda|->
block|{
name|in
operator|.
name|put
argument_list|(
literal|"rename"
argument_list|,
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|mockCC
argument_list|)
operator|.
name|rename
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
name|invocationOnMock
lambda|->
block|{
name|in
operator|.
name|put
argument_list|(
literal|"unload"
argument_list|,
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|mockCC
argument_list|)
operator|.
name|unload
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCC
operator|.
name|getCoreRootDirectory
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"coreroot"
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCC
operator|.
name|getContainerProperties
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCC
operator|.
name|getRequestHandlers
argument_list|()
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocationOnMock
lambda|->
name|out
operator|.
name|get
argument_list|(
literal|"getRequestHandlers"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|mockCC
return|;
block|}
block|}
end_class

end_unit

