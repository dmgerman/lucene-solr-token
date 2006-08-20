begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|data
operator|.
name|GDataAccount
operator|.
name|AccountRole
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
name|GDataRequestException
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
name|GDataResponse
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
name|ServiceException
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
name|GDataRequest
operator|.
name|GDataRequestType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_comment
comment|/**  * Default Handler implementation. This handler processes the incoming  * {@link org.apache.lucene.gdata.server.GDataRequest} and inserts the requested  * feed entry into the storage and the search component.  *<p>  * The handler sends following response to the client:  *</p>  *<ol>  *<li>if the entry was added - HTTP status code<i>200 OK</i></li>  *<li>if an error occurs - HTTP status code<i>500 INTERNAL SERVER ERROR</i></li>  *<li>if the resource could not found - HTTP status code<i>404 NOT FOUND</i></li>  *</ol>  *<p>The added entry will be send back to the client if the insert request was successful.</p>  *   * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|DefaultInsertHandler
specifier|public
class|class
name|DefaultInsertHandler
extends|extends
name|AbstractGdataRequestHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DefaultInsertHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @throws ServletException       * @see org.apache.lucene.gdata.servlet.handler.GDataRequestHandler#processRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
DECL|method|processRequest
specifier|public
name|void
name|processRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
try|try
block|{
name|initializeRequestHandler
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|GDataRequestType
operator|.
name|INSERT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataRequestException
name|e
parameter_list|)
block|{
name|sendError
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|authenticateAccount
argument_list|(
name|this
operator|.
name|feedRequest
argument_list|,
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|)
condition|)
block|{
name|setError
argument_list|(
name|GDataResponse
operator|.
name|UNAUTHORIZED
argument_list|)
expr_stmt|;
name|sendError
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
name|BaseEntry
name|entry
init|=
name|this
operator|.
name|service
operator|.
name|createEntry
argument_list|(
name|this
operator|.
name|feedRequest
argument_list|,
name|this
operator|.
name|feedResponse
argument_list|)
decl_stmt|;
name|setFeedResponseFormat
argument_list|()
expr_stmt|;
name|setFeedResponseStatus
argument_list|(
name|GDataResponse
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|this
operator|.
name|feedResponse
operator|.
name|sendResponse
argument_list|(
name|entry
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getConfigurator
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not process GetFeed request - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|setError
argument_list|(
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
name|sendError
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|closeService
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

