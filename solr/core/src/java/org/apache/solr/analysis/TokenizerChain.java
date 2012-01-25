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
name|*
import|;
end_import

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
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  *  */
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
specifier|final
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
DECL|class|SolrTokenStreamComponents
class|class
name|SolrTokenStreamComponents
extends|extends
name|TokenStreamComponents
block|{
DECL|method|SolrTokenStreamComponents
specifier|public
name|SolrTokenStreamComponents
parameter_list|(
specifier|final
name|Tokenizer
name|source
parameter_list|,
specifier|final
name|TokenStream
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// the tokenizers are currently reset by the indexing process, so only
comment|// the tokenizer needs to be reset.
name|Reader
name|r
init|=
name|initReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|super
operator|.
name|reset
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|initReader
specifier|public
name|Reader
name|initReader
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
name|CharFilterFactory
name|charFilter
range|:
name|charFilters
control|)
block|{
name|cs
operator|=
name|charFilter
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
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|aReader
parameter_list|)
block|{
name|Tokenizer
name|tk
init|=
name|tokenizer
operator|.
name|create
argument_list|(
name|initReader
argument_list|(
name|aReader
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|tk
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|filter
range|:
name|filters
control|)
block|{
name|ts
operator|=
name|filter
operator|.
name|create
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SolrTokenStreamComponents
argument_list|(
name|tk
argument_list|,
name|ts
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

