begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

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
name|Collection
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Essetially a Map<FieldName, {@link org.apache.lucene.store.instantiated.FieldSetting}>   */
end_comment

begin_class
DECL|class|FieldSettings
class|class
name|FieldSettings
block|{
DECL|method|FieldSettings
name|FieldSettings
parameter_list|()
block|{   }
DECL|field|fieldSettings
specifier|private
name|Map
argument_list|<
comment|/** field name */
name|String
argument_list|,
name|FieldSetting
argument_list|>
name|fieldSettings
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldSetting
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|merge
specifier|synchronized
name|FieldSetting
name|merge
parameter_list|(
name|FieldSetting
name|fieldSetting
parameter_list|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|fieldSetting
operator|.
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|setting
operator|==
literal|null
condition|)
block|{
name|setting
operator|=
operator|new
name|FieldSetting
argument_list|(
name|fieldSetting
operator|.
name|fieldName
argument_list|)
expr_stmt|;
name|fieldSettings
operator|.
name|put
argument_list|(
name|fieldSetting
operator|.
name|fieldName
argument_list|,
name|setting
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|stored
condition|)
block|{
name|setting
operator|.
name|stored
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|compressed
condition|)
block|{
name|setting
operator|.
name|compressed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
literal|"b3"
operator|.
name|equals
argument_list|(
name|fieldSetting
operator|.
name|fieldName
argument_list|)
condition|)
block|{
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|indexed
condition|)
block|{
name|setting
operator|.
name|indexed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|tokenized
condition|)
block|{
name|setting
operator|.
name|tokenized
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|storeTermVector
condition|)
block|{
name|setting
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|storeOffsetWithTermVector
condition|)
block|{
name|setting
operator|.
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|storePositionWithTermVector
condition|)
block|{
name|setting
operator|.
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fieldSetting
operator|.
name|storePayloads
condition|)
block|{
name|setting
operator|.
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|setting
return|;
block|}
DECL|method|get
name|FieldSetting
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|get
name|FieldSetting
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|create
parameter_list|)
block|{
name|FieldSetting
name|fieldSetting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
operator|&&
name|fieldSetting
operator|==
literal|null
condition|)
block|{
name|fieldSetting
operator|=
operator|new
name|FieldSetting
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fieldSettings
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|fieldSetting
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSetting
return|;
block|}
DECL|method|values
name|Collection
argument_list|<
name|FieldSetting
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|fieldSettings
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class

end_unit

