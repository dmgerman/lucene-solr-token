begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|Collection
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|store
operator|.
name|IOContext
operator|.
name|Context
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
name|NativeFSLockFactory
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
name|NoLockFactory
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
name|RateLimitedDirectoryWrapper
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
name|SimpleFSLockFactory
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
name|SingleInstanceLockFactory
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
name|util
operator|.
name|NamedList
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
comment|/**  * A {@link DirectoryFactory} impl base class for caching Directory instances  * per path. Most DirectoryFactory implementations will want to extend this  * class and simply implement {@link DirectoryFactory#create(String, DirContext)}.  *   * This is an expert class and these API's are subject to change.  *   */
end_comment

begin_class
DECL|class|CachingDirectoryFactory
specifier|public
specifier|abstract
class|class
name|CachingDirectoryFactory
extends|extends
name|DirectoryFactory
block|{
DECL|class|CacheValue
specifier|protected
class|class
name|CacheValue
block|{
DECL|field|path
specifier|final
specifier|public
name|String
name|path
decl_stmt|;
DECL|field|directory
specifier|final
specifier|public
name|Directory
name|directory
decl_stmt|;
comment|// use the setter!
DECL|field|deleteOnClose
specifier|private
name|boolean
name|deleteOnClose
init|=
literal|false
decl_stmt|;
DECL|method|CacheValue
specifier|public
name|CacheValue
parameter_list|(
name|String
name|path
parameter_list|,
name|Directory
name|directory
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|closeEntries
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|field|refCnt
specifier|public
name|int
name|refCnt
init|=
literal|1
decl_stmt|;
comment|// has close(Directory) been called on this?
DECL|field|closeDirectoryCalled
specifier|public
name|boolean
name|closeDirectoryCalled
init|=
literal|false
decl_stmt|;
DECL|field|doneWithDir
specifier|public
name|boolean
name|doneWithDir
init|=
literal|false
decl_stmt|;
DECL|field|deleteAfterCoreClose
specifier|private
name|boolean
name|deleteAfterCoreClose
init|=
literal|false
decl_stmt|;
DECL|field|removeEntries
specifier|public
name|Set
argument_list|<
name|CacheValue
argument_list|>
name|removeEntries
init|=
operator|new
name|HashSet
argument_list|<
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|closeEntries
specifier|public
name|Set
argument_list|<
name|CacheValue
argument_list|>
name|closeEntries
init|=
operator|new
name|HashSet
argument_list|<
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|setDeleteOnClose
specifier|public
name|void
name|setDeleteOnClose
parameter_list|(
name|boolean
name|deleteOnClose
parameter_list|,
name|boolean
name|deleteAfterCoreClose
parameter_list|)
block|{
if|if
condition|(
name|deleteOnClose
condition|)
block|{
name|removeEntries
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|deleteOnClose
operator|=
name|deleteOnClose
expr_stmt|;
name|this
operator|.
name|deleteAfterCoreClose
operator|=
name|deleteAfterCoreClose
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
literal|"CachedDir<<"
operator|+
name|directory
operator|.
name|toString
argument_list|()
operator|+
literal|";refCount="
operator|+
name|refCnt
operator|+
literal|";path="
operator|+
name|path
operator|+
literal|";done="
operator|+
name|doneWithDir
operator|+
literal|">>"
return|;
block|}
block|}
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CachingDirectoryFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|byPathCache
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|CacheValue
argument_list|>
name|byPathCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|byDirectoryCache
specifier|protected
name|Map
argument_list|<
name|Directory
argument_list|,
name|CacheValue
argument_list|>
name|byDirectoryCache
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Directory
argument_list|,
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|closeListeners
specifier|protected
name|Map
argument_list|<
name|Directory
argument_list|,
name|List
argument_list|<
name|CloseListener
argument_list|>
argument_list|>
name|closeListeners
init|=
operator|new
name|HashMap
argument_list|<
name|Directory
argument_list|,
name|List
argument_list|<
name|CloseListener
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|removeEntries
specifier|protected
name|Set
argument_list|<
name|CacheValue
argument_list|>
name|removeEntries
init|=
operator|new
name|HashSet
argument_list|<
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|maxWriteMBPerSecFlush
specifier|private
name|Double
name|maxWriteMBPerSecFlush
decl_stmt|;
DECL|field|maxWriteMBPerSecMerge
specifier|private
name|Double
name|maxWriteMBPerSecMerge
decl_stmt|;
DECL|field|maxWriteMBPerSecRead
specifier|private
name|Double
name|maxWriteMBPerSecRead
decl_stmt|;
DECL|field|maxWriteMBPerSecDefault
specifier|private
name|Double
name|maxWriteMBPerSecDefault
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|interface|CloseListener
specifier|public
interface|interface
name|CloseListener
block|{
DECL|method|postClose
specifier|public
name|void
name|postClose
parameter_list|()
function_decl|;
DECL|method|preClose
specifier|public
name|void
name|preClose
parameter_list|()
function_decl|;
block|}
annotation|@
name|Override
DECL|method|addCloseListener
specifier|public
name|void
name|addCloseListener
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|CloseListener
name|closeListener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|byDirectoryCache
operator|.
name|containsKey
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory: "
operator|+
name|dir
operator|+
literal|" "
operator|+
name|byDirectoryCache
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|CloseListener
argument_list|>
name|listeners
init|=
name|closeListeners
operator|.
name|get
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|listeners
operator|==
literal|null
condition|)
block|{
name|listeners
operator|=
operator|new
name|ArrayList
argument_list|<
name|CloseListener
argument_list|>
argument_list|()
expr_stmt|;
name|closeListeners
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|listeners
argument_list|)
expr_stmt|;
block|}
name|listeners
operator|.
name|add
argument_list|(
name|closeListener
argument_list|)
expr_stmt|;
name|closeListeners
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|listeners
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doneWithDirectory
specifier|public
name|void
name|doneWithDirectory
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|CacheValue
name|cacheValue
init|=
name|byDirectoryCache
operator|.
name|get
argument_list|(
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory: "
operator|+
name|directory
operator|+
literal|" "
operator|+
name|byDirectoryCache
argument_list|)
throw|;
block|}
name|cacheValue
operator|.
name|doneWithDir
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|cacheValue
operator|.
name|refCnt
operator|==
literal|0
condition|)
block|{
name|cacheValue
operator|.
name|refCnt
operator|++
expr_stmt|;
comment|// this will go back to 0 in close
name|close
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*    * (non-Javadoc)    *     * @see org.apache.solr.core.DirectoryFactory#close()    */
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
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
name|Collection
argument_list|<
name|CacheValue
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
name|values
operator|.
name|addAll
argument_list|(
name|byDirectoryCache
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CacheValue
name|val
range|:
name|values
control|)
block|{
try|try
block|{
comment|// if there are still refs out, we have to wait for them
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|val
operator|.
name|refCnt
operator|!=
literal|0
condition|)
block|{
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|cnt
operator|++
operator|>=
literal|1200
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Timeout waiting for all directory ref counts to be released"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
assert|assert
name|val
operator|.
name|refCnt
operator|==
literal|0
operator|:
name|val
operator|.
name|refCnt
assert|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error closing directory"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|values
operator|=
name|byDirectoryCache
operator|.
name|values
argument_list|()
expr_stmt|;
for|for
control|(
name|CacheValue
name|val
range|:
name|values
control|)
block|{
try|try
block|{
assert|assert
name|val
operator|.
name|refCnt
operator|==
literal|0
operator|:
name|val
operator|.
name|refCnt
assert|;
name|log
operator|.
name|info
argument_list|(
literal|"Closing directory when closing factory: "
operator|+
name|val
operator|.
name|path
argument_list|)
expr_stmt|;
name|closeDirectory
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error closing directory"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|byDirectoryCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|byPathCache
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|CacheValue
name|val
range|:
name|removeEntries
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Removing directory: "
operator|+
name|val
operator|.
name|path
argument_list|)
expr_stmt|;
name|removeDirectory
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|close
specifier|private
name|void
name|close
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// don't check if already closed here - we need to able to release
comment|// while #close() waits.
name|CacheValue
name|cacheValue
init|=
name|byDirectoryCache
operator|.
name|get
argument_list|(
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory: "
operator|+
name|directory
operator|+
literal|" "
operator|+
name|byDirectoryCache
argument_list|)
throw|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Releasing directory: "
operator|+
name|cacheValue
operator|.
name|path
argument_list|)
expr_stmt|;
name|cacheValue
operator|.
name|refCnt
operator|--
expr_stmt|;
if|if
condition|(
name|cacheValue
operator|.
name|refCnt
operator|==
literal|0
operator|&&
name|cacheValue
operator|.
name|doneWithDir
condition|)
block|{
name|closeDirectory
argument_list|(
name|cacheValue
argument_list|)
expr_stmt|;
name|byDirectoryCache
operator|.
name|remove
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|byPathCache
operator|.
name|remove
argument_list|(
name|cacheValue
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|closeDirectory
specifier|private
name|void
name|closeDirectory
parameter_list|(
name|CacheValue
name|cacheValue
parameter_list|)
block|{
name|List
argument_list|<
name|CloseListener
argument_list|>
name|listeners
init|=
name|closeListeners
operator|.
name|remove
argument_list|(
name|cacheValue
operator|.
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
name|listeners
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CloseListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|preClose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error executing preClose for directory"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|cacheValue
operator|.
name|closeDirectoryCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|cacheValue
operator|.
name|deleteOnClose
condition|)
block|{
comment|// see if we are a subpath
name|Collection
argument_list|<
name|CacheValue
argument_list|>
name|values
init|=
name|byPathCache
operator|.
name|values
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|CacheValue
argument_list|>
name|cacheValues
init|=
operator|new
name|ArrayList
argument_list|<
name|CacheValue
argument_list|>
argument_list|()
decl_stmt|;
name|cacheValues
operator|.
name|addAll
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|cacheValues
operator|.
name|remove
argument_list|(
name|cacheValue
argument_list|)
expr_stmt|;
for|for
control|(
name|CacheValue
name|otherCacheValue
range|:
name|cacheValues
control|)
block|{
comment|// if we are a parent path and all our sub children are not already closed,
comment|// get a sub path to close us later
if|if
condition|(
name|otherCacheValue
operator|.
name|path
operator|.
name|startsWith
argument_list|(
name|cacheValue
operator|.
name|path
argument_list|)
operator|&&
operator|!
name|otherCacheValue
operator|.
name|closeDirectoryCalled
condition|)
block|{
comment|// we let the sub dir remove and close us
if|if
condition|(
operator|!
name|otherCacheValue
operator|.
name|deleteAfterCoreClose
operator|&&
name|cacheValue
operator|.
name|deleteAfterCoreClose
condition|)
block|{
name|otherCacheValue
operator|.
name|deleteAfterCoreClose
operator|=
literal|true
expr_stmt|;
block|}
name|otherCacheValue
operator|.
name|removeEntries
operator|.
name|addAll
argument_list|(
name|cacheValue
operator|.
name|removeEntries
argument_list|)
expr_stmt|;
name|otherCacheValue
operator|.
name|closeEntries
operator|.
name|addAll
argument_list|(
name|cacheValue
operator|.
name|closeEntries
argument_list|)
expr_stmt|;
name|cacheValue
operator|.
name|closeEntries
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
for|for
control|(
name|CacheValue
name|val
range|:
name|cacheValue
operator|.
name|removeEntries
control|)
block|{
if|if
condition|(
operator|!
name|val
operator|.
name|deleteAfterCoreClose
condition|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Removing directory: "
operator|+
name|val
operator|.
name|path
argument_list|)
expr_stmt|;
name|removeDirectory
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error removing directory"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|removeEntries
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|CacheValue
name|val
range|:
name|cacheValue
operator|.
name|closeEntries
control|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Closing directory: "
operator|+
name|val
operator|.
name|path
argument_list|)
expr_stmt|;
name|val
operator|.
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error closing directory"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|listeners
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CloseListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postClose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error executing postClose for directory"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
specifier|abstract
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// back compat behavior
name|File
name|dirFile
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|dirFile
operator|.
name|canRead
argument_list|()
operator|&&
name|dirFile
operator|.
name|list
argument_list|()
operator|.
name|length
operator|>
literal|0
return|;
block|}
comment|/*    * (non-Javadoc)    *     * @see org.apache.solr.core.DirectoryFactory#get(java.lang.String,    * java.lang.String, boolean)    */
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|final
name|Directory
name|get
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|,
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fullPath
init|=
name|normalize
argument_list|(
name|path
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Already closed"
argument_list|)
throw|;
block|}
specifier|final
name|CacheValue
name|cacheValue
init|=
name|byPathCache
operator|.
name|get
argument_list|(
name|fullPath
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
name|cacheValue
operator|.
name|directory
expr_stmt|;
block|}
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|directory
operator|=
name|create
argument_list|(
name|fullPath
argument_list|,
name|dirContext
argument_list|)
expr_stmt|;
name|directory
operator|=
name|rateLimit
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|CacheValue
name|newCacheValue
init|=
operator|new
name|CacheValue
argument_list|(
name|fullPath
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|injectLockFactory
argument_list|(
name|directory
argument_list|,
name|fullPath
argument_list|,
name|rawLockType
argument_list|)
expr_stmt|;
name|byDirectoryCache
operator|.
name|put
argument_list|(
name|directory
argument_list|,
name|newCacheValue
argument_list|)
expr_stmt|;
name|byPathCache
operator|.
name|put
argument_list|(
name|fullPath
argument_list|,
name|newCacheValue
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"return new directory for "
operator|+
name|fullPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cacheValue
operator|.
name|refCnt
operator|++
expr_stmt|;
block|}
return|return
name|directory
return|;
block|}
block|}
DECL|method|rateLimit
specifier|private
name|Directory
name|rateLimit
parameter_list|(
name|Directory
name|directory
parameter_list|)
block|{
if|if
condition|(
name|maxWriteMBPerSecDefault
operator|!=
literal|null
operator|||
name|maxWriteMBPerSecFlush
operator|!=
literal|null
operator|||
name|maxWriteMBPerSecMerge
operator|!=
literal|null
operator|||
name|maxWriteMBPerSecRead
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
operator|new
name|RateLimitedDirectoryWrapper
argument_list|(
name|directory
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxWriteMBPerSecDefault
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|RateLimitedDirectoryWrapper
operator|)
name|directory
operator|)
operator|.
name|setMaxWriteMBPerSec
argument_list|(
name|maxWriteMBPerSecDefault
argument_list|,
name|Context
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxWriteMBPerSecFlush
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|RateLimitedDirectoryWrapper
operator|)
name|directory
operator|)
operator|.
name|setMaxWriteMBPerSec
argument_list|(
name|maxWriteMBPerSecFlush
argument_list|,
name|Context
operator|.
name|FLUSH
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxWriteMBPerSecMerge
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|RateLimitedDirectoryWrapper
operator|)
name|directory
operator|)
operator|.
name|setMaxWriteMBPerSec
argument_list|(
name|maxWriteMBPerSecMerge
argument_list|,
name|Context
operator|.
name|MERGE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxWriteMBPerSecRead
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|RateLimitedDirectoryWrapper
operator|)
name|directory
operator|)
operator|.
name|setMaxWriteMBPerSec
argument_list|(
name|maxWriteMBPerSecRead
argument_list|,
name|Context
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|directory
return|;
block|}
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.solr.core.DirectoryFactory#incRef(org.apache.lucene.store.Directory    * )    */
annotation|@
name|Override
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|(
name|Directory
name|directory
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|CacheValue
name|cacheValue
init|=
name|byDirectoryCache
operator|.
name|get
argument_list|(
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory: "
operator|+
name|directory
argument_list|)
throw|;
block|}
name|cacheValue
operator|.
name|refCnt
operator|++
expr_stmt|;
block|}
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
name|maxWriteMBPerSecFlush
operator|=
operator|(
name|Double
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxWriteMBPerSecFlush"
argument_list|)
expr_stmt|;
name|maxWriteMBPerSecMerge
operator|=
operator|(
name|Double
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxWriteMBPerSecMerge"
argument_list|)
expr_stmt|;
name|maxWriteMBPerSecRead
operator|=
operator|(
name|Double
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxWriteMBPerSecRead"
argument_list|)
expr_stmt|;
name|maxWriteMBPerSecDefault
operator|=
operator|(
name|Double
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxWriteMBPerSecDefault"
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.solr.core.DirectoryFactory#release(org.apache.lucene.store.Directory    * )    */
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|close
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|remove
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|remove
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|deleteAfterCoreClose
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|CacheValue
name|val
init|=
name|byPathCache
operator|.
name|get
argument_list|(
name|normalize
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory "
operator|+
name|path
argument_list|)
throw|;
block|}
name|val
operator|.
name|setDeleteOnClose
argument_list|(
literal|true
argument_list|,
name|deleteAfterCoreClose
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|deleteAfterCoreClose
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|CacheValue
name|val
init|=
name|byDirectoryCache
operator|.
name|get
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory "
operator|+
name|dir
argument_list|)
throw|;
block|}
name|val
operator|.
name|setDeleteOnClose
argument_list|(
literal|true
argument_list|,
name|deleteAfterCoreClose
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|injectLockFactory
specifier|private
specifier|static
name|Directory
name|injectLockFactory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|lockPath
parameter_list|,
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|rawLockType
condition|)
block|{
comment|// we default to "simple" for backwards compatibility
name|log
operator|.
name|warn
argument_list|(
literal|"No lockType configured for "
operator|+
name|dir
operator|+
literal|" assuming 'simple'"
argument_list|)
expr_stmt|;
name|rawLockType
operator|=
literal|"simple"
expr_stmt|;
block|}
specifier|final
name|String
name|lockType
init|=
name|rawLockType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"simple"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
comment|// multiple SimpleFSLockFactory instances should be OK
name|dir
operator|.
name|setLockFactory
argument_list|(
operator|new
name|SimpleFSLockFactory
argument_list|(
name|lockPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"native"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
name|dir
operator|.
name|setLockFactory
argument_list|(
operator|new
name|NativeFSLockFactory
argument_list|(
name|lockPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"single"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|dir
operator|.
name|getLockFactory
argument_list|()
operator|instanceof
name|SingleInstanceLockFactory
operator|)
condition|)
name|dir
operator|.
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
condition|)
block|{
comment|// Recipe for disaster
name|log
operator|.
name|error
argument_list|(
literal|"CONFIGURATION WARNING: locks are disabled on "
operator|+
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setLockFactory
argument_list|(
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unrecognized lockType: "
operator|+
name|rawLockType
argument_list|)
throw|;
block|}
return|return
name|dir
return|;
block|}
DECL|method|removeDirectory
specifier|protected
name|void
name|removeDirectory
parameter_list|(
name|CacheValue
name|cacheValue
parameter_list|)
throws|throws
name|IOException
block|{
name|empty
argument_list|(
name|cacheValue
operator|.
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|path
operator|=
name|stripTrailingSlash
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
DECL|method|stripTrailingSlash
specifier|private
name|String
name|stripTrailingSlash
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
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
return|return
name|path
return|;
block|}
block|}
end_class

end_unit

