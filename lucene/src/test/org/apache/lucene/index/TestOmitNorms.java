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
name|_TestUtil
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
name|store
operator|.
name|Directory
import|;
end_import

begin_class
DECL|class|TestOmitNorms
specifier|public
class|class
name|TestOmitNorms
extends|extends
name|LuceneTestCase
block|{
comment|// Tests whether the DocumentWriter correctly enable the
comment|// omitNorms bit in the FieldInfo
DECL|method|testOmitNorms
specifier|public
name|void
name|testOmitNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// this field will have norms
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has norms"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// this field will NOT have norms
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has NO norms in all docs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|f2
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// now we add another document which has term freq for field f2 and not for f1 and verify if the SegmentMerger
comment|// keep things constant
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// Reverse
name|f1
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setOmitNorms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ram
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OmitNorms field bit should be set."
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OmitNorms field bit should be set."
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Tests whether merging of docs that have different
comment|// omitNorms for the same field works
DECL|method|testMixedMerge
specifier|public
name|void
name|testMixedMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|3
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// this field will have norms
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has norms"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// this field will NOT have norms
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has NO norms in all docs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|f2
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
comment|// now we add another document which has norms for field f2 and not for f1 and verify if the SegmentMerger
comment|// keep things constant
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// Reverese
name|f1
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setOmitNorms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ram
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OmitNorms field bit should be set."
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OmitNorms field bit should be set."
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Make sure first adding docs that do not omitNorms for
comment|// field X, then adding docs that do omitNorms for that same
comment|// field,
DECL|method|testMixedRAM
specifier|public
name|void
name|testMixedRAM
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// this field will have norms
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has norms"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// this field will NOT have norms
name|Field
name|f2
init|=
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"This field has NO norms in all docs"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|f2
operator|.
name|setOmitNorms
argument_list|(
literal|true
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|ram
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OmitNorms field bit should not be set."
argument_list|,
operator|!
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OmitNorms field bit should be set."
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertNoNrm
specifier|private
name|void
name|assertNoNrm
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|Throwable
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|".nrm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Verifies no *.nrm exists when all fields omit norms:
DECL|method|testNoNrmFile
specifier|public
name|void
name|testNoNrmFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|ram
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ram
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|3
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f1
init|=
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"This field has no norms"
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
name|ANALYZED
argument_list|)
decl_stmt|;
name|f1
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNoNrm
argument_list|(
name|ram
argument_list|)
expr_stmt|;
comment|// force merge
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
comment|// flush
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNoNrm
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|ram
argument_list|)
expr_stmt|;
name|ram
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests various combinations of omitNorms=true/false, the field not existing at all,    * ensuring that only omitNorms is 'viral'.    * Internally checks that MultiNorms.norms() is consistent (returns the same bytes)    * as the optimized equivalent.    */
DECL|method|testOmitNormsCombos
specifier|public
name|void
name|testOmitNormsCombos
parameter_list|()
throws|throws
name|IOException
block|{
comment|// indexed with norms
name|Field
name|norms
init|=
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
comment|// indexed without norms
name|Field
name|noNorms
init|=
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED_NO_NORMS
argument_list|)
decl_stmt|;
comment|// not indexed, but stored
name|Field
name|noIndex
init|=
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
decl_stmt|;
comment|// not indexed but stored, omitNorms is set
name|Field
name|noNormsNoIndex
init|=
operator|new
name|Field
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
decl_stmt|;
name|noNormsNoIndex
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// not indexed nor stored (doesnt exist at all, we index a different field instead)
name|Field
name|emptyNorms
init|=
operator|new
name|Field
argument_list|(
literal|"bar"
argument_list|,
literal|"a"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
name|norms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
name|noNorms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
name|noIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
name|noNormsNoIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|norms
argument_list|,
name|emptyNorms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noNorms
argument_list|,
name|noNorms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noNorms
argument_list|,
name|noIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noNorms
argument_list|,
name|noNormsNoIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noNorms
argument_list|,
name|emptyNorms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noIndex
argument_list|,
name|noIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noIndex
argument_list|,
name|noNormsNoIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noIndex
argument_list|,
name|emptyNorms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noNormsNoIndex
argument_list|,
name|noNormsNoIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|noNormsNoIndex
argument_list|,
name|emptyNorms
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getNorms
argument_list|(
literal|"foo"
argument_list|,
name|emptyNorms
argument_list|,
name|emptyNorms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Indexes at least 1 document with f1, and at least 1 document with f2.    * returns the norms for "field".    */
DECL|method|getNorms
specifier|static
name|byte
index|[]
name|getNorms
parameter_list|(
name|String
name|field
parameter_list|,
name|Field
name|f1
parameter_list|,
name|Field
name|f2
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newInOrderLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
comment|// add f1
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|riw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// add f2
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|riw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// add a mix of f1's and f2's
name|int
name|numExtraDocs
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|1000
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
name|numExtraDocs
condition|;
name|i
operator|++
control|)
block|{
name|d
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|f1
else|:
name|f2
argument_list|)
expr_stmt|;
name|riw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir1
init|=
name|riw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|byte
index|[]
name|norms1
init|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|ir1
argument_list|,
name|field
argument_list|)
decl_stmt|;
comment|// optimize and validate MultiNorms against single segment.
name|riw
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|IndexReader
name|ir2
init|=
name|riw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|byte
index|[]
name|norms2
init|=
name|ir2
operator|.
name|getSequentialSubReaders
argument_list|()
index|[
literal|0
index|]
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|norms1
argument_list|,
name|norms2
argument_list|)
expr_stmt|;
name|ir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|norms1
return|;
block|}
block|}
end_class

end_unit

