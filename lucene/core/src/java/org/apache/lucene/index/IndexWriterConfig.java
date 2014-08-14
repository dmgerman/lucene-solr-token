begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|PrintStream
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
name|analysis
operator|.
name|Analyzer
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
name|codecs
operator|.
name|Codec
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
name|DocumentsWriterPerThread
operator|.
name|IndexingChain
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
operator|.
name|IndexReaderWarmer
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
name|similarities
operator|.
name|Similarity
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
name|InfoStream
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
name|PrintStreamInfoStream
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
name|SetOnce
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
name|SetOnce
operator|.
name|AlreadySetException
import|;
end_import

begin_comment
comment|/**  * Holds all the configuration that is used to create an {@link IndexWriter}.  * Once {@link IndexWriter} has been created with this object, changes to this  * object will not affect the {@link IndexWriter} instance. For that, use  * {@link LiveIndexWriterConfig} that is returned from {@link IndexWriter#getConfig()}.  *   *<p>  * All setter methods return {@link IndexWriterConfig} to allow chaining  * settings conveniently, for example:  *   *<pre class="prettyprint">  * IndexWriterConfig conf = new IndexWriterConfig(analyzer);  * conf.setter1().setter2();  *</pre>  *   * @see IndexWriter#getConfig()  *   * @since 3.1  */
end_comment

