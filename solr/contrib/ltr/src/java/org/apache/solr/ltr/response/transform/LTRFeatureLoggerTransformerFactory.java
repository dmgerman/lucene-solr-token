begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|response
operator|.
name|transform
package|;
end_package

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
name|Collections
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
name|index
operator|.
name|LeafReaderContext
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
name|search
operator|.
name|Explanation
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
name|SolrDocument
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
name|ltr
operator|.
name|CSVFeatureLogger
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
name|ltr
operator|.
name|FeatureLogger
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
name|ltr
operator|.
name|LTRRescorer
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
name|ltr
operator|.
name|LTRScoringQuery
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
name|ltr
operator|.
name|LTRThreadModule
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
name|ltr
operator|.
name|SolrQueryRequestContextUtils
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
name|ltr
operator|.
name|feature
operator|.
name|Feature
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
name|ltr
operator|.
name|model
operator|.
name|LTRScoringModel
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
name|ltr
operator|.
name|norm
operator|.
name|Normalizer
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
name|ltr
operator|.
name|search
operator|.
name|LTRQParserPlugin
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
name|ltr
operator|.
name|store
operator|.
name|FeatureStore
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
name|ltr
operator|.
name|store
operator|.
name|rest
operator|.
name|ManagedFeatureStore
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
name|ResultContext
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
name|transform
operator|.
name|DocTransformer
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
name|transform
operator|.
name|TransformerFactory
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
name|search
operator|.
name|SolrIndexSearcher
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
name|util
operator|.
name|SolrPluginUtils
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
comment|/**  * This transformer will take care to generate and append in the response the  * features declared in the feature store of the current reranking model,  * or a specified feature store.  Ex.<code>fl=id,[features store=myStore efi.user_text="ibm"]</code>  *   *<h3>Parameters</h3>  *<code>store</code> - The feature store to extract features from. If not provided it  * will default to the features used by your reranking model.<br>  *<code>efi.*</code> - External feature information variables required by the features  * you are extracting.<br>  *<code>format</code> - The format you want the features to be returned in.  Supports (dense|sparse). Defaults to sparse.<br> */
end_comment

