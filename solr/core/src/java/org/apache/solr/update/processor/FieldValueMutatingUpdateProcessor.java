begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|SolrInputField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Abstract subclass of FieldMutatingUpdateProcessor for implementing   * UpdateProcessors that will mutate all individual values of a selected   * field independently  *   * @see FieldMutatingUpdateProcessorFactory  */
end_comment

begin_class
DECL|class|FieldValueMutatingUpdateProcessor
specifier|public
specifier|abstract
class|class
name|FieldValueMutatingUpdateProcessor
extends|extends
name|FieldMutatingUpdateProcessor
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DELETE_VALUE_SINGLETON
specifier|public
specifier|static
specifier|final
name|Object
name|DELETE_VALUE_SINGLETON
init|=
operator|new
name|Object
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"!!Singleton Object Triggering Value Deletion!!"
return|;
block|}
block|}
decl_stmt|;
DECL|method|FieldValueMutatingUpdateProcessor
specifier|public
name|FieldValueMutatingUpdateProcessor
parameter_list|(
name|FieldNameSelector
name|selector
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|selector
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
comment|/**    * Mutates individual values of a field as needed, or returns the original     * value.    *     * @param src a value from a matched field which should be mutated    * @return the value to use as a replacement for src, or     *<code>DELETE_VALUE_SINGLETON</code> to indicate that the value     *         should be removed completely.    * @see #DELETE_VALUE_SINGLETON    */
DECL|method|mutateValue
specifier|protected
specifier|abstract
name|Object
name|mutateValue
parameter_list|(
specifier|final
name|Object
name|src
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|mutate
specifier|protected
specifier|final
name|SolrInputField
name|mutate
parameter_list|(
specifier|final
name|SolrInputField
name|src
parameter_list|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|src
operator|.
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
return|return
name|src
return|;
comment|//don't mutate
name|SolrInputField
name|result
init|=
operator|new
name|SolrInputField
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Object
name|srcVal
range|:
name|values
control|)
block|{
specifier|final
name|Object
name|destVal
init|=
name|mutateValue
argument_list|(
name|srcVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|DELETE_VALUE_SINGLETON
operator|==
name|destVal
condition|)
block|{
comment|/* NOOP */
name|log
operator|.
name|debug
argument_list|(
literal|"removing value from field '{}': {}"
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|,
name|srcVal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|destVal
operator|!=
name|srcVal
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"replace value from field '{}': {} with {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|src
operator|.
name|getName
argument_list|()
block|,
name|srcVal
block|,
name|destVal
block|}
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|addValue
argument_list|(
name|destVal
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|0
operator|==
name|result
operator|.
name|getValueCount
argument_list|()
condition|?
literal|null
else|:
name|result
return|;
block|}
DECL|method|valueMutator
specifier|public
specifier|static
name|FieldValueMutatingUpdateProcessor
name|valueMutator
parameter_list|(
name|FieldNameSelector
name|selector
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|,
name|Function
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|fun
parameter_list|)
block|{
return|return
operator|new
name|FieldValueMutatingUpdateProcessor
argument_list|(
name|selector
argument_list|,
name|next
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Object
name|mutateValue
parameter_list|(
name|Object
name|src
parameter_list|)
block|{
return|return
name|fun
operator|.
name|apply
argument_list|(
name|src
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

