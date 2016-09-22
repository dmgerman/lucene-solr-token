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
name|Collections
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
name|update
operator|.
name|processor
operator|.
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
import|;
end_import

begin_import
import|import static
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
operator|.
name|BAD_REQUEST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
import|;
end_import

begin_comment
comment|/**  * An update processor that keeps only the the maximum value from any selected   * fields where multiple values are found.  Correct behavior requires tha all   * of the values in the SolrInputFields being mutated are mutually comparable;   * If this is not the case, then a SolrException will br thrown.   *<p>  * By default, this processor matches no fields.  *</p>  *  *<p>  * In the example configuration below, if a document contains multiple integer   * values (ie:<code>64, 128, 1024</code>) in the field   *<code>largestFileSize</code> then only the biggest value   * (ie:<code>1024</code>) will be kept in that field.  *<br>  *  *<pre class="prettyprint">  *&lt;processor class="solr.MaxFieldValueUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;largestFileSize&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  *  * @see MinFieldValueUpdateProcessorFactory  * @see Collections#max  */
end_comment

begin_class
DECL|class|MaxFieldValueUpdateProcessorFactory
specifier|public
specifier|final
class|class
name|MaxFieldValueUpdateProcessorFactory
extends|extends
name|FieldValueSubsetUpdateProcessorFactory
block|{
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
name|Collection
name|result
init|=
name|values
decl_stmt|;
try|try
block|{
comment|// NOTE: the extra cast to Object is needed to prevent compile
comment|// errors on Eclipse Compiler (ecj) used for javadoc lint
name|result
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|Collections
operator|.
name|max
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
literal|"Field values are not mutually comparable: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultSelector
specifier|public
name|FieldNameSelector
name|getDefaultSelector
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
return|return
name|SELECT_NO_FIELDS
return|;
block|}
block|}
end_class

end_unit

