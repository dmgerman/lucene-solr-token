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

begin_class
DECL|class|TestSqlEntityProcessorDelta3
specifier|public
class|class
name|TestSqlEntityProcessorDelta3
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|P_FULLIMPORT_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|P_FULLIMPORT_QUERY
init|=
literal|"select * from parent"
decl_stmt|;
DECL|field|P_DELTA_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|P_DELTA_QUERY
init|=
literal|"select parent_id from parent where last_modified> NOW"
decl_stmt|;
DECL|field|P_DELTAIMPORT_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|P_DELTAIMPORT_QUERY
init|=
literal|"select * from parent where last_modified> NOW AND parent_id=${dih.delta.parent_id}"
decl_stmt|;
DECL|field|C_FULLIMPORT_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|C_FULLIMPORT_QUERY
init|=
literal|"select * from child"
decl_stmt|;
DECL|field|C_DELETED_PK_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|C_DELETED_PK_QUERY
init|=
literal|"select id from child where last_modified> NOW AND deleted='true'"
decl_stmt|;
DECL|field|C_DELTA_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|C_DELTA_QUERY
init|=
literal|"select id from child where last_modified> NOW"
decl_stmt|;
DECL|field|C_PARENTDELTA_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|C_PARENTDELTA_QUERY
init|=
literal|"select parent_id from child where id=${child.id}"
decl_stmt|;
DECL|field|C_DELTAIMPORT_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|C_DELTAIMPORT_QUERY
init|=
literal|"select * from child where last_modified> NOW AND parent_id=${dih.delta.parent_id}"
decl_stmt|;
DECL|field|dataConfig_delta
specifier|private
specifier|static
specifier|final
name|String
name|dataConfig_delta
init|=
literal|"<dataConfig>"
operator|+
literal|"<dataSource  type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"parent\" pk=\"parent_id\" rootEntity=\"false\""
operator|+
literal|"            query=\""
operator|+
name|P_FULLIMPORT_QUERY
operator|+
literal|"\""
operator|+
literal|"            deltaQuery=\""
operator|+
name|P_DELTA_QUERY
operator|+
literal|"\""
operator|+
literal|"            deltaImportQuery=\""
operator|+
name|P_DELTAIMPORT_QUERY
operator|+
literal|"\">"
operator|+
literal|"<field column=\"desc\" name=\"desc\"/>"
operator|+
literal|"<entity name=\"child\" pk=\"id\" rootEntity=\"true\""
operator|+
literal|"              query=\""
operator|+
name|C_FULLIMPORT_QUERY
operator|+
literal|"\""
operator|+
literal|"              deletedPkQuery=\""
operator|+
name|C_DELETED_PK_QUERY
operator|+
literal|"\""
operator|+
literal|"              deltaQuery=\""
operator|+
name|C_DELTA_QUERY
operator|+
literal|"\""
operator|+
literal|"              parentDeltaQuery=\""
operator|+
name|C_PARENTDELTA_QUERY
operator|+
literal|"\""
operator|+
literal|"              deltaImportQuery=\""
operator|+
name|C_DELTAIMPORT_QUERY
operator|+
literal|"\">"
operator|+
literal|"<field column=\"id\" name=\"id\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>\n"
decl_stmt|;
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
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|add1document
specifier|private
name|void
name|add1document
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
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"d1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|P_FULLIMPORT_QUERY
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
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_FULLIMPORT_QUERY
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runFullImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR add1document"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
literal|"desc:d1"
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
DECL|method|testCompositePk_FullImport
specifier|public
name|void
name|testCompositePk_FullImport
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
block|}
comment|// WORKS
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testCompositePk_DeltaImport_delete
specifier|public
name|void
name|testCompositePk_DeltaImport_delete
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|List
name|deletedRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deletedRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELETED_PK_QUERY
argument_list|,
name|deletedRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELTA_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|deletedParentRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|deletedParentRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select parent_id from child where id=2"
argument_list|,
name|deletedParentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_delete"
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
DECL|method|testCompositePk_DeltaImport_empty
specifier|public
name|void
name|testCompositePk_DeltaImport_empty
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|childDeltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childDeltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELTA_QUERY
argument_list|,
name|childDeltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELETED_PK_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childParentDeltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childParentDeltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select parent_id from child where id=2"
argument_list|,
name|childParentDeltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|P_DELTA_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|parentDeltaImportRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentDeltaImportRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"d1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from parent where last_modified> NOW AND parent_id=1"
argument_list|,
name|parentDeltaImportRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childDeltaImportRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childDeltaImportRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from child where last_modified> NOW AND parent_id=1"
argument_list|,
name|childDeltaImportRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_empty"
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
literal|"desc:d1"
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
DECL|method|testCompositePk_DeltaImport_replace_nodelete
specifier|public
name|void
name|testCompositePk_DeltaImport_replace_nodelete
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
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
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|P_DELTA_QUERY
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
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"d2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from parent where last_modified> NOW AND parent_id=1"
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
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from child where last_modified> NOW AND parent_id=1"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELETED_PK_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR XtestCompositePk_DeltaImport_replace_nodelete"
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
literal|"desc:s1 OR XtestCompositePk_DeltaImport_replace_nodelete"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:d2"
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
DECL|method|testCompositePk_DeltaImport_add
specifier|public
name|void
name|testCompositePk_DeltaImport_add
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|List
name|parentDeltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentDeltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|P_DELTA_QUERY
argument_list|,
name|parentDeltaRow
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
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|,
literal|"desc"
argument_list|,
literal|"d1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from parent where last_modified> NOW AND parent_id=1"
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childDeltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childDeltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELTA_QUERY
argument_list|,
name|childDeltaRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|childParentDeltaRow
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|childParentDeltaRow
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"parent_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select parent_id from child where id='3'"
argument_list|,
name|childParentDeltaRow
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
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from child where last_modified> NOW AND parent_id=1"
argument_list|,
name|childRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_add"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
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
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:d1"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
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
DECL|method|testCompositePk_DeltaImport_nodelta
specifier|public
name|void
name|testCompositePk_DeltaImport_nodelta
parameter_list|()
throws|throws
name|Exception
block|{
name|add1document
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|P_DELTA_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|C_DELTA_QUERY
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|runDeltaImport
argument_list|(
name|dataConfig_delta
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:* OR testCompositePk_DeltaImport_nodelta"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2 OR testCompositePk_DeltaImport_nodelta"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"desc:d1 OR testCompositePk_DeltaImport_nodelta"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

