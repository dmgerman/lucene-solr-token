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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for ScriptTransformer  *</p>  *<p/>  * All tests in this have been ignored because script support is only available  * in Java 1.6+  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestScriptTransformer
specifier|public
class|class
name|TestScriptTransformer
block|{
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|basic
specifier|public
name|void
name|basic
parameter_list|()
block|{
name|String
name|script
init|=
literal|"function f1(row,context){"
operator|+
literal|"row.put('name','Hello ' + row.get('name'));"
operator|+
literal|"return row;\n"
operator|+
literal|"}"
decl_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|"f1"
argument_list|,
name|script
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"Scott"
argument_list|)
expr_stmt|;
name|SqlEntityProcessor
name|sep
init|=
operator|new
name|SqlEntityProcessor
argument_list|()
decl_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|sep
operator|.
name|applyTransformer
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|"Hello Scott"
argument_list|)
expr_stmt|;
block|}
DECL|method|getContext
specifier|private
name|Context
name|getContext
parameter_list|(
name|String
name|funcName
parameter_list|,
name|String
name|script
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entity
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|entity
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|put
argument_list|(
literal|"transformer"
argument_list|,
literal|"script:"
operator|+
name|funcName
argument_list|)
expr_stmt|;
name|AbstractDataImportHandlerTest
operator|.
name|TestContext
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
name|entity
argument_list|)
decl_stmt|;
name|context
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|context
operator|.
name|scriptlang
operator|=
literal|"JavaScript"
expr_stmt|;
return|return
name|context
return|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|oneparam
specifier|public
name|void
name|oneparam
parameter_list|()
block|{
name|String
name|script
init|=
literal|"function f1(row){"
operator|+
literal|"row.put('name','Hello ' + row.get('name'));"
operator|+
literal|"return row;\n"
operator|+
literal|"}"
decl_stmt|;
name|Context
name|context
init|=
name|getContext
argument_list|(
literal|"f1"
argument_list|,
name|script
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"Scott"
argument_list|)
expr_stmt|;
name|SqlEntityProcessor
name|sep
init|=
operator|new
name|SqlEntityProcessor
argument_list|()
decl_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|sep
operator|.
name|applyTransformer
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|"Hello Scott"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|readScriptTag
specifier|public
name|void
name|readScriptTag
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|DataConfig
name|config
init|=
operator|new
name|DataConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|readFromXml
argument_list|(
operator|(
name|Element
operator|)
name|document
operator|.
name|getElementsByTagName
argument_list|(
literal|"dataConfig"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|config
operator|.
name|script
operator|.
name|text
operator|.
name|indexOf
argument_list|(
literal|"checkNextToken"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|checkScript
specifier|public
name|void
name|checkScript
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|DataConfig
name|config
init|=
operator|new
name|DataConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|readFromXml
argument_list|(
operator|(
name|Element
operator|)
name|document
operator|.
name|getElementsByTagName
argument_list|(
literal|"dataConfig"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|"checkNextToken"
argument_list|,
name|config
operator|.
name|script
operator|.
name|text
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"nextToken"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|SqlEntityProcessor
name|sep
init|=
operator|new
name|SqlEntityProcessor
argument_list|()
decl_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|sep
operator|.
name|applyTransformer
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"$hasMore"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"nextToken"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|sep
operator|.
name|applyTransformer
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"$hasMore"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|xml
specifier|static
name|String
name|xml
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<script><![CDATA[\n"
operator|+
literal|"function checkNextToken(row)\t{\n"
operator|+
literal|" var nt = row.get('nextToken');"
operator|+
literal|" if (nt&& nt !='' ){ "
operator|+
literal|"    row.put('$hasMore', 'true');}\n"
operator|+
literal|"    return row;\n"
operator|+
literal|"}]]></script>\t<document>\n"
operator|+
literal|"\t\t<entity name=\"mbx\" pk=\"articleNumber\" processor=\"XPathEntityProcessor\"\n"
operator|+
literal|"\t\t\turl=\"?boardId=${dataimporter.defaults.boardId}&amp;maxRecords=20&amp;includeBody=true&amp;startDate=${dataimporter.defaults.startDate}&amp;guid=:autosearch001&amp;reqId=1&amp;transactionId=stringfortracing&amp;listPos=${mbx.nextToken}\"\n"
operator|+
literal|"\t\t\tforEach=\"/mbmessage/articles/navigation | /mbmessage/articles/article\" transformer=\"script:checkNextToken\">\n"
operator|+
literal|"\n"
operator|+
literal|"\t\t\t<field column=\"nextToken\"\n"
operator|+
literal|"\t\t\t\txpath=\"/mbmessage/articles/navigation/nextToken\" />\n"
operator|+
literal|"\n"
operator|+
literal|"\t\t</entity>\n"
operator|+
literal|"\t</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
block|}
end_class

end_unit

