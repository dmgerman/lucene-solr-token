begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|CharTokenizer
import|;
end_import

begin_comment
comment|/**  * A RussianLetterTokenizer is a tokenizer that extends LetterTokenizer by additionally looking up letters  * in a given "russian charset". The problem with LeterTokenizer is that it uses Character.isLetter() method,  * which doesn't know how to detect letters in encodings like CP1252 and KOI8  * (well-known problems with 0xD7 and 0xF7 chars)  *  * @author  Boris Okner, b.okner@rogers.com  * @version $Id$  */
end_comment

begin_class
DECL|class|RussianLetterTokenizer
specifier|public
class|class
name|RussianLetterTokenizer
extends|extends
name|CharTokenizer
block|{
comment|/** Construct a new LetterTokenizer. */
DECL|field|charset
specifier|private
name|char
index|[]
name|charset
decl_stmt|;
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|,
name|char
index|[]
name|charset
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
block|}
comment|/**      * Collects only characters which satisfy      * {@link Character#isLetter(char)}.      */
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
condition|)
return|return
literal|true
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|charset
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|c
operator|==
name|charset
index|[
name|i
index|]
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

