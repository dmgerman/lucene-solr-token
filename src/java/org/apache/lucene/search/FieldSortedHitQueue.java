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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|PriorityQueue
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
name|WeakHashMap
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
name|Map
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
name|text
operator|.
name|Collator
import|;
end_import

begin_comment
comment|/**  * Expert: A hit queue for sorting by hits by terms in more than one field.  * Uses<code>FieldCache.DEFAULT</code> for maintaining internal term lookup tables.  *  *<p>Created: Dec 8, 2003 12:56:03 PM  *  * @author  Tim Jones (Nacimiento Software)  * @since   lucene 1.4  * @version $Id$  * @see Searchable#search(Query,Filter,int,Sort)  * @see FieldCache  */
end_comment

begin_class
DECL|class|FieldSortedHitQueue
class|class
name|FieldSortedHitQueue
extends|extends
name|PriorityQueue
block|{
comment|/**    * Creates a hit queue sorted by the given list of fields.    * @param reader  Index to use.    * @param fields Field names, in priority order (highest priority first).  Cannot be<code>null</code> or empty.    * @param size  The number of hits to retain.  Must be greater than zero.    * @throws IOException    */
DECL|method|FieldSortedHitQueue
name|FieldSortedHitQueue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|comparators
operator|=
operator|new
name|ScoreDocComparator
index|[
name|n
index|]
expr_stmt|;
name|this
operator|.
name|fields
operator|=
operator|new
name|SortField
index|[
name|n
index|]
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|String
name|fieldname
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
decl_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|getCachedComparator
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getLocale
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getFactory
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|comparators
index|[
name|i
index|]
operator|.
name|sortType
argument_list|()
argument_list|,
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/** Stores a comparator corresponding to each field being sorted by */
DECL|field|comparators
specifier|protected
name|ScoreDocComparator
index|[]
name|comparators
decl_stmt|;
comment|/** Stores the sort criteria being used. */
DECL|field|fields
specifier|protected
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/** Stores the maximum score value encountered, for normalizing.    *  we only care about scores greater than 1.0 - if all the scores    *  are less than 1.0, we don't have to normalize. */
DECL|field|maxscore
specifier|protected
name|float
name|maxscore
init|=
literal|1.0f
decl_stmt|;
comment|/**    * Returns whether<code>a</code> is less relevant than<code>b</code>.    * @param a ScoreDoc    * @param b ScoreDoc    * @return<code>true</code> if document<code>a</code> should be sorted after document<code>b</code>.    */
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|Object
name|a
parameter_list|,
specifier|final
name|Object
name|b
parameter_list|)
block|{
specifier|final
name|ScoreDoc
name|docA
init|=
operator|(
name|ScoreDoc
operator|)
name|a
decl_stmt|;
specifier|final
name|ScoreDoc
name|docB
init|=
operator|(
name|ScoreDoc
operator|)
name|b
decl_stmt|;
comment|// keep track of maximum score
if|if
condition|(
name|docA
operator|.
name|score
operator|>
name|maxscore
condition|)
name|maxscore
operator|=
name|docA
operator|.
name|score
expr_stmt|;
if|if
condition|(
name|docB
operator|.
name|score
operator|>
name|maxscore
condition|)
name|maxscore
operator|=
name|docB
operator|.
name|score
expr_stmt|;
comment|// run comparators
specifier|final
name|int
name|n
init|=
name|comparators
operator|.
name|length
decl_stmt|;
name|int
name|c
init|=
literal|0
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
name|n
operator|&&
name|c
operator|==
literal|0
condition|;
operator|++
name|i
control|)
block|{
name|c
operator|=
operator|(
name|fields
index|[
name|i
index|]
operator|.
name|reverse
operator|)
condition|?
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docB
argument_list|,
name|docA
argument_list|)
else|:
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|docA
argument_list|,
name|docB
argument_list|)
expr_stmt|;
block|}
comment|// avoid random sort order that could lead to duplicates (bug #31241):
if|if
condition|(
name|c
operator|==
literal|0
condition|)
return|return
name|docA
operator|.
name|doc
operator|>
name|docB
operator|.
name|doc
return|;
return|return
name|c
operator|>
literal|0
return|;
block|}
comment|/**    * Given a FieldDoc object, stores the values used    * to sort the given document.  These values are not the raw    * values out of the index, but the internal representation    * of them.  This is so the given search hit can be collated    * by a MultiSearcher with other search hits.    * @param  doc  The FieldDoc to store sort values into.    * @return  The same FieldDoc passed in.    * @see Searchable#search(Query,Filter,int,Sort)    */
DECL|method|fillFields
name|FieldDoc
name|fillFields
parameter_list|(
specifier|final
name|FieldDoc
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|comparators
operator|.
name|length
decl_stmt|;
specifier|final
name|Comparable
index|[]
name|fields
init|=
operator|new
name|Comparable
index|[
name|n
index|]
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
name|n
condition|;
operator|++
name|i
control|)
name|fields
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|sortValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
if|if
condition|(
name|maxscore
operator|>
literal|1.0f
condition|)
name|doc
operator|.
name|score
operator|/=
name|maxscore
expr_stmt|;
comment|// normalize scores
return|return
name|doc
return|;
block|}
comment|/** Returns the SortFields being used by this hit queue. */
DECL|method|getFields
name|SortField
index|[]
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/** Internal cache of comparators. Similar to FieldCache, only    *  caches comparators instead of term values. */
DECL|field|Comparators
specifier|static
specifier|final
name|Map
name|Comparators
init|=
operator|new
name|WeakHashMap
argument_list|()
decl_stmt|;
comment|/** Returns a comparator if it is in the cache. */
DECL|method|lookup
specifier|static
name|ScoreDocComparator
name|lookup
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|,
name|Object
name|factory
parameter_list|)
block|{
name|FieldCacheImpl
operator|.
name|Entry
name|entry
init|=
operator|(
name|factory
operator|!=
literal|null
operator|)
condition|?
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|factory
argument_list|)
else|:
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|Comparators
init|)
block|{
name|HashMap
name|readerCache
init|=
operator|(
name|HashMap
operator|)
name|Comparators
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerCache
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|ScoreDocComparator
operator|)
name|readerCache
operator|.
name|get
argument_list|(
name|entry
argument_list|)
return|;
block|}
block|}
comment|/** Stores a comparator into the cache. */
DECL|method|store
specifier|static
name|Object
name|store
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|,
name|Object
name|factory
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|FieldCacheImpl
operator|.
name|Entry
name|entry
init|=
operator|(
name|factory
operator|!=
literal|null
operator|)
condition|?
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|factory
argument_list|)
else|:
operator|new
name|FieldCacheImpl
operator|.
name|Entry
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|Comparators
init|)
block|{
name|HashMap
name|readerCache
init|=
operator|(
name|HashMap
operator|)
name|Comparators
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|readerCache
operator|==
literal|null
condition|)
block|{
name|readerCache
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|Comparators
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|readerCache
argument_list|)
expr_stmt|;
block|}
return|return
name|readerCache
operator|.
name|put
argument_list|(
name|entry
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
DECL|method|getCachedComparator
specifier|static
name|ScoreDocComparator
name|getCachedComparator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldname
parameter_list|,
name|int
name|type
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|SortComparatorSource
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|DOC
condition|)
return|return
name|ScoreDocComparator
operator|.
name|INDEXORDER
return|;
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|SCORE
condition|)
return|return
name|ScoreDocComparator
operator|.
name|RELEVANCE
return|;
name|ScoreDocComparator
name|comparator
init|=
name|lookup
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
name|type
argument_list|,
name|factory
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparator
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SortField
operator|.
name|AUTO
case|:
name|comparator
operator|=
name|comparatorAuto
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|INT
case|:
name|comparator
operator|=
name|comparatorInt
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|FLOAT
case|:
name|comparator
operator|=
name|comparatorFloat
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|STRING
case|:
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|comparator
operator|=
name|comparatorStringLocale
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
name|locale
argument_list|)
expr_stmt|;
else|else
name|comparator
operator|=
name|comparatorString
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
case|case
name|SortField
operator|.
name|CUSTOM
case|:
name|comparator
operator|=
name|factory
operator|.
name|newComparator
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown field type: "
operator|+
name|type
argument_list|)
throw|;
block|}
name|store
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|,
name|type
argument_list|,
name|factory
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
return|return
name|comparator
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing integers.    * @param reader  Index to use.    * @param fieldname  Field containg integer values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorInt
specifier|static
name|ScoreDocComparator
name|comparatorInt
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getInts
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
operator|new
name|Integer
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|INT
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing floats.    * @param reader  Index to use.    * @param fieldname  Field containg float values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorFloat
specifier|static
name|ScoreDocComparator
name|comparatorFloat
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|float
index|[]
name|fieldOrder
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|float
name|fi
init|=
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|float
name|fj
init|=
name|fieldOrder
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
operator|new
name|Float
argument_list|(
name|fieldOrder
index|[
name|i
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|FLOAT
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing strings.    * @param reader  Index to use.    * @param fieldname  Field containg string values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorString
specifier|static
name|ScoreDocComparator
name|comparatorString
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|StringIndex
name|index
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStringIndex
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
specifier|final
name|int
name|fi
init|=
name|index
operator|.
name|order
index|[
name|i
operator|.
name|doc
index|]
decl_stmt|;
specifier|final
name|int
name|fj
init|=
name|index
operator|.
name|order
index|[
name|j
operator|.
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|fi
operator|<
name|fj
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|fi
operator|>
name|fj
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|index
operator|.
name|lookup
index|[
name|index
operator|.
name|order
index|[
name|i
operator|.
name|doc
index|]
index|]
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|STRING
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to a field containing strings.    * @param reader  Index to use.    * @param fieldname  Field containg string values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorStringLocale
specifier|static
name|ScoreDocComparator
name|comparatorStringLocale
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|Locale
name|locale
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|index
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStrings
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ScoreDocComparator
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|compare
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|,
specifier|final
name|ScoreDoc
name|j
parameter_list|)
block|{
return|return
name|collator
operator|.
name|compare
argument_list|(
name|index
index|[
name|i
operator|.
name|doc
index|]
argument_list|,
name|index
index|[
name|j
operator|.
name|doc
index|]
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|sortValue
parameter_list|(
specifier|final
name|ScoreDoc
name|i
parameter_list|)
block|{
return|return
name|index
index|[
name|i
operator|.
name|doc
index|]
return|;
block|}
specifier|public
name|int
name|sortType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|STRING
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a comparator for sorting hits according to values in the given field.    * The terms in the field are looked at to determine whether they contain integers,    * floats or strings.  Once the type is determined, one of the other static methods    * in this class is called to get the comparator.    * @param reader  Index to use.    * @param fieldname  Field containg values.    * @return  Comparator for sorting hits.    * @throws IOException If an error occurs reading the index.    */
DECL|method|comparatorAuto
specifier|static
name|ScoreDocComparator
name|comparatorAuto
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|field
init|=
name|fieldname
operator|.
name|intern
argument_list|()
decl_stmt|;
name|Object
name|lookupArray
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getAuto
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookupArray
operator|instanceof
name|FieldCache
operator|.
name|StringIndex
condition|)
block|{
return|return
name|comparatorString
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|int
index|[]
condition|)
block|{
return|return
name|comparatorInt
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|float
index|[]
condition|)
block|{
return|return
name|comparatorFloat
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|lookupArray
operator|instanceof
name|String
index|[]
condition|)
block|{
return|return
name|comparatorString
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown data type in field '"
operator|+
name|field
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

