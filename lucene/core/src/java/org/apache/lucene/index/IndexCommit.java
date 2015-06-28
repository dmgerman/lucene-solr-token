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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. */
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
name|Map
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
comment|/**  *<p>Expert: represents a single commit into an index as seen by the  * {@link IndexDeletionPolicy} or {@link IndexReader}.</p>  *  *<p> Changes to the content of an index are made visible  * only after the writer who made that change commits by  * writing a new segments file  * (<code>segments_N</code>). This point in time, when the  * action of writing of a new segments file to the directory  * is completed, is an index commit.</p>  *  *<p>Each index commit point has a unique segments file  * associated with it. The segments file associated with a  * later index commit point would have a larger N.</p>  *  * @lucene.experimental */
end_comment

begin_comment
comment|// TODO: this is now a poor name, because this class also represents a
end_comment

begin_comment
comment|// point-in-time view from an NRT reader
end_comment

begin_class
DECL|class|IndexCommit
specifier|public
specifier|abstract
class|class
name|IndexCommit
implements|implements
name|Comparable
argument_list|<
name|IndexCommit
argument_list|>
block|{
comment|/**    * Get the segments file (<code>segments_N</code>) associated     * with this commit point.    */
DECL|method|getSegmentsFileName
specifier|public
specifier|abstract
name|String
name|getSegmentsFileName
parameter_list|()
function_decl|;
comment|/**    * Returns all index files referenced by this commit point.    */
DECL|method|getFileNames
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the {@link Directory} for the index.    */
DECL|method|getDirectory
specifier|public
specifier|abstract
name|Directory
name|getDirectory
parameter_list|()
function_decl|;
comment|/**    * Delete this commit point.  This only applies when using    * the commit point in the context of IndexWriter's    * IndexDeletionPolicy.    *<p>    * Upon calling this, the writer is notified that this commit     * point should be deleted.     *<p>    * Decision that a commit-point should be deleted is taken by the {@link IndexDeletionPolicy} in effect    * and therefore this should only be called by its {@link IndexDeletionPolicy#onInit onInit()} or     * {@link IndexDeletionPolicy#onCommit onCommit()} methods.   */
DECL|method|delete
specifier|public
specifier|abstract
name|void
name|delete
parameter_list|()
function_decl|;
comment|/** Returns true if this commit should be deleted; this is    *  only used by {@link IndexWriter} after invoking the    *  {@link IndexDeletionPolicy}. */
DECL|method|isDeleted
specifier|public
specifier|abstract
name|boolean
name|isDeleted
parameter_list|()
function_decl|;
comment|/** Returns number of segments referenced by this commit. */
DECL|method|getSegmentCount
specifier|public
specifier|abstract
name|int
name|getSegmentCount
parameter_list|()
function_decl|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|IndexCommit
specifier|protected
name|IndexCommit
parameter_list|()
block|{   }
comment|/** Two IndexCommits are equal if both their Directory and versions are equal. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|IndexCommit
condition|)
block|{
name|IndexCommit
name|otherCommit
init|=
operator|(
name|IndexCommit
operator|)
name|other
decl_stmt|;
return|return
name|otherCommit
operator|.
name|getDirectory
argument_list|()
operator|==
name|getDirectory
argument_list|()
operator|&&
name|otherCommit
operator|.
name|getGeneration
argument_list|()
operator|==
name|getGeneration
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getDirectory
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|Long
operator|.
name|valueOf
argument_list|(
name|getGeneration
argument_list|()
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns the generation (the _N in segments_N) for this    *  IndexCommit */
DECL|method|getGeneration
specifier|public
specifier|abstract
name|long
name|getGeneration
parameter_list|()
function_decl|;
comment|/** Returns userData, previously passed to {@link    *  IndexWriter#setCommitData(Map)} for this commit.  Map is    *  {@code String -> String}. */
DECL|method|getUserData
specifier|public
specifier|abstract
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
function_decl|;
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
block|{
if|if
condition|(
name|getDirectory
argument_list|()
operator|!=
name|commit
operator|.
name|getDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot compare IndexCommits from different Directory instances"
argument_list|)
throw|;
block|}
name|long
name|gen
init|=
name|getGeneration
argument_list|()
decl_stmt|;
name|long
name|comgen
init|=
name|commit
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
return|return
name|Long
operator|.
name|compare
argument_list|(
name|gen
argument_list|,
name|comgen
argument_list|)
return|;
block|}
comment|/** Package-private API for IndexWriter to init from a commit-point pulled from an NRT or non-NRT reader. */
DECL|method|getReader
name|StandardDirectoryReader
name|getReader
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

