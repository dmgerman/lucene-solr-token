begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|search
operator|.
name|TopDocs
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
name|MockDirectoryWrapper
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

begin_class
DECL|class|TestForTooMuchCloning
specifier|public
class|class
name|TestForTooMuchCloning
extends|extends
name|LuceneTestCase
block|{
comment|// Make sure we don't clone IndexInputs too frequently
comment|// during merging:
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: if we see a fail on this test with "NestedPulsing" its because its
comment|// reuse isnt perfect (but reasonable). see TestPulsingReuse.testNestedPulsing
comment|// for more details
specifier|final
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|TieredMergePolicy
name|tmp
init|=
operator|new
name|TieredMergePolicy
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|setMaxMergeAtOnce
argument_list|(
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|tmp
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
literal|20
decl_stmt|;
for|for
control|(
name|int
name|docs
init|=
literal|0
init|;
name|docs
operator|<
name|numDocs
condition|;
name|docs
operator|++
control|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|terms
init|=
literal|0
init|;
name|terms
operator|<
literal|100
condition|;
name|terms
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
specifier|final
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
literal|"field"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|int
name|cloneCount
init|=
name|dir
operator|.
name|getInputCloneCount
argument_list|()
decl_stmt|;
comment|//System.out.println("merge clone count=" + cloneCount);
name|assertTrue
argument_list|(
literal|"too many calls to IndexInput.clone during merging: "
operator|+
name|dir
operator|.
name|getInputCloneCount
argument_list|()
argument_list|,
name|cloneCount
operator|<
literal|500
argument_list|)
expr_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
comment|// MTQ that matches all terms so the AUTO_REWRITE should
comment|// cutover to filter rewrite and reuse a single DocsEnum
comment|// across all terms;
specifier|final
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermRangeQuery
argument_list|(
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"\uFFFF"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|totalHits
operator|>
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|queryCloneCount
init|=
name|dir
operator|.
name|getInputCloneCount
argument_list|()
operator|-
name|cloneCount
decl_stmt|;
comment|//System.out.println("query clone count=" + queryCloneCount);
name|assertTrue
argument_list|(
literal|"too many calls to IndexInput.clone during TermRangeQuery: "
operator|+
name|queryCloneCount
argument_list|,
name|queryCloneCount
operator|<
literal|50
argument_list|)
expr_stmt|;
name|r
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
block|}
end_class

end_unit

