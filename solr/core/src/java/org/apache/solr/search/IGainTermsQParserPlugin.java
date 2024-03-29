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
name|TreeSet
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

begin_class
DECL|class|IGainTermsQParserPlugin
specifier|public
class|class
name|IGainTermsQParserPlugin
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
literal|"igain"
decl_stmt|;
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
name|IGainTermsQParser
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
DECL|class|IGainTermsQParser
specifier|private
specifier|static
class|class
name|IGainTermsQParser
extends|extends
name|QParser
block|{
DECL|method|IGainTermsQParser
specifier|public
name|IGainTermsQParser
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
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|field
init|=
name|getParam
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|String
name|outcome
init|=
name|getParam
argument_list|(
literal|"outcome"
argument_list|)
decl_stmt|;
name|int
name|numTerms
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getParam
argument_list|(
literal|"numTerms"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|positiveLabel
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getParam
argument_list|(
literal|"positiveLabel"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|IGainTermsQuery
argument_list|(
name|field
argument_list|,
name|outcome
argument_list|,
name|positiveLabel
argument_list|,
name|numTerms
argument_list|)
return|;
block|}
block|}
DECL|class|IGainTermsQuery
specifier|private
specifier|static
class|class
name|IGainTermsQuery
extends|extends
name|AnalyticsQuery
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|outcome
specifier|private
name|String
name|outcome
decl_stmt|;
DECL|field|numTerms
specifier|private
name|int
name|numTerms
decl_stmt|;
DECL|field|positiveLabel
specifier|private
name|int
name|positiveLabel
decl_stmt|;
DECL|method|IGainTermsQuery
specifier|public
name|IGainTermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|outcome
parameter_list|,
name|int
name|positiveLabel
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|outcome
operator|=
name|outcome
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|positiveLabel
operator|=
name|positiveLabel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAnalyticsCollector
specifier|public
name|DelegatingCollector
name|getAnalyticsCollector
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
block|{
return|return
operator|new
name|IGainTermsCollector
argument_list|(
name|rb
argument_list|,
name|searcher
argument_list|,
name|field
argument_list|,
name|outcome
argument_list|,
name|positiveLabel
argument_list|,
name|numTerms
argument_list|)
return|;
block|}
block|}
DECL|class|IGainTermsCollector
specifier|private
specifier|static
class|class
name|IGainTermsCollector
extends|extends
name|DelegatingCollector
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|outcome
specifier|private
name|String
name|outcome
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|rb
specifier|private
name|ResponseBuilder
name|rb
decl_stmt|;
DECL|field|positiveLabel
specifier|private
name|int
name|positiveLabel
decl_stmt|;
DECL|field|numTerms
specifier|private
name|int
name|numTerms
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|leafOutcomeValue
specifier|private
name|NumericDocValues
name|leafOutcomeValue
decl_stmt|;
DECL|field|positiveSet
specifier|private
name|SparseFixedBitSet
name|positiveSet
decl_stmt|;
DECL|field|negativeSet
specifier|private
name|SparseFixedBitSet
name|negativeSet
decl_stmt|;
DECL|field|numPositiveDocs
specifier|private
name|int
name|numPositiveDocs
decl_stmt|;
DECL|method|IGainTermsCollector
specifier|public
name|IGainTermsCollector
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|outcome
parameter_list|,
name|int
name|positiveLabel
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
name|this
operator|.
name|rb
operator|=
name|rb
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|outcome
operator|=
name|outcome
expr_stmt|;
name|this
operator|.
name|positiveSet
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|negativeSet
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|positiveLabel
operator|=
name|positiveLabel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
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
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|leafOutcomeValue
operator|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|outcome
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
operator|++
name|count
expr_stmt|;
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
name|value
decl_stmt|;
if|if
condition|(
name|valuesDocID
operator|==
name|doc
condition|)
block|{
name|value
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
name|value
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
name|positiveLabel
condition|)
block|{
name|positiveSet
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
name|numPositiveDocs
operator|++
expr_stmt|;
block|}
else|else
block|{
name|negativeSet
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
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Double
argument_list|>
name|analytics
init|=
operator|new
name|NamedList
argument_list|<
name|Double
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|topFreq
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|allFreq
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"featuredTerms"
argument_list|,
name|analytics
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"docFreq"
argument_list|,
name|topFreq
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"numDocs"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|TreeSet
argument_list|<
name|TermWithScore
argument_list|>
name|topTerms
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|double
name|numDocs
init|=
name|count
decl_stmt|;
name|double
name|pc
init|=
name|numPositiveDocs
operator|/
name|numDocs
decl_stmt|;
name|double
name|entropyC
init|=
name|binaryEntropy
argument_list|(
name|pc
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
operator|(
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
operator|)
operator|.
name|getSlowAtomicReader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|==
literal|null
condition|?
name|TermsEnum
operator|.
name|EMPTY
else|:
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|PostingsEnum
name|postingsEnum
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
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
name|int
name|xc
init|=
literal|0
decl_stmt|;
name|int
name|nc
init|=
literal|0
decl_stmt|;
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
if|if
condition|(
name|positiveSet
operator|.
name|get
argument_list|(
name|postingsEnum
operator|.
name|docID
argument_list|()
argument_list|)
condition|)
block|{
name|xc
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|negativeSet
operator|.
name|get
argument_list|(
name|postingsEnum
operator|.
name|docID
argument_list|()
argument_list|)
condition|)
block|{
name|nc
operator|++
expr_stmt|;
block|}
block|}
name|int
name|docFreq
init|=
name|xc
operator|+
name|nc
decl_stmt|;
name|double
name|entropyContainsTerm
init|=
name|binaryEntropy
argument_list|(
operator|(
name|double
operator|)
name|xc
operator|/
name|docFreq
argument_list|)
decl_stmt|;
name|double
name|entropyNotContainsTerm
init|=
name|binaryEntropy
argument_list|(
call|(
name|double
call|)
argument_list|(
name|numPositiveDocs
operator|-
name|xc
argument_list|)
operator|/
operator|(
name|numDocs
operator|-
name|docFreq
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
name|double
name|score
init|=
name|entropyC
operator|-
operator|(
operator|(
name|docFreq
operator|/
name|numDocs
operator|)
operator|*
name|entropyContainsTerm
operator|+
operator|(
literal|1.0
operator|-
name|docFreq
operator|/
name|numDocs
operator|)
operator|*
name|entropyNotContainsTerm
operator|)
decl_stmt|;
name|topFreq
operator|.
name|add
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
if|if
condition|(
name|topTerms
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|topTerms
operator|.
name|add
argument_list|(
operator|new
name|TermWithScore
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|topTerms
operator|.
name|first
argument_list|()
operator|.
name|score
operator|<
name|score
condition|)
block|{
name|topTerms
operator|.
name|pollFirst
argument_list|()
expr_stmt|;
name|topTerms
operator|.
name|add
argument_list|(
operator|new
name|TermWithScore
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|TermWithScore
name|topTerm
range|:
name|topTerms
control|)
block|{
name|analytics
operator|.
name|add
argument_list|(
name|topTerm
operator|.
name|term
argument_list|,
name|topTerm
operator|.
name|score
argument_list|)
expr_stmt|;
name|topFreq
operator|.
name|add
argument_list|(
name|topTerm
operator|.
name|term
argument_list|,
name|allFreq
operator|.
name|get
argument_list|(
name|topTerm
operator|.
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|binaryEntropy
specifier|private
name|double
name|binaryEntropy
parameter_list|(
name|double
name|prob
parameter_list|)
block|{
if|if
condition|(
name|prob
operator|==
literal|0
operator|||
name|prob
operator|==
literal|1
condition|)
return|return
literal|0
return|;
return|return
operator|(
operator|-
literal|1
operator|*
name|prob
operator|*
name|Math
operator|.
name|log
argument_list|(
name|prob
argument_list|)
operator|)
operator|+
operator|(
operator|-
literal|1
operator|*
operator|(
literal|1.0
operator|-
name|prob
operator|)
operator|*
name|Math
operator|.
name|log
argument_list|(
literal|1.0
operator|-
name|prob
argument_list|)
operator|)
return|;
block|}
block|}
DECL|class|TermWithScore
specifier|private
specifier|static
class|class
name|TermWithScore
implements|implements
name|Comparable
argument_list|<
name|TermWithScore
argument_list|>
block|{
DECL|field|term
specifier|public
specifier|final
name|String
name|term
decl_stmt|;
DECL|field|score
specifier|public
specifier|final
name|double
name|score
decl_stmt|;
DECL|method|TermWithScore
specifier|public
name|TermWithScore
parameter_list|(
name|String
name|term
parameter_list|,
name|double
name|score
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|term
operator|.
name|hashCode
argument_list|()
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
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TermWithScore
name|other
init|=
operator|(
name|TermWithScore
operator|)
name|obj
decl_stmt|;
return|return
name|other
operator|.
name|term
operator|.
name|equals
argument_list|(
name|this
operator|.
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TermWithScore
name|o
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Double
operator|.
name|compare
argument_list|(
name|this
operator|.
name|score
argument_list|,
name|o
operator|.
name|score
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
return|return
name|this
operator|.
name|term
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|term
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|cmp
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

