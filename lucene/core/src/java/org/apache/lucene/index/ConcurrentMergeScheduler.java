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
name|IOException
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
name|Comparator
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
name|util
operator|.
name|CollectionUtil
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
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/** A {@link MergeScheduler} that runs each merge using a  *  separate thread.  *  *<p>Specify the max number of threads that may run at  *  once, and the maximum number of simultaneous merges  *  with {@link #setMaxMergesAndThreads}.</p>  *  *<p>If the number of merges exceeds the max number of threads   *  then the largest merges are paused until one of the smaller  *  merges completes.</p>  *  *<p>If more than {@link #getMaxMergeCount} merges are  *  requested then this class will forcefully throttle the  *  incoming threads by pausing until one more more merges  *  complete.</p>  *  *<p>This class attempts to detect whether the index is  *  on rotational storage (traditional hard drive) or not  *  (e.g. solid-state disk) and changes the default max merge  *  and thread count accordingly.  This detection is currently  *  Linux-only, and relies on the OS to put the right value  *  into /sys/block/&lt;dev&gt;/block/rotational.  For all  *  other operating systems it currently assumes a rotational  *  disk for backwards compatibility.  To enable default  *  settings for spinning or solid state disks for such  *  operating systems, use {@link #setDefaultMaxMergesAndThreads(boolean)}.  */
end_comment

