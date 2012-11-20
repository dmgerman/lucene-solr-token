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
name|java
operator|.
name|io
operator|.
name|File
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestFileListWithLineEntityProcessor
specifier|public
class|class
name|TestFileListWithLineEntityProcessor
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
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpdir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|"tmp"
argument_list|,
name|TEMP_DIR
argument_list|)
decl_stmt|;
name|tmpdir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tmpdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|tmpdir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.txt"
argument_list|,
literal|"a line one\na line two\na line three"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.txt"
argument_list|,
literal|"b line one\nb line two"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.txt"
argument_list|,
literal|"c line one\nc line two\nc line three\nc line four"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|config
init|=
name|generateConfig
argument_list|(
name|tmpdir
argument_list|)
decl_stmt|;
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"dataConfig"
argument_list|,
name|config
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"synchronous"
argument_list|,
literal|"true"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='9']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:?\\ line\\ one"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:a\\ line*"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:b\\ line*"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:c\\ line*"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
block|}
DECL|method|generateConfig
specifier|private
name|String
name|generateConfig
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
return|return
literal|"<dataConfig> \n"
operator|+
literal|"<dataSource type=\"FileDataSource\" encoding=\"UTF-8\" name=\"fds\"/> \n"
operator|+
literal|"<document> \n"
operator|+
literal|"<entity name=\"f\" processor=\"FileListEntityProcessor\" fileName=\".*[.]txt\" baseDir=\""
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\" recursive=\"false\" rootEntity=\"false\"  transformer=\"TemplateTransformer\"> \n"
operator|+
literal|"<entity name=\"jc\" processor=\"LineEntityProcessor\" url=\"${f.fileAbsolutePath}\" dataSource=\"fds\"  rootEntity=\"true\" transformer=\"TemplateTransformer\"> \n"
operator|+
literal|"<field column=\"rawLine\" name=\"id\" /> \n"
operator|+
literal|"</entity> \n"
operator|+
literal|"</entity> \n"
operator|+
literal|"</document> \n"
operator|+
literal|"</dataConfig> \n"
return|;
block|}
block|}
end_class

end_unit

