begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|index
operator|.
name|FilteredTermsEnum
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
name|BoostAttribute
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
name|FuzzyTermsEnum
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
name|AttributeSource
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|StringHelper
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
comment|/** Classic fuzzy TermsEnum for enumerating all terms that are similar  * to the specified filter term.  *  *<p>Term enumerations are always ordered by  * {@link #getComparator}.  Each term in the enumeration is  * greater than all that precede it.</p>  *   * @deprecated Use {@link FuzzyTermsEnum} instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SlowFuzzyTermsEnum
specifier|public
specifier|final
class|class
name|SlowFuzzyTermsEnum
extends|extends
name|FuzzyTermsEnum
block|{
DECL|method|SlowFuzzyTermsEnum
specifier|public
name|SlowFuzzyTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|,
name|Term
name|term
parameter_list|,
name|float
name|minSimilarity
parameter_list|,
name|int
name|prefixLength
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|terms
argument_list|,
name|atts
argument_list|,
name|term
argument_list|,
name|minSimilarity
argument_list|,
name|prefixLength
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|maxEditDistanceChanged
specifier|protected
name|void
name|maxEditDistanceChanged
parameter_list|(
name|BytesRef
name|lastTerm
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|boolean
name|init
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|newEnum
init|=
name|getAutomatonEnum
argument_list|(
name|maxEdits
argument_list|,
name|lastTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|newEnum
operator|!=
literal|null
condition|)
block|{
name|setEnum
argument_list|(
name|newEnum
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|init
condition|)
block|{
name|setEnum
argument_list|(
operator|new
name|LinearFuzzyTermsEnum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Implement fuzzy enumeration with linear brute force.    */
DECL|class|LinearFuzzyTermsEnum
specifier|private
class|class
name|LinearFuzzyTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
comment|/* Allows us save time required to create a new array      * every time similarity is called.      */
DECL|field|d
specifier|private
name|int
index|[]
name|d
decl_stmt|;
DECL|field|p
specifier|private
name|int
index|[]
name|p
decl_stmt|;
comment|// this is the text, minus the prefix
DECL|field|text
specifier|private
specifier|final
name|int
index|[]
name|text
decl_stmt|;
DECL|field|boostAtt
specifier|private
specifier|final
name|BoostAttribute
name|boostAtt
init|=
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Constructor for enumeration of all terms from specified<code>reader</code> which share a prefix of      * length<code>prefixLength</code> with<code>term</code> and which have a fuzzy similarity&gt;      *<code>minSimilarity</code>.      *<p>      * After calling the constructor the enumeration is already pointing to the first       * valid term if such a term exists.      *      * @throws IOException      */
DECL|method|LinearFuzzyTermsEnum
specifier|public
name|LinearFuzzyTermsEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|text
operator|=
operator|new
name|int
index|[
name|termLength
operator|-
name|realPrefixLength
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termText
argument_list|,
name|realPrefixLength
argument_list|,
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|String
name|prefix
init|=
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|termText
argument_list|,
literal|0
argument_list|,
name|realPrefixLength
argument_list|)
decl_stmt|;
name|prefixBytesRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|d
operator|=
operator|new
name|int
index|[
name|this
operator|.
name|text
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|this
operator|.
name|p
operator|=
operator|new
name|int
index|[
name|this
operator|.
name|text
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|setInitialSeekTerm
argument_list|(
name|prefixBytesRef
argument_list|)
expr_stmt|;
block|}
DECL|field|prefixBytesRef
specifier|private
specifier|final
name|BytesRef
name|prefixBytesRef
decl_stmt|;
comment|// used for unicode conversion from BytesRef byte[] to int[]
DECL|field|utf32
specifier|private
specifier|final
name|IntsRef
name|utf32
init|=
operator|new
name|IntsRef
argument_list|(
literal|20
argument_list|)
decl_stmt|;
comment|/**      * The termCompare method in FuzzyTermEnum uses Levenshtein distance to       * calculate the distance between the given term and the comparing term.       */
annotation|@
name|Override
DECL|method|accept
specifier|protected
specifier|final
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefixBytesRef
argument_list|)
condition|)
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF32
argument_list|(
name|term
argument_list|,
name|utf32
argument_list|)
expr_stmt|;
specifier|final
name|float
name|similarity
init|=
name|similarity
argument_list|(
name|utf32
operator|.
name|ints
argument_list|,
name|realPrefixLength
argument_list|,
name|utf32
operator|.
name|length
operator|-
name|realPrefixLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|similarity
operator|>
name|minSimilarity
condition|)
block|{
name|boostAtt
operator|.
name|setBoost
argument_list|(
operator|(
name|similarity
operator|-
name|minSimilarity
operator|)
operator|*
name|scale_factor
argument_list|)
expr_stmt|;
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
comment|/******************************      * Compute Levenshtein distance      ******************************/
comment|/**      *<p>Similarity returns a number that is 1.0f or less (including negative numbers)      * based on how similar the Term is compared to a target term.  It returns      * exactly 0.0f when      *<pre>      *    editDistance&gt; maximumEditDistance</pre>      * Otherwise it returns:      *<pre>      *    1 - (editDistance / length)</pre>      * where length is the length of the shortest term (text or target) including a      * prefix that are identical and editDistance is the Levenshtein distance for      * the two words.</p>      *      *<p>Embedded within this algorithm is a fail-fast Levenshtein distance      * algorithm.  The fail-fast algorithm differs from the standard Levenshtein      * distance algorithm in that it is aborted if it is discovered that the      * minimum distance between the words is greater than some threshold.      *      *<p>To calculate the maximum distance threshold we use the following formula:      *<pre>      *     (1 - minimumSimilarity) * length</pre>      * where length is the shortest term including any prefix that is not part of the      * similarity comparison.  This formula was derived by solving for what maximum value      * of distance returns false for the following statements:      *<pre>      *   similarity = 1 - ((float)distance / (float) (prefixLength + Math.min(textlen, targetlen)));      *   return (similarity> minimumSimilarity);</pre>      * where distance is the Levenshtein distance for the two words.      *</p>      *<p>Levenshtein distance (also known as edit distance) is a measure of similarity      * between two strings where the distance is measured as the number of character      * deletions, insertions or substitutions required to transform one string to      * the other string.      * @param target the target word or phrase      * @return the similarity,  0.0 or less indicates that it matches less than the required      * threshold and 1.0 indicates that the text and target are identical      */
DECL|method|similarity
specifier|private
specifier|final
name|float
name|similarity
parameter_list|(
specifier|final
name|int
index|[]
name|target
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
specifier|final
name|int
name|m
init|=
name|length
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|text
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
comment|//we don't have anything to compare.  That means if we just add
comment|//the letters for m we get the new word
return|return
name|realPrefixLength
operator|==
literal|0
condition|?
literal|0.0f
else|:
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|m
operator|/
name|realPrefixLength
operator|)
return|;
block|}
if|if
condition|(
name|m
operator|==
literal|0
condition|)
block|{
return|return
name|realPrefixLength
operator|==
literal|0
condition|?
literal|0.0f
else|:
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|n
operator|/
name|realPrefixLength
operator|)
return|;
block|}
specifier|final
name|int
name|maxDistance
init|=
name|calculateMaxDistance
argument_list|(
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxDistance
operator|<
name|Math
operator|.
name|abs
argument_list|(
name|m
operator|-
name|n
argument_list|)
condition|)
block|{
comment|//just adding the characters of m to n or vice-versa results in
comment|//too many edits
comment|//for example "pre" length is 3 and "prefixes" length is 8.  We can see that
comment|//given this optimal circumstance, the edit distance cannot be less than 5.
comment|//which is 8-3 or more precisely Math.abs(3-8).
comment|//if our maximum edit distance is 4, then we can discard this word
comment|//without looking at it.
return|return
name|Float
operator|.
name|NEGATIVE_INFINITY
return|;
block|}
comment|// init matrix d
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
operator|++
name|i
control|)
block|{
name|p
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
comment|// start computing edit distance
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
operator|++
name|j
control|)
block|{
comment|// iterates through target
name|int
name|bestPossibleEditDistance
init|=
name|m
decl_stmt|;
specifier|final
name|int
name|t_j
init|=
name|target
index|[
name|offset
operator|+
name|j
operator|-
literal|1
index|]
decl_stmt|;
comment|// jth character of t
name|d
index|[
literal|0
index|]
operator|=
name|j
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
operator|++
name|i
control|)
block|{
comment|// iterates through text
comment|// minimum of cell to the left+1, to the top+1, diagonally left and up +(0|1)
if|if
condition|(
name|t_j
operator|!=
name|text
index|[
name|i
operator|-
literal|1
index|]
condition|)
block|{
name|d
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
argument_list|,
name|p
index|[
name|i
index|]
argument_list|)
argument_list|,
name|p
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|d
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|,
name|p
index|[
name|i
index|]
operator|+
literal|1
argument_list|)
argument_list|,
name|p
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|bestPossibleEditDistance
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bestPossibleEditDistance
argument_list|,
name|d
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|//After calculating row i, the best possible edit distance
comment|//can be found by found by finding the smallest value in a given column.
comment|//If the bestPossibleEditDistance is greater than the max distance, abort.
if|if
condition|(
name|j
operator|>
name|maxDistance
operator|&&
name|bestPossibleEditDistance
operator|>
name|maxDistance
condition|)
block|{
comment|//equal is okay, but not greater
comment|//the closest the target can be to the text is just too far away.
comment|//this target is leaving the party early.
return|return
name|Float
operator|.
name|NEGATIVE_INFINITY
return|;
block|}
comment|// copy current distance counts to 'previous row' distance counts: swap p and d
name|int
name|_d
index|[]
init|=
name|p
decl_stmt|;
name|p
operator|=
name|d
expr_stmt|;
name|d
operator|=
name|_d
expr_stmt|;
block|}
comment|// our last action in the above loop was to switch d and p, so p now
comment|// actually has the most recent cost counts
comment|// this will return less than 0.0 when the edit distance is
comment|// greater than the number of characters in the shorter word.
comment|// but this was the formula that was previously used in FuzzyTermEnum,
comment|// so it has not been changed (even though minimumSimilarity must be
comment|// greater than 0.0)
return|return
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|p
index|[
name|n
index|]
operator|/
call|(
name|float
call|)
argument_list|(
name|realPrefixLength
operator|+
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|m
argument_list|)
argument_list|)
operator|)
return|;
block|}
comment|/**      * The max Distance is the maximum Levenshtein distance for the text      * compared to some other value that results in score that is      * better than the minimum similarity.      * @param m the length of the "other value"      * @return the maximum levenshtein distance that we care about      */
DECL|method|calculateMaxDistance
specifier|private
name|int
name|calculateMaxDistance
parameter_list|(
name|int
name|m
parameter_list|)
block|{
return|return
name|raw
condition|?
name|maxEdits
else|:
name|Math
operator|.
name|min
argument_list|(
name|maxEdits
argument_list|,
call|(
name|int
call|)
argument_list|(
operator|(
literal|1
operator|-
name|minSimilarity
operator|)
operator|*
operator|(
name|Math
operator|.
name|min
argument_list|(
name|text
operator|.
name|length
argument_list|,
name|m
argument_list|)
operator|+
name|realPrefixLength
operator|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

