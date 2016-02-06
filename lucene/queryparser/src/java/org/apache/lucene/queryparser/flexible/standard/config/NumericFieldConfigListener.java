begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfigListener
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
import|;
end_import

begin_comment
comment|/**  * This listener is used to listen to {@link FieldConfig} requests in  * {@link QueryConfigHandler} and add {@link ConfigurationKeys#NUMERIC_CONFIG}  * based on the {@link ConfigurationKeys#NUMERIC_CONFIG_MAP} set in the  * {@link QueryConfigHandler}.  *   * @see NumericConfig  * @see QueryConfigHandler  * @see ConfigurationKeys#NUMERIC_CONFIG  * @see ConfigurationKeys#NUMERIC_CONFIG_MAP  */
end_comment

begin_class
DECL|class|NumericFieldConfigListener
specifier|public
class|class
name|NumericFieldConfigListener
implements|implements
name|FieldConfigListener
block|{
DECL|field|config
specifier|final
specifier|private
name|QueryConfigHandler
name|config
decl_stmt|;
comment|/**    * Construcs a {@link NumericFieldConfigListener} object using the given {@link QueryConfigHandler}.    *     * @param config the {@link QueryConfigHandler} it will listen too    */
DECL|method|NumericFieldConfigListener
specifier|public
name|NumericFieldConfigListener
parameter_list|(
name|QueryConfigHandler
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"config cannot be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildFieldConfig
specifier|public
name|void
name|buildFieldConfig
parameter_list|(
name|FieldConfig
name|fieldConfig
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|NumericConfig
argument_list|>
name|numericConfigMap
init|=
name|config
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|NUMERIC_CONFIG_MAP
argument_list|)
decl_stmt|;
if|if
condition|(
name|numericConfigMap
operator|!=
literal|null
condition|)
block|{
name|NumericConfig
name|numericConfig
init|=
name|numericConfigMap
operator|.
name|get
argument_list|(
name|fieldConfig
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|numericConfig
operator|!=
literal|null
condition|)
block|{
name|fieldConfig
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|NUMERIC_CONFIG
argument_list|,
name|numericConfig
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

