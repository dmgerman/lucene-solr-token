begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|FieldAnalysisResponse
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
name|params
operator|.
name|AnalysisParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|SolrParams
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
name|ContentStream
import|;
end_import

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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * A request for the org.apache.solr.handler.DocumentAnalysisRequestHandler.  *  * @version $Id$  * @since solr.14  */
end_comment

begin_class
DECL|class|FieldAnalysisRequest
specifier|public
class|class
name|FieldAnalysisRequest
extends|extends
name|SolrRequest
block|{
DECL|field|fieldValue
specifier|private
name|String
name|fieldValue
decl_stmt|;
DECL|field|query
specifier|private
name|String
name|query
decl_stmt|;
DECL|field|showMatch
specifier|private
name|boolean
name|showMatch
decl_stmt|;
DECL|field|fieldNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
DECL|field|fieldTypes
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fieldTypes
decl_stmt|;
comment|/**    * Constructs a new FieldAnalysisRequest with a default uri of "/fieldanalysis".    */
DECL|method|FieldAnalysisRequest
specifier|public
name|FieldAnalysisRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|"/analysis/field"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new FieldAnalysisRequest with a given uri.    *    * @param uri the uri of the request handler.    */
DECL|method|FieldAnalysisRequest
specifier|public
name|FieldAnalysisRequest
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_VALUE
argument_list|,
name|fieldValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|AnalysisParams
operator|.
name|QUERY
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|AnalysisParams
operator|.
name|SHOW_MATCH
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|showMatch
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldNames
operator|!=
literal|null
condition|)
block|{
name|String
name|fieldNameValue
init|=
name|listToCommaDelimitedString
argument_list|(
name|fieldNames
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_NAME
argument_list|,
name|fieldNameValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldTypes
operator|!=
literal|null
condition|)
block|{
name|String
name|fieldTypeValue
init|=
name|listToCommaDelimitedString
argument_list|(
name|fieldTypes
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_TYPE
argument_list|,
name|fieldTypeValue
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|process
specifier|public
name|FieldAnalysisResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
if|if
condition|(
name|fieldTypes
operator|==
literal|null
operator|&&
name|fieldNames
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"At least one field type or field name need to be specified"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The field value must be set"
argument_list|)
throw|;
block|}
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|FieldAnalysisResponse
name|res
init|=
operator|new
name|FieldAnalysisResponse
argument_list|()
decl_stmt|;
name|res
operator|.
name|setResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|//================================================ Helper Methods ==================================================
comment|/**    * Convers the given list of string to a comma-separated string.    *    * @param list The list of string.    *    * @return The comma-separated string.    */
DECL|method|listToCommaDelimitedString
specifier|static
name|String
name|listToCommaDelimitedString
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|list
control|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//============================================ Setter/Getter Methods ===============================================
comment|/**    * Sets the field value to be analyzed.    *    * @param fieldValue The field value to be analyzed.    *    * @return This FieldAnalysisRequest (fluent interface support).    */
DECL|method|setFieldValue
specifier|public
name|FieldAnalysisRequest
name|setFieldValue
parameter_list|(
name|String
name|fieldValue
parameter_list|)
block|{
name|this
operator|.
name|fieldValue
operator|=
name|fieldValue
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the field value that will be analyzed when this request is processed.    *    * @return The field value that will be analyzed when this request is processed.    */
DECL|method|getFieldValue
specifier|public
name|String
name|getFieldValue
parameter_list|()
block|{
return|return
name|fieldValue
return|;
block|}
comment|/**    * Sets the query to be analyzed. May be {@code null} indicated that no query analysis should take place.    *    * @param query The query to be analyzed.    *    * @return This FieldAnalysisRequest (fluent interface support).    */
DECL|method|setQuery
specifier|public
name|FieldAnalysisRequest
name|setQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the query that will be analyzed. May return {@code null} indicating that no query analysis will be    * performed.    *    * @return The query that will be analyzed. May return {@code null} indicating that no query analysis will be    *         performed.    */
DECL|method|getQuery
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
comment|/**    * Sets whether index time tokens that match query time tokens should be marked as a "match". By default this is set    * to {@code false}. Obviously, this flag is ignored if when the query is set to {@code null}.    *    * @param showMatch Sets whether index time tokens that match query time tokens should be marked as a "match".    *    * @return This FieldAnalysisRequest (fluent interface support).    */
DECL|method|setShowMatch
specifier|public
name|FieldAnalysisRequest
name|setShowMatch
parameter_list|(
name|boolean
name|showMatch
parameter_list|)
block|{
name|this
operator|.
name|showMatch
operator|=
name|showMatch
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns whether index time tokens that match query time tokens should be marked as a "match".    *    * @return Whether index time tokens that match query time tokens should be marked as a "match".    *    * @see #setShowMatch(boolean)    */
DECL|method|isShowMatch
specifier|public
name|boolean
name|isShowMatch
parameter_list|()
block|{
return|return
name|showMatch
return|;
block|}
comment|/**    * Adds the given field name for analysis.    *    * @param fieldName A field name on which the analysis should be performed.    *    * @return this FieldAnalysisRequest (fluent interface support).    */
DECL|method|addFieldName
specifier|public
name|FieldAnalysisRequest
name|addFieldName
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldNames
operator|==
literal|null
condition|)
block|{
name|fieldNames
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fieldNames
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the field names on which the analysis should be performed.      *      * @param fieldNames The field names on which the analysis should be performed.      *      * @return this FieldAnalysisRequest (fluent interface support).      */
DECL|method|setFieldNames
specifier|public
name|FieldAnalysisRequest
name|setFieldNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns a list of field names the analysis should be performed on. May return {@code null} indicating that no    * analysis will be performed on field names.    *    * @return The field names the analysis should be performed on.    */
DECL|method|getFieldNames
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
comment|/**    * Adds the given field type for analysis.    *    * @param fieldTypeName A field type name on which analysis should be performed.    *    * @return This FieldAnalysisRequest (fluent interface support).    */
DECL|method|addFieldType
specifier|public
name|FieldAnalysisRequest
name|addFieldType
parameter_list|(
name|String
name|fieldTypeName
parameter_list|)
block|{
if|if
condition|(
name|fieldTypes
operator|==
literal|null
condition|)
block|{
name|fieldTypes
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fieldTypes
operator|.
name|add
argument_list|(
name|fieldTypeName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Sets the field types on which analysis should be performed.    *    * @param fieldTypes The field type names on which analysis should be performed.    *    * @return This FieldAnalysisRequest (fluent interface support).    */
DECL|method|setFieldTypes
specifier|public
name|FieldAnalysisRequest
name|setFieldTypes
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fieldTypes
parameter_list|)
block|{
name|this
operator|.
name|fieldTypes
operator|=
name|fieldTypes
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns a list of field types the analysis should be performed on. May return {@code null} indicating that no    * analysis will be peformed on field types.    *    * @return The field types the analysis should be performed on.    */
DECL|method|getFieldTypes
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFieldTypes
parameter_list|()
block|{
return|return
name|fieldTypes
return|;
block|}
block|}
end_class

end_unit

