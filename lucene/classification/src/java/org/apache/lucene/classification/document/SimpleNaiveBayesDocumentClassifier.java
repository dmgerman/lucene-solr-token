begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.classification.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|document
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
name|ArrayList
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
name|LinkedHashMap
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|classification
operator|.
name|ClassificationResult
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
name|classification
operator|.
name|SimpleNaiveBayesClassifier
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
name|LeafReader
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
name|search
operator|.
name|TotalHitCountCollector
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
name|WildcardQuery
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

begin_comment
comment|/**  * A simplistic Lucene based NaiveBayes classifier, see {@code http://en.wikipedia.org/wiki/Naive_Bayes_classifier}  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleNaiveBayesDocumentClassifier
specifier|public
class|class
name|SimpleNaiveBayesDocumentClassifier
extends|extends
name|SimpleNaiveBayesClassifier
implements|implements
name|DocumentClassifier
argument_list|<
name|BytesRef
argument_list|>
block|{
comment|/**    * {@link org.apache.lucene.analysis.Analyzer} to be used for tokenizing document fields    */
DECL|field|field2analyzer
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|field2analyzer
decl_stmt|;
comment|/**    * Creates a new NaiveBayes classifier.    *    * @param leafReader     the reader on the index to be used for classification    * @param query          a {@link org.apache.lucene.search.Query} to eventually filter the docs used for training the classifier, or {@code null}    *                       if all the indexed docs should be used    * @param classFieldName the name of the field used as the output for the classifier NOTE: must not be havely analyzed    *                       as the returned class will be a token indexed for this field    * @param textFieldNames the name of the fields used as the inputs for the classifier, they can contain boosting indication e.g. title^10    */
DECL|method|SimpleNaiveBayesDocumentClassifier
specifier|public
name|SimpleNaiveBayesDocumentClassifier
parameter_list|(
name|LeafReader
name|leafReader
parameter_list|,
name|Query
name|query
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|field2analyzer
parameter_list|,
name|String
modifier|...
name|textFieldNames
parameter_list|)
block|{
name|super
argument_list|(
name|leafReader
argument_list|,
literal|null
argument_list|,
name|query
argument_list|,
name|classFieldName
argument_list|,
name|textFieldNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|field2analyzer
operator|=
name|field2analyzer
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|assignClass
specifier|public
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|assignClass
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|assignNormClasses
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|assignedClass
init|=
literal|null
decl_stmt|;
name|double
name|maxscore
init|=
operator|-
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|c
range|:
name|assignedClasses
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getScore
argument_list|()
operator|>
name|maxscore
condition|)
block|{
name|assignedClass
operator|=
name|c
expr_stmt|;
name|maxscore
operator|=
name|c
operator|.
name|getScore
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|assignedClass
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getClasses
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|assignNormClasses
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|assignedClasses
argument_list|)
expr_stmt|;
return|return
name|assignedClasses
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getClasses
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|Document
name|document
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|assignNormClasses
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|assignedClasses
argument_list|)
expr_stmt|;
return|return
name|assignedClasses
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|assignNormClasses
specifier|private
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignNormClasses
parameter_list|(
name|Document
name|inputDocument
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
index|[]
argument_list|>
argument_list|>
name|fieldName2tokensArray
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldName2boost
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Terms
name|classes
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|classFieldName
argument_list|)
decl_stmt|;
name|TermsEnum
name|classesEnum
init|=
name|classes
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|c
decl_stmt|;
name|analyzeSeedDocument
argument_list|(
name|inputDocument
argument_list|,
name|fieldName2tokensArray
argument_list|,
name|fieldName2boost
argument_list|)
expr_stmt|;
name|int
name|docsWithClassSize
init|=
name|countDocsWithClass
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|classesEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|double
name|classScore
init|=
literal|0
decl_stmt|;
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|this
operator|.
name|classFieldName
argument_list|,
name|c
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|textFieldNames
control|)
block|{
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|tokensArrays
init|=
name|fieldName2tokensArray
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|double
name|fieldScore
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
index|[]
name|fieldTokensArray
range|:
name|tokensArrays
control|)
block|{
name|fieldScore
operator|+=
name|calculateLogPrior
argument_list|(
name|term
argument_list|,
name|docsWithClassSize
argument_list|)
operator|+
name|calculateLogLikelihood
argument_list|(
name|fieldTokensArray
argument_list|,
name|fieldName
argument_list|,
name|term
argument_list|,
name|docsWithClassSize
argument_list|)
operator|*
name|fieldName2boost
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|classScore
operator|+=
name|fieldScore
expr_stmt|;
block|}
name|assignedClasses
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|classScore
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|normClassificationResults
argument_list|(
name|assignedClasses
argument_list|)
return|;
block|}
comment|/**    * This methods performs the analysis for the seed document and extract the boosts if present.    * This is done only one time for the Seed Document.    *    * @param inputDocument         the seed unseen document    * @param fieldName2tokensArray a map that associated to a field name the list of token arrays for all its values    * @param fieldName2boost       a map that associates the boost to the field    * @throws IOException If there is a low-level I/O error    */
DECL|method|analyzeSeedDocument
specifier|private
name|void
name|analyzeSeedDocument
parameter_list|(
name|Document
name|inputDocument
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
index|[]
argument_list|>
argument_list|>
name|fieldName2tokensArray
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldName2boost
parameter_list|)
throws|throws
name|IOException
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
name|textFieldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fieldName
init|=
name|textFieldNames
index|[
name|i
index|]
decl_stmt|;
name|float
name|boost
init|=
literal|1
decl_stmt|;
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|tokenizedValues
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|contains
argument_list|(
literal|"^"
argument_list|)
condition|)
block|{
name|String
index|[]
name|field2boost
init|=
name|fieldName
operator|.
name|split
argument_list|(
literal|"\\^"
argument_list|)
decl_stmt|;
name|fieldName
operator|=
name|field2boost
index|[
literal|0
index|]
expr_stmt|;
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|field2boost
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|Field
index|[]
name|fieldValues
init|=
name|inputDocument
operator|.
name|getFields
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
for|for
control|(
name|Field
name|fieldValue
range|:
name|fieldValues
control|)
block|{
name|TokenStream
name|fieldTokens
init|=
name|fieldValue
operator|.
name|tokenStream
argument_list|(
name|field2analyzer
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
index|[]
name|fieldTokensArray
init|=
name|getTokenArray
argument_list|(
name|fieldTokens
argument_list|)
decl_stmt|;
name|tokenizedValues
operator|.
name|add
argument_list|(
name|fieldTokensArray
argument_list|)
expr_stmt|;
block|}
name|fieldName2tokensArray
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|tokenizedValues
argument_list|)
expr_stmt|;
name|fieldName2boost
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|textFieldNames
index|[
name|i
index|]
operator|=
name|fieldName
expr_stmt|;
block|}
block|}
comment|/**    * Counts the number of documents in the index having at least a value for the 'class' field    *    * @return the no. of documents having a value for the 'class' field    * @throws java.io.IOException If accessing to term vectors or search fails    */
DECL|method|countDocsWithClass
specifier|protected
name|int
name|countDocsWithClass
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|docCount
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|this
operator|.
name|leafReader
argument_list|,
name|this
operator|.
name|classFieldName
argument_list|)
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|docCount
operator|==
operator|-
literal|1
condition|)
block|{
comment|// in case codec doesn't support getDocCount
name|TotalHitCountCollector
name|classQueryCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|WildcardQuery
operator|.
name|WILDCARD_STRING
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|q
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|indexSearcher
operator|.
name|search
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|,
name|classQueryCountCollector
argument_list|)
expr_stmt|;
name|docCount
operator|=
name|classQueryCountCollector
operator|.
name|getTotalHits
argument_list|()
expr_stmt|;
block|}
return|return
name|docCount
return|;
block|}
comment|/**    * Returns a token array from the {@link org.apache.lucene.analysis.TokenStream} in input    *    * @param tokenizedText the tokenized content of a field    * @return a {@code String} array of the resulting tokens    * @throws java.io.IOException If tokenization fails because there is a low-level I/O error    */
DECL|method|getTokenArray
specifier|protected
name|String
index|[]
name|getTokenArray
parameter_list|(
name|TokenStream
name|tokenizedText
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|CharTermAttribute
name|charTermAttribute
init|=
name|tokenizedText
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokenizedText
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tokenizedText
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|charTermAttribute
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tokenizedText
operator|.
name|end
argument_list|()
expr_stmt|;
name|tokenizedText
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * @param tokenizedText the tokenized content of a field    * @param fieldName     the input field name    * @param term          the {@link Term} referring to the class to calculate the score of    * @param docsWithClass the total number of docs that have a class    * @return a normalized score for the class    * @throws IOException If there is a low-level I/O error    */
DECL|method|calculateLogLikelihood
specifier|private
name|double
name|calculateLogLikelihood
parameter_list|(
name|String
index|[]
name|tokenizedText
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docsWithClass
parameter_list|)
throws|throws
name|IOException
block|{
comment|// for each word
name|double
name|result
init|=
literal|0d
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|tokenizedText
control|)
block|{
comment|// search with text:word AND class:c
name|int
name|hits
init|=
name|getWordFreqForClass
argument_list|(
name|word
argument_list|,
name|fieldName
argument_list|,
name|term
argument_list|)
decl_stmt|;
comment|// num : count the no of times the word appears in documents of class c (+1)
name|double
name|num
init|=
name|hits
operator|+
literal|1
decl_stmt|;
comment|// +1 is added because of add 1 smoothing
comment|// den : for the whole dictionary, count the no of times a word appears in documents of class c (+|V|)
name|double
name|den
init|=
name|getTextTermFreqForClass
argument_list|(
name|term
argument_list|,
name|fieldName
argument_list|)
operator|+
name|docsWithClass
decl_stmt|;
comment|// P(w|c) = num/den
name|double
name|wordProbability
init|=
name|num
operator|/
name|den
decl_stmt|;
name|result
operator|+=
name|Math
operator|.
name|log
argument_list|(
name|wordProbability
argument_list|)
expr_stmt|;
block|}
comment|// log(P(d|c)) = log(P(w1|c))+...+log(P(wn|c))
name|double
name|normScore
init|=
name|result
operator|/
operator|(
name|tokenizedText
operator|.
name|length
operator|)
decl_stmt|;
comment|// this is normalized because if not, long text fields will always be more important than short fields
return|return
name|normScore
return|;
block|}
comment|/**    * Returns the average number of unique terms times the number of docs belonging to the input class    *    * @param  term the class term    * @return the average number of unique terms    * @throws java.io.IOException If there is a low-level I/O error    */
DECL|method|getTextTermFreqForClass
specifier|private
name|double
name|getTextTermFreqForClass
parameter_list|(
name|Term
name|term
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|avgNumberOfUniqueTerms
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|leafReader
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|long
name|numPostings
init|=
name|terms
operator|.
name|getSumDocFreq
argument_list|()
decl_stmt|;
comment|// number of term/doc pairs
name|avgNumberOfUniqueTerms
operator|=
name|numPostings
operator|/
operator|(
name|double
operator|)
name|terms
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
comment|// avg # of unique terms per doc
name|int
name|docsWithC
init|=
name|leafReader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
decl_stmt|;
return|return
name|avgNumberOfUniqueTerms
operator|*
name|docsWithC
return|;
comment|// avg # of unique terms in text fields per doc * # docs with c
block|}
comment|/**    * Returns the number of documents of the input class ( from the whole index or from a subset)    * that contains the word ( in a specific field or in all the fields if no one selected)    *    * @param word      the token produced by the analyzer    * @param fieldName the field the word is coming from    * @param term      the class term    * @return number of documents of the input class    * @throws java.io.IOException If there is a low-level I/O error    */
DECL|method|getWordFreqForClass
specifier|private
name|int
name|getWordFreqForClass
parameter_list|(
name|String
name|word
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|subQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|subQuery
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
name|fieldName
argument_list|,
name|word
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
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|subQuery
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|booleanQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|TotalHitCountCollector
name|totalHitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|booleanQuery
operator|.
name|build
argument_list|()
argument_list|,
name|totalHitCountCollector
argument_list|)
expr_stmt|;
return|return
name|totalHitCountCollector
operator|.
name|getTotalHits
argument_list|()
return|;
block|}
DECL|method|calculateLogPrior
specifier|private
name|double
name|calculateLogPrior
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|docsWithClassSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Math
operator|.
name|log
argument_list|(
operator|(
name|double
operator|)
name|docCount
argument_list|(
name|term
argument_list|)
argument_list|)
operator|-
name|Math
operator|.
name|log
argument_list|(
name|docsWithClassSize
argument_list|)
return|;
block|}
DECL|method|docCount
specifier|private
name|int
name|docCount
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|leafReader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
end_class

end_unit

