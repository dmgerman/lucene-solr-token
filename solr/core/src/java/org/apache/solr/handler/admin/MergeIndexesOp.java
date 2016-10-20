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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|IOUtils
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
name|params
operator|.
name|CoreAdminParams
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
name|params
operator|.
name|UpdateParams
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
name|CachingDirectoryFactory
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
name|DirectoryFactory
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|search
operator|.
name|SolrIndexSearcher
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
name|update
operator|.
name|MergeIndexesCommand
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|RefCounted
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

begin_class
DECL|class|MergeIndexesOp
class|class
name|MergeIndexesOp
implements|implements
name|CoreAdminHandler
operator|.
name|CoreAdminOp
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|CoreAdminHandler
operator|.
name|CallInfo
name|it
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|it
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|wrappedReq
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|SolrCore
argument_list|>
name|sourceCores
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
argument_list|>
name|searchers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|// stores readers created from indexDir param values
name|List
argument_list|<
name|DirectoryReader
argument_list|>
name|readersToBeClosed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Directory
argument_list|,
name|Boolean
argument_list|>
name|dirsToBeReleased
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|String
index|[]
name|dirNames
init|=
name|params
operator|.
name|getParams
argument_list|(
name|CoreAdminParams
operator|.
name|INDEX_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|dirNames
operator|==
literal|null
operator|||
name|dirNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|String
index|[]
name|sources
init|=
name|params
operator|.
name|getParams
argument_list|(
literal|"srcCore"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sources
operator|==
literal|null
operator|||
name|sources
operator|.
name|length
operator|==
literal|0
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"At least one indexDir or srcCore must be specified"
argument_list|)
throw|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|source
init|=
name|sources
index|[
name|i
index|]
decl_stmt|;
name|SolrCore
name|srcCore
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getCore
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcCore
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Core: "
operator|+
name|source
operator|+
literal|" does not exist"
argument_list|)
throw|;
name|sourceCores
operator|.
name|add
argument_list|(
name|srcCore
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|DirectoryFactory
name|dirFactory
init|=
name|core
operator|.
name|getDirectoryFactory
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
name|dirNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|markAsDone
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|dirFactory
operator|instanceof
name|CachingDirectoryFactory
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|CachingDirectoryFactory
operator|)
name|dirFactory
operator|)
operator|.
name|getLivePaths
argument_list|()
operator|.
name|contains
argument_list|(
name|dirNames
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|markAsDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|Directory
name|dir
init|=
name|dirFactory
operator|.
name|get
argument_list|(
name|dirNames
index|[
name|i
index|]
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
decl_stmt|;
name|dirsToBeReleased
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|markAsDone
argument_list|)
expr_stmt|;
comment|// TODO: why doesn't this use the IR factory? what is going on here?
name|readersToBeClosed
operator|.
name|add
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|DirectoryReader
argument_list|>
name|readers
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|readersToBeClosed
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|readers
operator|=
name|readersToBeClosed
expr_stmt|;
block|}
else|else
block|{
name|readers
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
for|for
control|(
name|SolrCore
name|solrCore
range|:
name|sourceCores
control|)
block|{
comment|// record the searchers so that we can decref
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|solrCore
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|searchers
operator|.
name|add
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|readers
operator|.
name|add
argument_list|(
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|UpdateRequestProcessorChain
name|processorChain
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|)
argument_list|)
decl_stmt|;
name|wrappedReq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|it
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|processorChain
operator|.
name|createProcessor
argument_list|(
name|wrappedReq
argument_list|,
name|it
operator|.
name|rsp
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processMergeIndexes
argument_list|(
operator|new
name|MergeIndexesCommand
argument_list|(
name|readers
argument_list|,
name|it
operator|.
name|req
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
comment|// log and rethrow so that if the finally fails we don't lose the original problem
name|log
operator|.
name|error
argument_list|(
literal|"ERROR executing merge:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
for|for
control|(
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
range|:
name|searchers
control|)
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|SolrCore
name|solrCore
range|:
name|sourceCores
control|)
block|{
if|if
condition|(
name|solrCore
operator|!=
literal|null
condition|)
name|solrCore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|readersToBeClosed
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Directory
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|entries
init|=
name|dirsToBeReleased
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Directory
argument_list|,
name|Boolean
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|DirectoryFactory
name|dirFactory
init|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|boolean
name|markAsDone
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|markAsDone
condition|)
block|{
name|dirFactory
operator|.
name|doneWithDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|dirFactory
operator|.
name|release
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wrappedReq
operator|!=
literal|null
condition|)
name|wrappedReq
operator|.
name|close
argument_list|()
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

