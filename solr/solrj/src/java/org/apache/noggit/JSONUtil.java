begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.noggit
package|package
name|org
operator|.
name|apache
operator|.
name|noggit
package|;
end_package

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|JSONUtil
specifier|public
class|class
name|JSONUtil
block|{
DECL|field|TRUE_CHARS
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|TRUE_CHARS
init|=
operator|new
name|char
index|[]
block|{
literal|'t'
block|,
literal|'r'
block|,
literal|'u'
block|,
literal|'e'
block|}
decl_stmt|;
DECL|field|FALSE_CHARS
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|FALSE_CHARS
init|=
operator|new
name|char
index|[]
block|{
literal|'f'
block|,
literal|'a'
block|,
literal|'l'
block|,
literal|'s'
block|,
literal|'e'
block|}
decl_stmt|;
DECL|field|NULL_CHARS
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|NULL_CHARS
init|=
operator|new
name|char
index|[]
block|{
literal|'n'
block|,
literal|'u'
block|,
literal|'l'
block|,
literal|'l'
block|}
decl_stmt|;
DECL|field|HEX_CHARS
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|HEX_CHARS
init|=
operator|new
name|char
index|[]
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
DECL|field|VALUE_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|VALUE_SEPARATOR
init|=
literal|','
decl_stmt|;
DECL|field|NAME_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|NAME_SEPARATOR
init|=
literal|':'
decl_stmt|;
DECL|field|OBJECT_START
specifier|public
specifier|static
specifier|final
name|char
name|OBJECT_START
init|=
literal|'{'
decl_stmt|;
DECL|field|OBJECT_END
specifier|public
specifier|static
specifier|final
name|char
name|OBJECT_END
init|=
literal|'}'
decl_stmt|;
DECL|field|ARRAY_START
specifier|public
specifier|static
specifier|final
name|char
name|ARRAY_START
init|=
literal|'['
decl_stmt|;
DECL|field|ARRAY_END
specifier|public
specifier|static
specifier|final
name|char
name|ARRAY_END
init|=
literal|']'
decl_stmt|;
DECL|method|toJSON
specifier|public
specifier|static
name|String
name|toJSON
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|)
operator|.
name|write
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @param o  The object to convert to JSON    * @param indentSize  The number of space characters to use as an indent (default 2). 0=newlines but no spaces, -1=no indent at all.    * @return Given Object converted to its JSON representation using the given indentSize    */
DECL|method|toJSON
specifier|public
specifier|static
name|String
name|toJSON
parameter_list|(
name|Object
name|o
parameter_list|,
name|int
name|indentSize
parameter_list|)
block|{
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
name|indentSize
argument_list|)
operator|.
name|write
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|writeNumber
specifier|public
specifier|static
name|void
name|writeNumber
parameter_list|(
name|int
name|number
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNumber
specifier|public
specifier|static
name|void
name|writeNumber
parameter_list|(
name|long
name|number
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNumber
specifier|public
specifier|static
name|void
name|writeNumber
parameter_list|(
name|float
name|number
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNumber
specifier|public
specifier|static
name|void
name|writeNumber
parameter_list|(
name|double
name|number
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString
specifier|public
specifier|static
name|void
name|writeString
parameter_list|(
name|CharArr
name|val
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|writeString
argument_list|(
name|val
operator|.
name|getArray
argument_list|()
argument_list|,
name|val
operator|.
name|getStart
argument_list|()
argument_list|,
name|val
operator|.
name|getEnd
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString
specifier|public
specifier|static
name|void
name|writeString
parameter_list|(
name|char
index|[]
name|val
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|writeStringPart
argument_list|(
name|val
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString
specifier|public
specifier|static
name|void
name|writeString
parameter_list|(
name|CharSequence
name|val
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|writeStringPart
argument_list|(
name|val
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStringPart
specifier|public
specifier|static
name|void
name|writeStringPart
parameter_list|(
name|char
index|[]
name|val
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
index|[
name|i
index|]
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'"'
case|:
case|case
literal|'\\'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'r'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\n'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'n'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\t'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'t'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\b'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'b'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\f'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'f'
argument_list|)
expr_stmt|;
break|break;
comment|// case '/':
default|default:
if|if
condition|(
name|ch
operator|<=
literal|0x1F
condition|)
block|{
name|unicodeEscape
argument_list|(
name|ch
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// These characters are valid JSON, but not valid JavaScript
if|if
condition|(
name|ch
operator|==
literal|'\u2028'
operator|||
name|ch
operator|==
literal|'\u2029'
condition|)
block|{
name|unicodeEscape
argument_list|(
name|ch
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|writeStringPart
specifier|public
specifier|static
name|void
name|writeStringPart
parameter_list|(
name|CharSequence
name|chars
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|chars
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'"'
case|:
case|case
literal|'\\'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'r'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\n'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'n'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\t'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'t'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\b'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'b'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\f'
case|:
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'f'
argument_list|)
expr_stmt|;
break|break;
comment|// case '/':
default|default:
if|if
condition|(
name|ch
operator|<=
literal|0x1F
condition|)
block|{
name|unicodeEscape
argument_list|(
name|ch
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// These characters are valid JSON, but not valid JavaScript
if|if
condition|(
name|ch
operator|==
literal|'\u2028'
operator|||
name|ch
operator|==
literal|'\u2029'
condition|)
block|{
name|unicodeEscape
argument_list|(
name|ch
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|unicodeEscape
specifier|public
specifier|static
name|void
name|unicodeEscape
parameter_list|(
name|int
name|ch
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'u'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|HEX_CHARS
index|[
name|ch
operator|>>>
literal|12
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|HEX_CHARS
index|[
operator|(
name|ch
operator|>>>
literal|8
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|HEX_CHARS
index|[
operator|(
name|ch
operator|>>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|HEX_CHARS
index|[
name|ch
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNull
specifier|public
specifier|static
name|void
name|writeNull
parameter_list|(
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|NULL_CHARS
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBoolean
specifier|public
specifier|static
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|val
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|val
condition|?
name|TRUE_CHARS
else|:
name|FALSE_CHARS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

