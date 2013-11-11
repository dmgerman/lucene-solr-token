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
name|AtomicReader
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
name|util
operator|.
name|BytesRef
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
name|StringReader
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
name|Map
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
DECL|field|mlt
specifier|private
name|MoreLikeThis
name|mlt
decl_stmt|;
DECL|field|textFieldNames
specifier|private
name|String
index|[]
name|textFieldNames
decl_stmt|;
DECL|field|classFieldName
specifier|private
name|String
name|classFieldName
decl_stmt|;
DECL|field|indexSearcher
specifier|private
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|k
specifier|private
specifier|final
name|int
name|k
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|/**    * Create a {@link Classifier} using kNN algorithm    *    * @param k the number of neighbors to analyze as an<code>int</code>    */
DECL|method|KNearestNeighborClassifier
specifier|public
name|KNearestNeighborClassifier
parameter_list|(
name|int
name|k
parameter_list|)
block|{
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
if|if
condition|(
name|mlt
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"You must first call Classifier#train"
argument_list|)
throw|;
block|}
name|BooleanQuery
name|mltQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|textFieldName
range|:
name|textFieldNames
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
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|,
name|textFieldName
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
name|TopDocs
name|topDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|mltQuery
argument_list|,
name|k
argument_list|)
decl_stmt|;
return|return
name|selectClassFromNeighbors
argument_list|(
name|topDocs
argument_list|)
return|;
block|}
DECL|method|selectClassFromNeighbors
specifier|private
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|selectClassFromNeighbors
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO : improve the nearest neighbor selection
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
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
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
name|BytesRef
name|cl
init|=
operator|new
name|BytesRef
argument_list|(
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
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
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
block|}
name|double
name|max
init|=
literal|0
decl_stmt|;
name|BytesRef
name|assignedClass
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|cl
range|:
name|classCounts
operator|.
name|keySet
argument_list|()
control|)
block|{
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
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|count
expr_stmt|;
name|assignedClass
operator|=
name|cl
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
name|double
name|score
init|=
name|max
operator|/
operator|(
name|double
operator|)
name|k
decl_stmt|;
return|return
operator|new
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|assignedClass
argument_list|,
name|score
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|train
specifier|public
name|void
name|train
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|,
name|String
name|textFieldName
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|train
argument_list|(
name|atomicReader
argument_list|,
name|textFieldName
argument_list|,
name|classFieldName
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|train
specifier|public
name|void
name|train
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|,
name|String
name|textFieldName
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|textFieldNames
operator|=
operator|new
name|String
index|[]
block|{
name|textFieldName
block|}
expr_stmt|;
name|this
operator|.
name|classFieldName
operator|=
name|classFieldName
expr_stmt|;
name|mlt
operator|=
operator|new
name|MoreLikeThis
argument_list|(
name|atomicReader
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|textFieldName
block|}
argument_list|)
expr_stmt|;
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|atomicReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|train
specifier|public
name|void
name|train
parameter_list|(
name|AtomicReader
name|atomicReader
parameter_list|,
name|String
index|[]
name|textFieldNames
parameter_list|,
name|String
name|classFieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
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
name|mlt
operator|=
operator|new
name|MoreLikeThis
argument_list|(
name|atomicReader
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|textFieldNames
argument_list|)
expr_stmt|;
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|atomicReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
block|}
end_class

end_unit

