begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * The {@link TimeLimitingCollector} is used to timeout search requests that  * take longer than the maximum allowed search time limit. After this time is  * exceeded, the search thread is stopped by throwing a  * {@link TimeExceededException}.  */
end_comment

begin_class
DECL|class|TimeLimitingCollector
specifier|public
class|class
name|TimeLimitingCollector
extends|extends
name|Collector
block|{
comment|/**     * Default timer resolution.    * @see #setResolution(long)     */
DECL|field|DEFAULT_RESOLUTION
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RESOLUTION
init|=
literal|20
decl_stmt|;
comment|/**    * Default for {@link #isGreedy()}.    * @see #isGreedy()    */
DECL|field|DEFAULT_GREEDY
specifier|public
name|boolean
name|DEFAULT_GREEDY
init|=
literal|false
decl_stmt|;
DECL|field|resolution
specifier|private
specifier|static
name|long
name|resolution
init|=
name|DEFAULT_RESOLUTION
decl_stmt|;
DECL|field|greedy
specifier|private
name|boolean
name|greedy
init|=
name|DEFAULT_GREEDY
decl_stmt|;
DECL|class|TimerThread
specifier|private
specifier|static
specifier|final
class|class
name|TimerThread
extends|extends
name|Thread
block|{
comment|// NOTE: we can avoid explicit synchronization here for several reasons:
comment|// * updates to volatile long variables are atomic
comment|// * only single thread modifies this value
comment|// * use of volatile keyword ensures that it does not reside in
comment|//   a register, but in main memory (so that changes are visible to
comment|//   other threads).
comment|// * visibility of changes does not need to be instantanous, we can
comment|//   afford losing a tick or two.
comment|//
comment|// See section 17 of the Java Language Specification for details.
DECL|field|time
specifier|private
specifier|volatile
name|long
name|time
init|=
literal|0
decl_stmt|;
comment|/**      * TimerThread provides a pseudo-clock service to all searching      * threads, so that they can count elapsed time with less overhead      * than repeatedly calling System.currentTimeMillis.  A single      * thread should be created to be used for all searches.      */
DECL|method|TimerThread
specifier|private
name|TimerThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"TimeLimitedCollector timer thread"
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
comment|// TODO: Use System.nanoTime() when Lucene moves to Java SE 5.
name|time
operator|+=
name|resolution
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Get the timer value in milliseconds.      */
DECL|method|getMilliseconds
specifier|public
name|long
name|getMilliseconds
parameter_list|()
block|{
return|return
name|time
return|;
block|}
block|}
comment|/** Thrown when elapsed search time exceeds allowed search time. */
DECL|class|TimeExceededException
specifier|public
specifier|static
class|class
name|TimeExceededException
extends|extends
name|RuntimeException
block|{
DECL|field|timeAllowed
specifier|private
name|long
name|timeAllowed
decl_stmt|;
DECL|field|timeElapsed
specifier|private
name|long
name|timeElapsed
decl_stmt|;
DECL|field|lastDocCollected
specifier|private
name|int
name|lastDocCollected
decl_stmt|;
DECL|method|TimeExceededException
specifier|private
name|TimeExceededException
parameter_list|(
name|long
name|timeAllowed
parameter_list|,
name|long
name|timeElapsed
parameter_list|,
name|int
name|lastDocCollected
parameter_list|)
block|{
name|super
argument_list|(
literal|"Elapsed time: "
operator|+
name|timeElapsed
operator|+
literal|"Exceeded allowed search time: "
operator|+
name|timeAllowed
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeAllowed
operator|=
name|timeAllowed
expr_stmt|;
name|this
operator|.
name|timeElapsed
operator|=
name|timeElapsed
expr_stmt|;
name|this
operator|.
name|lastDocCollected
operator|=
name|lastDocCollected
expr_stmt|;
block|}
comment|/** Returns allowed time (milliseconds). */
DECL|method|getTimeAllowed
specifier|public
name|long
name|getTimeAllowed
parameter_list|()
block|{
return|return
name|timeAllowed
return|;
block|}
comment|/** Returns elapsed time (milliseconds). */
DECL|method|getTimeElapsed
specifier|public
name|long
name|getTimeElapsed
parameter_list|()
block|{
return|return
name|timeElapsed
return|;
block|}
comment|/** Returns last doc that was collected when the search time exceeded. */
DECL|method|getLastDocCollected
specifier|public
name|int
name|getLastDocCollected
parameter_list|()
block|{
return|return
name|lastDocCollected
return|;
block|}
block|}
comment|// Declare and initialize a single static timer thread to be used by
comment|// all TimeLimitedCollector instances.  The JVM assures that
comment|// this only happens once.
DECL|field|TIMER_THREAD
specifier|private
specifier|final
specifier|static
name|TimerThread
name|TIMER_THREAD
init|=
operator|new
name|TimerThread
argument_list|()
decl_stmt|;
static|static
block|{
name|TIMER_THREAD
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|field|t0
specifier|private
specifier|final
name|long
name|t0
decl_stmt|;
DECL|field|timeout
specifier|private
specifier|final
name|long
name|timeout
decl_stmt|;
DECL|field|collector
specifier|private
specifier|final
name|Collector
name|collector
decl_stmt|;
comment|/**    * Create a TimeLimitedCollector wrapper over another {@link Collector} with a specified timeout.    * @param collector the wrapped {@link Collector}    * @param timeAllowed max time allowed for collecting hits after which {@link TimeExceededException} is thrown    */
DECL|method|TimeLimitingCollector
specifier|public
name|TimeLimitingCollector
parameter_list|(
specifier|final
name|Collector
name|collector
parameter_list|,
specifier|final
name|long
name|timeAllowed
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|t0
operator|=
name|TIMER_THREAD
operator|.
name|getMilliseconds
argument_list|()
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|t0
operator|+
name|timeAllowed
expr_stmt|;
block|}
comment|/**     * Return the timer resolution.    * @see #setResolution(long)    */
DECL|method|getResolution
specifier|public
specifier|static
name|long
name|getResolution
parameter_list|()
block|{
return|return
name|resolution
return|;
block|}
comment|/**    * Set the timer resolution.    * The default timer resolution is 20 milliseconds.     * This means that a search required to take no longer than     * 800 milliseconds may be stopped after 780 to 820 milliseconds.    *<br>Note that:     *<ul>    *<li>Finer (smaller) resolution is more accurate but less efficient.</li>    *<li>Setting resolution to less than 5 milliseconds will be silently modified to 5 milliseconds.</li>    *<li>Setting resolution smaller than current resolution might take effect only after current     * resolution. (Assume current resolution of 20 milliseconds is modified to 5 milliseconds,     * then it can take up to 20 milliseconds for the change to have effect.</li>    *</ul>          */
DECL|method|setResolution
specifier|public
specifier|static
name|void
name|setResolution
parameter_list|(
name|long
name|newResolution
parameter_list|)
block|{
name|resolution
operator|=
name|Math
operator|.
name|max
argument_list|(
name|newResolution
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// 5 milliseconds is about the minimum reasonable time for a Object.wait(long) call.
block|}
comment|/**    * Checks if this time limited collector is greedy in collecting the last hit.    * A non greedy collector, upon a timeout, would throw a {@link TimeExceededException}     * without allowing the wrapped collector to collect current doc. A greedy one would     * first allow the wrapped hit collector to collect current doc and only then     * throw a {@link TimeExceededException}.    * @see #setGreedy(boolean)    */
DECL|method|isGreedy
specifier|public
name|boolean
name|isGreedy
parameter_list|()
block|{
return|return
name|greedy
return|;
block|}
comment|/**    * Sets whether this time limited collector is greedy.    * @param greedy true to make this time limited greedy    * @see #isGreedy()    */
DECL|method|setGreedy
specifier|public
name|void
name|setGreedy
parameter_list|(
name|boolean
name|greedy
parameter_list|)
block|{
name|this
operator|.
name|greedy
operator|=
name|greedy
expr_stmt|;
block|}
comment|/**    * Calls {@link Collector#collect(int)} on the decorated {@link Collector}    * unless the allowed time has passed, in which case it throws an exception.    *     * @throws TimeExceededException    *           if the time allowed has exceeded.    */
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|time
init|=
name|TIMER_THREAD
operator|.
name|getMilliseconds
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeout
operator|<
name|time
condition|)
block|{
if|if
condition|(
name|greedy
condition|)
block|{
comment|//System.out.println(this+"  greedy: before failing, collecting doc: "+doc+"  "+(time-t0));
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println(this+"  failing on:  "+doc+"  "+(time-t0));
throw|throw
operator|new
name|TimeExceededException
argument_list|(
name|timeout
operator|-
name|t0
argument_list|,
name|time
operator|-
name|t0
argument_list|,
name|doc
argument_list|)
throw|;
block|}
comment|//System.out.println(this+"  collecting: "+doc+"  "+(time-t0));
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|base
argument_list|)
expr_stmt|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

