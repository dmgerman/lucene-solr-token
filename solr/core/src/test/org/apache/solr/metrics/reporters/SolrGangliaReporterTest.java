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
name|info
operator|.
name|ganglia
operator|.
name|gmetric4j
operator|.
name|gmetric
operator|.
name|GMetric
import|;
end_import

begin_import
import|import
name|info
operator|.
name|ganglia
operator|.
name|gmetric4j
operator|.
name|gmetric
operator|.
name|GMetricSlope
import|;
end_import

begin_import
import|import
name|info
operator|.
name|ganglia
operator|.
name|gmetric4j
operator|.
name|gmetric
operator|.
name|GMetricType
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SolrGangliaReporterTest
specifier|public
class|class
name|SolrGangliaReporterTest
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
name|GMetric
name|ganglia
init|=
name|mock
argument_list|(
name|GMetric
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|doAnswer
argument_list|(
name|invocation
lambda|->
block|{
specifier|final
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|ganglia
argument_list|)
operator|.
name|announce
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|GMetricType
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|GMetricSlope
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyString
argument_list|()
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
literal|"solr-gangliareporter.xml"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
literal|"test"
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
name|SolrGangliaReporter
argument_list|)
expr_stmt|;
name|SolrGangliaReporter
name|gangliaReporter
init|=
operator|(
name|SolrGangliaReporter
operator|)
name|reporter
decl_stmt|;
name|gangliaReporter
operator|.
name|setGMetric
argument_list|(
name|ganglia
argument_list|)
expr_stmt|;
name|gangliaReporter
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|size
argument_list|()
operator|>=
literal|3
argument_list|)
expr_stmt|;
name|String
index|[]
name|frozenNames
init|=
operator|(
name|String
index|[]
operator|)
name|names
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|names
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|frozenNames
control|)
block|{
name|assertTrue
argument_list|(
name|name
argument_list|,
name|name
operator|.
name|startsWith
argument_list|(
literal|"test.solr.node.cores."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

