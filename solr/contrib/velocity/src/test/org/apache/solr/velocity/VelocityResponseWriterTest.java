begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.velocity
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|velocity
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
name|response
operator|.
name|SolrQueryResponse
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
name|VelocityResponseWriter
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
name|junit
operator|.
name|Before
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
name|StringWriter
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

begin_class
DECL|class|VelocityResponseWriterTest
specifier|public
class|class
name|VelocityResponseWriterTest
extends|extends
name|SolrTestCaseJ4
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
name|getFile
argument_list|(
literal|"velocity/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
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
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTemplateName
specifier|public
name|void
name|testTemplateName
parameter_list|()
throws|throws
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|VelocityResponseWriter
name|vrw
init|=
operator|new
name|VelocityResponseWriter
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"v.template"
argument_list|,
literal|"custom"
argument_list|,
literal|"v.template.custom"
argument_list|,
literal|"$response.response.response_data"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response_data"
argument_list|,
literal|"testing"
argument_list|)
expr_stmt|;
name|vrw
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testing"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO: add test that works with true Solr requests and wt=velocity to ensure the test tests that it's registered properly, etc
block|}
end_class

end_unit

