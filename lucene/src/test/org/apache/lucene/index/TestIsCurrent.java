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
operator|.
name|Index
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
operator|.
name|Store
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
name|junit
operator|.
name|Test
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
DECL|class|TestIsCurrent
specifier|public
class|class
name|TestIsCurrent
extends|extends
name|LuceneTestCase
block|{
DECL|field|writer
specifier|private
name|RandomIndexWriter
name|writer
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// initialize directory
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|)
expr_stmt|;
comment|// write document
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
name|newField
argument_list|(
literal|"UUID"
argument_list|,
literal|"1"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|ANALYZED
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
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|writer
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
comment|/**    * Failing testcase showing the trouble    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testDeleteByTermIsCurrent
specifier|public
name|void
name|testDeleteByTermIsCurrent
parameter_list|()
throws|throws
name|IOException
block|{
comment|// get reader
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// assert index has a document and reader is up2date
name|assertEquals
argument_list|(
literal|"One document should be in the index"
argument_list|,
literal|1
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Document added, reader should be stale "
argument_list|,
name|reader
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove document
name|Term
name|idTerm
init|=
operator|new
name|Term
argument_list|(
literal|"UUID"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|idTerm
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// assert document has been deleted (index changed), reader is stale
name|assertEquals
argument_list|(
literal|"Document should be removed"
argument_list|,
literal|0
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Reader should be stale"
argument_list|,
name|reader
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Testcase for example to show that writer.deleteAll() is working as expected    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testDeleteAllIsCurrent
specifier|public
name|void
name|testDeleteAllIsCurrent
parameter_list|()
throws|throws
name|IOException
block|{
comment|// get reader
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// assert index has a document and reader is up2date
name|assertEquals
argument_list|(
literal|"One document should be in the index"
argument_list|,
literal|1
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Document added, reader should be stale "
argument_list|,
name|reader
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove all documents
name|writer
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// assert document has been deleted (index changed), reader is stale
name|assertEquals
argument_list|(
literal|"Document should be removed"
argument_list|,
literal|0
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Reader should be stale"
argument_list|,
name|reader
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

