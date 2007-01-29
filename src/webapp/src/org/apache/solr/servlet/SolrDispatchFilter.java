begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|StringWriter
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
name|Filter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
name|SolrConfig
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
name|QueryResponseWriter
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
name|SolrQueryRequest
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
name|SolrRequestHandler
import|;
end_import

begin_comment
comment|/**  * This filter looks at the incoming URL maps them to handlers defined in solrconfig.xml  */
end_comment

begin_class
DECL|class|SolrDispatchFilter
specifier|public
class|class
name|SolrDispatchFilter
implements|implements
name|Filter
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
name|SolrDispatchFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|core
specifier|protected
name|SolrCore
name|core
decl_stmt|;
DECL|field|parsers
specifier|protected
name|SolrRequestParsers
name|parsers
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init()"
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
name|parsers
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
name|core
argument_list|,
name|SolrConfig
operator|.
name|config
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init() done"
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
block|}
DECL|method|doFilter
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
if|if
condition|(
name|request
operator|instanceof
name|HttpServletRequest
condition|)
block|{
name|HttpServletRequest
name|req
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
try|try
block|{
name|String
name|path
init|=
name|req
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getPathInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// this lets you handle /update/commit when /update is a servlet
name|path
operator|+=
name|req
operator|.
name|getPathInfo
argument_list|()
expr_stmt|;
block|}
name|int
name|idx
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|// save the portion after the ':' for a 'handler' path parameter
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|SolrQueryRequest
name|solrReq
init|=
name|parsers
operator|.
name|parse
argument_list|(
name|path
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|solrRsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
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
operator|!=
literal|null
condition|)
block|{
name|sendError
argument_list|(
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|,
name|solrRsp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Now write it out
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|solrReq
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|responseWriter
operator|.
name|getContentType
argument_list|(
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|sendError
argument_list|(
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// Otherwise let the webapp handle the request
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|sendError
specifier|protected
name|void
name|sendError
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|,
name|Throwable
name|ex
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|code
init|=
literal|500
decl_stmt|;
name|String
name|trace
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|ex
operator|instanceof
name|SolrException
condition|)
block|{
name|code
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|ex
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
comment|// For any regular code, don't include the stack trace
if|if
condition|(
name|code
operator|==
literal|500
operator|||
name|code
operator|<
literal|100
condition|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
name|trace
operator|=
literal|"\n\n"
operator|+
name|sw
operator|.
name|toString
argument_list|()
expr_stmt|;
name|SolrException
operator|.
name|logOnce
argument_list|(
name|log
argument_list|,
literal|null
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|// non standard codes have undefined results with various servers
if|if
condition|(
name|code
operator|<
literal|100
condition|)
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"invalid return code: "
operator|+
name|code
argument_list|)
expr_stmt|;
name|code
operator|=
literal|500
expr_stmt|;
block|}
block|}
name|res
operator|.
name|sendError
argument_list|(
name|code
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
operator|+
name|trace
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

