begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
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
name|Collections
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure the CJK bigram factory is working.  */
end_comment

begin_class
DECL|class|TestCJKBigramFilterFactory
specifier|public
class|class
name|TestCJKBigramFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
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
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|)
decl_stmt|;
name|CJKBigramFilterFactory
name|factory
init|=
operator|new
name|CJKBigramFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|Collections
operator|.
name|emptyMap
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
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|"å¤ã"
block|,
literal|"ãã®"
block|,
literal|"ã®å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"çã"
block|,
literal|"ãè©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨ã«"
block|,
literal|"ã«è½"
block|,
literal|"è½ã¡"
block|,
literal|"ã¡ã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHanOnly
specifier|public
name|void
name|testHanOnly
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
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|)
decl_stmt|;
name|CJKBigramFilterFactory
name|factory
init|=
operator|new
name|CJKBigramFilterFactory
argument_list|()
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
literal|"hiragana"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
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
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|"å¤"
block|,
literal|"ã"
block|,
literal|"ã®"
block|,
literal|"å­¦ç"
block|,
literal|"ã"
block|,
literal|"è©¦é¨"
block|,
literal|"ã«"
block|,
literal|"è½"
block|,
literal|"ã¡"
block|,
literal|"ã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHanOnlyUnigrams
specifier|public
name|void
name|testHanOnlyUnigrams
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
literal|"å¤ãã®å­¦çãè©¦é¨ã«è½ã¡ãã"
argument_list|)
decl_stmt|;
name|CJKBigramFilterFactory
name|factory
init|=
operator|new
name|CJKBigramFilterFactory
argument_list|()
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
literal|"hiragana"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
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
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
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
literal|"å¤"
block|,
literal|"ã"
block|,
literal|"ã®"
block|,
literal|"å­¦"
block|,
literal|"å­¦ç"
block|,
literal|"ç"
block|,
literal|"ã"
block|,
literal|"è©¦"
block|,
literal|"è©¦é¨"
block|,
literal|"é¨"
block|,
literal|"ã«"
block|,
literal|"è½"
block|,
literal|"ã¡"
block|,
literal|"ã"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

