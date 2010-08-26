begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCaseJ4
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
name|SolrInputDocument
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
name|SolrInputField
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
name|params
operator|.
name|SolrParams
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
name|XML
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
name|core
operator|.
name|SolrConfig
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
name|util
operator|.
name|TestHarness
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|io
operator|.
name|IOException
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
name|HashSet
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * A junit4 Solr test harness that extends LuceneTestCaseJ4.  * Unlike AbstractSolrTestCase, a new core is not created for each test method.  *  */
end_comment

begin_class
DECL|class|SolrTestCaseJ4
specifier|public
class|class
name|SolrTestCaseJ4
extends|extends
name|LuceneTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClassSolrTestCase
specifier|public
specifier|static
name|void
name|beforeClassSolrTestCase
parameter_list|()
throws|throws
name|Exception
block|{
name|ignoreException
argument_list|(
literal|"ignore_exception"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassSolrTestCase
specifier|public
specifier|static
name|void
name|afterClassSolrTestCase
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
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
name|log
operator|.
name|info
argument_list|(
literal|"###Starting "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// returns<unknown>???
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
name|log
operator|.
name|info
argument_list|(
literal|"###Ending "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** Call initCore in @BeforeClass to instantiate a solr core in your test class.    * deleteCore will be called for you via SolrTestCaseJ4 @AfterClass */
DECL|method|initCore
specifier|public
specifier|static
name|void
name|initCore
parameter_list|(
name|String
name|config
parameter_list|,
name|String
name|schema
parameter_list|)
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
name|config
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Call initCore in @BeforeClass to instantiate a solr core in your test class.    * deleteCore will be called for you via SolrTestCaseJ4 @AfterClass */
DECL|method|initCore
specifier|public
specifier|static
name|void
name|initCore
parameter_list|(
name|String
name|config
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|solrHome
parameter_list|)
throws|throws
name|Exception
block|{
name|configString
operator|=
name|config
expr_stmt|;
name|schemaString
operator|=
name|schema
expr_stmt|;
if|if
condition|(
name|solrHome
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
block|}
name|initCore
argument_list|()
expr_stmt|;
block|}
comment|/** Causes an exception matching the regex pattern to not be logged. */
DECL|method|ignoreException
specifier|public
specifier|static
name|void
name|ignoreException
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
if|if
condition|(
name|SolrException
operator|.
name|ignorePatterns
operator|==
literal|null
condition|)
name|SolrException
operator|.
name|ignorePatterns
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|SolrException
operator|.
name|ignorePatterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
DECL|method|resetExceptionIgnores
specifier|public
specifier|static
name|void
name|resetExceptionIgnores
parameter_list|()
block|{
name|SolrException
operator|.
name|ignorePatterns
operator|=
literal|null
expr_stmt|;
name|ignoreException
argument_list|(
literal|"ignore_exception"
argument_list|)
expr_stmt|;
comment|// always ignore "ignore_exception"
block|}
DECL|method|getClassName
specifier|protected
specifier|static
name|String
name|getClassName
parameter_list|()
block|{
name|StackTraceElement
index|[]
name|stack
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"WhoAmI"
argument_list|)
operator|.
name|fillInStackTrace
argument_list|()
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|stack
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|StackTraceElement
name|ste
init|=
name|stack
index|[
name|i
index|]
decl_stmt|;
name|String
name|cname
init|=
name|ste
operator|.
name|getClassName
argument_list|()
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|indexOf
argument_list|(
literal|".lucene."
argument_list|)
operator|>=
literal|0
operator|||
name|cname
operator|.
name|indexOf
argument_list|(
literal|".solr."
argument_list|)
operator|>=
literal|0
condition|)
block|{
return|return
name|cname
return|;
block|}
block|}
return|return
name|SolrTestCaseJ4
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getSimpleClassName
specifier|protected
specifier|static
name|String
name|getSimpleClassName
parameter_list|()
block|{
name|String
name|cname
init|=
name|getClassName
argument_list|()
decl_stmt|;
return|return
name|cname
operator|.
name|substring
argument_list|(
name|cname
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|field|configString
specifier|protected
specifier|static
name|String
name|configString
decl_stmt|;
DECL|field|schemaString
specifier|protected
specifier|static
name|String
name|schemaString
decl_stmt|;
DECL|field|solrConfig
specifier|protected
specifier|static
name|SolrConfig
name|solrConfig
decl_stmt|;
comment|/**    * Harness initialized by initTestHarness.    *    *<p>    * For use in test methods as needed.    *</p>    */
DECL|field|h
specifier|protected
specifier|static
name|TestHarness
name|h
decl_stmt|;
comment|/**    * LocalRequestFactory initialized by initTestHarness using sensible    * defaults.    *    *<p>    * For use in test methods as needed.    *</p>    */
DECL|field|lrf
specifier|protected
specifier|static
name|TestHarness
operator|.
name|LocalRequestFactory
name|lrf
decl_stmt|;
comment|/**    * Subclasses must define this method to return the name of the    * schema.xml they wish to use.    */
DECL|method|getSchemaFile
specifier|public
specifier|static
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
name|schemaString
return|;
block|}
empty_stmt|;
comment|/**    * Subclasses must define this method to return the name of the    * solrconfig.xml they wish to use.    */
DECL|method|getSolrConfigFile
specifier|public
specifier|static
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
name|configString
return|;
block|}
empty_stmt|;
comment|/**    * The directory used to story the index managed by the TestHarness h    */
DECL|field|dataDir
specifier|protected
specifier|static
name|File
name|dataDir
decl_stmt|;
comment|/**    * Initializes things your test might need    *    *<ul>    *<li>Creates a dataDir in the "java.io.tmpdir"</li>    *<li>initializes the TestHarness h using this data directory, and getSchemaPath()</li>    *<li>initializes the LocalRequestFactory lrf using sensible defaults.</li>    *</ul>    *    */
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrTestCaseJ4
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|factoryProp
specifier|private
specifier|static
name|String
name|factoryProp
decl_stmt|;
DECL|method|createTempDir
specifier|public
specifier|static
name|void
name|createTempDir
parameter_list|()
block|{
name|String
name|cname
init|=
name|getSimpleClassName
argument_list|()
decl_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"solrtest-"
operator|+
name|cname
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
DECL|method|initCore
specifier|public
specifier|static
name|void
name|initCore
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"####initCore"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"ignore_exception"
argument_list|)
expr_stmt|;
name|factoryProp
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
if|if
condition|(
name|factoryProp
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.RAMDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
name|createTempDir
argument_list|()
expr_stmt|;
comment|// other  methods like starting a jetty instance need these too
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|String
name|configFile
init|=
name|getSolrConfigFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|configFile
operator|!=
literal|null
condition|)
block|{
name|solrConfig
operator|=
name|h
operator|.
name|createConfig
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|=
operator|new
name|TestHarness
argument_list|(
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|solrConfig
argument_list|,
name|getSchemaFile
argument_list|()
argument_list|)
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|"version"
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"####initCore end"
argument_list|)
expr_stmt|;
block|}
comment|/** Subclasses that override setUp can optionally call this method    * to log the fact that their setUp process has ended.    */
DECL|method|postSetUp
specifier|public
name|void
name|postSetUp
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"####POSTSETUP "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Subclasses that override tearDown can optionally call this method    * to log the fact that the tearDown process has started.  This is necessary    * since subclasses will want to call super.tearDown() at the *end* of their    * tearDown method.    */
DECL|method|preTearDown
specifier|public
name|void
name|preTearDown
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"####PRETEARDOWN "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shuts down the test harness, and makes the best attempt possible    * to delete dataDir, unless the system property "solr.test.leavedatadir"    * is set.    */
DECL|method|deleteCore
specifier|public
specifier|static
name|void
name|deleteCore
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"###deleteCore"
argument_list|)
expr_stmt|;
if|if
condition|(
name|h
operator|!=
literal|null
condition|)
block|{
name|h
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|String
name|skip
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|skip
operator|&&
literal|0
operator|!=
name|skip
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: per solr.test.leavedatadir, dataDir will not be removed: "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|recurseDelete
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"!!!! WARNING: best effort to remove "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" FAILED !!!!!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|factoryProp
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
block|}
name|dataDir
operator|=
literal|null
expr_stmt|;
name|solrConfig
operator|=
literal|null
expr_stmt|;
name|h
operator|=
literal|null
expr_stmt|;
name|lrf
operator|=
literal|null
expr_stmt|;
name|configString
operator|=
name|schemaString
operator|=
literal|null
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
name|res
init|=
name|h
operator|.
name|validateUpdate
argument_list|(
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
name|fail
argument_list|(
name|m
operator|+
literal|"update was not successful: "
operator|+
name|res
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|res
init|=
name|h
operator|.
name|validateErrorUpdate
argument_list|(
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
name|fail
argument_list|(
name|m
operator|+
literal|"update succeeded, but should have failed: "
operator|+
name|res
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
comment|/** Validates a query matches some XPath test expressions and closes the query */
DECL|method|assertQ
specifier|public
specifier|static
name|void
name|assertQ
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
block|{
name|assertQ
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
comment|/** Validates a query matches some XPath test expressions and closes the query */
DECL|method|assertQ
specifier|public
specifier|static
name|void
name|assertQ
parameter_list|(
name|String
name|message
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
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
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
literal|"facet"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// add a test to ensure that faceting did not throw an exception
comment|// internally, where it would be added to facet_counts/exception
name|String
index|[]
name|allTests
init|=
operator|new
name|String
index|[
name|tests
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|tests
argument_list|,
literal|0
argument_list|,
name|allTests
argument_list|,
literal|1
argument_list|,
name|tests
operator|.
name|length
argument_list|)
expr_stmt|;
name|allTests
index|[
literal|0
index|]
operator|=
literal|"*[count(//lst[@name='facet_counts']/*[@name='exception'])=0]"
expr_stmt|;
name|tests
operator|=
name|allTests
expr_stmt|;
block|}
name|String
name|results
init|=
name|h
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
name|fail
argument_list|(
name|m
operator|+
literal|"query failed XPath: "
operator|+
name|results
operator|+
literal|"\n xml response was: "
operator|+
name|response
operator|+
literal|"\n request was: "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
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
comment|/** Validates a query matches some JSON test expressions and closes the query.    * The text expression is of the form path:JSON.  To facilitate easy embedding    * in Java strings, the JSON can have double quotes replaced with single quotes.    *    * Please use this with care: this makes it easy to match complete structures, but doing so    * can result in fragile tests if you are matching more than what you want to test.    *    **/
DECL|method|assertJQ
specifier|public
specifier|static
name|void
name|assertJQ
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
literal|null
decl_stmt|;
try|try
block|{
name|params
operator|=
name|req
operator|.
name|getParams
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
literal|"json"
operator|.
name|equals
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
argument_list|)
operator|||
name|params
operator|.
name|get
argument_list|(
literal|"indent"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|ModifiableSolrParams
name|newParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|newParams
operator|.
name|set
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
literal|"indent"
argument_list|)
operator|==
literal|null
condition|)
name|newParams
operator|.
name|set
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|newParams
argument_list|)
expr_stmt|;
block|}
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
name|h
operator|.
name|query
argument_list|(
name|req
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
name|req
operator|.
name|getParamString
argument_list|()
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
name|String
name|testJSON
init|=
name|test
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
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
name|req
operator|.
name|getParamString
argument_list|()
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
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
comment|// restore the params
if|if
condition|(
name|params
operator|!=
literal|null
operator|&&
name|params
operator|!=
name|req
operator|.
name|getParams
argument_list|()
condition|)
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Makes sure a query throws a SolrException with the listed response code */
DECL|method|assertQEx
specifier|public
specifier|static
name|void
name|assertQEx
parameter_list|(
name|String
name|message
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|int
name|code
parameter_list|)
block|{
try|try
block|{
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|sex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|code
argument_list|,
name|sex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
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
DECL|method|assertQEx
specifier|public
specifier|static
name|void
name|assertQEx
parameter_list|(
name|String
name|message
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrException
operator|.
name|ErrorCode
name|code
parameter_list|)
block|{
try|try
block|{
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|code
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
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
comment|/**    * @see TestHarness#optimize    */
DECL|method|optimize
specifier|public
specifier|static
name|String
name|optimize
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|h
operator|.
name|optimize
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * @see TestHarness#commit    */
DECL|method|commit
specifier|public
specifier|static
name|String
name|commit
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|h
operator|.
name|commit
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Generates a simple&lt;add&gt;&lt;doc&gt;... XML String with no options    *    * @param fieldsAndValues 0th and Even numbered args are fields names odds are field values.    * @see #add    * @see #doc    */
DECL|method|adoc
specifier|public
specifier|static
name|String
name|adoc
parameter_list|(
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
block|{
name|Doc
name|d
init|=
name|doc
argument_list|(
name|fieldsAndValues
argument_list|)
decl_stmt|;
return|return
name|add
argument_list|(
name|d
argument_list|)
return|;
block|}
comment|/**    * Generates a simple&lt;add&gt;&lt;doc&gt;... XML String with no options    */
DECL|method|adoc
specifier|public
specifier|static
name|String
name|adoc
parameter_list|(
name|SolrInputDocument
name|sdoc
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrInputField
name|sf
range|:
name|sdoc
control|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|sf
operator|.
name|getValues
argument_list|()
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|adoc
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Generates an&lt;add&gt;&lt;doc&gt;... XML String with options    * on the add.    *    * @param doc the Document to add    * @param args 0th and Even numbered args are param names, Odds are param values.    * @see #add    * @see #doc    */
DECL|method|add
specifier|public
specifier|static
name|String
name|add
parameter_list|(
name|Doc
name|doc
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
try|try
block|{
name|StringWriter
name|r
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
comment|// this is anoying
if|if
condition|(
literal|null
operator|==
name|args
operator|||
literal|0
operator|==
name|args
operator|.
name|length
condition|)
block|{
name|r
operator|.
name|write
argument_list|(
literal|"<add>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|write
argument_list|(
name|doc
operator|.
name|xml
argument_list|)
expr_stmt|;
name|r
operator|.
name|write
argument_list|(
literal|"</add>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XML
operator|.
name|writeUnescapedXML
argument_list|(
name|r
argument_list|,
literal|"add"
argument_list|,
name|doc
operator|.
name|xml
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|args
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|getBuffer
argument_list|()
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
literal|"this should never happen with a StringWriter"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Generates a&lt;delete&gt;... XML string for an ID    *    * @see TestHarness#deleteById    */
DECL|method|delI
specifier|public
specifier|static
name|String
name|delI
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|h
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Generates a&lt;delete&gt;... XML string for an query    *    * @see TestHarness#deleteByQuery    */
DECL|method|delQ
specifier|public
specifier|static
name|String
name|delQ
parameter_list|(
name|String
name|q
parameter_list|)
block|{
return|return
name|h
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|)
return|;
block|}
comment|/**    * Generates a simple&lt;doc&gt;... XML String with no options    *    * @param fieldsAndValues 0th and Even numbered args are fields names, Odds are field values.    * @see TestHarness#makeSimpleDoc    */
DECL|method|doc
specifier|public
specifier|static
name|Doc
name|doc
parameter_list|(
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
block|{
name|Doc
name|d
init|=
operator|new
name|Doc
argument_list|()
decl_stmt|;
name|d
operator|.
name|xml
operator|=
name|h
operator|.
name|makeSimpleDoc
argument_list|(
name|fieldsAndValues
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|d
return|;
block|}
comment|/**    * Generates a SolrQueryRequest using the LocalRequestFactory    * @see #lrf    */
DECL|method|req
specifier|public
specifier|static
name|SolrQueryRequest
name|req
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
block|{
return|return
name|lrf
operator|.
name|makeRequest
argument_list|(
name|q
argument_list|)
return|;
block|}
comment|/**    * Generates a SolrQueryRequest using the LocalRequestFactory    * @see #lrf    */
DECL|method|req
specifier|public
specifier|static
name|SolrQueryRequest
name|req
parameter_list|(
name|String
index|[]
name|params
parameter_list|,
name|String
modifier|...
name|moreParams
parameter_list|)
block|{
name|String
index|[]
name|allParams
init|=
name|moreParams
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|int
name|len
init|=
name|params
operator|.
name|length
operator|+
name|moreParams
operator|.
name|length
decl_stmt|;
name|allParams
operator|=
operator|new
name|String
index|[
name|len
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|params
argument_list|,
literal|0
argument_list|,
name|allParams
argument_list|,
literal|0
argument_list|,
name|params
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|moreParams
argument_list|,
literal|0
argument_list|,
name|allParams
argument_list|,
name|params
operator|.
name|length
argument_list|,
name|moreParams
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|lrf
operator|.
name|makeRequest
argument_list|(
name|allParams
argument_list|)
return|;
block|}
comment|/** Neccessary to make method signatures un-ambiguous */
DECL|class|Doc
specifier|public
specifier|static
class|class
name|Doc
block|{
DECL|field|xml
specifier|public
name|String
name|xml
decl_stmt|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|xml
return|;
block|}
block|}
DECL|method|recurseDelete
specifier|public
specifier|static
name|boolean
name|recurseDelete
parameter_list|(
name|File
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|sub
range|:
name|f
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|recurseDelete
argument_list|(
name|sub
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"!!!! WARNING: best effort to remove "
operator|+
name|sub
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" FAILED !!!!!"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
name|f
operator|.
name|delete
argument_list|()
return|;
block|}
DECL|method|clearIndex
specifier|public
name|void
name|clearIndex
parameter_list|()
block|{
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

