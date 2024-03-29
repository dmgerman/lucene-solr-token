begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request.macro
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|macro
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

begin_class
DECL|class|TestMacros
specifier|public
class|class
name|TestMacros
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMacros
specifier|public
name|void
name|testMacros
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_s"
argument_list|,
literal|"aaa"
argument_list|,
literal|"val_i"
argument_list|,
literal|"123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"val_s"
argument_list|,
literal|"bbb"
argument_list|,
literal|"val_i"
argument_list|,
literal|"456"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"id:${id}"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'1'}]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"${idquery}"
argument_list|,
literal|"idquery"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'1'}]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"${fname}:${fval}"
argument_list|,
literal|"fname"
argument_list|,
literal|"id"
argument_list|,
literal|"fval"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'2'}]"
argument_list|)
expr_stmt|;
comment|// test macro expansion in keys...
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"{!term f=$fieldparam v=$valueparam}"
argument_list|,
literal|"field${p}"
argument_list|,
literal|"val_s"
argument_list|,
literal|"value${p}"
argument_list|,
literal|"aaa"
argument_list|,
literal|"p"
argument_list|,
literal|"param"
argument_list|,
literal|"echoParams"
argument_list|,
literal|"ALL"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'1'}]"
argument_list|)
expr_stmt|;
comment|// test disabling expansion
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"id:\"${id}\""
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"expandMacros"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"/response/docs==[]"
argument_list|)
expr_stmt|;
comment|// test multiple levels in values
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"${idquery}"
argument_list|,
literal|"idquery"
argument_list|,
literal|"${a}${b}"
argument_list|,
literal|"a"
argument_list|,
literal|"val${fieldpostfix}:"
argument_list|,
literal|"b"
argument_list|,
literal|"${fieldval}"
argument_list|,
literal|"fieldpostfix"
argument_list|,
literal|"_s"
argument_list|,
literal|"fieldval"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'2'}]"
argument_list|)
expr_stmt|;
comment|// test defaults
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"val_s:${val:aaa}"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'1'}]"
argument_list|)
expr_stmt|;
comment|// test defaults with value present
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"val_s:${val:aaa}"
argument_list|,
literal|"val"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'2'}]"
argument_list|)
expr_stmt|;
comment|// test zero length default value
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"val_s:${missing:}aaa"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'1'}]"
argument_list|)
expr_stmt|;
comment|// test missing value
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"val_s:${missing}aaa"
argument_list|)
argument_list|,
literal|"/response/docs==[{'id':'1'}]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

