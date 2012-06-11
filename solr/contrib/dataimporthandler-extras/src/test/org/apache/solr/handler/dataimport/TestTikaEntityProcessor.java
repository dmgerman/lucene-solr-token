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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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

begin_comment
comment|/**Testcase for TikaEntityProcessor  *  * @since solr 3.1  */
end_comment

begin_class
DECL|class|TestTikaEntityProcessor
specifier|public
class|class
name|TestTikaEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|conf
specifier|private
name|String
name|conf
init|=
literal|"<dataConfig>"
operator|+
literal|"<dataSource type=\"BinFileDataSource\"/>"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"Tika\" processor=\"TikaEntityProcessor\" url=\""
operator|+
name|getFile
argument_list|(
literal|"dihextras/solr-word.pdf"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\">"
operator|+
literal|"<field column=\"Author\" meta=\"true\" name=\"author\"/>"
operator|+
literal|"<field column=\"title\" meta=\"true\" name=\"title\"/>"
operator|+
literal|"<field column=\"text\"/>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|tests
specifier|private
name|String
index|[]
name|tests
init|=
block|{
literal|"//*[@numFound='1']"
block|,
literal|"//str[@name='author'][.='Grant Ingersoll']"
block|,
literal|"//str[@name='title'][.='solr-word']"
block|,
literal|"//str[@name='text']"
block|}
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
literal|"dataimport-schema-no-unique-key.xml"
argument_list|,
name|getFile
argument_list|(
literal|"dihextras/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndexingWithTikaEntityProcessor
specifier|public
name|void
name|testIndexingWithTikaEntityProcessor
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

