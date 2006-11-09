begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|analyzing
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ISOLatin1AccentFilter
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
name|LowerCaseFilter
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
name|StandardFilter
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
import|;
end_import

begin_comment
comment|/**  * @author  Ronnie Kolehmainen (ronnie.kolehmainen at ub.uu.se)  * @version $Revision$, $Date$  */
end_comment

begin_class
DECL|class|TestAnalyzingQueryParser
specifier|public
class|class
name|TestAnalyzingQueryParser
extends|extends
name|TestCase
block|{
DECL|field|a
specifier|private
name|Analyzer
name|a
decl_stmt|;
DECL|field|wildcardInput
specifier|private
name|String
index|[]
name|wildcardInput
decl_stmt|;
DECL|field|wildcardExpected
specifier|private
name|String
index|[]
name|wildcardExpected
decl_stmt|;
DECL|field|prefixInput
specifier|private
name|String
index|[]
name|prefixInput
decl_stmt|;
DECL|field|prefixExpected
specifier|private
name|String
index|[]
name|prefixExpected
decl_stmt|;
DECL|field|rangeInput
specifier|private
name|String
index|[]
name|rangeInput
decl_stmt|;
DECL|field|rangeExpected
specifier|private
name|String
index|[]
name|rangeExpected
decl_stmt|;
DECL|field|fuzzyInput
specifier|private
name|String
index|[]
name|fuzzyInput
decl_stmt|;
DECL|field|fuzzyExpected
specifier|private
name|String
index|[]
name|fuzzyExpected
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|wildcardInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"Ã¼bersetzung Ã¼ber*ung"
block|,
literal|"MÃ¶tley Cr\u00fce MÃ¶tl?* CrÃ¼?"
block|,
literal|"RenÃ©e Zellweger Ren?? Zellw?ger"
block|}
expr_stmt|;
name|wildcardExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"ubersetzung uber*ung"
block|,
literal|"motley crue motl?* cru?"
block|,
literal|"renee zellweger ren?? zellw?ger"
block|}
expr_stmt|;
name|prefixInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"Ã¼bersetzung Ã¼bersetz*"
block|,
literal|"MÃ¶tley CrÃ¼e MÃ¶tl* crÃ¼*"
block|,
literal|"RenÃ©? Zellw*"
block|}
expr_stmt|;
name|prefixExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"ubersetzung ubersetz*"
block|,
literal|"motley crue motl* cru*"
block|,
literal|"rene? zellw*"
block|}
expr_stmt|;
name|rangeInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"[aa TO bb]"
block|,
literal|"{AnaÃ¯s TO ZoÃ©}"
block|}
expr_stmt|;
name|rangeExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"[aa TO bb]"
block|,
literal|"{anais TO zoe}"
block|}
expr_stmt|;
name|fuzzyInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"Ãbersetzung Ãbersetzung~0.9"
block|,
literal|"MÃ¶tley CrÃ¼e MÃ¶tley~0.75 CrÃ¼e~0.5"
block|,
literal|"RenÃ©e Zellweger RenÃ©e~0.9 Zellweger~"
block|}
expr_stmt|;
name|fuzzyExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"ubersetzung ubersetzung~0.9"
block|,
literal|"motley crue motley~0.75 crue~0.5"
block|,
literal|"renee zellweger renee~0.9 zellweger~0.5"
block|}
expr_stmt|;
name|a
operator|=
operator|new
name|ASCIIAnalyzer
argument_list|()
expr_stmt|;
block|}
DECL|method|testWildCardQuery
specifier|public
name|void
name|testWildCardQuery
parameter_list|()
throws|throws
name|ParseException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wildcardInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing wildcards with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|wildcardInput
index|[
name|i
index|]
argument_list|,
name|wildcardExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|wildcardInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPrefixQuery
specifier|public
name|void
name|testPrefixQuery
parameter_list|()
throws|throws
name|ParseException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefixInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing prefixes with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|prefixInput
index|[
name|i
index|]
argument_list|,
name|prefixExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|prefixInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRangeQuery
specifier|public
name|void
name|testRangeQuery
parameter_list|()
throws|throws
name|ParseException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rangeInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing ranges with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|rangeInput
index|[
name|i
index|]
argument_list|,
name|rangeExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|rangeInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFuzzyQuery
specifier|public
name|void
name|testFuzzyQuery
parameter_list|()
throws|throws
name|ParseException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fuzzyInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing fuzzys with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|fuzzyInput
index|[
name|i
index|]
argument_list|,
name|fuzzyExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|fuzzyInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseWithAnalyzingQueryParser
specifier|private
name|String
name|parseWithAnalyzingQueryParser
parameter_list|(
name|String
name|s
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|ParseException
block|{
name|AnalyzingQueryParser
name|qp
init|=
operator|new
name|AnalyzingQueryParser
argument_list|(
literal|"field"
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
name|q
operator|.
name|toString
argument_list|(
literal|"field"
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|ASCIIAnalyzer
class|class
name|ASCIIAnalyzer
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
block|{
DECL|method|ASCIIAnalyzer
specifier|public
name|ASCIIAnalyzer
parameter_list|()
block|{   }
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ISOLatin1AccentFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

