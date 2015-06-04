begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
import|;
end_import

begin_comment
comment|/**   * This class makes a best-effort check that a provided {@link Lock}  * is valid before any destructive filesystem operation.  */
end_comment

begin_class
DECL|class|LockValidatingDirectoryWrapper
specifier|public
specifier|final
class|class
name|LockValidatingDirectoryWrapper
extends|extends
name|FilterDirectory
block|{
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
decl_stmt|;
DECL|method|LockValidatingDirectoryWrapper
specifier|public
name|LockValidatingDirectoryWrapper
parameter_list|(
name|Directory
name|in
parameter_list|,
name|Lock
name|writeLock
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
name|writeLock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|in
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|Directory
name|from
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|in
operator|.
name|copyFrom
argument_list|(
name|from
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|in
operator|.
name|renameFile
argument_list|(
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|in
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

