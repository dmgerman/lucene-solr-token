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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|Cookie
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
name|authentication
operator|.
name|AuthenticationController
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
name|authentication
operator|.
name|AuthenticatorException
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
name|authentication
operator|.
name|GDataHttpAuthenticator
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
comment|/**  * The RequestAuthenticator provides access to the registered  * {@link org.apache.lucene.gdata.server.authentication.AuthenticationController}  * as a super class for all request handler requiereing authentication for  * access. This class implements the  * {@link org.apache.lucene.gdata.server.authentication.GDataHttpAuthenticator}  * to get the auth token from the given request and call the needed Components  * to authenticat the client.  *<p>  * For request handler handling common requests like entry insert or update the  * authentication will be based on the account name verified as the owner of the  * feed to alter. If the accountname in the token does not match the name of the  * account which belongs to the feed the given role will be used for  * autentication. Authentication using the  * {@link RequestAuthenticator#authenticateAccount(HttpServletRequest, AccountRole)}  * method, the account name will be ignored, authentication will be based on the  * given<tt>AccountRole</tt>  *</p>  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|RequestAuthenticator
specifier|public
class|class
name|RequestAuthenticator
implements|implements
name|GDataHttpAuthenticator
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
name|RequestAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @see org.apache.lucene.gdata.server.authentication.GDataHttpAuthenticator#authenticateAccount(org.apache.lucene.gdata.server.GDataRequest,      *      org.apache.lucene.gdata.data.GDataAccount.AccountRole)      */
DECL|method|authenticateAccount
specifier|public
name|boolean
name|authenticateAccount
parameter_list|(
name|GDataRequest
name|request
parameter_list|,
name|AccountRole
name|role
parameter_list|)
block|{
name|String
name|clientIp
init|=
name|request
operator|.
name|getRemoteAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authenticating Account for GDataRequest -- modifying entries -- Role: "
operator|+
name|role
operator|+
literal|"; ClientIp: "
operator|+
name|clientIp
argument_list|)
expr_stmt|;
name|AuthenticationController
name|controller
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|lookup
argument_list|(
name|AuthenticationController
operator|.
name|class
argument_list|,
name|ComponentType
operator|.
name|AUTHENTICATIONCONTROLLER
argument_list|)
decl_stmt|;
name|ServiceFactory
name|factory
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
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
name|AdminService
name|adminService
init|=
name|factory
operator|.
name|getAdminService
argument_list|()
decl_stmt|;
name|GDataAccount
name|account
decl_stmt|;
try|try
block|{
name|account
operator|=
name|adminService
operator|.
name|getFeedOwningAccount
argument_list|(
name|request
operator|.
name|getFeedId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|token
init|=
name|getTokenFromRequest
argument_list|(
name|request
operator|.
name|getHttpServletRequest
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got Token: "
operator|+
name|token
operator|+
literal|"; for requesting account: "
operator|+
name|account
argument_list|)
expr_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
operator|&&
name|token
operator|!=
literal|null
condition|)
return|return
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|clientIp
argument_list|,
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|,
name|account
operator|.
name|getName
argument_list|()
argument_list|)
return|;
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
literal|"can get GDataAccount for feedID -- "
operator|+
name|request
operator|.
name|getFeedId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AuthenticatorException
argument_list|(
literal|" Service exception occured"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|adminService
operator|!=
literal|null
condition|)
name|adminService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.authentication.GDataHttpAuthenticator#authenticateAccount(javax.servlet.http.HttpServletRequest,      *      org.apache.lucene.gdata.data.GDataAccount.AccountRole)      */
DECL|method|authenticateAccount
specifier|public
name|boolean
name|authenticateAccount
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|AccountRole
name|role
parameter_list|)
block|{
name|String
name|clientIp
init|=
name|request
operator|.
name|getRemoteAddr
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authenticating Account for GDataRequest -- modifying entries -- Role: "
operator|+
name|role
operator|+
literal|"; ClientIp: "
operator|+
name|clientIp
argument_list|)
expr_stmt|;
name|AuthenticationController
name|controller
init|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|lookup
argument_list|(
name|AuthenticationController
operator|.
name|class
argument_list|,
name|ComponentType
operator|.
name|AUTHENTICATIONCONTROLLER
argument_list|)
decl_stmt|;
name|String
name|token
init|=
name|getTokenFromRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got Token: "
operator|+
name|token
operator|+
literal|";"
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|controller
operator|.
name|authenticateToken
argument_list|(
name|token
argument_list|,
name|clientIp
argument_list|,
name|role
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getTokenFromRequest
specifier|protected
name|String
name|getTokenFromRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|token
init|=
name|request
operator|.
name|getHeader
argument_list|(
name|AuthenticationController
operator|.
name|AUTHORIZATION_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
operator|||
operator|!
name|token
operator|.
name|startsWith
argument_list|(
literal|"GoogleLogin"
argument_list|)
condition|)
block|{
name|Cookie
index|[]
name|cookies
init|=
name|request
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|cookies
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cookies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|cookies
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|AuthenticationController
operator|.
name|TOKEN_KEY
argument_list|)
condition|)
block|{
name|token
operator|=
name|cookies
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
name|token
operator|=
name|token
operator|.
name|substring
argument_list|(
name|token
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

