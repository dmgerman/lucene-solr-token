begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  LARM - LANLab Retrieval Machine  *  *  $history: $  *  */
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
name|java
operator|.
name|net
operator|.
name|URL
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
name|InputStreamObserver
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
name|ObservableInputStream
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
name|WebDocument
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
name|SimpleCharArrayReader
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
name|DocumentStorage
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
name|SimpleLogger
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
name|HttpTimeoutFactory
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
name|java
operator|.
name|net
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
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|parser
operator|.
name|Tokenizer
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
name|parser
operator|.
name|LinkHandler
import|;
end_import

begin_comment
comment|/**  * this class gets the documents from the web. It connects to the server given  * by the IP address in the URLMessage, gets the document, and forwards it to  * the storage. If it's an HTML document, it will be parsed and all links will  * be put into the message handler again.  *  * @author    Clemens Marschner  *  */
end_comment

begin_class
DECL|class|FetcherTask
specifier|public
class|class
name|FetcherTask
implements|implements
name|InterruptableTask
implements|,
name|LinkHandler
implements|,
name|Serializable
block|{
DECL|field|isInterrupted
specifier|protected
specifier|volatile
name|boolean
name|isInterrupted
init|=
literal|false
decl_stmt|;
comment|/**      * each task has its own number. the class variable counts up if an instance      * of a fetcher task is created      */
DECL|field|taskIdentity
specifier|static
specifier|volatile
name|int
name|taskIdentity
init|=
literal|0
decl_stmt|;
comment|/**      * the number of this object      */
DECL|field|taskNr
name|int
name|taskNr
decl_stmt|;
comment|/**      * the BASE Href (defaults to contextUrl, may be changed with a<base> tag      * only valid within a doTask call      */
DECL|field|base
specifier|private
specifier|volatile
name|URL
name|base
decl_stmt|;
comment|/**      * the URL of the docuzment      * only valid within a doTask call      */
DECL|field|contextUrl
specifier|private
specifier|volatile
name|URL
name|contextUrl
decl_stmt|;
comment|/**      * the message handler the URL message comes from; same for all tasks      */
DECL|field|messageHandler
specifier|protected
specifier|static
specifier|volatile
name|MessageHandler
name|messageHandler
decl_stmt|;
comment|/**      * actual number of bytes read      * only valid within a doTask call      */
DECL|field|bytesRead
specifier|private
specifier|volatile
name|long
name|bytesRead
init|=
literal|0
decl_stmt|;
comment|/**      * the storage this task will put the document to      */
DECL|field|storage
specifier|private
specifier|static
specifier|volatile
name|DocumentStorage
name|storage
decl_stmt|;
comment|/**      * task state IDs. comparisons will be done by their references, so always      * use the IDs      */
DECL|field|FT_IDLE
specifier|public
specifier|final
specifier|static
name|String
name|FT_IDLE
init|=
literal|"idle"
decl_stmt|;
DECL|field|FT_STARTED
specifier|public
specifier|final
specifier|static
name|String
name|FT_STARTED
init|=
literal|"started"
decl_stmt|;
DECL|field|FT_OPENCONNECTION
specifier|public
specifier|final
specifier|static
name|String
name|FT_OPENCONNECTION
init|=
literal|"opening connection"
decl_stmt|;
DECL|field|FT_CONNECTING
specifier|public
specifier|final
specifier|static
name|String
name|FT_CONNECTING
init|=
literal|"connecting"
decl_stmt|;
DECL|field|FT_GETTING
specifier|public
specifier|final
specifier|static
name|String
name|FT_GETTING
init|=
literal|"getting"
decl_stmt|;
DECL|field|FT_READING
specifier|public
specifier|final
specifier|static
name|String
name|FT_READING
init|=
literal|"reading"
decl_stmt|;
DECL|field|FT_SCANNING
specifier|public
specifier|final
specifier|static
name|String
name|FT_SCANNING
init|=
literal|"scanning"
decl_stmt|;
DECL|field|FT_STORING
specifier|public
specifier|final
specifier|static
name|String
name|FT_STORING
init|=
literal|"storing"
decl_stmt|;
DECL|field|FT_READY
specifier|public
specifier|final
specifier|static
name|String
name|FT_READY
init|=
literal|"ready"
decl_stmt|;
DECL|field|FT_CLOSING
specifier|public
specifier|final
specifier|static
name|String
name|FT_CLOSING
init|=
literal|"closing"
decl_stmt|;
DECL|field|FT_EXCEPTION
specifier|public
specifier|final
specifier|static
name|String
name|FT_EXCEPTION
init|=
literal|"exception"
decl_stmt|;
DECL|field|FT_INTERRUPTED
specifier|public
specifier|final
specifier|static
name|String
name|FT_INTERRUPTED
init|=
literal|"interrupted"
decl_stmt|;
DECL|field|taskState
specifier|private
specifier|volatile
name|State
name|taskState
init|=
operator|new
name|State
argument_list|(
name|FT_IDLE
argument_list|)
decl_stmt|;
comment|/**      * the URLs found will be stored and only added to the message handler in the very      * end, to avoid too many synchronizations      */
DECL|field|foundUrls
specifier|private
specifier|volatile
name|LinkedList
name|foundUrls
decl_stmt|;
comment|/**      * the URL to be get      */
DECL|field|actURLMessage
specifier|protected
specifier|volatile
name|URLMessage
name|actURLMessage
decl_stmt|;
comment|/**      * the document title, if present      */
DECL|field|title
specifier|private
specifier|volatile
name|String
name|title
decl_stmt|;
comment|/**      * headers for HTTPClient      */
DECL|field|headers
specifier|private
specifier|static
specifier|volatile
name|NVPair
name|headers
index|[]
init|=
operator|new
name|NVPair
index|[
literal|1
index|]
decl_stmt|;
static|static
block|{
name|headers
index|[
literal|0
index|]
operator|=
operator|new
name|HTTPClient
operator|.
name|NVPair
argument_list|(
literal|"User-Agent"
argument_list|,
name|Constants
operator|.
name|CRAWLER_AGENT
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets a copy of the current taskState      *      * @return   The taskState value      */
DECL|method|getTaskState
specifier|public
name|State
name|getTaskState
parameter_list|()
block|{
return|return
name|taskState
operator|.
name|cloneState
argument_list|()
return|;
block|}
comment|/**      * Constructor for the FetcherTask object      *      * @param urlMessage   Description of the Parameter      */
DECL|method|FetcherTask
specifier|public
name|FetcherTask
parameter_list|(
name|URLMessage
name|urlMessage
parameter_list|)
block|{
name|actURLMessage
operator|=
name|urlMessage
expr_stmt|;
block|}
comment|/**      * Gets the uRLMessages attribute of the FetcherTask object      *      * @return   The uRLMessages value      */
DECL|method|getActURLMessage
specifier|public
name|URLMessage
name|getActURLMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|actURLMessage
return|;
block|}
comment|/**      * Sets the document storage      *      * @param storage  The new storage      */
DECL|method|setStorage
specifier|public
specifier|static
name|void
name|setStorage
parameter_list|(
name|DocumentStorage
name|storage
parameter_list|)
block|{
name|FetcherTask
operator|.
name|storage
operator|=
name|storage
expr_stmt|;
block|}
comment|/**      * Sets the messageHandler      *      * @param messageHandler  The new messageHandler      */
DECL|method|setMessageHandler
specifier|public
specifier|static
name|void
name|setMessageHandler
parameter_list|(
name|MessageHandler
name|messageHandler
parameter_list|)
block|{
name|FetcherTask
operator|.
name|messageHandler
operator|=
name|messageHandler
expr_stmt|;
block|}
comment|/**      * @return   the URL as a string      */
DECL|method|getInfo
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
name|actURLMessage
operator|.
name|getURLString
argument_list|()
return|;
block|}
comment|/**      * Gets the uRL attribute of the FetcherTask object      *      * @return   The uRL value      */
DECL|method|getURL
specifier|public
name|URL
name|getURL
parameter_list|()
block|{
return|return
name|actURLMessage
operator|.
name|getUrl
argument_list|()
return|;
block|}
DECL|field|log
name|SimpleLogger
name|log
decl_stmt|;
DECL|field|errorLog
name|SimpleLogger
name|errorLog
decl_stmt|;
comment|//private long startTime;
comment|/**      * this will be called by the fetcher thread and will do all the work      *      * @TODO probably split this up into different processing steps      * @param thread  Description of the Parameter      */
DECL|method|run
specifier|public
name|void
name|run
parameter_list|(
name|ServerThread
name|thread
parameter_list|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_STARTED
argument_list|)
expr_stmt|;
comment|// state information is always set to make the thread monitor happy
name|log
operator|=
name|thread
operator|.
name|getLog
argument_list|()
expr_stmt|;
name|HostManager
name|hm
init|=
operator|(
operator|(
name|FetcherThread
operator|)
name|thread
operator|)
operator|.
name|getHostManager
argument_list|()
decl_stmt|;
name|errorLog
operator|=
name|thread
operator|.
name|getErrorLog
argument_list|()
expr_stmt|;
comment|// startTime = System.currentTimeMillis();
name|int
name|threadNr
init|=
operator|(
operator|(
name|FetcherThread
operator|)
name|thread
operator|)
operator|.
name|getThreadNumber
argument_list|()
decl_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"start"
argument_list|)
expr_stmt|;
name|base
operator|=
name|contextUrl
operator|=
name|actURLMessage
operator|.
name|getUrl
argument_list|()
expr_stmt|;
name|String
name|urlString
init|=
name|actURLMessage
operator|.
name|getURLString
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|contextUrl
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|hostPos
init|=
name|urlString
operator|.
name|indexOf
argument_list|(
name|host
argument_list|)
decl_stmt|;
name|int
name|hostLen
init|=
name|host
operator|.
name|length
argument_list|()
decl_stmt|;
name|HostInfo
name|hi
init|=
name|hm
operator|.
name|getHostInfo
argument_list|(
name|host
argument_list|)
decl_stmt|;
comment|// get and create
if|if
condition|(
operator|!
name|hi
operator|.
name|isHealthy
argument_list|()
condition|)
block|{
comment|// we make this check as late as possible to get the most current information
name|log
operator|.
name|log
argument_list|(
literal|"Bad Host: "
operator|+
name|contextUrl
operator|+
literal|"; returning"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] bad host: "
operator|+
name|this
operator|.
name|actURLMessage
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_READY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
name|foundUrls
operator|=
operator|new
name|java
operator|.
name|util
operator|.
name|LinkedList
argument_list|()
expr_stmt|;
name|HTTPConnection
name|conn
init|=
literal|null
decl_stmt|;
name|title
operator|=
literal|"*untitled*"
expr_stmt|;
name|int
name|size
init|=
literal|1
decl_stmt|;
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
name|bytesRead
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|URL
name|ipURL
init|=
name|contextUrl
decl_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_OPENCONNECTION
argument_list|,
name|urlString
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"connecting to "
operator|+
name|ipURL
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_CONNECTING
argument_list|,
name|ipURL
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|new
name|HTTPConnection
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDefaultTimeout
argument_list|(
literal|75000
argument_list|)
expr_stmt|;
comment|// 75 s
name|conn
operator|.
name|setDefaultAllowUserInteraction
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|this
operator|.
name|FT_GETTING
argument_list|,
name|ipURL
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"getting"
argument_list|)
expr_stmt|;
name|HTTPResponse
name|response
init|=
name|conn
operator|.
name|Get
argument_list|(
name|ipURL
operator|.
name|getFile
argument_list|()
argument_list|,
literal|""
argument_list|,
name|headers
argument_list|)
decl_stmt|;
name|response
operator|.
name|setReadIncrement
argument_list|(
literal|2720
argument_list|)
expr_stmt|;
name|int
name|statusCode
init|=
name|response
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fullBuffer
init|=
literal|null
decl_stmt|;
name|String
name|contentType
init|=
literal|""
decl_stmt|;
name|int
name|contentLength
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|statusCode
operator|!=
literal|404
operator|&&
name|statusCode
operator|!=
literal|403
condition|)
block|{
comment|// read up to Constants.FETCHERTASK_MAXFILESIZE bytes into a byte array
name|taskState
operator|.
name|setState
argument_list|(
name|FT_READING
argument_list|,
name|ipURL
argument_list|)
expr_stmt|;
name|contentType
operator|=
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Type"
argument_list|)
expr_stmt|;
name|String
name|length
init|=
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Length"
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|!=
literal|null
condition|)
block|{
name|contentLength
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|log
argument_list|(
literal|"reading"
argument_list|)
expr_stmt|;
name|fullBuffer
operator|=
name|response
operator|.
name|getData
argument_list|(
name|Constants
operator|.
name|FETCHERTASK_MAXFILESIZE
argument_list|)
expr_stmt|;
comment|// max. 2 MB
if|if
condition|(
name|fullBuffer
operator|!=
literal|null
condition|)
block|{
name|contentLength
operator|=
name|fullBuffer
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|bytesRead
operator|+=
name|contentLength
expr_stmt|;
block|}
block|}
comment|//conn.stop();    // close connection. todo: Do some caching...
comment|/*              *  conn.disconnect();              */
if|if
condition|(
name|isInterrupted
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FetcherTask: interrupted while reading. File truncated"
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"interrupted while reading. File truncated"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|fullBuffer
operator|!=
literal|null
condition|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_SCANNING
argument_list|,
name|ipURL
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"read file ("
operator|+
name|fullBuffer
operator|.
name|length
operator|+
literal|" bytes). Now scanning."
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentType
operator|.
name|startsWith
argument_list|(
literal|"text/html"
argument_list|)
condition|)
block|{
comment|// ouch. I haven't found a better solution yet. just slower ones.
name|char
index|[]
name|fullCharBuffer
init|=
operator|new
name|char
index|[
name|contentLength
index|]
decl_stmt|;
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|fullBuffer
argument_list|)
argument_list|)
operator|.
name|read
argument_list|(
name|fullCharBuffer
argument_list|)
expr_stmt|;
name|Tokenizer
name|tok
init|=
operator|new
name|Tokenizer
argument_list|()
decl_stmt|;
name|tok
operator|.
name|setLinkHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tok
operator|.
name|parse
argument_list|(
operator|new
name|SimpleCharArrayReader
argument_list|(
name|fullCharBuffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// System.out.println("Discovered unknown content type: " + contentType + " at " + urlString);
name|errorLog
operator|.
name|log
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] Discovered unknown content type at "
operator|+
name|urlString
operator|+
literal|": "
operator|+
name|contentType
operator|+
literal|". just storing"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|log
argument_list|(
literal|"scanned"
argument_list|)
expr_stmt|;
block|}
name|taskState
operator|.
name|setState
argument_list|(
name|FT_STORING
argument_list|,
name|ipURL
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|putMessages
argument_list|(
name|foundUrls
argument_list|)
expr_stmt|;
name|storage
operator|.
name|store
argument_list|(
operator|new
name|WebDocument
argument_list|(
name|contextUrl
argument_list|,
name|contentType
argument_list|,
name|fullBuffer
argument_list|,
name|statusCode
argument_list|,
name|actURLMessage
operator|.
name|getReferer
argument_list|()
argument_list|,
name|contentLength
argument_list|,
name|title
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"stored"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedIOException
name|e
parameter_list|)
block|{
comment|// timeout while reading this file
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] FetcherTask: Timeout while opening: "
operator|+
name|this
operator|.
name|actURLMessage
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: Timeout: "
operator|+
name|this
operator|.
name|actURLMessage
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|hi
operator|.
name|badRequest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] FetcherTask: File not Found: "
operator|+
name|this
operator|.
name|actURLMessage
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: File not Found: "
operator|+
name|this
operator|.
name|actURLMessage
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoRouteToHostException
name|e
parameter_list|)
block|{
comment|// router is down or firewall prevents to connect
name|hi
operator|.
name|setReachable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// e.printStackTrace();
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectException
name|e
parameter_list|)
block|{
comment|// no server is listening at this port
name|hi
operator|.
name|setReachable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// e.printStackTrace();
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"]: SocketException:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|// IP Address not to be determined
name|hi
operator|.
name|setReachable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// e.printStackTrace();
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// e.printStackTrace();
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: IOException: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|ome
parameter_list|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] Task "
operator|+
name|this
operator|.
name|taskNr
operator|+
literal|" OutOfMemory after "
operator|+
name|size
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: OutOfMemory after "
operator|+
name|size
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|taskState
operator|.
name|setState
argument_list|(
name|FT_EXCEPTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"] "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" type: "
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|threadNr
operator|+
literal|"]: stopping"
argument_list|)
expr_stmt|;
name|errorLog
operator|.
name|log
argument_list|(
literal|"error: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"; stopping"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|isInterrupted
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Task was interrupted"
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"interrupted"
argument_list|)
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_INTERRUPTED
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isInterrupted
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Task: closed everything"
argument_list|)
expr_stmt|;
block|}
comment|/*          *  }          */
name|taskState
operator|.
name|setState
argument_list|(
name|FT_CLOSING
argument_list|)
expr_stmt|;
name|conn
operator|.
name|stop
argument_list|()
expr_stmt|;
name|taskState
operator|.
name|setState
argument_list|(
name|FT_READY
argument_list|)
expr_stmt|;
name|foundUrls
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * the interrupt method. not in use since the change to HTTPClient      * @TODO decide if we need this anymore      */
DECL|method|interrupt
specifier|public
name|void
name|interrupt
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FetcherTask: interrupted!"
argument_list|)
expr_stmt|;
name|this
operator|.
name|isInterrupted
operator|=
literal|true
expr_stmt|;
comment|/*          *  try          *  {          *  if (conn != null)          *  {          *  ((HttpURLConnection) conn).disconnect();          *  System.out.println("FetcherTask: disconnected URL Connection");          *  conn = null;          *  }          *  if (in != null)          *  {          *  in.close();          *  / possibly hangs at close() .> KeepAliveStream.close() -> MeteredStream.skip()          *  System.out.println("FetcherTask: Closed Input Stream");          *  in = null;          *  }          *  }          *  catch (IOException e)          *  {          *  System.out.println("IOException while interrupting: ");          *  e.printStackTrace();          *  }          *  System.out.println("FetcherTask: Set all IOs to null");          */
block|}
comment|/**      * this is called whenever a links was found in the current document,      * Don't create too many objects here, this will be called      * millions of times      *      * @param link  Description of the Parameter      */
DECL|method|handleLink
specifier|public
name|void
name|handleLink
parameter_list|(
name|String
name|link
parameter_list|,
name|boolean
name|isFrame
parameter_list|)
block|{
try|try
block|{
comment|// cut out Ref part
name|int
name|refPart
init|=
name|link
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
decl_stmt|;
comment|//System.out.println(link);
if|if
condition|(
name|refPart
operator|==
literal|0
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|refPart
operator|>
literal|0
condition|)
block|{
name|link
operator|=
name|link
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|refPart
argument_list|)
expr_stmt|;
block|}
name|URL
name|url
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|link
operator|.
name|startsWith
argument_list|(
literal|"http:"
argument_list|)
condition|)
block|{
comment|// distinguish between absolute and relative URLs
name|url
operator|=
operator|new
name|URL
argument_list|(
name|link
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// relative url
name|url
operator|=
operator|new
name|URL
argument_list|(
name|base
argument_list|,
name|link
argument_list|)
expr_stmt|;
block|}
name|URLMessage
name|urlMessage
init|=
operator|new
name|URLMessage
argument_list|(
name|url
argument_list|,
name|contextUrl
argument_list|,
name|isFrame
argument_list|)
decl_stmt|;
name|String
name|urlString
init|=
name|urlMessage
operator|.
name|getURLString
argument_list|()
decl_stmt|;
name|foundUrls
operator|.
name|add
argument_list|(
name|urlMessage
argument_list|)
expr_stmt|;
comment|//messageHandler.putMessage(new actURLMessage(url)); // put them in the very end
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|//log.log("malformed url: base:" + base + " -+- link:" + link);
name|log
operator|.
name|log
argument_list|(
literal|"warning: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
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
name|log
argument_list|(
literal|"warning: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// e.printStackTrace();
block|}
block|}
comment|/**      * called when a BASE tag was found      *      * @param base  the HREF attribute      */
DECL|method|handleBase
specifier|public
name|void
name|handleBase
parameter_list|(
name|String
name|base
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|base
operator|=
operator|new
name|URL
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|log
operator|.
name|log
argument_list|(
literal|"warning: "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" while converting '"
operator|+
name|base
operator|+
literal|"' to URL in document "
operator|+
name|contextUrl
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * called when a TITLE tag was found      *      * @param title  the string between&lt;title> and&gt;/title>      */
DECL|method|handleTitle
specifier|public
name|void
name|handleTitle
parameter_list|(
name|String
name|title
parameter_list|)
block|{
name|this
operator|.
name|title
operator|=
name|title
expr_stmt|;
block|}
comment|/*      *  public void notifyOpened(ObservableInputStream in, long timeElapsed)      *  {      *  }      *  public void notifyClosed(ObservableInputStream in, long timeElapsed)      *  {      *  }      *  public void notifyRead(ObservableInputStream in, long timeElapsed, int nrRead, int totalRead)      *  {      *  if(totalRead / ((double)timeElapsed)< 0.3) // weniger als 300 bytes/s      *  {      *  System.out.println("Task " + this.taskNr + " stalled at pos " + totalRead + " with " + totalRead / (timeElapsed / 1000.0) + " bytes/s");      *  }      *  }      *  public void notifyFinished(ObservableInputStream in, long timeElapsed, int totalRead)      *  {      *  /System.out.println("Task " + this.taskNr + " finished (" + totalRead + " bytes in " + timeElapsed + " ms with " + totalRead / (timeElapsed / 1000.0) + " bytes/s)");      *  }      */
DECL|method|getBytesRead
specifier|public
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|bytesRead
return|;
block|}
comment|/**      * do nothing if a warning occurs within the html parser      *      * @param message                  Description of the Parameter      * @param systemID                 Description of the Parameter      * @param line                     Description of the Parameter      * @param column                   Description of the Parameter      * @exception java.lang.Exception  Description of the Exception      */
DECL|method|warning
specifier|public
name|void
name|warning
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|systemID
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{ }
comment|/**      * do nothing if a fatal error occurs...      *      * @param message        Description of the Parameter      * @param systemID       Description of the Parameter      * @param line           Description of the Parameter      * @param column         Description of the Parameter      * @exception Exception  Description of the Exception      */
DECL|method|fatal
specifier|public
name|void
name|fatal
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|systemID
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fatal error: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|log
operator|.
name|log
argument_list|(
literal|"fatal error: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

