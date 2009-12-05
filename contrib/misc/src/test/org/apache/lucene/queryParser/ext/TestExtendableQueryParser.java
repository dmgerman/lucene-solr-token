begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.ext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ext
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SimpleAnalyzer
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
name|QueryParser
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
name|TestQueryParser
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|TermQuery
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
comment|/**  * Testcase for the class {@link ExtendableQueryParser}  */
end_comment

begin_class
DECL|class|TestExtendableQueryParser
specifier|public
class|class
name|TestExtendableQueryParser
extends|extends
name|TestQueryParser
block|{
DECL|field|DELIMITERS
specifier|private
specifier|static
name|char
index|[]
name|DELIMITERS
init|=
operator|new
name|char
index|[]
block|{
name|Extensions
operator|.
name|DEFAULT_EXTENSION_FIELD_DELIMITER
block|,
literal|'-'
block|,
literal|'|'
block|}
decl_stmt|;
DECL|method|TestExtendableQueryParser
specifier|public
name|TestExtendableQueryParser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParser
specifier|public
name|QueryParser
name|getParser
parameter_list|(
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getParser
argument_list|(
name|a
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getParser
specifier|public
name|QueryParser
name|getParser
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|Extensions
name|extensions
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
name|a
operator|=
operator|new
name|SimpleAnalyzer
argument_list|()
expr_stmt|;
name|QueryParser
name|qp
init|=
name|extensions
operator|==
literal|null
condition|?
operator|new
name|ExtendableQueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"field"
argument_list|,
name|a
argument_list|)
else|:
operator|new
name|ExtendableQueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"field"
argument_list|,
name|a
argument_list|,
name|extensions
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParser
operator|.
name|OR_OPERATOR
argument_list|)
expr_stmt|;
return|return
name|qp
return|;
block|}
DECL|method|testUnescapedExtDelimiter
specifier|public
name|void
name|testUnescapedExtDelimiter
parameter_list|()
throws|throws
name|Exception
block|{
name|Extensions
name|ext
init|=
name|newExtensions
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|ext
operator|.
name|add
argument_list|(
literal|"testExt"
argument_list|,
operator|new
name|ExtensionStub
argument_list|()
argument_list|)
expr_stmt|;
name|ExtendableQueryParser
name|parser
init|=
operator|(
name|ExtendableQueryParser
operator|)
name|getParser
argument_list|(
literal|null
argument_list|,
name|ext
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
literal|"aField:testExt:\"foo \\& bar\""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"extension field delimiter is not escaped"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|testExtFieldUnqoted
specifier|public
name|void
name|testExtFieldUnqoted
parameter_list|()
throws|throws
name|Exception
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
name|DELIMITERS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Extensions
name|ext
init|=
name|newExtensions
argument_list|(
name|DELIMITERS
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|ext
operator|.
name|add
argument_list|(
literal|"testExt"
argument_list|,
operator|new
name|ExtensionStub
argument_list|()
argument_list|)
expr_stmt|;
name|ExtendableQueryParser
name|parser
init|=
operator|(
name|ExtendableQueryParser
operator|)
name|getParser
argument_list|(
literal|null
argument_list|,
name|ext
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|ext
operator|.
name|buildExtensionField
argument_list|(
literal|"testExt"
argument_list|,
literal|"aField"
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s:foo bar"
argument_list|,
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected instance of BooleanQuery but was "
operator|+
name|query
operator|.
name|getClass
argument_list|()
argument_list|,
name|query
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bquery
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|bquery
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|clauses
operator|.
name|length
argument_list|)
expr_stmt|;
name|BooleanClause
name|booleanClause
init|=
name|clauses
index|[
literal|0
index|]
decl_stmt|;
name|query
operator|=
name|booleanClause
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected instance of TermQuery but was "
operator|+
name|query
operator|.
name|getClass
argument_list|()
argument_list|,
name|query
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|TermQuery
name|tquery
init|=
operator|(
name|TermQuery
operator|)
name|query
decl_stmt|;
name|assertEquals
argument_list|(
literal|"aField"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|booleanClause
operator|=
name|clauses
index|[
literal|1
index|]
expr_stmt|;
name|query
operator|=
name|booleanClause
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected instance of TermQuery but was "
operator|+
name|query
operator|.
name|getClass
argument_list|()
argument_list|,
name|query
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|tquery
operator|=
operator|(
name|TermQuery
operator|)
name|query
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testExtDefaultField
specifier|public
name|void
name|testExtDefaultField
parameter_list|()
throws|throws
name|Exception
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
name|DELIMITERS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Extensions
name|ext
init|=
name|newExtensions
argument_list|(
name|DELIMITERS
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|ext
operator|.
name|add
argument_list|(
literal|"testExt"
argument_list|,
operator|new
name|ExtensionStub
argument_list|()
argument_list|)
expr_stmt|;
name|ExtendableQueryParser
name|parser
init|=
operator|(
name|ExtendableQueryParser
operator|)
name|getParser
argument_list|(
literal|null
argument_list|,
name|ext
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|ext
operator|.
name|buildExtensionField
argument_list|(
literal|"testExt"
argument_list|)
decl_stmt|;
name|Query
name|parse
init|=
name|parser
operator|.
name|parse
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s:\"foo \\& bar\""
argument_list|,
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected instance of TermQuery but was "
operator|+
name|parse
operator|.
name|getClass
argument_list|()
argument_list|,
name|parse
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|TermQuery
name|tquery
init|=
operator|(
name|TermQuery
operator|)
name|parse
decl_stmt|;
name|assertEquals
argument_list|(
literal|"field"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo& bar"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newExtensions
specifier|public
name|Extensions
name|newExtensions
parameter_list|(
name|char
name|delimiter
parameter_list|)
block|{
return|return
operator|new
name|Extensions
argument_list|(
name|delimiter
argument_list|)
return|;
block|}
DECL|method|testExtField
specifier|public
name|void
name|testExtField
parameter_list|()
throws|throws
name|Exception
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
name|DELIMITERS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Extensions
name|ext
init|=
name|newExtensions
argument_list|(
name|DELIMITERS
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|ext
operator|.
name|add
argument_list|(
literal|"testExt"
argument_list|,
operator|new
name|ExtensionStub
argument_list|()
argument_list|)
expr_stmt|;
name|ExtendableQueryParser
name|parser
init|=
operator|(
name|ExtendableQueryParser
operator|)
name|getParser
argument_list|(
literal|null
argument_list|,
name|ext
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|ext
operator|.
name|buildExtensionField
argument_list|(
literal|"testExt"
argument_list|,
literal|"afield"
argument_list|)
decl_stmt|;
name|Query
name|parse
init|=
name|parser
operator|.
name|parse
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s:\"foo \\& bar\""
argument_list|,
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected instance of TermQuery but was "
operator|+
name|parse
operator|.
name|getClass
argument_list|()
argument_list|,
name|parse
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|TermQuery
name|tquery
init|=
operator|(
name|TermQuery
operator|)
name|parse
decl_stmt|;
name|assertEquals
argument_list|(
literal|"afield"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo& bar"
argument_list|,
name|tquery
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

