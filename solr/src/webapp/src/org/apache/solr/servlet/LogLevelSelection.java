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
name|*
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
name|Level
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
name|LogManager
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
comment|/**  * Admin JDK Logger level report and selection servlet.  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|LogLevelSelection
specifier|public
specifier|final
class|class
name|LogLevelSelection
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{   }
comment|/**    * Processes an HTTP GET request and changes the logging level as    * specified.    */
annotation|@
name|Override
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
name|IOException
throws|,
name|ServletException
block|{
comment|// Output page
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/html"
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
name|out
operator|.
name|write
argument_list|(
literal|"<html><head>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<title>Solr Admin: JDK Log Level Selector</title>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<link rel=\"stylesheet\" type=\"text/css\" href=\"solr-admin.css\" />"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</head><body>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<a href=\".\"><img border=\"0\" align=\"right\" height=\"78\" width=\"142\" src=\"solr_small.png\" alt=\"Solr\"></a>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<h1>JDK Log Level Selector</h1>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<p>Below is the complete JDK Log hierarchy with "
operator|+
literal|"intermediate logger/categories synthesized.  "
operator|+
literal|"The effective logging level is shown to the "
operator|+
literal|"far right. If a logger has unset level, then "
operator|+
literal|"the effective level is that of the nearest ancestor "
operator|+
literal|"with a level setting.  Note that this only shows "
operator|+
literal|"JDK Log levels.</p>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<form method='POST'>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<input type='submit' name='submit' value='set' "
operator|+
literal|"class='button'>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<input type='submit' name='submit' value='cancel' "
operator|+
literal|"class='button'>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<br><br>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<table cellspacing='2' cellpadding='2'>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<tr bgcolor='#CCCCFF'>"
operator|+
literal|"<th align=left>Logger/Category name<br>"
operator|+
literal|"<th colspan=9>Level</th>"
operator|+
literal|"</tr><tr bgcolor='#CCCCFF'>"
operator|+
literal|"<td bgcolor='#AAAAAA'>"
operator|+
literal|"(Dark rows don't yet exist.)</td>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|LEVELS
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"<th align=left>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|LEVELS
index|[
name|j
index|]
operator|!=
literal|null
condition|)
name|out
operator|.
name|write
argument_list|(
name|LEVELS
index|[
name|j
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|out
operator|.
name|write
argument_list|(
literal|"unset"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</th>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"<th align=left>Effective</th>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</tr>\n"
argument_list|)
expr_stmt|;
name|Iterator
name|iWrappers
init|=
name|buildWrappers
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iWrappers
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LogWrapper
name|wrapper
init|=
operator|(
name|LogWrapper
operator|)
name|iWrappers
operator|.
name|next
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<tr"
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrapper
operator|.
name|logger
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|" bgcolor='#AAAAAA'"
argument_list|)
expr_stmt|;
block|}
comment|//out.write( ( wrapper.logger != null ) ? "#DDDDDD" : "#AAAAAA" );
name|out
operator|.
name|write
argument_list|(
literal|"><td>"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|wrapper
operator|.
name|name
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"root"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|wrapper
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"</td>\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|LEVELS
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"<td align=center>"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|wrapper
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"root"
argument_list|)
operator|||
operator|(
name|LEVELS
index|[
name|j
index|]
operator|!=
literal|null
operator|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"<input type='radio' name='"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|wrapper
operator|.
name|name
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"root"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|wrapper
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"' value='"
argument_list|)
expr_stmt|;
if|if
condition|(
name|LEVELS
index|[
name|j
index|]
operator|!=
literal|null
condition|)
name|out
operator|.
name|write
argument_list|(
name|LEVELS
index|[
name|j
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|out
operator|.
name|write
argument_list|(
literal|"unset"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
if|if
condition|(
name|LEVELS
index|[
name|j
index|]
operator|==
name|wrapper
operator|.
name|level
argument_list|()
condition|)
name|out
operator|.
name|write
argument_list|(
literal|" checked"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"</td>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"<td align=center>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrapper
operator|.
name|logger
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|getEffectiveLevel
argument_list|(
name|wrapper
operator|.
name|logger
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"</td></tr>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"</table>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<br>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<input type='submit' name='submit' value='set' "
operator|+
literal|"class='button'>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<input type='submit' name='submit' value='cancel' "
operator|+
literal|"class='button'>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</form>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</body></html>\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|IOException
throws|,
name|ServletException
block|{
if|if
condition|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"submit"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"set"
argument_list|)
condition|)
block|{
name|Map
name|paramMap
init|=
name|request
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
name|Iterator
name|iParams
init|=
name|paramMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iParams
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|p
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iParams
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
operator|(
name|String
operator|)
name|p
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
operator|(
operator|(
name|String
index|[]
operator|)
name|p
operator|.
name|getValue
argument_list|()
operator|)
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"submit"
argument_list|)
condition|)
continue|continue;
name|Logger
name|logger
decl_stmt|;
name|LogManager
name|logManager
init|=
name|LogManager
operator|.
name|getLogManager
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"root"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|logger
operator|=
name|logManager
operator|.
name|getLogger
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
else|else
name|logger
operator|=
name|logManager
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"unset"
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|logger
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|logger
operator|.
name|getLevel
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
name|logger
operator|.
name|setLevel
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Unset log level on '"
operator|+
name|name
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Level
name|level
init|=
name|Level
operator|.
name|parse
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
name|logger
operator|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|getLevel
argument_list|()
operator|!=
name|level
condition|)
block|{
name|logger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Set '"
operator|+
name|name
operator|+
literal|"' to "
operator|+
name|level
operator|+
literal|" level."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|fine
argument_list|(
literal|"Selection form cancelled"
argument_list|)
expr_stmt|;
block|}
comment|// Redirect back to standard get page.
name|response
operator|.
name|sendRedirect
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|buildWrappers
specifier|private
name|Collection
name|buildWrappers
parameter_list|()
block|{
comment|// Use tree to get sorted results
name|SortedSet
argument_list|<
name|LogWrapper
argument_list|>
name|roots
init|=
operator|new
name|TreeSet
argument_list|<
name|LogWrapper
argument_list|>
argument_list|()
decl_stmt|;
name|roots
operator|.
name|add
argument_list|(
name|LogWrapper
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|LogManager
name|logManager
init|=
name|LogManager
operator|.
name|getLogManager
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|loggerNames
init|=
name|logManager
operator|.
name|getLoggerNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|loggerNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|loggerNames
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|LogWrapper
name|wrapper
init|=
operator|new
name|LogWrapper
argument_list|(
name|logger
argument_list|)
decl_stmt|;
name|roots
operator|.
name|remove
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
comment|// Make sure add occurs
name|roots
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|dot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|dot
operator|<
literal|0
condition|)
break|break;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
expr_stmt|;
name|roots
operator|.
name|add
argument_list|(
operator|new
name|LogWrapper
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
comment|// if not already
block|}
block|}
return|return
name|roots
return|;
block|}
DECL|method|getEffectiveLevel
specifier|private
name|Level
name|getEffectiveLevel
parameter_list|(
name|Logger
name|logger
parameter_list|)
block|{
name|Level
name|level
init|=
name|logger
operator|.
name|getLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|level
operator|!=
literal|null
condition|)
block|{
return|return
name|level
return|;
block|}
for|for
control|(
name|Level
name|l
range|:
name|LEVELS
control|)
block|{
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
comment|// avoid NPE
continue|continue;
block|}
if|if
condition|(
name|logger
operator|.
name|isLoggable
argument_list|(
name|l
argument_list|)
condition|)
block|{
comment|// return first level loggable
return|return
name|l
return|;
block|}
block|}
return|return
name|Level
operator|.
name|OFF
return|;
block|}
DECL|class|LogWrapper
specifier|private
specifier|static
class|class
name|LogWrapper
implements|implements
name|Comparable
block|{
DECL|field|ROOT
specifier|public
specifier|static
name|LogWrapper
name|ROOT
init|=
operator|new
name|LogWrapper
argument_list|(
name|LogManager
operator|.
name|getLogManager
argument_list|()
operator|.
name|getLogger
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|LogWrapper
specifier|public
name|LogWrapper
parameter_list|(
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|logger
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
DECL|method|LogWrapper
specifier|public
name|LogWrapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|this
operator|==
name|ROOT
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|other
operator|==
name|ROOT
condition|)
return|return
literal|1
return|;
return|return
name|name
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|LogWrapper
operator|)
name|other
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|LogWrapper
name|other
init|=
operator|(
name|LogWrapper
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|name
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|name
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|name
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|level
specifier|public
name|Level
name|level
parameter_list|()
block|{
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
return|return
name|logger
operator|.
name|getLevel
argument_list|()
return|;
return|return
literal|null
return|;
block|}
DECL|field|logger
specifier|public
name|Logger
name|logger
init|=
literal|null
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
block|}
DECL|field|LEVELS
specifier|private
specifier|static
name|Level
index|[]
name|LEVELS
init|=
block|{
literal|null
block|,
comment|// aka unset
name|Level
operator|.
name|FINEST
block|,
name|Level
operator|.
name|FINE
block|,
name|Level
operator|.
name|CONFIG
block|,
name|Level
operator|.
name|INFO
block|,
name|Level
operator|.
name|WARNING
block|,
name|Level
operator|.
name|SEVERE
block|,
name|Level
operator|.
name|OFF
comment|// Level.ALL -- ignore.  It is useless.
block|}
decl_stmt|;
DECL|field|log
specifier|private
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

