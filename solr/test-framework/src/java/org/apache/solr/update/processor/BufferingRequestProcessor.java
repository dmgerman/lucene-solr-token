begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|AddUpdateCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|CommitUpdateCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|DeleteUpdateCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|RollbackUpdateCommand
import|;
end_import

begin_class
DECL|class|BufferingRequestProcessor
specifier|public
class|class
name|BufferingRequestProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|addCommands
specifier|public
name|List
argument_list|<
name|AddUpdateCommand
argument_list|>
name|addCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|AddUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deleteCommands
specifier|public
name|List
argument_list|<
name|DeleteUpdateCommand
argument_list|>
name|deleteCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|DeleteUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|commitCommands
specifier|public
name|List
argument_list|<
name|CommitUpdateCommand
argument_list|>
name|commitCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rollbackCommands
specifier|public
name|List
argument_list|<
name|RollbackUpdateCommand
argument_list|>
name|rollbackCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|RollbackUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|BufferingRequestProcessor
specifier|public
name|BufferingRequestProcessor
parameter_list|(
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|addCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processDelete
specifier|public
name|void
name|processDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|commitCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processRollback
specifier|public
name|void
name|processRollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|rollbackCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing?
block|}
block|}
end_class

end_unit