begin_class
DECL|class|IndexWriterConfig
specifier|public
specifier|final
class|class
name|IndexWriterConfig
extends|extends
name|LiveIndexWriterConfig
block|{
comment|/**    * Specifies the open mode for {@link IndexWriter}.    */
DECL|enum|OpenMode
specifier|public
specifier|static
enum|enum
name|OpenMode
block|{
comment|/**       * Creates a new index or overwrites an existing one.       */
DECL|enum constant|CREATE
name|CREATE
block|,
comment|/**       * Opens an existing index.       */
DECL|enum constant|APPEND
name|APPEND
block|,
comment|/**       * Creates a new index if one does not exist,      * otherwise it opens the index and documents will be appended.       */
DECL|enum constant|CREATE_OR_APPEND
name|CREATE_OR_APPEND
block|}
comment|/** Denotes a flush trigger is disabled. */
DECL|field|DISABLE_AUTO_FLUSH
specifier|public
specifier|final
specifier|static
name|int
name|DISABLE_AUTO_FLUSH
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Disabled by default (because IndexWriter flushes by RAM usage by default). */
DECL|field|DEFAULT_MAX_BUFFERED_DELETE_TERMS
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_BUFFERED_DELETE_TERMS
init|=
name|DISABLE_AUTO_FLUSH
decl_stmt|;
comment|/** Disabled by default (because IndexWriter flushes by RAM usage by default). */
DECL|field|DEFAULT_MAX_BUFFERED_DOCS
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_BUFFERED_DOCS
init|=
name|DISABLE_AUTO_FLUSH
decl_stmt|;
comment|/**    * Default value is 16 MB (which means flush when buffered docs consume    * approximately 16 MB RAM).    */
DECL|field|DEFAULT_RAM_BUFFER_SIZE_MB
specifier|public
specifier|final
specifier|static
name|double
name|DEFAULT_RAM_BUFFER_SIZE_MB
init|=
literal|16.0
decl_stmt|;
comment|/**    * Default value for the write lock timeout (1,000 ms).    *    * @see #setDefaultWriteLockTimeout(long)    */
DECL|field|WRITE_LOCK_TIMEOUT
specifier|public
specifier|static
name|long
name|WRITE_LOCK_TIMEOUT
init|=
literal|1000
decl_stmt|;
comment|/** Default setting for {@link #setReaderPooling}. */
DECL|field|DEFAULT_READER_POOLING
specifier|public
specifier|final
specifier|static
name|boolean
name|DEFAULT_READER_POOLING
init|=
literal|false
decl_stmt|;
comment|/** Default value is 1945. Change using {@link #setRAMPerThreadHardLimitMB(int)} */
DECL|field|DEFAULT_RAM_PER_THREAD_HARD_LIMIT_MB
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RAM_PER_THREAD_HARD_LIMIT_MB
init|=
literal|1945
decl_stmt|;
comment|/** The maximum number of simultaneous threads that may be    *  indexing documents at once in IndexWriter; if more    *  than this many threads arrive they will wait for    *  others to finish. Default value is 8. */
DECL|field|DEFAULT_MAX_THREAD_STATES
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_THREAD_STATES
init|=
literal|8
decl_stmt|;
comment|/** Default value for compound file system for newly written segments    *  (set to<code>true</code>). For batch indexing with very large     *  ram buffers use<code>false</code> */
DECL|field|DEFAULT_USE_COMPOUND_FILE_SYSTEM
specifier|public
specifier|final
specifier|static
name|boolean
name|DEFAULT_USE_COMPOUND_FILE_SYSTEM
init|=
literal|true
decl_stmt|;
comment|/** Default value for calling {@link AtomicReader#checkIntegrity()} before    *  merging segments (set to<code>false</code>). You can set this    *  to<code>true</code> for additional safety. */
DECL|field|DEFAULT_CHECK_INTEGRITY_AT_MERGE
specifier|public
specifier|final
specifier|static
name|boolean
name|DEFAULT_CHECK_INTEGRITY_AT_MERGE
init|=
literal|false
decl_stmt|;
comment|/** Default value for whether calls to {@link IndexWriter#close()} include a commit. */
DECL|field|DEFAULT_COMMIT_ON_CLOSE
specifier|public
specifier|final
specifier|static
name|boolean
name|DEFAULT_COMMIT_ON_CLOSE
init|=
literal|true
decl_stmt|;
comment|/**    * Sets the default (for any instance) maximum time to wait for a write lock    * (in milliseconds).    */
DECL|method|setDefaultWriteLockTimeout
specifier|public
specifier|static
name|void
name|setDefaultWriteLockTimeout
parameter_list|(
name|long
name|writeLockTimeout
parameter_list|)
block|{
name|WRITE_LOCK_TIMEOUT
operator|=
name|writeLockTimeout
expr_stmt|;
block|}
comment|/**    * Returns the default write lock timeout for newly instantiated    * IndexWriterConfigs.    *    * @see #setDefaultWriteLockTimeout(long)    */
DECL|method|getDefaultWriteLockTimeout
specifier|public
specifier|static
name|long
name|getDefaultWriteLockTimeout
parameter_list|()
block|{
return|return
name|WRITE_LOCK_TIMEOUT
return|;
block|}
comment|// indicates whether this config instance is already attached to a writer.
comment|// not final so that it can be cloned properly.
DECL|field|writer
specifier|private
name|SetOnce
argument_list|<
name|IndexWriter
argument_list|>
name|writer
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Sets the {@link IndexWriter} this config is attached to.    *     * @throws AlreadySetException    *           if this config is already attached to a writer.    */
DECL|method|setIndexWriter
name|IndexWriterConfig
name|setIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|.
name|set
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Creates a new config that with the default {@link    * Analyzer}. By default, {@link TieredMergePolicy} is used    * for merging;    * Note that {@link TieredMergePolicy} is free to select    * non-contiguous merges, which means docIDs may not    * remain monotonic over time.  If this is a problem you    * should switch to {@link LogByteSizeMergePolicy} or    * {@link LogDocMergePolicy}.    */
DECL|method|IndexWriterConfig
specifier|public
name|IndexWriterConfig
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
comment|/** Specifies {@link OpenMode} of the index.    *     *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setOpenMode
specifier|public
name|IndexWriterConfig
name|setOpenMode
parameter_list|(
name|OpenMode
name|openMode
parameter_list|)
block|{
if|if
condition|(
name|openMode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"openMode must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|openMode
operator|=
name|openMode
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getOpenMode
specifier|public
name|OpenMode
name|getOpenMode
parameter_list|()
block|{
return|return
name|openMode
return|;
block|}
comment|/**    * Expert: allows an optional {@link IndexDeletionPolicy} implementation to be    * specified. You can use this to control when prior commits are deleted from    * the index. The default policy is {@link KeepOnlyLastCommitDeletionPolicy}    * which removes all prior commits as soon as a new commit is done (this    * matches behavior before 2.2). Creating your own policy can allow you to    * explicitly keep previous "point in time" commits alive in the index for    * some time, to allow readers to refresh to the new commit without having the    * old commit deleted out from under them. This is necessary on filesystems    * like NFS that do not support "delete on last close" semantics, which    * Lucene's "point in time" search normally relies on.    *<p>    *<b>NOTE:</b> the deletion policy cannot be null.    *    *<p>Only takes effect when IndexWriter is first created.     */
DECL|method|setIndexDeletionPolicy
specifier|public
name|IndexWriterConfig
name|setIndexDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|delPolicy
parameter_list|)
block|{
if|if
condition|(
name|delPolicy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"indexDeletionPolicy must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|delPolicy
operator|=
name|delPolicy
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexDeletionPolicy
specifier|public
name|IndexDeletionPolicy
name|getIndexDeletionPolicy
parameter_list|()
block|{
return|return
name|delPolicy
return|;
block|}
comment|/**    * Expert: allows to open a certain commit point. The default is null which    * opens the latest commit point.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setIndexCommit
specifier|public
name|IndexWriterConfig
name|setIndexCommit
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
block|{
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
comment|/**    * Expert: set the {@link Similarity} implementation used by this IndexWriter.    *<p>    *<b>NOTE:</b> the similarity cannot be null.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setSimilarity
specifier|public
name|IndexWriterConfig
name|setSimilarity
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
if|if
condition|(
name|similarity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"similarity must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|similarity
return|;
block|}
comment|/**    * Expert: sets the merge scheduler used by this writer. The default is    * {@link ConcurrentMergeScheduler}.    *<p>    *<b>NOTE:</b> the merge scheduler cannot be null.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setMergeScheduler
specifier|public
name|IndexWriterConfig
name|setMergeScheduler
parameter_list|(
name|MergeScheduler
name|mergeScheduler
parameter_list|)
block|{
if|if
condition|(
name|mergeScheduler
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"mergeScheduler must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|mergeScheduler
operator|=
name|mergeScheduler
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getMergeScheduler
specifier|public
name|MergeScheduler
name|getMergeScheduler
parameter_list|()
block|{
return|return
name|mergeScheduler
return|;
block|}
comment|/**    * Sets the maximum time to wait for a write lock (in milliseconds) for this    * instance. You can change the default value for all instances by calling    * {@link #setDefaultWriteLockTimeout(long)}.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setWriteLockTimeout
specifier|public
name|IndexWriterConfig
name|setWriteLockTimeout
parameter_list|(
name|long
name|writeLockTimeout
parameter_list|)
block|{
name|this
operator|.
name|writeLockTimeout
operator|=
name|writeLockTimeout
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteLockTimeout
specifier|public
name|long
name|getWriteLockTimeout
parameter_list|()
block|{
return|return
name|writeLockTimeout
return|;
block|}
comment|/**    * Set the {@link Codec}.    *     *<p>    * Only takes effect when IndexWriter is first created.    */
DECL|method|setCodec
specifier|public
name|IndexWriterConfig
name|setCodec
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|codec
operator|=
name|codec
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
annotation|@
name|Override
DECL|method|getMergePolicy
specifier|public
name|MergePolicy
name|getMergePolicy
parameter_list|()
block|{
return|return
name|mergePolicy
return|;
block|}
comment|/** Expert: Sets the {@link DocumentsWriterPerThreadPool} instance used by the    * IndexWriter to assign thread-states to incoming indexing threads.    *</p>    *<p>    * NOTE: The given {@link DocumentsWriterPerThreadPool} instance must not be used with    * other {@link IndexWriter} instances once it has been initialized / associated with an    * {@link IndexWriter}.    *</p>    *<p>    * NOTE: This only takes effect when IndexWriter is first created.</p>*/
DECL|method|setIndexerThreadPool
name|IndexWriterConfig
name|setIndexerThreadPool
parameter_list|(
name|DocumentsWriterPerThreadPool
name|threadPool
parameter_list|)
block|{
if|if
condition|(
name|threadPool
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"threadPool must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexerThreadPool
operator|=
name|threadPool
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexerThreadPool
name|DocumentsWriterPerThreadPool
name|getIndexerThreadPool
parameter_list|()
block|{
return|return
name|indexerThreadPool
return|;
block|}
comment|/**    * Sets the max number of simultaneous threads that may be indexing documents    * at once in IndexWriter. Values&lt; 1 are invalid and if passed    *<code>maxThreadStates</code> will be set to    * {@link #DEFAULT_MAX_THREAD_STATES}.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setMaxThreadStates
specifier|public
name|IndexWriterConfig
name|setMaxThreadStates
parameter_list|(
name|int
name|maxThreadStates
parameter_list|)
block|{
name|this
operator|.
name|indexerThreadPool
operator|=
operator|new
name|DocumentsWriterPerThreadPool
argument_list|(
name|maxThreadStates
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxThreadStates
specifier|public
name|int
name|getMaxThreadStates
parameter_list|()
block|{
return|return
name|indexerThreadPool
operator|.
name|getMaxThreadStates
argument_list|()
return|;
block|}
comment|/** By default, IndexWriter does not pool the    *  SegmentReaders it must open for deletions and    *  merging, unless a near-real-time reader has been    *  obtained by calling {@link DirectoryReader#open(IndexWriter, boolean)}.    *  This method lets you enable pooling without getting a    *  near-real-time reader.  NOTE: if you set this to    *  false, IndexWriter will still pool readers once    *  {@link DirectoryReader#open(IndexWriter, boolean)} is called.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setReaderPooling
specifier|public
name|IndexWriterConfig
name|setReaderPooling
parameter_list|(
name|boolean
name|readerPooling
parameter_list|)
block|{
name|this
operator|.
name|readerPooling
operator|=
name|readerPooling
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderPooling
specifier|public
name|boolean
name|getReaderPooling
parameter_list|()
block|{
return|return
name|readerPooling
return|;
block|}
comment|/** Expert: sets the {@link DocConsumer} chain to be used to process documents.    *    *<p>Only takes effect when IndexWriter is first created. */
DECL|method|setIndexingChain
name|IndexWriterConfig
name|setIndexingChain
parameter_list|(
name|IndexingChain
name|indexingChain
parameter_list|)
block|{
if|if
condition|(
name|indexingChain
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"indexingChain must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexingChain
operator|=
name|indexingChain
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexingChain
name|IndexingChain
name|getIndexingChain
parameter_list|()
block|{
return|return
name|indexingChain
return|;
block|}
comment|/**    * Expert: Controls when segments are flushed to disk during indexing.    * The {@link FlushPolicy} initialized during {@link IndexWriter} instantiation and once initialized    * the given instance is bound to this {@link IndexWriter} and should not be used with another writer.    * @see #setMaxBufferedDeleteTerms(int)    * @see #setMaxBufferedDocs(int)    * @see #setRAMBufferSizeMB(double)    */
DECL|method|setFlushPolicy
name|IndexWriterConfig
name|setFlushPolicy
parameter_list|(
name|FlushPolicy
name|flushPolicy
parameter_list|)
block|{
if|if
condition|(
name|flushPolicy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"flushPolicy must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|flushPolicy
operator|=
name|flushPolicy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Expert: Sets the maximum memory consumption per thread triggering a forced    * flush if exceeded. A {@link DocumentsWriterPerThread} is forcefully flushed    * once it exceeds this limit even if the {@link #getRAMBufferSizeMB()} has    * not been exceeded. This is a safety limit to prevent a    * {@link DocumentsWriterPerThread} from address space exhaustion due to its    * internal 32 bit signed integer based memory addressing.    * The given value must be less that 2GB (2048MB)    *     * @see #DEFAULT_RAM_PER_THREAD_HARD_LIMIT_MB    */
DECL|method|setRAMPerThreadHardLimitMB
specifier|public
name|IndexWriterConfig
name|setRAMPerThreadHardLimitMB
parameter_list|(
name|int
name|perThreadHardLimitMB
parameter_list|)
block|{
if|if
condition|(
name|perThreadHardLimitMB
operator|<=
literal|0
operator|||
name|perThreadHardLimitMB
operator|>=
literal|2048
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"PerThreadHardLimit must be greater than 0 and less than 2048MB"
argument_list|)
throw|;
block|}
name|this
operator|.
name|perThreadHardLimitMB
operator|=
name|perThreadHardLimitMB
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getRAMPerThreadHardLimitMB
specifier|public
name|int
name|getRAMPerThreadHardLimitMB
parameter_list|()
block|{
return|return
name|perThreadHardLimitMB
return|;
block|}
annotation|@
name|Override
DECL|method|getFlushPolicy
name|FlushPolicy
name|getFlushPolicy
parameter_list|()
block|{
return|return
name|flushPolicy
return|;
block|}
annotation|@
name|Override
DECL|method|getInfoStream
specifier|public
name|InfoStream
name|getInfoStream
parameter_list|()
block|{
return|return
name|infoStream
return|;
block|}
annotation|@
name|Override
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|super
operator|.
name|getAnalyzer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxBufferedDeleteTerms
specifier|public
name|int
name|getMaxBufferedDeleteTerms
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMaxBufferedDeleteTerms
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxBufferedDocs
specifier|public
name|int
name|getMaxBufferedDocs
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMaxBufferedDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMergedSegmentWarmer
specifier|public
name|IndexReaderWarmer
name|getMergedSegmentWarmer
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMergedSegmentWarmer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRAMBufferSizeMB
specifier|public
name|double
name|getRAMBufferSizeMB
parameter_list|()
block|{
return|return
name|super
operator|.
name|getRAMBufferSizeMB
argument_list|()
return|;
block|}
comment|/**     * Information about merges, deletes and a    * message when maxFieldLength is reached will be printed    * to this. Must not be null, but {@link InfoStream#NO_OUTPUT}     * may be used to supress output.    */
DECL|method|setInfoStream
specifier|public
name|IndexWriterConfig
name|setInfoStream
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot set InfoStream implementation to null. "
operator|+
literal|"To disable logging use InfoStream.NO_OUTPUT"
argument_list|)
throw|;
block|}
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**     * Convenience method that uses {@link PrintStreamInfoStream}.  Must not be null.    */
DECL|method|setInfoStream
specifier|public
name|IndexWriterConfig
name|setInfoStream
parameter_list|(
name|PrintStream
name|printStream
parameter_list|)
block|{
if|if
condition|(
name|printStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"printStream must not be null"
argument_list|)
throw|;
block|}
return|return
name|setInfoStream
argument_list|(
operator|new
name|PrintStreamInfoStream
argument_list|(
name|printStream
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMergePolicy
specifier|public
name|IndexWriterConfig
name|setMergePolicy
parameter_list|(
name|MergePolicy
name|mergePolicy
parameter_list|)
block|{
return|return
operator|(
name|IndexWriterConfig
operator|)
name|super
operator|.
name|setMergePolicy
argument_list|(
name|mergePolicy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxBufferedDeleteTerms
specifier|public
name|IndexWriterConfig
name|setMaxBufferedDeleteTerms
parameter_list|(
name|int
name|maxBufferedDeleteTerms
parameter_list|)
block|{
return|return
operator|(
name|IndexWriterConfig
operator|)
name|super
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
name|maxBufferedDeleteTerms
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxBufferedDocs
specifier|public
name|IndexWriterConfig
name|setMaxBufferedDocs
parameter_list|(
name|int
name|maxBufferedDocs
parameter_list|)
block|{
return|return
operator|(
name|IndexWriterConfig
operator|)
name|super
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setMergedSegmentWarmer
specifier|public
name|IndexWriterConfig
name|setMergedSegmentWarmer
parameter_list|(
name|IndexReaderWarmer
name|mergeSegmentWarmer
parameter_list|)
block|{
return|return
operator|(
name|IndexWriterConfig
operator|)
name|super
operator|.
name|setMergedSegmentWarmer
argument_list|(
name|mergeSegmentWarmer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setRAMBufferSizeMB
specifier|public
name|IndexWriterConfig
name|setRAMBufferSizeMB
parameter_list|(
name|double
name|ramBufferSizeMB
parameter_list|)
block|{
return|return
operator|(
name|IndexWriterConfig
operator|)
name|super
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBufferSizeMB
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setUseCompoundFile
specifier|public
name|IndexWriterConfig
name|setUseCompoundFile
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
return|return
operator|(
name|IndexWriterConfig
operator|)
name|super
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
return|;
block|}
comment|/**    * Sets if calls {@link IndexWriter#close()} should first commit    * before closing.  Use<code>true</code> to match behavior of Lucene 4.x.    */
DECL|method|setCommitOnClose
specifier|public
name|IndexWriterConfig
name|setCommitOnClose
parameter_list|(
name|boolean
name|commitOnClose
parameter_list|)
block|{
name|this
operator|.
name|commitOnClose
operator|=
name|commitOnClose
expr_stmt|;
return|return
name|this
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"writer="
argument_list|)
operator|.
name|append
argument_list|(
name|writer
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

