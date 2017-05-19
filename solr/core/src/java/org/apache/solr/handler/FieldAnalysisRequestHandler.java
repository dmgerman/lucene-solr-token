begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|BytesRef
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
name|request
operator|.
name|FieldAnalysisRequest
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
name|CommonParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Arrays
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
comment|/**  * Provides the ability to specify multiple field types and field names in the same request. Expected parameters:  *<table border="1" summary="table of parameters">  *<tr>  *<th align="left">Name</th>  *<th align="left">Type</th>  *<th align="left">required</th>  *<th align="left">Description</th>  *<th align="left">Multi-valued</th>  *</tr>  *<tr>  *<td>analysis.fieldname</td>  *<td>string</td>  *<td>no</td>  *<td>When present, the text will be analyzed based on the type of this field name.</td>  *<td>Yes, this parameter may hold a comma-separated list of values and the analysis will be performed for each of the specified fields</td>  *</tr>  *<tr>  *<td>analysis.fieldtype</td>  *<td>string</td>  *<td>no</td>  *<td>When present, the text will be analyzed based on the specified type</td>  *<td>Yes, this parameter may hold a comma-separated list of values and the analysis will be performed for each of the specified field types</td>  *</tr>  *<tr>  *<td>analysis.fieldvalue</td>  *<td>string</td>  *<td>no</td>  *<td>The text that will be analyzed. The analysis will mimic the index-time analysis.</td>  *<td>No</td>  *</tr>  *<tr>  *<td>{@code analysis.query} OR {@code q}</td>  *<td>string</td>  *<td>no</td>  *<td>When present, the text that will be analyzed. The analysis will mimic the query-time analysis. Note that the  * {@code analysis.query} parameter as precedes the {@code q} parameters.</td>  *<td>No</td>  *</tr>  *<tr>  *<td>analysis.showmatch</td>  *<td>boolean</td>  *<td>no</td>  *<td>When set to {@code true} and when query analysis is performed, the produced tokens of the field value  * analysis will be marked as "matched" for every token that is produces by the query analysis</td>  *<td>No</td>  *</tr>  *</table>  *<p>Note that if neither analysis.fieldname and analysis.fieldtype is specified, then the default search field's  * analyzer is used.</p>  *<p>Note that if one of analysis.value or analysis.query or q must be specified</p>  *  * @since solr 1.4   */
end_comment

