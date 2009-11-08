begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|index
operator|.
name|TermEnum
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
name|Analyzer
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
name|TokenStream
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
name|StopFilter
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
name|Version
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
name|io
operator|.
name|Reader
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
comment|/**  * An {@link Analyzer} used primarily at query time to wrap another analyzer and provide a layer of protection  * which prevents very common words from being passed into queries.   *<p>  * For very large indexes the cost  * of reading TermDocs for a very common word can be  high. This analyzer was created after experience with  * a 38 million doc index which had a term in around 50% of docs and was causing TermQueries for   * this term to take 2 seconds.  *</p>  *<p>  * Use the various "addStopWords" methods in this class to automate the identification and addition of   * stop words found in an already existing index.  *</p>  */
end_comment

begin_class
DECL|class|QueryAutoStopWordAnalyzer
specifier|public
class|class
name|QueryAutoStopWordAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|delegate
name|Analyzer
name|delegate
decl_stmt|;
DECL|field|stopWordsPerField
name|HashMap
name|stopWordsPerField
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|//The default maximum percentage (40%) of index documents which
comment|//can contain a term, after which the term is considered to be a stop word.
DECL|field|defaultMaxDocFreqPercent
specifier|public
specifier|static
specifier|final
name|float
name|defaultMaxDocFreqPercent
init|=
literal|0.4f
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Initializes this analyzer with the Analyzer object that actually produces the tokens    *    * @param delegate The choice of {@link Analyzer} that is used to produce the token stream which needs filtering    */
DECL|method|QueryAutoStopWordAnalyzer
specifier|public
name|QueryAutoStopWordAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|setOverridesTokenStreamMethod
argument_list|(
name|QueryAutoStopWordAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Automatically adds stop words for all fields with terms exceeding the defaultMaxDocFreqPercent    *    * @param reader The {@link IndexReader} which will be consulted to identify potential stop words that    *               exceed the required document frequency    * @return The number of stop words identified.    * @throws IOException    */
DECL|method|addStopWords
specifier|public
name|int
name|addStopWords
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addStopWords
argument_list|(
name|reader
argument_list|,
name|defaultMaxDocFreqPercent
argument_list|)
return|;
block|}
comment|/**    * Automatically adds stop words for all fields with terms exceeding the maxDocFreqPercent    *    * @param reader     The {@link IndexReader} which will be consulted to identify potential stop words that    *                   exceed the required document frequency    * @param maxDocFreq The maximum number of index documents which can contain a term, after which    *                   the term is considered to be a stop word    * @return The number of stop words identified.    * @throws IOException    */
DECL|method|addStopWords
specifier|public
name|int
name|addStopWords
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|maxDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numStopWords
init|=
literal|0
decl_stmt|;
name|Collection
name|fieldNames
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|fieldNames
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|numStopWords
operator|+=
name|addStopWords
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
name|maxDocFreq
argument_list|)
expr_stmt|;
block|}
return|return
name|numStopWords
return|;
block|}
comment|/**    * Automatically adds stop words for all fields with terms exceeding the maxDocFreqPercent    *    * @param reader        The {@link IndexReader} which will be consulted to identify potential stop words that    *                      exceed the required document frequency    * @param maxPercentDocs The maximum percentage (between 0.0 and 1.0) of index documents which    *                      contain a term, after which the word is considered to be a stop word.    * @return The number of stop words identified.    * @throws IOException    */
DECL|method|addStopWords
specifier|public
name|int
name|addStopWords
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|float
name|maxPercentDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numStopWords
init|=
literal|0
decl_stmt|;
name|Collection
name|fieldNames
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|fieldNames
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|numStopWords
operator|+=
name|addStopWords
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
name|maxPercentDocs
argument_list|)
expr_stmt|;
block|}
return|return
name|numStopWords
return|;
block|}
comment|/**    * Automatically adds stop words for the given field with terms exceeding the maxPercentDocs    *    * @param reader         The {@link IndexReader} which will be consulted to identify potential stop words that    *                       exceed the required document frequency    * @param fieldName      The field for which stopwords will be added    * @param maxPercentDocs The maximum percentage (between 0.0 and 1.0) of index documents which    *                       contain a term, after which the word is considered to be a stop word.    * @return The number of stop words identified.    * @throws IOException    */
DECL|method|addStopWords
specifier|public
name|int
name|addStopWords
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|float
name|maxPercentDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addStopWords
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
call|(
name|int
call|)
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|*
name|maxPercentDocs
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Automatically adds stop words for the given field with terms exceeding the maxPercentDocs    *    * @param reader     The {@link IndexReader} which will be consulted to identify potential stop words that    *                   exceed the required document frequency    * @param fieldName  The field for which stopwords will be added    * @param maxDocFreq The maximum number of index documents which    *                   can contain a term, after which the term is considered to be a stop word.    * @return The number of stop words identified.    * @throws IOException    */
DECL|method|addStopWords
specifier|public
name|int
name|addStopWords
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|maxDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
name|stopWords
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|String
name|internedFieldName
init|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|TermEnum
name|te
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|)
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
name|te
operator|.
name|term
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|internedFieldName
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|te
operator|.
name|docFreq
argument_list|()
operator|>
name|maxDocFreq
condition|)
block|{
name|stopWords
operator|.
name|add
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|te
operator|.
name|next
argument_list|()
condition|)
block|{
break|break;
block|}
name|term
operator|=
name|te
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
name|stopWordsPerField
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
comment|/* if the stopwords for a field are changed,      * then saved streams for that field are erased.      */
name|Map
name|streamMap
init|=
operator|(
name|Map
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streamMap
operator|!=
literal|null
condition|)
name|streamMap
operator|.
name|remove
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|stopWords
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
decl_stmt|;
try|try
block|{
name|result
operator|=
name|delegate
operator|.
name|reusableTokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|result
operator|=
name|delegate
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
name|HashSet
name|stopWords
init|=
operator|(
name|HashSet
operator|)
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|result
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
comment|/* the underlying stream */
DECL|field|wrapped
name|TokenStream
name|wrapped
decl_stmt|;
comment|/*      * when there are no stopwords for the field, refers to wrapped.      * if there stopwords, it is a StopFilter around wrapped.      */
DECL|field|withStopFilter
name|TokenStream
name|withStopFilter
decl_stmt|;
block|}
empty_stmt|;
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|overridesTokenStreamMethod
condition|)
block|{
comment|// LUCENE-1678: force fallback to tokenStream() if we
comment|// have been subclassed and that subclass overrides
comment|// tokenStream but not reusableTokenStream
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
comment|/* map of SavedStreams for each field */
name|Map
name|streamMap
init|=
operator|(
name|Map
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streamMap
operator|==
literal|null
condition|)
block|{
name|streamMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streamMap
argument_list|)
expr_stmt|;
block|}
name|SavedStreams
name|streams
init|=
operator|(
name|SavedStreams
operator|)
name|streamMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
comment|/* an entry for this field does not exist, create one */
name|streams
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|streamMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|streams
operator|.
name|wrapped
operator|=
name|delegate
operator|.
name|reusableTokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
expr_stmt|;
comment|/* if there are any stopwords for the field, save the stopfilter */
name|HashSet
name|stopWords
init|=
operator|(
name|HashSet
operator|)
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
name|streams
operator|.
name|withStopFilter
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|streams
operator|.
name|wrapped
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
else|else
name|streams
operator|.
name|withStopFilter
operator|=
name|streams
operator|.
name|wrapped
expr_stmt|;
block|}
else|else
block|{
comment|/*        * an entry for this field exists, verify the wrapped stream has not        * changed. if it has not, reuse it, otherwise wrap the new stream.        */
name|TokenStream
name|result
init|=
name|delegate
operator|.
name|reusableTokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|streams
operator|.
name|wrapped
condition|)
block|{
comment|/* the wrapped analyzer reused the stream */
name|streams
operator|.
name|withStopFilter
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|/*          * the wrapped analyzer did not. if there are any stopwords for the          * field, create a new StopFilter around the new stream          */
name|streams
operator|.
name|wrapped
operator|=
name|result
expr_stmt|;
name|HashSet
name|stopWords
init|=
operator|(
name|HashSet
operator|)
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
name|streams
operator|.
name|withStopFilter
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|streams
operator|.
name|wrapped
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
else|else
name|streams
operator|.
name|withStopFilter
operator|=
name|streams
operator|.
name|wrapped
expr_stmt|;
block|}
block|}
return|return
name|streams
operator|.
name|withStopFilter
return|;
block|}
comment|/**    * Provides information on which stop words have been identified for a field    *    * @param fieldName The field for which stop words identified in "addStopWords"    *                  method calls will be returned    * @return the stop words identified for a field    */
DECL|method|getStopWords
specifier|public
name|String
index|[]
name|getStopWords
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|String
index|[]
name|result
decl_stmt|;
name|HashSet
name|stopWords
init|=
operator|(
name|HashSet
operator|)
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|(
name|String
index|[]
operator|)
name|stopWords
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|stopWords
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Provides information on which stop words have been identified for all fields    *    * @return the stop words (as terms)    */
DECL|method|getStopWords
specifier|public
name|Term
index|[]
name|getStopWords
parameter_list|()
block|{
name|ArrayList
name|allStopWords
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|stopWordsPerField
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|HashSet
name|stopWords
init|=
operator|(
name|HashSet
operator|)
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|stopWords
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|text
init|=
operator|(
name|String
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|allStopWords
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|Term
index|[]
operator|)
name|allStopWords
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|allStopWords
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

