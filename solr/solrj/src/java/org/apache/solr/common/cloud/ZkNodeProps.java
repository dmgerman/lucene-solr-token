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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import

begin_comment
comment|/**  * ZkNodeProps contains immutable properties for a shard/solr core.  */
end_comment

begin_class
DECL|class|ZkNodeProps
specifier|public
class|class
name|ZkNodeProps
implements|implements
name|JSONWriter
operator|.
name|Writable
block|{
DECL|field|propMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|propMap
decl_stmt|;
comment|/**    * Construct ZKNodeProps from map.    */
DECL|method|ZkNodeProps
specifier|public
name|ZkNodeProps
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|propMap
parameter_list|)
block|{
name|this
operator|.
name|propMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|propMap
operator|.
name|putAll
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct ZKNodeProps from information of an existingZKNodeProps.    */
DECL|method|ZkNodeProps
specifier|public
name|ZkNodeProps
parameter_list|(
name|ZkNodeProps
name|zkNodeProps
parameter_list|)
block|{
name|this
operator|.
name|propMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|propMap
operator|.
name|putAll
argument_list|(
name|zkNodeProps
operator|.
name|propMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor that populates the from array of Strings in form key1, value1,    * key2, value2, ..., keyN, valueN    */
DECL|method|ZkNodeProps
specifier|public
name|ZkNodeProps
parameter_list|(
name|String
modifier|...
name|keyVals
parameter_list|)
block|{
if|if
condition|(
name|keyVals
operator|.
name|length
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"arguments should be key,value"
argument_list|)
throw|;
block|}
name|propMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keyVals
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|propMap
operator|.
name|put
argument_list|(
name|keyVals
index|[
name|i
index|]
argument_list|,
name|keyVals
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get property keys.    */
DECL|method|keySet
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|propMap
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get all properties as map.    */
DECL|method|getProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|propMap
argument_list|)
return|;
block|}
comment|/**    * Create ZkNodeProps from json string that is typically stored in zookeeper.    */
DECL|method|load
specifier|public
specifier|static
name|ZkNodeProps
name|load
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|ZkStateReader
operator|.
name|fromJSON
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
return|return
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|jsonWriter
parameter_list|)
block|{
name|jsonWriter
operator|.
name|write
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get property value.    */
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|propMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entries
init|=
name|propMap
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Check if property key exists.    */
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|propMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
end_class

end_unit

