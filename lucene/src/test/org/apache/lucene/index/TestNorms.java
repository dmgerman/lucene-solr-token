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
name|Random
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|DefaultSimilarity
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
name|Similarity
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
comment|/**  * Test that norms info is preserved during index life - including  * separate norms, addDocument, addIndexes, optimize.  */
end_comment

begin_class
DECL|class|TestNorms
specifier|public
class|class
name|TestNorms
extends|extends
name|LuceneTestCase
block|{
DECL|class|SimilarityOne
specifier|private
class|class
name|SimilarityOne
extends|extends
name|DefaultSimilarity
block|{
annotation|@
name|Override
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
block|}
DECL|field|NUM_FIELDS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_FIELDS
init|=
literal|10
decl_stmt|;
DECL|field|similarityOne
specifier|private
name|Similarity
name|similarityOne
decl_stmt|;
DECL|field|anlzr
specifier|private
name|Analyzer
name|anlzr
decl_stmt|;
DECL|field|numDocNorms
specifier|private
name|int
name|numDocNorms
decl_stmt|;
DECL|field|norms
specifier|private
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|norms
decl_stmt|;
DECL|field|modifiedNorms
specifier|private
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|modifiedNorms
decl_stmt|;
DECL|field|lastNorm
specifier|private
name|float
name|lastNorm
init|=
literal|0
decl_stmt|;
DECL|field|normDelta
specifier|private
name|float
name|normDelta
init|=
operator|(
name|float
operator|)
literal|0.001
decl_stmt|;
DECL|method|TestNorms
specifier|public
name|TestNorms
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
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|similarityOne
operator|=
operator|new
name|SimilarityOne
argument_list|()
expr_stmt|;
name|anlzr
operator|=
operator|new
name|MockAnalyzer
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that norms values are preserved as the index is maintained.    * Including separate norms.    * Including merging indexes with seprate norms.     * Including optimize.     */
DECL|method|testNorms
specifier|public
name|void
name|testNorms
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|Directory
name|dir1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|norms
operator|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|()
expr_stmt|;
name|modifiedNorms
operator|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|()
expr_stmt|;
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|)
expr_stmt|;
name|doTestNorms
argument_list|(
name|random
argument_list|,
name|dir1
argument_list|)
expr_stmt|;
comment|// test with a single index: index2
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|norms1
init|=
name|norms
decl_stmt|;
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|modifiedNorms1
init|=
name|modifiedNorms
decl_stmt|;
name|int
name|numDocNorms1
init|=
name|numDocNorms
decl_stmt|;
name|norms
operator|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|()
expr_stmt|;
name|modifiedNorms
operator|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|()
expr_stmt|;
name|numDocNorms
operator|=
literal|0
expr_stmt|;
name|Directory
name|dir2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
name|doTestNorms
argument_list|(
name|random
argument_list|,
name|dir2
argument_list|)
expr_stmt|;
comment|// add index1 and index2 to a third index: index3
name|Directory
name|dir3
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
name|random
argument_list|,
name|dir3
argument_list|)
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir3
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
name|anlzr
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|iw
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addIndexes
argument_list|(
operator|new
name|Directory
index|[]
block|{
name|dir1
block|,
name|dir2
block|}
argument_list|)
expr_stmt|;
name|iw
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|norms1
operator|.
name|addAll
argument_list|(
name|norms
argument_list|)
expr_stmt|;
name|norms
operator|=
name|norms1
expr_stmt|;
name|modifiedNorms1
operator|.
name|addAll
argument_list|(
name|modifiedNorms
argument_list|)
expr_stmt|;
name|modifiedNorms
operator|=
name|modifiedNorms1
expr_stmt|;
name|numDocNorms
operator|+=
name|numDocNorms1
expr_stmt|;
comment|// test with index3
name|verifyIndex
argument_list|(
name|dir3
argument_list|)
expr_stmt|;
name|doTestNorms
argument_list|(
name|random
argument_list|,
name|dir3
argument_list|)
expr_stmt|;
comment|// now with optimize
name|iw
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir3
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
name|anlzr
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|iw
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|iw
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|verifyIndex
argument_list|(
name|dir3
argument_list|)
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir3
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestNorms
specifier|private
name|void
name|doTestNorms
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|addDocs
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
literal|12
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verifyIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|modifyNormsForF1
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|verifyIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
literal|12
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|verifyIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|modifyNormsForF1
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|verifyIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createIndex
specifier|private
name|void
name|createIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
name|anlzr
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|5
argument_list|)
operator|.
name|setSimilarity
argument_list|(
name|similarityOne
argument_list|)
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|iw
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
literal|3
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundDocStore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|modifyNormsForF1
specifier|private
name|void
name|modifyNormsForF1
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|ir
operator|.
name|maxDoc
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
name|n
condition|;
name|i
operator|+=
literal|3
control|)
block|{
comment|// modify for every third doc
name|int
name|k
init|=
operator|(
name|i
operator|*
literal|3
operator|)
operator|%
name|modifiedNorms
operator|.
name|size
argument_list|()
decl_stmt|;
name|float
name|origNorm
init|=
name|modifiedNorms
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|float
name|newNorm
init|=
name|modifiedNorms
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
comment|//System.out.println("Modifying: for "+i+" from "+origNorm+" to "+newNorm);
comment|//System.out.println("      and: for "+k+" from "+newNorm+" to "+origNorm);
name|modifiedNorms
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|newNorm
argument_list|)
argument_list|)
expr_stmt|;
name|modifiedNorms
operator|.
name|set
argument_list|(
name|k
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|origNorm
argument_list|)
argument_list|)
expr_stmt|;
name|ir
operator|.
name|setNorm
argument_list|(
name|i
argument_list|,
literal|"f"
operator|+
literal|1
argument_list|,
name|newNorm
argument_list|)
expr_stmt|;
name|ir
operator|.
name|setNorm
argument_list|(
name|k
argument_list|,
literal|"f"
operator|+
literal|1
argument_list|,
name|origNorm
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyIndex
specifier|private
name|void
name|verifyIndex
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
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
name|NUM_FIELDS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
literal|"f"
operator|+
name|i
decl_stmt|;
name|byte
name|b
index|[]
init|=
name|ir
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"number of norms mismatches"
argument_list|,
name|numDocNorms
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|storedNorms
init|=
operator|(
name|i
operator|==
literal|1
condition|?
name|modifiedNorms
else|:
name|norms
operator|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|b
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|float
name|norm
init|=
name|similarityOne
operator|.
name|decodeNormValue
argument_list|(
name|b
index|[
name|j
index|]
argument_list|)
decl_stmt|;
name|float
name|norm1
init|=
name|storedNorms
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"stored norm value of "
operator|+
name|field
operator|+
literal|" for doc "
operator|+
name|j
operator|+
literal|" is "
operator|+
name|norm
operator|+
literal|" - a mismatch!"
argument_list|,
name|norm
argument_list|,
name|norm1
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
block|}
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|ndocs
parameter_list|,
name|boolean
name|compound
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
name|anlzr
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|5
argument_list|)
operator|.
name|setSimilarity
argument_list|(
name|similarityOne
argument_list|)
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|iw
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
literal|3
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundFile
argument_list|(
name|compound
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundDocStore
argument_list|(
name|compound
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|iw
operator|.
name|addDocument
argument_list|(
name|newDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// create the next document
DECL|method|newDoc
specifier|private
name|Document
name|newDoc
parameter_list|()
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|float
name|boost
init|=
name|nextNorm
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"f"
operator|+
name|i
argument_list|,
literal|"v"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|d
return|;
block|}
comment|// return unique norm values that are unchanged by encoding/decoding
DECL|method|nextNorm
specifier|private
name|float
name|nextNorm
parameter_list|()
block|{
name|float
name|norm
init|=
name|lastNorm
operator|+
name|normDelta
decl_stmt|;
do|do
block|{
name|float
name|norm1
init|=
name|similarityOne
operator|.
name|decodeNormValue
argument_list|(
name|similarityOne
operator|.
name|encodeNormValue
argument_list|(
name|norm
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|norm1
operator|>
name|lastNorm
condition|)
block|{
comment|//System.out.println(norm1+"> "+lastNorm);
name|norm
operator|=
name|norm1
expr_stmt|;
break|break;
block|}
name|norm
operator|+=
name|normDelta
expr_stmt|;
block|}
do|while
condition|(
literal|true
condition|)
do|;
name|norms
operator|.
name|add
argument_list|(
name|numDocNorms
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|norm
argument_list|)
argument_list|)
expr_stmt|;
name|modifiedNorms
operator|.
name|add
argument_list|(
name|numDocNorms
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|norm
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("creating norm("+numDocNorms+"): "+norm);
name|numDocNorms
operator|++
expr_stmt|;
name|lastNorm
operator|=
operator|(
name|norm
operator|>
literal|10
condition|?
literal|0
else|:
name|norm
operator|)
expr_stmt|;
comment|//there's a limit to how many distinct values can be stored in a ingle byte
return|return
name|norm
return|;
block|}
block|}
end_class

end_unit

