begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|PrintStream
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|IndexReader
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
name|search
operator|.
name|cache
operator|.
name|ByteValuesCreator
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
name|search
operator|.
name|cache
operator|.
name|DocTermsCreator
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
name|search
operator|.
name|cache
operator|.
name|DocTermsIndexCreator
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
name|search
operator|.
name|cache
operator|.
name|DoubleValuesCreator
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
name|search
operator|.
name|cache
operator|.
name|EntryCreator
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
name|search
operator|.
name|cache
operator|.
name|FloatValuesCreator
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
name|search
operator|.
name|cache
operator|.
name|IntValuesCreator
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
name|search
operator|.
name|cache
operator|.
name|LongValuesCreator
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
name|search
operator|.
name|cache
operator|.
name|ShortValuesCreator
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
name|search
operator|.
name|cache
operator|.
name|CachedArray
operator|.
name|ByteValues
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
name|search
operator|.
name|cache
operator|.
name|CachedArray
operator|.
name|DoubleValues
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
name|search
operator|.
name|cache
operator|.
name|CachedArray
operator|.
name|FloatValues
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
name|search
operator|.
name|cache
operator|.
name|CachedArray
operator|.
name|IntValues
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
name|search
operator|.
name|cache
operator|.
name|CachedArray
operator|.
name|LongValues
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
name|search
operator|.
name|cache
operator|.
name|CachedArray
operator|.
name|ShortValues
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
name|FieldCacheSanityChecker
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
name|StringHelper
import|;
end_import

begin_comment
comment|/**  * Expert: The default cache implementation, storing all values in memory.  * A WeakHashMap is used for storage.  *  *<p>Created: May 19, 2004 4:40:36 PM  *   * @lucene.internal -- this is now public so that the tests can use reflection  * to call methods.  It will likely be removed without (much) notice.  *   * @since   lucene 1.4  */
end_comment

