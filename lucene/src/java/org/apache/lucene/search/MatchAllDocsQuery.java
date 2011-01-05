begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|IndexReader
operator|.
name|ReaderContext
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
name|util
operator|.
name|ToStringUtils
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
name|Bits
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * A query that matches all documents.  *  */
end_comment

begin_class
DECL|class|MatchAllDocsQuery
specifier|public
class|class
name|MatchAllDocsQuery
extends|extends
name|Query
block|{
DECL|method|MatchAllDocsQuery
specifier|public
name|MatchAllDocsQuery
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|field|normsField
specifier|private
specifier|final
name|String
name|normsField
decl_stmt|;
comment|/**    * @param normsField Field used for normalization factor (document boost). Null if nothing.    */
DECL|method|MatchAllDocsQuery
specifier|public
name|MatchAllDocsQuery
parameter_list|(
name|String
name|normsField
parameter_list|)
block|{
name|this
operator|.
name|normsField
operator|=
name|normsField
expr_stmt|;
block|}
DECL|class|MatchAllScorer
specifier|private
class|class
name|MatchAllScorer
extends|extends
name|Scorer
block|{
DECL|field|score
specifier|final
name|float
name|score
decl_stmt|;
DECL|field|norms
specifier|final
name|byte
index|[]
name|norms
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|delDocs
specifier|private
specifier|final
name|Bits
name|delDocs
decl_stmt|;
DECL|method|MatchAllScorer
name|MatchAllScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Weight
name|w
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|delDocs
operator|=
name|reader
operator|.
name|getDeletedDocs
argument_list|()
expr_stmt|;
name|score
operator|=
name|w
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|doc
operator|++
expr_stmt|;
while|while
condition|(
name|delDocs
operator|!=
literal|null
operator|&&
name|doc
operator|<
name|maxDoc
operator|&&
name|delDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|doc
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|==
name|maxDoc
condition|)
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|norms
operator|==
literal|null
condition|?
name|score
else|:
name|score
operator|*
name|getSimilarity
argument_list|()
operator|.
name|decodeNormValue
argument_list|(
name|norms
index|[
name|docID
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
operator|-
literal|1
expr_stmt|;
return|return
name|nextDoc
argument_list|()
return|;
block|}
block|}
DECL|class|MatchAllDocsWeight
specifier|private
class|class
name|MatchAllDocsWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
decl_stmt|;
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
decl_stmt|;
DECL|method|MatchAllDocsWeight
specifier|public
name|MatchAllDocsWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"weight("
operator|+
name|MatchAllDocsQuery
operator|.
name|this
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|MatchAllDocsQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|queryWeight
return|;
block|}
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
block|{
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|queryNorm
expr_stmt|;
name|queryWeight
operator|*=
name|this
operator|.
name|queryNorm
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|ReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MatchAllScorer
argument_list|(
name|context
operator|.
name|reader
argument_list|,
name|similarity
argument_list|,
name|this
argument_list|,
name|normsField
operator|!=
literal|null
condition|?
name|context
operator|.
name|reader
operator|.
name|norms
argument_list|(
name|normsField
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|ReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
comment|// explain query weight
name|Explanation
name|queryExpl
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|getValue
argument_list|()
argument_list|,
literal|"MatchAllDocsQuery, product of:"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|queryExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|queryExpl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|queryExpl
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
return|return
operator|new
name|MatchAllDocsWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|MatchAllDocsQuery
operator|)
condition|)
return|return
literal|false
return|;
name|MatchAllDocsQuery
name|other
init|=
operator|(
name|MatchAllDocsQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
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
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
literal|0x1AA71190
return|;
block|}
block|}
end_class

end_unit

