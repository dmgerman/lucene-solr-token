begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|document
operator|.
name|StringField
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
name|MultiDocValues
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
name|RandomIndexWriter
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
name|SortedDocValues
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  * trivial test of CollationDocValuesField  */
end_comment

begin_class
DECL|class|TestCollationDocValuesField
specifier|public
class|class
name|TestCollationDocValuesField
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
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
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|CollationDocValuesField
name|collationField
init|=
operator|new
name|CollationDocValuesField
argument_list|(
literal|"collated"
argument_list|,
name|Collator
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|collationField
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"ABC"
argument_list|)
expr_stmt|;
name|collationField
operator|.
name|setStringValue
argument_list|(
literal|"ABC"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|collationField
operator|.
name|setStringValue
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|SortField
name|sortField
init|=
operator|new
name|SortField
argument_list|(
literal|"collated"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|is
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|5
argument_list|,
operator|new
name|Sort
argument_list|(
name|sortField
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ABC"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
name|ir
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
DECL|method|testRanges
specifier|public
name|void
name|testRanges
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
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
comment|// uses -Dtests.locale
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
block|}
name|CollationDocValuesField
name|collationField
init|=
operator|new
name|CollationDocValuesField
argument_list|(
literal|"collated"
argument_list|,
name|collator
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|collationField
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|collationField
operator|.
name|setStringValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|int
name|numChecks
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numChecks
condition|;
name|i
operator|++
control|)
block|{
name|String
name|start
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|end
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|lowerVal
init|=
operator|new
name|BytesRef
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|start
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|upperVal
init|=
operator|new
name|BytesRef
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|end
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|doTestRanges
argument_list|(
name|is
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|lowerVal
argument_list|,
name|upperVal
argument_list|,
name|collator
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ir
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
DECL|method|doTestRanges
specifier|private
name|void
name|doTestRanges
parameter_list|(
name|IndexSearcher
name|is
parameter_list|,
name|String
name|startPoint
parameter_list|,
name|String
name|endPoint
parameter_list|,
name|BytesRef
name|startBR
parameter_list|,
name|BytesRef
name|endBR
parameter_list|,
name|Collator
name|collator
parameter_list|)
throws|throws
name|Exception
block|{
name|SortedDocValues
name|dvs
init|=
name|MultiDocValues
operator|.
name|getSortedValues
argument_list|(
name|is
operator|.
name|getIndexReader
argument_list|()
argument_list|,
literal|"collated"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|is
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|;
name|docID
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|is
operator|.
name|doc
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|boolean
name|collatorAccepts
init|=
name|collate
argument_list|(
name|collator
argument_list|,
name|s
argument_list|,
name|startPoint
argument_list|)
operator|>=
literal|0
operator|&&
name|collate
argument_list|(
name|collator
argument_list|,
name|s
argument_list|,
name|endPoint
argument_list|)
operator|<=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
name|docID
argument_list|,
name|dvs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|br
init|=
name|dvs
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|boolean
name|luceneAccepts
init|=
name|br
operator|.
name|compareTo
argument_list|(
name|startBR
argument_list|)
operator|>=
literal|0
operator|&&
name|br
operator|.
name|compareTo
argument_list|(
name|endBR
argument_list|)
operator|<=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
name|startPoint
operator|+
literal|"<= "
operator|+
name|s
operator|+
literal|"<= "
operator|+
name|endPoint
argument_list|,
name|collatorAccepts
argument_list|,
name|luceneAccepts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

