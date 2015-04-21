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
comment|/**  * Model of the information gain based on the ratio of two Bernoulli processes.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AfterEffectB
specifier|public
class|class
name|AfterEffectB
extends|extends
name|AfterEffect
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|AfterEffectB
specifier|public
name|AfterEffectB
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
name|long
name|F
init|=
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
operator|+
literal|1
decl_stmt|;
name|long
name|n
init|=
name|stats
operator|.
name|getDocFreq
argument_list|()
operator|+
literal|1
decl_stmt|;
return|return
operator|(
name|F
operator|+
literal|1
operator|)
operator|/
operator|(
name|n
operator|*
operator|(
name|tfn
operator|+
literal|1
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
specifier|final
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|(
name|stats
argument_list|,
name|tfn
argument_list|)
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", computed from: "
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|tfn
argument_list|,
literal|"tfn"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
argument_list|,
literal|"totalTermFreq"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|stats
operator|.
name|getDocFreq
argument_list|()
argument_list|,
literal|"docFreq"
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
literal|"B"
return|;
block|}
block|}
end_class

end_unit

