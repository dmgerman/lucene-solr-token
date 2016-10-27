begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|Arrays
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|PostingsEnum
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
name|DocIdSetIterator
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
name|SparseFixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|ClassificationEvaluation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  *   Returns an AnalyticsQuery implementation that performs  *   one Gradient Descent iteration of a result set to train a  *   logistic regression model  *  *   The TextLogitStream provides the parallel iterative framework for this class.  **/
end_comment

begin_class
DECL|class|TextLogisticRegressionQParserPlugin
specifier|public
class|class
name|TextLogisticRegressionQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"tlogit"
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|TextLogisticRegressionQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
DECL|class|TextLogisticRegressionQParser
specifier|private
specifier|static
class|class
name|TextLogisticRegressionQParser
extends|extends
name|QParser
block|{
DECL|method|TextLogisticRegressionQParser
name|TextLogisticRegressionQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
block|{
name|String
name|fs
init|=
name|params
operator|.
name|get
argument_list|(
literal|"feature"
argument_list|)
decl_stmt|;
name|String
index|[]
name|terms
init|=
name|params
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
name|ws
init|=
name|params
operator|.
name|get
argument_list|(
literal|"weights"
argument_list|)
decl_stmt|;
name|String
name|dfsStr
init|=
name|params
operator|.
name|get
argument_list|(
literal|"idfs"
argument_list|)
decl_stmt|;
name|int
name|iteration
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"iteration"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|outcome
init|=
name|params
operator|.
name|get
argument_list|(
literal|"outcome"
argument_list|)
decl_stmt|;
name|int
name|positiveLabel
init|=
name|params
operator|.
name|getInt
argument_list|(
literal|"positiveLabel"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|double
name|threshold
init|=
name|params
operator|.
name|getDouble
argument_list|(
literal|"threshold"
argument_list|,
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|alpha
init|=
name|params
operator|.
name|getDouble
argument_list|(
literal|"alpha"
argument_list|,
literal|0.01
argument_list|)
decl_stmt|;
name|double
index|[]
name|idfs
init|=
operator|new
name|double
index|[
name|terms
operator|.
name|length
index|]
decl_stmt|;
name|String
index|[]
name|idfsArr
init|=
name|dfsStr
operator|.
name|split
argument_list|(
literal|","
argument_list|)
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
name|idfsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|idfs
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|idfsArr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|double
index|[]
name|weights
init|=
operator|new
name|double
index|[
name|terms
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|ws
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|wa
init|=
name|ws
operator|.
name|split
argument_list|(
literal|","
argument_list|)
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
name|wa
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|weights
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|wa
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|weights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|weights
index|[
name|i
index|]
operator|=
literal|1.0d
expr_stmt|;
block|}
block|}
name|TrainingParams
name|input
init|=
operator|new
name|TrainingParams
argument_list|(
name|fs
argument_list|,
name|terms
argument_list|,
name|idfs
argument_list|,
name|outcome
argument_list|,
name|weights
argument_list|,
name|iteration
argument_list|,
name|alpha
argument_list|,
name|positiveLabel
argument_list|,
name|threshold
argument_list|)
decl_stmt|;
return|return
operator|new
name|TextLogisticRegressionQuery
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
DECL|class|TextLogisticRegressionQuery
specifier|private
specifier|static
class|class
name|TextLogisticRegressionQuery
extends|extends
name|AnalyticsQuery
block|{
DECL|field|trainingParams
specifier|private
name|TrainingParams
name|trainingParams
decl_stmt|;
DECL|method|TextLogisticRegressionQuery
specifier|public
name|TextLogisticRegressionQuery
parameter_list|(
name|TrainingParams
name|trainingParams
parameter_list|)
block|{
name|this
operator|.
name|trainingParams
operator|=
name|trainingParams
expr_stmt|;
block|}
DECL|method|getAnalyticsCollector
specifier|public
name|DelegatingCollector
name|getAnalyticsCollector
parameter_list|(
name|ResponseBuilder
name|rbsp
parameter_list|,
name|IndexSearcher
name|indexSearcher
parameter_list|)
block|{
return|return
operator|new
name|TextLogisticRegressionCollector
argument_list|(
name|rbsp
argument_list|,
name|indexSearcher
argument_list|,
name|trainingParams
argument_list|)
return|;
block|}
block|}
DECL|class|TextLogisticRegressionCollector
specifier|private
specifier|static
class|class
name|TextLogisticRegressionCollector
extends|extends
name|DelegatingCollector
block|{
DECL|field|trainingParams
specifier|private
name|TrainingParams
name|trainingParams
decl_stmt|;
DECL|field|leafReader
specifier|private
name|LeafReader
name|leafReader
decl_stmt|;
DECL|field|workingDeltas
specifier|private
name|double
index|[]
name|workingDeltas
decl_stmt|;
DECL|field|classificationEvaluation
specifier|private
name|ClassificationEvaluation
name|classificationEvaluation
decl_stmt|;
DECL|field|weights
specifier|private
name|double
index|[]
name|weights
decl_stmt|;
DECL|field|rbsp
specifier|private
name|ResponseBuilder
name|rbsp
decl_stmt|;
DECL|field|leafOutcomeValue
specifier|private
name|NumericDocValues
name|leafOutcomeValue
decl_stmt|;
DECL|field|totalError
specifier|private
name|double
name|totalError
decl_stmt|;
DECL|field|positiveDocsSet
specifier|private
name|SparseFixedBitSet
name|positiveDocsSet
decl_stmt|;
DECL|field|docsSet
specifier|private
name|SparseFixedBitSet
name|docsSet
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|method|TextLogisticRegressionCollector
name|TextLogisticRegressionCollector
parameter_list|(
name|ResponseBuilder
name|rbsp
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|TrainingParams
name|trainingParams
parameter_list|)
block|{
name|this
operator|.
name|trainingParams
operator|=
name|trainingParams
expr_stmt|;
name|this
operator|.
name|workingDeltas
operator|=
operator|new
name|double
index|[
name|trainingParams
operator|.
name|weights
operator|.
name|length
index|]
expr_stmt|;
name|this
operator|.
name|weights
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|trainingParams
operator|.
name|weights
argument_list|,
name|trainingParams
operator|.
name|weights
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|rbsp
operator|=
name|rbsp
expr_stmt|;
name|this
operator|.
name|classificationEvaluation
operator|=
operator|new
name|ClassificationEvaluation
argument_list|()
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|positiveDocsSet
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|docsSet
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNextReader
specifier|public
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doSetNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|leafReader
operator|=
name|context
operator|.
name|reader
argument_list|()
expr_stmt|;
name|leafOutcomeValue
operator|=
name|leafReader
operator|.
name|getNumericDocValues
argument_list|(
name|trainingParams
operator|.
name|outcome
argument_list|)
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|valuesDocID
init|=
name|leafOutcomeValue
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|valuesDocID
operator|<
name|doc
condition|)
block|{
name|valuesDocID
operator|=
name|leafOutcomeValue
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|int
name|outcome
decl_stmt|;
if|if
condition|(
name|valuesDocID
operator|==
name|doc
condition|)
block|{
name|outcome
operator|=
operator|(
name|int
operator|)
name|leafOutcomeValue
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|outcome
operator|=
literal|0
expr_stmt|;
block|}
name|outcome
operator|=
name|trainingParams
operator|.
name|positiveLabel
operator|==
name|outcome
condition|?
literal|1
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|outcome
operator|==
literal|1
condition|)
block|{
name|positiveDocsSet
operator|.
name|set
argument_list|(
name|context
operator|.
name|docBase
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
name|docsSet
operator|.
name|set
argument_list|(
name|context
operator|.
name|docBase
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|double
index|[]
argument_list|>
name|docVectors
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|.
name|terms
argument_list|(
name|trainingParams
operator|.
name|feature
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|PostingsEnum
name|postingsEnum
init|=
literal|null
decl_stmt|;
name|int
name|termIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|termStr
range|:
name|trainingParams
operator|.
name|terms
control|)
block|{
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|(
name|termStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|postingsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|postingsEnum
argument_list|)
expr_stmt|;
while|while
condition|(
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|docId
init|=
name|postingsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|docsSet
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|double
index|[]
name|vector
init|=
name|docVectors
operator|.
name|get
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|==
literal|null
condition|)
block|{
name|vector
operator|=
operator|new
name|double
index|[
name|trainingParams
operator|.
name|terms
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|vector
index|[
literal|0
index|]
operator|=
literal|1.0
expr_stmt|;
name|docVectors
operator|.
name|put
argument_list|(
name|docId
argument_list|,
name|vector
argument_list|)
expr_stmt|;
block|}
name|vector
index|[
name|termIndex
operator|+
literal|1
index|]
operator|=
name|trainingParams
operator|.
name|idfs
index|[
name|termIndex
index|]
operator|*
operator|(
literal|1.0
operator|+
name|Math
operator|.
name|log
argument_list|(
name|postingsEnum
operator|.
name|freq
argument_list|()
argument_list|)
operator|)
expr_stmt|;
block|}
block|}
block|}
name|termIndex
operator|++
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|double
index|[]
argument_list|>
name|entry
range|:
name|docVectors
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|double
index|[]
name|vector
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|outcome
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|positiveDocsSet
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|outcome
operator|=
literal|1
expr_stmt|;
block|}
name|double
name|sig
init|=
name|sigmoid
argument_list|(
name|sum
argument_list|(
name|multiply
argument_list|(
name|vector
argument_list|,
name|weights
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|error
init|=
name|sig
operator|-
name|outcome
decl_stmt|;
name|double
name|lastSig
init|=
name|sigmoid
argument_list|(
name|sum
argument_list|(
name|multiply
argument_list|(
name|vector
argument_list|,
name|trainingParams
operator|.
name|weights
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|totalError
operator|+=
name|Math
operator|.
name|abs
argument_list|(
name|lastSig
operator|-
name|outcome
argument_list|)
expr_stmt|;
name|classificationEvaluation
operator|.
name|count
argument_list|(
name|outcome
argument_list|,
name|lastSig
operator|>=
name|trainingParams
operator|.
name|threshold
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|workingDeltas
operator|=
name|multiply
argument_list|(
name|error
operator|*
name|trainingParams
operator|.
name|alpha
argument_list|,
name|vector
argument_list|)
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
name|workingDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|weights
index|[
name|i
index|]
operator|-=
name|workingDeltas
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|NamedList
name|analytics
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|rbsp
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"logit"
argument_list|,
name|analytics
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|outWeights
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Double
name|d
range|:
name|weights
control|)
block|{
name|outWeights
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|analytics
operator|.
name|add
argument_list|(
literal|"weights"
argument_list|,
name|outWeights
argument_list|)
expr_stmt|;
name|analytics
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
name|totalError
argument_list|)
expr_stmt|;
name|analytics
operator|.
name|add
argument_list|(
literal|"evaluation"
argument_list|,
name|classificationEvaluation
operator|.
name|toMap
argument_list|()
argument_list|)
expr_stmt|;
name|analytics
operator|.
name|add
argument_list|(
literal|"feature"
argument_list|,
name|trainingParams
operator|.
name|feature
argument_list|)
expr_stmt|;
name|analytics
operator|.
name|add
argument_list|(
literal|"positiveLabel"
argument_list|,
name|trainingParams
operator|.
name|positiveLabel
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|delegate
operator|instanceof
name|DelegatingCollector
condition|)
block|{
operator|(
operator|(
name|DelegatingCollector
operator|)
name|this
operator|.
name|delegate
operator|)
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sigmoid
specifier|private
name|double
name|sigmoid
parameter_list|(
name|double
name|in
parameter_list|)
block|{
name|double
name|d
init|=
literal|1.0
operator|/
operator|(
literal|1
operator|+
name|Math
operator|.
name|exp
argument_list|(
operator|-
name|in
argument_list|)
operator|)
decl_stmt|;
return|return
name|d
return|;
block|}
DECL|method|multiply
specifier|private
name|double
index|[]
name|multiply
parameter_list|(
name|double
index|[]
name|vals
parameter_list|,
name|double
index|[]
name|weights
parameter_list|)
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
name|vals
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|workingDeltas
index|[
name|i
index|]
operator|=
name|vals
index|[
name|i
index|]
operator|*
name|weights
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|workingDeltas
return|;
block|}
DECL|method|multiply
specifier|private
name|double
index|[]
name|multiply
parameter_list|(
name|double
name|d
parameter_list|,
name|double
index|[]
name|vals
parameter_list|)
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
name|vals
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|workingDeltas
index|[
name|i
index|]
operator|=
name|vals
index|[
name|i
index|]
operator|*
name|d
expr_stmt|;
block|}
return|return
name|workingDeltas
return|;
block|}
DECL|method|sum
specifier|private
name|double
name|sum
parameter_list|(
name|double
index|[]
name|vals
parameter_list|)
block|{
name|double
name|d
init|=
literal|0.0d
decl_stmt|;
for|for
control|(
name|double
name|val
range|:
name|vals
control|)
block|{
name|d
operator|+=
name|val
expr_stmt|;
block|}
return|return
name|d
return|;
block|}
block|}
DECL|class|TrainingParams
specifier|private
specifier|static
class|class
name|TrainingParams
block|{
DECL|field|feature
specifier|public
specifier|final
name|String
name|feature
decl_stmt|;
DECL|field|terms
specifier|public
specifier|final
name|String
index|[]
name|terms
decl_stmt|;
DECL|field|idfs
specifier|public
specifier|final
name|double
index|[]
name|idfs
decl_stmt|;
DECL|field|outcome
specifier|public
specifier|final
name|String
name|outcome
decl_stmt|;
DECL|field|weights
specifier|public
specifier|final
name|double
index|[]
name|weights
decl_stmt|;
DECL|field|interation
specifier|public
specifier|final
name|int
name|interation
decl_stmt|;
DECL|field|positiveLabel
specifier|public
specifier|final
name|int
name|positiveLabel
decl_stmt|;
DECL|field|threshold
specifier|public
specifier|final
name|double
name|threshold
decl_stmt|;
DECL|field|alpha
specifier|public
specifier|final
name|double
name|alpha
decl_stmt|;
DECL|method|TrainingParams
specifier|public
name|TrainingParams
parameter_list|(
name|String
name|feature
parameter_list|,
name|String
index|[]
name|terms
parameter_list|,
name|double
index|[]
name|idfs
parameter_list|,
name|String
name|outcome
parameter_list|,
name|double
index|[]
name|weights
parameter_list|,
name|int
name|interation
parameter_list|,
name|double
name|alpha
parameter_list|,
name|int
name|positiveLabel
parameter_list|,
name|double
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|feature
operator|=
name|feature
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|idfs
operator|=
name|idfs
expr_stmt|;
name|this
operator|.
name|outcome
operator|=
name|outcome
expr_stmt|;
name|this
operator|.
name|weights
operator|=
name|weights
expr_stmt|;
name|this
operator|.
name|alpha
operator|=
name|alpha
expr_stmt|;
name|this
operator|.
name|interation
operator|=
name|interation
expr_stmt|;
name|this
operator|.
name|positiveLabel
operator|=
name|positiveLabel
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

