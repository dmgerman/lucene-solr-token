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
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * For non package access see {@link org.apache.lucene.index.IndexReader#getFieldNames(org.apache.lucene.index.IndexReader.FieldOption)}   */
end_comment

begin_class
DECL|class|FieldSetting
class|class
name|FieldSetting
implements|implements
name|Serializable
block|{
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|storeTermVector
name|boolean
name|storeTermVector
init|=
literal|false
decl_stmt|;
DECL|field|storeOffsetWithTermVector
name|boolean
name|storeOffsetWithTermVector
init|=
literal|false
decl_stmt|;
DECL|field|storePositionWithTermVector
name|boolean
name|storePositionWithTermVector
init|=
literal|false
decl_stmt|;
DECL|field|storePayloads
name|boolean
name|storePayloads
init|=
literal|false
decl_stmt|;
DECL|field|stored
name|boolean
name|stored
init|=
literal|false
decl_stmt|;
DECL|field|indexed
name|boolean
name|indexed
init|=
literal|false
decl_stmt|;
DECL|field|tokenized
name|boolean
name|tokenized
init|=
literal|false
decl_stmt|;
DECL|field|compressed
name|boolean
name|compressed
init|=
literal|false
decl_stmt|;
DECL|method|FieldSetting
name|FieldSetting
parameter_list|()
block|{   }
DECL|method|FieldSetting
name|FieldSetting
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|FieldSetting
name|that
init|=
operator|(
name|FieldSetting
operator|)
name|o
decl_stmt|;
return|return
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|fieldName
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

