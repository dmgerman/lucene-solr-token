begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|State
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|SimpleLoggerManager
import|;
end_import

begin_comment
comment|/**  * this monitor takes a sample of every thread every x milliseconds,  * and logs a lot of information. In the near past it has evolved into the multi  * purpose monitoring and maintenance facility.  * At the moment it prints status information  * to log files and to the console  * @TODO this can be done better. Probably with an agent where different services  * can be registered to be called every X seconds  * @version $Id$  */
end_comment

begin_class
DECL|class|ThreadMonitor
specifier|public
class|class
name|ThreadMonitor
extends|extends
name|Observable
implements|implements
name|Runnable
block|{
comment|/**      * a reference to the thread pool that's gonna be observed      */
DECL|field|threadPool
specifier|private
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|class|Sample
class|class
name|Sample
block|{
DECL|field|bytesRead
name|long
name|bytesRead
decl_stmt|;
DECL|field|docsRead
name|long
name|docsRead
decl_stmt|;
DECL|field|time
name|long
name|time
decl_stmt|;
DECL|method|Sample
specifier|public
name|Sample
parameter_list|(
name|long
name|bytesRead
parameter_list|,
name|long
name|docsRead
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|bytesRead
operator|=
name|bytesRead
expr_stmt|;
name|this
operator|.
name|docsRead
operator|=
name|docsRead
expr_stmt|;
name|this
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
block|}
DECL|field|bytesReadPerPeriod
name|ArrayList
name|bytesReadPerPeriod
decl_stmt|;
comment|/**      * Zeit zwischen den Messungen      */
DECL|field|sampleDelta
name|int
name|sampleDelta
decl_stmt|;
comment|/**      * the thread where this monitor runs in. Will run with high priority      */
DECL|field|thread
name|Thread
name|thread
decl_stmt|;
DECL|field|urlVisitedFilter
name|URLVisitedFilter
name|urlVisitedFilter
decl_stmt|;
DECL|field|urlScopeFilter
name|URLScopeFilter
name|urlScopeFilter
decl_stmt|;
comment|//    DNSResolver dnsResolver;
DECL|field|reFilter
name|RobotExclusionFilter
name|reFilter
decl_stmt|;
DECL|field|messageHandler
name|MessageHandler
name|messageHandler
decl_stmt|;
DECL|field|urlLengthFilter
name|URLLengthFilter
name|urlLengthFilter
decl_stmt|;
DECL|field|hostManager
name|HostManager
name|hostManager
decl_stmt|;
DECL|field|KBYTE
specifier|public
specifier|final
specifier|static
name|double
name|KBYTE
init|=
literal|1024
decl_stmt|;
DECL|field|MBYTE
specifier|public
specifier|final
specifier|static
name|double
name|MBYTE
init|=
literal|1024
operator|*
name|KBYTE
decl_stmt|;
DECL|field|ONEGBYTE
specifier|public
specifier|final
specifier|static
name|double
name|ONEGBYTE
init|=
literal|1024
operator|*
name|MBYTE
decl_stmt|;
DECL|method|formatBytes
name|String
name|formatBytes
parameter_list|(
name|long
name|lbytes
parameter_list|)
block|{
name|double
name|bytes
init|=
operator|(
name|double
operator|)
name|lbytes
decl_stmt|;
if|if
condition|(
name|bytes
operator|>=
name|ONEGBYTE
condition|)
block|{
return|return
name|fractionFormat
operator|.
name|format
argument_list|(
operator|(
name|bytes
operator|/
name|ONEGBYTE
operator|)
argument_list|)
operator|+
literal|" GB"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|>=
name|MBYTE
condition|)
block|{
return|return
name|fractionFormat
operator|.
name|format
argument_list|(
name|bytes
operator|/
name|MBYTE
argument_list|)
operator|+
literal|" MB"
return|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|>=
name|KBYTE
condition|)
block|{
return|return
name|fractionFormat
operator|.
name|format
argument_list|(
name|bytes
operator|/
name|KBYTE
argument_list|)
operator|+
literal|" KB"
return|;
block|}
else|else
block|{
return|return
name|fractionFormat
operator|.
name|format
argument_list|(
name|bytes
argument_list|)
operator|+
literal|" Bytes"
return|;
block|}
block|}
comment|/**      * a logfile where status information is posted      * FIXME: put that in a seperate class (double code in FetcherTask)      */
DECL|field|logWriter
name|PrintWriter
name|logWriter
decl_stmt|;
DECL|field|formatter
specifier|private
name|SimpleDateFormat
name|formatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"hh:mm:ss:SSSS"
argument_list|)
decl_stmt|;
DECL|field|fractionFormat
specifier|private
name|DecimalFormat
name|fractionFormat
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"0.00"
argument_list|)
decl_stmt|;
DECL|field|startTime
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|log
specifier|private
name|void
name|log
parameter_list|(
name|String
name|text
parameter_list|)
block|{
try|try
block|{
name|logWriter
operator|.
name|println
argument_list|(
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|";"
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|+
literal|";"
operator|+
name|text
argument_list|)
expr_stmt|;
name|logWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Couldn't write to logfile"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * construct the monitor gets a reference to all monitored filters      * @param threadPool  the pool to be observed      * @param sampleDelta time in ms between samples      */
DECL|method|ThreadMonitor
specifier|public
name|ThreadMonitor
parameter_list|(
name|URLLengthFilter
name|urlLengthFilter
parameter_list|,
name|URLVisitedFilter
name|urlVisitedFilter
parameter_list|,
name|URLScopeFilter
name|urlScopeFilter
parameter_list|,
comment|/*DNSResolver dnsResolver,*/
name|RobotExclusionFilter
name|reFilter
parameter_list|,
name|MessageHandler
name|messageHandler
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|HostManager
name|hostManager
parameter_list|,
name|int
name|sampleDelta
parameter_list|)
block|{
name|this
operator|.
name|urlLengthFilter
operator|=
name|urlLengthFilter
expr_stmt|;
name|this
operator|.
name|urlVisitedFilter
operator|=
name|urlVisitedFilter
expr_stmt|;
name|this
operator|.
name|urlScopeFilter
operator|=
name|urlScopeFilter
expr_stmt|;
comment|/* this.dnsResolver = dnsResolver;*/
name|this
operator|.
name|hostManager
operator|=
name|hostManager
expr_stmt|;
name|this
operator|.
name|reFilter
operator|=
name|reFilter
expr_stmt|;
name|this
operator|.
name|messageHandler
operator|=
name|messageHandler
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|bytesReadPerPeriod
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|sampleDelta
operator|=
name|sampleDelta
expr_stmt|;
name|this
operator|.
name|thread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"ThreadMonitor"
argument_list|)
expr_stmt|;
name|this
operator|.
name|thread
operator|.
name|setPriority
argument_list|(
literal|7
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|logDir
init|=
operator|new
name|File
argument_list|(
literal|"logs"
argument_list|)
decl_stmt|;
name|logDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|logWriter
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
literal|"logs/ThreadMonitor.log"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Couldn't create logfile (ThreadMonitor)"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * java.lang.Threads run method. To be invoked via start()      * the monitor's main thread takes the samples every sampleDelta ms      * Since Java is not real time, it remembers      */
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|nothingReadCount
init|=
literal|0
decl_stmt|;
name|long
name|lastPeriodBytesRead
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|monitorRunCount
init|=
literal|0
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
argument_list|(
literal|"time;overallBytesRead;overallTasksRun;urlsQueued;urlsWaiting;isWorkingOnMessage;urlsScopeFiltered;urlsVisitedFiltered;urlsREFiltered;memUsed;memFree;totalMem;nrHosts;visitedSize;visitedStringSize;urlLengthFiltered"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
try|try
block|{
name|thread
operator|.
name|sleep
argument_list|(
name|sampleDelta
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
name|Iterator
name|threadIterator
init|=
name|threadPool
operator|.
name|getThreadIterator
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|StringBuffer
name|bytesReadString
init|=
operator|new
name|StringBuffer
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|StringBuffer
name|rawBytesReadString
init|=
operator|new
name|StringBuffer
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|StringBuffer
name|tasksRunString
init|=
operator|new
name|StringBuffer
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|long
name|overallBytesRead
init|=
literal|0
decl_stmt|;
name|long
name|overallTasksRun
init|=
literal|0
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
comment|//System.out.print("\f");
comment|/*while(!finished)                 {                     boolean restart = false;*/
name|boolean
name|allThreadsIdle
init|=
literal|true
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|500
argument_list|)
decl_stmt|;
while|while
condition|(
name|threadIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FetcherThread
name|thread
init|=
operator|(
name|FetcherThread
operator|)
name|threadIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|totalBytesRead
init|=
name|thread
operator|.
name|getTotalBytesRead
argument_list|()
decl_stmt|;
name|overallBytesRead
operator|+=
name|totalBytesRead
expr_stmt|;
name|bytesReadString
operator|.
name|append
argument_list|(
name|formatBytes
argument_list|(
name|totalBytesRead
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|rawBytesReadString
operator|.
name|append
argument_list|(
name|totalBytesRead
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|long
name|tasksRun
init|=
name|thread
operator|.
name|getTotalTasksRun
argument_list|()
decl_stmt|;
name|overallTasksRun
operator|+=
name|tasksRun
expr_stmt|;
name|tasksRunString
operator|.
name|append
argument_list|(
name|tasksRun
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
comment|// check task status
name|State
name|state
init|=
name|thread
operator|.
name|getTaskState
argument_list|()
decl_stmt|;
comment|//StringBuffer sb = new StringBuffer(200);
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"["
operator|+
name|thread
operator|.
name|getThreadNumber
argument_list|()
operator|+
literal|"] "
operator|+
name|state
operator|.
name|getState
argument_list|()
operator|+
literal|" for "
operator|+
operator|(
name|now
operator|-
name|state
operator|.
name|getStateSince
argument_list|()
operator|)
operator|+
literal|" ms "
operator|+
operator|(
name|state
operator|.
name|getInfo
argument_list|()
operator|!=
literal|null
condition|?
literal|"("
operator|+
name|state
operator|.
name|getInfo
argument_list|()
operator|+
literal|")"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|state
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|FetcherThread
operator|.
name|STATE_IDLE
argument_list|)
operator|)
condition|)
block|{
comment|//if(allThreadsIdle) System.out.println("(not all threads are idle, '"+state.getState()+"' != '"+FetcherThread.STATE_IDLE+"')");
name|allThreadsIdle
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|state
operator|.
name|equals
argument_list|(
name|FetcherTask
operator|.
name|FT_CONNECTING
argument_list|)
operator|)
operator|||
operator|(
name|state
operator|.
name|equals
argument_list|(
name|FetcherTask
operator|.
name|FT_GETTING
argument_list|)
operator|)
operator|||
operator|(
name|state
operator|.
name|equals
argument_list|(
name|FetcherTask
operator|.
name|FT_READING
argument_list|)
operator|)
operator|||
operator|(
name|state
operator|.
name|equals
argument_list|(
name|FetcherTask
operator|.
name|FT_CLOSING
argument_list|)
operator|)
operator|)
operator|&&
operator|(
operator|(
name|now
operator|-
name|state
operator|.
name|getStateSince
argument_list|()
operator|)
operator|>
literal|160000
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****Restarting Thread "
operator|+
name|thread
operator|.
name|getThreadNumber
argument_list|()
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|restartThread
argument_list|(
name|thread
operator|.
name|getThreadNumber
argument_list|()
argument_list|)
expr_stmt|;
break|break;
comment|// Iterator is invalid
block|}
block|}
comment|/*if(restart)                 {                     continue;                 }                 finished = true;                 }*/
comment|/*                 if(overallBytesRead == lastPeriodBytesRead)                 {                     *                     disabled kickout feature - cm                      nothingReadCount ++;                    System.out.println("Anomaly: nothing read during the last period(s). " + (20-nothingReadCount+1) + " periods to exit");                     if(nothingReadCount> 20)  // nothing happens anymore                     {                         log("Ending");                         System.out.println("End at " + new Date().toString());                         // print some information                         System.exit(0);                     }                   }                 else                 {                     nothingReadCount = 0;                 }*/
name|lastPeriodBytesRead
operator|=
name|overallBytesRead
expr_stmt|;
comment|//State reState = new State("hhh"); //reFilter.getState();
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//System.out.println(sb + "Robot-Excl.Filter State: " + reState.getState() + " since " + (now-reState.getStateSince()) + " ms " + (reState.getInfo() != null ? " at " + reState.getInfo() : ""));
name|addSample
argument_list|(
operator|new
name|Sample
argument_list|(
name|overallBytesRead
argument_list|,
name|overallTasksRun
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|nrHosts
init|=
operator|(
operator|(
name|FetcherTaskQueue
operator|)
name|threadPool
operator|.
name|getTaskQueue
argument_list|()
operator|)
operator|.
name|getNumHosts
argument_list|()
decl_stmt|;
name|int
name|visitedSize
init|=
name|urlVisitedFilter
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|visitedStringSize
init|=
name|urlVisitedFilter
operator|.
name|getStringSize
argument_list|()
decl_stmt|;
name|double
name|bytesPerSecond
init|=
name|getAverageBytesRead
argument_list|()
decl_stmt|;
name|double
name|docsPerSecond
init|=
name|getAverageDocsRead
argument_list|()
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"\nBytes total:          "
operator|+
name|formatBytes
argument_list|(
name|overallBytesRead
argument_list|)
operator|+
literal|"  ("
operator|+
name|formatBytes
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
operator|(
name|double
operator|)
name|overallBytesRead
operator|)
operator|*
literal|1000
operator|/
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
argument_list|)
operator|+
literal|" per second since start)"
operator|+
literal|"\nBytes per Second:     "
operator|+
name|formatBytes
argument_list|(
operator|(
name|int
operator|)
name|bytesPerSecond
argument_list|)
operator|+
literal|" (50 secs)"
operator|+
literal|"\nDocs per Second:      "
operator|+
name|docsPerSecond
operator|+
literal|"\nBytes per Thread:     "
operator|+
name|bytesReadString
argument_list|)
expr_stmt|;
name|double
name|docsPerSecondTotal
init|=
operator|(
operator|(
name|double
operator|)
name|overallTasksRun
operator|)
operator|*
literal|1000
operator|/
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"Docs read total:      "
operator|+
name|overallTasksRun
operator|+
literal|"    Docs/s: "
operator|+
name|fractionFormat
operator|.
name|format
argument_list|(
name|docsPerSecondTotal
argument_list|)
operator|+
literal|"\nDocs p.thread:        "
operator|+
name|tasksRunString
argument_list|)
expr_stmt|;
name|long
name|memUsed
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
operator|-
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
name|long
name|memFree
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
name|long
name|totalMem
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"Mem used:             "
operator|+
name|formatBytes
argument_list|(
name|memUsed
argument_list|)
operator|+
literal|", free: "
operator|+
name|formatBytes
argument_list|(
name|memFree
argument_list|)
operator|+
literal|"     total VM: "
operator|+
name|totalMem
argument_list|)
expr_stmt|;
name|int
name|urlsQueued
init|=
name|messageHandler
operator|.
name|getQueued
argument_list|()
decl_stmt|;
name|int
name|urlsWaiting
init|=
name|threadPool
operator|.
name|getQueueSize
argument_list|()
decl_stmt|;
name|boolean
name|isWorkingOnMessage
init|=
name|messageHandler
operator|.
name|isWorkingOnMessage
argument_list|()
decl_stmt|;
name|int
name|urlsScopeFiltered
init|=
name|urlScopeFilter
operator|.
name|getFiltered
argument_list|()
decl_stmt|;
name|int
name|urlsVisitedFiltered
init|=
name|urlVisitedFilter
operator|.
name|getFiltered
argument_list|()
decl_stmt|;
name|int
name|urlsREFiltered
init|=
name|reFilter
operator|.
name|getFiltered
argument_list|()
decl_stmt|;
name|int
name|urlLengthFiltered
init|=
name|urlLengthFilter
operator|.
name|getFiltered
argument_list|()
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"URLs queued:          "
operator|+
name|urlsQueued
operator|+
literal|"     waiting: "
operator|+
name|urlsWaiting
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"Message is being processed: "
operator|+
name|isWorkingOnMessage
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"URLs Filtered: length: "
operator|+
name|urlLengthFiltered
operator|+
literal|"      scope: "
operator|+
name|urlsScopeFiltered
operator|+
literal|"     visited: "
operator|+
name|urlsVisitedFiltered
operator|+
literal|"      robot.txt: "
operator|+
name|urlsREFiltered
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
operator|+
literal|"Visited size: "
operator|+
name|visitedSize
operator|+
literal|"; String Size in VisitedFilter: "
operator|+
name|visitedStringSize
operator|+
literal|"; Number of Hosts: "
operator|+
name|nrHosts
operator|+
literal|"; hosts in Host Manager: "
operator|+
name|hostManager
operator|.
name|getSize
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|sb
operator|+
literal|""
operator|+
name|now
operator|+
literal|";"
operator|+
name|overallBytesRead
operator|+
literal|";"
operator|+
name|overallTasksRun
operator|+
literal|";"
operator|+
name|urlsQueued
operator|+
literal|";"
operator|+
name|urlsWaiting
operator|+
literal|";"
operator|+
name|isWorkingOnMessage
operator|+
literal|";"
operator|+
name|urlsScopeFiltered
operator|+
literal|";"
operator|+
name|urlsVisitedFiltered
operator|+
literal|";"
operator|+
name|urlsREFiltered
operator|+
literal|";"
operator|+
name|memUsed
operator|+
literal|";"
operator|+
name|memFree
operator|+
literal|";"
operator|+
name|totalMem
operator|+
literal|";"
operator|+
name|nrHosts
operator|+
literal|";"
operator|+
name|visitedSize
operator|+
literal|";"
operator|+
name|visitedStringSize
operator|+
literal|";"
operator|+
name|rawBytesReadString
operator|+
literal|";"
operator|+
name|urlLengthFiltered
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isWorkingOnMessage
operator|&&
operator|(
name|urlsQueued
operator|==
literal|0
operator|)
operator|&&
operator|(
name|urlsWaiting
operator|==
literal|0
operator|)
operator|&&
name|allThreadsIdle
condition|)
block|{
name|nothingReadCount
operator|++
expr_stmt|;
if|if
condition|(
name|nothingReadCount
operator|>
literal|3
condition|)
block|{
name|SimpleLoggerManager
operator|.
name|getInstance
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|nothingReadCount
operator|=
literal|0
expr_stmt|;
block|}
name|this
operator|.
name|setChanged
argument_list|()
expr_stmt|;
name|this
operator|.
name|notifyObservers
argument_list|()
expr_stmt|;
comment|// Request Garbage Collection
name|monitorRunCount
operator|++
expr_stmt|;
if|if
condition|(
name|monitorRunCount
operator|%
literal|6
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|monitorRunCount
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|SimpleLoggerManager
operator|.
name|getInstance
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Monitor: Exception: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * start the thread      */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|this
operator|.
name|clear
argument_list|()
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * interrupt the monitor thread      */
DECL|method|interrupt
specifier|public
name|void
name|interrupt
parameter_list|()
block|{
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
comment|//sampleTimeStamps.clear();
comment|/*for(int i=0; i< timeSamples.length; i++)         {             timeSamples[i].clear();         }         */
block|}
comment|/*    public synchronized double getAverageReadCount(int maxPeriods)     {         int lastPeriod = bytesReadPerPeriod.size()-1;         int periods = Math.min(lastPeriod, maxPeriods);         if(periods< 2)         {             return 0.0;         }           long bytesLastPeriod =   ((Sample)bytesReadPerPeriod.get(lastPeriod)).bytesRead;         long bytesBeforePeriod = ((Sample)bytesReadPerPeriod.get(lastPeriod - periods)).bytesRead;         long bytesRead = bytesLastPeriod - bytesBeforePeriod;          long endTime = ((Long)sampleTimeStamps.get(sampleTimeStamps.size()-1)).longValue();         long startTime = ((Long)sampleTimeStamps.get(sampleTimeStamps.size()-1 - periods)).longValue();         long duration = endTime - startTime;         System.out.println("bytes read: " + bytesRead + " duration in s: " + duration/1000.0 + " = " + ((double)bytesRead) / (duration/1000.0) + " per second");          return ((double)bytesRead) / (duration/1000.0);     } */
comment|/*public synchronized double getDocsPerSecond(int maxPeriods)     {         int lastPeriod = bytesReadPerPeriod.size()-1;         int periods = Math.min(lastPeriod, maxPeriods);         if(periods< 2)         {             return 0.0;         }           long docsLastPeriod =   ((Sample)bytesReadPerPeriod.get(lastPeriod)).docsRead;         long docsBeforePeriod = ((Sample)bytesReadPerPeriod.get(lastPeriod - periods)).docsRead;         long docsRead = docsLastPeriod - docsBeforePeriod;          long endTime = ((Long)sampleTimeStamps.get(sampleTimeStamps.size()-1)).longValue();         long startTime = ((Long)sampleTimeStamps.get(sampleTimeStamps.size() - periods)).longValue();         long duration = endTime - startTime;         System.out.println("docs read: " + docsRead + " duration in s: " + duration/1000.0 + " = " + ((double)docsRead) / (duration/1000.0) + " per second");          return ((double)docsRead) / (duration/1000.0);     }*/
comment|/**      * retrieves the number of threads whose byteCount is below the threshold      * @param maxPeriods the number of periods to look back      * @param threshold  the number of bytes per second that acts as the threshold for a stalled thread      */
comment|/*public synchronized int getStalledThreadCount(int maxPeriods, double threshold)     {         int periods = Math.min(sampleTimeStamps.size(), maxPeriods);         int stalledThreads = 0;         int j=0, i=0;         if(periods> 1)         {             for(j=0; j<timeSamples.length; j++)             {                 long threadByteCount = 0;                 ArrayList actArrayList = timeSamples[j];                 double bytesPerSecond = 0;                 try                 {                     for(i=0; i<periods; i++)                     {                          Sample actSample = (Sample)(actArrayList.get(i));                         threadByteCount += actSample.bytesRead;                     }                 }                 catch(Exception e)                 {                     System.out.println("getAverageReadCount: " + e.getClass().getName() + ": " + e.getMessage() + "(" + i + ";" + j + ")");                     e.printStackTrace();                 }                  bytesPerSecond = ((double)threadByteCount) /                        ((double)((Long)sampleTimeStamps.get(sampleTimeStamps.size()-1)).longValue()                       - ((Long)sampleTimeStamps.get(sampleTimeStamps.size()-periods)).longValue()) * 1000.0;                 if(bytesPerSecond< threshold)                 {                     stalledThreads++;                 }             }         }          return stalledThreads;     } */
DECL|field|samples
name|int
name|samples
init|=
literal|0
decl_stmt|;
DECL|method|addSample
specifier|public
name|void
name|addSample
parameter_list|(
name|Sample
name|s
parameter_list|)
block|{
if|if
condition|(
name|samples
operator|<
literal|10
condition|)
block|{
name|bytesReadPerPeriod
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|samples
operator|++
expr_stmt|;
block|}
else|else
block|{
name|bytesReadPerPeriod
operator|.
name|set
argument_list|(
name|samples
operator|%
literal|10
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAverageBytesRead
specifier|public
name|double
name|getAverageBytesRead
parameter_list|()
block|{
name|Iterator
name|i
init|=
name|bytesReadPerPeriod
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Sample
name|oldest
init|=
literal|null
decl_stmt|;
name|Sample
name|newest
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Sample
name|s
init|=
operator|(
name|Sample
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldest
operator|==
literal|null
condition|)
block|{
name|oldest
operator|=
name|newest
operator|=
name|s
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|s
operator|.
name|time
operator|<
name|oldest
operator|.
name|time
condition|)
block|{
name|oldest
operator|=
name|s
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|time
operator|>
name|newest
operator|.
name|time
condition|)
block|{
name|newest
operator|=
name|s
expr_stmt|;
block|}
block|}
block|}
return|return
operator|(
operator|(
name|newest
operator|.
name|bytesRead
operator|-
name|oldest
operator|.
name|bytesRead
operator|)
operator|/
operator|(
operator|(
name|newest
operator|.
name|time
operator|-
name|oldest
operator|.
name|time
operator|)
operator|/
literal|1000.0
operator|)
operator|)
return|;
block|}
DECL|method|getAverageDocsRead
specifier|public
name|double
name|getAverageDocsRead
parameter_list|()
block|{
name|Iterator
name|i
init|=
name|bytesReadPerPeriod
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Sample
name|oldest
init|=
literal|null
decl_stmt|;
name|Sample
name|newest
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Sample
name|s
init|=
operator|(
name|Sample
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldest
operator|==
literal|null
condition|)
block|{
name|oldest
operator|=
name|newest
operator|=
name|s
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|s
operator|.
name|time
operator|<
name|oldest
operator|.
name|time
condition|)
block|{
name|oldest
operator|=
name|s
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|time
operator|>
name|newest
operator|.
name|time
condition|)
block|{
name|newest
operator|=
name|s
expr_stmt|;
block|}
block|}
block|}
return|return
operator|(
operator|(
name|newest
operator|.
name|docsRead
operator|-
name|oldest
operator|.
name|docsRead
operator|)
operator|/
operator|(
operator|(
name|newest
operator|.
name|time
operator|-
name|oldest
operator|.
name|time
operator|)
operator|/
literal|1000.0
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

