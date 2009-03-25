begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|TokenizerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// An analyzer that uses a tokenizer and a list of token filters to
end_comment

begin_comment
comment|// create a TokenStream.
end_comment

begin_comment
comment|//
end_comment

begin_class
DECL|class|TokenizerChain
specifier|public
class|class
name|TokenizerChain
extends|extends
name|SolrAnalyzer
block|{
DECL|field|charFilters
specifier|final
specifier|private
name|CharFilterFactory
index|[]
name|charFilters
decl_stmt|;
DECL|field|tokenizer
specifier|final
specifier|private
name|TokenizerFactory
name|tokenizer
decl_stmt|;
DECL|field|filters
specifier|final
specifier|private
name|TokenFilterFactory
index|[]
name|filters
decl_stmt|;
DECL|method|TokenizerChain
specifier|public
name|TokenizerChain
parameter_list|(
name|TokenizerFactory
name|tokenizer
parameter_list|,
name|TokenFilterFactory
index|[]
name|filters
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|tokenizer
argument_list|,
name|filters
argument_list|)
expr_stmt|;
block|}
DECL|method|TokenizerChain
specifier|public
name|TokenizerChain
parameter_list|(
name|CharFilterFactory
index|[]
name|charFilters
parameter_list|,
name|TokenizerFactory
name|tokenizer
parameter_list|,
name|TokenFilterFactory
index|[]
name|filters
parameter_list|)
block|{
name|this
operator|.
name|charFilters
operator|=
name|charFilters
expr_stmt|;
name|this
operator|.
name|tokenizer
operator|=
name|tokenizer
expr_stmt|;
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
block|}
DECL|method|getCharFilterFactories
specifier|public
name|CharFilterFactory
index|[]
name|getCharFilterFactories
parameter_list|()
block|{
return|return
name|charFilters
return|;
block|}
DECL|method|getTokenizerFactory
specifier|public
name|TokenizerFactory
name|getTokenizerFactory
parameter_list|()
block|{
return|return
name|tokenizer
return|;
block|}
DECL|method|getTokenFilterFactories
specifier|public
name|TokenFilterFactory
index|[]
name|getTokenFilterFactories
parameter_list|()
block|{
return|return
name|filters
return|;
block|}
DECL|method|charStream
specifier|public
name|Reader
name|charStream
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|charFilters
operator|!=
literal|null
operator|&&
name|charFilters
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|CharStream
name|cs
init|=
name|CharReader
operator|.
name|get
argument_list|(
name|reader
argument_list|)
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
name|charFilters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cs
operator|=
name|charFilters
index|[
name|i
index|]
operator|.
name|create
argument_list|(
name|cs
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|cs
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|ts
init|=
name|tokenizer
operator|.
name|create
argument_list|(
name|charStream
argument_list|(
name|reader
argument_list|)
argument_list|)
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
name|filters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ts
operator|=
name|filters
index|[
name|i
index|]
operator|.
name|create
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"TokenizerChain("
argument_list|)
decl_stmt|;
for|for
control|(
name|CharFilterFactory
name|filter
range|:
name|charFilters
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
for|for
control|(
name|TokenFilterFactory
name|filter
range|:
name|filters
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

