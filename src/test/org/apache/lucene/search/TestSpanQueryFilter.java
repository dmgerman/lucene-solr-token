begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|spans
operator|.
name|SpanTermQuery
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
name|RAMDirectory
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
name|English
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|TestSpanQueryFilter
specifier|public
class|class
name|TestSpanQueryFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestSpanQueryFilter
specifier|public
name|TestSpanQueryFilter
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilterWorks
specifier|public
name|void
name|testFilterWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
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
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
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
literal|500
condition|;
name|i
operator|++
control|)
block|{
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
name|Field
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|" equals "
operator|+
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|query
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
literal|10
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQueryFilter
name|filter
init|=
operator|new
name|SpanQueryFilter
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SpanFilterResult
name|result
init|=
name|filter
operator|.
name|bitSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|BitSet
name|bits
init|=
name|result
operator|.
name|getBits
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"bits is null and it shouldn't be"
argument_list|,
name|bits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tenth bit is not on"
argument_list|,
name|bits
operator|.
name|get
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|spans
init|=
name|result
operator|.
name|getPositions
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"spans is null and it shouldn't be"
argument_list|,
name|spans
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"spans Size: "
operator|+
name|spans
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|,
name|spans
operator|.
name|size
argument_list|()
operator|==
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|spans
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SpanFilterResult
operator|.
name|PositionInfo
name|info
init|=
operator|(
name|SpanFilterResult
operator|.
name|PositionInfo
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"info is null and it shouldn't be"
argument_list|,
name|info
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//The doc should indicate the bit is on
name|assertTrue
argument_list|(
literal|"Bit is not on and it should be"
argument_list|,
name|bits
operator|.
name|get
argument_list|(
name|info
operator|.
name|getDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//There should be two positions in each
name|assertTrue
argument_list|(
literal|"info.getPositions() Size: "
operator|+
name|info
operator|.
name|getPositions
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|info
operator|.
name|getPositions
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

