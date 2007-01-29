begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
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
name|HashMap
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequestWrapper
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
name|solr
operator|.
name|core
operator|.
name|Config
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
name|SolrConfig
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
name|SolrCore
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
name|request
operator|.
name|ContentStream
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
name|request
operator|.
name|MapSolrParams
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
name|request
operator|.
name|MultiMapSolrParams
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
name|request
operator|.
name|SolrParams
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
name|request
operator|.
name|SolrQueryRequest
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
name|AbstractSolrTestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|SolrRequestParserTest
specifier|public
class|class
name|SolrRequestParserTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|field|parser
name|SolrRequestParsers
name|parser
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|parser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
argument_list|,
name|SolrConfig
operator|.
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|testStreamBody
specifier|public
name|void
name|testStreamBody
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|body1
init|=
literal|"AMANAPLANPANAMA"
decl_stmt|;
name|String
name|body2
init|=
literal|"qwertasdfgzxcvb"
decl_stmt|;
name|String
name|body3
init|=
literal|"1234567890"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|STREAM_BODY
argument_list|,
operator|new
name|String
index|[]
block|{
name|body1
block|}
argument_list|)
expr_stmt|;
comment|// Make sure it got a single stream in and out ok
name|List
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|body1
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now add three and make sure they come out ok
name|streams
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|STREAM_BODY
argument_list|,
operator|new
name|String
index|[]
block|{
name|body1
block|,
name|body2
block|,
name|body3
block|}
argument_list|)
expr_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|input
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|output
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|input
operator|.
name|add
argument_list|(
name|body1
argument_list|)
expr_stmt|;
name|input
operator|.
name|add
argument_list|(
name|body2
argument_list|)
expr_stmt|;
name|input
operator|.
name|add
argument_list|(
name|body3
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// sort them so the output is consistent
name|Collections
operator|.
name|sort
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStreamURL
specifier|public
name|void
name|testStreamURL
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
name|String
name|url
init|=
literal|"http://svn.apache.org/repos/asf/lucene/solr/trunk/"
decl_stmt|;
name|String
name|txt
init|=
literal|null
decl_stmt|;
try|try
block|{
name|txt
operator|=
name|IOUtils
operator|.
name|toString
argument_list|(
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// TODO - should it fail/skip?
name|fail
argument_list|(
literal|"this test only works if you have a network connection."
argument_list|)
expr_stmt|;
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|STREAM_URL
argument_list|,
operator|new
name|String
index|[]
block|{
name|url
block|}
argument_list|)
expr_stmt|;
comment|// Make sure it got a single stream in and out ok
name|List
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|parser
operator|.
name|buildRequestFrom
argument_list|(
operator|new
name|MultiMapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|txt
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|streams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

