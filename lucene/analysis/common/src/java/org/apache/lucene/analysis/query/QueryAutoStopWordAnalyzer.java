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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AnalyzerWrapper
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
name|core
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
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
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
name|MultiFields
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

begin_comment
comment|/**  * An {@link Analyzer} used primarily at query time to wrap another analyzer and provide a layer of protection  * which prevents very common words from being passed into queries.   *<p>  * For very large indexes the cost  * of reading TermDocs for a very common word can be  high. This analyzer was created after experience with  * a 38 million doc index which had a term in around 50% of docs and was causing TermQueries for   * this term to take 2 seconds.  *</p>  */
end_comment

begin_class
DECL|class|QueryAutoStopWordAnalyzer
specifier|public
specifier|final
class|class
name|QueryAutoStopWordAnalyzer
extends|extends
name|AnalyzerWrapper
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Analyzer
name|delegate
decl_stmt|;
DECL|field|stopWordsPerField
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|stopWordsPerField
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
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
comment|/**    * Creates a new QueryAutoStopWordAnalyzer with stopwords calculated for all    * indexed fields from terms with a document frequency percentage greater than    * {@link #defaultMaxDocFreqPercent}    *    * @param matchVersion Version to be used in {@link StopFilter}    * @param delegate Analyzer whose TokenStream will be filtered    * @param indexReader IndexReader to identify the stopwords from    * @throws IOException Can be thrown while reading from the IndexReader    */
DECL|method|QueryAutoStopWordAnalyzer
specifier|public
name|QueryAutoStopWordAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|delegate
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|delegate
argument_list|,
name|indexReader
argument_list|,
name|defaultMaxDocFreqPercent
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new QueryAutoStopWordAnalyzer with stopwords calculated for all    * indexed fields from terms with a document frequency greater than the given    * maxDocFreq    *    * @param matchVersion Version to be used in {@link StopFilter}    * @param delegate Analyzer whose TokenStream will be filtered    * @param indexReader IndexReader to identify the stopwords from    * @param maxDocFreq Document frequency terms should be above in order to be stopwords    * @throws IOException Can be thrown while reading from the IndexReader    */
DECL|method|QueryAutoStopWordAnalyzer
specifier|public
name|QueryAutoStopWordAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|delegate
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|int
name|maxDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|delegate
argument_list|,
name|indexReader
argument_list|,
name|MultiFields
operator|.
name|getIndexedFields
argument_list|(
name|indexReader
argument_list|)
argument_list|,
name|maxDocFreq
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new QueryAutoStopWordAnalyzer with stopwords calculated for all    * indexed fields from terms with a document frequency percentage greater than    * the given maxPercentDocs    *    * @param matchVersion Version to be used in {@link StopFilter}    * @param delegate Analyzer whose TokenStream will be filtered    * @param indexReader IndexReader to identify the stopwords from    * @param maxPercentDocs The maximum percentage (between 0.0 and 1.0) of index documents which    *                      contain a term, after which the word is considered to be a stop word    * @throws IOException Can be thrown while reading from the IndexReader    */
DECL|method|QueryAutoStopWordAnalyzer
specifier|public
name|QueryAutoStopWordAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|delegate
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|float
name|maxPercentDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|delegate
argument_list|,
name|indexReader
argument_list|,
name|MultiFields
operator|.
name|getIndexedFields
argument_list|(
name|indexReader
argument_list|)
argument_list|,
name|maxPercentDocs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new QueryAutoStopWordAnalyzer with stopwords calculated for the    * given selection of fields from terms with a document frequency percentage    * greater than the given maxPercentDocs    *    * @param matchVersion Version to be used in {@link StopFilter}    * @param delegate Analyzer whose TokenStream will be filtered    * @param indexReader IndexReader to identify the stopwords from    * @param fields Selection of fields to calculate stopwords for    * @param maxPercentDocs The maximum percentage (between 0.0 and 1.0) of index documents which    *                      contain a term, after which the word is considered to be a stop word    * @throws IOException Can be thrown while reading from the IndexReader    */
DECL|method|QueryAutoStopWordAnalyzer
specifier|public
name|QueryAutoStopWordAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|delegate
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|float
name|maxPercentDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|delegate
argument_list|,
name|indexReader
argument_list|,
name|fields
argument_list|,
call|(
name|int
call|)
argument_list|(
name|indexReader
operator|.
name|numDocs
argument_list|()
operator|*
name|maxPercentDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new QueryAutoStopWordAnalyzer with stopwords calculated for the    * given selection of fields from terms with a document frequency greater than    * the given maxDocFreq    *    * @param matchVersion Version to be used in {@link StopFilter}    * @param delegate Analyzer whose TokenStream will be filtered    * @param indexReader IndexReader to identify the stopwords from    * @param fields Selection of fields to calculate stopwords for    * @param maxDocFreq Document frequency terms should be above in order to be stopwords    * @throws IOException Can be thrown while reading from the IndexReader    */
DECL|method|QueryAutoStopWordAnalyzer
specifier|public
name|QueryAutoStopWordAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|delegate
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|int
name|maxDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|delegate
operator|.
name|getReuseStrategy
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stopWords
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|indexReader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|te
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|text
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|te
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
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
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|text
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|stopWords
operator|.
name|add
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|stopWordsPerField
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getWrappedAnalyzer
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|delegate
return|;
block|}
annotation|@
name|Override
DECL|method|wrapComponents
specifier|protected
name|TokenStreamComponents
name|wrapComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stopWords
init|=
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
operator|==
literal|null
condition|)
block|{
return|return
name|components
return|;
block|}
name|StopFilter
name|stopFilter
init|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|components
operator|.
name|getTokenStream
argument_list|()
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|components
operator|.
name|getTokenizer
argument_list|()
argument_list|,
name|stopFilter
argument_list|)
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
name|Set
argument_list|<
name|String
argument_list|>
name|stopWords
init|=
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
name|stopWords
operator|!=
literal|null
condition|?
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
else|:
operator|new
name|String
index|[
literal|0
index|]
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
name|List
argument_list|<
name|Term
argument_list|>
name|allStopWords
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|stopWordsPerField
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stopWords
init|=
name|stopWordsPerField
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|stopWords
control|)
block|{
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

