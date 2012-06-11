begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Used to request notification when the core is closed.  *<p/>  * Call {@link org.apache.solr.core.SolrCore#addCloseHook(org.apache.solr.core.CloseHook)} during the {@link org.apache.solr.util.plugin.SolrCoreAware#inform(SolrCore)} method to  * add a close hook to your object.  *<p/>  * The close hook can be useful for releasing objects related to the request handler (for instance, if you have a JDBC DataSource or something like that)  */
end_comment

begin_class
DECL|class|CloseHook
specifier|public
specifier|abstract
class|class
name|CloseHook
block|{
comment|/**    * Method called when the given SolrCore object is closing / shutting down but before the update handler and    * searcher(s) are actually closed    *<br />    *<b>Important:</b> Keep the method implementation as short as possible. If it were to use any heavy i/o , network connections -    * it might be a better idea to launch in a separate Thread so as to not to block the process of    * shutting down a given SolrCore instance.    *    * @param core SolrCore object that is shutting down / closing    */
DECL|method|preClose
specifier|public
specifier|abstract
name|void
name|preClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
function_decl|;
comment|/**    * Method called when the given SolrCore object has been shut down and update handlers and searchers are closed    *<br/>    * Use this method for post-close clean up operations e.g. deleting the index from disk.    *<br/>    *<b>The core's passed to the method is already closed and therefore, it's update handler or searcher should *NOT* be used</b>    *    *<b>Important:</b> Keep the method implementation as short as possible. If it were to use any heavy i/o , network connections -    * it might be a better idea to launch in a separate Thread so as to not to block the process of    * shutting down a given SolrCore instance.    *    * @param core    */
DECL|method|postClose
specifier|public
specifier|abstract
name|void
name|postClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
function_decl|;
block|}
end_class

end_unit