begin_class
DECL|class|ConcurrentMergeScheduler
specifier|public
class|class
name|ConcurrentMergeScheduler
extends|extends
name|MergeScheduler
block|{
comment|/** Dynamic default for {@code maxThreadCount} and {@code maxMergeCount},    *  used to detect whether the index is backed by an SSD or rotational disk and    *  set {@code maxThreadCount} accordingly.  If it's an SSD,    *  {@code maxThreadCount} is set to {@code max(1, min(3, cpuCoreCount/2))},    *  otherwise 1.  Note that detection only currently works on    *  Linux; other platforms will assume the index is not on an SSD. */
DECL|field|AUTO_DETECT_MERGES_AND_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|AUTO_DETECT_MERGES_AND_THREADS
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|mergeThreadPriority
specifier|private
name|int
name|mergeThreadPriority
init|=
operator|-
literal|1
decl_stmt|;
comment|/** List of currently active {@link MergeThread}s. */
DECL|field|mergeThreads
specifier|protected
specifier|final
name|List
argument_list|<
name|MergeThread
argument_list|>
name|mergeThreads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Max number of merge threads allowed to be running at
comment|// once.  When there are more merges then this, we
comment|// forcefully pause the larger ones, letting the smaller
comment|// ones run, up until maxMergeCount merges at which point
comment|// we forcefully pause incoming threads (that presumably
comment|// are the ones causing so much merging).
DECL|field|maxThreadCount
specifier|private
name|int
name|maxThreadCount
init|=
name|AUTO_DETECT_MERGES_AND_THREADS
decl_stmt|;
comment|// Max number of merges we accept before forcefully
comment|// throttling the incoming threads
DECL|field|maxMergeCount
specifier|private
name|int
name|maxMergeCount
init|=
name|AUTO_DETECT_MERGES_AND_THREADS
decl_stmt|;
comment|/** {@link Directory} that holds the index. */
DECL|field|dir
specifier|protected
name|Directory
name|dir
decl_stmt|;
comment|/** {@link IndexWriter} that owns this instance. */
DECL|field|writer
specifier|protected
name|IndexWriter
name|writer
decl_stmt|;
comment|/** How many {@link MergeThread}s have kicked off (this is use    *  to name them). */
DECL|field|mergeThreadCount
specifier|protected
name|int
name|mergeThreadCount
decl_stmt|;
comment|/** Sole constructor, with all settings set to default    *  values. */
DECL|method|ConcurrentMergeScheduler
specifier|public
name|ConcurrentMergeScheduler
parameter_list|()
block|{   }
comment|/**    * Expert: directly set the maximum number of merge threads and    * simultaneous merges allowed.    *     * @param maxMergeCount the max # simultaneous merges that are allowed.    *       If a merge is necessary yet we already have this many    *       threads running, the incoming thread (that is calling    *       add/updateDocument) will block until a merge thread    *       has completed.  Note that we will only run the    *       smallest<code>maxThreadCount</code> merges at a time.    * @param maxThreadCount the max # simultaneous merge threads that should    *       be running at once.  This must be&lt;=<code>maxMergeCount</code>    */
DECL|method|setMaxMergesAndThreads
specifier|public
specifier|synchronized
name|void
name|setMaxMergesAndThreads
parameter_list|(
name|int
name|maxMergeCount
parameter_list|,
name|int
name|maxThreadCount
parameter_list|)
block|{
if|if
condition|(
name|maxMergeCount
operator|==
name|AUTO_DETECT_MERGES_AND_THREADS
operator|&&
name|maxThreadCount
operator|==
name|AUTO_DETECT_MERGES_AND_THREADS
condition|)
block|{
comment|// OK
name|this
operator|.
name|maxMergeCount
operator|=
name|AUTO_DETECT_MERGES_AND_THREADS
expr_stmt|;
name|this
operator|.
name|maxThreadCount
operator|=
name|AUTO_DETECT_MERGES_AND_THREADS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maxMergeCount
operator|==
name|AUTO_DETECT_MERGES_AND_THREADS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"both maxMergeCount and maxThreadCount must be AUTO_DETECT_MERGES_AND_THREADS"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|maxThreadCount
operator|==
name|AUTO_DETECT_MERGES_AND_THREADS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"both maxMergeCount and maxThreadCount must be AUTO_DETECT_MERGES_AND_THREADS"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|maxThreadCount
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxThreadCount should be at least 1"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxMergeCount
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxMergeCount should be at least 1"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxThreadCount
operator|>
name|maxMergeCount
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxThreadCount should be<= maxMergeCount (= "
operator|+
name|maxMergeCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxThreadCount
operator|=
name|maxThreadCount
expr_stmt|;
name|this
operator|.
name|maxMergeCount
operator|=
name|maxMergeCount
expr_stmt|;
block|}
block|}
comment|/** Sets max merges and threads to proper defaults for rotational    *  or non-rotational storage.    *    * @param spins true to set defaults best for traditional rotatational storage (spinning disks),     *        else false (e.g. for solid-state disks)    */
DECL|method|setDefaultMaxMergesAndThreads
specifier|public
specifier|synchronized
name|void
name|setDefaultMaxMergesAndThreads
parameter_list|(
name|boolean
name|spins
parameter_list|)
block|{
if|if
condition|(
name|spins
condition|)
block|{
name|maxThreadCount
operator|=
literal|1
expr_stmt|;
name|maxMergeCount
operator|=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|maxThreadCount
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|3
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|maxMergeCount
operator|=
name|maxThreadCount
operator|+
literal|2
expr_stmt|;
block|}
block|}
comment|/** Returns {@code maxThreadCount}.    *    * @see #setMaxMergesAndThreads(int, int) */
DECL|method|getMaxThreadCount
specifier|public
specifier|synchronized
name|int
name|getMaxThreadCount
parameter_list|()
block|{
return|return
name|maxThreadCount
return|;
block|}
comment|/** See {@link #setMaxMergesAndThreads}. */
DECL|method|getMaxMergeCount
specifier|public
specifier|synchronized
name|int
name|getMaxMergeCount
parameter_list|()
block|{
return|return
name|maxMergeCount
return|;
block|}
comment|/** Return the priority that merge threads run at.  By    *  default the priority is 1 plus the priority of (ie,    *  slightly higher priority than) the first thread that    *  calls merge. */
DECL|method|getMergeThreadPriority
specifier|public
specifier|synchronized
name|int
name|getMergeThreadPriority
parameter_list|()
block|{
name|initMergeThreadPriority
argument_list|()
expr_stmt|;
return|return
name|mergeThreadPriority
return|;
block|}
comment|/** Set the base priority that merge threads run at.    *  Note that CMS may increase priority of some merge    *  threads beyond this base priority.  It's best not to    *  set this any higher than    *  Thread.MAX_PRIORITY-maxThreadCount, so that CMS has    *  room to set relative priority among threads.  */
DECL|method|setMergeThreadPriority
specifier|public
specifier|synchronized
name|void
name|setMergeThreadPriority
parameter_list|(
name|int
name|pri
parameter_list|)
block|{
if|if
condition|(
name|pri
operator|>
name|Thread
operator|.
name|MAX_PRIORITY
operator|||
name|pri
operator|<
name|Thread
operator|.
name|MIN_PRIORITY
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"priority must be in range "
operator|+
name|Thread
operator|.
name|MIN_PRIORITY
operator|+
literal|" .. "
operator|+
name|Thread
operator|.
name|MAX_PRIORITY
operator|+
literal|" inclusive"
argument_list|)
throw|;
name|mergeThreadPriority
operator|=
name|pri
expr_stmt|;
name|updateMergeThreads
argument_list|()
expr_stmt|;
block|}
comment|/** Sorts {@link MergeThread}s; larger merges come first. */
DECL|field|compareByMergeDocCount
specifier|protected
specifier|static
specifier|final
name|Comparator
argument_list|<
name|MergeThread
argument_list|>
name|compareByMergeDocCount
init|=
operator|new
name|Comparator
argument_list|<
name|MergeThread
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|MergeThread
name|t1
parameter_list|,
name|MergeThread
name|t2
parameter_list|)
block|{
specifier|final
name|MergePolicy
operator|.
name|OneMerge
name|m1
init|=
name|t1
operator|.
name|getCurrentMerge
argument_list|()
decl_stmt|;
specifier|final
name|MergePolicy
operator|.
name|OneMerge
name|m2
init|=
name|t2
operator|.
name|getCurrentMerge
argument_list|()
decl_stmt|;
specifier|final
name|int
name|c1
init|=
name|m1
operator|==
literal|null
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|m1
operator|.
name|totalDocCount
decl_stmt|;
specifier|final
name|int
name|c2
init|=
name|m2
operator|==
literal|null
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|m2
operator|.
name|totalDocCount
decl_stmt|;
return|return
name|c2
operator|-
name|c1
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Called whenever the running merges have changed, to pause and unpause    * threads. This method sorts the merge threads by their merge size in    * descending order and then pauses/unpauses threads from first to last --    * that way, smaller merges are guaranteed to run before larger ones.    */
DECL|method|updateMergeThreads
specifier|protected
specifier|synchronized
name|void
name|updateMergeThreads
parameter_list|()
block|{
comment|// Only look at threads that are alive& not in the
comment|// process of stopping (ie have an active merge):
specifier|final
name|List
argument_list|<
name|MergeThread
argument_list|>
name|activeMerges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|threadIdx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|threadIdx
operator|<
name|mergeThreads
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|MergeThread
name|mergeThread
init|=
name|mergeThreads
operator|.
name|get
argument_list|(
name|threadIdx
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|mergeThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
comment|// Prune any dead threads
name|mergeThreads
operator|.
name|remove
argument_list|(
name|threadIdx
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|mergeThread
operator|.
name|getCurrentMerge
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|activeMerges
operator|.
name|add
argument_list|(
name|mergeThread
argument_list|)
expr_stmt|;
block|}
name|threadIdx
operator|++
expr_stmt|;
block|}
comment|// Sort the merge threads in descending order.
name|CollectionUtil
operator|.
name|timSort
argument_list|(
name|activeMerges
argument_list|,
name|compareByMergeDocCount
argument_list|)
expr_stmt|;
name|int
name|pri
init|=
name|mergeThreadPriority
decl_stmt|;
specifier|final
name|int
name|activeMergeCount
init|=
name|activeMerges
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|threadIdx
operator|=
literal|0
init|;
name|threadIdx
operator|<
name|activeMergeCount
condition|;
name|threadIdx
operator|++
control|)
block|{
specifier|final
name|MergeThread
name|mergeThread
init|=
name|activeMerges
operator|.
name|get
argument_list|(
name|threadIdx
argument_list|)
decl_stmt|;
specifier|final
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|mergeThread
operator|.
name|getCurrentMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// pause the thread if maxThreadCount is smaller than the number of merge threads.
specifier|final
name|boolean
name|doPause
init|=
name|threadIdx
operator|<
name|activeMergeCount
operator|-
name|maxThreadCount
decl_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
if|if
condition|(
name|doPause
operator|!=
name|merge
operator|.
name|getPause
argument_list|()
condition|)
block|{
if|if
condition|(
name|doPause
condition|)
block|{
name|message
argument_list|(
literal|"pause thread "
operator|+
name|mergeThread
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
argument_list|(
literal|"unpause thread "
operator|+
name|mergeThread
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|doPause
operator|!=
name|merge
operator|.
name|getPause
argument_list|()
condition|)
block|{
name|merge
operator|.
name|setPause
argument_list|(
name|doPause
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|doPause
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"set priority of merge thread "
operator|+
name|mergeThread
operator|.
name|getName
argument_list|()
operator|+
literal|" to "
operator|+
name|pri
argument_list|)
expr_stmt|;
block|}
name|mergeThread
operator|.
name|setThreadPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
name|pri
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|,
literal|1
operator|+
name|pri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns true if verbosing is enabled. This method is usually used in    * conjunction with {@link #message(String)}, like that:    *     *<pre class="prettyprint">    * if (verbose()) {    *   message(&quot;your message&quot;);    * }    *</pre>    */
DECL|method|verbose
specifier|protected
name|boolean
name|verbose
parameter_list|()
block|{
return|return
name|writer
operator|!=
literal|null
operator|&&
name|writer
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"CMS"
argument_list|)
return|;
block|}
comment|/**    * Outputs the given message - this method assumes {@link #verbose()} was    * called and returned true.    */
DECL|method|message
specifier|protected
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|writer
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"CMS"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|initMergeThreadPriority
specifier|private
specifier|synchronized
name|void
name|initMergeThreadPriority
parameter_list|()
block|{
if|if
condition|(
name|mergeThreadPriority
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Default to slightly higher priority than our
comment|// calling thread
name|mergeThreadPriority
operator|=
literal|1
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
if|if
condition|(
name|mergeThreadPriority
operator|>
name|Thread
operator|.
name|MAX_PRIORITY
condition|)
name|mergeThreadPriority
operator|=
name|Thread
operator|.
name|MAX_PRIORITY
expr_stmt|;
block|}
block|}
DECL|method|initMaxMergesAndThreads
specifier|private
specifier|synchronized
name|void
name|initMaxMergesAndThreads
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|maxThreadCount
operator|==
name|AUTO_DETECT_MERGES_AND_THREADS
condition|)
block|{
assert|assert
name|writer
operator|!=
literal|null
assert|;
name|boolean
name|spins
init|=
name|IOUtils
operator|.
name|spins
argument_list|(
name|writer
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|setDefaultMaxMergesAndThreads
argument_list|(
name|spins
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"initMaxMergesAndThreads spins="
operator|+
name|spins
operator|+
literal|" maxThreadCount="
operator|+
name|maxThreadCount
operator|+
literal|" maxMergeCount="
operator|+
name|maxMergeCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|sync
argument_list|()
expr_stmt|;
block|}
comment|/** Wait for any running merge threads to finish. This call is not interruptible as used by {@link #close()}. */
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|()
block|{
name|boolean
name|interrupted
init|=
literal|false
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|MergeThread
name|toSync
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|MergeThread
name|t
range|:
name|mergeThreads
control|)
block|{
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|toSync
operator|=
name|t
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|toSync
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|toSync
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// ignore this Exception, we will retry until all threads are dead
name|interrupted
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
comment|// finally, restore interrupt status:
if|if
condition|(
name|interrupted
condition|)
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of merge threads that are alive. Note that this number    * is&le; {@link #mergeThreads} size.    */
DECL|method|mergeThreadCount
specifier|protected
specifier|synchronized
name|int
name|mergeThreadCount
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MergeThread
name|mt
range|:
name|mergeThreads
control|)
block|{
if|if
condition|(
name|mt
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|mt
operator|.
name|getCurrentMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|!=
literal|null
operator|&&
name|merge
operator|.
name|isAborted
argument_list|()
operator|==
literal|false
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
specifier|synchronized
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergeTrigger
name|trigger
parameter_list|,
name|boolean
name|newMergesFound
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|initMergeThreadPriority
argument_list|()
expr_stmt|;
name|initMaxMergesAndThreads
argument_list|()
expr_stmt|;
name|dir
operator|=
name|writer
operator|.
name|getDirectory
argument_list|()
expr_stmt|;
comment|// First, quickly run through the newly proposed merges
comment|// and add any orthogonal merges (ie a merge not
comment|// involving segments already pending to be merged) to
comment|// the queue.  If we are way behind on merging, many of
comment|// these newly proposed merges will likely already be
comment|// registered.
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"now merge"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"  index: "
operator|+
name|writer
operator|.
name|segString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Iterate, pulling from the IndexWriter's queue of
comment|// pending merges, until it's empty:
while|while
condition|(
literal|true
condition|)
block|{
name|maybeStall
argument_list|()
expr_stmt|;
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|writer
operator|.
name|getNextMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"  no more merges pending; now return"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"  consider merge "
operator|+
name|writer
operator|.
name|segString
argument_list|(
name|merge
operator|.
name|segments
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// OK to spawn a new merge thread to handle this
comment|// merge:
specifier|final
name|MergeThread
name|merger
init|=
name|getMergeThread
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
decl_stmt|;
name|mergeThreads
operator|.
name|add
argument_list|(
name|merger
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"    launch new thread ["
operator|+
name|merger
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|merger
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Must call this after starting the thread else
comment|// the new thread is removed from mergeThreads
comment|// (since it's not alive yet):
name|updateMergeThreads
argument_list|()
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
name|writer
operator|.
name|mergeFinish
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** This is invoked by {@link #merge} to possibly stall the incoming    *  thread when there are too many merges running or pending.  The     *  default behavior is to force this thread, which is producing too    *  many segments for merging to keep up, to wait until merges catch    *  up. Applications that can take other less drastic measures, such    *  as limiting how many threads are allowed to index, can do nothing    *  here and throttle elsewhere. */
DECL|method|maybeStall
specifier|protected
specifier|synchronized
name|void
name|maybeStall
parameter_list|()
block|{
name|long
name|startStallTime
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|writer
operator|.
name|hasPendingMerges
argument_list|()
operator|&&
name|mergeThreadCount
argument_list|()
operator|>=
name|maxMergeCount
condition|)
block|{
comment|// This means merging has fallen too far behind: we
comment|// have already created maxMergeCount threads, and
comment|// now there's at least one more merge pending.
comment|// Note that only maxThreadCount of
comment|// those created merge threads will actually be
comment|// running; the rest will be paused (see
comment|// updateMergeThreads).  We stall this producer
comment|// thread to prevent creation of new segments,
comment|// until merging has caught up:
name|startStallTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"    too many merges; stalling..."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// Only wait 0.25 seconds, so if all merges are aborted (by IW.rollback) we notice:
name|wait
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
if|if
condition|(
name|startStallTime
operator|!=
literal|0
condition|)
block|{
name|message
argument_list|(
literal|"  stalled for "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startStallTime
operator|)
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Does the actual merge, by calling {@link IndexWriter#merge} */
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
comment|/** Create and return a new MergeThread */
DECL|method|getMergeThread
specifier|protected
specifier|synchronized
name|MergeThread
name|getMergeThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MergeThread
name|thread
init|=
operator|new
name|MergeThread
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setThreadPriority
argument_list|(
name|mergeThreadPriority
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"Lucene Merge Thread #"
operator|+
name|mergeThreadCount
operator|++
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
comment|/** Runs a merge thread, which may run one or more merges    *  in sequence. */
DECL|class|MergeThread
specifier|protected
class|class
name|MergeThread
extends|extends
name|Thread
block|{
DECL|field|tWriter
name|IndexWriter
name|tWriter
decl_stmt|;
DECL|field|startMerge
name|MergePolicy
operator|.
name|OneMerge
name|startMerge
decl_stmt|;
DECL|field|runningMerge
name|MergePolicy
operator|.
name|OneMerge
name|runningMerge
decl_stmt|;
DECL|field|done
specifier|private
specifier|volatile
name|boolean
name|done
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MergeThread
specifier|public
name|MergeThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergePolicy
operator|.
name|OneMerge
name|startMerge
parameter_list|)
block|{
name|this
operator|.
name|tWriter
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|startMerge
operator|=
name|startMerge
expr_stmt|;
block|}
comment|/** Record the currently running merge. */
DECL|method|setRunningMerge
specifier|public
specifier|synchronized
name|void
name|setRunningMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
block|{
name|runningMerge
operator|=
name|merge
expr_stmt|;
block|}
comment|/** Return the currently running merge. */
DECL|method|getRunningMerge
specifier|public
specifier|synchronized
name|MergePolicy
operator|.
name|OneMerge
name|getRunningMerge
parameter_list|()
block|{
return|return
name|runningMerge
return|;
block|}
comment|/** Return the current merge, or null if this {@code      *  MergeThread} is done. */
DECL|method|getCurrentMerge
specifier|public
specifier|synchronized
name|MergePolicy
operator|.
name|OneMerge
name|getCurrentMerge
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|runningMerge
operator|!=
literal|null
condition|)
block|{
return|return
name|runningMerge
return|;
block|}
else|else
block|{
return|return
name|startMerge
return|;
block|}
block|}
comment|/** Set the priority of this thread. */
DECL|method|setThreadPriority
specifier|public
name|void
name|setThreadPriority
parameter_list|(
name|int
name|pri
parameter_list|)
block|{
try|try
block|{
name|setPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// Strangely, Sun's JDK 1.5 on Linux sometimes
comment|// throws NPE out of here...
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
comment|// Ignore this because we will still run fine with
comment|// normal thread priority
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// First time through the while loop we do the merge
comment|// that we were started with:
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|this
operator|.
name|startMerge
decl_stmt|;
try|try
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"  merge thread: start"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|setRunningMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
name|doMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
comment|// Subsequent times through the loop we do any new
comment|// merge that writer says is necessary:
name|merge
operator|=
name|tWriter
operator|.
name|getNextMerge
argument_list|()
expr_stmt|;
comment|// Notify here in case any threads were stalled;
comment|// they will notice that the pending merge has
comment|// been pulled and possibly resume:
synchronized|synchronized
init|(
name|ConcurrentMergeScheduler
operator|.
name|this
init|)
block|{
name|ConcurrentMergeScheduler
operator|.
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|merge
operator|!=
literal|null
condition|)
block|{
name|updateMergeThreads
argument_list|()
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"  merge thread: do another merge "
operator|+
name|tWriter
operator|.
name|segString
argument_list|(
name|merge
operator|.
name|segments
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|verbose
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"  merge thread: done"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|exc
parameter_list|)
block|{
comment|// Ignore the exception if it was due to abort:
if|if
condition|(
operator|!
operator|(
name|exc
operator|instanceof
name|MergePolicy
operator|.
name|MergeAbortedException
operator|)
condition|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": CMS: exc");
comment|//exc.printStackTrace(System.out);
if|if
condition|(
operator|!
name|suppressExceptions
condition|)
block|{
comment|// suppressExceptions is normally only set during
comment|// testing.
name|handleMergeException
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|done
operator|=
literal|true
expr_stmt|;
synchronized|synchronized
init|(
name|ConcurrentMergeScheduler
operator|.
name|this
init|)
block|{
name|updateMergeThreads
argument_list|()
expr_stmt|;
name|ConcurrentMergeScheduler
operator|.
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Called when an exception is hit in a background merge    *  thread */
DECL|method|handleMergeException
specifier|protected
name|void
name|handleMergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|)
block|{
try|try
block|{
comment|// When an exception is hit during merge, IndexWriter
comment|// removes any partial files and then allows another
comment|// merge to run.  If whatever caused the error is not
comment|// transient then the exception will keep happening,
comment|// so, we sleep here to avoid saturating CPU in such
comment|// cases:
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|MergePolicy
operator|.
name|MergeException
argument_list|(
name|exc
argument_list|,
name|dir
argument_list|)
throw|;
block|}
DECL|field|suppressExceptions
specifier|private
name|boolean
name|suppressExceptions
decl_stmt|;
comment|/** Used for testing */
DECL|method|setSuppressExceptions
name|void
name|setSuppressExceptions
parameter_list|()
block|{
name|suppressExceptions
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Used for testing */
DECL|method|clearSuppressExceptions
name|void
name|clearSuppressExceptions
parameter_list|()
block|{
name|suppressExceptions
operator|=
literal|false
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxThreadCount="
argument_list|)
operator|.
name|append
argument_list|(
name|maxThreadCount
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxMergeCount="
argument_list|)
operator|.
name|append
argument_list|(
name|maxMergeCount
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergeThreadPriority="
argument_list|)
operator|.
name|append
argument_list|(
name|mergeThreadPriority
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

