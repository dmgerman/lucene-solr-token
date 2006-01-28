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
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|IndexWriter
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
name|BooleanClause
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
name|BooleanQuery
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
name|Hits
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
name|IndexSearcher
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
name|Query
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
name|TermQuery
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
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<p>  *	Spell Checker class  (Main class)<br/>  * (initially inspired by the David Spencer code).  *</p>  *    *<p>Example Usage:  *   *<pre>  *  SpellChecker spellchecker = new SpellChecker(spellIndexDirectory);  *  // To index a field of a user index:  *  spellchecker.indexDictionary(new LuceneDictionary(my_lucene_reader, a_field));  *  // To index a file containing words:  *  spellchecker.indexDictionary(new PlainTextDictionary(new File("myfile.txt")));  *  String[] suggestions = spellchecker.suggestSimilar("misspelt", 5);  *</pre>  *   * @author Nicolas Maisonneuve  * @version 1.0  */
end_comment

begin_class
DECL|class|SpellChecker
specifier|public
class|class
name|SpellChecker
block|{
comment|/**      * Field name for each word in the ngram index.      */
DECL|field|F_WORD
specifier|public
specifier|static
specifier|final
name|String
name|F_WORD
init|=
literal|"word"
decl_stmt|;
comment|/**      * the spell index      */
DECL|field|spellindex
name|Directory
name|spellindex
decl_stmt|;
comment|/**      * Boost value for start and end grams      */
DECL|field|bStart
specifier|private
name|float
name|bStart
init|=
literal|2.0f
decl_stmt|;
DECL|field|bEnd
specifier|private
name|float
name|bEnd
init|=
literal|1.0f
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|min
name|float
name|min
init|=
literal|0.5f
decl_stmt|;
DECL|method|setSpellIndex
specifier|public
name|void
name|setSpellIndex
parameter_list|(
name|Directory
name|spellindex
parameter_list|)
block|{
name|this
operator|.
name|spellindex
operator|=
name|spellindex
expr_stmt|;
block|}
comment|/**      *  Set the accuracy 0&lt; min&lt; 1; default 0.5      */
DECL|method|setAccuraty
specifier|public
name|void
name|setAccuraty
parameter_list|(
name|float
name|min
parameter_list|)
block|{
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
block|}
DECL|method|SpellChecker
specifier|public
name|SpellChecker
parameter_list|(
name|Directory
name|gramIndex
parameter_list|)
block|{
name|this
operator|.
name|setSpellIndex
argument_list|(
name|gramIndex
argument_list|)
expr_stmt|;
block|}
comment|/**      * Suggest similar words      * @param word String the word you want a spell check done on      * @param num_sug int the number of suggest words      * @throws IOException      * @return String[]      */
DECL|method|suggestSimilar
specifier|public
name|String
index|[]
name|suggestSimilar
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|num_sug
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|suggestSimilar
argument_list|(
name|word
argument_list|,
name|num_sug
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Suggest similar words (restricted or not to a field of a user index)      * @param word String the word you want a spell check done on      * @param num_sug int the number of suggest words      * @param ir the indexReader of the user index (can be null see field param)      * @param field String the field of the user index: if field is not null, the suggested      * words are restricted to the words present in this field.      * @param morePopular boolean return only the suggest words that are more frequent than the searched word      * (only if restricted mode = (indexReader!=null and field!=null)      * @throws IOException      * @return String[] the sorted list of the suggest words with this 2 criteria:      * first criteria: the edit distance, second criteria (only if restricted mode): the popularity      * of the suggest words in the field of the user index      */
DECL|method|suggestSimilar
specifier|public
name|String
index|[]
name|suggestSimilar
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|num_sug
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|morePopular
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TRStringDistance
name|sd
init|=
operator|new
name|TRStringDistance
argument_list|(
name|word
argument_list|)
decl_stmt|;
specifier|final
name|int
name|lengthWord
init|=
name|word
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|goalFreq
init|=
operator|(
name|morePopular
operator|&&
name|ir
operator|!=
literal|null
operator|)
condition|?
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|word
argument_list|)
argument_list|)
else|:
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|morePopular
operator|&&
name|goalFreq
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
name|word
block|}
return|;
comment|// return the word if it exist in the index and i don't want a more popular word
block|}
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|String
index|[]
name|grams
decl_stmt|;
name|String
name|key
decl_stmt|;
for|for
control|(
name|int
name|ng
init|=
name|getMin
argument_list|(
name|lengthWord
argument_list|)
init|;
name|ng
operator|<=
name|getMax
argument_list|(
name|lengthWord
argument_list|)
condition|;
name|ng
operator|++
control|)
block|{
name|key
operator|=
literal|"gram"
operator|+
name|ng
expr_stmt|;
comment|// form key
name|grams
operator|=
name|formGrams
argument_list|(
name|word
argument_list|,
name|ng
argument_list|)
expr_stmt|;
comment|// form word into ngrams (allow dups too)
if|if
condition|(
name|grams
operator|.
name|length
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// hmm
block|}
if|if
condition|(
name|bStart
operator|>
literal|0
condition|)
block|{
comment|// should we boost prefixes?
name|add
argument_list|(
name|query
argument_list|,
literal|"start"
operator|+
name|ng
argument_list|,
name|grams
index|[
literal|0
index|]
argument_list|,
name|bStart
argument_list|)
expr_stmt|;
comment|// matches start of word
block|}
if|if
condition|(
name|bEnd
operator|>
literal|0
condition|)
block|{
comment|// should we boost suffixes
name|add
argument_list|(
name|query
argument_list|,
literal|"end"
operator|+
name|ng
argument_list|,
name|grams
index|[
name|grams
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|bEnd
argument_list|)
expr_stmt|;
comment|// matches end of word
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|grams
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|query
argument_list|,
name|key
argument_list|,
name|grams
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|this
operator|.
name|spellindex
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SuggestWordQueue
name|sugqueue
init|=
operator|new
name|SuggestWordQueue
argument_list|(
name|num_sug
argument_list|)
decl_stmt|;
name|int
name|stop
init|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|()
argument_list|,
literal|10
operator|*
name|num_sug
argument_list|)
decl_stmt|;
comment|// go thru more than 'maxr' matches in case the distance filter triggers
name|SuggestWord
name|sugword
init|=
operator|new
name|SuggestWord
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
name|stop
condition|;
name|i
operator|++
control|)
block|{
name|sugword
operator|.
name|string
operator|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
name|F_WORD
argument_list|)
expr_stmt|;
comment|// get orig word)
if|if
condition|(
name|sugword
operator|.
name|string
operator|.
name|equals
argument_list|(
name|word
argument_list|)
condition|)
block|{
continue|continue;
comment|// don't suggest a word for itself, that would be silly
block|}
comment|//edit distance/normalize with the min word length
name|sugword
operator|.
name|score
operator|=
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|sd
operator|.
name|getDistance
argument_list|(
name|sugword
operator|.
name|string
argument_list|)
operator|/
name|Math
operator|.
name|min
argument_list|(
name|sugword
operator|.
name|string
operator|.
name|length
argument_list|()
argument_list|,
name|lengthWord
argument_list|)
operator|)
expr_stmt|;
if|if
condition|(
name|sugword
operator|.
name|score
operator|<
name|min
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|ir
operator|!=
literal|null
condition|)
block|{
comment|// use the user index
name|sugword
operator|.
name|freq
operator|=
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|sugword
operator|.
name|string
argument_list|)
argument_list|)
expr_stmt|;
comment|// freq in the index
if|if
condition|(
operator|(
name|morePopular
operator|&&
name|goalFreq
operator|>
name|sugword
operator|.
name|freq
operator|)
operator|||
name|sugword
operator|.
name|freq
operator|<
literal|1
condition|)
block|{
comment|// don't suggest a word that is not present in the field
continue|continue;
block|}
block|}
name|sugqueue
operator|.
name|insert
argument_list|(
name|sugword
argument_list|)
expr_stmt|;
if|if
condition|(
name|sugqueue
operator|.
name|size
argument_list|()
operator|==
name|num_sug
condition|)
block|{
comment|//if queue full , maintain the min score
name|min
operator|=
operator|(
operator|(
name|SuggestWord
operator|)
name|sugqueue
operator|.
name|top
argument_list|()
operator|)
operator|.
name|score
expr_stmt|;
block|}
name|sugword
operator|=
operator|new
name|SuggestWord
argument_list|()
expr_stmt|;
block|}
comment|// convert to array string
name|String
index|[]
name|list
init|=
operator|new
name|String
index|[
name|sugqueue
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
name|sugqueue
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
name|list
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|SuggestWord
operator|)
name|sugqueue
operator|.
name|pop
argument_list|()
operator|)
operator|.
name|string
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/**      * Add a clause to a boolean query.      */
DECL|method|add
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|BooleanQuery
name|q
parameter_list|,
name|String
name|k
parameter_list|,
name|String
name|v
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a clause to a boolean query.      */
DECL|method|add
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|BooleanQuery
name|q
parameter_list|,
name|String
name|k
parameter_list|,
name|String
name|v
parameter_list|)
block|{
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Form all ngrams for a given word.      * @param text the word to parse      * @param ng the ngram length e.g. 3      * @return an array of all ngrams in the word and note that duplicates are not removed      */
DECL|method|formGrams
specifier|private
specifier|static
name|String
index|[]
name|formGrams
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|ng
parameter_list|)
block|{
name|int
name|len
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
index|[]
name|res
init|=
operator|new
name|String
index|[
name|len
operator|-
name|ng
operator|+
literal|1
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
name|len
operator|-
name|ng
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|text
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|i
operator|+
name|ng
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|clearIndex
specifier|public
name|void
name|clearIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
operator|.
name|unlock
argument_list|(
name|spellindex
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|spellindex
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Check whether the word exists in the index.      * @param word String      * @throws IOException      * @return true iff the word exists in the index      */
DECL|method|exist
specifier|public
name|boolean
name|exist
parameter_list|(
name|String
name|word
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|spellindex
argument_list|)
expr_stmt|;
block|}
return|return
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|F_WORD
argument_list|,
name|word
argument_list|)
argument_list|)
operator|>
literal|0
return|;
block|}
comment|/**      * Index a Dictionary      * @param dict the dictionary to index      * @throws IOException      */
DECL|method|indexDictionary
specifier|public
name|void
name|indexDictionary
parameter_list|(
name|Dictionary
name|dict
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
operator|.
name|unlock
argument_list|(
name|spellindex
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|spellindex
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
operator|!
name|IndexReader
operator|.
name|indexExists
argument_list|(
name|spellindex
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|300
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|150
argument_list|)
expr_stmt|;
name|Iterator
name|iter
init|=
name|dict
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|word
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|word
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|3
condition|)
block|{
continue|continue;
comment|// too short we bail but "too long" is fine...
block|}
if|if
condition|(
name|this
operator|.
name|exist
argument_list|(
name|word
argument_list|)
condition|)
block|{
comment|// if the word already exist in the gramindex
continue|continue;
block|}
comment|// ok index the word
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|word
argument_list|,
name|getMin
argument_list|(
name|len
argument_list|)
argument_list|,
name|getMax
argument_list|(
name|len
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// close writer
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close reader
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getMin
specifier|private
name|int
name|getMin
parameter_list|(
name|int
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|>
literal|5
condition|)
block|{
return|return
literal|3
return|;
block|}
if|if
condition|(
name|l
operator|==
literal|5
condition|)
block|{
return|return
literal|2
return|;
block|}
return|return
literal|1
return|;
block|}
DECL|method|getMax
specifier|private
name|int
name|getMax
parameter_list|(
name|int
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|>
literal|5
condition|)
block|{
return|return
literal|4
return|;
block|}
if|if
condition|(
name|l
operator|==
literal|5
condition|)
block|{
return|return
literal|3
return|;
block|}
return|return
literal|2
return|;
block|}
DECL|method|createDocument
specifier|private
specifier|static
name|Document
name|createDocument
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|ng1
parameter_list|,
name|int
name|ng2
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|F_WORD
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// orig term
name|addGram
argument_list|(
name|text
argument_list|,
name|doc
argument_list|,
name|ng1
argument_list|,
name|ng2
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|addGram
specifier|private
specifier|static
name|void
name|addGram
parameter_list|(
name|String
name|text
parameter_list|,
name|Document
name|doc
parameter_list|,
name|int
name|ng1
parameter_list|,
name|int
name|ng2
parameter_list|)
block|{
name|int
name|len
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ng
init|=
name|ng1
init|;
name|ng
operator|<=
name|ng2
condition|;
name|ng
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"gram"
operator|+
name|ng
decl_stmt|;
name|String
name|end
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
operator|<
name|len
operator|-
name|ng
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|String
name|gram
init|=
name|text
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|i
operator|+
name|ng
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|key
argument_list|,
name|gram
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"start"
operator|+
name|ng
argument_list|,
name|gram
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|end
operator|=
name|gram
expr_stmt|;
block|}
if|if
condition|(
name|end
operator|!=
literal|null
condition|)
block|{
comment|// may not be present if len==ng1
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"end"
operator|+
name|ng
argument_list|,
name|end
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

