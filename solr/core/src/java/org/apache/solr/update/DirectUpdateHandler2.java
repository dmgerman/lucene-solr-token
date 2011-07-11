begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  */
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
name|IndexReader
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
name|IndexWriter
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
name|Term
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
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|MatchAllDocsQuery
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
name|TermQuery
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
name|BooleanClause
operator|.
name|Occur
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
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|net
operator|.
name|URL
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
name|QParser
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
name|SolrConfig
operator|.
name|UpdateHandlerInfo
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

begin_comment
comment|/**  *  TODO: add soft commitWithin support  *   *<code>DirectUpdateHandler2</code> implements an UpdateHandler where documents are added  * directly to the main Lucene index as opposed to adding to a separate smaller index.  */
end_comment

begin_class
DECL|class|DirectUpdateHandler2
specifier|public
class|class
name|DirectUpdateHandler2
extends|extends
name|UpdateHandler
block|{
DECL|field|indexWriterProvider
specifier|protected
name|IndexWriterProvider
name|indexWriterProvider
decl_stmt|;
comment|// stats
DECL|field|addCommands
name|AtomicLong
name|addCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|addCommandsCumulative
name|AtomicLong
name|addCommandsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByIdCommands
name|AtomicLong
name|deleteByIdCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByIdCommandsCumulative
name|AtomicLong
name|deleteByIdCommandsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByQueryCommands
name|AtomicLong
name|deleteByQueryCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByQueryCommandsCumulative
name|AtomicLong
name|deleteByQueryCommandsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|expungeDeleteCommands
name|AtomicLong
name|expungeDeleteCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|mergeIndexesCommands
name|AtomicLong
name|mergeIndexesCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|commitCommands
name|AtomicLong
name|commitCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|optimizeCommands
name|AtomicLong
name|optimizeCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|rollbackCommands
name|AtomicLong
name|rollbackCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numDocsPending
name|AtomicLong
name|numDocsPending
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numErrors
name|AtomicLong
name|numErrors
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numErrorsCumulative
name|AtomicLong
name|numErrorsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// tracks when auto-commit should occur
DECL|field|commitTracker
specifier|protected
specifier|final
name|CommitTracker
name|commitTracker
decl_stmt|;
DECL|field|softCommitTracker
specifier|protected
specifier|final
name|CommitTracker
name|softCommitTracker
decl_stmt|;
DECL|method|DirectUpdateHandler2
specifier|public
name|DirectUpdateHandler2
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|indexWriterProvider
operator|=
operator|new
name|DefaultIndexWriterProvider
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|UpdateHandlerInfo
name|updateHandlerInfo
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getUpdateHandlerInfo
argument_list|()
decl_stmt|;
name|int
name|docsUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoCommmitMaxDocs
decl_stmt|;
comment|// getInt("updateHandler/autoCommit/maxDocs", -1);
name|int
name|timeUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoCommmitMaxTime
decl_stmt|;
comment|// getInt("updateHandler/autoCommit/maxTime", -1);
name|commitTracker
operator|=
operator|new
name|CommitTracker
argument_list|(
name|core
argument_list|,
name|docsUpperBound
argument_list|,
name|timeUpperBound
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|softCommitDocsUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoSoftCommmitMaxDocs
decl_stmt|;
comment|// getInt("updateHandler/autoSoftCommit/maxDocs", -1);
name|int
name|softCommitTimeUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoSoftCommmitMaxTime
decl_stmt|;
comment|// getInt("updateHandler/autoSoftCommit/maxTime", -1);
name|softCommitTracker
operator|=
operator|new
name|CommitTracker
argument_list|(
name|core
argument_list|,
name|softCommitDocsUpperBound
argument_list|,
name|softCommitTimeUpperBound
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|DirectUpdateHandler2
specifier|public
name|DirectUpdateHandler2
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|UpdateHandler
name|updateHandler
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateHandler
operator|instanceof
name|DirectUpdateHandler2
condition|)
block|{
name|this
operator|.
name|indexWriterProvider
operator|=
operator|(
operator|(
name|DirectUpdateHandler2
operator|)
name|updateHandler
operator|)
operator|.
name|indexWriterProvider
expr_stmt|;
block|}
else|else
block|{
comment|// the impl has changed, so we cannot use the old state - decref it
name|updateHandler
operator|.
name|decref
argument_list|()
expr_stmt|;
name|indexWriterProvider
operator|=
operator|new
name|DefaultIndexWriterProvider
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
name|UpdateHandlerInfo
name|updateHandlerInfo
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getUpdateHandlerInfo
argument_list|()
decl_stmt|;
name|int
name|docsUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoCommmitMaxDocs
decl_stmt|;
comment|// getInt("updateHandler/autoCommit/maxDocs", -1);
name|int
name|timeUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoCommmitMaxTime
decl_stmt|;
comment|// getInt("updateHandler/autoCommit/maxTime", -1);
name|commitTracker
operator|=
operator|new
name|CommitTracker
argument_list|(
name|core
argument_list|,
name|docsUpperBound
argument_list|,
name|timeUpperBound
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|softCommitDocsUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoSoftCommmitMaxDocs
decl_stmt|;
comment|// getInt("updateHandler/autoSoftCommit/maxDocs", -1);
name|int
name|softCommitTimeUpperBound
init|=
name|updateHandlerInfo
operator|.
name|autoSoftCommmitMaxTime
decl_stmt|;
comment|// getInt("updateHandler/autoSoftCommit/maxTime", -1);
name|softCommitTracker
operator|=
operator|new
name|CommitTracker
argument_list|(
name|core
argument_list|,
name|softCommitDocsUpperBound
argument_list|,
name|softCommitTimeUpperBound
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteAll
specifier|private
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrCore
operator|.
name|log
operator|.
name|info
argument_list|(
name|core
operator|.
name|getLogId
argument_list|()
operator|+
literal|"REMOVING ALL DOCUMENTS FROM INDEX"
argument_list|)
expr_stmt|;
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|rollbackWriter
specifier|protected
name|void
name|rollbackWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|numDocsPending
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|indexWriterProvider
operator|.
name|rollbackIndexWriter
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addDoc
specifier|public
name|int
name|addDoc
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
name|addCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|addCommandsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|int
name|rc
init|=
operator|-
literal|1
decl_stmt|;
comment|// if there is no ID field, don't overwrite
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|overwrite
operator|=
literal|false
expr_stmt|;
block|}
try|try
block|{
name|boolean
name|triggered
init|=
name|commitTracker
operator|.
name|addedDocument
argument_list|(
name|cmd
operator|.
name|commitWithin
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|triggered
condition|)
block|{
comment|// if we hard commit, don't soft commit
name|softCommitTracker
operator|.
name|addedDocument
argument_list|(
name|cmd
operator|.
name|commitWithin
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// still inc softCommit
name|softCommitTracker
operator|.
name|docsSinceCommit
operator|++
expr_stmt|;
block|}
comment|// this is the only unsynchronized code in the iwAccess block, which
comment|// should account for most of the time
name|Term
name|updateTerm
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|overwrite
condition|)
block|{
if|if
condition|(
name|cmd
operator|.
name|indexedId
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|indexedId
operator|=
name|getIndexedId
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
name|Term
name|idTerm
init|=
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
name|cmd
operator|.
name|indexedId
argument_list|)
decl_stmt|;
name|boolean
name|del
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|updateTerm
operator|==
literal|null
condition|)
block|{
name|updateTerm
operator|=
name|idTerm
expr_stmt|;
block|}
else|else
block|{
name|del
operator|=
literal|true
expr_stmt|;
name|updateTerm
operator|=
name|cmd
operator|.
name|updateTerm
expr_stmt|;
block|}
name|writer
operator|.
name|updateDocument
argument_list|(
name|updateTerm
argument_list|,
name|cmd
operator|.
name|getLuceneDocument
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|del
condition|)
block|{
comment|// ensure id remains unique
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|updateTerm
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|idTerm
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|bq
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// allow duplicates
name|writer
operator|.
name|addDocument
argument_list|(
name|cmd
operator|.
name|getLuceneDocument
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rc
operator|=
literal|1
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|rc
operator|!=
literal|1
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numDocsPending
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
comment|// could return the number of docs deleted, but is that always possible to know???
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteByIdCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|deleteByIdCommandsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
name|idFieldType
operator|.
name|toInternal
argument_list|(
name|cmd
operator|.
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|commitTracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|commitTracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|softCommitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|softCommitTracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|softCommitTracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
block|}
comment|// why not return number of docs deleted?
comment|// Depending on implementation, we may not be able to immediately determine the num...
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteByQueryCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|deleteByQueryCommandsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|boolean
name|madeIt
init|=
literal|false
decl_stmt|;
name|boolean
name|delAll
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Query
name|q
init|=
literal|null
decl_stmt|;
try|try
block|{
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|cmd
operator|.
name|query
argument_list|,
literal|"lucene"
argument_list|,
name|cmd
operator|.
name|req
argument_list|)
decl_stmt|;
name|q
operator|=
name|parser
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
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
argument_list|)
throw|;
block|}
name|delAll
operator|=
name|MatchAllDocsQuery
operator|.
name|class
operator|==
name|q
operator|.
name|getClass
argument_list|()
expr_stmt|;
if|if
condition|(
name|delAll
condition|)
block|{
name|deleteAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
name|madeIt
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|commitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|commitTracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|commitTracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|softCommitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|softCommitTracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|softCommitTracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|madeIt
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|mergeIndexes
specifier|public
name|int
name|mergeIndexes
parameter_list|(
name|MergeIndexesCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|mergeIndexesCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|int
name|rc
init|=
operator|-
literal|1
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"start "
operator|+
name|cmd
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers
init|=
name|cmd
operator|.
name|readers
decl_stmt|;
if|if
condition|(
name|readers
operator|!=
literal|null
operator|&&
name|readers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|rc
operator|=
literal|0
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"end_mergeIndexes"
argument_list|)
expr_stmt|;
comment|// TODO: consider soft commit issues
if|if
condition|(
name|rc
operator|==
literal|1
operator|&&
name|commitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|commitTracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|commitTracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|==
literal|1
operator|&&
name|softCommitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|softCommitTracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|softCommitTracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|optimizeCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|commitCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|expungeDeletes
condition|)
name|expungeDeleteCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|Future
index|[]
name|waitSearcher
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|waitSearcher
condition|)
block|{
name|waitSearcher
operator|=
operator|new
name|Future
index|[
literal|1
index|]
expr_stmt|;
block|}
name|boolean
name|error
init|=
literal|true
decl_stmt|;
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"start "
operator|+
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|writer
operator|.
name|optimize
argument_list|(
name|cmd
operator|.
name|maxOptimizeSegments
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|expungeDeletes
condition|)
block|{
name|writer
operator|.
name|expungeDeletes
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|softCommit
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|callPostCommitCallbacks
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|callPostSoftCommitCallbacks
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|callPostOptimizeCallbacks
argument_list|()
expr_stmt|;
block|}
comment|// open a new searcher in the sync block to avoid opening it
comment|// after a deleteByQuery changed the index, or in between deletes
comment|// and adds of another commit being done.
if|if
condition|(
name|cmd
operator|.
name|softCommit
condition|)
block|{
name|core
operator|.
name|getSearcher
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|waitSearcher
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|core
operator|.
name|getSearcher
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|waitSearcher
argument_list|)
expr_stmt|;
block|}
comment|// reset commit tracking
if|if
condition|(
name|cmd
operator|.
name|softCommit
condition|)
block|{
name|softCommitTracker
operator|.
name|didCommit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|commitTracker
operator|.
name|didCommit
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"end_commit_flush"
argument_list|)
expr_stmt|;
name|error
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|addCommands
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|deleteByIdCommands
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|deleteByQueryCommands
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numErrors
operator|.
name|set
argument_list|(
name|error
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// if we are supposed to wait for the searcher to be registered, then we should do it
comment|// outside of the synchronized block so that other update operations can proceed.
if|if
condition|(
name|waitSearcher
operator|!=
literal|null
operator|&&
name|waitSearcher
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|waitSearcher
index|[
literal|0
index|]
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|reopenSearcher
specifier|public
name|SolrIndexSearcher
name|reopenSearcher
parameter_list|(
name|SolrIndexSearcher
name|previousSearcher
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|currentReader
init|=
name|previousSearcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|IndexReader
name|newReader
decl_stmt|;
name|newReader
operator|=
name|currentReader
operator|.
name|reopen
argument_list|(
name|indexWriterProvider
operator|.
name|getIndexWriter
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|newReader
operator|==
name|currentReader
condition|)
block|{
name|currentReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SolrIndexSearcher
argument_list|(
name|core
argument_list|,
name|schema
argument_list|,
literal|"main"
argument_list|,
name|newReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newIndexWriter
specifier|public
name|void
name|newIndexWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|indexWriterProvider
operator|.
name|newIndexWriter
argument_list|()
expr_stmt|;
block|}
comment|/**    * @since Solr 1.4    */
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|rollbackCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|boolean
name|error
init|=
literal|true
decl_stmt|;
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"start "
operator|+
name|cmd
argument_list|)
expr_stmt|;
name|rollbackWriter
argument_list|()
expr_stmt|;
comment|//callPostRollbackCallbacks();
comment|// reset commit tracking
name|commitTracker
operator|.
name|didRollback
argument_list|()
expr_stmt|;
name|softCommitTracker
operator|.
name|didRollback
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"end_rollback"
argument_list|)
expr_stmt|;
name|error
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|addCommandsCumulative
operator|.
name|set
argument_list|(
name|addCommandsCumulative
operator|.
name|get
argument_list|()
operator|-
name|addCommands
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|deleteByIdCommandsCumulative
operator|.
name|set
argument_list|(
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
operator|-
name|deleteByIdCommands
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|deleteByQueryCommandsCumulative
operator|.
name|set
argument_list|(
name|deleteByQueryCommandsCumulative
operator|.
name|get
argument_list|()
operator|-
name|deleteByQueryCommands
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|numErrors
operator|.
name|set
argument_list|(
name|error
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"closing "
operator|+
name|this
argument_list|)
expr_stmt|;
name|commitTracker
operator|.
name|close
argument_list|()
expr_stmt|;
name|softCommitTracker
operator|.
name|close
argument_list|()
expr_stmt|;
name|numDocsPending
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|indexWriterProvider
operator|.
name|decref
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"closed "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
comment|/////////////////////////////////////////////////////////////////////
comment|// SolrInfoMBean stuff: Statistics and Module Info
comment|/////////////////////////////////////////////////////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|DirectUpdateHandler2
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Update handler that efficiently directly updates the on-disk main lucene index"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|UPDATEHANDLER
return|;
block|}
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
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"commits"
argument_list|,
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitTracker
operator|.
name|docsUpperBound
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"autocommit maxDocs"
argument_list|,
name|commitTracker
operator|.
name|docsUpperBound
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"autocommit maxTime"
argument_list|,
literal|""
operator|+
name|commitTracker
operator|.
name|timeUpperBound
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
name|lst
operator|.
name|add
argument_list|(
literal|"autocommits"
argument_list|,
name|commitTracker
operator|.
name|autoCommitCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|softCommitTracker
operator|.
name|docsUpperBound
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"soft autocommit maxDocs"
argument_list|,
name|softCommitTracker
operator|.
name|docsUpperBound
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|softCommitTracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"soft autocommit maxTime"
argument_list|,
literal|""
operator|+
name|softCommitTracker
operator|.
name|timeUpperBound
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
name|lst
operator|.
name|add
argument_list|(
literal|"soft autocommits"
argument_list|,
name|softCommitTracker
operator|.
name|autoCommitCount
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"optimizes"
argument_list|,
name|optimizeCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"rollbacks"
argument_list|,
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"expungeDeletes"
argument_list|,
name|expungeDeleteCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"docsPending"
argument_list|,
name|numDocsPending
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// pset.size() not synchronized, but it should be fine to access.
comment|// lst.add("deletesPending", pset.size());
name|lst
operator|.
name|add
argument_list|(
literal|"adds"
argument_list|,
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"deletesById"
argument_list|,
name|deleteByIdCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"deletesByQuery"
argument_list|,
name|deleteByQueryCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_adds"
argument_list|,
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_deletesById"
argument_list|,
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_deletesByQuery"
argument_list|,
name|deleteByQueryCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_errors"
argument_list|,
name|numErrorsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DirectUpdateHandler2"
operator|+
name|getStatistics
argument_list|()
return|;
block|}
DECL|method|getIndexWriterProvider
specifier|public
name|IndexWriterProvider
name|getIndexWriterProvider
parameter_list|()
block|{
return|return
name|indexWriterProvider
return|;
block|}
annotation|@
name|Override
DECL|method|decref
specifier|public
name|void
name|decref
parameter_list|()
block|{
try|try
block|{
name|indexWriterProvider
operator|.
name|decref
argument_list|()
expr_stmt|;
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
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|incref
specifier|public
name|void
name|incref
parameter_list|()
block|{
name|indexWriterProvider
operator|.
name|incref
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

