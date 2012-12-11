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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|TextField
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
name|schema
operator|.
name|StrField
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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  *<p>  * Replaces any list of values for a field matching the specified   * conditions with the the count of the number of values for that field.  *</p>  *<p>  * By default, this processor matches no fields.  *</p>  *<p>  * The typical use case for this processor would be in combination with the   * {@link CloneFieldUpdateProcessorFactory} so that it's possible to query by   * the quantity of values in the source field.  *<p>  * For example, in the configuration below, the end result will be that the  *<code>category_count</code> field can be used to search for documents based   * on how many values they contain in the<code>category</code> field.  *</p>  *  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;str name="source"&gt;category&lt;/str&gt;  *&lt;str name="dest"&gt;category_count&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.CountFieldValuesUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;category_count&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.DefaultValueUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;category_count&lt;/str&gt;  *&lt;int name="value"&gt;0&lt;/int&gt;  *&lt;/processor&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  *  *<p>  *<b>NOTE:</b> The use of {@link DefaultValueUpdateProcessorFactory} is   * important in this example to ensure that all documents have a value for the   *<code>category_count</code> field, because   *<code>CountFieldValuesUpdateProcessorFactory</code> only<i>replaces</i> the  * list of values with the size of that list.  If   *<code>DefaultValueUpdateProcessorFactory</code> was not used, then any   * document that had no values for the<code>category</code> field, would also   * have no value in the<code>category_count</code> field.  *</p>  */
end_comment

begin_class
DECL|class|CountFieldValuesUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|CountFieldValuesUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|FieldMutatingUpdateProcessor
argument_list|(
name|getSelector
argument_list|()
argument_list|,
name|next
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|SolrInputField
name|mutate
parameter_list|(
specifier|final
name|SolrInputField
name|src
parameter_list|)
block|{
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
name|result
operator|.
name|setValue
argument_list|(
name|src
operator|.
name|getValueCount
argument_list|()
argument_list|,
name|src
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

