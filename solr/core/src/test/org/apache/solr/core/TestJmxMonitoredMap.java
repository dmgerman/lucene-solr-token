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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|AbstractSolrTestCase
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
name|core
operator|.
name|SolrConfig
operator|.
name|JmxConfiguration
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
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|rmi
operator|.
name|RMIConnectorServer
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|registry
operator|.
name|LocateRegistry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|server
operator|.
name|RMIServerSocketFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|allOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
import|;
end_import

begin_comment
comment|/**  * Test for JmxMonitoredMap  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestJmxMonitoredMap
specifier|public
class|class
name|TestJmxMonitoredMap
extends|extends
name|LuceneTestCase
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|connector
specifier|private
name|JMXConnector
name|connector
decl_stmt|;
DECL|field|mbeanServer
specifier|private
name|MBeanServerConnection
name|mbeanServer
decl_stmt|;
DECL|field|monitoredMap
specifier|private
name|JmxMonitoredMap
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|monitoredMap
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
name|String
name|oldHost
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.rmi.server.hostname"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// this stupid sysprop thing is needed, because remote stubs use an
comment|// arbitrary local ip to connect
comment|// See: http://weblogs.java.net/blog/emcmanus/archive/2006/12/multihomed_comp.html
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.rmi.server.hostname"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
class|class
name|LocalhostRMIServerSocketFactory
implements|implements
name|RMIServerSocketFactory
block|{
name|ServerSocket
name|socket
decl_stmt|;
annotation|@
name|Override
specifier|public
name|ServerSocket
name|createServerSocket
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|socket
operator|=
operator|new
name|ServerSocket
argument_list|(
name|port
argument_list|)
return|;
block|}
block|}
empty_stmt|;
name|LocalhostRMIServerSocketFactory
name|factory
init|=
operator|new
name|LocalhostRMIServerSocketFactory
argument_list|()
decl_stmt|;
name|LocateRegistry
operator|.
name|createRegistry
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|port
operator|=
name|factory
operator|.
name|socket
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Using port: "
operator|+
name|port
argument_list|)
expr_stmt|;
name|String
name|url
init|=
literal|"service:jmx:rmi:///jndi/rmi://127.0.0.1:"
operator|+
name|port
operator|+
literal|"/solrjmx"
decl_stmt|;
name|JmxConfiguration
name|config
init|=
operator|new
name|JmxConfiguration
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
name|url
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|monitoredMap
operator|=
operator|new
name|JmxMonitoredMap
argument_list|<>
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|JMXServiceURL
name|u
init|=
operator|new
name|JMXServiceURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|connector
operator|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|mbeanServer
operator|=
name|connector
operator|.
name|getMBeanServerConnection
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|oldHost
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"java.rmi.server.hostname"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.rmi.server.hostname"
argument_list|,
name|oldHost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connector
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
block|{     }
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTypeName
specifier|public
name|void
name|testTypeName
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInfoMBean
name|mock
init|=
operator|new
name|MockInfoMBean
argument_list|()
decl_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|NamedList
name|dynamicStats
init|=
name|mock
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|size
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"Integer"
argument_list|)
operator|instanceof
name|Integer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"Double"
argument_list|)
operator|instanceof
name|Double
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"Long"
argument_list|)
operator|instanceof
name|Long
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"Short"
argument_list|)
operator|instanceof
name|Short
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"Byte"
argument_list|)
operator|instanceof
name|Byte
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"Float"
argument_list|)
operator|instanceof
name|Float
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dynamicStats
operator|.
name|get
argument_list|(
literal|"String"
argument_list|)
operator|instanceof
name|String
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|objects
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ObjectName
name|name
init|=
name|objects
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getObjectName
argument_list|()
decl_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"Integer"
argument_list|,
name|Integer
operator|.
name|class
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"Double"
argument_list|,
name|Double
operator|.
name|class
argument_list|,
literal|567.534
argument_list|)
expr_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"Long"
argument_list|,
name|Long
operator|.
name|class
argument_list|,
literal|32352463l
argument_list|)
expr_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"Short"
argument_list|,
name|Short
operator|.
name|class
argument_list|,
operator|(
name|short
operator|)
literal|32768
argument_list|)
expr_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"Byte"
argument_list|,
name|Byte
operator|.
name|class
argument_list|,
operator|(
name|byte
operator|)
literal|254
argument_list|)
expr_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"Float"
argument_list|,
name|Float
operator|.
name|class
argument_list|,
literal|3.456f
argument_list|)
expr_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
literal|"String"
argument_list|,
name|String
operator|.
name|class
argument_list|,
literal|"testing"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|assertMBeanTypeAndValue
specifier|public
name|void
name|assertMBeanTypeAndValue
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|String
name|attr
parameter_list|,
name|Class
name|type
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
name|attr
argument_list|)
argument_list|,
name|allOf
argument_list|(
name|instanceOf
argument_list|(
name|type
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutRemoveClear
specifier|public
name|void
name|testPutRemoveClear
parameter_list|()
throws|throws
name|Exception
block|{
name|MockInfoMBean
name|mock
init|=
operator|new
name|MockInfoMBean
argument_list|()
decl_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|objects
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No MBean for mock object found in MBeanServer"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|remove
argument_list|(
literal|"mock"
argument_list|)
expr_stmt|;
name|objects
operator|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MBean for mock object found in MBeanServer even after removal"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock2"
argument_list|,
name|mock
argument_list|)
expr_stmt|;
name|objects
operator|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No MBean for mock object found in MBeanServer"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|monitoredMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|objects
operator|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MBean for mock object found in MBeanServer even after clear has been called"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJmxAugmentedSolrInfoMBean
specifier|public
name|void
name|testJmxAugmentedSolrInfoMBean
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MockInfoMBean
name|mock
init|=
operator|new
name|MockInfoMBean
argument_list|()
decl_stmt|;
specifier|final
name|String
name|jmxKey
init|=
literal|"jmx"
decl_stmt|;
specifier|final
name|String
name|jmxValue
init|=
literal|"jmxValue"
decl_stmt|;
name|MockJmxAugmentedSolrInfoMBean
name|mbean
init|=
operator|new
name|MockJmxAugmentedSolrInfoMBean
argument_list|(
name|mock
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|NamedList
name|getStatisticsForJmx
parameter_list|()
block|{
name|NamedList
name|stats
init|=
name|getStatistics
argument_list|()
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
name|jmxKey
argument_list|,
name|jmxValue
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
block|}
decl_stmt|;
name|monitoredMap
operator|.
name|put
argument_list|(
literal|"mock"
argument_list|,
name|mbean
argument_list|)
expr_stmt|;
comment|// assert getStatistics called when used as a map.  Note can't use equals here to compare
comment|// because getStatistics returns a new Object each time.
name|assertNull
argument_list|(
name|monitoredMap
operator|.
name|get
argument_list|(
literal|"mock"
argument_list|)
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
name|jmxKey
argument_list|)
argument_list|)
expr_stmt|;
comment|//  assert getStatisticsForJmx called when used as jmx server
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|objects
init|=
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
literal|null
argument_list|,
name|Query
operator|.
name|match
argument_list|(
name|Query
operator|.
name|attr
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|Query
operator|.
name|value
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ObjectName
name|name
init|=
name|objects
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getObjectName
argument_list|()
decl_stmt|;
name|assertMBeanTypeAndValue
argument_list|(
name|name
argument_list|,
name|jmxKey
argument_list|,
name|jmxValue
operator|.
name|getClass
argument_list|()
argument_list|,
name|jmxValue
argument_list|)
expr_stmt|;
block|}
DECL|class|MockJmxAugmentedSolrInfoMBean
specifier|private
specifier|static
specifier|abstract
class|class
name|MockJmxAugmentedSolrInfoMBean
extends|extends
name|SolrInfoMBeanWrapper
implements|implements
name|JmxMonitoredMap
operator|.
name|JmxAugmentedSolrInfoMBean
block|{
DECL|method|MockJmxAugmentedSolrInfoMBean
specifier|public
name|MockJmxAugmentedSolrInfoMBean
parameter_list|(
name|SolrInfoMBean
name|mbean
parameter_list|)
block|{
name|super
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStatisticsForJmx
specifier|public
specifier|abstract
name|NamedList
name|getStatisticsForJmx
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

