begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for writerImpl paramater (to provide own SolrWriter)  *</p>  *   *   * @since solr 4.0  */
end_comment

begin_class
DECL|class|TestWriterImpl
specifier|public
class|class
name|TestWriterImpl
extends|extends
name|AbstractDataImportHandlerTestCase
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
literal|"dataimport-nodatasource-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testDataConfigWithDataSource
specifier|public
name|void
name|testDataConfigWithDataSource
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"desc"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"desc"
argument_list|,
literal|"break"
argument_list|)
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"desc"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|Map
name|extraParams
init|=
name|createMap
argument_list|(
literal|"writerImpl"
argument_list|,
name|TestSolrWriter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|runFullImport
argument_list|(
name|loadDataConfig
argument_list|(
literal|"data-config-with-datasource.xml"
argument_list|)
argument_list|,
name|extraParams
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:3"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:4"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|class|TestSolrWriter
specifier|public
specifier|static
class|class
name|TestSolrWriter
extends|extends
name|SolrWriter
block|{
DECL|method|TestSolrWriter
specifier|public
name|TestSolrWriter
parameter_list|(
name|UpdateRequestProcessor
name|processor
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|processor
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|upload
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|.
name|getField
argument_list|(
literal|"desc"
argument_list|)
operator|.
name|getFirstValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"break"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|super
operator|.
name|upload
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

