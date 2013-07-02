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
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
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
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
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
DECL|field|oldSolrHome
specifier|private
specifier|static
name|String
name|oldSolrHome
decl_stmt|;
DECL|field|SOLR_HOME_PROP
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME_PROP
init|=
literal|"solr.solr.home"
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
name|oldSolrHome
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
if|if
condition|(
name|oldSolrHome
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|,
name|oldSolrHome
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|SOLR_HOME_PROP
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|solrHomeDirectory
specifier|private
name|File
name|solrHomeDirectory
decl_stmt|;
DECL|method|init
specifier|private
name|CoreContainer
name|init
parameter_list|(
name|String
name|dirName
parameter_list|)
throws|throws
name|Exception
block|{
name|solrHomeDirectory
operator|=
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
name|dirName
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using solrconfig from "
operator|+
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|CoreContainer
name|ret
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
name|ret
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Test
DECL|method|testShareSchema
specifier|public
name|void
name|testShareSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"shareSchema"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|init
argument_list|(
literal|"_shareSchema"
argument_list|)
decl_stmt|;
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
name|getLatestSchema
argument_list|()
argument_list|,
name|core2
operator|.
name|getLatestSchema
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
DECL|method|testReloadSequential
specifier|public
name|void
name|testReloadSequential
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|(
literal|"_reloadSequential"
argument_list|)
decl_stmt|;
try|try
block|{
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReloadThreaded
specifier|public
name|void
name|testReloadThreaded
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|(
literal|"_reloadThreaded"
argument_list|)
decl_stmt|;
class|class
name|TestThread
extends|extends
name|Thread
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|cc
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numThreads
init|=
literal|4
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|TestThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
name|getRawInstanceDir
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
name|cores
operator|.
name|getSolrHome
argument_list|()
argument_list|,
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
literal|"/solr/cores[@defaultCoreName='collection1' and not(@transientCacheSize)]"
argument_list|,
literal|"/solr/cores/core[@name='collection1' and @instanceDir='"
operator|+
name|instDir
operator|+
literal|"' and @transient='false' and @loadOnStartup='true' ]"
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
comment|// Test for saving implicit properties, we should not do this.
name|assertXmlFile
argument_list|(
name|twoXml
argument_list|,
literal|"/solr/cores/core[@name='X' and not(@solr.core.instanceDir) and not (@solr.core.configName)]"
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
annotation|@
name|Test
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
name|SetUpHome
argument_list|(
name|solrHomeDirectory
argument_list|,
name|EMPTY_SOLR_XML
argument_list|)
expr_stmt|;
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
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
try|try
block|{
comment|//assert zero cores
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
name|assertXmlFile
argument_list|(
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
literal|"/solr/cores[@transientCacheSize='32']"
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
DECL|method|SetUpHome
specifier|private
name|void
name|SetUpHome
parameter_list|(
name|File
name|solrHomeDirectory
parameter_list|,
name|String
name|xmlFile
parameter_list|)
throws|throws
name|IOException
block|{
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
name|xmlFile
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
name|SOLR_HOME_PROP
argument_list|,
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClassLoaderHierarchy
specifier|public
name|void
name|testClassLoaderHierarchy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cc
init|=
name|init
argument_list|(
literal|"_classLoaderHierarchy"
argument_list|)
decl_stmt|;
try|try
block|{
name|cc
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ClassLoader
name|sharedLoader
init|=
name|cc
operator|.
name|loader
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|ClassLoader
name|contextLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|contextLoader
argument_list|,
name|sharedLoader
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|CoreDescriptor
name|descriptor1
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
literal|"core1"
argument_list|,
literal|"./collection1"
argument_list|)
decl_stmt|;
name|SolrCore
name|core1
init|=
name|cc
operator|.
name|create
argument_list|(
name|descriptor1
argument_list|)
decl_stmt|;
name|ClassLoader
name|coreLoader
init|=
name|core1
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|sharedLoader
argument_list|,
name|coreLoader
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|core1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSharedLib
specifier|public
name|void
name|testSharedLib
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmpRoot
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"testSharedLib"
argument_list|)
decl_stmt|;
name|File
name|lib
init|=
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"lib"
argument_list|)
decl_stmt|;
name|lib
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar1
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|lib
argument_list|,
literal|"jar1.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar1
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"defaultSharedLibFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar1
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar1
operator|.
name|close
argument_list|()
expr_stmt|;
name|File
name|customLib
init|=
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"customLib"
argument_list|)
decl_stmt|;
name|customLib
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|JarOutputStream
name|jar2
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|customLib
argument_list|,
literal|"jar2.jar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|jar2
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"customSharedLibFile"
argument_list|)
argument_list|)
expr_stmt|;
name|jar2
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jar2
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"default-lib-solr.xml"
argument_list|)
argument_list|,
literal|"<solr><cores/></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"explicit-lib-solr.xml"
argument_list|)
argument_list|,
literal|"<solr sharedLib=\"lib\"><cores/></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"custom-lib-solr.xml"
argument_list|)
argument_list|,
literal|"<solr sharedLib=\"customLib\"><cores/></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cc1
init|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"default-lib-solr.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cc1
operator|.
name|loader
operator|.
name|openResource
argument_list|(
literal|"defaultSharedLibFile"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc1
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|final
name|CoreContainer
name|cc2
init|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"explicit-lib-solr.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cc2
operator|.
name|loader
operator|.
name|openResource
argument_list|(
literal|"defaultSharedLibFile"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
specifier|final
name|CoreContainer
name|cc3
init|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|tmpRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|tmpRoot
argument_list|,
literal|"custom-lib-solr.xml"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cc3
operator|.
name|loader
operator|.
name|openResource
argument_list|(
literal|"customSharedLibFile"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cc3
operator|.
name|shutdown
argument_list|()
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
literal|"<cores adminPath=\"/admin/cores\" transientCacheSize=\"32\">\n"
operator|+
literal|"</cores>\n"
operator|+
literal|"</solr>"
decl_stmt|;
block|}
end_class

end_unit

