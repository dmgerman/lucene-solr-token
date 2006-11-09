begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.servlet.handler
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|Component
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
import|;
end_import

begin_comment
comment|/**  * Default implementation for RequestHandlerFactory Builds the  * {@link org.apache.lucene.gdata.servlet.handler.GDataRequestHandler}  * instances.  * This class should not be access directy. The class will be registered in the {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry}.  * Use {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry#lookup(Class, ComponentType)}  *   * @author Simon Willnauer  *   */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|componentType
operator|=
name|ComponentType
operator|.
name|REQUESTHANDLERFACTORY
argument_list|)
DECL|class|DefaultRequestHandlerFactory
specifier|public
class|class
name|DefaultRequestHandlerFactory
extends|extends
name|RequestHandlerFactory
block|{
comment|/**      * public constructor to enable loading via the registry      * @see org.apache.lucene.gdata.server.registry.Component      * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry      */
DECL|method|DefaultRequestHandlerFactory
specifier|public
name|DefaultRequestHandlerFactory
parameter_list|()
block|{
comment|//
block|}
comment|/** 	 * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getEntryUpdateHandler() 	 */
annotation|@
name|Override
DECL|method|getEntryUpdateHandler
specifier|public
name|GDataRequestHandler
name|getEntryUpdateHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultUpdateHandler
argument_list|()
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getEntryDeleteHandler() 	 */
annotation|@
name|Override
DECL|method|getEntryDeleteHandler
specifier|public
name|GDataRequestHandler
name|getEntryDeleteHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultDeleteHandler
argument_list|()
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getFeedQueryHandler() 	 */
annotation|@
name|Override
DECL|method|getFeedQueryHandler
specifier|public
name|GDataRequestHandler
name|getFeedQueryHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultGetHandler
argument_list|()
return|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getEntryInsertHandler() 	 */
annotation|@
name|Override
DECL|method|getEntryInsertHandler
specifier|public
name|GDataRequestHandler
name|getEntryInsertHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultInsertHandler
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getInsertAccountHandler()      */
annotation|@
name|Override
DECL|method|getInsertAccountHandler
specifier|public
name|GDataRequestHandler
name|getInsertAccountHandler
parameter_list|()
block|{
return|return
operator|new
name|InsertAccountStrategy
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getDeleteAccountHandler()      */
annotation|@
name|Override
DECL|method|getDeleteAccountHandler
specifier|public
name|GDataRequestHandler
name|getDeleteAccountHandler
parameter_list|()
block|{
return|return
operator|new
name|DeleteAccountStrategy
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getUpdateAccountHandler()      */
annotation|@
name|Override
DECL|method|getUpdateAccountHandler
specifier|public
name|GDataRequestHandler
name|getUpdateAccountHandler
parameter_list|()
block|{
return|return
operator|new
name|UpdataAccountStrategy
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getInsertFeedHandler()      */
annotation|@
name|Override
DECL|method|getInsertFeedHandler
specifier|public
name|GDataRequestHandler
name|getInsertFeedHandler
parameter_list|()
block|{
return|return
operator|new
name|InsertFeedHandler
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getUpdateFeedHandler()      */
annotation|@
name|Override
DECL|method|getUpdateFeedHandler
specifier|public
name|GDataRequestHandler
name|getUpdateFeedHandler
parameter_list|()
block|{
return|return
operator|new
name|UpdateFeedHandler
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getDeleteFeedHandler()      */
annotation|@
name|Override
DECL|method|getDeleteFeedHandler
specifier|public
name|GDataRequestHandler
name|getDeleteFeedHandler
parameter_list|()
block|{
return|return
operator|new
name|DeleteFeedHandler
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ServerComponent#initialize()      */
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{
comment|//
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ServerComponent#destroy()      */
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|//
block|}
block|}
end_class

end_unit

