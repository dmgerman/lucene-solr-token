begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|util
operator|.
name|SimpleOrderedMap
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|LRUCache
specifier|public
class|class
name|LRUCache
implements|implements
name|SolrCache
block|{
comment|/* An instance of this class will be shared across multiple instances    * of an LRUCache at the same time.  Make sure everything is thread safe.    */
DECL|class|CumulativeStats
specifier|private
specifier|static
class|class
name|CumulativeStats
block|{
DECL|field|lookups
name|AtomicLong
name|lookups
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|hits
name|AtomicLong
name|hits
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|inserts
name|AtomicLong
name|inserts
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|evictions
name|AtomicLong
name|evictions
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
block|}
DECL|field|stats
specifier|private
name|CumulativeStats
name|stats
decl_stmt|;
comment|// per instance stats.  The synchronization used for the map will also be
comment|// used for updating these statistics (and hence they are not AtomicLongs
DECL|field|lookups
specifier|private
name|long
name|lookups
decl_stmt|;
DECL|field|hits
specifier|private
name|long
name|hits
decl_stmt|;
DECL|field|inserts
specifier|private
name|long
name|inserts
decl_stmt|;
DECL|field|evictions
specifier|private
name|long
name|evictions
decl_stmt|;
DECL|field|map
specifier|private
name|Map
name|map
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|autowarmCount
specifier|private
name|int
name|autowarmCount
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|regenerator
specifier|private
name|CacheRegenerator
name|regenerator
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
init|=
literal|"LRU Cache"
decl_stmt|;
DECL|method|init
specifier|public
name|Object
name|init
parameter_list|(
name|Map
name|args
parameter_list|,
name|Object
name|persistence
parameter_list|,
name|CacheRegenerator
name|regenerator
parameter_list|)
block|{
name|state
operator|=
name|State
operator|.
name|CREATED
expr_stmt|;
name|this
operator|.
name|regenerator
operator|=
name|regenerator
expr_stmt|;
name|name
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|str
operator|==
literal|null
condition|?
literal|1024
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"initialSize"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|initialSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|str
operator|==
literal|null
condition|?
literal|1024
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"autowarmCount"
argument_list|)
expr_stmt|;
name|autowarmCount
operator|=
name|str
operator|==
literal|null
condition|?
literal|0
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|description
operator|=
literal|"LRU Cache(maxSize="
operator|+
name|limit
operator|+
literal|", initialSize="
operator|+
name|initialSize
expr_stmt|;
if|if
condition|(
name|autowarmCount
operator|>
literal|0
condition|)
block|{
name|description
operator|+=
literal|", autowarmCount="
operator|+
name|autowarmCount
operator|+
literal|", regenerator="
operator|+
name|regenerator
expr_stmt|;
block|}
name|description
operator|+=
literal|')'
expr_stmt|;
name|map
operator|=
operator|new
name|LinkedHashMap
argument_list|(
name|initialSize
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
block|{
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
name|eldest
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|>
name|limit
condition|)
block|{
comment|// increment evictions regardless of state.
comment|// this doesn't need to be synchronized because it will
comment|// only be called in the context of a higher level synchronized block.
name|evictions
operator|++
expr_stmt|;
name|stats
operator|.
name|evictions
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
expr_stmt|;
if|if
condition|(
name|persistence
operator|==
literal|null
condition|)
block|{
comment|// must be the first time a cache of this type is being created
name|persistence
operator|=
operator|new
name|CumulativeStats
argument_list|()
expr_stmt|;
block|}
name|stats
operator|=
operator|(
name|CumulativeStats
operator|)
name|persistence
expr_stmt|;
return|return
name|persistence
return|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|method|put
specifier|public
specifier|synchronized
name|Object
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|LIVE
condition|)
block|{
name|stats
operator|.
name|inserts
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|map
init|)
block|{
comment|// increment local inserts regardless of state???
comment|// it does make it more consistent with the current size...
name|inserts
operator|++
expr_stmt|;
return|return
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
name|Object
name|val
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|LIVE
condition|)
block|{
comment|// only increment lookups and hits if we are live.
name|lookups
operator|++
expr_stmt|;
name|stats
operator|.
name|lookups
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|hits
operator|++
expr_stmt|;
name|stats
operator|.
name|hits
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|val
return|;
block|}
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|map
init|)
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setState
specifier|public
name|void
name|setState
parameter_list|(
name|State
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|warm
specifier|public
name|void
name|warm
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|SolrCache
name|old
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|regenerator
operator|==
literal|null
condition|)
return|return;
name|LRUCache
name|other
init|=
operator|(
name|LRUCache
operator|)
name|old
decl_stmt|;
comment|// warm entries
if|if
condition|(
name|autowarmCount
operator|!=
literal|0
condition|)
block|{
name|Object
index|[]
name|keys
decl_stmt|,
name|vals
init|=
literal|null
decl_stmt|;
comment|// Don't do the autowarming in the synchronized block, just pull out the keys and values.
synchronized|synchronized
init|(
name|other
operator|.
name|map
init|)
block|{
name|int
name|sz
init|=
name|other
operator|.
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|autowarmCount
operator|!=
operator|-
literal|1
condition|)
name|sz
operator|=
name|Math
operator|.
name|min
argument_list|(
name|sz
argument_list|,
name|autowarmCount
argument_list|)
expr_stmt|;
name|keys
operator|=
operator|new
name|Object
index|[
name|sz
index|]
expr_stmt|;
name|vals
operator|=
operator|new
name|Object
index|[
name|sz
index|]
expr_stmt|;
name|Iterator
name|iter
init|=
name|other
operator|.
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// iteration goes from oldest (least recently used) to most recently used,
comment|// so we need to skip over the oldest entries.
name|int
name|skip
init|=
name|other
operator|.
name|map
operator|.
name|size
argument_list|()
operator|-
name|sz
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
name|skip
condition|;
name|i
operator|++
control|)
name|iter
operator|.
name|next
argument_list|()
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|keys
index|[
name|i
index|]
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|vals
index|[
name|i
index|]
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|// autowarm from the oldest to the newest entries so that the ordering will be
comment|// correct in the new cache.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|boolean
name|continueRegen
init|=
name|regenerator
operator|.
name|regenerateItem
argument_list|(
name|searcher
argument_list|,
name|this
argument_list|,
name|old
argument_list|,
name|keys
index|[
name|i
index|]
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|continueRegen
condition|)
break|break;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error during auto-warming of key:"
operator|+
name|keys
index|[
name|i
index|]
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|LRUCache
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|CACHE
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// returns a ratio, not a percent.
DECL|method|calcHitRatio
specifier|private
specifier|static
name|String
name|calcHitRatio
parameter_list|(
name|long
name|lookups
parameter_list|,
name|long
name|hits
parameter_list|)
block|{
if|if
condition|(
name|lookups
operator|==
literal|0
condition|)
return|return
literal|"0.00"
return|;
if|if
condition|(
name|lookups
operator|==
name|hits
condition|)
return|return
literal|"1.00"
return|;
name|int
name|hundredths
init|=
call|(
name|int
call|)
argument_list|(
name|hits
operator|*
literal|100
operator|/
name|lookups
argument_list|)
decl_stmt|;
comment|// rounded down
if|if
condition|(
name|hundredths
operator|<
literal|10
condition|)
return|return
literal|"0.0"
operator|+
name|hundredths
return|;
return|return
literal|"0."
operator|+
name|hundredths
return|;
comment|/*** code to produce a percent, if we want it...     int ones = (int)(hits*100 / lookups);     int tenths = (int)(hits*1000 / lookups) - ones*10;     return Integer.toString(ones) + '.' + tenths;     ***/
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|map
init|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"lookups"
argument_list|,
name|lookups
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"hits"
argument_list|,
name|hits
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"hitratio"
argument_list|,
name|calcHitRatio
argument_list|(
name|lookups
argument_list|,
name|hits
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"inserts"
argument_list|,
name|inserts
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"evictions"
argument_list|,
name|evictions
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|clookups
init|=
name|stats
operator|.
name|lookups
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|chits
init|=
name|stats
operator|.
name|hits
operator|.
name|get
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_lookups"
argument_list|,
name|clookups
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_hits"
argument_list|,
name|chits
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_hitratio"
argument_list|,
name|calcHitRatio
argument_list|(
name|clookups
argument_list|,
name|chits
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_inserts"
argument_list|,
name|stats
operator|.
name|inserts
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_evictions"
argument_list|,
name|stats
operator|.
name|evictions
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
name|getStatistics
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

