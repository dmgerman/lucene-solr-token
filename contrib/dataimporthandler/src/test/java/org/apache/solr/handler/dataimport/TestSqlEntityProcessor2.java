begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
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

begin_comment
comment|/**  *<p>  * Test for SqlEntityProcessor which checks full and delta imports using the  * test harness  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestSqlEntityProcessor2
specifier|public
class|class
name|TestSqlEntityProcessor2
extends|extends
name|AbstractDataImportHandlerTest
block|{
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"dataimport-schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"dataimport-solrconfig.xml"
return|;
block|}
annotation|@
name|Override
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
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_FullImport
specifier|public
name|void
name|testCompositePk_FullImport
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A=1"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|runFullImport
argument_list|(
name|dataConfig
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
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
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
DECL|method|testCompositePk_DeltaImport
specifier|public
name|void
name|testCompositePk_DeltaImport
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select id from x where last_modified> NOW"
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id = '5'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A=5"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:5"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
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
DECL|method|testCompositePk_DeltaImport_DeletedPkQuery
specifier|public
name|void
name|testCompositePk_DeltaImport_DeletedPkQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A=11"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|runFullImport
argument_list|(
name|dataConfig
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:11"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"15"
argument_list|)
argument_list|)
expr_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"17"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select id from x where last_modified> NOW"
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|deltaDeleteRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaDeleteRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|)
argument_list|)
expr_stmt|;
name|deltaDeleteRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"17"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select id from x where last_modified> NOW AND deleted='true'"
argument_list|,
name|deltaDeleteRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|parentRow
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"15"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id = '15'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|parentRow
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"17"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id = '17'"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:15"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:11"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:17"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
DECL|method|testCompositePk_DeltaImport_DeltaImportQuery
specifier|public
name|void
name|testCompositePk_DeltaImport_DeltaImportQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|deltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select id from x where last_modified> NOW"
argument_list|,
name|deltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from x where id=5"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"desc"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y where y.A=5"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|runDeltaImport
argument_list|(
name|dataConfig_deltaimportquery
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:5"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:hello"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|field|dataConfig
specifier|private
specifier|static
name|String
name|dataConfig
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"x\" pk=\"id\" query=\"select * from x\" deletedPkQuery=\"select id from x where last_modified> NOW AND deleted='true'\" deltaQuery=\"select id from x where last_modified> NOW\">\n"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"y\" query=\"select * from y where y.A=${x.id}\">\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>\n"
decl_stmt|;
DECL|field|dataConfig_deltaimportquery
specifier|private
specifier|static
name|String
name|dataConfig_deltaimportquery
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"x\" deltaImportQuery=\"select * from x where id=${dataimporter.delta.id}\" deltaQuery=\"select id from x where last_modified> NOW\">\n"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"y\" query=\"select * from y where y.A=${x.id}\">\n"
operator|+
literal|"<field column=\"desc\" />\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>\n"
decl_stmt|;
block|}
end_class

end_unit

