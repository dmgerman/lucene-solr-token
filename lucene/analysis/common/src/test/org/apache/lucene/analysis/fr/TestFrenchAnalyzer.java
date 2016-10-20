begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
package|;
end_package

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

begin_comment
comment|/**  * Test case for FrenchAnalyzer.  *  */
end_comment

begin_class
DECL|class|TestFrenchAnalyzer
specifier|public
class|class
name|TestFrenchAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{     }
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"chien chat cheval"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"chien CHAT CHEVAL"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"  chien  ,? + = -  CHAT /:> CHEVAL"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"chien++"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"mot \"entreguillemet\""
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mot"
block|,
literal|"entreguilemet"
block|}
argument_list|)
expr_stmt|;
comment|// let's do some french specific tests now
comment|/* 1. couldn't resist       I would expect this to stay one term as in French the minus      sign is often used for composing words */
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"Jean-FranÃ§ois"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jean"
block|,
literal|"francoi"
block|}
argument_list|)
expr_stmt|;
comment|// 2. stopwords
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"le la chien les aux chat du des Ã  cheval"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
comment|// some nouns and adjectives
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"lances chismes habitable chiste Ã©lÃ©ments captifs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lanc"
block|,
literal|"chism"
block|,
literal|"habitabl"
block|,
literal|"chist"
block|,
literal|"element"
block|,
literal|"captif"
block|}
argument_list|)
expr_stmt|;
comment|// some verbs
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"finissions souffrirent rugissante"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"finision"
block|,
literal|"soufrirent"
block|,
literal|"rugisant"
block|}
argument_list|)
expr_stmt|;
comment|// some everything else
comment|// aujourd'hui stays one term which is OK
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"C3PO aujourd'hui oeuf Ã¯Ã¢Ã¶Ã»Ã Ã¤ anticonstitutionnellement Java++ "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c3po"
block|,
literal|"aujourd'hui"
block|,
literal|"oeuf"
block|,
literal|"Ã¯aÃ¶uaÃ¤"
block|,
literal|"anticonstitutionel"
block|,
literal|"java"
block|}
argument_list|)
expr_stmt|;
comment|// some more everything else
comment|// here 1940-1945 stays as one term, 1940:1945 not ?
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"33Bis 1940-1945 1940:1945 (---i+++)*"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"33bi"
block|,
literal|"1940"
block|,
literal|"1945"
block|,
literal|"1940"
block|,
literal|"1945"
block|,
literal|"i"
block|}
argument_list|)
expr_stmt|;
name|fa
operator|.
name|close
argument_list|()
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
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|()
decl_stmt|;
comment|// stopwords
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"le la chien les aux chat du des Ã  cheval"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
comment|// some nouns and adjectives
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"lances chismes habitable chiste Ã©lÃ©ments captifs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lanc"
block|,
literal|"chism"
block|,
literal|"habitabl"
block|,
literal|"chist"
block|,
literal|"element"
block|,
literal|"captif"
block|}
argument_list|)
expr_stmt|;
name|fa
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testExclusionTableViaCtor
specifier|public
name|void
name|testExclusionTableViaCtor
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"habitable"
argument_list|)
expr_stmt|;
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|(
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"habitable chiste"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"habitable"
block|,
literal|"chist"
block|}
argument_list|)
expr_stmt|;
name|fa
operator|.
name|close
argument_list|()
expr_stmt|;
name|fa
operator|=
operator|new
name|FrenchAnalyzer
argument_list|(
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"habitable chiste"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"habitable"
block|,
literal|"chist"
block|}
argument_list|)
expr_stmt|;
name|fa
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testElision
specifier|public
name|void
name|testElision
parameter_list|()
throws|throws
name|Exception
block|{
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"voir l'embrouille"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"voir"
block|,
literal|"embrouil"
block|}
argument_list|)
expr_stmt|;
name|fa
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that stopwords are not case sensitive    */
DECL|method|testStopwordsCasing
specifier|public
name|void
name|testStopwordsCasing
parameter_list|()
throws|throws
name|IOException
block|{
name|FrenchAnalyzer
name|a
init|=
operator|new
name|FrenchAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Votre"
argument_list|,
operator|new
name|String
index|[]
block|{ }
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
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
name|Analyzer
name|a
init|=
operator|new
name|FrenchAnalyzer
argument_list|()
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
comment|/** test accent-insensitive */
DECL|method|testAccentInsensitive
specifier|public
name|void
name|testAccentInsensitive
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|FrenchAnalyzer
argument_list|()
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"sÃ©curitaires"
argument_list|,
literal|"securitair"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"securitaires"
argument_list|,
literal|"securitair"
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

