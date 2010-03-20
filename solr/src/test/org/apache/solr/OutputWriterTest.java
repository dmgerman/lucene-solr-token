begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|io
operator|.
name|Writer
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
name|response
operator|.
name|QueryResponseWriter
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/** Tests the ability to configure multiple query output writers, and select those  * at query time.  *  */
end_comment

begin_class
DECL|class|OutputWriterTest
specifier|public
class|class
name|OutputWriterTest
extends|extends
name|AbstractSolrTestCase
block|{
comment|/** The XML string that's output for testing purposes. */
DECL|field|USELESS_OUTPUT
specifier|public
specifier|static
specifier|final
name|String
name|USELESS_OUTPUT
init|=
literal|"useless output"
decl_stmt|;
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"solr/crazy-path-to-schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solr/crazy-path-to-config.xml"
return|;
block|}
comment|/** responseHeader has changed in SOLR-59, check old and new variants */
DECL|method|testSOLR59responseHeaderVersions
specifier|public
name|void
name|testSOLR59responseHeaderVersions
parameter_list|()
block|{
comment|// default version is 2.2, with "new" responseHeader
name|lrf
operator|.
name|args
operator|.
name|remove
argument_list|(
literal|"version"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"standard"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'][.='0']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|remove
argument_list|(
literal|"wt"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='QTime']"
argument_list|)
expr_stmt|;
comment|// version=2.1 reverts to old responseHeader
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.1"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"standard"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"/response/responseHeader/status[.='0']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|remove
argument_list|(
literal|"wt"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"/response/responseHeader/QTime"
argument_list|)
expr_stmt|;
comment|// and explicit 2.2 works as default
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"standard"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'][.='0']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|remove
argument_list|(
literal|"wt"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='QTime']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUselessWriter
specifier|public
name|void
name|testUselessWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"useless"
argument_list|)
expr_stmt|;
name|String
name|out
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|USELESS_OUTPUT
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrivialXsltWriter
specifier|public
name|void
name|testTrivialXsltWriter
parameter_list|()
throws|throws
name|Exception
block|{
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"xslt"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"tr"
argument_list|,
literal|"dummy.xsl"
argument_list|)
expr_stmt|;
name|String
name|out
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
comment|// System.out.println(out);
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"DUMMY"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|////////////////////////////////////////////////////////////////////////////
comment|/** An output writer that doesn't do anything useful. */
DECL|class|UselessOutputWriter
specifier|public
specifier|static
class|class
name|UselessOutputWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|method|UselessOutputWriter
specifier|public
name|UselessOutputWriter
parameter_list|()
block|{}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|n
parameter_list|)
block|{}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
name|USELESS_OUTPUT
argument_list|)
expr_stmt|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|CONTENT_TYPE_TEXT_UTF8
return|;
block|}
block|}
block|}
end_class

end_unit

