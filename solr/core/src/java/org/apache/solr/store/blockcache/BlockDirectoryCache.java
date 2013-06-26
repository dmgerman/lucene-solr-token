begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|BlockDirectoryCache
specifier|public
class|class
name|BlockDirectoryCache
implements|implements
name|Cache
block|{
DECL|field|blockCache
specifier|private
name|BlockCache
name|blockCache
decl_stmt|;
DECL|field|counter
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|names
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|names
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|metrics
specifier|private
name|Metrics
name|metrics
decl_stmt|;
DECL|method|BlockDirectoryCache
specifier|public
name|BlockDirectoryCache
parameter_list|(
name|BlockCache
name|blockCache
parameter_list|,
name|Metrics
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|blockCache
operator|=
name|blockCache
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|names
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|blockId
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|Integer
name|file
init|=
name|names
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
name|file
operator|=
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|names
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
name|BlockCacheKey
name|blockCacheKey
init|=
operator|new
name|BlockCacheKey
argument_list|()
decl_stmt|;
name|blockCacheKey
operator|.
name|setBlock
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|blockCache
operator|.
name|store
argument_list|(
name|blockCacheKey
argument_list|,
name|blockOffset
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fetch
specifier|public
name|boolean
name|fetch
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|blockId
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|lengthToReadInBlock
parameter_list|)
block|{
name|Integer
name|file
init|=
name|names
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BlockCacheKey
name|blockCacheKey
init|=
operator|new
name|BlockCacheKey
argument_list|()
decl_stmt|;
name|blockCacheKey
operator|.
name|setBlock
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|boolean
name|fetch
init|=
name|blockCache
operator|.
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|b
argument_list|,
name|blockOffset
argument_list|,
name|off
argument_list|,
name|lengthToReadInBlock
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetch
condition|)
block|{
name|metrics
operator|.
name|blockCacheHit
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|metrics
operator|.
name|blockCacheMiss
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|fetch
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|blockCache
operator|.
name|getSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|renameCacheFile
specifier|public
name|void
name|renameCacheFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
block|{
name|Integer
name|file
init|=
name|names
operator|.
name|remove
argument_list|(
name|source
argument_list|)
decl_stmt|;
comment|// possible if the file is empty
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|names
operator|.
name|put
argument_list|(
name|dest
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