begin_class
DECL|class|LTRFeatureLoggerTransformerFactory
specifier|public
class|class
name|LTRFeatureLoggerTransformerFactory
extends|extends
name|TransformerFactory
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
comment|// used inside fl to specify the format (dense|sparse) of the extracted features
DECL|field|FV_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|FV_FORMAT
init|=
literal|"format"
decl_stmt|;
comment|// used inside fl to specify the feature store to use for the feature extraction
DECL|field|FV_STORE
specifier|private
specifier|static
specifier|final
name|String
name|FV_STORE
init|=
literal|"store"
decl_stmt|;
DECL|field|DEFAULT_LOGGING_MODEL_NAME
specifier|private
specifier|static
name|String
name|DEFAULT_LOGGING_MODEL_NAME
init|=
literal|"logging-model"
decl_stmt|;
DECL|field|fvCacheName
specifier|private
name|String
name|fvCacheName
decl_stmt|;
DECL|field|loggingModelName
specifier|private
name|String
name|loggingModelName
init|=
name|DEFAULT_LOGGING_MODEL_NAME
decl_stmt|;
DECL|field|defaultStore
specifier|private
name|String
name|defaultStore
decl_stmt|;
DECL|field|defaultFormat
specifier|private
name|String
name|defaultFormat
decl_stmt|;
DECL|field|csvKeyValueDelimiter
specifier|private
name|char
name|csvKeyValueDelimiter
init|=
name|CSVFeatureLogger
operator|.
name|DEFAULT_KEY_VALUE_SEPARATOR
decl_stmt|;
DECL|field|csvFeatureSeparator
specifier|private
name|char
name|csvFeatureSeparator
init|=
name|CSVFeatureLogger
operator|.
name|DEFAULT_FEATURE_SEPARATOR
decl_stmt|;
DECL|field|threadManager
specifier|private
name|LTRThreadModule
name|threadManager
init|=
literal|null
decl_stmt|;
DECL|method|setFvCacheName
specifier|public
name|void
name|setFvCacheName
parameter_list|(
name|String
name|fvCacheName
parameter_list|)
block|{
name|this
operator|.
name|fvCacheName
operator|=
name|fvCacheName
expr_stmt|;
block|}
DECL|method|setLoggingModelName
specifier|public
name|void
name|setLoggingModelName
parameter_list|(
name|String
name|loggingModelName
parameter_list|)
block|{
name|this
operator|.
name|loggingModelName
operator|=
name|loggingModelName
expr_stmt|;
block|}
DECL|method|setDefaultStore
specifier|public
name|void
name|setDefaultStore
parameter_list|(
name|String
name|defaultStore
parameter_list|)
block|{
name|this
operator|.
name|defaultStore
operator|=
name|defaultStore
expr_stmt|;
block|}
DECL|method|setDefaultFormat
specifier|public
name|void
name|setDefaultFormat
parameter_list|(
name|String
name|defaultFormat
parameter_list|)
block|{
name|this
operator|.
name|defaultFormat
operator|=
name|defaultFormat
expr_stmt|;
block|}
DECL|method|setCsvKeyValueDelimiter
specifier|public
name|void
name|setCsvKeyValueDelimiter
parameter_list|(
name|String
name|csvKeyValueDelimiter
parameter_list|)
block|{
if|if
condition|(
name|csvKeyValueDelimiter
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"csvKeyValueDelimiter must be exactly 1 character"
argument_list|)
throw|;
block|}
name|this
operator|.
name|csvKeyValueDelimiter
operator|=
name|csvKeyValueDelimiter
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|setCsvFeatureSeparator
specifier|public
name|void
name|setCsvFeatureSeparator
parameter_list|(
name|String
name|csvFeatureSeparator
parameter_list|)
block|{
if|if
condition|(
name|csvFeatureSeparator
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"csvFeatureSeparator must be exactly 1 character"
argument_list|)
throw|;
block|}
name|this
operator|.
name|csvFeatureSeparator
operator|=
name|csvFeatureSeparator
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|threadManager
operator|=
name|LTRThreadModule
operator|.
name|getInstance
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|this
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrParams
name|localparams
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
comment|// Hint to enable feature vector cache since we are requesting features
name|SolrQueryRequestContextUtils
operator|.
name|setIsExtractingFeatures
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// Communicate which feature store we are requesting features for
name|SolrQueryRequestContextUtils
operator|.
name|setFvStoreName
argument_list|(
name|req
argument_list|,
name|localparams
operator|.
name|get
argument_list|(
name|FV_STORE
argument_list|,
name|defaultStore
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create and supply the feature logger to be used
name|SolrQueryRequestContextUtils
operator|.
name|setFeatureLogger
argument_list|(
name|req
argument_list|,
name|createFeatureLogger
argument_list|(
name|localparams
operator|.
name|get
argument_list|(
name|FV_FORMAT
argument_list|,
name|defaultFormat
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|FeatureTransformer
argument_list|(
name|name
argument_list|,
name|localparams
argument_list|,
name|req
argument_list|)
return|;
block|}
comment|/**    * returns a FeatureLogger that logs the features    * 'featureFormat' param: 'dense' will write features in dense format,    * 'sparse' will write the features in sparse format, null or empty will    * default to 'sparse'    *    *    * @return a feature logger for the format specified.    */
DECL|method|createFeatureLogger
specifier|private
name|FeatureLogger
name|createFeatureLogger
parameter_list|(
name|String
name|featureFormat
parameter_list|)
block|{
specifier|final
name|FeatureLogger
operator|.
name|FeatureFormat
name|f
decl_stmt|;
if|if
condition|(
name|featureFormat
operator|==
literal|null
operator|||
name|featureFormat
operator|.
name|isEmpty
argument_list|()
operator|||
name|featureFormat
operator|.
name|equals
argument_list|(
literal|"sparse"
argument_list|)
condition|)
block|{
name|f
operator|=
name|FeatureLogger
operator|.
name|FeatureFormat
operator|.
name|SPARSE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|featureFormat
operator|.
name|equals
argument_list|(
literal|"dense"
argument_list|)
condition|)
block|{
name|f
operator|=
name|FeatureLogger
operator|.
name|FeatureFormat
operator|.
name|DENSE
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
name|FeatureLogger
operator|.
name|FeatureFormat
operator|.
name|SPARSE
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"unknown feature logger feature format {}"
argument_list|,
name|featureFormat
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fvCacheName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"a fvCacheName must be configured"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CSVFeatureLogger
argument_list|(
name|fvCacheName
argument_list|,
name|f
argument_list|,
name|csvKeyValueDelimiter
argument_list|,
name|csvFeatureSeparator
argument_list|)
return|;
block|}
DECL|class|FeatureTransformer
class|class
name|FeatureTransformer
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|final
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|localparams
specifier|final
specifier|private
name|SolrParams
name|localparams
decl_stmt|;
DECL|field|req
specifier|final
specifier|private
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|leafContexts
specifier|private
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leafContexts
decl_stmt|;
DECL|field|searcher
specifier|private
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|scoringQuery
specifier|private
name|LTRScoringQuery
name|scoringQuery
decl_stmt|;
DECL|field|modelWeight
specifier|private
name|LTRScoringQuery
operator|.
name|ModelWeight
name|modelWeight
decl_stmt|;
DECL|field|featureLogger
specifier|private
name|FeatureLogger
name|featureLogger
decl_stmt|;
DECL|field|docsWereNotReranked
specifier|private
name|boolean
name|docsWereNotReranked
decl_stmt|;
comment|/**      * @param name      *          Name of the field to be added in a document representing the      *          feature vectors      */
DECL|method|FeatureTransformer
specifier|public
name|FeatureTransformer
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrParams
name|localparams
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|localparams
operator|=
name|localparams
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|setContext
specifier|public
name|void
name|setContext
parameter_list|(
name|ResultContext
name|context
parameter_list|)
block|{
name|super
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|getRequest
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|searcher
operator|=
name|context
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
if|if
condition|(
name|searcher
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
literal|"searcher is null"
argument_list|)
throw|;
block|}
name|leafContexts
operator|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
expr_stmt|;
comment|// Setup LTRScoringQuery
name|scoringQuery
operator|=
name|SolrQueryRequestContextUtils
operator|.
name|getScoringQuery
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|docsWereNotReranked
operator|=
operator|(
name|scoringQuery
operator|==
literal|null
operator|)
expr_stmt|;
name|String
name|featureStoreName
init|=
name|SolrQueryRequestContextUtils
operator|.
name|getFvStoreName
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsWereNotReranked
operator|||
operator|(
name|featureStoreName
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|featureStoreName
operator|.
name|equals
argument_list|(
name|scoringQuery
operator|.
name|getScoringModel
argument_list|()
operator|.
name|getFeatureStoreName
argument_list|()
argument_list|)
operator|)
operator|)
condition|)
block|{
comment|// if store is set in the transformer we should overwrite the logger
specifier|final
name|ManagedFeatureStore
name|fr
init|=
name|ManagedFeatureStore
operator|.
name|getManagedFeatureStore
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|FeatureStore
name|store
init|=
name|fr
operator|.
name|getFeatureStore
argument_list|(
name|featureStoreName
argument_list|)
decl_stmt|;
name|featureStoreName
operator|=
name|store
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|// if featureStoreName was null before this gets actual name
try|try
block|{
specifier|final
name|LoggingModel
name|lm
init|=
operator|new
name|LoggingModel
argument_list|(
name|loggingModelName
argument_list|,
name|featureStoreName
argument_list|,
name|store
operator|.
name|getFeatures
argument_list|()
argument_list|)
decl_stmt|;
name|scoringQuery
operator|=
operator|new
name|LTRScoringQuery
argument_list|(
name|lm
argument_list|,
name|LTRQParserPlugin
operator|.
name|extractEFIParams
argument_list|(
name|localparams
argument_list|)
argument_list|,
literal|true
argument_list|,
name|threadManager
argument_list|)
expr_stmt|;
comment|// request feature weights to be created for all features
comment|// Local transformer efi if provided
name|scoringQuery
operator|.
name|setOriginalQuery
argument_list|(
name|context
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
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
literal|"retrieving the feature store "
operator|+
name|featureStoreName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|scoringQuery
operator|.
name|getFeatureLogger
argument_list|()
operator|==
literal|null
condition|)
block|{
name|scoringQuery
operator|.
name|setFeatureLogger
argument_list|(
name|SolrQueryRequestContextUtils
operator|.
name|getFeatureLogger
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|scoringQuery
operator|.
name|setRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|featureLogger
operator|=
name|scoringQuery
operator|.
name|getFeatureLogger
argument_list|()
expr_stmt|;
try|try
block|{
name|modelWeight
operator|=
name|scoringQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|true
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|modelWeight
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
literal|"error logging the features, model weight is null"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|fv
init|=
name|featureLogger
operator|.
name|getFeatureVector
argument_list|(
name|docid
argument_list|,
name|scoringQuery
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
if|if
condition|(
name|fv
operator|==
literal|null
condition|)
block|{
comment|// FV for this document was not in the cache
name|fv
operator|=
name|featureLogger
operator|.
name|makeFeatureVector
argument_list|(
name|LTRRescorer
operator|.
name|extractFeaturesInfo
argument_list|(
name|modelWeight
argument_list|,
name|docid
argument_list|,
operator|(
name|docsWereNotReranked
condition|?
operator|new
name|Float
argument_list|(
name|score
argument_list|)
else|:
literal|null
operator|)
argument_list|,
name|leafContexts
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|fv
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LoggingModel
specifier|private
specifier|static
class|class
name|LoggingModel
extends|extends
name|LTRScoringModel
block|{
DECL|method|LoggingModel
specifier|public
name|LoggingModel
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|featureStoreName
parameter_list|,
name|List
argument_list|<
name|Feature
argument_list|>
name|allFeatures
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|featureStoreName
argument_list|,
name|allFeatures
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|LoggingModel
specifier|protected
name|LoggingModel
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Feature
argument_list|>
name|features
parameter_list|,
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
parameter_list|,
name|String
name|featureStoreName
parameter_list|,
name|List
argument_list|<
name|Feature
argument_list|>
name|allFeatures
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
name|featureStoreName
argument_list|,
name|allFeatures
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|float
index|[]
name|modelFeatureValuesNormalized
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|finalScore
parameter_list|,
name|List
argument_list|<
name|Explanation
argument_list|>
name|featureExplanations
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|finalScore
argument_list|,
name|toString
argument_list|()
operator|+
literal|" logging model, used only for logging the features"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

