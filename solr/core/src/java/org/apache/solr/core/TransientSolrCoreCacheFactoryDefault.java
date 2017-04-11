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

begin_class
DECL|class|TransientSolrCoreCacheFactoryDefault
specifier|public
class|class
name|TransientSolrCoreCacheFactoryDefault
extends|extends
name|TransientSolrCoreCacheFactory
block|{
DECL|field|transientSolrCoreCache
name|TransientSolrCoreCache
name|transientSolrCoreCache
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|getTransientSolrCoreCache
specifier|public
name|TransientSolrCoreCache
name|getTransientSolrCoreCache
parameter_list|()
block|{
if|if
condition|(
name|transientSolrCoreCache
operator|==
literal|null
condition|)
block|{
name|transientSolrCoreCache
operator|=
operator|new
name|TransientSolrCoreCacheDefault
argument_list|(
name|getCoreContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|transientSolrCoreCache
return|;
block|}
block|}
end_class

end_unit
