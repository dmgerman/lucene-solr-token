begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.registry
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextListener
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * This Listener creates the  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry} when the  * context is loaded. The registry will be loaded before the  * {@link org.apache.lucene.gdata.servlet.RequestControllerServlet} is loaded.  * The Registry will be loaded and set up before the REST interface is available.  *<p>  * This ContextListener has to be configured in the<code>web.xml</code>  * deployment descriptor.  *</p>  *<p>  * When the  * {@link javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)}  * method is called the registry will be destroyed using  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry#destroy()}  * method.  *   *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|RegistryContextListener
specifier|public
class|class
name|RegistryContextListener
implements|implements
name|ServletContextListener
block|{
DECL|field|serverRegistry
specifier|private
name|GDataServerRegistry
name|serverRegistry
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RegistryContextListener
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)      */
DECL|method|contextInitialized
specifier|public
name|void
name|contextInitialized
parameter_list|(
name|ServletContextEvent
name|arg0
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"RegistryContextListener has been loaded"
argument_list|)
expr_stmt|;
try|try
block|{
name|RegistryBuilder
operator|.
name|buildRegistry
argument_list|()
expr_stmt|;
name|this
operator|.
name|serverRegistry
operator|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
expr_stmt|;
comment|/*              * catch all exceptions and destroy the registry to release all resources.              * some components start lots of threads, the will remain running if the registry is not destroyed              */
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"can not register required components"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not register required components"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)      */
DECL|method|contextDestroyed
specifier|public
name|void
name|contextDestroyed
parameter_list|(
name|ServletContextEvent
name|arg0
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Destroying context"
argument_list|)
expr_stmt|;
comment|/*          * this might be null if startup fails          * --> prevent null pointer exception          */
if|if
condition|(
name|this
operator|.
name|serverRegistry
operator|!=
literal|null
condition|)
name|this
operator|.
name|serverRegistry
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

