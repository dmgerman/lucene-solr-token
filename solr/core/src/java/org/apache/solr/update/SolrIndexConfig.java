begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|index
operator|.
name|*
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
name|util
operator|.
name|Version
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
name|core
operator|.
name|SolrConfig
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
name|PluginInfo
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
comment|/**  * This config object encapsulates IndexWriter config params,  * defined in the&lt;indexConfig&gt; section of solrconfig.xml  */
end_comment

begin_class
DECL|class|SolrIndexConfig
specifier|public
class|class
name|SolrIndexConfig
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrIndexConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|defaultMergePolicyClassName
specifier|final
name|String
name|defaultMergePolicyClassName
decl_stmt|;
DECL|field|DEFAULT_MERGE_SCHEDULER_CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
init|=
name|ConcurrentMergeScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|luceneVersion
specifier|public
specifier|final
name|Version
name|luceneVersion
decl_stmt|;
DECL|field|useCompoundFile
specifier|public
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|public
specifier|final
name|int
name|maxBufferedDocs
decl_stmt|;
DECL|field|maxMergeDocs
specifier|public
specifier|final
name|int
name|maxMergeDocs
decl_stmt|;
DECL|field|mergeFactor
specifier|public
specifier|final
name|int
name|mergeFactor
decl_stmt|;
DECL|field|ramBufferSizeMB
specifier|public
specifier|final
name|double
name|ramBufferSizeMB
decl_stmt|;
DECL|field|writeLockTimeout
specifier|public
specifier|final
name|int
name|writeLockTimeout
decl_stmt|;
DECL|field|lockType
specifier|public
specifier|final
name|String
name|lockType
decl_stmt|;
DECL|field|mergePolicyInfo
specifier|public
specifier|final
name|PluginInfo
name|mergePolicyInfo
decl_stmt|;
DECL|field|mergeSchedulerInfo
specifier|public
specifier|final
name|PluginInfo
name|mergeSchedulerInfo
decl_stmt|;
DECL|field|termIndexInterval
specifier|public
specifier|final
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|infoStreamFile
specifier|public
name|String
name|infoStreamFile
init|=
literal|null
decl_stmt|;
comment|// Available lock types
DECL|field|LOCK_TYPE_SIMPLE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_SIMPLE
init|=
literal|"simple"
decl_stmt|;
DECL|field|LOCK_TYPE_NATIVE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_NATIVE
init|=
literal|"native"
decl_stmt|;
DECL|field|LOCK_TYPE_SINGLE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_SINGLE
init|=
literal|"single"
decl_stmt|;
DECL|field|LOCK_TYPE_NONE
specifier|public
specifier|final
specifier|static
name|String
name|LOCK_TYPE_NONE
init|=
literal|"none"
decl_stmt|;
comment|/**    * Internal constructor for setting defaults based on Lucene Version    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|SolrIndexConfig
specifier|private
name|SolrIndexConfig
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|)
block|{
name|luceneVersion
operator|=
name|solrConfig
operator|.
name|luceneMatchVersion
expr_stmt|;
name|useCompoundFile
operator|=
literal|false
expr_stmt|;
name|maxBufferedDocs
operator|=
operator|-
literal|1
expr_stmt|;
name|maxMergeDocs
operator|=
operator|-
literal|1
expr_stmt|;
name|mergeFactor
operator|=
operator|-
literal|1
expr_stmt|;
name|ramBufferSizeMB
operator|=
literal|32
expr_stmt|;
name|writeLockTimeout
operator|=
operator|-
literal|1
expr_stmt|;
name|lockType
operator|=
name|LOCK_TYPE_NATIVE
expr_stmt|;
name|termIndexInterval
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
expr_stmt|;
name|mergePolicyInfo
operator|=
literal|null
expr_stmt|;
name|mergeSchedulerInfo
operator|=
literal|null
expr_stmt|;
name|defaultMergePolicyClassName
operator|=
name|TieredMergePolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a SolrIndexConfig which parses the Lucene related config params in solrconfig.xml    * @param solrConfig the overall SolrConfig object    * @param prefix the XPath prefix for which section to parse (mandatory)    * @param def a SolrIndexConfig instance to pick default values from (optional)    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|SolrIndexConfig
specifier|public
name|SolrIndexConfig
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|prefix
parameter_list|,
name|SolrIndexConfig
name|def
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
literal|"indexConfig"
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Defaulting to prefix \""
operator|+
name|prefix
operator|+
literal|"\" for index configuration"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
name|def
operator|=
operator|new
name|SolrIndexConfig
argument_list|(
name|solrConfig
argument_list|)
expr_stmt|;
block|}
name|luceneVersion
operator|=
name|solrConfig
operator|.
name|luceneMatchVersion
expr_stmt|;
comment|// Assert that end-of-life parameters or syntax is not in our config.
comment|// Warn for luceneMatchVersion's before LUCENE_36, fail fast above
name|assertWarnOrFail
argument_list|(
literal|"The<mergeScheduler>myclass</mergeScheduler> syntax is no longer supported in solrconfig.xml. Please use syntax<mergeScheduler class=\"myclass\"/> instead."
argument_list|,
operator|!
operator|(
operator|(
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler/text()"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler/@class"
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
operator|)
operator|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"The<mergePolicy>myclass</mergePolicy> syntax is no longer supported in solrconfig.xml. Please use syntax<mergePolicy class=\"myclass\"/> instead."
argument_list|,
operator|!
operator|(
operator|(
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy/text()"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy/@class"
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
operator|)
operator|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertWarnOrFail
argument_list|(
literal|"The<luceneAutoCommit>true|false</luceneAutoCommit> parameter is no longer valid in solrconfig.xml."
argument_list|,
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/luceneAutoCommit"
argument_list|,
literal|null
argument_list|)
operator|==
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|defaultMergePolicyClassName
operator|=
name|def
operator|.
name|defaultMergePolicyClassName
expr_stmt|;
name|useCompoundFile
operator|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/useCompoundFile"
argument_list|,
name|def
operator|.
name|useCompoundFile
argument_list|)
expr_stmt|;
name|maxBufferedDocs
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxBufferedDocs"
argument_list|,
name|def
operator|.
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|maxMergeDocs
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxMergeDocs"
argument_list|,
name|def
operator|.
name|maxMergeDocs
argument_list|)
expr_stmt|;
name|mergeFactor
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/mergeFactor"
argument_list|,
name|def
operator|.
name|mergeFactor
argument_list|)
expr_stmt|;
name|ramBufferSizeMB
operator|=
name|solrConfig
operator|.
name|getDouble
argument_list|(
name|prefix
operator|+
literal|"/ramBufferSizeMB"
argument_list|,
name|def
operator|.
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
name|writeLockTimeout
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/writeLockTimeout"
argument_list|,
name|def
operator|.
name|writeLockTimeout
argument_list|)
expr_stmt|;
name|lockType
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/lockType"
argument_list|,
name|def
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|mergeSchedulerInfo
operator|=
name|getPluginInfo
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler"
argument_list|,
name|solrConfig
argument_list|,
name|def
operator|.
name|mergeSchedulerInfo
argument_list|)
expr_stmt|;
name|mergePolicyInfo
operator|=
name|getPluginInfo
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy"
argument_list|,
name|solrConfig
argument_list|,
name|def
operator|.
name|mergePolicyInfo
argument_list|)
expr_stmt|;
name|termIndexInterval
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/termIndexInterval"
argument_list|,
name|def
operator|.
name|termIndexInterval
argument_list|)
expr_stmt|;
name|boolean
name|infoStreamEnabled
init|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/infoStream"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStreamEnabled
condition|)
block|{
name|infoStreamFile
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/infoStream/@file"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"IndexWriter infoStream debug log is enabled: "
operator|+
name|infoStreamFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Assert that assertCondition is true.    * If not, prints reason as log warning.    * If failCondition is true, then throw exception instead of warning     */
DECL|method|assertWarnOrFail
specifier|private
name|void
name|assertWarnOrFail
parameter_list|(
name|String
name|reason
parameter_list|,
name|boolean
name|assertCondition
parameter_list|,
name|boolean
name|failCondition
parameter_list|)
block|{
if|if
condition|(
name|assertCondition
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|failCondition
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
name|reason
argument_list|)
throw|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getPluginInfo
specifier|private
name|PluginInfo
name|getPluginInfo
parameter_list|(
name|String
name|path
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|PluginInfo
name|def
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|l
init|=
name|solrConfig
operator|.
name|readPluginInfos
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|l
operator|.
name|isEmpty
argument_list|()
condition|?
name|def
else|:
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|toIndexWriterConfig
specifier|public
name|IndexWriterConfig
name|toIndexWriterConfig
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|luceneVersion
argument_list|,
name|schema
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxBufferedDocs
operator|!=
operator|-
literal|1
condition|)
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|ramBufferSizeMB
operator|!=
operator|-
literal|1
condition|)
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
if|if
condition|(
name|termIndexInterval
operator|!=
operator|-
literal|1
condition|)
name|iwc
operator|.
name|setTermIndexInterval
argument_list|(
name|termIndexInterval
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeLockTimeout
operator|!=
operator|-
literal|1
condition|)
name|iwc
operator|.
name|setWriteLockTimeout
argument_list|(
name|writeLockTimeout
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setSimilarity
argument_list|(
name|schema
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|buildMergePolicy
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
name|buildMergeScheduler
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|iwc
return|;
block|}
DECL|method|buildMergePolicy
specifier|private
name|MergePolicy
name|buildMergePolicy
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|String
name|mpClassName
init|=
name|mergePolicyInfo
operator|==
literal|null
condition|?
name|defaultMergePolicyClassName
else|:
name|mergePolicyInfo
operator|.
name|className
decl_stmt|;
name|MergePolicy
name|policy
init|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|mpClassName
argument_list|,
name|MergePolicy
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|instanceof
name|LogMergePolicy
condition|)
block|{
name|LogMergePolicy
name|logMergePolicy
init|=
operator|(
name|LogMergePolicy
operator|)
name|policy
decl_stmt|;
if|if
condition|(
name|maxMergeDocs
operator|!=
operator|-
literal|1
condition|)
name|logMergePolicy
operator|.
name|setMaxMergeDocs
argument_list|(
name|maxMergeDocs
argument_list|)
expr_stmt|;
name|logMergePolicy
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeFactor
operator|!=
operator|-
literal|1
condition|)
name|logMergePolicy
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|policy
operator|instanceof
name|TieredMergePolicy
condition|)
block|{
name|TieredMergePolicy
name|tieredMergePolicy
init|=
operator|(
name|TieredMergePolicy
operator|)
name|policy
decl_stmt|;
name|tieredMergePolicy
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeFactor
operator|!=
operator|-
literal|1
condition|)
block|{
name|tieredMergePolicy
operator|.
name|setMaxMergeAtOnce
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
name|tieredMergePolicy
operator|.
name|setSegmentsPerTier
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Use of compound file format or mergefactor cannot be configured if merge policy is not an instance of LogMergePolicy or TieredMergePolicy. The configured policy's defaults will be used."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergePolicyInfo
operator|!=
literal|null
condition|)
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|policy
argument_list|,
name|mergePolicyInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
return|return
name|policy
return|;
block|}
DECL|method|buildMergeScheduler
specifier|private
name|MergeScheduler
name|buildMergeScheduler
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|String
name|msClassName
init|=
name|mergeSchedulerInfo
operator|==
literal|null
condition|?
name|SolrIndexConfig
operator|.
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
else|:
name|mergeSchedulerInfo
operator|.
name|className
decl_stmt|;
name|MergeScheduler
name|scheduler
init|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|msClassName
argument_list|,
name|MergeScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeSchedulerInfo
operator|!=
literal|null
condition|)
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|scheduler
argument_list|,
name|mergeSchedulerInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
return|return
name|scheduler
return|;
block|}
block|}
end_class

end_unit

