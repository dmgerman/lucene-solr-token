begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cz
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cz
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|InputStream
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Test the CzechAnalyzer  *   * Before Lucene 3.1, CzechAnalyzer was a StandardAnalyzer with a custom   * stopword list. As of 3.1 it also includes a stemmer.  *  */
end_comment

begin_class
DECL|class|TestCzechAnalyzer
specifier|public
class|class
name|TestCzechAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|dataDir
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|customStopFile
name|File
name|customStopFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"org/apache/lucene/analysis/cz/customStopWordFile.txt"
argument_list|)
decl_stmt|;
comment|/**    * @deprecated Remove this test when support for 3.0 indexes is no longer needed.    */
annotation|@
name|Deprecated
DECL|method|testStopWordLegacy
specifier|public
name|void
name|testStopWordLegacy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|CzechAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
argument_list|,
literal|"Pokud mluvime o volnem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mluvime"
block|,
literal|"volnem"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopWord
specifier|public
name|void
name|testStopWord
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"Pokud mluvime o volnem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mluvim"
block|,
literal|"voln"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated Remove this test when support for 3.0 indexes is no longer needed.    */
annotation|@
name|Deprecated
DECL|method|testReusableTokenStreamLegacy
specifier|public
name|void
name|testReusableTokenStreamLegacy
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"Pokud mluvime o volnem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mluvime"
block|,
literal|"volnem"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"ÄeskÃ¡ Republika"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÄeskÃ¡"
block|,
literal|"republika"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"Pokud mluvime o volnem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mluvim"
block|,
literal|"voln"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"ÄeskÃ¡ Republika"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Äesk"
block|,
literal|"republik"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * An input stream that always throws IOException for testing.    * @deprecated Remove this class when the loadStopWords method is removed.    */
annotation|@
name|Deprecated
DECL|class|UnreliableInputStream
specifier|private
class|class
name|UnreliableInputStream
extends|extends
name|InputStream
block|{
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
block|}
comment|/**    * The loadStopWords method does not throw IOException on error,    * instead previously it set the stoptable to null (versus empty)    * this would cause a NPE when it is time to create the StopFilter.    * @deprecated Remove this test when the loadStopWords method is removed.    */
annotation|@
name|Deprecated
DECL|method|testInvalidStopWordFile
specifier|public
name|void
name|testInvalidStopWordFile
parameter_list|()
throws|throws
name|Exception
block|{
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|cz
operator|.
name|loadStopWords
argument_list|(
operator|new
name|UnreliableInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Pokud mluvime o volnem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pokud"
block|,
literal|"mluvime"
block|,
literal|"o"
block|,
literal|"volnem"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test that changes to the stop table via loadStopWords are applied immediately    * when using reusable token streams.    * @deprecated Remove this test when the loadStopWords method is removed.    */
annotation|@
name|Deprecated
DECL|method|testStopWordFileReuse
specifier|public
name|void
name|testStopWordFileReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|cz
argument_list|,
literal|"ÄeskÃ¡ Republika"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÄeskÃ¡"
block|,
literal|"republika"
block|}
argument_list|)
expr_stmt|;
name|InputStream
name|stopwords
init|=
operator|new
name|FileInputStream
argument_list|(
name|customStopFile
argument_list|)
decl_stmt|;
name|cz
operator|.
name|loadStopWords
argument_list|(
name|stopwords
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|cz
argument_list|,
literal|"ÄeskÃ¡ Republika"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÄeskÃ¡"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithStemExclusionSet
specifier|public
name|void
name|testWithStemExclusionSet
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"hole"
argument_list|)
expr_stmt|;
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hole desek"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hole"
block|,
literal|"desk"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

