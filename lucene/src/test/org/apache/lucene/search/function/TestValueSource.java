begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|util
operator|.
name|*
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
name|*
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
name|*
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
name|*
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
name|*
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
name|*
import|;
end_import

begin_class
DECL|class|TestValueSource
specifier|public
class|class
name|TestValueSource
extends|extends
name|LuceneTestCase
block|{
DECL|method|testMultiValueSource
specifier|public
name|void
name|testMultiValueSource
parameter_list|()
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
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
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
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
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
literal|17
condition|;
name|i
operator|++
control|)
block|{
name|f
operator|.
name|setValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
name|ValueSource
name|s1
init|=
operator|new
name|IntFieldSource
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|DocValues
name|v1
init|=
name|s1
operator|.
name|getValues
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|DocValues
name|v2
init|=
operator|new
name|MultiValueSource
argument_list|(
name|s1
argument_list|)
operator|.
name|getValues
argument_list|(
name|r
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
name|r
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|v1
operator|.
name|intVal
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v2
operator|.
name|intVal
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purgeAllCaches
argument_list|()
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

