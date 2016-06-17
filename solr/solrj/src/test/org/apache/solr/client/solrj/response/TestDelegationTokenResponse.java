begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
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
name|response
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|ResponseParser
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
name|DelegationTokenRequest
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
name|SolrException
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
name|noggit
operator|.
name|CharArr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import

begin_class
DECL|class|TestDelegationTokenResponse
specifier|public
class|class
name|TestDelegationTokenResponse
extends|extends
name|LuceneTestCase
block|{
DECL|method|delegationTokenResponse
specifier|private
name|void
name|delegationTokenResponse
parameter_list|(
name|DelegationTokenRequest
name|request
parameter_list|,
name|DelegationTokenResponse
name|response
parameter_list|,
name|String
name|responseBody
parameter_list|)
throws|throws
name|Exception
block|{
name|ResponseParser
name|parser
init|=
name|request
operator|.
name|getResponseParser
argument_list|()
decl_stmt|;
name|response
operator|.
name|setResponse
argument_list|(
name|parser
operator|.
name|processResponse
argument_list|(
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|responseBody
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNestedMapJson
specifier|private
name|String
name|getNestedMapJson
parameter_list|(
name|String
name|outerKey
parameter_list|,
name|String
name|innerKey
parameter_list|,
name|Object
name|innerValue
parameter_list|)
block|{
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|JSONWriter
name|w
init|=
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|innerMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|innerMap
operator|.
name|put
argument_list|(
name|innerKey
argument_list|,
name|innerValue
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|outerMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|outerMap
operator|.
name|put
argument_list|(
name|outerKey
argument_list|,
name|innerMap
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|outerMap
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getMapJson
specifier|private
name|String
name|getMapJson
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|JSONWriter
name|w
init|=
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|map
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testGetResponse
specifier|public
name|void
name|testGetResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|DelegationTokenRequest
operator|.
name|Get
name|getRequest
init|=
operator|new
name|DelegationTokenRequest
operator|.
name|Get
argument_list|()
decl_stmt|;
name|DelegationTokenResponse
operator|.
name|Get
name|getResponse
init|=
operator|new
name|DelegationTokenResponse
operator|.
name|Get
argument_list|()
decl_stmt|;
comment|// not a map
try|try
block|{
name|delegationTokenResponse
argument_list|(
name|getRequest
argument_list|,
name|getResponse
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|getResponse
operator|.
name|getDelegationToken
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected SolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{     }
comment|// doesn't have Token outerMap
specifier|final
name|String
name|someToken
init|=
literal|"someToken"
decl_stmt|;
name|delegationTokenResponse
argument_list|(
name|getRequest
argument_list|,
name|getResponse
argument_list|,
name|getNestedMapJson
argument_list|(
literal|"NotToken"
argument_list|,
literal|"urlString"
argument_list|,
name|someToken
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getResponse
operator|.
name|getDelegationToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// Token is not a map
try|try
block|{
name|delegationTokenResponse
argument_list|(
name|getRequest
argument_list|,
name|getResponse
argument_list|,
name|getMapJson
argument_list|(
literal|"Token"
argument_list|,
name|someToken
argument_list|)
argument_list|)
expr_stmt|;
name|getResponse
operator|.
name|getDelegationToken
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected SolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{     }
comment|// doesn't have urlString
name|delegationTokenResponse
argument_list|(
name|getRequest
argument_list|,
name|getResponse
argument_list|,
name|getNestedMapJson
argument_list|(
literal|"Token"
argument_list|,
literal|"notUrlString"
argument_list|,
name|someToken
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getResponse
operator|.
name|getDelegationToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// has Token + urlString
name|delegationTokenResponse
argument_list|(
name|getRequest
argument_list|,
name|getResponse
argument_list|,
name|getNestedMapJson
argument_list|(
literal|"Token"
argument_list|,
literal|"urlString"
argument_list|,
name|someToken
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|someToken
argument_list|,
name|getResponse
operator|.
name|getDelegationToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenewResponse
specifier|public
name|void
name|testRenewResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|DelegationTokenRequest
operator|.
name|Renew
name|renewRequest
init|=
operator|new
name|DelegationTokenRequest
operator|.
name|Renew
argument_list|(
literal|"token"
argument_list|)
decl_stmt|;
name|DelegationTokenResponse
operator|.
name|Renew
name|renewResponse
init|=
operator|new
name|DelegationTokenResponse
operator|.
name|Renew
argument_list|()
decl_stmt|;
comment|// not a map
try|try
block|{
name|delegationTokenResponse
argument_list|(
name|renewRequest
argument_list|,
name|renewResponse
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|renewResponse
operator|.
name|getExpirationTime
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected SolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{     }
comment|// doesn't have long
name|delegationTokenResponse
argument_list|(
name|renewRequest
argument_list|,
name|renewResponse
argument_list|,
name|getMapJson
argument_list|(
literal|"notLong"
argument_list|,
literal|"123"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|renewResponse
operator|.
name|getExpirationTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// long isn't valid
try|try
block|{
name|delegationTokenResponse
argument_list|(
name|renewRequest
argument_list|,
name|renewResponse
argument_list|,
name|getMapJson
argument_list|(
literal|"long"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|renewResponse
operator|.
name|getExpirationTime
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected SolrException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{     }
comment|// valid
name|Long
name|expirationTime
init|=
operator|new
name|Long
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|delegationTokenResponse
argument_list|(
name|renewRequest
argument_list|,
name|renewResponse
argument_list|,
name|getMapJson
argument_list|(
literal|"long"
argument_list|,
name|expirationTime
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expirationTime
argument_list|,
name|renewResponse
operator|.
name|getExpirationTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCancelResponse
specifier|public
name|void
name|testCancelResponse
parameter_list|()
throws|throws
name|Exception
block|{
comment|// expect empty response
name|DelegationTokenRequest
operator|.
name|Cancel
name|cancelRequest
init|=
operator|new
name|DelegationTokenRequest
operator|.
name|Cancel
argument_list|(
literal|"token"
argument_list|)
decl_stmt|;
name|DelegationTokenResponse
operator|.
name|Cancel
name|cancelResponse
init|=
operator|new
name|DelegationTokenResponse
operator|.
name|Cancel
argument_list|()
decl_stmt|;
name|delegationTokenResponse
argument_list|(
name|cancelRequest
argument_list|,
name|cancelResponse
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
