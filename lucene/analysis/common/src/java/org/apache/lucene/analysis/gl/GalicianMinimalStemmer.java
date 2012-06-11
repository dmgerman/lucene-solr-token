begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.gl
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|gl
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
name|analysis
operator|.
name|pt
operator|.
name|RSLPStemmerBase
import|;
end_import

begin_comment
comment|/**  * Minimal Stemmer for Galician  *<p>  * This follows the "RSLP-S" algorithm, but modified for Galician.  * Hence this stemmer only applies the plural reduction step of:  * "Regras do lematizador para o galego"  * @see RSLPStemmerBase  */
end_comment

begin_class
DECL|class|GalicianMinimalStemmer
specifier|public
class|class
name|GalicianMinimalStemmer
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
name|GalicianMinimalStemmer
operator|.
name|class
argument_list|,
literal|"galician.rslp"
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

