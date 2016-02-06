begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_class
DECL|class|CachePropertyUtil
specifier|public
class|class
name|CachePropertyUtil
block|{
DECL|method|getAttributeValueAsString
specifier|public
specifier|static
name|String
name|getAttributeValueAsString
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|attr
parameter_list|)
block|{
name|Object
name|o
init|=
name|context
operator|.
name|getSessionAttribute
argument_list|(
name|attr
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|o
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|&&
name|context
operator|.
name|getRequestParameters
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|o
operator|=
name|context
operator|.
name|getRequestParameters
argument_list|()
operator|.
name|get
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getAttributeValue
specifier|public
specifier|static
name|Object
name|getAttributeValue
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|attr
parameter_list|)
block|{
name|Object
name|o
init|=
name|context
operator|.
name|getSessionAttribute
argument_list|(
name|attr
argument_list|,
name|Context
operator|.
name|SCOPE_ENTITY
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|o
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|&&
name|context
operator|.
name|getRequestParameters
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|o
operator|=
name|context
operator|.
name|getRequestParameters
argument_list|()
operator|.
name|get
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|o
return|;
block|}
block|}
end_class

end_unit

