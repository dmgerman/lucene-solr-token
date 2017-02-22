begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|StandardAnalyzer
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
name|document
operator|.
name|Document
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|RandomIndexWriter
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
name|index
operator|.
name|Term
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|OutputStreamDataOutput
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
name|BytesRef
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
name|CharsRefBuilder
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
operator|.
name|assertTokenStreamContents
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|TestSuggestField
operator|.
name|Entry
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|TestSuggestField
operator|.
name|assertSuggestions
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|TestSuggestField
operator|.
name|iwcWithSuggestField
import|;
end_import

begin_class
DECL|class|TestContextSuggestField
specifier|public
class|class
name|TestContextSuggestField
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|public
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptySuggestion
specifier|public
name|void
name|testEmptySuggestion
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|ContextSuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|,
literal|"type1"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReservedChars
specifier|public
name|void
name|testReservedChars
parameter_list|()
throws|throws
name|Exception
block|{
name|CharsRefBuilder
name|charsRefBuilder
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|charsRefBuilder
operator|.
name|append
argument_list|(
literal|"sugg"
argument_list|)
expr_stmt|;
name|charsRefBuilder
operator|.
name|setCharAt
argument_list|(
literal|2
argument_list|,
operator|(
name|char
operator|)
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
try|try
init|(
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwcWithSuggestField
argument_list|(
name|analyzer
argument_list|,
literal|"name"
argument_list|)
argument_list|)
init|)
block|{
comment|// exception should be thrown for context value containing CONTEXT_SEPARATOR
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"name"
argument_list|,
literal|"sugg"
argument_list|,
literal|1
argument_list|,
name|charsRefBuilder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"[0x1d]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|document
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
init|(
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwcWithSuggestField
argument_list|(
name|analyzer
argument_list|,
literal|"name"
argument_list|)
argument_list|)
init|)
block|{
comment|// exception should be thrown for context value containing CONTEXT_SEPARATOR
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"name"
argument_list|,
name|charsRefBuilder
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|"sugg"
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"[0x1d]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|ContextSuggestField
name|field
init|=
operator|new
name|ContextSuggestField
argument_list|(
literal|"field"
argument_list|,
literal|"input"
argument_list|,
literal|1
argument_list|,
literal|"context1"
argument_list|,
literal|"context2"
argument_list|)
decl_stmt|;
name|BytesRef
name|surfaceForm
init|=
operator|new
name|BytesRef
argument_list|(
literal|"input"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|byteArrayOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|OutputStreamDataOutput
name|output
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|byteArrayOutputStream
argument_list|)
init|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|surfaceForm
operator|.
name|bytes
argument_list|,
name|surfaceForm
operator|.
name|offset
argument_list|,
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
literal|1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|ContextSuggestField
operator|.
name|TYPE
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|(
name|byteArrayOutputStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|expectedOutputs
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|CharsRefBuilder
name|builder
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"context1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
operator|(
name|char
operator|)
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
operator|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
operator|(
name|char
operator|)
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
operator|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"input"
argument_list|)
expr_stmt|;
name|expectedOutputs
index|[
literal|0
index|]
operator|=
name|builder
operator|.
name|toCharsRef
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clear
argument_list|()
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"context2"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
operator|(
name|char
operator|)
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
operator|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|(
operator|(
name|char
operator|)
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
operator|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"input"
argument_list|)
expr_stmt|;
name|expectedOutputs
index|[
literal|1
index|]
operator|=
name|builder
operator|.
name|toCharsRef
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|CompletionTokenStreamTest
operator|.
name|PayloadAttrToTypeAttrFilter
argument_list|(
name|field
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
name|expectedOutputs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
name|payload
operator|.
name|utf8ToString
argument_list|()
block|,
name|payload
operator|.
name|utf8ToString
argument_list|()
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|CompletionAnalyzer
name|completionAnalyzer
init|=
operator|new
name|CompletionAnalyzer
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|CompletionTokenStreamTest
operator|.
name|PayloadAttrToTypeAttrFilter
argument_list|(
name|field
operator|.
name|tokenStream
argument_list|(
name|completionAnalyzer
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
name|expectedOutputs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
name|payload
operator|.
name|utf8ToString
argument_list|()
block|,
name|payload
operator|.
name|utf8ToString
argument_list|()
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMixedSuggestFields
specifier|public
name|void
name|testMixedSuggestFields
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|SuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion1"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion2"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwcWithSuggestField
argument_list|(
name|analyzer
argument_list|,
literal|"suggest_field"
argument_list|)
argument_list|)
init|)
block|{
comment|// mixing suggest field types for same field name should error out
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"mixed types"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWithSuggestFields
specifier|public
name|void
name|testWithSuggestFields
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwcWithSuggestField
argument_list|(
name|analyzer
argument_list|,
literal|"suggest_field"
argument_list|,
literal|"context_suggest_field"
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|SuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion1"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|SuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion2"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|SuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion3"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"context_suggest_field"
argument_list|,
literal|"suggestion1"
argument_list|,
literal|4
argument_list|,
literal|"type1"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"context_suggest_field"
argument_list|,
literal|"suggestion2"
argument_list|,
literal|3
argument_list|,
literal|"type2"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"context_suggest_field"
argument_list|,
literal|"suggestion3"
argument_list|,
literal|2
argument_list|,
literal|"type3"
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|SuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion4"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"context_suggest_field"
argument_list|,
literal|"suggestion4"
argument_list|,
literal|1
argument_list|,
literal|"type4"
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|DirectoryReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SuggestIndexSearcher
name|suggestIndexSearcher
init|=
operator|new
name|SuggestIndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|CompletionQuery
name|query
init|=
operator|new
name|PrefixCompletionQuery
argument_list|(
name|analyzer
argument_list|,
operator|new
name|Term
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"sugg"
argument_list|)
argument_list|)
decl_stmt|;
name|TopSuggestDocs
name|suggest
init|=
name|suggestIndexSearcher
operator|.
name|suggest
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertSuggestions
argument_list|(
name|suggest
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion1"
argument_list|,
literal|4
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion2"
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion3"
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion4"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|PrefixCompletionQuery
argument_list|(
name|analyzer
argument_list|,
operator|new
name|Term
argument_list|(
literal|"context_suggest_field"
argument_list|,
literal|"sugg"
argument_list|)
argument_list|)
expr_stmt|;
name|suggest
operator|=
name|suggestIndexSearcher
operator|.
name|suggest
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSuggestions
argument_list|(
name|suggest
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion1"
argument_list|,
literal|"type1"
argument_list|,
literal|4
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion2"
argument_list|,
literal|"type2"
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion3"
argument_list|,
literal|"type3"
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion4"
argument_list|,
literal|"type4"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompletionAnalyzer
specifier|public
name|void
name|testCompletionAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|CompletionAnalyzer
name|completionAnalyzer
init|=
operator|new
name|CompletionAnalyzer
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwcWithSuggestField
argument_list|(
name|completionAnalyzer
argument_list|,
literal|"suggest_field"
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion1"
argument_list|,
literal|4
argument_list|,
literal|"type1"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion2"
argument_list|,
literal|3
argument_list|,
literal|"type2"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion3"
argument_list|,
literal|2
argument_list|,
literal|"type3"
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|ContextSuggestField
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"suggestion4"
argument_list|,
literal|1
argument_list|,
literal|"type4"
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|DirectoryReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SuggestIndexSearcher
name|suggestIndexSearcher
init|=
operator|new
name|SuggestIndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ContextQuery
name|query
init|=
operator|new
name|ContextQuery
argument_list|(
operator|new
name|PrefixCompletionQuery
argument_list|(
name|completionAnalyzer
argument_list|,
operator|new
name|Term
argument_list|(
literal|"suggest_field"
argument_list|,
literal|"sugg"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TopSuggestDocs
name|suggest
init|=
name|suggestIndexSearcher
operator|.
name|suggest
argument_list|(
name|query
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertSuggestions
argument_list|(
name|suggest
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion1"
argument_list|,
literal|"type1"
argument_list|,
literal|4
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion2"
argument_list|,
literal|"type2"
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion3"
argument_list|,
literal|"type3"
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion4"
argument_list|,
literal|"type4"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|addContext
argument_list|(
literal|"type1"
argument_list|)
expr_stmt|;
name|suggest
operator|=
name|suggestIndexSearcher
operator|.
name|suggest
argument_list|(
name|query
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSuggestions
argument_list|(
name|suggest
argument_list|,
operator|new
name|Entry
argument_list|(
literal|"suggestion1"
argument_list|,
literal|"type1"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

