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
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|MockRAMDirectory
import|;
end_import

begin_comment
comment|/**  * Test class to illustrate using IndexDeletionPolicy to provide multi-level rollback capability.  * This test case creates an index of records 1 to 100, introducing a commit point every 10 records.  *   * A "keep all" deletion policy is used to ensure we keep all commit points for testing purposes  */
end_comment

begin_class
DECL|class|TestTransactionRollback
specifier|public
class|class
name|TestTransactionRollback
extends|extends
name|TestCase
block|{
DECL|field|FIELD_RECORD_ID
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_RECORD_ID
init|=
literal|"record_id"
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|//Rolls back index to a chosen ID
DECL|method|rollBackLast
specifier|private
name|void
name|rollBackLast
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|Exception
block|{
comment|// System.out.println("Attempting to rollback to "+id);
name|String
name|ids
init|=
literal|"-"
operator|+
name|id
decl_stmt|;
name|IndexCommit
name|last
init|=
literal|null
decl_stmt|;
name|Collection
name|commits
init|=
name|IndexReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|commits
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|IndexCommit
name|commit
init|=
operator|(
name|IndexCommit
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
name|ud
init|=
name|commit
operator|.
name|getUserData
argument_list|()
decl_stmt|;
if|if
condition|(
name|ud
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|ud
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|)
operator|.
name|endsWith
argument_list|(
name|ids
argument_list|)
condition|)
name|last
operator|=
name|commit
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Couldn't find commit point "
operator|+
name|id
argument_list|)
throw|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
operator|new
name|RollbackDeletionPolicy
argument_list|(
name|id
argument_list|)
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|,
name|last
argument_list|)
decl_stmt|;
name|Map
name|data
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"index"
argument_list|,
literal|"Rolled back to 1-"
operator|+
name|id
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRepeatedRollBacks
specifier|public
name|void
name|testRepeatedRollBacks
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|expectedLastRecordId
init|=
literal|100
decl_stmt|;
while|while
condition|(
name|expectedLastRecordId
operator|>
literal|10
condition|)
block|{
name|expectedLastRecordId
operator|-=
literal|10
expr_stmt|;
name|rollBackLast
argument_list|(
name|expectedLastRecordId
argument_list|)
expr_stmt|;
name|BitSet
name|expecteds
init|=
operator|new
name|BitSet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|expecteds
operator|.
name|set
argument_list|(
literal|1
argument_list|,
operator|(
name|expectedLastRecordId
operator|+
literal|1
operator|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkExpecteds
argument_list|(
name|expecteds
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkExpecteds
specifier|private
name|void
name|checkExpecteds
parameter_list|(
name|BitSet
name|expecteds
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|//Perhaps not the most efficient approach but meets our needs here.
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
if|if
condition|(
operator|!
name|r
operator|.
name|isDeleted
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|String
name|sval
init|=
name|r
operator|.
name|document
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
name|FIELD_RECORD_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|sval
operator|!=
literal|null
condition|)
block|{
name|int
name|val
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sval
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not expect document #"
operator|+
name|val
argument_list|,
name|expecteds
operator|.
name|get
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|.
name|set
argument_list|(
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have 0 docs remaining "
argument_list|,
literal|0
argument_list|,
name|expecteds
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*   private void showAvailableCommitPoints() throws Exception {     Collection commits = IndexReader.listCommits(dir);     for (Iterator iterator = commits.iterator(); iterator.hasNext();) {       IndexCommit comm = (IndexCommit) iterator.next();       System.out.print("\t Available commit point:["+comm.getUserData()+"] files=");       Collection files = comm.getFileNames();       for (Iterator iterator2 = files.iterator(); iterator2.hasNext();) {         String filename = (String) iterator2.next();         System.out.print(filename+", ");				       }       System.out.println();     }   }   */
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
operator|new
name|MockRAMDirectory
argument_list|()
expr_stmt|;
comment|//		dir=FSDirectory.getDirectory("/indexes/testDeletionPolicy");
comment|//		String[] files = dir.list();
comment|//		for (String string : files) {
comment|//			dir.deleteFile(string);
comment|//		}
comment|//Build index, of records 1 to 100, committing after each batch of 10
name|IndexDeletionPolicy
name|sdp
init|=
operator|new
name|KeepAllDeletionPolicy
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
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|sdp
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|currentRecordId
init|=
literal|1
init|;
name|currentRecordId
operator|<=
literal|100
condition|;
name|currentRecordId
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
operator|new
name|Field
argument_list|(
name|FIELD_RECORD_ID
argument_list|,
literal|""
operator|+
name|currentRecordId
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
name|currentRecordId
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|Map
name|data
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"index"
argument_list|,
literal|"records 1-"
operator|+
name|currentRecordId
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Rolls back to previous commit point
DECL|class|RollbackDeletionPolicy
class|class
name|RollbackDeletionPolicy
implements|implements
name|IndexDeletionPolicy
block|{
DECL|field|rollbackPoint
specifier|private
name|int
name|rollbackPoint
decl_stmt|;
DECL|method|RollbackDeletionPolicy
specifier|public
name|RollbackDeletionPolicy
parameter_list|(
name|int
name|rollbackPoint
parameter_list|)
block|{
name|this
operator|.
name|rollbackPoint
operator|=
name|rollbackPoint
expr_stmt|;
block|}
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{     }
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
name|iterator
init|=
name|commits
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|IndexCommit
name|commit
init|=
operator|(
name|IndexCommit
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
name|userData
init|=
name|commit
operator|.
name|getUserData
argument_list|()
decl_stmt|;
if|if
condition|(
name|userData
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Label for a commit point is "Records 1-30"
comment|// This code reads the last id ("30" in this example) and deletes it
comment|// if it is after the desired rollback point
name|String
name|x
init|=
operator|(
name|String
operator|)
name|userData
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|String
name|lastVal
init|=
name|x
operator|.
name|substring
argument_list|(
name|x
operator|.
name|lastIndexOf
argument_list|(
literal|"-"
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|last
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lastVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|>
name|rollbackPoint
condition|)
block|{
comment|/*             System.out.print("\tRolling back commit point:" +                              " UserData="+commit.getUserData() +")  ("+(commits.size()-1)+" commit points left) files=");             Collection files = commit.getFileNames();             for (Iterator iterator2 = files.iterator(); iterator2.hasNext();) {               System.out.print(" "+iterator2.next());				             }             System.out.println();             */
name|commit
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|class|DeleteLastCommitPolicy
class|class
name|DeleteLastCommitPolicy
implements|implements
name|IndexDeletionPolicy
block|{
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{}
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|IndexCommit
operator|)
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|)
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testRollbackDeletionPolicy
specifier|public
name|void
name|testRollbackDeletionPolicy
parameter_list|()
throws|throws
name|Exception
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
comment|// Unless you specify a prior commit point, rollback
comment|// should not work:
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
operator|new
name|DeleteLastCommitPolicy
argument_list|()
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
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
block|}
block|}
comment|// Keeps all commit points (used to build index)
DECL|class|KeepAllDeletionPolicy
class|class
name|KeepAllDeletionPolicy
implements|implements
name|IndexDeletionPolicy
block|{
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{}
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
block|}
end_class

end_unit

