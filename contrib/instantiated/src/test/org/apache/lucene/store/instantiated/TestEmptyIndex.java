begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

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
name|index
operator|.
name|TermEnum
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
name|TopDocCollector
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestEmptyIndex
specifier|public
class|class
name|TestEmptyIndex
extends|extends
name|TestCase
block|{
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|InstantiatedIndexReader
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|TopDocCollector
name|c
init|=
operator|new
name|TopDocCollector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|c
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNorms
specifier|public
name|void
name|testNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|InstantiatedIndexReader
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|testNorms
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure a Directory acts the same
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|testNorms
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNorms
specifier|private
name|void
name|testNorms
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|norms
decl_stmt|;
name|norms
operator|=
name|r
operator|.
name|norms
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|r
operator|.
name|getDisableFakeNorms
argument_list|()
condition|)
block|{
name|assertNotNull
argument_list|(
name|norms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|norms
operator|.
name|length
argument_list|)
expr_stmt|;
name|norms
operator|=
operator|new
name|byte
index|[
literal|10
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|norms
argument_list|,
operator|(
name|byte
operator|)
literal|10
argument_list|)
expr_stmt|;
name|r
operator|.
name|norms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|norms
control|)
block|{
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testTermEnum
specifier|public
name|void
name|testTermEnum
parameter_list|()
throws|throws
name|Exception
block|{
name|InstantiatedIndex
name|ii
init|=
operator|new
name|InstantiatedIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
operator|new
name|InstantiatedIndexReader
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|termEnumTest
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ii
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// make sure a Directory acts the same
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|termEnumTest
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|termEnumTest
specifier|public
name|void
name|termEnumTest
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|Exception
block|{
name|TermEnum
name|terms
init|=
name|r
operator|.
name|terms
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|terms
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|terms
operator|.
name|skipTo
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

