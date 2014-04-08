begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|lucene46
operator|.
name|Lucene46Codec
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
name|store
operator|.
name|BaseDirectoryWrapper
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

begin_comment
comment|/* Intentionally outside of oal.index to verify fully    external codecs work fine */
end_comment

begin_class
DECL|class|TestExternalCodecs
specifier|public
class|class
name|TestExternalCodecs
extends|extends
name|LuceneTestCase
block|{
DECL|class|CustomPerFieldCodec
specifier|private
specifier|static
specifier|final
class|class
name|CustomPerFieldCodec
extends|extends
name|Lucene46Codec
block|{
DECL|field|ramFormat
specifier|private
specifier|final
name|PostingsFormat
name|ramFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"RAMOnly"
argument_list|)
decl_stmt|;
DECL|field|defaultFormat
specifier|private
specifier|final
name|PostingsFormat
name|defaultFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Lucene41"
argument_list|)
decl_stmt|;
DECL|field|pulsingFormat
specifier|private
specifier|final
name|PostingsFormat
name|pulsingFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Pulsing41"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"field2"
argument_list|)
operator|||
name|field
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
return|return
name|pulsingFormat
return|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"field1"
argument_list|)
condition|)
block|{
return|return
name|defaultFormat
return|;
block|}
else|else
block|{
return|return
name|ramFormat
return|;
block|}
block|}
block|}
comment|// tests storing "id" and "field2" fields as pulsing codec,
comment|// whose term sort is backwards unicode code point, and
comment|// storing "field1" as a custom entirely-in-RAM codec
DECL|method|testPerFieldCodec
specifier|public
name|void
name|testPerFieldCodec
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|173
argument_list|)
decl_stmt|;
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
literal|"TEST: NUM_DOCS="
operator|+
name|NUM_DOCS
argument_list|)
expr_stmt|;
block|}
name|BaseDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we use a custom codec provider
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
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
name|setCodec
argument_list|(
operator|new
name|CustomPerFieldCodec
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// uses default codec:
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field1"
argument_list|,
literal|"this field uses the standard codec as the test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// uses pulsing codec:
name|Field
name|field2
init|=
name|newTextField
argument_list|(
literal|"field2"
argument_list|,
literal|"this field uses the pulsing codec as the test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
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
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
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
literal|"TEST: now delete id=77"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"77"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
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
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|1
argument_list|,
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
literal|"field1"
argument_list|,
literal|"standard"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|1
argument_list|,
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
literal|"field2"
argument_list|,
literal|"pulsing"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
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
literal|"\nTEST: now delete 2nd doc"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|)
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
literal|"\nTEST: now force merge"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
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
literal|"\nTEST: now open reader"
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|=
name|newSearcher
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
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
literal|"field1"
argument_list|,
literal|"standard"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
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
literal|"field2"
argument_list|,
literal|"pulsing"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
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
literal|"id"
argument_list|,
literal|"76"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
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
literal|"id"
argument_list|,
literal|"77"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
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
literal|"id"
argument_list|,
literal|"44"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
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
literal|"\nTEST: now close NRT reader"
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|shutdown
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

