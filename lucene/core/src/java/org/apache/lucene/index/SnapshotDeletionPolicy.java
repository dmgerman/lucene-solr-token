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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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

begin_comment
comment|/**  * An {@link IndexDeletionPolicy} that wraps around any other  * {@link IndexDeletionPolicy} and adds the ability to hold and later release  * snapshots of an index. While a snapshot is held, the {@link IndexWriter} will  * not remove any files associated with it even if the index is otherwise being  * actively, arbitrarily changed. Because we wrap another arbitrary  * {@link IndexDeletionPolicy}, this gives you the freedom to continue using  * whatever {@link IndexDeletionPolicy} you would normally want to use with your  * index.  *   *<p>  * This class maintains all snapshots in-memory, and so the information is not  * persisted and not protected against system failures. If persistency is  * important, you can use {@link PersistentSnapshotDeletionPolicy} (or your own  * extension) and when creating a new instance of this deletion policy, pass the  * persistent snapshots information to  * {@link #SnapshotDeletionPolicy(IndexDeletionPolicy, Map)}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SnapshotDeletionPolicy
specifier|public
class|class
name|SnapshotDeletionPolicy
implements|implements
name|IndexDeletionPolicy
block|{
comment|/** Holds a Snapshot's information. */
DECL|class|SnapshotInfo
specifier|private
specifier|static
class|class
name|SnapshotInfo
block|{
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|segmentsFileName
name|String
name|segmentsFileName
decl_stmt|;
DECL|field|commit
name|IndexCommit
name|commit
decl_stmt|;
DECL|method|SnapshotInfo
specifier|public
name|SnapshotInfo
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|segmentsFileName
parameter_list|,
name|IndexCommit
name|commit
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|segmentsFileName
operator|=
name|segmentsFileName
expr_stmt|;
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
operator|+
literal|" : "
operator|+
name|segmentsFileName
return|;
block|}
block|}
DECL|class|SnapshotCommitPoint
specifier|protected
class|class
name|SnapshotCommitPoint
extends|extends
name|IndexCommit
block|{
DECL|field|cp
specifier|protected
name|IndexCommit
name|cp
decl_stmt|;
DECL|method|SnapshotCommitPoint
specifier|protected
name|SnapshotCommitPoint
parameter_list|(
name|IndexCommit
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SnapshotDeletionPolicy.SnapshotCommitPoint("
operator|+
name|cp
operator|+
literal|")"
return|;
block|}
comment|/**      * Returns true if this segment can be deleted. The default implementation      * returns false if this segment is currently held as snapshot.      */
DECL|method|shouldDelete
specifier|protected
name|boolean
name|shouldDelete
parameter_list|(
name|String
name|segmentsFileName
parameter_list|)
block|{
return|return
operator|!
name|segmentsFileToIDs
operator|.
name|containsKey
argument_list|(
name|segmentsFileName
argument_list|)
return|;
block|}
annotation|@
name|Override
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
comment|// currently snapshotted.
if|if
condition|(
name|shouldDelete
argument_list|(
name|getSegmentsFileName
argument_list|()
argument_list|)
condition|)
block|{
name|cp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
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
annotation|@
name|Override
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getGeneration
argument_list|()
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserData
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cp
operator|.
name|getUserData
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|cp
operator|.
name|isDeleted
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentCount
specifier|public
name|int
name|getSegmentCount
parameter_list|()
block|{
return|return
name|cp
operator|.
name|getSegmentCount
argument_list|()
return|;
block|}
block|}
comment|/** Snapshots info */
DECL|field|idToSnapshot
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SnapshotInfo
argument_list|>
name|idToSnapshot
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SnapshotInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|// multiple IDs could point to the same commit point (segments file name)
DECL|field|segmentsFileToIDs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|segmentsFileToIDs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|primary
specifier|private
name|IndexDeletionPolicy
name|primary
decl_stmt|;
DECL|field|lastCommit
specifier|protected
name|IndexCommit
name|lastCommit
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
comment|/**    * {@link SnapshotDeletionPolicy} wraps another {@link IndexDeletionPolicy} to    * enable flexible snapshotting.    *     * @param primary    *          the {@link IndexDeletionPolicy} that is used on non-snapshotted    *          commits. Snapshotted commits, are not deleted until explicitly    *          released via {@link #release(String)}    * @param snapshotsInfo    *          A mapping of snapshot ID to the segments filename that is being    *          snapshotted. The expected input would be the output of    *          {@link #getSnapshots()}. A null value signals that there are no    *          initial snapshots to maintain.    */
DECL|method|SnapshotDeletionPolicy
specifier|public
name|SnapshotDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|primary
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|snapshotsInfo
parameter_list|)
block|{
name|this
argument_list|(
name|primary
argument_list|)
expr_stmt|;
if|if
condition|(
name|snapshotsInfo
operator|!=
literal|null
condition|)
block|{
comment|// Add the ID->segmentIDs here - the actual IndexCommits will be
comment|// reconciled on the call to onInit()
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|snapshotsInfo
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|registerSnapshotInfo
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Checks if the given id is already used by another snapshot, and throws    * {@link IllegalStateException} if it is.    */
DECL|method|checkSnapshotted
specifier|protected
name|void
name|checkSnapshotted
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|isSnapshotted
argument_list|(
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Snapshot ID "
operator|+
name|id
operator|+
literal|" is already used - must be unique"
argument_list|)
throw|;
block|}
block|}
comment|/** Registers the given snapshot information. */
DECL|method|registerSnapshotInfo
specifier|protected
name|void
name|registerSnapshotInfo
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|segment
parameter_list|,
name|IndexCommit
name|commit
parameter_list|)
block|{
name|idToSnapshot
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|SnapshotInfo
argument_list|(
name|id
argument_list|,
name|segment
argument_list|,
name|commit
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|segmentsFileToIDs
operator|.
name|get
argument_list|(
name|segment
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|segmentsFileToIDs
operator|.
name|put
argument_list|(
name|segment
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|wrapCommits
specifier|protected
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|wrapCommits
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|wrappedCommits
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexCommit
argument_list|>
argument_list|(
name|commits
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexCommit
name|ic
range|:
name|commits
control|)
block|{
name|wrappedCommits
operator|.
name|add
argument_list|(
operator|new
name|SnapshotCommitPoint
argument_list|(
name|ic
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wrappedCommits
return|;
block|}
comment|/**    * Get a snapshotted IndexCommit by ID. The IndexCommit can then be used to    * open an IndexReader on a specific commit point, or rollback the index by    * opening an IndexWriter with the IndexCommit specified in its    * {@link IndexWriterConfig}.    *     * @param id    *          a unique identifier of the commit that was snapshotted.    * @throws IllegalStateException    *           if no snapshot exists by the specified ID.    * @return The {@link IndexCommit} for this particular snapshot.    */
DECL|method|getSnapshot
specifier|public
specifier|synchronized
name|IndexCommit
name|getSnapshot
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|SnapshotInfo
name|snapshotInfo
init|=
name|idToSnapshot
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|snapshotInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No snapshot exists by ID: "
operator|+
name|id
argument_list|)
throw|;
block|}
return|return
name|snapshotInfo
operator|.
name|commit
return|;
block|}
comment|/**    * Get all the snapshots in a map of snapshot IDs to the segments they    * 'cover.' This can be passed to    * {@link #SnapshotDeletionPolicy(IndexDeletionPolicy, Map)} in order to    * initialize snapshots at construction.    */
DECL|method|getSnapshots
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSnapshots
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|snapshots
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|SnapshotInfo
argument_list|>
name|e
range|:
name|idToSnapshot
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|snapshots
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|segmentsFileName
argument_list|)
expr_stmt|;
block|}
return|return
name|snapshots
return|;
block|}
comment|/**    * Returns true if the given ID is already used by a snapshot. You can call    * this method before {@link #snapshot(String)} if you are not sure whether    * the ID is already used or not.    */
DECL|method|isSnapshotted
specifier|public
name|boolean
name|isSnapshotted
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|idToSnapshot
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|onCommit
specifier|public
specifier|synchronized
name|void
name|onCommit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
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
DECL|method|onInit
specifier|public
specifier|synchronized
name|void
name|onInit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
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
comment|/*      * Assign snapshotted IndexCommits to their correct snapshot IDs as      * specified in the constructor.      */
for|for
control|(
name|IndexCommit
name|commit
range|:
name|commits
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|segmentsFileToIDs
operator|.
name|get
argument_list|(
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|idToSnapshot
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
block|}
block|}
block|}
comment|/*      * Second, see if there are any instances where a snapshot ID was specified      * in the constructor but an IndexCommit doesn't exist. In this case, the ID      * should be removed.      *       * Note: This code is protective for extreme cases where IDs point to      * non-existent segments. As the constructor should have received its      * information via a call to getSnapshots(), the data should be well-formed.      */
comment|// Find lost snapshots
name|ArrayList
argument_list|<
name|String
argument_list|>
name|idsToRemove
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|SnapshotInfo
argument_list|>
name|e
range|:
name|idToSnapshot
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|commit
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|idsToRemove
operator|==
literal|null
condition|)
block|{
name|idsToRemove
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|idsToRemove
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Finally, remove those 'lost' snapshots.
if|if
condition|(
name|idsToRemove
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|idsToRemove
control|)
block|{
name|SnapshotInfo
name|info
init|=
name|idToSnapshot
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|segmentsFileToIDs
operator|.
name|remove
argument_list|(
name|info
operator|.
name|segmentsFileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Release a snapshotted commit by ID.    *     * @param id    *          a unique identifier of the commit that is un-snapshotted.    * @throws IllegalStateException    *           if no snapshot exists by this ID.    */
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotInfo
name|info
init|=
name|idToSnapshot
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Snapshot doesn't exist: "
operator|+
name|id
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|segmentsFileToIDs
operator|.
name|get
argument_list|(
name|info
operator|.
name|segmentsFileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|!=
literal|null
condition|)
block|{
name|ids
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|segmentsFileToIDs
operator|.
name|remove
argument_list|(
name|info
operator|.
name|segmentsFileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Snapshots the last commit. Once a commit is 'snapshotted,' it is protected    * from deletion (as long as this {@link IndexDeletionPolicy} is used). The    * commit can be removed by calling {@link #release(String)} using the same ID    * parameter followed by a call to {@link IndexWriter#deleteUnusedFiles()}.    *<p>    *<b>NOTE:</b> ID must be unique in the system. If the same ID is used twice,    * an {@link IllegalStateException} is thrown.    *<p>    *<b>NOTE:</b> while the snapshot is held, the files it references will not    * be deleted, which will consume additional disk space in your index. If you    * take a snapshot at a particularly bad time (say just before you call    * forceMerge) then in the worst case this could consume an extra 1X of your    * total index size, until you release the snapshot.    *     * @param id    *          a unique identifier of the commit that is being snapshotted.    * @throws IllegalStateException    *           if either there is no 'last commit' to snapshot, or if the    *           parameter 'ID' refers to an already snapshotted commit.    * @return the {@link IndexCommit} that was snapshotted.    */
DECL|method|snapshot
specifier|public
specifier|synchronized
name|IndexCommit
name|snapshot
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastCommit
operator|==
literal|null
condition|)
block|{
comment|// no commit exists. Really shouldn't happen, but might be if SDP is
comment|// accessed before onInit or onCommit were called.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No index commit to snapshot"
argument_list|)
throw|;
block|}
comment|// Can't use the same snapshot ID twice...
name|checkSnapshotted
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|registerSnapshotInfo
argument_list|(
name|id
argument_list|,
name|lastCommit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|,
name|lastCommit
argument_list|)
expr_stmt|;
return|return
name|lastCommit
return|;
block|}
block|}
end_class

end_unit

