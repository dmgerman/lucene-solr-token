begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics.reporters
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|reporters
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
name|Map
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
name|SolrDocumentList
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
name|logging
operator|.
name|LogWatcher
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
name|logging
operator|.
name|LogWatcherConfig
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
name|SolrMetricManager
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
name|SolrMetricReporter
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
name|Test
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SolrSlf4jReporterTest
specifier|public
class|class
name|SolrSlf4jReporterTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testReporter
specifier|public
name|void
name|testReporter
parameter_list|()
throws|throws
name|Exception
block|{
name|LogWatcherConfig
name|watcherCfg
init|=
operator|new
name|LogWatcherConfig
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|LogWatcher
name|watcher
init|=
name|LogWatcher
operator|.
name|newRegisteredLogWatcher
argument_list|(
name|watcherCfg
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|watcher
operator|.
name|setThreshold
argument_list|(
literal|"INFO"
argument_list|)
expr_stmt|;
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
literal|"solr-slf4jreporter.xml"
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
name|CoreContainer
name|cc
init|=
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
decl_stmt|;
name|h
operator|.
name|coreName
operator|=
name|DEFAULT_TEST_CORENAME
expr_stmt|;
name|SolrMetricManager
name|metricManager
init|=
name|cc
operator|.
name|getMetricManager
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
literal|"solr.node"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reporters
operator|.
name|toString
argument_list|()
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|instanceof
name|SolrSlf4jReporter
argument_list|)
expr_stmt|;
name|reporter
operator|=
name|reporters
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|instanceof
name|SolrSlf4jReporter
argument_list|)
expr_stmt|;
name|watcher
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|history
init|=
name|watcher
operator|.
name|getHistory
argument_list|(
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// dot-separated names are treated like class names and collapsed
comment|// in regular log output, but here we get the full name
name|assertTrue
argument_list|(
name|history
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|d
lambda|->
literal|"solr.node"
operator|.
name|equals
argument_list|(
name|d
operator|.
name|getFirstValue
argument_list|(
literal|"logger"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|count
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|history
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|d
lambda|->
literal|"foobar"
operator|.
name|equals
argument_list|(
name|d
operator|.
name|getFirstValue
argument_list|(
literal|"logger"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|count
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

