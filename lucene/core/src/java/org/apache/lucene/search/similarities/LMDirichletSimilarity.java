begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Explanation
import|;
end_import

begin_comment
comment|/**  * Bayesian smoothing using Dirichlet priors. From Chengxiang Zhai and John  * Lafferty. 2001. A study of smoothing methods for language models applied to  * Ad Hoc information retrieval. In Proceedings of the 24th annual international  * ACM SIGIR conference on Research and development in information retrieval  * (SIGIR '01). ACM, New York, NY, USA, 334-342.  *<p>  * The formula as defined the paper assigns a negative score to documents that  * contain the term, but with fewer occurrences than predicted by the collection  * language model. The Lucene implementation returns {@code 0} for such  * documents.  *</p>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|LMDirichletSimilarity
specifier|public
class|class
name|LMDirichletSimilarity
extends|extends
name|LMSimilarity
block|{
comment|/** The&mu; parameter. */
DECL|field|mu
specifier|private
specifier|final
name|float
name|mu
decl_stmt|;
comment|/** @param mu the&mu; parameter. */
DECL|method|LMDirichletSimilarity
specifier|public
name|LMDirichletSimilarity
parameter_list|(
name|CollectionModel
name|collectionModel
parameter_list|,
name|float
name|mu
parameter_list|)
block|{
name|super
argument_list|(
name|collectionModel
argument_list|)
expr_stmt|;
name|this
operator|.
name|mu
operator|=
name|mu
expr_stmt|;
block|}
comment|/** @param mu the&mu; parameter. */
DECL|method|LMDirichletSimilarity
specifier|public
name|LMDirichletSimilarity
parameter_list|(
name|float
name|mu
parameter_list|)
block|{
name|this
operator|.
name|mu
operator|=
name|mu
expr_stmt|;
block|}
comment|/** Instantiates the similarity with the default&mu; value of 2000. */
DECL|method|LMDirichletSimilarity
specifier|public
name|LMDirichletSimilarity
parameter_list|(
name|CollectionModel
name|collectionModel
parameter_list|)
block|{
name|this
argument_list|(
name|collectionModel
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
block|}
comment|/** Instantiates the similarity with the default&mu; value of 2000. */
DECL|method|LMDirichletSimilarity
specifier|public
name|LMDirichletSimilarity
parameter_list|()
block|{
name|this
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|protected
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
name|float
name|score
init|=
name|stats
operator|.
name|getTotalBoost
argument_list|()
operator|*
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|+
name|freq
operator|/
operator|(
name|mu
operator|*
operator|(
operator|(
name|LMStats
operator|)
name|stats
operator|)
operator|.
name|getCollectionProbability
argument_list|()
operator|)
argument_list|)
operator|+
name|Math
operator|.
name|log
argument_list|(
name|mu
operator|/
operator|(
name|docLen
operator|+
name|mu
operator|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|score
operator|>
literal|0.0f
condition|?
name|score
else|:
literal|0.0f
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|protected
name|void
name|explain
parameter_list|(
name|Explanation
name|expl
parameter_list|,
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
if|if
condition|(
name|stats
operator|.
name|getTotalBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|expl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|stats
operator|.
name|getTotalBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|mu
argument_list|,
literal|"mu"
argument_list|)
argument_list|)
expr_stmt|;
name|Explanation
name|weightExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|weightExpl
operator|.
name|setValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|+
name|freq
operator|/
operator|(
name|mu
operator|*
operator|(
operator|(
name|LMStats
operator|)
name|stats
operator|)
operator|.
name|getCollectionProbability
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|weightExpl
operator|.
name|setDescription
argument_list|(
literal|"term weight"
argument_list|)
expr_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|weightExpl
argument_list|)
expr_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
name|mu
operator|/
operator|(
name|docLen
operator|+
name|mu
operator|)
argument_list|)
argument_list|,
literal|"document norm"
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|explain
argument_list|(
name|expl
argument_list|,
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
argument_list|,
name|docLen
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the&mu; parameter. */
DECL|method|getMu
specifier|public
name|float
name|getMu
parameter_list|()
block|{
return|return
name|mu
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Dirichlet(%f)"
argument_list|,
name|getMu
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

