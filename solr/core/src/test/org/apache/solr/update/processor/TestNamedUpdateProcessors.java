begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

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
name|Collection
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
name|SolrQuery
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|QueryResponse
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
name|common
operator|.
name|SolrDocument
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
name|SolrInputDocument
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
name|StrUtils
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
name|TestDynamicLoading
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
name|TestSolrConfigHandler
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
name|Test
import|;
end_import

begin_class
DECL|class|TestNamedUpdateProcessors
specifier|public
class|class
name|TestNamedUpdateProcessors
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
name|HttpSolrClient
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
DECL|method|test
specifier|public
name|void
name|test
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
specifier|final
name|String
name|solrClientUrl
init|=
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
decl_stmt|;
name|TestBlobHandler
operator|.
name|createSystemCollection
argument_list|(
name|getHttpSolrClient
argument_list|(
name|solrClientUrl
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
name|TestBlobHandler
operator|.
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
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
argument_list|,
name|blobName
argument_list|,
name|TestDynamicLoading
operator|.
name|generateZip
argument_list|(
name|RuntimeUrp
operator|.
name|class
argument_list|)
argument_list|,
literal|1
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
literal|"'create-updateprocessor' : { 'name' : 'firstFld', 'class': 'solr.FirstFieldValueUpdateProcessorFactory', 'fieldName':'test_s'}, \n"
operator|+
literal|"'create-updateprocessor' : { 'name' : 'test', 'class': 'org.apache.solr.update.processor.RuntimeUrp', 'runtimeLib':true }, \n"
operator|+
literal|"'create-updateprocessor' : { 'name' : 'maxFld', 'class': 'solr.MaxFieldValueUpdateProcessorFactory', 'fieldName':'mul_s'} \n"
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
for|for
control|(
name|RestTestHarness
name|restTestHarness
range|:
name|restTestHarnesses
control|)
block|{
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|restTestHarness
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
literal|"updateProcessor"
argument_list|,
literal|"firstFld"
argument_list|,
literal|"fieldName"
argument_list|)
argument_list|,
literal|"test_s"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"test_s"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"mul_s"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"aaa"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|randomClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|randomClient
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryResponse
name|result
init|=
name|randomClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:123"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Collection
operator|)
name|result
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValues
argument_list|(
literal|"test_s"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Collection
operator|)
name|result
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValues
argument_list|(
literal|"mul_s"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"test_s"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"three"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"mul_s"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"aaa"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|UpdateRequest
name|ur
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|ur
operator|.
name|add
argument_list|(
name|doc
argument_list|)
operator|.
name|setParam
argument_list|(
literal|"processor"
argument_list|,
literal|"firstFld,maxFld,test"
argument_list|)
expr_stmt|;
name|randomClient
operator|.
name|request
argument_list|(
name|ur
argument_list|)
expr_stmt|;
name|randomClient
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|randomClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:456"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrDocument
name|d
init|=
name|result
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"test_s"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"mul_s"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"three"
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"test_s"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"mul_s"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|processors
init|=
operator|(
name|String
operator|)
name|d
operator|.
name|getFirstValue
argument_list|(
literal|"processors_s"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|processors
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|processors
argument_list|,
literal|'>'
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"FirstFieldValueUpdateProcessorFactory"
argument_list|,
literal|"MaxFieldValueUpdateProcessorFactory"
argument_list|,
literal|"RuntimeUrp"
argument_list|,
literal|"LogUpdateProcessorFactory"
argument_list|,
literal|"DistributedUpdateProcessorFactory"
argument_list|,
literal|"RunUpdateProcessorFactory"
argument_list|)
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

