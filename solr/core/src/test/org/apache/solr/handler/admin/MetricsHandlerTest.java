begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|CommonParams
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
name|NamedList
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
name|SimpleOrderedMap
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
name|response
operator|.
name|SolrQueryResponse
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

begin_comment
comment|/**  * Test for {@link MetricsHandler}  */
end_comment

begin_class
DECL|class|MetricsHandlerTest
specifier|public
class|class
name|MetricsHandlerTest
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
comment|// this is needed to enable default SolrJmxReporter in TestHarness
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
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
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|MetricsHandler
name|handler
init|=
operator|new
name|MetricsHandler
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|NamedList
name|values
init|=
name|resp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jetty"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.core.collection1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|nl
operator|.
name|get
argument_list|(
literal|"SEARCHER.new.errors"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|o
argument_list|)
expr_stmt|;
comment|// counter type
name|assertTrue
argument_list|(
name|o
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
comment|// response wasn't serialized so we get here whatever MetricUtils produced instead of NamedList
name|assertNotNull
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|o
operator|)
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"SEARCHER.new.errors"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nl
operator|.
name|get
argument_list|(
literal|"CONTAINER.cores.loaded"
argument_list|)
argument_list|)
expr_stmt|;
comment|// int gauge
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"CONTAINER.cores.loaded"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|nl
operator|.
name|get
argument_list|(
literal|"ADMIN./admin/authorization.clientErrors"
argument_list|)
argument_list|)
expr_stmt|;
comment|// timer type
name|assertEquals
argument_list|(
literal|5
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"ADMIN./admin/authorization.clientErrors"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"group"
argument_list|,
literal|"jvm,jetty"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jetty"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
comment|// "collection" works too, because it's a prefix for "collection1"
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"registry"
argument_list|,
literal|"solr.core.collection,solr.jvm"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.core.collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
comment|// "collection" works too, because it's a prefix for "collection1"
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"registry"
argument_list|,
literal|"solr.core.collection"
argument_list|,
literal|"registry"
argument_list|,
literal|"solr.jvm"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.core.collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"group"
argument_list|,
literal|"jvm,jetty"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jetty"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"group"
argument_list|,
literal|"jvm"
argument_list|,
literal|"group"
argument_list|,
literal|"jetty"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jetty"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"group"
argument_list|,
literal|"node"
argument_list|,
literal|"type"
argument_list|,
literal|"counter"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"ADMIN./admin/authorization.errors"
argument_list|)
argument_list|)
expr_stmt|;
comment|// this is a timer node
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"prefix"
argument_list|,
literal|"CONTAINER.cores,CONTAINER.threadPool"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.jetty"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.core.collection1"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"CONTAINER.cores.lazy"
argument_list|)
argument_list|)
expr_stmt|;
comment|// this is a gauge node
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"CONTAINER.threadPool.coreContainerWorkExecutor.completed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"CONTAINER.threadPool.coreLoadExecutor.completed"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"prefix"
argument_list|,
literal|"CONTAINER.cores"
argument_list|,
literal|"regex"
argument_list|,
literal|"C.*thread.*completed"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"CONTAINER.threadPool.coreContainerWorkExecutor.completed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"CONTAINER.threadPool.coreLoadExecutor.completed"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"group"
argument_list|,
literal|"jvm"
argument_list|,
literal|"prefix"
argument_list|,
literal|"CONTAINER.cores"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.jvm"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"solr.node"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
literal|"group"
argument_list|,
literal|"node"
argument_list|,
literal|"type"
argument_list|,
literal|"timer"
argument_list|,
literal|"prefix"
argument_list|,
literal|"CONTAINER.cores"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|values
operator|=
name|resp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|map
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"solr.node"
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
DECL|method|testCompact
specifier|public
name|void
name|testCompact
parameter_list|()
throws|throws
name|Exception
block|{
name|MetricsHandler
name|handler
init|=
operator|new
name|MetricsHandler
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/metrics"
argument_list|,
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
name|MetricsHandler
operator|.
name|COMPACT_PARAM
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|NamedList
name|values
init|=
name|resp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"solr.core.collection1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|nl
operator|.
name|get
argument_list|(
literal|"SEARCHER.new.errors"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|o
argument_list|)
expr_stmt|;
comment|// counter type
name|assertTrue
argument_list|(
name|o
operator|instanceof
name|Number
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

