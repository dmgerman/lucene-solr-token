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
name|handler
operator|.
name|component
operator|.
name|RealTimeGetComponent
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
name|UpdateCommand
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
name|RefCounted
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
name|SERVER_ERROR
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
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import

begin_comment
comment|/**  *<p>  *     This Factory generates an UpdateProcessor that will (by default) skip inserting new documents  *     if there already exists a document with the same uniqueKey value in the index. It will also  *     skip Atomic Updates to a document if that document does not already exist. This behaviour is applied  *     to each document in turn, so adding a batch of documents can result in some being added and some  *     ignored, depending on what is already in the index. If all of the documents are skipped, no changes  *     to the index will occur.  *</p>  * These two forms of skipping can be switched on or off independently, by using init params:  *<ul>  *<li><code>skipInsertIfExists</code> - This boolean parameter defaults to  *<code>true</code>, but if set to<code>false</code> then inserts (i.e. not Atomic Updates)  *          will be passed through unchanged even if the document already exists.</li>  *<li><code>skipUpdateIfMissing</code> - This boolean parameter defaults to  *<code>true</code>, but if set to<code>false</code> then Atomic Updates  *          will be passed through unchanged regardless of whether the document exists.</li>  *</ul>  *<p>  *     These params can also be specified per-request, to override the configured behaviour  *     for specific updates e.g.<code>/update?skipUpdateIfMissing=true</code>  *</p>  *<p>  *     This implementation is a simpler alternative to {@link DocBasedVersionConstraintsProcessorFactory}  *     when you are not concerned with versioning, and just want to quietly ignore duplicate documents and/or  *     silently skip updates to non-existent documents (in the same way a database<code>UPDATE</code> would).  *  *     If your documents do have an explicit version field, and you want to ensure older versions are  *     skipped instead of replacing the indexed document, you should consider {@link DocBasedVersionConstraintsProcessorFactory}  *     instead.  *</p>  *<p>  *     An example chain configuration to use this for skipping duplicate inserts, but not skipping updates to  *     missing documents by default, is:  *</p>  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain name="skipexisting"&gt;  *&lt;processor class="solr.LogUpdateProcessorFactory" /&gt;  *&lt;processor class="solr.SkipExistingDocumentsProcessorFactory"&gt;  *&lt;bool name="skipInsertIfExists"&gt;true&lt;/bool&gt;  *&lt;bool name="skipUpdateIfMissing"&gt;false&lt;/bool&gt;&lt;!-- Can override this per-request --&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.DistributedUpdateProcessorFactory" /&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  */
end_comment

