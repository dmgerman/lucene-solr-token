begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
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
name|core
operator|.
name|Config
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|SolrException
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
name|request
operator|.
name|SolrQueryResponse
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
name|request
operator|.
name|XMLResponseWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NoInitialContextException
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
name|HttpServlet
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * @author yonik  */
end_comment

begin_class
DECL|class|SolrServlet
specifier|public
class|class
name|SolrServlet
extends|extends
name|HttpServlet
block|{
DECL|field|log
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrServlet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|core
name|SolrCore
name|core
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|private
specifier|static
name|String
name|CONTENT_TYPE
init|=
literal|"text/xml;charset=UTF-8"
decl_stmt|;
DECL|field|xmlResponseWriter
name|XMLResponseWriter
name|xmlResponseWriter
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrServlet.init()"
argument_list|)
expr_stmt|;
try|try
block|{
name|Context
name|c
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
comment|/***       System.out.println("Enumerating JNDI Context=" + c);       NamingEnumeration<NameClassPair> en = c.list("java:comp/env");       while (en.hasMore()) {         NameClassPair ncp = en.next();         System.out.println("  ENTRY:" + ncp);       }       System.out.println("JNDI lookup=" + c.lookup("java:comp/env/solr/home"));       ***/
name|String
name|home
init|=
operator|(
name|String
operator|)
name|c
operator|.
name|lookup
argument_list|(
literal|"java:comp/env/solr/home"
argument_list|)
decl_stmt|;
if|if
condition|(
name|home
operator|!=
literal|null
condition|)
name|Config
operator|.
name|setInstanceDir
argument_list|(
name|home
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoInitialContextException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"JNDI not configured for Solr (NoInitialContextEx)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No /solr/home in JNDI"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"user.dir="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
expr_stmt|;
name|xmlResponseWriter
operator|=
operator|new
name|XMLResponseWriter
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SolrServlet.init() done"
argument_list|)
expr_stmt|;
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
DECL|method|doPost
specifier|public
name|void
name|doPost
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
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|doGet
specifier|public
name|void
name|doGet
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
name|SolrServletRequest
name|solrReq
init|=
literal|null
decl_stmt|;
name|SolrQueryResponse
name|solrRsp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|solrRsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|solrReq
operator|=
operator|new
name|SolrServletRequest
argument_list|(
name|core
argument_list|,
name|request
argument_list|)
expr_stmt|;
comment|// log.severe("REQUEST PARAMS:" + solrReq.getParamString());
name|core
operator|.
name|execute
argument_list|(
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrRsp
operator|.
name|getException
argument_list|()
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
comment|// if (solrReq.getStrParam("version","2").charAt(0) == '1')
name|xmlResponseWriter
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Exception
name|e
init|=
name|solrRsp
operator|.
name|getException
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
literal|500
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
name|rc
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
name|sendErr
argument_list|(
name|rc
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|logged
condition|)
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|sendErr
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|sendErr
argument_list|(
literal|500
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// This releases the IndexReader associated with the request
name|solrReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sendErr
specifier|final
name|void
name|sendErr
parameter_list|(
name|int
name|rc
parameter_list|,
name|String
name|msg
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
block|{
try|try
block|{
comment|// hmmm, what if this was already set to text/xml?
try|try
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
comment|// response.setCharacterEncoding("UTF-8");
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
try|try
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getParam
specifier|final
name|int
name|getParam
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|param
parameter_list|,
name|int
name|defval
parameter_list|)
block|{
specifier|final
name|String
name|pval
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
operator|(
name|pval
operator|==
literal|null
operator|)
condition|?
name|defval
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|pval
argument_list|)
return|;
block|}
DECL|method|paramExists
specifier|final
name|boolean
name|paramExists
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|param
parameter_list|)
block|{
return|return
name|request
operator|.
name|getParameter
argument_list|(
name|param
argument_list|)
operator|!=
literal|null
condition|?
literal|true
else|:
literal|false
return|;
block|}
block|}
end_class

end_unit

