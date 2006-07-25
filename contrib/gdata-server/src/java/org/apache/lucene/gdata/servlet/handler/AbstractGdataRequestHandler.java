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
name|server
operator|.
name|GDataRequest
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
name|Service
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
name|GDataRequest
operator|.
name|GDataRequestType
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

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|AbstractGdataRequestHandler
specifier|public
specifier|abstract
class|class
name|AbstractGdataRequestHandler
extends|extends
name|RequestAuthenticator
implements|implements
name|GDataRequestHandler
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AbstractGdataRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/*      * UTF-8 is the encoding used in the client API to send the entries to the server      */
DECL|field|ENCODING
specifier|private
specifier|final
specifier|static
name|String
name|ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|service
specifier|protected
name|Service
name|service
decl_stmt|;
DECL|field|feedRequest
specifier|protected
name|GDataRequest
name|feedRequest
decl_stmt|;
DECL|field|feedResponse
specifier|protected
name|GDataResponse
name|feedResponse
decl_stmt|;
comment|/**      * @see org.apache.lucene.gdata.servlet.handler.GDataRequestHandler#processRequest(javax.servlet.http.HttpServletRequest,      *      javax.servlet.http.HttpServletResponse)      */
DECL|method|processRequest
specifier|public
specifier|abstract
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
function_decl|;
DECL|method|initializeRequestHandler
specifier|protected
name|void
name|initializeRequestHandler
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|HttpServletResponse
name|response
parameter_list|,
specifier|final
name|GDataRequestType
name|type
parameter_list|)
throws|throws
name|GDataRequestException
throws|,
name|ServletException
block|{
name|this
operator|.
name|feedRequest
operator|=
operator|new
name|GDataRequest
argument_list|(
name|request
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|feedResponse
operator|=
operator|new
name|GDataResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|this
operator|.
name|feedResponse
operator|.
name|setEncoding
argument_list|(
name|ENCODING
argument_list|)
expr_stmt|;
name|getService
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|feedRequest
operator|.
name|initializeRequest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataRequestException
name|e
parameter_list|)
block|{
name|this
operator|.
name|feedResponse
operator|.
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't initialize FeedRequest - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|sendError
specifier|protected
name|void
name|sendError
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|feedResponse
operator|.
name|sendError
argument_list|()
expr_stmt|;
block|}
DECL|method|setFeedResponseFormat
specifier|protected
name|void
name|setFeedResponseFormat
parameter_list|()
block|{
name|this
operator|.
name|feedResponse
operator|.
name|setOutputFormat
argument_list|(
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setFeedResponseStatus
specifier|protected
name|void
name|setFeedResponseStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|feedResponse
operator|.
name|setResponseCode
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|setError
specifier|protected
name|void
name|setError
parameter_list|(
name|int
name|error
parameter_list|)
block|{
name|this
operator|.
name|feedResponse
operator|.
name|setError
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
DECL|method|getService
specifier|private
name|void
name|getService
parameter_list|()
throws|throws
name|ServletException
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
name|this
operator|.
name|service
operator|=
name|serviceFactory
operator|.
name|getService
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|service
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Service not available"
argument_list|)
throw|;
block|}
DECL|method|closeService
specifier|protected
name|void
name|closeService
parameter_list|()
block|{
name|this
operator|.
name|service
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