begin_class
DECL|class|SkipExistingDocumentsProcessorFactory
specifier|public
class|class
name|SkipExistingDocumentsProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
implements|,
name|UpdateRequestProcessorFactory
operator|.
name|RunAlways
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
DECL|field|PARAM_SKIP_INSERT_IF_EXISTS
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_SKIP_INSERT_IF_EXISTS
init|=
literal|"skipInsertIfExists"
decl_stmt|;
DECL|field|PARAM_SKIP_UPDATE_IF_MISSING
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_SKIP_UPDATE_IF_MISSING
init|=
literal|"skipUpdateIfMissing"
decl_stmt|;
DECL|field|skipInsertIfExists
specifier|private
name|boolean
name|skipInsertIfExists
init|=
literal|true
decl_stmt|;
DECL|field|skipUpdateIfMissing
specifier|private
name|boolean
name|skipUpdateIfMissing
init|=
literal|true
decl_stmt|;
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
name|tmp
init|=
name|args
operator|.
name|remove
argument_list|(
name|PARAM_SKIP_INSERT_IF_EXISTS
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|tmp
operator|instanceof
name|Boolean
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'"
operator|+
name|PARAM_SKIP_INSERT_IF_EXISTS
operator|+
literal|"' must be configured as a<bool>"
argument_list|)
throw|;
block|}
name|skipInsertIfExists
operator|=
operator|(
name|Boolean
operator|)
name|tmp
expr_stmt|;
block|}
name|tmp
operator|=
name|args
operator|.
name|remove
argument_list|(
name|PARAM_SKIP_UPDATE_IF_MISSING
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|tmp
operator|instanceof
name|Boolean
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'"
operator|+
name|PARAM_SKIP_UPDATE_IF_MISSING
operator|+
literal|"' must be configured as a<bool>"
argument_list|)
throw|;
block|}
name|skipUpdateIfMissing
operator|=
operator|(
name|Boolean
operator|)
name|tmp
expr_stmt|;
block|}
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|SkipExistingDocumentsUpdateProcessor
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
comment|// Ensure the parameters are forwarded to the leader
name|DistributedUpdateProcessorFactory
operator|.
name|addParamToDistributedRequestWhitelist
argument_list|(
name|req
argument_list|,
name|PARAM_SKIP_INSERT_IF_EXISTS
argument_list|,
name|PARAM_SKIP_UPDATE_IF_MISSING
argument_list|)
expr_stmt|;
comment|// Allow the particular request to override the plugin's configured behaviour
name|boolean
name|skipInsertForRequest
init|=
name|req
operator|.
name|getOriginalParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|PARAM_SKIP_INSERT_IF_EXISTS
argument_list|,
name|this
operator|.
name|skipInsertIfExists
argument_list|)
decl_stmt|;
name|boolean
name|skipUpdateForRequest
init|=
name|req
operator|.
name|getOriginalParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|PARAM_SKIP_UPDATE_IF_MISSING
argument_list|,
name|this
operator|.
name|skipUpdateIfMissing
argument_list|)
decl_stmt|;
return|return
operator|new
name|SkipExistingDocumentsUpdateProcessor
argument_list|(
name|req
argument_list|,
name|next
argument_list|,
name|skipInsertForRequest
argument_list|,
name|skipUpdateForRequest
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"updateLog must be enabled."
argument_list|)
throw|;
block|}
if|if
condition|(
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"schema must have uniqueKey defined."
argument_list|)
throw|;
block|}
block|}
DECL|class|SkipExistingDocumentsUpdateProcessor
specifier|static
class|class
name|SkipExistingDocumentsUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|skipInsertIfExists
specifier|private
specifier|final
name|boolean
name|skipInsertIfExists
decl_stmt|;
DECL|field|skipUpdateIfMissing
specifier|private
specifier|final
name|boolean
name|skipUpdateIfMissing
decl_stmt|;
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|distribProc
specifier|private
name|DistributedUpdateProcessor
name|distribProc
decl_stmt|;
comment|// the distributed update processor following us
DECL|field|phase
specifier|private
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
name|phase
decl_stmt|;
DECL|method|SkipExistingDocumentsUpdateProcessor
name|SkipExistingDocumentsUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|,
name|boolean
name|skipInsertIfExists
parameter_list|,
name|boolean
name|skipUpdateIfMissing
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|skipInsertIfExists
operator|=
name|skipInsertIfExists
expr_stmt|;
name|this
operator|.
name|skipUpdateIfMissing
operator|=
name|skipUpdateIfMissing
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|req
operator|.
name|getCore
argument_list|()
expr_stmt|;
for|for
control|(
name|UpdateRequestProcessor
name|proc
init|=
name|next
init|;
name|proc
operator|!=
literal|null
condition|;
name|proc
operator|=
name|proc
operator|.
name|next
control|)
block|{
if|if
condition|(
name|proc
operator|instanceof
name|DistributedUpdateProcessor
condition|)
block|{
name|distribProc
operator|=
operator|(
name|DistributedUpdateProcessor
operator|)
name|proc
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|distribProc
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
name|SERVER_ERROR
argument_list|,
literal|"DistributedUpdateProcessor must follow SkipExistingDocumentsUpdateProcessor"
argument_list|)
throw|;
block|}
name|phase
operator|=
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
operator|.
name|parseParam
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isSkipInsertIfExists
name|boolean
name|isSkipInsertIfExists
parameter_list|()
block|{
return|return
name|this
operator|.
name|skipInsertIfExists
return|;
block|}
DECL|method|isSkipUpdateIfMissing
name|boolean
name|isSkipUpdateIfMissing
parameter_list|()
block|{
return|return
name|this
operator|.
name|skipUpdateIfMissing
return|;
block|}
DECL|method|doesDocumentExist
name|boolean
name|doesDocumentExist
parameter_list|(
name|BytesRef
name|indexedDocId
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
literal|null
operator|!=
name|indexedDocId
assert|;
comment|// we don't need any fields populated, we just need to know if the doc is in the tlog...
name|SolrInputDocument
name|oldDoc
init|=
name|RealTimeGetComponent
operator|.
name|getInputDocumentFromTlog
argument_list|(
name|core
argument_list|,
name|indexedDocId
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldDoc
operator|==
name|RealTimeGetComponent
operator|.
name|DELETED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|oldDoc
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// need to look up in index now...
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|newestSearcher
init|=
name|core
operator|.
name|getRealtimeSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|newestSearcher
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|searcher
operator|.
name|lookupId
argument_list|(
name|indexedDocId
argument_list|)
operator|>=
literal|0L
return|;
block|}
catch|catch
parameter_list|(
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
name|SERVER_ERROR
argument_list|,
literal|"Error reading document from index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|newestSearcher
operator|!=
literal|null
condition|)
block|{
name|newestSearcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|isLeader
name|boolean
name|isLeader
parameter_list|(
name|UpdateCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
operator|(
name|cmd
operator|.
name|getFlags
argument_list|()
operator|&
operator|(
name|UpdateCommand
operator|.
name|REPLAY
operator||
name|UpdateCommand
operator|.
name|PEER_SYNC
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|phase
operator|==
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
operator|.
name|FROMLEADER
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|distribProc
operator|.
name|isLeader
argument_list|(
name|cmd
argument_list|)
return|;
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
name|BytesRef
name|indexedDocId
init|=
name|cmd
operator|.
name|getIndexedId
argument_list|()
decl_stmt|;
name|boolean
name|isUpdate
init|=
name|AtomicUpdateDocumentMerger
operator|.
name|isAtomicUpdate
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
comment|// boolean existsByLookup = (RealTimeGetComponent.getInputDocument(core, indexedDocId) != null);
comment|// if (docExists != existsByLookup) {
comment|//   log.error("Found docExists {} but existsByLookup {} for doc {}", docExists, existsByLookup, indexedDocId.utf8ToString());
comment|// }
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
literal|"Document ID {} ... exists already? {} ... isAtomicUpdate? {} ... isLeader? {}"
argument_list|,
name|indexedDocId
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|doesDocumentExist
argument_list|(
name|indexedDocId
argument_list|)
argument_list|,
name|isUpdate
argument_list|,
name|isLeader
argument_list|(
name|cmd
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|skipInsertIfExists
operator|&&
operator|!
name|isUpdate
operator|&&
name|isLeader
argument_list|(
name|cmd
argument_list|)
operator|&&
name|doesDocumentExist
argument_list|(
name|indexedDocId
argument_list|)
condition|)
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
literal|"Skipping insert for pre-existing document ID {}"
argument_list|,
name|indexedDocId
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|skipUpdateIfMissing
operator|&&
name|isUpdate
operator|&&
name|isLeader
argument_list|(
name|cmd
argument_list|)
operator|&&
operator|!
name|doesDocumentExist
argument_list|(
name|indexedDocId
argument_list|)
condition|)
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
literal|"Skipping update to non-existent document ID {}"
argument_list|,
name|indexedDocId
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
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
literal|"Passing on document ID {}"
argument_list|,
name|indexedDocId
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

