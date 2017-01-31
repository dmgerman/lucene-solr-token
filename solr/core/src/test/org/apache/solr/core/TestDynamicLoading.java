begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|SolrClient
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
name|HttpSolrClient
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
name|handler
operator|.
name|TestBlobHandler
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|SimplePostTool
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|nio
operator|.
name|ByteBuffer
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
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|handler
operator|.
name|TestSolrConfigHandlerCloud
operator|.
name|compareValues
import|;
end_import

begin_class
DECL|class|TestDynamicLoading
specifier|public
class|class
name|TestDynamicLoading
extends|extends
name|AbstractFullDistribZkTestBase
block|{
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
name|SolrClient
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
parameter_list|()
lambda|->
operator|(
operator|(
name|HttpSolrClient
operator|)
name|client
operator|)
operator|.
name|getBaseURL
argument_list|()
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
annotation|@
name|BeforeClass
DECL|method|enableRuntimeLib
specifier|public
specifier|static
name|void
name|enableRuntimeLib
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.runtime.lib"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
for|for
control|(
name|RestTestHarness
name|r
range|:
name|restTestHarnesses
control|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDynamicLoading
specifier|public
name|void
name|testDynamicLoading
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.runtime.lib"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|setupHarnesses
argument_list|()
expr_stmt|;
name|String
name|blobName
init|=
literal|"colltest"
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|HttpSolrClient
name|randomClient
init|=
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|baseURL
init|=
name|randomClient
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|baseURL
operator|=
name|baseURL
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseURL
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"'add-runtimelib' : { 'name' : 'colltest' ,'version':1}\n"
operator|+
literal|"}"
decl_stmt|;
name|RestTestHarness
name|client
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
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
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"runtimeLib"
argument_list|,
name|blobName
argument_list|,
literal|"version"
argument_list|)
argument_list|,
literal|1l
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|"'create-requesthandler' : { 'name' : '/test1', 'class': 'org.apache.solr.core.BlobStoreTestRequestHandler' ,registerPath: '/,/v2',  'runtimeLib' : true }\n"
operator|+
literal|"}"
expr_stmt|;
name|client
operator|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/test1"
argument_list|,
literal|"class"
argument_list|)
argument_list|,
literal|"org.apache.solr.core.BlobStoreTestRequestHandler"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|TestBlobHandler
operator|.
name|getAsString
argument_list|(
name|map
argument_list|)
argument_list|,
name|map
operator|=
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|TestBlobHandler
operator|.
name|getAsString
argument_list|(
name|map
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|".system collection not available"
argument_list|)
argument_list|)
expr_stmt|;
name|TestBlobHandler
operator|.
name|createSystemCollection
argument_list|(
name|getHttpSolrClient
argument_list|(
name|baseURL
argument_list|,
name|randomClient
operator|.
name|getHttpClient
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|".system"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|=
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"full output "
operator|+
name|TestBlobHandler
operator|.
name|getAsString
argument_list|(
name|map
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"no such blob or version available: colltest/1"
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'watched': {"
operator|+
literal|"                    'x':'X val',\n"
operator|+
literal|"                    'y': 'Y val'}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"watched"
argument_list|,
literal|"x"
argument_list|)
argument_list|,
literal|"X val"
argument_list|,
literal|10
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"X val"
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|ByteBuffer
name|jar
init|=
literal|null
decl_stmt|;
comment|//     jar = persistZip("/tmp/runtimelibs.jar.bin", TestDynamicLoading.class, RuntimeLibReqHandler.class, RuntimeLibResponseWriter.class, RuntimeLibSearchComponent.class);
comment|//    if(true) return;
name|jar
operator|=
name|getFileContent
argument_list|(
literal|"runtimecode/runtimelibs.jar.bin"
argument_list|)
expr_stmt|;
name|TestBlobHandler
operator|.
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
name|baseURL
argument_list|,
name|blobName
argument_list|,
name|jar
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|"'create-requesthandler' : { 'name' : '/runtime', 'class': 'org.apache.solr.core.RuntimeLibReqHandler' , 'runtimeLib':true },"
operator|+
literal|"'create-searchcomponent' : { 'name' : 'get', 'class': 'org.apache.solr.core.RuntimeLibSearchComponent' , 'runtimeLib':true },"
operator|+
literal|"'create-queryResponseWriter' : { 'name' : 'json1', 'class': 'org.apache.solr.core.RuntimeLibResponseWriter' , 'runtimeLib':true }"
operator|+
literal|"}"
expr_stmt|;
name|client
operator|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|Map
name|result
init|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/runtime"
argument_list|,
literal|"class"
argument_list|)
argument_list|,
literal|"org.apache.solr.core.RuntimeLibReqHandler"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|"org.apache.solr.core.RuntimeLibResponseWriter"
argument_list|,
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"queryResponseWriter"
argument_list|,
literal|"json1"
argument_list|,
literal|"class"
argument_list|)
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|"org.apache.solr.core.RuntimeLibSearchComponent"
argument_list|,
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"searchComponent"
argument_list|,
literal|"get"
argument_list|,
literal|"class"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/runtime?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"class"
argument_list|)
argument_list|,
literal|"org.apache.solr.core.RuntimeLibReqHandler"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
name|MemClassLoader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|asList
argument_list|(
literal|"loader"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/runtime?wt=json1"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"wt"
argument_list|)
argument_list|,
literal|"org.apache.solr.core.RuntimeLibResponseWriter"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
name|MemClassLoader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|asList
argument_list|(
literal|"loader"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/get?abc=xyz"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"get"
argument_list|)
argument_list|,
literal|"org.apache.solr.core.RuntimeLibSearchComponent"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
name|MemClassLoader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|asList
argument_list|(
literal|"loader"
argument_list|)
argument_list|)
expr_stmt|;
name|jar
operator|=
name|getFileContent
argument_list|(
literal|"runtimecode/runtimelibs_v2.jar.bin"
argument_list|)
expr_stmt|;
name|TestBlobHandler
operator|.
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
name|baseURL
argument_list|,
name|blobName
argument_list|,
name|jar
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|"'update-runtimelib' : { 'name' : 'colltest' ,'version':2}\n"
operator|+
literal|"}"
expr_stmt|;
name|client
operator|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"runtimeLib"
argument_list|,
name|blobName
argument_list|,
literal|"version"
argument_list|)
argument_list|,
literal|2l
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/get?abc=xyz"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"Version"
argument_list|)
argument_list|,
literal|"2"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'watched': {"
operator|+
literal|"                    'x':'X val',\n"
operator|+
literal|"                    'y': 'Y val'}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"watched"
argument_list|,
literal|"x"
argument_list|)
argument_list|,
literal|"X val"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/test1?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|)
argument_list|,
literal|"X val"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'watched': {"
operator|+
literal|"                    'x':'X val changed',\n"
operator|+
literal|"                    'y': 'Y val'}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/test1?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|)
argument_list|,
literal|"X val changed"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileContent
specifier|public
specifier|static
name|ByteBuffer
name|getFileContent
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|jar
decl_stmt|;
try|try
init|(
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|getFile
argument_list|(
name|f
argument_list|)
argument_list|)
init|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|fis
operator|.
name|available
argument_list|()
index|]
decl_stmt|;
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|jar
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
return|return
name|jar
return|;
block|}
DECL|method|persistZip
specifier|public
specifier|static
name|ByteBuffer
name|persistZip
parameter_list|(
name|String
name|loc
parameter_list|,
name|Class
modifier|...
name|classes
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|jar
init|=
name|generateZip
argument_list|(
name|classes
argument_list|)
decl_stmt|;
try|try
init|(
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|loc
argument_list|)
init|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|jar
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|jar
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
return|return
name|jar
return|;
block|}
DECL|method|generateZip
specifier|public
specifier|static
name|ByteBuffer
name|generateZip
parameter_list|(
name|Class
modifier|...
name|classes
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipOutputStream
name|zipOut
init|=
literal|null
decl_stmt|;
name|SimplePostTool
operator|.
name|BAOS
name|bos
init|=
operator|new
name|SimplePostTool
operator|.
name|BAOS
argument_list|()
decl_stmt|;
name|zipOut
operator|=
operator|new
name|ZipOutputStream
argument_list|(
name|bos
argument_list|)
expr_stmt|;
name|zipOut
operator|.
name|setLevel
argument_list|(
name|ZipOutputStream
operator|.
name|DEFLATED
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|classes
control|)
block|{
name|String
name|path
init|=
name|c
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|.
name|concat
argument_list|(
literal|".class"
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
init|=
operator|new
name|ZipEntry
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|ByteBuffer
name|b
init|=
name|SimplePostTool
operator|.
name|inputStreamToByteArray
argument_list|(
name|c
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|zipOut
operator|.
name|putNextEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|zipOut
operator|.
name|write
argument_list|(
name|b
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|zipOut
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|zipOut
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|bos
operator|.
name|getByteBuffer
argument_list|()
return|;
block|}
block|}
end_class

end_unit

