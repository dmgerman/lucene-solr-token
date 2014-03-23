begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
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
name|DirectoryReader
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
name|IndexCommit
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
name|IndexFileNames
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
name|IndexNotFoundException
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
name|SegmentInfos
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
name|replicator
operator|.
name|ReplicationClient
operator|.
name|ReplicationHandler
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
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

begin_comment
comment|/**  * A {@link ReplicationHandler} for replication of an index. Implements  * {@link #revisionReady} by copying the files pointed by the client resolver to  * the index {@link Directory} and then touches the index with  * {@link IndexWriter} to make sure any unused files are deleted.  *<p>  *<b>NOTE:</b> this handler assumes that {@link IndexWriter} is not opened by  * another process on the index directory. In fact, opening an  * {@link IndexWriter} on the same directory to which files are copied can lead  * to undefined behavior, where some or all the files will be deleted, override  * other files or simply create a mess. When you replicate an index, it is best  * if the index is never modified by {@link IndexWriter}, except the one that is  * open on the source index, from which you replicate.  *<p>  * This handler notifies the application via a provided {@link Callable} when an  * updated index commit was made available for it.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexReplicationHandler
specifier|public
class|class
name|IndexReplicationHandler
implements|implements
name|ReplicationHandler
block|{
comment|/**    * The component used to log messages to the {@link InfoStream#getDefault()    * default} {@link InfoStream}.    */
DECL|field|INFO_STREAM_COMPONENT
specifier|public
specifier|static
specifier|final
name|String
name|INFO_STREAM_COMPONENT
init|=
literal|"IndexReplicationHandler"
decl_stmt|;
DECL|field|indexDir
specifier|private
specifier|final
name|Directory
name|indexDir
decl_stmt|;
DECL|field|callback
specifier|private
specifier|final
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|callback
decl_stmt|;
DECL|field|currentRevisionFiles
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|currentRevisionFiles
decl_stmt|;
DECL|field|currentVersion
specifier|private
specifier|volatile
name|String
name|currentVersion
decl_stmt|;
DECL|field|infoStream
specifier|private
specifier|volatile
name|InfoStream
name|infoStream
init|=
name|InfoStream
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|/**    * Returns the last {@link IndexCommit} found in the {@link Directory}, or    * {@code null} if there are no commits.    */
DECL|method|getLastCommit
specifier|public
specifier|static
name|IndexCommit
name|getLastCommit
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|commits
init|=
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// listCommits guarantees that we get at least one commit back, or
comment|// IndexNotFoundException which we handle below
return|return
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IndexNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore the exception and return null
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Verifies that the last file is segments_N and fails otherwise. It also    * removes and returns the file from the list, because it needs to be handled    * last, after all files. This is important in order to guarantee that if a    * reader sees the new segments_N, all other segment files are already on    * stable storage.    *<p>    * The reason why the code fails instead of putting segments_N file last is    * that this indicates an error in the Revision implementation.    */
DECL|method|getSegmentsFile
specifier|public
specifier|static
name|String
name|getSegmentsFile
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|boolean
name|allowEmpty
parameter_list|)
block|{
if|if
condition|(
name|files
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|allowEmpty
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"empty list of files not allowed"
argument_list|)
throw|;
block|}
block|}
name|String
name|segmentsFile
init|=
name|files
operator|.
name|remove
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|segmentsFile
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|||
name|segmentsFile
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"last file to copy+sync must be segments_N but got "
operator|+
name|segmentsFile
operator|+
literal|"; check your Revision implementation!"
argument_list|)
throw|;
block|}
return|return
name|segmentsFile
return|;
block|}
comment|/**    * Cleanup the index directory by deleting all given files. Called when file    * copy or sync failed.    */
DECL|method|cleanupFilesOnFailure
specifier|public
specifier|static
name|void
name|cleanupFilesOnFailure
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// suppress any exception because if we're here, it means copy
comment|// failed, and we must cleanup after ourselves.
block|}
block|}
block|}
comment|/**    * Cleans up the index directory from old index files. This method uses the    * last commit found by {@link #getLastCommit(Directory)}. If it matches the    * expected segmentsFile, then all files not referenced by this commit point    * are deleted.    *<p>    *<b>NOTE:</b> this method does a best effort attempt to clean the index    * directory. It suppresses any exceptions that occur, as this can be retried    * the next time.    */
DECL|method|cleanupOldIndexFiles
specifier|public
specifier|static
name|void
name|cleanupOldIndexFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentsFile
parameter_list|)
block|{
try|try
block|{
name|IndexCommit
name|commit
init|=
name|getLastCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// commit == null means weird IO errors occurred, ignore them
comment|// if there were any IO errors reading the expected commit point (i.e.
comment|// segments files mismatch), then ignore that commit either.
if|if
condition|(
name|commit
operator|!=
literal|null
operator|&&
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
operator|.
name|equals
argument_list|(
name|segmentsFile
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|commitFiles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|commitFiles
operator|.
name|addAll
argument_list|(
name|commit
operator|.
name|getFileNames
argument_list|()
argument_list|)
expr_stmt|;
name|commitFiles
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
expr_stmt|;
name|Matcher
name|matcher
init|=
name|IndexFileNames
operator|.
name|CODEC_FILE_PATTERN
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|commitFiles
operator|.
name|contains
argument_list|(
name|file
argument_list|)
operator|&&
operator|(
name|matcher
operator|.
name|reset
argument_list|(
name|file
argument_list|)
operator|.
name|matches
argument_list|()
operator|||
name|file
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|)
condition|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// suppress, it's just a best effort
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ignore any errors that happens during this state and only log it. this
comment|// cleanup will have a chance to succeed the next time we get a new
comment|// revision.
block|}
block|}
comment|/**    * Copies the files from the source directory to the target one, if they are    * not the same.    */
DECL|method|copyFiles
specifier|public
specifier|static
name|void
name|copyFiles
parameter_list|(
name|Directory
name|source
parameter_list|,
name|Directory
name|target
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|source
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|source
operator|.
name|copy
argument_list|(
name|target
argument_list|,
name|file
argument_list|,
name|file
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Writes {@link IndexFileNames#SEGMENTS_GEN} file to the directory, reading    * the generation from the given {@code segmentsFile}. If it is {@code null},    * this method deletes segments.gen from the directory.    */
DECL|method|writeSegmentsGen
specifier|public
specifier|static
name|void
name|writeSegmentsGen
parameter_list|(
name|String
name|segmentsFile
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
if|if
condition|(
name|segmentsFile
operator|!=
literal|null
condition|)
block|{
name|SegmentInfos
operator|.
name|writeSegmentsGen
argument_list|(
name|dir
argument_list|,
name|SegmentInfos
operator|.
name|generationFromSegmentsFileName
argument_list|(
name|segmentsFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// suppress any errors while deleting this file.
block|}
block|}
block|}
comment|/**    * Constructor with the given index directory and callback to notify when the    * indexes were updated.    */
DECL|method|IndexReplicationHandler
specifier|public
name|IndexReplicationHandler
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|callback
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
name|currentRevisionFiles
operator|=
literal|null
expr_stmt|;
name|currentVersion
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|indexDir
argument_list|)
condition|)
block|{
specifier|final
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|commits
init|=
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
specifier|final
name|IndexCommit
name|commit
init|=
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|currentRevisionFiles
operator|=
name|IndexRevision
operator|.
name|revisionFiles
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|currentVersion
operator|=
name|IndexRevision
operator|.
name|revisionVersion
argument_list|(
name|commit
argument_list|)
expr_stmt|;
specifier|final
name|InfoStream
name|infoStream
init|=
name|InfoStream
operator|.
name|getDefault
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|,
literal|"constructor(): currentVersion="
operator|+
name|currentVersion
operator|+
literal|" currentRevisionFiles="
operator|+
name|currentRevisionFiles
argument_list|)
expr_stmt|;
name|infoStream
operator|.
name|message
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|,
literal|"constructor(): commit="
operator|+
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|currentVersion
specifier|public
name|String
name|currentVersion
parameter_list|()
block|{
return|return
name|currentVersion
return|;
block|}
annotation|@
name|Override
DECL|method|currentRevisionFiles
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|currentRevisionFiles
parameter_list|()
block|{
return|return
name|currentRevisionFiles
return|;
block|}
annotation|@
name|Override
DECL|method|revisionReady
specifier|public
name|void
name|revisionReady
parameter_list|(
name|String
name|version
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|revisionFiles
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|copiedFiles
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Directory
argument_list|>
name|sourceDirectory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|revisionFiles
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this handler handles only a single source; got "
operator|+
name|revisionFiles
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
name|Directory
name|clientDir
init|=
name|sourceDirectory
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|copiedFiles
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|segmentsFile
init|=
name|getSegmentsFile
argument_list|(
name|files
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// copy files from the client to index directory
name|copyFiles
argument_list|(
name|clientDir
argument_list|,
name|indexDir
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// fsync all copied files (except segmentsFile)
name|indexDir
operator|.
name|sync
argument_list|(
name|files
argument_list|)
expr_stmt|;
comment|// now copy and fsync segmentsFile
name|clientDir
operator|.
name|copy
argument_list|(
name|indexDir
argument_list|,
name|segmentsFile
argument_list|,
name|segmentsFile
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
name|indexDir
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|segmentsFile
argument_list|)
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|segmentsFile
argument_list|)
expr_stmt|;
comment|// add it back so it gets deleted too
name|cleanupFilesOnFailure
argument_list|(
name|indexDir
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
comment|// all files have been successfully copied + sync'd. update the handler's state
name|currentRevisionFiles
operator|=
name|revisionFiles
expr_stmt|;
name|currentVersion
operator|=
name|version
expr_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|,
literal|"revisionReady(): currentVersion="
operator|+
name|currentVersion
operator|+
literal|" currentRevisionFiles="
operator|+
name|currentRevisionFiles
argument_list|)
expr_stmt|;
block|}
comment|// update the segments.gen file
name|writeSegmentsGen
argument_list|(
name|segmentsFile
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
comment|// Cleanup the index directory from old and unused index files.
comment|// NOTE: we don't use IndexWriter.deleteUnusedFiles here since it may have
comment|// side-effects, e.g. if it hits sudden IO errors while opening the index
comment|// (and can end up deleting the entire index). It is not our job to protect
comment|// against those errors, app will probably hit them elsewhere.
name|cleanupOldIndexFiles
argument_list|(
name|indexDir
argument_list|,
name|segmentsFile
argument_list|)
expr_stmt|;
comment|// successfully updated the index, notify the callback that the index is
comment|// ready.
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|callback
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Sets the {@link InfoStream} to use for logging messages. */
DECL|method|setInfoStream
specifier|public
name|void
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
name|infoStream
operator|=
name|InfoStream
operator|.
name|NO_OUTPUT
expr_stmt|;
block|}
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
block|}
end_class

end_unit

