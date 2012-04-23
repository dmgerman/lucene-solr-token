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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * Builds a minimal deterministic automaton that accepts a set of strings. The  * algorithm requires sorted input data, but is very fast (nearly linear with  * the input size).  */
end_comment

begin_class
DECL|class|DaciukMihovAutomatonBuilder
specifier|public
specifier|final
class|class
name|DaciukMihovAutomatonBuilder
block|{
comment|/**    * DFSA state with<code>char</code> labels on transitions.    */
DECL|class|State
specifier|public
specifier|final
specifier|static
class|class
name|State
block|{
comment|/** An empty set of labels. */
DECL|field|NO_LABELS
specifier|private
specifier|final
specifier|static
name|int
index|[]
name|NO_LABELS
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
comment|/** An empty set of states. */
DECL|field|NO_STATES
specifier|private
specifier|final
specifier|static
name|State
index|[]
name|NO_STATES
init|=
operator|new
name|State
index|[
literal|0
index|]
decl_stmt|;
comment|/**      * Labels of outgoing transitions. Indexed identically to {@link #states}.      * Labels must be sorted lexicographically.      */
DECL|field|labels
name|int
index|[]
name|labels
init|=
name|NO_LABELS
decl_stmt|;
comment|/**      * States reachable from outgoing transitions. Indexed identically to      * {@link #labels}.      */
DECL|field|states
name|State
index|[]
name|states
init|=
name|NO_STATES
decl_stmt|;
comment|/**      *<code>true</code> if this state corresponds to the end of at least one      * input sequence.      */
DECL|field|is_final
name|boolean
name|is_final
decl_stmt|;
comment|/**      * Returns the target state of a transition leaving this state and labeled      * with<code>label</code>. If no such transition exists, returns      *<code>null</code>.      */
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|(
name|int
name|label
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|labels
argument_list|,
name|label
argument_list|)
decl_stmt|;
return|return
name|index
operator|>=
literal|0
condition|?
name|states
index|[
name|index
index|]
else|:
literal|null
return|;
block|}
comment|/**      * Returns an array of outgoing transition labels. The array is sorted in      * lexicographic order and indexes correspond to states returned from      * {@link #getStates()}.      */
DECL|method|getTransitionLabels
specifier|public
name|int
index|[]
name|getTransitionLabels
parameter_list|()
block|{
return|return
name|this
operator|.
name|labels
return|;
block|}
comment|/**      * Returns an array of outgoing transitions from this state. The returned      * array must not be changed.      */
DECL|method|getStates
specifier|public
name|State
index|[]
name|getStates
parameter_list|()
block|{
return|return
name|this
operator|.
name|states
return|;
block|}
comment|/**      * Two states are equal if:      *<ul>      *<li>they have an identical number of outgoing transitions, labeled with      * the same labels</li>      *<li>corresponding outgoing transitions lead to the same states (to states      * with an identical right-language).      *</ul>      */
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
specifier|final
name|State
name|other
init|=
operator|(
name|State
operator|)
name|obj
decl_stmt|;
return|return
name|is_final
operator|==
name|other
operator|.
name|is_final
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|labels
argument_list|,
name|other
operator|.
name|labels
argument_list|)
operator|&&
name|referenceEquals
argument_list|(
name|this
operator|.
name|states
argument_list|,
name|other
operator|.
name|states
argument_list|)
return|;
block|}
comment|/**      * Return<code>true</code> if this state has any children (outgoing      * transitions).      */
DECL|method|hasChildren
specifier|public
name|boolean
name|hasChildren
parameter_list|()
block|{
return|return
name|labels
operator|.
name|length
operator|>
literal|0
return|;
block|}
comment|/**      * Is this state a final state in the automaton?      */
DECL|method|isFinal
specifier|public
name|boolean
name|isFinal
parameter_list|()
block|{
return|return
name|is_final
return|;
block|}
comment|/**      * Compute the hash code of the<i>current</i> status of this state.      */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|is_final
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|hash
operator|^=
name|hash
operator|*
literal|31
operator|+
name|this
operator|.
name|labels
operator|.
name|length
expr_stmt|;
for|for
control|(
name|int
name|c
range|:
name|this
operator|.
name|labels
control|)
name|hash
operator|^=
name|hash
operator|*
literal|31
operator|+
name|c
expr_stmt|;
comment|/*        * Compare the right-language of this state using reference-identity of        * outgoing states. This is possible because states are interned (stored        * in registry) and traversed in post-order, so any outgoing transitions        * are already interned.        */
for|for
control|(
name|State
name|s
range|:
name|this
operator|.
name|states
control|)
block|{
name|hash
operator|^=
name|System
operator|.
name|identityHashCode
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/**      * Create a new outgoing transition labeled<code>label</code> and return      * the newly created target state for this transition.      */
DECL|method|newState
name|State
name|newState
parameter_list|(
name|int
name|label
parameter_list|)
block|{
assert|assert
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|labels
argument_list|,
name|label
argument_list|)
operator|<
literal|0
operator|:
literal|"State already has transition labeled: "
operator|+
name|label
assert|;
name|labels
operator|=
name|copyOf
argument_list|(
name|labels
argument_list|,
name|labels
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|states
operator|=
name|copyOf
argument_list|(
name|states
argument_list|,
name|states
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|labels
index|[
name|labels
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|label
expr_stmt|;
return|return
name|states
index|[
name|states
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|new
name|State
argument_list|()
return|;
block|}
comment|/**      * Return the most recent transitions's target state.      */
DECL|method|lastChild
name|State
name|lastChild
parameter_list|()
block|{
assert|assert
name|hasChildren
argument_list|()
operator|:
literal|"No outgoing transitions."
assert|;
return|return
name|states
index|[
name|states
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
comment|/**      * Return the associated state if the most recent transition is labeled with      *<code>label</code>.      */
DECL|method|lastChild
name|State
name|lastChild
parameter_list|(
name|int
name|label
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|labels
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|State
name|s
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
name|labels
index|[
name|index
index|]
operator|==
name|label
condition|)
block|{
name|s
operator|=
name|states
index|[
name|index
index|]
expr_stmt|;
block|}
assert|assert
name|s
operator|==
name|getState
argument_list|(
name|label
argument_list|)
assert|;
return|return
name|s
return|;
block|}
comment|/**      * Replace the last added outgoing transition's target state with the given      * state.      */
DECL|method|replaceLastChild
name|void
name|replaceLastChild
parameter_list|(
name|State
name|state
parameter_list|)
block|{
assert|assert
name|hasChildren
argument_list|()
operator|:
literal|"No outgoing transitions."
assert|;
name|states
index|[
name|states
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|state
expr_stmt|;
block|}
comment|/**      * JDK1.5-replacement of {@link Arrays#copyOf(int[], int)}      */
DECL|method|copyOf
specifier|private
specifier|static
name|int
index|[]
name|copyOf
parameter_list|(
name|int
index|[]
name|original
parameter_list|,
name|int
name|newLength
parameter_list|)
block|{
name|int
index|[]
name|copy
init|=
operator|new
name|int
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|original
argument_list|,
literal|0
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|original
operator|.
name|length
argument_list|,
name|newLength
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/**      * JDK1.5-replacement of {@link Arrays#copyOf(char[], int)}      */
DECL|method|copyOf
specifier|public
specifier|static
name|State
index|[]
name|copyOf
parameter_list|(
name|State
index|[]
name|original
parameter_list|,
name|int
name|newLength
parameter_list|)
block|{
name|State
index|[]
name|copy
init|=
operator|new
name|State
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|original
argument_list|,
literal|0
argument_list|,
name|copy
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|original
operator|.
name|length
argument_list|,
name|newLength
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/**      * Compare two lists of objects for reference-equality.      */
DECL|method|referenceEquals
specifier|private
specifier|static
name|boolean
name|referenceEquals
parameter_list|(
name|Object
index|[]
name|a1
parameter_list|,
name|Object
index|[]
name|a2
parameter_list|)
block|{
if|if
condition|(
name|a1
operator|.
name|length
operator|!=
name|a2
operator|.
name|length
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|a1
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|a1
index|[
name|i
index|]
operator|!=
name|a2
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * "register" for state interning.    */
DECL|field|register
specifier|private
name|HashMap
argument_list|<
name|State
argument_list|,
name|State
argument_list|>
name|register
init|=
operator|new
name|HashMap
argument_list|<
name|State
argument_list|,
name|State
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Root automaton state.    */
DECL|field|root
specifier|private
name|State
name|root
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
comment|/**    * Previous sequence added to the automaton in {@link #add(CharSequence)}.    */
DECL|field|previous
specifier|private
name|CharsRef
name|previous
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|CharsRef
argument_list|>
name|comparator
init|=
name|CharsRef
operator|.
name|getUTF16SortedAsUTF8Comparator
argument_list|()
decl_stmt|;
comment|/**    * Add another character sequence to this automaton. The sequence must be    * lexicographically larger or equal compared to any previous sequences added    * to this automaton (the input must be sorted).    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|CharsRef
name|current
parameter_list|)
block|{
assert|assert
name|register
operator|!=
literal|null
operator|:
literal|"Automaton already built."
assert|;
assert|assert
name|previous
operator|==
literal|null
operator|||
name|comparator
operator|.
name|compare
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
operator|<=
literal|0
operator|:
literal|"Input must be sorted: "
operator|+
name|previous
operator|+
literal|">= "
operator|+
name|current
assert|;
assert|assert
name|setPrevious
argument_list|(
name|current
argument_list|)
assert|;
comment|// Descend in the automaton (find matching prefix).
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|max
init|=
name|current
operator|.
name|length
argument_list|()
decl_stmt|;
name|State
name|next
decl_stmt|,
name|state
init|=
name|root
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|max
operator|&&
operator|(
name|next
operator|=
name|state
operator|.
name|lastChild
argument_list|(
name|Character
operator|.
name|codePointAt
argument_list|(
name|current
argument_list|,
name|pos
argument_list|)
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|state
operator|=
name|next
expr_stmt|;
comment|// todo, optimize me
name|pos
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|Character
operator|.
name|codePointAt
argument_list|(
name|current
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|hasChildren
argument_list|()
condition|)
name|replaceOrRegister
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|addSuffix
argument_list|(
name|state
argument_list|,
name|current
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finalize the automaton and return the root state. No more strings can be    * added to the builder after this call.    *     * @return Root automaton state.    */
DECL|method|complete
specifier|public
name|State
name|complete
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|register
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
if|if
condition|(
name|root
operator|.
name|hasChildren
argument_list|()
condition|)
name|replaceOrRegister
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|register
operator|=
literal|null
expr_stmt|;
return|return
name|root
return|;
block|}
comment|/**    * Internal recursive traversal for conversion.    */
DECL|method|convert
specifier|private
specifier|static
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
name|State
name|convert
parameter_list|(
name|State
name|s
parameter_list|,
name|IdentityHashMap
argument_list|<
name|State
argument_list|,
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
name|State
argument_list|>
name|visited
parameter_list|)
block|{
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
name|State
name|converted
init|=
name|visited
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|converted
operator|!=
literal|null
condition|)
return|return
name|converted
return|;
name|converted
operator|=
operator|new
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
name|State
argument_list|()
expr_stmt|;
name|converted
operator|.
name|setAccept
argument_list|(
name|s
operator|.
name|is_final
argument_list|)
expr_stmt|;
name|visited
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|converted
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
index|[]
name|labels
init|=
name|s
operator|.
name|labels
decl_stmt|;
for|for
control|(
name|DaciukMihovAutomatonBuilder
operator|.
name|State
name|target
range|:
name|s
operator|.
name|states
control|)
block|{
name|converted
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|labels
index|[
name|i
operator|++
index|]
argument_list|,
name|convert
argument_list|(
name|target
argument_list|,
name|visited
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|converted
return|;
block|}
comment|/**    * Build a minimal, deterministic automaton from a sorted list of strings.    */
DECL|method|build
specifier|public
specifier|static
name|Automaton
name|build
parameter_list|(
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|input
parameter_list|)
block|{
specifier|final
name|DaciukMihovAutomatonBuilder
name|builder
init|=
operator|new
name|DaciukMihovAutomatonBuilder
argument_list|()
decl_stmt|;
name|CharsRef
name|scratch
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|b
range|:
name|input
control|)
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|b
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|a
operator|.
name|initial
operator|=
name|convert
argument_list|(
name|builder
operator|.
name|complete
argument_list|()
argument_list|,
operator|new
name|IdentityHashMap
argument_list|<
name|State
argument_list|,
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
name|State
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Copy<code>current</code> into an internal buffer.    */
DECL|method|setPrevious
specifier|private
name|boolean
name|setPrevious
parameter_list|(
name|CharsRef
name|current
parameter_list|)
block|{
comment|// don't need to copy, once we fix https://issues.apache.org/jira/browse/LUCENE-3277
comment|// still, called only from assert
name|previous
operator|=
name|CharsRef
operator|.
name|deepCopyOf
argument_list|(
name|current
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Replace last child of<code>state</code> with an already registered state    * or register the last child state.    */
DECL|method|replaceOrRegister
specifier|private
name|void
name|replaceOrRegister
parameter_list|(
name|State
name|state
parameter_list|)
block|{
specifier|final
name|State
name|child
init|=
name|state
operator|.
name|lastChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|hasChildren
argument_list|()
condition|)
name|replaceOrRegister
argument_list|(
name|child
argument_list|)
expr_stmt|;
specifier|final
name|State
name|registered
init|=
name|register
operator|.
name|get
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|registered
operator|!=
literal|null
condition|)
block|{
name|state
operator|.
name|replaceLastChild
argument_list|(
name|registered
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|register
operator|.
name|put
argument_list|(
name|child
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add a suffix of<code>current</code> starting at<code>fromIndex</code>    * (inclusive) to state<code>state</code>.    */
DECL|method|addSuffix
specifier|private
name|void
name|addSuffix
parameter_list|(
name|State
name|state
parameter_list|,
name|CharSequence
name|current
parameter_list|,
name|int
name|fromIndex
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|current
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|fromIndex
operator|<
name|len
condition|)
block|{
name|int
name|cp
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|current
argument_list|,
name|fromIndex
argument_list|)
decl_stmt|;
name|state
operator|=
name|state
operator|.
name|newState
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|fromIndex
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
name|state
operator|.
name|is_final
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

