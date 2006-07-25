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
name|ServerBaseFeed
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
name|GDataEntityBuilder
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
name|ServiceFactory
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
name|administration
operator|.
name|AdminService
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
name|GDataServerRegistry
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
name|ProvidedService
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
name|util
operator|.
name|ParseException
import|;
end_import

begin_comment
comment|/**  *   * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|AbstractFeedHandler
specifier|public
specifier|abstract
class|class
name|AbstractFeedHandler
extends|extends
name|RequestAuthenticator
implements|implements
name|GDataRequestHandler
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
name|AbstractFeedHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PARAMETER_ACCOUNT
specifier|protected
specifier|static
specifier|final
name|String
name|PARAMETER_ACCOUNT
init|=
literal|"account"
decl_stmt|;
DECL|field|PARAMETER_SERVICE
specifier|protected
specifier|static
specifier|final
name|String
name|PARAMETER_SERVICE
init|=
literal|"service"
decl_stmt|;
DECL|field|error
specifier|private
name|int
name|error
decl_stmt|;
DECL|field|authenticated
specifier|protected
name|boolean
name|authenticated
init|=
literal|false
decl_stmt|;
DECL|field|errorMessage
specifier|private
name|String
name|errorMessage
init|=
literal|""
decl_stmt|;
DECL|field|isError
specifier|private
name|boolean
name|isError
init|=
literal|false
decl_stmt|;
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.GDataRequestHandler#processRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|ServletException
throws|,
name|IOException
block|{
name|this
operator|.
name|authenticated
operator|=
name|authenticateAccount
argument_list|(
name|request
argument_list|,
name|AccountRole
operator|.
name|FEEDAMINISTRATOR
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|authenticated
condition|)
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|,
literal|"Authorization failed"
argument_list|)
expr_stmt|;
block|}
DECL|method|createFeedFromRequest
specifier|protected
name|ServerBaseFeed
name|createFeedFromRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
throws|,
name|FeedHandlerException
block|{
name|GDataServerRegistry
name|registry
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
name|String
name|providedService
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|PARAMETER_SERVICE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|registry
operator|.
name|isServiceRegistered
argument_list|(
name|providedService
argument_list|)
condition|)
block|{
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
literal|"no such service"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FeedHandlerException
argument_list|(
literal|"ProvicdedService is not registered -- Name: "
operator|+
name|providedService
argument_list|)
throw|;
block|}
name|ProvidedService
name|provServiceInstance
init|=
name|registry
operator|.
name|getProvidedService
argument_list|(
name|providedService
argument_list|)
decl_stmt|;
if|if
condition|(
name|providedService
operator|==
literal|null
condition|)
block|{
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"no such service"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FeedHandlerException
argument_list|(
literal|"no such service registered -- "
operator|+
name|providedService
argument_list|)
throw|;
block|}
try|try
block|{
name|ServerBaseFeed
name|retVal
init|=
operator|new
name|ServerBaseFeed
argument_list|(
name|GDataEntityBuilder
operator|.
name|buildFeed
argument_list|(
name|request
operator|.
name|getReader
argument_list|()
argument_list|,
name|provServiceInstance
argument_list|)
argument_list|)
decl_stmt|;
name|retVal
operator|.
name|setServiceConfig
argument_list|(
name|provServiceInstance
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not read from input stream - "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"Can not read from input stream"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"feed can not be parsed - "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"incoming feed can not be parsed"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|createRequestedAccount
specifier|protected
name|GDataAccount
name|createRequestedAccount
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|FeedHandlerException
block|{
name|GDataServerRegistry
name|registry
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
name|ServiceFactory
name|serviceFactory
init|=
name|registry
operator|.
name|lookup
argument_list|(
name|ServiceFactory
operator|.
name|class
argument_list|,
name|ComponentType
operator|.
name|SERVICEFACTORY
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceFactory
operator|==
literal|null
condition|)
block|{
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"Required server component not available"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FeedHandlerException
argument_list|(
literal|"Required server component not available -- "
operator|+
name|ServiceFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|AdminService
name|service
init|=
name|serviceFactory
operator|.
name|getAdminService
argument_list|()
decl_stmt|;
name|String
name|account
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|PARAMETER_ACCOUNT
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|service
operator|.
name|getAccount
argument_list|(
name|account
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"no account for requested account - "
operator|+
name|account
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"no such account"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FeedHandlerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|sendResponse
specifier|protected
name|void
name|sendResponse
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|isError
condition|)
return|return;
try|try
block|{
name|response
operator|.
name|sendError
argument_list|(
name|this
operator|.
name|error
argument_list|,
name|this
operator|.
name|errorMessage
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"can send error in RequestHandler "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setError
specifier|protected
name|void
name|setError
parameter_list|(
name|int
name|error
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|isError
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getErrorCode
specifier|protected
name|int
name|getErrorCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|error
return|;
block|}
DECL|method|getErrorMessage
specifier|protected
name|String
name|getErrorMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|errorMessage
return|;
block|}
DECL|class|FeedHandlerException
specifier|static
class|class
name|FeedHandlerException
extends|extends
name|Exception
block|{
comment|/**          *           */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**          * Creates a new FeedHandlerException with a exception message and the exception cause this ex.          * @param arg0 - the message          * @param arg1 - the cause          */
DECL|method|FeedHandlerException
specifier|public
name|FeedHandlerException
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Throwable
name|arg1
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**          * Creates a new FeedHandlerException with a exception message.          * @param arg0 - message          */
DECL|method|FeedHandlerException
specifier|public
name|FeedHandlerException
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

