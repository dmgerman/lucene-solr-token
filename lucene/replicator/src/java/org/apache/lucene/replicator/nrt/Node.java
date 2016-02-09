begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
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
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|nio
operator|.
name|file
operator|.
name|NoSuchFileException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Locale
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
name|ReentrantReadWriteLock
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
name|CodecUtil
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
name|CorruptIndexException
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
name|search
operator|.
name|IndexSearcher
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
name|ReferenceManager
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
name|SearcherFactory
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
name|FSDirectory
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
name|store
operator|.
name|IndexInput
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
name|RAMOutputStream
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
name|StringHelper
import|;
end_import

begin_comment
comment|/** Common base class for {@link PrimaryNode} and {@link ReplicaNode}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|Node
specifier|abstract
class|class
name|Node
implements|implements
name|Closeable
block|{
DECL|field|VERBOSE_FILES
specifier|static
name|boolean
name|VERBOSE_FILES
init|=
literal|true
decl_stmt|;
DECL|field|VERBOSE_CONNECTIONS
specifier|static
name|boolean
name|VERBOSE_CONNECTIONS
init|=
literal|false
decl_stmt|;
comment|// Keys we store into IndexWriter's commit user data:
comment|/** Key to store the primary gen in the commit data, which increments every time we promote a new primary, so replicas can detect when the    *  primary they were talking to is changed */
DECL|field|PRIMARY_GEN_KEY
specifier|public
specifier|static
name|String
name|PRIMARY_GEN_KEY
init|=
literal|"__primaryGen"
decl_stmt|;
comment|/** Key to store the version in the commit data, which increments every time we open a new NRT reader */
DECL|field|VERSION_KEY
specifier|public
specifier|static
name|String
name|VERSION_KEY
init|=
literal|"__version"
decl_stmt|;
comment|/** Compact ordinal for this node */
DECL|field|id
specifier|protected
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|dir
specifier|protected
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|searcherFactory
specifier|protected
specifier|final
name|SearcherFactory
name|searcherFactory
decl_stmt|;
comment|// Tracks NRT readers, opened from IW (primary) or opened from replicated SegmentInfos pulled across the wire (replica):
DECL|field|mgr
specifier|protected
name|ReferenceManager
argument_list|<
name|IndexSearcher
argument_list|>
name|mgr
decl_stmt|;
comment|/** Startup time of original test, carefully propogated to all nodes to produce consistent "seconds since start time" in messages */
DECL|field|globalStartNS
specifier|public
specifier|static
name|long
name|globalStartNS
decl_stmt|;
comment|/** When this node was started */
DECL|field|localStartNS
specifier|public
specifier|static
specifier|final
name|long
name|localStartNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|// public static final long globalStartNS;
comment|// For debugging:
DECL|field|state
specifier|volatile
name|String
name|state
init|=
literal|"idle"
decl_stmt|;
comment|/** File metadata for last sync that succeeded; we use this as a cache */
DECL|field|lastFileMetaData
specifier|protected
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|lastFileMetaData
decl_stmt|;
DECL|method|Node
specifier|public
name|Node
parameter_list|(
name|int
name|id
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SearcherFactory
name|searcherFactory
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|searcherFactory
operator|=
name|searcherFactory
expr_stmt|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(id="
operator|+
name|id
operator|+
literal|")"
return|;
block|}
DECL|method|commit
specifier|public
specifier|abstract
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|nodeMessage
specifier|public
specifier|static
name|void
name|nodeMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%5.3fs %5.1fs:           [%11s] %s"
argument_list|,
operator|(
name|now
operator|-
name|globalStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
operator|(
name|now
operator|-
name|localStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|nodeMessage
specifier|public
specifier|static
name|void
name|nodeMessage
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%5.3fs %5.1fs:         N%d [%11s] %s"
argument_list|,
operator|(
name|now
operator|-
name|globalStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
operator|(
name|now
operator|-
name|localStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
name|id
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|message
specifier|protected
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%5.3fs %5.1fs: %7s %2s [%11s] %s"
argument_list|,
operator|(
name|now
operator|-
name|globalStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
operator|(
name|now
operator|-
name|localStartNS
operator|)
operator|/
literal|1000000000.
argument_list|,
name|state
argument_list|,
name|name
argument_list|()
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
name|char
name|mode
init|=
name|this
operator|instanceof
name|PrimaryNode
condition|?
literal|'P'
else|:
literal|'R'
decl_stmt|;
return|return
name|mode
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|isClosed
specifier|public
specifier|abstract
name|boolean
name|isClosed
parameter_list|()
function_decl|;
DECL|method|getCurrentSearchingVersion
specifier|public
name|long
name|getCurrentSearchingVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
name|mgr
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|(
operator|(
name|DirectoryReader
operator|)
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|)
operator|.
name|getVersion
argument_list|()
return|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bytesToString
specifier|public
specifier|static
name|String
name|bytesToString
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|<
literal|1024
condition|)
block|{
return|return
name|bytes
operator|+
literal|" b"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|<
literal|1024
operator|*
literal|1024
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f KB"
argument_list|,
name|bytes
operator|/
literal|1024.
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|<
literal|1024
operator|*
literal|1024
operator|*
literal|1024
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f MB"
argument_list|,
name|bytes
operator|/
literal|1024.
operator|/
literal|1024.
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f GB"
argument_list|,
name|bytes
operator|/
literal|1024.
operator|/
literal|1024.
operator|/
literal|1024.
argument_list|)
return|;
block|}
block|}
comment|/** Opens the specified file, reads its identifying information, including file length, full index header (includes the unique segment    *  ID) and the full footer (includes checksum), and returns the resulting {@link FileMetaData}.    *    *<p>This returns null, logging a message, if there are any problems (the file does not exist, is corrupt, truncated, etc.).</p> */
DECL|method|readLocalFileMetaData
specifier|public
name|FileMetaData
name|readLocalFileMetaData
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|cache
init|=
name|lastFileMetaData
decl_stmt|;
name|FileMetaData
name|result
decl_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
comment|// We may already have this file cached from the last NRT point:
name|result
operator|=
name|cache
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// Pull from the filesystem
name|long
name|checksum
decl_stmt|;
name|long
name|length
decl_stmt|;
name|byte
index|[]
name|header
decl_stmt|;
name|byte
index|[]
name|footer
decl_stmt|;
try|try
init|(
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
try|try
block|{
name|length
operator|=
name|in
operator|.
name|length
argument_list|()
expr_stmt|;
name|header
operator|=
name|CodecUtil
operator|.
name|readIndexHeader
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|footer
operator|=
name|CodecUtil
operator|.
name|readFooter
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|checksum
operator|=
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
decl||
name|CorruptIndexException
name|cie
parameter_list|)
block|{
comment|// File exists but is busted: we must copy it.  This happens when node had crashed, corrupting an un-fsync'd file.  On init we try
comment|// to delete such unreferenced files, but virus checker can block that, leaving this bad file.
if|if
condition|(
name|VERBOSE_FILES
condition|)
block|{
name|message
argument_list|(
literal|"file "
operator|+
name|fileName
operator|+
literal|": will copy [existing file is corrupt]"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
if|if
condition|(
name|VERBOSE_FILES
condition|)
block|{
name|message
argument_list|(
literal|"file "
operator|+
name|fileName
operator|+
literal|" has length="
operator|+
name|bytesToString
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|e
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE_FILES
condition|)
block|{
name|message
argument_list|(
literal|"file "
operator|+
name|fileName
operator|+
literal|": will copy [file does not exist]"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|// NOTE: checksum is redundant w/ footer, but we break it out separately because when the bits cross the wire we need direct access to
comment|// checksum when copying to catch bit flips:
name|result
operator|=
operator|new
name|FileMetaData
argument_list|(
name|header
argument_list|,
name|footer
argument_list|,
name|length
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

