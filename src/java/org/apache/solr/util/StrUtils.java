begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|StrUtils
specifier|public
class|class
name|StrUtils
block|{
comment|/**    * Split a string based on a separator, but don't split if it's inside    * a string.  Assume '\' escapes the next char both inside and    * outside strings.    */
DECL|method|splitSmart
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitSmart
parameter_list|(
name|String
name|s
parameter_list|,
name|char
name|separator
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|start
init|=
literal|0
decl_stmt|,
name|end
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|inString
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
comment|// skip escaped chars
name|pos
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inString
operator|!=
literal|0
operator|&&
name|ch
operator|==
name|inString
condition|)
block|{
name|inString
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'\''
operator|||
name|ch
operator|==
literal|'"'
condition|)
block|{
name|inString
operator|=
name|ch
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
name|separator
operator|&&
name|inString
operator|==
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|pos
expr_stmt|;
block|}
block|}
if|if
condition|(
name|start
operator|<
name|end
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/***     if (SolrCore.log.isLoggable(Level.FINEST)) {       SolrCore.log.finest("splitCommand=" + lst);     }     ***/
return|return
name|lst
return|;
block|}
comment|/** Splits a backslash escaped string on the separator.    *<p>    * Current backslash escaping supported:    *<br> \n \t \r \b \f are escaped the same as a Java String    *<br> Other characters following a backslash are produced verbatim (\c => c)    *    * @param s  the string to split    * @param separator the separator to split on    * @param decode decode backslash escaping    */
DECL|method|splitSmart
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitSmart
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|separator
parameter_list|,
name|boolean
name|decode
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|end
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
name|separator
argument_list|,
name|pos
argument_list|)
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
name|pos
operator|+=
name|separator
operator|.
name|length
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
operator|!
name|decode
condition|)
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
break|break;
comment|// ERROR, or let it go?
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|decode
condition|)
block|{
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'n'
case|:
name|ch
operator|=
literal|'\n'
expr_stmt|;
case|case
literal|'t'
case|:
name|ch
operator|=
literal|'\t'
expr_stmt|;
case|case
literal|'r'
case|:
name|ch
operator|=
literal|'\r'
expr_stmt|;
case|case
literal|'b'
case|:
name|ch
operator|=
literal|'\b'
expr_stmt|;
case|case
literal|'f'
case|:
name|ch
operator|=
literal|'\f'
expr_stmt|;
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|lst
return|;
block|}
DECL|method|splitWS
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitWS
parameter_list|(
name|String
name|s
parameter_list|,
name|boolean
name|decode
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|end
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
operator|!
name|decode
condition|)
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
break|break;
comment|// ERROR, or let it go?
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|decode
condition|)
block|{
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'n'
case|:
name|ch
operator|=
literal|'\n'
expr_stmt|;
case|case
literal|'t'
case|:
name|ch
operator|=
literal|'\t'
expr_stmt|;
case|case
literal|'r'
case|:
name|ch
operator|=
literal|'\r'
expr_stmt|;
case|case
literal|'b'
case|:
name|ch
operator|=
literal|'\b'
expr_stmt|;
case|case
literal|'f'
case|:
name|ch
operator|=
literal|'\f'
expr_stmt|;
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|lst
return|;
block|}
DECL|method|toLower
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|toLower
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|strings
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|strings
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|str
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/** Return if a string starts with '1', 't', or 'T'    *  and return false otherwise.    */
DECL|method|parseBoolean
specifier|public
specifier|static
name|boolean
name|parseBoolean
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
else|:
literal|0
decl_stmt|;
return|return
operator|(
name|ch
operator|==
literal|'1'
operator|||
name|ch
operator|==
literal|'t'
operator|||
name|ch
operator|==
literal|'T'
operator|)
return|;
block|}
comment|/**    * URLEncodes a value, replacing only enough chars so that    * the URL may be unambiguously pasted back into a browser.    *<p>    * Characters with a numeric value less than 32 are encoded.    *&amp;,=,%,+,space are encoded.    *<p>    */
DECL|method|partialURLEncodeVal
specifier|public
specifier|static
name|void
name|partialURLEncodeVal
parameter_list|(
name|Appendable
name|dest
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|<
literal|32
condition|)
block|{
name|dest
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
comment|// Hmmm, if we used StringBuilder rather than Appendable, it
comment|// could add an integer more efficiently.
name|dest
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|' '
case|:
name|dest
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'&'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"%26"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'%'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"%25"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'='
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"%3D"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'+'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"%2B"
argument_list|)
expr_stmt|;
break|break;
default|default :
name|dest
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

