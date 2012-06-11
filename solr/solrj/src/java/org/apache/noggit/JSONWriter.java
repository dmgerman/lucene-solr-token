begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|JSONWriter
specifier|public
class|class
name|JSONWriter
block|{
comment|/** Implement this interface on your class to support serialization */
DECL|interface|Writable
specifier|public
specifier|static
interface|interface
name|Writable
block|{
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|writer
parameter_list|)
function_decl|;
block|}
DECL|field|level
specifier|protected
name|int
name|level
decl_stmt|;
DECL|field|indent
specifier|protected
name|int
name|indent
decl_stmt|;
DECL|field|out
specifier|protected
specifier|final
name|CharArr
name|out
decl_stmt|;
comment|/**    * @param out the CharArr to write the output to.    * @param indentSize  The number of space characters to use as an indent (default 2). 0=newlines but no spaces, -1=no indent at all.    */
DECL|method|JSONWriter
specifier|public
name|JSONWriter
parameter_list|(
name|CharArr
name|out
parameter_list|,
name|int
name|indentSize
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|indent
operator|=
name|indentSize
expr_stmt|;
block|}
DECL|method|JSONWriter
specifier|public
name|JSONWriter
parameter_list|(
name|CharArr
name|out
parameter_list|)
block|{
name|this
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|setIndentSize
specifier|public
name|void
name|setIndentSize
parameter_list|(
name|int
name|indentSize
parameter_list|)
block|{
name|this
operator|.
name|indent
operator|=
name|indentSize
expr_stmt|;
block|}
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|()
block|{
if|if
condition|(
name|indent
operator|>=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|indent
operator|>
literal|0
condition|)
block|{
name|int
name|spaces
init|=
name|level
operator|*
name|indent
decl_stmt|;
name|out
operator|.
name|reserve
argument_list|(
name|spaces
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|spaces
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|unsafeWrite
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|writeNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|CharSequence
condition|)
block|{
name|writeString
argument_list|(
operator|(
name|CharSequence
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Number
condition|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Integer
operator|||
name|o
operator|instanceof
name|Long
condition|)
block|{
name|write
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Float
operator|||
name|o
operator|instanceof
name|Double
condition|)
block|{
name|write
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CharArr
name|arr
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|arr
operator|.
name|write
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writeNumber
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|write
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Collection
condition|)
block|{
name|write
argument_list|(
operator|(
name|Collection
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|write
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Boolean
condition|)
block|{
name|write
argument_list|(
operator|(
operator|(
name|Boolean
operator|)
name|o
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Writable
condition|)
block|{
operator|(
operator|(
name|Writable
operator|)
name|o
operator|)
operator|.
name|write
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|int
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|int
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|float
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|float
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|long
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|long
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|double
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|double
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|short
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|short
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|boolean
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|boolean
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|char
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|char
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|write
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleUnknownClass
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Override this method for custom handling of unknown classes.  Also see the Writable interface. */
DECL|method|handleUnknownClass
specifier|public
name|void
name|handleUnknownClass
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|writeString
argument_list|(
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Map
name|val
parameter_list|)
block|{
name|startObject
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|val
operator|.
name|size
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|val
operator|.
name|entrySet
argument_list|()
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sz
operator|>
literal|1
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writeNameSeparator
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Collection
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|val
operator|.
name|size
argument_list|()
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
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sz
operator|>
literal|1
condition|)
name|indent
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
comment|/** A byte[] may be either a single logical value, or a list of small integers.    * It's up to the implementation to decide.    */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|short
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|short
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|short
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|long
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|long
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|float
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|float
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|double
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|double
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|boolean
index|[]
name|val
parameter_list|)
block|{
name|startArray
argument_list|()
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|boolean
name|v
range|:
name|val
control|)
block|{
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
name|writeValueSeparator
argument_list|()
expr_stmt|;
block|}
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|short
name|number
parameter_list|)
block|{
name|write
argument_list|(
operator|(
name|int
operator|)
name|number
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|number
parameter_list|)
block|{
name|write
argument_list|(
operator|(
name|int
operator|)
name|number
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|()
block|{
name|JSONUtil
operator|.
name|writeNull
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|CharSequence
name|str
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeString
argument_list|(
name|str
argument_list|,
literal|0
argument_list|,
name|str
operator|.
name|length
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|CharArr
name|str
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeString
argument_list|(
name|str
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStringStart
specifier|public
name|void
name|writeStringStart
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStringChars
specifier|public
name|void
name|writeStringChars
parameter_list|(
name|CharArr
name|partialStr
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeStringPart
argument_list|(
name|partialStr
operator|.
name|getArray
argument_list|()
argument_list|,
name|partialStr
operator|.
name|getStart
argument_list|()
argument_list|,
name|partialStr
operator|.
name|getEnd
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStringEnd
specifier|public
name|void
name|writeStringEnd
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|long
name|number
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeNumber
argument_list|(
name|number
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|number
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeNumber
argument_list|(
name|number
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|double
name|number
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeNumber
argument_list|(
name|number
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|float
name|number
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeNumber
argument_list|(
name|number
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|boolean
name|bool
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeBoolean
argument_list|(
name|bool
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|char
index|[]
name|val
parameter_list|)
block|{
name|JSONUtil
operator|.
name|writeString
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNumber
specifier|public
name|void
name|writeNumber
parameter_list|(
name|CharArr
name|digits
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|digits
argument_list|)
expr_stmt|;
block|}
DECL|method|writePartialNumber
specifier|public
name|void
name|writePartialNumber
parameter_list|(
name|CharArr
name|digits
parameter_list|)
block|{
name|out
operator|.
name|write
argument_list|(
name|digits
argument_list|)
expr_stmt|;
block|}
DECL|method|startObject
specifier|public
name|void
name|startObject
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|level
operator|++
expr_stmt|;
block|}
DECL|method|endObject
specifier|public
name|void
name|endObject
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|level
operator|--
expr_stmt|;
block|}
DECL|method|startArray
specifier|public
name|void
name|startArray
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|level
operator|++
expr_stmt|;
block|}
DECL|method|endArray
specifier|public
name|void
name|endArray
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|level
operator|--
expr_stmt|;
block|}
DECL|method|writeValueSeparator
specifier|public
name|void
name|writeValueSeparator
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNameSeparator
specifier|public
name|void
name|writeNameSeparator
parameter_list|()
block|{
name|out
operator|.
name|write
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

