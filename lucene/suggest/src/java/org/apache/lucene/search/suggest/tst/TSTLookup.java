begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.tst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|tst
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
name|List
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
name|search
operator|.
name|suggest
operator|.
name|InputIterator
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
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|SortedInputIterator
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
name|DataInput
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
name|DataOutput
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRef
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
name|RamUsageEstimator
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * Suggest implementation based on a   *<a href="http://en.wikipedia.org/wiki/Ternary_search_tree">Ternary Search Tree</a>  *   * @see TSTAutocomplete  */
end_comment

begin_class
DECL|class|TSTLookup
specifier|public
class|class
name|TSTLookup
extends|extends
name|Lookup
block|{
DECL|field|root
name|TernaryTreeNode
name|root
init|=
operator|new
name|TernaryTreeNode
argument_list|()
decl_stmt|;
DECL|field|autocomplete
name|TSTAutocomplete
name|autocomplete
init|=
operator|new
name|TSTAutocomplete
argument_list|()
decl_stmt|;
comment|/** Number of entries the lookup was built with */
DECL|field|count
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
comment|/**     * Creates a new TSTLookup with an empty Ternary Search Tree.    * @see #build(InputIterator)    */
DECL|method|TSTLookup
specifier|public
name|TSTLookup
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|InputIterator
name|iterator
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iterator
operator|.
name|hasPayloads
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this suggester doesn't support payloads"
argument_list|)
throw|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasContexts
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this suggester doesn't support contexts"
argument_list|)
throw|;
block|}
name|root
operator|=
operator|new
name|TernaryTreeNode
argument_list|()
expr_stmt|;
comment|// make sure it's sorted and the comparator uses UTF16 sort order
name|iterator
operator|=
operator|new
name|SortedInputIterator
argument_list|(
name|iterator
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Number
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BytesRef
name|spare
decl_stmt|;
name|CharsRef
name|charsSpare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|charsSpare
operator|.
name|grow
argument_list|(
name|spare
operator|.
name|length
argument_list|)
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|spare
operator|.
name|bytes
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
name|spare
operator|.
name|length
argument_list|,
name|charsSpare
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|charsSpare
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|iterator
operator|.
name|weight
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|autocomplete
operator|.
name|balancedTree
argument_list|(
name|tokens
operator|.
name|toArray
argument_list|()
argument_list|,
name|vals
operator|.
name|toArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
comment|/**     * Adds a new node if<code>key</code> already exists,    * otherwise replaces its value.    *<p>    * This method always returns true.    */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|autocomplete
operator|.
name|insert
argument_list|(
name|root
argument_list|,
name|key
argument_list|,
name|value
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// XXX we don't know if a new node was created
return|return
literal|true
return|;
block|}
comment|/**    * Returns the value for the specified key, or null    * if the key does not exist.    */
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|TernaryTreeNode
argument_list|>
name|list
init|=
name|autocomplete
operator|.
name|prefixCompletion
argument_list|(
name|root
argument_list|,
name|key
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
operator|||
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|TernaryTreeNode
name|n
range|:
name|list
control|)
block|{
if|if
condition|(
name|charSeqEquals
argument_list|(
name|n
operator|.
name|token
argument_list|,
name|key
argument_list|)
condition|)
block|{
return|return
name|n
operator|.
name|val
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|charSeqEquals
specifier|private
specifier|static
name|boolean
name|charSeqEquals
parameter_list|(
name|CharSequence
name|left
parameter_list|,
name|CharSequence
name|right
parameter_list|)
block|{
name|int
name|len
init|=
name|left
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|right
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|left
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
name|right
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|contexts
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this suggester doesn't support contexts"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|TernaryTreeNode
argument_list|>
name|list
init|=
name|autocomplete
operator|.
name|prefixCompletion
argument_list|(
name|root
argument_list|,
name|key
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
operator|||
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|res
return|;
block|}
name|int
name|maxCnt
init|=
name|Math
operator|.
name|min
argument_list|(
name|num
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|onlyMorePopular
condition|)
block|{
name|LookupPriorityQueue
name|queue
init|=
operator|new
name|LookupPriorityQueue
argument_list|(
name|num
argument_list|)
decl_stmt|;
for|for
control|(
name|TernaryTreeNode
name|ttn
range|:
name|list
control|)
block|{
name|queue
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|ttn
operator|.
name|token
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|ttn
operator|.
name|val
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LookupResult
name|lr
range|:
name|queue
operator|.
name|getResults
argument_list|()
control|)
block|{
name|res
operator|.
name|add
argument_list|(
name|lr
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxCnt
condition|;
name|i
operator|++
control|)
block|{
name|TernaryTreeNode
name|ttn
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|ttn
operator|.
name|token
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|ttn
operator|.
name|val
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
DECL|field|LO_KID
specifier|private
specifier|static
specifier|final
name|byte
name|LO_KID
init|=
literal|0x01
decl_stmt|;
DECL|field|EQ_KID
specifier|private
specifier|static
specifier|final
name|byte
name|EQ_KID
init|=
literal|0x02
decl_stmt|;
DECL|field|HI_KID
specifier|private
specifier|static
specifier|final
name|byte
name|HI_KID
init|=
literal|0x04
decl_stmt|;
DECL|field|HAS_TOKEN
specifier|private
specifier|static
specifier|final
name|byte
name|HAS_TOKEN
init|=
literal|0x08
decl_stmt|;
DECL|field|HAS_VALUE
specifier|private
specifier|static
specifier|final
name|byte
name|HAS_VALUE
init|=
literal|0x10
decl_stmt|;
comment|// pre-order traversal
DECL|method|readRecursively
specifier|private
name|void
name|readRecursively
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|TernaryTreeNode
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|node
operator|.
name|splitchar
operator|=
name|in
operator|.
name|readString
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|byte
name|mask
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|mask
operator|&
name|HAS_TOKEN
operator|)
operator|!=
literal|0
condition|)
block|{
name|node
operator|.
name|token
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|HAS_VALUE
operator|)
operator|!=
literal|0
condition|)
block|{
name|node
operator|.
name|val
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|LO_KID
operator|)
operator|!=
literal|0
condition|)
block|{
name|node
operator|.
name|loKid
operator|=
operator|new
name|TernaryTreeNode
argument_list|()
expr_stmt|;
name|readRecursively
argument_list|(
name|in
argument_list|,
name|node
operator|.
name|loKid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|EQ_KID
operator|)
operator|!=
literal|0
condition|)
block|{
name|node
operator|.
name|eqKid
operator|=
operator|new
name|TernaryTreeNode
argument_list|()
expr_stmt|;
name|readRecursively
argument_list|(
name|in
argument_list|,
name|node
operator|.
name|eqKid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mask
operator|&
name|HI_KID
operator|)
operator|!=
literal|0
condition|)
block|{
name|node
operator|.
name|hiKid
operator|=
operator|new
name|TernaryTreeNode
argument_list|()
expr_stmt|;
name|readRecursively
argument_list|(
name|in
argument_list|,
name|node
operator|.
name|hiKid
argument_list|)
expr_stmt|;
block|}
block|}
comment|// pre-order traversal
DECL|method|writeRecursively
specifier|private
name|void
name|writeRecursively
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|TernaryTreeNode
name|node
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write out the current node
name|out
operator|.
name|writeString
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
name|node
operator|.
name|splitchar
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// prepare a mask of kids
name|byte
name|mask
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|eqKid
operator|!=
literal|null
condition|)
name|mask
operator||=
name|EQ_KID
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|loKid
operator|!=
literal|null
condition|)
name|mask
operator||=
name|LO_KID
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|hiKid
operator|!=
literal|null
condition|)
name|mask
operator||=
name|HI_KID
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|token
operator|!=
literal|null
condition|)
name|mask
operator||=
name|HAS_TOKEN
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|val
operator|!=
literal|null
condition|)
name|mask
operator||=
name|HAS_VALUE
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|mask
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|token
operator|!=
literal|null
condition|)
name|out
operator|.
name|writeString
argument_list|(
name|node
operator|.
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|val
operator|!=
literal|null
condition|)
name|out
operator|.
name|writeLong
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|node
operator|.
name|val
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// recurse and write kids
if|if
condition|(
name|node
operator|.
name|loKid
operator|!=
literal|null
condition|)
block|{
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|loKid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|eqKid
operator|!=
literal|null
condition|)
block|{
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|eqKid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|hiKid
operator|!=
literal|null
condition|)
block|{
name|writeRecursively
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|hiKid
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|store
specifier|public
specifier|synchronized
name|boolean
name|store
parameter_list|(
name|DataOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|writeRecursively
argument_list|(
name|output
argument_list|,
name|root
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
specifier|synchronized
name|boolean
name|load
parameter_list|(
name|DataInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|count
operator|=
name|input
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|root
operator|=
operator|new
name|TernaryTreeNode
argument_list|()
expr_stmt|;
name|readRecursively
argument_list|(
name|input
argument_list|,
name|root
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Returns byte size of the underlying TST */
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
name|long
name|mem
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|mem
operator|+=
name|root
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|mem
return|;
block|}
annotation|@
name|Override
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

