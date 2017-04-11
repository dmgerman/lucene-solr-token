begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|KNearestNeighborClassifier
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
comment|/**  * A k-Nearest Neighbor Document classifier (see<code>http://en.wikipedia.org/wiki/K-nearest_neighbors</code>) based  * on {@link org.apache.lucene.queries.mlt.MoreLikeThis} .  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|KNearestNeighborDocumentClassifier
specifier|public
class|class
name|KNearestNeighborDocumentClassifier
extends|extends
name|KNearestNeighborClassifier
implements|implements
name|DocumentClassifier
argument_list|<
name|BytesRef
argument_list|>
block|{
comment|/**    * map of per field analyzers    */
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
comment|/**    * Creates a {@link KNearestNeighborClassifier}.    *    * @param indexReader     the reader on the index to be used for classification    * @param similarity     the {@link Similarity} to be used by the underlying {@link IndexSearcher} or {@code null}    *                       (defaults to {@link org.apache.lucene.search.similarities.BM25Similarity})    * @param query          a {@link org.apache.lucene.search.Query} to eventually filter the docs used for training the classifier, or {@code null}    *                       if all the indexed docs should be used    * @param k              the no. of docs to select in the MLT results to find the nearest neighbor    * @param minDocsFreq    {@link org.apache.lucene.queries.mlt.MoreLikeThis#minDocFreq} parameter    * @param minTermFreq    {@link org.apache.lucene.queries.mlt.MoreLikeThis#minTermFreq} parameter    * @param classFieldName the name of the field used as the output for the classifier    * @param field2analyzer map with key a field name and the related {org.apache.lucene.analysis.Analyzer}    * @param textFieldNames the name of the fields used as the inputs for the classifier, they can contain boosting indication e.g. title^10    */
DECL|method|KNearestNeighborDocumentClassifier
specifier|public
name|KNearestNeighborDocumentClassifier
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Similarity
name|similarity
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
name|indexReader
argument_list|,
name|similarity
argument_list|,
literal|null
argument_list|,
name|query
argument_list|,
name|k
argument_list|,
name|minDocsFreq
argument_list|,
name|minTermFreq
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
return|return
name|classifyFromTopDocs
argument_list|(
name|knnSearch
argument_list|(
name|document
argument_list|)
argument_list|)
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
name|TopDocs
name|knnResults
init|=
name|knnSearch
argument_list|(
name|document
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
name|Document
name|document
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
name|document
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
name|max
operator|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|assignedClasses
operator|.
name|size
argument_list|()
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
comment|/**    * Returns the top k results from a More Like This query based on the input document    *    * @param document the document to use for More Like This search    * @return the top results for the MLT query    * @throws IOException If there is a low-level I/O error    */
DECL|method|knnSearch
specifier|private
name|TopDocs
name|knnSearch
parameter_list|(
name|Document
name|document
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
name|String
index|[]
name|fieldValues
init|=
name|document
operator|.
name|getValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// we want always to use the boost coming from TF * IDF of the term
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
comment|// this is an additional multiplicative boost coming from the field boost
block|}
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|field2analyzer
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|fieldContent
range|:
name|fieldValues
control|)
block|{
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
name|fieldContent
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
block|}
end_class

end_unit

