begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Methods for manipulating arrays.  */
end_comment

begin_class
DECL|class|ArrayUtil
specifier|public
specifier|final
class|class
name|ArrayUtil
block|{
comment|/*      Begin Apache Harmony code       Revision taken on Friday, June 12. https://svn.apache.org/repos/asf/harmony/enhanced/classlib/archive/java6/modules/luni/src/main/java/java/lang/Integer.java     */
comment|/**    * Parses the string argument as if it was an int value and returns the    * result. Throws NumberFormatException if the string does not represent an    * int quantity.    *    * @param chars a string representation of an int quantity.    * @return int the value represented by the argument    * @throws NumberFormatException if the argument could not be parsed as an int quantity.    */
DECL|method|parseInt
specifier|public
specifier|static
name|int
name|parseInt
parameter_list|(
name|char
index|[]
name|chars
parameter_list|)
throws|throws
name|NumberFormatException
block|{
return|return
name|parseInt
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
literal|10
argument_list|)
return|;
block|}
comment|/**    * Parses a char array into an int.    * @param chars the character array    * @param offset The offset into the array    * @param len The length    * @return the int    * @throws NumberFormatException if it can't parse    */
DECL|method|parseInt
specifier|public
specifier|static
name|int
name|parseInt
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|NumberFormatException
block|{
return|return
name|parseInt
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
literal|10
argument_list|)
return|;
block|}
comment|/**    * Parses the string argument as if it was an int value and returns the    * result. Throws NumberFormatException if the string does not represent an    * int quantity. The second argument specifies the radix to use when parsing    * the value.    *    * @param chars a string representation of an int quantity.    * @param radix the base to use for conversion.    * @return int the value represented by the argument    * @throws NumberFormatException if the argument could not be parsed as an int quantity.    */
DECL|method|parseInt
specifier|public
specifier|static
name|int
name|parseInt
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|radix
parameter_list|)
throws|throws
name|NumberFormatException
block|{
if|if
condition|(
name|chars
operator|==
literal|null
operator|||
name|radix
argument_list|<
name|Character
operator|.
name|MIN_RADIX
operator|||
name|radix
argument_list|>
name|Character
operator|.
name|MAX_RADIX
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|()
throw|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"chars length is 0"
argument_list|)
throw|;
block|}
name|boolean
name|negative
init|=
name|chars
index|[
name|offset
operator|+
name|i
index|]
operator|==
literal|'-'
decl_stmt|;
if|if
condition|(
name|negative
operator|&&
operator|++
name|i
operator|==
name|len
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"can't convert to an int"
argument_list|)
throw|;
block|}
if|if
condition|(
name|negative
operator|==
literal|true
condition|)
block|{
name|offset
operator|++
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
return|return
name|parse
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|radix
argument_list|,
name|negative
argument_list|)
return|;
block|}
DECL|method|parse
specifier|private
specifier|static
name|int
name|parse
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|radix
parameter_list|,
name|boolean
name|negative
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|int
name|max
init|=
name|Integer
operator|.
name|MIN_VALUE
operator|/
name|radix
decl_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|digit
init|=
name|Character
operator|.
name|digit
argument_list|(
name|chars
index|[
name|i
operator|+
name|offset
index|]
argument_list|,
name|radix
argument_list|)
decl_stmt|;
if|if
condition|(
name|digit
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
if|if
condition|(
name|max
operator|>
name|result
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
name|int
name|next
init|=
name|result
operator|*
name|radix
operator|-
name|digit
decl_stmt|;
if|if
condition|(
name|next
operator|>
name|result
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
name|result
operator|=
name|next
expr_stmt|;
block|}
comment|/*while (offset< len) {      }*/
if|if
condition|(
operator|!
name|negative
condition|)
block|{
name|result
operator|=
operator|-
name|result
expr_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*   END APACHE HARMONY CODE   */
DECL|method|getNextSize
specifier|public
specifier|static
name|int
name|getNextSize
parameter_list|(
name|int
name|targetSize
parameter_list|)
block|{
comment|/* This over-allocates proportional to the list size, making room      * for additional growth.  The over-allocation is mild, but is      * enough to give linear-time amortized behavior over a long      * sequence of appends() in the presence of a poorly-performing      * system realloc().      * The growth pattern is:  0, 4, 8, 16, 25, 35, 46, 58, 72, 88, ...      */
return|return
operator|(
name|targetSize
operator|>>
literal|3
operator|)
operator|+
operator|(
name|targetSize
operator|<
literal|9
condition|?
literal|3
else|:
literal|6
operator|)
operator|+
name|targetSize
return|;
block|}
DECL|method|getShrinkSize
specifier|public
specifier|static
name|int
name|getShrinkSize
parameter_list|(
name|int
name|currentSize
parameter_list|,
name|int
name|targetSize
parameter_list|)
block|{
specifier|final
name|int
name|newSize
init|=
name|getNextSize
argument_list|(
name|targetSize
argument_list|)
decl_stmt|;
comment|// Only reallocate if we are "substantially" smaller.
comment|// This saves us from "running hot" (constantly making a
comment|// bit bigger then a bit smaller, over and over):
if|if
condition|(
name|newSize
operator|<
name|currentSize
operator|/
literal|2
condition|)
return|return
name|newSize
return|;
else|else
return|return
name|currentSize
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|int
index|[]
name|grow
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
name|int
index|[]
name|newArray
init|=
operator|new
name|int
index|[
name|getNextSize
argument_list|(
name|minSize
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|int
index|[]
name|grow
parameter_list|(
name|int
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|shrink
specifier|public
specifier|static
name|int
index|[]
name|shrink
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|targetSize
parameter_list|)
block|{
specifier|final
name|int
name|newSize
init|=
name|getShrinkSize
argument_list|(
name|array
operator|.
name|length
argument_list|,
name|targetSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSize
operator|!=
name|array
operator|.
name|length
condition|)
block|{
name|int
index|[]
name|newArray
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|long
index|[]
name|grow
parameter_list|(
name|long
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
name|long
index|[]
name|newArray
init|=
operator|new
name|long
index|[
name|getNextSize
argument_list|(
name|minSize
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|long
index|[]
name|grow
parameter_list|(
name|long
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|shrink
specifier|public
specifier|static
name|long
index|[]
name|shrink
parameter_list|(
name|long
index|[]
name|array
parameter_list|,
name|int
name|targetSize
parameter_list|)
block|{
specifier|final
name|int
name|newSize
init|=
name|getShrinkSize
argument_list|(
name|array
operator|.
name|length
argument_list|,
name|targetSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSize
operator|!=
name|array
operator|.
name|length
condition|)
block|{
name|long
index|[]
name|newArray
init|=
operator|new
name|long
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|byte
index|[]
name|grow
parameter_list|(
name|byte
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
name|byte
index|[]
name|newArray
init|=
operator|new
name|byte
index|[
name|getNextSize
argument_list|(
name|minSize
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|byte
index|[]
name|grow
parameter_list|(
name|byte
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|shrink
specifier|public
specifier|static
name|byte
index|[]
name|shrink
parameter_list|(
name|byte
index|[]
name|array
parameter_list|,
name|int
name|targetSize
parameter_list|)
block|{
specifier|final
name|int
name|newSize
init|=
name|getShrinkSize
argument_list|(
name|array
operator|.
name|length
argument_list|,
name|targetSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSize
operator|!=
name|array
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|newArray
init|=
operator|new
name|byte
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
else|else
return|return
name|array
return|;
block|}
comment|/**    * Returns hash of chars in range start (inclusive) to    * end (inclusive)    */
DECL|method|hashCode
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|char
index|[]
name|array
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|start
condition|;
name|i
operator|--
control|)
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|array
index|[
name|i
index|]
expr_stmt|;
return|return
name|code
return|;
block|}
comment|/**    * Returns hash of chars in range start (inclusive) to    * end (inclusive)    */
DECL|method|hashCode
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|byte
index|[]
name|array
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|start
condition|;
name|i
operator|--
control|)
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|array
index|[
name|i
index|]
expr_stmt|;
return|return
name|code
return|;
block|}
block|}
end_class

end_unit

