begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton
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
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
comment|/**  * Class to construct DFAs that match a word within some edit distance.  *<p>  * Implements the algorithm described in:  * Schulz and Mihov: Fast String Correction with Levenshtein Automata  * @lucene.experimental  */
end_comment

begin_class
DECL|class|LevenshteinAutomata
specifier|public
class|class
name|LevenshteinAutomata
block|{
comment|/** Maximum edit distance this class can generate an automaton for.    *  @lucene.internal */
DECL|field|MAXIMUM_SUPPORTED_DISTANCE
specifier|public
specifier|static
specifier|final
name|int
name|MAXIMUM_SUPPORTED_DISTANCE
init|=
literal|2
decl_stmt|;
comment|/* input word */
DECL|field|word
specifier|final
name|int
name|word
index|[]
decl_stmt|;
comment|/* the automata alphabet. */
DECL|field|alphabet
specifier|final
name|int
name|alphabet
index|[]
decl_stmt|;
comment|/* the maximum symbol in the alphabet (e.g. 255 for UTF-8 or 10FFFF for UTF-32) */
DECL|field|alphaMax
specifier|final
name|int
name|alphaMax
decl_stmt|;
comment|/* the ranges outside of alphabet */
DECL|field|rangeLower
specifier|final
name|int
name|rangeLower
index|[]
decl_stmt|;
DECL|field|rangeUpper
specifier|final
name|int
name|rangeUpper
index|[]
decl_stmt|;
DECL|field|numRanges
name|int
name|numRanges
init|=
literal|0
decl_stmt|;
DECL|field|descriptions
name|ParametricDescription
name|descriptions
index|[]
decl_stmt|;
comment|/**    * Create a new LevenshteinAutomata for some input String.    * Optionally count transpositions as a primitive edit.    */
DECL|method|LevenshteinAutomata
specifier|public
name|LevenshteinAutomata
parameter_list|(
name|String
name|input
parameter_list|,
name|boolean
name|withTranspositions
parameter_list|)
block|{
name|this
argument_list|(
name|codePoints
argument_list|(
name|input
argument_list|)
argument_list|,
name|Character
operator|.
name|MAX_CODE_POINT
argument_list|,
name|withTranspositions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: specify a custom maximum possible symbol    * (alphaMax); default is Character.MAX_CODE_POINT.    */
DECL|method|LevenshteinAutomata
specifier|public
name|LevenshteinAutomata
parameter_list|(
name|int
index|[]
name|word
parameter_list|,
name|int
name|alphaMax
parameter_list|,
name|boolean
name|withTranspositions
parameter_list|)
block|{
name|this
operator|.
name|word
operator|=
name|word
expr_stmt|;
name|this
operator|.
name|alphaMax
operator|=
name|alphaMax
expr_stmt|;
comment|// calculate the alphabet
name|SortedSet
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
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
name|word
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|v
init|=
name|word
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|v
operator|>
name|alphaMax
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"alphaMax exceeded by symbol "
operator|+
name|v
operator|+
literal|" in word"
argument_list|)
throw|;
block|}
name|set
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|alphabet
operator|=
operator|new
name|int
index|[
name|set
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
init|=
name|set
operator|.
name|iterator
argument_list|()
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
name|alphabet
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|alphabet
index|[
name|i
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|rangeLower
operator|=
operator|new
name|int
index|[
name|alphabet
operator|.
name|length
operator|+
literal|2
index|]
expr_stmt|;
name|rangeUpper
operator|=
operator|new
name|int
index|[
name|alphabet
operator|.
name|length
operator|+
literal|2
index|]
expr_stmt|;
comment|// calculate the unicode range intervals that exclude the alphabet
comment|// these are the ranges for all unicode characters not in the alphabet
name|int
name|lower
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
name|alphabet
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|higher
init|=
name|alphabet
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|higher
operator|>
name|lower
condition|)
block|{
name|rangeLower
index|[
name|numRanges
index|]
operator|=
name|lower
expr_stmt|;
name|rangeUpper
index|[
name|numRanges
index|]
operator|=
name|higher
operator|-
literal|1
expr_stmt|;
name|numRanges
operator|++
expr_stmt|;
block|}
name|lower
operator|=
name|higher
operator|+
literal|1
expr_stmt|;
block|}
comment|/* add the final endpoint */
if|if
condition|(
name|lower
operator|<=
name|alphaMax
condition|)
block|{
name|rangeLower
index|[
name|numRanges
index|]
operator|=
name|lower
expr_stmt|;
name|rangeUpper
index|[
name|numRanges
index|]
operator|=
name|alphaMax
expr_stmt|;
name|numRanges
operator|++
expr_stmt|;
block|}
name|descriptions
operator|=
operator|new
name|ParametricDescription
index|[]
block|{
literal|null
block|,
comment|/* for n=0, we do not need to go through the trouble */
name|withTranspositions
condition|?
operator|new
name|Lev1TParametricDescription
argument_list|(
name|word
operator|.
name|length
argument_list|)
else|:
operator|new
name|Lev1ParametricDescription
argument_list|(
name|word
operator|.
name|length
argument_list|)
block|,
name|withTranspositions
condition|?
operator|new
name|Lev2TParametricDescription
argument_list|(
name|word
operator|.
name|length
argument_list|)
else|:
operator|new
name|Lev2ParametricDescription
argument_list|(
name|word
operator|.
name|length
argument_list|)
block|,     }
expr_stmt|;
block|}
DECL|method|codePoints
specifier|private
specifier|static
name|int
index|[]
name|codePoints
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|int
name|length
init|=
name|Character
operator|.
name|codePointCount
argument_list|(
name|input
argument_list|,
literal|0
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|word
index|[]
init|=
operator|new
name|int
index|[
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|;
name|i
operator|<
name|input
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|word
index|[
name|j
operator|++
index|]
operator|=
name|cp
operator|=
name|input
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|word
return|;
block|}
comment|/**    * Compute a DFA that accepts all strings within an edit distance of<code>n</code>.    *<p>    * All automata have the following properties:    *<ul>    *<li>They are deterministic (DFA).    *<li>There are no transitions to dead states.    *<li>They are not minimal (some transitions could be combined).    *</ul>    */
DECL|method|toAutomaton
specifier|public
name|Automaton
name|toAutomaton
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|toAutomaton
argument_list|(
name|n
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**    * Compute a DFA that accepts all strings within an edit distance of<code>n</code>,    * matching the specified exact prefix.    *<p>    * All automata have the following properties:    *<ul>    *<li>They are deterministic (DFA).    *<li>There are no transitions to dead states.    *<li>They are not minimal (some transitions could be combined).    *</ul>    */
DECL|method|toAutomaton
specifier|public
name|Automaton
name|toAutomaton
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
assert|assert
name|prefix
operator|!=
literal|null
assert|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
return|return
name|Automata
operator|.
name|makeString
argument_list|(
name|prefix
operator|+
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|word
argument_list|,
literal|0
argument_list|,
name|word
operator|.
name|length
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|n
operator|>=
name|descriptions
operator|.
name|length
condition|)
return|return
literal|null
return|;
specifier|final
name|int
name|range
init|=
literal|2
operator|*
name|n
operator|+
literal|1
decl_stmt|;
name|ParametricDescription
name|description
init|=
name|descriptions
index|[
name|n
index|]
decl_stmt|;
comment|// the number of states is based on the length of the word and n
name|int
name|numStates
init|=
name|description
operator|.
name|size
argument_list|()
decl_stmt|;
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|int
name|lastState
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
comment|// Insert prefix
name|lastState
operator|=
name|a
operator|.
name|createState
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|cp
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|int
name|state
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|cp
operator|=
name|prefix
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|lastState
argument_list|,
name|state
argument_list|,
name|cp
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|lastState
operator|=
name|state
expr_stmt|;
block|}
block|}
else|else
block|{
name|lastState
operator|=
name|a
operator|.
name|createState
argument_list|()
expr_stmt|;
block|}
name|int
name|stateOffset
init|=
name|lastState
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|lastState
argument_list|,
name|description
operator|.
name|isAccept
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// create all states, and mark as accept states if appropriate
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numStates
condition|;
name|i
operator|++
control|)
block|{
name|int
name|state
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|state
argument_list|,
name|description
operator|.
name|isAccept
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO: this creates bogus states/transitions (states are final, have self loops, and can't be reached from an init state)
comment|// create transitions from state to state
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|numStates
condition|;
name|k
operator|++
control|)
block|{
specifier|final
name|int
name|xpos
init|=
name|description
operator|.
name|getPosition
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|xpos
operator|<
literal|0
condition|)
continue|continue;
specifier|final
name|int
name|end
init|=
name|xpos
operator|+
name|Math
operator|.
name|min
argument_list|(
name|word
operator|.
name|length
operator|-
name|xpos
argument_list|,
name|range
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|alphabet
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
specifier|final
name|int
name|ch
init|=
name|alphabet
index|[
name|x
index|]
decl_stmt|;
comment|// get the characteristic vector at this position wrt ch
specifier|final
name|int
name|cvec
init|=
name|getVector
argument_list|(
name|ch
argument_list|,
name|xpos
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|int
name|dest
init|=
name|description
operator|.
name|transition
argument_list|(
name|k
argument_list|,
name|xpos
argument_list|,
name|cvec
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|>=
literal|0
condition|)
block|{
name|a
operator|.
name|addTransition
argument_list|(
name|stateOffset
operator|+
name|k
argument_list|,
name|stateOffset
operator|+
name|dest
argument_list|,
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add transitions for all other chars in unicode
comment|// by definition, their characteristic vectors are always 0,
comment|// because they do not exist in the input string.
name|int
name|dest
init|=
name|description
operator|.
name|transition
argument_list|(
name|k
argument_list|,
name|xpos
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// by definition
if|if
condition|(
name|dest
operator|>=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|r
init|=
literal|0
init|;
name|r
operator|<
name|numRanges
condition|;
name|r
operator|++
control|)
block|{
name|a
operator|.
name|addTransition
argument_list|(
name|stateOffset
operator|+
name|k
argument_list|,
name|stateOffset
operator|+
name|dest
argument_list|,
name|rangeLower
index|[
name|r
index|]
argument_list|,
name|rangeUpper
index|[
name|r
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|a
operator|.
name|finishState
argument_list|()
expr_stmt|;
assert|assert
name|a
operator|.
name|isDeterministic
argument_list|()
assert|;
return|return
name|a
return|;
block|}
comment|/**    * Get the characteristic vector<code>X(x, V)</code>     * where V is<code>substring(pos, end)</code>    */
DECL|method|getVector
name|int
name|getVector
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|int
name|vector
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pos
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|vector
operator|<<=
literal|1
expr_stmt|;
if|if
condition|(
name|word
index|[
name|i
index|]
operator|==
name|x
condition|)
name|vector
operator||=
literal|1
expr_stmt|;
block|}
return|return
name|vector
return|;
block|}
comment|/**    * A ParametricDescription describes the structure of a Levenshtein DFA for some degree n.    *<p>    * There are four components of a parametric description, all parameterized on the length    * of the word<code>w</code>:    *<ol>    *<li>The number of states: {@link #size()}    *<li>The set of final states: {@link #isAccept(int)}    *<li>The transition function: {@link #transition(int, int, int)}    *<li>Minimal boundary function: {@link #getPosition(int)}    *</ol>    */
DECL|class|ParametricDescription
specifier|static
specifier|abstract
class|class
name|ParametricDescription
block|{
DECL|field|w
specifier|protected
specifier|final
name|int
name|w
decl_stmt|;
DECL|field|n
specifier|protected
specifier|final
name|int
name|n
decl_stmt|;
DECL|field|minErrors
specifier|private
specifier|final
name|int
index|[]
name|minErrors
decl_stmt|;
DECL|method|ParametricDescription
name|ParametricDescription
parameter_list|(
name|int
name|w
parameter_list|,
name|int
name|n
parameter_list|,
name|int
index|[]
name|minErrors
parameter_list|)
block|{
name|this
operator|.
name|w
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|minErrors
operator|=
name|minErrors
expr_stmt|;
block|}
comment|/**      * Return the number of states needed to compute a Levenshtein DFA      */
DECL|method|size
name|int
name|size
parameter_list|()
block|{
return|return
name|minErrors
operator|.
name|length
operator|*
operator|(
name|w
operator|+
literal|1
operator|)
return|;
block|}
empty_stmt|;
comment|/**      * Returns true if the<code>state</code> in any Levenshtein DFA is an accept state (final state).      */
DECL|method|isAccept
name|boolean
name|isAccept
parameter_list|(
name|int
name|absState
parameter_list|)
block|{
comment|// decode absState -> state, offset
name|int
name|state
init|=
name|absState
operator|/
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
name|int
name|offset
init|=
name|absState
operator|%
operator|(
name|w
operator|+
literal|1
operator|)
decl_stmt|;
assert|assert
name|offset
operator|>=
literal|0
assert|;
return|return
name|w
operator|-
name|offset
operator|+
name|minErrors
index|[
name|state
index|]
operator|<=
name|n
return|;
block|}
comment|/**      * Returns the position in the input word for a given<code>state</code>.      * This is the minimal boundary for the state.      */
DECL|method|getPosition
name|int
name|getPosition
parameter_list|(
name|int
name|absState
parameter_list|)
block|{
return|return
name|absState
operator|%
operator|(
name|w
operator|+
literal|1
operator|)
return|;
block|}
comment|/**      * Returns the state number for a transition from the given<code>state</code>,      * assuming<code>position</code> and characteristic vector<code>vector</code>      */
DECL|method|transition
specifier|abstract
name|int
name|transition
parameter_list|(
name|int
name|state
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|vector
parameter_list|)
function_decl|;
DECL|field|MASKS
specifier|private
specifier|final
specifier|static
name|long
index|[]
name|MASKS
init|=
operator|new
name|long
index|[]
block|{
literal|0x1
block|,
literal|0x3
block|,
literal|0x7
block|,
literal|0xf
block|,
literal|0x1f
block|,
literal|0x3f
block|,
literal|0x7f
block|,
literal|0xff
block|,
literal|0x1ff
block|,
literal|0x3ff
block|,
literal|0x7ff
block|,
literal|0xfff
block|,
literal|0x1fff
block|,
literal|0x3fff
block|,
literal|0x7fff
block|,
literal|0xffff
block|,
literal|0x1ffff
block|,
literal|0x3ffff
block|,
literal|0x7ffff
block|,
literal|0xfffff
block|,
literal|0x1fffff
block|,
literal|0x3fffff
block|,
literal|0x7fffff
block|,
literal|0xffffff
block|,
literal|0x1ffffff
block|,
literal|0x3ffffff
block|,
literal|0x7ffffff
block|,
literal|0xfffffff
block|,
literal|0x1fffffff
block|,
literal|0x3fffffff
block|,
literal|0x7fffffffL
block|,
literal|0xffffffffL
block|,
literal|0x1ffffffffL
block|,
literal|0x3ffffffffL
block|,
literal|0x7ffffffffL
block|,
literal|0xfffffffffL
block|,
literal|0x1fffffffffL
block|,
literal|0x3fffffffffL
block|,
literal|0x7fffffffffL
block|,
literal|0xffffffffffL
block|,
literal|0x1ffffffffffL
block|,
literal|0x3ffffffffffL
block|,
literal|0x7ffffffffffL
block|,
literal|0xfffffffffffL
block|,
literal|0x1fffffffffffL
block|,
literal|0x3fffffffffffL
block|,
literal|0x7fffffffffffL
block|,
literal|0xffffffffffffL
block|,
literal|0x1ffffffffffffL
block|,
literal|0x3ffffffffffffL
block|,
literal|0x7ffffffffffffL
block|,
literal|0xfffffffffffffL
block|,
literal|0x1fffffffffffffL
block|,
literal|0x3fffffffffffffL
block|,
literal|0x7fffffffffffffL
block|,
literal|0xffffffffffffffL
block|,
literal|0x1ffffffffffffffL
block|,
literal|0x3ffffffffffffffL
block|,
literal|0x7ffffffffffffffL
block|,
literal|0xfffffffffffffffL
block|,
literal|0x1fffffffffffffffL
block|,
literal|0x3fffffffffffffffL
block|,
literal|0x7fffffffffffffffL
block|}
decl_stmt|;
DECL|method|unpack
specifier|protected
name|int
name|unpack
parameter_list|(
name|long
index|[]
name|data
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
specifier|final
name|long
name|bitLoc
init|=
name|bitsPerValue
operator|*
name|index
decl_stmt|;
specifier|final
name|int
name|dataLoc
init|=
call|(
name|int
call|)
argument_list|(
name|bitLoc
operator|>>
literal|6
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bitStart
init|=
call|(
name|int
call|)
argument_list|(
name|bitLoc
operator|&
literal|63
argument_list|)
decl_stmt|;
comment|//System.out.println("index=" + index + " dataLoc=" + dataLoc + " bitStart=" + bitStart + " bitsPerV=" + bitsPerValue);
if|if
condition|(
name|bitStart
operator|+
name|bitsPerValue
operator|<=
literal|64
condition|)
block|{
comment|// not split
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|data
index|[
name|dataLoc
index|]
operator|>>
name|bitStart
operator|)
operator|&
name|MASKS
index|[
name|bitsPerValue
operator|-
literal|1
index|]
argument_list|)
return|;
block|}
else|else
block|{
comment|// split
specifier|final
name|int
name|part
init|=
literal|64
operator|-
name|bitStart
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|data
index|[
name|dataLoc
index|]
operator|>>
name|bitStart
operator|)
operator|&
name|MASKS
index|[
name|part
operator|-
literal|1
index|]
operator|)
operator|+
operator|(
operator|(
name|data
index|[
literal|1
operator|+
name|dataLoc
index|]
operator|&
name|MASKS
index|[
name|bitsPerValue
operator|-
name|part
operator|-
literal|1
index|]
operator|)
operator|<<
name|part
operator|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

