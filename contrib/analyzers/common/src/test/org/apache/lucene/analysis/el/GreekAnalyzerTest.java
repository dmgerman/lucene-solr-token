begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.el
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|el
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * A unit test class for verifying the correct operation of the GreekAnalyzer.  *  */
end_comment

begin_class
DECL|class|GreekAnalyzerTest
specifier|public
class|class
name|GreekAnalyzerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** 	 * Test the analysis of various greek strings. 	 * 	 * @throws Exception in case an error occurs 	 */
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|GreekAnalyzer
argument_list|()
decl_stmt|;
comment|// Verify the correct analysis of capitals and small accented letters
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\u039c\u03af\u03b1 \u03b5\u03be\u03b1\u03b9\u03c1\u03b5\u03c4\u03b9\u03ba\u03ac \u03ba\u03b1\u03bb\u03ae \u03ba\u03b1\u03b9 \u03c0\u03bb\u03bf\u03cd\u03c3\u03b9\u03b1 \u03c3\u03b5\u03b9\u03c1\u03ac \u03c7\u03b1\u03c1\u03b1\u03ba\u03c4\u03ae\u03c1\u03c9\u03bd \u03c4\u03b7\u03c2 \u0395\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ae\u03c2 \u03b3\u03bb\u03ce\u03c3\u03c3\u03b1\u03c2"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u03bc\u03b9\u03b1"
block|,
literal|"\u03b5\u03be\u03b1\u03b9\u03c1\u03b5\u03c4\u03b9\u03ba\u03b1"
block|,
literal|"\u03ba\u03b1\u03bb\u03b7"
block|,
literal|"\u03c0\u03bb\u03bf\u03c5\u03c3\u03b9\u03b1"
block|,
literal|"\u03c3\u03b5\u03b9\u03c1\u03b1"
block|,
literal|"\u03c7\u03b1\u03c1\u03b1\u03ba\u03c4\u03b7\u03c1\u03c9\u03bd"
block|,
literal|"\u03b5\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03b7\u03c3"
block|,
literal|"\u03b3\u03bb\u03c9\u03c3\u03c3\u03b1\u03c3"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of small letters with diaeresis and the elimination
comment|// of punctuation marks
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\u03a0\u03c1\u03bf\u03ca\u03cc\u03bd\u03c4\u03b1 (\u03ba\u03b1\u03b9)     [\u03c0\u03bf\u03bb\u03bb\u03b1\u03c0\u03bb\u03ad\u03c2]	-	\u0391\u039d\u0391\u0393\u039a\u0395\u03a3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u03c0\u03c1\u03bf\u03b9\u03bf\u03bd\u03c4\u03b1"
block|,
literal|"\u03c0\u03bf\u03bb\u03bb\u03b1\u03c0\u03bb\u03b5\u03c3"
block|,
literal|"\u03b1\u03bd\u03b1\u03b3\u03ba\u03b5\u03c3"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of capital accented letters and capitalletters with diaeresis,
comment|// as well as the elimination of stop words
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\u03a0\u03a1\u039f\u03ab\u03a0\u039f\u0398\u0395\u03a3\u0395\u0399\u03a3  \u0386\u03c8\u03bf\u03b3\u03bf\u03c2, \u03bf \u03bc\u03b5\u03c3\u03c4\u03cc\u03c2 \u03ba\u03b1\u03b9 \u03bf\u03b9 \u03ac\u03bb\u03bb\u03bf\u03b9"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u03c0\u03c1\u03bf\u03c5\u03c0\u03bf\u03b8\u03b5\u03c3\u03b5\u03b9\u03c3"
block|,
literal|"\u03b1\u03c8\u03bf\u03b3\u03bf\u03c3"
block|,
literal|"\u03bc\u03b5\u03c3\u03c4\u03bf\u03c3"
block|,
literal|"\u03b1\u03bb\u03bb\u03bf\u03b9"
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
name|a
init|=
operator|new
name|GreekAnalyzer
argument_list|()
decl_stmt|;
comment|// Verify the correct analysis of capitals and small accented letters
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"\u039c\u03af\u03b1 \u03b5\u03be\u03b1\u03b9\u03c1\u03b5\u03c4\u03b9\u03ba\u03ac \u03ba\u03b1\u03bb\u03ae \u03ba\u03b1\u03b9 \u03c0\u03bb\u03bf\u03cd\u03c3\u03b9\u03b1 \u03c3\u03b5\u03b9\u03c1\u03ac \u03c7\u03b1\u03c1\u03b1\u03ba\u03c4\u03ae\u03c1\u03c9\u03bd \u03c4\u03b7\u03c2 \u0395\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ae\u03c2 \u03b3\u03bb\u03ce\u03c3\u03c3\u03b1\u03c2"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u03bc\u03b9\u03b1"
block|,
literal|"\u03b5\u03be\u03b1\u03b9\u03c1\u03b5\u03c4\u03b9\u03ba\u03b1"
block|,
literal|"\u03ba\u03b1\u03bb\u03b7"
block|,
literal|"\u03c0\u03bb\u03bf\u03c5\u03c3\u03b9\u03b1"
block|,
literal|"\u03c3\u03b5\u03b9\u03c1\u03b1"
block|,
literal|"\u03c7\u03b1\u03c1\u03b1\u03ba\u03c4\u03b7\u03c1\u03c9\u03bd"
block|,
literal|"\u03b5\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03b7\u03c3"
block|,
literal|"\u03b3\u03bb\u03c9\u03c3\u03c3\u03b1\u03c3"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of small letters with diaeresis and the elimination
comment|// of punctuation marks
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"\u03a0\u03c1\u03bf\u03ca\u03cc\u03bd\u03c4\u03b1 (\u03ba\u03b1\u03b9)     [\u03c0\u03bf\u03bb\u03bb\u03b1\u03c0\u03bb\u03ad\u03c2] -   \u0391\u039d\u0391\u0393\u039a\u0395\u03a3"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u03c0\u03c1\u03bf\u03b9\u03bf\u03bd\u03c4\u03b1"
block|,
literal|"\u03c0\u03bf\u03bb\u03bb\u03b1\u03c0\u03bb\u03b5\u03c3"
block|,
literal|"\u03b1\u03bd\u03b1\u03b3\u03ba\u03b5\u03c3"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of capital accented letters and capitalletters with diaeresis,
comment|// as well as the elimination of stop words
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"\u03a0\u03a1\u039f\u03ab\u03a0\u039f\u0398\u0395\u03a3\u0395\u0399\u03a3  \u0386\u03c8\u03bf\u03b3\u03bf\u03c2, \u03bf \u03bc\u03b5\u03c3\u03c4\u03cc\u03c2 \u03ba\u03b1\u03b9 \u03bf\u03b9 \u03ac\u03bb\u03bb\u03bf\u03b9"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u03c0\u03c1\u03bf\u03c5\u03c0\u03bf\u03b8\u03b5\u03c3\u03b5\u03b9\u03c3"
block|,
literal|"\u03b1\u03c8\u03bf\u03b3\u03bf\u03c3"
block|,
literal|"\u03bc\u03b5\u03c3\u03c4\u03bf\u03c3"
block|,
literal|"\u03b1\u03bb\u03bb\u03bf\u03b9"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

