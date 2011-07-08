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
name|ScoreDoc
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
name|MockDirectoryWrapper
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

begin_class
DECL|class|TestIndexReaderOnDiskFull
specifier|public
class|class
name|TestIndexReaderOnDiskFull
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Make sure if reader tries to commit but hits disk    * full that reader remains consistent and usable.    */
DECL|method|testDiskFull
specifier|public
name|void
name|testDiskFull
parameter_list|()
throws|throws
name|IOException
block|{
name|Term
name|searchTerm
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
name|int
name|START_COUNT
init|=
literal|157
decl_stmt|;
name|int
name|END_COUNT
init|=
literal|144
decl_stmt|;
comment|// First build up a starting index:
name|MockDirectoryWrapper
name|startDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|startDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
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
literal|"TEST: create initial index"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|157
condition|;
name|i
operator|++
control|)
block|{
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
name|newField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
argument_list|,
name|Field
operator|.
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
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|i
operator|%
literal|10
condition|)
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|{
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|startDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
literal|null
decl_stmt|;
try|try
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
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
name|fail
argument_list|(
literal|"exception when init searching: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|long
name|diskUsage
init|=
name|startDir
operator|.
name|getRecomputedActualSizeInBytes
argument_list|()
decl_stmt|;
name|long
name|diskFree
init|=
name|diskUsage
operator|+
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|50
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|IOException
name|err
init|=
literal|null
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
name|boolean
name|gotExc
init|=
literal|false
decl_stmt|;
comment|// Iterate w/ ever increasing free disk space:
while|while
condition|(
operator|!
name|done
condition|)
block|{
name|MockDirectoryWrapper
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|,
operator|new
name|RAMDirectory
argument_list|(
name|startDir
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// If IndexReader hits disk full, it can write to
comment|// the same files again.
name|dir
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
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
comment|// For each disk size, first try to commit against
comment|// dir that will hit random IOExceptions& disk
comment|// full; after, give it infinite disk space& turn
comment|// off random IOExceptions& retry w/ same reader:
name|boolean
name|success
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|2
condition|;
name|x
operator|++
control|)
block|{
name|double
name|rate
init|=
literal|0.05
decl_stmt|;
name|double
name|diskRatio
init|=
operator|(
operator|(
name|double
operator|)
name|diskFree
operator|)
operator|/
name|diskUsage
decl_stmt|;
name|long
name|thisDiskFree
decl_stmt|;
name|String
name|testName
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|thisDiskFree
operator|=
name|diskFree
expr_stmt|;
if|if
condition|(
name|diskRatio
operator|>=
literal|2.0
condition|)
block|{
name|rate
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|diskRatio
operator|>=
literal|4.0
condition|)
block|{
name|rate
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|diskRatio
operator|>=
literal|6.0
condition|)
block|{
name|rate
operator|=
literal|0.0
expr_stmt|;
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
literal|"\ncycle: "
operator|+
name|diskFree
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
block|}
name|testName
operator|=
literal|"disk full during reader.close() @ "
operator|+
name|thisDiskFree
operator|+
literal|" bytes"
expr_stmt|;
block|}
else|else
block|{
name|thisDiskFree
operator|=
literal|0
expr_stmt|;
name|rate
operator|=
literal|0.0
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
literal|"\ncycle: same writer: unlimited disk space"
argument_list|)
expr_stmt|;
block|}
name|testName
operator|=
literal|"reader re-use after disk full"
expr_stmt|;
block|}
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
name|thisDiskFree
argument_list|)
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|rate
argument_list|)
expr_stmt|;
name|DefaultSimilarity
name|sim
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|int
name|docId
init|=
literal|12
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
literal|13
condition|;
name|i
operator|++
control|)
block|{
name|reader
operator|.
name|deleteDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
name|docId
argument_list|,
literal|"content"
argument_list|,
name|sim
operator|.
name|encodeNormValue
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|docId
operator|+=
literal|12
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|x
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"  hit IOException: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
name|err
operator|=
name|e
expr_stmt|;
name|gotExc
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|1
operator|==
name|x
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|" hit IOException after disk space was freed up"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Finally, verify index is not corrupt, and, if
comment|// we succeeded, we see all docs changed, and if
comment|// we failed, we see either all docs or no docs
comment|// changed (transactional semantics):
name|IndexReader
name|newReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|newReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
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
name|fail
argument_list|(
name|testName
operator|+
literal|":exception when creating IndexReader after disk full during close: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
comment|/*         int result = newReader.docFreq(searchTerm);         if (success) {           if (result != END_COUNT) {             fail(testName + ": method did not throw exception but docFreq('aaa') is " + result + " instead of expected " + END_COUNT);           }         } else {           // On hitting exception we still may have added           // all docs:           if (result != START_COUNT&& result != END_COUNT) {             err.printStackTrace();             fail(testName + ": method did throw exception but docFreq('aaa') is " + result + " instead of expected " + START_COUNT + " or " + END_COUNT);           }         }         */
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|newReader
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
literal|null
decl_stmt|;
try|try
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|searchTerm
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
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
name|fail
argument_list|(
name|testName
operator|+
literal|": exception when searching: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|int
name|result2
init|=
name|hits
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|result2
operator|!=
name|END_COUNT
condition|)
block|{
name|fail
argument_list|(
name|testName
operator|+
literal|": method did not throw exception but hits.length for search on term 'aaa' is "
operator|+
name|result2
operator|+
literal|" instead of expected "
operator|+
name|END_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// On hitting exception we still may have added
comment|// all docs:
if|if
condition|(
name|result2
operator|!=
name|START_COUNT
operator|&&
name|result2
operator|!=
name|END_COUNT
condition|)
block|{
name|err
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|testName
operator|+
literal|": method did throw exception but hits.length for search on term 'aaa' is "
operator|+
name|result2
operator|+
literal|" instead of expected "
operator|+
name|START_COUNT
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|newReader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|result2
operator|==
name|END_COUNT
condition|)
block|{
if|if
condition|(
operator|!
name|gotExc
condition|)
name|fail
argument_list|(
literal|"never hit disk full"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Try again with more bytes of free space:
name|diskFree
operator|+=
name|TEST_NIGHTLY
condition|?
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|5
argument_list|,
literal|20
argument_list|)
else|:
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|50
argument_list|,
literal|200
argument_list|)
expr_stmt|;
block|}
name|startDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

