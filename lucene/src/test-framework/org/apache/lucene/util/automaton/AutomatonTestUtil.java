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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|UnicodeUtil
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|AutomatonTestUtil
specifier|public
class|class
name|AutomatonTestUtil
block|{
comment|/** Returns random string, including full unicode range. */
DECL|method|randomRegexp
specifier|public
specifier|static
name|String
name|randomRegexp
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|regexp
init|=
name|randomRegexpString
argument_list|(
name|r
argument_list|)
decl_stmt|;
comment|// we will also generate some undefined unicode queries
if|if
condition|(
operator|!
name|UnicodeUtil
operator|.
name|validUTF16String
argument_list|(
name|regexp
argument_list|)
condition|)
continue|continue;
try|try
block|{
operator|new
name|RegExp
argument_list|(
name|regexp
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
expr_stmt|;
return|return
name|regexp
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
block|}
block|}
DECL|method|randomRegexpString
specifier|private
specifier|static
name|String
name|randomRegexpString
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|==
literal|0
condition|)
block|{
comment|// allow 0 length
return|return
literal|""
return|;
block|}
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|end
index|]
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
name|int
name|t
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|t
operator|&&
name|i
operator|<
name|end
operator|-
literal|1
condition|)
block|{
comment|// Make a surrogate pair
comment|// High surrogate
name|buffer
index|[
name|i
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0xd800
argument_list|,
literal|0xdbff
argument_list|)
expr_stmt|;
comment|// Low surrogate
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0xdc00
argument_list|,
literal|0xdfff
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|<=
literal|1
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|r
operator|.
name|nextInt
argument_list|(
literal|0x80
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|2
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0x80
argument_list|,
literal|0x800
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|3
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0x800
argument_list|,
literal|0xd7ff
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|4
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|0xe000
argument_list|,
literal|0xffff
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|5
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'.'
expr_stmt|;
elseif|else
if|if
condition|(
literal|6
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'?'
expr_stmt|;
elseif|else
if|if
condition|(
literal|7
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'*'
expr_stmt|;
elseif|else
if|if
condition|(
literal|8
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'+'
expr_stmt|;
elseif|else
if|if
condition|(
literal|9
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'('
expr_stmt|;
elseif|else
if|if
condition|(
literal|10
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|')'
expr_stmt|;
elseif|else
if|if
condition|(
literal|11
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'-'
expr_stmt|;
elseif|else
if|if
condition|(
literal|12
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'['
expr_stmt|;
elseif|else
if|if
condition|(
literal|13
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|']'
expr_stmt|;
elseif|else
if|if
condition|(
literal|14
operator|==
name|t
condition|)
name|buffer
index|[
name|i
index|]
operator|=
literal|'|'
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|end
argument_list|)
return|;
block|}
comment|/** picks a random int code point, avoiding surrogates;    * throws IllegalArgumentException if this transition only    * accepts surrogates */
DECL|method|getRandomCodePoint
specifier|private
specifier|static
name|int
name|getRandomCodePoint
parameter_list|(
specifier|final
name|Random
name|r
parameter_list|,
specifier|final
name|Transition
name|t
parameter_list|)
block|{
specifier|final
name|int
name|code
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|max
argument_list|<
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|||
name|t
operator|.
name|min
argument_list|>
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_END
condition|)
block|{
comment|// easy: entire range is before or after surrogates
name|code
operator|=
name|t
operator|.
name|min
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|t
operator|.
name|max
operator|-
name|t
operator|.
name|min
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|min
operator|>=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
condition|)
block|{
if|if
condition|(
name|t
operator|.
name|max
operator|>
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
condition|)
block|{
comment|// after surrogates
name|code
operator|=
literal|1
operator|+
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|t
operator|.
name|max
operator|-
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"transition accepts only surrogates: "
operator|+
name|t
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|max
operator|<=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
condition|)
block|{
if|if
condition|(
name|t
operator|.
name|min
operator|<
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
condition|)
block|{
comment|// before surrogates
name|code
operator|=
name|t
operator|.
name|min
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|-
name|t
operator|.
name|min
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"transition accepts only surrogates: "
operator|+
name|t
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// range includes all surrogates
name|int
name|gap1
init|=
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|-
name|t
operator|.
name|min
decl_stmt|;
name|int
name|gap2
init|=
name|t
operator|.
name|max
operator|-
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
decl_stmt|;
name|int
name|c
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|gap1
operator|+
name|gap2
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
name|gap1
condition|)
block|{
name|code
operator|=
name|t
operator|.
name|min
operator|+
name|c
expr_stmt|;
block|}
else|else
block|{
name|code
operator|=
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|+
name|c
operator|-
name|gap1
operator|+
literal|1
expr_stmt|;
block|}
block|}
assert|assert
name|code
operator|>=
name|t
operator|.
name|min
operator|&&
name|code
operator|<=
name|t
operator|.
name|max
operator|&&
operator|(
name|code
argument_list|<
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
operator|||
name|code
argument_list|>
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_END
operator|)
operator|:
literal|"code="
operator|+
name|code
operator|+
literal|" min="
operator|+
name|t
operator|.
name|min
operator|+
literal|" max="
operator|+
name|t
operator|.
name|max
assert|;
return|return
name|code
return|;
block|}
DECL|class|RandomAcceptedStrings
specifier|public
specifier|static
class|class
name|RandomAcceptedStrings
block|{
DECL|field|leadsToAccept
specifier|private
specifier|final
name|Map
argument_list|<
name|Transition
argument_list|,
name|Boolean
argument_list|>
name|leadsToAccept
decl_stmt|;
DECL|field|a
specifier|private
specifier|final
name|Automaton
name|a
decl_stmt|;
DECL|class|ArrivingTransition
specifier|private
specifier|static
class|class
name|ArrivingTransition
block|{
DECL|field|from
specifier|final
name|State
name|from
decl_stmt|;
DECL|field|t
specifier|final
name|Transition
name|t
decl_stmt|;
DECL|method|ArrivingTransition
specifier|public
name|ArrivingTransition
parameter_list|(
name|State
name|from
parameter_list|,
name|Transition
name|t
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|t
operator|=
name|t
expr_stmt|;
block|}
block|}
DECL|method|RandomAcceptedStrings
specifier|public
name|RandomAcceptedStrings
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
block|{
name|leadsToAccept
operator|=
literal|null
expr_stmt|;
return|return;
block|}
comment|// must use IdentityHashmap because two Transitions w/
comment|// different start nodes can be considered the same
name|leadsToAccept
operator|=
operator|new
name|IdentityHashMap
argument_list|<
name|Transition
argument_list|,
name|Boolean
argument_list|>
argument_list|()
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|State
argument_list|,
name|List
argument_list|<
name|ArrivingTransition
argument_list|>
argument_list|>
name|allArriving
init|=
operator|new
name|HashMap
argument_list|<
name|State
argument_list|,
name|List
argument_list|<
name|ArrivingTransition
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|LinkedList
argument_list|<
name|State
argument_list|>
name|q
init|=
operator|new
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|State
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
comment|// reverse map the transitions, so we can quickly look
comment|// up all arriving transitions to a given state
for|for
control|(
name|State
name|s
range|:
name|a
operator|.
name|getNumberedStates
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|numTransitions
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Transition
name|t
init|=
name|s
operator|.
name|transitionsArray
index|[
name|i
index|]
decl_stmt|;
name|List
argument_list|<
name|ArrivingTransition
argument_list|>
name|tl
init|=
name|allArriving
operator|.
name|get
argument_list|(
name|t
operator|.
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
name|tl
operator|==
literal|null
condition|)
block|{
name|tl
operator|=
operator|new
name|ArrayList
argument_list|<
name|ArrivingTransition
argument_list|>
argument_list|()
expr_stmt|;
name|allArriving
operator|.
name|put
argument_list|(
name|t
operator|.
name|to
argument_list|,
name|tl
argument_list|)
expr_stmt|;
block|}
name|tl
operator|.
name|add
argument_list|(
operator|new
name|ArrivingTransition
argument_list|(
name|s
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|.
name|accept
condition|)
block|{
name|q
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|seen
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Breadth-first search, from accept states,
comment|// backwards:
while|while
condition|(
operator|!
name|q
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|State
name|s
init|=
name|q
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ArrivingTransition
argument_list|>
name|arriving
init|=
name|allArriving
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|arriving
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ArrivingTransition
name|at
range|:
name|arriving
control|)
block|{
specifier|final
name|State
name|from
init|=
name|at
operator|.
name|from
decl_stmt|;
if|if
condition|(
operator|!
name|seen
operator|.
name|contains
argument_list|(
name|from
argument_list|)
condition|)
block|{
name|q
operator|.
name|add
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|seen
operator|.
name|add
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|leadsToAccept
operator|.
name|put
argument_list|(
name|at
operator|.
name|t
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|getRandomAcceptedString
specifier|public
name|int
index|[]
name|getRandomAcceptedString
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|soFar
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
block|{
comment|// accepts only one
specifier|final
name|String
name|s
init|=
name|a
operator|.
name|singleton
decl_stmt|;
name|int
name|charUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|charUpto
operator|<
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
specifier|final
name|int
name|cp
init|=
name|s
operator|.
name|codePointAt
argument_list|(
name|charUpto
argument_list|)
decl_stmt|;
name|charUpto
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|soFar
operator|.
name|add
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|State
name|s
init|=
name|a
operator|.
name|initial
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|accept
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|numTransitions
operator|==
literal|0
condition|)
block|{
comment|// stop now
break|break;
block|}
else|else
block|{
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|s
operator|.
name|numTransitions
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this automaton has dead states"
argument_list|)
throw|;
block|}
name|boolean
name|cheat
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Transition
name|t
decl_stmt|;
if|if
condition|(
name|cheat
condition|)
block|{
comment|// pick a transition that we know is the fastest
comment|// path to an accept state
name|List
argument_list|<
name|Transition
argument_list|>
name|toAccept
init|=
operator|new
name|ArrayList
argument_list|<
name|Transition
argument_list|>
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
name|s
operator|.
name|numTransitions
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Transition
name|t0
init|=
name|s
operator|.
name|transitionsArray
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|leadsToAccept
operator|.
name|containsKey
argument_list|(
name|t0
argument_list|)
condition|)
block|{
name|toAccept
operator|.
name|add
argument_list|(
name|t0
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|toAccept
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// this is OK -- it means we jumped into a cycle
name|t
operator|=
name|s
operator|.
name|transitionsArray
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|s
operator|.
name|numTransitions
argument_list|)
index|]
expr_stmt|;
block|}
else|else
block|{
name|t
operator|=
name|toAccept
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|toAccept
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|t
operator|=
name|s
operator|.
name|transitionsArray
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|s
operator|.
name|numTransitions
argument_list|)
index|]
expr_stmt|;
block|}
name|soFar
operator|.
name|add
argument_list|(
name|getRandomCodePoint
argument_list|(
name|r
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|=
name|t
operator|.
name|to
expr_stmt|;
block|}
block|}
return|return
name|ArrayUtil
operator|.
name|toIntArray
argument_list|(
name|soFar
argument_list|)
return|;
block|}
block|}
comment|/** return a random NFA/DFA for testing */
DECL|method|randomAutomaton
specifier|public
specifier|static
name|Automaton
name|randomAutomaton
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
comment|// get two random Automata from regexps
name|Automaton
name|a1
init|=
operator|new
name|RegExp
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
name|a1
operator|=
name|BasicOperations
operator|.
name|complement
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Automaton
name|a2
init|=
operator|new
name|RegExp
argument_list|(
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
name|a2
operator|=
name|BasicOperations
operator|.
name|complement
argument_list|(
name|a2
argument_list|)
expr_stmt|;
comment|// combine them in random ways
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|BasicOperations
operator|.
name|union
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|BasicOperations
operator|.
name|intersection
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
return|;
default|default:
return|return
name|BasicOperations
operator|.
name|minus
argument_list|(
name|a1
argument_list|,
name|a2
argument_list|)
return|;
block|}
block|}
comment|/**     * below are original, unoptimized implementations of DFA operations for testing.    * These are from brics automaton, full license (BSD) below:    */
comment|/*    * dk.brics.automaton    *     * Copyright (c) 2001-2009 Anders Moeller    * All rights reserved.    *     * Redistribution and use in source and binary forms, with or without    * modification, are permitted provided that the following conditions    * are met:    * 1. Redistributions of source code must retain the above copyright    *    notice, this list of conditions and the following disclaimer.    * 2. Redistributions in binary form must reproduce the above copyright    *    notice, this list of conditions and the following disclaimer in the    *    documentation and/or other materials provided with the distribution.    * 3. The name of the author may not be used to endorse or promote products    *    derived from this software without specific prior written permission.    *     * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR    * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES    * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,    * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT    * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,    * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY    * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT    * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF    * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.    */
comment|/**    * Simple, original brics implementation of Brzozowski minimize()    */
DECL|method|minimizeSimple
specifier|public
specifier|static
name|void
name|minimizeSimple
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
return|return;
name|determinizeSimple
argument_list|(
name|a
argument_list|,
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|determinizeSimple
argument_list|(
name|a
argument_list|,
name|SpecialOperations
operator|.
name|reverse
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple, original brics implementation of determinize()    */
DECL|method|determinizeSimple
specifier|public
specifier|static
name|void
name|determinizeSimple
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|deterministic
operator|||
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
return|return;
name|Set
argument_list|<
name|State
argument_list|>
name|initialset
init|=
operator|new
name|HashSet
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
name|initialset
operator|.
name|add
argument_list|(
name|a
operator|.
name|initial
argument_list|)
expr_stmt|;
name|determinizeSimple
argument_list|(
name|a
argument_list|,
name|initialset
argument_list|)
expr_stmt|;
block|}
comment|/**     * Simple, original brics implementation of determinize()    * Determinizes the given automaton using the given set of initial states.     */
DECL|method|determinizeSimple
specifier|public
specifier|static
name|void
name|determinizeSimple
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|Set
argument_list|<
name|State
argument_list|>
name|initialset
parameter_list|)
block|{
name|int
index|[]
name|points
init|=
name|a
operator|.
name|getStartPoints
argument_list|()
decl_stmt|;
comment|// subset construction
name|Map
argument_list|<
name|Set
argument_list|<
name|State
argument_list|>
argument_list|,
name|Set
argument_list|<
name|State
argument_list|>
argument_list|>
name|sets
init|=
operator|new
name|HashMap
argument_list|<
name|Set
argument_list|<
name|State
argument_list|>
argument_list|,
name|Set
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|Set
argument_list|<
name|State
argument_list|>
argument_list|>
name|worklist
init|=
operator|new
name|LinkedList
argument_list|<
name|Set
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Set
argument_list|<
name|State
argument_list|>
argument_list|,
name|State
argument_list|>
name|newstate
init|=
operator|new
name|HashMap
argument_list|<
name|Set
argument_list|<
name|State
argument_list|>
argument_list|,
name|State
argument_list|>
argument_list|()
decl_stmt|;
name|sets
operator|.
name|put
argument_list|(
name|initialset
argument_list|,
name|initialset
argument_list|)
expr_stmt|;
name|worklist
operator|.
name|add
argument_list|(
name|initialset
argument_list|)
expr_stmt|;
name|a
operator|.
name|initial
operator|=
operator|new
name|State
argument_list|()
expr_stmt|;
name|newstate
operator|.
name|put
argument_list|(
name|initialset
argument_list|,
name|a
operator|.
name|initial
argument_list|)
expr_stmt|;
while|while
condition|(
name|worklist
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Set
argument_list|<
name|State
argument_list|>
name|s
init|=
name|worklist
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|State
name|r
init|=
name|newstate
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
for|for
control|(
name|State
name|q
range|:
name|s
control|)
if|if
condition|(
name|q
operator|.
name|accept
condition|)
block|{
name|r
operator|.
name|accept
operator|=
literal|true
expr_stmt|;
break|break;
block|}
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|points
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|Set
argument_list|<
name|State
argument_list|>
name|p
init|=
operator|new
name|HashSet
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|State
name|q
range|:
name|s
control|)
for|for
control|(
name|Transition
name|t
range|:
name|q
operator|.
name|getTransitions
argument_list|()
control|)
if|if
condition|(
name|t
operator|.
name|min
operator|<=
name|points
index|[
name|n
index|]
operator|&&
name|points
index|[
name|n
index|]
operator|<=
name|t
operator|.
name|max
condition|)
name|p
operator|.
name|add
argument_list|(
name|t
operator|.
name|to
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|sets
operator|.
name|containsKey
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|sets
operator|.
name|put
argument_list|(
name|p
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|worklist
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|newstate
operator|.
name|put
argument_list|(
name|p
argument_list|,
operator|new
name|State
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|State
name|q
init|=
name|newstate
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|int
name|min
init|=
name|points
index|[
name|n
index|]
decl_stmt|;
name|int
name|max
decl_stmt|;
if|if
condition|(
name|n
operator|+
literal|1
operator|<
name|points
operator|.
name|length
condition|)
name|max
operator|=
name|points
index|[
name|n
operator|+
literal|1
index|]
operator|-
literal|1
expr_stmt|;
else|else
name|max
operator|=
name|Character
operator|.
name|MAX_CODE_POINT
expr_stmt|;
name|r
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|min
argument_list|,
name|max
argument_list|,
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
name|a
operator|.
name|clearNumberedStates
argument_list|()
expr_stmt|;
name|a
operator|.
name|removeDeadTransitions
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns true if the language of this automaton is finite.    *<p>    * WARNING: this method is slow, it will blow up if the automaton is large.    * this is only used to test the correctness of our faster implementation.    */
DECL|method|isFiniteSlow
specifier|public
specifier|static
name|boolean
name|isFiniteSlow
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
return|return
literal|true
return|;
return|return
name|isFiniteSlow
argument_list|(
name|a
operator|.
name|initial
argument_list|,
operator|new
name|HashSet
argument_list|<
name|State
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Checks whether there is a loop containing s. (This is sufficient since    * there are never transitions to dead states.)    */
comment|// TODO: not great that this is recursive... in theory a
comment|// large automata could exceed java's stack
DECL|method|isFiniteSlow
specifier|private
specifier|static
name|boolean
name|isFiniteSlow
parameter_list|(
name|State
name|s
parameter_list|,
name|HashSet
argument_list|<
name|State
argument_list|>
name|path
parameter_list|)
block|{
name|path
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
for|for
control|(
name|Transition
name|t
range|:
name|s
operator|.
name|getTransitions
argument_list|()
control|)
if|if
condition|(
name|path
operator|.
name|contains
argument_list|(
name|t
operator|.
name|to
argument_list|)
operator|||
operator|!
name|isFiniteSlow
argument_list|(
name|t
operator|.
name|to
argument_list|,
name|path
argument_list|)
condition|)
return|return
literal|false
return|;
name|path
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

