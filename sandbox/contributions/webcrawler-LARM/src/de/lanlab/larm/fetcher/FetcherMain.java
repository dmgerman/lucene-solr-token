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
name|ThreadPoolObserver
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
name|threads
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|java
operator|.
name|util
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
name|gui
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
name|storage
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
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|UIManager
import|;
end_import

begin_import
import|import
name|HTTPClient
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|MalformedPatternException
import|;
end_import

begin_comment
comment|/**  * ENTRY POINT: this class contains the main()-method of the application, does  * all the initializing and optionally connects the fetcher with the GUI.  *  * @author    Clemens Marschner  * @created   December 16, 2000  * @version $Id$  */
end_comment

begin_class
DECL|class|FetcherMain
specifier|public
class|class
name|FetcherMain
block|{
comment|/**      * the main message pipeline      */
DECL|field|messageHandler
specifier|protected
name|MessageHandler
name|messageHandler
decl_stmt|;
comment|/**      * this filter records all incoming URLs and filters everything it already      * knows      */
DECL|field|urlVisitedFilter
specifier|protected
name|URLVisitedFilter
name|urlVisitedFilter
decl_stmt|;
comment|/**      * the scope filter filters URLs that fall out of the scope given by the      * regular expression      */
DECL|field|urlScopeFilter
specifier|protected
name|URLScopeFilter
name|urlScopeFilter
decl_stmt|;
comment|/*      * The DNS resolver was supposed to hold the host addresses for all hosts      * this is done by URL itself today      *      * protected DNSResolver dnsResolver;      */
comment|/**      * the robot exclusion filter looks if a robots.txt is present on a host      * before it is first accessed      */
DECL|field|reFilter
specifier|protected
name|RobotExclusionFilter
name|reFilter
decl_stmt|;
comment|/**      * the host manager keeps track of all hosts and is used by the filters.      */
DECL|field|hostManager
specifier|protected
name|HostManager
name|hostManager
decl_stmt|;
comment|/**      * this rather flaky filter just filters out some URLs, i.e. different views      * of Apache the apache DirIndex module. Has to be made      * configurable in near future      */
DECL|field|knownPathsFilter
specifier|protected
name|KnownPathsFilter
name|knownPathsFilter
decl_stmt|;
comment|/**      * this is the main document fetcher. It contains a thread pool that fetches the      * documents and stores them      */
DECL|field|fetcher
specifier|protected
name|Fetcher
name|fetcher
decl_stmt|;
comment|/**      * the thread monitor once was only a monitoring tool, but now has become a      * vital part of the system that computes statistics and      * flushes the log file buffers      */
DECL|field|monitor
specifier|protected
name|ThreadMonitor
name|monitor
decl_stmt|;
comment|/**      * the storage is a central class that puts all fetched documents somewhere.      * Several differnt implementations exist.      */
DECL|field|storage
specifier|protected
name|DocumentStorage
name|storage
decl_stmt|;
comment|/**      * the URL length filter filters URLs that are too long, i.e. because of errors      * in the implementation of dynamic web sites      */
DECL|field|urlLengthFilter
specifier|protected
name|URLLengthFilter
name|urlLengthFilter
decl_stmt|;
comment|/**      * initializes all classes and registers anonymous adapter classes as      * listeners for fetcher events.      *      * @param nrThreads  number of fetcher threads to be created      */
DECL|method|FetcherMain
specifier|public
name|FetcherMain
parameter_list|(
name|int
name|nrThreads
parameter_list|)
block|{
comment|// to make things clear, this method is commented a bit better than
comment|// the rest of the program...
comment|// this is the main message queue. handlers are registered with
comment|// the queue, and whenever a message is put in it, they are passed to the
comment|// filters in a "chain of responibility" manner. Every listener can decide
comment|// to throw the message away
name|messageHandler
operator|=
operator|new
name|MessageHandler
argument_list|()
expr_stmt|;
comment|// the storage is the class which saves a WebDocument somewhere, no
comment|// matter how it does it, whether it's in a file, in a database or
comment|// whatever
comment|// example for the (very slow) SQL Server storage:
comment|// this.storage = new SQLServerStorage("sun.jdbc.odbc.JdbcOdbcDriver","jdbc:odbc:search","sa","...",nrThreads);
comment|// the LogStorage used here does extensive logging. It logs all links and
comment|// document information.
comment|// it also saves all documents to page files. Probably this single storage
comment|// could also be replaced by a pipeline; or even incorporated into the
comment|// existing message pipeline
name|SimpleLogger
name|storeLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"store"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SimpleLogger
name|linksLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"links"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StoragePipeline
name|storage
init|=
operator|new
name|StoragePipeline
argument_list|()
decl_stmt|;
name|storage
operator|.
name|addDocStorage
argument_list|(
operator|new
name|LogStorage
argument_list|(
name|storeLog
argument_list|,
comment|/* save in page files? */
literal|false
argument_list|,
comment|/* logfile prefix */
literal|"logs/pagefile"
argument_list|)
argument_list|)
expr_stmt|;
name|storage
operator|.
name|addLinkStorage
argument_list|(
operator|new
name|LinkLogStorage
argument_list|(
name|linksLog
argument_list|)
argument_list|)
expr_stmt|;
name|storage
operator|.
name|addLinkStorage
argument_list|(
name|messageHandler
argument_list|)
expr_stmt|;
comment|//storage.addStorage(new LuceneStorage(...));
comment|//storage.addStorage(new JMSStorage(...));
comment|// a third example would be the NullStorage, which converts the documents into
comment|// heat, which evaporates above the processor
comment|// NullStorage();
comment|// create the filters and add them to the message queue
name|urlScopeFilter
operator|=
operator|new
name|URLScopeFilter
argument_list|()
expr_stmt|;
name|urlVisitedFilter
operator|=
operator|new
name|URLVisitedFilter
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
comment|// dnsResolver = new DNSResolver();
name|hostManager
operator|=
operator|new
name|HostManager
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|reFilter
operator|=
operator|new
name|RobotExclusionFilter
argument_list|(
name|hostManager
argument_list|)
expr_stmt|;
name|fetcher
operator|=
operator|new
name|Fetcher
argument_list|(
name|nrThreads
argument_list|,
name|storage
argument_list|,
name|storage
argument_list|,
name|hostManager
argument_list|)
expr_stmt|;
name|knownPathsFilter
operator|=
operator|new
name|KnownPathsFilter
argument_list|()
expr_stmt|;
name|urlLengthFilter
operator|=
operator|new
name|URLLengthFilter
argument_list|(
literal|255
argument_list|)
expr_stmt|;
comment|// prevent message box popups
name|HTTPConnection
operator|.
name|setDefaultAllowUserInteraction
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// prevent GZipped files from being decoded
name|HTTPConnection
operator|.
name|removeDefaultModule
argument_list|(
name|HTTPClient
operator|.
name|ContentEncodingModule
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// initialize the threads
name|fetcher
operator|.
name|init
argument_list|()
expr_stmt|;
comment|// the thread monitor watches the thread pool.
name|monitor
operator|=
operator|new
name|ThreadMonitor
argument_list|(
name|urlLengthFilter
argument_list|,
name|urlVisitedFilter
argument_list|,
name|urlScopeFilter
argument_list|,
comment|/*dnsResolver,*/
name|reFilter
argument_list|,
name|messageHandler
argument_list|,
name|fetcher
operator|.
name|getThreadPool
argument_list|()
argument_list|,
name|hostManager
argument_list|,
literal|5000
comment|// wake up every 5 seconds
argument_list|)
expr_stmt|;
comment|// add all filters to the handler.
name|messageHandler
operator|.
name|addListener
argument_list|(
name|urlLengthFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|urlScopeFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|reFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|urlVisitedFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|knownPathsFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|fetcher
argument_list|)
expr_stmt|;
comment|/* uncomment this to enable HTTPClient logging         try         {             HTTPClient.Log.setLogWriter(new java.io.FileWriter("logs/HttpClient.log"),false);             HTTPClient.Log.setLogging(HTTPClient.Log.ALL, true);         }         catch (Exception e)         {             e.printStackTrace();         }         */
block|}
comment|/**      * Sets the RexString attribute of the FetcherMain object      *      * @param restrictTo                          The new RexString value      */
DECL|method|setRexString
specifier|public
name|void
name|setRexString
parameter_list|(
name|String
name|restrictTo
parameter_list|)
throws|throws
name|MalformedPatternException
block|{
name|urlScopeFilter
operator|.
name|setRexString
argument_list|(
name|restrictTo
argument_list|)
expr_stmt|;
block|}
comment|/**      * Description of the Method      *      * @param url                                 Description of Parameter      * @param isFrame                             Description of the Parameter      * @exception java.net.MalformedURLException  Description of Exception      */
DECL|method|putURL
specifier|public
name|void
name|putURL
parameter_list|(
name|URL
name|url
parameter_list|,
name|boolean
name|isFrame
parameter_list|)
throws|throws
name|java
operator|.
name|net
operator|.
name|MalformedURLException
block|{
try|try
block|{
name|messageHandler
operator|.
name|putMessage
argument_list|(
operator|new
name|URLMessage
argument_list|(
name|url
argument_list|,
literal|null
argument_list|,
name|isFrame
argument_list|,
literal|null
argument_list|,
name|this
operator|.
name|hostManager
argument_list|)
argument_list|)
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
literal|"Exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("URLs geschrieben");
block|}
comment|/**      * Description of the Method      */
DECL|method|startMonitor
specifier|public
name|void
name|startMonitor
parameter_list|()
block|{
name|monitor
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/*      * the GUI is not working at this time. It was used in the very beginning, but      * synchronous updates turned out to slow down the program a lot, even if the      * GUI would be turned off. Thus, a lot      * of Observer messages where removed later. Nontheless, it's quite cool to see      * it working...      *      * @param f         Description of Parameter      * @param startURL  Description of Parameter      */
comment|/*     public void initGui(FetcherMain f, String startURL)     {         // if we're on a windows platform, make it look a bit more convenient         try         {             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());         }         catch (Exception e)         {             // dann halt nicht...         }         System.out.println("Init FetcherFrame");          FetcherSummaryFrame fetcherFrame;         fetcherFrame = new FetcherSummaryFrame();         fetcherFrame.setSize(640, 450);         fetcherFrame.setVisible(true);         FetcherGUIController guiController = new FetcherGUIController(f, fetcherFrame, startURL);     }         */
comment|/**      * The main program. parsed      *      * @param args  The command line arguments      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|int
name|nrThreads
init|=
literal|10
decl_stmt|;
name|String
name|startURL
init|=
literal|""
decl_stmt|;
name|String
name|restrictTo
init|=
literal|"http://141.84.120.82/ll/cmarschn/.*"
decl_stmt|;
name|boolean
name|gui
init|=
literal|false
decl_stmt|;
name|boolean
name|showInfo
init|=
literal|false
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"LARM - LANLab Retrieval Machine - Fetcher - V 1.00 - (C) LANLab 2000-02"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-start"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|startURL
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Start-URL set to: "
operator|+
name|startURL
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-restrictto"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|restrictTo
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Restricting URLs to "
operator|+
name|restrictTo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-threads"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|nrThreads
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Threads set to "
operator|+
name|nrThreads
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-gui"
argument_list|)
condition|)
block|{
name|gui
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-?"
argument_list|)
condition|)
block|{
name|showInfo
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown option: "
operator|+
name|args
index|[
name|i
index|]
operator|+
literal|"; use -? to get syntax"
argument_list|)
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
comment|//URL.setURLStreamHandlerFactory(new HttpTimeoutFactory(500));
comment|// replaced by HTTPClient
name|FetcherMain
name|f
init|=
operator|new
name|FetcherMain
argument_list|(
name|nrThreads
argument_list|)
decl_stmt|;
if|if
condition|(
name|showInfo
operator|||
operator|(
name|startURL
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
name|gui
operator|==
literal|false
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: FetcherMain -start<URL> -restrictto<RegEx> [-threads<nr=10>]"
argument_list|)
expr_stmt|;
comment|// [-gui]
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|f
operator|.
name|setRexString
argument_list|(
name|restrictTo
argument_list|)
expr_stmt|;
if|if
condition|(
name|gui
condition|)
block|{
comment|// f.initGui(f, startURL);
block|}
else|else
block|{
try|try
block|{
name|f
operator|.
name|startMonitor
argument_list|()
expr_stmt|;
name|f
operator|.
name|putURL
argument_list|(
operator|new
name|URL
argument_list|(
name|startURL
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Malformed URL"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|MalformedPatternException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrong RegEx syntax. Must be a valid PERL RE"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

