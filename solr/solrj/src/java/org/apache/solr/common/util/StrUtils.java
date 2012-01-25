begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StrUtils
specifier|public
class|class
name|StrUtils
block|{
DECL|field|HEX_DIGITS
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|HEX_DIGITS
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|}
decl_stmt|;
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
name|char
name|ch
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
name|prevChar
init|=
name|ch
decl_stmt|;
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
comment|// If char is directly preceeded by a number or letter
comment|// then don't treat it as the start of a string.
comment|// Examples: 50" TV, or can't
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|prevChar
argument_list|)
condition|)
block|{
name|inString
operator|=
name|ch
expr_stmt|;
block|}
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
comment|/***     if (SolrCore.log.isLoggable(Level.FINEST)) {       SolrCore.log.trace("splitCommand=" + lst);     }     ***/
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
break|break;
case|case
literal|'t'
case|:
name|ch
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|ch
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|ch
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|ch
operator|=
literal|'\f'
expr_stmt|;
break|break;
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
comment|/**    * Splits file names separated by comma character.    * File names can contain comma characters escaped by backslash '\'    *    * @param fileNames the string containing file names    * @return a list of file names with the escaping backslashed removed    */
DECL|method|splitFileNames
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitFileNames
parameter_list|(
name|String
name|fileNames
parameter_list|)
block|{
if|if
condition|(
name|fileNames
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
return|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|fileNames
operator|.
name|split
argument_list|(
literal|"(?<!\\\\),"
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|file
operator|.
name|replaceAll
argument_list|(
literal|"\\\\(?=,)"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** Creates a backslash escaped string, joining all the items. */
DECL|method|join
specifier|public
specifier|static
name|String
name|join
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|items
parameter_list|,
name|char
name|separator
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|items
operator|.
name|size
argument_list|()
operator|<<
literal|3
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|items
control|)
block|{
name|String
name|item
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
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
name|item
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
name|item
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\\'
operator|||
name|ch
operator|==
name|separator
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
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
break|break;
case|case
literal|'t'
case|:
name|ch
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|ch
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|ch
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|ch
operator|=
literal|'\f'
expr_stmt|;
break|break;
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
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
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
comment|/** how to transform a String into a boolean... more flexible than    * Boolean.parseBoolean() to enable easier integration with html forms.    */
DECL|method|parseBool
specifier|public
specifier|static
name|boolean
name|parseBool
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"true"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"on"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"false"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"off"
argument_list|)
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"invalid boolean value: "
operator|+
name|s
argument_list|)
throw|;
block|}
comment|/**    * {@link NullPointerException} and {@link SolrException} free version of {@link #parseBool(String)}    * @param s    * @param def    * @return parsed boolean value (or def, if s is null or invalid)    */
DECL|method|parseBool
specifier|public
specifier|static
name|boolean
name|parseBool
parameter_list|(
name|String
name|s
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"true"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"on"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"false"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"off"
argument_list|)
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|def
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
if|if
condition|(
name|ch
operator|<
literal|0x10
condition|)
name|dest
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
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