begin_class
DECL|class|FieldAnalysisRequestHandler
specifier|public
class|class
name|FieldAnalysisRequestHandler
extends|extends
name|AnalysisRequestHandlerBase
block|{
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|doAnalysis
specifier|protected
name|NamedList
name|doAnalysis
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|FieldAnalysisRequest
name|analysisRequest
init|=
name|resolveAnalysisRequest
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|IndexSchema
name|indexSchema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
return|return
name|handleAnalysisRequest
argument_list|(
name|analysisRequest
argument_list|,
name|indexSchema
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provide a breakdown of the analysis process of field/query text"
return|;
block|}
comment|// ================================================= Helper methods ================================================
comment|/**    * Resolves the AnalysisRequest based on the parameters in the given SolrParams.    *    * @param req the request    *    * @return AnalysisRequest containing all the information about what needs to be analyzed, and using what    *         fields/types    */
DECL|method|resolveAnalysisRequest
name|FieldAnalysisRequest
name|resolveAnalysisRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|SolrException
block|{
name|SolrParams
name|solrParams
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|FieldAnalysisRequest
name|analysisRequest
init|=
operator|new
name|FieldAnalysisRequest
argument_list|()
decl_stmt|;
name|boolean
name|useDefaultSearchField
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|solrParams
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_TYPE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|analysisRequest
operator|.
name|setFieldTypes
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|solrParams
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_TYPE
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|useDefaultSearchField
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|solrParams
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_NAME
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|analysisRequest
operator|.
name|setFieldNames
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|solrParams
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_NAME
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|useDefaultSearchField
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|useDefaultSearchField
condition|)
block|{
if|if
condition|(
name|solrParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|analysisRequest
operator|.
name|addFieldName
argument_list|(
name|solrParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Field analysis request must contain one of analysis.fieldtype, analysis.fieldname or df."
argument_list|)
throw|;
block|}
block|}
name|analysisRequest
operator|.
name|setQuery
argument_list|(
name|solrParams
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|QUERY
argument_list|,
name|solrParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|FIELD_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|analysisRequest
operator|.
name|getQuery
argument_list|()
operator|==
literal|null
operator|&&
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"One of analysis.fieldvalue, q, or analysis.query parameters must be specified"
argument_list|)
throw|;
block|}
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|!=
literal|null
condition|)
block|{
comment|// NOTE: Only the first content stream is currently processed
for|for
control|(
name|ContentStream
name|stream
range|:
name|streams
control|)
block|{
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|stream
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|value
operator|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// do nothing, leave value set to the request parameter
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
name|analysisRequest
operator|.
name|setFieldValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|analysisRequest
operator|.
name|setShowMatch
argument_list|(
name|solrParams
operator|.
name|getBool
argument_list|(
name|AnalysisParams
operator|.
name|SHOW_MATCH
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|analysisRequest
return|;
block|}
comment|/**    * Handles the resolved analysis request and returns the analysis breakdown response as a named list.    *    * @param request The request to handle.    * @param schema  The index schema.    *    * @return The analysis breakdown as a named list.    */
DECL|method|handleAnalysisRequest
specifier|protected
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|handleAnalysisRequest
parameter_list|(
name|FieldAnalysisRequest
name|request
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|analysisResults
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|fieldTypeAnalysisResults
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getFieldTypes
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|fieldTypeName
range|:
name|request
operator|.
name|getFieldTypes
argument_list|()
control|)
block|{
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|fieldTypeName
argument_list|)
decl_stmt|;
name|fieldTypeAnalysisResults
operator|.
name|add
argument_list|(
name|fieldTypeName
argument_list|,
name|analyzeValues
argument_list|(
name|request
argument_list|,
name|fieldType
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|fieldNameAnalysisResults
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getFieldNames
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|request
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|fieldNameAnalysisResults
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|analyzeValues
argument_list|(
name|request
argument_list|,
name|fieldType
argument_list|,
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|analysisResults
operator|.
name|add
argument_list|(
literal|"field_types"
argument_list|,
name|fieldTypeAnalysisResults
argument_list|)
expr_stmt|;
name|analysisResults
operator|.
name|add
argument_list|(
literal|"field_names"
argument_list|,
name|fieldNameAnalysisResults
argument_list|)
expr_stmt|;
return|return
name|analysisResults
return|;
block|}
comment|/**    * Analyzes the index value (if it exists) and the query value (if it exists) in the given AnalysisRequest, using    * the Analyzers of the given field type.    *    * @param analysisRequest AnalysisRequest from where the index and query values will be taken    * @param fieldType       Type of field whose analyzers will be used    * @param fieldName       Name of the field to be analyzed.  Can be {@code null}    *    * @return NamedList containing the tokens produced by the analyzers of the given field, separated into an index and    *         a query group    */
comment|// package access for testing
DECL|method|analyzeValues
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|analyzeValues
parameter_list|(
name|FieldAnalysisRequest
name|analysisRequest
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|String
name|queryValue
init|=
name|analysisRequest
operator|.
name|getQuery
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termsToMatch
init|=
operator|(
name|queryValue
operator|!=
literal|null
operator|&&
name|analysisRequest
operator|.
name|isShowMatch
argument_list|()
operator|)
condition|?
name|getQueryTokenSet
argument_list|(
name|queryValue
argument_list|,
name|fieldType
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
else|:
name|EMPTY_BYTES_SET
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|analyzeResults
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|analysisRequest
operator|.
name|getFieldValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|AnalysisContext
name|context
init|=
operator|new
name|AnalysisContext
argument_list|(
name|fieldName
argument_list|,
name|fieldType
argument_list|,
name|fieldType
operator|.
name|getIndexAnalyzer
argument_list|()
argument_list|,
name|termsToMatch
argument_list|)
decl_stmt|;
name|NamedList
name|analyzedTokens
init|=
name|analyzeValue
argument_list|(
name|analysisRequest
operator|.
name|getFieldValue
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|analyzeResults
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|analyzedTokens
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|analysisRequest
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|AnalysisContext
name|context
init|=
operator|new
name|AnalysisContext
argument_list|(
name|fieldName
argument_list|,
name|fieldType
argument_list|,
name|fieldType
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
name|analyzedTokens
init|=
name|analyzeValue
argument_list|(
name|analysisRequest
operator|.
name|getQuery
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|analyzeResults
operator|.
name|add
argument_list|(
literal|"query"
argument_list|,
name|analyzedTokens
argument_list|)
expr_stmt|;
block|}
return|return
name|analyzeResults
return|;
block|}
block|}
end_class

end_unit

