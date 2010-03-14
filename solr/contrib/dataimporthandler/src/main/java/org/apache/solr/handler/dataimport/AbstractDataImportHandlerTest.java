begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|AbstractSolrTestCase
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
name|io
operator|.
name|File
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

begin_comment
comment|/**  *<p>  * Abstract base class for DataImportHandler tests  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|AbstractDataImportHandlerTest
specifier|public
specifier|abstract
class|class
name|AbstractDataImportHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// remove dataimport.properties
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"solr/conf/dataimport.properties"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Looking for dataimport.properties at: "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting dataimport.properties"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete dataimport.properties"
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|loadDataConfig
specifier|protected
name|String
name|loadDataConfig
parameter_list|(
name|String
name|dataConfigFileName
parameter_list|)
block|{
try|try
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
return|return
name|SolrWriter
operator|.
name|getResourceAsString
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|dataConfigFileName
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|runFullImport
specifier|protected
name|void
name|runFullImport
parameter_list|(
name|String
name|dataConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"debug"
argument_list|,
literal|"on"
argument_list|,
literal|"clean"
argument_list|,
literal|"true"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"dataConfig"
argument_list|,
name|dataConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|runDeltaImport
specifier|protected
name|void
name|runDeltaImport
parameter_list|(
name|String
name|dataConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"command"
argument_list|,
literal|"delta-import"
argument_list|,
literal|"debug"
argument_list|,
literal|"on"
argument_list|,
literal|"clean"
argument_list|,
literal|"false"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"dataConfig"
argument_list|,
name|dataConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs a full-import using the given dataConfig and the provided request parameters.    *    * By default, debug=on, clean=true and commit=true are passed which can be overridden.    *    * @param dataConfig the data-config xml as a string    * @param extraParams any extra request parameters needed to be passed to DataImportHandler    * @throws Exception in case of any error    */
DECL|method|runFullImport
specifier|protected
name|void
name|runFullImport
parameter_list|(
name|String
name|dataConfig
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extraParams
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"dataConfig"
argument_list|,
name|dataConfig
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"clean"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|putAll
argument_list|(
name|extraParams
argument_list|)
expr_stmt|;
name|NamedList
name|l
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
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
name|e
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LocalSolrQueryRequest
name|request
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper for creating a Context instance. Useful for testing Transformers    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getContext
specifier|public
specifier|static
name|TestContext
name|getContext
parameter_list|(
name|DataConfig
operator|.
name|Entity
name|parentEntity
parameter_list|,
name|VariableResolverImpl
name|resolver
parameter_list|,
name|DataSource
name|parentDataSource
parameter_list|,
name|String
name|currProcess
parameter_list|,
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entityFields
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
parameter_list|)
block|{
if|if
condition|(
name|resolver
operator|==
literal|null
condition|)
name|resolver
operator|=
operator|new
name|VariableResolverImpl
argument_list|()
expr_stmt|;
specifier|final
name|Context
name|delegate
init|=
operator|new
name|ContextImpl
argument_list|(
name|parentEntity
argument_list|,
name|resolver
argument_list|,
name|parentDataSource
argument_list|,
name|currProcess
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|TestContext
argument_list|(
name|entityAttrs
argument_list|,
name|delegate
argument_list|,
name|entityFields
argument_list|,
name|parentEntity
operator|==
literal|null
argument_list|)
return|;
block|}
comment|/**    * Strings at even index are keys, odd-index strings are values in the    * returned map    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createMap
specifier|public
specifier|static
name|Map
name|createMap
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|Map
name|result
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|==
literal|null
operator|||
name|args
operator|.
name|length
operator|==
literal|0
condition|)
return|return
name|result
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
name|result
operator|.
name|put
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|TestContext
specifier|static
class|class
name|TestContext
extends|extends
name|Context
block|{
DECL|field|entityAttrs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
decl_stmt|;
DECL|field|delegate
specifier|private
specifier|final
name|Context
name|delegate
decl_stmt|;
DECL|field|entityFields
specifier|private
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entityFields
decl_stmt|;
DECL|field|root
specifier|private
specifier|final
name|boolean
name|root
decl_stmt|;
DECL|field|script
DECL|field|scriptlang
name|String
name|script
decl_stmt|,
name|scriptlang
decl_stmt|;
DECL|method|TestContext
specifier|public
name|TestContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
parameter_list|,
name|Context
name|delegate
parameter_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entityFields
parameter_list|,
name|boolean
name|root
parameter_list|)
block|{
name|this
operator|.
name|entityAttrs
operator|=
name|entityAttrs
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|entityFields
operator|=
name|entityFields
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
DECL|method|getEntityAttribute
specifier|public
name|String
name|getEntityAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entityAttrs
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getEntityAttribute
argument_list|(
name|name
argument_list|)
else|:
name|entityAttrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getResolvedEntityAttribute
specifier|public
name|String
name|getResolvedEntityAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entityAttrs
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|name
argument_list|)
else|:
name|delegate
operator|.
name|getVariableResolver
argument_list|()
operator|.
name|replaceTokens
argument_list|(
name|entityAttrs
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getAllEntityFields
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getAllEntityFields
parameter_list|()
block|{
return|return
name|entityFields
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getAllEntityFields
argument_list|()
else|:
name|entityFields
return|;
block|}
DECL|method|getVariableResolver
specifier|public
name|VariableResolver
name|getVariableResolver
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getVariableResolver
argument_list|()
return|;
block|}
DECL|method|getDataSource
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getDataSource
argument_list|()
return|;
block|}
DECL|method|isRootEntity
specifier|public
name|boolean
name|isRootEntity
parameter_list|()
block|{
return|return
name|root
return|;
block|}
DECL|method|currentProcess
specifier|public
name|String
name|currentProcess
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|currentProcess
argument_list|()
return|;
block|}
DECL|method|getRequestParameters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRequestParameters
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getRequestParameters
argument_list|()
return|;
block|}
DECL|method|getEntityProcessor
specifier|public
name|EntityProcessor
name|getEntityProcessor
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|setSessionAttribute
specifier|public
name|void
name|setSessionAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|delegate
operator|.
name|setSessionAttribute
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
DECL|method|getSessionAttribute
specifier|public
name|Object
name|getSessionAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getSessionAttribute
argument_list|(
name|name
argument_list|,
name|scope
argument_list|)
return|;
block|}
DECL|method|getParentContext
specifier|public
name|Context
name|getParentContext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getParentContext
argument_list|()
return|;
block|}
DECL|method|getDataSource
specifier|public
name|DataSource
name|getDataSource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getDataSource
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getSolrCore
specifier|public
name|SolrCore
name|getSolrCore
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSolrCore
argument_list|()
return|;
block|}
DECL|method|getStats
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getStats
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getStats
argument_list|()
return|;
block|}
DECL|method|getScript
specifier|public
name|String
name|getScript
parameter_list|()
block|{
return|return
name|script
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getScript
argument_list|()
else|:
name|script
return|;
block|}
DECL|method|getScriptLanguage
specifier|public
name|String
name|getScriptLanguage
parameter_list|()
block|{
return|return
name|scriptlang
operator|==
literal|null
condition|?
name|delegate
operator|.
name|getScriptLanguage
argument_list|()
else|:
name|scriptlang
return|;
block|}
DECL|method|deleteDoc
specifier|public
name|void
name|deleteDoc
parameter_list|(
name|String
name|id
parameter_list|)
block|{      }
DECL|method|deleteDocByQuery
specifier|public
name|void
name|deleteDocByQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{      }
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|String
name|var
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|resolve
argument_list|(
name|var
argument_list|)
return|;
block|}
DECL|method|replaceTokens
specifier|public
name|String
name|replaceTokens
parameter_list|(
name|String
name|template
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|replaceTokens
argument_list|(
name|template
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

