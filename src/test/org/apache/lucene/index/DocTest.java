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
name|store
operator|.
name|FSDirectory
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
name|search
operator|.
name|Similarity
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
name|demo
operator|.
name|FileDocument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|// FIXME: OG: remove hard-coded file names
end_comment

begin_class
DECL|class|DocTest
class|class
name|DocTest
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
literal|"test"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDoc
argument_list|(
literal|"one"
argument_list|,
literal|"test.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
literal|"one"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
literal|"two"
argument_list|,
literal|"test2.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
literal|"two"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"merge"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
literal|"merge"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"merge2"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
literal|"merge2"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"merge"
argument_list|,
literal|"merge2"
argument_list|,
literal|"merge3"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
literal|"merge3"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|indexDoc
specifier|public
specifier|static
name|void
name|indexDoc
parameter_list|(
name|String
name|segment
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
name|Similarity
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|FileDocument
operator|.
name|Document
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|segment
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|merge
specifier|static
name|void
name|merge
parameter_list|(
name|String
name|seg1
parameter_list|,
name|String
name|seg2
parameter_list|,
name|String
name|merged
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentReader
name|r1
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|seg1
argument_list|,
literal|1
argument_list|,
name|directory
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentReader
name|r2
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|seg2
argument_list|,
literal|1
argument_list|,
name|directory
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|directory
argument_list|,
name|merged
argument_list|)
decl_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|merger
operator|.
name|merge
argument_list|()
expr_stmt|;
name|merger
operator|.
name|closeReaders
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|printSegment
specifier|static
name|void
name|printSegment
parameter_list|(
name|String
name|segment
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|segment
argument_list|,
literal|1
argument_list|,
name|directory
argument_list|)
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
name|reader
operator|.
name|numDocs
argument_list|()
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|TermEnum
name|tis
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|tis
operator|.
name|next
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|tis
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" DF="
operator|+
name|tis
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|TermPositions
name|positions
init|=
name|reader
operator|.
name|termPositions
argument_list|(
name|tis
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|positions
operator|.
name|next
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" doc="
operator|+
name|positions
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" TF="
operator|+
name|positions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" pos="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|positions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|positions
operator|.
name|freq
argument_list|()
condition|;
name|j
operator|++
control|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|","
operator|+
name|positions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|positions
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

