begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|SolrServer
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|util
operator|.
name|BaseTestHarness
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
name|util
operator|.
name|RESTfulServerProvider
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
name|util
operator|.
name|RestTestHarness
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
DECL|class|TestCloudManagedSchemaConcurrent
specifier|public
class|class
name|TestCloudManagedSchemaConcurrent
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestCloudManagedSchemaConcurrent
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SUCCESS_XPATH
specifier|private
specifier|static
specifier|final
name|String
name|SUCCESS_XPATH
init|=
literal|"/response/lst[@name='responseHeader']/int[@name='status'][.='0']"
decl_stmt|;
DECL|method|TestCloudManagedSchemaConcurrent
specifier|public
name|TestCloudManagedSchemaConcurrent
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|sliceCount
operator|=
literal|4
expr_stmt|;
name|shardCount
operator|=
literal|8
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|initSysProperties
specifier|public
specifier|static
name|void
name|initSysProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-managed-schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getExtraServlets
specifier|public
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|getExtraServlets
parameter_list|()
block|{
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
comment|// '/schema/*' matches '/schema', '/schema/', and '/schema/whatever...'
return|return
name|extraServlets
return|;
block|}
DECL|field|restTestHarnesses
specifier|private
name|List
argument_list|<
name|RestTestHarness
argument_list|>
name|restTestHarnesses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setupHarnesses
specifier|private
name|void
name|setupHarnesses
parameter_list|()
block|{
for|for
control|(
specifier|final
name|SolrServer
name|client
range|:
name|clients
control|)
block|{
name|RestTestHarness
name|harness
init|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|RESTfulServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
operator|(
operator|(
name|HttpSolrServer
operator|)
name|client
operator|)
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|restTestHarnesses
operator|.
name|add
argument_list|(
name|harness
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifySuccess
specifier|private
name|void
name|verifySuccess
parameter_list|(
name|String
name|request
parameter_list|,
name|String
name|response
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|result
init|=
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|SUCCESS_XPATH
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|result
condition|)
block|{
name|String
name|msg
init|=
literal|"QUERY FAILED: xpath="
operator|+
name|result
operator|+
literal|"  request="
operator|+
name|request
operator|+
literal|"  response="
operator|+
name|response
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addFieldPut
specifier|private
name|void
name|addFieldPut
parameter_list|(
name|RestTestHarness
name|publisher
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|content
init|=
literal|"{\"type\":\"text\",\"stored\":\"false\"}"
decl_stmt|;
name|String
name|request
init|=
literal|"/schema/fields/"
operator|+
name|fieldName
operator|+
literal|"?wt=xml"
decl_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|put
argument_list|(
name|request
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|verifySuccess
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|addFieldPost
specifier|private
name|void
name|addFieldPost
parameter_list|(
name|RestTestHarness
name|publisher
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|content
init|=
literal|"[{\"name\":\""
operator|+
name|fieldName
operator|+
literal|"\",\"type\":\"text\",\"stored\":\"false\"}]"
decl_stmt|;
name|String
name|request
init|=
literal|"/schema/fields/?wt=xml"
decl_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|post
argument_list|(
name|request
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|verifySuccess
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|copyField
specifier|private
name|void
name|copyField
parameter_list|(
name|RestTestHarness
name|publisher
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|content
init|=
literal|"[{\"source\":\""
operator|+
name|source
operator|+
literal|"\",\"dest\":[\""
operator|+
name|dest
operator|+
literal|"\"]}]"
decl_stmt|;
name|String
name|request
init|=
literal|"/schema/copyfields/?wt=xml"
decl_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|post
argument_list|(
name|request
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|verifySuccess
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|getExpectedFieldResponses
specifier|private
name|String
index|[]
name|getExpectedFieldResponses
parameter_list|(
name|int
name|numAddFieldPuts
parameter_list|,
name|String
name|putFieldName
parameter_list|,
name|int
name|numAddFieldPosts
parameter_list|,
name|String
name|postFieldName
parameter_list|)
block|{
name|String
index|[]
name|expectedAddFields
init|=
operator|new
name|String
index|[
literal|1
operator|+
name|numAddFieldPuts
operator|+
name|numAddFieldPosts
index|]
decl_stmt|;
name|expectedAddFields
index|[
literal|0
index|]
operator|=
name|SUCCESS_XPATH
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numAddFieldPuts
condition|;
operator|++
name|i
control|)
block|{
name|String
name|newFieldName
init|=
name|putFieldName
operator|+
name|i
decl_stmt|;
name|expectedAddFields
index|[
literal|1
operator|+
name|i
index|]
operator|=
literal|"/response/arr[@name='fields']/lst/str[@name='name'][.='"
operator|+
name|newFieldName
operator|+
literal|"']"
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numAddFieldPosts
condition|;
operator|++
name|i
control|)
block|{
name|String
name|newFieldName
init|=
name|postFieldName
operator|+
name|i
decl_stmt|;
name|expectedAddFields
index|[
literal|1
operator|+
name|numAddFieldPuts
operator|+
name|i
index|]
operator|=
literal|"/response/arr[@name='fields']/lst/str[@name='name'][.='"
operator|+
name|newFieldName
operator|+
literal|"']"
expr_stmt|;
block|}
return|return
name|expectedAddFields
return|;
block|}
DECL|method|getExpectedCopyFieldResponses
specifier|private
name|String
index|[]
name|getExpectedCopyFieldResponses
parameter_list|(
name|List
argument_list|<
name|CopyFieldInfo
argument_list|>
name|copyFields
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|expectedCopyFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedCopyFields
operator|.
name|add
argument_list|(
name|SUCCESS_XPATH
argument_list|)
expr_stmt|;
for|for
control|(
name|CopyFieldInfo
name|cpi
range|:
name|copyFields
control|)
block|{
name|String
name|expectedSourceName
init|=
name|cpi
operator|.
name|getSourceField
argument_list|()
decl_stmt|;
name|expectedCopyFields
operator|.
name|add
argument_list|(
literal|"/response/arr[@name='copyFields']/lst/str[@name='source'][.='"
operator|+
name|expectedSourceName
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|String
name|expectedDestName
init|=
name|cpi
operator|.
name|getDestField
argument_list|()
decl_stmt|;
name|expectedCopyFields
operator|.
name|add
argument_list|(
literal|"/response/arr[@name='copyFields']/lst/str[@name='dest'][.='"
operator|+
name|expectedDestName
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
return|return
name|expectedCopyFields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|expectedCopyFields
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
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
name|setupHarnesses
argument_list|()
expr_stmt|;
name|concurrentOperationsTest
argument_list|()
expr_stmt|;
name|schemaLockTest
argument_list|()
expr_stmt|;
block|}
DECL|method|concurrentOperationsTest
specifier|private
name|void
name|concurrentOperationsTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// First, add a bunch of fields via PUT and POST, as well as copyFields,
comment|// but do it fast enough and verify shards' schemas after all of them are added
name|int
name|numFields
init|=
literal|100
decl_stmt|;
name|int
name|numAddFieldPuts
init|=
literal|0
decl_stmt|;
name|int
name|numAddFieldPosts
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|CopyFieldInfo
argument_list|>
name|copyFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
name|putFieldName
init|=
literal|"newfieldPut"
decl_stmt|;
specifier|final
name|String
name|postFieldName
init|=
literal|"newfieldPost"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|numFields
condition|;
operator|++
name|i
control|)
block|{
name|RestTestHarness
name|publisher
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
comment|// send an add field via PUT
name|addFieldPut
argument_list|(
name|publisher
argument_list|,
name|putFieldName
operator|+
name|numAddFieldPuts
operator|++
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
comment|// send an add field via POST
name|addFieldPost
argument_list|(
name|publisher
argument_list|,
name|postFieldName
operator|+
name|numAddFieldPosts
operator|++
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|2
condition|)
block|{
comment|// send a copy field
name|String
name|sourceField
init|=
literal|null
decl_stmt|;
name|String
name|destField
init|=
literal|null
decl_stmt|;
name|int
name|sourceType
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceType
operator|==
literal|0
condition|)
block|{
comment|// existing
name|sourceField
operator|=
literal|"name"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceType
operator|==
literal|1
condition|)
block|{
comment|// newly created
name|sourceField
operator|=
literal|"copySource"
operator|+
name|i
expr_stmt|;
name|addFieldPut
argument_list|(
name|publisher
argument_list|,
name|sourceField
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// dynamic
name|sourceField
operator|=
literal|"*_dynamicSource"
operator|+
name|i
operator|+
literal|"_t"
expr_stmt|;
comment|// * only supported if both src and dst use it
name|destField
operator|=
literal|"*_dynamicDest"
operator|+
name|i
operator|+
literal|"_t"
expr_stmt|;
block|}
if|if
condition|(
name|destField
operator|==
literal|null
condition|)
block|{
name|int
name|destType
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|destType
operator|==
literal|0
condition|)
block|{
comment|// existing
name|destField
operator|=
literal|"title"
expr_stmt|;
block|}
else|else
block|{
comment|// newly created
name|destField
operator|=
literal|"copyDest"
operator|+
name|i
expr_stmt|;
name|addFieldPut
argument_list|(
name|publisher
argument_list|,
name|destField
argument_list|)
expr_stmt|;
block|}
block|}
name|copyField
argument_list|(
name|publisher
argument_list|,
name|sourceField
argument_list|,
name|destField
argument_list|)
expr_stmt|;
name|copyFields
operator|.
name|add
argument_list|(
operator|new
name|CopyFieldInfo
argument_list|(
name|sourceField
argument_list|,
name|destField
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|expectedAddFields
init|=
name|getExpectedFieldResponses
argument_list|(
name|numAddFieldPuts
argument_list|,
name|putFieldName
argument_list|,
name|numAddFieldPosts
argument_list|,
name|postFieldName
argument_list|)
decl_stmt|;
name|String
index|[]
name|expectedCopyFields
init|=
name|getExpectedCopyFieldResponses
argument_list|(
name|copyFields
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|long
name|maxTimeoutMillis
init|=
literal|100000
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|String
name|request
init|=
literal|null
decl_stmt|;
name|String
name|response
init|=
literal|null
decl_stmt|;
name|String
name|result
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutMillis
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|RestTestHarness
name|client
range|:
name|restTestHarnesses
control|)
block|{
comment|// verify addFieldPuts and addFieldPosts
name|request
operator|=
literal|"/schema/fields?wt=xml"
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|query
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|result
operator|=
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|expectedAddFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
comment|// verify copyFields
name|request
operator|=
literal|"/schema/copyfields?wt=xml"
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|query
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|result
operator|=
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|expectedCopyFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
name|success
operator|=
operator|(
name|result
operator|==
literal|null
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|String
name|msg
init|=
literal|"QUERY FAILED: xpath="
operator|+
name|result
operator|+
literal|"  request="
operator|+
name|request
operator|+
literal|"  response="
operator|+
name|response
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PutPostThread
specifier|private
class|class
name|PutPostThread
extends|extends
name|Thread
block|{
DECL|field|harness
name|RestTestHarness
name|harness
decl_stmt|;
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|isPut
name|boolean
name|isPut
decl_stmt|;
DECL|method|PutPostThread
specifier|public
name|PutPostThread
parameter_list|(
name|RestTestHarness
name|harness
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|isPut
parameter_list|)
block|{
name|this
operator|.
name|harness
operator|=
name|harness
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|isPut
operator|=
name|isPut
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|isPut
condition|)
block|{
name|addFieldPut
argument_list|(
name|harness
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addFieldPost
argument_list|(
name|harness
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// log.error("###ACTUAL FAILURE!");
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|schemaLockTest
specifier|private
name|void
name|schemaLockTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// First, add a bunch of fields via PUT and POST, as well as copyFields,
comment|// but do it fast enough and verify shards' schemas after all of them are added
name|int
name|numFields
init|=
literal|25
decl_stmt|;
name|int
name|numAddFieldPuts
init|=
literal|0
decl_stmt|;
name|int
name|numAddFieldPosts
init|=
literal|0
decl_stmt|;
specifier|final
name|String
name|putFieldName
init|=
literal|"newfieldPutThread"
decl_stmt|;
specifier|final
name|String
name|postFieldName
init|=
literal|"newfieldPostThread"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|numFields
condition|;
operator|++
name|i
control|)
block|{
comment|// System.err.println("###ITERATION: " + i);
name|int
name|postHarness
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|RestTestHarness
name|publisher
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|postHarness
argument_list|)
decl_stmt|;
name|PutPostThread
name|postThread
init|=
operator|new
name|PutPostThread
argument_list|(
name|publisher
argument_list|,
name|postFieldName
operator|+
name|numAddFieldPosts
operator|++
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|postThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|putHarness
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|publisher
operator|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|putHarness
argument_list|)
expr_stmt|;
name|PutPostThread
name|putThread
init|=
operator|new
name|PutPostThread
argument_list|(
name|publisher
argument_list|,
name|putFieldName
operator|+
name|numAddFieldPuts
operator|++
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|putThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|postThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|putThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|String
index|[]
name|expectedAddFields
init|=
name|getExpectedFieldResponses
argument_list|(
name|numAddFieldPuts
argument_list|,
name|putFieldName
argument_list|,
name|numAddFieldPosts
argument_list|,
name|postFieldName
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|long
name|maxTimeoutMillis
init|=
literal|100000
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|String
name|request
init|=
literal|null
decl_stmt|;
name|String
name|response
init|=
literal|null
decl_stmt|;
name|String
name|result
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutMillis
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// int j = 0;
for|for
control|(
name|RestTestHarness
name|client
range|:
name|restTestHarnesses
control|)
block|{
comment|// System.err.println("###CHECKING HARNESS: " + j++ + " for iteration: " + i);
comment|// verify addFieldPuts and addFieldPosts
name|request
operator|=
literal|"/schema/fields?wt=xml"
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|query
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|//System.err.println("###RESPONSE: " + response);
name|result
operator|=
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|expectedAddFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// System.err.println("###FAILURE!");
break|break;
block|}
block|}
name|success
operator|=
operator|(
name|result
operator|==
literal|null
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|String
name|msg
init|=
literal|"QUERY FAILED: xpath="
operator|+
name|result
operator|+
literal|"  request="
operator|+
name|request
operator|+
literal|"  response="
operator|+
name|response
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CopyFieldInfo
specifier|private
specifier|static
class|class
name|CopyFieldInfo
block|{
DECL|field|sourceField
specifier|private
name|String
name|sourceField
decl_stmt|;
DECL|field|destField
specifier|private
name|String
name|destField
decl_stmt|;
DECL|method|CopyFieldInfo
specifier|public
name|CopyFieldInfo
parameter_list|(
name|String
name|sourceField
parameter_list|,
name|String
name|destField
parameter_list|)
block|{
name|this
operator|.
name|sourceField
operator|=
name|sourceField
expr_stmt|;
name|this
operator|.
name|destField
operator|=
name|destField
expr_stmt|;
block|}
DECL|method|getSourceField
specifier|public
name|String
name|getSourceField
parameter_list|()
block|{
return|return
name|sourceField
return|;
block|}
DECL|method|getDestField
specifier|public
name|String
name|getDestField
parameter_list|()
block|{
return|return
name|destField
return|;
block|}
block|}
block|}
end_class

end_unit

