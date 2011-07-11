begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
package|;
end_package

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
name|MockAnalyzer
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
name|analysis
operator|.
name|util
operator|.
name|ReusableAnalyzerBase
import|;
end_import

begin_class
DECL|class|TestWordnetSynonymParser
specifier|public
class|class
name|TestWordnetSynonymParser
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|synonymsFile
name|String
name|synonymsFile
init|=
literal|"s(100000001,1,'woods',n,1,0).\n"
operator|+
literal|"s(100000001,2,'wood',n,1,0).\n"
operator|+
literal|"s(100000001,3,'forest',n,1,0).\n"
operator|+
literal|"s(100000002,1,'wolfish',n,1,0).\n"
operator|+
literal|"s(100000002,2,'ravenous',n,1,0).\n"
operator|+
literal|"s(100000003,1,'king',n,1,1).\n"
operator|+
literal|"s(100000003,2,'baron',n,1,1).\n"
operator|+
literal|"s(100000004,1,'king''s evil',n,1,1).\n"
operator|+
literal|"s(100000004,2,'king''s meany',n,1,1).\n"
decl_stmt|;
DECL|method|testSynonyms
specifier|public
name|void
name|testSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|WordnetSynonymParser
name|parser
init|=
operator|new
name|WordnetSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|synonymsFile
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SynonymMap
name|map
init|=
name|parser
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|SynonymFilter
argument_list|(
name|tokenizer
argument_list|,
name|map
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/* all expansions */
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"Lost in the woods"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Lost"
block|,
literal|"in"
block|,
literal|"the"
block|,
literal|"woods"
block|,
literal|"wood"
block|,
literal|"forest"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|8
block|,
literal|12
block|,
literal|12
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|,
literal|11
block|,
literal|17
block|,
literal|17
block|,
literal|17
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
comment|/* single quote */
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"king"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"king"
block|,
literal|"baron"
block|}
argument_list|)
expr_stmt|;
comment|/* multi words */
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"king's evil"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"king's"
block|,
literal|"king's"
block|,
literal|"evil"
block|,
literal|"meany"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

