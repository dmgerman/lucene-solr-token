begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|IOException
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
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|document
operator|.
name|Field
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
name|FieldType
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
name|StringField
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
name|TextField
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
name|IndexReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|IndexableField
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
name|search
operator|.
name|IndexSearcher
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
name|ScoreDoc
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
name|Sort
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
name|SortField
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
name|search
operator|.
name|TermRangeFilter
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
name|TermRangeQuery
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
name|LuceneTestCase
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
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Base test class for testing Unicode collation.  */
end_comment

begin_class
DECL|class|CollationTestBase
specifier|public
specifier|abstract
class|class
name|CollationTestBase
extends|extends
name|LuceneTestCase
block|{
DECL|field|firstRangeBeginningOriginal
specifier|protected
name|String
name|firstRangeBeginningOriginal
init|=
literal|"\u062F"
decl_stmt|;
DECL|field|firstRangeEndOriginal
specifier|protected
name|String
name|firstRangeEndOriginal
init|=
literal|"\u0698"
decl_stmt|;
DECL|field|secondRangeBeginningOriginal
specifier|protected
name|String
name|secondRangeBeginningOriginal
init|=
literal|"\u0633"
decl_stmt|;
DECL|field|secondRangeEndOriginal
specifier|protected
name|String
name|secondRangeEndOriginal
init|=
literal|"\u0638"
decl_stmt|;
DECL|method|testFarsiRangeFilterCollating
specifier|public
name|void
name|testFarsiRangeFilterCollating
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BytesRef
name|firstBeg
parameter_list|,
name|BytesRef
name|firstEnd
parameter_list|,
name|BytesRef
name|secondBeg
parameter_list|,
name|BytesRef
name|secondEnd
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633\u0627\u0628"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a TermRangeFilter with a Farsi
comment|// Collator (or an Arabic one for the case when Farsi searcher not
comment|// supported).
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|TermRangeFilter
argument_list|(
literal|"content"
argument_list|,
name|firstBeg
argument_list|,
name|firstEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should not be included."
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|TermRangeFilter
argument_list|(
literal|"content"
argument_list|,
name|secondBeg
argument_list|,
name|secondEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should be included."
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFarsiRangeQueryCollating
specifier|public
name|void
name|testFarsiRangeQueryCollating
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BytesRef
name|firstBeg
parameter_list|,
name|BytesRef
name|firstEnd
parameter_list|,
name|BytesRef
name|secondBeg
parameter_list|,
name|BytesRef
name|secondEnd
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a TermRangeQuery with a Farsi
comment|// Collator (or an Arabic one for the case when Farsi is not supported).
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633\u0627\u0628"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|firstBeg
argument_list|,
name|firstEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should not be included."
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|secondBeg
argument_list|,
name|secondEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should be included."
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFarsiTermRangeQuery
specifier|public
name|void
name|testFarsiTermRangeQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BytesRef
name|firstBeg
parameter_list|,
name|BytesRef
name|firstEnd
parameter_list|,
name|BytesRef
name|secondBeg
parameter_list|,
name|BytesRef
name|secondEnd
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|farsiIndex
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|farsiIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633\u0627\u0628"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|farsiIndex
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a TermRangeQuery
comment|// with a Farsi Collator (or an Arabic one for the case when Farsi is
comment|// not supported).
name|Query
name|csrq
init|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|firstBeg
argument_list|,
name|firstEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
init|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should not be included."
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|csrq
operator|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|secondBeg
argument_list|,
name|secondEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should be included."
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|farsiIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test using various international locales with accented characters (which
comment|// sort differently depending on locale)
comment|//
comment|// Copied (and slightly modified) from
comment|// org.apache.lucene.search.TestSort.testInternationalSort()
comment|//
comment|// TODO: this test is really fragile. there are already 3 different cases,
comment|// depending upon unicode version.
DECL|method|testCollationKeySort
specifier|public
name|void
name|testCollationKeySort
parameter_list|(
name|Analyzer
name|usAnalyzer
parameter_list|,
name|Analyzer
name|franceAnalyzer
parameter_list|,
name|Analyzer
name|swedenAnalyzer
parameter_list|,
name|Analyzer
name|denmarkAnalyzer
parameter_list|,
name|String
name|usResult
parameter_list|,
name|String
name|frResult
parameter_list|,
name|String
name|svResult
parameter_list|,
name|String
name|dkResult
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|indexStore
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// document data:
comment|// the tracer field is used to determine which document was hit
name|String
index|[]
index|[]
name|sortData
init|=
operator|new
name|String
index|[]
index|[]
block|{
comment|// tracer contents US                 France             Sweden (sv_SE)     Denmark (da_DK)
block|{
literal|"A"
block|,
literal|"x"
block|,
literal|"p\u00EAche"
block|,
literal|"p\u00EAche"
block|,
literal|"p\u00EAche"
block|,
literal|"p\u00EAche"
block|}
block|,
block|{
literal|"B"
block|,
literal|"y"
block|,
literal|"HAT"
block|,
literal|"HAT"
block|,
literal|"HAT"
block|,
literal|"HAT"
block|}
block|,
block|{
literal|"C"
block|,
literal|"x"
block|,
literal|"p\u00E9ch\u00E9"
block|,
literal|"p\u00E9ch\u00E9"
block|,
literal|"p\u00E9ch\u00E9"
block|,
literal|"p\u00E9ch\u00E9"
block|}
block|,
block|{
literal|"D"
block|,
literal|"y"
block|,
literal|"HUT"
block|,
literal|"HUT"
block|,
literal|"HUT"
block|,
literal|"HUT"
block|}
block|,
block|{
literal|"E"
block|,
literal|"x"
block|,
literal|"peach"
block|,
literal|"peach"
block|,
literal|"peach"
block|,
literal|"peach"
block|}
block|,
block|{
literal|"F"
block|,
literal|"y"
block|,
literal|"H\u00C5T"
block|,
literal|"H\u00C5T"
block|,
literal|"H\u00C5T"
block|,
literal|"H\u00C5T"
block|}
block|,
block|{
literal|"G"
block|,
literal|"x"
block|,
literal|"sin"
block|,
literal|"sin"
block|,
literal|"sin"
block|,
literal|"sin"
block|}
block|,
block|{
literal|"H"
block|,
literal|"y"
block|,
literal|"H\u00D8T"
block|,
literal|"H\u00D8T"
block|,
literal|"H\u00D8T"
block|,
literal|"H\u00D8T"
block|}
block|,
block|{
literal|"I"
block|,
literal|"x"
block|,
literal|"s\u00EDn"
block|,
literal|"s\u00EDn"
block|,
literal|"s\u00EDn"
block|,
literal|"s\u00EDn"
block|}
block|,
block|{
literal|"J"
block|,
literal|"y"
block|,
literal|"HOT"
block|,
literal|"HOT"
block|,
literal|"HOT"
block|,
literal|"HOT"
block|}
block|,     }
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sortData
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"tracer"
argument_list|,
name|sortData
index|[
name|i
index|]
index|[
literal|0
index|]
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"contents"
argument_list|,
name|sortData
index|[
name|i
index|]
index|[
literal|1
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortData
index|[
name|i
index|]
index|[
literal|2
index|]
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"US"
argument_list|,
name|usAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"US"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|sortData
index|[
name|i
index|]
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortData
index|[
name|i
index|]
index|[
literal|3
index|]
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"France"
argument_list|,
name|franceAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"France"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|sortData
index|[
name|i
index|]
index|[
literal|3
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortData
index|[
name|i
index|]
index|[
literal|4
index|]
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"Sweden"
argument_list|,
name|swedenAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"Sweden"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|sortData
index|[
name|i
index|]
index|[
literal|4
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortData
index|[
name|i
index|]
index|[
literal|5
index|]
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"Denmark"
argument_list|,
name|denmarkAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"Denmark"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|sortData
index|[
name|i
index|]
index|[
literal|5
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|()
decl_stmt|;
name|Query
name|queryX
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|queryY
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
decl_stmt|;
name|sort
operator|.
name|setSort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"US"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|queryY
argument_list|,
name|sort
argument_list|,
name|usResult
argument_list|)
expr_stmt|;
name|sort
operator|.
name|setSort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"France"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|queryX
argument_list|,
name|sort
argument_list|,
name|frResult
argument_list|)
expr_stmt|;
name|sort
operator|.
name|setSort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"Sweden"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|queryY
argument_list|,
name|sort
argument_list|,
name|svResult
argument_list|)
expr_stmt|;
name|sort
operator|.
name|setSort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"Denmark"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|queryY
argument_list|,
name|sort
argument_list|,
name|dkResult
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Make sure the documents returned by the search match the expected list
comment|// Copied from TestSort.java
DECL|method|assertMatches
specifier|private
name|void
name|assertMatches
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|String
name|expectedResult
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|result
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|result
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|v
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"tracer"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|v
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|v
index|[
name|j
index|]
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertThreadSafe
specifier|public
name|void
name|assertThreadSafe
parameter_list|(
specifier|final
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numTestPoints
init|=
literal|100
decl_stmt|;
name|int
name|numThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
comment|// create a map<String,SortKey> up front.
comment|// then with multiple threads, generate sort keys for all the keys in the map
comment|// and ensure they are the same as the ones we produced in serial fashion.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTestPoints
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"fake"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|term
argument_list|)
argument_list|)
decl_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
comment|// ensure we make a copy of the actual bytes too
name|map
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Thread
name|threads
index|[]
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|mapping
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|term
init|=
name|mapping
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
name|mapping
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"fake"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|term
argument_list|)
argument_list|)
decl_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

