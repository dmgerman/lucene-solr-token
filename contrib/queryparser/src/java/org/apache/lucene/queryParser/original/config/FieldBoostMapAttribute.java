begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.original.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
operator|.
name|config
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * This attribute enables the user to define a default boost per field.  * it's used by {@link FieldBoostMapFCListener#buildFieldConfig(FieldConfig)}  */
end_comment

begin_interface
DECL|interface|FieldBoostMapAttribute
specifier|public
interface|interface
name|FieldBoostMapAttribute
extends|extends
name|Attribute
block|{
comment|/**    * @param boosts a mapping from field name to its default boost    */
DECL|method|setFieldBoostMap
specifier|public
name|void
name|setFieldBoostMap
parameter_list|(
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|Float
argument_list|>
name|boosts
parameter_list|)
function_decl|;
DECL|method|getFieldBoostMap
specifier|public
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|Float
argument_list|>
name|getFieldBoostMap
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

