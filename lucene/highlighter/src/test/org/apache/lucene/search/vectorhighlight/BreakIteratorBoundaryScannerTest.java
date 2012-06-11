begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_class
DECL|class|BreakIteratorBoundaryScannerTest
specifier|public
class|class
name|BreakIteratorBoundaryScannerTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|TEXT
specifier|static
specifier|final
name|String
name|TEXT
init|=
literal|"Apache Lucene(TM) is a high-performance, full-featured text search engine library written entirely in Java."
operator|+
literal|"\nIt is a technology suitable for nearly any application that requires\n"
operator|+
literal|"full-text search, especially cross-platform. \nApache Lucene is an open source project available for free download."
decl_stmt|;
DECL|method|testOutOfRange
specifier|public
name|void
name|testOutOfRange
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
name|TEXT
argument_list|)
decl_stmt|;
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|BoundaryScanner
name|scanner
init|=
operator|new
name|BreakIteratorBoundaryScanner
argument_list|(
name|bi
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|TEXT
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
name|start
argument_list|,
name|scanner
operator|.
name|findStartOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|start
argument_list|,
name|scanner
operator|.
name|findEndOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
literal|0
expr_stmt|;
name|assertEquals
argument_list|(
name|start
argument_list|,
name|scanner
operator|.
name|findStartOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
operator|-
literal|1
expr_stmt|;
name|assertEquals
argument_list|(
name|start
argument_list|,
name|scanner
operator|.
name|findEndOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWordBoundary
specifier|public
name|void
name|testWordBoundary
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
name|TEXT
argument_list|)
decl_stmt|;
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|BoundaryScanner
name|scanner
init|=
operator|new
name|BreakIteratorBoundaryScanner
argument_list|(
name|bi
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"formance"
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"high-performance"
argument_list|)
decl_stmt|;
name|testFindStartOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|expected
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
name|expected
operator|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|", full"
argument_list|)
expr_stmt|;
name|testFindEndOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|expected
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
block|}
DECL|method|testSentenceBoundary
specifier|public
name|void
name|testSentenceBoundary
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
name|TEXT
argument_list|)
decl_stmt|;
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|()
decl_stmt|;
name|BoundaryScanner
name|scanner
init|=
operator|new
name|BreakIteratorBoundaryScanner
argument_list|(
name|bi
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"any application"
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"It is a"
argument_list|)
decl_stmt|;
name|testFindStartOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|expected
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
name|expected
operator|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"Apache Lucene is an open source"
argument_list|)
expr_stmt|;
name|testFindEndOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|expected
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
block|}
DECL|method|testLineBoundary
specifier|public
name|void
name|testLineBoundary
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
name|TEXT
argument_list|)
decl_stmt|;
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getLineInstance
argument_list|()
decl_stmt|;
name|BoundaryScanner
name|scanner
init|=
operator|new
name|BreakIteratorBoundaryScanner
argument_list|(
name|bi
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"any application"
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"nearly"
argument_list|)
decl_stmt|;
name|testFindStartOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|expected
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
name|expected
operator|=
name|TEXT
operator|.
name|indexOf
argument_list|(
literal|"application that requires"
argument_list|)
expr_stmt|;
name|testFindEndOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|expected
argument_list|,
name|scanner
argument_list|)
expr_stmt|;
block|}
DECL|method|testFindStartOffset
specifier|private
name|void
name|testFindStartOffset
parameter_list|(
name|StringBuilder
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|expected
parameter_list|,
name|BoundaryScanner
name|scanner
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|scanner
operator|.
name|findStartOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFindEndOffset
specifier|private
name|void
name|testFindEndOffset
parameter_list|(
name|StringBuilder
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|expected
parameter_list|,
name|BoundaryScanner
name|scanner
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|scanner
operator|.
name|findEndOffset
argument_list|(
name|text
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

