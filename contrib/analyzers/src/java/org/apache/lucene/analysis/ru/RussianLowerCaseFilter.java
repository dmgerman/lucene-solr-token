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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
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
name|Token
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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Normalizes token text to lower case, analyzing given ("russian") charset.  *  * @author  Boris Okner, b.okner@rogers.com  * @version $Id$  */
end_comment

begin_class
DECL|class|RussianLowerCaseFilter
specifier|public
specifier|final
class|class
name|RussianLowerCaseFilter
extends|extends
name|TokenFilter
block|{
DECL|field|charset
name|char
index|[]
name|charset
decl_stmt|;
DECL|method|RussianLowerCaseFilter
specifier|public
name|RussianLowerCaseFilter
parameter_list|(
name|TokenStream
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
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|txt
init|=
name|t
operator|.
name|termText
argument_list|()
decl_stmt|;
name|char
index|[]
name|chArray
init|=
name|txt
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chArray
index|[
name|i
index|]
operator|=
name|RussianCharsets
operator|.
name|toLowerCase
argument_list|(
name|chArray
index|[
name|i
index|]
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
name|String
name|newTxt
init|=
operator|new
name|String
argument_list|(
name|chArray
argument_list|)
decl_stmt|;
comment|// create new token
name|Token
name|newToken
init|=
operator|new
name|Token
argument_list|(
name|newTxt
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|newToken
return|;
block|}
block|}
end_class

end_unit

