begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|Arrays
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|Accountable
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
name|IOUtils
import|;
end_import

begin_comment
comment|// TODO
end_comment

begin_comment
comment|//   - let subclass dictate policy...?
end_comment

begin_comment
comment|//   - rename to MergeCacheingDir?  NRTCachingDir
end_comment

begin_comment
comment|/**  * Wraps a {@link RAMDirectory}  * around any provided delegate directory, to  * be used during NRT search.  *  *<p>This class is likely only useful in a near-real-time  * context, where indexing rate is lowish but reopen  * rate is highish, resulting in many tiny files being  * written.  This directory keeps such segments (as well as  * the segments produced by merging them, as long as they  * are small enough), in RAM.</p>  *  *<p>This is safe to use: when your app calls {IndexWriter#commit},  * all cached files will be flushed from the cached and sync'd.</p>  *  *<p>Here's a simple example usage:  *  *<pre class="prettyprint">  *   Directory fsDir = FSDirectory.open(new File("/path/to/index").toPath());  *   NRTCachingDirectory cachedFSDir = new NRTCachingDirectory(fsDir, 5.0, 60.0);  *   IndexWriterConfig conf = new IndexWriterConfig(analyzer);  *   IndexWriter writer = new IndexWriter(cachedFSDir, conf);  *</pre>  *  *<p>This will cache all newly flushed segments, all merges  * whose expected segment size is<= 5 MB, unless the net  * cached bytes exceeds 60 MB at which point all writes will  * not be cached (until the net bytes falls below 60 MB).</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NRTCachingDirectory
specifier|public
class|class
name|NRTCachingDirectory
extends|extends
name|FilterDirectory
implements|implements
name|Accountable
block|{
DECL|field|cache
specifier|private
specifier|final
name|RAMDirectory
name|cache
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|maxMergeSizeBytes
specifier|private
specifier|final
name|long
name|maxMergeSizeBytes
decl_stmt|;
DECL|field|maxCachedBytes
specifier|private
specifier|final
name|long
name|maxCachedBytes
decl_stmt|;
DECL|field|VERBOSE
specifier|private
specifier|static
specifier|final
name|boolean
name|VERBOSE
init|=
literal|false
decl_stmt|;
comment|/**    *  We will cache a newly created output if 1) it's a    *  flush or a merge and the estimated size of the merged segment is<=    *  maxMergeSizeMB, and 2) the total cached bytes is<=    *  maxCachedMB */
DECL|method|NRTCachingDirectory
specifier|public
name|NRTCachingDirectory
parameter_list|(
name|Directory
name|delegate
parameter_list|,
name|double
name|maxMergeSizeMB
parameter_list|,
name|double
name|maxCachedMB
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|maxMergeSizeBytes
operator|=
call|(
name|long
call|)
argument_list|(
name|maxMergeSizeMB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|maxCachedBytes
operator|=
call|(
name|long
call|)
argument_list|(
name|maxCachedMB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
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
literal|"NRTCachingDirectory("
operator|+
name|in
operator|+
literal|"; maxCacheMB="
operator|+
operator|(
name|maxCachedBytes
operator|/
literal|1024
operator|/
literal|1024.
operator|)
operator|+
literal|" maxMergeSizeMB="
operator|+
operator|(
name|maxMergeSizeBytes
operator|/
literal|1024
operator|/
literal|1024.
operator|)
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
specifier|synchronized
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|cache
operator|.
name|listAll
argument_list|()
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|f
range|:
name|in
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|files
operator|.
name|add
argument_list|(
name|f
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"file: "
operator|+
name|in
operator|+
literal|" appears both in delegate and in cache: "
operator|+
literal|"cache="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|cache
operator|.
name|listAll
argument_list|()
argument_list|)
operator|+
literal|",delegate="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|in
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|return
name|files
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|files
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
specifier|synchronized
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nrtdir.deleteFile name="
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cache
operator|.
name|fileNameExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|cache
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|fileLength
specifier|public
specifier|synchronized
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|.
name|fileNameExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|cache
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
DECL|method|listCachedFiles
specifier|public
name|String
index|[]
name|listCachedFiles
parameter_list|()
block|{
return|return
name|cache
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nrtdir.createOutput name="
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doCacheWrite
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  to cache"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|in
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// This is fine: file may not exist
block|}
return|return
name|cache
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
name|cache
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// This is fine: file may not exist
block|}
return|return
name|in
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|fileNames
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nrtdir.sync files="
operator|+
name|fileNames
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|fileName
range|:
name|fileNames
control|)
block|{
name|unCache
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|sync
argument_list|(
name|fileNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NOTE: uncache is unnecessary for lucene's usage, as we always sync() before renaming.
name|unCache
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|in
operator|.
name|renameFile
argument_list|(
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
specifier|synchronized
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nrtdir.openInput name="
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cache
operator|.
name|fileNameExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  from cache"
argument_list|)
expr_stmt|;
block|}
return|return
name|cache
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
comment|/** Close this directory, which flushes any cached files    *  to the delegate and then closes the delegate. */
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
comment|// NOTE: technically we shouldn't have to do this, ie,
comment|// IndexWriter should have sync'd all files, but we do
comment|// it for defensive reasons... or in case the app is
comment|// doing something custom (creating outputs directly w/o
comment|// using IndexWriter):
for|for
control|(
name|String
name|fileName
range|:
name|cache
operator|.
name|listAll
argument_list|()
control|)
block|{
name|unCache
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Subclass can override this to customize logic; return    *  true if this file should be written to the RAMDirectory. */
DECL|method|doCacheWrite
specifier|protected
name|boolean
name|doCacheWrite
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": CACHE check merge=" + merge + " size=" + (merge==null ? 0 : merge.estimatedMergeBytes));
name|long
name|bytes
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|mergeInfo
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|=
name|context
operator|.
name|mergeInfo
operator|.
name|estimatedMergeBytes
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|flushInfo
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|=
name|context
operator|.
name|flushInfo
operator|.
name|estimatedSegmentSize
expr_stmt|;
block|}
return|return
operator|(
name|bytes
operator|<=
name|maxMergeSizeBytes
operator|)
operator|&&
operator|(
name|bytes
operator|+
name|cache
operator|.
name|ramBytesUsed
argument_list|()
operator|)
operator|<=
name|maxCachedBytes
return|;
block|}
DECL|field|uncacheLock
specifier|private
specifier|final
name|Object
name|uncacheLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|unCache
specifier|private
name|void
name|unCache
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Only let one thread uncache at a time; this only
comment|// happens during commit() or close():
synchronized|synchronized
init|(
name|uncacheLock
init|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nrtdir.unCache name="
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|cache
operator|.
name|fileNameExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
comment|// Another thread beat us...
return|return;
block|}
specifier|final
name|IOContext
name|context
init|=
name|IOContext
operator|.
name|DEFAULT
decl_stmt|;
specifier|final
name|IndexOutput
name|out
init|=
name|in
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|IndexInput
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
name|cache
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|in
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
comment|// Lock order: uncacheLock -> this
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Must sync here because other sync methods have
comment|// if (cache.fileNameExists(name)) { ... } else { ... }:
name|cache
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|cache
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
block|}
end_class

end_unit

