begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.uima.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|uima
operator|.
name|processor
operator|.
name|SolrUIMAConfiguration
operator|.
name|MapField
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
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngineProcessException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|JCas
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|resource
operator|.
name|ResourceInitializationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|util
operator|.
name|JCasPool
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
comment|/**  * Update document(s) to be indexed with UIMA extracted information  *   */
end_comment

begin_class
DECL|class|UIMAUpdateRequestProcessor
specifier|public
class|class
name|UIMAUpdateRequestProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UIMAUpdateRequestProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|solrUIMAConfiguration
specifier|private
name|SolrUIMAConfiguration
name|solrUIMAConfiguration
decl_stmt|;
DECL|field|ae
specifier|private
name|AnalysisEngine
name|ae
decl_stmt|;
DECL|field|pool
specifier|private
name|JCasPool
name|pool
decl_stmt|;
DECL|method|UIMAUpdateRequestProcessor
specifier|public
name|UIMAUpdateRequestProcessor
parameter_list|(
name|UpdateRequestProcessor
name|next
parameter_list|,
name|String
name|coreName
parameter_list|,
name|SolrUIMAConfiguration
name|config
parameter_list|,
name|AnalysisEngine
name|ae
parameter_list|,
name|JCasPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|ae
operator|=
name|ae
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|solrUIMAConfiguration
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|text
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|/* get Solr document */
name|SolrInputDocument
name|solrInputDocument
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
comment|/* get the fields to analyze */
name|String
index|[]
name|texts
init|=
name|getTextsToAnalyze
argument_list|(
name|solrInputDocument
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|currentText
range|:
name|texts
control|)
block|{
name|text
operator|=
name|currentText
expr_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|/* create a JCas which contain the text to analyze */
name|JCas
name|jcas
init|=
name|pool
operator|.
name|getJCas
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
comment|/* process the text value */
name|processText
argument_list|(
name|text
argument_list|,
name|jcas
argument_list|)
expr_stmt|;
name|UIMAToSolrMapper
name|uimaToSolrMapper
init|=
operator|new
name|UIMAToSolrMapper
argument_list|(
name|solrInputDocument
argument_list|,
name|jcas
argument_list|)
decl_stmt|;
comment|/* get field mapping from config */
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|typesAndFeaturesFieldsMap
init|=
name|solrUIMAConfiguration
operator|.
name|getTypesFeaturesFieldsMapping
argument_list|()
decl_stmt|;
comment|/* map type features on fields */
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|entry
range|:
name|typesAndFeaturesFieldsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|uimaToSolrMapper
operator|.
name|map
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pool
operator|.
name|releaseJCas
argument_list|(
name|jcas
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|logField
init|=
name|solrUIMAConfiguration
operator|.
name|getLogField
argument_list|()
decl_stmt|;
if|if
condition|(
name|logField
operator|==
literal|null
condition|)
block|{
name|SchemaField
name|uniqueKeyField
init|=
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|uniqueKeyField
operator|!=
literal|null
condition|)
block|{
name|logField
operator|=
name|uniqueKeyField
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|optionalFieldInfo
init|=
name|logField
operator|==
literal|null
condition|?
literal|"."
else|:
operator|new
name|StringBuilder
argument_list|(
literal|". "
argument_list|)
operator|.
name|append
argument_list|(
name|logField
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
operator|.
name|getField
argument_list|(
name|logField
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|len
decl_stmt|;
name|String
name|debugString
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|text
operator|.
name|length
argument_list|()
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|debugString
operator|=
operator|new
name|StringBuilder
argument_list|(
literal|" text=\""
argument_list|)
operator|.
name|append
argument_list|(
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"...\""
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|debugString
operator|=
literal|" null text"
expr_stmt|;
block|}
if|if
condition|(
name|solrUIMAConfiguration
operator|.
name|isIgnoreErrors
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"skip the text processing due to {}"
argument_list|,
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|optionalFieldInfo
argument_list|)
operator|.
name|append
argument_list|(
name|debugString
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
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
operator|new
name|StringBuilder
argument_list|(
literal|"processing error "
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|optionalFieldInfo
argument_list|)
operator|.
name|append
argument_list|(
name|debugString
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
comment|/*    * get the texts to analyze from the corresponding fields    */
DECL|method|getTextsToAnalyze
specifier|private
name|String
index|[]
name|getTextsToAnalyze
parameter_list|(
name|SolrInputDocument
name|solrInputDocument
parameter_list|)
block|{
name|String
index|[]
name|fieldsToAnalyze
init|=
name|solrUIMAConfiguration
operator|.
name|getFieldsToAnalyze
argument_list|()
decl_stmt|;
name|boolean
name|merge
init|=
name|solrUIMAConfiguration
operator|.
name|isFieldsMerging
argument_list|()
decl_stmt|;
name|String
index|[]
name|textVals
decl_stmt|;
if|if
condition|(
name|merge
condition|)
block|{
name|StringBuilder
name|unifiedText
init|=
operator|new
name|StringBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|aFieldsToAnalyze
range|:
name|fieldsToAnalyze
control|)
block|{
name|unifiedText
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|solrInputDocument
operator|.
name|getFieldValue
argument_list|(
name|aFieldsToAnalyze
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|textVals
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
name|textVals
index|[
literal|0
index|]
operator|=
name|unifiedText
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|textVals
operator|=
operator|new
name|String
index|[
name|fieldsToAnalyze
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldsToAnalyze
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|textVals
index|[
name|i
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|solrInputDocument
operator|.
name|getFieldValue
argument_list|(
name|fieldsToAnalyze
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|textVals
return|;
block|}
comment|/*    * process a field value executing UIMA on the JCas containing it as document    * text    */
DECL|method|processText
specifier|private
name|void
name|processText
parameter_list|(
name|String
name|textFieldValue
parameter_list|,
name|JCas
name|jcas
parameter_list|)
throws|throws
name|ResourceInitializationException
throws|,
name|AnalysisEngineProcessException
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Analyzing text"
argument_list|)
expr_stmt|;
block|}
name|jcas
operator|.
name|setDocumentText
argument_list|(
name|textFieldValue
argument_list|)
expr_stmt|;
comment|/* perform analysis on text field */
name|ae
operator|.
name|process
argument_list|(
name|jcas
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Text processing completed"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return the configuration object for this request processor    */
DECL|method|getConfiguration
specifier|public
name|SolrUIMAConfiguration
name|getConfiguration
parameter_list|()
block|{
return|return
name|solrUIMAConfiguration
return|;
block|}
block|}
end_class

end_unit

