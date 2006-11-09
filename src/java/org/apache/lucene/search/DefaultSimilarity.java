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

begin_comment
comment|/** Expert: Default scoring implementation. */
end_comment

begin_class
DECL|class|DefaultSimilarity
specifier|public
class|class
name|DefaultSimilarity
extends|extends
name|Similarity
block|{
comment|/** Implemented as<code>1/sqrt(numTerms)</code>. */
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|numTerms
argument_list|)
argument_list|)
return|;
block|}
comment|/** Implemented as<code>1/sqrt(sumOfSquaredWeights)</code>. */
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|sumOfSquaredWeights
argument_list|)
argument_list|)
return|;
block|}
comment|/** Implemented as<code>sqrt(freq)</code>. */
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|freq
argument_list|)
return|;
block|}
comment|/** Implemented as<code>1 / (distance + 1)</code>. */
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
operator|/
operator|(
name|distance
operator|+
literal|1
operator|)
return|;
block|}
comment|/** Implemented as<code>log(numDocs/(docFreq+1)) + 1</code>. */
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|numDocs
operator|/
call|(
name|double
call|)
argument_list|(
name|docFreq
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1.0
argument_list|)
return|;
block|}
comment|/** Implemented as<code>overlap / maxOverlap</code>. */
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|overlap
operator|/
operator|(
name|float
operator|)
name|maxOverlap
return|;
block|}
block|}
end_class

end_unit

