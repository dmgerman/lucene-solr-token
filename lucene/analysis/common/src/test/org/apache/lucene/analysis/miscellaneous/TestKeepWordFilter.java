begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Analyzer
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
name|BaseTokenStreamTestCase
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
name|CharArraySet
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

begin_comment
comment|/** Test {@link KeepWordFilter} */
end_comment

begin_class
DECL|class|TestKeepWordFilter
specifier|public
class|class
name|TestKeepWordFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testStopAndGo
specifier|public
name|void
name|testStopAndGo
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|words
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|words
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|words
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|String
name|input
init|=
literal|"xxx yyy aaa zzz BBB ccc ddd EEE"
decl_stmt|;
comment|// Test Stopwords
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|KeepWordFilter
argument_list|(
name|stream
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|words
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"BBB"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
comment|// Now force case
name|stream
operator|=
name|whitespaceMockTokenizer
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|KeepWordFilter
argument_list|(
name|stream
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|words
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|words
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|words
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|words
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|KeepWordFilter
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|words
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

