begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|ZkSolrResourceLoader
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
name|SolrException
operator|.
name|ErrorCode
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
name|params
operator|.
name|CommonParams
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
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|ContentStreamBase
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
name|NamedList
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
name|SimpleOrderedMap
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
name|core
operator|.
name|SolrCore
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
name|SolrResourceLoader
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
name|handler
operator|.
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|RawResponseWriter
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
name|response
operator|.
name|SolrQueryResponse
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|ManagedIndexSchema
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|URISyntaxException
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
name|Locale
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

begin_comment
comment|/**  * This handler uses the RawResponseWriter to give client access to  * files inside ${solr.home}/conf  *<p>  * If you want to selectively restrict access some configuration files, you can list  * these files in the {@link #HIDDEN} invariants.  For example to hide   * synonyms.txt and anotherfile.txt, you would register:  *<br>  *<pre>  *&lt;requestHandler name="/admin/file" class="org.apache.solr.handler.admin.ShowFileRequestHandler"&gt;  *&lt;lst name="defaults"&gt;  *&lt;str name="echoParams"&gt;explicit&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="invariants"&gt;  *&lt;str name="hidden"&gt;synonyms.txt&lt;/str&gt;   *&lt;str name="hidden"&gt;anotherfile.txt&lt;/str&gt;  *&lt;str name="hidden"&gt;*&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  *  * At present, there is only explicit file names (including path) or the glob '*' are supported. Variants like '*.xml'  * are NOT supported.ere  *  *<p>  * The ShowFileRequestHandler uses the {@link RawResponseWriter} (wt=raw) to return  * file contents.  If you need to use a different writer, you will need to change   * the registered invariant param for wt.  *<p>  * If you want to override the contentType header returned for a given file, you can  * set it directly using: {@link #USE_CONTENT_TYPE}.  For example, to get a plain text  * version of schema.xml, try:  *<pre>  *   http://localhost:8983/solr/admin/file?file=schema.xml&amp;contentType=text/plain  *</pre>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|ShowFileRequestHandler
specifier|public
class|class
name|ShowFileRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|HIDDEN
specifier|public
specifier|static
specifier|final
name|String
name|HIDDEN
init|=
literal|"hidden"
decl_stmt|;
DECL|field|USE_CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|USE_CONTENT_TYPE
init|=
literal|"contentType"
decl_stmt|;
DECL|field|hiddenFiles
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenFiles
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ShowFileRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ShowFileRequestHandler
specifier|public
name|ShowFileRequestHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|hiddenFiles
operator|=
name|initHidden
argument_list|(
name|invariants
argument_list|)
expr_stmt|;
block|}
DECL|method|initHidden
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|initHidden
parameter_list|(
name|SolrParams
name|invariants
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenRet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Build a list of hidden files
if|if
condition|(
name|invariants
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|hidden
init|=
name|invariants
operator|.
name|getParams
argument_list|(
name|HIDDEN
argument_list|)
decl_stmt|;
if|if
condition|(
name|hidden
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|hidden
control|)
block|{
name|hiddenRet
operator|.
name|add
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|hiddenRet
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
throws|,
name|IOException
block|{
name|CoreContainer
name|coreContainer
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|showFromZooKeeper
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|coreContainer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|showFromFileSystem
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Get a list of files from ZooKeeper for from the path in the file= parameter.
DECL|method|showFromZooKeeper
specifier|private
name|void
name|showFromZooKeeper
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|CoreContainer
name|coreContainer
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|UnsupportedEncodingException
block|{
name|SolrZkClient
name|zkClient
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|String
name|adminFile
init|=
name|getAdminFileFromZooKeeper
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|zkClient
argument_list|,
name|hiddenFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminFile
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Show a directory listing
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
name|adminFile
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|NamedList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|files
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|children
control|)
block|{
if|if
condition|(
name|isHiddenFile
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|f
argument_list|,
literal|false
argument_list|,
name|hiddenFiles
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|fileInfo
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|fileInfo
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fchildren
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|adminFile
operator|+
literal|"/"
operator|+
name|f
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|fchildren
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fileInfo
operator|.
name|add
argument_list|(
literal|"directory"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO? content type
name|fileInfo
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO: ?
comment|// fileInfo.add( "modified", new Date( f.lastModified() ) );
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"files"
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Include the file contents
comment|// The file logic depends on RawResponseWriter, so force its use.
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"raw"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|ContentStreamBase
name|content
init|=
operator|new
name|ContentStreamBase
operator|.
name|ByteArrayStream
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|adminFile
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
name|adminFile
argument_list|)
decl_stmt|;
name|content
operator|.
name|setContentType
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|USE_CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Return the file indicated (or the directory listing) from the local file system.
DECL|method|showFromFileSystem
specifier|private
name|void
name|showFromFileSystem
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|File
name|adminFile
init|=
name|getAdminFileFromFileSystem
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|hiddenFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminFile
operator|==
literal|null
condition|)
block|{
comment|// exception already recorded
return|return;
block|}
comment|// Make sure the file exists, is readable and is not a hidden file
if|if
condition|(
operator|!
name|adminFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can not find: "
operator|+
name|adminFile
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Can not find: "
operator|+
name|adminFile
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|adminFile
operator|.
name|canRead
argument_list|()
operator|||
name|adminFile
operator|.
name|isHidden
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can not show: "
operator|+
name|adminFile
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Can not show: "
operator|+
name|adminFile
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Show a directory listing
if|if
condition|(
name|adminFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// it's really a directory, just go for it.
name|int
name|basePath
init|=
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|NamedList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|files
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|adminFile
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|substring
argument_list|(
name|basePath
argument_list|)
decl_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|// normalize slashes
if|if
condition|(
name|isHiddenFile
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|f
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
argument_list|,
literal|false
argument_list|,
name|hiddenFiles
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|fileInfo
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|path
argument_list|,
name|fileInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|fileInfo
operator|.
name|add
argument_list|(
literal|"directory"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO? content type
name|fileInfo
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fileInfo
operator|.
name|add
argument_list|(
literal|"modified"
argument_list|,
operator|new
name|Date
argument_list|(
name|f
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"files"
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Include the file contents
comment|//The file logic depends on RawResponseWriter, so force its use.
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"raw"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|ContentStreamBase
name|content
init|=
operator|new
name|ContentStreamBase
operator|.
name|FileStream
argument_list|(
name|adminFile
argument_list|)
decl_stmt|;
name|content
operator|.
name|setContentType
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|USE_CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//////////////////////// Static methods //////////////////////////////
DECL|method|isHiddenFile
specifier|public
specifier|static
name|boolean
name|isHiddenFile
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|String
name|fnameIn
parameter_list|,
name|boolean
name|reportError
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenFiles
parameter_list|)
block|{
name|String
name|fname
init|=
name|fnameIn
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|hiddenFiles
operator|.
name|contains
argument_list|(
name|fname
argument_list|)
operator|||
name|hiddenFiles
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
if|if
condition|(
name|reportError
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot access "
operator|+
name|fname
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Can not access: "
operator|+
name|fnameIn
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|// This is slightly off, a valid path is something like ./schema.xml. I don't think it's worth the effort though
comment|// to fix it to handle all possibilities though.
if|if
condition|(
name|fname
operator|.
name|indexOf
argument_list|(
literal|".."
argument_list|)
operator|>=
literal|0
operator|||
name|fname
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
if|if
condition|(
name|reportError
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid path: "
operator|+
name|fname
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Invalid path: "
operator|+
name|fnameIn
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|// Make sure that if the schema is managed, we don't allow editing. Don't really want to put
comment|// this in the init since we're not entirely sure when the managed schema will get initialized relative to this
comment|// handler.
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|instanceof
name|ManagedIndexSchema
condition|)
block|{
name|String
name|managed
init|=
name|schema
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fname
operator|.
name|equalsIgnoreCase
argument_list|(
name|managed
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// Refactored to be usable from multiple methods. Gets the path of the requested file from ZK.
comment|// Returns null if the file is not found.
comment|//
comment|// Assumes that the file is in a parameter called "file".
DECL|method|getAdminFileFromZooKeeper
specifier|public
specifier|static
name|String
name|getAdminFileFromZooKeeper
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|SolrZkClient
name|zkClient
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenFiles
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|String
name|adminFile
init|=
literal|null
decl_stmt|;
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
specifier|final
name|ZkSolrResourceLoader
name|loader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|String
name|confPath
init|=
name|loader
operator|.
name|getConfigSetZkPath
argument_list|()
decl_stmt|;
name|String
name|fname
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fname
operator|==
literal|null
condition|)
block|{
name|adminFile
operator|=
name|confPath
expr_stmt|;
block|}
else|else
block|{
name|fname
operator|=
name|fname
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|// normalize slashes
if|if
condition|(
name|isHiddenFile
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|fname
argument_list|,
literal|true
argument_list|,
name|hiddenFiles
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fname
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Only files relative to conf are valid
name|fname
operator|=
name|fname
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|adminFile
operator|=
name|confPath
operator|+
literal|"/"
operator|+
name|fname
expr_stmt|;
block|}
comment|// Make sure the file exists, is readable and is not a hidden file
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
name|adminFile
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can not find: "
operator|+
name|adminFile
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Can not find: "
operator|+
name|adminFile
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|adminFile
return|;
block|}
comment|// Find the file indicated by the "file=XXX" parameter or the root of the conf directory on the local
comment|// file system. Respects all the "interesting" stuff around what the resource loader does to find files.
DECL|method|getAdminFileFromFileSystem
specifier|public
specifier|static
name|File
name|getAdminFileFromFileSystem
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenFiles
parameter_list|)
block|{
name|File
name|adminFile
init|=
literal|null
decl_stmt|;
specifier|final
name|SolrResourceLoader
name|loader
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|File
name|configdir
init|=
operator|new
name|File
argument_list|(
name|loader
operator|.
name|getConfigDir
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|configdir
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// TODO: maybe we should just open it this way to start with?
try|try
block|{
name|configdir
operator|=
operator|new
name|File
argument_list|(
name|loader
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|loader
operator|.
name|getConfigDir
argument_list|()
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can not access configuration directory!"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Can not access configuration directory!"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
name|String
name|fname
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fname
operator|==
literal|null
condition|)
block|{
name|adminFile
operator|=
name|configdir
expr_stmt|;
block|}
else|else
block|{
name|fname
operator|=
name|fname
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|// normalize slashes
if|if
condition|(
name|hiddenFiles
operator|.
name|contains
argument_list|(
name|fname
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Can not access: "
operator|+
name|fname
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Can not access: "
operator|+
name|fname
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fname
operator|.
name|indexOf
argument_list|(
literal|".."
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid path: "
operator|+
name|fname
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Invalid path: "
operator|+
name|fname
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|adminFile
operator|=
operator|new
name|File
argument_list|(
name|configdir
argument_list|,
name|fname
argument_list|)
expr_stmt|;
block|}
return|return
name|adminFile
return|;
block|}
DECL|method|getHiddenFiles
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|getHiddenFiles
parameter_list|()
block|{
return|return
name|hiddenFiles
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Admin Config File -- view or update config files directly"
return|;
block|}
block|}
end_class

end_unit

