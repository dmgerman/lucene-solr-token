begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerFactory
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
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Utility methods to find a MBeanServer.  *  * This was factored out from {@link org.apache.solr.core.JmxMonitoredMap}  * and can eventually replace the logic used there.  */
end_comment

begin_class
DECL|class|JmxUtil
specifier|public
specifier|final
class|class
name|JmxUtil
block|{
comment|/**    * Retrieve the first MBeanServer found.    *    * @return the first MBeanServer found    */
DECL|method|findFirstMBeanServer
specifier|public
specifier|static
name|MBeanServer
name|findFirstMBeanServer
parameter_list|()
block|{
return|return
name|findMBeanServerForAgentId
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Find a MBeanServer given a service url.    *    * @param serviceUrl the service url    * @return a MBeanServer    */
DECL|method|findMBeanServerForServiceUrl
specifier|public
specifier|static
name|MBeanServer
name|findMBeanServerForServiceUrl
parameter_list|(
name|String
name|serviceUrl
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|serviceUrl
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MBeanServer
name|server
init|=
name|MBeanServerFactory
operator|.
name|newMBeanServer
argument_list|()
decl_stmt|;
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
return|return
name|server
return|;
block|}
comment|/**    * Find a MBeanServer given an agent id.    *    * @param agentId the agent id    * @return a MBeanServer    */
DECL|method|findMBeanServerForAgentId
specifier|public
specifier|static
name|MBeanServer
name|findMBeanServerForAgentId
parameter_list|(
name|String
name|agentId
parameter_list|)
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
name|agentId
argument_list|)
decl_stmt|;
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
return|return
literal|null
return|;
block|}
return|return
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit
