begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.pt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pt
package|;
end_package

begin_comment
comment|/**  * Minimal Stemmer for Portuguese  *<p>  * This follows the "RSLP-S" algorithm presented in:  *<i>A study on the Use of Stemming for Monolingual Ad-Hoc Portuguese  * Information Retrieval</i> (Orengo, et al)  * which is just the plural reduction step of the RSLP  * algorithm from<i>A Stemming Algorithm for the Portuguese Language</i>,  * Orengo et al.  * @see RSLPStemmerBase  */
end_comment

begin_class
DECL|class|PortugueseMinimalStemmer
specifier|public
class|class
name|PortugueseMinimalStemmer
extends|extends
name|RSLPStemmerBase
block|{
DECL|field|pluralStep
specifier|private
specifier|static
specifier|final
name|Step
name|pluralStep
init|=
name|parse
argument_list|(
name|PortugueseMinimalStemmer
operator|.
name|class
argument_list|,
literal|"portuguese.rslp"
argument_list|)
operator|.
name|get
argument_list|(
literal|"Plural"
argument_list|)
decl_stmt|;
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
name|pluralStep
operator|.
name|apply
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
end_class

end_unit

