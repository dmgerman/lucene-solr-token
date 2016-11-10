begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.fst
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
name|fst
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Comparator
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
name|InMemorySorter
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
name|BytesRefBuilder
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
name|BytesRefIterator
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
name|IntsRefBuilder
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
name|fst
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Finite state automata based implementation of "autocomplete" functionality.  *   *<h2>Implementation details</h2>  *   *<p>  * The construction step in {@link #finalize()} works as follows:  *<ul>  *<li>A set of input terms and their buckets is given.</li>  *<li>All terms in the input are prefixed with a synthetic pseudo-character  * (code) of the weight bucket the term fell into. For example a term  *<code>abc</code> with a discretized weight equal '1' would become  *<code>1abc</code>.</li>  *<li>The terms are then sorted by their raw value of UTF-8 character values  * (including the synthetic bucket code in front).</li>  *<li>A finite state automaton ({@link FST}) is constructed from the input. The  * root node has arcs labeled with all possible weights. We cache all these  * arcs, highest-weight first.</li>  *</ul>  *   *<p>  * At runtime, in {@link FSTCompletion#lookup(CharSequence, int)},   * the automaton is utilized as follows:  *<ul>  *<li>For each possible term weight encoded in the automaton (cached arcs from  * the root above), starting with the highest one, we descend along the path of  * the input key. If the key is not a prefix of a sequence in the automaton  * (path ends prematurely), we exit immediately -- no completions.</li>  *<li>Otherwise, we have found an internal automaton node that ends the key.  *<b>The entire subautomaton (all paths) starting from this node form the key's  * completions.</b> We start the traversal of this subautomaton. Every time we  * reach a final state (arc), we add a single suggestion to the list of results  * (the weight of this suggestion is constant and equal to the root path we  * started from). The tricky part is that because automaton edges are sorted and  * we scan depth-first, we can terminate the entire procedure as soon as we  * collect enough suggestions the user requested.</li>  *<li>In case the number of suggestions collected in the step above is still  * insufficient, we proceed to the next (smaller) weight leaving the root node  * and repeat the same algorithm again.</li>  *</ul>  *   *<h2>Runtime behavior and performance characteristic</h2>  *   * The algorithm described above is optimized for finding suggestions to short  * prefixes in a top-weights-first order. This is probably the most common use  * case: it allows presenting suggestions early and sorts them by the global  * frequency (and then alphabetically).  *   *<p>  * If there is an exact match in the automaton, it is returned first on the  * results list (even with by-weight sorting).  *   *<p>  * Note that the maximum lookup time for<b>any prefix</b> is the time of  * descending to the subtree, plus traversal of the subtree up to the number of  * requested suggestions (because they are already presorted by weight on the  * root level and alphabetically at any node level).  *   *<p>  * To order alphabetically only (no ordering by priorities), use identical term  * weights for all terms. Alphabetical suggestions are returned even if  * non-constant weights are used, but the algorithm for doing this is  * suboptimal.  *   *<p>  * "alphabetically" in any of the documentation above indicates UTF-8  * representation order, nothing else.  *   *<p>  *<b>NOTE</b>: the FST file format is experimental and subject to suddenly  * change, requiring you to rebuild the FST suggest index.  *   * @see FSTCompletion  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FSTCompletionBuilder
specifier|public
class|class
name|FSTCompletionBuilder
block|{
comment|/**     * Default number of buckets.    */
DECL|field|DEFAULT_BUCKETS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BUCKETS
init|=
literal|10
decl_stmt|;
comment|/**    * The number of separate buckets for weights (discretization). The more    * buckets, the more fine-grained term weights (priorities) can be assigned.    * The speed of lookup will not decrease for prefixes which have    * highly-weighted completions (because these are filled-in first), but will    * decrease significantly for low-weighted terms (but these should be    * infrequent, so it is all right).    *     *<p>    * The number of buckets must be within [1, 255] range.    */
DECL|field|buckets
specifier|private
specifier|final
name|int
name|buckets
decl_stmt|;
comment|/**    * Finite state automaton encoding all the lookup terms. See class notes for    * details.    */
DECL|field|automaton
name|FST
argument_list|<
name|Object
argument_list|>
name|automaton
decl_stmt|;
comment|/**    * FST construction require re-sorting the input. This is the class that    * collects all the input entries, their weights and then provides sorted    * order.    */
DECL|field|sorter
specifier|private
specifier|final
name|BytesRefSorter
name|sorter
decl_stmt|;
comment|/**    * Scratch buffer for {@link #add(BytesRef, int)}.    */
DECL|field|scratch
specifier|private
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
comment|/**    * Max tail sharing length.    */
DECL|field|shareMaxTailLength
specifier|private
specifier|final
name|int
name|shareMaxTailLength
decl_stmt|;
comment|/**    * Creates an {@link FSTCompletion} with default options: 10 buckets, exact match    * promoted to first position and {@link InMemorySorter} with a comparator obtained from    * {@link Comparator#naturalOrder()}.    */
DECL|method|FSTCompletionBuilder
specifier|public
name|FSTCompletionBuilder
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_BUCKETS
argument_list|,
operator|new
name|InMemorySorter
argument_list|(
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an FSTCompletion with the specified options.    * @param buckets    *          The number of buckets for weight discretization. Buckets are used    *          in {@link #add(BytesRef, int)} and must be smaller than the number    *          given here.    *              * @param sorter    *          {@link BytesRefSorter} used for re-sorting input for the automaton.    *          For large inputs, use on-disk sorting implementations. The sorter    *          is closed automatically in {@link #build()} if it implements    *          {@link Closeable}.    *              * @param shareMaxTailLength    *          Max shared suffix sharing length.    *              *          See the description of this parameter in {@link Builder}'s constructor.    *          In general, for very large inputs you'll want to construct a non-minimal    *          automaton which will be larger, but the construction will take far less ram.    *          For minimal automata, set it to {@link Integer#MAX_VALUE}.    */
DECL|method|FSTCompletionBuilder
specifier|public
name|FSTCompletionBuilder
parameter_list|(
name|int
name|buckets
parameter_list|,
name|BytesRefSorter
name|sorter
parameter_list|,
name|int
name|shareMaxTailLength
parameter_list|)
block|{
if|if
condition|(
name|buckets
argument_list|<
literal|1
operator|||
name|buckets
argument_list|>
literal|255
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Buckets must be>= 1 and<= 255: "
operator|+
name|buckets
argument_list|)
throw|;
block|}
if|if
condition|(
name|sorter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"BytesRefSorter must not be null."
argument_list|)
throw|;
name|this
operator|.
name|sorter
operator|=
name|sorter
expr_stmt|;
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
name|this
operator|.
name|shareMaxTailLength
operator|=
name|shareMaxTailLength
expr_stmt|;
block|}
comment|/**    * Appends a single suggestion and its weight to the internal buffers.    *     * @param utf8    *          The suggestion (utf8 representation) to be added. The content is    *          copied and the object can be reused.    * @param bucket    *          The bucket to place this suggestion in. Must be non-negative and    *          smaller than the number of buckets passed in the constructor.    *          Higher numbers indicate suggestions that should be presented    *          before suggestions placed in smaller buckets.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|BytesRef
name|utf8
parameter_list|,
name|int
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bucket
operator|<
literal|0
operator|||
name|bucket
operator|>=
name|buckets
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket outside of the allowed range [0, "
operator|+
name|buckets
operator|+
literal|"): "
operator|+
name|bucket
argument_list|)
throw|;
block|}
name|scratch
operator|.
name|grow
argument_list|(
name|utf8
operator|.
name|length
operator|+
literal|10
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|clear
argument_list|()
expr_stmt|;
name|scratch
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|bucket
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|append
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds the final automaton from a list of added entries. This method may    * take a longer while as it needs to build the automaton.    */
DECL|method|build
specifier|public
name|FSTCompletion
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|automaton
operator|=
name|buildAutomaton
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
if|if
condition|(
name|sorter
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|sorter
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|FSTCompletion
argument_list|(
name|automaton
argument_list|)
return|;
block|}
comment|/**    * Builds the final automaton from a list of entries.    */
DECL|method|buildAutomaton
specifier|private
name|FST
argument_list|<
name|Object
argument_list|>
name|buildAutomaton
parameter_list|(
name|BytesRefSorter
name|sorter
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Build the automaton.
specifier|final
name|Outputs
argument_list|<
name|Object
argument_list|>
name|outputs
init|=
name|NoOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|empty
init|=
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|Object
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|shareMaxTailLength
argument_list|,
name|outputs
argument_list|,
literal|true
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|BytesRef
name|entry
decl_stmt|;
specifier|final
name|IntsRefBuilder
name|scratchIntsRef
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|BytesRefIterator
name|iter
init|=
name|sorter
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|entry
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
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|scratch
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|entry
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|entry
argument_list|,
name|scratchIntsRef
argument_list|)
argument_list|,
name|empty
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|copyBytes
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|count
operator|==
literal|0
condition|?
literal|null
else|:
name|builder
operator|.
name|finish
argument_list|()
return|;
block|}
block|}
end_class

end_unit

