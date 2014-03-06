begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|SlowCompositeReaderWrapper
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
name|Bits
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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|IndexSortingTest
specifier|public
class|class
name|IndexSortingTest
extends|extends
name|SorterTestBase
block|{
DECL|field|SORTERS
specifier|private
specifier|static
specifier|final
name|Sorter
index|[]
name|SORTERS
init|=
operator|new
name|Sorter
index|[]
block|{
operator|new
name|SortSorter
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|NUMERIC_DV_FIELD
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
argument_list|)
block|,
name|Sorter
operator|.
name|REVERSE_DOCS
block|,   }
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassSorterUtilTest
specifier|public
specifier|static
name|void
name|beforeClassSorterUtilTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// only read the values of the undeleted documents, since after addIndexes,
comment|// the deleted ones will be dropped from the index.
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
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
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Sorter
name|sorter
init|=
name|SORTERS
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|SORTERS
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
if|if
condition|(
name|sorter
operator|==
name|Sorter
operator|.
name|REVERSE_DOCS
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
if|if
condition|(
name|sorter
operator|instanceof
name|SortSorter
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|sorter
operator|=
operator|new
name|SortSorter
argument_list|(
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|NUMERIC_DV_FIELD
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// descending
name|Collections
operator|.
name|reverse
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
name|sortedValues
operator|=
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Integer
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sortedValues: "
operator|+
name|sortedValues
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sorter: "
operator|+
name|sorter
argument_list|)
expr_stmt|;
block|}
name|Directory
name|target
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
name|target
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|=
name|SortingAtomicReader
operator|.
name|wrap
argument_list|(
name|reader
argument_list|,
name|sorter
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
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
comment|// CheckIndex the target directory
name|dir
operator|=
name|target
expr_stmt|;
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|// set reader for tests
name|reader
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"index should not have deletions"
argument_list|,
name|reader
operator|.
name|hasDeletions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

