begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.en
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|en
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

begin_comment
comment|/**  * Simple tests for {@link EnglishMinimalStemFilter}  */
end_comment

begin_class
DECL|class|TestEnglishMinimalStemFilter
specifier|public
class|class
name|TestEnglishMinimalStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
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
name|source
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
name|source
argument_list|,
operator|new
name|EnglishMinimalStemFilter
argument_list|(
name|source
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Test some examples from various papers about this technique */
DECL|method|testExamples
specifier|public
name|void
name|testExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"queries"
argument_list|,
literal|"query"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"phrases"
argument_list|,
literal|"phrase"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"corpus"
argument_list|,
literal|"corpus"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"stress"
argument_list|,
literal|"stress"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"kings"
argument_list|,
literal|"king"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"panels"
argument_list|,
literal|"panel"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"aerodynamics"
argument_list|,
literal|"aerodynamic"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"congress"
argument_list|,
literal|"congress"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"serious"
argument_list|,
literal|"serious"
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
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|analyzer
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

