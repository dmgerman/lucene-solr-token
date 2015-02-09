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
name|common
operator|.
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|schema
operator|.
name|BoolField
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
name|IndexSchema
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Locale
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

begin_comment
comment|/**  *<p>  * Attempts to mutate selected fields that have only CharSequence-typed values  * into Boolean values.  *</p>  *<p>  * The default selection behavior is to mutate both those fields that don't match  * a schema field, as well as those fields that do match a schema field and have  * a field type that uses class solr.BooleanField.  *</p>  *<p>  * If all values are parseable as boolean (or are already Boolean), then the field  * will be mutated, replacing each value with its parsed Boolean equivalent;   * otherwise, no mutation will occur.  *</p>  *<p>  * The default true and false values are "true" and "false", respectively, and match  * case-insensitively.  The following configuration changes the acceptable values, and  * requires a case-sensitive match - note that either individual&lt;str&gt; elements  * or&lt;arr&gt;-s of&lt;str&gt; elements may be used to specify the trueValue-s  * and falseValue-s:  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.ParseBooleanFieldUpdateProcessorFactory"&gt;  *&lt;str name="caseSensitive"&gt;true&lt;/str&gt;  *&lt;str name="trueValue"&gt;True&lt;/str&gt;  *&lt;str name="trueValue"&gt;Yes&lt;/str&gt;  *&lt;arr name="falseValue"&gt;  *&lt;str&gt;False&lt;/str&gt;  *&lt;str&gt;No&lt;/str&gt;  *&lt;/arr&gt;  *&lt;/processor&gt;</pre>  */
end_comment

begin_class
DECL|class|ParseBooleanFieldUpdateProcessorFactory
specifier|public
class|class
name|ParseBooleanFieldUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
DECL|field|TRUE_VALUES_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|TRUE_VALUES_PARAM
init|=
literal|"trueValue"
decl_stmt|;
DECL|field|FALSE_VALUES_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|FALSE_VALUES_PARAM
init|=
literal|"falseValue"
decl_stmt|;
DECL|field|CASE_SENSITIVE_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|CASE_SENSITIVE_PARAM
init|=
literal|"caseSensitive"
decl_stmt|;
DECL|field|trueValues
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|trueValues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"true"
block|}
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|falseValues
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|falseValues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"false"
block|}
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|caseSensitive
specifier|private
name|boolean
name|caseSensitive
init|=
literal|false
decl_stmt|;
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
name|AllValuesOrNoneFieldMutatingUpdateProcessor
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
name|Object
name|mutateValue
parameter_list|(
name|Object
name|srcVal
parameter_list|)
block|{
if|if
condition|(
name|srcVal
operator|instanceof
name|CharSequence
condition|)
block|{
name|String
name|stringVal
init|=
name|caseSensitive
condition|?
name|srcVal
operator|.
name|toString
argument_list|()
else|:
name|srcVal
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|trueValues
operator|.
name|contains
argument_list|(
name|stringVal
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|falseValues
operator|.
name|contains
argument_list|(
name|stringVal
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
block|}
if|if
condition|(
name|srcVal
operator|instanceof
name|Boolean
condition|)
block|{
return|return
name|srcVal
return|;
block|}
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Object
name|caseSensitiveParam
init|=
name|args
operator|.
name|remove
argument_list|(
name|CASE_SENSITIVE_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|caseSensitiveParam
condition|)
block|{
if|if
condition|(
name|caseSensitiveParam
operator|instanceof
name|Boolean
condition|)
block|{
name|caseSensitive
operator|=
operator|(
name|Boolean
operator|)
name|caseSensitiveParam
expr_stmt|;
block|}
else|else
block|{
name|caseSensitive
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|caseSensitiveParam
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|trueValuesParam
init|=
name|args
operator|.
name|removeConfigArgs
argument_list|(
name|TRUE_VALUES_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|trueValuesParam
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|trueValues
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|trueVal
range|:
name|trueValuesParam
control|)
block|{
name|trueValues
operator|.
name|add
argument_list|(
name|caseSensitive
condition|?
name|trueVal
else|:
name|trueVal
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|falseValuesParam
init|=
name|args
operator|.
name|removeConfigArgs
argument_list|(
name|FALSE_VALUES_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|falseValuesParam
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|falseValues
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|val
range|:
name|falseValuesParam
control|)
block|{
specifier|final
name|String
name|falseVal
init|=
name|caseSensitive
condition|?
name|val
else|:
name|val
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|trueValues
operator|.
name|contains
argument_list|(
name|falseVal
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Param '"
operator|+
name|FALSE_VALUES_PARAM
operator|+
literal|"' contains a value also in param '"
operator|+
name|TRUE_VALUES_PARAM
operator|+
literal|"': '"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|falseValues
operator|.
name|add
argument_list|(
name|falseVal
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
comment|/**    * Returns true if the field doesn't match any schema field or dynamic field,    *           or if the matched field's type is BoolField    */
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
operator|new
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
operator|(
literal|null
operator|==
name|type
operator|)
operator|||
operator|(
name|type
operator|instanceof
name|BoolField
operator|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

