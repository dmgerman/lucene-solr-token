begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_class
DECL|class|ZkCoreNodeProps
specifier|public
class|class
name|ZkCoreNodeProps
block|{
DECL|field|nodeProps
specifier|private
name|ZkNodeProps
name|nodeProps
decl_stmt|;
DECL|method|ZkCoreNodeProps
specifier|public
name|ZkCoreNodeProps
parameter_list|(
name|ZkNodeProps
name|nodeProps
parameter_list|)
block|{
name|this
operator|.
name|nodeProps
operator|=
name|nodeProps
expr_stmt|;
block|}
comment|// may return null
DECL|method|getCoreUrl
specifier|public
name|String
name|getCoreUrl
parameter_list|()
block|{
return|return
name|getCoreUrl
argument_list|(
name|nodeProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|,
name|nodeProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getNodeName
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
return|;
block|}
DECL|method|getState
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
return|;
block|}
DECL|method|getBaseUrl
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
return|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
return|;
block|}
DECL|method|getCoreUrl
specifier|public
specifier|static
name|String
name|getCoreUrl
parameter_list|(
name|String
name|baseUrl
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseUrl
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|coreName
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|sb
operator|.
name|append
argument_list|(
name|baseUrl
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|baseUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|coreName
operator|==
literal|null
condition|?
literal|""
else|:
name|coreName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|sb
operator|.
name|substring
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
operator|)
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getCoreNodeName
specifier|public
name|String
name|getCoreNodeName
parameter_list|()
block|{
return|return
name|getNodeName
argument_list|()
operator|+
literal|"_"
operator|+
name|getCoreName
argument_list|()
return|;
block|}
DECL|method|getNodeProps
specifier|public
name|ZkNodeProps
name|getNodeProps
parameter_list|()
block|{
return|return
name|nodeProps
return|;
block|}
block|}
end_class

end_unit

