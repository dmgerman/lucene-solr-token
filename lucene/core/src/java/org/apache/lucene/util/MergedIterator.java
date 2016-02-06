begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

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
name|NoSuchElementException
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

begin_comment
comment|/**  * Provides a merged sorted view from several sorted iterators.  *<p>  * If built with<code>removeDuplicates</code> set to true and an element  * appears in multiple iterators then it is deduplicated, that is this iterator  * returns the sorted union of elements.  *<p>  * If built with<code>removeDuplicates</code> set to false then all elements  * in all iterators are returned.  *<p>  * Caveats:  *<ul>  *<li>The behavior is undefined if the iterators are not actually sorted.  *<li>Null elements are unsupported.  *<li>If removeDuplicates is set to true and if a single iterator contains  *       duplicates then they will not be deduplicated.  *<li>When elements are deduplicated it is not defined which one is returned.  *<li>If removeDuplicates is set to false then the order in which duplicates  *       are returned isn't defined.  *</ul>  * @lucene.internal  */
end_comment

begin_class
DECL|class|MergedIterator
specifier|public
specifier|final
class|class
name|MergedIterator
parameter_list|<
name|T
extends|extends
name|Comparable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
DECL|field|current
specifier|private
name|T
name|current
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|TermMergeQueue
argument_list|<
name|T
argument_list|>
name|queue
decl_stmt|;
DECL|field|top
specifier|private
specifier|final
name|SubIterator
argument_list|<
name|T
argument_list|>
index|[]
name|top
decl_stmt|;
DECL|field|removeDuplicates
specifier|private
specifier|final
name|boolean
name|removeDuplicates
decl_stmt|;
DECL|field|numTop
specifier|private
name|int
name|numTop
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|MergedIterator
specifier|public
name|MergedIterator
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
modifier|...
name|iterators
parameter_list|)
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|iterators
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|MergedIterator
specifier|public
name|MergedIterator
parameter_list|(
name|boolean
name|removeDuplicates
parameter_list|,
name|Iterator
argument_list|<
name|T
argument_list|>
modifier|...
name|iterators
parameter_list|)
block|{
name|this
operator|.
name|removeDuplicates
operator|=
name|removeDuplicates
expr_stmt|;
name|queue
operator|=
operator|new
name|TermMergeQueue
argument_list|<>
argument_list|(
name|iterators
operator|.
name|length
argument_list|)
expr_stmt|;
name|top
operator|=
operator|new
name|SubIterator
index|[
name|iterators
operator|.
name|length
index|]
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
range|:
name|iterators
control|)
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SubIterator
argument_list|<
name|T
argument_list|>
name|sub
init|=
operator|new
name|SubIterator
argument_list|<>
argument_list|()
decl_stmt|;
name|sub
operator|.
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|sub
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
name|sub
operator|.
name|index
operator|=
name|index
operator|++
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
literal|true
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
name|numTop
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|top
index|[
name|i
index|]
operator|.
name|iterator
operator|.
name|hasNext
argument_list|()
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
annotation|@
name|Override
DECL|method|next
specifier|public
name|T
name|next
parameter_list|()
block|{
comment|// restore queue
name|pushTop
argument_list|()
expr_stmt|;
comment|// gather equal top elements
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pullTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|pullTop
specifier|private
name|void
name|pullTop
parameter_list|()
block|{
assert|assert
name|numTop
operator|==
literal|0
assert|;
name|top
index|[
name|numTop
operator|++
index|]
operator|=
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|removeDuplicates
condition|)
block|{
comment|// extract all subs from the queue that have the same top element
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
operator|&&
name|queue
operator|.
name|top
argument_list|()
operator|.
name|current
operator|.
name|equals
argument_list|(
name|top
index|[
literal|0
index|]
operator|.
name|current
argument_list|)
condition|)
block|{
name|top
index|[
name|numTop
operator|++
index|]
operator|=
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
name|current
operator|=
name|top
index|[
literal|0
index|]
operator|.
name|current
expr_stmt|;
block|}
DECL|method|pushTop
specifier|private
name|void
name|pushTop
parameter_list|()
block|{
comment|// call next() on each top, and put back into queue
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTop
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|top
index|[
name|i
index|]
operator|.
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|top
index|[
name|i
index|]
operator|.
name|current
operator|=
name|top
index|[
name|i
index|]
operator|.
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|top
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no more elements
name|top
index|[
name|i
index|]
operator|.
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|numTop
operator|=
literal|0
expr_stmt|;
block|}
DECL|class|SubIterator
specifier|private
specifier|static
class|class
name|SubIterator
parameter_list|<
name|I
extends|extends
name|Comparable
parameter_list|<
name|I
parameter_list|>
parameter_list|>
block|{
DECL|field|iterator
name|Iterator
argument_list|<
name|I
argument_list|>
name|iterator
decl_stmt|;
DECL|field|current
name|I
name|current
decl_stmt|;
DECL|field|index
name|int
name|index
decl_stmt|;
block|}
DECL|class|TermMergeQueue
specifier|private
specifier|static
class|class
name|TermMergeQueue
parameter_list|<
name|C
extends|extends
name|Comparable
parameter_list|<
name|C
parameter_list|>
parameter_list|>
extends|extends
name|PriorityQueue
argument_list|<
name|SubIterator
argument_list|<
name|C
argument_list|>
argument_list|>
block|{
DECL|method|TermMergeQueue
name|TermMergeQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SubIterator
argument_list|<
name|C
argument_list|>
name|a
parameter_list|,
name|SubIterator
argument_list|<
name|C
argument_list|>
name|b
parameter_list|)
block|{
specifier|final
name|int
name|cmp
init|=
name|a
operator|.
name|current
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
operator|<
literal|0
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|index
operator|<
name|b
operator|.
name|index
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

