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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|TokenStream
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
name|analysis
operator|.
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure the NGram filter factories are working.  */
end_comment

begin_class
DECL|class|TestNGramFilters
specifier|public
class|class
name|TestNGramFilters
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test NGramTokenizerFactory    */
DECL|method|testNGramTokenizer
specifier|public
name|void
name|testNGramTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NGramTokenizerFactory
name|factory
init|=
operator|new
name|NGramTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|,
literal|"e"
block|,
literal|"s"
block|,
literal|"t"
block|,
literal|"te"
block|,
literal|"es"
block|,
literal|"st"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test NGramTokenizerFactory with min and max gram options    */
DECL|method|testNGramTokenizer2
specifier|public
name|void
name|testNGramTokenizer2
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minGramSize"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxGramSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|NGramTokenizerFactory
name|factory
init|=
operator|new
name|NGramTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"te"
block|,
literal|"es"
block|,
literal|"st"
block|,
literal|"tes"
block|,
literal|"est"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the NGramFilterFactory    */
DECL|method|testNGramFilter
specifier|public
name|void
name|testNGramFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NGramFilterFactory
name|factory
init|=
operator|new
name|NGramFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|,
literal|"e"
block|,
literal|"s"
block|,
literal|"t"
block|,
literal|"te"
block|,
literal|"es"
block|,
literal|"st"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the NGramFilterFactory with min and max gram options    */
DECL|method|testNGramFilter2
specifier|public
name|void
name|testNGramFilter2
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minGramSize"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxGramSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|NGramFilterFactory
name|factory
init|=
operator|new
name|NGramFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"te"
block|,
literal|"es"
block|,
literal|"st"
block|,
literal|"tes"
block|,
literal|"est"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test EdgeNGramTokenizerFactory    */
DECL|method|testEdgeNGramTokenizer
specifier|public
name|void
name|testEdgeNGramTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|EdgeNGramTokenizerFactory
name|factory
init|=
operator|new
name|EdgeNGramTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test EdgeNGramTokenizerFactory with min and max gram size    */
DECL|method|testEdgeNGramTokenizer2
specifier|public
name|void
name|testEdgeNGramTokenizer2
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minGramSize"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxGramSize"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|EdgeNGramTokenizerFactory
name|factory
init|=
operator|new
name|EdgeNGramTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|,
literal|"te"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test EdgeNGramTokenizerFactory with side option    */
DECL|method|testEdgeNGramTokenizer3
specifier|public
name|void
name|testEdgeNGramTokenizer3
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ready"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"side"
argument_list|,
literal|"back"
argument_list|)
expr_stmt|;
name|EdgeNGramTokenizerFactory
name|factory
init|=
operator|new
name|EdgeNGramTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"y"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test EdgeNGramFilterFactory    */
DECL|method|testEdgeNGramFilter
specifier|public
name|void
name|testEdgeNGramFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|EdgeNGramFilterFactory
name|factory
init|=
operator|new
name|EdgeNGramFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test EdgeNGramFilterFactory with min and max gram size    */
DECL|method|testEdgeNGramFilter2
specifier|public
name|void
name|testEdgeNGramFilter2
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minGramSize"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxGramSize"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|EdgeNGramFilterFactory
name|factory
init|=
operator|new
name|EdgeNGramFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"t"
block|,
literal|"te"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test EdgeNGramFilterFactory with side option    */
DECL|method|testEdgeNGramFilter3
specifier|public
name|void
name|testEdgeNGramFilter3
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ready"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"side"
argument_list|,
literal|"back"
argument_list|)
expr_stmt|;
name|EdgeNGramFilterFactory
name|factory
init|=
operator|new
name|EdgeNGramFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"y"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

