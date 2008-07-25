begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|MultiCoreResponse
specifier|public
class|class
name|MultiCoreResponse
extends|extends
name|SolrResponseBase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getCoreStatus
specifier|public
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|getCoreStatus
parameter_list|()
block|{
return|return
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
return|;
block|}
DECL|method|getCoreStatus
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getCoreStatus
parameter_list|(
name|String
name|core
parameter_list|)
block|{
return|return
name|getCoreStatus
argument_list|()
operator|.
name|get
argument_list|(
name|core
argument_list|)
return|;
block|}
DECL|method|getStartTime
specifier|public
name|Date
name|getStartTime
parameter_list|(
name|String
name|core
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|v
init|=
name|getCoreStatus
argument_list|(
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|Date
operator|)
name|v
operator|.
name|get
argument_list|(
literal|"startTime"
argument_list|)
return|;
block|}
DECL|method|getUptime
specifier|public
name|Long
name|getUptime
parameter_list|(
name|String
name|core
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|v
init|=
name|getCoreStatus
argument_list|(
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|Long
operator|)
name|v
operator|.
name|get
argument_list|(
literal|"uptime"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

