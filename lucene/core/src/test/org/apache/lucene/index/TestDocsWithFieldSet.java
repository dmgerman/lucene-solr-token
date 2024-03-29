begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestDocsWithFieldSet
specifier|public
class|class
name|TestDocsWithFieldSet
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDense
specifier|public
name|void
name|testDense
parameter_list|()
throws|throws
name|IOException
block|{
name|DocsWithFieldSet
name|set
init|=
operator|new
name|DocsWithFieldSet
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|ramBytesUsed
init|=
name|set
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ramBytesUsed
argument_list|,
name|set
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSparse
specifier|public
name|void
name|testSparse
parameter_list|()
throws|throws
name|IOException
block|{
name|DocsWithFieldSet
name|set
init|=
operator|new
name|DocsWithFieldSet
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|doc2
init|=
name|doc
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|it
operator|=
name|set
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc2
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDenseThenSparse
specifier|public
name|void
name|testDenseThenSparse
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|denseCount
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|int
name|nextDoc
init|=
name|denseCount
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|DocsWithFieldSet
name|set
init|=
operator|new
name|DocsWithFieldSet
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
name|denseCount
condition|;
operator|++
name|i
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|add
argument_list|(
name|nextDoc
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
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
name|denseCount
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|nextDoc
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

