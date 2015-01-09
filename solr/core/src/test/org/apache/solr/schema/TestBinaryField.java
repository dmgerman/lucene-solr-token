begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|SolrJettyTestBase
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|beans
operator|.
name|Field
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
name|SolrDocumentList
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|TestBinaryField
specifier|public
class|class
name|TestBinaryField
extends|extends
name|SolrJettyTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|homeDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|collDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"collection1"
argument_list|)
decl_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|collDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|collDir
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|collDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|src_dir
init|=
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|src_dir
argument_list|,
literal|"schema-binaryfield.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|src_dir
argument_list|,
literal|"solrconfig-basic.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|src_dir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|createJetty
argument_list|(
name|homeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrClient
name|client
init|=
name|getSolrClient
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|SolrInputDocument
name|doc
init|=
literal|null
decl_stmt|;
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
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"data"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buf
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc
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
literal|2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"data"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buf
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc
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
literal|3
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"data"
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|SolrDocumentList
name|res
init|=
name|resp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Bean
argument_list|>
name|beans
init|=
name|resp
operator|.
name|getBeans
argument_list|(
name|Bean
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|beans
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrDocument
name|d
range|:
name|res
control|)
block|{
name|Integer
name|id
init|=
operator|(
name|Integer
operator|)
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|data
operator|.
name|length
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|2
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|data
operator|.
name|length
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|4
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|data
operator|.
name|length
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Bean
name|d
range|:
name|beans
control|)
block|{
name|Integer
name|id
init|=
name|d
operator|.
name|id
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|d
operator|.
name|data
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|data
operator|.
name|length
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|2
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|data
operator|.
name|length
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|+
literal|4
argument_list|)
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|data
operator|.
name|length
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|Bean
specifier|public
specifier|static
class|class
name|Bean
block|{
annotation|@
name|Field
DECL|field|id
name|int
name|id
decl_stmt|;
annotation|@
name|Field
DECL|field|data
name|byte
index|[]
name|data
decl_stmt|;
block|}
block|}
end_class

end_unit

