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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|ScheduledFuture
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
name|TimeUnit
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|util
operator|.
name|logging
operator|.
name|Level
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
name|search
operator|.
name|QueryParsing
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

begin_comment
comment|/**  *<code>DirectUpdateHandler2</code> implements an UpdateHandler where documents are added  * directly to the main Lucene index as opposed to adding to a separate smaller index.  * For this reason, not all combinations to/from pending and committed are supported.  * This version supports efficient removal of duplicates on a commit.  It works by maintaining  * a related count for every document being added or deleted.  At commit time, for every id with a count,  * all but the last "count" docs with that id are deleted.  *<p>  *  * Supported add command parameters:<TABLE BORDER><TR><TH>allowDups</TH><TH>overwritePending</TH><TH>overwriteCommitted</TH><TH>efficiency</TH></TR><TR><TD>false</TD><TD>false</TD><TD>true</TD><TD>fast</TD></TR><TR><TD>true or false</TD><TD>true</TD><TD>true</TD><TD>fast</TD></TR><TR><TD>true</TD><TD>false</TD><TD>false</TD><TD>fastest</TD></TR></TABLE><p>Supported delete commands:<TABLE BORDER><TR><TH>command</TH><TH>fromPending</TH><TH>fromCommitted</TH><TH>efficiency</TH></TR><TR><TD>delete</TD><TD>true</TD><TD>true</TD><TD>fast</TD></TR><TR><TD>deleteByQuery</TD><TD>true</TD><TD>true</TD><TD>very slow*</TD></TR></TABLE><p>* deleteByQuery causes a commit to happen (close current index writer, open new index reader)   before it can be processed.  If deleteByQuery functionality is needed, it's best if they can   be batched and executed together so they may share the same index reader.   *  * @version $Id$  * @since solr 0.9  */
end_comment

