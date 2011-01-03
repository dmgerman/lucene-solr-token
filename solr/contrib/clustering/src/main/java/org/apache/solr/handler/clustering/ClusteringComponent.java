begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.clustering
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SolrDocumentList
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
name|core
operator|.
name|SolrResourceLoader
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
name|handler
operator|.
name|clustering
operator|.
name|carrot2
operator|.
name|CarrotClusteringEngine
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|handler
operator|.
name|component
operator|.
name|ShardRequest
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
name|DocListAndSet
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
name|plugin
operator|.
name|SolrCoreAware
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
comment|/**  * Provide a plugin for clustering results.  Can either be for search results (i.e. via Carrot2) or for  * clustering documents (i.e. via Mahout)  *<p/>  * This engine is experimental.  Output from this engine is subject to change in future releases.  *  */
end_comment

begin_class
DECL|class|ClusteringComponent
specifier|public
class|class
name|ClusteringComponent
extends|extends
name|SearchComponent
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|private
specifier|transient
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ClusteringComponent
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|searchClusteringEngines
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SearchClusteringEngine
argument_list|>
name|searchClusteringEngines
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SearchClusteringEngine
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|documentClusteringEngines
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentClusteringEngine
argument_list|>
name|documentClusteringEngines
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocumentClusteringEngine
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Base name for all spell checker query parameters. This name is also used to    * register this component with SearchHandler.    */
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"clustering"
decl_stmt|;
DECL|field|initParams
specifier|private
name|NamedList
name|initParams
decl_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|name
init|=
name|getClusteringEngineName
argument_list|(
name|rb
argument_list|)
decl_stmt|;
name|boolean
name|useResults
init|=
name|params
operator|.
name|getBool
argument_list|(
name|ClusteringParams
operator|.
name|USE_SEARCH_RESULTS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|useResults
operator|==
literal|true
condition|)
block|{
name|SearchClusteringEngine
name|engine
init|=
name|getSearchClusteringEngine
argument_list|(
name|rb
argument_list|)
decl_stmt|;
if|if
condition|(
name|engine
operator|!=
literal|null
condition|)
block|{
name|DocListAndSet
name|results
init|=
name|rb
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
init|=
operator|new
name|HashMap
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|results
operator|.
name|docList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|solrDocList
init|=
name|engine
operator|.
name|getSolrDocumentList
argument_list|(
name|results
operator|.
name|docList
argument_list|,
name|rb
operator|.
name|req
argument_list|,
name|docIds
argument_list|)
decl_stmt|;
name|Object
name|clusters
init|=
name|engine
operator|.
name|cluster
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|,
name|solrDocList
argument_list|,
name|docIds
argument_list|,
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"clusters"
argument_list|,
name|clusters
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No engine for: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|useCollection
init|=
name|params
operator|.
name|getBool
argument_list|(
name|ClusteringParams
operator|.
name|USE_COLLECTION
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|useCollection
operator|==
literal|true
condition|)
block|{
name|DocumentClusteringEngine
name|engine
init|=
name|documentClusteringEngines
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|engine
operator|!=
literal|null
condition|)
block|{
name|boolean
name|useDocSet
init|=
name|params
operator|.
name|getBool
argument_list|(
name|ClusteringParams
operator|.
name|USE_DOC_SET
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NamedList
name|nl
init|=
literal|null
decl_stmt|;
comment|//TODO: This likely needs to be made into a background task that runs in an executor
if|if
condition|(
name|useDocSet
operator|==
literal|true
condition|)
block|{
name|nl
operator|=
name|engine
operator|.
name|cluster
argument_list|(
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docSet
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nl
operator|=
name|engine
operator|.
name|cluster
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"clusters"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No engine for "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSearchClusteringEngine
specifier|private
name|SearchClusteringEngine
name|getSearchClusteringEngine
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
return|return
name|searchClusteringEngines
operator|.
name|get
argument_list|(
name|getClusteringEngineName
argument_list|(
name|rb
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getClusteringEngineName
specifier|private
name|String
name|getClusteringEngineName
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
return|return
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ClusteringParams
operator|.
name|ENGINE_NAME
argument_list|,
name|ClusteringEngine
operator|.
name|DEFAULT_ENGINE_NAME
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|modifyRequest
specifier|public
name|void
name|modifyRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SearchComponent
name|who
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
operator|||
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|ClusteringParams
operator|.
name|USE_SEARCH_RESULTS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
operator|)
operator|!=
literal|0
condition|)
block|{
name|String
name|fl
init|=
name|sreq
operator|.
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*"
argument_list|)
decl_stmt|;
comment|// if fl=* then we don't need check
if|if
condition|(
name|fl
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
operator|>=
literal|0
condition|)
return|return;
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|getSearchClusteringEngine
argument_list|(
name|rb
argument_list|)
operator|.
name|getFieldsToLoad
argument_list|(
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|flparams
init|=
name|fl
operator|.
name|split
argument_list|(
literal|"[,\\s]+"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|flParamSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|flparams
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|flparam
range|:
name|flparams
control|)
block|{
comment|// no need trim() because of split() by \s+
name|flParamSet
operator|.
name|add
argument_list|(
name|flparam
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|aFieldToLoad
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|flParamSet
operator|.
name|contains
argument_list|(
name|aFieldToLoad
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|aFieldToLoad
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|fl
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
operator|||
operator|!
name|params
operator|.
name|getBool
argument_list|(
name|ClusteringParams
operator|.
name|USE_SEARCH_RESULTS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
name|SearchClusteringEngine
name|engine
init|=
name|getSearchClusteringEngine
argument_list|(
name|rb
argument_list|)
decl_stmt|;
if|if
condition|(
name|engine
operator|!=
literal|null
condition|)
block|{
name|SolrDocumentList
name|solrDocList
init|=
operator|(
name|SolrDocumentList
operator|)
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
comment|// TODO: Currently, docIds is set to null in distributed environment.
comment|// This causes CarrotParams.PRODUCE_SUMMARY doesn't work.
comment|// To work CarrotParams.PRODUCE_SUMMARY under distributed mode, we can choose either one of:
comment|// (a) In each shard, ClusteringComponent produces summary and finishStage()
comment|//     merges these summaries.
comment|// (b) Adding doHighlighting(SolrDocumentList, ...) method to SolrHighlighter and
comment|//     making SolrHighlighter uses "external text" rather than stored values to produce snippets.
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
init|=
literal|null
decl_stmt|;
name|Object
name|clusters
init|=
name|engine
operator|.
name|cluster
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|,
name|solrDocList
argument_list|,
name|docIds
argument_list|,
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"clusters"
argument_list|,
name|clusters
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|name
init|=
name|getClusteringEngineName
argument_list|(
name|rb
argument_list|)
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"No engine for: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
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
name|this
operator|.
name|initParams
operator|=
name|args
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|initParams
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Initializing Clustering Engines"
argument_list|)
expr_stmt|;
name|boolean
name|searchHasDefault
init|=
literal|false
decl_stmt|;
name|boolean
name|documentHasDefault
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|initParams
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|initParams
operator|.
name|getName
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
literal|"engine"
argument_list|)
condition|)
block|{
name|NamedList
name|engineNL
init|=
operator|(
name|NamedList
operator|)
name|initParams
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|className
init|=
operator|(
name|String
operator|)
name|engineNL
operator|.
name|get
argument_list|(
literal|"classname"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
name|className
operator|=
name|CarrotClusteringEngine
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|SolrResourceLoader
name|loader
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|ClusteringEngine
name|clusterer
init|=
operator|(
name|ClusteringEngine
operator|)
name|loader
operator|.
name|newInstance
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterer
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|clusterer
operator|.
name|init
argument_list|(
name|engineNL
argument_list|,
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|boolean
name|isDefault
init|=
name|name
operator|.
name|equals
argument_list|(
name|ClusteringEngine
operator|.
name|DEFAULT_ENGINE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterer
operator|instanceof
name|SearchClusteringEngine
condition|)
block|{
if|if
condition|(
name|isDefault
operator|==
literal|true
operator|&&
name|searchHasDefault
operator|==
literal|false
condition|)
block|{
name|searchHasDefault
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isDefault
operator|==
literal|true
operator|&&
name|searchHasDefault
operator|==
literal|true
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one engine is missing name: "
operator|+
name|engineNL
argument_list|)
throw|;
block|}
name|searchClusteringEngines
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|(
name|SearchClusteringEngine
operator|)
name|clusterer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clusterer
operator|instanceof
name|DocumentClusteringEngine
condition|)
block|{
if|if
condition|(
name|isDefault
operator|==
literal|true
operator|&&
name|documentHasDefault
operator|==
literal|false
condition|)
block|{
name|searchHasDefault
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isDefault
operator|==
literal|true
operator|&&
name|documentHasDefault
operator|==
literal|true
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one engine is missing name: "
operator|+
name|engineNL
argument_list|)
throw|;
block|}
name|documentClusteringEngines
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|(
name|DocumentClusteringEngine
operator|)
name|clusterer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|clusterer
operator|instanceof
name|SearchClusteringEngine
operator|&&
name|searchHasDefault
operator|==
literal|false
condition|)
block|{
name|searchClusteringEngines
operator|.
name|put
argument_list|(
name|ClusteringEngine
operator|.
name|DEFAULT_ENGINE_NAME
argument_list|,
operator|(
name|SearchClusteringEngine
operator|)
name|clusterer
argument_list|)
expr_stmt|;
name|searchHasDefault
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clusterer
operator|instanceof
name|DocumentClusteringEngine
operator|&&
name|documentHasDefault
operator|==
literal|false
condition|)
block|{
name|documentClusteringEngines
operator|.
name|put
argument_list|(
name|ClusteringEngine
operator|.
name|DEFAULT_ENGINE_NAME
argument_list|,
operator|(
name|DocumentClusteringEngine
operator|)
name|clusterer
argument_list|)
expr_stmt|;
name|documentHasDefault
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"More than one engine is missing name: "
operator|+
name|engineNL
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Finished Initializing Clustering Engines"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*   * @return Unmodifiable Map of the engines, key is the name from the config, value is the engine   * */
DECL|method|getSearchClusteringEngines
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SearchClusteringEngine
argument_list|>
name|getSearchClusteringEngines
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|searchClusteringEngines
argument_list|)
return|;
block|}
comment|// ///////////////////////////////////////////
comment|// / SolrInfoMBean
comment|// //////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Clustering component"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

