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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestMultiReader
specifier|public
class|class
name|TestMultiReader
extends|extends
name|TestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|doc1
specifier|private
name|Document
name|doc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|doc2
specifier|private
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|reader1
specifier|private
name|SegmentReader
name|reader1
decl_stmt|;
DECL|field|reader2
specifier|private
name|SegmentReader
name|reader2
decl_stmt|;
DECL|field|readers
specifier|private
name|SegmentReader
index|[]
name|readers
init|=
operator|new
name|SegmentReader
index|[
literal|2
index|]
decl_stmt|;
DECL|field|sis
specifier|private
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
DECL|method|TestMultiReader
specifier|public
name|TestMultiReader
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
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
literal|"seg-1"
argument_list|,
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
literal|"seg-2"
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
try|try
block|{
name|sis
operator|.
name|write
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader1
operator|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
literal|"seg-1"
argument_list|,
literal|1
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|reader2
operator|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
literal|"seg-2"
argument_list|,
literal|1
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|readers
index|[
literal|0
index|]
operator|=
name|reader1
expr_stmt|;
name|readers
index|[
literal|1
index|]
operator|=
name|reader2
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*IndexWriter writer  = new IndexWriter(dir, new WhitespaceAnalyzer(), true);       writer.addDocument(doc1);       writer.addDocument(doc2);       writer.close();*/
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader1
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sis
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocument
specifier|public
name|void
name|testDocument
parameter_list|()
block|{
try|try
block|{
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|MultiReader
name|reader
init|=
operator|new
name|MultiReader
argument_list|(
name|dir
argument_list|,
name|readers
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Document
name|newDoc1
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc1
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc1
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc1
argument_list|)
operator|-
literal|2
argument_list|)
expr_stmt|;
name|Document
name|newDoc2
init|=
name|reader
operator|.
name|document
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc2
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc2
argument_list|)
operator|-
literal|2
argument_list|)
expr_stmt|;
name|TermFreqVector
name|vector
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTermVectors
specifier|public
name|void
name|testTermVectors
parameter_list|()
block|{
try|try
block|{
name|MultiReader
name|reader
init|=
operator|new
name|MultiReader
argument_list|(
name|dir
argument_list|,
name|readers
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

