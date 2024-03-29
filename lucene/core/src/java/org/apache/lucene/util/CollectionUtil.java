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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|RandomAccess
import|;
end_import

begin_comment
comment|/**  * Methods for manipulating (sorting) collections.  * Sort methods work directly on the supplied lists and don't copy to/from arrays  * before/after. For medium size collections as used in the Lucene indexer that is  * much more efficient.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|CollectionUtil
specifier|public
specifier|final
class|class
name|CollectionUtil
block|{
DECL|method|CollectionUtil
specifier|private
name|CollectionUtil
parameter_list|()
block|{}
comment|// no instance
DECL|class|ListIntroSorter
specifier|private
specifier|static
specifier|final
class|class
name|ListIntroSorter
parameter_list|<
name|T
parameter_list|>
extends|extends
name|IntroSorter
block|{
DECL|field|pivot
name|T
name|pivot
decl_stmt|;
DECL|field|list
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
decl_stmt|;
DECL|field|comp
specifier|final
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
decl_stmt|;
DECL|method|ListIntroSorter
name|ListIntroSorter
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|list
operator|instanceof
name|RandomAccess
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"CollectionUtil can only sort random access lists in-place."
argument_list|)
throw|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPivot
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|pivot
operator|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|swap
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|Collections
operator|.
name|swap
argument_list|(
name|list
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|comp
operator|.
name|compare
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|comparePivot
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
return|return
name|comp
operator|.
name|compare
argument_list|(
name|pivot
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|ListTimSorter
specifier|private
specifier|static
specifier|final
class|class
name|ListTimSorter
parameter_list|<
name|T
parameter_list|>
extends|extends
name|TimSorter
block|{
DECL|field|list
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
decl_stmt|;
DECL|field|comp
specifier|final
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
decl_stmt|;
DECL|field|tmp
specifier|final
name|T
index|[]
name|tmp
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ListTimSorter
name|ListTimSorter
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|,
name|int
name|maxTempSlots
parameter_list|)
block|{
name|super
argument_list|(
name|maxTempSlots
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|list
operator|instanceof
name|RandomAccess
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"CollectionUtil can only sort random access lists in-place."
argument_list|)
throw|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
if|if
condition|(
name|maxTempSlots
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|tmp
operator|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|maxTempSlots
index|]
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|tmp
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|swap
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|Collections
operator|.
name|swap
argument_list|(
name|list
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|protected
name|void
name|copy
parameter_list|(
name|int
name|src
parameter_list|,
name|int
name|dest
parameter_list|)
block|{
name|list
operator|.
name|set
argument_list|(
name|dest
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|src
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|save
specifier|protected
name|void
name|save
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
operator|++
name|j
control|)
block|{
name|tmp
index|[
name|j
index|]
operator|=
name|list
operator|.
name|get
argument_list|(
name|i
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|restore
specifier|protected
name|void
name|restore
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|list
operator|.
name|set
argument_list|(
name|j
argument_list|,
name|tmp
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|comp
operator|.
name|compare
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareSaved
specifier|protected
name|int
name|compareSaved
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|comp
operator|.
name|compare
argument_list|(
name|tmp
index|[
name|i
index|]
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Sorts the given random access {@link List} using the {@link Comparator}.    * The list must implement {@link RandomAccess}. This method uses the intro sort    * algorithm, but falls back to insertion sort for small lists.    * @throws IllegalArgumentException if list is e.g. a linked list without random access.    */
DECL|method|introSort
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|introSort
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|1
condition|)
return|return;
operator|new
name|ListIntroSorter
argument_list|<>
argument_list|(
name|list
argument_list|,
name|comp
argument_list|)
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given random access {@link List} in natural order.    * The list must implement {@link RandomAccess}. This method uses the intro sort    * algorithm, but falls back to insertion sort for small lists.    * @throws IllegalArgumentException if list is e.g. a linked list without random access.    */
DECL|method|introSort
specifier|public
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
name|void
name|introSort
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|1
condition|)
return|return;
name|introSort
argument_list|(
name|list
argument_list|,
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Tim sorts:
comment|/**    * Sorts the given random access {@link List} using the {@link Comparator}.    * The list must implement {@link RandomAccess}. This method uses the Tim sort    * algorithm, but falls back to binary sort for small lists.    * @throws IllegalArgumentException if list is e.g. a linked list without random access.    */
DECL|method|timSort
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|timSort
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|1
condition|)
return|return;
operator|new
name|ListTimSorter
argument_list|<>
argument_list|(
name|list
argument_list|,
name|comp
argument_list|,
name|list
operator|.
name|size
argument_list|()
operator|/
literal|64
argument_list|)
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given random access {@link List} in natural order.    * The list must implement {@link RandomAccess}. This method uses the Tim sort    * algorithm, but falls back to binary sort for small lists.    * @throws IllegalArgumentException if list is e.g. a linked list without random access.    */
DECL|method|timSort
specifier|public
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
name|void
name|timSort
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<=
literal|1
condition|)
return|return;
name|timSort
argument_list|(
name|list
argument_list|,
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

