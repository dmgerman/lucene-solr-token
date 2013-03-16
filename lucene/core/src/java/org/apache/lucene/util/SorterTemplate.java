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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This class was inspired by CGLIB, but provides a better  * QuickSort algorithm without additional InsertionSort  * at the end.  * To use, subclass and override the four abstract methods  * which compare and modify your data.  * Allows custom swap so that two arrays can be sorted  * at the same time.  * @lucene.internal  */
end_comment

begin_class
DECL|class|SorterTemplate
specifier|public
specifier|abstract
class|class
name|SorterTemplate
block|{
DECL|field|TIMSORT_MINRUN
specifier|private
specifier|static
specifier|final
name|int
name|TIMSORT_MINRUN
init|=
literal|32
decl_stmt|;
DECL|field|TIMSORT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|TIMSORT_THRESHOLD
init|=
literal|64
decl_stmt|;
DECL|field|TIMSORT_STACKSIZE
specifier|private
specifier|static
specifier|final
name|int
name|TIMSORT_STACKSIZE
init|=
literal|40
decl_stmt|;
comment|// change if you change TIMSORT_MINRUN
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
static|static
block|{
comment|// check whether TIMSORT_STACKSIZE is large enough
comment|// for a run length of TIMSORT_MINRUN and an array
comment|// of 2B values when TimSort invariants are verified
specifier|final
name|long
index|[]
name|lengths
init|=
operator|new
name|long
index|[
name|TIMSORT_STACKSIZE
index|]
decl_stmt|;
name|lengths
index|[
literal|0
index|]
operator|=
name|TIMSORT_MINRUN
expr_stmt|;
name|lengths
index|[
literal|1
index|]
operator|=
name|lengths
index|[
literal|0
index|]
operator|+
literal|1
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|TIMSORT_STACKSIZE
condition|;
operator|++
name|i
control|)
block|{
name|lengths
index|[
name|i
index|]
operator|=
name|lengths
index|[
name|i
operator|-
literal|2
index|]
operator|+
name|lengths
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|lengths
index|[
name|TIMSORT_STACKSIZE
operator|-
literal|1
index|]
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
literal|"TIMSORT_STACKSIZE is too small"
argument_list|)
throw|;
block|}
block|}
comment|/** Implement this method, that swaps slots {@code i} and {@code j} in your data */
DECL|method|swap
specifier|protected
specifier|abstract
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
comment|/** Compares slots {@code i} and {@code j} of you data.    * Should be implemented like<code><em>valueOf(i)</em>.compareTo(<em>valueOf(j)</em>)</code> */
DECL|method|compare
specifier|protected
specifier|abstract
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
comment|/** Implement this method, that stores the value of slot {@code i} as pivot value */
DECL|method|setPivot
specifier|protected
specifier|abstract
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/** Implements the compare function for the previously stored pivot value.    * Should be implemented like<code>pivot.compareTo(<em>valueOf(j)</em>)</code> */
DECL|method|comparePivot
specifier|protected
specifier|abstract
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
function_decl|;
comment|/** Sorts via stable in-place InsertionSort algorithm (O(n<sup>2</sup>))    *(ideal for small collections which are mostly presorted). */
DECL|method|insertionSort
specifier|public
specifier|final
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
comment|/** Sorts via stable in-place BinarySort algorithm (O(n<sup>2</sup>))    * (ideal for small collections which are in random order). */
DECL|method|binarySort
specifier|public
specifier|final
name|void
name|binarySort
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
operator|++
name|i
control|)
block|{
name|int
name|l
init|=
name|lo
decl_stmt|;
name|int
name|h
init|=
name|i
operator|-
literal|1
decl_stmt|;
name|setPivot
argument_list|(
name|i
argument_list|)
expr_stmt|;
while|while
condition|(
name|l
operator|<=
name|h
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|l
operator|+
name|h
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|cmp
init|=
name|comparePivot
argument_list|(
name|mid
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|h
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|l
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|>
name|l
condition|;
operator|--
name|j
control|)
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
block|}
block|}
comment|/** Sorts via in-place, but unstable, QuickSort algorithm.    * For small collections falls back to {@link #insertionSort(int,int)}. */
DECL|method|quickSort
specifier|public
specifier|final
name|void
name|quickSort
parameter_list|(
specifier|final
name|int
name|lo
parameter_list|,
specifier|final
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|hi
operator|<=
name|lo
condition|)
return|return;
comment|// from Integer's Javadocs: ceil(log2(x)) = 32 - numberOfLeadingZeros(x - 1)
name|quickSort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|,
operator|(
name|Integer
operator|.
name|SIZE
operator|-
name|Integer
operator|.
name|numberOfLeadingZeros
argument_list|(
name|hi
operator|-
name|lo
argument_list|)
operator|)
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|quickSort
specifier|private
name|void
name|quickSort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
comment|// fall back to insertion when array has short length
specifier|final
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
name|insertionSort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// fall back to merge sort when recursion depth gets too big
if|if
condition|(
operator|--
name|maxDepth
operator|==
literal|0
condition|)
block|{
name|mergeSort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|mid
init|=
name|lo
operator|+
operator|(
name|diff
operator|>>>
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compare
argument_list|(
name|mid
argument_list|,
name|hi
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|mid
argument_list|,
name|hi
argument_list|)
expr_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|left
init|=
name|lo
operator|+
literal|1
decl_stmt|;
name|int
name|right
init|=
name|hi
operator|-
literal|1
decl_stmt|;
name|setPivot
argument_list|(
name|mid
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
while|while
condition|(
name|comparePivot
argument_list|(
name|right
argument_list|)
operator|<
literal|0
condition|)
operator|--
name|right
expr_stmt|;
while|while
condition|(
name|left
operator|<
name|right
operator|&&
name|comparePivot
argument_list|(
name|left
argument_list|)
operator|>=
literal|0
condition|)
operator|++
name|left
expr_stmt|;
if|if
condition|(
name|left
operator|<
name|right
condition|)
block|{
name|swap
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
operator|--
name|right
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|quickSort
argument_list|(
name|lo
argument_list|,
name|left
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
name|quickSort
argument_list|(
name|left
operator|+
literal|1
argument_list|,
name|hi
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
block|}
comment|/** TimSort implementation. The only difference with the spec is that this    *  impl reuses {@link SorterTemplate#merge(int, int, int, int, int)} to    *  merge runs (in place) instead of the original merging routine from    *  TimSort (which requires extra memory but might be slightly faster). */
DECL|class|TimSort
specifier|private
class|class
name|TimSort
block|{
DECL|field|hi
specifier|final
name|int
name|hi
decl_stmt|;
DECL|field|minRun
specifier|final
name|int
name|minRun
decl_stmt|;
DECL|field|runEnds
specifier|final
name|int
index|[]
name|runEnds
decl_stmt|;
DECL|field|stackSize
name|int
name|stackSize
decl_stmt|;
DECL|method|TimSort
name|TimSort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
assert|assert
name|hi
operator|>
name|lo
assert|;
comment|// +1 because the first slot is reserved and always lo
name|runEnds
operator|=
operator|new
name|int
index|[
name|TIMSORT_STACKSIZE
operator|+
literal|1
index|]
expr_stmt|;
name|runEnds
index|[
literal|0
index|]
operator|=
name|lo
expr_stmt|;
name|stackSize
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|hi
operator|=
name|hi
expr_stmt|;
name|minRun
operator|=
name|minRun
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Minimum run length for an array of length<code>length</code>. */
DECL|method|minRun
name|int
name|minRun
parameter_list|(
name|int
name|length
parameter_list|)
block|{
assert|assert
name|length
operator|>=
name|TIMSORT_MINRUN
assert|;
name|int
name|n
init|=
name|length
decl_stmt|;
name|int
name|r
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|n
operator|>=
literal|64
condition|)
block|{
name|r
operator||=
name|n
operator|&
literal|1
expr_stmt|;
name|n
operator|>>>=
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|minRun
init|=
name|n
operator|+
name|r
decl_stmt|;
assert|assert
name|minRun
operator|>=
name|TIMSORT_MINRUN
operator|&&
name|minRun
operator|<=
literal|64
assert|;
return|return
name|minRun
return|;
block|}
DECL|method|runLen
name|int
name|runLen
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|int
name|off
init|=
name|stackSize
operator|-
name|i
decl_stmt|;
return|return
name|runEnds
index|[
name|off
index|]
operator|-
name|runEnds
index|[
name|off
operator|-
literal|1
index|]
return|;
block|}
DECL|method|runBase
name|int
name|runBase
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|runEnds
index|[
name|stackSize
operator|-
name|i
operator|-
literal|1
index|]
return|;
block|}
DECL|method|runEnd
name|int
name|runEnd
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|runEnds
index|[
name|stackSize
operator|-
name|i
index|]
return|;
block|}
DECL|method|setRunEnd
name|void
name|setRunEnd
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|runEnd
parameter_list|)
block|{
name|runEnds
index|[
name|stackSize
operator|-
name|i
index|]
operator|=
name|runEnd
expr_stmt|;
block|}
DECL|method|pushRunLen
name|void
name|pushRunLen
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|runEnds
index|[
name|stackSize
operator|+
literal|1
index|]
operator|=
name|runEnds
index|[
name|stackSize
index|]
operator|+
name|len
expr_stmt|;
operator|++
name|stackSize
expr_stmt|;
block|}
comment|/** Merge run i with run i+1 */
DECL|method|mergeAt
name|void
name|mergeAt
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|stackSize
operator|>
name|i
operator|+
literal|1
assert|;
specifier|final
name|int
name|l
init|=
name|runBase
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|pivot
init|=
name|runBase
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|runEnd
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|l
argument_list|,
name|pivot
argument_list|,
name|h
argument_list|,
name|pivot
operator|-
name|l
argument_list|,
name|h
operator|-
name|pivot
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|i
operator|+
literal|1
condition|;
operator|++
name|j
control|)
block|{
name|setRunEnd
argument_list|(
name|j
argument_list|,
name|runEnd
argument_list|(
name|j
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|--
name|stackSize
expr_stmt|;
block|}
comment|/** Compute the length of the next run, make the run sorted and return its      *  length. */
DECL|method|nextRun
name|int
name|nextRun
parameter_list|()
block|{
specifier|final
name|int
name|runBase
init|=
name|runEnd
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|runBase
operator|==
name|hi
condition|)
block|{
return|return
literal|1
return|;
block|}
name|int
name|l
init|=
literal|1
decl_stmt|;
comment|// length of the run
if|if
condition|(
name|compare
argument_list|(
name|runBase
argument_list|,
name|runBase
operator|+
literal|1
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// run must be strictly descending
while|while
condition|(
name|runBase
operator|+
name|l
operator|<=
name|hi
operator|&&
name|compare
argument_list|(
name|runBase
operator|+
name|l
operator|-
literal|1
argument_list|,
name|runBase
operator|+
name|l
argument_list|)
operator|>
literal|0
condition|)
block|{
operator|++
name|l
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|<
name|minRun
operator|&&
name|runBase
operator|+
name|l
operator|<=
name|hi
condition|)
block|{
name|l
operator|=
name|Math
operator|.
name|min
argument_list|(
name|hi
operator|-
name|runBase
operator|+
literal|1
argument_list|,
name|minRun
argument_list|)
expr_stmt|;
name|binarySort
argument_list|(
name|runBase
argument_list|,
name|runBase
operator|+
name|l
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// revert
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|halfL
init|=
name|l
operator|>>>
literal|1
init|;
name|i
operator|<
name|halfL
condition|;
operator|++
name|i
control|)
block|{
name|swap
argument_list|(
name|runBase
operator|+
name|i
argument_list|,
name|runBase
operator|+
name|l
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// run must be non-descending
while|while
condition|(
name|runBase
operator|+
name|l
operator|<=
name|hi
operator|&&
name|compare
argument_list|(
name|runBase
operator|+
name|l
operator|-
literal|1
argument_list|,
name|runBase
operator|+
name|l
argument_list|)
operator|<=
literal|0
condition|)
block|{
operator|++
name|l
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|<
name|minRun
operator|&&
name|runBase
operator|+
name|l
operator|<=
name|hi
condition|)
block|{
name|l
operator|=
name|Math
operator|.
name|min
argument_list|(
name|hi
operator|-
name|runBase
operator|+
literal|1
argument_list|,
name|minRun
argument_list|)
expr_stmt|;
name|binarySort
argument_list|(
name|runBase
argument_list|,
name|runBase
operator|+
name|l
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// else nothing to do, the run is already sorted
block|}
return|return
name|l
return|;
block|}
DECL|method|ensureInvariants
name|void
name|ensureInvariants
parameter_list|()
block|{
while|while
condition|(
name|stackSize
operator|>
literal|1
condition|)
block|{
specifier|final
name|int
name|runLen0
init|=
name|runLen
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|runLen1
init|=
name|runLen
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|stackSize
operator|>
literal|2
condition|)
block|{
specifier|final
name|int
name|runLen2
init|=
name|runLen
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|runLen2
operator|<=
name|runLen1
operator|+
name|runLen0
condition|)
block|{
comment|// merge the smaller of 0 and 2 with 1
if|if
condition|(
name|runLen2
operator|<
name|runLen0
condition|)
block|{
name|mergeAt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mergeAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
block|}
if|if
condition|(
name|runLen1
operator|<=
name|runLen0
condition|)
block|{
name|mergeAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
continue|continue;
block|}
break|break;
block|}
block|}
DECL|method|exhaustStack
name|void
name|exhaustStack
parameter_list|()
block|{
while|while
condition|(
name|stackSize
operator|>
literal|1
condition|)
block|{
name|mergeAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sort
name|void
name|sort
parameter_list|()
block|{
do|do
block|{
name|ensureInvariants
argument_list|()
expr_stmt|;
comment|// Push a new run onto the stack
name|pushRunLen
argument_list|(
name|nextRun
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|runEnd
argument_list|(
literal|0
argument_list|)
operator|<=
name|hi
condition|)
do|;
name|exhaustStack
argument_list|()
expr_stmt|;
assert|assert
name|runEnd
argument_list|(
literal|0
argument_list|)
operator|==
name|hi
operator|+
literal|1
assert|;
block|}
block|}
comment|/** Sorts using<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">TimSort</a>, see     *  also<a href="http://svn.python.org/projects/python/trunk/Objects/listobject.c">source code</a>.    *  TimSort is a stable sorting algorithm based on MergeSort but known to    *  perform extremely well on partially-sorted inputs.    *  For small collections, falls back to {@link #binarySort(int, int)}. */
DECL|method|timSort
specifier|public
specifier|final
name|void
name|timSort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|hi
operator|-
name|lo
operator|<=
name|TIMSORT_THRESHOLD
condition|)
block|{
name|binarySort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
return|return;
block|}
operator|new
name|TimSort
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
operator|.
name|sort
argument_list|()
expr_stmt|;
block|}
comment|/** Sorts via stable in-place MergeSort algorithm    * For small collections falls back to {@link #insertionSort(int,int)}. */
DECL|method|mergeSort
specifier|public
specifier|final
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
specifier|final
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
specifier|final
name|int
name|mid
init|=
name|lo
operator|+
operator|(
name|diff
operator|>>>
literal|1
operator|)
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
comment|// pkg-protected for access from TimSort class
DECL|method|merge
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
operator|>>>
literal|1
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
operator|>>>
literal|1
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
specifier|final
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
specifier|final
name|int
name|half
init|=
name|len
operator|>>>
literal|1
decl_stmt|,
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
specifier|final
name|int
name|half
init|=
name|len
operator|>>>
literal|1
decl_stmt|,
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

