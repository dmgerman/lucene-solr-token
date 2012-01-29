begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.viterbi
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|viterbi
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|Segmenter
operator|.
name|Mode
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
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
operator|.
name|CharacterDefinition
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
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
operator|.
name|ConnectionCosts
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
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
operator|.
name|TokenInfoDictionary
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
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
operator|.
name|TokenInfoFST
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
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
operator|.
name|UnknownDictionary
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
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
operator|.
name|UserDictionary
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
name|analysis
operator|.
name|kuromoji
operator|.
name|viterbi
operator|.
name|ViterbiNode
operator|.
name|Type
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
name|FST
import|;
end_import

begin_class
DECL|class|Viterbi
specifier|public
class|class
name|Viterbi
block|{
DECL|field|fst
specifier|private
specifier|final
name|TokenInfoFST
name|fst
decl_stmt|;
DECL|field|dictionary
specifier|private
specifier|final
name|TokenInfoDictionary
name|dictionary
decl_stmt|;
DECL|field|unkDictionary
specifier|private
specifier|final
name|UnknownDictionary
name|unkDictionary
decl_stmt|;
DECL|field|costs
specifier|private
specifier|final
name|ConnectionCosts
name|costs
decl_stmt|;
DECL|field|userDictionary
specifier|private
specifier|final
name|UserDictionary
name|userDictionary
decl_stmt|;
DECL|field|characterDefinition
specifier|private
specifier|final
name|CharacterDefinition
name|characterDefinition
decl_stmt|;
DECL|field|useUserDictionary
specifier|private
specifier|final
name|boolean
name|useUserDictionary
decl_stmt|;
DECL|field|searchMode
specifier|private
specifier|final
name|boolean
name|searchMode
decl_stmt|;
DECL|field|extendedMode
specifier|private
specifier|final
name|boolean
name|extendedMode
decl_stmt|;
DECL|field|DEFAULT_COST
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_COST
init|=
literal|10000000
decl_stmt|;
DECL|field|SEARCH_MODE_LENGTH_KANJI
specifier|private
specifier|static
specifier|final
name|int
name|SEARCH_MODE_LENGTH_KANJI
init|=
literal|3
decl_stmt|;
DECL|field|SEARCH_MODE_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|SEARCH_MODE_LENGTH
init|=
literal|7
decl_stmt|;
DECL|field|SEARCH_MODE_PENALTY
specifier|private
specifier|static
specifier|final
name|int
name|SEARCH_MODE_PENALTY
init|=
literal|10000
decl_stmt|;
DECL|field|BOS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|BOS
init|=
literal|"BOS"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|EOS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|EOS
init|=
literal|"EOS"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|/**    * Constructor    */
DECL|method|Viterbi
specifier|public
name|Viterbi
parameter_list|(
name|TokenInfoDictionary
name|dictionary
parameter_list|,
name|UnknownDictionary
name|unkDictionary
parameter_list|,
name|ConnectionCosts
name|costs
parameter_list|,
name|UserDictionary
name|userDictionary
parameter_list|,
name|Mode
name|mode
parameter_list|)
block|{
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
name|this
operator|.
name|fst
operator|=
name|dictionary
operator|.
name|getFST
argument_list|()
expr_stmt|;
name|this
operator|.
name|unkDictionary
operator|=
name|unkDictionary
expr_stmt|;
name|this
operator|.
name|costs
operator|=
name|costs
expr_stmt|;
name|this
operator|.
name|userDictionary
operator|=
name|userDictionary
expr_stmt|;
if|if
condition|(
name|userDictionary
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|useUserDictionary
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|useUserDictionary
operator|=
literal|true
expr_stmt|;
block|}
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|SEARCH
case|:
name|searchMode
operator|=
literal|true
expr_stmt|;
name|extendedMode
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|EXTENDED
case|:
name|searchMode
operator|=
literal|true
expr_stmt|;
name|extendedMode
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
name|searchMode
operator|=
literal|false
expr_stmt|;
name|extendedMode
operator|=
literal|false
expr_stmt|;
break|break;
block|}
name|this
operator|.
name|characterDefinition
operator|=
name|unkDictionary
operator|.
name|getCharacterDefinition
argument_list|()
expr_stmt|;
block|}
comment|/**    * Find best path from input lattice.    * @param lattice the result of build method    * @return	List of ViterbiNode which consist best path     */
DECL|method|search
specifier|public
name|List
argument_list|<
name|ViterbiNode
argument_list|>
name|search
parameter_list|(
name|ViterbiNode
index|[]
index|[]
index|[]
name|lattice
parameter_list|)
block|{
name|ViterbiNode
index|[]
index|[]
name|startIndexArr
init|=
name|lattice
index|[
literal|0
index|]
decl_stmt|;
name|ViterbiNode
index|[]
index|[]
name|endIndexArr
init|=
name|lattice
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|startIndexArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|startIndexArr
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|endIndexArr
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
comment|// continue since no array which contains ViterbiNodes exists. Or no previous node exists.
continue|continue;
block|}
for|for
control|(
name|ViterbiNode
name|node
range|:
name|startIndexArr
index|[
name|i
index|]
control|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
comment|// If array doesn't contain ViterbiNode any more, continue to next index
break|break;
block|}
name|int
name|backwardConnectionId
init|=
name|node
operator|.
name|getLeftId
argument_list|()
decl_stmt|;
name|int
name|wordCost
init|=
name|node
operator|.
name|getWordCost
argument_list|()
decl_stmt|;
name|int
name|leastPathCost
init|=
name|DEFAULT_COST
decl_stmt|;
for|for
control|(
name|ViterbiNode
name|leftNode
range|:
name|endIndexArr
index|[
name|i
index|]
control|)
block|{
if|if
condition|(
name|leftNode
operator|==
literal|null
condition|)
block|{
comment|// If array doesn't contain ViterbiNode any more, continue to next index
break|break;
block|}
name|int
name|pathCost
init|=
name|leftNode
operator|.
name|getPathCost
argument_list|()
operator|+
name|costs
operator|.
name|get
argument_list|(
name|leftNode
operator|.
name|getRightId
argument_list|()
argument_list|,
name|backwardConnectionId
argument_list|)
operator|+
name|wordCost
decl_stmt|;
comment|// cost = [total cost from BOS to previous node] + [connection cost between previous node and current node] + [word cost]
comment|// "Search mode". Add extra costs if it is long node.
if|if
condition|(
name|searchMode
condition|)
block|{
comment|//						System.out.print(""); // If this line exists, kuromoji runs faster for some reason when searchMode == false.
name|char
index|[]
name|surfaceForm
init|=
name|node
operator|.
name|getSurfaceForm
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|node
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|node
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|>
name|SEARCH_MODE_LENGTH_KANJI
condition|)
block|{
name|boolean
name|allKanji
init|=
literal|true
decl_stmt|;
comment|// check if node consists of only kanji
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|length
condition|;
name|pos
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|characterDefinition
operator|.
name|isKanji
argument_list|(
name|surfaceForm
index|[
name|offset
operator|+
name|pos
index|]
argument_list|)
condition|)
block|{
name|allKanji
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|allKanji
condition|)
block|{
comment|// Process only Kanji keywords
name|pathCost
operator|+=
operator|(
name|length
operator|-
name|SEARCH_MODE_LENGTH_KANJI
operator|)
operator|*
name|SEARCH_MODE_PENALTY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
name|SEARCH_MODE_LENGTH
condition|)
block|{
name|pathCost
operator|+=
operator|(
name|length
operator|-
name|SEARCH_MODE_LENGTH
operator|)
operator|*
name|SEARCH_MODE_PENALTY
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|pathCost
operator|<
name|leastPathCost
condition|)
block|{
comment|// If total cost is lower than before, set current previous node as best left node (previous means left).
name|leastPathCost
operator|=
name|pathCost
expr_stmt|;
name|node
operator|.
name|setPathCost
argument_list|(
name|leastPathCost
argument_list|)
expr_stmt|;
name|node
operator|.
name|setLeftNode
argument_list|(
name|leftNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// track best path
name|ViterbiNode
name|node
init|=
name|endIndexArr
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
comment|// EOS
name|LinkedList
argument_list|<
name|ViterbiNode
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<
name|ViterbiNode
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|ViterbiNode
name|leftNode
init|=
name|node
operator|.
name|getLeftNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftNode
operator|==
literal|null
condition|)
block|{
break|break;
block|}
comment|// EXTENDED mode convert unknown word into unigram node
if|if
condition|(
name|extendedMode
operator|&&
name|leftNode
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|UNKNOWN
condition|)
block|{
name|byte
name|unigramWordId
init|=
name|CharacterDefinition
operator|.
name|NGRAM
decl_stmt|;
name|int
name|unigramLeftId
init|=
name|unkDictionary
operator|.
name|getLeftId
argument_list|(
name|unigramWordId
argument_list|)
decl_stmt|;
comment|// isn't required
name|int
name|unigramRightId
init|=
name|unkDictionary
operator|.
name|getLeftId
argument_list|(
name|unigramWordId
argument_list|)
decl_stmt|;
comment|// isn't required
name|int
name|unigramWordCost
init|=
name|unkDictionary
operator|.
name|getWordCost
argument_list|(
name|unigramWordId
argument_list|)
decl_stmt|;
comment|// isn't required
name|char
index|[]
name|surfaceForm
init|=
name|leftNode
operator|.
name|getSurfaceForm
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|leftNode
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|leftNode
operator|.
name|getLength
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|charLen
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|Character
operator|.
name|isLowSurrogate
argument_list|(
name|surfaceForm
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
condition|)
block|{
name|i
operator|--
expr_stmt|;
name|charLen
operator|=
literal|2
expr_stmt|;
block|}
name|ViterbiNode
name|uniGramNode
init|=
operator|new
name|ViterbiNode
argument_list|(
name|unigramWordId
argument_list|,
name|surfaceForm
argument_list|,
name|offset
operator|+
name|i
argument_list|,
name|charLen
argument_list|,
name|unigramLeftId
argument_list|,
name|unigramRightId
argument_list|,
name|unigramWordCost
argument_list|,
name|leftNode
operator|.
name|getStartIndex
argument_list|()
operator|+
name|i
argument_list|,
name|Type
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
name|result
operator|.
name|addFirst
argument_list|(
name|uniGramNode
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|.
name|addFirst
argument_list|(
name|leftNode
argument_list|)
expr_stmt|;
block|}
name|node
operator|=
name|leftNode
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Build lattice from input text    * @param text    */
DECL|method|build
specifier|public
name|ViterbiNode
index|[]
index|[]
index|[]
name|build
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|ViterbiNode
index|[]
index|[]
name|startIndexArr
init|=
operator|new
name|ViterbiNode
index|[
name|length
operator|+
literal|2
index|]
index|[]
decl_stmt|;
comment|// text length + BOS and EOS
name|ViterbiNode
index|[]
index|[]
name|endIndexArr
init|=
operator|new
name|ViterbiNode
index|[
name|length
operator|+
literal|2
index|]
index|[]
decl_stmt|;
comment|// text length + BOS and EOS
name|int
index|[]
name|startSizeArr
init|=
operator|new
name|int
index|[
name|length
operator|+
literal|2
index|]
decl_stmt|;
comment|// array to keep ViterbiNode count in startIndexArr
name|int
index|[]
name|endSizeArr
init|=
operator|new
name|int
index|[
name|length
operator|+
literal|2
index|]
decl_stmt|;
comment|// array to keep ViterbiNode count in endIndexArr
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
name|arc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|ViterbiNode
name|bosNode
init|=
operator|new
name|ViterbiNode
argument_list|(
operator|-
literal|1
argument_list|,
name|BOS
argument_list|,
literal|0
argument_list|,
name|BOS
operator|.
name|length
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
name|Type
operator|.
name|KNOWN
argument_list|)
decl_stmt|;
name|addToArrays
argument_list|(
name|bosNode
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|startIndexArr
argument_list|,
name|endIndexArr
argument_list|,
name|startSizeArr
argument_list|,
name|endSizeArr
argument_list|)
expr_stmt|;
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Process user dictionary;
if|if
condition|(
name|useUserDictionary
condition|)
block|{
name|processUserDictionary
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|startIndexArr
argument_list|,
name|endIndexArr
argument_list|,
name|startSizeArr
argument_list|,
name|endSizeArr
argument_list|)
expr_stmt|;
block|}
name|int
name|unknownWordEndIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|// index of the last character of unknown word
specifier|final
name|IntsRef
name|wordIdRef
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|startIndex
init|=
literal|0
init|;
name|startIndex
operator|<
name|length
condition|;
name|startIndex
operator|++
control|)
block|{
comment|// If no token ends where current token starts, skip this index
if|if
condition|(
name|endSizeArr
index|[
name|startIndex
operator|+
literal|1
index|]
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|suffixStart
init|=
name|offset
operator|+
name|startIndex
decl_stmt|;
name|int
name|suffixLength
init|=
name|length
operator|-
name|startIndex
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|arc
operator|=
name|fst
operator|.
name|getFirstArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
name|int
name|output
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|endIndex
init|=
literal|1
init|;
name|endIndex
operator|<
name|suffixLength
operator|+
literal|1
condition|;
name|endIndex
operator|++
control|)
block|{
name|int
name|ch
init|=
name|text
index|[
name|suffixStart
operator|+
name|endIndex
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|ch
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|,
name|endIndex
operator|==
literal|1
argument_list|,
name|fstReader
argument_list|)
operator|==
literal|null
condition|)
block|{
break|break;
comment|// continue to next position
block|}
name|output
operator|+=
name|arc
operator|.
name|output
operator|.
name|intValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|output
operator|+=
name|arc
operator|.
name|nextFinalOutput
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
comment|// Don't produce unknown word starting from this index
name|dictionary
operator|.
name|lookupWordIds
argument_list|(
name|output
argument_list|,
name|wordIdRef
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ofs
init|=
literal|0
init|;
name|ofs
operator|<
name|wordIdRef
operator|.
name|length
condition|;
name|ofs
operator|++
control|)
block|{
specifier|final
name|int
name|wordId
init|=
name|wordIdRef
operator|.
name|ints
index|[
name|wordIdRef
operator|.
name|offset
operator|+
name|ofs
index|]
decl_stmt|;
name|ViterbiNode
name|node
init|=
operator|new
name|ViterbiNode
argument_list|(
name|wordId
argument_list|,
name|text
argument_list|,
name|suffixStart
argument_list|,
name|endIndex
argument_list|,
name|dictionary
operator|.
name|getLeftId
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|dictionary
operator|.
name|getRightId
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|dictionary
operator|.
name|getWordCost
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|startIndex
argument_list|,
name|Type
operator|.
name|KNOWN
argument_list|)
decl_stmt|;
name|addToArrays
argument_list|(
name|node
argument_list|,
name|startIndex
operator|+
literal|1
argument_list|,
name|startIndex
operator|+
literal|1
operator|+
name|endIndex
argument_list|,
name|startIndexArr
argument_list|,
name|endIndexArr
argument_list|,
name|startSizeArr
argument_list|,
name|endSizeArr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// In the case of normal mode, it doesn't process unknown word greedily.
if|if
condition|(
operator|!
name|searchMode
operator|&&
name|unknownWordEndIndex
operator|>
name|startIndex
condition|)
block|{
continue|continue;
block|}
comment|// Process Unknown Word: hmm what is this isInvoke logic (same no matter what)
name|int
name|unknownWordLength
init|=
literal|0
decl_stmt|;
name|char
name|firstCharacter
init|=
name|text
index|[
name|suffixStart
index|]
decl_stmt|;
name|boolean
name|isInvoke
init|=
name|characterDefinition
operator|.
name|isInvoke
argument_list|(
name|firstCharacter
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInvoke
condition|)
block|{
comment|// Process "invoke"
name|unknownWordLength
operator|=
name|unkDictionary
operator|.
name|lookup
argument_list|(
name|text
argument_list|,
name|suffixStart
argument_list|,
name|suffixLength
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|found
operator|==
literal|false
condition|)
block|{
comment|// Process not "invoke"
name|unknownWordLength
operator|=
name|unkDictionary
operator|.
name|lookup
argument_list|(
name|text
argument_list|,
name|suffixStart
argument_list|,
name|suffixLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|unknownWordLength
operator|>
literal|0
condition|)
block|{
comment|// found unknown word
specifier|final
name|int
name|characterId
init|=
name|characterDefinition
operator|.
name|getCharacterClass
argument_list|(
name|firstCharacter
argument_list|)
decl_stmt|;
name|unkDictionary
operator|.
name|lookupWordIds
argument_list|(
name|characterId
argument_list|,
name|wordIdRef
argument_list|)
expr_stmt|;
comment|// characters in input text are supposed to be the same
for|for
control|(
name|int
name|ofs
init|=
literal|0
init|;
name|ofs
operator|<
name|wordIdRef
operator|.
name|length
condition|;
name|ofs
operator|++
control|)
block|{
specifier|final
name|int
name|wordId
init|=
name|wordIdRef
operator|.
name|ints
index|[
name|wordIdRef
operator|.
name|offset
operator|+
name|ofs
index|]
decl_stmt|;
name|ViterbiNode
name|node
init|=
operator|new
name|ViterbiNode
argument_list|(
name|wordId
argument_list|,
name|text
argument_list|,
name|suffixStart
argument_list|,
name|unknownWordLength
argument_list|,
name|unkDictionary
operator|.
name|getLeftId
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|unkDictionary
operator|.
name|getRightId
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|unkDictionary
operator|.
name|getWordCost
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|startIndex
argument_list|,
name|Type
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
name|addToArrays
argument_list|(
name|node
argument_list|,
name|startIndex
operator|+
literal|1
argument_list|,
name|startIndex
operator|+
literal|1
operator|+
name|unknownWordLength
argument_list|,
name|startIndexArr
argument_list|,
name|endIndexArr
argument_list|,
name|startSizeArr
argument_list|,
name|endSizeArr
argument_list|)
expr_stmt|;
block|}
name|unknownWordEndIndex
operator|=
name|startIndex
operator|+
name|unknownWordLength
expr_stmt|;
block|}
block|}
name|ViterbiNode
name|eosNode
init|=
operator|new
name|ViterbiNode
argument_list|(
operator|-
literal|1
argument_list|,
name|EOS
argument_list|,
literal|0
argument_list|,
name|EOS
operator|.
name|length
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|length
operator|+
literal|1
argument_list|,
name|Type
operator|.
name|KNOWN
argument_list|)
decl_stmt|;
name|addToArrays
argument_list|(
name|eosNode
argument_list|,
name|length
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
name|startIndexArr
argument_list|,
name|endIndexArr
argument_list|,
name|startSizeArr
argument_list|,
name|endSizeArr
argument_list|)
expr_stmt|;
comment|//Add EOS node to endIndexArr at index 0
name|ViterbiNode
index|[]
index|[]
index|[]
name|result
init|=
operator|new
name|ViterbiNode
index|[]
index|[]
index|[]
block|{
name|startIndexArr
block|,
name|endIndexArr
block|}
decl_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Find token(s) in input text and set found token(s) in arrays as normal tokens    * @param text	    * @param startIndexArr    * @param endIndexArr    * @param startSizeArr    * @param endSizeArr    */
DECL|method|processUserDictionary
specifier|private
name|void
name|processUserDictionary
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|startIndexArr
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|endIndexArr
parameter_list|,
name|int
index|[]
name|startSizeArr
parameter_list|,
name|int
index|[]
name|endSizeArr
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
index|[]
name|result
init|=
name|userDictionary
operator|.
name|lookup
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
index|[]
name|segmentation
range|:
name|result
control|)
block|{
name|int
name|wordId
init|=
name|segmentation
index|[
literal|0
index|]
decl_stmt|;
name|int
name|index
init|=
name|segmentation
index|[
literal|1
index|]
decl_stmt|;
name|int
name|length
init|=
name|segmentation
index|[
literal|2
index|]
decl_stmt|;
name|ViterbiNode
name|node
init|=
operator|new
name|ViterbiNode
argument_list|(
name|wordId
argument_list|,
name|text
argument_list|,
name|offset
operator|+
name|index
argument_list|,
name|length
argument_list|,
name|userDictionary
operator|.
name|getLeftId
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|userDictionary
operator|.
name|getRightId
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|userDictionary
operator|.
name|getWordCost
argument_list|(
name|wordId
argument_list|)
argument_list|,
name|index
argument_list|,
name|Type
operator|.
name|USER
argument_list|)
decl_stmt|;
name|addToArrays
argument_list|(
name|node
argument_list|,
name|index
operator|+
literal|1
argument_list|,
name|index
operator|+
literal|1
operator|+
name|length
argument_list|,
name|startIndexArr
argument_list|,
name|endIndexArr
argument_list|,
name|startSizeArr
argument_list|,
name|endSizeArr
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add node to arrays and increment count in size array    * @param node    * @param startIndex    * @param endIndex    * @param startIndexArr    * @param endIndexArr    * @param startSizeArr    * @param endSizeArr    */
DECL|method|addToArrays
specifier|private
name|void
name|addToArrays
parameter_list|(
name|ViterbiNode
name|node
parameter_list|,
name|int
name|startIndex
parameter_list|,
name|int
name|endIndex
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|startIndexArr
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|endIndexArr
parameter_list|,
name|int
index|[]
name|startSizeArr
parameter_list|,
name|int
index|[]
name|endSizeArr
parameter_list|)
block|{
name|int
name|startNodesCount
init|=
name|startSizeArr
index|[
name|startIndex
index|]
decl_stmt|;
name|int
name|endNodesCount
init|=
name|endSizeArr
index|[
name|endIndex
index|]
decl_stmt|;
if|if
condition|(
name|startNodesCount
operator|==
literal|0
condition|)
block|{
name|startIndexArr
index|[
name|startIndex
index|]
operator|=
operator|new
name|ViterbiNode
index|[
literal|10
index|]
expr_stmt|;
block|}
if|if
condition|(
name|endNodesCount
operator|==
literal|0
condition|)
block|{
name|endIndexArr
index|[
name|endIndex
index|]
operator|=
operator|new
name|ViterbiNode
index|[
literal|10
index|]
expr_stmt|;
block|}
if|if
condition|(
name|startIndexArr
index|[
name|startIndex
index|]
operator|.
name|length
operator|<=
name|startNodesCount
condition|)
block|{
name|startIndexArr
index|[
name|startIndex
index|]
operator|=
name|extendArray
argument_list|(
name|startIndexArr
index|[
name|startIndex
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|endIndexArr
index|[
name|endIndex
index|]
operator|.
name|length
operator|<=
name|endNodesCount
condition|)
block|{
name|endIndexArr
index|[
name|endIndex
index|]
operator|=
name|extendArray
argument_list|(
name|endIndexArr
index|[
name|endIndex
index|]
argument_list|)
expr_stmt|;
block|}
name|startIndexArr
index|[
name|startIndex
index|]
index|[
name|startNodesCount
index|]
operator|=
name|node
expr_stmt|;
name|endIndexArr
index|[
name|endIndex
index|]
index|[
name|endNodesCount
index|]
operator|=
name|node
expr_stmt|;
name|startSizeArr
index|[
name|startIndex
index|]
operator|=
name|startNodesCount
operator|+
literal|1
expr_stmt|;
name|endSizeArr
index|[
name|endIndex
index|]
operator|=
name|endNodesCount
operator|+
literal|1
expr_stmt|;
block|}
comment|/**    * Return twice as big array which contains value of input array    * @param array    * @return    */
DECL|method|extendArray
specifier|private
name|ViterbiNode
index|[]
name|extendArray
parameter_list|(
name|ViterbiNode
index|[]
name|array
parameter_list|)
block|{
comment|//extend array
name|ViterbiNode
index|[]
name|newArray
init|=
operator|new
name|ViterbiNode
index|[
name|array
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
block|}
end_class

end_unit

