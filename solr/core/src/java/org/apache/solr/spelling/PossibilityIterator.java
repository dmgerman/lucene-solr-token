begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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
name|HashSet
import|;
end_import

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
name|LinkedHashMap
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
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|analysis
operator|.
name|Token
import|;
end_import

begin_comment
comment|/**  *<p>  * Given a list of possible Spelling Corrections for multiple mis-spelled words  * in a query, This iterator returns Possible Correction combinations ordered by  * reasonable probability that such a combination will return actual hits if  * re-queried. This implementation simply ranks the Possible Combinations by the  * sum of their component ranks.  *</p>  *   */
end_comment

begin_class
DECL|class|PossibilityIterator
specifier|public
class|class
name|PossibilityIterator
implements|implements
name|Iterator
argument_list|<
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
argument_list|>
block|{
DECL|field|possibilityList
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
name|possibilityList
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rankedPossibilityIterator
specifier|private
name|Iterator
argument_list|<
name|RankedSpellPossibility
argument_list|>
name|rankedPossibilityIterator
init|=
literal|null
decl_stmt|;
DECL|field|correctionIndex
specifier|private
name|int
name|correctionIndex
index|[]
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|nextOnes
specifier|private
name|Iterator
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
name|nextOnes
init|=
literal|null
decl_stmt|;
DECL|field|nextOnesRank
specifier|private
name|int
name|nextOnesRank
init|=
literal|0
decl_stmt|;
DECL|field|nextOnesIndex
specifier|private
name|int
name|nextOnesIndex
init|=
literal|0
decl_stmt|;
DECL|field|suggestionsMayOverlap
specifier|private
name|boolean
name|suggestionsMayOverlap
init|=
literal|false
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|PossibilityIterator
specifier|private
name|PossibilityIterator
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"You shan't go here."
argument_list|)
throw|;
block|}
comment|/**    *<p>    * We assume here that the passed-in inner LinkedHashMaps are already sorted    * in order of "Best Possible Correction".    *</p>    *     * @param suggestions    */
DECL|method|PossibilityIterator
specifier|public
name|PossibilityIterator
parameter_list|(
name|Map
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|suggestions
parameter_list|,
name|int
name|maximumRequiredSuggestions
parameter_list|,
name|int
name|maxEvaluations
parameter_list|,
name|boolean
name|overlap
parameter_list|)
block|{
name|this
operator|.
name|suggestionsMayOverlap
operator|=
name|overlap
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|entry
range|:
name|suggestions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Token
name|token
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|possibleCorrections
init|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry1
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SpellCheckCorrection
name|correction
init|=
operator|new
name|SpellCheckCorrection
argument_list|()
decl_stmt|;
name|correction
operator|.
name|setOriginal
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|correction
operator|.
name|setCorrection
argument_list|(
name|entry1
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|correction
operator|.
name|setNumberOfOccurences
argument_list|(
name|entry1
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|possibleCorrections
operator|.
name|add
argument_list|(
name|correction
argument_list|)
expr_stmt|;
block|}
name|possibilityList
operator|.
name|add
argument_list|(
name|possibleCorrections
argument_list|)
expr_stmt|;
block|}
name|int
name|wrapSize
init|=
name|possibilityList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|wrapSize
operator|==
literal|0
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|correctionIndex
operator|=
operator|new
name|int
index|[
name|wrapSize
index|]
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
name|wrapSize
condition|;
name|i
operator|++
control|)
block|{
name|int
name|suggestSize
init|=
name|possibilityList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|suggestSize
operator|==
literal|0
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|correctionIndex
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|PriorityQueue
argument_list|<
name|RankedSpellPossibility
argument_list|>
name|rankedPossibilities
init|=
operator|new
name|PriorityQueue
argument_list|<
name|RankedSpellPossibility
argument_list|>
argument_list|(
literal|11
argument_list|,
operator|new
name|RankComparator
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|RankedSpellPossibility
argument_list|>
name|removeDuplicates
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|suggestionsMayOverlap
condition|)
block|{
name|removeDuplicates
operator|=
operator|new
name|HashSet
argument_list|<
name|RankedSpellPossibility
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|long
name|numEvaluations
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|numEvaluations
operator|<
name|maxEvaluations
operator|&&
name|internalHasNext
argument_list|()
condition|)
block|{
name|RankedSpellPossibility
name|rsp
init|=
name|internalNext
argument_list|()
decl_stmt|;
name|numEvaluations
operator|++
expr_stmt|;
if|if
condition|(
name|rankedPossibilities
operator|.
name|size
argument_list|()
operator|>=
name|maximumRequiredSuggestions
operator|&&
name|rsp
operator|.
name|rank
operator|>=
name|rankedPossibilities
operator|.
name|peek
argument_list|()
operator|.
name|rank
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|isSuggestionForReal
argument_list|(
name|rsp
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|removeDuplicates
operator|==
literal|null
condition|)
block|{
name|rankedPossibilities
operator|.
name|offer
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Needs to be in token-offset order so that the match-and-replace
comment|// option for collations can work.
name|Collections
operator|.
name|sort
argument_list|(
name|rsp
operator|.
name|corrections
argument_list|,
operator|new
name|StartOffsetComparator
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|removeDuplicates
operator|.
name|add
argument_list|(
name|rsp
argument_list|)
condition|)
block|{
name|rankedPossibilities
operator|.
name|offer
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rankedPossibilities
operator|.
name|size
argument_list|()
operator|>
name|maximumRequiredSuggestions
condition|)
block|{
name|RankedSpellPossibility
name|removed
init|=
name|rankedPossibilities
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|removeDuplicates
operator|!=
literal|null
condition|)
block|{
name|removeDuplicates
operator|.
name|remove
argument_list|(
name|removed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|RankedSpellPossibility
index|[]
name|rpArr
init|=
operator|new
name|RankedSpellPossibility
index|[
name|rankedPossibilities
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|rankedPossibilities
operator|.
name|size
argument_list|()
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
name|rpArr
index|[
name|i
index|]
operator|=
name|rankedPossibilities
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|rankedPossibilityIterator
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|rpArr
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|isSuggestionForReal
specifier|private
name|boolean
name|isSuggestionForReal
parameter_list|(
name|RankedSpellPossibility
name|rsp
parameter_list|)
block|{
for|for
control|(
name|SpellCheckCorrection
name|corr
range|:
name|rsp
operator|.
name|corrections
control|)
block|{
if|if
condition|(
operator|!
name|corr
operator|.
name|getOriginalAsString
argument_list|()
operator|.
name|equals
argument_list|(
name|corr
operator|.
name|getCorrection
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|internalHasNext
specifier|private
name|boolean
name|internalHasNext
parameter_list|()
block|{
if|if
condition|(
name|nextOnes
operator|!=
literal|null
operator|&&
name|nextOnes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|done
condition|)
block|{
return|return
literal|false
return|;
block|}
name|internalNextAdvance
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextOnes
operator|!=
literal|null
operator|&&
name|nextOnes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    *<p>    * This method is converting the independent LinkHashMaps containing various    * (silo'ed) suggestions for each mis-spelled word into individual    * "holistic query corrections", aka. "Spell Check Possibility"    *</p>    *<p>    * Rank here is the sum of each selected term's position in its respective    * LinkedHashMap.    *</p>    *     * @return    */
DECL|method|internalNext
specifier|private
name|RankedSpellPossibility
name|internalNext
parameter_list|()
block|{
if|if
condition|(
name|nextOnes
operator|!=
literal|null
operator|&&
name|nextOnes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RankedSpellPossibility
name|rsl
init|=
operator|new
name|RankedSpellPossibility
argument_list|()
decl_stmt|;
name|rsl
operator|.
name|corrections
operator|=
name|nextOnes
operator|.
name|next
argument_list|()
expr_stmt|;
name|rsl
operator|.
name|rank
operator|=
name|nextOnesRank
expr_stmt|;
name|rsl
operator|.
name|index
operator|=
name|nextOnesIndex
operator|++
expr_stmt|;
return|return
name|rsl
return|;
block|}
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|internalNextAdvance
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextOnes
operator|!=
literal|null
operator|&&
name|nextOnes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RankedSpellPossibility
name|rsl
init|=
operator|new
name|RankedSpellPossibility
argument_list|()
decl_stmt|;
name|rsl
operator|.
name|corrections
operator|=
name|nextOnes
operator|.
name|next
argument_list|()
expr_stmt|;
name|rsl
operator|.
name|rank
operator|=
name|nextOnesRank
expr_stmt|;
name|rsl
operator|.
name|index
operator|=
name|nextOnesIndex
operator|++
expr_stmt|;
return|return
name|rsl
return|;
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
DECL|method|internalNextAdvance
specifier|private
name|void
name|internalNextAdvance
parameter_list|()
block|{
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|possibleCorrection
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nextOnes
operator|!=
literal|null
operator|&&
name|nextOnes
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|possibleCorrection
operator|=
name|nextOnes
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|possibleCorrection
operator|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
name|possibleCorrections
init|=
literal|null
decl_stmt|;
name|int
name|rank
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|done
operator|&&
operator|(
name|possibleCorrections
operator|==
literal|null
operator|||
name|possibleCorrections
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|rank
operator|=
literal|0
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
name|correctionIndex
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|singleWordPossibilities
init|=
name|possibilityList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SpellCheckCorrection
name|singleWordPossibility
init|=
name|singleWordPossibilities
operator|.
name|get
argument_list|(
name|correctionIndex
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|rank
operator|+=
name|correctionIndex
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|correctionIndex
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|correctionIndex
index|[
name|i
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|correctionIndex
index|[
name|i
index|]
operator|==
name|singleWordPossibilities
operator|.
name|size
argument_list|()
condition|)
block|{
name|correctionIndex
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|correctionIndex
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
for|for
control|(
name|int
name|ii
init|=
name|i
operator|-
literal|1
init|;
name|ii
operator|>=
literal|0
condition|;
name|ii
operator|--
control|)
block|{
name|correctionIndex
index|[
name|ii
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|correctionIndex
index|[
name|ii
index|]
operator|>=
name|possibilityList
operator|.
name|get
argument_list|(
name|ii
argument_list|)
operator|.
name|size
argument_list|()
operator|&&
name|ii
operator|>
literal|0
condition|)
block|{
name|correctionIndex
index|[
name|ii
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
name|possibleCorrection
operator|.
name|add
argument_list|(
name|singleWordPossibility
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|correctionIndex
index|[
literal|0
index|]
operator|==
name|possibilityList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|suggestionsMayOverlap
condition|)
block|{
name|possibleCorrections
operator|=
name|separateOverlappingTokens
argument_list|(
name|possibleCorrection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|possibleCorrections
operator|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|possibleCorrections
operator|.
name|add
argument_list|(
name|possibleCorrection
argument_list|)
expr_stmt|;
block|}
block|}
name|nextOnes
operator|=
name|possibleCorrections
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|nextOnesRank
operator|=
name|rank
expr_stmt|;
name|nextOnesIndex
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|separateOverlappingTokens
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
name|separateOverlappingTokens
parameter_list|(
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|possibleCorrection
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
name|ret
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|possibleCorrection
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|ret
operator|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|possibleCorrection
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
name|ret
operator|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
argument_list|()
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
name|possibleCorrection
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|c
init|=
name|compatible
argument_list|(
name|possibleCorrection
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|compatible
specifier|private
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|compatible
parameter_list|(
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|all
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|priorPassCompatibles
init|=
literal|null
decl_stmt|;
block|{
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|firstPassCompatibles
init|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|(
name|all
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|SpellCheckCorrection
name|sacred
init|=
name|all
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|firstPassCompatibles
operator|.
name|add
argument_list|(
name|sacred
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|pos
decl_stmt|;
name|boolean
name|gotOne
init|=
literal|false
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
name|all
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|index
operator|++
expr_stmt|;
if|if
condition|(
name|index
operator|==
name|all
operator|.
name|size
argument_list|()
condition|)
block|{
name|index
operator|=
literal|0
expr_stmt|;
block|}
name|SpellCheckCorrection
name|disposable
init|=
name|all
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|conflicts
argument_list|(
name|sacred
argument_list|,
name|disposable
argument_list|)
condition|)
block|{
name|firstPassCompatibles
operator|.
name|add
argument_list|(
name|disposable
argument_list|)
expr_stmt|;
name|gotOne
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|gotOne
condition|)
block|{
return|return
name|firstPassCompatibles
return|;
block|}
name|priorPassCompatibles
operator|=
name|firstPassCompatibles
expr_stmt|;
block|}
block|{
name|pos
operator|=
literal|1
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|pos
operator|==
name|priorPassCompatibles
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
name|priorPassCompatibles
return|;
block|}
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|subsequentPassCompatibles
init|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|(
name|priorPassCompatibles
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|SpellCheckCorrection
name|sacred
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|pos
condition|;
name|i
operator|++
control|)
block|{
name|sacred
operator|=
name|priorPassCompatibles
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|subsequentPassCompatibles
operator|.
name|add
argument_list|(
name|sacred
argument_list|)
expr_stmt|;
block|}
name|int
name|index
init|=
name|pos
decl_stmt|;
name|boolean
name|gotOne
init|=
literal|false
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
name|priorPassCompatibles
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|index
operator|++
expr_stmt|;
if|if
condition|(
name|index
operator|==
name|priorPassCompatibles
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
name|SpellCheckCorrection
name|disposable
init|=
name|priorPassCompatibles
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|conflicts
argument_list|(
name|sacred
argument_list|,
name|disposable
argument_list|)
condition|)
block|{
name|subsequentPassCompatibles
operator|.
name|add
argument_list|(
name|disposable
argument_list|)
expr_stmt|;
name|gotOne
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|gotOne
operator|||
name|pos
operator|==
name|priorPassCompatibles
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
name|subsequentPassCompatibles
return|;
block|}
name|priorPassCompatibles
operator|=
name|subsequentPassCompatibles
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|conflicts
specifier|private
name|boolean
name|conflicts
parameter_list|(
name|SpellCheckCorrection
name|c1
parameter_list|,
name|SpellCheckCorrection
name|c2
parameter_list|)
block|{
name|int
name|s1
init|=
name|c1
operator|.
name|getOriginal
argument_list|()
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|e1
init|=
name|c1
operator|.
name|getOriginal
argument_list|()
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|c2
operator|.
name|getOriginal
argument_list|()
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|e2
init|=
name|c2
operator|.
name|getOriginal
argument_list|()
operator|.
name|endOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|s2
operator|>=
name|s1
operator|&&
name|s2
operator|<=
name|e1
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|s1
operator|>=
name|s2
operator|&&
name|s1
operator|<=
name|e2
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|rankedPossibilityIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
name|next
parameter_list|()
block|{
return|return
name|rankedPossibilityIterator
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|class|RankedSpellPossibility
specifier|public
class|class
name|RankedSpellPossibility
block|{
DECL|field|corrections
specifier|public
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|corrections
decl_stmt|;
DECL|field|rank
specifier|public
name|int
name|rank
decl_stmt|;
DECL|field|index
specifier|public
name|int
name|index
decl_stmt|;
annotation|@
name|Override
comment|// hashCode() and equals() only consider the actual correction, not the rank
comment|// or index.
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|corrections
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|corrections
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
comment|// hashCode() and equals() only consider the actual correction, not the rank
comment|// or index.
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RankedSpellPossibility
name|other
init|=
operator|(
name|RankedSpellPossibility
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|corrections
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|corrections
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|corrections
operator|.
name|equals
argument_list|(
name|other
operator|.
name|corrections
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"rank="
argument_list|)
operator|.
name|append
argument_list|(
name|rank
argument_list|)
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|index
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|corrections
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SpellCheckCorrection
name|corr
range|:
name|corrections
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"     "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|corr
operator|.
name|getOriginal
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
operator|.
name|append
argument_list|(
name|corr
operator|.
name|getCorrection
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|corr
operator|.
name|getNumberOfOccurences
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|StartOffsetComparator
specifier|private
class|class
name|StartOffsetComparator
implements|implements
name|Comparator
argument_list|<
name|SpellCheckCorrection
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|SpellCheckCorrection
name|o1
parameter_list|,
name|SpellCheckCorrection
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getOriginal
argument_list|()
operator|.
name|startOffset
argument_list|()
operator|-
name|o2
operator|.
name|getOriginal
argument_list|()
operator|.
name|startOffset
argument_list|()
return|;
block|}
block|}
DECL|class|RankComparator
specifier|private
class|class
name|RankComparator
implements|implements
name|Comparator
argument_list|<
name|RankedSpellPossibility
argument_list|>
block|{
comment|// Rank poorer suggestions ahead of better ones for use with a PriorityQueue
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|RankedSpellPossibility
name|r1
parameter_list|,
name|RankedSpellPossibility
name|r2
parameter_list|)
block|{
name|int
name|retval
init|=
name|r2
operator|.
name|rank
operator|-
name|r1
operator|.
name|rank
decl_stmt|;
if|if
condition|(
name|retval
operator|==
literal|0
condition|)
block|{
name|retval
operator|=
name|r2
operator|.
name|index
operator|-
name|r1
operator|.
name|index
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
block|}
block|}
end_class

end_unit

