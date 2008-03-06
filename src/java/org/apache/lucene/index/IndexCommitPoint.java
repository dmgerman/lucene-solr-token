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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @deprecated Please subclass IndexCommit class instead  */
end_comment

begin_interface
DECL|interface|IndexCommitPoint
specifier|public
interface|interface
name|IndexCommitPoint
block|{
comment|/**    * Get the segments file (<code>segments_N</code>) associated     * with this commit point.    */
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
function_decl|;
comment|/**    * Returns all index files referenced by this commit point.    */
DECL|method|getFileNames
specifier|public
name|Collection
name|getFileNames
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete this commit point.    *<p>    * Upon calling this, the writer is notified that this commit     * point should be deleted.     *<p>    * Decision that a commit-point should be deleted is taken by the {@link IndexDeletionPolicy} in effect    * and therefore this should only be called by its {@link IndexDeletionPolicy#onInit onInit()} or     * {@link IndexDeletionPolicy#onCommit onCommit()} methods.   */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

