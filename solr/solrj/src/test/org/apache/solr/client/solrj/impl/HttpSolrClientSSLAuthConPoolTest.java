begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
operator|.
name|impl
package|;
end_package

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
name|Collections
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
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
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
name|pool
operator|.
name|PoolStats
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
name|SolrServerException
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
name|util
operator|.
name|RandomizeSSL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|RandomizeSSL
argument_list|(
literal|1.0
argument_list|)
DECL|class|HttpSolrClientSSLAuthConPoolTest
specifier|public
class|class
name|HttpSolrClientSSLAuthConPoolTest
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|yetty
specifier|private
specifier|static
name|JettySolrRunner
name|yetty
decl_stmt|;
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
name|createJetty
argument_list|(
name|legacyExampleCollection1SolrHome
argument_list|()
argument_list|)
expr_stmt|;
comment|// stealing the first made jetty
name|yetty
operator|=
name|jetty
expr_stmt|;
name|createJetty
argument_list|(
name|legacyExampleCollection1SolrHome
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopYetty
specifier|public
specifier|static
name|void
name|stopYetty
parameter_list|()
throws|throws
name|Exception
block|{
name|yetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|yetty
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testPoolSize
specifier|public
name|void
name|testPoolSize
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|PoolingHttpClientConnectionManager
name|pool
init|=
name|HttpClientUtil
operator|.
name|createPoolingConnectionManager
argument_list|()
decl_stmt|;
specifier|final
name|HttpSolrClient
name|client1
decl_stmt|;
specifier|final
name|String
name|fooUrl
decl_stmt|;
block|{
name|fooUrl
operator|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
literal|"collection1"
expr_stmt|;
name|CloseableHttpClient
name|httpClient
init|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|,
name|pool
argument_list|,
literal|false
comment|/* let client shutdown it*/
argument_list|)
decl_stmt|;
name|client1
operator|=
name|getHttpSolrClient
argument_list|(
name|fooUrl
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
name|client1
operator|.
name|setConnectionTimeout
argument_list|(
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|barUrl
init|=
name|yetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
literal|"collection1"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|17
condition|;
name|i
operator|++
control|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|fooUrl
argument_list|)
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
literal|31
condition|;
name|i
operator|++
control|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|barUrl
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|urls
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|url
range|:
name|urls
control|)
block|{
if|if
condition|(
operator|!
name|client1
operator|.
name|getBaseURL
argument_list|()
operator|.
name|equals
argument_list|(
name|url
argument_list|)
condition|)
block|{
name|client1
operator|.
name|setBaseURL
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|client1
operator|.
name|add
argument_list|(
operator|new
name|SolrInputDocument
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
operator|(
name|i
operator|++
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|client1
operator|.
name|setBaseURL
argument_list|(
name|fooUrl
argument_list|)
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|17
argument_list|,
name|client1
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|client1
operator|.
name|setBaseURL
argument_list|(
name|barUrl
argument_list|)
expr_stmt|;
name|client1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|31
argument_list|,
name|client1
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|PoolStats
name|stats
init|=
name|pool
operator|.
name|getTotalStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"oh "
operator|+
name|stats
argument_list|,
literal|2
argument_list|,
name|stats
operator|.
name|getAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|HttpSolrClient
name|c
range|:
operator|new
name|HttpSolrClient
index|[]
block|{
name|client1
block|}
control|)
block|{
name|HttpClientUtil
operator|.
name|close
argument_list|(
name|c
operator|.
name|getHttpClient
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