begin_class
DECL|class|DirectUpdateHandler2
specifier|public
class|class
name|DirectUpdateHandler2
extends|extends
name|UpdateHandler
block|{
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
DECL|field|numDocsDeleted
name|AtomicLong
name|numDocsDeleted
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
DECL|field|tracker
specifier|protected
specifier|final
name|CommitTracker
name|tracker
decl_stmt|;
comment|// iwCommit protects internal data and open/close of the IndexWriter and
comment|// is a mutex. Any use of the index writer should be protected by iwAccess,
comment|// which admits multiple simultaneous acquisitions.  iwAccess is
comment|// mutually-exclusive with the iwCommit lock.
DECL|field|iwAccess
DECL|field|iwCommit
specifier|protected
specifier|final
name|Lock
name|iwAccess
decl_stmt|,
name|iwCommit
decl_stmt|;
DECL|field|writer
specifier|protected
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|searcher
specifier|protected
name|SolrIndexSearcher
name|searcher
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
name|ReadWriteLock
name|rwl
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|iwAccess
operator|=
name|rwl
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|iwCommit
operator|=
name|rwl
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|tracker
operator|=
operator|new
name|CommitTracker
argument_list|()
expr_stmt|;
block|}
comment|// must only be called when iwCommit lock held
DECL|method|deleteAll
specifier|private
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|core
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
name|closeWriter
argument_list|()
expr_stmt|;
name|closeSearcher
argument_list|()
expr_stmt|;
name|writer
operator|=
name|createMainIndexWriter
argument_list|(
literal|"DirectUpdateHandler2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// must only be called when iwCommit lock held
DECL|method|openWriter
specifier|protected
name|void
name|openWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
name|createMainIndexWriter
argument_list|(
literal|"DirectUpdateHandler2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|// must only be called when iwCommit lock held
DECL|method|closeWriter
specifier|protected
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|numDocsPending
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// if an exception causes the writelock to not be
comment|// released, we could try and delete it here
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|openSearcher
specifier|protected
name|void
name|openSearcher
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
name|searcher
operator|=
name|core
operator|.
name|newSearcher
argument_list|(
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeSearcher
specifier|protected
name|void
name|closeSearcher
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// if an exception causes a lock to not be
comment|// released, we could try to delete it.
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
block|}
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
comment|// if there is no ID field, use allowDups
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|allowDups
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|overwriteCommitted
operator|=
literal|false
expr_stmt|;
name|cmd
operator|.
name|overwritePending
operator|=
literal|false
expr_stmt|;
block|}
name|iwAccess
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// We can't use iwCommit to protect internal data here, since it would
comment|// block other addDoc calls.  Hence, we synchronize to protect internal
comment|// state.  This is safe as all other state-changing operations are
comment|// protected with iwCommit (which iwAccess excludes from this block).
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// adding document -- prep writer
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|tracker
operator|.
name|addedDocument
argument_list|()
expr_stmt|;
block|}
comment|// end synchronized block
comment|// this is the only unsynchronized code in the iwAccess block, which
comment|// should account for most of the time
if|if
condition|(
name|cmd
operator|.
name|overwriteCommitted
operator|||
name|cmd
operator|.
name|overwritePending
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
name|writer
operator|.
name|updateDocument
argument_list|(
name|idTerm
operator|.
name|createTerm
argument_list|(
name|cmd
operator|.
name|indexedId
argument_list|)
argument_list|,
name|cmd
operator|.
name|getLuceneDocument
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
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
name|iwAccess
operator|.
name|unlock
argument_list|()
expr_stmt|;
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
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|&&
operator|!
name|cmd
operator|.
name|fromCommitted
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
literal|"meaningless command: "
operator|+
name|cmd
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|||
operator|!
name|cmd
operator|.
name|fromCommitted
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
literal|"operation not supported"
operator|+
name|cmd
argument_list|)
throw|;
block|}
name|iwCommit
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|idTerm
operator|.
name|createTerm
argument_list|(
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
block|}
finally|finally
block|{
name|iwCommit
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|tracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|tracker
operator|.
name|timeUpperBound
argument_list|)
expr_stmt|;
block|}
block|}
comment|// why not return number of docs deleted?
comment|// Depending on implementation, we may not be able to immediately determine the num...
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
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|&&
operator|!
name|cmd
operator|.
name|fromCommitted
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
literal|"meaningless command: "
operator|+
name|cmd
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|||
operator|!
name|cmd
operator|.
name|fromCommitted
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
literal|"operation not supported"
operator|+
name|cmd
argument_list|)
throw|;
block|}
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
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|cmd
operator|.
name|query
argument_list|,
name|schema
argument_list|)
decl_stmt|;
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
name|int
name|totDeleted
init|=
literal|0
decl_stmt|;
name|iwCommit
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
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
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|iwCommit
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|delAll
condition|)
block|{
if|if
condition|(
name|core
operator|.
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|core
operator|.
name|log
operator|.
name|fine
argument_list|(
name|core
operator|.
name|getLogId
argument_list|()
operator|+
literal|"docs deleted by query:"
operator|+
name|totDeleted
argument_list|)
expr_stmt|;
block|}
name|numDocsDeleted
operator|.
name|getAndAdd
argument_list|(
name|totDeleted
argument_list|)
expr_stmt|;
block|}
name|madeIt
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|tracker
operator|.
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|tracker
operator|.
name|scheduleCommitWithin
argument_list|(
name|tracker
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
name|iwCommit
operator|.
name|lock
argument_list|()
expr_stmt|;
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
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
name|closeSearcher
argument_list|()
expr_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
name|callPostCommitCallbacks
argument_list|()
expr_stmt|;
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
comment|// reset commit tracking
name|tracker
operator|.
name|didCommit
argument_list|()
expr_stmt|;
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
name|iwCommit
operator|.
name|unlock
argument_list|()
expr_stmt|;
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
return|return;
block|}
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
name|iwCommit
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// cancel any pending operations
if|if
condition|(
name|tracker
operator|.
name|pending
operator|!=
literal|null
condition|)
block|{
name|tracker
operator|.
name|pending
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|pending
operator|=
literal|null
expr_stmt|;
block|}
name|tracker
operator|.
name|scheduler
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|closeSearcher
argument_list|()
expr_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|iwCommit
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
comment|/** Helper class for tracking autoCommit state.    *    * Note: This is purely an implementation detail of autoCommit and will    * definitely change in the future, so the interface should not be    * relied-upon    *    * Note: all access must be synchronized.    */
DECL|class|CommitTracker
class|class
name|CommitTracker
implements|implements
name|Runnable
block|{
comment|// scheduler delay for maxDoc-triggered autocommits
DECL|field|DOC_COMMIT_DELAY_MS
specifier|public
specifier|final
name|int
name|DOC_COMMIT_DELAY_MS
init|=
literal|250
decl_stmt|;
comment|// settings, not final so we can change them in testing
DECL|field|docsUpperBound
name|int
name|docsUpperBound
decl_stmt|;
DECL|field|timeUpperBound
name|long
name|timeUpperBound
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduler
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|pending
specifier|private
name|ScheduledFuture
name|pending
decl_stmt|;
comment|// state
DECL|field|docsSinceCommit
name|long
name|docsSinceCommit
decl_stmt|;
DECL|field|autoCommitCount
name|int
name|autoCommitCount
init|=
literal|0
decl_stmt|;
DECL|field|lastAddedTime
name|long
name|lastAddedTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|CommitTracker
specifier|public
name|CommitTracker
parameter_list|()
block|{
name|docsSinceCommit
operator|=
literal|0
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
name|docsUpperBound
operator|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getInt
argument_list|(
literal|"updateHandler/autoCommit/maxDocs"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|timeUpperBound
operator|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getInt
argument_list|(
literal|"updateHandler/autoCommit/maxTime"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|SolrCore
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"AutoCommit: "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
comment|/** schedeule individual commits */
DECL|method|scheduleCommitWithin
specifier|public
specifier|synchronized
name|void
name|scheduleCommitWithin
parameter_list|(
name|long
name|commitMaxTime
parameter_list|)
block|{
comment|// Check if there is a commit already scheduled for longer then this time
if|if
condition|(
name|pending
operator|!=
literal|null
operator|&&
name|pending
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|>=
name|commitMaxTime
condition|)
block|{
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
block|}
comment|// schedule a new commit
if|if
condition|(
name|pending
operator|==
literal|null
condition|)
block|{
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|commitMaxTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Indicate that documents have been added      */
DECL|method|addedDocument
specifier|public
name|void
name|addedDocument
parameter_list|()
block|{
name|docsSinceCommit
operator|++
expr_stmt|;
name|lastAddedTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|// maxDocs-triggered autoCommit
if|if
condition|(
name|docsUpperBound
operator|>
literal|0
operator|&&
operator|(
name|docsSinceCommit
operator|>
name|docsUpperBound
operator|)
condition|)
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
operator|&&
name|pending
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|>
name|DOC_COMMIT_DELAY_MS
condition|)
block|{
comment|// another commit is pending, but too far away (probably due to
comment|// maxTime)
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|pending
operator|==
literal|null
condition|)
block|{
comment|// 1/4 second seems fast enough for anyone using maxDocs
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|DOC_COMMIT_DELAY_MS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|// maxTime-triggered autoCommit
if|if
condition|(
name|pending
operator|==
literal|null
operator|&&
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
comment|// Don't start a new event if one is already waiting
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|timeUpperBound
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Inform tracker that a commit has occurred, cancel any pending commits */
DECL|method|didCommit
specifier|public
name|void
name|didCommit
parameter_list|()
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
condition|)
block|{
name|pending
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
comment|// let it start another one
block|}
name|docsSinceCommit
operator|=
literal|0
expr_stmt|;
block|}
comment|/** This is the worker part for the ScheduledFuture **/
DECL|method|run
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|long
name|started
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|CommitUpdateCommand
name|command
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|command
operator|.
name|waitFlush
operator|=
literal|true
expr_stmt|;
name|command
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|commit
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|autoCommitCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|severe
argument_list|(
literal|"auto commit error..."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|pending
operator|=
literal|null
expr_stmt|;
block|}
comment|// check if docs have been submitted since the commit started
if|if
condition|(
name|lastAddedTime
operator|>
name|started
condition|)
block|{
if|if
condition|(
name|docsUpperBound
operator|>
literal|0
operator|&&
name|docsSinceCommit
operator|>
name|docsUpperBound
condition|)
block|{
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|timeUpperBound
operator|>
literal|0
condition|)
block|{
name|pending
operator|=
name|scheduler
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|timeUpperBound
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// to facilitate testing: blocks if called during commit
DECL|method|getCommitCount
specifier|public
specifier|synchronized
name|int
name|getCommitCount
parameter_list|()
block|{
return|return
name|autoCommitCount
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|timeUpperBound
operator|>
literal|0
operator|||
name|docsUpperBound
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|timeUpperBound
operator|>
literal|0
condition|?
operator|(
literal|"if uncommited for "
operator|+
name|timeUpperBound
operator|+
literal|"ms; "
operator|)
else|:
literal|""
operator|)
operator|+
operator|(
name|docsUpperBound
operator|>
literal|0
condition|?
operator|(
literal|"if "
operator|+
name|docsUpperBound
operator|+
literal|" uncommited docs "
operator|)
else|:
literal|""
operator|)
return|;
block|}
else|else
block|{
return|return
literal|"disabled"
return|;
block|}
block|}
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
name|tracker
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
name|tracker
operator|.
name|docsUpperBound
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tracker
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
name|tracker
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
name|tracker
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
name|lst
operator|.
name|add
argument_list|(
literal|"docsDeleted"
argument_list|,
name|numDocsDeleted
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
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
block|}
end_class

end_unit

