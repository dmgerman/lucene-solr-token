begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package

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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|cloud
operator|.
name|ZkController
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
name|cloud
operator|.
name|SolrZkClient
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
name|StrUtils
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
name|XML
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
name|CoreContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_comment
comment|/**  * Zookeeper Info  *  * @since solr 4.0  */
end_comment

begin_class
DECL|class|ZookeeperInfoServlet
specifier|public
specifier|final
class|class
name|ZookeeperInfoServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{   }
annotation|@
name|Override
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|response
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json"
argument_list|)
expr_stmt|;
name|CoreContainer
name|cores
init|=
operator|(
name|CoreContainer
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"org.apache.solr.CoreContainer"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"addr"
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|!=
literal|null
operator|&&
name|addr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|addr
operator|=
literal|null
expr_stmt|;
block|}
name|String
name|detailS
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"detail"
argument_list|)
decl_stmt|;
name|boolean
name|detail
init|=
name|detailS
operator|!=
literal|null
operator|&&
name|detailS
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|ZKPrinter
name|printer
init|=
operator|new
name|ZKPrinter
argument_list|(
name|response
argument_list|,
name|out
argument_list|,
name|cores
operator|.
name|getZkController
argument_list|()
argument_list|,
name|addr
argument_list|)
decl_stmt|;
name|printer
operator|.
name|detail
operator|=
name|detail
expr_stmt|;
try|try
block|{
name|printer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|printer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doPost
specifier|public
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------------------
comment|//
comment|//--------------------------------------------------------------------------------------
DECL|class|ZKPrinter
specifier|static
class|class
name|ZKPrinter
block|{
DECL|field|FULLPATH_DEFAULT
specifier|static
name|boolean
name|FULLPATH_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|indent
name|boolean
name|indent
init|=
literal|true
decl_stmt|;
DECL|field|fullpath
name|boolean
name|fullpath
init|=
name|FULLPATH_DEFAULT
decl_stmt|;
DECL|field|detail
name|boolean
name|detail
init|=
literal|false
decl_stmt|;
DECL|field|addr
name|String
name|addr
decl_stmt|;
comment|// the address passed to us
DECL|field|keeperAddr
name|String
name|keeperAddr
decl_stmt|;
comment|// the address we're connected to
DECL|field|doClose
name|boolean
name|doClose
decl_stmt|;
comment|// close the client after done if we opened it
DECL|field|response
name|HttpServletResponse
name|response
decl_stmt|;
DECL|field|out
name|PrintWriter
name|out
decl_stmt|;
DECL|field|zkClient
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|level
name|int
name|level
decl_stmt|;
DECL|field|maxData
name|int
name|maxData
init|=
literal|95
decl_stmt|;
DECL|method|ZKPrinter
specifier|public
name|ZKPrinter
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|PrintWriter
name|out
parameter_list|,
name|ZkController
name|controller
parameter_list|,
name|String
name|addr
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|addr
operator|=
name|addr
expr_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|controller
operator|!=
literal|null
condition|)
block|{
comment|// this core is zk enabled
name|keeperAddr
operator|=
name|controller
operator|.
name|getZkServerAddress
argument_list|()
expr_stmt|;
name|zkClient
operator|=
name|controller
operator|.
name|getZkClient
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkClient
operator|!=
literal|null
operator|&&
name|zkClient
operator|.
name|isConnected
argument_list|()
condition|)
block|{
return|return;
block|}
else|else
block|{
comment|// try a different client with this address
name|addr
operator|=
name|keeperAddr
expr_stmt|;
block|}
block|}
block|}
name|keeperAddr
operator|=
name|addr
expr_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
literal|404
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
operator|+
literal|"\"status\": 404"
operator|+
literal|", \"error\" : \"Zookeeper is not configured for this Solr Core. Please try connecting to an alternate zookeeper address.\""
operator|+
literal|"}"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|addr
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|doClose
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
literal|503
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
operator|+
literal|"\"status\": 503"
operator|+
literal|", \"error\" : \"Could not connect to zookeeper at '"
operator|+
name|addr
operator|+
literal|"'\""
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|zkClient
operator|=
literal|null
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
literal|503
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
operator|+
literal|"\"status\": 503"
operator|+
literal|", \"error\" : \"Could not connect to zookeeper at '"
operator|+
name|addr
operator|+
literal|"'\""
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|zkClient
operator|=
literal|null
expr_stmt|;
return|return;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|doClose
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore exception on close
block|}
block|}
comment|// main entry point
DECL|method|print
name|void
name|print
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|zkClient
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// normalize path
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|path
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
block|}
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|idx
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|parent
init|=
name|idx
operator|>=
literal|0
condition|?
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
else|:
name|path
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|parent
operator|=
literal|"/"
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
if|if
condition|(
name|detail
condition|)
block|{
name|printZnode
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"\"tree\" : ["
argument_list|)
expr_stmt|;
name|printTree
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|exception
name|void
name|exception
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
operator|+
literal|"\"status\": 500"
operator|+
literal|", \"error\" : \""
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"\""
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|xmlescape
name|void
name|xmlescape
parameter_list|(
name|String
name|s
parameter_list|)
block|{
try|try
block|{
name|XML
operator|.
name|escapeCharData
argument_list|(
name|s
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// collapse all whitespace to a single space or escaped newline
DECL|method|compress
name|String
name|compress
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|whitespace
init|=
literal|false
decl_stmt|;
name|boolean
name|newline
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
condition|)
block|{
name|whitespace
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\n'
condition|)
name|newline
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|>=
name|str
operator|.
name|length
argument_list|()
condition|)
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
name|ch
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newline
condition|)
block|{
comment|// sb.append("\\n");
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
comment|// collapse newline to two spaces
block|}
elseif|else
if|if
condition|(
name|whitespace
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
comment|// TODO: handle non-printable chars
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>=
name|maxData
condition|)
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|" ..."
return|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|url
name|void
name|url
parameter_list|(
name|String
name|label
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|detail
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<a href=\"zookeeper?"
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"path="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|path
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|detail
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"&detail="
operator|+
name|detail
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fullpath
operator|!=
name|FULLPATH_DEFAULT
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"&fullpath="
operator|+
name|fullpath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|addr
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"&addr="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|addr
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
name|xmlescape
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</a>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|exception
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|printTree
name|void
name|printTree
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|label
init|=
name|path
decl_stmt|;
if|if
condition|(
operator|!
name|fullpath
condition|)
block|{
name|int
name|idx
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|label
operator|=
name|idx
operator|>
literal|0
condition|?
name|path
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
else|:
name|path
expr_stmt|;
block|}
comment|//url(label, path, true);
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"\"data\" : \""
operator|+
name|label
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|getEphemeralOwner
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|", \"ephemeral\" : true"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"version\" : \""
operator|+
name|stat
operator|.
name|getVersion
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
comment|/*         if (stat.getNumChildren() != 0)         {           out.println(", \"children_count\" : \"" + stat.getNumChildren() + "\"");         }         */
comment|//if (data != null)
if|if
condition|(
name|stat
operator|.
name|getDataLength
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|String
name|str
decl_stmt|;
try|try
block|{
name|str
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|str
operator|=
name|str
operator|.
name|replaceAll
argument_list|(
literal|"\\\""
argument_list|,
literal|"\\\\\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|", \"content\" : \""
argument_list|)
expr_stmt|;
comment|//xmlescape(compress(str));
name|out
operator|.
name|print
argument_list|(
name|compress
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// not UTF8
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"BIN("
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"len="
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"hex="
argument_list|)
expr_stmt|;
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|maxData
operator|/
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StrUtils
operator|.
name|HEX_DIGITS
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StrUtils
operator|.
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|limit
operator|!=
name|data
operator|.
name|length
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|str
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//out.print(str);
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// path doesn't exist (must have been removed)
name|out
operator|.
name|println
argument_list|(
literal|"(path gone)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|stat
operator|.
name|getNumChildren
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", \"children\" : ["
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
literal|null
decl_stmt|;
try|try
block|{
name|children
operator|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|exception
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|exception
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// path doesn't exist (must have been removed)
name|out
operator|.
name|println
argument_list|(
literal|"(children gone)"
argument_list|)
expr_stmt|;
block|}
name|Integer
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
literal|0
operator|!=
name|i
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|String
name|childPath
init|=
name|path
operator|+
operator|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|?
literal|""
else|:
literal|"/"
operator|)
operator|+
name|child
decl_stmt|;
name|printTree
argument_list|(
name|childPath
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|time
name|String
name|time
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
return|return
operator|(
operator|new
name|Date
argument_list|(
name|ms
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
operator|+
literal|" ("
operator|+
name|ms
operator|+
literal|")"
return|;
block|}
DECL|method|printZnode
name|void
name|printZnode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"\"znode\" : {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"\"path\" : \""
argument_list|)
expr_stmt|;
name|xmlescape
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"version\" : \""
operator|+
name|stat
operator|.
name|getVersion
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"aversion\" : \""
operator|+
name|stat
operator|.
name|getAversion
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"cversion\" : \""
operator|+
name|stat
operator|.
name|getCversion
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"ctime\" : \""
operator|+
name|time
argument_list|(
name|stat
operator|.
name|getCtime
argument_list|()
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"mtime\" : \""
operator|+
name|time
argument_list|(
name|stat
operator|.
name|getMtime
argument_list|()
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"czxid\" : \""
operator|+
name|stat
operator|.
name|getCzxid
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"mzxid\" : \""
operator|+
name|stat
operator|.
name|getMzxid
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"pzxid\" : \""
operator|+
name|stat
operator|.
name|getPzxid
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"children_count\" : \""
operator|+
name|stat
operator|.
name|getNumChildren
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"ephemeralOwner\" : \""
operator|+
name|stat
operator|.
name|getEphemeralOwner
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|", \"dataLength\" : \""
operator|+
name|stat
operator|.
name|getDataLength
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|stat
operator|.
name|getDataLength
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|boolean
name|isBinary
init|=
literal|false
decl_stmt|;
name|String
name|str
decl_stmt|;
try|try
block|{
name|str
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|// The results are unspecified
comment|// when the bytes are not properly encoded.
comment|// not UTF8
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|data
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|data
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StrUtils
operator|.
name|HEX_DIGITS
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|StrUtils
operator|.
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|0x3f
operator|)
operator|==
literal|0x3f
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|str
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|str
operator|=
name|str
operator|.
name|replaceAll
argument_list|(
literal|"\\\""
argument_list|,
literal|"\\\\\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|", \"data\" : \""
argument_list|)
expr_stmt|;
comment|//xmlescape(str);
name|out
operator|.
name|print
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|exception
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|exception
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

