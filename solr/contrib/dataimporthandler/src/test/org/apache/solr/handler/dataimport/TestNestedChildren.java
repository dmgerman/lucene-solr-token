begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestNestedChildren
specifier|public
class|class
name|TestNestedChildren
extends|extends
name|AbstractDIHJdbcTestCase
block|{
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|generateRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"third_s:CHICKEN"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|generateConfig
specifier|protected
name|String
name|generateConfig
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataConfig> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataSource name=\"derby\" driver=\"org.apache.derby.jdbc.EmbeddedDriver\" url=\"jdbc:derby:memory:derbyDB;territory=en_US\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<document name=\"TestSimplePropertiesWriter\"> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"FIRST\" processor=\"SqlEntityProcessor\" dataSource=\"derby\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" query=\"select 1 as id, 'PORK' as FIRST_S from sysibm.sysdummy1 \">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"FIRST_S\" name=\"first_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"SECOND\" processor=\"SqlEntityProcessor\" dataSource=\"derby\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"   query=\"select 1 as id, 2 as SECOND_ID, 'BEEF' as SECOND_S from sysibm.sysdummy1 WHERE 1=${FIRST.ID}\">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"SECOND_S\" name=\"second_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"THIRD\" processor=\"SqlEntityProcessor\" dataSource=\"derby\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    query=\"select 1 as id, 'CHICKEN' as THIRD_S from sysibm.sysdummy1 WHERE 2=${SECOND.SECOND_ID}\">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"THIRD_S\" name=\"third_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</document> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</dataConfig> \n"
argument_list|)
expr_stmt|;
name|String
name|config
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
annotation|@
name|Override
DECL|method|setAllowedDatabases
specifier|protected
name|Database
name|setAllowedDatabases
parameter_list|()
block|{
return|return
name|Database
operator|.
name|DERBY
return|;
block|}
block|}
end_class

end_unit

