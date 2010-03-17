begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Copyright 2003 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Borrowed from Cglib. Allows custom swap so that two arrays can be sorted  * at the same time.  */
end_comment

begin_class
DECL|class|SorterTemplate
specifier|public
specifier|abstract
class|class
name|SorterTemplate
block|{
DECL|field|MERGESORT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|MERGESORT_THRESHOLD
init|=
literal|12
decl_stmt|;
DECL|field|QUICKSORT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|QUICKSORT_THRESHOLD
init|=
literal|7
decl_stmt|;
DECL|method|swap
specifier|abstract
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
function_decl|;
DECL|method|compare
specifier|abstract
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
function_decl|;
DECL|method|quickSort
specifier|public
name|void
name|quickSort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
name|quickSortHelper
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
name|insertionSort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
DECL|method|quickSortHelper
specifier|private
name|void
name|quickSortHelper
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|diff
init|=
name|hi
operator|-
name|lo
decl_stmt|;
if|if
condition|(
name|diff
operator|<=
name|QUICKSORT_THRESHOLD
condition|)
block|{
break|break;
block|}
name|int
name|i
init|=
operator|(
name|hi
operator|+
name|lo
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|lo
argument_list|,
name|i
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|lo
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compare
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compare
argument_list|(
name|i
argument_list|,
name|hi
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|i
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
name|int
name|j
init|=
name|hi
operator|-
literal|1
decl_stmt|;
name|swap
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|i
operator|=
name|lo
expr_stmt|;
name|int
name|v
init|=
name|j
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
while|while
condition|(
name|compare
argument_list|(
operator|++
name|i
argument_list|,
name|v
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|/* nothing */
empty_stmt|;
block|}
while|while
condition|(
name|compare
argument_list|(
operator|--
name|j
argument_list|,
name|v
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|/* nothing */
empty_stmt|;
block|}
if|if
condition|(
name|j
operator|<
name|i
condition|)
block|{
break|break;
block|}
name|swap
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
name|swap
argument_list|(
name|i
argument_list|,
name|hi
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|-
name|lo
operator|<=
name|hi
operator|-
name|i
operator|+
literal|1
condition|)
block|{
name|quickSortHelper
argument_list|(
name|lo
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|lo
operator|=
name|i
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|quickSortHelper
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|hi
argument_list|)
expr_stmt|;
name|hi
operator|=
name|j
expr_stmt|;
block|}
block|}
block|}
DECL|method|insertionSort
specifier|private
name|void
name|insertionSort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|lo
operator|+
literal|1
init|;
name|i
operator|<=
name|hi
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|>
name|lo
condition|;
name|j
operator|--
control|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|j
operator|-
literal|1
argument_list|,
name|j
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|j
operator|-
literal|1
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
DECL|method|mergeSort
specifier|protected
name|void
name|mergeSort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
name|int
name|diff
init|=
name|hi
operator|-
name|lo
decl_stmt|;
if|if
condition|(
name|diff
operator|<=
name|MERGESORT_THRESHOLD
condition|)
block|{
name|insertionSort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|mid
init|=
name|lo
operator|+
name|diff
operator|/
literal|2
decl_stmt|;
name|mergeSort
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|)
expr_stmt|;
name|mergeSort
argument_list|(
name|mid
argument_list|,
name|hi
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|,
name|hi
argument_list|,
name|mid
operator|-
name|lo
argument_list|,
name|hi
operator|-
name|mid
argument_list|)
expr_stmt|;
block|}
DECL|method|merge
specifier|private
name|void
name|merge
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|pivot
parameter_list|,
name|int
name|hi
parameter_list|,
name|int
name|len1
parameter_list|,
name|int
name|len2
parameter_list|)
block|{
if|if
condition|(
name|len1
operator|==
literal|0
operator|||
name|len2
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|len1
operator|+
name|len2
operator|==
literal|2
condition|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|pivot
argument_list|,
name|lo
argument_list|)
operator|<
literal|0
condition|)
block|{
name|swap
argument_list|(
name|pivot
argument_list|,
name|lo
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|int
name|first_cut
decl_stmt|,
name|second_cut
decl_stmt|;
name|int
name|len11
decl_stmt|,
name|len22
decl_stmt|;
if|if
condition|(
name|len1
operator|>
name|len2
condition|)
block|{
name|len11
operator|=
name|len1
operator|/
literal|2
expr_stmt|;
name|first_cut
operator|=
name|lo
operator|+
name|len11
expr_stmt|;
name|second_cut
operator|=
name|lower
argument_list|(
name|pivot
argument_list|,
name|hi
argument_list|,
name|first_cut
argument_list|)
expr_stmt|;
name|len22
operator|=
name|second_cut
operator|-
name|pivot
expr_stmt|;
block|}
else|else
block|{
name|len22
operator|=
name|len2
operator|/
literal|2
expr_stmt|;
name|second_cut
operator|=
name|pivot
operator|+
name|len22
expr_stmt|;
name|first_cut
operator|=
name|upper
argument_list|(
name|lo
argument_list|,
name|pivot
argument_list|,
name|second_cut
argument_list|)
expr_stmt|;
name|len11
operator|=
name|first_cut
operator|-
name|lo
expr_stmt|;
block|}
name|rotate
argument_list|(
name|first_cut
argument_list|,
name|pivot
argument_list|,
name|second_cut
argument_list|)
expr_stmt|;
name|int
name|new_mid
init|=
name|first_cut
operator|+
name|len22
decl_stmt|;
name|merge
argument_list|(
name|lo
argument_list|,
name|first_cut
argument_list|,
name|new_mid
argument_list|,
name|len11
argument_list|,
name|len22
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|new_mid
argument_list|,
name|second_cut
argument_list|,
name|hi
argument_list|,
name|len1
operator|-
name|len11
argument_list|,
name|len2
operator|-
name|len22
argument_list|)
expr_stmt|;
block|}
DECL|method|rotate
specifier|private
name|void
name|rotate
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|mid
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
name|int
name|lot
init|=
name|lo
decl_stmt|;
name|int
name|hit
init|=
name|mid
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|lot
operator|<
name|hit
condition|)
block|{
name|swap
argument_list|(
name|lot
operator|++
argument_list|,
name|hit
operator|--
argument_list|)
expr_stmt|;
block|}
name|lot
operator|=
name|mid
expr_stmt|;
name|hit
operator|=
name|hi
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|lot
operator|<
name|hit
condition|)
block|{
name|swap
argument_list|(
name|lot
operator|++
argument_list|,
name|hit
operator|--
argument_list|)
expr_stmt|;
block|}
name|lot
operator|=
name|lo
expr_stmt|;
name|hit
operator|=
name|hi
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|lot
operator|<
name|hit
condition|)
block|{
name|swap
argument_list|(
name|lot
operator|++
argument_list|,
name|hit
operator|--
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|lower
specifier|private
name|int
name|lower
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|hi
operator|-
name|lo
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|half
init|=
name|len
operator|/
literal|2
decl_stmt|;
name|int
name|mid
init|=
name|lo
operator|+
name|half
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|mid
argument_list|,
name|val
argument_list|)
operator|<
literal|0
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|len
operator|=
name|len
operator|-
name|half
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|len
operator|=
name|half
expr_stmt|;
block|}
block|}
return|return
name|lo
return|;
block|}
DECL|method|upper
specifier|private
name|int
name|upper
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|hi
operator|-
name|lo
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|half
init|=
name|len
operator|/
literal|2
decl_stmt|;
name|int
name|mid
init|=
name|lo
operator|+
name|half
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|val
argument_list|,
name|mid
argument_list|)
operator|<
literal|0
condition|)
block|{
name|len
operator|=
name|half
expr_stmt|;
block|}
else|else
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|len
operator|=
name|len
operator|-
name|half
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|lo
return|;
block|}
block|}
end_class

end_unit

