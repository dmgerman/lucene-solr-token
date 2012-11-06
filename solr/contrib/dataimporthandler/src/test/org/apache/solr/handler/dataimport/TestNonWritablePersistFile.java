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
name|File
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

begin_class
DECL|class|TestNonWritablePersistFile
specifier|public
class|class
name|TestNonWritablePersistFile
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|FULLIMPORT_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|FULLIMPORT_QUERY
init|=
literal|"select * from x"
decl_stmt|;
DECL|field|DELTA_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|DELTA_QUERY
init|=
literal|"select id from x where last_modified> NOW"
decl_stmt|;
DECL|field|DELETED_PK_QUERY
specifier|private
specifier|static
specifier|final
name|String
name|DELETED_PK_QUERY
init|=
literal|"select id from x where last_modified> NOW AND deleted='true'"
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
literal|"<document>\n"
operator|+
literal|"<entity name=\"x\" transformer=\"TemplateTransformer\""
operator|+
literal|"            query=\""
operator|+
name|FULLIMPORT_QUERY
operator|+
literal|"\""
operator|+
literal|"            deletedPkQuery=\""
operator|+
name|DELETED_PK_QUERY
operator|+
literal|"\""
operator|+
literal|"            deltaImportQuery=\"select * from x where id='${dih.delta.id}'\""
operator|+
literal|"            deltaQuery=\""
operator|+
name|DELTA_QUERY
operator|+
literal|"\">\n"
operator|+
literal|"<field column=\"id\" name=\"id\"/>\n"
operator|+
literal|"<entity name=\"y\" query=\"select * from y where y.A='${x.id}'\">\n"
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
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testNonWritablePersistFile
specifier|public
name|void
name|testNonWritablePersistFile
parameter_list|()
throws|throws
name|Exception
block|{
comment|// See SOLR-2551
name|String
name|configDir
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
decl_stmt|;
name|String
name|filePath
init|=
name|configDir
decl_stmt|;
if|if
condition|(
name|configDir
operator|!=
literal|null
operator|&&
operator|!
name|configDir
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
name|filePath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|filePath
operator|+=
literal|"dataimport.properties"
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
try|try
block|{
comment|// execute the test only if we are able to set file to read only mode
name|assumeTrue
argument_list|(
literal|"No dataimport.properties file"
argument_list|,
name|f
operator|.
name|exists
argument_list|()
operator|||
name|f
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
literal|"dataimport.proprties can't be set read only"
argument_list|,
name|f
operator|.
name|setReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"dataimport.proprties is still writable even though "
operator|+
literal|"marked readonly - test running as superuser?"
argument_list|,
name|f
operator|.
name|canWrite
argument_list|()
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"Properties is not writable"
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
name|FULLIMPORT_QUERY
argument_list|,
name|parentRow
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
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
literal|"select * from y where y.A='1'"
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
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

