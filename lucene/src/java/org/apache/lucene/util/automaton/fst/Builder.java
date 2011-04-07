begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|fst
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IntsRef
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

begin_comment
comment|/**  * Builds a compact FST (maps an IntsRef term to an arbitrary  * output) from pre-sorted terms with outputs (the FST  * becomes an FSA if you use NoOutputs).  The FST is written  * on-the-fly into a compact serialized format byte array, which can  * be saved to / loaded from a Directory or used directly  * for traversal.  The FST is always finite (no cycles).  *  *<p>NOTE: The algorithm is described at  * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.24.3698</p>  *  * If your outputs are ByteSequenceOutput then the final FST  * will be minimal, but if you use PositiveIntOutput then  * it's only "near minimal".  For example, aa/0, aab/1, bbb/2  * will produce 6 states when a 5 state fst is also  * possible.  *  * The parameterized type T is the output type.  See the  * subclasses of {@link Outputs}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Builder
specifier|public
class|class
name|Builder
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|dedupHash
specifier|private
specifier|final
name|NodeHash
argument_list|<
name|T
argument_list|>
name|dedupHash
decl_stmt|;
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|T
argument_list|>
name|fst
decl_stmt|;
DECL|field|NO_OUTPUT
specifier|private
specifier|final
name|T
name|NO_OUTPUT
decl_stmt|;
comment|// simplistic pruning: we prune node (and all following
comment|// nodes) if less than this number of terms go through it:
DECL|field|minSuffixCount1
specifier|private
specifier|final
name|int
name|minSuffixCount1
decl_stmt|;
comment|// better pruning: we prune node (and all following
comment|// nodes) if the prior node has less than this number of
comment|// terms go through it:
DECL|field|minSuffixCount2
specifier|private
specifier|final
name|int
name|minSuffixCount2
decl_stmt|;
DECL|field|lastInput
specifier|private
specifier|final
name|IntsRef
name|lastInput
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
comment|// NOTE: cutting this over to ArrayList instead loses ~6%
comment|// in build performance on 9.8M Wikipedia terms; so we
comment|// left this as an array:
comment|// current "frontier"
DECL|field|frontier
specifier|private
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
index|[]
name|frontier
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|FST
operator|.
name|INPUT_TYPE
name|inputType
parameter_list|,
name|int
name|minSuffixCount1
parameter_list|,
name|int
name|minSuffixCount2
parameter_list|,
name|boolean
name|doMinSuffix
parameter_list|,
name|Outputs
argument_list|<
name|T
argument_list|>
name|outputs
parameter_list|)
block|{
name|this
operator|.
name|minSuffixCount1
operator|=
name|minSuffixCount1
expr_stmt|;
name|this
operator|.
name|minSuffixCount2
operator|=
name|minSuffixCount2
expr_stmt|;
name|fst
operator|=
operator|new
name|FST
argument_list|<
name|T
argument_list|>
argument_list|(
name|inputType
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
if|if
condition|(
name|doMinSuffix
condition|)
block|{
name|dedupHash
operator|=
operator|new
name|NodeHash
argument_list|<
name|T
argument_list|>
argument_list|(
name|fst
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dedupHash
operator|=
literal|null
expr_stmt|;
block|}
name|NO_OUTPUT
operator|=
name|outputs
operator|.
name|getNoOutput
argument_list|()
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
index|[]
name|f
init|=
operator|(
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
index|[]
operator|)
operator|new
name|UnCompiledNode
index|[
literal|10
index|]
decl_stmt|;
name|frontier
operator|=
name|f
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|frontier
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|frontier
index|[
name|idx
index|]
operator|=
operator|new
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTotStateCount
specifier|public
name|int
name|getTotStateCount
parameter_list|()
block|{
return|return
name|fst
operator|.
name|nodeCount
return|;
block|}
DECL|method|getTermCount
specifier|public
name|long
name|getTermCount
parameter_list|()
block|{
return|return
name|frontier
index|[
literal|0
index|]
operator|.
name|inputCount
return|;
block|}
DECL|method|getMappedStateCount
specifier|public
name|int
name|getMappedStateCount
parameter_list|()
block|{
return|return
name|dedupHash
operator|==
literal|null
condition|?
literal|0
else|:
name|fst
operator|.
name|nodeCount
return|;
block|}
DECL|method|compileNode
specifier|private
name|CompiledNode
name|compileNode
parameter_list|(
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|address
decl_stmt|;
if|if
condition|(
name|dedupHash
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|.
name|numArcs
operator|==
literal|0
condition|)
block|{
name|address
operator|=
name|fst
operator|.
name|addNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
name|dedupHash
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|address
operator|=
name|fst
operator|.
name|addNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
assert|assert
name|address
operator|!=
operator|-
literal|2
assert|;
name|n
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|CompiledNode
name|fn
init|=
operator|new
name|CompiledNode
argument_list|()
decl_stmt|;
name|fn
operator|.
name|address
operator|=
name|address
expr_stmt|;
return|return
name|fn
return|;
block|}
DECL|method|compilePrevTail
specifier|private
name|void
name|compilePrevTail
parameter_list|(
name|int
name|prefixLenPlus1
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|prefixLenPlus1
operator|>=
literal|1
assert|;
comment|//System.out.println("  compileTail " + prefixLenPlus1);
for|for
control|(
name|int
name|idx
init|=
name|lastInput
operator|.
name|length
init|;
name|idx
operator|>=
name|prefixLenPlus1
condition|;
name|idx
operator|--
control|)
block|{
name|boolean
name|doPrune
init|=
literal|false
decl_stmt|;
name|boolean
name|doCompile
init|=
literal|false
decl_stmt|;
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|node
init|=
name|frontier
index|[
name|idx
index|]
decl_stmt|;
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|parent
init|=
name|frontier
index|[
name|idx
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|inputCount
operator|<
name|minSuffixCount1
condition|)
block|{
name|doPrune
operator|=
literal|true
expr_stmt|;
name|doCompile
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|>
name|prefixLenPlus1
condition|)
block|{
comment|// prune if parent's inputCount is less than suffixMinCount2
if|if
condition|(
name|parent
operator|.
name|inputCount
operator|<
name|minSuffixCount2
operator|||
name|minSuffixCount2
operator|==
literal|1
operator|&&
name|parent
operator|.
name|inputCount
operator|==
literal|1
condition|)
block|{
comment|// my parent, about to be compiled, doesn't make the cut, so
comment|// I'm definitely pruned
comment|// if pruneCount2 is 1, we keep only up
comment|// until the 'distinguished edge', ie we keep only the
comment|// 'divergent' part of the FST. if my parent, about to be
comment|// compiled, has inputCount 1 then we are already past the
comment|// distinguished edge.  NOTE: this only works if
comment|// the FST outputs are not "compressible" (simple
comment|// ords ARE compressible).
name|doPrune
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// my parent, about to be compiled, does make the cut, so
comment|// I'm definitely not pruned
name|doPrune
operator|=
literal|false
expr_stmt|;
block|}
name|doCompile
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// if pruning is disabled (count is 0) we can always
comment|// compile current node
name|doCompile
operator|=
name|minSuffixCount2
operator|==
literal|0
expr_stmt|;
block|}
comment|//System.out.println("    label=" + ((char) lastInput.ints[lastInput.offset+idx-1]) + " idx=" + idx + " inputCount=" + frontier[idx].inputCount + " doCompile=" + doCompile + " doPrune=" + doPrune);
if|if
condition|(
name|node
operator|.
name|inputCount
operator|<
name|minSuffixCount2
operator|||
name|minSuffixCount2
operator|==
literal|1
operator|&&
name|node
operator|.
name|inputCount
operator|==
literal|1
condition|)
block|{
comment|// drop all arcs
for|for
control|(
name|int
name|arcIdx
init|=
literal|0
init|;
name|arcIdx
operator|<
name|node
operator|.
name|numArcs
condition|;
name|arcIdx
operator|++
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|target
init|=
operator|(
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
operator|)
name|node
operator|.
name|arcs
index|[
name|arcIdx
index|]
operator|.
name|target
decl_stmt|;
name|target
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|node
operator|.
name|numArcs
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|doPrune
condition|)
block|{
comment|// this node doesn't make it -- deref it
name|node
operator|.
name|clear
argument_list|()
expr_stmt|;
name|parent
operator|.
name|deleteLast
argument_list|(
name|lastInput
operator|.
name|ints
index|[
name|lastInput
operator|.
name|offset
operator|+
name|idx
operator|-
literal|1
index|]
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|minSuffixCount2
operator|!=
literal|0
condition|)
block|{
name|compileAllTargets
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|final
name|T
name|nextFinalOutput
init|=
name|node
operator|.
name|output
decl_stmt|;
specifier|final
name|boolean
name|isFinal
init|=
name|node
operator|.
name|isFinal
decl_stmt|;
if|if
condition|(
name|doCompile
condition|)
block|{
comment|// this node makes it and we now compile it.  first,
comment|// compile any targets that were previously
comment|// undecided:
name|parent
operator|.
name|replaceLast
argument_list|(
name|lastInput
operator|.
name|ints
index|[
name|lastInput
operator|.
name|offset
operator|+
name|idx
operator|-
literal|1
index|]
argument_list|,
name|compileNode
argument_list|(
name|node
argument_list|)
argument_list|,
name|nextFinalOutput
argument_list|,
name|isFinal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// replaceLast just to install
comment|// nextFinalOutput/isFinal onto the arc
name|parent
operator|.
name|replaceLast
argument_list|(
name|lastInput
operator|.
name|ints
index|[
name|lastInput
operator|.
name|offset
operator|+
name|idx
operator|-
literal|1
index|]
argument_list|,
name|node
argument_list|,
name|nextFinalOutput
argument_list|,
name|isFinal
argument_list|)
expr_stmt|;
comment|// this node will stay in play for now, since we are
comment|// undecided on whether to prune it.  later, it
comment|// will be either compiled or pruned, so we must
comment|// allocate a new node:
name|frontier
index|[
name|idx
index|]
operator|=
operator|new
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|field|scratchIntsRef
specifier|private
specifier|final
name|IntsRef
name|scratchIntsRef
init|=
operator|new
name|IntsRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|T
name|output
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|getInputType
argument_list|()
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
assert|;
name|scratchIntsRef
operator|.
name|grow
argument_list|(
name|input
operator|.
name|length
argument_list|)
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
name|input
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scratchIntsRef
operator|.
name|ints
index|[
name|i
index|]
operator|=
name|input
operator|.
name|bytes
index|[
name|i
operator|+
name|input
operator|.
name|offset
index|]
operator|&
literal|0xFF
expr_stmt|;
block|}
name|scratchIntsRef
operator|.
name|length
operator|=
name|input
operator|.
name|length
expr_stmt|;
name|add
argument_list|(
name|scratchIntsRef
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/** Sugar: adds the UTF32 codepoints from char[] slice.  FST    *  must be FST.INPUT_TYPE.BYTE4! */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|T
name|output
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|getInputType
argument_list|()
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
assert|;
name|int
name|charIdx
init|=
name|offset
decl_stmt|;
name|int
name|intIdx
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|charLimit
init|=
name|offset
operator|+
name|length
decl_stmt|;
while|while
condition|(
name|charIdx
operator|<
name|charLimit
condition|)
block|{
name|scratchIntsRef
operator|.
name|grow
argument_list|(
name|intIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|utf32
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|s
argument_list|,
name|charIdx
argument_list|)
decl_stmt|;
name|scratchIntsRef
operator|.
name|ints
index|[
name|intIdx
index|]
operator|=
name|utf32
expr_stmt|;
name|charIdx
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|utf32
argument_list|)
expr_stmt|;
name|intIdx
operator|++
expr_stmt|;
block|}
name|scratchIntsRef
operator|.
name|length
operator|=
name|intIdx
expr_stmt|;
name|add
argument_list|(
name|scratchIntsRef
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/** Sugar: adds the UTF32 codepoints from CharSequence.  FST    *  must be FST.INPUT_TYPE.BYTE4! */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|T
name|output
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fst
operator|.
name|getInputType
argument_list|()
operator|==
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE4
assert|;
name|int
name|charIdx
init|=
literal|0
decl_stmt|;
name|int
name|intIdx
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|charLimit
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|charIdx
operator|<
name|charLimit
condition|)
block|{
name|scratchIntsRef
operator|.
name|grow
argument_list|(
name|intIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|utf32
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|s
argument_list|,
name|charIdx
argument_list|)
decl_stmt|;
name|scratchIntsRef
operator|.
name|ints
index|[
name|intIdx
index|]
operator|=
name|utf32
expr_stmt|;
name|charIdx
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|utf32
argument_list|)
expr_stmt|;
name|intIdx
operator|++
expr_stmt|;
block|}
name|scratchIntsRef
operator|.
name|length
operator|=
name|intIdx
expr_stmt|;
name|add
argument_list|(
name|scratchIntsRef
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IntsRef
name|input
parameter_list|,
name|T
name|output
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("\nFST ADD: input=" + input + " output=" + fst.outputs.outputToString(output));
assert|assert
name|lastInput
operator|.
name|length
operator|==
literal|0
operator|||
name|input
operator|.
name|compareTo
argument_list|(
name|lastInput
argument_list|)
operator|>
literal|0
operator|:
literal|"inputs are added out of order lastInput="
operator|+
name|lastInput
operator|+
literal|" vs input="
operator|+
name|input
assert|;
assert|assert
name|validOutput
argument_list|(
name|output
argument_list|)
assert|;
comment|//System.out.println("\nadd: " + input);
if|if
condition|(
name|input
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// empty input: only allowed as first input.  we have
comment|// to special case this because the packed FST
comment|// format cannot represent the empty input since
comment|// 'finalness' is stored on the incoming arc, not on
comment|// the node
name|frontier
index|[
literal|0
index|]
operator|.
name|inputCount
operator|++
expr_stmt|;
name|fst
operator|.
name|setEmptyOutput
argument_list|(
name|output
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// compare shared prefix length
name|int
name|pos1
init|=
literal|0
decl_stmt|;
name|int
name|pos2
init|=
name|input
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|pos1Stop
init|=
name|Math
operator|.
name|min
argument_list|(
name|lastInput
operator|.
name|length
argument_list|,
name|input
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  incr " + pos1);
name|frontier
index|[
name|pos1
index|]
operator|.
name|inputCount
operator|++
expr_stmt|;
if|if
condition|(
name|pos1
operator|>=
name|pos1Stop
operator|||
name|lastInput
operator|.
name|ints
index|[
name|pos1
index|]
operator|!=
name|input
operator|.
name|ints
index|[
name|pos2
index|]
condition|)
block|{
break|break;
block|}
name|pos1
operator|++
expr_stmt|;
name|pos2
operator|++
expr_stmt|;
block|}
specifier|final
name|int
name|prefixLenPlus1
init|=
name|pos1
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|frontier
operator|.
name|length
operator|<
name|input
operator|.
name|length
operator|+
literal|1
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
index|[]
name|next
init|=
operator|new
name|UnCompiledNode
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|input
operator|.
name|length
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|frontier
argument_list|,
literal|0
argument_list|,
name|next
argument_list|,
literal|0
argument_list|,
name|frontier
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|frontier
operator|.
name|length
init|;
name|idx
operator|<
name|next
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|next
index|[
name|idx
index|]
operator|=
operator|new
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
name|frontier
operator|=
name|next
expr_stmt|;
block|}
comment|// minimize/compile states from previous input's
comment|// orphan'd suffix
name|compilePrevTail
argument_list|(
name|prefixLenPlus1
argument_list|)
expr_stmt|;
comment|// init tail states for current input
for|for
control|(
name|int
name|idx
init|=
name|prefixLenPlus1
init|;
name|idx
operator|<=
name|input
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|frontier
index|[
name|idx
operator|-
literal|1
index|]
operator|.
name|addArc
argument_list|(
name|input
operator|.
name|ints
index|[
name|input
operator|.
name|offset
operator|+
name|idx
operator|-
literal|1
index|]
argument_list|,
name|frontier
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
comment|//System.out.println("  incr tail " + idx);
name|frontier
index|[
name|idx
index|]
operator|.
name|inputCount
operator|++
expr_stmt|;
block|}
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|lastNode
init|=
name|frontier
index|[
name|input
operator|.
name|length
index|]
decl_stmt|;
name|lastNode
operator|.
name|isFinal
operator|=
literal|true
expr_stmt|;
name|lastNode
operator|.
name|output
operator|=
name|NO_OUTPUT
expr_stmt|;
comment|// push conflicting outputs forward, only as far as
comment|// needed
for|for
control|(
name|int
name|idx
init|=
literal|1
init|;
name|idx
operator|<
name|prefixLenPlus1
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|node
init|=
name|frontier
index|[
name|idx
index|]
decl_stmt|;
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|parentNode
init|=
name|frontier
index|[
name|idx
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|T
name|lastOutput
init|=
name|parentNode
operator|.
name|getLastOutput
argument_list|(
name|input
operator|.
name|ints
index|[
name|input
operator|.
name|offset
operator|+
name|idx
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
assert|assert
name|validOutput
argument_list|(
name|lastOutput
argument_list|)
assert|;
specifier|final
name|T
name|commonOutputPrefix
decl_stmt|;
specifier|final
name|T
name|wordSuffix
decl_stmt|;
if|if
condition|(
name|lastOutput
operator|!=
name|NO_OUTPUT
condition|)
block|{
name|commonOutputPrefix
operator|=
name|fst
operator|.
name|outputs
operator|.
name|common
argument_list|(
name|output
argument_list|,
name|lastOutput
argument_list|)
expr_stmt|;
assert|assert
name|validOutput
argument_list|(
name|commonOutputPrefix
argument_list|)
assert|;
name|wordSuffix
operator|=
name|fst
operator|.
name|outputs
operator|.
name|subtract
argument_list|(
name|lastOutput
argument_list|,
name|commonOutputPrefix
argument_list|)
expr_stmt|;
assert|assert
name|validOutput
argument_list|(
name|wordSuffix
argument_list|)
assert|;
name|parentNode
operator|.
name|setLastOutput
argument_list|(
name|input
operator|.
name|ints
index|[
name|input
operator|.
name|offset
operator|+
name|idx
operator|-
literal|1
index|]
argument_list|,
name|commonOutputPrefix
argument_list|)
expr_stmt|;
name|node
operator|.
name|prependOutput
argument_list|(
name|wordSuffix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commonOutputPrefix
operator|=
name|wordSuffix
operator|=
name|NO_OUTPUT
expr_stmt|;
block|}
name|output
operator|=
name|fst
operator|.
name|outputs
operator|.
name|subtract
argument_list|(
name|output
argument_list|,
name|commonOutputPrefix
argument_list|)
expr_stmt|;
assert|assert
name|validOutput
argument_list|(
name|output
argument_list|)
assert|;
block|}
comment|// push remaining output:
name|frontier
index|[
name|prefixLenPlus1
operator|-
literal|1
index|]
operator|.
name|setLastOutput
argument_list|(
name|input
operator|.
name|ints
index|[
name|input
operator|.
name|offset
operator|+
name|prefixLenPlus1
operator|-
literal|1
index|]
argument_list|,
name|output
argument_list|)
expr_stmt|;
comment|// save last input
name|lastInput
operator|.
name|copy
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|//System.out.println("  count[0]=" + frontier[0].inputCount);
block|}
DECL|method|validOutput
specifier|private
name|boolean
name|validOutput
parameter_list|(
name|T
name|output
parameter_list|)
block|{
return|return
name|output
operator|==
name|NO_OUTPUT
operator|||
operator|!
name|output
operator|.
name|equals
argument_list|(
name|NO_OUTPUT
argument_list|)
return|;
block|}
comment|/** Returns final FST.  NOTE: this will return null if    *  nothing is accepted by the FST. */
DECL|method|finish
specifier|public
name|FST
argument_list|<
name|T
argument_list|>
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
comment|// minimize nodes in the last word's suffix
name|compilePrevTail
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|//System.out.println("finish: inputCount=" + frontier[0].inputCount);
if|if
condition|(
name|frontier
index|[
literal|0
index|]
operator|.
name|inputCount
operator|<
name|minSuffixCount1
operator|||
name|frontier
index|[
literal|0
index|]
operator|.
name|inputCount
operator|<
name|minSuffixCount2
operator|||
name|frontier
index|[
literal|0
index|]
operator|.
name|numArcs
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|fst
operator|.
name|emptyOutput
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|minSuffixCount1
operator|>
literal|0
operator|||
name|minSuffixCount2
operator|>
literal|0
condition|)
block|{
comment|// empty string got pruned
return|return
literal|null
return|;
block|}
else|else
block|{
name|fst
operator|.
name|finish
argument_list|(
name|compileNode
argument_list|(
name|frontier
index|[
literal|0
index|]
argument_list|)
operator|.
name|address
argument_list|)
expr_stmt|;
comment|//System.out.println("compile addr = " + fst.getStartNode());
return|return
name|fst
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|minSuffixCount2
operator|!=
literal|0
condition|)
block|{
name|compileAllTargets
argument_list|(
name|frontier
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("NOW: " + frontier[0].numArcs);
name|fst
operator|.
name|finish
argument_list|(
name|compileNode
argument_list|(
name|frontier
index|[
literal|0
index|]
argument_list|)
operator|.
name|address
argument_list|)
expr_stmt|;
block|}
return|return
name|fst
return|;
block|}
DECL|method|compileAllTargets
specifier|private
name|void
name|compileAllTargets
parameter_list|(
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|node
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|arcIdx
init|=
literal|0
init|;
name|arcIdx
operator|<
name|node
operator|.
name|numArcs
condition|;
name|arcIdx
operator|++
control|)
block|{
specifier|final
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|node
operator|.
name|arcs
index|[
name|arcIdx
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|arc
operator|.
name|target
operator|.
name|isCompiled
argument_list|()
condition|)
block|{
comment|// not yet compiled
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|n
init|=
operator|(
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
operator|)
name|arc
operator|.
name|target
decl_stmt|;
name|arc
operator|.
name|target
operator|=
name|compileNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Arc
specifier|static
class|class
name|Arc
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|label
specifier|public
name|int
name|label
decl_stmt|;
comment|// really an "unsigned" byte
DECL|field|target
specifier|public
name|Node
name|target
decl_stmt|;
DECL|field|isFinal
specifier|public
name|boolean
name|isFinal
decl_stmt|;
DECL|field|output
specifier|public
name|T
name|output
decl_stmt|;
DECL|field|nextFinalOutput
specifier|public
name|T
name|nextFinalOutput
decl_stmt|;
block|}
comment|// NOTE: not many instances of Node or CompiledNode are in
comment|// memory while the FST is being built; it's only the
comment|// current "frontier":
DECL|interface|Node
specifier|static
interface|interface
name|Node
block|{
DECL|method|isCompiled
name|boolean
name|isCompiled
parameter_list|()
function_decl|;
block|}
DECL|class|CompiledNode
specifier|static
specifier|final
class|class
name|CompiledNode
implements|implements
name|Node
block|{
DECL|field|address
name|int
name|address
decl_stmt|;
DECL|method|isCompiled
specifier|public
name|boolean
name|isCompiled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|class|UnCompiledNode
specifier|static
specifier|final
class|class
name|UnCompiledNode
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Node
block|{
DECL|field|owner
specifier|final
name|Builder
argument_list|<
name|T
argument_list|>
name|owner
decl_stmt|;
DECL|field|numArcs
name|int
name|numArcs
decl_stmt|;
DECL|field|arcs
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
name|arcs
decl_stmt|;
DECL|field|output
name|T
name|output
decl_stmt|;
DECL|field|isFinal
name|boolean
name|isFinal
decl_stmt|;
DECL|field|inputCount
name|long
name|inputCount
decl_stmt|;
comment|/** This node's depth, starting from the automaton root. */
DECL|field|depth
specifier|final
name|int
name|depth
decl_stmt|;
comment|/**      * @param depth      *          The node's depth starting from the automaton root. Needed for      *          LUCENE-2934 (node expansion based on conditions other than the      *          fanout size).      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|UnCompiledNode
specifier|public
name|UnCompiledNode
parameter_list|(
name|Builder
argument_list|<
name|T
argument_list|>
name|owner
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|arcs
operator|=
operator|(
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
operator|)
operator|new
name|Arc
index|[
literal|1
index|]
expr_stmt|;
name|arcs
index|[
literal|0
index|]
operator|=
operator|new
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
expr_stmt|;
name|output
operator|=
name|owner
operator|.
name|NO_OUTPUT
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
DECL|method|isCompiled
specifier|public
name|boolean
name|isCompiled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|numArcs
operator|=
literal|0
expr_stmt|;
name|isFinal
operator|=
literal|false
expr_stmt|;
name|output
operator|=
name|owner
operator|.
name|NO_OUTPUT
expr_stmt|;
name|inputCount
operator|=
literal|0
expr_stmt|;
comment|// We don't clear the depth here because it never changes
comment|// for nodes on the frontier (even when reused).
block|}
DECL|method|getLastOutput
specifier|public
name|T
name|getLastOutput
parameter_list|(
name|int
name|labelToMatch
parameter_list|)
block|{
assert|assert
name|numArcs
operator|>
literal|0
assert|;
assert|assert
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
operator|.
name|label
operator|==
name|labelToMatch
assert|;
return|return
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
operator|.
name|output
return|;
block|}
DECL|method|addArc
specifier|public
name|void
name|addArc
parameter_list|(
name|int
name|label
parameter_list|,
name|Node
name|target
parameter_list|)
block|{
assert|assert
name|label
operator|>=
literal|0
assert|;
assert|assert
name|numArcs
operator|==
literal|0
operator|||
name|label
operator|>
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
operator|.
name|label
operator|:
literal|"arc[-1].label="
operator|+
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
operator|.
name|label
operator|+
literal|" new label="
operator|+
name|label
operator|+
literal|" numArcs="
operator|+
name|numArcs
assert|;
if|if
condition|(
name|numArcs
operator|==
name|arcs
operator|.
name|length
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
name|newArcs
init|=
operator|new
name|Arc
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numArcs
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arcs
argument_list|,
literal|0
argument_list|,
name|newArcs
argument_list|,
literal|0
argument_list|,
name|arcs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|arcIdx
init|=
name|numArcs
init|;
name|arcIdx
operator|<
name|newArcs
operator|.
name|length
condition|;
name|arcIdx
operator|++
control|)
block|{
name|newArcs
index|[
name|arcIdx
index|]
operator|=
operator|new
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|arcs
operator|=
name|newArcs
expr_stmt|;
block|}
specifier|final
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|arcs
index|[
name|numArcs
operator|++
index|]
decl_stmt|;
name|arc
operator|.
name|label
operator|=
name|label
expr_stmt|;
name|arc
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|arc
operator|.
name|output
operator|=
name|arc
operator|.
name|nextFinalOutput
operator|=
name|owner
operator|.
name|NO_OUTPUT
expr_stmt|;
name|arc
operator|.
name|isFinal
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|replaceLast
specifier|public
name|void
name|replaceLast
parameter_list|(
name|int
name|labelToMatch
parameter_list|,
name|Node
name|target
parameter_list|,
name|T
name|nextFinalOutput
parameter_list|,
name|boolean
name|isFinal
parameter_list|)
block|{
assert|assert
name|numArcs
operator|>
literal|0
assert|;
specifier|final
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
decl_stmt|;
assert|assert
name|arc
operator|.
name|label
operator|==
name|labelToMatch
operator|:
literal|"arc.label="
operator|+
name|arc
operator|.
name|label
operator|+
literal|" vs "
operator|+
name|labelToMatch
assert|;
name|arc
operator|.
name|target
operator|=
name|target
expr_stmt|;
comment|//assert target.address != -2;
name|arc
operator|.
name|nextFinalOutput
operator|=
name|nextFinalOutput
expr_stmt|;
name|arc
operator|.
name|isFinal
operator|=
name|isFinal
expr_stmt|;
block|}
DECL|method|deleteLast
specifier|public
name|void
name|deleteLast
parameter_list|(
name|int
name|label
parameter_list|,
name|Node
name|target
parameter_list|)
block|{
assert|assert
name|numArcs
operator|>
literal|0
assert|;
assert|assert
name|label
operator|==
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
operator|.
name|label
assert|;
assert|assert
name|target
operator|==
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
operator|.
name|target
assert|;
name|numArcs
operator|--
expr_stmt|;
block|}
DECL|method|setLastOutput
specifier|public
name|void
name|setLastOutput
parameter_list|(
name|int
name|labelToMatch
parameter_list|,
name|T
name|newOutput
parameter_list|)
block|{
assert|assert
name|owner
operator|.
name|validOutput
argument_list|(
name|newOutput
argument_list|)
assert|;
assert|assert
name|numArcs
operator|>
literal|0
assert|;
specifier|final
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|arcs
index|[
name|numArcs
operator|-
literal|1
index|]
decl_stmt|;
assert|assert
name|arc
operator|.
name|label
operator|==
name|labelToMatch
assert|;
name|arc
operator|.
name|output
operator|=
name|newOutput
expr_stmt|;
block|}
comment|// pushes an output prefix forward onto all arcs
DECL|method|prependOutput
specifier|public
name|void
name|prependOutput
parameter_list|(
name|T
name|outputPrefix
parameter_list|)
block|{
assert|assert
name|owner
operator|.
name|validOutput
argument_list|(
name|outputPrefix
argument_list|)
assert|;
for|for
control|(
name|int
name|arcIdx
init|=
literal|0
init|;
name|arcIdx
operator|<
name|numArcs
condition|;
name|arcIdx
operator|++
control|)
block|{
name|arcs
index|[
name|arcIdx
index|]
operator|.
name|output
operator|=
name|owner
operator|.
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|outputPrefix
argument_list|,
name|arcs
index|[
name|arcIdx
index|]
operator|.
name|output
argument_list|)
expr_stmt|;
assert|assert
name|owner
operator|.
name|validOutput
argument_list|(
name|arcs
index|[
name|arcIdx
index|]
operator|.
name|output
argument_list|)
assert|;
block|}
if|if
condition|(
name|isFinal
condition|)
block|{
name|output
operator|=
name|owner
operator|.
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|outputPrefix
argument_list|,
name|output
argument_list|)
expr_stmt|;
assert|assert
name|owner
operator|.
name|validOutput
argument_list|(
name|output
argument_list|)
assert|;
block|}
block|}
block|}
block|}
end_class

end_unit

