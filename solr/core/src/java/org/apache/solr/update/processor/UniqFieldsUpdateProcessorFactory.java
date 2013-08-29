begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|core
operator|.
name|SolrCore
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
name|SolrInputDocument
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
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
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
comment|/**  * Removes duplicate values found in fields matching the specified conditions.    * The existing field values are iterated in order, and values are removed when   * they are equal to a value that has already been seen for this field.  *<p>  * By default this processor matches no fields.  *</p>  *   *<p>  * In the example configuration below, if a document initially contains the values   *<code>"Steve","Lucy","Jim",Steve","Alice","Bob","Alice"</code> in a field named   *<code>foo_uniq</code> then using this processor will result in the final list of   * field values being<code>"Steve","Lucy","Jim","Alice","Bob"</code>  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.UniqFieldsUpdateProcessorFactory"&gt;  *&lt;str name="fieldRegex"&gt;.*_uniq&lt;/str&gt;  *&lt;/processor&gt;  *</pre>   */
end_comment

begin_class
DECL|class|UniqFieldsUpdateProcessorFactory
specifier|public
class|class
name|UniqFieldsUpdateProcessorFactory
extends|extends
name|FieldValueSubsetUpdateProcessorFactory
block|{
DECL|field|log
specifier|public
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UniqFieldsUpdateProcessorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
DECL|method|getDefaultSelector
name|getDefaultSelector
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
return|return
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|NamedList
name|args
parameter_list|)
block|{
comment|// legacy init param support, will be removed in 5.0
comment|// no idea why this was ever implimented as<lst> should have just been<arr>
name|NamedList
argument_list|<
name|String
argument_list|>
name|flst
init|=
operator|(
name|NamedList
argument_list|<
name|String
argument_list|>
operator|)
name|args
operator|.
name|remove
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|flst
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Use of the 'fields' init param in UniqFieldsUpdateProcessorFactory is deprecated, please use 'fieldName' (or another FieldMutatingUpdateProcessorFactory selector option) instead"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Replacing 'fields' init param with (individual) 'fieldName' params"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|flst
control|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"fieldName"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|pickSubset
specifier|public
name|Collection
name|pickSubset
parameter_list|(
name|Collection
name|values
parameter_list|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|uniqs
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|values
control|)
block|{
if|if
condition|(
operator|!
name|uniqs
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|uniqs
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

