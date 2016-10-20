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
name|net
operator|.
name|URL
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
name|ExecutionException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|HttpClientConnection
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
name|HttpConnectionMetrics
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
name|HttpException
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
name|HttpHost
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
name|HttpRequest
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
name|HttpVersion
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
name|protocol
operator|.
name|HttpClientContext
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
name|conn
operator|.
name|ConnectionPoolTimeoutException
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
name|conn
operator|.
name|ConnectionRequest
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
name|conn
operator|.
name|routing
operator|.
name|HttpRoute
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
name|message
operator|.
name|BasicHttpRequest
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
name|cloud
operator|.
name|SolrCloudTestCase
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
name|update
operator|.
name|AddUpdateCommand
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
name|TestInjection
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
annotation|@
name|SuppressSSL
DECL|class|ConnectionReuseTest
specifier|public
class|class
name|ConnectionReuseTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|id
specifier|private
name|AtomicInteger
name|id
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
name|HttpClientContext
name|context
init|=
name|HttpClientContext
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|field|COLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION
init|=
literal|"collection1"
decl_stmt|;
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
name|TestInjection
operator|.
name|failUpdateRequests
operator|=
literal|"true:100"
expr_stmt|;
name|configureCluster
argument_list|(
literal|1
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"config"
argument_list|,
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"cloud-minimal"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|COLLECTION
argument_list|,
literal|"config"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|waitForState
argument_list|(
name|COLLECTION
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildClient
specifier|private
name|SolrClient
name|buildClient
parameter_list|(
name|CloseableHttpClient
name|httpClient
parameter_list|,
name|URL
name|url
parameter_list|)
block|{
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
comment|// currently only testing with 1 thread
return|return
name|getConcurrentUpdateSolrClient
argument_list|(
name|url
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|COLLECTION
argument_list|,
name|httpClient
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|getHttpSolrClient
argument_list|(
name|url
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|COLLECTION
argument_list|,
name|httpClient
argument_list|)
return|;
case|case
literal|2
case|:
name|CloudSolrClient
name|client
init|=
name|getCloudSolrClient
argument_list|(
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|httpClient
argument_list|)
decl_stmt|;
name|client
operator|.
name|setParallelUpdates
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|setDefaultCollection
argument_list|(
name|COLLECTION
argument_list|)
expr_stmt|;
name|client
operator|.
name|getLbClient
argument_list|()
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|client
operator|.
name|getLbClient
argument_list|()
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"impossible"
argument_list|)
throw|;
block|}
annotation|@
name|Test
DECL|method|testConnectionReuse
specifier|public
name|void
name|testConnectionReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|PoolingHttpClientConnectionManager
name|cm
init|=
operator|new
name|PoolingHttpClientConnectionManager
argument_list|()
decl_stmt|;
name|CloseableHttpClient
name|httpClient
init|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
literal|null
argument_list|,
name|cm
argument_list|)
decl_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|buildClient
argument_list|(
name|httpClient
argument_list|,
name|url
argument_list|)
init|)
block|{
name|HttpHost
name|target
init|=
operator|new
name|HttpHost
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
argument_list|,
name|url
operator|.
name|getPort
argument_list|()
argument_list|,
name|isSSLMode
argument_list|()
condition|?
literal|"https"
else|:
literal|"http"
argument_list|)
decl_stmt|;
name|HttpRoute
name|route
init|=
operator|new
name|HttpRoute
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|ConnectionRequest
name|mConn
init|=
name|getClientConnectionRequest
argument_list|(
name|httpClient
argument_list|,
name|route
argument_list|,
name|cm
argument_list|)
decl_stmt|;
name|HttpClientConnection
name|conn1
init|=
name|getConn
argument_list|(
name|mConn
argument_list|)
decl_stmt|;
name|headerRequest
argument_list|(
name|target
argument_list|,
name|route
argument_list|,
name|conn1
argument_list|,
name|cm
argument_list|)
expr_stmt|;
name|cm
operator|.
name|releaseConnection
argument_list|(
name|conn1
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|int
name|queueBreaks
init|=
literal|0
decl_stmt|;
name|int
name|cnt1
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|int
name|cnt2
init|=
name|atLeast
argument_list|(
literal|30
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|cnt1
condition|;
name|j
operator|++
control|)
block|{
name|boolean
name|done
init|=
literal|false
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
name|cnt2
condition|;
name|i
operator|++
control|)
block|{
name|AddUpdateCommand
name|c
init|=
operator|new
name|AddUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|c
operator|.
name|solrDoc
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|id
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|add
argument_list|(
name|c
operator|.
name|solrDoc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|done
operator|&&
name|i
operator|>
literal|0
operator|&&
name|i
operator|<
name|cnt2
operator|-
literal|1
operator|&&
name|client
operator|instanceof
name|ConcurrentUpdateSolrClient
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|>
literal|8
condition|)
block|{
name|queueBreaks
operator|++
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|350
argument_list|)
expr_stmt|;
comment|// wait past streaming client poll time of 250ms
block|}
block|}
if|if
condition|(
name|client
operator|instanceof
name|ConcurrentUpdateSolrClient
condition|)
block|{
operator|(
operator|(
name|ConcurrentUpdateSolrClient
operator|)
name|client
operator|)
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
block|}
block|}
name|route
operator|=
operator|new
name|HttpRoute
argument_list|(
operator|new
name|HttpHost
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
argument_list|,
name|url
operator|.
name|getPort
argument_list|()
argument_list|,
name|isSSLMode
argument_list|()
condition|?
literal|"https"
else|:
literal|"http"
argument_list|)
argument_list|)
expr_stmt|;
name|mConn
operator|=
name|cm
operator|.
name|requestConnection
argument_list|(
name|route
argument_list|,
name|HttpSolrClient
operator|.
name|cacheKey
argument_list|)
expr_stmt|;
name|HttpClientConnection
name|conn2
init|=
name|getConn
argument_list|(
name|mConn
argument_list|)
decl_stmt|;
name|HttpConnectionMetrics
name|metrics
init|=
name|conn2
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|headerRequest
argument_list|(
name|target
argument_list|,
name|route
argument_list|,
name|conn2
argument_list|,
name|cm
argument_list|)
expr_stmt|;
name|cm
operator|.
name|releaseConnection
argument_list|(
name|conn2
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"No connection metrics found - is the connection getting aborted? server closing the connection? "
operator|+
name|client
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
comment|// we try and make sure the connection we get has handled all of the requests in this test
if|if
condition|(
name|client
operator|instanceof
name|ConcurrentUpdateSolrClient
condition|)
block|{
comment|// we can't fully control queue polling breaking up requests - allow a bit of leeway
name|int
name|exp
init|=
name|cnt1
operator|+
name|queueBreaks
operator|+
literal|2
decl_stmt|;
name|assertTrue
argument_list|(
literal|"We expected all communication via streaming client to use one connection! expected="
operator|+
name|exp
operator|+
literal|" got="
operator|+
name|metrics
operator|.
name|getRequestCount
argument_list|()
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|exp
argument_list|,
name|metrics
operator|.
name|getRequestCount
argument_list|()
argument_list|)
operator|-
name|Math
operator|.
name|min
argument_list|(
name|exp
argument_list|,
name|metrics
operator|.
name|getRequestCount
argument_list|()
argument_list|)
operator|<
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"We expected all communication to use one connection! "
operator|+
name|client
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" "
operator|+
name|metrics
operator|.
name|getRequestCount
argument_list|()
argument_list|,
name|cnt1
operator|*
name|cnt2
operator|+
literal|2
operator|<=
name|metrics
operator|.
name|getRequestCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|HttpClientUtil
operator|.
name|close
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConn
specifier|public
name|HttpClientConnection
name|getConn
parameter_list|(
name|ConnectionRequest
name|mConn
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ConnectionPoolTimeoutException
throws|,
name|ExecutionException
block|{
name|HttpClientConnection
name|conn
init|=
name|mConn
operator|.
name|get
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
return|return
name|conn
return|;
block|}
DECL|method|headerRequest
specifier|public
name|void
name|headerRequest
parameter_list|(
name|HttpHost
name|target
parameter_list|,
name|HttpRoute
name|route
parameter_list|,
name|HttpClientConnection
name|conn
parameter_list|,
name|PoolingHttpClientConnectionManager
name|cm
parameter_list|)
throws|throws
name|IOException
throws|,
name|HttpException
block|{
name|HttpRequest
name|req
init|=
operator|new
name|BasicHttpRequest
argument_list|(
literal|"OPTIONS"
argument_list|,
literal|"*"
argument_list|,
name|HttpVersion
operator|.
name|HTTP_1_1
argument_list|)
decl_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
literal|"Host"
argument_list|,
name|target
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|conn
operator|.
name|isOpen
argument_list|()
condition|)
block|{
comment|// establish connection based on its route info
name|cm
operator|.
name|connect
argument_list|(
name|conn
argument_list|,
name|route
argument_list|,
literal|1000
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// and mark it as route complete
name|cm
operator|.
name|routeComplete
argument_list|(
name|conn
argument_list|,
name|route
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|conn
operator|.
name|sendRequestHeader
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|conn
operator|.
name|flush
argument_list|()
expr_stmt|;
name|conn
operator|.
name|receiveResponseHeader
argument_list|()
expr_stmt|;
block|}
DECL|method|getClientConnectionRequest
specifier|public
name|ConnectionRequest
name|getClientConnectionRequest
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|HttpRoute
name|route
parameter_list|,
name|PoolingHttpClientConnectionManager
name|cm
parameter_list|)
block|{
name|ConnectionRequest
name|mConn
init|=
name|cm
operator|.
name|requestConnection
argument_list|(
name|route
argument_list|,
name|HttpSolrClient
operator|.
name|cacheKey
argument_list|)
decl_stmt|;
return|return
name|mConn
return|;
block|}
block|}
end_class

end_unit
