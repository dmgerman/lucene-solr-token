begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * Passes the entire query string to the configured analyzer as-is.  **/
end_comment

begin_class
DECL|class|SuggestQueryConverter
specifier|public
class|class
name|SuggestQueryConverter
extends|extends
name|SpellingQueryConverter
block|{
annotation|@
name|Override
DECL|method|convert
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|convert
parameter_list|(
name|String
name|original
parameter_list|)
block|{
if|if
condition|(
name|original
operator|==
literal|null
condition|)
block|{
comment|// this can happen with q.alt = and no query
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|analyze
argument_list|(
name|result
argument_list|,
name|original
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

