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
name|List
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** A {@link IndexDeletionPolicy} that wraps around any other  *  {@link IndexDeletionPolicy} and adds the ability to hold and  *  later release a single "snapshot" of an index.  While  *  the snapshot is held, the {@link IndexWriter} will not  *  remove any files associated with it even if the index is  *  otherwise being actively, arbitrarily changed.  Because  *  we wrap another arbitrary {@link IndexDeletionPolicy}, this  *  gives you the freedom to continue using whatever {@link  *  IndexDeletionPolicy} you would normally want to use with your  *  index. */
end_comment

begin_class
DECL|class|SnapshotDeletionPolicy
specifier|public
class|class
name|SnapshotDeletionPolicy
implements|implements
name|IndexDeletionPolicy
block|{
DECL|field|lastCommit
specifier|private
name|IndexCommitPoint
name|lastCommit
decl_stmt|;
DECL|field|primary
specifier|private
name|IndexDeletionPolicy
name|primary
decl_stmt|;
DECL|field|snapshot
specifier|private
name|IndexCommitPoint
name|snapshot
decl_stmt|;
DECL|method|SnapshotDeletionPolicy
specifier|public
name|SnapshotDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|primary
parameter_list|)
block|{
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
block|}
DECL|method|onInit
specifier|public
specifier|synchronized
name|void
name|onInit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
name|primary
operator|.
name|onInit
argument_list|(
name|wrapCommits
argument_list|(
name|commits
argument_list|)
argument_list|)
expr_stmt|;
name|lastCommit
operator|=
operator|(
name|IndexCommitPoint
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
expr_stmt|;
block|}
DECL|method|onCommit
specifier|public
specifier|synchronized
name|void
name|onCommit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
name|primary
operator|.
name|onCommit
argument_list|(
name|wrapCommits
argument_list|(
name|commits
argument_list|)
argument_list|)
expr_stmt|;
name|lastCommit
operator|=
operator|(
name|IndexCommitPoint
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
expr_stmt|;
block|}
comment|/** Take a snapshot of the most recent commit to the    *  index.  You must call release() to free this snapshot.    *  Note that while the snapshot is held, the files it    *  references will not be deleted, which will consume    *  additional disk space in your index. If you take a    *  snapshot at a particularly bad time (say just before    *  you call optimize()) then in the worst case this could    *  consume an extra 1X of your total index size, until    *  you release the snapshot. */
DECL|method|snapshot
specifier|public
specifier|synchronized
name|IndexCommitPoint
name|snapshot
parameter_list|()
block|{
if|if
condition|(
name|snapshot
operator|==
literal|null
condition|)
name|snapshot
operator|=
name|lastCommit
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"snapshot is already set; please call release() first"
argument_list|)
throw|;
return|return
name|snapshot
return|;
block|}
comment|/** Release the currently held snapshot. */
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
name|snapshot
operator|=
literal|null
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"snapshot was not set; please call snapshot() first"
argument_list|)
throw|;
block|}
DECL|class|MyCommitPoint
specifier|private
class|class
name|MyCommitPoint
implements|implements
name|IndexCommitPoint
block|{
DECL|field|cp
name|IndexCommitPoint
name|cp
decl_stmt|;
DECL|method|MyCommitPoint
name|MyCommitPoint
parameter_list|(
name|IndexCommitPoint
name|cp
parameter_list|)
block|{
name|this
operator|.
name|cp
operator|=
name|cp
expr_stmt|;
block|}
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getSegmentsFileName
argument_list|()
return|;
block|}
DECL|method|getFileNames
specifier|public
name|Collection
name|getFileNames
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cp
operator|.
name|getFileNames
argument_list|()
return|;
block|}
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
synchronized|synchronized
init|(
name|SnapshotDeletionPolicy
operator|.
name|this
init|)
block|{
comment|// Suppress the delete request if this commit point is
comment|// our current snapshot.
if|if
condition|(
name|snapshot
operator|!=
name|cp
condition|)
name|cp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|wrapCommits
specifier|private
name|List
name|wrapCommits
parameter_list|(
name|List
name|commits
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|commits
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
name|myCommits
init|=
operator|new
name|ArrayList
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
name|myCommits
operator|.
name|add
argument_list|(
operator|new
name|MyCommitPoint
argument_list|(
operator|(
name|IndexCommitPoint
operator|)
name|commits
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|myCommits
return|;
block|}
block|}
end_class

end_unit

