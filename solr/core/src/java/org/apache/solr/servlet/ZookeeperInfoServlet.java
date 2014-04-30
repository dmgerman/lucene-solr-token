begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|CharArr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|SolrException
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
name|cloud
operator|.
name|ZkStateReader
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
name|params
operator|.
name|SolrParams
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
name|solr
operator|.
name|util
operator|.
name|FastWriter
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
name|KeeperException
operator|.
name|NoNodeException
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

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|CharArr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZookeeperInfoServlet
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
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
name|ServletException
throws|,
name|IOException
block|{
comment|// This attribute is set by the SolrDispatchFilter
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
if|if
condition|(
name|cores
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Missing request attribute org.apache.solr.CoreContainer."
argument_list|)
throw|;
block|}
specifier|final
name|SolrParams
name|params
decl_stmt|;
try|try
block|{
name|params
operator|=
name|SolrRequestParsers
operator|.
name|DEFAULT
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|request
operator|.
name|getServletPath
argument_list|()
argument_list|,
name|request
argument_list|)
operator|.
name|getParams
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|int
name|code
init|=
literal|500
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
name|code
operator|=
name|Math
operator|.
name|min
argument_list|(
literal|599
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|100
argument_list|,
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|sendError
argument_list|(
name|code
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|path
init|=
name|params
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
name|params
operator|.
name|get
argument_list|(
literal|"addr"
argument_list|)
decl_stmt|;
name|boolean
name|all
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"all"
argument_list|)
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
name|params
operator|.
name|get
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
name|String
name|dumpS
init|=
name|params
operator|.
name|get
argument_list|(
literal|"dump"
argument_list|)
decl_stmt|;
name|boolean
name|dump
init|=
name|dumpS
operator|!=
literal|null
operator|&&
name|dumpS
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
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
name|Writer
name|out
init|=
operator|new
name|FastWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
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
name|printer
operator|.
name|dump
operator|=
name|dump
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
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
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
name|ServletException
throws|,
name|IOException
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
DECL|field|dump
name|boolean
name|dump
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
specifier|final
name|HttpServletResponse
name|response
decl_stmt|;
DECL|field|out
specifier|final
name|Writer
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
name|Writer
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
name|writeError
argument_list|(
literal|404
argument_list|,
literal|"Zookeeper is not configured for this Solr Core. Please try connecting to an alternate zookeeper address."
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
name|Exception
name|e
parameter_list|)
block|{
name|writeError
argument_list|(
literal|503
argument_list|,
literal|"Could not connect to zookeeper at '"
operator|+
name|addr
operator|+
literal|"'\""
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
operator|=
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
name|CharArr
name|chars
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|JSONWriter
name|json
init|=
operator|new
name|JSONWriter
argument_list|(
name|chars
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|json
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|detail
condition|)
block|{
if|if
condition|(
operator|!
name|printZnode
argument_list|(
name|json
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return;
block|}
name|json
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|json
operator|.
name|writeString
argument_list|(
literal|"tree"
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|startArray
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|printTree
argument_list|(
name|json
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return;
comment|// there was an error
block|}
name|json
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|chars
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeError
name|void
name|writeError
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|CharArr
name|chars
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|JSONWriter
name|w
init|=
operator|new
name|JSONWriter
argument_list|(
name|chars
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|w
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|w
operator|.
name|indent
argument_list|()
expr_stmt|;
name|w
operator|.
name|writeString
argument_list|(
literal|"status"
argument_list|)
expr_stmt|;
name|w
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|w
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
name|w
operator|.
name|indent
argument_list|()
expr_stmt|;
name|w
operator|.
name|writeString
argument_list|(
literal|"error"
argument_list|)
expr_stmt|;
name|w
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|w
operator|.
name|writeString
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|w
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|chars
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|printTree
name|boolean
name|printTree
parameter_list|(
name|JSONWriter
name|json
parameter_list|,
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
name|json
operator|.
name|startObject
argument_list|()
expr_stmt|;
comment|//writeKeyValue(json, "data", label, true );
name|json
operator|.
name|writeString
argument_list|(
literal|"data"
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"title"
argument_list|,
name|label
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|writeString
argument_list|(
literal|"attr"
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"href"
argument_list|,
literal|"zookeeper?detail=true&path="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|path
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|json
operator|.
name|endObject
argument_list|()
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
comment|// Trickily, the call to zkClient.getData fills in the stat variable
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
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"ephemeral"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"version"
argument_list|,
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dump
condition|)
block|{
name|json
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
name|printZnode
argument_list|(
name|json
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// path doesn't exist (must have been removed)
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"warning"
argument_list|,
literal|"(path gone)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"warning"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Keeper Exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"warning"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"InterruptedException"
argument_list|,
name|e
argument_list|)
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
name|json
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
if|if
condition|(
name|indent
condition|)
block|{
name|json
operator|.
name|indent
argument_list|()
expr_stmt|;
block|}
name|json
operator|.
name|writeString
argument_list|(
literal|"children"
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|startArray
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
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
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|sort
argument_list|(
name|children
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
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
operator|!
name|first
condition|)
block|{
name|json
operator|.
name|writeValueSeparator
argument_list|()
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
if|if
condition|(
operator|!
name|printTree
argument_list|(
name|json
argument_list|,
name|childPath
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|writeError
argument_list|(
literal|500
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|writeError
argument_list|(
literal|500
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// path doesn't exist (must have been removed)
name|json
operator|.
name|writeString
argument_list|(
literal|"(children gone)"
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
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
DECL|method|writeKeyValue
specifier|public
name|void
name|writeKeyValue
parameter_list|(
name|JSONWriter
name|json
parameter_list|,
name|String
name|k
parameter_list|,
name|Object
name|v
parameter_list|,
name|boolean
name|isFirst
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isFirst
condition|)
block|{
name|json
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|indent
condition|)
block|{
name|json
operator|.
name|indent
argument_list|()
expr_stmt|;
block|}
name|json
operator|.
name|writeString
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|printZnode
name|boolean
name|printZnode
parameter_list|(
name|JSONWriter
name|json
parameter_list|,
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
comment|// Trickily, the call to zkClient.getData fills in the stat variable
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
name|String
name|dataStr
init|=
literal|null
decl_stmt|;
name|String
name|dataStrErr
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|data
condition|)
block|{
try|try
block|{
name|dataStr
operator|=
operator|(
operator|new
name|BytesRef
argument_list|(
name|data
argument_list|)
operator|)
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|dataStrErr
operator|=
literal|"data is not parsable as a utf8 String: "
operator|+
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
name|json
operator|.
name|writeString
argument_list|(
literal|"znode"
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"path"
argument_list|,
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeValueSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|writeString
argument_list|(
literal|"prop"
argument_list|)
expr_stmt|;
name|json
operator|.
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|json
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"version"
argument_list|,
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"aversion"
argument_list|,
name|stat
operator|.
name|getAversion
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"children_count"
argument_list|,
name|stat
operator|.
name|getNumChildren
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"ctime"
argument_list|,
name|time
argument_list|(
name|stat
operator|.
name|getCtime
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"cversion"
argument_list|,
name|stat
operator|.
name|getCversion
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"czxid"
argument_list|,
name|stat
operator|.
name|getCzxid
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"ephemeralOwner"
argument_list|,
name|stat
operator|.
name|getEphemeralOwner
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"mtime"
argument_list|,
name|time
argument_list|(
name|stat
operator|.
name|getMtime
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"mzxid"
argument_list|,
name|stat
operator|.
name|getMzxid
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"pzxid"
argument_list|,
name|stat
operator|.
name|getPzxid
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"dataLength"
argument_list|,
name|stat
operator|.
name|getDataLength
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|dataStrErr
condition|)
block|{
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"dataNote"
argument_list|,
name|dataStrErr
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|dataStr
condition|)
block|{
name|writeKeyValue
argument_list|(
name|json
argument_list|,
literal|"data"
argument_list|,
name|dataStr
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|writeError
argument_list|(
literal|500
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|writeError
argument_list|(
literal|500
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

