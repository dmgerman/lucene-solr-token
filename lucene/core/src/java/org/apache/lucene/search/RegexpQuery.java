begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|Term
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
name|ToStringUtils
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|AutomatonProvider
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
name|automaton
operator|.
name|Operations
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
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_comment
comment|/**  * A fast regular expression query based on the  * {@link org.apache.lucene.util.automaton} package.  *<ul>  *<li>Comparisons are<a  * href="http://tusker.org/regex/regex_benchmark.html">fast</a>  *<li>The term dictionary is enumerated in an intelligent way, to avoid  * comparisons. See {@link AutomatonQuery} for more details.  *</ul>  *<p>  * The supported syntax is documented in the {@link RegExp} class.  * Note this might be different than other regular expression implementations.  * For some alternatives with different syntax, look under the sandbox.  *</p>  *<p>  * Note this query can be slow, as it needs to iterate over many terms. In order  * to prevent extremely slow RegexpQueries, a Regexp term should not start with  * the expression<code>.*</code>  *   * @see RegExp  * @lucene.experimental  */
end_comment

begin_class
DECL|class|RegexpQuery
specifier|public
class|class
name|RegexpQuery
extends|extends
name|AutomatonQuery
block|{
comment|/**    * A provider that provides no named automata    */
DECL|field|defaultProvider
specifier|private
specifier|static
name|AutomatonProvider
name|defaultProvider
init|=
operator|new
name|AutomatonProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Automaton
name|getAutomaton
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Constructs a query for terms matching<code>term</code>.    *<p>    * By default, all regular expression features are enabled.    *</p>    *     * @param term regular expression.    */
DECL|method|RegexpQuery
specifier|public
name|RegexpQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|RegExp
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a query for terms matching<code>term</code>.    *     * @param term regular expression.    * @param flags optional RegExp features from {@link RegExp}    */
DECL|method|RegexpQuery
specifier|public
name|RegexpQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|flags
argument_list|,
name|defaultProvider
argument_list|,
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a query for terms matching<code>term</code>.    *     * @param term regular expression.    * @param flags optional RegExp features from {@link RegExp}    * @param maxDeterminizedStates maximum number of states that compiling the    *  automaton for the regexp can result in.  Set higher to allow more complex    *  queries and lower to prevent memory exhaustion.    */
DECL|method|RegexpQuery
specifier|public
name|RegexpQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|flags
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|flags
argument_list|,
name|defaultProvider
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a query for terms matching<code>term</code>.    *     * @param term regular expression.    * @param flags optional RegExp features from {@link RegExp}    * @param provider custom AutomatonProvider for named automata    * @param maxDeterminizedStates maximum number of states that compiling the    *  automaton for the regexp can result in.  Set higher to allow more complex    *  queries and lower to prevent memory exhaustion.    */
DECL|method|RegexpQuery
specifier|public
name|RegexpQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|flags
parameter_list|,
name|AutomatonProvider
name|provider
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|,
operator|new
name|RegExp
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|flags
argument_list|)
operator|.
name|toAutomaton
argument_list|(
name|provider
argument_list|,
name|maxDeterminizedStates
argument_list|)
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the regexp of this query wrapped in a Term. */
DECL|method|getRegexp
specifier|public
name|Term
name|getRegexp
parameter_list|()
block|{
return|return
name|term
return|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

