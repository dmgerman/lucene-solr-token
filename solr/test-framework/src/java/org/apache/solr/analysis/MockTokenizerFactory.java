begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|MockTokenizer
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
name|Tokenizer
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
name|util
operator|.
name|automaton
operator|.
name|CharacterRunAutomaton
import|;
end_import

begin_comment
comment|/**  * Factory for {@link MockTokenizer} for testing purposes.  */
end_comment

begin_class
DECL|class|MockTokenizerFactory
specifier|public
class|class
name|MockTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|field|pattern
name|CharacterRunAutomaton
name|pattern
decl_stmt|;
DECL|field|enableChecks
name|boolean
name|enableChecks
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|patternArg
init|=
name|args
operator|.
name|get
argument_list|(
literal|"pattern"
argument_list|)
decl_stmt|;
if|if
condition|(
name|patternArg
operator|==
literal|null
condition|)
block|{
name|patternArg
operator|=
literal|"whitespace"
expr_stmt|;
block|}
if|if
condition|(
literal|"whitespace"
operator|.
name|equalsIgnoreCase
argument_list|(
name|patternArg
argument_list|)
condition|)
block|{
name|pattern
operator|=
name|MockTokenizer
operator|.
name|WHITESPACE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"keyword"
operator|.
name|equalsIgnoreCase
argument_list|(
name|patternArg
argument_list|)
condition|)
block|{
name|pattern
operator|=
name|MockTokenizer
operator|.
name|KEYWORD
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"simple"
operator|.
name|equalsIgnoreCase
argument_list|(
name|patternArg
argument_list|)
condition|)
block|{
name|pattern
operator|=
name|MockTokenizer
operator|.
name|SIMPLE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid pattern!"
argument_list|)
throw|;
block|}
name|enableChecks
operator|=
name|getBoolean
argument_list|(
literal|"enableChecks"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|MockTokenizer
name|t
init|=
operator|new
name|MockTokenizer
argument_list|(
name|input
argument_list|,
name|pattern
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|t
operator|.
name|setEnableChecks
argument_list|(
name|enableChecks
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

