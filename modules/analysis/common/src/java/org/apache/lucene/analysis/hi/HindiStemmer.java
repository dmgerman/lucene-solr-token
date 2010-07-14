begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hi
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hi
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|StemmerUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Light Stemmer for Hindi.  *<p>  * Implements the algorithm specified in:  *<i>A Lightweight Stemmer for Hindi</i>  * Ananthakrishnan Ramanathan and Durgesh D Rao.  * http://computing.open.ac.uk/Sites/EACLSouthAsia/Papers/p6-Ramanathan.pdf  *</p>  */
end_comment

begin_class
DECL|class|HindiStemmer
specifier|public
class|class
name|HindiStemmer
block|{
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|buffer
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// 5
if|if
condition|(
operator|(
name|len
operator|>
literal|6
operator|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¤à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤¯à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤¯à¥à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤¯à¤¾à¤"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|5
return|;
comment|// 4
if|if
condition|(
operator|(
name|len
operator|>
literal|5
operator|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¤à¥à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¨à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¨à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¤à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¤à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¿à¤¯à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¿à¤¯à¥à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¿à¤¯à¤¾à¤"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|4
return|;
comment|// 3
if|if
condition|(
operator|(
name|len
operator|>
literal|4
operator|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤°"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¯à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¨à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¨à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤¤à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¤à¥à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤à¤"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|3
return|;
comment|// 2
if|if
condition|(
operator|(
name|len
operator|>
literal|3
operator|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤à¤°"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¿à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¨à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¨à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¨à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¤à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¤à¤¾"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥à¤"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|2
return|;
comment|// 1
if|if
condition|(
operator|(
name|len
operator|>
literal|2
operator|)
operator|&&
operator|(
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¥"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¿"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|,
literal|"à¤¾"
argument_list|)
operator|)
condition|)
return|return
name|len
operator|-
literal|1
return|;
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

