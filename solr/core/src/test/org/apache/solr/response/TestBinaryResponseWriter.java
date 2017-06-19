begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|UUID
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
name|params
operator|.
name|CommonParams
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
name|JavaBinCodec
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
name|NamedList
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
name|LocalSolrQueryRequest
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
name|response
operator|.
name|BinaryResponseWriter
operator|.
name|Resolver
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
name|search
operator|.
name|SolrReturnFields
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
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Test for BinaryResponseWriter  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TestBinaryResponseWriter
specifier|public
class|class
name|TestBinaryResponseWriter
extends|extends
name|AbstractSolrTestCase
block|{
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests known types implementation by asserting correct encoding/decoding of UUIDField    */
DECL|method|testUUID
specifier|public
name|void
name|testUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|s
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
literal|"uuid"
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
name|h
operator|.
name|queryAndResponse
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|BinaryQueryResponseWriter
name|writer
init|=
operator|(
name|BinaryQueryResponseWriter
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
literal|"javabin"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|baos
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|res
decl_stmt|;
try|try
init|(
name|JavaBinCodec
name|jbc
init|=
operator|new
name|JavaBinCodec
argument_list|()
init|)
block|{
name|res
operator|=
operator|(
name|NamedList
operator|)
name|jbc
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SolrDocumentList
name|docs
init|=
operator|(
name|SolrDocumentList
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|doc
range|:
name|docs
control|)
block|{
name|SolrDocument
name|document
init|=
operator|(
name|SolrDocument
operator|)
name|doc
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Returned object must be a string"
argument_list|,
literal|"java.lang.String"
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"uuid"
argument_list|)
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong UUID string returned"
argument_list|,
name|s
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"uuid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testResolverSolrDocumentPartialFields
specifier|public
name|void
name|testResolverSolrDocumentPartialFields
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,xxx,ddd_s"
argument_list|)
decl_stmt|;
name|SolrDocument
name|in
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|in
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|345
argument_list|)
expr_stmt|;
name|in
operator|.
name|addField
argument_list|(
literal|"aaa_s"
argument_list|,
literal|"aaa"
argument_list|)
expr_stmt|;
name|in
operator|.
name|addField
argument_list|(
literal|"bbb_s"
argument_list|,
literal|"bbb"
argument_list|)
expr_stmt|;
name|in
operator|.
name|addField
argument_list|(
literal|"ccc_s"
argument_list|,
literal|"ccc"
argument_list|)
expr_stmt|;
name|in
operator|.
name|addField
argument_list|(
literal|"ddd_s"
argument_list|,
literal|"ddd"
argument_list|)
expr_stmt|;
name|in
operator|.
name|addField
argument_list|(
literal|"eee_s"
argument_list|,
literal|"eee"
argument_list|)
expr_stmt|;
name|Resolver
name|r
init|=
operator|new
name|Resolver
argument_list|(
name|req
argument_list|,
operator|new
name|SolrReturnFields
argument_list|(
name|req
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|r
operator|.
name|resolve
argument_list|(
name|in
argument_list|,
operator|new
name|JavaBinCodec
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"obj is null"
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"obj is not doc"
argument_list|,
name|o
operator|instanceof
name|SolrDocument
argument_list|)
expr_stmt|;
name|SolrDocument
name|out
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|in
control|)
block|{
if|if
condition|(
name|r
operator|.
name|isWritable
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
name|out
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"id not found"
argument_list|,
name|out
operator|.
name|getFieldNames
argument_list|()
operator|.
name|contains
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ddd_s not found"
argument_list|,
name|out
operator|.
name|getFieldNames
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ddd_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of fields found"
argument_list|,
literal|2
argument_list|,
name|out
operator|.
name|getFieldNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

