begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/** An index update command encapsulated in an object (Command pattern)  *  *  */
end_comment

begin_class
DECL|class|UpdateCommand
specifier|public
specifier|abstract
class|class
name|UpdateCommand
implements|implements
name|Cloneable
block|{
DECL|field|req
specifier|protected
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|version
specifier|protected
name|long
name|version
decl_stmt|;
DECL|field|route
specifier|protected
name|String
name|route
decl_stmt|;
DECL|field|flags
specifier|protected
name|int
name|flags
decl_stmt|;
DECL|field|BUFFERING
specifier|public
specifier|static
name|int
name|BUFFERING
init|=
literal|0x00000001
decl_stmt|;
comment|// update command is being buffered.
DECL|field|REPLAY
specifier|public
specifier|static
name|int
name|REPLAY
init|=
literal|0x00000002
decl_stmt|;
comment|// update command is from replaying a log.
DECL|field|PEER_SYNC
specifier|public
specifier|static
name|int
name|PEER_SYNC
init|=
literal|0x00000004
decl_stmt|;
comment|// update command is a missing update being provided by a peer.
DECL|field|IGNORE_AUTOCOMMIT
specifier|public
specifier|static
name|int
name|IGNORE_AUTOCOMMIT
init|=
literal|0x00000008
decl_stmt|;
comment|// this update should not count toward triggering of autocommits.
DECL|field|CLEAR_CACHES
specifier|public
specifier|static
name|int
name|CLEAR_CACHES
init|=
literal|0x00000010
decl_stmt|;
comment|// clear caches associated with the update log.  used when applying reordered DBQ updates when doing an add.
DECL|method|UpdateCommand
specifier|public
name|UpdateCommand
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
DECL|method|name
specifier|public
specifier|abstract
name|String
name|name
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|boolean
name|needComma
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|flags
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"flags="
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|flags
argument_list|)
argument_list|)
expr_stmt|;
name|needComma
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|needComma
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"_version_="
argument_list|)
operator|.
name|append
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|setVersion
specifier|public
name|void
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|getRoute
specifier|public
name|String
name|getRoute
parameter_list|()
block|{
return|return
name|route
return|;
block|}
DECL|method|setRoute
specifier|public
name|void
name|setRoute
parameter_list|(
name|String
name|route
parameter_list|)
block|{
name|this
operator|.
name|route
operator|=
name|route
expr_stmt|;
block|}
DECL|method|setFlags
specifier|public
name|void
name|setFlags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|getFlags
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
DECL|method|getReq
specifier|public
name|SolrQueryRequest
name|getReq
parameter_list|()
block|{
return|return
name|req
return|;
block|}
DECL|method|setReq
specifier|public
name|void
name|setReq
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|UpdateCommand
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|UpdateCommand
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

