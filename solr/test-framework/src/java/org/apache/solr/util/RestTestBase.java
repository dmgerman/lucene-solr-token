begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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
name|JSONTestUtil
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
name|SolrJettyTestBase
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
name|common
operator|.
name|params
operator|.
name|MultiMapSolrParams
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
name|util
operator|.
name|StrUtils
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
name|servlet
operator|.
name|SolrRequestParsers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|SAXException
import|;
end_import

begin_class
DECL|class|RestTestBase
specifier|abstract
specifier|public
class|class
name|RestTestBase
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RestTestBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|restTestHarness
specifier|protected
specifier|static
name|RestTestHarness
name|restTestHarness
decl_stmt|;
DECL|method|createJettyAndHarness
specifier|public
specifier|static
name|void
name|createJettyAndHarness
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|configFile
parameter_list|,
name|String
name|schemaFile
parameter_list|,
name|String
name|context
parameter_list|,
name|boolean
name|stopAtShutdown
parameter_list|,
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
parameter_list|)
throws|throws
name|Exception
block|{
name|createJetty
argument_list|(
name|solrHome
argument_list|,
name|configFile
argument_list|,
name|schemaFile
argument_list|,
name|context
argument_list|,
name|stopAtShutdown
argument_list|,
name|extraServlets
argument_list|)
expr_stmt|;
name|restTestHarness
operator|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|RESTfulServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Validates an update XML String is successful    */
DECL|method|assertU
specifier|public
specifier|static
name|void
name|assertU
parameter_list|(
name|String
name|update
parameter_list|)
block|{
name|assertU
argument_list|(
literal|null
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
comment|/** Validates an update XML String is successful    */
DECL|method|assertU
specifier|public
specifier|static
name|void
name|assertU
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|update
parameter_list|)
block|{
name|checkUpdateU
argument_list|(
name|message
argument_list|,
name|update
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Validates an update XML String failed    */
DECL|method|assertFailedU
specifier|public
specifier|static
name|void
name|assertFailedU
parameter_list|(
name|String
name|update
parameter_list|)
block|{
name|assertFailedU
argument_list|(
literal|null
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
comment|/** Validates an update XML String failed    */
DECL|method|assertFailedU
specifier|public
specifier|static
name|void
name|assertFailedU
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|update
parameter_list|)
block|{
name|checkUpdateU
argument_list|(
name|message
argument_list|,
name|update
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Checks the success or failure of an update message    */
DECL|method|checkUpdateU
specifier|private
specifier|static
name|void
name|checkUpdateU
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|update
parameter_list|,
name|boolean
name|shouldSucceed
parameter_list|)
block|{
try|try
block|{
name|String
name|m
init|=
operator|(
literal|null
operator|==
name|message
operator|)
condition|?
literal|""
else|:
name|message
operator|+
literal|" "
decl_stmt|;
if|if
condition|(
name|shouldSucceed
condition|)
block|{
name|String
name|response
init|=
name|restTestHarness
operator|.
name|validateUpdate
argument_list|(
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
name|fail
argument_list|(
name|m
operator|+
literal|"update was not successful: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|response
init|=
name|restTestHarness
operator|.
name|validateErrorUpdate
argument_list|(
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
name|fail
argument_list|(
name|m
operator|+
literal|"update succeeded, but should have failed: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid XML"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**     * Validates a query matches some XPath test expressions    *     * @param request a URL path with optional query params, e.g. "/schema/fields?fl=id,_version_"     */
DECL|method|assertQ
specifier|public
specifier|static
name|void
name|assertQ
parameter_list|(
name|String
name|request
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
block|{
try|try
block|{
name|int
name|queryStartPos
init|=
name|request
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
name|String
name|query
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|queryStartPos
condition|)
block|{
name|query
operator|=
literal|""
expr_stmt|;
name|path
operator|=
name|request
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|request
operator|.
name|substring
argument_list|(
name|queryStartPos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|path
operator|=
name|request
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queryStartPos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|query
operator|.
name|matches
argument_list|(
literal|".*wt=schema\\.xml.*"
argument_list|)
condition|)
block|{
comment|// don't overwrite wt=schema.xml
name|query
operator|=
name|setParam
argument_list|(
name|query
argument_list|,
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
block|}
name|request
operator|=
name|path
operator|+
literal|'?'
operator|+
name|setParam
argument_list|(
name|query
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|restTestHarness
operator|.
name|query
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// TODO: should the facet handling below be converted to parse the URL?
comment|/*       if (req.getParams().getBool("facet", false)) {         // add a test to ensure that faceting did not throw an exception         // internally, where it would be added to facet_counts/exception         String[] allTests = new String[tests.length+1];         System.arraycopy(tests,0,allTests,1,tests.length);         allTests[0] = "*[count(//lst[@name='facet_counts']/*[@name='exception'])=0]";         tests = allTests;       }       */
name|String
name|results
init|=
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|tests
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|results
condition|)
block|{
name|String
name|msg
init|=
literal|"REQUEST FAILED: xpath="
operator|+
name|results
operator|+
literal|"\n\txml response was: "
operator|+
name|response
operator|+
literal|"\n\trequest was:"
operator|+
name|request
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"XPath is invalid"
argument_list|,
name|e1
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"REQUEST FAILED: "
operator|+
name|request
argument_list|,
name|e2
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Exception during query"
argument_list|,
name|e2
argument_list|)
throw|;
block|}
block|}
comment|/**    *  Makes a query request and returns the JSON string response     *    * @param request a URL path with optional query params, e.g. "/schema/fields?fl=id,_version_"     */
DECL|method|JQ
specifier|public
specifier|static
name|String
name|JQ
parameter_list|(
name|String
name|request
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|queryStartPos
init|=
name|request
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
name|String
name|query
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|queryStartPos
condition|)
block|{
name|query
operator|=
literal|""
expr_stmt|;
name|path
operator|=
name|request
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|request
operator|.
name|substring
argument_list|(
name|queryStartPos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|path
operator|=
name|request
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queryStartPos
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|setParam
argument_list|(
name|query
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|request
operator|=
name|path
operator|+
literal|'?'
operator|+
name|setParam
argument_list|(
name|query
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|String
name|response
decl_stmt|;
name|boolean
name|failed
init|=
literal|true
decl_stmt|;
try|try
block|{
name|response
operator|=
name|restTestHarness
operator|.
name|query
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"REQUEST FAILED: "
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
return|;
block|}
comment|/**    * Validates a query matches some JSON test expressions using the default double delta tolerance.    * @see org.apache.solr.JSONTestUtil#DEFAULT_DELTA    * @see #assertJQ(String,double,String...)    */
DECL|method|assertJQ
specifier|public
specifier|static
name|void
name|assertJQ
parameter_list|(
name|String
name|request
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|request
argument_list|,
name|JSONTestUtil
operator|.
name|DEFAULT_DELTA
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates a query matches some JSON test expressions and closes the    * query. The text expression is of the form path:JSON.  To facilitate    * easy embedding in Java strings, the JSON can have double quotes    * replaced with single quotes.    *<p>    * Please use this with care: this makes it easy to match complete    * structures, but doing so can result in fragile tests if you are    * matching more than what you want to test.    *</p>    * @param request a URL path with optional query params, e.g. "/schema/fields?fl=id,_version_"    * @param delta tolerance allowed in comparing float/double values    * @param tests JSON path expression + '==' + expected value    */
DECL|method|assertJQ
specifier|public
specifier|static
name|void
name|assertJQ
parameter_list|(
name|String
name|request
parameter_list|,
name|double
name|delta
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|queryStartPos
init|=
name|request
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
name|String
name|query
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|queryStartPos
condition|)
block|{
name|query
operator|=
literal|""
expr_stmt|;
name|path
operator|=
name|request
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|request
operator|.
name|substring
argument_list|(
name|queryStartPos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|path
operator|=
name|request
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queryStartPos
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|setParam
argument_list|(
name|query
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|request
operator|=
name|path
operator|+
literal|'?'
operator|+
name|setParam
argument_list|(
name|query
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|String
name|response
decl_stmt|;
name|boolean
name|failed
init|=
literal|true
decl_stmt|;
try|try
block|{
name|response
operator|=
name|restTestHarness
operator|.
name|query
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"REQUEST FAILED: "
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|test
range|:
name|tests
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|test
operator|||
literal|0
operator|==
name|test
operator|.
name|length
argument_list|()
condition|)
continue|continue;
name|String
name|testJSON
init|=
name|json
argument_list|(
name|test
argument_list|)
decl_stmt|;
try|try
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|String
name|err
init|=
name|JSONTestUtil
operator|.
name|match
argument_list|(
name|response
argument_list|,
name|testJSON
argument_list|,
name|delta
argument_list|)
decl_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"query failed JSON validation. error="
operator|+
name|err
operator|+
literal|"\n expected ="
operator|+
name|testJSON
operator|+
literal|"\n response = "
operator|+
name|response
operator|+
literal|"\n request = "
operator|+
name|request
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"JSON query validation threw an exception."
operator|+
literal|"\n expected ="
operator|+
name|testJSON
operator|+
literal|"\n response = "
operator|+
name|response
operator|+
literal|"\n request = "
operator|+
name|request
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Validates the response from a PUT request matches some JSON test expressions    *     * @see org.apache.solr.JSONTestUtil#DEFAULT_DELTA    * @see #assertJQ(String,double,String...)    */
DECL|method|assertJPut
specifier|public
specifier|static
name|void
name|assertJPut
parameter_list|(
name|String
name|request
parameter_list|,
name|String
name|content
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|assertJPut
argument_list|(
name|request
argument_list|,
name|content
argument_list|,
name|JSONTestUtil
operator|.
name|DEFAULT_DELTA
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates the response from a PUT request matches some JSON test expressions    * and closes the query. The text expression is of the form path==JSON.    * To facilitate easy embedding in Java strings, the JSON can have double    * quotes replaced with single quotes.    *<p>    * Please use this with care: this makes it easy to match complete    * structures, but doing so can result in fragile tests if you are    * matching more than what you want to test.    *</p>    * @param request a URL path with optional query params, e.g. "/schema/fields?fl=id,_version_"    * @param content The content to include with the PUT request    * @param delta tolerance allowed in comparing float/double values    * @param tests JSON path expression + '==' + expected value    */
DECL|method|assertJPut
specifier|public
specifier|static
name|void
name|assertJPut
parameter_list|(
name|String
name|request
parameter_list|,
name|String
name|content
parameter_list|,
name|double
name|delta
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|queryStartPos
init|=
name|request
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
name|String
name|query
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|queryStartPos
condition|)
block|{
name|query
operator|=
literal|""
expr_stmt|;
name|path
operator|=
name|request
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|request
operator|.
name|substring
argument_list|(
name|queryStartPos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|path
operator|=
name|request
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queryStartPos
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|setParam
argument_list|(
name|query
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|request
operator|=
name|path
operator|+
literal|'?'
operator|+
name|setParam
argument_list|(
name|query
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|String
name|response
decl_stmt|;
name|boolean
name|failed
init|=
literal|true
decl_stmt|;
try|try
block|{
name|response
operator|=
name|restTestHarness
operator|.
name|put
argument_list|(
name|request
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"REQUEST FAILED: "
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|test
range|:
name|tests
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|test
operator|||
literal|0
operator|==
name|test
operator|.
name|length
argument_list|()
condition|)
continue|continue;
name|String
name|testJSON
init|=
name|json
argument_list|(
name|test
argument_list|)
decl_stmt|;
try|try
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|String
name|err
init|=
name|JSONTestUtil
operator|.
name|match
argument_list|(
name|response
argument_list|,
name|testJSON
argument_list|,
name|delta
argument_list|)
decl_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"query failed JSON validation. error="
operator|+
name|err
operator|+
literal|"\n expected ="
operator|+
name|testJSON
operator|+
literal|"\n response = "
operator|+
name|response
operator|+
literal|"\n request = "
operator|+
name|request
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"JSON query validation threw an exception."
operator|+
literal|"\n expected ="
operator|+
name|testJSON
operator|+
literal|"\n response = "
operator|+
name|response
operator|+
literal|"\n request = "
operator|+
name|request
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Validates the response from a POST request matches some JSON test expressions    *    * @see org.apache.solr.JSONTestUtil#DEFAULT_DELTA    * @see #assertJQ(String,double,String...)    */
DECL|method|assertJPost
specifier|public
specifier|static
name|void
name|assertJPost
parameter_list|(
name|String
name|request
parameter_list|,
name|String
name|content
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|assertJPost
argument_list|(
name|request
argument_list|,
name|content
argument_list|,
name|JSONTestUtil
operator|.
name|DEFAULT_DELTA
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates the response from a PUT request matches some JSON test expressions    * and closes the query. The text expression is of the form path==JSON.    * To facilitate easy embedding in Java strings, the JSON can have double    * quotes replaced with single quotes.    *<p>    * Please use this with care: this makes it easy to match complete    * structures, but doing so can result in fragile tests if you are    * matching more than what you want to test.    *</p>    * @param request a URL path with optional query params, e.g. "/schema/fields?fl=id,_version_"    * @param content The content to include with the PUT request    * @param delta tolerance allowed in comparing float/double values    * @param tests JSON path expression + '==' + expected value    */
DECL|method|assertJPost
specifier|public
specifier|static
name|void
name|assertJPost
parameter_list|(
name|String
name|request
parameter_list|,
name|String
name|content
parameter_list|,
name|double
name|delta
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|queryStartPos
init|=
name|request
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
name|String
name|query
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|queryStartPos
condition|)
block|{
name|query
operator|=
literal|""
expr_stmt|;
name|path
operator|=
name|request
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|request
operator|.
name|substring
argument_list|(
name|queryStartPos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|path
operator|=
name|request
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|queryStartPos
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|setParam
argument_list|(
name|query
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|request
operator|=
name|path
operator|+
literal|'?'
operator|+
name|setParam
argument_list|(
name|query
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|String
name|response
decl_stmt|;
name|boolean
name|failed
init|=
literal|true
decl_stmt|;
try|try
block|{
name|response
operator|=
name|restTestHarness
operator|.
name|post
argument_list|(
name|request
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"REQUEST FAILED: "
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|test
range|:
name|tests
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|test
operator|||
literal|0
operator|==
name|test
operator|.
name|length
argument_list|()
condition|)
continue|continue;
name|String
name|testJSON
init|=
name|json
argument_list|(
name|test
argument_list|)
decl_stmt|;
try|try
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|String
name|err
init|=
name|JSONTestUtil
operator|.
name|match
argument_list|(
name|response
argument_list|,
name|testJSON
argument_list|,
name|delta
argument_list|)
decl_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"query failed JSON validation. error="
operator|+
name|err
operator|+
literal|"\n expected ="
operator|+
name|testJSON
operator|+
literal|"\n response = "
operator|+
name|response
operator|+
literal|"\n request = "
operator|+
name|request
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"JSON query validation threw an exception."
operator|+
literal|"\n expected ="
operator|+
name|testJSON
operator|+
literal|"\n response = "
operator|+
name|response
operator|+
literal|"\n request = "
operator|+
name|request
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Insures that the given param is included in the query with the given value.    *    *<ol>    *<li>If the param is already included with the given value, the request is returned unchanged.</li>    *<li>If the param is not already included, it is added with the given value.</li>    *<li>If the param is already included, but with a different value, the value is replaced with the given value.</li>    *<li>If the param is already included multiple times, they are replaced with a single param with given value.</li>    *</ol>    *    * The passed-in valueToSet should NOT be URL encoded, as it will be URL encoded by this method.    *    * @param query The query portion of a request URL, e.g. "wt=json&indent=on&fl=id,_version_"    * @param paramToSet The parameter name to insure the presence of in the returned request     * @param valueToSet The parameter value to insure in the returned request    * @return The query with the given param set to the given value     */
DECL|method|setParam
specifier|private
specifier|static
name|String
name|setParam
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|paramToSet
parameter_list|,
name|String
name|valueToSet
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|valueToSet
condition|)
block|{
name|valueToSet
operator|=
literal|""
expr_stmt|;
block|}
try|try
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|query
operator|||
name|query
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// empty query -> return "paramToSet=valueToSet"
name|builder
operator|.
name|append
argument_list|(
name|paramToSet
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|StrUtils
operator|.
name|partialURLEncodeVal
argument_list|(
name|builder
argument_list|,
name|valueToSet
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
name|MultiMapSolrParams
name|requestParams
init|=
name|SolrRequestParsers
operator|.
name|parseQueryString
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
name|requestParams
operator|.
name|getParams
argument_list|(
name|paramToSet
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|values
condition|)
block|{
comment|// paramToSet isn't present in the request -> append "&paramToSet=valueToSet"
name|builder
operator|.
name|append
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|paramToSet
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|StrUtils
operator|.
name|partialURLEncodeVal
argument_list|(
name|builder
argument_list|,
name|valueToSet
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
if|if
condition|(
literal|1
operator|==
name|values
operator|.
name|length
operator|&&
name|valueToSet
operator|.
name|equals
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|// paramToSet=valueToSet is already in the query - just return the query as-is.
return|return
name|query
return|;
block|}
comment|// More than one value for paramToSet on the request, or paramToSet's value is not valueToSet
comment|// -> rebuild the query
name|boolean
name|isFirst
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|requestParams
operator|.
name|getMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|valarr
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
name|paramToSet
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|val
range|:
name|valarr
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|isFirst
condition|?
literal|""
else|:
literal|'&'
argument_list|)
expr_stmt|;
name|isFirst
operator|=
literal|false
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|StrUtils
operator|.
name|partialURLEncodeVal
argument_list|(
name|builder
argument_list|,
literal|null
operator|==
name|val
condition|?
literal|""
else|:
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|builder
operator|.
name|append
argument_list|(
name|isFirst
condition|?
literal|""
else|:
literal|'&'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|paramToSet
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|StrUtils
operator|.
name|partialURLEncodeVal
argument_list|(
name|builder
argument_list|,
name|valueToSet
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

