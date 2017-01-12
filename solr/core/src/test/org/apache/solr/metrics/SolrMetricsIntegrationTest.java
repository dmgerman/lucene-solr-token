begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
name|TestUtil
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
name|core
operator|.
name|CoreContainer
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
name|NodeConfig
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
name|PluginInfo
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
name|SolrInfoMBean
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
name|SolrResourceLoader
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
name|SolrXmlConfig
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
name|metrics
operator|.
name|reporters
operator|.
name|MockMetricReporter
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|SolrMetricsIntegrationTest
specifier|public
class|class
name|SolrMetricsIntegrationTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|MAX_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ITERATIONS
init|=
literal|20
decl_stmt|;
DECL|field|CORE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CORE_NAME
init|=
literal|"metrics_integration"
decl_stmt|;
DECL|field|METRIC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|METRIC_NAME
init|=
literal|"requestTimes"
decl_stmt|;
DECL|field|HANDLER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|HANDLER_NAME
init|=
literal|"standard"
decl_stmt|;
DECL|field|REPORTER_NAMES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|REPORTER_NAMES
init|=
block|{
literal|"reporter1"
block|,
literal|"reporter2"
block|}
decl_stmt|;
DECL|field|UNIVERSAL
specifier|private
specifier|static
specifier|final
name|String
name|UNIVERSAL
init|=
literal|"universal"
decl_stmt|;
DECL|field|SPECIFIC
specifier|private
specifier|static
specifier|final
name|String
name|SPECIFIC
init|=
literal|"specific"
decl_stmt|;
DECL|field|MULTIGROUP
specifier|private
specifier|static
specifier|final
name|String
name|MULTIGROUP
init|=
literal|"multigroup"
decl_stmt|;
DECL|field|MULTIREGISTRY
specifier|private
specifier|static
specifier|final
name|String
name|MULTIREGISTRY
init|=
literal|"multiregistry"
decl_stmt|;
DECL|field|INITIAL_REPORTERS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|INITIAL_REPORTERS
init|=
block|{
name|REPORTER_NAMES
index|[
literal|0
index|]
block|,
name|REPORTER_NAMES
index|[
literal|1
index|]
block|,
name|UNIVERSAL
block|,
name|SPECIFIC
block|,
name|MULTIGROUP
block|,
name|MULTIREGISTRY
block|}
decl_stmt|;
DECL|field|RENAMED_REPORTERS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|RENAMED_REPORTERS
init|=
block|{
name|REPORTER_NAMES
index|[
literal|0
index|]
block|,
name|REPORTER_NAMES
index|[
literal|1
index|]
block|,
name|UNIVERSAL
block|,
name|MULTIGROUP
block|}
decl_stmt|;
DECL|field|HANDLER_CATEGORY
specifier|private
specifier|static
specifier|final
name|SolrInfoMBean
operator|.
name|Category
name|HANDLER_CATEGORY
init|=
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|QUERY
decl_stmt|;
DECL|field|cc
specifier|private
name|CoreContainer
name|cc
decl_stmt|;
DECL|field|metricManager
specifier|private
name|SolrMetricManager
name|metricManager
decl_stmt|;
annotation|@
name|Before
DECL|method|beforeTest
specifier|public
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|home
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
comment|// define these properties, they are used in solrconfig.xml
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
name|solrXml
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|home
operator|.
name|toString
argument_list|()
argument_list|,
literal|"solr-metricreporter.xml"
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|NodeConfig
name|cfg
init|=
name|SolrXmlConfig
operator|.
name|fromString
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|home
argument_list|)
argument_list|,
name|solrXml
argument_list|)
decl_stmt|;
name|cc
operator|=
name|createCoreContainer
argument_list|(
name|cfg
argument_list|,
operator|new
name|TestHarness
operator|.
name|TestCoresLocator
argument_list|(
name|DEFAULT_TEST_CORENAME
argument_list|,
name|initCoreDataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|h
operator|.
name|coreName
operator|=
name|DEFAULT_TEST_CORENAME
expr_stmt|;
name|metricManager
operator|=
name|cc
operator|.
name|getMetricManager
argument_list|()
expr_stmt|;
comment|// initially there are more reporters, because two of them are added via a matching collection name
name|Map
argument_list|<
name|String
argument_list|,
name|SolrMetricReporter
argument_list|>
name|reporters
init|=
name|metricManager
operator|.
name|getReporters
argument_list|(
literal|"solr.core."
operator|+
name|DEFAULT_TEST_CORENAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|INITIAL_REPORTERS
operator|.
name|length
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporters
operator|.
name|keySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|INITIAL_REPORTERS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test rename operation
name|cc
operator|.
name|rename
argument_list|(
name|DEFAULT_TEST_CORENAME
argument_list|,
name|CORE_NAME
argument_list|)
expr_stmt|;
name|h
operator|.
name|coreName
operator|=
name|CORE_NAME
expr_stmt|;
name|cfg
operator|=
name|cc
operator|.
name|getConfig
argument_list|()
expr_stmt|;
name|PluginInfo
index|[]
name|plugins
init|=
name|cfg
operator|.
name|getMetricReporterPlugins
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|plugins
operator|.
name|length
argument_list|)
expr_stmt|;
name|reporters
operator|=
name|metricManager
operator|.
name|getReporters
argument_list|(
literal|"solr.node"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter '"
operator|+
name|REPORTER_NAMES
index|[
literal|0
index|]
operator|+
literal|"' missing in solr.node"
argument_list|,
name|reporters
operator|.
name|containsKey
argument_list|(
name|REPORTER_NAMES
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter '"
operator|+
name|UNIVERSAL
operator|+
literal|"' missing in solr.node"
argument_list|,
name|reporters
operator|.
name|containsKey
argument_list|(
name|UNIVERSAL
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter '"
operator|+
name|MULTIGROUP
operator|+
literal|"' missing in solr.node"
argument_list|,
name|reporters
operator|.
name|containsKey
argument_list|(
name|MULTIGROUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter '"
operator|+
name|MULTIREGISTRY
operator|+
literal|"' missing in solr.node"
argument_list|,
name|reporters
operator|.
name|containsKey
argument_list|(
name|MULTIREGISTRY
argument_list|)
argument_list|)
expr_stmt|;
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
name|REPORTER_NAMES
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter "
operator|+
name|reporter
operator|+
literal|" is not an instance of "
operator|+
name|MockMetricReporter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|reporter
operator|instanceof
name|MockMetricReporter
argument_list|)
expr_stmt|;
name|reporter
operator|=
name|reporters
operator|.
name|get
argument_list|(
name|UNIVERSAL
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter "
operator|+
name|reporter
operator|+
literal|" is not an instance of "
operator|+
name|MockMetricReporter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|reporter
operator|instanceof
name|MockMetricReporter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|afterTest
specifier|public
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCoreMetricManager
name|coreMetricManager
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreMetricManager
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrMetricReporter
argument_list|>
name|reporters
init|=
name|metricManager
operator|.
name|getReporters
argument_list|(
name|coreMetricManager
operator|.
name|getRegistryName
argument_list|()
argument_list|)
decl_stmt|;
name|deleteCore
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|reporterName
range|:
name|RENAMED_REPORTERS
control|)
block|{
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
name|reporterName
argument_list|)
decl_stmt|;
name|MockMetricReporter
name|mockReporter
init|=
operator|(
name|MockMetricReporter
operator|)
name|reporter
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter "
operator|+
name|reporterName
operator|+
literal|" was not closed: "
operator|+
name|mockReporter
argument_list|,
name|mockReporter
operator|.
name|didClose
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testConfigureReporter
specifier|public
name|void
name|testConfigureReporter
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|String
name|metricName
init|=
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
name|METRIC_NAME
argument_list|,
name|HANDLER_CATEGORY
operator|.
name|toString
argument_list|()
argument_list|,
name|HANDLER_NAME
argument_list|)
decl_stmt|;
name|SolrCoreMetricManager
name|coreMetricManager
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreMetricManager
argument_list|()
decl_stmt|;
name|Timer
name|timer
init|=
operator|(
name|Timer
operator|)
name|metricManager
operator|.
name|timer
argument_list|(
name|coreMetricManager
operator|.
name|getRegistryName
argument_list|()
argument_list|,
name|metricName
argument_list|)
decl_stmt|;
name|long
name|initialCount
init|=
name|timer
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|int
name|iterations
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
name|MAX_ITERATIONS
argument_list|)
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|finalCount
init|=
name|timer
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"metric counter incorrect"
argument_list|,
name|iterations
argument_list|,
name|finalCount
operator|-
name|initialCount
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrMetricReporter
argument_list|>
name|reporters
init|=
name|metricManager
operator|.
name|getReporters
argument_list|(
name|coreMetricManager
operator|.
name|getRegistryName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|RENAMED_REPORTERS
operator|.
name|length
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// SPECIFIC and MULTIREGISTRY were skipped because they were
comment|// specific to collection1
for|for
control|(
name|String
name|reporterName
range|:
name|RENAMED_REPORTERS
control|)
block|{
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
name|reporterName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Reporter "
operator|+
name|reporterName
operator|+
literal|" was not found."
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|instanceof
name|MockMetricReporter
argument_list|)
expr_stmt|;
name|MockMetricReporter
name|mockReporter
init|=
operator|(
name|MockMetricReporter
operator|)
name|reporter
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter "
operator|+
name|reporterName
operator|+
literal|" was not initialized: "
operator|+
name|mockReporter
argument_list|,
name|mockReporter
operator|.
name|didInit
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reporter "
operator|+
name|reporterName
operator|+
literal|" was not validated: "
operator|+
name|mockReporter
argument_list|,
name|mockReporter
operator|.
name|didValidate
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Reporter "
operator|+
name|reporterName
operator|+
literal|" was incorrectly closed: "
operator|+
name|mockReporter
argument_list|,
name|mockReporter
operator|.
name|didClose
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

