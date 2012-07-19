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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenMBeanAttributeInfoSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
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
name|JMXConnectorServer
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
name|JMXConnectorServerFactory
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  *<p>  * Responsible for finding (or creating) a MBeanServer from given configuration  * and registering all SolrInfoMBean objects with JMX.  *</p>  *<p/>  *<p>  * Please see http://wiki.apache.org/solr/SolrJmx for instructions on usage and configuration  *</p>  *  *  * @see org.apache.solr.core.SolrConfig.JmxConfiguration  * @since solr 1.3  */
end_comment

begin_class
DECL|class|JmxMonitoredMap
specifier|public
class|class
name|JmxMonitoredMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JmxMonitoredMap
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|server
specifier|private
name|MBeanServer
name|server
init|=
literal|null
decl_stmt|;
DECL|field|jmxRootName
specifier|private
name|String
name|jmxRootName
decl_stmt|;
DECL|field|coreHashCode
specifier|private
name|String
name|coreHashCode
decl_stmt|;
DECL|method|JmxMonitoredMap
specifier|public
name|JmxMonitoredMap
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|coreHashCode
parameter_list|,
specifier|final
name|JmxConfiguration
name|jmxConfig
parameter_list|)
block|{
name|this
operator|.
name|coreHashCode
operator|=
name|coreHashCode
expr_stmt|;
name|jmxRootName
operator|=
operator|(
literal|null
operator|!=
name|jmxConfig
operator|.
name|rootName
condition|?
name|jmxConfig
operator|.
name|rootName
else|:
operator|(
literal|"solr"
operator|+
operator|(
literal|null
operator|!=
name|coreName
condition|?
literal|"/"
operator|+
name|coreName
else|:
literal|""
operator|)
operator|)
operator|)
expr_stmt|;
if|if
condition|(
name|jmxConfig
operator|.
name|serviceUrl
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|jmxConfig
operator|.
name|agentId
operator|==
literal|null
condition|)
block|{
comment|// Try to find the first MBeanServer
name|servers
operator|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jmxConfig
operator|.
name|agentId
operator|!=
literal|null
condition|)
block|{
comment|// Try to find the first MBean server with the given agentId
name|servers
operator|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
name|jmxConfig
operator|.
name|agentId
argument_list|)
expr_stmt|;
comment|// throw Exception if no servers were found with the given agentId
if|if
condition|(
name|servers
operator|==
literal|null
operator|||
name|servers
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"No JMX Servers found with agentId: "
operator|+
name|jmxConfig
operator|.
name|agentId
argument_list|)
throw|;
block|}
if|if
condition|(
name|servers
operator|==
literal|null
operator|||
name|servers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No JMX servers found, not exposing Solr information with JMX."
argument_list|)
expr_stmt|;
return|return;
block|}
name|server
operator|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JMX monitoring is enabled. Adding Solr mbeans to JMX Server: "
operator|+
name|server
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
comment|// Create a new MBeanServer with the given serviceUrl
name|server
operator|=
name|MBeanServerFactory
operator|.
name|newMBeanServer
argument_list|()
expr_stmt|;
name|JMXConnectorServer
name|connector
init|=
name|JMXConnectorServerFactory
operator|.
name|newJMXConnectorServer
argument_list|(
operator|new
name|JMXServiceURL
argument_list|(
name|jmxConfig
operator|.
name|serviceUrl
argument_list|)
argument_list|,
literal|null
argument_list|,
name|server
argument_list|)
decl_stmt|;
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JMX monitoring is enabled at "
operator|+
name|jmxConfig
operator|.
name|serviceUrl
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Release the reference
name|server
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not start JMX monitoring "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Clears the map and unregisters all SolrInfoMBeans in the map from    * MBeanServer    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|entry
range|:
name|entrySet
argument_list|()
control|)
block|{
name|unregister
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds the SolrInfoMBean to the map and registers the given SolrInfoMBean    * instance with the MBeanServer defined for this core. If a SolrInfoMBean is    * already registered with the MBeanServer then it is unregistered and then    * re-registered.    *    * @param key      the JMX type name for this SolrInfoMBean    * @param infoBean the SolrInfoMBean instance to be registered    */
annotation|@
name|Override
DECL|method|put
specifier|public
name|SolrInfoMBean
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|SolrInfoMBean
name|infoBean
parameter_list|)
block|{
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
name|infoBean
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ObjectName
name|name
init|=
name|getObjectName
argument_list|(
name|key
argument_list|,
name|infoBean
argument_list|)
decl_stmt|;
if|if
condition|(
name|server
operator|.
name|isRegistered
argument_list|(
name|name
argument_list|)
condition|)
name|server
operator|.
name|unregisterMBean
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|SolrDynamicMBean
name|mbean
init|=
operator|new
name|SolrDynamicMBean
argument_list|(
name|coreHashCode
argument_list|,
name|infoBean
argument_list|)
decl_stmt|;
name|server
operator|.
name|registerMBean
argument_list|(
name|mbean
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to register info bean: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|infoBean
argument_list|)
return|;
block|}
comment|/**    * Removes the SolrInfoMBean object at the given key and unregisters it from    * MBeanServer    *    * @param key the JMX type name for this SolrInfoMBean    */
annotation|@
name|Override
DECL|method|remove
specifier|public
name|SolrInfoMBean
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|SolrInfoMBean
name|infoBean
init|=
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoBean
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|unregister
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|,
name|infoBean
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to unregister info bean: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|unregister
specifier|private
name|void
name|unregister
parameter_list|(
name|String
name|key
parameter_list|,
name|SolrInfoMBean
name|infoBean
parameter_list|)
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|ObjectName
name|name
init|=
name|getObjectName
argument_list|(
name|key
argument_list|,
name|infoBean
argument_list|)
decl_stmt|;
if|if
condition|(
name|server
operator|.
name|isRegistered
argument_list|(
name|name
argument_list|)
operator|&&
name|coreHashCode
operator|.
name|equals
argument_list|(
name|server
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|,
literal|"coreHashCode"
argument_list|)
argument_list|)
condition|)
block|{
name|server
operator|.
name|unregisterMBean
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Failed to unregister info bean: "
operator|+
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|infoBean
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|infoBean
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
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
block|}
return|return
name|ObjectName
operator|.
name|getInstance
argument_list|(
name|jmxRootName
argument_list|,
name|map
argument_list|)
return|;
block|}
comment|/**    * DynamicMBean is used to dynamically expose all SolrInfoMBean    * getStatistics() NameList keys as String getters.    */
DECL|class|SolrDynamicMBean
specifier|static
class|class
name|SolrDynamicMBean
implements|implements
name|DynamicMBean
block|{
DECL|field|infoBean
specifier|private
name|SolrInfoMBean
name|infoBean
decl_stmt|;
DECL|field|staticStats
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|staticStats
decl_stmt|;
DECL|field|coreHashCode
specifier|private
name|String
name|coreHashCode
decl_stmt|;
DECL|method|SolrDynamicMBean
specifier|public
name|SolrDynamicMBean
parameter_list|(
name|String
name|coreHashCode
parameter_list|,
name|SolrInfoMBean
name|managedResource
parameter_list|)
block|{
name|this
operator|.
name|infoBean
operator|=
name|managedResource
expr_stmt|;
name|staticStats
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
comment|// For which getters are already available in SolrInfoMBean
name|staticStats
operator|.
name|add
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|staticStats
operator|.
name|add
argument_list|(
literal|"version"
argument_list|)
expr_stmt|;
name|staticStats
operator|.
name|add
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
name|staticStats
operator|.
name|add
argument_list|(
literal|"category"
argument_list|)
expr_stmt|;
name|staticStats
operator|.
name|add
argument_list|(
literal|"sourceId"
argument_list|)
expr_stmt|;
name|staticStats
operator|.
name|add
argument_list|(
literal|"source"
argument_list|)
expr_stmt|;
name|this
operator|.
name|coreHashCode
operator|=
name|coreHashCode
expr_stmt|;
block|}
DECL|method|getMBeanInfo
specifier|public
name|MBeanInfo
name|getMBeanInfo
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|MBeanAttributeInfo
argument_list|>
name|attrInfoList
init|=
operator|new
name|ArrayList
argument_list|<
name|MBeanAttributeInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|stat
range|:
name|staticStats
control|)
block|{
name|attrInfoList
operator|.
name|add
argument_list|(
operator|new
name|MBeanAttributeInfo
argument_list|(
name|stat
argument_list|,
name|String
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add core's hashcode
name|attrInfoList
operator|.
name|add
argument_list|(
operator|new
name|MBeanAttributeInfo
argument_list|(
literal|"coreHashCode"
argument_list|,
name|String
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|NamedList
name|dynamicStats
init|=
name|infoBean
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
if|if
condition|(
name|dynamicStats
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dynamicStats
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|dynamicStats
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|staticStats
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Class
name|type
init|=
name|dynamicStats
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|OpenType
name|typeBox
init|=
name|determineType
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|||
name|typeBox
operator|==
literal|null
condition|)
block|{
name|attrInfoList
operator|.
name|add
argument_list|(
operator|new
name|MBeanAttributeInfo
argument_list|(
name|dynamicStats
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|String
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attrInfoList
operator|.
name|add
argument_list|(
operator|new
name|OpenMBeanAttributeInfoSupport
argument_list|(
name|dynamicStats
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|dynamicStats
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|typeBox
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not getStatistics on info bean {}"
argument_list|,
name|infoBean
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|MBeanAttributeInfo
index|[]
name|attrInfoArr
init|=
name|attrInfoList
operator|.
name|toArray
argument_list|(
operator|new
name|MBeanAttributeInfo
index|[
name|attrInfoList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|MBeanInfo
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|infoBean
operator|.
name|getDescription
argument_list|()
argument_list|,
name|attrInfoArr
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|determineType
specifier|private
name|OpenType
name|determineType
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|Field
name|field
range|:
name|SimpleType
operator|.
name|class
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|SimpleType
operator|.
name|class
argument_list|)
condition|)
block|{
name|SimpleType
name|candidate
init|=
operator|(
name|SimpleType
operator|)
name|field
operator|.
name|get
argument_list|(
name|SimpleType
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|candidate
operator|.
name|getTypeName
argument_list|()
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|candidate
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
return|return
literal|null
return|;
block|}
DECL|method|getAttribute
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
name|Object
name|val
decl_stmt|;
if|if
condition|(
literal|"coreHashCode"
operator|.
name|equals
argument_list|(
name|attribute
argument_list|)
condition|)
block|{
name|val
operator|=
name|coreHashCode
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|staticStats
operator|.
name|contains
argument_list|(
name|attribute
argument_list|)
operator|&&
name|attribute
operator|!=
literal|null
operator|&&
name|attribute
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|String
name|getter
init|=
literal|"get"
operator|+
name|attribute
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
name|attribute
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Method
name|meth
init|=
name|infoBean
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|getter
argument_list|)
decl_stmt|;
name|val
operator|=
name|meth
operator|.
name|invoke
argument_list|(
name|infoBean
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AttributeNotFoundException
argument_list|(
name|attribute
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|NamedList
name|list
init|=
name|infoBean
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|val
operator|=
name|list
operator|.
name|get
argument_list|(
name|attribute
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
comment|// Its String or one of the simple types, just return it as JMX suggests direct support for such types
for|for
control|(
name|String
name|simpleTypeName
range|:
name|SimpleType
operator|.
name|ALLOWED_CLASSNAMES_LIST
control|)
block|{
if|if
condition|(
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|simpleTypeName
argument_list|)
condition|)
block|{
return|return
name|val
return|;
block|}
block|}
comment|// Its an arbitrary object which could be something complex and odd, return its toString, assuming that is
comment|// a workable representation of the object
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getAttributes
specifier|public
name|AttributeList
name|getAttributes
parameter_list|(
name|String
index|[]
name|attributes
parameter_list|)
block|{
name|AttributeList
name|list
init|=
operator|new
name|AttributeList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|attribute
range|:
name|attributes
control|)
block|{
try|try
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Attribute
argument_list|(
name|attribute
argument_list|,
name|getAttribute
argument_list|(
name|attribute
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not get attibute "
operator|+
name|attribute
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
DECL|method|setAttribute
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Attribute
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|InvalidAttributeValueException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Operation not Supported"
argument_list|)
throw|;
block|}
DECL|method|setAttributes
specifier|public
name|AttributeList
name|setAttributes
parameter_list|(
name|AttributeList
name|attributes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Operation not Supported"
argument_list|)
throw|;
block|}
DECL|method|invoke
specifier|public
name|Object
name|invoke
parameter_list|(
name|String
name|actionName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Operation not Supported"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

