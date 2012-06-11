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
name|io
operator|.
name|Reader
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for XPathEntityProcessor  *</p>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestXPathEntityProcessor
specifier|public
class|class
name|TestXPathEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|simulateSlowReader
name|boolean
name|simulateSlowReader
decl_stmt|;
DECL|field|simulateSlowResultProcessor
name|boolean
name|simulateSlowResultProcessor
decl_stmt|;
DECL|field|rowsToRead
name|int
name|rowsToRead
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Test
DECL|method|withFieldsAndXpath
specifier|public
name|void
name|withFieldsAndXpath
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
literal|"x.xsl"
argument_list|,
name|xsl
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Map
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|,
literal|"url"
argument_list|,
literal|"cd.xml"
argument_list|,
name|XPathEntityProcessor
operator|.
name|FOR_EACH
argument_list|,
literal|"/catalog/cd"
argument_list|)
decl_stmt|;
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"title"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/catalog/cd/title"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"artist"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/catalog/cd/artist"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"year"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/catalog/cd/year"
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
name|getDataSource
argument_list|(
name|cdData
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|XPathEntityProcessor
name|xPathEntityProcessor
init|=
operator|new
name|XPathEntityProcessor
argument_list|()
decl_stmt|;
name|xPathEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|xPathEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
condition|)
break|break;
name|result
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Empire Burlesque"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bonnie Tyler"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"artist"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1982"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"year"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiValued
specifier|public
name|void
name|testMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|,
literal|"url"
argument_list|,
literal|"testdata.xml"
argument_list|,
name|XPathEntityProcessor
operator|.
name|FOR_EACH
argument_list|,
literal|"/root"
argument_list|)
decl_stmt|;
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"a"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/root/a"
argument_list|,
name|DataImporter
operator|.
name|MULTI_VALUED
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
name|getDataSource
argument_list|(
name|testXml
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|XPathEntityProcessor
name|xPathEntityProcessor
init|=
operator|new
name|XPathEntityProcessor
argument_list|()
decl_stmt|;
name|xPathEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|xPathEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
condition|)
break|break;
name|result
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|List
operator|)
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiValuedFlatten
specifier|public
name|void
name|testMultiValuedFlatten
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|,
literal|"url"
argument_list|,
literal|"testdata.xml"
argument_list|,
name|XPathEntityProcessor
operator|.
name|FOR_EACH
argument_list|,
literal|"/root"
argument_list|)
decl_stmt|;
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"a"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/root/a"
argument_list|,
literal|"flatten"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
name|getDataSource
argument_list|(
name|testXmlFlatten
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|XPathEntityProcessor
name|xPathEntityProcessor
init|=
operator|new
name|XPathEntityProcessor
argument_list|()
decl_stmt|;
name|xPathEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|xPathEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
condition|)
break|break;
name|result
operator|=
name|row
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"1B2"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withFieldsAndXpathStream
specifier|public
name|void
name|withFieldsAndXpathStream
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Object
name|monitor
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|final
name|boolean
index|[]
name|done
init|=
operator|new
name|boolean
index|[
literal|1
index|]
decl_stmt|;
name|Map
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|,
literal|"url"
argument_list|,
literal|"cd.xml"
argument_list|,
name|XPathEntityProcessor
operator|.
name|FOR_EACH
argument_list|,
literal|"/catalog/cd"
argument_list|,
literal|"stream"
argument_list|,
literal|"true"
argument_list|,
literal|"batchSize"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"title"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/catalog/cd/title"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"artist"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/catalog/cd/artist"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"year"
argument_list|,
literal|"xpath"
argument_list|,
literal|"/catalog/cd/year"
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
name|getDataSource
argument_list|(
name|cdData
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|XPathEntityProcessor
name|xPathEntityProcessor
init|=
operator|new
name|XPathEntityProcessor
argument_list|()
block|{
specifier|private
name|int
name|count
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|record
parameter_list|,
name|String
name|xpath
parameter_list|)
block|{
synchronized|synchronized
init|(
name|monitor
init|)
block|{
if|if
condition|(
name|simulateSlowReader
operator|&&
operator|!
name|done
index|[
literal|0
index|]
condition|)
block|{
try|try
block|{
name|monitor
operator|.
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
return|return
name|super
operator|.
name|readRow
argument_list|(
name|record
argument_list|,
name|xpath
argument_list|)
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|simulateSlowResultProcessor
condition|)
block|{
name|xPathEntityProcessor
operator|.
name|blockingQueueSize
operator|=
literal|1
expr_stmt|;
block|}
name|xPathEntityProcessor
operator|.
name|blockingQueueTimeOut
operator|=
literal|1
expr_stmt|;
name|xPathEntityProcessor
operator|.
name|blockingQueueTimeOutUnits
operator|=
name|TimeUnit
operator|.
name|MICROSECONDS
expr_stmt|;
name|xPathEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|rowsToRead
operator|>=
literal|0
operator|&&
name|result
operator|.
name|size
argument_list|()
operator|>=
name|rowsToRead
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|xPathEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
condition|)
break|break;
name|result
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
if|if
condition|(
name|simulateSlowResultProcessor
condition|)
block|{
synchronized|synchronized
init|(
name|xPathEntityProcessor
operator|.
name|publisherThread
init|)
block|{
if|if
condition|(
name|xPathEntityProcessor
operator|.
name|publisherThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|xPathEntityProcessor
operator|.
name|publisherThread
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
synchronized|synchronized
init|(
name|monitor
init|)
block|{
name|done
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
name|monitor
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
comment|// confirm that publisher thread stops.
name|xPathEntityProcessor
operator|.
name|publisherThread
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected thread to stop"
argument_list|,
literal|false
argument_list|,
name|xPathEntityProcessor
operator|.
name|publisherThread
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rowsToRead
operator|<
literal|0
condition|?
literal|3
else|:
name|rowsToRead
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rowsToRead
operator|<
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Empire Burlesque"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bonnie Tyler"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"artist"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1982"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"year"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|withFieldsAndXpathStreamContinuesOnTimeout
specifier|public
name|void
name|withFieldsAndXpathStreamContinuesOnTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|simulateSlowReader
operator|=
literal|true
expr_stmt|;
name|withFieldsAndXpathStream
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|streamWritesMessageAfterBlockedAttempt
specifier|public
name|void
name|streamWritesMessageAfterBlockedAttempt
parameter_list|()
throws|throws
name|Exception
block|{
name|simulateSlowResultProcessor
operator|=
literal|true
expr_stmt|;
name|withFieldsAndXpathStream
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|streamStopsAfterInterrupt
specifier|public
name|void
name|streamStopsAfterInterrupt
parameter_list|()
throws|throws
name|Exception
block|{
name|simulateSlowResultProcessor
operator|=
literal|true
expr_stmt|;
name|rowsToRead
operator|=
literal|1
expr_stmt|;
name|withFieldsAndXpathStream
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withDefaultSolrAndXsl
specifier|public
name|void
name|withDefaultSolrAndXsl
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
name|AbstractDataImportHandlerTestCase
operator|.
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"x.xsl"
argument_list|,
name|xsl
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Map
name|entityAttrs
init|=
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|,
name|XPathEntityProcessor
operator|.
name|USE_SOLR_ADD_SCHEMA
argument_list|,
literal|"true"
argument_list|,
literal|"xsl"
argument_list|,
literal|""
operator|+
operator|new
name|File
argument_list|(
name|tmpdir
argument_list|,
literal|"x.xsl"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|,
literal|"url"
argument_list|,
literal|"cd.xml"
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
name|getDataSource
argument_list|(
name|cdData
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
literal|null
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
name|XPathEntityProcessor
name|xPathEntityProcessor
init|=
operator|new
name|XPathEntityProcessor
argument_list|()
decl_stmt|;
name|xPathEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|xPathEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
condition|)
break|break;
name|result
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Empire Burlesque"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bonnie Tyler"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"artist"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1982"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
literal|"year"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getDataSource
specifier|private
name|DataSource
argument_list|<
name|Reader
argument_list|>
name|getDataSource
parameter_list|(
specifier|final
name|String
name|xml
parameter_list|)
block|{
return|return
operator|new
name|DataSource
argument_list|<
name|Reader
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|Reader
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|field|xsl
specifier|private
specifier|static
specifier|final
name|String
name|xsl
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<xsl:stylesheet version=\"1.0\"\n"
operator|+
literal|"xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
operator|+
literal|"<xsl:output version='1.0' method='xml' encoding='UTF-8' indent='yes'/>\n"
operator|+
literal|"\n"
operator|+
literal|"<xsl:template match=\"/\">\n"
operator|+
literal|"<add> \n"
operator|+
literal|"<xsl:for-each select=\"catalog/cd\">\n"
operator|+
literal|"<doc>\n"
operator|+
literal|"<field name=\"title\"><xsl:value-of select=\"title\"/></field>\n"
operator|+
literal|"<field name=\"artist\"><xsl:value-of select=\"artist\"/></field>\n"
operator|+
literal|"<field name=\"country\"><xsl:value-of select=\"country\"/></field>\n"
operator|+
literal|"<field name=\"company\"><xsl:value-of select=\"company\"/></field>      \n"
operator|+
literal|"<field name=\"price\"><xsl:value-of select=\"price\"/></field>\n"
operator|+
literal|"<field name=\"year\"><xsl:value-of select=\"year\"/></field>      \n"
operator|+
literal|"</doc>\n"
operator|+
literal|"</xsl:for-each>\n"
operator|+
literal|"</add>  \n"
operator|+
literal|"</xsl:template>\n"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
DECL|field|cdData
specifier|private
specifier|static
specifier|final
name|String
name|cdData
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<?xml-stylesheet type=\"text/xsl\" href=\"solr.xsl\"?>\n"
operator|+
literal|"<catalog>\n"
operator|+
literal|"\t<cd>\n"
operator|+
literal|"\t\t<title>Empire Burlesque</title>\n"
operator|+
literal|"\t\t<artist>Bob Dylan</artist>\n"
operator|+
literal|"\t\t<country>USA</country>\n"
operator|+
literal|"\t\t<company>Columbia</company>\n"
operator|+
literal|"\t\t<price>10.90</price>\n"
operator|+
literal|"\t\t<year>1985</year>\n"
operator|+
literal|"\t</cd>\n"
operator|+
literal|"\t<cd>\n"
operator|+
literal|"\t\t<title>Hide your heart</title>\n"
operator|+
literal|"\t\t<artist>Bonnie Tyler</artist>\n"
operator|+
literal|"\t\t<country>UK</country>\n"
operator|+
literal|"\t\t<company>CBS Records</company>\n"
operator|+
literal|"\t\t<price>9.90</price>\n"
operator|+
literal|"\t\t<year>1988</year>\n"
operator|+
literal|"\t</cd>\n"
operator|+
literal|"\t<cd>\n"
operator|+
literal|"\t\t<title>Greatest Hits</title>\n"
operator|+
literal|"\t\t<artist>Dolly Parton</artist>\n"
operator|+
literal|"\t\t<country>USA</country>\n"
operator|+
literal|"\t\t<company>RCA</company>\n"
operator|+
literal|"\t\t<price>9.90</price>\n"
operator|+
literal|"\t\t<year>1982</year>\n"
operator|+
literal|"\t</cd>\n"
operator|+
literal|"</catalog>\t"
decl_stmt|;
DECL|field|testXml
specifier|private
specifier|static
specifier|final
name|String
name|testXml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><a>1</a><a>2</a></root>"
decl_stmt|;
DECL|field|testXmlFlatten
specifier|private
specifier|static
specifier|final
name|String
name|testXmlFlatten
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><a>1<b>B</b>2</a></root>"
decl_stmt|;
block|}
end_class

end_unit

