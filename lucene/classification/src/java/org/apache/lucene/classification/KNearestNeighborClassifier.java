begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
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
name|io
operator|.
name|StringReader
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
name|Collections
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
name|StorableField
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
name|queries
operator|.
name|mlt
operator|.
name|MoreLikeThis
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
name|ScoreDoc
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
name|TopDocs
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
name|search
operator|.
name|similarities
operator|.
name|ClassicSimilarity
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
name|similarities
operator|.
name|Similarity
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
comment|/**  * A k-Nearest Neighbor classifier (see<code>http://en.wikipedia.org/wiki/K-nearest_neighbors</code>) based  * on {@link MoreLikeThis}  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|KNearestNeighborClassifier
specifier|public
class|class
name|KNearestNeighborClassifier
implements|implements
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
block|{
comment|/**    * a {@link MoreLikeThis} instance used to perform MLT queries    */
DECL|field|mlt
specifier|protected
specifier|final
name|MoreLikeThis
name|mlt
decl_stmt|;
comment|/**    * the name of the fields used as the input text    */
DECL|field|textFieldNames
specifier|protected
specifier|final
name|String
index|[]
name|textFieldNames
decl_stmt|;
comment|/**    * the name of the field used as the output text    */
DECL|field|classFieldName
specifier|protected
specifier|final
name|String
name|classFieldName
decl_stmt|;
comment|/**    * an {@link IndexSearcher} used to perform queries    */
DECL|field|indexSearcher
specifier|protected
specifier|final
name|IndexSearcher
name|indexSearcher
decl_stmt|;
comment|/**    * the no. of docs to compare in order to find the nearest neighbor to the input text    */
DECL|field|k
specifier|protected
specifier|final
name|int
name|k
decl_stmt|;
comment|/**    * a {@link Query} used to filter the documents that should be used from this classifier's underlying {@link LeafReader}    */
DECL|field|query
specifier|protected
specifier|final
name|Query
name|query
decl_stmt|;
comment|/**    * Creates a {@link KNearestNeighborClassifier}.    *    * @param leafReader     the reader on the index to be used for classification    * @param analyzer       an {@link Analyzer} used to analyze unseen text    * @param similarity     the {@link Similarity} to be used by the underlying {@link IndexSearcher} or {@code null}    *                       (defaults to {@link org.apache.lucene.search.similarities.ClassicSimilarity})    * @param query          a {@link Query} to eventually filter the docs used for training the classifier, or {@code null}    *                       if all the indexed docs should be used    * @param k              the no. of docs to select in the MLT results to find the nearest neighbor    * @param minDocsFreq    {@link MoreLikeThis#minDocFreq} parameter    * @param minTermFreq    {@link MoreLikeThis#minTermFreq} parameter    * @param classFieldName the name of the field used as the output for the classifier    * @param textFieldNames the name of the fields used as the inputs for the classifier, they can contain boosting indication e.g. title^10    */
DECL|method|KNearestNeighborClassifier
specifier|public
name|KNearestNeighborClassifier
parameter_list|(
name|LeafReader
name|leafReader
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|k
parameter_list|,
name|int
name|minDocsFreq
parameter_list|,
name|int
name|minTermFreq
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|String
modifier|...
name|textFieldNames
parameter_list|)
block|{
name|this
operator|.
name|textFieldNames
operator|=
name|textFieldNames
expr_stmt|;
name|this
operator|.
name|classFieldName
operator|=
name|classFieldName
expr_stmt|;
name|this
operator|.
name|mlt
operator|=
operator|new
name|MoreLikeThis
argument_list|(
name|leafReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|textFieldNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|leafReader
argument_list|)
expr_stmt|;
if|if
condition|(
name|similarity
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|indexSearcher
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|indexSearcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minDocsFreq
operator|>
literal|0
condition|)
block|{
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|minDocsFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minTermFreq
operator|>
literal|0
condition|)
block|{
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|minTermFreq
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|k
operator|=
name|k
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
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|knnResults
init|=
name|knnSearch
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|buildListFromTopDocs
argument_list|(
name|knnResults
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
name|cl
range|:
name|assignedClasses
control|)
block|{
if|if
condition|(
name|cl
operator|.
name|getScore
argument_list|()
operator|>
name|maxscore
condition|)
block|{
name|assignedClass
operator|=
name|cl
expr_stmt|;
name|maxscore
operator|=
name|cl
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
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|knnResults
init|=
name|knnSearch
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|buildListFromTopDocs
argument_list|(
name|knnResults
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
name|String
name|text
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|knnResults
init|=
name|knnSearch
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|assignedClasses
init|=
name|buildListFromTopDocs
argument_list|(
name|knnResults
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
DECL|method|knnSearch
specifier|private
name|TopDocs
name|knnSearch
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
operator|.
name|Builder
name|mltQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|textFieldNames
control|)
block|{
name|String
name|boost
init|=
literal|null
decl_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//terms boost actually helps in MLT queries
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
name|field2boost
index|[
literal|1
index|]
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|mlt
operator|.
name|setBoostFactor
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|boost
argument_list|)
argument_list|)
expr_stmt|;
comment|//if we have a field boost, we add it
block|}
name|mltQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|mlt
operator|.
name|like
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
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
name|mlt
operator|.
name|setBoostFactor
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// restore neutral boost for next field
block|}
name|Query
name|classFieldQuery
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|classFieldName
argument_list|,
literal|"*"
argument_list|)
argument_list|)
decl_stmt|;
name|mltQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|classFieldQuery
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
name|mltQuery
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
return|return
name|indexSearcher
operator|.
name|search
argument_list|(
name|mltQuery
operator|.
name|build
argument_list|()
argument_list|,
name|k
argument_list|)
return|;
block|}
comment|//ranking of classes must be taken in consideration
DECL|method|buildListFromTopDocs
specifier|protected
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|buildListFromTopDocs
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|classCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Double
argument_list|>
name|classBoosts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// this is a boost based on class ranking positions in topDocs
name|float
name|maxScore
init|=
name|topDocs
operator|.
name|getMaxScore
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
name|StorableField
name|storableField
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
name|classFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|storableField
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|cl
init|=
operator|new
name|BytesRef
argument_list|(
name|storableField
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
comment|//update count
name|Integer
name|count
init|=
name|classCounts
operator|.
name|get
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|classCounts
operator|.
name|put
argument_list|(
name|cl
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|classCounts
operator|.
name|put
argument_list|(
name|cl
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//update boost, the boost is based on the best score
name|Double
name|totalBoost
init|=
name|classBoosts
operator|.
name|get
argument_list|(
name|cl
argument_list|)
decl_stmt|;
name|double
name|singleBoost
init|=
name|scoreDoc
operator|.
name|score
operator|/
name|maxScore
decl_stmt|;
if|if
condition|(
name|totalBoost
operator|!=
literal|null
condition|)
block|{
name|classBoosts
operator|.
name|put
argument_list|(
name|cl
argument_list|,
name|totalBoost
operator|+
name|singleBoost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|classBoosts
operator|.
name|put
argument_list|(
name|cl
argument_list|,
name|singleBoost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|returnList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|temporaryList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|sumdoc
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|classCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Integer
name|count
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Double
name|normBoost
init|=
name|classBoosts
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|/
name|count
decl_stmt|;
comment|//the boost is normalized to be 0<b<1
name|temporaryList
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|,
operator|(
name|count
operator|*
name|normBoost
operator|)
operator|/
operator|(
name|double
operator|)
name|k
argument_list|)
argument_list|)
expr_stmt|;
name|sumdoc
operator|+=
name|count
expr_stmt|;
block|}
comment|//correction
if|if
condition|(
name|sumdoc
operator|<
name|k
condition|)
block|{
for|for
control|(
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|cr
range|:
name|temporaryList
control|)
block|{
name|returnList
operator|.
name|add
argument_list|(
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
name|cr
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|cr
operator|.
name|getScore
argument_list|()
operator|*
name|k
operator|/
operator|(
name|double
operator|)
name|sumdoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|returnList
operator|=
name|temporaryList
expr_stmt|;
block|}
return|return
name|returnList
return|;
block|}
block|}
end_class

end_unit

