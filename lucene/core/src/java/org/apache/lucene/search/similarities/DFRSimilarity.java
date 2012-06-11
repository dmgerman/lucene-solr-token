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
comment|/**  * Implements the<em>divergence from randomness (DFR)</em> framework  * introduced in Gianni Amati and Cornelis Joost Van Rijsbergen. 2002.  * Probabilistic models of information retrieval based on measuring the  * divergence from randomness. ACM Trans. Inf. Syst. 20, 4 (October 2002),  * 357-389.  *<p>The DFR scoring formula is composed of three separate components: the  *<em>basic model</em>, the<em>aftereffect</em> and an additional  *<em>normalization</em> component, represented by the classes  * {@code BasicModel}, {@code AfterEffect} and {@code Normalization},  * respectively. The names of these classes were chosen to match the names of  * their counterparts in the Terrier IR engine.</p>  *<p>Note that<em>qtf</em>, the multiplicity of term-occurrence in the query,  * is not handled by this implementation.</p>  * @see BasicModel  * @see AfterEffect  * @see Normalization  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DFRSimilarity
specifier|public
class|class
name|DFRSimilarity
extends|extends
name|SimilarityBase
block|{
comment|/** The basic model for information content. */
DECL|field|basicModel
specifier|protected
specifier|final
name|BasicModel
name|basicModel
decl_stmt|;
comment|/** The first normalization of the information content. */
DECL|field|afterEffect
specifier|protected
specifier|final
name|AfterEffect
name|afterEffect
decl_stmt|;
comment|/** The term frequency normalization. */
DECL|field|normalization
specifier|protected
specifier|final
name|Normalization
name|normalization
decl_stmt|;
DECL|method|DFRSimilarity
specifier|public
name|DFRSimilarity
parameter_list|(
name|BasicModel
name|basicModel
parameter_list|,
name|AfterEffect
name|afterEffect
parameter_list|,
name|Normalization
name|normalization
parameter_list|)
block|{
if|if
condition|(
name|basicModel
operator|==
literal|null
operator|||
name|afterEffect
operator|==
literal|null
operator|||
name|normalization
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"null parameters not allowed."
argument_list|)
throw|;
block|}
name|this
operator|.
name|basicModel
operator|=
name|basicModel
expr_stmt|;
name|this
operator|.
name|afterEffect
operator|=
name|afterEffect
expr_stmt|;
name|this
operator|.
name|normalization
operator|=
name|normalization
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
name|tfn
init|=
name|normalization
operator|.
name|tfn
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|docLen
argument_list|)
decl_stmt|;
return|return
name|stats
operator|.
name|getTotalBoost
argument_list|()
operator|*
name|basicModel
operator|.
name|score
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|)
operator|*
name|afterEffect
operator|.
name|score
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|)
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
name|Explanation
name|normExpl
init|=
name|normalization
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|docLen
argument_list|)
decl_stmt|;
name|float
name|tfn
init|=
name|normExpl
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|normExpl
argument_list|)
expr_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|basicModel
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|)
argument_list|)
expr_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|afterEffect
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|)
argument_list|)
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
literal|"DFR "
operator|+
name|basicModel
operator|.
name|toString
argument_list|()
operator|+
name|afterEffect
operator|.
name|toString
argument_list|()
operator|+
name|normalization
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getBasicModel
specifier|public
name|BasicModel
name|getBasicModel
parameter_list|()
block|{
return|return
name|basicModel
return|;
block|}
DECL|method|getAfterEffect
specifier|public
name|AfterEffect
name|getAfterEffect
parameter_list|()
block|{
return|return
name|afterEffect
return|;
block|}
DECL|method|getNormalization
specifier|public
name|Normalization
name|getNormalization
parameter_list|()
block|{
return|return
name|normalization
return|;
block|}
block|}
end_class

end_unit

