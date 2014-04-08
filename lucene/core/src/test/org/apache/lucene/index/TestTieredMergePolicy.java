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
DECL|class|TestTieredMergePolicy
specifier|public
class|class
name|TestTieredMergePolicy
extends|extends
name|BaseMergePolicyTestCase
block|{
DECL|method|mergePolicy
specifier|public
name|MergePolicy
name|mergePolicy
parameter_list|()
block|{
return|return
name|newTieredMergePolicy
argument_list|()
return|;
block|}
DECL|method|testForceMergeDeletes
specifier|public
name|void
name|testForceMergeDeletes
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
name|IndexWriterConfig
name|conf
init|=
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
decl_stmt|;
name|TieredMergePolicy
name|tmp
init|=
name|newTieredMergePolicy
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxMergeAtOnce
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setSegmentsPerTier
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setForceMergeDeletesPctAllowed
argument_list|(
literal|30.0
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
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
literal|80
condition|;
name|i
operator|++
control|)
block|{
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
name|newTextField
argument_list|(
literal|"content"
argument_list|,
literal|"aaa "
operator|+
operator|(
name|i
operator|%
literal|4
operator|)
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
block|}
name|assertEquals
argument_list|(
literal|80
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|80
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
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
literal|"\nTEST: delete docs"
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
literal|"content"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|80
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
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
literal|"\nTEST: forceMergeDeletes2"
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|TieredMergePolicy
operator|)
name|w
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setForceMergeDeletesPctAllowed
argument_list|(
literal|10.0
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
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
DECL|method|testPartialMerge
specifier|public
name|void
name|testPartialMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
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
literal|"TEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
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
decl_stmt|;
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|TieredMergePolicy
name|tmp
init|=
name|newTieredMergePolicy
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxMergeAtOnce
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setSegmentsPerTier
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|int
name|maxCount
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|20
argument_list|,
literal|100
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
name|newTextField
argument_list|(
literal|"content"
argument_list|,
literal|"aaa "
operator|+
operator|(
name|i
operator|%
literal|4
operator|)
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
name|int
name|count
init|=
name|w
operator|.
name|getSegmentCount
argument_list|()
decl_stmt|;
name|maxCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|count
argument_list|,
name|maxCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"count="
operator|+
name|count
operator|+
literal|" maxCount="
operator|+
name|maxCount
argument_list|,
name|count
operator|>=
name|maxCount
operator|-
literal|3
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|int
name|segmentCount
init|=
name|w
operator|.
name|getSegmentCount
argument_list|()
decl_stmt|;
name|int
name|targetCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|segmentCount
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
literal|"TEST: merge to "
operator|+
name|targetCount
operator|+
literal|" segs (current count="
operator|+
name|segmentCount
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
name|targetCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|targetCount
argument_list|,
name|w
operator|.
name|getSegmentCount
argument_list|()
argument_list|)
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
DECL|method|testForceMergeDeletesMaxSegSize
specifier|public
name|void
name|testForceMergeDeletesMaxSegSize
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|conf
init|=
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
decl_stmt|;
specifier|final
name|TieredMergePolicy
name|tmp
init|=
operator|new
name|TieredMergePolicy
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|setMaxMergedSegmentMB
argument_list|(
literal|0.01
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setForceMergeDeletesPctAllowed
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|w
operator|.
name|setDoRandomForceMerge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|200
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
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"content"
argument_list|,
literal|"aaa "
operator|+
name|i
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
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
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
literal|"\nTEST: delete doc"
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
literal|""
operator|+
operator|(
literal|42
operator|+
literal|17
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|field|EPSILON
specifier|private
specifier|static
specifier|final
name|double
name|EPSILON
init|=
literal|1E
operator|-
literal|14
decl_stmt|;
DECL|method|testSetters
specifier|public
name|void
name|testSetters
parameter_list|()
block|{
specifier|final
name|TieredMergePolicy
name|tmp
init|=
operator|new
name|TieredMergePolicy
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|setMaxMergedSegmentMB
argument_list|(
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.5
argument_list|,
name|tmp
operator|.
name|getMaxMergedSegmentMB
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxMergedSegmentMB
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|tmp
operator|.
name|getMaxMergedSegmentMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxMergedSegmentMB
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|tmp
operator|.
name|getMaxMergedSegmentMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
try|try
block|{
name|tmp
operator|.
name|setMaxMergedSegmentMB
argument_list|(
operator|-
literal|2.0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// pass
block|}
name|tmp
operator|.
name|setFloorSegmentMB
argument_list|(
literal|2.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|tmp
operator|.
name|getFloorSegmentMB
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setFloorSegmentMB
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|tmp
operator|.
name|getFloorSegmentMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setFloorSegmentMB
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|tmp
operator|.
name|getFloorSegmentMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
try|try
block|{
name|tmp
operator|.
name|setFloorSegmentMB
argument_list|(
operator|-
literal|2.0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// pass
block|}
name|tmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
literal|2.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|tmp
operator|.
name|getMaxCFSSegmentSizeMB
argument_list|()
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|tmp
operator|.
name|getMaxCFSSegmentSizeMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|1024
operator|/
literal|1024.
argument_list|,
name|tmp
operator|.
name|getMaxCFSSegmentSizeMB
argument_list|()
argument_list|,
name|EPSILON
operator|*
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
try|try
block|{
name|tmp
operator|.
name|setMaxCFSSegmentSizeMB
argument_list|(
operator|-
literal|2.0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// pass
block|}
comment|// TODO: Add more checks for other non-double setters!
block|}
block|}
end_class

end_unit

