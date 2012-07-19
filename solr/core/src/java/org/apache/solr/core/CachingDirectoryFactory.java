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
comment|/**  * A {@link DirectoryFactory} impl base class for caching Directory instances  * per path. Most DirectoryFactory implementations will want to extend this  * class and simply implement {@link DirectoryFactory#create(String)}.  *   */
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
class|class
name|CacheValue
block|{
DECL|field|directory
name|Directory
name|directory
decl_stmt|;
DECL|field|refCnt
name|int
name|refCnt
init|=
literal|1
decl_stmt|;
DECL|field|path
specifier|public
name|String
name|path
decl_stmt|;
DECL|field|doneWithDir
specifier|public
name|boolean
name|doneWithDir
init|=
literal|false
decl_stmt|;
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
name|HashMap
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
DECL|interface|CloseListener
specifier|public
interface|interface
name|CloseListener
block|{
DECL|method|onClose
specifier|public
name|void
name|onClose
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
for|for
control|(
name|CacheValue
name|val
range|:
name|byDirectoryCache
operator|.
name|values
argument_list|()
control|)
block|{
name|val
operator|.
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|directory
operator|.
name|close
argument_list|()
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
name|listener
operator|.
name|onClose
argument_list|()
expr_stmt|;
block|}
name|closeListeners
operator|.
name|remove
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|create
specifier|protected
specifier|abstract
name|Directory
name|create
parameter_list|(
name|String
name|path
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
comment|/*    * (non-Javadoc)    *     * @see org.apache.solr.core.DirectoryFactory#get(java.lang.String,    * java.lang.String)    */
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
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|path
argument_list|,
name|rawLockType
argument_list|,
literal|false
argument_list|)
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
name|String
name|rawLockType
parameter_list|,
name|boolean
name|forceNew
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fullPath
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
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
if|if
condition|(
name|forceNew
condition|)
block|{
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
name|close
argument_list|(
name|cacheValue
operator|.
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|directory
operator|==
literal|null
operator|||
name|forceNew
condition|)
block|{
name|directory
operator|=
name|create
argument_list|(
name|fullPath
argument_list|)
expr_stmt|;
name|CacheValue
name|newCacheValue
init|=
operator|new
name|CacheValue
argument_list|()
decl_stmt|;
name|newCacheValue
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|newCacheValue
operator|.
name|path
operator|=
name|fullPath
expr_stmt|;
name|injectLockFactory
argument_list|(
name|directory
argument_list|,
name|path
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
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.solr.core.DirectoryFactory#incRef(org.apache.lucene.store.Directory    * )    */
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
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{}
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
comment|/**    * @param dir    * @param lockPath    * @param rawLockType    * @return    * @throws IOException    */
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
block|}
end_class

end_unit

