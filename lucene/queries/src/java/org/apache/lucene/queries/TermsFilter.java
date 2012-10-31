begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Filter
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
name|ArrayUtil
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
name|Bits
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
name|FixedBitSet
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * Constructs a filter for docs matching any of the terms added to this class.  * Unlike a RangeFilter this can be used for filtering on multiple terms that are not necessarily in  * a sequence. An example might be a collection of primary keys from a database query result or perhaps  * a choice of "category" labels picked by the end user. As a filter, this is much faster than the  * equivalent query (a BooleanQuery with many "should" TermQueries)  */
end_comment

begin_class
DECL|class|TermsFilter
specifier|public
specifier|final
class|class
name|TermsFilter
extends|extends
name|Filter
block|{
comment|/*    * this class is often used for large number of terms in a single field.    * to optimize for this case and to be filter-cache friendly we     * serialize all terms into a single byte array and store offsets    * in a parallel array to keep the # of object constant and speed up    * equals / hashcode.    *     * This adds quite a bit of complexity but allows large term filters to    * be efficient for GC and cache-lookups    */
DECL|field|offsets
specifier|private
specifier|final
name|int
index|[]
name|offsets
decl_stmt|;
DECL|field|termsBytes
specifier|private
specifier|final
name|byte
index|[]
name|termsBytes
decl_stmt|;
DECL|field|termsAndFields
specifier|private
specifier|final
name|TermsAndField
index|[]
name|termsAndFields
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
comment|// cached hashcode for fast cache lookups
DECL|field|PRIME
specifier|private
specifier|static
specifier|final
name|int
name|PRIME
init|=
literal|31
decl_stmt|;
comment|/**    * Creates a new {@link TermsFilter} from the given list. The list    * can contain duplicate terms and multiple fields.    */
DECL|method|TermsFilter
specifier|public
name|TermsFilter
parameter_list|(
specifier|final
name|List
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|FieldAndTermEnum
argument_list|()
block|{
comment|// we need to sort for deduplication and to have a common cache key
specifier|final
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iter
init|=
name|sort
argument_list|(
name|terms
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|field
operator|=
name|next
operator|.
name|field
argument_list|()
expr_stmt|;
return|return
name|next
operator|.
name|bytes
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsFilter} from the given {@link BytesRef} list for    * a single field.    */
DECL|method|TermsFilter
specifier|public
name|TermsFilter
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|terms
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|FieldAndTermEnum
argument_list|(
name|field
argument_list|)
block|{
comment|// we need to sort for deduplication and to have a common cache key
specifier|final
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iter
init|=
name|sort
argument_list|(
name|terms
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|iter
operator|.
name|next
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsFilter} from the given {@link BytesRef} array for    * a single field.    */
DECL|method|TermsFilter
specifier|public
name|TermsFilter
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|BytesRef
modifier|...
name|terms
parameter_list|)
block|{
comment|// this ctor prevents unnecessary Term creations
name|this
argument_list|(
name|field
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link TermsFilter} from the given array. The array can    * contain duplicate terms and multiple fields.    */
DECL|method|TermsFilter
specifier|public
name|TermsFilter
parameter_list|(
specifier|final
name|Term
modifier|...
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TermsFilter
specifier|private
name|TermsFilter
parameter_list|(
name|FieldAndTermEnum
name|iter
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|hash
init|=
literal|9
decl_stmt|;
name|byte
index|[]
name|serializedTerms
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|this
operator|.
name|offsets
operator|=
operator|new
name|int
index|[
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|int
name|lastEndOffset
init|=
literal|0
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
name|ArrayList
argument_list|<
name|TermsAndField
argument_list|>
name|termsAndFields
init|=
operator|new
name|ArrayList
argument_list|<
name|TermsAndField
argument_list|>
argument_list|()
decl_stmt|;
name|TermsAndField
name|lastTermsAndField
init|=
literal|null
decl_stmt|;
name|BytesRef
name|previousTerm
init|=
literal|null
decl_stmt|;
name|String
name|previousField
init|=
literal|null
decl_stmt|;
name|BytesRef
name|currentTerm
decl_stmt|;
name|String
name|currentField
decl_stmt|;
while|while
condition|(
operator|(
name|currentTerm
operator|=
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|currentField
operator|=
name|iter
operator|.
name|field
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentField
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|previousField
operator|!=
literal|null
condition|)
block|{
comment|// deduplicate
if|if
condition|(
name|previousField
operator|.
name|equals
argument_list|(
name|currentField
argument_list|)
condition|)
block|{
if|if
condition|(
name|previousTerm
operator|.
name|bytesEquals
argument_list|(
name|currentTerm
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|start
init|=
name|lastTermsAndField
operator|==
literal|null
condition|?
literal|0
else|:
name|lastTermsAndField
operator|.
name|end
decl_stmt|;
name|lastTermsAndField
operator|=
operator|new
name|TermsAndField
argument_list|(
name|start
argument_list|,
name|index
argument_list|,
name|previousField
argument_list|)
expr_stmt|;
name|termsAndFields
operator|.
name|add
argument_list|(
name|lastTermsAndField
argument_list|)
expr_stmt|;
block|}
block|}
name|hash
operator|=
name|PRIME
operator|*
name|hash
operator|+
name|currentField
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|PRIME
operator|*
name|hash
operator|+
name|currentTerm
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|serializedTerms
operator|.
name|length
operator|<
name|lastEndOffset
operator|+
name|currentTerm
operator|.
name|length
condition|)
block|{
name|serializedTerms
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|serializedTerms
argument_list|,
name|lastEndOffset
operator|+
name|currentTerm
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|currentTerm
operator|.
name|bytes
argument_list|,
name|currentTerm
operator|.
name|offset
argument_list|,
name|serializedTerms
argument_list|,
name|lastEndOffset
argument_list|,
name|currentTerm
operator|.
name|length
argument_list|)
expr_stmt|;
name|offsets
index|[
name|index
index|]
operator|=
name|lastEndOffset
expr_stmt|;
name|lastEndOffset
operator|+=
name|currentTerm
operator|.
name|length
expr_stmt|;
name|index
operator|++
expr_stmt|;
name|previousTerm
operator|=
name|currentTerm
expr_stmt|;
name|previousField
operator|=
name|currentField
expr_stmt|;
block|}
name|offsets
index|[
name|index
index|]
operator|=
name|lastEndOffset
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|lastTermsAndField
operator|==
literal|null
condition|?
literal|0
else|:
name|lastTermsAndField
operator|.
name|end
decl_stmt|;
name|lastTermsAndField
operator|=
operator|new
name|TermsAndField
argument_list|(
name|start
argument_list|,
name|index
argument_list|,
name|previousField
argument_list|)
expr_stmt|;
name|termsAndFields
operator|.
name|add
argument_list|(
name|lastTermsAndField
argument_list|)
expr_stmt|;
name|this
operator|.
name|termsBytes
operator|=
name|ArrayUtil
operator|.
name|shrink
argument_list|(
name|serializedTerms
argument_list|,
name|lastEndOffset
argument_list|)
expr_stmt|;
name|this
operator|.
name|termsAndFields
operator|=
name|termsAndFields
operator|.
name|toArray
argument_list|(
operator|new
name|TermsAndField
index|[
name|termsAndFields
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|hash
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|FixedBitSet
name|result
init|=
literal|null
decl_stmt|;
comment|// lazy init if needed - no need to create a big bitset ahead of time
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|(
name|this
operator|.
name|termsBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|Terms
name|terms
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
for|for
control|(
name|TermsAndField
name|termsAndField
range|:
name|this
operator|.
name|termsAndFields
control|)
block|{
if|if
condition|(
operator|(
name|terms
operator|=
name|fields
operator|.
name|terms
argument_list|(
name|termsAndField
operator|.
name|field
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
comment|// this won't return null
for|for
control|(
name|int
name|i
init|=
name|termsAndField
operator|.
name|start
init|;
name|i
operator|<
name|termsAndField
operator|.
name|end
condition|;
name|i
operator|++
control|)
block|{
name|spare
operator|.
name|offset
operator|=
name|offsets
index|[
name|i
index|]
expr_stmt|;
name|spare
operator|.
name|length
operator|=
name|offsets
index|[
name|i
operator|+
literal|1
index|]
operator|-
name|offsets
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|spare
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// don't use cache since we could pollute the cache here easily
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// no freq since we don't need them
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|docs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|result
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// lazy init but don't do it in the hot loop since we could read many docs
name|result
operator|.
name|set
argument_list|(
name|docs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
name|docs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|result
operator|.
name|set
argument_list|(
name|docs
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/*      * TODO: we should explore if it is worth to build the union of the terms in      * an automaton an call intersect on the termsenum if the density is high      */
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermsFilter
name|test
init|=
operator|(
name|TermsFilter
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|hashCode
operator|==
name|hashCode
operator|&&
name|this
operator|.
name|termsAndFields
operator|.
name|length
operator|==
name|test
operator|.
name|termsAndFields
operator|.
name|length
condition|)
block|{
comment|// first check the fields before even comparing the bytes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termsAndFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermsAndField
name|current
init|=
name|termsAndFields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|current
operator|.
name|equals
argument_list|(
name|test
operator|.
name|termsAndFields
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// straight byte comparison since we sort they must be identical
name|int
name|end
init|=
name|offsets
index|[
name|termsAndFields
operator|.
name|length
index|]
decl_stmt|;
name|byte
index|[]
name|left
init|=
name|this
operator|.
name|termsBytes
decl_stmt|;
name|byte
index|[]
name|right
init|=
name|test
operator|.
name|termsBytes
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
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|left
index|[
name|i
index|]
operator|!=
name|right
index|[
name|i
index|]
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
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|(
name|termsBytes
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
name|termsAndFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermsAndField
name|current
init|=
name|termsAndFields
index|[
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|current
operator|.
name|start
init|;
name|j
operator|<
name|current
operator|.
name|end
condition|;
name|j
operator|++
control|)
block|{
name|spare
operator|.
name|offset
operator|=
name|offsets
index|[
name|j
index|]
expr_stmt|;
name|spare
operator|.
name|length
operator|=
name|offsets
index|[
name|j
operator|+
literal|1
index|]
operator|-
name|offsets
index|[
name|j
index|]
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|current
operator|.
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|spare
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|TermsAndField
specifier|private
specifier|static
specifier|final
class|class
name|TermsAndField
block|{
DECL|field|start
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|final
name|int
name|end
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|TermsAndField
name|TermsAndField
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|field
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|field
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|end
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|start
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TermsAndField
name|other
init|=
operator|(
name|TermsAndField
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|field
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|end
operator|!=
name|other
operator|.
name|end
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|start
operator|!=
name|other
operator|.
name|start
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
DECL|class|FieldAndTermEnum
specifier|private
specifier|static
specifier|abstract
class|class
name|FieldAndTermEnum
block|{
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|method|next
specifier|public
specifier|abstract
name|BytesRef
name|next
parameter_list|()
function_decl|;
DECL|method|FieldAndTermEnum
specifier|public
name|FieldAndTermEnum
parameter_list|()
block|{}
DECL|method|FieldAndTermEnum
specifier|public
name|FieldAndTermEnum
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
block|}
comment|/*    * simple utility that returns the in-place sorted list    */
DECL|method|sort
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|T
argument_list|>
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|sort
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|toSort
parameter_list|)
block|{
if|if
condition|(
name|toSort
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no terms provided"
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|toSort
argument_list|)
expr_stmt|;
return|return
name|toSort
return|;
block|}
block|}
end_class

end_unit

