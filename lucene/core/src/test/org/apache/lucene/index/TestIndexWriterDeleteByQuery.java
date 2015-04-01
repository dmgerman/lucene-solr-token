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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|MatchAllDocsQuery
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestIndexWriterDeleteByQuery
specifier|public
class|class
name|TestIndexWriterDeleteByQuery
extends|extends
name|LuceneTestCase
block|{
comment|// LUCENE-6379
DECL|method|testDeleteMatchAllDocsQuery
specifier|public
name|void
name|testDeleteMatchAllDocsQuery
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
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Norms are disabled:
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldInfo
name|fi
init|=
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|r
argument_list|)
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fi
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r2
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r2
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// Confirm the omitNorms bit is in fact no longer set:
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// Norms are disabled:
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|DirectoryReader
name|r3
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r2
argument_list|)
decl_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|r3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r3
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r3
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure norms can come back to life for a field after deleting by MatchAllDocsQuery:
name|fi
operator|=
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|r3
argument_list|)
operator|.
name|fieldInfo
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fi
operator|.
name|hasNorms
argument_list|()
argument_list|)
expr_stmt|;
name|r3
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
