begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|solr
operator|.
name|core
operator|.
name|JmxMonitoredMap
operator|.
name|SolrDynamicMBean
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
name|javax
operator|.
name|management
operator|.
name|*
import|;
end_import

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
name|List
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
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_comment
comment|/**  * Test for JMX Integration  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestJmxIntegration
specifier|public
class|class
name|TestJmxIntegration
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
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
comment|// Make sure that at least one MBeanServer is available
name|MBeanServer
name|mbeanServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJmxRegistration
specifier|public
name|void
name|testJmxRegistration
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Servers in testJmxRegistration: "
operator|+
name|servers
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"MBeanServers were null"
argument_list|,
name|servers
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No MBeanServer was found"
argument_list|,
name|servers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbeanServer
init|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No MBeans found in server"
argument_list|,
name|mbeanServer
operator|.
name|getMBeanCount
argument_list|()
operator|>
literal|0
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
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No SolrInfoMBean objects found in mbean server"
argument_list|,
name|objects
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectInstance
name|o
range|:
name|objects
control|)
block|{
name|MBeanInfo
name|mbeanInfo
init|=
name|mbeanServer
operator|.
name|getMBeanInfo
argument_list|(
name|o
operator|.
name|getObjectName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbeanInfo
operator|.
name|getClassName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|SolrDynamicMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"No Attributes found for mbean: "
operator|+
name|mbeanInfo
argument_list|,
name|mbeanInfo
operator|.
name|getAttributes
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testJmxUpdate
specifier|public
name|void
name|testJmxUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Servers in testJmxUpdate: "
operator|+
name|servers
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectName
name|searcher
init|=
literal|null
decl_stmt|;
comment|// wait until searcher is registered
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|searcher
operator|=
name|getObjectName
argument_list|(
literal|"searcher"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"searcher was never registered"
argument_list|)
throw|;
name|MBeanServer
name|mbeanServer
init|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Mbeans in server: "
operator|+
name|mbeanServer
operator|.
name|queryNames
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No mbean found for SolrIndexSearcher"
argument_list|,
name|mbeanServer
operator|.
name|queryMBeans
argument_list|(
name|searcher
argument_list|,
literal|null
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|oldNumDocs
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|searcher
argument_list|,
literal|"numDocs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|mbeanServer
operator|.
name|getAttribute
argument_list|(
name|searcher
argument_list|,
literal|"numDocs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"New numDocs is same as old numDocs as reported by JMX"
argument_list|,
name|numDocs
operator|>
name|oldNumDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|getObjectName
specifier|private
name|ObjectName
name|getObjectName
parameter_list|(
name|String
name|key
parameter_list|,
name|SolrInfoMBean
name|infoBean
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|infoBean
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ObjectName
operator|.
name|getInstance
argument_list|(
literal|"solr"
argument_list|,
name|map
argument_list|)
return|;
block|}
block|}
end_class

end_unit

