begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileOutputStream
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
name|OutputStreamWriter
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
name|ParserConfigurationException
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|util
operator|.
name|IOUtils
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
DECL|class|TestCoreContainer
specifier|public
class|class
name|TestCoreContainer
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
DECL|method|testShareSchema
specifier|public
name|void
name|testShareSchema
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
specifier|final
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"_shareSchema"
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
name|File
name|fconf
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"shareSchema"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|cores
operator|.
name|load
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|fconf
argument_list|)
expr_stmt|;
try|try
block|{
name|cores
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cores
operator|.
name|isShareSchema
argument_list|()
argument_list|)
expr_stmt|;
name|CoreDescriptor
name|descriptor1
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|,
literal|"./collection1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core1
init|=
name|cores
operator|.
name|create
argument_list|(
name|descriptor1
argument_list|)
decl_stmt|;
name|CoreDescriptor
name|descriptor2
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"core2"
argument_list|,
literal|"./collection1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core2
init|=
name|cores
operator|.
name|create
argument_list|(
name|descriptor2
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|core1
operator|.
name|getSchema
argument_list|()
argument_list|,
name|core2
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
name|core1
operator|.
name|close
argument_list|()
expr_stmt|;
name|core2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"shareSchema"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPersist
specifier|public
name|void
name|testPersist
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"_persist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|workDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|workDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|cores
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// is this needed since we make explicit calls?
name|String
name|instDir
init|=
literal|null
decl_stmt|;
block|{
name|SolrCore
name|template
init|=
literal|null
decl_stmt|;
try|try
block|{
name|template
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|instDir
operator|=
name|template
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|template
condition|)
name|template
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|File
name|instDirFile
init|=
operator|new
name|File
argument_list|(
name|instDir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"instDir doesn't exist: "
operator|+
name|instDir
argument_list|,
name|instDirFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// sanity check the basic persistence of the default init
specifier|final
name|File
name|oneXml
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"1.solr.xml"
argument_list|)
decl_stmt|;
name|cores
operator|.
name|persistFile
argument_list|(
name|oneXml
argument_list|)
expr_stmt|;
name|assertXmlFile
argument_list|(
name|oneXml
argument_list|,
literal|"/solr[@persistent='true']"
argument_list|,
literal|"/solr/cores[@defaultCoreName='collection1']"
argument_list|,
literal|"/solr/cores/core[@name='collection1' and @instanceDir='"
operator|+
name|instDir
operator|+
literal|"']"
argument_list|,
literal|"1=count(/solr/cores/core)"
argument_list|)
expr_stmt|;
comment|// create some new cores and sanity check the persistence
specifier|final
name|File
name|dataXfile
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"dataX"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|dataX
init|=
name|dataXfile
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"dataXfile mkdirs failed: "
operator|+
name|dataX
argument_list|,
name|dataXfile
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|instYfile
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"instY"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|instDirFile
argument_list|,
name|instYfile
argument_list|)
expr_stmt|;
comment|// :HACK: dataDir leaves off trailing "/", but instanceDir uses it
specifier|final
name|String
name|instY
init|=
name|instYfile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/"
decl_stmt|;
specifier|final
name|CoreDescriptor
name|xd
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"X"
argument_list|,
name|instDir
argument_list|)
decl_stmt|;
name|xd
operator|.
name|setDataDir
argument_list|(
name|dataX
argument_list|)
expr_stmt|;
specifier|final
name|CoreDescriptor
name|yd
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"Y"
argument_list|,
name|instY
argument_list|)
decl_stmt|;
name|SolrCore
name|x
init|=
literal|null
decl_stmt|;
name|SolrCore
name|y
init|=
literal|null
decl_stmt|;
try|try
block|{
name|x
operator|=
name|cores
operator|.
name|create
argument_list|(
name|xd
argument_list|)
expr_stmt|;
name|y
operator|=
name|cores
operator|.
name|create
argument_list|(
name|yd
argument_list|)
expr_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|x
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|y
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cores not added?"
argument_list|,
literal|3
argument_list|,
name|cores
operator|.
name|getCoreNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|twoXml
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"2.solr.xml"
argument_list|)
decl_stmt|;
name|cores
operator|.
name|persistFile
argument_list|(
name|twoXml
argument_list|)
expr_stmt|;
name|assertXmlFile
argument_list|(
name|twoXml
argument_list|,
literal|"/solr[@persistent='true']"
argument_list|,
literal|"/solr/cores[@defaultCoreName='collection1']"
argument_list|,
literal|"/solr/cores/core[@name='collection1' and @instanceDir='"
operator|+
name|instDir
operator|+
literal|"']"
argument_list|,
literal|"/solr/cores/core[@name='X' and @instanceDir='"
operator|+
name|instDir
operator|+
literal|"' and @dataDir='"
operator|+
name|dataX
operator|+
literal|"']"
argument_list|,
literal|"/solr/cores/core[@name='Y' and @instanceDir='"
operator|+
name|instY
operator|+
literal|"']"
argument_list|,
literal|"3=count(/solr/cores/core)"
argument_list|)
expr_stmt|;
comment|// delete a core, check persistence again
name|assertNotNull
argument_list|(
literal|"removing X returned null"
argument_list|,
name|cores
operator|.
name|remove
argument_list|(
literal|"X"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|File
name|threeXml
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"3.solr.xml"
argument_list|)
decl_stmt|;
name|cores
operator|.
name|persistFile
argument_list|(
name|threeXml
argument_list|)
expr_stmt|;
name|assertXmlFile
argument_list|(
name|threeXml
argument_list|,
literal|"/solr[@persistent='true']"
argument_list|,
literal|"/solr/cores[@defaultCoreName='collection1']"
argument_list|,
literal|"/solr/cores/core[@name='collection1' and @instanceDir='"
operator|+
name|instDir
operator|+
literal|"']"
argument_list|,
literal|"/solr/cores/core[@name='Y' and @instanceDir='"
operator|+
name|instY
operator|+
literal|"']"
argument_list|,
literal|"2=count(/solr/cores/core)"
argument_list|)
expr_stmt|;
comment|// sanity check that persisting w/o changes has no changes
specifier|final
name|File
name|fourXml
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"4.solr.xml"
argument_list|)
decl_stmt|;
name|cores
operator|.
name|persistFile
argument_list|(
name|fourXml
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"3 and 4 should be identical files"
argument_list|,
name|FileUtils
operator|.
name|contentEquals
argument_list|(
name|threeXml
argument_list|,
name|fourXml
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// y is closed by the container, but
comment|// x has been removed from the container
if|if
condition|(
name|x
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|x
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|assertXmlFile
specifier|public
name|void
name|assertXmlFile
parameter_list|(
specifier|final
name|File
name|file
parameter_list|,
name|String
modifier|...
name|xpath
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
try|try
block|{
name|String
name|xml
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|file
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|xml
argument_list|,
name|xpath
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
literal|"File XPath failure: file="
operator|+
name|file
operator|.
name|getPath
argument_list|()
operator|+
literal|" xpath="
operator|+
name|results
operator|+
literal|"\n\nxml was: "
operator|+
name|xml
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"XPath is invalid"
argument_list|,
name|e2
argument_list|)
throw|;
block|}
block|}
DECL|method|testNoCores
specifier|public
name|void
name|testNoCores
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
block|{
comment|//create solrHome
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"_noCores"
argument_list|)
decl_stmt|;
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|solrXmlFile
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|solrXmlFile
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|EMPTY_SOLR_XML
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|//init
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|CoreContainer
operator|.
name|Initializer
name|init
init|=
operator|new
name|CoreContainer
operator|.
name|Initializer
argument_list|()
decl_stmt|;
name|CoreContainer
name|cores
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cores
operator|=
name|init
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"CoreContainer not created"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|//assert cero cores
name|assertEquals
argument_list|(
literal|"There should not be cores"
argument_list|,
literal|0
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"collection1"
argument_list|)
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
comment|//add a new core
name|CoreDescriptor
name|coreDescriptor
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|SolrCore
name|newCore
init|=
name|cores
operator|.
name|create
argument_list|(
name|coreDescriptor
argument_list|)
decl_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|newCore
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//assert one registered core
name|assertEquals
argument_list|(
literal|"There core registered"
argument_list|,
literal|1
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|newCore
operator|.
name|close
argument_list|()
expr_stmt|;
name|cores
operator|.
name|remove
argument_list|(
literal|"core1"
argument_list|)
expr_stmt|;
comment|//assert cero cores
name|assertEquals
argument_list|(
literal|"There should not be cores"
argument_list|,
literal|0
argument_list|,
name|cores
operator|.
name|getCores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|EMPTY_SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_SOLR_XML
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr persistent=\"false\">\n"
operator|+
literal|"<cores adminPath=\"/admin/cores\">\n"
operator|+
literal|"</cores>\n"
operator|+
literal|"</solr>"
decl_stmt|;
block|}
end_class

end_unit

