begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|MultiTermQuery
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
name|automaton
operator|.
name|LevenshteinAutomata
import|;
end_import

begin_comment
comment|/**  * Simple automaton-based spellchecker.  *<p>  * Candidates are presented directly from the term dictionary, based on  * Levenshtein distance. This is an alternative to {@link SpellChecker}  * if you are using an edit-distance-like metric such as Levenshtein  * or {@link JaroWinklerDistance}.  *<p>  * A practical benefit of this spellchecker is that it requires no additional  * datastructures (neither in RAM nor on disk) to do its work.  *   * @see LevenshteinAutomata  * @see FuzzyTermsEnum  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DirectSpellChecker
specifier|public
class|class
name|DirectSpellChecker
block|{
comment|/** The default StringDistance, Levenshtein distance implemented internally    *  via {@link LevenshteinAutomata}.    *<p>    *  Note: this is the fastest distance metric, because Levenshtein is used    *  to draw candidates from the term dictionary: this just re-uses the scoring.    *<p>    *  Note also that this metric differs in subtle ways from {@link LevenshteinDistance}:    *<ul>    *<li> This metric treats full unicode codepoints as characters, but    *         LevenshteinDistance calculates based on UTF-16 code units.    *<li> This metric scales raw edit distances into a floating point score    *         differently than LevenshteinDistance: the scaling is based upon the    *         shortest of the two terms instead of the longest.    *</ul>    */
DECL|field|INTERNAL_LEVENSHTEIN
specifier|public
specifier|static
specifier|final
name|StringDistance
name|INTERNAL_LEVENSHTEIN
init|=
operator|new
name|StringDistance
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|getDistance
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not for external use."
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
comment|/** maximum edit distance for candidate terms */
DECL|field|maxEdits
specifier|private
name|int
name|maxEdits
init|=
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
decl_stmt|;
comment|/** minimum prefix for candidate terms */
DECL|field|minPrefix
specifier|private
name|int
name|minPrefix
init|=
literal|1
decl_stmt|;
comment|/** maximum number of top-N inspections per suggestion */
DECL|field|maxInspections
specifier|private
name|int
name|maxInspections
init|=
literal|5
decl_stmt|;
comment|/** minimum accuracy for a term to match */
DECL|field|accuracy
specifier|private
name|float
name|accuracy
init|=
name|SpellChecker
operator|.
name|DEFAULT_ACCURACY
decl_stmt|;
comment|/** value in [0..1] (or absolute number>=1) representing the minimum     * number of documents (of the total) where a term should appear. */
DECL|field|thresholdFrequency
specifier|private
name|float
name|thresholdFrequency
init|=
literal|0f
decl_stmt|;
comment|/** minimum length of a query word to return suggestions */
DECL|field|minQueryLength
specifier|private
name|int
name|minQueryLength
init|=
literal|4
decl_stmt|;
comment|/** value in [0..1] (or absolute number>=1) representing the maximum    *  number of documents (of the total) a query term can appear in to    *  be corrected. */
DECL|field|maxQueryFrequency
specifier|private
name|float
name|maxQueryFrequency
init|=
literal|0.01f
decl_stmt|;
comment|/** true if the spellchecker should lowercase terms */
DECL|field|lowerCaseTerms
specifier|private
name|boolean
name|lowerCaseTerms
init|=
literal|true
decl_stmt|;
comment|/** the comparator to use */
DECL|field|comparator
specifier|private
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comparator
init|=
name|SuggestWordQueue
operator|.
name|DEFAULT_COMPARATOR
decl_stmt|;
comment|/** the string distance to use */
DECL|field|distance
specifier|private
name|StringDistance
name|distance
init|=
name|INTERNAL_LEVENSHTEIN
decl_stmt|;
comment|/** Get the maximum number of Levenshtein edit-distances to draw    *  candidate terms from. */
DECL|method|getMaxEdits
specifier|public
name|int
name|getMaxEdits
parameter_list|()
block|{
return|return
name|maxEdits
return|;
block|}
comment|/** Sets the maximum number of Levenshtein edit-distances to draw    *  candidate terms from. This value can be 1 or 2. The default is 2.    *<p>    *  Note: a large number of spelling errors occur with an edit distance    *  of 1, by setting this value to 1 you can increase both performance    *  and precision at the cost of recall.    */
DECL|method|setMaxEdits
specifier|public
name|void
name|setMaxEdits
parameter_list|(
name|int
name|maxEdits
parameter_list|)
block|{
if|if
condition|(
name|maxEdits
argument_list|<
literal|1
operator|||
name|maxEdits
argument_list|>
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Invalid maxEdits"
argument_list|)
throw|;
name|this
operator|.
name|maxEdits
operator|=
name|maxEdits
expr_stmt|;
block|}
comment|/**    * Get the minimal number of characters that must match exactly    */
DECL|method|getMinPrefix
specifier|public
name|int
name|getMinPrefix
parameter_list|()
block|{
return|return
name|minPrefix
return|;
block|}
comment|/**    * Sets the minimal number of initial characters (default: 1)     * that must match exactly.    *<p>    * This can improve both performance and accuracy of results,    * as misspellings are commonly not the first character.    */
DECL|method|setMinPrefix
specifier|public
name|void
name|setMinPrefix
parameter_list|(
name|int
name|minPrefix
parameter_list|)
block|{
name|this
operator|.
name|minPrefix
operator|=
name|minPrefix
expr_stmt|;
block|}
comment|/**    * Get the maximum number of top-N inspections per suggestion    */
DECL|method|getMaxInspections
specifier|public
name|int
name|getMaxInspections
parameter_list|()
block|{
return|return
name|maxInspections
return|;
block|}
comment|/**    * Set the maximum number of top-N inspections (default: 5) per suggestion.    *<p>    * Increasing this number can improve the accuracy of results, at the cost     * of performance.    */
DECL|method|setMaxInspections
specifier|public
name|void
name|setMaxInspections
parameter_list|(
name|int
name|maxInspections
parameter_list|)
block|{
name|this
operator|.
name|maxInspections
operator|=
name|maxInspections
expr_stmt|;
block|}
comment|/**    * Get the minimal accuracy from the StringDistance for a match    */
DECL|method|getAccuracy
specifier|public
name|float
name|getAccuracy
parameter_list|()
block|{
return|return
name|accuracy
return|;
block|}
comment|/**    * Set the minimal accuracy required (default: 0.5f) from a StringDistance     * for a suggestion match.    */
DECL|method|setAccuracy
specifier|public
name|void
name|setAccuracy
parameter_list|(
name|float
name|accuracy
parameter_list|)
block|{
name|this
operator|.
name|accuracy
operator|=
name|accuracy
expr_stmt|;
block|}
comment|/**    * Get the minimal threshold of documents a term must appear for a match    */
DECL|method|getThresholdFrequency
specifier|public
name|float
name|getThresholdFrequency
parameter_list|()
block|{
return|return
name|thresholdFrequency
return|;
block|}
comment|/**    * Set the minimal threshold of documents a term must appear for a match.    *<p>    * This can improve quality by only suggesting high-frequency terms. Note that    * very high values might decrease performance slightly, by forcing the spellchecker    * to draw more candidates from the term dictionary, but a practical value such    * as<code>1</code> can be very useful towards improving quality.    *<p>    * This can be specified as a relative percentage of documents such as 0.5f,    * or it can be specified as an absolute whole document frequency, such as 4f.    * Absolute document frequencies may not be fractional.    */
DECL|method|setThresholdFrequency
specifier|public
name|void
name|setThresholdFrequency
parameter_list|(
name|float
name|thresholdFrequency
parameter_list|)
block|{
if|if
condition|(
name|thresholdFrequency
operator|>=
literal|1f
operator|&&
name|thresholdFrequency
operator|!=
operator|(
name|int
operator|)
name|thresholdFrequency
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Fractional absolute document frequencies are not allowed"
argument_list|)
throw|;
name|this
operator|.
name|thresholdFrequency
operator|=
name|thresholdFrequency
expr_stmt|;
block|}
comment|/** Get the minimum length of a query term needed to return suggestions */
DECL|method|getMinQueryLength
specifier|public
name|int
name|getMinQueryLength
parameter_list|()
block|{
return|return
name|minQueryLength
return|;
block|}
comment|/**     * Set the minimum length of a query term (default: 4) needed to return suggestions.     *<p>    * Very short query terms will often cause only bad suggestions with any distance    * metric.    */
DECL|method|setMinQueryLength
specifier|public
name|void
name|setMinQueryLength
parameter_list|(
name|int
name|minQueryLength
parameter_list|)
block|{
name|this
operator|.
name|minQueryLength
operator|=
name|minQueryLength
expr_stmt|;
block|}
comment|/**    * Get the maximum threshold of documents a query term can appear in order    * to provide suggestions.    */
DECL|method|getMaxQueryFrequency
specifier|public
name|float
name|getMaxQueryFrequency
parameter_list|()
block|{
return|return
name|maxQueryFrequency
return|;
block|}
comment|/**    * Set the maximum threshold (default: 0.01f) of documents a query term can     * appear in order to provide suggestions.    *<p>    * Very high-frequency terms are typically spelled correctly. Additionally,    * this can increase performance as it will do no work for the common case    * of correctly-spelled input terms.    *<p>    * This can be specified as a relative percentage of documents such as 0.5f,    * or it can be specified as an absolute whole document frequency, such as 4f.    * Absolute document frequencies may not be fractional.    */
DECL|method|setMaxQueryFrequency
specifier|public
name|void
name|setMaxQueryFrequency
parameter_list|(
name|float
name|maxQueryFrequency
parameter_list|)
block|{
if|if
condition|(
name|maxQueryFrequency
operator|>=
literal|1f
operator|&&
name|maxQueryFrequency
operator|!=
operator|(
name|int
operator|)
name|maxQueryFrequency
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Fractional absolute document frequencies are not allowed"
argument_list|)
throw|;
name|this
operator|.
name|maxQueryFrequency
operator|=
name|maxQueryFrequency
expr_stmt|;
block|}
comment|/** true if the spellchecker should lowercase terms */
DECL|method|getLowerCaseTerms
specifier|public
name|boolean
name|getLowerCaseTerms
parameter_list|()
block|{
return|return
name|lowerCaseTerms
return|;
block|}
comment|/**     * True if the spellchecker should lowercase terms (default: true)    *<p>    * This is a convenience method, if your index field has more complicated    * analysis (such as StandardTokenizer removing punctuation), its probably    * better to turn this off, and instead run your query terms through your    * Analyzer first.    *<p>    * If this option is not on, case differences count as an edit!     */
DECL|method|setLowerCaseTerms
specifier|public
name|void
name|setLowerCaseTerms
parameter_list|(
name|boolean
name|lowerCaseTerms
parameter_list|)
block|{
name|this
operator|.
name|lowerCaseTerms
operator|=
name|lowerCaseTerms
expr_stmt|;
block|}
comment|/**    * Get the current comparator in use.    */
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
comment|/**    * Set the comparator for sorting suggestions.    * The default is {@link SuggestWordQueue#DEFAULT_COMPARATOR}    */
DECL|method|setComparator
specifier|public
name|void
name|setComparator
parameter_list|(
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
comment|/**    * Get the string distance metric in use.    */
DECL|method|getDistance
specifier|public
name|StringDistance
name|getDistance
parameter_list|()
block|{
return|return
name|distance
return|;
block|}
comment|/**    * Set the string distance metric.    * The default is {@link #INTERNAL_LEVENSHTEIN}    *<p>    * Note: because this spellchecker draws its candidates from the    * term dictionary using Levenshtein, it works best with an edit-distance-like    * string metric. If you use a different metric than the default,    * you might want to consider increasing {@link #setMaxInspections(int)}    * to draw more candidates for your metric to rank.    */
DECL|method|setDistance
specifier|public
name|void
name|setDistance
parameter_list|(
name|StringDistance
name|distance
parameter_list|)
block|{
name|this
operator|.
name|distance
operator|=
name|distance
expr_stmt|;
block|}
comment|/**    * Calls {@link #suggestSimilar(Term, int, IndexReader, boolean)     *       suggestSimilar(term, numSug, ir, false)    */
DECL|method|suggestSimilar
specifier|public
name|SuggestWord
index|[]
name|suggestSimilar
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|numSug
parameter_list|,
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|suggestSimilar
argument_list|(
name|term
argument_list|,
name|numSug
argument_list|,
name|ir
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Calls {@link #suggestSimilar(Term, int, IndexReader, boolean, float)     *       suggestSimilar(term, numSug, ir, morePopular, this.accuracy)    */
DECL|method|suggestSimilar
specifier|public
name|SuggestWord
index|[]
name|suggestSimilar
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|numSug
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|boolean
name|morePopular
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|suggestSimilar
argument_list|(
name|term
argument_list|,
name|numSug
argument_list|,
name|ir
argument_list|,
name|morePopular
argument_list|,
name|accuracy
argument_list|)
return|;
block|}
comment|/**    * Suggest similar words.    *     *<p>Unlike {@link SpellChecker}, the similarity used to fetch the most    * relevant terms is an edit distance, therefore typically a low value    * for numSug will work very well.    *     * @param term Term you want to spell check on    * @param numSug the maximum number of suggested words    * @param ir IndexReader to find terms from    * @param morePopular return only suggested words that are as frequent or more frequent than the searched word    * @param accuracy return only suggested words that match with this similarity    * @return sorted list of the suggested words according to the comparator    * @throws IOException    */
DECL|method|suggestSimilar
specifier|public
name|SuggestWord
index|[]
name|suggestSimilar
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|numSug
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|boolean
name|morePopular
parameter_list|,
name|float
name|accuracy
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|text
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|minQueryLength
operator|>
literal|0
operator|&&
name|text
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
operator|<
name|minQueryLength
condition|)
return|return
operator|new
name|SuggestWord
index|[
literal|0
index|]
return|;
if|if
condition|(
name|lowerCaseTerms
condition|)
name|term
operator|=
name|term
operator|.
name|createTerm
argument_list|(
name|text
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|docfreq
init|=
name|ir
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|// see line 341 of spellchecker. this is certainly very very nice for perf,
comment|// but is it really the right way to go?
if|if
condition|(
operator|!
name|morePopular
operator|&&
name|docfreq
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|SuggestWord
index|[
literal|0
index|]
return|;
block|}
name|int
name|maxDoc
init|=
name|ir
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxQueryFrequency
operator|>=
literal|1f
operator|&&
name|docfreq
operator|>
name|maxQueryFrequency
condition|)
block|{
return|return
operator|new
name|SuggestWord
index|[
literal|0
index|]
return|;
block|}
elseif|else
if|if
condition|(
name|docfreq
operator|>
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|maxQueryFrequency
operator|*
operator|(
name|float
operator|)
name|maxDoc
argument_list|)
condition|)
block|{
return|return
operator|new
name|SuggestWord
index|[
literal|0
index|]
return|;
block|}
if|if
condition|(
operator|!
name|morePopular
condition|)
name|docfreq
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|thresholdFrequency
operator|>=
literal|1f
condition|)
block|{
name|docfreq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|docfreq
argument_list|,
operator|(
name|int
operator|)
name|thresholdFrequency
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thresholdFrequency
operator|>
literal|0f
condition|)
block|{
name|docfreq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|docfreq
argument_list|,
call|(
name|int
call|)
argument_list|(
name|thresholdFrequency
operator|*
operator|(
name|float
operator|)
name|maxDoc
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|ScoreTerm
argument_list|>
name|terms
init|=
literal|null
decl_stmt|;
name|int
name|inspections
init|=
name|numSug
operator|*
name|maxInspections
decl_stmt|;
comment|// try ed=1 first, in case we get lucky
name|terms
operator|=
name|suggestSimilar
argument_list|(
name|term
argument_list|,
name|inspections
argument_list|,
name|ir
argument_list|,
name|docfreq
argument_list|,
literal|1
argument_list|,
name|accuracy
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxEdits
operator|>
literal|1
operator|&&
name|terms
operator|.
name|size
argument_list|()
operator|<
name|inspections
condition|)
block|{
name|HashSet
argument_list|<
name|ScoreTerm
argument_list|>
name|moreTerms
init|=
operator|new
name|HashSet
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|()
decl_stmt|;
name|moreTerms
operator|.
name|addAll
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|moreTerms
operator|.
name|addAll
argument_list|(
name|suggestSimilar
argument_list|(
name|term
argument_list|,
name|inspections
argument_list|,
name|ir
argument_list|,
name|docfreq
argument_list|,
name|maxEdits
argument_list|,
name|accuracy
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|=
name|moreTerms
expr_stmt|;
block|}
comment|// create the suggestword response, sort it, and trim it to size.
name|SuggestWord
name|suggestions
index|[]
init|=
operator|new
name|SuggestWord
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
name|suggestions
operator|.
name|length
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|ScoreTerm
name|s
range|:
name|terms
control|)
block|{
name|SuggestWord
name|suggestion
init|=
operator|new
name|SuggestWord
argument_list|()
decl_stmt|;
name|suggestion
operator|.
name|string
operator|=
name|s
operator|.
name|termAsString
operator|!=
literal|null
condition|?
name|s
operator|.
name|termAsString
else|:
name|s
operator|.
name|term
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
name|suggestion
operator|.
name|score
operator|=
name|s
operator|.
name|score
expr_stmt|;
name|suggestion
operator|.
name|freq
operator|=
name|s
operator|.
name|docfreq
expr_stmt|;
name|suggestions
index|[
name|index
operator|--
index|]
operator|=
name|suggestion
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|suggestions
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|(
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|numSug
operator|<
name|suggestions
operator|.
name|length
condition|)
block|{
name|SuggestWord
name|trimmed
index|[]
init|=
operator|new
name|SuggestWord
index|[
name|numSug
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|suggestions
argument_list|,
literal|0
argument_list|,
name|trimmed
argument_list|,
literal|0
argument_list|,
name|numSug
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|trimmed
expr_stmt|;
block|}
return|return
name|suggestions
return|;
block|}
DECL|method|suggestSimilar
specifier|private
name|Collection
argument_list|<
name|ScoreTerm
argument_list|>
name|suggestSimilar
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|numSug
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|int
name|docfreq
parameter_list|,
name|int
name|editDistance
parameter_list|,
name|float
name|accuracy
parameter_list|)
throws|throws
name|IOException
block|{
name|FuzzyTermsEnum
name|e
init|=
operator|new
name|FuzzyTermsEnum
argument_list|(
name|ir
argument_list|,
name|term
argument_list|,
name|editDistance
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|minPrefix
argument_list|,
name|editDistance
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|PriorityQueue
argument_list|<
name|ScoreTerm
argument_list|>
name|stQueue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|()
decl_stmt|;
name|BytesRef
name|queryTerm
init|=
operator|new
name|BytesRef
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|candidateTerm
decl_stmt|;
name|ScoreTerm
name|st
init|=
operator|new
name|ScoreTerm
argument_list|()
decl_stmt|;
name|MultiTermQuery
operator|.
name|BoostAttribute
name|boostAtt
init|=
name|e
operator|.
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|MultiTermQuery
operator|.
name|BoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|candidateTerm
operator|=
name|e
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|float
name|boost
init|=
name|boostAtt
operator|.
name|getBoost
argument_list|()
decl_stmt|;
comment|// ignore uncompetitive hits
if|if
condition|(
name|stQueue
operator|.
name|size
argument_list|()
operator|>=
name|numSug
operator|&&
name|boost
operator|<=
name|stQueue
operator|.
name|peek
argument_list|()
operator|.
name|boost
condition|)
continue|continue;
comment|// ignore exact match of the same term
if|if
condition|(
name|queryTerm
operator|.
name|bytesEquals
argument_list|(
name|candidateTerm
argument_list|)
condition|)
continue|continue;
name|int
name|df
init|=
name|e
operator|.
name|docFreq
argument_list|()
decl_stmt|;
comment|// check docFreq if required
if|if
condition|(
name|df
operator|<=
name|docfreq
condition|)
continue|continue;
specifier|final
name|float
name|score
decl_stmt|;
specifier|final
name|String
name|termAsString
decl_stmt|;
if|if
condition|(
name|distance
operator|==
name|INTERNAL_LEVENSHTEIN
condition|)
block|{
comment|// delay creating strings until the end
name|termAsString
operator|=
literal|null
expr_stmt|;
comment|// undo FuzzyTermsEnum's scale factor for a real scaled lev score
name|score
operator|=
name|boost
operator|/
name|e
operator|.
name|getScaleFactor
argument_list|()
operator|+
name|e
operator|.
name|getMinSimilarity
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|termAsString
operator|=
name|candidateTerm
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
name|score
operator|=
name|distance
operator|.
name|getDistance
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|termAsString
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|score
operator|<
name|accuracy
condition|)
continue|continue;
comment|// add new entry in PQ
name|st
operator|.
name|term
operator|=
operator|new
name|BytesRef
argument_list|(
name|candidateTerm
argument_list|)
expr_stmt|;
name|st
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|st
operator|.
name|docfreq
operator|=
name|df
expr_stmt|;
name|st
operator|.
name|termAsString
operator|=
name|termAsString
expr_stmt|;
name|st
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|stQueue
operator|.
name|offer
argument_list|(
name|st
argument_list|)
expr_stmt|;
comment|// possibly drop entries from queue
name|st
operator|=
operator|(
name|stQueue
operator|.
name|size
argument_list|()
operator|>
name|numSug
operator|)
condition|?
name|stQueue
operator|.
name|poll
argument_list|()
else|:
operator|new
name|ScoreTerm
argument_list|()
expr_stmt|;
name|boostAtt
operator|.
name|setMaxNonCompetitiveBoost
argument_list|(
operator|(
name|stQueue
operator|.
name|size
argument_list|()
operator|>=
name|numSug
operator|)
condition|?
name|stQueue
operator|.
name|peek
argument_list|()
operator|.
name|boost
else|:
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
return|return
name|stQueue
return|;
block|}
DECL|class|ScoreTerm
specifier|private
specifier|static
class|class
name|ScoreTerm
implements|implements
name|Comparable
argument_list|<
name|ScoreTerm
argument_list|>
block|{
DECL|field|term
specifier|public
name|BytesRef
name|term
decl_stmt|;
DECL|field|boost
specifier|public
name|float
name|boost
decl_stmt|;
DECL|field|docfreq
specifier|public
name|int
name|docfreq
decl_stmt|;
DECL|field|termAsString
specifier|public
name|String
name|termAsString
decl_stmt|;
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|ScoreTerm
name|other
parameter_list|)
block|{
if|if
condition|(
name|term
operator|.
name|bytesEquals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
return|return
literal|0
return|;
comment|// consistent with equals
if|if
condition|(
name|this
operator|.
name|boost
operator|==
name|other
operator|.
name|boost
condition|)
return|return
name|other
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|this
operator|.
name|term
argument_list|)
return|;
else|else
return|return
name|Float
operator|.
name|compare
argument_list|(
name|this
operator|.
name|boost
argument_list|,
name|other
operator|.
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|term
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|term
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
name|ScoreTerm
name|other
init|=
operator|(
name|ScoreTerm
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|term
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
name|term
operator|.
name|bytesEquals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

