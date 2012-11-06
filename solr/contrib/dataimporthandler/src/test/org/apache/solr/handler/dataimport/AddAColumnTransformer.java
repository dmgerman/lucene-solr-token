begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|AddAColumnTransformer
specifier|public
class|class
name|AddAColumnTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|String
name|colName
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"newColumnName"
argument_list|)
decl_stmt|;
name|colName
operator|=
name|colName
operator|==
literal|null
condition|?
literal|"AddAColumn_s"
else|:
name|colName
expr_stmt|;
name|String
name|colValue
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"newColumnValue"
argument_list|)
decl_stmt|;
name|colValue
operator|=
name|colValue
operator|==
literal|null
condition|?
literal|"Added"
else|:
name|colValue
expr_stmt|;
name|aRow
operator|.
name|put
argument_list|(
name|colName
argument_list|,
name|colValue
argument_list|)
expr_stmt|;
return|return
name|aRow
return|;
block|}
block|}
end_class

end_unit

