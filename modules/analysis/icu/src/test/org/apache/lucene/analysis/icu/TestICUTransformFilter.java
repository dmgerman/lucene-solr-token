begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
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
name|core
operator|.
name|KeywordTokenizer
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
name|core
operator|.
name|WhitespaceTokenizer
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
name|util
operator|.
name|ReusableAnalyzerBase
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Transliterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|UnicodeSet
import|;
end_import

begin_comment
comment|/**  * Test the ICUTransformFilter with some basic examples.  */
end_comment

begin_class
DECL|class|TestICUTransformFilter
specifier|public
class|class
name|TestICUTransformFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testBasicFunctionality
specifier|public
name|void
name|testBasicFunctionality
parameter_list|()
throws|throws
name|Exception
block|{
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Traditional-Simplified"
argument_list|)
argument_list|,
literal|"ç°¡åå­"
argument_list|,
literal|"ç®åå­"
argument_list|)
expr_stmt|;
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Katakana-Hiragana"
argument_list|)
argument_list|,
literal|"ãã©ã¬ã"
argument_list|,
literal|"ã²ãããª"
argument_list|)
expr_stmt|;
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Fullwidth-Halfwidth"
argument_list|)
argument_list|,
literal|"ã¢ã«ã¢ããªã¦"
argument_list|,
literal|"ï½±ï¾ï½±ï¾ï¾ï½³"
argument_list|)
expr_stmt|;
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Any-Latin"
argument_list|)
argument_list|,
literal|"ÎÎ»ÏÎ±Î²Î·ÏÎ¹ÎºÏÏ ÎÎ±ÏÎ¬Î»Î¿Î³Î¿Ï"
argument_list|,
literal|"AlphabÄtikÃ³s KatÃ¡logos"
argument_list|)
expr_stmt|;
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"NFD; [:Nonspacing Mark:] Remove"
argument_list|)
argument_list|,
literal|"AlphabÄtikÃ³s KatÃ¡logos"
argument_list|,
literal|"Alphabetikos Katalogos"
argument_list|)
expr_stmt|;
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Han-Latin"
argument_list|)
argument_list|,
literal|"ä¸­å½"
argument_list|,
literal|"zhÅng guÃ³"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomFunctionality
specifier|public
name|void
name|testCustomFunctionality
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rules
init|=
literal|"a> b; b> c;"
decl_stmt|;
comment|// convert a's to b's and b's to c's
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|createFromRules
argument_list|(
literal|"test"
argument_list|,
name|rules
argument_list|,
name|Transliterator
operator|.
name|FORWARD
argument_list|)
argument_list|,
literal|"abacadaba"
argument_list|,
literal|"bcbcbdbcb"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomFunctionality2
specifier|public
name|void
name|testCustomFunctionality2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rules
init|=
literal|"c { a> b; a> d;"
decl_stmt|;
comment|// convert a's to b's and b's to c's
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|createFromRules
argument_list|(
literal|"test"
argument_list|,
name|rules
argument_list|,
name|Transliterator
operator|.
name|FORWARD
argument_list|)
argument_list|,
literal|"caa"
argument_list|,
literal|"cbd"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOptimizer
specifier|public
name|void
name|testOptimizer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rules
init|=
literal|"a> b; b> c;"
decl_stmt|;
comment|// convert a's to b's and b's to c's
name|Transliterator
name|custom
init|=
name|Transliterator
operator|.
name|createFromRules
argument_list|(
literal|"test"
argument_list|,
name|rules
argument_list|,
name|Transliterator
operator|.
name|FORWARD
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|custom
operator|.
name|getFilter
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
operator|new
name|ICUTransformFilter
argument_list|(
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
name|custom
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|custom
operator|.
name|getFilter
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|UnicodeSet
argument_list|(
literal|"[ab]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOptimizer2
specifier|public
name|void
name|testOptimizer2
parameter_list|()
throws|throws
name|Exception
block|{
name|checkToken
argument_list|(
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Traditional-Simplified; CaseFold"
argument_list|)
argument_list|,
literal|"ABCDE"
argument_list|,
literal|"abcde"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOptimizerSurrogate
specifier|public
name|void
name|testOptimizerSurrogate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rules
init|=
literal|"\\U00020087> x;"
decl_stmt|;
comment|// convert CJK UNIFIED IDEOGRAPH-20087 to an x
name|Transliterator
name|custom
init|=
name|Transliterator
operator|.
name|createFromRules
argument_list|(
literal|"test"
argument_list|,
name|rules
argument_list|,
name|Transliterator
operator|.
name|FORWARD
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|custom
operator|.
name|getFilter
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
operator|new
name|ICUTransformFilter
argument_list|(
operator|new
name|KeywordTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|,
name|custom
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|custom
operator|.
name|getFilter
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|UnicodeSet
argument_list|(
literal|"[\\U00020087]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkToken
specifier|private
name|void
name|checkToken
parameter_list|(
name|Transliterator
name|transform
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
operator|new
name|ICUTransformFilter
argument_list|(
operator|new
name|KeywordTokenizer
argument_list|(
operator|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
operator|)
argument_list|)
argument_list|,
name|transform
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
name|expected
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
name|Transliterator
name|transform
init|=
name|Transliterator
operator|.
name|getInstance
argument_list|(
literal|"Any-Latin"
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|ReusableAnalyzerBase
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
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ICUTransformFilter
argument_list|(
name|tokenizer
argument_list|,
name|transform
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