begin_class
DECL|class|FieldCacheImpl
specifier|public
class|class
name|FieldCacheImpl
implements|implements
name|FieldCache
block|{
comment|// Made Public so that
DECL|field|caches
specifier|private
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Cache
argument_list|>
name|caches
decl_stmt|;
DECL|method|FieldCacheImpl
name|FieldCacheImpl
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init
specifier|private
specifier|synchronized
name|void
name|init
parameter_list|()
block|{
name|caches
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Cache
argument_list|>
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|Byte
operator|.
name|TYPE
argument_list|,
operator|new
name|Cache
argument_list|<
name|ByteValues
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|Short
operator|.
name|TYPE
argument_list|,
operator|new
name|Cache
argument_list|<
name|ShortValues
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|TYPE
argument_list|,
operator|new
name|Cache
argument_list|<
name|IntValues
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|Float
operator|.
name|TYPE
argument_list|,
operator|new
name|Cache
argument_list|<
name|FloatValues
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|Long
operator|.
name|TYPE
argument_list|,
operator|new
name|Cache
argument_list|<
name|LongValues
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|Double
operator|.
name|TYPE
argument_list|,
operator|new
name|Cache
argument_list|<
name|DoubleValues
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|DocTermsIndex
operator|.
name|class
argument_list|,
operator|new
name|Cache
argument_list|<
name|DocTermsIndex
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|caches
operator|.
name|put
argument_list|(
name|DocTerms
operator|.
name|class
argument_list|,
operator|new
name|Cache
argument_list|<
name|DocTerms
argument_list|>
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|purgeAllCaches
specifier|public
specifier|synchronized
name|void
name|purgeAllCaches
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|purge
specifier|public
specifier|synchronized
name|void
name|purge
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
for|for
control|(
name|Cache
name|c
range|:
name|caches
operator|.
name|values
argument_list|()
control|)
block|{
name|c
operator|.
name|purge
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCacheEntries
specifier|public
specifier|synchronized
name|CacheEntry
index|[]
name|getCacheEntries
parameter_list|()
block|{
name|List
argument_list|<
name|CacheEntry
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|CacheEntry
argument_list|>
argument_list|(
literal|17
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Cache
argument_list|>
name|cacheEntry
range|:
name|caches
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Cache
argument_list|<
name|?
argument_list|>
name|cache
init|=
name|cacheEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cacheType
init|=
name|cacheEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|cache
operator|.
name|readerCache
init|)
block|{
for|for
control|(
name|Object
name|readerKey
range|:
name|cache
operator|.
name|readerCache
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|Object
argument_list|>
name|innerCache
init|=
name|cache
operator|.
name|readerCache
operator|.
name|get
argument_list|(
name|readerKey
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|Object
argument_list|>
name|mapEntry
range|:
name|innerCache
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|mapEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|CacheEntryImpl
argument_list|(
name|readerKey
argument_list|,
name|entry
operator|.
name|field
argument_list|,
name|cacheType
argument_list|,
name|entry
operator|.
name|creator
argument_list|,
name|mapEntry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|CacheEntry
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|class|CacheEntryImpl
specifier|private
specifier|static
specifier|final
class|class
name|CacheEntryImpl
extends|extends
name|CacheEntry
block|{
DECL|field|readerKey
specifier|private
specifier|final
name|Object
name|readerKey
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|cacheType
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cacheType
decl_stmt|;
DECL|field|custom
specifier|private
specifier|final
name|EntryCreator
name|custom
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
DECL|method|CacheEntryImpl
name|CacheEntryImpl
parameter_list|(
name|Object
name|readerKey
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|cacheType
parameter_list|,
name|EntryCreator
name|custom
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|readerKey
operator|=
name|readerKey
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|cacheType
operator|=
name|cacheType
expr_stmt|;
name|this
operator|.
name|custom
operator|=
name|custom
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
comment|// :HACK: for testing.
comment|//         if (null != locale || SortField.CUSTOM != sortFieldType) {
comment|//           throw new RuntimeException("Locale/sortFieldType: " + this);
comment|//         }
block|}
annotation|@
name|Override
DECL|method|getReaderKey
specifier|public
name|Object
name|getReaderKey
parameter_list|()
block|{
return|return
name|readerKey
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheType
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getCacheType
parameter_list|()
block|{
return|return
name|cacheType
return|;
block|}
annotation|@
name|Override
DECL|method|getCustom
specifier|public
name|Object
name|getCustom
parameter_list|()
block|{
return|return
name|custom
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|field|purgeReader
specifier|final
specifier|static
name|IndexReader
operator|.
name|ReaderFinishedListener
name|purgeReader
init|=
operator|new
name|IndexReader
operator|.
name|ReaderFinishedListener
argument_list|()
block|{
comment|// @Override -- not until Java 1.6
specifier|public
name|void
name|finished
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purge
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/** Expert: Internal cache. */
DECL|class|Cache
specifier|final
specifier|static
class|class
name|Cache
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|Cache
name|Cache
parameter_list|()
block|{
name|this
operator|.
name|wrapper
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|Cache
name|Cache
parameter_list|(
name|FieldCache
name|wrapper
parameter_list|)
block|{
name|this
operator|.
name|wrapper
operator|=
name|wrapper
expr_stmt|;
block|}
DECL|field|wrapper
specifier|final
name|FieldCache
name|wrapper
decl_stmt|;
DECL|field|readerCache
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Map
argument_list|<
name|Entry
argument_list|<
name|T
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|readerCache
init|=
operator|new
name|WeakHashMap
argument_list|<
name|Object
argument_list|,
name|Map
argument_list|<
name|Entry
argument_list|<
name|T
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|createValue
specifier|protected
name|Object
name|createValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Entry
name|entryKey
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|entryKey
operator|.
name|creator
operator|.
name|create
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/** Remove this reader from the cache, if present. */
DECL|method|purge
specifier|public
name|void
name|purge
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|Object
name|readerKey
init|=
name|r
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|readerCache
operator|.
name|remove
argument_list|(
name|readerKey
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Entry
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|Entry
argument_list|<
name|T
argument_list|>
argument_list|,
name|Object
argument_list|>
name|innerCache
decl_stmt|;
name|Object
name|value
decl_stmt|;
specifier|final
name|Object
name|readerKey
init|=
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|innerCache
operator|=
name|readerCache
operator|.
name|get
argument_list|(
name|readerKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerCache
operator|==
literal|null
condition|)
block|{
comment|// First time this reader is using FieldCache
name|innerCache
operator|=
operator|new
name|HashMap
argument_list|<
name|Entry
argument_list|<
name|T
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|readerCache
operator|.
name|put
argument_list|(
name|readerKey
argument_list|,
name|innerCache
argument_list|)
expr_stmt|;
name|reader
operator|.
name|addReaderFinishedListener
argument_list|(
name|purgeReader
argument_list|)
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|innerCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|CreationPlaceholder
argument_list|()
expr_stmt|;
name|innerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|CreationPlaceholder
condition|)
block|{
synchronized|synchronized
init|(
name|value
init|)
block|{
name|CreationPlaceholder
name|progress
init|=
operator|(
name|CreationPlaceholder
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|progress
operator|.
name|value
operator|==
literal|null
condition|)
block|{
name|progress
operator|.
name|value
operator|=
name|createValue
argument_list|(
name|reader
argument_list|,
name|key
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|readerCache
init|)
block|{
name|innerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|progress
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
comment|// Only check if key.custom (the parser) is
comment|// non-null; else, we check twice for a single
comment|// call to FieldCache.getXXX
if|if
condition|(
name|key
operator|.
name|creator
operator|!=
literal|null
operator|&&
name|wrapper
operator|!=
literal|null
condition|)
block|{
specifier|final
name|PrintStream
name|infoStream
init|=
name|wrapper
operator|.
name|getInfoStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|printNewInsanity
argument_list|(
name|infoStream
argument_list|,
name|progress
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|progress
operator|.
name|value
return|;
block|}
block|}
comment|// Validate new entries
if|if
condition|(
name|key
operator|.
name|creator
operator|.
name|shouldValidate
argument_list|()
condition|)
block|{
name|key
operator|.
name|creator
operator|.
name|validate
argument_list|(
operator|(
name|T
operator|)
name|value
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
DECL|method|printNewInsanity
specifier|private
name|void
name|printNewInsanity
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
specifier|final
name|FieldCacheSanityChecker
operator|.
name|Insanity
index|[]
name|insanities
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|wrapper
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
name|insanities
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FieldCacheSanityChecker
operator|.
name|Insanity
name|insanity
init|=
name|insanities
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|CacheEntry
index|[]
name|entries
init|=
name|insanity
operator|.
name|getCacheEntries
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|entries
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|entries
index|[
name|j
index|]
operator|.
name|getValue
argument_list|()
operator|==
name|value
condition|)
block|{
comment|// OK this insanity involves our entry
name|infoStream
operator|.
name|println
argument_list|(
literal|"WARNING: new FieldCache insanity created\nDetails: "
operator|+
name|insanity
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|infoStream
operator|.
name|println
argument_list|(
literal|"\nStack:\n"
argument_list|)
expr_stmt|;
operator|new
name|Throwable
argument_list|()
operator|.
name|printStackTrace
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
comment|/** Expert: Every composite-key in the internal cache is of this type. */
DECL|class|Entry
specifier|static
class|class
name|Entry
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
comment|// which Fieldable
DECL|field|creator
specifier|final
name|EntryCreator
argument_list|<
name|T
argument_list|>
name|creator
decl_stmt|;
comment|// which custom comparator or parser
comment|/** Creates one of these objects for a custom comparator/parser. */
DECL|method|Entry
name|Entry
parameter_list|(
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|T
argument_list|>
name|custom
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|creator
operator|=
name|custom
expr_stmt|;
block|}
comment|/** Two of these are equal iff they reference the same field and type. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Entry
condition|)
block|{
name|Entry
name|other
init|=
operator|(
name|Entry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|field
operator|==
name|field
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|creator
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|creator
operator|==
literal|null
condition|)
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|creator
operator|.
name|equals
argument_list|(
name|creator
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Composes a hashcode based on the field and type. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|creator
operator|==
literal|null
condition|?
literal|0
else|:
name|creator
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
block|}
comment|// inherit javadocs
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBytes
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|ByteValuesCreator
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
comment|// inherit javadocs
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ByteParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBytes
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|ByteValuesCreator
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getBytes
specifier|public
name|ByteValues
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|ByteValues
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|ByteValues
operator|)
name|caches
operator|.
name|get
argument_list|(
name|Byte
operator|.
name|TYPE
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getShorts
specifier|public
name|short
index|[]
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getShorts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|ShortValuesCreator
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
comment|// inherit javadocs
DECL|method|getShorts
specifier|public
name|short
index|[]
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ShortParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getShorts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|ShortValuesCreator
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getShorts
specifier|public
name|ShortValues
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|ShortValues
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|ShortValues
operator|)
name|caches
operator|.
name|get
argument_list|(
name|Short
operator|.
name|TYPE
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|IntValuesCreator
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
comment|// inherit javadocs
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|IntParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|IntValuesCreator
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getInts
specifier|public
name|IntValues
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|IntValues
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|IntValues
operator|)
name|caches
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|TYPE
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|FloatValuesCreator
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
comment|// inherit javadocs
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|FloatParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|FloatValuesCreator
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getFloats
specifier|public
name|FloatValues
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|FloatValues
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|FloatValues
operator|)
name|caches
operator|.
name|get
argument_list|(
name|Float
operator|.
name|TYPE
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|LongValuesCreator
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
comment|// inherit javadocs
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|LongParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLongs
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|LongValuesCreator
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getLongs
specifier|public
name|LongValues
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|LongValues
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|LongValues
operator|)
name|caches
operator|.
name|get
argument_list|(
name|Long
operator|.
name|TYPE
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
comment|// inherit javadocs
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DoubleValuesCreator
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
comment|// inherit javadocs
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|DoubleParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DoubleValuesCreator
argument_list|(
name|field
argument_list|,
name|parser
argument_list|)
argument_list|)
operator|.
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getDoubles
specifier|public
name|DoubleValues
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|DoubleValues
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|DoubleValues
operator|)
name|caches
operator|.
name|get
argument_list|(
name|Double
operator|.
name|TYPE
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTermsIndex
specifier|public
name|DocTermsIndex
name|getTermsIndex
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DocTermsIndexCreator
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTermsIndex
specifier|public
name|DocTermsIndex
name|getTermsIndex
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|fasterButMoreRAM
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTermsIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DocTermsIndexCreator
argument_list|(
name|field
argument_list|,
name|fasterButMoreRAM
condition|?
name|DocTermsIndexCreator
operator|.
name|FASTER_BUT_MORE_RAM
else|:
literal|0
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTermsIndex
specifier|public
name|DocTermsIndex
name|getTermsIndex
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|DocTermsIndex
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|DocTermsIndex
operator|)
name|caches
operator|.
name|get
argument_list|(
name|DocTermsIndex
operator|.
name|class
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
comment|// TODO: this if DocTermsIndex was already created, we
comment|// should share it...
DECL|method|getTerms
specifier|public
name|DocTerms
name|getTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DocTermsCreator
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTerms
specifier|public
name|DocTerms
name|getTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|fasterButMoreRAM
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
operator|new
name|DocTermsCreator
argument_list|(
name|field
argument_list|,
name|fasterButMoreRAM
condition|?
name|DocTermsCreator
operator|.
name|FASTER_BUT_MORE_RAM
else|:
literal|0
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTerms
specifier|public
name|DocTerms
name|getTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|EntryCreator
argument_list|<
name|DocTerms
argument_list|>
name|creator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|DocTerms
operator|)
name|caches
operator|.
name|get
argument_list|(
name|DocTerms
operator|.
name|class
argument_list|)
operator|.
name|get
argument_list|(
name|reader
argument_list|,
operator|new
name|Entry
argument_list|(
name|field
argument_list|,
name|creator
argument_list|)
argument_list|)
return|;
block|}
DECL|field|infoStream
specifier|private
specifier|volatile
name|PrintStream
name|infoStream
decl_stmt|;
DECL|method|setInfoStream
specifier|public
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|stream
parameter_list|)
block|{
name|infoStream
operator|=
name|stream
expr_stmt|;
block|}
DECL|method|getInfoStream
specifier|public
name|PrintStream
name|getInfoStream
parameter_list|()
block|{
return|return
name|infoStream
return|;
block|}
block|}
end_class

end_unit

