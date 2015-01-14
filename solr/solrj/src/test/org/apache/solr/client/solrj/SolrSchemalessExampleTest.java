begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

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
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|InputStreamEntity
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
name|BinaryRequestWriter
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
name|BinaryResponseParser
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
name|util
operator|.
name|ExternalPaths
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|io
operator|.
name|OutputStreamWriter
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
name|HashSet
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|SolrSchemalessExampleTest
specifier|public
class|class
name|SolrSchemalessExampleTest
extends|extends
name|SolrExampleTestsBase
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrSchemalessExampleTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempSolrHome
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
comment|// Schemaless renames schema.xml -> schema.xml.bak, and creates + modifies conf/managed-schema,
comment|// which violates the test security manager's rules, which disallow writes outside the build dir,
comment|// so we copy the example/example-schemaless/solr/ directory to a new temp dir where writes are allowed.
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SERVER_HOME
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
name|tempSolrHome
argument_list|)
expr_stmt|;
name|File
name|collection1Dir
init|=
operator|new
name|File
argument_list|(
name|tempSolrHome
argument_list|,
literal|"collection1"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|collection1Dir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectoryToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SCHEMALESS_CONFIGSET
argument_list|)
argument_list|,
name|collection1Dir
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|OutputStreamWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|collection1Dir
argument_list|,
literal|"core.properties"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|props
operator|.
name|store
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
block|}
name|createJetty
argument_list|(
name|tempSolrHome
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
annotation|@
name|Test
DECL|method|testArbitraryJsonIndexing
specifier|public
name|void
name|testArbitraryJsonIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// two docs, one with uniqueKey, another without it
name|String
name|json
init|=
literal|"{\"id\":\"abc1\", \"name\": \"name1\"} {\"name\" : \"name2\"}"
decl_stmt|;
name|HttpClient
name|httpClient
init|=
name|client
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|HttpPost
name|post
init|=
operator|new
name|HttpPost
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/update/json/docs"
argument_list|)
decl_stmt|;
name|post
operator|.
name|setHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json"
argument_list|)
expr_stmt|;
name|post
operator|.
name|setEntity
argument_list|(
operator|new
name|InputStreamEntity
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|json
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|post
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFieldMutating
specifier|public
name|void
name|testFieldMutating
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// make sure it got in
comment|// two docs, one with uniqueKey, another without it
name|String
name|json
init|=
literal|"{\"name one\": \"name\"} "
operator|+
literal|"{\"name  two\" : \"name\"}"
operator|+
literal|"{\"first-second\" : \"name\"}"
operator|+
literal|"{\"x+y\" : \"name\"}"
operator|+
literal|"{\"p%q\" : \"name\"}"
operator|+
literal|"{\"p.q\" : \"name\"}"
operator|+
literal|"{\"a&b\" : \"name\"}"
decl_stmt|;
name|HttpClient
name|httpClient
init|=
name|client
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|HttpPost
name|post
init|=
operator|new
name|HttpPost
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/update/json/docs"
argument_list|)
decl_stmt|;
name|post
operator|.
name|setHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json"
argument_list|)
expr_stmt|;
name|post
operator|.
name|setEntity
argument_list|(
operator|new
name|InputStreamEntity
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|json
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|post
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"name_one"
argument_list|,
literal|"name__two"
argument_list|,
literal|"first-second"
argument_list|,
literal|"a_b"
argument_list|,
literal|"p_q"
argument_list|,
literal|"p.q"
argument_list|,
literal|"x_y"
argument_list|)
decl_stmt|;
name|HashSet
name|set
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|assertNumFound
argument_list|(
literal|"*:*"
argument_list|,
name|expected
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
name|set
operator|.
name|addAll
argument_list|(
name|doc
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|expected
control|)
block|{
name|assertTrue
argument_list|(
name|s
operator|+
literal|" not created "
operator|+
name|rsp
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createNewSolrClient
specifier|public
name|SolrClient
name|createNewSolrClient
parameter_list|()
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/collection1"
decl_stmt|;
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|client
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|client
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|client
operator|.
name|setUseMultiPartPost
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|client
operator|.
name|setParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

