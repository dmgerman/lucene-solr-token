begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
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
name|SolrTestCaseJ4
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SolrException
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
name|schema
operator|.
name|SchemaField
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
name|LinkedHashMap
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
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|QueryParsingTest
specifier|public
class|class
name|QueryParsingTest
extends|extends
name|SolrTestCaseJ4
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the main QParserPlugins people are likely to use    * as defaults fail with a consistent exception when the query string     * is either empty or null.    * @see<a href="https://issues.apache.org/jira/browse/SOLR-435">SOLR-435</a>    * @see<a href="https://issues.apache.org/jira/browse/SOLR-2001">SOLR-2001</a>    */
DECL|method|testQParserEmptyInput
specifier|public
name|void
name|testQParserEmptyInput
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|parsersTested
init|=
operator|new
name|String
index|[]
block|{
name|OldLuceneQParserPlugin
operator|.
name|NAME
block|,
name|LuceneQParserPlugin
operator|.
name|NAME
block|,
name|DisMaxQParserPlugin
operator|.
name|NAME
block|,
name|ExtendedDismaxQParserPlugin
operator|.
name|NAME
block|}
decl_stmt|;
for|for
control|(
name|String
name|defType
range|:
name|parsersTested
control|)
block|{
for|for
control|(
name|String
name|qstr
range|:
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|""
block|}
control|)
block|{
name|QParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|QParser
operator|.
name|getParser
argument_list|(
name|qstr
argument_list|,
name|defType
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"getParser excep using defType="
operator|+
name|defType
operator|+
literal|" with qstr="
operator|+
name|qstr
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Query
name|q
init|=
name|parser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"expected no query"
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testLocalParamsWithModifiableSolrParams
specifier|public
name|void
name|testLocalParamsWithModifiableSolrParams
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|target
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|QueryParsing
operator|.
name|parseLocalParams
argument_list|(
literal|"{!handler foo1=bar1 foo2=bar2 multi=loser multi=winner}"
argument_list|,
literal|0
argument_list|,
name|target
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|,
literal|"{!"
argument_list|,
literal|'}'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar1"
argument_list|,
name|target
operator|.
name|get
argument_list|(
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar2"
argument_list|,
name|target
operator|.
name|get
argument_list|(
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"loser"
block|,
literal|"winner"
block|}
argument_list|,
name|target
operator|.
name|getParams
argument_list|(
literal|"multi"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLiteralFunction
specifier|public
name|void
name|testLiteralFunction
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|NAME
init|=
name|FunctionQParserPlugin
operator|.
name|NAME
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"variable"
argument_list|,
literal|"foobar"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|QParser
operator|.
name|getParser
argument_list|(
literal|"literal('a value')"
argument_list|,
name|NAME
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|QParser
operator|.
name|getParser
argument_list|(
literal|"literal('a value')"
argument_list|,
name|NAME
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|QParser
operator|.
name|getParser
argument_list|(
literal|"literal(\"a value\")"
argument_list|,
name|NAME
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|QParser
operator|.
name|getParser
argument_list|(
literal|"literal($variable)"
argument_list|,
name|NAME
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|QParser
operator|.
name|getParser
argument_list|(
literal|"strdist(\"a value\",literal('a value'),edit)"
argument_list|,
name|NAME
argument_list|,
name|req
argument_list|)
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

