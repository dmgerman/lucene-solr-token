begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.servlet
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
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|servlet
operator|.
name|handler
operator|.
name|GDataRequestHandler
import|;
end_import

begin_comment
comment|/**  * Provides a clean basic interface for GDATA Client API and requests to the  * GDATA Server. This Servlet dispatches the incoming requests to defined GDATA  * request handlers. Each of the handler processes the incoming request and  * responds according to the requested action.  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|RequestControllerServlet
specifier|public
class|class
name|RequestControllerServlet
extends|extends
name|AbstractGdataServlet
block|{
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Log
name|LOGGER
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RequestControllerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Version ID since this class implements      *       * @see java.io.Serializable      */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7540810742476175576L
decl_stmt|;
comment|/**      * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,      *      javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|GDataRequestHandler
name|hanlder
init|=
name|HANDLER_FACTORY
operator|.
name|getEntryDeleteHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOGGER
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Process DELETE request"
argument_list|)
expr_stmt|;
name|hanlder
operator|.
name|processRequest
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,      *      javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
DECL|method|doGet
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|GDataRequestHandler
name|hanlder
init|=
name|HANDLER_FACTORY
operator|.
name|getFeedQueryHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOGGER
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Process GET request"
argument_list|)
expr_stmt|;
name|hanlder
operator|.
name|processRequest
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,      *      javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
DECL|method|doPost
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|GDataRequestHandler
name|hanlder
init|=
name|HANDLER_FACTORY
operator|.
name|getEntryInsertHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOGGER
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Process POST request"
argument_list|)
expr_stmt|;
name|hanlder
operator|.
name|processRequest
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,      *      javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
DECL|method|doPut
specifier|protected
name|void
name|doPut
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|GDataRequestHandler
name|hanlder
init|=
name|HANDLER_FACTORY
operator|.
name|getEntryUpdateHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOGGER
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Process PUT request"
argument_list|)
expr_stmt|;
name|hanlder
operator|.
name|processRequest
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

