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
import|import static
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
name|SimilarityBase
operator|.
name|log2
import|;
end_import

begin_comment
comment|/**  * Implements the approximation of the binomial model with the divergence  * for DFR. The formula used in Lucene differs slightly from the one in the  * original paper: to avoid underflow for small values of {@code N} and  * {@code F}, {@code N} is increased by {@code 1} and  * {@code F} is always increased by {@code tfn+1}.  *<p>  * WARNING: for terms that do not meet the expected random distribution  * (e.g. stopwords), this model may give poor performance, such as  * abnormally high scores for low tf values.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BasicModelD
specifier|public
class|class
name|BasicModelD
extends|extends
name|BasicModel
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|BasicModelD
specifier|public
name|BasicModelD
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
comment|// we have to ensure phi is always< 1 for tiny TTF values, otherwise nphi can go negative,
comment|// resulting in NaN. cleanest way is to unconditionally always add tfn to totalTermFreq
comment|// to create a 'normalized' F.
name|double
name|F
init|=
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
operator|+
literal|1
operator|+
name|tfn
decl_stmt|;
name|double
name|phi
init|=
operator|(
name|double
operator|)
name|tfn
operator|/
name|F
decl_stmt|;
name|double
name|nphi
init|=
literal|1
operator|-
name|phi
decl_stmt|;
name|double
name|p
init|=
literal|1.0
operator|/
operator|(
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
operator|+
literal|1
operator|)
decl_stmt|;
name|double
name|D
init|=
name|phi
operator|*
name|log2
argument_list|(
name|phi
operator|/
name|p
argument_list|)
operator|+
name|nphi
operator|*
name|log2
argument_list|(
name|nphi
operator|/
operator|(
literal|1
operator|-
name|p
operator|)
argument_list|)
decl_stmt|;
return|return
call|(
name|float
call|)
argument_list|(
name|D
operator|*
name|F
operator|+
literal|0.5
operator|*
name|log2
argument_list|(
literal|1
operator|+
literal|2
operator|*
name|Math
operator|.
name|PI
operator|*
name|tfn
operator|*
name|nphi
argument_list|)
argument_list|)
return|;
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
literal|"D"
return|;
block|}
block|}
end_class

end_unit

