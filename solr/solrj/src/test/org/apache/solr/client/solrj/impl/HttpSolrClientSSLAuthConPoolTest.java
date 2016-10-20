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
name|Arrays
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
name|HttpSolrClientConPoolTest
block|{
annotation|@
name|BeforeClass
DECL|method|checkUrls
specifier|public
specifier|static
name|void
name|checkUrls
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
index|[]
name|urls
init|=
operator|new
name|URL
index|[]
block|{
name|jetty
operator|.
name|getBaseUrl
argument_list|()
block|,
name|yetty
operator|.
name|getBaseUrl
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|URL
name|u
range|:
name|urls
control|)
block|{
name|assertEquals
argument_list|(
literal|"expect https urls "
argument_list|,
literal|"https"
argument_list|,
name|u
operator|.
name|getProtocol
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"expect different urls "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|urls
argument_list|)
argument_list|,
name|urls
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|urls
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
